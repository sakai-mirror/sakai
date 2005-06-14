/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/common-composite-component/src/java/org/sakaiproject/component/common/authorization/DefaultPermissionsManagerImpl.java,v 1.2 2005/05/13 17:22:17 lance.indiana.edu Exp $
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

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.hibernate.Criteria;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import net.sf.hibernate.expression.Expression;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.common.agent.AgentGroupManager;
import org.sakaiproject.api.common.authorization.DefaultPermissions;
import org.sakaiproject.api.common.authorization.Permissions;
import org.sakaiproject.api.common.authorization.PermissionsMask;
import org.sakaiproject.api.common.superstructure.Node;
import org.sakaiproject.api.common.uuid.UuidManager;
import org.sakaiproject.component.common.manager.PersistableHelper;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @author <a href="mailto:jlannan.iupui.edu">Jarrod Lannan</a>
 * @version $Id$
 */
public class DefaultPermissionsManagerImpl extends HibernateDaoSupport
    implements org.sakaiproject.api.common.authorization.PermissionsManager
{
  private static final Log LOG = LogFactory
      .getLog(DefaultPermissionsManagerImpl.class);

  public static final String UUID = "uuid";
  public static final String FIND_PERMISSIONS_BY_UUID = "findPermissionsByUuid";

  // findAllRelatedPermissions constants
  private static final int OPTIMAL_STRING_BUFFER_SIZE = 133;
  private static final String HQL_SELECT_FROM_AS_P = "select p.uuid from org.sakaiproject.component.common.authorization.DefaultPermissionsImpl as p where ";
  private static final String HQL_AND = "and ";
  private static final String HQL_P_AUDIT_IS_NOT_NULL = "p.audit is not null ";
  private static final String HQL_P_CREATE_IS_NOT_NULL = "p.create is not null ";
  private static final String HQL_P_CREATE_COLLECTION_IS_NOT_NULL = "p.createCollection is not null ";
  private static final String HQL_P_DELETE_IS_NOT_NULL = "p.delete is not null ";
  private static final String HQL_P_DELETE_COLLECTION_IS_NOT_NULL = "p.deleteCollection is not null ";
  private static final String HQL_P_EXECUTE_IS_NOT_NULL = "p.execute is not null ";
  private static final String HQL_P_MANAGE_PERMISSIONS_IS_NOT_NULL = "p.managePermissions is not null ";
  private static final String HQL_P_READ_IS_NOT_NULL = "p.read is not null ";
  private static final String HQL_P_READ_EXTENDED_METADATA_IS_NOT_NULL = "p.readExtendedMetadata is not null ";
  private static final String HQL_P_READ_METADATA_IS_NOT_NULL = "p.readMetadata is not null ";
  private static final String HQL_P_READ_PERMISSIONS_IS_NOT_NULL = "p.readPermissions is not null ";
  private static final String HQL_P_TAKE_OWNERSHIP_IS_NOT_NULL = "p.takeOwnership is not null ";
  private static final String HQL_P_WRITE_IS_NOT_NULL = "p.write is not null ";
  private static final String HQL_P_WRITE_EXTENDED_METADATA_IS_NOT_NULL = "p.writeExtendedMetadata is not null ";
  private static final String HQL_P_WRITE_METADATA_IS_NOT_NULL = "p.writeMetadata is not null ";

  private UuidManager idManager; // dep inj
  private AgentGroupManager agentGroupManager; // dep inj
  private PersistableHelper persistableHelper; // dep inj

  private boolean cacheGetPermissionsByUuid = true;
  private boolean cacheFindByPermissionsMask = true;
  private boolean cacheFindAllRelatedPermissionsUuid = true;

  /**
   * 
   * @see org.sakaiproject.api.common.authorization.PermissionsManager#createPermissions(java.lang.String, java.lang.String, org.sakaiproject.api.common.authorization.PermissionsMask)
   */
  public Permissions createPermissions(String name, String description,
      PermissionsMask permissionsMask)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("createPermissions(String " + name + ", String " + description
          + ", PermissionsMask " + permissionsMask + ")");
    }
    if (name == null)
      throw new IllegalArgumentException("Illegal name argument passed!");
    if (permissionsMask == null)
      throw new IllegalArgumentException(
          "Illegal permissionsMask argument passed!");

    DefaultPermissionsImpl p = new DefaultPermissionsImpl();
    persistableHelper.createPersistableFields(p);
    p.setName(name);
    p.setDescription(description);
    p.setUuid(idManager.createUuid());
    for (Iterator iter = permissionsMask.entrySet().iterator(); iter.hasNext();)
    {
      Map.Entry entry = (Map.Entry) iter.next();
      try
      {
        PropertyUtils.setProperty(p, (String) entry.getKey(), entry.getValue());
      }
      catch (Exception e)
      {
        throw new Error(e);
      }
    }

    getHibernateTemplate().save(p);
    return p;
  }

  /**
   * 
   * @see org.sakaiproject.api.common.authorization.PermissionsManager#getPermissions(java.util.Map)
   */
  public Permissions getPermissions(final PermissionsMask permissionsMask)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getPermissions(Map " + permissionsMask + ")");
    }
    if (permissionsMask == null || permissionsMask.size() < 1)
      throw new IllegalArgumentException(
          "Illegal permissionsMask argument passed!");

    // first let's try to find a row like this in the database...
    final HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        Criteria c = session.createCriteria(DefaultPermissionsImpl.class);
        for (Iterator iter = permissionsMask.entrySet().iterator(); iter
            .hasNext();)
        {
          Map.Entry entry = (Map.Entry) iter.next();
          c.add(Expression.eq((String) entry.getKey(), entry.getValue()));
        }
        c.setCacheable(cacheFindByPermissionsMask);
        return c.uniqueResult();
      }
    };
    DefaultPermissionsImpl pi = (DefaultPermissionsImpl) getHibernateTemplate()
        .execute(hcb);

    // did we find any results?
    if (pi == null)
    {
      // no row exists like this; create a new one
      StringBuffer sb = new StringBuffer();
      for (Iterator iter = permissionsMask.entrySet().iterator(); iter
          .hasNext();)
      {
        Map.Entry entry = (Map.Entry) iter.next();
        sb.append(entry.getKey());
        sb.append("=");
        sb.append(entry.getValue());
      }
      Permissions newPerms = this.createPermissions(sb.toString(),
          "//generated", permissionsMask);
      return newPerms;
    }
    else
    {
      // we found the row that matches the mask; return it
      return pi;
    }
  }

  /**
   * @see org.sakaiproject.api.common.authorization.PermissionsManager#getPermissions(java.lang.String)
   */
  public Permissions getPermissions(final String permissionsUuid)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getPermissions(String " + permissionsUuid + ")");
    }
    if (permissionsUuid == null || permissionsUuid.length() < 1)
      throw new IllegalArgumentException(
          "Illegal permissionsUuid argument passed!");

    final HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        Query q = session.getNamedQuery(FIND_PERMISSIONS_BY_UUID);
        q.setString(UUID, permissionsUuid);
        q.setCacheable(cacheGetPermissionsByUuid);
        return q.uniqueResult();
      }
    };
    return (Permissions) getHibernateTemplate().execute(hcb);
  }

  /**
   * @see org.sakaiproject.api.common.authorization.PermissionsManager#savePermissions(org.sakaiproject.api.common.authorization.Permissions)
   */
  public void savePermissions(Permissions permissions)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("savePermissions(Permissions " + permissions + ")");
    }
    if (permissions == null)
      throw new IllegalArgumentException("Illegal permissions argument passed!");

    getHibernateTemplate().saveOrUpdate(permissions);
  }

  /**
   * @see org.sakaiproject.api.common.authorization.PermissionsManager#getBackingBeanClass()
   */
  public Class getBackingBeanClass()
  {
    LOG.debug("getBackingBeanClass()");

    return DefaultPermissions.class;
  }

  /**
   * @see org.sakaiproject.api.common.authorization.PermissionsManager#findAllRelatedPermissions(org.sakaiproject.api.common.authorization.Permissions)
   */
  public List findAllRelatedPermissions(Permissions permissions)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("findAllRelatedPermissions(Permissions " + permissions + ")");
    }
    if (permissions == null)
      throw new IllegalArgumentException("Illegal permissions argument passed!");

    final DefaultPermissionsImpl p = (DefaultPermissionsImpl) permissions;

    final HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        // build HQL query
        boolean previousWhereClause = false;
        final StringBuffer sb = new StringBuffer(OPTIMAL_STRING_BUFFER_SIZE);
        sb.append(HQL_SELECT_FROM_AS_P);
        if (p.getAudit() != null)
        {
          sb.append(HQL_P_AUDIT_IS_NOT_NULL);
          previousWhereClause = true;
        }
        if (p.getCreate() != null)
        {
          if (previousWhereClause) sb.append(HQL_AND);
          sb.append(HQL_P_CREATE_IS_NOT_NULL);
          previousWhereClause = true;
        }
        if (p.getCreateCollection() != null)
        {
          if (previousWhereClause) sb.append(HQL_AND);
          sb.append(HQL_P_CREATE_COLLECTION_IS_NOT_NULL);
          previousWhereClause = true;
        }
        if (p.getDelete() != null)
        {
          if (previousWhereClause) sb.append(HQL_AND);
          sb.append(HQL_P_DELETE_IS_NOT_NULL);
          previousWhereClause = true;
        }
        if (p.getDeleteCollection() != null)
        {
          if (previousWhereClause) sb.append(HQL_AND);
          sb.append(HQL_P_DELETE_COLLECTION_IS_NOT_NULL);
          previousWhereClause = true;
        }
        if (p.getExecute() != null)
        {
          if (previousWhereClause) sb.append(HQL_AND);
          sb.append(HQL_P_EXECUTE_IS_NOT_NULL);
          previousWhereClause = true;
        }
        if (p.getManagePermissions() != null)
        {
          if (previousWhereClause) sb.append(HQL_AND);
          sb.append(HQL_P_MANAGE_PERMISSIONS_IS_NOT_NULL);
          previousWhereClause = true;
        }
        if (p.getRead() != null)
        {
          if (previousWhereClause) sb.append(HQL_AND);
          sb.append(HQL_P_READ_IS_NOT_NULL);
          previousWhereClause = true;
        }
        if (p.getReadExtendedMetadata() != null)
        {
          if (previousWhereClause) sb.append(HQL_AND);
          sb.append(HQL_P_READ_EXTENDED_METADATA_IS_NOT_NULL);
          previousWhereClause = true;
        }
        if (p.getReadMetadata() != null)
        {
          if (previousWhereClause) sb.append(HQL_AND);
          sb.append(HQL_P_READ_METADATA_IS_NOT_NULL);
          previousWhereClause = true;
        }
        if (p.getReadPermissions() != null)
        {
          if (previousWhereClause) sb.append(HQL_AND);
          sb.append(HQL_P_READ_PERMISSIONS_IS_NOT_NULL);
          previousWhereClause = true;
        }
        if (p.getTakeOwnership() != null)
        {
          if (previousWhereClause) sb.append(HQL_AND);
          sb.append(HQL_P_TAKE_OWNERSHIP_IS_NOT_NULL);
          previousWhereClause = true;
        }
        if (p.getWrite() != null)
        {
          if (previousWhereClause) sb.append(HQL_AND);
          sb.append(HQL_P_WRITE_IS_NOT_NULL);
          previousWhereClause = true;
        }
        if (p.getWriteExtendedMetadata() != null)
        {
          if (previousWhereClause) sb.append(HQL_AND);
          sb.append(HQL_P_WRITE_EXTENDED_METADATA_IS_NOT_NULL);
          previousWhereClause = true;
        }
        if (p.getWriteMetadata() != null)
        {
          if (previousWhereClause) sb.append(HQL_AND);
          sb.append(HQL_P_WRITE_METADATA_IS_NOT_NULL);
          // not necessary; last property:          previousWhereClause = true;
        }

        if (LOG.isDebugEnabled())
        {
          LOG.debug("HQL=" + sb.toString());
        }
        Query q = session.createQuery(sb.toString());
        q.setCacheable(cacheFindAllRelatedPermissionsUuid);
        return q.list();
      }
    };

    List l = getHibernateTemplate().executeFind(hcb);
    return l;
  }

  /**
   * @see org.sakaiproject.api.common.superstructure.DefaultContainer#getDefaultContainer()
   */
  public Node getDefaultContainer() throws UnsupportedOperationException
  {
    LOG.debug("getDefaultContainer()");

    throw new UnsupportedOperationException("Method not implemented");
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
   * @param cacheFindByPermissionsMask The cacheFindByPermissionsMask to set.
   */
  public void setCacheFindByPermissionsMask(boolean cacheFindByPermissionsMask)
  {
    if (LOG.isInfoEnabled())
    {
      LOG.info("setCacheFindByPermissionsMask(boolean "
          + cacheFindByPermissionsMask + ")");
    }

    this.cacheFindByPermissionsMask = cacheFindByPermissionsMask;
  }

  /**
   * @param cacheFindAllRelatedPermissionsUuid The cacheFindAllRelatedPermissionsUuid to set.
   */
  public void setCacheFindAllRelatedPermissionsUuid(
      boolean cacheFindAllRelatedPermissionsUuid)
  {
    this.cacheFindAllRelatedPermissionsUuid = cacheFindAllRelatedPermissionsUuid;
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
 * $Header: /cvs/sakai2/common/common-composite-component/src/java/org/sakaiproject/component/common/authorization/DefaultPermissionsManagerImpl.java,v 1.2 2005/05/13 17:22:17 lance.indiana.edu Exp $
 *
 **********************************************************************************/
