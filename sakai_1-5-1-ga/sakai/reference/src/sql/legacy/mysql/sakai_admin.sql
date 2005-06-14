INSERT INTO SAKAI_REALM VALUES(DEFAULT,'/site/!admin',NULL,NULL,'admin','admin','2005-03-20 23:06:04','2005-03-20 23:07:43');
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
INSERT INTO SAKAI_SITE_PAGE (PAGE_ID, SITE_ID, TITLE, LAYOUT, SITE_ORDER) VALUES ('1111378063199-2','!admin','Home','0',1);
INSERT INTO SAKAI_SITE_PAGE (PAGE_ID, SITE_ID, TITLE, LAYOUT, SITE_ORDER) VALUES ('1111378063204-5','!admin','Users','0',2);
INSERT INTO SAKAI_SITE_PAGE (PAGE_ID, SITE_ID, TITLE, LAYOUT, SITE_ORDER) VALUES ('1111378063206-7','!admin','Aliases','0',3);
INSERT INTO SAKAI_SITE_PAGE (PAGE_ID, SITE_ID, TITLE, LAYOUT, SITE_ORDER) VALUES ('1111378063209-9','!admin','Sites','0',4);
INSERT INTO SAKAI_SITE_PAGE (PAGE_ID, SITE_ID, TITLE, LAYOUT, SITE_ORDER) VALUES ('1111378063211-11','!admin','Realms','0',5);
INSERT INTO SAKAI_SITE_PAGE (PAGE_ID, SITE_ID, TITLE, LAYOUT, SITE_ORDER) VALUES ('1111378063213-13','!admin','Worksite Setup','0',6);
INSERT INTO SAKAI_SITE_PAGE (PAGE_ID, SITE_ID, TITLE, LAYOUT, SITE_ORDER) VALUES ('1111378063215-15','!admin','MOTD','0',7);
INSERT INTO SAKAI_SITE_PAGE (PAGE_ID, SITE_ID, TITLE, LAYOUT, SITE_ORDER) VALUES ('1111378063219-17','!admin','Resources','0',8);
INSERT INTO SAKAI_SITE_PAGE (PAGE_ID, SITE_ID, TITLE, LAYOUT, SITE_ORDER) VALUES ('1111378063221-19','!admin','On-Line','0',9);
INSERT INTO SAKAI_SITE_PAGE (PAGE_ID, SITE_ID, TITLE, LAYOUT, SITE_ORDER) VALUES ('1111378063224-21','!admin','Memory','0',10);
INSERT INTO SAKAI_SITE_PAGE (PAGE_ID, SITE_ID, TITLE, LAYOUT, SITE_ORDER) VALUES ('1111378063226-23','!admin','Site Archive','0',11);
INSERT INTO SAKAI_SITE_TOOL (TOOL_ID, PAGE_ID, SITE_ID, REGISTRATION, PAGE_ORDER, TITLE, LAYOUT_HINTS) VALUES ('1111378063201-3','1111378063199-2','!admin','chef.motd',1,'Message of the Day','null');
INSERT INTO SAKAI_SITE_TOOL (TOOL_ID, PAGE_ID, SITE_ID, REGISTRATION, PAGE_ORDER, TITLE, LAYOUT_HINTS) VALUES ('1111378063202-4','1111378063199-2','!admin','chef.iframe',2,'My Workspace Information','null');
INSERT INTO SAKAI_SITE_TOOL (TOOL_ID, PAGE_ID, SITE_ID, REGISTRATION, PAGE_ORDER, TITLE, LAYOUT_HINTS) VALUES ('1111378063205-6','1111378063204-5','!admin','chef.users',1,'Users','null');
INSERT INTO SAKAI_SITE_TOOL (TOOL_ID, PAGE_ID, SITE_ID, REGISTRATION, PAGE_ORDER, TITLE, LAYOUT_HINTS) VALUES ('1111378063207-8','1111378063206-7','!admin','chef.aliases',1,'Aliases','null');
INSERT INTO SAKAI_SITE_TOOL (TOOL_ID, PAGE_ID, SITE_ID, REGISTRATION, PAGE_ORDER, TITLE, LAYOUT_HINTS) VALUES ('1111378063210-10','1111378063209-9','!admin','chef.sites',1,'Sites','null');
INSERT INTO SAKAI_SITE_TOOL (TOOL_ID, PAGE_ID, SITE_ID, REGISTRATION, PAGE_ORDER, TITLE, LAYOUT_HINTS) VALUES ('1111378063212-12','1111378063211-11','!admin','chef.realms',1,'Realms','null');
INSERT INTO SAKAI_SITE_TOOL (TOOL_ID, PAGE_ID, SITE_ID, REGISTRATION, PAGE_ORDER, TITLE, LAYOUT_HINTS) VALUES ('1111378063214-14','1111378063213-13','!admin','chef.sitesetupgeneric',1,'Worksite Setup','null');
INSERT INTO SAKAI_SITE_TOOL (TOOL_ID, PAGE_ID, SITE_ID, REGISTRATION, PAGE_ORDER, TITLE, LAYOUT_HINTS) VALUES ('1111378063217-16','1111378063215-15','!admin','chef.announcements',1,'MOTD','null');
INSERT INTO SAKAI_SITE_TOOL (TOOL_ID, PAGE_ID, SITE_ID, REGISTRATION, PAGE_ORDER, TITLE, LAYOUT_HINTS) VALUES ('1111378063220-18','1111378063219-17','!admin','chef.resources',1,'Resources','null');
INSERT INTO SAKAI_SITE_TOOL (TOOL_ID, PAGE_ID, SITE_ID, REGISTRATION, PAGE_ORDER, TITLE, LAYOUT_HINTS) VALUES ('1111378063222-20','1111378063221-19','!admin','chef.presence',1,'On-Line','null');
INSERT INTO SAKAI_SITE_TOOL (TOOL_ID, PAGE_ID, SITE_ID, REGISTRATION, PAGE_ORDER, TITLE, LAYOUT_HINTS) VALUES ('1111378063225-22','1111378063224-21','!admin','chef.memory',1,'Memory','null');
INSERT INTO SAKAI_SITE_TOOL (TOOL_ID, PAGE_ID, SITE_ID, REGISTRATION, PAGE_ORDER, TITLE, LAYOUT_HINTS) VALUES ('1111378063227-24','1111378063226-23','!admin','chef.archive',1,'Site Archive / Import','null');
INSERT INTO SAKAI_SITE_TOOL_PROPERTY (SITE_ID, TOOL_ID, NAME, VALUE) VALUES ('!admin','1111378063202-4','special','workspace');
INSERT INTO SAKAI_SITE_TOOL_PROPERTY (SITE_ID, TOOL_ID, NAME, VALUE) VALUES ('!admin','1111378063217-16','channel','/announcement/channel/!site/motd');
INSERT INTO SAKAI_SITE_TOOL_PROPERTY (SITE_ID, TOOL_ID, NAME, VALUE) VALUES ('!admin','1111378063220-18','home','/');
INSERT INTO SAKAI_SITE_USER (SITE_ID, USER_ID, PERMISSION) VALUES ('!admin','admin',-1);
INSERT INTO ANNOUNCEMENT_CHANNEL (CHANNEL_ID, NEXT_ID, XML) VALUES ('/announcement/channel/!admin/main',0,'<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<channel context=\"!admin\" id=\"main\">\n    <properties/>\n</channel>\n');
INSERT INTO CONTENT_COLLECTION (COLLECTION_ID, IN_COLLECTION, XML) VALUES ('/group/!admin/','/group/','<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<collection id=\"/group/!admin/\">\n    <properties>\n        <property enc=\"BASE64\" name=\"CHEF:is-collection\" value=\"dHJ1ZQ==&#xa;\"/>\n        <property enc=\"BASE64\" name=\"CHEF:creator\" value=\"YWRtaW4=&#xa;\"/>\n        <property enc=\"BASE64\" name=\"DAV:displayname\" value=\"QWRtaW5pc3RyYXRpb24gV29ya3NwYWNl&#xa;\"/>\n        <property enc=\"BASE64\" name=\"CHEF:modifiedby\" value=\"YWRtaW4=&#xa;\"/>\n        <property enc=\"BASE64\" name=\"DAV:getlastmodified\" value=\"MjAwNTAzMjEwNDA3NDM3NjI=&#xa;\"/>\n        <property enc=\"BASE64\" name=\"DAV:creationdate\" value=\"MjAwNTAzMjEwNDA3NDM3NjE=&#xa;\"/>\n    </properties>\n</collection>\n');
INSERT INTO SAKAI_SITE (SITE_ID, TITLE, TYPE, SHORT_DESC, DESCRIPTION, ICON_URL, INFO_URL, SKIN, PUBLISHED, JOINABLE, PUBVIEW, JOIN_ROLE, CREATEDBY, MODIFIEDBY, CREATEDON, MODIFIEDON, IS_SPECIAL, IS_USER) VALUES ('!admin','Administration Workspace',NULL,NULL,'Administration Workspace',NULL,NULL,NULL,1,'0','0',NULL,'admin','admin','2005-03-20 23:07:43','2005-03-20 23:07:43','0','0');
