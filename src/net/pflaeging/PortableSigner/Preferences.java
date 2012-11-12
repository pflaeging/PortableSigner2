/*
 * Preferences.java
 *
 * Created on 13. Oktober 2006, 16:30
 * This File is part of PortableSigner (http://portablesigner.sf.net/)
 *  and is under the European Public License V1.1 (http://www.osor.eu/eupl)
 * (c) Peter Pfläging <peter@pflaeging.net>
 */

package net.pflaeging.PortableSigner;


/**
 *
 * @author  peter@pflaeging.net
 */
public class Preferences {
    
    // public static java.util.Locale Language;
    
    public String lastInputFile = "",
            // lastOutputFile = "",
            lastP12File = "",
            signLanguage = "",
            sigLogo = "",
            signComment = "",
            signLocation = "",
            signReason = "";
    public Boolean signText = false, useComment = true, toolTip = true, noExtraPage = false, 
            signLastPage = true;
    public float verticalPos = 0f, leftMargin = 0f, rightMargin = 0f;
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
        signLanguage = prefs.get("SignLanguage", "en");
        sigLogo = prefs.get("SignatureLogo", "");
        signComment = prefs.get("SignComment", "");
        useComment = prefs.getBoolean("useComment", true);
        toolTip = prefs.getBoolean("ToolTip", true);
        signLocation = prefs.get("SignLocation", "");
        signReason = prefs.get("SignReason", "");
        noExtraPage = prefs.getBoolean("NoExtraPage", false);
        verticalPos = prefs.getFloat("VerticalPosition", 0f);
        leftMargin = prefs.getFloat("LeftMargin", 0f);
        rightMargin = prefs.getFloat("RightMargin", 0f);
        signLastPage = prefs.getBoolean("SignatureOnLastPage", true);
    }
    
    public void set(String property, String value) {
        prefs.put(property, value);
        get();
    }
    
    public void set(String property, Boolean value) {
        prefs.putBoolean(property, value);
        get();
    }
    public void set(String property, float value) {
        prefs.putFloat(property, value);
        get();
    }

}
