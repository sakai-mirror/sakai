<%-- $Id$
include file for delivering multiple choice single correct survey questions
should be included in file importing DeliveryMessages
--%>
<!--
<%--
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
*
* Licensed under the Educational Community License Version 1.0 (the "License");
* By obtaining, using and/or copying this Original Work, you agree that you have read,
* understand, and will comply with the terms and conditions of the Educational Community License.
* You may obtain a copy of the License at:
*
*      http://cvs.sakaiproject.org/licenses/license_1_0.html
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
* AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*
**********************************************************************************/
--%>
-->

  <h:outputText escape="false" value="#{question.itemData.text}" />
  <h:dataTable value="#{question.itemData.itemTextArraySorted}" var="itemText">
    <h:column>
      <h:dataTable value="#{itemText.answerArraySorted}" var="answer">
        <h:column>
         <h:panelGroup rendered="#{answer.text !=null && answer.text!=''}">
          <h:graphicImage id="image1" rendered="#{answer.isCorrect}" alt="#{msg.correct}" url="/images/radiochecked.gif"/>
         
          <h:graphicImage id="image2" rendered="#{!answer.isCorrect}" alt="#{msg.not_correct}" url="/images/radiounchecked.gif"/>
        
          <h:outputText escape="false" value="#{answer.label}. #{answer.text}" />
</h:panelGroup>
</h:column><h:column>
 
          <h:panelGroup rendered="#{answer.text ne null && answer.text ne '' && assessmentSettings.feedbackAuthoring ne '1' && answer.generalAnswerFbIsNotEmpty}">
          <h:outputLabel value="          #{msg.feedback}:  "/>
        
          <h:outputText escape="false" value="#{answer.generalAnswerFeedback}" />
    </h:panelGroup>
        </h:column>
      </h:dataTable>
  </h:column>
  </h:dataTable>


<h:panelGroup>
  <h:outputLabel value="#{msg.answerKey}: "/>
  <h:outputText escape="false" value="#{question.itemData.answerKey}" />
  <f:verbatim><br/></f:verbatim>
</h:panelGroup>
<h:panelGroup rendered="#{question.itemData.correctItemFbIsNotEmpty && assessmentSettings.feedbackAuthoring ne '2'}">
  <h:outputLabel value="#{msg.correctItemFeedback}: "/>
  <h:outputText  value="#{question.itemData.correctItemFeedback}" escape="false" />
 <f:verbatim><br/></f:verbatim>
</h:panelGroup>
<h:panelGroup rendered="#{question.itemData.incorrectItemFbIsNotEmpty && assessmentSettings.feedbackAuthoring ne '2'}">
  <h:outputLabel value="#{msg.incorrectItemFeedback}: "/>
  <h:outputText value="#{question.itemData.inCorrectItemFeedback}" escape="false" />
</h:panelGroup>




