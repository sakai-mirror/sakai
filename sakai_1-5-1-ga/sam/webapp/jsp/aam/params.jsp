<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<logic:present parameter="agent_id">
  <bean:parameter id="agent_id" name="agent_id" />
  <% session.setAttribute("agent_id", request.getParameter("agent_id")); %>
</logic:present>
<logic:present parameter="agent_name">
  <bean:parameter id="agent_name" name="agent_name" />
  <% session.setAttribute("agent_name",
      request.getParameter("agent_name")); %>
</logic:present>
<logic:present parameter="course_id">
  <bean:parameter id="course_id" name="course_id" />
  <% session.setAttribute("course_id", request.getParameter("course_id")); %>
</logic:present>
<logic:present parameter="course_name">
  <bean:parameter id="course_name" name="course_name" />
  <% session.setAttribute("course_name",
      request.getParameter("course_name")); %>
</logic:present>
