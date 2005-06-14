-- open servers
select server_id, to_char(update_time,'YYYY-MM-DD HH24:MI:SS') from sakai_cluster order by server_id ASC;

-- open sessions
select session_server, session_user, session_ip, session_id, to_char(session_start,'YYYY-MM-DD HH24:MI:SS') from chef_session where session_start = session_end order by session_server, session_start ASC;

-- presence
select count(*) from chef_presence;
