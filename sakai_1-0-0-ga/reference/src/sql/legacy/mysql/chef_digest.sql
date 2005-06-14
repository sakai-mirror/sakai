-----------------------------------------------------------------------------
-- CHEF_DIGEST
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS CHEF_DIGEST;

CREATE TABLE CHEF_DIGEST
(
    DIGEST_ID VARCHAR (99) NOT NULL,
    XML LONG
);

CREATE UNIQUE INDEX CHEF_DIGEST_INDEX ON CHEF_DIGEST
(
	DIGEST_ID
);

DESCRIBE CHEF_DIGEST;

