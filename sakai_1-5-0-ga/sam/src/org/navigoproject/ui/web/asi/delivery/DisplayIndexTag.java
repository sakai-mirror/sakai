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

package org.navigoproject.ui.web.asi.delivery;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.util.RequestUtils;
import org.apache.struts.util.ResponseUtils;

/**
 * DOCUMENTATION PENDING
 *
 * @author $author$
 * @version $Id: DisplayIndexTag.java,v 1.1.1.1 2004/07/28 21:32:07 rgollub.stanford.edu Exp $
 */
public class DisplayIndexTag
  extends TagSupport
{
  /**
   * Creates a new DisplayIndexTag object.
   */
  public DisplayIndexTag()
  {
  }

  /**
   * <p>Title: NavigoProject.org</p>
   * <p>Description: ASI Delivery implementation</p>
   * <p>Copyright: Copyright (c) 2003 Trustees of Indiana University</p>
   * <p>Company: </p>
   * @author <a href="mailto:casong@indiana.edu">Pamela Song</a>
   * @version $Id: DisplayIndexTag.java,v 1.1.1.1 2004/07/28 21:32:07 rgollub.stanford.edu Exp $
   */
  public int doStartTag()
    throws JspException
  {
    Object value = RequestUtils.lookup(pageContext, name, property, scope);

    if(value == null)
    {
      return (SKIP_BODY);
    }

    String output = formatValue(value);

    ResponseUtils.write(pageContext, output);

    return (SKIP_BODY);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param value DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private String formatValue(Object value)
  {
    String output = "";
    int displayIndex = -1;
    if(value instanceof Integer)
    {
      displayIndex = ((Integer) value).intValue() + 1;
    }
    else if(value instanceof String)
    {
      displayIndex = Integer.parseInt((String) value) + 1;
    }

    return String.valueOf(displayIndex);
  }

  /**
   * Release all allocated resources.
   */
  public void release()
  {
    super.release();
    name = null;
    property = null;
    scope = null;
  }

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
   * @param name DOCUMENTATION PENDING
   */
  public void setName(String name)
  {
    this.name = name;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String GetProperty()
  {
    return property;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param property DOCUMENTATION PENDING
   */
  public void setProperty(String property)
  {
    this.property = property;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String GetScope()
  {
    return scope;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param scope DOCUMENTATION PENDING
   */
  public void SetScope(String scope)
  {
    this.scope = scope;
  }

  private String name = null;
  private String property = null;
  private String scope = null;
}
