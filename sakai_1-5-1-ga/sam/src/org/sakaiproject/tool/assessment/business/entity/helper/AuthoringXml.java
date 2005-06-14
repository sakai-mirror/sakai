package org.sakaiproject.tool.assessment.business.entity.helper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.faces.context.FacesContext;

import org.apache.xerces.dom.CharacterDataImpl;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.TextImpl;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>Utility to load XML templates from Faces context or local file system.</p>
 * <p> </p>
 * <p>Copyright: Copyright (c) 2005 Sakai</p>
 * <p> </p>
 * @author Ed Smiley esmiley@stanford.edu
 * @version $Id: AuthoringXml.java,v 1.11 2005/02/01 19:25:42 esmiley.stanford.edu Exp $
 */

public class AuthoringXml
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(AuthoringXml.class);
  private static boolean dumpLog = false; // use log4j for errors
//  private static boolean dumpLog = true;// bypass log4j

  // paths
  public static final String TEMPLATE_PATH = "/xml/author/";
  public static final String SURVEY_PATH = "survey/";
  //assessment template
  public static final String ASSESSMENT = "assessmentTemplate.xml";
  // section template
  public static final String SECTION = "sectionTemplate.xml";
  // item templates
  public static final String ITEM_AUDIO = "audioRecordingTemplate.xml";
  public static final String ITEM_ESSAY = "essayTemplate.xml";
  public static final String ITEM_FIB = "fibTemplate.xml";
  public static final String ITEM_FILE = "fileUploadTemplate.xml";
  public static final String ITEM_MATCH = "matchTemplate.xml";
  public static final String ITEM_MCMC = "mcMCTemplate.xml";
  public static final String ITEM_MCSC = "mcSCTemplate.xml";
  public static final String ITEM_SURVEY = "mcSurveyTemplate.xml";
  public static final String ITEM_TF = "trueFalseTemplate.xml";
  public static final String ITEM_MATCHING = "matchTemplate.xml";
  public static final String SURVEY_10 = SURVEY_PATH + "10.xml";
  public static final String SURVEY_5 = SURVEY_PATH + "5.xml";
  public static final String SURVEY_AGREE = SURVEY_PATH + "AGREE.xml";
  public static final String SURVEY_AVERAGE = SURVEY_PATH + "AVERAGE.xml";
  public static final String SURVEY_EXCELLENT = SURVEY_PATH +
    "EXCELLENT.xml";
  public static final String SURVEY_STRONGLY = SURVEY_PATH +
    "STRONGLY_AGREE.xml";
  public static final String SURVEY_UNDECIDED = SURVEY_PATH +
    "UNDECIDED.xml";
  public static final String SURVEY_YES = SURVEY_PATH + "YES.xml";

  public Map validTemplates = null;

  public AuthoringXml()
  {
    initTemplates();
  }

  private void initTemplates()
  {
    validTemplates = new HashMap();
    validTemplates.put(ASSESSMENT, "assessmentTemplate.xml");
    validTemplates.put(SECTION, "assessmentTemplate.xml");
    validTemplates.put(ITEM_AUDIO, "audioRecordingTemplate.xml");
    validTemplates.put(ITEM_ESSAY, "essayTemplate.xml");
    validTemplates.put(ITEM_FIB, "fibTemplate.xml");
    validTemplates.put(ITEM_FILE, "fileUploadTemplate.xml");
    validTemplates.put(ITEM_MATCH, "matchTemplate.xml");
    validTemplates.put(ITEM_MCMC, "mcMCTemplate.xml");
    validTemplates.put(ITEM_MCSC, "mcSCTemplate.xml");
    validTemplates.put(ITEM_SURVEY, "mcSurveyTemplate.xml");
    validTemplates.put(ITEM_TF, "trueFalseTemplate.xml");
    validTemplates.put(SURVEY_10, SURVEY_PATH + "10.xml");
    validTemplates.put(SURVEY_5, SURVEY_PATH + "5.xml");
    validTemplates.put(SURVEY_AGREE, SURVEY_PATH + "AGREE.xml");
    validTemplates.put(SURVEY_AVERAGE, SURVEY_PATH + "AVERAGE.xml");
    validTemplates.put(SURVEY_EXCELLENT, SURVEY_PATH + "EXCELLENT.xml");
    validTemplates.put(SURVEY_STRONGLY, SURVEY_PATH + "STRONGLY_AGREE.xml");
    validTemplates.put(SURVEY_UNDECIDED, SURVEY_PATH + "UNDECIDED.xml");
    validTemplates.put(SURVEY_YES, SURVEY_PATH + "YES.xml");
  }

  /**
   * test that a String is a valid template key
   * @param s a key
   * @return true if it is a valid key
   */
  public boolean valid(String s)
  {
    return validTemplates.containsKey(s);
  }

  /**
   * get template as stream using faces context
   * @param templateName
   * @param context
   * @return
   */
  public InputStream getTemplateInputStream(String templateName,
    FacesContext context)
  {
    InputStream is = null;

    try
    {
      if (!this.valid(templateName))
      {
        throw new IllegalArgumentException("not a valid template: " +
          templateName);
      }
      is =
        context.getExternalContext().getResourceAsStream(
        TEMPLATE_PATH + templateName);
    }
    catch (Exception e)
    {
      logError(e.getMessage(), e);
    }
    return is;
  }

  /**
   * get template as stream using local context
   * this presupposes a path of TEMPLATE_PATH off of /
   * this is useful for unit testing
   * @param templateName
   * @return the input stream
   */
  public InputStream getTemplateInputStream(String templateName)
  {
    InputStream is = null;

    try
    {
      if (!this.valid(templateName))
      {
        throw new IllegalArgumentException("not a valid template: " +
          templateName);
      }
      is = new FileInputStream(TEMPLATE_PATH + templateName);
    }
    catch (FileNotFoundException e)
    {
      logError(e.getMessage(), e);
    }
    catch (IOException e1)
    {
      logError(e1.getMessage(), e1);
    }
    catch (Exception e)
    {
      logError(e.getMessage(), e);
    }
    return is;
  }

  /**
   * get a template as a string from its input stream
   * @param templateName
   * @return the xml string
   */
  public String getTemplateAsString(InputStream templateStream)
  {
    InputStreamReader reader;
    String xmlString = null;
    try
    {
      reader = new InputStreamReader(templateStream);
      StringWriter out = new StringWriter();
      int c;

      while ( (c = reader.read()) != -1)
      {
        out.write(c);
      }

      reader.close();
      xmlString = (String) out.toString();
    }
    catch (Exception e)
    {
      logError(e.getMessage(), e);
    }

    return xmlString;

  }

  // this wraps log4j so that you can run standalone unit tests
  private static void logError(Object o, Throwable t)
  {
    if (dumpLog)
    {
      System.out.println("ERROR: " + o + " " + t);
    }
    else
    {
      LOG.error(o, t);
    }
  }

  public boolean isAssessment(String documentType)
  {
    if (ASSESSMENT.equals(documentType))
    {
      return true;
    }
    return false;
  }

  public boolean isSection(String documentType)
  {
    if (SECTION.equals(documentType))
    {
      return true;
    }
    return false;
  }

  public boolean isItem(String documentType)
  {
    if (valid(documentType) && documentType.startsWith("ITEM_"))
    {
      return true;
    }
    return false;
  }

  public boolean isSurveyFragment(String documentType)
  {
    if (valid(documentType) && documentType.startsWith("SURVEY_"))
    {
      return true;
    }
    return false;
  }

  /**
   * Based on method in XmlStringBuffer
   * @author rpembry
   * @author casong changed XmlStringBuffer to be org.w3c.dom compliance,
   * @author Ed Smiley esmiley@stanford.edu changed method signatures used Document
   * @param document Document
   * @param xpath
   * @param element
   * @return modified Document
   */
  public Document update(Document document, String xpath, Element element)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("update(String " + xpath + ", Element " + element + ")");
    }

    List itemResults = this.selectNodes(document, xpath);
    Iterator iterator = itemResults.iterator();
    while (iterator.hasNext())
    {
      Element node = (Element) iterator.next();
      Element replacement =
        (Element) node.getOwnerDocument().importNode(element, true);
      node.getParentNode().replaceChild(replacement, node);
    }

    if (itemResults.size() == 0)
    {
      String parentPath = xpath.substring(0, xpath.lastIndexOf("/"));
      addElement(document, parentPath, element);
    }

    return document;
  }

  /**
   * perform Update on this object
   * Based on method originally in XmlStringBuffer
   * @author rashmi
   * @author casong
   * @author Ed Smiley esmiley@stanford.edu changed method signatures used Document
   * @param document Document
   * @param xpath :- xpath and
   * @param value :-  Value of xpath
   *
   * @return modified Document
   * @throws DOMException DOCUMENTATION PENDING
   * @throws Exception DOCUMENTATION PENDING
   */
  public Document update(Document document, String xpath,
    String value) throws DOMException, Exception
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("update(String " + xpath + ", String " + value + ")");
    }

    try
    {
      Element newElement = null;
      Attr newAttribute = null;
      List newElementList = this.selectNodes(document, xpath);
      int aIndex = xpath.indexOf("@");
      int size = newElementList.size();
      if (size > 1)
      {
        LOG.warn("UPDATING MORE THAN ONE ELEMENT");
      }

      if ( (aIndex == -1) && (size != 0))
      {
        for (int i = 0; i < size; i++)
        {
          newElement = (Element) newElementList.get(i);
          Node childNode = newElement.getFirstChild();

          if (childNode == null)
          {
            CoreDocumentImpl core = new CoreDocumentImpl();
            TextImpl newElementText =
              new TextImpl(core, newElement.getNodeName());
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

      if ( (aIndex != -1) && (size != 0))
      {
        newAttribute = (Attr) newElementList.set(0, newAttribute);
        if (newAttribute != null)
        {
          newAttribute.setValue(value);
        }
      }
    }
    catch (Exception ex)
    {
      LOG.error(ex.getMessage(), ex);
    }

    return document;
  }

  /**
   * Based on method in XmlStringBuffer
   * @author rpembry
   * @author casong changed XmlStringBuffer to be org.w3c.dom compliance,
   * @author Ed Smiley esmiley@stanford.edu changed method signatures used Document
   *
   * @param document Document
   * @param parentXpath
   * @param element
   * @return modified Document
   */
  public Document addElement(Document document, String parentXpath,
    Element element)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug(
        "addElement(String " + parentXpath + ", Element " + element + ")");
    }

    List nodes = this.selectNodes(document, parentXpath);
    Iterator iterator = nodes.iterator();
    while (iterator.hasNext())
    {
      Element parent = (Element) iterator.next();
      if (!parent.equals(element.getOwnerDocument()))
      {
        element = (Element) parent.getOwnerDocument().importNode(element, true);
      }

      parent.insertBefore(element, null);// inserts at end, as before-reference is null
    }
    return document;
  }

  /**
   * Based on method in XmlStringBuffer
   * @author rpembry
   * @author casong changed XmlStringBuffer to be org.w3c.dom compliance,
   * @author Ed Smiley esmiley@stanford.edu changed method signatures used Document
   *
   * @param document Document
   * @param elementXpath
   * @param attributeName
   * @return modified Document
   */
  public Document addAttribute(Document document, String elementXpath,
    String attributeName)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug(
        "addAttribute(String " + elementXpath + ", String" + attributeName +
        ")");
    }

    List nodes = this.selectNodes(document, elementXpath);
    int size = nodes.size();
    for (int i = 0; i < size; i++)
    {
      Element element = (Element) nodes.get(i);
      element.setAttribute(attributeName, "");
    }

    return document;
  }

  /**
   * Based on method in XmlStringBuffer
   * @author rpembry
   * @author casong changed XmlStringBuffer to be org.w3c.dom compliance,
   * @author Ed Smiley esmiley@stanford.edu changed method signatures used Document
   *
   * @return a List of Nodes
   */
  public final List selectNodes(Document document, String xpath)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("selectNodes(String " + xpath + ")");
    }

    List result = new ArrayList();

    try
    {
      XPath path = new DOMXPath(xpath);
      result = path.selectNodes(document);
    }
    catch (JaxenException je)
    {
      LOG.error(je.getMessage(), je);
    }

    return result;
  }

  /**
   * read in XML document from input stream
   * @param inputStream source for XML document
   * @return the Document
   */
  public Document readXMLDocument(InputStream inputStream)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("readDocument(InputStream " + inputStream);
    }

    Document document = null;
    DocumentBuilderFactory builderFactory =
      DocumentBuilderFactory.newInstance();
    builderFactory.setNamespaceAware(true);

    try
    {
      DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
      document = documentBuilder.newDocument();
      document = documentBuilder.parse(inputStream);
    }
    catch (ParserConfigurationException e)
    {
      LOG.error(e.getMessage(), e);
//      System.out.println("oops" + e);
    }
    catch (SAXException e)
    {
      LOG.error(e.getMessage(), e);
//      System.out.println("oops" + e);
    }
    catch (IOException e)
    {
      LOG.error(e.getMessage(), e);
//      System.out.println("oops" + e);
    }

    return document;
  }

  /**
   * Read a DOM Document from xml in a string.
   * @param in The string containing the XML
   * @return A new DOM Document with the xml contents.
   */
  public static Document readDocumentFromString(String in)
  {
    try
    {
      DocumentBuilder docBuilder = null;

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(false);
      docBuilder = dbf.newDocumentBuilder();
      InputSource inputSource = new InputSource(new StringReader(in));
      Document doc = docBuilder.parse(inputSource);
      return doc;
    }
    catch (Exception any)
    {
      LOG.warn("Xml.readDocumentFromString: " + any.toString());
      System.out.println("any=" + any);
//                      any.printStackTrace();
      return null;
    }

  } // readDocumentFromString

}
