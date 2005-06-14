<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ page import="org.navigoproject.business.entity.assessment.model.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<jsp:useBean id="templateEditorForm" scope="session" class="org.navigoproject.ui.web.form.edit.TemplateForm" />
<%-- recording data should be passed in from the session from the action--%>
<%
  request.setAttribute("recordingData", session.getAttribute("recordingData"));
%>
<html>
<head>
<title>Template Editor</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<link href="../../css/main.css" rel="stylesheet" type="text/css">

<script type="text/JavaScript" language="JavaScript">
var runHide=true;

function showHideDiv(divNo)
  {
  var tmpdiv = "div" + divNo;
  var tmpimg = "img" + divNo;
  var divisionNo = getelm(tmpdiv);
  var imgNo = getelm(tmpimg);
  if(divisionNo)
    {
    if(divisionNo.style.display =="block")
      {
      divisionNo.style.display="none";
      imgNo.src ="<%=request.getContextPath()%>/images/right_arrow.gif";
      }
    else
      {
      divisionNo.style.display ="block";
      imgNo.src ="<%=request.getContextPath()%>/images/down_arrow.gif";
      // this is to fix Mozilla for HTMLArea inside of a DIV that gets hidden
      // todo: tho' no harm in IE add in a test to only do this only if Mozilla
      if (editor && divNo == "1")
        {
        editor.setMode("textmode");
        editor.setMode("wysiwyg");
        }
      }
    }

  }

function hideUnhideAllDivs(maxDivs,action)
  {
  if(runHide==true)
    {
    runHide=false;
    for(i=1; i <(maxDivs + 1); i++)
      {
      divisionNo = "div"+i;
      elem = document.getElementById(divisionNo);
      if (elem){
        elem.style.display =action;
      }
      }
    }
  }
</script>

<!-- HTMLAREA scripts -->
<link type="TEXT/CSS" rel="STYLESHEET" href="<%=request.getContextPath()%>/htmlarea/htmlarea.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/htmlarea/htmlarea.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/htmlarea/lang/en.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/htmlarea/dialog.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/htmlarea/popupwin.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/htmlarea/popups/popup.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/htmlarea/navigo_js/navigo_editor.js"></script>
<script type="text/JavaScript" language="JavaScript">
var item =0;
var ta_editor =  [];
var hidden = [];
var textAreaNames = [];
var editor;
function startup()
  {
  editor = initEditorById("templateDescription",
    "<%=request.getContextPath()%>/htmlarea/", "two", true);
  hideUnhideAllDivs(13, "none");
  }
</script>
<!-- end HTMLAREA scripts -->
</head>

<body onFocus="javascript:hideUnhideAllDivs(13,'none');" onload="javascript:startup();">
<%-- yes, this file should really not be in the evaluation directory :) --%>
<%@ include file="evaluation/recordingScriptInfo.jsp" %>
<div class="heading">Sakai Assessment Manager</div>
<h1>
  <table>
    <tr>
      <td class="h1text">Template Editor</td>
      <td class="alignRight"> <html:form action="/Navigation" method="post"> <html:hidden property="navigationSubmit"  value="Template Editor"/>
        <html:submit property="Submit" value="My Templates" /><br>
    </html:form>
        </td>
  </tr>
</table>
</h1>
<html:form action="editDescription.do" method="post">
<h2 onclick="javascript:showHideDiv(1);"><img id="img1" alt=""
src="<%=request.getContextPath()%>/images/right_arrow.gif" style="cursor:pointer;" />Template
  Information</h2>
<div class="h2unit" id="div1">
  <table width="100%" border="0">
    <tr>
      <td colspan="2" class="indent1"><br>
        Template Info</td>
      <td class="tdDisplay"> Displayed to Instructor? <html:checkbox name="templateEditorForm" property="value(templateName_isInstructorEditable)"/>
      </td>
    </tr>
    <tr>
      <td colspan="3" class="indent1"><br>
        <table border="0">
          <tr>
            <td>Template Title</td>
            <td colspan="2"><html:text size="60" property="templateName" /></td>
          </tr>
          <tr>
            <td>Author(s) (optional)</td>
            <td colspan="2"><html:text size="60" property="templateAuthor" /></td>
          </tr>
          <tr>
            <td>Description/Intro (optional)</td>
            <td width="30%">
              <html:textarea cols="60" rows="10"
                property="templateDescription" styleId="templateDescription" />
            </td>
            <td nowrap>
							<a href="javascript:toggle_display_toolbar('templateDescription',editor,'two');">
              Show/Hide<br>
              Editor</a></td>
          </tr>
        </table></td>
    </tr>
  </table>
</div>
<h2 onclick="javascript:showHideDiv(2);"><img id="img2" alt="closed section" src="<%=request.getContextPath()%>/images/right_arrow.gif" style="cursor:pointer;">Assessment
  Introduction</h2>
<div class="h2unit" id="div2">
  <table width="100%">
    <tr>
      <td colspan="2" class="indent1"><br>
        Assessment Author(s)<br>
        Assessment Creator<br>
        Assessment Description/Intro<br> </td>
      <td class="tdDisplay">Instructor Can Edit or View?<br> <html:checkbox property="value(assessmentAuthor_isInstructorEditable)"/><br>
        <html:checkbox property="value(assessmentCreator_isInstructorEditable)"/><br>
        <html:checkbox property="value(description_isInstructorEditable)"/></td>
    </tr>
  </table>
</div>
<h2 onclick="javascript:showHideDiv(3);"><img id="img3" alt="closed section" src="<%=request.getContextPath()%>/images/right_arrow.gif" style="cursor:pointer;">Delivery
  Dates</h2>
<div class="h2unit" id="div3">
  <table width="100%">
    <tr>
      <td colspan="2" class="indent1"><br>
        Due Date<br>
        Retract Date</td>
      <td class="tdDisplay">Instructor Can Edit?<br> <html:checkbox property="value(dueDate_isInstructorEditable)"/><br>
        <html:checkbox property="value(releaseDate_isInstructorEditable)"/> </td>
    </tr>
  </table>
</div>
<h2 onclick="javascript:showHideDiv(4);"><img id="img4" alt="closed section" src="<%=request.getContextPath()%>/images/right_arrow.gif" style="cursor:pointer;">Assessment
  released to:</h2>
<div class="h2unit" id="div4">
  <table width="100%">
    <tr>
      <td colspan="2" class="indent1"><br>
        Anonymous<br>
        Authenticated Users<br> </td>
      <td class="tdDisplay">Instructor Can Select?<br> <html:checkbox property="value(anonymousRelease_isInstructorEditable)"/><br>
        <html:checkbox property="value(authenticatedRelease_isInstructorEditable)"/><br>
    </tr>
  </table>
</div>
<h2 onclick="javascript:showHideDiv(5);"><img id="img5" alt="closed section" src="<%=request.getContextPath()%>/images/right_arrow.gif" style="cursor:pointer;">High
  Security</h2>
<div class="h2unit" id="div5">
  <table width="100%">
    <tr>
      <td colspan="2" class="indent1"> <br>
        Allow only specified IP Addresses<br>
        Secondary ID and Password</td>
      <td class="tdDisplay">Instructor Can Edit?<br> <html:checkbox property="value(ipAccessType_isInstructorEditable)"/><br>
        <html:checkbox property="value(passwordRequired_isInstructorEditable)"/>
      </td>
    </tr>
  </table>
</div>
<h2 onclick="javascript:showHideDiv(6);"><img id="img6" alt="closed section" src="<%=request.getContextPath()%>/images/right_arrow.gif" style="cursor:pointer;">Timed
  Assessment</h2>
<div class="h2unit" id="div6">
  <table width="100%">
    <tr>
      <td colspan="2" class="indent1"><br>
        Timed Assessment <br>
        Auto-submit when time expires </td>
      <td class="tdDisplay">Instructor Can Edit?<br> <html:checkbox property="value(timedAssessment_isInstructorEditable)" /><br>
        <html:checkbox property="value(timedAssessmentAutoSubmit_isInstructorEditable)" />
      </td>
    </tr>
  </table>
</div>
<h2 onclick="javascript:showHideDiv(7);"><img id="img7" alt="closed section" src="<%=request.getContextPath()%>/images/right_arrow.gif" style="cursor:pointer;">Assessment
  Organization</h2>
<div class="h2unit" id="div7">
  <table width="100%">
    <tr>
      <td colspan="2" class="indent1"> <strong>Navigation</strong><br> </td>
      <td class="tdDisplay"> Instructor Can Edit? <html:checkbox property="value(itemAccessType_isInstructorEditable)" />
      </td>
    </tr>
    <tr>
      <!-- item.access.0=Sequential Access to questions with return to previous pages. -->
      <td colspan="3" class="indent2"> Default Value:<br>
        <html:radio property="itemAccessType" value="1" /> Linear Access to questions
        with NO return to previous pages. There are only NEXT buttons to go forward.
        There is NO Table of Contents page.<br>
        <html:radio property="itemAccessType" value="2" /> Random access to questions
        from a Table of Contents. There are NEXT and PREV buttons on each page
        for navigation. Auto-submit when time expires.</td>
    </tr>
    <tr>
      <td colspan="2" class="indent1"> <strong>Question Layout</strong></td>
      <td class="tdDisplay"> Instructor Can Edit? <html:checkbox property="value(displayChunking_isInstructorEditable)" />
      </td>
    </tr>
    <tr>
      <!-- 2=Each Part can be divided to be displayed on multiple web pages. -->
      <td colspan="3" class="indent2"> Default Value:<br>
        <html:radio property="displayChunking" value="0"/> Each Question is on
        a separate Web page.<br> <html:radio property="displayChunking" value="1"/>
        Each Part is on a separate Web page.<br> <html:radio property="displayChunking" value="3"/>
        The complete Assessment is displayed on one Web page.</td>
    </tr>
    <tr>
      <td colspan="2" class="indent1"><strong>Numbering</strong></td>
      <td class="tdDisplay">Instructor Can Edit? <html:checkbox property="value(displayNumbering_isInstructorEditable)"/>
      </td>
    </tr>
    <tr>
      <td colspan="3" class="indent2">Default Value:<br>
        <html:radio property="questionNumbering" value="0"/> Continuous numbering
        between Parts.<br>
        <html:radio property="questionNumbering" value="1"/> Restart numbering
        for each Part.</td>
    </tr>
  </table>
</div>
<h2 onclick="javascript:showHideDiv(8);"><img id="img8" alt="closed section" src="<%=request.getContextPath()%>/images/right_arrow.gif" style="cursor:pointer;">Submissions</h2>
<div class="h2unit" id="div8">
  <table width="100%">
    <tr>
      <td colspan="2" class="indent1"> <strong>Number of Submissions Allowed</strong>
      </td>
      <td class="tdDisplay"> Instructor Can Edit? <html:checkbox property="value(submissionModel_isInstructorEditable)" />
      </td>
    </tr>
    <tr>
      <!-- submissionModel=1 means only ONE submission allowed-->
      <td colspan="3" class="indent2"> Default Value:<br> <html:radio property="submissionModel" value="0" />
        Unlimited<br>
        <html:radio property="submissionModel" value="2" /> Only <html:text property="submissionNumber" size="5" />
        submissions allowed.</td>
    </tr>
    <tr>
      <td colspan="2" class="indent1"><strong>Late Handling</strong><br> </td>
      <td class="tdDisplay"> Instructor Can Edit? <html:checkbox property="value(lateHandling_isInstructorEditable)" /></td>
    </tr>
    <tr>
      <!-- lateHandling = 2 is late handling is allowed and not tagged -->
      <td colspan="3" class="indent2"> Default Value:<br> <html:radio property="lateHandling" value="1"/>
        Late Submissions (After Due Date) will NOT be accepted.<br> <html:radio property="lateHandling" value="0"/>
        Late Submissions will be accepted and will be tagged late during grading.
      </td>
    </tr>
    <tr>
      <td colspan="2" class="indent1"><strong>Auto-Save</strong></td>
      <td class="tdDisplay">Instructor Can Edit? <html:checkbox property="value(autoSave_isInstructorEditable)"/>
      </td>
    </tr>
    <tr>
      <td colspan="3" class="indent2">Default Value:<br> <html:radio property="autoSave" value="0"/>
        User must click &#8220;Save&#8221; button to save input.<br> <html:radio property="autoSave" value="1"/>
        All user input saved automatically.</td>
    </tr>
  </table>
</div>
<h2 onclick="javascript:showHideDiv(9);"><img id="img9" alt="closed section" src="<%=request.getContextPath()%>/images/right_arrow.gif" style="cursor:pointer;">Submission
  Message </h2>
<div class="h2unit" id="div9">
  <table width="100%">
    <tr>
      <td colspan="2" class="indent1">Submission Message</td>
      <td class="tdDisplay">Instructor Can Edit?<br> <html:checkbox property="value(submissionMessage_isInstructorEditable)" />
      </td>
    </tr>
  </table>
</div>
<h2 onclick="javascript:showHideDiv(10);"><img id="img10" alt="closed section" src="<%=request.getContextPath()%>/images/right_arrow.gif" style="cursor:pointer;">Feedback</h2>
<div class="h2unit" id="div10">
  <table width="100%">
    <tr>
      <td colspan="2" class="indent1"><strong>Feedback Delivery</strong><br>
      </td>
      <td class="tdDisplay">Instructor Can Edit? <html:checkbox property="value(feedbackType_isInstructorEditable)" /></td>
    </tr>
    <tr>
      <td colspan="3" class="indent2"> Default Value:<br> <html:radio property="feedbackType" value="0" />
        Immediate Feedback <br> <html:radio property="feedbackType" value="1" />
        Feedback will be displayed to the student at a specified date<br> <html:radio property="feedbackType" value="2" />
        No Feedback will be displayed to the student </td>
    </tr>
    <tr>
      <td colspan="2" class="indent1"><strong>Select the Feedback Components</strong>:</td>
      <td class="tdDisplay"> Instructor Can Edit? <html:checkbox property="value(feedbackComponents_isInstructorEditable)" /></td>
    </tr>
    <tr>
      <td class="indent2"> Default Value:<br> <html:checkbox disabled="true" property="feedbackComponent_QuestionText" />
        Question Text <br>
        <html:checkbox property="feedbackComponent_StudentResp" /> Student Response
        <br> <html:checkbox property="feedbackComponent_CorrectResp" />
        Correct Response <br> <html:checkbox property="feedbackComponent_StudentScore" />
        Student&#8217;s Score </td>
      <td class="indent2"><br>
        <html:checkbox property="feedbackComponent_QuestionLevel" /> Question-level
        <br> <html:checkbox property="feedbackComponent_SelectionLevel" />
        Selection-level <br> <html:checkbox property="feedbackComponent_GraderComments" />
        Grader&#8217;s Comments <br> <html:checkbox property="feedbackComponent_Statistics" />
        Statistics and Histograms </td>
      <td class="indent2">&nbsp;</td>
    </tr>
  </table>
</div>
<h2 onclick="javascript:showHideDiv(11);"><img id="img11" alt="closed section" src="<%=request.getContextPath()%>/images/right_arrow.gif" style="cursor:pointer;">Grading</h2>
<div class="h2unit" id="div11">
  <table width="100%">
    <tr>
      <td colspan="2" class="indent1"> Default Value:<br> <html:checkbox property="anonymousGrading" />
        Anonymous Grading Only </td>
      <td class="tdDisplay">Instructor Can Edit? <html:checkbox property="value(testeeIdentity_isInstructorEditable)"/>
      </td>
    </tr>
    <tr>
      <td colspan="2" class="indent1"><strong>Gradebook Options</strong><br>
      </td>
      <td class="tdDisplay">Instructor Can Edit? <html:checkbox property="value(toGradebook_isInstructorEditable)"/>
      </td>
    </tr>
    <tr>
      <td colspan="3" class="indent2"> Default Value:<br> <html:radio property="toGradebook" value="0" />
        Grades to Default Gradebook <br> <html:radio property="toGradebook" value="1" />
        Grades to Selected Gradebook </td>
    </tr>
    <tr>
      <td colspan="2" class="indent1"><strong>Recorded Score If Multiple Submissions
        per User:</strong><br> </td>
      <td class="tdDisplay">Instructor Can Edit? <html:checkbox property="value(recordedScore_isInstructorEditable)"/>
      </td>
    </tr>
    <tr>
      <td colspan="3" class="indent2">Default Value:<br> <html:radio property="recordedScore" value="0" />
        Record the Highest Score <br> <html:radio property="recordedScore" value="1" />
        Record the Average Score </td>
    </tr>
  </table>
</div>
<h2 onclick="javascript:showHideDiv(12);"><img id="img12" alt="closed section" src="<%=request.getContextPath()%>/images/right_arrow.gif" style="cursor:pointer;">Graphics</h2>
<div class="h2unit" id="div12">
  <table width="100%">
    <tr>
      <td colspan="2" class="indent1"> <br>
        BG Color<br>
        BG Image<br>
      </td>
      <td class="tdDisplay">Instructor Can Edit? <br><html:checkbox property="value(bgColor_isInstructorEditable)"/>
        <br> <html:checkbox property="value(bgImage_isInstructorEditable)"/>
    </tr>
  </table>
</div>
<h2 onclick="javascript:showHideDiv(13);"><img id="img13" alt="closed section" src="<%=request.getContextPath()%>/images/right_arrow.gif" style="cursor:pointer;">Metadata</h2>
<div class="h2unit" id="div13">
  <table width="100%">
    <tr>
      <td colspan="2" class="indent1"> <br>
        Record Metadata for the full Assessment<br>
        Record Metadata for Parts <br>
        Record Metadata for Questions</td>
      <td class="tdDisplay">Instructor Can Edit?<br> <html:checkbox property="value(metadataAssess_isInstructorEditable)"/>
        <br> <html:checkbox property="value(metadataParts_isInstructorEditable)"/>
        <br> <html:checkbox property="value(metadataQuestions_isInstructorEditable)"/>
      </td>
    </tr>
  </table>
</div>
<p align="center">
  <input type="submit" name="Submit" value="Save">
  <html:reset onclick="history.go(-1)" value="Cancel"/> </p>
</html:form>
</body>
</html>
