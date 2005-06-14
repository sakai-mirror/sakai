<%-- $Id: displayShortAnswer.jsp,v 1.3 2004/12/04 08:31:46 rgollub.stanford.edu Exp $
include file for displaying short answer essay questions
--%>
<h:outputText value="#{question.description}" escape="false"/>
<f:verbatim><br /></f:verbatim>
<h:outputText value="#{question.text}"  escape="false"/>

