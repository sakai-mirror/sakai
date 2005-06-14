<h:dataTable id="TreeTable" value="#{questionpool.sortedSubqpools}"
	 var="pool" width="100%" headerClass="regHeading" >

    <h:column id="col1">

     <f:facet name="header">
       <h:outputText id="c1header" value="#{msg.p_name}"/>
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


</h:panelGroup>
    </h:column>

    <h:column id="col2">
     <f:facet name="header">
       <h:outputText id="c2header" value="#{msg.creator}"/>
     </f:facet>
     <h:panelGroup id="secondcolumn">
        <h:outputText value="#{pool.ownerId}"/>
     </h:panelGroup>
    </h:column>

    <h:column id="col3">
     <f:facet name="header">
       <h:outputText id="c3header" value="#{msg.last_mod}"/>
     </f:facet>
     <h:panelGroup id="thirdcolumn">
        <h:outputText value="#{pool.lastModified}"/>
     </h:panelGroup>
    </h:column>

    <h:column id="col4">
     <f:facet name="header">
       <h:outputText value="#{msg.qs}"/>
     </f:facet>
     <h:panelGroup id="fourthcolumn">
        <h:outputText value="#{pool.questionSize}"/>
     </h:panelGroup>
    </h:column>


    <h:column id="col5">
     <f:facet name="header">
       <h:outputText value="#{msg.subps}"/>
     </f:facet>
     <h:panelGroup id="fifthcolumn">
        <h:outputText value="#{pool.subPoolSize}"/>
     </h:panelGroup>
    </h:column>


  </h:dataTable>

