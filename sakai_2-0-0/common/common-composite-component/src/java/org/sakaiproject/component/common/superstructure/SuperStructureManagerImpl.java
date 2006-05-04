/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/common-composite-component/src/java/org/sakaiproject/component/common/superstructure/SuperStructureManagerImpl.java,v 1.2 2005/05/13 17:22:17 lance.indiana.edu Exp $
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

package org.sakaiproject.component.common.superstructure;

import java.sql.SQLException;
import java.util.List;

import net.sf.hibernate.Criteria;
import net.sf.hibernate.FetchMode;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import net.sf.hibernate.expression.Expression;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.common.agent.AgentGroupManager;
import org.sakaiproject.api.common.superstructure.Node;
import org.sakaiproject.api.common.superstructure.SuperStructureManager;
import org.sakaiproject.api.common.type.Type;
import org.sakaiproject.api.common.type.TypeManager;
import org.sakaiproject.api.common.uuid.UuidManager;
import org.sakaiproject.component.common.manager.PersistableHelper;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id: SuperStructureManagerImpl.java,v 1.2 2005/05/13 17:22:17 lance.indiana.edu Exp $
 */
public class SuperStructureManagerImpl extends HibernateDaoSupport implements
    SuperStructureManager
{
  private static final Log LOG = LogFactory
      .getLog(SuperStructureManagerImpl.class);

  private static final String[] ORGANIZATION_TYPE_PRIMITIVES = {
      "org.sakaiproject", "api.common.superstructure",
      "Node.type.Organization", "Organization", "Organization", null, };
  private static final String[] ORGANIZATIONAL_UNIT_TYPE_PRIMITIVES = {
      "org.sakaiproject", "api.common.superstructure",
      "Node.type.OrganizationalUnit", "Organizational Unit",
      "Organizational Unit", null, };
  private static final String[] DEFAULT_ROOT_NODE_PRIMITIVES = {
      "e7c94bc6-393a-4f53-80af-40e53a9350d0", "Sakai", "Default Organization", };

  private static final String ID = "id";
  private static final String UUID = "uuid";
  private static final String CHILD = "child";
  private static final String CHILDREN = "children";
  private static final String PARENT = "parent";
  private static final String REFERENCEUUID = "referenceUuid";
  private static final String GET_NODE_BY_UUID = "getNodeByUuid";
  private static final String JOIN_NODE_AND_MAP_FOR_CHILD = "joinNodeAndMapForChild";
  private static final String JOIN_NODE_AND_MAP_FOR_PARENT = "joinNodeAndMapForParent";

  private Type organizationType; // oba constant
  private Type organizationalUnitType; // oba constant
  private Node rootNode;

  private boolean cacheGetNodeById = true;
  private boolean cacheGetNodeByUuid = true;
  private boolean cacheGetTransitiveNodeChildrenByParent = true;
  private boolean cacheGetTransitiveNodeParentsByChild = true;
  private boolean cacheGetReferencedNodeByUuid = true;

  private UuidManager idManager; // dep inj
  private TypeManager typeManager; // dep inj
  private AgentGroupManager agentGroupManager; //dep inj
  private PersistableHelper persistableHelper; // dep inj

  /**
   * @see org.sakaiproject.api.common.superstructure.SuperStructureManager#createNode(java.lang.String, org.sakaiproject.api.common.superstructure.Node, org.sakaiproject.api.common.type.Type, java.lang.String, java.lang.String)
   */
  public Node createNode(String referenceUuid, Node parent, Type type,
      String displayName, String description)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("createNode(String " + referenceUuid + ", Node " + parent
          + ", Type " + type + ", String " + displayName + ", String "
          + description + ")");
    }
    ; // validation is not necessary; impl is delegated

    LOG
        .debug("return createNode(referenceUuid, parent, type, idManager.createUuid(), displayName, description);");
    return createNode(referenceUuid, parent, type, idManager.createUuid(),
        displayName, description);
  }

  private Node createNode(String referenceUuid, Node parent, Type type,
      String uuid, String displayName, String description)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("createNode(String " + referenceUuid + ", Node " + parent
          + ", Type " + type + ", String " + uuid + ", String " + displayName
          + ", String " + description + ")");
    }
    if (referenceUuid == null || referenceUuid.length() < 1)
      throw new IllegalArgumentException(
          "Illegal referenceUuid argument passed!");
    if (parent == null
        && !referenceUuid.equals(DEFAULT_ROOT_NODE_PRIMITIVES[0]))
      throw new IllegalArgumentException("Illegal parent argument passed!");
    if (type == null)
      throw new IllegalArgumentException("Illegal type argument passed!");
    if (uuid == null || uuid.length() < 1)
      throw new IllegalArgumentException("Illegal uuid argument passed!");
    if (displayName == null || displayName.length() < 1)
      throw new IllegalArgumentException("Illegal displayName argument passed!");

    NodeImpl q = new NodeImpl();

    persistableHelper.createPersistableFields(q);

    q.setReferenceUuid(referenceUuid);
    q.setParent(parent);
    q.setType(type);
    q.setUuid(uuid);
    q.setDisplayName(displayName);
    q.setDescription(description);
    if (parent != null)
    {
      parent.addChild(q);
    }
    getHibernateTemplate().save(q);

    LOG.debug("return q;");
    return q;
  }

  public Node getNode(final Long id)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getNode(Long " + id + ")");
    }
    ; // validation not required; delegated impl

    LOG.debug("return getNode(id, false);");
    return getNode(id, false); // lazy loading by default
  }

  public Node getNode(final Long id, final boolean eagerChildren)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getNode(Long " + id + ", boolean " + eagerChildren + ")");
    }
    if (id == null)
      throw new IllegalArgumentException("Illegal id argument passed!");

    final HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        FetchMode fetchMode = null;
        if (eagerChildren)
        {
          fetchMode = FetchMode.EAGER;
        }
        else
        {
          fetchMode = FetchMode.DEFAULT;
        }
        Criteria c = session.createCriteria(NodeImpl.class);
        c.setFetchMode(CHILDREN, fetchMode);
        c.add(Expression.eq(ID, id));
        c.setCacheable(cacheGetNodeById);
        return c.uniqueResult();
      }
    };

    LOG.debug("return (Node) getHibernateTemplate().execute(hcb);");
    return (Node) getHibernateTemplate().execute(hcb);
  }

  public Node getNode(final String uuid)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getNode(String " + uuid + ")");
    }
    ; // no validation required; delegated impl

    LOG.debug("return getNode(uuid, false);");
    return getNode(uuid, false);
  }

  /**
   * @see org.sakaiproject.api.common.superstructure.SuperStructureManager#getNode(java.lang.String, boolean)
   */
  public Node getNode(final String uuid, final boolean eagerChildren)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getNode(String " + uuid + ", boolean " + eagerChildren + ")");
    }
    if (uuid == null || uuid.length() < 1)
      throw new IllegalArgumentException("Illegal uuid argument passed!");

    final HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        FetchMode fetchMode = null;
        if (eagerChildren)
        {
          fetchMode = FetchMode.EAGER;
        }
        else
        {
          fetchMode = FetchMode.DEFAULT;
        }
        Criteria c = session.createCriteria(NodeImpl.class);
        c.setFetchMode(CHILDREN, fetchMode);
        c.add(Expression.eq(UUID, uuid));
        c.setCacheable(cacheGetNodeByUuid);
        return c.uniqueResult();
      }
    };

    LOG.debug("return (Node) getHibernateTemplate().execute(hcb);");
    return (Node) getHibernateTemplate().execute(hcb);
  }

  public void saveNode(Node node)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("saveNode(Node " + node + ")");
    }
    if (node == null)
      throw new IllegalArgumentException("Illegal node argument passed!");

    NodeImpl qbi = (NodeImpl) node;
    getHibernateTemplate().saveOrUpdate(qbi);

    LOG.debug("return;");
    return;
  }

  public void deleteNode(Node node)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("deleteNode(Node " + node + ")");
    }
    if (node == null)
      throw new IllegalArgumentException("Illegal node argument passed!");

    if (node.getParent() != null)
    {
      LOG.debug("// parent is not null; remove Node from parent's child Set");
      node.getParent().getChildren().remove(node);
    }
    // delete Node
    getHibernateTemplate().delete(node);

    LOG.debug("return;");
    return;
  }

  public List getTransitiveChildren(final Node parent)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getTransitiveChildren(Node " + parent + ")");
    }
    if (parent == null)
      throw new IllegalArgumentException("Illegal parent argument passed!");

    final HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        Query q = session.getNamedQuery(JOIN_NODE_AND_MAP_FOR_PARENT);
        q.setParameter(PARENT, ((NodeImpl) parent).getId(), Hibernate.LONG);
        q.setCacheable(cacheGetTransitiveNodeChildrenByParent);
        return q.list();
      }
    };
    List transitiveChildren = getHibernateTemplate().executeFind(hcb);
    transitiveChildren.remove(parent);

    LOG.debug("return transitiveChildren;");
    return transitiveChildren;
  }

  public List getTransitiveParents(final Node child)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getTransitiveParents(Node " + child + ")");
    }
    if (child == null) throw new IllegalArgumentException("child == null");

    final HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        Query q = session.getNamedQuery(JOIN_NODE_AND_MAP_FOR_CHILD);
        q.setParameter(CHILD, ((NodeImpl) child).getId(), Hibernate.LONG);
        q.setCacheable(cacheGetTransitiveNodeParentsByChild);
        return q.list();
      }
    };
    List transitiveParents = getHibernateTemplate().executeFind(hcb);

    LOG.debug("return transitiveParents;");
    return transitiveParents;
  }

  public Type getOrganizationNodeType()
  {
    LOG.debug("getOrganizationNodeType()");

    if (organizationType == null)
    {
      LOG.debug("// organizationType == null; init organizationType");
      organizationType = typeManager.getType(ORGANIZATION_TYPE_PRIMITIVES[0],
          ORGANIZATION_TYPE_PRIMITIVES[1], ORGANIZATION_TYPE_PRIMITIVES[2]);
      if (organizationType == null)
      {
        organizationType = typeManager.createType(
            ORGANIZATION_TYPE_PRIMITIVES[0], ORGANIZATION_TYPE_PRIMITIVES[1],
            ORGANIZATION_TYPE_PRIMITIVES[2], ORGANIZATION_TYPE_PRIMITIVES[3],
            ORGANIZATION_TYPE_PRIMITIVES[4]);
      }
      if (organizationType == null)
        throw new IllegalStateException("organizationType == null");
    }
    return organizationType;
  }

  public Type getOrganizationalUnitNodeType()
  {
    LOG.debug("getOrganizationalUnitNodeType()");

    if (organizationalUnitType == null)
    {
      LOG
          .debug("// organizationalUnitType == null; init organizationalUnitType");
      organizationalUnitType = typeManager.getType(
          ORGANIZATIONAL_UNIT_TYPE_PRIMITIVES[0],
          ORGANIZATIONAL_UNIT_TYPE_PRIMITIVES[1],
          ORGANIZATIONAL_UNIT_TYPE_PRIMITIVES[2]);
      if (organizationalUnitType == null)
      {
        organizationalUnitType = typeManager.createType(
            ORGANIZATIONAL_UNIT_TYPE_PRIMITIVES[0],
            ORGANIZATIONAL_UNIT_TYPE_PRIMITIVES[1],
            ORGANIZATIONAL_UNIT_TYPE_PRIMITIVES[2],
            ORGANIZATIONAL_UNIT_TYPE_PRIMITIVES[3],
            ORGANIZATIONAL_UNIT_TYPE_PRIMITIVES[4]);
      }
      if (organizationalUnitType == null)
        throw new IllegalStateException("organizationalUnitType == null");
    }
    return organizationalUnitType;
  }

  /**
   * Enable cache for this signature.
   * 
   * @param cacheGetNodeById
   *          The cacheGetNodeById to set.
   */
  public void setCacheGetNodeById(boolean cacheGetNodeById)
  {
    this.cacheGetNodeById = cacheGetNodeById;
  }

  /**
   * Enable cache for this signature.
   * 
   * @param cacheGetNodeByUuid
   *          The cacheGetNodeByUuid to set.
   */
  public void setCacheGetNodeByUuid(boolean cacheGetNodeByUuid)
  {
    this.cacheGetNodeByUuid = cacheGetNodeByUuid;
  }

  /**
   * @param cacheGetTransitiveNodeChildrenByParent
   *          The cacheGetTransitiveNodeChildrenByParent to set.
   */
  public void setCacheGetTransitiveNodeChildrenByParent(
      boolean cacheGetTransitiveNodeChildrenByParent)
  {
    this.cacheGetTransitiveNodeChildrenByParent = cacheGetTransitiveNodeChildrenByParent;
  }

  /**
   * @param cacheGetTransitiveNodeParentsByChild
   *          The cacheGetTransitiveNodeParentsByChild to set.
   */
  public void setCacheGetTransitiveNodeParentsByChild(
      boolean cacheGetTransitiveNodeParentsByChild)
  {
    this.cacheGetTransitiveNodeParentsByChild = cacheGetTransitiveNodeParentsByChild;
  }

  /**
   * @param idManager
   *          The idManager to set.
   */
  public void setIdManager(UuidManager idManager)
  {
    this.idManager = idManager;
  }

  /**
   * @param typeManager
   *          The typeManager to set.
   */
  public void setTypeManager(TypeManager typeManager)
  {
    this.typeManager = typeManager;
  }

  /**
   * @see org.sakaiproject.api.common.superstructure.SuperStructureManager#getRootNode()
   */
  public Node getRootNode()
  {
    LOG.debug("getRootNode()");

    if (rootNode == null)
    {
      LOG.debug("// rootNode == null; init defaultRootNode");
      rootNode = getNode(DEFAULT_ROOT_NODE_PRIMITIVES[0]);
      if (rootNode == null)
      {
        rootNode = createNode(DEFAULT_ROOT_NODE_PRIMITIVES[0], null,
            getOrganizationNodeType(), DEFAULT_ROOT_NODE_PRIMITIVES[0],
            DEFAULT_ROOT_NODE_PRIMITIVES[1], DEFAULT_ROOT_NODE_PRIMITIVES[2]);
      }
      if (rootNode == null)
        throw new IllegalStateException("defaultRootNode == null");
    }

    LOG.debug("return rootNode;");
    return rootNode;
  }

  /**
   * @see org.sakaiproject.api.common.superstructure.SuperStructureManager#moveNode(org.sakaiproject.api.common.superstructure.Node, org.sakaiproject.api.common.superstructure.Node)
   */
  public void moveNode(Node childNode, Node newParent)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("moveNode(Node " + childNode + ", Node " + newParent + ")");
    }
    if (childNode == null)
      throw new IllegalArgumentException("Illegal childNode argument passed!");
    if (newParent == null)
      throw new IllegalArgumentException("Illegal newParent argument passed!");

    // maintain collection relationships
    Node oldParent = childNode.getParent();
    if (oldParent != null)
    {
      oldParent.getChildren().remove(childNode);
    }
    newParent.addChild(childNode);

    saveNode(childNode);

    LOG.debug("return;");
    return;
  }

  /**
   * @see org.sakaiproject.api.common.superstructure.SuperStructureManager#getReferencedNode(java.lang.String)
   */
  public Node getReferencedNode(String referenceUuid)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getReferencedNode(String " + referenceUuid + ")");
    }
    ; // no validation required; delegated impl

    LOG.debug("return getReferencedNode(referenceUuid, false);");
    return getReferencedNode(referenceUuid, false); // lazy by default
  }

  /**
   * @see org.sakaiproject.api.common.superstructure.SuperStructureManager#getReferencedNode(java.lang.String, boolean)
   */
  public Node getReferencedNode(final String referenceUuid,
      final boolean eagerChildren)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getReferencedNode(String " + referenceUuid + ", boolean "
          + eagerChildren + ")");
    }
    if (referenceUuid == null || referenceUuid.length() < 1)
      throw new IllegalArgumentException(
          "Illegal referenceUuid argument passed!");

    final HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        FetchMode fetchMode = null;
        if (eagerChildren)
        {
          fetchMode = FetchMode.EAGER;
        }
        else
        {
          fetchMode = FetchMode.DEFAULT;
        }
        Criteria c = session.createCriteria(NodeImpl.class);
        c.setFetchMode(CHILDREN, fetchMode);
        c.add(Expression.eq(REFERENCEUUID, referenceUuid));
        c.setCacheable(cacheGetReferencedNodeByUuid);
        return c.uniqueResult();
      }
    };

    LOG.debug("return (Node) getHibernateTemplate().execute(hcb);");
    return (Node) getHibernateTemplate().execute(hcb);
  }

  /**
   * @param cacheGetReferencedNodeById The cacheGetReferencedNodeById to set.
   */
  public void setCacheGetReferencedNodeByUuid(
      boolean cacheGetReferencedNodeByUuid)
  {
    this.cacheGetReferencedNodeByUuid = cacheGetReferencedNodeByUuid;
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
   * @param persistableHelper The persistableHelper to set.
   */
  public void setPersistableHelper(PersistableHelper persistableHelper)
  {
    this.persistableHelper = persistableHelper;
  }

}