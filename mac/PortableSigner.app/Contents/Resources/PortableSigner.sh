#! /bin/sh
INSTPATH=/Applications/PortableSigner.app

cd $INSTPATH/Contents/MacOS
exec ./JavaApplicationStub $*
