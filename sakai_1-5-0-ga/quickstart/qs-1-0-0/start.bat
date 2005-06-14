@echo off
echo Note: you must insure that java 1.4.2+ is properly installed.
echo Check which version you have by running "java -version".
echo You must also have the environment variable "JAVA_HOME".

echo Configuring Sakai Quick Start...

if NOT DEFINED JAVA_HOME (
   echo JAVA_HOME Variable not defined
   exit/B
)

rem Use the ant that we supply.
SET ANT_HOME=%cd%\apache-ant-1.6.2

rem Make sure that Ant is on the path, but don't put it on again if 
rem you know it already is there.
if NOT DEFINED SAKAI_PATH_SET (
  SET PATH=%ANT_HOME%\bin;%PATH%
  SET SAKAI_PATH_SET=YES
)

echo path: %PATH%

SET PWD=%CD%

rem Run the customization script.
cd install
CALL ant -f customize.xml %1 -Dwork.rootdir="%PWD%"

echo Starting Tomcat
SET TOMCAT_HOME=
SET CATALINA_HOME=
SET CATALINA_BASE=
SET CATALINA_TMPDIR=
cd "%PWD%\jakarta-tomcat-5.0.28\bin"
call "%PWD%\jakarta-tomcat-5.0.28\bin\startup.bat"
   
cd "%PWD%"

rem $Header: /cvs/quickstart/start.bat,v 1.5 2004/10/22 18:49:46 dlhaines.umich.edu Exp $
