alter table SYLLABUS_DATA_T drop constraint FK8AD134994FDCE067;
drop table SYLLABUS_DATA_T cascade constraints;
drop table SYLLABUS_T cascade constraints;
drop sequence SyllabusDataImpl_SEQ;
drop sequence SyllabusItemImpl_SEQ;
create table SYLLABUS_DATA_T (
   id number(19,0) not null,
   lockId number(10,0) not null,
   asset clob,
   position number(10,0) not null,
   title varchar2(256),
   xview varchar2(16),
   status varchar2(64),
   emailNotification varchar2(128),
   surrogateKey number(19,0),
   primary key (id)
);
create table SYLLABUS_T (
   id number(19,0) not null,
   lockId number(10,0) not null,
   userId varchar2(36) not null,
   contextId varchar2(36) not null,
   redirectURL varchar2(512),
   primary key (id),
   unique (userId, contextId)
);
create index syllabus_position on SYLLABUS_DATA_T (position);
alter table SYLLABUS_DATA_T add constraint FK8AD134994FDCE067 foreign key (surrogateKey) references SYLLABUS_T;
create index syllabus_userId on SYLLABUS_T (userId);
create index syllabus_contextId on SYLLABUS_T (contextId);
create sequence SyllabusDataImpl_SEQ;
create sequence SyllabusItemImpl_SEQ;
