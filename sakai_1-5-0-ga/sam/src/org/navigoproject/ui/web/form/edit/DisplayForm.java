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
 * Description: AAM - class form for assessmentDisplaySettingWizard
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
 * @version $Id: DisplayForm.java,v 1.1.1.1 2004/07/28 21:32:07 rgollub.stanford.edu Exp $
 */
public class DisplayForm
  extends ActionForm
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(DisplayForm.class);
    
  private String itemAccessType;
  private boolean itemAccessType_isInstructorViewable;
  private boolean itemAccessType_isInstructorEditable;
  private boolean itemAccessType_isStudentViewable;
  private boolean itemBookMarking;
  private boolean itemBookMarking_isInstructorViewable;
  private boolean itemBookMarking_isInstructorEditable;
  private boolean itemBookMarking_isStudentViewable;
  private String displayChunking;
  private boolean displayChunking_isInstructorViewable;
  private boolean displayChunking_isInstructorEditable;
  private boolean displayChunking_isStudentViewable;
  private boolean multiPartAllowed;
  private boolean multiPartAllowed_isInstructorViewable;
  private boolean multiPartAllowed_isInstructorEditable;
  private boolean multiPartAllowed_isStudentViewable;

  /**
   * Creates a new DisplayForm object.
   */
  public DisplayForm()
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

    //if (this.name == null || this.name.length() == 0)
    // {
    // }
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
      itemAccessType_isInstructorViewable = false;
      itemAccessType_isInstructorEditable = false;
      itemBookMarking_isInstructorViewable = false;
      itemBookMarking_isInstructorEditable = false;
      displayChunking_isInstructorViewable = false;
      displayChunking_isInstructorEditable = false;
      multiPartAllowed_isInstructorViewable = false;
      multiPartAllowed_isInstructorEditable = false;
      itemAccessType_isStudentViewable = false;
      itemBookMarking_isStudentViewable = false;
      displayChunking_isStudentViewable = false;
      multiPartAllowed_isStudentViewable = false;
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
      return 4;
    }
    else
    {
      int i = 0;
      if(
        itemAccessType_isInstructorEditable ||
          itemAccessType_isInstructorViewable)
      {
        i++;
      }

      if(
        itemBookMarking_isInstructorEditable ||
          itemBookMarking_isInstructorViewable)
      {
        i++;
      }

      if(
        displayChunking_isInstructorEditable ||
          displayChunking_isInstructorViewable)
      {
        i++;
      }

      if(
        multiPartAllowed_isInstructorEditable ||
          multiPartAllowed_isInstructorViewable)
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
  public String getItemAccessType()
  {
    return itemAccessType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param type DOCUMENTATION PENDING
   */
  public void setItemAccessType(String type)
  {
    if(type != null)
    {
      itemAccessType = type;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getItemAccessType_isInstructorViewable()
  {
    return itemAccessType_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param type_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setItemAccessType_isInstructorViewable(
    boolean type_isInstructorViewable)
  {
    itemAccessType_isInstructorViewable = type_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getItemAccessType_isInstructorEditable()
  {
    return itemAccessType_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param type_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setItemAccessType_isInstructorEditable(
    boolean type_isInstructorEditable)
  {
    itemAccessType_isInstructorEditable = type_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getItemAccessType_isStudentViewable()
  {
    return itemAccessType_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param type_isStudentViewable DOCUMENTATION PENDING
   */
  public void setItemAccessType_isStudentViewable(
    boolean type_isStudentViewable)
  {
    itemAccessType_isStudentViewable = type_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getItemBookMarking()
  {
    return itemBookMarking;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param bookMarking DOCUMENTATION PENDING
   */
  public void setItemBookMarking(boolean bookMarking)
  {
    itemBookMarking = bookMarking;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getItemBookMarking_isInstructorViewable()
  {
    return itemBookMarking_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param bookMarking_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setItemBookMarking_isInstructorViewable(
    boolean bookMarking_isInstructorViewable)
  {
    itemBookMarking_isInstructorViewable = bookMarking_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getItemBookMarking_isInstructorEditable()
  {
    return itemBookMarking_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param bookMarking_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setItemBookMarking_isInstructorEditable(
    boolean bookMarking_isInstructorEditable)
  {
    itemBookMarking_isInstructorEditable = bookMarking_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getItemBookMarking_isStudentViewable()
  {
    return itemBookMarking_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param bookMarking_isStudentViewable DOCUMENTATION PENDING
   */
  public void setItemBookMarking_isStudentViewable(
    boolean bookMarking_isStudentViewable)
  {
    itemBookMarking_isStudentViewable = bookMarking_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getDisplayChunking()
  {
    return displayChunking;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param display DOCUMENTATION PENDING
   */
  public void setDisplayChunking(String display)
  {
    if(display != null)
    {
      displayChunking = display;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getDisplayChunking_isInstructorViewable()
  {
    return displayChunking_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param display_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setDisplayChunking_isInstructorViewable(
    boolean display_isInstructorViewable)
  {
    displayChunking_isInstructorViewable = display_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getDisplayChunking_isInstructorEditable()
  {
    return displayChunking_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param display_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setDisplayChunking_isInstructorEditable(
    boolean display_isInstructorEditable)
  {
    displayChunking_isInstructorEditable = display_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getDisplayChunking_isStudentViewable()
  {
    return displayChunking_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param display_isStudentViewable DOCUMENTATION PENDING
   */
  public void setDisplayChunking_isStudentViewable(
    boolean display_isStudentViewable)
  {
    displayChunking_isStudentViewable = display_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getMultiPartAllowed()
  {
    return multiPartAllowed;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param multiPart DOCUMENTATION PENDING
   */
  public void setMultiPartAllowed(boolean multiPart)
  {
    multiPartAllowed = multiPart;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getMultiPartAllowed_isInstructorViewable()
  {
    return multiPartAllowed_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param multiPart_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setMultiPartAllowed_isInstructorViewable(
    boolean multiPart_isInstructorViewable)
  {
    multiPartAllowed_isInstructorViewable = multiPart_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getMultiPartAllowed_isInstructorEditable()
  {
    return multiPartAllowed_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param multiPart_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setMultiPartAllowed_isInstructorEditable(
    boolean multiPart_isInstructorEditable)
  {
    multiPartAllowed_isInstructorEditable = multiPart_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getMultiPartAllowed_isStudentViewable()
  {
    return multiPartAllowed_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param multiPart_isStudentViewable DOCUMENTATION PENDING
   */
  public void setMultiPartAllowed_isStudentViewable(
    boolean multiPart_isStudentViewable)
  {
    multiPartAllowed_isStudentViewable = multiPart_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   */
  public void resetFields()
  {
    if(itemAccessType == null)
    {
      LOG.debug("u r in the display form");
      itemAccessType = "1";
      itemAccessType_isInstructorViewable = true;
      itemAccessType_isInstructorEditable = true;
      itemAccessType_isStudentViewable = false;
      itemBookMarking = true;
      itemBookMarking_isInstructorViewable = true;
      itemBookMarking_isInstructorEditable = true;
      itemBookMarking_isStudentViewable = false;
      displayChunking = "1";
      displayChunking_isInstructorViewable = true;
      displayChunking_isInstructorEditable = true;
      displayChunking_isStudentViewable = false;
      multiPartAllowed = true;
      multiPartAllowed_isInstructorViewable = false;
      multiPartAllowed_isInstructorEditable = false;
      multiPartAllowed_isStudentViewable = false;
    }
  }
}
