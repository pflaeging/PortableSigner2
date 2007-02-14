/*
 * DoSignPDF.java
 *
 * Created on 21. September 2006, 15:25
 */




package at.gv.wien.PortableSigner;

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.StampContent;
import java.awt.Graphics;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
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
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSignatureAppearance;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

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
			String pkcs12FileName, String password, Boolean signText, String signLanguage,
                        String sigLogo) {
		try {
                        //System.out.println("-> DoSignPDF <-");
                        //System.out.println("Eingabedatei: " + pdfInputFileName);
                        //System.out.println("Ausgabedatei: " + pdfOutputFileName);
                        //System.out.println("Signaturdatei: " + pkcs12FileName);
                        //System.out.println("Signaturblock?: " + signText);
                        //System.out.println("Sprache der Blocks: " + signLanguage);
                        //System.out.println("Signaturlogo: " + sigLogo);
                        
			java.security.Security.insertProviderAt(
                            new org.bouncycastle.jce.provider.BouncyCastleProvider(),2);
                        
                        pkcs12 = new GetPKCS12(pkcs12FileName, password);

			PdfReader reader = null;
			try {
				reader = new PdfReader(pdfInputFileName);
			} catch (IOException e) {
				Main.setResult(
                                        java.util.ResourceBundle.getBundle(
                                            "at/gv/wien/PortableSigner/i18n").getString(
                                                "CouldNotBeOpened"), 
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
                                Date datum = new Date(System.currentTimeMillis());
                                
                                int pages = reader.getNumberOfPages();
                             
                                Rectangle size = reader.getPageSize(pages);
                                stp = PdfStamper.createSignature(reader, fout, '\0');
                                if (signText) {
                                    String greet, signator, datestr, ca, serial, special;
                                    int specialcount = 0;
                                    ResourceBundle block = ResourceBundle.getBundle(
                                            "at/gv/wien/PortableSigner/Signatureblock");
                                    greet = block.getString(signLanguage + "-greeting");
                                    signator = block.getString(signLanguage + "-signator");
                                    datestr = block.getString(signLanguage + "-date");
                                    ca = block.getString(signLanguage + "-issuer");
                                    serial = block.getString(signLanguage + "-serial");
                                    special = block.getString(signLanguage + "-special");
                                    stp.insertPage(pages + 1, size);
                                    if(!pkcs12.atEgovOID.equals("")) {
                                        specialcount = 1;
                                    }
                                    PdfContentByte content =  stp.getOverContent(pages + 1);
                                    float topy = size.top();
                                    float rightx = size.right();
                                    float [] cellsize = new float[2];
                                    cellsize[0] = 85f;
                                    cellsize[1] = rightx - 60 - cellsize[0] - cellsize[1] - 70;
                                
                                    PdfPTable table = new PdfPTable(2);
                                    PdfPTable otable = new PdfPTable(2);
                                    PdfPCell  cell = 
                                        new PdfPCell(new Paragraph(
                                            new Chunk(greet, 
                                                new Font(Font.HELVETICA, 12))));
                                    cell.setPaddingBottom(5);
                                    cell.setColspan(2);
                                    cell.setBorderWidth(0f);
                                    table.addCell(cell);
                                
                                    // inner table start
                                    otable.addCell(
                                        new Paragraph(
                                            new Chunk(signator, new Font(Font.HELVETICA, 10))));
                                    otable.addCell(
                                        new Paragraph(
                                            new Chunk(pkcs12.subject, new Font(Font.COURIER, 10))));
                                    // L 1
                                    otable.addCell(
                                        new Paragraph(
                                            new Chunk(datestr, new Font(Font.HELVETICA, 10))));
                                    otable.addCell(
                                        new Paragraph(
                                            new Chunk(datum.toString(), new Font(Font.COURIER, 10))));
                                    // L 2
                                    otable.addCell(
                                        new Paragraph(
                                            new Chunk(ca, new Font(Font.HELVETICA, 10))));
                                    otable.addCell(
                                        new Paragraph(
                                            new Chunk(pkcs12.issuer, new Font(Font.COURIER, 10))));
                                    // L 3
                                    otable.addCell(
                                        new Paragraph(
                                            new Chunk(serial, new Font(Font.HELVETICA, 10))));
                                    otable.addCell(
                                        new Paragraph(
                                            new Chunk(pkcs12.serial.toString(), new Font(Font.COURIER, 10))));
                                    // L 4
                                    if (specialcount == 1) {
                                        otable.addCell(
                                            new Paragraph(
                                                new Chunk(special, new Font(Font.HELVETICA, 10))));
                                        otable.addCell(
                                            new Paragraph(
                                                new Chunk(pkcs12.atEgovOID, new Font(Font.COURIER, 10))));
                                    }
                                    otable.setTotalWidth(cellsize);
                                    // inner table end
                                
                                    table.setHorizontalAlignment(table.ALIGN_CENTER);
                                    Image logo;
                                    // System.out.println("Logo:" + sigLogo + ":");
                                    if (sigLogo.equals("") || sigLogo == null) {
                                        logo = Image.getInstance(getClass().getResource(
                                            "/at/gv/wien/PortableSigner/SignatureLogo.png"));
                                    } else {
                                        logo = Image.getInstance(sigLogo);
                                    }
                                         
                                    PdfPCell logocell = new PdfPCell();
                                    logocell.setVerticalAlignment(logocell.ALIGN_MIDDLE);
                                    logocell.setHorizontalAlignment(logocell.ALIGN_CENTER);
                                    logocell.setImage(logo);
                                    table.addCell(logocell);
                                    PdfPCell incell = new PdfPCell(otable);
                                    incell.setBorderWidth(0f);
                                    table.addCell(incell);
                                    float [] cells = {70, cellsize[0] + cellsize[1]};
                                    table.setTotalWidth(cells);
                                    table.writeSelectedRows(0, 4 + specialcount, 30, topy - 20, content );
                                }
                                
                                PdfSignatureAppearance sap = stp.getSignatureAppearance();
				sap.setCrypto(pkcs12.privateKey, pkcs12.certificateChain, null,
						PdfSignatureAppearance.WINCER_SIGNED);
                                sap.setReason("");
			        //sap.setLocation("Vienna");                                    
                                
//                                sap.setVisibleSignature(new Rectangle(100, topy - 300, rightx - 100, topy - 100),
//                                pages+1, "Signaturblock");
                              
//                                Rectangle sigRect = new Rectangle(100, topy - 300, rightx - 100, topy - 100);
//                                sigRect.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
//                                stp.getWriter().add(); 
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
