-----------------------------------------------------------------------------
-- CHEF_PREFERENCES
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS CHEF_PREFERENCES;

CREATE TABLE CHEF_PREFERENCES
(
    PREFERENCES_ID VARCHAR (99) NOT NULL,
    XML LONG
);

CREATE UNIQUE INDEX CHEF_PREFERENCES_INDEX ON CHEF_PREFERENCES
(
	PREFERENCES_ID
);

DESCRIBE CHEF_PREFERENCES;


