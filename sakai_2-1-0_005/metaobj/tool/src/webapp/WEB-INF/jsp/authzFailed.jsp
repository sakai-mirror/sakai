<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core' %>
<%@ taglib prefix='osp' uri='http://www.theospi.org' %>


<H2>
Authorization failed for function <c:out value="${exception.function}" /> on object <c:out value="${exception.qualifier}" />
</H2>
<P>


<%
Exception ex = (Exception) request.getAttribute("exception");
ex.printStackTrace(new java.io.PrintWriter(out));
%>

<P>
<BR>
