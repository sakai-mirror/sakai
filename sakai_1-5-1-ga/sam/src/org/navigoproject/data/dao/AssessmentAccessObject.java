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

import org.navigoproject.business.entity.AssessmentTemplate;
import org.navigoproject.business.entity.AssessmentTemplateIterator;
import org.navigoproject.business.entity.SectionTemplateIterator;
import org.navigoproject.business.entity.assessment.model.AccessGroup;
import org.navigoproject.business.entity.assessment.model.AssessmentImpl;
import org.navigoproject.business.entity.assessment.model.AssessmentIteratorImpl;
import org.navigoproject.business.entity.assessment.model.AssessmentPropertiesImpl;
import org.navigoproject.business.entity.assessment.model.AssessmentTemplateImpl;
import org.navigoproject.business.entity.assessment.model.AssessmentTemplateIteratorImpl;
import org.navigoproject.business.entity.assessment.model.AssessmentTemplatePropertiesImpl;
import org.navigoproject.business.entity.assessment.model.DistributionGroup;
import org.navigoproject.business.entity.assessment.model.FeedbackModel;
import org.navigoproject.business.entity.assessment.model.MediaData;
import org.navigoproject.business.entity.assessment.model.ScoringModel;
import org.navigoproject.business.entity.assessment.model.SubmissionModel;
import org.navigoproject.business.entity.properties.AssessmentTemplateProperties;
import org.navigoproject.data.GenericConnectionManager;
import org.navigoproject.osid.OsidManagerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import osid.assessment.Assessment;
import osid.assessment.SectionIterator;

import osid.shared.Id;
import osid.shared.SharedManager;

/**
 * <p>
 * This class handles database access for templates and assessments
 * </p>
 *
 * @author <a href="mailto:rgollub@stanford.edu">Rachel Gollub</a>
 * @author <a href="mailto:esmiley@stanford.edu">Ed Smiley</a>
 * @version $Id: AssessmentAccessObject.java,v 1.3 2004/09/13 18:51:59 esmiley.stanford.edu Exp $
 */
public class AssessmentAccessObject
{
  private static final Logger LOG =
    Logger.getLogger(AssessmentAccessObject.class);
  private static final long ALL = 0;
  GenericConnectionManager gcm = null;

  /**
   * Creates a new AssessmentAccessObject object.
   */
  public AssessmentAccessObject()
  {
    gcm = GenericConnectionManager.getInstance();
  }

  /**
   * Get all assessments.
   *
   * @return an iterator for the assessments
   */
  public AssessmentIteratorImpl getAllAssessments()
  {
    LOG.debug("getAllAssessments");

    return getAssessments(ALL);
  }

  /**
   * Get all assessments for a course.
   *
   * @param courseId DOCUMENTATION PENDING
   *
   * @return an iterator for the assessments
   */
  public AssessmentIteratorImpl getAllAssessments(long courseId)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getAllAssessments(long " + courseId + ")");
    }

    return getAssessments(ALL, courseId);
  }

  /**
   * Get all assessments for a course.
   *
   * @param assessmentId DOCUMENTATION PENDING
   *
   * @return an iterator for the assessments
   */
  public AssessmentIteratorImpl getAssessments(long assessmentId)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getAssessments(long " + assessmentId + ")");
    }

    return getAssessments(assessmentId, ALL);
  }

  /**
   * Get all assessments for a course or assessment id.
   *
   * @param assessmentId DOCUMENTATION PENDING
   * @param courseId DOCUMENTATION PENDING
   *
   * @return an iterator for the assessments
   */

  // Note: all other getAssessments methods use this internally.
  public AssessmentIteratorImpl getAssessments(
    long assessmentId, long courseId)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "getAssessments(long " + assessmentId + ", long " + courseId + ")");
    }

    Connection conn = null;
    PreparedStatement stmt = null;
    PreparedStatement stmt2 = null;
    ResultSet rs = null;
    Collection assessments = new ArrayList();
    try
    {
      conn = gcm.getConnection();
      stmt2 = conn.prepareStatement(GETDATES);
      if(assessmentId == ALL)
      {
        if(courseId == ALL)
        {
          stmt = conn.prepareStatement(GETALLASSESSMENTS);
        }
        else
        {
          stmt = conn.prepareStatement(GETCOURSEASSESSMENTS);
          stmt.setLong(1, courseId);
        }
      }
      else
      {
        if(courseId == ALL)
        {
          stmt = conn.prepareStatement(GETASSESSMENT);
          stmt.setLong(1, assessmentId);
        }
        else
        {
          stmt = conn.prepareStatement(GETCOURSEASSESSMENT);
          stmt.setLong(1, courseId);
          stmt.setLong(2, assessmentId);
        }
      }

      rs = stmt.executeQuery();
      while(rs.next())
      {
        SharedManager sm = OsidManagerFactory.createSharedManager();
        AssessmentImpl assessment =
          new AssessmentImpl(sm.getId(rs.getString("ASSESSMENTID")));
        assessment.updateDisplayName(rs.getString("TITLE"));
        assessment.updateDescription(rs.getString("DESCRIPTION"));
        AssessmentPropertiesImpl props = new AssessmentPropertiesImpl();

        // Just get title, description, type if we're getting them all.
        if(assessmentId > 0)
        {
          assessment.updateData(getAssessmentProperties(props, rs));
          if(rs.getLong("TEMPLATEID") != 0)
          {
            AssessmentTemplateIterator ati =
              getTemplate(rs.getLong("TEMPLATEID"));
            if(ati.hasNext())
            {
              props.setAssessmentTemplate(ati.next());
            }
          }

          SectionAccessObject sao = new SectionAccessObject();
          SectionIterator sti = sao.getAllSections(rs.getLong("ASSESSMENTID"));
          while(sti.hasNext())
          {
            assessment.addSection(sti.next());
          }
        }
        else // Get due date
        {
          stmt2.setLong(1, rs.getLong("ASSESSMENTID"));
          stmt2.setString(2, "Full Class");

          ResultSet rs2 = stmt2.executeQuery();
          if(rs2.next())
          {
            props.setDueDate(rs2.getString("DUEDATE"));
            assessment.updateData(props);
          }
        }

        assessments.add(assessment);
      }
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
      throw new Error(e);
    }
    finally
    {
      try
      {
        stmt.close();
      }
      catch(Exception e)
      {
        LOG.error("Statement did not close.", e);
      }

      try
      {
        stmt2.close();
      }
      catch(Exception e)
      {
        LOG.error("Statement2 did not close.", e);
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e.getMessage(), e);
        throw new Error(e);
      }
    }

    Collections.sort((ArrayList) assessments, new AssessmentComparator());

    return new AssessmentIteratorImpl(assessments);
  }

  /**
   * Get all templates.
   *
   * @return an iterator for the assessments
   */
  public AssessmentTemplateIteratorImpl getAllTemplates()
  {
    LOG.debug("getAllTemplates()");

    return getTemplate(ALL);
  }

  /**
   * Get all templates for a course.
   *
   * @param courseId DOCUMENTATION PENDING
   *
   * @return an iterator for the templates
   */
  public AssessmentTemplateIteratorImpl getAllTemplates(long courseId)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getAllTemplates(long " + courseId + ")");
    }

    return getTemplate(ALL, courseId);
  }

  /**
   * Get all templates for a course.
   *
   * @param assessmentId DOCUMENTATION PENDING
   *
   * @return an iterator for the templates
   */
  public AssessmentTemplateIteratorImpl getTemplate(long assessmentId)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getTemplate(long " + assessmentId + ")");
    }

    return getTemplate(assessmentId, ALL);
  }

  /**
   * Get all templates for a course or assessment id.
   *
   * @param assessmentId DOCUMENTATION PENDING
   * @param courseId DOCUMENTATION PENDING
   *
   * @return an iterator for the templates
   */

  // Note: all other getTemplate methods use this internally.
  public AssessmentTemplateIteratorImpl getTemplate(
    long assessmentId, long courseId)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "getTemplate(long " + assessmentId + ", long " + courseId + ")");
    }

    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    Collection templates = new ArrayList();
    try
    {
      conn = gcm.getConnection();
      if(assessmentId == ALL)
      {
        if(courseId == ALL)
        {
          stmt = conn.prepareStatement(GETALLTEMPLATES);
        }
        else
        {
          stmt = conn.prepareStatement(GETCOURSETEMPLATES);
          stmt.setLong(1, courseId);
        }
      }
      else
      {
        if(courseId == ALL)
        {
          stmt = conn.prepareStatement(GETTEMPLATE);
          stmt.setLong(1, assessmentId);
        }
        else
        {
          stmt = conn.prepareStatement(GETCOURSETEMPLATE);
          stmt.setLong(1, courseId);
          stmt.setLong(2, assessmentId);
        }
      }

      rs = stmt.executeQuery();
      while(rs.next())
      {
        SharedManager sm = OsidManagerFactory.createSharedManager();
        AssessmentTemplateImpl template =
          new AssessmentTemplateImpl(sm.getId(rs.getString("ASSESSMENTID")));
        template.setTemplateName(rs.getString("TEMPLATENAME"));
        template.setTemplateAuthor(rs.getString("TEMPLATEAUTHOR"));
        template.setComments(rs.getString("TEMPLATECOMMENTS"));

        // Just get name, comments, type if we're getting them all.
        if(assessmentId > 0)
        {
          AssessmentTemplatePropertiesImpl props =
            new AssessmentTemplatePropertiesImpl();
          template.updateData(getAssessmentProperties(props, rs));

          //SectionAccessObject sao = new SectionAccessObject();
          //SectionTemplateIterator sti =
          //  sao.getAllTemplates(rs.getLong("ASSESSMENTID"));
          //while(sti.hasNext())
          //{
          //  template.addSection(sti.next());
          //}
        }
        else // Get last modified
        {
          AssessmentTemplateProperties props =
            new AssessmentTemplatePropertiesImpl();
          props.setLastModified(rs.getTimestamp("LASTMODIFIED"));
          template.updateData(props);
        }

        templates.add(template);
      }
      rs.close();
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
      throw new Error(e);
    }
    finally
    {
      try
      {
        stmt.close();
      }
      catch(Exception e)
      {
        LOG.error("Statement did not close.", e);
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e.getMessage(), e);
        throw new Error(e);
      }
    }

    return new AssessmentTemplateIteratorImpl(templates);
  }

  /**
   * Save an Assessment object
   *
   * @param assessment to persist
   */
  public void saveAssessment(Assessment assessment)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("saveAssessment(Assessment " + assessment + ")");
    }

    Connection conn = null;
    PreparedStatement stmt = null;
    PreparedStatement substmt = null;
    PreparedStatement substmt2 = null;
    ResultSet rs = null;
    ResultSet rs2 = null;
    boolean isTemplate = false;

    try
    {
      if(assessment instanceof AssessmentTemplate)
      {
        isTemplate = true;
      }

      LOG.debug("SAVING ASSESSMENT");
      conn = gcm.getConnection();
      int id = new Integer(((Id) assessment.getId()).getIdString()).intValue();
      stmt = conn.prepareStatement(ISUPDATETEMPLATE);
      stmt.setInt(1, id);
      if(LOG.isDebugEnabled())
      {
        LOG.debug("PreparedStatement stmt = " + stmt.toString());
      }

      rs = stmt.executeQuery();
      boolean isUpdate = false;
      if(rs.next())
      {
        isUpdate = true;
      }

      LOG.error("isUpdate: " + isUpdate + ", isTemplate: " + isTemplate);
      LOG.debug("isUpdate: " + isUpdate + ", isTemplate: " + isTemplate);
      stmt.close();

      int typeid = 1;
      if(assessment.getAssessmentType() != null)
      {
        stmt = conn.prepareStatement(GETTYPEID);
        stmt.setString(1, assessment.getAssessmentType().getDescription());
        stmt.setString(2, "assessment");
        if(LOG.isDebugEnabled())
        {
          LOG.debug("PreparedStatement stmt = " + stmt.toString());
        }

        rs = stmt.executeQuery();
        if(rs.next())
        {
          typeid = rs.getInt(1);
        }
      }

      int i = 1;
      if(isUpdate)
      {
        if(isTemplate)
        {
          stmt = conn.prepareStatement(UPDATETEMPLATE);
        }
        else
        {
          stmt = conn.prepareStatement(UPDATEASSESSMENT);
        }
      }
      else
      {
        if(isTemplate)
        {
          stmt = conn.prepareStatement(INSERTTEMPLATE);
        }
        else
        {
          stmt = conn.prepareStatement(INSERTASSESSMENT);
        }

        stmt.setInt(i++, id);
      }

      AssessmentPropertiesImpl props =
        (AssessmentPropertiesImpl) assessment.getData();

      if(isTemplate)
      {
        stmt.setString(
          i++, ((AssessmentTemplateImpl) assessment).getTemplateName());
        stmt.setString(
          i++, ((AssessmentTemplateImpl) assessment).getTemplateAuthor());
        stmt.setString(
          i++, ((AssessmentTemplateImpl) assessment).getComments());
      }
      else
      {
        if(props.getAssessmentTemplate() != null)
        {
          stmt.setInt(
            i++,
            new Integer(props.getAssessmentTemplate().getId().toString()).intValue());
        }
        else
        {
          stmt.setInt(i++, 0);
        }

        stmt.setString(i++, assessment.getDisplayName());
        stmt.setString(i++, assessment.getDescription());
      }

      LOG.debug("Typeid = " + i);
      stmt.setInt(i++, typeid);
      stmt.setString(i++, props.getObjectives());
      stmt.setString(i++, props.getKeywords());
      stmt.setString(i++, props.getRubrics());
      stmt.setString(i++, props.getItemAccessType());
      stmt.setString(i++, (props.getItemBookmarking() ? "T" : "F"));
      stmt.setString(i++, (props.getMultiPartAllowed() ? "T" : "F"));
      stmt.setString(i++, props.getDisplayChunking());
      stmt.setString(i++, props.getQuestionNumbering());
      LOG.debug("Display chunking: " + props.getDisplayChunking());
      LOG.debug("ID = " + id);
      stmt.setString(i++, props.getLateHandling());
      stmt.setString(i++, props.getAutoSave());
      stmt.setString(i++, props.getSubmissionsSaved());
      stmt.setString(i++, props.getTesteeNotification());
      stmt.setString(i++, props.getInstructorNotification());
      stmt.setString(i++, props.getEvaluationDistribution());
      stmt.setString(i++, props.getTesteeIdentity());
      stmt.setString(i++, props.getEvaluationComponents());
      stmt.setString(i++, (props.getAutoScoring() ? "T" : "F"));
      LOG.debug("Model start: " + i);

      ScoringModel model = (ScoringModel) props.getScoringModel();
      if(model != null)
      {
        stmt.setString(i++, model.getScoringType());
        stmt.setString(i++, model.getNumericModel());
        stmt.setInt(i++, model.getDefaultQuestionValue());
        stmt.setInt(i++, model.getFixedTotalScore());
      }
      else
      {
        stmt.setNull(i++, Types.VARCHAR);
        stmt.setNull(i++, Types.VARCHAR);
        stmt.setNull(i++, Types.INTEGER);
        stmt.setNull(i++, Types.INTEGER);
      }

      LOG.debug("Feedback type: " + i);
      stmt.setString(i++, props.getFeedbackType());
      stmt.setString(i++, props.getFeedbackComponents());

      FeedbackModel fmodel = (FeedbackModel) props.getFeedbackModel();
      if(fmodel != null)
      {
        // stmt.setString(i++, fmodel.getFeedbackType());   // moved up for the new navigo UI.
        stmt.setString(i++, fmodel.getImmediateFeedbackType());
        stmt.setString(i++, fmodel.getDatedFeedbackType());
        stmt.setString(i++, fmodel.getPerQuestionFeedbackType());
        if(fmodel.getFeedbackDate() != null)
        {
          stmt.setTimestamp(
            i++, new Timestamp(fmodel.getFeedbackDate().getTime()));
        }
        else
        {
          stmt.setNull(i++, Types.TIMESTAMP);
        }

        if(fmodel.getScoreDate() != null)
        {
          stmt.setTimestamp(
            i++, new Timestamp(fmodel.getScoreDate().getTime()));
        }
        else
        {
          stmt.setNull(i++, Types.TIMESTAMP);
        }
      }
      else
      {
        //stmt.setNull(i++, Types.VARCHAR); // FEEDBACKTYPE and
        stmt.setNull(i++, Types.VARCHAR); // FEEDBACKCOMPONENTS are moved up for the new navigo UI
        stmt.setNull(i++, Types.VARCHAR);
        stmt.setNull(i++, Types.VARCHAR);
        stmt.setNull(i++, Types.TIMESTAMP);
        stmt.setNull(i++, Types.TIMESTAMP);
      }

      // Grading
      LOG.debug("Anonymous " + i);
      stmt.setString(i++, (props.getAnonymousGrading() ? "T" : "F"));
      stmt.setString(i++, props.getToGradebook());
      stmt.setString(i++, props.getRecordedScore());

      if(isUpdate)
      {
        // Last modified
        stmt.setTimestamp(i++, new Timestamp(new java.util.Date().getTime()));

        stmt.setInt(i++, id);
      }
      else
      {
        if(isTemplate)
        {
          stmt.setString(i++, "T");
        }
        else
        {
          stmt.setString(i++, "F");
        }
      }

      // if create new assessment/assessment template,
      // set courseId and agentId - daisyf 10/21/03
      if(! isUpdate)
      {
        LOG.debug("is create not update");
        if(props.getCourseId() != null)
        {
          stmt.setLong(i++, props.getCourseId().longValue());
        }
        else
        {
          stmt.setLong(i++, 0);
        }

        if(props.getAgentId() != null)
        {
          stmt.setString(i++, props.getAgentId());
        }
        else
        {
          stmt.setString(i++, props.getAgentId());
        }

        // Date created
        stmt.setTimestamp(i++, new Timestamp(new java.util.Date().getTime()));

        // Last modified
        stmt.setTimestamp(i++, new Timestamp(new java.util.Date().getTime()));
      }

      stmt.executeUpdate();
      stmt.close();

      /*
         // Get the old ones
         Collection oldMedia = new ArrayList();
         stmt = conn.prepareStatement(GETMEDIA);
         stmt.setInt(1, id);
         rs = stmt.executeQuery();
         while(rs.next())
         {
           oldMedia.add(new Integer(rs.getInt("MEDIAID")));
         }
         stmt.close();

         // Delete the links to all of them
         stmt = conn.prepareStatement(DELETEMEDIA);
         stmt.setInt(1, id);
         stmt.executeUpdate();
         stmt.close();
         if(
           (props.getMediaCollection() != null) &&
             (props.getMediaCollection().size() > 0))
         {
           // Save the new ones
           Iterator iter = props.getMediaCollection().iterator();
           int[] ids = new int[props.getMediaCollection().size()];
           int ii = 0;
           while(iter.hasNext())
           {
             MediaData data = (MediaData) iter.next();
             ids[ii++] = UtilAccessObject.saveMedia(data);
           }
           // Create links to the new ones
           LOG.debug("Inserting...");
           stmt = conn.prepareStatement(INSERTMEDIA);
           for(ii = 0; ii < ids.length; ii++)
           {
             stmt.setInt(1, id);
             LOG.debug("Next ID: " + ids[ii]);
             stmt.setInt(2, ids[ii]);
             stmt.setInt(3, ii);
             stmt.executeUpdate();
           }
           stmt.close();
         }
         // Now that we've linked them all, delete the ones that aren't
         // linked.
         Iterator iter = oldMedia.iterator();
         while(iter.hasNext())
         {
           // This will only delete media that have no links
           UtilAccessObject.deleteMedia(((Integer) iter.next()).intValue());
         }
       */

      // XXX @todo This section has to change SIGNIFICANTLY for 1.0 --
      // it's a mess!
      if(props.getSubmissionModel() != null)
      {
        LOG.debug("Doing Submission model");
        boolean updateSM = false;
        int smid = 0;
        int ii = 1;
        if(
          ((SubmissionModel) props.getSubmissionModel()).getSubmissionModelId() > 0)
        {
          updateSM = true;
          smid =
            ((SubmissionModel) props.getSubmissionModel()).getSubmissionModelId();
        }
        else
        {
          smid = new Integer(getSubmissionModelId().toString()).intValue();
        }

        if(updateSM)
        {
          stmt = conn.prepareStatement(UPDATESUBMISSIONMODEL);
        }
        else
        {
          stmt = conn.prepareStatement(GETSUBMISSIONMODELID);
          stmt.setInt(1, id);
          rs = stmt.executeQuery();
          long subid = 0;
          if(rs.next())
          {
            subid = rs.getLong(1);
          }

          stmt.close();

          if(subid > 0)
          {
            stmt = conn.prepareStatement(DELETESUBMISSIONMODEL);
            stmt.setLong(1, subid);
            stmt.executeUpdate();
            stmt.close();
          }

          stmt = conn.prepareStatement(INSERTSUBMISSIONMODEL);
          stmt.setInt(ii++, smid);
        }

        stmt.setString(
          ii++,
          ((SubmissionModel) props.getSubmissionModel()).getNumberSubmissions());
        stmt.setInt(
          ii++,
          ((SubmissionModel) props.getSubmissionModel()).getSubmissionsAllowed());
        if(updateSM)
        {
          stmt.setInt(ii++, smid);
        }

        if(LOG.isDebugEnabled())
        {
          LOG.debug("PreparedStatement stmt = " + stmt.toString());
        }

        stmt.executeUpdate();
        stmt.close();

        // Add pointer to model
        LOG.debug(
          "Added submission model: " +
          ((SubmissionModel) props.getSubmissionModel()).getNumberSubmissions() +
          " as " + smid);
        stmt = conn.prepareStatement(UPDATESUBMISSIONMODELID);
        stmt.setLong(1, smid);
        stmt.setLong(2, id);
        stmt.executeUpdate();
        stmt.close();
      }

      /*
         if(
           (props.getDistributionGroups() != null) &&
             ! props.getDistributionGroups().isEmpty())
         {
           // Delete all existing distribution groups, so we can
           // create new ones.  We need to revisit this, but we're
           // under a tight deadline... -rmg
           stmt = conn.prepareStatement(GETDISTRIBUTIONIDS);
           substmt = conn.prepareStatement(DELETEDISTRIBUTIONID);
           substmt2 = conn.prepareStatement(DELETEDISTRIBUTIONGROUP);
           stmt.setInt(1, id);
           rs = stmt.executeQuery();
           while(rs.next())
           {
             substmt.setLong(1, rs.getLong("DISTRIBUTIONGROUPID"));
             substmt.executeUpdate();
             substmt2.setLong(1, rs.getLong("DISTRIBUTIONGROUPID"));
             substmt2.executeUpdate();
           }
           substmt.close();
           substmt2.close();
           stmt.close();
           stmt = conn.prepareStatement(INSERTDISTRIBUTIONGROUP);
           substmt = conn.prepareStatement(INSERTDISTRIBUTIONID);
           iter = props.getDistributionGroups().iterator();
           while(iter.hasNext())
           {
             DistributionGroup group = (DistributionGroup) iter.next();
             long did = new Long(getDistributionId().toString()).longValue();
             Iterator i2 = group.getDistributionTypes().iterator();
             String types = "";
             while(i2.hasNext())
             {
               types = types + i2.next().toString() + ":";
             }
             if(types.endsWith(":"))
             {
               types = types.substring(0, types.length() - 2);
             }
             // Take off last ":"
             stmt.setString(1, group.getDistributionGroup());
             stmt.setString(2, types);
             stmt.setLong(3, did);
             stmt.executeUpdate();
             substmt.setLong(1, id);
             substmt.setLong(2, did);
             substmt.executeUpdate();
           }
           stmt.close();
           substmt.close();
         }
         if(
           (props.getAccessGroups() != null) &&
             ! props.getAccessGroups().isEmpty())
         {
           // Delete all existing access groups too, so we can
           // create new ones.  We need to revisit this, but we're
           // under a tight deadline... -rmg
           stmt = conn.prepareStatement(GETACCESSIDS);
           substmt = conn.prepareStatement(DELETEACCESSID);
           substmt2 = conn.prepareStatement(DELETEACCESSGROUP);
           stmt.setInt(1, id);
           rs = stmt.executeQuery();
           while(rs.next())
           {
             substmt.setLong(1, rs.getLong("ACCESSGROUPID"));
             substmt.executeUpdate();
             substmt2.setLong(1, rs.getLong("ACCESSGROUPID"));
             substmt2.executeUpdate();
           }
           substmt.close();
           substmt2.close();
           stmt.close();
           stmt = conn.prepareStatement(INSERTACCESSGROUP);
           substmt = conn.prepareStatement(INSERTACCESSID);
           iter = props.getAccessGroups().iterator();
           while(iter.hasNext())
           {
             AccessGroup group = (AccessGroup) iter.next();
             long gid = new Long(getAccessId().toString()).longValue();
             stmt.setString(1, group.getName());
             stmt.setString(2, group.getReleaseType());
             if(
               (group.getReleaseType() != null) &&
                 group.getReleaseType().equals("1") &&
                 (group.getReleaseDate() != null))
             {
               stmt.setTimestamp(
                 3, new Timestamp(group.getReleaseDate().getTime()));
             }
             else
             {
               stmt.setNull(3, Types.TIMESTAMP);
             }
             stmt.setString(4, group.getReleaseWhen());
             stmt.setString(5, group.getReleaseScore());
             stmt.setString(6, group.getRetractType());
             if(
               (group.getRetractType() != null) &&
                 group.getRetractType().equals("2") &&
                 (group.getRetractDate() != null))
             {
               stmt.setTimestamp(
                 7, new Timestamp(group.getRetractDate().getTime()));
             }
             else
             {
               stmt.setNull(7, Types.TIMESTAMP);
             }
             stmt.setString(8, group.getDueDateModel());
             if(
               (group.getDueDateModel() != null) &&
                 group.getDueDateModel().equals("1") &&
                 (group.getDueDate() != null))
             {
               stmt.setTimestamp(9, new Timestamp(group.getDueDate().getTime()));
             }
             else
             {
               stmt.setNull(9, Types.TIMESTAMP);
             }
             stmt.setString(10, (group.getRetryAllowed() ? "T" : "F"));
             stmt.setString(11, (group.getTimedAssessment() ? "T" : "F"));
             stmt.setString(12, group.getMinutes());
             stmt.setString(13, (group.getPasswordAccess() ? "T" : "F"));
             stmt.setString(14, group.getPassword());
             stmt.setString(15, (group.getIsActive() ? "T" : "F"));
             stmt.setString(16, group.getIpAccess());
             stmt.setLong(17, gid);
             stmt.executeUpdate();
             substmt.setLong(1, id);
             substmt.setLong(2, gid);
             substmt.executeUpdate();
           }
           stmt.close();
           substmt.close();
         }
       */
      if(isTemplate)
      {
        LOG.debug("ISTEMPLATE");
        AssessmentTemplatePropertiesImpl tprops =
          (AssessmentTemplatePropertiesImpl) props;

        /*  Don't have this table any more
        stmt = conn.prepareStatement(DELETEPROPS);
        stmt.setInt(1, id);
        stmt.executeUpdate();
        stmt.close();
        */

        ArrayList fields = new ArrayList();
        for(
          Enumeration e = tprops.getInstructorEditableMap().keys();
            e.hasMoreElements();)
        {
          fields.add((String) e.nextElement());
        }

        for(
          Enumeration e = tprops.getInstructorViewableMap().keys();
            e.hasMoreElements();)
        {
          String field = (String) e.nextElement();
          if(! fields.contains(field))
          {
            fields.add(field);
          }
        }

        for(
          Enumeration e = tprops.getStudentViewableMap().keys();
            e.hasMoreElements();)
        {
          String field = (String) e.nextElement();
          if(! fields.contains(field))
          {
            fields.add(field);
          }
        }

        stmt = conn.prepareStatement(INSERTPROPS);
        Iterator iter = fields.iterator();
        while(iter.hasNext())
        {
          String field = (String) iter.next();
          stmt.setInt(1, id);
          stmt.setString(2, field);
          stmt.setString(3, (tprops.isInstructorEditable(field) ? "T" : "F"));
          stmt.setString(4, (tprops.isInstructorViewable(field) ? "T" : "F"));
          stmt.setString(5, (tprops.isStudentViewable(field) ? "T" : "F"));
          stmt.executeUpdate();
        }

        stmt.close();
      }

      // Now save the section template.
      //SectionAccessObject sao = new SectionAccessObject();
      //SectionIterator si = assessment.getSections();
      //int j = 0;
      //while(si.hasNext())
      //{
      //  sao.saveSection(si.next(), id, ++j);
      //}
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
      throw new Error(e);
    }
    finally
    {
      try
      {
        stmt.close();
      }
      catch(Exception e)
      {
        LOG.error("Statement did not close.", e);
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e.getMessage(), e);
        throw new Error(e);
      }
    }
  }

  /*
   * For beta 2: Check to see if a template or assessment can be deleted,
   * and send back: <ul>
   * <li> 0: No dependencies, can just delete.
   * <li> 1: It's a template, and has assessments
   * <li> 2: It's a template, and has taken assessments
   * <li> 3: It's an assessment, and has been taken
   * <li> 4: There was an error, and the state is unknown
   */
  public int checkDelete(long id)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("checkDelete(long " + id + ")");
    }

    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    int results = 4;

    try
    {
      conn = gcm.getConnection();

      // Is this a template?
      boolean isTemplate = true;
      stmt = conn.prepareStatement(ISTEMPLATE);
      stmt.setLong(1, id);
      rs = stmt.executeQuery();
      if(rs.next())
      {
        if(rs.getString("ISTEMPLATE").equals("F"))
        {
          isTemplate = false;
        }
      }

      stmt.close();

      Collection assessments = new ArrayList();
      if(isTemplate)
      {
        stmt = conn.prepareStatement(GETASSESSMENTSFORTEMPLATE);
        stmt.setLong(1, id);
        rs = stmt.executeQuery();
        while(rs.next())
        {
          assessments.add(new Long(rs.getLong("ASSESSMENTID")));
        }

        stmt.close();
      }
      else
      {
        assessments.add(new Long(id));
      }

      boolean isTaken = false;
      Iterator iter = assessments.iterator();
      while(iter.hasNext() && ! isTaken)
      {
        stmt = conn.prepareStatement(ISTAKEN);
        stmt.setLong(1, ((Long) iter.next()).longValue());
        rs = stmt.executeQuery();
        if(rs.next())
        {
          isTaken = true;
        }

        stmt.close();
      }

      stmt.close();

      if(isTemplate && isTaken)
      {
        results = 2;
      }
      else if(isTemplate && (assessments.size() > 0))
      {
        results = 1;
      }
      else if(! isTemplate && isTaken)
      {
        results = 3;
      }

      // If none of those, it's fine to delete.
      else
      {
        results = 0;
      }
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
      throw new Error(e);
    }
    finally
    {
      try
      {
        stmt.close();
      }
      catch(Exception e)
      {
        LOG.error("Statement did not close.", e);
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e.getMessage(), e);
        throw new Error(e);
      }
    }

    // Return the state
    return results;
  }

  /**
   * Delete a template, along with all related values.  This also deletes the
   * SECTIONTEMPLATE and ITEMTEMPLATE, since they're actually part of the
   * template itself, and not separately deleteable entities.
   *
   * @param templateId DOCUMENTATION PENDING
   */
  public void deleteTemplate(long templateId)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("deleteTemplate(long " + templateId + ")");
    }

    Connection conn = null;
    PreparedStatement stmt = null;
    PreparedStatement substmt = null;
    ResultSet rs = null;

    /* Don't have these tables any more
    try
    {
      //conn = gcm.getConnection();

      // Delete the template properties
      stmt = conn.prepareStatement(DELETEPROPS);
      stmt.setLong(1, templateId);
      stmt.executeUpdate();
      stmt.close();

      // The next two may look confusing, but there's a reason they
      // do what they do.  First, there should only be one section
      // and one item for a template, but errors sometimes produce
      // two, which is why I use "while" instead of "if".  Second,
      // these have to be done individually because sectiontemplates
      // and itemtemplates are not stored in ASSESSMENTSECTION
      // and SECTIONITEM -- they refer directly back to the
      // ASSESSMENTTEMPLATEID instead. --rmg
      // Get itemId, and delete it.
      ItemAccessObject iao = new ItemAccessObject();
      stmt = conn.prepareStatement(GETITEMID);
      substmt = conn.prepareStatement(DELETEITEMPROPS);
      stmt.setLong(1, templateId);
      rs = stmt.executeQuery();
      while(rs.next())
      {
        long itemId = rs.getLong(1);

        // Delete the properties
        substmt.setLong(1, itemId);
        substmt.executeUpdate();

        // Delete the item
        iao.deleteItem(itemId);
      }

      substmt.close();
      stmt.close();

      // Get sectionId, and delete it.
      SectionAccessObject sao = new SectionAccessObject();
      stmt = conn.prepareStatement(GETSECTIONID);
      substmt = conn.prepareStatement(DELETESECTIONPROPS);
      stmt.setLong(1, templateId);
      rs = stmt.executeQuery();
      while(rs.next())
      {
        long sectionId = rs.getLong(1);

        // Delete the section properties
        substmt.setLong(1, sectionId);
        substmt.executeUpdate();

        // Delete the section
        sao.deleteSection(sectionId);
      }

      substmt.close();
      stmt.close();

      // ********************************************
      // *** REMOVE THIS AFTER BETA 2 ***************
      // ********************************************
      stmt = conn.prepareStatement(GETASSESSMENTSFORTEMPLATE);
      stmt.setLong(1, templateId);
      rs = stmt.executeQuery();
      while(rs.next())
      {
        deleteAssessment(rs.getLong("ASSESSMENTID"));
      }

      stmt.close();

      // ************ TO HERE ***********************
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
      throw new Error(e);
    }
    finally
    {
      try
      {
        stmt.close();
      }
      catch(Exception e)
      {
        LOG.error("Statement did not close.", e);
      }

      try
      {
        substmt.close();
      }
      catch(Exception e)
      {
        LOG.error("Substatement did not close.", e);
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e.getMessage(), e);
        throw new Error(e);
      }
    }
    */
    // Now delete the assessment table entry and extras.
    deleteAssessment(templateId);
  }

  /**
   * Delete an assessment, along with all related sections, items, media, etc.
   *
   * @param assessmentId DOCUMENTATION PENDING
   */
  public void deleteAssessment(long assessmentId)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("deleteAssessment(long " + assessmentId + ")");
    }

    Connection conn = null;
    PreparedStatement stmt = null;
    PreparedStatement substmt = null;
    PreparedStatement substmt2 = null;
    ResultSet rs = null;

    try
    {
      conn = gcm.getConnection();

      // Delete the submission model (this is first because it depends
      // on assessment still being there.
      stmt = conn.prepareStatement(GETSUBMISSIONMODELID);
      substmt = conn.prepareStatement(DELETESUBMISSIONMODEL);
      stmt.setLong(1, assessmentId);
      rs = stmt.executeQuery();
      if(rs.next())
      {
        substmt.setLong(1, rs.getLong("SUBMISSIONMODELID"));
        substmt.executeUpdate();
        substmt.close();
      }

      stmt.close();

      // Delete the actual assessment entry
      stmt = conn.prepareStatement(DELETEASSESSMENT);
      stmt.setLong(1, assessmentId);
      stmt.executeUpdate();
      stmt.close();

      /* Don't use these for templates
      // Delete distribution groups
      stmt = conn.prepareStatement(GETDISTRIBUTIONIDS);
      substmt = conn.prepareStatement(DELETEDISTRIBUTIONID);
      substmt2 = conn.prepareStatement(DELETEDISTRIBUTIONGROUP);
      stmt.setLong(1, assessmentId);
      rs = stmt.executeQuery();
      while(rs.next())
      {
        substmt.setLong(1, rs.getLong("DISTRIBUTIONGROUPID"));
        substmt.executeUpdate();
        substmt2.setLong(1, rs.getLong("DISTRIBUTIONGROUPID"));
        substmt2.executeUpdate();
      }

      substmt.close();
      substmt2.close();
      stmt.close();

      // Delete all access groups
      stmt = conn.prepareStatement(GETACCESSIDS);
      substmt = conn.prepareStatement(DELETEACCESSID);
      substmt2 = conn.prepareStatement(DELETEACCESSGROUP);
      stmt.setLong(1, assessmentId);
      rs = stmt.executeQuery();
      while(rs.next())
      {
        substmt.setLong(1, rs.getLong("ACCESSGROUPID"));
        substmt.executeUpdate();
        substmt2.setLong(1, rs.getLong("ACCESSGROUPID"));
        substmt2.executeUpdate();
      }

      substmt.close();
      substmt2.close();
      stmt.close();

      // Delete all sections in this assessment
      SectionAccessObject sao = new SectionAccessObject();
      stmt = conn.prepareStatement(GETSECTIONIDS);
      stmt.setLong(1, assessmentId);
      rs = stmt.executeQuery();
      while(rs.next())
      {
        sao.deleteSection(rs.getInt("SECTIONID"));
      }

      stmt.close();

      // Delete media
      stmt = conn.prepareStatement(DELETEMEDIA);
      stmt.setLong(1, assessmentId);
      stmt.executeUpdate();
      stmt.close();

      stmt = conn.prepareStatement(GETMEDIA);
      stmt.setLong(1, assessmentId);
      rs = stmt.executeQuery();
      while(rs.next())
      {
        UtilAccessObject.deleteMedia(rs.getInt("MEDIAID"));
      }

      stmt.close();

      // ************************************************
      // ********REMOVE THIS AFTER BETA 2****************
      // ************************************************
      // This removes all taken assessments and their subparts
      // for each assessment.  Something we're not going to do
      // after beta2.
      // These statements are up here instead of at the bottom
      // of the file so they're easier to clean up later. If
      // we end up keeping them, please move them to the
      // bottom. --rmg
      stmt =
        conn.prepareStatement(
          "SELECT ASSESSMENTTAKENID FROM ASSESSMENTTAKEN WHERE ASSESSMENTID = ?");
      substmt =
        conn.prepareStatement(
          "SELECT SECTIONTAKENID FROM SECTIONTAKEN WHERE ASSESSMENTTAKENID = ?");
      substmt2 =
        conn.prepareStatement(
          "SELECT ITEMTAKENID FROM ITEMTAKEN WHERE SECTIONTAKENID = ?");
      PreparedStatement stmt2 =
        conn.prepareStatement("DELETE FROM ANSWERTAKEN WHERE ITEMTAKENID = ?");
      PreparedStatement stmt3 =
        conn.prepareStatement("DELETE FROM ITEMTAKEN WHERE SECTIONTAKENID = ?");
      PreparedStatement stmt4 =
        conn.prepareStatement(
          "DELETE FROM SECTIONTAKEN WHERE ASSESSMENTTAKENID = ?");

      stmt.setLong(1, assessmentId);
      rs = stmt.executeQuery();
      while(rs.next())
      {
        substmt.setLong(1, rs.getLong("ASSESSMENTTAKENID"));
        ResultSet rs2 = substmt.executeQuery();
        while(rs2.next())
        {
          substmt2.setLong(1, rs2.getLong("SECTIONTAKENID"));
          ResultSet rs3 = substmt2.executeQuery();
          while(rs3.next())
          {
            stmt2.setLong(1, rs3.getLong("ITEMTAKENID"));
            stmt2.executeUpdate();
          }

          stmt3.setLong(1, rs2.getLong("SECTIONTAKENID"));
          stmt3.executeUpdate();
        }

        stmt4.setLong(1, rs.getLong("ASSESSMENTTAKENID"));
        stmt4.executeUpdate();
      }

      stmt4.close();
      stmt3.close();
      stmt2.close();
      substmt2.close();
      substmt.close();
      stmt.close();

      stmt =
        conn.prepareStatement(
          "DELETE FROM ASSESSMENTTAKEN WHERE ASSESSMENTID = ?");
      stmt.setLong(1, assessmentId);
      stmt.executeUpdate();
      stmt.close();

      // ****************TO HERE*************************

      */
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
      throw new Error(e);
    }
    finally
    {
      try
      {
        stmt.close();
      }
      catch(Exception e)
      {
        LOG.error("Statement did not close.", e);
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e.getMessage(), e);
        throw new Error(e);
      }
    }
  }

  /**
   * Fill in assessment properties from a given resultset.
   *
   * @param props the assessment properties
   * @param rs the database resultset
   *
   * @return
   */
  public AssessmentPropertiesImpl getAssessmentProperties(
    AssessmentPropertiesImpl props, ResultSet rs)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "getAssessmentProperties(AssessmentPropertiesImpl " + props +
        ", ResultSet " + rs + ")");
    }

    Connection conn = null;
    PreparedStatement stmt = null;
    PreparedStatement substmt = null;
    ResultSet rs2 = null;
    try
    {
      props.setObjectives(rs.getString("OBJECTIVE"));
      props.setKeywords(rs.getString("KEYWORDS"));
      props.setRubrics(rs.getString("RUBRICS"));
      props.setLateHandling(rs.getString("LATEHANDLING"));
      props.setAutoSave(rs.getString("AUTOSAVE"));
      props.setSubmissionsSaved(rs.getString("SUBMISSIONSSAVED"));
      props.setItemAccessType(rs.getString("ITEMACCESSTYPE"));
      if(rs.getString("ITEMBOOKMARKING") != null)
      {
        props.setItemBookmarking(
          (rs.getString("ITEMBOOKMARKING").equals("T") ? true : false));
      }

      if(rs.getString("MULTIPARTALLOWED") != null)
      {
        props.setMultiPartAllowed(
          (rs.getString("MULTIPARTALLOWED").equals("T") ? true : false));
      }

      props.setDisplayChunking(rs.getString("DISPLAYCHUNKING"));
      props.setQuestionNumbering(rs.getString("QUESTIONNUMBERING"));
      props.setTesteeNotification(rs.getString("TESTEENOTIFICATION"));
      props.setInstructorNotification(rs.getString("INSTRUCTORNOTIFICATION"));
      props.setEvaluationComponents(rs.getString("EVALUATIONCOMPONENTS"));
      props.setTesteeIdentity(rs.getString("TESTEEIDENTITY"));
      props.setEvaluationDistribution(rs.getString("EVALUATIONDISTRIBUTION"));
      if(rs.getString("AUTOSCORING") != null)
      {
        props.setAutoScoring(
          (rs.getString("AUTOSCORING").equals("T") ? true : false));
      }

      props.setType(UtilAccessObject.getType(rs.getInt("TYPEID")));
      ScoringModel model = new ScoringModel();
      model.setScoringType(rs.getString("SCORINGTYPE"));
      model.setNumericModel(rs.getString("NUMERICMODEL"));
      model.setDefaultQuestionValue(rs.getInt("DEFAULTQUESTIONVALUE"));
      model.setFixedTotalScore(rs.getInt("FIXEDTOTALSCORE"));
      props.setScoringModel(model);
      FeedbackModel fmodel = new FeedbackModel();
      fmodel.setFeedbackType(rs.getString("FEEDBACKTYPE"));
      fmodel.setImmediateFeedbackType(rs.getString("IMMFEEDBACKTYPE"));
      fmodel.setDatedFeedbackType(rs.getString("DATEDFEEDBACKTYPE"));
      fmodel.setPerQuestionFeedbackType(rs.getString("PERQFEEDBACKTYPE"));
      fmodel.setFeedbackDate(rs.getTimestamp("FEEDBACKDATE"));
      fmodel.setScoreDate(rs.getTimestamp("SCOREDATE"));
      props.setFeedbackModel(fmodel);

      // Feedback
      props.setFeedbackType(rs.getString("FEEDBACKTYPE"));
      props.setFeedbackComponents(rs.getString("FEEDBACKCOMPONENTS"));

      // add the following properties to support the idea that an
      // assessment (and its template) is created by an agent and
      // the assessment can belong to a course - daisyf 10/21/03
      props.setCourseId(
        (rs.getObject("COURSEID") == null) ? new Long("0")
                                           : new Long(rs.getLong("COURSEID")));
      props.setAgentId(
        (rs.getObject("AGENTID") == null) ? "" : rs.getString("AGENTID"));

      // Grading
      if(rs.getString("ANONYMOUSGRADING") != null)
      {
        props.setAnonymousGrading(
          (rs.getString("ANONYMOUSGRADING").equals("T") ? true : false));
      }
      else
      {
        props.setAnonymousGrading(false);
      }

      props.setToGradebook(rs.getString("TOGRADEBOOK"));
      props.setRecordedScore(rs.getString("RECORDEDSCORE"));
      props.setLastModified(rs.getTimestamp("LASTMODIFIED"));
      conn = gcm.getConnection();

      /*
         stmt = conn.prepareStatement(GETMEDIA);
         stmt.setInt(1, rs.getInt("ASSESSMENTID"));
         rs2 = stmt.executeQuery();
         while(rs2.next())
         {
           props.addMedia(UtilAccessObject.getMediaData(rs2.getInt("MEDIAID")));
         }
         stmt.close();
       */
      if(rs.getInt("SUBMISSIONMODELID") > 0)
      {
        SubmissionModel submissionModel = new SubmissionModel();
        submissionModel.setSubmissionModelId(rs.getInt("SUBMISSIONMODELID"));
        stmt = conn.prepareStatement(GETSUBMISSIONMODEL);
        stmt.setInt(1, rs.getInt("SUBMISSIONMODELID"));
        rs2 = stmt.executeQuery();
        if(rs2.next())
        {
          submissionModel.setNumberSubmissions(
            rs2.getString("SUBMISSIONMODEL"));
          submissionModel.setSubmissionsAllowed(
            rs2.getInt("NUMOFSUBMISSIONSALLOWED"));
        }

        stmt.close();
        props.setSubmissionModel(submissionModel);
      }

      /*
         stmt = conn.prepareStatement(GETDISTRIBUTIONIDS);
         substmt = conn.prepareStatement(GETDISTRIBUTIONGROUP);
         stmt.setLong(1, rs.getLong("ASSESSMENTID"));
         rs2 = stmt.executeQuery();
         while(rs2.next())
         {
           substmt.setLong(1, rs2.getLong("DISTRIBUTIONGROUPID"));
           ResultSet rs3 = substmt.executeQuery();
           while(rs3.next())
           {
             DistributionGroup group = new DistributionGroup();
             group.setDistributionGroup(rs3.getString("NAME"));
             String types = rs3.getString("DISTRIBUTIONTYPES");
             StringTokenizer st = new StringTokenizer(types, ":");
             while(st.hasMoreTokens())
             {
               group.addDistributionType(st.nextToken());
             }
             props.addDistributionGroup(group);
           }
         }
         stmt.close();
         substmt.close();
         stmt = conn.prepareStatement(GETACCESSIDS);
         substmt = conn.prepareStatement(GETACCESSGROUP);
         stmt.setLong(1, rs.getLong("ASSESSMENTID"));
         rs2 = stmt.executeQuery();
         while(rs2.next())
         {
           substmt.setLong(1, rs2.getLong("ACCESSGROUPID"));
           ResultSet rs3 = substmt.executeQuery();
           while(rs3.next())
           {
             LOG.debug(
               "I'm reading from group " + rs2.getLong("ACCESSGROUPID") + ", " +
               rs3.getString("NAME"));
             AccessGroup group = new AccessGroup();
             group.setName(rs3.getString("NAME"));
             group.setReleaseType(rs3.getString("RELEASETYPE"));
             LOG.debug(
               "Setting release date to " + rs3.getTimestamp("RELEASEDATE"));
             group.setReleaseDate(rs3.getTimestamp("RELEASEDATE"));
             LOG.debug("Set.");
             group.setReleaseWhen(rs3.getString("RELEASEWHEN"));
             group.setReleaseScore(rs3.getString("RELEASESCORE"));
             group.setRetractType(rs3.getString("RETRACTTYPE"));
             group.setRetractDate(rs3.getTimestamp("RETRACTDATE"));
             group.setDueDateModel(rs3.getString("DUEDATETYPE"));
             group.setDueDate(rs3.getTimestamp("DUEDATE"));
             // This is a kludge until we get the access groups thing
             // straightened out --rmg
             if(group.getName().equals("Full Class"))
             {
               props.setDueDate(rs3.getString("DUEDATE"));
             }
             if(
               (rs3.getTimestamp("DUEDATE") != null) &&
                 rs3.getTimestamp("DUEDATE").before(new java.util.Date()))
             {
               props.setIsLate(true);
             }
             else
             {
               props.setIsLate(false);
             }
             group.setRetryAllowed(
               (rs3.getString("RETRYALLOWED").equals("T") ? true : false));
             group.setTimedAssessment(
               (rs3.getString("TIMEDASSESSMENT").equals("T") ? true : false));
             group.setMinutes(rs3.getString("MINUTES"));
             group.setPasswordAccess(
               (rs3.getString("PASSWORDACCESS").equals("T") ? true : false));
             group.setPassword(rs3.getString("PASSWORD"));
             group.setIsActive(
               (rs3.getString("ISACTIVE").equals("T") ? true : false));
             group.setIpAccess(rs3.getString("IPACCESS"));
             group.setId(rs3.getLong("ACCESSGROUPID"));
             props.addAccessGroup(group);
           }
         }
         stmt.close();
         substmt.close();
       */
      if(props instanceof AssessmentTemplatePropertiesImpl)
      {
        AssessmentTemplatePropertiesImpl tprops =
          (AssessmentTemplatePropertiesImpl) props;
        stmt = conn.prepareStatement(GETPROPS);
        stmt.setInt(1, rs.getInt("ASSESSMENTID"));
        rs2 = stmt.executeQuery();
        while(rs2.next())
        {
          String field = rs2.getString("FIELD");
          if(rs2.getString("ISINSTRUCTORVIEWABLE").equals("T"))
          {
            tprops.setInstructorViewable(field, true);
          }
          else
          {
            tprops.setInstructorViewable(field, false);
          }

          if(rs2.getString("ISINSTRUCTOREDITABLE").equals("T"))
          {
            tprops.setInstructorEditable(field, true);
          }
          else
          {
            tprops.setInstructorEditable(field, false);
          }

          if(rs2.getString("ISSTUDENTVIEWABLE").equals("T"))
          {
            tprops.setStudentViewable(field, true);
          }
          else
          {
            tprops.setStudentViewable(field, false);
          }
        }
      }
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
      throw new Error(e);
    }
    finally
    {
      try
      {
        stmt.close();
      }
      catch(Exception e)
      {
        LOG.error("Statement did not close.", e);
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e.getMessage(), e);
        throw new Error(e);
      }
    }

    return props;
  }

  /**
   * Create a new assessment id for a template or assessment. In addition,
   * create across reference to the author and course.
   *
   * @param courseId the corse for which it is created
   * @param agentId the author/creator
   *
   * @return Id encapsulating the assessment/template key
   */
  public synchronized Id getAssessmentId(long courseId, String agentId)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "getAssessmentId(long " + courseId + ", String " + agentId + ")");
    }

    Id id = UtilAccessObject.getNewId("ASSESSMENT");

    // todo: write xref for id.getIdString(), courseId, agentId goes here...
    return id;
  }

  /**
   * Create a new assessment id for a template or assessment.
   *
   * @return Id encapsulating the assessment/template key
   */
  public synchronized Id getAssessmentId()
  {
    LOG.debug("getAssessmentId()");

    return UtilAccessObject.getNewId("ASSESSMENT");
  }

  /**
   * Create a new submission model.
   *
   * @return Id encapsulating the submission model key
   */
  public synchronized Id getSubmissionModelId()
  {
    LOG.debug("getSubmissionModelId()");

    return UtilAccessObject.getNewId("SUBMISSIONMODEL");
  }

  /**
   * Create a new distribution.
   *
   * @return Id encapsulating the distribution key
   */
  public synchronized Id getDistributionId()
  {
    LOG.debug("getDistributionId()");

    return UtilAccessObject.getNewId("DISTRIBUTIONGROUP");
  }

  /**
   * Create a new access.
   *
   * @return Id encapsulating the access key
   */
  public synchronized Id getAccessId()
  {
    LOG.debug("getAccessId()");

    return UtilAccessObject.getNewId("ACCESSGROUP");
  }

  public static final String GETALLASSESSMENTS =
    "SELECT * FROM ASSESSMENT WHERE ISTEMPLATE = 'F'";
  public static final String GETCOURSEASSESSMENTS =
    "SELECT * FROM ASSESSMENT WHERE ISTEMPLATE = 'F' and COURSEID = ?";
  public static final String GETASSESSMENT =
    "SELECT * FROM ASSESSMENT WHERE ISTEMPLATE= 'F' and ASSESSMENTID = ?";
  public static final String GETCOURSEASSESSMENT =
    "SELECT * FROM ASSESSMENT WHERE ISTEMPLATE = 'F' and COURSEID = ? and ASSESSMENTID = ?";
  public static final String GETALLTEMPLATES =
    "SELECT * FROM ASSESSMENT WHERE ISTEMPLATE = 'T' ORDER BY LASTMODIFIED DESC";
  public static final String GETCOURSETEMPLATES =
    "SELECT * FROM ASSESSMENT WHERE ISTEMPLATE = 'T' and COURSEID = ? ORDER BY LASTMODIFIED DESC";
  public static final String GETTEMPLATE =
    "SELECT * FROM ASSESSMENT WHERE ISTEMPLATE = 'T' AND ASSESSMENTID = ?";
  public static final String GETCOURSETEMPLATE =
    "SELECT * FROM ASSESSMENT WHERE ISTEMPLATE = 'T' and COURSEID = ? and ASSESSMENTID = ?";
  public static final String ISUPDATETEMPLATE =
    "SELECT ASSESSMENTID FROM ASSESSMENT WHERE ASSESSMENTID = ?";
  public static final String GETTYPEID =
    "SELECT TYPEID FROM STANFORDTYPE WHERE DESCRIPTION=? AND KEYWORD=?";
  public static final String INSERTTEMPLATE =
    "INSERT INTO ASSESSMENT (ASSESSMENTID, TEMPLATENAME, TEMPLATEAUTHOR, TEMPLATECOMMENTS, TYPEID, OBJECTIVE, KEYWORDS, RUBRICS, ITEMACCESSTYPE, ITEMBOOKMARKING, MULTIPARTALLOWED, DISPLAYCHUNKING, QUESTIONNUMBERING, LATEHANDLING, AUTOSAVE, SUBMISSIONSSAVED, TESTEENOTIFICATION, INSTRUCTORNOTIFICATION, EVALUATIONDISTRIBUTION, TESTEEIDENTITY, EVALUATIONCOMPONENTS, AUTOSCORING, SCORINGTYPE, NUMERICMODEL, DEFAULTQUESTIONVALUE, FIXEDTOTALSCORE, FEEDBACKTYPE,FEEDBACKCOMPONENTS, IMMFEEDBACKTYPE, DATEDFEEDBACKTYPE, PERQFEEDBACKTYPE, FEEDBACKDATE, SCOREDATE, ANONYMOUSGRADING, TOGRADEBOOK, RECORDEDSCORE, ISTEMPLATE, COURSEID, AGENTID, DATECREATED, LASTMODIFIED) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
  public static final String UPDATETEMPLATE =
    "UPDATE ASSESSMENT SET TEMPLATENAME=?, TEMPLATEAUTHOR=?, TEMPLATECOMMENTS=?, TYPEID=?, OBJECTIVE=?, KEYWORDS=?, RUBRICS=?, ITEMACCESSTYPE=?, ITEMBOOKMARKING=?, MULTIPARTALLOWED=?, DISPLAYCHUNKING=?, QUESTIONNUMBERING=?, LATEHANDLING=?, AUTOSAVE=?, SUBMISSIONSSAVED=?, TESTEENOTIFICATION=?, INSTRUCTORNOTIFICATION=?, EVALUATIONDISTRIBUTION=?, TESTEEIDENTITY=?, EVALUATIONCOMPONENTS=?, AUTOSCORING=?, SCORINGTYPE=?, NUMERICMODEL=?, DEFAULTQUESTIONVALUE=?, FIXEDTOTALSCORE=?, FEEDBACKTYPE=?, FEEDBACKCOMPONENTS=?, IMMFEEDBACKTYPE=?, DATEDFEEDBACKTYPE=?, PERQFEEDBACKTYPE=?, FEEDBACKDATE=?, SCOREDATE=?, ANONYMOUSGRADING=?, TOGRADEBOOK=?, RECORDEDSCORE=?, LASTMODIFIED=? WHERE ASSESSMENTID = ?";

  // The following two are out of date now, and can no longer be used.  --rmg
  public static final String INSERTASSESSMENT =
    "INSERT INTO ASSESSMENT (ASSESSMENTID, TEMPLATEID, TITLE, DESCRIPTION, TYPEID, OBJECTIVE, KEYWORDS, RUBRICS, ITEMACCESSTYPE, ITEMBOOKMARKING, MULTIPARTALLOWED, DISPLAYCHUNKING, LATEHANDLING, SUBMISSIONSSAVED, TESTEENOTIFICATION, INSTRUCTORNOTIFICATION, EVALUATIONDISTRIBUTION, TESTEEIDENTITY, EVALUATIONCOMPONENTS, AUTOSCORING, SCORINGTYPE, NUMERICMODEL, DEFAULTQUESTIONVALUE, FIXEDTOTALSCORE, FEEDBACKTYPE, IMMFEEDBACKTYPE, DATEDFEEDBACKTYPE, PERQFEEDBACKTYPE, FEEDBACKDATE, SCOREDATE, ISTEMPLATE, COURSEID, AGENTID, DATECREATED, LASTMODIFIED) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
  public static final String UPDATEASSESSMENT =
    "UPDATE ASSESSMENT SET TEMPLATEID=?, TITLE=?, DESCRIPTION=?, TYPEID=?, OBJECTIVE=?, KEYWORDS=?, RUBRICS=?, ITEMACCESSTYPE=?, ITEMBOOKMARKING=?, MULTIPARTALLOWED=?, DISPLAYCHUNKING=?, LATEHANDLING=?,SUBMISSIONSSAVED=?, TESTEENOTIFICATION=?, INSTRUCTORNOTIFICATION=?, EVALUATIONDISTRIBUTION=?, TESTEEIDENTITY=?, EVALUATIONCOMPONENTS=?, AUTOSCORING=?, SCORINGTYPE=?, NUMERICMODEL=?, DEFAULTQUESTIONVALUE=?, FIXEDTOTALSCORE=?, FEEDBACKTYPE=?, IMMFEEDBACKTYPE=?, DATEDFEEDBACKTYPE=?, PERQFEEDBACKTYPE=?, FEEDBACKDATE=?, SCOREDATE=?, LASTMODIFIED=? WHERE ASSESSMENTID = ?";
  public static final String GETSUBMISSIONMODEL =
    "SELECT * FROM SUBMISSIONMODEL WHERE SUBMISSIONMODELID=?";
  public static final String INSERTSUBMISSIONMODEL =
    "INSERT INTO SUBMISSIONMODEL (SUBMISSIONMODELID, SUBMISSIONMODEL, NUMOFSUBMISSIONSALLOWED) VALUES (?,?,?)";
  public static final String UPDATESUBMISSIONMODEL =
    "UPDATE SUBMISSIONMODEL SET SUBMISSIONMODEL=?, NUMOFSUBMISSIONSALLOWED=? WHERE SUBMISSIONMODELID = ?";
  public static final String DELETEPROPS =
    "DELETE FROM ASSESSMENTTEMPLATE WHERE ASSESSMENTID = ?";
  public static final String INSERTPROPS =
    "INSERT INTO ASSESSMENTTEMPLATE (ASSESSMENTID, FIELD, ISINSTRUCTOREDITABLE, ISINSTRUCTORVIEWABLE, ISSTUDENTVIEWABLE) VALUES (?,?,?,?,?)";
  public static final String GETPROPS =
    "SELECT * FROM ASSESSMENTTEMPLATE WHERE ASSESSMENTID = ?";
  public static final String GETMEDIA =
    "SELECT MEDIAID FROM ASSESSMENTMEDIA WHERE ASSESSMENTID = ? ORDER BY POSITION";
  public static final String DELETEMEDIA =
    "DELETE FROM ASSESSMENTMEDIA WHERE ASSESSMENTID = ?";
  public static final String INSERTMEDIA =
    "INSERT INTO ASSESSMENTMEDIA (ASSESSMENTID, MEDIAID, POSITION) VALUES(?,?,?)";
  public static final String GETDISTRIBUTIONIDS =
    "SELECT DISTRIBUTIONGROUPID FROM ASSESSMENTDISTRIBUTION WHERE ASSESSMENTID = ?";
  public static final String GETDISTRIBUTIONGROUP =
    "SELECT * FROM DISTRIBUTIONGROUP WHERE DISTRIBUTIONGROUPID = ?";
  public static final String DELETEDISTRIBUTIONID =
    "DELETE FROM ASSESSMENTDISTRIBUTION WHERE DISTRIBUTIONGROUPID=?";
  public static final String DELETEDISTRIBUTIONGROUP =
    "DELETE FROM DISTRIBUTIONGROUP WHERE DISTRIBUTIONGROUPID = ?";
  public static final String INSERTDISTRIBUTIONID =
    "INSERT INTO ASSESSMENTDISTRIBUTION (ASSESSMENTID, DISTRIBUTIONGROUPID) VALUES (?,?)";
  public static final String INSERTDISTRIBUTIONGROUP =
    "INSERT INTO DISTRIBUTIONGROUP (NAME, DISTRIBUTIONTYPES, DISTRIBUTIONGROUPID) VALUES (?,?,?)";
  public static final String GETACCESSIDS =
    "SELECT ACCESSGROUPID FROM ASSESSMENTACCESSGROUP WHERE ASSESSMENTID = ?";
  public static final String GETACCESSGROUP =
    "SELECT * FROM ACCESSGROUP WHERE ACCESSGROUPID = ?";
  public static final String DELETEACCESSID =
    "DELETE FROM ASSESSMENTACCESSGROUP WHERE ACCESSGROUPID=?";
  public static final String DELETEACCESSGROUP =
    "DELETE FROM ACCESSGROUP WHERE ACCESSGROUPID = ?";
  public static final String INSERTACCESSID =
    "INSERT INTO ASSESSMENTACCESSGROUP (ASSESSMENTID, ACCESSGROUPID) VALUES (?,?)";
  public static final String INSERTACCESSGROUP =
    "INSERT INTO ACCESSGROUP (NAME, RELEASETYPE, RELEASEDATE, RELEASEWHEN, RELEASESCORE, RETRACTTYPE, RETRACTDATE, DUEDATETYPE, DUEDATE, RETRYALLOWED, TIMEDASSESSMENT, MINUTES, PASSWORDACCESS, PASSWORD, ISACTIVE, IPACCESS, ACCESSGROUPID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
  public static final String ISTEMPLATE =
    "SELECT ISTEMPLATE FROM ASSESSMENT WHERE ASSESSMENTID = ?";
  public static final String DELETEASSESSMENT =
    "DELETE FROM ASSESSMENT WHERE ASSESSMENTID = ?";
  public static final String GETSECTIONIDS =
    "SELECT SECTIONID FROM ASSESSMENTSECTION WHERE ASSESSMENTID = ?";
  public static final String GETSUBMISSIONMODELID =
    "SELECT SUBMISSIONMODELID FROM ASSESSMENT WHERE ASSESSMENTID = ?";
  public static final String DELETESUBMISSIONMODEL =
    "DELETE FROM SUBMISSIONMODEL WHERE SUBMISSIONMODELID = ?";
  public static final String UPDATESUBMISSIONMODELID =
    "UPDATE ASSESSMENT SET SUBMISSIONMODELID = ? WHERE ASSESSMENTID = ?";
  public static final String GETSECTIONID =
    "SELECT SECTIONID FROM SECTION WHERE ASSESSMENTTEMPLATEID = ?";
  public static final String GETITEMID =
    "SELECT ITEMID FROM ITEM WHERE ASSESSMENTTEMPLATEID = ?";
  public static final String DELETESECTIONPROPS =
    "DELETE FROM SECTIONTEMPLATE WHERE SECTIONID = ?";
  public static final String DELETEITEMPROPS =
    "DELETE FROM ITEMTEMPLATE WHERE ITEMID = ?";
  public static final String GETASSESSMENTSFORTEMPLATE =
    "SELECT ASSESSMENTID FROM ASSESSMENT WHERE TEMPLATEID = ?";
  public static final String ISTAKEN =
    "SELECT ASSESSMENTTAKENID FROM ASSESSMENTTAKEN WHERE ASSESSMENTID = ?";
  public static final String GETDATES =
    "SELECT DUEDATE FROM ASSESSMENTACCESSGROUP, ACCESSGROUP WHERE ASSESSMENTACCESSGROUP.ASSESSMENTID = ? AND ASSESSMENTACCESSGROUP.ACCESSGROUPID = ACCESSGROUP.ACCESSGROUPID AND ACCESSGROUP.NAME = ?";
}


/**
 * DOCUMENTATION PENDING
 *
 * @author $author$
 * @version $Id: AssessmentAccessObject.java,v 1.3 2004/09/13 18:51:59 esmiley.stanford.edu Exp $
 */
class AssessmentComparator
  implements Comparator
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(AssessmentComparator.class);

  /**
   * DOCUMENTATION PENDING
   *
   * @param o1 DOCUMENTATION PENDING
   * @param o2 DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public int compare(Object o1, Object o2)
  {
    if((o1 == null) || (o2 == null))
    {
      return 0;
    }

    if(! (o1 instanceof Assessment) || ! (o2 instanceof Assessment))
    {
      LOG.debug("not assessments");

      return 0;
    }

    try
    {
      AssessmentPropertiesImpl props1 =
        (AssessmentPropertiesImpl) ((Assessment) o1).getData();
      AssessmentPropertiesImpl props2 =
        (AssessmentPropertiesImpl) ((Assessment) o2).getData();

      if((props1 == null) && (props2 != null))
      {
        return 1;
      }

      if((props2 == null) && (props1 != null))
      {
        return -1;
      }

      if((props1 == null) && (props2 == null))
      {
        return 0;
      }

      /*  May use this later when we have more groups than one
         ArrayList groups1 = (ArrayList) props1.getAccessGroups();
         AccessGroup group1 = null;

         ArrayList groups2 = (ArrayList) props2.getAccessGroups();
         AccessGroup group2 = null;

         if (groups1 != null && !groups1.isEmpty())
         {
           Iterator iter  = groups1.iterator();
           while (iter.hasNext())
           {
             AccessGroup tmp = (AccessGroup) iter.next();
             if (tmp.getName().equals("Full Class"))
             {
               group1 = tmp;
               break;
             }
           }
         }

         if (groups2 != null && !groups2.isEmpty())
         {
           Iterator iter  = groups2.iterator();
           while (iter.hasNext())
           {
             AccessGroup tmp = (AccessGroup) iter.next();
             if (tmp.getName().equals("Full Class"))
             {
               group2 = tmp;
               break;
             }
           }
         }
       */
      java.util.Date date1 = null;
      try
      {
        date1 = Timestamp.valueOf(props1.getDueDate());
      }
      catch(Exception e)
      {
        // It's null -- that's no longer an error.
      }

      java.util.Date date2 = null;
      try
      {
        date2 = Timestamp.valueOf(props2.getDueDate());
      }
      catch(Exception e)
      {
        // It's null; that's no longer an error.
      }

      if((date1 == null) && (date2 != null))
      {
        return -1;
      }

      if((date2 == null) && (date1 != null))
      {
        return 1;
      }

      if((date1 == null) && (date2 == null))
      {
        return 0;
      }

      if(date1.before(date2))
      {
        return 1;
      }

      if(date1.after(date2))
      {
        return -1;
      }

      return 0;
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
      throw new Error(e);
    }
  }
}
