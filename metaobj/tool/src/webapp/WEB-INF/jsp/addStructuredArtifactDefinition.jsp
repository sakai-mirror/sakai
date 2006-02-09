<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>




<c:if test="${empty bean.id}">
<form method="POST" action="addStructuredArtifactDefinition.osp">
</c:if>

<c:if test="${!empty bean.id}">
<form method="POST" action="editStructuredArtifactDefinition.osp">
</c:if>
<osp:form/>

<spring:bind path="bean.id">
<input type="hidden" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" />
</spring:bind>


<c:if test="${empty bean.id}">
<h3>
Add Form
</h3>
<p class="instruction">
Please select an XSD file that defines your new form.
</p>
<p class="instruction">
Required items marked with <span class="reqStarInline">*</span>
</p>
</c:if>
<c:if test="${!empty bean.id}">
<h3>
Edit Form
</h3>
<p class="instruction">
Please edit your form properties
</p>
<p class="instruction">
Required items marked with <span class="reqStarInline">*</span>
</p>
</c:if>

<h4>Form</h4>

<p class="shorttext">
<spring:bind path="bean.description">
<span class="reqStar">*</span><label>Name</label>
<input type="text" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>"/>
<span class="error_message"><c:out value="${status.errorMessage}"/></span>
</spring:bind>
</p>

<c:if test="${!bean.published}">
<p class="shorttext">
<c:if test="${empty bean.id}"><span class="reqStar">*</span></c:if>
<label>Schema File (xsd)</label>
<spring:bind path="bean.schemaFileName">
<input type="text" id="<c:out value="${status.expression}"/>" name="<c:out value="${status.expression}"/>"
      disabled="true" value="<c:out value="${status.value}" />" />
</spring:bind>
<spring:bind path="bean.schemaFile">
<input type="hidden" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>"
      value="<c:out value="${status.value}"/>" />

     <span class="error_message"><c:out value="${status.errorMessage}"/></span>
<a href="#"
   onclick="document.forms[0]['filePickerAction'].value='pickSchema';
      document.forms[0]['filePickerFrom'].value='<spring:message
         code="filePickerMessage.pickSchema" />';
      document.forms[0].submit();return false;">
Select Schema File</a>
</spring:bind>
</p>

<p class="shorttext">
<spring:bind path="bean.documentRoot">
<label>Document Root Node</label>
<select name="<c:out value="${status.expression}" />" id="<c:out value="${status.expression}" />">
<c:forEach var="element" items="${elements}" varStatus="status">
<option value="<c:out value="${element}"/>"><c:out value="${element}"/></option>
</c:forEach>
</select>
</spring:bind>
</p>
</c:if>

<p class="longtext">
<spring:bind path="bean.instruction">
<label class="block">Instruction</label>
<table><tr>
<td>
<textarea id="<c:out value="${status.expression}"/>" name="<c:out value="${status.expression}"/>" cols="80" rows="25"><c:out value="${status.value}"/></textarea>
</td>
</tr></table>
<span class="error_message"><c:out value="${status.errorMessage}"/></span>
    <script type="text/javascript" src="/library/htmlarea/sakai-htmlarea.js"></script>
    <script type="text/javascript" defer="1">chef_setupformattedtextarea('<c:out value="${status.expression}"/>');</script>
</spring:bind>
</p>

<p class="act">
<input name="action" type="submit" value="Save"/>
<input name="action" id="action" type="hidden" value=""/>
<input name="filePickerAction" id="filePickerAction" type="hidden" value="" />
<input name="filePickerFrom" id="filePickerFrom" type="hidden" value="" />
<input type="button" value="Cancel" onclick="window.document.location='<osp:url value="listStructuredArtifactDefinitions.osp"/>'">
</p>

</form>

