import os
import sys
from SOAPpy import WSDL

username = "admin"
password = "admin"
script_url = "http://localhost:8080/sakai-axis/SakaiScript.jws?wsdl"

scriptsoap = WSDL.SOAPProxy(script_url)
sessionid = scriptsoap.login(username, password)

usersfile = open("nobel_laureates.txt")
lines = usersfile.readlines()

for line in lines:
    line = line.rstrip("\n")
    parts = line.split(",")
    newuser = scriptsoap.addNewUser(sessionid, parts[0], parts[1], parts[2], "", "", "password" )
    print str(newuser)