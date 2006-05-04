-----------------------------------------------------------------------------
-- SAKAI_EVENT
-----------------------------------------------------------------------------

CREATE TABLE SAKAI_EVENT
(
	EVENT_ID BIGINT AUTO_INCREMENT,
	EVENT_DATE DATETIME,
	EVENT VARCHAR (32),
	REF VARCHAR (255),
	SESSION_ID VARCHAR (36),
	EVENT_CODE VARCHAR (1),
	PRIMARY KEY (EVENT_ID)
);