set PWD=%CD%
mkdir \usr\local\sakai\db
cd\usr\local\sakai\db
start java -cp %CATALINA_HOME%\common\lib\hsqldb-1.7.2.2.jar org.hsqldb.Server
cd %PWD%
