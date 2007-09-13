/*
 * Certificate.java
 *
 * Created on 13.09.2007, 12:38:39
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.gv.wien.PortableSigner;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 *
 * @author pfp
 */
public class PSCertificate {
    static public int count = 0;
    public String aliasname;
    public String name;
    public    Boolean isKey;
    public    X509Certificate issuer;
    public    X509Certificate certificate;
    public    String serial;
    public    String subject;
    public    Date from;
    public    Date to;
        
    public PSCertificate(String alias, KeyStore ks) {
        //String name;
        //Boolean isKey;
        //X509Certificate issuer;
        //X509Certificate certificate;
        //String serial;
        //String subject;
        //Date from;
        //Date to;

        try {
            aliasname = alias;
            certificate = (X509Certificate) ks.getCertificate(alias);
            name = certificate.toString();
            issuer = certificate;
            if (ks.getCertificateChain(alias) != null) {
                if (ks.getCertificateChain(alias).length >= 2) {
                    issuer = (X509Certificate) ks.getCertificateChain(alias)[1];
                }
            }

            isKey = ks.isKeyEntry(alias);
            serial = certificate.getSerialNumber().toString();
            subject = certificate.getSubjectX500Principal().toString();
            from = certificate.getNotBefore();
            to = certificate.getNotAfter();
            // System.out.println("Alias: " + alias + " | Name: " + subject + " | Issuer: " + issuer.getSubjectDN().toString());
        } catch (KeyStoreException e) {
            System.err.println("Beim Lesen des Zertifikates trat ein Fehler auf (Zertifikatsfehler)");
        }
        count++;
        
    }
}
