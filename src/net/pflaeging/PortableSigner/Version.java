/*
 * Version.java
 *
 * for 2.0 (Release 2.0)
 * This File is part of PortableSigner (http://portablesigner.sf.net/)
 *  and is under the European Public License V1.1 (http://www.osor.eu/eupl)
 * (c) Peter Pfläging <peter@pflaeging.net>
 */

package net.pflaeging.PortableSigner;

/**
 * @author peter@pflaeging.net
 */
public class Version {
	private static String date = "$Date$";
	private static String author = "$Author$";
	private static String revision = "$Revision$";
        private static String internaltag = "Delta";
        public static String release = "2.0" + internaltag;
        
        public static String print;
        public static String version;
	
	public Version() {
		String cleanDate, cleanAuthor, cleanRevision;
		cleanDate = date.replace('$', ' ').trim();
		cleanAuthor = author.replace('$', ' ').trim();
		cleanRevision = revision.replace('$', ' ').trim();
		print =  cleanRevision + "\n" + cleanAuthor + "\n" + cleanDate;
                version = release + " " + cleanRevision;
	}
}
