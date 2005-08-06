drop table CM_SESSION_T cascade constraints;
drop table CM_SESSIONTYPE_T cascade constraints;
drop sequence CM_SESSION_ID_S;
drop sequence CM_SESSIONTYPE_ID_S;
create table CM_SESSION_T (
   SESSIONID number(19,0) not null,
   TITLE varchar2(255) not null,
   ABBREVIATION varchar2(255) not null,
   YEAR varchar2(255) not null,
   ISCURRENT number(1,0) not null,
   UUID varchar2(255) not null,
   SESSIONTYPEUUID varchar2(255),
   CREATEDBY varchar2(255) not null,
   CREATEDDATE date not null,
   LASTMODIFIEDBY varchar2(255) not null,
   LASTMODIFIEDDATE date not null,
   primary key (SESSIONID)
);
create table CM_SESSIONTYPE_T (
   SESSIONTYPEID number(19,0) not null,
   AUTHORITY varchar2(255) not null,
   DOMAIN varchar2(255) not null,
   KEYWORD varchar2(255) not null,
   DISPLAYNAME varchar2(255),
   DESCRIPTION varchar2(255),
   UUID varchar2(255) not null,
   STATUS number(10,0) not null,
   LASTMODIFIEDBY varchar2(255) not null,
   LASTMODIFIEDDATE date not null,
   CREATEDBY varchar2(255) not null,
   CREATEDDATE date not null,
   primary key (SESSIONTYPEID)
);
create sequence CM_SESSION_ID_S;
create sequence CM_SESSIONTYPE_ID_S;

insert into CM_SESSIONTYPE_T ("SESSIONTYPEID", "AUTHORITY", "DOMAIN", "KEYWORD",
    "DISPLAYNAME", "DESCRIPTION", "UUID", 
    "STATUS" ,"CREATEDBY" ,"CREATEDDATE" ,"LASTMODIFIEDBY" , "LASTMODIFIEDDATE" )
    VALUES (CM_SESSIONTYPE_ID_S.nextVal, 'org.sakaiproject' ,'coursemgmt' ,'term.unknown' ,
    'Unknown', NULL, '*uuid_session_1',
    1, 'site', SYSDATE , 'site' ,SYSDATE);
insert into CM_SESSIONTYPE_T ("SESSIONTYPEID", "AUTHORITY", "DOMAIN", "KEYWORD",
    "DISPLAYNAME", "DESCRIPTION", "UUID", 
    "STATUS" ,"CREATEDBY" ,"CREATEDDATE" ,"LASTMODIFIEDBY" , "LASTMODIFIEDDATE" )
    VALUES (CM_SESSIONTYPE_ID_S.nextVal, 'org.sakaiproject' ,'coursemgmt' ,'term.spring' ,
    'Unknown', NULL, '*uuid_session_2',
    1, 'site', SYSDATE , 'site' ,SYSDATE);
insert into CM_SESSIONTYPE_T ("SESSIONTYPEID", "AUTHORITY", "DOMAIN", "KEYWORD",
    "DISPLAYNAME", "DESCRIPTION", "UUID", 
    "STATUS" ,"CREATEDBY" ,"CREATEDDATE" ,"LASTMODIFIEDBY" , "LASTMODIFIEDDATE" )
    VALUES (CM_SESSIONTYPE_ID_S.nextVal, 'org.sakaiproject' ,'coursemgmt' ,'term.summer' ,
    'Unknown', NULL, 'uuuid_session_3',
    1, 'site', SYSDATE , 'site' ,SYSDATE);
insert into CM_SESSIONTYPE_T ("SESSIONTYPEID", "AUTHORITY", "DOMAIN", "KEYWORD",
    "DISPLAYNAME", "DESCRIPTION", "UUID", 
    "STATUS" ,"CREATEDBY" ,"CREATEDDATE" ,"LASTMODIFIEDBY" , "LASTMODIFIEDDATE" )
    VALUES (CM_SESSIONTYPE_ID_S.nextVal, 'org.sakaiproject' ,'coursemgmt' ,'term.fall' ,
    'Unknown', NULL, 'uuuid_session_4',
    1, 'site', SYSDATE , 'site' ,SYSDATE);
insert into CM_SESSIONTYPE_T ("SESSIONTYPEID", "AUTHORITY", "DOMAIN", "KEYWORD",
    "DISPLAYNAME", "DESCRIPTION", "UUID", 
    "STATUS" ,"CREATEDBY" ,"CREATEDDATE" ,"LASTMODIFIEDBY" , "LASTMODIFIEDDATE" )
    VALUES (CM_SESSIONTYPE_ID_S.nextVal, 'org.sakaiproject' ,'coursemgmt' ,'term.winter' ,
    'Unknown', NULL, 'uuuid_session_5',
    1, 'site', SYSDATE , 'site' ,SYSDATE);
insert into CM_SESSIONTYPE_T ("SESSIONTYPEID", "AUTHORITY", "DOMAIN", "KEYWORD",
    "DISPLAYNAME", "DESCRIPTION", "UUID", 
    "STATUS" ,"CREATEDBY" ,"CREATEDDATE" ,"LASTMODIFIEDBY" , "LASTMODIFIEDDATE" )
    VALUES (CM_SESSIONTYPE_ID_S.nextVal, 'org.sakaiproject' ,'coursemgmt' ,'term.first_quarter' ,
    'Unknown', NULL, 'uuuid_session_6',
    1, 'site', SYSDATE , 'site' ,SYSDATE);
insert into CM_SESSIONTYPE_T ("SESSIONTYPEID", "AUTHORITY", "DOMAIN", "KEYWORD",
    "DISPLAYNAME", "DESCRIPTION", "UUID", 
    "STATUS" ,"CREATEDBY" ,"CREATEDDATE" ,"LASTMODIFIEDBY" , "LASTMODIFIEDDATE" )
    VALUES (CM_SESSIONTYPE_ID_S.nextVal, 'org.sakaiproject' ,'coursemgmt' ,'term.second_quarter' ,
    'Unknown', NULL, 'uuuid_session_7',
    1, 'site', SYSDATE , 'site' ,SYSDATE);
insert into CM_SESSIONTYPE_T ("SESSIONTYPEID", "AUTHORITY", "DOMAIN", "KEYWORD",
    "DISPLAYNAME", "DESCRIPTION", "UUID", 
    "STATUS" ,"CREATEDBY" ,"CREATEDDATE" ,"LASTMODIFIEDBY" , "LASTMODIFIEDDATE" )
    VALUES (CM_SESSIONTYPE_ID_S.nextVal, 'org.sakaiproject' ,'coursemgmt' ,'term.third_quarter' ,
    'Unknown', NULL, 'uuuid_session_8',
    1, 'site', SYSDATE , 'site' ,SYSDATE);
insert into CM_SESSIONTYPE_T ("SESSIONTYPEID", "AUTHORITY", "DOMAIN", "KEYWORD",
    "DISPLAYNAME", "DESCRIPTION", "UUID", 
    "STATUS" ,"CREATEDBY" ,"CREATEDDATE" ,"LASTMODIFIEDBY" , "LASTMODIFIEDDATE" )
    VALUES (CM_SESSIONTYPE_ID_S.nextVal, 'org.sakaiproject' ,'coursemgmt' ,'term.forth_quarter' ,
    'Unknown', NULL, 'uuuid_session_9',
    1, 'site', SYSDATE , 'site' ,SYSDATE);

commit;