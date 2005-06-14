<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%-- include this where files need audio record and upload --%>
<%--
    a bean called recordingData of type edu.stanford.aam.util.RecordingData
    must be present and creates the settings for the sound recorder!
--%>
<logic:notPresent name="recordingData">
  <font color="red">RecordingData unavailable.  Unable to start applet.</font>
</logic:notPresent>

<logic:present name="recordingData">

<a
  href=<%=
    "'" +
      "<%=request.getContextPath()%>/applet/soundRecorder.jsp?" +
        "filename="
          %><bean:write name="recordingData"
              property="fileName"/>.<bean:write name="recordingData"
              property="fileExtension"/><%=
        "&seconds=" %><bean:write name="recordingData" property="seconds"/><%=
        "&limit=" %><bean:write name="recordingData" property="limit"/><%=
    "'" %>

  onClick=<%=
    "\"" +
    "startTarget('AudioCapture', 500, 400); this.href=" +
      "'" +
      "<%=request.getContextPath()%>/applet/soundRecorder.jsp?" +
        "filename="
          %><bean:write name="recordingData"
              property="fileName"/>.<bean:write name="recordingData"
              property="fileExtension"/><%=
        "&seconds=" %><bean:write name="recordingData" property="seconds"/><%=
        "&limit=" %><bean:write name="recordingData" property="limit"/><%=
      "'" +
    "\""
  %>

  onMouseOver="window.status='Record Audio Clip'; return true"

  target="AudioCapture"><img src="<%=request.getContextPath()%>/images/recordresponse.gif"
        alt="Record Audio" border="0"></a>

</logic:present>

