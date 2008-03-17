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
            // lastOutputFile = "",
            lastP12File = "",
            signLanguage = "",
            sigLogo = "",
            signComment = "";
    public Boolean signText = false, useComment = true, toolTip = true;
    private java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage (Main.class);
                
    /** Creates a new instance of Preferences */
    public Preferences() {
        get();
    }
    
    public void get() {
        // Language = new java.util.Locale(prefs.get("Language", "de"));
        lastInputFile = prefs.get("LastInputFile", "");
        // lastOutputFile = prefs.get("LastOutputFile", "");
        lastP12File = prefs.get("LastP12File", "");
        signText = prefs.getBoolean("SignText", false);
        signLanguage = prefs.get("SignLanguage", "german");
        sigLogo = prefs.get("SignatureLogo", "");
        signComment = prefs.get("SignComment", "");
        useComment = prefs.getBoolean("useComment", true);
        toolTip = prefs.getBoolean("ToolTip", true);
    }
    
    public void set(String property, String value) {
        prefs.put(property, value);
        get();
    }
    
    public void set(String property, Boolean value) {
        prefs.putBoolean(property, value);
        get();
    }

}
