alter table CMN_AGENT_GROUP_T drop foreign key FKD858333FA8D3F022
alter table CMN_AGENT_GROUP_T drop foreign key FKD858333FF7F2E3EA
alter table CMN_NODE_T drop foreign key FK596CE0D2217F38D0
alter table CMN_NODE_T drop foreign key FK596CE0D227873A
alter table CMN_NODE_PARENT_CHILD_MAP_T drop foreign key FK614EA03B3D1FCFC
alter table CMN_NODE_PARENT_CHILD_MAP_T drop foreign key FK614EA03B8C3DFCAA
alter table CMN_GRP_PRNT_MMBR_MAP_T drop foreign key FKEBB1654487557E9A
alter table CMN_GRP_PRNT_MMBR_MAP_T drop foreign key FKEBB165448C3DFCAA
alter table CMN_AGENT_GROUP_ASSOC_T drop foreign key FKBE10ED353D1FCFC
alter table CMN_AGENT_GROUP_ASSOC_T drop foreign key FKBE10ED358C3DFCAA
drop table if exists CMN_AGENT_GROUP_T
drop table if exists CMN_TYPE_T
drop table if exists SAKAI_PERSON_T
drop table if exists CMN_NODE_T
drop table if exists CMN_DEFAULT_PERMISSIONS_T
drop table if exists CMN_NODE_PARENT_CHILD_MAP_T
drop table if exists CMN_GRP_PRNT_MMBR_MAP_T
drop table if exists CMN_AGENT_GROUP_ASSOC_T
drop table if exists CMN_AUTHORIZATION_T
create table CMN_AGENT_GROUP_T (
   ID bigint not null auto_increment,
   AGENT_TYPE varchar(2) not null,
   VERSION integer not null,
   UUID varchar(36) not null unique,
   LAST_MODIFIED_BY varchar(36) not null,
   LAST_MODIFIED_DATE datetime not null,
   CREATED_BY varchar(36) not null,
   CREATED_DATE datetime not null,
   DISPLAY_NAME varchar(255) not null,
   ENTERPRISE_ID varchar(36) not null unique,
   SESSION_MGR_USER_ID varchar(36) unique,
   TYPE_FK bigint not null,
   NODE_FK bigint not null,
   primary key (ID)
)
create table CMN_TYPE_T (
   ID bigint not null auto_increment,
   VERSION integer not null,
   UUID varchar(36) not null unique,
   LAST_MODIFIED_BY varchar(36) not null,
   LAST_MODIFIED_DATE datetime not null,
   CREATED_BY varchar(36) not null,
   CREATED_DATE datetime not null,
   AUTHORITY varchar(100) not null,
   DOMAIN varchar(100) not null,
   KEYWORD varchar(100) not null,
   DISPLAY_NAME varchar(255) not null,
   DESCRIPTION varchar(255),
   primary key (ID),
   unique (AUTHORITY, DOMAIN, KEYWORD)
)
create table SAKAI_PERSON_T (
   ID bigint not null auto_increment,
   PERSON_TYPE varchar(3) not null,
   VERSION integer not null,
   UUID varchar(36) not null unique,
   LAST_MODIFIED_BY varchar(36) not null,
   LAST_MODIFIED_DATE datetime not null,
   CREATED_BY varchar(36) not null,
   CREATED_DATE datetime not null,
   AGENT_UUID varchar(36) not null,
   TYPE_UUID varchar(36) not null,
   COMMON_NAME varchar(255),
   DESCRIPTION varchar(255),
   SEE_ALSO varchar(255),
   STREET varchar(255),
   SURNAME varchar(255),
   TELEPHONE_NUMBER varchar(255),
   FAX_NUMBER varchar(255),
   LOCALITY_NAME varchar(255),
   OU varchar(255),
   PHYSICAL_DELIVERY_OFFICE_NAME varchar(255),
   POSTAL_ADDRESS varchar(255),
   POSTAL_CODE varchar(255),
   POST_OFFICE_BOX varchar(255),
   STATE_PROVINCE_NAME varchar(255),
   STREET_ADDRESS varchar(255),
   TITLE varchar(255),
   BUSINESS_CATEGORY varchar(255),
   CAR_LICENSE varchar(255),
   DEPARTMENT_NUMBER varchar(255),
   DISPLAY_NAME varchar(255),
   EMPLOYEE_NUMBER varchar(255),
   EMPLOYEE_TYPE varchar(255),
   GIVEN_NAME varchar(255),
   HOME_PHONE varchar(255),
   HOME_POSTAL_ADDRESS varchar(255),
   INITIALS varchar(255),
   JPEG_PHOTO tinyblob,
   LABELED_URI varchar(255),
   MAIL varchar(255),
   MANAGER varchar(255),
   MOBILE varchar(255),
   ORGANIZATION varchar(255),
   PAGER varchar(255),
   PREFERRED_LANGUAGE varchar(255),
   ROOM_NUMBER varchar(255),
   SECRETARY varchar(255),
   UID_C varchar(255),
   USER_CERTIFICATE tinyblob,
   USER_PKCS12 tinyblob,
   USER_SMIME_CERTIFICATE tinyblob,
   X500_UNIQUE_ID varchar(255),
   AFFILIATION varchar(255),
   ENTITLEMENT varchar(255),
   NICKNAME varchar(255),
   ORG_DN varchar(255),
   ORG_UNIT_DN varchar(255),
   PRIMARY_AFFILIATION varchar(255),
   PRIMARY_ORG_UNIT_DN varchar(255),
   PRINCIPAL_NAME varchar(255),
   CAMPUS varchar(255),
   HIDE_PRIVATE_INFO bit,
   HIDE_PUBLIC_INFO bit,
   NOTES varchar(255),
   PICTURE_URL varchar(255),
   SYSTEM_PICTURE_PREFERRED bit,
   primary key (ID),
   unique (AGENT_UUID, TYPE_UUID)
)
create table CMN_NODE_T (
   ID bigint not null auto_increment,
   VERSION integer not null,
   UUID varchar(36) not null unique,
   LAST_MODIFIED_BY varchar(36) not null,
   LAST_MODIFIED_DATE datetime not null,
   CREATED_BY varchar(36) not null,
   CREATED_DATE datetime not null,
   TYPE bigint not null,
   REFERENCE_UUID varchar(36) not null unique,
   DISPLAY_NAME varchar(255) not null,
   DESCRIPTION varchar(255),
   DEPTH integer not null,
   PARENT_ID bigint,
   primary key (ID)
)
create table CMN_DEFAULT_PERMISSIONS_T (
   ID bigint not null auto_increment,
   VERSION integer not null,
   UUID varchar(36) not null unique,
   LAST_MODIFIED_BY varchar(36) not null,
   LAST_MODIFIED_DATE datetime not null,
   CREATED_BY varchar(36) not null,
   CREATED_DATE datetime not null,
   NAME varchar(255) not null unique,
   DESCRIPTION varchar(255),
   AUDIT_C bit,
   CREATE_C bit,
   CREATE_COLLECTION bit,
   DELETE_C bit,
   DELETE_COLLECTION bit,
   EXECUTE_C bit,
   MANAGE_PERMISSSIONS bit,
   READ_C bit,
   READ_EXTENDED_METADATA bit,
   READ_METADATA bit,
   READ_PERMISSIONS bit,
   TAKE_OWNERSHIP bit,
   WRITE_C bit,
   WRITE_EXTENDED_METADATA bit,
   WRITE_METADATA bit,
   primary key (ID),
   unique (AUDIT_C, CREATE_C, CREATE_COLLECTION, DELETE_C, DELETE_COLLECTION, EXECUTE_C, MANAGE_PERMISSSIONS, READ_C, READ_EXTENDED_METADATA, READ_METADATA, READ_PERMISSIONS, TAKE_OWNERSHIP, WRITE_C, WRITE_EXTENDED_METADATA, WRITE_METADATA)
)
create table CMN_NODE_PARENT_CHILD_MAP_T (
   ID bigint not null auto_increment,
   VERSION integer not null,
   PARENT bigint not null,
   CHILD bigint not null,
   primary key (ID),
   unique (PARENT, CHILD)
)
create table CMN_GRP_PRNT_MMBR_MAP_T (
   ID bigint not null auto_increment,
   VERSION integer not null,
   PARENT bigint not null,
   MEMBER bigint not null,
   primary key (ID),
   unique (PARENT, MEMBER)
)
create table CMN_AGENT_GROUP_ASSOC_T (
   PARENT bigint not null,
   CHILD bigint not null,
   primary key (PARENT, CHILD)
)
create table CMN_AUTHORIZATION_T (
   ID bigint not null auto_increment,
   VERSION integer not null,
   UUID varchar(36) not null unique,
   LAST_MODIFIED_BY varchar(36) not null,
   LAST_MODIFIED_DATE datetime not null,
   CREATED_BY varchar(36) not null,
   CREATED_DATE datetime not null,
   AGENT_UUID varchar(36) not null,
   PERMISSIONS_UUID varchar(36) not null,
   NODE_UUID varchar(36) not null,
   EFFECTIVE_DATE datetime not null,
   EXPIRATION_DATE datetime,
   primary key (ID),
   unique (AGENT_UUID, PERMISSIONS_UUID, NODE_UUID)
)
create index CMN_AGENT_T_SESSN_MGR_USR_ID_I on CMN_AGENT_GROUP_T (SESSION_MGR_USER_ID)
create index CMN_AGENT_T_DISPLAY_NAME_I on CMN_AGENT_GROUP_T (DISPLAY_NAME)
create index CMN_AGENT_T_ENTERPRISE_ID_I on CMN_AGENT_GROUP_T (ENTERPRISE_ID)
create index CMN_AGENT_T_NODE_I on CMN_AGENT_GROUP_T (NODE_FK)
create index CMN_AGENT_T_TYPE_I on CMN_AGENT_GROUP_T (TYPE_FK)
alter table CMN_AGENT_GROUP_T add index FKD858333FA8D3F022 (NODE_FK), add constraint FKD858333FA8D3F022 foreign key (NODE_FK) references CMN_NODE_T (ID)
alter table CMN_AGENT_GROUP_T add index FKD858333FF7F2E3EA (TYPE_FK), add constraint FKD858333FF7F2E3EA foreign key (TYPE_FK) references CMN_TYPE_T (ID)
create index CMN_TYPE_T_DISPLAY_NAME_I on CMN_TYPE_T (DISPLAY_NAME)
create index CMN_TYPE_T_KEYWORD_I on CMN_TYPE_T (KEYWORD)
create index CMN_TYPE_T_DOMAIN_I on CMN_TYPE_T (DOMAIN)
create index CMN_TYPE_T_AUTHORITY_I on CMN_TYPE_T (AUTHORITY)
create index SAKAI_PERSON_TYPE_UUID_I on SAKAI_PERSON_T (TYPE_UUID)
create index SAKAI_PERSON_SURNAME_I on SAKAI_PERSON_T (SURNAME)
create index SAKAI_PERSON_AGENT_UUID_I on SAKAI_PERSON_T (AGENT_UUID)
create index SAKAI_PERSON_GIVEN_NAME_I on SAKAI_PERSON_T (GIVEN_NAME)
create index SAKAI_PERSON_UID_I on SAKAI_PERSON_T (UID_C)
create index CMN_NODE_DISPLAY_NAME_I on CMN_NODE_T (DISPLAY_NAME)
create index CMN_NODE_DEPTH_I on CMN_NODE_T (DEPTH)
create index CMN_NODE_TYPE_I on CMN_NODE_T (TYPE)
create index CMN_NODE_REFERENCE_UUID_I on CMN_NODE_T (REFERENCE_UUID)
create index CMN_NODE_PARENT_I on CMN_NODE_T (PARENT_ID)
alter table CMN_NODE_T add index FK596CE0D2217F38D0 (PARENT_ID), add constraint FK596CE0D2217F38D0 foreign key (PARENT_ID) references CMN_NODE_T (ID)
alter table CMN_NODE_T add index FK596CE0D227873A (TYPE), add constraint FK596CE0D227873A foreign key (TYPE) references CMN_TYPE_T (ID)
create index CMN_PERMISSIONS_MNG_PERMS_I on CMN_DEFAULT_PERMISSIONS_T (MANAGE_PERMISSSIONS)
create index CMN_PERMISSIONS_READ_MD_I on CMN_DEFAULT_PERMISSIONS_T (READ_METADATA)
create index CMN_PERMISSIONS_READ_PERMS_I on CMN_DEFAULT_PERMISSIONS_T (READ_PERMISSIONS)
create index CMN_PERMISSIONS_READ_XMD_I on CMN_DEFAULT_PERMISSIONS_T (READ_EXTENDED_METADATA)
create index CMN_PERMISSIONS_NAME_I on CMN_DEFAULT_PERMISSIONS_T (NAME)
create index CMN_PERMISSIONS_WRITE_I on CMN_DEFAULT_PERMISSIONS_T (WRITE_C)
create index CMN_PERMISSIONS_CREATE_COLL_I on CMN_DEFAULT_PERMISSIONS_T (CREATE_COLLECTION)
create index CMN_PERMISSIONS_WRITE_XMD_I on CMN_DEFAULT_PERMISSIONS_T (WRITE_EXTENDED_METADATA)
create index CMN_PERMISSIONS_DELETE_I on CMN_DEFAULT_PERMISSIONS_T (DELETE_C)
create index CMN_PERMISSIONS_AUDIT_I on CMN_DEFAULT_PERMISSIONS_T (AUDIT_C)
create index CMN_PERMISSIONS_WRITE_MD_I on CMN_DEFAULT_PERMISSIONS_T (WRITE_METADATA)
create index CMN_PERMISSIONS_TAKE_OWN_I on CMN_DEFAULT_PERMISSIONS_T (TAKE_OWNERSHIP)
create index CMN_PERMISSIONS_DELETE_COLL_I on CMN_DEFAULT_PERMISSIONS_T (DELETE_COLLECTION)
create index CMN_PERMISSIONS_READ_I on CMN_DEFAULT_PERMISSIONS_T (READ_C)
create index CMN_PERMISSIONS_CREATE_I on CMN_DEFAULT_PERMISSIONS_T (CREATE_C)
create index CMN_PERMISSIONS_EXECUTE_I on CMN_DEFAULT_PERMISSIONS_T (EXECUTE_C)
create index CMN_NODE_PCMAP_PARENT_I on CMN_NODE_PARENT_CHILD_MAP_T (PARENT)
create index CMN_NODE_PCMAP_CHILD_I on CMN_NODE_PARENT_CHILD_MAP_T (CHILD)
alter table CMN_NODE_PARENT_CHILD_MAP_T add index FK614EA03B3D1FCFC (CHILD), add constraint FK614EA03B3D1FCFC foreign key (CHILD) references CMN_NODE_T (ID)
alter table CMN_NODE_PARENT_CHILD_MAP_T add index FK614EA03B8C3DFCAA (PARENT), add constraint FK614EA03B8C3DFCAA foreign key (PARENT) references CMN_NODE_T (ID)
create index CMN_GRP_PRNT_MMBR_MAP_PRNT_I on CMN_GRP_PRNT_MMBR_MAP_T (PARENT)
create index CMN_GRP_PRNT_MMBR_MAP_MMBR_I on CMN_GRP_PRNT_MMBR_MAP_T (MEMBER)
alter table CMN_GRP_PRNT_MMBR_MAP_T add index FKEBB1654487557E9A (MEMBER), add constraint FKEBB1654487557E9A foreign key (MEMBER) references CMN_AGENT_GROUP_T (ID)
alter table CMN_GRP_PRNT_MMBR_MAP_T add index FKEBB165448C3DFCAA (PARENT), add constraint FKEBB165448C3DFCAA foreign key (PARENT) references CMN_AGENT_GROUP_T (ID)
alter table CMN_AGENT_GROUP_ASSOC_T add index FKBE10ED353D1FCFC (CHILD), add constraint FKBE10ED353D1FCFC foreign key (CHILD) references CMN_AGENT_GROUP_T (ID)
alter table CMN_AGENT_GROUP_ASSOC_T add index FKBE10ED358C3DFCAA (PARENT), add constraint FKBE10ED358C3DFCAA foreign key (PARENT) references CMN_AGENT_GROUP_T (ID)
create index CMN_AUTHZ_NODE_UUID_I on CMN_AUTHORIZATION_T (NODE_UUID)
create index CMN_AUTHZ_AGENT_UUID_I on CMN_AUTHORIZATION_T (AGENT_UUID)
create index CMN_AUTHZ_PERMISSIONS_UUID_I on CMN_AUTHORIZATION_T (PERMISSIONS_UUID)
