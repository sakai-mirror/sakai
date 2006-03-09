-- This is the Oracle Sakai 2.1.1 -> 2.1.2 conversion script
----------------------------------------------------------------------------------------------------------------------------------------
--
-- use this to convert a Sakai database from 2.1.1 to 2.1.2.  Run this before you run your first app server.
-- auto.ddl does not need to be enabled in your app server - this script takes care of all new TABLEs, changed TABLEs, and changed data.
--
----------------------------------------------------------------------------------------------------------------------------------------


-- RWiki
alter table rwikipreference add column prefcontext varchar(255), add column preftype varchar(64);

-- SAM
alter table SAM_PUBLISHEDASSESSMENT_T modify column ASSESSMENTID integer;
alter table SAM_ITEMGRADING_T modify column PUBLISHEDITEMID integer not null;
alter table SAM_ITEMGRADING_T modify column PUBLISHEDITEMTEXTID integer not null;
alter table SAM_ITEMGRADING_T modify column PUBLISHEDANSWERID integer;

drop index FKB68E6756D4927;
alter table SAM_ITEMGRADING_T drop constraint FKB68E6756D4927;

drop index FKB68E6756A75F9029;
alter table SAM_ITEMGRADING_T drop constraint FKB68E6756A75F9029;

drop index FKB68E6756C42AA2BC;
alter table SAM_ITEMGRADING_T drop constraint FKB68E6756C42AA2BC;

drop index FKB2E48A65C07F835D;
alter table SAM_PUBLISHEDASSESSMENT_T drop constraint FKB2E48A65C07F835D;


-- OSP
alter table osp_structured_artifact_def add schema_hash varchar(255);
