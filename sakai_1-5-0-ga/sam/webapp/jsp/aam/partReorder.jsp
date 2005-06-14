<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<jsp:useBean id="partReorder" scope="session" 
  class="org.navigoproject.ui.web.form.edit.PartReorderForm" />
<HTML>
<HEAD>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<TITLE>Reorder Parts</TITLE>
<SCRIPT LANGUAGE=JAVASCRIPT>
<!-- hide from non-JS browsers
function doMySubmit()
{
  document.forms[0].isMySubmit.value = "false";
  document.forms[0].submit();
  document.forms[0].isMySubmit.value = "true";
  return true;
}
// -->
</SCRIPT>

</HEAD>
<body>
<html:form action="/partReorder.do" method="post">
<input type="hidden" name="isMySubmit" value="true">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
<td ALIGN=LEFT WIDTH=100% VALIGN=BOTTOM>
<font FACE="arial, helvetica" SIZE=3 COLOR=#660000><b>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
Reorder Parts</b>
</font>
&nbsp;
<font FACE="arial,helvetica" SIZE=1 COLOR=#660000>

</td>
</tr>
<tr>
<td VALIGN=TOP><img src="<%=request.getContextPath()%>/images/divider2.gif" border="0"></td>
</tr>
</table>

<table WIDTH=100% CELLPADDING=4 CELLSPACING=2 BORDER=0>
<tr>
<td  ALIGN=CENTER>
<font FACE="arial,helvetica" size=-1><i>
<br>Click <b>Cancel</b> to exit with no changes.  Click <b>OK</b> to save the changes.</i></font><br><br></td></tr>
<tr><td> 
<font FACE="arial,helvetica" COLOR=#660000><b>
Reorder parts.
</b></font>
</td>
</tr>
</table>
<p>
<table width=100%>
<tr>
    <td><font FACE="arial,helvetica" SIZE=-2>Part Name</font></td>
    <td>&nbsp;</td>
</tr>

  <logic:iterate name="partReorder" property="initialList" id="partItem"
    type="org.navigoproject.business.entity.assessment.model.SectionImpl"
    indexId="ctr">
  <tr bgcolor=<%= (ctr.intValue()%2==0?"#E1E1E1":"#FFFFFF") %>>
    <td><font FACE="arial,helvetica" SIZE=-1><bean:write 
      name="partItem" property="name" /></font></td>
    <td><html:select name="partReorder" property='<%= "listAsArray[" + 
      ctr + "].data.position" %>' onchange="doMySubmit()">
      <html:options name="partReorder" property="order"/>  
    </html:select></td>
  </tr>
  </logic:iterate>

</table>
<center>
<html:submit value="  OK  "property="submitit" />
</html:form>
<html:form action="/cancelPartReorder.do" method="post">
<html:submit value="  CANCEL "/>
</html:form>
</body>
</HTML>
