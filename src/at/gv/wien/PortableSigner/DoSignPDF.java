/*
 * DoSignPDF.java
 *
 * Created on 21. September 2006, 15:25
 * Patches and bugfixes from: dzoe@users.sourceforge.net
 * 
 */
package at.gv.wien.PortableSigner;

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfContentByte;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Date;
import java.util.ResourceBundle;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSignatureAppearance;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import java.util.HashMap;

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
            String sigLogo, Boolean finalize, String sigComment, String signReason, String signLocation,
            byte[] ownerPassword) {
        try {
            //System.out.println("-> DoSignPDF <-");
            //System.out.println("Eingabedatei: " + pdfInputFileName);
            //System.out.println("Ausgabedatei: " + pdfOutputFileName);
            //System.out.println("Signaturdatei: " + pkcs12FileName);
            //System.out.println("Signaturblock?: " + signText);
            //System.out.println("Sprache der Blocks: " + signLanguage);
            //System.out.println("Signaturlogo: " + sigLogo);
            Rectangle signatureBlock;

            java.security.Security.insertProviderAt(
                    new org.bouncycastle.jce.provider.BouncyCastleProvider(), 2);

            pkcs12 = new GetPKCS12(pkcs12FileName, password);

            PdfReader reader = null;
            try {
				if (ownerPassword == null)
					reader = new PdfReader(pdfInputFileName);
				else
					reader = new PdfReader(pdfInputFileName, ownerPassword);
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
                // stp = PdfStamper.createSignature(reader, fout, '\0');
                stp = PdfStamper.createSignature(reader, fout, '\0', null, true);
                HashMap pdfInfo = reader.getInfo();
                String pdfInfoProducer = pdfInfo.get("Producer").toString();
                pdfInfo.put("Producer", pdfInfoProducer + " (signed with PortableSigner " + Version.release + ")");
                stp.setMoreInfo(pdfInfo);
                if (signText) {
                    String greet, signator, datestr, ca, serial, special, note, urn, urnvalue;
                    int specialcount = 0;
                    ResourceBundle block = ResourceBundle.getBundle(
                            "at/gv/wien/PortableSigner/Signatureblock");
                    greet = block.getString(signLanguage + "-greeting");
                    signator = block.getString(signLanguage + "-signator");
                    datestr = block.getString(signLanguage + "-date");
                    ca = block.getString(signLanguage + "-issuer");
                    serial = block.getString(signLanguage + "-serial");
                    special = block.getString(signLanguage + "-special");
                    note = block.getString(signLanguage + "-note");
                    urn = block.getString(signLanguage + "-urn");
                    urnvalue = block.getString(signLanguage + "-urnvalue");
                    //sigcomment = block.getString(signLanguage + "-comment");
                    stp.insertPage(pages + 1, size);
                    if (!pkcs12.atEgovOID.equals("")) {
                        specialcount = 1;
                    }
                    PdfContentByte content = stp.getOverContent(pages + 1);
                    // float topy = size.top();
                    float topy = size.getTop();
                    //float rightx = size.right();
                    float rightx = size.getRight();
                    float[] cellsize = new float[2];
                    cellsize[0] = 100f;
                    cellsize[1] = rightx - 60 - cellsize[0] - cellsize[1] - 70;

                    // Pagetable = Greeting, signatureblock, comment
                    // sigpagetable = outer table
                    //      consist: greetingcell, signatureblock , commentcell
                    PdfPTable signatureBlockCompleteTable = new PdfPTable(2);
                    PdfPTable signatureTextTable = new PdfPTable(2);
                    PdfPCell signatureBlockHeadingCell =
                            new PdfPCell(new Paragraph(
                            new Chunk(greet,
                            new Font(Font.HELVETICA, 12))));
                    signatureBlockHeadingCell.setPaddingBottom(5);
                    signatureBlockHeadingCell.setColspan(2);
                    signatureBlockHeadingCell.setBorderWidth(0f);
                    signatureBlockCompleteTable.addCell(signatureBlockHeadingCell);

                    // inner table start
                    // Line 1
                    signatureTextTable.addCell(
                            new Paragraph(
                            new Chunk(signator, new Font(Font.HELVETICA, 10, Font.BOLD))));
                    signatureTextTable.addCell(
                            new Paragraph(
                            new Chunk(pkcs12.subject, new Font(Font.COURIER, 10))));
                    // Line 2
                    signatureTextTable.addCell(
                            new Paragraph(
                            new Chunk(datestr, new Font(Font.HELVETICA, 10, Font.BOLD))));
                    signatureTextTable.addCell(
                            new Paragraph(
                            new Chunk(datum.toString(), new Font(Font.COURIER, 10))));
                    // Line 3
                    signatureTextTable.addCell(
                            new Paragraph(
                            new Chunk(ca, new Font(Font.HELVETICA, 10, Font.BOLD))));
                    signatureTextTable.addCell(
                            new Paragraph(
                            new Chunk(pkcs12.issuer, new Font(Font.COURIER, 10))));
                    // Line 4
                    signatureTextTable.addCell(
                            new Paragraph(
                            new Chunk(serial, new Font(Font.HELVETICA, 10, Font.BOLD))));
                    signatureTextTable.addCell(
                            new Paragraph(
                            new Chunk(pkcs12.serial.toString(), new Font(Font.COURIER, 10))));
                    // Line 5
                    if (specialcount == 1) {
                        signatureTextTable.addCell(
                                new Paragraph(
                                new Chunk(special, new Font(Font.HELVETICA, 10, Font.BOLD))));
                        signatureTextTable.addCell(
                                new Paragraph(
                                new Chunk(pkcs12.atEgovOID, new Font(Font.COURIER, 10))));
                    }
                    signatureTextTable.addCell(
                            new Paragraph(
                            new Chunk(urn, new Font(Font.HELVETICA, 10, Font.BOLD))));
                    signatureTextTable.addCell(
                            new Paragraph(
                            new Chunk(urnvalue, new Font(Font.COURIER, 10))));
                    signatureTextTable.setTotalWidth(cellsize);
                    // inner table end

                    signatureBlockCompleteTable.setHorizontalAlignment(signatureBlockCompleteTable.ALIGN_CENTER);
                    Image logo;
//                     System.out.println("Logo:" + sigLogo + ":");
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
                    signatureBlockCompleteTable.addCell(logocell);
                    PdfPCell incell = new PdfPCell(signatureTextTable);
                    incell.setBorderWidth(0f);
                    signatureBlockCompleteTable.addCell(incell);
                    PdfPCell commentcell =
                            new PdfPCell(new Paragraph(
                            new Chunk(sigComment,
                            new Font(Font.HELVETICA, 10))));
                    PdfPCell notecell =
                            new PdfPCell(new Paragraph(
                            new Chunk(note,
                            new Font(Font.HELVETICA, 10, Font.BOLD))));
                    //commentcell.setPaddingTop(10);
                    //commentcell.setColspan(2);
                    // commentcell.setBorderWidth(0f);
                    if (!sigComment.equals("")) {
                        signatureBlockCompleteTable.addCell(notecell);
                        signatureBlockCompleteTable.addCell(commentcell);
                    }
                    float[] cells = {70, cellsize[0] + cellsize[1]};
                    signatureBlockCompleteTable.setTotalWidth(cells);
                    signatureBlockCompleteTable.writeSelectedRows(0, 4 + specialcount, 30, topy - 20, content);
                    signatureBlock = new Rectangle( 30 + signatureBlockCompleteTable.getTotalWidth() - 20,
                            topy - 20 - 20,
                            30 + signatureBlockCompleteTable.getTotalWidth(),
                            topy - 20);
//                    //////
//                    AcroFields af = reader.getAcroFields();
//                    ArrayList names = af.getSignatureNames();
//                    for (int k = 0; k < names.size(); ++k) {
//                        String name = (String) names.get(k);
//                        System.out.println("Signature name: " + name);
//                        System.out.println("\tSignature covers whole document: " + af.signatureCoversWholeDocument(name));
//                        System.out.println("\tDocument revision: " + af.getRevision(name) + " of " + af.getTotalRevisions());
//                        PdfPKCS7 pk = af.verifySignature(name);
//                        X509Certificate tempsigner = pk.getSigningCertificate();
//                        Calendar cal = pk.getSignDate();
//                        Certificate pkc[] = pk.getCertificates();
//                        java.util.ResourceBundle tempoid =
//                                java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/SpecialOID");
//                        String tmpEgovOID = "";
//
//                        for (Enumeration<String> o = tempoid.getKeys(); o.hasMoreElements();) {
//                            String element = o.nextElement();
//                            // System.out.println(element + ":" + oid.getString(element));
//                            if (tempsigner.getNonCriticalExtensionOIDs().contains(element)) {
//                                if (!tmpEgovOID.equals("")) {
//                                    tmpEgovOID += ", ";
//                                }
//                                tmpEgovOID += tempoid.getString(element) + " (OID=" + element + ")";
//                            }
//                        }
//                        //System.out.println("\tSigniert von: " + PdfPKCS7.getSubjectFields(pk.getSigningCertificate()));
//                        System.out.println("\tSigniert von: " + tempsigner.getSubjectX500Principal().toString());
//                        System.out.println("\tDatum: " + cal.getTime().toString());
//                        System.out.println("\tAusgestellt von: " + tempsigner.getIssuerX500Principal().toString());
//                        System.out.println("\tSeriennummer: " + tempsigner.getSerialNumber());
//                        if (!tmpEgovOID.equals("")) {
//                            System.out.println("\tVerwaltungseigenschaft: " + tmpEgovOID);
//                        }
//                        System.out.println("\n");
//                        System.out.println("\tDocument modified: " + !pk.verify());
////                Object fails[] = PdfPKCS7.verifyCertificates(pkc, kall, null, cal);
////                if (fails == null) {
////                    System.out.println("\tCertificates verified against the KeyStore");
////                } else {
////                    System.out.println("\tCertificate failed: " + fails[1]);
////                }
//                    }
//
//                //////
                } else {
                    signatureBlock = new Rectangle(0, 0, 0, 0); // fake definition
                }
                PdfSignatureAppearance sap = stp.getSignatureAppearance();
                sap.setCrypto(pkcs12.privateKey, pkcs12.certificateChain, null,
                        PdfSignatureAppearance.WINCER_SIGNED);
                sap.setReason(signReason);
                sap.setLocation(signLocation);
//                if (signText) {
//                    sap.setVisibleSignature(signatureBlock,
//                            pages + 1, "PortableSigner");
//                }
                if (finalize) {
                    sap.setCertificationLevel(sap.CERTIFIED_NO_CHANGES_ALLOWED);
                } else {
                    sap.setCertificationLevel(sap.NOT_CERTIFIED);
                }
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
