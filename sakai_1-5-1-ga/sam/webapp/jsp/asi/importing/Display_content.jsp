<%--
/**
 * <p>Title: NavigoProject.org</p>
 * <p>Description: OKI based implementation</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author <a href="mailto:casong@indiana.edu">Pamela Song</a>
 * @version $Id: Display_content.jsp,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
 */
--%>
<%!
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger("org.navigoproject.ui.web.item.delivery.deliveryAssets.jsp");
%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>

<!--
<b>The Text</b>: <%= request.getAttribute("text") %> <br />

<b>The Query Parameter</b>: <%= request.getAttribute("queryValue") %> <br />

<b>The File name</b>: <%= request.getAttribute("fileName") %> <br />

<b>The File content type</b>: <%= request.getAttribute("contentType") %> <br />

<b>The File size</b>: <%= request.getAttribute("size") %> <br />

<b>The File data</b>: <br />
-->
<!--
<table align="left" width="80%"><tr><td width="150">Assessment Name</td><td>Assessment Description</td></tr>
<logic:iterate name="displayAssetsForm" id="asset" property="assets">
<tr><td>
<html:link page="/asi/delivery/xmlSelectAction.do" paramId="assessmentId" paramName="asset" paramProperty="id">
<bean:write name="asset" property="displayName" /> </html:link>
</td><td>Description</td></tr>
</logic:iterate>
</table>
-->
<div>
You have successfully uploaded the file. 
<br />
<br />
<html:form action="/asi/author/assessment/assessmentAction.do" enctype="multipart/form-data">
 <html:submit  value ="Go back to authoring"/>
</html:form> 
</div>