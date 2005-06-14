<%@ page import="org.navigoproject.business.entity.RecordingData" %>
<%-- $Id: recordingScriptInfo.jsp,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $ -->
<%-- include this where files need audio record and upload --%>

<%--
    a bean called recordingData of type edu.stanford.aam.util.RecordingData
    must be present and creates the settings for the sound recorder!
--%>
<logic:notPresent name="recordingData">
  <font color="red">RecordingData unavailable.  Unable to use recording applet.</font>
</logic:notPresent>
<logic:present name="recordingData">
<script language="javascript">
		 // audio applet data for htmlarea audio popup
		  var audioAgentName= '<bean:write name="recordingData" property="agentName"/>';
		  var audioAgentId= '<bean:write name="recordingData" property="agentId"/>';
		  var audioCourseAssignmentContext=
							'<bean:write name="recordingData" property="courseAssignmentContext"/>';
		  var audioFileExtension= '<bean:write name="recordingData"
              property="fileExtension"/>';
		  var audioFileName= '<bean:write name="recordingData"
              property="fileName"/>';
		  var audioLimit= '<bean:write name="recordingData" property="limit"/>';
		  var audioDir= '<bean:write name="recordingData" property="dir"/>';
		  var audioSeconds= '<bean:write name="recordingData" property="seconds"/>';
		  var audioAppName= '<bean:write name="recordingData" property="appName"/>';
		  var audioImageURL= '<bean:write name="recordingData" property="imageURL"/>';
</script>
</logic:present>



