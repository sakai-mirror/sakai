
/*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
*
* Licensed under the Educational Community License Version 1.0 (the "License");
* By obtaining, using and/or copying this Original Work, you agree that you have read,
* understand, and will comply with the terms and conditions of the Educational Community License.
* You may obtain a copy of the License at:
*
*      http://cvs.sakaiproject.org/licenses/license_1_0.html
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
* AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package org.sakaiproject.tool.assessment.ui.bean.questionpool;

import org.sakaiproject.tool.assessment.business.entity.questionpool.*;

import java.io.Serializable;

import java.util.*;

import javax.servlet.http.*;

import org.apache.log4j.Logger;

import org.apache.struts.action.*;
import org.apache.struts.action.ActionErrors;

import osid.assessment.Item;
import org.sakaiproject.tool.assessment.data.dao.questionpool.QuestionPoolProperties;

/**
 * This holds question pool information.
 *
 * Used to be org.navigoproject.ui.web.form.questionpool.QuestionPoolBean
 *
 * @author Rachel Gollub <rgollub@stanford.edu>
 */
public class QuestionPoolData
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
  private ArrayList parentPools = new ArrayList();






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
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ArrayList getParentPools()
  {
    return parentPools;
  }

  public void setParentPools(ArrayList newpools)
  {
    parentPools = newpools;
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
