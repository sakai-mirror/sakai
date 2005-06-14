<?xml version="1.0" encoding="UTF-8"?>
<!--
* <p>Title: NavigoProject.org</p>
* <p>Description: ASI Settings XML Style Sheet: Multimedia Audio</p>
* <p>Copyright: Copyright 2004 Trustees of Indiana University, Stanford University</p>
* @author <a href="mailto:esmiley@stanford.edu">Ed Smiley</a>
* @version $Id: settingsRecordingData.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
<!--
This creates JavaScript variables and should be called in a script tag.
Right now this is similar to the one used for authoring items, but there
may be differences later on.

Usage 
<script type="text/javascript">...
  <xsl:call-template name="js_recording_variables"/>...
-->
<xsl:template name="js_recording_variables">
  <!-- define some convenience variables --> 
  <xsl:variable name="audioAgentName"><xsl:value-of 
  	select="//RecordingData/AgentName"/></xsl:variable>
  <xsl:variable name="audioAgentId"><xsl:value-of 
  	select="//RecordingData/AgentId"/></xsl:variable>
  <xsl:variable name="audioCourseAssignmentContext"><xsl:value-of 
  	select="//RecordingData/CourseAssignmentContext"/></xsl:variable>
  <xsl:variable name="audioFileExtension"><xsl:value-of 
  	select="//RecordingData/FileExtension"/></xsl:variable>
  <xsl:variable name="audioFileName"><xsl:value-of 
  	select="//RecordingData/FileName"/></xsl:variable>
  <xsl:variable name="audioLimit"><xsl:value-of 
  	select="//RecordingData/Limit"/></xsl:variable>
  <xsl:variable name="audioDir">  <xsl:value-of 
  	select="//RecordingData/Dir"/></xsl:variable>
  <xsl:variable name="audioSeconds"><xsl:value-of 
  	select="//RecordingData/Seconds"/></xsl:variable>
  <xsl:variable name="audioAppName"><xsl:value-of 
  	select="//RecordingData/AppName"/></xsl:variable>
  <xsl:variable name="audioImageURL"><xsl:value-of 
  	select="//RecordingData/ImageURL"/></xsl:variable>

 <!-- generated JavaScript (indent moved slightly to make more readable in target HTML)-->
		 // audio applet data for htmlarea audio popup
		  var audioAgentName= '<xsl:value-of select="$audioAgentName"/>';
		  var audioAgentId= '<xsl:value-of select="$audioAgentId"/>';
		  var audioCourseAssignmentContext= '<xsl:value-of 
		  	select="$audioCourseAssignmentContext"/>';
		  var audioFileExtension= '<xsl:value-of select="$audioFileExtension"/>';
		  var audioFileName= '<xsl:value-of select="$audioFileName"/>';
		  var audioLimit= '<xsl:value-of select="$audioLimit"/>';
		  var audioDir= '<xsl:value-of select="$audioDir"/>';
		  var audioSeconds= '<xsl:value-of select="$audioSeconds"/>';
		  var audioAppName= '<xsl:value-of select="$audioAppName"/>';
		  var audioImageURL= '<xsl:value-of select="$audioImageURL"/>';
</xsl:template>
</xsl:stylesheet>
