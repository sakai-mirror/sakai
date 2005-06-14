#!/bin/sh
# Create a standalone qs that will run under /usr/local/tomcat with an arbitrary /opt directory
# $Header: /cvs/quickstart/qs-1-5-1/packageSamigoInSakai.sh,v 1.7 2005/05/03 17:50:47 dlhaines.umich.edu Exp $

########

# Get the required properties and make sure they are set.

source packageQuickDemoSamigo.properties

SAKAI_TAG=${SAKAI_TAG:?Must define value}
TAG=${TAG:?Must define value}
SAKAI_SAMIGO_TAG=${SAKAI_SAMIGO_TAG:?"Must define value"} 
SAMIGO_TAG=${SAMIGO_TAG:?"Must define value"} ;
SCRIPT_BASE=${SCRIPT_BASE:?"Must define value"} ;
BUILD_BASE=${BUILD_BASE:?"Must define value"}
PWD=${PWD:?"Must define value"}
OPT_DEST=${OPT_DEST:?"Must define value"}
SAMIGODB_SRC_DIR=${SAMIGODB_SRC_DIR:?"Must define value"}
SAMIGODB_DEST_DIR=${SAMIGODB_DEST_DIR:?"Must define value"}
SAMIGODB_LOGIN_DIR=${SAMIGODB_LOGIN_DIR:?"Must define value"}
APPSERVER_DIR=${APPSERVER_DIR:?"Must define value"}

########### Change only things above this line. #######################

date;
echo "Get and build sakai and samigo distributions.  Add help, presentation, and syllabus tools."

# set -x

source $SCRIPT_BASE/adjustSamigoProperties.sh

# BUILD_BASE contains the build directory
mkdir $BUILD_BASE
rm -r $BUILD_BASE/*

# Do sakai
#(cd $BUILD_BASE; ant -f $SCRIPT_BASE/package.xml buildDistribution;)
(cd $BUILD_BASE; ant -f $SCRIPT_BASE/packageQuickDemoSakai.xml buildDistribution;)

# don't zip sakai

# get samigo (sam)
(cd $BUILD_BASE/build; cvs -z9 -d:ext:sakai@cvs.sakaiproject.org:/cvs co -r $SAMIGO_TAG sam)

# sakai-samigo
(cd $BUILD_BASE/build; cvs -z9 -d:ext:sakai@cvs.sakaiproject.org:/cvs export -r $SAKAI_SAMIGO_TAG sakai-samigo) 

# copy sakai-samigo over sam (to replace various things to make them support sakai)
(cd $BUILD_BASE/build; cp -rf sakai-samigo/* sam)

# add the samigo db to the build 
(cd $BUILD_BASE/localfiles; 
    rm -rf $BUILD_BASE/localfiles/samigo;
    mkdir -p $BUILD_BASE/localfiles/samigo;
    cp $SAMIGODB_SRC_DIR/samigodb.* $BUILD_BASE/localfiles/samigo;
    )

# FIX - copy down sakai-dispatch (Bad) Should deploy to the right spot.
(cp -r /usr/local/sakai/sakai-dispatch $BUILD_BASE/localfiles)

# changes values in samigo to point to Quickstart locations
# This is a shell function defined in adjustSamigoProperties.sh.
customizeSamigoProperties

# compile and deploy samigo into sakai
(cd $BUILD_BASE/build/sam ; ant deploy-in-sakai -Dappserver.deployment.dir=$APPSERVER_DIR)


mkdir $APPSERVER_DIR/samigo
mv  $APPSERVER_DIR/samigo.war  $APPSERVER_DIR/samigo
(cd $APPSERVER_DIR/samigo; jar xf samigo.war; rm samigo.war)

# zip the whole thing up.
(cd $BUILD_BASE; ant -f $SCRIPT_BASE/packageQuickDemoSakai.xml zipDistribution)

date;
#end
