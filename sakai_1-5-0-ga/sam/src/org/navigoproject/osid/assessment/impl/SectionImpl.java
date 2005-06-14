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
 * Created on Oct 27, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.osid.assessment.impl;

import org.navigoproject.business.entity.XmlStringBuffer;
import org.navigoproject.data.RepositoryManager;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import osid.assessment.AssessmentException;
import osid.assessment.Item;
import osid.assessment.ItemIterator;
import osid.assessment.Section;
import osid.assessment.SectionIterator;

import osid.dr.Asset;
import osid.dr.DigitalRepositoryException;

import osid.shared.Id;
import osid.shared.SharedException;
import osid.shared.Type;

/**
 * DOCUMENT ME!
 *
 * @author casong To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public class SectionImpl
  implements Section
{
  private final static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(SectionImpl.class);
  protected Asset asset;

  /**
   * Creates a new SectionImpl object.
   *
   * @param asset DOCUMENTATION PENDING
   *
   * @throws AssessmentException DOCUMENTATION PENDING
   */
  protected SectionImpl(Asset asset)
    throws AssessmentException
  {
    if(asset == null)
    {
      LOG.error(
        "SecttionImpl(asset): Unable to instantiate SectionImpl, asset is null");
      throw new AssessmentException(AssessmentException.NULL_ARGUMENT);
    }

    this.asset = asset;
  }

  /* (non-Javadoc)
   * @see osid.assessment.Section#updateDisplayName(java.lang.String)
   */
  public void updateDisplayName(String displayName)
    throws AssessmentException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("updateDisplayName(String " + displayName + ")");
    }

    try
    {
      this.asset.updateDisplayName(displayName);
    }
    catch(DigitalRepositoryException e)
    {
      LOG.error(
        "updateDisplayName(displayName): Unable to update display name. " +
        e.getMessage());
      throw new AssessmentException(AssessmentException.OPERATION_FAILED);
    }
  }

  /* (non-Javadoc)
   * @see osid.assessment.Section#updateDescription(java.lang.String)
   */
  public void updateDescription(String description)
    throws AssessmentException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("updateDescription(String " + description + ")");
    }

    try
    {
      this.asset.updateDescription(description);
    }
    catch(DigitalRepositoryException e)
    {
      LOG.error(
        "updateDescription(description): Unable to update description. " +
        e.getMessage());
      throw new AssessmentException(AssessmentException.OPERATION_FAILED);
    }
  }

  /* (non-Javadoc)
   * @see osid.assessment.Section#updateData(java.io.Serializable)
   */
  public void updateData(Serializable data)
    throws AssessmentException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("updateData(Serializable " + data + ")");
    }

    try
    {
      if(data instanceof String)
      {
        this.asset.updateContent(data);
      }
      else if(data instanceof XmlStringBuffer)
      {
        this.asset.updateContent(((XmlStringBuffer) data).stringValue());
      }
    }
    catch(DigitalRepositoryException e)
    {
      LOG.error("updateData(data): Unable to update data. " + e.getMessage());
      throw new AssessmentException(AssessmentException.OPERATION_FAILED);
    }
  }

  /* (non-Javadoc)
   * @see osid.assessment.Section#getDisplayName()
   */
  public String getDisplayName()
    throws AssessmentException
  {
    LOG.debug("getDisplayName()");
    String displayName;
    try
    {
      displayName = asset.getDisplayName();
    }
    catch(DigitalRepositoryException e)
    {
      LOG.error(
        "getDisplayName(): Unable to get display name. " + e.getMessage());
      throw new AssessmentException(AssessmentException.OPERATION_FAILED);
    }

    return displayName;
  }

  /* (non-Javadoc)
   * @see osid.assessment.Section#getDescription()
   */
  public String getDescription()
    throws AssessmentException
  {
    LOG.debug("getDescription()");
    String description;
    try
    {
      description = asset.getDescription();
    }
    catch(DigitalRepositoryException e)
    {
      LOG.error(
        "getDescription(): Unable to get description. " + e.getMessage());
      throw new AssessmentException(AssessmentException.OPERATION_FAILED);
    }

    return description;
  }

  /* (non-Javadoc)
   * @see osid.assessment.Section#getId()
   */
  public Id getId()
    throws AssessmentException
  {
    LOG.debug("getId()");
    Id id;
    try
    {
      id = asset.getId();
    }
    catch(DigitalRepositoryException e)
    {
      LOG.error("getId(): Unable to get Id. " + e.getMessage());
      throw new AssessmentException(AssessmentException.OPERATION_FAILED);
    }

    return id;
  }

  /* (non-Javadoc)
   * @see osid.assessment.Section#getSectionType()
   */
  public Type getSectionType()
    throws AssessmentException
  {
    LOG.debug("getSectionType()");
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.assessment.Section#getData()
   */
  public Serializable getData()
    throws AssessmentException
  {
    LOG.debug("getData()");
    Serializable data;
    try
    {
      data = asset.getContent();
      data = new org.navigoproject.business.entity.Section((String) data);
    }
    catch(DigitalRepositoryException e)
    {
      LOG.error("getData(): Unable to get data. " + e.getMessage());
      throw new AssessmentException(AssessmentException.OPERATION_FAILED);
    }

    return data;
  }

  /* (non-Javadoc)
   * @see osid.assessment.Section#addItem(osid.assessment.Item)
   */
  public void addItem(Item item)
    throws AssessmentException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("addItem(Item " + item + ")");
    }

    try
    {
      Id itemId = item.getId();
      org.navigoproject.business.entity.Section sXml = this.getSectionXml();
      sXml.addItemRef(itemId.getIdString());
      this.updateData(sXml.stringValue());
    }
    catch(SharedException e)
    {
      LOG.error("addItem(item): Unable to add item. " + e.getMessage());
      throw new AssessmentException(AssessmentException.OPERATION_FAILED);
    }
  }

  /* (non-Javadoc)
   * @see osid.assessment.Section#removeItem(osid.shared.Id)
   */
  public void removeItem(Id itemId)
    throws AssessmentException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("removeItem(Id " + itemId + ")");
    }

    try
    {
      org.navigoproject.business.entity.Section sXml = this.getSectionXml();
      sXml.removeItemRef(itemId.getIdString());
      this.updateData(sXml);
    }
    catch(SharedException e)
    {
      LOG.error("removeItem(): Unable to removeItem. " + e.getMessage());
      throw new AssessmentException(AssessmentException.OPERATION_FAILED);
    }
  }

  /* (non-Javadoc)
   * @see osid.assessment.Section#getItems()
   */
  public ItemIterator getItems()
    throws AssessmentException
  {
    LOG.debug("getItems()");
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.assessment.Section#orderItems(osid.assessment.Item[])
   */
  public void orderItems(Item[] items)
    throws AssessmentException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("orderItems(Item[] " + items + ")");
    }

    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.assessment.Assessment#addSection(osid.assessment.Section)
   */
  public void addSection(Section section)
    throws AssessmentException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("addSection(Section " + section + ")");
    }

    try
    {
      Id sectionId = section.getId();
      org.navigoproject.business.entity.Section sXml = this.getSectionXml();
      sXml.addSectionRef(sectionId.getIdString());
      this.updateData(sXml);
    }
    catch(SharedException e)
    {
      LOG.error("addSection(): Unable to add section. " + e.getMessage());
      throw new AssessmentException(AssessmentException.OPERATION_FAILED);
    }
  }

  /* (non-Javadoc)
   * @see osid.assessment.Assessment#removeSection(osid.shared.Id)
   */
  public void removeSection(Id sectionId)
    throws AssessmentException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("removeSection(Id " + sectionId + ")");
    }

    try
    {
      org.navigoproject.business.entity.Section sXml = this.getSectionXml();
      sXml.removeSectionRef(sectionId.getIdString());
      this.updateData(sXml);
    }
    catch(SharedException e)
    {
      LOG.error(
        "removeSection(sectionId): Unable to remove section. " +
        e.getMessage());
      throw new AssessmentException(AssessmentException.OPERATION_FAILED);
    }
  }

  /**
   * @see osid.assessment.Assessment#getSections()
   */
  public SectionIterator getSections()
    throws AssessmentException
  {
    LOG.debug("getSections()");
    org.navigoproject.business.entity.Section sectionXml = this.getSectionXml();
    List sectionRefIds = sectionXml.getSectionRefIds();
    int size = sectionRefIds.size();
    ArrayList sections = new ArrayList();
    RepositoryManager rm = new RepositoryManager();
    SectionIterator sectionIterator = null;
    try
    {
      for(int i = 0; i < size; i++)
      {
        Id id = rm.getId((String) (sectionRefIds.get(i)));
        Asset asset = rm.getAsset(id);
        sections.add(asset);
      }

      sectionIterator = new SectionIteratorImpl(sections.iterator());
    }
    catch(DigitalRepositoryException e)
    {
      LOG.error(e.getMessage(), e);
    }

    return sectionIterator;
  }

  /* (non-Javadoc)
   * @see osid.assessment.Section#orderSections(osid.assessment.Section[])
   */
  public void orderSections(Section[] sections)
    throws AssessmentException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("orderSections(Section[] " + sections + ")");
    }

    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws AssessmentException DOCUMENTATION PENDING
   */
  private org.navigoproject.business.entity.Section getSectionXml()
    throws AssessmentException
  {
    Serializable assetContent;
    org.navigoproject.business.entity.Section sXml = null;
    try
    {
      assetContent = this.asset.getContent();
      if(assetContent instanceof org.navigoproject.business.entity.Section)
      {
        sXml = (org.navigoproject.business.entity.Section) assetContent;
      }
      else
      {
        if(assetContent instanceof XmlStringBuffer)
        {
          sXml =
            new org.navigoproject.business.entity.Section(
              ((XmlStringBuffer) assetContent).stringValue());
        }
        else
        {
          sXml =
            new org.navigoproject.business.entity.Section(
              (String) assetContent);
        }
      }
    }
    catch(DigitalRepositoryException e)
    {
      LOG.error(
        "getSectionXml(): Unable to get Section Xml content. " +
        e.getMessage());
      throw new AssessmentException(AssessmentException.OPERATION_FAILED);
    }

    return sXml;
  }
}
