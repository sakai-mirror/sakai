/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/common-composite-component/src/java/org/sakaiproject/component/common/edu/person/SakaiPersonManagerImpl.java,v 1.5 2005/05/13 18:56:30 lance.indiana.edu Exp $
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

package org.sakaiproject.component.common.edu.person;

import java.sql.SQLException;
import java.util.List;

import net.sf.hibernate.Criteria;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import net.sf.hibernate.expression.Example;
import net.sf.hibernate.expression.Expression;
import net.sf.hibernate.expression.Order;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.common.agent.Agent;
import org.sakaiproject.api.common.agent.AgentGroupManager;
import org.sakaiproject.api.common.edu.person.SakaiPerson;
import org.sakaiproject.api.common.edu.person.SakaiPersonManager;
import org.sakaiproject.api.common.type.Type;
import org.sakaiproject.api.common.type.TypeManager;
import org.sakaiproject.api.common.uuid.UuidManager;
import org.sakaiproject.component.common.manager.PersistableHelper;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id$
 */
public class SakaiPersonManagerImpl extends HibernateDaoSupport implements
    SakaiPersonManager
{
  private static final Log LOG = LogFactory
      .getLog(SakaiPersonManagerImpl.class);

  private static final String PERCENT_SIGN = "%";
  private static final String SURNAME = "surname";
  private static final String GIVENNAME = "givenName";
  private static final String UID = "uid";
  private static final String TYPE_UUID = "typeUuid";
  private static final String AGENT_UUID = "agentUuid";
  private static final String HQL_FIND_SAKAI_PERSON_BY_AGENT_AND_TYPE = "findEduPersonByAgentAndType";
  private static final String HQL_FIND_SAKAI_PERSON_BY_UID = "findSakaiPersonByUid";

  private TypeManager typeManager; // dep inj
  private AgentGroupManager agentGroupManager; // dep inj
  private UuidManager uuidManager; // dep inj
  private PersistableHelper persistableHelper; // dep inj

  // SakaiPerson record types
  private Type systemMutableType; // oba constant
  private Type userMutableType; // oba constant

  private boolean cacheFindSakaiPersonString = true;
  private boolean cacheFindSakaiPersonStringType = true;
  private boolean cacheFindSakaiPersonSakaiPerson = true;
  private boolean cacheFindSakaiPersonByUid = true;

  private static final String[] SYSTEM_MUTBALE_PRIMITIVES = {
      "org.sakaiproject", "api.common.edu.person",
      "SakaiPerson.recordType.systemMutable", "System Mutable SakaiPerson",
      "System Mutable SakaiPerson", };
  private static final String[] USER_MUTBALE_PRIMITIVES = { "org.sakaiproject",
      "api.common.edu.person", "SakaiPerson.recordType.userMutable",
      "User Mutable SakaiPerson", "User Mutable SakaiPerson", };

  public void init()
  {
    LOG.debug("init()");

    LOG.debug("// init systemMutableType");
    systemMutableType = typeManager.getType(SYSTEM_MUTBALE_PRIMITIVES[0],
        SYSTEM_MUTBALE_PRIMITIVES[1], SYSTEM_MUTBALE_PRIMITIVES[2]);
    if (systemMutableType == null)
    {
      systemMutableType = typeManager.createType(SYSTEM_MUTBALE_PRIMITIVES[0],
          SYSTEM_MUTBALE_PRIMITIVES[1], SYSTEM_MUTBALE_PRIMITIVES[2],
          SYSTEM_MUTBALE_PRIMITIVES[3], SYSTEM_MUTBALE_PRIMITIVES[4]);
    }
    if (systemMutableType == null)
      throw new IllegalStateException("systemMutableType == null");

    LOG.debug("// init userMutableType");
    userMutableType = typeManager.getType(USER_MUTBALE_PRIMITIVES[0],
        USER_MUTBALE_PRIMITIVES[1], USER_MUTBALE_PRIMITIVES[2]);
    if (userMutableType == null)
    {
      userMutableType = typeManager.createType(USER_MUTBALE_PRIMITIVES[0],
          USER_MUTBALE_PRIMITIVES[1], USER_MUTBALE_PRIMITIVES[2],
          USER_MUTBALE_PRIMITIVES[3], USER_MUTBALE_PRIMITIVES[4]);
    }
    if (userMutableType == null)
      throw new IllegalStateException("userMutableType == null");

    LOG.debug("init() has completed successfully");
  }

  /**
   * @see org.sakaiproject.api.common.edu.person.SakaiPersonManager#create(java.lang.String, org.sakaiproject.api.common.type.Type)
   */
  public SakaiPerson create(final String agentUuid, final Type recordType)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("create(String " + agentUuid + ", Type " + recordType + ")");
    }
    ; // no validation necessary; delegated impl

    LOG.debug("return this.create(agentUuid, null, recordType);");
    return this.create(agentUuid, null, recordType);
  }

  /**
   * @see org.sakaiproject.api.common.edu.person.SakaiPersonManager#create(java.lang.String, java.lang.String, org.sakaiproject.api.common.type.Type)
   */
  public SakaiPerson create(String agentUuid, String uid, Type recordType)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("create(String " + agentUuid + ", String " + uid + ", Type "
          + recordType + ")");
    }
    if (agentUuid == null || agentUuid.length() < 1)
      throw new IllegalArgumentException("Illegal agentUuid argument passed!");
    ; // a null uid is valid
    if (!isSupportedType(recordType))
      throw new IllegalArgumentException("Illegal recordType argument passed!");

    SakaiPersonImpl spi = new SakaiPersonImpl();
    persistableHelper.createPersistableFields(spi);
    spi.setUuid(uuidManager.createUuid());
    spi.setAgentUuid(agentUuid);
    spi.setUid(uid);
    spi.setTypeUuid(recordType.getUuid());
    this.getHibernateTemplate().save(spi);

    LOG.debug("return spi;");
    return spi;
  }

  /**
   * @see org.sakaiproject.api.common.edu.person.SakaiPersonManager#getSakaiPerson(org.sakaiproject.api.common.type.Type)
   */
  public SakaiPerson getSakaiPerson(Type recordType)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getSakaiPerson(Type " + recordType + ")");
    }
    ; // no validation required; method is delegated.

    Agent agent = agentGroupManager.getAgent();
    if (agent == null)
      throw new IllegalStateException("Could not obtain Agent!");

    LOG.debug("return findSakaiPerson(agent.getUuid(), recordType);");
    return getSakaiPerson(agent.getUuid(), recordType);
  }

  /**
   * @see org.sakaiproject.api.common.edu.person.SakaiPersonManager#getPrototype()
   */
  public SakaiPerson getPrototype()
  {
    LOG.debug("getPrototype()");

    return new SakaiPersonImpl();
  }

  /**
   * @see org.sakaiproject.api.common.edu.person.SakaiPersonManager#findSakaiPersonByUid(java.lang.String)
   */
  public List findSakaiPersonByUid(final String uid)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("findSakaiPersonByUid(String " + uid + ")");
    }
    if (uid == null || uid.length() < 1)
      throw new IllegalArgumentException("Illegal uid argument passed!");

    final HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        final Query q = session.getNamedQuery(HQL_FIND_SAKAI_PERSON_BY_UID);
        q.setParameter(UID, uid, Hibernate.STRING);
        q.setCacheable(cacheFindSakaiPersonByUid);
        return q.list();
      }
    };

    LOG.debug("return getHibernateTemplate().executeFind(hcb);");
    return getHibernateTemplate().executeFind(hcb);
  }

  /**
   * @see SakaiPersonManager#save(SakaiPerson)
   */
  public void save(SakaiPerson sakaiPerson)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("save(SakaiPerson " + sakaiPerson + ")");
    }
    if (sakaiPerson == null)
      throw new IllegalArgumentException("Illegal sakaiPerson argument passed!");
    if (!isSupportedType(sakaiPerson.getTypeUuid()))
      throw new IllegalArgumentException(
          "The sakaiPerson argument contains an invalid Type!");

    // AuthZ
    if (getSystemMutableType().getUuid().equals(sakaiPerson.getTypeUuid()))
    {
      throw new IllegalAccessError("System mutable records cannot be updated.");
    }
    else
    {
      if (getUserMutableType().getUuid().equals(sakaiPerson.getTypeUuid()))
      {
        // AuthZ - Ensure the current user is updating their own record
        if (!agentGroupManager.getAgent().getUuid().equals(
            sakaiPerson.getAgentUuid()))
          throw new IllegalAccessError(
              "You do not have permissions to update this record!");
      }
      else
      {
        throw new UnsupportedOperationException(
            "SakaiPerson has an unsupported Type!");
      }
    }

    // store record
    if (!(sakaiPerson instanceof SakaiPersonImpl))
    {
      //TODO support alternate implementations of SakaiPerson
      // copy bean properties into new SakaiPersonImpl with beanutils?
      throw new UnsupportedOperationException(
          "Unknown SakaiPerson implementation found!");
    }
    else
    {
      // update lastModifiedDate
      SakaiPersonImpl spi = (SakaiPersonImpl) sakaiPerson;
      persistableHelper.modifyPersistableFields(spi);
      // use update(..) method to ensure someone does not try to insert a 
      // prototype.
      getHibernateTemplate().update(spi);
    }
  }

  /**
   * @see org.sakaiproject.api.common.edu.person.SakaiPersonManager#findSakaiPerson(java.lang.String, org.sakaiproject.api.common.type.Type)
   */
  public SakaiPerson getSakaiPerson(final String agentUuid,
      final Type recordType)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getSakaiPerson(String " + agentUuid + ", Type " + recordType
          + ")");
    }
    if (agentUuid == null || agentUuid.length() < 1)
      throw new IllegalArgumentException("Illegal agentUuid argument passed!");
    if (recordType == null || !isSupportedType(recordType))
      throw new IllegalArgumentException("Illegal recordType argument passed!");

    final HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        Query q = session
            .getNamedQuery(HQL_FIND_SAKAI_PERSON_BY_AGENT_AND_TYPE);
        q.setParameter(AGENT_UUID, agentUuid, Hibernate.STRING);
        q.setParameter(TYPE_UUID, recordType.getUuid(), Hibernate.STRING);
        q.setCacheable(cacheFindSakaiPersonStringType);
        return q.uniqueResult();
      }
    };

    LOG.debug("return (SakaiPerson) getHibernateTemplate().execute(hcb);");
    return (SakaiPerson) getHibernateTemplate().execute(hcb);
  }

  /**
   * @see SakaiPersonManager#findSakaiPerson(String)
   */
  public List findSakaiPerson(final String simpleSearchCriteria)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("findSakaiPerson(String " + simpleSearchCriteria + ")");
    }
    if (simpleSearchCriteria == null || simpleSearchCriteria.length() < 1)
      throw new IllegalArgumentException(
          "Illegal simpleSearchCriteria argument passed!");

    final String match = PERCENT_SIGN + simpleSearchCriteria + PERCENT_SIGN;
    final HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        final Criteria c = session.createCriteria(SakaiPersonImpl.class);
        c.add(Expression.disjunction().add(Expression.ilike(UID, match)).add(
            Expression.ilike(GIVENNAME, match)).add(
            Expression.ilike(SURNAME, match)));
        c.addOrder(Order.asc(SURNAME));
        c.setCacheable(cacheFindSakaiPersonString);
        return c.list();
      }
    };

    LOG.debug("return getHibernateTemplate().executeFind(hcb);");
    return getHibernateTemplate().executeFind(hcb);
  }

  /**
   * @param typeManager
   *          The typeManager to set.
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
   * 
   * @see org.sakaiproject.api.common.edu.person.SakaiPersonManager#getUserMutableType()
   */
  public Type getUserMutableType()
  {
    LOG.debug("getUserMutableType()");

    return userMutableType;
  }

  /**
   * 
   * @see org.sakaiproject.api.common.edu.person.SakaiPersonManager#getSystemMutableType()
   */
  public Type getSystemMutableType()
  {
    LOG.debug("getSystemMutableType()");

    return systemMutableType;
  }

  /**
   * @param agentGroupManager
   *          The agentGroupManager to set.
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
   * @see org.sakaiproject.api.common.edu.person.SakaiPersonManager#findSakaiPerson(org.sakaiproject.api.common.edu.person.SakaiPerson)
   */
  public List findSakaiPerson(final SakaiPerson queryByExample)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("findSakaiPerson(SakaiPerson " + queryByExample + ")");
    }
    if (queryByExample == null)
      throw new IllegalArgumentException(
          "Illegal queryByExample argument passed!");

    final HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        Criteria criteria = session.createCriteria(queryByExample.getClass());
        criteria.add(Example.create(queryByExample));
        criteria.setCacheable(cacheFindSakaiPersonSakaiPerson);
        return criteria.list();
      }
    };

    LOG.debug("return getHibernateTemplate().executeFind(hcb);");
    return getHibernateTemplate().executeFind(hcb);
  }

  /**
   * @see org.sakaiproject.api.common.edu.person.SakaiPersonManager#delete(org.sakaiproject.api.common.edu.person.SakaiPerson)
   */
  public void delete(final SakaiPerson sakaiPerson)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("delete(SakaiPerson " + sakaiPerson + ")");
    }
    if (sakaiPerson == null)
      throw new IllegalArgumentException("Illegal sakaiPerson argument passed!");

    LOG.debug("getHibernateTemplate().delete(sakaiPerson);");
    getHibernateTemplate().delete(sakaiPerson);
  }

  private boolean isSupportedType(Type recordType)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("isSupportedType(Type " + recordType + ")");
    }

    if (recordType == null) return false;
    if (this.getUserMutableType().equals(recordType)) return true;
    if (this.getSystemMutableType().equals(recordType)) return true;
    return false;
  }

  private boolean isSupportedType(String typeUuid)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("isSupportedType(String " + typeUuid + ")");
    }

    if (typeUuid == null) return false;
    if (this.getUserMutableType().getUuid().equals(typeUuid)) return true;
    if (this.getSystemMutableType().getUuid().equals(typeUuid)) return true;
    return false;
  }

  /**
   * @param cacheFindSakaiPersonStringType The cacheFindSakaiPersonStringType to set.
   */
  public void setCacheFindSakaiPersonStringType(
      boolean cacheFindSakaiPersonStringType)
  {
    this.cacheFindSakaiPersonStringType = cacheFindSakaiPersonStringType;
  }

  /**
   * @param cacheFindSakaiPersonString The cacheFindSakaiPersonString to set.
   */
  public void setCacheFindSakaiPersonString(boolean cacheFindSakaiPersonString)
  {
    this.cacheFindSakaiPersonString = cacheFindSakaiPersonString;
  }

  /**
   * @param cacheFindSakaiPersonSakaiPerson The cacheFindSakaiPersonSakaiPerson to set.
   */
  public void setCacheFindSakaiPersonSakaiPerson(
      boolean cacheFindSakaiPersonSakaiPerson)
  {
    this.cacheFindSakaiPersonSakaiPerson = cacheFindSakaiPersonSakaiPerson;
  }

  /**
   * @param uuidManager The uuidManager to set.
   */
  public void setUuidManager(UuidManager uuidManager)
  {
    this.uuidManager = uuidManager;
  }

  /**
   * @param cacheFindSakaiPersonByUid The cacheFindSakaiPersonByUid to set.
   */
  public void setCacheFindSakaiPersonByUid(boolean cacheFindSakaiPersonByUid)
  {
    this.cacheFindSakaiPersonByUid = cacheFindSakaiPersonByUid;
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
 * $Header: /cvs/sakai2/common/common-composite-component/src/java/org/sakaiproject/component/common/edu/person/SakaiPersonManagerImpl.java,v 1.5 2005/05/13 18:56:30 lance.indiana.edu Exp $
 *
 **********************************************************************************/
