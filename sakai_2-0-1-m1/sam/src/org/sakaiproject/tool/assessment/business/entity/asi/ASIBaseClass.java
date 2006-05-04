/**********************************************************************************
 *
 * $Header: /cvs/sakai2/sam/src/org/sakaiproject/tool/assessment/business/entity/asi/ASIBaseClass.java,v 1.7 2005/05/31 19:14:30 janderse.umich.edu Exp $
 *
 ***********************************************************************************/
/*
 * Copyright (c) 2003, 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.tool.assessment.business.entity.asi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.dom.CharacterDataImpl;
import org.apache.xerces.dom.CommentImpl;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.dom.TextImpl;
import org.sakaiproject.tool.assessment.business.entity.XmlStringBuffer;
import org.sakaiproject.tool.assessment.business.entity.constants.QTIConstantStrings;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organization: Sakai Project</p>
 * @author rshastri
 * @author Ed Smiley esmiley@stanford.edu
 * @version $Id$
 */
public abstract class ASIBaseClass
  extends XmlStringBuffer
{
  private static Log log = LogFactory.getLog(ASIBaseClass.class);
  private static final long serialVersionUID = 5670937321581940933L;
  private String idString;

  /**
   * Creates a new ASIBaseClass object.
   */
  protected ASIBaseClass()
  {
    super();
  }

  /**
   * Creates a new ASIBaseClass object.
   *
   * @param xml XML string
   */
  protected ASIBaseClass(String xml)
  {
    super(xml);
  }

  /**
   * Creates a new ASIBaseClass object.
   *
   * @param document Document
   */
  protected ASIBaseClass(Document document)
  {
    super(document);
  }

  /**
   * extract string for tag
   * @param tagName name of tag
   * @return a String
   * @throws ParserConfigurationException
   * @throws SAXException
   * @throws IOException
   * @throws DOMException
   */
  protected String extractString(String tagName)
    throws ParserConfigurationException, SAXException, IOException,
      DOMException
  {
    if(log.isDebugEnabled())
    {
      log.debug("extractString(String " + tagName + ")");
    }

    String title = null;
    String description = null;
    NodeList nodes = this.getDocument().getElementsByTagName(tagName);
    Element element = (Element) nodes.item(0);
    title = element.getAttribute(QTIConstantStrings.TITLE);
    description = title;

    int size = nodes.getLength();
    for(int i = 0; i < size; i++)
    {
      Element node = (Element) nodes.item(i);
      node.setAttribute(QTIConstantStrings.IDENT, this.getIdString());
    }


    return this.stringValue();
  }


  /**
   * DOCUMENTATION PENDING
   *
   * @param xpath DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  protected String getFieldentry(String xpath)
  {
    if(log.isDebugEnabled())
    {
      log.debug("getFieldentry(String " + xpath + ")");
    }

    String val = null;
    int no = 0;

    List metadataList;
    try
    {
      metadataList = this.selectNodes(xpath);
      no = metadataList.size();

      if(metadataList.size() > 0)
      {
        Document document = this.getDocument();
        Element fieldentry = (Element) metadataList.get(0);
        CharacterData fieldentryText =
          (CharacterData) fieldentry.getFirstChild();

        Integer getTime = null;
        if(
          (fieldentryText != null) && (fieldentryText.getNodeValue() != null) &&
            (fieldentryText.getNodeValue().trim().length() > 0))
        {
          val = fieldentryText.getNodeValue();
        }
      }
    }
    catch(DOMException ex)
    {
      log.error(ex.getMessage(), ex);
    }

    catch(Exception ex)
    {
      log.error(ex.getMessage(), ex);
    }

    return val;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param xpath DOCUMENTATION PENDING
   * @param setValue DOCUMENTATION PENDING
   */
  protected void setFieldentry(String xpath, String setValue)
  {
    if(log.isDebugEnabled())
    {
      log.debug("setFieldentry(String " + xpath + ", String " + setValue + ")");
    }

    List metadataList;
    try
    {
      metadataList = this.selectNodes(xpath);
      int no = metadataList.size();
      String val = null;

      if(metadataList.size() > 0)
      {
        Document document = this.getDocument();
        Element fieldentry = (Element) metadataList.get(0);
        CharacterData fieldentryText =
          (CharacterData) fieldentry.getFirstChild();

        Integer getTime = null;
        if(
          (fieldentryText != null) && (fieldentryText.getNodeValue() != null) &&
            (fieldentryText.getNodeValue().trim().length() > 0))
        {
          val = fieldentryText.getNodeValue();
        }

        if(setValue != null)
        {
          if(fieldentryText == null)
          {
            Text newElementText =
            	fieldentry.getOwnerDocument().createTextNode(setValue);

            fieldentry.appendChild(newElementText);
            fieldentryText = (CharacterData) fieldentry.getFirstChild();
          }
          else
          {
            fieldentryText.setNodeValue(setValue);
          }
        }
      }
    }
    catch(ParserConfigurationException e)
    {
      log.error(e.getMessage(), e);
    }
    catch(SAXException e)
    {
      log.error(e.getMessage(), e);
    }
    catch(IOException e)
    {
      log.error(e.getMessage(), e);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param xpath DOCUMENTATION PENDING
   * @param fieldlabel DOCUMENTATION PENDING
   */
  protected void createFieldentry(String xpath, String fieldlabel)
  {
    if(log.isDebugEnabled())
    {
      log.debug(
        "createFieldentry(String " + xpath + ", String " + fieldlabel + ")");
    }

    try
    {
      List qtimetadataNodes = this.selectNodes(xpath);
      if(qtimetadataNodes.size() > 0)
      {
        Node qtimetadataNode = (Node) qtimetadataNodes.get(0);
        CoreDocumentImpl newDocument = new CoreDocumentImpl();

        Element qtimetadataField =
          new ElementImpl(newDocument, QTIConstantStrings.QTIMETADATAFIELD);
        Element fieldlabelElement =
          new ElementImpl(newDocument, QTIConstantStrings.FIELDLABEL);
        Element fieldentryElement =
          new ElementImpl(newDocument, QTIConstantStrings.FIELDENTRY);

        TextImpl fieldlabelText =
          new TextImpl(newDocument, QTIConstantStrings.FIELDLABEL);
        fieldlabelText.setNodeValue(fieldlabel);
        fieldlabelElement.appendChild(fieldlabelText);

        TextImpl fieldentryText =
          new TextImpl(newDocument, QTIConstantStrings.FIELDENTRY);
        fieldentryElement.appendChild(fieldentryText);

        Node importedFLE =
          qtimetadataField.getOwnerDocument().importNode(
            fieldlabelElement, true);
        Node importedFEE =
          qtimetadataField.getOwnerDocument().importNode(
            fieldentryElement, true);
        qtimetadataField.appendChild(importedFLE);
        qtimetadataField.appendChild(importedFEE);
        Node importedField =
          qtimetadataNode.getOwnerDocument().importNode(qtimetadataField, true);
        qtimetadataNode.appendChild(importedField);
      }
    }
    catch(Exception ex)
    {
      log.error(ex.getMessage(), ex);
    }
  }

  /**
   * Methods shared by Assessment and Section only
   *
   * @param basePath DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  /**
   * DOCUMENTATION PENDING
   *
   * @param basePath DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  protected List getAllSections(String basePath)
  {
    if(log.isDebugEnabled())
    {
      log.debug("getAllSections(String " + basePath + ")");
    }

    String xpath = basePath + "/" + QTIConstantStrings.SECTION;
    List nodes = this.selectNodes(xpath);
    List clonedList = new ArrayList();
    int size = nodes.size();
    for(int i = 0; i < size; i++)
    {
      Node clonedNode = ((Node) nodes.get(i)).cloneNode(true);
      clonedList.add(clonedNode);
    }

    return clonedList;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param basePath DOCUMENTATION PENDING
   */
  protected void removeSections(String basePath)
  {
    if(log.isDebugEnabled())
    {
      log.debug("removeSections(String " + basePath + ")");
    }

    String xpath = basePath + "/" + QTIConstantStrings.SECTION;
    this.removeElement(xpath);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param basePath DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  protected ArrayList selectSections(String basePath)
  {
    if(log.isDebugEnabled())
    {
      log.debug("selectSections(String " + basePath + ")");
      log.debug("After Remove Section: " + this.stringValue());
    }

    ArrayList sections = new ArrayList();
    try
    {
      String xpath =
        basePath + "/" + QTIConstantStrings.SELECTION_ORDERING + "/";
      String selectionXPath = xpath + QTIConstantStrings.SELECTION;

      List selectNodes = this.selectNodes(selectionXPath);

      int selectNodeSize = selectNodes.size();
      for(int i = 0; i < selectNodeSize; i++)
      {
        Element selectElement = (Element) selectNodes.get(i);
        sections.addAll(processSelectElement(basePath, selectElement));
      }

      if(selectNodeSize == 0)
      {
        // no select element, then select all items
        sections.addAll(this.getAllSections(basePath));
      }
    }
    catch(Exception ex)
    {
      log.error(ex.getMessage(), ex);
    }

    removeSections(basePath);

    return sections;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param basePath DOCUMENTATION PENDING
   * @param selectElement DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  protected List processSelectElement(String basePath, Element selectElement)
  {
    if(log.isDebugEnabled())
    {
      log.debug(
        "processSelectElement(String " + basePath + ", Element" +
        selectElement + ")");
    }

    int selectNumber = -1;
    String sourceBankId = null;

    // there is no select number and sourceBank_ref
    // then select all items within this section.
    if((selectNumber == -1) && (sourceBankId == null))
    {
      return getAllSections(basePath);
    }

    // there is select number but no sourceBank_ref
    // then select number of items within this section.
    if((selectNumber > 0) && (sourceBankId == null))
    {
      return getNumOfSections(basePath, selectNumber);
    }

    // We are not supporting object bank for sections at this time.
    return null;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param basePath DOCUMENTATION PENDING
   * @param selectNumber DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  protected List getNumOfSections(String basePath, int selectNumber)
  {
    if(log.isDebugEnabled())
    {
      log.debug(
        "getNumOfSections(String " + basePath + ", int " + selectNumber + ")");
    }

    List list = new ArrayList();
    List clonedList = new ArrayList();
    List allSections = getAllSections(basePath);
    long seed = System.currentTimeMillis();
    int allItemSize = allSections.size();
    Random random = new Random(seed);
    while(list.size() < selectNumber)
    {
      int randomNum = random.nextInt(allItemSize);
      Object item = allSections.get(randomNum);
      if(! list.contains(item))
      {
        list.add(item);
      }
    }

    for(int i = 0; i < selectNumber; i++)
    {
      Node clonedNode = ((Node) list.get(i)).cloneNode(true);
      clonedList.add(clonedNode);
    }

    return clonedList;
  }

//  /**
//   * DOCUMENTATION PENDING
//   *
//   * @param basePath DOCUMENTATION PENDING
//   * @param sections DOCUMENTATION PENDING
//   */
//  protected void orderSections(String basePath, ArrayList sections, int qtiVersion)
//  {
//    if(log.isDebugEnabled())
//    {
//      log.debug(
//        "orderSections(String " + basePath + ", ArrayList " + sections + ")");
//    }
//
//    try
//    {
//      String xpath =
//        basePath + "/" + QTIConstantStrings.SELECTION_ORDERING + "/";
//      String orderingXPath = xpath + QTIConstantStrings.ORDER;
//      List orderNodes = this.selectNodes(orderingXPath);
//      if((orderNodes != null) && (orderNodes.size() > 0))
//      {
//        Element order = (Element) orderNodes.get(0);
//        String orderType = order.getAttribute(QTIConstantStrings.ORDER_TYPE);
//        if("Random".equalsIgnoreCase(orderType))
//        {
//          //Randomly order items.
//          long seed = System.currentTimeMillis();
//          Random rand = new Random(seed);
//          int size = sections.size();
//          for(int i = 0; i < size; i++)
//          {
//            int randomNum = rand.nextInt(size);
//            Object temp = sections.get(i);
//            sections.set(i, sections.get(randomNum));
//            sections.set(randomNum, temp);
//          }
//        }
//      }
//    }
//    catch(Exception ex)
//    {
//      log.error(ex.getMessage(), ex);
//    }
//
//    addSections(basePath, sections, qtiVersion);
//  }

//  /**
//   * DOCUMENTATION PENDING
//   *
//   * @param basePath DOCUMENTATION PENDING
//   * @param sections DOCUMENTATION PENDING
//   */
//  protected void addSections(String basePath, ArrayList sections, int qtiVersion)
//  {
//    if(log.isDebugEnabled())
//    {
//      log.debug(
//        "addSections(String " + basePath + ", ArrayList " + sections + ")");
//    }
//
//    try
//    {
//      String xpath = basePath;
//      for(int i = 0; i < sections.size(); i++)
//      {
//        Element sectionElement = (Element) sections.get(i);
//        Document sectionDoc = XmlUtil.createDocument();
//        Node newNode = sectionDoc.importNode(sectionElement, true);
//        sectionDoc.appendChild(newNode);
//        Section section = new Section(sectionDoc, qtiVersion);
//
//        //Shuffle Section if specified within Item.
//        section.selectAndOrder();
//        sectionElement = (Element) section.getDocument().getFirstChild();
//
//        this.addElement(xpath, sectionElement);
//      }
//    }
//    catch(ParserConfigurationException e)
//    {
//      log.error(e.getMessage(), e);
//    }
//    catch(SAXException e)
//    {
//      log.error(e.getMessage(), e);
//    }
//    catch(IOException e)
//    {
//      log.error(e.getMessage(), e);
//    }
//  }

  protected void wrappingMattext()
  {
    log.debug("wrappingMattext()");

    try
    {
      NodeList list =
        this.getDocument().getElementsByTagName(QTIConstantStrings.MATTEXT);
      int size = list.getLength();
      for(int i = 0; i < size; i++)
      {
        Node node = list.item(i);
        Node childNode = node.getFirstChild();
        if((childNode != null) && childNode instanceof CharacterDataImpl)
        {
          CharacterDataImpl cdi = (CharacterDataImpl) childNode;
          String data = cdi.getData();

          //modify this string;
          CoreDocumentImpl doc = new CoreDocumentImpl();
          Comment comment = new CommentImpl(doc, data);
          node.appendChild(node.getOwnerDocument().importNode(comment, true));
          cdi.setData("");
        }
      }
    }
    catch(ParserConfigurationException e)
    {
      log.error(e.getMessage(), e);
    }
    catch(SAXException e)
    {
      log.error(e.getMessage(), e);
    }
    catch(IOException e)
    {
      log.error(e.getMessage(), e);
    }
  }
  public String getIdString()
  {
    return idString;
  }
  public void setIdString(String idString)
  {
    this.idString = idString;
  }
}
/**********************************************************************************
 *
 * $Header: /cvs/sakai2/sam/src/org/sakaiproject/tool/assessment/business/entity/asi/ASIBaseClass.java,v 1.7 2005/05/31 19:14:30 janderse.umich.edu Exp $
 *
 ***********************************************************************************/