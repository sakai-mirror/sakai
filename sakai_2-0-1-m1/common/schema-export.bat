@echo off

rem -------------------------------------------------------------------
rem Execute SchemaExport tool
rem -------------------------------------------------------------------

rem set HIBERNATE_DIALECT=net.sf.hibernate.dialect.HSQLDialect
rem set HIBERNATE_DIALECT=net.sf.hibernate.dialect.MySQLDialect
set HIBERNATE_DIALECT=net.sf.hibernate.dialect.Oracle9Dialect

set MAVEN_TARGET=C:\java\projects\common\common-composite-component-data\target\classes
set MAVEN_SRC=C:\java\projects\common\foo

set HIBERNATE_HOME=C:\java\lib\hibernate-2.1.8
set LIB=%HIBERNATE_HOME%\lib
set PROPS=%HIBERNATE_HOME%\src
set CP=%MAVEN_TARGET%;%PROPS%;%HIBERNATE_HOME%\hibernate2.jar;%LIB%\commons-logging-1.0.4.jar;%LIB%\commons-collections-2.1.1.jar;%LIB%\commons-lang-1.0.1.jar;%LIB%\cglib-full-2.0.2.jar;%LIB%\dom4j-1.4.jar;%LIB%\odmg-3.0.jar;%LIB%\xml-apis.jar;%LIB%\xerces-2.4.0.jar;%LIB%\xalan-2.4.0.jar
set CP=%CP%;C:\java\projects\common\agent-api\target\classes;C:\java\projects\common\manager-api\target\classes;C:\java\projects\common\type-api\target\classes;C:\java\projects\common\superstructure-api\target\classes;C:\java\projects\common\authorization-api\target\classes;C:\java\projects\common\edu-person-api\target\classes

java -cp %CP% -Dhibernate.dialect=%HIBERNATE_DIALECT% net.sf.hibernate.tool.hbm2ddl.SchemaExport --text --format --output=%HIBERNATE_DIALECT%-ddl.sql %MAVEN_SRC%\*.hbm.xml