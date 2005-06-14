<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://www.sakaiproject.org/samigo" prefix="samigo" %>
  <f:view>
    <f:verbatim><!DOCTYPE html
     PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
    </f:verbatim>
    <f:loadBundle
     basename="org.sakaiproject.tool.assessment.bundle.EvaluationMessages"
     var="msg"/>
    <f:loadBundle
     basename="org.sakaiproject.tool.assessment.bundle.DeliveryMessages"
     var="dmsg"/>
   <f:loadBundle
     basename="org.sakaiproject.tool.assessment.bundle.GeneralMessages"
     var="genMsg"/>
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
      <title><h:outputText
        value="#{msg.title_total}" /></title>
      <samigo:stylesheet path="/css/samigo.css"/>
      <samigo:stylesheet path="/css/sam.css"/>
    <samigo:script path="/jsf/widget/hideDivision/hideDivision.js" />
      </head>
    <body onload="hideUnhideAllDivs('none');">
<!-- $Id:  -->
<!-- content... -->
<h:form id="editStudentResults">
  <h:inputHidden id="publishedIdd" value="#{studentScores.publishedId}" />
  <h:inputHidden id="publishedId" value="#{studentScores.publishedId}" />
  <h:inputHidden id="studentid" value="#{studentScores.studentId}" />
  <h:inputHidden id="studentName" value="#{studentScores.studentName}" />
  <h:inputHidden id="gradingData" value="#{studentScores.assessmentGradingId}" />
  <h:inputHidden id="itemId" value="#{studentScores.itemId}" />

  <!-- HEADINGS -->
  <p class="navIntraTool">
    <h:commandLink action="author" immediate="true">
       <f:actionListener
         type="org.sakaiproject.tool.assessment.ui.listener.author.AuthorActionListener" />
      <h:outputText value="#{msg.global_nav_assessmt}" />
    </h:commandLink>
    <h:outputText value=" | " />
    <h:commandLink action="template" immediate="true">
      <h:outputText value="#{msg.global_nav_template}" />
    </h:commandLink>
    <h:outputText value=" | " />
    <h:commandLink action="poolList" immediate="true">
      <h:outputText value="#{msg.global_nav_pools}" />
    </h:commandLink>
  </p>
  <h3>
    <h:outputText value="#{studentScores.studentName} "/>
  </h3>
  <p class="navModeAction">
    <h:commandLink action="totalScores" immediate="true">
    <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.evaluation.TotalScoreListener" />
      <h:outputText value="#{msg.title_total}" />
    </h:commandLink>
    <h:outputText value=" | " rendered="#{totalScores.firstItem ne ''}"  />
    <h:commandLink action="questionScores" immediate="true"
      rendered="#{totalScores.firstItem ne ''}" >
      <h:outputText value="#{msg.q_view}" />
      <f:actionListener
        type="org.sakaiproject.tool.assessment.ui.listener.evaluation.QuestionScoreListener" />
    </h:commandLink>
    <h:outputText value=" | " rendered="#{totalScores.firstItem ne ''}"  />
    <h:commandLink action="histogramScores" immediate="true"
      rendered="#{totalScores.firstItem ne ''}" >
      <h:outputText value="#{msg.stat_view}" />
      <f:actionListener
        type="org.sakaiproject.tool.assessment.ui.listener.evaluation.HistogramListener" />
    </h:commandLink>
  </p>

  <h:messages layout="table" style="color:red"/>

<f:verbatim><h4></f:verbatim>
<h:outputText value="#{delivery.assessmentTitle}" />
<f:verbatim></h4></f:verbatim>
<div class="tier3">
<h:panelGrid columns="2">
   <h:outputText value="Comments:"/>
     <samigo:wysiwyg rows="140" value="#{studentScores.comments}" >
       <f:validateLength maximum="4000"/>
     </samigo:wysiwyg>
   </h:panelGrid>
</div>
<f:verbatim><h4></f:verbatim>
<h:outputText value="#{dmsg.table_of_contents}" />
<f:verbatim></h4></f:verbatim>
<div class="tier2">
  <h:dataTable value="#{delivery.tableOfContents.partsContents}" var="part">
  <h:column>
    <samigo:hideDivision id="hidePartDiv" title = "#{dmsg.p} #{part.number} - #{part.text}  -
       #{part.questions-part.unansweredQuestions}/#{part.questions} #{dmsg.ans_q}, #{part.maxPoints} #{dmsg.pt}" >

      <h:dataTable value="#{part.itemContents}" var="question">
      <h:column>
      <f:verbatim><h4 class="tier2"></f:verbatim>
        <h:panelGroup>
          <h:outputText value="<a href=\"#" escape="false" />
          <h:outputText value="#{part.number}_#{question.number}\">" 
            escape="false" />
          <h:outputText value="#{question.number}. #{question.strippedText} #{question.maxPoints} #{dmsg.pt} " escape="false" />
          <h:outputText value="</a>" escape="false" />
        </h:panelGroup>
        <f:verbatim></h4></f:verbatim>
       </h:column>
      </h:dataTable>
    </samigo:hideDivision>
   </h:column>
  </h:dataTable>
</div>

<div class="tier2">
  <h:dataTable value="#{delivery.pageContents.partsContents}" var="part">
    <h:column>
     <!-- f:subview id="parts" -->
      <f:verbatim><h4 class="tier1"></f:verbatim>
      <h:outputText value="#{dmsg.p} #{part.number} #{dmsg.of} #{part.numParts}" />
      <!-- h:outputText value="#{part.unansweredQuestions}/#{part.questions} " / -->
      <!-- h:outputText value="#{dmsg.ans_q}, " / -->
      <!-- h:outputText value="#{part.points}/#{part.maxPoints} #{dmsg.pt}" / -->
      <f:verbatim></h4><div class="indnt1"></f:verbatim>
      <h:outputText value="#{part.text}" escape="false" rendered="#{part.numParts ne '1'}" />
      <f:verbatim></div></f:verbatim>
      <h:dataTable value="#{part.itemContents}" columnClasses="tier2"
          var="question">
        <h:column>
          <h:outputText value="<a name=\"" escape="false" />
          <h:outputText value="#{part.number}_#{question.number}\"></a>"
            escape="false" />
          <f:verbatim><h4 class="tier2"></f:verbatim>
          <h:outputText value="#{dmsg.q} #{question.number} #{dmsg.of} " />
          <h:outputText value="#{part.questions} :  " />
          <h:inputText value="#{question.points}" />
          <h:outputText value=" / #{question.maxPoints} " />
          <h:outputText value="#{dmsg.pt}"/>
          <f:verbatim></h4><div class="indnt3"></f:verbatim>
          <h:outputText value="#{question.itemData.description}" escape="false"/>
          <h:panelGroup rendered="#{question.itemData.typeId == 7}">
           <f:subview id="deliverAudioRecording">
           <%@ include file="/jsf/delivery/item/deliverAudioRecording.jsp" %>
           </f:subview>
          </h:panelGroup>
          <h:panelGroup rendered="#{question.itemData.typeId == 6}">
           <f:subview id="deliverFileUpload">
           <%@ include file="/jsf/delivery/item/deliverFileUpload.jsp" %>
           </f:subview>
          </h:panelGroup>
          <h:panelGroup rendered="#{question.itemData.typeId == 8}">
           <f:subview id="deliverFillInTheBlank">
           <%@ include file="/jsf/delivery/item/deliverFillInTheBlank.jsp" %>
           </f:subview>
          </h:panelGroup>
          <h:panelGroup rendered="#{question.itemData.typeId == 9}">
           <f:subview id="deliverMatching">
            <%@ include file="/jsf/delivery/item/deliverMatching.jsp" %>
           </f:subview>
          </h:panelGroup>
          <h:panelGroup
            rendered="#{question.itemData.typeId == 1 || question.itemData.typeId == 3}">
           <f:subview id="deliverMultipleChoiceSingleCorrect">
           <%@ include file="/jsf/delivery/item/deliverMultipleChoiceSingleCorrect.jsp" %>
           </f:subview>
          </h:panelGroup>
          <h:panelGroup rendered="#{question.itemData.typeId == 2}">
           <f:subview id="deliverMultipleChoiceMultipleCorrect">
           <%@ include file="/jsf/delivery/item/deliverMultipleChoiceMultipleCorrect.jsp" %>
           </f:subview>
          </h:panelGroup>
          <h:panelGroup rendered="#{question.itemData.typeId == 5}">
           <f:subview id="deliverShortAnswer">
           <%@ include file="/jsf/delivery/item/deliverShortAnswer.jsp" %>
           </f:subview>
          </h:panelGroup>
          <h:panelGroup rendered="#{question.itemData.typeId == 4}">
           <f:subview id="deliverTrueFalse">
           <%@ include file="/jsf/delivery/item/deliverTrueFalse.jsp" %>
           </f:subview>
           <f:verbatim></div></f:verbatim>
          </h:panelGroup>
          <h:panelGrid columns="2">
            <h:outputText value="Comments:"/>
            <samigo:wysiwyg rows="140" value="#{question.gradingComment}">
              <f:validateLength maximum="4000"/>
            </samigo:wysiwyg>
          </h:panelGrid>
        </h:column>
      </h:dataTable>
     <!-- /f:subview -->
    </h:column>
  </h:dataTable>
</div>

<h:panelGrid columns="2" columnClasses="textTable">

   <h:commandButton style="act" value="#{msg.save_cont}" action="studentScores" type="submit" >
      <f:actionListener
         type="org.sakaiproject.tool.assessment.ui.listener.evaluation.StudentScoreUpdateListener" />
      <f:actionListener
         type="org.sakaiproject.tool.assessment.ui.listener.evaluation.StudentScoreListener" />
   </h:commandButton>
   <h:commandButton style="act" value="#{msg.cancel}" action="author" immediate="true">
 <f:actionListener
         type="org.sakaiproject.tool.assessment.ui.listener.author.AuthorActionListener" />
</h:commandButton>

</h:panelGrid>
</h:form>

  <!-- end content -->
      </body>
    </html>
  </f:view>
