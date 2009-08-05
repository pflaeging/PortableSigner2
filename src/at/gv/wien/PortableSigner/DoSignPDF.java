/*
 * DoSignPDF.java
 *
 * Created on 21. September 2006, 15:25
 * Patches and bugfixes from: dzoe@users.sourceforge.net
 *
 */
package at.gv.wien.PortableSigner;

import java.security.PrivateKey;
import java.security.cert.Certificate;


/**
 *
 * @author peter.pflaeging@wien.gv.at
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

    	PDFSigner pdfSigner = new PDFSigner();

    	try{
        	pdfSigner.doSignPDF(pdfInputFileName, pdfOutputFileName, pkcs12FileName, password, signText, signLanguage, sigLogo, finalize, sigComment, signReason, signLocation, ownerPassword);

        	//if ok
            Main.setResult(
                    java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("IsGeneratedAndSigned"),
                    false,
                    "");
    	}
    	catch(PDFSignerException pdfex){
    		//if error thrown
            Main.setResult(pdfex.getResultText(), pdfex.getErrorState(), pdfex.getErrorString());
    	}
    }

}
