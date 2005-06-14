<%--show only to assessment editor if one or more options is inst editable--%>
<% if (submissions.getLength((String) session.getAttribute("editorRole")) > 0) { %>
<table class="tblMain">

  <%--
If the submissions info is in template editor then show the section.
If the submissions info is in assessment editor only show if instructor visible.
--%>
  <% 
  if (submissions.getSubmissionModel_isInstructorEditable() ||
  session.getAttribute("editorRole").equals("templateEditor"))
  {
  %>
  <tr>
    <td class="tdEditorSide"><a name=submissions></a><span class="heading2">Submissions</span><br>
      <html:link page="/jsp/aam/edit/submissions.jsp">Edit</html:link>
    </td>
    <td class="tdEditorTopFeature">Feature</td>
    <td class="tdEditorTop">Value</td>
    <logic:equal name="editorRole" value="templateEditor">
      <td class="tdEditorEditView"> Instructor<br>
        Editable</td>
    </logic:equal>
    <td class="tdEditorEditView"> Student<br>
      Viewable</td>
  </tr>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
      (submissions.getLateHandling_isInstructorEditable())) { %>
  <% count=0; %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Late Handling of Submissions</td>
    <td>&nbsp;
      <%-- @todo we should move the AllChoices into a resource after Struts 1.1 --%>
      <%--<jsp:getProperty name="submissions" property="lateHandling"/>--%>
      <% int lateHandling = 0;

    try{
      lateHandling = new Integer(submissions.getLateHandling()).intValue();
    }
    catch (Exception e){
      //
    }
    %>
      <%=AllChoices.lateHandlingShort[lateHandling] %> </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="submissions" property="lateHandling_isInstructorEditable" value="true"> x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="submissions" property="lateHandling_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
      (submissions.getSubmissionModel_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Number of Submissions Allowed</td>
    <td>&nbsp;
      <%
    int submissionModel = 0;

    try{
      submissionModel = new Integer(submissions.getSubmissionModel()).intValue();
    }
    catch (Exception e){
      //
    }
    %>
      <%=AllChoices.numberSubmissionsShort[submissionModel] %> </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="submissions" property="submissionModel_isInstructorEditable" value="true"> x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="submissions" property="submissionModel_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
      (submissions.getSubmissionsSaved_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Submissions Saved</td>
    <td>&nbsp;
      <!-- submissionsSavedShort -->
      <logic:equal name="submissions" property="submissionsSaved1" value="on">
        <%-- Save first submission by student: --%>
        <%=AllChoices.submissionsSavedShort[0] %> </logic:equal>
      <logic:equal name="submissions" property="submissionsSaved2" value="on">
        <%-- Save last submission by student: --%>
        <%=AllChoices.submissionsSavedShort[1] %> </logic:equal>
      <logic:equal name="submissions" property="submissionsSaved3" value="on">
        <%-- Save all submissions by student: --%>
        <%=AllChoices.submissionsSavedShort[2] %> </logic:equal>
      <%--
      Save first submission by student:
        <jsp:getProperty name="submissions" property="submissionsSaved1"/>      <BR>

      Save last submission by student:
      <jsp:getProperty name="submissions" property="submissionsSaved2"/>  <BR>

      Save all submissions by student:
      <jsp:getProperty name="submissions" property="submissionsSaved3"/>
--%>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="submissions" property="submissionsSaved_isInstructorEditable" value="true"> x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="submissions" property="submissionsSaved_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>
  <% } %>
  <% } %>
  <%-- end if getSubmissionModel_isInstructorViewable --%>
  <!-- END SUBMISSIONS -->
</table>
<a href="#top">Go to top of page</a>
<% } %>
