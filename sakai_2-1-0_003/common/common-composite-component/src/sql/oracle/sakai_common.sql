alter table CMN_AGENT_GROUP_T drop constraint FKD858333FA8D3F022
alter table CMN_AGENT_GROUP_T drop constraint FKD858333FF7F2E3EA
alter table CMN_NODE_T drop constraint FK596CE0D2217F38D0
alter table CMN_NODE_T drop constraint FK596CE0D227873A
alter table CMN_NODE_PARENT_CHILD_MAP_T drop constraint FK614EA03B3D1FCFC
alter table CMN_NODE_PARENT_CHILD_MAP_T drop constraint FK614EA03B8C3DFCAA
alter table CMN_GRP_PRNT_MMBR_MAP_T drop constraint FKEBB1654487557E9A
alter table CMN_GRP_PRNT_MMBR_MAP_T drop constraint FKEBB165448C3DFCAA
alter table CMN_AGENT_GROUP_ASSOC_T drop constraint FKBE10ED353D1FCFC
alter table CMN_AGENT_GROUP_ASSOC_T drop constraint FKBE10ED358C3DFCAA
drop table CMN_AGENT_GROUP_T cascade constraints
drop table CMN_TYPE_T cascade constraints
drop table SAKAI_PERSON_T cascade constraints
drop table CMN_NODE_T cascade constraints
drop table CMN_DEFAULT_PERMISSIONS_T cascade constraints
drop table CMN_NODE_PARENT_CHILD_MAP_T cascade constraints
drop table CMN_GRP_PRNT_MMBR_MAP_T cascade constraints
drop table CMN_AGENT_GROUP_ASSOC_T cascade constraints
drop table CMN_AUTHORIZATION_T cascade constraints
drop sequence CMN_NODE_PARENT_CHILD_MAP_S
drop sequence CMN_NODE_S
drop sequence CMN_DEFAULT_PERMISSIONS_S
drop sequence CMN_GRP_PRNT_MMBR_MAP_S
drop sequence CMN_TYPE_S
drop sequence CMN_AGENT_GROUP_S
drop sequence CMN_AUTHORIZATION_S
drop sequence SAKAI_PERSON_S
create table CMN_AGENT_GROUP_T (
   ID number(19,0) not null,
   AGENT_TYPE varchar2(2) not null,
   VERSION number(10,0) not null,
   UUID varchar2(36) not null unique,
   LAST_MODIFIED_BY varchar2(36) not null,
   LAST_MODIFIED_DATE date not null,
   CREATED_BY varchar2(36) not null,
   CREATED_DATE date not null,
   DISPLAY_NAME varchar2(255) not null,
   ENTERPRISE_ID varchar2(36) not null unique,
   SESSION_MGR_USER_ID varchar2(36) unique,
   TYPE_FK number(19,0) not null,
   NODE_FK number(19,0) not null,
   primary key (ID)
)
create table CMN_TYPE_T (
   ID number(19,0) not null,
   VERSION number(10,0) not null,
   UUID varchar2(36) not null unique,
   LAST_MODIFIED_BY varchar2(36) not null,
   LAST_MODIFIED_DATE date not null,
   CREATED_BY varchar2(36) not null,
   CREATED_DATE date not null,
   AUTHORITY varchar2(100) not null,
   DOMAIN varchar2(100) not null,
   KEYWORD varchar2(100) not null,
   DISPLAY_NAME varchar2(255) not null,
   DESCRIPTION varchar2(255),
   primary key (ID),
   unique (AUTHORITY, DOMAIN, KEYWORD)
)
create table SAKAI_PERSON_T (
   ID number(19,0) not null,
   PERSON_TYPE varchar2(3) not null,
   VERSION number(10,0) not null,
   UUID varchar2(36) not null unique,
   LAST_MODIFIED_BY varchar2(36) not null,
   LAST_MODIFIED_DATE date not null,
   CREATED_BY varchar2(36) not null,
   CREATED_DATE date not null,
   AGENT_UUID varchar2(36) not null,
   TYPE_UUID varchar2(36) not null,
   COMMON_NAME varchar2(255),
   DESCRIPTION varchar2(255),
   SEE_ALSO varchar2(255),
   STREET varchar2(255),
   SURNAME varchar2(255),
   TELEPHONE_NUMBER varchar2(255),
   FAX_NUMBER varchar2(255),
   LOCALITY_NAME varchar2(255),
   OU varchar2(255),
   PHYSICAL_DELIVERY_OFFICE_NAME varchar2(255),
   POSTAL_ADDRESS varchar2(255),
   POSTAL_CODE varchar2(255),
   POST_OFFICE_BOX varchar2(255),
   STATE_PROVINCE_NAME varchar2(255),
   STREET_ADDRESS varchar2(255),
   TITLE varchar2(255),
   BUSINESS_CATEGORY varchar2(255),
   CAR_LICENSE varchar2(255),
   DEPARTMENT_NUMBER varchar2(255),
   DISPLAY_NAME varchar2(255),
   EMPLOYEE_NUMBER varchar2(255),
   EMPLOYEE_TYPE varchar2(255),
   GIVEN_NAME varchar2(255),
   HOME_PHONE varchar2(255),
   HOME_POSTAL_ADDRESS varchar2(255),
   INITIALS varchar2(255),
   JPEG_PHOTO blob,
   LABELED_URI varchar2(255),
   MAIL varchar2(255),
   MANAGER varchar2(255),
   MOBILE varchar2(255),
   ORGANIZATION varchar2(255),
   PAGER varchar2(255),
   PREFERRED_LANGUAGE varchar2(255),
   ROOM_NUMBER varchar2(255),
   SECRETARY varchar2(255),
   UID_C varchar2(255),
   USER_CERTIFICATE raw(255),
   USER_PKCS12 raw(255),
   USER_SMIME_CERTIFICATE raw(255),
   X500_UNIQUE_ID varchar2(255),
   AFFILIATION varchar2(255),
   ENTITLEMENT varchar2(255),
   NICKNAME varchar2(255),
   ORG_DN varchar2(255),
   ORG_UNIT_DN varchar2(255),
   PRIMARY_AFFILIATION varchar2(255),
   PRIMARY_ORG_UNIT_DN varchar2(255),
   PRINCIPAL_NAME varchar2(255),
   CAMPUS varchar2(255),
   HIDE_PRIVATE_INFO number(1,0),
   HIDE_PUBLIC_INFO number(1,0),
   NOTES varchar2(255),
   PICTURE_URL varchar2(255),
   SYSTEM_PICTURE_PREFERRED number(1,0),
   ferpaEnabled number(1,0),
   primary key (ID),
   unique (AGENT_UUID, TYPE_UUID)
)
create table CMN_NODE_T (
   ID number(19,0) not null,
   VERSION number(10,0) not null,
   UUID varchar2(36) not null unique,
   LAST_MODIFIED_BY varchar2(36) not null,
   LAST_MODIFIED_DATE date not null,
   CREATED_BY varchar2(36) not null,
   CREATED_DATE date not null,
   TYPE number(19,0) not null,
   REFERENCE_UUID varchar2(36) not null unique,
   DISPLAY_NAME varchar2(255) not null,
   DESCRIPTION varchar2(255),
   DEPTH number(10,0) not null,
   PARENT_ID number(19,0),
   primary key (ID)
)
create table CMN_DEFAULT_PERMISSIONS_T (
   ID number(19,0) not null,
   VERSION number(10,0) not null,
   UUID varchar2(36) not null unique,
   LAST_MODIFIED_BY varchar2(36) not null,
   LAST_MODIFIED_DATE date not null,
   CREATED_BY varchar2(36) not null,
   CREATED_DATE date not null,
   NAME varchar2(255) not null unique,
   DESCRIPTION varchar2(255),
   AUDIT_C number(1,0),
   CREATE_C number(1,0),
   CREATE_COLLECTION number(1,0),
   DELETE_C number(1,0),
   DELETE_COLLECTION number(1,0),
   EXECUTE_C number(1,0),
   MANAGE_PERMISSSIONS number(1,0),
   READ_C number(1,0),
   READ_EXTENDED_METADATA number(1,0),
   READ_METADATA number(1,0),
   READ_PERMISSIONS number(1,0),
   TAKE_OWNERSHIP number(1,0),
   WRITE_C number(1,0),
   WRITE_EXTENDED_METADATA number(1,0),
   WRITE_METADATA number(1,0),
   primary key (ID),
   unique (AUDIT_C, CREATE_C, CREATE_COLLECTION, DELETE_C, DELETE_COLLECTION, EXECUTE_C, MANAGE_PERMISSSIONS, READ_C, READ_EXTENDED_METADATA, READ_METADATA, READ_PERMISSIONS, TAKE_OWNERSHIP, WRITE_C, WRITE_EXTENDED_METADATA, WRITE_METADATA)
)
create table CMN_NODE_PARENT_CHILD_MAP_T (
   ID number(19,0) not null,
   VERSION number(10,0) not null,
   PARENT number(19,0) not null,
   CHILD number(19,0) not null,
   primary key (ID),
   unique (PARENT, CHILD)
)
create table CMN_GRP_PRNT_MMBR_MAP_T (
   ID number(19,0) not null,
   VERSION number(10,0) not null,
   PARENT number(19,0) not null,
   MEMBER number(19,0) not null,
   primary key (ID),
   unique (PARENT, MEMBER)
)
create table CMN_AGENT_GROUP_ASSOC_T (
   PARENT number(19,0) not null,
   CHILD number(19,0) not null,
   primary key (PARENT, CHILD)
)
create table CMN_AUTHORIZATION_T (
   ID number(19,0) not null,
   VERSION number(10,0) not null,
   UUID varchar2(36) not null unique,
   LAST_MODIFIED_BY varchar2(36) not null,
   LAST_MODIFIED_DATE date not null,
   CREATED_BY varchar2(36) not null,
   CREATED_DATE date not null,
   AGENT_UUID varchar2(36) not null,
   PERMISSIONS_UUID varchar2(36) not null,
   NODE_UUID varchar2(36) not null,
   EFFECTIVE_DATE date not null,
   EXPIRATION_DATE date,
   primary key (ID),
   unique (AGENT_UUID, PERMISSIONS_UUID, NODE_UUID)
)
create index CMN_AGENT_T_SESSN_MGR_USR_ID_I on CMN_AGENT_GROUP_T (SESSION_MGR_USER_ID)
create index CMN_AGENT_T_DISPLAY_NAME_I on CMN_AGENT_GROUP_T (DISPLAY_NAME)
create index CMN_AGENT_T_ENTERPRISE_ID_I on CMN_AGENT_GROUP_T (ENTERPRISE_ID)
create index CMN_AGENT_T_NODE_I on CMN_AGENT_GROUP_T (NODE_FK)
create index CMN_AGENT_T_TYPE_I on CMN_AGENT_GROUP_T (TYPE_FK)
alter table CMN_AGENT_GROUP_T add constraint FKD858333FA8D3F022 foreign key (NODE_FK) references CMN_NODE_T
alter table CMN_AGENT_GROUP_T add constraint FKD858333FF7F2E3EA foreign key (TYPE_FK) references CMN_TYPE_T
create index CMN_TYPE_T_DISPLAY_NAME_I on CMN_TYPE_T (DISPLAY_NAME)
create index CMN_TYPE_T_KEYWORD_I on CMN_TYPE_T (KEYWORD)
create index CMN_TYPE_T_DOMAIN_I on CMN_TYPE_T (DOMAIN)
create index CMN_TYPE_T_AUTHORITY_I on CMN_TYPE_T (AUTHORITY)
create index SAKAI_PERSON_TYPE_UUID_I on SAKAI_PERSON_T (TYPE_UUID)
create index SAKAI_PERSON_SURNAME_I on SAKAI_PERSON_T (SURNAME)
create index SAKAI_PERSON_ferpaEnabled_I on SAKAI_PERSON_T (ferpaEnabled)
create index SAKAI_PERSON_AGENT_UUID_I on SAKAI_PERSON_T (AGENT_UUID)
create index SAKAI_PERSON_GIVEN_NAME_I on SAKAI_PERSON_T (GIVEN_NAME)
create index SAKAI_PERSON_UID_I on SAKAI_PERSON_T (UID_C)
create index CMN_NODE_DISPLAY_NAME_I on CMN_NODE_T (DISPLAY_NAME)
create index CMN_NODE_DEPTH_I on CMN_NODE_T (DEPTH)
create index CMN_NODE_TYPE_I on CMN_NODE_T (TYPE)
create index CMN_NODE_REFERENCE_UUID_I on CMN_NODE_T (REFERENCE_UUID)
create index CMN_NODE_PARENT_I on CMN_NODE_T (PARENT_ID)
alter table CMN_NODE_T add constraint FK596CE0D2217F38D0 foreign key (PARENT_ID) references CMN_NODE_T
alter table CMN_NODE_T add constraint FK596CE0D227873A foreign key (TYPE) references CMN_TYPE_T
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
alter table CMN_NODE_PARENT_CHILD_MAP_T add constraint FK614EA03B3D1FCFC foreign key (CHILD) references CMN_NODE_T
alter table CMN_NODE_PARENT_CHILD_MAP_T add constraint FK614EA03B8C3DFCAA foreign key (PARENT) references CMN_NODE_T
create index CMN_GRP_PRNT_MMBR_MAP_PRNT_I on CMN_GRP_PRNT_MMBR_MAP_T (PARENT)
create index CMN_GRP_PRNT_MMBR_MAP_MMBR_I on CMN_GRP_PRNT_MMBR_MAP_T (MEMBER)
alter table CMN_GRP_PRNT_MMBR_MAP_T add constraint FKEBB1654487557E9A foreign key (MEMBER) references CMN_AGENT_GROUP_T
alter table CMN_GRP_PRNT_MMBR_MAP_T add constraint FKEBB165448C3DFCAA foreign key (PARENT) references CMN_AGENT_GROUP_T
alter table CMN_AGENT_GROUP_ASSOC_T add constraint FKBE10ED353D1FCFC foreign key (CHILD) references CMN_AGENT_GROUP_T
alter table CMN_AGENT_GROUP_ASSOC_T add constraint FKBE10ED358C3DFCAA foreign key (PARENT) references CMN_AGENT_GROUP_T
create index CMN_AUTHZ_NODE_UUID_I on CMN_AUTHORIZATION_T (NODE_UUID)
create index CMN_AUTHZ_AGENT_UUID_I on CMN_AUTHORIZATION_T (AGENT_UUID)
create index CMN_AUTHZ_PERMISSIONS_UUID_I on CMN_AUTHORIZATION_T (PERMISSIONS_UUID)
create sequence CMN_NODE_PARENT_CHILD_MAP_S
create sequence CMN_NODE_S
create sequence CMN_DEFAULT_PERMISSIONS_S
create sequence CMN_GRP_PRNT_MMBR_MAP_S
create sequence CMN_TYPE_S
create sequence CMN_AGENT_GROUP_S
create sequence CMN_AUTHORIZATION_S
create sequence SAKAI_PERSON_S
