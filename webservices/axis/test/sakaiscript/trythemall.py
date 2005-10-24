import os
import sys
from SOAPpy import WSDL

siteid = "trythemall"
username = "admin"
password = "admin"
script_url = "http://localhost:8080/sakai-axis/SakaiScript.jws?wsdl"

scriptsoap = WSDL.SOAPProxy(script_url)
sessionid = scriptsoap.login(username, password)

scriptsoap.addNewSite(sessionid, siteid, "Try All The Tools", "A site containing all the tools that come with the demo", \
                    "All the shipped tools", "", "", True, "access", True, True, "", "test" )
 
toolsfile = open("all_the_tools.txt")
lines = toolsfile.readlines()

for line in lines:
    line = line.rstrip("\n")
    parts = line.split(",")
    result = scriptsoap.addNewPageToSite(sessionid, siteid, parts[0], 0)
    print "AddPage:" + parts[0] + "\t" + result
    result2 = scriptsoap.addNewToolToPage(sessionid, siteid, parts[0], parts[0], parts[1], "")
    print "AddTool:" + parts[1] + "\t" + result2

