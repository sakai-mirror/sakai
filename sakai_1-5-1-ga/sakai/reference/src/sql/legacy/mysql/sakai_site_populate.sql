-----------------------------------------------------------------------------
-- INITIAL VALUES FOR SAKAI_SITE
-----------------------------------------------------------------------------

-- Load the set of bootstrap sites for Sakai.

-- If you wish to add sites for your installation you probably want add them
-- through the online tools provided by Sakai rather than by editing this file.

-- These are MySql compatible commands.

-- $Header: /cvs/sakai/reference/src/sql/legacy/mysql/sakai_site_populate.sql,v 1.3 2005/02/17 14:35:10 zqian.umich.edu Exp $

-- Set the time zone.

SET @time_zone = "+0:00";

-- Create site for the administrator.

INSERT INTO SAKAI_SITE VALUES('~admin', 'Administration Workspace', null, null, 'Administration Workspace', null, null, null, 1, 0, 0, '', null, null, null, null, 0, 1 );
INSERT INTO SAKAI_SITE_PAGE VALUES('~admin-100', '~admin', 'Home', '0', 1 );
INSERT INTO SAKAI_SITE_TOOL VALUES('~admin-110', '~admin-100', '~admin', 'chef.motd', 1, 'Message of the Day', 'null' );
INSERT INTO SAKAI_SITE_TOOL VALUES('~admin-120', '~admin-100', '~admin', 'chef.iframe', 2, 'My Workspace Information', 'null' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('~admin', '~admin-120', 'special', 'workspace' );
INSERT INTO SAKAI_SITE_PAGE VALUES('~admin-200', '~admin', 'Users', '0', 2 );
INSERT INTO SAKAI_SITE_TOOL VALUES('~admin-210', '~admin-200', '~admin', 'chef.users', 1, 'Users', 'null' );
INSERT INTO SAKAI_SITE_PAGE VALUES('~admin-250', '~admin', 'Aliases', '0', 3 );
INSERT INTO SAKAI_SITE_TOOL VALUES('~admin-260', '~admin-250', '~admin', 'chef.aliases', 1, 'Aliases', 'null' );
INSERT INTO SAKAI_SITE_PAGE VALUES('~admin-300', '~admin', 'Sites', '0', 4 );
INSERT INTO SAKAI_SITE_TOOL VALUES('~admin-310', '~admin-300', '~admin', 'chef.sites', 1, 'Sites', 'null' );
INSERT INTO SAKAI_SITE_PAGE VALUES('~admin-350', '~admin', 'Realms', '0', 5 );
INSERT INTO SAKAI_SITE_TOOL VALUES('~admin-355', '~admin-350', '~admin', 'chef.realms', 1, 'Realms', 'null' );
INSERT INTO SAKAI_SITE_PAGE VALUES('~admin-360', '~admin', 'Worksite Setup', '0', 6 );
INSERT INTO SAKAI_SITE_TOOL VALUES('~admin-365', '~admin-360', '~admin', 'chef.sitesetupgeneric', 1, 'Worksite Setup', 'null' );
INSERT INTO SAKAI_SITE_PAGE VALUES('~admin-400', '~admin', 'MOTD', '0', 7 );
INSERT INTO SAKAI_SITE_TOOL VALUES('~admin-410', '~admin-400', '~admin', 'chef.announcements', 1, 'MOTD', 'null' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('~admin', '~admin-410', 'channel', '/announcement/channel/!site/motd' );
INSERT INTO SAKAI_SITE_PAGE VALUES('~admin-500', '~admin', 'Resources', '0', 8 );
INSERT INTO SAKAI_SITE_TOOL VALUES('~admin-510', '~admin-500', '~admin', 'chef.resources', 1, 'Resources', 'null' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('~admin', '~admin-510', 'home', '/' );
INSERT INTO SAKAI_SITE_PAGE VALUES('~admin-600', '~admin', 'On-Line', '0', 9 );
INSERT INTO SAKAI_SITE_TOOL VALUES('~admin-610', '~admin-600', '~admin', 'chef.presence', 1, 'On-Line', 'null' );
INSERT INTO SAKAI_SITE_PAGE VALUES('~admin-700', '~admin', 'Memory', '0', 10 );
INSERT INTO SAKAI_SITE_TOOL VALUES('~admin-710', '~admin-700', '~admin', 'chef.memory', 1, 'Memory', 'null' );
INSERT INTO SAKAI_SITE_PAGE VALUES('~admin-900', '~admin', 'Site Archive', '0', 11 );
INSERT INTO SAKAI_SITE_TOOL VALUES('~admin-910', '~admin-900', '~admin', 'chef.archive', 1, 'Site Archive / Import', 'null' );

-- Create the !error site to be displayed when there is a problem accessing a site.

INSERT INTO SAKAI_SITE VALUES('!error', 'Site Unavailable', null, null, 'The site you requested is not available.', null, null, null, 1, 0, 0, '', null, null, null, null, 1, 0 );
UPDATE SAKAI_SITE SET CREATEDBY='admin' WHERE SITE_ID = '!error';
UPDATE SAKAI_SITE SET MODIFIEDBY='admin' WHERE SITE_ID = '!error';
UPDATE SAKAI_SITE SET MODIFIEDON='20030624121053597' WHERE SITE_ID = '!error';
UPDATE SAKAI_SITE SET CREATEDON='20030624041508851' WHERE SITE_ID = '!error';
-- UPDATE SAKAI_SITE SET MODIFIEDON=TO_TIMESTAMP('20030624121053597','YYYYMMDDHHMISSFF6') WHERE SITE_ID = '!error';
-- UPDATE SAKAI_SITE SET CREATEDON=TO_TIMESTAMP('20030624041508851','YYYYMMDDHHMISSFF6') WHERE SITE_ID = '!error';
INSERT INTO SAKAI_SITE_PAGE VALUES('!error-100', '!error', 'Site Unavailable', '1', 1 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!error-110', '!error-100', '!error', 'chef.iframe', 1, 'Site Unavailable', 'null' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!error', '!error-110', 'height', '400px' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!error', '!error-110', 'special', 'worksite' );

-- Create the !urlError site to be used when there is a problem understanding the user's request url.

INSERT INTO SAKAI_SITE VALUES('!urlError', 'Invalid URL', null, null, 'The URL you entered is invalid.  SOLUTIONS: Please check for spelling errors or typos.  Make sure you are using the right URL.  Type a URL to try again.', null, null, null, 1, 0, 0, '', null, null, null, null, 1, 0 );
UPDATE SAKAI_SITE SET CREATEDBY='admin' WHERE SITE_ID = '!urlError';
UPDATE SAKAI_SITE SET MODIFIEDBY='admin' WHERE SITE_ID = '!urlError';
UPDATE SAKAI_SITE SET MODIFIEDON='20030624121053597' WHERE SITE_ID = '!urlError';
UPDATE SAKAI_SITE SET CREATEDON='20030624041508851' WHERE SITE_ID = '!urlError';
--UPDATE SAKAI_SITE SET MODIFIEDON=TO_TIMESTAMP('20030624121053597','YYYYMMDDHHMISSFF3') WHERE SITE_ID = '!urlError';
--UPDATE SAKAI_SITE SET CREATEDON=TO_TIMESTAMP('20030624041508851','YYYYMMDDHHMISSFF3') WHERE SITE_ID = '!urlError';
INSERT INTO SAKAI_SITE_PAGE VALUES('!urlError-100', '!urlError', 'Invalid URL', '1', 1 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!urlError-110', '!urlError-100', '!urlError', 'chef.iframe', 1, 'Invalid URL', 'null' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!urlError', '!urlError-110', 'height', '400px' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!urlError', '!urlError-110', 'special', 'worksite' );

-- Create the !gateway site to be used for the user's initial view of Sakai.

INSERT INTO SAKAI_SITE VALUES('!gateway', 'Gateway', null, null, 'The Gateway Site', null, null, null, 1, 0, 0, '', null, null, null, null, 1, 0 );
UPDATE SAKAI_SITE SET MODIFIEDBY='admin' WHERE SITE_ID = '!gateway';
UPDATE SAKAI_SITE SET MODIFIEDON='20031126034522061' WHERE SITE_ID = '!gateway';
INSERT INTO SAKAI_SITE_PAGE VALUES('!gateway-100', '!gateway', 'Welcome', '0', 1 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!gateway-110', '!gateway-100', '!gateway', 'chef.motd', 1, 'Message of the day', 'null' );
INSERT INTO SAKAI_SITE_TOOL VALUES('!gateway-120', '!gateway-100', '!gateway', 'chef.iframe', 2, 'Welcome!', 'null' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!gateway', '!gateway-120', 'special', 'site' );
INSERT INTO SAKAI_SITE_PAGE VALUES('!gateway-200', '!gateway', 'About', '0', 2 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!gateway-210', '!gateway-200', '!gateway', 'chef.iframe', 1, 'About', 'null' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!gateway', '!gateway-210', 'height', '500px' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!gateway', '!gateway-210', 'source', '/content/public/gateway/about.html' );
INSERT INTO SAKAI_SITE_PAGE VALUES('!gateway-300', '!gateway', 'Features', '0', 3 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!gateway-310', '!gateway-300', '!gateway', 'chef.iframe', 1, 'Features', 'null' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!gateway', '!gateway-310', 'height', '500px' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!gateway', '!gateway-310', 'source', '/content/public/gateway/features.html' );
INSERT INTO SAKAI_SITE_PAGE VALUES('!gateway-400', '!gateway', 'Sites', '0', 4 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!gateway-410', '!gateway-400', '!gateway', 'chef.sitebrowser', 1, 'Sites', 'null' );
INSERT INTO SAKAI_SITE_PAGE VALUES('!gateway-500', '!gateway', 'Training', '0', 5 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!gateway-510', '!gateway-500', '!gateway', 'chef.iframe', 1, 'Training', 'null' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!gateway', '!gateway-510', 'height', '500px' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!gateway', '!gateway-510', 'source', '/content/public/gateway/training.html' );
INSERT INTO SAKAI_SITE_PAGE VALUES('!gateway-600', '!gateway', 'New Account', '0', 6 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!gateway-610', '!gateway-600', '!gateway', 'chef.createuser', 1, 'New Account', 'null' );

-- Create the !user site to be used as the template for a new user's site.

INSERT INTO SAKAI_SITE VALUES('!user', 'My Workspace', null, null, 'MyWorkspace Site', null, null, null, 1, 0, 0, '', null, null, null, null, 1, 0 );
INSERT INTO SAKAI_SITE_PAGE VALUES('!user-100', '!user', 'Home', '0', 1 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!user-110', '!user-100', '!user', 'chef.motd', 1, 'Message of the Day', 'null' );
INSERT INTO SAKAI_SITE_TOOL VALUES('!user-120', '!user-100', '!user', 'chef.iframe', 2, 'My Workspace Information', 'null' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!user', '!user-120', 'special', 'workspace' );
INSERT INTO SAKAI_SITE_PAGE VALUES('!user-200', '!user', 'Membership', '0', 2 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!user-210', '!user-200', '!user', 'chef.membership', 1, 'Membership', 'null' );
INSERT INTO SAKAI_SITE_PAGE VALUES('!user-300', '!user', 'Schedule', '0', 3 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!user-310', '!user-300', '!user', 'chef.schedule', 1, 'Schedule', 'null' );
INSERT INTO SAKAI_SITE_PAGE VALUES('!user-400', '!user', 'Resources', '0', 4 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!user-410', '!user-400', '!user', 'chef.resources', 1, 'Resources', 'null' );
INSERT INTO SAKAI_SITE_PAGE VALUES('!user-450', '!user', 'Announcements', '0', 5 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!user-455', '!user-450', '!user', 'chef.announcements', 1, 'Announcements', 'null' );
INSERT INTO SAKAI_SITE_PAGE VALUES('!user-500', '!user', 'Worksite Setup', '0', 6 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!user-510', '!user-500', '!user', 'chef.sitesetupgeneric', 1, 'Worksite Setup', 'null' );
INSERT INTO SAKAI_SITE_PAGE VALUES('!user-600', '!user', 'Preferences', '0', 7 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!user-610', '!user-600', '!user', 'chef.noti.prefs', 1, 'Preferences', 'null' );
INSERT INTO SAKAI_SITE_PAGE VALUES('!user-700', '!user', 'Account', '0', 8 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!user-710', '!user-700', '!user', 'chef.singleuser', 1, 'Account', 'null' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!user', '!user-710', 'include-password', 'true' );

-- Create the !worksite site.

INSERT INTO SAKAI_SITE VALUES('!worksite', 'worksite', null, null, null, '/content/public/pig.gif', '/content/public/site_home.html', null, 0, 0, 0, 'access', null, null, null, null, 1, 0 );
UPDATE SAKAI_SITE SET CREATEDBY='admin' WHERE SITE_ID = '!worksite';
UPDATE SAKAI_SITE SET MODIFIEDBY='admin' WHERE SITE_ID = '!worksite';
UPDATE SAKAI_SITE SET MODIFIEDON='20030624121053597' WHERE SITE_ID = '!worksite';
UPDATE SAKAI_SITE SET CREATEDON='20030624041508851' WHERE SITE_ID = '!worksite';
INSERT INTO SAKAI_SITE_PAGE VALUES('!worksite-100', '!worksite', 'Home', '1', 1 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!worksite-110', '!worksite-100', '!worksite', 'chef.iframe', 1, 'My Workspace Information', 'null' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!worksite', '!worksite-110', 'height', '100px' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!worksite', '!worksite-110', 'special', 'workspace' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!worksite', '!worksite-110', 'source', '' );
INSERT INTO SAKAI_SITE_TOOL VALUES('!worksite-120', '!worksite-100', '!worksite', 'chef.synoptic.announcement', 2, 'Recent Announcements', 'null' );
INSERT INTO SAKAI_SITE_TOOL VALUES('!worksite-130', '!worksite-100', '!worksite', 'chef.synoptic.discussion', 3, 'Recent Discussion Items', 'null' );
INSERT INTO SAKAI_SITE_TOOL VALUES('!worksite-140', '!worksite-100', '!worksite', 'chef.synoptic.chat', 4, 'Recent Chat Messages', 'null' );
INSERT INTO SAKAI_SITE_PAGE VALUES('!worksite-200', '!worksite', 'Schedule', '0', 2 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!worksite-210', '!worksite-200', '!worksite', 'chef.schedule', 1, 'Schedule', 'null' );
INSERT INTO SAKAI_SITE_PAGE VALUES('!worksite-300', '!worksite', 'Announcements', '0', 3 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!worksite-310', '!worksite-300', '!worksite', 'chef.announcements', 1, 'Announcements', 'null' );
INSERT INTO SAKAI_SITE_PAGE VALUES('!worksite-400', '!worksite', 'Resources', '0', 4 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!worksite-410', '!worksite-400', '!worksite', 'chef.resources', 1, 'Resources', 'null' );
INSERT INTO SAKAI_SITE_PAGE VALUES('!worksite-500', '!worksite', 'Discussion', '0', 5 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!worksite-510', '!worksite-500', '!worksite', 'chef.discussion', 1, 'Discussion', 'null' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!worksite', '!worksite-510', 'category', 'false' );
INSERT INTO SAKAI_SITE_PAGE VALUES('!worksite-600', '!worksite', 'Assignments', '0', 6 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!worksite-610', '!worksite-600', '!worksite', 'chef.assignment', 1, 'Assignments', 'null' );
INSERT INTO SAKAI_SITE_PAGE VALUES('!worksite-700', '!worksite', 'Drop Box', '0', 7 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!worksite-710', '!worksite-700', '!worksite', 'chef.dropbox', 1, 'Drop Box', 'null' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!worksite', '!worksite-710', 'resources_mode', 'dropbox' );
INSERT INTO SAKAI_SITE_PAGE VALUES('!worksite-800', '!worksite', 'Chat', '0', 8 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!worksite-810', '!worksite-800', '!worksite', 'chef.chat', 1, 'Chat', 'null' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!worksite', '!worksite-810', 'display-date', 'true' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!worksite', '!worksite-810', 'filter-param', '3' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!worksite', '!worksite-810', 'display-time', 'true' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!worksite', '!worksite-810', 'sound-alert', 'true' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!worksite', '!worksite-810', 'filter-type', 'SelectMessagesByTime' );
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!worksite', '!worksite-810', 'display-user', 'true' );
INSERT INTO SAKAI_SITE_PAGE VALUES('!worksite-900', '!worksite', 'Email Archive', '0', 9 );
INSERT INTO SAKAI_SITE_TOOL VALUES('!worksite-910', '!worksite-900', '!worksite', 'chef.mailbox', 1, 'Email Archive', 'null' );

-- end of bootstrap site list