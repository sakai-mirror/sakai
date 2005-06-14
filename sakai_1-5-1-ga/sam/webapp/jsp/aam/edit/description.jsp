<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" language="java" import="java.sql.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<jsp:useBean id="description" scope="session" class="org.navigoproject.ui.web.form.edit.DescriptionForm" />
<% int count=0; //this sets up the row counter %>
<html:html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Edit Descriptive Information</title>
<link href="../stylesheets/main.css" rel="stylesheet" type="text/css">
<%@include file="../includeJS/edit.js" %>
</head>

<logic:equal name="editorRole" value="templateEditor">
<body>
</logic:equal>
<logic:equal name="editorRole" value="assessmentEditor">
<body onLoad="disableCheckboxes('StudentViewable');">
</logic:equal>
<html:form action="editDescription.do" method="post">

<%--only show if new template --%>
<logic:equal name="editorState" value="newTemplate">
<p class="pageTitle">Template Name and Description</p>
<img src="../images/divider2.gif">
<p class="instructions">Name the Template. You may also add a description of
  the template that users can view.
These can be changed later, if you wish.
Then click the Submit Button to go to full Template Editor.</p>
<table class="tblEdit">
  <tr>
    <td colspan="2" class="tdEditTop"><span class="heading2">
  <em>Template Editor:</em> New Template</span></td>
  </tr>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"> <span class="heading2">Template Name</span> </td>
    <td>  <html:text size="60" property="templateName" tabindex="4"/>
    </td>
  </tr>

  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"><span class="heading2">Template Description (optional)</span></td>
    <td> <html:textarea cols="50" rows="4" property="templateDescription" tabindex="5"/>
    </td>
  </tr>
  <tr>
    <td class="tdEditSide">&nbsp;</td>
    <td align="center"> <html:submit value="Submit" property="Submit" tabindex="9"/>
      <html:reset onclick="history.go(-1)" value="Cancel" tabindex="10"/> </td>
</tr>
</table>
</logic:equal>

<%--only show if new assessment--%>
<logic:equal name="editorState" value="newAssessment">

<p class="pageTitle">Assessment Name and Description</p>
<img src="../images/divider2.gif">
<p class="instructions">Name the Assessment. You may also add a description of
  the assessment that users can view. These can be changed later, if you wish.
  Then click the Submit Button to go to full Assessment Editor.</p>
<table class="tblEdit">
  <tr>
    <td colspan="2" class="tdEditTop"><span class="heading2">
  <em>Assessment Editor:</em> New Assessment</span></td>
  </tr>

  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"><span class="heading2">Assessment Name</span> </td>
    <td width="100%">
      <html:text size="60" property="name" tabindex="1" />
    </td>
  </tr>

  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"><span class="heading2">Assessment Description (optional)</span></td>
    <td><html:textarea cols="50" rows="4" property="description" tabindex="3"/>
    </td>
  </tr>
  <tr>
    <td class="tdEditSide">&nbsp;</td>
    <td align="center"> <html:submit value="Submit" property="Submit" tabindex="9"/>
      <html:reset onclick="history.go(-1)" value="Cancel" tabindex="10"/> </td>
 </tr>
</table>
</logic:equal>

<%--only show when in normal edit mode --%>
<logic:equal name="editorState" value="edit">

<p class="pageTitle">Edit Descriptive Information</p>
<img src="../images/divider2.gif">
<table class="tblEdit">
  <tr>
    <td colspan="3">
      <p class="instructions">
      Specify the Descriptive Information to be displayed in the Assessment
      Header--<br>
      the top of the first assessment Web page.<br>
      Note: If the Student Viewable column is checked for a variable, then the<br>
      information will be displayed when a student takes the assessment.
    </td>
  </tr>
  <tr>
    <td colspan="2" class="tdEditTop"><logic:equal name="editorRole" value="templateEditor"><span class="heading2"><em>Template
      Editor:</em>
      <jsp:getProperty name="description" property="templateName"/>
      </span> </logic:equal> <logic:equal name="editorRole" value="assessmentEditor"><span class="heading2"><em>Assessment
      Editor:</em>
      <jsp:getProperty name="description" property="name"/>
      </span></logic:equal> </td>
    <logic:equal name="editorRole" value="templateEditor">
    <td class="tdEditEditView">Instructor<br>
      Editable</td>
    </logic:equal>
    <td class="tdEditEditView">Student Viewable</td>
  </tr>

<logic:equal name="editorRole" value="templateEditor">
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"> <span class="heading2">Template Name</span> </td>
    <td><logic:equal name="editorRole" value="templateEditor">
          <html:text size="60" property="templateName" tabindex="4"/>
        </logic:equal>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td>
      <span class="textSmall">View Only</span>
      <html:checkbox property="templateName_isInstructorViewable"/>
      </td>
    </logic:equal>
    <td><html:checkbox property="templateName_isStudentViewable"/> </td>
  </tr>

  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"><span class="heading2">Template Description</span></td>
    <td> <logic:equal name="editorRole" value="templateEditor"> <html:textarea cols="50" rows="4" property="templateDescription" tabindex="5"/>
      </logic:equal> <logic:equal name="editorRole" value="assessmentEditor">
      <bean:write name="description" property="templateDescription"/> </logic:equal>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td>
        <span class="textSmall">View Only</span>
        <html:checkbox property="templateDescription_isInstructorViewable" />
      </td>
    </logic:equal>
    <td><html:checkbox property="templateDescription_isStudentViewable"/> </td>
  </tr>
    </logic:equal>

<%--only show if role is assessment Editor--%>
<logic:equal name="editorRole" value="assessmentEditor">
  <%-- if (session.getAttribute("editorRole").equals("templateEditor") ||
         (description.getName_isInstructorEditable())) { --%>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"><span class="heading2">Assessment Name</span> </td>
    <td width="100%">
      <logic:equal name="editorRole" value="templateEditor">
      This value entered by assessment author
      </logic:equal>
      <logic:equal name="editorRole" value="assessmentEditor">
        <logic:equal name="description" property="name_isInstructorEditable" value="true">
          <html:text size="60" property="name" tabindex="1" />
        </logic:equal>
        <logic:equal name="description" property="name_isInstructorEditable" value="false">
          <bean:write name="description" property="name"/>
        </logic:equal>
      </logic:equal>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
    <td><html:checkbox property="name_isInstructorEditable"/>
    </td>
    </logic:equal>
    <td><html:checkbox property="name_isStudentViewable"/> </td>
  </tr>
  <%-- } --%>

      <%-- if (session.getAttribute("editorRole").equals("templateEditor") ||
         (description.getDescription_isInstructorEditable())) { --%>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"><span class="heading2">Assessment Description</span></td>
    <td>
      <logic:equal name="editorRole" value="templateEditor">This value entered
      by assessment author
      </logic:equal>
      <logic:equal name="editorRole" value="assessmentEditor">
        <logic:equal name="description" property="description_isInstructorEditable" value="true">
          <html:textarea cols="50" rows="4" property="description" tabindex="3"/>
        </logic:equal>
        <logic:equal name="description" property="description_isInstructorEditable" value="false">
          <bean:write name="description" property="description"/>
        </logic:equal>
      </logic:equal>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
    <td><html:checkbox property="description_isInstructorEditable"/> </td>
    </logic:equal>
    <td><html:checkbox property="description_isStudentViewable"/> </td>
  </tr>
 </logic:equal>
 <%-- } --%>
<logic:equal name="description" property="templateName_isInstructorEditable" value="true">
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"> <span class="heading2">Template Name</span> </td>
    <td>
      <logic:equal name="editorRole" value="templateEditor">
        <html:text size="60" property="templateName" tabindex="4"/>
      </logic:equal>
      <logic:equal name="editorRole" value="assessmentEditor">
        <bean:write name="description" property="templateName"/>
      </logic:equal>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td>
        <span class="textSmall">View Only</span>
        <html:checkbox property="templateName_isInstructorEditable"/>
      </td>
    </logic:equal>
    <td><html:checkbox property="templateName_isStudentViewable"/> </td>
  </tr>
</logic:equal>
<logic:equal name="description" property="templateName_isInstructorEditable" value="true">
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"><span class="heading2">Template Description</span></td>
    <td>
      <logic:equal name="editorRole" value="templateEditor">
        <html:textarea cols="50" rows="4" property="templateDescription" tabindex="5"/>
      </logic:equal>
      <logic:equal name="editorRole" value="assessmentEditor">
        <bean:write name="description" property="templateDescription"/>
      </logic:equal>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td>
      <span class="textSmall">View Only</span>
      <html:checkbox property="templateDescription_isInstructorEditable"/>
      </td>
    </logic:equal>
    <td><html:checkbox property="templateDescription_isStudentViewable"/> </td>
  </tr>
</logic:equal>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (description.getAssessmentType_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"><span class="heading2">Assessment Type</span> </td>
    <td> <% if (session.getAttribute("editorRole").equals("templateEditor") ||
           description.getAssessmentType_isInstructorEditable()) { %>
      <html:select property="assessmentType" name="description" tabindex="2">
      <html:options name="description" property="assessmentTypes"/> </html:select>
      <% } else { %>
      <bean:write name="description" property="assessmentType"/>
      <% } %>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
    <td> <html:checkbox property="assessmentType_isInstructorEditable"/>
    </td>
    </logic:equal>
    <td><html:checkbox property="assessmentType_isStudentViewable"/> </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (description.getObjectives_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"><span class="heading2">Assessment Objectives</span>
      <!--textarea -->
    </td>
    <td> <% if (session.getAttribute("editorRole").equals("templateEditor") ||
          description.getObjectives_isInstructorEditable()) { %>
      <html:textarea cols="50" rows="4" property="objectives" tabindex="7"/>
      <% } else { %>
      <bean:write name="description" property="objectives"/> <% }
        %>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
    <td><html:checkbox property="objectives_isInstructorEditable"/> </td>
    </logic:equal>
    <td><html:checkbox property="objectives_isStudentViewable"/> </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (description.getKeywords_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"> <span class="heading2">Assessment Keywords</span></td>
    <td> <% if (session.getAttribute("editorRole").equals("templateEditor") ||
           description.getKeywords_isInstructorEditable()) { %>
      Please enter comma separated words or phrases:<br>
      <html:textarea cols="50" rows="4" property="keywords" tabindex="6"/>
      <% } else { %> <bean:write name="description" property="keywords"/> <% } %>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
    <td><html:checkbox property="keywords_isInstructorEditable"/> </td>
    </logic:equal>
    <td><html:checkbox property="keywords_isStudentViewable"/> </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (description.getRubrics_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"><span class="heading2">Assessment Rubrics</span>
      <!--textarea -->
    </td>
    <td> <% if (session.getAttribute("editorRole").equals("templateEditor") ||
          description.getRubrics_isInstructorEditable()) { %>
      <html:textarea cols="50" rows="4" property="rubrics" tabindex="8"/>
      <% } else { %> <bean:write name="description" property="rubrics"/>
      <% } %>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
    <td><html:checkbox property="rubrics_isInstructorEditable"/> </td>
    </logic:equal>
    <td><html:checkbox property="rubrics_isStudentViewable"/> </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (description.getMediaCollection_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <%-- ref 242 s/b 'Inline Media' --%>
    <%-- <td class="tdSideRedText">Inline Media      </td>--%>
    <td class="tdEditSide"> <span class="heading2">Inline Media</span> </td>
    <td> See Template Editor for list of Inline Media.</td>
    <logic:equal name="editorRole" value="templateEditor">
    <td><html:checkbox property="mediaCollection_isInstructorEditable"/> </td>
    </logic:equal>
    <td><html:checkbox property="mediaCollection_isStudentViewable"/> </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (description.getRelatedMediaCollection_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"><span class="heading2">Related Files and Links</span>
      <!--sub table list -->
    </td>
    <td> See Template Editor for list of Related Files and Links.</td>
    <logic:equal name="editorRole" value="templateEditor">
    <td><html:checkbox property="relatedMediaCollection_isInstructorEditable"/>
    </td>
    </logic:equal>
    <td><html:checkbox property="relatedMediaCollection_isStudentViewable"/> </td>
  </tr>
  <% } %>
  <tr>
    <td class="tdEditSide">&nbsp;</td>
    <td align="center"> <html:submit value="Submit" property="Submit" tabindex="9"/>
      <html:reset onclick="history.go(-1)" value="Cancel" tabindex="10"/> </td>
    <logic:equal name="editorRole" value="templateEditor">
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    </logic:equal> </tr>
</table>

</logic:equal>

</html:form>
</body>
</html:html>
