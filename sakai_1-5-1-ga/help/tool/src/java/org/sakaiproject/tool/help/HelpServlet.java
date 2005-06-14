/*
 * Created on Feb 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.sakaiproject.tool.help;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import KBSOAP.DosearchResponse;
import KBSOAP.KBSOAPPort;
import KBSOAP.KBSOAPService;
import KBSOAP.KBSOAPServiceLocator;
import KBSOAP.KbDocumentInfo;


/**
 * @author casong
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HelpServlet extends HttpServlet
{
  private static final String XML_TO_HTML_XSL = "/xsl/xmlToHtml.xsl";
  private static String LOGIN_PASSWORD;
  private static final String CONFIG_HELP_KEY = "help.soap.key";
  private static Log LOG = LogFactory.getLog("org.sakaiproject.tool.help.HelpServlet");

  /* (non-Javadoc)
   * @see javax.servlet.GenericServlet#init()
   */
  public void init() throws ServletException
  {    
    super.init();
    LOGIN_PASSWORD = ServerConfigurationService.getString(CONFIG_HELP_KEY); 
    
  }
  private ServletContext context; 
  protected void doGet(HttpServletRequest req,HttpServletResponse resp) 
      throws ServletException, java.io.IOException
  {
    KBSOAPService srv;
    KBSOAPPort port;
    DosearchResponse dsres;
    String whichkb = "";
    String[] domainlist = { "all", "oncoursecl" };
    String[] docids;
    String[] titles;
    int x;

    try {
        srv = new KBSOAPServiceLocator();
        port = srv.getKBSOAPPort();

        String docid = req.getParameter("docid");
        if(context == null)
        {
          context = req.getSession().getServletContext();
        }
        if(docid != null){
          KbDocumentInfo kbDocument = port.getDocument(LOGIN_PASSWORD, whichkb,docid, domainlist,"xml",null,null);
          PrintWriter writer = resp.getWriter();
          resp.setContentType("text/html");


          Document htmlDocument = this.getTransformedDocument(kbDocument);
          writer.write(this.serializeDocument(this.associateXsl(htmlDocument)));
        }
    }
    catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }
  private Document associateXsl(Document document)
  {
    Document htmlDocument = null;
//    document.appendChild(
//	    document.createProcessingInstruction("xml-stylesheet", 
//	            "href=\"" + XML_TO_HTML_XSL + "\" type=\"text/xsl\""));

//	unneccessarily adding duplicate declaration
//  -------------------------------------------
    //htmlDocument = addLinkToCss(document);
    //return htmlDocument;
	return document;
  }
  
  private Document getHtmlDocument(KbDocumentInfo kbDocument)
  {
    Document htmlDocument = null;
    try{
	    DocumentBuilderFactory dbfi = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = dbfi.newDocumentBuilder();
	    StringReader sr = new StringReader(new String(kbDocument.getText()));
	    InputSource is = new org.xml.sax.InputSource(sr);
	    Document document = builder.parse(is);
	    NodeList bodyElements = document.getElementsByTagName("body");
	    Node body = bodyElements.item(0);
	    htmlDocument = builder.newDocument();
	    Element htmlElement = htmlDocument.createElement("html");
	    Node importedBody = htmlDocument.importNode(body, true);
	    htmlElement.appendChild(importedBody);
	    htmlDocument.appendChild(htmlElement);
    }
    catch(ParserConfigurationException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(IOException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(SAXException e)
    {
      LOG.error(e.getMessage(), e);
    }
    return htmlDocument;
  }
  
  private Document getTransformedDocument(KbDocumentInfo kbDocument)
  {
    Document htmlDocument = null;
    try{
	    DocumentBuilderFactory dbfi = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = dbfi.newDocumentBuilder();
	    StringReader sr = new StringReader(new String(kbDocument.getText()));
	    InputSource is = new org.xml.sax.InputSource(sr);
	    Document document = builder.parse(is);
	    

	    Document stylesheet = readDocument(this.context, XML_TO_HTML_XSL);
	    htmlDocument = this.transformDocument(document, stylesheet);
	    htmlDocument = addLinkToCss(htmlDocument);
    }
    catch(ParserConfigurationException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(IOException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(SAXException e)
    {
      LOG.error(e.getMessage(), e);
    }
    return htmlDocument;
  }
  
  /**
   * @param htmlDocument
   * @return
   */
  private Document addLinkToCss(Document htmlDocument)
  {
    Document document = htmlDocument;
	String skinRoot = ServerConfigurationService.getString("skin.root", "/sakai-portal/css/");
	String skin = ServerConfigurationService.getString("skin.default", "sakai_core.css");
    NodeList nodes = document.getElementsByTagName("head");
    Node node = nodes.item(0);
    Element linkNode = htmlDocument.createElement("link");
    linkNode.setAttribute("href", skinRoot + skin);
    linkNode.setAttribute("rel", "stylesheet");
    linkNode.setAttribute("content-type", "text/css;charset=utf-8");
    if (node.getFirstChild()==null || !(node.getFirstChild().getNodeName().equals("link")))
    {
      node.appendChild(linkNode);
    }
    return htmlDocument;
  }

  private String serializeDocument(Document document)
  {
    if(document != null)
    {
      OutputFormat format = new OutputFormat(document);
      StringWriter writer = new StringWriter();
      XMLSerializer serializer = new XMLSerializer(writer, format);
       try
      {
        serializer.serialize(document);
      }
      catch(IOException e)
      {
        LOG.error(e.getMessage(), e);
      }

      return writer.toString();
    }
    else{
      return "<html><body>Unable to retrieve document</body></html>";
    }
  }
  
  private void transform(
      Transformer transformer, Source source, Result result)
    {
      if(LOG.isDebugEnabled())
      {
        LOG.debug(
          "performTransform(Transformer " + transformer + ", Source" + source +
          ", Result " + result + ")");
      }

      try
      {
        transformer.transform(source, result);
      }
      catch(TransformerException e)
      {
        LOG.error(e.getMessage(), e);
      }
    }
  
  private Document transformDocument(
      Document document, Document stylesheet)
    {
      if(LOG.isDebugEnabled())
      {
        LOG.debug(
          "Document transformDocument(Document " + document + ", Document " +
          stylesheet + ")");
      }
        LOG.debug(
          "Document transformDocument(Document " + document + ", Document " +
          stylesheet + ")");

      Document transformedDoc = createDocument();
      DOMSource docSource = new DOMSource(document);
      DOMResult docResult = new DOMResult(transformedDoc);
      Transformer transformer = createTransformer(stylesheet);
      transform(transformer, docSource, docResult);

      return transformedDoc;
    }
  
  private Document createDocument()
  {
 //    LOG.debug("createDocument()");

    Document document = null;
    DocumentBuilderFactory builderFactory =
      DocumentBuilderFactory.newInstance();
    builderFactory.setNamespaceAware(true);

    try
    {
      DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
      document = documentBuilder.newDocument();
    }
    catch(ParserConfigurationException e)
    {
//      LOG.error(e.getMessage(), e);
      e.printStackTrace();
    }

    return document;
  }
  
  private Transformer createTransformer(Document stylesheet)
  {

    if(LOG.isDebugEnabled())
    {
      LOG.debug("createTransformer(Document " + stylesheet + ")");
    }

    Transformer transformer = null;
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    URIResolver resolver = new URIResolver();
    transformerFactory.setURIResolver(resolver);


    try
    {
      DOMSource source = new DOMSource(stylesheet);
      String systemId = "/xsl";
      source.setSystemId(systemId);
      transformer = transformerFactory.newTransformer(source);
    }
    catch(TransformerConfigurationException e)
    {
//      LOG.error(e.getMessage(), e);
      e.printStackTrace();
    }

    return transformer;
  }
  
  private Document readDocument(ServletContext context, String path)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("readDocument(String " + path + ")");
    }

      LOG.debug("readDocument(String " + path + ")");

    Document document = null;
    InputStream inputStream = context.getResourceAsStream(path);
    String fullpath = context.getRealPath(path);
    LOG.debug("readDocument(full path) " + fullpath + ")");
    DocumentBuilderFactory builderFactory =
      DocumentBuilderFactory.newInstance();
    builderFactory.setNamespaceAware(true);

    try
    {
      DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
      document = documentBuilder.parse(inputStream);
    }
    catch(ParserConfigurationException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(SAXException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(IOException e)
    {
      LOG.error(e.getMessage(), e);
    }

    return document;
  }
}
