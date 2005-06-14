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

import java.util.Collection;
import java.util.Iterator;

import org.navigoproject.business.entity.evaluation.model.AgentResults;
import org.navigoproject.data.dao.GradingExtractionAccessObject;

/**
 * Test harness for estracted grade retrieval.
 * @author Ed Smiley
 * @version $Id: GradingAccessTest.java,v 1.1.1.1 2004/07/28 21:32:08 rgollub.stanford.edu Exp $
 */
public class GradingAccessTest
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(GradingAccessTest.class);

  // edit these to be realized assessment's id and item ids under the assessment
  private static final String assessmentId = "534b30b78644122801715c20d92d0c4b";
  private static final String[] itemIds = {
    "534b30b78644122801715c20d92d0c4b",
    "53468dee8644122801b15387d95a7671",
    "5348115386441228005e305fd986884d"
  };
  /**
   * unit test
   *
   * @param args not used
   */
  public static void main(String[] args)
  {
    // could configure GenericConnectionManager to use private database for this
    GradingExtractionAccessObject geao = new GradingExtractionAccessObject();

    // test assessment agents
    LOG.debug("============================================");
    LOG.debug("allAssessmentAgents, assessmentId=" + assessmentId);
    Collection allAssessmentAgents = geao.getAgentTotalResults(assessmentId, false);
    printAgents(allAssessmentAgents);
    LOG.debug("--------------------------------------------");

    LOG.debug("lastAssessmentAgents, assessmentId=" + assessmentId);
    Collection lastAssessmentAgents = geao.getAgentTotalResults(assessmentId, true);
    printAgents(lastAssessmentAgents);
    LOG.debug("--------------------------------------------");
    LOG.debug("============================================");

    // test item agents
    for(int i=0; i<itemIds.length; i++)
    {
      String itemId = itemIds[i];
      LOG.debug("============================================");

      LOG.debug("allItemAgents for item id: " + itemId);
      Collection allItemAgents = geao.getAgentItemResults(assessmentId, itemId, false);
      printAgents(allItemAgents);
      LOG.debug("--------------------------------------------");

      LOG.debug("lastItemAgents for item id: " + itemId);
      Collection lastItemAgents = geao.getAgentItemResults(assessmentId, itemId, true);
      printAgents(lastItemAgents);
      LOG.debug("--------------------------------------------");

      LOG.debug("============================================");
    }
    LOG.debug("Done.");

  }

  private static void printAgents(Collection agents)
  {
    Iterator it = agents.iterator();
    while(it.hasNext())
    {
      printAgent( (AgentResults) it.next());
    }

  }

  private static void printAgent(AgentResults agent)
  {
    LOG.debug(
    "\nassessment id:" + agent.getAssessmentID() +
    "\nagent id:" + agent.getAgentId() +
    "\nagent comments:" + agent.getComments()+
    "\nagent final score:" + agent.getFinalScore() +
    "\nagent fname:" + agent.getFirstName() +
    "\nagent lname:" + agent.getLastName() +
    "\nagent init:" + agent.getLastInitial() +
    "\nagent rationale:" + agent.getRationale() +
    "\nagent role:" + agent.getRole() +
    "\nagent section:" + agent.getSection() +
    "\nagent sub date:" + agent.getSubmissionDate() +
    "\nagent adjust:" + agent.getAdjustmentTotalScore() +
    "\nagent total comments:" + agent.getTotalScoreComments() +
    "\nagent total:" + agent.getTotalScore()
    );

  }
}
