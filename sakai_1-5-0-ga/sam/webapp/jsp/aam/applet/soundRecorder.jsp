<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Record audio.  Requires Java 1.4 plugin.</title>
</head>
<body bgcolor="#cccccc">
<CENTER>
<h3>
<%-- if a parameter is missing complain --%>
<logic:notPresent parameter="filename">
  <logic:notPresent parameter="seconds">
    <logic:notPresent parameter="limit">
      <logic:notPresent parameter="app">
        <logic:notPresent parameter="dir">
          <logic:notPresent parameter="imageUrl">
              <br><font color="red"><b>Unable to record.</b></font><br>
          </logic:notPresent>
          </logic:notPresent>
        </logic:notPresent>
    </logic:notPresent>
  </logic:notPresent>
</logic:notPresent>
<logic:notPresent parameter="filename">
  <br><font color="red"><b>Missing: parameter=&quot;filename&quot;</b></font><br>
</logic:notPresent>
<logic:notPresent parameter="seconds">
  <br><font color="red"><b>Missing: parameter=&quot;seconds&quot;</b></font><br>
</logic:notPresent>
<logic:notPresent parameter="limit">
  <br><font color="red"><b>Missing: parameter=&quot;limit&quot;</b></font><br>
</logic:notPresent>
<logic:notPresent parameter="app">
  <br><font color="red"><b>Missing: parameter=&quot;app&quot;</b></font><br>
</logic:notPresent>
<logic:notPresent parameter="dir">
  <br><font color="red"><b>Missing: parameter=&quot;dir&quot;</b></font><br>
</logic:notPresent>
<logic:notPresent parameter="imageUrl">
  <br><font color="red"><b>Missing: parameter=&quot;imageUrl&quot;</b></font><br>
</logic:notPresent>
</h3>
<%-- display applet only if all parameters supplied --%>
<logic:present parameter="filename">
  <logic:present parameter="seconds">
    <logic:present parameter="limit">
      <logic:present parameter="app">
        <logic:present parameter="dir">
          <logic:present parameter="imageUrl">
<!--
******************************************************************
DEBUGGING INFO
******************************************************************
debug:
filename <%= request.getParameter("filename") %><br>
seconds <%= request.getParameter("seconds") %><br>
limit <%= request.getParameter("limit") %><br>
app <%= request.getParameter("app") %><br>
dir <%= request.getParameter("dir") %><br>
imageUrl <%= request.getContextPath()%>/<%= request.getParameter("imageUrl") %><br>
******************************************************************
-->
<!-- RECORDING APPLET -->
<jsp:plugin
  type = "applet"
  code = "CapturePlaybackApplet.class"
  archive = "CapturePlayback.jar"
  width = "400"
  height = "250"
  hspace="2"
  vspace="2"
  jreversion="1.4"
  nspluginurl="http://java.sun.com/products/plugin/1.4/plugin-install.html"
  iepluginurl=
"http://java.sun.com/products/plugin/1.4/jinstall-14-win32.cab#Version=1,4,0,mn"
>
  <jsp:params>
    <jsp:param name="scriptable" value="false"/>
    <jsp:param name="file" value='<%= request.getParameter("filename") %>'/>
    <jsp:param name="seconds" value='<%= request.getParameter("seconds") %>'/>
    <jsp:param name="limit" value='<%= request.getParameter("limit") %>'/>
    <jsp:param name="dir" value='<%= request.getParameter("dir") %>'/>
    <jsp:param name="app" value='<%= request.getParameter("app") %>'/>
    <jsp:param name="imageUrl" value='<%= request.getContextPath()+"/"+request.getParameter("imageUrl") %>'/>
  </jsp:params>
  <jsp:fallback>
    <font color="red"><b>Unable to record.</b></font><br/>
    Note: Requires Java Plug-in 1.4 (Windows, Solaris, Linux).
  </jsp:fallback>
</jsp:plugin>
<!-- END RECORDING APPLET -->
<!-- SAVE FORM -->
<script type="text/javascript" src="<%=request.getContextPath()%>/htmlarea/popups/popup.js"></script>

<script language="javascript"><!--

// we have disabled the preview option

function Init() {
  __dlg_init();
//  if f_url were not a hidden value but a text input, uncomment this line
//  document.getElementById("f_url").focus();
};

// This is if the Submit button is pressed, the default dialog used OK, hence the name
function onOK() {
// need to fix bug in validation
/**********************
  var required = {
    "f_url": "You must enter the URL or file.",
    "f_alt": "Please enter the alternate text"
  };
  for (var i in required) {
    var el = document.getElementById(i);
    if (!el.value) {
      alert(required[i]);
      el.focus();
      return false;
    }
    window.close();//mozilla
  }
***/

  // pass data back to the calling window
  var fields = ["f_url", "f_name", "f_author",
  "f_source_1",  "f_description" ];

  // create an associative array of parameters
  var param = new Object();
  for (var i in fields) {
    var id = fields[i];
    var el = document.getElementById(id);
    param[id] = el.value;
  }

  // insert the audio URL HTML in the calling editor
  //opener.editor.insertHTML('<a  href="' + param["f_url"] + '" />' +
  //  param["f_name"] +
  //  '</a>'
  //  );

  form[0].submit();
  // on success, htmlInlineAdded.jsp is rendered which writes the url showMedia.do?id= back to the
  // parent window. - daisyf (07/05/04)

  //  __dlg_close(param);
  // window.close();//mozilla

  return false;
};

// This is if the cancel is pressed
function onCancel() {

  __dlg_close(null);
  window.close();//mozilla
  //return false;
};

//--></script>
<html:form action="htmlInlineUpload.do" method="post" onsubmit="onOK();"
  enctype="multipart/form-data">
  <html:hidden property="source" styleId="f_source_1" value="2"/>
  <input type="hidden" name="link" id="f_link"
    value='<%=
      "file://" + request.getParameter("dir") + "/" +
      request.getParameter("filename")
    %>'
  />
  <input type="hidden" name="url" id="f_url"
    value='<%=
      "file://" + request.getParameter("dir") + "/" +
      request.getParameter("filename")
    %>'
  />
  <html:hidden  property="name" styleId="f_name" value="Audio Upload"/>
  <html:hidden property="description" styleId="f_description"
    value="Audio Upload"/>
  <html:hidden property="author" styleId="f_author" value="Audio Upload"/>
  <html:hidden property="type" value="audio/basic"/>
  <html:hidden property="isHtmlInline" value="true"/>
  <input type="hidden" name="isHtmlImage" id="isHtmlImage"
      value="false" />
  <html:reset onclick="return onCancel();" value="Cancel"/>
  <html:submit value="Save" property="Save" />
</html:form>
<!-- END SAVE FORM -->

          </logic:present>
        </logic:present>
      </logic:present>
    </logic:present>
  </logic:present>
</logic:present>
</CENTER>
</body>
</html>
