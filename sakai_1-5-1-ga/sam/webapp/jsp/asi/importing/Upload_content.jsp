<%@ page import="org.apache.struts.action.*,
                 java.util.Iterator,
                 org.navigoproject.ui.web.asi.importing.UploadForm "%>
<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<html>
<head>
<title>File Upload Example</title>
<link type="TEXT/CSS" rel="STYLESHEET" href="/sam-stg/css/navigo.css">
</head>

<body>

<!-- Find out if the maximum length has been exceeded. -->
<logic:present name="<%= Action.ERROR_KEY %>" scope="request">
    <%
        ActionErrors errors = (ActionErrors) request.getAttribute(Action.ERROR_KEY);
        //note that this error is created in the validate() method of UploadForm
        Iterator iterator = errors.get(UploadForm.ERROR_PROPERTY_MAX_LENGTH_EXCEEDED);
        //there's only one possible error in this
        ActionError error = (ActionError) iterator.next();
        pageContext.setAttribute("maxlength.error", error, PageContext.REQUEST_SCOPE);
    %>
</logic:present>
<!-- If there was an error, print it out -->
<logic:present name="maxlength.error" scope="request">
    <font color="red"><bean:message name="maxlength.error" property="key" /></font>
</logic:present>
<logic:notPresent name="maxlength.error" scope="request">
<!--    Note that the maximum allowed size of an uploaded file for this application is two megabytes.-->
    <!--See the /WEB-INF/struts-config.xml file for this application to change it. -->
</logic:notPresent>


<table align="top" width="100%">
  <tr>
    <td align="left" class="navigo_border">Import an assessment </td>
  </tr>
  <tr>
    <td>
      <br/>
    </td>
  </tr>
</table>

<table align="top" width="100%">
  <html:form action="/asi/select/upload.do?queryParam=Successful" enctype="multipart/form-data">
    <tr>
      <td class="bold">Assessment Title: </td>
      <td><html:text property="theText"/></td>
      <td></td>
    </tr>
    <tr>
      <td align="left" width="15%" class="bold">Import from a file: </td>
      <td align="left" width="20%">
        <html:file property="theFile" />
      </td>
      <td align="left" width="65%">
        <html:submit value="Import Assessment" />
      </td>
    </tr>
  </html:form>
  <tr>
    <table align="left" width="100%">
      <tr>
        <td align="left" width="40%">
        </td>
        <html:form action="/asi/author/assessment/assessmentAction.do">
          <td align="left" width="10%">
            <html:submit value="Cancel"/>
          </td>
          <td align="left" width="50%">
          </td>
        </html:form>
      </tr>
    </table>
  </tr>
</table>

<%--
<logic:notPresent name="maxlength.error" scope="request">
<!--    Note that the maximum allowed size of an uploaded file for this application is two megabytes.-->
    <!--See the /WEB-INF/struts-config.xml file for this application to change it. -->
</logic:notPresent>

<br /><br />
<!--
  The most important part is to declare your form's enctype to be "multipart/form-data",
  and to have a form:file element that maps to your ActionForm's FormFile property
-->


<html:form action="/asi/select/upload.do?queryParam=Successful" enctype="multipart/form-data">
  Select a file to upload/import:<br />
  <html:file property="theFile" /><br /><br />

<!--
        If you would rather write this file to another file, please check here:
        <html:checkbox property="writeFile" /><br /><br />%>

        If you checked the box to write to a file, please specify the file path here:<br />
        <html:text property="filePath" /><br /><br />
        -->

  <html:submit value="Import Assessment" />
</html:form>
<html:form action="/asi/author/assessment/assessmentAction.do">
  <html:submit value="Cancel"/>
</html:form>
--%>

</body>
</html>