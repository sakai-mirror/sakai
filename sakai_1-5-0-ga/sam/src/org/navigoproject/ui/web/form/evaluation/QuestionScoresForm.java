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

package org.navigoproject.ui.web.form.evaluation;

import org.navigoproject.business.entity.AssessmentTemplate;
import org.navigoproject.business.entity.BeanSort;
import org.navigoproject.business.entity.assessment.model.AssessmentTemplateImpl;
import org.navigoproject.business.entity.evaluation.model.AgentResults;

import java.io.Serializable;

import java.util.*;

import javax.servlet.http.*;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import org.navigoproject.business.entity.Item;
//import osid.assessment.Item;

/**
 * <p>Title: NavigoProject.org</p>
 * <p>Description: AAM - class form for evaluating individual question scores</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Organization: Stanford University</p>
 * @author Rachel Gollub
 * @author Ed Smiley
 * @author Huong Nguyen
 * @version $Id: QuestionScoresForm.java,v 1.1.1.1 2004/07/28 21:32:08 rgollub.stanford.edu Exp $
 */
public class QuestionScoresForm
  extends ActionForm
  implements Serializable
{
  private String assessmentName;
  private String assessmentId;
  private String maxScore;
  private String groupName;
  private Collection agents;
  private Collection sortedAgents;
  private String sortProperty;
  private String totalPeople;
  public String questionNumber;
  public String partNumber;
  public Integer parts;
  public Integer[] questions;
  public Item item;
  private String lateHandling; // read-only property set for UI late handling
  private String dueDate;
  private String commentEachQuestion;
  private String sortType;
  private String roleSelection;
  private boolean anonymous;
  private boolean allSubmissions;
  BeanSort bs ;

  private String questionText;
  private String questionAnswer;
  private String questionPoint;
  private String partText="Dummy Part Name.";
  private String questionType="True/False";

    public QuestionScoresForm()
 {
    super();
    resetFields();
  }

  /**
   * Standard Struts validation
   *
   * @param actionMapping action mapping
   * @param httpServletRequest the servlet request
   *
   * @return Struts action errors object
   */
  public ActionErrors validate(
    ActionMapping actionMapping, HttpServletRequest httpServletRequest)
  {
    ActionErrors errors = new ActionErrors();

    return errors;
  }

  /**
   * reset method for Struts
   * @param actionMapping Struts
   * @param httpServletRequest the servlet request
   */


  
  public void reset(
    ActionMapping actionMapping, HttpServletRequest httpServletRequest)
  {
    // Set checkboxes to false here.
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getAssessmentName()
  {
    return assessmentName;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param passessmentName DOCUMENTATION PENDING
   */
  public void setAssessmentName(String passessmentName)
  {
    assessmentName = passessmentName;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getAssessmentId()
  {
    return assessmentId;
  }
  /**
   * DOCUMENTATION PENDING
   *
   * @param passessmentName DOCUMENTATION PENDING
   */
  public void setAnonymous (boolean panonymous)
  {
    anonymous = panonymous;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getAnonymous()
  {
    return anonymous;
  }
  /**
   * Is this an all submissions or, just the largest
   * @return true if is is, else false
   */
  public boolean getAllSubmissions()
  {
    return allSubmissions;
  }

  /**
   * set whether all submissions are to be exposed
   * @param pallSubmissions true if it is
   */
  public void setAllSubmissions(boolean pallSubmissions)
  {
    allSubmissions=pallSubmissions;
  }


  /**
   * DOCUMENTATION PENDING
   *
   * @param passessmentId DOCUMENTATION PENDING
   */
  public void setAssessmentId(String passessmentId)
  {
    assessmentId = passessmentId;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getGroupName()
  {
    return groupName;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pgroupName DOCUMENTATION PENDING
   */
  public void setGroupName(String pgroupName)
  {
    groupName = pgroupName;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getAgents()
  {
    return agents;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pagents DOCUMENTATION PENDING
   */
  public void setAgents(Collection pagents)
  {
    agents = pagents;
    if(sortProperty == null)
    {
      setAgentSort("lastName");
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getMaxScore()
  {
    return maxScore;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pmaxScore DOCUMENTATION PENDING
   */
  public void setMaxScore(String pmaxScore)
  {
    maxScore = pmaxScore;
  }

/**
   * get Part Text
   *
   * @return the text
   */
public String getPartText()
{
  return partText;
}

public void setPartText(String ppartText)
{
  partText = ppartText;
}

public String getQuestionType()
{
  return questionType;
}

public void setQuestionType(String pquestionType)
{
  questionType = pquestionType;
}

public String getQuestionText()
{
  return questionText;
}
public void setQuestionText(String pquestionText)
{
  questionText=pquestionText;
}

 public String getQuestionAnswer()
{
   return questionAnswer;
}

public void setQuestionAnswer(String pquestionAnswer)
{
   questionAnswer=pquestionAnswer;
}

public String getQuestionPoint()
{
   return questionPoint;
}
public void setQuestionPoint(String pquestionPoint)
{
   questionPoint=pquestionPoint;
}

  /** This is a read-only calculated property.
   * @return list of uppercase student initials
   */
  public String getAgentInitials()
  {
    Collection c = getAgents();
    String initials = "";
    if(c.isEmpty())
    {
      return "";
    }

    Iterator it = c.iterator();

    while(it.hasNext())
    {
      try
      {
        AgentResults ar = (AgentResults) it.next();
        String initial = ar.getLastInitial();
        initials = initials + initial;
      }
      catch(Exception ex)
      {
        // if there is any problem, we skip, and go on
      }
    }

    return initials.toUpperCase();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getSortedAgents()
  {
    return sortedAgents;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pagents DOCUMENTATION PENDING
   */
  public void setSortedAgents(Collection pagents)
  {
    sortedAgents = pagents;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTotalPeople()
  {
    return totalPeople;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param ptotalPeople DOCUMENTATION PENDING
   */
  public void setTotalPeople(String ptotalPeople)
  {
    totalPeople = ptotalPeople;
  }


  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getQuestionNumber()
  {
    return questionNumber;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pquestionNumber DOCUMENTATION PENDING
   */
  public void setQuestionNumber(String pquestionNumber)
  {
    questionNumber = pquestionNumber;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getPartNumber()
  {
    return partNumber;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param ppartNumber DOCUMENTATION PENDING
   */
  public void setPartNumber(String ppartNumber)
  {
    partNumber = ppartNumber;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Integer getParts()
  {
    return parts;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pparts DOCUMENTATION PENDING
   */
  public void setParts(Integer pparts)
  {
    parts = pparts;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param part DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Integer getLength(Integer part)
  {
    return questions[part.intValue()];
  }


  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Item getItem()
  {
    return item;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pitem DOCUMENTATION PENDING
   */
  public void setItem(Item pitem)
  {
    item = pitem;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Object[] getAgentArray()
  {
//    return sortedAgents.toArray();
    return agents.toArray();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param parray DOCUMENTATION PENDING
   */
  public void setAgentArray(Object[] parray)
  {
    // Not used
  }

  /**
   * This is a "trigger property" which actually initiates a sort
   * of the agent collection to a designated property.
   * @param propertyName the name of the property to sort on
   */
  public void setAgentSort(String propertyName)
  {
       String sortKey = propertyName;

       bs = new BeanSort(agents, sortKey);

    bs.sort();
   sortProperty = propertyName;
  }

  /**
   * Get the name of the property for the sort
   * of the agent collection to a designated property.
   * @return the name of the property to sort on
   */
  public String getAgentSort()
  {
    return sortProperty;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getLateHandling()
  {
    return lateHandling;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param plateHandling DOCUMENTATION PENDING
   */
  public void setLateHandling(String plateHandling)
  {
    lateHandling = plateHandling;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getDueDate()
  {
    return dueDate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param dateString DOCUMENTATION PENDING
   */
  public void setDueDate(String dateString)
  {
    dueDate = dateString;
  }


    public Integer[] getQuestions()
    {
  return questions;
    }
    public void setQuestions(Integer[] pquestions)
    {
  questions=pquestions;
    }



  /**
   * DOCUMENTATION PENDING
   *
   * @param pcommentEach DOCUMENTATION PENDING
   */
  public void setCommentEachQuestion(String pcommentEach)
  {
    commentEachQuestion = pcommentEach;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getCommentEachQuestion()
  {
    return commentEachQuestion;
  }

  public String getSortType()
  {
    return sortType;
  }


  public void setSortType(String psortType)
  {
    sortType=psortType;
  }


  public String getRoleSelection()
  {

    return roleSelection;

  }


  public void setRoleSelection(String proleSelection)
  {
    roleSelection=proleSelection;

  }


  /**
   * DOCUMENTATION PENDING
   */
  public void resetFields()
  {
   
      agents = new ArrayList();
       parts=new Integer(2);
       questions = new Integer[2];
       questions[0] =new Integer(2);
       questions[1] =new Integer(3);

       if(questionNumber == null || questionNumber.equals(""))
       {
       questionNumber="1";
         }
      else setQuestionNumber(questionNumber);
       if(partNumber == null || partNumber.equals(""))
	  {
        partNumber="1";
         }
        else setPartNumber(partNumber);
      
      AgentResults agent = new AgentResults();
     
      agents.add(agent);

      setAgents(agents);

    

  }
}
