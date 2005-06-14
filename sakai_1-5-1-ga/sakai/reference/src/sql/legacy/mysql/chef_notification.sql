-----------------------------------------------------------------------------
-- CHEF_NOTIFICATION
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS CHEF_NOTIFICATION;

CREATE TABLE CHEF_NOTIFICATION
(
    NOTIFICATION_ID VARCHAR (99) NOT NULL,
    XML LONGTEXT
);

CREATE UNIQUE INDEX CHEF_NOTIFICATION_INDEX ON CHEF_NOTIFICATION
(
	NOTIFICATION_ID
);

DESCRIBE CHEF_NOTIFICATION;

