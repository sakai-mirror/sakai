<%-- $Id: displayFillInTheBlank.jsp,v 1.6 2005/02/01 18:42:42 rgollub.stanford.edu Exp $
include file for displaying fill in the blank questions
--%>
<h:outputText value="#{question.description}" escape="false"/>
<f:verbatim><br /></f:verbatim>
<h:outputText value="#{question.text}"  escape="false"/>
<h:dataTable value="#{question.itemTextArray}" var="itemText">
 <h:column>
 <h:dataTable value="#{itemText.answerArraySorted}" var="answer">
  <h:column>
    <h:graphicImage id="image2" rendered="#{answer.isCorrect}"
      alt="#{msg2.correct}" url="/images/delivery/checkmark.gif" >
     </h:graphicImage>
    <h:graphicImage id="image3" rendered="#{!answer.isCorrect}"
      alt="#{msg2.not_correct}" url="/images/delivery/spacer.gif" >
     </h:graphicImage>
  </h:column>
  <h:column>
   <h:outputText value="#{answer.sequence}. #{answer.text}" escape="false" />
  </h:column>
 </h:dataTable>
 </h:column>
</h:dataTable>
