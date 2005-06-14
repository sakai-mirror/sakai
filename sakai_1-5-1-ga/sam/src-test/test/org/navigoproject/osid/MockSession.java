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


/**
 *
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * @author <a href="mailto:rpembry@indiana.edu">Randall P. Embry</a>
 * @version $Id: MockSession.java,v 1.1.1.1 2004/07/28 21:32:08 rgollub.stanford.edu Exp $
 */
package test.org.navigoproject.osid;

import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/**
 *
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * @author <a href="mailto:rpembry@indiana.edu">Randall P. Embry</a>
 * @version $Id: MockSession.java,v 1.1.1.1 2004/07/28 21:32:08 rgollub.stanford.edu Exp $
 */
public class MockSession
  implements HttpSession
{
  private HashMap session = new HashMap();

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpSession#getCreationTime()
   */
  public long getCreationTime()
  {
    return 0;
  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpSession#getId()
   */
  public String getId()
  {
    return null;
  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpSession#getLastAccessedTime()
   */
  public long getLastAccessedTime()
  {
    return 0;
  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpSession#getServletContext()
   */
  public ServletContext getServletContext()
  {
    return null;
  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpSession#setMaxInactiveInterval(int)
   */
  public void setMaxInactiveInterval(int arg0)
  {
  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpSession#getMaxInactiveInterval()
   */
  public int getMaxInactiveInterval()
  {
    return 0;
  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpSession#getSessionContext()
   *
   */
  public HttpSessionContext getSessionContext()
  {
    return null;
  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpSession#getAttribute(java.lang.String)
   */
  public Object getAttribute(String arg0)
  {
    return session.get(arg0);
  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpSession#getValue(java.lang.String)
   */
  public Object getValue(String arg0)
  {
    return null;
  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpSession#getAttributeNames()
   */
  public Enumeration getAttributeNames()
  {
    return null;
  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpSession#getValueNames()
   */
  public String[] getValueNames()
  {
    return null;
  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpSession#setAttribute(java.lang.String, java.lang.Object)
   */
  public void setAttribute(String arg0, Object arg1)
  {
    this.session.put(arg0, arg1);
  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpSession#putValue(java.lang.String, java.lang.Object)
   */
  public void putValue(String arg0, Object arg1)
  {
  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpSession#removeAttribute(java.lang.String)
   */
  public void removeAttribute(String arg0)
  {
  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpSession#removeValue(java.lang.String)
   */
  public void removeValue(String arg0)
  {
  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpSession#invalidate()
   */
  public void invalidate()
  {
  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpSession#isNew()
   */
  public boolean isNew()
  {
    return false;
  }
}
