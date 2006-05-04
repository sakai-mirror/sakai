-----------------------------------------------------------------------------
-- CALENDAR_CALENDAR
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS CALENDAR_CALENDAR;

CREATE TABLE CALENDAR_CALENDAR
(
    CALENDAR_ID VARCHAR (99) NOT NULL,
	NEXT_ID INT,
    XML LONGTEXT
);

CREATE UNIQUE INDEX CALENDAR_CALENDAR_INDEX ON CALENDAR_CALENDAR
(
	CALENDAR_ID
);

-----------------------------------------------------------------------------
-- CALENDAR_EVENT
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS CALENDAR_EVENT;

CREATE TABLE CALENDAR_EVENT
(
    CALENDAR_ID VARCHAR (99) NOT NULL,
	EVENT_ID VARCHAR (32) NOT NULL,
	EVENT_START DATETIME NOT NULL,
	EVENT_END DATETIME NOT NULL,
    XML LONGTEXT
);

CREATE INDEX CALENDAR_EVENT_INDEX ON CALENDAR_EVENT
(
	CALENDAR_ID
);

DESCRIBE CALENDAR_CALENDAR;
DESCRIBE CALENDAR_EVENT;


