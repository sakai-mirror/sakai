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

/*
 * Copyright 2004 by Leland Stanford Junior University Board of Trustees.
 * The code contained herein is subject to license terms available at
 * http://aboutcoursework.stanford.edu/opensource/terms.html
 */
package org.sakai.osid.impl.authn;

import org.navigoproject.osid.OsidManagerFactory;
import org.navigoproject.osid.shared.impl.TypeIteratorImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import osid.OsidOwner;

import osid.shared.Id;
import osid.shared.SharedManager;

/**
 * OKI Authentication Manager implementation
 * @author Ganapathy Subramaniam
 * @author Qingru Zhang
 * @version
 */
public class AuthenticationManager
  implements osid.authentication.AuthenticationManager
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(AuthenticationManager.class);
  private OsidOwner owner = null;
  private Object credentials;
  private static Random rn = new Random();

  /**
   * Creates a new AuthenticationManager object.
   */
  public AuthenticationManager()
  {
  }

  /**
   * Creates a new AuthenticationManager object.
   *
   * @param credentials DOCUMENTATION PENDING
   */
  public AuthenticationManager(Object credentials)
  {
    this.credentials = credentials;
  }

  /**
   * the method to generate random number
   */
  public static int rand(int lo, int hi)
  {
    int n = hi - lo + 1;
    int i = rn.nextInt() % n;
    if(i < 0)
    {
      i = -i;
    }

    return lo + i;
  }

  /**
   * the method to generate random string
   */
  public static String randomstring(int lo, int hi)
  {
    int n = rand(lo, hi);
    byte[] b = new byte[n];
    for(int i = 0; i < n; i++)
    {
      b[i] = (byte) rand('a', 'z');
    }

    return new String(b, 0);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public static String randomstring()
  {
    return randomstring(8, 10);
  }

  /**
   * verify that the Osid Version used is 1.0
   */
  public void osidVersion_1_0()
    throws osid.OsidException
  {
    // --TO DO
  }

  /**
     Return owner of this OsidManager.
     @return osid.OsidOwner Owner
   */
  public osid.OsidOwner getOwner()
    throws osid.OsidException
  {
    return owner;
  }

  /**
     Update the owner of this OsidManager.
     @param osid.OsidOwner owner
   */
  public void updateOwner(osid.OsidOwner owner)
    throws osid.OsidException
  {
    LOG.debug("AUTHN: Setting owner");
    this.owner = owner;
  }

  /**
     Update the configuration of this OsidManager.
     @param java.util.Map configuration
   */
  public void updateConfiguration(java.util.Map configuration)
    throws osid.OsidException
  {
  }

  /**
   * Returns the types of Authentication that are available
   * to the current implementation.
   *
   * @return osid.shared.TypeIterator
   * @throws osid.authentication.AuthenticationException
   */
  public osid.shared.TypeIterator getAuthenticationTypes()
    throws osid.authentication.AuthenticationException
  {
    Collection typeColl = new ArrayList();
    typeColl.add(new AUTHN_WEB_BASICType());
    typeColl.add(new AUTHN_ANONYMOUSType());

    return new TypeIteratorImpl(typeColl.iterator());
  }

  /**
   * Return boolean true if the owner Agent is authenticated for the
   * specified authentication type.
   * @param agent
   * @param authenType
   * @return boolean true if the agent is authenticated under the authenType.
   * @throws osid.authentication.AuthenticationException
   */
  public boolean isUserAuthenticated(osid.shared.Type authenType)
    throws osid.authentication.AuthenticationException
  {
    try
    {
      authenticateUser(authenType);

      return true;
    }
    catch(Exception e)
    {
      return false;
    }
  }

  /**
   * @param type
   * @throws osid.authentication.AuthenticationException
   */
  public void authenticateUser(osid.shared.Type type)
    throws osid.authentication.AuthenticationException
  {
    if(type.isEqual(new AUTHN_WEB_BASICType()))
    {
      AUTHN_WEB_BASICUser();
    }
    else if(type.isEqual(new AUTHN_ANONYMOUSType()))
    {
      AUTHN_ANONYMOUSUser();
    }
  }

  /**
   * getUserId - Returns the user Id representing the authenticated user
   * @param Type
   * @throws osid.authentication.AuthenticationException
   */
  public osid.shared.Id getUserId(osid.shared.Type authenticationType)
    throws osid.authentication.AuthenticationException
  {
    LOG.debug("Type: " + authenticationType + ", owner = " + owner);

    try
    {
      credentials = owner.getContext("javax.servlet.http.HttpServletRequest");
      HttpServletRequest req = (HttpServletRequest) credentials;
      String authID = null;

      /* debug: print out all of headers and their values   */
      Enumeration headerNames = req.getHeaderNames();
      while(headerNames.hasMoreElements())
      {
        String headerName = (String) headerNames.nextElement();
        LOG.debug(
          " Header:" + headerName + ", value:" + req.getHeader(headerName));
      }

      if(authenticationType.isEqual(new AUTHN_ANONYMOUSType()))
      {
        HttpSession session = req.getSession(true);
        authID = (String) session.getAttribute("authID");
        if(authID == null)
        {
          authID = randomstring();
        }
      }
      else
      {
        if((req != null) && (req.getRemoteUser() != null))
        {
          authID = req.getRemoteUser().toString();
        }

        if((authID == null) || (authID.length() == 0))
        {
          //throw new AuthenticationException("Webauth failed to authenticate user. No Credentials found.");
          authID = "guest";
        }
      }

      SharedManager sm = OsidManagerFactory.createSharedManager(owner);
      LOG.debug("authID: " + authID);
      Id id = sm.getId(authID);
      LOG.debug("authID Id: " + id);

      return id;
    }
    catch(Exception e)
    {
      LOG.error(e);

      return null;
    }

    //}
    //else { 
    //  throw new AuthenticationException("Unknown Authentication Type.");
    //}
  }

  /**
   * Destroy all authentications for the agent.
   * @throws osid.authentication.AuthenticationException
   */
  public void destroyAuthentication()
    throws osid.authentication.AuthenticationException
  {
    // TODO
  }

  /**
   * Destroy agent's authentication of specified type.
   * @param type
   * @throws osid.authentication.AuthenticationException
   */
  public void destroyAuthenticationForType(osid.shared.Type type)
    throws osid.authentication.AuthenticationException
  {
    // TODO
  }

  /**
   * Authenticates user using basic web server authentication
   * @throws osid.authentication.AuthenticationException
   */
  public void AUTHN_WEB_BASICUser()
    throws osid.authentication.AuthenticationException
  {
    HttpServletRequest req = (HttpServletRequest) credentials;
    String authID = req.getRemoteUser().toString();
    if((authID == null) || (authID.length() == 0))
    {
      //throw new AuthenticationException("AUTHN_WEB_BASIC failed to authenticate user. No Credentials found.");
      authID = "guest";
    }
  }

  /**
   * Authenticates user using basic Anonymous  authentication
   * @throws osid.authentication.AuthenticationException
   */
  public void AUTHN_ANONYMOUSUser()
    throws osid.authentication.AuthenticationException
  {
    HttpServletRequest req = (HttpServletRequest) credentials;
    String authID = randomstring();
    HttpSession session = req.getSession(true);
    session.setAttribute("authID", authID);
  }
}
