<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="/spring" %>

<osp:authZMap prefix="osp.sad." var="can" qualifier="${authZqualifier}"/>
<!-- GUID=<c:out value="${newFormId}" /> -->

<div class="chefToolBarWrap">
<%--   <c:if test="${can.create}"> --%>
      <a href="<osp:url value="/addStructuredArtifactDefinition.osp?new=true"/>"
          title="New..." >New...</a>
<%--   </c:if> --%>
   <c:if test="${isMaintainer}">
      <a href="<osp:url value="/editPermissions.osp">
       <osp:param name="message" value="Set permissions for ${tool.title} in worksite '${worksite.title}'"/>
       <osp:param name="name" value="artifacts"/>
       <osp:param name="qualifier" value="${authZqualifier}"/>
       <osp:param name="returnView" value="listStructuredArtifactDefinitionsRedirect"/>
       </osp:url>"title="Permissions..." >Permissions...
     </a>
   </c:if>
</div>

<div class ="chefPortletContent">

<osp:url var="listUrl" value="listStructuredArtifactDefinitions.osp"/>
<osp:listScroll listUrl="${listUrl}" className="chefToolBarWrap" />

<c:if test="${!empty types}">
 <table class="chefFlatListViewTable" cellspacing="0" >
   <thead>
      <tr>
         <th scope="col">Name</th>
         <th scope="col">Owner</th>
         <th scope="col">Site Id</th>
         <th scope="col">Last Modified</th>
         <c:if test="${isGlobal == false}">
            <th scope="col">Site State</th>
         </c:if>
         <th scope="col">Global State</th>
      </tr>
   </thead>

  <c:forEach var="home" items="${types}">
    <TR>
      <TD nowrap>
         <c:out value="${home.type.description}" /><br/>&nbsp;&nbsp;&nbsp;
         <c:if test="${home.modifiable}">
            <c:if test="${can.edit}"><a href="<osp:url value="/editStructuredArtifactDefinition.osp"/>&id=<c:out value="${home.id}" />">edit</a></c:if>
            <c:if test="${!isGlobal && can.publish && home.canPublish}"> | <a href="<osp:url value="confirmSADPublish.osp"/>&action=site_publish&id=<c:out value="${home.id}" />">publish</a></c:if>
            <c:if test="${isGlobal && can.publish &&  home.canGlobalPublish}"> | <a href="<osp:url value="confirmSADPublish.osp"/>&action=global_publish&id=<c:out value="${home.id}" />">global publish</a></c:if>
            <c:if test="${!isGlobal && home.canSuggestGlobalPublish && can.suggest_global_publish}"> | <a href="<osp:url value="confirmSADPublish.osp"/>&action=suggest_global_publish&id=<c:out value="${home.id}" />">suggest for global publish</a></c:if>
            <c:if test="${isGlobal && home.canApproveGlobalPublish && can.publish}"> | <a href="<osp:url value="confirmSADPublish.osp"/>&action=global_publish&id=<c:out value="${home.id}" />">approve global publish</a></c:if>
         </c:if>
      </TD>
      <TD>
         <c:if test="${home.modifiable}">
            <c:out value="${home.owner.displayName}" />
         </c:if>
      </TD>
      <TD>
         <c:choose>
            <c:when test="${!home.modifiable}">
               global
            </c:when>
            <c:otherwise>
               <c:set var="site" value="${sites[home.siteId]}" />
               <c:if test="${!empty site}">
                  <c:out value="${site.title}" />
               </c:if>
               <c:if test="${empty site}">
                  global
               </c:if>
            </c:otherwise>
         </c:choose>
      </TD>
      <TD nowrap><c:out value="${home.modified}" /></TD>
      <c:if test="${isGlobal == false}">
      <TD>
         <c:choose>
            <c:when test="${home.global}">
               N/A
            </c:when>
            <c:otherwise>
               <c:if test="${home.modifiable}">
                  <c:choose>
                     <c:when test="${home.siteState == 0}">
                        unpublished
                     </c:when>
                     <c:when test="${home.siteState == 2}">
                        published
                     </c:when>
                  </c:choose>
               </c:if>
            </c:otherwise>
         </c:choose>
      </TD>
      </c:if>
      <TD>
         <c:if test="${home.modifiable}">
            <c:choose>
               <c:when test="${home.globalState == 0}">
                  unpublished
               </c:when>
               <c:when test="${home.globalState == 1}">
                  waiting for approval
               </c:when>
               <c:when test="${home.globalState == 2}">
                  published
               </c:when>
            </c:choose>
         </c:if>
         <c:if test="${!home.modifiable}">published</c:if>
      </TD>
    </TR>
  </c:forEach>
  </table>
<div class="chefPageviewTitle">
<br/>
'Site State' refers to the state of this Form within the worksite (eg. unpublished/published). <br/><br/>
'Global State' refers to the state of the Form within the system (eg. unpublished/waiting for approval/published).<br/><br/>
Only System Administrators can approve a Form for global access.
</div>
</c:if>

<c:if test="${empty types}">
No Forms Available.  Click 'New' to create one.
</c:if>

</div>