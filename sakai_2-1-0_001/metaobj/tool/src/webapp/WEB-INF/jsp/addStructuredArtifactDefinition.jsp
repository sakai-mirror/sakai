<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>



<div class ="chefPortletContent">

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

<fieldset>
<legend>Form</legend>

<table class="chefEditItem">

<spring:bind path="bean.xslConversionFileId">
<tr>
<td colspan="2">
<c:if test="${!empty bean.id}">
     <c:forEach var="error" items="${status.errorMessages}">
       <B><FONT color=RED>
         <BR><c:out value="${error}"/>
       </FONT></B>
     </c:forEach>
</c:if>
</td>
</tr>
</spring:bind>

<spring:bind path="bean.description">
<tr>
<td class="chefLabel">Name:</td>
<td>
<input type="text" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>"/>
<span class="error_message"><c:out value="${status.errorMessage}"/></span>
</td>
</tr>
</spring:bind>

<tr>
<td class="chefLabel" nowrap>Schema File (xsd):</td>
<td>
<c:if test="${!empty bean.id}">
<span class="error_message">
Warning: changing the .xsd file may adversely impact all existing items built with this Form
</span>
<br/>
</c:if>
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
Pick Schema</a>
</td></tr>
</spring:bind>

<spring:bind path="bean.documentRoot">
<tr>
<td class="chefLabel" nowrap>Document Root Node:</td>
<td>
<select name="<c:out value="${status.expression}" />" id="<c:out value="${status.expression}" />">
<c:forEach var="element" items="${elements}" varStatus="status">
<option value="<c:out value="${element}"/>"><c:out value="${element}"/></option>
</c:forEach>
</select>
</td>
</tr>
</spring:bind>

<c:if test="${!empty bean.id}">
<tr>
<td class="chefLabel" nowrap>Conversion File (xsl):</td>
<td>
<spring:bind path="bean.xslFileName">
<input type="text" id="<c:out value="${status.expression}"/>" name="<c:out value="${status.expression}"/>"
      disabled="true" value="<c:out value="${status.value}" />" />
</spring:bind>
<spring:bind path="bean.xslConversionFileId">
<input type="hidden" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>"
      value="<c:out value="${status.value}"/>" />

     <span class="error_message"><c:out value="${status.errorMessage}"/></span>
<a href="#"
   onclick="document.forms[0]['filePickerAction'].value='pickTransform';
            document.forms[0]['filePickerFrom'].value='<spring:message
               code="filePickerMessage.pickTransform" />';
            document.forms[0].submit();return false;">
Pick Transformation File</a>
</td></tr>
</spring:bind>
</c:if>
<spring:bind path="bean.instruction">
<tr>
<td class="chefLabel">Instruction:</td>
<td>
<textarea id="<c:out value="${status.expression}"/>" name="<c:out value="${status.expression}"/>" cols="80" rows="25"><c:out value="${status.value}"/></textarea>
<span class="error_message"><c:out value="${status.errorMessage}"/></span>
</spring:bind>
</td>
</tr>
</table>

</fieldset>

<script type="text/javascript">
   //DO NOT use osp:url tag here trailing '?pid=34234' doesn't work with htmlarea
  _editor_url = "<c:url value="/js/htmlarea/"/>";
  _editor_lang = "en";
</script>
<script type="text/javascript" src="<c:url value="/js/htmlarea/htmlarea.js"/>"></script>
<script type="text/javascript" defer="1">
    HTMLArea.replace('instruction');
</script>

<p class="act">
<input name="action" type="submit" value="Save"/>
<input name="action" id="action" type="hidden" value=""/>
<input name="filePickerAction" id="filePickerAction" type="hidden" value="" />
<input name="filePickerFrom" id="filePickerFrom" type="hidden" value="" />

<input type="button" value="Cancel" onclick="window.document.location='<osp:url value="listStructuredArtifactDefinitions.osp"/>'">
</p>

</form>

</div>
