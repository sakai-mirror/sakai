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
import org.navigoproject.business.entity.ItemTemplateIterator;
import org.navigoproject.business.entity.SectionTemplate;
import org.navigoproject.business.entity.SectionTemplateIterator;
import org.navigoproject.business.entity.assessment.model.Answer;
import org.navigoproject.business.entity.assessment.model.AssessmentImpl;
import org.navigoproject.business.entity.assessment.model.AssessmentIteratorImpl;
import org.navigoproject.business.entity.assessment.model.AssessmentTemplateIteratorImpl;
import org.navigoproject.business.entity.assessment.model.ItemImpl;
import org.navigoproject.business.entity.assessment.model.ItemIteratorImpl;
import org.navigoproject.business.entity.assessment.model.ItemPropertiesImpl;
import org.navigoproject.business.entity.assessment.model.ItemTemplateImpl;
import org.navigoproject.business.entity.assessment.model.ItemTemplateIteratorImpl;
import org.navigoproject.business.entity.assessment.model.MediaData;
import org.navigoproject.business.entity.assessment.model.SectionImpl;
import org.navigoproject.business.entity.assessment.model.SectionIteratorImpl;
import org.navigoproject.business.entity.assessment.model.SectionTemplateImpl;
import org.navigoproject.business.entity.assessment.model.SectionTemplateIteratorImpl;
import org.navigoproject.business.entity.properties.ItemTemplateProperties;
import org.navigoproject.business.entity.properties.SectionTemplateProperties;
import org.navigoproject.data.dao.AssessmentAccessObject;
import org.navigoproject.data.dao.ItemAccessObject;
import org.navigoproject.data.dao.SectionAccessObject;
import org.navigoproject.data.dao.UtilAccessObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import osid.assessment.Assessment;
import osid.assessment.AssessmentException;
import osid.assessment.AssessmentIterator;
import osid.assessment.AssessmentPublished;
import osid.assessment.AssessmentPublishedIterator;
import osid.assessment.AssessmentTaken;
import osid.assessment.Evaluation;
import osid.assessment.Item;
import osid.assessment.ItemIterator;
import osid.assessment.Section;
import osid.assessment.SectionIterator;

import osid.shared.Agent;
import osid.shared.Id;
import osid.shared.Type;

/**
 * <p>
 * Title: NavigoProject.org
 * </p>
 *
 * <p>
 * Description: OKI based implementation
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
 * @author Rachel Gollub
 * @version $Id: AssessmentManager.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
public class AssessmentManager
  implements osid.assessment.AssessmentManager
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(AssessmentManager.class);
    
  private static AssessmentManager factory = null;
  private static Collection assessments;
  private static Collection assessmentTemplates;
  private static Collection sections;
  private static Collection sectionTemplates;
  private static Collection items;
  private static Collection itemTemplates;
  private static Logger logger =
    Logger.getLogger(AssessmentManager.class.getName());

  /**
   * Creates a new AssessmentManager object.
   */
  public AssessmentManager()
  {
    assessments = new ArrayList();
    assessmentTemplates = new ArrayList();
    sections = new ArrayList();
    sectionTemplates = new ArrayList();
    items = new ArrayList();
    itemTemplates = new ArrayList();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public static AssessmentManager getInstance()
  {
    if(factory == null)
    {
      factory = new AssessmentManager();
    }

    return factory;
  }

  /**
   * verify that the Osid Version used is 1.0
   *
   * @throws osid.OsidException DOCUMENTATION PENDING
   */
  public void osidVersion_1_0()
    throws osid.OsidException
  {
    // --TO DO
  }

  /**
   * Get the assessment corresponding to the assessment Id
   *
   * @param assessmentId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Assessment getAssessment(Id assessmentId)
  {
    try
    {
      AssessmentAccessObject aao = new AssessmentAccessObject();
      AssessmentIterator aiter =
        aao.getAssessments((new Long(assessmentId.getIdString())).longValue());
      if(aiter == null)
      {
        return null;
      }
      else
      {
        if(! aiter.hasNext())
        {
          return null;
        }
        else
        {
          return aiter.next();
        }
      }
    }
    catch(Exception e)
    {
      LOG.error(e);
//      throw new Error(e);

      return null;
    }
  }

  /**
   * Get all the Assessments of a specific Type.  Iterators return a set, one
   * at a time.  The Iterator's hasNext method returns true if there are
   * additional objects available; false otherwise.  The Iterator's next
   * method returns the next object.
   *
   * @param assessmentType AssessmentType
   *
   * @return AssessmentIterator  The order of the objects returned by the
   *         Iterator is not guaranteed.
   *
   * @throws AssessmentException if there is a general failure or if the Type
   *         is unknown
   */
  public AssessmentTemplateIterator getAssessmentTemplates(
    osid.shared.Type assessmentType)
    throws AssessmentException
  {
    ArrayList templates = new ArrayList();
    Iterator it = assessmentTemplates.iterator();
    while(it.hasNext())
    {
      AssessmentTemplate template = (AssessmentTemplate) it.next();
      if(template.getAssessmentType().equals(assessmentType))
      {
        templates.add(template);
      }
    }

    return new AssessmentTemplateIteratorImpl(templates);
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
   * @throws AssessmentException if there is a general failure
   */
  public AssessmentTemplateIterator getAssessmentTemplates()
    throws AssessmentException
  {
    AssessmentAccessObject aao = new AssessmentAccessObject();

    return aao.getAllTemplates();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param courseId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws AssessmentException DOCUMENTATION PENDING
   */
  public AssessmentTemplateIterator getAssessmentTemplates(long courseId)
    throws AssessmentException
  {
    AssessmentAccessObject aao = new AssessmentAccessObject();

    return aao.getAllTemplates(courseId);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param templateId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws AssessmentException DOCUMENTATION PENDING
   */
  public AssessmentTemplateIterator getAssessmentTemplate(long templateId)
    throws AssessmentException
  {
    AssessmentAccessObject aao = new AssessmentAccessObject();

    return aao.getTemplate(templateId);
  }

  /**
   * Get a Collection of AssessmentStatus for the student view of a course.
   *
   * @param courseId DOCUMENTATION PENDING
   * @param agent DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getStudentView(long courseId, Agent agent)
  {
    /*
       StudentViewAccessObject svao = new StudentViewAccessObject();
       return svao.getAssessments(courseId, agent);
     */
    return new ArrayList();
  }

  /**
   * Create a new Section Template and adds it to the Section Template Bank.
   *
   * @param name
   * @param description
   * @param sectionType
   * @param properties a populated SectionTemplateProperties object
   *
   * @return
   *
   * @throws AssessmentException AssessmentException if there is a general
   *         failure
   */
  public SectionTemplate createSectionTemplate(
    String name, String description, Type sectionType,
    SectionTemplateProperties properties)
    throws AssessmentException
  {
    SectionAccessObject sao = new SectionAccessObject();
    SectionTemplate template = new SectionTemplateImpl(sao.getSectionId());
    template.updateDisplayName(name);
    template.updateDescription(description);
    template.updateData(properties);
    sectionTemplates.add(template);

    return template;
  }

  /**
   * Create a SectionTemplate.
   *
   * @param name
   * @param description
   * @param sectionType
   *
   * @return
   *
   * @throws AssessmentException
   */
  public SectionTemplate createSectionTemplate(
    String name, String description, Type sectionType)
    throws AssessmentException
  {
    return createSectionTemplate(name, description, sectionType, null);
  }

  /**
   * Delete a SectionTemplate from the SectionTemplate Bank.
   *
   * @param sectionId
   *
   * @throws AssessmentException if there is a general failure or if the object
   *         has not been created
   */
  public void deleteSectionTemplate(osid.shared.Id sectionId)
    throws AssessmentException
  {
    Iterator it = sectionTemplates.iterator();
    while(it.hasNext())
    {
      SectionTemplate template = (SectionTemplate) it.next();
      if(template.getId().equals(sectionId))
      {
        sectionTemplates.remove(template);

        return;
      }
    }

    throw new AssessmentException("SectionTemplate does not exist.");
  }

  /**
   * Get all the SectionTemplates of a specific Type.  Iterators return a set,
   * one at a time.  The Iterator's hasNext method returns true if there are
   * additional objects available; false otherwise.  The Iterator's next
   * method returns the next object.
   *
   * @param sectionType sectionType
   *
   * @return SectionTemplateIterator  The order of the objects returned by the
   *         Iterator is not guaranteed.
   *
   * @throws AssessmentException if there is a general failure or if the Type
   *         is unknown
   */
  public SectionTemplateIterator getSectionTemplates(
    osid.shared.Type sectionType)
    throws AssessmentException
  {
    ArrayList templates = new ArrayList();
    Iterator it = sectionTemplates.iterator();
    while(it.hasNext())
    {
      SectionTemplate template = (SectionTemplate) it.next();
      if(template.getSectionType().equals(sectionType))
      {
        templates.add(template);
      }
    }

    return new SectionTemplateIteratorImpl(templates);
  }

  /**
   * Get all the Sections in the Section Bank.  Iterators return a set, one at
   * a time.  The Iterator's hasNext method returns true if there are
   * additional objects available; false otherwise.  The Iterator's next
   * method returns the next object.
   *
   * @return SectionIterator  The order of the objects returned by the Iterator
   *         is not guaranteed.
   *
   * @throws AssessmentException if there is a general failure
   */
  public SectionTemplateIterator getSectionTemplates()
    throws AssessmentException
  {
    return new SectionTemplateIteratorImpl(sectionTemplates);
  }

  /**
   * Create a new Item Template and adds it to the Item Template Bank.
   *
   * @param name
   * @param description
   * @param itemType
   * @param properties a populated ItemTemplateProperties object
   *
   * @return
   *
   * @throws AssessmentException AssessmentException if there is a general
   *         failure
   */
  public ItemTemplate createItemTemplate(
    String name, String description, Type itemType,
    ItemTemplateProperties properties)
    throws AssessmentException
  {
    ItemAccessObject iao = new ItemAccessObject();
    ItemTemplate template = new ItemTemplateImpl(iao.getItemId());
    template.updateDisplayName(name);
    template.updateDescription(description);
    template.updateData(properties);
    itemTemplates.add(template);

    return template;
  }

  /**
   * DOCUMENT ME!
   *
   * @param name
   * @param description
   * @param itemType
   *
   * @return
   *
   * @throws AssessmentException
   */
  public ItemTemplate createItemTemplate(
    String name, String description, Type itemType)
    throws AssessmentException
  {
    return createItemTemplate(name, description, itemType, null);
  }

  /**
   * Delete an Assessment from the Assessment Bank.
   *
   * @param itemId
   *
   * @throws AssessmentException if there is a general failure or if the object
   *         has not been created
   */
  public void deleteItemTemplate(osid.shared.Id itemId)
    throws AssessmentException
  {
    Iterator it = itemTemplates.iterator();
    while(it.hasNext())
    {
      ItemTemplate template = (ItemTemplate) it.next();
      if(template.getId().equals(itemId))
      {
        itemTemplates.remove(template);

        return;
      }
    }

    throw new AssessmentException("ItemTemplate does not exist.");
  }

  /**
   * Get all the Assessments of a specific Type.  Iterators return a set, one
   * at a time.  The Iterator's hasNext method returns true if there are
   * additional objects available; false otherwise.  The Iterator's next
   * method returns the next object.
   *
   * @param itemType AssessmentType
   *
   * @return AssessmentIterator  The order of the objects returned by the
   *         Iterator is not guaranteed.
   *
   * @throws AssessmentException if there is a general failure or if the Type
   *         is unknown
   */
  public ItemTemplateIterator getItemTemplates(osid.shared.Type itemType)
    throws AssessmentException
  {
    ArrayList templates = new ArrayList();
    Iterator it = itemTemplates.iterator();
    while(it.hasNext())
    {
      ItemTemplate template = (ItemTemplate) it.next();
      if(template.getItemType().equals(itemType))
      {
        templates.add(template);
      }
    }

    return new ItemTemplateIteratorImpl(templates);
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
   * @throws AssessmentException if there is a general failure
   */
  public ItemTemplateIterator getItemTemplates()
    throws AssessmentException
  {
    return new ItemTemplateIteratorImpl(itemTemplates);
  }

  /**
   * Create a new Section and add it to the Section Bank.
   *
   * @param name name
   * @param description description
   * @param sectionType SectionType
   *
   * @return Item with its name, description, and Unique Id set.
   *
   * @throws AssessmentException if there is a general failure or if the Type
   *         is unknown
   */
  public Section createSection(
    String name, String description, osid.shared.Type sectionType)
    throws AssessmentException
  {
    SectionAccessObject sao = new SectionAccessObject();
    Section section = new SectionImpl(sao.getSectionId());
    section.updateDisplayName(name);
    section.updateDescription(description);
    sections.add(section);

    return section;
  }

  /**
   * Delete a Section from the Section Bank.
   *
   * @param sectionId
   *
   * @throws AssessmentException if there is a general failure or if the object
   *         has not been created
   */
  public void deleteSection(osid.shared.Id sectionId)
    throws AssessmentException
  {
    try
    {
      SectionAccessObject sao = new SectionAccessObject();
      sao.deleteSection(new Long(sectionId.toString()).longValue());
    }
    catch(Exception e)
    {
      throw new AssessmentException(e.getMessage());
    }
  }

  /**
   * Get the Section corresponding to the sectionId
   *
   * @param sectionId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Section getSection(Id sectionId)
  {
    // TO DO
    return null;
  }

  /**
   * Get all the Sections of a specific Type.  Iterators return a set, one at a
   * time.  The Iterator's hasNext method returns true if there are additional
   * objects available; false otherwise.  The Iterator's next method returns
   * the next object.
   *
   * @param sectionType SectionType
   *
   * @return SectionIterator  The order of the objects returned by the Iterator
   *         is not guaranteed.
   *
   * @throws AssessmentException if there is a general failure or if the Type
   *         is unknown
   */
  public SectionIterator getSectionsByType(osid.shared.Type sectionType)
    throws AssessmentException
  {
    ArrayList sectionList = new ArrayList();
    Iterator it = sections.iterator();
    while(it.hasNext())
    {
      Section section = (Section) it.next();
      if(section.getSectionType().equals(sectionType))
      {
        sectionList.add(section);
      }
    }

    return new SectionIteratorImpl(sectionList);
  }

  /**
   * Get all the Sections in the Section Bank  Iterators return a set, one at a
   * time.  The Iterator's hasNext method returns true if there are additional
   * objects available; false otherwise.  The Iterator's next method returns
   * the next object.
   *
   * @return SectionIterator  The order of the objects returned by the Iterator
   *         is not guaranteed.
   *
   * @throws AssessmentException if there is a general failure
   */
  public SectionIterator getSections()
    throws AssessmentException
  {
    return new SectionIteratorImpl(sections);
  }

  /**
   * Create a new Item  and add it to the Item Bank.
   *
   * @param name name
   * @param description description
   * @param itemType ResponseType
   *
   * @return Item with its name, description, and Unique Id set.
   *
   * @throws AssessmentException if there is a general failure or if the Type
   *         is unknown
   */
  public Item createItem(
    String name, String description, osid.shared.Type itemType)
    throws AssessmentException
  {
    logger.debug("Item type is " + itemType.toString());
    ItemAccessObject iao = new ItemAccessObject();
    Item item = new ItemImpl(iao.getItemId());
    item.updateDisplayName(name);
    item.updateDescription(description);
    ((ItemPropertiesImpl) item.getData()).setItemType(itemType);
    items.add(item);

    return item;
  }

  /**
   * Delete a Item from the Item Bank.
   *
   * @param itemId
   *
   * @throws AssessmentException if there is a general failure or if the object
   *         has not been created
   */
  public void deleteItem(osid.shared.Id itemId)
    throws AssessmentException
  {
    try
    {
      ItemAccessObject iao = new ItemAccessObject();
      iao.deleteItem(new Long(itemId.toString()).longValue());
    }
    catch(Exception e)
    {
      throw new AssessmentException(e.getMessage());
    }
  }

  /**
   * Get Item corresponding to itemId
   *
   * @param itemId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Item getItem(Id itemId)
  {
    // --TO DO
    return null;
  }

  /**
   * Get all the Items of a specific Type.  Iterators return a set, one at a
   * time.  The Iterator's hasNext method returns true if there are additional
   * objects available; false otherwise.  The Iterator's next method returns
   * the next object.
   *
   * @param itemType itemType
   *
   * @return ItemIterator  The order of the objects returned by the Iterator is
   *         not guaranteed.
   *
   * @throws osid.assessment.AssessmentException if there is a general failure
   *         or if the Type is unknown
   */
  public ItemIterator getItemsByType(osid.shared.Type itemType)
    throws osid.assessment.AssessmentException
  {
    ArrayList itemList = new ArrayList();
    Iterator it = items.iterator();
    while(it.hasNext())
    {
      Item item = (Item) it.next();
      if(item.getItemType().equals(itemType))
      {
        itemList.add(item);
      }
    }

    return new ItemIteratorImpl(itemList);
  }

  /**
   * Get all the Items in the Item Bank.  Iterators return a set, one at a
   * time.  The Iterator's hasNext method returns true if there are additional
   * objects available; false otherwise.  The Iterator's next method returns
   * the next object.
   *
   * @return ItemIterator  The order of the objects returned by the Iterator is
   *         not guaranteed.
   *
   * @throws osid.assessment.AssessmentException if there is a general failure
   */
  public ItemIterator getItems()
    throws osid.assessment.AssessmentException
  {
    return new ItemIteratorImpl(items);
  }

  /**
   * Create a new Answer and give it an Id.
   *
   * @return Answer with its Id set.
   *
   * @throws AssessmentException if there is a general failure
   */
  public Answer createAnswer()
    throws AssessmentException
  {
    ItemAccessObject iao = new ItemAccessObject();
    Answer answer = new Answer(iao.getAnswerId());

    return answer;
  }

  /**
   * Delete Answers.
   *
   * @param answers Answers to delete.
   *
   * @throws AssessmentException if there is a general failure or if the Type
   *         is unknown
   */
  public void deleteAnswers(Collection answers)
    throws AssessmentException
  {
    ItemAccessObject iao = new ItemAccessObject();
    iao.deleteAnswers(answers);
  }

  /**
   * Get Media
   *
   * @param mediaId The id of the media to retrieve
   *
   * @return DOCUMENTATION PENDING
   */
  public MediaData getMedia(int mediaId)
  {
    UtilAccessObject uao = new UtilAccessObject();

    return UtilAccessObject.getMediaData(mediaId);
  }

  /**
   * Get mediatype with its matched ICONURL.
   *
   * @return DOCUMENTATION PENDING
   */
  public HashMap getMediaIcon()
  {
    HashMap mediatypeicon = new HashMap();
    mediatypeicon = UtilAccessObject.getMediaIcon();

    return mediatypeicon;
  }

  /**
   * Delete media.
   *
   * @param id The id of the media to delete.
   */
  public void deleteMedia(int id)
  {
    UtilAccessObject.deleteMedia(id);
  }

  /**
   * Return owner of this OsidManager.
   *
   * @return osid.OsidOwner Owner
   *
   * @throws osid.OsidException DOCUMENTATION PENDING
   */
  public osid.OsidOwner getOwner()
    throws osid.OsidException
  {
    return null;
  }

  /**
   * Update the owner of this OsidManager.
   *
   * @param owner owner
   *
   * @throws osid.OsidException DOCUMENTATION PENDING
   */
  public void updateOwner(osid.OsidOwner owner)
    throws osid.OsidException
  {
  }

  /**
   * Update the configuration of this OsidManager.
   *
   * @param configuration configuration
   *
   * @throws osid.OsidException DOCUMENTATION PENDING
   */
  public void updateConfiguration(java.util.Map configuration)
    throws osid.OsidException
  {
  }

  /**
   * Create a new Assessment and add it to the Assessment Bank.
   *
   * @param name name
   * @param description description
   * @param assessmentType AssessmentType
   *
   * @return Item with its name, description, and Unique Id set.
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   * @throws AssessmentException if there is a general failure or if the Type
   *         is unknown
   */
  public Assessment createAssessment(
    String name, String description, osid.shared.Type assessmentType)
    throws osid.assessment.AssessmentException
  {
    try
    {
      AssessmentAccessObject aao = new AssessmentAccessObject();
      Id id = aao.getAssessmentId();
      Assessment assessment = new AssessmentImpl(id);
      assessment.updateDisplayName(name);
      assessment.updateDescription(description);
      assessments.add(assessment);

      return assessment;
    }
    catch(Exception e)
    {
      LOG.error(e);
      throw new AssessmentException("Unable to create Id:" + e.getMessage());
    }
  }

  /**
   * Delete an Assessment from the Assessment Bank.
   *
   * @param assessmentId
   *
   * @throws osid.assessment.AssessmentException if there is a general failure
   *         or if the object has not been created
   */
  public void deleteAssessment(osid.shared.Id assessmentId)
    throws osid.assessment.AssessmentException
  {
    AssessmentAccessObject aao = new AssessmentAccessObject();
    aao.deleteAssessment(new Long(assessmentId.toString()).longValue());
  }

  /**
   * Get all the Assessments of a specific Type.  Iterators return a set, one
   * at a time.  The Iterator's hasNext method returns true if there are
   * additional objects available; false otherwise.  The Iterator's next
   * method returns the next object.
   *
   * @param assessmentType AssessmentType
   *
   * @return AssessmentIterator  The order of the objects returned by the
   *         Iterator is not guaranteed.
   *
   * @throws osid.assessment.AssessmentException if there is a general failure
   *         or if the Type is unknown
   */
  public AssessmentIterator getAssessmentsByType(
    osid.shared.Type assessmentType)
    throws osid.assessment.AssessmentException
  {
    ArrayList assessmentList = new ArrayList();
    Iterator it = assessments.iterator();
    while(it.hasNext())
    {
      Assessment assessment = (Assessment) it.next();
      if(assessment.getAssessmentType().equals(assessmentType))
      {
        assessmentList.add(assessment);
      }
    }

    return new AssessmentIteratorImpl(assessmentList);
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
   * @throws osid.assessment.AssessmentException if there is a general failure
   */
  public AssessmentIterator getAssessments()
    throws osid.assessment.AssessmentException
  {
    AssessmentAccessObject aao = new AssessmentAccessObject();

    return aao.getAllAssessments();
  }

  /**
   * DOCUMENTATION PENDING
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
    AssessmentAccessObject aao = new AssessmentAccessObject();

    return aao.getAllAssessments(courseId);
  }

  /**
   * Create an Evaluation for this AssessmentTaken.
   *
   * @param assessmentTaken
   * @param evaluationType evaluationType
   * @param agent agent
   *
   * @return Evaluation with its Unique Id set.
   *
   * @throws osid.assessment.AssessmentException if there is a general failure
   *         or if the Type is unknown
   */
  public Evaluation createEvaluation(
    AssessmentTaken assessmentTaken, osid.shared.Type evaluationType,
    osid.shared.Agent agent)
    throws osid.assessment.AssessmentException
  {
    return null;
  }

  /**
   * Get all the Assessments taken.  Iterators return a set, one at a time. The
   * Iterator's hasNext method returns true if there are additional objects
   * available; false otherwise.  The Iterator's next method returns the next
   * object.
   *
   * @return AssessmentPublishedIterator  The order of the objects returned by
   *         the Iterator is not guaranteed.
   *
   * @throws osid.assessment.AssessmentException if there is a general failure
   */
  public AssessmentPublishedIterator getAssessmentsPublished()
    throws osid.assessment.AssessmentException
  {
    return null;
  }

  /**
   * Get all the Assessment Types.  Iterators return a set, one at a time.  The
   * Iterator's hasNext method returns true if there are additional objects
   * available; false otherwise.  The Iterator's next method returns the next
   * object.
   *
   * @return osid.shared.TypeIterator
   *
   * @throws osid.assessment.AssessmentException if there is a general failure
   */
  public osid.shared.TypeIterator getAssessmentTypes()
    throws osid.assessment.AssessmentException
  {
    return UtilAccessObject.getAllTypes("assessment");
  }

  /**
   * Get all the Section Types.  Iterators return a set, one at a time.  The
   * Iterator's hasNext method returns true if there are additional objects
   * available; false otherwise.  The Iterator's next method returns the next
   * object.
   *
   * @return osid.shared.TypeIterator
   *
   * @throws osid.assessment.AssessmentException if there is a general failure
   */
  public osid.shared.TypeIterator getSectionTypes()
    throws osid.assessment.AssessmentException
  {
    return null;
  }

  /**
   * Create AssessmentPublished using the Assessment
   *
   * @param assessment DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public AssessmentPublished createAssessmentPublished(Assessment assessment)
  {
    // --TO DO
    return null;
  }

  /**
   * Delete a specific AssessmentPublished
   *
   * @param assessmentPublishedId DOCUMENTATION PENDING
   */
  public void deleteAssessmentPublished(Id assessmentPublishedId)
  {
    // To DO
  }

  /**
   * Get the AssessmentPublished corresponding to the Id
   *
   * @param assessmentPublishedId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public AssessmentPublished getAssessmentPublished(Id assessmentPublishedId)
  {
    // -- TO DO
    return null;
  }

  /**
   * Get all the Response Types.  Iterators return a set, one at a time.  The
   * Iterator's hasNext method returns true if there are additional objects
   * available; false otherwise.  The Iterator's next method returns the next
   * object.
   *
   * @return osid.shared.TypeIterator
   *
   * @throws osid.assessment.AssessmentException if there is a general failure
   */
  public osid.shared.TypeIterator getItemTypes()
    throws osid.assessment.AssessmentException
  {
    return UtilAccessObject.getAllTypes("item");
  }

  /**
   * Get all the Evaluation Types.  Iterators return a set, one at a time.  The
   * Iterator's hasNext method returns true if there are additional objects
   * available; false otherwise.  The Iterator's next method returns the next
   * object.
   *
   * @return osid.shared.TypeIterator
   *
   * @throws osid.assessment.AssessmentException if there is a general failure
   */
  public osid.shared.TypeIterator getEvaluationTypes()
    throws osid.assessment.AssessmentException
  {
    return null;
  }
}
