-- CREATE TABLESPACES FOR ONCOURSE NG
--------------------------------------
-- CREATE TABLESPACE ONC01
-- DATAFILE '/OPT/ORACLE/D07/ORADATA/ONC01A.DBF' SIZE 3072008K
-- EXTENT MANAGEMENT LOCAL UNIFORM SIZE 1M ;
--
-- CREATE TABLESPACE ONC_IDX01
-- DATAFILE '/OPT/ORACLE/D08/ORADATA/ONC_IDX01A.DBF' SIZE 3072008K
-- EXTENT MANAGEMENT LOCAL UNIFORM SIZE 1M ;
--
-- CREATE TABLESPACE ONC_ASSETS_LOB01
-- DATAFILE '/OPT/ORACLE/D08/ORADATA/ONC_ASSETS_LOB01.DBF' SIZE 4096008K
-- EXTENT MANAGEMENT LOCAL UNIFORM SIZE 1M ;
----------------------------------------------------------------------------------------

-- DROP EXISTING TABLES
DROP TABLE ONC.SECTIONITEM;
DROP TABLE ONC.ITEM;
DROP TABLE ONC.ITEMMEDIA;
DROP TABLE ONC.ASSESSMENTSECTION;
DROP TABLE ONC.ASSESSMENTMEDIA;
DROP TABLE ONC.ASSESSMENTIPADDRESSPERMISSION;
DROP TABLE ONC.IDLIST;
DROP TABLE ONC.ASSESSMENTTEMPLATE;
DROP TABLE ONC.ASSESSMENTDISTRIBUTION;
DROP TABLE ONC.DISTRIBUTIONGROUP;
DROP TABLE ONC.ASSESSMENTACCESSGROUP;
DROP TABLE ONC.ACCESSGROUP;
DROP TABLE ONC.EVALUATIONMETHOD;
DROP TABLE ONC.FEEDBACK;
DROP TABLE ONC.ACCESSIP;
DROP TABLE ONC.IPADDRESS;
DROP TABLE ONC.ASSESSMENT;
DROP TABLE ONC.AUTHORIZATIONS;
DROP TABLE ONC.FUNCTIONS;
DROP TABLE ONC.QUALIFIERS;
DROP TABLE ONC.TYPES;
DROP TABLE ONC.AGENTS;
DROP TABLE ONC.GROUPS;
DROP TABLE ONC.DR_ASSET_MAP;
DROP TABLE ONC.ASSETS;
DROP TABLE ONC.QTI_SETTINGS;
DROP TABLE ONC.ITEM_RESULT;
DROP TABLE ONC.QTI_ASSESSMENT_TAKEN;
DROP TABLE ONC.ASSESSMENT_PUBLISHED;
DROP TABLE ONC.SECTION_RESULT;
DROP TABLE ONC.ASSESSMENT_RESULT;
DROP TABLE ONC.ITEMGRADING;
DROP TABLE ONC.ASSESSMENTGRADING;
DROP TABLE ONC.QUESTIONPOOL;
DROP TABLE ONC.QUESTIONPOOLITEM;
DROP TABLE ONC.QUESTIONPOOLMEDIA;
DROP TABLE ONC.QUESTIONPOOLACCESS;
DROP TABLE ONC.ITEMTAKEN;
DROP TABLE ONC.ANSWERTAKEN;
DROP TABLE ONC.ITEMPUBLISHED;
DROP TABLE ONC.ITEMTEMPLATE;
DROP TABLE ONC.ASSESSMENTTAKEN;
DROP TABLE ONC.ASSESSMENTPUBLISHED;
DROP TABLE ONC.ANSWER;
DROP TABLE ONC.ITEMANSWER;
DROP TABLE ONC.SECTIONMEDIA;
DROP TABLE ONC.MEDIA;
DROP TABLE ONC.MEDIATYPE;
DROP TABLE ONC.PERMISSION;
DROP TABLE ONC.ROLE;
DROP TABLE ONC.SECTIONTAKEN;
DROP TABLE ONC.SECTIONPUBLISHED;
DROP TABLE ONC.SECTION;
DROP TABLE ONC.SECTIONTEMPLATE;
DROP TABLE ONC.SUBMISSIONMODEL;
DROP TABLE ONC.INTELLECTUALPROPERTY;
DROP TABLE ONC.STANFORDTYPE;

-- CREATE TABLES
CREATE TABLE ONC.STANFORDTYPE (
       TYPEID               number,
       DESCRIPTION          varchar(255),
       KEYWORD              varchar(255),
       DOMAIN               varchar(255),
       AUTHORITY            varchar(255)
)
TABLESPACE ONC01;

CREATE TABLE ONC.ASSESSMENT (
       ASSESSMENTID         NUMBER,
       TITLE                VARCHAR2(255),
       DUEDATE              DATE,
       SUBMISSIONMODELID    NUMBER,
       SUBMISSIONSSAVED     VARCHAR2(255),
       PARENTID             NUMBER,
       GRADEAVAILABILITY    DATE,
       EVAULATIONID         NUMBER,
       ISSTUDENTIDPUBLIC    VARCHAR2(1),
       COURSEID             NUMBER,
       AGENTID              VARCHAR2(255),
       DESCRIPTION          VARCHAR2(255),
       OBJECTIVE            VARCHAR2(255),
       METADATA             VARCHAR2(4000),
       STARTDATE            DATE,
       TIMELIMIT            NUMBER,
       TEMPLATEID           NUMBER,
       TEMPLATENAME         VARCHAR2(255),
       TEMPLATEAUTHOR       VARCHAR2(255),
       TEMPLATECOMMENTS     VARCHAR2(255),
       ISTEMPLATE           VARCHAR2(1),
       MULTIPARTALLOWED     VARCHAR2(1),
       ITEMACCESSTYPE       VARCHAR2(255),
       ITEMBOOKMARKING      VARCHAR2(255),
       EVALUATIONCOMPONENTS VARCHAR2(255),
       AUTOSCORING          VARCHAR2(1),
       TESTEEIDENTITY       VARCHAR2(255),
       EVALUATIONDISTRIBUTION VARCHAR2(255),
       SCORINGTYPE          VARCHAR2(255),
       NUMERICMODEL         VARCHAR2(255),
       DEFAULTQUESTIONVALUE NUMBER,
       FIXEDTOTALSCORE      NUMBER,
       INSTRUCTORNOTIFICATION VARCHAR2(255),
       TESTEENOTIFICATION   VARCHAR2(255),
       LATEHANDLING         VARCHAR2(255),
       TIMEDASSESSMENT      NUMBER,
       DISPLAYCHUNKING      VARCHAR2(255),
       KEYWORDS             VARCHAR2(255),
       RUBRICS              VARCHAR2(255),
       TYPEID               NUMBER,
       CLASS_NAME           VARCHAR2(255),
       PROPERTIESID         NUMBER,
       RETRYALLOWED         NUMBER,
       FEEDBACKTYPE         VARCHAR2(255),
       IMMFEEDBACKTYPE      VARCHAR2(255),
       DATEDFEEDBACKTYPE    VARCHAR2(255),
       PERQFEEDBACKTYPE     VARCHAR2(255),
       FEEDBACKDATE         DATE,
       SCOREDATE            DATE,
       DATECREATED          DATE,
       LASTMODIFIED         DATE,
       ANONYMOUSGRADING     VARCHAR2(1),
       TOGRADEBOOK          VARCHAR2(255),
       RECORDEDSCORE        VARCHAR2(255),
       FEEDBACKCOMPONENTS   VARCHAR2(255),
       AUTOSAVE             VARCHAR2(255),
       QUESTIONNUMBERING    VARCHAR2(255)
)
TABLESPACE ONC01;

CREATE TABLE ONC.AUTHORIZATIONS(
   AGENT_ID VARCHAR2(36),
   FUNCTION_ID VARCHAR2(36),
   QUALIFIER_ID VARCHAR2(36),
   EFFECTIVE_DATE TIMESTAMP(3) WITH TIME ZONE,
   EXPIRATION_DATE TIMESTAMP(3) WITH TIME ZONE,
   MODIFIER_ID VARCHAR2(36),
   MODIFIED_DATE TIMESTAMP(3) WITH TIME ZONE,
   IS_EXPLICIT CHAR(1),
   CONSTRAINT AUTHORIZATIONS_PK PRIMARY KEY (AGENT_ID,FUNCTION_ID,QUALIFIER_ID) DISABLE
)
TABLESPACE ONC01 ;

CREATE UNIQUE INDEX ONC.AUTHORIZATIONS_PK
 ON ONC.AUTHORIZATIONS (AGENT_ID,FUNCTION_ID,QUALIFIER_ID)
 TABLESPACE ONC_IDX01 ;

ALTER TABLE ONC.AUTHORIZATIONS ENABLE PRIMARY KEY USING INDEX ;

CREATE TABLE ONC.FUNCTIONS (
   FUNCTION_ID VARCHAR2(36) CONSTRAINT FUNCTIONS_PK PRIMARY KEY DISABLE,
   DISPLAY_NAME VARCHAR2(50),
   DESCRIPTION VARCHAR2(100),
   FUNCTION_TYPE VARCHAR2(36) CONSTRAINT FUNCTIONS_FUNCTION_TYPE_NN NOT NULL,
   QUALIFIER_HIERARCHY_ID VARCHAR2(36)
)
TABLESPACE ONC01 ;

CREATE UNIQUE INDEX ONC.FUNCTIONS_PK
 ON ONC.FUNCTIONS (FUNCTION_ID)
 TABLESPACE ONC_IDX01 ;

ALTER TABLE ONC.FUNCTIONS ENABLE PRIMARY KEY USING INDEX ;

CREATE TABLE ONC.QUALIFIERS(
   QUALIFIER_ID VARCHAR2(36) CONSTRAINT QUALIFIERS_PK PRIMARY KEY DISABLE,
   DISPLAY_NAME VARCHAR2(50),
   DESCRIPTION VARCHAR2(100),
   QUALIFIER_TYPE VARCHAR2(200) CONSTRAINT QUALIFIERS_QUALIFIER_TYPE_NN NOT NULL,
   QUALIFIER_HIERARCHY_ID VARCHAR2(36)
)
TABLESPACE ONC01 ;

CREATE UNIQUE INDEX ONC.QUALIFIERS_PK
 ON ONC.QUALIFIERS (QUALIFIER_ID)
 TABLESPACE ONC_IDX01 ;

ALTER TABLE ONC.QUALIFIERS ENABLE PRIMARY KEY USING INDEX ;

CREATE TABLE ONC.TYPES(
   TYPE_ID VARCHAR2(36)      CONSTRAINT TYPES_PK PRIMARY KEY DISABLE,
   AUTHORITY VARCHAR2(100)   CONSTRAINT TYPES_AUTHORITY_NN NOT NULL,
   DOMAIN VARCHAR2(100)      CONSTRAINT TYPES_DOMAIN_NN NOT NULL,
   KEYWORD VARCHAR2(100)     CONSTRAINT TYPES_KEYWORD_NN NOT NULL,
   DESCRIPTION VARCHAR2(100)
)
TABLESPACE ONC01 ;

CREATE UNIQUE INDEX ONC.TYPES_PK
 ON ONC.TYPES (TYPE_ID)
 TABLESPACE ONC01 ;

ALTER TABLE ONC.TYPES ENABLE PRIMARY KEY USING INDEX ;

CREATE TABLE ONC.AGENTS (
   AGENT_ID VARCHAR2(36)    CONSTRAINT AGENTS_PK PRIMARY KEY DISABLE,
   TYPE_ID VARCHAR2(36)     CONSTRAINT AGENTS_TYPE_ID_NN NOT NULL,
   PS_EMPL_ID VARCHAR2(11),
   KERBEROS_ID VARCHAR2(36),
   IP_ADDR VARCHAR2(15)
)
TABLESPACE ONC01 ;

CREATE UNIQUE INDEX ONC.AGENTS_PK
 ON ONC.AGENTS (AGENT_ID)
 TABLESPACE ONC_IDX01 ;

ALTER TABLE ONC.AGENTS ENABLE PRIMARY KEY USING INDEX ;

CREATE TABLE ONC.GROUPS (
   GROUP_ID VARCHAR2(255) CONSTRAINT GROUPS_PK PRIMARY KEY DISABLE,
   TYPE_ID VARCHAR2(255) ,
   OC_GROUP_ID VARCHAR2(255)
)
TABLESPACE ONC01 ;

CREATE UNIQUE INDEX ONC.GROUPS_PK
 ON ONC.GROUPS (GROUP_ID)
 TABLESPACE ONC_IDX01 ;

ALTER TABLE ONC.GROUPS ENABLE PRIMARY KEY USING INDEX ;

CREATE TABLE ONC.DR_ASSET_MAP(
   DR_ID VARCHAR2(36),
   ASSET_ID VARCHAR2(36),
   AGENT_ID VARCHAR2(36),
   CONSTRAINT DR_ASSET_MAP_PK PRIMARY KEY (DR_ID,ASSET_ID) DISABLE
)
TABLESPACE ONC01 ;

CREATE UNIQUE INDEX ONC.DR_ASSET_MAP_PK
 ON ONC.DR_ASSET_MAP (DR_ID,ASSET_ID)
 TABLESPACE ONC_IDX01 ;

ALTER TABLE ONC.DR_ASSET_MAP ENABLE PRIMARY KEY USING INDEX ;

CREATE TABLE ONC.ASSETS (
   ID VARCHAR2(36),
   VERSION NUMBER(19),
   DELETED CHAR(1),
   CREATED TIMESTAMP(3) WITH TIME ZONE,
   DATA BLOB DEFAULT EMPTY_BLOB(),
   DESCRIPTION VARCHAR2(1024),
   TITLE VARCHAR2(128),
   DR_ID VARCHAR2(36),
   EFFECTIVE_DATE TIMESTAMP(3) WITH TIME ZONE,
   EXPIRATION_DATE TIMESTAMP(3) WITH TIME ZONE,
   TYPE_ID VARCHAR2(36) CONSTRAINT ASSETS_TYPE_ID_NN NOT NULL
   )
   TABLESPACE ONC01
   LOB (DATA) STORE AS (TABLESPACE ONC_ASSETS_LOB01) ;

CREATE UNIQUE INDEX ASSETS_PK ON ASSETS
(
       ID                             ASC,
       VERSION                        ASC
);

ALTER TABLE ONC.ASSETS
       ADD  ( PRIMARY KEY (ID, VERSION) ) ;

ALTER TABLE ONC.ASSETS ENABLE PRIMARY KEY USING INDEX ;

CREATE TABLE ONC.QTI_SETTINGS (
  ID                   VARCHAR2(200)    CONSTRAINT QTI_SETTINGS_PK PRIMARY KEY DISABLE,
  DISPLAY_NAME VARCHAR2(128),
  MAX_ATTEMPTS         NUMBER(22),
  AUTO_SUBMIT          VARCHAR2(5),
  AUTO_SAVE            CHAR(1),
  TEST_DISABLED        VARCHAR2(5),
  FEEDBACK_TYPE        VARCHAR2(9),
  LATE_HANDLING        CHAR(1),
  IP_RESTRICTIONS      VARCHAR2(4000),
  PASSWORD_RESTRICTION VARCHAR2(15),
  USERNAME_RESTICTION  VARCHAR2(30),
  START_DATE           TIMESTAMP(3) WITH TIME ZONE,
  END_DATE             TIMESTAMP(3) WITH TIME ZONE,
  CREATED_DATE         TIMESTAMP(3) WITH TIME ZONE,
  FEEDBACK_DATE        TIMESTAMP(3) WITH TIME ZONE,
  RETRACT_DATE         TIMESTAMP(3) WITH TIME ZONE
)
TABLESPACE ONC01 ;

CREATE UNIQUE INDEX ONC.QTI_SETTINGS_PK
 ON ONC.QTI_SETTINGS (ID)
 TABLESPACE ONC_IDX01 ;

ALTER TABLE ONC.QTI_SETTINGS ENABLE PRIMARY KEY USING INDEX ;

CREATE TABLE ONC.ITEM_RESULT (
  ASSESSMENT_ID VARCHAR2(36),
  ITEM_ID VARCHAR2(36),
  ELEMENT_ID VARCHAR2(36),
  CONSTRAINT ITEM_RESULT_PK PRIMARY KEY (ASSESSMENT_ID,ITEM_ID) DISABLE
)
TABLESPACE ONC01 ;

CREATE UNIQUE INDEX ONC.ITEM_RESULT_PK
 ON ONC.ITEM_RESULT (ASSESSMENT_ID, ITEM_ID)
 TABLESPACE ONC_IDX01 ;

ALTER TABLE ONC.ITEM_RESULT ENABLE PRIMARY KEY USING INDEX ;

CREATE TABLE ONC.QTI_ASSESSMENT_TAKEN (
  ASSESSMENT_TAKEN_ID VARCHAR2(50) CONSTRAINT QTI_ASSESSMENT_TAKEN_PK PRIMARY KEY DISABLE,
  ASSESSMENT_PUB_ID VARCHAR2(50),
  AGENT_ID VARCHAR2(50),
  SUBMITTED NUMBER(1),
  LATE_SUBMISSION NUMBER(1),
  SUBMISSION_TIME TIMESTAMP(3)WITH TIME ZONE,
  BEGIN_TIME TIMESTAMP(3)WITH TIME ZONE,
  END_TIME TIMESTAMP(3)WITH TIME ZONE,
  ASSESSMENT_TITLE VARCHAR2(128)
)
TABLESPACE ONC01 ;

CREATE UNIQUE INDEX ONC.QTI_ASSESSMENT_TAKEN_PK
 ON ONC.QTI_ASSESSMENT_TAKEN (ASSESSMENT_TAKEN_ID)
 TABLESPACE ONC_IDX01 ;

ALTER TABLE ONC.QTI_ASSESSMENT_TAKEN ENABLE PRIMARY KEY USING INDEX ;

CREATE TABLE ONC.ASSESSMENT_PUBLISHED (
   PUBLISHED_ID VARCHAR2(36)     CONSTRAINT ASSESSMENT_PUBLISHED_PK PRIMARY KEY DISABLE,
   CORE_ID VARCHAR2(36)
)
TABLESPACE ONC01 ;

CREATE UNIQUE INDEX ONC.ASSESSMENT_PUBLISHED_PK
 ON ONC.ASSESSMENT_PUBLISHED (PUBLISHED_ID)
 TABLESPACE ONC_IDX01 ;

ALTER TABLE ONC.ASSESSMENT_PUBLISHED ENABLE PRIMARY KEY USING INDEX ;

CREATE TABLE ONC.SECTION_RESULT (
  ASSESSMENT_ID VARCHAR2(36),
  SECTION_ID VARCHAR2(36),
  ELEMENT_ID VARCHAR2(36),
  CONSTRAINT SECTION_RESULT_PK PRIMARY KEY (ASSESSMENT_ID, SECTION_ID) DISABLE
)
TABLESPACE ONC01 ;

CREATE UNIQUE INDEX ONC.SECTION_RESULT_PK
 ON ONC.SECTION_RESULT (ASSESSMENT_ID, SECTION_ID)
 TABLESPACE ONC_IDX01 ;

ALTER TABLE ONC.SECTION_RESULT ENABLE PRIMARY KEY USING INDEX ;

CREATE TABLE ONC.ASSESSMENT_RESULT (
  ASSESSMENT_ID VARCHAR2(36) CONSTRAINT ASSESSMENT_RESULT_PK PRIMARY KEY DISABLE,
  ELEMENT_ID VARCHAR2(36)
)
TABLESPACE ONC01 ;

CREATE UNIQUE INDEX ONC.ASSESSMENT_RESULT_PK
 ON ONC.ASSESSMENT_RESULT (ASSESSMENT_ID)
 TABLESPACE ONC_IDX01 ;

ALTER TABLE ONC.ASSESSMENT_RESULT ENABLE PRIMARY KEY USING INDEX ;

CREATE TABLE ONC.ITEMGRADING (
        ITEMID               VARCHAR2(36),
        ASSESSMENTRESULTID   VARCHAR2(36),
        ITEMSCORE            VARCHAR2(255),
        ITEMMAXSCORE         VARCHAR2(255),
        ITEMTYPE             VARCHAR2(36),
        ANSWERTEXT           VARCHAR2(255),
        ANSWERMEDIAID        NUMBER,
        ANSWERCORRECT        VARCHAR2(1),
        RATIONALE            VARCHAR2(255),
        COMMENTS             VARCHAR2(255)
 )
TABLESPACE ONC01 ;

CREATE TABLE ONC.ASSESSMENTGRADING (
       ASSESSMENTID         VARCHAR2(36),
       ASSESSMENTRESULTID  VARCHAR2(36),
       RESULTDATE      DATE,
       AGENTID              VARCHAR2(255),
       AGENTNAME            VARCHAR2(255),
       AGENTROLE            VARCHAR2(255),
       MAXSCORE             VARCHAR2(255),
       SCORE                VARCHAR2(255),
       ADJUSTSCORE          VARCHAR2(255),
       FINALSCORE           VARCHAR2(255),
       COMMENTS             VARCHAR2(255),
       ISLATE               VARCHAR2(1)
)
TABLESPACE ONC01 ;

CREATE TABLE ONC.QUESTIONPOOL (
        QUESTIONPOOLID      NUMBER,
        PARENTPOOLID        NUMBER(38),
        OWNERID             VARCHAR2(255),
        ORGANIZATIONNAME    VARCHAR2(255),
        DATECREATED         DATE,
        LASTMODIFIED        DATE,
        LASTMODIFIEDBY      VARCHAR2(255),
        DEFAULTACCESSTYPEID NUMBER,
        TITLE               VARCHAR2(255),
        DESCRIPTION         VARCHAR2(255),
        OBJECTIVE           VARCHAR2(255),
        KEYWORDS            VARCHAR2(255),
        RUBRIC              VARCHAR2(4000),
        TYPEID              NUMBER,
        INTELLECTUALPROPERTYID NUMBER
)
TABLESPACE ONC01 ;

CREATE TABLE ONC.QUESTIONPOOLITEM (
        QUESTIONPOOLID      NUMBER,
        ITEMID              VARCHAR2(36)
)
TABLESPACE ONC01 ;



CREATE TABLE ONC.QUESTIONPOOLMEDIA (
        QUESTIONPOOLID      NUMBER,
        MEDIAID             NUMBER,
        POSITION            NUMBER
)
TABLESPACE ONC01 ;


CREATE TABLE ONC.QUESTIONPOOLACCESS (
        QUESTIONPOOLID      NUMBER,
        AGENTID             VARCHAR2(255),
        ACCESSTYPEID        NUMBER
)
TABLESPACE ONC01 ;


CREATE TABLE ONC.SECTIONITEM (
       SECTIONID            NUMERIC(10),
       ITEMID               NUMBER,
       POSITION             NUMBER
)
TABLESPACE ONC01;


CREATE TABLE ONC.ITEMMEDIA (
       ITEMID               NUMBER,
       MEDIAID              NUMBER,
       POSITION             NUMBER
)
TABLESPACE ONC01;


CREATE TABLE ONC.ITEM (
       ITEMID               NUMBER,
       TITLE                VARCHAR2(255),
       TEXT                 VARCHAR2(255),
       DESCRIPTION          VARCHAR2(255),
       TYPEID               NUMBER,
       OBJECTIVE            VARCHAR2(255),
       KEYWORDS             VARCHAR2(255),
       RUBRICS              VARCHAR2(255),
       VALUE                VARCHAR2(80),
       HINT                 VARCHAR2(255),
       ISTEMPLATE           VARCHAR2(1),
       TEMPLATEID           NUMBER,
       ASSESSMENTTEMPLATEID NUMERIC(10),
       FEEDBACK             VARCHAR2(4000),
       ADDPAGEBREAK         VARCHAR2(1),
       HASRATIONALE         VARCHAR2(1)
)
TABLESPACE ONC01;


CREATE TABLE ONC.ASSESSMENTSECTION (
       SECTIONID            NUMERIC(10),
       ASSESSMENTID         NUMBER,
       POSITION             NUMBER
)
TABLESPACE ONC01;


CREATE TABLE ONC.ASSESSMENTMEDIA (
       MEDIAID              NUMBER,
       ASSESSMENTID         NUMBER,
       POSITION             NUMBER
)
TABLESPACE ONC01;

CREATE TABLE ONC.ASSESSMENTIPADDRESSPERMISSION (
       ASSESSMENTPERMISSIONIPADDRESSI NUMBER,
       PERMISSIONID         NUMBER,
       IPADDRESSID          NUMBER,
       ASSESSMENTID         NUMBER
)
TABLESPACE ONC01;

CREATE TABLE ONC.IDLIST (
       TABLENAME            VARCHAR2(80),
       NEXTID               NUMBER
)
TABLESPACE ONC01;

CREATE TABLE ONC.ASSESSMENTTEMPLATE (
       ASSESSMENTID         NUMBER,
       FIELD                VARCHAR2(255),
       ISINSTRUCTOREDITABLE VARCHAR2(1),
       ISINSTRUCTORVIEWABLE VARCHAR2(1),
       ISSTUDENTVIEWABLE    VARCHAR2(1)
)
TABLESPACE ONC01;

CREATE TABLE ONC.ASSESSMENTDISTRIBUTION (
       ASSESSMENTID         NUMBER,
       DISTRIBUTIONGROUPID  NUMBER
)
TABLESPACE ONC01;

CREATE TABLE ONC.DISTRIBUTIONGROUP (
       DISTRIBUTIONGROUPID  NUMBER,
       NAME                 VARCHAR2(255),
       DISTRIBUTIONTYPES    VARCHAR2(4000)
)
TABLESPACE ONC01;

CREATE TABLE ONC.ASSESSMENTACCESSGROUP (
       ASSESSMENTID         NUMBER,
       ACCESSGROUPID        NUMBER
)
TABLESPACE ONC01;

CREATE TABLE ONC.ACCESSGROUP (
       ACCESSGROUPID        NUMBER,
       NAME                 VARCHAR2(255),
       RELEASETYPE          VARCHAR2(255),
       RELEASEDATE          DATE,
       RELEASEWHEN          VARCHAR2(255),
       RELEASESCORE         VARCHAR2(255),
       RETRACTTYPE          VARCHAR2(255),
       RETRACTDATE          DATE,
       ISRELEASED                DATE,
       DUEDATE              DATE,
       DUEDATETYPE          VARCHAR2(255),
       RETRYALLOWED         VARCHAR2(1),
       TIMEDASSESSMENT      VARCHAR2(1),
       MINUTES              NUMBER,
       PASSWORDACCESS       VARCHAR2(1),
       PASSWORD             VARCHAR2(255),
       ISACTIVE             VARCHAR2(1),
       IPACCESS             VARCHAR2(255)
)
TABLESPACE ONC01;

CREATE TABLE ONC.EVALUATIONMETHOD (
       EVAULATIONID         NUMBER,
       EVALUATIONMETHOD     VARCHAR2(255)
)
TABLESPACE ONC01;

CREATE TABLE ONC.FEEDBACK (
       FEEDBACKID           NUMBER,
       FEEDBACKLEVEL        VARCHAR2(255)
)
TABLESPACE ONC01;

CREATE TABLE ONC.ACCESSIP (
       ACCESSGROUPID        NUMBER,
       IPADDRESSID          NUMBER
)
TABLESPACE ONC01;

CREATE TABLE ONC.IPADDRESS (
       IPADDRESSID          NUMBER,
       IPADDRESS            VARCHAR2(255)
)
TABLESPACE ONC01;

CREATE TABLE ONC.ITEMTAKEN (
       ITEMID               number,
       ITEMTAKENID          number,
       SECTIONTAKENID       number,
       AUTOSAVED            varchar2(1),
       SUBMITDATE           DATE,
       AUTOSAVEDATE         DATE,
       FEEDBACK             varchar2(4000),
       SCORE                varchar2(255),
       FEEBACKMEDIAID       number
)
TABLESPACE ONC01;

CREATE TABLE ONC.ANSWERTAKEN (
       ITEMTAKENID          number,
       ANSWERID             number,
       ANSWERTEXT           varchar2(4000),
       ANSWERMEDIAID        number
)
TABLESPACE ONC01;

CREATE TABLE ONC.ITEMPUBLISHED (
       ITEMID               number,
       PUBLISHDATE          DATE,
       COURSEID             number
)
TABLESPACE ONC01;

CREATE TABLE ONC.ITEMTEMPLATE (
       ITEMID               number,
       FIELD                varchar2(255),
       ISINSTRUCTOREDITABLE varchar2(1),
       ISINSTRUCTORVIEWABLE varchar2(1),
       ISSTUDENTVIEWABLE    varchar2(1)
)
TABLESPACE ONC01;

CREATE TABLE ONC.ASSESSMENTTAKEN (
       ASSESSMENTTAKENID    number,
       DATECOMPLETED        DATE,
       AGENTID              varchar2(255),
       COURSEID             number,
       SUBMISSIONNUMBER     number,
       FEEDBACK             varchar2(4000),
       FEEDBACKMEDIAID      number,
       ADJUSTSCORE          varchar2(255),
       ASSESSMENTID         number
)
TABLESPACE ONC01;

CREATE TABLE ONC.ASSESSMENTPUBLISHED (
       PUBLISHDATE          DATE,
       ASSESSMENTID         number,
       COURSEID             number
)
TABLESPACE ONC01;

CREATE TABLE ONC.ANSWER (
       ANSWERID             number,
       TEXT                 varchar2(255),
       ISCORRECT            varchar2(1),
       FEEDBACK             varchar2(255),
       VALUE                varchar2(255)
)
TABLESPACE ONC01;

CREATE TABLE ONC.ITEMANSWER (
       ITEMID               number,
       ANSWERID             number,
       POSITION             number
)
TABLESPACE ONC01;

CREATE TABLE ONC.SECTIONMEDIA (
       SECTIONID            numeric(10),
       MEDIAID              number,
       POSITION             number
)
TABLESPACE ONC01;

CREATE TABLE ONC.MEDIA (
       MEDIAID              number,
       MEDIA                blob default empty_blob(),
       MEDIATYPEID          number,
       DESCRIPTION          varchar2(255),
       LOCATION             varchar2(255),
       NAME                 varchar2(255),
       AUTHOR               varchar2(255),
       FILENAME             varchar2(255),
       DATEADDED            DATE,
       ISLINK               varchar2(1),
       ISHTMLINLINE         varchar2(1)
)
TABLESPACE ONC01;

CREATE TABLE ONC.MEDIATYPE (
       MEDIATYPEID          number,
       MEDIATYPE            varchar2(255),
       RECORDER             varchar2(255),
       PLAYER               varchar2(255),
       ICONURL              varchar2(255),
       DESCRIPTION          varchar2(255)
)
TABLESPACE ONC01;

CREATE TABLE ONC.PERMISSION (
       PERMISSIONID         number,
       PERMISSION           number,
       DESCRIPTION          varchar2(255)
)
TABLESPACE ONC01;

CREATE TABLE ONC.ROLE (
       ROLEID               number,
       ROLENAME             varchar2(255)
)
TABLESPACE ONC01;

CREATE TABLE ONC.SECTIONTAKEN (
       SECTIONID            number,
       SECTIONTAKENID       number,
       ASSESSMENTTAKENID    number,
       DATECOMPLETED   DATE,
       FEEDBACK             varchar2(4000),
       FEEDBACKMEDIAID      number
)
TABLESPACE ONC01;

CREATE TABLE ONC.SECTIONPUBLISHED (
       PUBLISHDATE     DATE,
       SECTIONID            numeric(10),
       COURSEID             number
)
TABLESPACE ONC01;

CREATE TABLE ONC.SECTION (
       SECTIONID            numeric(10),
       TITLE                varchar2(255),
       PSECTIONID           number,
       DESCRIPTION          varchar2(255),
       TYPEID               number,
       OBJECTIVE            varchar2(255),
       KEYWORDS             varchar2(255),
       RUBRICS              varchar2(255),
       QUESTIONORDER        varchar2(255),
       RANDOMPOOLID         number,
       RANDOMNUMBER         number,
       ISTEMPLATE           varchar2(1),
       TEMPLATEID           number,
       ASSESSMENTTEMPLATEID number
)
TABLESPACE ONC01;

CREATE TABLE ONC.SECTIONTEMPLATE (
       SECTIONID            numeric(10),
       FIELD                varchar2(255),
       ISINSTRUCTOREDITABLE varchar2(1),
       ISINSTRUCTORVIEWABLE varchar2(1),
       ISSTUDENTVIEWABLE    varchar2(1)
)
TABLESPACE ONC01;

CREATE TABLE ONC.SUBMISSIONMODEL (
       SUBMISSIONMODELID    number,
       SUBMISSIONMODEL      varchar2(255),
       NUMOFSUBMISSIONSALLOWED number
)
TABLESPACE ONC01; 
