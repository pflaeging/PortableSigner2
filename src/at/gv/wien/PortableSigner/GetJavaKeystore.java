/*
 * GetJavaKeystore.java
 *
 * Created on 15. November 2006, 22:57
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
import java.util.Enumeration;

/**
 *
 * @author pfp
 */
public class GetJavaKeystore {
    
    public String[] aliases = new String[64];
    private KeyStore ks = null;
    
    /** Creates a new instance of GetJavaKeystore */
    public GetJavaKeystore(String keystore, String password) {

        try {
            ks = KeyStore.getInstance("jks");
            java.io.FileInputStream fis =
                    new java.io.FileInputStream(keystore);
            ks.load(fis, password.toCharArray());
        } catch (NoSuchAlgorithmException e) {
            Main.setResult(
                    "Beim Lesen des Zertifikates trat ein Fehler auf (Algorithmus)!",
                    true,
                    e.getLocalizedMessage());
        } catch (CertificateException e) {
            Main.setResult(
                    "Beim Lesen des Zertifikates trat ein Fehler auf (Zertifikatsfehler)",
                    true,
                    e.getLocalizedMessage());
        } catch (FileNotFoundException e) {
            Main.setResult(
                    "Beim Lesen des Zertifikates trat ein Fehler auf (Datei nicht zugreifbar)",
                    true,
                    e.getLocalizedMessage());
        } catch (IOException e) {
            Main.setResult(
                    "Beim Lesen des Zertifikates trat ein Fehler auf (EA Fehler)",
                    true,
                    e.getLocalizedMessage());
        } catch (KeyStoreException e) {
            Main.setResult(
                    "Beim Lesen des Zertifikates trat ein Fehler auf (Datei nicht zugreifbar)",
                    true,
                    e.getLocalizedMessage());
        }
        
        String alias = "";
        try {
            int count = 0;
            for (Enumeration aliasKey = ks.aliases(); aliasKey.hasMoreElements();) {
                String key = aliasKey.nextElement().toString();
                String name = ks.getCertificate(key).toString();
                if (ks.isKeyEntry(key)) {
                    aliases[count] = key;
                    System.out.println("Key#" + count + " " + key);
                    count++;
                }
                
            }
        } catch (NoSuchElementException e) {
            Main.setResult(
                    "Beim Lesen des Zertifikates trat ein Fehler auf (Keine Schlüssel)",
                    true,
                    e.getLocalizedMessage());
        } catch (KeyStoreException e) {
            Main.setResult(
                    "Beim Lesen des Zertifikates trat ein Fehler auf (Datei nicht zugreifbar)",
                    true,
                    e.getLocalizedMessage());
        }
    }
    
}

