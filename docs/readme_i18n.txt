Sakai Internationalization
==========================

All of the legacy Sakai tools and many of the JSF based tools have been
localized and internationalized. Translations are underway in many
languages, and completed for the following:

1) Chinese (Simplified) 
2) Korean

Currently, the language locale must be defined at boot time, by
setting the tomcat JAVA_OPTS property in the catalina.sh or catalina.bat file:

## Define default language locale: Chinese (Simplified) / China
#JAVA_OPTS="$JAVA_OPTS -Duser.language=zh -Duser.region=CN"

## Define default language locale: Japanese / Japan
#JAVA_OPTS="$JAVA_OPTS -Duser.language=ja -Duser.region=JP"

## Define default language locale: Korean / Korea
#JAVA_OPTS="$JAVA_OPTS -Duser.language=ko -Duser.region=KR"

