CruiseControl configuration
Continuous Build System for Sakai

These CruiseControl configuration files implement a 
continuous build system for Sakai.  CruiseControl helps to 
quickly detect broken code.  It will build
Sakai from source whenever devlopers check in code into SVN,
and thus detect code that breaks the build.  It will send out
email notifications if the build is broken.

CruiseControl should be installed on a machine that is NOT
used for other development.  In general it should not be installed 
on a machine that is being used by developers to compile
and run Sakai, as it will not play nice (it needs its own
Maven repository all to itself).

To install:

1. Install the prerequisites for CruiseControl:
* Java 1.4.X (make sure the environment variable JAVA_HOME is set)
	http://java.sun.com/j2se/1.4.2/download.html
* Maven 1.0.X (known to work with Maven 1.0.2, NOT 2.0+)
	http://maven.apache.org/
* CruiseControl binary release (known to work with 2.3.0.1)
	http://cruisecontrol.sourceforge.net/

2. Copy all these configuration files into the CruiseControl directory.

3. Edit the configuration files by hand to be correct for your local system.
 * config.xml - Set mavenscript attribute to the full path to Maven's
     startup script on your machine ("maven.bat" on Windows, "maven" otherwise)
 * config.xml - Set all attributes of <email> tag to the appropriate values
     for your system.  Make sure the host running CruiseControl has its 
     server name here, and also make sure the mailhost, returnaddress, 
     returnname, etc, are all what you want.  Set the address attribute to 
     the email address of the person that will be notified about the build.



4. Now start up CruiseControl using the startup script.  "cruisecontrol.bat"
on Windows, "cruisecontrol.sh" on other.
