/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/agent-api/src/java/org/sakaiproject/api/common/agent/Agent.java,v 1.2 2005/05/13 20:09:36 lance.indiana.edu Exp $
 *
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.api.common.agent;

import org.sakaiproject.api.common.manager.Persistable;
import org.sakaiproject.api.common.type.Typeable;

/**
 * Agent is a very abstract notion of what would be considered an "Actor". An
 * Agent could be a person, a machine, or some application for example. Another
 * way to think about Agent is that it would be analogous to a security 
 * principal. Agents are one of the fundamental pieces of authorization. They 
 * are one of the targets to which permissions are assigned.
 * 
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id$
 * @since Sakai 2.0
 */
public interface Agent extends Persistable, Typeable
{
  /**
   * The unique id generated by 
   * {@link org.sakaiproject.api.kernel.session.SessionManager}.
   * 
   * @return
   */
  public String getSessionManagerUserId();

  /**
   * @param sessionManagerUserId The unique id generated by
   *        {@link org.sakaiproject.api.kernel.session.SessionManager}.
   */
  public void setSessionManagerUserId(String sessionManagerUserId);

  /**
   * The local institution unique id (e.g. kerberos name, etc.)
   * 
   * @return
   */
  public String getEnterpriseId();

  /**
   * The local institution unique id (e.g. kerberos name, etc.)
   * 
   * @param enterpriseId
   */
  public void setEnterpriseId(String enterpriseId);

  /**
   * @return Returns the displayName.
   */
  public String getDisplayName();

  /**
   * @param displayName
   *          The displayName to set.
   */
  public void setDisplayName(String displayName);

  /**
   * All sorting of Agents should be performed on this representation of the
   * object.
   * 
   * @return
   */
  public String getSortName();

  /**
   * All sorting of Agents should be performed on this representation of the
   * object.
   * 
   * @param sortName
   */
  public void setSortName(String sortName);
}

/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/agent-api/src/java/org/sakaiproject/api/common/agent/Agent.java,v 1.2 2005/05/13 20:09:36 lance.indiana.edu Exp $
 *
 **********************************************************************************/