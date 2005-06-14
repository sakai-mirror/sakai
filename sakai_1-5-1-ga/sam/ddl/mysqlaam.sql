drop table authorizations;
drop table functions;
drop table qualifiers;
drop table types;
drop table agents;
drop table groups;

CREATE TABLE groups (
group_id VARCHAR(255) primary key,
type_id VARCHAR(255),
oc_group_id VARCHAR(255),
ol_timestamp date
);

create table types(
   type_id varchar(32) primary key,
   authority varchar(100) not null,
   domain varchar(100) not null,
   keyword varchar(100) not null,
   description varchar(100),
   ol_timestamp date
);

create table functions(
   function_id varchar(32) primary key,
   display_name varchar(50),
   description varchar(100),
   function_type varchar(32) not null,
   qualifier_hierarchy_id varchar(32),
   ol_timestamp date
);

create table qualifiers(
   qualifier_id varchar(32) primary key,
   display_name varchar(50),
   description varchar(100),
   qualifier_type varchar(200) not null,
   qualifier_hierarchy_id varchar(32),
   ol_timestamp date 
);

create table authorizations(
   agent_id varchar(32) not null,
   function_id varchar(32) not null,
   qualifier_id varchar(32) not null,
   effective_date date,
   expiration_date date,
   modifier_id varchar(32),
   modified_date date,
   is_explicit char(1),
   ol_timestamp date,
   constraint pk_unique primary key (agent_id,function_id,qualifier_id)
);

create table agents(
   agent_id varchar(32) primary key,
   type_id varchar(32) not null,
   ps_empl_id varchar(11),
   kerberos_id varchar(32),
   ip_addr varchar(15),
   ol_timestamp date
);

drop table dr_asset_map;
drop table assets;

create table dr_asset_map(
   dr_id varchar(32) not null,
   asset_id varchar(32) not null,
   agent_id varchar(32),   
   constraint pk_assets primary key (dr_id,asset_id),
   ol_timestamp date
);

create table assets(
   id varchar(32)  primary key,        
   created date,
   data blob,
   description blob,
   title varchar(128),
   dr_id varchar(32),
   effective_date date,
   expiration_date date,
   type_id varchar(32) not null,
   ol_timestamp date
);

DROP TABLE SECTIONITEM;

CREATE TABLE SECTIONITEM (
       SECTIONID            numeric(10),
       ITEMID               integer,
       POSITION             integer
);

DROP TABLE ITEMTAKEN;

CREATE TABLE ITEMTAKEN (
       ITEMID               integer,
       ITEMTAKENID          integer,
       SECTIONTAKENID       integer,
       AUTOSAVED            varchar(1),
       SUBMITDATE           datetime,
       AUTOSAVEDATE         datetime,
       FEEDBACK             long varbinary,
       SCORE                varchar(255),
       FEEBACKMEDIAID       integer
);

DROP TABLE ANSWERTAKEN;

CREATE TABLE ANSWERTAKEN (
       ITEMTAKENID          integer,
       ANSWERID             integer,
       ANSWERTEXT           long varbinary,
       ANSWERMEDIAID        integer
);

DROP TABLE ITEMPUBLISHED;

CREATE TABLE ITEMPUBLISHED (
       ITEMID               integer,
       PUBLISHDATE          datetime,
       COURSEID             integer
);


DROP TABLE ITEMMEDIA;

CREATE TABLE ITEMMEDIA (
       ITEMID               integer,
       MEDIAID              integer,
       POSITION             integer
);


DROP TABLE ITEM;

CREATE TABLE ITEM (
       ITEMID               integer,
       TITLE                varchar(255),
       TEXT                 long varchar,
       DESCRIPTION          varchar(255),
       TYPEID               integer,
       OBJECTIVE            varchar(255),
       KEYWORDS             varchar(255),
       RUBRICS              varchar(255),
       VALUE                varchar(80),
       HINT                 long varchar,
       ISTEMPLATE           varchar(1),
       TEMPLATEID           integer,
       ASSESSMENTTEMPLATEID numeric(10),
       FEEDBACK             long varchar,
       ADDPAGEBREAK         varchar(1),
       HASRATIONALE         varchar(1)
);

DROP TABLE ITEMTEMPLATE;

CREATE TABLE ITEMTEMPLATE (
       ITEMID               integer,
       FIELD                varchar(255),
       ISINSTRUCTOREDITABLE varchar(1),
       ISINSTRUCTORVIEWABLE varchar(1),
       ISSTUDENTVIEWABLE    varchar(1)
);

DROP TABLE ASSESSMENTTAKEN;

CREATE TABLE ASSESSMENTTAKEN (
       ASSESSMENTTAKENID    integer,
       DATECOMPLETED        datetime,
       AGENTID              varchar(255),
       COURSEID             integer,
       SUBMISSIONNUMBER     integer,
       FEEDBACK             long varbinary,
       FEEDBACKMEDIAID      integer,
       ADJUSTSCORE          varchar(255),
       ASSESSMENTID         bigint
);


DROP TABLE ASSESSMENTSECTION;

CREATE TABLE ASSESSMENTSECTION (
       SECTIONID            numeric(10),
       ASSESSMENTID         bigint,
       POSITION             integer
);


DROP TABLE ASSESSMENTPUBLISHED;

CREATE TABLE ASSESSMENTPUBLISHED (
       PUBLISHDATE          datetime,
       ASSESSMENTID         bigint,
       COURSEID             integer
);


DROP TABLE ASSESSMENTMEDIA;

CREATE TABLE ASSESSMENTMEDIA (
       MEDIAID              integer,
       ASSESSMENTID         bigint,
       POSITION             integer
);


DROP TABLE ASSESSMENTIPADDRESSPERMISSION;

CREATE TABLE ASSESSMENTIPADDRESSPERMISSION (
       ASSESSMENTPERMISSIONIPADDRESSI integer,
       PERMISSIONID         integer,
       IPADDRESSID          integer,
       ASSESSMENTID         bigint
);


DROP TABLE ASSESSMENT;

CREATE TABLE ASSESSMENT (
       ASSESSMENTID         bigint,
       TITLE                varchar(255),
       DUEDATE              datetime,
       SUBMISSIONMODELID    integer,
       SUBMISSIONSSAVED     varchar(255),
       PARENTID             integer,
       GRADEAVAILABILITY    datetime,
       EVAULATIONID         integer,
       ISSTUDENTIDPUBLIC    varchar(1),
       COURSEID             integer,
       AGENTID              varchar(255),
       DESCRIPTION          long varbinary,
       OBJECTIVE            long varbinary,
       METADATA             long varbinary,
       STARTDATE            datetime,
       TIMELIMIT            integer,
       TEMPLATEID           bigint,
       TEMPLATENAME         varchar(255),
       TEMPLATEAUTHOR       varchar(255),
       TEMPLATECOMMENTS     varchar(255),
       ISTEMPLATE           varchar(1),
       MULTIPARTALLOWED     varchar(1),
       ITEMACCESSTYPE       varchar(255),
       ITEMBOOKMARKING      varchar(255),
       EVALUATIONCOMPONENTS varchar(255),
       AUTOSCORING          varchar(1),
       TESTEEIDENTITY       varchar(255),
       EVALUATIONDISTRIBUTION varchar(255),
       SCORINGTYPE          varchar(255),
       NUMERICMODEL         varchar(255),
       DEFAULTQUESTIONVALUE integer,
       FIXEDTOTALSCORE      integer,
       INSTRUCTORNOTIFICATION varchar(255),
       TESTEENOTIFICATION   varchar(255),
       LATEHANDLING         varchar(255),
       TIMEDASSESSMENT      integer,
       DISPLAYCHUNKING      varchar(255),
       KEYWORDS             varchar(255),
       RUBRICS              varchar(255),
       TYPEID               integer,
       CLASS_NAME           varchar(255),
       PROPERTIESID         integer,
       RETRYALLOWED         integer,
       FEEDBACKTYPE         varchar(255),
       IMMFEEDBACKTYPE      varchar(255),
       DATEDFEEDBACKTYPE    varchar(255),
       PERQFEEDBACKTYPE     varchar(255),
       FEEDBACKDATE         datetime,
       SCOREDATE            datetime,
       DATECREATED          datetime,
       LASTMODIFIED         datetime,
       ANONYMOUSGRADING     varchar(1),
       TOGRADEBOOK          varchar(255),
       RECORDEDSCORE        varchar(255),
       FEEDBACKCOMPONENTS   varchar(255),
       AUTOSAVE             varchar(255),
       QUESTIONNUMBERING    varchar(255)
);

DROP TABLE IDLIST;

CREATE TABLE IDLIST (
       TABLENAME            varchar(80),
       NEXTID               integer
);

DROP TABLE ASSESSMENTTEMPLATE;

CREATE TABLE ASSESSMENTTEMPLATE (
       ASSESSMENTID         integer,
       FIELD                varchar(255),
       ISINSTRUCTOREDITABLE varchar(1),
       ISINSTRUCTORVIEWABLE varchar(1),
       ISSTUDENTVIEWABLE    varchar(1)
);

DROP TABLE ASSESSMENTDISTRIBUTION;

CREATE TABLE ASSESSMENTDISTRIBUTION (
       ASSESSMENTID         integer,
       DISTRIBUTIONGROUPID  integer
);

DROP TABLE DISTRIBUTIONGROUP;

CREATE TABLE DISTRIBUTIONGROUP (
       DISTRIBUTIONGROUPID  integer,
       NAME                 varchar(255),
       DISTRIBUTIONTYPES    long varbinary
);

DROP TABLE ASSESSMENTACCESSGROUP;

CREATE TABLE ASSESSMENTACCESSGROUP (
       ASSESSMENTID         integer,
       ACCESSGROUPID        integer
);

DROP TABLE ACCESSGROUP;

CREATE TABLE ACCESSGROUP (
       ACCESSGROUPID        integer,
       NAME                 varchar(255),
       RELEASETYPE          varchar(255),
       RELEASEDATE          datetime,
       RELEASEWHEN          varchar(255),
       RELEASESCORE         varchar(255),
       RETRACTTYPE          varchar(255),
       RETRACTDATE          datetime,
       ISRELEASED           datetime,
       DUEDATE              datetime,
       DUEDATETYPE          varchar(255),
       RETRYALLOWED         varchar(1),
       TIMEDASSESSMENT      varchar(1),
       MINUTES              integer,
       PASSWORDACCESS       varchar(1),
       PASSWORD             varchar(255),
       ISACTIVE             varchar(1),
       IPACCESS             varchar(255)
);


DROP TABLE STANFORDTYPE;

CREATE TABLE STANFORDTYPE (
       TYPEID               integer,
       DESCRIPTION          varchar(255),
       KEYWORD              varchar(255),
       DOMAIN               varchar(255),
       AUTHORITY            varchar(255)
);


DROP TABLE ANSWER;

CREATE TABLE ANSWER (
       ANSWERID             integer,
       TEXT                 varchar(255),
       ISCORRECT            varchar(1),
       FEEDBACK             varchar(255),
       VALUE                varchar(255)
);

DROP TABLE ITEMANSWER;

CREATE TABLE ITEMANSWER (
       ITEMID               integer,
       ANSWERID             integer,
       POSITION             integer
);

DROP TABLE EVALUATIONMETHOD;

CREATE TABLE EVALUATIONMETHOD (
       EVAULATIONID         integer,
       EVALUATIONMETHOD     varchar(255)
);


DROP TABLE FEEDBACK;

CREATE TABLE FEEDBACK (
       FEEDBACKID           integer,
       FEEDBACKLEVEL        varchar(255)
);

DROP TABLE ACCESSIP;

CREATE TABLE ACCESSIP (
       ACCESSGROUPID        integer,
       IPADDRESSID          integer
);

DROP TABLE IPADDRESS;

CREATE TABLE IPADDRESS (
       IPADDRESSID          integer,
       IPADDRESS            varchar(255)
);


DROP TABLE SECTIONMEDIA;

CREATE TABLE SECTIONMEDIA (
       SECTIONID            numeric(10),
       MEDIAID              integer,
       POSITION             integer
);


DROP TABLE MEDIA;

CREATE TABLE MEDIA (
       MEDIAID              integer,
       MEDIA                long varbinary,
       MEDIATYPEID          integer,
       DESCRIPTION          varchar(255),
       LOCATION             varchar(255),
       NAME                 varchar(255),
       AUTHOR               varchar(255),
       FILENAME             varchar(255),
       DATEADDED            datetime,
       ISLINK               varchar(1),
       ISHTMLINLINE         varchar(1)
);


DROP TABLE MEDIATYPE;

CREATE TABLE MEDIATYPE (
       MEDIATYPEID          integer,
       MEDIATYPE            varchar(255),
       RECORDER             varchar(255),
       PLAYER               varchar(255),
       ICONURL              varchar(255),
       DESCRIPTION          varchar(255) 
);


DROP TABLE PERMISSION;

CREATE TABLE PERMISSION (
       PERMISSIONID         integer,
       PERMISSION           integer,
       DESCRIPTION          varchar(255)
);


DROP TABLE ROLE;

CREATE TABLE ROLE (
       ROLEID               integer,
       ROLENAME             varchar(255)
);

DROP TABLE SECTIONTAKEN;

CREATE TABLE SECTIONTAKEN (
       SECTIONID            integer,
       SECTIONTAKENID       integer,
       ASSESSMENTTAKENID    integer,
       DATECOMPLETED        datetime,
       FEEDBACK             long varchar,
       FEEDBACKMEDIAID      integer
);

DROP TABLE SECTIONPUBLISHED;

CREATE TABLE SECTIONPUBLISHED (
       PUBLISHDATE          datetime,
       SECTIONID            numeric(10),
       COURSEID             integer
);


DROP TABLE SECTION;

CREATE TABLE SECTION (
       SECTIONID            numeric(10),
       TITLE                varchar(255),
       PSECTIONID           integer,
       DESCRIPTION          varchar(255),
       TYPEID               integer,
       OBJECTIVE            varchar(255),
       KEYWORDS             varchar(255),
       RUBRICS              varchar(255),
       QUESTIONORDER        varchar(255),
       RANDOMPOOLID         integer,
       RANDOMNUMBER         integer,
       ISTEMPLATE           varchar(1),
       TEMPLATEID           integer,
       ASSESSMENTTEMPLATEID integer
);

DROP TABLE SECTIONTEMPLATE;

CREATE TABLE SECTIONTEMPLATE (
       SECTIONID            numeric(10),
       FIELD                varchar(255),
       ISINSTRUCTOREDITABLE varchar(1),
       ISINSTRUCTORVIEWABLE varchar(1),
       ISSTUDENTVIEWABLE    varchar(1)
);

DROP TABLE SUBMISSIONMODEL;

CREATE TABLE SUBMISSIONMODEL (
       SUBMISSIONMODELID    integer,
       SUBMISSIONMODEL      varchar(255),
       NUMOFSUBMISSIONSALLOWED integer
);

DROP TABLE ITEMGRADING;

CREATE TABLE ITEMGRADING (
        ITEMID               varchar(32),
        ASSESSMENTRESULTID   varchar(32),
        ITEMSCORE            varchar(255),
        ITEMMAXSCORE         varchar(255),
        ITEMTYPE             varchar(32),
        ANSWERTEXT           varchar(255), 
        ANSWERMEDIAID        integer, 
        ANSWERCORRECT        varchar(1),
        RATIONALE           varchar(255), 
        COMMENTS              varchar(255) 
 );

DROP TABLE ASSESSMENTGRADING;

CREATE TABLE ASSESSMENTGRADING (
       ASSESSMENTID         varchar(32),
       ASSESSMENTRESULTID   varchar(32),
       RESULTDATE      DATE,
       AGENTID              varchar(255),
       AGENTNAME            varchar(255),
       AGENTROLE            varchar(255),
       MAXSCORE             varchar(255),
       SCORE                varchar(255),
       ADJUSTSCORE          varchar(255),
       FINALSCORE           varchar(255),
       COMMENTS             varchar(255) 
);

DROP TABLE QUESTIONPOOL;

CREATE TABLE QUESTIONPOOL (
	QUESTIONPOOLID      integer,
	PARENTPOOLID        integer,
        OWNERID             varchar(255),
        ORGANIZATIONNAME    varchar(255),
        DATECREATED         datetime,
        LASTMODIFIED        datetime,
        LASTMODIFIEDBY      varchar(255),
        DEFAULTACCESSTYPEID integer,
        TITLE               varchar(255),
        DESCRIPTION         long varchar,
        OBJECTIVE           long varchar,
        KEYWORDS            long varchar,
        RUBRIC              long varchar,
        TYPEID              integer,
        INTELLECTUALPROPERTYID integer
);

DROP TABLE QUESTIONPOOLITEM;

CREATE TABLE QUESTIONPOOLITEM (
        QUESTIONPOOLID      integer,
        ITEMID              varchar(32)
);

DROP TABLE QUESTIONPOOLMEDIA;

CREATE TABLE QUESTIONPOOLMEDIA (
        QUESTIONPOOLID      integer,
        MEDIAID             integer,
        POSITION            integer
);

DROP TABLE QUESTIONPOOLACCESS;

CREATE TABLE QUESTIONPOOLACCESS (
        QUESTIONPOOLID      integer,
        AGENTID             varchar(255),
        ACCESSTYPEID        integer
);

DROP TABLE INTELLECTUALPROPERTY;

CREATE TABLE INTELLECTUALPROPERTY (
        INTELLECTUALPROPERTYID integer,
        PROPERTYINFO           long varchar
);

INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(2, "Quiz", "assessment", "Stanford", "AAM");
INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(3, "Test", "assessment", "Stanford", "AAM");
INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(4, "Exam", "assessment", "Stanford", "AAM");
INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(5, "Problem Set", "assessment", "Stanford", "AAM");
INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(6, "Self Study Quiz", "assessment", "Stanford", "AAM");
INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(7, "Paper Submission", "assessment", "Stanford", "AAM");
INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(8, "Project Submission", "assessment", "Stanford", "AAM");

INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(20, "Multiple Choice", "item", "Stanford", "AAM");
INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(21, "True/False", "item", "Stanford", "AAM");
INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(22, "Short Answer", "item", "Stanford", "AAM");
INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(23, "File Upload", "item", "Stanford", "AAM");
INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(24, "Record Audio", "item", "Stanford", "AAM");

INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(30, "Access Denied", "access", "Stanford", "AAM");
INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(31, "Read Only", "access", "Stanford", "AAM");
INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(32, "Read and Copy", "access", "Stanford", "AAM");
INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(33, "Read/Write", "access", "Stanford", "AAM");
INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(34, "Administration", "access", "Stanford", "AAM");

INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(1,"image/pjpeg",null,null,"/Navigo/images/jpg.gif","JPEG Image");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(2,"image/jpeg",null,null,"/Navigo/images/jpg.gif","JPEG Image");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(3,"text/plain",null,null,"/Navigo/images/txt.gif","Text Document");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(4,"image/gif",null,null,"/Navigo/images/gif.gif","GIF Image");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(5,"audio/x-ms-wma",null,null,"/Navigo/images/unknown.gif","Unknown");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(6,"application/octet-stream",null,null,"/Navigo/images/exe.gif","PC Executable");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(7,"application/x-macbinary",null,null,"/Navigo/images/jpg.gif","Mac JPEG Image");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(8,"application/msword",null,null,"/Navigo/images/doc.gif","Microsoft Word");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(9,"application/pdf",null,null,"/Navigo/images/pdf.gif","PDF Document");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(10,"image/bmp",null,null,"/Navigo/images/bmp.gif","Bitmap Image");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(11,"text/html",null,null,"/Navigo/images/html.gif","HTML Document");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(12,"application/postscript",null,null,"/Navigo/images/Ppt.gif","Postscript File");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(13,"application/vnd.ms-excel",null,null,"/Navigo/images/xls.gif","Microsoft Excel");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(14,"text/richtext",null,null,"/Navigo/images/doc.gif","Microsoft Word");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(15,"application/vnd.ms-powerpoint",null,null,"/Navigo/images/Ppt.gif","Microsoft PowerPoint");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(16,"application/msexcel",null,null,"/Navigo/images/xls.gif","Microsoft Excel");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(17,"audio/x-wav",null,null,"/Navigo/images/wav.gif","WAV Sound Clip");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(18,"image/x-ms-bmp",null,null,"/Navigo/images/bmp.gif","Bitmap Image");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(19,"video/mpeg",null,null,"/Navigo/images/mpg.gif","MPEG Video Clip");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(20,"application/zip",null,null,"/Navigo/images/zip.gif","ZIP Archive");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(21,"image/tiff",null,null,"/Navigo/images/tif.gif","TIFF Image");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(22,"Quicktime Video",null,null,"/Navigo/images/mov.gif","Quicktime Video");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(23,"unknown",null,null,"/Navigo/images/unknown.gif","Unknown");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(24,"audio/mp3",null,null,"/Navigo/images/Mp3.gif","MP3 Audio");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(25,"audio/x-pn-realaudio-plugin",null,null,"/Navigo/images/real.gif","Real Player");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(26,"application/mac-binhex40",null,null,"/Navigo/images/zip.gif","Mac Binhex Archive");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(27,"application/x-stuffit",null,null,"/Navigo/images/zip.gif","Stuffit Archive");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(28,"image/x-djvu",null,null,"/Navigo/images/djvu.gif","DjVu Document");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(29,"application/vnd.rn-realmedia",null,null,"/Navigo/images/real.gif","Real Player");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(30,"",null,null,"/Navigo/images/unknown.gif","Unknown");
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(31,"application/x-zip-compressed",null,null,"/Navigo/images/zip.gif","ZIP Archive");

