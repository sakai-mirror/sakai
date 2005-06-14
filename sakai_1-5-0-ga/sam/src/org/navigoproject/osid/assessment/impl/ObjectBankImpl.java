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
 * Created on Dec 11, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.osid.assessment.impl;

import org.navigoproject.business.entity.XmlStringBuffer;

import java.io.Serializable;

import osid.assessment.AssessmentException;
import osid.assessment.Item;
import osid.assessment.Section;

import osid.dr.Asset;
import osid.dr.DigitalRepositoryException;

import osid.shared.Id;
import osid.shared.SharedException;

/**
 * @author casong
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ObjectBankImpl
  extends SectionImpl
{
  private static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(ObjectBankImpl.class);

  /**
   * @param asset
   * @throws AssessmentException
   */
  protected ObjectBankImpl(Asset asset)
    throws AssessmentException
  {
    super(asset);

    // TODO Auto-generated constructor stub
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
      data = new org.navigoproject.business.entity.ObjectBank((String) data);
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
      org.navigoproject.business.entity.ObjectBank xml =
        this.getObjectBankXml();
      xml.addItemRef(itemId.getIdString());
      this.updateData(xml.stringValue());
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
      org.navigoproject.business.entity.ObjectBank xml =
        this.getObjectBankXml();
      xml.removeItemRef(itemId.getIdString());
      this.updateData(xml);
    }
    catch(SharedException e)
    {
      LOG.error("removeItem(): Unable to removeItem. " + e.getMessage());
      throw new AssessmentException(AssessmentException.OPERATION_FAILED);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param section DOCUMENTATION PENDING
   *
   * @throws AssessmentException DOCUMENTATION PENDING
   */
  public void addSection(Section section)
    throws AssessmentException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("addSection(Section " + section + ")");
    }

    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param sectionId DOCUMENTATION PENDING
   *
   * @throws AssessmentException DOCUMENTATION PENDING
   */
  public void removeSection(Id sectionId)
    throws AssessmentException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("removeSection(Id " + sectionId + ")");
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
  private org.navigoproject.business.entity.ObjectBank getObjectBankXml()
    throws AssessmentException
  {
    Serializable assetContent;
    org.navigoproject.business.entity.ObjectBank xml = null;
    try
    {
      assetContent = this.asset.getContent();
      if(assetContent instanceof org.navigoproject.business.entity.Section)
      {
        xml = (org.navigoproject.business.entity.ObjectBank) assetContent;
      }
      else
      {
        if(assetContent instanceof XmlStringBuffer)
        {
          xml =
            new org.navigoproject.business.entity.ObjectBank(
              ((XmlStringBuffer) assetContent).stringValue());
        }
        else
        {
          xml =
            new org.navigoproject.business.entity.ObjectBank(
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

    return xml;
  }
}
