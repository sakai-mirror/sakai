\* true

/* Setup the HSQLDB username and password for Sakai.              */
/*   To change it, change the username and password here AND in   */
/*   /usr/local/sakai/sakai.properties                            */


CREATE USER sakaiuser PASSWORD "sakaipassword" ADMIN;

/* Create all the tables                                          */

\i sakai_alias.sql
\i chef_announcement.sql
\i chef_assignment.sql
\i chef_calendar.sql
\i chef_chat.sql
\i sakai_cluster.sql
\i chef_content.sql
\i chef_digest.sql
\i chef_discussion.sql
\i chef_event.sql
\i chef_id.sql
\i chef_mailarchive.sql
\i chef_notification.sql
\i chef_preferences.sql
\i chef_presence.sql
\i sakai_realm.sql
\i sakai_realm_populate.sql
\i chef_session.sql
\i sakai_site.sql
\i sakai_site_populate.sql
\i sakai_locks.sql
\i sakai_user.sql
\i sakai_resources.sql
\i sakai_admin.sql



COMMIT;
SHUTDOWN SCRIPT;



