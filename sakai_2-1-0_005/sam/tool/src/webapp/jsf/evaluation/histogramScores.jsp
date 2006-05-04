<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://www.sakaiproject.org/samigo" prefix="samigo" %>
<!DOCTYPE html
     PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

  <f:view>
   
    <f:loadBundle
     basename="org.sakaiproject.tool.assessment.bundle.EvaluationMessages"
     var="msg"/>
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head><%= request.getAttribute("html.head") %>
      <title><h:outputText
        value="#{msg.title_stat}" /></title>
      </head>
      <body onload="<%= request.getAttribute("html.body.onload") %>">
<!--
$Id$
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
<!-- content... -->
 <div class="portletBody">
<h:form id="histogram">

  <h:inputHidden id="publishedId" value="#{histogramScores.publishedId}" />
  <h:inputHidden id="itemId" value="#{histogramScores.itemId}" />

  <!-- HEADINGS -->
  <%@ include file="/jsf/evaluation/evaluationHeadings.jsp" %>

  <h3>
    <h:outputText value="#{msg.stat_view}"/>
    <h:outputText value=": "/>
    <h:outputText value="#{histogramScores.assessmentName} "/>
  </h3>
  <p class="navModeAction">
    <h:commandLink action="totalScores" immediate="true" rendered="#{histogramScores.hasNav==null || histogramScores.hasNav=='true'}">
    <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.evaluation.TotalScoreListener" />
      <h:outputText value="#{msg.title_total}" />
    </h:commandLink>
    <h:outputText value=" | " rendered="#{histogramScores.hasNav==null || histogramScores.hasNav=='true'}"/>
    <h:commandLink action="questionScores" immediate="true" rendered="#{histogramScores.hasNav==null || histogramScores.hasNav=='true'}">
      <f:actionListener
        type="org.sakaiproject.tool.assessment.ui.listener.evaluation.QuestionScoreListener" />
      <h:outputText value="#{msg.q_view}" />
    </h:commandLink>
    <h:outputText value=" | " rendered="#{histogramScores.hasNav==null || histogramScores.hasNav=='true'}"/>
      <h:outputText value="#{msg.stat_view}" rendered="#{histogramScores.hasNav==null || histogramScores.hasNav=='true'}"/>
  </p>

  <h:messages styleClass="validation" />

<div class="indent2">


  <!-- LAST/ALL SUBMISSIONS; PAGER; ALPHA INDEX  -->
    <h:panelGroup rendered="#{histogramScores.hasNav==null || histogramScores.hasNav=='true'}">
     <h:outputText value="#{msg.view} " />
      <h:outputText value=" : " />

     <h:selectOneMenu value="#{histogramScores.allSubmissions}" id="allSubmissionsL"
        required="true" onchange="document.forms[0].submit();" rendered="#{totalScores.scoringOption eq '2'}">
      <f:selectItem itemValue="3" itemLabel="#{msg.all_sub}" />
      <f:selectItem itemValue="2" itemLabel="#{msg.last_sub}" />
      <f:valueChangeListener
         type="org.sakaiproject.tool.assessment.ui.listener.evaluation.HistogramListener" />
     </h:selectOneMenu>

     <h:selectOneMenu value="#{histogramScores.allSubmissions}" id="allSubmissionsH"
        required="true" onchange="document.forms[0].submit();" rendered="#{totalScores.scoringOption eq '1'}">
      <f:selectItem itemValue="3" itemLabel="#{msg.all_sub}" />
      <f:selectItem itemValue="1" itemLabel="#{msg.highest_sub}" />
      <f:valueChangeListener
         type="org.sakaiproject.tool.assessment.ui.listener.evaluation.HistogramListener" />
     </h:selectOneMenu>
    </h:panelGroup>

    <h:panelGroup rendered="#{histogramScores.randomType =='true'}">
    <h:outputText value="#{msg.no_histogram_for_random}" />
      </h:panelGroup>


    <h:panelGroup rendered="#{histogramScores.randomType =='false'}">
 <f:verbatim><h4></f:verbatim>
  <h:outputText value="#{msg.tot}" />
   <f:verbatim></h4></f:verbatim>


 <h:dataTable value="#{histogramScores.histogramBars}" var="bar" footerClass="alignCenter">
    <h:column>
        <h:outputText value=" #{bar.rangeInfo} #{msg.pts} " />
    </h:column>
     <h:column>
        <h:graphicImage url="/images/reddot.gif" height="12" width="#{bar.columnHeight}"/>
        <h:outputText value=" #{bar.numStudents}" />
</h:column>
       <f:facet name="footer">
          <h:outputText value="#{msg.num_students}" />
      </f:facet>
  </h:dataTable>

    </h:panelGroup>

<h:panelGrid columns="2" columnClasses="alignLeft,aligntRight">

<h:outputText value="#{msg.sub_view}"/>
<h:outputText value="#{histogramScores.numResponses}" />

<h:outputText value="#{msg.tot} Possible" />
<h:outputText value="#{histogramScores.totalPossibleScore}"/>

<h:outputText value="#{msg.mean_eq}" />
<h:outputText value="#{histogramScores.mean}"/>

<h:outputText value="#{msg.median}" />
<h:outputText value="#{histogramScores.median}"/>

<h:outputText value="#{msg.mode}" />
<h:outputText value="#{histogramScores.mode}"/>

<h:outputText value="#{msg.range_eq}" />
<h:outputText value="#{histogramScores.range}"/>

<h:outputText value="#{msg.qtile_1_eq}" />
<h:outputText value="#{histogramScores.q1}"/>

<h:outputText value="#{msg.qtile_3_eq}" />
<h:outputText value="#{histogramScores.q3}"/>

<h:outputText value="#{msg.std_dev}" />
<h:outputText value="#{histogramScores.standDev}"/>
</h:panelGrid>

  <h:dataTable value="#{histogramScores.info}" var="item">

<!-- need to add a randomtype property for histogramQuestionScoreBean (item) and if it's true, hide histogram  -->
<%--
    <h:column rendered="#{histogramScores.randomType =='true'}">
      <h:outputText value="#{msg.no_histogram_for_random}" />
    </h:column>
--%>


    <h:column rendered="#{histogramScores.randomType =='false'}">
      <h:panelGroup>
        <f:verbatim><h4></f:verbatim>
          <h:outputText value="#{item.title}" escape="false" />
        <f:verbatim></h4></f:verbatim>

        <h:outputText value="#{item.questionText}" escape="false" />

        <h:dataTable value="#{item.histogramBars}" var="bar">
          <h:column>
            <h:panelGrid columns="1">
              <h:panelGroup>

<h:graphicImage id="image8" rendered="#{bar.isCorrect}" width="12" height="12"
        alt="#{msg.correct}" url="/images/delivery/checkmark.gif" >
       </h:graphicImage>

<h:graphicImage id="image9" rendered="#{!bar.isCorrect}" width="12" height="12"
        alt="#{msg.not_correct}" url="/images/delivery/spacer.gif" >
       </h:graphicImage>

                <h:graphicImage url="/images/reddot.gif" height="12" width="#{bar.columnHeight}"/>
                <h:outputText value=" #{bar.numStudentsText}" />
              </h:panelGroup>
               <h:panelGroup>
               <h:graphicImage width="12" height="12" url="/images/delivery/spacer.gif" />
              <h:outputText value="#{bar.label}" escape="false" />
</h:panelGroup>
            </h:panelGrid>
          </h:column>
        </h:dataTable>

        <!-- 1-2=mcmc 3=mcsc 4=tf 5=essay 6=file 7=audio 8=FIB 9=matching -->

        <h:panelGrid columns="2" rendered="#{item.questionType == '5' or item.questionType == '6' or item.questionType == '7'}" columnClasses="alignLeft,aligntRight">

          <h:outputLabel for="responses" value="#{msg.responses}" />
          <h:outputText id="responses" value="#{item.numResponses}" />
          <h:outputLabel for="possible" value="#{msg.tot_poss_eq}" />
          <h:outputText id="possible" value="#{item.totalScore}" />
          <h:outputLabel for="mean" value="#{msg.mean_eq}" />
          <h:outputText id="mean" value="#{item.mean}" />
          <h:outputLabel for="median" value="#{msg.median}" />
          <h:outputText id="median" value="#{item.median}" />
          <h:outputLabel for="mode" value="#{msg.mode}" />
          <h:outputText id="mode" value="#{item.mode}" />
        </h:panelGrid>
        <h:panelGrid columns="2" rendered="#{item.questionType == '3'}" columnClasses="alignLeft,aligntRight">
          <h:outputLabel for="responses1" value="#{msg.responses}" />
          <h:outputText id="responses1" value="#{item.numResponses}" />
        </h:panelGrid>
        <h:panelGrid columns="2" rendered="#{item.questionType == '1' or  item.questionType == '2' or  item.questionType == '4' or  item.questionType == '8' or item.questionType == '9'}" columnClasses="alignLeft,aligntRight">
          <h:outputLabel for="responses2" value="#{msg.responses}" />
          <h:outputText id="responses2" value="#{item.numResponses}" />
          <h:outputLabel for="percentCorrect" value="#{msg.percentCorrect}" />
          <h:outputText id="percentCorrect" value="#{item.percentCorrect}" />
        </h:panelGrid>


      </h:panelGroup>
    </h:column>
  </h:dataTable>
</div>

<h:commandButton value="#{msg.return}" action="select" type="submit" rendered="#{histogramScores.hasNav=='false'}"/>

</h:form>
</div>
  <!-- end content -->
      </body>
    </html>
  </f:view>