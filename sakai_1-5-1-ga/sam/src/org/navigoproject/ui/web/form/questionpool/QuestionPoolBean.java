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

package org.navigoproject.ui.web.form.questionpool;

import org.navigoproject.business.entity.questionpool.model.*;

import java.io.Serializable;

import java.util.*;

import javax.servlet.http.*;

import org.apache.log4j.Logger;

import org.apache.struts.action.*;
import org.apache.struts.action.ActionErrors;

import osid.assessment.Item;

/**
 * This holds question pool information.
 *
 * @author Rachel Gollub <rgollub@stanford.edu>
 */
public class QuestionPoolBean
  implements Serializable
{
  private String name;

  /** Use serialVersionUID for interoperability. */
  private final static long serialVersionUID = 418920360211039758L;
  private String description;
  private String id;
  private String parentPoolId;
  private String numberOfSubpools;
  private QuestionPoolProperties properties;
  private static Logger LOG =
    Logger.getLogger(QuestionPoolBean.class.getName());

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getName()
  {
    return name;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newName DOCUMENTATION PENDING
   */
  public void setName(String newName)
  {
    LOG.debug("Setting form name = " + newName);
    name = newName;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newDesc DOCUMENTATION PENDING
   */
  public void setDescription(String newDesc)
  {
    LOG.debug("Set form description: " + newDesc);
    description = newDesc;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getParentPoolId()
  {
    return parentPoolId;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newId DOCUMENTATION PENDING
   */
  public void setParentPoolId(String newId)
  {
    parentPoolId = newId;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getId()
  {
    return id;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newId DOCUMENTATION PENDING
   */
  public void setId(String newId)
  {
    id = newId;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public QuestionPoolProperties getProperties()
  {
    return properties;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newProperties DOCUMENTATION PENDING
   */
  public void setProperties(QuestionPoolProperties newProperties)
  {
    properties = newProperties;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getNumberOfQuestions()
  {
    if(properties.getQuestions() == null)
    {
      return "0";
    }

    return new Integer(properties.getQuestions().size()).toString();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newNumber DOCUMENTATION PENDING
   */
  public void setNumberOfQuestions(String newNumber)
  {
    // Not used.
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getNumberOfSubpools()
  {
    return numberOfSubpools;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newNumber DOCUMENTATION PENDING
   */
  public void setNumberOfSubpools(String newNumber)
  {
    numberOfSubpools = newNumber;
  }

  /**
   * This checks to see if the question titles are unique.  If they're
   * all the same, or null, the titles aren't displayed.
   */
  public boolean getShowTitles()
  {
    String title = null;

    if(properties.getQuestions() == null)
    {
      return true;
    }

    Iterator iter = properties.getQuestions().iterator();
    try
    {
      while(iter.hasNext())
      {
        Item item = (Item) iter.next();

        // If we've found at least two different titles, show titles
        if(
          (item.getDisplayName() != null) && (title != null) &&
            ! item.getDisplayName().equals(title))
        {
          return true;
        }

        if((title == null) && (item.getDisplayName() != null))
        {
          title = item.getDisplayName();
        }
      }
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }

    return true;
  }
}
