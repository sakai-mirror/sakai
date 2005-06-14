<%--show only to assessment editor if one or more options is inst editable--%>
<% if (description.getLength((String) session.getAttribute("editorRole")) > 0) { %>
<table width="100%" class="tblMain">
  <tr>
    <td class="tdEditorSide"><a name="description"></a><span class="heading2">Descriptive<br>
      Information</span><br>
      <html:link page="/jsp/aam/edit/description.jsp">Edit</html:link>
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
  <%-- display template name and description for template --%>
  <% if (session.getAttribute("editorRole").equals("templateEditor")  ) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Template Name</td>
    <td><jsp:getProperty name="description" property="templateName"/>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="description" property="templateName_isInstructorViewable" value="true"> x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="description" property="templateName_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Template Description</td>
    <td><jsp:getProperty name="description" property="templateDescription"/>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="description" property="templateDescription_isInstructorViewable" value="true"> x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="description" property="templateDescription_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("assessmentEditor") &&
         (description.getName_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Assessment Name</td>
    <td><jsp:getProperty name="description" property="name"/>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="description" property="name_isInstructorEditable" value="true"> x </logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="description" property="name_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("assessmentEditor") &&
         (description.getDescription_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Assessment Description</td>
    <td><jsp:getProperty name="description" property="description"/>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="description" property="description_isInstructorEditable"value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="description" property="description_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
  <%-- display template name and description for assessment, if viewable --%>
  <% if (session.getAttribute("editorRole").equals("assessmentEditor") &&
         (description.getTemplateName_isInstructorViewable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Template Name</td>
    <td><jsp:getProperty name="description" property="templateName"/>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="description" property="templateName_isInstructorEditable" value="true"> x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="description" property="templateName_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("assessmentEditor") &&
         (description.getTemplateDescription_isInstructorViewable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Template Description</td>
    <td><jsp:getProperty name="description" property="templateDescription"/>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="description" property="templateDescription_isInstructorEditable" value="true"> x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="description" property="templateDescription_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (description.getAssessmentType_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Assessment Type</td>
    <td><jsp:getProperty name="description" property="assessmentType"/>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="description" property="assessmentType_isInstructorEditable" value="true">x </logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="description" property="assessmentType_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (description.getObjectives_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Assessment Objectives</td>
    <td><jsp:getProperty name="description" property="objectives"/>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="description" property="objectives_isInstructorEditable" value="true"> x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="description" property="objectives_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (description.getKeywords_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Assessment Keywords</td>
    <td><jsp:getProperty name="description" property="keywords"/>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="description" property="keywords_isInstructorEditable" value="true"> x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="description" property="keywords_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (description.getRubrics_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Assessment Rubrics</td>
    <td><jsp:getProperty name="description" property="rubrics"/>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="description" property="rubrics_isInstructorEditable" value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="description" property="rubrics_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (description.getMediaCollection_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide"><a href="<%=request.getContextPath()%>/startFileUploadDescription.do?isLink=false"
              onclick="document.forms[0].submit()" />Add Inline Media</a><br>
    <%-- probably should try something like: logic:greaterThan name="description" property="mediaCollection" parameter="size" value="1"--%>
    <% if (description.getMediaCollection().size() > 1) { %>
      <a href="<%=request.getContextPath()%>/startReorderDescription.do?isLink=false">Reorder Inline Media</a>
    <% } %>
    <%--/logic:greaterThan--%>
</td>
    <td>Inline Media in Assessment Header</td>
    <td>
  <%-- show inline media table only if there are items --%>
  <logic:notEmpty name="description" property="mediaCollection">
  <table class="tblMedia">
          <tr>
            <th>&nbsp; </th>
            <th>Title/Author</th>
            <th>File Format</th>
            <th>Date Added</th>
<%-- Ganapathy was working on some changes:
<td><html:image value="New File" src="../images/NewFile.gif" property="NewFileNoLink"/></td>
--%>
            <td>
            </td>
          </tr>
          <logic:iterate name="description" id="mediaItem" property="reversedMediaCollection" type="org.navigoproject.business.entity.assessment.model.MediaData" indexId="ctr">
            <tr>
              <td> <img src="<bean:write name='mediaItem' property='iconUrl' />"> </td>
              <td>
               <logic:equal name='mediaItem' property='location' value="">
                  <html:link page="/showMedia.do" target="_blank" paramId="id"
                    paramName="mediaItem"
                    paramProperty="mapId"><bean:write name="mediaItem" property="name" /> </html:link>
               </logic:equal>
               <logic:notEqual name='mediaItem' property='location' value="">
                 <a href="<bean:write name='mediaItem' property='location' />"
                  target="_blank" ><bean:write name="mediaItem" property="name" /></a>
              </logic:notEqual>
              <logic:notEmpty name='mediaItem' property='description'>
                <br><bean:write name='mediaItem' property='description'/><br>
              </logic:notEmpty>
              <logic:notEmpty name='mediaItem' property='author'>
                by <bean:write name='mediaItem' property='author'/>
              </logic:notEmpty>
              </td>
              <td> <bean:write name='mediaItem' property='type' /> </td>
              <td> <bean:write name='mediaItem' property='dateAdded' /> </td>
           <% if (session.getAttribute("editorRole").equals("templateEditor") ||
                  description.getMediaCollection_isInstructorEditable()) { %>
              <td nowrap>
<%-- Ganapathy was working on some changes:
<html:image value="Edit" src="../images/Edit.gif" property="...EditFileNoLink...+ctr.toString()..." />
<html:image value="Delete" src="../images/Delete.gif" property="...DeleteFileNoLink...+ctr.toString()..." />
--%>

                <a href="<%=request.getContextPath()%>/startFileUploadDescription.do?isLink=false&index=<%= ctr.toString() %>"
                   >Edit</a> |
                <a href="<%=request.getContextPath()%>/deleteFileUploadDescription.do?isLink=false&index=<%= ctr.toString() %>"
                   >Remove</a>
              <% } %>
            </tr>
          </logic:iterate>
</table>
</logic:notEmpty>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="description" property="mediaCollection_isInstructorEditable" value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="description" property="mediaCollection_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (description.getRelatedMediaCollection_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide"><a href="<%=request.getContextPath()%>/startFileUploadDescription.do?isLink=true"
                 >Add Related Files and Links</a><br>
   <% if (description.getRelatedMediaCollection().size() > 1) { %>
        <a href="<%=request.getContextPath()%>/startReorderDescription.do?isLink=true">Reorder Related Files and Links</a>
    <% } %>
          <!--|  html:link action='moveMedia' paramId='id' paramName='relatedMediaItem' paramProperty='id' -->
        <!-- Move -->
        <!-- /html:link -->
  </td>
    <td>Related Files and Links in Assessment Header</td>
    <td>
  <%-- show related files and links table only if there are items --%>
  <logic:notEmpty name="description" property="relatedMediaCollection">
  <table class="tblMedia">
          <tr>
            <th>&nbsp; </th>
            <th>Title/Author</th>
            <th>File Format</th>
            <th>Date Added</th>
<!-- Ganapathy was working on some changes:
<td><html:image value="New File" src="../images/NewFile.gif" property="NewFileNoLink"/></td>
-->
            <td>
            </td>
          </tr>
          <logic:iterate name="description" id="relatedMediaItem" property="reversedRelatedMediaCollection" type="org.navigoproject.business.entity.assessment.model.MediaData" indexId="rctr">
            <tr>
              <td> <img src="<bean:write name='relatedMediaItem' property='iconUrl' />"> </td>
              <td>
               <logic:equal name='relatedMediaItem' property='location' value="">
                  <html:link page="/showMedia.do" target="_blank" 
                    paramId="id" paramName="relatedMediaItem"
                    paramProperty="mapId"><bean:write name="relatedMediaItem"
                    property="name" />
                  </html:link>
               </logic:equal>
               <logic:notEqual name='relatedMediaItem' property='location' value="">
                 <a href="<bean:write name='relatedMediaItem' property='location' />"
                  target="_blank"><bean:write
                  name="relatedMediaItem" property="name" />
                </a>
               </logic:notEqual>
              <logic:notEmpty name='relatedMediaItem' property='description'>
                <br><bean:write name='relatedMediaItem' property='description'/><br>
              </logic:notEmpty>
              <logic:notEmpty name='relatedMediaItem' property='author'>
                by <bean:write name='relatedMediaItem' property='author'/>
              </logic:notEmpty>
              </td>
              <td><bean:write name='relatedMediaItem' property='type' /> </td>
              <td> <bean:write name='relatedMediaItem' property='dateAdded' /> </td>
           <% if (session.getAttribute("editorRole").equals("templateEditor") ||
                  description.getMediaCollection_isInstructorEditable()) { %>
              <td nowrap>
<%-- Ganapathy was working on some changes:
<html:image value="Edit" src="../images/Edit.gif" property="...EditFileNoLink...ctr.toString()..." />
<html:image value="Delete" src="../images/Delete.gif" property="...DeleteFileNoLink...ctr.toString()..." />
--%>
                <a href="<%=request.getContextPath()%>/startFileUploadDescription.do?isLink=true&index=<%= rctr.toString() %>"
                  >Edit</a>
               |
                <a href="<%=request.getContextPath()%>/deleteFileUploadDescription.do?isLink=true&index=<%= rctr.toString() %>"
                   >Remove</a>

              <% } %>
            </tr>
          </logic:iterate>
        </table>
    </logic:notEmpty>
  </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="description" property="relatedMediaCollection_isInstructorEditable" value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="description" property="relatedMediaCollection_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
</table>
<a href="#top">Go to top of page</a>
<% } %>
