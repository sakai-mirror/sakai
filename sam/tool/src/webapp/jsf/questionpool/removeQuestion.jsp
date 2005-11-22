<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://www.sakaiproject.org/samigo" prefix="samigo" %>
<!DOCTYPE html
     PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<!-- $Id$
<%--
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
*
* Licensed under the Educational Community License Version 1.0 (the "License");
* By obtaining, using and/or copying this Original Work, you agree that you have read,
* understand, and will comply with the terms and conditions of the Educational Community License.
* You may obtain a copy of the License at:
*
*      http://cvs.sakaiproject.org/licenses/license_1_0.html
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
* AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*
**********************************************************************************/
--%>
-->
  <f:view>
    <f:loadBundle
       basename="org.sakaiproject.tool.assessment.bundle.QuestionPoolMessages"
       var="msg"/>
   
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head><%= request.getAttribute("html.head") %>
      <title><h:outputText value="Remove Question"/></title>
      </head>
      <body onload="<%= request.getAttribute("html.body.onload") %>">
<!-- content... -->
<div class="portletBody">
 <h:form id="removePoolForm">
   <h:panelGrid cellpadding="5" cellspacing="3">
     <h:panelGroup>
       <f:verbatim><h3 style="insColor insBak"></f:verbatim>
       <h:outputText  value="#{msg.rm_q_confirm}" />
       <f:verbatim></h3></f:verbatim>
     </h:panelGroup>
     <h:outputText styleClass="validation" value="#{msg.remove_sure_q}" />

<h:dataTable value="#{questionpool.itemsToDelete}" var="question">
      <h:column>
       <h:outputText escape="false" value="#{question.text}" />
      </h:column>
</h:dataTable>

     <h:panelGrid columns="2" cellpadding="3" cellspacing="3">
  <h:commandButton type="submit" immediate="true" id="Submit" value="#{msg.remove}"
    action="#{questionpool.removeQuestionsFromPool}" >
  </h:commandButton>

<h:commandButton style="act" value="#{msg.cancel}" action="poolList"/>

     </h:panelGrid>
   </h:panelGrid>

 </h:form>
 <!-- end content -->


</div>

</body>
</html>
</f:view>
