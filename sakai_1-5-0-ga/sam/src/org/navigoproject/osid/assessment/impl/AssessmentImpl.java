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

package org.navigoproject.osid.assessment.impl;

import org.navigoproject.business.entity.XmlStringBuffer;
import org.navigoproject.data.RepositoryManager;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import osid.assessment.Assessment;
import osid.assessment.AssessmentException;
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
 * @author casong
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id: AssessmentImpl.java,v 1.2 2004/09/24 05:37:12 rgollub.stanford.edu Exp $
 */
public class AssessmentImpl
  implements Assessment
{
  private final static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(AssessmentImpl.class);
  private Asset asset;

  /**
   * Creates a new AssessmentImpl object.
   *
   * @param asset DOCUMENTATION PENDING
   *
   * @throws AssessmentException DOCUMENTATION PENDING
   */
  public AssessmentImpl(Asset asset)
    throws AssessmentException
  {
    if(asset == null)
    {
      throw new AssessmentException(AssessmentException.NULL_ARGUMENT);
    }

    this.asset = asset;
  }

  /**
   * @see osid.assessment.Assessment#updateDisplayName(java.lang.String)
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
        "updateDisplayName(displayName): Unable to update display name: " +
        e.getMessage());
      throw new AssessmentException(AssessmentException.OPERATION_FAILED);
    }
  }

  /**
   * @see osid.assessment.Assessment#updateDescription(java.lang.String)
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
      LOG.debug(
        "updateDescription(description): Unable to update description: " +
        e.getMessage());
      throw new AssessmentException(AssessmentException.OPERATION_FAILED);
    }
  }

  /**
   * @see osid.assessment.Assessment#updateTopic(java.lang.String)
   */
  public void updateTopic(String arg0)
    throws AssessmentException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("updateTopic(String " + arg0 + ")");
    }

    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /**
   * @see osid.assessment.Assessment#updateData(java.io.Serializable)
   */
  public void updateData(Serializable data)
    throws AssessmentException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("updateData(Serializable)");
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
      LOG.error(
        "updateData(data): Unable to update assessment data: " +
        e.getMessage());
      throw new AssessmentException(AssessmentException.OPERATION_FAILED);
    }
  }

  /**
   * @see osid.assessment.Assessment#getDisplayName()
   */
  public String getDisplayName()
    throws AssessmentException
  {
    LOG.debug("getDisplayName()");
    String displayName = "";
    try
    {
      displayName = this.asset.getDisplayName();
    }
    catch(DigitalRepositoryException e)
    {
      LOG.error(
        "getDisplayName(): Unable to get assessment displayName: " +
        e.getMessage());
      throw new AssessmentException(AssessmentException.OPERATION_FAILED);
    }

    return displayName;
  }

  /**
   * @see osid.assessment.Assessment#getDescription()
   */
  public String getDescription()
    throws AssessmentException
  {
    LOG.debug("getDescription()");
    String description = "";

    try
    {
      description = this.asset.getDescription();
    }
    catch(DigitalRepositoryException e)
    {
      LOG.error(
        "getDisplayName(): Unable to get assessment description: " +
        e.getMessage());
      throw new AssessmentException(AssessmentException.OPERATION_FAILED);
    }

    return description;
  }

  /**
   * @see osid.assessment.Assessment#getId()
   */
  public Id getId()
    throws AssessmentException
  {
    LOG.debug("getId()");
    Id id = null;
    try
    {
      id = this.asset.getId();
    }
    catch(DigitalRepositoryException e)
    {
      LOG.error("getId(): Unable to get assessment id: " + e.getMessage());
      throw new AssessmentException(AssessmentException.OPERATION_FAILED);
    }

    return id;
  }

  /**
   * @see osid.assessment.Assessment#getAssessmentType()
   */
  public Type getAssessmentType()
    throws AssessmentException
  {
    LOG.debug("getAssessmentType()");
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /**
   * @see osid.assessment.Assessment#getTopic()
   */
  public String getTopic()
    throws AssessmentException
  {
    LOG.debug("getTopic()");
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /**
   * @see osid.assessment.Assessment#getData()
   */
  public Serializable getData()
    throws AssessmentException
  {
    Serializable data;
    LOG.debug("getData()");
    try
    {
      data = this.asset.getContent();
      data = new org.navigoproject.business.entity.Assessment((String) data);
    }
    catch(DigitalRepositoryException e)
    {
      LOG.error(
        "getData(): Unable to get assessment content data: " + e.getMessage());
      throw new AssessmentException(AssessmentException.OPERATION_FAILED);
    }

    return data;
  }

  /**
   * @see osid.assessment.Assessment#addSection(osid.assessment.Section)
   */
  public void addSection(Section section)
    throws AssessmentException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("addSection(Section " + section + ")");
    }

    if(section == null)
    {
      LOG.error("addSection(section): NULL_ARGUMENT");
      throw new AssessmentException(AssessmentException.NULL_ARGUMENT);
    }

    try
    {
      org.navigoproject.business.entity.Assessment aXml = this.getAssessment();
      aXml.addSectionRef(section.getId().getIdString());
      this.updateData(aXml.stringValue());
    }
    catch(SharedException e)
    {
      LOG.debug(
        "addSection(section): Unable to get section id: " + e.getMessage());
      e.printStackTrace();
      throw new AssessmentException(AssessmentException.OPERATION_FAILED);
    } 
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  /**
   * @see osid.assessment.Assessment#removeSection(osid.shared.Id)
   */
  public void removeSection(Id sectionId)
    throws AssessmentException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("removeSection(Id " + sectionId + ")");
    }

    if(sectionId == null)
    {
      LOG.error("removeSection(sectionId): NULL_ARGUMENT");
      throw new AssessmentException(AssessmentException.NULL_ARGUMENT);
    }

    try
    {
      org.navigoproject.business.entity.Assessment aXml = this.getAssessment();
      aXml.removeSectionRef(sectionId.getIdString());
      this.updateData(aXml);
    }
    catch(SharedException e)
    {
      LOG.debug(
        "removeSection(sectionId): Unable to get section id: " +
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
    org.navigoproject.business.entity.Assessment assessmentXml =
      this.getAssessment();
    List sectionRefIds = assessmentXml.getSectionRefIds();
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

  /**
   * @see osid.assessment.Assessment#orderSections(osid.assessment.Section[])
   */
  public void orderSections(Section[] sections)
    throws AssessmentException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("orderSections(" + sections + ")");
    }

    this.removeSections();
    int size = sections.length;
    for(int i = 0; i < size; i++)
    {
      Section section = sections[i];
      this.addSection(section);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws AssessmentException DOCUMENTATION PENDING
   */
  private void removeSections()
    throws AssessmentException
  {
    org.navigoproject.business.entity.Assessment aXml = this.getAssessment();
    aXml.removeSectionRefs();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws AssessmentException DOCUMENTATION PENDING
   */
  private org.navigoproject.business.entity.Assessment getAssessment()
    throws AssessmentException
  {
    org.navigoproject.business.entity.Assessment aXml = null;
    try
    {
      Serializable assetContent = this.asset.getContent();
			//LOG.debug("printning asset content: " + (String) assetContent);
      if(assetContent instanceof org.navigoproject.business.entity.Assessment)
      {
        aXml = (org.navigoproject.business.entity.Assessment) assetContent;
      }
      else
      {
        if(assetContent instanceof XmlStringBuffer)
        {        	
          aXml =
            new org.navigoproject.business.entity.Assessment(
              ((XmlStringBuffer) assetContent).stringValue());
        }
        else
        {        	
          aXml =
            new org.navigoproject.business.entity.Assessment(
              (String) assetContent);
        }
      }
    }
    catch(DigitalRepositoryException e)
    {
      LOG.debug(e.getMessage(), e);
      e.printStackTrace();
      throw new AssessmentException(
        "Unable to get Assessment Xml content." + e.getMessage());
    }

    return aXml;
  }
}
