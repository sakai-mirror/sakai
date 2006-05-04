<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0"
  xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fn="http://java.sun.com/jsp/jstl/functions"
  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt"
  ><jsp:directive.page language="java"
    contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
    errorPage="/WEB-INF/command-pages/errorpage.jsp"
    /><jsp:text><![CDATA[<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> ]]>
  </jsp:text>
  <c:set var="viewBean" value="${requestScope.rsacMap.viewBean}"/>
  <c:set var="preferencesBean" value="${requestScope.rsacMap.preferencesBean}"/>
  <html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
    <head>
      <title>Info: <c:out value="${realmBean.localName}" /></title>
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
		<jsp:element name="a"><jsp:attribute name="href"><c:out value="${viewBean.viewUrl}"/></jsp:attribute>View</jsp:element>
		<!-- Edit Link -->
		<jsp:element name="a"><jsp:attribute name="href"><c:out value="${viewBean.editUrl}"/></jsp:attribute>Edit</jsp:element>
		<!-- Info Link -->
		<jsp:element name="a"><jsp:attribute name="href"><c:out value="${viewBean.infoUrl}"/></jsp:attribute><jsp:attribute name="class">rwiki_currentPage</jsp:attribute>Info</jsp:element>
		<!-- History Link -->
		<jsp:element name="a"><jsp:attribute name="href"><c:out value="${viewBean.historyUrl}"/></jsp:attribute>History</jsp:element>
	      </span>
	      <span class="rwiki_searchBox">
		Search:	<input type="hidden" name="action" value="${requestScope.rsacMap.searchTarget}" />
		<input type="hidden" name="panel" value="Main" />
		<input type="text" name="search" />
	      </span>
	    </form>
	  </div>
	  <jsp:directive.include file="breadcrumb.jsp"/>
	  <jsp:directive.include file="sidebar.jsp"/>
	  <c:set var="rwikiContentStyle"  value="rwiki_content" />
	  <div id="${rwikiContentStyle}">
	    <!-- CONTENT HERE -->
	    <h3>Notifications for: <c:out value="${viewBean.localSpace}"/></h3>
	    <form action="?#" method="post">
	      <p class="radio">
		<c:choose>
		  <c:when test="${preferencesBean.notificationLevel eq 'separate'}"><input type="radio" name="notificationLevel" id="notificationSeparate" value="separate" checked="checked"/></c:when>
		  <c:otherwise><input type="radio" name="notificationLevel" id="notificationSeparate" value="separate"/></c:otherwise>
		</c:choose><label for="notificationSeparate">Send me each notification separately</label>
	      </p>
	      <p class="radio">
		<c:choose>
		  <c:when test="${preferencesBean.notificationLevel eq 'digest'}"><input type="radio" name="notificationLevel" value="digest" id="notificationDigest" checked="checked"/></c:when>
		  <c:otherwise><input type="radio" name="notificationLevel" value="digest" id="notificationDigest"/></c:otherwise>
		</c:choose><label for="notificationDigest">Send me one email per day summarizing all notifications</label>
	      </p>
	      <p class="radio">
		<c:choose>
		  <c:when test="${preferencesBean.notificationLevel eq 'none'}"><input type="radio" name="notificationLevel" value="none" id="notificationNone" checked="checked"/></c:when>
		  <c:otherwise><input type="radio" name="notificationLevel" value="none" id="notificationNone"/></c:otherwise>
		</c:choose><label for="notificationNone">Do not send me notifications for this subspace</label>
	      </p>
	      <p class="radio">
		<c:choose>
		  <c:when test="${preferencesBean.notificationLevel eq 'nopreference'}"><input type="radio" name="notificationLevel" value="nopreference" id="notificationNoPreference" checked="checked"/></c:when>
		  <c:otherwise><input type="radio" name="notificationLevel" value="nopreference" id="notificationNoPreference"/></c:otherwise>
		</c:choose><label for="notificationNoPreference">I do not have a preference</label>
	      </p>
	      <input type="submit" name="save" value="Save"/>
	      <input type="submit" name="save" value="Cancel"/>
	      <input type="hidden" name="action" value="updatePreferences"/>
	      <input type="hidden" name="pageName" value="${viewBean.pageName}"/>
	    </form>
	  </div>
	</div>
      </div>
      <jsp:directive.include file="footer.jsp"/>
    </jsp:element>
  </html>
</jsp:root>
