/*
 * DoSignPDF.java
 *
 * Created on 21. September 2006, 15:25
 */




package at.gv.wien.PortableSigner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.NoSuchElementException;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSignatureAppearance;
import com.lowagie.text.pdf.PdfStamper;

/**
 * 
 * @author pfp@adv.magwien.gv.at
 */

public class DoSignPDF {
	private static PrivateKey privateKey;

	private static Certificate[] certificateChain;
        
        private static GetPKCS12 pkcs12;

	/** Creates a new instance of DoSignPDF */
	public DoSignPDF(String pdfInputFileName, String pdfOutputFileName,
			String pkcs12FileName, String password) {
		try {
//            System.out.println("Eingabedatei: " + pdfInputFileName);
//            System.out.println("Ausgabedatei: " + pdfOutputFileName);
//            System.out.println("Signaturdatei: " + pkcs12FileName);
			java.security.Security
					.insertProviderAt(
							new org.bouncycastle.jce.provider.BouncyCastleProvider(),
							2);
                        
                        pkcs12 = new GetPKCS12(pkcs12FileName, password);

			PdfReader reader = null;
			try {
				reader = new PdfReader(pdfInputFileName);
			} catch (IOException e) {
				Main.setResult(
                                        java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("CouldNotBeOpened"), 
                                        true,
                                        e.getLocalizedMessage());
			}
			FileOutputStream fout = null;
			try {
				fout = new FileOutputStream(pdfOutputFileName);
			} catch (FileNotFoundException e) {
				Main.setResult(
                                        java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("CouldNotBeWritten"),
                                        true,
                                        e.getLocalizedMessage());
			}
			PdfStamper stp = null;
			try {
				stp = PdfStamper.createSignature(reader, fout, '\0');
				PdfSignatureAppearance sap = stp.getSignatureAppearance();
				sap.setCrypto(pkcs12.privateKey, pkcs12.certificateChain, null,
						PdfSignatureAppearance.WINCER_SIGNED);
				// sap.setReason("I'm the author");
				// sap.setLocation("Vienna");
				// sap.setVisibleSignature(new Rectangle(00, 00, 300, 50), 1,
				// null);
				sap.setCertified(true);
				stp.close();
				Main.setResult(
                                        java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("IsGeneratedAndSigned"),
                                        false,
                                        "");
			} catch (Exception e) {
				Main.setResult(
                                        java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("ErrorWhileSigningFile"),
                                        true,
                                        e.getLocalizedMessage());
			}
		} catch (KeyStoreException kse) {
			Main.setResult(java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("ErrorCreatingKeystore"),
					true, kse.getLocalizedMessage());
		}
	}

}
