English (german scroll down)
=======

Start program with 

java -jar PortableSigner

The following additional libraries are provided and used:

    - itext (siehe auch http://itext.sourceforge.net)
    - BouncyCastle (siehe auch http://www.bouncycastle.org)
    - Swing-layout (siehe auch http://layout.jdesktop.org)

For the use of Bouncycastle the Java Strong export Security must be used.
(Look at http://www.bouncycastle.org/documentation.html):

"
   3. If you are using JDK 1.4, or later, you must use the signed jar for 
    the provider and you must download the unrestricted policy files for 
    the Sun JCE if you want the provider to work properly. The policy files 
    can be found at the same place as the JDK download. Further information 
    on this can be found in the Sun documentation on the JCE. If you have not 
    installed the policy files you will see something like:

java.lang.SecurityException: Unsupported keysize or algorithm parameters
		at javax.crypto.Cipher.init(DashoA6275)
"

The new JCE Policy can be found 
http://java.sun.com/javase/downloads/index.jsp
at the end of the page:
"Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy "

The Windows installer is installing all required patches automagic.
If you'r upgrading your Java runtime, please reinstall PortableSigner to apply
the security policies again.

:peter pflaeging <pfp@adv.magwien.gv.at>



Deutsch
=======

Das vorliegende Programm laesst sich in einer Java 1.5.0 Umgebung mit

java -jar PortableSigner.jar starten.

Es verwendet folgende zusaetzliche Klassenbibliotheken:

    - itext (siehe auch http://itext.sourceforge.net)
    - BouncyCastle (siehe auch http://www.bouncycastle.org)
    - Swing-layout (siehe auch http://layout.jdesktop.org)

Fuer die Verwendung von Bouncycastle muss das Schema fuer Strong Export Security
in der Java Runtime installiert sein (siehe auch Zitat 
von http://www.bouncycastle.org/documentation.html): 

"
   3. If you are using JDK 1.4, or later, you must use the signed jar for 
    the provider and you must download the unrestricted policy files for 
    the Sun JCE if you want the provider to work properly. The policy files 
    can be found at the same place as the JDK download. Further information 
    on this can be found in the Sun documentation on the JCE. If you have not 
    installed the policy files you will see something like:

java.lang.SecurityException: Unsupported keysize or algorithm parameters
		at javax.crypto.Cipher.init(DashoA6275)

"

Die neue JCE Policy ist zu finden unter 
http://java.sun.com/javase/downloads/index.jsp
gegen Ende der Seite als:
"Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy "

Der Windows Installer installiert die Security Policy automatisch mit.
Sollten Sie Ihre Java Runtime updaten oder neu installieren, bitte den PortableSigner 
noch einmal installieren, damit die Security Policy auch dort wirkt.

:peter pflaeging <peter.pflaeging@wien.gv.at>

