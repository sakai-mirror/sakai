<table width="100%">

 <tr>
   <!-- specifying at least 1 width keeps columns constant
   despite changes in visible content -->
   <td width="5%" > 
   </td>
   <td width="95%"></td>
 </tr>

 <logic:iterate name="subpoolTree" property="sortedObjects" id="pool" type="org.navigoproject.business.entity.questionpool.model.QuestionPool" indexId="ctr">
   <% subpoolTree.setCurrentId(pool.getId()); %>
   <logic:empty name="subpoolTree" property="parent">
     <tr id="<%= subpoolTree.getCurrentObjectHTMLId() %>" class="tblMidList">
   </logic:empty>
   <logic:notEmpty name="subpoolTree" property="parent">
     <tr id="<%= subpoolTree.getCurrentObjectHTMLId() %>" class="a">
   </logic:notEmpty>
   <td>
        <html:radio property="destPool" value="id" idName="pool">
        </html:radio>
   </td>

     <td id="p<%= (ctr.intValue() * 3) + 1 %>">
       <div id="p<%= (ctr.intValue() * 3) + 2 %>" 
         class="tier<%= subpoolTree.getCurrentLevel() %>" >
         <logic:empty name="subpoolTree" property="childList">
           <a id="p<%= (ctr.intValue() * 3) + 3 %>" 
              class="doc"></a>
         </logic:empty>
         <logic:notEmpty name="subpoolTree" property="childList">
           <a id="p<%= (ctr.intValue() * 3) + 3 %>"
             href="#" onclick="toggleRows(this)" class="folder"></a>
         </logic:notEmpty>
         <bean:write name="pool" property="displayName" />
       </div>
     </td>
   </tr>
 </logic:iterate>

</table>
