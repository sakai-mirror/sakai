@echo off
echo This script will _regenerate_ an empty Sakai database for 
echo HSQLDB from the DDL in the current directory.
echo/
pause

del sakaidb.*
start java -cp %CATALINA_HOME%\common\lib\hsqldb-1.7.2.2.jar org.hsqldb.Server 
echo Press any key to continue after the HSQLDB server has started up.
pause
java -jar %CATALINA_HOME%\common\lib\hsqldb-1.7.2.2.jar --rcfile sqltool.rc sakaidb all.sql

echo Now copy the newly regenerated HSQLDB database into \usr\local\sakai\db.
echo (Copy the files sakaidb.script and sakaidb.properties)
echo Or check in to CVS sakaidb.script and sakaidb.properties to update the default starter DB.


