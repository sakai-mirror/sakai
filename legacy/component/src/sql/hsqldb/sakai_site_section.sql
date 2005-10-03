-- Site related tables added in Sakai 2.1
-----------------------------------------------------------------------------
-- SAKAI_SITE_SECTION
-----------------------------------------------------------------------------

CREATE TABLE SAKAI_SITE_SECTION (
       SECTION_ID           VARCHAR (99) NOT NULL,
       SITE_ID              VARCHAR (99) NOT NULL,
       TITLE                VARCHAR (99) NULL,
       DESCRIPTION          LONGVARCHAR NULL,
       PRIMARY KEY (SECTION_ID)
);

ALTER TABLE SAKAI_SITE_SECTION
       ADD FOREIGN KEY (SITE_ID)
                             REFERENCES SAKAI_SITE (SITE_ID);

CREATE INDEX IE_SAKAI_SITE_SECT_SITE ON SAKAI_SITE_SECTION
(
       SITE_ID                       ASC
);

-----------------------------------------------------------------------------
-- SAKAI_SITE_SECTION_PROPERTY
-----------------------------------------------------------------------------

CREATE TABLE SAKAI_SITE_SECTION_PROPERTY (
       SITE_ID              VARCHAR (99) NOT NULL,
       SECTION_ID           VARCHAR (99) NOT NULL,
       NAME                 VARCHAR (99) NOT NULL,
       VALUE                LONGVARCHAR NULL,
       PRIMARY KEY (SECTION_ID, NAME)
);


ALTER TABLE SAKAI_SITE_SECTION_PROPERTY
       ADD  FOREIGN KEY (SECTION_ID)
                             REFERENCES SAKAI_SITE_SECTION (SECTION_ID);

ALTER TABLE SAKAI_SITE_SECTION_PROPERTY
       ADD  FOREIGN KEY (SITE_ID)
                             REFERENCES SAKAI_SITE (SITE_ID);

CREATE INDEX IE_SAKAI_SITE_SECT_PROP_SITE ON SAKAI_SITE_SECTION_PROPERTY
(
       SITE_ID                       ASC
);
