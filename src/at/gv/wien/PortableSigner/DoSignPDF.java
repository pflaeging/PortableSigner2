/*
 * DoSignPDF.java
 *
 * Created on 21. September 2006, 15:25
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
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

	public static String result;

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
			readPrivateKeyFromPKCS12(pkcs12FileName, password);

			PdfReader reader = null;
			try {
				reader = new PdfReader(pdfInputFileName);
			} catch (IOException e) {
				setResult(java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("File") + pdfInputFileName
						+ java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("CouldNotBeOpened"), true);
			}
			FileOutputStream fout = null;
			try {
				fout = new FileOutputStream(pdfOutputFileName);
			} catch (FileNotFoundException e) {
				setResult(java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("File") + pdfOutputFileName
						+ java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("CouldNotBeWritten"), true);
			}
			PdfStamper stp = null;
			try {
				stp = PdfStamper.createSignature(reader, fout, '\0');
				PdfSignatureAppearance sap = stp.getSignatureAppearance();
				sap.setCrypto(privateKey, certificateChain, null,
						PdfSignatureAppearance.WINCER_SIGNED);
				// sap.setReason("I'm the author");
				// sap.setLocation("Vienna");
				// sap.setVisibleSignature(new Rectangle(00, 00, 300, 50), 1,
				// null);
				sap.setCertified(true);
				stp.close();
				setResult(java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("Document") + pdfOutputFileName
						+ java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("IsGeneratedAndSigned"), false);
			} catch (Exception e) {
				setResult(java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("ErrorWhileSigningFile"), true);
			}
		} catch (KeyStoreException kse) {
			setResult(java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("ErrorCreatingKeystore"),
					true);
		}
	}

	protected static void readPrivateKeyFromPKCS12(String pkcs12FileName,
			String pkcs12Password) throws KeyStoreException {
		KeyStore ks = null;
		try {
			ks = KeyStore.getInstance("pkcs12");
			ks.load(new FileInputStream(pkcs12FileName), pkcs12Password
					.toCharArray());
		} catch (NoSuchAlgorithmException e) {
			setResult(
					java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("ErrorReadingCertificateAlgorythm"),
					true);
		} catch (CertificateException e) {
			setResult(
					java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("ErrorReadingCertificate"),
					true);
		} catch (FileNotFoundException e) {
			setResult(
					java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("ErrorReadingCertificateNotAccessible"),
					true);
		} catch (IOException e) {
			setResult(
					java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("ErrorReadingCertificateIO"),
					true);
		}

		String alias = "";
		try {
			alias = (String) ks.aliases().nextElement();
			privateKey = (PrivateKey) ks.getKey(alias, pkcs12Password
					.toCharArray());
		} catch (NoSuchElementException e) {
			setResult(
					java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("ErrorReadingCertificateNoKey"),
					true);
		} catch (NoSuchAlgorithmException e) {
			setResult(
					java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("ErrorReadingCertificateAlgorythm"),
					true);
		} catch (UnrecoverableKeyException e) {
			setResult(
					java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("ErrorReadingCertificateAlgorythm"),
					true);
		}
		certificateChain = ks.getCertificateChain(alias);
	}

	private static void setResult(String resultText, Boolean errorState) {
		System.err.println(resultText);
		if (result == null) {
			result = resultText;
		}
	}
}
