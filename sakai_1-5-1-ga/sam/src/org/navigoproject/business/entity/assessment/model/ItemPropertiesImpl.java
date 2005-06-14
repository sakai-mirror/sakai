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

package org.navigoproject.business.entity.assessment.model;

import org.navigoproject.business.entity.ItemTemplate;
import org.navigoproject.business.entity.properties.ItemProperties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import osid.shared.Type;

/**
 * <p>
 * Title: NavigoProject.org
 * </p>
 *
 * <p>
 * Description: OKI based implementation
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
 * @author Rachel Gollub
 * @version 1.0
 */
public class ItemPropertiesImpl
  implements ItemProperties
{
  // General data
  private Collection mediaCollection;
  private String type;
  private String text;
  private String objectives;
  private String keywords;
  private String rubrics;
  private ItemTemplate itemTemplate;
  private String value;
  private Collection answers;
  private String hint;
  private String feedback;
  private boolean pagebreak = false;
  private boolean isSelected = false; // Used by question pools
  private Type itemType;
  private String position;

  /**
   * Creates a new ItemPropertiesImpl object.
   */
  public ItemPropertiesImpl()
  {
    mediaCollection = new ArrayList();
    answers = new ArrayList();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getType()
  {
    return type;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param ptype DOCUMENTATION PENDING
   */
  public void setType(String ptype)
  {
    type = ptype;
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
   * @param pmediaCollection DOCUMENTATION PENDING
   */
  public void setMediaCollection(Collection pmediaCollection)
  {
    mediaCollection = pmediaCollection;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getReversedMediaCollection()
  {
    List l = new ArrayList(this.mediaCollection);
    Collections.reverse(l);

    return l;
  }

  /**
   * This should be a MediaData object.
   *
   * @param pmedia DOCUMENTATION PENDING
   */
  public void addMedia(Object pmedia)
  {
    mediaCollection.add(pmedia);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pmedia DOCUMENTATION PENDING
   */
  public void removeMedia(Object pmedia)
  {
    mediaCollection.remove(pmedia);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getText()
  {
    return text;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param ptext DOCUMENTATION PENDING
   */
  public void setText(String ptext)
  {
    text = ptext;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getObjectives()
  {
    return objectives;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pobjectives DOCUMENTATION PENDING
   */
  public void setObjectives(String pobjectives)
  {
    objectives = pobjectives;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getKeywords()
  {
    return keywords;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pkeywords DOCUMENTATION PENDING
   */
  public void setKeywords(String pkeywords)
  {
    keywords = pkeywords;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getRubrics()
  {
    return rubrics;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param prubrics DOCUMENTATION PENDING
   */
  public void setRubrics(String prubrics)
  {
    rubrics = prubrics;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ItemTemplate getItemTemplate()
  {
    return itemTemplate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pitemTemplate DOCUMENTATION PENDING
   */
  public void setItemTemplate(ItemTemplate pitemTemplate)
  {
    itemTemplate = pitemTemplate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getValue()
  {
    return value;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pvalue DOCUMENTATION PENDING
   */
  public void setValue(String pvalue)
  {
    value = pvalue;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getAnswers()
  {
    if(answers != null)
    {
      return answers;
    }

    return new ArrayList();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param panswers DOCUMENTATION PENDING
   */
  public void setAnswers(Collection panswers)
  {
    answers = panswers;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Object[] getAnswerArray()
  {
    if(answers != null)
    {
      return answers.toArray();
    }

    return new Object[0];
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param answerarray DOCUMENTATION PENDING
   */
  public void setAnswerArray(Answer[] answerarray)
  {
    //ignore
  }

  /**
   * Add an Answer object to the list of possible answers.
   *
   * @param panswer The Answer object.
   */
  public void addAnswer(Object panswer)
  {
    if(answers == null)
    {
      answers = new ArrayList();
    }

    answers.add(panswer);
  }

  /**
   * Remove an Answer object from the list of possible answers.
   *
   * @param panswer The Answer object.
   */
  public void removeAnswer(Object panswer)
  {
    if(answers != null)
    {
      answers.remove(panswer);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getHint()
  {
    return hint;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param phint DOCUMENTATION PENDING
   */
  public void setHint(String phint)
  {
    hint = phint;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getFeedback()
  {
    return feedback;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pfeedback DOCUMENTATION PENDING
   */
  public void setFeedback(String pfeedback)
  {
    feedback = pfeedback;
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
   * @param ppagebreak DOCUMENTATION PENDING
   */
  public void setPageBreak(boolean ppagebreak)
  {
    pagebreak = ppagebreak;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getIsSelected()
  {
    return isSelected;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newIsSelected DOCUMENTATION PENDING
   */
  public void setIsSelected(boolean newIsSelected)
  {
    isSelected = newIsSelected;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Type getItemType()
  {
    return itemType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pitemType DOCUMENTATION PENDING
   */
  public void setItemType(Type pitemType)
  {
    itemType = pitemType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getPosition()
  {
    return position;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newPosition DOCUMENTATION PENDING
   */
  public void setPosition(String newPosition)
  {
    position = newPosition;
  }
}
