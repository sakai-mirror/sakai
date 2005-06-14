#!/bin/sh
# $Header: /cvs/quickstart/qs-1-5-1/assembleWebSite.sh,v 1.1 2005/03/27 18:40:20 dlhaines.umich.edu Exp $

set -x
# Assemble and tar up a web site for Sakai
# get the resources
# put in right place
# tar it all up

# to zip files use below syntax.  Must specify the 
# output zip file name.
# zip sakai_src.zip sakai_src

setupVariables();

# BANNER="WARNING this is a TEST setup for TESTING web site construction"

# #### references here down to end marker line are verified.
# ## assumes that only docs are of interest.
# SRC_HTML_DIR=$HOME/dev/sakaie-eclipse/docs/release/1.5.0
# SRC_TEXT_DIR=$HOME/dev/sakaie-eclipse/docs/release/1.5.0
# SAMIGO_SRC_TEXT_DIR=$HOME/releases/1-5-0/sakai-samigo/
# ##### references below here not verified

# SRC_ZIP_DIR=$HOME/releases/1-5-0/docs
# SRC_TAR_DIR=$HOME/releases/1-5-0
# SRC_DOCS_DIR=$HOME/dev/sakaie-eclipse/docs/release/1.5.0/docs
# #SRC_DOCS_DIR_2=$HOME/test/source
# DEST_DIR=./1.5.0
# DEST_SRC_DIR=src
# DEST_DOCS_DIR=docs
# QUICKSTART=$HOME/Sites/sakai.sakai_1-5-rc11.zip
# QUICKSTART_NAME=sakai_quickstart_1-5-0.zip
# OUTPUT_TAR=sakai-web-1-5-0.tar
# LICENSES=/Users/haines/dev/sakaif-eclipse/Licenses/text

### Nothing below this line should need to be changed (after
### I get it right)

echoVariables();

generateContent();

# echo $BANNER

# echo Html pages are coming from $SRC_HTML_DIR
# echo Text files are coming from $SRC_TEXT_DIR
# echo Zip files are coming from $SRC_ZIP_DIR
# echo Documents are coming from $SRC_DOCS_DIR and $SRC_DOCS_DIR_2
# echo The files are being placed in $DEST_DIR and sub directories
# echo The quickstart used is: $QUICKSTART and is being named $QUICKSTART_NAME
# echo The output tar name is: $OUTPUT_TAR

setupOutputDirs();

# # setup output directories
# rm -r -f $DEST_DIR
# mkdir $DEST_DIR
# mkdir -p $DEST_DIR/$DEST_SRC_DIR
# mkdir -p $DEST_DIR/$DEST_DOCS_DIR

# grab resources

getStaticPages();

# # grab static pages
# cp $SRC_HTML_DIR/*.html $DEST_DIR
# cp $SRC_TEXT_DIR/*.txt $DEST_DIR
# cp $SRC_TEXT_DIR/*.zip $DEST_DIR/$DEST_DOCS_DIR
# cp $SAMIGO_SRC_TEXT_DIR/*.txt $DEST_DIR
# cp $LICENSES/*.pdf $DEST_DIR/$DEST_DOCS_DIR

getQuickstart();

getZipTarPdf();
# get the code
# cp $SRC_ZIP_DIR/*.zip $DEST_DIR/$DEST_SRC_DIR
# cp $SRC_TAR_DIR/*.tar $DEST_DIR/$DEST_SRC_DIR
# cp $SRC_TAR_DIR/docs/*.pdf $DEST_DIR/$DEST_SRC_DIR

# # get the docs 
# cp $SRC_DOCS_DIR/*.pdf $DEST_DIR/$DEST_DOCS_DIR
# #cp $SRC_DOCS_DIR_2/*.pdf $DEST_DIR/$DEST_DOCS_DIR

# tar up that tar
buildTar();
#tar cvf $OUTPUT_TAR $DEST_DIR 
#ls -l $OUTPUT_TAR


############# functions

function setupVariables() {
    BANNER="WARNING this is a TEST setup for TESTING web site construction"

#### references here down to end marker line are verified.
## assumes that only docs are of interest.
    SRC_HTML_DIR=$HOME/dev/sakaie-eclipse/docs/release/1.5.0
    SRC_TEXT_DIR=$HOME/dev/sakaie-eclipse/docs/release/1.5.0
    SAMIGO_SRC_TEXT_DIR=$HOME/releases/1-5-0/sakai-samigo/
##### references below here not verified

    SRC_ZIP_DIR=$HOME/releases/1-5-0/docs
    SRC_TAR_DIR=$HOME/releases/1-5-0
    SRC_DOCS_DIR=$HOME/dev/sakaie-eclipse/docs/release/1.5.0/docs
    #SRC_DOCS_DIR_2=$HOME/test/source
    DEST_DIR=./1.5.0
    DEST_SRC_DIR=src
    DEST_DOCS_DIR=docs
    QUICKSTART=$HOME/Sites/sakai.sakai_1-5-rc11.zip
    QUICKSTART_NAME=sakai_quickstart_1-5-0.zip
    OUTPUT_TAR=sakai-web-1-5-0.tar
    LICENSES=/Users/haines/dev/sakaif-eclipse/Licenses/text
}

function echoVariables {
    echo $BANNER

    echo Html pages are coming from $SRC_HTML_DIR
    echo Text files are coming from $SRC_TEXT_DIR
    echo Zip files are coming from $SRC_ZIP_DIR
    echo Documents are coming from $SRC_DOCS_DIR and $SRC_DOCS_DIR_2
    echo The files are being placed in $DEST_DIR and sub directories
    echo The quickstart used is: $QUICKSTART and is being named $QUICKSTART_NAME
    echo The output tar name is: $OUTPUT_TAR
}


function setupOutputDirs {
    # setup output directories
    rm -r -f $DEST_DIR
    mkdir $DEST_DIR
    mkdir -p $DEST_DIR/$DEST_SRC_DIR
    mkdir -p $DEST_DIR/$DEST_DOCS_DIR
}

function getStaticPages {
# grab static pages
    cp $SRC_HTML_DIR/*.html $DEST_DIR
    cp $SRC_TEXT_DIR/*.txt $DEST_DIR
    cp $SRC_TEXT_DIR/*.zip $DEST_DIR/$DEST_DOCS_DIR
    cp $SAMIGO_SRC_TEXT_DIR/*.txt $DEST_DIR
    cp $LICENSES/*.pdf $DEST_DIR/$DEST_DOCS_DIR

}

function getQuickstart {
    # get the quickstart
    cp $QUICKSTART $DEST_DIR/$DEST_SRC_DIR/$QUICKSTART_NAME
}

function getZipTarPdf {
# get the code
    cp $SRC_ZIP_DIR/*.zip $DEST_DIR/$DEST_SRC_DIR
    cp $SRC_TAR_DIR/*.tar $DEST_DIR/$DEST_SRC_DIR
    cp $SRC_TAR_DIR/docs/*.pdf $DEST_DIR/$DEST_SRC_DIR

# get the docs 
    cp $SRC_DOCS_DIR/*.pdf $DEST_DIR/$DEST_DOCS_DIR
#cp $SRC_DOCS_DIR_2/*.pdf $DEST_DIR/$DEST_DOCS_DIR

}

function buildTar {
    # tar up that tar
    tar cvf $OUTPUT_TAR $DEST_DIR 
    ls -l $OUTPUT_TAR
}


#############
function generateContent {
}

function generateTagLibDoc {
    echo to implement
######## taglib doc generation
# ./help/tool/src/webapp/WEB-INF/help_jsf.tld
# ./jsf/src/META-INF/sakai_jsf.tld
# ./module/src/webapp/WEB-INF/module_jsf.tld
# ./syllabus/tool/src/webapp/WEB-INF/syllabus_jsf.tld

# extract the jsf tld information
# jar xvf /Users/haines/.maven/repository/jsf/jars/jsf-impl-1.1.jar META-INF/jsf_core.tld META-INF/html_basic.tld
# generate the docs
#java -jar /Users/haines/.maven/repository/taglibrarydoc/jars/tlddoc-1.1.jar -d ./sakai-tld ./jsf/src/META-INF/sakai_jsf.tld META-INF/*.tld

##(cd jsf; maven -Dtaglib.src.dir=$HOME/dev/sakai_1-5-0_test/sakai/jsf/src/META-INF taglib:taglibdoc)
# these should work
# oought to be able to specify the outdir, but it doesn't seem to work.
#(maven -Dtaglib.src.dir=$HOME/dev/sakai_1-5-0_test/sakai/syllabus/tool/src/webapp/WEB-INF taglib:taglibdoc)
#(maven -Dtaglib.src.dir=$HOME/dev/sakai_1-5-0_test/sakai/module/src/webapp/WEB-INF taglib:taglibdoc)
#(maven -Dtaglib.src.dir=$HOME/dev/sakai_1-5-0_test/sakai/help/tool/src/webapp/WEB-INF taglib:taglibdoc)
#(maven -Dtaglib.src.dir=$HOME/dev/sakai_1-5-0_test/sakai/jsf/src/META-INF taglib:taglibdoc)

}

function generateSourceJar {
    echo to implement
}

function generateJavaDoc {
    echo to implement

# javadocs  Note that the dest dir is not modified by name of project so if specify a generic one files get overwritten.
#maven -Dmaven.javadocs.destdir= <???> -Dgoal=javadoc multiproject:goal 
# this runs, and should put the javadocs in a jar
#maven -Dgoal=javadoc:generate,javadoc:install multiproject:goal 


}

function generateMavenJars {
    echo to implement
}



#end
