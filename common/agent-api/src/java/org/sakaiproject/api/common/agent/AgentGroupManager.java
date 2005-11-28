/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
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

import java.util.List;

import org.sakaiproject.api.common.superstructure.DefaultContainer;
import org.sakaiproject.api.common.superstructure.Node;
import org.sakaiproject.api.common.type.Type;
import org.sakaiproject.api.common.type.UuidTypeResolvable;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id$
 * @since Sakai 2.0
 */
public interface AgentGroupManager extends DefaultContainer, UuidTypeResolvable
{
  /* ---------------------- Create ----------------------------------------- */

  /**
   * Creates an Agent in the default Agent OU, with a default Agent Type.
   * 
   * @param displayName
   * @return
   */
  public Agent createAgent(String displayName);

  /**
   * Creates an Agent in the default Agent OU, with a default Agent Type.
   * 
   * @param enterpriseId Enterprise username
   * @param displayName
   * @return
   */
  public Agent createAgent(String enterpriseId, String displayName);

  /**
   * Creates an Agent in the OU specified by parentNode, with a default Agent 
   * Type.
   * 
   * @param parentNode The OU that will contain new Agent.
   * @param enterpriseId Enterprise username
   * @param displayName
   * @return
   */
  public Agent createAgent(Node parentNode, String enterpriseId,
      String displayName);

  /**
   * Creates an Agent with a Type specified by agentType, contained by the OU specified by parentNode.
   * 
   * @param parentNode Container OU for new Agent.
   * @param sessionManagerUserId The unique id from 
   *        {@link org.sakaiproject.api.kernel.session.Session#getUserId()}
   * @param enterpriseId
   * @param displayName Text that will presented in the UI
   * @param agentType Caller specified Agent Type.
   * @return
   */
  public Agent createAgent(Node parentNode, String sessionManagerUserId,
      String enterpriseId, String displayName, Type agentType);

  /**
   * Create a Group in the default OU with the default Group Type.
   * 
   * @param displayName
   * @return
   */
  public Group createGroup(String displayName);

  /**
   * Create a Group in the container specified by parentNode. Group is created
   * with default Group Type.
   * 
   * @param parentNode The newly created Group's parent.
   * @param displayName
   * @return
   */
  public Group createGroup(Node parentNode, String displayName);

  /**
   * Provides the caller with full control over the location in the 
   * SuperStructure as well as the Group Type.
   * 
   * @param parentNode The newly created Group's parent.
   * @param enterpriseId
   * @param displayName
   * @param groupType
   * @return
   */
  public Group createGroup(Node parentNode, String enterpriseId,
      String displayName, Type groupType);

  /**
   * Creates a new Group in the default OU container but gives caller control
   * of Group Type.
   * 
   * @param displayName
   * @param groupType
   * @return
   */
  public Group createGroup(String displayName, Type groupType);

  /* --------------------------- Read -------------------------------------- */

  /**
   * Convenience method to get the currently logged in Agent
   * 
   * @return The currently logged in Agent
   */
  public Agent getAgent();

  /**
   * Polymorphic retrieve will include both Agent and Group objects.
   * 
   * @param uuid
   * @return null if not found.
   */
  public Agent getAgentByUuid(String uuid);

  /**
   * Polymorphic retrieve will include both Agent and Group objects.
   * 
   * @param enterpriseId
   * @return null if not found.
   */
  public Agent getAgentByEnterpriseId(String enterpriseId);

  /**
   * Retrieve an Agent by the unique id generated by 
   * {@link org.sakaiproject.api.kernel.session.SessionManager}.
   * 
   * @see org.sakaiproject.api.kernel.session.Session#getUserId()
   * 
   * @param sessionManagerUserId
   * @return
   */
  public Agent getAgentBySessionManagerUserId(String sessionManagerUserId);

  /**
   * Retrieve only objects of type Group.
   * 
   * @param uuid
   * @return
   */
  public Group getGroupByUuid(String uuid);

  /**
   * Retrieve only objects of type Group.
   * 
   * @param enterpriseId
   * @return
   */
  public Group getGroupByEnterpriseId(String enterpriseId);

  /**
   * Returns a reference to a special Group. The "Anonymous" Group virtually
   * contains all known Agents; i.e. "Everyone" is a virtual member of this
   * Group.
   * 
   * @return
   */
  public Group getAnonymousGroup();

  /**
   * Returns a reference to a special Group. The "Authenticated Users" Group 
   * virtually contains all Agents which are authenticated or identifiable.
   * 
   * @return
   */
  public Group getAuthenticatedUsers();

  /**
   * Find all transitive children for a given Group.  The passed Group will not
   * be included in the results by default.
   * 
   * @param group
   * @return List of Agents
   */
  public List findTransitiveDescendents(Group group);

  /**
   * Find all transitive children for a given Group.
   * 
   * @param group
   * @param includeThisAgent TRUE == include passed Group in returned results.
   *                          FALSE == do not include passed Group in results.
   * @return List of Agents
   */
  public List findTransitiveDescendents(Group group, boolean includeThisAgent);

  /* ---------------------------- Upate ------------------------------------- */

  /**
   * Persist an Agent or Group.
   */
  public void save(Agent agentOrGroup);

  /* ---------------------------- Delete ------------------------------------ */

  /**
   * Delete the Agent.
   * 
   * @param agentOrGroup
   */
  public void delete(Agent agentOrGroup);

  /**
   * Delete the Agent by UUID.
   * 
   * @param uuid
   */
  public void delete(String uuid);

  /* --------------------- Bootstrap sort of stuff -------------------------- */

  /**
   * The default Type for an Agent
   * 
   * @return
   */
  public Type getDefaultAgentType();

  /**
   * The default Type for a Group
   * 
   * @return
   */
  public Type getDefaultGroupType();

}



