/*
 * GetJavaKeystore.java
 *
 * Created on 15. November 2006, 22:57
 * This File is part of PortableSigner (http://portablesigner.sf.net/)
 *  and is under the European Public License V1.1 (http://www.osor.eu/eupl)
 * (c) Peter Pfläging <peter@pflaeging.net>
 * 
 */

package net.pflaeging.PortableSigner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
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
                    "Beim Lesen des Zertifikates trat ein Fehler auf (Keine Schluessel)",
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

