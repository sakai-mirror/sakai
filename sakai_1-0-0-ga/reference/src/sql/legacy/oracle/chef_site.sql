-----------------------------------------------------------------------------
-- CHEF_SITE (sakai)
-----------------------------------------------------------------------------

DROP TABLE CHEF_SITE;

CREATE TABLE CHEF_SITE
(
SITE_ID VARCHAR2 (255) NOT NULL,
XML LONG
);

CREATE UNIQUE INDEX CHEF_SITE_INDEX ON CHEF_SITE
(
SITE_ID
);

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
