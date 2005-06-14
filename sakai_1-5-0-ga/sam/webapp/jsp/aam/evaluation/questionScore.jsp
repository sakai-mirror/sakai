<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" import="java.sql.*" errorPage="" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<jsp:useBean id="questionScores" scope="session" class="org.navigoproject.ui.web.form.evaluation.QuestionScoresForm" />
<%-- recording data should be passed in from the session from the action--%>
<%
  request.setAttribute("recordingData", session.getAttribute("recordingData"));
%>
<%-- publishedID is passed in from the session from the action --%>
<%
  request.setAttribute("publishedID", session.getAttribute("publishedID"));
%>

<html>
<head>
<title>Grading - Question Scores View</title>
<link type="TEXT/CSS" href="<%=request.getContextPath()%>/css/main.css" rel="stylesheet">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script language="javascript"><!--
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
//    alert("not numeric");
    o.value="0";
  }
}

function autoSave(){
  document.forms[0].target = "dummyFrame";
  document.forms[0].submit();
}

function doRoleSelection(index)
{
  if(index.options[index.selectedIndex].value !="")
  {
    location.href="<%=request.getContextPath()%>/questionScores.do?roleSelection="+index.options[index.selectedIndex].value;
  }
}
function popupWin(url)
{
window.open(url,"uploaded win", "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,width=600, height=600");
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

<html:form action="editQuestionResults.do" method="post">
<table width="100%" border=0 cellspacing=2 cellpadding=4>

<tr class="heading"><td> Grading - Question <bean:write name="questionScores" property="questionNumber" />
<logic:equal name="questionScores" property="anonymous" value="true">
(anonymous)
</logic:equal>
<logic:equal name="questionScores" property="anonymous" value="false">
(by username)
</logic:equal>
</td></tr></table>


<table width="100%">
  <tbody>
  <tr>
    <td colspan="8">

     Grading: <bean:write name='questionScores' property='assessmentName' />
      <br>
     Total Possible:<bean:write name="questionScores" property="maxScore" />


    </td>
    <td align="right">
			<%-- only allow export if an audio or file upload question --%>
			<%-- (disabled for now)                                    --%>
  	 	<logic:equal name="questionScores" property="questionType" value="audio">
      <!-- <input type="button" name="button" value="Export" onClick=location.href="<%=request.getContextPath()%>/exportZip.do?id=<bean:write name='questionScores' property='assessmentId'/>&publishedID=<bean:write name='publishedID'/>">-->
	   	</logic:equal>
  		<logic:equal name="questionScores" property="questionType" value="file upload">
      <!-- <input type="button" name="button" value="Export" onClick=location.href="<%=request.getContextPath()%>/exportZip.do?id=<bean:write name='questionScores' property='assessmentId'/>&publishedID=<bean:write name='publishedID'/>">-->
  		</logic:equal>
      <input type="button" name="button" value="Total Score View" onClick=location.href="<%=request.getContextPath()%>/totalScores.do?id=<bean:write name='questionScores' property='assessmentId'/>&publishedID=<bean:write name='publishedID'/>">
      <input type="button" name="button" value="Statistics View" onClick=location.href="<%=request.getContextPath()%>/histogramScores.do?id=<bean:write name='questionScores' property='assessmentId'/>&publishedID=<bean:write name='publishedID'/>">

<%--html:link page='/totalScores.do' paramId='id' paramName='questionScores' paramProperty='assessmentId'>Total Score View</html:link>&nbsp;&nbsp;
<html:link page='/histogramScores.do' paramId='id' paramName='questionScores' paramProperty='assessmentId'>Statistics View</html:link--%>
</td>
     </tr>
</table>
<table width=100%>
    <tr>
      <th colspan=2>Grading Settings</th>
    </tr>

   <tr>
      <td width="10%" align="left" valign="top">Question:</td>
      <td width="90%" align="left" valign="top">
<% for(int p=0;p<questionScores.getParts().intValue(); p++) { %>

<% if (questionScores.getLength(new Integer(p)).intValue() > 0) { %>
    Part <%= p+1 %>:
    <% for (int q=0; q<questionScores.getLength(new Integer(p)).intValue(); q++) { %>
 <input type="radio" name="radiobutton" value="<%= p+1 %>"
 <%
            if (questionScores.getQuestionNumber().equals("" +(q+1) )
            && questionScores.getPartNumber().equals("" +( p+1)	))
             {
               out.println("checked=\"checked\"");
             }
        %>
onclick=location.href='<%=request.getContextPath()%>/questionScores.do?id=<bean:write name="questionScores" property="assessmentId"/>&publishedID=<bean:write name="publishedID"/>&questionNumber=<%=q+1%>&partNumber=<%=p+1%>'>Q<%=q+1 %> <% } %><br> <% } %>
<% }%>



     </td>
  </tr>
  <tr>
     <td width="10%" align="left" valign="top">Submissions:</td>
     <td width="90%" align="left" valign="top">
 <input type=radio name="allSubmissions" value="false"
<logic:equal name="questionScores" property="allSubmissions" value="false">
checked="check"</logic:equal>

onclick=location.href='<%=request.getContextPath()%>/questionScores.do?id=<bean:write name="questionScores" property="assessmentId"/>&publishedID=<bean:write name="publishedID"/>&allSubmissions=false'>

Last Submission<br>

<input type=radio name="allSubmissions" value="true"
<logic:equal name="questionScores" property="allSubmissions" value="true">
checked="check"</logic:equal>
onclick=location.href='<%=request.getContextPath()%>/questionScores.do?id=<bean:write name="questionScores" property="assessmentId"/>&publishedID=<bean:write name="publishedID"/>&allSubmissions=true'>


All Submissions<br>

     </td>
 </tr>


</table>
<table width=100%>

  <tr>
    <td width=100%>
      <table width=100%>
        <tr>
          <th width=20%>Question <bean:write name="questionScores" property="questionNumber" />/<%=questionScores.getLength(new Integer(""+((new Integer(questionScores.getPartNumber()).intValue())-1))).intValue()%></th>
          <th width=30%>Part <bean:write name="questionScores" property="partNumber"/>: <bean:write name="questionScores" property="partText"/></th>
          <th width=30%><bean:write name="questionScores" property="questionType" /></th>
          <th width=20% align=right><bean:write name="questionScores" property="questionPoint"/> points</th>
        <tr>
      </table>
    </td>
  </tr>
  <tr>
    <td>
        <bean:write name="questionScores" property="questionNumber" />.&nbsp;
        <bean:write name="questionScores" property="item.itemText"
          filter="false"/>
				<bean:write name="questionScores" property="questionAnswer" filter="false"/>
    </td>
  </tr>
</table>
<table width=100%>

  <tr>
    <th>
    <logic:equal name="questionScores" property="allSubmissions" value="true">
         All Submissions
        </logic:equal>
         <logic:equal name="questionScores" property="allSubmissions" value="false">
         Last Submission
        </logic:equal>
        for All Participants.</th>
  </tr>

  <tr  class="altBackground">
    <td>

       <%
        // index for each letter
       for (char c='A'; c<'Z' + 1; c++)
          {
            // the student's last name starts with this letter, make this a link
            %>

            <logic:match name="questionScores"
              property="agentInitials" value='<%= "" + c %>'>
        | <a href='<%= "#" + c %>'><b><%= c %></b></a>
            </logic:match>

            <%
            // the student's last name DOES NOT start with this letter
            %>

            <logic:notMatch name="questionScores"
              property="agentInitials" value='<%= "" + c %>'>
        | <b><%= c %></b>
            </logic:notMatch>

            <%
          }
       %>

     </td>
   </tr>
<logic:equal name="questionScores" property="anonymous" value="true">
<%@ include file="anonymousQuestion.jsp" %>
</logic:equal>
 <logic:equal name="questionScores" property="anonymous" value="false">
<%@ include file="nonAnonymousQuestion.jsp" %>
</logic:equal>

<table width=100%>
  <tr>
    <td align=center>
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
</html:html>

