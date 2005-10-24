==========================
Sakai Internationalization
==========================

All of the legacy Sakai tools and many of the JSF based tools have been
localized and internationalized. Translations are underway in many
languages, as follows:

1) Chinese (China)
   Status: Completed for Sakai 2.0.1
   Translated by: Tianhua Ding
   Contact: Beth Kirschner    beth.kirschner@umich.edu
   Local ID: zh_CN
   
2) Korean
   Status: Completed for Sakai 2.0.1
   Translated by: Il-hwan Kim
   Contact: Beth Kirschner    beth.kirschner@umich.edu
   Local ID: ko_KR
   
3) Japanese
   Status: in progress
   Translated by: Tatsuki Sugura
   Contact: Shoji Kajita   kajita@nagoya-u.jp
   Local ID: ja_JP
   
4) Dutch
   Status: Completed for Sakai 2.0.1
   Translated by: n/a
   Contact: Jim Doherty   jim.doherty@gmail.com 
   Local ID: nl_NL
   
5) Danish
   Status: in progress
   Translated by: n/a
   Contact: Kasper Pagels   kasper@pagels.dk
   Local ID: da_DK
   
6) Hebrew 
   Status: ??
   Translated by: n/a
   Contact: Dov Winer      admin@makash.org.il
   Local ID: iw_IL

7) Brazilian Portugese 
   Status: ??
   Translated by: n/a
   Contact: Alceu Fernandes Filho   alceu@unisinos.br
   Local ID: pt_BR
   
8) Portugese
   Status: ??
   Translated by: n/a
   Contact:  Feliz Gouveia    fribeiro@ufp.pt
   Local ID: pt_PT
   
9) Slovakian
   Status: ??
   Translated by: n/a
   Contact: Michal Mosovic   salmon@salmon.sk
   Local ID: sk_SK
    
10) Catalan
    Status: ??
    Translated by: n/a
    Contact: Alex Ballesté   alex@asic.udl.es
    Local ID: ca_ES
    

Currently, the language locale must be defined at boot time, by
setting the tomcat JAVA_OPTS property as follows:

-- catalina.sh -----------------------------------------------
## Define default language locale: Japanese / Japan
JAVA_OPTS="$JAVA_OPTS -Duser.language=ja -Duser.region=JP"
--------------------------------------------------------------

-- catalina.bat ----------------------------------------------
rem Define default language locale: Japanese / Japan
set JAVA_OPTS=%JAVA_OPTS% -Duser.language=ja -Duser.region=JP
--------------------------------------------------------------
