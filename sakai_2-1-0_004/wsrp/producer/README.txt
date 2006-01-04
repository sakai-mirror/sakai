Overview
---------
Sakai's WSRP (Web Services for Remote Portlets) producer interface allows individual tools to be placed
in a WSRP consumer portal (such as uPortal) simply by pointing to a Sakai deployment. 

Sakai's WSRP producer interface allows individual tools to be placed in a WSRP consumer portal (such as 
uPortal) simply by pointing to a Sakai deployment. These tools can be rendered either in a separate 
configurable context or in any given Sakai site context. See “Choosing the portlet handle” for more details
on how to choose the tool context.

Most tools should be WSRP agnostic in the way they produce markup, as long as they are using the recommended
APIs and practices to generate markup. For velocity templates for instance, it means using the macros correctly when 
encoding URLs (see the TOOL_DEVELOPER_GUIDE.txt for details). The Sakai Kernel will support Generic APIs for
creating URLs, which when correctly used will produce WSRP friendly markup as well as markup for other portals.

BUILD/DEPLOY
-------------
Building the WSRP producer code is simple. You download the code and run maven. If you get an error
about war:war-resources goal not being supported, please proceed to download the maven-war-plugin-1.6.1 and
notify the author (vgoenka@sungardsct.com) that the automatic plugin download/install isn't working!

Sakai WSRP Producer deploys as a war file, so the maven deploy target should take of that as well.

Choosing the Portlet Handle
----------------------------
Sakai Tool instances are referred in WSRP using “portletHandle”. Sakai tool instances that are placed within a 
specific site have a unique too placement-id that can be retrieved by looking at the Tool URL. For instance, 
if you navigate to a specific tool within a given site using the Sakai default portal and view the URL of the 
tool, you may see something like:
	http://localhost:8080/portal/tool/92757208-f347-41a9-00b9-28ea26fdcba5?panel=Main

The tool placement id for this tool is 92757208-f347-41a9-00b9-28ea26fdcba5. This value can be used as the portlet 
handle in the WSRP Consumer to access this site specific instance of tool over WSRP.

Note however that this will not work with uPortal 2.x (I haven’t tried uPortal 3.x yet!) because of an unresolved 
issue in uPortal’s WSRP consumer implementation. In a nutshell, Sakai does not advertise all instances of all 
tools in the getServiceDescription method, because that would be too overwhelming. Sakai only advertises the 
“context-less” portlets as described below and expects a different (currently out-of-band) mechanism for configuring 
these site-specific instances. uPortal’s WSRP consumer however will not issue a request for a portlet that is not 
advertised in the getServiceDescription.

I will be very interested to know what behavior folks are seeing in using this with other Portals.

A tool can also be accessed without a specific site context by using the tool identifier as the portlet handle. 
The tool identifiers for various tools can be seen by visiting the mercury portal at http://localhost:8080/mercury.
For instance, the announcement tool has a tool identifier of “sakai.announcements”. When accessed in this manner, 
a unique sakai placement is created for the user’s session and the tool is placed in a configurable context. 
The context defaults to “mercury” so that it can be accessed through the mercury development portal as well, 
but it can be configured by editing the following file in wsrp/producer module.
File: src/webapp/WEB-INF/classes/WSRPServices.properties
Property name: sakai.portlet.context.default

TEST Drive (Run)
----------------
In order to test drive the Sakai WSRP producer, you need a WSRP Consumer portal. Unless you are already familiar
with one (such as Oracle's Portal or IBM WebSphere), it is perhaps quickest to get a quickstart version of 
uPortal 2.4.2 or higher (2.5.0 is preferred, but no quickstart is available as of this writing). See instructions
to get uPortal running. The only important thing to be aware of is a potential port conflict, if you are running
Sakai on port 8080. Consider changing uPortal to run on a different port.

Once you have uPortal running, login as one of the users pre-created. You may choose (admin,admin) or one of 
the following:

demo, demo
student, student
faculty, faculty
staff, staff
developer, developer

If you login with a user-id that exists in Sakai, the user's Sakai privileges will be used to determine what
they can do with the tools (so there is authorization, at this point in the development we just trust uPortal
to assert the user's identity -- more on AuthN and AuthZ later). So, if you login as (admin, admin) you will
probably be able to do more with the tools assuming admin is a privileged account on Sakai. 

Once in uPortal, you need to publish a Sakai Tool as a WSRP portlet using the uPortal Channel Manager. 

The URLs for Sakai WSRP producer that you will need to use are as follows (replace sakai-host with your hostname).

1. Portlet Management Interface URL
       http://sakai-host:8080/sakai-wsrp-producer/wsrp4j/WSRPPortletManagementService 

2. Registration Interface URL  
       http://sakai-host:8080/sakai-wsrp-producer/wsrp4j/WSRPRegistrationService 
 
3. Portlet Handle  
		This should be the tool id for the tool you want to publish.  See the section above 
		“Choosing the portlet Handle” for more information. 

4.  Markup Interface URL  
       http://sakai-host:8080/sakai-wsrp-producer/wsrp4j/WSRPBaseService 
 
5. Service Description Interface URL  
       http://sakai-host:8080/sakai-wsrp-producer/wsrp4j/WSRPServiceDescriptionService 

WSRP isn't the fastest protocol, so be sure to use a generous timeout (such as 60 seconds) to allow initial
setup including consumer registration that may happen in-band during channel rendering!

Once you have successfully published a WSRP portlet, add it to your channel layout and see it in action. 

BUGS
-----
If you find a bug, please be aware that it may also be in the WSRP Consumer (uPortal 2.4.2 WSRP Consumer has
several noted bugs, for instance). It will help to setup a TCP/IP monitor to watch the traffic, so you can either 
make an intelligent diagnosis yourself or provide valuable input to the bug tracking system. If you use the 
MyEclipse IDE, it includes a TCP/IP monitor that is accessible from 
	Window --> Show View --> MyEclipse Enterprise WorkBench --> TCP/IP Monitor

I setup my TCP/IP monitor to listen on say port 80 and point my WSRP Consumer to it (All 4 URLs listed above).
The TCP/IP monitor simply forwards traffic to Sakai listening on port 8080. Use appropriate ports for your 
setup.

Authentication/Authorization
------------------------------
The current code trusts the WSRP consumer for having authenticated the end-user and does not require a separate 
end-user authentication. However, the user-id of the end user should be the same in Sakai and the WSRP Consumer 
(such as uPortal).

The WSRP consumer itself is currently allowed/denied access based on its IP address that can be configured in 
the web.xml file. 

HTTP Basic Authentication for the WSRP consumer can be enabled by simply un-commenting the <security-constraint>
element in web.xml and editing the username/passwords in trusted-consumers.xml. Since we aren't authenticating 
end-users, it doesn't necessarily make sense to use a Sakai user's account for WSRP consumer authentication.
Be aware though, that since uPortal doesn’t yet support configuring Basic Auth credentials for accessing a WSRP 
producer, it will not be usable right away with uPortal. Other WSRP Consumers may support HTTP Basic Auth.

End-user authentication using proxy-CAS and/or WS-Security have been discussed but there is no timetable set 
for their implementation.


What's Working, what's Not!
----------------------------

The current state of development allows basic tasks as listed below. 

1. register -- Online registration of consumer 

2. deregister -- Online de-registration of consumer 

3. initCookie -- Cookies are required and set per user (not per group).

4. getServiceDescription -- All Sakai tools that are registered with Sakai are returned. Sakai tools
   currently don't have a knowledge of WSRP, therefore only the 'wsrp:view' mode is supported with 
   'wsrp:normal' window state. The portlet handles returned are the same as the tool's "id". 

5. getMarkup -- Works well with most Sakai tools. Some tools that make use of "iframes" or Javascript 
   will not render well. Currently Sakai will serve the markup anyway. This needs to be fixed by marking
   such tools that don't work well with WSRP so that 'getServiceDescription' won't report them. User 
   context passed by consumer is used to create a context in Sakai using the user-id passed by the consumer. 
   Again, there is no authentication of this information yet. Currently Sakai does not return the cacheability
   information for the consumer to be able to cache portlet markup. 

6. performBlockingInteraction -- Ditto as getMarkup. 

The only data that is currently persisted is the consumer resigtration. List of supported Tools (portlets) is 
fetched from Sakai and no separate persistence is required. Consumer configured portlets are not supported yet.
The persistence of registration information is done using the default mechanism that is included with the
WSRP4J code, which happens to be a Castor based XML file persistence. The persistence files are written in 
WEB-INF/persistence directory, which will be overwritten if you re-deploy sakai-wsrp-producer, losing all
consumer registrations.
 
Data persisted by individual tools are not affected. Since portlet cloning is not currently implemented, the 
only place where this seems painful is if you re-deploy Sakai's WSRP producer, consumer will need to re-register!

