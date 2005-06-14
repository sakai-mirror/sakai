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

package org.sakaiproject.tool.assessment.business.entity.helper.item;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;

import javax.faces.context.FacesContext;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import org.sakaiproject.tool.assessment.business.entity.asi.Item;
import org.sakaiproject.tool.assessment.business.entity.helper.AuthoringHelper;
import org.sakaiproject.tool.assessment.business.entity.helper.AuthoringXml;
import org.sakaiproject.tool.assessment.data.ifc.shared.TypeIfc;
import org.sakaiproject.tool.assessment.business.entity.constants.AuthoringConstantStrings;

/**
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organization: Sakai Project</p>
 * @author rshastri
 * @author Ed Smiley esmiley@stanford.edu
 * @version $Id: ItemHelper.java,v 1.20.2.2 2005/02/26 01:35:28 esmiley.stanford.edu Exp $
 */

public class ItemHelper
//  extends Action
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(ItemHelper.class);

  private Document doc;

  private static final long ITEM_AUDIO = TypeIfc.AUDIO_RECORDING.longValue();
  private static final long ITEM_ESSAY = TypeIfc.ESSAY_QUESTION.longValue();
  private static final long ITEM_FILE = TypeIfc.FILE_UPLOAD.longValue();
  private static final long ITEM_FIB = TypeIfc.FILL_IN_BLANK.longValue();
  private static final long ITEM_MCSC = TypeIfc.MULTIPLE_CHOICE.longValue();
  private static final long ITEM_SURVEY = TypeIfc.MULTIPLE_CHOICE_SURVEY.
    longValue();
  private static final long ITEM_MCMC = TypeIfc.MULTIPLE_CORRECT.longValue();
  private static final long ITEM_TF = TypeIfc.TRUE_FALSE.longValue();
  private static final long ITEM_MATCHING = TypeIfc.MATCHING.longValue();
  private String[] itemTypes = AuthoringConstantStrings.itemTypes;

  /**
   *
   */
  public ItemHelper()
  {
    super();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param inputStream DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Item readXMLDocument(
    InputStream inputStream)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("readDocument(InputStream " + inputStream);
    }

    Item itemXml = null;

    try
    {
      AuthoringHelper authoringHelper = new AuthoringHelper();
      itemXml =
        new Item(
        authoringHelper.readXMLDocument(inputStream).getDocument());
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

    return itemXml;
  }

  /**
   * Get XML template for a given item type
   * @param type
   * @return
   */
  public String getTemplateFromType(TypeIfc type)
  {
    String template = "";
    AuthoringXml ax = new AuthoringXml();
    long typeId = ITEM_TF;

    if (type != null)
    {
      typeId = type.getTypeId().longValue();
    }

    if (ITEM_AUDIO == typeId)
    {
      template = ax.ITEM_AUDIO;
    }
    else if (ITEM_ESSAY == typeId)
    {
      template = ax.ITEM_ESSAY;
    }
    else if (ITEM_FILE == typeId)
    {
      template = ax.ITEM_FILE;
    }
    else if (ITEM_FIB == typeId)
    {
      template = ax.ITEM_FIB;
    }
    else if (ITEM_MCSC == typeId)
    {
      template = ax.ITEM_MCSC;
    }
    else if (ITEM_SURVEY == typeId)
    {
      template = ax.ITEM_SURVEY;
    }
    else if (ITEM_MCMC == typeId)
    {
      template = ax.ITEM_MCMC;
    }
    else if (ITEM_TF == typeId)
    {
      template = ax.ITEM_TF;
    }
    else if (ITEM_MATCHING == typeId)
    {
      template = ax.ITEM_MATCHING;
    }

    System.out.println("typeId: " + typeId);
    System.out.println("template: " + template);

    return template;
  }

  /**
 * Get XML template for a given item type
 * @param type
 * @return
 */
public String getTemplateFromScale(String scaleName)
{
  AuthoringXml ax = new AuthoringXml();
  String template = ax.SURVEY_10;//default

  if (scaleName.equals("YESNO"))
  {
    template = ax.SURVEY_YES;
  } else if (scaleName.equals("AGREE"))
  {
    template = ax.SURVEY_AGREE;
  }else if (scaleName.equals("UNDECIDED"))
  {
    template = ax.SURVEY_UNDECIDED;
  }else if (scaleName.equals("AVERAGE"))
  {
    template = ax.SURVEY_AVERAGE;
  }else if (scaleName.equals("STRONGLY_AGREE"))
  {
    template = ax.SURVEY_STRONGLY;
  }else if (scaleName.equals("EXCELLENT"))
  {
    template = ax.SURVEY_EXCELLENT;
  }else if (scaleName.equals("SCALEFIVE"))
  {
    template = ax.SURVEY_5;
  }else if (scaleName.equals("SCALETEN"))
  {
    template = ax.SURVEY_10;
  }

  System.out.println("scale: " + scaleName);
  System.out.println("template: " + template);

  return template;
}


  /**
   * Get item type string which is used for the title of a given item type
   * @param type
   * @return
   */
  public String getItemTypeString(TypeIfc type)
  {
    long typeId = 0;
    if (type != null)
    {
      typeId = type.getTypeId().longValue();
    }

    int itemType = type.getTypeId().intValue();
    if (itemType < 1 || itemType > itemTypes.length)
    {
      itemType = 0;
    }
    return itemTypes[itemType];
  }

  /**
   * Add minimum score to item XML.
   * @param score
   * @param itemXml
   */
  public void addMaxScore(Float score, Item itemXml)
  {
    String xPath = "item/resprocessing/outcomes/decvar/@maxvalue";
    System.out.println("Setting max score in: " + xPath + " to " + score);
    updateItemXml(itemXml, xPath, "" + score.toString());
  }

  /**
   * Add maximum score to item XML
   * @param score
   * @param itemXml
   */
  public void addMinScore(Float score, Item itemXml)
  {
    String xPath = "item/resprocessing/outcomes/decvar/@minvalue";
    System.out.println("Setting min score in: " + xPath + " to 0" );
    updateItemXml(itemXml, xPath, "0");
  }

  /**
   * Flags an answer as correct.
   * @param correctAnswerLabel
   */
  public void addCorrectAnswer(String correctAnswerLabel, Item itemXml)
  {
    if (correctAnswerLabel==null) correctAnswerLabel = "";

    String respProcBaseXPath = "item/resprocessing/respcondition";
    String respProcCondXPath = "/conditionvar/varequal";
    String respProcFeedbackXPath = "/displayfeedback/@linkrefid";

    int respSize = 0;

    // now get each response and flag correct answer
    List resp = itemXml.selectNodes(respProcBaseXPath);

    if ( (resp != null) && (resp.size() > 0))
    {
      respSize = resp.size();
    }

    for (int i = 1; i <= respSize; i++)
    {
      String index = ("[" + i) + "]";
      String answerVar =
        itemXml.selectSingleValue(respProcBaseXPath + index +
        respProcCondXPath, "element");

      if (correctAnswerLabel.equals(answerVar))//found right displayfeedback
      {
        String xPath = respProcBaseXPath + index + respProcFeedbackXPath;
        LOG.info("answerVar correct: " + answerVar);
        LOG.info("setting correct displayfeedback xPath: " + xPath);
        updateItemXml(itemXml, xPath, "Correct");
        break;//done
      }
    }
  }

  /**
   * Add/update a response label entry
   * @param itemXml
   * @param xpath
   * @param itemText
   * @param isInsert
   * @param responseNo
   * @param responseLabelIdent
   */
  public void addResponseEntry(
    Item itemXml, String xpath, String value,
    boolean isInsert, String responseNo, String responseLabel)
  {
    if(isInsert)
    {
      String nextNode = "response_label[" + responseNo + "]";
      itemXml.insertElement(nextNode, xpath, "response_label");
      itemXml.add(
        xpath + "/response_label[" + responseNo + "]", "material/mattext");
    }
    else
    {
      itemXml.add(xpath, "response_label/material/mattext");
    }
    try
    {
      itemXml.update(xpath + "/response_label[" + responseNo +
        "]/material/mattext",
        value);
    }
    catch (Exception ex)
    {
      LOG.error("Cannot update value in addResponselEntry(): " + ex );
    }

    String newPath = xpath + "/response_label[" + responseNo + "]";
    itemXml.addAttribute(newPath, "ident");
    newPath = xpath + "/response_label[" + responseNo + "]/@ident";
    itemXml = updateItemXml(itemXml, newPath, responseLabel);
  }

  /**
   * Get Item Xml for a given item type as a TypeIfc.
   * @param type item type as a TypeIfc
   * @return
   */

  public Item readTypeXMLItem(TypeIfc type)
  {
    AuthoringXml ax = new AuthoringXml();
    ItemHelper itemHelper = new ItemHelper();
    InputStream is;
    String template = itemHelper.getTemplateFromType(type);
    is = ax.getTemplateInputStream(template,
         FacesContext.getCurrentInstance());
    Item itemXml = itemHelper.readXMLDocument(is);
    return itemXml;
  }

  /**
   * Get Item Xml for a given survey item scale name.
   * @param scaleName
   * @return
   */
  public Item readTypeSurveyItem(String scaleName)
  {
    AuthoringXml ax = new AuthoringXml();
    ItemHelper itemHelper = new ItemHelper();
    InputStream is = null;
    String template = itemHelper.getTemplateFromScale(scaleName);
    is = ax.getTemplateInputStream(template,
         FacesContext.getCurrentInstance());
    Item itemXml = itemHelper.readXMLDocument(is);
    return itemXml;

  }



  /**
   * Add Item Feedback for a given response number.
   * @param itemXml the item xml
   * @param responseNo the numnber
   */
  public void addItemfeedback(Item itemXml, String value,
    boolean isInsert, String responseNo, String responseLabel)

  {
    ItemHelper itemHelper = new ItemHelper();
    String xpath = "item";

    String nextNode = "itemfeedback[" + responseNo + "]";
    System.out.println("Next node for feedback: " + nextNode);
    if (isInsert)
    {
      itemXml.insertElement(nextNode, xpath, "itemfeedback");
      System.out.println("Adding feedback: " + xpath +
        "/itemfeedback[" + responseNo + "]" +
        "<==/itemfeedback/flow_mat/material/mattext");
      itemXml.add(
        xpath + "/itemfeedback[" + responseNo + "]",
        "flow_mat/material/mattext");
    }
    else
    {
      System.out.println("Updating feedback: " + xpath +
        "<==/itemfeedback/flow_mat/material/mattext");
//      itemXml.add(xpath, "/itemfeedback/flow_mat/material/mattext");
    }
    try
    {
      if (value==null) value ="";
      itemXml.update(xpath + "/itemfeedback[" + responseNo + "]/flow_mat/material/mattext",
        value);
      System.out.println("updated value in addItemfeedback():\n" +
       xpath + "/itemfeedback[" + responseNo + "]/flow_mat/material/mattext" +
       " gets '" + value + "'");
    }
    catch (Exception ex)
    {
      LOG.error("Cannot update value in addItemfeedback(): " + ex );
    }

    String newPath = xpath + "/itemfeedback[" + responseNo + "]";
    itemXml.addAttribute(newPath, "ident");
    newPath = xpath + "/itemfeedback[" + responseNo + "]/@ident";
    String feedbackIdent = responseLabel;
    itemXml = updateItemXml(itemXml, newPath, feedbackIdent);

  }


  /**
   * DOCUMENTATION PENDING
   *
   * @param itemXml DOCUMENTATION PENDING
   * @param xpath DOCUMENTATION PENDING
   * @param value DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Item updateItemXml(
    Item itemXml, String xpath, String value)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "updateItemXml(Item " + itemXml +
        ", String" + xpath + ", String" + value + ")");
    }

    try
    {
      itemXml.update(xpath, value);
    }
    catch(DOMException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
    }

    return itemXml;
  }

}
