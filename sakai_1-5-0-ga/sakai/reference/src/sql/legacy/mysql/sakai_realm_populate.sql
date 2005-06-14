-----------------------------------------------------------------------------
-- INITIAL VALUES FOR SAKAI_REALM
-----------------------------------------------------------------------------

SET @time_zone = "+0:00";

INSERT INTO SAKAI_REALM_ROLE VALUES (DEFAULT, '.anon');
INSERT INTO SAKAI_REALM_ROLE VALUES (DEFAULT, '.auth');
INSERT INTO SAKAI_REALM_ROLE VALUES (DEFAULT, 'maintain');
INSERT INTO SAKAI_REALM_ROLE VALUES (DEFAULT, 'access');
INSERT INTO SAKAI_REALM_ROLE VALUES (DEFAULT, 'pubview');

INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'annc.delete.any');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'annc.delete.own');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'annc.new');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'annc.read');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'annc.read.drafts');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'annc.revise.any');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'annc.revise.own');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'asn.delete');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'asn.new');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'asn.read');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'asn.revise');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'asn.submit');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'calendar.delete');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'calendar.new');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'calendar.read');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'calendar.revise');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'chat.delete.any');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'chat.delete.own');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'chat.new');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'chat.read');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'chat.revise.any');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'chat.revise.own');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'content.delete');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'content.new');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'content.read');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'content.revise');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.dis.add');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.dis.del');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.dis.read');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.dis.upd');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.path.add');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.path.del');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.path.read');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.path.upd');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.status.add');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.status.del');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.status.read');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.status.upd');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.step.add');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.step.del');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.step.read');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.step.upd');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'disc.delete.any');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'disc.delete.own');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'disc.new');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'disc.new.topic');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'disc.read');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'disc.read.drafts');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'disc.revise.any');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'disc.revise.own');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dropbox.own');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'mail.delete.any');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'mail.delete.own');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'mail.read');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'mail.revise.any');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'mail.revise.own');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'prefs.add');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'prefs.del');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'prefs.upd');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'realm.add');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'realm.del');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'realm.upd');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'realm.upd.own');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'site.add');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'site.add.usersite');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'site.del');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'site.upd');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'site.visit');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'site.visit.unp');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'user.add');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'user.upd.own');

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '!site.helper', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.helper'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'), 
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.helper'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'), 
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.del'));

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '!site.user', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.revise.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.read.drafts'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.delete.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.delete.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.revise'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.delete'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.revise.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.delete.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.delete.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.revise'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.delete'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.delete.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.read.drafts'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.revise.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.new.topic'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.delete.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.delete.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.revise.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.delete.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit.unp'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.upd'));

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '!user.template', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.anon'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.add.usersite'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.upd.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.upd.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.del'));

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '!user.template.guest', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.guest'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.anon'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.guest'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.add.usersite'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.guest'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.guest'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.guest'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.upd.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.guest'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.upd.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.guest'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.guest'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.guest'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.del'));

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '!user.template.maintain', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.maintain'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.anon'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.maintain'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.add.usersite'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.maintain'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.maintain'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.maintain'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.upd.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.maintain'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.upd.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.maintain'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.maintain'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.maintain'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.maintain'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.add'));

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '!user.template.registered', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.registered'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.anon'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.registered'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.add.usersite'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.registered'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.registered'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.registered'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.upd.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.registered'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.upd.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.registered'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.registered'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.registered'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.registered'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.add'));

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '!user.template.sample', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.sample'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.anon'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.sample'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.add.usersite'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.sample'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.sample'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.sample'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.upd.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.sample'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.upd.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.sample'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.sample'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.sample'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.sample'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.add'));

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '!site.template', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'asn.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'asn.submit'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dropbox.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.dis.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.path.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.step.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.revise.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.delete.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.delete.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.read.drafts'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'asn.delete'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'asn.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'asn.revise'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'asn.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.revise'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.delete'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.delete.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.delete.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.revise.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.revise'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.delete'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.delete.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.new.topic'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.delete.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.read.drafts'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.revise.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.revise.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.delete.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.delete.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit.unp'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.dis.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.dis.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.path.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.path.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.path.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.dis.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.step.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.step.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.dis.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.step.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.step.read'));

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '/content/public/', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/content/public/'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.anon'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/content/public/'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.read'));

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '/content/attachment/', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/content/attachment/'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.anon'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/content/attachment/'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/content/attachment/'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/content/attachment/'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.delete'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/content/attachment/'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.revise'));

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '/announcement/channel/!site/motd', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/announcement/channel/!site/motd'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.anon'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/announcement/channel/!site/motd'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.read'));

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '!pubview', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!pubview'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'pubview'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!pubview'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'pubview'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!pubview'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'pubview'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!pubview'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'pubview'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!pubview'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'pubview'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!pubview'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'pubview'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!pubview'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'pubview'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.read'));

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '/site/!gateway', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!gateway'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.anon'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!gateway'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit'));

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '/site/!error', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!error'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.anon'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!error'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit'));
	
INSERT INTO SAKAI_REALM VALUES (DEFAULT, '/site/!urlError', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!urlError'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.anon'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!urlError'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit'));
	
