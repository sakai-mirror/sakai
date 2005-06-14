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
 * Created on Dec 29, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package test.org.navigoproject.business.entity.properties;

import org.navigoproject.business.entity.Assessment;
import org.navigoproject.business.entity.assessment.model.AssessmentPropertiesImpl;
import org.navigoproject.business.entity.assessment.model.AssessmentTemplatePropertiesImpl;
import org.navigoproject.business.entity.assessment.model.FeedbackModel;
import org.navigoproject.business.entity.assessment.model.ScoringModel;
import org.navigoproject.business.entity.assessment.model.SubmissionModel;
import org.navigoproject.business.entity.properties.PropertyToMetadataTranslator;

import java.sql.Date;

import junit.framework.TestCase;

import test.org.navigoproject.fixture.Log4jFixture;
import test.org.navigoproject.fixture.PathInfoFixture;

/**
 * DOCUMENT ME!
 *
 * @author casong To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public class TestPropertiesToMetadataTranslator
  extends TestCase
{
  private final static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(TestPropertiesToMetadataTranslator.class);
  private Log4jFixture log4jFixture;
  private PathInfoFixture pathInfoFixture;

  /**
   * Creates a new TestPropertiesToMetadataTranslator object.
   *
   * @param name DOCUMENTATION PENDING
   */
  public TestPropertiesToMetadataTranslator(String name)
  {
    super(name);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  protected void setUp()
    throws Exception
  {
    super.setUp();
    log4jFixture = new Log4jFixture(this);
    pathInfoFixture = new PathInfoFixture(this);

    log4jFixture.setUp();
    pathInfoFixture.setUp();

    /** @todo remove this configurator: */
    org.apache.log4j.BasicConfigurator.configure();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  protected void tearDown()
    throws Exception
  {
    log4jFixture.tearDown();
    pathInfoFixture.tearDown();

    log4jFixture = null;
    pathInfoFixture = null;
    super.tearDown();
  }

  /**
   * DOCUMENTATION PENDING
   */
  public static void testTranslateTemplateToMetadata()
  {
    AssessmentPropertiesImpl properties =
      TestPropertiesToMetadataTranslator.initAssessmentProperties();
    AssessmentTemplatePropertiesImpl template =
      TestPropertiesToMetadataTranslator.initAssessmentTemplateProperties();

    PropertyToMetadataTranslator translator =
      new PropertyToMetadataTranslator();
    Assessment assessment =
      translator.translateTemplateToMetadata(template, properties);
    LOG.debug(assessment.stringValue());
  }

  /**
   * DOCUMENTATION PENDING
   */
  public static void testTranslateTemplateToMetadata2()
  {
    AssessmentTemplatePropertiesImpl template =
      TestPropertiesToMetadataTranslator.initAssessmentTemplateProperties();

    PropertyToMetadataTranslator translator =
      new PropertyToMetadataTranslator();
    Assessment assessment = translator.translateTemplateToMetadata(template);
    LOG.debug(assessment.stringValue());
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private static AssessmentPropertiesImpl initAssessmentProperties()
  {
    AssessmentPropertiesImpl properties = new AssessmentPropertiesImpl();

    properties.setAccessGroups(null);

    properties.setAgentId("testAgentId");
    properties.setAutoScoring(true);
    properties.setDisplayChunking("Item by item");

    properties.setDistributionGroups(null);

    properties.setDueDate("some date for due date");
    properties.setEvaluationComponents("");
    properties.setEvaluationDistribution("");

    FeedbackModel feedbackModel = new FeedbackModel();
    Date now = new Date(System.currentTimeMillis());
    feedbackModel.setDatedFeedbackType("Due date");
    feedbackModel.setFeedbackDate(now);
    feedbackModel.setFeedbackType("By Date");
    feedbackModel.setImmediateFeedbackType("Section");
    feedbackModel.setPerQuestionFeedbackType("What's this");
    feedbackModel.setScoreDate(now);
    properties.setFeedbackModel(feedbackModel);

    properties.setInstructorNotification("");
    properties.setItemAccessType("Item by item");
    properties.setItemBookmarking(true);
    properties.setKeywords("");
    properties.setLateHandling("Accept and Mark");
    properties.setMultiPartAllowed(true);
    properties.setObjectives("This is a test.");
    properties.setRubrics("Assessment rubrics");

    ScoringModel scoringModel = new ScoringModel();
    scoringModel.setDefaultQuestionValue(10);
    scoringModel.setFixedTotalScore(100);
    scoringModel.setNumericModel("Sum");
    scoringModel.setScoringType("Numerical");
    properties.setScoringModel(scoringModel);

    SubmissionModel submissionModel = new SubmissionModel();
    submissionModel.setNumberSubmissions("unlimited");
    submissionModel.setSubmissionsAllowed(3);
    properties.setSubmissionModel(submissionModel);

    properties.setTesteeIdentity("anonymous");
    properties.setTesteeNotification("testee notification");

    return properties;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private static AssessmentTemplatePropertiesImpl initAssessmentTemplateProperties()
  {
    AssessmentTemplatePropertiesImpl template =
      new AssessmentTemplatePropertiesImpl();

    /***** set up assessment properties part *******/
    template.setAccessGroups(null);

    template.setAgentId("testAgentId");
    template.setAutoScoring(true);
    template.setDisplayChunking("Item by item");

    template.setDistributionGroups(null);

    template.setDueDate("some date for due date");
    template.setEvaluationComponents("");
    template.setEvaluationDistribution("");

    FeedbackModel feedbackModel = new FeedbackModel();
    Date now = new Date(System.currentTimeMillis());
    feedbackModel.setDatedFeedbackType("Due date");
    feedbackModel.setFeedbackDate(now);
    feedbackModel.setFeedbackType("By Date");
    feedbackModel.setImmediateFeedbackType("Section");
    feedbackModel.setPerQuestionFeedbackType("What's this");
    feedbackModel.setScoreDate(now);
    template.setFeedbackModel(feedbackModel);

    template.setInstructorNotification("");
    template.setItemAccessType("Item by item");
    template.setItemBookmarking(true);
    template.setKeywords("");
    template.setLateHandling("Accept and Mark");
    template.setMultiPartAllowed(true);
    template.setObjectives("This is a test.");
    template.setRubrics("Assessment rubrics");

    ScoringModel scoringModel = new ScoringModel();
    scoringModel.setDefaultQuestionValue(10);
    scoringModel.setFixedTotalScore(100);
    scoringModel.setNumericModel("Sum");
    scoringModel.setScoringType("Numerical");
    template.setScoringModel(scoringModel);

    SubmissionModel submissionModel = new SubmissionModel();
    submissionModel.setNumberSubmissions("unlimited");
    submissionModel.setSubmissionsAllowed(3);
    template.setSubmissionModel(submissionModel);

    template.setTesteeIdentity("anonymous");
    template.setTesteeNotification("testee notification");

    /********set up instructorEditable and studentViewable *******/
    template.setInstructorEditable("dueDate", true);
    template.setStudentViewable("dueDate", true);

    template.setInstructorEditable("multiPartAllowed", true);
    template.setStudentViewable("multiPartAllowed", false);

    template.setInstructorEditable("itemAccessType", true);
    template.setStudentViewable("itemAccessType", false);

    template.setInstructorEditable("itemBookmarking", true);
    template.setStudentViewable("itemBookmarking", true);

    template.setInstructorEditable("displayChunking", true);
    template.setStudentViewable("displayChunking", true);

    template.setInstructorEditable("feedbackType", true);
    template.setStudentViewable("feedbackType", false);

    template.setInstructorEditable("immediateFeedbackType", true);
    template.setStudentViewable("immediateFeedbackType", true);

    template.setInstructorEditable("datedFeedbackType", true);
    template.setStudentViewable("datedFeedbackType", false);

    template.setInstructorEditable("perQuestionFeedbackType", true);
    template.setStudentViewable("perQuestionFeedbackType", true);

    template.setInstructorEditable("feedbackDate", true);
    template.setStudentViewable("feedbackDate", true);

    template.setInstructorEditable("scoreDate", true);
    template.setStudentViewable("scoreDate", true);

    template.setInstructorEditable("numberSubmissions", true);
    template.setStudentViewable("numberSubmissions", true);

    template.setInstructorEditable("submissionsAllowed", true);
    template.setStudentViewable("submissionsAllowed", true);

    template.setInstructorEditable("lateHandling", true);
    template.setStudentViewable("lateHandling", true);

    template.setInstructorEditable("submissionsSaved", true);
    template.setStudentViewable("submissionsSaved", true);

    template.setInstructorEditable("testeeIdentity", true);
    template.setStudentViewable("testeeIdentity", true);

    template.setInstructorEditable("evaluationComponents", true);
    template.setStudentViewable("evaluationComponents", false);

    template.setInstructorEditable("autoScoring", true);
    template.setStudentViewable("autoScoring", false);

    template.setInstructorEditable("scoringType", true);
    template.setStudentViewable("scoringType", false);

    template.setInstructorEditable("defaultQuestionValue", true);
    template.setStudentViewable("defaultQuestionValue", false);

    template.setInstructorEditable("fixedTotalScore", true);
    template.setStudentViewable("fixedTotalScore", true);

    template.setInstructorEditable("testeeNotification", true);
    template.setStudentViewable("testeeNotification", true);

    template.setInstructorEditable("instructorNotification", true);
    template.setStudentViewable("instructorNotification", true);

    return template;
  }
}
