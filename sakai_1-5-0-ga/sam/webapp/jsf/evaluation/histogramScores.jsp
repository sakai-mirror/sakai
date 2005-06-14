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
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
      <title><h:outputText
        value="#{msg.title_stat}" /></title>
      <samigo:stylesheet path="/css/samigo.css"/>
      <samigo:stylesheet path="/css/sam.css"/>
      </head>
      <body>
<!-- $Id:  -->
<!-- content... -->
<h:form id="histogram">

  <h:inputHidden id="publishedId" value="#{histogramScores.publishedId}" />
  <h:inputHidden id="itemId" value="#{histogramScores.itemId}" />

  <!-- HEADINGS -->
  <p class="navIntraTool">
    <h:commandLink action="author" immediate="true">
      <h:outputText value="#{msg.global_nav_assessmt}" />
       <f:actionListener
         type="org.sakaiproject.tool.assessment.ui.listener.author.AuthorActionListener" />
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
    <h:outputText value="#{msg.stat_view}"/>
    <h:outputText value=": "/>
    <h:outputText value="#{histogramScores.assessmentName} "/>
  </h3>
  <p class="navModeAction">
    <h:commandLink action="totalScores" immediate="true">
    <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.evaluation.TotalScoreListener" />
      <h:outputText value="#{msg.title_total}" />
    </h:commandLink>
    <h:outputText value=" | " />
    <h:commandLink action="questionScores" immediate="true">
      <f:actionListener
        type="org.sakaiproject.tool.assessment.ui.listener.evaluation.QuestionScoreListener" />
      <h:outputText value="#{msg.q_view}" />
    </h:commandLink>
    <h:outputText value=" | " />
      <h:outputText value="#{msg.stat_view}" />
  </p>

  <h:messages layout="table" />

<div class="indent2">

  <!-- LAST/ALL SUBMISSIONS; PAGER; ALPHA INDEX  -->
    <h:panelGroup>
     <h:outputText value="#{msg.view} " />
      <h:outputText value=" : " />
     <h:selectOneMenu value="#{totalScores.allSubmissions}" id="allSubmissions"
        required="true" onchange="document.forms[0].submit();">
      <f:selectItem itemValue="false" itemLabel="#{msg.last_sub}" />
      <f:selectItem itemValue="true" itemLabel="#{msg.all_sub}" />
      <f:valueChangeListener
         type="org.sakaiproject.tool.assessment.ui.listener.evaluation.HistogramListener" />
     </h:selectOneMenu>
    </h:panelGroup>

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


<h:panelGrid columns="2" columnClasses="alignRight,alignLeft">

<h:outputText value="#{msg.sub_view}"/>
<h:outputText value="#{histogramScores.numResponses}" />

<h:outputText value="#{msg.tot} Possible" />
<h:outputText value="#{histogramScores.totalScore}"/>

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
    <h:column>
      <h:panelGroup>
        <f:verbatim><h4></f:verbatim>
          <h:outputText value="#{item.title}" escape="false" /> 
        <f:verbatim></h4></f:verbatim>

        <h:outputText value="#{item.questionText}" escape="false" />

        <h:dataTable value="#{item.histogramBars}" var="bar">
          <h:column>
            <h:panelGrid columns="1">
              <h:panelGroup>
                <h:graphicImage url="/images/reddot.gif" height="12" width="#{bar.columnHeight}"/>
                <h:outputText value=" #{bar.numStudentsText}" />
              </h:panelGroup>
              <h:outputText value="#{bar.label}" escape="false" />
            </h:panelGrid>
          </h:column>
        </h:dataTable>

        <!-- 1-2=mcmc 3=mcsc 4=tf 5=essay 6=file 7=audio 8=FIB 9=matching --> 

        <h:panelGrid columns="2" rendered="#{item.questionType == '5' or item.questionType == '7' or item.questionType == '6' or item.questionType == '8' }" columnClasses="alignRight,aligntLeft">
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
      </h:panelGroup>
    </h:column>
  </h:dataTable>
</h:form>
</div>
  <!-- end content -->
      </body>
    </html>
  </f:view>
