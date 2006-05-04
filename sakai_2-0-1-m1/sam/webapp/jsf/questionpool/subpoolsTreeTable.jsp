<h:dataTable id="TreeTable" value="#{questionpool.sortedSubqpools}"
	 var="pool" width="100%" styleClass="listHier" >

    <h:column id="col1">
     <f:facet name="header">
      <h:panelGroup>
       <h:commandLink id="sortByTitle" immediate="true"  rendered="#{questionpool.sortSubPoolProperty !='title'}" action="#{questionpool.sortSubPoolByColumnHeader}">
          <f:param name="subPoolOrderBy" value="title"/>
          <f:param name="subPoolAscending" value="true"/>
          <h:outputText  value="#{msg.p_name}"  rendered="#{questionpool.sortSubPoolProperty !='title'}" /> 
       </h:commandLink>
       <h:outputText  value="#{msg.p_name}" styleClass="currentSort" rendered="#{questionpool.sortSubPoolProperty =='title'}" />
       <h:commandLink  immediate="true" rendered="#{questionpool.sortSubPoolProperty =='title' && questionpool.sortSubPoolAscending }"  action="#{questionpool.sortSubPoolByColumnHeader}">
          <f:param name="subPoolOrderBy" value="title"/>           
          <f:param name="subPoolAscending" value="false" />
          <h:graphicImage alt="#{msg.asc}" rendered="#{questionpool.sortSubPoolAscending}" url="/images/sortascending.gif"/>
      </h:commandLink>
      <h:commandLink  immediate="true" rendered="#{questionpool.sortSubPoolProperty =='title' && !questionpool.sortSubPoolAscending }"  action="#{questionpool.sortSubPoolByColumnHeader}">
          <f:param name="subPoolOrderBy" value="title"/>
          <f:param name="subPoolAscending" value="true" />
          <h:graphicImage alt="#{msg.asc}" rendered="#{!questionpool.sortSubPoolAscending}" url="/images/sortdescending.gif"/>
      </h:commandLink>
     </h:panelGroup>   
     </f:facet>

<h:panelGroup styleClass="treetier#{questionpool.tree.currentLevel-questionpool.parentPoolSize-1}"  id="firstcolumn">
<h:inputHidden id="rowid" value="#{questionpool.tree.currentObjectHTMLId}"/>

<h:outputLink id="parenttogglelink"  onclick="toggleRows(this)" value="#" styleClass="treefolder" rendered="#{questionpool.tree.hasChildList}" >
<h:graphicImage id="spacer_for_mozilla" style="border:0" height="14" width="30" value="/images/delivery/spacer.gif" />
</h:outputLink>
<h:outputLink id="togglelink"  styleClass="treedoc" rendered="#{questionpool.tree.hasNoChildList}" >
<h:graphicImage id="spacer_for_mozilla1" style="border:0" height="14" width="30" value="/images/delivery/spacer.gif" />
</h:outputLink>



<h:commandLink id="editlink" immediate="true" action="#{questionpool.editPool}">
  <h:outputText id="poolnametext" value="#{pool.displayName}"/>
  <f:param name="qpid" value="#{pool.questionPoolId}"/>
</h:commandLink>

<f:verbatim><br/></f:verbatim>
<h:graphicImage id="spacer" style="border:0" height="14" width="30" value="/images/delivery/spacer.gif" />
 <f:verbatim><span class="itemAction"></f:verbatim>
<h:commandLink rendered="#{questionpool.importToAuthoring != 'true'}"  id="addlink" immediate="true" action="#{questionpool.addPool}">
  <h:outputText id="add" value="#{msg.add}"/>
  <f:param name="qpid" value="#{pool.questionPoolId}"/>
</h:commandLink>
<h:outputText  rendered="#{questionpool.importToAuthoring != 'true'}" value=" | " />
<h:commandLink rendered="#{questionpool.importToAuthoring != 'true'}" id="copylink" immediate="true" action="#{questionpool.startCopyPool}">
  <h:outputText id="copy" value="#{msg.copy}"/>
  <f:param name="qpid" value="#{pool.questionPoolId}"/>
</h:commandLink>
<h:outputText  rendered="#{questionpool.importToAuthoring != 'true'}" value=" | " />

<h:commandLink rendered="#{questionpool.importToAuthoring != 'true'}"  id="movelink" immediate="true" action="#{questionpool.startMovePool}">
  <h:outputText id="move" value="#{msg.move}"/>
  <f:param name="qpid" value="#{pool.questionPoolId}"/>
</h:commandLink>
<%--
<h:outputText  rendered="#{questionpool.importToAuthoring != 'true'}" value=" | " />
<h:commandLink rendered="#{questionpool.importToAuthoring != 'true'}" id="exportlink" immediate="true" action="#{questionpool.exportPool}">
  <h:outputText id="export" value="#{msg.export}"/>
  <f:param name="qpid" value="#{pool.questionPoolId}"/>
</h:commandLink>
--%>

<h:outputText  rendered="#{questionpool.importToAuthoring != 'true'}" value=" | " />
<h:commandLink rendered="#{questionpool.importToAuthoring != 'true'}"  id="removelink" immediate="true" action="#{questionpool.confirmRemovePool}">
  <h:outputText id="remove" value="#{msg.remove}"/>
  <f:param name="qpid" value="#{pool.questionPoolId}"/>
</h:commandLink>
 <f:verbatim></span></f:verbatim>

</h:panelGroup>
    </h:column>
    <h:column id="col2">
     <f:facet name="header">
    <h:panelGroup>
       <h:commandLink id="sortByOwner" immediate="true"  rendered="#{questionpool.sortSubPoolProperty !='ownerId'}" action="#{questionpool.sortSubPoolByColumnHeader}">
          <f:param name="subPoolOrderBy" value="ownerId"/>
          <f:param name="subPoolAscending" value="true"/>
          <h:outputText  value="#{msg.creator}"  rendered="#{questionpool.sortSubPoolProperty !='ownerId'}" /> 
       </h:commandLink>
       <h:outputText  value="#{msg.creator}" styleClass="currentSort" rendered="#{questionpool.sortSubPoolProperty =='ownerId'}" />
       <h:commandLink  immediate="true" rendered="#{questionpool.sortSubPoolProperty =='ownerId' && questionpool.sortSubPoolAscending }"  action="#{questionpool.sortSubPoolByColumnHeader}">
          <f:param name="subPoolOrderBy" value="ownerId"/>           
          <f:param name="subPoolAscending" value="false" />
          <h:graphicImage alt="#{msg.asc}" rendered="#{questionpool.sortSubPoolAscending}" url="/images/sortascending.gif"/>
      </h:commandLink>
      <h:commandLink  immediate="true" rendered="#{questionpool.sortSubPoolProperty =='ownerId' && !questionpool.sortSubPoolAscending }"  action="#{questionpool.sortSubPoolByColumnHeader}">
          <f:param name="subPoolOrderBy" value="ownerId"/>
          <f:param name="subPoolAscending" value="true" />
          <h:graphicImage alt="#{msg.asc}" rendered="#{!questionpool.sortSubPoolAscending}" url="/images/sortdescending.gif"/>
      </h:commandLink>
     </h:panelGroup>  
     </f:facet>
     <h:panelGroup id="secondcolumn">
        <h:outputText value="#{pool.ownerId}"/>
     </h:panelGroup>
    </h:column>

    <h:column id="col3">
     <f:facet name="header">
      <h:panelGroup>
       <h:commandLink id="sortByLastModified" immediate="true"  rendered="#{questionpool.sortSubPoolProperty !='lastModified'}" action="#{questionpool.sortSubPoolByColumnHeader}">
          <f:param name="subPoolOrderBy" value="lastModified"/>
          <f:param name="subPoolAscending" value="true"/>
          <h:outputText  value="#{msg.last_mod}"  rendered="#{questionpool.sortSubPoolProperty !='lastModified'}" /> 
       </h:commandLink>
       <h:outputText  value="#{msg.last_mod}" styleClass="currentSort" rendered="#{questionpool.sortSubPoolProperty =='lastModified'}" />
       <h:commandLink  immediate="true" rendered="#{questionpool.sortSubPoolProperty =='lastModified' && questionpool.sortSubPoolAscending }"  action="#{questionpool.sortSubPoolByColumnHeader}">
          <f:param name="subPoolOrderBy" value="lastModified"/>           
          <f:param name="subPoolAscending" value="false" />
          <h:graphicImage alt="#{msg.asc}" rendered="#{questionpool.sortSubPoolAscending}" url="/images/sortascending.gif"/>
      </h:commandLink>
      <h:commandLink  immediate="true" rendered="#{questionpool.sortSubPoolProperty =='lastModified' && !questionpool.sortSubPoolAscending }"  action="#{questionpool.sortSubPoolByColumnHeader}">
          <f:param name="subPoolOrderBy" value="lastModified"/>
          <f:param name="subPoolAscending" value="true" />
          <h:graphicImage alt="#{msg.asc}" rendered="#{!questionpool.sortSubPoolAscending}" url="/images/sortdescending.gif"/>
      </h:commandLink>
     </h:panelGroup>   
     </f:facet>
     <h:panelGroup id="thirdcolumn">
        <h:outputText value="#{pool.lastModified}">
          <f:convertDateTime pattern="#{genMsg.output_date_picker}"/>
        </h:outputText>
     </h:panelGroup>
    </h:column>

    <h:column id="col4">
     <f:facet name="header">
      <h:panelGroup>
       <h:commandLink id="sortByQuestion" immediate="true"  rendered="#{questionpool.sortSubPoolProperty !='question'}" action="#{questionpool.sortSubPoolByColumnHeader}">
          <f:param name="subPoolOrderBy" value="question"/>
          <f:param name="subPoolAscending" value="true"/>
          <h:outputText  value="#{msg.qs}"  rendered="#{questionpool.sortSubPoolProperty !='question'}" /> 
       </h:commandLink>
       <h:outputText  value="#{msg.qs}" styleClass="currentSort" rendered="#{questionpool.sortSubPoolProperty =='question'}" />
       <h:commandLink  immediate="true" rendered="#{questionpool.sortSubPoolProperty =='question' && questionpool.sortSubPoolAscending }"  action="#{questionpool.sortSubPoolByColumnHeader}">
          <f:param name="subPoolOrderBy" value="question"/>           
          <f:param name="subPoolAscending" value="false" />
          <h:graphicImage alt="#{msg.asc}" rendered="#{questionpool.sortSubPoolAscending}" url="/images/sortascending.gif"/>
      </h:commandLink>
      <h:commandLink  immediate="true" rendered="#{questionpool.sortSubPoolProperty =='question' && !questionpool.sortSubPoolAscending }"  action="#{questionpool.sortSubPoolByColumnHeader}">
          <f:param name="subPoolOrderBy" value="question"/>
          <f:param name="subPoolAscending" value="true" />
          <h:graphicImage alt="#{msg.asc}" rendered="#{!questionpool.sortSubPoolAscending}" url="/images/sortdescending.gif"/>
      </h:commandLink>
     </h:panelGroup>        
     </f:facet>
     <h:panelGroup id="fourthcolumn">
        <h:outputText value="#{pool.questionSize}"/>
     </h:panelGroup>
    </h:column>


    <h:column id="col5">
     <f:facet name="header">
 <h:panelGroup>
       <h:commandLink id="sortBySubPool" immediate="true"  rendered="#{questionpool.sortSubPoolProperty !='subPoolSize'}" action="#{questionpool.sortSubPoolByColumnHeader}">
          <f:param name="subPoolOrderBy" value="subPoolSize"/>
          <f:param name="subPoolAscending" value="true"/>
          <h:outputText  value="#{msg.subps}"  rendered="#{questionpool.sortSubPoolProperty !='subPoolSize'}" /> 
       </h:commandLink>
       <h:outputText  value="#{msg.subps}" styleClass="currentSort" rendered="#{questionpool.sortSubPoolProperty =='subPoolSize'}" />
       <h:commandLink  immediate="true" rendered="#{questionpool.sortSubPoolProperty =='subPoolSize' && questionpool.sortSubPoolAscending }"  action="#{questionpool.sortSubPoolByColumnHeader}">
          <f:param name="subPoolOrderBy" value="subPoolSize"/>           
          <f:param name="subPoolAscending" value="false" />
          <h:graphicImage alt="#{msg.asc}" rendered="#{questionpool.sortSubPoolAscending}" url="/images/sortascending.gif"/>
      </h:commandLink>
      <h:commandLink  immediate="true" rendered="#{questionpool.sortSubPoolProperty =='subPoolSize' && !questionpool.sortSubPoolAscending }"  action="#{questionpool.sortSubPoolByColumnHeader}">
          <f:param name="subPoolOrderBy" value="subPoolSize"/>
          <f:param name="subPoolAscending" value="true" />
          <h:graphicImage alt="#{msg.asc}" rendered="#{!questionpool.sortSubPoolAscending}" url="/images/sortdescending.gif"/>
      </h:commandLink>
      </h:panelGroup>
     </f:facet>
     <h:panelGroup id="fifthcolumn">
        <h:outputText value="#{pool.subPoolSize}"/>
     </h:panelGroup>
    </h:column>


  </h:dataTable>
