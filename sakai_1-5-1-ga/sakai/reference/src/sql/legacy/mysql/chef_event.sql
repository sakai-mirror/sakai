-----------------------------------------------------------------------------
-- CHEF_EVENT
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS CHEF_EVENT;

CREATE TABLE CHEF_EVENT
(
	EVENT_ID BIGINT AUTO_INCREMENT,
	EVENT_DATE DATETIME,
	EVENT VARCHAR (32),
	REF VARCHAR (255),
	SESSION_ID VARCHAR (32),
	EVENT_CODE VARCHAR (1),
	PRIMARY KEY (EVENT_ID)
);

DESCRIBE CHEF_EVENT;
