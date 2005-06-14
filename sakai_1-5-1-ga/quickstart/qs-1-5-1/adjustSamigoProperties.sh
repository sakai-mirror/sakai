# This file should be sourced into another one, not run on its own.
# This defines a bash function to customize properties during the 
# build of the Samigo in Sakai Quickstart.  The values will be further
# customized during the install in the destination directory.

# $Header: /cvs/quickstart/qs-1-5-1/adjustSamigoProperties.sh,v 1.9 2005/05/18 15:37:36 dlhaines.umich.edu Exp $

# TTD (things to do)
# - make the output for samigo.xml able to be customized.

# - make the tag accurate.
# - move some things to the install step (customize.xml). E.g. database selection.
# - make this work in ant.


################# functions ################

function customizeDB {
# MOVE TO INSTALL STEP change the db type 
(cd $BUILD_BASE/build; perl -i -p -e "s/samigo.db=oracle/samigo.db=hsqldb/" sam/build.properties;)
(cd $BUILD_BASE/build; perl -i -p -e 's|^#(hibernate.*HSQLDialect.*)|\1|' sam/hibernate.properties sam/hibernate-hbm/hibernate.properties;)
(cd $BUILD_BASE/build; perl -i -p -e 's|^(hibernate.*Oracle.*)|#\1|' sam/hibernate.properties sam/hibernate-hbm/hibernate.properties;)
}

function customizeSamigoProperties {

#set -x
# fix up things that need to change before building.

# these are build step
(cd $BUILD_BASE/build; perl -i -p -e "s|appserver.deployment.dir=.*|appserver.deployment.dir=${APPSERVER_DIR}|" sam/build.properties;)

echo MAKE TAG ACCURATE
(cd $BUILD_BASE/build; 
cat <<EOF >> sam/build.properties
cvs.tag=sakai_1-5-1

EOF
)

# keep in build
# There was some weird problem with cp not copying the directory,
# only the contents.  This variation works.
(cd $BUILD_BASE/build; mkdir -p $OPT_DEST/sa_forms)
(cd $BUILD_BASE/build; cp -rf ./sam/conf/opt/sa_forms/* $OPT_DEST/sa_forms)

(cd $BUILD_BASE/build; cp -rfv ./sam/conf/opt/j2ee $OPT_DEST)

# register samigo
cp $BUILD_BASE/build/sakai-samigo/reg/*.xml $BUILD_BASE/localfiles/reg

# The value SAMIGODB_LOCAL_DIR will be replaced during installation / customization

# setup the samigo db connection information
cat <<END >|$SAMIGODB_LOGIN_DIR/samigo.xml
db.driverClassName=org.hsqldb.jdbcDriver
db.url=jdbc:hsqldb:file:SAMIGODB_LOCAL_DIR/samigo/samigodb
db.username=sa
db.password=
END

customizeDB;
}



#end
