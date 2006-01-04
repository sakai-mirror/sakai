<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>

<f:loadBundle basename="org.sakaiproject.tool.presentation.bundle.Messages" var="msgs"/>

<f:view>
<sakai:view_container title="#{msgs.pt_title_help}">
<sakai:view_content>
	<h:form>

		<sakai:tool_bar>
			<sakai:tool_bar_item
				action="#{PresentationTool.processActionExit}"
				value="#{msgs.pt_done_button}" />
		</sakai:tool_bar>

			<h:messages showSummary="true" showDetail="true" />
			<sakai:instruction_message value="
			This is the Sakai Presentation tool.  It allows one or more presenters to 
			present a set of slides to many viewers.  The presenters can move forward
			and backwards in the slides and the viewers follow each move within a few
			seconds.  The Viewers can watch the presentation in <b>Join</b> mode, 
			or swith to <b>View</b> mode and navigate
			through the slides independent of the presenters.  Viewers can rejoin the 
			show at any time.  While watching a presentation, it may take up to 10 seconds
			to see the new slide after the presenter advances the slide.  
			The viewer can refresh manually to see the latest slide if the automatic
			advance seems to be taking too long.
			" />
			<sakai:instruction_message value="
			To create a slide presentation, upload a series of files into the Resource
			tool under the folder <b>Presentations</b> (case does matter).  Within the
			Presentations folder, each sub-folder will contain a separate presentation.
			So an example pesentation might be a series of JPEG images in the folder
			<b>Presentations/week-01</b>.  Slide order is determined by a sort of the 
			file names or file titles (if provided).
			" />
			<sakai:instruction_message value="
			This tool can handle any web displayable image file including: GIF, PNG, 
			and JPG.
			In terms of security, if a user has write access to the resource folder
			which contains the presentation, 
			they have the right to show the presentation.
			" />		
			<sakai:instruction_message value="
			Note: This is a relatively new tool so you may experience problems.
			This tool may experience performance problems if you have a 
			large number of presentations
			or a large number of slides in the Presentations folder at the same time.
			" />		

	</h:form>

</sakai:view_content>
</sakai:view_container>
</f:view>
