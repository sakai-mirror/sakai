DROP TABLE QUESTIONPOOLDATA;

CREATE TABLE QUESTIONPOOLDATA (
        QUESTIONPOOLID      INTEGER ,
        TITLE               varchar(255),
        DESCRIPTION         varchar(255),
        PARENTPOOLID        INTEGER ,
        ORGANIZATIONNAME    varchar(255),
        DEFAULTACCESSTYPEID INTEGER ,
        OBJECTIVE           varchar(255),
        KEYWORDS            varchar(255),
        RUBRIC              long varchar,
        TYPEID              INTEGER ,
        INTELLECTUALPROPERTYID INTEGER ,
        STATUS              INTEGER ,
        OWNERID             varchar(36),
        DATECREATED         DATETIME ,
        LASTMODIFIEDBY      VARCHAR(36) ,
        LASTMODIFIEDDATE    DATETIME
);

-- QuestionPoolItem is a map, used to associate a question with a questionPool
DROP TABLE QUESTIONPOOLITEMDATA;

CREATE TABLE QUESTIONPOOLITEMDATA (
        QUESTIONPOOLID      INTEGER ,
        ITEMID              varchar(36),
        CONSTRAINT "QUESTIONPOOLITEM_PRIMARY" PRIMARY KEY ("QUESTIONPOOLID","ITEMID")
);

-- QuestionPoolPool describes the access privilege to a questionpool that a
-- person(agent) has. This privilege override what is set as default by
-- the questionpool. e.g If the pool is default to "ACCESS_DENIED", "ADMIN"
-- access can be granted to an agent by registering the override here.
DROP TABLE QUESTIONPOOLACCESSDATA;

CREATE TABLE QUESTIONPOOLACCESSDATA (
        QUESTIONPOOLID      INTEGER ,
        AGENTID             varchar(36),
        ACCESSTYPEID        INTEGER
);
