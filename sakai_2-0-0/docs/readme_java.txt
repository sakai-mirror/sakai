
CHECKING TO SEE IF JAVA IS ALREADY INSTALLED

Check to see if you have Java installed on your system by running the following commands:

Windows:
C:\> echo %JAVA_HOME%
JAVA_HOME=C:\j2sdk1.4.2_04

C:\> java -version
Java(TM) 2 Runtime Environment, Standard Edition (build 1.4.2_04-141.3)
Java HotSpot(TM) Client VM (build 1.4.2-38, mixed mode)
                
Macintosh/Linux/Solaris:
                
$ echo $JAVA_HOME
/Library/Java/Home
$ java -version
java version "1.4.2_04"
Java(TM) 2 Runtime Environment, Standard Edition (build 1.4.2_04-141.3)
Java HotSpot(TM) Client VM (build 1.4.2-38, mixed mode)
$ 

INSTALLING JAVA

If Java is not installed, install the Java Run-Time Environment (JRE) or 
the Java Software Development Kit (SDK) from http://java.sun.com/j2se/

Note: Java is already installed on Mac OS/X computers.

Set the JAVA_HOME environment variable to point to the base directory of your Java 
installation. This will enable Tomcat to find the right Java installation 
automatically. This may already be setup for you by your Java SDK installation.

Windows example: 

Set the environment variable JAVA_HOME to "C:\j2sdk1.4.2_04" (do not include the quotes quotes)

Mac example: export JAVA_HOME=/Library/Java/Home

Linux example: export JAVA_HOME=/usr/local/java-current

You should also set the system path so as to include the Java commands

Windows example: 

Append the string ";C:\j2sdk1.4.2_04\bin" (include the semicolon but not the quotes) 
to the end of the System variable named Path.

Mac example: Not Necessary

Linux example: export PATH=$PATH:$JAVA_HOME/bin/

In the UNIX operating systems, you typically modify a startup file like ~/.bash_login 
to set and export these shell variables. In Windows-XP, you go to 
Start -> Control Panel -> System -> Advanced -> Environment Variables -> New 
create or modify the named variable.

In Windows XP, you need to start a new shell and use the set command to verify that 
the environment variables are set properly. In UNIX you also start a shell and use 
the set command to see the variables are set properly.

Once the variables are set properly, run the commands at the top of the page to 
make sure that Java is properly installed.

You can also look for the Sun Java Installation Instructions page at the Java web site.
