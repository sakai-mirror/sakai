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

package org.navigoproject.osid.assessment;

import org.navigoproject.business.entity.AssessmentTemplate;
import org.navigoproject.business.entity.AssessmentTemplateIterator;
import org.navigoproject.business.entity.ItemTemplate;
import org.navigoproject.business.entity.SectionTemplate;
import org.navigoproject.business.entity.assessment.model.Answer;
import org.navigoproject.business.entity.assessment.model.MediaData;
import org.navigoproject.business.entity.properties.AssessmentTemplateProperties;
import org.navigoproject.data.GenericConnectionManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.ejb.CreateException;

import osid.assessment.Assessment;
import osid.assessment.AssessmentException;
import osid.assessment.AssessmentIterator;
import osid.assessment.AssessmentPublished;
import osid.assessment.Item;
import osid.assessment.ItemIterator;
import osid.assessment.Section;

import osid.shared.Agent;
import osid.shared.Id;
import osid.shared.Type;
import osid.shared.TypeIterator;

/**
 * <p>
 * Title: AssessmentServiceDelegate
 * </p>
 *
 * <p>
 * Description: The AssessmentServiceDelegate follows the Business Delegate
 * J2EE Pattern. It acts as a bridge between the Presentation Layer and the
 * EJB Layer
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 *
 * <p>
 * Company: Stanford University
 * </p>
 *
 * @author Sherwin Lu
 * @version 1.0
 */
/**
 * Todo:
 *
 * <ul>
 * <li>
 * Make sure all methods go through the service locator to get to the back end,
 * instead of calling it directly, so we can separate the back end if needed.
 * </li>
 * <li>
 * Factor out all methods that aren't in AssessmentManager, since the MIT folks
 * want us to keep our implementations clean.
 * </li>
 * <li>
 * Comment more effectively.
 * </li>
 * </ul>
 */
public class AssessmentServiceDelegate
{
  private static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(AssessmentServiceDelegate.class);
  private AssessmentFactory assessmentService = null;

  /**
   * Creates a new AssessmentServiceDelegate object.
   *
   * @throws AssessmentException DOCUMENTATION PENDING
   */
  public AssessmentServiceDelegate()
    throws AssessmentException
  {
    LOG.debug("AssessmentServiceDelegate()");
    try
    {
      assessmentService = new AssessmentFactory();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    try
    {
      // XXX Properties file!
      GenericConnectionManager.getInstance();
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
      throw new Error(e);
    }
  }

  /**
   * Verifies that the Osid version used is 1.0
   *
   * @throws osid.OsidException DOCUMENTATION PENDING
   */
  public void osidVersion_1_0()
    throws osid.OsidException
  {
    // -- TO Do
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentPublishedId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public AssessmentPublished getAssessmentPublished(Id assessmentPublishedId)
    throws osid.assessment.AssessmentException
  {
    // --TO DO
    return null;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessment DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public AssessmentPublished createAssessmentPublished(Assessment assessment)
    throws osid.assessment.AssessmentException
  {
    // -- TO DO
    return null;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentPublishedId DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public void deleteAssessmentPublished(Id assessmentPublishedId)
    throws osid.assessment.AssessmentException
  {
    // -- TO DO
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param name DOCUMENTATION PENDING
   * @param description DOCUMENTATION PENDING
   * @param assessmentType DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   * @throws AssessmentException DOCUMENTATION PENDING
   */
  public Assessment createAssessment(
    String name, String description, Type assessmentType)
    throws osid.assessment.AssessmentException
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("createAssessment(String " + name + ", String " + description + ", Type " + assessmentType + ")");
    }
    Assessment assessment = null;
    try
    {
      assessment =
        assessmentService.createAssessment(name, description, assessmentType);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
      throw new AssessmentException(e.getMessage());
    }

    return assessment;
  }

  /**
   * For beta 2: Check to see if a template or assessment can be deleted, and
   * send back:
   *
   * <ul>
   * <li>
   * 0: No dependencies, can just delete.
   * </li>
   * <li>
   * 1: It's a template, and has assessments
   * </li>
   * <li>
   * 2: It's a template, and has taken assessments
   * </li>
   * <li>
   * 3: It's an assessment, and has been taken
   * </li>
   * <li>
   * 4: There was an error, and the state is unknown
   * </li>
   * </ul>
   *
   *
   * @param assessmentId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   * @throws AssessmentException DOCUMENTATION PENDING
   */
  public int checkDelete(long assessmentId)
    throws osid.assessment.AssessmentException
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("checkDelete(long " + assessmentId + ")");
    }
    try
    {
      return assessmentService.checkDelete(assessmentId);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
      throw new AssessmentException(e.getMessage());
    }
  }

  /**
   * Delete an assessment from the assessment bank.
   *
   * @param assessmentId DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   * @throws AssessmentException DOCUMENTATION PENDING
   */
  public void deleteAssessment(Id assessmentId)
    throws osid.assessment.AssessmentException
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("deleteAssessment(Id " + assessmentId + ")");
    }
    try
    {
      assessmentService.deleteAssessment(assessmentId);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
      throw new AssessmentException(e.getMessage());
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public AssessmentIterator getAssessments()
    throws osid.assessment.AssessmentException
  {
    LOG.debug("getAssessments()");
    try
    {
      /*
         Criteria criteria = new Criteria();
         criteria.addGreaterOrEqualThan("assessmentId", new Integer(0));
         AAMQuery query = new AAMQuery(AssessmentImpl.class, criteria);
         DataAccessObjectDelegate dao = new DataAccessObjectDelegate();
         Collection c = dao.getAllObjects(query);
       */
      return assessmentService.getAssessments();
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
      throw new osid.assessment.AssessmentException(e.getMessage());
    }
  }

  /**
   * A new method to get all assessments for a course.  This should actually
   * call the factory on the back end when we clean this file up.
   *
   * @param courseId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public AssessmentIterator getAssessments(long courseId)
    throws osid.assessment.AssessmentException
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getAssessments(long " + courseId + ")");
    }
    try
    {
      return assessmentService.getAssessments(courseId);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
      throw new osid.assessment.AssessmentException(e.getMessage());
    }
  }

  /**
   * Returns a particular Assessment corresponding to the Id
   *
   * @param assessmentId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public Assessment getAssessment(Id assessmentId)
    throws osid.assessment.AssessmentException
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getAssessment(Id " + assessmentId + ")");
    }
    try
    {
      return assessmentService.getAssessment(assessmentId);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
      throw new osid.assessment.AssessmentException(e.getMessage());
    }
  }

  /* NEED TO GET RID OF THIS..
   * A new method to get a particular assessment.  This should
   * actually call the factory on the back end when we clean this
   * file up.
   */
  public Assessment getAssessment(long assessmentId)
    throws osid.assessment.AssessmentException
  {
    try
    {
      return assessmentService.getAssessment(assessmentId);
    }
    catch(Exception e)
    {
      LOG.error(e);
      throw new osid.assessment.AssessmentException(e.getMessage());
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param name DOCUMENTATION PENDING
   * @param description DOCUMENTATION PENDING
   * @param sectionType DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public Section createSection(
    String name, String description, Type sectionType)
    throws osid.assessment.AssessmentException
  {
    Section section = null;
    try
    {
      section = assessmentService.createSection(name, description, sectionType);
    }
    catch(Exception e)
    {
      throw new osid.assessment.AssessmentException(e.getMessage());
    }

    return section;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param SectionId DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public void deleteSection(Id SectionId)
    throws osid.assessment.AssessmentException
  {
    try
    {
      assessmentService.deleteSection(SectionId);
    }
    catch(Exception e)
    {
      throw new osid.assessment.AssessmentException(e.getMessage());
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param name DOCUMENTATION PENDING
   * @param description DOCUMENTATION PENDING
   * @param type DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.OsidException DOCUMENTATION PENDING
   */
  public SectionTemplate createSectionTemplate(
    String name, String description, Type type)
    throws osid.OsidException
  {
    SectionTemplate template = null;
    try
    {
      template =
        assessmentService.createSectionTemplate(name, description, type);
    }
    catch(Exception e)
    {
      throw new osid.OsidException(e.getMessage());
    }

    return template;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param name DOCUMENTATION PENDING
   * @param description DOCUMENTATION PENDING
   * @param itemType DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public Item createItem(String name, String description, Type itemType)
    throws osid.assessment.AssessmentException
  {
    Item item = null;
    try
    {
      item = assessmentService.createItem(name, description, itemType);
    }
    catch(Exception e)
    {
      throw new osid.assessment.AssessmentException(e.getMessage());
    }

    return item;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param ItemId DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public void deleteItem(Id ItemId)
    throws osid.assessment.AssessmentException
  {
    try
    {
      assessmentService.deleteItem(ItemId);
    }
    catch(Exception e)
    {
      throw new osid.assessment.AssessmentException(e.getMessage());
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public TypeIterator getItemTypes()
    throws osid.assessment.AssessmentException
  {
    try
    {
      return assessmentService.getItemTypes();
    }
    catch(Exception e)
    {
      throw new osid.assessment.AssessmentException(e.getMessage());
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param itemType DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   * @throws java.lang.UnsupportedOperationException DOCUMENTATION PENDING
   */
  public ItemIterator getItemsByType(Type itemType)
    throws osid.assessment.AssessmentException
  {
    /**
     * @todo Implement this osid.assessment.AssessmentManager method
     */
    throw new java.lang.UnsupportedOperationException(
      "Method getItems() not yet implemented.");
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   * @throws java.lang.UnsupportedOperationException DOCUMENTATION PENDING
   */
  public ItemIterator getItems()
    throws osid.assessment.AssessmentException
  {
    /**
     * @todo Implement this osid.assessment.AssessmentManager method
     */
    throw new java.lang.UnsupportedOperationException(
      "Method getItems() not yet implemented.");
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param name DOCUMENTATION PENDING
   * @param description DOCUMENTATION PENDING
   * @param type DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.OsidException DOCUMENTATION PENDING
   */
  public ItemTemplate createItemTemplate(
    String name, String description, Type type)
    throws osid.OsidException
  {
    ItemTemplate template = null;
    try
    {
      template = assessmentService.createItemTemplate(name, description, type);
    }
    catch(Exception e)
    {
      throw new osid.OsidException(e.getMessage());
    }

    return template;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public Answer createAnswer()
    throws osid.assessment.AssessmentException
  {
    Answer answer = null;
    try
    {
      answer = assessmentService.createAnswer();
    }
    catch(Exception e)
    {
      throw new osid.assessment.AssessmentException(e.getMessage());
    }

    return answer;
  }

  /**
   * Delete a collection of answers from the database.
   *
   * @param answers DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public void deleteAnswers(Collection answers)
    throws osid.assessment.AssessmentException
  {
    try
    {
      assessmentService.deleteAnswers(answers);
    }
    catch(Exception e)
    {
      throw new osid.assessment.AssessmentException(e.getMessage());
    }
  }

  /**
   * Delete a media item from the database.
   *
   * @param id DOCUMENTATION PENDING
   */
  public void deleteMedia(int id)
  {
    try
    {
      assessmentService.deleteMedia(id);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /**
   * get the hashmap which contains the MEDIATYPE and its matched ICONURL from
   * the database.
   *
   * @return DOCUMENTATION PENDING
   */
  public HashMap getMediaIcon()
  {
    HashMap mediatypeicon = new HashMap();
    try
    {
      mediatypeicon = assessmentService.getMediaIcon();
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }

    return mediatypeicon;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   * @throws AssessmentException DOCUMENTATION PENDING
   */
  public TypeIterator getAssessmentTypes()
    throws osid.assessment.AssessmentException
  {
    try
    {
      return assessmentService.getAssessmentTypes();
    }
    catch(Exception e)
    {
      LOG.error(e); 
      throw new AssessmentException(e.getMessage());
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   * @throws AssessmentException DOCUMENTATION PENDING
   */
  public TypeIterator getResponseTypes()
    throws osid.assessment.AssessmentException
  {
    try
    {
      return assessmentService.getItemTypes();
    }
    catch(Exception e)
    {
      LOG.error(e);
      throw new AssessmentException(e.getMessage());
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param name DOCUMENTATION PENDING
   * @param description DOCUMENTATION PENDING
   * @param assessmentType DOCUMENTATION PENDING
   * @param properties DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.OsidException DOCUMENTATION PENDING
   */
  public AssessmentTemplate createAssessmentTemplate(
    String name, String description, Type assessmentType,
    AssessmentTemplateProperties properties)
    throws osid.OsidException
  {
    AssessmentTemplate template = null;
    try
    {
      template =
        assessmentService.createAssessmentTemplate(
          name, description, assessmentType, properties);
    }
    catch(Exception e)
    {
      throw new osid.OsidException(e.getMessage());
    }

    return template;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param name DOCUMENTATION PENDING
   * @param description DOCUMENTATION PENDING
   * @param assessmentType DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.OsidException DOCUMENTATION PENDING
   */
  public AssessmentTemplate createAssessmentTemplate(
    String name, String description, Type assessmentType)
    throws osid.OsidException
  {
    return createAssessmentTemplate(name, description, assessmentType, null);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentTemplate DOCUMENTATION PENDING
   *
   * @throws osid.OsidException DOCUMENTATION PENDING
   */
  public void updateAssessmentTemplate(AssessmentTemplate assessmentTemplate)
    throws osid.OsidException
  {
    try
    {
      assessmentService.updateAssessmentTemplate(assessmentTemplate);
    }
    catch(Exception e)
    {
      osid.OsidException oe = new osid.OsidException(osid.OsidException.OPERATION_FAILED);
      oe.initCause(e);
      throw oe;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessment DOCUMENTATION PENDING
   *
   * @throws osid.OsidException DOCUMENTATION PENDING
   */
  public void updateAssessment(Assessment assessment)
    throws osid.OsidException
  {
    try
    {
      assessmentService.updateAssessment(assessment);
    }
    catch(Exception e)
    {
      throw new osid.OsidException(e.getMessage());
    }
  }

  /**
   * Delete a template from the assessment bank.
   *
   * @param assessmentId
   *
   * @throws osid.OsidException if there is a general failure
   */
  public void deleteAssessmentTemplate(osid.shared.Id assessmentId)
    throws osid.OsidException
  {
    try
    {
      assessmentService.deleteAssessmentTemplate(assessmentId);
    }
    catch(Exception e)
    {
      throw new osid.OsidException(e.getMessage());
    }
  }

  /**
   * Get all the Assessments in the Assessment Bank.  Iterators return a set,
   * one at a time.  The Iterator's hasNext method returns true if there are
   * additional objects available; false otherwise.  The Iterator's next
   * method returns the next object.
   *
   * @return AssessmentIterator  The order of the objects returned by the
   *         Iterator is not guaranteed.
   *
   * @throws osid.OsidException if there is a general failure
   */
  public AssessmentTemplateIterator getAssessmentTemplates()
    throws osid.OsidException
  {
    try
    {
      /*
         Criteria criteria = new Criteria();
         criteria.addGreaterOrEqualThan("assessmentId", new Integer(0));
         AAMQuery query = new AAMQuery(AssessmentTemplateImpl.class, criteria);
         DataAccessObjectDelegate dao = new DataAccessObjectDelegate();
         Collection c = dao.getAllObjects(query);
         return new AssessmentTemplateIteratorImpl(c);
       */
      return assessmentService.getAssessmentTemplates();
    }
    catch(Exception e)
    {
      LOG.error(e); 
      throw new osid.OsidException(e.getMessage());
    }
  }

  /**
   * A new method to get all templates for a course.  This should call the
   * factory on the back end when we clean this file up.
   *
   * @param courseId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.OsidException DOCUMENTATION PENDING
   */
  public AssessmentTemplateIterator getAssessmentTemplates(long courseId)
    throws osid.OsidException
  {
    try
    {
      return assessmentService.getAssessmentTemplates(courseId);
    }
    catch(Exception e)
    {
      LOG.error(e); 
      throw new osid.OsidException(e.getMessage());
    }
  }

  /**
   * A new method to get a specific template.  This should call the factory on
   * the back end when we clean this file up.
   *
   * @param templateId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.OsidException DOCUMENTATION PENDING
   */
  public AssessmentTemplateIterator getAssessmentTemplate(long templateId)
    throws osid.OsidException
  {
    try
    {
      return assessmentService.getAssessmentTemplate(templateId);
    }
    catch(Exception e)
    {
      LOG.error(e); 
      throw new osid.OsidException(e.getMessage());
    }
  }

  /**
   * This gets a Collection of AssessmentStatus objects for a given student
   * view.
   *
   * @param courseId DOCUMENTATION PENDING
   * @param agent DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws AssessmentException DOCUMENTATION PENDING
   */
  public Collection getStudentView(long courseId, Agent agent)
    throws AssessmentException
  {
    Collection assessments = new ArrayList();
    try
    {
      assessments = assessmentService.getStudentView(courseId, agent);
    }
    catch(Exception e)
    {
      throw new AssessmentException(e.getMessage());
    }

    return assessments;
  }

  /**
   * This fetches a media item from the back end (because it wasn't properly
   * loaded into the media map.) This method should be factored out and
   * connected to the factory on the back end when we clean this file up.
   *
   * @param mediaId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public MediaData getMedia(long mediaId)
  {
    try
    {
      return assessmentService.getMedia((int) mediaId);
    }
    catch(Exception e)
    {
      LOG.error(e); 

      return null;
    }
  }
}
