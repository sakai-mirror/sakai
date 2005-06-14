#!/bin/sh

# Terminate the Tomcat instance for the quickstart version of Sakai.

# Reset the environment variables so that the values used for starting 
# Tomcat are used when shutting Tomcat down. The CATALINA variables might have 
# other values set by default and those would get used unless we explicitly
# reset the values here.

# Run the shutdown in a subshell so that the environment changes (cd, unset)
# are restricted to the subshell and don't affect the calling shell.
#
(cd *tomcat*; 
	unset CATALINA_BASE; unset CATALINA_HOME; unset CATALINA_TMPDIR;  
	bin/shutdown.sh)

# $Header: /cvs/quickstart/qs-1-5-0/stop.sh,v 1.2 2005/03/27 02:12:28 dlhaines.umich.edu Exp $
#end