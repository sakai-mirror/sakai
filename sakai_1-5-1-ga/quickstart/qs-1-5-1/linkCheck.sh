#!/bin/bash
set -x
# $Header: /cvs/quickstart/qs-1-5-1/linkCheck.sh,v 1.2 2005/05/19 18:56:25 dlhaines.umich.edu Exp $
# linkcheck a local (file reference) website based on this page.
#linkchecker --intern=^file -s -P3 -r3 -Fhtml file:///Volumes/UserData/Users/dlhaines/tmp/sakai_1-5-1/03/index.html
#linkchecker --intern=cvs.sakaiproject -s -P3 -r3 -Fhtml http://cvs.sakaiproject.org/release/proto-1.5.1/index.html
linkchecker --intern=^file -s -P3 -r3 -Fhtml file:///Volumes/UserData/Users/dlhaines/dev/sakai_1-5-1-release/docs/release/1.5.1/index.html
#end
