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

package org.navigoproject.data.dao;

import org.navigoproject.business.entity.GradingExtractionModel;
import org.navigoproject.business.entity.ItemTemplate;
import org.navigoproject.business.entity.assessment.model.Answer;
import org.navigoproject.business.entity.assessment.model.ItemImpl;
import org.navigoproject.business.entity.assessment.model.ItemIteratorImpl;
import org.navigoproject.business.entity.assessment.model.ItemPropertiesImpl;
import org.navigoproject.business.entity.assessment.model.ItemTemplateImpl;
import org.navigoproject.business.entity.assessment.model.ItemTemplateIteratorImpl;
import org.navigoproject.business.entity.assessment.model.ItemTemplatePropertiesImpl;
import org.navigoproject.business.entity.assessment.model.MediaData;
import org.navigoproject.business.entity.evaluation.model.AgentResults;
import org.navigoproject.data.GenericConnectionManager;
import org.navigoproject.osid.OsidManagerFactory;
import org.navigoproject.ui.web.asi.author.item.ItemHelper;
import org.navigoproject.util.XMLMapper;
import org.navigoproject.util.XmlUtil;

import java.io.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;

import javax.xml.parsers.*;

import org.apache.commons.beanutils.BeanUtils;

import org.apache.log4j.*;
import org.apache.log4j.Logger;

import org.w3c.dom.*;

import org.xml.sax.*;

import osid.assessment.Item;

import osid.shared.Id;
import osid.shared.SharedManager;

/**
 * Accesses persisted grading information and returns maps of statistics.
 *
 * @author Ed Smiley (data access)
 * @author Huong Nguyen (statistical analysis)
 * @version $Id: GradingStatisticsAccessObject.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
public class GradingStatisticsAccessObject
{
  GenericConnectionManager gcm = null;
  private static final Logger LOG =
    Logger.getLogger(GradingExtractionAccessObject.class.getName());

  /**
   * Creates a new GradingExtractionAccessObject object.
   */
  public GradingStatisticsAccessObject()
  {
    gcm = GenericConnectionManager.getInstance();
  }

  /**
   * Gets a Map keyed by statistical value for whole assessment:
   *    mean, median, mode, etc.
   *
   * @param assessmentId
   * @return a Map
   */
  public Map getAssessmentStatisticsMap(String assessmentId)
  {
    return getAssessmentStatisticsMap(assessmentId, false);
  }

  /**
   * Gets a Map keyed by statistical value for whole assessment:
   *    mean, median, mode, etc.
   *
   * @param assessmentId
   * @param last true if only the last submission
   * @return a Map
   */
  public Map getAssessmentStatisticsMap(String assessmentId, boolean last)
  {
    String[] scoreStrings = getAssessmentScores(assessmentId, last);
    if((scoreStrings == null) || (scoreStrings.length == 0))
    {
      return new HashMap();
    }

    double[] scores = new double[scoreStrings.length];
    for(int i = 0; i < scoreStrings.length; i++)
    {
      try
      {
        scores[i] = Double.parseDouble(scoreStrings[i]);
      }
      catch(Exception e)
      {
        scores[i] = 0;
      }
    }

    return getStatisticsMapFromScoreArray(scores);
  }

  /**
   * Gets a Map keyed by statistical value for item.
   *    mean, median, mode, etc.
   * @param assessmentId
   * @param itemId
   * @return key value pairs.
   */
  public Map getItemStatisticsMap(String assessmentId, String itemId)
  {
    return getItemStatisticsMap(assessmentId, itemId, false);
  }

  /**
   * Gets a Map keyed by statistical value for item.
   *    mean, median, mode, etc.
   * @param assessmentId
   * @param itemId
   * @param last true if only the last submission
   * @return key value pairs.
   */
  public Map getItemStatisticsMap(
    String assessmentId, String itemId, boolean last)
  {
    Map map = new HashMap();
    String[] scoreStrings = getItemScores(assessmentId, itemId, last);
    double[] scores = new double[scoreStrings.length];

    for(int i = 0; i < scoreStrings.length; i++)
    {
      System.out.println(
        "QUESTION SCORES for item:" + itemId + ": " + scores[i]);
      try
      {
        scores[i] = Double.parseDouble(scoreStrings[i]);
      }
      catch(Exception e)
      {
        scores[i] = 0;
      }
    }

    ItemHelper itemHelper = new ItemHelper();
    org.navigoproject.business.entity.Item item = itemHelper.getItemXml(itemId);
    String[] choices = getAnswerArray(item);
    String type = getItemType(assessmentId, itemId);
    String correct = getCorrectAnswer(assessmentId, itemId);

    String[] answers = getItemAnswers(assessmentId, itemId, last);
    if(
      (type != null) && (! type.equals("True False")) &&
        (! type.equals("Multiple Correct Answer")) &&
        (! type.equals("Multiple Choice")))
    {
      map.putAll(getStatisticsMapFromScoreArray(scores));
    }
    else
    {
      map.putAll(
        getStatisticsMapFromAnswerArray(answers, choices, correct, type));
    }

    return map;
  }

  /**
   * Get the type for the item.
   * @param assessmentId the assessment id
   * @param itemId the item id
   * @return the type
   */
  public String getItemType(String assessmentId, String itemId)
  {
    String type = null;
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try
    {
      conn = gcm.getConnection();
      stmt = conn.prepareStatement(GETTYPE);
      stmt.setString(1, assessmentId);
      stmt.setString(2, itemId);
      rs = stmt.executeQuery();
      if(rs.next())
      {
        type = rs.getString("ITEMTYPE");

        //        String id = rs.getString("ITEMID");
        //        LOG.debug("debug " + id + "=>" + type);
      }
    }
    catch(Exception e)
    {
      LOG.error(e);
      throw new Error(e);
    }
    finally
    {
      try
      {
        rs.close();
      }
      catch(Exception e)
      {
        LOG.error("Resultset did not close.");
      }

      try
      {
        stmt.close();
      }
      catch(Exception e)
      {
        LOG.error("Statement did not close.");
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e);
        throw new Error(e);

        //        LOG.error("Failed to Free Connection.");
      }
    }

    return type;
  }

  /**
   * Get the correct answer string for an item in an assessment.
   *
   * @param assessmentId the assessment id
   * @param itemId the item id
   *
   * @return the answer.  Returns multiple correct answers as
   * pipe-delimited String.
   */
  public String getCorrectAnswer(String assessmentId, String itemId)
  {
    String correct = null;
    ItemHelper itemHelper = new ItemHelper();
    org.navigoproject.business.entity.Item item = itemHelper.getItemXml(itemId);
    correct = getItemCorrectAnswer(item);

    return correct;
  }

  /**
   * Get the correct answer text from the Item XML.
   * Returns multiple correct answers as pipe-delimited String.
   *
   * @param itemXml  the Item XML
   *
   * @return a String containing the answer
   */
  private String getItemCorrectAnswer(
    org.navigoproject.business.entity.Item itemXml)
  {
    String baseXPath =
      "item/presentation/flow/response_lid/render_choice/response_label";
    String respXPath = "/material/mattext";
    String respVarXPath = "/@ident";

    String respVarProcBaseXPath = "item/resprocessing/respcondition";
    String respVarCorrectXPath = "/conditionvar/varequal";
    String respVarFeedbackXPath = "/displayfeedback/@linkrefid";

    int respSize = 0;
    int correctRespSize = 0;
    String answerItem = "";

    // calculate correct element's number
    String correctAnswerVar = "";

    List corr = itemXml.selectNodes(respVarProcBaseXPath);

    if((corr != null) && (corr.size() > 0))
    {
      correctRespSize = corr.size();
    }

    // find the number of the correct answer
    String sep = "";
    for(int i = 1; i <= correctRespSize; i++)
    {
      String index = ("[" + i) + "]";

      String possibleValue =
        itemXml.selectSingleValue(
          respVarProcBaseXPath + index + respVarFeedbackXPath, "attribute");

      LOG.info("possibleValue=" + possibleValue);
      if("Correct".equals(possibleValue))
      {
        correctAnswerVar += (
          sep +
          itemXml.selectSingleValue(
            respVarProcBaseXPath + index + respVarCorrectXPath, "element")
        );
        LOG.debug("correct!: " + i);
        sep = "|";

        //        break;
      }
    }

    LOG.info("correctAnswerVar=" + correctAnswerVar);

    // now get each response and return correct answer, if known
    List resp = itemXml.selectNodes(baseXPath);

    if((resp != null) && (resp.size() > 0))
    {
      respSize = resp.size();
    }

    sep = "";
    for(int i = 1; i <= respSize; i++)
    {
      String index = ("[" + i) + "]";
      String answerVar =
        itemXml.selectSingleValue(
          baseXPath + index + respVarXPath, "attribute");
      String answer =
        itemXml.selectSingleValue(baseXPath + index + respXPath, "element");

      if(answer == null)
      {
        answer = "";
      }

      if(answerRight(correctAnswerVar, answerVar))
      //        if (correctAnswerVar.equals(answerVar))
      {
        LOG.info("answerVar correct: " + answerVar);
        answerItem += (sep + answer);
        sep = "|";
        LOG.info("answerItem correct: " + answerItem);
      }
    }

    return answerItem; // return empty string if no correct answer text
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param correctAnswerVar DOCUMENTATION PENDING
   * @param answerVar DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private boolean answerRight(String correctAnswerVar, String answerVar)
  {
    StringTokenizer st = new StringTokenizer(correctAnswerVar, "|");
    while(st.hasMoreElements())
    {
      String correct = st.nextToken();
      if(correct.equals(answerVar))
      {
        return true; // match
      }
    }

    return false;
  }

  /**
   *
   * Gets a Map keyed by statistical type for answers, used by
   * getItemStatisticsMap()
   * @param answers String array of answers
   * @param correct correct answer
   * @param type item type
   * @return a map of keyed values
   */
  public Map getStatisticsMapFromAnswerArray(
    String[] answers, String[] choices, String correct, String type)
  {
    HashMap statMap = new HashMap();

    double numCorrect = (new Integer(calCorrect(answers, correct))).doubleValue();

    // if data is missing avoids division by zero
    double percentCorrect = 0; // sets percent correct at zero if wrong
    if(answers.length != 0)
    {
      percentCorrect = (numCorrect * 100) / answers.length;
    }

    int[] num = calNum(answers, choices, type);
    int[] column = calColumnHeight(num);
    int nResponses=0;
              int nStudent=num.length;
              for(int i=0;i<nStudent;i++)
		  {
		      nResponses+=num[i];
		  }
    statMap.put("rangeCollection", choices);
    statMap.put("percentCorrect", castingNum(percentCorrect,2));
    statMap.put("numResponses", new Integer(answers.length));
    statMap.put("arrayLength", new Integer(num.length));
    statMap.put("numStudentCollection", num);
    statMap.put("columnHeight", column);
    statMap.put("totalResponses", new Integer(nResponses));

    return statMap;
  }

  /**
   *
   * Gets a Map keyed by statistical type, used by
   * getItemStatisticsMap()
   * getAssessmentStatisticsMap()
   * @param scores
   * @return a map of keyed values
   */
  public Map getStatisticsMapFromScoreArray(double[] scores)
  {
    HashMap statMap = new HashMap();

    // if scores has no elements we set up a dummy with one score of zero
    if(scores.length == 0)
    {
      scores = new double[1];
      scores[0] = 0;
    }

    double min = scores[0];
    double max = scores[scores.length - 1];
    double total = calTotal(scores);
    double mean = calMean(scores, total);
    int interval = 0;
    interval = calInterval(scores, min, max);
    int[] numStudents = calNumStudents(scores, min, max, interval);

    statMap.put("maxScore", castingNum(max,2));
    statMap.put("interval", new Integer(interval));
    statMap.put("numResponses", new Integer(scores.length));
    statMap.put("mean", castingNum(mean,2));
    statMap.put("median", castingNum(calMedian(scores),2));
    statMap.put("mode", (new String(calMode(scores))));
    statMap.put("numStudentCollection", numStudents);
    statMap.put(
      "rangeCollection", calRange(scores, numStudents, min, max, interval));
    statMap.put("standDev", castingNum(calStandDev(scores, mean, total),2));
    statMap.put("columnHeight", calColumnHeight(numStudents));
    statMap.put("arrayLength", new Integer(numStudents.length));
    statMap.put("maxScore", castingNum(max,2));
    statMap.put("numResponses", new Integer(scores.length));
    statMap.put(
      "range",
      castingNum(scores[0],2) + " - " + castingNum(scores[scores.length - 1],2));
    statMap.put("q1", castingNum(calQuartiles(scores, 0.25),2));
    statMap.put("q2", castingNum(calQuartiles(scores, 0.5),2));
    statMap.put("q3", castingNum(calQuartiles(scores, 0.75),2));
    statMap.put("q4", castingNum(max,2));

    return statMap;
  }

  /**
   * Get a String array of all scores for an assessment
   *
   * @param assessmentId the assessment id
   *
   * @return the String array
   */
  public String[] getAssessmentScores(String assessmentId)
  {
    return getAssessmentScores(assessmentId, false);
  }

  /**
   * Get a String array of all scores for an assessment
   *
   * @param assessmentId the assessment id
   * @param last return only the last submission
   *
   * @return the String array
   */
  public String[] getAssessmentScores(String assessmentId, boolean last)
  {
    Object[] scores = null;
    ArrayList scoreList = new ArrayList();
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String lastAgentId = "zzzzzzzz"; // used if last is true

    try
    {
      conn = gcm.getConnection();
      stmt = conn.prepareStatement(GETASSESSMENTSCORES);
      stmt.setString(1, assessmentId);
      rs = stmt.executeQuery();
      while(rs.next())
      {
        // identity
        String agentId = rs.getString("AGENTID");

        // is this a new agent, if not, this is not the last record
        if(last && lastAgentId.equals(agentId))
        {
          continue; //if set to last submission only skip earlier records
        }

        scoreList.add(new Double(rs.getString("FINALSCORE")));
        lastAgentId = agentId; // record the current agent for the next test
      }

      scores = scoreList.toArray();
      LOG.debug(" BEFORE SORTING : ");
      for(int i = 0; i < scores.length; i++)
      {
        LOG.debug(scores[i]);
      }

      Arrays.sort(scores);
      LOG.debug("  &&&&&&&&&&&&&&&  SORTED ARRAY:&&&&&&&&&& : ");
      for(int i = 0; i < scores.length; i++)
      {
        LOG.debug(scores[i]);
      }
    }
    catch(Exception e)
    {
      LOG.error(e);
      throw new Error(e);
    }
    finally
    {
      try
      {
        rs.close();
      }
      catch(Exception e)
      {
        LOG.error("Resultset did not close.");
      }

      try
      {
        stmt.close();
      }
      catch(Exception e)
      {
        LOG.error("Statement did not close.");
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e);
        throw new Error(e);

        //        LOG.error("Failed to Free Connection.");
      }
    }

    String[] scoreStrings = new String[scores.length];
    for(int i = 0; i < scores.length; i++)
    {
      scoreStrings[i] = scores[i].toString();
    }

    return scoreStrings;
  }

  /**
   * Get a String array of all scores for an assessment item
   *
   * @param assessmentId the assessment id
   * @param itemId the item id
   * @return the String array
   */
  public String[] getItemScores(String assessmentId, String itemId)
  {
    return getItemScores(assessmentId, itemId, false);
  }

  /**
   * Get a String array of all scores for an assessment item
   *
   * @param assessmentId the assessment id
   * @param itemId the item id
   * @return the String array
   */
  public String[] getItemScores(
    String assessmentId, String itemId, boolean last)
  {
    Object[] scores = null;
    ArrayList scoreList = new ArrayList();
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String lastAgentId = "zzzzzzzz"; // used if last is true

    try
    {
      conn = gcm.getConnection();
      stmt = conn.prepareStatement(GETITEMSCORES);
      stmt.setString(1, assessmentId);
      stmt.setString(2, itemId);
      rs = stmt.executeQuery();
      while(rs.next())
      {
        // identity
        String agentId = rs.getString("AGENTID");

        // is this a new agent, if not, this is not the last record
        if(last && lastAgentId.equals(agentId))
        {
          continue; //if set to last submission only skip earlier records
        }

        scoreList.add(rs.getString("ITEMSCORE"));
        lastAgentId = agentId; // record the current agent for the next test
      }

      scores = scoreList.toArray();
      Arrays.sort(scores);
    }
    catch(Exception e)
    {
      LOG.error(e);
      throw new Error(e);
    }
    finally
    {
      try
      {
        rs.close();
      }
      catch(Exception e)
      {
        LOG.error("Resultset did not close.");
      }

      try
      {
        stmt.close();
      }
      catch(Exception e)
      {
        LOG.error("Statement did not close.");
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e);
        throw new Error(e);

        //        LOG.error("Failed to Free Connection.");
      }
    }

    String[] scoreStrings = new String[scores.length];
    for(int i = 0; i < scores.length; i++)
    {
      scoreStrings[i] = (String) scores[i];
    }

    return scoreStrings;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentId DOCUMENTATION PENDING
   * @param itemId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private String[] getItemAnswers(String assessmentId, String itemId)
  {
    return getItemAnswers(assessmentId, itemId, false);
  }

  /**
   * Return an array of the answer text for all items taken for an assessment.
   *
   * @param assessmentId the assessment id
   * @param itemId the item id
   *
   * @return array of the answer text instances
   */
  private String[] getItemAnswers(
    String assessmentId, String itemId, boolean last)
  {
    String[] answers = null;
    ArrayList answerList = new ArrayList();
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String lastAgentId = "zzzzzzzz"; // used if last is true

    try
    {
      conn = gcm.getConnection();
      stmt = conn.prepareStatement(GETITEMANSWERS);
      stmt.setString(1, assessmentId);
      stmt.setString(2, itemId);
      rs = stmt.executeQuery();
      while(rs.next())
      {
        // identity
        String agentId = rs.getString("AGENTID");

        // is this a new agent, if not, this is not the last record
        if(last && lastAgentId.equals(agentId))
        {
          continue; //if set to last submission only skip earlier records
        }

        answerList.add(rs.getString("ANSWERTEXT").trim());
        lastAgentId = agentId; // record the current agent for the next test
      }

      Object[] olist = answerList.toArray();
      answers = new String[olist.length];
      for(int i = 0; i < olist.length; i++)
      {
        String answer = (String) olist[i];
        answers[i] = answer.trim();
      }
    }
    catch(Exception e)
    {
      LOG.error(e);
      throw new Error(e);
    }
    finally
    {
      try
      {
        rs.close();
      }
      catch(Exception e)
      {
        LOG.error(e);
        LOG.error("Resultset did not close.");
      }

      try
      {
        stmt.close();
      }
      catch(Exception e)
      {
        LOG.error(e);
        LOG.error("Statement did not close.");
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e);
        throw new Error(e);

        //        LOG.error("Failed to Free Connection.");
      }
    }

    return answers;
  }

  /**
   * Calculate the total score for all students
   * @param scores array of scores
   * @return the total
   */
  private static double calTotal(double[] scores)
  {
    double total = 0;
    for(int i = 0; i < scores.length; i++)
    {
      total = total + scores[i];
    }

    return total;
  }

  /**
   * Calculate mean.
   *
   * @param scores array of scores
   * @param total the total of all scores
   *
   * @return mean
   */
  private static double calMean(double[] scores, double total)
  {
    return total / scores.length;
  }

  /**
   * Calculate median.
   *
   * @param scores array of scores
   *
   * @return median
   */
  private static double calMedian(double[] scores)
  {
    double median;
    if(((scores.length) % 2) == 0)
    {
      median =
        (scores[(scores.length / 2)] + scores[(scores.length / 2) - 1]) / 2;
    }

    else
    {
      median = scores[(scores.length - 1) / 2];
    }

    return median;
  }

  /**
   * Calculate mode
   *
   * @param scores array of scores
   *
   * @return mode
   */
  private static String calMode(double[] scores)
  {
    String max = ", " + scores[0];
    String temp = ", " + scores[0];
    int nmax = 1;
    int ntemp = 1;
    String scoreString;
    for(int i = 1; i < scores.length; i++)
    {
      if(Math.ceil(scores[i]) == Math.floor(scores[i]))
      {
        scoreString = "" + (int) scores[i];
      }
      else
      {
        scoreString = "" + scores[i];
      }

      if((", " + scoreString).equals(temp))
      {
        ntemp++;
        if(nmax < ntemp)
        {
          max = temp;
          nmax = ntemp;
        }
        else if(nmax == ntemp)
        {
          max = max + temp;
          nmax = ntemp;
        }
        else
        {
          // nmax>ntemp do nothing
        }
      }
      else
      {
        temp = ", " + scoreString;
        ntemp = 1;
      }
    }

    max = max.substring(2, max.length());

    return max;
  }

  /**
   * Calculate standard Deviation
   *
   * @param scores array of scores
   * @param mean the mean
   * @param total the total
   *
   * @return the standard deviation
   */
  private static double calStandDev(double[] scores, double mean, double total)
  {
    for(int i = 0; i < scores.length; i++)
    {
      total = total + ((scores[i] - mean) * (scores[i] - mean));
    }

    return Math.sqrt(total / scores.length);
  }

  /**
   * Calculate the interval to use for histograms.
   *
   * @param scores array of scores
   * @param min the minimum score
   * @param max the maximum score
   *
   * @return the interval
   */
  private static int calInterval(double[] scores, double min, double max)
  {
    int interval;

    if((max - min) < 10)
    {
      interval = 1;
    }
    else
    {
      interval = (int) Math.ceil((max - min) / 10);
    }

    return interval;
  }

  /**
   * Calculate the number for each answer.
   *
   * @param answers array of answers
   *
   *
   * @return array of number giving each answer.
   */
  private static int[] calNum(String[] answers, String[] choices, String type)
  {
    int[] num = new int[choices.length];

    for(int i = 0; i < answers.length; i++)
    {
      for(int j = 0; j < choices.length; j++)
      {
        if(type.equals("Multiple Correct Answer"))
        {
          // TODO: using Tokenizer because split() doesn't seem to work.  
          StringTokenizer st = new StringTokenizer(answers[i], "|");
          while(st.hasMoreTokens())
          {
            String nt = st.nextToken();

            if((nt.trim()).equals(choices[j].trim()))
            {
              num[j] = num[j] + 1;
            }
          }

          /*
             String[] answerArray = answers[i].split("|");
             for(int k=0;i<answerArray.length ; k++)  {
               if ( (answerArray[k].trim()).equals(choices[j].trim())) {
                 num[j] = num[j] + 1;
               }
             }
           */
        }
        else
        {
          if(answers[i].equals(choices[j]))
          {
            num[j] = num[j] + 1;
          }
        }
      }
    }

    return num;
  }

  /**
   * Calculate the number correct answer
   *
   * @param answers array of answers
   * @param correct the correct answer
   *
   * @return the number correct
   */
  private int calCorrect(String[] answers, String correct)
  {
    int cal = 0;
    for(int i = 0; i < answers.length; i++)
    {
      if(answers[i].equals(correct))
      {
        cal++;
      }
    }

    return cal;
  }

  /**
   * Calculate the number of students per interval for histograms.
   *
   * @param scores array of scores
   * @param min the minimum score
   * @param max the maximum score
   * @param interval the interval
   *
   * @return number of students per interval
   */
  private static int[] calNumStudents(
    double[] scores, double min, double max, int interval)
  {
    if(min > max)
    {
      LOG.info("max(" + max + ") <min(" + min + ")");
      max = min;
    }

    int[] numStudents = new int[(int) Math.ceil((max - min) / interval)];

    // this handles a case where there are no num students, treats as if
    // a single value of 0
    if(numStudents.length == 0)
    {
      numStudents = new int[1];
      numStudents[0] = 0;
    }

    for(int i = 0; i < scores.length; i++)
    {
      if(scores[i] <= (min + interval))
      {
        numStudents[0]++;
      }
      else
      {
        for(int j = 1; j < (numStudents.length); j++)
        {
          if(
            ((scores[i] > (min + (j * interval))) &&
              (scores[i] <= (min + ((j + 1) * interval)))))
          {
            numStudents[j]++;

            break;
          }
        }
      }
    }

    return numStudents;
  }

  /**
   * Get range text for each interval
   *
   * @param answers array of ansers
   *
   *
   * @return array of range text strings for each interval
   */
  private static String[] calRange(String[] answers, String[] choices)
  {
    String[] range = new String[choices.length];
    int current = 0;

    // gracefully handle a condition where there are no answers
    if(answers.length == 0)
    {
      for(int i = 0; i < range.length; i++)
      {
        range[i] = "unknown";
      }

      return range;
    }

    choices[0] = answers[0];
    for(int a = 1; a < answers.length; a++)
    {
      if(! (answers[a].equals(choices[current])))
      {
        current++;
        choices[current] = answers[a];
      }
    }

    return range;
  }

  /**
   * Calculate range strings for each interval.
   *
   * @param scores array of scores
   * @param numStudents number of students for each interval
   * @param min the minimium
   * @param max the maximum
   * @param interval the number of intervals
   *
   * @return array of range strings for each interval.
   */
  private static String[] calRange(
    double[] scores, int[] numStudents, double min, double max, int interval)
  {
    String[] ranges = new String[numStudents.length];

    ranges[0] = (int) min + " - " + (int) (min + interval);
    int i = 1;
    while(i < ranges.length)
    {
      if((((i + 1) * interval) + min) < max)
      {
        ranges[i] =
          ">" + (int) ((i * interval) + min) + " - " +
          (int) (((i + 1) * interval) + min);
      }
      else
      {
        ranges[i] = ">" + (int) ((i * interval) + min) + " - " + (int) max;
      }

      i++;
    }

    return ranges;
  }

  /**
   * Calculate the height of each histogram column.
   *
   * @param numStudents the number of students for each column
   *
   * @return array of column heights
   */
  private static int[] calColumnHeight(int[] numStudents)
  {
    int length = numStudents.length;
    int[] temp = new int[length];

    int[] height = new int[length];
    int i = 0;
    while(i < length)
    {
      temp[i] = numStudents[i];

      i++;
    }

    Arrays.sort(temp);

    int num = 1;

    if((temp.length > 0) && (temp[temp.length - 1] > 0))
    {
      num = (int) (600 / temp[temp.length - 1]);
      int j = 0;
      while(j < length)
      {
        height[j] = num * numStudents[j];

        j++;
      }
    }

    return height;
  }

  /**
   * Calculate quartiles.
   *
   * @param scores score array
   * @param r the quartile rank
   *
   * @return the quartile
   */
  private static double calQuartiles(double[] scores, double r)
  {
    int k;
    double f;
    k = (int) (Math.floor((r * (scores.length - 1)) + 1));
    f = (r * (scores.length - 1)) - Math.floor(r * (scores.length - 1));

    // special handling if insufficient data to calculate
    if(k < 2)
    {
      if(scores != null)
      {
        return scores[0];
      }
      else
      {
        return 0;
      }
    }

    return scores[k - 1] + (f * (scores[k] - scores[k - 1]));
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param n DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
    private String castingNum(double number,int decimal)
  {
      int indexOfDec=0;
      String n;
      int index;
    if(Math.ceil(number) == Math.floor(number))
    {
      return ("" + (int) number);
    }
    else
    {
        n=""+number;      
	indexOfDec=n.indexOf(".");    
	index=indexOfDec+decimal+1;  
        System.out.println("NUMBER : "+n);
        System.out.println("NUMBER LENGTH : "+n.length());
        if(n.length()>index)
	    {
        return n.substring(0,index);
	    }
        else{
	    return ""+number;
	}
	
    }
  }

  /**
   * Get the answer text from the Item XML in a string array.
   * @param itemXml  the Item XML
   *
   * @return a String[] containing the answers
   */
  private String[] getAnswerArray(
    org.navigoproject.business.entity.Item itemXml)
  {
    String baseXPath =
      "item/presentation/flow/response_lid/render_choice/response_label";
    String respXPath = "/material/mattext";
    String respVarXPath = "/@ident";

    int respSize = 0;

    List resp = itemXml.selectNodes(baseXPath);

    if((resp != null) && (resp.size() > 0))
    {
      respSize = resp.size();
    }

    String[] answers = new String[respSize];

    for(int i = 1; i <= respSize; i++)
    {
      String index = ("[" + i) + "]";
      String answerVar =
        itemXml.selectSingleValue(
          baseXPath + index + respVarXPath, "attribute");
      String answerItem =
        itemXml.selectSingleValue(baseXPath + index + respXPath, "element");

      if(answerItem == null)
      {
        answerItem = "";
      }

      answers[i - 1] = answerItem.trim();

      LOG.info(" respVarXPath:" + baseXPath + "[" + i + "]" + respVarXPath);
      LOG.info(" content respVarXPath: " + answerItem);
    }

    return answers;
  }

  private static final String GETASSESSMENTSCORES =
    "SELECT FINALSCORE, AGENTID FROM ASSESSMENTGRADING WHERE ASSESSMENTID = ? " +
    " ORDER BY AGENTID, RESULTDATE DESC";
  private static final String GETITEMSCORES =
    "SELECT ITEMID, ITEMSCORE, AGENTID FROM ITEMGRADING i, ASSESSMENTGRADING a " +
    " WHERE a.ASSESSMENTID=? AND ITEMID=? " +
    " AND a.ASSESSMENTRESULTID = i.ASSESSMENTRESULTID " +
    " ORDER BY AGENTID, RESULTDATE DESC";
  private static final String GETITEMANSWERS =
    " SELECT AGENTID, ANSWERTEXT FROM ASSESSMENTGRADING a, ITEMGRADING i " +
    " WHERE a.ASSESSMENTRESULTID=i.ASSESSMENTRESULTID AND " +
    " ASSESSMENTID = ? AND ITEMID = ? " + " ORDER BY AGENTID, RESULTDATE DESC";
  private static final String GETTYPE =
    "SELECT ITEMID, ITEMTYPE from ITEMGRADING i, ASSESSMENTGRADING a" +
    " WHERE a.ASSESSMENTRESULTID=i.ASSESSMENTRESULTID AND " +
    " ASSESSMENTID = ? AND ITEMID = ? ";
}
