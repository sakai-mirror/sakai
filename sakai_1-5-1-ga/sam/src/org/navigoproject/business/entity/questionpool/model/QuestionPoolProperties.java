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

package org.navigoproject.business.entity.questionpool.model;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import osid.shared.Agent;
import osid.shared.Type;

/**
 * DOCUMENTATION PENDING
 *
 * @author $author$
 * @version $Id: QuestionPoolProperties.java,v 1.4 2004/10/15 21:50:26 lydial.stanford.edu Exp $
 */
public class QuestionPoolProperties
  implements Serializable
{
  private Agent owner;

  /** Use serialVersionUID for interoperability. */
  private final static long serialVersionUID = 9180085666292824370L;
  private Date dateCreated;
  private Date lastModified;
  private Agent lastModifiedBy;
  private Type accessType; // This will change depending on who's
                           // requesting the question pool.
  private String objectives;
  private String keywords;
  private String rubric;
  private Type type;
  private String intellectualProperty;
  private String organizationName;
  private Collection items = new ArrayList();

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Agent getOwner()
  {
    return owner;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newOwner DOCUMENTATION PENDING
   */
  public void setOwner(Agent newOwner)
  {
    owner = newOwner;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Date getDateCreated()
  {
    return dateCreated;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newDateCreated DOCUMENTATION PENDING
   */
  public void setDateCreated(Date newDateCreated)
  {
    dateCreated = newDateCreated;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Date getLastModified()
  {
    return lastModified;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newLastModified DOCUMENTATION PENDING
   */
  public void setLastModified(Date newLastModified)
  {
    lastModified = newLastModified;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Agent getLastModifiedBy()
  {
    return lastModifiedBy;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newLastModifiedBy DOCUMENTATION PENDING
   */
  public void setLastModifiedBy(Agent newLastModifiedBy)
  {
    lastModifiedBy = newLastModifiedBy;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Type getAccessType()
  {
    return accessType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newAccessType DOCUMENTATION PENDING
   */
  public void setAccessType(Type newAccessType)
  {
    accessType = newAccessType;
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
   * @param newObjectives DOCUMENTATION PENDING
   */
  public void setObjectives(String newObjectives)
  {
    objectives = newObjectives;
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
   * @param newKeywords DOCUMENTATION PENDING
   */
  public void setKeywords(String newKeywords)
  {
    keywords = newKeywords;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getRubric()
  {
    return rubric;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newRubric DOCUMENTATION PENDING
   */
  public void setRubric(String newRubric)
  {
    rubric = newRubric;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Type getType()
  {
    return type;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newType DOCUMENTATION PENDING
   */
  public void setType(Type newType)
  {
    type = newType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getIntellectualProperty()
  {
    return intellectualProperty;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newIntellectualProperty DOCUMENTATION PENDING
   */
  public void setIntellectualProperty(String newIntellectualProperty)
  {
    intellectualProperty = newIntellectualProperty;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getOrganizationName()
  {
    return organizationName;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newOrganizationName DOCUMENTATION PENDING
   */
  public void setOrganizationName(String newOrganizationName)
  {
    organizationName = newOrganizationName;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getQuestions()
  {
    return items;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newItems DOCUMENTATION PENDING
   */
  public void setQuestions(Collection newItems)
  {
    items = newItems;
  }
}
