Sakai Help
# $Id: README.txt,v 1.2.2.3 2005/03/21 17:32:10 lance.indiana.edu Exp $

INTRODUCTION

Currently Help project directory is outside SAKAI directory. Therefore, in order 
to make help work. Developers need to download help project, build and deploy 
using maven. 

Sakai help system is developed as a TTP tool. It has three parts, service, 
components and tool part. However, it is not registered to Sakai as regular 
tools and it should not be add as a regular TPP tool. 

ENABLE AND DISABLE SAKAI HELP
Sakai help can be disabled if "helpEnabled" property in 
/usr/local/sakai/sakai.properties file is set to "false".
"helpEnable" property is set to "true” by default. 

LOCATION FOR INDEX
Sakai help 'Search' feature is supported by indexing help documents.
The folder location of the indexed documents is defined in property file and set as helpIndex.path=/usr/local/sakai/index   
Write access to this folder is required for 'Search' to work under help tool. 

HELP DOCUMENT FOR EXISTING CHEF-TOOLS
All of the help registration files for existing chef-tools are in 
help/tool/src/webapp/helpReg directory. 
Each tool has its own registration file. Although all registration information 
can be put in one file, it will make trouble shooting very hard. Therefore, 
registration information is categories and put in different xml files.  

For example, announcement tool’s registration file is announcement.xml file. 

Here is an example of the registration file

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN//EN" "http://www.springframework.org/dtd/spring-beans.dtd">

<beans>
   <bean id="announcementOverview" class="org.sakaiproject.component.help.model.ResourceBean">
      <property name="docId"><value>chef-announcements</value></property>
      <property name="name"><value>Announcements Overview</value></property>
      <property name="location"><value>http://kb.indiana.edu/data/apkh.html</value></property>
   </bean>
   
    <bean id="org.sakaiproject.service.help.TableOfContents" 
          class="org.sakaiproject.component.help.model.TableOfContentsBean">
      <property name="name"><value>root</value></property>
      <property name="categories">
         <list>
            <bean id="announcementCategory" class="org.sakaiproject.component.help.model.CategoryBean">
               <property name="name"><value>Announcement</value></property>
               <property name="resources">
                  <list>
                     <ref bean="announcementOverview"/>
                  </list>
               </property>
            </bean>
           </list>
         </property>
       </bean>
</beans>

As the above registration file shows, each resource bean represents a help page. 
The resource bean contains three properties, docId, name, and location. 
When compile this file developer has to make sure that docId is application 
globally unique. The value of name property will show up as the folder name in 
the Table of Content tree. Therefore, it should be unique at least within tool 
level. Location is the URL of the help page. In this example, it is an external 
link that point to a page in IU knowledge base. If other institute would like to 
use their own help document instead of IU KB help files, they can simple change 
the value of the location to point to their own help page. 

Each help registration xml file has its entry in the helpRegistration.xml file. 
The helpRegistration.xml file register all the tool level help registration 
files. For example:

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN//EN" "http://www.springframework.org/dtd/spring-beans.dtd">

<beans>

   <bean id="generalInfo" class="org.sakaiproject.tool.help.RegistrationFile">
      <property name="name"><value>generalInfo.xml</value></property>
   </bean>
</beans>
This file tells help system to read the generalInfo.xml and register the help 
resources in that file. 

If there are more help documents become available and a new category need to be 
created, a new resource registration xml file following the example 
announcement.xml file should be created. Then an entry should be added in 
helpRegistration.xml so that help system will read in the information in the new 
resource registration xml file. 

HOW SHOULD TPP TOOL USE SAKAI HELP

It is very easy to use Sakai Help system for newly developed TPP tools. 
Here we are going to use syllabus tool as example.
 
Prerequisites

Download, build and deploy SAKAI application.
Download, build and deploy Help system. 

Step 1: 
Create resource registration (a spring bean injection) file (tool-help.xml) in 
the webapp directory: tool/src/webapp/helpReg 

syllabus-help.xml

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN//EN" "http://www.springframework.org/dtd/spring-beans.dtd">

<beans>
   <bean id="syllabusOverview" class="org.sakaiproject.component.help.model.ResourceBean">
      <property name="docId">
         <value>syllabus_overview</value>
      </property>
      <property name="name">
         <value>Syllabus Overview</value>
      </property>
      <property name="location">
        <value>http://kb.indiana.edu/data/aqck.html</value>
      </property>
   </bean>
   
   <bean id="org.sakaiproject.service.help.TableOfContents" 
          class="org.sakaiproject.component.help.model.TableOfContentsBean">
      <property name="name"><value>root</value></property>
      <property name="categories">
         <list>
            <bean id="syllabusCategory" class="org.sakaiproject.component.help.model.CategoryBean">
               <property name="name">
                   <value>Syllabus Tool</value>
               </property>
               <property name="resources">
                  <list>
                     <ref bean="syllabusOverview"/>
                  </list>
               </property>
            </bean>
           </list>
         </property>
       </bean>
</beans>

Note:  make sure that the value for 
(<property name="docId"><value> </value></property>) is globally unique through 
Sakai application. 

Make sure that the value for
 (<property name="location">
   <value>http://kb.indiana.edu/data/aqck.html</value>
 </property>)
 is URL that points to the related help page. 

Step 2: 
Add "helpDocId” attribute in <sakai:title_bar /> tag
For example: 
<sakai:title_bar value="Syllabus_Tool" helpDocId="syllabus_overview" />
Note: make sure that the value for helpDocId is the same as the docId of the 
help document in spring bean injection file. 

Step 3:

Change the registration file in tool/src/reg directory by adding a "helpUrl" 
attribute. Make sure the value of this attribute reflect the location of the 
spring bean injection xml file. 

For example: sakai.syllabus.xml file 

<?xml version="1.0"?>

<registration>
  
  <function name="syllabus.read" />
  <function name="syllabus.add" />
  <function name="syllabus.update" />
  <function name="syllabus.delete" />
  <function name="syllabusItem.create" />
  <function name="syllabusItem.get" />
  <function name="syllabusItem.save" />
  
	<tool
			id="sakai.syllabus"
			url="/sakai-syllabus/syllabus/jsf.tool" 

<!-- add this following line to enable help --> 
			helpUrl="/sakai-syllabus/helpReg/syllabus_help.xml" 

			title="Syllabus"
			description="Syllabus tool for sakai.">

	</tool>
</registration>

Step 4:
Make sure the entry in usr/local/sakai/sakai.properties file is set to "true". 

#should the title help button be visible?
helpEnabled=true

Step 5:
Make sure the 'help.soap.key' entry in usr/local/sakai/sakai.properties file is set. 
System administrators will need to contact the IU KB people to obtain a key so that
the Help tool can synchronize the content.

Step 5: 
Run "maven" and "maven reg" for the newly developed tool to build, deploy and 
register the new tool and its help files. 
