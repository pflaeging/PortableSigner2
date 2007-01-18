/*
 * Preferences.java
 *
 * Created on 13. Oktober 2006, 16:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package at.gv.wien.PortableSigner;


/**
 *
 * @author  pfp@adv.magwien.gv.at
 */
public class Preferences {
    
    // public static java.util.Locale Language;
    
    public String lastInputFile = "",
            lastOutputFile = "",
            lastP12File = "";
    private java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage (Main.class);
                
    /** Creates a new instance of Preferences */
    public Preferences() {
        get();
    }
    
    private void get() {
        // Language = new java.util.Locale(prefs.get("Language", "de"));
        lastInputFile = prefs.get("LastInputFile", "");
        lastOutputFile = prefs.get("LastOutputFile", "");
        lastP12File = prefs.get("LastP12File", "");
    }
    
    public void set(String property, String value) {
        prefs.put(property, value);
        get();
    }

}
