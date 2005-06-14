#!/bin/sh
# $Header: /cvs/quickstart/qs-1-5-1/makeSakaiTarball.sh,v 1.1 2005/03/27 18:40:20 dlhaines.umich.edu Exp $

# Use this to build the Sakai tar ball.
# Download the Samigo tar ball.

TAG=' -r sakai_1-5-rc11 '
set -x
cvs -z9 -d:ext:sakai@cvs.sakaiproject.org:/cvs export $TAG sakai
cvs -z9 -d:ext:sakai@cvs.sakaiproject.org:/cvs export $TAG help
cvs -z9 -d:ext:sakai@cvs.sakaiproject.org:/cvs export $TAG presentation
#cvs -z9 -d:ext:sakai@cvs.sakaiproject.org:/cvs export $TAG sakai-samigo
#cvs -z9 -d:ext:sakai@cvs.sakaiproject.org:/cvs export $TAG sam
cvs -z9 -d:ext:sakai@cvs.sakaiproject.org:/cvs export $TAG syllabus
#end
