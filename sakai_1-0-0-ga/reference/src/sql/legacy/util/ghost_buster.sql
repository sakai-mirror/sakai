-- find all the presence records still around from closed sessions
select * from CHEF_PRESENCE where SESSION_ID not in
     (select SESSION_ID from CHEF_SESSION where SESSION_START = SESSION_END);

-- find all sessions still open from closed servers
select session_server, session_id, session_user, session_ip, to_char(session_start,'YYYY-MM-DD HH24:MI:SS')
	from CHEF_SESSION where SESSION_START = SESSION_END
	and SESSION_SERVER not in
		(select SERVER_ID from SAKAI_CLUSTER);

-- delete all the presence records still around from closed sessions
delete from CHEF_PRESENCE where SESSION_ID not in
	(select SESSION_ID from CHEF_SESSION where SESSION_START = SESSION_END);
