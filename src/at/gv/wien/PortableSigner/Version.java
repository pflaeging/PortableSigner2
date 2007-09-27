/*
 * Version.java
 *
 * Created on 25. Oktober 2006, 14:56
 *
 */

package at.gv.wien.PortableSigner;

/**
 * @author peter.pflaeging@wien.gv.at
 */
public class Version {
	private static String date = "$Date$";
	private static String author = "$Author$";
	private static String revision = "$Revision$";
        public static String release = "1.3";      
        
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