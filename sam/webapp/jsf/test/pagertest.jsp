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
      <title><h:outputText value="#{msg.q_mgr}" /></title>
      <samigo:stylesheet path="/css/main.css"/>
	<samigo:stylesheet path="/jsp/aam/stylesheets/nav.css" />
<script language="javascript" style="text/JavaScript">
<!--
<%@ include file="/js/samigotree.js" %>
//-->
</script>

      </head>
    <body>

<!-- content... -->
<h:form id="questionpool">

<h1>JSF TAGS CONTROL</h1>
<div align="right">
<h:commandButton type="button" id="previousbtn" value="#{msg.previous}"
     action="questionpool"
     onclick="document.forms['questionpool']['test'].value='>'; document.forms['questionpool'].submit(); return false;"/>
<h:selectOneMenu id="selectlist" value="10">
  <f:selectItem itemValue="10" itemLabel="Show 10 Items per Page"/>
  <f:selectItem itemValue="20"  itemLabel="Show 20 Items per Page"/>
  <f:selectItem itemValue="30"  itemLabel="Show 30 Items per Page"/>
  <f:selectItem itemValue="50"  itemLabel="Show 50 Items per Page"/>
  <f:selectItem itemValue="100" itemLabel="Show 100 Items per Page"/>
</h:selectOneMenu>
<h:commandButton type="button" id="nextbtn" value="#{msg.next}"
     action="questionpool"
     onclick="document.forms['questionpool']['test'].value='<'; document.forms['questionpool'].submit(); return false;"/>
</div>

<h1>CUSTOM CONTROL</h1>

  <samigo:pagerButtonControl controlId="test" formId="questionpool" />

<h:dataTable id="TreeTable" value="#{questionpool.testPools}"
    var="pool" rows="3" headerClass="altHeading2" rowClasses="trEven,trOdd">

    <h:column id="col1">

     <f:facet name="header">
       <h:outputText id="c1header" value="name"/>
     </f:facet>


<h:panelGroup id="firstcolumn">
<h:inputHidden id="rowid" value="#{pool.trid}"/>
<h:selectBooleanCheckbox id="checkboxes" value ="#{questionpoo.destPools}"/>
<h:outputLink id="togglelink"  onclick="toggleRows(this)" value="#" styleClass="folder">
<h:graphicImage id="spacer_for_mozilla" style="border:0" width="17" value="../../images/delivery/spacer.gif" />
</h:outputLink>

<h:outputLink id="poolnamelink" value="editPool.faces?id=#{pool.name}">
<h:outputText id="poolnametext" value="#{pool.name}"/>
</h:outputLink>

</h:panelGroup>
    </h:column>

    <h:column id="col2">
     <f:facet name="header">
       <h:outputText id="c2header" value="creator"/>
     </f:facet>
     <h:panelGroup id="secondcolumn">
        <h:outputText value="#{pool.creator}"/>
     </h:panelGroup>
    </h:column>

    <h:column id="col3">
     <f:facet name="header">
       <h:outputText id="c3header" value="last modified"/>
     </f:facet>
     <h:panelGroup id="thirdcolumn">
        <h:outputText value="#{pool.lastModified}"/>
     </h:panelGroup>
    </h:column>

    <h:column id="col4">
     <f:facet name="header">
       <h:outputText value="# of questions"/>
     </f:facet>
     <h:panelGroup id="fourthcolumn">
        <h:outputText value="#{pool.noQuestions}"/>
     </h:panelGroup>
    </h:column>


    <h:column id="col5">
     <f:facet name="header">
       <h:outputText value="# of subpools"/>
     </f:facet>
     <h:panelGroup id="fifthcolumn">
        <h:outputText value="#{pool.nosubpools}"/>
     </h:panelGroup>
    </h:column>

    <h:column id="col6">
            <h:commandButton type="submit" id="addbutton" value="Add Subpool"
              action="addPool">
            </h:commandButton>
    </h:column>



  </h:dataTable>

  <samigo:pager controlId="test" dataTableId="TreeTable" showpages="999"
    showLinks="false"
    styleClass="rtEven" selectedStyleClass="rtOdd"/>

</h:form>
      </body>
    </html>
  </f:view>
