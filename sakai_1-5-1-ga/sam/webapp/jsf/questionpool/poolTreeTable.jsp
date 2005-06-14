<h:dataTable id="TreeTable" value="#{questionpool.qpools}"
    var="pool" width="100%" headerClass="regHeading" >

    <h:column id="col1">

     <f:facet name="header">
       <h:commandLink id="sortByTitle" immediate="true" action="#{questionpool.sortByColumnHeader}" >
          <h:outputText id="c1header" value="#{msg.p_name}"/>
          <f:param name="orderBy" value="title"/>
        </h:commandLink>

     </f:facet>

<h:panelGroup styleClass="treetier#{questionpool.tree.currentLevel}"  id="firstcolumn">
<h:inputHidden id="rowid" value="#{questionpool.tree.currentObjectHTMLId}"/>
<h:outputLink id="parenttogglelink"  onclick="toggleRows(this)" value="#" styleClass="treefolder" rendered="#{questionpool.tree.hasChildList}" >
<h:graphicImage id="spacer_for_mozilla" style="border:0" height="14" width="30" value="/images/delivery/spacer.gif" />
</h:outputLink>
<h:outputLink id="togglelink"  value="#" styleClass="treedoc" rendered="#{questionpool.tree.hasNoChildList}" >
<h:graphicImage id="spacer_for_mozilla1" style="border:0" width="30" height="14"  value="/images/delivery/spacer.gif" />
</h:outputLink>

<h:commandLink id="editlink" immediate="true" action="#{questionpool.editPool}">
  <h:outputText id="poolnametext" value="#{pool.displayName}"/>
  <f:param name="qpid" value="#{pool.questionPoolId}"/>
</h:commandLink>

<f:verbatim><br/></f:verbatim>
<h:graphicImage id="spacer" style="border:0" width="30" height="14" value="/images/delivery/spacer.gif" />

<h:commandLink rendered="#{questionpool.importToAuthoring != 'true'}"  styleClass="treetier#{questionpool.tree.currentLevel}" id="addlink" immediate="true" action="#{questionpool.addPool}">
  <h:outputText id="add" value="#{msg.add}"/>
  <f:param name="qpid" value="#{pool.questionPoolId}"/>
</h:commandLink>
<h:outputText rendered="#{questionpool.importToAuthoring != 'true'}" value=" | " />
<h:commandLink rendered="#{questionpool.importToAuthoring != 'true'}" id="copylink" immediate="true" action="#{questionpool.startCopyPool}">
  <h:outputText id="copy" value="#{msg.copy}"/>
  <f:param name="qpid" value="#{pool.questionPoolId}"/>
</h:commandLink>
<h:outputText rendered="#{questionpool.importToAuthoring != 'true'}" value=" | " />

<h:commandLink rendered="#{questionpool.importToAuthoring != 'true'}" id="movelink" immediate="true" action="#{questionpool.startMovePool}">
  <h:outputText id="move" value="#{msg.move}"/>
  <f:param name="qpid" value="#{pool.questionPoolId}"/>
</h:commandLink>

<%--
<h:outputText value=" | " />

<h:commandLink id="exportlink" immediate="true" action="#{questionpool.exportPool}">
  <h:outputText id="export" value=""/>
  <f:param name="qpid" value="#{pool.questionPoolId}"/>
</h:commandLink>
--%>

</h:panelGroup>
    </h:column>

    <h:column id="col2">
     <f:facet name="header">
        <h:commandLink id="sortByOwner" immediate="true" action="#{questionpool.sortByColumnHeader}">
          <h:outputText id="c2header" value="#{msg.creator}"/>
          <f:param name="orderBy" value="ownerId"/>
        </h:commandLink>

     </f:facet>
     <h:panelGroup id="secondcolumn">
        <h:outputText value="#{pool.ownerId}"/>
     </h:panelGroup>
    </h:column>

    <h:column id="col3">
     <f:facet name="header">
        <h:commandLink id="sortByLastModified" immediate="true" action="#{questionpool.sortByColumnHeader}" >
          <h:outputText id="c3header" value="#{msg.last_mod}"/>
          <f:param name="orderBy" value="lastModified"/>
        </h:commandLink>
     </f:facet>
     <h:panelGroup id="thirdcolumn">
        <h:outputText value="#{pool.lastModified}">
          <f:convertDateTime pattern="#{genMsg.output_date}"/>
	</h:outputText>
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

    <h:column id="col6" rendered="#{questionpool.importToAuthoring == 'false'}" >
     <f:facet name="header">
       <h:outputText value="#{msg.remove_chbox}"/>
     </f:facet>
<h:selectManyCheckbox id="removeCheckbox" value ="#{questionpool.destPools}">
	<f:selectItem itemValue="#{pool.questionPoolId}" itemLabel=""/>
</h:selectManyCheckbox>
    </h:column>



  </h:dataTable>

