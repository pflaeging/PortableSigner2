/*
 * DoSignPDF.java
 *
 * Created on 21. September 2006, 15:25
 * This File is part of PortableSigner (http://portablesigner.sf.net/)
 *  and is under the European Public License V1.1 (http://www.osor.eu/eupl)
 * (c) Peter Pflaeging <peter@pflaeging.net>
 *
 * Patches and bugfixes from: dzoe@users.sourceforge.net
 *
 */
package net.pflaeging.PortableSigner;


/**
 *
 * @author peter@pflaeging.net
 */
public class DoSignPDF {

    /** Creates a new instance of DoSignPDF */
    public DoSignPDF(String pdfInputFileName,
            String pdfOutputFileName,
            String pkcs12FileName,
            String password,
            Boolean signText,
            String signLanguage,
            String sigLogo,
            Boolean finalize,
            String sigComment,
            String signReason,
            String signLocation,
            Boolean noExtraPage,
            float verticalPos,
            float leftMargin,
            float rightMargin,
            Boolean signLastPage,
            byte[] ownerPassword) {

    	PDFSigner pdfSigner = new PDFSigner();

    	try{
        	pdfSigner.doSignPDF(pdfInputFileName, pdfOutputFileName, pkcs12FileName,
                        password, signText, signLanguage, sigLogo, finalize, sigComment,
                        signReason, signLocation, noExtraPage, verticalPos, leftMargin,
                        rightMargin, signLastPage, ownerPassword);

        	//if ok
            Main.setResult(
                    java.util.ResourceBundle.getBundle("net/pflaeging/PortableSigner/i18n").getString("IsGeneratedAndSigned"),
                    false,
                    "");
    	}
    	catch(PDFSignerException pdfex){
    		//if error thrown
            Main.setResult(pdfex.getResultText(), pdfex.getErrorState(), pdfex.getErrorString());
    	}
    }

}
