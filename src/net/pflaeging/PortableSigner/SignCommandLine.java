/*
 * CommandLine.java
 *
 * Created on 25. Oktober 2006, 10:03
 * This File is part of PortableSigner (http://portablesigner.sf.net/)
 *  and is under the European Public License V1.1 (http://www.osor.eu/eupl)
 * (c) Peter Pfläging <peter@pflaeging.net>
 */
package net.pflaeging.PortableSigner;

import java.io.Console;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.ResourceBundle;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 *
 * @author pfp
 */
public class SignCommandLine {

    public String input = "",  output = "",  signature = "",  password = "",
            sigblock = "",  sigimage = "",  comment = "",  reason = "",
            location = "",  pwdFile = "",  ownerPwdFile = "",
            ownerPwdString = "";
    private String embedParams = "";
    public byte[] ownerPwd = null;
    public Boolean nogui = false,  finalize = true, noSigPage = false, lastPage = false;
    private Boolean help = false;
    public float vPos = 0f, lMargin = 0f, rMargin = 0f;
    String langcodes;
	private static final ResourceBundle rbi18n
		= ResourceBundle.getBundle("net/pflaeging/PortableSigner/i18n");

    /** Creates a new instance of CommandLine */
    public SignCommandLine(String args[]) {
        langcodes = "";
        java.util.Enumeration<String> langCodes =
                ResourceBundle.getBundle("net/pflaeging/PortableSigner/SignatureblockLanguages").getKeys();

        while ( langCodes.hasMoreElements() )  {
            langcodes = langcodes + langCodes.nextElement() + "|";
        }
        langcodes = langcodes.substring(0, langcodes.length()-1);
//        System.out.println("Langcodes: " + langcodes);

        CommandLine cmd;
        Options options = new Options();
        options.addOption("t", true,
                rbi18n.getString("CLI-InputFile"));
        options.addOption("o", true,
                rbi18n.getString("CLI-OutputFile"));
        options.addOption("s", true,
                rbi18n.getString("CLI-SignatureFile"));
        options.addOption("p", true,
                rbi18n.getString("CLI-Password"));
        options.addOption("n", false,
                rbi18n.getString("CLI-WithoutGUI"));
        options.addOption("f", false,
                rbi18n.getString("CLI-Finalize"));
        options.addOption("h", false,
                rbi18n.getString("CLI-Help"));
        options.addOption("b", true,
                rbi18n.getString("CLI-SigBlock")
                + langcodes);
        options.addOption("i", true,
                rbi18n.getString("CLI-SigImage"));
        options.addOption("c", true,
                rbi18n.getString("CLI-SigComment"));
        options.addOption("r", true,
                rbi18n.getString("CLI-SigReason"));
        options.addOption("l", true,
                rbi18n.getString("CLI-SigLocation"));
        options.addOption("e", true,
                rbi18n.getString("CLI-EmbedSignature"));
        options.addOption("pwdfile", true,
                rbi18n.getString("CLI-PasswdFile"));
        options.addOption("ownerpwd", true,
                rbi18n.getString("CLI-OwnerPasswd"));
        options.addOption("ownerpwdfile", true,
                rbi18n.getString("CLI-OwnerPasswdFile"));
        options.addOption("z", false,
                rbi18n.getString("CLI-LastPage"));

        CommandLineParser parser = new PosixParser();
        HelpFormatter usage = new HelpFormatter();
        try {
            cmd = parser.parse(options, args);
            input = cmd.getOptionValue("t", "");
            output = cmd.getOptionValue("o", "");
            signature = cmd.getOptionValue("s", "");
            password = cmd.getOptionValue("p", "");
            nogui = cmd.hasOption("n");
            help = cmd.hasOption("h");
            finalize = !cmd.hasOption("f");
            sigblock = cmd.getOptionValue("b", "");
            sigimage = cmd.getOptionValue("i", "");
            comment = cmd.getOptionValue("c", "");
            reason = cmd.getOptionValue("r", "");
            location = cmd.getOptionValue("l", "");
            embedParams = cmd.getOptionValue("e", "");
            pwdFile = cmd.getOptionValue("pwdfile", "");
            ownerPwdString = cmd.getOptionValue("ownerpwd", "");
            ownerPwdFile = cmd.getOptionValue("ownerpwdfile", "");
            lastPage = !cmd.hasOption("z");

            if (cmd.getArgs().length != 0) {
                throw new ParseException(rbi18n
                        .getString("CLI-UnknownArguments"));
            }
        } catch (ParseException e) {
            System.err.println(rbi18n
                    .getString("CLI-WrongArguments"));
            usage.printHelp("PortableSigner", options);
            System.exit(3);
        }
        if (nogui) {
            if (input.equals("") || output.equals("") || signature.equals("")) {
                System.err.println(rbi18n
                        .getString("CLI-MissingArguments"));
                usage.printHelp("PortableSigner", options);
                System.exit(2);
            }
	    if (! help) {
            if (password.equals("")) {
                // password missing
                if (!pwdFile.equals("")) {
                    // read the password from the given file
                    try {
                        FileInputStream pwdfis = new FileInputStream(pwdFile);
                        byte[] pwd = new byte[1024];
                        password = "";
                        try {
                            do {
                                int r = pwdfis.read(pwd);
                                if (r < 0) {
                                    break;
                                }
                                password += new String(pwd);
                                password = password.trim();
                            } while (pwdfis.available() > 0);
                            pwdfis.close();
					} catch (IOException ex) {
                        }
				} catch (FileNotFoundException fnfex) {
                    }
                } else {
                    // no password file given, read from standard input
				System.out.print(rbi18n
                            .getString("CLI-MissingPassword"));
				Console con = System.console ();
				if (con == null) {
                    byte[] pwd = new byte[1024];
                    password = "";
                    try {
                        do {
                            int r = System.in.read(pwd);
                            if (r < 0) {
                                break;
                            }
                            password += new String(pwd);
                            password = password.trim();
                        } while (System.in.available() > 0);
					} catch (IOException ex) {
					}
				} else {
					// Console not null. Use it to read the password without echo
					char[] pwd = con.readPassword ();
					if (pwd != null) {
						password = new String (pwd);
					}
				}
			}
		}
		if (ownerPwdString.equals("") && ownerPwdFile.equals("")) {
			// no owner password or owner password file given, read from standard input
			System.out.print(rbi18n.getString("CLI-MissingOwnerPassword") + " ");
			Console con = System.console ();
			if (con == null) {
				byte[] pwd = new byte[1024];
				String tmppassword = "";
				try {
					do {
						int r = System.in.read(pwd);
						if (r < 0) {
							break;
						}
						tmppassword += new String(pwd, 0, r);
						tmppassword = tmppassword.trim();
					} while (System.in.available() > 0);
                    } catch (java.io.IOException ex) {
					// TODO: perhaps notify the user
				}
				ownerPwd = tmppassword.getBytes ();
			}
			else {
				// Console not null. Use it to read the password without echo
				char[] pwd = con.readPassword ();
				if (pwd != null) {
					ownerPwd = new byte[pwd.length];
					for (int i=0; i < pwd.length; i++ ) {
						ownerPwd[i] = (byte) pwd[i];
                    }
                }
            }
            } else if (!ownerPwdString.equals("")) {
                ownerPwd = ownerPwdString.getBytes();
            } else if (!ownerPwdFile.equals("")) {
                try {
                    FileInputStream pwdfis = new FileInputStream(ownerPwdFile);
                    ownerPwd = new byte[0];
                    byte[] tmp = new byte[1024];
                    byte[] full;
                    try {
                        do {
                            int r = pwdfis.read(tmp);
                            if (r < 0) {
                                break;
                            }
						// trim the length:
						tmp = Arrays.copyOfRange(tmp, 0, r);
						//System.arraycopy(tmp, 0, tmp, 0, r);
                            full = new byte[ownerPwd.length + tmp.length];
                            System.arraycopy(ownerPwd, 0, full, 0, ownerPwd.length);
                            System.arraycopy(tmp, 0, full, ownerPwd.length, tmp.length);
                            ownerPwd = full;
                        } while (pwdfis.available() > 0);
                        pwdfis.close();
				} catch (IOException ex) {
				}
			} catch (FileNotFoundException fnfex) {
                    }
                }
            }

        }
        if (!embedParams.equals("")) {
            String [] parameter = null;
            parameter = embedParams.split(",");
             try {
                Float vPosF = new Float(parameter[0]),
                lMarginF = new Float(parameter[1]),
                rMarginF = new Float(parameter[2]);
                vPos = vPosF.floatValue();
                lMargin = lMarginF.floatValue();
                rMargin = rMarginF.floatValue();
                noSigPage = true;
            } catch (NumberFormatException nfe)
            {
               System.err.println(rbi18n
                        .getString("CLI-embedParameter-Error"));
                usage.printHelp("PortableSigner", options);
                System.exit(5);
             }
        }
        if (!(langcodes.contains(sigblock)|| sigblock.equals(""))) {
            System.err.println(rbi18n
                    .getString("CLI-Only-german-english") + langcodes);
            usage.printHelp("PortableSigner", options);
            System.exit(4);
        }
        if (help) {
            usage.printHelp("PortableSigner", options);
            System.exit(1);
        }
//        System.err.println("CMDline: input: " + input);
//        System.err.println("CMDline: output: " + output);
//        System.err.println("CMDline: signature: " + signature);
//        System.err.println("CMDline: password: " + password);
//        System.err.println("CMDline: sigblock: " + sigblock);
//        System.err.println("CMDline: sigimage: " + sigimage);
//        System.err.println("CMDline: comment: " + comment);
//        System.err.println("CMDline: reason: " + reason);
//        System.err.println("CMDline: location: " + location);
//        System.err.println("CMDline: pwdFile: " + pwdFile);
//        System.err.println("CMDline: ownerPwdFile: " + ownerPwdFile);
//        System.err.println("CMDline: ownerPwdString: " + ownerPwdString);
//        System.err.println("CMDline: ownerPwd: " + ownerPwd.toString());
    }
}
