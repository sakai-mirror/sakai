<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" import="java.sql.*" errorPage="" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="java.util.Date" %>
<%--@ page import="edu.stanford.aam.util.SortableDate" --%>

<%@ include file="../params.jsp" %>
<%-- should be passed in from the session
<%
  request.setAttribute("course_name", "" + session.getAttribute("course_name"));
  request.setAttribute("course_id", "" + session.getAttribute("course_id"));
  request.setAttribute("agent_name", "" + session.getAttribute("agent_name"));
  request.setAttribute("agent_id", "" + session.getAttribute("agent_id"));
%>

<jsp:useBean id="submissionStatus" scope="session" class="org.navigoproject.ui.web.form.evaluation.TotalScoresForm" />
<jsp:useBean id="totalScores" scope="session" class="org.navigoproject.ui.web.form.evaluation.TotalScoresForm" />


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Grading - Submission Status</title>
<link href="stylesheets/main.css" rel="stylesheet" type="text/css">
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
    o.value="0";
  }
}

function autoSave(ctr){
  document.forms[0].submit();
}


//--></script>

</head>

<body>
<html:form action="submissionStatus.do" method="post"> 

<table width="100%">

<tr class="heading"><td >Submission Status</td></tr></table>


<table width="100%">
  <tbody>
  <tr>
    <td colspan="8">
      
     Grading: <bean:write name='submissionStatus' property='assessmentName' />
      <br>
     Total Possible:<bean:write name="submissionStatus" property="maxScore" /> 
       
     
    </td>
    <td align="right">
      <input type="button" name="button" value="Question View" onClick=location.href="<%=request.getContextPath()%>/questionScores.do?id=<bean:write name='submissionStatus' property='assessmentId'/>"> 
<input type="button" name="button" value="Statistics View" onClick=location.href="<%=request.getContextPath()%>/histogramScores.do?id=<bean:write name='submissionStatus' property='assessmentId'/>"> 

<input type="button" name="button" value="Total Score View" onClick=location.href="<%=request.getContextPath()%>/totalScores.do?id=<bean:write name='submissionStatus' property='assessmentId'/>"> 
<%--html:link page='/questionScores.do' paramId='id' paramName='submissionStatus' paramProperty='assessmentId'>Question View</html:link>&nbsp;&nbsp;

<html:link page='/histogramScores.do' paramId='id' paramName='submissionStatus' paramProperty='assessmentId'>Statistics View</html:link>&nbsp;&nbsp;
<html:link page='/totalScores.do' paramId='id' paramName='submissionStatus' paramProperty='assessmentId'>Total Score View</html:link--%>
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
<logic:equal name="submissionStatus" property="allSubmissions" value="false">
checked="check"</logic:equal>
 onclick=location.href='<%=request.getContextPath()%>/submissionStatus.do?id=<bean:write name="submissionStatus" property="assessmentId"/>&allSubmissions=false'>

Last Submission<br>

<input type=radio name="allSubmissions" value="true"
<logic:equal name="submissionStatus" property="allSubmissions" value="true">
checked="check"</logic:equal>
onclick=location.href='<%=request.getContextPath()%>/submissionStatus.do?id=<bean:write name="submissionStatus" property="assessmentId"/>&allSubmissions=true'>

All Submissions

</td>
          </tr>

          <%--tr>
            <td>Groups:</td>
            <td><bean:write name="submissionStatus" property="groupName"/></td>
          </tr--%>
       </table>
     
 <table width=100%>

    <tr>
      <th>
         <logic:equal name="submissionStatus" property="allSubmissions" value="true">
         All Submissions
        </logic:equal>
         <logic:equal name="submissionStatus" property="allSubmissions" value="false">
         Last Submission
        </logic:equal>
        for All Participants.
      </th>
    </tr>

     <tr class="altBackground">
      <td>
      
       <%
        // index for each letter
       for (char c='A'; c<'Z' + 1; c++)
          {
            // the student's last name starts with this letter, make this a link
            %>
           
            <logic:match name="submissionStatus"
              property="agentInitials" value='<%= "" + c %>'>
        | <a href='<%= "#" + c %>'><b><%= c %></b></a>
            </logic:match>

            <%
            // the student's last name DOES NOT start with this letter
            %>

            <logic:notMatch name="submissionStatus"
              property="agentInitials" value='<%= "" + c %>'>
        | <b><%= c %></b>
            </logic:notMatch>

            <%
          }
       %>
     
      </td>
    </tr>
  
    <tr>
      <td width=100%>
        <table width=100%>
          <tr>
            <th>
 <logic:notEqual name="totalScores" property="sortType" value="lastName">
<A class="unit1heading" href="<%=request.getContextPath()%>/submissionStatus.do?id=<bean:write name='submissionStatus' property='assessmentId'/>&sortType=lastName">
</logic:notEqual> Name
 <logic:notEqual name="totalScores" property="sortType" value="lastName">
</A>
 </logic:notEqual>
</th>
            <th>
 <logic:notEqual name="totalScores" property="sortType" value="sunnetid">
<A class="unit1heading" href="<%=request.getContextPath()%>/submissionStatus.do?id=<bean:write name='submissionStatus' property='assessmentId'/>&sortType=sunetid">
 </logic:notEqual>
UserID
 <logic:notEqual name="totalScores" property="sortType" value="sunetid">
</A>
 </logic:notEqual>
</th>
           
            <th>
 <logic:notEqual name="totalScores" property="sortType" value="submissionDate">
<A class="unit1heading" href="<%=request.getContextPath()%>/submissionStatus.do?id=<bean:write name='submissionStatus' property='assessmentId'/>&sortType=submissionDate">
</logic:notEqual>
Date
 <logic:notEqual name="totalScores" property="sortType" value="submissionDate">
</A>
</logic:notEqual>
</th>
           
          </tr>
 <logic:iterate name="submissionStatus" id="description" property="agents"
    type="org.navigoproject.business.entity.evaluation.model.AgentResults" indexId="ctr">
<% if(ctr.intValue() % 2 !=0){%> 
      <tr class="altBackground">
       <%}else{%>
<tr>
 <%}%>

      
        <td> <% out.print("<a name='#");
      %><jsp:getProperty name="description" property="lastInitial" /><%
       out.print("'/>");
      %>

<html:link page='/asi/grading/xmlSelectAction.do' paramId='assessmentId' paramName='description' paramProperty='assessmentResultID'>
<bean:write name="description" property="lastName" />, <bean:write name="description" property="firstName"/>
</html:link>
            </a></td>
        <td><bean:write name="description" property="sunetid"/></td>
     
        <td><bean:write name="description" property="submissionDate"/></td>
       
      </tr>
</logic:iterate>
        </table>
      </td>
    </tr>

  
   
   </table>

<table width=100%>
 
  <tr>
    <td align="middle">


<input type=button value="Done" onclick="location.href='<%=request.getContextPath()%>/Navigation.xml?navigationSubmit=Author'"/>

    </td>
   </tr>
 </table>
</html:form>
</body>
</html>
