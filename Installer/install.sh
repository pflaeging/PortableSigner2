#! /bin/sh
INSTALLER=/Applications/IzPack/bin/compile 
INSTALLER_ROOT=/Applications/IzPack/
XMLFILE=./Installer/install.xml
JARFILE=./dist/ship/PortableSigner-Install.jar
$INSTALLER $XMLFILE -h $INSTALLER_ROOT -o $JARFILE
