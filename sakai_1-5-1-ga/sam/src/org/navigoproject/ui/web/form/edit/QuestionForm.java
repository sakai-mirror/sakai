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

import org.navigoproject.business.entity.assessment.model.Answer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * <p>
 * Title: NavigoProject.org
 * </p>
 *
 * <p>
 * Description: AAM - form class for part.jsp
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
public class QuestionForm
  extends ActionForm
  implements MediaInterface
{
  private String name;
  private boolean name_isInstructorViewable;
  private boolean name_isInstructorEditable;
  private boolean name_isStudentViewable;
  private String type;
  private String a_type;
  private String hint;
  private boolean hint_isInstructorViewable;
  private boolean hint_isInstructorEditable;
  private boolean hint_isStudentViewable;
  private String value;
  private boolean value_isInstructorViewable;
  private boolean value_isInstructorEditable;
  private boolean value_isStudentViewable;
  private String objectives;
  private boolean objectives_isInstructorViewable;
  private boolean objectives_isInstructorEditable;
  private boolean objectives_isStudentViewable;
  private String keywords;
  private boolean keywords_isInstructorViewable;
  private boolean keywords_isInstructorEditable;
  private boolean keywords_isStudentViewable;
  private String rubrics;
  private boolean rubrics_isInstructorViewable;
  private boolean rubrics_isInstructorEditable;
  private boolean rubrics_isStudentViewable;
  private String feedback;
  private boolean feedback_isInstructorViewable;
  private boolean feedback_isInstructorEditable;
  private boolean feedback_isStudentViewable;
  private boolean afeedback_isInstructorViewable;
  private boolean afeedback_isInstructorEditable;
  private boolean afeedback_isStudentViewable;
  private boolean pagebreak;
  private boolean pagebreak_isInstructorViewable;
  private boolean pagebreak_isInstructorEditable;
  private boolean pagebreak_isStudentViewable;
  private boolean offerPagebreak;
  private String text;
  private boolean text_isInstructorViewable;
  private boolean text_isInstructorEditable;
  private boolean text_isStudentViewable;
  private Collection answers;
  private Collection mediaCollection;
  private Collection relatedMediaCollection;
  private int id;
  String itemType;
  private String forwardAction;
  private String number;
  private String pools;
  private boolean resetFields = true;

  /**
   * Creates a new QuestionForm object.
   */
  public QuestionForm()
  {
    super();
    resetFields();
    resetFields = false;
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

    if((name == null) || (name.length() == 0))
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
    if(session.getAttribute("editorRole").equals("templateEditor"))
    { // This is so we don't remove the correct values from the session
      name_isInstructorViewable = false;
      name_isInstructorEditable = false;
      name_isStudentViewable = false;
      text_isInstructorViewable = false;
      text_isInstructorEditable = false;
      text_isStudentViewable = false;
      hint_isInstructorViewable = false;
      hint_isInstructorEditable = false;
      hint_isStudentViewable = false;
      value_isInstructorViewable = false;
      value_isInstructorEditable = false;
      value_isStudentViewable = false;
      objectives_isInstructorViewable = false;
      objectives_isInstructorEditable = false;
      objectives_isStudentViewable = false;
      keywords_isInstructorViewable = false;
      keywords_isInstructorEditable = false;
      keywords_isStudentViewable = false;
      rubrics_isInstructorViewable = false;
      rubrics_isInstructorEditable = false;
      rubrics_isStudentViewable = false;
      feedback_isInstructorViewable = false;
      feedback_isInstructorEditable = false;
      feedback_isStudentViewable = false;
      afeedback_isInstructorViewable = false;
      afeedback_isInstructorEditable = false;
      afeedback_isStudentViewable = false;
      pagebreak = false;
      pagebreak_isInstructorViewable = false;
      pagebreak_isInstructorEditable = false;
      pagebreak_isStudentViewable = false;
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
      return 6;
    }
    else
    {
      int i = 0;
      if(name_isInstructorEditable || name_isInstructorViewable)
      {
        i++;
      }

      if(text_isInstructorEditable || text_isInstructorViewable)
      {
        i++;
      }

      if(hint_isInstructorEditable || hint_isInstructorViewable)
      {
        i++;
      }

      if(value_isInstructorEditable || value_isInstructorViewable)
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

      return i;
    }
  }

  /* get and set method for question name */
  public String getName()
  {
    return name;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param n DOCUMENTATION PENDING
   */
  public void setName(String n)
  {
    if(n != null)
    {
      name = n;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getName_isInstructorViewable()
  {
    return name_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param n_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setName_isInstructorViewable(boolean n_isInstructorViewable)
  {
    name_isInstructorViewable = n_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getName_isInstructorEditable()
  {
    return name_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param n_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setName_isInstructorEditable(boolean n_isInstructorEditable)
  {
    name_isInstructorEditable = n_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getName_isStudentViewable()
  {
    return name_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param n_isStudentViewable DOCUMENTATION PENDING
   */
  public void setName_isStudentViewable(boolean n_isStudentViewable)
  {
    name_isStudentViewable = n_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getText_isInstructorViewable()
  {
    return text_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param t_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setText_isInstructorViewable(boolean t_isInstructorViewable)
  {
    text_isInstructorViewable = t_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getText_isInstructorEditable()
  {
    return text_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param t_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setText_isInstructorEditable(boolean t_isInstructorEditable)
  {
    text_isInstructorEditable = t_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getText_isStudentViewable()
  {
    return text_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param t_isStudentViewable DOCUMENTATION PENDING
   */
  public void setText_isStudentViewable(boolean t_isStudentViewable)
  {
    text_isStudentViewable = t_isStudentViewable;
  }

  /* get and set method for answer text type */
  public String getA_type()
  {
    return a_type;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param t DOCUMENTATION PENDING
   */
  public void setA_type(String t)
  {
    if(t != null)
    {
      a_type = t;
    }
  }

  /* get and set method for question text type */
  public String getType()
  {
    return type;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param t DOCUMENTATION PENDING
   */
  public void setType(String t)
  {
    if(t != null)
    {
      type = t;
    }
  }

  /* get and set method for hint */
  public String getHint()
  {
    return hint;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param h DOCUMENTATION PENDING
   */
  public void setHint(String h)
  {
    if(h != null)
    {
      hint = h;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getHint_isInstructorViewable()
  {
    return hint_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param h_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setHint_isInstructorViewable(boolean h_isInstructorViewable)
  {
    hint_isInstructorViewable = h_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getHint_isInstructorEditable()
  {
    return hint_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param h_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setHint_isInstructorEditable(boolean h_isInstructorEditable)
  {
    hint_isInstructorEditable = h_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getHint_isStudentViewable()
  {
    return hint_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param h_isStudentViewable DOCUMENTATION PENDING
   */
  public void setHint_isStudentViewable(boolean h_isStudentViewable)
  {
    hint_isStudentViewable = h_isStudentViewable;
  }

  /* get and set method for value */
  public String getValue()
  {
    return value;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param val DOCUMENTATION PENDING
   */
  public void setValue(String val)
  {
    if(val != null)
    {
      value = val;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getValue_isInstructorViewable()
  {
    return value_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param val_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setValue_isInstructorViewable(boolean val_isInstructorViewable)
  {
    value_isInstructorViewable = val_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getValue_isInstructorEditable()
  {
    return value_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param val_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setValue_isInstructorEditable(boolean val_isInstructorEditable)
  {
    value_isInstructorEditable = val_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getValue_isStudentViewable()
  {
    return value_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param val_isStudentViewable DOCUMENTATION PENDING
   */
  public void setValue_isStudentViewable(boolean val_isStudentViewable)
  {
    value_isStudentViewable = val_isStudentViewable;
  }

  /* get and set method for objectives */
  public String getObjectives()
  {
    return objectives;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param obj DOCUMENTATION PENDING
   */
  public void setObjectives(String obj)
  {
    if(obj != null)
    {
      objectives = obj;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getObjectives_isInstructorViewable()
  {
    return objectives_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param obj_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setObjectives_isInstructorViewable(
    boolean obj_isInstructorViewable)
  {
    objectives_isInstructorViewable = obj_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getObjectives_isInstructorEditable()
  {
    return objectives_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param obj_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setObjectives_isInstructorEditable(
    boolean obj_isInstructorEditable)
  {
    objectives_isInstructorEditable = obj_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getObjectives_isStudentViewable()
  {
    return objectives_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param obj_isStudentViewable DOCUMENTATION PENDING
   */
  public void setObjectives_isStudentViewable(boolean obj_isStudentViewable)
  {
    objectives_isStudentViewable = obj_isStudentViewable;
  }

  /* get and set method for keywords */
  public String getKeywords()
  {
    return keywords;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param key DOCUMENTATION PENDING
   */
  public void setKeywords(String key)
  {
    if(key != null)
    {
      keywords = key;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getKeywords_isInstructorViewable()
  {
    return keywords_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param key_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setKeywords_isInstructorViewable(
    boolean key_isInstructorViewable)
  {
    keywords_isInstructorViewable = key_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getKeywords_isInstructorEditable()
  {
    return keywords_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param key_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setKeywords_isInstructorEditable(
    boolean key_isInstructorEditable)
  {
    keywords_isInstructorEditable = key_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getKeywords_isStudentViewable()
  {
    return keywords_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param key_isStudentViewable DOCUMENTATION PENDING
   */
  public void setKeywords_isStudentViewable(boolean key_isStudentViewable)
  {
    keywords_isStudentViewable = key_isStudentViewable;
  }

  /* get and set method for rubrics */
  public String getRubrics()
  {
    return rubrics;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param rub DOCUMENTATION PENDING
   */
  public void setRubrics(String rub)
  {
    if(rub != null)
    {
      rubrics = rub;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getRubrics_isInstructorViewable()
  {
    return rubrics_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param rub_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setRubrics_isInstructorViewable(boolean rub_isInstructorViewable)
  {
    rubrics_isInstructorViewable = rub_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getRubrics_isInstructorEditable()
  {
    return rubrics_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param rub_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setRubrics_isInstructorEditable(boolean rub_isInstructorEditable)
  {
    rubrics_isInstructorEditable = rub_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getRubrics_isStudentViewable()
  {
    return rubrics_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param rub_isStudentViewable DOCUMENTATION PENDING
   */
  public void setRubrics_isStudentViewable(boolean rub_isStudentViewable)
  {
    rubrics_isStudentViewable = rub_isStudentViewable;
  }

  /* get and set method for feedback */
  public String getFeedback()
  {
    return feedback;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param fb DOCUMENTATION PENDING
   */
  public void setFeedback(String fb)
  {
    if(fb != null)
    {
      feedback = fb;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getFeedback_isInstructorViewable()
  {
    return feedback_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param fb_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setFeedback_isInstructorViewable(boolean fb_isInstructorViewable)
  {
    feedback_isInstructorViewable = fb_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getFeedback_isInstructorEditable()
  {
    return feedback_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param fb_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setFeedback_isInstructorEditable(boolean fb_isInstructorEditable)
  {
    feedback_isInstructorEditable = fb_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getFeedback_isStudentViewable()
  {
    return feedback_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param afb_isStudentViewable DOCUMENTATION PENDING
   */
  public void setFeedback_isStudentViewable(boolean afb_isStudentViewable)
  {
    feedback_isStudentViewable = afb_isStudentViewable;
  }

  /* get and set methods for answer Feedback */
  public boolean getAfeedback_isInstructorViewable()
  {
    return afeedback_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param afb_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setAfeedback_isInstructorViewable(
    boolean afb_isInstructorViewable)
  {
    afeedback_isInstructorViewable = afb_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getAfeedback_isInstructorEditable()
  {
    return afeedback_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param afb_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setAfeedback_isInstructorEditable(
    boolean afb_isInstructorEditable)
  {
    afeedback_isInstructorEditable = afb_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getAfeedback_isStudentViewable()
  {
    return afeedback_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param afb_isStudentViewable DOCUMENTATION PENDING
   */
  public void setAfeedback_isStudentViewable(boolean afb_isStudentViewable)
  {
    afeedback_isStudentViewable = afb_isStudentViewable;
  }

  /* get and set method for pagebreak */
  public boolean getOfferPageBreak()
  {
    return offerPagebreak;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pb DOCUMENTATION PENDING
   */
  public void setOfferPageBreak(boolean pb)
  {
    offerPagebreak = pb;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getPageBreak()
  {
    return pagebreak;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pb DOCUMENTATION PENDING
   */
  public void setPageBreak(boolean pb)
  {
    pagebreak = pb;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getPageBreak_isInstructorViewable()
  {
    return pagebreak_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pb_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setPageBreak_isInstructorViewable(
    boolean pb_isInstructorViewable)
  {
    pagebreak_isInstructorViewable = pb_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getPageBreak_isInstructorEditable()
  {
    return pagebreak_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pb_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setPageBreak_isInstructorEditable(
    boolean pb_isInstructorEditable)
  {
    pagebreak_isInstructorEditable = pb_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getPageBreak_isStudentViewable()
  {
    return pagebreak_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pb_isStudentViewable DOCUMENTATION PENDING
   */
  public void setPageBreak_isStudentViewable(boolean pb_isStudentViewable)
  {
    pagebreak_isStudentViewable = pb_isStudentViewable;
  }

  /* get and set method for text */
  public String getText()
  {
    return text;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param txt DOCUMENTATION PENDING
   */
  public void setText(String txt)
  {
    if(txt != null)
    {
      text = txt;
    }
  }

  /* get and set method for answers */
  public Collection getAnswers()
  {
    return answers;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param ans DOCUMENTATION PENDING
   */
  public void setAnswers(Collection ans)
  {
    if(ans != null)
    {
      answers = ans;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Object[] getAnswerArray()
  {
    return answers.toArray();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param answerarray DOCUMENTATION PENDING
   */
  public void setAnswerArray(Answer[] answerarray)
  {
    // Ignore.
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getMediaCollection()
  {
    return mediaCollection;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public List getReversedMediaCollection()
  {
    List l = new ArrayList(this.mediaCollection);

    //Qingru: Add reverse function here for bug #189
    Collections.reverse(l);

    return l;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param media DOCUMENTATION PENDING
   */
  public void setMediaCollection(Collection media)
  {
    if(media != null)
    {
      mediaCollection = media;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getRelatedMediaCollection()
  {
    return relatedMediaCollection;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public List getReversedRelatedMediaCollection()
  {
    List l = new ArrayList(this.relatedMediaCollection);

    //Qingru: Add reverse function here for bug #189
    Collections.reverse(l);

    return l;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param media DOCUMENTATION PENDING
   */
  public void setRelatedMediaCollection(Collection media)
  {
    if(media != null)
    {
      relatedMediaCollection = media;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public int getId()
  {
    return id;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pid DOCUMENTATION PENDING
   */
  public void setId(int pid)
  {
    id = pid;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getItemType()
  {
    return itemType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pType DOCUMENTATION PENDING
   */
  public void setItemType(String pType)
  {
    itemType = pType;
  }

  /* get and set method for question name */
  public String getForwardAction()
  {
    return forwardAction;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param f DOCUMENTATION PENDING
   */
  public void setForwardAction(String f)
  {
    if(f != null)
    {
      forwardAction = f;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getNumber()
  {
    return number;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newNumber DOCUMENTATION PENDING
   */
  public void setNumber(String newNumber)
  {
    number = newNumber;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getPools()
  {
    return pools;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newPools DOCUMENTATION PENDING
   */
  public void setPools(String newPools)
  {
    pools = newPools;
  }

  /**
   * DOCUMENTATION PENDING
   */
  private void resetFields()
  {
    if(resetFields)
    {
      id = 0;
      name = "";
      name_isInstructorViewable = false;
      name_isInstructorEditable = false;
      name_isStudentViewable = false;
      type = "text";
      a_type = "text";
      hint = "";
      hint_isInstructorViewable = false;
      hint_isInstructorEditable = false;
      hint_isStudentViewable = false;
      value = "";
      value_isInstructorViewable = true;
      value_isInstructorEditable = true;
      value_isStudentViewable = true;
      objectives = "";
      objectives_isInstructorViewable = false;
      objectives_isInstructorEditable = false;
      objectives_isStudentViewable = false;
      keywords = "";
      keywords_isInstructorViewable = false;
      keywords_isInstructorEditable = false;
      keywords_isStudentViewable = false;
      rubrics = "";
      rubrics_isInstructorViewable = false;
      rubrics_isInstructorEditable = false;
      rubrics_isStudentViewable = false;
      feedback = "";
      feedback_isInstructorViewable = false;
      feedback_isInstructorEditable = false;
      feedback_isStudentViewable = false;
      mediaCollection = new ArrayList();
      relatedMediaCollection = new ArrayList();
      answers = new ArrayList();
      text = "";
      itemType = "";
      pools = "No pools found.";

      // this is debugging code and should be activated for debugging only
      //      dummy.setLocation("http://www.hungry.com/");
      //      dummy.setLink(false);
      //      dummy.setAuthor("Rachel Gollub");
      //      dummy.setType("link");
      //      dummy.setTypeId(2);
      //      dummy.setName("Hungry link.");
      //      dummy.setDescription("Link to the hungry home page.");
      //      dummy.setDateAdded(new Date());
      //      Answer ans=new Answer();
      //      ans.setId(9230);
      //      ans.setText("Option Text");
      //      ans.setIsCorrect(true);
      //      answers.add(ans);
      //      text="Question Text ";
      //      itemType = "Multiple Choice";
    }
  }
}
