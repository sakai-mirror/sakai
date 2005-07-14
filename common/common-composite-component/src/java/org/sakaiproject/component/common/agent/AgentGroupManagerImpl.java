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

import java.sql.SQLException;
import java.util.List;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.common.agent.Agent;
import org.sakaiproject.api.common.agent.AgentGroupManager;
import org.sakaiproject.api.common.agent.Group;
import org.sakaiproject.api.common.manager.LifeCycleAware;
import org.sakaiproject.api.common.superstructure.Node;
import org.sakaiproject.api.common.superstructure.SuperStructureManager;
import org.sakaiproject.api.common.type.Type;
import org.sakaiproject.api.common.type.TypeManager;
import org.sakaiproject.api.common.type.UnsupportedTypeException;
import org.sakaiproject.api.common.uuid.UuidManager;
import org.sakaiproject.api.kernel.session.SessionManager;
import org.sakaiproject.component.common.manager.PersistableHelper;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id$
 */
public class AgentGroupManagerImpl extends HibernateDaoSupport implements
    AgentGroupManager, LifeCycleAware
{
  private static final Log LOG = LogFactory.getLog(AgentGroupManagerImpl.class);

  private static final String REFERENCENAME = "referenceName";
  private static final String UUID = "uuid";
  private static final String DISPLAY_NAME = "displayName";
  private static final String MEMBER = "member";
  private static final String ENTERPRISEID = "enterpriseId";
  private static final String SESSION_MANAGER_USER_ID = "sessionManagerUserId";
  private static final String QUERY_GETAGENTBYUUID = "getAgentByUuid";
  private static final String QUERY_FIND_DESCENDENTS_FOR_GROUP = "findDescendentsForGroup";
  private static final String QUERY_GETGROUPBYUUID = "getGroupByUuid";
  private static final String QUERY_GET_AGENT_BY_ENTERPRISE_ID = "getAgentByEnterpriseId";
  private static final String QUERY_GET_GROUP_BY_ENTERPRISE_ID = "getGroupByEnterpriseId";
  private static final String QUERY_GET_AGENT_BY_SESSION_MGR_USER_ID = "getAgentBySessionManagerUserId";

  private boolean cacheGetTransitiveDescendents = true;
  private boolean cacheGetAgentByUuid = true;
  private boolean cacheGetAgentByDisplayName = true;
  private boolean cacheGetAgentByReferenceName = true;
  private boolean cacheGetGroupByUuid = true;
  private boolean cacheGetAgentByEnterpriseId = true;
  private boolean cacheGetGroupByEnterpriseId = true;
  private boolean cacheGetAgentBySessionManagerUserId = true;

  private boolean initialized = false;
  private Type defaultAgentType;
  private Type defaultGroupType;
  private Type virtualMemberGroupType;
  private Group anonymousGroup;
  private Group authenticatedUsersGroup;
  private Node defaultAgentContainer;

  // injected dependencies
  private UuidManager uuidManager; // dep inj
  private TypeManager typeManager; // dep inj
  private SuperStructureManager superStructureManager; // dep inj
  private SessionManager sessionManager; // dep inj
  private PersistableHelper persistableHelper; // dep inj

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#createAgent(java.lang.String)
   */
  public Agent createAgent(String displayName)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("createAgent(String " + displayName + ")");
    }
    ; // no validation is required here as it is delegated

    return createAgent(getDefaultContainer(), null, displayName);
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#createAgent(java.lang.String, java.lang.String)
   */
  public Agent createAgent(String enterpriseId, String displayName)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("createAgent(String " + enterpriseId + ", String "
          + displayName + ")");
    }
    ; // no validation is required here as it is delegated

    return createAgent(getDefaultContainer(), enterpriseId, displayName);
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#createAgent(org.sakaiproject.api.common.superstructure.Node, java.lang.String, java.lang.String)
   */
  public Agent createAgent(Node parentNode, String enterpriseId,
      String displayName)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("createAgent(Node " + parentNode + ", String " + enterpriseId
          + ", String " + displayName + ")");
    }
    ; // no validation is required here as it is delegated

    return createAgent(parentNode, null, enterpriseId, displayName,
        getDefaultAgentType());
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#createAgent(org.sakaiproject.api.common.superstructure.Node, java.lang.String, java.lang.String, java.lang.String, org.sakaiproject.api.common.type.Type)
   */
  public Agent createAgent(Node parentNode, String sessionManagerUserId,
      String enterpriseId, String displayName, Type agentType)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("createAgent(Node " + parentNode + ", String "
          + sessionManagerUserId + ", String " + displayName + ", Type "
          + agentType + ")");
    }
    if (parentNode == null)
      throw new IllegalArgumentException("Illegal parentNode argument passed!");
    if (enterpriseId == null || enterpriseId.length() < 1)
      throw new IllegalArgumentException(
          "Illegal enterpriseId argument passed!");
    if (displayName == null || displayName.length() < 1)
      throw new IllegalArgumentException("Illegal displayName argument passed!");
    if (agentType == null)
      throw new IllegalArgumentException("Illegal agentType argument passed!");

    String agentUuid = uuidManager.createUuid();

    // create a Node for this new Agent
    Node agentNode = superStructureManager.createNode(agentUuid, parentNode,
        agentType, displayName, null);
    agentNode.setParent(parentNode);
    superStructureManager.saveNode(agentNode);

    // create new Agent
    AgentBean ab = new AgentBean();
    ab.setUuid(agentUuid);
    ab.setSessionManagerUserId(sessionManagerUserId);
    ab.setEnterpriseId(enterpriseId);
    ab.setNode(agentNode);
    ab.setDisplayName(displayName);
    ab.setType(agentType);

    persistableHelper.createPersistableFields(ab);

    getHibernateTemplate().save(ab);
    return ab;
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#createGroup(java.lang.String)
   */
  public Group createGroup(String displayName)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("createGroup(String " + displayName + ")");
    }
    ; // no validation is required here as it is delegated

    return createGroup(getDefaultContainer(), displayName);
  }

  public Group createGroup(String displayName, Type groupType)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("createGroup(String " + displayName + ", Type " + groupType
          + ")");
    }
    if (displayName == null || displayName.length() < 1)
      throw new IllegalArgumentException("Illegal displayName argument passed!");
    if (groupType == null)
      throw new IllegalArgumentException("Illegal groupType argument passed!");

    LOG
        .debug("return createGroup(getDefaultContainer(), uuidManager.createUuid(), displayName, groupType);");
    return createGroup(getDefaultContainer(), uuidManager.createUuid(),
        displayName, groupType);
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#createGroup(org.sakaiproject.api.common.superstructure.Node, java.lang.String)
   */
  public Group createGroup(Node parentNode, String displayName)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("createGroup(Node " + parentNode + ", String " + displayName
          + ")");
    }
    ; // no validation is required here as it is delegated

    return createGroup(parentNode, uuidManager.createUuid(), displayName,
        getDefaultGroupType());
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#createGroup(org.sakaiproject.api.common.superstructure.Node, java.lang.String, org.sakaiproject.api.common.type.Type)
   */
  public Group createGroup(Node parentNode, String enterpriseId,
      String displayName, Type groupType)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("createGroup(parentNode, displayName, groupType)");
    }
    ; // no validation is required here as it is delegated

    return createGroup(parentNode, enterpriseId, uuidManager.createUuid(),
        displayName, groupType);
  }

  private Group createGroup(Node parentNode, String enterpriseId,
      String groupUuid, String displayName, Type groupType)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("createGroup(Node " + parentNode + ", String " + groupUuid
          + ", String " + displayName + ", Type " + groupType + ")");
    }
    if (parentNode == null)
      throw new IllegalArgumentException("Illegal parentNode argument passed!");
    if (groupUuid == null || groupUuid.length() < 1)
      throw new IllegalArgumentException("Illegal groupUuid argument passed!");
    if (displayName == null || displayName.length() < 1)
      throw new IllegalArgumentException("Illegal displayName argument passed!");
    if (groupType == null)
      throw new IllegalArgumentException("Illegal groupType argument passed!");

    if (enterpriseId == null || enterpriseId.length() < 1)
      enterpriseId = uuidManager.createUuid();

    // create Node for this new Group
    Node groupNode = superStructureManager.createNode(groupUuid, parentNode,
        groupType, displayName, null);
    superStructureManager.saveNode(groupNode);

    // create new Group
    GroupBean gb = new GroupBean();
    gb.setUuid(groupUuid);
    gb.setNode(groupNode);
    gb.setEnterpriseId(enterpriseId);
    gb.setDisplayName(displayName);
    gb.setType(groupType);

    persistableHelper.createPersistableFields(gb);

    getHibernateTemplate().save(gb);
    return gb;
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#getAgent()
   */
  public Agent getAgent()
  {
    LOG.debug("getAgent()");

    final org.sakaiproject.api.kernel.session.Session session = sessionManager
        .getCurrentSession();
    if (session != null)
    {
      final String userUuid = session.getUserId();
      final String uid = session.getUserEid();
      if (LOG.isDebugEnabled())
      {
        LOG.debug("userUuid=" + userUuid + ", uid=" + uid);
      }

      if (userUuid != null && userUuid.length() > 0)
      {
        Agent agent = this.getAgentBySessionManagerUserId(userUuid);
        if (agent != null)
        {
          return agent;
        }
        else
        {
          LOG
              .debug("// no persistent data exists for this Agent; create new Agent");
          //TODO Where to get user's description? Can this be improved?
          String description = "//generated";

          return this.createAgent(this.getDefaultContainer(), userUuid, uid,
              description, this.getDefaultAgentType());
        }
      }
      else
      {
        LOG.debug("// uid is not available; return Anonymous Agent (Group)");
        return getAnonymousGroup();
      }
    }
    else
    {
      LOG.debug("// Session is not available; return Anonymous Agent (Group)");
      return getAnonymousGroup();
    }
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#getAgentByUuid(java.lang.String)
   */
  public Agent getAgentByUuid(final String uuid)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getAgentByUuid(String " + uuid + ")");
    }
    if (uuid == null || uuid.length() < 1)
      throw new IllegalArgumentException("Illegal uuid argument passed!");

    final HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        Query q = session.getNamedQuery(QUERY_GETAGENTBYUUID);
        q.setString(UUID, uuid);
        q.setCacheable(cacheGetAgentByUuid);
        return q.uniqueResult();
      }
    };
    return (Agent) getHibernateTemplate().execute(hcb);
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#getAgentByEnterpriseId(java.lang.String)
   */
  public Agent getAgentByEnterpriseId(final String enterpriseId)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getAgentByEnterpriseId(String " + enterpriseId + ")");
    }
    if (enterpriseId == null || enterpriseId.length() < 1)
      throw new IllegalArgumentException(
          "Illegal enterpriseId argument passed!");

    final HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        Query q = session.getNamedQuery(QUERY_GET_AGENT_BY_ENTERPRISE_ID);
        q.setString(ENTERPRISEID, enterpriseId);
        q.setCacheable(cacheGetAgentByEnterpriseId);
        return q.uniqueResult();
      }
    };

    LOG
        .debug("getAgentByEnterpriseId(String): return (Agent) getHibernateTemplate().execute(hcb);");
    return (Agent) getHibernateTemplate().execute(hcb);
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#getAgentBySessionManagerUserId(java.lang.String)
   */
  public Agent getAgentBySessionManagerUserId(final String sessionManagerUserId)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getAgentBySessionManagerUserId(String " + sessionManagerUserId
          + ")");
    }
    if (sessionManagerUserId == null || sessionManagerUserId.length() < 1)
      throw new IllegalArgumentException(
          "Illegal sessionManagerUserId argument passed!");

    final HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        Query q = session.getNamedQuery(QUERY_GET_AGENT_BY_SESSION_MGR_USER_ID);
        q.setString(SESSION_MANAGER_USER_ID, sessionManagerUserId);
        q.setCacheable(cacheGetAgentBySessionManagerUserId);
        return q.uniqueResult();
      }
    };

    LOG.debug("return (Agent) getHibernateTemplate().execute(hcb);");
    return (Agent) getHibernateTemplate().execute(hcb);
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#getGroupByUuid(java.lang.String)
   */
  public Group getGroupByUuid(final String uuid)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getGroupByUuid(String " + uuid + ")");
    }
    if (uuid == null || uuid.length() < 1)
      throw new IllegalArgumentException("Illegal uuid argument passed!");

    final HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        Query q = session.getNamedQuery(QUERY_GETGROUPBYUUID);
        q.setString(UUID, uuid);
        q.setCacheable(cacheGetGroupByUuid);
        return q.uniqueResult();
      }
    };
    return (Group) getHibernateTemplate().execute(hcb);
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#getGroupByEnterpriseId(java.lang.String)
   */
  public Group getGroupByEnterpriseId(final String enterpriseId)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getGroupByEnterpriseId(String " + enterpriseId + ")");
    }
    if (enterpriseId == null || enterpriseId.length() < 1)
      throw new IllegalArgumentException(
          "Illegal enterpriseId argument passed!");

    final HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        Query q = session.getNamedQuery(QUERY_GET_GROUP_BY_ENTERPRISE_ID);
        q.setString(ENTERPRISEID, enterpriseId);
        q.setCacheable(cacheGetGroupByEnterpriseId);
        return q.uniqueResult();
      }
    };
    return (Group) getHibernateTemplate().execute(hcb);
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#getAnonymousGroup()
   */
  public Group getAnonymousGroup()
  {
    LOG.debug("getAnonymousGroup()");

    if (!initialized) init();

    return anonymousGroup;
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#getAuthenticatedUsers()
   */
  public Group getAuthenticatedUsers()
  {
    LOG.debug("getAuthenticatedUsers()");

    if (!initialized) init();

    return authenticatedUsersGroup;
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#findTransitiveDescendents(org.sakaiproject.api.common.agent.Group)
   */
  public List findTransitiveDescendents(Group group)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("findTransitiveDescendents(Group " + group + ")");
    }
    ; // validation is delegated

    return findTransitiveDescendents(group, false);
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#findTransitiveDescendents(org.sakaiproject.api.common.agent.Group, boolean)
   */
  public List findTransitiveDescendents(final Group group,
      boolean includeThisAgent)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("findTransitiveDescendents(Group " + group + ", boolean "
          + includeThisAgent + ")");
    }
    if (group == null)
      throw new IllegalArgumentException("Illegal group argument passed!");

    if (group instanceof GroupBean)
    { // well known impl
      final GroupBean gb = (GroupBean) group;
      final HibernateCallback hcb = new HibernateCallback()
      {
        public Object doInHibernate(Session session) throws HibernateException,
            SQLException
        {
          Query q = session.getNamedQuery(QUERY_FIND_DESCENDENTS_FOR_GROUP);
          q.setParameter(MEMBER, gb.getId(), Hibernate.LONG);
          q.setCacheable(cacheGetTransitiveDescendents);
          return q.list();
        }
      };
      List l = getHibernateTemplate().executeFind(hcb);
      if (!includeThisAgent)
      {
        l.remove(gb);
      }
      return l;
    }
    else
    {
      throw new UnsupportedOperationException(
          "Only well known implementations are currenlty supported.");
    }
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
    if (agentOrGroup == null)
      throw new IllegalArgumentException(
          "Illegal agentOrGroup argument passed!");

    getHibernateTemplate().saveOrUpdate(agentOrGroup);
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
    if (agentOrGroup == null)
      throw new IllegalArgumentException(
          "Illegal agentOrGroup argument passed!");

    if (agentOrGroup instanceof AgentBean)
    {
      final AgentBean ab = (AgentBean) agentOrGroup;
      getHibernateTemplate().delete(ab);
      return;
    }
    if (agentOrGroup instanceof GroupBean)
    {
      final GroupBean gb = (GroupBean) agentOrGroup;
      getHibernateTemplate().delete(gb);
      return;
    }
    throw new UnsupportedOperationException(
        "Agent is not a well known implementation");
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#delete(java.lang.String)
   */
  public void delete(final String uuid)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("delete(String " + uuid + ")");
    }
    if (uuid == null || uuid.length() < 1)
      throw new IllegalArgumentException("Illegal uuid argument!");

    final HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        return new Integer(
            session
                .delete(
                    "from org.sakaiproject.component.common.agent.AgentBean as agent where agent.uuid = ?",
                    uuid, Hibernate.STRING));
      }
    };

    LOG.debug("return;");
    return;
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#getDefaultAgentType()
   */
  public Type getDefaultAgentType()
  {
    LOG.debug("getDefaultAgentType()");

    if (!initialized) init();

    return defaultAgentType;
  }

  /**
   * @see org.sakaiproject.api.common.agent.AgentGroupManager#getDefaultGroupType()
   */
  public Type getDefaultGroupType()
  {
    LOG.debug("getDefaultGroupType()");

    if (!initialized) init();

    return defaultGroupType;
  }

  public Type getVirtualMemberGroupType()
  {
    LOG.debug("getVirtualMemberGroupType()");

    if (!initialized) init();

    return virtualMemberGroupType;
  }

  /**
   * @see org.sakaiproject.api.common.superstructure.DefaultContainer#getDefaultContainer()
   */
  public Node getDefaultContainer()
  {
    LOG.debug("getDefaultContainer()");

    if (!initialized) init();

    return defaultAgentContainer;
  }

  /**
   * @see org.sakaiproject.api.common.manager.UuidTypeResolvable#getObject(java.lang.String, org.sakaiproject.api.common.type.Type)
   */
  public Object getObject(String uuid, Type type)
      throws UnsupportedTypeException
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getObject(String " + uuid + ", Type " + type + ")");
    }
    if (uuid == null || uuid.length() < 1)
      throw new IllegalArgumentException("Illegal uuid argument passed!");
    if (type == null)
      throw new IllegalArgumentException("Illegal type argument passed!");

    if (this.getDefaultAgentType().equals(type))
    {
      return this.getAgentByUuid(uuid);
    }
    if (this.getDefaultGroupType().equals(type)
        || this.getVirtualMemberGroupType().equals(type))
    {
      return this.getGroupByUuid(uuid);
    }
    throw new UnsupportedTypeException();
  }

  /**
   * @param cacheGetAgentByDisplayName The cacheGetAgentByDisplayName to set.
   */
  public void setCacheGetAgentByDisplayName(boolean cacheGetAgentByDisplayName)
  {
    if (LOG.isInfoEnabled())
    {
      LOG.info("setCacheGetAgentByDisplayName(boolean "
          + cacheGetAgentByDisplayName + ")");
    }

    this.cacheGetAgentByDisplayName = cacheGetAgentByDisplayName;
  }

  /**
   * @param cacheGetAgentByReferenceName The cacheGetAgentByReferenceName to set.
   */
  public void setCacheGetAgentByReferenceName(
      boolean cacheGetAgentByReferenceName)
  {
    if (LOG.isInfoEnabled())
    {
      LOG.info("setCacheGetAgentByReferenceName(boolean "
          + cacheGetAgentByReferenceName + ")");
    }

    this.cacheGetAgentByReferenceName = cacheGetAgentByReferenceName;
  }

  /**
   * @param cacheGetAgentByUuid The cacheGetAgentByUuid to set.
   */
  public void setCacheGetAgentByUuid(boolean cacheGetAgentByUuid)
  {
    if (LOG.isInfoEnabled())
    {
      LOG.info("setCacheGetAgentByUuid(boolean " + cacheGetAgentByUuid + ")");
    }

    this.cacheGetAgentByUuid = cacheGetAgentByUuid;
  }

  /**
   * @param cacheGetGroupByUuid The cacheGetGroupByUuid to set.
   */
  public void setCacheGetGroupByUuid(boolean cacheGetGroupByUuid)
  {
    if (LOG.isInfoEnabled())
    {
      LOG.info("setCacheGetGroupByUuid(boolean " + cacheGetGroupByUuid + ")");
    }

    this.cacheGetGroupByUuid = cacheGetGroupByUuid;
  }

  /**
   * @param cacheGetTransitiveAncestors The cacheGetTransitiveAncestors to set.
   */
  public void setCacheGetTransitiveDescendents(
      boolean cacheGetTransitiveAncestors)
  {
    if (LOG.isInfoEnabled())
    {
      LOG.info("setCacheGetTransitiveDescendents(boolean "
          + cacheGetTransitiveAncestors + ")");
    }

    this.cacheGetTransitiveDescendents = cacheGetTransitiveAncestors;
  }

  /**
   * @param cacheGetAgentByEnterpriseId The cacheGetAgentByEnterpriseId to set.
   */
  public void setCacheGetAgentByEnterpriseId(boolean cacheGetAgentByEnterpriseId)
  {
    if (LOG.isInfoEnabled())
    {
      LOG.info("setCacheGetAgentByEnterpriseId(boolean "
          + cacheGetAgentByEnterpriseId + ")");
    }

    this.cacheGetAgentByEnterpriseId = cacheGetAgentByEnterpriseId;
  }

  /**
   * @param superStructureManager The superStructureManager to set.
   */
  public void setSuperStructureManager(
      SuperStructureManager superStructureManager)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setSuperStructureManager(SuperStructureManager "
          + superStructureManager + ")");
    }

    this.superStructureManager = superStructureManager;
  }

  /**
   * @param typeManager The typeManager to set.
   */
  public void setTypeManager(TypeManager typeManager)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setTypeManager(TypeManager " + typeManager + ")");
    }

    this.typeManager = typeManager;
  }

  /**
   * @param uuidManager The uuidManager to set.
   */
  public void setUuidManager(UuidManager uuidManager)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setUuidManager(UuidManager " + uuidManager + ")");
    }

    this.uuidManager = uuidManager;
  }

  /**
   * @see {@link AgentGroupManager#isInitialized()}
   * @return Returns the initialized.
   */
  public boolean isInitialized()
  {
    LOG.trace("isInitialized()");
    if (LOG.isDebugEnabled())
    {
      LOG.debug("AgentGroupManager.isInitialized()=" + initialized);
    }

    return initialized;
  }

  private void init()
  {
    LOG.info("init()");

    LOG.info("// init defaultAgentType");
    final String[] defaultAgentTypePrimitives = { "org.sakaiproject",
        "api.common.agent", "Agent.type.default", "Agent",
        "Default Agent Type", };
    defaultAgentType = typeManager.getType(defaultAgentTypePrimitives[0],
        defaultAgentTypePrimitives[1], defaultAgentTypePrimitives[2]);
    if (defaultAgentType == null)
    {
      LOG.info("seed initial data set with new defaultAgentType data...");
      defaultAgentType = typeManager.createType(defaultAgentTypePrimitives[0],
          defaultAgentTypePrimitives[1], defaultAgentTypePrimitives[2],
          defaultAgentTypePrimitives[3], defaultAgentTypePrimitives[4]);
    }
    if (defaultAgentType == null)
    {
      LOG.fatal("defaultAgentType could not be initialized!");
      throw new IllegalStateException("defaultAgentType == null");
    }

    LOG.info("// init defaultGroupType");
    final String[] defaultGroupTypePrimitives = { "org.sakaiproject",
        "api.common.agent", "Group.type.default", "Group",
        "Default Group Type", };
    defaultGroupType = typeManager.getType(defaultGroupTypePrimitives[0],
        defaultGroupTypePrimitives[1], defaultGroupTypePrimitives[2]);
    if (defaultGroupType == null)
    {
      LOG.info("seed initial data set with new defaultGroupType data...");
      defaultGroupType = typeManager.createType(defaultGroupTypePrimitives[0],
          defaultGroupTypePrimitives[1], defaultGroupTypePrimitives[2],
          defaultGroupTypePrimitives[3], defaultGroupTypePrimitives[4]);
    }
    if (defaultGroupType == null)
    {
      LOG.fatal("defaultGroupType could not be initialized!");
      throw new IllegalStateException("defaultGroupType == null");
    }

    LOG.info("// init virtualMemberGroupType");
    final String[] virtualMemberGroupTypePrimitives = { "org.sakaiproject",
        "api.common.agent", "Group.type.virtualMember", "Virtual Member Group",
        "Virtual Member Group Type", };
    virtualMemberGroupType = typeManager.getType(
        virtualMemberGroupTypePrimitives[0],
        virtualMemberGroupTypePrimitives[1],
        virtualMemberGroupTypePrimitives[2]);
    if (virtualMemberGroupType == null)
    {
      LOG.info("seed initial data set with new virtualMemberGroupType data...");
      virtualMemberGroupType = typeManager.createType(
          virtualMemberGroupTypePrimitives[0],
          virtualMemberGroupTypePrimitives[1],
          virtualMemberGroupTypePrimitives[2],
          virtualMemberGroupTypePrimitives[3],
          virtualMemberGroupTypePrimitives[4]);
    }
    if (virtualMemberGroupType == null)
    {
      LOG.fatal("virtualMemberGroupType could not be initialized!");
      throw new IllegalStateException("virtualMemberGroupType == null");
    }

    LOG.info("// init defaultAgentContainer");
    final String[] defaultAgentContainerPrimitives = {
        "529c6b60-8fc9-4456-00cd-1656406a7fb5", "Users", "Users and Groups", };
    defaultAgentContainer = superStructureManager
        .getReferencedNode(defaultAgentContainerPrimitives[0]);
    if (defaultAgentContainer == null)
    {
      LOG.info("seed initial data set with new defaultAgentContainer data...");
      defaultAgentContainer = superStructureManager.createNode(
          defaultAgentContainerPrimitives[0], superStructureManager
              .getRootNode(), superStructureManager
              .getOrganizationalUnitNodeType(),
          defaultAgentContainerPrimitives[1],
          defaultAgentContainerPrimitives[2]);
    }
    if (defaultAgentContainer == null)
    {
      LOG.fatal("defaultAgentContainer could not be initialized!");
      throw new IllegalStateException("defaultAgentContainer == null");
    }

    LOG.info("// init anonymousGroup");
    final String[] anonymousGroupPrimitives = {
        "2a17ca9d-f8a8-4fc3-80a3-a45d4c1c49e3", "Anonymous", };
    anonymousGroup = getGroupByUuid(anonymousGroupPrimitives[0]);
    if (anonymousGroup == null)
    {
      LOG.info("seed initial data set with new anonymousGroup data...");
      anonymousGroup = createGroup(defaultAgentContainer, "Anonymous",
          anonymousGroupPrimitives[0], anonymousGroupPrimitives[1],
          virtualMemberGroupType);
    }
    if (anonymousGroup == null)
    {
      LOG.fatal("anonymousGroup could not be initialized!");
      throw new IllegalStateException("anonymousGroup == null");
    }
    if (!(anonymousGroup.getUuid().equals(anonymousGroupPrimitives[0])))
    {
      LOG.fatal("anonymousGroup could not be initialized!");
      throw new IllegalStateException("anonymousGroup.uuid != well known UUID");
    }

    LOG.info("// init authenticatedUsersGroup");
    final String[] authenticatedUsersGroupPrimitives = {
        "55f993f2-9345-47aa-802b-bd6f106a1e25", "Authenticated Users", };
    authenticatedUsersGroup = getGroupByUuid(authenticatedUsersGroupPrimitives[0]);
    if (authenticatedUsersGroup == null)
    {
      LOG
          .info("seed initial data set with new authenticatedUsersGroup data...");
      authenticatedUsersGroup = createGroup(defaultAgentContainer,
          "Authenticated Users", authenticatedUsersGroupPrimitives[0],
          authenticatedUsersGroupPrimitives[1], virtualMemberGroupType);
    }
    if (authenticatedUsersGroup == null)
    {
      LOG.fatal("authenticatedUsersGroup could not be initialized!");
      throw new IllegalStateException("authenticatedUsersGroup == null");
    }
    if (!(authenticatedUsersGroup.getUuid()
        .equals(authenticatedUsersGroupPrimitives[0])))
    {
      LOG.fatal("authenticatedUsersGroup could not be initialized!");
      throw new IllegalStateException(
          "authenticatedUsersGroup.uuid != well known UUID");
    }

    this.initialized = true;
    LOG.info("Initialization completed successfully");
    LOG.debug("return;");
  }

  /**
   * Dependency injection.
   * @param sessionManager The sessionManager to set.
   */
  public void setSessionManager(SessionManager sessionManager)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setSessionManager(SessionManager " + sessionManager + ")");
    }
    if (sessionManager == null)
      throw new IllegalArgumentException(
          "Illegal sessionManager argument passed!");

    this.sessionManager = sessionManager;
  }

  /**
   * @param persistableHelper The persistableHelper to set.
   */
  public void setPersistableHelper(PersistableHelper persistableHelper)
  {
    this.persistableHelper = persistableHelper;
  }

  /**
   * @param cacheGetAgentBySessionManagerUserId The cacheGetAgentBySessionManagerUserId to set.
   */
  public void setCacheGetAgentBySessionManagerUserId(
      boolean cacheGetAgentBySessionManagerUserId)
  {
    this.cacheGetAgentBySessionManagerUserId = cacheGetAgentBySessionManagerUserId;
  }

}



