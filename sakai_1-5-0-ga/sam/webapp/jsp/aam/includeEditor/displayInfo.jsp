<%--show only to assessment editor if one or more options is inst editable--%>
<% if (display.getLength((String) session.getAttribute("editorRole")) > 0) { %>
<table class="tblMain">
  <tr>
    <td class="tdEditorSide"><a name=display id="display"></a><span class="heading2">Display
        Features</span><br>
      <html:link page="/jsp/aam/edit/display.jsp">Edit</html:link>
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
         (display.getMultiPartAllowed_isInstructorEditable())) { %>
  <% count=0; %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Multi-Part Allowed</td>
    <td><bean:message key='<%= "multipart.allowed." + display.getMultiPartAllowed() %>' bundle="option"/>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="display" property="multiPartAllowed_isInstructorEditable" value="true"> x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="display" property="multiPartAllowed_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (display.getItemAccessType_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Access to Questions</td>
    <td><bean:message key='<%= "item.access." + display.getItemAccessType() %>' bundle="option"/>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="display" property="itemAccessType_isInstructorEditable" value="true"> x </logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="display" property="itemAccessType_isStudentViewable" value="true"> x</logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (display.getDisplayChunking_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Web Page Content</td>
    <td><bean:message key='<%= "display.chunking." + display.getDisplayChunking() %>' bundle="option"/>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="display" property="displayChunking_isInstructorEditable" value="true"> x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="display" property="displayChunking_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (display.getItemBookMarking_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Review List</td>
    <td><bean:message key='<%= "item.bookmarking." + display.getItemBookMarking() %>' bundle="option"/>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="display" property="itemBookMarking_isInstructorEditable" value="true"> x </logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="display" property="itemBookMarking_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>
  <% } %>
</table>
<a href="#top">Go to top of page</a>
<% } %>
