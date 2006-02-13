<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0" 
  xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fn="http://java.sun.com/jsp/jstl/functions"
  ><jsp:directive.page language="java"
		contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
		errorPage="/WEB-INF/command-pages/errorpage.jsp" 
	/><jsp:text
	><![CDATA[<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> ]]>
  </jsp:text>
      <c:set var="searchBean" value="${requestScope.rsacMap.searchBean}"/>
      <c:set var="currentLocalSpace" value="${requestScope.rsacMap.currentLocalSpace}"/>
      <c:set var="rightRenderBean" value="${requestScope.rsacMap.searchRightRenderBean}"/>
        <c:set var="recentlyVisitedBean" value="${requestScope.rsacMap.recentlyVisitedBean}"/>
  		<c:set target="${recentlyVisitedBean}" property="searchPage" value="${searchBean}"/>
  <c:set var="homeBean" value="${requestScope.rsacMap.homeBean}"/>
  <html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
    <head>
      <title>Search: <c:out value="${searchBean.search}"/></title>
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
	  </span>
	  <span class="rwiki_searchBox">
	    Search: 
	    <input type="hidden" name="action" value="search" />
	    <input type="hidden" name="panel" value="Main" />
	    <input type="text" name="search" />
	  </span>
	</form>
      </div>

      	<c:set var="rwikiContentStyle"  value="rwiki_content" />
      	
	<jsp:directive.include file="breadcrumb.jsp"/>
	<!-- Creates the right hand sidebar -->
	<jsp:directive.include file="sidebar.jsp"/>

	<h3>Search: <c:out value="${searchBean.search}"/></h3>

	<c:set var="searchResults" value="${searchBean.searchResults}"/>
	<jsp:useBean id="searchViewBean" class="uk.ac.cam.caret.sakai.rwiki.tool.bean.ViewBean"/>
	<jsp:setProperty name="searchViewBean" value="${currentLocalSpace}" property="localSpace"/>
      	<div id="${rwikiContentStyle}" >
      		<p>
	  	<c:choose>
		  	<c:when test="${fn:length(searchResults) gt 0 }">
		  		<c:if test="${fn:length(searchResults) gt 1}">		  	
			  		<c:forEach var="foundItem" items="${searchResults}" end="${fn:length(searchResults) -2}">
				      <jsp:setProperty name="searchViewBean" value="${foundItem.name}" property="pageName"/>
			   	      <jsp:element name="a">
						<jsp:attribute name="href"><c:out value="${searchViewBean.viewUrl}"/></jsp:attribute><c:out value="${searchViewBean.localName}"/>
	    			  </jsp:element> :: 
		    		</c:forEach>
	    		</c:if>
	    		<c:forEach var="foundItem" items="${searchResults}" begin="${fn:length(searchResults) - 1}">
			      <jsp:setProperty name="searchViewBean" value="${foundItem.name}" property="pageName"/>
			      <jsp:element name="a">
					<jsp:attribute name="href"><c:out value="${searchViewBean.viewUrl}"/></jsp:attribute><c:out value="${searchViewBean.localName}"/>
			      </jsp:element>
	    		</c:forEach>
		  	</c:when>
	  		<c:otherwise>
	  		   <b>No results found.</b>
	  		</c:otherwise>
	  	</c:choose>
	    
	  </p>
	</div>

      </div>
      </div>
      <jsp:directive.include file="footer.jsp"/>
    </jsp:element>
  </html>
</jsp:root>
