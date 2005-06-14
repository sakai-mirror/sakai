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
 * Created on Oct 24, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.osid.assessment.impl;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import org.navigoproject.business.entity.QtiIpAddressData;
import org.navigoproject.business.entity.QtiSettingsData;
import org.navigoproject.data.RepositoryManager;
import org.navigoproject.osid.TypeLib;

import osid.OsidException;
import osid.OsidOwner;
import osid.assessment.Assessment;
import osid.assessment.AssessmentException;
import osid.assessment.AssessmentIterator;
import osid.assessment.AssessmentManager;
import osid.assessment.AssessmentPublished;
import osid.assessment.AssessmentPublishedIterator;
import osid.assessment.Item;
import osid.assessment.ItemIterator;
import osid.assessment.Section;
import osid.assessment.SectionIterator;
import osid.dr.Asset;
import osid.dr.DigitalRepository;
import osid.dr.DigitalRepositoryException;
import osid.shared.Id;
import osid.shared.Type;
import osid.shared.TypeIterator;

/**
 * DOCUMENT ME!
 *
 * @author casong To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 * @version $Id: AssessmentManagerImpl.java,v 1.1.1.1 2004/07/28 21:32:07 rgollub.stanford.edu Exp $
 */
public class AssessmentManagerImpl
  implements AssessmentManager
{
  private final static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(AssessmentManagerImpl.class);
  private RepositoryManager repositoryManager;

  /**
   * Creates a new AssessmentManagerImpl object.
   */
  public AssessmentManagerImpl()
  {
    repositoryManager = new RepositoryManager();
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentManager#createAssessment(java.lang.String, java.lang.String, osid.shared.Type)
   */


  public Assessment createAssessment(
    String name, String description, Type assessmentType)
    throws AssessmentException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "createAssessment(String " + name + ", String " + description +
        ", Type " + assessmentType + ")");
    }

    Assessment assessment;
    try
    {
    	Asset asset = null;
    	
    	LOG.debug(assessmentType);
			LOG.debug(TypeLib.DR_QTI_ASSESSMENT_TAKEN);
			LOG.debug(String.valueOf(assessmentType.equals(TypeLib.DR_QTI_ASSESSMENT_TAKEN)));
    	
    	if (assessmentType.equals(TypeLib.DR_QTI_ASSESSMENT_TAKEN))
    	{
    		// fake it for now
				asset =
								repositoryManager.createAsset(
									java.util.Date.class, name, description);
    	}
    	else{
				asset =
								repositoryManager.createAsset(
									org.navigoproject.business.entity.Assessment.class, name, description);    		
    	}
      
      assessment = new AssessmentImpl(asset);
    }
    catch(DigitalRepositoryException e)
    {
      throw new AssessmentException(
        "Unable to create assessment. " + e.getMessage());
    }

    return assessment;
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentManager#deleteAssessment(osid.shared.Id)
   */
  public void deleteAssessment(Id arg0)
    throws AssessmentException
  {
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentManager#getAssessment(osid.shared.Id)
   */
  public Assessment getAssessment(Id assessmentId)
    throws AssessmentException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getAssessment(Id " + assessmentId + ")");
    }

    Assessment assessment;
    try
    {
      Asset asset = repositoryManager.getAsset(assessmentId);
      assessment = new AssessmentImpl(asset);
    }
    catch(DigitalRepositoryException e)
    {
      throw new AssessmentException(
        "Unable to get assessment with the given id :" + assessmentId);
    }

    return assessment;
  }

	public Assessment getAssessment(Id assessmentId, Calendar dated)
		throws AssessmentException
	{
		if(LOG.isDebugEnabled())
		{
			LOG.debug("getAssessment(Id " + assessmentId + ", Calendar "+ dated+")");
		}

		Assessment assessment;
		try
		{
			Asset asset = repositoryManager.getAsset(assessmentId, dated);
			assessment = new AssessmentImpl(asset);
		}
		catch(DigitalRepositoryException e)
		{
			throw new AssessmentException(
				"Unable to get assessment with the given id :" + assessmentId + "for date " + dated);
		}

		return assessment;
	} 
  /* (non-Javadoc)
   * @see osid.assessment.AssessmentManager#getAssessmentsByType(osid.shared.Type)
   */
  public AssessmentIterator getAssessmentsByType(Type arg0)
    throws AssessmentException
  {
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentManager#getAssessments()
   */
  public AssessmentIterator getAssessments()
    throws AssessmentException
  {
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentManager#createSection(java.lang.String, java.lang.String, osid.shared.Type)
   */
  public Section createSection(
    String name, String description, Type sectionType)
    throws AssessmentException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "createSection(String " + name + ", String " + description + ", Type " +
        sectionType + ")");
    }

    Section section;
    try
    {
      Asset asset =
        repositoryManager.createAsset(
          org.navigoproject.business.entity.Section.class, name, description);
      Id id = asset.getId();
      section = new SectionImpl(asset);
    }
    catch(DigitalRepositoryException e)
    {
      throw new AssessmentException(
        "Unable to create section. " + e.getMessage());
    }

    return section;
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentManager#deleteSection(osid.shared.Id)
   */
  public void deleteSection(Id arg0)
    throws AssessmentException
  {
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentManager#getSection(osid.shared.Id)
   */
  public Section getSection(Id sectionId)
    throws AssessmentException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getSection(Id " + sectionId + ")");
    }

    Section section;
    try
    {
      Asset asset = repositoryManager.getAsset(sectionId);
      section = new SectionImpl(asset);
    }
    catch(DigitalRepositoryException e)
    {
      throw new AssessmentException(
        "Unable to get section with the given id :" + sectionId);
    }

    return section;
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentManager#getSectionsByType(osid.shared.Type)
   */
  public SectionIterator getSectionsByType(Type arg0)
    throws AssessmentException
  {
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentManager#getSections()
   */
  public SectionIterator getSections()
    throws AssessmentException
  {
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentManager#createItem(java.lang.String, java.lang.String, osid.shared.Type)
   */
  public Item createItem(String name, String description, Type itemType)
    throws AssessmentException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "createItem(String " + name + ", String " + description + ", Type " +
        itemType + ")");
    }

    Item item;
    try
    {
      Asset asset =
        repositoryManager.createAsset(
          org.navigoproject.business.entity.Item.class, name, description);
      item = new ItemImpl(asset);
    }
    catch(DigitalRepositoryException e)
    {
      throw new AssessmentException(
        "Unable to create Item. " + e.getMessage());
    }

    return item;
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentManager#deleteItem(osid.shared.Id)
   */
  public void deleteItem(Id arg0)
    throws AssessmentException
  {
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentManager#getItem(osid.shared.Id)
   */
  public Item getItem(Id itemId)
    throws AssessmentException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getItem(Id " + itemId + ")");
    }

    Item item;
    try
    {
      Asset asset = repositoryManager.getAsset(itemId);
      item = new ItemImpl(asset);
    }
    catch(DigitalRepositoryException e)
    {
      throw new AssessmentException(
        "Unable to get section with the given id :" + itemId);
    }

    return item;
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentManager#getItemsByType(osid.shared.Type)
   */
  public ItemIterator getItemsByType(Type arg0)
    throws AssessmentException
  {
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentManager#getItems()
   */
  public ItemIterator getItems()
    throws AssessmentException
  {
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param strTimestamp DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  private Timestamp formatTimestampString(String strTimestamp)
    throws Exception
  {
    Timestamp retVal = null;

    // Check for where the values are all 0's since the Timestamp.valueOf
    // method seems to be interpreting it as Nov 30 0002  
    if((strTimestamp != null) &&
        (! strTimestamp.equals("0000-00-00T00:00:00")))
    {
      // The passed string has a 'T' separating the date and time portion and
      // should be removed to match what the database is looking for.
      retVal = Timestamp.valueOf(strTimestamp.replaceFirst("T", " "));
    }

    return retVal;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param id DOCUMENTATION PENDING
   * @param xmlBuffer DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  private void setAssessmentSettings(
    String id, org.navigoproject.business.entity.Assessment xmlBuffer)
    throws Exception
  {
    Integer maxAttempts = null;
    String autoSubmit = "";
    String testDisabled = "";
    Timestamp startDate = null;
    Timestamp endDate = null;
		Timestamp retractDate = null;
		Timestamp feedbackDate = null;

    maxAttempts = Integer.valueOf(xmlBuffer.getFieldentry("MAX_ATTEMPTS"));
    autoSubmit = xmlBuffer.getFieldentry("AUTO_SUBMIT");
    testDisabled = xmlBuffer.getFieldentry("TEST_DISABLED");
    startDate = formatTimestampString(xmlBuffer.getFieldentry("START_DATE"));
    endDate = formatTimestampString(xmlBuffer.getFieldentry("END_DATE"));

		if("True".equalsIgnoreCase(xmlBuffer.getFieldentry("CONSIDER_RETRACT_DATE")))
		{
		  retractDate = formatTimestampString(xmlBuffer.getFieldentry("RETRACT_DATE"));
		}
		if("DATED".equalsIgnoreCase(xmlBuffer.getFieldentry("FEEDBACK_DELIVERY")))
		{			
		  feedbackDate = formatTimestampString(xmlBuffer.getFieldentry("FEEDBACK_DELIVERY_DATE"));
		}
		if("IMMEDIATE".equalsIgnoreCase(xmlBuffer.getFieldentry("FEEDBACK_DELIVERY")))
		{
			feedbackDate = startDate;
		}
    QtiSettingsData settingsData =
      new QtiSettingsData(
        id, maxAttempts, autoSubmit, testDisabled, startDate, endDate, feedbackDate, retractDate);

    QtiIpAddressData ipData = new QtiIpAddressData(id, "1.22.30.4", "1.2.3.7");

    settingsData.store();
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentManager#createAssessmentPublished(osid.assessment.Assessment)
   */
  public AssessmentPublished createAssessmentPublished(Assessment assessment)
    throws AssessmentException
  {
    //    AssessmentPublished assessmentPublished;
    //    Calendar date = new GregorianCalendar();
    //    assessmentPublished = new AssessmentPublishedImpl(assessment, date);
    //    return assessmentPublished;
    try
    {    	
      setAssessmentSettings(
        assessment.getId().getIdString(),
        (org.navigoproject.business.entity.Assessment) assessment.getData());
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
    }

    return null;

    //throw new AssessmentException("Method is not supported. ");
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentManager#deleteAssessmentPublished(osid.shared.Id)
   */
  public void deleteAssessmentPublished(Id arg0)
    throws AssessmentException
  {
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentManager#getAssessmentPublished(osid.shared.Id)
   */
  public AssessmentPublished getAssessmentPublished(Id assessmentPublishedId)
    throws AssessmentException
  {
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentManager#getAssessmentsPublished()
   */
  public AssessmentPublishedIterator getAssessmentsPublished()
    throws AssessmentException
  {
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentManager#getAssessmentTypes()
   */
  public TypeIterator getAssessmentTypes()
    throws AssessmentException
  {
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentManager#getSectionTypes()
   */
  public TypeIterator getSectionTypes()
    throws AssessmentException
  {
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentManager#getItemTypes()
   */
  public TypeIterator getItemTypes()
    throws AssessmentException
  {
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentManager#getEvaluationTypes()
   */
  public TypeIterator getEvaluationTypes()
    throws AssessmentException
  {
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.OsidManager#getOwner()
   */
  public OsidOwner getOwner()
    throws OsidException
  {
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.OsidManager#updateOwner(osid.OsidOwner)
   */
  public void updateOwner(OsidOwner arg0)
    throws OsidException
  {
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.OsidManager#updateConfiguration(java.util.Map)
   */
  public void updateConfiguration(Map arg0)
    throws OsidException
  {
    throw new OsidException(OsidException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.OsidManager#osidVersion_1_0()
   */
  public void osidVersion_1_0()
    throws OsidException
  {
    throw new OsidException(OsidException.UNIMPLEMENTED);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param itemId DOCUMENTATION PENDING
   * @param date DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws AssessmentException DOCUMENTATION PENDING
   */
  public Item getItem(Id itemId, Calendar date)
    throws AssessmentException
  {
    Item item;
    try
    {
      DigitalRepository repository =
        repositoryManager.getDigitalRepository(Item.class);
      Asset asset = repository.getAssetByDate(itemId, date);
      item = new ItemImpl(asset);
    }
    catch(DigitalRepositoryException e)
    {
      throw new AssessmentException(
        "Unable to get asset by date. " + e.getMessage());
    }

    return item;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param sectionId DOCUMENTATION PENDING
   * @param date DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws AssessmentException DOCUMENTATION PENDING
   */
  public Section getSection(Id sectionId, Calendar date)
    throws AssessmentException
  {
    Section section;
    try
    {
      DigitalRepository repository =
        repositoryManager.getDigitalRepository(Section.class);
      Asset asset = repository.getAssetByDate(sectionId, date);
      section = new SectionImpl(asset);
    }
    catch(DigitalRepositoryException e)
    {
      throw new AssessmentException(
        "Unable to get asset by date. " + e.getMessage());
    }

    return section;
  }
}
