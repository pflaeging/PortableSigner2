/*
 * Version.java
 *
 * for Release 2.0
 * This File is part of PortableSigner (http://portablesigner.sf.net/)
 *  and is under the European Public License V1.1 (http://www.osor.eu/eupl)
 * (c) Peter Pfläging <peter@pflaeging.net>
 */

package net.pflaeging.PortableSigner;

/**
 * @author peter@pflaeging.net
 */
public class Version {
        private static String internaltag;
        public static String release;
        
        public static String print;
        public static String version;
	
	public Version() {
		String cleanDate, cleanAuthor, cleanRevision, cleanRelease, cleanInternalTag;
		cleanDate = "Date: " +
                        java.util.ResourceBundle.getBundle("net/pflaeging/PortableSigner/Version")
                        .getString("Date");
		cleanAuthor = "Committer: " +
                        java.util.ResourceBundle.getBundle("net/pflaeging/PortableSigner/Version")
                        .getString("Committer");
		cleanRevision = "GitVersion: " +
                        java.util.ResourceBundle.getBundle("net/pflaeging/PortableSigner/Version")
                        .getString("GitVersion");
                cleanRelease = java.util.ResourceBundle.getBundle("net/pflaeging/PortableSigner/Version")
                        .getString("Release");
                cleanInternalTag = java.util.ResourceBundle.getBundle("net/pflaeging/PortableSigner/Version")
                        .getString("InternalTag");
		print =  cleanRevision + "\n" + cleanAuthor + "\n" + cleanDate;
                if (cleanInternalTag.equals(""))
                {
                    internaltag = "";
                } else {
                    internaltag = " " + cleanInternalTag;
                }
                release = cleanRelease + internaltag;
                version = release + " " + cleanRevision;
	}
}
