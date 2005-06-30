-- This is the Oracle Sakai 1.5 -> 2.0 conversion script

-- new tables will be established by running auto-ddl.  If this is not desired, you must manually
-- create the tables for any new Sakai feature

-- tables were renamed
alter table CHEF_EVENT rename to SAKAI_EVENT;
alter table CHEF_DIGEST rename to SAKAI_DIGEST;
alter table CHEF_NOTIFICATION rename to SAKAI_NOTIFICATION;
alter table CHEF_PREFERENCES rename to SAKAI_PREFERENCES;
alter table CHEF_PRESENCE rename to SAKAI_PRESENCE;
alter table CHEF_SESSION rename to SAKAI_SESSION;
alter table SYLLABUS_DATA_T rename to SAKAI_SYLLABUS_DATA;
alter table SYLLABUS_T rename to SAKAI_SYLLABUS_ITEM;

-- sequences were renamed
rename CHEF_EVENT_SEQ to SAKAI_EVENT_SEQ;

-- and removed
DROP SEQUENCE CHEF_ID_SEQ;

--fields were expanded
alter table ANNOUNCEMENT_MESSAGE modify MESSAGE_ID VARCHAR2(36);
alter table CALENDAR_EVENT modify EVENT_ID VARCHAR2(36);
alter table CHAT_MESSAGE modify MESSAGE_ID VARCHAR2(36);
alter table DISCUSSION_MESSAGE modify MESSAGE_ID VARCHAR2(36);
alter table DISCUSSION_MESSAGE modify REPLY VARCHAR2(36);
alter table MAILARCHIVE_MESSAGE modify MESSAGE_ID VARCHAR2(36);
alter table SAKAI_EVENT modify SESSION_ID VARCHAR2(36);
alter table SAKAI_PRESENCE modify SESSION_ID VARCHAR2(36);
alter table SAKAI_SESSION modify SESSION_ID VARCHAR2(36);
alter table SAKAI_LOCKS modify USAGE_SESSION_ID VARCHAR2(36);

-- tool ids were changed
update SAKAI_SITE_TOOL
set REGISTRATION=concat('sakai', substr(REGISTRATION,5)) where UPPER(substr(REGISTRATION,1,4)) = 'CHEF';
update SAKAI_SITE_TOOL set REGISTRATION='sakai.preferences' where REGISTRATION='sakai.noti.prefs';
update SAKAI_SITE_TOOL set REGISTRATION='sakai.online' where REGISTRATION='sakai.presence';
update SAKAI_SITE_TOOL set REGISTRATION='sakai.siteinfo' where REGISTRATION='sakai.siteinfogeneric';
update SAKAI_SITE_TOOL set REGISTRATION='sakai.sitesetup' where REGISTRATION='sakai.sitesetupgeneric';
update SAKAI_SITE_TOOL set REGISTRATION='sakai.discussion' where REGISTRATION='sakai.threadeddiscussion';

/*
update SAKAI_SITE_TOOL set REGISTRATION='ctools.dissertation' where REGISTRATION='sakai.dissertation';
update SAKAI_SITE_TOOL set REGISTRATION='ctools.dissertation.upload' where REGISTRATION='sakai.dissertation.upload';
update SAKAI_SITE_TOOL set REGISTRATION='ctools.gradToolsHelp' where REGISTRATION='sakai.gradToolsHelp';
update SAKAI_SITE_TOOL set REGISTRATION='ctools.aboutGradTools' where REGISTRATION='sakai.aboutGradTools';
*/

-- optional: drop old skins, everyone re-starts as default
/*
update SAKAI_SITE set SKIN=null;
*/

-- change the old site types
update sakai_site set type='project' where type='CTNG-project';
update sakai_site set type='course' where type='CTNG-course';

-- optional: drop all user myWorkspaces, so they get new ones (with new stuff)
/*
delete from sakai_site_user where site_id like '~%' and site_id != '~admin';
delete from sakai_site_tool_property where site_id like '~%' and site_id != '~admin';
delete from sakai_site_tool where site_id like '~%' and site_id != '~admin';
delete from sakai_site_page where site_id like '~%' and site_id != '~admin';
delete from sakai_site where site_id like '~%' and site_id != '~admin';
*/

-- replace the gateway site
/*
delete from sakai_site_user where site_id = '!gateway';
delete from sakai_site_tool_property where site_id = '!gateway';
delete from sakai_site_tool where site_id = '!gateway';
delete from sakai_site_page where site_id = '!gateway';
delete from sakai_site where site_id like '!gateway';
INSERT INTO SAKAI_SITE VALUES('!gateway', 'Gateway', null, null, 'The Gateway Site', null, null, null, 1, 0, 0, '', null, null, null, null, 1, 0 );
UPDATE SAKAI_SITE SET MODIFIEDBY='admin' WHERE SITE_ID = '!gateway';
UPDATE SAKAI_SITE SET MODIFIEDON=TO_TIMESTAMP('20031126034522061','YYYYMMDDHHMISSFF3') WHERE SITE_ID = '!gateway';
INSERT INTO SAKAI_SITE_PAGE VALUES('!gateway-100', '!gateway', 'Welcome', '0', 1 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!gateway-110', '!gateway-100', '!gateway', 'sakai.motd', 1, 'Message of the day', NULL );
INSERT INTO SAKAI_SITE_TOOL VALUES('!gateway-120', '!gateway-100', '!gateway', 'sakai.iframe', 2, 'Welcome!', NULL );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!gateway', '!gateway-120', 'special', 'site' );
INSERT INTO SAKAI_SITE_PAGE VALUES('!gateway-200', '!gateway', 'About', '0', 2 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!gateway-210', '!gateway-200', '!gateway', 'sakai.iframe', 1, 'About', NULL );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!gateway', '!gateway-210', 'height', '500px' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!gateway', '!gateway-210', 'source', '/library/content/gateway/about.html' );
INSERT INTO SAKAI_SITE_PAGE VALUES('!gateway-300', '!gateway', 'Features', '0', 3 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!gateway-310', '!gateway-300', '!gateway', 'sakai.iframe', 1, 'Features', NULL );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!gateway', '!gateway-310', 'height', '500px' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!gateway', '!gateway-310', 'source', '/library/content/gateway/features.html' );
INSERT INTO SAKAI_SITE_PAGE VALUES('!gateway-400', '!gateway', 'Sites', '0', 4 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!gateway-410', '!gateway-400', '!gateway', 'sakai.sitebrowser', 1, 'Sites', NULL );
INSERT INTO SAKAI_SITE_PAGE VALUES('!gateway-500', '!gateway', 'Training', '0', 5 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!gateway-510', '!gateway-500', '!gateway', 'sakai.iframe', 1, 'Training', NULL );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!gateway', '!gateway-510', 'height', '500px' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!gateway', '!gateway-510', 'source', '/library/content/gateway/training.html' );
INSERT INTO SAKAI_SITE_PAGE VALUES('!gateway-600', '!gateway', 'New Account', '0', 6 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!gateway-610', '!gateway-600', '!gateway', 'sakai.createuser', 1, 'New Account', NULL );
*/

-- skin has changed, so most should have defaults and icons set
-- here's an example of finding old skins and changing them
/*
update SAKAI_SITE set SKIN=null, ICON_URL='/ctlib/icon/arc.gif' where skin='arc.css';
update SAKAI_SITE set SKIN=null, ICON_URL='/ctlib/icon/art.gif' where skin='art.css';
update SAKAI_SITE set SKIN=null, ICON_URL='/ctlib/icon/bus.gif' where skin='bus.css';
update SAKAI_SITE set SKIN=null where skin='chef.css';
update SAKAI_SITE set SKIN=null where skin='ctng.css';
update SAKAI_SITE set SKIN=null, ICON_URL='/ctlib/icon/den.gif' where skin='den.css';
update SAKAI_SITE set SKIN=null, ICON_URL='/ctlib/icon/edu.gif' where skin='edu.css';
update SAKAI_SITE set SKIN=null, ICON_URL='/ctlib/icon/eng.gif' where skin='eng.css';
update SAKAI_SITE set SKIN=null where skin='examp-u.css';
update SAKAI_SITE set SKIN=null where skin='glrc.css';
update SAKAI_SITE set SKIN=null where skin='hkitty.css';
update SAKAI_SITE set SKIN=null, ICON_URL='/ctlib/icon/inf.gif' where skin='inf.css';
update SAKAI_SITE set SKIN=null, ICON_URL='/ctlib/icon/kin.gif' where skin='kin.css';
update SAKAI_SITE set SKIN=null where skin='komisar.css';
update SAKAI_SITE set SKIN=null, ICON_URL='/ctlib/icon/law.gif' where skin='law';
update SAKAI_SITE set SKIN=null, ICON_URL='/ctlib/icon/law.gif' where skin='law.css';
update SAKAI_SITE set SKIN=null, ICON_URL='/ctlib/icon/lsa.gif' where skin='lsa';
update SAKAI_SITE set SKIN=null, ICON_URL='/ctlib/icon/lsa.gif' where skin='lsa.css';
update SAKAI_SITE set SKIN=null where skin='lynx.css';
update SAKAI_SITE set SKIN='med', ICON_URL='/ctlib/icon/med.jpg' where skin='med.css';
update SAKAI_SITE set SKIN=null, ICON_URL='/ctlib/icon/mus.gif' where skin='mus.css';
update SAKAI_SITE set SKIN=null, ICON_URL='/ctlib/icon/nre.gif' where skin='nre.css';
update SAKAI_SITE set SKIN=null, ICON_URL='/ctlib/icon/nur.gif' where skin='nur.css';
update SAKAI_SITE set SKIN=null, ICON_URL='/ctlib/icon/off.gif' where skin='off.css';
update SAKAI_SITE set SKIN=null, ICON_URL='/ctlib/icon/pha.gif' where skin='pha.css';
update SAKAI_SITE set SKIN=null where skin='pro.css';
update SAKAI_SITE set SKIN=null where skin='prp.css';
update SAKAI_SITE set SKIN=null, ICON_URL='/ctlib/icon/rac.gif' where skin='rac.css';
update SAKAI_SITE set SKIN=null, ICON_URL='/ctlib/icon/sph.gif' where skin='sph.css';
update SAKAI_SITE set SKIN=null, ICON_URL='/ctlib/icon/spp.gif' where skin='spp.css';
update SAKAI_SITE set SKIN=null, ICON_URL='/ctlib/icon/ssw.gif' where skin='ssw.css';
update SAKAI_SITE set SKIN=null where skin='um';
update SAKAI_SITE set SKIN=null where skin='um.css';
update SAKAI_SITE set SKIN=null where skin='sakai_core.css';
update SAKAI_SITE set SKIN=null, ICON_URL='/ctlib/icon/umd.gif' where skin='umd.css';
*/

-- GradTools specific conversions
/*
update sakai_site_tool_property set value='http://gradtools.umich.edu/about.html'
	where dbms_lob.substr( value, 4000, 1 ) ='/content/public/GradToolsInfo.html';
update sakai_site_tool_property set value='http://gradtools.umich.edu/help'
	where dbms_lob.substr( value, 4000, 1 ) ='https://coursetools.ummu.umich.edu/disstools/help.nsf';
*/