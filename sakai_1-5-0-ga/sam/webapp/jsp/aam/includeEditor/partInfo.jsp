<%--show only to assessment editor if one or more options is inst editable--%>
<% if (part.getLength((String) session.getAttribute("editorRole")) > 0) { %>
<%-- display multi-part info if multi-part --%>
<logic:equal name="display" property="multiPartAllowed" value="true">
<table class="tblMain">
  <tr>
      <td class="tdEditorSide" >
        <a name=part></a><span class="heading2">Parts
        Display</span><br>
        <html:link page="/jsp/aam/edit/part.jsp">Edit</html:link>
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
         (part.getName_isInstructorEditable())) { %>
  <% count=0; %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Part Name</td>
    <td><jsp:getProperty name="part" property="name"/>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="part" property="name_isInstructorEditable" value="true"> x</logic:equal>
      </td>
    </logic:equal>
    <td>
      <logic:equal name="part" property="name_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (part.getDescription_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Part Description</td>
    <td><jsp:getProperty name="part" property="description"/>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="part" property="description_isInstructorEditable" value="true"> x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="part" property="description_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (part.getObjectives_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Part Objectives</td>
    <td><jsp:getProperty name="part" property="objectives"/>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td>
        <logic:equal name="part" property="objectives_isInstructorEditable" value="true"> x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="part" property="objectives_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (part.getKeywords_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Part Keywords</td>
    <td><jsp:getProperty name="part" property="keywords"/>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="part" property="keywords_isInstructorEditable"value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="part" property="keywords_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (part.getRubrics_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Part Rubrics</td>
    <td><jsp:getProperty name="part" property="rubrics"/>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="part" property="rubrics_isInstructorEditable"value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="part" property="rubrics_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (part.getQuestionOrder_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Question Order</td>
    <td>
    <bean:message key='<%= "question.order." + part.getQuestionOrder() %>' bundle="option"/>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="part" property="questionOrder_isInstructorEditable"value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="part" property="questionOrder_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (part.getMediaCollection_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide"><html:link page="/startFileUploadPartItem.do?isLink=false">Add Inline Media</html:link><br>
      <% if (part.getMediaCollection().size() > 1) { %>
        <html:link
          page="/startReorderPartItem.do?isLink=false">Reorder Inline Media</html:link>
      <% } %>
    </td>
    <td>Inline Media in All Part Headers</td>
    <td>&nbsp;
  <%-- show related files and links table only if there are items --%>
  <logic:notEmpty name="part" property="mediaCollection">
    <table class="tblMedia">
          <tr>
            <th>&nbsp; </th>
            <th> Title/Author </th>
            <th> File Format </th>
            <th> Date Added </th>
            <td>&nbsp;</td>
          </tr>
          <logic:iterate name="part" id="mediaItem" property="reversedMediaCollection" type="org.navigoproject.business.entity.assessment.model.MediaData" indexId="ctr">
            <tr>
              <td> <img src="<bean:write name='mediaItem' property='iconUrl' />"> </td>
              <td>
                <logic:equal name='mediaItem' property='location' value="">
                  <html:link page="/showMedia.do" target="_blank" paramId="id"
               paramName="mediaItem" paramProperty="mapId"> <bean:write name="mediaItem" property="name" /> </html:link>
                </logic:equal>
                <logic:notEqual name='mediaItem' property='location' value=""> <a href="<bean:write name='mediaItem' property='location' />"
              target="_blank" > <bean:write name="mediaItem" property="name" /> </a> </logic:notEqual>
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
                  part.getMediaCollection_isInstructorEditable()) { %>
              <td nowrap> <a href="<%=request.getContextPath()%>/startFileUploadPart.do?isLink=false&amp;index=<%= ctr.toString() %>"
              >Edit</a><br>
<a href="<%=request.getContextPath()%>/deleteFileUploadPart.do?isLink=false&amp;index=<%= ctr.toString() %>"
              >Remove</a>
                <!-- |
html:link action='moveMedia'
paramId='id' paramName='mediaItem' paramProperty='id' -->
                <!-- Move -->
                <!-- /html:link -->
                <% } %>
            </tr>
          </logic:iterate>
      </table>    
    </logic:notEmpty>
      </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="part" property="mediaCollection_isInstructorEditable" value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="part" property="mediaCollection_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (part.getRelatedMediaCollection_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditorSide"><html:link page="/startFileUploadPartItem.do?isLink=true">Add Related Files and Links</html:link><br>
      <% if (part.getRelatedMediaCollection().size() > 1) { %>
        <html:link
          page="/startReorderPartItem.do?isLink=true">Reorder Related Files and Links</html:link>
      <% } %>
    </td>
    <td>Related Files and Links in All Part Headers</td>
    <td>
  <%-- show related files and links table only if there are items --%>
  <logic:notEmpty name="part" property="relatedMediaCollection">
    <table class="tblMedia">
          <tr>
            <th>&nbsp; </th>
            <th> Title/Author </th>
            <th> File Format </th>
            <th> Date Added </th>
            <td>&nbsp; </td>
          </tr>
          <logic:iterate name="part" id="relatedMediaItem" property="reversedRelatedMediaCollection" type="org.navigoproject.business.entity.assessment.model.MediaData" indexId="rctr">
            <tr>
              <td> <img src="<bean:write name='relatedMediaItem' property='iconUrl' />"> </td>
              <td>
                <logic:equal name='relatedMediaItem' property='location' value="">
                  <html:link page="/showMedia.do" target="_blank" paramId="id"
              paramName="relatedMediaItem" paramProperty="mapId" > <bean:write name="relatedMediaItem" property="name" /> </html:link>
                </logic:equal>
                <logic:notEqual name='relatedMediaItem' property='location' value=""> <a href="<bean:write name='relatedMediaItem' property='location' />"
                target="_blank" > <bean:write name="relatedMediaItem" property="name" /> </a> </logic:notEqual>
              <logic:notEmpty name='relatedMediaItem' property='description'>
                <br><bean:write name='relatedMediaItem' property='description'/><br>
              </logic:notEmpty>
              <logic:notEmpty name='relatedMediaItem' property='author'>
                by <bean:write name='relatedMediaItem' property='author'/>
              </logic:notEmpty>
              </td>
              <td> <bean:write name='relatedMediaItem' property='type' /> </td>
              <td> <bean:write name='relatedMediaItem' property='dateAdded' /> </td>
              <% if (session.getAttribute("editorRole").equals("templateEditor") ||
                  part.getMediaCollection_isInstructorEditable()) { %>
              <td nowrap> <a href="<%=request.getContextPath()%>/startFileUploadPart.do?isLink=true&amp;index=<%= rctr.toString() %>"
              >Edit</a><br>
<a href="<%=request.getContextPath()%>/deleteFileUploadPart.do?isLink=true&amp;index=<%= rctr.toString() %>"
              >Remove</a>
                <!-- |
html:link action='moveMedia'
paramId='id' paramName='mediaItem' paramProperty='id' -->
                <!-- Move -->
                <!-- /html:link -->
                <% } %>
            </tr>
          </logic:iterate>
      </table>    
    </logic:notEmpty>
      </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="part" property="relatedMediaCollection_isInstructorEditable" value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="part" property="relatedMediaCollection_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
</table>
</logic:equal><%-- END: multi-part detail info and links IF multi-part --%>

<%-- if single part grey out --%>
<logic:equal name="display" property="multiPartAllowed" value="false">
<table class="tblgrayedOut">
  <tr>
      <td class="tdEditorSide" >
        <a name=part></a><span class="heading2">Parts
        Display</span>
      </td>
      <td class="tdEditorTopFeature">Feature</td>
      <td class="tdEditorTop">
			<logic:equal name="editorRole" value="templateEditor">
        <strong><font color="#000000">Any Assessments made from this template will not have any Parts and no
          Part Header will be displayed. To allow Parts in Assessments, change
          the value for Multipart Allowed in Display Features.<br>
        If any values for Parts Display was saved before setting Multipart Allowed
        to NO, then that information is saved but not displayed unless Multipart
      Allowed is reset to Yes.</font></strong></logic:equal></td>
      <logic:equal name="editorRole" value="templateEditor">
        <td class="tdEditorEditView"> Instructor<br>
          Editable</td>
      </logic:equal>
      <td class="tdEditorEditView"> Student<br>
      Viewable</td>
  </tr>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (part.getName_isInstructorEditable())) { %>
  <% count=0; %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEvenGray") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Part Name</td>
    <td>No Parts in this <logic:equal name="editorRole" value="templateEditor">Template</logic:equal> <logic:equal name="editorRole" value="assessmentEditor">Assessment</logic:equal></td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="part" property="name_isInstructorEditable" value="true"> x</logic:equal>
      </td>
    </logic:equal>
    <td>
      <logic:equal name="part" property="name_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (part.getDescription_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEvenGray") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Part Description</td>
    <td>No Parts in this <logic:equal name="editorRole" value="templateEditor">Template</logic:equal> <logic:equal name="editorRole" value="assessmentEditor">Assessment</logic:equal>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="part" property="description_isInstructorEditable" value="true"> x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="part" property="description_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (part.getObjectives_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEvenGray") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Part Objectives</td>
    <td>No Parts in this <logic:equal name="editorRole" value="templateEditor">Template</logic:equal> <logic:equal name="editorRole" value="assessmentEditor">Assessment</logic:equal>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td>
        <logic:equal name="part" property="objectives_isInstructorEditable" value="true"> x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="part" property="objectives_isStudentViewable" value="true"> x </logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (part.getKeywords_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEvenGray") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Part Keywords</td>
    <td>No Parts in this <logic:equal name="editorRole" value="templateEditor">Template</logic:equal> <logic:equal name="editorRole" value="assessmentEditor">Assessment</logic:equal>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="part" property="keywords_isInstructorEditable"value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="part" property="keywords_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (part.getRubrics_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEvenGray") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Part Rubrics</td>
    <td>No Parts in this <logic:equal name="editorRole" value="templateEditor">Template</logic:equal> <logic:equal name="editorRole" value="assessmentEditor">Assessment</logic:equal>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="part" property="rubrics_isInstructorEditable"value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="part" property="rubrics_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (part.getQuestionOrder_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEvenGray") %>'>
    <td class="tdEditorSide">&nbsp;</td>
    <td>Question Order</td>
    <td>No Parts in this <logic:equal name="editorRole" value="templateEditor">Template</logic:equal> <logic:equal name="editorRole" value="assessmentEditor">Assessment</logic:equal>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="part" property="questionOrder_isInstructorEditable"value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="part" property="questionOrder_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (part.getMediaCollection_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEvenGray") %>'>
    <td class="tdEditorSide">
    </td>
    <td>Inline Media in All Part Headers</td>
    <td>No Parts in this <logic:equal name="editorRole" value="templateEditor">Template</logic:equal> <logic:equal name="editorRole" value="assessmentEditor">Assessment</logic:equal></td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="part" property="mediaCollection_isInstructorEditable" value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="part" property="mediaCollection_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (part.getRelatedMediaCollection_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEvenGray") %>'>
    <td class="tdEditorSide">
    </td>
    <td>Related Files and Links in All Part Headers</td>
    <td>No Parts in this <logic:equal name="editorRole" value="templateEditor">Template</logic:equal> <logic:equal name="editorRole" value="assessmentEditor">Assessment</logic:equal></td>
    <logic:equal name="editorRole" value="templateEditor">
      <td><logic:equal name="part" property="relatedMediaCollection_isInstructorEditable" value="true">x</logic:equal>
      </td>
    </logic:equal>
    <td><logic:equal name="part" property="relatedMediaCollection_isStudentViewable" value="true">x</logic:equal>
    </td>
  </tr>
  <% } %>
</table>
</logic:equal><%-- end single part --%>
<a href="#top">Go to top of page</a>
<% } %>
