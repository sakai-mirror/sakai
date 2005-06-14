-----------------------------------------------------------------------------
-- CHEF_ID (SEQ)
-----------------------------------------------------------------------------

--DROP SEQUENCE CHEF_ID_SEQ;
--CREATE SEQUENCE CHEF_ID_SEQ;

DROP TABLE IF EXISTS CHEF_ID_SEQ;

CREATE TABLE CHEF_ID_SEQ (NEXTVAL INT NOT NULL);
INSERT INTO CHEF_ID_SEQ VALUES (0);

DESCRIBE CHEF_ID_SEQ;
