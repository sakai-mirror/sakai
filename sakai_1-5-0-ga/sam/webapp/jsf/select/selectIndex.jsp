<!-- $Id: selectIndex.jsp,v 1.41 2005/02/15 02:15:56 daisyf.stanford.edu Exp $ -->
<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://www.sakaiproject.org/samigo" prefix="samigo" %>
  <f:view>
  <f:loadBundle
     basename="org.sakaiproject.tool.assessment.bundle.SelectIndexMessages"
     var="msg"/>
   <f:loadBundle
     basename="org.sakaiproject.tool.assessment.bundle.GeneralMessages"
     var="genMsg"/>
    <f:verbatim><!DOCTYPE html
     PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
    </f:verbatim>
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
      <title><h:outputText value="#{msg.page_title}" /></title>
      <samigo:stylesheet path="/css/samigo.css"/>
      <samigo:stylesheet path="/css/sam.css"/>
      </head>
      <body>
  <!-- content... -->
  <h:form id="selectIndexForm">
  <h3>
    <h:outputText value="#{msg.page_heading}"/>
  </h3>
  <!-- SELECT -->
  <h4 class="tier1"><h:outputText value="#{msg.take_assessment}" /></h4>
  <p class="tier2">
    <h:outputText value="#{msg.take_assessment_notes}" />
  </p>
<%-- pager controls NOT required by mockups, not implemented
  <span class="rightNav">
    <samigo:pagerButtons  formId="editTotalResults" dataTableId="myData"
      prevText="Previous" nextText="Next"
      firstItem="#{select.takePager.firstItem}"
      lastItem="#{select.takePager.lastItem}"
      totalItems="#{select.takePager.totalItems}"
      numItems="#{select.takePager.numItems}" />
  </span>
  <br />
  <br />
  <br />
--%>
  <br />
<%-- TODO: add ascending/descending images with action of same sort as the column--%>
<%--
sorting actions for table:

* Sort by: Title
* Sort by: Date Due
--%>
  <!-- SELECT TABLE -->
  <div class="tier4">
  <h:dataTable id="selectTable" value="#{select.takeableAssessments}"
    var="takeable" styleClass="listHier"> 
    <h:column>  
      <f:facet name="header">
       <h:panelGroup>
        <h:commandLink id="taketitle">
          <f:param name="takeableSortType" value="title" />
          <f:param name="takeAscending" value="true" />
          <f:actionListener
             type="org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener" />
          <h:outputText  value="#{msg.title}" styleClass="currentSort" rendered="#{select.takeableSortOrder=='title'}" />
          <h:outputText  value="#{msg.title}"  rendered="#{select.takeableSortOrder!='title'}" />
        </h:commandLink>  
          <h:commandLink rendered="#{select.takeableSortOrder=='title' && select.takeableAscending }">
           <f:param name="takeAscending" value="false" />
           <f:actionListener
             type="org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener" />
             <h:graphicImage alt="#{msg.asc}" rendered="#{select.takeableAscending}" url="/images/sortascending.gif"/>
          </h:commandLink>
          <h:commandLink rendered="#{select.takeableSortOrder=='title'&& !select.takeableAscending }">
           <f:param name="takeAscending" value="true" />
           <f:actionListener
             type="org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener" />
           <h:graphicImage alt="#{msg.desc}" rendered="#{!select.takeableAscending}" url="/images/sortdescending.gif"/>
          </h:commandLink>
       </h:panelGroup>    
      </f:facet>
      <h:commandLink action="beginAssessment" >
        <f:param name="publishedId" value="#{takeable.assessmentId}" />
        <f:actionListener
           type="org.sakaiproject.tool.assessment.ui.listener.delivery.BeginDeliveryActionListener" />
        <h:outputText value="#{takeable.assessmentTitle}"/>
      </h:commandLink>
    </h:column>
    <h:column>
      <f:facet name="header">
       <h:panelGroup>
        <h:commandLink id="takedue">
          <f:param name="takeableSortType" value="due" />
          <f:param name="takeAscending" value="true" />
          <f:actionListener
             type="org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener" />
          <h:outputText value="#{msg.date_due}" styleClass="currentSort" rendered="#{select.takeableSortOrder=='due'}" />
          <h:outputText value="#{msg.date_due}" rendered="#{select.takeableSortOrder!='due'}" />
        </h:commandLink>
        <h:commandLink rendered="#{select.takeableSortOrder=='due' && select.takeableAscending }">
          <f:param name="takeAscending" value="false" />
          <f:actionListener
             type="org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener" />
          <h:graphicImage alt="#{msg.asc}" rendered="#{select.takeableAscending}" url="/images/sortascending.gif"/>
        </h:commandLink>
        <h:commandLink rendered="#{select.takeableSortOrder=='due'&& !select.takeableAscending }">
           <f:param name="takeAscending" value="true" />
           <f:actionListener
             type="org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener" />
          <h:graphicImage alt="#{msg.desc}" rendered="#{!select.takeableAscending}" url="/images/sortdescending.gif"/>
        </h:commandLink>
       </h:panelGroup> 
      </f:facet>
      <h:outputText value="n/a" rendered="#{takeable.dueDate == null}" />
      <h:outputText value="#{takeable.dueDate}" style="color: red;" rendered="#{takeable.pastDue}">
        <f:convertDateTime pattern="#{genMsg.output_date_no_sec}" />
      </h:outputText>
      <h:outputText value="#{takeable.dueDate}" rendered="#{!takeable.pastDue}">
        <f:convertDateTime pattern="#{genMsg.output_date_no_sec}" />
      </h:outputText>
    </h:column>
  </h:dataTable>
  </div>
  <!-- SUBMITTED ASSESMENTS -->
  <h4 class="tier1"><h:outputText value="#{msg.submitted_assessments}" /></h4>
  <p class="tier2">
    <h:outputText value="#{msg.review_assessment_notes}" />
  </p>

<%-- pager controls NOT required by mockups, not implemented
  <span class="rightNav">
    <samigo:pagerButtons  formId="editTotalResults" dataTableId="myData"
      prevText="Previous" nextText="Next"
      firstItem="#{select.reviewPager.firstItem}"
      lastItem="#{select.reviewPager.lastItem}"
      totalItems="#{select.reviewPager.totalItems}"
      numItems="#{select.reviewPager.numItems}" />
  </span>
  <br />
  <br />
  <br />
--%>
  <br />
  <!-- REVIEW TABLE -->
<%--
sorting actions for table:

* Sort by: Grade
* Sort by: Raw Score
* Sort by: Statistics
* Sort by: Submitted
* Sort by: Time
* Sort by: Title
--%>
  <div class="tier2">
  <h:dataTable id="reviewTable" value="#{select.reviewableAssessments}"
       var="reviewable" styleClass="listHier"  width="100%">

<%-- TITLE --%>
    <h:column>
      <f:facet name="header">
       <h:panelGroup>
        <h:commandLink id="reviewtitle">
          <f:param name="reviewableSortType" value="title" />
          <f:param name="reviewableAscending" value="true" />
          <f:actionListener
             type="org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener" />
          <h:outputText value="#{msg.title}" styleClass="currentSort" rendered="#{select.reviewableSortOrder=='title'}" />
          <h:outputText value="#{msg.title}"  rendered="#{select.reviewableSortOrder!='title'}" />
        </h:commandLink>  
        <h:commandLink rendered="#{select.reviewableSortOrder=='title' && select.reviewableAscending } ">
          <f:param name="reviewableAscending" value="false" />
           <f:actionListener
             type="org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener" />
          <h:graphicImage alt="#{msg.asc}" rendered="#{select.reviewableSortOrder=='title' && select.reviewableAscending}"
            url="/images/sortascending.gif"/>
        </h:commandLink> 
       <h:commandLink rendered="#{select.reviewableSortOrder=='title' && !select.reviewableAscending } ">
          <f:param name="reviewableAscending" value="true" />
           <f:actionListener
             type="org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener" />
          <h:graphicImage alt="#{msg.desc}" rendered="#{select.reviewableSortOrder=='title' && !select.reviewableAscending}"
            url="/images/sortdescending.gif"/>
          </h:commandLink>
      </h:panelGroup>
      </f:facet>
      <h:commandLink action="takeAssessment">
        <f:param name="publishedId" value="#{reviewable.assessmentId}" />
        <f:param name="review" value="true" />
        <f:actionListener
           type="org.sakaiproject.tool.assessment.ui.listener.delivery.DeliveryActionListener" />
        <h:outputText value="#{reviewable.assessmentTitle}"/>
      </h:commandLink>

<%-- OK, Marc wants links for all submitted assessments regardless if feedback is available, see SAM-229
     So, command these out for 1.5
      <h:commandLink action="takeAssessment"
        rendered="#{reviewable.feedback eq 'true'}" >
        <f:param name="publishedId" value="#{reviewable.assessmentId}" />
        <f:param name="review" value="true" />
        <f:actionListener
           type="org.sakaiproject.tool.assessment.ui.listener.delivery.DeliveryActionListener" />
        <h:outputText value="#{reviewable.assessmentTitle}"/>
      </h:commandLink>
        <h:outputText value="#{reviewable.assessmentTitle}"
          rendered="#{reviewable.feedback eq 'false'}" />
--%>
    </h:column>

<%-- FEEDBACK DATE --%>
    <h:column>
      <f:facet name="header">
       <h:panelGroup>
        <h:commandLink id="feedbackDate">
          <f:param name="reviewableSortType" value="feedbackDate" />
          <f:param name="reviewableAscending" value="true" />
          <f:actionListener
             type="org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener" />
          <h:outputText value="#{msg.feedback_date}" styleClass="currentSort" rendered="#{select.reviewableSortOrder=='feedbackDate'}" />
          <h:outputText value="#{msg.feedback_date}" rendered="#{select.reviewableSortOrder!='feedbackDate'}" />
        </h:commandLink> 
        <h:commandLink rendered="#{select.reviewableSortOrder=='feedbackDate' && select.reviewableAscending}">
           <f:param name="reviewableAscending" value="false" />
           <f:actionListener
             type="org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener" />
          <h:graphicImage alt="#{msg.asc}"
            rendered="#{select.reviewableSortOrder=='feedbackDate' && select.reviewableAscending}"
            url="/images/sortascending.gif"/>
        </h:commandLink> 
        <h:commandLink rendered="#{select.reviewableSortOrder=='feedbackDate' && !select.reviewableAscending } ">
          <f:param name="reviewableAscending" value="true" />
           <f:actionListener
             type="org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener" />
          <h:graphicImage alt="#{msg.desc}"
            rendered="#{select.reviewableSortOrder=='feedbackDate' && !select.reviewableAscending}"
            url="/images/sortdescending.gif"/>
          </h:commandLink>
         </h:panelGroup>
      </f:facet>
      <h:outputText value="#{reviewable.feedbackDate}" rendered="#{reviewable.feedback eq 'true'}" >
        <f:convertDateTime pattern="#{genMsg.output_date_no_sec}"  />
      </h:outputText>
      <h:outputText value="Immediate" rendered="#{reviewable.feedback eq 'true' 
        && reviewable.feedbackDate==null}" />
       <h:outputText value="#{msg.not_applicable}" rendered="#{reviewable.feedback eq 'false'}" />
    </h:column>

<%-- STATISTICS --%>
<%-- no statistic for 1.5 release
    <h:column>
      <f:facet name="header">
        <h:commandLink id="reviewstats">
          <f:param name="reviewableSortType" value="stats" />
          <f:actionListener
             type="org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener" />
          <h:outputText value="#{msg.statistics}" styleClass="currentSort" rendered="#{select.reviewableSortOrder=='stats'}"/>
          <h:outputText value="#{msg.statistics}" rendered="#{select.reviewableSortOrder!='stats'}"/>
           <h:commandLink rendered="#{select.reviewableSortOrder=='stats'}">
          <h:graphicImage alt="#{msg.asc}"
            rendered="#{!select.reviewableAscending}"
            url="/images/sortascending.gif"/>
          <h:graphicImage alt="#{msg.desc}"
            rendered="#{select.reviewableAscending}"
            url="/images/sortdescending.gif"/>
          </h:commandLink>
        </h:commandLink>
      </f:facet>
      <h:panelGroup>
        <h:commandLink action="personalStats" id="personalStats"
          rendered="#{reviewable.statsAvailable}">
          <h:inputHidden id="statsAssessmentId" value="#{reviewable.assessmentId}"/>
          <h:outputText value="#{msg.view}"/>
        </h:commandLink>
        <h:outputText value="#{msg.not_applicable}"
          rendered="#{!reviewable.statsAvailable}"/>
      </h:panelGroup>
    </h:column>
--%>


<%-- GRADE --%>
    <h:column>
      <f:facet name="header">
        <h:panelGroup>
        <h:commandLink id="reviewgrade">
          <f:param name="reviewableSortType" value="grade" />
          <f:param name="reviewableAscending" value="true" />
          <f:actionListener
             type="org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener" />
          <h:outputText value="#{msg.grade}" styleClass="currentSort" rendered="#{select.reviewableSortOrder=='grade'}" />
          <h:outputText value="#{msg.grade}"  rendered="#{select.reviewableSortOrder!='grade'}" />
        </h:commandLink> 
        <h:commandLink rendered="#{select.reviewableSortOrder=='grade' && select.reviewableAscending  }">
           <f:param name="reviewableAscending" value="false" />
           <f:actionListener
             type="org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener" />
          <h:graphicImage alt="#{msg.asc}"
            rendered="#{select.reviewableSortOrder=='grade' && select.reviewableAscending}"
            url="/images/sortascending.gif"/>
        </h:commandLink>
        <h:commandLink rendered="#{select.reviewableSortOrder=='grade' && !select.reviewableAscending  }">
           <f:param name="reviewableAscending" value="true" />
           <f:actionListener
             type="org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener" />
          <h:graphicImage alt="#{msg.desc}"
            rendered="#{select.reviewableSortOrder=='grade' && !select.reviewableAscending}"
            url="/images/sortdescending.gif"/>
        </h:commandLink>
        </h:panelGroup>
      </f:facet>
      <h:panelGroup>
        <h:outputText value="#{reviewable.grade}" rendered="#{reviewable.graded}"/>
        <h:outputText value="#{msg.not_applicable}" rendered="#{!reviewable.graded}"/>
      </h:panelGroup>
    </h:column>

<%-- SCORE --%>
    <h:column>
      <f:facet name="header">
       <h:panelGroup>
        <h:commandLink id="reviewraw">
          <f:param name="reviewableSortType" value="raw" />
          <f:param name="reviewableAscending" value="true" />
          <f:actionListener
             type="org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener" />
          <h:outputText value="#{msg.score}" styleClass="currentSort" rendered="#{select.reviewableSortOrder=='raw'}"/>
          <h:outputText value="#{msg.score}" rendered="#{select.reviewableSortOrder!='raw'}"/>
        </h:commandLink> 
        <h:commandLink rendered="#{select.reviewableSortOrder=='raw' && select.reviewableAscending}">
           <f:param name="reviewableAscending" value="false" />
           <f:actionListener
             type="org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener" />
          <h:graphicImage alt="#{msg.asc}"
            rendered="#{select.reviewableSortOrder=='raw' && select.reviewableAscending}"
            url="/images/sortascending.gif"/>
        </h:commandLink> 
        <h:commandLink rendered="#{select.reviewableSortOrder=='raw' && !select.reviewableAscending}">
           <f:param name="reviewableAscending" value="true" />
           <f:actionListener
             type="org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener" />
          <h:graphicImage alt="#{msg.desc}"
            rendered="#{select.reviewableSortOrder=='raw' && !select.reviewableAscending}"
            url="/images/sortdescending.gif"/>
          </h:commandLink>
        </h:panelGroup>
      </f:facet>
      <h:outputText value="#{reviewable.rawScore}" rendered="#{reviewable.showScore eq 'true'}" />
      <h:outputText value="#{msg.not_applicable}" rendered="#{reviewable.showScore eq 'false'}" />
    </h:column>

<%-- TIME --%>
    <h:column>
      <f:facet name="header">
       <h:panelGroup>
        <h:commandLink id="reviewtime">
          <f:param name="reviewableSortType" value="time" />
          <f:param name="reviewableAscending" value="true" />
          <f:actionListener
             type="org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener" />
          <h:outputText value="#{msg.time}"  styleClass="currentSort" rendered="#{select.reviewableSortOrder=='time'}"/>
          <h:outputText value="#{msg.time}"  rendered="#{select.reviewableSortOrder!='time'}"/>
        </h:commandLink>  
        <h:commandLink rendered="#{select.reviewableSortOrder=='time'&& select.reviewableAscending }">
           <f:param name="reviewableAscending" value="false" />
           <f:actionListener
             type="org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener" />
          <h:graphicImage alt="#{msg.asc}" rendered="#{select.reviewableSortOrder=='time' && select.reviewableAscending}"
           url="/images/sortascending.gif"/>
        </h:commandLink>  
        <h:commandLink rendered="#{select.reviewableSortOrder=='time'&& !select.reviewableAscending }">
           <f:param name="reviewableAscending" value="true" />
           <f:actionListener
             type="org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener" />
          <h:graphicImage alt="#{msg.desc}"
            rendered="#{select.reviewableSortOrder=='time'&& !select.reviewableAscending}"
            url="/images/sortdescending.gif"/>
          </h:commandLink>
        </h:panelGroup>
      </f:facet>
      <h:panelGroup>
        <h:outputText value="#{reviewable.submissionHours} #{msg.hours}, #{reviewable.submissionMinutes} #{msg.minutes}"
          rendered="#{reviewable.submitted}" />
        <h:outputText value="#{msg.not_applicable}"
          rendered="#{!reviewable.submitted}"/>
      </h:panelGroup>
    </h:column>

<%-- SUBMITTED --%>
    <h:column>
      <f:facet name="header">
       <h:panelGroup>
        <h:commandLink id="reviewsubmitted">
          <f:param name="reviewableSortType" value="submitted" />
          <f:param name="reviewableAscending" value="true" />
          <f:actionListener
             type="org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener" />
          <h:outputText value="#{msg.submitted}" styleClass="currentSort" rendered="#{select.reviewableSortOrder=='submitted'}"/>
          <h:outputText value="#{msg.submitted}" rendered="#{select.reviewableSortOrder!='submitted'}"/>
        </h:commandLink>
        <h:commandLink rendered="#{select.reviewableSortOrder=='submitted' && select.reviewableAscending }">
          <f:param name="reviewableAscending" value="false" />
          <f:actionListener
             type="org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener" />
          <h:graphicImage alt="#{msg.asc}"
            rendered="#{select.reviewableSortOrder=='submitted' && select.reviewableAscending}"
            url="/images/sortascending.gif"/>
        </h:commandLink>
        <h:commandLink rendered="#{select.reviewableSortOrder=='submitted' && !select.reviewableAscending }">
          <f:param name="reviewableAscending" value="true" />
          <f:actionListener
             type="org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener" />
          <h:graphicImage alt="#{msg.desc}"
            rendered="#{select.reviewableSortOrder=='submitted' && !select.reviewableAscending}"
            url="/images/sortdescending.gif"/>
          </h:commandLink>
        </h:panelGroup>
      </f:facet>
      <h:outputText value="#{reviewable.submissionDate}" rendered="#{reviewable.feedback eq 'true'}" >
        <f:convertDateTime pattern="#{genMsg.output_date_no_sec}"  />
      </h:outputText>
      <h:outputText value="#{msg.not_applicable}" rendered="#{reviewable.feedback eq 'false'}" />
    </h:column>
  </h:dataTable>
  </div>
  </h:form>
  <!-- end content -->
      </body>
    </html>
  </f:view>
