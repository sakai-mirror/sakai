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
public class NotificationForm
  extends ActionForm
  implements Serializable
{
  private String testeeNotification1;
  private String testeeNotification2;
  private String testeeNotification3;
  private String testeeNotification4;
  private boolean testeeNotification_isInstructorViewable;
  private boolean testeeNotification_isInstructorEditable;
  private boolean testeeNotification_isStudentViewable;
  private String instructorNotification1;
  private String instructorNotification2;
  private String instructorNotification3;
  private String instructorNotification4;
  private String instructorNotification5;
  private boolean instructorNotification_isInstructorViewable;
  private boolean instructorNotification_isInstructorEditable;
  private boolean instructorNotification_isStudentViewable;

  /**
   * Creates a new NotificationForm object.
   */
  public NotificationForm()
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

    // if (this.savedSubmissions == null || this.savedSubmissions.length() == 0)
    //   {
    //   }
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
      testeeNotification_isInstructorViewable = false;
      testeeNotification_isInstructorEditable = false;
      instructorNotification_isInstructorViewable = false;
      instructorNotification_isInstructorEditable = false;
      testeeNotification_isStudentViewable = false;
      instructorNotification_isStudentViewable = false;
    }

    if(
      session.getAttribute("editorRole").equals("templateEditor") ||
        testeeNotification_isInstructorEditable)
    {
      testeeNotification1 = "off";
      testeeNotification2 = "off";
      testeeNotification3 = "off";
      testeeNotification4 = "off";
    }

    if(
      session.getAttribute("editorRole").equals("templateEditor") ||
        instructorNotification_isInstructorEditable)
    {
      instructorNotification1 = "off";
      instructorNotification2 = "off";
      instructorNotification3 = "off";
      instructorNotification4 = "off";
      instructorNotification5 = "off";
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
      return 2;
    }
    else
    {
      int i = 0;
      if(
        testeeNotification_isInstructorEditable ||
          testeeNotification_isInstructorViewable)
      {
        i++;
      }

      if(
        instructorNotification_isInstructorEditable ||
          instructorNotification_isInstructorViewable)
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
  public String getTesteeNotification1()
  {
    return testeeNotification1;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param testee DOCUMENTATION PENDING
   */
  public void setTesteeNotification1(String testee)
  {
    if(testee != null)
    {
      testeeNotification1 = testee;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTesteeNotification2()
  {
    return testeeNotification2;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param testee DOCUMENTATION PENDING
   */
  public void setTesteeNotification2(String testee)
  {
    if(testee != null)
    {
      testeeNotification2 = testee;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTesteeNotification3()
  {
    return testeeNotification3;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param testee DOCUMENTATION PENDING
   */
  public void setTesteeNotification3(String testee)
  {
    if(testee != null)
    {
      testeeNotification3 = testee;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTesteeNotification4()
  {
    return testeeNotification4;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param testee DOCUMENTATION PENDING
   */
  public void setTesteeNotification4(String testee)
  {
    if(testee != null)
    {
      testeeNotification4 = testee;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getTesteeNotification_isInstructorViewable()
  {
    return testeeNotification_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param testee_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setTesteeNotification_isInstructorViewable(
    boolean testee_isInstructorViewable)
  {
    testeeNotification_isInstructorViewable = testee_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getTesteeNotification_isInstructorEditable()
  {
    return testeeNotification_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param testee_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setTesteeNotification_isInstructorEditable(
    boolean testee_isInstructorEditable)
  {
    testeeNotification_isInstructorEditable = testee_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getTesteeNotification_isStudentViewable()
  {
    return testeeNotification_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param testee_isStudentViewable DOCUMENTATION PENDING
   */
  public void setTesteeNotification_isStudentViewable(
    boolean testee_isStudentViewable)
  {
    testeeNotification_isStudentViewable = testee_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getInstructorNotification1()
  {
    return instructorNotification1;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param instructor DOCUMENTATION PENDING
   */
  public void setInstructorNotification1(String instructor)
  {
    if(instructor != null)
    {
      instructorNotification1 = instructor;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getInstructorNotification2()
  {
    return instructorNotification2;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param instructor DOCUMENTATION PENDING
   */
  public void setInstructorNotification2(String instructor)
  {
    if(instructor != null)
    {
      instructorNotification2 = instructor;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getInstructorNotification3()
  {
    return instructorNotification3;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param instructor DOCUMENTATION PENDING
   */
  public void setInstructorNotification3(String instructor)
  {
    if(instructor != null)
    {
      instructorNotification3 = instructor;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getInstructorNotification4()
  {
    return instructorNotification4;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param instructor DOCUMENTATION PENDING
   */
  public void setInstructorNotification4(String instructor)
  {
    if(instructor != null)
    {
      instructorNotification4 = instructor;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getInstructorNotification5()
  {
    return instructorNotification5;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param instructor DOCUMENTATION PENDING
   */
  public void setInstructorNotification5(String instructor)
  {
    if(instructor != null)
    {
      instructorNotification5 = instructor;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getInstructorNotification_isInstructorViewable()
  {
    return instructorNotification_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param instructor_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setInstructorNotification_isInstructorViewable(
    boolean instructor_isInstructorViewable)
  {
    instructorNotification_isInstructorViewable =
      instructor_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getInstructorNotification_isInstructorEditable()
  {
    return instructorNotification_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param instructor_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setInstructorNotification_isInstructorEditable(
    boolean instructor_isInstructorEditable)
  {
    instructorNotification_isInstructorEditable =
      instructor_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getInstructorNotification_isStudentViewable()
  {
    return instructorNotification_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param instructor_isStudentViewable DOCUMENTATION PENDING
   */
  public void setInstructorNotification_isStudentViewable(
    boolean instructor_isStudentViewable)
  {
    instructorNotification_isStudentViewable = instructor_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   */
  public void resetFields()
  {
    if(testeeNotification1 == null)
    {
      testeeNotification1 = "off";
      testeeNotification2 = "off";
      testeeNotification3 = "off";
      testeeNotification4 = "off";
      testeeNotification_isInstructorViewable = true;
      testeeNotification_isInstructorEditable = true;
      testeeNotification_isStudentViewable = false;
      instructorNotification1 = "off";
      instructorNotification2 = "off";
      instructorNotification3 = "off";
      instructorNotification4 = "off";
      instructorNotification5 = "off";
      instructorNotification_isInstructorViewable = true;
      instructorNotification_isInstructorEditable = true;
      instructorNotification_isStudentViewable = false;
    }
  }
}
