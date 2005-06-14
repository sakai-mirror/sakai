<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<tiles:insert definition="DefaultLayout" flush="true">
<tiles:put name="title" value="Navigation Page" />
<tiles:put name="body" value="/jsp/navigation/Navigation_content.jsp" />
</tiles:insert>