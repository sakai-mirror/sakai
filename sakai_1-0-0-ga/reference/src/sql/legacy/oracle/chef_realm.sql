-----------------------------------------------------------------------------
-- CHEF_REALM
-----------------------------------------------------------------------------

DROP TABLE CHEF_REALM;

CREATE TABLE CHEF_REALM
(
REALM_ID VARCHAR2 (99) NOT NULL,
XML LONG
);

CREATE UNIQUE INDEX CHEF_REALM_INDEX ON CHEF_REALM
(
REALM_ID
);

INSERT INTO CHEF_REALM VALUES ('/content/public/', '<?xml version="1.0" encoding="UTF-8"?>
<realm id="/content/public/">
<ability auth="auth" lock="content.read"/>
<ability anon="anon" lock="content.read"/>
</realm>
');

INSERT INTO CHEF_REALM VALUES ('/content/attachment/', '<?xml version="1.0" encoding="UTF-8"?>
<realm id="/content/attachment/">
<ability auth="auth" lock="content.new"/>
<ability auth="auth" lock="content.read"/>
<ability auth="auth" lock="content.revise"/>
<ability auth="auth" lock="content.delete"/>
<ability anon="anon" lock="content.read"/>
</realm>
');

INSERT INTO CHEF_REALM VALUES ('/announcement/channel/!site/motd', '<?xml version="1.0" encoding="UTF-8"?>
<realm id="/announcement/channel/!site/motd">
<ability auth="auth" lock="annc.read"/>
<ability anon="anon" lock="annc.read"/>
</realm>
');

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

INSERT INTO CHEF_REALM VALUES ('/site/!gateway', '<?xml version="1.0" encoding="UTF-8"?>
<realm id="/site/!gateway">
<properties />
<ability anon="anon" lock="site.visit"/>
<ability auth="auth" lock="site.visit"/>
</realm>
');

INSERT INTO CHEF_REALM VALUES ('!site.helper', '<?xml version="1.0" encoding="UTF-8"?>
<realm id="!site.helper">
<properties/>
<role id="maintain">
<ability lock="realm.del"/>
<ability lock="realm.upd"/>
</role>
</realm>
');

INSERT INTO CHEF_REALM VALUES ('/site/!error', '<?xml version="1.0" encoding="UTF-8"?>
<realm id="/site/!error">
<properties />
<ability anon="anon" lock="site.visit"/>
<ability auth="auth" lock="site.visit"/>
</realm>
');
