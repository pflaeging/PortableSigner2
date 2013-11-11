#! /bin/sh

VERSIONPROPERTY=src/net/pflaeging/PortableSigner/Version.properties
touch $VERSIONPROPERTY
git log --pretty="GitVersion=%h %d" -1 > $VERSIONPROPERTY
if [ $? -ne 0 ]; then echo "Date=2013-01-01" > $VERSIONPROPERTY; fi
git log --pretty="Committer=%cn <%ce>" -1 >> $VERSIONPROPERTY
if [ $? -ne 0 ]; then echo "Committer=a" >> $VERSIONPROPERTY; fi
git log --pretty="Date=%cd" -1 >> $VERSIONPROPERTY
if [ $? -ne 0 ]; then echo "GitVersion=a" >> $VERSIONPROPERTY; fi

exit