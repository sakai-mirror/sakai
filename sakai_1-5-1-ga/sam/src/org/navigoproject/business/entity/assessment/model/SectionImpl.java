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

import org.navigoproject.osid.OsidManagerFactory;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import osid.assessment.Item;
import osid.assessment.ItemIterator;
import osid.assessment.Section;
import osid.assessment.SectionIterator;

import osid.shared.Id;
import osid.shared.SharedManager;
import osid.shared.Type;

//import org.navigoproject.business.entity.SectionTemplate;
//import org.navigoproject.business.entity.Objective;
//import org.navigoproject.business.entity.Rubric;

/**
 * This keeps track of which group to distribute results to, and how to
 * distribute those results.  A collection of these is held in the assessment.
 *
 * @author Rachel Gollub
 */
public class SectionImpl
  implements Section
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(SectionImpl.class);
  private String name;
  private String description;
  private Serializable data = new SectionPropertiesImpl();
  private Id okiId;

  // Section data
  private Collection sections;

  /**
   * Creates a new SectionImpl object.
   */
  public SectionImpl()
  {
    try
    {
      SharedManager sm = OsidManagerFactory.createSharedManager();
      Id id = sm.createId();
      sections = new ArrayList();
      okiId = id;
    }
    catch(Exception e)
    {
      // TODO How to handle this exception? LDS
      LOG.error(e.getMessage(), e);
      throw new Error(e);
    }
  }

  /**
   *
   */
  public SectionImpl(Id pid)
  {
    sections = new ArrayList();
    okiId = pid;
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public String getDisplayName()
  {
    return name;
  }

  /**
   * DOCUMENT ME!
   *
   * @param pname
   */
  public void updateDisplayName(String pname)
  {
    name = pname;
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * DOCUMENT ME!
   *
   * @param pdescription
   */
  public void updateDescription(String pdescription)
  {
    description = pdescription;
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public Serializable getData()
  {
    return data;
  }

  /**
   * DOCUMENT ME!
   *
   * @param pdata
   */
  public void updateData(Serializable pdata)
  {
    data = pdata;
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   *
   * @throws osid.assessment.AssessmentException
   */
  public Id getId()
    throws osid.assessment.AssessmentException
  {
    return okiId;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public Type getSectionType()
    throws osid.assessment.AssessmentException
  {
    return ((SectionPropertiesImpl) data).getSectionType();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getName()
  {
    return name;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pname DOCUMENTATION PENDING
   */
  public void setName(String pname)
  {
    name = pname;
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
    ((SectionPropertiesImpl) section).setParentSection(this);
    ((SectionPropertiesImpl) section).setParentSectionId(this.getId());
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
      Section section = (Section) iter.next();
      if(section.getId().equals(sectionId))
      {
        sections.remove(section);

        break;
      }
    }
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   *
   * @throws osid.assessment.AssessmentException
   */
  public SectionIterator getSections()
    throws osid.assessment.AssessmentException
  {
    return new SectionIteratorImpl(sections);
  }

  /**
   * DOCUMENT ME!
   *
   * @param psections
   *
   * @throws osid.assessment.AssessmentException
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

  /**
   * DOCUMENT ME!
   *
   * @param item
   *
   * @throws osid.assessment.AssessmentException
   */
  public void addItem(Item item)
    throws osid.assessment.AssessmentException
  {
    Collection items = ((SectionPropertiesImpl) data).getItemList();
    items.add(item);
    ((SectionPropertiesImpl) data).setItemList(items);
  }

  /**
   * DOCUMENT ME!
   *
   * @param itemId
   *
   * @throws osid.assessment.AssessmentException
   */
  public void removeItem(osid.shared.Id itemId)
    throws osid.assessment.AssessmentException
  {
    try
    {
      Collection items = ((SectionPropertiesImpl) data).getItemList();
      Iterator iter = items.iterator();
      while(iter.hasNext())
      {
        Item item = (Item) iter.next();
        if(item.getId().isEqual(itemId))
        {
          items.remove(item);

          break;
        }
      }

      ((SectionPropertiesImpl) data).setItemList(items);
    }
    catch(Exception e)
    {
      throw new osid.assessment.AssessmentException(e.getMessage());
    }
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   *
   * @throws osid.assessment.AssessmentException
   */
  public ItemIterator getItems()
    throws osid.assessment.AssessmentException
  {
    return new ItemIteratorImpl(((SectionPropertiesImpl) data).getItemList());
  }

  /**
   * DOCUMENT ME!
   *
   * @param pitems
   *
   * @throws osid.assessment.AssessmentException
   */
  public void orderItems(Item[] pitems)
    throws osid.assessment.AssessmentException
  {
    Collection items = new ArrayList();
    for(int i = 0; i < pitems.length; i++)
    {
      items.add(pitems[i]);
    }

    ((SectionPropertiesImpl) data).setItemList(items);
  }
}
