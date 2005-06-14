<%--
/**
 * <p>Title: NavigoProject.org</p>
 * <p>Description: OKI based implementation</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id: SelectAssessment.jsp,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
 */
--%>
<%!
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger("org.navigoproject.ui.web.item.asi.select.SelectAssessment.jsp");
%>
<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<html:html xhtml="true" locale="true">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>
Navigo: Select Assessment
<html:base/>
</title>
<body bgcolor="#ffffff">
<h1>
Select Assessment
</h1>
<html:errors/>
<html:form action="/asi/select/selectAssessment" method="POST">
<font face="Verdana">
        <table border="1" cellspacing="1" width="824" height="132">
          <tr>
            <td align="center" width="372" height="1">
              <font size="2">Order by:</font>&nbsp;
              <select size="1" name="Order_by" style="font-family: Verdana; font-size: 10pt">
              <option>Available Date</option>
              <option>Test Name</option>
              <option>Completion Date</option>
              </select></p>
            </form>
            </td>
            <td align="center" width="223" height="1"></td>
            <td align="center" width="211" height="1"></td>
          </tr>
          <tr>
            <td align="center" width="372" height="9"><b>
            <font face="Verdana" size="2">Test Name</font></b></td>
            <td align="center" width="223" height="9"><b>
            <font face="Verdana" size="2">Available</font></b></td>
            <td align="center" width="211" height="9"><b>
            <font face="Verdana" size="2">Completed (Review)</font></b></td>
          </tr>
          <tr>
            <td width="372" align="center" height="21">
            <font face="Verdana" size="2">Introductory Quiz</font></td>
            <td align="center" width="223" height="21"><font size="2">06/01/2003</font></td>
            <td align="center" width="211" height="21">
            <font size="2">
            06/01/2003
            </font></td>
          </tr>
          <tr>
            <td width="372" align="center" height="18">
            <font face="Verdana" size="2">Test One</font></td>
            <td align="center" width="223" height="18"><font size="2">06/04/2003</font></td>
            <td align="center" width="211" height="18">
            <font size="2">
            06/05/2003
            </font></td>
          </tr>
          <tr>
            <td width="372" align="center" height="18">
            <font face="Verdana" size="2">Midterm</font></td>
            <td align="center" width="223" height="18">
            <font size="2">
            06/05/2003
            </font></td>
            <td align="center" width="211" height="18"></td>
          </tr>
          <tr>
            <td width="372" align="center" height="18">
            <font face="Verdana" size="2">Final Exam (practice)</font></td>
            <td align="center" width="223" height="18"><font size="2">
            <html:link page="/asi/delivery/xmlSelectAction.do" paramId="assessmentId" paramProperty="assessment">06/05/2003</html:link></font></td>
            <td align="center" width="211" height="18"></td>
          </tr>
          <tr>
            <td width="372" align="center" height="18"><font size="2">Final Exam</font></td>
            <td align="center" width="223" height="18"><font size="2">06/26/2003</font></td>
            <td align="center" width="211" height="18"></td>
          </tr>
        </table>
</font>
</html:form>
</body>
</html:html>
