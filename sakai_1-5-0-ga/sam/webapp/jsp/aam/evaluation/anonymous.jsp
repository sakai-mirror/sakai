
    <tr>
      <td width=100%>
        <table width=100%>

          <tr>

            <th>

       <logic:notEqual name="totalScores" property="sortType" value="assessmentResultID">
               <A href="<%=request.getContextPath()%>/totalScores.do?id=<bean:write name='totalScores' property='assessmentId'/>&publishedID=<bean:write name='publishedID'/>&sortType=assessmentResultID">
</logic:notEqual>

Submission ID

<logic:notEqual name="totalScores" property="sortType" value="assessmentResultID">
</A>
</logic:notEqual>
</th>

            <th>
<logic:notEqual name="totalScores" property="sortType" value="lateSubmission">
<A href="<%=request.getContextPath()%>/totalScores.do?id=<bean:write name='totalScores' property='assessmentId'/>&publishedID=<bean:write name='publishedID'/>&sortType=lateSubmission">
</logic:notEqual>
Status
<logic:notEqual name="totalScores" property="sortType" value="lateSubmission">
</A>
</logic:notEqual>
</th>
            <th>
<logic:notEqual name="totalScores" property="sortType" value="totalScore">
<A href="<%=request.getContextPath()%>/totalScores.do?id=<bean:write name='totalScores' property='assessmentId'/>&publishedID=<bean:write name='publishedID'/>&sortType=totalScore">
</logic:notEqual>
Total
<logic:notEqual name="totalScores" property="sortType" value="totalScore">
</A>
</logic:notEqual>
</th>
            <th>
<logic:notEqual name="totalScores" property="sortType" value="adjustmentTotalScore">
<A href="<%=request.getContextPath()%>/totalScores.do?id=<bean:write name='totalScores' property='assessmentId'/>&publishedID=<bean:write name='publishedID'/>&sortType=adjustmentTotalScore">
</logic:notEqual>
 Adj
<logic:notEqual name="totalScores" property="sortType" value="adjustmentTotalScore">
</A>
</logic:notEqual>
</th>
            <th>
<logic:notEqual name="totalScores" property="sortType" value="finalScore">
<A href="<%=request.getContextPath()%>/totalScores.do?id=<bean:write name='totalScores' property='assessmentId'/>&publishedID=<bean:write name='publishedID'/>&sortType=finalScore">
</logic:notEqual>
Final
</A>
</logic:notEqual>
</th>
            <th>
<logic:notEqual name="totalScores" property="sortType" value="totalScoreComments">
<A href="<%=request.getContextPath()%>/totalScores.do?id=<bean:write name='totalScores' property='assessmentId'/>&publishedID=<bean:write name='publishedID'/>&sortType=totalScoreComments">
</logic:notEqual>
Comment
<logic:notEqual name="totalScores" property="sortType" value="totalScoreComments">
</A>
</logic:notEqual>
</th>
          </tr>
 <logic:iterate name="totalScores" id="description" property="agents"
    type="org.navigoproject.business.entity.evaluation.model.AgentResults" indexId="ctr">

      <% if(ctr.intValue() % 2 !=0){%>
      <tr class="altBackground">
       <%}else{%>
<tr>
 <%}%>
        <td>

<!--<html:link page='/asi/grading/xmlSelectAction.do' paramId='assessmentId' paramName='description' paramProperty='assessmentResultID'><bean:write name="description" property="assessmentResultID" />
</html:link>-->
</td>
        <td>
<center>
<span class="red">
<logic:equal name="description" property="lateSubmission" value="1">
All <br>Graded <br>LATE
</logic:equal>
<logic:equal name="description" property="lateSubmission" value="0">
All <br>Graded
</logic:equal>
</span>
</center>

</td>

        <td><bean:write name="description" property="totalScore"/></td>
<%
// Reading autosave settings from SAM.properties
if (org.navigoproject.settings.ApplicationSettings.isEnableAutoSaveForGrading())
{ %>
        <td><html:text  onblur="autoSave()" name="totalScores" size="3"
					property='<%= "agentArray[" + ctr + "].adjustmentTotalScore" %>' /></td>
<%} 
  else {
%>
        <td><html:text  name="totalScores" size="3"
					property='<%= "agentArray[" + ctr + "].adjustmentTotalScore" %>' /></td>
<%}
%>


        <td><bean:write name="totalScores"
						property='<%= "agentArray[" + ctr + "].finalScore" %>'/></td>
        <td>
<%
// Reading autosave settings from SAM.properties
if (org.navigoproject.settings.ApplicationSettings.isEnableAutoSaveForGrading())
{ %>
          <html:textarea  onblur="autoSave()" name="totalScores" cols="50" rows="9"
            property='<%= "agentArray[" + ctr + "].totalScoreComments" %>'/>
/td>
<%}
  else {
%>
          <html:textarea  name="totalScores" cols="50" rows="9"
            styleId='<%= "agentArray[" + ctr + "].totalScoreComments" %>'
            property='<%= "agentArray[" + ctr + "].totalScoreComments" %>'/>
/td>
<%}
%>
<%--          <img src="<%=request.getContextPath()%>/htmlarea/images/htmlarea_editor.gif"
            alt="Toggle Toolbar"
            onClick='<%= "javascript:hideUnhide(\"" +
							"agentArray[" + ctr + "].totalScoreComments" +"\",null)" %>'/>
--%>
					<a href='<%= "javascript:hideUnhide(\"" +
							"agentArray[" + ctr + "].totalScoreComments" +"\",null)" %>'
          	>Show/Hide Editor</a>
        </td>
      </tr>
</logic:iterate>
        </table>
      </td>
</div>
    </tr>



   </table>


