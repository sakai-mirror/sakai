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

package org.navigoproject;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * DOCUMENT ME!
 *
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id$
 */
public class CompositeObjectUtil
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(CompositeObjectUtil.class);

  /**
   * Creates a new CompositeObjectUtil object.
   *
   * @throws java.lang.UnsupportedOperationException DOCUMENTATION PENDING
   */
  private CompositeObjectUtil()
  {
    LOG.debug("new CompositeObjectUtil()");
    throw new java.lang.UnsupportedOperationException(
      "Constructor not implemented.");
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param object DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public static String printToString(Object object)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("printToString(Object object)");
    }

    StringBuffer buff = new StringBuffer();
    buff.append("{");
    printData(buff, object);
    buff.append("}");

    return buff.toString();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param mutableBuffer DOCUMENTATION PENDING
   * @param object DOCUMENTATION PENDING
   */
  private static void printData(StringBuffer mutableBuffer, Object object)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("printData(StringBuffer mutableBuffer, Object object)");
    }

    try
    {
      Map map = PropertyUtils.describe(object);
      for(Iterator i = map.keySet().iterator(); i.hasNext();)
      {
        String key = (String) i.next();
        if(
          ! key.equals("class") && ! key.equals("servletWrapper") &&
            ! key.equals("multipartRequestHandler"))
        { // filter out unwanted properties
          mutableBuffer.append(key);
          mutableBuffer.append("=");
          if(key.equalsIgnoreCase("password") ||
              key.equalsIgnoreCase("passwd"))
          { // do not print passwords, replace with "*"
            mutableBuffer.append(debugPassword(map.get(key)));
          }
          else
          {
            mutableBuffer.append(map.get(key));
          }

          if(i.hasNext())
          {
            mutableBuffer.append(", ");
          }
        }
      }
    }
    catch(Exception ex)
    {
      LOG.warn(ex.getMessage(), ex);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param password DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public static String debugPassword(Object password)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("debugPassword(Object password)");
    }

    String passwordStr = null;
    if(password != null)
    {
      String passwd = "" + password;
      passwordStr = "";
      for(int i = 0; i < passwd.length(); i++)
      {
        passwordStr += "*";
      }
    }

    return passwordStr;
  }
}