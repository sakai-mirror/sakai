<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://www.sakaiproject.org/samigo" prefix="samigo" %>


  <f:view>
  <f:loadBundle
     basename="org.sakaiproject.tool.assessment.bundle.QuestionPoolMessages"
     var="msg"/>
    <f:verbatim><!DOCTYPE html
     PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
    </f:verbatim>
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
      <title><h:outputText value="#{msg.q_mgr}"/></title>
      <samigo:stylesheet path="/css/samigo.css"/>
      <samigo:stylesheet path="/css/sam.css"/>
      <samigo:stylesheet path="/css/nav.css"/>
<script language="javascript" style="text/JavaScript">
<!--
<%@ include file="/js/samigotree.js" %>
//-->
</script>
      </head>
<body onload="collapseAllRows();flagRows();">

<!-- content... -->
<h:form id="questionpool">


<p class="navIntraTool" style="background-color:DDE3EB">
   <h:commandLink action="author" id="authorlink" immediate="true">
   <h:outputText id="myassessment" value="#{msg.my_assessments}"/>
       <f:actionListener
         type="org.sakaiproject.tool.assessment.ui.listener.author.AuthorActionListener" />
   </h:commandLink>

<h:outputText value=" | " />

<h:outputLink id="mytemplate" value="#{msg.template_url}">
        <h:outputText value="#{msg.my_templates}"/>
</h:outputLink>
<h:outputText value=" | " />
        <h:outputText value="#{msg.qps}"/>
</p>

 <div class="indnt1">
 <h3><h:outputText value="#{msg.qps}"/></h3>

<h:outputText rendered="#{questionpool.importToAuthoring == 'true'}" value="#{msg.msg_imp_poolmanager}"/>

<p class="navIntraTool" style="background-color:DDE3EB">
<h:panelGrid rendered="#{questionpool.importToAuthoring == 'false'}">
<h:panelGroup>


<h:commandLink id="add" immediate="true" action="#{questionpool.addPool}"> 
  <h:outputText value="#{msg.add_new_pool}"/>
  <f:param name="qpid" value="0"/>
</h:commandLink>
<%--
<h:outputText value=" | " />

<h:outputLink id="import" value="editPool.faces">
        <h:outputText value="#{msg.import}"/>
</h:outputLink>
<h:outputText value=" | " />

<h:outputLink id="search" value="editPool.faces">
        <h:outputText value="#{msg.search}"/>
</h:outputLink>
<h:outputText value=" | " />

<h:outputLink id="useradmin" value="editPool.faces">
        <h:outputText value="#{msg.useradmin}"/>
</h:outputLink>
--%>

</h:panelGroup>
</h:panelGrid>
</p>
 
<%@ include file="/jsf/questionpool/poolTreeTable.jsp" %>




<f:verbatim><br/></f:verbatim>
  <h:commandButton rendered="#{questionpool.importToAuthoring == 'false'}"  type="submit" immediate="true" id="Submit" value="#{msg.update}" action="#{questionpool.startRemovePool}" >
  </h:commandButton>
  <h:commandButton rendered="#{questionpool.importToAuthoring == 'true'}"  type="submit" immediate="true" id="cancel" value="#{msg.cancel}" action="#{questionpool.cancelImport}" >
  </h:commandButton>


 </div>
</h:form>
      </body>
    </html>
  </f:view>
