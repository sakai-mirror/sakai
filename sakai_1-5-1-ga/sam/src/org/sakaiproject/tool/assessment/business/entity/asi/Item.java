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

package org.sakaiproject.tool.assessment.business.entity.asi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xerces.dom.CharacterDataImpl;
import org.apache.xerces.dom.CommentImpl;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.ElementImpl;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.sakaiproject.tool.assessment.business.entity.constants.AuthoringConstantStrings;
import org.sakaiproject.tool.assessment.business.entity.constants.QTIConstantStrings;
import org.sakaiproject.tool.assessment.business.entity.helper.item.ItemHelper;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AnswerIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemTextIfc;
import org.sakaiproject.tool.assessment.util.TextFormat;

/**
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organization: Sakai Project</p>
 * @author rshastri
 * @author Ed Smiley esmiley@stanford.edu
 * @version $Id: Item.java,v 1.23.2.1 2005/02/23 00:46:18 esmiley.stanford.edu Exp $
 */
public class Item extends ASIBaseClass
{
  private final static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(Item.class);
  private final String basePath = QTIConstantStrings.ITEM;

  /**
   * Explicitly setting serialVersionUID insures future versions can be
     * successfully restored. It is essential this variable name not be changed
     * to SERIALVERSIONUID, as the default serialization methods expects this
   * exact name.
   */
  private static final long serialVersionUID = 1;

  /**
   * Creates a new Item object.
   */
  public Item()
  {
    super();
  }

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
      if (length > 0)
      {
        for (int i = 0; i < length; i++)
        {
          Element renderChoice = (Element) renderChoices.item(i);
          processRenderChoice(renderChoice);
        }
      }
    }
    catch (ParserConfigurationException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch (SAXException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch (IOException e)
    {
      LOG.error(e.getMessage(), e);
    }
  }

  public void setIdent(String ident)
  {
    String xpath = "item";
    List list = this.selectNodes(xpath);
    if (list.size() > 0)
    {
      Element element = (Element) list.get(0);
      element.setAttribute("ident", ident);
    }
  }

  public void setTitle(String title)
  {
    String xpath = "item";
    List list = this.selectNodes(xpath);
    if (list.size() > 0)
    {
      Element element = (Element) list.get(0);
      element.setAttribute("title", title);
    }
  }

  /**
   * Update XML from perisistence
   * @param item
   */
  public void update(ItemDataIfc item)
  {
    // metadata
    setFieldentry("ITEM_OBJECTIVE",
      item.getItemMetaDataByLabel("ITEM_OBJECTIVE"));
    setFieldentry("ITEM_KEYWORD",
      item.getItemMetaDataByLabel("ITEM_KEYWORD"));
    setFieldentry("ITEM_RUBRIC", item.getItemMetaDataByLabel("ITEM_RUBRIC"));
    // item data
    ItemHelper helper = new ItemHelper();
    if (!this.isSurvey()) //surveys are unscored
    {
      helper.addMaxScore(item.getScore(), this);
      helper.addMinScore(item.getScore(), this);
    }
    ArrayList itemTexts = item.getItemTextArraySorted();
    setItemTexts(itemTexts);
    if (!this.isSurvey()) //answers for surveys are a stereotyped scale
    {
      setAnswers(itemTexts);
    }
    setFeedback(itemTexts);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param renderChoice DOCUMENTATION PENDING
   */
  private void processRenderChoice(Element renderChoice)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("processRenderChoice(Element " + renderChoice + ")");
    }

    try
    {
      String shuffle = renderChoice.getAttribute(QTIConstantStrings.
                       SHUFFLE);
      if ( (shuffle != null) | shuffle.equalsIgnoreCase("Yes"))
      {
        String parentName = renderChoice.getParentNode().getNodeName();

        //Shuffle the response label within response lid.
        if (QTIConstantStrings.RESPONSE_LID.equals(parentName))
        {
          XPath xpath = new DOMXPath(QTIConstantStrings.RESPONSE_LABEL);
          List responseLabels = xpath.selectNodes(renderChoice);
          shuffleResponseLabels(renderChoice, responseLabels);
        }

        if (QTIConstantStrings.RESPONSE_GRP.equals(parentName))
        {
          String xpathString =
            QTIConstantStrings.RESPONSE_LABEL + "[@match_group]";
          XPath xpath = new DOMXPath(xpathString);
          List sourceResponseLabels = xpath.selectNodes(renderChoice);
          shuffleResponseLabels(renderChoice, sourceResponseLabels);
        }
      }
    }
    catch (Exception ex)
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
    if (LOG.isDebugEnabled())
    {
      LOG.debug("shuffleResponseLabels(List " + responseLabels + ")");
    }
    int size = responseLabels.size();
    long seed = System.currentTimeMillis();
    Random random = new Random(seed);
    for (int i = 0; i < size; i++)
    {
      Node currentNode = (Node) responseLabels.get(i);
      Node tempNode = currentNode.cloneNode(true);
      Node newNode = currentNode.getOwnerDocument().importNode(tempNode, true);
      int randomNum = random.nextInt(size);
      Node randomNode = (Node) responseLabels.get(randomNum);
      Node newRandomNode = (Node) randomNode.cloneNode(true);
      String currentShuffle =
        ( (Element) currentNode).getAttribute(QTIConstantStrings.RSHUFFLE);
      String randomShuffle =
        ( (Element) randomNode).getAttribute(QTIConstantStrings.RSHUFFLE);

      // Shuffle only if the random node is not the current node,
      // and both currentNode and randomNode are allowed to be shuffled.
      if (
        (randomNum != i) && !"No".equalsIgnoreCase(currentShuffle) &&
        !"No".equalsIgnoreCase(randomShuffle))
      {
        randomNode.getOwnerDocument().importNode(newRandomNode, true);
        responseLabels.set(i, newRandomNode);
        responseLabels.set(randomNum, tempNode);
      }
    }

    // CoreDocumentImpl cd = new CoreDocumentImpl();

    for (int i = 0; i < size; i++)
    {
      Node newNode = (Node) responseLabels.get(i);
      int index = i + 1;
      XPath xpath;
      try
      {
        xpath =
          new DOMXPath(QTIConstantStrings.RESPONSE_LABEL + "[" + index +
          "]");
        Node oldNode = (Node) xpath.selectSingleNode(parentNode);
        parentNode.replaceChild(newNode, oldNode);
      }
      catch (JaxenException e)
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
    if (LOG.isDebugEnabled())
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
    if (LOG.isDebugEnabled())
    {
      LOG.debug(
        "setFieldentry(String " + fieldlabel + ", String " + setValue +
        ")");
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
    if (LOG.isDebugEnabled())
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
    if (LOG.isDebugEnabled())
    {
      LOG.debug(
        "convertSmartText(String " + textFormat + ", String " + iconPath +
        ")");
    }

    try
    {
      NodeList list =
        this.getDocument().getElementsByTagName(QTIConstantStrings.MATTEXT);
      int size = list.getLength();
      for (int i = 0; i < size; i++)
      {
        Node node = list.item(i);
        if (textFormat.equals("SMART") || "HTML".equals(textFormat))
        {
          Node childNode = node.getFirstChild();
          if ( (childNode != null) &&
            childNode instanceof CharacterDataImpl)
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
    catch (ParserConfigurationException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch (SAXException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch (IOException e)
    {
      LOG.error(e.getMessage(), e);
    }
  }

  public String getItemType()
  {
    String type = this.getFieldentry("qmd_itemtype");

    return type;
  }

  /**
   * Set the item texts.
   * Valid for single and multiple texts.
   * @param itemText text to be updated
   */
  public void setItemTexts(ArrayList itemTextList)
  {
    System.out.println("item text size: " + itemTextList.size());
    System.out.println("item text type: " + this.getItemType());
    System.out.println("item is matching: " + isMatching());
    System.out.println("item is fib: " + isMatching());

    if (itemTextList.size() < 1)
    {
      return;
    }

    if (isMatching())
    {
      setItemTextMatching(itemTextList);
      return;
    }

    String text = ( (ItemTextIfc) itemTextList.get(0)).getText();
    System.out.println("item text: " + text);
    if (isFIB())
    {
      setItemTextFIB(text);
      return;
    }
    else
    {
      setItemText(text);
      return;
    }
  }

  private boolean isEssay()
  {
    return AuthoringConstantStrings.ESSAY.equals(this.getItemType()) ? true : false;
  }

  private boolean isSurvey()
  {
    return AuthoringConstantStrings.SURVEY.equals(this.getItemType()) ? true : false;
  }

  private boolean isAudio()
  {
    return AuthoringConstantStrings.AUDIO.equals(this.getItemType()) ? true : false;
  }

  private boolean isFile()
  {
    return AuthoringConstantStrings.FILE.equals(this.getItemType()) ? true : false;
  }

  private boolean isMatching()
  {
    return AuthoringConstantStrings.MATCHING.equals(this.getItemType()) ? true : false;
  }

  private boolean isFIB()
  {
    return AuthoringConstantStrings.FIB.equals(this.getItemType()) ? true : false;
  }

  private boolean isMCMC()
  {
    return AuthoringConstantStrings.MCMC.equals(this.getItemType()) ? true : false;
  }

  private boolean isMCSC()
  {
    return AuthoringConstantStrings.MCSC.equals(this.getItemType()) ? true : false;
  }

  /**
   * Set the answer texts for item.
   * @param itemTextList the text(s) for item
   */
  public void setAnswers(ArrayList itemTextList)
  {

    // other types either have no answer or include them in their template
    if (!isMatching() && !isFIB() && !isMCSC() && !isMCMC())
    {
      return;
    }
    // OK, so now we are in business.
    String xpath =
      "item/presentation/flow/response_lid/render_choice";

    List list = this.selectNodes(xpath);
    System.out.println("xpath size:" + list.size());
    Iterator nodeIter = list.iterator();
    ItemHelper helper = new ItemHelper();

    Iterator iter = itemTextList.iterator();
    Set answerSet = new HashSet();

    char label = 'A';
    int xpathIndex = 1;
    while (iter.hasNext())
    {
      answerSet = ( (ItemTextIfc) iter.next()).getAnswerSet();
      Iterator aiter = answerSet.iterator();
      while (aiter.hasNext())
      {
        AnswerIfc answer = (AnswerIfc) aiter.next();
        if (Boolean.TRUE.equals(answer.getIsCorrect()))
        {
          helper.addCorrectAnswer("" + label, this);
        }
        String value = answer.getText();
        System.out.println("answer: " + answer.getText());
        // process into XML
        // we assume that we have equal to or more than the requisite elements
        // if we have more than the existing elements we manufacture more
        // with labels 'A', 'B'....etc.
        Node node = null;
        try
        {
          boolean isInsert = true;
          if (nodeIter.hasNext())
          {
            isInsert = false;
          }
          helper.addResponseEntry(
            this, xpath, value, isInsert, "" + xpathIndex, "" + label);
        }
        catch (Exception ex)
        {
          LOG.error("Cannot process source document.", ex);
        }

        label++;
        xpathIndex++;
      }
    }
  }

  /**
   * Set the feedback texts for item.
   * @param itemTextList the text(s) for item
   */
  public void setFeedback(ArrayList itemTextList)
  {
    System.out.println("item text size: " + itemTextList.size());
    System.out.println("item answer type: " + this.getItemType());

    // for any answers that are now in the template, create a feedback
    String xpath =
      "item/itemfeedback/flow/response_lid/render_choice";
    int xpathIndex = 1;
    ItemHelper helper = new ItemHelper();


    List list = this.selectNodes(xpath);
    System.out.println("xpath size:" + list.size());
    Iterator nodeIter = list.iterator();

    Iterator iter = itemTextList.iterator();
    Set answerSet = new HashSet();

    char label = 'A';
    boolean first = true;
    while (iter.hasNext())
    {
      ItemTextIfc itemTextIfc = (ItemTextIfc)iter.next();

      if (first) // then do once
      {
        // add in Correct and InCorrect Feedback
        String correctFeedback = itemTextIfc.getItem().getCorrectItemFeedback();
        String incorrectFeedback = itemTextIfc.getItem().getInCorrectItemFeedback();
        System.out.println("CORRECT FEEDBACK: " + correctFeedback);
        if (correctFeedback != null) helper.addItemfeedback(this, correctFeedback,
            false, "1", "" + "Correct");
        System.out.println("INCORRECT FEEDBACK: " + incorrectFeedback);
        if (incorrectFeedback != null) helper.addItemfeedback(this, incorrectFeedback,
            false, "2", "" + "InCorrect");
        xpathIndex = 3;
        first = false;
      }

      // answer feedback
      answerSet = itemTextIfc.getAnswerSet();
      Iterator aiter = answerSet.iterator();
      while (aiter.hasNext())
      {
        AnswerIfc answer = (AnswerIfc) aiter.next();
        String value = answer.getGeneralAnswerFeedback();
        System.out.println("answer feedback: " + answer.getText());
        Node node = null;
        try
        {
          boolean isInsert = true;
          if (nodeIter.hasNext())
          {
            isInsert = false;
          }
          helper.addItemfeedback(this, value, isInsert, "" + xpathIndex,
            "" + label);
        }
        catch (Exception ex)
        {
          LOG.error("Cannot process source document.", ex);
        }

        label++;
        xpathIndex++;
      }
      // finally, add in General Feedback
      String generalFeedback = itemTextIfc.getItem().getGeneralItemFeedback();
      String itemId = itemTextIfc.getItem().getItemIdString();
      if (generalFeedback != null) helper.addItemfeedback(this, generalFeedback,
          true, "" + xpathIndex++, itemId);
    }

  }

  /**
   * Matching only, sets each item to be matched to.
   * @param itemTextList lvalue of matches
   */
  private void setItemTextMatching(ArrayList itemTextList)
  {
    String xpath = "item/presentation/flow/material";
    Iterator iter = itemTextList.iterator();
    CoreDocumentImpl document = new CoreDocumentImpl();

    while (iter.hasNext())
    {
      Element element = new ElementImpl(document,
                        QTIConstantStrings.MATTEXT);
      String text = ( (ItemTextIfc) iter.next()).getText();
      System.out.println("item text: " + text);
      element.setNodeValue(text);
      addElement(xpath, element);
    }

  }

  /**
   * Set the item text.
   * This is only valid for FIB,a single item text separated by '{}'.
   * @param itemText text to be updated, the syntax is in the form:
   * 'roses are {} and violets are {}.' -> 'roses are ',' and violets are ','.'
   */
  private void setItemTextFIB(String itemText)
  {
    String xpath = "item/presentation/flow/flow";
    CoreDocumentImpl document = new CoreDocumentImpl();

    List list = this.selectNodes(xpath);
    System.out.println("xpath size:" + list.size());
    if (list.size() > 0)
    {
      StringTokenizer st = new StringTokenizer(itemText, "{}", false);
      while (st.hasMoreTokens())
      {
        Element element = new ElementImpl(document,
                          QTIConstantStrings.MATERIAL);
        Element subElement = new ElementImpl(document,
                             QTIConstantStrings.MATTEXT);
        element.appendChild(subElement);
        String text = st.nextToken();
        System.out.println("item text: " + text);
        subElement.setNodeValue(text);
        addElement(xpath, element);
      }
    }
  }

  /**
   * Set the item text.
   * This is valid for all undelimited single item texts.
   * Not valid for matching or fill in the blank
   * @param itemText text to be updated
   */
  private void setItemText(String itemText)
  {
    String xpath = "item/presentation/flow/material/mattext";

    List list = this.selectNodes(xpath);
    System.out.println("xpath size:" + list.size());
//    if (list.size() > 0)
//    {
//      Element element = (Element) list.get(0);
    System.out.println("setting element text to: " + itemText);
    try
    {
      update(xpath, itemText);
    }
    catch (Exception ex)
    {
      LOG.error(ex.getMessage(), ex);
    }
//    }
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
    if (itemType.equals(AuthoringConstantStrings.MATCHING))
    {
      xpath = "item/presentation/flow//mattext";
    }
    List nodes = this.selectNodes(xpath);
    Iterator iter = nodes.iterator();
    while (iter.hasNext())
    {
      Node node = (Node) iter.next();
      Node child = node.getFirstChild();
      if ( (child != null) && child instanceof CharacterDataImpl)
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
