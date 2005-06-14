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

package org.navigoproject.business.entity.assessment.model;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import osid.assessment.Assessment;
import osid.assessment.Section;
import osid.assessment.SectionIterator;

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
 * @author Rachel Gollub
 * @author Sherwin Lu
 * @version 1.0
 */
public class AssessmentImpl
  implements Assessment
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(AssessmentImpl.class);
    
  // OKI data
  private Integer assessmentTypeId;
  private Serializable data = new AssessmentPropertiesImpl();
  private String topic;
  private String name;
  private String description;
  private int assessmentPropertiesId;

  /* the special attribute telling OJB the object's concrete type.
   * NOTE: this attribute MUST be called ojbConcreteClass
   */

  //private String ojbConcreteClass;
  // Assessment Sections
  private Collection sections;

  /**
   * Creates a new AssessmentImpl object.
   */
  public AssessmentImpl()
  {
    this(null);
    LOG.debug("WHO is calling me??");

    //this.ojbConcreteClass = this.getClass().getName();
  }

  /**
   * Creates a new AssessmentImpl object.
   *
   * @param id DOCUMENTATION PENDING
   */
  public AssessmentImpl(Id id)
  {
    ((AssessmentPropertiesImpl) data).setAssessmentId(id);
    LOG.debug("AssessmentImpl constructor Id:" + id);
    sections = new ArrayList();

    //this.ojbConcreteClass = this.getClass().getName();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getDisplayName()
  {
    return name;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pname DOCUMENTATION PENDING
   */
  public void updateDisplayName(String pname)
  {
    name = pname;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public Id getId()
    throws osid.assessment.AssessmentException
  {
    return ((AssessmentPropertiesImpl) data).getAssessmentId();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public Type getAssessmentType()
    throws osid.assessment.AssessmentException
  {
    return ((AssessmentPropertiesImpl) data).getType();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Serializable getData()
  {
    return data;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pdata DOCUMENTATION PENDING
   */
  public void updateData(Serializable pdata)
  {
    // Need to be watchful of the AssessmentId !
    // Probably it is not such a good idea to keep it in the properties 
    Id tempId = null;
    if(data != null)
    {
      tempId = ((AssessmentPropertiesImpl) data).getAssessmentId();
    }

    data = pdata;
    if(tempId != null)
    {
      if(data == null)
      {
        data = new AssessmentPropertiesImpl();
      }

      ((AssessmentPropertiesImpl) data).setAssessmentId(tempId);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTopic()
  {
    return topic;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param ptopic DOCUMENTATION PENDING
   */
  public void updateTopic(String ptopic)
  {
    topic = ptopic;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pdescription DOCUMENTATION PENDING
   */
  public void updateDescription(String pdescription)
  {
    description = pdescription;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param section DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public void addSection(Section section)
    throws osid.assessment.AssessmentException
  {
    sections.add(section);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param sectionId DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public void removeSection(osid.shared.Id sectionId)
    throws osid.assessment.AssessmentException
  {
    Iterator iter = sections.iterator();
    while(iter.hasNext())
    {
      try
      {
        Section section = (Section) iter.next();
        if(section.getId().isEqual(sectionId))
        {
          sections.remove(section);

          break;
        }
      }
      catch(Exception e)
      {
        LOG.error(e);
        throw new Error(e);
      }
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public SectionIterator getSections()
    throws osid.assessment.AssessmentException
  {
    return new SectionIteratorImpl(sections);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param psections DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public void orderSections(Section[] psections)
    throws osid.assessment.AssessmentException
  {
    sections = new ArrayList();
    for(int i = 0; i < psections.length; i++)
    {
      sections.add(psections[i]);
    }
  }
}
