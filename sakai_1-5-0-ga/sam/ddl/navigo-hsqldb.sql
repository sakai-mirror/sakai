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
   agent_id varchar(36),
   function_id varchar(36),
   qualifier_id varchar(36),
   effective_date timestamp(),
   expiration_date timestamp(),
   modifier_id varchar(36),
   modified_date timestamp(),
   is_explicit char(1),
   ol_timestamp timestamp(),
   constraint pk_unique primary key (agent_id,function_id,qualifier_id)
);

create table functions(
   function_id varchar(36) primary key,
   display_name varchar(50),
   description varchar(100),
   function_type varchar(36) not null,
   qualifier_hierarchy_id varchar(36),
   ol_timestamp timestamp()
);

create table qualifiers(
   qualifier_id varchar(36) primary key,
   display_name varchar(50),
   description varchar(100),
   qualifier_type varchar(200) not null,
   qualifier_hierarchy_id varchar(36),
   ol_timestamp timestamp()
);

create table types(
   type_id varchar(36) primary key,
   authority varchar(100) not null,
   domain varchar(100) not null,
   keyword varchar(100) not null,
   description varchar(100),
   ol_timestamp timestamp()
);


create table agents(
   agent_id varchar(36) primary key,
   type_id varchar(36) not null,
   ps_empl_id varchar(11),
   kerberos_id varchar(36),
   ip_addr varchar(15),
   ol_timestamp timestamp()
);

CREATE TABLE groups (
   group_id varchar(255) primary key,
   type_id varchar(255) ,
   oc_group_id varchar(255),
   ol_timestamp timestamp()
);

create table dr_asset_map(
   dr_id varchar(36),
   asset_id varchar(36),
   agent_id varchar(36),   
   ol_timestamp timestamp(),
   constraint pk_assets primary key (dr_id,asset_id)
);

create table assets(
   id varchar(36),
   version numeric(19),
   deleted char(1),
   created timestamp(),
   data LONGVARBINARY,
   description varchar(1024),
   title varchar(128),
   dr_id varchar(36),
   effective_date timestamp(),
   expiration_date timestamp(),
   type_id varchar(36) not null,
   ol_timestamp timestamp(),
   constraint pk_unique_assets primary key (id, version)
);


CREATE TABLE QTI_SETTINGS (
  ID 	varchar(200) PRIMARY KEY,
  DISPLAY_NAME VARCHAR2(128),
  PUBLISHED_URL VARCHAR2(255),
  MAX_ATTEMPTS 	numeric(22),
  AUTO_SUBMIT 	varchar(5),
  AUTO_SAVE     CHAR(1),
  TEST_DISABLED varchar(5), 
  FEEDBACK_TYPE varchar(9),
  IP_RESTRICTIONS varchar(4000),
  PASSWORD_RESTRICTION varchar(15),
  USERNAME_RESTICTION varchar(30),
  START_DATE	TIMESTAMP(),
  END_DATE 	TIMESTAMP(),
  CREATED_DATE 	TIMESTAMP(),
  FEEDBACK_DATE TIMESTAMP(),
  RETRACT_DATE TIMESTAMP(),
  LATE_HANDLING CHAR(1)
);

CREATE TABLE ITEM_RESULT (
  ASSESSMENT_ID varchar(36),
  ITEM_ID varchar(36),
  ELEMENT_ID varchar(36),
  constraint pk_item_result primary key (ASSESSMENT_ID,ITEM_ID)
);

CREATE TABLE QTI_ASSESSMENT_TAKEN (
  ASSESSMENT_TAKEN_ID varchar(50) PRIMARY KEY,
  ASSESSMENT_PUB_ID varchar(50),
  AGENT_ID varchar(50),
  SUBMITTED numeric(1),
  LATE_SUBMISSION numeric(1),
  SUBMISSION_TIME TIMESTAMP(),
  BEGIN_TIME TIMESTAMP(),
  END_TIME TIMESTAMP(),
  ASSESSMENT_TITLE VARCHAR2(128)
);

create table assessment_published (
   published_id varchar(36) primary key,
   core_id varchar(36)
);

CREATE TABLE SECTION_RESULT (
  ASSESSMENT_ID varchar(36),
  SECTION_ID varchar(36),
  ELEMENT_ID varchar(36),
  CONSTRAINT SECTION_RESULT_PK PRIMARY KEY (ASSESSMENT_ID, SECTION_ID)
);

CREATE TABLE ASSESSMENT_RESULT(
  ASSESSMENT_ID varchar(36) PRIMARY KEY,
  ELEMENT_ID varchar(36)
);
