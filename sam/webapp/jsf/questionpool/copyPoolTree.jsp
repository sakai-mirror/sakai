<!-- $Id$
<%--
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
*
* Licensed under the Educational Community License Version 1.0 (the "License");
* By obtaining, using and/or copying this Original Work, you agree that you have read,
* understand, and will comply with the terms and conditions of the Educational Community License.
* You may obtain a copy of the License at:
*
*      http://cvs.sakaiproject.org/licenses/license_1_0.html
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
* AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*
**********************************************************************************/
--%>
-->
<h:panelGrid  rendered="#{questionpool.actionType == 'pool'}" columns="2">

	<h:selectManyCheckbox id="checkboxes" layout="pageDirection" value="#{questionpool.destPools}">
		<f:selectItem itemValue="0" itemLabel=""/>
	</h:selectManyCheckbox>

        <h:outputText value="#{msg.q_mgr}"/>
</h:panelGrid>


<h:dataTable id="TreeTable" value="#{questionpool.copyQpools}"
     var="pool" width="100%" styleClass="listHier"  >


    <h:column  id="radiocol">

<h:selectManyCheckbox  id="checkboxes" layout="pageDirection"
		value="#{questionpool.destPools}">
                <f:selectItem itemValue="#{pool.questionPoolId}" itemLabel=""/>
</h:selectManyCheckbox>

    </h:column>

    <h:column id="col1">
     <f:facet name="header">
      <h:panelGroup>
       <h:commandLink id="sortByTitle" immediate="true"  rendered="#{questionpool.sortCopyPoolProperty !='title'}" action="#{questionpool.sortCopyPoolByColumnHeader}">
          <f:param name="copyPoolOrderBy" value="title"/>
          <f:param name="copyPoolAscending" value="true"/>
          <h:outputText  value="#{msg.p_name}"  rendered="#{questionpool.sortCopyPoolProperty !='title'}" />
       </h:commandLink>
       <h:outputText  value="#{msg.p_name}" styleClass="currentSort" rendered="#{questionpool.sortCopyPoolProperty =='title'}" />
       <h:commandLink  immediate="true" rendered="#{questionpool.sortCopyPoolProperty =='title' && questionpool.sortCopyPoolAscending }"  action="#{questionpool.sortCopyPoolByColumnHeader}">
          <f:param name="copyPoolOrderBy" value="title"/>
          <f:param name="copyPoolAscending" value="false" />
          <h:graphicImage alt="#{msg.asc}" rendered="#{questionpool.sortCopyPoolAscending}" url="/images/sortascending.gif"/>
      </h:commandLink>
      <h:commandLink  immediate="true" rendered="#{questionpool.sortCopyPoolProperty =='title' && !questionpool.sortCopyPoolAscending }"  action="#{questionpool.sortCopyPoolByColumnHeader}">
          <f:param name="copyPoolOrderBy" value="title"/>
          <f:param name="copyPoolAscending" value="true" />
          <h:graphicImage alt="#{msg.asc}" rendered="#{!questionpool.sortCopyPoolAscending}" url="/images/sortdescending.gif"/>
      </h:commandLink>
     </h:panelGroup>
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
      <h:panelGroup>
       <h:commandLink id="sortByOwner" immediate="true"  rendered="#{questionpool.sortCopyPoolProperty !='ownerId'}" action="#{questionpool.sortCopyPoolByColumnHeader}">
          <f:param name="copyPoolOrderBy" value="ownerId"/>
          <f:param name="copyPoolAscending" value="true"/>
          <h:outputText  value="#{msg.creator}"  rendered="#{questionpool.sortCopyPoolProperty !='ownerId'}" />
       </h:commandLink>
       <h:outputText  value="#{msg.creator}" styleClass="currentSort" rendered="#{questionpool.sortCopyPoolProperty =='ownerId'}" />
       <h:commandLink  immediate="true" rendered="#{questionpool.sortCopyPoolProperty =='ownerId' && questionpool.sortCopyPoolAscending }"  action="#{questionpool.sortCopyPoolByColumnHeader}">
          <f:param name="copyPoolOrderBy" value="ownerId"/>
          <f:param name="copyPoolAscending" value="false" />
          <h:graphicImage alt="#{msg.asc}" rendered="#{questionpool.sortCopyPoolAscending}" url="/images/sortascending.gif"/>
      </h:commandLink>
      <h:commandLink  immediate="true" rendered="#{questionpool.sortCopyPoolProperty =='ownerId' && !questionpool.sortCopyPoolAscending }"  action="#{questionpool.sortCopyPoolByColumnHeader}">
          <f:param name="copyPoolOrderBy" value="ownerId"/>
          <f:param name="copyPoolAscending" value="true" />
          <h:graphicImage alt="#{msg.asc}" rendered="#{!questionpool.sortCopyPoolAscending}" url="/images/sortdescending.gif"/>
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
       <h:commandLink id="sortByLastModified" immediate="true"  rendered="#{questionpool.sortCopyPoolProperty !='lastModified'}" action="#{questionpool.sortCopyPoolByColumnHeader}">
          <f:param name="copyPoolOrderBy" value="lastModified"/>
          <f:param name="copyPoolAscending" value="true"/>
          <h:outputText  value="#{msg.last_mod}"  rendered="#{questionpool.sortCopyPoolProperty !='lastModified'}" />
       </h:commandLink>
       <h:outputText  value="#{msg.last_mod}" styleClass="currentSort" rendered="#{questionpool.sortCopyPoolProperty =='lastModified'}" />
       <h:commandLink  immediate="true" rendered="#{questionpool.sortCopyPoolProperty =='lastModified' && questionpool.sortCopyPoolAscending }"  action="#{questionpool.sortCopyPoolByColumnHeader}">
          <f:param name="copyPoolOrderBy" value="lastModified"/>
          <f:param name="copyPoolAscending" value="false" />
          <h:graphicImage alt="#{msg.asc}" rendered="#{questionpool.sortCopyPoolAscending}" url="/images/sortascending.gif"/>
      </h:commandLink>
      <h:commandLink  immediate="true" rendered="#{questionpool.sortCopyPoolProperty =='lastModified' && !questionpool.sortCopyPoolAscending }"  action="#{questionpool.sortCopyPoolByColumnHeader}">
          <f:param name="copyPoolOrderBy" value="lastModified"/>
          <f:param name="copyPoolAscending" value="true" />
          <h:graphicImage alt="#{msg.asc}" rendered="#{!questionpool.sortCopyPoolAscending}" url="/images/sortdescending.gif"/>
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
       <h:commandLink id="sortByQuestion" immediate="true"  rendered="#{questionpool.sortCopyPoolProperty !='question'}" action="#{questionpool.sortCopyPoolByColumnHeader}">
          <f:param name="copyPoolOrderBy" value="question"/>
          <f:param name="copyPoolAscending" value="true"/>
          <h:outputText  value="#{msg.qs}"  rendered="#{questionpool.sortCopyPoolProperty !='question'}" />
       </h:commandLink>
       <h:outputText  value="#{msg.qs}" styleClass="currentSort" rendered="#{questionpool.sortCopyPoolProperty =='question'}" />
       <h:commandLink  immediate="true" rendered="#{questionpool.sortCopyPoolProperty =='question' && questionpool.sortCopyPoolAscending }"  action="#{questionpool.sortCopyPoolByColumnHeader}">
          <f:param name="copyPoolOrderBy" value="question"/>
          <f:param name="copyPoolAscending" value="false" />
          <h:graphicImage alt="#{msg.asc}" rendered="#{questionpool.sortCopyPoolAscending}" url="/images/sortascending.gif"/>
      </h:commandLink>
      <h:commandLink  immediate="true" rendered="#{questionpool.sortCopyPoolProperty =='question' && !questionpool.sortCopyPoolAscending }"  action="#{questionpool.sortCopyPoolByColumnHeader}">
          <f:param name="copyPoolOrderBy" value="question"/>
          <f:param name="copyPoolAscending" value="true" />
          <h:graphicImage alt="#{msg.asc}" rendered="#{!questionpool.sortCopyPoolAscending}" url="/images/sortdescending.gif"/>
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
       <h:commandLink id="sortBySubPool" immediate="true"  rendered="#{questionpool.sortCopyPoolProperty !='subPoolSize'}" action="#{questionpool.sortCopyPoolByColumnHeader}">
          <f:param name="copyPoolOrderBy" value="subPoolSize"/>
          <f:param name="copyPoolAscending" value="true"/>
          <h:outputText  value="#{msg.subps}"  rendered="#{questionpool.sortCopyPoolProperty !='subPoolSize'}" />
       </h:commandLink>
       <h:outputText  value="#{msg.subps}" styleClass="currentSort" rendered="#{questionpool.sortCopyPoolProperty =='subPoolSize'}" />
       <h:commandLink  immediate="true" rendered="#{questionpool.sortCopyPoolProperty =='subPoolSize' && questionpool.sortCopyPoolAscending }"  action="#{questionpool.sortCopyPoolByColumnHeader}">
          <f:param name="copyPoolOrderBy" value="subPoolSize"/>
          <f:param name="copyPoolAscending" value="false" />
          <h:graphicImage alt="#{msg.asc}" rendered="#{questionpool.sortCopyPoolAscending}" url="/images/sortascending.gif"/>
      </h:commandLink>
      <h:commandLink  immediate="true" rendered="#{questionpool.sortCopyPoolProperty =='subPoolSize' && !questionpool.sortCopyPoolAscending }"  action="#{questionpool.sortCopyPoolByColumnHeader}">
          <f:param name="copyPoolOrderBy" value="subPoolSize"/>
          <f:param name="copyPoolAscending" value="true" />
          <h:graphicImage alt="#{msg.asc}" rendered="#{!questionpool.sortCopyPoolAscending}" url="/images/sortdescending.gif"/>
      </h:commandLink>
     </h:panelGroup>
     </f:facet>
     <h:panelGroup id="fifthcolumn">
        <h:outputText value="#{pool.subPoolSize}"/>
     </h:panelGroup>
    </h:column>


</h:dataTable>

