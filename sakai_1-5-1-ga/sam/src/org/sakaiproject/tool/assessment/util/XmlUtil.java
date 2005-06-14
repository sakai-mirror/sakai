/*
 * Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
 *                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
 *
 * Licensed under the Educational Community License Version 1.0 (the "License");
 * By obtaining, using and/or copying this Original Work, you agree that you have read,
 * understand, and will comply with the terms and conditions of the Educational Community License.
 * You may obtain a copy of the License at:
 *
 *      http://cvs.sakaiproject.org/licenses/license_1_0.html
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */



package org.sakaiproject.tool.assessment.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import javax.servlet.ServletContext;
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

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.faces.context.FacesContext;


/**
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Organization: Sakai Project</p>
 * @author palcasi
 * mods:
 * @author Rachel Gollub rgollub@stanford.edu
 * @author Ed Smiley esmiley@stanford.edu
 * @version $Id: XmlUtil.java,v 1.4 2005/01/27 18:51:54 esmiley.stanford.edu Exp $
 */

public final class XmlUtil
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(XmlUtil.class);

  /**
   * Create document object
   *
   * @return Document
   */
  public static Document createDocument()
  {
    LOG.debug("createDocument()");

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
      LOG.error(e.getMessage(), e);
    }

    return document;
  }

  /**
   * Create document object from XML string
   *
   * @return Document
   */
  public static Document createDocument(String xmlString)
  {
    LOG.debug("createDocument()");

    Document document = null;
    DocumentBuilderFactory builderFactory =
      DocumentBuilderFactory.newInstance();
    builderFactory.setNamespaceAware(true);

    try
    {
      DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
        document = documentBuilder.parse(xmlString);
    }
    catch (IOException ex)
    {
      LOG.error(ex.getMessage(), ex);
    }
    catch (SAXException ex)
    {
      LOG.error(ex.getMessage(), ex);
    }
    catch(ParserConfigurationException e)
    {
      LOG.error(e.getMessage(), e);
    }

    return document;
  }
  /**
   * Read document from ServletContext/context path
   *
   * @param context ServletContext
   * @param path path
   *
   * @return Document
   */
  public static Document readDocument(ServletContext context, String path)
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

  /**
   * Read document from within FacesContext/context path
   *
   * @param context FacesContext
   * @param path path
   *
   * @return Document
   */
  public static Document readDocument(FacesContext context, String path)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("readDocument(String " + path + ")");
    }

      LOG.debug("readDocument(String " + path + ")");

    Document document = null;
    InputStream inputStream = context.getExternalContext().getResourceAsStream(path);
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
  public static DOMSource getDocumentSource(ServletContext context, String path)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("readDocument(String " + path + ")");
    }


    InputStream inputStream = context.getResourceAsStream(path);
    String realPath = null;
    try
    {
      realPath = context.getResource(path).toString();
    }
    catch (MalformedURLException e1)
    {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    DocumentBuilderFactory builderFactory =
      DocumentBuilderFactory.newInstance();
    builderFactory.setNamespaceAware(true);

    Document document = null;
    DOMSource source = null;
    try
    {
      DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
      document = documentBuilder.parse(inputStream);
      source = new DOMSource(document, realPath);
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

    return source;
  }
  /**
   * DOCUMENTATION PENDING
   *
   * @param context DOCUMENTATION PENDING
   * @param path DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public static Document readDocument(String path)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("readDocument(String " + path + ")");
    }

    Document document = null;

    try
    {
      InputStream inputStream = new FileInputStream(path);
      DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
      builderFactory.setNamespaceAware(true);
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

  /**
   * DOCUMENTATION PENDING
   *
   * @param xslSource DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public static Transformer createTransformer(Document stylesheet)
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
      String systemId = "/xml/xsl/report";
      source.setSystemId(systemId);
      transformer = transformerFactory.newTransformer(source);
    }
    catch(TransformerConfigurationException e)
    {
      LOG.error(e.getMessage(), e);
    }

    return transformer;
  }

  public static Transformer createTransformer(DOMSource source)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("createTransformer(DOMSource " + source + ")");
    }

    Transformer transformer = null;
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    URIResolver resolver = new URIResolver();
    transformerFactory.setURIResolver(resolver);

    try
    {
      transformer = transformerFactory.newTransformer(source);
    }
    catch(TransformerConfigurationException e)
    {
      LOG.error(e.getMessage(), e);
    }

    return transformer;

  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param transformer DOCUMENTATION PENDING
   * @param source DOCUMENTATION PENDING
   * @param result DOCUMENTATION PENDING
   */
  private static void transform(
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

  /**
   * DOCUMENTATION PENDING
   *
   * @param document DOCUMENTATION PENDING
   * @param stylesheet DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public static Document transformDocument(
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

//    LOG.debug("INPUT DOCUMENT: \n" + (document));
//    LOG.debug("TRANSFORM DOCUMENT: \n" + getDOMString(stylesheet));
//    LOG.debug("OUTPUT DOCUMENT: \n" + getDOMString(transformedDoc));

    return transformedDoc;
  }

  public static Document transformDocument(Document document, Transformer transformer)
  {

    LOG.debug("Document transformDocument(Document " + document + ", Trasformer " + transformer);
    Document transformedDoc = createDocument();
    DOMSource docSource = new DOMSource(document);
    DOMResult docResult = new DOMResult(transformedDoc);
    transform(transformer, docSource, docResult);

    return transformedDoc;
  }

  public static void transformNode(Node source, Node result, Transformer transformer)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("transformNode(Node " + source + ", Node " + result + ", Transformer ," + transformer);
    }
    DOMSource domSource = new DOMSource(source);
    DOMResult domResult = new DOMResult(result);
    transform(transformer, domSource, domResult);
  }

  /**
   * Get a textual representation of a Node for debugging purposes
   * @param node The Node
   * @return the document in a text string
   */
  public static String getDOMString(Node node)
  {
    String domString = "";
    int type = node.getNodeType();
    switch (type)
    {
      // print the document element
      case Node.DOCUMENT_NODE:
      {
        domString += "<?xml version=\"1.0\" ?>\n";
        domString += getDOMString(((Document)node).getDocumentElement());
        break;
      }

      // print element with attributes
      case Node.ELEMENT_NODE:
      {
        domString += ("<");
        domString += (node.getNodeName());
        NamedNodeMap attrs = node.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++)
        {
          Node attr = attrs.item(i);
          domString += (" " + attr.getNodeName().trim() +
                        "=\"" + attr.getNodeValue().trim() +
                        "\"");
        }
        domString += (">\n");

        NodeList children = node.getChildNodes();
        if (children != null)
        {
          int len = children.getLength();
          for (int i = 0; i < len; i++)
            domString += getDOMString(children.item(i));
        }
        domString += ("</");
        domString += (node.getNodeName());
        domString += (">\n");

        break;
      }

      // handle entity reference nodes
      case Node.ENTITY_REFERENCE_NODE:
      {
        domString += ("&");
        domString += (node.getNodeName().trim());
        domString += (";");

        break;
      }

      // print cdata sections
      case Node.CDATA_SECTION_NODE:
      {
        domString += ("");
        break;
      }

      // print text
      case Node.TEXT_NODE:
      {
        String val = node.getNodeValue();
        if (val==null) val = "";
        domString += (val.trim());
        break;
      }

      // print processing instruction
      case Node.PROCESSING_INSTRUCTION_NODE:
      {
        domString += ("");
        break;
      }
    }

    if (type == Node.ELEMENT_NODE) {
      domString +=("\n");
    }

    return domString;
  }
}
