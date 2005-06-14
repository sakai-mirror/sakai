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
 * @version $Id: DistributionForm.java,v 1.1.1.1 2004/07/28 21:32:07 rgollub.stanford.edu Exp $
 */
public class DistributionForm
  extends ActionForm
  implements Serializable
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(DistributionForm.class);
    
  private String toAdmin1;
  private String toAdmin2;
  private String toAdmin3;
  private boolean toAdmin_isInstructorViewable;
  private boolean toAdmin_isInstructorEditable;
  private boolean toAdmin_isStudentViewable;
  private String toTestee1;
  private String toTestee2;
  private String toTestee3;
  private String toTestee4;
  private boolean toTestee_isInstructorViewable;
  private boolean toTestee_isInstructorEditable;
  private boolean toTestee_isStudentViewable;
  private String toGradebook;
  private boolean toGradebook_isInstructorViewable;
  private boolean toGradebook_isInstructorEditable;
  private boolean toGradebook_isStudentViewable;

  /**
   * Creates a new DistributionForm object.
   */
  public DistributionForm()
  {
    super();
    resetFields();
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
      toAdmin_isInstructorViewable = false;
      toAdmin_isInstructorEditable = false;
      toTestee_isInstructorViewable = false;
      toTestee_isInstructorEditable = false;
      toGradebook_isInstructorViewable = false;
      toGradebook_isInstructorEditable = false;
    }

    if(
      session.getAttribute("editorRole").equals("templateEditor") ||
        toTestee_isInstructorEditable)
    {
      toTestee1 = "off";
      toTestee2 = "off";
      toTestee3 = "off";
      toTestee4 = "off";
    }

    if(
      session.getAttribute("editorRole").equals("templateEditor") ||
        toAdmin_isInstructorEditable)
    {
      toAdmin1 = "off";
      toAdmin2 = "off";
      toAdmin3 = "off";
    }

    if(
      session.getAttribute("editorRole").equals("templateEditor") ||
        toGradebook_isInstructorEditable)
    {
      toGradebook = "off";
    }

    if(session.getAttribute("editorRole").equals("templateEditor"))
    {
      toAdmin_isStudentViewable = false;
      toTestee_isStudentViewable = false;
      toGradebook_isStudentViewable = false;
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
      if(toAdmin_isInstructorEditable || toAdmin_isInstructorViewable)
      {
        i++;
      }

      if(toTestee_isInstructorEditable || toTestee_isInstructorViewable)
      {
        i++;
      }

      if(toGradebook_isInstructorEditable || toGradebook_isInstructorViewable)
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
  public String getToAdmin1()
  {
    return toAdmin1;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param admin DOCUMENTATION PENDING
   */
  public void setToAdmin1(String admin)
  {
    if(admin != null)
    {
      toAdmin1 = admin;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getToAdmin2()
  {
    return toAdmin2;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param admin DOCUMENTATION PENDING
   */
  public void setToAdmin2(String admin)
  {
    if(admin != null)
    {
      toAdmin2 = admin;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getToAdmin3()
  {
    return toAdmin3;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param admin DOCUMENTATION PENDING
   */
  public void setToAdmin3(String admin)
  {
    if(admin != null)
    {
      toAdmin3 = admin;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getToAdmin_isInstructorViewable()
  {
    return toAdmin_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param admin_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setToAdmin_isInstructorViewable(
    boolean admin_isInstructorViewable)
  {
    toAdmin_isInstructorViewable = admin_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getToAdmin_isInstructorEditable()
  {
    return toAdmin_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param admin_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setToAdmin_isInstructorEditable(
    boolean admin_isInstructorEditable)
  {
    toAdmin_isInstructorEditable = admin_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getToAdmin_isStudentViewable()
  {
    return toAdmin_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param admin_isStudentViewable DOCUMENTATION PENDING
   */
  public void setToAdmin_isStudentViewable(boolean admin_isStudentViewable)
  {
    toAdmin_isStudentViewable = admin_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getToTestee1()
  {
    return toTestee1;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param testee DOCUMENTATION PENDING
   */
  public void setToTestee1(String testee)
  {
    if(testee != null)
    {
      toTestee1 = testee;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getToTestee2()
  {
    return toTestee2;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param testee DOCUMENTATION PENDING
   */
  public void setToTestee2(String testee)
  {
    if(testee != null)
    {
      toTestee2 = testee;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getToTestee3()
  {
    return toTestee3;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param testee DOCUMENTATION PENDING
   */
  public void setToTestee3(String testee)
  {
    if(testee != null)
    {
      toTestee3 = testee;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getToTestee4()
  {
    return toTestee4;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param testee DOCUMENTATION PENDING
   */
  public void setToTestee4(String testee)
  {
    if(testee != null)
    {
      toTestee4 = testee;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getToTestee_isInstructorViewable()
  {
    return toTestee_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param testee_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setToTestee_isInstructorViewable(
    boolean testee_isInstructorViewable)
  {
    toTestee_isInstructorViewable = testee_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getToTestee_isInstructorEditable()
  {
    return toTestee_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param testee_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setToTestee_isInstructorEditable(
    boolean testee_isInstructorEditable)
  {
    toTestee_isInstructorEditable = testee_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getToTestee_isStudentViewable()
  {
    return toTestee_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param testee_isStudentViewable DOCUMENTATION PENDING
   */
  public void setToTestee_isStudentViewable(boolean testee_isStudentViewable)
  {
    toTestee_isStudentViewable = testee_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getToGradebook()
  {
    return toGradebook;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param gradebook DOCUMENTATION PENDING
   */
  public void setToGradebook(String gradebook)
  {
    if(gradebook != null)
    {
      toGradebook = gradebook;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getToGradebook_isInstructorViewable()
  {
    return toGradebook_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param gradebook_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setToGradebook_isInstructorViewable(
    boolean gradebook_isInstructorViewable)
  {
    toGradebook_isInstructorViewable = gradebook_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getToGradebook_isInstructorEditable()
  {
    return toGradebook_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param gradebook_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setToGradebook_isInstructorEditable(
    boolean gradebook_isInstructorEditable)
  {
    toGradebook_isInstructorEditable = gradebook_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getToGradebook_isStudentViewable()
  {
    return toGradebook_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param gradebook_isStudentViewable DOCUMENTATION PENDING
   */
  public void setToGradebook_isStudentViewable(
    boolean gradebook_isStudentViewable)
  {
    toGradebook_isStudentViewable = gradebook_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   */
  public void resetFields()
  {
    if(toAdmin1 == null)
    {
      LOG.debug("u r in distribution form");
      toAdmin1 = "off";
      toAdmin2 = "on";
      toAdmin3 = "off";
      toAdmin_isInstructorViewable = true;
      toAdmin_isInstructorEditable = true;
      toAdmin_isStudentViewable = false;
      toTestee1 = "on";
      toTestee2 = "off";
      toTestee3 = "off";
      toTestee4 = "off";
      toTestee_isInstructorViewable = true;
      toTestee_isInstructorEditable = true;
      toTestee_isStudentViewable = false;
      toGradebook = "on";
      toGradebook_isInstructorViewable = true;
      toGradebook_isInstructorEditable = true;
      toGradebook_isStudentViewable = false;
    }
  }
}
