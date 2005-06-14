
CREATE TABLE SYLLABUS_DATA_T(ID BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,LOCKID INTEGER NOT NULL,
ASSET LONGVARCHAR,POSITION INTEGER NOT NULL,TITLE VARCHAR(256),XVIEW VARCHAR(16),STATUS VARCHAR(64),EMAILNOTIFICATION VARCHAR(128),
SURROGATEKEY BIGINT) ;

CREATE TABLE SYLLABUS_T(ID BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,LOCKID INTEGER NOT NULL,
USERID VARCHAR(36) NOT NULL,CONTEXTID VARCHAR(36) NOT NULL,REDIRECTURL VARCHAR(512),CONSTRAINT SYS_CT_1 UNIQUE(USERID,CONTEXTID)) ;

CREATE INDEX SYLLABUS_USERID ON SYLLABUS_T(USERID);
CREATE INDEX SYLLABUS_CONTEXTID ON SYLLABUS_T(CONTEXTID) ;

ALTER TABLE SYLLABUS_DATA_T ADD CONSTRAINT FK8AD134994FDCE067 FOREIGN KEY(SURROGATEKEY) REFERENCES SYLLABUS_T(ID); 
