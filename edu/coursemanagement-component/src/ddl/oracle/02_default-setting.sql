insert into CM_SESSIONTYPE_T ("SESSIONTYPEID", "AUTHORITY", "DOMAIN", "KEYWORD",
    "DISPLAYNAME", "DESCRIPTION", "UUID", 
    "STATUS" ,"CREATEDBY" ,"CREATEDDATE" ,"LASTMODIFIEDBY" , "LASTMODIFIEDDATE" )
    VALUES (CM_SESSIONTYPE_ID_S.nextVal, 'org.sakaiproject' ,'coursemgmt' ,'term.unknown' ,
    'Unknown', NULL, '*uuid_session_1',
    1, 'site', SYSDATE , 'site' ,SYSDATE);
insert into CM_SESSIONTYPE_T ("SESSIONTYPEID", "AUTHORITY", "DOMAIN", "KEYWORD",
    "DISPLAYNAME", "DESCRIPTION", "UUID", 
    "STATUS" ,"CREATEDBY" ,"CREATEDDATE" ,"LASTMODIFIEDBY" , "LASTMODIFIEDDATE" )
    VALUES (CM_SESSIONTYPE_ID_S.nextVal, 'org.sakaiproject' ,'coursemgmt' ,'term.spring' ,
    'Unknown', NULL, '*uuid_session_2',
    1, 'site', SYSDATE , 'site' ,SYSDATE);
insert into CM_SESSIONTYPE_T ("SESSIONTYPEID", "AUTHORITY", "DOMAIN", "KEYWORD",
    "DISPLAYNAME", "DESCRIPTION", "UUID", 
    "STATUS" ,"CREATEDBY" ,"CREATEDDATE" ,"LASTMODIFIEDBY" , "LASTMODIFIEDDATE" )
    VALUES (CM_SESSIONTYPE_ID_S.nextVal, 'org.sakaiproject' ,'coursemgmt' ,'term.summer' ,
    'Unknown', NULL, 'uuuid_session_3',
    1, 'site', SYSDATE , 'site' ,SYSDATE);
insert into CM_SESSIONTYPE_T ("SESSIONTYPEID", "AUTHORITY", "DOMAIN", "KEYWORD",
    "DISPLAYNAME", "DESCRIPTION", "UUID", 
    "STATUS" ,"CREATEDBY" ,"CREATEDDATE" ,"LASTMODIFIEDBY" , "LASTMODIFIEDDATE" )
    VALUES (CM_SESSIONTYPE_ID_S.nextVal, 'org.sakaiproject' ,'coursemgmt' ,'term.fall' ,
    'Unknown', NULL, 'uuuid_session_4',
    1, 'site', SYSDATE , 'site' ,SYSDATE);
insert into CM_SESSIONTYPE_T ("SESSIONTYPEID", "AUTHORITY", "DOMAIN", "KEYWORD",
    "DISPLAYNAME", "DESCRIPTION", "UUID", 
    "STATUS" ,"CREATEDBY" ,"CREATEDDATE" ,"LASTMODIFIEDBY" , "LASTMODIFIEDDATE" )
    VALUES (CM_SESSIONTYPE_ID_S.nextVal, 'org.sakaiproject' ,'coursemgmt' ,'term.winter' ,
    'Unknown', NULL, 'uuuid_session_5',
    1, 'site', SYSDATE , 'site' ,SYSDATE);
insert into CM_SESSIONTYPE_T ("SESSIONTYPEID", "AUTHORITY", "DOMAIN", "KEYWORD",
    "DISPLAYNAME", "DESCRIPTION", "UUID", 
    "STATUS" ,"CREATEDBY" ,"CREATEDDATE" ,"LASTMODIFIEDBY" , "LASTMODIFIEDDATE" )
    VALUES (CM_SESSIONTYPE_ID_S.nextVal, 'org.sakaiproject' ,'coursemgmt' ,'term.first_quarter' ,
    'Unknown', NULL, 'uuuid_session_6',
    1, 'site', SYSDATE , 'site' ,SYSDATE);
insert into CM_SESSIONTYPE_T ("SESSIONTYPEID", "AUTHORITY", "DOMAIN", "KEYWORD",
    "DISPLAYNAME", "DESCRIPTION", "UUID", 
    "STATUS" ,"CREATEDBY" ,"CREATEDDATE" ,"LASTMODIFIEDBY" , "LASTMODIFIEDDATE" )
    VALUES (CM_SESSIONTYPE_ID_S.nextVal, 'org.sakaiproject' ,'coursemgmt' ,'term.second_quarter' ,
    'Unknown', NULL, 'uuuid_session_7',
    1, 'site', SYSDATE , 'site' ,SYSDATE);
insert into CM_SESSIONTYPE_T ("SESSIONTYPEID", "AUTHORITY", "DOMAIN", "KEYWORD",
    "DISPLAYNAME", "DESCRIPTION", "UUID", 
    "STATUS" ,"CREATEDBY" ,"CREATEDDATE" ,"LASTMODIFIEDBY" , "LASTMODIFIEDDATE" )
    VALUES (CM_SESSIONTYPE_ID_S.nextVal, 'org.sakaiproject' ,'coursemgmt' ,'term.third_quarter' ,
    'Unknown', NULL, 'uuuid_session_8',
    1, 'site', SYSDATE , 'site' ,SYSDATE);
insert into CM_SESSIONTYPE_T ("SESSIONTYPEID", "AUTHORITY", "DOMAIN", "KEYWORD",
    "DISPLAYNAME", "DESCRIPTION", "UUID", 
    "STATUS" ,"CREATEDBY" ,"CREATEDDATE" ,"LASTMODIFIEDBY" , "LASTMODIFIEDDATE" )
    VALUES (CM_SESSIONTYPE_ID_S.nextVal, 'org.sakaiproject' ,'coursemgmt' ,'term.forth_quarter' ,
    'Unknown', NULL, 'uuuid_session_9',
    1, 'site', SYSDATE , 'site' ,SYSDATE);

commit;