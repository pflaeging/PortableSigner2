/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.gv.wien.PortableSigner;

/**
 *
 * @author peter
 */
public class SignBlock {

}

/*
                    String greet, signator, datestr, ca, serial, special, sigcomment;
                    int specialcount = 0;
                    ResourceBundle block = ResourceBundle.getBundle(
                            "at/gv/wien/PortableSigner/Signatureblock");
                    greet = block.getString(signLanguage + "-greeting");
                    signator = block.getString(signLanguage + "-signator");
                    datestr = block.getString(signLanguage + "-date");
                    ca = block.getString(signLanguage + "-issuer");
                    serial = block.getString(signLanguage + "-serial");
                    special = block.getString(signLanguage + "-special");
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
                    cellsize[0] = 85f;
                    cellsize[1] = rightx - 60 - cellsize[0] - cellsize[1] - 70;

                    // Pagetable = Greeting, signatureblock, comment
                    // sigpagetable = outer table
                    //      consist: greetingcell, signatureblock , commentcell
                    PdfPTable sigpagetable = new PdfPTable(2);
                    PdfPTable sigblocktable = new PdfPTable(2);
                    PdfPCell greetingcell =
                            new PdfPCell(new Paragraph(
                            new Chunk(greet,
                            new Font(Font.HELVETICA, 12))));
                    greetingcell.setPaddingBottom(5);
                    greetingcell.setColspan(2);
                    greetingcell.setBorderWidth(0f);
                    sigpagetable.addCell(greetingcell);

                    // inner table start
                    // Line 1
                    sigblocktable.addCell(
                            new Paragraph(
                            new Chunk(signator, new Font(Font.HELVETICA, 10))));
                    sigblocktable.addCell(
                            new Paragraph(
                            new Chunk(pkcs12.subject, new Font(Font.COURIER, 10))));
                    // Line 2
                    sigblocktable.addCell(
                            new Paragraph(
                            new Chunk(datestr, new Font(Font.HELVETICA, 10))));
                    sigblocktable.addCell(
                            new Paragraph(
                            new Chunk(datum.toString(), new Font(Font.COURIER, 10))));
                    // Line 3
                    sigblocktable.addCell(
                            new Paragraph(
                            new Chunk(ca, new Font(Font.HELVETICA, 10))));
                    sigblocktable.addCell(
                            new Paragraph(
                            new Chunk(pkcs12.issuer, new Font(Font.COURIER, 10))));
                    // Line 4
                    sigblocktable.addCell(
                            new Paragraph(
                            new Chunk(serial, new Font(Font.HELVETICA, 10))));
                    sigblocktable.addCell(
                            new Paragraph(
                            new Chunk(pkcs12.serial.toString(), new Font(Font.COURIER, 10))));
                    // Line 5
                    if (specialcount == 1) {
                        sigblocktable.addCell(
                                new Paragraph(
                                new Chunk(special, new Font(Font.HELVETICA, 10))));
                        sigblocktable.addCell(
                                new Paragraph(
                                new Chunk(pkcs12.atEgovOID, new Font(Font.COURIER, 10))));
                    }
                    sigblocktable.setTotalWidth(cellsize);
                    // inner table end

                    sigpagetable.setHorizontalAlignment(sigpagetable.ALIGN_CENTER);
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
                    sigpagetable.addCell(logocell);
                    PdfPCell incell = new PdfPCell(sigblocktable);
                    incell.setBorderWidth(0f);
                    sigpagetable.addCell(incell);
                    PdfPCell commentcell =
                            new PdfPCell(new Paragraph(
                            new Chunk(sigComment,
                            new Font(Font.HELVETICA, 10))));
                    commentcell.setPaddingTop(10);
                    commentcell.setColspan(2);
                    commentcell.setBorderWidth(0f);
                    if (!sigComment.equals("")) {
                        sigpagetable.addCell(commentcell);
                    }
                    float[] cells = {70, cellsize[0] + cellsize[1]};
                    sigpagetable.setTotalWidth(cells);
                    sigpagetable.writeSelectedRows(0, 4 + specialcount, 30, topy - 20, content);
                    signatureBlock = new Rectangle( 30 + sigpagetable.getTotalWidth() - 20,
                            topy - 20 - 20,
                            30 + sigpagetable.getTotalWidth(),
                            topy - 20);

 */ 