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

import org.navigoproject.osid.assessment.AssessmentServiceDelegate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import osid.shared.TypeIterator;

/**
 * <p>
 * Title: NavigoProject.org
 * </p>
 *
 * <p>
 * Description: AAM - form class for assessDescEdit.jsp
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
 * @version 1.0
 */
public class DescriptionForm
  extends ActionForm
  implements MediaInterface
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(DescriptionForm.class);
    
  private String name;
  private boolean name_isInstructorViewable;
  private boolean name_isInstructorEditable;
  private boolean name_isStudentViewable;
  private Collection assessmentTypes;
  private String assessmentType;
  private boolean assessmentType_isInstructorViewable;
  private boolean assessmentType_isInstructorEditable;
  private boolean assessmentType_isStudentViewable;
  private String description;
  private boolean description_isInstructorViewable;
  private boolean description_isInstructorEditable;
  private boolean description_isStudentViewable;
  private String templateName;
  private boolean templateName_isInstructorViewable;
  private boolean templateName_isInstructorEditable;
  private boolean templateName_isStudentViewable;
  private String templateDescription;
  private boolean templateDescription_isInstructorViewable;
  private boolean templateDescription_isInstructorEditable;
  private boolean templateDescription_isStudentViewable;
  private Collection mediaCollection;
  private boolean mediaCollection_isInstructorViewable;
  private boolean mediaCollection_isInstructorEditable;
  private boolean mediaCollection_isStudentViewable;
  private Collection relatedMediaCollection;
  private boolean relatedMediaCollection_isInstructorViewable;
  private boolean relatedMediaCollection_isInstructorEditable;
  private boolean relatedMediaCollection_isStudentViewable;
  private String keywords;
  private boolean keywords_isInstructorViewable;
  private boolean keywords_isInstructorEditable;
  private boolean keywords_isStudentViewable;
  private String objectives;
  private boolean objectives_isInstructorViewable;
  private boolean objectives_isInstructorEditable;
  private boolean objectives_isStudentViewable;
  private String rubrics;
  private boolean rubrics_isInstructorViewable;
  private boolean rubrics_isInstructorEditable;
  private boolean rubrics_isStudentViewable;

  /**
   * Creates a new DescriptionForm object.
   */
  public DescriptionForm()
  {
    super();
    resetFields();
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

    if((this.name == null) || (this.name.length() == 0))
    {
      ;
    }

    if((this.description == null) || (this.description.length() == 0))
    {
      ;
    }

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
    HttpSession session = httpServletRequest.getSession(true);

    // No checkboxes in name/description page.
    if(
      session.getAttribute("editorState").equals("newTemplate") ||
        session.getAttribute("editorState").equals("newAssessment"))
    {
      return;
    }

    if(session.getAttribute("editorRole").equals("templateEditor"))
    { // This is so we don't remove the correct values from the session
      LOG.debug(
        "setting the defaults in the session i guess first time");

      // Don't set name -- the checkbox isn't there any more.
      //this.name_isInstructorViewable = false;
      //this.name_isInstructorEditable = false;
      this.assessmentType_isInstructorViewable = false;
      this.assessmentType_isInstructorEditable = false;

      // Don't set description -- the checkbox isn't there any more.
      //this.description_isInstructorViewable = false;
      //this.description_isInstructorEditable = false;
      this.templateName_isInstructorViewable = false;
      this.templateName_isInstructorEditable = false;
      this.templateDescription_isInstructorViewable = false;
      this.templateDescription_isInstructorEditable = false;
      this.mediaCollection_isInstructorViewable = false;
      this.mediaCollection_isInstructorEditable = false;
      this.relatedMediaCollection_isInstructorViewable = false;
      this.relatedMediaCollection_isInstructorEditable = false;
      this.keywords_isInstructorViewable = false;
      this.keywords_isInstructorEditable = false;
      this.objectives_isInstructorViewable = false;
      this.objectives_isInstructorEditable = false;
      this.rubrics_isInstructorViewable = false;
      this.rubrics_isInstructorEditable = false;

      //this.name_isStudentViewable = false;
      this.rubrics_isStudentViewable = false;
      this.assessmentType_isStudentViewable = false;

      //this.description_isStudentViewable = false;
      this.templateName_isStudentViewable = false;
      this.templateDescription_isStudentViewable = false;
      this.mediaCollection_isStudentViewable = false;
      this.relatedMediaCollection_isStudentViewable = false;
      this.keywords_isStudentViewable = false;
      this.objectives_isStudentViewable = false;
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
      return 10;
    }
    else
    {
      int i = 0;
      if(name_isInstructorEditable || name_isInstructorViewable)
      {
        i++;
      }

      if(
        assessmentType_isInstructorEditable ||
          assessmentType_isInstructorViewable)
      {
        i++;
      }

      if(description_isInstructorEditable || description_isInstructorViewable)
      {
        i++;
      }

      if(templateName_isInstructorEditable ||
          templateName_isInstructorViewable)
      {
        i++;
      }

      if(
        templateDescription_isInstructorEditable ||
          templateDescription_isInstructorViewable)
      {
        i++;
      }

      if(
        mediaCollection_isInstructorEditable ||
          mediaCollection_isInstructorViewable)
      {
        i++;
      }

      if(
        relatedMediaCollection_isInstructorEditable ||
          relatedMediaCollection_isInstructorViewable)
      {
        i++;
      }

      if(keywords_isInstructorEditable || keywords_isInstructorViewable)
      {
        i++;
      }

      if(objectives_isInstructorEditable || objectives_isInstructorViewable)
      {
        i++;
      }

      if(rubrics_isInstructorEditable || rubrics_isInstructorViewable)
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
  public String getName()
  {
    return this.name;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param name DOCUMENTATION PENDING
   */
  public void setName(String name)
  {
    if(name != null)
    {
      this.name = name;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getName_isInstructorViewable()
  {
    return this.name_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param name_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setName_isInstructorViewable(boolean name_isInstructorViewable)
  {
    this.name_isInstructorViewable = name_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getName_isInstructorEditable()
  {
    return this.name_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param name_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setName_isInstructorEditable(boolean name_isInstructorEditable)
  {
    this.name_isInstructorEditable = name_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getName_isStudentViewable()
  {
    return this.name_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param name_isStudentViewable DOCUMENTATION PENDING
   */
  public void setName_isStudentViewable(boolean name_isStudentViewable)
  {
    this.name_isStudentViewable = name_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getAssessmentTypes()
  {
    return assessmentTypes;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param types DOCUMENTATION PENDING
   */
  public void setAssessmentTypes(Collection types)
  {
    if(types != null)
    {
      assessmentTypes = types;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getAssessmentType()
  {
    return this.assessmentType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentType DOCUMENTATION PENDING
   */
  public void setAssessmentType(String assessmentType)
  {
    if(assessmentType != null)
    {
      this.assessmentType = assessmentType;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getAssessmentType_isInstructorViewable()
  {
    return this.assessmentType_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentType_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setAssessmentType_isInstructorViewable(
    boolean assessmentType_isInstructorViewable)
  {
    this.assessmentType_isInstructorViewable =
      assessmentType_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getAssessmentType_isInstructorEditable()
  {
    return this.assessmentType_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentType_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setAssessmentType_isInstructorEditable(
    boolean assessmentType_isInstructorEditable)
  {
    this.assessmentType_isInstructorEditable =
      assessmentType_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getAssessmentType_isStudentViewable()
  {
    return this.assessmentType_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentType_isStudentViewable DOCUMENTATION PENDING
   */
  public void setAssessmentType_isStudentViewable(
    boolean assessmentType_isStudentViewable)
  {
    this.assessmentType_isStudentViewable = assessmentType_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getDescription()
  {
    return this.description;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param description DOCUMENTATION PENDING
   */
  public void setDescription(String description)
  {
    if(description != null)
    {
      this.description = description;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getDescription_isInstructorViewable()
  {
    return this.description_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param description_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setDescription_isInstructorViewable(
    boolean description_isInstructorViewable)
  {
    this.description_isInstructorViewable = description_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getDescription_isInstructorEditable()
  {
    return this.description_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param description_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setDescription_isInstructorEditable(
    boolean description_isInstructorEditable)
  {
    this.description_isInstructorEditable = description_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getDescription_isStudentViewable()
  {
    return this.description_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param description_isStudentViewable DOCUMENTATION PENDING
   */
  public void setDescription_isStudentViewable(
    boolean description_isStudentViewable)
  {
    this.description_isStudentViewable = description_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTemplateName()
  {
    return this.templateName;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param templateName DOCUMENTATION PENDING
   */
  public void setTemplateName(String templateName)
  {
    if(templateName != null)
    {
      this.templateName = templateName;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getTemplateName_isInstructorViewable()
  {
    return this.templateName_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param templateName_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setTemplateName_isInstructorViewable(
    boolean templateName_isInstructorViewable)
  {
    this.templateName_isInstructorViewable = templateName_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getTemplateName_isInstructorEditable()
  {
    return this.templateName_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param templateName_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setTemplateName_isInstructorEditable(
    boolean templateName_isInstructorEditable)
  {
    this.templateName_isInstructorEditable = templateName_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getTemplateName_isStudentViewable()
  {
    return this.templateName_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param templateName_isStudentViewable DOCUMENTATION PENDING
   */
  public void setTemplateName_isStudentViewable(
    boolean templateName_isStudentViewable)
  {
    this.templateName_isStudentViewable = templateName_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTemplateDescription()
  {
    return this.templateDescription;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param templateDescription DOCUMENTATION PENDING
   */
  public void setTemplateDescription(String templateDescription)
  {
    if(templateDescription != null)
    {
      this.templateDescription = templateDescription;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getTemplateDescription_isInstructorViewable()
  {
    return this.templateDescription_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param templateDescription_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setTemplateDescription_isInstructorViewable(
    boolean templateDescription_isInstructorViewable)
  {
    this.templateDescription_isInstructorViewable =
      templateDescription_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getTemplateDescription_isInstructorEditable()
  {
    return this.templateDescription_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param templateDescription_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setTemplateDescription_isInstructorEditable(
    boolean templateDescription_isInstructorEditable)
  {
    this.templateDescription_isInstructorEditable =
      templateDescription_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getTemplateDescription_isStudentViewable()
  {
    return this.templateDescription_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param templateDescription_isStudentViewable DOCUMENTATION PENDING
   */
  public void setTemplateDescription_isStudentViewable(
    boolean templateDescription_isStudentViewable)
  {
    this.templateDescription_isStudentViewable =
      templateDescription_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getMediaCollection()
  {
    return this.mediaCollection;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public List getReversedMediaCollection()
  {
    List l = new ArrayList(this.mediaCollection); //Qingru: Add reverse function here for bug #189 
    Collections.reverse(l);

    return l;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param mediaCollection DOCUMENTATION PENDING
   */
  public void setMediaCollection(Collection mediaCollection)
  {
    if(mediaCollection != null)
    {
      this.mediaCollection = mediaCollection;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getMediaCollection_isInstructorViewable()
  {
    return this.mediaCollection_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param mediaCollection_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setMediaCollection_isInstructorViewable(
    boolean mediaCollection_isInstructorViewable)
  {
    this.mediaCollection_isInstructorViewable =
      mediaCollection_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getMediaCollection_isInstructorEditable()
  {
    return this.mediaCollection_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param mediaCollection_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setMediaCollection_isInstructorEditable(
    boolean mediaCollection_isInstructorEditable)
  {
    this.mediaCollection_isInstructorEditable =
      mediaCollection_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getMediaCollection_isStudentViewable()
  {
    return this.mediaCollection_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param mediaCollection_isStudentViewable DOCUMENTATION PENDING
   */
  public void setMediaCollection_isStudentViewable(
    boolean mediaCollection_isStudentViewable)
  {
    this.mediaCollection_isStudentViewable = mediaCollection_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getRelatedMediaCollection()
  {
    return this.relatedMediaCollection;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public List getReversedRelatedMediaCollection()
  {
    List l = new ArrayList(this.relatedMediaCollection); //Qingru: Add reverse function here for bug #189 
    Collections.reverse(l);

    return l;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param relatedMediaCollection DOCUMENTATION PENDING
   */
  public void setRelatedMediaCollection(Collection relatedMediaCollection)
  {
    if(relatedMediaCollection != null)
    {
      this.relatedMediaCollection = relatedMediaCollection;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getRelatedMediaCollection_isInstructorViewable()
  {
    return this.relatedMediaCollection_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param relatedMediaCollection_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setRelatedMediaCollection_isInstructorViewable(
    boolean relatedMediaCollection_isInstructorViewable)
  {
    this.relatedMediaCollection_isInstructorViewable =
      relatedMediaCollection_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getRelatedMediaCollection_isInstructorEditable()
  {
    return this.relatedMediaCollection_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param relatedMediaCollection_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setRelatedMediaCollection_isInstructorEditable(
    boolean relatedMediaCollection_isInstructorEditable)
  {
    this.relatedMediaCollection_isInstructorEditable =
      relatedMediaCollection_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getRelatedMediaCollection_isStudentViewable()
  {
    return this.relatedMediaCollection_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param relatedMediaCollection_isStudentViewable DOCUMENTATION PENDING
   */
  public void setRelatedMediaCollection_isStudentViewable(
    boolean relatedMediaCollection_isStudentViewable)
  {
    this.relatedMediaCollection_isStudentViewable =
      relatedMediaCollection_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getKeywords()
  {
    return this.keywords;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param keywords DOCUMENTATION PENDING
   */
  public void setKeywords(String keywords)
  {
    if(keywords != null)
    {
      this.keywords = keywords;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getKeywords_isInstructorViewable()
  {
    return this.keywords_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param keywords_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setKeywords_isInstructorViewable(
    boolean keywords_isInstructorViewable)
  {
    this.keywords_isInstructorViewable = keywords_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getKeywords_isInstructorEditable()
  {
    return this.keywords_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param keywords_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setKeywords_isInstructorEditable(
    boolean keywords_isInstructorEditable)
  {
    this.keywords_isInstructorEditable = keywords_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getKeywords_isStudentViewable()
  {
    return this.keywords_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param keywords_isStudentViewable DOCUMENTATION PENDING
   */
  public void setKeywords_isStudentViewable(boolean keywords_isStudentViewable)
  {
    this.keywords_isStudentViewable = keywords_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getObjectives()
  {
    return this.objectives;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param objectives DOCUMENTATION PENDING
   */
  public void setObjectives(String objectives)
  {
    if(objectives != null)
    {
      this.objectives = objectives;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getObjectives_isInstructorViewable()
  {
    return this.objectives_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param objectives_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setObjectives_isInstructorViewable(
    boolean objectives_isInstructorViewable)
  {
    this.objectives_isInstructorViewable = objectives_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getObjectives_isInstructorEditable()
  {
    return this.objectives_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param objectives_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setObjectives_isInstructorEditable(
    boolean objectives_isInstructorEditable)
  {
    this.objectives_isInstructorEditable = objectives_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getObjectives_isStudentViewable()
  {
    return this.objectives_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param objectives_isStudentViewable DOCUMENTATION PENDING
   */
  public void setObjectives_isStudentViewable(
    boolean objectives_isStudentViewable)
  {
    this.objectives_isStudentViewable = objectives_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getRubrics()
  {
    return this.rubrics;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param rubrics DOCUMENTATION PENDING
   */
  public void setRubrics(String rubrics)
  {
    if(rubrics != null)
    {
      this.rubrics = rubrics;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getRubrics_isInstructorViewable()
  {
    return this.rubrics_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param rubrics_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setRubrics_isInstructorViewable(
    boolean rubrics_isInstructorViewable)
  {
    this.rubrics_isInstructorViewable = rubrics_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getRubrics_isInstructorEditable()
  {
    return this.rubrics_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param rubrics_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setRubrics_isInstructorEditable(
    boolean rubrics_isInstructorEditable)
  {
    this.rubrics_isInstructorEditable = rubrics_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getRubrics_isStudentViewable()
  {
    return this.rubrics_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param rubrics_isStudentViewable DOCUMENTATION PENDING
   */
  public void setRubrics_isStudentViewable(boolean rubrics_isStudentViewable)
  {
    this.rubrics_isStudentViewable = rubrics_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   */
  private void resetFields()
  {
    if(templateName == null)
    {
      LOG.debug("u should be called second to complete logic");
      this.name = "";
      this.name_isInstructorViewable = true;
      this.name_isInstructorEditable = true;
      this.name_isStudentViewable = true;
      this.assessmentType = null;
      this.assessmentType_isInstructorViewable = true;
      this.assessmentType_isInstructorEditable = true;
      this.assessmentType_isStudentViewable = true;
      this.description = "";
      this.description_isInstructorViewable = true;
      this.description_isInstructorEditable = true;
      this.description_isStudentViewable = false;
      this.templateName = "";
      this.templateName_isInstructorViewable = true;
      this.templateName_isInstructorEditable = false;
      this.templateName_isStudentViewable = false;
      this.templateDescription = "";
      this.templateDescription_isInstructorViewable = true;
      this.templateDescription_isInstructorEditable = false;
      this.templateDescription_isStudentViewable = false;
      this.mediaCollection = new ArrayList();
      this.mediaCollection_isInstructorViewable = true;
      this.mediaCollection_isInstructorEditable = true;
      this.mediaCollection_isStudentViewable = true;
      relatedMediaCollection = new ArrayList();
      this.relatedMediaCollection_isInstructorViewable = true;
      this.relatedMediaCollection_isInstructorEditable = true;
      this.relatedMediaCollection_isStudentViewable = true;
      this.keywords = "";
      this.keywords_isInstructorViewable = false;
      this.keywords_isInstructorEditable = false;
      this.keywords_isStudentViewable = false;
      this.objectives = "";
      this.objectives_isInstructorViewable = false;
      this.objectives_isInstructorEditable = false;
      this.objectives_isStudentViewable = false;
      this.rubrics = "";
      this.rubrics_isInstructorViewable = false;
      this.rubrics_isInstructorEditable = false;
      this.rubrics_isStudentViewable = false;
      assessmentTypes = new ArrayList();
      try
      {
        AssessmentServiceDelegate delegate = new AssessmentServiceDelegate();
        TypeIterator ti = delegate.getAssessmentTypes();
        while(ti.hasNext())
        {
          assessmentTypes.add(ti.next().getDescription());
        }
      }
      catch(Exception e)
      {
        LOG.error(e); throw new Error(e);
      }
    }
  }
}
