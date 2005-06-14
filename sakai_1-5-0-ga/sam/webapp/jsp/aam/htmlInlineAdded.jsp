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
If source=2, audio upload and url parsed back as file://
If isHtmlImage it requires an image tag, otherwise it requires a link.
--%>
<logic:equal name='fileUpload' property='isHtmlInline' value='true'>
 <script language="javascript" style="text/JavaScript">
  <logic:equal name='fileUpload' property='isHtmlImage' value='true'>
      // create image tag, insert the image HTML in the calling editor:
    <logic:equal name='fileUpload' property='source' value='0'><%--upload --%>
      // source is a file upload...
      opener.currentEditorForPopup.insertHTML('<img src="<%=request.getContextPath()%>/showMedia.do?id=' +
        "<bean:write name='fileUpload' property='mapId' />"  +
        '" alt="' +
          "<bean:write name='fileUpload' property='imageAlt' />" +
        '" vspace="' +
          "<bean:write name='fileUpload' property='imageVspace' />" +
        '" hspace="' +
          "<bean:write name='fileUpload' property='imageHspace' />" +
        '" align="' +
          "<bean:write name='fileUpload' property='imageAlign' />" +
        '" border="' +
          "<bean:write name='fileUpload' property='imageBorder' />" +
        '" />');
    </logic:equal><%-- end source file upload to data content: source = 0 --%>
    <logic:equal name='fileUpload' property='source' value='1'><%--extern 1 --%>
      // source is an external file...
      opener.currentEditorForPopup.insertHTML('<img src="' +
        "<bean:write name='fileUpload' property='link' />" +
        '" alt="' +
          "<bean:write name='fileUpload' property='imageAlt' />" +
        '" vspace="' +
          "<bean:write name='fileUpload' property='imageVspace' />" +
        '" hspace="' +
          "<bean:write name='fileUpload' property='imageHspace' />" +
        '" align="' +
          "<bean:write name='fileUpload' property='imageAlign' />" +
        '" border="' +
          "<bean:write name='fileUpload' property='imageBorder' />" +
        '" />');
    </logic:equal><%-- end source is an external file: source = 1 --%>
  </logic:equal><%-- end isHtmlImage true --%>
  <logic:equal name='fileUpload' property='isHtmlImage' value='false'>
      // create link
    <logic:equal name='fileUpload' property='source' value='0'><%-- upload 0--%>
      // source is a file upload
      opener.currentEditorForPopup.insertHTML('<a  href="<%=request.getContextPath()%>/showMedia.do?id=' +
        "<bean:write name='fileUpload' property='mapId' />" +
        '" >' +
        "<bean:write name='fileUpload' property='name' />" +
        opener.currentEditorForPopup.getSelectedHTML() +
        '</a>'
      );
    </logic:equal><%-- end source is a file upload: source = 0 --%>
    <logic:equal name='fileUpload' property='source' value='1'><%--source 1 --%>
      <logic:notMatch name='fileUpload' property='link' value='file://'
        location='start'>

        // source is an external file
        opener.currentEditorForPopup.insertHTML('<a  href="' +
          "<bean:write name='fileUpload' property='link' />" +
          '" >' +
          "<bean:write name='fileUpload' property='name' />" + " " +
          opener.currentEditorForPopup.getSelectedHTML() +
          '</a>'
        );
      </logic:notMatch>
      <logic:match name='fileUpload' property='link' value='file://'
          location='start'>

        // source is an internal file on server file system
        opener.currentEditorForPopup.insertHTML('<a  href="<%=request.getContextPath()%>/showMedia.do?id=' +
          "<bean:write name='fileUpload' property='mapId' />" +
          '" >' +
          "<bean:write name='fileUpload' property='name' />" + " " +
          opener.currentEditorForPopup.getSelectedHTML() +
          '</a>'
        );

      </logic:match>
    </logic:equal><%-- end source = 1 --%>
    <logic:equal name='fileUpload' property='source' value='2'><%--source 2 --%>
      <logic:notMatch name='fileUpload' property='link' value='file://'
        location='start'>

        // source is an external file
        opener.currentEditorForPopup.insertHTML('<a  href="' +
          "<bean:write name='fileUpload' property='link' />" +
          '" >' +
          "<bean:write name='fileUpload' property='name' />" + " " +
          opener.currentEditorForPopup.getSelectedHTML() +
          '</a>'
        );
      </logic:notMatch>
      <logic:match name='fileUpload' property='link' value='file://'
          location='start'>

        // source is an internal file on server file system
        opener.currentEditorForPopup.insertHTML('<a  href="<%=request.getContextPath()%>/showMedia.do?id=' +
          "<bean:write name='fileUpload' property='mapId' />" +
          '" >' +
          "<bean:write name='fileUpload' property='name' />" + " " +
          opener.currentEditorForPopup.getSelectedHTML() +
          '</a>'
        );

      </logic:match>
    </logic:equal><%-- end source = 2 --%>
  </logic:equal><%-- end isHtmlImage false --%>
  // now we close the window, comment out this line to display debugging info
  window.close();
 </script>
</logic:equal><%-- end htmlInline true--%>

</head>
<body bgcolor="#ffffff">
<logic:notEqual name='fileUpload' property='isHtmlInline' value='true'>
  <br/><font color="red"><b>Usage: isHtmlInline not set.</b></font><br/>
</logic:notEqual>
Debug: <bean:write name='fileUpload' property='mapId' /> Added successfully.
Debug:
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
