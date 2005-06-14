<%--show only to assessment editor if one or more options is inst editable--%>
<% if (question.getLength((String) session.getAttribute("editorRole")) > 0) { %>
<table class="tblMain">
  <tr>
    <td class="tdEditorSide"><a name=question></a><span class="heading2">Question
        Display</span><br>
      <html:link page="/jsp/aam/edit/question.jsp">Edit</html:link>
    </td>
    <td class="tdEditorTop">Feature</td>
    <logic:equal name="editorRole" value="templateEditor">
      <td class="tdEditorEditView"> Instructor<br>
        Editable</td>
    </logic:equal>
    <td class="tdEditorEditView"> Student<BR>
      Viewable</td>
  </tr>
  <% count=0; %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide"><span class="heading3">Question Components</span></td>
    <td>Value <span class="textSmall">(How many points the question is worth)</span></td>
    <logic:equal name="editorRole" value="templateEditor">
      <td>
        <logic:equal name="question" property="value_isInstructorEditable" value="true"> x</logic:equal>
      </td>
    </logic:equal>
    <td>
      <logic:equal name="question" property="value_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Question Feedback</td>
    <logic:equal name="editorRole" value="templateEditor">
      <td>
        <logic:equal name="question" property="feedback_isInstructorEditable" value="true"> x</logic:equal>
      </td>
    </logic:equal>
    <td>
      <logic:equal name="question" property="feedback_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>  
	<tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Answer Feedback<br>
      <span class="textSmall">(for  multiple choice questions)</span></td>
    <logic:equal name="editorRole" value="templateEditor">
      <td>
        <logic:equal name="question" property="afeedback_isInstructorEditable" value="true"> x</logic:equal>
      </td>
    </logic:equal>
    <td>
      <logic:equal name="question" property="afeedback_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Hint </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td>
        <logic:equal name="question" property="hint_isInstructorEditable" value="true"> x</logic:equal>
      </td>
    </logic:equal>
    <td>
      <logic:equal name="question" property="hint_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide"><span class="heading3">Question Metadata</span></td>
    <td> Question Title</td>
    <logic:equal name="editorRole" value="templateEditor">
      <td>
        <logic:equal name="question" property="name_isInstructorEditable" value="true"> x</logic:equal>
      </td>
    </logic:equal>
    <td>
      <logic:equal name="question" property="name_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>
  
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Objectives </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td>
        <logic:equal name="question" property="objectives_isInstructorEditable" value="true"> x</logic:equal>
      </td>
    </logic:equal>
    <td>
      <logic:equal name="question" property="objectives_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Keywords</td>
    <logic:equal name="editorRole" value="templateEditor">
      <td>
        <logic:equal name="question" property="keywords_isInstructorEditable" value="true"> x</logic:equal>
      </td>
    </logic:equal>
    <td>
      <logic:equal name="question" property="keywords_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Rubrics </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td>
        <logic:equal name="question" property="rubrics_isInstructorEditable" value="true"> x</logic:equal>
      </td>
    </logic:equal>
    <td>
      <logic:equal name="question" property="rubrics_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>
  <!-- End of part/question metadata for template editor -->
</table>
<a href="#top">Go to top of page</a>
<% } %>
