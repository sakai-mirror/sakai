<%--show only to assessment editor if one or more options is inst editable--%>
<% if (distribution.getLength((String) session.getAttribute("editorRole")) > 0) { %>
<table class="tblMain">
  <tr>
    <td class="tdEditorSide" ><span class="heading2"><a name=results></a>Grades Distribution</span><br>
      <html:link page="/jsp/aam/edit/distribution.jsp">Edit</html:link>
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
         (distribution.getToAdmin_isInstructorEditable())) { %>
  <% count=0; %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Result Type to Instructor</td>
    <td>
      <logic:equal name="distribution" property="toAdmin2" value="on">
        <bean:message key="distribution.admin2" bundle="option"/>,
      </logic:equal>
      <logic:equal name="distribution" property="toAdmin1" value="on">
        <bean:message key="distribution.admin1" bundle="option"/>,
      </logic:equal>
      <logic:equal name="distribution" property="toAdmin3" value="on">
        <bean:message key="distribution.admin3" bundle="option"/>,
      </logic:equal>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="distribution" property="toAdmin_isInstructorEditable" value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="distribution" property="toAdmin_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (distribution.getToTestee_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Result Type to Testee</td>
    <td>
      <logic:equal name="distribution" property="toTestee1" value="on">
        <bean:message key="distribution.testee1" bundle="option"/>,
      </logic:equal>
      <logic:equal name="distribution" property="toTestee2" value="on">
        <bean:message key="distribution.testee2" bundle="option"/>,
      </logic:equal>
      <logic:equal name="distribution" property="toTestee3" value="on">
        <bean:message key="distribution.testee3" bundle="option"/>,
      </logic:equal>
      <logic:equal name="distribution" property="toTestee4" value="on">
        <bean:message key="distribution.testee4" bundle="option"/>,
      </logic:equal>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="distribution" property="toTestee_isInstructorEditable" value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="distribution" property="toTestee_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (distribution.getToGradebook_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Result Type to Gradebook</td>
    <td>
      <logic:equal name="distribution" property="toGradebook" value="on">
        <bean:message key="distribution.gradebook1" bundle="option"/>
      </logic:equal>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="distribution" property="toGradebook_isInstructorEditable" value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="distribution" property="toGradebook_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
</table>
<a href="#top">Go to top of page</a>
<% } %>
