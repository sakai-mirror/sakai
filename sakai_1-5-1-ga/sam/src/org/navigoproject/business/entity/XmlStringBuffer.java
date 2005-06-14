/*
 *                       Navigo Software License
 *
 * Copyright 2003, Trustees of Indiana University, The Regents of the University
 * of Michigan, and Stanford University, all rights reserved.
 *
 * This work, including software, documents, or other related items (the
 * "Software"), is being provided by the copyright holder(s) subject to the
 * terms of the Navigo Software License. By obtaining, using and/or copying this
 * Software, you agree that you have read, understand, and will comply with the
 * following terms and conditions of the Navigo Software License:
 *
 * Permission to use, copy, modify, and distribute this Software and its
 * documentation, with or without modification, for any purpose and without fee
 * or royalty is hereby granted, provided that you include the following on ALL
 * copies of the Software or portions thereof, including modifications or
 * derivatives, that you make:
 *
 *    The full text of the Navigo Software License in a location viewable to
 *    users of the redistributed or derivative work.
 *
 *    Any pre-existing intellectual property disclaimers, notices, or terms and
 *    conditions. If none exist, a short notice similar to the following should
 *    be used within the body of any redistributed or derivative Software:
 *    "Copyright 2003, Trustees of Indiana University, The Regents of the
 *    University of Michigan and Stanford University, all rights reserved."
 *
 *    Notice of any changes or modifications to the Navigo Software, including
 *    the date the changes were made.
 *
 *    Any modified software must be distributed in such as manner as to avoid
 *    any confusion with the original Navigo Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) and/or Indiana University,
 * The University of Michigan, Stanford University, or Navigo may NOT be used
 * in advertising or publicity pertaining to the Software without specific,
 * written prior permission. Title to copyright in the Software and any
 * associated documentation will at all times remain with the copyright holders.
 * The export of software employing encryption technology may require a specific
 * license from the United States Government. It is the responsibility of any
 * person or organization contemplating export to obtain such a license before
 * exporting this Software.
 */

/*
 * Created on Sep 9, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.business.entity;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections.ReferenceMap;

import org.apache.xerces.dom.AttrImpl;
import org.apache.xerces.dom.CharacterDataImpl;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.dom.TextImpl;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

import org.jaxen.JaxenException;
import org.jaxen.XPath;

import org.jaxen.dom.DOMXPath;

import org.jaxen.jdom.JDOMXPath;

import org.jdom.JDOMException;

import org.jdom.input.SAXBuilder;

import org.jdom.output.DOMOutputter;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.xml.sax.SAXException;

/**
 * DOCUMENT ME!
 *
 * @author rpembry
 * @author casong changed XmlStringBuffer to be org.w3c.dom compliance,
 *         deprecated old methods.
 */
public class XmlStringBuffer
  implements java.io.Serializable
{
  private final static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(XmlStringBuffer.class);

  /**
   * Explicitly setting serialVersionUID insures future versions can be
   * successfully restored. It is essential this variable name not be changed
   * to SERIALVERSIONUID, as the default serialization methods expects this
   * exact name.
   */
  private static final long serialVersionUID = 1;

  //The following need not be persisted and so are declared transient
  private transient Document document = null;
  private transient DocumentBuilder builder = null;
  private transient ReferenceMap cache = null;
  private StringBuffer xml;

  /**
   * Constructor to be accessed by subclasses.
   */
  protected XmlStringBuffer()
  {
    this.xml = new StringBuffer();
  }

  /**
   * Constructs an XmlStringBuffer whose initial value is String.
   *
   * @param xml DOCUMENTATION PENDING
   */
  public XmlStringBuffer(String xml)
  {
    this.xml = new StringBuffer(xml);
  }

  /**
   * Constructs an XmlStringBuffer whose initial value is Document
   *
   * @param document DOCUMENTATION PENDING
   */
  public XmlStringBuffer(Document document)
  {
    this.document = document;
  }

  /**
   * Constructs an XmlStringBuffer whose initial value is Document
   *
   * @param jdomDoc
   *
   * @deprecated using XmlStringBuffer(org.w3c.dom.Document document) instead.
   */
  public XmlStringBuffer(org.jdom.Document jdomDoc)
  {
    try
    {
      this.document = new DOMOutputter().output(jdomDoc);
    }
    catch(JDOMException e)
    {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * Clears the xml
   */
  public final void clear()
  {
    this.xml.setLength(0);
    this.reset();
  }

  /**
   * replace the current xml with the given string
   *
   * @param xml DOCUMENTATION PENDING
   *
   * @deprecated
   */
  public final void replace(String xml)
  {
    this.xml = new StringBuffer(xml);
    this.reset();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws ParserConfigurationException DOCUMENTATION PENDING
   * @throws SAXException DOCUMENTATION PENDING
   * @throws IOException DOCUMENTATION PENDING
   */
  public final Document getDocument()
    throws ParserConfigurationException, SAXException, IOException
  {
    if(this.document == null)
    {
      this.parseContent();
    }

    return this.document;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private ReferenceMap getCache()
  {
    if(this.cache == null)
    {
      this.cache = new ReferenceMap();
    }

    return this.cache;
  }

  /**
   * DOCUMENTATION PENDING
   */
  private void clearCache()
  {
    if(this.cache == null)
    {
      this.cache = new ReferenceMap();
    }
    else
    {
      this.cache.clear();
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws JDOMException DOCUMENTATION PENDING
   * @throws IOException DOCUMENTATION PENDING
   */
  private final org.jdom.Document parseContentToJDOM()
    throws JDOMException, IOException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("parseContentToJDOM()");
    }

    String xmlString = this.stringValue();
    org.jdom.Document result = null;
    try
    {
      SAXBuilder saxbuilder = new SAXBuilder();
      result = saxbuilder.build(new StringReader(xmlString));
    }
    catch(JDOMException ex)
    {
      LOG.error("Exception thrown while parsing XML:\n" + ex.getMessage(), ex);
      throw ex;
    }
    catch(IOException ie)
    {
      LOG.error("Exception thrown while parsing XML:\n" + ie.getMessage(), ie);
      throw ie;
    }

    return result;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws ParserConfigurationException DOCUMENTATION PENDING
   * @throws SAXException DOCUMENTATION PENDING
   * @throws IOException DOCUMENTATION PENDING
   */
  private final void parseContent()
    throws ParserConfigurationException, SAXException, IOException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("parseContent()");
    }

    this.clearCache();
    DocumentBuilderFactory dbfi = null;
    DocumentBuilder builder = null;
    StringReader sr = null;
    org.xml.sax.InputSource is = null;
    try
    {
      if(builder == null)
      {
        dbfi = DocumentBuilderFactory.newInstance();
        builder = dbfi.newDocumentBuilder();
        sr = new StringReader(this.xml.toString());
        is = new org.xml.sax.InputSource(sr);
        this.document = builder.parse(is);
      }
    }
    catch(ParserConfigurationException e)
    {
      LOG.error(e.getMessage(), e);
      throw e;
    }
    catch(SAXException e)
    {
      LOG.error(e.getMessage(), e);
      LOG.error("DocumentBuilderFactory dbfi = " + dbfi);
      LOG.error("StringReader sr = " + sr);
      LOG.error("InputSource is = " + is);
      LOG.error("StringBuffer xml = " + this.xml);
      throw e;
    }
    catch(IOException e)
    {
      LOG.error(e.getMessage(), e);
      throw e;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public final String stringValue()
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("stringValue()");
    }

    if(document == null)
    {
      return this.xml.toString();
    }
    else
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
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public final boolean isEmpty()
  {
    return ((this.xml == null) || (this.xml.length() == 0));
  }

  /**
   * DOCUMENTATION PENDING
   */
  private void reset()
  {
    this.document = null;
    this.cache = null;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param xpath DOCUMENTATION PENDING
   * @param type DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String selectSingleValue(String xpath, String type)
  {
    // user passes the if its an element or attribute
    String value = null;
    List list = this.selectNodes(xpath);
    if(list != null)
    {
      int no = list.size();
      try
      {
        if(list.size() > 0)
        {
          Document document = this.getDocument();
          if((type != null) && type.equals("element"))
          {
            ElementImpl element = (ElementImpl) list.get(0);

            CharacterDataImpl elementText =
              (CharacterDataImpl) element.getFirstChild();
            Integer getTime = null;
            if(
              (elementText != null) && (elementText.getNodeValue() != null) &&
                (elementText.getNodeValue().trim().length() > 0))
            {
              value = elementText.getNodeValue();
            }
          }

          if((type != null) && type.equals("attribute"))
          {
            AttrImpl attr = (AttrImpl) list.get(0);

            CharacterDataImpl elementText =
              (CharacterDataImpl) attr.getFirstChild();

            Integer getTime = null;
            if(
              (elementText != null) && (elementText.getNodeValue() != null) &&
                (elementText.getNodeValue().trim().length() > 0))
            {
              value = elementText.getNodeValue();
            }
          }
        }
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
    }

    return value;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param xpath DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public final List selectNodes(String xpath)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("selectNodes(String " + xpath + ")");
    }

    // First try retrieving it from the cache
    List result = null;
    result = (List) this.getCache().get(xpath);

    //  TODO need to invalidate cache when underlying document changes!
    result = null;
    if(result == null)
    {
      try
      {
        XPath path = new DOMXPath(xpath);
        result = path.selectNodes(this.getDocument());
      }
      catch(JaxenException je)
      {
        LOG.error(je.getMessage(), je);
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
    }
    else
    {
      LOG.debug("found in cache");
    }

    return result;
  }

  /**
   * perform Update on this object
   *
   * @param xpath :- xpath and
   * @param value :-  Value of xpath
   *
   * @return XmlStringBuffer
   *
   * @throws DOMException DOCUMENTATION PENDING
   * @throws Exception DOCUMENTATION PENDING
   */

  // Rashmi Aug 19th changed by Pamela on Sept 10th.
  // Rashmi - replacing updateJDOM as on Sep 15 
  public final XmlStringBuffer update(String xpath, String value)
    throws DOMException, Exception
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("update(String " + xpath + ", String " + value + ")");
    }

    try
    {
      Element newElement = null;
      Attr newAttribute = null;
      List newElementList = this.selectNodes(xpath);
      int aIndex = xpath.indexOf("@");
      int size = newElementList.size();
      if(size > 1)
      {
        LOG.warn("UPDATING MORE THAN ONE ELEMENT");
      }

      if((aIndex == -1) && (size != 0))
      {
        for(int i = 0; i < size; i++)
        {
          newElement = (Element) newElementList.get(i);
          Node childNode = newElement.getFirstChild();

          if(childNode == null)
          {
            CoreDocumentImpl document = new CoreDocumentImpl();
            TextImpl newElementText =
              new TextImpl(document, newElement.getNodeName());
            newElementText.setNodeValue(value);
            TextImpl clonedText =
              (TextImpl) newElement.getOwnerDocument().importNode(
                newElementText, true);
            newElement.appendChild(clonedText);
          }
          else
          {
            CharacterDataImpl newElementText =
              (CharacterDataImpl) newElement.getFirstChild();
            newElementText.setNodeValue(value);
          }
        }
      }

      if((aIndex != -1) && (size != 0))
      {
        newAttribute = (Attr) newElementList.set(0, newAttribute);
        if(newAttribute != null)
        {
          newAttribute.setValue(value);
        }
      }
    }
    catch(Exception ex)
    {
      LOG.error(ex.getMessage(), ex);
    }

    return this;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param xpath DOCUMENTATION PENDING
   * @param element DOCUMENTATION PENDING
   */
  public final void update(String xpath, Element element)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("update(String " + xpath + ", Element " + element + ")");
    }

    List itemResults = this.selectNodes(xpath);
    Iterator iterator = itemResults.iterator();
    while(iterator.hasNext())
    {
      Element node = (Element) iterator.next();
      Element replacement =
        (Element) node.getOwnerDocument().importNode(element, true);
      node.getParentNode().replaceChild(replacement, node);
    }

    if(itemResults.size() == 0)
    {
      String parentPath = xpath.substring(0, xpath.lastIndexOf("/"));
      addElement(parentPath, element);
    }
  }

  /**
   * DOCUMENT ME!
   *
   * @param xpath
   * @param element
   *
   * @deprecated addElement(String, org.w3c.dom.Element)
   */
  public final void addJDOMElement(String xpath, org.jdom.Element element)
  {
    try
    {
      List nodes = this.selectNodes(xpath);
      int size = nodes.size();
      for(int i = 0; i < size; i++)
      {
        org.jdom.Element node = (org.jdom.Element) nodes.get(i);
        node.addContent(element);
      }
    }
    catch(Exception ex)
    {
      LOG.error(ex.getMessage(), ex);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param afterNode DOCUMENTATION PENDING
   * @param parentXpath DOCUMENTATION PENDING
   * @param childXpath DOCUMENTATION PENDING
   */
  public void insertElement(
    String afterNode, String parentXpath, String childXpath)
  {
    String nextXpath = parentXpath + "/" + afterNode;

    //*************************************************************
    Element element = null;
    CoreDocumentImpl document = new CoreDocumentImpl();
    element = new ElementImpl(document, childXpath);

    //**************************************************************					
    Element parent = null;
    List parentNodes = this.selectNodes(parentXpath);
    Iterator iteratorNext = parentNodes.iterator();
    while(iteratorNext.hasNext())
    {
      parent = (Element) iteratorNext.next();
    }

    if(parent != null)
    {
      List nodes = this.selectNodes(nextXpath);
      Iterator iterator = nodes.iterator();
      Element nextSibling = null;
      while(iterator.hasNext())
      {
        nextSibling = (Element) iterator.next();
      }

      if(
        (nextSibling != null) &&
          ! nextSibling.equals(element.getOwnerDocument()))
      {
        element = (Element) parent.getOwnerDocument().importNode(element, true);
        parent.insertBefore(element, nextSibling);
      }
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param parentXpath DOCUMENTATION PENDING
   * @param childXpath DOCUMENTATION PENDING
   */
  public final void add(String parentXpath, String childXpath)
  {
    Element childElement = createChildElement(childXpath);
    this.addElement(parentXpath, childElement);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param childXpath DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private final Element createChildElement(String childXpath)
  {
    int index = childXpath.indexOf("/");
    String elementName = childXpath;
    String subChildXpath = null;
    Element element = null;
    Element child = null;
    if(index > 0)
    {
      elementName = childXpath.substring(0, index);
      subChildXpath = childXpath.substring(index + 1);
      child = createChildElement(subChildXpath);
    }

    CoreDocumentImpl document = new CoreDocumentImpl();
    element = new ElementImpl(document, elementName);
    if(child != null)
    {
      Node importedNode = document.importNode(child, true);
      element.appendChild(importedNode);
    }

    return element;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param parentXpath DOCUMENTATION PENDING
   * @param element DOCUMENTATION PENDING
   */
  public final void addElement(String parentXpath, Element element)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "addElement(String " + parentXpath + ", Element " + element + ")");
    }

    List nodes = this.selectNodes(parentXpath);
    Iterator iterator = nodes.iterator();
    while(iterator.hasNext())
    {
      Element parent = (Element) iterator.next();
      if(! parent.equals(element.getOwnerDocument()))
      {
        element = (Element) parent.getOwnerDocument().importNode(element, true);
      }

      parent.appendChild(element);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param elementXpath DOCUMENTATION PENDING
   * @param attributeName DOCUMENTATION PENDING
   */
  public final void addAttribute(String elementXpath, String attributeName)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "addAttribute(String " + elementXpath + ", String" + attributeName +
        ")");
    }

    List nodes = this.selectNodes(elementXpath);
    int size = nodes.size();
    for(int i = 0; i < size; i++)
    {
      Element element = (Element) nodes.get(i);
      element.setAttribute(attributeName, "");
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param xpath DOCUMENTATION PENDING
   */
  public final void removeElement(String xpath)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("removeElement(String " + xpath + ")");
    }

    List nodes = this.selectNodes(xpath);
    Iterator iterator = nodes.iterator();
    while(iterator.hasNext())
    {
      Node node = (Node) iterator.next();
      Node parent = node.getParentNode();
      parent.removeChild(node);
    }
  }

  /**
   * Synchronizes object prior to serialization
   *
   * @param out
   *
   * @throws IOException
   */
  private void writeObject(java.io.ObjectOutputStream out)
    throws IOException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("writeObject(ObjectOutputStream " + out + ")");
    }

    this.xml = new StringBuffer(this.stringValue());
    out.defaultWriteObject();
  }
}
