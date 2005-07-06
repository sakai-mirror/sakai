/*
 * The Open Source Portfolio Initiative Software is Licensed under the Educational Community License Version 1.0:
 *
 * This Educational Community License (the "License") applies to any original work of authorship
 * (the "Original Work") whose owner (the "Licensor") has placed the following notice immediately
 * following the copyright notice for the Original Work:
 *
 * Copyright (c) 2004 Trustees of Indiana University and r-smart Corporation
 *
 * This Original Work, including software, source code, documents, or other related items, is being
 * provided by the copyright holder(s) subject to the terms of the Educational Community License.
 * By obtaining, using and/or copying this Original Work, you agree that you have read, understand,
 * and will comply with the following terms and conditions of the Educational Community License:
 *
 * Permission to use, copy, modify, merge, publish, distribute, and sublicense this Original Work and
 * its documentation, with or without modification, for any purpose, and without fee or royalty to the
 * copyright holder(s) is hereby granted, provided that you include the following on ALL copies of the
 * Original Work or portions thereof, including modifications or derivatives, that you make:
 *
 * - The full text of the Educational Community License in a location viewable to users of the
 * redistributed or derivative work.
 *
 * - Any pre-existing intellectual property disclaimers, notices, or terms and conditions.
 *
 * - Notice of any changes or modifications to the Original Work, including the date the changes were made.
 *
 * - Any modifications of the Original Work must be distributed in such a manner as to avoid any confusion
 *  with the Original Work of the copyright holders.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) may NOT be used in advertising or publicity pertaining
 * to the Original or Derivative Works without specific, written prior permission. Title to copyright
 * in the Original Work and any associated documentation will at all times remain with the copyright holders.
 *
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/mgt/impl/security/SakaiSlideSessionManager.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/29 18:36:41 $
 */
package org.sakaiproject.metaobj.repository.mgt.impl.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.slide.simple.authentication.SessionAuthenticationManager;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.utils.BeanFactory;

public class SakaiSlideSessionManager implements SessionAuthenticationManager {
   protected final transient Log logger = LogFactory.getLog(getClass());

   /**
    * Closes an authentication session that is no longer used by Slide. It is
    * guaranteed that no further requests will be done to this session. The
    * only exception is when the same object is returned again when
    * authenticating.
    *
    * @param session the authentication session to be closed
    * @throws Exception in case anything went wrong
    */
   public void closeAuthenticationSession(Object session) throws Exception {
      UsageSessionService.closeSession();
   }

   /**
    * Tries to authenticate a user to the custom store and return an object
    * that will allow for access to the custom store. No password is supplied
    * as this is a trusted user who has proven its identity to JAAS
    * authentication before.
    *
    * @param user the name of the user who is requesting an authenticated
    *             session
    * @return an object representing the sesssion or connection or whatever is
    *         necessary for acccess to your custom store
    * @throws Exception in case authentication to your custom store failed or
    *                   anything else went wrong
    */
   public Object getAuthenticationSession(String user) throws Exception {
      UsageSessionService.startSession(user, "unknown", "webdav");

      return getAuthenticationManager().getAgent();
   }

   /**
    * Tries to authenticate a user to the custom store and return an object
    * that will allow for access to the custom store. Slide does not care at
    * all what the nature of the object is.
    *
    * @param user     the name of the user who is requesting an authenticated
    *                 session
    * @param password the password of the user who is requesting an authenticated
    *                 session
    * @return an object representing the sesssion or connection or whatever is
    *         necessary for acccess to your custom store
    * @throws Exception in case authentication to your custom store failed or
    *                   anything else went wrong
    */
   public Object getAuthenticationSession(String user, String password) throws Exception {
      if (UserDirectoryService.authenticate(user, password) == null) {
         throw new Exception("failed authentication");
      };

      return getAuthenticationSession(user);
   }

   public AuthenticationManager getAuthenticationManager() {
      return (AuthenticationManager)BeanFactory.getInstance().getBean(
         "authManager", AuthenticationManager.class);
   }

}
