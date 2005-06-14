This is the QuickSource Samigo source distribution for Sakai 1.5.1.

This information has been distilled from the contents of the
sam/README.txt and the sam/README-INTEGRATION.txt files.  It is edited
to reflect the QuickSource distribution format.  There are some
differences between building the QuickSource and the CVS 
distributions. 

- Samigo integration with Sakai requires some special steps.  The
QuickSource distribution is already configured to support Samigo within
Sakai. The steps described in the README-INTEGRATION.txt file to copy
the sakai-samigo files into the sam distribution are not required when
using the QuickSource source distribution.  If samigo / sakai-samigo
code is checked out directly from CVS be sure to read the readme files
in the the sam and sakai-samigo directories.

- Configuration of the security and settings locations for QuickSource
is by hand.  If you change the default location of these files the
changes made to those configuration files in the QuickSource source
distribution will not automatically be propagated to those non-default
locations.

The steps in building and deploying the Samigo tool are as follows:
- Get the QuickSource distribution.
- Install Sakai and verify that it is working. 
- Configure the Samigo build and database settings.
- Build and deploy Samigo.

OBTAINING THE QuickSource DISTRIBUTION

The source for the Sakai 1.5.1 QuickSource distribution is at:
http://cvs.sakaiproject.org/release/1.5.1/sakai.sakai_1-5-1.QuickSource.zip.
This distribution includes code for both Sakai and Samigo.

FILE LOCATIONS

It is possible for the installer to choose to put files used by Sakai
and Samigo in locations of their choice. In this readme we refer to
the location of the configuration files used by a running Samigo
instance as OPT.  Typically this is a top level directory.  For
example in a unix installation it may be /opt and in a Windows
installation it may be C:\opt.  When the OPT value is referenced below
you should replace it in a specific installation with a value
appropriate to your installation.

INSTALLING SAKAI

Please see the accompanying QuickSource_README.TXT file.

INSTALLING SAMIGO

There are three aspects to a Samigo installation:
- Build and deploy Samigo.  This is the best time to decide on which
database implementation will be used.
- Configure Samigo file locations.
- Configure Samigo database.

In practice these aspects are interwoven in build and deploy process.

BUILDING AND DEPLOYING SAMIGO

The instructions for performing a Samigo build are somewhat different
that those for a Sakai build.  Samigo builds are controlled by the Ant
tool, while Sakai uses Maven.  The database configuration is done
during the build and deploy step. Changing the database implementation
is most easily done by changing the build time configuration and then
redeploying Samigo.

1. Decide on a location to store the security and settings files for
Samigo installation. For a production installation make sure that this
location is available in the production environment.  This is the OPT
value mentioned above.

2. Edit build.properties (in the sam directory), and create/modify the
properties: build.environment, samigo.db, and appserver.deployment.dir.

The property build.environment is a tag for the type of build.  We suggest you
leave the value of the tag as 'dev'.

The property samigo.db states the type of database implementation to
be used.  The supported values are: mysql, oracle, and hsqldb. 

The property appserver.deployment.dir should point to the correct
tomcat webapps location.  For example:
appserver.deployment.dir=/usr/local/tomcat/webapps (in Unix) or
appserver.deployment.dir=c:/tomcat/webapps (in Windows).  Even though
the back slash is preferred in Windows file paths, it is not
interpreted appropriately in Java properties files so the forward
slash is used here.

The other values in this build.properties file can be left as they are.

3. Specify the desired hibernate RDBMS dialect in both the
sam/hibernate-hbm/hibernate.properties and sam/hibernate.properties
files. See below for details on the dialect setting.  Samigo v1.5
contains its predecessor, the Navigo project, so it "appears" to have
some duplicated configuration files. In Samigo v2.0, Navigo will be
excluded from the distribution.

4. Recursively copy the security and settings directories found in the
sam/conf/opt/sa_forms/ and sam/conf/opt/j2ee/ directories in the Sam
source directory to the OPT directory you have chosen.  

5. Edit the appropriate applicationContext.xml configuration file
sam/src/org/sakaiproject/spring directory.  For Oracle the file is
applicationContext_sequence.xml, for MySQL or HSQLDB it is
applicationContext_identity.xml.  DO NOT change the
applicationContext.xml file itself as it will be overwritten by one of
the files above during the build process.  Changes made directly to
applicationContext.xml file will be lost.

In either case, update the initial portion of the following line in
the file to point to the desired file.

   <value>file:/opt/sa_forms/java/dev/org/sakaiproject/security/sam/samigo.xml</value>

Also modify the following parameter value in
sam/webapp/WEB-INF/web.xml to point to the appropriate location as 
   
   <context-param>
     <param-name>PathToSecurity</param-name>
     <param-value>/opt/sa_forms/java/dev</param-value>
   </context-param>

Note that the directory structure after /opt/sa_forms/java/dev/ must
remain as org/sakaiproject/security/sam.

Set the following parameter in sam/webapp/WEB-INF/web.xml to the
appropriate path.

   <context-param>
     <param-name>PathToSettings</param-name>
     <param-value>/opt/j2ee/dev</param-value>
   </context-param>

However, make sure that the directory structure after opt/j2ee/dev/
remains as org/sakaiproject/settings/sam/.

6. Update the file samigo.xml under OPT so it reflects your chosen
database settings.  More information on configuring databases is given
below.  Note that the content of this file is actually in a properties
file format.

7. Register the Samigo tool by copying sakai-samigo/reg/*.xml to
SAKAILOCAL/reg. This makes Samigo available in the Sakai user
interface.

8. Configure a database for Samigo (see below).  

9. Build and deploy Samigo by running the command "ant
deploy-in-sakai" to create samigo.war. This ant build will also move
samigo.war to tomcat/webapps ready to be deployed.

10. Verify the settings under the OPT directory.

11. Test Samigo as a tool inside of the Sakai portal. Log into Sakai as
'admin' and create a new worksite by clicking on "Worksite Setup" on
the left navigation bar, then "New", select "project website" and hit
"Continue". A project Information form will be displayed, complete the
form and hit "Continue". A list of tools available for your project
will be displayed and you should notice that Samigo is among
them. Select Samigo and hit "Continue", and follow the instructions to
finish setting up the new worksite.

CONFIGURE A DATABASE FOR SAMIGO

For each of the supported database implementations follow the
instructions below to create and populate the necessary database and
database tables.

***** Support for MySQL ******
1. Download and install MySQL 4.1+ from http://dev.mysql.com/downloads.

2. Log in to the database, create a user with a password (using GRANT), and create a database called "samigo".

   shell> mysql -u root -p

   mysql> create database samigo default character set utf8;
   mysql> grant all on samigo.* to 'sakaiuser'@'127.0.01' identified by 'sakaipassword';
   mysql> grant all on samigo.* to 'sakaiuser'@'localhost' identified  by 'sakaipassword';

4. Create tables and populate default settings.

   shell> mysql -u root -p samigo < ~/sam/ddl/samigo-ddl/01_schema_mysql.sql --force
   shell> mysql -u root -p samigo < ~/sam/ddl/samigo-ddl/02_defaultSetUp_mysql.sql  --force

The path you use for the sql files may vary.

5. Now, update the database settings in samigo.xml.

   db.driverClassName=com.mysql.jdbc.Driver
   db.url=jdbc:mysql://your.server.edu:3306/samigo?user=youname&password=yourpassword&autoReconnect=true
   db.username=yourname
   db.password=yourpassword

   The url, username and password should have the values required
   given your installation of MySQL.

6. Modify the following properties in sam/build.properties as follows.
   # db support (oracle,hsqldb,mysql)
   samigo.db=mysql

7. Specify the correct RDBMS dialect in both the
   sam/hibernate-hbm/hibernate.properties and sam/hibernate.properties
   files. Samigo v1.5 contains its predecessor, the Navigo project, so
   it "appears" to have some duplicated configuration files. In Samigo
   v2.0, Navigo will be excluded from the distribution.

   The dialect for MySQL is: net.sf.hibernate.dialect.MySQLDialect

8. Download the JDBC driver "Connector/J 3.1" from
    http://dev.mysql.com/downloads/connector/j/3.1.html and copy it to
    sam/lib/ before compiling and deploying samigo.  You may also need
    to copy it to shared/lib or common/lib in your tomcat
    installation.


***** Support for Oracle *****

1 Make sure there is an accessible installation of Oracle.

2. Set up a user in your Oracle database. Then create the tables by running 
   the following ddl files in your SQLPlus in this order.
   i.  01_schema_oracle.sql which contains SQL statement for creating all the 
       required tables and sequence, please be sure to commit.
   ii. 02_defaultSetUp_oracle.sql which contains SQL statements for populating 
       basic information (such as Type and  a Default Template) required for 
       running Samigo.
   Both files are located inside sam/ddl/samigo-ddl/. You may want to capture the output to 
   check that all the tables and sequence are created accordingly.

3. Download Oracle JDBC Driver and install it in sam/lib/ directory
    Goto http://www.oracle.com/technology/software/tech/java/sqlj_jdbc/index.html,
    select 10g(10.1.0.2.0) drivers, complete the licensing agreement to download this
    driver: 
    ojdbc14.jar (1,352,918 bytes) - classes for use with JDK 1.4

4. Update the database settings in samigo.xml.

   db.driverClassName=oracle.jdbc.driver.OracleDriver
   db.url=jdbc:oracle:thin:@monster.oracle.org:1521:db
   db.username=sakaiuser
   db.password=sakaipassword

5. Modify the following properties in sam/build.properties as follows.
   # db support (oracle,hsqldb,mysql)
   samigo.db=oracle

6. Be sure that you specify the correct RDBMS dialect in both
   sam/hibernate-hbm/hibernate.properties and
   sam/hibernate.properties. Samigo v1.5 contains its predecessor, the
   Navigo project, so it "appears" to have duplicated configuration
   files. In Samigo v2.0, Navigo will be excluded from the
   distribution.

   The dialect for Oracle is: net.sf.hibernate.dialect.Oracel9Dialect.


***** Support for Hypersonic HSQLDB ******

We do not recommend using Hypersonic for a production installation of Samigo.

1. Download Hypersonic 1.7.x from http://hsqldb.sourceforge.net/ and
   follow the installation instruction

2. Add the following statement to sqltool.rc. If you have followed the
   HSQLDB installation instruction, this file should be placed at the
   HSQLDB Owner's home directory.

   urlid samigo
   url jdbc:hsqldb:hsql://your.server.edu:9001/samigo
   username sa
   password

   Note that we are using the user "sa" with password "" that came with the initial setup.  You 
   should use the user and password appropriate to your installation.

3. Create ~/hsqldb/samigo/server.properties and add the following statement.

   server.database.0=~/hsqldb/samigo
   server.dbname.0=samigo

4. Start the server with database "samigo" using these commands

   shell> cd hsqldb/lib
   shell> java -cp hsqldb.jar org.hsqldb.Server -database samigo &


5. Create tables and populate default settings using the SQL tool

   shell> cd hsqldb/lib
   shell> java -jar hsqldb.jar --rcfile ~/sqltool.rc samigo 

   cut and paste SQL stmt from ~/sam/ddl/samigo-ddl/01_schema_hsqldb.sql and
   ~/sam/ddl/samigo-ddl/02_defaultSetUp_hsqldb.sql to the "sql>" prompt

   Afterward, execute "select * from sam_type_t;". If it returns a list of rows back,
   then you have successfully set up the database.

6. Now, update the database settings in samigo.xml.  There is no need
   to specify the database name "samigo" as it is the only one that is
   up in step 4.

   db.driverClassName=org.hsqldb.jdbcDriver
   db.url=jdbc:hsqldb:hsql://your.server.edu:9001
   db.username=sa
   db.password=

8. Modify the following properties in sam/build.properties as follows.
   # db support (oracle,hsqldb,mysql)
   samigo.db=hsqldb

9. Be sure that you specify the correct RDBMS dialect in both
   sam/hibernate-hbm/hibernate.properties and
   sam/hibernate.properties. Samigo v1.5 contains its predecessor, the
   Navigo project, so it "appears" to have duplicated configuration
   files. In Samigo v2.0, Navigo will be excluded from the
   distribution.

   The dialect for HSQLDB is: hibernate.dialect net.sf.hibernate.dialect.HSQLDialect.

10. Copy hsqldb/lib/hsqldb.jar to sam/lib/ before compiling and deploying samigo.


$Header: /cvs/quickstart/qs-1-5-1/docs/QuickSource_SAMIGO_README.TXT,v 1.6 2005/05/26 18:24:59 dlhaines.umich.edu Exp $