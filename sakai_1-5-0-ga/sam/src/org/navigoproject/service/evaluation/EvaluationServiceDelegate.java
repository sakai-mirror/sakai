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

package org.navigoproject.service.evaluation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.CreateException;

import javax.servlet.ServletContext;

import org.w3c.dom.Document;

import osid.assessment.AssessmentException;

import org.navigoproject.data.dao.GradingExtractionAccessObject;
import org.navigoproject.data.dao.GradingStatisticsAccessObject;

/**
 * DOCUMENTATION PENDING
 *
 * @author Ed Smiley <esmiley@stanford.edu>
 * @version $Id: EvaluationServiceDelegate.java,v 1.2 2004/09/24 05:37:12 rgollub.stanford.edu Exp $
 */
public class EvaluationServiceDelegate
{
  private static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(EvaluationServiceDelegate.class);

  private GradingExtractionAccessObject geao = null;
  private GradingStatisticsAccessObject gsao = null;

  /**
   * Creates a new EvaluationServiceDelegate object.
   *
   * @throws ServiceLocatorException DOCUMENTATION PENDING
   * @throws EvaluationServiceException DOCUMENTATION PENDING
   */
  public EvaluationServiceDelegate()
    throws EvaluationServiceException
  {
    try
    {
      geao = new GradingExtractionAccessObject();
      gsao = new GradingStatisticsAccessObject();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }


  /**
   * Invoke service: delivers Map of assessment statistics
   * @param assessmentId the assesssment id
   * @return a Map containing key/value pairs "mode", "mean", etc.
   */
  public Map getAssessmentStatisticsMap(String assessmentId)
  {
    LOG.debug("getAssessmentStatisticsMap " + assessmentId);

    try
    {
      return gsao.getAssessmentStatisticsMap(assessmentId);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    
  }

  /**
   * Invoke service: delivers Map of assessment statistics
   * @param assessmentId the assesssment id
   * @param last last submission only
   * @return a Map containing key/value pairs "mode", "mean", etc.
   */
  public Map getAssessmentStatisticsMap(String assessmentId,
    boolean last)
  {
    LOG.debug("getAssessmentStatisticsMap " + assessmentId);

    try
    {
      return gsao.getAssessmentStatisticsMap(
          assessmentId, last);
    } catch (Exception e)
    {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Invoke service: delivers Map of item statistics
   * @param assessmentId the assesssment id
   * @param itemId the item id
   * @return a Map containing key/value pairs "mode", "mean", etc.
   */
  public Map getItemStatisticsMap(String assessmentId, String itemId)
  {
    LOG.debug("getItemStatisticsMap");

    try
    {
      return gsao.getItemStatisticsMap(assessmentId, itemId);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Invoke service: delivers Map of item statistics
   * @param assessmentId the assesssment id
   * @param itemId the item id
   * @param last last submission only
   * @return a Map containing key/value pairs "mode", "mean", etc.
   */
  public Map getItemStatisticsMap(String assessmentId,
    String itemId, boolean last)
  {
    LOG.debug("getItemStatisticsMap");

    try
    {
      return gsao.getItemStatisticsMap(
          assessmentId, itemId, last);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Invoke service: Invoke service: get the correct answer
   * answer for the item.
   * @param assessmentId
   * @param itemId
   * @return correct answer text
   */
  public String getCorrectAnswer(String assessmentId, String itemId)
  {
    if(LOG.isDebugEnabled())

    {
      LOG.debug("getCorrectAnswer " + assessmentId + " " + itemId);
    }

    try
    {
      return gsao.getCorrectAnswer(assessmentId, itemId);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Invoke service: get the type for the item.
   * @param assessmentId the assessment id
   * @param itemId the item id
   * @return the type
   */
  public String getItemType(String assessmentId, String itemId)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getItemType " + assessmentId + " " + itemId);
    }

    try
    {
      return gsao.getItemType(assessmentId, itemId);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
      e.printStackTrace();
      return null;
    }
  }

  /** Invoke service: given a realized assessment in QTI format,
   * look up the internal transform URI from the current ServletContext.
   * @param qtiResult the qti result document
   * @param context the ServletContext for the xsl transform
   */
  public void setGradeFromDocument(Document qtiResult, ServletContext context)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("setGradeFromDocument");
    }

    try
    {
      geao.setGradeFromDocument(qtiResult, context);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
      e.printStackTrace();
    }
  }

  /**
   * Invoke service: models assessment wide results for agents (students).
   * @param assessmentID
   * @param last true if filter by last
   * @return Collection of agent results
   */
  public Collection getAgentTotalResults(String assessmentID, boolean last)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getAgentTotalResults " + assessmentID + " " + last);
    }

    try
    {
      return geao.getAgentTotalResults(assessmentID, last);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Invoke service: models item (question) results for agents (students).
   * @param assessmentId the assessment id
   * @param itemId the itemid
   * @param last true if get the last submission only
   * @return Collection of agent results
   */
  public Collection getAgentItemResults(
    String assessmentId, String itemId, boolean last)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "getAgentItemResults " + assessmentId + " " + itemId + " " + last);
    }

    try
    {
      return geao.getAgentItemResults(assessmentId, itemId, last);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Invoke service: change the grade/grading comment of an assessment.
   * @param score the raw score for the grade
   * @param adjustScore the adjustment to the score
   * @param comments the grading comments
   * @param assessmentId the id of the assessment
   * @param assessmentResultId the submission id for the result
   */
  public void changeAssessmentGrade(
    String score, String adjustScore, String comments, String assessmentId,
    String assessmentResultId)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "changeAssessmentGrade" + score + " " + adjustScore + " '" + comments +
        "' " + assessmentId);
    }

    try
    {
      geao.changeAssessmentGrade(
        score, adjustScore, comments, assessmentId, assessmentResultId);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
      e.printStackTrace();
    }
  }

  /**
   * Invoke service: change the grade or grading comment of an item.
   * @param score the raw score for the grade
   * @param adjustScore the adjustment to the score
   * @param comments the grading comments
   * @param itemId the id of the item
   * @param assessmentResultId the submission id for the result
   */
  public void changeItemGrade(
    String score, String comments, String itemId, String assessmentResultId)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "changeItemGrade " + score + " '" + comments + "' " + itemId + " " +
        assessmentResultId);
    }

    try
    {
      geao.changeItemGrade(
        score, comments, itemId, assessmentResultId);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
      e.printStackTrace();
    }
  }
}
