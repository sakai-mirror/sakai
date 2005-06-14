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

package org.sakaiproject.tool.assessment.business.entity.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;

import javax.faces.context.FacesContext;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import org.sakaiproject.tool.assessment.business.entity.asi.ASIBaseClass;
import org.sakaiproject.tool.assessment.business.entity.asi.Assessment;
import org.sakaiproject.tool.assessment.business.entity.asi.Item;
import org.sakaiproject.tool.assessment.business.entity.asi.Section;
import org.sakaiproject.tool.assessment.business.entity.constants.
  QTIVersion;
import org.sakaiproject.tool.assessment.facade.AssessmentFacade;
import org.sakaiproject.tool.assessment.facade.ItemFacade;
import org.sakaiproject.tool.assessment.facade.SectionFacade;
import org.sakaiproject.tool.assessment.services.assessment.
  AssessmentService;
import org.sakaiproject.tool.assessment.util.XMLMapper;
import org.sakaiproject.tool.assessment.util.XmlUtil;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemDataIfc;
import org.sakaiproject.tool.assessment.util.Iso8601DateFormat;
import java.util.Calendar;
import java.util.Set;
import java.util.HashSet;
import org.sakaiproject.tool.assessment.data.ifc.assessment.
  ItemFeedbackIfc;
import org.sakaiproject.tool.assessment.data.dao.assessment.ItemFeedback;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemTextIfc;
import org.sakaiproject.tool.assessment.data.dao.assessment.ItemText;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AnswerIfc;
import org.sakaiproject.tool.assessment.data.dao.assessment.Answer;
import org.sakaiproject.tool.assessment.data.ifc.shared.TypeIfc;
import org.sakaiproject.tool.assessment.business.entity.helper.item.
  ItemHelper;
import org.sakaiproject.tool.assessment.business.entity.constants.
  AuthoringConstantStrings;
import org.sakaiproject.tool.assessment.facade.TypeFacade;
import java.util.Iterator;
import org.sakaiproject.tool.assessment.data.ifc.assessment.
  ItemMetaDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.
  AssessmentAccessControlIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.
  EvaluationModelIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.
  AssessmentFeedbackIfc;
import org.sakaiproject.tool.assessment.data.dao.assessment.
  AssessmentAccessControl;
import org.sakaiproject.tool.assessment.data.dao.assessment.
  EvaluationModel;
import org.sakaiproject.tool.assessment.data.dao.assessment.
  AssessmentFeedback;
import java.lang.reflect.*;
import org.navigoproject.business.exception.*;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AnswerFeedbackIfc;
import org.sakaiproject.tool.assessment.data.dao.assessment.AnswerFeedback;

/**
 * <p>Has helper methods for data extraction (import) from QTI</p>
 * <p> </p>
 * <p>Copyright: Copyright (c) 2005 Sakai</p>
 * @author Ed Smiley esmiley@stanford.edu
 * @version $Id: ExtractionHelper.java,v 1.40.2.1 2005/02/23 00:32:00 esmiley.stanford.edu Exp $
 */

public class ExtractionHelper
{
  private static final String QTI_VERSION_1_2_PATH = "v1p2";
  private static final String QTI_VERSION_2_0_PATH = "v2p0";
  private static final String TRANSFORM_PATH =
    "/xml/xsl/dataTransform/import";

  private static final String ASSESSMENT_TRANSFORM =
    "extractAssessment.xsl";
  private static final String SECTION_TRANSFORM = "extractSection.xsl";
  private static final String ITEM_TRANSFORM = "extractItem.xsl";
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(ExtractionHelper.class);

  private int qtiVersion = QTIVersion.VERSION_1_2;
  private String overridePath = null; // override defaults and settings
  private AuthoringXml ax = null;

  private String FIB_BLANK_INDICATOR = " {} ";


  /**
   * @todo muck with version
   */
  public ExtractionHelper()
  {
    ax = new AuthoringXml();
  }

  /**
   * Get ExtractionHelper for QTIVersion.VERSION_1_2
   * or QTIVersion.VERSION_2_0
   * @param qtiVersion
   */
  public ExtractionHelper(int qtiVersion)
  {
    this.setQtiVersion(qtiVersion);
  }

  /**
   * Path to XSL transform code.
   * @return context-relative path to XSL transform code.
   */
  public String getTransformPath()
  {
    // first check to see if normal computed path has been overridden
    if (overridePath != null)
    {
      return overridePath;
    }

    return TRANSFORM_PATH + "/" + getQtiPath();
  }

  private String getQtiPath()
  {
    return qtiVersion == QTIVersion.VERSION_1_2 ? QTI_VERSION_1_2_PATH :
      QTI_VERSION_2_0_PATH;
  }

  /**
   * Get QTI version flag.
   * Either QTIVersion.VERSION_1_2 or QTIVersion.VERSION_2_0;
   * @return QTI version flag
   */
  public int getQtiVersion()
  {
    return qtiVersion;
  }

  /**
   * Set QTI version flag.
   * Either QTIVersion.VERSION_1_2 or QTIVersion.VERSION_2_0;
   * @param qtiVersion
   */
  public void setQtiVersion(int qtiVersion)
  {
    if (!QTIVersion.isValid(qtiVersion))
    {
      throw new IllegalArgumentException("NOT Legal Qti Version.");
    }
    this.qtiVersion = qtiVersion;
  }

  /**
   * Get an XML document for the transform
   * @param template
   * @return
   */
  public Document getTransformDocument(String template)
  {
    Document document = null;

    if (!isOKtransform(template))
    {
      throw new IllegalArgumentException("NOT valid template.");
    }
    String templateContextPath = this.getTransformPath() + "/" + template;
    FacesContext context = FacesContext.getCurrentInstance();
    if (context == null)
    {
      return XmlUtil.readDocument(templateContextPath);
    }
    document = XmlUtil.readDocument(context, templateContextPath);

    return document;
  }

  /**
   * Get map of data to set from assessment XML
   * @param assessmentXml
   * @return a Map
   */
  public Map mapAssessment(Assessment assessmentXml)
  {
    LOG.debug("inside: mapAssessment");
    return map(ASSESSMENT_TRANSFORM, assessmentXml);
  }

  /**
   * Get map of data to set from section XML
   * @param sectionXml
   * @return a Map
   */
  public Map mapSection(Section sectionXml)
  {
    return map(SECTION_TRANSFORM, sectionXml);
  }

  /**
   * Get map of data to set from item XML
   * @param itemXml
   * @return a Map
   */
  public Map mapItem(Item itemXml)
  {
    return map(ITEM_TRANSFORM, itemXml);
  }

  /**
   * Helper method
   * @param transformType ASSESSMENT_TRANSFORM, SECTION_TRANSFORM, ITEM_TRANSFORM
   * @param asi ASIBaseClass: Assessment, Section, or Item XML
   * @return
   */
  private Map map(String transformType, ASIBaseClass asi)
  {
    if (!isOKasi(asi))
    {
      throw new IllegalArgumentException("Incorrect ASI subclass.");
    }
    if (!isOKtransform(transformType))
    {
      throw new IllegalArgumentException("Incorrect transform: " +
        transformType + ".");
    }
    Map map = null;
    try
    {
      Document transform = getTransformDocument(transformType);
      Document xml = asi.getDocument();
      Document model = XmlUtil.transformDocument(xml, transform);
      map = XMLMapper.map(model);
    }
    catch (IOException ex)
    {
      LOG.error(ex);
      ex.printStackTrace(System.out);
    }
    catch (SAXException ex)
    {
      LOG.error(ex);
      ex.printStackTrace(System.out);
    }
    catch (ParserConfigurationException ex)
    {
      LOG.error(ex);
      ex.printStackTrace(System.out);
    }
    return map;

  }

  /**
   * Look up a List of Section XML from Assessment Xml
   * @return a List of Section XML objects
   */
  public List getSectionXmlList(Assessment assessmentXml)
  {
    List nodeList = assessmentXml.selectNodes("//section");
    List sectionXmlList = new ArrayList();

    // now convert our list of Nodes to a list of section xml
    for (int i = 0; i < nodeList.size(); i++)
    {
      try
      {
        Node node = (Node) nodeList.get(i);
        // create a document for a section xml object
        Document sectionDoc = XmlUtil.createDocument();
        // Make a copy for inserting into the new document
        Node importNode = sectionDoc.importNode(node, true);
        // Insert the copy into sectionDoc
        sectionDoc.appendChild(importNode);
        Section sectionXml = new Section(sectionDoc);
        // add the new section xml object to the list
        sectionXmlList.add(sectionXml);
      }
      catch (DOMException ex)
      {
        LOG.error(ex);
        ex.printStackTrace(System.out);
      }
    }
    return sectionXmlList;
  }

  /**
   * Look up a List of Item XML from Section Xml
   * @param Section sectionXml
   * @return a List of Item XML objects
   */
  public List getItemXmlList(Section sectionXml)
  {
    String itemElementName =
      qtiVersion == QTIVersion.VERSION_1_2 ? "//item" : "//assessmentItem";

    // now convert our list of Nodes to a list of section xml
    List nodeList = sectionXml.selectNodes(itemElementName);
    List itemXmlList = new ArrayList();
    for (int i = 0; i < nodeList.size(); i++)
    {
      try
      {
        Node node = (Node) nodeList.get(i);
        // create a document for a item xml object
        Document itemDoc = XmlUtil.createDocument();
        // Make a copy for inserting into the new document
        Node importNode = itemDoc.importNode(node, true);
        // Insert the copy into itemDoc
        itemDoc.appendChild(importNode);
        Item itemXml = new Item(itemDoc);
        // add the new section xml object to the list
        itemXmlList.add(itemXml);
      }
      catch (DOMException ex)
      {
        LOG.error(ex);
        ex.printStackTrace(System.out);
      }
    }
    return itemXmlList;
  }

  /**
   * Used internally.
   * @param transform
   * @return true if OK
   */
  private boolean isOKtransform(String transform)
  {
    return (transform == this.ASSESSMENT_TRANSFORM ||
      transform == this.SECTION_TRANSFORM ||
      transform == this.ITEM_TRANSFORM) ? true : false;
  }

  /**
   * Used internally.
   * @param asi
   * @return true if OK
   */
  private boolean isOKasi(ASIBaseClass asi)
  {
    return (asi instanceof Assessment ||
      asi instanceof Section ||
      asi instanceof Item) ? true : false;
  }

  /**
   * Create assessment from the extracted properties.
   * @param assessmentMap the extracted properties
   * @return an assessment, which has been persisted
   */
  public AssessmentFacade createAssessment(Map assessmentMap)
  {
    String description = (String) assessmentMap.get("description");
    String title = (String) assessmentMap.get("title");
    AssessmentService assessmentService = new AssessmentService();
    AssessmentFacade assessment = assessmentService.createAssessment(
      title, description, null, null);
    return assessment;
  }

  /**
   * Update assessment from the extracted properties.
   * Note: you need to do a save when you are done.
   * @param assessment the assessment, which will  be persisted
   * @param assessmentMap the extracted properties
   */
  public void updateAssessment(AssessmentFacade assessment,
    Map assessmentMap)
  {
    LOG.debug("Updating AssessmentFacade assessment<==Map assessmentMap");
    String title;
    String displayName;
    String comments;
    String status;

    String instructorNotification;
    String testeeNotification;
    String multipartAllowed;
    String createdBy;
    String createdDate;

    // for now, we will not try to obtain these for the imported assessment,
    // but we could enhance import to know about more Sakai-specific properties
//    AssessmentAccessControlIfc assessmentAccessControl = new AssessmentAccessControl();
//    EvaluationModelIfc evaluationModel = new EvaluationModel();
//    AssessmentFeedbackIfc assessmentFeedback= new AssessmentFeedback();
//    Set securedIPAddressSet = new HashSet();
    title = (String) assessmentMap.get("title");
    displayName = (String) assessmentMap.get("title");
    comments = (String) assessmentMap.get("comments");
    // avoid a constraint violation >255
    if (comments != null && comments.length() > 253)
    {
      comments = comments.substring(0, 250) + "...";
    }
    status = (String) assessmentMap.get("status");
    createdBy = (String) assessmentMap.get("createdBy");
    createdDate = (String) assessmentMap.get("createdDate");

    LOG.debug("ASSESSMENT updating metadata information");
    // set meta data
    List metadataList = (List) assessmentMap.get("metadata");
    addMetadata(metadataList, assessment);

    instructorNotification = assessment.getAssessmentMetaDataByLabel(
                             "INSTRUCTOR_NOTIFICATION");
    testeeNotification = assessment.getAssessmentMetaDataByLabel(
                         "TESTEE_NOTIFICATION");
    multipartAllowed = assessment.getAssessmentMetaDataByLabel(
                       "MULTI_PART_ALLOWED");

    LOG.debug("ASSESSMENT updating basic information");
    // set basic properties
    assessment.setCreatedBy(createdBy);
    assessment.setDescription(comments);
    assessment.setComments(comments);
    try
    {
      Iso8601DateFormat iso = new Iso8601DateFormat();
      Calendar cal = iso.parse(createdDate);
      assessment.setCreatedDate(cal.getTime());
    }
    catch (Exception ex)
    {
      assessment.setCreatedDate(new Date());
    }

    assessment.setLastModifiedBy("Sakai Import");
    assessment.setLastModifiedDate(new Date());

    LOG.debug("ASSESSMENT updating notify/allowed information");

    // notification
    if (notNullOrEmpty(instructorNotification))
    {
      assessment.setInstructorNotification(new Integer(
        instructorNotification));
    }
    if (notNullOrEmpty(testeeNotification))
    {
      assessment.setTesteeNotification(new Integer(testeeNotification));

      //multipart
    }
    if (notNullOrEmpty(multipartAllowed))
    {
      assessment.setMultipartAllowed(new Integer(multipartAllowed));

    }
  }

  /**
   * Update section from the extracted properties.
   * Note: you need to do a save when you are done.
   * @param section the section, which will  be persisted
   * @param sectionMap the extracted properties
   */
  public void updateSection(SectionFacade section, Map sectionMap)
  {
    LOG.debug("Updating SectionFacade section<==Map sectionMap");
    section.setTitle( (String) sectionMap.get("title"));
    section.setDescription( (String) sectionMap.get("title"));
    section.setLastModifiedBy("Sakai Import");
    section.setLastModifiedDate(new Date());
  }

  /**
   * Update item from the extracted properties.
   * Note: you need to do a save when you are done.
   * @param item the item, which will  be persisted
   * @param itemMap the extracted properties
   */
  public void updateItem(ItemFacade item, Map itemMap)
  {
    LOG.info("Updating ItemFacade item<==Map itemMap");
    // type and title
    String title = (String) itemMap.get("title");
    item.setDescription(title);
    // type
    // first, we will try to get item type from meta info
    // but otherwise, we will use the other two
    String qmd = item.getItemMetaDataByLabel("qmd_itemtype");
    String itemIntrospect = (String) itemMap.get("itemIntrospect");
    String itemType = calculateType(item, title, itemIntrospect, qmd);
    Long typeId = getType(itemType);// we use this later...
    LOG.debug("SETTING TYPE: ItemFacade item<==Map itemMap");
    LOG.debug("============>item type id: '" + typeId + "'");
    item.setTypeId(typeId);

    // meta data
    List metadataList = (List) itemMap.get("metadata");
    LOG.info("SETTING METADATA: ItemFacade item<==Map itemMap");
    addMetadata(metadataList, item);

    // basic properties
    LOG.debug("SETTING PROPERTIES: ItemFacade item<==Map itemMap");
    addItemProperties(item, itemMap);

    // feedback
    LOG.info("SETTING FEEDBACK: ItemFacade item<==Map itemMap");
    // correct, incorrect, general
    addFeedback(item, itemMap);

    // item text and answers
    LOG.info("SETTING TEXT AND ANSWERS: ItemFacade item<==Map itemMap");
    LOG.info("FILL_IN_BLANK=" + TypeIfc.FILL_IN_BLANK.longValue());
    LOG.info("TYPE=" + typeId.longValue());
    if (TypeIfc.FILL_IN_BLANK.longValue() == typeId.longValue())
    {
      addFibTextAndAnswers(item, itemMap);
    }
    else
    {
      addTextAndAnswers(item, itemMap);
    }

  }

  /**
   *
   * @param item
   * @param title
   * @param itemIntrospect
   * @param qmdType
   * @return
   */
  private String calculateType(ItemFacade item, String title,
    String itemIntrospect,
    String qmdType)
  {
    String itemType = qmdType;

    if (itemType == null)
    {
      if (title != null) // it is also very common to put an item type in the title
      {
        itemType = getTypeFromTitle(title);
      }
      else // OK try to figure out from item structure
      {
        itemType = itemIntrospect;
      }
    }
    return itemType;
  }

  /**
   *
   * @param item
   * @param itemMap
   */
  private void addItemProperties(ItemFacade item, Map itemMap)
  {
    String duration = (String) itemMap.get("duration");
    String triesAllowed = (String) itemMap.get("triesAllowed");
    String score = (String) itemMap.get("score");
    String hasRationale = (String) itemMap.get("hasRationale");
    String status = (String) itemMap.get("status");
    String createdBy = (String) itemMap.get("createdBy");

    // not being set yet
    String instruction = (String) itemMap.get("instruction");
    String hint = (String) itemMap.get("hint");

    // created by is not nullable
    if (createdBy == null)
    {
      createdBy = "Imported by Sakai";
    }

    String createdDate = (String) itemMap.get("createdDate");

    if (notNullOrEmpty(duration))
    {
      item.setDuration(new Integer(duration));
    }
    if (notNullOrEmpty(triesAllowed))
    {
      item.setTriesAllowed(new Integer(triesAllowed));
    }
    item.setInstruction( (String) itemMap.get("instruction"));
    if (notNullOrEmpty(score))
    {
      item.setScore(new Float(score));
    }
    item.setHint( (String) itemMap.get("hint"));
    if (notNullOrEmpty(hasRationale))
    {
      item.setHasRationale(new Boolean(hasRationale));
    }
    if (notNullOrEmpty(status))
    {
      item.setStatus(new Integer(status));
    }
    item.setCreatedBy(createdBy);
    try
    {
      Iso8601DateFormat iso = new Iso8601DateFormat();
      Calendar cal = iso.parse(createdDate);
      item.setCreatedDate(cal.getTime());
    }
    catch (Exception ex)
    {
      item.setCreatedDate(new Date());
    }
    item.setLastModifiedBy("Sakai Import");
    item.setLastModifiedDate(new Date());
  }

  /**
   * add feedback
   * @param item
   * @param itemMap
   */
  private void addFeedback(ItemFacade item, Map itemMap)
  {
    // write the map out
    Iterator iter = itemMap.keySet().iterator();
    while (iter.hasNext()) {
      String key = (String) iter.next();
      Object o = itemMap.get(key);
      System.out.println("itemMap: " + key + "=" + itemMap.get(key));
    }
    LOG.info("calculating item feedback from" + itemMap);
    String correctItemFeedback = (String) itemMap.get(
      "correctItemFeedback");
    String incorrectItemFeedback = (String) itemMap.get(
      "incorrectItemFeedback");
    String generalItemFeedback = (String) itemMap.get(
      "generalItemFeedback");
    LOG.info("correctItemFeedback=" + correctItemFeedback);
    LOG.info("incorrectItemFeedback=" + incorrectItemFeedback);
    LOG.info("generalItemFeedback=" + generalItemFeedback);

    if (notNullOrEmpty(correctItemFeedback))
    {
      LOG.info("setting correctItemFeedback");
      item.setCorrectItemFeedback(correctItemFeedback);
    }
    if (notNullOrEmpty(incorrectItemFeedback))
    {
      LOG.info("setting incorrectItemFeedback");
      item.setInCorrectItemFeedback(incorrectItemFeedback);
    }
    if (notNullOrEmpty(generalItemFeedback))
    {
      LOG.info("setting generalItemFeedback");
      item.setGeneralItemFeedback(generalItemFeedback);
    }

  }

  /**
   * create the answer feedback set for an answer
   * @param item
   * @param itemMap
   */
  private void addAnswerFeedback(Answer answer, String value)
  {
      HashSet answerFeedbackSet = new HashSet();
      answerFeedbackSet.add(new AnswerFeedback(answer,
        AnswerFeedbackIfc.ANSWER_FEEDBACK, value));
      answer.setAnswerFeedbackSet(answerFeedbackSet);
  }

  /**
   * matching only: create the answer feedback set for an answer
   * @param answer
   * @param correct
   * @param incorrect
   */
  private void addMatchingAnswerFeedback(Answer answer,
    String correct, String incorrect)
  {
      HashSet answerFeedbackSet = new HashSet();
      answerFeedbackSet.add(new AnswerFeedback(answer,
        AnswerFeedbackIfc.CORRECT_FEEDBACK, correct));
      answerFeedbackSet.add(new AnswerFeedback(answer,
        AnswerFeedbackIfc.INCORRECT_FEEDBACK, incorrect));
  }

  /**
   * @param item
   * @param itemMap
   */
  private void addTextAndAnswers(ItemFacade item, Map itemMap)
  {
    List itemTextList = (List) itemMap.get("itemText");
    HashSet itemTextSet = new HashSet();
    for (int i = 0; i < itemTextList.size(); i++)
    {
      ItemText itemText = new ItemText();
      String text = (String) itemTextList.get(i);
      // should be allow this or, continue??
      // for now, empty string OK, setting to empty string if null
      if (text == null)
      {
        text = "";
      }
      itemText.setText(text);
      itemText.setItem(item.getData());
      itemText.setSequence(new Long(i + 1));
      List answerList = new ArrayList();
      List aList = (List) itemMap.get("itemAnswer");
      answerList = aList == null ? answerList : aList;
      HashSet answerSet = new HashSet();
      char answerLabel = 'A';
      List answerFeedbackList = (List) itemMap.get("itemFeedback");

      String correctLabel = (String) itemMap.get("itemAnswerCorrectLabel");
      for (int a = 0; a < answerList.size(); a++)
      {
        Answer answer = new Answer();
        String answerText = (String) answerList.get(a);
        LOG.debug("EXTRACT ANSWER DEBUG: " + answerText);
        // these are not supposed to be empty
        if (notNullOrEmpty(answerText))
        {
          String label = "" + answerLabel++;
          answer.setLabel(label); // up to 26, is this a problem?
          // if label matches correct answer it is correct
          if (label.equals(correctLabel))
          {
            answer.setIsCorrect(new Boolean(true));
          }
          else
          {
            answer.setIsCorrect(new Boolean(false));
          }
          answer.setText(answerText);
          answer.setItemText(itemText);
          answer.setItem(item.getData());
          int sequence = a+1;
          answer.setSequence(new Long(sequence));
          // prepare answer feedback - daisyf added this on 2/21/05
          // need to check if this works for question type other than
          // MC
          HashSet set = new HashSet();
          if (answerFeedbackList !=null) {
            LOG.info("extracting answer: " + a);
            AnswerFeedback answerFeedback = new AnswerFeedback();
            answerFeedback.setAnswer(answer);
            answerFeedback.setTypeId(AnswerFeedbackIfc.GENERAL_FEEDBACK);
            if (answerFeedbackList.get(sequence -1) != null){
              answerFeedback.setText( (String) answerFeedbackList.get(sequence -
                  1));
              set.add(answerFeedback);
              answer.setAnswerFeedbackSet(set);
            }
          }

          answerSet.add(answer);
        }
      }
      itemText.setAnswerSet(answerSet);
      itemTextSet.add(itemText);
    }
    item.setItemTextSet(itemTextSet);
  }

  /**
 * FIB questions ONLY
 * @param item
 * @param itemMap
 */
private void addFibTextAndAnswers(ItemFacade item, Map itemMap)
{
  List itemTextList = (List) itemMap.get("itemFibText");
  HashSet itemTextSet = new HashSet();
  ItemText itemText = new ItemText();
  String itemTextString = "";
  List answerFeedbackList = (List) itemMap.get("itemFeedback");

  List answerList = new ArrayList();
  List aList = (List) itemMap.get("itemFibAnswer");
  answerList = aList == null ? answerList : aList;
  // loop through all our extracted FIB texts interposing FIB_BLANK_INDICATOR
  for (int i = 0; i < itemTextList.size(); i++)
  {
    String text = (String) itemTextList.get(i);
    LOG.info("text=" + text);
    // should be allow this or, continue??
    // for now, empty string OK, setting to empty string if null
    if (text == null)
    {
      text = "";
    }
    itemTextString += text;
    if (i <answerList.size())
    {
      itemTextString += FIB_BLANK_INDICATOR;
    }
  }
  LOG.info("complete text=" + itemTextString);
  itemText.setText(itemTextString);
  itemText.setItem(item.getData());
  itemText.setSequence(new Long(0));
  HashSet answerSet = new HashSet();
  char answerLabel = 'A';

  String correctLabel = (String) itemMap.get("itemAnswerCorrectLabel");
  LOG.info("calculating FIB " + answerList.size() + " answers");
  for (int a = 0; a < answerList.size(); a++)
  {
    Answer answer = new Answer();
    String answerText = (String) answerList.get(a);
    LOG.debug("EXTRACT ANSWER: " + answerText);
    // these are not supposed to be empty
    if (notNullOrEmpty(answerText))
    {
      String label = "" + answerLabel++;
      answer.setLabel(label); // up to 26, is this a problem?
      answer.setIsCorrect(new Boolean(true));
      answer.setText(answerText);
      answer.setItemText(itemText);
      answer.setItem(item.getData());
      int sequence = a+1;
      answer.setSequence(new Long(sequence));
      HashSet set = new HashSet();
      if (answerFeedbackList !=null) {
        LOG.info("extracting answer feedback: " + a);
        AnswerFeedback answerFeedback = new AnswerFeedback();
        answerFeedback.setAnswer(answer);
        answerFeedback.setTypeId(AnswerFeedbackIfc.GENERAL_FEEDBACK);
        if (answerFeedbackList.get(sequence - 1) != null)
        {
          answerFeedback.setText( (String) answerFeedbackList.get(
            sequence - 1));
          set.add(answerFeedback);
          answer.setAnswerFeedbackSet(set);
        }
      }
      answerSet.add(answer);
    }
  }

  LOG.debug("SET ANSWER SET");
  itemText.setAnswerSet(answerSet);
  LOG.debug("ADD ITEM TEXT");
  itemTextSet.add(itemText);
  LOG.debug("SET ITEM TEXT SET");
  item.setItemTextSet(itemTextSet);
}


  /**
   * Try to infer the type of imported question from title.
   * @param title
   * @return AuthoringConstantStrings.{type}
   */
  private String getTypeFromTitle(String title)
  {
    String itemType;
    String lower = title.toLowerCase();
    itemType = AuthoringConstantStrings.MCSC;
    if (lower.indexOf("multiple") != -1 &&
      lower.indexOf("response") != -1)
    {
      itemType = AuthoringConstantStrings.MCMC;
    }
    else if (lower.indexOf("true") != -1 ||
      lower.indexOf("tf") != -1)
    {
      itemType = AuthoringConstantStrings.TF;
    }
    else if (lower.indexOf("multiple") != -1 &&
      lower.indexOf("correct") != -1)
    {
      itemType = AuthoringConstantStrings.MCMC;
    }
    else if (lower.indexOf("audio") != -1 ||
      lower.indexOf("recording") != -1)
    {
      itemType = AuthoringConstantStrings.AUDIO;
    }
    else if (lower.indexOf("file") != -1 ||
      lower.indexOf("upload") != -1)
    {
      itemType = AuthoringConstantStrings.FILE;
    }
    else if (lower.indexOf("match") != -1)
    {
      itemType = AuthoringConstantStrings.MATCHING;
    }
    else if (lower.indexOf("fib") != -1 ||
      lower.indexOf("fill") != -1 ||
      lower.indexOf("f.i.b.") != -1
      )
    {
      itemType = AuthoringConstantStrings.FIB;
    }
    else if (lower.indexOf("essay") != -1 ||
      lower.indexOf("short") != -1)
    {
      itemType = AuthoringConstantStrings.ESSAY;
    }

    return itemType;
  }

  /**
   * We use the QTI qmd_itemtype in itemmetadata
   * Note, this may be deprecated in favor of a vocabulary based approach.
   * Need to investigate.  Anyway, this is the form that is backwardly compatible.
   *
   * @todo take hardcode conversion of file upload out for Samigo 2.0!
   *
   * @param metadataMap
   * @return
   */
  private Long getType(String qmd_itemtype)
  {
    //////////////////////////////////////////////////////
    // SAMIGO 1.5 ONLY, FILE UPLOAD IS NOT AVAILABLE
    // FORCE CONVERSION TO ESSAY, TAKE THIS OUT LATER!!!
    //////////////////////////////////////////////////////
    if (AuthoringConstantStrings.FILE.equals(qmd_itemtype))
    {
      qmd_itemtype = AuthoringConstantStrings.ESSAY;
    }
    //////////////////////////////////////////////////////

    String[] typeArray = AuthoringConstantStrings.itemTypes;
    for (int i = 0; i < typeArray.length; i++)
    {
      LOG.debug("Testing type: Is '" + qmd_itemtype.trim() + "'='" +
        typeArray[i] + "'?...");
      if (qmd_itemtype.trim().equalsIgnoreCase(typeArray[i]))
      {
        return new Long(i);
      }
    }

    LOG.warn("Unable to set item type: '" + qmd_itemtype + "'.");
    LOG.warn("guessing item type: '" + typeArray[2] + "'.");
    return new Long(2);
  }

  /**
   * helper method
   * @param s
   * @return
   */
  private boolean notNullOrEmpty(String s)
  {
    return s != null && s.trim().length() > 0 ?
      true : false;
  }

  /**
   * Primarily for testing purposes.
   * @return an overridden path if not null
   */
  public String getOverridePath()
  {
    return overridePath;
  }

  /**
   * Primarily for testing purposes.
   * @param overridePath an overriding path
   */
  public void setOverridePath(String overridePath)
  {
    this.overridePath = overridePath;
  }

  /**
   * Adds extraction-created list of "|" key value pairs
   * to item metadata map, if there are any.
   * Example:<br/>
   * <p> 
   * &lt; metadata type =" list " &gt; TEXT_FORMAT| HTML &lt;/ metadata &gt;<br/>  
     * &lt; metadata type =" list " &gt; ITEM_OBJECTIVE| &lt/ metadata &gt;<br/>
   * Becomes:<br/>
   * TEXT_FORMAT=>HTML etc.
   * </p>
   * @param metadataList extraction-created list of "|" key value pairs
   * @param item the item
   */
  private void addMetadata(List metadataList, ItemDataIfc item)
  {
    if (metadataList == null)
    {
      return; // no metadata found
    }

    for (int i = 0; i < metadataList.size(); i++)
    {
      String meta = (String) metadataList.get(i);
      StringTokenizer st = new StringTokenizer(meta, "|");
      String key = null;
      String value = null;
      if (st.hasMoreTokens())
      {
        key = st.nextToken().trim();
      }
      if (st.hasMoreTokens())
      {
        value = st.nextToken().trim();
        LOG.debug("==>ITEM METADATA key ='" + key + "'.");
        LOG.debug("==>ITEM METADATA value length =" + value.length() + ".");
        item.addItemMetaData(key, value);
      }
    }
  }

  /**
   * Adds extraction-created list of "|" key value pairs
   * to assessment metadata map, if there are any.
   * Example:<br/>
   * <p> 
   *
   * &lt; metadata type =" list " &gt; FEEDBACK_SHOW_CORRECT_RESPONSE|True &lt;/ metadata &gt;<br/>  
   * &lt; metadata type =" list " &gt; FEEDBACK_SHOW_STUDENT_SCORE|True &lt/ metadata &gt;<br/>
   * Becomes:<br/>
   * TEXT_FORMAT=>HTML etc.
   * </p>
   * @param metadataList extraction-created list of "|" key value pairs
   * @param assessment the assessment
   */
  private void addMetadata(List metadataList, AssessmentFacade assessment)
  {
    if (metadataList == null)
    {
      return; // no metadata found
    }

    for (int i = 0; i < metadataList.size(); i++)
    {
      String meta = (String) metadataList.get(i);
      StringTokenizer st = new StringTokenizer(meta, "|");
      String key = null;
      String value = null;
      if (st.hasMoreTokens())
      {
        key = st.nextToken().trim();
      }
      if (st.hasMoreTokens())
      {
        value = st.nextToken().trim();
        LOG.debug("==>ASSESSMENT METADATA key ='" + key + "'.");
        LOG.debug("==>ASSESSMENT METADATA value length =" + value.length() +
          ".");

        assessment.addAssessmentMetaData(key, value);
      }
    }
  }

}