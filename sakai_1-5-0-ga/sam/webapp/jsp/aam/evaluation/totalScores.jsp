<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" import="java.sql.*" errorPage="" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="java.util.Date" %>
<%--@ page import="edu.stanford.aam.util.SortableDate" --%>

<%@ include file="../params.jsp" %>
<%-- recording data should be passed in from the session from the action--%>
<%
  request.setAttribute("recordingData", session.getAttribute("recordingData"));
%>
<%-- publishedID is passed in from the session from the action --%>
<%
  request.setAttribute("publishedID", session.getAttribute("publishedID"));
%>

<jsp:useBean id="totalScores" scope="session" class="org.navigoproject.ui.web.form.evaluation.TotalScoresForm" />

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Grading - Total Scores View</title>
<link type="TEXT/CSS" href="<%=request.getContextPath()%>/css/main.css" rel="stylesheet">
<script language="javascript"><!--
var item=0;
// test if positive or negative integer (+12 format not accepted)
function isInteger(s) {
  if (!s) return false;
  var Chars = "0123456789-";

  for (var i = 0; i < s.length; i++) {
   if (Chars.indexOf(s.charAt(i)) == -1){
      return false;
   }
   // minus tested in first character only
   Chars = "0123456789";
  }
  return true;
}
// if a "numeric" field contains non-numeric values set to 0
function fixMyNumeric(o){
  s=o.value;
  if (!isInteger(s)){
    o.value="0";
  }
}

function autoSave(){
  document.forms[0].target = "dummyFrame";
  document.forms[0].submit();
}

//--></script>
<!-- HTMLAREA -->
<link type="TEXT/CSS" rel="STYLESHEET" href="<%=request.getContextPath()%>/htmlarea/htmlarea.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/htmlarea/htmlarea.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/htmlarea/lang/en.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/htmlarea/dialog.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/htmlarea/popupwin.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/htmlarea/navigo_js/navigo_editor.js"></script>
<script language="javascript"><!--
// HTMLAREA
var item =0;
HTMLArea.loadPlugin("SpellChecker", "<%=request.getContextPath()%>/htmlarea/");
var ta_editor =  [];
var hidden = [];
var textAreaNames = [];
var runFocus=true;

function startup()
{
  loadEditor("<%=request.getContextPath()%>/htmlarea/",1,0,"three");
}
//--></script>
</head>

<body onload="startup();">
<%@ include file="recordingScriptInfo.jsp" %>

<html:form action="editTotalResults.do" method="post">
<table width="100%" border=0 cellspacing=2 cellpadding=4>

<tr class="heading"><td>Grading - Total Score

<logic:equal name="totalScores" property="anonymous" value="true">
(anonymous)
</logic:equal>
<logic:equal name="totalScores" property="anonymous" value="false">
(by username)
</logic:equal>

</td></tr></table>


<table cellspacing="2" cellpadding="4" width="100%" border="0">
  <tbody>
  <tr>
    <td colspan="8">

     Grading: <bean:write name='totalScores' property='assessmentName' />
      <br>
     Total Possible:<bean:write name="totalScores" property="maxScore" />

    </td>
    <td align="right">

<logic:equal name="totalScores" property="anonymous" value="true">
<input type="button" name="button" value="Submission Status"
  onClick=location.href="<%=request.getContextPath()%>/submissionStatus.do?id=<bean:write name='totalScores' property='assessmentId'/>&publishedID=<bean:write name='publishedID'/>">
</logic:equal>

<input type="button" name="button" value="Question View"
  onClick=location.href="<%=request.getContextPath()%>/questionScores.do?id=<bean:write name='totalScores' property='assessmentId'/>&publishedID=<bean:write name='publishedID'/>">

<input type="button" name="button" value="Statistics View"
  onClick=location.href="<%=request.getContextPath()%>/histogramScores.do?id=<bean:write name='totalScores' property='assessmentId'/>&publishedID=<bean:write name='publishedID'/>">

</td>
     </tr>
</table>
<table width=100%>

    <tr>
      <th colspan=2>Grading Settings</th>
    </tr>

    <tr>

            <td width=10% align="left" valign="top">Submissions:</td>
            <td width=90% align="left" valign="top">

 <input type=radio name="allSubmissions" value="false"
<logic:equal name="totalScores" property="allSubmissions" value="false">
checked="check"</logic:equal>

onclick=location.href='<%=request.getContextPath()%>/totalScores.do?id=<bean:write name="totalScores" property="assessmentId"/>&publishedID=<bean:write name="publishedID"/>&allSubmissions=false'>

Last Submission<br>

<input type=radio name="allSubmissions" value="true"
 <logic:equal name="totalScores" property="allSubmissions" value="true">
checked="check"</logic:equal>

onclick=location.href='<%=request.getContextPath()%>/totalScores.do?id=<bean:write name="totalScores" property="assessmentId"/>&publishedID=<bean:write name="publishedID"/>&allSubmissions=true'>


All Submissions


</td>
          </tr>


       </table>
<table width=100%>

    <tr>
      <th>
        <logic:equal name="totalScores" property="allSubmissions" value="true">
         All Submissions
        </logic:equal>
         <logic:equal name="totalScores" property="allSubmissions" value="false">
         Last Submission
        </logic:equal>
        for All Participants</th>
    </tr>

     <tr class="altBackground">
      <td>

       <%
        // index for each letter
       for (char c='A'; c<'Z' + 1; c++)
          {
            // the student's last name starts with this letter, make this a link
            %>

            <logic:match name="totalScores"
              property="agentInitials" value='<%= "" + c %>'>
        | <a href='<%= "#" + c %>'><b><%= c %></b></a>
            </logic:match>

            <%
            // the student's last name DOES NOT start with this letter
            %>

            <logic:notMatch name="totalScores"
              property="agentInitials" value='<%= "" + c %>'>
        | <b><%= c %></b>
            </logic:notMatch>

            <%
          }
       %>

      </td>
    </tr>


<logic:equal name="totalScores" property="anonymous" value="true">
  <%@ include file="anonymous.jsp" %>
</logic:equal>
 <logic:equal name="totalScores" property="anonymous" value="false">
  <%@ include file="nonAnonymous.jsp" %>
</logic:equal>

<table width=100%>

  <tr>
    <td align="middle">
     <html:submit value="Save and Exit" property="submitexit"/>
     <html:submit value="Save and Continue" property="submitcontinue"/>
<!--
     <html:button property="button" onclick="location.href='<%=request.getContextPath()%>/Navigation.xml?navigationSubmit=Author'" value="Cancel"/>
-->
     <input type="button" name="button" value="Cancel" onclick="location.href='<%=request.getContextPath()%>/Navigation.xml?navigationSubmit=Author'"/>
    </td>
   </tr>
 </table>
</html:form>
</body>
</html>
