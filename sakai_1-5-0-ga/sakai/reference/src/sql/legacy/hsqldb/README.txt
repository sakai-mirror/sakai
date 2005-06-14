To install HSQLDB into your Sakai instance:

The sakaidb.* files contain a startup database for Sakai.  If you wish to 
regenerate the startup database, run hsqldb_ddl.bat.

1. Install and test Sakai (from the latest CVS)
2. Copy the starter DB by running "maven conf_db"
   This copies HSQLDB server.properties, and HSQLDB database files sakaidb.*
3. Enable HSQLDB in /usr/local/sakai/sakai.properties (use the first sql.url)

The default installation connects directly to the 
Sakai HSQLDB database files in /usr/local/sakai/db.  If you would prefer to 
start up a separate HSQLDB server you can do so by running hsqldb_server.bat.
You can connect to the running server using hsqldb_client.bat.  You can 
cause Sakai to also use the networked HSQLDB server by changing 
sakai.properties.  Change the HSQLDB JDBC connection URL to:
sql.connect=jdbc:hsqldb:hsql://localhost/sakaidb





