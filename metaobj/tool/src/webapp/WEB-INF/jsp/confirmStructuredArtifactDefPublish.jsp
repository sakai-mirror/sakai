<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<div class ="chefPortletContent">

<form method="POST" action="confirmSADPublish.osp">
<osp:form/>

<spring:bind path="bean.id">
<input type="hidden" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" />
</spring:bind>

<fieldset>
<legend>confirm</legend>

<div class="chefPageviewTitle">
<spring:bind path="bean.action">
<input type="hidden" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>"/>
<c:choose>
<c:when test="${status.value == 'site_publish'}">
Publishing will make this Form available to others in the worksite.
Are you sure you want to do this?
</c:when>
<c:when test="${status.value == 'global_publish'}">
Global publishing will make this Form available to all users in all worksites.
Are you sure you want to do this?
</c:when>
<c:when test="${status.value == 'suggest_global_publish'}">
You are submitting a request that this Form be made availabe on a global basis.
A system administator will need to approve your suggestion.
Are you sure you want to do this?
</c:when>
</c:choose>
</spring:bind>
</div>

</fieldset>
<spring:bind path="bean.description">
<span class="error_message"><c:out value="${status.errorMessage}"/></span>
</spring:bind>

<div class="chefButtonRow">
<input name="publish" type="submit" value="Yes"/>
<input name="_cancel" type="submit" value="No"/>
</div>

</form>

</div>
