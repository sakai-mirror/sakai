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

package org.navigoproject.settings;

import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.PropertyConfigurator;

/**
 * Initializes log4j at startup.
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id: Log4jConfigListener.java,v 1.1.1.1 2004/07/28 21:32:07 rgollub.stanford.edu Exp $
 */
public class Log4jConfigListener
  implements ServletContextListener
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(Log4jConfigListener.class);
    
  /**
   * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
   */
  public void contextInitialized(ServletContextEvent sce)
  {  	
    LOG.debug(
      "Log4jConfigListener.contextInitialized(ServletContextEvent " + sce +
      ")");
    PathInfo pathInfo = PathInfo.getInstance();
    PropertyConfigurator.configureAndWatch(
      pathInfo.getPathToSettings() +
      "/log4j.properties", 5 * 60 * 1000);

    LOG.debug("Log4jConfigListener initialized successfully!");
  }

  /**
   * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
   */
  public void contextDestroyed(ServletContextEvent sce)
  {
    ;
  }
}