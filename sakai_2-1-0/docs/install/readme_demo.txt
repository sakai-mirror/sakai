DEMO REQUIREMENTS

A successful demo installation requires the support of the correct Java installation.  Steps for ensuring this are detailed here.


CHECK JAVA VERSION

Check to see if you have Java 1.4.2 (1.4.2 is required; Java 1.5 will not work) installed on your system by running the following command:

Windows: 	

	C:\> java -version
	Java(TM) 2 Runtime Environment, Standard Edition 
	(build 1.4.2_04-141.3)
	Java HotSpot(TM) Client VM (build 1.4.2-38, mixed mode)

*nix variants: 	

	$ java -version
	java version "1.4.2_04"
	Java(TM) 2 Runtime Environment, Standard Edition 
	(build 1.4.2_04-141.3)
	Java HotSpot(TM) Client VM (build 1.4.2-38, mixed mode)

If you do not have the correct version of Java installed, install the Java Software Development Kit (SDK) from http://java.sun.com/j2se/1.4.2/download.html 


SET ENVIRONMENT VARIABLES

Set the JAVA_HOME environment variable to point to the base directory of your Java installation. This will enable Tomcat to find the right Java installation automatically. This may already be set up for you by your Java SDK installation, but it should be double-checked.

In the UNIX operating systems, you typically modify a startup file like ~/.bash_login to set and export these shell variables. In Windows XP, you go to

Start -> Control Panel -> System -> Advanced -> Environment Variables

And then set them as described below:

Windows: 	Set the environment variable JAVA_HOME to "C:\j2sdk1.4.2_04" (do not include the quotes)
Mac: 	export JAVA_HOME=/Library/Java/Home
Linux: 	export JAVA_HOME=/usr/local/java-current

Next you'll want to extend the PATH variable so as to include the Java commands.

Windows: 	Append the string ";C:\j2sdk1.4.2_04\bin" (include the semicolon but not the quotes) to the end of the system variable named Path.
Mac: 	not necessary
*nix: 	export PATH=$PATH:$JAVA_HOME/bin/

You should test that these variables are set correctly.  In both Windows XP and *nix operating systems you can simply start a new shell and type the 'set' command to see your environment variables. You may run the java -version command once more (see above) as a final check.

You can also look for the Sun Java Installation Instructions page at the Java web site for further details.