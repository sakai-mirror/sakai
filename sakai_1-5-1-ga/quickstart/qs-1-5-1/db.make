# # Makefile for managing Sakai db
# $Header: /cvs/quickstart/qs-1-5-1/db.make,v 1.1 2005/03/27 18:40:20 dlhaines.umich.edu Exp $
#
# Makefiles, for those who don't know, are used by gnumake.  That is the build 
# tool used for C and other languages instead of Ant.
# It would make sense to convert this to Ant for portability.  This is 
# likely to work only on a Unix (OS X) OS.
# One strong reason to make this Ant based is so that separate tools can start to support
# targets that allow them to add information to the default database.
#

######### Module variables ############

######### Sakai common variables
SQL_FILE=all.sql
SAKAIUSER=sakaiuser
SAKAIPASSWORD=sakaipassword

######## Syllabus varables
SYLLABUS_FILE_HSQL=$HOME/build-work/build/syllabus/component/src/sql/ddl/sakai-component-syllabus-sakai.1.5.0-schema-hsql.sql

####### Samigo variables
SAMIGO_FILE=${HOME}/dev/sam/ddl/samigo-ddl/00_buildscript_oracle.sql

######### Database dialect variables

##### Hsql specific variables
# Note this assumes that there is a variable
# HSQLDB_JAR pointing to the current hsqldb jar.
SAKAI_HSQLDB=sakaidb
#OUT_DIR=/usr/localXXXX ##### unused?
# Location of the Hsqldb sql files.
SRC_DIR=${SAKAI}/reference/src/sql/legacy/hsqldb
# Location of the files containing sql commands for Hsqldb. E.g. shutdown or compact.
CMD_DIR=${SAKAI}/reference/src/sql/legacy/hsqldb
# Location where the work should be done.
WORK_DIR=${SAKAI}/reference/src/sql/legacy/hsqldb


####### MySql variables
MYSQL_DIR=${SAKAI}/reference/src/sql/legacy/mysql

####### Oracle variables
# This process assumes that the Oracle instantclient is installed.
IC=${HOME}/tools/instantclient/instantclient10_1
RUN_DIR=${SAKAI}/reference/src/sql/legacy/oracle
SRC_FILE=all.sql
# remember to pass in this variable with the correct information.
# Do not update this file.
#CONNECT=XXXXX/XXXXX@//navigo-ora.ummu.umich.edu:1521/sakaidev

##### Syllabus load file for hsqldb
SYLLABUS_FILE=$HOME/build-work/build/syllabus/component/src/sql/ddl/sakai-component-syllabus-sakai.1.5.0-schema-hsql.sql

####### Samigo variables
SAMIGO_FILE=${HOME}/dev/sam/ddl/samigo-ddl/00_buildscript_oracle.sql


####################### Things below here should not need to change.

# Utility goals
all: starttime endtime

starttime: time

endtime: time

time:
	@echo '***********' `date`

###### Contains code to build a mysql version of the sakai db.
## May want to change to not use script.
#mysql-reload-sql: mysql-create-sql mysql-load-sql

#mysql-create-sql:
#	@echo "create mysql for sites"
#	(cd ${HOME}/eclipse-workspace/sakai-devel-util/bin; ${HOME}/eclipse-workspace/test-helpers/db/unix/trunk/runMySqlTrans.sh)

#mysql-load-sql:
#	@echo "load mysql for sites"
#	(cd ${SAKAI}/reference/src/sql/legacy/mysql; ${HOME}/eclipse-workspace/test-helpers/db/unix/trunk/loadMySql.sh )


########### rebuild the mysql db
# Use the MySql dump utility to dump a loaded sql db to sql that can restore the db.

# restore to a MySql db
#dumpMySqltoSql:
#	echo "update user / password
#	sh -c 'mysqldump --user=sakaiuser --password=sakaipassword sakai --skip-extended-insert --complete-insert --no-create-info --skip-quote-names >| sakai.mysql.sql'

# Move from MySql to Oracle  UNTESTED
#dumpMySqltoOracleSql:
#	echo "update user / password
#	sh -c 'mysqldump --compatible=oracle --user=sakaiuser --password=sakaipassword sakai --skip-extended-insert --complete-insert --no-create-info --skip-quote-names >| sakai.oracle.sql'
#######
#rebuildMySql:
#	echo "update user / password
#	cd ${MYSQL_DIR}; sh -c 'mysql --user=sakaiuser --password=sakaipassword sakai <${MYSQL_DIR}/${SQL_FILE}'

#loadFileMySql:
#	echo "update user / password
#	sh -c 'mysql --user=sakaiuser --password=sakaipassword sakai <${NEW_FILE}'

########### rebuild my oracle instance
# This assumes that the user has the Oracle instant client installed.

# This will rebuild the oracle version of the db.
# We cat the source file in so that it gets
# an EOF, otherwise we need to change the script
# to add a quit
#rebuildOracle:
#	export DYLD_LIBRARY_PATH=${IC}; \
#	export PATH=${PATH}:${IC}; \
#	cd ${RUN_DIR}; \
#	cat ${SRC_FILE} | sqlplus ${CONNECT}  >| ~/tmp.tmp;
#	# This extracts out the error messages but ignores ones we know are harmless.
#	cd ${RUN_DIR}; grep -i ORA-  <~/tmp.tmp | grep -v 'ORA-01418'

#reloadOracleOld:
#	${HOME}/helpers/db/unix/trunk/runSakaiLoadOracle.sh

##SAMIGO_FILE=${HOME}/dev/sam/ddl/samigo-ddl/00_buildscript_oracle.sql
#rebuildSamigoOracle:
#	export DYLD_LIBRARY_PATH=${IC}; \
#	export PATH=${PATH}:${IC}; \
#	cd ${RUN_DIR}; \
#	cat ${SRC_FILE} | sqlplus ${CONNECT}  >| ~/tmp.tmp;
#	# This extracts out the error messages but ignores ones we know are harmless.
#	cd ${RUN_DIR}; grep -i ORA-  <~/tmp.tmp | grep -v 'ORA-01418'


############ rebuild the hsql db
# need to:
# - get rid of the sakaidb
# - copy in server.properties
# - start hsqldb server
# - load the sql

# Default to the one defined in the environment
##HSQLDB_JAR=${CATALINA_HOME}/common/lib/hsqldb-1.7.2.2.jar
##OUT_DIR=/usr/local/sakai/db
#OUT_DIR=/usr/localXXXX
#SRC_DIR=${SAKAI}/reference/src/sql/legacy/hsqldb
##SRC_DIR=${HOME}/dev/SakaiB/sakai/reference/src/sql/legacy/hsqldb
## SQL_FILE="./all.sql"
#CMD_DIR=${SAKAI}/reference/src/sql/legacy/hsqldb
#WORK_DIR=${SAKAI}/reference/src/sql/legacy/hsqldb
#SYLLABUS_FILE=$HOME/build-work/build/syllabus/component/src/sql/ddl/sakai-component-syllabus-sakai.1.5.0-schema-hsql.sql

############## run a hsql server 
# This code sets up a server on the default port.  It will
# then allow adding files.  Each file add will autocommit.  Otherwise
# the transaction gets rolled back.

# The files are assumed to be in the working directory.  This makes it 
# possible to use files that include others.  Otherwise there are
# portability problems specifying path names.


# setup the operating directory
setupHsqldbWork:
	-mkdir ${WORK_DIR};
	-cp ${CMD_DIR}/*.rc ${WORK_DIR}
	-cp ${CMD_DIR}/*.sql ${WORK_DIR}
	
# Start up a hsqldb server
runHsqldbServer: 
	#	cp ${CMD_DIR}/server.properties ${WORK_DIR};
	#	-chmod +w ${WORK_DIR}/server.properties 
	cd ${WORK_DIR} && java -cp ${HSQLDB_JAR} org.hsqldb.Server -database.0 ${HSQL_DB} -dbname.0 ${HSQL_DB} &
	@echo "sleep to allow server to start"
	sleep 15;

# Startup the hsqldb server with arguments (Do I need both?)
runHsqldbServerArgs:
	#	-mkdir ${WORK_DIR};
	cd ${WORK_DIR} && java -cp ${HSQLDB_JAR} org.hsqldb.Server ${HSQLDB_ARGS} &
	@echo "sleep to allow server to start"
	sleep 15;

#mkdir /usr/local/sakai/db
#cd /usr/local/sakai/db
#java -cp $CATALINA_HOME/common/lib/hsqldb-1.7.2.2.jar org.hsqldb.Server

# Compact a hsqldb database so that all the information is
# in the script file.
compactHsqldb: runHsqldbServer
	cd ${SRC_DIR}; java -jar ${HSQLDB_JAR} --rcfile ${CMD_DIR}/sqltool.rc sakaidb ${CMD_DIR}/compact.sql

# Copy useful files to the current directory
copySrcFiles:
	-cp ${SRC_DIR}/*.rc ${WORK_DIR}
	-cp ${SRC_DIR}/*.sql ${WORK_DIR}

############ load hsqldb sakai data

# This will start the server on an empty db and then add files one by one.
# The recursive calls to make allow resetting the value of the variables.
# Values passed on the command line override values assigned in the file.
loadSakaiHsqldb: deleteCurrentSakaidb runHsqldbServer
	# get local copies of the input files
	make copySrcFiles SRC_DIR=${CMD_DIR}
	make copySrcFiles SRC_DIR=${SAKAI_SRC_DIR}
	# add the default Sakai files.  
	make addFileToHsqldb2 ADD_FILE=all.sql
	# Add the syllabus file.  Note that the 
	# default hsqldb sql file will shutdown the db.
	make copySrcFiles SRC_DIR=${SYLLABUS_DIR}
	make runHsqldbServer
	make addFileToHsqldb2 ADD_FILE=sakai-component-syllabus-sakai.1.5.0-schema-hsql.sql
	make addFileToHsqldb2 ADD_FILE=shutdown.sql


shutdownSakaiHsqldb: 
	# not fully tested
	cd ${WORK_DIR}; java -jar ${HSQLDB_JAR} --rcfile sqltool.rc sakaidb shutdown.sql

#	cd ${SRC_DIR}; java -jar ${HSQLDB_JAR} --rcfile ${CMD_DIR}/sqltool.rc sakaidb ${CMD_DIR}/shutdown.sql


addFileToHsqldb: runHsqldbServer
	# not fully tested
	cd ${WORK_DIR}; java -jar ${HSQLDB_JAR} --continueOnErr --rcfile sqltool.rc sakaidb ${ADD_FILE} shutdown.sql

addFileToHsqldb2: 
	# autoCommit is set so that the file will be added.  sqltool by default will not commit 
	# the transaction.
	cd ${WORK_DIR}; java -jar ${HSQLDB_JAR} --autoCommit --rcfile sqltool.rc ${DB} ${ADD_FILE} 

#	cd ${SRC_DIR}; java -jar ${HSQLDB_JAR} --rcfile ${CMD_DIR}/sqltool.rc sakaidb ${ADD_FILE}

#(cd ~/dev/SakaiB/sakai/reference/src/sql/legacy/hsqldb;
#SQL_SRC="./all.sql"
#rm sakaidb.*; java -jar $CATALINA_HOME/common/lib/hsqldb-1.7.2.2.jar --rcfile sqltool.rc sakaidb ${SQL_SRC}

# get rid of the current hsql sakai db
deleteCurrentSakaidb:
	@echo "change to variable sakaidb name"
	-rm ${WORK_DIR}/sakaidb.*

# rebuild from scratch
# This is a variable set just for this target.
rebuildSakaiHsqldb: HSQL_DB=${SAKAI_HSQLDB}
# Seems redundent with loadSakaiHsqldb
rebuildSakaiHsqldb: deleteCurrentSakaidb loadSakaiHsqldb

# Samigo is different enough to make separate targets a 
# requirement
prepSamigoDb:
	-mkdir ${WORK_DIR}
	-rm ${WORK_DIR}/*
	-cp ${SAMIGO_SQL_DIR}/*.sql ${WORK_DIR}
	-cp ${SAMIGO_SQL_DIR}/*.rc ${WORK_DIR}

deleteCurrentSamigodb:
	-rm ${WORK_DIR}/samigodb


# rebuild Samigo from scratch
rebuildSamigoHsqldb: 
	@echo not fully tested
	make prepSamigoDb WORK_DIR=${HOME}/tmp/samigodb SAMIGO_SQL_DIR=/Volumes/UserData/Users/dlhaines/build-work/localfiles/ddl/samigo-ddl
	make deleteCurrentSamigodb WORK_DIR=${HOME}/tmp/samigodb
	make runHsqldbServerArgs HSQLDB_ARGS="-database.0 file:${HOME}/tmp/samigodb/samigodb -dbname.0 samigodb"   WORK_DIR=${HOME}/tmp/samigodb
	make addFileToHsqldb2 DB=samigodb ADD_FILE=01_schema_hsqldb.sql  WORK_DIR=${HOME}/tmp/samigodb
	make addFileToHsqldb2 DB=samigodb ADD_FILE=02_defaultSetup_hsqldb.sql WORK_DIR=${HOME}/tmp/samigodb
	make hsqlShutdown DB=samigodb WORK_DIR=${HOME}/tmp/samigodb

hsqlShutdown:
	@echo not fully tested
	cd ${WORK_DIR}; java -jar ${HSQLDB_JAR} --rcfile sqltool.rc --noinput --sql "shutdown;" ${DB} 

#	cd ${OUT_DIR}; java -jar ${HSQLDB_JAR} --rcfile sqltool.rc --noinput --sql "commit; shutdown;" ${DB} 

###### Contains code to build a mysql version of the sakai db.

## Should change to no rely on a scripts
mysql-reload-sql: mysql-create-sql mysql-load-sql

mysql-create-sql:
	@echo "too specific"
	@echo "create mysql for sites"
	(cd ${HOME}/eclipse-workspace/sakai-devel-util/bin; ${HOME}/eclipse-workspace/test-helpers/db/unix/trunk/runMySqlTrans.sh)

mysql-load-sql:
	@echo "too specific"
	@echo "load mysql for sites"
	(cd ${SAKAI}/reference/src/sql/legacy/mysql; ${HOME}/eclipse-workspace/test-helpers/db/unix/trunk/loadMySql.sh )


########### rebuild the mysql db
# Use the MySql dump utility to dump a loaded sql db to sql that can restore the db.

# restore to a MySql db
dumpMySqltoSql:
	echo "update user / password"
	sh -c 'mysqldump --user=sakaiuser --password=sakaipassword sakai --skip-extended-insert --complete-insert --no-create-info --skip-quote-names >| sakai.mysql.sql'

# Move from MySql to Oracle  UNTESTED
dumpMySqltoOracleSql:
	echo "update user / password"
	sh -c 'mysqldump --compatible=oracle --user=sakaiuser --password=sakaipassword sakai --skip-extended-insert --complete-insert --no-create-info --skip-quote-names >| sakai.oracle.sql'
#######
rebuildMySql:
	echo "update user / password"
	cd ${MYSQL_DIR}; sh -c 'mysql --user=sakaiuser --password=sakaipassword sakai <${MYSQL_DIR}/${SQL_FILE}'

loadFileMySql:
	echo "update user / password"
	sh -c 'mysql --user=sakaiuser --password=sakaipassword sakai <${NEW_FILE}'


################ Oracle database #################

# This assumes that the user has the Oracle instant client installed.

# This will rebuild the oracle version of the db.
# We cat the source file in so that it gets
# an EOF, otherwise we need to change the script
# to add a quit
rebuildOracle:
	export DYLD_LIBRARY_PATH=${IC}; \
	export PATH=${PATH}:${IC}; \
	cd ${RUN_DIR}; \
	cat ${SRC_FILE} | sqlplus ${CONNECT}  >| ~/tmp.tmp;
	# This extracts out the error messages but ignores ones we know are harmless.
	cd ${RUN_DIR}; grep -i ORA-  <~/tmp.tmp | grep -v 'ORA-01418'

#reloadOracleOld:
#	${HOME}/helpers/db/unix/trunk/runSakaiLoadOracle.sh

#SAMIGO_FILE=${HOME}/dev/sam/ddl/samigo-ddl/00_buildscript_oracle.sql
rebuildSamigoOracle:
	export DYLD_LIBRARY_PATH=${IC}; \
	export PATH=${PATH}:${IC}; \
	cd ${RUN_DIR}; \
	cat ${SRC_FILE} | sqlplus ${CONNECT}  >| ~/tmp.tmp;
	# This extracts out the error messages but ignores ones we know are harmless.
	cd ${RUN_DIR}; grep -i ORA-  <~/tmp.tmp | grep -v 'ORA-01418'



###### leave this here for emacs

     ### Local Variables: ***
     ### mode:makefile ***
     ### End: ***


# # # end
