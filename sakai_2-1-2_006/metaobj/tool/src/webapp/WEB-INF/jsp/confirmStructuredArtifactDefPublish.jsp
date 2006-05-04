<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "messages"/>

<div class ="chefPortletContent">

<form method="POST" action="confirmSADPublish.osp">
<osp:form/>

<spring:bind path="bean.id">
<input type="hidden" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" />
</spring:bind>

<fieldset>
<legend><fmt:message key="legend_confirm"/></legend>

<div class="chefPageviewTitle">
<spring:bind path="bean.action">
<input type="hidden" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>"/>
<c:choose>
<c:when test="${status.value == 'site_publish'}">
<fmt:message key="confirm_publish"/>
</c:when>
<c:when test="${status.value == 'global_publish'}">
<fmt:message key="confirm_globalPublish"/>
</c:when>
<c:when test="${status.value == 'suggest_global_publish'}">
<fmt:message key="confirm_requestGlobalPublish"/>
</c:when>
</c:choose>
</spring:bind>
</div>

</fieldset>
<spring:bind path="bean.description">
<span class="error_message"><c:out value="${status.errorMessage}"/></span>
</spring:bind>

<p class="act">
<input name="publish" type="submit" value="<fmt:message key="button_yes"/>"/>
<input name="_cancel" type="submit" value="<fmt:message key="button_no"/>"/>
</p>

</form>

</div>
