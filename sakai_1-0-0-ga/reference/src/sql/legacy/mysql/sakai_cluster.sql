-----------------------------------------------------------------------------
-- SAKAI_CLUSTER
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS SAKAI_CLUSTER;

CREATE TABLE SAKAI_CLUSTER
(
	SERVER_ID VARCHAR (64),
	UPDATE_TIME DATETIME
);



--ALTER TABLE SAKAI_CLUSTER ADD ( CONSTRAINT 'SAKAI_CLUSTER_PK' PRIMARY KEY ('SERVER_ID') VALIDATE );
ALTER TABLE SAKAI_CLUSTER
       ADD  ( PRIMARY KEY (SERVER_ID) ) ;


DESCRIBE SAKAI_CLUSTER;

