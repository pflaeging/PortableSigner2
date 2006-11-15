/*
 * GetPKCS12.java
 *
 * Created on 15. November 2006, 11:18
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

/**
 *
 * @author pfp
 */
public class GetPKCS12 {
    
    public static PrivateKey privateKey;
    
    public static Certificate[] certificateChain;
    
    /** Creates a new instance of GetPKCS12 */    
    public GetPKCS12(String pkcs12FileName,
            String pkcs12Password) throws KeyStoreException {
        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance("pkcs12");
            ks.load(new FileInputStream(pkcs12FileName), pkcs12Password
                    .toCharArray());
        } catch (NoSuchAlgorithmException e) {
            Main.setResult(
                    java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("ErrorReadingCertificateAlgorythm"),
                    true);
        } catch (CertificateException e) {
            Main.setResult(
                    java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("ErrorReadingCertificate"),
                    true);
        } catch (FileNotFoundException e) {
            Main.setResult(
                    java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("ErrorReadingCertificateNotAccessible"),
                    true);
        } catch (IOException e) {
            Main.setResult(
                    java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("ErrorReadingCertificateIO"),
                    true);
        }
        
        String alias = "";
        try {
            alias = (String) ks.aliases().nextElement();
            privateKey = (PrivateKey) ks.getKey(alias, pkcs12Password
                    .toCharArray());
        } catch (NoSuchElementException e) {
            Main.setResult(
                    java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("ErrorReadingCertificateNoKey"),
                    true);
        } catch (NoSuchAlgorithmException e) {
            Main.setResult(
                    java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("ErrorReadingCertificateAlgorythm"),
                    true);
        } catch (UnrecoverableKeyException e) {
            Main.setResult(
                    java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("ErrorReadingCertificateAlgorythm"),
                    true);
        }
        certificateChain = ks.getCertificateChain(alias);
    }
    
    
}
