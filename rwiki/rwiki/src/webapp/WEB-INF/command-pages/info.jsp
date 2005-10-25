<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0"
  xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:fn="http://java.sun.com/jsp/jstl/functions"
  ><jsp:directive.page language="java"
		contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
		errorPage="/WEB-INF/command-pages/errorpage.jsp"
	/><jsp:text
	><![CDATA[<?xml version="1.0" encoding="UTF-8" ?>]]>
  </jsp:text>
  <jsp:text>
    <![CDATA[ <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> ]]>
  </jsp:text>
  <c:set var="renderBean" value="${requestScope.rsacMap.renderBean}" />
  <c:set var="rightRenderBean"
    value="${requestScope.rsacMap.infoRightRenderBean}" />
  <c:set var="reverseHistoryHelperBean"
    value="${requestScope.rsacMap.reverseHistoryHelperBean}" />
  <c:set var="permissionsBean"
    value="${requestScope.rsacMap.permissionsBean}" />
  <c:set var="referencesBean"
    value="${requestScope.rsacMap.referencesBean}" />
  <c:set var="homeBean" value="${requestScope.rsacMap.homeBean}"/>
  <c:set var="updatePermissionsBean" value="${requestScope.rsacMap.updatePermissionsBean}"/>
  <c:set var="currentRWikiObject" value="${requestScope.rsacMap.currentRWikiObject}"/>
  <c:set var="realmBean" value="${requestScope.rsacMap.realmBean}"/>
  <html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
    <head>
      <title>Info: <c:out value="${realmBean.localName}" /></title>
      <jsp:expression>request.getAttribute("sakai.html.head")</jsp:expression>
    </head>  	
    <jsp:element name="body">
      <jsp:attribute name="onload"><jsp:expression>request.getAttribute("sakai.html.body.onload")</jsp:expression>parent.updCourier(doubleDeep,ignoreCourier);</jsp:attribute>
      <jsp:directive.include file="header.jsp"/>
      <div id="rwiki_container">
	<div class="portletBody">
	  <div class="navIntraTool">
	    <form action="?#" method="get" class="rwiki_searchForm">
	      <span class="rwiki_pageLinks">
		<!-- Home Link -->
		<jsp:element name="a"><jsp:attribute name="href"><c:out value="${homeBean.homeLinkUrl}"/></jsp:attribute><c:out value="${homeBean.homeLinkValue}"/></jsp:element>
		<!-- View Link -->
		<jsp:element name="a"><jsp:attribute name="href"><c:out value="${realmBean.viewUrl}"/></jsp:attribute>view</jsp:element>
		<!-- Edit Link -->
		<jsp:element name="a"><jsp:attribute name="href"><c:out value="${realmBean.editUrl}"/></jsp:attribute>edit</jsp:element>
		<!-- Info Link -->
		<jsp:element name="a"><jsp:attribute name="href"><c:out value="${realmBean.infoUrl}"/></jsp:attribute><jsp:attribute name="class">rwiki_currentPage</jsp:attribute>info</jsp:element>
		<!-- History Link -->
		<jsp:element name="a"><jsp:attribute name="href"><c:out value="${realmBean.historyUrl}"/></jsp:attribute>history</jsp:element>
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

	    <h3>Info: <c:out value="${realmBean.localName}" /></h3>
	    <c:if test="${fn:length(errorBean.errors) gt 0}">
	      <!-- XXX This is hideous -->
	      <p class="validation" style="clear: none;">

		<c:forEach var="error" items="${errorBean.errors}">
		  <c:out value="${error}"/>
		</c:forEach>
	      </p>
	    </c:if>
	    <form action="?#" method="post">
	      <div class="rwikirenderedContent">
		<table class="rwiki_info" cellspacing="0">
		  <tbody>
		    <tr id="permissions">
		    	<th>Page Permissions<a href="#" class="rwiki_help_popup_link" onClick="showPopupHere(this,'permissionshelp'); return false;"
		    		onMouseOut="hidePopup('permissionshelp');" >?</a></th>
		    	<td>Create<a href="#" class="rwiki_help_popup_link" onClick="showPopupHere(this,'createhelp'); return false;"
		    		onMouseOut="hidePopup('createhelp');" >?</a></td>
		    	<td>Read<a href="#" class="rwiki_help_popup_link" onClick="showPopupHere(this,'readhelp'); return false;"
		    		onMouseOut="hidePopup('readhelp');" >?</a></td>
		    	<td>Update<a href="#" class="rwiki_help_popup_link" onClick="showPopupHere(this,'updatehelp'); return false;"
		    		onMouseOut="hidePopup('updatehelp');" >?</a></td>
		    	<!--<td>Delete<a href="#" class="rwiki_help_popup_link" onClick="showPopupHere(this,'deletehelp'); return false;"
		    		onMouseOut="hidePopup('deletehelp');" >?</a></td>-->
		    	<td>Admin<a href="#" class="rwiki_help_popup_link" onClick="showPopupHere(this,'adminhelp'); return false;"
		    		onMouseOut="hidePopup('adminhelp');" >?</a></td>
		    	<td>Super Admin<a href="#" class="rwiki_help_popup_link" onClick="showPopupHere(this,'superadminhelp'); return false;"
		    		onMouseOut="hidePopup('superadminhelp');" >?</a></td>
		    </tr>
		  	
		  	<c:choose>
		  		<c:when test="${permissionsBean.adminAllowed}" >
		  			<tr id="permissionsOwner">
		  				<th>Page Owner<a href="#" class="rwiki_help_popup_link" onClick="showPopupHere(this,'pageownerhelp'); return false;"
		  					onMouseOut="hidePopup('pageownerhelp');" >?</a></th>
		  				<td></td>
		  				<td>
		  					<c:choose>
		  						<c:when test="${currentRWikiObject.ownerRead}">
		  							<input type="checkbox" name="ownerRead" checked="checked"/>
		  						</c:when>
		  						<c:otherwise>
		  							<input type="checkbox" name="ownerRead"/>
		  						</c:otherwise>
		  					</c:choose>
		  				</td>
		  				<td>
		  					<c:choose>
		  						<c:when test="${currentRWikiObject.ownerWrite}">
		  							<input type="checkbox" name="ownerWrite" checked="checked" onClick="if (checked) ownerRead.checked = checked;"/>
		  						</c:when>
		  						<c:otherwise>
		  							<input type="checkbox" name="ownerWrite" onClick="if (checked) ownerRead.checked = checked;"/>
		  						</c:otherwise>
		  					</c:choose>
		  				</td>
		  				
		  				<td>
		  					<c:choose>
		  						<c:when test="${currentRWikiObject.ownerAdmin}">
		  							<input type="checkbox" name="ownerAdmin" checked="checked" onClick="if ( checked ) { ownerRead.checked = checked; ownerWrite.checked = checked; }"/>
		  						</c:when>
		  						<c:otherwise>
		  							<input type="checkbox" name="ownerAdmin" onClick="if (checked) { ownerRead.checked = checked; ownerWrite.checked = checked; }"/>
		  						</c:otherwise>
		  					</c:choose>
		  				</td>
		  				<td></td>
		  			</tr>
		  			<tr id="permissionsPublic">
		  				<th>Public<a href="#" class="rwiki_help_popup_link" onClick="showPopupHere(this,'publichelp'); return false;"
		  					onMouseOut="hidePopup('publichelp');" >?</a></th>
		  				<td></td>
		  				<td>
		  					<c:choose>
		  						<c:when test="${currentRWikiObject.publicRead}">
		  							<input type="checkbox" name="publicRead" checked="checked"/>
		  						</c:when>
		  						<c:otherwise>
		  							<input type="checkbox" name="publicRead"/>
		  						</c:otherwise>
		  					</c:choose>
		  				</td>
		  				<td>
		  					<c:choose>
		  						<c:when test="${currentRWikiObject.publicWrite}">
		  							<input type="checkbox" name="publicWrite" checked="checked" onClick="if ( checked) publicRead.checked = checked;"/>
		  						</c:when>
		  						<c:otherwise>
		  							<input type="checkbox" name="publicWrite" onClick="if (checked) publicRead.checked = checked;"/>
		  						</c:otherwise>
		  					</c:choose>
		  				</td>
		  				<!--<td></td>-->
		  				<td></td>
		  				<td></td>
		  				<td></td>
		  			</tr>
		  			
		  			
		  			
		  			
		  			
		  			<tr id="permissionsGroup">
		  				<th>Site Enable<a href="#" class="rwiki_help_popup_link" onClick="showPopupHere(this,'siteenablehelp'); return false;"
		  					onMouseOut="hidePopup('siteenablehelp');" >?</a></th>
		  				<td></td>
		  				<td>
		  					<c:choose>
		  						<c:when test="${currentRWikiObject.groupRead}">
		  							<input type="checkbox" name="groupRead" checked="checked" 
		  								onClick="setPermissionDisplay(1,checked,'rwiki_info_secure_granted','rwiki_info_secure_denied',false,groupRead.checked,groupWrite.checked,groupAdmin.checked,false);"/>
		  						</c:when>
		  						<c:otherwise>
		  							<input type="checkbox" name="groupRead" onClick="setPermissionDisplay(1,checked,'rwiki_info_secure_granted','rwiki_info_secure_denied',false,groupRead.checked,groupWrite.checked,groupAdmin.checked,false);"/>
		  						</c:otherwise>
		  					</c:choose>
		  				</td>
		  				<td>
		  					<c:choose>
		  						<c:when test="${currentRWikiObject.groupWrite}">
		  							<input type="checkbox" name="groupWrite" checked="checked" 
		  								onClick="setPermissionDisplay(2,checked,'rwiki_info_secure_granted','rwiki_info_secure_denied',false,groupRead.checked,groupWrite.checked,groupAdmin.checked,false);}" />
		  						</c:when>
		  						<c:otherwise>
		  							<input type="checkbox" name="groupWrite" onClick="setPermissionDisplay(2,checked,'rwiki_info_secure_granted','rwiki_info_secure_denied',false,groupRead.checked,groupWrite.checked,groupAdmin.checked,false);" />
		  						</c:otherwise>
		  					</c:choose>
		  				</td>
		  				<td>
		  					<c:choose>
		  						<c:when test="${currentRWikiObject.groupAdmin}">
		  							<input type="checkbox" name="groupAdmin" checked="checked" onClick="setPermissionDisplay(3,checked,'rwiki_info_secure_granted','rwiki_info_secure_denied',false,groupRead.checked,groupWrite.checked,groupAdmin.checked,false);"/>
		  						</c:when>
		  						<c:otherwise>
		  							<input type="checkbox" name="groupAdmin" onClick="setPermissionDisplay(3,checked,'rwiki_info_secure_granted','rwiki_info_secure_denied',false,groupRead.checked,groupWrite.checked,groupAdmin.checked,false);"/>
		  						</c:otherwise>
		  					</c:choose>
		  				</td>
		  				<td></td>
		  			</tr>
		  		</c:when>
		  		<!--
		  		
		  		No Page edit allowed
		  		
		  		-->
		  		
		  		
		  		<c:otherwise>
		  			<tr id="permissionsOwner">
		  				<th>Page Owner<a href="#" class="rwiki_help_popup_link" onClick="showPopupHere(this,'pageownerdisphelp'); return false;"
		  					onMouseOut="hidePopup('pageownerdisphelp');" >?</a></th>
		  				<td></td>
		  				<td>
		  					<c:choose>
		  						<c:when test="${currentRWikiObject.ownerRead}">
		  							<span class="rwiki_info_page_granted" >yes</span>		  							
		  						</c:when>
		  						<c:otherwise>
		  							<span class="rwiki_info_page_denied" >no</span>		  							
		  						</c:otherwise>
		  					</c:choose>
		  				</td>
		  				<td>
		  					<c:choose>
		  						<c:when test="${currentRWikiObject.ownerWrite}">
		  							<span class="rwiki_info_page_granted" >yes</span>		  							
		  						</c:when>
		  						<c:otherwise>
		  							<span class="rwiki_info_page_denied" >no</span>		  							
		  						</c:otherwise>
		  					</c:choose>
		  				</td>
		  				
		  				<!--
		  					<td>
		  					<c:choose>
		  					<c:when test="${currentRWikiObject.ownerDelete}">
		  					<span class="rwiki_info_page_granted" >yes</span>		  							
		  					</c:when>
		  					<c:otherwise>
		  					<span class="rwiki_info_page_denied" >no</span>		  							
		  					</c:otherwise>
		  					</c:choose>
		  					</td>
		  				-->
		  				<td>
		  					<c:choose>
		  						<c:when test="${currentRWikiObject.ownerAdmin}">
		  							<span class="rwiki_info_page_granted" >yes</span>		  							
		  						</c:when>
		  						<c:otherwise>
		  							<span class="rwiki_info_page_denied" >no</span>		  							
		  						</c:otherwise>
		  					</c:choose>
		  				</td>
		  				<td></td>
		  			</tr>
		  			<tr id="permissionsPublic">
		  				<th>Public<a href="#" class="rwiki_help_popup_link" onClick="showPopupHere(this,'publicdisphelp'); return false;"
		  					onMouseOut="hidePopup('publicdisphelp');" >?</a></th>
		  				<td></td>
		  				<td>
		  					<c:choose>
		  						<c:when test="${currentRWikiObject.publicRead}">
		  							<span class="rwiki_info_page_granted" >yes</span>		  							
		  						</c:when>
		  						<c:otherwise>
		  							<span class="rwiki_info_page_denied" >no</span>		  							
		  						</c:otherwise>
		  					</c:choose>
		  				</td>
		  				<td>
		  					<c:choose>
		  						<c:when test="${currentRWikiObject.publicWrite}">
		  							<span class="rwiki_info_page_granted" >yes</span>		  							
		  						</c:when>
		  						<c:otherwise>
		  							<span class="rwiki_info_page_denied" >no</span>		  							
		  						</c:otherwise>
		  					</c:choose>
		  				</td>
		  				<!--<td></td>-->
		  				<td></td>
		  				<td></td>
		  				<td></td>
		  			</tr>
		  			
		  			
		  			
		  			
		  			
		  			<tr id="permissionsGroup">
		  				<th>Site Enable<a href="#" class="rwiki_help_popup_link" onClick="showPopupHere(this,'siteenabledisphelp'); return false;"
		  					onMouseOut="hidePopup('siteenabledisphelp');" >?</a></th>
		  				<td></td>
		  				<td>
		  					<c:choose>
		  						<c:when test="${currentRWikiObject.groupRead}">
		  							<span class="rwiki_info_page_granted" >yes</span>		  							
		  						</c:when>
		  						<c:otherwise>
		  							<span class="rwiki_info_page_denied" >no</span>		  							
		  						</c:otherwise>
		  					</c:choose>
		  				</td>
		  				<td>
		  					<c:choose>
		  						<c:when test="${currentRWikiObject.groupWrite}">
		  							<span class="rwiki_info_page_granted" >yes</span>		  							
		  						</c:when>
		  						<c:otherwise>
		  							<span class="rwiki_info_page_denied" >no</span>		  							
		  						</c:otherwise>
		  					</c:choose>
		  				</td>
		  				<!--
		  					<td>
		  					<c:choose>
		  					<c:when test="${currentRWikiObject.groupDelete}">
		  					<span class="rwiki_info_page_granted" >yes</span>		  							
		  					</c:when>
		  					<c:otherwise>
		  					<span class="rwiki_info_page_denied" >no</span>		  							
		  					</c:otherwise>
		  					</c:choose>
		  					</td>
		  				-->
		  				<td>
		  					<c:choose>
		  						<c:when test="${currentRWikiObject.groupAdmin}">
		  							<span class="rwiki_info_page_granted" >yes</span>		  							
		  						</c:when>
		  						<c:otherwise>
		  							<span class="rwiki_info_page_denied" >no</span>		  							
		  						</c:otherwise>
		  					</c:choose>
		  				</td>
		  				<td></td>
		  			</tr>		  			
		  		</c:otherwise>
		  	</c:choose>  			  	
		  	<script type="text/javascript" >
		  		var permissionsMatrix = new Array();
		  		var permissionsMatriNCols = 5;
		  		var permissionsStem = "permissions_";
		  		function setPermissionDisplay(column,enabled,enabledClass,disabledClass,s0,s1,s2,s3,s4) {
		  			// bit of good old vector processing !
		  			var switches = new Array();
		  			switches[0] = s0;
		  			switches[1] = s1;
		  			switches[2] = s2;
		  			switches[3] = s3;
		  			switches[4] = s4;
		  		
		  			for ( i = column; i &lt; permissionsMatrix.length; i += permissionsMatriNCols ) {
		  				var elName = permissionsStem+i;
		  				if ( enabled ) {
					  		permissionsMatrix[i][2]++;
		  					if ( permissionsMatrix[i][2] &gt;= 1 ) setClassName(elName,enabledClass);
		  				} else {
					  		permissionsMatrix[i][2]--;
		  					if ( permissionsMatrix[i][2] &lt;= 0 ) setClassName(elName,disabledClass);
		  				}
		  				if ( permissionsMatrix[i][0] ) {
		  					for ( k = 0; k &lt; permissionsMatriNCols; k++ ) {
		  						j = i-column+k
		  						
		  						if ( permissionsMatrix[j][1].charAt(column) == "x" ) {
		  				 			elName = permissionsStem+j;
		  				 			if ( enabled ) {
		  				 				permissionsMatrix[j][2]++;
		  								if ( permissionsMatrix[j][2] &gt;= 1 ) setClassName(elName,enabledClass);
		  				 			} else {
		  				 				permissionsMatrix[j][2]--;
								  		if ( permissionsMatrix[j][2] &lt;= 0 ) setClassName(elName,disabledClass);
		  							}
		  				 		}
		  					}
		  				}
		  			} 
		  			
		  			//s = "";
		  			//for ( i = 0; i &lt; permissionsMatrix.length; i++ ) {
		  			//	s = s + " " + permissionsMatrix[i][2];
		  			//}
		  			//alert(" Counters "+s);
		  		}
		  		function setClassName(elId,className) {
		  			
			  		var el = null;
			  		if ( document.all ) {
			  			el = document.all[elId];
		  			} else {
		  				el = document.getElementById(elId);
		  			}
		  			if ( el != null ) {
		  				el.className = className;
		  			}
		  		}
		  		var pmi=0;
		  		var row
		  		<c:forEach var="role" items="${realmBean.roles}">
		  			var x = new Array(); 
					x[0] = <c:out value="${role.secureCreate}" />;
		  			x[1] = "----x";
		  			x[2] = 0;
		  			if ( <c:out value="${role.secureCreate}" />) x[2]++;
		  			if ( <c:out value="${role.secureSuperAdmin}" />) x[2]++;
		  			permissionsMatrix[pmi] = x;
		  			pmi++;
		  			x = new Array(); 
		  			x[0] = <c:out value="${role.secureRead}" />;
		  			x[1] = "--xxx";
		  			x[2] = 0;
		  			if ( <c:out value="${role.secureRead and currentRWikiObject.groupRead}" />   ) x[2]++;
		  			if ( <c:out value="${role.secureUpdate and currentRWikiObject.groupWrite}" />  ) x[2]++;
		  			if ( <c:out value="${role.secureAdmin and currentRWikiObject.groupAdmin}" />  ) x[2]++;
		  			if ( <c:out value="${role.secureSuperAdmin}" />) x[2]++;
		  			permissionsMatrix[pmi] = x;
		  			pmi++;
		  			x = new Array(); 
		  			x[0] = <c:out value="${role.secureUpdate}" />;
		  			x[1] = "---xx";
		  			x[2] = 0;
		  			if ( <c:out value="${role.secureUpdate and currentRWikiObject.groupWrite}" />  ) x[2]++;
		  			if ( <c:out value="${role.secureAdmin and currentRWikiObject.groupAdmin}" />  ) x[2]++;
		  			if ( <c:out value="${role.secureSuperAdmin}" />) x[2]++;
		  			permissionsMatrix[pmi] = x;
		  			pmi++;
		  			x = new Array(); 
		  			x[0] = <c:out value="${role.secureAdmin}" />;
		  			x[1] = "----x";
		  			x[2] = 0;
		  			if ( <c:out value="${role.secureAdmin and currentRWikiObject.groupAdmin}" />  ) x[2]++;
		  			if ( <c:out value="${role.secureSuperAdmin}" />) x[2]++;
		  			permissionsMatrix[pmi] = x;
		  			pmi++;
		  			x = new Array(); 
		  			x[0] = <c:out value="${role.secureSuperAdmin}" />;
		  			x[1] = "-----";
		  			x[2] = 0;
		  			if ( <c:out value="${role.secureSuperAdmin}" />) x[2]++;
		  			permissionsMatrix[pmi] = x;
		  			pmi++;
		  		</c:forEach>
		  	</script>
		  									
		  		
		  	
		  	
		  	
		  	<c:set var="pmcounter" value="${0}" />		  		
		    <c:forEach var="role" items="${realmBean.roles}">
		    	<tr class="permissionsGroupRole">
		    		<th><c:out value="${role.id}"/></th>
		    		<td>
		    			<jsp:element name="span" >
		    				<jsp:attribute name="id" >permissions_<c:out value="${pmcounter}" /></jsp:attribute>
		    				<c:set var="pmcounter" value="${pmcounter+1}" />
		    				<jsp:attribute name="class">rwiki_info_secure_granted</jsp:attribute>
		    				<c:choose>
		    				<c:when test="${role.secureCreate}">
		    						yes
		    				</c:when>
		    				<c:otherwise>
		    						no
		    				</c:otherwise>
		    				</c:choose>
		    			</jsp:element>
		    		</td>
		    		<td>
		    			<c:choose>
		    				<c:when test="${currentRWikiObject.groupRead}">
		    					<jsp:element name="span" >
		    						<jsp:attribute name="id" >permissions_<c:out value="${pmcounter}" /></jsp:attribute>
		    						<c:set var="pmcounter" value="${pmcounter+1}" />
		    						<jsp:attribute name="class">rwiki_info_secure_granted</jsp:attribute>
		    						<c:choose>		    						
		    						<c:when test="${role.secureRead}">
		    								yes
		    						</c:when>
		    						<c:otherwise>
		    								no
		    						</c:otherwise>
		    						</c:choose>
		    					</jsp:element>
		    				</c:when>
		    				<c:otherwise>
		    					<jsp:element name="span" >
		    						<jsp:attribute name="id" >permissions_<c:out value="${pmcounter}" /></jsp:attribute>
		    						<c:set var="pmcounter" value="${pmcounter+1}" />
		    						<jsp:attribute name="class">rwiki_info_secure_denied</jsp:attribute>
		    						<c:choose>
		    						<c:when test="${role.secureRead}">
		    								yes
		    						</c:when>
		    						<c:otherwise>
		    								no
		    						</c:otherwise>
		    						</c:choose>
		    					</jsp:element>
		    				</c:otherwise>
		    			</c:choose>
		    		</td>
		    		<td>
		    			<c:choose>
		    				<c:when test="${currentRWikiObject.groupWrite}">
		    					<jsp:element name="span" >
		    						<jsp:attribute name="id" >permissions_<c:out value="${pmcounter}" /></jsp:attribute>
		    						<c:set var="pmcounter" value="${pmcounter+1}" />
		    						<jsp:attribute name="class">rwiki_info_secure_granted</jsp:attribute>
		    						<c:choose>
		    							<c:when test="${role.secureUpdate}">
		    								yes
		    							</c:when>
		    							<c:otherwise>
		    								no
		    							</c:otherwise>
			    					</c:choose>
			    				</jsp:element>
		    				</c:when>
		    				<c:otherwise>
		    					<jsp:element name="span" >
		    						<jsp:attribute name="id" >permissions_<c:out value="${pmcounter}" /></jsp:attribute>
		    						<c:set var="pmcounter" value="${pmcounter+1}" />
		    						<jsp:attribute name="class">rwiki_info_secure_denied</jsp:attribute>
		    						<c:choose>
		    							<c:when test="${role.secureUpdate}">
		    								yes
		    							</c:when>
		    							<c:otherwise>
		    								no
		    							</c:otherwise>
			    					</c:choose>
			    				</jsp:element>	
		    				</c:otherwise>
		    			</c:choose>
		    		</td>
		    		<td>
		    			<c:choose>
		    				<c:when test="${currentRWikiObject.groupAdmin}">
		    					<jsp:element name="span" >
			    					<jsp:attribute name="id" >permissions_<c:out value="${pmcounter}" /></jsp:attribute>
		    						<c:set var="pmcounter" value="${pmcounter+1}" />
		    						<jsp:attribute name="class">rwiki_info_secure_granted</jsp:attribute>
			    					<c:choose>
		    							<c:when test="${role.secureAdmin}">
			    							yes
				    					</c:when>
			    						<c:otherwise>
		    								no
		    							</c:otherwise>
		    						</c:choose>
		    					</jsp:element>
		    				</c:when>
		    				<c:otherwise>
		    					<jsp:element name="span" >
		    						<jsp:attribute name="id" >permissions_<c:out value="${pmcounter}" /></jsp:attribute>
		    						<c:set var="pmcounter" value="${pmcounter+1}" />
		    						<jsp:attribute name="class">rwiki_info_secure_denied</jsp:attribute>
		    						<c:choose>
		    							<c:when test="${role.secureAdmin}">
		    								yes
			    						</c:when>
			    						<c:otherwise>
		    								no
			    						</c:otherwise>
		    						</c:choose>
		    					</jsp:element>
		    				</c:otherwise>
		    			</c:choose>
		    		</td>
		    		<td>
		    			<jsp:element name="span" >
		    				<jsp:attribute name="id" >permissions_<c:out value="${pmcounter}" /></jsp:attribute>
		    				<c:set var="pmcounter" value="${pmcounter+1}" />
		    				<jsp:attribute name="class">rwiki_info_secure_granted</jsp:attribute>
		    				<c:choose>
			    				<c:when test="${role.secureSuperAdmin}">
		    						yes
			    				</c:when>
			    				<c:otherwise>
		    						no
			    				</c:otherwise>
		    				</c:choose>
		    			</jsp:element>
		    		</td>
		    	</tr>
		    </c:forEach>
		    <c:if test="${permissionsBean.adminAllowed}">
		      <tr>
			<td colspan="7">
			  <div class="rwiki_editControl">
			    <p class="act">
			      <input type="hidden" name="pageName" value="${currentRWikiObject.name}" />
			      <input type="hidden" name="panel" value="Main"/>
			      <input type="hidden" name="action" value="updatePermissions"/>
			      <input type="hidden" name="version" value="${currentRWikiObject.version.time}"/>
			      <input type="submit" name="updatePermissions" value="save"/>
			      <input type="hidden" name="realm" value="${currentRWikiObject.realm }"/>
			      <c:if test="${realmBean.siteUpdateAllowed}">
				In addition to editing the page permission you may <a href="${realmBean.editRealmUrl}">edit site permissions</a>
			      </c:if>
			      <!--<c:if test="${updatePermissionsBean.updatePermissionsMethod ne null}">-->
				<!--<input type="submit" name="updatePermissions" value="overwrite"/>-->
				<!--</c:if>-->
			    </p>
			  </div>

			</td>
		      </tr>
		    </c:if>

		  	
		    <tr id="incommingStart" >
		    	<th>Incoming<a href="#" class="rwiki_help_popup_link" onClick="showPopupHere(this,'incomminghelp'); return false;"
		    		onMouseOut="hidePopup('incomminghelp');" >?</a></th>
		      <td colspan="6">
			<c:set var="referencingLinks"
			  value="${referencesBean.referencingPageLinks}" />
			<c:if test="${fn:length(referencingLinks) gt 0}">
			  <ul id="referencingLinks">
			    <c:forEach var="item" items="${referencingLinks}">
			      <li><c:out value="${item}" escapeXml="false" /></li>
			    </c:forEach>
			  </ul>
			</c:if>
		      </td>
		    </tr>
		    <tr>
		    	<th>Outgoing<a href="#" class="rwiki_help_popup_link" onClick="showPopupHere(this,'outgoinghelp'); return false;"
		    		onMouseOut="hidePopup('outgoinghelp');" >?</a></th>
		      <td colspan="6">
			<c:set var="referencedLinks"
			  value="${referencesBean.referencedPageLinks }" /> 
			<c:if test="${fn:length(referencedLinks) gt 0}">
			  <ul id="referencedLinks">
			    <c:forEach var="item" items="${referencedLinks }">
			      <li><c:out value="${item }" escapeXml="false" /></li>
			    </c:forEach>
			  </ul>
			</c:if>
		      </td>
		    </tr>
		    <tr>
		    	<th>Owner<a href="#" class="rwiki_help_popup_link" onClick="showPopupHere(this,'ownerhelp'); return false;"
		    		onMouseOut="hidePopup('ownerhelp');" >?</a></th>
		      <td colspan="6"><c:out value="${currentRWikiObject.owner}"/></td>
		    </tr>
		    <tr>
		    	<th>Global Name<a href="#" class="rwiki_help_popup_link" onClick="showPopupHere(this,'globalhelp'); return false;"
		    		onMouseOut="hidePopup('globalhelp');" >?</a></th>
		      <td colspan="6"><c:out value="${realmBean.pageName }"/></td>
		    </tr>
		    <tr>
		    	<th>Permission Section<a href="#" class="rwiki_help_popup_link" onClick="showPopupHere(this,'realmhelp'); return false;"
		    		onMouseOut="hidePopup('realmhelp');" >?</a></th>
		      <td colspan="6"><c:out value="${currentRWikiObject.realm}"/></td>
		    </tr>
		    <tr>
		    	<th>Id<a href="#" class="rwiki_help_popup_link" onClick="showPopupHere(this,'idhelp'); return false;"
		    		onMouseOut="hidePopup('idhelp');" >?</a></th>
		      <td colspan="6"><c:out value="${currentRWikiObject.id}"/></td>
		    </tr>
		    <tr>
		    	<th>Last Edited<a href="#" class="rwiki_help_popup_link" onClick="showPopupHere(this,'lastedithelp'); return false;"
		    		onMouseOut="hidePopup('lastedithelp');" >?</a></th>
		      <td colspan="6"><c:out value="${currentRWikiObject.version}"/> by <c:out value="${currentRWikiObject.user}"/></td>
		    </tr>
		    <tr>
		    	<th>SHA-1<a href="#" class="rwiki_help_popup_link" onClick="showPopupHere(this,'digesthelp'); return false;"
		    		onMouseOut="hidePopup('digesthelp');" >?</a></th>
		      <td colspan="6"><c:out value="${currentRWikiObject.sha1}"/></td>
		    </tr>
		  </tbody>
		</table>
	      </div>


	    </form>
	  </div>
	</div>
      </div>
      <jsp:directive.include file="footer.jsp"/>
    	<div id="permissionshelp" style=" position: absolute; visibility: hidden;" 
    		class="rwiki_help_popup" >
    		<h3>Page Permissions</h3>
    		Each page has a set of page permissions that confer
    		rights on the Page owner, members of the site and the public    		
    	</div>
    	<div id="createhelp" style=" position: absolute; visibility: hidden;" 
    		class="rwiki_help_popup" >
    		<h3>Create Page Site Permission</h3>
    		This is a site member permission, independant of the page,
    		that allows a user who has been granted it the ability to 
    		create a page. To change you must use the role permissions editor. 
    		If you have permission, you can tick the checkbox to grant
    		If you do not have permission, you will see the state of the permission
    		
    	</div>
    	<div id="readhelp" style=" position: absolute; visibility: hidden;" 
    		class="rwiki_help_popup" >
    		<h3>Read Page Permission</h3>
    		Once granted, a user may read this page.
    		If you have permission, you can tick the checkbox to grant
    		If you do not have permission, you will see the state of the permission
    	</div>
    	<div id="updatehelp" style=" position: absolute; visibility: hidden;" 
    		class="rwiki_help_popup" >
    		<h3>Update Page Permission</h3>
    		Once granted, a user may edit the content of this page.
    		If you have permission, you can tick the checkbox to grant
    		If you do not have permission, you will see the state of the permission
    	</div>
    	<div id="deletehelp" style=" position: absolute; visibility: hidden;" 
    		class="rwiki_help_popup" >
    		<h3>Delete Page Permission</h3>
    		Once granted, a user may delete this page.
    		If you have permission, you can tick the checkbox to grant
    		If you do not have permission, you will see the state of the permission
    	</div>
    	<div id="adminhelp" style=" position: absolute; visibility: hidden;" 
    		class="rwiki_help_popup" >
    		<h3>Admin</h3>
    		Once granted, a user may edit the permissions of the page, and restore 
    		older versions. Granting admin permission alos grants read permission.    		
    		If you have permission, you can tick the checkbox to grant
    		If you do not have permission, you will see the state of the permission
    	</div>
    	<div id="superadminhelp" style=" position: absolute; visibility: hidden;" 
    		class="rwiki_help_popup" >
    		<h3>Super Admin Site Permission</h3>
    		A use that has Super Admin permission in their worksite role is allowed 
    		to do anything to any page.
    		If you have permission, you can tick the checkbox to grant
    		If you do not have permission, you will see the state of the permission
    	</div>
    	<div id="pageownerhelp" style=" position: absolute; visibility: hidden;" 
    		class="rwiki_help_popup" >
    		<h3>Page Owner Permissions</h3>
    		Each page has an owner, this defaults to the user who created the page.
    		Permissions that are ticked in this row (or displayed as 'yes') are granted
    		to the page owner.
    	</div>
    	<div id="publichelp" style=" position: absolute; visibility: hidden;" 
    		class="rwiki_help_popup" >
    		<h3>Public Permissions</h3>
    		Where a user is not the page owner and not a member of the worksite
    		they are a 'public' user. Granting Public permissions on the page 
    		gives a 'public' user the permission to read or update the page.
    	</div>
    	<div id="siteenablehelp" style=" position: absolute; visibility: hidden;" 
    		class="rwiki_help_popup" >
    		<h3>Site Enable Page permissions</h3>
    		When a site enabled page permission is granted, the worksite roles
    		are consulted to see if a user has permission to perform the action. 
    		Where the specific site enable page permission is not granted, the 
    		role permissions are not enabled for this page.
    	</div>
    	<div id="pageownerdisphelp" style=" position: absolute; visibility: hidden;" 
    		class="rwiki_help_popup" >
    		<h3>Page Owner Permissions</h3>
    		Each page has an owner, this defaults to the user who created the page.    		
    	</div>
    	<div id="publicdisphelp" style=" position: absolute; visibility: hidden;" 
    		class="rwiki_help_popup" >
    		<h3>Public Permissions</h3>
    		Where a user is not the page owner and not a member of the worksite
    		they are a 'public' user. Granting Public permissions on the page 
    		gives a 'public' user the permission to read or update the page.    		
    	</div>
    	<div id="siteenabledisphelp" style=" position: absolute; visibility: hidden;" 
    		class="rwiki_help_popup" >
    		<h3>Site Enable Page permissions</h3>
    		When a site enabled page permission is granted, the worksite roles
    		are consulted to see if a user has permission to perform the action. 
    		Where the specific site enable page permission is not granted, the 
    		role permissions are not enabled for this page.
    	</div>
    	<div id="incomminghelp" style=" position: absolute; visibility: hidden;" 
    		class="rwiki_help_popup" >
    		<h3>Incomming Pages</h3>
This is  a list of pages that reference or link to this page.    		
    	</div>
    	<div id="outgoinghelp" style=" position: absolute; visibility: hidden;" 
    		class="rwiki_help_popup" >
    		<h3>Outgoign Pages</h3>
This is a list of pages which this page references or links to.    		
    	</div>
    	<div id="ownerhelp" style=" position: absolute; visibility: hidden;" 
    		class="rwiki_help_popup" >
    		<h3>Page Owner</h3>
Each page has an owner; this is normally the user who create the page.    		
    	</div>
    	<div id="realmhelp" style=" position: absolute; visibility: hidden;" 
    		class="rwiki_help_popup" >
    		<h3>Page Section</h3>
    		This is the site permissions realm that is used to determin site permissions
    		on this page. Normally the realm is the default realm for the site contianing
    		the page.    		
    	</div>
    	<div id="idhelp" style=" position: absolute; visibility: hidden;" 
    		class="rwiki_help_popup" >
    		<h3>Page Id</h3>
    		Every page is given a unique ID.
    	</div>
    	<div id="globalhelp" style=" position: absolute; visibility: hidden;" 
    		class="rwiki_help_popup" >
    		<h3>Page Name</h3>
    		Every page has a name, this name is used in the wiki links. If pages between 
    		differnt worksites is to be referenced, the full page name should be used.
    	</div>
    	
    	<div id="lastedithelp" style=" position: absolute; visibility: hidden;" 
    		class="rwiki_help_popup" >
    		<h3>Last Edited</h3>
    		The date and time when the page was last edited.
    	</div>
    	<div id="digesthelp" style=" position: absolute; visibility: hidden;" 
    		class="rwiki_help_popup" >
    		<h3>SHA1 Digest</h3>
Each page has a SHA1 digest. If the digests of 2 pages are the same, the content of
both pages are identical. Any change in the page, will change the digest on the page.    		
    	</div>
    </jsp:element>
    
  </html>
</jsp:root>
