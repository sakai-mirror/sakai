alter table SYLLABUS_DATA_T drop foreign key FK8AD134994FDCE067;
drop table if exists SYLLABUS_DATA_T;
drop table if exists SYLLABUS_T;
create table SYLLABUS_DATA_T (
   id bigint not null auto_increment,
   lockId integer not null,
   asset text,
   position integer not null,
   title text,
   xview varchar(16),
   status varchar(64),
   emailNotification varchar(128),
   surrogateKey bigint,
   primary key (id)
);
create table SYLLABUS_T (
   id bigint not null auto_increment,
   lockId integer not null,
   userId varchar(36) not null,
   contextId varchar(36) not null,
   redirectURL text,
   primary key (id),
   unique (userId, contextId)
);
create index syllabus_position on SYLLABUS_DATA_T (position);
alter table SYLLABUS_DATA_T add index FK8AD134994FDCE067 (surrogateKey), add constraint FK8AD134994FDCE067 foreign key (surrogateKey) references SYLLABUS_T (id);
create index syllabus_userId on SYLLABUS_T (userId);
create index syllabus_contextId on SYLLABUS_T (contextId);
