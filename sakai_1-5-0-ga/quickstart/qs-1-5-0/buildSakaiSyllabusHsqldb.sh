#!/bin/bash
set -x

export CMD_DIR=${HOME}/tmp/test_D
export SAKAI_SRC_DIR=${HOME}/dev/sakai_1-5-1-m1/sakai/reference/src/sql/legacy/hsqldb
#export SYLLABUS_DIR=${HOME}/dev/sakai_1-5-1-m1/sakai/syllabus/component/src/sql/ddl/sakai-component-syllabus-sakai.1.5.0-schema-hsql.sql
export SYLLABUS_DIR=${HOME}/dev/sakai_1-5-1-m1/sakai/syllabus/component/src/sql/ddl
export WORK_DIR=`pwd`
export DB=sakaidb
export HSQL_DB=sakaidb

make -e loadSakaiHsqldb 
#make -w rebuildSakaiHsqldb SRC_DIR=$SRC_DIR WORK_DIR=$WORK_DIR
ls -l sakaidb*

#end
