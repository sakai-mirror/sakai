BINARY REQUIREMENTS

A successful binary installation requires the support of the correct Java and Tomcat installations.  Steps for ensuring this are detailed here.


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


INSTALL TOMCAT

The latest stable version of Tomcat 5.5 (currently 5.5.12) can be downloaded as a binary from:
http://tomcat.apache.org/download-55.cgi
The distribution you want is the one labeled Core, along with the JDK 1.4 Compatability Package.

Choose a location to install Tomcat, and unpack both the Tomcat binary and the compatibility package there in the same location. The compatibility package will simply overlay your Tomcat directories with the appropriate files. From this point forward these instructions will refer to the top-level Tomcat directory (e.g. apache-tomcat-5.5.12) as $CATALINA_HOME. You may set this as an environment variable for convenience's sake, but this is not required.  Make sure that you have write permissions to the Tomcat files and directories before proceeding, or you may later run into errors during the build phase.


CONFIGURE TOMCAT

Sakai supports UTF-8, allowing for non-Roman characters, but this requires that Tomcat be configured to accept UTF-8 URLs, since it ships with ISO-8859-1 as the default URL encoding. To change this setting, edit $CATALINA_HOME/conf/server.xml. Add the attribute URIEncoding="UTF-8" to the <connector> element. For example:

<Connector
port="8080" maxThreads="150" minSpareThreads="25" maxSpareThreads="75"
enableLookups="false" redirectPort="8443" acceptCount="100"
debug="0" connectionTimeout="20000" disableUploadTimeout="true"
URIEncoding="UTF-8"/>

If you want to run Tomcat on different ports than the defaults, this would also be a good time to make those changes in the server.xml file. See Tomcat configuration documentation for more details.

If you're going to run Tomcat in isolation (i.e. if you're not going to connect it to Apache) then you'll want to make a further minor Tomcat change that may spare some confusion later. In order to make sure that entering the URL to your server will redirect to the Sakai application seamlessly, you'll need to copy an index.html file to webapps/ROOT. The ROOT webapp is the one served up when a request is made to your web server's root URL. The index.html file to add to webapps/ROOT simply redirects browsers to the full URL of the gateway page, and it should look something like:

<html>
<head>
  <meta http-equiv="refresh" content="0;url=/portal">
</head>
<body>
  redirecting to /portal ...
</body>
</html>

If you don't make this change you'll need to append '/portal' to the URL you enter to access Sakai each time. If you are going to connect Tomcat with Apache, you can handle this as a matter of Apache configuration, which is however outside the scope of this document.