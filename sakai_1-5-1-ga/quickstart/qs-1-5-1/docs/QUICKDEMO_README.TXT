2005/05/01


Sakai 1.5.1. QuickDemo

This is a distribution of Sakai 1.5.1 designed to allow someone to
have a demonstation installation in a few minutes.  The Sakai QuickDemo is
distributed as a pre-configured Tomcat installation. In addition to
the core Sakai framework and tools it contains the Syllabus, "Test and
Quiz" (Samigo), and Presentation tools.  

The primary differences between the 1.5.0 Quickstart release and the
1.5.1 QuickDemo distribution is a variety of bug fixes and the
inclusion of a preconfigured version of Test and Quiz.

Note that the QuickDemo only supports the HSQLDB database system and
is not designed to be a production installation of Sakai.  For
production purposes Sakai supports MySQL and Oracle, but those
versions need to be built and configured by the local installer.  For
more information see the QuickSource distribution.

WHAT DOES QuickDemo REQUIRE?

For HSQLDB the distribution is self contained (except for Java). The
required Ant and Tomcat tools are provided in the distribution.  You
will need to have a previously installed Java JRE or SDK
environment. It requires Java 1.4.2 or better.


QuickDemo INSTALLATION

The installation steps are:
- Download the distribution zip file.
- Unzip the zip file.
- Go into the sakai directory.

The remaining steps vary a bit depending on the host OS.

WINDOWS:
- Run start.bat.  This does some configuration and starts Tomcat.
- Sakai will be available on localhost:8080.
- Run stop.bat to shut down Tomcat and stop Sakai.

UNIX:
- Run 'sh start.sh'.  This will set permissions on other scripts, setup some 
environment variables and start the Sakai tomcat.  After the first execution the
correct permissions will be set on the scripts and this can be invoked simply as 'start.sh'
- Sakai will be available on localhost:8080.
- Run stop.sh to shut down Tomcat and stop Sakai.


If something goes wrong, look at the tomcat logs in the tomcat/logs directory and the 
Sakai logs in localfiles/logs.


$Header: /cvs/quickstart/qs-1-5-1/docs/QUICKDEMO_README.TXT,v 1.4 2005/06/02 20:30:30 dlhaines.umich.edu Exp $
 
