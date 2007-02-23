#! /bin/sh
INSTALLER=/Developer/Applications/IzPack/bin/compile 
INSTALLER_ROOT=/Developer/Applications/IzPack/
XMLFILE=./Installer/install.xml
JARFILE=./dist/ship/PortableSigner-Install.jar
$INSTALLER $XMLFILE -h $INSTALLER_ROOT -o $JARFILE
