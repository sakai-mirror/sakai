<div> 
<table width="100%">

 <tr>
   <!-- specifying at least 1 width keeps columns constant
   despite changes in visible content -->
   <td width="100%">
   </td>
 </tr>

 <logic:iterate name="subpoolTree" property="sortedObjects" id="pool" type="org.navigoproject.business.entity.questionpool.model.QuestionPool" indexId="ctr">
   <% subpoolTree.setCurrentId(pool.getId()); %>
   <logic:empty name="subpoolTree" property="parent">
     <tr id="<%= subpoolTree.getCurrentObjectHTMLId() %>" >
   </logic:empty>
   <logic:notEmpty name="subpoolTree" property="parent">
     <tr id="<%= subpoolTree.getCurrentObjectHTMLId() %>" >
   </logic:notEmpty>

     <td id="p<%= (ctr.intValue() * 3) + 1 %>">
       <div id="p<%= (ctr.intValue() * 3) + 2 %>" 
         class="tier<%= subpoolTree.getCurrentLevel() %>" >
        <html:multibox property="destPools">
                <bean:write name="pool" property="id"/>
        </html:multibox>
         <logic:empty name="subpoolTree" property="childList">
           <a id="p<%= (ctr.intValue() * 3) + 3 %>" class="doc"></a>
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
</div>

<p></p>
