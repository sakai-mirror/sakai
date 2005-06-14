DROP TABLE SECTIONITEM;
DROP TABLE ITEMTAKEN;
DROP TABLE ANSWERTAKEN;
DROP TABLE ITEMPUBLISHED;
DROP TABLE ITEMMEDIA;
DROP TABLE ITEM;
DROP TABLE ITEMTEMPLATE;
DROP TABLE ASSESSMENTTAKEN;
DROP TABLE ASSESSMENTSECTION;
DROP TABLE ASSESSMENTPUBLISHED;
DROP TABLE ASSESSMENTMEDIA;
DROP TABLE ASSESSMENTIPADDRESSPERMISSION;
DROP TABLE ASSESSMENT;
DROP TABLE IDLIST;
DROP TABLE ASSESSMENTTEMPLATE;
DROP TABLE ASSESSMENTDISTRIBUTION;
DROP TABLE DISTRIBUTIONGROUP;
DROP TABLE ASSESSMENTACCESSGROUP;
DROP TABLE ACCESSGROUP;
DROP TABLE STANFORDTYPE;
DROP TABLE ANSWER;
DROP TABLE ITEMANSWER;
DROP TABLE EVALUATIONMETHOD;
DROP TABLE FEEDBACK;
DROP TABLE ACCESSIP;
DROP TABLE IPADDRESS;
DROP TABLE SECTIONMEDIA;
DROP TABLE MEDIA;
DROP TABLE MEDIATYPE;
DROP TABLE PERMISSION;
DROP TABLE ROLE;
DROP TABLE SECTIONTAKEN;
DROP TABLE SECTIONPUBLISHED;
DROP TABLE SECTION;
DROP TABLE SECTIONTEMPLATE;
DROP TABLE SUBMISSIONMODEL;
DROP TABLE ITEMGRADING;
DROP TABLE ASSESSMENTGRADING;
DROP TABLE QUESTIONPOOL;
DROP TABLE QUESTIONPOOLITEM;
DROP TABLE QUESTIONPOOLMEDIA;
DROP TABLE QUESTIONPOOLACCESS;
DROP TABLE INTELLECTUALPROPERTY;

CREATE TABLE SECTIONITEM (
       SECTIONID            numeric(10),
       ITEMID               numeric,
       POSITION             numeric
);

CREATE TABLE ITEMTAKEN (
       ITEMID               numeric,
       ITEMTAKENID          numeric,
       SECTIONTAKENID       numeric,
       AUTOSAVED            varchar(1),
       SUBMITDATE           DATE,
       AUTOSAVEDATE         DATE,
       FEEDBACK             LONGVARCHAR,
       SCORE                varchar(255),
       FEEBACKMEDIAID       numeric
);

CREATE TABLE ANSWERTAKEN (
       ITEMTAKENID          numeric,
       ANSWERID             numeric,
       ANSWERTEXT           LONGVARCHAR,
       ANSWERMEDIAID        numeric
);

CREATE TABLE ITEMPUBLISHED (
       ITEMID               numeric,
       PUBLISHDATE          DATE,
       COURSEID             numeric
);


CREATE TABLE ITEMMEDIA (
       ITEMID               numeric,
       MEDIAID              numeric,
       POSITION             numeric
);


CREATE TABLE ITEM (
       ITEMID               numeric,
       TITLE                varchar(255),
       TEXT                 varchar(255),
       DESCRIPTION          varchar(255),
       TYPEID               numeric,
       OBJECTIVE            varchar(255),
       KEYWORDS             varchar(255),
       RUBRICS              varchar(255),
       VALUE                varchar(80),
       HINT                 varchar(255),
       ISTEMPLATE           varchar(1),
       TEMPLATEID           numeric,
       ASSESSMENTTEMPLATEID numeric(10),
       FEEDBACK             LONGVARCHAR,
       ADDPAGEBREAK         varchar(1),
       HASRATIONALE         varchar(1)
);

CREATE TABLE ITEMTEMPLATE (
       ITEMID               numeric,
       FIELD                varchar(255),
       ISINSTRUCTOREDITABLE varchar(1),
       ISINSTRUCTORVIEWABLE varchar(1),
       ISSTUDENTVIEWABLE    varchar(1)
);

CREATE TABLE ASSESSMENTTAKEN (
       ASSESSMENTTAKENID    numeric,
       DATECOMPLETED        DATE,
       AGENTID              varchar(255),
       COURSEID             numeric,
       SUBMISSIONnumeric     numeric,
       FEEDBACK             LONGVARCHAR,
       FEEDBACKMEDIAID      numeric,
       ADJUSTSCORE          varchar(255),
       ASSESSMENTID         numeric
);


CREATE TABLE ASSESSMENTSECTION (
       SECTIONID            numeric(10),
       ASSESSMENTID         numeric,
       POSITION             numeric
);


CREATE TABLE ASSESSMENTPUBLISHED (
       PUBLISHDATE          DATE,
       ASSESSMENTID         numeric,
       COURSEID             numeric
);


CREATE TABLE ASSESSMENTMEDIA (
       MEDIAID              numeric,
       ASSESSMENTID         numeric,
       POSITION             numeric
);


CREATE TABLE ASSESSMENTIPADDRESSPERMISSION (
       ASSESSMENTPERMISSIONIPADDRESSI numeric,
       PERMISSIONID         numeric,
       IPADDRESSID          numeric,
       ASSESSMENTID         numeric
);


CREATE TABLE ASSESSMENT (
       ASSESSMENTID         numeric,
       TITLE                varchar(255),
       DUEDATE              DATE,
       SUBMISSIONMODELID    numeric,
       SUBMISSIONSSAVED     varchar(255),
       PARENTID             numeric,
       GRADEAVAILABILITY    DATE,
       EVAULATIONID         numeric,
       ISSTUDENTIDPUBLIC    varchar(1),
       COURSEID             numeric,
       AGENTID              varchar(255),
       DESCRIPTION          varchar(255),
       OBJECTIVE            varchar(255),
       METADATA             varchar(4000),
       STARTDATE            DATE,
       TIMELIMIT            numeric,
       TEMPLATEID           numeric,      
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
       DEFAULTQUESTIONVALUE numeric,
       FIXEDTOTALSCORE      numeric,
       INSTRUCTORNOTIFICATION varchar(255),
       TESTEENOTIFICATION   varchar(255),
       LATEHANDLING         varchar(255),
       TIMEDASSESSMENT      numeric,
       DISPLAYCHUNKING      varchar(255),
       KEYWORDS             varchar(255),
       RUBRICS              varchar(255),
       TYPEID               numeric,
       CLASS_NAME           varchar(255),
       PROPERTIESID         numeric,
       RETRYALLOWED         numeric,
       FEEDBACKTYPE         varchar(255),
       IMMFEEDBACKTYPE      varchar(255),
       DATEDFEEDBACKTYPE    varchar(255),
       PERQFEEDBACKTYPE     varchar(255),
       FEEDBACKDATE         DATE,
       SCOREDATE            DATE,
       DATECREATED          DATE,
       LASTMODIFIED         DATE,
       ANONYMOUSGRADING     varchar(1),
       TOGRADEBOOK          varchar(255),
       RECORDEDSCORE        varchar(255),
       FEEDBACKCOMPONENTS   varchar(255),
       AUTOSAVE             varchar(255),
       QUESTIONnumericING    varchar(255)      
);

CREATE TABLE IDLIST (
       TABLENAME            varchar(80),
       NEXTID               numeric
);

CREATE TABLE ASSESSMENTTEMPLATE (
       ASSESSMENTID         numeric,
       FIELD                varchar(255),
       ISINSTRUCTOREDITABLE varchar(1),
       ISINSTRUCTORVIEWABLE varchar(1),
       ISSTUDENTVIEWABLE    varchar(1)
);

CREATE TABLE ASSESSMENTDISTRIBUTION (
       ASSESSMENTID         numeric,
       DISTRIBUTIONGROUPID  numeric
);

CREATE TABLE DISTRIBUTIONGROUP (
       DISTRIBUTIONGROUPID  numeric,
       NAME                 varchar(255),
       DISTRIBUTIONTYPES    LONGVARCHAR
);

CREATE TABLE ASSESSMENTACCESSGROUP (
       ASSESSMENTID         numeric,
       ACCESSGROUPID        numeric
);

CREATE TABLE ACCESSGROUP (
       ACCESSGROUPID        numeric,
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
       MINUTES              numeric,
       PASSWORDACCESS       varchar(1),
       PASSWORD             varchar(255),
       ISACTIVE             varchar(1),
       IPACCESS             varchar(255)
);


CREATE TABLE STANFORDTYPE (
       TYPEID               numeric,
       DESCRIPTION          varchar(255),
       KEYWORD              varchar(255),
       DOMAIN               varchar(255),
       AUTHORITY            varchar(255)
);


CREATE TABLE ANSWER (
       ANSWERID             numeric,
       TEXT                 varchar(255),
       ISCORRECT            varchar(1),
       FEEDBACK             varchar(255),
       VALUE                varchar(255)
);

CREATE TABLE ITEMANSWER (
       ITEMID               numeric,
       ANSWERID             numeric,
       POSITION             numeric
);

CREATE TABLE EVALUATIONMETHOD (
       EVAULATIONID         numeric,
       EVALUATIONMETHOD     varchar(255)
);


CREATE TABLE FEEDBACK (
       FEEDBACKID           numeric,
       FEEDBACKLEVEL        varchar(255)
);

CREATE TABLE ACCESSIP (
       ACCESSGROUPID        numeric,
       IPADDRESSID          numeric
);

CREATE TABLE IPADDRESS (
       IPADDRESSID          numeric,
       IPADDRESS            varchar(255)
);


CREATE TABLE SECTIONMEDIA (
       SECTIONID            numeric(10),
       MEDIAID              numeric,
       POSITION             numeric
);


CREATE TABLE MEDIA (
       MEDIAID              numeric,
       MEDIA                LONGVARBINARY,
       MEDIATYPEID          numeric,
       DESCRIPTION          varchar(255),
       LOCATION             varchar(255),
       NAME                 varchar(255),
       AUTHOR               varchar(255),
       FILENAME             varchar(255),
       DATEADDED            DATE,
       ISLINK               varchar(1),
       ISHTMLINLINE         varchar(1)
);


CREATE TABLE MEDIATYPE (
       MEDIATYPEID          numeric,
       MEDIATYPE            varchar(255),
       RECORDER             varchar(255),
       PLAYER               varchar(255),
       ICONURL              varchar(255),
       DESCRIPTION          varchar(255) 
);


CREATE TABLE PERMISSION (
       PERMISSIONID         numeric,
       PERMISSION           numeric,
       DESCRIPTION          varchar(255)
);


CREATE TABLE ROLE (
       ROLEID               numeric,
       ROLENAME             varchar(255)
);

CREATE TABLE SECTIONTAKEN (
       SECTIONID            numeric,
       SECTIONTAKENID       numeric,
       ASSESSMENTTAKENID    numeric,
       DATECOMPLETED        DATE,
       FEEDBACK             LONGVARCHAR,
       FEEDBACKMEDIAID      numeric
);

CREATE TABLE SECTIONPUBLISHED (
       PUBLISHDATE          DATE,
       SECTIONID            numeric(10),
       COURSEID             numeric
);


CREATE TABLE SECTION (
       SECTIONID            numeric(10),
       TITLE                varchar(255),
       PSECTIONID           numeric,
       DESCRIPTION          varchar(255),
       TYPEID               numeric,
       OBJECTIVE            varchar(255),
       KEYWORDS             varchar(255),
       RUBRICS              varchar(255),
       QUESTIONORDER        varchar(255),
       RANDOMPOOLID         numeric,
       RANDOMnumeric        numeric,
       ISTEMPLATE           varchar(1),
       TEMPLATEID           numeric,
       ASSESSMENTTEMPLATEID numeric
);

CREATE TABLE SECTIONTEMPLATE (
       SECTIONID            numeric(10),
       FIELD                varchar(255),
       ISINSTRUCTOREDITABLE varchar(1),
       ISINSTRUCTORVIEWABLE varchar(1),
       ISSTUDENTVIEWABLE    varchar(1)
);

CREATE TABLE SUBMISSIONMODEL (
       SUBMISSIONMODELID    numeric,
       SUBMISSIONMODEL      varchar(255),
       NUMOFSUBMISSIONSALLOWED numeric
);

CREATE TABLE ITEMGRADING (
        ITEMID               varchar(36),
        ASSESSMENTRESULTID   varchar(36),
        ITEMSCORE            varchar(255),
        ITEMMAXSCORE         varchar(255),
        ITEMTYPE             varchar(36),
        ANSWERTEXT           varchar(255), 
        ANSWERMEDIAID        numeric, 
        ANSWERCORRECT        varchar(1),
        RATIONALE           varchar(255), 
       COMMENTS              varchar(255) 
 );

CREATE TABLE ASSESSMENTGRADING (
       ASSESSMENTID         varchar(36),
       ASSESSMENTRESULTID   varchar(36),
       RESULTDATE      DATE,
       AGENTID              varchar(255),
       AGENTNAME            varchar(255),
       AGENTROLE            varchar(255),
       MAXSCORE             varchar(255),
       SCORE                varchar(255),
       ADJUSTSCORE          varchar(255),
       FINALSCORE           varchar(255),
       COMMENTS             varchar(255), 
       ISLATE               varchar(1)
);

CREATE TABLE QUESTIONPOOL (
	QUESTIONPOOLID      numeric,
	PARENTPOOLID        numeric,
        OWNERID             varchar(255),
        ORGANIZATIONNAME    varchar(255),
        DATECREATED         DATE,
        LASTMODIFIED        DATE,
        LASTMODIFIEDBY      varchar(255),
        DEFAULTACCESSTYPEID numeric,
        TITLE               varchar(255),
        DESCRIPTION         varchar(255),
        OBJECTIVE           varchar(255),
        KEYWORDS            varchar(255),
        RUBRIC              varchar(4000),
        TYPEID              numeric,
        INTELLECTUALPROPERTYID numeric
);

CREATE TABLE QUESTIONPOOLITEM (
        QUESTIONPOOLID      numeric,
        ITEMID              varchar(36)
);

CREATE TABLE QUESTIONPOOLMEDIA (
        QUESTIONPOOLID      numeric,
        MEDIAID             numeric,
        POSITION            numeric
);

CREATE TABLE QUESTIONPOOLACCESS (
        QUESTIONPOOLID      numeric,
        AGENTID             varchar(255),
        ACCESSTYPEID        numeric
);

CREATE TABLE INTELLECTUALPROPERTY (
        INTELLECTUALPROPERTYID numeric,
        PROPERTYINFO           LONGVARCHAR
);

INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(2, 'Quiz', 'assessment', 'Stanford', 'AAM');
INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(3, 'Test', 'assessment', 'Stanford', 'AAM');
INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(4, 'Exam', 'assessment', 'Stanford', 'AAM');
INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(5, 'Problem Set', 'assessment', 'Stanford', 'AAM');
INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(6, 'Self Study Quiz', 'assessment', 'Stanford', 'AAM');
INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(7, 'Paper Submission', 'assessment', 'Stanford', 'AAM');
INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(8, 'Project Submission', 'assessment', 'Stanford', 'AAM');

INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(20, 'Multiple Choice', 'item', 'Stanford', 'AAM');
INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(21, 'True/False', 'item', 'Stanford', 'AAM');
INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(22, 'Short Answer', 'item', 'Stanford', 'AAM');
INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(23, 'File Upload', 'item', 'Stanford', 'AAM');
INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(24, 'Record Audio', 'item', 'Stanford', 'AAM');

INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(30, 'Access Denied', 'access', 'Stanford', 'AAM');
INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(31, 'Read Only', 'access', 'Stanford', 'AAM');
INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(32, 'Read and Copy', 'access', 'Stanford', 'AAM');
INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(33, 'Read/Write', 'access', 'Stanford', 'AAM');
INSERT INTO STANFORDTYPE (TYPEID, DESCRIPTION, KEYWORD, DOMAIN, AUTHORITY)
  VALUES(34, 'Administration', 'access', 'Stanford', 'AAM');

INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(1,'image/pjpeg',null,null,'images/jpg.gif','JPEG Image');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(2,'image/jpeg',null,null,'images/jpg.gif','JPEG Image');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(3,'text/plain',null,null,'images/txt.gif','Text Document');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(4,'image/gif',null,null,'images/gif.gif','GIF Image');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(5,'audio/x-ms-wma',null,null,'images/unknown.gif','Unknown');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(6,'application/octet-stream',null,null,'images/exe.gif','PC Executable');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(7,'application/x-macbinary',null,null,'images/jpg.gif','Mac JPEG Image');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(8,'application/msword',null,null,'images/doc.gif','Microsoft Word');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(9,'application/pdf',null,null,'images/pdf.gif','PDF Document');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(10,'image/bmp',null,null,'images/bmp.gif','Bitmap Image');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(11,'text/html',null,null,'images/html.gif','HTML Document');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(12,'application/postscript',null,null,'images/Ppt.gif','Postscript File');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(13,'application/vnd.ms-excel',null,null,'images/xls.gif','Microsoft Excel');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(14,'text/richtext',null,null,'images/doc.gif','Microsoft Word');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(15,'application/vnd.ms-powerpoint',null,null,'images/Ppt.gif','Microsoft PowerPoint');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(16,'application/msexcel',null,null,'images/xls.gif','Microsoft Excel');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(17,'audio/x-wav',null,null,'images/wav.gif','WAV Sound Clip');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(18,'image/x-ms-bmp',null,null,'images/bmp.gif','Bitmap Image');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(19,'video/mpeg',null,null,'images/mpg.gif','MPEG Video Clip');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(20,'application/zip',null,null,'images/zip.gif','ZIP Archive');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(21,'image/tiff',null,null,'images/tif.gif','TIFF Image');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(22,'Quicktime Video',null,null,'images/mov.gif','Quicktime Video');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(23,'unknown',null,null,'images/unknown.gif','Unknown');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(24,'audio/mp3',null,null,'images/Mp3.gif','MP3 Audio');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(25,'audio/x-pn-realaudio-plugin',null,null,'images/real.gif','Real Player');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(26,'application/mac-binhex40',null,null,'images/zip.gif','Mac Binhex Archive');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(27,'application/x-stuffit',null,null,'images/zip.gif','Stuffit Archive');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(28,'image/x-djvu',null,null,'images/djvu.gif','DjVu Document');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(29,'application/vnd.rn-realmedia',null,null,'images/real.gif','Real Player');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(30,'',null,null,'images/unknown.gif','Unknown');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(31,'application/x-zip-compressed',null,null,'images/zip.gif','ZIP Archive');
INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE, RECORDER, PLAYER, ICONURL, DESCRIPTION)
  VALUES(32,'audio/basic',null,null,'images/au.gif','Audio Clip');

INSERT into IDLIST VALUES("MEDIATYPE", 100);

