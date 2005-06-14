<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" import="java.sql.*" errorPage="" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<jsp:useBean id="histogramScores" scope="session" class="org.navigoproject.ui.web.form.evaluation.HistogramScoresForm" />
<jsp:useBean id="histogramQuestionScores" scope="session" class="org.navigoproject.ui.web.form.evaluation.HistogramQuestionScoresForm" />
<%-- publishedID is passed in from the session from the action --%>
<%
  request.setAttribute("publishedID", session.getAttribute("publishedID"));
%>

<html:html>
<head>
<script language="javascript">

function doRoleSelection(index)
{
  if(index.options[index.selectedIndex].value !="")
  {
    location.href="<%=request.getContextPath()%>/histogramScores.do?roleSelection="+index.options[index.selectedIndex].value;
  }
}

</script>
<title>Grading - Histogram View</title>
<link type="TEXT/CSS" href="<%=request.getContextPath()%>/css/main.css" rel="stylesheet">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
</head>
<body>

<table width="100%">
  <tr class="heading">
    <td>Grading - Statistics and Histogram
    </td>
  </tr>
</table>

<table width=100%>
  <tr>
    <td align=left>Statistics: <bean:write name="histogramScores" property="assessmentName"/>
    </td>
    <td align=right>
      <input type="button" name="button" value="Question View" onClick=location.href="<%=request.getContextPath()%>/questionScores.do?id=<bean:write name='histogramScores' property='assessmentId'/>&publishedID=<bean:write name='publishedID'/>">
      <input type="button" name="button" value="Total Score View" onClick=location.href="<%=request.getContextPath()%>/totalScores.do?id=<bean:write name='histogramScores' property='assessmentId'/>&publishedID=<bean:write name='publishedID'/>">

<%--html:link page='/questionScores.do' paramId='id' paramName='histogramScores' paramProperty='assessmentId'>Question View</html:link>&nbsp; &nbsp; <html:link page='/totalScores.do' paramId='id' paramName='histogramScores' paramProperty='assessmentId'>Total Score View</html:link--%>
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
       <%
          if (histogramScores.getAllSubmissions()==false)
             out.println("checked=\"checked\"");

      %>
      onclick=location.href='<%=request.getContextPath()%>/histogramScores.do?id=<bean:write name="histogramScores" property="assessmentId"/>&publishedID=<bean:write name="publishedID"/>&allSubmissions=false'
      > Last Submission<br>

			<input type=radio name="allSubmissions" value="true"
      <%
            if (histogramScores.getAllSubmissions()==true)
               out.println("checked=\"checked\"");

      %>
			onclick=location.href='<%=request.getContextPath()%>/histogramScores.do?id=<bean:write name="histogramScores" property="assessmentId"/>&publishedID=<bean:write name="publishedID"/>&allSubmissions=true'
			>  All Submissions
          <%--logic:equal name="histogramScores" property="allSubmissions" value="true">
                  <html:radio name="histogramScores" property="allSubmissions" value="true"/>Last Submission<br>
                  <html:radio name="histogramScores" property="allSubmissions" value="false"/> All Submissions
         </logic:equal>
         <logic:equal name="histogramScores" property="allSubmissions" value="false">
                  <html:radio name="histogramScores" property="allSubmissions" value="false"/>Last Submission<br>
                  <html:radio name="histogramScores" property="allSubmissions" value="true"/> All Submissions
         </logic:equal--%>
   </td>
  </tr>
</table>

<br>
<logic:equal name="histogramScores" property="numResponses" value="0">
      No Responses found
</logic:equal>

<logic:notEqual name="histogramScores" property="numResponses" value="0">

<table width="100%">
 <tr>
  <th width=100%>

  	<logic:equal name="histogramScores" property="allSubmissions" value="true">
       All Submissions
    </logic:equal>
    <logic:equal name="histogramScores" property="allSubmissions" value="false">
       Last Submission
    </logic:equal>
    for All Participants.

	</th>
  </tr>
	<tr>
		<td>
		</td>
	</tr>
  <tr>
    <th>
	Total Score
    </th>
  </tr>
  <tr>
    <td height="117" colspan="2" align=left>
       <table bgcolor=E1E1E1>
       <%
				for(int i=0;i<histogramScores.getArrayLength();i++){%>
         <tr>
           <td width=5%>&nbsp;</td>
           <td width=95% align=left> <img src="../images/reddot.gif" height="20" width=<%out.println(histogramScores.getColumnHeight()[i]);%> >&nbsp;<%out.println(histogramScores.getNumStudentCollection()[i]);%> <% if(histogramScores.getNumStudentCollection()[i]>1){%>responses<%}else{%> response<%}%></td>
         </tr>
         <tr>
           <td>&nbsp;</td>
           <td align=left><%out.println(histogramScores.getRangeCollection()[i]);%> &nbsp; points</td>
         </tr>
     		<%
        }
        %>
        <tr>
          <td>&nbsp;</td>
          <td align=middle>
						<span class="bold">Number of Students</span></font>
					</td>
        </tr>
      </table>
     </td>
    </tr>

    </table>
    </td>
  </tr>

  <tr>
    <td>

     <bean:write name="histogramScores" property="numResponses"/> responses<br>
     Mean = <bean:write name="histogramScores" property="mean"/><br>
     Median = <bean:write name="histogramScores" property="median"/><br>
     Mode = <bean:write name="histogramScores" property="mode"/><br>
     Range = <bean:write name="histogramScores" property="range"/><br>
     Quartile 1 = <bean:write name="histogramScores" property="q1"/><br>
     Quartile 3 = <bean:write name="histogramScores" property="q3"/><br>
     Standard Deviation = <bean:write name="histogramScores" property="standDev"/><br>
     Total Possible = <bean:write name="histogramScores" property="totalScore"/><br>

       <%--   interval option
       <html:form action="histogramScores.do" method="post">
       Interval:
       <html:text size="3" name="histogramScores" property="interval"/>
       <html:submit value="Submit" property="submit"/><br>
       <i>  To change interval for the totalScore histogram, please enter an integer between 1 - <bean:write name="histogramScores" property="q4"/>.</i>
       </html:form>
--%>
    </td>
 </tr>
</table>

</logic:notEqual>

<logic:iterate name="histogramScores" property="histogramQuestions" id="question"
	type="org.navigoproject.ui.web.form.evaluation.HistogramQuestionScoresForm" indexId="ctr">

<table width="100%" border=0 cellspacing=2 cellpadding=4>
  <tr>
    <th>

			Part <bean:write name="question" property="partNumber"/>,
			Question <bean:write name="question" property="questionNumber"/>
			(<bean:write name="question" property="questionType" />)

		</th>
  </tr>

<tr><td><bean:write name="question" property="questionText" filter="false"/></td></tr>
<logic:equal name="question" property="numResponses" value="0">
   <tr><td>   No Responses found. </td></tr>
</logic:equal>
<logic:notEqual name="question" property="numResponses" value="0">
  <tr>
    <td height="117" colspan="2" align=left>
     <table bgcolor=E1E1E1>
      <tr>
      <% for(int i=0;i<question.getArrayLength();i++){%>
       	<td width=5% align=right>
<%
// TODO: why isn't split() working?

        java.util.StringTokenizer st = new java.util.StringTokenizer(question.getCorrectAnswer() , "|");
while (st.hasMoreTokens()) {
  String nexttoken = st.nextToken();
	if ( (nexttoken.trim()).equals(question.getRangeCollection()[i].trim())) {
/*
	String[] answerArray = question.getCorrectAnswerArray();
      for(int k=0;k<answerArray.length ; k++)  {
	if ( (answerArray[k].trim()).equals(question.getRangeCollection()[i].trim())) {

*/

%>
        <image src="../images/checkmark.gif">
<%}}%>
	</td>
       <td width=90% align=left>
					<img src="../images/reddot.gif" height="20"
						width=<%out.println(question.getColumnHeight()[i]);%> >&nbsp;
						<%out.println(question.getNumStudentCollection()[i]);%><% if(question.getNumStudentCollection()[i]>1){%>responses<%}else{%> response<%}%>
				</td>
      </tr>
      <tr>
        <td>&nbsp</td>
        <td align=left>
					<%out.println(question.getRangeCollection()[i]);%> &nbsp;
<logic:notEqual name="question" property="questionType" value="True False">
<logic:notEqual name="question" property="questionType" value="Multiple Choice">
<logic:notEqual name="question" property="questionType" value="Multiple Correct Answer">
 points</logic:notEqual></logic:notEqual></logic:notEqual>
				</td>
      </tr>
    <%}%>
      <tr>
        <td>&nbsp;</td>
        <td align=middle>
					<span class="bold">Number of Students</span>
				</td>
      </tr>
    </table>
    </td>
  </tr>
  <tr>
    <td>
	<logic:equal name="question" property="questionType" value="Multiple Correct Answer">
	<bean:write name="question" property="numResponses"/> students responding, <bean:write name="question" property="totalResponses"/>
 responses. <br>

</logic:equal>
<logic:notEqual name="question" property="questionType" value="Multiple Correct Answer">
			<bean:write name="question" property="numResponses"/> responses <br>
</logic:notEqual>
  <logic:equal name="question" property="questionType" value="True False">

    Percentage of Students with Correct Answer =
		<bean:write name="question" property="percentCorrect"/>, &nbsp;
     Discrimination = <><br>

  </logic:equal>
  <logic:equal name="question" property="questionType" value="Multiple Choice">

    Percentage of Students with Correct Answer =
		<bean:write name="question" property="percentCorrect"/>, &nbsp;
     Discrimination = <><br>

  </logic:equal>
  <logic:equal name="question" property="questionType" value="Multiple Correct Answer">

    Percentage of Students with Correct Answer =
		<bean:write name="question" property="percentCorrect"/>, &nbsp;
     Discrimination = <><br>

  </logic:equal>

  <logic:notEqual name="question" property="questionType" value="True False">
  <logic:notEqual name="question" property="questionType" value="Multiple Choice">
  <logic:notEqual name="question" property="questionType" value="Multiple Correct Answer">
    Mean = <bean:write name="question" property="mean"/><br>
        Median = <bean:write name="question" property="median"/><br>
        Mode = <bean:write name="question" property="mode"/><br>
        Discrimination = <><br>
  </logic:notEqual>
  </logic:notEqual>
  </logic:notEqual>
  </td>
  </tr>
</logic:notEqual>
</table>
</logic:iterate>



<form>
	<center>
		<input type="button" value=" Done "
			onClick="location.href='<%=request.getContextPath()%>/Navigation.xml?navigationSubmit=Author'">
	</center>
</form>

</body>
</html:html>
