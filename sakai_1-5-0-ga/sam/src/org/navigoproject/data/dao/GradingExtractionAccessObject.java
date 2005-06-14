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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import org.navigoproject.business.entity.GradingExtractionModel;
import org.navigoproject.business.entity.assessment.model.MediaData;
import org.navigoproject.business.entity.evaluation.model.AgentResults;
import org.navigoproject.data.GenericConnectionManager;
import org.navigoproject.data.PublishedAssessmentBean;
import org.navigoproject.data.RealizationBean;
import org.navigoproject.data.RealizationManager;
import org.navigoproject.osid.impl.PersistenceService;
import org.navigoproject.util.XMLMapper;
import org.navigoproject.util.*;


/**
 * Write methods: Create/update
 *
 * This takes an existing realized assessment using XML and persists it to
 * relational database using:
 *   setGradeFromDocument(Document, ServletContext)
 *
 * This updates grade and comments for assessments and items.
 *
 * public void changeAssessmentGrade(String score, String adjustScore,
 *   String comments, String assessmentId, String assessmentResultId)
 *
 * public void changeItemGrade(String score, String comments, String itemId,
 *   String assessmentResultId)
 *
 * Read methods:
 *
 * Populate total scores agent results collection from database use:
 *   getAgentTotalResults(String assessmentID)
 *
 * Populate individual question scores agent results collection from database:
 *   getAgentItemResults(String assessmentId, String itemId)
 *
 *
 * @author <a href="mailto:esmiley@stanford.edu">Ed Smiley</a>
 * @version $Id: GradingExtractionAccessObject.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
public class GradingExtractionAccessObject
{
  GenericConnectionManager gcm = null;
  static Logger LOG =
    Logger.getLogger(GradingExtractionAccessObject.class.getName());
  static final String gradingTransform =
    "/xml/xsl/dataTransform/result/extractGrades.xsl";

  /**
   * Creates a new GradingExtractionAccessObject object.
   */
  public GradingExtractionAccessObject()
  {
    gcm = GenericConnectionManager.getInstance();
  }

  /**
   * Grading records will be inserted unless they have already been created
   * in which case they will be reset and updated from the document.
   *
   * In most cases, I expect that the records will be created once from the
   * document, and not updated from it.  Fields like comments and adjustments
   * will be independently set when the assessment is manually graded.
   *
   * @param assessmentTakenResult  assessment and assessment taken result document
   * e.g.:
   *
   * <assessment_taken>
   *  <assessment ident=...
   *  </assessment>
   *  <qti_result_report>
   *    <result>...
   *     <context>...
   *     </context>
   *      <assessment_result>...
   *      </assessment_result>...
   *    </result>
   *  </qti_result_report>
   * </assessment_taken>
   *
   * @param context the ServletContext for the xsl transform
   */
  public void setGradeFromDocument(Document assessmentTakenResult, ServletContext context)
  {
    Document transform = XmlUtil.readDocument(context, gradingTransform);
    setGradeFromDocument(assessmentTakenResult, transform);
  }

  /**
   * Variant of
   * @see setGradeFromDocument(Document assessmentTakenResult, ServletContext context).
   * Takes a Document for the XSLT transform instead of a ServletContext.
   * @param assessmentTakenResult  assessment and assessment taken result document,
   * e.g.:
   *
   * <assessment_taken>
   *  <assessment ident=...
   *  </assessment>
   *  <qti_result_report>
   *    <result>...
   *     <context>...
   *     </context>
   *      <assessment_result>...
   *      </assessment_result>...
   *    </result>
   *  </qti_result_report>
   * </assessment_taken>
   *
   * @param transform the xsl transform document
   */
  public void setGradeFromDocument(Document assessmentTakenResult, Document transform)
    {
    // get the data that is available from the document
    LOG.debug(" calling setGradeFromDocuemnt");
    Document dataModel = XmlUtil.transformDocument(assessmentTakenResult, transform);
    GradingExtractionModel gem = new GradingExtractionModel();
    XMLMapper.populateBeanFromDoc(gem, dataModel);

    // get the published assessment realization cross reference information
    String assessmentResultId = gem.getAssessmentResultId();
    LOG.debug(" assessmentResultId:  " + assessmentResultId);
    RealizationManager rManager = new RealizationManager();
    RealizationBean rBean = rManager.getRealizationBean(assessmentResultId);
    LOG.debug("calling createGrade with GradingExtractionModel and RealizationBean");
    createGrade(gem, rBean);
  }

  /**
   * Determine need to create new assessment grading and item grading records.
   * @param assessmentResultsId the result id
   * @return true if there is already a record,
   * or if it cannot be determined,
   * due to an error (to avoid creating a duplicate)
   */
  private boolean resultExists(String assessmentResultsId)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    boolean exists = false;

    try
    {
      conn = gcm.getConnection();
      stmt = conn.prepareStatement(GETRESULTEXISTS);
      stmt.setString(1, assessmentResultsId);
      rs = stmt.executeQuery();

      if (rs.next()){
        int count = rs.getInt(1);

        if (count !=0)
        {
          exists = true;
        }
        else
        {
          exists = false;
        }
      }
      else
      {
        exists = true;// don't know how this would happen
      }
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
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
        LOG.error(e); throw new Error(e);
//        LOG.error("Failed to Free Connection.");
      }
    }
    return exists;
  }

  /**
   * Creating the new grading records
   * @param g the extracted GradingExtractionModel, obtained using transform
   * and XMLMapper
   */
  private void createGrade(GradingExtractionModel g, RealizationBean rBean)
  {
    Connection conn = null;
    PreparedStatement stmt1 = null;
    PreparedStatement stmt2 = null;
    ResultSet rs = null;
    String assessmentId = null;

    LOG.debug (" calling createGrade"  );
    try
    {
      // First we need to call the persistence service to grab a core assessment
      // id from published assessment id, which we get from the RealizationBean.
      LOG.debug ("publishedId from rBean.getAssessmentPublishedId()=" +
                 rBean.getAssessmentPublishedId());
      String publishedId = rBean.getAssessmentPublishedId();
      PublishedAssessmentBean pab =
          PersistenceService.getInstance().getPublishedAssessmentQueries().
            getByPublishedId(publishedId);

      LOG.debug ("assessmentId from pab.getCoreId()=" +pab.getCoreId());
      assessmentId = pab.getCoreId();

      conn = gcm.getConnection();

      // Insert overall record for assessment.
      LOG.debug ("conn.prepareStatement(INSERTGRADE)");
      stmt1 = conn.prepareStatement(INSERTGRADE);
      LOG.debug ("setString 1 assessmentId=" +  assessmentId);
      stmt1.setString(1, assessmentId);
      LOG.debug ("setString 2 g.getAssessmentResultId()=" +
                 g.getAssessmentResultId());
      stmt1.setString(2, g.getAssessmentResultId());
      java.util.Date date = rBean.getSubmissionTime();
      java.sql.Timestamp timeanddate = new java.sql.Timestamp(date.getTime());
      LOG.debug("date=" + date);
      LOG.debug("timeanddate=" + timeanddate);
      LOG.debug("setTimestamp 3=" + timeanddate);
      stmt1.setTimestamp(3,timeanddate);
      LOG.debug("setString 4 g.getStudentId()=" + g.getStudentId());
      stmt1.setString(4, g.getStudentId());
      LOG.debug("setString  5 g.getStudent()=" + g.getStudent());
      stmt1.setString(5, g.getStudent());
      // we probably want to remove this from the model
      // get this later from a lookup from AgentResults.getAgentId
      LOG.debug("setString 6 'Student'");
      stmt1.setString(6, "Student"); //we don't have way in the xml for this...
      LOG.debug("setString 7 g.getMaxScore()=" + g.getMaxScore());
      stmt1.setString(7, g.getMaxScore());
      LOG.debug("setString 8 g.getScore()=" + g.getScore());
      stmt1.setString(8, g.getScore());
      LOG.debug("setString 9 = 0");
      stmt1.setString(9, "0"); // no adjustment set by grader yet
      LOG.debug("setString 10 g.getScore()=" + g.getScore());
      stmt1.setString(10, "0"); //no adjustment yet, final is same
      LOG.debug("setString 11 =''");
      stmt1.setString(11, ""); // no comment set by grader yet yet
      // the getLateSubmission() method is extremely flakey!
      // DO NOT RETHROW THIS EXCEPTION !!!!!!!!!!!!!!!!!!!!!
      // IF YOU RETHROW, STUDENT WILL LOSE GRADE !!!!!!!!!!!
      LOG.debug("**********************************************");
      LOG.debug("ATTEMPTING setString rBean.getLateSubmission()");
      LOG.debug("**********************************************");
      try {
        LOG.debug("setString rBean.getLateSubmission().intValue()=" +
                  rBean.getLateSubmission().intValue());
        stmt1.setString(12, "" + rBean.getLateSubmission().intValue());
        LOG.debug("**********************************************");
      }
      catch (Exception ex) {
        LOG.error("*******************************************************");
        LOG.error("setString rBean.getLateSubmission().intValue FAILED!!!");
        LOG.error("*******************************************************");
        stmt1.setString(12, "1");
        ex.printStackTrace();
        LOG.error("*******************************************************");
      }
      LOG.debug ("after 12:  stmt1.executeUpdate() for: " + INSERTGRADE);
      stmt1.executeUpdate();

      // Now, insert the grade record for each item.
      LOG.debug ("prepareStatement(INSERTITEMGRADE);"  );
      stmt2 = conn.prepareStatement(INSERTITEMGRADE);
      LOG.debug ("Creating arrays.\nitemIds"  );
      Object[] itemIds =  g.getItemId().toArray();
      LOG.debug ("itemMaxScores."  );
      Object[] itemMaxScores =  g.getItemMaxScore().toArray();
      LOG.debug ("answerTexts."  );
      Object[] answerTexts =  g.getItemAnswerText().toArray();
      LOG.debug ("itemCorrect."  );
      Object[] itemCorrect =  g.getItemCorrect().toArray();
      LOG.debug ("itemType."  );
      Object[] itemType =  g.getItemType().toArray();
      LOG.debug ("itemScores,"  );
      Object[] itemScores =  g.getItemScore().toArray();
      LOG.debug ("itemScores,"  );
      Object[] itemMedia =  g.getItemMedia().toArray();

      // loop thru each item
      LOG.debug ("Loop thru itemIds.length=" + itemIds.length);
      for(int i = 0; i < itemIds.length; i++)
      {
        LOG.debug ("LOOP number i=" + i);
        LOG.debug("setString 1 itemIds[i]=" + itemIds[i]);
        stmt2.setString(1, ""+itemIds[i]);
        LOG.debug("setString 2 g.getAssessmentResultId()=" +
                  g.getAssessmentResultId());
        stmt2.setString(2, g.getAssessmentResultId());
        LOG.debug("setString 3 itemScores[i]=" + itemScores[i]);
        stmt2.setString(3, ""+itemScores[i]);
        LOG.debug("setString 4 itemMaxScores[i]=" + itemMaxScores[i]);
        stmt2.setString(4, ""+itemMaxScores[i]);
        // we separate multiple answers for a single question with a pipe symbol
        LOG.debug("setString 5 trimPipe:answerTexts[i]=" +
                  trimPipe("" + answerTexts[i]));
        stmt2.setString(5, trimPipe("" + answerTexts[i]));
        String mediaId = "0";
        if ("File Upload".equals(itemType[i]) ||
            "Audio Recording".equals(itemType[i]))
        {
          mediaId = "" + itemMedia[i];
        }
        LOG.debug("setString 6 = "+ mediaId);
        stmt2.setString(6, mediaId); // media id included in HTML, no need for now
        LOG.debug("setString 7 = ''");
        stmt2.setString(7, "");// unused #7 (rationale)
        LOG.debug("setString 8 =''");
        stmt2.setString(8, "");// comments start out blank
        String itemRight = (String) itemCorrect[i];
        // convert True or False into T or F
        String tf = "F";
        if (itemRight.equalsIgnoreCase("True"))
        {
          tf="T";
        }

        LOG.debug("setString 9 =" + tf + "; itemCorrect[i]=" + itemCorrect[i]);
        stmt2.setString(9, tf);
        LOG.debug("setString 10 itemType[i]=" + itemType[i]);
        stmt2.setString(10, ""+itemType[i]);
        LOG.debug ("after 10:  stmt1.executeUpdate() for: " + INSERTITEMGRADE);
        stmt2.executeUpdate();
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
        stmt1.close();
        stmt2.close();
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
        LOG.error(e); throw new Error(e);
//        LOG.error("Failed to Free Connection.");
      }
    }
  }

  /**
   * Trim string and remove trailing "|".
   * Pipe symbol is used for multiple records.
   * @param s the string
   * @return trimmed string
   */
  private String trimPipe(String s){
    if (s==null) return "";
    s = s.trim();
    if (s.endsWith("|"))
    {
     s = s.substring(0,s.length()-1);
    }
    return s;
  }


  /**
   * Models assessment wide results for agents (students).
   * @param assessmentID
   * @param last true if filter by last
   * @return Collection of agent results
   */
  public Collection getAgentTotalResults(String assessmentID, boolean last)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    ArrayList agentResultList = new ArrayList();
    try
    {
      conn = gcm.getConnection();
      stmt = conn.prepareStatement(GETAGENTGRADING);
      stmt.setString(1, assessmentID);
      rs = stmt.executeQuery();

      String lastAgentId = "zzzzzzzz"; // used if last is true

      while(rs.next()) // this is most recent first, sorted by agent
      {
        AgentResults agentResults = new AgentResults();

        // identity
        String agentId = rs.getString("AGENTID");

        // is this a new agent, if not, this is not the last record
        if(last && lastAgentId.equals(agentId))
        {
          continue; //if set to last submission only skip earlier records
        }

        agentResults.setAgentId(agentId);
        String fullName = rs.getString("AGENTNAME");
        agentResults.setFirstName(StringParseUtils.getFirstNameFromName(fullName));
        agentResults.setLastName(StringParseUtils.getLastNameFromName(fullName));
        agentResults.setRole(rs.getString("AGENTROLE"));

        // grading
        agentResults.setAssessmentID(rs.getString("ASSESSMENTID"));
        agentResults.setAssessmentResultID(rs.getString("ASSESSMENTRESULTID"));
        agentResults.setSubmissionDate(rs.getString("RESULTDATE"));
        agentResults.setTotalScore(rs.getString("SCORE"));
        agentResults.setAdjustmentTotalScore(rs.getString("ADJUSTSCORE"));
        agentResults.setFinalScore(rs.getString("FINALSCORE"));
        agentResults.setTotalScoreComments(rs.getString("COMMENTS"));
        agentResults.setLateSubmission(rs.getInt("ISLATE"));

        agentResultList.add(agentResults);
        lastAgentId = agentId; // record the current agent for the next test
      }

      return agentResultList;
    }
    catch(Exception e)
    {
      LOG.error(e);
//      throw new Error(e);
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
        LOG.error("Failed to Free Connection.");
      }
    }
    return null;
  }

  /**
   * Models item (question) results for agents (students).
   * @param assessmentId the assessment id
   * @param itemId the itemid
   * @param last true if get the last submission only
   * @return Collection of agent results
   */
  public Collection getAgentItemResults(String assessmentId, String itemId,
                                        boolean last)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    ArrayList agentResultList = new ArrayList();
    try
    {
      conn = gcm.getConnection();
      stmt = conn.prepareStatement(GETAGENTITEMGRADING);
      stmt.setString(1, assessmentId);
      stmt.setString(2, itemId);
      rs = stmt.executeQuery();

      String lastAgentId = "zzzzzzzz"; // used if last is true

      while(rs.next())
      {
        AgentResults agentResults = new AgentResults();

        // identity
        String agentId = rs.getString("AGENTID");

        // is this a new agent, if not, this is not the last record
        if(last && lastAgentId.equals(agentId))
        {
          continue; //if set to last submission only skip earlier records
        }

        agentResults.setAgentId(agentId);
        String fullName = rs.getString("AGENTNAME");
        agentResults.setFirstName(StringParseUtils.getFirstNameFromName(fullName));
        agentResults.setLastName(StringParseUtils.getLastNameFromName(fullName));
        agentResults.setRole(rs.getString("AGENTROLE"));

        // grading
        agentResults.setAssessmentID(rs.getString("ASSESSMENTID"));
        agentResults.setAssessmentResultID(rs.getString("ASSESSMENTRESULTID"));
        agentResults.setItemID(rs.getString("ITEMID"));
        agentResults.setSubmissionDate(rs.getString("RESULTDATE"));
        agentResults.setTotalScore(rs.getString("ITEMSCORE"));
        agentResults.setComments(rs.getString("COMMENTS"));
        agentResults.setResponse(rs.getString("ANSWERTEXT"));

        // media for file upload or audio recording item
        // left in mediaId field, but apparently html link to showMedia for this
        // id will be used.
        int mediaId = rs.getInt("ANSWERMEDIAID");
        if(mediaId != 0)
        {
          MediaData md = UtilAccessObject.getMediaData(mediaId);
          agentResults.setMediaData(md);
        }

        agentResultList.add(agentResults);
        lastAgentId = agentId; // record the current agent for the next test
      }

      return agentResultList;
    }
    catch(Exception e)
    {
      LOG.error(e);
//      throw new Error(e);
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
        LOG.error("Failed to Free Connection.");
      }
    }
    return null;
  }

  /**
   * Change the grade or grading comment of an assessment.
   * Sets the final score to the score, unless
   *  a) score is an integer
   *  b) adjustScore is a nonzero integer
   *
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
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    int numericScore = 0;
    int numericAdjustScore = 0;
    int numericFinalScore = 0;
    String finalScore = score;
    try
    {
      numericScore = Integer.parseInt(score);

      // default without adjustment is final score is unchanged
      numericFinalScore = numericScore;

      // if we can get a numeric value for adjustment to score
      numericAdjustScore = Integer.parseInt(adjustScore);

      // we will  update the final score
      numericFinalScore = numericScore + numericAdjustScore;
      finalScore = "" + numericFinalScore;
    }
    catch(Exception ex)
    {
      // this will ignore cases of decimal and text grades if we need them later
    }

    try
    {
      conn = gcm.getConnection();
      stmt = conn.prepareStatement(CHANGEASSESSMENTGRADE);

      stmt.setString(1, score);
      stmt.setString(2, adjustScore);
      stmt.setString(3, finalScore);
      stmt.setString(4, comments);
      stmt.setString(5, assessmentId);
      stmt.setString(6, assessmentResultId);

      stmt.executeUpdate();
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
    finally
    {

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
        LOG.error(e); throw new Error(e);
//        LOG.error("Failed to Free Connection.");
      }
    }
  }

  /**
   * Change the grade or grading comment of an item.
   *
   * @todo make this transactional
   *
   * @param score the raw score for the grade
   * @param adjustScore the adjustment to the score
   * @param comments the grading comments
   * @param itemId the id of the item
   * @param assessmentResultId the submission id for the result
   */
  synchronized public void changeItemGrade(
    String score, String comments, String itemId, String assessmentResultId)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    PreparedStatement stmt2 = null;
    PreparedStatement stmt3 = null;
    PreparedStatement stmt4 = null;
    boolean updateAssessment = false;
    ResultSet rs = null;

    try
    {
      conn = gcm.getConnection();

      // record the current score
      LOG.debug("record the current score");
      stmt = conn.prepareStatement(GETITEMSCORE);
      stmt.setString(1, itemId);
      stmt.setString(2, assessmentResultId);
      rs = stmt.executeQuery();

      String currentScore = "";

      if (rs.next())
      {
        currentScore = rs.getString(1);
      }

      // compute the change
      LOG.debug("compute the change");
      double diff = 0.0;

      // doing it this way will be forwardly compatible with non numneric scores
      try {
        Double s1 = new Double(currentScore);
        Double s2 = new Double(score);
        diff  = s2.intValue() - s1.intValue();
      }
      catch (Exception ex) {
        diff = 0.0;
      }

      //update the item score
      LOG.debug("update the item score");
      stmt2 = conn.prepareStatement(CHANGEITEMGRADE);
      stmt2.setString(1, score);
      stmt2.setString(2, comments);
      stmt2.setString(3, itemId);
      stmt2.setString(4, assessmentResultId);

      stmt2.executeUpdate();

      // update the assessment score, if needed
      LOG.debug("update the assessment score, if needed");
      if (diff > 0.00001)
      {
        // get the current assessment score
        LOG.debug("get the current assessment score");
        stmt3 = conn.prepareStatement(GETASSESSMENTSCORE);
        stmt3.setString(1, assessmentResultId);
        rs = stmt3.executeQuery();
        String currentAssessmentScore = "";


        if (rs.next())
        {
          currentAssessmentScore = rs.getString(1);
        }

        // compute and update the new assessment score
        LOG.debug("compute and update the new assessment score");
        try {
          Double as = new Double(currentAssessmentScore);
          double assessmentScore  = as.doubleValue() + diff;

          stmt4 = conn.prepareStatement(RESETASSESSMENTSCORE);
          stmt4.setDouble(1, assessmentScore);
          stmt4.setString(2, assessmentResultId);

          stmt4.executeUpdate();
        }
        catch (Exception ex) {
          // right now we use numeric only, do not throw if support strings
          throw new IllegalArgumentException(
            "non numeric currentAssessmentScore: " + currentAssessmentScore);
        }
        updateAssessment = true;
      }

    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
    finally
    {

      try
      {
        stmt.close();
        stmt2.close();
        if (updateAssessment)
        {
         stmt3.close();
         stmt4.close();
        }

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
        LOG.error(e); throw new Error(e);
//        LOG.error("Failed to Free Connection.");
      }
    }
  }

  // the XML representation from which we are extracting data does not separate
  // out first & last names, this is a very clever (ahem) kluge for that....


  private static final String GETAGENTGRADING =
    "SELECT * FROM ASSESSMENTGRADING WHERE ASSESSMENTID = ? " +
    " ORDER BY AGENTID, RESULTDATE DESC";
  private static final String GETAGENTITEMGRADING =
    "SELECT ASSESSMENTID, a.ASSESSMENTRESULTID, RESULTDATE, AGENTID, AGENTNAME, " +
    " AGENTROLE, MAXSCORE, SCORE, ADJUSTSCORE, FINALSCORE, " +
    " ITEMID, ITEMSCORE, ITEMMAXSCORE, ANSWERTEXT, " +
    " ANSWERMEDIAID, RATIONALE, i.COMMENTS " +
    " FROM ASSESSMENTGRADING a, ITEMGRADING i " +
    " WHERE a.ASSESSMENTRESULTID=i.ASSESSMENTRESULTID AND " +
    " ASSESSMENTID = ? AND ITEMID = ? " +
    " ORDER BY AGENTID, RESULTDATE DESC";
  private static final String INSERTGRADE =
    "INSERT INTO ASSESSMENTGRADING " +
    " (ASSESSMENTID, ASSESSMENTRESULTID, RESULTDATE, AGENTID, AGENTNAME, " +
    " AGENTROLE, MAXSCORE, SCORE, ADJUSTSCORE, FINALSCORE, COMMENTS, ISLATE) " +
    " VALUES " + " (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
  private static final String INSERTITEMGRADE =
    "INSERT INTO ITEMGRADING " +
    " (ITEMID, ASSESSMENTRESULTID, ITEMSCORE, ITEMMAXSCORE, ANSWERTEXT, " +
    " ANSWERMEDIAID, RATIONALE, COMMENTS, ANSWERCORRECT, ITEMTYPE ) " +
    " VALUES " + " (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
  private static final String UPDATEGRADE =
    "UPDATE ASSESSMENTGRADING SET " +
    " RESULTDATE = ?, AGENTID = ?, AGENTNAME = ?, AGENTROLE = ?, " +
    " MAXSCORE = ?, SCORE = ?, ADJUSTSCORE = ?, FINALSCORE = ?, COMMENTS = ?" +
    " WHERE ASSESSMENTID = ? AND ASSESSMENTRESULTID = ?";
  private static final String UPDATEITEMGRADE =
    "UPDATE ITEMGRADING SET " +
    " ITEMSCORE = ?, ITEMMAXSCORE = ?, ANSWERTEXT = ?,  ANSWERMEDIAID = ?, " +
    " RATIONALE = ? , COMMENTS = ?, ANSWERCORRECT = ?, ITEMTYPE = ? " +
    " WHERE ITEMID = ? AND ASSESSMENTRESULTID = ?";
  private static final String GETRESULTEXISTS =
    "SELECT COUNT(ASSESSMENTID) FROM ASSESSMENTGRADING " +
    " WHERE ASSESSMENTRESULTID = ?";
  private static final String CHANGEASSESSMENTGRADE =
    "UPDATE ASSESSMENTGRADING SET " +
    " SCORE = ?, ADJUSTSCORE = ?, FINALSCORE = ?, COMMENTS = ?" +
    " WHERE ASSESSMENTID = ? AND ASSESSMENTRESULTID = ?";
  private static final String CHANGEITEMGRADE =
    "UPDATE ITEMGRADING SET " + " ITEMSCORE = ?,  COMMENTS = ?" +
    " WHERE ITEMID = ? AND ASSESSMENTRESULTID = ?";
  private static final String GETITEMSCORE =
    "SELECT ITEMSCORE FROM ITEMGRADING " +
    " WHERE ITEMID = ? AND ASSESSMENTRESULTID = ?";
  private static final String GETASSESSMENTSCORE =
    "SELECT SCORE FROM ASSESSMENTGRADING " +
    " WHERE ASSESSMENTRESULTID = ?";
  //used only when a change is made to an item score
  private static final String RESETASSESSMENTSCORE =
    "UPDATE ASSESSMENTGRADING SET SCORE = ?" +
    " WHERE ASSESSMENTRESULTID = ?";

}
