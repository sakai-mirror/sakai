-----------------------------------------------------------------------------
-- CHEF_PREFERENCES
-----------------------------------------------------------------------------

DROP TABLE CHEF_PREFERENCES;

CREATE TABLE CHEF_PREFERENCES
(
    PREFERENCES_ID VARCHAR2 (99) NOT NULL,
    XML LONG
);

CREATE UNIQUE INDEX CHEF_PREFERENCES_INDEX ON CHEF_PREFERENCES
(
	PREFERENCES_ID
);

