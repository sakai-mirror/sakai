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

package org.navigoproject.ui.web.form.evaluation;

import org.navigoproject.business.entity.assessment.model.AccessGroup;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorForm;

/**
 * <p>
 * Title: NavigoProject.org
 * </p>
 *
 * <p>
 * Description: AAM - form class for edit/access.jsp
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
 * @author Huong Nguyen
 * @version 1.0
 */
public class HistogramScoresForm
  extends ActionForm
// implements Serializable
{
  private String assessmentName;
  private String assessmentId;
  private String agent;
  private String groupName;
  private String maxScore; // heighest score
  private String totalScore; //total possible score
  private String adjustedScore;
  private String questionNumber;
  private boolean allSubmissions;
  private String partNumber;
  private Integer parts;
  private String mean;
  private String median;
  private String mode;
  private String highestRange;
  private String standDev;
  private String lowerQuartile; //medidan of lowest-median
  private String upperQuartile; //median of median-highest
  private int interval; // number interval breaks down
  private Collection info;
  private int[] numStudentCollection = {  };
  private String[] rangeCollection = {  };
  private int[] columnHeight = {  };
  private int arrayLength; //length of array
  private String range; // range of student Score lowest-highest
  private int numResponses;
  private String q1;
  private String q2;
  private String q3;
  private String q4;

  private HistogramQuestionScoresForm[] histogramQuestions;

  static Logger logger = Logger.getLogger(HistogramScoresForm.class.getName());

  /**
   * Creates a new HistogramScoresForm object.
   */
  public HistogramScoresForm()
  {
    super();

    //  resetFields();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param actionMapping DOCUMENTATION PENDING
   * @param httpServletRequest DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ActionErrors validate(
    ActionMapping actionMapping, HttpServletRequest httpServletRequest)
  {
    ActionErrors errors = new ActionErrors();

    return errors;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param actionMapping DOCUMENTATION PENDING
   * @param httpServletRequest DOCUMENTATION PENDING
   */
  public void reset(
    ActionMapping actionMapping, HttpServletRequest httpServletRequest)
  {
    // Set checkboxes to false here.
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pagent DOCUMENTATION PENDING
   */
  public void setAgent(String pagent)
  {
    agent = pagent;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getAgent()
  {
    return agent;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getAssessmentName()
  {
    return assessmentName;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param passessmentName DOCUMENTATION PENDING
   */
  public void setAssessmentName(String passessmentName)
  {
    assessmentName = passessmentName;
  }
  /**
  * get histogram quesions
  *
  * @return the histogram quesions beans in an array
  */
 public HistogramQuestionScoresForm[] getHistogramQuestions()
 {
   return histogramQuestions;
 }

 /**
  * set assessment name
  *
  * @param passessmentName the name
  */
 public void setHistogramQuestions(HistogramQuestionScoresForm[] phistogramQuestions)
 {
   histogramQuestions = phistogramQuestions;
 }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getAssessmentId()
  {
    return assessmentId;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param passessmentId DOCUMENTATION PENDING
   */
  public void setAssessmentId(String passessmentId)
  {
    assessmentId = passessmentId;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getGroupName()
  {
    return groupName;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pgroupName DOCUMENTATION PENDING
   */
  public void setGroupName(String pgroupName)
  {
    groupName = pgroupName;
  }

  public boolean getAllSubmissions()
  {
    return allSubmissions;
  }
  public void setAllSubmissions(boolean pallSubmissions)
  {
    allSubmissions=pallSubmissions;
  }


  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String[] getRangeCollection()
  {
    return rangeCollection;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param prange DOCUMENTATION PENDING
   */
  public void setRangeCollection(String[] prange)
  {
    rangeCollection = prange;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public int[] getNumStudentCollection()
  {
    return numStudentCollection;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pnumStudent DOCUMENTATION PENDING
   */
  public void setNumStudentCollection(int[] pnumStudent)
  {
    numStudentCollection = pnumStudent;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public int[] getColumnHeight()
  {
    return columnHeight;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pcolumnHeight DOCUMENTATION PENDING
   */
  public void setColumnHeight(int[] pcolumnHeight)
  {
    columnHeight = pcolumnHeight;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public int getArrayLength()
  {
    return arrayLength;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param parrayLength DOCUMENTATION PENDING
   */
  public void setArrayLength(int parrayLength)
  {
    arrayLength = parrayLength;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public int getInterval()
  {
    return interval;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pinterval DOCUMENTATION PENDING
   */
  public void setInterval(int pinterval)
  {
    interval = pinterval;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getInfo()
  {
    return info;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pinfo DOCUMENTATION PENDING
   */
  public void setInfo(Collection pinfo)
  {
    info = pinfo;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getMaxScore()
  {
    return maxScore;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pmaxScore DOCUMENTATION PENDING
   */
  public void setMaxScore(String pmaxScore)
  {
    maxScore = pmaxScore;
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
  public String getAdjustedScore()
  {
    return adjustedScore;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param padjustedScore DOCUMENTATION PENDING
   */
  public void setAdjustedScore(String padjustedScore)
  {
    adjustedScore = padjustedScore;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getQuestionNumber()
  {
    return questionNumber;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pquestionNumber DOCUMENTATION PENDING
   */
  public void setQuestionNumber(String pquestionNumber)
  {
    questionNumber = pquestionNumber;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getPartNumber()
  {
    return partNumber;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param ppartNumber DOCUMENTATION PENDING
   */
  public void setPartNumber(String ppartNumber)
  {
    partNumber = ppartNumber;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getMean()
  {
    return mean;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pmean DOCUMENTATION PENDING
   */
  public void setMean(String pmean)
  {
    mean = pmean;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getMedian()
  {
    return median;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pmedian DOCUMENTATION PENDING
   */
  public void setMedian(String pmedian)
  {
    median = pmedian;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getMode()
  {
    return mode;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pmode DOCUMENTATION PENDING
   */
  public void setMode(String pmode)
  {
    mode = pmode;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getStandDev()
  {
    return standDev;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pstandDev DOCUMENTATION PENDING
   */
  public void setStandDev(String pstandDev)
  {
    standDev = pstandDev;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getLowerQuartile()
  {
    return lowerQuartile;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param plowerQuartile DOCUMENTATION PENDING
   */
  public void setLowerQuartile(String plowerQuartile)
  {
    lowerQuartile = plowerQuartile;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getUpperQuartile()
  {
    return upperQuartile;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pupperQuartile DOCUMENTATION PENDING
   */
  public void setUpperQuartile(String pupperQuartile)
  {
    upperQuartile = pupperQuartile;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getQ1()
  {
    return q1;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pq1 DOCUMENTATION PENDING
   */
  public void setQ1(String pq1)
  {
    q1 = pq1;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getQ2()
  {
    return q2;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pq2 DOCUMENTATION PENDING
   */
  public void setQ2(String pq2)
  {
    q2 = pq2;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getQ3()
  {
    return q3;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pq3 DOCUMENTATION PENDING
   */
  public void setQ3(String pq3)
  {
    q3 = pq3;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getQ4()
  {
    return q4;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pq4 DOCUMENTATION PENDING
   */
  public void setQ4(String pq4)
  {
    q4 = pq4;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getRange()
  {
    return range;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param prange DOCUMENTATION PENDING
   */
  public void setRange(String prange)
  {
    range = prange;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public int getNumResponses()
  {
    return numResponses;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pnumResponses DOCUMENTATION PENDING
   */
  public void setNumResponses(int pnumResponses)
  {
    numResponses = pnumResponses;
  }
}
