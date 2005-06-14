<logic:notEmpty name="questionpool" property="currentPool">
<html:form action="startQuestionAction.do" method="post">

<h2># of Questions in
<bean:write name="thisPool" property = "name" />: (
<bean:write name="thisPool" property = "numberOfQuestions" />)
<input type="button" value="Add"  onclick="document.location='<%=request.getContextPath()%>/startAddQuestion.do'">
<input type="button" value="Import"  onclick="document.location='<%=request.getContextPath()%>/startImportQuestion.do'">
</h2>
<div class="h2unit">

<html:submit property="method" >
<bean:message key="button.addToAssessment"/>
</html:submit>

<html:submit property="method" >
<bean:message key="button.copy"/>
</html:submit>

<html:submit property="method" >
<bean:message key="button.move"/>
</html:submit>

<html:submit property="method" >
<bean:message key="button.remove"/>
</html:submit>

<html:submit property="method" >
<bean:message key="button.export"/>
</html:submit>
<!-- reuse the same property names as in poolTreeTable.jsp -->


<table class="tblMain">
  <tr>
        <th class="altBackground" width="5%">
  <html:checkbox  onclick="this.value=checkAll(this.form.selectedQuestions)"  property="allItemsSelected"/>
   </th>
    <th class="altBackground" width="35%">Question Text</th>
    <th class="altBackground"  width="30%">Question Type</th>
    <th class="altBackground" width="30%">&nbsp;</th>
  </tr>
  <logic:iterate name="questionpool" id="question" property="currentPool.properties.questions"
    type="osid.assessment.Item" indexId="ctr">
  <bean:define name="question" id="qprops" property="data" />
  <tr class='<%= (ctr.intValue() % 2==0 ?"trEven":"trOdd") %>'>
    <td>
       <html:multibox property="selectedQuestions">
                <bean:write name="question" property="id"/>
        </html:multibox>
    </td>
<td><html:link page="/asi/author/item/itemAction.do?target=questionpool&action=Modify" paramName="question" paramProperty="id" paramId="ItemIdent">
<bean:write name="qprops" property="itemText" filter="false"/>
</html:link>
</td>

    <td><bean:write name="qprops" property="itemType" /></td>
    <td>
<input type="button" value="Preview"  onclick="javascript:PopupWin('<%=request.getContextPath()%>/asi/author/item/itemAction.do?action=Preview&amp;ItemIdent=<%=question.getId()%>')"  >
<input type="button" value="Summary"  onclick="document.location='<%=request.getContextPath()%>/questionChooser.do'">

</td>
  </tr>
  </logic:iterate>
</table>
<br>
</html:form>
</div>
</logic:notEmpty>


