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
 * PartForm: form class for part.jsp
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
 * @author Qingru Zhang
 * @version $Id: PartForm.java,v 1.1.1.1 2004/07/28 21:32:08 rgollub.stanford.edu Exp $
 */
public class PartForm
  extends ActionForm
  implements MediaInterface
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(PartForm.class);
    
  private String name;
  private boolean name_isInstructorViewable;
  private boolean name_isInstructorEditable;
  private boolean name_isStudentViewable;
  private String type;
  private Collection partTypes;
  private Collection responseTypes;
  private boolean type_isInstructorViewable;
  private boolean type_isInstructorEditable;
  private boolean type_isStudentViewable;
  private String description;
  private boolean description_isInstructorViewable;
  private boolean description_isInstructorEditable;
  private boolean description_isStudentViewable;
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
  private String questionOrder;
  private boolean questionOrder_isInstructorViewable;
  private boolean questionOrder_isInstructorEditable;
  private boolean questionOrder_isStudentViewable;
  Collection partList;
  Collection original_partList;

  /**
   * Creates a new PartForm object.
   */
  public PartForm()
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

    // Exit if we're not actually editing a part.
    if((actionMapping == null) ||
        ! actionMapping.getPath().equals("/editPart"))
    {
      return;
    }

    if(session.getAttribute("editorRole").equals("templateEditor"))
    {
      this.name_isInstructorViewable = false;
      this.name_isInstructorEditable = false;
      this.type_isInstructorViewable = false;
      this.type_isInstructorEditable = false;
      this.description_isInstructorViewable = false;
      this.description_isInstructorEditable = false;
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
      this.questionOrder_isInstructorViewable = false;
      this.questionOrder_isInstructorEditable = false;
      this.name_isStudentViewable = false;
      this.type_isStudentViewable = false;
      this.description_isStudentViewable = false;
      this.mediaCollection_isStudentViewable = false;
      this.relatedMediaCollection_isStudentViewable = false;
      this.keywords_isStudentViewable = false;
      this.objectives_isStudentViewable = false;
      this.rubrics_isStudentViewable = false;
      this.questionOrder_isStudentViewable = false;
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
      return 9;
    }
    else
    {
      int i = 0;
      if(name_isInstructorEditable || name_isInstructorViewable)
      {
        i++;
      }

      if(type_isInstructorEditable || type_isInstructorViewable)
      {
        i++;
      }

      if(description_isInstructorEditable || description_isInstructorViewable)
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

      if(objectives_isInstructorEditable || objectives_isInstructorViewable)
      {
        i++;
      }

      if(keywords_isInstructorEditable || keywords_isInstructorViewable)
      {
        i++;
      }

      if(rubrics_isInstructorEditable || rubrics_isInstructorViewable)
      {
        i++;
      }

      if(
        questionOrder_isInstructorEditable ||
          questionOrder_isInstructorViewable)
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
  public Collection getPartTypes()
  {
    return partTypes;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param types DOCUMENTATION PENDING
   */
  public void setPartTypes(Collection types)
  {
    if(types != null)
    {
      partTypes = types;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getResponseTypes()
  {
    return responseTypes;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param ptypes DOCUMENTATION PENDING
   */
  public void setResponseTypes(Collection ptypes)
  {
    responseTypes = ptypes;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getType()
  {
    return this.type;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param type DOCUMENTATION PENDING
   */
  public void setType(String type)
  {
    if(type != null)
    {
      this.type = type;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getType_isInstructorViewable()
  {
    return this.type_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param type_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setType_isInstructorViewable(boolean type_isInstructorViewable)
  {
    this.type_isInstructorViewable = type_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getType_isInstructorEditable()
  {
    return this.type_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param type_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setType_isInstructorEditable(boolean type_isInstructorEditable)
  {
    this.type_isInstructorEditable = type_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getType_isStudentViewable()
  {
    return this.type_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param type_isStudentViewable DOCUMENTATION PENDING
   */
  public void setType_isStudentViewable(boolean type_isStudentViewable)
  {
    this.type_isStudentViewable = type_isStudentViewable;
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
   *
   * @return DOCUMENTATION PENDING
   */
  public String getQuestionOrder()
  {
    return this.questionOrder;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param questionOrder DOCUMENTATION PENDING
   */
  public void setQuestionOrder(String questionOrder)
  {
    if(questionOrder != null)
    {
      this.questionOrder = questionOrder;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getQuestionOrder_isInstructorViewable()
  {
    return this.questionOrder_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param questionOrder_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setQuestionOrder_isInstructorViewable(
    boolean questionOrder_isInstructorViewable)
  {
    this.questionOrder_isInstructorViewable =
      questionOrder_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getQuestionOrder_isInstructorEditable()
  {
    return this.questionOrder_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param questionOrder_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setQuestionOrder_isInstructorEditable(
    boolean questionOrder_isInstructorEditable)
  {
    this.questionOrder_isInstructorEditable =
      questionOrder_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getQuestionOrder_isStudentViewable()
  {
    return this.questionOrder_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param questionOrder_isStudentViewable DOCUMENTATION PENDING
   */
  public void setQuestionOrder_isStudentViewable(
    boolean questionOrder_isStudentViewable)
  {
    this.questionOrder_isStudentViewable = questionOrder_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getPartList()
  {
    LOG.error("Form: partList size = " + partList.size());

    return partList;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param parts DOCUMENTATION PENDING
   */
  public void setPartList(Collection parts)
  {
    if(parts != null)
    {
      partList = parts;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getOriginal_partList()
  {
    return original_partList;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param parts DOCUMENTATION PENDING
   */
  public void setOriginal_partList(Collection parts)
  {
    if(parts != null)
    {
      original_partList = parts;
    }
  }

  /**
   * DOCUMENTATION PENDING
   */
  private void resetFields()
  {
    if(description == null)
    {
      LOG.error("u r in part field");
      this.name = "";
      this.name_isInstructorViewable = true;
      this.name_isInstructorEditable = true;
      this.name_isStudentViewable = true;
      this.type = null;
      this.type_isInstructorViewable = true;
      this.type_isInstructorEditable = true;
      this.type_isStudentViewable = true;
      this.description = "";
      this.description_isInstructorViewable = true;
      this.description_isInstructorEditable = true;
      this.description_isStudentViewable = true;
      partList = new ArrayList();
      original_partList = new ArrayList();
      this.mediaCollection = new ArrayList();

      // dummy values, useful for debugging
      //        MediaData dummy = new MediaData();
      //        dummy.setLocation("http://www.hungry.com/");
      //        dummy.setLink(false);
      //        dummy.setAuthor("Rachel Gollub");
      //        dummy.setType("link");
      //        dummy.setTypeId(2);
      //        dummy.setName("Hungry link.");
      //        dummy.setDescription("Link to the hungry home page.");
      //        dummy.setDateAdded(new Date());
      //        mediaCollection.add(dummy);
      this.mediaCollection_isInstructorViewable = true;
      this.mediaCollection_isInstructorEditable = true;
      this.mediaCollection_isStudentViewable = true;
      relatedMediaCollection = new ArrayList();

      // dummy values, useful for debugging
      //        dummy = new MediaData();
      //        dummy.setLocation("http://kevinandkell.com/");
      //        dummy.setLink(true);
      //        dummy.setAuthor("Bill Hollbrook");
      //        dummy.setType("link");
      //        dummy.setTypeId(2);
      //        dummy.setName("Kevin and Kell");
      //        dummy.setDescription("A cool comic");
      //        dummy.setDateAdded(new Date());
      //        relatedMediaCollection.add(dummy);
      //        this.relatedMediaCollection.add(dummy);
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

      this.questionOrder = "0";
      this.questionOrder_isInstructorViewable = true;
      this.questionOrder_isInstructorEditable = true;
      this.questionOrder_isStudentViewable = false;

      partTypes = new ArrayList();
      responseTypes = new ArrayList();

      try
      {
        AssessmentServiceDelegate delegate = new AssessmentServiceDelegate();
        TypeIterator ti = delegate.getAssessmentTypes();
        while(ti.hasNext())
        {
          partTypes.add(ti.next());
        }

        ti = delegate.getResponseTypes();
        while(ti.hasNext())
        {
          responseTypes.add(ti.next());
        }
      }
      catch(Exception e)
      {
        LOG.error(e); throw new Error(e);
      }
    }
  }
}
