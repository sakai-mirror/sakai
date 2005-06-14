<html:form action="startPoolAction.do" method="post">

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
<br>


<div style="border: 1px #000;">
  <table border="0" width="100%">
    <tr> 
<!-- specifying at least 1 width keeps columns constant despite changes in visible content -->
      <th width="30%" class="altBackground"><html:checkbox  onclick="this.value=checkAll(this.form.selectedPools)"  property="allPoolsSelected"/> 
        Pool Name</th>
      <th width="25%" class="altBackground">Creator</th>
      <th width="15%" class="altBackground">Last Modified</th>
      <th width="10%" class="altBackground"># of Questions</th>
      <th width="10%" class="altBackground"># of Subpools</th>
      <th width="10%" class="altBackground">&nbsp;</th>
    </tr>
    <logic:iterate name="subpoolTree" property="sortedObjects" id="pool" type="org.navigoproject.business.entity.questionpool.model.QuestionPool" indexId="ctr"> 
    <% subpoolTree.setCurrentId(pool.getId()); %>
    <logic:empty name="subpoolTree" property="parent"> 
    <tr id="<%= subpoolTree.getCurrentObjectHTMLId() %>" > 
    </logic:empty> <logic:notEmpty name="subpoolTree" property="parent"> 
    <tr id="<%= subpoolTree.getCurrentObjectHTMLId() %>" > 
    </logic:notEmpty> 
    <td id="p<%= (ctr.intValue() * 3) + 1 %>"> 
      <div id="p<%= (ctr.intValue() * 3) + 2 %>" 
         class="tier<%= subpoolTree.getCurrentLevel() %>" > <html:multibox property="selectedPools"> 
        <bean:write name="pool" property="id"/> </html:multibox> <logic:empty name="subpoolTree" property="childList"> 
        <a id="p<%= (ctr.intValue() * 3) + 3 %>" class="doc" >
<!-- need this following line for Mozilla -->
<img border="0" width="17" src="../images/spacer.gif">
</a> 
        </logic:empty> <logic:notEmpty name="subpoolTree" property="childList"> 
        <a name="p<%= (ctr.intValue() * 3) + 3 %>" id="p<%= (ctr.intValue() * 3) + 3 %>"
             href="#<%=pool.getId()%>" onclick="toggleRows(this)" class="folder">
<!-- need this following line for Mozilla -->
<img border="0" width="17" src="../images/spacer.gif">
</a> </logic:notEmpty> 
        <html:link page="/startCreatePool.do?use=edit" paramName="pool" paramProperty="id" paramId="id"> 
        <bean:write name="pool" property="displayName" /> </html:link> </div></td>
    <logic:iterate name="subpoolTree" property="currentObjectProperties"
       id="props" indexId="propctr"> 
    <td> <logic:equal name="propctr" value="1"> <bean:write name="props" format="MM/dd/yyyy" /> 
      </logic:equal> <logic:equal name="propctr" value="2"> <logic:equal name="props" value="0"> 
      -- </logic:equal> <logic:notEqual name="props" value="0"> <bean:write name="props" /> 
      </logic:notEqual> </logic:equal> <logic:equal name="propctr" value="3"> 
      <logic:equal name="props" value="0"> -- </logic:equal> <logic:notEqual name="props" value="0"> 
      <bean:write name="props" /> </logic:notEqual> </logic:equal> <logic:equal name="propctr" value="0"> 
      <bean:write name="props" /> </logic:equal> </td>
    </logic:iterate> 
    <td> <input type="button" value="Add Subpool"  onclick="document.location='<%=request.getContextPath()%>/startCreatePool.do?id=<%=subpoolTree.getCurrentId().toString()%>&use=createsub&pid=<%=subpoolTree.getCurrentId().toString()%>'"> 
    </td>
    </tr>
    </logic:iterate> 
  </table>
</div>

</html:form>
