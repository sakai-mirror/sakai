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
 * Created on Oct 29, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.osid.assessment.impl;

import java.io.Serializable;

import java.util.Calendar;

import osid.assessment.AssessmentException;
import osid.assessment.AssessmentManager;
import osid.assessment.AssessmentPublished;
import osid.assessment.AssessmentTaken;
import osid.assessment.Evaluation;
import osid.assessment.EvaluationIterator;
import osid.assessment.Section;
import osid.assessment.SectionTaken;
import osid.assessment.SectionTakenIterator;

import osid.dr.Asset;
import osid.dr.DigitalRepositoryException;

import osid.shared.Agent;
import osid.shared.Id;
import osid.shared.Type;

/**
 * @author casong
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AssessmentTakenImpl
  implements AssessmentTaken
{
  private Asset asset;
  private Agent agent;

  /**
   * Creates a new AssessmentTakenImpl object.
   *
   * @param asset DOCUMENTATION PENDING
   */
  public AssessmentTakenImpl(Asset asset)
  {
    this.asset = asset;
    this.agent = null;
  }

  /**
   * Creates a new AssessmentTakenImpl object.
   *
   * @param asset DOCUMENTATION PENDING
   * @param agent DOCUMENTATION PENDING
   */
  public AssessmentTakenImpl(Asset asset, Agent agent)
  {
    this.asset = asset;
    this.agent = agent;
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentTaken#updateData(java.io.Serializable)
   */
  public void updateData(Serializable data)
    throws AssessmentException
  {
    try
    {
      this.asset.updateContent(data);
    }
    catch (DigitalRepositoryException e)
    {
      throw new AssessmentException(AssessmentException.OPERATION_FAILED
      + "Unable to update asset content" + e.getMessage());
    }
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentTaken#getId()
   */
  public Id getId()
    throws AssessmentException
  {
    Id id;
    try
    {
      id = asset.getId();
    }
    catch(DigitalRepositoryException e)
    {
      throw new AssessmentException(AssessmentException.OPERATION_FAILED +
        "Unable to get assessment taken id. " + e.getMessage());
    }

    return id;
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentTaken#getAssessmentPublished()
   */
  public AssessmentPublished getAssessmentPublished()
    throws AssessmentException
  {
    // check QTI_ASSESSMENT_TAKEN table for assessmentPublished Id.
    Id id = null;
    AssessmentManager assessmentManager = new AssessmentManagerImpl();
    assessmentManager.getAssessmentPublished(id);
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentTaken#getAgent()
   */
  public Agent getAgent()
    throws AssessmentException
  {
    return agent;
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentTaken#getDate()
   */
  public Calendar getDate()
    throws AssessmentException
  {
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentTaken#getData()
   */
  public Serializable getData()
    throws AssessmentException
  {
    Serializable data;
    try
    {
      data = asset.getContent();
    }
    catch(DigitalRepositoryException e)
    {
      throw new AssessmentException(
        "Unable to get assessment taken data. " + e.getMessage());
    }

    return data;
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentTaken#createSectionTaken(osid.assessment.Section)
   */
  public SectionTaken createSectionTaken(Section arg0)
    throws AssessmentException
  {
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentTaken#deleteSectionTaken(osid.shared.Id)
   */
  public void deleteSectionTaken(Id arg0)
    throws AssessmentException
  {
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentTaken#getSectionsTaken()
   */
  public SectionTakenIterator getSectionsTaken()
    throws AssessmentException
  {
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentTaken#createEvaluation(osid.shared.Type)
   */
  public Evaluation createEvaluation(Type arg0)
    throws AssessmentException
  {
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentTaken#deleteEvaluation(osid.shared.Id)
   */
  public void deleteEvaluation(Id arg0)
    throws AssessmentException
  {
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentTaken#getEvaluationsByType(osid.shared.Type)
   */
  public EvaluationIterator getEvaluationsByType(Type arg0)
    throws AssessmentException
  {
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentTaken#getEvaluations()
   */
  public EvaluationIterator getEvaluations()
    throws AssessmentException
  {
    throw new AssessmentException(AssessmentException.UNIMPLEMENTED);
  }
}
