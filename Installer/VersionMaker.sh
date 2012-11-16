#! /bin/sh

VERSIONPROPERTY=src/net/pflaeging/PortableSigner/Version.properties
touch $VERSIONPROPERTY
git log --pretty="GitVersion=%h %d" -1 > $VERSIONPROPERTY
git log --pretty="Committer=%cn <%ce>" -1 >> $VERSIONPROPERTY
git log --pretty="Date=%cd" -1 >> $VERSIONPROPERTY
exit