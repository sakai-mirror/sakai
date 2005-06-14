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
 * Created on Sep 10, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.business.entity;

import org.navigoproject.QTIConstantStrings;
import org.navigoproject.ui.web.smarttext.TextFormat;

import java.io.IOException;
import java.io.StringWriter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xerces.dom.CharacterDataImpl;
import org.apache.xerces.dom.CommentImpl;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.DeferredElementImpl;

import org.jaxen.JaxenException;
import org.jaxen.XPath;

import org.jaxen.dom.DOMXPath;

import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

import osid.dr.Asset;
import osid.dr.DigitalRepositoryException;

import osid.shared.SharedException;

/**
 * DOCUMENT ME!
 *
 * @author casong To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public class Item
  extends ASIBaseClass
{
  private final static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(Item.class);
  private final String basePath = QTIConstantStrings.ITEM;
  private String iconPath = "http://oncourse.iu.edu/images/icons/";

  /**
   * Explicitly setting serialVersionUID insures future versions can be
   * successfully restored. It is essential this variable name not be changed
   * to SERIALVERSIONUID, as the default serialization methods expects this
   * exact name.
   */
  private static final long serialVersionUID = 1;

  /**
   * Creates a new Item object.
   *
   * @param document DOCUMENTATION PENDING
   */
  public Item(Document document)
  {
    super(document);
  }

  /**
   * Creates a new Item object.
   *
   * @param xml DOCUMENTATION PENDING
   */
  public Item(String xml)
  {
    super(xml);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws ParserConfigurationException DOCUMENTATION PENDING
   * @throws SAXException DOCUMENTATION PENDING
   * @throws IOException DOCUMENTATION PENDING
   * @throws DOMException DOCUMENTATION PENDING
   * @throws SharedException DOCUMENTATION PENDING
   * @throws DigitalRepositoryException DOCUMENTATION PENDING
   */
  protected Asset save()
    throws ParserConfigurationException, SAXException, IOException, 
      DOMException, SharedException, DigitalRepositoryException
  {
    LOG.debug("save()");

    Asset asset = super.save(QTIConstantStrings.ITEM);

    return asset;
  }

  /**
   * DOCUMENTATION PENDING
   */
  public void prepareItem()
  {
    LOG.debug("prepareItem()");
    this.shuffleResponse();
//    this.processSmartTextHtml();
  }

  /**
   * DOCUMENTATION PENDING
   */
  public void shuffleResponse()
  {
    LOG.debug("shuffleResponse()");

    try
    {
      NodeList renderChoices;
      renderChoices =
        this.getDocument().getElementsByTagName(
          QTIConstantStrings.RENDER_CHOICE);

      int length = renderChoices.getLength();
      if(length > 0)
      {
        for(int i = 0; i < length; i++)
        {
          Element renderChoice = (Element) renderChoices.item(i);
          processRenderChoice(renderChoice);
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

  /**
   * DOCUMENTATION PENDING
   */
  public void processSmartTextHtml()
  {
    /**
     * PLEASE DO NOT DELETE THIS CODE.
     */


       LOG.debug("processSmartTextHtml()");
       String textFormat = this.getFieldentry("TEXT_FORMAT");
       if(
         "SMART".equalsIgnoreCase(textFormat) ||
           "HTML".equalsIgnoreCase(textFormat))
       {
         this.convertSmartText(textFormat, iconPath);
       }


    /** Do nothing now because all mattext tag is taken care of by wrapMattext() method in assessment level for now. **/
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param renderChoice DOCUMENTATION PENDING
   */
  private void processRenderChoice(Element renderChoice)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("processRenderChoice(Element " + renderChoice + ")");
    }

    try
    {
      String shuffle = renderChoice.getAttribute(QTIConstantStrings.SHUFFLE);
      if((shuffle != null) | shuffle.equalsIgnoreCase("Yes"))
      {
        String parentName = renderChoice.getParentNode().getNodeName();

        //Shuffle the response label within response lid. 
        if(QTIConstantStrings.RESPONSE_LID.equals(parentName))
        {
          XPath xpath = new DOMXPath(QTIConstantStrings.RESPONSE_LABEL);
          List responseLabels = xpath.selectNodes(renderChoice);
          shuffleResponseLabels(renderChoice, responseLabels);
        }

        if(QTIConstantStrings.RESPONSE_GRP.equals(parentName))
        {
          String xpathString =
            QTIConstantStrings.RESPONSE_LABEL + "[@match_group]";
          XPath xpath = new DOMXPath(xpathString);
          List sourceResponseLabels = xpath.selectNodes(renderChoice);
          shuffleResponseLabels(renderChoice, sourceResponseLabels);
        }
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
   * @param parentNode DOCUMENTATION PENDING
   * @param responseLabels DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private List shuffleResponseLabels(Node parentNode, List responseLabels)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("shuffleResponseLabels(List " + responseLabels + ")");
    }
    int size = responseLabels.size();
    long seed = System.currentTimeMillis();
    Random random = new Random(seed);
    for(int i = 0; i < size; i++)
    {
      Node currentNode = (Node) responseLabels.get(i);
      Node tempNode = currentNode.cloneNode(true);
      Node newNode = currentNode.getOwnerDocument().importNode(tempNode, true);
      int randomNum = random.nextInt(size);
      Node randomNode = (Node) responseLabels.get(randomNum);
      Node newRandomNode = (Node) randomNode.cloneNode(true);
      String currentShuffle =
        ((Element) currentNode).getAttribute(QTIConstantStrings.RSHUFFLE);
      String randomShuffle =
        ((Element) randomNode).getAttribute(QTIConstantStrings.RSHUFFLE);

      // Shuffle only if the random node is not the current node,
      // and both currentNode and randomNode are allowed to be shuffled.  
      if(
        (randomNum != i) && ! "No".equalsIgnoreCase(currentShuffle) &&
          ! "No".equalsIgnoreCase(randomShuffle))
      {
      	randomNode.getOwnerDocument().importNode(newRandomNode, true);
        responseLabels.set(i, newRandomNode);
        responseLabels.set(randomNum, tempNode);
      }
    }

    // CoreDocumentImpl cd = new CoreDocumentImpl();

    for(int i = 0; i < size; i++)
    {
      Node newNode = (Node) responseLabels.get(i);
      int index = i + 1;
      XPath xpath;
      try
      {
        xpath =
          new DOMXPath(QTIConstantStrings.RESPONSE_LABEL + "[" + index + "]");
        Node oldNode = (Node) xpath.selectSingleNode(parentNode);
        parentNode.replaceChild(newNode, oldNode);
      }
      catch(JaxenException e)
      {
        LOG.error(e.getMessage(), e);
      }
    }

    return responseLabels;
  }

  /**
   * methods for meta data
   *
   * @param fieldlabel DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getFieldentry(String fieldlabel)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getFieldentry(String " + fieldlabel + ")");
    }

    String xpath =
      "item/itemmetadata/qtimetadata/qtimetadatafield/fieldlabel[text()='" +
      fieldlabel + "']/following-sibling::fieldentry";

    return super.getFieldentry(xpath);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param fieldlabel DOCUMENTATION PENDING
   * @param setValue DOCUMENTATION PENDING
   */
  public void setFieldentry(String fieldlabel, String setValue)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "setFieldentry(String " + fieldlabel + ", String " + setValue + ")");
    }

    String xpath =
      "item/itemmetadata/qtimetadata/qtimetadatafield/fieldlabel[text()='" +
      fieldlabel + "']/following-sibling::fieldentry";
    super.setFieldentry(xpath, setValue);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param fieldlabel DOCUMENTATION PENDING
   */
  public void createFieldentry(String fieldlabel)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("createFieldentry(String " + fieldlabel + ")");
    }

    String xpath = "item/itemmetadata/qtimetadata";
    super.createFieldentry(xpath, fieldlabel);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param textFormat DOCUMENTATION PENDING
   * @param iconPath DOCUMENTATION PENDING
   */
  private void convertSmartText(String textFormat, String iconPath)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "convertSmartText(String " + textFormat + ", String " + iconPath + ")");
    }

    try
    {
      NodeList list =
        this.getDocument().getElementsByTagName(QTIConstantStrings.MATTEXT);
      int size = list.getLength();
      for(int i = 0; i < size; i++)
      {
        Node node = list.item(i);
        if(textFormat.equals("SMART") || "HTML".equals(textFormat))
        {
          Node childNode = node.getFirstChild();
          if((childNode != null) && childNode instanceof CharacterDataImpl)
          {
            CharacterDataImpl cdi = (CharacterDataImpl) childNode;
            String data = cdi.getData();

            //modify this string;
            TextFormat tf = new TextFormat();
            String newData = tf.formatText(data, textFormat, iconPath);
            CoreDocumentImpl doc = new CoreDocumentImpl();
            Comment comment = new CommentImpl(doc, newData);
            node.appendChild(node.getOwnerDocument().importNode(comment, true));
            cdi.setData("");
          }
        }

        //        if(textFormat.equals("HTML"))
        //        {
        //          String data = this.getNodeStringValue(node);
        //          TextFormat tf = new TextFormat();
        //          String newData = tf.formatText(data, textFormat, iconPath);
        //          CoreDocumentImpl doc = new CoreDocumentImpl();
        //          Comment comment = new CommentImpl(doc, newData);
        //          Node newNode = node.cloneNode(false);
        //          newNode.appendChild(
        //            newNode.getOwnerDocument().importNode(comment, true));
        //          node.getParentNode().replaceChild(newNode, node);
        //        }
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

  //  /**
  //   * DOCUMENTATION PENDING
  //   *
  //   * @param node DOCUMENTATION PENDING
  //   *
  //   * @return DOCUMENTATION PENDING
  //   */
  //  public String getNodeStringValue(Node node)
  //  {
  //    String returnValue = "";
  //    try
  //    {
  //      DOMSource source = new DOMSource(node);
  //      TransformerFactory factory = TransformerFactory.newInstance();
  //      Transformer transformer = factory.newTransformer();
  //      StringWriter stringWriter = new StringWriter();
  //      StreamResult result = new StreamResult(stringWriter);
  //      transformer.transform(source, result);
  //      returnValue = stringWriter.toString();
  //    }
  //    catch(TransformerException e)
  //    {
  //      LOG.error(e.getMessage(), e);
  //    }
  //
  //    return returnValue;
  //  }
  public String getItemType()
  {
    String type = this.getFieldentry("qmd_itemtype");

    return type;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getItemText()
  {
    String text = "";
    String xpath = "item/presentation/flow/material/mattext";
    String itemType = this.getItemType();
    if (itemType.equals("Fill In the Blank"))
    {
      xpath = "item/presentation/flow//mattext";
    }
    List nodes = this.selectNodes(xpath);
    Iterator iter = nodes.iterator();
    while (iter.hasNext())
    {
      Node node = (Node) iter.next();
      Node child = node.getFirstChild();
      if((child != null) && child instanceof CharacterDataImpl)
      {
        CharacterDataImpl cdi = (CharacterDataImpl) child;
        text = text + " " + cdi.getData();
      }
    }
    
//    if(nodes.size() > 0)
//    {
//      Node node = (Node)nodes.get(0);
//      Node childNode = node.getFirstChild();
//      if((childNode != null) && childNode instanceof CharacterDataImpl)
//      {
//        CharacterDataImpl cdi = (CharacterDataImpl) childNode;
//        text = cdi.getData();
//      }
//    }
    return text;
  }
}
