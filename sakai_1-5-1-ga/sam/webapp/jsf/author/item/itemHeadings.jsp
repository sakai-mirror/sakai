<%-- $Id:
Headings for item edit pages, needs to have msg=AuthorMessages.properties.
--%>
<h:form id="itemFormHeading">
<p class="navIntraTool">
  <h:outputText value="#{msg.my_qs}" />
  <h:outputText value=" | " />
<h:outputLink id="myassessment" value="#{msg.assessment_url}">
        <h:outputText value="#{msg.my_assmts}"/>
</h:outputLink>

  <h:outputText value=" | " />
<h:outputLink id="mytemplate" value="#{msg.template_url}">
        <h:outputText value="#{msg.my_ts}"/>
</h:outputLink>

  <h:outputText value=" | " />
<h:outputLink id="myqpool" value="#{msg.qpool_url}">
        <h:outputText value="#{msg.my_qp}"/>
</h:outputLink>
</p>
<h3 style="insColor insBak">
   <h:outputText value="#{msg.modify_q}: #{itemauthor.assessTitle}" />
</h3>
<!-- CHANGE TYPE -->
<h:outputText value="#{msg.change_q_type}"/>

<h:selectOneMenu rendered="#{questionpool.importToAuthoring == 'true'}" onchange="document.links[3].onclick();"
  value="#{itemauthor.currentItem.itemType}" required="true" id="changeQType1">
  <f:valueChangeListener
           type="org.sakaiproject.tool.assessment.ui.listener.author.StartCreateItemListener" />

  <f:selectItem itemLabel="#{msg.select_qtype}" itemValue=""/>
  <f:selectItem itemLabel="#{msg.multiple_choice_type}" itemValue="1"/>
  <f:selectItem itemLabel="#{msg.multiple_choice_surv}" itemValue="3"/>
  <f:selectItem itemLabel="#{msg.short_answer_essay}" itemValue="5"/>
  <f:selectItem itemLabel="#{msg.fill_in_the_blank}" itemValue="8"/>
  <f:selectItem itemLabel="#{msg.matching}" itemValue="9"/>
  <f:selectItem itemLabel="#{msg.true_false}" itemValue="4"/>
<%--
  <f:selectItem itemLabel="#{msg.audio_recording}" itemValue="7"/>
  <f:selectItem itemLabel="#{msg.file_upload}" itemValue="6"/>
--%>
</h:selectOneMenu>


<h:selectOneMenu onchange="document.links[3].onclick();" rendered="#{questionpool.importToAuthoring == 'false'}" 
  value="#{itemauthor.currentItem.itemType}" required="true" id="changeQType2">
  <f:valueChangeListener
           type="org.sakaiproject.tool.assessment.ui.listener.author.StartCreateItemListener" />

  <f:selectItem itemLabel="#{msg.select_qtype}" itemValue=""/>
  <f:selectItem itemLabel="#{msg.multiple_choice_type}" itemValue="1"/>
  <f:selectItem itemLabel="#{msg.multiple_choice_surv}" itemValue="3"/>
  <f:selectItem itemLabel="#{msg.short_answer_essay}" itemValue="5"/>
  <f:selectItem itemLabel="#{msg.fill_in_the_blank}" itemValue="8"/>
  <f:selectItem itemLabel="#{msg.matching}" itemValue="9"/>
  <f:selectItem itemLabel="#{msg.true_false}" itemValue="4"/>
<%--
  <f:selectItem itemLabel="#{msg.audio_recording}" itemValue="7"/>
  <f:selectItem itemLabel="#{msg.file_upload}" itemValue="6"/>
--%>
  <f:selectItem itemLabel="#{msg.import_from_q}" itemValue="10"/>
</h:selectOneMenu>

<h:commandLink id="hiddenlink" action="#{itemauthor.doit}" value="">
</h:commandLink>

<h:message rendered="#{questionpool.importToAuthoring == 'true'}" for="changeQType1" styleClass="validate"/>
<h:message rendered="#{questionpool.importToAuthoring == 'false'}" for="changeQType2" styleClass="validate"/>

<!-- SUBHEADING -->
<p class="navModeAction">
  <span class="leftNav">
   <b>
     <h:outputText value="#{msg.q}"/>
<%--
     <h:outputText value="#{msg.q} #{itemauthor.currentItem.itemNo}"/>
--%>
     <h:outputText value=" - "/>
     <h:outputText rendered="#{itemauthor.currentItem.itemType == 1}" value="#{msg.multiple_choice_type}"/>
     <h:outputText rendered="#{itemauthor.currentItem.itemType== 2}" value="#{msg.multiple_choice_type}"/>
     <h:outputText rendered="#{itemauthor.currentItem.itemType== 3}" value="#{msg.multiple_choice_surv}"/>
     <h:outputText rendered="#{itemauthor.currentItem.itemType== 4}" value="#{msg.true_false}"/>
     <h:outputText rendered="#{itemauthor.currentItem.itemType== 5}" value="#{msg.short_answer_essay}"/>
     <h:outputText rendered="#{itemauthor.currentItem.itemType== 8}" value="#{msg.fill_in_the_blank}"/>
     <h:outputText rendered="#{itemauthor.currentItem.itemType== 9}" value="#{msg.matching}"/>
<%--
     <h:outputText rendered="#{itemauthor.currentItem.itemType== 7}" value="#{msg.audio_recording}"/>
     <h:outputText rendered="#{itemauthor.currentItem.itemType== 6}" value="#{msg.file_upload}"/>
--%>
     <h:outputText rendered="#{itemauthor.currentItem.itemType== 10}" value="#{msg.import_from_q}"/>
   </b>
 </span>
 <span class="rightNav">
 
 <%--
  temporily comment put Preview link for a specific question in Author. It will not be the feature in Sam 1.5.
  <h:commandLink id="preview" immediate="true" action="preview">
          <h:outputText value="#{msg.preview}" />
        <f:actionListener
           type="org.sakaiproject.tool.assessment.ui.listener.author.ItemModifyListener" />
  </h:commandLink>
--%>

  <h:outputText rendered="#{itemauthor.currentItem.itemId != null}" value=" | " />
  <h:commandLink rendered="#{itemauthor.currentItem.itemId != null}" styleClass="alignRight" immediate="true" id="deleteitem" action="#{itemauthor.confirmDeleteItem}">
                <h:outputText value="#{msg.button_remove}" />
                <f:param name="itemid" value="#{itemauthor.currentItem.itemId}"/>
              </h:commandLink>


  <h:outputText rendered="#{itemauthor.target == 'assessment'}" value=" | " />
  <h:commandLink immediate="true" rendered="#{itemauthor.target == 'assessment'}" action="editAssessment">
    <h:outputText value="#{msg.my_qs}" />
  </h:commandLink>
 </span>
 <br />
   <h:messages layout="table" style="color:red"/>
 <br />
</p>
</h:form>
