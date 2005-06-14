<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<tiles:insert definition="DefaultLayout" flush="true">
<tiles:put name="title" value="Please Login" />
<tiles:put name="body" value="/jsp/login/SimpleFormLogin_content.jsp" />
</tiles:insert>