<%-- $Id$
include file for displaying matching questions
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

<h:outputText value="#{question.description}" escape="false"/>
<f:verbatim><br /></f:verbatim>
<h:outputText value="#{question.text}"  escape="false"/>
<h:dataTable value="#{question.itemTextArraySorted}" var="itemText">
 <h:column>
   <h:outputText value="#{itemText.sequence}. #{itemText.text}" escape="false" />
   <h:dataTable value="#{itemText.answerArraySorted}" var="answer">
     <h:column>
      <h:graphicImage id="image4" rendered="#{answer.isCorrect}"
        alt="#{msg.correct}" url="/images/delivery/checkmark.gif" >
       </h:graphicImage>
      <h:graphicImage id="image5" rendered="#{!answer.isCorrect}"
        alt="#{msg.not_correct}" url="/images/delivery/spacer.gif" >
       </h:graphicImage>
     </h:column>
     <h:column>
       <h:outputText value="#{answer.text}" escape="false" />
     </h:column>
   </h:dataTable>
 </h:column>
</h:dataTable>

