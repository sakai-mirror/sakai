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

package org.navigoproject.ui.web.form.edit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorForm;

/**
 * <p>
 * Title: NavigoProject.org
 * </p>
 *
 * <p>
 * Description: AAM - form class for edit/evaluation.jsp
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
 * @author Marc Brierley
 * @version $Id: EvaluationForm.java,v 1.1.1.1 2004/07/28 21:32:07 rgollub.stanford.edu Exp $
 */
public class EvaluationForm
  extends ValidatorForm /* was ActionForm */
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(EvaluationForm.class);
    
  private String evaluationDistribution_toInstructor;
  private String evaluationDistribution_toTAs;
  private String evaluationDistribution_toSectionGrader;
  private String evaluationDistribution_toReviewGroup;
  private String evaluationDistribution_toTestee;
  private boolean evaluationDistribution_isInstructorViewable;
  private boolean evaluationDistribution_isInstructorEditable;
  private boolean evaluationDistribution_isStudentViewable;
  private String testeeIdentity;
  private boolean testeeIdentity_isInstructorViewable;
  private boolean testeeIdentity_isInstructorEditable;
  private boolean testeeIdentity_isStudentViewable;
  private String evaluationComponents_scores;
  private String evaluationComponents_commentsForQuestions;
  private String evaluationComponents_commentsForParts;
  private String evaluationComponents_commentForAssess;
  private boolean evaluationComponents_isInstructorViewable;
  private boolean evaluationComponents_isInstructorEditable;
  private boolean evaluationComponents_isStudentViewable;
  private boolean autoScoring;
  private boolean autoScoring_isInstructorViewable;
  private boolean autoScoring_isInstructorEditable;
  private boolean autoScoring_isStudentViewable;
  private String scoringType;
  private boolean scoringType_isInstructorViewable;
  private boolean scoringType_isInstructorEditable;
  private boolean scoringType_isStudentViewable;
  private String numericModel;
  private boolean numericModel_isInstructorViewable;
  private boolean numericModel_isInstructorEditable;
  private boolean numericModel_isStudentViewable;
  private String fixedTotalScore;
  private String defaultQuestionValue;
  private boolean defaultQuestionValue_isInstructorViewable;
  private boolean defaultQuestionValue_isInstructorEditable;
  private boolean defaultQuestionValue_isStudentViewable;

  /**
   * Creates a new EvaluationForm object.
   */
  public EvaluationForm()
  {
    super();
    resetFields();
  }

  /*  public ActionErrors validate(ActionMapping actionMapping,
     HttpServletRequest httpServletRequest)
     {
        ActionErrors errors = super.validate(mapping, request);
        if (errors==null) errors = new ActionErrors();
        if (errors.empty())
        {
          if (notGood(mapping,request)) errors.add(ActionErrors.GLOBAL_ERROR,new
          ActionError("errors.notGood","goodProperty"));
        }
        if (errors.empty()) return null;
        return errors;
        return (errors);
        if (this.name == null || this.name.length() == 0)
        {
        }
        if (this.description == null || this.description.length() == 0)
        {
        }
     }
   */
  public void reset(
    ActionMapping actionMapping, HttpServletRequest httpServletRequest)
  {
    HttpSession session = httpServletRequest.getSession(true);
    if(session.getAttribute("editorRole").equals("templateEditor"))
    { // This is so we don't remove the correct values from the session
      this.evaluationDistribution_isInstructorViewable = false;
      this.evaluationDistribution_isInstructorEditable = false;
      this.testeeIdentity_isInstructorViewable = false;
      this.testeeIdentity_isInstructorEditable = false;
      this.evaluationComponents_isInstructorViewable = false;
      this.evaluationComponents_isInstructorEditable = false;
      this.autoScoring_isInstructorViewable = false;
      this.autoScoring_isInstructorEditable = false;
      this.scoringType_isInstructorViewable = false;
      this.scoringType_isInstructorEditable = false;
      this.numericModel_isInstructorViewable = false;
      this.numericModel_isInstructorEditable = false;
      this.defaultQuestionValue_isInstructorViewable = false;
      this.defaultQuestionValue_isInstructorEditable = false;
    }

    if(
      session.getAttribute("editorRole").equals("templateEditor") ||
        evaluationDistribution_isInstructorEditable)
    {
      evaluationDistribution_toInstructor = "off";
      evaluationDistribution_toTAs = "off";
      evaluationDistribution_toSectionGrader = "off";
      evaluationDistribution_toReviewGroup = "off";
      evaluationDistribution_toTestee = "off";
    }

    if(
      session.getAttribute("editorRole").equals("templateEditor") ||
        evaluationComponents_isInstructorEditable)
    {
      evaluationComponents_scores = "off";
      evaluationComponents_commentsForQuestions = "off";
      evaluationComponents_commentsForParts = "off";
      evaluationComponents_commentForAssess = "off";
    }

    if(session.getAttribute("editorRole").equals("templateEditor"))
    {
      this.evaluationDistribution_isStudentViewable = false;
      this.testeeIdentity_isStudentViewable = false;
      this.evaluationComponents_isStudentViewable = false;
      this.autoScoring_isStudentViewable = false;
      this.scoringType_isStudentViewable = false;
      this.numericModel_isStudentViewable = false;
      this.defaultQuestionValue_isStudentViewable = false;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param editorRole DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public int getLength(String editorRole)
  {
    if(editorRole.equals("templateEditor"))
    {
      return 7;
    }
    else
    {
      int i = 0;
      if(
        evaluationDistribution_isInstructorEditable ||
          evaluationDistribution_isInstructorViewable)
      {
        i++;
      }

      if(
        testeeIdentity_isInstructorEditable ||
          testeeIdentity_isInstructorViewable)
      {
        i++;
      }

      if(
        evaluationComponents_isInstructorEditable ||
          evaluationComponents_isInstructorViewable)
      {
        i++;
      }

      if(autoScoring_isInstructorEditable || autoScoring_isInstructorViewable)
      {
        i++;
      }

      if(scoringType_isInstructorEditable || scoringType_isInstructorViewable)
      {
        i++;
      }

      if(numericModel_isInstructorEditable ||
          numericModel_isInstructorViewable)
      {
        i++;
      }

      if(
        defaultQuestionValue_isInstructorEditable ||
          defaultQuestionValue_isInstructorViewable)
      {
        i++;
      }

      return i;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getEvaluationDistribution_toInstructor()
  {
    return this.evaluationDistribution_toInstructor;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param evaluationDistribution_toInstructor DOCUMENTATION PENDING
   */
  public void setEvaluationDistribution_toInstructor(
    String evaluationDistribution_toInstructor)
  {
    if(evaluationDistribution_toInstructor != null)
    {
      this.evaluationDistribution_toInstructor =
        evaluationDistribution_toInstructor;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getEvaluationDistribution_toTAs()
  {
    return this.evaluationDistribution_toTAs;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param evaluationDistribution_toTAs DOCUMENTATION PENDING
   */
  public void setEvaluationDistribution_toTAs(
    String evaluationDistribution_toTAs)
  {
    if(evaluationDistribution_toTAs != null)
    {
      this.evaluationDistribution_toTAs = evaluationDistribution_toTAs;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getEvaluationDistribution_toSectionGrader()
  {
    return this.evaluationDistribution_toSectionGrader;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param evaluationDistribution_toSectionGrader DOCUMENTATION PENDING
   */
  public void setEvaluationDistribution_toSectionGrader(
    String evaluationDistribution_toSectionGrader)
  {
    if(evaluationDistribution_toSectionGrader != null)
    {
      this.evaluationDistribution_toSectionGrader =
        evaluationDistribution_toSectionGrader;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getEvaluationDistribution_toReviewGroup()
  {
    return this.evaluationDistribution_toReviewGroup;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param evaluationDistribution_toReviewGroup DOCUMENTATION PENDING
   */
  public void setEvaluationDistribution_toReviewGroup(
    String evaluationDistribution_toReviewGroup)
  {
    if(evaluationDistribution_toReviewGroup != null)
    {
      this.evaluationDistribution_toReviewGroup =
        evaluationDistribution_toReviewGroup;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getEvaluationDistribution_toTestee()
  {
    return this.evaluationDistribution_toTestee;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param evaluationDistribution_toTestee DOCUMENTATION PENDING
   */
  public void setEvaluationDistribution_toTestee(
    String evaluationDistribution_toTestee)
  {
    if(evaluationDistribution_toTestee != null)
    {
      this.evaluationDistribution_toTestee = evaluationDistribution_toTestee;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getEvaluationDistribution_isInstructorViewable()
  {
    return this.evaluationDistribution_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param evaluationDistribution_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setEvaluationDistribution_isInstructorViewable(
    boolean evaluationDistribution_isInstructorViewable)
  {
    this.evaluationDistribution_isInstructorViewable =
      evaluationDistribution_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getEvaluationDistribution_isInstructorEditable()
  {
    return this.evaluationDistribution_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param evaluationDistribution_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setEvaluationDistribution_isInstructorEditable(
    boolean evaluationDistribution_isInstructorEditable)
  {
    this.evaluationDistribution_isInstructorEditable =
      evaluationDistribution_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getEvaluationDistribution_isStudentViewable()
  {
    return this.evaluationDistribution_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param evaluationDistribution_isStudentViewable DOCUMENTATION PENDING
   */
  public void setEvaluationDistribution_isStudentViewable(
    boolean evaluationDistribution_isStudentViewable)
  {
    this.evaluationDistribution_isStudentViewable =
      evaluationDistribution_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTesteeIdentity()
  {
    return this.testeeIdentity;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param testeeIdentity DOCUMENTATION PENDING
   */
  public void setTesteeIdentity(String testeeIdentity)
  {
    if(testeeIdentity != null)
    {
      this.testeeIdentity = testeeIdentity;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getTesteeIdentity_isInstructorViewable()
  {
    return this.testeeIdentity_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param testeeIdentity_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setTesteeIdentity_isInstructorViewable(
    boolean testeeIdentity_isInstructorViewable)
  {
    this.testeeIdentity_isInstructorViewable =
      testeeIdentity_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getTesteeIdentity_isInstructorEditable()
  {
    return this.testeeIdentity_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param testeeIdentity_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setTesteeIdentity_isInstructorEditable(
    boolean testeeIdentity_isInstructorEditable)
  {
    this.testeeIdentity_isInstructorEditable =
      testeeIdentity_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getTesteeIdentity_isStudentViewable()
  {
    return this.testeeIdentity_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param testeeIdentity_isStudentViewable DOCUMENTATION PENDING
   */
  public void setTesteeIdentity_isStudentViewable(
    boolean testeeIdentity_isStudentViewable)
  {
    this.testeeIdentity_isStudentViewable = testeeIdentity_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getEvaluationComponents_scores()
  {
    return this.evaluationComponents_scores;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param evaluationComponents_scores DOCUMENTATION PENDING
   */
  public void setEvaluationComponents_scores(
    String evaluationComponents_scores)
  {
    if(evaluationComponents_scores != null)
    {
      this.evaluationComponents_scores = evaluationComponents_scores;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getEvaluationComponents_commentsForQuestions()
  {
    return this.evaluationComponents_commentsForQuestions;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param evaluationComponents_commentsForQuestions DOCUMENTATION PENDING
   */
  public void setEvaluationComponents_commentsForQuestions(
    String evaluationComponents_commentsForQuestions)
  {
    if(evaluationComponents_commentsForQuestions != null)
    {
      this.evaluationComponents_commentsForQuestions =
        evaluationComponents_commentsForQuestions;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getEvaluationComponents_commentsForParts()
  {
    return this.evaluationComponents_commentsForParts;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param evaluationComponents_commentsForParts DOCUMENTATION PENDING
   */
  public void setEvaluationComponents_commentsForParts(
    String evaluationComponents_commentsForParts)
  {
    if(evaluationComponents_commentsForParts != null)
    {
      this.evaluationComponents_commentsForParts =
        evaluationComponents_commentsForParts;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getEvaluationComponents_commentForAssess()
  {
    return this.evaluationComponents_commentForAssess;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param evaluationComponents_commentForAssess DOCUMENTATION PENDING
   */
  public void setEvaluationComponents_commentForAssess(
    String evaluationComponents_commentForAssess)
  {
    if(evaluationComponents_commentForAssess != null)
    {
      this.evaluationComponents_commentForAssess =
        evaluationComponents_commentForAssess;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getEvaluationComponents_isInstructorViewable()
  {
    return this.evaluationComponents_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param evaluationComponents_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setEvaluationComponents_isInstructorViewable(
    boolean evaluationComponents_isInstructorViewable)
  {
    this.evaluationComponents_isInstructorViewable =
      evaluationComponents_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getEvaluationComponents_isInstructorEditable()
  {
    return this.evaluationComponents_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param evaluationComponents_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setEvaluationComponents_isInstructorEditable(
    boolean evaluationComponents_isInstructorEditable)
  {
    this.evaluationComponents_isInstructorEditable =
      evaluationComponents_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getEvaluationComponents_isStudentViewable()
  {
    return this.evaluationComponents_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param evaluationComponents_isStudentViewable DOCUMENTATION PENDING
   */
  public void setEvaluationComponents_isStudentViewable(
    boolean evaluationComponents_isStudentViewable)
  {
    this.evaluationComponents_isStudentViewable =
      evaluationComponents_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getAutoScoring()
  {
    return this.autoScoring;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param autoScoring DOCUMENTATION PENDING
   */
  public void setAutoScoring(boolean autoScoring)
  {
    this.autoScoring = autoScoring;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getAutoScoring_isInstructorViewable()
  {
    return this.autoScoring_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param autoScoring_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setAutoScoring_isInstructorViewable(
    boolean autoScoring_isInstructorViewable)
  {
    this.autoScoring_isInstructorViewable = autoScoring_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getAutoScoring_isInstructorEditable()
  {
    return this.autoScoring_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param autoScoring_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setAutoScoring_isInstructorEditable(
    boolean autoScoring_isInstructorEditable)
  {
    this.autoScoring_isInstructorEditable = autoScoring_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getAutoScoring_isStudentViewable()
  {
    return this.autoScoring_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param autoScoring_isStudentViewable DOCUMENTATION PENDING
   */
  public void setAutoScoring_isStudentViewable(
    boolean autoScoring_isStudentViewable)
  {
    this.autoScoring_isStudentViewable = autoScoring_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getScoringType()
  {
    return this.scoringType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param scoringType DOCUMENTATION PENDING
   */
  public void setScoringType(String scoringType)
  {
    if(scoringType != null)
    {
      this.scoringType = scoringType;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getScoringType_isInstructorViewable()
  {
    return this.scoringType_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param scoringType_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setScoringType_isInstructorViewable(
    boolean scoringType_isInstructorViewable)
  {
    this.scoringType_isInstructorViewable = scoringType_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getScoringType_isInstructorEditable()
  {
    return this.scoringType_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param scoringType_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setScoringType_isInstructorEditable(
    boolean scoringType_isInstructorEditable)
  {
    this.scoringType_isInstructorEditable = scoringType_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getScoringType_isStudentViewable()
  {
    return this.scoringType_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param scoringType_isStudentViewable DOCUMENTATION PENDING
   */
  public void setScoringType_isStudentViewable(
    boolean scoringType_isStudentViewable)
  {
    this.scoringType_isStudentViewable = scoringType_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getNumericModel()
  {
    return this.numericModel;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param numericModel DOCUMENTATION PENDING
   */
  public void setNumericModel(String numericModel)
  {
    if(numericModel != null)
    {
      this.numericModel = numericModel;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getNumericModel_isInstructorViewable()
  {
    return this.numericModel_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param numericModel_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setNumericModel_isInstructorViewable(
    boolean numericModel_isInstructorViewable)
  {
    this.numericModel_isInstructorViewable = numericModel_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getNumericModel_isInstructorEditable()
  {
    return this.numericModel_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param numericModel_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setNumericModel_isInstructorEditable(
    boolean numericModel_isInstructorEditable)
  {
    this.numericModel_isInstructorEditable = numericModel_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getNumericModel_isStudentViewable()
  {
    return this.numericModel_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param numericModel_isStudentViewable DOCUMENTATION PENDING
   */
  public void setNumericModel_isStudentViewable(
    boolean numericModel_isStudentViewable)
  {
    this.numericModel_isStudentViewable = numericModel_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getFixedTotalScore()
  {
    return this.fixedTotalScore;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param fixedTotalScore DOCUMENTATION PENDING
   */
  public void setFixedTotalScore(String fixedTotalScore)
  {
    if(fixedTotalScore != null)
    {
      this.fixedTotalScore = fixedTotalScore;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getDefaultQuestionValue()
  {
    return this.defaultQuestionValue;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param defaultQuestionValue DOCUMENTATION PENDING
   */
  public void setDefaultQuestionValue(String defaultQuestionValue)
  {
    if(defaultQuestionValue != null)
    {
      this.defaultQuestionValue = defaultQuestionValue;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getDefaultQuestionValue_isInstructorEditable()
  {
    return defaultQuestionValue_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param value DOCUMENTATION PENDING
   */
  public void setDefaultQuestionValue_isInstructorEditable(boolean value)
  {
    defaultQuestionValue_isInstructorEditable = value;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getDefaultQuestionValue_isInstructorViewable()
  {
    return defaultQuestionValue_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param value DOCUMENTATION PENDING
   */
  public void setDefaultQuestionValue_isInstructorViewable(boolean value)
  {
    defaultQuestionValue_isInstructorViewable = value;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getDefaultQuestionValue_isStudentViewable()
  {
    return defaultQuestionValue_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param value DOCUMENTATION PENDING
   */
  public void setDefaultQuestionValue_isStudentViewable(boolean value)
  {
    defaultQuestionValue_isStudentViewable = value;
  }

  /**
   * DOCUMENTATION PENDING
   */
  private void resetFields()
  {
    LOG.debug("u r in evaluation form");
    this.evaluationDistribution_toInstructor = "on";
    this.evaluationDistribution_toTAs = "off";
    this.evaluationDistribution_toSectionGrader = "off";
    this.evaluationDistribution_toReviewGroup = "off";
    this.evaluationDistribution_toTestee = "off";
    this.evaluationDistribution_isInstructorViewable = true;
    this.evaluationDistribution_isInstructorEditable = true;
    this.evaluationDistribution_isStudentViewable = false;
    this.testeeIdentity = "true";
    this.testeeIdentity_isInstructorViewable = true;
    this.testeeIdentity_isInstructorEditable = true;
    this.testeeIdentity_isStudentViewable = false;
    this.evaluationComponents_scores = "on";
    this.evaluationComponents_commentsForQuestions = "off";
    this.evaluationComponents_commentsForParts = "off";
    this.evaluationComponents_commentForAssess = "on";
    this.evaluationComponents_isInstructorViewable = true;
    this.evaluationComponents_isInstructorEditable = true;
    this.evaluationComponents_isStudentViewable = false;
    this.autoScoring = true;
    this.autoScoring_isInstructorViewable = true;
    this.autoScoring_isInstructorEditable = true;
    this.autoScoring_isStudentViewable = false;
    this.scoringType = "0";
    this.scoringType_isInstructorViewable = true;
    this.scoringType_isInstructorEditable = true;
    this.scoringType_isStudentViewable = false;
    this.numericModel = "0";
    this.numericModel_isInstructorViewable = true;
    this.numericModel_isInstructorEditable = true;
    this.numericModel_isStudentViewable = false;
    this.fixedTotalScore = "";
    this.defaultQuestionValue = "";
    this.defaultQuestionValue_isInstructorViewable = true;
    this.defaultQuestionValue_isInstructorViewable = true;
    this.defaultQuestionValue_isInstructorViewable = false;
  }
}
