<%-- $Id: inputFileUpload.jsp,v 1.4 2005/05/04 21:20:40 janderse.umich.edu Exp $ --%>
<f:view>
<sakaix:view title="inputFileUpload tag - Sakai 2.0 JSF example">
<h:commandLink action="index"><h:outputText value="Back to examples index" /></h:commandLink>
<f:verbatim><a href="<%=request.getRequestURI()%>.source">View page source</a></f:verbatim>


<hr />
<h2>inputFileUpload example</h2>
<hr />

 <h:form id="uploadForm" enctype="multipart/form-data">
   <%--
        target represents location where import will be temporarily stored
        check valueChangeListener for final destination
   --%>
   <h:outputText value="Upload: " />
   <%-- todo: set up backing bean --%>
   <sakaix:inputFileUpload target="/jsf/upload_tmp/myapplication"
       valueChangeListener="#{examplebean.listenForUpload}" />
   <h:commandButton value="Upload" type="submit"
         style="act" />
 </h:form>

<hr />
<h3>inputFileUpload usage:</h3>
<pre>
<font color="#000000"> &lt;</font><font color="#800080">h:form</font><font color="#000000"> </font><font color="#800000">id</font><font color="#000000">=</font><font color="#0000ff">"uploadForm"</font><font color="#000000"> </font><font color="#800000">enctype</font><font color="#000000">=</font><font color="#0000ff">"multipart/form-data"</font><font color="#000000">&gt;
     </font><font color="#008000">&lt;%--
          target represents location where import will be temporarily stored
          check valueChangeListener for final destination
     --%&gt;</font><font color="#000000">
   &lt;</font><font color="#800080">h:outputText</font><font color="#000000"> </font><font color="#800000">value</font><font color="#000000">=</font><font color="#0000ff">"Upload: "</font><font color="#000000"> /&gt;
   &lt;</font><font color="#800080">sakaix:inputFileUpload</font><font color="#000000"> </font><font color="#800000">target</font><font color="#000000">=</font><font color="#0000ff">"/jsf/upload_tmp/myapplication"</font><font color="#000000">
       </font><font color="#800000">valueChangeListener</font><font color="#000000">=</font><font color="#0000ff">"#{myHandler.myMethod}"</font><font color="#000000"> /&gt;
   &lt;</font><font color="#800080">h:commandButton</font><font color="#000000"> </font><font color="#800000">value</font><font color="#000000">=</font><font color="#0000ff">"Upload"</font><font color="#000000"> </font><font color="#800000">type</font><font color="#000000">=</font><font color="#0000ff">"submit"</font><font color="#000000">
         </font><font color="#800000">style</font><font color="#000000">=</font><font color="#0000ff">"act"</font><font color="#000000"> /&gt;
 &lt;</font><font color="#800080">/h:form</font><font color="#000000">&gt;
</font>
</pre>
<hr />

</sakaix:view>
</f:view>

