alter table SAKAI_SYLLABUS_DATA drop constraint FK3BC123AA4FDCE067
drop table SAKAI_SYLLABUS_DATA if exists
drop table SAKAI_SYLLABUS_ITEM if exists
create table SAKAI_SYLLABUS_DATA (
   id bigint generated by default as identity (start with 1),
   lockId integer not null,
   asset longvarchar,
   position integer not null,
   title varchar(256),
   xview varchar(16),
   status varchar(64),
   emailNotification varchar(128),
   surrogateKey bigint
)
create table SAKAI_SYLLABUS_ITEM (
   id bigint generated by default as identity (start with 1),
   lockId integer not null,
   userId varchar(36) not null,
   contextId varchar(36) not null,
   redirectURL varchar(512),
   unique (userId, contextId)
)
create index syllabus_position on SAKAI_SYLLABUS_DATA (position)
alter table SAKAI_SYLLABUS_DATA add constraint FK3BC123AA4FDCE067 foreign key (surrogateKey) references SAKAI_SYLLABUS_ITEM
create index syllabus_userId on SAKAI_SYLLABUS_ITEM (userId)
create index syllabus_contextId on SAKAI_SYLLABUS_ITEM (contextId)
