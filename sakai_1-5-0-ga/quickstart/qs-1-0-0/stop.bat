@echo off
rem Stop the tomcat provided in the quickstart distribution of Sakai 1.0.

echo Stopping Tomcat

SET PWD=%CD%
set TOMCAT_HOME=
SET CATALINA_BASE=
SET CATALINA_HOME=
SET CATALINA_TMPDIR=
cd "%PWD%\jakarta-tomcat-5.0.28\bin\"
call "%PWD%\jakarta-tomcat-5.0.28\bin\shutdown"
        
cd "%PWD%"
rem $Header: /cvs/quickstart/stop.bat,v 1.3 2004/10/22 15:30:23 dlhaines.umich.edu Exp $