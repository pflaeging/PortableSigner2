#!/bin/sh
PROGDIR=/usr/local/PortableSigner/
IFS='
'
exec java -cp $PROGDIR -jar $PROGDIR/PortableSigner.jar $*
