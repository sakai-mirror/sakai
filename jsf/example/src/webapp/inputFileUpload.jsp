<%-- $Id: inputFileUpload.jsp,v 1.4 2005/05/04 21:20:40 janderse.umich.edu Exp $ --%>
<f:view>
<sakaix:view title="inputFileUpload tag - Sakai 2.0 JSF example">
<h:commandLink action="index"><h:outputText value="Back to examples index" /></h:commandLink>
<f:verbatim><a href="<%=request.getRequestURI()%>.source">View page source</a></f:verbatim>


<hr />
<h2>inputFileUpload example</h2>
<hr />

<h:form enctype="multipart/form-data">   
   
   <%-- <sakaix:inputFileUpload id="plainVanillaNoOptions" /><h:message for="plainVanillaNoOptions" styleClass="validationEmbedded" /> --%>
   
   <h:outputText value=" * " />
   <sakaix:inputFileUpload id="itsRequired"
       valueChangeListener="#{examplebean.processFileUpload}"
       required="true"
   />
   <h:message for="itsRequired" styleClass="validationEmbedded" />
   <br />   

   <h:outputText value="Lots of options: " />
   <sakaix:inputFileUpload id="everything"
       valueChangeListener="#{examplebean.processFileUpload}"
       accesskey="u"
        maxlength="18"
       size="20"
       tabindex="5"
       style="color: red"
       styleClass="myCssClass"
       directory="/teemp"
       required="false"
   />
   <h:message for="everything" styleClass="validationEmbedded" />
   <br />
   
   <h:commandButton value="Upload" type="submit" style="act" />
<%--
   <br />
    <sakaix:pager totalItems="92" pageSize="20" textItem="students" renderPageSize="false" />   
    <br />
--%>
</h:form>

 <br />  
    <h:outputText value="No form test: " />
    <sakaix:inputFileUpload id="noform" /><h:message for="noform" styleClass="validationEmbedded" />

 <h:form enctype="incorrect/mime-type">
   <h:outputText value="Wrong enctype test: " />
   <sakaix:inputFileUpload id="wrongFormEncType" /><h:message for="wrongFormEncType" styleClass="validationEmbedded" />
 </h:form>
 <br />
 
 <h:form enctype="multipart/form-data">
   <h:outputText value="Bad directory test: " />
   <sakaix:inputFileUpload id="badDirectory" directory="/bad_directory/foobar/" /><h:message for="badDirectory" styleClass="validationEmbedded" />
   <br />
 </h:form>

 <h:outputText value=" * Required" />

</sakaix:view>
</f:view>

