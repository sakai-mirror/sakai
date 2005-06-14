<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<jsp:useBean id="fileUpload" scope="session"
  class="org.navigoproject.ui.web.form.edit.FileUploadForm" />
<html:html>
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8">
<title>Added successfully.</title>
<link href="stylesheets/main.css" rel="stylesheet" type="text/css">
<%--
Create JavaScript from the ActionForm properties.  This JavaScript code
writes it back into the calling HTMLArea editor control in the parent window.
If source=0 it is a file uploaded and persisted in the at content property.
If source=1, an external url or a file:// url on the server filesystem.
If isHtmlImage it requires an image tag, otherwise it requires a link.
--%>
<logic:equal name='fileUpload' property='isHtmlInline' value='true'>
 <script language="javascript" style="text/JavaScript">
   // write answer back to parent window (search the hidden input "response_ident_ref"
   // in xsl/delivery/item.xsl) - daisyf
   function writeToParent(){
     var item_ident_ref = "<bean:write name='fileUpload' property='itemIdentRef' />";
     var i=0;

     while (opener.ASIDeliveryForm.elements[i]!=null){
       if (opener.ASIDeliveryForm.elements[i].id==item_ident_ref){
         opener.ASIDeliveryForm.elements[i].value="<%=request.getContextPath()%>/showMedia.do?id="+"<bean:write name='fileUpload' property='mapId' />";
       }
       if (opener.ASIDeliveryForm.elements[i].id=="isnew"+item_ident_ref){
         opener.ASIDeliveryForm.elements[i].value="true";
       }
       i++;
     }
     // now we close the window, comment out this line to display debugging info
     window.close();
   }
 </script>
</logic:equal><%-- end htmlInline true--%>

</head>
<body bgcolor="#ffffff" onLoad="javascript:writeToParent();">
Debug: <bean:write name='fileUpload' property='mapId' /> Added successfully.
Debug: hello 
<pre>
      source = <bean:write name='fileUpload' property='source' />
      newfile = <bean:write name='fileUpload' property='newfile' />
      link = <bean:write name='fileUpload' property='link' />
      name = <bean:write name='fileUpload' property='name' />
      description = <bean:write name='fileUpload' property='description' />
      author = <bean:write name='fileUpload' property='author' />
      type = <bean:write name='fileUpload' property='type' />
      filename = <bean:write name='fileUpload' property='fileName' />
      mediaTypes = <bean:write name='fileUpload' property='mediaTypes' />
      mapId = <bean:write name='fileUpload' property='mapId' />
      imageAlt = <bean:write name='fileUpload' property='imageAlt' />
      imageVspace = <bean:write name='fileUpload' property='imageVspace' />
      imageHspace = <bean:write name='fileUpload' property='imageHspace' />
      imageAlign = <bean:write name='fileUpload' property='imageAlign' />
      imageAlign = <bean:write name='fileUpload' property='imageBorder' />
      isEdit = <bean:write name='fileUpload' property='isEdit' />
      isHtmlInline = <bean:write name='fileUpload' property='isHtmlInline' />
      isHtmlImage = <bean:write name='fileUpload' property='isHtmlImage' />
</pre>
</body>
</html:html>
