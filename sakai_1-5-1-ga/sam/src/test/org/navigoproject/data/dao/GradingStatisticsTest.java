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

package test.org.navigoproject.data.dao;

import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import org.navigoproject.data.dao.GradingStatisticsAccessObject;
import org.navigoproject.ui.web.form.evaluation.HistogramQuestionScoresForm;
import org.navigoproject.ui.web.form.evaluation.HistogramScoresForm;

/**
 * Test harness for grade statistics.
 * @author Ed Smiley
 * @version $Id: GradingStatisticsTest.java,v 1.1.1.1 2004/07/28 21:32:08 rgollub.stanford.edu Exp $
 */

public class GradingStatisticsTest
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(GradingStatisticsTest.class);
    
  // edit these to be realized assessment's id and item ids under the assessment
  private static final String assessmentId = "0e4440f0ab408ac500fa1b85b6f0bcea";
  private static final String[] itemIds = {
    "0e42fb2eab408ac501f75ab1b2abbd58",
    "389047a3ab408ac500f37edba193d2e1",
//    "5348115386441228005e305fd986884d"
  };


/**
 * unit test
 *
 * @param args not used
 */

  public static void main(String[] args)
  {
//    LOG.debug("debug");
    // could configure GenericConnectionManager to use private database for this
    GradingStatisticsAccessObject gsao = new GradingStatisticsAccessObject();

    // Test forms.
//    LOG.debug("debug");
    HistogramQuestionScoresForm hqsf = new HistogramQuestionScoresForm();
    HistogramScoresForm hsf = new HistogramScoresForm();
//    QuestionScoresForm qsf = new QuestionScoresForm();
//    TotalScoresForm tsf = new TotalScoresForm();

    // test assessment map
    LOG.debug("--------------------------------------------");
    LOG.debug("statistics map for assessment id: " + assessmentId);
//    LOG.debug("debug 1");
    Map assessmentMap = gsao.getAssessmentStatisticsMap(assessmentId);
//    LOG.debug("debug 2");
    LOG.debug(assessmentMap);
    LOG.debug("--------------------------------------------");
    try{
    BeanUtils.populate(hsf, assessmentMap);
    LOG.debug("H Scores Form: =>" +
                       hsf.getMean() + ":" +
                       hsf.getColumnHeight() + ":" +
                       hsf.getInterval() + ":" +
                       hsf.getLowerQuartile() + ":" +
                       hsf.getMaxScore() + ":" +
                       hsf.getMean() + ":" +
                       hsf.getMedian() + ":" +
                       hsf.getNumResponses() + ":" +
                       hsf.getNumStudentCollection() + ":" +
                       hsf.getQ1() + ":" +
                       hsf.getQ2() + ":" +
                       hsf.getQ3() + ":" +
                       hsf.getQ4()
                       );
    LOG.debug("--------------------------------------------");
    }
    catch (Exception e)
    {
      LOG.debug("unable to populate hsf" + e);
    }
    // test item agents
    for(int i=0; i<itemIds.length; i++)
    {
      String itemId = itemIds[i];
      LOG.debug("--------------------------------------------");

      LOG.debug("statistics map for item id: " +
                         itemId + "assessment id: " + assessmentId);
      Map itemMap = gsao.getItemStatisticsMap(assessmentId, itemId);
      LOG.debug(itemMap);
      LOG.debug("--------------------------------------------");
      String ans = gsao.getCorrectAnswer(assessmentId, itemId);
      LOG.debug("correct answer for: " + itemId);
      LOG.debug(ans);
      LOG.debug("--------------------------------------------");
      String type = gsao.getItemType(assessmentId, itemId);
      LOG.debug("type for: " + itemId);
      LOG.debug(type);
      LOG.debug("--------------------------------------------");
      try
      {
        LOG.debug("H Q Scores Form: =>" +
                         hqsf.getMean() + ":" +
                         hqsf.getColumnHeight() + ":" +
                         hqsf.getInterval() + ":" +
                         hqsf.getLowerQuartile() + ":" +
                         hqsf.getMaxScore() + ":" +
                         hqsf.getMean() + ":" +
                         hqsf.getMedian() + ":" +
                         hqsf.getNumResponses() + ":" +
                         hqsf.getNumStudentCollection() + ":" +
                         hqsf.getQ1() + ":" +
                         hqsf.getQ2() + ":" +
                         hqsf.getQ3() + ":" +
                         hqsf.getQ4()
                         );
      LOG.debug("--------------------------------------------");
      }
      catch (Exception e)
      {
        LOG.debug("unable to populate hsf" + e);
      }
    }



    LOG.debug("Done.");
  }
}
