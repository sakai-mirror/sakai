DROP TABLE ITEMTAKEN;

CREATE TABLE ITEMTAKEN (
       ITEMID               number,
       ITEMTAKENID          number,
       SECTIONTAKENID       number,
       AUTOSAVED            varchar(1),
       SUBMITDATE           DATE,
       AUTOSAVEDATE         DATE,
       FEEDBACK             clob,
       SCORE                varchar(255),
       FEEBACKMEDIAID       number
);

DROP TABLE ANSWERTAKEN;

CREATE TABLE ANSWERTAKEN (
       ITEMTAKENID          number,
       ANSWERID             number,
       ANSWERTEXT           clob,
       ANSWERMEDIAID        number
);

DROP TABLE ITEMPUBLISHED;

CREATE TABLE ITEMPUBLISHED (
       ITEMID               number,
       PUBLISHDATE          DATE,
       COURSEID             number
);


DROP TABLE ITEMMEDIA;

CREATE TABLE ITEMMEDIA (
       ITEMID               number,
       MEDIAID              number,
       POSITION             number
);


DROP TABLE ITEM;

CREATE TABLE ITEM (
       ITEMID               number,
       TITLE                varchar(255),
       TEXT                 varchar(255),
       DESCRIPTION          varchar(255),
       TYPEID               number,
       OBJECTIVE            varchar(255),
       KEYWORDS             varchar(255),
       RUBRICS              varchar(255),
       VALUE                varchar(80),
       HINT                 varchar(255),
       ISTEMPLATE           varchar(1),
       TEMPLATEID           number,
       ASSESSMENTTEMPLATEID numeric(10),
       FEEDBACK             clob,
       ADDPAGEBREAK         varchar(1),
       HASRATIONALE         varchar(1)
);

DROP TABLE ITEMTEMPLATE;

CREATE TABLE ITEMTEMPLATE (
       ITEMID               number,
       FIELD                varchar(255),
       ISINSTRUCTOREDITABLE varchar(1),
       ISINSTRUCTORVIEWABLE varchar(1),
       ISSTUDENTVIEWABLE    varchar(1)
);

DROP TABLE ASSESSMENTTAKEN;

CREATE TABLE ASSESSMENTTAKEN (
       ASSESSMENTTAKENID    number,
       DATECOMPLETED        DATE,
       AGENTID              varchar(255),
       COURSEID             number,
       SUBMISSIONNUMBER     number,
       FEEDBACK             clob,
       FEEDBACKMEDIAID      number,
       ADJUSTSCORE          varchar(255),
       ASSESSMENTID         number
);


DROP TABLE ASSESSMENTSECTION;

CREATE TABLE ASSESSMENTSECTION (
       SECTIONID            numeric(10),
       ASSESSMENTID         number,
       POSITION             number
);


DROP TABLE ASSESSMENTPUBLISHED;

CREATE TABLE ASSESSMENTPUBLISHED (
       PUBLISHDATE          DATE,
       ASSESSMENTID         number,
       COURSEID             number
);


DROP TABLE ASSESSMENTMEDIA;

CREATE TABLE ASSESSMENTMEDIA (
       MEDIAID              number,
       ASSESSMENTID         number,
       POSITION             number
);


DROP TABLE ASSESSMENTIPADDRESSPERMISSION;

CREATE TABLE ASSESSMENTIPADDRESSPERMISSION (
       ASSESSMENTPERMISSIONIPADDRESSI number,
       PERMISSIONID         number,
       IPADDRESSID          number,
       ASSESSMENTID         number
);



DROP TABLE ASSESSMENTDISTRIBUTION;

CREATE TABLE ASSESSMENTDISTRIBUTION (
       ASSESSMENTID         number,
       DISTRIBUTIONGROUPID  number
);

DROP TABLE DISTRIBUTIONGROUP;

CREATE TABLE DISTRIBUTIONGROUP (
       DISTRIBUTIONGROUPID  number,
       NAME                 varchar(255),
       DISTRIBUTIONTYPES    clob
);

DROP TABLE ASSESSMENTACCESSGROUP;

CREATE TABLE ASSESSMENTACCESSGROUP (
       ASSESSMENTID         number,
       ACCESSGROUPID        number
);

DROP TABLE ACCESSGROUP;

CREATE TABLE ACCESSGROUP (
       ACCESSGROUPID        number,
       NAME                 varchar(255),
       RELEASETYPE          varchar(255),
       RELEASEDATE          DATE,
       RELEASEWHEN          varchar(255),
       RELEASESCORE         varchar(255),
       RETRACTTYPE          varchar(255),
       RETRACTDATE          DATE,
       ISRELEASED                DATE,
       DUEDATE              DATE,
       DUEDATETYPE          varchar(255),
       RETRYALLOWED         varchar(1),
       TIMEDASSESSMENT      varchar(1),
       MINUTES              number,
       PASSWORDACCESS       varchar(1),
       PASSWORD             varchar(255),
       ISACTIVE             varchar(1),
       IPACCESS             varchar(255)
);


DROP TABLE STANFORDTYPE;

CREATE TABLE STANFORDTYPE (
       TYPEID               number,
       DESCRIPTION          varchar(255),
       KEYWORD              varchar(255),
       DOMAIN               varchar(255),
       AUTHORITY            varchar(255)
);


DROP TABLE ANSWER;

CREATE TABLE ANSWER (
       ANSWERID             number,
       TEXT                 varchar(255),
       ISCORRECT            varchar(1),
       FEEDBACK             varchar(255),
       VALUE                varchar(255)
);

DROP TABLE ITEMANSWER;

CREATE TABLE ITEMANSWER (
       ITEMID               number,
       ANSWERID             number,
       POSITION             number
);

DROP TABLE EVALUATIONMETHOD;

CREATE TABLE EVALUATIONMETHOD (
       EVAULATIONID         number,
       EVALUATIONMETHOD     varchar(255)
);


DROP TABLE FEEDBACK;

CREATE TABLE FEEDBACK (
       FEEDBACKID           number,
       FEEDBACKLEVEL        varchar(255)
);

DROP TABLE ACCESSIP;

CREATE TABLE ACCESSIP (
       ACCESSGROUPID        number,
       IPADDRESSID          number
);

DROP TABLE IPADDRESS;

CREATE TABLE IPADDRESS (
       IPADDRESSID          number,
       IPADDRESS            varchar(255)
);


DROP TABLE SECTIONMEDIA;

CREATE TABLE SECTIONMEDIA (
       SECTIONID            numeric(10),
       MEDIAID              number,
       POSITION             number
);



DROP TABLE PERMISSION;

CREATE TABLE PERMISSION (
       PERMISSIONID         number,
       PERMISSION           number,
       DESCRIPTION          varchar(255)
);


DROP TABLE ROLE;

CREATE TABLE ROLE (
       ROLEID               number,
       ROLENAME             varchar(255)
);

DROP TABLE SECTIONTAKEN;

CREATE TABLE SECTIONTAKEN (
       SECTIONID            number,
       SECTIONTAKENID       number,
       ASSESSMENTTAKENID    number,
       DATECOMPLETED   DATE,
       FEEDBACK             clob,
       FEEDBACKMEDIAID      number
);

DROP TABLE SECTIONPUBLISHED;

CREATE TABLE SECTIONPUBLISHED (
       PUBLISHDATE     DATE,
       SECTIONID            numeric(10),
       COURSEID             number
);


DROP TABLE SECTION;

CREATE TABLE SECTION (
       SECTIONID            numeric(10),
       TITLE                varchar(255),
       PSECTIONID           number,
       DESCRIPTION          varchar(255),
       TYPEID               number,
       OBJECTIVE            varchar(255),
       KEYWORDS             varchar(255),
       RUBRICS              varchar(255),
       QUESTIONORDER        varchar(255),
       RANDOMPOOLID         number,
       RANDOMNUMBER         number,
       ISTEMPLATE           varchar(1),
       TEMPLATEID           number,
       ASSESSMENTTEMPLATEID number
);

DROP TABLE SECTIONTEMPLATE;

CREATE TABLE SECTIONTEMPLATE (
       SECTIONID            numeric(10),
       FIELD                varchar(255),
       ISINSTRUCTOREDITABLE varchar(1),
       ISINSTRUCTORVIEWABLE varchar(1),
       ISSTUDENTVIEWABLE    varchar(1)
);


DROP TABLE QUESTIONPOOLMEDIA;

CREATE TABLE QUESTIONPOOLMEDIA (
        QUESTIONPOOLID      number,
        MEDIAID             number,
        POSITION            number
);

