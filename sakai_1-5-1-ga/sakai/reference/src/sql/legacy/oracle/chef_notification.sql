-----------------------------------------------------------------------------
-- CHEF_NOTIFICATION
-----------------------------------------------------------------------------

DROP TABLE CHEF_NOTIFICATION;

CREATE TABLE CHEF_NOTIFICATION
(
    NOTIFICATION_ID VARCHAR2 (99) NOT NULL,
    XML LONG
);

CREATE UNIQUE INDEX CHEF_NOTIFICATION_INDEX ON CHEF_NOTIFICATION
(
	NOTIFICATION_ID
);
