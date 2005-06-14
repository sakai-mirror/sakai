<%--show only to assessment editor if one or more options is inst editable--%>
<% if (feedback.getLength((String) session.getAttribute("editorRole")) > 0) { %>
<table class="tblMain">
  <tr>
    <td class="tdEditorSide" ><a name=feedback></a><span class="heading2">Feedback</span><br>
      <html:link page="/jsp/aam/edit/feedback.jsp">Edit</html:link>
    </td>
    <td class="tdEditorTopFeature">Feature</td>
    <td class="tdEditorTop">Value</td>
    <logic:equal name="editorRole" value="templateEditor">
      <td class="tdEditorEditView"> Instructor<br>
        Editable</td>
    </logic:equal>
    <td class="tdEditorEditView"> Student<br>
      Viewable</td>
    <% if (session.getAttribute("editorRole").equals("templateEditor") ||
      (feedback.getFeedbackType_isInstructorEditable())) { %>
    <% count=0; %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Feedback Model for Full Class</td>
    <td>
      <%-- @todo: could be moved out to a resource  --%>
      <!-- FEEDBACK TYPE: <bean:write name="feedback" property="feedbackType" />-->
      <logic:equal name="feedback" property="feedbackType" value="0"> immediate </logic:equal>
      <logic:equal name="feedback" property="feedbackType" value="1"> by Date </logic:equal>
      <logic:equal name="feedback" property="feedbackType" value="2"> no feedback </logic:equal>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="feedback" property="feedbackType_isInstructorEditable" value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="feedback" property="feedbackType_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
      (feedback.getFeedbackReleaseDate_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Feedback Release Date for Full Class</td>
    <td>&nbsp;
      <logic:equal name="feedback" property="feedbackType" value="0"> immediate </logic:equal>
      <logic:equal name="feedback" property="feedbackType" value="1">
        <logic:equal name="feedback" property="datedFeedbackType" value="0"> <bean:write name="feedback" property="feedbackReleaseDate"/> </logic:equal>
        <logic:equal name="feedback" property="datedFeedbackType" value="1"> <bean:write name="feedback" property="feedbackReleaseDate"/> </logic:equal>
      </logic:equal>
      <logic:equal name="feedback" property="feedbackType" value="2">
        <%-- display nothing if value="2" --%>
      </logic:equal>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="feedback" property="feedbackReleaseDate_isInstructorEditable" value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="feedback" property="feedbackReleaseDate_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>

<!-- removed for beta2 because not in mockup, may need to return for v1 
<% if (session.getAttribute("editorRole").equals("templateEditor") ||
      (feedback.getScoreReleaseDate_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Score Release Date for Full Class</td>
    <td>&nbsp;
      <logic:equal name="feedback" property="feedbackType" value="0"> immediate </logic:equal>
      <logic:equal name="feedback" property="feedbackType" value="1">
        <logic:equal name="feedback" property="datedFeedbackType" value="0"> <bean:write name="feedback" property="feedbackReleaseDate"/> </logic:equal>
        <logic:equal name="feedback" property="datedFeedbackType" value="1"> <bean:write name="feedback" property="totalScoreReleaseDate"/> </logic:equal>
      </logic:equal>
      <logic:equal name="feedback" property="feedbackType" value="2">
        <%-- display nothing if value="2" --%>
      </logic:equal>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="feedback" property="scoreReleaseDate_isInstructorEditable" value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="feedback" property="scoreReleaseDate_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
-->
</table>
<a href="#top">Go to top of page</a>
<% } %>
