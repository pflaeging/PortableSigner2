Sorry, I've got no install package for Linux right now.

Meanwhile you get an install script.
All you have to do is the following:

1. Install a suitable Java Runtime (example here is Ubuntu 9.04):
    $ sudo apt-get install sun-java6-bin sun-java6-jre tzdata-java
2. unpack PortableSigner-Generic-x.x.xxx.zip
    $ unzip PortableSigner-Generic-x.xxx.zip
    $ cd PortableSigner-Generic-x.x.xxx
3. Install with install script:
    $ sudo sh ./linux-install.sh
4. Ready!


The best would be to have a RPM and DEB with the right dependencies.


At the moment, I can give a small support how to run the package under
Ubuntu 9.04. It should be very similar on all other flavors of Linux or
BSD.

What you need:
1. A Java Runtime and on older releases the JCE unrestricted Security policy.
2. The PortableSigner package ;-)

1. Java JRE package.
    You should install the following Java related Ubuntu 9.04 Desktop:
    # apt-get install sun-java6-bin sun-java6-jre tzdata-java
2. I made an install script "linux-install.sh" which does simply the following:
    make dir /usr/local/PortableSigner
    copy program, lib and needed files in this dir
    symbolic link to /usr/local/bin/PortableSigner (the commandline client!)
    symbolic link to /usr/share/applications/portablesigner.desktop
        (now the program is in the application menu cat. "Office"
