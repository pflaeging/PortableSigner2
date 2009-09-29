/*
 * This File is part of PortableSigner (http://portablesigner.sf.net/)
 *  and is under the European Public License V1.1 (http://www.osor.eu/eupl)
 * (c) Stadt Wien, Peter Pfläging <peter.pflaeging@wien.gv.at>
 *
 * Stolen from http://de.pastebin.ca/942635
 */

package at.gv.wien.PortableSigner;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolTipUI;

/*
 *  Renders ToolTips:
 *  - with multiple lines
 *  - autowidth + max. width
 *  - linewrap
 *  - uses jTextArea
 *  
 *  For ease of use insert the following lines into you Application:
 *  
 *      MultiLineToolTipUI.setMaximumWidth(250);
 *      MultiLineToolTipUI.initialize();
 *      javax.swing.ToolTipManager.sharedInstance().setDismissDelay(20000);
 *
 *  © Dexter 2008
 */
public class MultiLineToolTipUI extends BasicToolTipUI {

    protected CellRendererPane rendererPane;
    private static JTextArea textArea;
    private static int maximumWidth = 0;
    
    static MultiLineToolTipUI singleton = new MultiLineToolTipUI();

    public static void initialize() {
        // don't hardcode class name
        String key = "ToolTipUI";
        Class cls = singleton.getClass();
        String name = cls.getName();
        UIManager.put(key,name);
        UIManager.put(name,cls);
    }

    private MultiLineToolTipUI() {
        super();
    }

    public static ComponentUI createUI(JComponent c) {
        return singleton;
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        rendererPane = new CellRendererPane();
        c.add(rendererPane);
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        c.remove(rendererPane);
        rendererPane = null;
    }
    
    /**
     *  set maximum width
     *  0 = no maximum width
     */
    public static void setMaximumWidth(int width){
        maximumWidth = width;
    }

    public void paint(Graphics g, JComponent c) {
        Dimension size = c.getSize();
        textArea.setBackground(c.getBackground());
        rendererPane.paintComponent(g, textArea, c, 1, 1,
                    size.width - 1, size.height - 1, true);
    }

    public Dimension getPreferredSize(JComponent c) {
        String tipText = ((JToolTip)c).getTipText();
        if (tipText == null){
            return new Dimension(0,0);
        }
        textArea = new JTextArea(tipText );
        rendererPane.removeAll();
        rendererPane.add(textArea );
        textArea.setWrapStyleWord(true);

        textArea.setLineWrap(false);
        
        if( maximumWidth > 0 && maximumWidth < textArea.getPreferredSize().getWidth()){
            textArea.setLineWrap(true);
            Dimension d = textArea.getPreferredSize();    
            d.width = maximumWidth;
            d.height++;
            textArea.setSize(d);
         }
         Dimension dim = textArea.getPreferredSize();
         dim.height += 1;
         dim.width += 1;
         return dim;
    }

    public Dimension getMinimumSize(JComponent c) {
        return getPreferredSize(c);
    }

    public Dimension getMaximumSize(JComponent c) {
        return getPreferredSize(c);
    }
}