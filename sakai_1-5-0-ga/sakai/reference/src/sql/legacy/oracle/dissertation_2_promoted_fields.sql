-- dissertation_2_promoted_fields.sql
-- promote fields from xml long and create indexes supporting new storage methods
-- 10/31/04

-----------------------------------------------------------------------------
-- DISSERTATION_DISSERTATION
-----------------------------------------------------------------------------

ALTER TABLE DISSERTATION_DISSERTATION
	ADD (SITE varchar2(100), TYPE varchar2(100));
	ADD ( PRIMARY KEY (DISSERTATION_ID) );

CREATE INDEX IE_DISS_DISS_SITE ON DISSERTATION_DISSERTATION
(
	SITE	ASC
);

CREATE INDEX IE_DISS_DISS_TYPE ON DISSERTATION_DISSERTATION
(
	TYPE ASC
);

CREATE INDEX IE_DISS_DISS_SITE_TYPE ON DISSERTATION_DISSERTATION
(
	SITE	ASC,
	TYPE	ASC
);


-----------------------------------------------------------------------------
-- DISSERTATION_CANDIDATEINFO
-----------------------------------------------------------------------------

ALTER TABLE DISSERTATION_CANDIDATEINFO
	ADD (CHEFID varchar2(100), PARENTSITE varchar2(100));
	ADD ( PRIMARY KEY (INFO_ID) );

CREATE INDEX IE_DISS_INFO_CHEFID ON DISSERTATION_CANDIDATEINFO
(
	CHEFID	ASC

CREATE INDEX IE_DISS_INFO_CHEFID_PARENTSITE ON DISSERTATION_CANDIDATEINFO
(
	CHEFID	ASC,
	PARENTSITE	ASC
);


-----------------------------------------------------------------------------
-- DISSERTATION_PATH
-----------------------------------------------------------------------------

ALTER TABLE DISSERTATION_PATH
	ADD (CANDIDATE varchar2(100), SITE varchar2(100), PARENTSITE varchar2(100), SORTLETTER char, TYPE varchar2(100));
	ADD ( PRIMARY KEY (PATH_ID) );
	

CREATE INDEX IE_DISS_PATH_CANDIDATE ON DISSERTATION_PATH
(
	CANDIDATE	ASC
);

CREATE INDEX IE_DISS_PATH_PARENTSITE ON DISSERTATION_PATH
(
	PARENTSITE	ASC
);

CREATE INDEX IE_DISS_PATH_SITE ON DISSERTATION_PATH
(
	SITE	ASC
);

CREATE INDEX IE_DISS_PATH_SORTLETTER_TYPE ON DISSERTATION_PATH
(
	SORTLETTER	ASC,
	TYPE		ASC
);

CREATE INDEX IE_DISS_PATH_TYPE ON DISSERTATION_PATH
(
	TYPE	ASC
);

COMMIT;





