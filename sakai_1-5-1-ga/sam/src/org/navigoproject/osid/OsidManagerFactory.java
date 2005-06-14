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

package org.navigoproject.osid;

import org.navigoproject.settings.PathInfo;

import java.io.IOException;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.sakaiproject.framework.Constants;
import org.sakaiproject.framework.ThreadLocalMapProvider;

import osid.OsidException;
import osid.OsidLoader;
import osid.OsidOwner;

import osid.authentication.AuthenticationManager;

import osid.authorization.AuthorizationManager;

import osid.coursemanagement.CourseManagementManager;

import osid.dr.DigitalRepositoryManager;

import osid.shared.Agent;
import osid.shared.Id;
import osid.shared.SharedManager;

/**
 * DOCUMENT ME!
 * 
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id: OsidManagerFactory.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
public class OsidManagerFactory
{
  private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
      .getLogger(OsidManagerFactory.class);
  protected static Properties SETTINGS;

  static
  {
    try
    {
      PathInfo pi = PathInfo.getInstance();
      SETTINGS = pi.getSettingsProperties("OsidImplBindings.properties");
    }
    catch (IOException e)
    {
      LOG.fatal(e.getMessage(), e);
      throw new Error(e);
    }
  }

  /**
   * Creates a new ManagerFactory object.
   * 
   * @throws java.lang.UnsupportedOperationException
   *           DOCUMENT ME!
   */
  private OsidManagerFactory()
  {
    throw new java.lang.UnsupportedOperationException(
        "Method not yet implemented.");
  }

  /**
   * DOCUMENT ME!
   * 
   * @param owner
   *          DOCUMENT ME!
   * 
   * @return DOCUMENT ME!
   * 
   * @throws osid.OsidException
   *           DOCUMENT ME!
   */
  public static SharedManager createSharedManager(OsidOwner owner)
      throws osid.OsidException
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("createSharedManager(OsidOwner " + owner + ")");
    }

    return (SharedManager) getOsidManager(OsidConstants.SHARED_MANAGER, owner);
  }

  /**
   * DOCUMENTATION PENDING
   * 
   * @return DOCUMENTATION PENDING
   * 
   * @throws osid.OsidException
   *           DOCUMENTATION PENDING
   */
  public static SharedManager createSharedManager() throws osid.OsidException
  {
    LOG.debug("createSharedManager()");

    return createSharedManager(getOsidOwner());
  }

  /**
   * DOCUMENTATION PENDING
   * 
   * @param owner
   *          DOCUMENTATION PENDING
   * 
   * @return DOCUMENTATION PENDING
   * 
   * @throws osid.OsidException
   *           DOCUMENTATION PENDING
   */
  public static CourseManagementManager createCourseManagementManager(
      OsidOwner owner) throws osid.OsidException
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("createCourseManagementManager(OsidOwner " + owner + ")");
    }

    return (CourseManagementManager) getOsidManager(
        OsidConstants.COURSEMANAGEMENT_MANAGER, owner);
  }

  /**
   * DOCUMENTATION PENDING
   * 
   * @return DOCUMENTATION PENDING
   * 
   * @throws osid.OsidException
   *           DOCUMENTATION PENDING
   */
  public static CourseManagementManager createCourseManagementManager()
      throws osid.OsidException
  {
    LOG.debug("createCourseManagementManager()");

    return createCourseManagementManager(getOsidOwner());
  }

  /**
   * DOCUMENTATION PENDING
   * 
   * @return DOCUMENTATION PENDING
   * 
   * @throws osid.OsidException
   *           DOCUMENTATION PENDING
   */
  public static AuthenticationManager createAuthenticationManager()
      throws osid.OsidException
  {
    LOG.debug("createCourseManagementManager()");

    return createAuthenticationManager(getOsidOwner());
  }

  /**
   * DOCUMENT ME!
   * 
   * @param owner
   *          DOCUMENT ME!
   * 
   * @return DOCUMENT ME!
   * 
   * @throws osid.OsidException
   *           DOCUMENT ME!
   */
  public static AuthenticationManager createAuthenticationManager(
      OsidOwner owner) throws osid.OsidException
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("createAuthenticationManager(OsidOwner " + owner + ")");
    }

    return (AuthenticationManager) getOsidManager(
        OsidConstants.AUTHENTICATION_MANAGER, owner);
  }

  /**
   * DOCUMENT ME!
   * 
   * @param owner
   *          DOCUMENT ME!
   * 
   * @return DOCUMENT ME!
   * 
   * @throws osid.OsidException
   *           DOCUMENT ME!
   */
  public static AuthorizationManager createAuthorizationManager(OsidOwner owner)
      throws osid.OsidException
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("createAuthorizationManager(OsidOwner " + owner + ")");
    }

    return (AuthorizationManager) getOsidManager(
        OsidConstants.AUTHORIZATION_MANAGER, owner);
  }

  /**
   * DOCUMENT ME!
   * 
   * @param owner
   *          DOCUMENT ME!
   * 
   * @return DOCUMENT ME!
   * 
   * @throws osid.OsidException
   *           DOCUMENT ME!
   */
  public static AuthorizationManager createAuthorizationManager()
      throws osid.OsidException
  {
    LOG.debug("createAuthorizationManager()");

    return (AuthorizationManager) getOsidManager(
        OsidConstants.AUTHORIZATION_MANAGER, getOsidOwner());
  }

  /**
   * DOCUMENT ME!
   * 
   * @param owner
   *          DOCUMENT ME!
   * 
   * @return DOCUMENT ME!
   * 
   * @throws osid.OsidException
   *           DOCUMENT ME!
   */
  public static DigitalRepositoryManager createDigitalRepositoryManager(
      OsidOwner owner) throws osid.OsidException
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("createDigitalRepositoryManager(OsidOwner " + owner + ")");
    }

    DigitalRepositoryManager result = (DigitalRepositoryManager) getOsidManager(
        OsidConstants.DR_MANAGER, owner);
    LOG.debug("manually calling updateOwner");
    result.updateOwner(owner);

    return result;
  }

  /**
   * DOCUMENTATION PENDING
   * 
   * @return DOCUMENTATION PENDING
   * 
   * @throws osid.OsidException
   *           DOCUMENTATION PENDING
   */
  public static DigitalRepositoryManager createDigitalRepositoryManager()
      throws osid.OsidException
  {
    LOG.debug("createSharedManager()");

    return createDigitalRepositoryManager(getOsidOwner());
  }

  /**
   * DOCUMENTATION PENDING
   * 
   * @return DOCUMENTATION PENDING
   */
  public static OsidOwner getOsidOwner()
  {
    LOG.debug("getOsidOwner()");
    OsidOwner owner = null;

    owner = (OsidOwner) ThreadLocalMapProvider.getMap().get(
        Constants.OSID_OWNER);

    if (owner == null)
    {
      LOG.debug("OsidOwner not found in ThreadLocalMapProvider");
      owner = new OsidOwner();

      /* Place the Owner into ThreadLocal Map. */
      ThreadLocalMapProvider.getMap().put(Constants.OSID_OWNER, owner);
    }

    return owner;
  }

  /**
   * DOCUMENTATION PENDING
   * 
   * @return DOCUMENTATION PENDING
   * 
   * @throws IllegalStateException
   *           DOCUMENTATION PENDING
   */
  public static Id getAgentId() throws IllegalStateException
  {
    LOG.debug("getAgentId()");

    try
    {
      return createAuthenticationManager(getOsidOwner()).getUserId(
          TypeLib.AUTHN_WEB_BASIC);
    }
    catch (OsidException e)
    {
      LOG.error(e);
      IllegalStateException error = new IllegalStateException(
          "Could not get Agent's Id!");
      error.initCause(e);
      throw error;
    }
  }

  /**
   * DOCUMENT ME!
   * 
   * @return DOCUMENT ME!
   *  
   */
  public static Agent getAgent() throws IllegalStateException
  {
    LOG.debug("getAgent()");
    Agent agent = null;
    try
    {
      // try to get cached Agent from HttpSession
      HttpServletRequest request = (HttpServletRequest) ThreadLocalMapProvider
          .getMap().get(Constants.HTTP_SERVLET_REQUEST);
      agent = (Agent) request.getSession().getAttribute(
          Constants.CURRENT_OSID_AGENT);
      if (agent == null)
      {
        LOG.debug("Agent not found in Session, going to SharedManager");
        agent = OsidManagerFactory.createSharedManager(getOsidOwner())
            .getAgent(getAgentId());
        // cache Agent in HttpSession
        request.getSession().setAttribute(Constants.CURRENT_OSID_AGENT, agent);
      }
    }
    catch (Exception e)
    {
      LOG.error(e.getMessage(), e);
      IllegalStateException error = new IllegalStateException(
          "Could not get Agent");
      error.initCause(e);
      throw error;
    }

    return agent;
  }

  /**
   * DOCUMENT ME!
   * 
   * @param interfaceName
   *          DOCUMENT ME!
   * @param owner
   *          DOCUMENT ME!
   * 
   * @return DOCUMENT ME!
   * 
   * @throws osid.OsidException
   *           DOCUMENT ME!
   */
  private static osid.OsidManager getOsidManager(String interfaceName,
      OsidOwner owner) throws osid.OsidException
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getOsidManager(String " + interfaceName + ", OsidOwner "
          + owner + ")");
    }

    if (owner == null)
    {
      LOG.error(OsidException.NULL_ARGUMENT, new IllegalArgumentException(
          OsidException.NULL_ARGUMENT));
      throw new osid.OsidException(OsidException.NULL_ARGUMENT);
    }

    HttpServletRequest request = (HttpServletRequest) ThreadLocalMapProvider
        .getMap().get(Constants.HTTP_SERVLET_REQUEST);

    if (request == null)
    {
      LOG.fatal("Didn't find Request in ThreadLocal");
      throw new IllegalStateException(
          "Didn't find HttpServletRequest in ThreadLocal");
    }

    // Should no longer be needed by the osid impl - LDS
    //    SerializableHttpServletRequest srequest =
    //      new SerializableHttpServletRequest(request);
    //    try
    //    {
    //      owner.addContext(Constants.HTTP_SERVLET_REQUEST, srequest);
    //    }
    //    catch(Exception e)
    //    {
    //      LOG.error(e.getMessage(), e);
    //      throw new IllegalStateException(e.getMessage());
    //    }

    org.sakaiproject.framework.ThreadLocalMapProvider.getMap().put(
        Constants.OSID_OWNER, owner);

    return OsidLoader.getManager(interfaceName, SETTINGS
        .getProperty(interfaceName), owner);
  }
}