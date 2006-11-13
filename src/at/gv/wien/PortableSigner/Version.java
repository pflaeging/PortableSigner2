/*
 * Version.java
 *
 * Created on 25. Oktober 2006, 14:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package at.gv.wien.PortableSigner;

/**
 * @author pfp
 */
public class Version {
	private static String date = "$Date: 2006-10-31 03:02:54 +0100 (Tue, 31 Oct 2006) $";
	private static String author = "$Author: pfp $";
	private static String revision = "$Revision: 46 $";
	public String print;
	
	public Version() {
		String cleanDate, cleanAuthor, cleanRevision;
		cleanDate = date.replace('$', ' ').trim();
		cleanAuthor = author.replace('$', ' ').trim();
		cleanRevision = revision.replace('$', ' ').trim();
		print =  cleanRevision + "\n" + cleanAuthor + "\n" + cleanDate;
	}

}