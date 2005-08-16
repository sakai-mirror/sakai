alter table CM_ENROLLMENTRECORD_T drop constraint FK371AA8DF6B67A391;
alter table CM_COURSEOFFERING_T drop constraint FKD254CBEB8B4970AE;
alter table CM_COURSEOFFERING_T drop constraint FKD254CBEBCA525211;
alter table CM_COURSEOFFERING_T drop constraint FKD254CBEBA459A5D9;
alter table CM_COURSEOFFERING_T drop constraint FKD254CBEB21961636;
alter table CM_PARTICIPATIONRECORD_T drop constraint FKA9134DF2F65C8CAE;
alter table CM_COURSESECTION_T drop constraint FKE6425DCA6B67A391;
alter table CM_COURSESECTION_T drop constraint FKE6425DCACA525211;
alter table CM_COURSESECTION_T drop constraint FKE6425DCA1880BC9F;
alter table CM_COURSESECTION_T drop constraint FKE6425DCA722420D7;
drop table CM_ENROLLMENTSTATUS_T cascade constraints;
drop table CM_ENROLLMENTRECORD_T cascade constraints;
drop table CM_COURSEOFFERINGTYPE_T cascade constraints;
drop table CM_COURSEOFFERING_T cascade constraints;
drop table CM_COURSESETTYPE_T cascade constraints;
drop table CM_CANONICALCOURSESTATUS_T cascade constraints;
drop table CM_PARTICIPATIONRECORD_T cascade constraints;
drop table CM_COURSEOFFERINGSTATUS_T cascade constraints;
drop table CM_COURSESECTIONSTATUS_T cascade constraints;
drop table CM_COURSESECTIONTYPE_T cascade constraints;
drop table CM_SESSION_T cascade constraints;
drop table CM_CANONICALCOURSE_T cascade constraints;
drop table CM_ENROLLMENTTYPE_T cascade constraints;
drop table CM_PARTICIPATIONSTATUS_T cascade constraints;
drop table CM_COURSESECTION_T cascade constraints;
drop table CM_SESSIONTYPE_T cascade constraints;
drop table CM_COURSESET_T cascade constraints;
drop sequence CM_COURSESECTIONTYPE_ID_S;
drop sequence CM_CANONICALCOURSESTATUS_ID_S;
drop sequence CM_PARTICIPATIONRECORD_ID_S;
drop sequence CM_COURSESECTION_ID_S;
drop sequence CM_ENROLLMENTRECORD_ID_S;
drop sequence CM_CANONICALCOURSE_ID_S;
drop sequence CM_COURSESETTYPE_ID_S;
drop sequence CM_PARTICIPATIONSTATUS_ID_S;
drop sequence CM_SESSIONTYPE_ID_S;
drop sequence CM_COURSESET_ID_S;
drop sequence CM_COURSEOFFERINGSTATUS_ID_S;
drop sequence CM_COURSEOFFERING_ID_S;
drop sequence CM_COURSEOFFERINGTYPE_ID_S;
drop sequence CM_ENROLLMENTTYPE_ID_S;
drop sequence CM_ENROLLMENTSTATUS_ID_S;
drop sequence CM_SESSION_ID_S;
drop sequence CM_COURSESECTIONSTATUS_ID_S;
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
create table CM_ENROLLMENTRECORD_T (
   ENROLLMENTRECORDID number(19,0) not null,
   AGENT varchar2(255) not null,
   ROLE varchar2(255),
   CREDITS varchar2(255),
   COURSEOFFERINGUUID varchar2(255),
   COURSESECTIONUUID varchar2(255) not null,
   UUID varchar2(255) not null,
   CREATEDBY varchar2(255) not null,
   CREATEDDATE date not null,
   LASTMODIFIEDBY varchar2(255) not null,
   LASTMODIFIEDDATE date not null,
   ENROLLMENTSTATUSID number(19,0),
   COURSESECTIONID number(19,0),
   primary key (ENROLLMENTRECORDID)
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
create table CM_COURSEOFFERING_T (
   COURSEOFFERINGID number(19,0) not null,
   TITLE varchar2(255) not null,
   DESCRIPTION varchar2(255),
   OFFERINGNUMBER varchar2(255) not null,
   SESSIONID number(19,0),
   MAXIMUMSTUDENTS number(10,0),
   DEFAULTLOCATION varchar2(255),
   DEFAULTMEETINGTIME varchar2(255),
   DEFAULTSCHEDULE varchar2(255),
   ISCROSSLISTED number(1,0),
   EQUIVALENTS varchar2(255),
   COURSEOFFERINGSTATUSID number(19,0),
   COURSEOFFERINGTYPEID number(19,0),
   CANONICALCOURSEUUID varchar2(255) not null,
   ENROLLMENTTYPEID number(19,0),
   UUID varchar2(255) not null,
   CREATEDBY varchar2(255) not null,
   CREATEDDATE date not null,
   LASTMODIFIEDBY varchar2(255) not null,
   LASTMODIFIEDDATE date not null,
   CANONICALCOURSEID number(19,0),
   primary key (COURSEOFFERINGID)
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
create table CM_PARTICIPATIONRECORD_T (
   PARTICIPATIONRECORDID number(19,0) not null,
   AGENT varchar2(255) not null,
   ROLE varchar2(255),
   PARTICIPATIONSTATUSID number(19,0),
   COURSEOFFERINGUUID varchar2(255),
   COURSESECTIONUUID varchar2(255) not null,
   CREATEDBY varchar2(255) not null,
   CREATEDDATE date not null,
   LASTMODIFIEDBY varchar2(255) not null,
   LASTMODIFIEDDATE date not null,
   COURSEOFFERINGID number(19,0),
   COURSESECTIONID number(19,0),
   primary key (PARTICIPATIONRECORDID)
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
create table CM_CANONICALCOURSE_T (
   CANONICALCOURSEID number(19,0) not null,
   TITLE varchar2(255) not null,
   DESCRIPTION varchar(4000),
   COURSENUMBER varchar2(255) not null,
   DEFAULTCREDITS varchar2(255),
   TOPICS varchar2(255),
   PREREQUISITE varchar2(255),
   CANONICALCOURSESTATUSTYPEUUID varchar2(255) not null,
   EQUIVALENTS varchar2(255),
   PARENTID varchar2(255),
   UUID varchar2(255) not null,
   CREATEDBY varchar2(255) not null,
   CREATEDDATE date not null,
   LASTMODIFIEDBY varchar2(255) not null,
   LASTMODIFIEDDATE date not null,
   primary key (CANONICALCOURSEID)
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
create table CM_COURSESECTION_T (
   COURSESECTIONID number(19,0) not null,
   TITLE varchar2(255) not null,
   DESCRIPTION varchar2(255),
   SECTIONNUMBER varchar2(255) not null,
   MAXIMUMSTUDENTS number(10,0),
   SESSIONID number(19,0),
   SCHEDULE varchar(4000),
   SECTIONEVENTS varchar2(255),
   LOCATION varchar(4000),
   MEETINGTIME varchar(4000),
   COURSESECTIONSTATUSID number(19,0),
   COURSESECTIONTYPEID number(19,0),
   HOLDINGSECTION number(1,0),
   ENROLLMENTSTATUSID number(19,0),
   COURSEOFFERINGUUID varchar2(255) not null,
   PARENTID varchar2(255),
   UUID varchar2(255) not null,
   CREATEDBY varchar2(255) not null,
   CREATEDDATE date not null,
   LASTMODIFIEDBY varchar2(255) not null,
   LASTMODIFIEDDATE date not null,
   ALLOWSELFREGISTRATION number(1,0),
   COURSEOFFERINGID number(19,0),
   primary key (COURSESECTIONID)
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
   CONTEXT varchar2(255),
   UUID varchar2(255) not null,
   CREATEDBY varchar2(255) not null,
   CREATEDDATE date not null,
   LASTMODIFIEDBY varchar2(255) not null,
   LASTMODIFIEDDATE date not null,
   primary key (COURSESETID)
);
alter table CM_ENROLLMENTRECORD_T add constraint FK371AA8DF6B67A391 foreign key (ENROLLMENTSTATUSID) references CM_ENROLLMENTSTATUS_T;
alter table CM_COURSEOFFERING_T add constraint FKD254CBEB8B4970AE foreign key (COURSEOFFERINGSTATUSID) references CM_COURSEOFFERINGSTATUS_T;
alter table CM_COURSEOFFERING_T add constraint FKD254CBEBCA525211 foreign key (SESSIONID) references CM_SESSION_T;
alter table CM_COURSEOFFERING_T add constraint FKD254CBEBA459A5D9 foreign key (ENROLLMENTTYPEID) references CM_ENROLLMENTTYPE_T;
alter table CM_COURSEOFFERING_T add constraint FKD254CBEB21961636 foreign key (COURSEOFFERINGTYPEID) references CM_COURSEOFFERINGTYPE_T;
alter table CM_PARTICIPATIONRECORD_T add constraint FKA9134DF2F65C8CAE foreign key (PARTICIPATIONSTATUSID) references CM_PARTICIPATIONSTATUS_T;
alter table CM_COURSESECTION_T add constraint FKE6425DCA6B67A391 foreign key (ENROLLMENTSTATUSID) references CM_ENROLLMENTSTATUS_T;
alter table CM_COURSESECTION_T add constraint FKE6425DCACA525211 foreign key (SESSIONID) references CM_SESSION_T;
alter table CM_COURSESECTION_T add constraint FKE6425DCA1880BC9F foreign key (COURSESECTIONTYPEID) references CM_COURSESECTIONTYPE_T;
alter table CM_COURSESECTION_T add constraint FKE6425DCA722420D7 foreign key (COURSESECTIONSTATUSID) references CM_COURSESECTIONSTATUS_T;
create sequence CM_COURSESECTIONTYPE_ID_S;
create sequence CM_CANONICALCOURSESTATUS_ID_S;
create sequence CM_PARTICIPATIONRECORD_ID_S;
create sequence CM_COURSESECTION_ID_S;
create sequence CM_ENROLLMENTRECORD_ID_S;
create sequence CM_CANONICALCOURSE_ID_S;
create sequence CM_COURSESETTYPE_ID_S;
create sequence CM_PARTICIPATIONSTATUS_ID_S;
create sequence CM_SESSIONTYPE_ID_S;
create sequence CM_COURSESET_ID_S;
create sequence CM_COURSEOFFERINGSTATUS_ID_S;
create sequence CM_COURSEOFFERING_ID_S;
create sequence CM_COURSEOFFERINGTYPE_ID_S;
create sequence CM_ENROLLMENTTYPE_ID_S;
create sequence CM_ENROLLMENTSTATUS_ID_S;
create sequence CM_SESSION_ID_S;
create sequence CM_COURSESECTIONSTATUS_ID_S;
