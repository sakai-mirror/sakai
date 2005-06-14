alter table sam_gradingSummary_t drop foreign key FK5F622E077BD8188B;
alter table sam_item_t drop foreign key FK80FB06A8D78B62E0;
alter table sam_publishedItem_t drop foreign key FK373D6576D78B62E0;
alter table sam_publishedItemMetaData_t drop foreign key FKBB428825B9BF0B8E;
alter table sam_publishedItemText_t drop foreign key FKD3215703B9BF0B8E;
alter table sam_securedIP_t drop foreign key FKAFEA10493660977D;
alter table sam_section_t drop foreign key FKF358113A3660977D;
alter table sam_publishedItemFeedback_t drop foreign key FKC8EB4EDBB9BF0B8E;
alter table sam_media_t drop foreign key FK585626792ED9513C;
alter table sam_itemGrading_t drop foreign key FKF7272F9633FEB0A9;
alter table sam_itemGrading_t drop foreign key FKF7272F964AF7EF1C;
alter table sam_itemGrading_t drop foreign key FKF7272F96A9AE4A6D;
alter table sam_itemGrading_t drop foreign key FKF7272F968C78987;
alter table sam_publishedEvaluation_t drop foreign key FKBEC6F0DF3660977D;
alter table sam_answerFeedback_t drop foreign key FKFB0830F8E2395179;
alter table sam_publishedFeedback_t drop foreign key FK81CC3A683660977D;
alter table sam_publishedSecuredIP_t drop foreign key FK59F8C23B3660977D;
alter table sam_publishedAssessment_t drop foreign key FKDCE056E53660977D;
alter table sam_itemText_t drop foreign key FKD5E70F35B9BF0B8E;
alter table sam_assessFeedback_t drop foreign key FKF7B68D1E3660977D;
alter table sam_publishedAccessControl_t drop foreign key FK552FA5A03660977D;
alter table sam_publishedAnswerFeedback_t drop foreign key FK1074E646E2395179;
alter table sam_assessEvaluation_t drop foreign key FK63676E153660977D;
alter table sam_publishedAnswer_t drop foreign key FKF97E97E1781E441B;
alter table sam_publishedAnswer_t drop foreign key FKF97E97E1B9BF0B8E;
alter table sam_assessmentGrading_t drop foreign key FK48D0F0C77BD8188B;
alter table sam_publishedSection_t drop foreign key FKA8EE97AC3660977D;
alter table sam_publishedMetaData_t drop foreign key FK742373B23660977D;
alter table sam_itemFeedback_t drop foreign key FK4D59E0DB9BF0B8E;
alter table sam_itemMetaData_t drop foreign key FKF72CD757B9BF0B8E;
alter table sam_assessAccessControl_t drop foreign key FK257A94AA3660977D;
alter table sam_answer_t drop foreign key FKCA58B493B9BF0B8E;
alter table sam_answer_t drop foreign key FKCA58B493781E441B;
alter table sam_assessMetaData_t drop foreign key FKEA0DC6683660977D;
drop table if exists sam_gradingSummary_t;
drop table if exists sam_item_t;
drop table if exists sam_publishedItem_t;
drop table if exists sam_publishedItemMetaData_t;
drop table if exists sam_publishedItemText_t;
drop table if exists sam_securedIP_t;
drop table if exists sam_section_t;
drop table if exists sam_publishedItemFeedback_t;
drop table if exists sam_media_t;
drop table if exists sam_itemGrading_t;
drop table if exists sam_questionPool_t;
drop table if exists sam_publishedEvaluation_t;
drop table if exists sam_answerFeedback_t;
drop table if exists sam_publishedFeedback_t;
drop table if exists sam_questionPoolItem_t;
drop table if exists sam_publishedSecuredIP_t;
drop table if exists sam_functionData_t;
drop table if exists sam_assessmentBase_t;
drop table if exists sam_publishedAssessment_t;
drop table if exists sam_itemText_t;
drop table if exists sam_assessFeedback_t;
drop table if exists sam_type_t;
drop table if exists sam_publishedAccessControl_t;
drop table if exists sam_publishedAnswerFeedback_t;
drop table if exists sam_assessEvaluation_t;
drop table if exists sam_publishedAnswer_t;
drop table if exists sam_authzData_t;
drop table if exists sam_qualifierData_t;
drop table if exists sam_assessmentGrading_t;
drop table if exists sam_publishedSection_t;
drop table if exists sam_publishedMetaData_t;
drop table if exists sam_itemFeedback_t;
drop table if exists sam_itemMetaData_t;
drop table if exists sam_assessAccessControl_t;
drop table if exists sam_answer_t;
drop table if exists sam_questionPoolAccess_t;
drop table if exists sam_assessMetaData_t;
create table sam_gradingSummary_t (
   assessmentGradingSummaryId bigint not null auto_increment,
   publishedAssessmentId bigint not null,
   agentId varchar(36) not null,
   totalSubmitted integer,
   totalSubmittedForGrade integer,
   lastSubmittedDate datetime,
   lastSubmittedAssessmentIsLate integer not null,
   sumOf_autoScoreForGrade float,
   average_autoScoreForGrade float,
   highest_autoScoreForGrade float,
   lowest_autoScoreForGrade float,
   last_autoScoreForGrade float,
   sumOf_overrideScoreForGrade float,
   average_overrideScoreForGrade float,
   highest_overrideScoreForGrade float,
   lowest_overrideScoreForGrade float,
   last_overrideScoreForGrade float,
   scoringType integer,
   acceptedAssessmentIsLate integer,
   finalAssessmentScore float,
   feedToGradeBook integer,
   primary key (assessmentGradingSummaryId)
);
create table sam_item_t (
   itemId bigint not null auto_increment,
   sectionId bigint,
   itemIdString varchar(36),
   sequence integer,
   duration integer,
   triesAllowed integer,
   instruction text,
   description text,
   typeId varchar(36) not null,
   grade varchar(80),
   score float,
   hint text,
   hasRationale varchar(1),
   status integer not null,
   createdBy varchar(36) not null,
   createdDate datetime not null,
   lastModifiedBy varchar(36) not null,
   lastModifiedDate datetime not null,
   primary key (itemId)
);
create table sam_publishedItem_t (
   itemId bigint not null auto_increment,
   sectionId bigint not null,
   itemIdString varchar(36),
   sequence integer,
   duration integer,
   triesAllowed integer,
   instruction text,
   description text,
   typeId varchar(36) not null,
   grade varchar(80),
   score float,
   hint text,
   hasRationale varchar(1),
   status integer not null,
   createdBy varchar(36) not null,
   createdDate datetime not null,
   lastModifiedBy varchar(36) not null,
   lastModifiedDate datetime not null,
   primary key (itemId)
);
create table sam_publishedItemMetaData_t (
   itemMetaDataId bigint not null auto_increment,
   itemId bigint not null,
   label varchar(255) not null,
   entry varchar(255),
   primary key (itemMetaDataId)
);
create table sam_publishedItemText_t (
   itemTextId bigint not null auto_increment,
   itemId bigint not null,
   sequence integer not null,
   text text,
   primary key (itemTextId)
);
create table sam_securedIP_t (
   ipAddressId bigint not null auto_increment,
   assessmentId bigint not null,
   hostname varchar(255),
   ipAddress varchar(255),
   primary key (ipAddressId)
);
create table sam_section_t (
   sectionId bigint not null auto_increment,
   assessmentId bigint not null,
   duration integer,
   sequence integer,
   title varchar(255),
   description text,
   typeId integer,
   status integer not null,
   createdBy varchar(36) not null,
   createdDate datetime not null,
   lastModifiedBy varchar(36) not null,
   lastModifiedDate datetime not null,
   primary key (sectionId)
);
create table sam_publishedItemFeedback_t (
   itemFeedbackId bigint not null auto_increment,
   itemId bigint not null,
   typeId varchar(36) not null,
   text text,
   primary key (itemFeedbackId)
);
create table sam_media_t (
   mediaId bigint not null auto_increment,
   itemGradingId bigint,
   media blob,
   fileSize integer,
   mimeType varchar(80),
   description text,
   location varchar(255),
   filename varchar(255),
   isLink integer,
   isHtmlInline integer,
   status integer,
   createdBy varchar(36),
   createdDate datetime,
   lastModifiedBy varchar(36),
   lastModifiedDate datetime,
   primary key (mediaId)
);
create table sam_itemGrading_t (
   itemGradingId bigint not null auto_increment,
   assessmentGradingId bigint not null,
   publishedItemId bigint not null,
   publishedItemTextId bigint not null,
   agentId varchar(36) not null,
   submittedDate datetime not null,
   publishedAnswerId bigint,
   rationale text,
   answerText text,
   autoScore float,
   overrideScore float,
   Comments text,
   gradedBy varchar(36),
   gradedDate datetime,
   review integer,
   primary key (itemGradingId)
);
create table sam_questionPool_t (
   questionPoolId bigint not null auto_increment,
   title varchar(255),
   description varchar(255),
   parentPoolId integer,
   ownerId varchar(255),
   organizationName varchar(255),
   dateCreated datetime,
   lastModifiedDate datetime,
   lastModifiedBy varchar(255),
   defaultAccessTypeId integer,
   objective varchar(255),
   keywords varchar(255),
   rubric text,
   typeId integer,
   intellectualPropertyId integer,
   primary key (questionPoolId)
);
create table sam_publishedEvaluation_t (
   assessmentId bigint not null,
   evaluationComponents varchar(255),
   scoringType integer,
   numericModelId varchar(255),
   fixedTotalScore integer,
   gradeAvailable integer,
   isStudentIdPublic integer,
   anonymousGrading integer,
   autoScoring integer,
   toGradeBook integer,
   primary key (assessmentId)
);
create table sam_answerFeedback_t (
   answerFeedbackId bigint not null auto_increment,
   answerId bigint not null,
   typeId varchar(36),
   text text,
   primary key (answerFeedbackId)
);
create table sam_publishedFeedback_t (
   assessmentId bigint not null,
   feedbackDelivery integer,
   editComponents integer,
   showQuestionText integer,
   showStudentResponse integer,
   showCorrectResponse integer,
   showStudentScore integer,
   showQuestionLevelFeedback integer,
   showSelectionLevelFeedback integer,
   showGraderComments integer,
   showStatistics integer,
   primary key (assessmentId)
);
create table sam_questionPoolItem_t (
   questionPoolId bigint not null,
   itemId varchar(255) not null,
   primary key (questionPoolId, itemId)
);
create table sam_publishedSecuredIP_t (
   ipAddressId bigint not null auto_increment,
   assessmentId bigint not null,
   hostname varchar(255),
   ipAddress varchar(255),
   primary key (ipAddressId)
);
create table sam_functionData_t (
   functionId bigint not null auto_increment,
   referenceName varchar(255) not null,
   displayName varchar(255),
   description text,
   functionTypeId text,
   primary key (functionId)
);
create table sam_assessmentBase_t (
   id bigint not null auto_increment,
   isTemplate varchar(255) not null,
   parentId integer,
   title varchar(255),
   description text,
   comments text,
   typeId varchar(36),
   instructorNotification integer,
   testeeNotification integer,
   multipartAllowed integer,
   status integer not null,
   createdBy varchar(36) not null,
   createdDate datetime not null,
   lastModifiedBy varchar(36) not null,
   lastModifiedDate datetime not null,
   assessmentTemplateId bigint,
   primary key (id)
);
create table sam_publishedAssessment_t (
   id bigint not null auto_increment,
   assessmentId bigint not null,
   title varchar(255),
   description text,
   comments varchar(255),
   typeId varchar(36),
   instructorNotification integer,
   testeeNotification integer,
   multipartAllowed integer,
   status integer not null,
   createdBy varchar(36) not null,
   createdDate datetime not null,
   lastModifiedBy varchar(36) not null,
   lastModifiedDate datetime not null,
   primary key (id)
);
create table sam_itemText_t (
   itemTextId bigint not null auto_increment,
   itemId bigint not null,
   sequence integer not null,
   text text,
   primary key (itemTextId)
);
create table sam_assessFeedback_t (
   assessmentId bigint not null,
   feedbackDelivery integer,
   editComponents integer,
   showQuestionText integer,
   showStudentResponse integer,
   showCorrectResponse integer,
   showStudentScore integer,
   showQuestionLevelFeedback integer,
   showSelectionLevelFeedback integer,
   showGraderComments integer,
   showStatistics integer,
   primary key (assessmentId)
);
create table sam_type_t (
   typeId bigint not null auto_increment,
   authority varchar(255),
   domain varchar(255),
   keyword varchar(255),
   description text,
   status integer not null,
   createdBy varchar(36) not null,
   createdDate datetime not null,
   lastModifiedBy varchar(36) not null,
   lastModifiedDate datetime not null,
   primary key (typeId)
);
create table sam_publishedAccessControl_t (
   assessmentId bigint not null,
   unlimitedSubmissions integer,
   submissionsAllowed integer,
   submissionsSaved integer,
   assessmentFormat integer,
   bookMarkingItem integer,
   timeLimit integer,
   timedAssessment integer,
   retryAllowed integer,
   lateHandling integer,
   startDate datetime,
   dueDate datetime,
   scoreDate datetime,
   feedbackDate datetime,
   retractDate datetime,
   autoSubmit integer,
   itemNavigation integer,
   itemNumbering integer,
   submissionMessage text,
   releaseTo varchar(255),
   username varchar(255),
   password varchar(255),
   finalPageUrl text,
   primary key (assessmentId)
);
create table sam_publishedAnswerFeedback_t (
   answerFeedbackId bigint not null auto_increment,
   answerId bigint not null,
   typeId varchar(36),
   text text,
   primary key (answerFeedbackId)
);
create table sam_assessEvaluation_t (
   assessmentId bigint not null,
   evaluationComponents varchar(255),
   scoringType integer,
   numericModelId varchar(255),
   fixedTotalScore integer,
   gradeAvailable integer,
   isStudentIdPublic integer,
   anonymousGrading integer,
   autoScoring integer,
   toGradeBook varchar(255),
   primary key (assessmentId)
);
create table sam_publishedAnswer_t (
   answerId bigint not null auto_increment,
   itemTextId bigint not null,
   itemId bigint not null,
   text text,
   sequence integer not null,
   label varchar(20),
   isCorrect varchar(1),
   grade varchar(80),
   score float,
   primary key (answerId)
);
create table sam_authzData_t (
   id bigint not null auto_increment,
   lockId integer not null,
   agentId varchar(36) not null,
   functionId varchar(36) not null,
   qualifierId varchar(36) not null,
   effectiveDate datetime,
   expirationDate datetime,
   lastModifiedBy varchar(36) not null,
   lastModifiedDate datetime not null,
   isExplicit integer,
   primary key (id),
   unique (agentId, functionId, qualifierId)
);
create table sam_qualifierData_t (
   qualifierId bigint not null auto_increment,
   referenceName varchar(255) not null,
   displayName varchar(255),
   description text,
   qualifierTypeId text,
   primary key (qualifierId)
);
create table sam_assessmentGrading_t (
   assessmentGradingId bigint not null auto_increment,
   publishedAssessmentId bigint not null,
   agentId varchar(36) not null,
   submittedDate datetime not null,
   isLate varchar(1) not null,
   forGrade integer not null,
   totalAutoScore float,
   totalOverrideScore float,
   finalScore float,
   comments text,
   gradedBy varchar(36),
   gradedDate datetime,
   status integer not null,
   attemptDate datetime,
   timeElapsed integer,
   primary key (assessmentGradingId)
);
create table sam_publishedSection_t (
   sectionId bigint not null auto_increment,
   assessmentId bigint not null,
   duration integer,
   sequence integer,
   title varchar(255),
   description text,
   typeId integer not null,
   status integer not null,
   createdBy varchar(36) not null,
   createdDate datetime not null,
   lastModifiedBy varchar(36) not null,
   lastModifiedDate datetime not null,
   primary key (sectionId)
);
create table sam_publishedMetaData_t (
   assessmentMetaDataId bigint not null auto_increment,
   assessmentId bigint not null,
   label varchar(255) not null,
   entry varchar(255),
   primary key (assessmentMetaDataId)
);
create table sam_itemFeedback_t (
   itemFeedbackId bigint not null auto_increment,
   itemId bigint not null,
   typeId varchar(36) not null,
   text text,
   primary key (itemFeedbackId)
);
create table sam_itemMetaData_t (
   itemMetaDataId bigint not null auto_increment,
   itemId bigint not null,
   label varchar(255) not null,
   entry varchar(255),
   primary key (itemMetaDataId)
);
create table sam_assessAccessControl_t (
   assessmentId bigint not null,
   submissionsAllowed integer,
   unlimitedSubmissions integer,
   submissionsSaved integer,
   assessmentFormat integer,
   bookMarkingItem integer,
   timeLimit integer,
   timedAssessment integer,
   retryAllowed integer,
   lateHandling integer,
   startDate datetime,
   dueDate datetime,
   scoreDate datetime,
   feedbackDate datetime,
   retractDate datetime,
   autoSubmit integer,
   itemNavigation integer,
   itemNumbering integer,
   submissionMessage text,
   releaseTo varchar(255),
   username varchar(255),
   password varchar(255),
   finalPageUrl text,
   primary key (assessmentId)
);
create table sam_answer_t (
   answerId bigint not null auto_increment,
   itemTextId bigint not null,
   itemId bigint not null,
   text text,
   sequence integer not null,
   label varchar(20),
   isCorrect varchar(1),
   grade varchar(80),
   score float,
   primary key (answerId)
);
create table sam_questionPoolAccess_t (
   questionPoolId bigint not null,
   agentId varchar(255) not null,
   accessTypeId bigint not null,
   primary key (questionPoolId, agentId, accessTypeId)
);
create table sam_assessMetaData_t (
   assessmentMetaDataId bigint not null auto_increment,
   assessmentId bigint not null,
   label varchar(255) not null,
   entry varchar(255),
   primary key (assessmentMetaDataId)
);
alter table sam_gradingSummary_t add index FK5F622E077BD8188B (publishedAssessmentId), add constraint FK5F622E077BD8188B foreign key (publishedAssessmentId) references sam_publishedAssessment_t (id);
alter table sam_item_t add index FK80FB06A8D78B62E0 (sectionId), add constraint FK80FB06A8D78B62E0 foreign key (sectionId) references sam_section_t (sectionId);
alter table sam_publishedItem_t add index FK373D6576D78B62E0 (sectionId), add constraint FK373D6576D78B62E0 foreign key (sectionId) references sam_publishedSection_t (sectionId);
alter table sam_publishedItemMetaData_t add index FKBB428825B9BF0B8E (itemId), add constraint FKBB428825B9BF0B8E foreign key (itemId) references sam_publishedItem_t (itemId);
alter table sam_publishedItemText_t add index FKD3215703B9BF0B8E (itemId), add constraint FKD3215703B9BF0B8E foreign key (itemId) references sam_publishedItem_t (itemId);
alter table sam_securedIP_t add index FKAFEA10493660977D (assessmentId), add constraint FKAFEA10493660977D foreign key (assessmentId) references sam_assessmentBase_t (id);
alter table sam_section_t add index FKF358113A3660977D (assessmentId), add constraint FKF358113A3660977D foreign key (assessmentId) references sam_assessmentBase_t (id);
alter table sam_publishedItemFeedback_t add index FKC8EB4EDBB9BF0B8E (itemId), add constraint FKC8EB4EDBB9BF0B8E foreign key (itemId) references sam_publishedItem_t (itemId);
alter table sam_media_t add index FK585626792ED9513C (itemGradingId), add constraint FK585626792ED9513C foreign key (itemGradingId) references sam_itemGrading_t (itemGradingId);
alter table sam_itemGrading_t add index FKF7272F9633FEB0A9 (publishedItemTextId), add constraint FKF7272F9633FEB0A9 foreign key (publishedItemTextId) references sam_publishedItemText_t (itemTextId);
alter table sam_itemGrading_t add index FKF7272F964AF7EF1C (publishedItemId), add constraint FKF7272F964AF7EF1C foreign key (publishedItemId) references sam_publishedItem_t (itemId);
alter table sam_itemGrading_t add index FKF7272F96A9AE4A6D (assessmentGradingId), add constraint FKF7272F96A9AE4A6D foreign key (assessmentGradingId) references sam_assessmentGrading_t (assessmentGradingId);
alter table sam_itemGrading_t add index FKF7272F968C78987 (publishedAnswerId), add constraint FKF7272F968C78987 foreign key (publishedAnswerId) references sam_publishedAnswer_t (answerId);
alter table sam_publishedEvaluation_t add index FKBEC6F0DF3660977D (assessmentId), add constraint FKBEC6F0DF3660977D foreign key (assessmentId) references sam_publishedAssessment_t (id);
alter table sam_answerFeedback_t add index FKFB0830F8E2395179 (answerId), add constraint FKFB0830F8E2395179 foreign key (answerId) references sam_answer_t (answerId);
alter table sam_publishedFeedback_t add index FK81CC3A683660977D (assessmentId), add constraint FK81CC3A683660977D foreign key (assessmentId) references sam_publishedAssessment_t (id);
alter table sam_publishedSecuredIP_t add index FK59F8C23B3660977D (assessmentId), add constraint FK59F8C23B3660977D foreign key (assessmentId) references sam_publishedAssessment_t (id);
alter table sam_publishedAssessment_t add index FKDCE056E53660977D (assessmentId), add constraint FKDCE056E53660977D foreign key (assessmentId) references sam_assessmentBase_t (id);
alter table sam_itemText_t add index FKD5E70F35B9BF0B8E (itemId), add constraint FKD5E70F35B9BF0B8E foreign key (itemId) references sam_item_t (itemId);
alter table sam_assessFeedback_t add index FKF7B68D1E3660977D (assessmentId), add constraint FKF7B68D1E3660977D foreign key (assessmentId) references sam_assessmentBase_t (id);
alter table sam_publishedAccessControl_t add index FK552FA5A03660977D (assessmentId), add constraint FK552FA5A03660977D foreign key (assessmentId) references sam_publishedAssessment_t (id);
alter table sam_publishedAnswerFeedback_t add index FK1074E646E2395179 (answerId), add constraint FK1074E646E2395179 foreign key (answerId) references sam_publishedAnswer_t (answerId);
alter table sam_assessEvaluation_t add index FK63676E153660977D (assessmentId), add constraint FK63676E153660977D foreign key (assessmentId) references sam_assessmentBase_t (id);
alter table sam_publishedAnswer_t add index FKF97E97E1781E441B (itemTextId), add constraint FKF97E97E1781E441B foreign key (itemTextId) references sam_publishedItemText_t (itemTextId);
alter table sam_publishedAnswer_t add index FKF97E97E1B9BF0B8E (itemId), add constraint FKF97E97E1B9BF0B8E foreign key (itemId) references sam_publishedItem_t (itemId);
create index sam_authz_functionId_idx on sam_authzData_t (functionId);
create index sam_authz_qualifierId_idx on sam_authzData_t (qualifierId);
create index sam_authz_agentId_idx on sam_authzData_t (agentId);
alter table sam_assessmentGrading_t add index FK48D0F0C77BD8188B (publishedAssessmentId), add constraint FK48D0F0C77BD8188B foreign key (publishedAssessmentId) references sam_publishedAssessment_t (id);
alter table sam_publishedSection_t add index FKA8EE97AC3660977D (assessmentId), add constraint FKA8EE97AC3660977D foreign key (assessmentId) references sam_publishedAssessment_t (id);
alter table sam_publishedMetaData_t add index FK742373B23660977D (assessmentId), add constraint FK742373B23660977D foreign key (assessmentId) references sam_publishedAssessment_t (id);
alter table sam_itemFeedback_t add index FK4D59E0DB9BF0B8E (itemId), add constraint FK4D59E0DB9BF0B8E foreign key (itemId) references sam_item_t (itemId);
alter table sam_itemMetaData_t add index FKF72CD757B9BF0B8E (itemId), add constraint FKF72CD757B9BF0B8E foreign key (itemId) references sam_item_t (itemId);
alter table sam_assessAccessControl_t add index FK257A94AA3660977D (assessmentId), add constraint FK257A94AA3660977D foreign key (assessmentId) references sam_assessmentBase_t (id);
alter table sam_answer_t add index FKCA58B493B9BF0B8E (itemId), add constraint FKCA58B493B9BF0B8E foreign key (itemId) references sam_item_t (itemId);
alter table sam_answer_t add index FKCA58B493781E441B (itemTextId), add constraint FKCA58B493781E441B foreign key (itemTextId) references sam_itemText_t (itemTextId);
alter table sam_assessMetaData_t add index FKEA0DC6683660977D (assessmentId), add constraint FKEA0DC6683660977D foreign key (assessmentId) references sam_assessmentBase_t (id);
