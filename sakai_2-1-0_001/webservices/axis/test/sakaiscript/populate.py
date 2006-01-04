import os
import sys
from SOAPpy import WSDL

siteid = "trythemall"
username = "admin"
password = "admin"
script_url = "http://localhost:8080/sakai-axis/SakaiScript.jws?wsdl"

scriptsoap = WSDL.SOAPProxy(script_url)
sessionid = scriptsoap.login(username, password)

usersfile = open("nobel_laureates.txt")
lines = usersfile.readlines()

for index, line in enumerate(lines):
	line = line.rstrip("\n")
	parts = line.split(",")
	
	if (index < 5):
		result = scriptsoap.addMemberToSiteWithRole(sessionid, siteid, parts[0], "maintain")
		print "Adding Instructor " + parts[0] + ": " + result
	else:
		result = scriptsoap.addMemberToSiteWithRole(sessionid, siteid, parts[0], "access")
		print "Adding Student " + parts[0] + ": " + result