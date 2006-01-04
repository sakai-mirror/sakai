<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0" 
  xmlns:c="http://java.sun.com/jsp/jstl/core"
  ><jsp:directive.page language="java"
		contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
		errorPage="/WEB-INF/command-pages/errorpage.jsp" 
	/><jsp:text
	><![CDATA[<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> ]]>
  </jsp:text>
  <jsp:scriptlet>
	  		long start = System.currentTimeMillis();
  </jsp:scriptlet>
  <c:set var="viewBean" value="${requestScope.rsacMap.viewBean}"/>
  <jsp:scriptlet>
	  		long finish = System.currentTimeMillis();
	  	uk.ac.cam.caret.sakai.rwiki.component.util.TimeLogger.printTimer("get ViewBean:",start,finish);
			start = System.currentTimeMillis();
  </jsp:scriptlet>
  
  <c:set var="renderBean" value="${requestScope.rsacMap.renderBean}"/>
  <jsp:scriptlet>
	  		finish = System.currentTimeMillis();
	  	uk.ac.cam.caret.sakai.rwiki.component.util.TimeLogger.printTimer("get RenderBean:",start,finish);
			start = System.currentTimeMillis();
  </jsp:scriptlet>
  <c:set var="rightRenderBean" value="${requestScope.rsacMap.viewRightRenderBean}"/>
    <jsp:scriptlet>
	  		finish = System.currentTimeMillis();
	  	uk.ac.cam.caret.sakai.rwiki.component.util.TimeLogger.printTimer("get RightRenderBean:",start,finish);
			start = System.currentTimeMillis();
  </jsp:scriptlet>
  
  <c:set var="permissionsBean" value="${requestScope.rsacMap.permissionsBean}"/>
      <jsp:scriptlet>
	  		finish = System.currentTimeMillis();
	  	uk.ac.cam.caret.sakai.rwiki.component.util.TimeLogger.printTimer("get permissionsBean:",start,finish);
			start = System.currentTimeMillis();
  </jsp:scriptlet>
  
  <c:set var="homeBean" value="${requestScope.rsacMap.homeBean}"/>
      <jsp:scriptlet>
	  		finish = System.currentTimeMillis();
	  	uk.ac.cam.caret.sakai.rwiki.component.util.TimeLogger.printTimer("get homeBean:",start,finish);
			start = System.currentTimeMillis();
  </jsp:scriptlet>
  <c:set var="recentlyVisitedBean" value="${requestScope.rsacMap.recentlyVisitedBean }"/>
      <jsp:scriptlet>
	  		finish = System.currentTimeMillis();
	  	uk.ac.cam.caret.sakai.rwiki.component.util.TimeLogger.printTimer("get recentlyVisitedBean:",start,finish);
			start = System.currentTimeMillis();
  </jsp:scriptlet>
  
  <c:set target="${recentlyVisitedBean}" property="viewPage" value="${viewBean}"/>
  
  <html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
    <head>
      <title>View: <c:out value="${renderBean.localisedPageName}"/></title>
      <jsp:expression>request.getAttribute("sakai.html.head")</jsp:expression>

    </head>
    <jsp:element name="body">
      <jsp:attribute name="onload"><jsp:expression>request.getAttribute("sakai.html.body.onload")</jsp:expression>parent.updCourier(doubleDeep,ignoreCourier); callAllLoaders();</jsp:attribute>
      <jsp:directive.include file="header.jsp"/>
      <div id="rwiki_container">
	<div class="portletBody">
	<div class="navIntraTool">
	  <form action="?#" method="get" class="rwiki_searchForm">
	    <span class="rwiki_pageLinks">
	      <!-- Home Link -->
	      <jsp:element name="a"><jsp:attribute name="href"><c:out value="${homeBean.homeLinkUrl}"/></jsp:attribute><c:out value="${homeBean.homeLinkValue}"/></jsp:element>
	      <!-- View Link -->
	      <jsp:element name="a"><jsp:attribute name="href"><c:out value="${viewBean.viewUrl}"/></jsp:attribute><jsp:attribute name="class">rwiki_currentPage</jsp:attribute>View</jsp:element>
	      <!-- Edit Link -->
	      <jsp:element name="a"><!--
		--><jsp:attribute name="href"><c:out value="${viewBean.editUrl}"/></jsp:attribute><!--
		--><!--<c:if test="${not(permissionsBean.updateAllowed)}"><jsp:attribute name="class">rwiki_disabled</jsp:attribute></c:if>--><!-- 
		-->Edit<!--
		--></jsp:element>
	      <!-- Info Link -->
	      <jsp:element name="a"><jsp:attribute name="href"><c:out value="${viewBean.infoUrl}"/></jsp:attribute>Info</jsp:element>
	      <!-- History Link -->
	      <jsp:element name="a"><jsp:attribute name="href"><c:out value="${viewBean.historyUrl}"/></jsp:attribute>History</jsp:element>
	    </span>
	    <span class="rwiki_searchBox">
	      Search:	<input type="hidden" name="action" value="search" />
	      <input type="hidden" name="panel" value="Main" />
	      <input type="text" name="search" />
	    </span>
	  </form>
	</div>
      <c:set var="rwikiContentStyle"  value="rwiki_content" />
	  <jsp:directive.include file="breadcrumb.jsp"/>
	  <!-- Creates the right hand sidebar -->
	  <jsp:directive.include file="sidebar.jsp"/>
	  <!-- Main page -->
	  <div id="${rwikiContentStyle}" >
	    <h3><c:out value="${renderBean.localisedPageName}"/></h3>
	    <div class="rwikiRenderBody">
	      <div class="rwikiRenderedContent"> 
		<c:out value="${renderBean.renderedPage}" escapeXml="false"/><br/>	    
	      </div>
	    </div>
	  </div>
	      <jsp:element name="a"><!--
		--><jsp:attribute name="href"><c:out value="${viewBean.publicViewUrl}"/></jsp:attribute><!--
		--><jsp:attribute name="target">publicview</jsp:attribute><!--
		-->public link<!--
		--></jsp:element>
	</div>
      </div>
      <jsp:directive.include file="comments.jsp"/>
      <jsp:directive.include file="footer.jsp"/>
    </jsp:element>
  </html>
</jsp:root>
