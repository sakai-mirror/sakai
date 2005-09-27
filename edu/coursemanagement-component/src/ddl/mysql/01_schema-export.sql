alter table CM_ENROLLMENTRECORD_T drop foreign key FK371AA8DF6B67A391;
alter table CM_COURSEOFFERING_T drop foreign key FKD254CBEB8B4970AE;
alter table CM_COURSEOFFERING_T drop foreign key FKD254CBEBCA525211;
alter table CM_COURSEOFFERING_T drop foreign key FKD254CBEBA459A5D9;
alter table CM_COURSEOFFERING_T drop foreign key FKD254CBEB21961636;
alter table CM_PARTICIPATIONRECORD_T drop foreign key FKA9134DF2F65C8CAE;
alter table CM_CANONICALCOURSE_T drop foreign key FKDD9E516F49D9D37C;
alter table CM_COURSESECTION_T drop foreign key FKE6425DCA6B67A391;
alter table CM_COURSESECTION_T drop foreign key FKE6425DCACA525211;
alter table CM_COURSESECTION_T drop foreign key FKE6425DCA1880BC9F;
alter table CM_COURSESECTION_T drop foreign key FKE6425DCA722420D7;
alter table CM_COURSESECTION_T drop foreign key FKE6425DCAEA261936;
drop table if exists CM_ENROLLMENTSTATUS_T;
drop table if exists CM_ENROLLMENTRECORD_T;
drop table if exists CM_COURSEOFFERINGTYPE_T;
drop table if exists CM_COURSEOFFERING_T;
drop table if exists CM_COURSESETTYPE_T;
drop table if exists CM_CANONICALCOURSESTATUS_T;
drop table if exists CM_PARTICIPATIONRECORD_T;
drop table if exists CM_COURSEOFFERINGSTATUS_T;
drop table if exists CM_COURSESECTIONSTATUS_T;
drop table if exists CM_COURSESECTIONTYPE_T;
drop table if exists CM_SESSION_T;
drop table if exists CM_CANONICALCOURSE_T;
drop table if exists CM_ENROLLMENTTYPE_T;
drop table if exists CM_PARTICIPATIONSTATUS_T;
drop table if exists CM_COURSESECTION_T;
drop table if exists CM_COURSESET_T;
drop table if exists CM_SESSIONTYPE_T;
create table CM_ENROLLMENTSTATUS_T (
   ENROLLMENTSTATUSID bigint not null auto_increment,
   AUTHORITY varchar(255) not null,
   DOMAIN varchar(255) not null,
   KEYWORD varchar(255) not null,
   DISPLAYNAME varchar(255),
   DESCRIPTION varchar(255),
   UUID varchar(255) not null,
   STATUS integer not null,
   LASTMODIFIEDBY varchar(255) not null,
   LASTMODIFIEDDATE datetime not null,
   CREATEDBY varchar(255) not null,
   CREATEDDATE datetime not null,
   primary key (ENROLLMENTSTATUSID)
);
create table CM_ENROLLMENTRECORD_T (
   ENROLLMENTRECORDID bigint not null auto_increment,
   AGENT varchar(255) not null,
   ROLE varchar(255),
   CREDITS varchar(255),
   COURSEOFFERINGUUID varchar(255),
   COURSESECTIONUUID varchar(255) not null,
   UUID varchar(255) not null,
   CREATEDBY varchar(255) not null,
   CREATEDDATE datetime not null,
   LASTMODIFIEDBY varchar(255) not null,
   LASTMODIFIEDDATE datetime not null,
   ENROLLMENTSTATUSID bigint,
   COURSESECTIONID bigint,
   primary key (ENROLLMENTRECORDID)
);
create table CM_COURSEOFFERINGTYPE_T (
   COURSEOFFERINGTYPEID bigint not null auto_increment,
   AUTHORITY varchar(255) not null,
   DOMAIN varchar(255) not null,
   KEYWORD varchar(255) not null,
   DISPLAYNAME varchar(255),
   DESCRIPTION varchar(255),
   UUID varchar(255) not null,
   STATUS integer not null,
   LASTMODIFIEDBY varchar(255) not null,
   LASTMODIFIEDDATE datetime not null,
   CREATEDBY varchar(255) not null,
   CREATEDDATE datetime not null,
   primary key (COURSEOFFERINGTYPEID)
);
create table CM_COURSEOFFERING_T (
   COURSEOFFERINGID bigint not null auto_increment,
   TITLE varchar(255) not null,
   DESCRIPTION varchar(255),
   OFFERINGNUMBER varchar(255) not null,
   SESSIONID bigint,
   MAXIMUMSTUDENTS integer,
   DEFAULTLOCATION varchar(255),
   DEFAULTMEETINGTIME varchar(255),
   DEFAULTSCHEDULE varchar(255),
   ISCROSSLISTED bit,
   EQUIVALENTS varchar(255),
   COURSEOFFERINGSTATUSID bigint,
   COURSEOFFERINGTYPEID bigint,
   CANONICALCOURSEUUID varchar(255) not null,
   ENROLLMENTTYPEID bigint,
   UUID varchar(255) not null,
   CREATEDBY varchar(255) not null,
   CREATEDDATE datetime not null,
   LASTMODIFIEDBY varchar(255) not null,
   LASTMODIFIEDDATE datetime not null,
   CANONICALCOURSEID bigint,
   primary key (COURSEOFFERINGID)
);
create table CM_COURSESETTYPE_T (
   COURSESETTYPEID bigint not null auto_increment,
   AUTHORITY varchar(255) not null,
   DOMAIN varchar(255) not null,
   KEYWORD varchar(255) not null,
   DISPLAYNAME varchar(255),
   DESCRIPTION varchar(255),
   UUID varchar(255) not null,
   STATUS integer not null,
   LASTMODIFIEDBY varchar(255) not null,
   LASTMODIFIEDDATE datetime not null,
   CREATEDBY varchar(255) not null,
   CREATEDDATE datetime not null,
   primary key (COURSESETTYPEID)
);
create table CM_CANONICALCOURSESTATUS_T (
   CANONICALCOURSESTATUSID bigint not null auto_increment,
   AUTHORITY varchar(255) not null,
   DOMAIN varchar(255) not null,
   KEYWORD varchar(255) not null,
   DISPLAYNAME varchar(255),
   DESCRIPTION varchar(255),
   UUID varchar(255) not null,
   STATUS integer not null,
   LASTMODIFIEDBY varchar(255) not null,
   LASTMODIFIEDDATE datetime not null,
   CREATEDBY varchar(255) not null,
   CREATEDDATE datetime not null,
   primary key (CANONICALCOURSESTATUSID)
);
create table CM_PARTICIPATIONRECORD_T (
   PARTICIPATIONRECORDID bigint not null auto_increment,
   AGENT varchar(255) not null,
   ROLE varchar(255),
   PARTICIPATIONSTATUSID bigint,
   COURSEOFFERINGUUID varchar(255),
   COURSESECTIONUUID varchar(255) not null,
   CREATEDBY varchar(255) not null,
   CREATEDDATE datetime not null,
   LASTMODIFIEDBY varchar(255) not null,
   LASTMODIFIEDDATE datetime not null,
   COURSEOFFERINGID bigint,
   COURSESECTIONID bigint,
   primary key (PARTICIPATIONRECORDID)
);
create table CM_COURSEOFFERINGSTATUS_T (
   COURSEOFFERINGSTATUSID bigint not null auto_increment,
   AUTHORITY varchar(255) not null,
   DOMAIN varchar(255) not null,
   KEYWORD varchar(255) not null,
   DISPLAYNAME varchar(255),
   DESCRIPTION varchar(255),
   UUID varchar(255) not null,
   STATUS integer not null,
   LASTMODIFIEDBY varchar(255) not null,
   LASTMODIFIEDDATE datetime not null,
   CREATEDBY varchar(255) not null,
   CREATEDDATE datetime not null,
   primary key (COURSEOFFERINGSTATUSID)
);
create table CM_COURSESECTIONSTATUS_T (
   COURSESECTIONSTATUSID bigint not null auto_increment,
   AUTHORITY varchar(255) not null,
   DOMAIN varchar(255) not null,
   KEYWORD varchar(255) not null,
   DISPLAYNAME varchar(255),
   DESCRIPTION varchar(255),
   UUID varchar(255) not null,
   STATUS integer not null,
   LASTMODIFIEDBY varchar(255) not null,
   LASTMODIFIEDDATE datetime not null,
   CREATEDBY varchar(255) not null,
   CREATEDDATE datetime not null,
   primary key (COURSESECTIONSTATUSID)
);
create table CM_COURSESECTIONTYPE_T (
   COURSESECTIONTYPEID bigint not null auto_increment,
   AUTHORITY varchar(255) not null,
   DOMAIN varchar(255) not null,
   KEYWORD varchar(255) not null,
   DISPLAYNAME varchar(255),
   DESCRIPTION varchar(255),
   UUID varchar(255) not null,
   STATUS integer not null,
   LASTMODIFIEDBY varchar(255) not null,
   LASTMODIFIEDDATE datetime not null,
   CREATEDBY varchar(255) not null,
   CREATEDDATE datetime not null,
   primary key (COURSESECTIONTYPEID)
);
create table CM_SESSION_T (
   SESSIONID bigint not null auto_increment,
   TITLE varchar(255) not null,
   ABBREVIATION varchar(255) not null,
   YEAR varchar(255) not null,
   ISCURRENT bit not null,
   UUID varchar(255) not null,
   SESSIONTYPEUUID varchar(255),
   CREATEDBY varchar(255) not null,
   CREATEDDATE datetime not null,
   LASTMODIFIEDBY varchar(255) not null,
   LASTMODIFIEDDATE datetime not null,
   primary key (SESSIONID)
);
create table CM_CANONICALCOURSE_T (
   CANONICALCOURSEID bigint not null auto_increment,
   TITLE varchar(255) not null,
   DESCRIPTION varchar(4000),
   COURSENUMBER varchar(255) not null,
   DEFAULTCREDITS varchar(255),
   TOPICS varchar(255),
   PREREQUISITE varchar(255),
   CANONICALCOURSESTATUSID bigint,
   EQUIVALENTS varchar(255),
   PARENTID varchar(255),
   UUID varchar(255) not null,
   CREATEDBY varchar(255) not null,
   CREATEDDATE datetime not null,
   LASTMODIFIEDBY varchar(255) not null,
   LASTMODIFIEDDATE datetime not null,
   primary key (CANONICALCOURSEID)
);
create table CM_ENROLLMENTTYPE_T (
   ENROLLMENTTYPEID bigint not null auto_increment,
   AUTHORITY varchar(255) not null,
   DOMAIN varchar(255) not null,
   KEYWORD varchar(255) not null,
   DISPLAYNAME varchar(255),
   DESCRIPTION varchar(255),
   UUID varchar(255) not null,
   STATUS integer not null,
   LASTMODIFIEDBY varchar(255) not null,
   LASTMODIFIEDDATE datetime not null,
   CREATEDBY varchar(255) not null,
   CREATEDDATE datetime not null,
   primary key (ENROLLMENTTYPEID)
);
create table CM_PARTICIPATIONSTATUS_T (
   PARTICIPATIONSTATUSID bigint not null auto_increment,
   AUTHORITY varchar(255) not null,
   DOMAIN varchar(255) not null,
   KEYWORD varchar(255) not null,
   DISPLAYNAME varchar(255),
   DESCRIPTION varchar(255),
   UUID varchar(255) not null,
   STATUS integer not null,
   LASTMODIFIEDBY varchar(255) not null,
   LASTMODIFIEDDATE datetime not null,
   CREATEDBY varchar(255) not null,
   CREATEDDATE datetime not null,
   primary key (PARTICIPATIONSTATUSID)
);
create table CM_COURSESECTION_T (
   COURSESECTIONID bigint not null auto_increment,
   TITLE varchar(255) not null,
   DESCRIPTION varchar(255),
   SECTIONNUMBER varchar(255) not null,
   MAXIMUMSTUDENTS integer,
   SESSIONID bigint,
   SCHEDULE varchar(4000),
   SECTIONEVENTS varchar(255),
   LOCATION varchar(4000),
   MEETINGTIME varchar(4000),
   COURSESECTIONSTATUSID bigint,
   COURSESECTIONTYPEID bigint,
   HOLDINGSECTION bit,
   ENROLLMENTSTATUSID bigint,
   COURSEOFFERINGUUID varchar(255) not null,
   PARENTSECTIONID bigint,
   UUID varchar(255) not null,
   CREATEDBY varchar(255) not null,
   CREATEDDATE datetime not null,
   LASTMODIFIEDBY varchar(255) not null,
   LASTMODIFIEDDATE datetime not null,
   ALLOWSELFREGISTRATION bit,
   COURSEOFFERINGID bigint,
   primary key (COURSESECTIONID)
);
create table CM_COURSESET_T (
   COURSESETID bigint not null auto_increment,
   TITLE varchar(255),
   CONTEXT varchar(255),
   UUID varchar(255) not null,
   CREATEDBY varchar(255) not null,
   CREATEDDATE datetime not null,
   LASTMODIFIEDBY varchar(255) not null,
   LASTMODIFIEDDATE datetime not null,
   primary key (COURSESETID)
);
create table CM_SESSIONTYPE_T (
   SESSIONTYPEID bigint not null auto_increment,
   AUTHORITY varchar(255) not null,
   DOMAIN varchar(255) not null,
   KEYWORD varchar(255) not null,
   DISPLAYNAME varchar(255),
   DESCRIPTION varchar(255),
   UUID varchar(255) not null,
   STATUS integer not null,
   LASTMODIFIEDBY varchar(255) not null,
   LASTMODIFIEDDATE datetime not null,
   CREATEDBY varchar(255) not null,
   CREATEDDATE datetime not null,
   primary key (SESSIONTYPEID)
);
alter table CM_ENROLLMENTRECORD_T add index FK371AA8DF6B67A391 (ENROLLMENTSTATUSID), add constraint FK371AA8DF6B67A391 foreign key (ENROLLMENTSTATUSID) references CM_ENROLLMENTSTATUS_T (ENROLLMENTSTATUSID);
alter table CM_COURSEOFFERING_T add index FKD254CBEB8B4970AE (COURSEOFFERINGSTATUSID), add constraint FKD254CBEB8B4970AE foreign key (COURSEOFFERINGSTATUSID) references CM_COURSEOFFERINGSTATUS_T (COURSEOFFERINGSTATUSID);
alter table CM_COURSEOFFERING_T add index FKD254CBEBCA525211 (SESSIONID), add constraint FKD254CBEBCA525211 foreign key (SESSIONID) references CM_SESSION_T (SESSIONID);
alter table CM_COURSEOFFERING_T add index FKD254CBEBA459A5D9 (ENROLLMENTTYPEID), add constraint FKD254CBEBA459A5D9 foreign key (ENROLLMENTTYPEID) references CM_ENROLLMENTTYPE_T (ENROLLMENTTYPEID);
alter table CM_COURSEOFFERING_T add index FKD254CBEB21961636 (COURSEOFFERINGTYPEID), add constraint FKD254CBEB21961636 foreign key (COURSEOFFERINGTYPEID) references CM_COURSEOFFERINGTYPE_T (COURSEOFFERINGTYPEID);
alter table CM_PARTICIPATIONRECORD_T add index FKA9134DF2F65C8CAE (PARTICIPATIONSTATUSID), add constraint FKA9134DF2F65C8CAE foreign key (PARTICIPATIONSTATUSID) references CM_PARTICIPATIONSTATUS_T (PARTICIPATIONSTATUSID);
alter table CM_CANONICALCOURSE_T add index FKDD9E516F49D9D37C (CANONICALCOURSESTATUSID), add constraint FKDD9E516F49D9D37C foreign key (CANONICALCOURSESTATUSID) references CM_CANONICALCOURSESTATUS_T (CANONICALCOURSESTATUSID);
alter table CM_COURSESECTION_T add index FKE6425DCA6B67A391 (ENROLLMENTSTATUSID), add constraint FKE6425DCA6B67A391 foreign key (ENROLLMENTSTATUSID) references CM_ENROLLMENTSTATUS_T (ENROLLMENTSTATUSID);
alter table CM_COURSESECTION_T add index FKE6425DCACA525211 (SESSIONID), add constraint FKE6425DCACA525211 foreign key (SESSIONID) references CM_SESSION_T (SESSIONID);
alter table CM_COURSESECTION_T add index FKE6425DCA1880BC9F (COURSESECTIONTYPEID), add constraint FKE6425DCA1880BC9F foreign key (COURSESECTIONTYPEID) references CM_COURSESECTIONTYPE_T (COURSESECTIONTYPEID);
alter table CM_COURSESECTION_T add index FKE6425DCA722420D7 (COURSESECTIONSTATUSID), add constraint FKE6425DCA722420D7 foreign key (COURSESECTIONSTATUSID) references CM_COURSESECTIONSTATUS_T (COURSESECTIONSTATUSID);
alter table CM_COURSESECTION_T add index FKE6425DCAEA261936 (PARENTSECTIONID), add constraint FKE6425DCAEA261936 foreign key (PARENTSECTIONID) references CM_COURSESECTION_T (COURSESECTIONID);
