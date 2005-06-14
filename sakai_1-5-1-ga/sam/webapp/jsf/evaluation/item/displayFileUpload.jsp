<%-- $Id: displayFileUpload.jsp,v 1.2 2004/12/04 08:31:46 rgollub.stanford.edu Exp $
include file for displaying file upload questions
should be included in file importing DeliveryMessages
--%>
<h:outputText value="#{question.description}" escape="false"/>
<f:verbatim><br /></f:verbatim>
<h:outputText value="#{question.text}"  escape="false"/>
<%-- use existing JSP for now until we have JSF file upload --%>
<h:outputLink
 value="javascript:window.open('/samigo/htmlarea/navigo_popups/file_upload.jsp','ha_fullscreen','toolbar=no,location=no,directories=no,status=no,menubar=yes,'scrollbars=yes,resizable=yes,width=640,height=480');">
  <h:outputText value="#{msg2.upload_file}." />
</h:outputLink>

