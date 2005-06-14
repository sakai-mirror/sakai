
   <tr>
     <td width=100%>
        <table width=100%>
          <tr>
           <th><logic:notEqual name="questionScores" property="sortType" value="lastName">
                   <A href="<%=request.getContextPath()%>/questionScores.do?id=<bean:write name='questionScores' property='assessmentId'/>&publishedID=<bean:write name='publishedID'/>&sortType=lastName">
                </logic:notEqual>
                 Name
               <logic:notEqual name="questionScores" property="sortType" value="lastName"></A></logic:notEqual>
            </th>
            <th><logic:notEqual name="questionScores" property="sortType" value="sunetid">
<A href="<%=request.getContextPath()%>/questionScores.do?id=<bean:write name='questionScores' property='assessmentId'/>&publishedID=<bean:write name='publishedID'/>&sortType=sunetid">
</logic:notEqual>
UserID
<logic:notEqual name="questionScores" property="sortType" value="sunnetid">
</A></logic:notEqual></th>
            <th><logic:notEqual name="questionScores" property="sortType" value="role">
<A href="<%=request.getContextPath()%>/questionScores.do?id=<bean:write name='questionScores' property='assessmentId'/>&publishedID=<bean:write name='publishedID'/>&sortType=role"></logic:notEqual>
Role
<logic:notEqual name="questionScores" property="sortType" value="submissionDate"></A></logic:notEqual></th>
            <th> <logic:notEqual name="questionScores" property="sortType" value="submissionDate"><A href="<%=request.getContextPath()%>/questionScores.do?id=<bean:write name='questionScores' property='assessmentId'/>&publishedID=<bean:write name='publishedID'/>&sortType=submissionDate">
</logic:notEqual> Date
 <logic:notEqual name="questionScores" property="sortType" value="submissionDate"></A></logic:notEqual></th>

            <th> <logic:notEqual name="questionScores" property="sortType" value="totalScore"><A href="<%=request.getContextPath()%>/questionScores.do?id=<bean:write name='questionScores' property='assessmentId'/>&publishedID=<bean:write name='publishedID'/>&sortType=totalScore"></logic:notEqual>
 Score
 <logic:notEqual name="questionScores" property="sortType" value="totalScore">
</A></logic:notEqual></th>
            <th> <logic:notEqual name="questionScores" property="sortType" value="response"><A href="<%=request.getContextPath()%>/questionScores.do?id=<bean:write name='questionScores' property='assessmentId'/>&publishedID=<bean:write name='publishedID'/>&sortType=response"></logic:notEqual> Student Response
 <logic:notEqual name="questionScores" property="sortType" value="response"></A></logic:notEqual></th>
            <th> <logic:notEqual name="questionScores" property="sortType" value="comment">
<A href="<%=request.getContextPath()%>/questionScores.do?id=<bean:write name='questionScores' property='assessmentId'/>&publishedID=<bean:write name='publishedID'/>&sortType=comment">
</logic:notEqual>
Comment
<logic:notEqual name="questionScores" property="sortType" value="comment"></A></logic:notEqual></th>
          </tr>
 <logic:iterate name="questionScores" id="description" property="agents"
    type="org.navigoproject.business.entity.evaluation.model.AgentResults" indexId="ctr">

 <% if(ctr.intValue() % 2 !=0){%>
      <tr class="altBackground">
       <%}else{%>
<tr>
 <%}%>

            <td> <% out.print("<a name='#");
      %><jsp:getProperty name="description" property="lastInitial" /><%
       out.print("'/>");
      %>

<%-- <html:link page='/asi/grading/xmlSelectAction.do' paramId='assessmentId' paramName='description' paramProperty='assessmentResultID'>--%>
<A href="<%=request.getContextPath()%>/asi/grading/xmlSelectAction.do?assessmentId=<bean:write name='description'
  property='assessmentResultID'/>&publishedID=<bean:write name='publishedID'/>">
<bean:write name="description" property="lastName" />, <bean:write name="description" property="firstName"/>
</A>      </a></td>
      <td><bean:write name="description" property="sunetid"/></td>
      <td><bean:write name="description" property="role"/></td>
      <td><bean:write name="description" property="submissionDate"/></td>
      <td>
<%
// Reading autosave settings from SAM.properties
if (org.navigoproject.settings.ApplicationSettings.isEnableAutoSaveForGrading())
{ %>
        <html:text  onblur="autoSave()" name="questionScores" size="3"
        property='<%= "agentArray[" + ctr + "].totalScore" %>' />
<%} 
  else {
%>
        <html:text  name="questionScores" size="3"
        property='<%= "agentArray[" + ctr + "].totalScore" %>' />
<%}
%>

      </td>
      <td>
  <%-- first, display the text, if any --%>
    <bean:write name="description" property="response"/>
  <%-- then, if needed, do special handling for this type --%>
  <logic:equal name="questionScores" property="questionType" value="audio">
    <br>
    <A href='javascript:void()' onClick='window.open("<%=request.getContextPath()%>/jsp/aam/applet/soundRecorder_captured.jsp?filename=audioFileName&seconds=audioSeconds&limit=audioLimit&app=audioAppName&dir=audioDir&imageUrl=audioImageURL","LoadingAudioFile","width=400,height=300,scrollbars=yes,toolbar=no,resizable=no,menubar=no,status=no")'>Recorded Answer</A>
  </logic:equal>

  <logic:equal name="questionScores" property="questionType" value="file upload">
    <A href='javascript:void()' onClick='window.open("<%=request.getContextPath()%>/Login.do","upload","width=600,height=600,scrollbars=yes,toolbar=no,resizable=no,menubar=no,status=no")'>downloadingFile.txt</A>
  </logic:equal>

  <logic:equal name="questionScores" property="questionType" value="multimedia">
    WYSIWYG
  </logic:equal>

      </td>
      <td>
<%
// Reading autosave settings from SAM.properties
if (org.navigoproject.settings.ApplicationSettings.isEnableAutoSaveForGrading())
{ %>
          <html:textarea  onblur="autoSave()" name="questionScores"
            property='<%= "agentArray[" + ctr + "].comments" %>'
            cols="30" rows="9"/>
<%}
  else {
%>
          <html:textarea  name="questionScores"
            property='<%= "agentArray[" + ctr + "].comments" %>'
            styleId='<%= "agentArray[" + ctr + "].comments" %>'
            cols="30" rows="9"/>
<%}
%>

<%--          <img src="<%=request.getContextPath()%>/htmlarea/images/htmlarea_editor.gif"
            alt="Toggle Toolbar"
            onClick='<%= "javascript:hideUnhide(\"" +
							"agentArray[" + ctr + "].comments" +"\",null)" %>'/>
--%>
						<a href='<%= "javascript:hideUnhide(\"" +
							"agentArray[" + ctr + "].comments" +"\",null)" %>'
    	      	>Show/Hide Editor</a>

      </td>
    </tr>
</logic:iterate>
     </table>
   </td>
 </tr>

</table>
