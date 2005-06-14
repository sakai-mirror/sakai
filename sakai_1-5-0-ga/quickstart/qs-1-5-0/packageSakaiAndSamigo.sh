#!/bin/sh
echo use this to get over the ant problem invoking sakai-samigo? 
set -x
date;


    echo "Get and build sakai and samigo distributions.  Get sakai-samigo ingration code."
    cd ~/build-work  ;
 	 echo $PWD ;
 	 rm -r ~/build-work/* ;
 	 ant -f ~/dev/sakaie-eclipse/quickstart/qs-1-5-0/package.xml buildDistribution ;
 	 cd $PWD ;
 	 (ant -v -f ~/dev/sakaie-eclipse/quickstart/qs-1-5-0/sakai-samigo.xml getSourceCVS) ;
         echo "clean and compile sakai-samigo integration"      ;
 	 ant -v -f ~/dev/sakaie-eclipse/quickstart/qs-1-5-0/sakai-samigo.xml updateSakaiSamigo ;
 	 cd build/sam ;
 	 ant deploy  -Dappserver.deployment.dir=$HOME/build-work/jakarta-tomcat-5.0.28/webapps ;
	 mv $HOME/build-work/jakarta-tomcat-5.0.28/webapps/samigo.war $HOME/build-work/jakarta-tomcat-5.0.28/webapps/samigo.war.hide
 	 cd ../.. ;
	 rm -r ./build/sam/build/* ;
 	 ant -f ~/dev/sakaie-eclipse/quickstart/qs-1-5-0/sakai-samigo.xml installSamigo ;
 	 ant -f ~/dev/sakaie-eclipse/quickstart/qs-1-5-0/package.xml zipDistribution;

date;


# 	&& ant -v -f ~/dev/sakaie-eclipse/quickstart/qs-1-5-0/sakai-samigo.xml buildSakaiSamigo \
#        && rm $HOME/build-work/build/sam/lib/sakai-component-1.0.0.jar \
#	&& rm $HOME/build-work/build/sam/lib/sakai-service-1.0.0.jar \


#end
