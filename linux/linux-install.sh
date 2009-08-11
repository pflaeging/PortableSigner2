#!/bin/sh
# <peter@pflaeging.net> Linux like install, until I can provide DEB or RPM's
# where are the progs and libraries?
INSTPATH=/usr/local/PortableSigner
# copy the programs in place
mkdir $INSTPATH
cp -r lib linux/* $INSTPATH
cp PortableSigner.jar $INSTPATH
# make a program in your path
ln -s $INSTPATH/PortableSigner.sh /usr/local/bin/PortableSigner
# place a menu under applications/office
ln -s $INSTPATH/portablesigner.desktop /usr/share/applications
# thats all
exit 0
