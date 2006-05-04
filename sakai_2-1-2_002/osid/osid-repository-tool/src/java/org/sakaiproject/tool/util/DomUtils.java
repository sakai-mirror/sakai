/**********************************************************************************
*
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
*
**********************************************************************************/
package org.sakaiproject.tool.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.xml.serialize.HTMLSerializer;
import org.apache.xml.serialize.Method;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLDocument;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class DomUtils {
	/**
	 * Default encoding (NekoHTML)
	 */
  private static final String ENCODING_OPTION =
      "http://cyberneko.org/html/properties/default-encoding";

  private DomUtils() {
  }

  public final static String INPUT_ENCODING 	= "iso-8859-1";
  public final static String ENCODING 				= "UTF-8";

  /**
   * Create a new element
   * @param document Document to contain the new element
   * @param name the element name
   * @return new Element
   */
  public static Element createElement(Document document, String name) {
    Element element;

    return document.createElement(name);
  }

  /**
   * Add a new element to the given parent
   * @param parent the parent Element
   * @param name the child name
   * @return new Element
   */
  public static Element createElement(Element parent, String name) {
    Document document;
    Element element;

    document = parent.getOwnerDocument();
    element  = document.createElement(name);

    parent.appendChild(element);
    return element;
  }

  /**
   * Add Text object to an Element.
   * @param element the containing element
   * @param text the text to add
   */
  public static void addText(Element element, String text) {
    element.appendChild(element.getOwnerDocument().createTextNode(text));
  }

 /**
   * Add an entity to a specified Element.
   *		(eg <code>DomUtils.addEntity(element, "nbsp");</code>)
   * @param element the containing element
   * @param entity the entity to add
   */
  public static void addEntity(Element element, String entity) {
    element.appendChild(element.getOwnerDocument().createEntityReference(entity));
  }

  /**
   * "Normalize" XML text node content to create a simple string
   * @param original Original text
   * @param update Text to add to the original string
   * @return Concatenated contents (trimmed, pagination characters (\r, \n, etc.)
   *         removed, with a space seperator)
   */
  private static String normalize(String original, String update) {
  	StringBuffer	result;

    if (original == null) {
      return StringUtils.replace(update.trim(), "\\s", " ");
    }

    result = new StringBuffer(original.trim());
    result.append(' ');
    result.append(update.trim());

    return StringUtils.replace(result.toString(), "\\s", " ");
  }

  /**
   * Get the text associated with this element, at this level only
   * @param parent the node containing text
   * @return Text (trimmed of leading/trailing whitespace, null if none)
   */
  public static String getText(Node parent) {
    return textSearch(parent, false);
  }

  /**
   * Get the text associated with this element, at all suboordinate levels
   * @param parent the node containing text
   * @return Text (trimmed of leading/trailing whitespace, null if none)
   */
  public static String getAllTextAtNode(Node parent) {
    return textSearch(parent, true);
  }

  /**
   * Get the text associated with this element at this level only, or
   * recursivley, searching through all child elements
   * @param parent the node containing text
   * @param recursiveSearch Search all chile elements?
   * @return Text (trimmed of leading/trailing whitespace, null if none)
   */
  public static String textSearch(Node parent, boolean recursiveSearch) {
    String text = null;

    if (parent != null) {
      for (Node child = parent.getFirstChild();
                child != null;
                child = child.getNextSibling()) {

        switch (child.getNodeType())
        {
          case Node.TEXT_NODE:
            text = normalize(text, child.getNodeValue());
            break;

          case Node.ELEMENT_NODE:
            if (recursiveSearch) {
              text = normalize(text, getText(child));
            }
            break;

          default:
            break;
        }
      }
    }
    return text == null ? text : text.trim();
  }

  /**
   * Get the first text node associated with this element
   * @param parent the node containing text
   * @return Text (trimmed of leanding/trailing whitespace, null if none)
   */
  public static String getFirstText(Node parent) {
    return getTextNodeByNumber(parent, 1);
  }

  /**
   * Get the specified text node associated with this element
   * @param parent the node containing text
   * @param number The text node to fetch (1st, 2nd, etc)
   * @return Text (trimmed of leanding/trailing whitespace, null if none)
   */
  public static String getTextNodeByNumber(Node parent, int number) {
    String  text  = null;
    int     count = 1;

    if (parent != null) {
      for (Node child = parent.getFirstChild();
                child != null;
                child = child.getNextSibling()) {

        if ((child.getNodeType() == Node.TEXT_NODE) && (count++ == number)) {
          text = child.getNodeValue();
          return text.trim();
        }
      }
    }
    return text;
  }

  /**
   * Get any text associated with this element and it's children.  Null if none.
   * @param parent the node containing text
   * @return Text
   */
  public static String getAllText(Node parent) {
    String text = null;

    if (parent != null) {

      for (Node child = parent.getFirstChild();
                child != null;
                child = child.getNextSibling()) {

        if (child.getNodeType() == Node.TEXT_NODE) {
          text = normalize(text, child.getNodeValue());
          continue;
        }

        if (child.getNodeType() == Node.ELEMENT_NODE) {
          String childText = getText(child);

          if (childText != null) {
            text = normalize(text, childText);
          }
        }
      }
    }
    return text;
  }

  /**
   * Get an Attribute from an Element.  Returns an empty String if none found
   * @param element the containing Element
   * @param name the attribute name
   * @return Attribute as a String
   */
  public static String getAttribute(Element element, String name) {
    return element.getAttribute(name);
  }

  /**
   * Set an Attribute in an Element
   * @param element the containing Element
   * @param name the attribute name
   * @param value the attribute value
   */
  public static void setAttribute(Element element, String name, String value) {
    element.setAttribute(name, value);
  }

  /**
   * Return a list of named Elements.
   * @param element the containing Element
   * @param name the tag name
   * @return NodeList of matching elements
   */
  public static NodeList getElementList(Element element, String name) {
    return element.getElementsByTagName(name);
  }

  /**
   * Return a list of named Elements with a specific attribute value.
   * @param element the containing Element
   * @param name the tag name
   * @param attribute Attribute name
   * @param value Attribute value
   * @return List of matching elements
   */
  public static List selectElementsByAttributeValue(Element element, String name,
  																									String attribute, String value) {
		return selectElementsByAttributeValue(element, name,
																				  attribute, value, false);
	}

  /**
   * Return the first named Element with a specific attribute value.
   * @param element the containing Element
   * @param name the tag name
   * @param attribute Attribute name
   * @param value Attribute value
   * @return The first matching Element (null if none)
   */
  public static Element selectFirstElementByAttributeValue(Element 	element,
  																												 String 	name,
  																												 String 	attribute,
  																											   String 	value) {

		ArrayList	resultList = (ArrayList) selectElementsByAttributeValue(element, name,
																																		  attribute, value,
																																		  true);
		return (resultList.size() == 0) ? null : (Element) resultList.get(0);
	}

  /**
   * Return a list of named Elements with a specific attribute value.
   * @param element the containing Element
   * @param name the tag name
   * @param attribute Attribute name
   * @param value Attribute value
   * @param returnFirst Return only the first matching value?
   * @return List of matching elements
   */
  public static List selectElementsByAttributeValue(Element element, String name,
  																									String attribute, String value,
  																									boolean returnFirst) {
    NodeList 	elementList = element.getElementsByTagName(name);
    List 			resultList	= new ArrayList();

    for (int i = 0; i < elementList.getLength(); i++) {
    	if (getAttribute((Element) elementList.item(i), attribute).equals(value)) {
    		resultList.add(elementList.item(i));
    		if (returnFirst) {
    			break;
    		}
    	}
    }
		return resultList;
  }

  /**
   * Return the first named Element found.  Null if none.
   * @param element the containing Element
   * @param name the tag name
   * @return matching Element (null if none)
   */
  public static Element getElement(Element element, String name) {
    NodeList nodeList = getElementList(element, name);
    return (nodeList.getLength() == 0) ? null : (Element) nodeList.item(0);
  }

  /**
   * Remove this node from its parent.
   * @param node the node to remove
   * @return Node removed
   */
  public Node removeNode(Node node) {
    return node.getParentNode().removeChild(node);
  }

	/**
	 * Search up the tree for a given node
	 * @param currentNode Starting point for our search
	 * @param tagName Node name to look up
	 * @return matching Node (null if none)
	 */
  public static Node getPreviousNodeByName(Node currentNode, String tagName) {
    Node node = currentNode.getParentNode();

    while ((node != null) && (!node.getNodeName().equals(tagName))) {
      node = node.getParentNode();
    }
    return node;
  }

	/**
	 * Search down the tree for a given node
	 * @param currentNode Starting point for our search
	 * @param tagName Node name to look up
	 * @return matching Node (null if none)
	 */
  public static Node getNextNodeByName(Node currentNode, String tagName) {
    Node node = currentNode.getNextSibling();

    while ((node != null) && (!node.getNodeName().equals(tagName))) {
      node = node.getNextSibling();
    }
    return node;
  }

  /**
   * Get a DOM Document builder.
   * @return The DocumentBuilder
   * @throws DomException
   */
  public static DocumentBuilder getXmlDocumentBuilder() throws DomException {
    try {
      DocumentBuilderFactory factory;

      factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(false);

      return factory.newDocumentBuilder();

    } catch (Exception e) {
      throw new DomException(e.toString());
    }
  }

  /**
   * Start a new HTML Document.
   * @return the HTMLDocument
   * @throws DomException
   */
  public static HTMLDocument createHtmlDocument() {
    return new HTMLDocumentImpl();
  }

  /**
   * Start a new XML Document (with root name = xml)
   * @return the Document
   * @throws DomException
   */
  public static Document createXmlDocument() throws DomException {
  	return createXmlDocument("xml");
  }

  /**
   * Start a new XML Document.
   * @param rootName The name of the Document root Element (created here)
   * @return the Document
   * @throws DomException
   */
  public static Document createXmlDocument(String rootName) throws DomException {
    try {
      Document 	document 	= getXmlDocumentBuilder().newDocument();
      Element		root			= document.createElement(rootName);

      document.appendChild(root);
      return document;

    } catch (Exception e) {
      throw new DomException(e.toString());
    }
  }

  /**
   * Parse XML text (from an input stream) into a Document.
   * @param xmlStream The XML text stream
   * @return DOM Document
   * @throws DomException
   */
  public static Document parseXmlStream(InputStream xmlStream)
      																							throws DomException {
    try {
      return getXmlDocumentBuilder().parse(new InputSource(xmlStream));

    } catch (Exception e) {
      throw new DomException(e.toString());
    }
  }

  /**
   * Parse XML text (from a Reader) into a Document.
   * @param xmlReader The XML Reader
   * @return DOM Document
   * @throws DomException
   */
  public static Document parseXmlReader(Reader xmlReader) throws DomException {

    try {
      return getXmlDocumentBuilder().parse(new InputSource(xmlReader));

    } catch (Exception e) {
      throw new DomException(e.toString());
    }
  }

  /**
   * Parse XML text (from a raw byte array) into a Document.
   * @param xml The XML text
   * @return DOM Document
   * @throws DomException
   */
  public static Document parseXmlBytes(byte[] xml) throws DomException {
    return parseXmlStream(new ByteArrayInputStream(xml));
  }

  /**
   * Parse XML text (from a string) into a Document.
   * @param xml The XML text
   * @return DOM Document
   * @throws DomException
   */
  public static Document parseXmlString(String xml) throws DomException {
    return parseXmlStream(new ByteArrayInputStream(xml.getBytes()));
  }

  /**
   * Parse an XML file into a Document.
   * @param filename - The filename to parse
   * @return DOM Document
   * @throws DomException
   */
  public static Document parseXmlFile(String filename) throws DomException {
    try {
      return getXmlDocumentBuilder().parse(filename);
    } catch (Exception exception) {
      throw new DomException(exception.toString());
    }
  }

  /**
   * Set up and configure an HTML DOM parser.  We specifiy a
   * default encoding value to be used when no encoding information
   * is available in the HTML document itself.
   *
   * An appropriate META tag will override this default:
   * <code>
   *   <meta http-equiv="Content-Type" content="text/html; charset=XXXX">
   * </code>
   *
   * @return The parser
   */
  private static org.cyberneko.html.parsers.DOMParser newHtmlDomParser()
                 throws SAXNotRecognizedException, SAXNotSupportedException {
    org.cyberneko.html.parsers.DOMParser domParser;

    domParser = new org.cyberneko.html.parsers.DOMParser();
    domParser.setProperty(ENCODING_OPTION, INPUT_ENCODING);

    return domParser;
  }

  /**
   * Parse HTML from an InputStream
   * @param  stream Input stream
   * @return DOM Document
   * @throws DomException
   */
  public static Document parseHtmlStream(InputStream stream)
                                         throws DomException {
    return parseHtmlFromInputSource(new InputSource(stream));
  }

  /**
   * Parse HTML from a Reader
   * @param reader Reader input
   * @return DOM Document
   * @throws DomException
   */
  public static Document parseHtmlReader(Reader reader) throws DomException {
    return parseHtmlFromInputSource(new InputSource(reader));
  }

  /**
   * Parse HTML from an InputSource
   * @param in InputSource
   * @return DOM Document
   * @throws DomException
   */
  public static Document parseHtmlFromInputSource(InputSource in) throws DomException {
    try {
      org.cyberneko.html.parsers.DOMParser domParser;

      domParser = newHtmlDomParser();
      domParser.parse(in);
      return domParser.getDocument();

    } catch (Exception e) {
      throw new DomException(e.toString());
    }
  }

  /**
   * Parse HTML text (from a raw byte array) into a Document.
   * @param html The HTML text
   * @return DOM Document
   * @throws DomException
   */
  public static Document parseHtmlBytes(byte[] html) throws DomException {
    return parseHtmlStream(new ByteArrayInputStream(html));
  }

  /**
   * Parse HTML text (from a String) into a Document.
   * @param html The HTML text
   * @return DOM Document
   * @throws DomException
   */
  public static Document parseHtmlString(String html) throws DomException {
    return parseHtmlReader(new StringReader(html));
  }

  /**
   * Parse an HTML file
   * @param filename
   * @return DOM Document
   * @throws DomException
   */
  public static Document parseHtmlFile(String filename) throws DomException {
    try {
      org.cyberneko.html.parsers.DOMParser domParser;

      domParser = newHtmlDomParser();
      domParser.parse(filename);
      return domParser.getDocument();

    } catch (Exception e) {
      throw new DomException(e.toString());
    }
  }

  /**
   * Get (and configure) an XML formatter.
   * @return A Xerces OutputFormat object
   */
  public static OutputFormat getXmlFormatter() {
    return getFormatter(Method.XML, ENCODING, true);
  }

  /**
   * Get (and configure) an HTML formatter.
   * @return A Xerces OutputFormat object
   */
  public static OutputFormat getHtmlFormatter() {
    return getFormatter(Method.HTML, ENCODING, true);
  }

  /**
   * Get a DOM formatter.
   * @return A Xerces OutputFormat object
   */
  public static OutputFormat getFormatter(String type, String encoding, boolean doIndent) {
    OutputFormat outputFormat;

    outputFormat = new OutputFormat(type, encoding, doIndent);

    if (doIndent) {
      outputFormat.setPreserveSpace(false);
      outputFormat.setIndent(2);
    }

    if (Method.HTML.equals(type)) {
      outputFormat.setPreserveEmptyAttributes(true);
    }
    return outputFormat;
  }

  /**
   * Write formatted HTML text to supplied OutputStream.
   * @param document the Document to write
   * @param target stream to write to
   * @throws DomException
   */
  public static void serializeHtml(Document document, OutputStream target)
      throws DomException {
    try {
      HTMLSerializer htmlSerial = new HTMLSerializer(target, getHtmlFormatter());
      htmlSerial.asDOMSerializer().serialize(document);

    } catch (Exception e) {
      throw new DomException(e.toString());
    }
  }

  /**
   * Write formatted HTML text to supplied Writer.
   * @param document the Document to write
   * @param target Output Writer
   * @throws DomException
   */
  public static void serializeHtml(Document document, Writer target)
      throws DomException {
    try {
      HTMLSerializer htmlSerial = new HTMLSerializer(target, getHtmlFormatter());
      htmlSerial.asDOMSerializer().serialize(document);

    } catch (Exception e) {
      throw new DomException(e.toString());
    }
  }

  /**
   * Write formatted XML text to supplied OutputStream.
   * @param document the Document to write
   * @param target stream to write to
   * @throws DomException
   */
  public static void serializeXml(Document document, OutputStream target)
      throws DomException {
    try {
      XMLSerializer xmlSerial = new XMLSerializer(target, getXmlFormatter());
      xmlSerial.asDOMSerializer().serialize(document);

    } catch (Exception e) {
      throw new DomException(e.toString());
    }
  }

  /**
   * Write formatted XML text to supplied Writer.
   * @param document the Document to write
   * @param writer Writer the document is written to
   * @throws DomException
   */
  public static void serializeXml(Document document, Writer writer) throws DomException {
    try {
      XMLSerializer xmlSerial = new XMLSerializer(writer, getXmlFormatter());
      xmlSerial.asDOMSerializer().serialize(document);

    } catch (Exception e) {
      throw new DomException(e.toString());
    }
  }

  /**
   * Write formatted XML text to specified file.
   * @param document the Document to write
   * @param filename Filename the document is written to
   * @throws DomException
   */
  public static void serializeXml(Document document, String filename) throws DomException {
    FileOutputStream stream = null;
    Writer writer = null;

    try {
      stream = new FileOutputStream(filename);
      writer = new OutputStreamWriter(stream, ENCODING);

      serializeXml(document, writer);

    } catch (Exception e) {
      throw new DomException(e.toString());

    } finally {
      try { if (writer != null) writer.close(); } catch (Exception ignore) { }
      try { if (stream != null) stream.close(); } catch (Exception ignore) { }
    }
  }

  /**
   * Write formatted HTML or XML text to a String.
   * @param document the Document to write
   * @return String containing the formatted document text
   * @throws DomException
   */
  public static String serialize(Document document) throws DomException {
    ByteArrayOutputStream stream = null;
    Writer writer = null;

    try {
      stream = new ByteArrayOutputStream();
      writer = new OutputStreamWriter(stream, ENCODING);

      if (document instanceof HTMLDocument) {
        serializeHtml(document, writer);
      } else {
        serializeXml(document, writer);
      }
      return stream.toString();

    } catch (Exception e) {
      throw new DomException(e.toString());

    } finally {
      try { if (writer != null) writer.close(); } catch (Exception ignore) { }
      try { if (stream != null) stream.close(); } catch (Exception ignore) { }
    }
  }
}
