-- CHEF user ids are case sensitive, Sakai's are case insensitive and stored in lower case.
-- We need any record with upper case user_id converted to have a lower case user_id field.

-- Note: for CHEF compatibility, we need to make a copy of the records
--insert into chef_user (user_id, xml)
--	(select lower(user_id), xml from chef_user
--		where user_id != lower(user_id));
-- Note: this insert does not work due to the long xml fields

-- get the list of who got changes
select user_id, lower(user_id) from chef_user where user_id != lower(user_id) and lower(user_id) not in (select user_id from chef_user);

update chef_user set user_id=lower(user_id)
	where user_id != lower(user_id)
		and lower(user_id) not in (select user_id from chef_user);

-- run an app server running Sakai against your CHEF database with the legacy site service set thusly:
--  		<property name="regenerateIds"><value>true</value></property>
-- to assure unique page and tool ids in all sites.


-- Sakai uses new special realms - as defined in the chef_realm.sql

delete from CHEF_REALM where realm_id='/content/public/';
INSERT INTO CHEF_REALM VALUES ('/content/public/', '<?xml version="1.0" encoding="UTF-8"?>
<realm id="/content/public/">
<ability auth="auth" lock="content.read"/>
<ability anon="anon" lock="content.read"/>
</realm>
');

delete from CHEF_REALM where realm_id='/content/attachment/';
INSERT INTO CHEF_REALM VALUES ('/content/attachment/', '<?xml version="1.0" encoding="UTF-8"?>
<realm id="/content/attachment/">
<ability auth="auth" lock="content.new"/>
<ability auth="auth" lock="content.read"/>
<ability auth="auth" lock="content.revise"/>
<ability auth="auth" lock="content.delete"/>
<ability anon="anon" lock="content.read"/>
</realm>
');

delete from CHEF_REALM where realm_id='/announcement/channel/!site/motd';
INSERT INTO CHEF_REALM VALUES ('/announcement/channel/!site/motd', '<?xml version="1.0" encoding="UTF-8"?>
<realm id="/announcement/channel/!site/motd">
<ability auth="auth" lock="annc.read"/>
<ability anon="anon" lock="annc.read"/>
</realm>
');

delete from CHEF_REALM where realm_id='!site.template';
INSERT INTO CHEF_REALM VALUES ('!site.template', '<?xml version="1.0" encoding="UTF-8"?>
<realm id="!site.template">
<properties/>
<role id="access">
<ability lock="annc.read"/>
<ability lock="asn.read"/>
<ability lock="asn.submit"/>
<ability lock="chat.new"/>
<ability lock="chat.read"/>
<ability lock="disc.new"/>
<ability lock="disc.read"/>
<ability lock="disc.revise.own"/>
<ability lock="dropbox.own"/>
<ability lock="calendar.read"/>
<ability lock="content.read"/>
<ability lock="mail.read"/>
<ability lock="site.visit"/>
<ability lock="dis.status.read"/>
<ability lock="dis.status.del"/>
<ability lock="dis.dis.read"/>
<ability lock="dis.status.upd"/>
<ability lock="dis.path.upd"/>
<ability lock="dis.status.add"/>
<ability lock="dis.step.read"/>
</role>
<role id="maintain">
<ability lock="annc.new"/>
<ability lock="annc.read"/>
<ability lock="annc.revise.any"/>
<ability lock="annc.revise.own"/>
<ability lock="annc.delete.any"/>
<ability lock="annc.delete.own"/>
<ability lock="annc.read.drafts"/>
<ability lock="asn.delete"/>
<ability lock="asn.read"/>
<ability lock="asn.revise"/>
<ability lock="asn.new"/>
<ability lock="calendar.revise"/>
<ability lock="calendar.read"/>
<ability lock="calendar.delete"/>
<ability lock="calendar.new"/>
<ability lock="chat.new"/>
<ability lock="chat.revise.own"/>
<ability lock="chat.read"/>
<ability lock="chat.delete.any"/>
<ability lock="chat.delete.own"/>
<ability lock="chat.revise.any"/>
<ability lock="content.revise"/>
<ability lock="content.new"/>
<ability lock="content.read"/>
<ability lock="content.delete"/>
<ability lock="disc.delete.own"/>
<ability lock="disc.new"/>
<ability lock="disc.read"/>
<ability lock="disc.revise.own"/>
<ability lock="disc.new.topic"/>
<ability lock="disc.delete.any"/>
<ability lock="disc.read.drafts"/>
<ability lock="disc.revise.any"/>
<ability lock="mail.read"/>
<ability lock="mail.revise.own"/>
<ability lock="mail.revise.any"/>
<ability lock="mail.delete.own"/>
<ability lock="mail.delete.any"/>
<ability lock="realm.del"/>
<ability lock="realm.upd"/>
<ability lock="site.del"/>
<ability lock="site.visit"/>
<ability lock="site.upd"/>
<ability lock="site.visit.unp"/>
<ability lock="dis.status.del"/>
<ability lock="dis.dis.read"/>
<ability lock="dis.dis.upd"/>
<ability lock="dis.path.del"/>
<ability lock="dis.path.read"/>
<ability lock="dis.status.upd"/>
<ability lock="dis.path.add"/>
<ability lock="dis.dis.add"/>
<ability lock="dis.step.upd"/>
<ability lock="dis.step.del"/>
<ability lock="dis.status.add"/>
<ability lock="dis.status.read"/>
<ability lock="dis.dis.del"/>
<ability lock="dis.step.add"/>
<ability lock="dis.step.read"/>
</role>
</realm>
');

delete from CHEF_REALM where realm_id='!pubview';
INSERT INTO CHEF_REALM VALUES ('!pubview', '<?xml version="1.0" encoding="UTF-8"?>
<realm id="!pubview">
<role id="pubview">
<ability lock="calendar.read"/>
<ability lock="content.read"/>
<ability lock="annc.read"/>
<ability lock="chat.read"/>
<ability lock="disc.read"/>
<ability lock="mail.read"/>
<ability lock="site.visit"/>
</role>
</realm>
');

delete from CHEF_REALM where realm_id='!site.user';
INSERT INTO CHEF_REALM VALUES ('!site.user', '<?xml version="1.0" encoding="UTF-8"?>
<realm id="!site.user">
<properties/>
<role id="access">
<ability lock="annc.read"/>
<ability lock="calendar.read"/>
<ability lock="chat.new"/>
<ability lock="chat.read"/>
<ability lock="content.read"/>
<ability lock="disc.new"/>
<ability lock="disc.revise.own"/>
<ability lock="disc.read"/>
<ability lock="mail.read"/>
<ability lock="site.visit"/>
</role>
<role id="maintain">
<ability lock="annc.new"/>
<ability lock="annc.read"/>
<ability lock="annc.revise.any"/>
<ability lock="annc.revise.own"/>
<ability lock="annc.read.drafts"/>
<ability lock="annc.delete.any"/>
<ability lock="annc.delete.own"/>
<ability lock="calendar.revise"/>
<ability lock="calendar.read"/>
<ability lock="calendar.delete"/>
<ability lock="calendar.new"/>
<ability lock="chat.new"/>
<ability lock="chat.read"/>
<ability lock="chat.revise.own"/>
<ability lock="chat.revise.any"/>
<ability lock="chat.delete.any"/>
<ability lock="chat.delete.own"/>
<ability lock="content.revise"/>
<ability lock="content.new"/>
<ability lock="content.read"/>
<ability lock="content.delete"/>
<ability lock="disc.delete.own"/>
<ability lock="disc.new"/>
<ability lock="disc.read"/>
<ability lock="disc.revise.own"/>
<ability lock="disc.read.drafts"/>
<ability lock="disc.revise.any"/>
<ability lock="disc.new.topic"/>
<ability lock="disc.delete.any"/>
<ability lock="mail.read"/>
<ability lock="mail.delete.any"/>
<ability lock="mail.revise.any"/>
<ability lock="mail.delete.own"/>
<ability lock="mail.revise.own"/>
<ability lock="realm.del"/>
<ability lock="realm.upd"/>
<ability lock="site.visit"/>
<ability lock="site.visit.unp"/>
<ability lock="site.upd"/>
</role>
</realm>
');

-- Note: this is setup to NOT give users site.add- add this if you want it
-- <ability auth="auth" lock="site.add"/>
delete from CHEF_REALM where realm_id='!user.template';
INSERT INTO CHEF_REALM VALUES ('!user.template', '<?xml version="1.0" encoding="UTF-8"?>
<realm id="!user.template">
<properties/>
<ability auth="auth" lock="site.add.usersite"/>
<ability anon="anon" lock="user.add"/>
<ability auth="auth" lock="user.add"/>
<ability auth="auth" lock="realm.add"/>
<ability auth="auth" lock="user.upd.own"/>
<ability auth="auth" lock="prefs.add"/>
<ability auth="auth" lock="prefs.upd"/>
<ability auth="auth" lock="prefs.del"/>
<ability auth="auth" lock="realm.upd.own"/>
</realm>
');

-- Note: this is setup to give users site.add - will be used by user with the "maintain" type
delete from CHEF_REALM where realm_id='!user.template.maintain';
INSERT INTO CHEF_REALM VALUES ('!user.template.maintain', '<?xml version="1.0" encoding="UTF-8"?>
<realm id="!user.template.maintain">
<properties/>
<ability auth="auth" lock="site.add.usersite"/>
<ability anon="anon" lock="user.add"/>
<ability auth="auth" lock="user.add"/>
<ability auth="auth" lock="realm.add"/>
<ability auth="auth" lock="user.upd.own"/>
<ability auth="auth" lock="prefs.add"/>
<ability auth="auth" lock="prefs.upd"/>
<ability auth="auth" lock="prefs.del"/>
<ability auth="auth" lock="realm.upd.own"/>
<ability auth="auth" lock="site.add"/>
</realm>
');

delete from CHEF_REALM where realm_id='/site/!gateway';
INSERT INTO CHEF_REALM VALUES ('/site/!gateway', '<?xml version="1.0" encoding="UTF-8"?>
<realm id="/site/!gateway">
<properties />
<ability anon="anon" lock="site.visit"/>
<ability auth="auth" lock="site.visit"/>
</realm>
');

delete from CHEF_REALM where realm_id='!site.helper';
INSERT INTO CHEF_REALM VALUES ('!site.helper', '<?xml version="1.0" encoding="UTF-8"?>
<realm id="!site.helper">
<properties/>
<role id="maintain">
<ability lock="realm.del"/>
<ability lock="realm.upd"/>
</role>
</realm>
');

delete from CHEF_REALM where realm_id='/site/!error';
INSERT INTO CHEF_REALM VALUES ('/site/!error', '<?xml version="1.0" encoding="UTF-8"?>
<realm id="/site/!error">
<properties />
<ability anon="anon" lock="site.visit"/>
<ability auth="auth" lock="site.visit"/>
</realm>
');

-- Sakai no longer uses these realms; they can be deleted if you don't need CHEF compatibliity:
-- "~", "@", "/site/", "/realm"
delete from CHEF_REALM where realm_id='~';
delete from CHEF_REALM where realm_id='@';
delete from CHEF_REALM where realm_id='/site/';
delete from CHEF_REALM where realm_id='/site/!error';


-- Sakai defines new special sites

delete from CHEF_SITE where site_id='!gateway';
INSERT INTO CHEF_SITE VALUES ('!gateway', '<?xml version="1.0" encoding="UTF-8"?>
<site description-enc="VGhlIEdhdGV3YXkgU2l0ZQ==&#xa;" id="!gateway"
joinable="false" status="2" title="Gateway">
<properties>
<property enc="BASE64" name="CHEF:modifiedby" value="YWRtaW4=&#xa;"/>
<property enc="BASE64" name="DAV:getlastmodified" value="MjAwMzExMjYwMzQ1MjIwNjE=&#xa;"/>
</properties>
<pages>
<page id="!gateway-100" layout="0" title="Welcome">
<properties/>
<tools>
<tool id="!gateway-110" title="Message of the day" toolId="chef.motd">
<properties/>
</tool>
<tool id="!gateway-120" title="Welcome!" toolId="chef.iframe">
<properties>
<property enc="BASE64" name="special" value="c2l0ZQ==&#xa;"/>
</properties>
</tool>
</tools>
</page>
<page id="!gateway-200" layout="0" title="About">
<properties/>
<tools>
<tool id="!gateway-210" title="About" toolId="chef.iframe">
<properties>
<property enc="BASE64" name="height" value="NTAwcHg=&#xa;"/>
<property enc="BASE64" name="source" value="L2NvbnRlbnQvcHVibGljL2dhdGV3YXkvYWJvdXQuaHRtbA==&#xa;"/>
</properties>
</tool>
</tools>
</page>
<page id="!gateway-300" layout="0" title="Features">
<properties/>
<tools>
<tool id="!gateway-310" title="Features" toolId="chef.iframe">
<properties>
<property enc="BASE64" name="height" value="NTAwcHg=&#xa;"/>
<property enc="BASE64" name="source" value="L2NvbnRlbnQvcHVibGljL2dhdGV3YXkvZmVhdHVyZXMuaHRtbA==&#xa;"/>
</properties>
</tool>
</tools>
</page>
<page id="!gateway-400" layout="0" title="Sites">
<properties/>
<tools>
<tool id="!gateway-410" title="Sites" toolId="chef.sitebrowser">
<properties/>
</tool>
</tools>
</page>
<page id="!gateway-500" layout="0" title="Training">
<properties/>
<tools>
<tool id="!gateway-510" title="Training" toolId="chef.iframe">
<properties>
<property enc="BASE64" name="height" value="NTAwcHg=&#xa;"/>
<property enc="BASE64" name="source" value="L2NvbnRlbnQvcHVibGljL2dhdGV3YXkvdHJhaW5pbmcuaHRtbA==&#xa;"/>
</properties>
</tool>
</tools>
</page>
<page id="!gateway-600" layout="0" title="Help">
<properties/>
<tools>
<tool id="!gateway-610" title="Help" toolId="chef.contactSupport">
<properties/>
</tool>
</tools>
</page>
<page id="!gateway-700" layout="0" title="New Account">
<properties/>
<tools>
<tool id="!gateway-710" title="New Account" toolId="chef.createuser">
<properties />
</tool>
</tools>
</page>
</pages>
</site>
');

delete from CHEF_SITE where site_id='!user';
INSERT INTO CHEF_SITE VALUES ('!user', '<?xml version="1.0" encoding="UTF-8"?>
<site description-enc="TXlXb3Jrc3BhY2UgU2l0ZQ==&#xa;" id="!user"
joinable="false" status="2" title="My Workspace">
<properties/>
<pages>
<page id="!user-100" layout="0" title="Home">
<properties/>
<tools>
<tool id="!user-110" title="Message of the Day" toolId="chef.motd">
<properties/>
</tool>
<tool id="!user-120" title="My Workspace Information" toolId="chef.iframe">
<properties>
<property enc="BASE64" name="special" value="d29ya3NwYWNl&#xa;"/>
</properties>
</tool>
</tools>
</page>
<page id="!user-200" layout="0" title="Membership">
<properties/>
<tools>
<tool id="!user-210" title="Membership" toolId="chef.membership">
<properties/>
</tool>
</tools>
</page>
<page id="!user-300" layout="0" title="Schedule">
<properties/>
<tools>
<tool id="!user-310" title="Schedule" toolId="chef.schedule">
<properties/>
</tool>
</tools>
</page>
<page id="!user-400" layout="0" title="Resources">
<properties/>
<tools>
<tool id="!user-410" title="Resources" toolId="chef.resources">
<properties/>
</tool>
</tools>
</page>
<page id="!user-450" layout="0" title="Announcements">
<properties/>
<tools>
<tool id="!user-455" title="Announcements" toolId="chef.announcements">
<properties/>
</tool>
</tools>
</page>
<page id="!user-500" layout="0" title="Worksite Setup">
<properties/>
<tools>
<tool id="!user-510" title="Worksite Setup" toolId="chef.sitesetupgeneric">
<properties/>
</tool>
</tools>
</page>
<page id="!user-600" layout="0" title="Preferences">
<properties/>
<tools>
<tool id="!user-610" title="Preferences" toolId="chef.noti.prefs">
<properties/>
</tool>
</tools>
</page>
<page id="!user-700" layout="0" title="Account">
<properties/>
<tools>
<tool id="!user-710" title="Account" toolId="chef.singleuser">
<properties>
<property name="include-password" value="true"/>
</properties>
</tool>
</tools>
</page>
</pages>
</site>
');

delete from CHEF_SITE where site_id='~admin';
INSERT INTO CHEF_SITE VALUES ('~admin', '<?xml version="1.0" encoding="UTF-8"?>
<site description-enc="QWRtaW5pc3RyYXRpb24gV29ya3NwYWNl&#xa;"
id="~admin" joinable="false" status="2" title="Administration Workspace">
<properties/>
<pages>
<page id="~admin-100" layout="0" title="Home">
<properties/>
<tools>
<tool id="~admin-110" title="Message of the Day" toolId="chef.motd">
<properties/>
</tool>
<tool id="~admin-120"
title="My Workspace Information" toolId="chef.iframe">
<properties>
<property enc="BASE64" name="special" value="d29ya3NwYWNl&#xa;"/>
</properties>
</tool>
</tools>
</page>
<page id="~admin-200" layout="0" title="Users">
<properties/>
<tools>
<tool id="~admin-210" title="Users" toolId="chef.users">
<properties/>
</tool>
</tools>
</page>
<page id="~admin-250" layout="0" title="Aliases">
<properties/>
<tools>
<tool id="~admin-260" title="Aliases" toolId="chef.aliases">
<properties/>
</tool>
</tools>
</page>
<page id="~admin-300" layout="0" title="Sites">
<properties/>
<tools>
<tool id="~admin-310" title="Sites" toolId="chef.sites">
<properties/>
</tool>
</tools>
</page>
<page id="~admin-350" layout="0" title="Realms">
<properties/>
<tools>
<tool id="~admin-355" title="Realms" toolId="chef.realms">
<properties/>
</tool>
</tools>
</page>
<page id="~admin-360" layout="0" title="Worksite Setup">
<properties/>
<tools>
<tool id="~admin-365" title="Worksite Setup" toolId="chef.sitesetupgeneric">
<properties/>
</tool>
</tools>
</page>
<page id="~admin-400" layout="0" title="MOTD">
<properties/>
<tools>
<tool id="~admin-410" title="MOTD" toolId="chef.announcements">
<properties>
<property enc="BASE64" name="channel" value="L2Fubm91bmNlbWVudC9jaGFubmVsLyFzaXRlL21vdGQ=&#xa;"/>
</properties>
</tool>
</tools>
</page>
<page id="~admin-500" layout="0" title="Resources">
<properties/>
<tools>
<tool id="~admin-510" title="Resources" toolId="chef.resources">
<properties>
<property enc="BASE64" name="home" value="Lw==&#xa;"/>
</properties>
</tool>
</tools>
</page>
<page id="~admin-600" layout="0" title="On-Line">
<properties/>
<tools>
<tool id="~admin-610" title="On-Line" toolId="chef.presence">
<properties/>
</tool>
</tools>
</page>
<page id="~admin-700" layout="0" title="Memory">
<properties/>
<tools>
<tool id="~admin-710" title="Memory" toolId="chef.memory">
<properties/>
</tool>
</tools>
</page>
<page id="~admin-900" layout="0" title="Site Archive">
<properties/>
<tools>
<tool id="~admin-910" title="Site Archive / Import" toolId="chef.archive">
<properties/>
</tool>
</tools>
</page>
</pages>
</site>
');

delete from CHEF_SITE where site_id='!admin';
INSERT INTO CHEF_SITE VALUES ('!admin', '<?xml version="1.0" encoding="UTF-8"?>
<site description-enc="QWRtaW5pc3RyYXRpb24gV29ya3NwYWNl&#xa;"
id="!admin" joinable="false" status="2" title="Administration Workspace">
<properties/>
<pages>
<page id="!admin-100" layout="0" title="Home">
<properties/>
<tools>
<tool id="!admin-110" title="Message of the Day" toolId="chef.motd">
<properties/>
</tool>
<tool id="!admin-120"
title="My Workspace Information" toolId="chef.iframe">
<properties>
<property enc="BASE64" name="special" value="d29ya3NwYWNl&#xa;"/>
</properties>
</tool>
</tools>
</page>
<page id="!admin-200" layout="0" title="Users">
<properties/>
<tools>
<tool id="!admin-210" title="Users" toolId="chef.users">
<properties/>
</tool>
</tools>
</page>
<page id="!admin-250" layout="0" title="Aliases">
<properties/>
<tools>
<tool id="!admin-260" title="Aliases" toolId="chef.aliases">
<properties/>
</tool>
</tools>
</page>
<page id="!admin-300" layout="0" title="Sites">
<properties/>
<tools>
<tool id="!admin-310" title="Sites" toolId="chef.sites">
<properties/>
</tool>
</tools>
</page>
<page id="!admin-350" layout="0" title="Realms">
<properties/>
<tools>
<tool id="!admin-355" title="Realms" toolId="chef.realms">
<properties/>
</tool>
</tools>
</page>
<page id="!admin-360" layout="0" title="Worksite Setup">
<properties/>
<tools>
<tool id="!admin-365" title="Worksite Setup" toolId="chef.sitesetupgeneric">
<properties/>
</tool>
</tools>
</page>
<page id="!admin-400" layout="0" title="MOTD">
<properties/>
<tools>
<tool id="!admin-410" title="MOTD" toolId="chef.announcements">
<properties>
<property enc="BASE64" name="channel" value="L2Fubm91bmNlbWVudC9jaGFubmVsLyFzaXRlL21vdGQ=&#xa;"/>
</properties>
</tool>
</tools>
</page>
<page id="!admin-500" layout="0" title="Resources">
<properties/>
<tools>
<tool id="!admin-510" title="Resources" toolId="chef.resources">
<properties>
<property enc="BASE64" name="home" value="Lw==&#xa;"/>
</properties>
</tool>
</tools>
</page>
<page id="!admin-600" layout="0" title="On-Line">
<properties/>
<tools>
<tool id="!admin-610" title="On-Line" toolId="chef.presence">
<properties/>
</tool>
</tools>
</page>
<page id="!admin-700" layout="0" title="Memory">
<properties/>
<tools>
<tool id="!admin-710" title="Memory" toolId="chef.memory">
<properties/>
</tool>
</tools>
</page>
<page id="!admin-900" layout="0" title="Site Archive">
<properties/>
<tools>
<tool id="!admin-910" title="Site Archive / Import" toolId="chef.archive">
<properties/>
</tool>
</tools>
</page>
</pages>
</site>
');

delete from CHEF_SITE where site_id='!worksite';
INSERT INTO CHEF_SITE VALUES ('!worksite', '<?xml version="1.0" encoding="UTF-8"?>
<site icon="/content/public/pig.gif" id="!worksite"
info="/content/public/site_home.html" joinable="false"
joiner-role="access" status="1" title="worksite">
<properties>
<property enc="BASE64" name="CHEF:creator" value="YWRtaW4=&#xa;"/>
<property enc="BASE64" name="CHEF:modifiedby" value="YWRtaW4=&#xa;"/>
<property enc="BASE64" name="DAV:getlastmodified" value="MjAwMzA2MjQxMjEwNTM1OTc=&#xa;"/>
<property enc="BASE64" name="DAV:creationdate" value="MjAwMzA2MjQwNDE1MDg4NTE=&#xa;"/>
</properties>
<pages>
<page id="!worksite-100" layout="1" title="Home">
<properties/>
<tools>
<tool id="!worksite-110" layoutHints="0,0"
title="My Workspace Information" toolId="chef.iframe">
<properties>
<property enc="BASE64" name="height" value="MTAwcHg=&#xa;"/>
<property enc="BASE64" name="special" value="d29ya3NwYWNl&#xa;"/>
<property enc="BASE64" name="source" value=""/>
</properties>
</tool>
<tool id="!worksite-120" layoutHints="0,1"
title="Recent Announcements" toolId="chef.synoptic.announcement">
<properties/>
</tool>
<tool id="!worksite-130" layoutHints="1,1"
title="Recent Discussion Items" toolId="chef.synoptic.discussion">
<properties/>
</tool>
<tool id="!worksite-140" layoutHints="2,1"
title="Recent Chat Messages" toolId="chef.synoptic.chat">
<properties/>
</tool>
</tools>
</page>
<page id="!worksite-200" layout="0" title="Schedule">
<properties/>
<tools>
<tool id="!worksite-210" title="Schedule" toolId="chef.schedule">
<properties/>
</tool>
</tools>
</page>
<page id="!worksite-300" layout="0" title="Announcements">
<properties/>
<tools>
<tool id="!worksite-310" title="Announcements" toolId="chef.announcements">
<properties/>
</tool>
</tools>
</page>
<page id="!worksite-400" layout="0" title="Resources">
<properties/>
<tools>
<tool id="!worksite-410" title="Resources" toolId="chef.resources">
<properties/>
</tool>
</tools>
</page>
<page id="!worksite-500" layout="0" title="Discussion">
<properties/>
<tools>
<tool id="!worksite-510" title="Discussion" toolId="chef.discussion">
<properties>
<property enc="BASE64" name="category" value="ZmFsc2U=&#xa;"/>
</properties>
</tool>
</tools>
</page>
<page id="!worksite-600" layout="0" title="Assignments">
<properties/>
<tools>
<tool id="!worksite-610" title="Assignments" toolId="chef.assignment">
<properties/>
</tool>
</tools>
</page>
<page id="!worksite-700" layout="0" title="Drop Box">
<properties/>
<tools>
<tool id="!worksite-710" title="Drop Box" toolId="chef.dropbox">
<properties>
<property enc="BASE64" name="resources_mode" value="ZHJvcGJveA==&#xa;"/>
</properties>
</tool>
</tools>
</page>
<page id="!worksite-800" layout="0" title="Chat">
<properties/>
<tools>
<tool id="!worksite-810" title="Chat" toolId="chef.chat">
<properties>
<property enc="BASE64" name="display-date" value="dHJ1ZQ==&#xa;"/>
<property enc="BASE64" name="filter-param" value="Mw==&#xa;"/>
<property enc="BASE64" name="display-time" value="dHJ1ZQ==&#xa;"/>
<property enc="BASE64" name="sound-alert" value="dHJ1ZQ==&#xa;"/>
<property enc="BASE64" name="filter-type" value="U2VsZWN0TWVzc2FnZXNCeVRpbWU=&#xa;"/>
<property enc="BASE64" name="display-user" value="dHJ1ZQ==&#xa;"/>
</properties>
</tool>
</tools>
</page>
<page id="!worksite-900" layout="0" title="Email Archive">
<properties/>
<tools>
<tool id="!worksite-910" title="Email Archive" toolId="chef.mailbox">
<properties/>
</tool>
</tools>
</page>
</pages>
</site>
');

delete from CHEF_SITE where site_id='!error';
INSERT INTO CHEF_SITE VALUES ('!error', '<?xml version="1.0" encoding="UTF-8"?>
<site id="!error"
joinable="false"
joiner-role="" status="2" title="Site Unavailable"
description = "The site you requested is not avaialable.">
<properties>
<property enc="BASE64" name="CHEF:creator" value="YWRtaW4=&#xa;"/>
<property enc="BASE64" name="CHEF:modifiedby" value="YWRtaW4=&#xa;"/>
<property enc="BASE64" name="DAV:getlastmodified" value="MjAwMzA2MjQxMjEwNTM1OTc=&#xa;"/>
<property enc="BASE64" name="DAV:creationdate" value="MjAwMzA2MjQwNDE1MDg4NTE=&#xa;"/>
</properties>
<pages>
<page id="!error-100" layout="1" title="Site Unavailable">
<properties/>
<tools>
<tool id="!error-110"
title="Site Unavailable" toolId="chef.iframe">
<properties>
<property name="height" value="400px"/>
<property enc="BASE64" name="special" value="d29ya3NpdGU=&#xa;"/>
</properties>
</tool>
</tools>
</page>
</pages>
</site>
');

-- Sakai no longer uses these sites:
-- ~anon  ~  
delete from CHEF_SITE where site_id='~';
delete from CHEF_SITE where site_id='@';


-- Dissertation has a new table

--CREATE TABLE DISSERTATION_GROUP
--(
--    GROUP_ID VARCHAR2 (99) NOT NULL,
--    XML LONG
--);

--CREATE UNIQUE INDEX DISSERTATION_GROUP_INDEX ON DISSERTATION_GROUP
--(
--	GROUP_ID
--);


-- new tables

@\dev\sakai\deploy\src\sql\legacy\oracle\sakai_locks.sql
@\dev\sakai\deploy\src\sql\legacy\oracle\sakai_alias.sql
@\dev\sakai\deploy\src\sql\legacy\oracle\sakai_user.sql
@\dev\sakai\deploy\src\sql\legacy\oracle\sakai_cluster.sql

-- we don't need the user records, they will get converted
delete from sakai_user where user_id = 'admin';
delete from sakai_user where user_id = 'postmaster';

-- don't need the old cluster table
drop table CHEF_CLUSTER cascade constraints;

-- message table new fields

ALTER TABLE ANNOUNCEMENT_MESSAGE ADD DRAFT CHAR (1);
ALTER TABLE ANNOUNCEMENT_MESSAGE ADD PUBVIEW CHAR (1);
ALTER TABLE ANNOUNCEMENT_MESSAGE ADD OWNER VARCHAR2 (99);

ALTER TABLE CHAT_MESSAGE ADD DRAFT CHAR (1);
ALTER TABLE CHAT_MESSAGE ADD PUBVIEW CHAR (1);
ALTER TABLE CHAT_MESSAGE ADD OWNER VARCHAR2 (99);

ALTER TABLE DISCUSSION_MESSAGE ADD DRAFT CHAR (1);
ALTER TABLE DISCUSSION_MESSAGE ADD PUBVIEW CHAR (1);
ALTER TABLE DISCUSSION_MESSAGE ADD OWNER VARCHAR2 (99);
ALTER TABLE DISCUSSION_MESSAGE ADD REPLY VARCHAR2 (32);
ALTER TABLE DISCUSSION_MESSAGE ADD CATEGORY VARCHAR2 (255);

ALTER TABLE MAILARCHIVE_MESSAGE ADD DRAFT CHAR (1);
ALTER TABLE MAILARCHIVE_MESSAGE ADD PUBVIEW CHAR (1);
ALTER TABLE MAILARCHIVE_MESSAGE ADD OWNER VARCHAR2 (99);

-- message table new indexes

DROP INDEX ANNOUNCEMENT_CHANNEL_INDEX;
DROP INDEX XIF1ANNOUNCEMENT_MESSAGE;
DROP INDEX IE_ANNC_MSG_CHANNEL;
DROP INDEX IE_ANNC_MSG_ATTRIB;
DROP INDEX IE_ANNC_MSG_DATE;
DROP INDEX IE_ANNC_MSG_DATE_DESC;

CREATE UNIQUE INDEX ANNOUNCEMENT_CHANNEL_INDEX ON ANNOUNCEMENT_CHANNEL
(
	CHANNEL_ID
);
CREATE INDEX IE_ANNC_MSG_CHANNEL ON ANNOUNCEMENT_MESSAGE
(
       CHANNEL_ID                     ASC
);
CREATE INDEX IE_ANNC_MSG_ATTRIB ON ANNOUNCEMENT_MESSAGE
(
       DRAFT                          ASC,
       PUBVIEW                        ASC,
       OWNER                          ASC
);
CREATE INDEX IE_ANNC_MSG_DATE ON ANNOUNCEMENT_MESSAGE
(
       MESSAGE_DATE                   ASC
);
CREATE INDEX IE_ANNC_MSG_DATE_DESC ON ANNOUNCEMENT_MESSAGE
(
       MESSAGE_DATE                   DESC
);


DROP INDEX CHAT_CHANNEL_INDEX;
DROP INDEX XIF1CHAT_MESSAGE;
DROP INDEX IE_CHAT_MESSAGE_CHANNEL;
DROP INDEX IE_CHAT_MESSAGE_ATTRIB;
DROP INDEX IE_CHAT_MESSAGE_DATE;
DROP INDEX IE_CHAT_MESSAGE_DATE_DESC;

CREATE INDEX IE_CHAT_MESSAGE_CHANNEL ON CHAT_MESSAGE
(
       CHANNEL_ID                     ASC
);
CREATE INDEX IE_CHAT_MESSAGE_ATTRIB ON CHAT_MESSAGE
(
       DRAFT                          ASC,
       PUBVIEW                        ASC,
       OWNER                          ASC
);
CREATE INDEX IE_CHAT_MESSAGE_DATE ON CHAT_MESSAGE
(
       MESSAGE_DATE                   ASC
);
CREATE INDEX IE_CHAT_MESSAGE_DATE_DESC ON CHAT_MESSAGE
(
       MESSAGE_DATE                   DESC
);
CREATE UNIQUE INDEX CHAT_CHANNEL_INDEX ON CHAT_CHANNEL
(
	CHANNEL_ID
);


DROP INDEX DISCUSSION_CHANNEL_INDEX;
DROP INDEX FK_DISC_MSG_CHANNEL;
DROP INDEX FK_DISC_MSG_REPLY;
DROP INDEX IE_DISC_MSG_ATTRIB;
DROP INDEX IE_DISC_MSG_DATE;
DROP INDEX IE_DISC_MSG_DATE_DESC;

CREATE INDEX FK_DISC_MSG_CHANNEL ON DISCUSSION_MESSAGE
(
       CHANNEL_ID                     ASC
);
CREATE INDEX FK_DISC_MSG_REPLY ON DISCUSSION_MESSAGE
(
       REPLY                          ASC
);
CREATE INDEX IE_DISC_MSG_ATTRIB ON DISCUSSION_MESSAGE
(
       DRAFT                          ASC,
       PUBVIEW                        ASC,
       OWNER                          ASC
);
CREATE INDEX IE_DISC_MSG_DATE ON DISCUSSION_MESSAGE
(
       MESSAGE_DATE                   ASC
);
CREATE INDEX IE_DISC_MSG_DATE_DESC ON DISCUSSION_MESSAGE
(
       MESSAGE_DATE                   DESC
);
CREATE INDEX IE_DISC_MSG_CATEGORY ON DISCUSSION_MESSAGE
(
       CATEGORY                       ASC
);
CREATE INDEX IE_DISC_MSG_REPLY ON DISCUSSION_MESSAGE
(
       REPLY                          ASC
);
CREATE UNIQUE INDEX DISCUSSION_CHANNEL_INDEX ON DISCUSSION_CHANNEL
(
	CHANNEL_ID
);


DROP INDEX MAILARCHIVE_CHANNEL_INDEX;
DROP INDEX XIF1MAILARCHIVE_MESSAGE;
DROP INDEX IE_MAILARC_MSG_CHAN;
DROP INDEX IE_MAILARC_MSG_ATTRIB;
DROP INDEX IE_MAILARC_MSG_DATE;
DROP INDEX IE_MAILARC_MSG_DATE_DESC;

CREATE INDEX IE_MAILARC_MSG_CHAN ON MAILARCHIVE_MESSAGE
(
       CHANNEL_ID                     ASC
);
CREATE INDEX IE_MAILARC_MSG_ATTRIB ON MAILARCHIVE_MESSAGE
(
       DRAFT                          ASC,
       PUBVIEW                        ASC,
       OWNER                          ASC
);
CREATE INDEX IE_MAILARC_MSG_DATE ON MAILARCHIVE_MESSAGE
(
       MESSAGE_DATE                   ASC
);
CREATE INDEX IE_MAILARC_MSG_DATE_DESC ON MAILARCHIVE_MESSAGE
(
       MESSAGE_DATE                   DESC
);
CREATE UNIQUE INDEX MAILARCHIVE_CHANNEL_INDEX ON MAILARCHIVE_CHANNEL
(
	CHANNEL_ID
);

-- old tables (after conversion!)

--DROP TABLE CHEF_CLUSTER CASCADE CONSTRAINTS;
--DROP TABLE CHEF_USER CASCADE CONSTRAINTS;
--DROP TABLE CHEF_ALIAS CASCADE CONSTRAINTS;
