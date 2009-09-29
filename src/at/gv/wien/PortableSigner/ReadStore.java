/*
 * ReadStore.java
 *
 * Created on 12.09.2007, 18:17:58
 * This File is part of PortableSigner (http://portablesigner.sf.net/)
 *  and is under the European Public License V1.1 (http://www.osor.eu/eupl)
 * (c) Stadt Wien, Peter Pfläging <peter.pflaeging@wien.gv.at>
 */
package at.gv.wien.PortableSigner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.NoSuchElementException;
import java.util.Enumeration;
//import sun.security.x509.*;



/**
 *
 * @author pfp
 */
public class ReadStore {
    public static PSCertificate[] certs = new PSCertificate[100];

    public ReadStore() {
        KeyStore ks = null;
        
        try {
            try {
                if (Main.platform.equals("mac")) {
                    ks = KeyStore.getInstance("KeychainStore", "Apple");
                } else if (Main.platform.equals("windows")) {
                    ks = KeyStore.getInstance("Windows-MY");
                }
                ks.load(null, null);
            } catch (NoSuchProviderException e) {
                System.err.println("Beim Lesen des Keystores trat ein Fehler auf (Algorithmus)!");
            }
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Beim Lesen des Zertifikates trat ein Fehler auf (Algorithmus)!");
        } catch (CertificateException e) {
            System.err.println("Beim Lesen des Zertifikates trat ein Fehler auf (Zertifikatsfehler)");
        } catch (FileNotFoundException e) {
            System.err.println("Beim Lesen des Zertifikates trat ein Fehler auf (Datei nicht zugreifbar)");
        } catch (IOException e) {
            System.err.println("Beim Lesen des Zertifikates trat ein Fehler auf (EA Fehler)");
        } catch (KeyStoreException e) {
            System.err.println("Beim Lesen des Zertifikates trat ein Fehler auf (Datei nicht zugreifbar)");
        }
        
        String alias = "";
        try {
            int count = 0;
            Enumeration aliasEnum = ks.aliases();
            for (; aliasEnum.hasMoreElements();) {
                String key = (String)aliasEnum.nextElement();
                //if (!ks.isCertificateEntry(key)) {
                if (true) {
                    certs[count] = new PSCertificate(key, ks);
                    count++;
                }
            }
        } catch (NoSuchElementException e) {
            System.err.println("Beim Lesen des Zertifikates trat ein Fehler auf (Keine Schl?ssel)");
        } catch (KeyStoreException e) {
            System.err.println("Beim Lesen des Zertifikates trat ein Fehler auf (Datei nicht zugreifbar)");
        }
    }
}
