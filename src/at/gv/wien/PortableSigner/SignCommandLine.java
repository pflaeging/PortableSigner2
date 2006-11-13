/*
 * CommandLine.java
 *
 * Created on 25. Oktober 2006, 10:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package at.gv.wien.PortableSigner;

import org.apache.commons.cli.*;


/**
 *
 * @author pfp
 */
public class SignCommandLine {
    public String input = "", output = "", signature = "", password = "";
    public Boolean nogui = false;
    private Boolean help = false;
    
    /** Creates a new instance of CommandLine */
    public SignCommandLine(String args[]) {
       CommandLine cmd;
       Options options = new Options();
       options.addOption("t", true, "Eingabedatei (PDF)");
       options.addOption("o", true, "Ausgabedatei (PDF)");
       options.addOption("s", true, "Signaturdatei (P12)");
       options.addOption("p", true, "Signaturpasswort");
       options.addOption("n", false, "Ohne GUI");
       options.addOption("h", false, "Hilfe (diese Seite)");
       CommandLineParser parser = new PosixParser();
       HelpFormatter usage = new HelpFormatter();
       try {
            cmd = parser.parse(options, args);
            input = cmd.getOptionValue("t"); 
            output = cmd.getOptionValue("o");
            signature = cmd.getOptionValue("s");
            password = cmd.getOptionValue("p");
            nogui = cmd.hasOption("n");
            help = cmd.hasOption("h");
            if (cmd.getArgs().length != 0) {
                throw new ParseException("Unknown Arguments");
            }
       } catch(ParseException e) {
           System.err.println("Falsche Kommandozeilenparameter");
           usage.printHelp("PortableSigner", options);
           System.exit(3);
       }
       if (nogui) {
           if (input == null || output == null || signature == null ) {
               System.err.println("Fehlende Argumente -t, -o oder -s");
               usage.printHelp("PortableSigner", options);
               System.exit(2);
           }
       }
       if (help) {
           usage.printHelp("PortableSigner", options);
           System.exit(1);
       }
    }
}
