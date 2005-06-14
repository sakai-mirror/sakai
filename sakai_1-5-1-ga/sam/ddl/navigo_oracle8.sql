drop table authorizations;
drop table functions;
drop table qualifiers;
drop table types;
drop table agents;
drop table groups;
drop table dr_asset_map;
drop table assets;

drop table QTI_SETTINGS;
drop table ITEM_RESULT;
drop table QTI_ASSESSMENT_TAKEN;
drop table assessment_published;
drop table section_result;
drop table assessment_result;

create table authorizations(
   agent_id varchar2(36),
   function_id varchar2(36),
   qualifier_id varchar2(36),
   effective_date date,
   expiration_date date,
   modifier_id varchar2(36),
   modified_date date,
   is_explicit char(1),
   ol_timestamp date,
   constraint pk_unique primary key (agent_id,function_id,qualifier_id)
);

create table functions(
   function_id varchar2(36) primary key,
   display_name varchar2(50),
   description varchar2(100),
   function_type varchar2(36) not null,
   qualifier_hierarchy_id varchar2(36),
   ol_timestamp date 
);

create table qualifiers(
   qualifier_id varchar2(36) primary key,
   display_name varchar2(50),
   description varchar2(100),
   qualifier_type varchar2(200) not null,
   qualifier_hierarchy_id varchar2(36),
   ol_timestamp date 
);

create table types(
   type_id varchar2(36) primary key,
   authority varchar2(100) not null,
   domain varchar2(100) not null,
   keyword varchar2(100) not null,
   description varchar2(100),
   ol_timestamp date 
);


create table agents(
   agent_id varchar2(36) primary key,
   type_id varchar2(36) not null,
   ps_empl_id varchar2(11),
   kerberos_id varchar2(36),
   ip_addr varchar2(15),
   ol_timestamp date 
);

CREATE TABLE groups (
   group_id VARCHAR2(255) primary key,
   type_id VARCHAR2(255),
   oc_group_id VARCHAR2(255),
   ol_timestamp date 
);

create table dr_asset_map(
   dr_id varchar2(36),
   asset_id varchar2(36),
   agent_id varchar2(36),   
   constraint pk_assets primary key (dr_id,asset_id),
   ol_timestamp date 
);


create table assets(
   id varchar2(36),
   version Number(19),
   deleted char(1),
   created date,
   data blob default empty_blob(),
   description varchar2(1024),
   title varchar2(128),
   dr_id varchar2(36),
   effective_date date,
   expiration_date date,
   type_id varchar2(36) not null,
   ol_timestamp date,
   constraint pk_unique_assets primary key (id, version)
);

CREATE TABLE QTI_SETTINGS (
  ID    VARCHAR2(200) PRIMARY KEY,
  DISPLAY_NAME VARCHAR2(128),
  PUBLISHED_URL VARCHAR2(255),
  MAX_ATTEMPTS  NUMBER(22),
  AUTO_SUBMIT   VARCHAR2(5),
  AUTO_SAVE     CHAR(1),
  TEST_DISABLED VARCHAR2(5),
  FEEDBACK_TYPE VARCHAR2(9),
  IP_RESTRICTIONS VARCHAR2(4000),
  PASSWORD_RESTRICTION VARCHAR2(15),
  USERNAME_RESTICTION VARCHAR2(30),
  START_DATE    DATE,
  END_DATE      DATE,
  CREATED_DATE  DATE,
  FEEDBACK_DATE DATE,
  RETRACT_DATE DATE,
  LATE_HANDLING CHAR(1)
);

CREATE TABLE ITEM_RESULT (
  ASSESSMENT_ID VARCHAR2(36),
  ITEM_ID VARCHAR2(36),
  ELEMENT_ID VARCHAR2(36),
  constraint pk_item_result primary key (ASSESSMENT_ID,ITEM_ID)
);

CREATE TABLE QTI_ASSESSMENT_TAKEN (
  ASSESSMENT_TAKEN_ID VARCHAR2(50) PRIMARY KEY,
  ASSESSMENT_PUB_ID VARCHAR2(50),
  AGENT_ID VARCHAR2(50),
  SUBMITTED NUMBER(1),
  LATE_SUBMISSION NUMBER(1),
  SUBMISSION_TIME DATE,
  BEGIN_TIME DATE,
  END_TIME DATE,
  ASSESSMENT_TITLE VARCHAR2(128)
);

create table assessment_published (
   published_id varchar2(36) primary key,
   core_id varchar2(36)
);

CREATE TABLE SECTION_RESULT (
  ASSESSMENT_ID VARCHAR2(36),
  SECTION_ID VARCHAR2(36),
  ELEMENT_ID VARCHAR2(36),
  CONSTRAINT SECTION_RESULT_PK PRIMARY KEY (ASSESSMENT_ID, SECTION_ID)
);

CREATE TABLE ASSESSMENT_RESULT(
  ASSESSMENT_ID VARCHAR2(36) PRIMARY KEY,
  ELEMENT_ID VARCHAR2(36)
);
