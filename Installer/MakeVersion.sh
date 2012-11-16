#! /bin/sh
VERSION=2.0
GITVERSION=`git log --pretty="%h" -1`
SRCPATH=dist/ship/
echo "Making Releases for:" $VERSION.$GITVERSION
cd $SRCPATH
mv PortableSigner-Generic.zip PortableSigner-Generic-$VERSION.$GITVERSION.zip
mv PortableSigner-Install.jar PortableSigner-Install-$VERSION.$GITVERSION.jar
mv PortableSigner-Installer.exe PortableSigner-Installer-$VERSION.$GITVERSION.exe
mv PortableSigner-MacOSX.tar.gz PortableSigner-MacOSX-$VERSION.$GITVERSION.tar.gz
mv PortableSigner-Windows.zip PortableSigner-Windows-$VERSION.$GITVERSION.zip
rm PortableSigner-MacOSX.tar