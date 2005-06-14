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

package org.navigoproject.ui.web.form;

import org.navigoproject.business.entity.AssessmentTemplate;
import org.navigoproject.business.entity.AssessmentTemplateIterator;
import org.navigoproject.osid.OsidManagerFactory;
import org.navigoproject.osid.assessment.AssessmentServiceDelegate;
import org.navigoproject.osid.shared.impl.TypeImpl;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import osid.assessment.Assessment;
import osid.assessment.AssessmentIterator;

import osid.authentication.AuthenticationManager;

import osid.shared.Agent;
import osid.shared.SharedManager;

/**
 * DOCUMENTATION PENDING
 *
 * @author $author$
 * @version $Id: IndexForm.java,v 1.1.1.1 2004/07/28 21:32:07 rgollub.stanford.edu Exp $
 */
public class IndexForm
  extends ActionForm
{
  private Collection templateList;
  private Collection templateNames;
  private Collection templateIds;
  private Collection studentAssessmentList;
  private Collection assessmentList;
  private Collection assessmentTypeList;
  private String assessmentTypeChoice;
  private String course_id;
  private String agent_id;
  static Logger LOG = Logger.getLogger(IndexForm.class.getName());

  /**
   * Creates a new IndexForm object.
   */
  public IndexForm()
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

    //      if (this.name == null || this.name.length() == 0)
    //      {
    //      }
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
    // No checkboxes to reset.
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getTemplateList()
  {
    try
    {
      AssessmentServiceDelegate delegate = new AssessmentServiceDelegate();
      AssessmentTemplateIterator iter = null;
      try
      {
        iter =
          delegate.getAssessmentTemplates((new Long(course_id)).longValue());
      }
      catch(Exception e)
      {
        LOG.debug("Course ID not specified.");
        iter = delegate.getAssessmentTemplates();
      }

      templateList = new ArrayList();
      templateNames = new ArrayList();
      templateIds = new ArrayList();

      while(iter.hasNext())
      {
        AssessmentTemplate template = (AssessmentTemplate) iter.next();
        if(template.getTemplateName() != null)
        {
          templateList.add(template);
          templateNames.add(template.getTemplateName());
          templateIds.add(template.getId());
          LOG.debug(
            "Name:id = " + template.getTemplateName() + ":" + template.getId());
        }
      }

      assessmentTypeChoice = "";
      if(templateIds.size() > 0)
      {
        assessmentTypeChoice = templateIds.toArray()[0].toString();
      }
    }
    catch(Exception e)
    {
      LOG.error(e); 
      if(templateList == null)
      {
        templateList = new ArrayList();
        templateNames = new ArrayList();
        templateIds = new ArrayList();
      }
    }

    return this.templateList;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param templateList DOCUMENTATION PENDING
   */
  public void setTemplateList(Collection templateList)
  {
    this.templateList = templateList;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getTemplateNames()
  {
    if(templateNames == null)
    {
      getTemplateList();
    }

    return templateNames;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param names DOCUMENTATION PENDING
   */
  public void setTemplateNames(Collection names)
  {
    templateNames = names;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getTemplateIds()
  {
    if(templateIds == null)
    {
      getTemplateList();
    }

    return templateIds;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param ids DOCUMENTATION PENDING
   */
  public void setTemplateIds(Collection ids)
  {
    templateIds = ids;
  }

  /**
   * This is designed to quickly and efficiently obtain a small number of
   * pieces of information in the assessment list.  Specifically, you will not
   * be able to obtain properties from the getData() method.  To get a more
   * complete, but less efficient list, use getStudentAssessmentList()
   * instead.
   *
   * @return bare Collection of assessments
   */
  public Collection getAssessmentList()
  {
    try
    {
      AssessmentServiceDelegate delegate = new AssessmentServiceDelegate();
      AssessmentIterator aiter = null;
      try
      {
        aiter = delegate.getAssessments((new Long(course_id)).longValue());
      }
      catch(Exception e)
      {
        LOG.warn("No Course ID specified.");
        aiter = delegate.getAssessments();
      }

      assessmentList = new ArrayList();

      while(aiter.hasNext())
      {
        Assessment assessment = (Assessment) aiter.next();
        assessmentList.add(assessment);
      }
    }
    catch(Exception e)
    {
      LOG.error(e); 
      if(assessmentList == null)
      {
        assessmentList = new ArrayList();
      }
    }

    return assessmentList;
  }

  /**
   * This is designed to obtain a list of assessments and their properties.
   *
   * @return Collection of assessments
   */
  public Collection getStudentAssessmentList()
  {
    try
    {
      AssessmentServiceDelegate delegate = new AssessmentServiceDelegate();
      Collection coll = null;
      try
      {
        SharedManager sm = OsidManagerFactory.createSharedManager();
        AuthenticationManager am =
          OsidManagerFactory.createAuthenticationManager(
            OsidManagerFactory.getOsidOwner());
        Agent agent =
          sm.getAgent(
            am.getUserId(new TypeImpl("Stanford", "AAM", "agent", "sunetid")));
        long courseId = new Long(course_id).longValue();
        coll = delegate.getStudentView(courseId, agent);
      }
      catch(Exception e)
      {
        LOG.warn("Either No Course ID or Agent ID or Both.");
        coll = delegate.getStudentView(0, null);
      }

      studentAssessmentList = new ArrayList(coll);
    }
    catch(Exception e)
    {
      LOG.error(e); 
      if(studentAssessmentList == null)
      {
        studentAssessmentList = new ArrayList();
      }
    }

    return studentAssessmentList;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentList DOCUMENTATION PENDING
   */
  public void setStudentAssessmentList(Collection assessmentList)
  {
    studentAssessmentList = assessmentList;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentList DOCUMENTATION PENDING
   */
  public void setAssessmentList(Collection assessmentList)
  {
    this.assessmentList = assessmentList;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getAssessmentTypeList()
  {
    return assessmentTypeList;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentTypeList DOCUMENTATION PENDING
   */
  public void setAssessmentTypeList(Collection assessmentTypeList)
  {
    this.assessmentTypeList = assessmentTypeList;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getAssessmentTypeChoice()
  {
    return assessmentTypeChoice;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param typeChoice DOCUMENTATION PENDING
   */
  public void setAssessmentTypeChoice(String typeChoice)
  {
    assessmentTypeChoice = typeChoice;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getCourseId()
  {
    return course_id;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param id DOCUMENTATION PENDING
   */
  public void setCourseId(String id)
  {
    LOG.debug("Setting course id to " + id);
    course_id = id;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getAgentId()
  {
    return agent_id;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param id DOCUMENTATION PENDING
   */
  public void setAgentId(String id)
  {
    LOG.debug("Setting agent id to " + id);
    agent_id = id;
  }

  /**
   * DOCUMENTATION PENDING
   */
  private void resetFields()
  {
    if(assessmentTypeList == null)
    {
      assessmentTypeList = new ArrayList();
    }
  }
}
