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

import java.io.Serializable;

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
 * Description: AAM - class form for editing Submissions
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
public class SubmissionsForm
  extends ActionForm
  implements Serializable
{
  private String lateHandling;
  private boolean lateHandling_isInstructorViewable;
  private boolean lateHandling_isInstructorEditable;
  private boolean lateHandling_isStudentViewable;
  private String submissionModel;
  private String submissionNumber;
  private boolean submissionModel_isInstructorViewable;
  private boolean submissionModel_isInstructorEditable;
  private boolean submissionModel_isStudentViewable;
  private String submissionsSaved1;
  private String submissionsSaved2;
  private String submissionsSaved3;
  private boolean submissionsSaved_isInstructorViewable;
  private boolean submissionsSaved_isInstructorEditable;
  private boolean submissionsSaved_isStudentViewable;

  /**
   * Creates a new SubmissionsForm object.
   */
  public SubmissionsForm()
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

    if((submissionsSaved1 == null) || (submissionsSaved1.length() == 0))
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
      lateHandling_isInstructorViewable = false;
      lateHandling_isInstructorEditable = false;
      submissionModel_isInstructorViewable = false;
      submissionModel_isInstructorEditable = false;
      submissionsSaved_isInstructorViewable = false;
      submissionsSaved_isInstructorEditable = false;
    }

    if(
      session.getAttribute("editorRole").equals("templateEditor") ||
        submissionsSaved_isInstructorEditable)
    {
      submissionsSaved1 = "off";
      submissionsSaved2 = "off";
      submissionsSaved3 = "off";
    }

    if(session.getAttribute("editorRole").equals("templateEditor"))
    {
      lateHandling_isStudentViewable = false;
      submissionModel_isStudentViewable = false;
      submissionsSaved_isStudentViewable = false;
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
      return 3;
    }
    else
    {
      int i = 0;
      if(lateHandling_isInstructorEditable ||
          lateHandling_isInstructorViewable)
      {
        i++;
      }

      if(
        submissionModel_isInstructorEditable ||
          submissionModel_isInstructorViewable)
      {
        i++;
      }

      if(
        submissionsSaved_isInstructorEditable ||
          submissionsSaved_isInstructorViewable)
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
  public String getLateHandling()
  {
    return lateHandling;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param late DOCUMENTATION PENDING
   */
  public void setLateHandling(String late)
  {
    if(late != null)
    {
      lateHandling = late;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getLateHandling_isInstructorViewable()
  {
    return lateHandling_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param late_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setLateHandling_isInstructorViewable(
    boolean late_isInstructorViewable)
  {
    lateHandling_isInstructorViewable = late_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getLateHandling_isInstructorEditable()
  {
    return lateHandling_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param late_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setLateHandling_isInstructorEditable(
    boolean late_isInstructorEditable)
  {
    lateHandling_isInstructorEditable = late_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getLateHandling_isStudentViewable()
  {
    return lateHandling_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param late_isStudentViewable DOCUMENTATION PENDING
   */
  public void setLateHandling_isStudentViewable(boolean late_isStudentViewable)
  {
    lateHandling_isStudentViewable = late_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getSubmissionModel()
  {
    return submissionModel;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param model DOCUMENTATION PENDING
   */
  public void setSubmissionModel(String model)
  {
    if(model != null)
    {
      submissionModel = model;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getSubmissionNumber()
  {
    return submissionNumber;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pNumber DOCUMENTATION PENDING
   */
  public void setSubmissionNumber(String pNumber)
  {
    if(pNumber != null)
    {
      submissionNumber = pNumber;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getSubmissionModel_isInstructorViewable()
  {
    return submissionModel_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param model_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setSubmissionModel_isInstructorViewable(
    boolean model_isInstructorViewable)
  {
    submissionModel_isInstructorViewable = model_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getSubmissionModel_isInstructorEditable()
  {
    return submissionModel_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param model_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setSubmissionModel_isInstructorEditable(
    boolean model_isInstructorEditable)
  {
    submissionModel_isInstructorEditable = model_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getSubmissionModel_isStudentViewable()
  {
    return submissionModel_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param model_isStudentViewable DOCUMENTATION PENDING
   */
  public void setSubmissionModel_isStudentViewable(
    boolean model_isStudentViewable)
  {
    submissionModel_isStudentViewable = model_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getSubmissionsSaved1()
  {
    return submissionsSaved1;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param saved DOCUMENTATION PENDING
   */
  public void setSubmissionsSaved1(String saved)
  {
    if(saved != null)
    {
      submissionsSaved1 = saved;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getSubmissionsSaved2()
  {
    return submissionsSaved2;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param saved DOCUMENTATION PENDING
   */
  public void setSubmissionsSaved2(String saved)
  {
    if(saved != null)
    {
      submissionsSaved2 = saved;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getSubmissionsSaved3()
  {
    return submissionsSaved3;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param saved DOCUMENTATION PENDING
   */
  public void setSubmissionsSaved3(String saved)
  {
    if(saved != null)
    {
      submissionsSaved3 = saved;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getSubmissionsSaved_isInstructorViewable()
  {
    return submissionsSaved_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param saved_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setSubmissionsSaved_isInstructorViewable(
    boolean saved_isInstructorViewable)
  {
    submissionsSaved_isInstructorViewable = saved_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getSubmissionsSaved_isInstructorEditable()
  {
    return submissionsSaved_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param saved_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setSubmissionsSaved_isInstructorEditable(
    boolean saved_isInstructorEditable)
  {
    submissionsSaved_isInstructorEditable = saved_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getSubmissionsSaved_isStudentViewable()
  {
    return submissionsSaved_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param saved_isStudentViewable DOCUMENTATION PENDING
   */
  public void setSubmissionsSaved_isStudentViewable(
    boolean saved_isStudentViewable)
  {
    submissionsSaved_isStudentViewable = saved_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   */
  public void resetFields()
  {
    if(lateHandling == null)
    {
      lateHandling = "0";
      lateHandling_isInstructorViewable = true;
      lateHandling_isInstructorEditable = true;
      lateHandling_isStudentViewable = false;
      submissionModel = "0";
      submissionNumber = "Type Number";
      submissionModel_isInstructorViewable = true;
      submissionModel_isInstructorEditable = true;
      submissionModel_isStudentViewable = true;
      submissionsSaved1 = "off";
      submissionsSaved2 = "on";
      submissionsSaved3 = "off";
      submissionsSaved_isInstructorViewable = true;
      submissionsSaved_isInstructorEditable = true;
      submissionsSaved_isStudentViewable = false;
    }
  }
}
