<%--show only to assessment editor if one or more options is inst editable--%>
<% if (notification.getLength((String) session.getAttribute("editorRole")) > 0) { %>
<table class="tblMain">
  <tr>
    <td class="tdEditorSide"><span class="heading2"><a name=notification></a>Notification</span><br>
      <html:link page="/jsp/aam/edit/notification.jsp">Edit</html:link>
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
         (notification.getTesteeNotification_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Student Notification</td>
    <td>
      <jsp:getProperty name="notification" property="testeeNotification1"/>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="notification" property="testeeNotification_isInstructorEditable" value="true"> x </logic:equal>
      </td>
    </logic:equal>
    <td>
      <logic:equal name="notification" property="testeeNotification_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (notification.getInstructorNotification_isInstructorEditable())) { %>
  <% count=0; %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Instructional Staff Notification</td>
    <td>
      <jsp:getProperty name="notification" property="instructorNotification1"/>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="notification" property="instructorNotification_isInstructorEditable" value="true"> x </logic:equal>
      </td>
    </logic:equal>
    <td>
      <logic:equal name="notification" property="instructorNotification_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>
  <% } %>
</table>
<a href="#top">Go to top of page</a>
<% } %>
