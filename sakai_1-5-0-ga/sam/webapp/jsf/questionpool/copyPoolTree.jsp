<h:panelGrid  rendered="#{questionpool.actionType == 'pool'}" columns="2">
	
	<h:selectManyCheckbox id="checkboxes" layout="pageDirection" value="#{questionpool.destPools}">
		<f:selectItem itemValue="0" itemLabel=""/>
	</h:selectManyCheckbox>

        <h:outputText value="#{msg.q_mgr}"/>
</h:panelGrid>


<h:dataTable id="TreeTable" value="#{questionpool.qpools}"
    width="100%" headerClass="regHeading" var="pool" >


    <h:column  id="radiocol">

<h:selectManyCheckbox  id="checkboxes" layout="pageDirection" 
		value="#{questionpool.destPools}">
                <f:selectItem itemValue="#{pool.questionPoolId}" itemLabel=""/>
</h:selectManyCheckbox>

    </h:column>

    <h:column id="col1">
     <f:facet name="header">
       <h:outputText id="c1header" value="#{msg.p_name}"/>
     </f:facet>


<h:panelGroup styleClass="treetier#{questionpool.tree.currentLevel}"  id="firstcolumn">
<h:inputHidden id="rowid" value="#{questionpool.tree.currentObjectHTMLId}"/>

<h:outputLink id="parenttogglelink"  onclick="toggleRowsForSelectList(this)" value="#" styleClass="treefolder" rendered="#{questionpool.tree.hasChildList}" >
<h:graphicImage id="spacer_for_mozilla" style="border:0" height="14" width="30" value="/images/delivery/spacer.gif" />
</h:outputLink>
<h:outputLink id="togglelink"  styleClass="treedoc" rendered="#{questionpool.tree.hasNoChildList}" >
<h:graphicImage id="spacer_for_mozilla1" style="border:0" height="14" width="30" value="/images/delivery/spacer.gif" />
</h:outputLink>



  <h:outputText id="poolnametext" value="#{pool.displayName}"/>

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


</h:dataTable>

