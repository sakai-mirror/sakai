-----------------------------------------------------------------------------
-- CHEF_PREFERENCES
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS CHEF_PREFERENCES;

CREATE TABLE CHEF_PREFERENCES
(
    PREFERENCES_ID VARCHAR (99) NOT NULL,
    XML LONGVARCHAR,
    CONSTRAINT CHEF_PREFERENCES_INDEX UNIQUE (PREFERENCES_ID)
);

