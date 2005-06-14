<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" import="java.sql.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<jsp:useBean id="part" scope="session" class="org.navigoproject.ui.web.form.edit.PartForm" />
<% int count=0; //this sets up the row counter %>
<html:html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<logic:notEqual name="isNew" value="true">
<title>Edit Parts Display</title>
</logic:notEqual>
<logic:equal name="isNew" value="true">
<title>Add New Part</title>
</logic:equal>
<link href="../stylesheets/main.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#ffffff" topmargin=0 marginheight=0 leftmargin=0 marginwidth=0>
<logic:notEqual name="isNew" value="true">
<p class="pageTitle">Edit Parts Display</p>
</logic:notEqual>
<logic:equal name="isNew" value="true">
<p class="pageTitle">Add New Part</p>
</logic:equal>

<img src="../images/divider2.gif">
<p class="instructions">
Specify which components of the Part Headers will be available to faculty to
edit. <br>
Specify the types of questions and how they will be ordered for the Part.<br>
Then click the Submit button to save your input. No input will be saved if you
click the Cancel button.
<br><br>
Note: A Part is a section of an assessment that contains a header
and a set of questions.</p>
<html:form action="editPart.do" method="post">
  <table class="tblEdit">
    <tr>
      <td colspan="2" class="tdEditTop">
        <logic:equal name="editorRole" value="templateEditor">
          <span class="heading2"><em>Template Editor:</em>
          <jsp:getProperty name="description" property="templateName"/>
          </span>
        </logic:equal>
        <logic:equal name="editorRole" value="assessmentEditor"><span class="heading2"><em>Assessment
              Editor:</em>
          <jsp:getProperty name="description" property="name"/>
          </span></logic:equal>
      </td>
      <logic:equal name="editorRole" value="templateEditor">
        <td class="tdEditEditView">Instructor Editable</td>
      </logic:equal>
      <td class="tdEditEditView">Student Viewable</td>
    </tr>
    <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (part.getName_isInstructorEditable())) { %>
    <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
      <td class="tdEditSide"> <span class="heading2">Part Name</span> </td>
        <td><html:text property="name" size="50" tabindex="1"/></td>
      <logic:equal name="editorRole" value="templateEditor">
        <td><html:checkbox property="name_isInstructorEditable"/>
        </td>
      </logic:equal>
      <td><html:checkbox property="name_isStudentViewable"/>
      </td>
    </tr>
    <% } %>
<%-- REMOVED FORE BETA2, PERMANENT? -mARC
    <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (part.getType_isInstructorEditable())) { %>
    <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
      <td class="tdEditSide"><span class="heading2">Part Type</span> </td>
      <td>
        <logic:equal name="editorRole" value="templateEditor">
          <html:select property="type" name="part" tabindex="2">
            <html:options name="part" property="partTypes"/>
          </html:select>
        </logic:equal>
        <logic:equal name="editorRole" value="assessmentEditor"> <bean:write name="part" property="type"/> </logic:equal>
      </td>
      <logic:equal name="editorRole" value="templateEditor">
        <td><html:checkbox property="type_isInstructorEditable"/>
        </td>
      </logic:equal>
      <td><html:checkbox property="type_isStudentViewable"/>
      </td>
    </tr>
    <% } %>
--%>    <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (part.getDescription_isInstructorEditable())) { %>
    <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
      <td class="tdEditSide"><span class="heading2">Part Description</span></td>
      <td>
          <html:textarea property="description" cols="50" rows="2" tabindex="3"/>
      </td>
      <logic:equal name="editorRole" value="templateEditor">
        <td><html:checkbox property="description_isInstructorEditable"/>
        </td>
      </logic:equal>
      <td><html:checkbox property="description_isStudentViewable"/>
      </td>
    </tr>
    <% } %>
    <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (part.getKeywords_isInstructorEditable())) { %>
    <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
      <td class="tdEditSide"> <span class="heading2">Part Keywords</span></td>
      <td>
          <html:textarea property="keywords" cols="50" rows="2" tabindex="4"/>
      </td>
      <logic:equal name="editorRole" value="templateEditor">
        <td><html:checkbox property="keywords_isInstructorEditable"/>
        </td>
      </logic:equal>
      <td><html:checkbox property="keywords_isStudentViewable"/>
      </td>
    </tr>
    <% } %>
    <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (part.getObjectives_isInstructorEditable())) { %>
    <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
      <td class="tdEditSide"><span class="heading2">Part Objectives</span> </td>
      <td>
          <html:textarea property="objectives" cols="50" rows="2" tabindex="5"/>
      </td>
      <logic:equal name="editorRole" value="templateEditor">
        <td><html:checkbox property="objectives_isInstructorEditable"/>
        </td>
      </logic:equal>
      <td><html:checkbox property="objectives_isStudentViewable"/>
      </td>
    </tr>
    <% } %>
    <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (part.getRubrics_isInstructorEditable())) { %>
    <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
      <td class="tdEditSide"><span class="heading2">Part Rubrics</span> </td>
      <td>
          <html:textarea property="rubrics" cols="50" rows="2" tabindex="6"/>
      </td>
        <logic:equal name="editorRole" value="templateEditor">
      <td><html:checkbox property="rubrics_isInstructorEditable"/></td>
      </logic:equal>
      <td><html:checkbox property="rubrics_isStudentViewable"/>
      </td>
    </tr>
    <% } %>
    <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (part.getQuestionOrder_isInstructorEditable())) { %>
    <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
      <td class="tdEditSide"><span class="heading2">Question Order</span> </td>
      <td>
        <html:radio property="questionOrder" value="0" tabindex="7"/>
        In the order listed.<BR>
        <html:radio property="questionOrder" value="1" tabindex="8"/>
        Shuffled differently for each testee.<BR>
      </td>
      <logic:equal name="editorRole" value="templateEditor">
        <td><html:checkbox property="questionOrder_isInstructorEditable"/>
        </td>
      </logic:equal>
      <td><html:checkbox property="questionOrder_isStudentViewable"/>
      </td>
    </tr>
    <% } %>
    <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (part.getMediaCollection_isInstructorEditable())) { %>
    <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
      <td class="tdEditSide"><span class="heading2">Inline Media</span> </td>
      <td>(See
          <logic:equal name="editorRole" value="assessmentEditor">
            Assessment
          </logic:equal>
          <logic:notEqual name="editorRole" value="assessmentEditor">
            Template
          </logic:notEqual>
        Editor for list of Inline Media)
      </td>
      <logic:equal name="editorRole" value="templateEditor">
        <td><html:checkbox property="mediaCollection_isInstructorEditable"/>
        </td>
      </logic:equal>
      <td><html:checkbox property="mediaCollection_isStudentViewable"/>
      </td>
    </tr>
    <% } %>
    <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (part.getRelatedMediaCollection_isInstructorEditable())) { %>
    <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
      <td class="tdEditSide"> <span class="heading2">Related Files and Links</span> </td>
      <td>(See
          <logic:equal name="editorRole" value="assessmentEditor">
            Assessment
          </logic:equal>
          <logic:notEqual name="editorRole" value="assessmentEditor">
            Template
          </logic:notEqual>
           Editor for list of Related Files and Links)</td>
      <logic:equal name="editorRole" value="templateEditor">
        <td><html:checkbox property="relatedMediaCollection_isInstructorEditable"/>
        </td>
      </logic:equal>
      <td><html:checkbox property="relatedMediaCollection_isStudentViewable"/>
      </td>
    </tr>
    <% } %>
    <tr>
      <td class="tdEditSide">&nbsp;</td>
      <td align="center">
        <html:submit value="Submit" property="Submit" tabindex="9"/>
        <html:reset onclick="history.go(-1)" value="Cancel" tabindex="10"/>
      </td>
      <logic:equal name="editorRole" value="templateEditor">
        <td align="center">&nbsp;</td>
        <td align="center">&nbsp;</td>
      </logic:equal>
    </tr>
  </table>
</html:form>
</body>
</html:html>
