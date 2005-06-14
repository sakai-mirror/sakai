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

package org.navigoproject.business.entity.evaluation.model;

import org.navigoproject.business.entity.assessment.model.MediaData;

import java.io.Serializable;

import java.util.Date;

import org.apache.log4j.Logger;

/**
 * This combines what agent info is available with course info and
 * actual results.
 *
 * @author Rachel Gollub <rgollub@stanford.edu>
 */
public class AgentResults
  implements Serializable
{
  private String firstName;

  /** Use serialVersionUID for interoperability. */
  private final static long serialVersionUID = -572820583562954275L;
  private String lastName;
  private String agentId;

  /**
   * @deprecated unused in latest UI
   */
  private String email;
  private String role;
  private String section;
  private String submissionDate;
  private Date submissionAsDate;
  private int lateSubmission;
  private String sortableDate;
  private String totalScore;
  private String adjustmentTotalScore;
  private String totalScoreComments;
  private String finalScore;
  private String rationale;
  private String comments;
  private String response; // text of student response, if any

  private String assessmentID;
  private String assessmentResultID;
  private String itemID;
  private MediaData mediaData;
  private static Logger logger = Logger.getLogger(AgentResults.class.getName());

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getFirstName()
  {
    return firstName;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pfirstName DOCUMENTATION PENDING
   */
  public void setFirstName(String pfirstName)
  {
    firstName = pfirstName;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getLastName()
  {
    return lastName;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param plastName DOCUMENTATION PENDING
   */
  public void setLastName(String plastName)
  {
    lastName = plastName;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getLastInitial()
  {
    if((lastName != null) && (lastName.length() > 0))
    {
      return lastName.substring(0, 1).toUpperCase();
    }

    return " ";
  }

  /**
   * @deprecated use getAgentId()
   *
   */
  public String getSunetid()
  {
    return agentId;
  }

  /**
   * @deprecated use setAgentId()
   *
   */
  public void setSunetid(String pagentId)
  {
    agentId = pagentId;
  }

  /**
   * Get agent id.
   * @return agent id
   */
  public String getAgentId()
  {
    return agentId;
  }

  /**
   * Set agent id.
   * @param pagentId the id
   */
  public void setAgentId(String pagentId)
  {
    agentId = pagentId;
  }

  /**
   * @deprecated
   */
  public String getEmail()
  {
    return email;
  }

  /**
   * @deprecated
   */
  public void setEmail(String pemail)
  {
    email = pemail;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getRole()
  {
    return role;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param prole DOCUMENTATION PENDING
   */
  public void setRole(String prole)
  {
    role = prole;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getSection()
  {
    return section;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param psection DOCUMENTATION PENDING
   */
  public void setSection(String psection)
  {
    section = psection;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getSubmissionDate()
  {
    return submissionDate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param psubmissionDate DOCUMENTATION PENDING
   */
  public void setSubmissionDate(String psubmissionDate)
  {
    submissionDate = psubmissionDate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Date getSubmissionAsDate()
  {
    return submissionAsDate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param psubmissionAsDate DOCUMENTATION PENDING
   */
  public void setSubmissionAsDate(Date psubmissionAsDate)
  {
    submissionAsDate = psubmissionAsDate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getSortableDate()
  {
    return sortableDate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pDate DOCUMENTATION PENDING
   */
  public void setSortableDate(String pDate)
  {
    sortableDate = pDate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTotalScore()
  {
    return totalScore;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param ptotalScore DOCUMENTATION PENDING
   */
  public void setTotalScore(String ptotalScore)
  {
    totalScore = ptotalScore;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getAdjustmentTotalScore()
  {
    return adjustmentTotalScore;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param padjustmentTotalScore DOCUMENTATION PENDING
   */
  public void setAdjustmentTotalScore(String padjustmentTotalScore)
  {
    adjustmentTotalScore = padjustmentTotalScore;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTotalScoreComments()
  {
    return totalScoreComments;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pTotalScoreComments DOCUMENTATION PENDING
   */
  public void setTotalScoreComments(String pTotalScoreComments)
  {
    totalScoreComments = pTotalScoreComments;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getFinalScore()
  {
    int ts = 0;
    int ats = 0;

    try
    {
      Integer its = new Integer(totalScore);
      ts = its.intValue();
    }
    catch(Exception e)
    {
      ;
    }

    try
    {
      Integer iats = new Integer(adjustmentTotalScore);
      ats = iats.intValue();
    }
    catch(Exception e)
    {
      ;
    }

    finalScore = "" + (ts + ats);

    return finalScore;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pfinalScore DOCUMENTATION PENDING
   */
  public void setFinalScore(String pfinalScore)
  {
    finalScore = pfinalScore;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getRationale()
  {
    return rationale;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param prationale DOCUMENTATION PENDING
   */
  public void setRationale(String prationale)
  {
    rationale = prationale;
  }
  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getResponse()
  {
    return response;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pResults DOCUMENTATION PENDING
   */
  public void setResponse(String presponse)
  {
    response = presponse;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public MediaData getMediaData()
  {
    return mediaData;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pData DOCUMENTATION PENDING
   */
  public void setMediaData(MediaData pData)
  {
    mediaData = pData;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getComments()
  {
    return comments;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pcomments DOCUMENTATION PENDING
   */
  public void setComments(String pcomments)
  {
    comments = pcomments;
  }



  /**
   * Set the assessment id, which is now a UID.
   * The String assessmentID property obsoletes the long assessmentId property.
   * @param pid the id
   */
  public void setAssessmentID(String pid)
  {
    assessmentID = pid;
  }

  /**
   * Get the assessment id, which is now a UID.
   * @return the assessment id.
   */
  public String getAssessmentID()
  {
    return assessmentID;
  }

  /**
   * Set the item id, which is now a UID.
   * The String itemID property obsoletes the long itemTakenId property.
   * An item taken is represented by an assessment result id and item id.
   * @param pid the id
   */
  public void setItemID(String pid)
  {
    itemID = pid;
  }

  /**
   * Get the item id, which is now a UID.
   * The String itemID property obsoletes the long itemTakenId property.
   * An item taken is represented by an assessment result id and item id.
   * @return the item id.
   */
  public String getItemID()
  {
    return itemID;
  }

  /**
   * Set the assessment result id, which is now a UID.
   * The String assessmentResultID property obsoletes the long
   * assessmentTakenId property.
   * @param pid the id
   */
  public void setAssessmentResultID(String pid)
  {
    assessmentResultID = pid;
  }

  /**
   * Get the assessment id, which is now a UID.
   * @return the assessment id.
   */
  public String getAssessmentResultID()
  {
    return assessmentResultID;
  }


  /**
   * Set the lateSubmission. 1 = late, 0 = not late  .
   * @param plateSubmission
   */
  public void setLateSubmission(int plateSubmission)
  {
    lateSubmission = plateSubmission;
  }

  /**
   * Get the lateSubmission. 1 = late, 0 = not late  .
   * @return lateSubmission.
   */
  public int getLateSubmission()
  {
    return lateSubmission;
  }
}
