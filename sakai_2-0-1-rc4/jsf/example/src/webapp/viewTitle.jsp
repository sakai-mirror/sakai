<%-- $Id: viewTitle.jsp,v 1.4 2005/05/04 21:20:40 janderse.umich.edu Exp $ --%>
<f:view>
<sakaix:view title="viewTitle tag - Sakai 2.0 JSF example">
<h:commandLink action="index"><h:outputText value="Back to examples index" /></h:commandLink>
<f:verbatim><a href="<%=request.getRequestURI()%>.source">View page source</a></f:verbatim>


<hr />
<h2>viewTitle tag example</h2>
<hr />
<h:form id="theForm">
<pre>
    &lt;sakaix:viewTitle value="Item Detail Summary View" /&gt;
</pre>
    <sakaix:viewTitle value="Item Detail Summary View" />

 <br />

<pre>
    &lt;sakaix:viewTitle value="Assignments" indent="2" /&gt;
</pre>
    <sakaix:viewTitle value="Assignments" indent="2" />

 <br />

</h:form>
</sakaix:view>
</f:view>
