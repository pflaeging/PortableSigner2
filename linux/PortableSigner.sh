#!/bin/sh
PROGDIR=/usr/local/PortableSigner/
exec java -cp $PROGDIR -jar $PROGDIR/PortableSigner.jar "$*"
