<%--show only to assessment editor if one or more options is inst editable--%>
<% if (access.getLength((String) session.getAttribute("editorRole")) > 0) { %>

<%--
If the access info is in template editor then show the section.
If the access info is in assessment editor only show if instructor visible.
--%>
<% String accessWidth="120"; //sets td with of access groups %>

<table class="tblMain">
  <% if (access.getGroups_isInstructorEditable() ||
    session.getAttribute("editorRole").equals("templateEditor"))
{

%>
  <tr>
    <td class="tdEditorSide" ><a name=access></a><span class="heading2">Access to
        Assessment</span><br />
      <!-- html:link page="/edit/groups.jsp">Edit</html:link -->
      <a href="<%=request.getContextPath()%>/startAccess.do?name=Full+Class">Edit</a>
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
  <% count=0; %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (access.getGroups_isInstructorEditable())) { %>
  <!-- Don't show this part
  <tr class='< %= (count++ % 2==0 ?"trOdd":"trEven") % >'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Release Groups for Access and Feedback</td>
    <td>
      <table border=0 cellpadding=4 cellspacing=2>
        <tr>
          <logic:iterate name="access" property="groups" id="groupItem"
      type="org.navigoproject.business.entity.assessment.model.AccessGroup">
            <logic:equal name="groupItem" property="isActive" value="true">
              <td><bean:write name="groupItem" property="name"/><br>
                <html:link page="/startAccess.do" paramId="name" paramName="groupItem"
            paramProperty="name">Edit Access</html:link>
                <br>
                <img height="1" src="images/spacer.gif" width="<%=accessWidth%>"></td>
            </logic:equal>
          </logic:iterate>
        </tr>
      </table>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="access" property="groups_isInstructorEditable" value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="access" property="groups_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr -->
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (access.getReleaseType_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Release Model</td>
    <td>
      <table border=0>
        <tr>
          <logic:iterate name="access" property="groups" id="groupItem"
      type="org.navigoproject.business.entity.assessment.model.AccessGroup">
            <logic:equal name="groupItem" property="isActive" value="true">
              <td>
                <logic:notEmpty name="groupItem" property="releaseType">
                  <bean:message                    key='<%= "release.type." + groupItem.getReleaseType() %>'
                    bundle="option"/>
                </logic:notEmpty><br>
                <img height="1" src="images/spacer.gif" width="<%=accessWidth%>">
              </td>
            </logic:equal>
          </logic:iterate>
        </tr>
      </table>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="access" property="releaseType_isInstructorEditable" value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="access" property="releaseType_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (access.getReleaseDate_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Release Date</td>
    <td>
      <table border=0>
        <tr>
          <logic:iterate name="access" property="groups" id="groupItem"
      type="org.navigoproject.business.entity.assessment.model.AccessGroup">
            <logic:equal name="groupItem" property="isActive" value="true">
              <td><!-- only show date if the release type is 'on date:' -->
	<logic:equal name="groupItem" property="releaseType" value="1">
          <logic:equal name="editorRole" value="templateEditor">
            <i>Assessment Author sets Date</i><br>
          </logic:equal>
          <logic:equal name="editorRole" value="assessmentEditor">
		<bean:write name="groupItem" property="releaseDate"/><br>
          </logic:equal>
	</logic:equal>
                <img height="1" src="images/spacer.gif" width="<%=accessWidth%>"> </td>
            </logic:equal>
          </logic:iterate>
        </tr>
      </table>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="access" property="releaseDate_isInstructorEditable" value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="access" property="releaseDate_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (access.getRetractType_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Retract Model</td>
    <td>
      <table border=0>
        <tr>
          <logic:iterate name="access" property="groups" id="groupItem"
      type="org.navigoproject.business.entity.assessment.model.AccessGroup">
            <logic:equal name="groupItem" property="isActive" value="true">
              <td>  
               <logic:notEmpty name="groupItem" property="retractType">
                  <bean:message                    key='<%= "retract.type." + groupItem.getRetractType() %>'
                    bundle="option"/>
                </logic:notEmpty><br>                 
                <img height="1" src="images/spacer.gif" width="<%=accessWidth%>"> </td>
            </logic:equal>
          </logic:iterate>
        </tr>
      </table>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="access" property="retractType_isInstructorEditable" value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="access" property="retractType_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (access.getRetractDate_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Retract Date</td>
    <td>
      <table border=0>
        <tr>
          <logic:iterate name="access" property="groups" id="groupItem"
      type="org.navigoproject.business.entity.assessment.model.AccessGroup">
            <logic:equal name="groupItem" property="isActive" value="true">
              <td><!-- only show date if the retract type is 'on date:' -->
		<logic:equal name="groupItem" property="retractType" value="2">
          <logic:equal name="editorRole" value="templateEditor">
            <i>Assessment Author sets Date</i><br>
          </logic:equal>
          <logic:equal name="editorRole" value="assessmentEditor">
                <bean:write name="groupItem" property="retractDate"/><br>
          </logic:equal>
	</logic:equal>
                <img height="1" src="images/spacer.gif" width="<%=accessWidth%>"> </td>
            </logic:equal>
          </logic:iterate>
        </tr>
      </table>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="access" property="retractDate_isInstructorEditable" value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="access" property="retractDate_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (access.getDueDate_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Due Date</td>
    <td>
      <table border=0>
        <tr>
          <logic:iterate name="access" property="groups" id="groupItem"
      type="org.navigoproject.business.entity.assessment.model.AccessGroup">
            <logic:equal name="groupItem" property="isActive" value="true">
              <td>
                <logic:equal name="groupItem" property="dueDateModel" value="0">
                  No Due Date<br>
                </logic:equal>
                <logic:equal name="groupItem" property="dueDateModel" value="1">
          <logic:equal name="editorRole" value="templateEditor">
            <i>Assessment Author sets Date</i><br>
          </logic:equal>
          <logic:equal name="editorRole" value="assessmentEditor">
                <bean:write name="groupItem" property="dueDate"/><br>
          </logic:equal>
                <img height="1" src="images/spacer.gif" width="<%=accessWidth%>"> </td>
                </logic:equal>
            </logic:equal>
          </logic:iterate>
        </tr>
      </table>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="access" property="dueDate_isInstructorEditable" value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="access" property="dueDate_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (access.getIpAccessType_isInstructorEditable())) { %>
  <!-- tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>IP Mask</td>
    <td>
      <table border=0>
        <tr>
          <logic:iterate name="access" property="groups" id="groupItem"
      type="org.navigoproject.business.entity.assessment.model.AccessGroup">
            <logic:equal name="groupItem" property="isActive" value="true">
              <td><bean:write name="groupItem" property="ipAccess"/><br>
                <img height="1" src="images/spacer.gif" width="<%=accessWidth%>"> </td>
            </logic:equal>
          </logic:iterate>
        </tr>
      </table>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="access" property="ipAccessType_isInstructorEditable" value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="access" property="ipAccessType_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr -->
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (access.getPasswordRequired_isInstructorEditable())) { %>
  <!-- tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Password Access</td>
    <td>&nbsp;
      <table border=0>
        <tr>
          <logic:iterate name="access" property="groups" id="groupItem"
      type="org.navigoproject.business.entity.assessment.model.AccessGroup">
            <logic:equal name="groupItem" property="isActive" value="true">
              <td><bean:write name="groupItem" property="passwordAccess"/><br>
                <img height="1" src="images/spacer.gif" width="<%=accessWidth%>"> </td>
            </logic:equal>
          </logic:iterate>
        </tr>
      </table>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="access" property="passwordRequired_isInstructorEditable" value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="access" property="passwordRequired_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr -->
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (access.getRetryAllowed_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Due Date Extension Allowed</td>
    <td>&nbsp;
      <table border=0>
        <tr>
          <logic:iterate name="access" property="groups" id="groupItem"
      type="org.navigoproject.business.entity.assessment.model.AccessGroup">
            <logic:equal name="groupItem" property="isActive" value="true">
              <td><bean:write name="groupItem" property="retryAllowed"/><br>
                <img height="1" src="images/spacer.gif" width="<%=accessWidth%>"> </td>
            </logic:equal>
          </logic:iterate>
        </tr>
      </table>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="access" property="retryAllowed_isInstructorEditable" value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="access" property="retryAllowed_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
      (access.getTimedAssessment_isInstructorEditable())) { %>
  <!-- tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Timed Assessment</td>
    <td>&nbsp;
      <table border=0>
        <tr>
          <logic:iterate name="access" property="groups" id="groupItem"
      type="org.navigoproject.business.entity.assessment.model.AccessGroup">
            <logic:equal name="groupItem" property="isActive" value="true">
              <td><bean:write name="groupItem" property="timedAssessment"/><br>
                <img height="1" src="images/spacer.gif" width="<%=accessWidth%>"> </td>
            </logic:equal>
          </logic:iterate>
        </tr>
      </table>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="access" property="timedAssessment_isInstructorEditable" value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="access" property="timedAssessment_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr -->
  <% } %>

  <% } %>
</table>
<a href="#top">Go to top of page</a> 
<% } %>
