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

package org.navigoproject.ui.web.messaging;

import org.navigoproject.AbstractCompositeObject;
import org.navigoproject.business.exception.ValidationException;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;

/**
 * <p>Title: NavigoProject.org</p>
 * <p>Description: OKI based implementation</p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id: DisplayMessage.java,v 1.1.1.1 2004/07/28 21:32:08 rgollub.stanford.edu Exp $
 */
public class DisplayMessage
  extends AbstractCompositeObject
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(DisplayMessage.class);
  private static final String NAME =
    "org.navigoproject.ui.web.misc.DisplayMessage";
  private static final int DEFAULT_TIMEOUT = -1;
  private static final boolean DEFAULT_TERMINUS = false;
  private String title;
  private String message;
  private ActionForward actionForward;
  private int timeout = DEFAULT_TIMEOUT;
  private boolean terminus = DEFAULT_TERMINUS;

  /**
   * This method will create a new instance of <code>DisplayMessage</code>
   * and return a reference to it.
   * @param title The title to be displayed for this message.
   * @param message The body of the message (HTML).
   * @param actionForward After the message is displayed, the request will be
   * forwarded here.
   * @param request HttpServletRequest
   * @return DisplayMessage
   * @throws ValidationException
   */
  public static DisplayMessage getInstance(
    String title, String message, ActionForward actionForward,
    HttpServletRequest request)
    throws ValidationException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "getInstance(String " + title + ", " + "String " + message + ", " +
        "ActionForward " + actionForward + ", " +
        "HttpServletRequest request)");
    }

    return new DisplayMessage(
      title, message, actionForward, DEFAULT_TIMEOUT, DEFAULT_TERMINUS, request);
  }

  /**
   * This method will create a new instance of <code>DisplayMessage</code>
   * and return a reference to it.
   * @param title The title to be displayed for this message.
   * @param message The body of the message (HTML).
   * @param actionForward After the message is displayed, the request will be
   * forwarded here.
   * @param timeout Controls how long the message will be displayed in seconds.
   * @param request HttpServletRequest
   * @return DisplayMessage
   * @throws ValidationException
   */
  public static DisplayMessage getInstance(
    String title, String message, ActionForward actionForward, int timeout,
    HttpServletRequest request)
    throws ValidationException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "getInstance(String " + title + ", " + "String " + message + ", " +
        "ActionForward " + actionForward + ", " + "int " + timeout + ", " +
        "HttpServletRequest request)");
    }

    return new DisplayMessage(
      title, message, actionForward, timeout, DEFAULT_TERMINUS, request);
  }

  /**
   * This method will create a new instance of <code>DisplayMessage</code>
   * and return a reference to it.
   * @param title The title to be displayed for this message.
   * @param message The body of the message (HTML).
   * @param actionForward After the message is displayed, the request will be
   * forwarded here.
   * @param timeout Controls how long the message will be displayed in seconds.
   * @param terminus If this is set to <code>true</code>, the request will be
   * forwarded to EXIT after displaying the message.
   * @param request HttpServletRequest
   * @return DisplayMessage
   * @throws ValidationException
   */
  public static DisplayMessage getInstance(
    String title, String message, ActionForward actionForward, int timeout,
    boolean terminus, HttpServletRequest request)
    throws ValidationException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "getInstance(String " + title + ", " + "String " + message + ", " +
        "ActionForward " + actionForward + ", " + "int " + timeout + ", " +
        "boolean " + terminus + ", " + "HttpServletRequest request)");
    }

    return new DisplayMessage(
      title, message, actionForward, timeout, terminus, request);
  }

  /**
   * This method is used to get an instance of a pre-existing
   * <code>DisplayMessage</code>.
   * @param request HttpServletRequest
   * @return DisplayMessage
   */
  public static DisplayMessage getInstance(HttpServletRequest request)
  {
    LOG.debug("getInstance(HttpServletRequest request)");

    return (DisplayMessage) request.getSession().getAttribute(NAME);
  }

  /**
   * Creates a new DisplayMessage object.
   *
   * @param title DOCUMENTATION PENDING
   * @param message DOCUMENTATION PENDING
   * @param actionForward DOCUMENTATION PENDING
   * @param timeout DOCUMENTATION PENDING
   * @param terminus DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   *
   * @throws ValidationException DOCUMENTATION PENDING
   */
  private DisplayMessage(
    String title, String message, ActionForward actionForward, int timeout,
    boolean terminus, HttpServletRequest request)
    throws ValidationException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "new DisplayMessage(String " + title + ", " + "String " + message +
        ", " + "ActionForward " + actionForward + ", " + "int " + timeout +
        ", " + "+boolean " + terminus + ", " + "HttpServletRequest request)");
    }

    if(title == null)
    {
      throw new ValidationException("illegal String title argument: " + title);
    }

    if(message == null)
    {
      throw new ValidationException(
        "illegal String message argument: " + message);
    }

    if(actionForward == null)
    {
      throw new ValidationException(
        "illegal ActionForward actionForward argument: " + actionForward);
    }

    if(request == null)
    {
      throw new ValidationException(
        "illegal HttpServletRequest request argument: " + request);
    }

    this.title = title;
    this.message = message;
    this.actionForward = actionForward;
    this.timeout = timeout;
    this.terminus = terminus;
    request.getSession().setAttribute(NAME, this);
  }

  /**
   * Removes the object from session.
   * @param request HttpServletRequest
   */
  public void remove(HttpServletRequest request)
  {
    LOG.debug("remove(HttpServletRequest request)");
    request.getSession().removeAttribute(NAME);
  }

  /* getters */
  public ActionForward getActionForward()
  {
    LOG.debug("getActionForward()");

    return this.actionForward;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getMessage()
  {
    LOG.debug("getMessage()");

    return this.message;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public int getTimeout()
  {
    LOG.debug("getTimeout()");

    return this.timeout;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTitle()
  {
    LOG.debug("getTitle()");

    return this.title;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean isTerminus()
  {
    LOG.debug("isTerminus()");

    return this.terminus;
  }
}
