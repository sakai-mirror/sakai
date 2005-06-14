alter table sam_gradingSummary_t drop constraint FK5F622E077BD8188B;
alter table sam_item_t drop constraint FK80FB06A8D78B62E0;
alter table sam_publishedItem_t drop constraint FK373D6576D78B62E0;
alter table sam_publishedItemMetaData_t drop constraint FKBB428825B9BF0B8E;
alter table sam_publishedItemText_t drop constraint FKD3215703B9BF0B8E;
alter table sam_securedIP_t drop constraint FKAFEA10493660977D;
alter table sam_section_t drop constraint FKF358113A3660977D;
alter table sam_publishedItemFeedback_t drop constraint FKC8EB4EDBB9BF0B8E;
alter table sam_media_t drop constraint FK585626792ED9513C;
alter table sam_itemGrading_t drop constraint FKF7272F9633FEB0A9;
alter table sam_itemGrading_t drop constraint FKF7272F964AF7EF1C;
alter table sam_itemGrading_t drop constraint FKF7272F96A9AE4A6D;
alter table sam_itemGrading_t drop constraint FKF7272F968C78987;
alter table sam_publishedEvaluation_t drop constraint FKBEC6F0DF3660977D;
alter table sam_answerFeedback_t drop constraint FKFB0830F8E2395179;
alter table sam_publishedFeedback_t drop constraint FK81CC3A683660977D;
alter table sam_publishedSecuredIP_t drop constraint FK59F8C23B3660977D;
alter table sam_publishedAssessment_t drop constraint FKDCE056E53660977D;
alter table sam_itemText_t drop constraint FKD5E70F35B9BF0B8E;
alter table sam_assessFeedback_t drop constraint FKF7B68D1E3660977D;
alter table sam_publishedAccessControl_t drop constraint FK552FA5A03660977D;
alter table sam_publishedAnswerFeedback_t drop constraint FK1074E646E2395179;
alter table sam_assessEvaluation_t drop constraint FK63676E153660977D;
alter table sam_publishedAnswer_t drop constraint FKF97E97E1781E441B;
alter table sam_publishedAnswer_t drop constraint FKF97E97E1B9BF0B8E;
alter table sam_assessmentGrading_t drop constraint FK48D0F0C77BD8188B;
alter table sam_publishedSection_t drop constraint FKA8EE97AC3660977D;
alter table sam_publishedMetaData_t drop constraint FK742373B23660977D;
alter table sam_itemFeedback_t drop constraint FK4D59E0DB9BF0B8E;
alter table sam_itemMetaData_t drop constraint FKF72CD757B9BF0B8E;
alter table sam_assessAccessControl_t drop constraint FK257A94AA3660977D;
alter table sam_answer_t drop constraint FKCA58B493B9BF0B8E;
alter table sam_answer_t drop constraint FKCA58B493781E441B;
alter table sam_assessMetaData_t drop constraint FKEA0DC6683660977D;
drop table sam_gradingSummary_t cascade constraints;
drop table sam_item_t cascade constraints;
drop table sam_publishedItem_t cascade constraints;
drop table sam_publishedItemMetaData_t cascade constraints;
drop table sam_publishedItemText_t cascade constraints;
drop table sam_securedIP_t cascade constraints;
drop table sam_section_t cascade constraints;
drop table sam_publishedItemFeedback_t cascade constraints;
drop table sam_media_t cascade constraints;
drop table sam_itemGrading_t cascade constraints;
drop table sam_questionPool_t cascade constraints;
drop table sam_publishedEvaluation_t cascade constraints;
drop table sam_answerFeedback_t cascade constraints;
drop table sam_publishedFeedback_t cascade constraints;
drop table sam_questionPoolItem_t cascade constraints;
drop table sam_publishedSecuredIP_t cascade constraints;
drop table sam_functionData_t cascade constraints;
drop table sam_assessmentBase_t cascade constraints;
drop table sam_publishedAssessment_t cascade constraints;
drop table sam_itemText_t cascade constraints;
drop table sam_assessFeedback_t cascade constraints;
drop table sam_type_t cascade constraints;
drop table sam_publishedAccessControl_t cascade constraints;
drop table sam_publishedAnswerFeedback_t cascade constraints;
drop table sam_assessEvaluation_t cascade constraints;
drop table sam_publishedAnswer_t cascade constraints;
drop table sam_authzData_t cascade constraints;
drop table sam_qualifierData_t cascade constraints;
drop table sam_assessmentGrading_t cascade constraints;
drop table sam_publishedSection_t cascade constraints;
drop table sam_publishedMetaData_t cascade constraints;
drop table sam_itemFeedback_t cascade constraints;
drop table sam_itemMetaData_t cascade constraints;
drop table sam_assessAccessControl_t cascade constraints;
drop table sam_answer_t cascade constraints;
drop table sam_questionPoolAccess_t cascade constraints;
drop table sam_assessMetaData_t cascade constraints;
drop sequence sam_type_id_s;
drop sequence sam_functionId_s;
drop sequence sam_securedIP_id_s;
drop sequence sam_item_id_s;
drop sequence sam_publishedSecuredIP_id_s;
drop sequence sam_gradingSummary_id_s;
drop sequence sam_pubItem_id_s;
drop sequence sam_itemFeedback_id_s;
drop sequence sam_section_id_s;
drop sequence sam_pubAnswerFeedback_id_s;
drop sequence sam_pubAnswer_id_s;
drop sequence sam_assessmentGrading_id_s;
drop sequence sam_publishedMetaData_id_s;
drop sequence sam_publishedSection_id_s;
drop sequence sam_media_id_s;
drop sequence sam_assessmentBase_id_s;
drop sequence sam_questionPool_id_s;
drop sequence sam_publishedAssessment_id_s;
drop sequence sam_assessMetaData_id_s;
drop sequence sam_answer_id_s;
drop sequence sam_itemMetaData_id_s;
drop sequence sam_AuthzData_s;
drop sequence sam_pubItemMetaData_id_s;
drop sequence sam_answerFeedback_id_s;
drop sequence sam_pubItemFeedback_id_s;
drop sequence sam_pubItemText_id_s;
drop sequence sam_itemText_id_s;
drop sequence sam_itemGrading_id_s;
create table sam_gradingSummary_t (
   assessmentGradingSummaryId NUMBER(19,0) not null,
   publishedAssessmentId NUMBER(19,0) not null,
   agentId varchar(36) not null,
   totalSubmitted integer,
   totalSubmittedForGrade integer,
   lastSubmittedDate date,
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
   itemId NUMBER(19,0) not null,
   sectionId NUMBER(19,0),
   itemIdString varchar(36),
   sequence integer,
   duration integer,
   triesAllowed integer,
   instruction varchar(4000),
   description varchar(4000),
   typeId varchar(36) not null,
   grade varchar(80),
   score float,
   hint varchar(4000),
   hasRationale varchar(1),
   status integer not null,
   createdBy varchar(36) not null,
   createdDate date not null,
   lastModifiedBy varchar(36) not null,
   lastModifiedDate date not null,
   primary key (itemId)
);
create table sam_publishedItem_t (
   itemId NUMBER(19,0) not null,
   sectionId NUMBER(19,0) not null,
   itemIdString varchar(36),
   sequence integer,
   duration integer,
   triesAllowed integer,
   instruction varchar(4000),
   description varchar(4000),
   typeId varchar(36) not null,
   grade varchar(80),
   score float,
   hint varchar(4000),
   hasRationale varchar(1),
   status integer not null,
   createdBy varchar(36) not null,
   createdDate date not null,
   lastModifiedBy varchar(36) not null,
   lastModifiedDate date not null,
   primary key (itemId)
);
create table sam_publishedItemMetaData_t (
   itemMetaDataId NUMBER(19,0) not null,
   itemId NUMBER(19,0) not null,
   label varchar(255) not null,
   entry varchar(255),
   primary key (itemMetaDataId)
);
create table sam_publishedItemText_t (
   itemTextId NUMBER(19,0) not null,
   itemId NUMBER(19,0) not null,
   sequence integer not null,
   text varchar(4000),
   primary key (itemTextId)
);
create table sam_securedIP_t (
   ipAddressId NUMBER(19,0) not null,
   assessmentId NUMBER(19,0) not null,
   hostname varchar(255),
   ipAddress varchar(255),
   primary key (ipAddressId)
);
create table sam_section_t (
   sectionId NUMBER(19,0) not null,
   assessmentId NUMBER(19,0) not null,
   duration integer,
   sequence integer,
   title varchar(255),
   description varchar(4000),
   typeId integer,
   status integer not null,
   createdBy varchar(36) not null,
   createdDate date not null,
   lastModifiedBy varchar(36) not null,
   lastModifiedDate date not null,
   primary key (sectionId)
);
create table sam_publishedItemFeedback_t (
   itemFeedbackId NUMBER(19,0) not null,
   itemId NUMBER(19,0) not null,
   typeId varchar(36) not null,
   text varchar(4000),
   primary key (itemFeedbackId)
);
create table sam_media_t (
   mediaId NUMBER(19,0) not null,
   itemGradingId NUMBER(19,0),
   media RAW(255),
   fileSize integer,
   mimeType varchar(80),
   description varchar(4000),
   location varchar(255),
   filename varchar(255),
   isLink integer,
   isHtmlInline integer,
   status integer,
   createdBy varchar(36),
   createdDate date,
   lastModifiedBy varchar(36),
   lastModifiedDate date,
   primary key (mediaId)
);
create table sam_itemGrading_t (
   itemGradingId NUMBER(19,0) not null,
   assessmentGradingId NUMBER(19,0) not null,
   publishedItemId NUMBER(19,0) not null,
   publishedItemTextId NUMBER(19,0) not null,
   agentId varchar(36) not null,
   submittedDate date not null,
   publishedAnswerId NUMBER(19,0),
   rationale varchar(4000),
   answerText varchar(4000),
   autoScore float,
   overrideScore float,
   Comments varchar(4000),
   gradedBy varchar(36),
   gradedDate date,
   review integer,
   primary key (itemGradingId)
);
create table sam_questionPool_t (
   questionPoolId NUMBER(19,0) not null,
   title varchar(255),
   description varchar(255),
   parentPoolId integer,
   ownerId varchar(255),
   organizationName varchar(255),
   dateCreated date,
   lastModifiedDate date,
   lastModifiedBy varchar(255),
   defaultAccessTypeId integer,
   objective varchar(255),
   keywords varchar(255),
   rubric varchar(4000),
   typeId integer,
   intellectualPropertyId integer,
   primary key (questionPoolId)
);
create table sam_publishedEvaluation_t (
   assessmentId NUMBER(19,0) not null,
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
   answerFeedbackId NUMBER(19,0) not null,
   answerId NUMBER(19,0) not null,
   typeId varchar(36),
   text varchar(4000),
   primary key (answerFeedbackId)
);
create table sam_publishedFeedback_t (
   assessmentId NUMBER(19,0) not null,
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
   questionPoolId NUMBER(19,0) not null,
   itemId VARCHAR2(255) not null,
   primary key (questionPoolId, itemId)
);
create table sam_publishedSecuredIP_t (
   ipAddressId NUMBER(19,0) not null,
   assessmentId NUMBER(19,0) not null,
   hostname varchar(255),
   ipAddress varchar(255),
   primary key (ipAddressId)
);
create table sam_functionData_t (
   functionId NUMBER(19,0) not null,
   referenceName varchar(255) not null,
   displayName varchar(255),
   description varchar(4000),
   functionTypeId varchar(4000),
   primary key (functionId)
);
create table sam_assessmentBase_t (
   id NUMBER(19,0) not null,
   isTemplate VARCHAR2(255) not null,
   parentId integer,
   title varchar(255),
   description varchar(4000),
   comments varchar(4000),
   typeId varchar(36),
   instructorNotification integer,
   testeeNotification integer,
   multipartAllowed integer,
   status integer not null,
   createdBy varchar(36) not null,
   createdDate date not null,
   lastModifiedBy varchar(36) not null,
   lastModifiedDate date not null,
   assessmentTemplateId NUMBER(19,0),
   primary key (id)
);
create table sam_publishedAssessment_t (
   id NUMBER(19,0) not null,
   assessmentId NUMBER(19,0) not null,
   title varchar(255),
   description varchar(4000),
   comments varchar(255),
   typeId varchar(36),
   instructorNotification integer,
   testeeNotification integer,
   multipartAllowed integer,
   status integer not null,
   createdBy varchar(36) not null,
   createdDate date not null,
   lastModifiedBy varchar(36) not null,
   lastModifiedDate date not null,
   primary key (id)
);
create table sam_itemText_t (
   itemTextId NUMBER(19,0) not null,
   itemId NUMBER(19,0) not null,
   sequence integer not null,
   text varchar(4000),
   primary key (itemTextId)
);
create table sam_assessFeedback_t (
   assessmentId NUMBER(19,0) not null,
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
   typeId NUMBER(19,0) not null,
   authority varchar(255),
   domain varchar(255),
   keyword varchar(255),
   description varchar(4000),
   status integer not null,
   createdBy varchar(36) not null,
   createdDate date not null,
   lastModifiedBy varchar(36) not null,
   lastModifiedDate date not null,
   primary key (typeId)
);
create table sam_publishedAccessControl_t (
   assessmentId NUMBER(19,0) not null,
   unlimitedSubmissions integer,
   submissionsAllowed integer,
   submissionsSaved integer,
   assessmentFormat integer,
   bookMarkingItem integer,
   timeLimit integer,
   timedAssessment integer,
   retryAllowed integer,
   lateHandling integer,
   startDate date,
   dueDate date,
   scoreDate date,
   feedbackDate date,
   retractDate date,
   autoSubmit integer,
   itemNavigation integer,
   itemNumbering integer,
   submissionMessage varchar(4000),
   releaseTo varchar(255),
   username varchar(255),
   password varchar(255),
   finalPageUrl varchar(1023),
   primary key (assessmentId)
);
create table sam_publishedAnswerFeedback_t (
   answerFeedbackId NUMBER(19,0) not null,
   answerId NUMBER(19,0) not null,
   typeId varchar(36),
   text varchar(4000),
   primary key (answerFeedbackId)
);
create table sam_assessEvaluation_t (
   assessmentId NUMBER(19,0) not null,
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
   answerId NUMBER(19,0) not null,
   itemTextId NUMBER(19,0) not null,
   itemId NUMBER(19,0) not null,
   text varchar(4000),
   sequence integer not null,
   label varchar(20),
   isCorrect varchar(1),
   grade varchar(80),
   score float,
   primary key (answerId)
);
create table sam_authzData_t (
   id NUMBER(19,0) not null,
   lockId NUMBER(10,0) not null,
   agentId VARCHAR2(255) not null,
   functionId VARCHAR2(255) not null,
   qualifierId VARCHAR2(255) not null,
   effectiveDate date,
   expirationDate date,
   lastModifiedBy varchar(36) not null,
   lastModifiedDate date not null,
   isExplicit integer,
   primary key (id),
    unique (agentId, functionId, qualifierId)
);
create table sam_qualifierData_t (
   qualifierId NUMBER(19,0) not null,
   referenceName varchar(255) not null,
   displayName varchar(255),
   description varchar(4000),
   qualifierTypeId varchar(4000),
   primary key (qualifierId)
);
create table sam_assessmentGrading_t (
   assessmentGradingId NUMBER(19,0) not null,
   publishedAssessmentId NUMBER(19,0) not null,
   agentId varchar(36) not null,
   submittedDate date not null,
   isLate varchar(1) not null,
   forGrade integer not null,
   totalAutoScore float,
   totalOverrideScore float,
   finalScore float,
   comments varchar(4000),
   gradedBy varchar(36),
   gradedDate date,
   status integer not null,
   attemptDate date,
   timeElapsed integer,
   primary key (assessmentGradingId)
);
create table sam_publishedSection_t (
   sectionId NUMBER(19,0) not null,
   assessmentId NUMBER(19,0) not null,
   duration integer,
   sequence integer,
   title varchar(255),
   description varchar(4000),
   typeId integer not null,
   status integer not null,
   createdBy varchar(36) not null,
   createdDate date not null,
   lastModifiedBy varchar(36) not null,
   lastModifiedDate date not null,
   primary key (sectionId)
);
create table sam_publishedMetaData_t (
   assessmentMetaDataId NUMBER(19,0) not null,
   assessmentId NUMBER(19,0) not null,
   label varchar(255) not null,
   entry varchar(255),
   primary key (assessmentMetaDataId)
);
create table sam_itemFeedback_t (
   itemFeedbackId NUMBER(19,0) not null,
   itemId NUMBER(19,0) not null,
   typeId varchar(36) not null,
   text varchar(4000),
   primary key (itemFeedbackId)
);
create table sam_itemMetaData_t (
   itemMetaDataId NUMBER(19,0) not null,
   itemId NUMBER(19,0) not null,
   label varchar(255) not null,
   entry varchar(255),
   primary key (itemMetaDataId)
);
create table sam_assessAccessControl_t (
   assessmentId NUMBER(19,0) not null,
   submissionsAllowed integer,
   unlimitedSubmissions integer,
   submissionsSaved integer,
   assessmentFormat integer,
   bookMarkingItem integer,
   timeLimit integer,
   timedAssessment integer,
   retryAllowed integer,
   lateHandling integer,
   startDate date,
   dueDate date,
   scoreDate date,
   feedbackDate date,
   retractDate date,
   autoSubmit integer,
   itemNavigation integer,
   itemNumbering integer,
   submissionMessage varchar(4000),
   releaseTo varchar(255),
   username varchar(255),
   password varchar(255),
   finalPageUrl varchar(1023),
   primary key (assessmentId)
);
create table sam_answer_t (
   answerId NUMBER(19,0) not null,
   itemTextId NUMBER(19,0) not null,
   itemId NUMBER(19,0) not null,
   text varchar(4000),
   sequence integer not null,
   label varchar(20),
   isCorrect varchar(1),
   grade varchar(80),
   score float,
   primary key (answerId)
);
create table sam_questionPoolAccess_t (
   questionPoolId NUMBER(19,0) not null,
   agentId VARCHAR2(255) not null,
   accessTypeId NUMBER(19,0) not null,
   primary key (questionPoolId, agentId, accessTypeId)
);
create table sam_assessMetaData_t (
   assessmentMetaDataId NUMBER(19,0) not null,
   assessmentId NUMBER(19,0) not null,
   label varchar(255) not null,
   entry varchar(255),
   primary key (assessmentMetaDataId)
);
alter table sam_gradingSummary_t add constraint FK5F622E077BD8188B foreign key (publishedAssessmentId) references sam_publishedAssessment_t;
alter table sam_item_t add constraint FK80FB06A8D78B62E0 foreign key (sectionId) references sam_section_t;
alter table sam_publishedItem_t add constraint FK373D6576D78B62E0 foreign key (sectionId) references sam_publishedSection_t;
alter table sam_publishedItemMetaData_t add constraint FKBB428825B9BF0B8E foreign key (itemId) references sam_publishedItem_t;
alter table sam_publishedItemText_t add constraint FKD3215703B9BF0B8E foreign key (itemId) references sam_publishedItem_t;
alter table sam_securedIP_t add constraint FKAFEA10493660977D foreign key (assessmentId) references sam_assessmentBase_t;
alter table sam_section_t add constraint FKF358113A3660977D foreign key (assessmentId) references sam_assessmentBase_t;
alter table sam_publishedItemFeedback_t add constraint FKC8EB4EDBB9BF0B8E foreign key (itemId) references sam_publishedItem_t;
alter table sam_media_t add constraint FK585626792ED9513C foreign key (itemGradingId) references sam_itemGrading_t;
alter table sam_itemGrading_t add constraint FKF7272F9633FEB0A9 foreign key (publishedItemTextId) references sam_publishedItemText_t;
alter table sam_itemGrading_t add constraint FKF7272F964AF7EF1C foreign key (publishedItemId) references sam_publishedItem_t;
alter table sam_itemGrading_t add constraint FKF7272F96A9AE4A6D foreign key (assessmentGradingId) references sam_assessmentGrading_t;
alter table sam_itemGrading_t add constraint FKF7272F968C78987 foreign key (publishedAnswerId) references sam_publishedAnswer_t;
alter table sam_publishedEvaluation_t add constraint FKBEC6F0DF3660977D foreign key (assessmentId) references sam_publishedAssessment_t;
alter table sam_answerFeedback_t add constraint FKFB0830F8E2395179 foreign key (answerId) references sam_answer_t;
alter table sam_publishedFeedback_t add constraint FK81CC3A683660977D foreign key (assessmentId) references sam_publishedAssessment_t;
alter table sam_publishedSecuredIP_t add constraint FK59F8C23B3660977D foreign key (assessmentId) references sam_publishedAssessment_t;
alter table sam_publishedAssessment_t add constraint FKDCE056E53660977D foreign key (assessmentId) references sam_assessmentBase_t;
alter table sam_itemText_t add constraint FKD5E70F35B9BF0B8E foreign key (itemId) references sam_item_t;
alter table sam_assessFeedback_t add constraint FKF7B68D1E3660977D foreign key (assessmentId) references sam_assessmentBase_t;
alter table sam_publishedAccessControl_t add constraint FK552FA5A03660977D foreign key (assessmentId) references sam_publishedAssessment_t;
alter table sam_publishedAnswerFeedback_t add constraint FK1074E646E2395179 foreign key (answerId) references sam_publishedAnswer_t;
alter table sam_assessEvaluation_t add constraint FK63676E153660977D foreign key (assessmentId) references sam_assessmentBase_t;
alter table sam_publishedAnswer_t add constraint FKF97E97E1781E441B foreign key (itemTextId) references sam_publishedItemText_t;
alter table sam_publishedAnswer_t add constraint FKF97E97E1B9BF0B8E foreign key (itemId) references sam_publishedItem_t;
create index sam_authz_functionId_idx on sam_authzData_t (functionId);
create index sam_authz_qualifierId_idx on sam_authzData_t (qualifierId);
create index sam_authz_agentId_idx on sam_authzData_t (agentId);
alter table sam_assessmentGrading_t add constraint FK48D0F0C77BD8188B foreign key (publishedAssessmentId) references sam_publishedAssessment_t;
alter table sam_publishedSection_t add constraint FKA8EE97AC3660977D foreign key (assessmentId) references sam_publishedAssessment_t;
alter table sam_publishedMetaData_t add constraint FK742373B23660977D foreign key (assessmentId) references sam_publishedAssessment_t;
alter table sam_itemFeedback_t add constraint FK4D59E0DB9BF0B8E foreign key (itemId) references sam_item_t;
alter table sam_itemMetaData_t add constraint FKF72CD757B9BF0B8E foreign key (itemId) references sam_item_t;
alter table sam_assessAccessControl_t add constraint FK257A94AA3660977D foreign key (assessmentId) references sam_assessmentBase_t;
alter table sam_answer_t add constraint FKCA58B493B9BF0B8E foreign key (itemId) references sam_item_t;
alter table sam_answer_t add constraint FKCA58B493781E441B foreign key (itemTextId) references sam_itemText_t;
alter table sam_assessMetaData_t add constraint FKEA0DC6683660977D foreign key (assessmentId) references sam_assessmentBase_t;
create sequence sam_type_id_s;
create sequence sam_functionId_s;
create sequence sam_securedIP_id_s;
create sequence sam_item_id_s;
create sequence sam_publishedSecuredIP_id_s;
create sequence sam_gradingSummary_id_s;
create sequence sam_pubItem_id_s;
create sequence sam_itemFeedback_id_s;
create sequence sam_section_id_s;
create sequence sam_pubAnswerFeedback_id_s;
create sequence sam_pubAnswer_id_s;
create sequence sam_assessmentGrading_id_s;
create sequence sam_publishedMetaData_id_s;
create sequence sam_publishedSection_id_s;
create sequence sam_media_id_s;
create sequence sam_assessmentBase_id_s;
create sequence sam_questionPool_id_s;
create sequence sam_publishedAssessment_id_s;
create sequence sam_assessMetaData_id_s;
create sequence sam_answer_id_s;
create sequence sam_itemMetaData_id_s;
create sequence sam_AuthzData_s;
create sequence sam_pubItemMetaData_id_s;
create sequence sam_answerFeedback_id_s;
create sequence sam_pubItemFeedback_id_s;
create sequence sam_pubItemText_id_s;
create sequence sam_itemText_id_s;
create sequence sam_itemGrading_id_s;
