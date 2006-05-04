/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
 *                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
 *
 * Licensed under the Educational Community License Version 1.0 (the "License");
 * By obtaining, using and/or copying this Original Work, you agree that you have read,
 * understand, and will comply with the terms and conditions of the Educational Community License.
 * You may obtain a copy of the License at:
 *
 *      http://cvs.sakaiproject.org/licenses/license_1_0.html
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 **********************************************************************************/
package org.sakaiproject.metaobj.shared.model;

import java.io.Serializable;
import java.security.Principal;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 8, 2004
 * Time: 5:21:55 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Agent extends Serializable, Principal {
   public static final String AGENT_SESSION_KEY = "osp_agent";

   public static final String ROLE_MEMEBER = "ROLE_MEMBER";
   public static final String ROLE_ADMIN = "ROLE_ADMIN";
   public static final String ROLE_REVIEWER = "ROLE_REVIEWER";
   public static final String ROLE_GUEST = "ROLE_GUEST";
   public static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";

   public Id getId();

   public Artifact getProfile();

   public Object getProperty(String key);

   public String getDisplayName();

   public boolean isInRole(String role);

   public boolean isInitialized();

   public String getRole();

   public List getWorksiteRoles(String worksiteId);

   public List getWorksiteRoles();

   public boolean isRole();

}
