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
 * Created on Dec 23, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.author.assessment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;

import org.navigoproject.osid.TypeLib;
import org.navigoproject.osid.assessment.impl.AssessmentManagerImpl;
import org.navigoproject.ui.web.asi.author.AuthoringHelper;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import osid.OsidException;
import osid.assessment.Assessment;
import osid.assessment.AssessmentException;
import osid.assessment.Section;
import osid.dr.Asset;
import osid.dr.DigitalRepositoryException;
import osid.dr.DigitalRepositoryManager;
import osid.shared.Id;
import osid.shared.SharedException;

import com.oroad.stxx.action.Action;

/**
 * DOCUMENT ME!
 *
 * @author rshastri To change the template for this generated type comment go
 *         to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public class AssessmentHelper
  extends Action
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(AssessmentHelper.class);

  /**
   * DOCUMENTATION PENDING
   *
   * @param inputStream DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public org.navigoproject.business.entity.Assessment readXMLDocument(
    InputStream inputStream)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("readDocument(InputStream " + inputStream);
    }

    org.navigoproject.business.entity.Assessment assessXml = null;

    try
    {
      AuthoringHelper authoringHelper = new AuthoringHelper();
      assessXml =
        new org.navigoproject.business.entity.Assessment(
          authoringHelper.readXMLDocument(inputStream).getDocument());
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

    return assessXml;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentID DOCUMENTATION PENDING
   * @param title DOCUMENTATION PENDING
   * @param sectionXml DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String createSection(
    String assessmentID, String title,
    org.navigoproject.business.entity.Section sectionXml)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("addSection( String " + assessmentID + ")");
    }

    AssessmentManagerImpl assessmentManager = new AssessmentManagerImpl();
    Assessment assessment = getAssessment(assessmentID);
    Section section;
    String sectionIdent = null;
    try
    {
      section =
        assessmentManager.createSection(
          title, "QTIxml", TypeLib.DR_QTI_SECTION);
      sectionIdent = section.getId().getIdString();
      sectionXml.update("section/@ident", sectionIdent);
      sectionXml.update("section/@title", title);
      section.updateData(sectionXml);
      assessment.addSection(section);
     	
    }
    catch(AssessmentException e)
    {
    	// set the section id to null 
			sectionIdent = null;
      LOG.error(e.getMessage(), e);
    }
    catch(SharedException e)
    {	sectionIdent = null;
      LOG.error(e.getMessage(), e);
    }
    catch(DOMException e)
    {
			sectionIdent = null;
      LOG.error(e.getMessage(), e);
    }
    catch(Exception e)
    {
			sectionIdent = null;
      LOG.error(e.getMessage(), e);
    }
		return sectionIdent;
    
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentId DOCUMENTATION PENDING
   *
   * @return ArrayList of fully exploded sections
   */
  
  public ArrayList getSections(String assessmentId)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getSectionRefs(  String" + assessmentId + ")");
    }

    ArrayList arrList = getSectionsOrSectionRef(assessmentId, false);

    return arrList;
  }

  // this function can be called by get composed sections and get Section refs only 
  private ArrayList getSectionsOrSectionRef(
    String assessmentId, boolean secRefOnly)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "getSectionRefs(  String" + assessmentId + ", boolean" + secRefOnly +
        ")");
    }

    ArrayList arrList = new ArrayList();
    try
    {
      Assessment assessment = getAssessment(assessmentId);
      org.navigoproject.business.entity.Assessment assessXml =
        (org.navigoproject.business.entity.Assessment) assessment.getData();
      List list = assessXml.getSectionRefs();
      int size = list.size();
      for(int i = 0; i < size; i++)
      {
        Element element = (Element) (list.get(i));
        String secID = element.getAttribute("linkrefid");
        if(secID != null)
        {
          org.navigoproject.ui.web.asi.author.section.SectionHelper sectionHelper =
            new org.navigoproject.ui.web.asi.author.section.SectionHelper();
          org.navigoproject.business.entity.Section section =
            sectionHelper.getSectionXml(secID);
          List sec = section.selectNodes("section");
          if((sec != null) && (secRefOnly == false))
          {
            arrList.add(sec.get(0)); // gives you full blown section
          }

          if(secRefOnly == true)
          {
            arrList.add(secID);
          }
        }
      }
    }
    catch(AssessmentException e)
    {
      LOG.error(e.getMessage(), e);
    }

    return arrList;
  }
  
  // this function can be called by get composed sections and get Section refs only 
  private ArrayList getSectionsOrSectionRefByDate(
    String assessmentId, Calendar cal, boolean secRefOnly)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "getSectionRefs(  String" + assessmentId + ", boolean" + secRefOnly +
        ")");
    }

    ArrayList arrList = new ArrayList();
    try
    {
      Assessment assessment = getAssessmentByDate(assessmentId, cal);
      org.navigoproject.business.entity.Assessment assessXml =
        (org.navigoproject.business.entity.Assessment) assessment.getData();
      List list = assessXml.getSectionRefs();
      int size = list.size();
      for(int i = 0; i < size; i++)
      {
        Element element = (Element) (list.get(i));
        String secID = element.getAttribute("linkrefid");
        if(secID != null)
        {
          org.navigoproject.ui.web.asi.author.section.SectionHelper sectionHelper =
            new org.navigoproject.ui.web.asi.author.section.SectionHelper();
          org.navigoproject.business.entity.Section section =
            sectionHelper.getSectionXml(secID);
          List sec = section.selectNodes("section");
          if((sec != null) && (secRefOnly == false))
          {
            arrList.add(sec.get(0)); // gives you full blown section
          }

          if(secRefOnly == true)
          {
            arrList.add(secID);
          }
        }
      }
    }
    catch(AssessmentException e)
    {
      LOG.error(e.getMessage(), e);
    }

    return arrList;
  }  

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Assessment getAssessment(String assessmentId)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getAssessment( String " + assessmentId + ")");
    }

    AssessmentManagerImpl assessmentManager = new AssessmentManagerImpl();
    Assessment assessment = null;

    try
    {
      AuthoringHelper authoringHelper = new AuthoringHelper();
      Id assessid = authoringHelper.getId(assessmentId);

      if(assessid != null)
      {
        assessment = assessmentManager.getAssessment(assessid);
      }
    }
    catch(AssessmentException e)
    {
      LOG.error(e.getMessage(), e);
    }

    return assessment;
  }
  
	/**
	 * DOCUMENTATION PENDING
	 *
	 * @param assessmentId DOCUMENTATION PENDING
	 *
	 * @return DOCUMENTATION PENDING
	 */
	public Assessment getAssessmentByDate(String assessmentId, Calendar date)
	{
		if(LOG.isDebugEnabled())
		{
			LOG.debug("getAssessmentByDate( String " + assessmentId + "," + date + ")");
		}

		AssessmentManagerImpl assessmentManager = new AssessmentManagerImpl();
		Assessment assessment = null;

		try
		{
			AuthoringHelper authoringHelper = new AuthoringHelper();
			Id assessid = authoringHelper.getId(assessmentId);

			if(assessid != null)
			{
				assessment = assessmentManager.getAssessment(assessid, date);
			}
		}
		catch(AssessmentException e)
		{
			LOG.error(e.getMessage(), e);
		}

		return assessment;
	}  

  /**
   * DOCUMENTATION PENDING
   *
   * @param title DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String createNewAssessment(String title, org.navigoproject.business.entity.Assessment assessXml)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("createNewAssessment(String " + title + " )");
    }

    String ident = null;
    try
    {
      AssessmentManagerImpl assessmentManager = new AssessmentManagerImpl();
		  Assessment assessment =
        assessmentManager.createAssessment(
          title, "QTIxml", TypeLib.DR_QTI_ASSESSMENT);
      ident = assessment.getId().getIdString();
      assessXml.update("questestinterop/assessment/@ident", ident);
			assessXml.update("questestinterop/assessment/@title", title);
			
		
//			File f = new File("/test.xml");
//			FileWriter fw = new FileWriter(f);			
//		  fw.write(assessXml.stringValue().toString());
//		  fw.close();
		  
      assessment.updateData(assessXml);
      LOG.debug("");
    }
    catch(DOMException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
    }

    return ident;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessID DOCUMENTATION PENDING
   * @param sectionTitle DOCUMENTATION PENDING
   * @param inputStream DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String createNewSection(
    String assessID, String sectionTitle, InputStream inputStream)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "createNewSection( String " + assessID + ", String " + sectionTitle +
        " )");
    }

    org.navigoproject.business.entity.Section sectionXml =
      new org.navigoproject.business.entity.Section(
        readXMLDocument(inputStream).stringValue());
    String ident = createSection(assessID, sectionTitle, sectionXml);
    return ident;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public org.navigoproject.business.entity.Assessment getAssessmentXml(
    String assessmentId)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getAssessment( String " + assessmentId + ")");
    }

    AssessmentManagerImpl assessmentManager = new AssessmentManagerImpl();
    org.navigoproject.business.entity.Assessment assessXml = null;
    Assessment assessment = null;

    try
    {
      AuthoringHelper authoringHelper = new AuthoringHelper();
      Id assessid = authoringHelper.getId(assessmentId);

      if(assessid != null)
      {
        assessment = assessmentManager.getAssessment(assessid);
        assessXml =
          (org.navigoproject.business.entity.Assessment) assessment.getData();
      }
    }
    catch(AssessmentException e)
    {
      LOG.error(e.getMessage(), e);
    }

    return assessXml;
  }
  
  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public org.navigoproject.business.entity.Assessment getAssessmentXmlByDate(
    String assessmentId, Calendar cal)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getAssessment( String " + assessmentId + ")");
    }

    AssessmentManagerImpl assessmentManager = new AssessmentManagerImpl();
    org.navigoproject.business.entity.Assessment assessXml = null;
    Assessment assessment = null;

    try
    {
      AuthoringHelper authoringHelper = new AuthoringHelper();
      Id assessid = authoringHelper.getId(assessmentId);

      if(assessid != null)
      {
        assessment = assessmentManager.getAssessment(assessid, cal);
        assessXml =
          (org.navigoproject.business.entity.Assessment) assessment.getData();
      }
    }
    catch(AssessmentException e)
    {
      LOG.error(e.getMessage(), e);
    }

    return assessXml;
  }  

	public org.navigoproject.business.entity.Assessment getDatedAssessmentXml(
			String assessmentId, Calendar dated)
		{
			if(LOG.isDebugEnabled())
			{
				LOG.debug("getAssessment( String " + assessmentId + ", Calendar "+ dated+")");
			}

			AssessmentManagerImpl assessmentManager = new AssessmentManagerImpl();
			org.navigoproject.business.entity.Assessment assessXml = null;
			Assessment assessment = null;

			try
			{
				AuthoringHelper authoringHelper = new AuthoringHelper();
				Id assessid = authoringHelper.getId(assessmentId);

				if(assessid != null)
				{
					assessment = assessmentManager.getAssessment(assessid, dated);
					assessXml =
						(org.navigoproject.business.entity.Assessment) assessment.getData();
				}
			}
			catch(AssessmentException e)
			{
				LOG.error(e.getMessage(), e);
			}

			return assessXml;
		}
		
  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentID DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   */
  public void setAssessmentDocument(
    String assessmentID, HttpServletRequest request)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "setAssessmentDocument(String " + assessmentID +
        ", HttpServletRequest " + request + " )");
    }

    try
    {
			if(assessmentID != null)
		{
		
      org.navigoproject.business.entity.Assessment newxmlString =
        getComposedAssessment(assessmentID, true);
      Document document;
      document = newxmlString.getDocument();
      this.saveDocument(request, document);
    }
			else
		{
			LOG.error("No Assessment found");
 
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
	 *
	 * @param assessmentID DOCUMENTATION PENDING
	 * @param request DOCUMENTATION PENDING
	 */
	public void setAssessmentDocumentByDate(
		String assessmentID, Calendar cal, HttpServletRequest request)
	{
		if(LOG.isDebugEnabled())
		{
			LOG.debug(
				"setAssessmentDocumentByDate(String " + assessmentID + "," + cal + 
				", HttpServletRequest " + request + " )");
		}

		try
		{
			if(assessmentID != null)
		{
		
			org.navigoproject.business.entity.Assessment newxmlString =
				getComposedAssessmentByDate(assessmentID, cal);
			Document document;
			document = newxmlString.getDocument();
			this.saveDocument(request, document);
		}
			else
		{
			LOG.error("No Assessment found");
 
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
   *
   * @param assessmentId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public org.navigoproject.business.entity.Assessment getComposedAssessment(
    String assessmentId, boolean getFormating)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getComposedAssessment( String " + assessmentId + ")");
    }

    org.navigoproject.business.entity.Assessment assessXml = null;
    try
    {
      Assessment assessment = getAssessment(assessmentId);
      assessXml =
        (org.navigoproject.business.entity.Assessment) assessment.getData();
      
      assessXml.recompose(true);
		 
    }
    catch(AssessmentException e)
    {
      LOG.error(e.getMessage(), e);
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

    return assessXml;
  }
  
	/**
	 * DOCUMENTATION PENDING
	 *
	 * @param assessmentId DOCUMENTATION PENDING
	 *
	 * @return DOCUMENTATION PENDING
	 */
	public org.navigoproject.business.entity.Assessment getComposedAssessmentByDate(
		String assessmentId, Calendar assessmentDate)
	{
		if(LOG.isDebugEnabled())
		{
			LOG.debug("getComposedAssessmentByDate( String " + assessmentId + "," + assessmentDate + ")");
		}

		org.navigoproject.business.entity.Assessment assessXml = null;
		try
		{
			Assessment assessment = getAssessmentByDate(assessmentId, assessmentDate);
			assessXml =
				(org.navigoproject.business.entity.Assessment) assessment.getData();
      
			assessXml.recompose(true);
		 
		}
		catch(AssessmentException e)
		{
			LOG.error(e.getMessage(), e);
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

		return assessXml;
	}  

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  // should be removed 
  /*public org.navigoproject.business.entity.Section getDefaultSection(
    String assessmentId)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getSectionByTitle(  String" + assessmentId + ")");
    }

    return getSectionByTitle(assessmentId, "*#*DEFAULT*#*");
  }*/

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentId DOCUMENTATION PENDING
   * @param sectionTitle DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public org.navigoproject.business.entity.Section getSectionByTitle(
    String assessmentId, String sectionTitle)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "getSectionByTitle(  String" + assessmentId + ", String" +
        sectionTitle + ")");
    }

    org.navigoproject.business.entity.Section sectionXml = null;
    ArrayList secs = getSections(assessmentId);
    if((secs != null) && (secs.size() > 0) && (sectionTitle != null))
    {
      for(int i = 0; i < secs.size(); i++)
      {
        sectionXml = (org.navigoproject.business.entity.Section) secs.get(0);
        String title =
          sectionXml.selectSingleValue("section/@title", "attribute");
        if((title != null) && title.equals(sectionTitle))
        {
          break;
        }
      }
    }

    return sectionXml;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentID DOCUMENTATION PENDING
   * @param assessmentLevelPosition DOCUMENTATION PENDING
   * @param itemIdent DOCUMENTATION PENDING
   */
  public void insertItemInAssessment(
    String assessmentID, String assessmentLevelPosition, String itemIdent)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "insertItemInAssessment(String " + assessmentID + ", String " +
        assessmentLevelPosition + ", String" + itemIdent + " )");
    }

    String sectionID = null;
    ArrayList secRef = getSectionRefs(assessmentID);
    int requiredPosition = new Integer(assessmentLevelPosition).intValue();
    if((secRef != null) && (secRef.size() > 0))
    {
      for(int i = 0; i < secRef.size(); i++)
      {
        org.navigoproject.ui.web.asi.author.section.SectionHelper sectionHelper =
          new org.navigoproject.ui.web.asi.author.section.SectionHelper();

        sectionID = (String) secRef.get(i);
        org.navigoproject.business.entity.Section sec =
          sectionHelper.getSectionXml(sectionID);
        List itemList = sec.selectNodes("section/itemref");
        if((itemList != null) && (itemList.size() >= requiredPosition))
        {
          // the position is in this section 
          sectionHelper.insertItemInSection(
            sectionID, itemIdent, (requiredPosition - 1));

          break;
        }
        else
        {
          requiredPosition = requiredPosition - itemList.size();
        }
      }
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ArrayList getSectionRefs(String assessmentId)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getSectionRefs(  String" + assessmentId + ")");
    }

    ArrayList arrList = getSectionsOrSectionRef(assessmentId, true);

    return arrList;
  }
  
  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ArrayList getSectionRefsByDate(String assessmentId, Calendar cal)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getSectionRefs(  String" + assessmentId + ")");
    }

    ArrayList arrList = getSectionsOrSectionRefByDate(assessmentId, cal, true);

    return arrList;
  }  

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getLastSectionRef(String assessmentId)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getLatSectionRef(  String" + assessmentId + ")");
    }

    String sectionRef = null;
    ArrayList secs = getSectionRefs(assessmentId);
    if((secs != null) && (secs.size() > 1))
    {
      sectionRef = (String) secs.get(secs.size() - 1);
    }
    else
		{
			if((secs != null) && (secs.size()==1))
				 {
					 sectionRef = (String) secs.get(0);
				 }
		}

    return sectionRef;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param sectionid DOCUMENTATION PENDING
   * @param assessmentID DOCUMENTATION PENDING
   */
  public void removeSection(String sectionid, String assessmentID)
  {
    AuthoringHelper authoringHelper = new AuthoringHelper();
    Id secId = authoringHelper.getId(sectionid); // shall handle expection if any
    Id assessId = authoringHelper.getId(assessmentID);
    Assessment assessment = getAssessment(assessmentID);
    org.navigoproject.business.entity.Assessment assessXml;
    try
    {
      assessXml =
        (org.navigoproject.business.entity.Assessment) assessment.getData();
      assessment.removeSection(secId);
      assessXml.removeSectionRef(sectionid);
    }
    catch(AssessmentException e)
    {
      LOG.error(e.getMessage(), e);
    }
  }
  
  public void deleteAssesment(String assessIdent)
  {
		
		try
    {
    	AuthoringHelper authoringHelper = new AuthoringHelper();
  	DigitalRepositoryManager digitalRepositoryManager = null;
	try
			{
				digitalRepositoryManager =
					org.navigoproject.osid.OsidManagerFactory.createDigitalRepositoryManager();
				Asset assessmentAsset =  digitalRepositoryManager.getAsset(authoringHelper.getId(assessIdent));
				assessmentAsset.removeAsset(authoringHelper.getId(assessIdent),true);
			}
			catch(OsidException e)
			{
				LOG.warn("Unexpected OsidException: " + e.getMessage());
				throw new DigitalRepositoryException(e.getMessage());
			}
    }
    catch (DOMException e)
    {
			LOG.error(e.getMessage(), e);
    }
    catch (Exception e)
    {
			LOG.error(e.getMessage(), e);
    }
  }

  /**
   * @param assessmentID
   * @param requiredPosition
   * @param sectionID
   */
  public void insertSection(String assessmentID, String requiredPosition, String sectionID)
  {
		ArrayList secRef = getSectionsOrSectionRef(assessmentID, true);
		ArrayList newSecRefs = new ArrayList();
		 if((secRef != null) && (secRef.size() > 0))
			 {
				 for(int i = 0; i < secRef.size(); i++)
				 {
						String	secID = (String) secRef.get(i);
						//delete all the previous section references
						removeSection(secID,assessmentID);
					if((new Integer(requiredPosition).intValue())- 1 == (i))
					{
						newSecRefs.add(sectionID);						
					}
					newSecRefs.add(secID);
				 
				 }				 
		 }
		//  add new secRefs
		 
		if((newSecRefs != null) && (newSecRefs.size() > 0))
		{
			for(int i = 0; i < newSecRefs.size(); i++)
		 {
				String	secID = (String) newSecRefs.get(i);
				appendSection(assessmentID, secID);
		 }
		}
  }

  /**
   * @param assessmentID
   * @param sectionID
   */
  public void appendSection(String assessmentID, String sectionID)
  {
		try
				{
		org.navigoproject.ui.web.asi.author.section.SectionHelper sectionHelper =
								new org.navigoproject.ui.web.asi.author.section.SectionHelper();
		Assessment assessment = getAssessment(assessmentID);
		Section section = sectionHelper.getSection(sectionID);
		
      assessment.addSection(section);
    }
    catch (AssessmentException e)
    {
      LOG.error(e.getMessage(),e);
    }
    
  }

  /**
   * @param string
   * @return
   */
  public String getFieldentry(String assessmentID, String fieldlabel)
  {
    return getAssessmentXml(assessmentID).getFieldentry(fieldlabel);
    
  }
  
  /**
   * @param string
   * @return
   */
  public String getFieldentryByDate(String assessmentID, Calendar cal, String fieldlabel)
  {
    return getAssessmentXmlByDate(assessmentID, cal).getFieldentry(fieldlabel);
  }
  
  

	public ArrayList getAssessmentReleasedTo(org.navigoproject.business.entity.Assessment assessXml, boolean putName)
	{
		AuthoringHelper authoringHelper = new AuthoringHelper();
		ArrayList selectedList = new ArrayList();
		ArrayList returnSelectedList = new ArrayList();
		selectedList=authoringHelper.changeDelimitedStringtoArray(assessXml.getFieldentry("ASSESSMENT_RELEASED_TO"), ",");
		if(putName)
		{
		
		if(selectedList !=null && selectedList.size()>0)
		{
			Iterator iter = selectedList.iterator();
			while(iter.hasNext())
			{
			String id= (String) iter.next();
			if(id!=null)
			{
			
				if(id.equals("Anonymous"))
				{
					returnSelectedList.add("Anonymous" + "+"+"Anonymous");
				}
				else if(id.equals("Authenticated users"))
				{
					returnSelectedList.add("Authenticated users" + "+"+"Authenticated users");
				}
				else
				{
					String nameOfID = authoringHelper.getIdName(id);
					returnSelectedList.add(id+"+"+nameOfID);	
				}
			}					
			}
		}
		}
		else
		{
			returnSelectedList = selectedList;
		}
		
		return returnSelectedList;
	}

	public void updateLastModifiedBy(String assessmentID)
	{
		AuthoringHelper authoringHelper = new AuthoringHelper();
		org.navigoproject.business.entity.Assessment assessXml = getAssessmentXml(assessmentID);
		assessXml.setFieldentry("LAST_MODIFIED_ON", authoringHelper.getCurrentDateAndTime());
		Assessment assessment = getAssessment(assessmentID);
		try
    {
      assessment.updateData(assessXml);
    }
    catch (AssessmentException e)
    {
    	    	LOG.error(e.getMessage(),e);
    }
	}


  
}
