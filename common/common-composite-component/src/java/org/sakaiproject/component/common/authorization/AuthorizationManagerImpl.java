/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/common-composite-component/src/java/org/sakaiproject/component/common/authorization/AuthorizationManagerImpl.java,v 1.2 2005/05/13 17:22:17 lance.indiana.edu Exp $
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

package org.sakaiproject.component.common.authorization;

import java.beans.PropertyDescriptor;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.common.agent.Agent;
import org.sakaiproject.api.common.agent.AgentGroupManager;
import org.sakaiproject.api.common.authorization.Authorization;
import org.sakaiproject.api.common.authorization.AuthorizationManager;
import org.sakaiproject.api.common.authorization.Permissions;
import org.sakaiproject.api.common.authorization.PermissionsManager;
import org.sakaiproject.api.common.superstructure.SuperStructureManager;
import org.sakaiproject.api.common.uuid.UuidManager;
import org.sakaiproject.component.common.agent.AgentBean;
import org.sakaiproject.component.common.manager.PersistableHelper;
import org.sakaiproject.component.common.superstructure.NodeImpl;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

/**
 * @author <a href="mailto:jlannan.iupui.edu">Jarrod Lannan</a>
 * @version $Id$
 * 
 */
public class AuthorizationManagerImpl extends HibernateDaoSupport implements
    AuthorizationManager
{

  private static final Log LOG = LogFactory
      .getLog(AuthorizationManagerImpl.class);

  private UuidManager idManager; // dep inj
  private AgentGroupManager agentGroupManager; // dep inj
  private SuperStructureManager superStructureManager; // dep inj
  private PersistableHelper persistableHelper; // dep inj

  private static String agentAncestorsQuery;
  private static String nodeAncestorsQuery;

  private static final String HQL_SINGLE_QUOTE = "'";
  private static final String HQL_COMMA = ",";
  private static final String HQL_OPEN_PAREN = "(";
  private static final String HQL_CLOSE_PAREN = ")";
  private static final String HQL_AND = " and ";
  private static final String HQL_OR = " or ";
  private static final String HQL_SELECT_FROM = "select p from ";
  private static final String HQL_WHERE_P = "where p.uuid in ";
  private static final String HQL_AS_P = " as p ";
  private static final String HQL_AS_A = " as a, ";
  private static final String HQL_AS_N = " as n, ";
  private static final String HQL_AGENT_IN = " and a.agentUuid in (";
  private static final String HQL_NODE_IN = ") and a.nodeUuid in (";
  private static final String HQL_NODE_EQUALS = ") and n.uuid = a.nodeUuid";
  private static final String HQL_PERMS_AUTHZ_EQUALS = " and p.uuid = a.permissionsUuid";
  private static final String HQL_ORDER_BY = " order by n.depth desc";
  private static final String HQL_MEMBER = "member";
  private static final String HQL_CHILD = "child";

  public void init()
  {
    LOG.debug("init()");

    final HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        agentAncestorsQuery = session.getNamedQuery(
            "findIdsOfAncestorsForAgent").getQueryString();
        nodeAncestorsQuery = session.getNamedQuery("idJoinNodeAndMapForChild")
            .getQueryString();
        return null;
      }
    };
    getHibernateTemplate().execute(hcb);

    LOG.debug("init() completed successfully");
  }

  /**
   * @see org.sakaiproject.api.common.authorization.AuthorizationManager#createAuthorization(java.lang.String, java.lang.String, java.lang.String)
   */
  public Authorization createAuthorization(String agentUuid,
      String permissionsUuid, String nodeUuid)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("createAuthorization(String " + agentUuid + ", String "
          + permissionsUuid + ", String" + nodeUuid + ")");
    }

    return createAuthorization(agentUuid, permissionsUuid, nodeUuid, null, null);
  }

  /**
   * @see org.sakaiproject.api.common.authorization.AuthorizationManager#createAuthorization(java.lang.String, java.lang.String, java.lang.String, java.util.Date, java.util.Date)
   */
  public Authorization createAuthorization(String agentUuid,
      String permissionsUuid, String nodeUuid, Date effectiveDate,
      Date expirationDate)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("createAuthorization(String " + agentUuid + ", String "
          + permissionsUuid + ", String" + nodeUuid + ", Date" + effectiveDate
          + ", Date" + expirationDate + ")");
    }

    if (agentUuid == null)
      throw new IllegalArgumentException("Illegal agentUuid argument passed!");
    else
      if (permissionsUuid == null)
        throw new IllegalArgumentException(
            "Illegal permissionsUuid argument passed!");
      else
        if (nodeUuid == null)
          throw new IllegalArgumentException(
              "Illegal nodeUuid argument passed!");

    // construct a new Authorization         
    Authorization auth = new AuthorizationImpl(agentUuid, permissionsUuid,
        nodeUuid, effectiveDate, expirationDate);

    ((AuthorizationImpl) auth).setUuid(idManager.createUuid());
    persistableHelper.createPersistableFields(auth);
    saveAuthorization(auth);
    return auth;
  }

  /**
   * @see org.sakaiproject.api.common.authorization.AuthorizationManager#getAuthorization(java.lang.String)
   */
  public Authorization getAuthorization(String uuid)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getAuthorization(String " + uuid + ")");
    }

    return (AuthorizationImpl) getHibernateTemplate().get(
        AuthorizationImpl.class, uuid);
  }

  /**
   * @see org.sakaiproject.api.common.authorization.AuthorizationManager#saveAuthorization(org.sakaiproject.api.common.authorization.Authorization)
   */
  public void saveAuthorization(Authorization authorization)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("saveAuthorization(Authorization " + authorization + ")");
    }
    if (authorization == null)
      throw new IllegalArgumentException(
          "Illegal authorization argument passed!");

    getHibernateTemplate().saveOrUpdate(authorization);

  }

  /**
   * @see org.sakaiproject.api.common.authorization.AuthorizationManager#deleteAuthorization(org.sakaiproject.api.common.authorization.Authorization)
   */
  public void deleteAuthorization(Authorization authorization)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("deleteAuthorization(Authorization " + authorization + ")");
    }

    getHibernateTemplate().delete(authorization);
  }

  /**
   * @see org.sakaiproject.api.common.authorization.AuthorizationManager#isAuthorized(org.sakaiproject.api.common.authorization.PermissionsManager, org.sakaiproject.api.common.authorization.Permissions, java.lang.String)
   */
  public boolean isAuthorized(PermissionsManager permissionsManager,
      Permissions permissions, String nodeUuid)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("isAuthorized(PermissionsManager " + permissionsManager
          + ", Permissions " + permissions + ", String " + nodeUuid + ")");
    }

    Agent currentAgent = agentGroupManager.getAgent();
    return isAuthorized(currentAgent.getUuid(), permissionsManager,
        permissions, nodeUuid);

  }

  /**
   * @see org.sakaiproject.api.common.authorization.AuthorizationManager#isAuthorized(java.lang.String, org.sakaiproject.api.common.authorization.PermissionsManager, org.sakaiproject.api.common.authorization.Permissions, java.lang.String)
   */
  public boolean isAuthorized(final String agentUuid,
      final PermissionsManager permissionsManager,
      final Permissions permissions, final String nodeUuid)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("isAuthorized(String " + agentUuid + "PermissionsManager "
          + permissionsManager + ", Permissions " + permissions + ", String "
          + nodeUuid + ")");
    }
    if (agentUuid == null || agentUuid.length() < 1)
      throw new IllegalArgumentException("Illegal agentUuid argument passed!");
    if (permissionsManager == null)
      throw new IllegalArgumentException(
          "Illegal permissionsManager argument passed!");
    if (permissions == null)
      throw new IllegalArgumentException(
          "Illegal permissionsUuid argument passed!");
    if (nodeUuid == null || nodeUuid.length() < 1)
      throw new IllegalArgumentException("Illegal nodeUuid argument passed!");

    final HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        List listRelatedPermissions = permissionsManager
            .findAllRelatedPermissions(permissions);

        if (listRelatedPermissions == null || listRelatedPermissions.isEmpty())
        {
          if (LOG.isWarnEnabled())
          {
            LOG.warn("No related permissions found for permissions="
                + permissions);
          }
          return Boolean.FALSE;
        }

        // dynamic query
        final StringBuffer sb = new StringBuffer();
        sb.append(HQL_SELECT_FROM + AuthorizationImpl.class.getName()
            + HQL_AS_A);

        // in clause with related permission uuids
        final StringBuffer sbInClause = new StringBuffer(HQL_OPEN_PAREN);
        Iterator i = listRelatedPermissions.iterator();
        while (true)
        {
          String permUuid = (String) i.next();
          sbInClause.append(HQL_SINGLE_QUOTE + permUuid + HQL_SINGLE_QUOTE);
          if (i.hasNext())
          {
            sbInClause.append(HQL_COMMA);
          }
          else
          {
            sbInClause.append(HQL_CLOSE_PAREN);
            break;
          }
        }

        // date time handling        
        Timestamp now = new Timestamp(new Date().getTime());
        StringBuffer dateTimeHandling = new StringBuffer(HQL_AND);
        dateTimeHandling.append(HQL_SINGLE_QUOTE + now + HQL_SINGLE_QUOTE);
        dateTimeHandling.append(" >= a.effectiveDate" + HQL_AND);
        dateTimeHandling.append(HQL_OPEN_PAREN + "a.expirationDate IS NULL"
            + HQL_OR + HQL_SINGLE_QUOTE + now + HQL_SINGLE_QUOTE
            + " <= a.expirationDate" + HQL_CLOSE_PAREN);

        // build query
        sb.append(NodeImpl.class.getName() + HQL_AS_N);
        sb.append(permissionsManager.getBackingBeanClass().getName());
        sb.append(HQL_AS_P);
        sb.append(HQL_WHERE_P);
        sb.append(sbInClause);
        sb.append(HQL_AGENT_IN);
        sb.append(agentAncestorsQuery);
        sb.append(HQL_NODE_IN);
        sb.append(nodeAncestorsQuery);
        sb.append(HQL_NODE_EQUALS);
        sb.append(HQL_PERMS_AUTHZ_EQUALS);
        sb.append(dateTimeHandling);
        sb.append(HQL_ORDER_BY);

        final Query q = session.createQuery(sb.toString());

        //TODO these two calls must be optimized out of the impl
        AgentBean agentBean = (AgentBean) agentGroupManager
            .getAgentByUuid(agentUuid);
        NodeImpl nodeImpl = (NodeImpl) superStructureManager.getNode(nodeUuid);

        q.setLong(HQL_MEMBER, agentBean.getId().longValue());
        q.setLong(HQL_CHILD, nodeImpl.getId().longValue());

        if (LOG.isDebugEnabled())
        {
          LOG.debug("doInHibernate(session) HQL=" + q.getQueryString());
        }
        //XXX remove me!
        System.out.println("doInHibernate(session) HQL=" + q.getQueryString());

        final List authorizedPermissionsList = q.list();

        // find properties of queriedPermissions which are true
        final List significantPropertyNames = getSignificantPropertyNames(permissions);

        // find lowest tree-level permission        
        final Iterator iter = authorizedPermissionsList.iterator();
        if (iter.hasNext())
        {
          Permissions permissionsObject = (Permissions) iter.next();
          if (isDenySetOnPermissions(significantPropertyNames,
              permissionsObject))
          {
            return Boolean.FALSE;
          }
          return Boolean.TRUE;
        }
        else
        // no rows returned
        {
          return Boolean.FALSE;
        }
      }
    };

    return ((Boolean) getHibernateTemplate().execute(hcb)).booleanValue();
  }

  /**
   * 
   * @param searchPropertyNames the property names if interest
   * @param propertyObject the properties object being inspected
   * @return true if deny is set on any permissions, false otherwise
   */
  public boolean isDenySetOnPermissions(List searchPropertyNames,
      Object propertiesObject)
  {

    // find properties in propertyObject where deny is set
    for (Iterator nameIterator = searchPropertyNames.iterator(); nameIterator
        .hasNext();)
    {
      String propertyName = (String) nameIterator.next();
      Boolean propertyValue = null;
      try
      {
        propertyValue = (Boolean) PropertyUtils.getProperty(propertiesObject,
            propertyName);
        if (Boolean.FALSE.equals(propertyValue))
        {
          return true;
        }
      }
      catch (Exception e)
      {
        throw new Error("Could not access property: " + propertyName);
      }
    }
    return false;
  }

  /**
   * @param o the Permissions object
   * @return List of property names queried
   */
  public List getSignificantPropertyNames(Object o)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getQueriedFields(Object " + o + ")");
    }
    if (o == null)
      throw new IllegalArgumentException("Illegal Object o argument passed!");

    // loop through properties finding true values
    PropertyDescriptor[] arrPropDescriptors = PropertyUtils
        .getPropertyDescriptors(o);
    final List listReturn = new ArrayList();

    int length = arrPropDescriptors.length;
    for (int index = 0; index < length; index++)
    {
      try
      {
        String propertyName = arrPropDescriptors[index].getName();
        if (PropertyUtils.getPropertyType(o, propertyName)
            .equals(Boolean.class))
        {
          Boolean b = (Boolean) PropertyUtils.getProperty(o, propertyName);
          //TODO if(b!= null) add to List; must consider asking negative questions
          if (Boolean.TRUE.equals(b))
          {
            listReturn.add(propertyName);
          }
        }
      }
      catch (Exception e)
      {
        throw new Error("Property: " + arrPropDescriptors[index].getName());
      }
    }
    return listReturn;
  }

  /**
   * @param agentGroupManager The agentGroupManager to set.
   */
  public void setAgentGroupManager(AgentGroupManager agentGroupManager)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setAgentGroupManager(AgentGroupManager " + agentGroupManager
          + ")");
    }
    this.agentGroupManager = agentGroupManager;
  }

  /**
   * @param idManager The idManager to set.
   */
  public void setIdManager(UuidManager idManager)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setIdManager(UuidManager " + idManager + ")");
    }
    this.idManager = idManager;
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
   * @param persistableHelper The persistableHelper to set.
   */
  public void setPersistableHelper(PersistableHelper persistableHelper)
  {
    this.persistableHelper = persistableHelper;
  }

}

/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/common-composite-component/src/java/org/sakaiproject/component/common/authorization/AuthorizationManagerImpl.java,v 1.2 2005/05/13 17:22:17 lance.indiana.edu Exp $
 *
 **********************************************************************************/
