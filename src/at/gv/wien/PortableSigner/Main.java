/*
 * Main.java
 *
 * Created on 21. September 2006, 09:13
 */

package at.gv.wien.PortableSigner;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import java.awt.Cursor;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;

/*
 * @author  peter.pflaeging@wien.gv.at
 */
public class Main extends javax.swing.JFrame {
    
    private static SignCommandLine mycommand;
    public String copyright = "Peter Pfl\u00e4ging <peter.pflaeging@wien.gv.at>)";
    public String url = "http://portablesigner.sf.net/";
    public String contributors = "Contributors:\n" +
            "Bogdan Drozdowski, " +
            "Alessio Caiazza, " +
            "Dominik Joe Pant\u016f\u010dek" +
            "\n\nTranslations:\n" +
            "PL: Bogdan Drozdowski\n" +
            "IT: Alessio Caiazza\n" +
            "CZ: Tom\u00e1\u0161 Hal\u00e1sz\n" +
            "DE: Peter Pfl\u00e4ging\n";
    private Preferences  prefs;
    private static java.awt.Color resultcolor;
    private static String result, exceptionstring;
    public static String platform;
    String inputPDFFile = "", outputPDFFile = "", signatureP12File = "";
    String password = "";
    private Vector signatureBlockLanguages = new Vector();
    private static java.awt.Color colorok = new java.awt.Color(0, 240, 0);
    private static java.awt.Color colorerror = new java.awt.Color(240, 0, 0);
    private static java.awt.Color gotitcolor = new java.awt.Color(0, 0, 240);
    private static boolean workingJCE = true, finalize = true;
    Cursor questionCursor = new Cursor(Cursor.HAND_CURSOR);
    Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    Version version = new Version();
    
    
    /** Creates new form Main */
    public Main() {

        java.util.Enumeration<String> langNumbers =
                java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/SignatureblockLanguages")
                .getKeys();
        while ( langNumbers.hasMoreElements() )  {
            String languageCode = langNumbers.nextElement();
            String language = java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/SignatureblockLanguages").getString(languageCode);
//            System.out.println("Sprache: " +  language + " (" + languageCode + ")");
            signatureBlockLanguages.add(language + " (" + languageCode + ")");
        }

        prefs = new Preferences();
//        ReadStore teststore;
        if (prefs.signLanguage.length() != 2) { // we have old prefs!
            prefs.set("SignLanguage", "en");
            prefs.get();
        }
//        System.out.println(prefs.lastInputFile + prefs.lastOutputFile + prefs.lastP12File);
        String operatingSystem = System.getProperty("os.name");
        if (operatingSystem.contains("Mac OS X")) {
            platform = "mac";
            System.setProperty(
                    "Quaqua.tabLayoutPolicy","wrap"
                    );
            // set the Quaqua Look and Feel in the UIManager
            try {
                UIManager.setLookAndFeel(
                        "ch.randelshofer.quaqua.QuaquaLookAndFeel"
                        );
                // set UI manager properties here that affect Quaqua
            } catch (Exception e) {
                // take an appropriate action here
                System.err.println("Unable to load Aqua UI");
            }
        } else if (operatingSystem.contains("Windows")) {
            platform = "windows";
            try {
                UIManager.setLookAndFeel(
                        "net.java.plaf.windows.WindowsLookAndFeel"
                        );
                // set UI manager properties here that affect Quaqua
            } catch (Exception e) {
                // take an appropriate action here
                System.err.println("Unable to load Windows UI");
            }

        } else {
            platform = "other";
        }
      MultiLineToolTipUI.setMaximumWidth(250);
      MultiLineToolTipUI.initialize();
      javax.swing.ToolTipManager.sharedInstance().setDismissDelay(20000);
      
        initComponents();
        if (mycommand.signature == null) {
            jTextFieldSignaturefile.setText(prefs.lastP12File);
        } else {
            jTextFieldSignaturefile.setText(mycommand.signature);
        }
        if (mycommand.input == null) {
            jTextFieldInputfile.setText(prefs.lastInputFile);
        } else {
            jTextFieldInputfile.setText(mycommand.input);
        }
        if (mycommand.output == null) {
            jTextFieldOutputfile.setText(generateOutputFile(jTextFieldInputfile.getText()));
        } else {
            jTextFieldOutputfile.setText(mycommand.output);
        }
        if (mycommand.password != null) {
            jPasswordFieldPassword.setText(mycommand.password);
        }
        if (mycommand.sigimage.equals("")) {
            jTextFieldOptionLogo.setText(prefs.sigLogo);
        } else {
            jTextFieldOptionLogo.setText(mycommand.sigimage);
        }
        if (mycommand.location ==  null) {
            jTextFieldLocation.setText(prefs.signLocation);
        } else {
            jTextFieldLocation.setText(mycommand.location);
        }
        if (mycommand.reason == null) {
            jTextFieldReason.setText(prefs.signReason);
        } else {
            jTextFieldReason.setText(mycommand.reason);
        }
        if (mycommand.comment.equals("")) {
            jTextPaneCommentField.setText(prefs.signComment);
        } else {
            jTextPaneCommentField.setText(mycommand.comment);
            prefs.set("useComment", true);
            jCheckBoxComment.setSelected(true);
        }
        if (mycommand.finalize == false) {
            finalize = false;
        }

        if (!mycommand.sigblock.equals("")) {
            // backward compatibility, !!!!
            if (mycommand.sigblock.equals("german")) {
                prefs.set("SignLanguage", "2");
            } 
            if (mycommand.sigblock.equals("english")) {
                prefs.set("SignLanguage", "1");
            }

            prefs.set("SignText", true);
        }
        
        
        if (!workingJCE) {
            jDialogJCEAlert.setSize(650, 170);
            jDialogJCEAlert.setVisible(true);
            System.err.println(java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n")
                    .getString("JCEMissing"));
            System.exit(254);
        }

        if (prefs.toolTip) {
            jCheckBoxTooltip.setSelected(true);
            setTooltips(true);
        } else {
            jCheckBoxTooltip.setSelected(false);
            setTooltips(false);
        }

    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialogCancel = new javax.swing.JDialog();
        jPanelCancel = new javax.swing.JPanel();
        jButtonCancelYes = new javax.swing.JButton();
        jButtonCancelNo = new javax.swing.JButton();
        jLabelCancelQuestion = new javax.swing.JLabel();
        jDialogAbout = new javax.swing.JDialog();
        jLabelAboutText = new javax.swing.JLabel();
        jButtonAboutOk = new javax.swing.JButton();
        jScrollPaneAboutVersion = new javax.swing.JScrollPane();
        jTextAreaAboutVersion = new javax.swing.JTextArea();
        jLabelAboutCopyright = new javax.swing.JLabel();
        jButtonLicense = new javax.swing.JButton();
        jDialogLicense = new javax.swing.JDialog();
        jButtonLicenseOK = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaLicenseText = new javax.swing.JTextArea();
        jFrameOption = new javax.swing.JFrame();
        jButtonOptionSearchLogo = new javax.swing.JButton();
        jLabelOptionLanguage = new javax.swing.JLabel();
        jTextFieldOptionLogo = new javax.swing.JTextField();
        jLabelOptionLogo = new javax.swing.JLabel();
        jButtonOptionOK = new javax.swing.JButton();
        jButtonResetLogo = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextPaneCommentField = new javax.swing.JTextPane();
        jButtonResetCommentField = new javax.swing.JButton();
        jCheckBoxComment = new javax.swing.JCheckBox();
        jLabelReason = new javax.swing.JLabel();
        jTextFieldReason = new javax.swing.JTextField();
        jLabelLocation = new javax.swing.JLabel();
        jTextFieldLocation = new javax.swing.JTextField();
        jComboBoxSignatureLanguage = new javax.swing.JComboBox();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jFrameSelectKeystore = new javax.swing.JFrame();
        jButtonSelectKeystoreFile = new javax.swing.JButton();
        jButtonSelectKeystoreKeystore = new javax.swing.JButton();
        jButtonSelectKeystoreCancel = new javax.swing.JButton();
        jFrameChooseCert = new javax.swing.JFrame();
        jScrollPaneCerts = new javax.swing.JScrollPane();
        jListCerts = new javax.swing.JList();
        jButtonCertOK = new javax.swing.JButton();
        jButtonCertCancel = new javax.swing.JButton();
        jButtonCertInfo = new javax.swing.JButton();
        jFrameHelp = new javax.swing.JFrame();
        jScrollPane2 = new javax.swing.JScrollPane();
        jEditorPaneHelp = new javax.swing.JEditorPane();
        jDialogJCEAlert = new javax.swing.JDialog();
        jButtonJCEAlertOK = new javax.swing.JButton();
        jLabelJCEAlert = new javax.swing.JLabel();
        jDialogErrorReport = new javax.swing.JDialog();
        jLabelErrorReportHeading = new javax.swing.JLabel();
        jTextFieldErrorReport = new javax.swing.JTextField();
        jButtonErrorReportOK = new javax.swing.JButton();
        jLabelInput = new javax.swing.JLabel();
        jLabelOutput = new javax.swing.JLabel();
        jLabelSignature = new javax.swing.JLabel();
        jTextFieldInputfile = new javax.swing.JTextField();
        jButtonInputfile = new javax.swing.JButton();
        jTextFieldOutputfile = new javax.swing.JTextField();
        jButtonOutputfile = new javax.swing.JButton();
        jTextFieldSignaturefile = new javax.swing.JTextField();
        jButtonSignaturefile = new javax.swing.JButton();
        jButtonCancelMain = new javax.swing.JButton();
        jButtonAbout = new javax.swing.JButton();
        jLabelTitle = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabelPassword = new javax.swing.JLabel();
        jPasswordFieldPassword = new javax.swing.JPasswordField();
        jButtonPasswordOK = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabelSign = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabelWorking = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel7 = new javax.swing.JLabel();
        jLabelResult = new javax.swing.JLabel();
        jLabelFinished = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabelRestart = new javax.swing.JLabel();
        jLabelFinishNext = new javax.swing.JLabel();
        jCheckBoxSignatureBlock = new javax.swing.JCheckBox();
        jButtonOption = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jCheckBoxFinalize = new javax.swing.JCheckBox();
        jButtonErrorReport = new javax.swing.JButton();
        jButtonViewOutput = new javax.swing.JButton();
        jButtonViewSource = new javax.swing.JButton();
        jMenuBarMain = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemInputfile = new javax.swing.JMenuItem();
        jMenuItemOutputfile = new javax.swing.JMenuItem();
        jMenuItemSignaturefile = new javax.swing.JMenuItem();
        jMenuItemSign = new javax.swing.JMenuItem();
        jMenuItemQuit = new javax.swing.JMenuItem();
        jMenuEdit = new javax.swing.JMenu();
        jMenuItemOptions = new javax.swing.JMenuItem();
        jMenuHelp = new javax.swing.JMenu();
        jCheckBoxTooltip = new javax.swing.JCheckBoxMenuItem();
        jMenuItemHelp = new javax.swing.JMenuItem();
        jMenuItemAbout = new javax.swing.JMenuItem();

        jDialogCancel.setAlwaysOnTop(true);
        jDialogCancel.setLocationByPlatform(true);
        jDialogCancel.setModal(true);
        jDialogCancel.getContentPane().setLayout(new java.awt.GridLayout(1, 0));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n"); // NOI18N
        jButtonCancelYes.setText(bundle.getString("Yes")); // NOI18N
        jButtonCancelYes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelYesActionPerformed(evt);
            }
        });

        jButtonCancelNo.setText(bundle.getString("No")); // NOI18N
        jButtonCancelNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelNoActionPerformed(evt);
            }
        });

        jLabelCancelQuestion.setText(bundle.getString("ReallyCancel")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanelCancelLayout = new org.jdesktop.layout.GroupLayout(jPanelCancel);
        jPanelCancel.setLayout(jPanelCancelLayout);
        jPanelCancelLayout.setHorizontalGroup(
            jPanelCancelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelCancelLayout.createSequentialGroup()
                .add(jPanelCancelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanelCancelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jButtonCancelYes)
                        .add(5, 5, 5)
                        .add(jButtonCancelNo))
                    .add(jPanelCancelLayout.createSequentialGroup()
                        .add(32, 32, 32)
                        .add(jLabelCancelQuestion)))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanelCancelLayout.setVerticalGroup(
            jPanelCancelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelCancelLayout.createSequentialGroup()
                .add(8, 8, 8)
                .add(jLabelCancelQuestion)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelCancelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButtonCancelNo)
                    .add(jButtonCancelYes)))
        );

        jDialogCancel.getContentPane().add(jPanelCancel);

        jDialogAbout.setLocationByPlatform(true);
        jDialogAbout.setResizable(false);

        jLabelAboutText.setFont(new java.awt.Font("Lucida Grande", 1, 18));
        jLabelAboutText.setText("PortableSigner " + Version.release);

        jButtonAboutOk.setText(bundle.getString("OK")); // NOI18N
        jButtonAboutOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAboutOkActionPerformed(evt);
            }
        });

        jTextAreaAboutVersion.setColumns(20);
        jTextAreaAboutVersion.setRows(5);
        jScrollPaneAboutVersion.setViewportView(jTextAreaAboutVersion);

        jLabelAboutCopyright.setText("(c) Stadt Wien");

        jButtonLicense.setText(bundle.getString("LicenseButton")); // NOI18N
        jButtonLicense.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLicenseActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jDialogAboutLayout = new org.jdesktop.layout.GroupLayout(jDialogAbout.getContentPane());
        jDialogAbout.getContentPane().setLayout(jDialogAboutLayout);
        jDialogAboutLayout.setHorizontalGroup(
            jDialogAboutLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialogAboutLayout.createSequentialGroup()
                .add(jDialogAboutLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jDialogAboutLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jDialogAboutLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPaneAboutVersion, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jDialogAboutLayout.createSequentialGroup()
                                .add(jLabelAboutCopyright)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 144, Short.MAX_VALUE)
                                .add(jButtonLicense)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jButtonAboutOk))))
                    .add(jDialogAboutLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabelAboutText)))
                .addContainerGap())
        );
        jDialogAboutLayout.setVerticalGroup(
            jDialogAboutLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jDialogAboutLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabelAboutText)
                .add(14, 14, 14)
                .add(jScrollPaneAboutVersion, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jDialogAboutLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButtonAboutOk)
                    .add(jLabelAboutCopyright)
                    .add(jButtonLicense))
                .addContainerGap())
        );

        jButtonLicenseOK.setText("OK");
        jButtonLicenseOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLicenseOKActionPerformed(evt);
            }
        });

        jTextAreaLicenseText.setColumns(20);
        jTextAreaLicenseText.setRows(5);
        jTextAreaLicenseText.setText(bundle.getString("LicenseText")); // NOI18N
        jScrollPane1.setViewportView(jTextAreaLicenseText);

        org.jdesktop.layout.GroupLayout jDialogLicenseLayout = new org.jdesktop.layout.GroupLayout(jDialogLicense.getContentPane());
        jDialogLicense.getContentPane().setLayout(jDialogLicenseLayout);
        jDialogLicenseLayout.setHorizontalGroup(
            jDialogLicenseLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialogLicenseLayout.createSequentialGroup()
                .addContainerGap()
                .add(jDialogLicenseLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jButtonLicenseOK)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE))
                .addContainerGap())
        );
        jDialogLicenseLayout.setVerticalGroup(
            jDialogLicenseLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jDialogLicenseLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButtonLicenseOK)
                .addContainerGap())
        );

        jFrameOption.setTitle("PortableSigner: Options");
        jFrameOption.setAlwaysOnTop(true);

        jButtonOptionSearchLogo.setText(bundle.getString("SearchButton")); // NOI18N
        jButtonOptionSearchLogo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOptionSearchLogoActionPerformed(evt);
            }
        });

        jLabelOptionLanguage.setText(bundle.getString("LanguageOfSignatureBlock")); // NOI18N

        jLabelOptionLogo.setText(bundle.getString("SignatureLogo")); // NOI18N

        jButtonOptionOK.setText(bundle.getString("OK")); // NOI18N
        jButtonOptionOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOptionOKActionPerformed(evt);
            }
        });

        jButtonResetLogo.setText(bundle.getString("ResetLogo")); // NOI18N
        jButtonResetLogo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonResetLogoActionPerformed(evt);
            }
        });

        jScrollPane3.setViewportView(jTextPaneCommentField);

        jButtonResetCommentField.setText(bundle.getString("ResetCommentField")); // NOI18N
        jButtonResetCommentField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonResetCommentFieldActionPerformed(evt);
            }
        });

        jCheckBoxComment.setSelected(prefs.useComment);
        jCheckBoxComment.setText(bundle.getString("UseCommentField")); // NOI18N
        jCheckBoxComment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxCommentActionPerformed(evt);
            }
        });

        jLabelReason.setText(bundle.getString("ReasonForSignature")); // NOI18N

        jTextFieldReason.setText(prefs.signReason);
        jTextFieldReason.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldReasonActionPerformed(evt);
            }
        });

        jLabelLocation.setText(bundle.getString("Location")); // NOI18N

        jTextFieldLocation.setText(prefs.signLocation);
        jTextFieldLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldLocationActionPerformed(evt);
            }
        });

        jComboBoxSignatureLanguage.setModel(new DefaultComboBoxModel(signatureBlockLanguages));
        jComboBoxSignatureLanguage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxSignatureLanguageActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jFrameOptionLayout = new org.jdesktop.layout.GroupLayout(jFrameOption.getContentPane());
        jFrameOption.getContentPane().setLayout(jFrameOptionLayout);
        jFrameOptionLayout.setHorizontalGroup(
            jFrameOptionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jFrameOptionLayout.createSequentialGroup()
                .addContainerGap()
                .add(jFrameOptionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jTextFieldLocation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jTextFieldReason, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
                    .add(jLabelOptionLanguage)
                    .add(jFrameOptionLayout.createSequentialGroup()
                        .add(jLabelOptionLogo)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jFrameOptionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jFrameOptionLayout.createSequentialGroup()
                                .add(jButtonResetLogo)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 314, Short.MAX_VALUE)
                                .add(jButtonOptionOK))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jFrameOptionLayout.createSequentialGroup()
                                .add(jFrameOptionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(jComboBoxSignatureLanguage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 182, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(jTextFieldOptionLogo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jButtonOptionSearchLogo))))
                    .add(jButtonResetCommentField)
                    .add(jCheckBoxComment)
                    .add(jLabelReason)
                    .add(jLabelLocation))
                .addContainerGap())
        );
        jFrameOptionLayout.setVerticalGroup(
            jFrameOptionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jFrameOptionLayout.createSequentialGroup()
                .addContainerGap()
                .add(jFrameOptionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelOptionLanguage)
                    .add(jComboBoxSignatureLanguage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(10, 10, 10)
                .add(jFrameOptionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelOptionLogo)
                    .add(jTextFieldOptionLogo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButtonOptionSearchLogo))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButtonResetLogo)
                .add(18, 18, 18)
                .add(jCheckBoxComment)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 134, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButtonResetCommentField)
                .add(18, 18, 18)
                .add(jLabelReason)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTextFieldReason, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabelLocation)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTextFieldLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 10, Short.MAX_VALUE)
                .add(jButtonOptionOK)
                .addContainerGap())
        );

        jFrameSelectKeystore.setTitle("Select Keystore Type");
        jFrameSelectKeystore.setAlwaysOnTop(true);

        jButtonSelectKeystoreFile.setText("Select PKCS#12 File ...");
        jButtonSelectKeystoreFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelectKeystoreFileActionPerformed(evt);
            }
        });

        jButtonSelectKeystoreKeystore.setText("Select from Windows Keystore ...");
        jButtonSelectKeystoreKeystore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelectKeystoreKeystoreActionPerformed(evt);
            }
        });

        jButtonSelectKeystoreCancel.setText("Cancel");
        jButtonSelectKeystoreCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelectKeystoreCancelActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jFrameSelectKeystoreLayout = new org.jdesktop.layout.GroupLayout(jFrameSelectKeystore.getContentPane());
        jFrameSelectKeystore.getContentPane().setLayout(jFrameSelectKeystoreLayout);
        jFrameSelectKeystoreLayout.setHorizontalGroup(
            jFrameSelectKeystoreLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jFrameSelectKeystoreLayout.createSequentialGroup()
                .addContainerGap()
                .add(jFrameSelectKeystoreLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jButtonSelectKeystoreFile)
                    .add(jButtonSelectKeystoreKeystore))
                .addContainerGap(132, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jFrameSelectKeystoreLayout.createSequentialGroup()
                .addContainerGap(295, Short.MAX_VALUE)
                .add(jButtonSelectKeystoreCancel)
                .addContainerGap())
        );
        jFrameSelectKeystoreLayout.setVerticalGroup(
            jFrameSelectKeystoreLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jFrameSelectKeystoreLayout.createSequentialGroup()
                .addContainerGap()
                .add(jButtonSelectKeystoreFile)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButtonSelectKeystoreKeystore)
                .addContainerGap(48, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jFrameSelectKeystoreLayout.createSequentialGroup()
                .addContainerGap(81, Short.MAX_VALUE)
                .add(jButtonSelectKeystoreCancel)
                .addContainerGap())
        );

        jFrameChooseCert.setTitle("Choose Certificate");
        jFrameChooseCert.setAlwaysOnTop(true);

        jListCerts.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jListCerts.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPaneCerts.setViewportView(jListCerts);

        jButtonCertOK.setText("Use");

        jButtonCertCancel.setText("Cancel");
        jButtonCertCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCertCancelActionPerformed(evt);
            }
        });

        jButtonCertInfo.setText("Info");
        jButtonCertInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCertInfoActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jFrameChooseCertLayout = new org.jdesktop.layout.GroupLayout(jFrameChooseCert.getContentPane());
        jFrameChooseCert.getContentPane().setLayout(jFrameChooseCertLayout);
        jFrameChooseCertLayout.setHorizontalGroup(
            jFrameChooseCertLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jFrameChooseCertLayout.createSequentialGroup()
                .addContainerGap()
                .add(jFrameChooseCertLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPaneCerts, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
                    .add(jFrameChooseCertLayout.createSequentialGroup()
                        .add(jButtonCertInfo)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 391, Short.MAX_VALUE)
                        .add(jButtonCertCancel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButtonCertOK)))
                .addContainerGap())
        );
        jFrameChooseCertLayout.setVerticalGroup(
            jFrameChooseCertLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jFrameChooseCertLayout.createSequentialGroup()
                .add(26, 26, 26)
                .add(jScrollPaneCerts, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                .add(24, 24, 24)
                .add(jFrameChooseCertLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButtonCertOK)
                    .add(jButtonCertCancel)
                    .add(jButtonCertInfo))
                .addContainerGap())
        );

        jFrameHelp.setTitle(bundle.getString("PortableSigner_Help")); // NOI18N

        jEditorPaneHelp.setContentType("text/html"); // NOI18N
        jEditorPaneHelp.setEditable(false);
        jScrollPane2.setViewportView(jEditorPaneHelp);

        org.jdesktop.layout.GroupLayout jFrameHelpLayout = new org.jdesktop.layout.GroupLayout(jFrameHelp.getContentPane());
        jFrameHelp.getContentPane().setLayout(jFrameHelpLayout);
        jFrameHelpLayout.setHorizontalGroup(
            jFrameHelpLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jFrameHelpLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)
                .addContainerGap())
        );
        jFrameHelpLayout.setVerticalGroup(
            jFrameHelpLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jFrameHelpLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE)
                .addContainerGap())
        );

        jDialogJCEAlert.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jDialogJCEAlert.setTitle("JCE!");
        jDialogJCEAlert.setAlwaysOnTop(true);
        jDialogJCEAlert.setModal(true);
        jDialogJCEAlert.setName("JCE Policy not installed"); // NOI18N

        jButtonJCEAlertOK.setText("OK");
        jButtonJCEAlertOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonJCEAlertOKActionPerformed(evt);
            }
        });

        jLabelJCEAlert.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelJCEAlert.setText(bundle.getString("JCEmissing")); // NOI18N
        jLabelJCEAlert.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        org.jdesktop.layout.GroupLayout jDialogJCEAlertLayout = new org.jdesktop.layout.GroupLayout(jDialogJCEAlert.getContentPane());
        jDialogJCEAlert.getContentPane().setLayout(jDialogJCEAlertLayout);
        jDialogJCEAlertLayout.setHorizontalGroup(
            jDialogJCEAlertLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jDialogJCEAlertLayout.createSequentialGroup()
                .addContainerGap()
                .add(jDialogJCEAlertLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabelJCEAlert, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jButtonJCEAlertOK))
                .addContainerGap())
        );
        jDialogJCEAlertLayout.setVerticalGroup(
            jDialogJCEAlertLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jDialogJCEAlertLayout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .add(jLabelJCEAlert, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 41, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButtonJCEAlertOK)
                .addContainerGap())
        );

        jDialogErrorReport.setTitle(bundle.getString("ErrorReportTitle")); // NOI18N
        jDialogErrorReport.setAlwaysOnTop(true);

        jLabelErrorReportHeading.setText(bundle.getString("ErrorReportLabel")); // NOI18N

        jTextFieldErrorReport.setEditable(false);
        jTextFieldErrorReport.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jTextFieldErrorReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldErrorReportActionPerformed(evt);
            }
        });

        jButtonErrorReportOK.setText(bundle.getString("OK")); // NOI18N
        jButtonErrorReportOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonErrorReportOKActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jDialogErrorReportLayout = new org.jdesktop.layout.GroupLayout(jDialogErrorReport.getContentPane());
        jDialogErrorReport.getContentPane().setLayout(jDialogErrorReportLayout);
        jDialogErrorReportLayout.setHorizontalGroup(
            jDialogErrorReportLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialogErrorReportLayout.createSequentialGroup()
                .addContainerGap()
                .add(jDialogErrorReportLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jTextFieldErrorReport, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE)
                    .add(jLabelErrorReportHeading)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jButtonErrorReportOK))
                .addContainerGap())
        );
        jDialogErrorReportLayout.setVerticalGroup(
            jDialogErrorReportLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialogErrorReportLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabelErrorReportHeading)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jTextFieldErrorReport, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButtonErrorReportOK)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("PortableSigner");
        setLocationByPlatform(true);

        jLabelInput.setText(bundle.getString("InputLabel")); // NOI18N

        jLabelOutput.setText(bundle.getString("OutputLabel")); // NOI18N

        jLabelSignature.setText(bundle.getString("SignatureLabel")); // NOI18N

        jButtonInputfile.setText(bundle.getString("SearchButton")); // NOI18N
        jButtonInputfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonInputfileActionPerformed(evt);
            }
        });

        jButtonOutputfile.setText(bundle.getString("SearchButton")); // NOI18N
        jButtonOutputfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOutputfileActionPerformed(evt);
            }
        });

        jButtonSignaturefile.setText(bundle.getString("SearchButton")); // NOI18N
        jButtonSignaturefile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSignaturefileActionPerformed(evt);
            }
        });

        jButtonCancelMain.setText(bundle.getString("CancelButton")); // NOI18N
        jButtonCancelMain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelMainActionPerformed(evt);
            }
        });

        jButtonAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/gv/wien/PortableSigner/PortableSignerLogo-small.png"))); // NOI18N
        jButtonAbout.setText(bundle.getString("AboutButton")); // NOI18N
        jButtonAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAboutActionPerformed(evt);
            }
        });

        jLabelTitle.setFont(new java.awt.Font("Lucida Grande", 3, 14));
        jLabelTitle.setText(bundle.getString("SignPDF")); // NOI18N

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 3, 13));
        jLabel1.setText("1.");

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 3, 13));
        jLabel2.setText("2.");

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 3, 13));
        jLabel3.setText("3.");

        jLabel4.setFont(new java.awt.Font("Lucida Grande", 3, 13));
        jLabel4.setText("4.");

        jLabelPassword.setText(bundle.getString("PasswordLabel")); // NOI18N

        jPasswordFieldPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPasswordFieldPasswordActionPerformed(evt);
            }
        });

        jButtonPasswordOK.setText(bundle.getString("OK")); // NOI18N
        jButtonPasswordOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPasswordOKActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Lucida Grande", 3, 13));
        jLabel5.setText("5.");

        jLabelSign.setText(bundle.getString("Sign")); // NOI18N

        jLabel6.setFont(new java.awt.Font("Lucida Grande", 3, 13));
        jLabel6.setText("6.");

        jLabelWorking.setText(bundle.getString("Working")); // NOI18N

        jLabel7.setFont(new java.awt.Font("Lucida Grande", 3, 13));
        jLabel7.setText("7.");

        jLabelResult.setText(bundle.getString("Result")); // NOI18N

        jLabelFinished.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelFinished.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabelFinished.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jLabel8.setFont(new java.awt.Font("Lucida Grande", 3, 13));
        jLabel8.setText("8.");

        jLabelRestart.setText(bundle.getString("Restart")); // NOI18N

        jLabelFinishNext.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        jLabelFinishNext.setText(bundle.getString("Goto_1")); // NOI18N

        jCheckBoxSignatureBlock.setSelected(prefs.signText);
        jCheckBoxSignatureBlock.setText(bundle.getString("SignatureBlockLabel")); // NOI18N
        jCheckBoxSignatureBlock.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxSignatureBlock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxSignatureBlockActionPerformed(evt);
            }
        });

        jButtonOption.setText(bundle.getString("OptionsButton")); // NOI18N
        jButtonOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOptionActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Lucida Grande", 3, 13));
        jLabel10.setText("9.");

        jCheckBoxFinalize.setSelected(true);
        jCheckBoxFinalize.setText(bundle.getString("FinalizeDocument")); // NOI18N
        jCheckBoxFinalize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxFinalizeActionPerformed(evt);
            }
        });

        jButtonErrorReport.setText(bundle.getString("ErrorReportButton")); // NOI18N
        jButtonErrorReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonErrorReportActionPerformed(evt);
            }
        });

        jButtonViewOutput.setText(bundle.getString("ViewButton")); // NOI18N
        jButtonViewOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonViewOutputActionPerformed(evt);
            }
        });

        jButtonViewSource.setText(bundle.getString("ViewButton")); // NOI18N
        jButtonViewSource.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonViewSourceActionPerformed(evt);
            }
        });

        jMenuFile.setText(bundle.getString("MenuFile")); // NOI18N
        jMenuFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuFileActionPerformed(evt);
            }
        });

        jMenuItemInputfile.setText(bundle.getString("MenuInputfile")); // NOI18N
        jMenuItemInputfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemInputfileActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemInputfile);

        jMenuItemOutputfile.setText(bundle.getString("MenuOutputfile")); // NOI18N
        jMenuItemOutputfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOutputfileActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemOutputfile);

        jMenuItemSignaturefile.setText(bundle.getString("MenuSignaturfile")); // NOI18N
        jMenuItemSignaturefile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSignaturefileActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemSignaturefile);

        jMenuItemSign.setText(bundle.getString("MenuSign")); // NOI18N
        jMenuItemSign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSignActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemSign);

        jMenuItemQuit.setText(bundle.getString("MenuQuit")); // NOI18N
        jMenuItemQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemQuitActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemQuit);

        jMenuBarMain.add(jMenuFile);

        jMenuEdit.setText(bundle.getString("MenuEdit")); // NOI18N

        jMenuItemOptions.setText(bundle.getString("MenuOptions")); // NOI18N
        jMenuItemOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOptionsActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemOptions);

        jMenuBarMain.add(jMenuEdit);

        jMenuHelp.setText(bundle.getString("MenuHelp")); // NOI18N

        jCheckBoxTooltip.setText(bundle.getString("ToolTips")); // NOI18N
        jCheckBoxTooltip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxTooltipActionPerformed(evt);
            }
        });
        jMenuHelp.add(jCheckBoxTooltip);

        jMenuItemHelp.setText(bundle.getString("MenuHelpHelp")); // NOI18N
        jMenuItemHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemHelpActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuItemHelp);

        jMenuItemAbout.setText(bundle.getString("MenuAbout")); // NOI18N
        jMenuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAboutActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuItemAbout);

        jMenuBarMain.add(jMenuHelp);

        setJMenuBar(jMenuBarMain);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 646, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(jLabelTitle)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 368, Short.MAX_VALUE)
                        .add(jButtonAbout))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(jLabel1)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabelInput))
                            .add(layout.createSequentialGroup()
                                .add(jLabel2)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabelOutput))
                            .add(layout.createSequentialGroup()
                                .add(jLabel3)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabelSignature))
                            .add(jLabel4)
                            .add(layout.createSequentialGroup()
                                .add(jLabel5)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabelPassword))
                            .add(layout.createSequentialGroup()
                                .add(jLabel6)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabelSign))
                            .add(layout.createSequentialGroup()
                                .add(jLabel7)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabelWorking))
                            .add(layout.createSequentialGroup()
                                .add(jLabel10)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabelRestart))
                            .add(layout.createSequentialGroup()
                                .add(jLabel8)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabelResult)))
                        .add(10, 10, 10)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(jLabelFinishNext)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 350, Short.MAX_VALUE)
                                .add(jButtonCancelMain))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .add(jCheckBoxSignatureBlock)
                                        .add(128, 128, 128))
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPasswordFieldPassword, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
                                            .add(jTextFieldSignaturefile, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
                                            .add(org.jdesktop.layout.GroupLayout.LEADING, jProgressBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
                                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabelFinished, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
                                            .add(layout.createSequentialGroup()
                                                .add(jCheckBoxFinalize)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 106, Short.MAX_VALUE)
                                                .add(jButtonPasswordOK)))
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                            .add(org.jdesktop.layout.GroupLayout.LEADING, jTextFieldInputfile, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
                                            .add(jTextFieldOutputfile, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE))
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(layout.createSequentialGroup()
                                        .add(9, 9, 9)
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jButtonInputfile, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jButtonOutputfile, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jButtonSignaturefile, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jButtonOption, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                            .add(jButtonViewOutput)
                                            .add(jButtonViewSource)))
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(jButtonErrorReport)
                                        .add(34, 34, 34)))))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelTitle)
                    .add(jButtonAbout))
                .add(18, 18, 18)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelInput)
                    .add(jLabel1)
                    .add(jButtonInputfile)
                    .add(jTextFieldInputfile, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButtonViewSource))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelOutput)
                    .add(jTextFieldOutputfile, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2)
                    .add(jButtonOutputfile)
                    .add(jButtonViewOutput))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelSignature)
                    .add(jTextFieldSignaturefile, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3)
                    .add(jButtonSignaturefile))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(jCheckBoxSignatureBlock)
                    .add(jButtonOption))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jPasswordFieldPassword, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabelPassword)
                            .add(jLabel5))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabelSign)
                            .add(jLabel6)
                            .add(jButtonPasswordOK)
                            .add(jCheckBoxFinalize))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel7)
                            .add(jLabelWorking)
                            .add(jProgressBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabelResult)
                            .add(jLabelFinished, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 47, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel8))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jButtonErrorReport)
                        .add(23, 23, 23)))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jButtonCancelMain)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabelFinishNext)
                        .add(jLabel10)
                        .add(jLabelRestart)))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jButtonErrorReport.setVisible(false);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jMenuItemHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemHelpActionPerformed
        try {
            com.lowagie.tools.Executable.launchBrowser(java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n")
                .getString("HomepageURL"));
        } catch (IOException ex) {
            System.err.println("Unable to launch Browser for helppage");
        }

      
}//GEN-LAST:event_jMenuItemHelpActionPerformed

    private void jMenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAboutActionPerformed
    jButtonAboutActionPerformed(evt);
}//GEN-LAST:event_jMenuItemAboutActionPerformed

private void jMenuItemOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOptionsActionPerformed
    jButtonOptionActionPerformed(evt);
}//GEN-LAST:event_jMenuItemOptionsActionPerformed

private void jMenuItemQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemQuitActionPerformed
    jButtonCancelYesActionPerformed(evt);
}//GEN-LAST:event_jMenuItemQuitActionPerformed

private void jMenuItemSignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSignActionPerformed
    jButtonPasswordOKActionPerformed(evt);
}//GEN-LAST:event_jMenuItemSignActionPerformed

private void jMenuItemSignaturefileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSignaturefileActionPerformed
    jButtonSignaturefileActionPerformed(evt);
}//GEN-LAST:event_jMenuItemSignaturefileActionPerformed

private void jMenuItemOutputfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOutputfileActionPerformed
    jButtonOutputfileActionPerformed(evt);    
}//GEN-LAST:event_jMenuItemOutputfileActionPerformed

private void jMenuItemInputfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemInputfileActionPerformed
    jButtonInputfileActionPerformed(evt);
}//GEN-LAST:event_jMenuItemInputfileActionPerformed

    private void jMenuFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuFileActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_jMenuFileActionPerformed

private void jButtonCertInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCertInfoActionPerformed
    if (jListCerts.getSelectedIndex() != -1)
    {
        System.out.println("Index: " + jListCerts.getSelectedIndex());
    }
}//GEN-LAST:event_jButtonCertInfoActionPerformed

private void jButtonCertCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCertCancelActionPerformed
    jFrameChooseCert.setVisible(false);
}//GEN-LAST:event_jButtonCertCancelActionPerformed

private void jButtonSelectKeystoreKeystoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectKeystoreKeystoreActionPerformed
    jFrameChooseCert.setSize(500, 300);
    jFrameChooseCert.setVisible(true);
}//GEN-LAST:event_jButtonSelectKeystoreKeystoreActionPerformed

private void jButtonSelectKeystoreCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectKeystoreCancelActionPerformed
    jFrameSelectKeystore.setVisible(false);
}//GEN-LAST:event_jButtonSelectKeystoreCancelActionPerformed

private void jButtonSelectKeystoreFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectKeystoreFileActionPerformed
        jFrameSelectKeystore.setVisible(false);
        String file = chooseP12File();
        // do nothing if open dialog was cancelled
        if (file == null) {
            return;
        }
        jTextFieldSignaturefile.setText(file);
//        System.out.println(file);
}//GEN-LAST:event_jButtonSelectKeystoreFileActionPerformed

    private void jButtonResetLogoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResetLogoActionPerformed
        jTextFieldOptionLogo.setText("");
    }//GEN-LAST:event_jButtonResetLogoActionPerformed

    private void jButtonOptionSearchLogoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOptionSearchLogoActionPerformed
        jFrameOption.setAlwaysOnTop(false);
        String file = chooseImageFile();
        // do nothing if open dialog was cancelled
        if (file == null) {
            jFrameOption.setAlwaysOnTop(true);
            return;
        }
        jTextFieldOptionLogo.setText(file);
//        System.out.println(file);
        jFrameOption.setAlwaysOnTop(true);
    }//GEN-LAST:event_jButtonOptionSearchLogoActionPerformed

    private void jButtonOptionOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOptionOKActionPerformed
        prefs.set("SignatureLogo",jTextFieldOptionLogo.getText());
        prefs.set("SignComment", jTextPaneCommentField.getText());
        prefs.set("SignReason", jTextFieldReason.getText());
        prefs.set("SignLocation", jTextFieldLocation.getText());
        // System.out.println("SigLogo: " + prefs.sigLogo );
        jFrameOption.setVisible(false);
    }//GEN-LAST:event_jButtonOptionOKActionPerformed

    private void jButtonOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOptionActionPerformed
        
        jFrameOption.setSize(550,550);
        String lang = prefs.signLanguage;
        java.util.ResourceBundle block = java.util.ResourceBundle.getBundle(
                                            "at/gv/wien/PortableSigner/Signatureblock_" + lang);
        jComboBoxSignatureLanguage.setSelectedItem(
                java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/SignatureblockLanguages")
                .getString(lang) + " (" + lang + ")");
        jTextPaneCommentField.setEditable(prefs.useComment);
        if (prefs.signComment.equals("")) {
            jTextPaneCommentField.setText(block.getString("comment"));
        }
        jFrameOption.setVisible(true);
    }//GEN-LAST:event_jButtonOptionActionPerformed

    private void jCheckBoxSignatureBlockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxSignatureBlockActionPerformed
        prefs.set("SignText", !prefs.signText);
        // System.out.println(prefs.signText);
    }//GEN-LAST:event_jCheckBoxSignatureBlockActionPerformed

    private void jPasswordFieldPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPasswordFieldPasswordActionPerformed
        jButtonPasswordOKActionPerformed(evt);
    }//GEN-LAST:event_jPasswordFieldPasswordActionPerformed

    private void jButtonLicenseOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLicenseOKActionPerformed
        jDialogLicense.setVisible(false);        
    }//GEN-LAST:event_jButtonLicenseOKActionPerformed

    private void jButtonLicenseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLicenseActionPerformed
        jDialogLicense.setVisible(true);
        jDialogLicense.setSize(450,450);
    }//GEN-LAST:event_jButtonLicenseActionPerformed
    
    private void jButtonPasswordOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPasswordOKActionPerformed
        // DoSign Sign;
        
        jButtonErrorReport.setVisible(false);
        jProgressBar1.setIndeterminate(true);
        jLabelFinished.setText("");
        jLabelFinished.setForeground(new java.awt.Color(0,0,0));
        //String inputPDFFile = "", outputPDFFile = "", signatureP12File = "";
        //String password = "";
        password = String.valueOf(jPasswordFieldPassword.getPassword());
        // Clear password after using it (from bogdandr@op.pl)
        jPasswordFieldPassword.setText ("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		jPasswordFieldPassword.setText ("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
		jPasswordFieldPassword.setText ("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCC");
		jPasswordFieldPassword.setText ("DDDDDDDDDDDDDDDDDDDDDDDDDDDDDD");
		jPasswordFieldPassword.setText ("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
		jPasswordFieldPassword.setText ("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
		jPasswordFieldPassword.setText ("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
		jPasswordFieldPassword.setText ("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
		jPasswordFieldPassword.setText ("IIIIIIIIIIIIIIIIIIIIIIIIIIIIII");
		jPasswordFieldPassword.setText ("JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ");
		jPasswordFieldPassword.setText ("KKKKKKKKKKKKKKKKKKKKKKKKKKKKKK");
		jPasswordFieldPassword.setText ("LLLLLLLLLLLLLLLLLLLLLLLLLLLLLL");
		jPasswordFieldPassword.setText ("MMMMMMMMMMMMMMMMMMMMMMMMMMMMMM");
		jPasswordFieldPassword.setText ("NNNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
		jPasswordFieldPassword.setText ("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
		jPasswordFieldPassword.setText ("");

        inputPDFFile = jTextFieldInputfile.getText();
        outputPDFFile = jTextFieldOutputfile.getText();
        signatureP12File = jTextFieldSignaturefile.getText();
        if (inputPDFFile == null || outputPDFFile == null || signatureP12File == null) {
            return;
        }
        final String sigComment;
        if (prefs.useComment) {
            sigComment = prefs.signComment;
        } else {
            sigComment = "";
        }
        // System.err.println("Comment:" + prefs.useComment + ">" + sigComment + "<");
        prefs.set("LastInputFile", inputPDFFile);
        // prefs.set("LastOutputFile", outputPDFFile);
        prefs.set("LastP12File", signatureP12File);
        result = null;
        exceptionstring = null;
        
        // create Thread for signing
        Runnable runnable = new Runnable() {
            public void run() {
                new DoSignPDF(inputPDFFile,
                        outputPDFFile,
                        signatureP12File,
                        password,
                        prefs.signText,
                        prefs.signLanguage,
                        prefs.sigLogo,
                        finalize,
                        sigComment,
                        prefs.signReason,
                        prefs.signLocation,
                        null);
                // password cleanup (from bogdandr@op.pl)
				password = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
				System.gc ();
                jProgressBar1.setIndeterminate(false);
                jProgressBar1.setValue(100);
                //getParent().setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
                jLabelFinished.setForeground(resultcolor);
                jLabelFinished.setToolTipText(exceptionstring);
                jLabelFinished.setText(result);
                jDialogErrorReport.setSize(500, 200);
                if (resultcolor.equals(colorerror)) {
                    jButtonErrorReport.setText(java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n")
                            .getString("ErrorReportButton"));
                } else {
                    jButtonErrorReport.setText(java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n")
                            .getString("ViewButton"));
                    jButtonErrorReport.setForeground(gotitcolor);
                }
                jButtonErrorReport.setVisible(true);
                jTextFieldErrorReport.setText(exceptionstring);
            }
        };
        
        
        Thread thread = new Thread(runnable);
        thread.start();
        
    }//GEN-LAST:event_jButtonPasswordOKActionPerformed
    
    private void jButtonAboutOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAboutOkActionPerformed
        jDialogAbout.setVisible(false);
    }//GEN-LAST:event_jButtonAboutOkActionPerformed
    
    private void jButtonAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAboutActionPerformed
        jTextAreaAboutVersion.setText(
                copyright + "\n" + url + "\n\n" +
                Version.print + "\n\n" + contributors);
        jTextAreaAboutVersion.setCaretPosition(0);
        jDialogAbout.setSize(500,300);
        jDialogAbout.setVisible(true);
    }//GEN-LAST:event_jButtonAboutActionPerformed
    
    private void jButtonCancelNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelNoActionPerformed
        jDialogCancel.setVisible(false);
    }//GEN-LAST:event_jButtonCancelNoActionPerformed
    
    private void jButtonCancelYesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelYesActionPerformed
        dispose();
        System.exit(0);
    }//GEN-LAST:event_jButtonCancelYesActionPerformed
    
    private void jButtonCancelMainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelMainActionPerformed
        jDialogCancel.setSize(200, 100);
        jDialogCancel.setVisible(true);
    }//GEN-LAST:event_jButtonCancelMainActionPerformed
    
    private void jButtonSignaturefileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSignaturefileActionPerformed

// At the moment the action for selecting the keystore is disabled. Keystores are not working!        
//        jFrameSelectKeystore.setSize(380, 130);
//        jFrameSelectKeystore.setVisible(true);
                String file = chooseP12File();
        // do nothing if open dialog was cancelled
        if (file == null) {
            return;
        }
        jTextFieldSignaturefile.setText(file);

    }//GEN-LAST:event_jButtonSignaturefileActionPerformed
    
    private void jButtonOutputfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOutputfileActionPerformed
        String file = choosePDFFileSave();
        // do nothing if open dialog was cancelled
        if (file == null) {
            return;
        }
        jTextFieldOutputfile.setText(file);
//        System.out.println(file);
    }//GEN-LAST:event_jButtonOutputfileActionPerformed
    
    private void jButtonInputfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonInputfileActionPerformed
        String file = choosePDFFile();
        // do nothing if open dialog was cancelled
        if (file == null) {
            return;
        }
        // System.out.println("output: " + generateOutputFile(file));
        jTextFieldOutputfile.setText(generateOutputFile(file));
        jTextFieldInputfile.setText(file);
    }//GEN-LAST:event_jButtonInputfileActionPerformed

    private void jButtonJCEAlertOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonJCEAlertOKActionPerformed
        System.exit(254);
}//GEN-LAST:event_jButtonJCEAlertOKActionPerformed

    private void jCheckBoxCommentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxCommentActionPerformed
       prefs.set("useComment", !prefs.useComment);
       jTextPaneCommentField.setEditable(prefs.useComment);
       jTextPaneCommentField.setEnabled(prefs.useComment);
       // System.err.println("Comment = " + prefs.useComment);
}//GEN-LAST:event_jCheckBoxCommentActionPerformed

    private void jCheckBoxFinalizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxFinalizeActionPerformed
        finalize = !finalize;
        // System.err.println("Finalize: " + finalize);
    }//GEN-LAST:event_jCheckBoxFinalizeActionPerformed

    private void jButtonResetCommentFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResetCommentFieldActionPerformed
        String originalComment = 
                java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/Signatureblock_" + prefs.signLanguage)
                .getString("comment");
        prefs.set("SignComment", originalComment);
        jTextPaneCommentField.setText(originalComment);        
    }//GEN-LAST:event_jButtonResetCommentFieldActionPerformed

    private void jTextFieldErrorReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldErrorReportActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_jTextFieldErrorReportActionPerformed

    private void jButtonErrorReportOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonErrorReportOKActionPerformed
        jDialogErrorReport.setVisible(false);
    }//GEN-LAST:event_jButtonErrorReportOKActionPerformed

    private void jButtonErrorReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonErrorReportActionPerformed
        if (resultcolor.equals(colorerror)) {
            jDialogErrorReport.setVisible(true);
        } else {
            try {
                com.lowagie.tools.Executable.openDocument(jTextFieldOutputfile.getText());
            } catch (IOException ex) {
                System.err.println("Unable to start PDF reader");
            }
        }
    }//GEN-LAST:event_jButtonErrorReportActionPerformed

    private void jCheckBoxTooltipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxTooltipActionPerformed
        prefs.set("ToolTip", !prefs.toolTip);
        prefs.get();
        setTooltips(prefs.toolTip);

    }//GEN-LAST:event_jCheckBoxTooltipActionPerformed

    private void jTextFieldReasonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldReasonActionPerformed
        prefs.set("SignReason", jTextFieldReason.getText());
        prefs.get();
        // System.err.println("Reason: " + prefs.signReason);
}//GEN-LAST:event_jTextFieldReasonActionPerformed

    private void jTextFieldLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldLocationActionPerformed
        prefs.set("SignLocation", jTextFieldLocation.getText());
        prefs.get();
        // System.err.println("Location: " + prefs.signLocation);
}//GEN-LAST:event_jTextFieldLocationActionPerformed

    private void jButtonViewSourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonViewSourceActionPerformed
        try {
            com.lowagie.tools.Executable.openDocument(jTextFieldInputfile.getText());
            } catch (IOException ex) {
//            System.err.println("Unable to start PDF reader");
        }

    }//GEN-LAST:event_jButtonViewSourceActionPerformed

    private void jComboBoxSignatureLanguageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxSignatureLanguageActionPerformed

        String tempS = jComboBoxSignatureLanguage.getSelectedItem().toString();
        String tempLang = tempS.substring(tempS.indexOf("(") + 1,tempS.indexOf(")"));
//        System.out.println("Selected Languagecode: " + tempLang);
        prefs.set("SignLanguage", tempLang);
    }//GEN-LAST:event_jComboBoxSignatureLanguageActionPerformed

    private void jButtonViewOutputActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        try {
            com.lowagie.tools.Executable.openDocument(jTextFieldOutputfile.getText());
        } catch (IOException ex) {
            System.err.println("Unable to start PDF reader");
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
                        
            // check for Java JCE unrestricted!!!
            if (javax.crypto.Cipher.getMaxAllowedKeyLength("RC5") < java.lang.Integer.MAX_VALUE) {
                workingJCE = false;
            }

            mycommand = new SignCommandLine(args);
//            System.out.println("Main: Password:" + mycommand.ownerPwd.toString());

            if (mycommand.nogui) {
                if (!workingJCE) {
                    java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("JCEmissing");
                    System.exit(254);
                }
                new DoSignPDF(mycommand.input,
                        mycommand.output, 
                        mycommand.signature, 
                        mycommand.password, 
                        !mycommand.sigblock.equals(""), 
                        mycommand.sigblock, 
                        mycommand.sigimage, 
                        mycommand.finalize, 
                        mycommand.comment,
                        mycommand.reason,
                        mycommand.location,
                        mycommand.ownerPwd);
                System.exit(0);
            } else {
                java.awt.EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        new Main().setVisible(true);
                    }
                });
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButtonAbout;
    private javax.swing.JButton jButtonAboutOk;
    private javax.swing.JButton jButtonCancelMain;
    private javax.swing.JButton jButtonCancelNo;
    private javax.swing.JButton jButtonCancelYes;
    private javax.swing.JButton jButtonCertCancel;
    private javax.swing.JButton jButtonCertInfo;
    private javax.swing.JButton jButtonCertOK;
    private javax.swing.JButton jButtonErrorReport;
    private javax.swing.JButton jButtonErrorReportOK;
    private javax.swing.JButton jButtonInputfile;
    private javax.swing.JButton jButtonJCEAlertOK;
    private javax.swing.JButton jButtonLicense;
    private javax.swing.JButton jButtonLicenseOK;
    private javax.swing.JButton jButtonOption;
    private javax.swing.JButton jButtonOptionOK;
    private javax.swing.JButton jButtonOptionSearchLogo;
    private javax.swing.JButton jButtonOutputfile;
    private javax.swing.JButton jButtonPasswordOK;
    private javax.swing.JButton jButtonResetCommentField;
    private javax.swing.JButton jButtonResetLogo;
    private javax.swing.JButton jButtonSelectKeystoreCancel;
    private javax.swing.JButton jButtonSelectKeystoreFile;
    private javax.swing.JButton jButtonSelectKeystoreKeystore;
    private javax.swing.JButton jButtonSignaturefile;
    private javax.swing.JButton jButtonViewOutput;
    private javax.swing.JButton jButtonViewSource;
    private javax.swing.JCheckBox jCheckBoxComment;
    private javax.swing.JCheckBox jCheckBoxFinalize;
    private javax.swing.JCheckBox jCheckBoxSignatureBlock;
    private javax.swing.JCheckBoxMenuItem jCheckBoxTooltip;
    private javax.swing.JComboBox jComboBoxSignatureLanguage;
    private javax.swing.JDialog jDialogAbout;
    private javax.swing.JDialog jDialogCancel;
    private javax.swing.JDialog jDialogErrorReport;
    private javax.swing.JDialog jDialogJCEAlert;
    private javax.swing.JDialog jDialogLicense;
    private javax.swing.JEditorPane jEditorPaneHelp;
    private javax.swing.JFrame jFrameChooseCert;
    private javax.swing.JFrame jFrameHelp;
    private javax.swing.JFrame jFrameOption;
    private javax.swing.JFrame jFrameSelectKeystore;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabelAboutCopyright;
    private javax.swing.JLabel jLabelAboutText;
    private javax.swing.JLabel jLabelCancelQuestion;
    private javax.swing.JLabel jLabelErrorReportHeading;
    private javax.swing.JLabel jLabelFinishNext;
    private javax.swing.JLabel jLabelFinished;
    private javax.swing.JLabel jLabelInput;
    private javax.swing.JLabel jLabelJCEAlert;
    private javax.swing.JLabel jLabelLocation;
    private javax.swing.JLabel jLabelOptionLanguage;
    private javax.swing.JLabel jLabelOptionLogo;
    private javax.swing.JLabel jLabelOutput;
    private javax.swing.JLabel jLabelPassword;
    private javax.swing.JLabel jLabelReason;
    private javax.swing.JLabel jLabelRestart;
    private javax.swing.JLabel jLabelResult;
    private javax.swing.JLabel jLabelSign;
    private javax.swing.JLabel jLabelSignature;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JLabel jLabelWorking;
    private javax.swing.JList jListCerts;
    private javax.swing.JMenuBar jMenuBarMain;
    private javax.swing.JMenu jMenuEdit;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuHelp;
    private javax.swing.JMenuItem jMenuItemAbout;
    private javax.swing.JMenuItem jMenuItemHelp;
    private javax.swing.JMenuItem jMenuItemInputfile;
    private javax.swing.JMenuItem jMenuItemOptions;
    private javax.swing.JMenuItem jMenuItemOutputfile;
    private javax.swing.JMenuItem jMenuItemQuit;
    private javax.swing.JMenuItem jMenuItemSign;
    private javax.swing.JMenuItem jMenuItemSignaturefile;
    private javax.swing.JPanel jPanelCancel;
    private javax.swing.JPasswordField jPasswordFieldPassword;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPaneAboutVersion;
    private javax.swing.JScrollPane jScrollPaneCerts;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea jTextAreaAboutVersion;
    private javax.swing.JTextArea jTextAreaLicenseText;
    private javax.swing.JTextField jTextFieldErrorReport;
    private javax.swing.JTextField jTextFieldInputfile;
    private javax.swing.JTextField jTextFieldLocation;
    private javax.swing.JTextField jTextFieldOptionLogo;
    private javax.swing.JTextField jTextFieldOutputfile;
    private javax.swing.JTextField jTextFieldReason;
    private javax.swing.JTextField jTextFieldSignaturefile;
    private javax.swing.JTextPane jTextPaneCommentField;
    // End of variables declaration//GEN-END:variables

    
    private String generateOutputFile(String inputFile) {
        // generating filename by appending -sig to filename:
        // /i/am/here.pdf becomes /i/am/here-sig.pdf
        if (inputFile.equals("")) { return ""; }
        return inputFile.substring(0,inputFile.lastIndexOf(".pdf")) + "-sig.pdf";
    }
    
    private String getDir (String absFile) {
        // get Dir component from file as string
        if (absFile.equals("")) { return ""; }
        java.io.File myfile = new java.io.File(absFile);
        return myfile.getParent();
    }
    
    /** Opens dialog for user to choose an PDF file to open and read.
     *
     * @return PDF file or null if user cancelled the dialog
     */
    private String choosePDFFile() {
        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser(getDir(prefs.lastInputFile));
        chooser.setFileFilter(new PDFFilter());
        
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        }
        // cancel was clicked
        return null;
    }
    
    /** Save dialog for user to choose an PDF file to save.
     *
     * @return PDF file or null if user cancelled the dialog
     */
    private String choosePDFFileSave() {
        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser(getDir(jTextFieldOutputfile.getText()));
        chooser.setFileFilter(new PDFFilter());
        
        int returnVal = chooser.showSaveDialog(this);
        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        }
        // cancel was clicked
        return null;
    }
    
    /** Opens dialog for user to choose an P12 file to open and read.
     *
     * @return P12 file or null if user cancelled the dialog
     */
    private String chooseP12File() {
        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser(getDir(prefs.lastP12File));
        chooser.setFileFilter(new P12Filter());
        
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        }
        // cancel was clicked
        return null;
    }

    /** Opens dialog for user to choose an image file to open and read.
     *
     * @return image file or null if user cancelled the dialog
     */
    private String chooseImageFile() {
        String imagefile;
        if (jTextFieldOptionLogo.getText().equals("")) {
            imagefile = prefs.sigLogo; 
        } else {
            imagefile = jTextFieldOptionLogo.getText();
        }    
        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser(getDir(imagefile));
        chooser.setFileFilter(new ImageFilter());
        
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        }
        // cancel was clicked
        return null;
    }

    /** Filter which accepts only PDF files */
    private static class PDFFilter extends javax.swing.filechooser.FileFilter {
        public boolean accept(java.io.File f) {
            return f.isDirectory() || f.getName().endsWith(".pdf");
        }
        
        public String getDescription() {
            return java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("PDFDescription");
        }
    }
    
    /** Filter which accepts only P12 files */
    private static class P12Filter extends javax.swing.filechooser.FileFilter {
        public boolean accept(java.io.File f) {
            return f.isDirectory() || f.getName().endsWith(".p12") || f.getName().endsWith(".pfx");
        }
        
        public String getDescription() {
            return java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("PKCS12Description");
        }
    }

    private static class ImageFilter extends javax.swing.filechooser.FileFilter {
        public boolean accept(java.io.File f) {
            return f.isDirectory() || f.getName().endsWith(".gif") || 
                    f.getName().endsWith(".png") || f.getName().endsWith(".jpg");
        }
        
        public String getDescription() {
            return java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("ImageDescription");
        }
    }
    
    
    
    private static String getTooltip(String value) {
        return java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/ToolTips").getString(value);
    }
    
    private void setTooltips(boolean visible) {
        if (visible) {
            jLabelInput.setToolTipText(getTooltip("Inputfile"));
            jLabelInput.setCursor(questionCursor);
            jLabelOutput.setToolTipText(getTooltip("Outputfile"));
            jLabelOutput.setCursor(questionCursor);
            jLabelSignature.setToolTipText(getTooltip("Signaturefile"));
            jLabelSignature.setCursor(questionCursor);
            jCheckBoxSignatureBlock.setToolTipText(getTooltip("Signatureblock"));
            jCheckBoxSignatureBlock.setCursor(questionCursor);
            jLabelPassword.setToolTipText(getTooltip("Password"));
            jLabelPassword.setCursor(questionCursor);
            jLabelResult.setToolTipText(getTooltip("Result"));
            jLabelResult.setCursor(questionCursor);
            jLabelRestart.setToolTipText(getTooltip("Again"));
            jLabelRestart.setCursor(questionCursor);
            jCheckBoxFinalize.setToolTipText(getTooltip("Finalize"));
            jCheckBoxFinalize.setCursor(questionCursor);
            jLabelOptionLanguage.setToolTipText(getTooltip("SignatureLanguage"));
            jLabelOptionLanguage.setCursor(questionCursor);
            jLabelOptionLogo.setToolTipText(getTooltip("SignatureLogo"));
            jLabelOptionLogo.setCursor(questionCursor);
            jCheckBoxComment.setToolTipText(getTooltip("CommentField"));
            jCheckBoxComment.setCursor(questionCursor);
            jLabelReason.setToolTipText(getTooltip("SignatureReason"));
            jLabelReason.setCursor(questionCursor);
            jLabelLocation.setToolTipText(getTooltip("SignatureLocation"));
            jLabelLocation.setCursor(questionCursor);
        } else {
            jLabelInput.setToolTipText(null);
            jLabelInput.setCursor(defaultCursor);
            jLabelOutput.setToolTipText(null);
            jLabelOutput.setCursor(defaultCursor);
            jLabelSignature.setToolTipText(null);
            jLabelSignature.setCursor(defaultCursor);
            jCheckBoxSignatureBlock.setToolTipText(null);
            jCheckBoxSignatureBlock.setCursor(defaultCursor);
            jLabelPassword.setToolTipText(null);
            jLabelPassword.setCursor(defaultCursor);
            jLabelResult.setToolTipText(null);
            jLabelResult.setCursor(defaultCursor);
            jLabelRestart.setToolTipText(null);
            jLabelRestart.setCursor(defaultCursor);
            jCheckBoxFinalize.setToolTipText(null);
            jCheckBoxFinalize.setCursor(defaultCursor);
            jLabelOptionLanguage.setToolTipText(null);
            jLabelOptionLanguage.setCursor(defaultCursor);
            jLabelOptionLogo.setToolTipText(null);
            jLabelOptionLogo.setCursor(defaultCursor);
            jCheckBoxComment.setToolTipText(null);
            jCheckBoxComment.setCursor(defaultCursor);
            jLabelReason.setToolTipText(null);
            jLabelReason.setCursor(defaultCursor);
            jLabelLocation.setToolTipText(null);
            jLabelLocation.setCursor(defaultCursor);
 
        }
    }
    
    
    public static void setResult(String resultText, Boolean errorState, String errorString) {
        if (errorState) {
            resultcolor = colorerror;
            if (exceptionstring == null) {
                exceptionstring = errorString;
            }
            System.err.println(resultText  + "\n\t" + exceptionstring);
        } else {
            resultcolor = colorok;
            exceptionstring = null;
        }
        if (result == null) {
            result = resultText;
        }
    }
}

