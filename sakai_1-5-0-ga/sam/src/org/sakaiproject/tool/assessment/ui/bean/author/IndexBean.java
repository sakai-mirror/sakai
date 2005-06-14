
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

package org.sakaiproject.tool.assessment.ui.bean.author;

import org.sakaiproject.tool.assessment.services.assessment.AssessmentService;
import org.sakaiproject.tool.assessment.facade.AssessmentTemplateFacade;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import osid.assessment.Assessment;
import osid.assessment.AssessmentIterator;

import osid.authentication.AuthenticationManager;


import osid.shared.Agent;
import osid.shared.SharedManager;

/**
 * Used to be org.navigoproject.ui.web.form.IndexForm.java
 *
 * @author $author$
 * @version $Id: IndexBean.java,v 1.6 2005/01/25 06:05:53 daisyf.stanford.edu Exp $
 */
public class IndexBean
  implements Serializable
{
  private Collection templateList;

  /** Use serialVersionUID for interoperability. */
  private final static long serialVersionUID = 7919219404875270127L;
  private Collection templateNames;
  private Collection templateIds;
//  private Collection studentAssessmentList;
//  private Collection assessmentList;
//  private Collection assessmentTypeList;
  private String assessmentTypeChoice;
  private String course_id;
  private String agent_id;
  static Logger LOG = Logger.getLogger(IndexBean.class.getName());

  /**
   * Creates a new IndexBean object.
   */
  public IndexBean()
  {
//    if(assessmentTypeList == null)
//    {
//      assessmentTypeList = new ArrayList();
//    }
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
      FacesContext.getCurrentInstance().
        getExternalContext().getSessionMap().
          put("template", new TemplateBean());
      AssessmentService delegate = new AssessmentService();
      ArrayList list = delegate.getBasicInfoOfAllActiveAssessmentTemplates("title");
      //ArrayList list = delegate.getAllAssessmentTemplates();
      System.out.println("Got " + list.size() + " templates in front end");
      ArrayList templates = new ArrayList();
      Iterator iter = list.iterator();
      while (iter.hasNext())
      {
        AssessmentTemplateFacade facade =
          (AssessmentTemplateFacade) iter.next();
        TemplateBean bean = new TemplateBean();
        bean.setTemplateName(facade.getTitle());
        bean.setIdString(facade.getAssessmentBaseId().toString());
        bean.setLastModified(facade.getLastModifiedDate().toString());
        templates.add(bean);
      }
      return templates;
    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList();
    }
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

//  /**
//   * This is designed to quickly and efficiently obtain a small number of
//   * pieces of information in the assessment list.  Specifically, you will not
//   * be able to obtain properties from the getData() method.  To get a more
//   * complete, but less efficient list, use getStudentAssessmentList()
//   * instead.
//   *
//   * @return bare Collection of assessments
//   */
//  public Collection getAssessmentList()
//  {
//    try
//    {
//      AssessmentServiceDelegate delegate = new AssessmentServiceDelegate();
//      AssessmentIterator aiter = null;
//      try
//      {
//        aiter = delegate.getAssessments((new Long(course_id)).longValue());
//      }
//      catch(Exception e)
//      {
//        LOG.warn("No Course ID specified.");
//        aiter = delegate.getAssessments();
//      }
//
//      assessmentList = new ArrayList();
//
//      while(aiter.hasNext())
//      {
//        Assessment assessment = (Assessment) aiter.next();
//        assessmentList.add(assessment);
//      }
//    }
//    catch(Exception e)
//    {
//      LOG.error(e);
//      if(assessmentList == null)
//      {
//        assessmentList = new ArrayList();
//      }
//    }
//
//    return assessmentList;
//  }
//
//  /**
//   * This is designed to obtain a list of assessments and their properties.
//   *
//   * @return Collection of assessments
//   */
//  public Collection getStudentAssessmentList()
//  {
//    try
//    {
//      AssessmentServiceDelegate delegate = new AssessmentServiceDelegate();
//      Collection coll = null;
//      try
//      {
//        SharedManager sm = OsidManagerFactory.createSharedManager();
//        AuthenticationManager am =
//          OsidManagerFactory.createAuthenticationManager(
//            OsidManagerFactory.getOsidOwner());
//        Agent agent =
//          sm.getAgent(
//            am.getUserId(new TypeImpl("Stanford", "AAM", "agent", "sunetid")));
//        long courseId = new Long(course_id).longValue();
//        coll = delegate.getStudentView(courseId, agent);
//      }
//      catch(Exception e)
//      {
//        LOG.warn("Either No Course ID or Agent ID or Both.");
//        coll = delegate.getStudentView(0, null);
//      }
//
//      studentAssessmentList = new ArrayList(coll);
//    }
//    catch(Exception e)
//    {
//      LOG.error(e);
//      if(studentAssessmentList == null)
//      {
//        studentAssessmentList = new ArrayList();
//      }
//    }
//
//    return studentAssessmentList;
//  }

//  /**
//   * DOCUMENTATION PENDING
//   *
//   * @param assessmentList DOCUMENTATION PENDING
//   */
//  public void setStudentAssessmentList(Collection assessmentList)
//  {
//    studentAssessmentList = assessmentList;
//  }
//
//  /**
//   * DOCUMENTATION PENDING
//   *
//   * @param assessmentList DOCUMENTATION PENDING
//   */
//  public void setAssessmentList(Collection assessmentList)
//  {
//    this.assessmentList = assessmentList;
//  }

//  /**
//   * DOCUMENTATION PENDING
//   *
//   * @return DOCUMENTATION PENDING
//   */
//  public Collection getAssessmentTypeList()
//  {
//    return assessmentTypeList;
//  }
//
//  /**
//   * DOCUMENTATION PENDING
//   *
//   * @param assessmentTypeList DOCUMENTATION PENDING
//   */
//  public void setAssessmentTypeList(Collection assessmentTypeList)
//  {
//    this.assessmentTypeList = assessmentTypeList;
//  }

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

}
