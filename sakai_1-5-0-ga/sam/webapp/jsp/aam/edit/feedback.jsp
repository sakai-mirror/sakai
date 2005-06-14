<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<jsp:useBean id="feedback" scope="session" class="org.navigoproject.ui.web.form.edit.FeedbackForm" />
<jsp:useBean id="dateutil" scope="session" class="org.navigoproject.business.entity.DateHandlerWithNull" />

<% int count=0; //this sets up the row counter %><head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Edit Feedback</title>

<link href="../stylesheets/main.css" rel="stylesheet" type="text/css">
<html:base/>
<%@include file="../includeJS/edit.js" %>
</head>

<logic:equal name="editorRole" value="templateEditor">
<body>
</logic:equal>
<logic:equal name="editorRole" value="assessmentEditor">
<body onLoad="disableCheckboxes('StudentViewable');">
</logic:equal>

<p class="pageTitle">Edit Feedback</p>
<img src="../images/divider2.gif"> 
<p class="instructions">Specify when and how feedback will be delivered to students. 
  Feedback includes correct responses, explanations, scores, and instructor comments. Then 
  click the Submit button to save your input. No input will be saved if you click 
  the Cancel button. </p>
<p>
<div class="error">
<html:errors/>
</div>
</p>
<html:form action="editFeedback.do" method="post">
<table class="tblEdit">
  <tr>
    <td colspan="2" class="tdEditTop"> 
<logic:equal name="editorRole" value="templateEditor"><span class="heading2"><em>Template 
      Editor:</em> 
      <jsp:getProperty name="description" property="templateName"/>
      </span> </logic:equal> <logic:equal name="editorRole" value="assessmentEditor"><span class="heading2"><em>Assessment 
      Editor:</em> 
      <jsp:getProperty name="description" property="name"/>
      </span></logic:equal>    </td>
    <logic:equal name="editorRole" value="templateEditor">
   
    <th class="tdEditEditView">Instructor Editable</th>
 </logic:equal>
    <th class="tdEditEditView">Student Viewable</th>
    </tr>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (feedback.getFeedbackType_isInstructorViewable() ||
          feedback.getFeedbackType_isInstructorEditable())) { %>
  <tr>
    <td valign=top bgcolor=#cccccc class="tdEditSide"><span class="heading2">Feedback</span></td>
    <td>
     <table border=0 cellpadding=4 cellspacing=2>
        <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
          <td> <% if (session.getAttribute("editorRole").equals("templateEditor") ||
           feedback.getFeedbackType_isInstructorEditable()) { %> 
	<html:radio name="feedback" property="feedbackType" value="0" onclick="disableObj(1,3,false);"/> Feedback will be displayed immediately to student upon completing:<br> <ul>
              <html:checkbox property="immediateFeedbackType_q" /> Each Question<br>
              <html:checkbox property="immediateFeedbackType_p" /> Each Part<br>
              <html:checkbox property="immediateFeedbackType_a" /> The Full Assignment<br>
            </ul>
            <% } else { %> <bean:write name="feedback" property="feedbackType"/>
            <% } %> </td>
        </tr>
        <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
          <td> <html:radio name="feedback" property="feedbackType" value="1" onclick="disableObj(1,3,true);"/> Feedback will be displayed to student at specified date:<BR>
          <logic:equal name="editorRole" value="templateEditor">
            <i>Assessment Author sets Date</i><br>
          </logic:equal>
          <logic:equal name="editorRole" value="assessmentEditor">
            Day: <html:select property="feedbackDay">
<!-- B2 do not support NULL feedback date
							<option value="">--
-->
              <html:options name="dateutil" property="day"/> </html:select>
            Month<html:select property="feedbackMonth">
<!-- B2 do not support NULL feedback date
							<option value="">--
-->
							<html:options name="dateutil" property="month"/>
              </html:select>
						Year<html:select property="feedbackYear">
<!-- B2 do not support NULL feedback date
							<option value="">--
-->
              <html:options name="dateutil" property="year"/> 
							</html:select>
						Time: <html:select property="feedbackHour">
              <html:options name="dateutil" property="hour"/> 
							</html:select>
						<html:select property="feedbackMinute">
              <html:options name="dateutil" property="min"/> 
							</html:select>
						<html:select property="feedbackAmPm">
              <html:options name="dateutil" property="amPm"/> 
							</html:select><br />
              <!-- SCORES ON SAME DATE -->
            </logic:equal>  
          </td>
        </tr>
<tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
      <td><html:radio name="feedback" property="feedbackType" value="2" onclick="disableObj(1,3,true);"/> Feedback (including Scores) will NOT be displayed to student.<br></td></tr>
      </table>
 </td>
    <logic:equal name="editorRole" value="templateEditor">
   
    <td><html:checkbox property="feedbackType_isInstructorEditable"/></td>  </logic:equal> 
    <td><html:checkbox property="feedbackType_isStudentViewable"/></td>
  </tr>
  <% } %>
  <tr> 
    <td class="tdEditSide">&nbsp;</td>
    <td align="center"> <html:submit value="Submit" property="Submit"/> <html:reset onclick="history.go(-1)" value="Cancel"/> 
    </td>
    <logic:equal name="editorRole" value="templateEditor"> 
    <td align="center">&nbsp;</td>
    <td align="center">&nbsp;</td>
    </logic:equal> </tr>
</table>
</html:form>
</body>
</html:html>
