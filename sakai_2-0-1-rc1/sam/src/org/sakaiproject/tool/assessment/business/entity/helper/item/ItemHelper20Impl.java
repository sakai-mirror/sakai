/**********************************************************************************
* $HeadURL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2003-2005 The Regents of the University of Michigan, Trustees of Indiana University,
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


package org.sakaiproject.tool.assessment.business.entity.helper.item;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.sakaiproject.tool.assessment.business.entity.asi.Item;
import org.sakaiproject.tool.assessment.business.entity.constants.AuthoringConstantStrings;
import org.sakaiproject.tool.assessment.business.entity.constants.QTIConstantStrings;
import org.sakaiproject.tool.assessment.business.entity.constants.QTIVersion;
import org.sakaiproject.tool.assessment.business.entity.helper.AuthoringXml;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AnswerIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemTextIfc;

/**
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organization: Sakai Project</p>
 * <p>Version for QTI 2.0 item XML, significant differences between 1.2 and 2.0</p>
 * @author Ed Smiley esmiley@stanford.edu
 * @version $Id$
 */

public class ItemHelper20Impl
    extends ItemHelperBase
    implements ItemHelperIfc
{
  private static Log log = LogFactory.getLog(ItemHelper20Impl.class);
  private AuthoringXml authoringXml;

  public ItemHelper20Impl()
  {
    super();
    authoringXml = new AuthoringXml(getQtiVersion());
    System.out.println("ItemHelper20Impl");
  }

  protected AuthoringXml getAuthoringXml()
  {
    return authoringXml;
  }

  /**
   * Add maximum score to item XML.
   * @param score
   * @param itemXml
   */
  public void addMaxScore(Float score, Item itemXml)
  {
    // normalize if null
    if (score == null)
    {
      score = new Float(0);
    }
    // set the responseElse baseValue, if it exists
    String xPath =
        "assessmentItem/responseCondition/responseIf/" +
        "setOutcomeValue/baseValue";
    // test if this is a type that has this baseValue
    List list = itemXml.selectNodes(xPath);
    if (list==null || list.size()==0) return;
    updateItemXml(itemXml, xPath, score.toString());
  }

  /**
   * Add minimum score to item XML
   * @param score
   * @param itemXml
   */
  public void addMinScore(Float score, Item itemXml)
  {
    // normalize if null
    if (score == null)
    {
      score = new Float(0);
    }
    // first, set the outcomeDeclaration defaultValue, if it exists
    String xPath =
        "assessmentItem/responseDeclaration/outcomeDeclaration/defaultValue";
    // test if this is a type that has a defaultValue
    List list = itemXml.selectNodes(xPath);
    if (list==null || list.size()==0) return;
    updateItemXml(itemXml, xPath, score.toString());
    // next, set the responseElse baseValue, if it exists
    xPath =
        "assessmentItem/responseCondition/responseElse/" +
        "setOutcomeValue/baseValue";
    // test if this is a type that has this baseValue
    list = itemXml.selectNodes(xPath);
    if (list==null || list.size()==0) return;
    updateItemXml(itemXml, xPath, score.toString());
  }

  /**
   * Flags an answer as correct.
   * @param correctAnswerLabel
   */
  public void addCorrectAnswer(String correctAnswerLabel, Item itemXml)
  {
    String xPath = "assessmentItem/responseDeclaration/correctResponse/value";
    updateItemXml(itemXml, xPath, correctAnswerLabel);
  }


  /**
   * assessmentItem/qtiMetadata not be permissible in QTI 2.0
   * this this should be used by manifest
   * Get the metadata field entry XPath
   * @return the XPath
   */
  public String getMetaXPath()
  {
    String xpath = "assessmentItem/qtiMetadata";
    return xpath;
  }

  /**
   * assessmentItem/qtiMetadata not be permissible in QTI 2.0
   * this this should be used by manifest
   * Get the metadata field entry XPath for a given label
   * @param fieldlabel
   * @return the XPath
   */
  public String getMetaLabelXPath(String fieldlabel)
  {
    String xpath =
        "assessmentItem/qtiMetadata/qtimetadatafield/fieldlabel[text()='" +
        fieldlabel + "']/following-sibling::fieldentry";
    return xpath;
  }

  /**
   * Get the text for the item
   * @param itemXml
   * @return the text
   */
  public String getText(Item itemXml)
  {
    String xpath = "assessmentItem/itemBody";
    String itemType = itemXml.getItemType();
    if (itemType.equals(AuthoringConstantStrings.MATCHING))
    {
      xpath =
      "assessmentItem/itemBody/matchInteraction/simpleMatchSet/simpleAssociableChoice";
    }

    return makeItemNodeText(itemXml, xpath);
  }


  /**
   * Set the (one or more) item texts.
   * Valid for single and multiple texts.
   * @todo FIB, MATCHING TEXT
   * @param itemXml
   * @param itemText text to be updated
   */
  public void setItemTexts(ArrayList itemTextList, Item itemXml)
  {
    String xPath = "assessmentItem/itemBody";
    if (itemTextList.size() < 1) {
      return;
    }

    if (itemXml.isMatching()) {
//      process matching
//      return;
    }

    String text = ( (ItemTextIfc) itemTextList.get(0)).getText();
    log.debug("item text: " + text);
    if (itemXml.isFIB()) {
//      process fib
//      return;
    }
      try {
        itemXml.update(xPath, text);
      }
      catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }

//  }

  /**
   * get item type string
   * we use title for this for now
   * @param itemXml
   * @return type as string
   */
  public String getItemType(Item itemXml)
  {
    String type = "";
    String xpath = "assessmentItem";
    List list = itemXml.selectNodes(xpath);
    if (list.size() > 0)
    {
      Element element = (Element) list.get(0);
      element.getAttribute(QTIConstantStrings.TITLE);
    }


    return type;
  }

  /**
   * Set the answer texts for item.
   * @param itemTextList the text(s) for item
   */

  public void setAnswers(ArrayList itemTextList, Item itemXml)
  {
    // other types either have no answer or include them in their template
    if (!itemXml.isMatching() && !itemXml.isFIB() &&
        !itemXml.isMCSC() && !itemXml.isMCMC())
    {
      return;
    }
    // OK, so now we are in business.
    String xpath =
        "assessmentItem/itemBody/choiceInteraction/<simpleChoice";

    List list = itemXml.selectNodes(xpath);
    log.debug("xpath size:" + list.size());
    Iterator nodeIter = list.iterator();

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
          this.addCorrectAnswer("" + label, itemXml);
        }
        String value = answer.getText();
        log.debug("answer: " + answer.getText());
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

          this.addIndexedEntry(itemXml, xpath, value,
          isInsert, xpathIndex, "" + label);

        }
        catch (Exception ex)
        {
          log.error("Cannot process source document.", ex);
        }

        label++;
        xpathIndex++;
      }
    }
  }

  /**
   * @todo NEED TO SET CORRECT, INCORRECT, GENERAL FEEDBACK
   * Set the feedback texts for item.
   * @param itemTextList the text(s) for item
   */

  public void setFeedback(ArrayList itemTextList, Item itemXml)
  {
    String xpath = "assessmentItem/itemBody/choiceInteraction/<simpleChoice/feedbackInline";
    // for any answers that are now in the template, create a feedback
    int xpathIndex = 1;
    List list = itemXml.selectNodes(xpath);
    if (list==null) return;

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
        String generalFeedback = itemTextIfc.getItem().getGeneralItemFeedback();
        log.debug("NEED TO SET CORRECT FEEDBACK: " + correctFeedback);
        log.debug("NEED TO SET INCORRECT FEEDBACK: " + incorrectFeedback);
        log.debug("NEED TO SET GENERAL FEEDBACK: " + incorrectFeedback);
        first = false;
      }

      // answer feedback
      answerSet = itemTextIfc.getAnswerSet();
      Iterator aiter = answerSet.iterator();
      while (aiter.hasNext())
      {
        AnswerIfc answer = (AnswerIfc) aiter.next();
        String value = answer.getGeneralAnswerFeedback();
        log.debug("answer feedback: " + answer.getText());
        Node node = null;
        try
        {
          boolean isInsert = true;
          if (nodeIter.hasNext())
          {
            isInsert = false;
          }
          addIndexedEntry(itemXml, xpath, value,
                          isInsert, xpathIndex, null);
        }
        catch (Exception ex)
        {
          log.error("Cannot process source document.", ex);
        }

        label++;
        xpathIndex++;
      }
    }
  }

  /**
   * Add/insert the index-th value.
   * @param itemXml the item xml
   * @param xpath
   * @param value
   * @param isInsert
   * @param index the numnber
   * @param identifier set this attribute if not null)
   */
  private void addIndexedEntry(Item itemXml, String xpath, String value,
    boolean isInsert, int index, String identifier)

  {
    String indexBrackets = "[" + index + "]";
    String thisNode = xpath + indexBrackets;
    String thisNodeIdentity = thisNode + "/@identity";
    if (isInsert)
    {
      log.debug("Adding entry: " + thisNode);
      itemXml.insertElement(thisNode, xpath, "itemfeedback");
    }
    else
    {
      log.debug("Updating entry: " + thisNode);
    }
    try
    {
      if (value==null) value ="";
      itemXml.update(thisNode, value);
      log.debug("updated value in addIndexedEntry()");
    }
    catch (Exception ex)
    {
      log.error("Cannot update value in addIndexedEntry(): " + ex );
    }
  }


  /**
   * get QTI version
   * @return
   */
  protected int getQtiVersion()
  {
    return QTIVersion.VERSION_2_0;
  }

  /**
   * @todo implement this method for 2.0 release
   * @param incorrectAnswerLabel
   * @param itemXml
   */
  public void addIncorrectAnswer(String incorrectAnswerLabel, Item itemXml)
  {
  }

  public void setItemText(String itemText, Item itemXml)
  {//todo
  }
}
/**********************************************************************************
 *
 * $Header: /cvs/sakai2/sam/src/org/sakaiproject/tool/assessment/business/entity/helper/item/ItemHelper20Impl.java,v 1.13 2005/05/21 03:40:53 esmiley.stanford.edu Exp $
 *
 ***********************************************************************************/