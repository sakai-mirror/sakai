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
      <title><h:outputText value="copy pool"/></title>
                        <!-- stylesheet and script widgets -->
      <samigo:stylesheet path="/css/samigo.css"/>
      <samigo:stylesheet path="/css/sam.css"/>
      <samigo:stylesheet path="/css/nav.css"/>
<script language="javascript" style="text/JavaScript">
<!--
<%@ include file="/js/samigotree.js" %>
//-->
</script>
      </head>
<body onload="collapseAllRowsForSelectList();flagRows();">
<!-- content... -->

<h:form id="copyPool">

<h3 class="insColor insBak insBor">
<h:outputText rendered="#{questionpool.actionType == 'pool'}" value="#{msg.copy_p}"/>
<h:outputText rendered="#{questionpool.actionType == 'item'}" value="#{msg.copy_q}"/>
</h3>


<div class="indnt1">
<h:outputText value="#{msg.sel_dest}"/>
<f:verbatim><br/></f:verbatim>
<h:outputText rendered="#{questionpool.actionType == 'pool'}" value="#{questionpool.currentPool.displayName}"/>
<h:outputText rendered="#{questionpool.actionType == 'item'}" value="#{questionpool.currentItem.text}"/>

</div>

<f:verbatim><br/></f:verbatim>
<f:verbatim><br/></f:verbatim>
<div class="indnt2">
<h:outputText styleClass="number" value="1"/>
<h:outputText rendered="#{questionpool.actionType == 'pool'}" value="#{msg.copy_p_to}"/>
<h:outputText rendered="#{questionpool.actionType == 'item'}" value="#{msg.copy_q_to}"/>

<f:verbatim><br/></f:verbatim>
<f:verbatim><br/></f:verbatim>
<f:verbatim><br/></f:verbatim>

<%@ include file="/jsf/questionpool/copyPoolTree.jsp" %>

<f:verbatim><br/></f:verbatim>
<h:outputText styleClass="number" value="2"/>
<h:outputText value="#{msg.click_copy}"/>

</div>
<f:verbatim><br/></f:verbatim>
<f:verbatim><br/></f:verbatim>


<div class="indnt2">
<h:panelGrid rendered="#{questionpool.actionType == 'pool'}">
<h:panelGroup>

  <h:commandButton id="copypoolsubmit" immediate="true" value="#{msg.copy}"
    action="#{questionpool.copyPool}">
  </h:commandButton>


<h:commandButton style="act" value="#{msg.cancel}" action="poolList"/>

</h:panelGroup>
</h:panelGrid>

<h:panelGrid rendered="#{questionpool.actionType == 'item'}">
<h:panelGroup>

  <h:commandButton id="copyitemsubmit" immediate="true" value="#{msg.copy}"
    action="#{questionpool.copyQuestion}">
  </h:commandButton>

<h:commandButton style="act" value="#{msg.cancel}" action="poolList"/>

</h:panelGroup>
</h:panelGrid>

</div>

</h:form>
</body>
</html>
</f:view>
