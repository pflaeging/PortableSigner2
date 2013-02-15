PortableSigner2
===============

PortableSigner - A Commandline and GUI Tool to digital sign PDF files with X.509 certificates

------
English (german scroll down)
=======

Start program with 

java -jar PortableSigner

There's a minimal installer in the package PortableSigner-Generic-x.x.xxx.zip
in the directory linux/.

There's a full blown installer as PortableSigner-Installer-x.x.xxx.exe.

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

If you're upgrading your Java runtime, please reinstall the policies to apply
the security policies again.

:peter pflaeging <peter@pflaeging.net>



Deutsch
=======

Das vorliegende Programm laesst sich in einer Java 1.5.0 Umgebung mit

java -jar PortableSigner.jar starten.

Es gibt minimalen Installationssupport fuer Linux im Paket
PortableSigner-Generic-x.x.xxx.zip im Verziechnis linux/.

Es gibt einen kompletten Installer fuer Windows als 
Paket: PortableSigner-Installer-x.x.xxx.exe.

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

Sollten Sie Ihre Java Runtime updaten oder neu installieren, bitte die Policy 
noch einmal installieren, damit die Security Policy auch dort wirkt.

:peter pflaeging <peter@pflaeging.net>

