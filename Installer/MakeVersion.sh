#! /bin/sh
VERSION=1.7
SVNVERSION=`/usr/local/bin/svnversion . |  cut -d: -f2 | sed s/.$//g`
SRCPATH=dist/ship/
echo "Making Releases for:" $VERSION.$SVNVERSION
cd $SRCPATH
mv PortableSigner-Generic.zip PortableSigner-Generic-$VERSION.$SVNVERSION.zip
mv PortableSigner-Install.jar PortableSigner-Install-$VERSION.$SVNVERSION.jar
mv PortableSigner-Installer.exe PortableSigner-Installer-$VERSION.$SVNVERSION.exe
mv PortableSigner-MacOSX.tar.gz PortableSigner-MacOSX-$VERSION.$SVNVERSION.tar.gz
mv PortableSigner-Windows.zip PortableSigner-Windows-$VERSION.$SVNVERSION.zip
rm PortableSigner-MacOSX.tar