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

package org.sakaiproject.component.common.agent;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.common.agent.Agent;
import org.sakaiproject.api.common.agent.AgentGroupManager;
import org.sakaiproject.api.common.agent.Group;
import org.sakaiproject.api.common.superstructure.Node;
import org.sakaiproject.api.common.type.Type;
import org.sakaiproject.api.common.type.UnsupportedTypeException;
import org.sakaiproject.service.legacy.user.UserDirectoryService;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id$
 */
public class AgentGroupManagerUserDirectoryProxy implements AgentGroupManager
{
  private static final Log LOG = LogFactory
      .getLog(AgentGroupManagerUserDirectoryProxy.class);

  private UserDirectoryService userDirectoryService; // dep inj

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#createAgent(java.lang.String)
   */
  public Agent createAgent(String displayName)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("createAgent(String " + displayName + ")");
    }

    try
    {
      return new AgentUserProxy(userDirectoryService.addUser(displayName));
    }
    catch (Exception e)
    {
      LOG.error(e.getMessage(), e);
      throw new Error(e);
    }
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#createAgent(java.lang.String, java.lang.String)
   */
  public Agent createAgent(String enterpriseId, String displayName)
  {
    throw new UnsupportedOperationException(
        "Not implemented in proxy implementation");
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#createAgent(org.sakaiproject.api.common.superstructure.Node, java.lang.String, java.lang.String)
   */
  public Agent createAgent(Node parentNode, String enterpriseId,
      String displayName)
  {
    throw new UnsupportedOperationException(
        "Not implemented in proxy implementation");
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#createAgent(org.sakaiproject.api.common.superstructure.Node, java.lang.String, java.lang.String, java.lang.String, org.sakaiproject.api.common.type.Type)
   */
  public Agent createAgent(Node parentNode, String sessionManagerUserId,
      String enterpriseId, String displayName, Type agentType)
  {
    throw new UnsupportedOperationException(
        "Not implemented in proxy implementation");
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#createGroup(java.lang.String)
   */
  public Group createGroup(String displayName)
  {
    throw new UnsupportedOperationException(
        "Not implemented in proxy implementation");
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#createGroup(org.sakaiproject.api.common.superstructure.Node, java.lang.String)
   */
  public Group createGroup(Node parentNode, String displayName)
  {
    throw new UnsupportedOperationException(
        "Not implemented in proxy implementation");
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#createGroup(org.sakaiproject.api.common.superstructure.Node, java.lang.String, java.lang.String, org.sakaiproject.api.common.type.Type)
   */
  public Group createGroup(Node parentNode, String enterpriseId,
      String displayName, Type groupType)
  {
    throw new UnsupportedOperationException(
        "Not implemented in proxy implementation");
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#createGroup(java.lang.String, org.sakaiproject.api.common.type.Type)
   */
  public Group createGroup(String displayName, Type groupType)
  {
    throw new UnsupportedOperationException(
        "Not implemented in proxy implementation");
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#getAgent()
   * @see UserDirectoryService#getCurrentUser()
   */
  public Agent getAgent()
  {
    LOG.debug("getAgent()");

    return new AgentUserProxy(userDirectoryService.getCurrentUser());
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#getAgentByUuid(java.lang.String)
   */
  public Agent getAgentByUuid(String uuid)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getAgentByUuid(String " + uuid + ")");
    }
    if (uuid == null || uuid.length() < 1)
      throw new IllegalArgumentException("Illegal uuid argument passed!");

    try
    {
      return new AgentUserProxy(userDirectoryService.getUser(uuid));
    }
    catch (Exception e)
    {
      LOG.error(e.getMessage(), e);
      throw new Error(e);
    }
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#getAgentByEnterpriseId(java.lang.String)
   */
  public Agent getAgentByEnterpriseId(String enterpriseId)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getAgentByEnterpriseId(String " + enterpriseId + ")");
    }
    if (enterpriseId == null || enterpriseId.length() < 1)
      throw new IllegalArgumentException(
          "Illegal enterpriseId argument passed!");

    try
    {
      return new AgentUserProxy(userDirectoryService.getUser(enterpriseId));
    }
    catch (Exception e)
    {
      LOG.error(e.getMessage(), e);
      throw new Error(e);
    }
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#getAgentBySessionManagerUserId(java.lang.String)
   */
  public Agent getAgentBySessionManagerUserId(String sessionManagerUserId)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getAgentBySessionManagerUserId(String " + sessionManagerUserId
          + ")");
    }
    if (sessionManagerUserId == null || sessionManagerUserId.length() < 1)
      throw new IllegalArgumentException(
          "Illegal sessionManagerUserId argument passed!");

    try
    {
      return new AgentUserProxy(userDirectoryService
          .getUser(sessionManagerUserId));
    }
    catch(org.sakaiproject.exception.IdUnusedException e)
    {
      // The user may not exist in the directory; handle it gracefully
      LOG.info(e.getMessage());
      return null;
    }
    catch (Exception e)
    {
      LOG.error(e.getMessage(), e);
      throw new Error(e);
    }
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#getGroupByUuid(java.lang.String)
   */
  public Group getGroupByUuid(String uuid)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getGroupByUuid(String " + uuid + ")");
    }
    if (uuid == null || uuid.length() < 1)
      throw new IllegalArgumentException("Illegal uuid argument passed!");

    try
    {
      return (Group) new AgentUserProxy(userDirectoryService.getUser(uuid));
    }
    catch (Exception e)
    {
      LOG.error(e.getMessage(), e);
      throw new Error(e);
    }
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#getGroupByEnterpriseId(java.lang.String)
   */
  public Group getGroupByEnterpriseId(String enterpriseId)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getGroupByEnterpriseId(String " + enterpriseId + ")");
    }
    if (enterpriseId == null || enterpriseId.length() < 1)
      throw new IllegalArgumentException(
          "Illegal enterpriseId argument passed!");

    try
    {
      return (Group) new AgentUserProxy(userDirectoryService
          .getUser(enterpriseId));
    }
    catch (Exception e)
    {
      LOG.error(e.getMessage(), e);
      throw new Error(e);
    }
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#getAnonymousGroup()
   */
  public Group getAnonymousGroup()
  {
    LOG.trace("getAnonymousGroup()");

    return (Group) new AgentUserProxy(userDirectoryService.getAnonymousUser());
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#getAuthenticatedUsers()
   */
  public Group getAuthenticatedUsers()
  {
    LOG.trace("getAuthenticatedUsers()");

    throw new UnsupportedOperationException("Method not implemented");
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#findTransitiveDescendents(org.sakaiproject.api.common.agent.Group)
   */
  public List findTransitiveDescendents(Group group)
  {
    throw new UnsupportedOperationException("Method not implemented");
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#findTransitiveDescendents(org.sakaiproject.api.common.agent.Group, boolean)
   */
  public List findTransitiveDescendents(Group group, boolean includeThisAgent)
  {
    throw new UnsupportedOperationException("Method not implemented");
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#save(org.sakaiproject.api.common.agent.Agent)
   */
  public void save(Agent agentOrGroup)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("save(Agent " + agentOrGroup + ")");
    }

    throw new UnsupportedOperationException("Method not implemented");
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#delete(org.sakaiproject.api.common.agent.Agent)
   */
  public void delete(Agent agentOrGroup)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("delete(Agent " + agentOrGroup + ")");
    }

    throw new UnsupportedOperationException("Method not implemented");
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#delete(java.lang.String)
   */
  public void delete(String uuid)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("delete(String " + uuid + ")");
    }

    throw new UnsupportedOperationException("Method not implemented");
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#getDefaultAgentType()
   */
  public Type getDefaultAgentType()
  {
    LOG.trace("getDefaultAgentType()");

    throw new UnsupportedOperationException("Method not implemented");
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#getDefaultGroupType()
   */
  public Type getDefaultGroupType()
  {
    LOG.trace("getDefaultGroupType()");

    throw new UnsupportedOperationException("Method not implemented");
  }

  /**
   * @see org.sakaiproject.api.common.superstructure.DefaultContainer#getDefaultContainer()
   */
  public Node getDefaultContainer() throws UnsupportedOperationException
  {
    LOG.trace("getDefaultContainer()");

    throw new UnsupportedOperationException("Method not implemented");
  }

  /**
   * @see org.sakaiproject.api.common.type.UuidTypeResolvable#getObject(java.lang.String, org.sakaiproject.api.common.type.Type)
   */
  public Object getObject(String uuid, Type type)
      throws UnsupportedTypeException
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getObject(String " + uuid + ")");
    }
    if (uuid == null || uuid.length() < 1)
      throw new IllegalArgumentException("Illegal uuid argument passed!");

    try
    {
      return new AgentUserProxy(userDirectoryService.getUser(uuid));
    }
    catch (Exception e)
    {
      LOG.error(e.getMessage(), e);
      throw new Error(e);
    }
  }

  /**
   * Dependency injection.
   * @param userDirectoryService The userDirectoryService to set.
   */
  public void setUserDirectoryService(UserDirectoryService userDirectoryService)
  {
    this.userDirectoryService = userDirectoryService;
  }

}



