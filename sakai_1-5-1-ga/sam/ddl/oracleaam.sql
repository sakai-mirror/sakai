SET ESCAPE \;

-- assessment templates are stored in this table,
-- beware that assessments are stored as ASSETS in ASSET.DATA (blob) 
DROP TABLE ASSESSMENT;

CREATE TABLE ASSESSMENT (
       ASSESSMENTID         number,
       TITLE                varchar(255),
       DUEDATE              DATE,
       SUBMISSIONMODELID    number,
       SUBMISSIONSSAVED     varchar(255),
       PARENTID             number,
       GRADEAVAILABILITY    DATE,
       EVAULATIONID         number,
       ISSTUDENTIDPUBLIC    varchar(1),
       COURSEID             number,
       AGENTID              varchar(255),
       DESCRIPTION          varchar(255),
       OBJECTIVE            varchar(255),
       METADATA             varchar(4000),
       STARTDATE            DATE,
       TIMELIMIT            number,
       TEMPLATEID           number,      
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
       DEFAULTQUESTIONVALUE number,
       FIXEDTOTALSCORE      number,
       INSTRUCTORNOTIFICATION varchar(255),
       TESTEENOTIFICATION   varchar(255),
       LATEHANDLING         varchar(255),
       TIMEDASSESSMENT      number,
       DISPLAYCHUNKING      varchar(255),
       KEYWORDS             varchar(255),
       RUBRICS              varchar(255),
       TYPEID               number,
       CLASS_NAME           varchar(255),
       PROPERTIESID         number,
       RETRYALLOWED         number,
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
       QUESTIONNUMBERING    varchar(255)      
);

DROP TABLE ASSESSMENTTEMPLATE;

CREATE TABLE ASSESSMENTTEMPLATE (
       ASSESSMENTID         number,
       FIELD                varchar(255),
       ISINSTRUCTOREDITABLE varchar(1),
       ISINSTRUCTORVIEWABLE varchar(1),
       ISSTUDENTVIEWABLE    varchar(1)
);




-- need explanation
DROP TABLE IDLIST;

CREATE TABLE IDLIST (
       TABLENAME            varchar(80),
       NEXTID               number
);




-- keep records for any media uploaded or referenced
-- media can be a http reference, stored as file in server or stored as blob in the table
DROP TABLE MEDIA;

CREATE TABLE MEDIA (
       MEDIAID              number,
       MEDIA                blob default empty_blob(),
       MEDIATYPEID          number,
       DESCRIPTION          varchar(255),
       LOCATION             varchar(255),
       NAME                 varchar(255),
       AUTHOR               varchar(255),
       FILENAME             varchar(255),
       DATEADDED            DATE,
       ISLINK               varchar(1),
       ISHTMLINLINE         varchar(1)
);


DROP TABLE MEDIATYPE;

CREATE TABLE MEDIATYPE (
       MEDIATYPEID          number,
       MEDIATYPE            varchar(255),
       RECORDER             varchar(255),
       PLAYER               varchar(255),
       ICONURL              varchar(255),
       DESCRIPTION          varchar(255) 
);




-- need explanation
DROP TABLE SUBMISSIONMODEL;

CREATE TABLE SUBMISSIONMODEL (
       SUBMISSIONMODELID    number,
       SUBMISSIONMODEL      varchar(255),
       NUMOFSUBMISSIONSALLOWED number
);



-- Question Pool, lydia and gpathy 
DROP TABLE QUESTIONPOOL;

CREATE TABLE QUESTIONPOOL (
	QUESTIONPOOLID      number,
	PARENTPOOLID        number(38),
        OWNERID             varchar(255),
        ORGANIZATIONNAME    varchar(255),
        DATECREATED         DATE,
        LASTMODIFIED        DATE,
        LASTMODIFIEDBY      varchar(255),
        DEFAULTACCESSTYPEID number,
        TITLE               varchar(255),
        DESCRIPTION         varchar(255),
        OBJECTIVE           varchar(255),
        KEYWORDS            varchar(255),
        RUBRIC              varchar(4000),
        TYPEID              number,
        INTELLECTUALPROPERTYID number
);

DROP TABLE QUESTIONPOOLITEM;

CREATE TABLE QUESTIONPOOLITEM (
        QUESTIONPOOLID      number,
        ITEMID              varchar(36)
);

DROP TABLE QUESTIONPOOLACCESS;

CREATE TABLE QUESTIONPOOLACCESS (
        QUESTIONPOOLID      number,
        AGENTID             varchar(255),
        ACCESSTYPEID        number
);

-- We are currently not using SECTIONITEM. However, before dropping this table,
-- you may want to clean up methods in the following classes that use it
-- data/dao/QuestionPoolAccessObject
-- osid/questionpool/QuestionPoolFactory
-- osid/questionpool/QuestionPoolDelegate
-- osid/questionpool/ejb/QuestionPoolService
-- osid/questionpool/ejb/QuestionPoolServiceBean
-- lydial, gpathy, daisyf (06/14/04)
DROP TABLE SECTIONITEM;

CREATE TABLE SECTIONITEM (
       SECTIONID            numeric(10),
       ITEMID               number,
       POSITION             number
);




-- Charles Kern's request, keep for now, rgollub
DROP TABLE INTELLECTUALPROPERTY;

CREATE TABLE INTELLECTUALPROPERTY (
        INTELLECTUALPROPERTYID number,
        PROPERTYINFO           varchar(4000)
);




-- For Grading Extration, esmiley & lydial
DROP TABLE ITEMGRADING;

CREATE TABLE ITEMGRADING (
        ITEMID               varchar(36),
        ASSESSMENTRESULTID   varchar(36),
        ITEMSCORE            varchar(255),
        ITEMMAXSCORE         varchar(255),
        ITEMTYPE             varchar(36),
        ANSWERTEXT           varchar(255),
        ANSWERMEDIAID        number,
        ANSWERCORRECT        varchar(1),
        RATIONALE           varchar(255),
       COMMENTS              varchar(255)
 );

DROP TABLE ASSESSMENTGRADING;

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


-- terms used by Stanford?
DROP TABLE STANFORDTYPE;

CREATE TABLE STANFORDTYPE (
       TYPEID               number,
       DESCRIPTION          varchar(255),
       KEYWORD              varchar(255),
       DOMAIN               varchar(255),
       AUTHORITY            varchar(255)
);

-- to run Samigo out of the box, create a user with username "admin" 
-- in reference_httpbasicpersons
DROP TABLE REFERENCE_HTTPBASICPERSONS;

CREATE TABLE REFERENCE_HTTPBASICPERSONS(
   USERNAME                VARCHAR(255) PRIMARY KEY,
   DISPLAY_NAME            VARCHAR(255)
);

INSERT into REFERENCE_HTTPBASICPERSONS VALUES('admin', 'Administrator');

DROP TABLE REFERENCE_COURSE_GROUPS;

CREATE TABLE REFERENCE_COURSE_GROUPS(
   ID                VARCHAR(255) PRIMARY KEY,
   GROUP_ID          VARCHAR(255),
   USERNAME          VARCHAR(255)
);

DROP TABLE REFERENCE_COURSE_MEMBERS;

CREATE TABLE REFERENCE_COURSE_MEMBERS(
   ID                VARCHAR(255) PRIMARY KEY,
   COURSE_ID         VARCHAR(255),
   USERNAME          VARCHAR(255),
   ROLE              VARCHAR(255)
);

DROP TABLE REFERENCE_COURSE_SECTIONS;

CREATE TABLE REFERENCE_COURSE_SECTIONS(
   COURSE_ID              VARCHAR(255) PRIMARY KEY,
   AUTHOR_NAME            VARCHAR(255)
);


-- populate StanfordType info
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

-- any new mediaTypeID will starts with 100+
INSERT into IDLIST VALUES('MEDIATYPE', 100);

