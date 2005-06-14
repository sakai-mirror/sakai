-----------------------------------------------------------------------------
-- CHEF_PRESENCE
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS CHEF_PRESENCE;

CREATE TABLE CHEF_PRESENCE
(
	SESSION_ID VARCHAR (32),
	LOCATION_ID VARCHAR (255)
);

CREATE INDEX CHEF_PRESENCE_SESSION_INDEX ON CHEF_PRESENCE
(
	SESSION_ID
);

CREATE INDEX CHEF_PRESENCE_LOCATION_INDEX ON CHEF_PRESENCE
(
	LOCATION_ID
);

