THESE SCRIPTS ARE NOT READY FOR PRIME-TIME YET, LOOK AT THEM AT YOUR OWN RISK.

IF YOU USE THESE SCRIPTS, PLEASE CHANGE THE SERVER YOU ARE RUNNING
THEM AGAINST.  They are configured to run against the University of Michigan's
load testing cluster, which IS NOT FOR PUBLIC USE.

These scripts are for loadtesting Sakai using the Apache JMeter
loadtesting engine.



Files:

sakai_gateway.jmx - Simple script.  
   Repeatedly load the Sakai gateway ("Welcome") page.  Good for monitoring.
   Can be used again sakai 1.0, 1.5, and 2.0 equally well.

sakai2_browse_all_tools.jmx - For each user, pick a random site (to which they have
   access).  Browse all of the tools in that site.  Equivalent to logging in,
   navigating to a tool site, and clicking on each left-hand page (tool) in
   sequence, then logging out.

sakai2_browse_course_homepages.jmx - For each user, visit the home pages
   of each of the sites to which they have access.  This is equivalent to
   logging in, clicking on each of the site tabs, and logging out.

sakai2_download_iframes_example.jmx - Shows the script explicitly handling
   IFRAMEs - downloads each iframe individually.  For each user, visit the home pages
   of each of the sites to which they have access.  This is equivalent to
   logging in, clicking on each of the site tabs, and logging out.

sakai1_browse_all_tools.jmx - For testing against Sakai 1.0 (and 1.5?).
   For each user, pick a random site (to which they have
   access).  Browse all of the tools in that site.  Equivalent to logging in,
   navigating to a tool site, and clicking on each left-hand page (tool) in
   sequence, then logging out.


fake_usernames.txt
fake_passwords.txt - newline-seperated list of Sakai usernames and passwords
   which the scripts will use.  One username/password combination will be used
   for each iteration of the script.

install_ssl_cert.bat - Install the University of Michigan's SSL certificate.
   Something like this is necessary to test against an SSL-enabled server.

jmeter.properties - Example JMeter configuration to use when loadtesting Sakai.

install_ssl_cert.bat - Example of installing an SSL certificate to loadtest
   against an SSL-enabled server.

umwebCA.pem - Example SSL certificate for University of Michigan.



Notes on how to use these scripts:

* In the root node (Test Plan) of each script there is a "scriptdir" variable.
Change this variable value to be the full path to the jmeter script directory 
(the directory this README.txt is in).

* The "Enterprise Login once only" node is optional.  It allows for the 
script to authenticate somehow against the webserver once per thread.
The example shows University of Michigan's single-sign-on system.  You can 
disable the node if you only need to test Sakai's login, and not some
seperate external login.



General JMeter gotchas:

* Don't try to run a large load of JMeter against SSL.  If you do,
JMeter may gradually increase its memory usage until it uses all available 
memory.  Apparently its a bug in Sun's SSL implementation where SSL sockets
aren't closed properly.  You can see if this is happening by running the 
shell command "netstat -n".  If you see a lot of connections in the "TIME_WAIT"
state, you've got the bug!  The JMeter mailing list recommended another
solution of using "HTTP Request HTTPClient" when creating the script, rather
than the default "HTTP Request", but we're not sure if that works yet.
Observed on WinXP.

* Don't turn on "Functional Test" mode under the root "Test Plan" node!
 Not only does it make JMeter write out huge logs, it accelerates
JMeter's memory consumption and memory leaks unacceptably. Observed on WinXP.

* Don't turn on "Redirect automatically" on HTTP request sampling nodes.
"Follow redirects" is good, but "Redirect automatically" doesn't 
always work properly.  For example, it mysteriously breaks CTools 
Cosign login.  It probably gobbles cookies. Observed on WinXP.

* Don't use the "Stop Test" action on the "Thread Group" node!  This option
is supposed to stop the test when an error occurs.  However, it appears
that if there are many threads it doesn't work right.  It appears
that if there are many threads (say 50), and an error occurs, JMeter hangs
waiting for all the threads to stop.  Use the "Stop Thread" or "Continue"
options instead.  Observed on WinXP.

* In order for JMeter to download IFRAMEs automatically, you must
change jmeter.properties to use the RegexpHTMLParser instead of the 
default parser.  You can comment the default parser and uncomment
the RegexpHTMLParser.  See the example jmeter.properties.  Affects all platforms.

* There is a soft limit to the number of threads that you can run simultaneously.
You may be able to get around this partially by running multiple JMeters, either
on the same machine, or on different machines.  Each JMeter instance can run ~30 threads safely
on WinXP, you may be able to do more on other operating systems.  You can also
try JMeter's remote testing capabilities.  This allows multiple JMeter load-generating servers
to be controlled through one JMeter GUI.

* You may need to increase JMeter's memory allocation.   Affects all platforms.
To increase the memory available to JMeter, its startup script (jmeter.bat
on Windows). 
Change the line:
set HEAP=-Xms256m -Xmx256m
To (for example):
set HEAP=-Xms1024m -Xmx1024m




