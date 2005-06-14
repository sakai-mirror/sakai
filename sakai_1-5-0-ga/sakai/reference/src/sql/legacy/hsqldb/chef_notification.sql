-----------------------------------------------------------------------------
-- CHEF_NOTIFICATION
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS CHEF_NOTIFICATION;

CREATE TABLE CHEF_NOTIFICATION
(
    NOTIFICATION_ID VARCHAR (99) NOT NULL,
    XML LONGVARCHAR,
    CONSTRAINT CHEF_NOTIFICATION_INDEX UNIQUE (NOTIFICATION_ID)
);



