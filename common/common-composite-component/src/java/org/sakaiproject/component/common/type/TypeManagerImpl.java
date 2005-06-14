/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/common-composite-component/src/java/org/sakaiproject/component/common/type/TypeManagerImpl.java,v 1.2 2005/05/13 17:22:17 lance.indiana.edu Exp $
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

package org.sakaiproject.component.common.type;

import java.sql.SQLException;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.common.agent.AgentGroupManager;
import org.sakaiproject.api.common.type.Type;
import org.sakaiproject.api.common.type.TypeManager;
import org.sakaiproject.api.common.uuid.UuidManager;
import org.sakaiproject.component.common.manager.PersistableHelper;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id: TypeManagerImpl.java,v 1.2 2005/05/13 17:22:17 lance.indiana.edu Exp $
 */
public class TypeManagerImpl extends HibernateDaoSupport implements TypeManager
{
  private static final Log LOG = LogFactory.getLog(TypeManagerImpl.class);

  private static final String ID = "id";
  private static final String FINDTYPEBYID = "findTypeById";
  private static final String UUID = "uuid";
  private static final String FINDTYPEBYUUID = "findTypeByUuid";
  private static final String AUTHORITY = "authority";
  private static final String DOMAIN = "domain";
  private static final String KEYWORD = "keyword";
  private static final String FINDTYPEBYTUPLE = "findTypeByTuple";

  private boolean cacheFindTypeByTuple = true;
  private boolean cacheFindTypeByUuid = true;
  private boolean cacheFindTypeById = true;

  private UuidManager uuidManager; // dep inj
  private AgentGroupManager agentGroupManager; // dep inj
  private PersistableHelper persistableHelper; // dep inj

  //  public Type getType(final Long id)
  //  {
  //    if (LOG.isDebugEnabled())
  //    {
  //      LOG.debug("getType(Long " + id + ")");
  //    }
  //    if (id == null) throw new IllegalArgumentException();
  //
  //    final HibernateCallback hcb = new HibernateCallback()
  //    {
  //      public Object doInHibernate(Session session) throws HibernateException,
  //          SQLException
  //      {
  //        Query q = session.getNamedQuery(FINDTYPEBYID);
  //        q.setLong(ID, id.longValue());
  //        q.setCacheable(cacheFindTypeById);
  //        return q.uniqueResult();
  //      }
  //    };
  //    Type type = (Type) getHibernateTemplate().execute(hcb);
  //    return type;
  //  }

  /**
   * @see org.sakaiproject.api.type.TypeManager#createType(java.lang.String, java.lang.String,
   *      java.lang.String, java.lang.String, java.lang.String)
   */
  public Type createType(String authority, String domain, String keyword,
      String displayName, String description)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("createType(String " + authority + ", String " + domain
          + ", String " + keyword + ", String " + displayName + ", String "
          + description + ")");
    }
    // validation
    if (authority == null || authority.length() < 1)
      throw new IllegalArgumentException("authority");
    if (domain == null || domain.length() < 1)
      throw new IllegalArgumentException("domain");
    if (keyword == null || keyword.length() < 1)
      throw new IllegalArgumentException("keyword");
    if (displayName == null || displayName.length() < 1)
      throw new IllegalArgumentException("displayName");

    TypeImpl ti = new TypeImpl();
    persistableHelper.createPersistableFields(ti);
    ti.setUuid(uuidManager.createUuid());
    ti.setAuthority(authority);
    ti.setDomain(domain);
    ti.setKeyword(keyword);
    ti.setDisplayName(displayName);
    ti.setDescription(description);
    getHibernateTemplate().save(ti);
    return ti;
  }

  public void saveType(Type type)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("saveType(Type " + type + ")");
    }
    if (type == null) throw new IllegalArgumentException("type");

    if (type instanceof TypeImpl)
    { // found well known Type
      TypeImpl ti = (TypeImpl) type;
      persistableHelper.modifyPersistableFields(ti);
      getHibernateTemplate().saveOrUpdate(ti);
    }
    else
    { // found external Type 
      throw new IllegalAccessError(
          "Alternate Type implementations not supported yet.");
    }
  }

  /**
   * Dependency injection
   * 
   * @param uuidManager
   *          The uuidManager to set.
   */
  public void setUuidManager(UuidManager uuidManager)
  {
    if (LOG.isTraceEnabled())
    {
      LOG.trace("setUuidManager(UuidManager " + uuidManager + ")");
    }

    this.uuidManager = uuidManager;
  }

  /**
   * @see org.sakaiproject.service.common.type.TypeManager#getType(java.lang.String)
   */
  public Type getType(final String uuid)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getType(String " + uuid + ")");
    }
    if (uuid == null || uuid.length() < 1)
    {
      throw new IllegalArgumentException("uuid");
    }

    final HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        Query q = session.getNamedQuery(FINDTYPEBYUUID);
        q.setString(UUID, uuid);
        q.setCacheable(cacheFindTypeByUuid);
        return q.uniqueResult();
      }
    };
    Type type = (Type) getHibernateTemplate().execute(hcb);
    return type;
  }

  /**
   * @see org.sakaiproject.service.common.type.TypeManager#getType(java.lang.String, java.lang.String,
   *      java.lang.String)
   */
  public Type getType(final String authority, final String domain,
      final String keyword)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getType(String " + authority + ", String " + domain
          + ", String " + keyword + ")");
    }
    // validation
    if (authority == null || authority.length() < 1)
      throw new IllegalArgumentException("authority");
    if (domain == null || domain.length() < 1)
      throw new IllegalArgumentException("domain");
    if (keyword == null || keyword.length() < 1)
      throw new IllegalArgumentException("keyword");

    final HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        Query q = session.getNamedQuery(FINDTYPEBYTUPLE);
        q.setString(AUTHORITY, authority);
        q.setString(DOMAIN, domain);
        q.setString(KEYWORD, keyword);
        q.setCacheable(cacheFindTypeByTuple);
        return q.uniqueResult();
      }
    };
    Type type = (Type) getHibernateTemplate().execute(hcb);
    return type;
  }

  /**
   * @param cacheFindTypeByTuple
   *          The cacheFindTypeByTuple to set.
   */
  public void setCacheFindTypeByTuple(boolean cacheFindTypeByTuple)
  {
    if (LOG.isInfoEnabled())
    {
      LOG.info("setCacheFindTypeByTuple(boolean " + cacheFindTypeByTuple + ")");
    }

    this.cacheFindTypeByTuple = cacheFindTypeByTuple;
  }

  /**
   * @param cacheFindTypeByUuid
   *          The cacheFindTypeByUuid to set.
   */
  public void setCacheFindTypeByUuid(boolean cacheFindTypeByUuid)
  {
    if (LOG.isInfoEnabled())
    {
      LOG.info("setCacheFindTypeByUuid(boolean " + cacheFindTypeByUuid + ")");
    }

    this.cacheFindTypeByUuid = cacheFindTypeByUuid;
  }

  /**
   * @param cacheFindTypeById
   *          The cacheFindTypeById to set.
   */
  public void setCacheFindTypeById(boolean cacheFindTypeById)
  {
    if (LOG.isInfoEnabled())
    {
      LOG.info("setCacheFindTypeById(boolean " + cacheFindTypeById + ")");
    }

    this.cacheFindTypeById = cacheFindTypeById;
  }

  public void deleteType(Type type)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("deleteType(Type " + type + ")");
    }

    throw new UnsupportedOperationException("Types should never be deleted!");
  }

  /**
   * Dependency injection
   * 
   * @param agentGroupManager The agentGroupManager to set.
   */
  public void setAgentGroupManager(AgentGroupManager agentGroupManager)
  {
    if (LOG.isTraceEnabled())
    {
      LOG.trace("setAgentGroupManager(AgentGroupManager " + agentGroupManager
          + ")");
    }
    this.agentGroupManager = agentGroupManager;
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
 * $Header: /cvs/sakai2/common/common-composite-component/src/java/org/sakaiproject/component/common/type/TypeManagerImpl.java,v 1.2 2005/05/13 17:22:17 lance.indiana.edu Exp $
 *
 **********************************************************************************/
