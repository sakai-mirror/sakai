#!/bin/sh

# This shell script will bring up the Sakai quickstart at
# http://localhost:8080.  Needs for running Sakai in production will differ and
# the proper way to run Sakai in production must be determined separately for 
# each installation.
# The tomcat instance for the Sakai quickstart can be stopped by the supplied
# script 'stop.sh'.


# We are going nowhere without JAVA
if [ -z "$JAVA_HOME" ]; 
then 
  echo JAVA_HOME variable not set properly, please set this variable and rerun
  exit
fi
echo JAVA_HOME: $JAVA_HOME


# Select the data store type.  Default to hsqldb.
dbtype=$1
if [ -z "$dbtype" ];
then 
	dbtype=hsqldb;
fi

# We use Ant to configure the Sakai quickstart.  We use the version supplied 
# with the quickstart.
ANT_HOME=`pwd`/apache-ant-1.6.2
export ANT_HOME
echo ANT_HOME: $ANT_HOME

# Set the ant binary to be executable.
chmod a+x $ANT_HOME/bin/ant

PATH=$PATH:$ANT_HOME/bin
export PATH
echo new path: $PATH

# Set shell scripts to be executable.
find . -name \*.sh -exec chmod a+x {} \;

# Set the base location for the quickstart. This assumes that the 
# user will use the quickstart as distributed. That is a reasonable assumption 
# for the quickstart install.  
workrootdir=`pwd`

# Run the customization ant script in a subshell so that
# the directory change will automatically be undone.
(cd install; ant -f customize.xml -Dwork.rootdir=$workrootdir $dbtype)

# Unset the CATALINA (Tomcat) varibles so that the Tomcat scripts will
# know to automatically set appropriate values for them.
# Run Tomcat in a subshell so that the changes to the environment varables  
# will only be in effect in the subshell.  This way we do not have to worry 
# about cleaning up these variables later.

(cd *tomcat*; 
	unset CATALINA_BASE; unset CATALINA_HOME; unset CATALINA_TMPDIR;  
	bin/startup.sh)

#$Header: /cvs/quickstart/qs-1-5-1/start.sh,v 1.1 2005/03/27 18:40:20 dlhaines.umich.edu Exp $
#end
