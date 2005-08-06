drop table CM_SESSION_T cascade constraints;
drop table CM_CANONICALCOURSESTATUS_T cascade constraints;
drop table CM_SESSIONTYPE_T cascade constraints;
drop sequence CM_CANONICALCOURSESTATUS_ID_S;
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
create table CM_CANONICALCOURSESTATUS_T (
   CANONICALCOURSESTATUSID number(19,0) not null,
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
   primary key (CANONICALCOURSESTATUSID)
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
create sequence CM_CANONICALCOURSESTATUS_ID_S;
create sequence CM_SESSION_ID_S;
create sequence CM_SESSIONTYPE_ID_S;
