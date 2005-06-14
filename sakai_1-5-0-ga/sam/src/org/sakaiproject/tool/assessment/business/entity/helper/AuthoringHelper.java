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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.faces.context.FacesContext;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import org.sakaiproject.tool.assessment.business.entity.XmlStringBuffer;
import org.sakaiproject.tool.assessment.business.entity.asi.Assessment;
import org.sakaiproject.tool.assessment.business.entity.asi.Item;
import org.sakaiproject.tool.assessment.business.entity.asi.Section;
import org.sakaiproject.tool.assessment.business.entity.helper.assessment.AssessmentHelper;
import org.sakaiproject.tool.assessment.business.entity.helper.item.ItemHelper;
import org.sakaiproject.tool.assessment.business.entity.helper.section.SectionHelper;
import org.sakaiproject.tool.assessment.data.dao.assessment.ItemMetaData;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentAccessControlIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentFeedbackIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.EvaluationModelIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.SectionDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.shared.TypeIfc;
import org.sakaiproject.tool.assessment.facade.AssessmentFacade;
import org.sakaiproject.tool.assessment.facade.ItemFacade;
import org.sakaiproject.tool.assessment.facade.PublishedAssessmentFacade;
import org.sakaiproject.tool.assessment.facade.SectionFacade;
import org.sakaiproject.tool.assessment.services.ItemService;
import org.sakaiproject.tool.assessment.services.assessment.AssessmentService;
import org.sakaiproject.tool.assessment.services.assessment.PublishedAssessmentService;
import org.sakaiproject.tool.assessment.util.XmlUtil;
import java.io.File;
import org.sakaiproject.tool.assessment.facade.AgentFacade;
import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;

/**
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organization: Sakai Project</p>
 * @author rshastri
 * @author Ed Smiley esmiley@stanford.edu
 * @version $Id: AuthoringHelper.java,v 1.61.2.1 2005/02/23 00:32:00 esmiley.stanford.edu Exp $
 */
public class AuthoringHelper
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(AuthoringHelper.class);
  private static final AuthoringXml ax = new AuthoringXml();

  private  static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss a";
  private  static final String DISPLAY_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss a";


  /**
   * Get a published assessment in Document form from Faces context.
   *
   * @param publishedAssessmentId the published assessment's Id
   * @return the Document with the published assessment data
   */
  public Document getPublishedAssessment(String publishedAssessmentId)
  {
    InputStream is = getBlankAssessmentTemplateContextStream();
    return getPublishedAssessment(publishedAssessmentId, is);
  }

  /**
   * Get a published assessment in Document form from local file.
   *
   * @param publishedAssessmentId the published assessment's Id
   * @return the Document with the published assessment data
   */
  public Document getPublishedAssessmentFromFile(String publishedAssessmentId)
  {
    InputStream is = getBlankAssessmentTemplateFileStream();
    return getPublishedAssessment(publishedAssessmentId, is);
  }
  /**
   * Get a published assessment in Document form.
   *
   * @param publishedAssessmentId the published assessment's Id
   * @param is a stream containing the unpopulated XML document
   * @return the Document with the published assessment data
   */
  public Document getPublishedAssessment(String publishedAssessmentId, InputStream is)
  {
    try
    {
      PublishedAssessmentService assessmentService = new PublishedAssessmentService();
      PublishedAssessmentFacade assessment = assessmentService.getPublishedAssessment(
          publishedAssessmentId);
      // convert published assessment to document
      AssessmentHelper assessmentHelper = new AssessmentHelper();
      Assessment assessmentXml = assessmentHelper.readXMLDocument(is);
      assessmentXml.recompose(false);
      assessmentXml.setIdent(publishedAssessmentId);
      assessmentXml.setTitle(assessment.getTitle());
      // fieldentry properties
      EvaluationModelIfc evaluationModel = assessment.getEvaluationModel();
      if (evaluationModel != null)
      {
        assessmentHelper.updateEvaluationModel(assessmentXml,
          evaluationModel);
      }
      AssessmentFeedbackIfc assessmentFeedback = assessment.getAssessmentFeedback();
      if (assessmentFeedback != null)
      {
        assessmentHelper.updateFeedbackModel(assessmentXml, assessmentFeedback);
      }
      AssessmentAccessControlIfc assessmentAccessControl = assessment.getAssessmentAccessControl();
      if (assessmentAccessControl != null)
      {
        assessmentHelper.updateAccessControl(assessmentXml, assessmentAccessControl);
      }

      // sections
      SectionHelper sectionHelper = new SectionHelper();
      List sectionList = assessment.getSectionArray();
      for (int i=0 ; i< sectionList.size(); i++)
      {
        SectionDataIfc section = (SectionDataIfc) sectionList.get(i);
        Section sectionXml =
          sectionHelper.readXMLDocument(getBlankSectionTemplateContextStream());
        sectionXml.update(section);
        addSection(assessmentXml, sectionXml);
      }

      return assessmentXml.getDocument();
    }
    catch (Exception e)
    {
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  /**
   * Get a published assessment in Document form from Faces context.
   *
   * @param assessmentId the published assessment's Id
   * @return the Document with the published assessment data
   */
  public Document getAssessment(String assessmentId)
  {
    InputStream is = getBlankAssessmentTemplateContextStream();
    return getAssessment(assessmentId, is);
  }

  /**
   * Get an assessment in Document form.
   *
   * @param assessmentId the assessment's Id
   * @param is a stream containing the unpopulated XML document
   * @return the Document with the published assessment data
   */
  public Document getAssessment(String assessmentId, InputStream is)
  {
    try
    {
      AssessmentService assessmentService = new AssessmentService();
      LOG.info("getAssessment() Getting assessment " + assessmentId + "from AssessmentService.");

      AssessmentFacade assessment =
        assessmentService.getAssessment(assessmentId);
      // convert assessment to document
      AssessmentHelper assessmentHelper = new AssessmentHelper();
      Assessment assessmentXml = assessmentHelper.readXMLDocument(is);
      assessmentXml.recompose(false);
      assessmentXml.setIdent(assessmentId);
      assessmentXml.setTitle(assessment.getTitle());
      // fieldentry properties
      EvaluationModelIfc evaluationModel = assessment.getEvaluationModel();
      if (evaluationModel != null)
      {
        assessmentHelper.updateEvaluationModel(assessmentXml,
          evaluationModel);
      }
      AssessmentFeedbackIfc assessmentFeedback = assessment.getAssessmentFeedback();
      if (assessmentFeedback != null)
      {
        assessmentHelper.updateFeedbackModel(assessmentXml, assessmentFeedback);
      }
      AssessmentAccessControlIfc assessmentAccessControl = assessment.getAssessmentAccessControl();
      if (assessmentAccessControl != null)
      {
        assessmentHelper.updateAccessControl(assessmentXml, assessmentAccessControl);
      }

      // sections
      SectionHelper sectionHelper = new SectionHelper();
//      List sectionList = assessment.getSectionArray();
      List sectionList = assessment.getSectionArraySorted();
      for (int i=0 ; i< sectionList.size(); i++)
      {
        SectionDataIfc section = (SectionDataIfc) sectionList.get(i);
        Section sectionXml =
          sectionHelper.readXMLDocument(getBlankSectionTemplateContextStream());
        sectionXml.update(section);
        addSection(assessmentXml, sectionXml);
      }

      return assessmentXml.getDocument();
    }
    catch (Exception e)
    {
      LOG.error(e.getMessage(), e);
    }
    return null;
  }
  /**
   * Get an item in Document form.
   *
   * @param itemId the item id
   * @return the Document with the item data
   */
  public Document getItem(String itemId)
  {
    Item itemXml = new Item();
    try
    {
      ItemService itemService = new ItemService();
      ItemHelper itemHelper = new ItemHelper();
      ItemDataIfc item = itemService.getItem(itemId);
      TypeIfc type = item.getType();
      if ((type.MULTIPLE_CHOICE_SURVEY).equals(type))
      {
        String scale = item.getItemMetaDataByLabel(ItemMetaData.SCALENAME);
        itemXml = itemHelper.readTypeSurveyItem(scale);
      }
      else
      {
        itemXml = itemHelper.readTypeXMLItem(type);
      }
      itemXml.update(item);
      return itemXml.getDocument();
    }
    catch (Exception ex)
    {
      LOG.error(ex);
      return null;
    }
  }

  /**
   *
   * @param assessmentXml
   * @param sectionXml
   * @throws IOException
   * @throws SAXException
   * @throws ParserConfigurationException
   */
  private void addSection(Assessment assessmentXml,
    Section sectionXml) throws IOException, SAXException,
    ParserConfigurationException
  {
    ax.addElement(assessmentXml.getDocument(), "questestinterop/assessment",
          sectionXml.getDocument().getDocumentElement());
  }

  /**
   * Get an InputStream to an unpopulated assessment XML from context.
   * @param context the FacesContext
   * @return InputStream to an unpopulated assessment XML
   */
  public InputStream getBlankAssessmentTemplateContextStream()
  {
    InputStream is = ax.getTemplateInputStream(ax.ASSESSMENT,
                     FacesContext.getCurrentInstance());
    return is;
  }

  /**
   * Get an InputStream to an unpopulated assessment XML from file system.
   * @return InputStream to an unpopulated assessment XML
   */
  public InputStream getBlankAssessmentTemplateFileStream()
  {
    InputStream is = ax.getTemplateInputStream(ax.ASSESSMENT);
    return is;
  }

  /**
   * Get an InputStream to an unpopulated section XML from context.
   * @param context the FacesContext
   * @return InputStream to an unpopulated section XML
   */
  public InputStream getBlankSectionTemplateContextStream()
  {
    InputStream is = ax.getTemplateInputStream(ax.SECTION,
                     FacesContext.getCurrentInstance());
    return is;
  }

  /**
   * Get an InputStream to an unpopulated section XML from file system.
   * @return InputStream to an unpopulated section XML
   */
  public InputStream getBlankSectionTemplateFileStream()
  {
    InputStream is = ax.getTemplateInputStream(ax.SECTION);
    return is;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param fileName DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public XmlStringBuffer readFile(String fileName)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("readFile(String" + fileName + ")");
    }

    XmlStringBuffer xmlString = null;
    InputStreamReader reader;
    String xmlExample;
    try
    {
      InputStream is = new FileInputStream(fileName);
      reader = new InputStreamReader(is);
      StringWriter out = new StringWriter();
      int c;

      while((c = reader.read()) != -1)
      {
        out.write(c);
      }

      reader.close();
      xmlExample = (String) out.toString();
      xmlString = new XmlStringBuffer(xmlExample);
    }
    catch(FileNotFoundException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(IOException e1)
    {
      LOG.error(e1.getMessage(), e1);
    }

    return xmlString;
  }


  /**
   * Import an assessment XML document in QTI format, extract & persist the data.
   * @param document the assessment XML document in QTI format
   * @return a persisted assessment
   */
  public AssessmentFacade createImportedAssessment(Document document)
  {
    LOG.debug(
        document==null?
        "DOCUMENT IS NULL IN createPublishedAssessment(Document)":
        "createPublishedAssessment(Document)");
    AssessmentFacade assessment = null;

    try
    {
      // we need to know who we are
      String me = AgentFacade.getAgentString();

      // create the assessment
      ExtractionHelper exHelper = new ExtractionHelper();
      AssessmentService assessmentService = new AssessmentService();
      ItemService itemService = new ItemService();
      Assessment assessmentXml = new Assessment(document);
      Map assessmentMap = exHelper.mapAssessment(assessmentXml);
//      assessment = exHelper.createAssessment(assessmentMap);
      String description = (String) assessmentMap.get("description");
      String title = (String) assessmentMap.get("title");
      assessment = assessmentService.createAssessmentWithoutDefaultSection(
        title, description, null, null);

      // update the remaining assessment properties
      exHelper.updateAssessment(assessment, assessmentMap);

      // make sure required fields are set
      assessment.setCreatedBy(me);
      assessment.setCreatedDate(assessment.getCreatedDate());
      assessment.setLastModifiedBy(me);
      assessment.setLastModifiedDate(assessment.getCreatedDate());
      assessment.setTypeId(TypeIfc.QUIZ);
      assessment.setStatus(new Integer(1));

      // process each section and each item withiassessmentn each section
      List sectionList = exHelper.getSectionXmlList(assessmentXml);
      int sectionListSize = sectionList.size();
      LOG.debug("sections=" + sectionListSize);


      for (int sec = 0; sec < sectionListSize; sec++)// for each section...
      {
        Section sectionXml =(Section) sectionList.get(sec);
        Map sectionMap = exHelper.mapSection(sectionXml);
        LOG.debug("SECTION MAP=" + sectionMap);
        // create the assessment section
        SectionFacade section =
            assessmentService.addSection("" + assessment.getAssessmentId());
        exHelper.updateSection(section, sectionMap);
        // make sure we are the creator
        LOG.debug("section " + section.getTitle() +
          "created by '" + me+ "'.");
        section.setCreatedBy(me);
        section.setCreatedDate(assessment.getCreatedDate());
        section.setLastModifiedBy(me);
        section.setLastModifiedDate(assessment.getCreatedDate());
        section.setTypeId(TypeIfc.DEFAULT_SECTION);
        section.setStatus(new Integer(1));
        // set the sequence
        section.setSequence(new Integer(sec + 1));
//        // add the section to the assessment
//        section.setAssessmentId(assessment.getAssessmentId());//many to one
//        section.setAssessment(assessment);
//        assessment.getSectionArray().add(section);// one to many

        List itemList = exHelper.getItemXmlList(sectionXml);
        for (int itm = 0; itm < itemList.size(); itm++)// for each item
        {
          LOG.debug("items=" + itemList.size());
          Item itemXml = (Item) itemList.get(itm);
          Map itemMap = exHelper.mapItem(itemXml);
          LOG.debug("ITEM MAP=" + itemMap);

          ItemFacade item = new ItemFacade();
          exHelper.updateItem(item, itemMap);
          // make sure required fields are set
          item.setCreatedBy(me);
          item.setCreatedDate(assessment.getCreatedDate());
          item.setLastModifiedBy(me);
          item.setLastModifiedDate(assessment.getCreatedDate());
          LOG.debug("ITEM TYPE IS: " +item.getTypeId());
          item.setStatus(ItemDataIfc.ACTIVE_STATUS);
          // assign the next sequence number
          item.setSequence(new Integer(itm + 1));
          // add item to section
          item.setSection(section);// one to many
          section.addItem(item);// many to one
          itemService.saveItem(item);
          // debugging
          LOG.debug("ITEM:  ans key" + item.getAnswerKey() );
          LOG.debug("ITEM:  correct feed" + item.getCorrectItemFeedback() );
          LOG.debug("ITEM:  incorrect feed " + item.getInCorrectItemFeedback() );
          LOG.debug("ITEM:  by " + item.getCreatedBy() );
          LOG.debug("ITEM:  date" + item.getCreatedDate() );
          LOG.debug("ITEM:  desc " + item.getDescription() );
          LOG.debug("ITEM:  duration" + item.getDuration() );
          LOG.debug("ITEM:  general feed " + item.getGeneralItemFeedback() );
          LOG.debug("ITEM:  incorrect " + item.getInCorrectItemFeedback() );
          LOG.debug("ITEM:  is true " + item.getIsTrue() );
          LOG.debug("ITEM DEBUG item text" + item.getText() );
          LOG.debug("ITEM:  item text" + item.getText() );
        }// ... end for each item
      }// ... end for each section

      LOG.debug("assessment created by '" + assessment.getCreatedBy() + "'.");
      assessmentService.update(assessment);
      // debugging
      LOG.debug("ASSESSMENT:  meta " + assessment.getAssessmentMetaDataMap());
      LOG.debug("ASSESSMENT:  feed " + assessment.getAssessmentFeedback());
      LOG.debug("ASSESSMENT:  comments  " + assessment.getComments());
      LOG.debug("ASSESSMENT:  by " + assessment.getCreatedBy());
      LOG.debug("ASSESSMENT:  by date " + assessment.getCreatedDate());
      LOG.debug("ASSESSMENT:  desc" + assessment.getDescription());
      LOG.debug("ASSESSMENT:  disp " + assessment.getDisplayName());
      LOG.debug("ASSESSMENT:  last by " + assessment.getLastModifiedBy());
      LOG.debug("ASSESSMENT:  last date" + assessment.getLastModifiedDate());
      LOG.debug("ASSESSMENT:  mult " + assessment.getMultipartAllowed());
      LOG.debug("ASSESSMENT:  title " + assessment.getTitle());
      LOG.debug("ASSESSMENT DEBUG title " + assessment.getTitle());
      assessmentService.saveAssessment(assessment);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }

    return assessment;
  }

  /**
   * Import an item XML document in QTI format, extract & persist the data.
   * @param document the item XML document in QTI format
   * @return a persisted item
   */
  public ItemFacade createImportedItem(Document document)
  {
    LOG.debug(
        document==null?
        "DOCUMENT IS NULL IN createImportedItem(Document)":
        "createImportedItem(Document)");
    ItemFacade item = new ItemFacade();

    try
    {
      // create the item
      ExtractionHelper exHelper = new ExtractionHelper();
      LOG.debug("XSLT Path: " + exHelper.getTransformPath());
      Item itemXml = new Item(document);
      Map itemMap = exHelper.mapItem(itemXml);
      LOG.debug("ITEM MAP=" + itemMap);
      LOG.debug("updating item");
      exHelper.updateItem(item, itemMap);
      ItemService itemService = new ItemService();
      LOG.debug("Saving item");
      itemService.saveItem(item);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
//      e.printStackTrace();
    }

    return item;
  }


  /**
   * DOCUMENTATION PENDING
   *
   * @param inputStream DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public XmlStringBuffer readXMLDocument(InputStream inputStream)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("readDocument(InputStream " + inputStream);
    }

    Document document = null;
    DocumentBuilderFactory builderFactory =
      DocumentBuilderFactory.newInstance();
//    builderFactory.setNamespaceAware(true);
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

    return new XmlStringBuffer(document);
  }


	public ArrayList changeDelimitedStringtoArray(String inputStr, String delimiter)
		{
			ArrayList selectedList = new ArrayList();
			if(inputStr != null && inputStr.trim().length() >0 )
			{
				StringTokenizer st = new StringTokenizer(inputStr, delimiter );
				if(st != null)
				{

				while(st.hasMoreTokens())
				{
					selectedList.add(st.nextToken());
				}
				}
				else
				{
					selectedList.add(inputStr);
				}
			}
			return selectedList;
		}

    /**
     * current date default format
     * @return date string
     */
    public String getCurrentDateAndTime()
    {
         return getCurrentDateAndTime(DATE_FORMAT);
    }
    /**
     * current date default format
     * @return date string
     */
    public String getCurrentDisplayDateAndTime()
    {
         return getCurrentDateAndTime(DISPLAY_DATE_FORMAT);
    }

    /**
     * current date format
     * @param dateFormat
     * @return date string
     */
    public String getCurrentDateAndTime(String dateFormat)
    {
      Calendar cal = Calendar.getInstance(TimeZone.getDefault());
      java.text.SimpleDateFormat sdf =
        new java.text.SimpleDateFormat(dateFormat);
      sdf.setTimeZone(TimeZone.getDefault());
      LOG.debug("Now : " + sdf.format(cal.getTime()));
      return sdf.format(cal.getTime());
    }

}
