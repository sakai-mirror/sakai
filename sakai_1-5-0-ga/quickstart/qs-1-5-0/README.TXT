2005/02/25

Sakai quickstart 1.5

This is a quickstart version of Sakai 1.5.  The Sakai quickstart is distributed as a 
pre-configured Tomcat installation. 

WHAT DOES IT NEED?
It is self contained (except for Java) and 
the required Ant and Tomcat systems are provided.  

You will need to have a 
previously installed Java JRE or SDK environment. It requires Java 1.4.2 or better.

The installation steps are:
- Download the zip file with the distribution.
- Unzip the zip file.
- Go to the sakai directory.

WINDOWS:
- Run start.bat.  This does some configuration and starts Tomcat.
- Sakai will be available on localhost:8080.
- Run stop.bat to shut down Tomcat and stop Sakai.

UNIX:
- Change the permission on the shell scripts to allow execution.
- Run start.sh.  This will set permissions on other scripts, setup some 
environment variables and start the Sakai tomcat.  
- Sakai will be available on localhost:8080.
- Run stop.sh to shut down Tomcat and stop Sakai.


If something goes wrong, look at the tomcat logs in the tomcat/logs directory and the 
Sakai logs in localfiles/logs.

$Header: /cvs/quickstart/qs-1-5-0/README.TXT,v 1.2 2005/03/27 02:12:28 dlhaines.umich.edu Exp $
 
