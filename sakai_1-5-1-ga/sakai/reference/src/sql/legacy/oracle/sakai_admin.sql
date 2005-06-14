INSERT INTO ANNOUNCEMENT_CHANNEL VALUES('/announcement/channel/!admin/main',0,'<?xml version="1.0" encoding="UTF-8"?>\u000a<channel context="!admin" id="main">\u000a    <properties/>\u000a</channel>\u000a');
INSERT INTO CONTENT_COLLECTION VALUES('/group/!admin/','/group/','<?xml version="1.0" encoding="UTF-8"?>\u000a<collection id="/group/!admin/">\u000a    <properties>\u000a        <property enc="BASE64" name="CHEF:is-collection" value="dHJ1ZQ==&#xa;"/>\u000a        <property enc="BASE64" name="CHEF:creator" value="YWRtaW4=&#xa;"/>\u000a        <property enc="BASE64" name="DAV:displayname" value="QWRtaW5pc3RyYXRpb24gV29ya3NwYWNl&#xa;"/>\u000a        <property enc="BASE64" name="CHEF:modifiedby" value="YWRtaW4=&#xa;"/>\u000a        <property enc="BASE64" name="DAV:getlastmodified" value="MjAwNTAzMTYxODI4MDE2NjM=&#xa;"/>\u000a        <property enc="BASE64" name="DAV:creationdate" value="MjAwNTAzMTYxODI4MDE2NjM=&#xa;"/>\u000a    </properties>\u000a</collection>\u000a');
INSERT INTO SAKAI_REALM VALUES(SAKAI_REALM_SEQ.nextval,'/site/!admin',NULL,NULL,'admin','admin',TO_TIMESTAMP('20050217031429406','YYYYMMDDHHMISSFF3'),TO_TIMESTAMP('20050316062801153','YYYYMMDDHHMISSFF3'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.delete.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.delete.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.read.drafts'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.revise.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'asn.delete'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'asn.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'asn.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'asn.revise'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.delete'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.revise'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.delete.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.delete.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.revise.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.delete'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.revise'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.dis.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.dis.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.dis.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.dis.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.path.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.path.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.path.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.step.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.step.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.step.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.step.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.delete.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.delete.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.new.topic'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.read.drafts'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.revise.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.delete.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.delete.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.revise.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit.unp'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'asn.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'asn.submit'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.dis.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.path.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.step.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dropbox.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit'));
INSERT INTO SAKAI_REALM_RL_GR VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	'admin',(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),'1','0');
INSERT INTO SAKAI_SITE VALUES('!admin','Administration Workspace',NULL,NULL,'Administration Workspace',NULL,NULL,NULL,1,'0','0',NULL,'admin','admin',TO_TIMESTAMP('20050316062801400','YYYYMMDDHHMISSFF3'),TO_TIMESTAMP('20050316062801400','YYYYMMDDHHMISSFF3'),'0','0');
INSERT INTO SAKAI_SITE_PAGE VALUES('1110997680999-1','!admin','Home','0',1);
INSERT INTO SAKAI_SITE_PAGE VALUES('1110997681000-4','!admin','Users','0',2);
INSERT INTO SAKAI_SITE_PAGE VALUES('1110997681001-10','!admin','Realms','0',5);
INSERT INTO SAKAI_SITE_PAGE VALUES('1110997681001-6','!admin','Aliases','0',3);
INSERT INTO SAKAI_SITE_PAGE VALUES('1110997681001-8','!admin','Sites','0',4);
INSERT INTO SAKAI_SITE_PAGE VALUES('1110997681002-12','!admin','Worksite Setup','0',6);
INSERT INTO SAKAI_SITE_PAGE VALUES('1110997681002-14','!admin','MOTD','0',7);
INSERT INTO SAKAI_SITE_PAGE VALUES('1110997681002-16','!admin','Resources','0',8);
INSERT INTO SAKAI_SITE_PAGE VALUES('1110997681003-18','!admin','On-Line','0',9);
INSERT INTO SAKAI_SITE_PAGE VALUES('1110997681003-20','!admin','Memory','0',10);
INSERT INTO SAKAI_SITE_PAGE VALUES('1110997681003-22','!admin','Site Archive','0',11);
INSERT INTO SAKAI_SITE_TOOL VALUES('1110997680999-2','1110997680999-1','!admin','chef.motd',1,'Message of the Day','null');
INSERT INTO SAKAI_SITE_TOOL VALUES('1110997681000-3','1110997680999-1','!admin','chef.iframe',2,'My Workspace Information','null');
INSERT INTO SAKAI_SITE_TOOL VALUES('1110997681001-5','1110997681000-4','!admin','chef.users',1,'Users','null');
INSERT INTO SAKAI_SITE_TOOL VALUES('1110997681001-7','1110997681001-6','!admin','chef.aliases',1,'Aliases','null');
INSERT INTO SAKAI_SITE_TOOL VALUES('1110997681001-9','1110997681001-8','!admin','chef.sites',1,'Sites','null');
INSERT INTO SAKAI_SITE_TOOL VALUES('1110997681002-11','1110997681001-10','!admin','chef.realms',1,'Realms','null');
INSERT INTO SAKAI_SITE_TOOL VALUES('1110997681002-13','1110997681002-12','!admin','chef.sitesetupgeneric',1,'Worksite Setup','null');
INSERT INTO SAKAI_SITE_TOOL VALUES('1110997681002-15','1110997681002-14','!admin','chef.announcements',1,'MOTD','null');
INSERT INTO SAKAI_SITE_TOOL VALUES('1110997681003-17','1110997681002-16','!admin','chef.resources',1,'Resources','null');
INSERT INTO SAKAI_SITE_TOOL VALUES('1110997681003-19','1110997681003-18','!admin','chef.presence',1,'On-Line','null');
INSERT INTO SAKAI_SITE_TOOL VALUES('1110997681003-21','1110997681003-20','!admin','chef.memory',1,'Memory','null');
INSERT INTO SAKAI_SITE_TOOL VALUES('1110997681004-23','1110997681003-22','!admin','chef.archive',1,'Site Archive / Import','null');
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!admin','1110997681000-3','special','workspace');
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!admin','1110997681002-15','channel','/announcement/channel/!site/motd');
INSERT INTO SAKAI_SITE_TOOL_PROPERTY VALUES('!admin','1110997681003-17','home','/');
INSERT INTO SAKAI_SITE_USER VALUES('!admin','admin',-1);
