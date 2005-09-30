<%-- $Id: debug.jsp,v 1.4 2005/05/04 21:20:40 janderse.umich.edu Exp $ --%>
<f:view>
<sakaix:view title="debug tag - Sakai 2.0 JSF example">
<h:commandLink action="index"><h:outputText value="Back to examples index" /></h:commandLink>
<f:verbatim><a href="<%=request.getRequestURI()%>.source">View page source</a></f:verbatim>

<h:form id="theForm">
<hr />
<h2>debug tag example</h2>
<hr />

<pre>
    &lt;sakaix:debug /&gt;
</pre>
    <sakaix:debug /> 

 <br />

<pre>
    &lt;sakaix:debug rendered="false" /&gt;
</pre>
    <sakaix:debug rendered="false" />

 <br />

</h:form>
</sakaix:view>
</f:view>
