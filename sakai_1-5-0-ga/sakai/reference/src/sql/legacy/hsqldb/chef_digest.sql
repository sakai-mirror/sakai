-----------------------------------------------------------------------------
-- CHEF_DIGEST
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS CHEF_DIGEST;

CREATE TABLE CHEF_DIGEST
(
    DIGEST_ID VARCHAR (99) NOT NULL,
    XML LONGVARCHAR,
    CONSTRAINT CHEF_DIGEST_INDEX UNIQUE (DIGEST_ID)
);

