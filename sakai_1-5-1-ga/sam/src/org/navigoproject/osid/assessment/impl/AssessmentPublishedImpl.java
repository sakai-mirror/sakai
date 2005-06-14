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

import osid.assessment.Assessment;
import osid.assessment.AssessmentException;
import osid.assessment.AssessmentPublished;
import osid.assessment.AssessmentTaken;
import osid.assessment.AssessmentTakenIterator;
import osid.assessment.EvaluationIterator;

import osid.shared.Agent;
import osid.shared.Id;
import osid.shared.Type;

/**
 * DOCUMENT ME!
 *
 * @author casong To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public class AssessmentPublishedImpl
  implements AssessmentPublished
{
  private final static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(AssessmentPublishedImpl.class);
  private Assessment assessment;
  private Calendar date;

  /**
   * DOCUMENT ME!
   *
   * @param assessment
   * @param date
   */
  public AssessmentPublishedImpl(Assessment assessment, Calendar date)
  {
    this.assessment = assessment;
    this.date = date;
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentPublished#updateDisplayName(java.lang.String)
   */
  public void updateDisplayName(String displayName)
    throws AssessmentException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("updateDisplayName(String " + displayName + ")");
    }

    assessment.updateDisplayName(displayName);
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentPublished#updateDescription(java.lang.String)
   */
  public void updateDescription(String description)
    throws AssessmentException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("updateDescription(" + description + ")");
    }

    assessment.updateDescription(description);
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentPublished#updateGradingAssignmentId(osid.shared.Id)
   */
  public void updateGradingAssignmentId(Id arg0)
    throws AssessmentException
  {
    throw new AssessmentException("Method not supported.");
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentPublished#updateCourseSectionId(osid.shared.Id)
   */
  public void updateCourseSectionId(Id arg0)
    throws AssessmentException
  {
    // TODO Auto-generated method stub
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentPublished#updateData(java.io.Serializable)
   */
  public void updateData(Serializable data)
    throws AssessmentException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("updateData(Serializable " + data + ")");
    }

    assessment.updateData(data);
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentPublished#getDisplayName()
   */
  public String getDisplayName()
    throws AssessmentException
  {
    LOG.debug("getDisplayName()");

    return assessment.getDisplayName();
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentPublished#getDescription()
   */
  public String getDescription()
    throws AssessmentException
  {
    LOG.debug("getDescription()");

    return assessment.getDescription();
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentPublished#getId()
   */
  public Id getId()
    throws AssessmentException
  {
    LOG.debug("getId()");

    return assessment.getId();
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentPublished#getDate()
   */
  public Calendar getDate()
    throws AssessmentException
  {
    LOG.debug("getDate()");

    return date;
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentPublished#getAssessment()
   */
  public Assessment getAssessment()
    throws AssessmentException
  {
    LOG.debug("getAssessment()");

    return assessment;
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentPublished#getGradingAssignmentId()
   */
  public Id getGradingAssignmentId()
    throws AssessmentException
  {
    throw new AssessmentException("Method not supported.");
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentPublished#getCourseSectionId()
   */
  public Id getCourseSectionId()
    throws AssessmentException
  {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentPublished#getData()
   */
  public Serializable getData()
    throws AssessmentException
  {
    LOG.debug("getData()");

    return assessment.getData();
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentPublished#createAssessmentTaken(osid.shared.Agent)
   */
  public AssessmentTaken createAssessmentTaken(Agent arg0)
    throws AssessmentException
  {
    //get assessment by a certain date.
    //apply selection and ordering.
    //save the data into the DR.
    throw new AssessmentException("Method not supported.");
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentPublished#deleteAssessmentTaken(osid.shared.Id)
   */
  public void deleteAssessmentTaken(Id arg0)
    throws AssessmentException
  {
    throw new AssessmentException("Method not supported.");
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentPublished#getAssessmentsTaken()
   */
  public AssessmentTakenIterator getAssessmentsTaken()
    throws AssessmentException
  {
    throw new AssessmentException("Method not supported.");
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentPublished#getAssessmentsTakenBy(osid.shared.Id)
   */
  public AssessmentTakenIterator getAssessmentsTakenBy(Id arg0)
    throws AssessmentException
  {
    throw new AssessmentException("Method not supported.");
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentPublished#getEvaluations()
   */
  public EvaluationIterator getEvaluations()
    throws AssessmentException
  {
    throw new AssessmentException("Method not supported.");
  }

  /* (non-Javadoc)
   * @see osid.assessment.AssessmentPublished#getEvaluationsByType(osid.shared.Type)
   */
  public EvaluationIterator getEvaluationsByType(Type arg0)
    throws AssessmentException
  {
    throw new AssessmentException("Method not supported.");
  }
}
