<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<jsp:useBean id="question" scope="session" class="org.navigoproject.ui.web.form.edit.QuestionForm" />
<% int count=1; //this sets up the row counter %>
<html:html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>
Add Question to Part
</title>
<link href="<%=request.getContextPath()%>/stylesheets/main.css" rel="stylesheet" type="text/css">
</head>
<html:form action="editQuestionEditor.do" method="post">
<body>
<!-- this hidden parameter is used to decide which jsp page to goto after saving the question content -->
<input type="hidden" name="forwardAction" value="">
<p class="pageTitle">Create <jsp:getProperty name="question" property="itemType"/> Question</p>
<img src="<%=request.getContextPath()%>/images/divider2.gif">
<p class="instructions">Enter the question, answer selections, and any other information
  needed, then click Submit at bottom of page. </p>

<table class="tblEdit">
  <logic:equal name="question" property="value_isInstructorEditable"
     value="true">
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"> <span class="heading2">Total Possible Points</span>
    </td>
    <td colspan="2"><html:text size="5" property="value" />
     </td>
   </tr>
   </logic:equal>
   <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"> <span class="heading2">Question <bean:write name="question" property="number" /></span> </td>
    <td colspan="2"><html:textarea rows="5" cols="50" property="text" /><BR>
<!-- No text/html choice in beta2
<html:radio property="type" value="text"/>Text
<html:radio property="type" value="html" />HTML
-->
<table width="100%">
        <tr>
          <td> <div align="left">Title/Author</div></td>
          <td>File Format</td>
          <td>Date added: </td>
          <td>

<!-- replacing this one with the following because content of question should be saved before going to upload file
<a href="<%=request.getContextPath()%>/startFileUploadQuestion.do?isLink=false" onClick="javascript:document.forwardAction.value='startFileUploadQuestion';document.forms[0].submit();">New
            File</a><br>
-->
<a href="#" onClick="javascript:document.forms[0].forwardAction.value='startFileUploadQuestion';document.forms[0].submit();document.forms[0].forwardAction.value='';">New
            File</a><br>
    <% if (question.getMediaCollection().size() > 1) { %>
      <a href="<%=request.getContextPath()%>/startReorderQuestion.do?isLink=false">Reorder</a>
    <% } %>
    </td>
          <td>&nbsp; </td>
          <td>&nbsp; </td>
        </tr>
        <logic:iterate name="question" id="mediaItem" property="reversedMediaCollection" type="org.navigoproject.business.entity.assessment.model.MediaData" indexId="ctr">
        <tr>
          <td> <logic:equal name='mediaItem' property='location' value=""> <html:link page="/showMedia.do" target="_blank"  paramId="id" paramName="mediaItem" paramProperty="mapId"><bean:write name="mediaItem" property="name" />
            </html:link> </logic:equal> <logic:notEqual name='mediaItem' property='location' value="">
            <a href="<bean:write name='mediaItem' property='location' />"><bean:write name="mediaItem" property="name" /></a>
            </logic:notEqual> 
            <logic:notEmpty name='mediaItem' property='description'>
               <br><bean:write name='mediaItem' property='description'/><br>
            </logic:notEmpty>
            <logic:notEmpty name='mediaItem' property='author'>
                by <bean:write name='mediaItem' property='author'/>
            </logic:notEmpty>
          </td>
          <td> <bean:write name='mediaItem' property='type' /> </td>
          <td> <bean:write name='mediaItem' property='dateAdded' /> </td>
          <td> <a href="<%=request.getContextPath()%>/startFileUploadQuestion.do?isLink=false&index=<%= ctr.toString() %>">Edit</a>
          </td>
          <td> <a href="<%=request.getContextPath()%>/deleteFileUploadQuestion.do?isLink=false&index=<%= ctr.toString() %>">Remove</a>
          </td>
          <td>
            <!-- html:link action='moveMedia'
paramId='id' paramName='mediaItem' paramProperty='id' -->
            <!-- /html:link -->
          </td>
        </tr>
        </logic:iterate> </table>
     </td></tr>

<logic:equal name="question" property="itemType" value="Multiple Choice">
  <%@ include file="mcEditor.jsp" %>
</logic:equal>
<logic:equal name="question" property="itemType" value="True/False">
  <%@ include file="tfEditor.jsp" %>
</logic:equal>
<logic:equal name="question" property="itemType" value="File Upload">
  <%@ include file="saEditor.jsp" %>
</logic:equal>
<logic:equal name="question" property="itemType" value="Short Answer">
  <%@ include file="saEditor.jsp" %>
</logic:equal>
<logic:notEqual name="question" property="itemType" value="File Upload">
 <% if (question.getHint_isInstructorEditable()) { %>
<tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"> <span class="heading2">Hint</span> </td>
    <td colspan="2"><html:textarea cols="50" rows="2" property="hint" /></td></tr>
<%}%>
</logic:notEqual>
<%--page break removed for beta 2, permanent?
<logic:equal name="question" property="offerPageBreak" value="true">
  <tr><td class="tdSideRedText"> Page Break? </td><td colspan="2"><html:checkbox name="question" property="pageBreak" /> Insert a page break after this question?</td></tr>
</logic:equal>
--%><tr>
    <td class="tdEditSide"> <span class="heading2">Question <bean:write name="question" property="number" /> Data</span><br><br>
<a href="<%=request.getContextPath()%>/questionChooser.do?use=adder&qid=<%= question.getId() %>"
  >Save Question in Pool</a></td>
    <td>
  <table width=100%>
      <logic:equal name="question" property="name_isInstructorEditable"
        value="true">
        <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
          <td>Question Title:</td>
          <td><html:text size="50" property="name"/></td>
        </tr>
      </logic:equal>
        <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
        <td>Question Type</td>
        <td><jsp:getProperty name="question" property="itemType"/></td>
        </tr>
      <logic:equal name="question" property="objectives_isInstructorEditable"
        value="true">
        <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
          <td>Question Objectives</td>
          <td><html:textarea cols="50" rows="2" property="objectives"/></td>
        </tr>
      </logic:equal>
      <logic:equal name="question" property="keywords_isInstructorEditable"
        value="true">
        <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
          <td>Question Keywords</td>
          <td><html:textarea cols="50" rows="2" property="keywords"/></td>
        </tr>
      </logic:equal>
      <logic:equal name="question" property="rubrics_isInstructorEditable"
        value="true">
        <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
          <td>Question Rubrics</td>
          <td><html:textarea cols="50" rows="2" property="rubrics"/></td>
        </tr>
      </logic:equal>
        <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
          <td>Question Pools</td>
          <td>This question is in:<br><br>
              <bean:write name="question" property="pools"/></td>
        </tr>

      </table>
</td>
</tr>
<tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'><td colspan=3 valign=TOP align="center">
    <html:submit value="Submit" property="Submit"/>
<%--  removed for beta2 per 11/20/03 AAM checkin mtg. decision   <html:reset onclick="history.go(-1)" value="Cancel"/>
--%></td></tr>
</table>

</html:form>
</body>
</html:html>
