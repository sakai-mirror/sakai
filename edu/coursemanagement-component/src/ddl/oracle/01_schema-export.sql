drop table CM_ENROLLMENTSTATUS_T cascade constraints;
drop table CM_COURSEOFFERINGTYPE_T cascade constraints;
drop table CM_COURSESETTYPE_T cascade constraints;
drop table CM_CANONICALCOURSESTATUS_T cascade constraints;
drop table CM_COURSEOFFERINGSTATUS_T cascade constraints;
drop table CM_COURSESECTIONSTATUS_T cascade constraints;
drop table CM_COURSESECTIONTYPE_T cascade constraints;
drop table CM_CANONICALCOURSE_T cascade constraints;
drop table CM_SESSION_T cascade constraints;
drop table CM_EQUIVALENTCOURSES_T cascade constraints;
drop table CM_ENROLLMENTTYPE_T cascade constraints;
drop table CM_PARTICIPATIONSTATUS_T cascade constraints;
drop table CM_SESSIONTYPE_T cascade constraints;
drop table CM_COURSESET_T cascade constraints;
drop sequence CM_COURSESECTIONTYPE_ID_S;
drop sequence hibernate_sequence;
drop sequence CM_COURSEOFFERINGSTATUS_ID_S;
drop sequence CM_CANONICALCOURSESTATUS_ID_S;
drop sequence CM_COURSEOFFERINGTYPE_ID_S;
drop sequence CM_ENROLLMENTSTATUS_ID_S;
drop sequence CM_ENROLLMENTTYPE_ID_S;
drop sequence CM_SESSION_ID_S;
drop sequence CM_COURSESETTYPE_ID_S;
drop sequence CM_COURSESECTIONSTATUS_ID_S;
drop sequence CM_PARTICIPATIONSTATUS_ID_S;
drop sequence CM_SESSIONTYPE_ID_S;
create table CM_ENROLLMENTSTATUS_T (
   ENROLLMENTSTATUSID number(19,0) not null,
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
   primary key (ENROLLMENTSTATUSID)
);
create table CM_COURSEOFFERINGTYPE_T (
   COURSEOFFERINGTYPEID number(19,0) not null,
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
   primary key (COURSEOFFERINGTYPEID)
);
create table CM_COURSESETTYPE_T (
   COURSESETTYPEID number(19,0) not null,
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
   primary key (COURSESETTYPEID)
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
create table CM_COURSEOFFERINGSTATUS_T (
   COURSEOFFERINGSTATUSID number(19,0) not null,
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
   primary key (COURSEOFFERINGSTATUSID)
);
create table CM_COURSESECTIONSTATUS_T (
   COURSESECTIONSTATUSID number(19,0) not null,
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
   primary key (COURSESECTIONSTATUSID)
);
create table CM_COURSESECTIONTYPE_T (
   COURSESECTIONTYPEID number(19,0) not null,
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
   primary key (COURSESECTIONTYPEID)
);
create table CM_CANONICALCOURSE_T (
   CANONICALCOURSEID number(19,0) not null,
   TITLE varchar2(255),
   DESCRIPTION varchar2(255),
   COURSENUMBER varchar2(255) not null,
   UUID varchar2(255) not null,
   DEFAULTCREDITS varchar2(255),
   PARENTID varchar2(255),
   TOPICS varchar2(255),
   PREREQUISITE varchar2(255),
   CANONICALCOURSESTATUSTYPEUUID varchar2(255) not null,
   CREATEDBY varchar2(255) not null,
   CREATEDDATE date not null,
   LASTMODIFIEDBY varchar2(255) not null,
   LASTMODIFIEDDATE date not null,
   CANONICALCOURSEUUID number(19,0),
   primary key (CANONICALCOURSEID)
);
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
create table CM_EQUIVALENTCOURSES_T (
   EQUIVALENTID number(19,0) not null,
   TITLE varchar2(255),
   UUID varchar2(255) not null,
   primary key (EQUIVALENTID)
);
create table CM_ENROLLMENTTYPE_T (
   ENROLLMENTTYPEID number(19,0) not null,
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
   primary key (ENROLLMENTTYPEID)
);
create table CM_PARTICIPATIONSTATUS_T (
   PARTICIPATIONSTATUSID number(19,0) not null,
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
   primary key (PARTICIPATIONSTATUSID)
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
create table CM_COURSESET_T (
   COURSESETID number(19,0) not null,
   TITLE varchar2(255),
   UUID varchar2(255) not null,
   CREATEDBY varchar2(255) not null,
   CREATEDDATE date not null,
   LASTMODIFIEDBY varchar2(255) not null,
   LASTMODIFIEDDATE date not null,
   primary key (COURSESETID)
);
create sequence CM_COURSESECTIONTYPE_ID_S;
create sequence hibernate_sequence;
create sequence CM_COURSEOFFERINGSTATUS_ID_S;
create sequence CM_CANONICALCOURSESTATUS_ID_S;
create sequence CM_COURSEOFFERINGTYPE_ID_S;
create sequence CM_ENROLLMENTSTATUS_ID_S;
create sequence CM_ENROLLMENTTYPE_ID_S;
create sequence CM_SESSION_ID_S;
create sequence CM_COURSESETTYPE_ID_S;
create sequence CM_COURSESECTIONSTATUS_ID_S;
create sequence CM_PARTICIPATIONSTATUS_ID_S;
create sequence CM_SESSIONTYPE_ID_S;
