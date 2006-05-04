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

package org.sakaiproject.component.common.hibernate;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.common.agent.Agent;
import org.sakaiproject.api.common.superstructure.Node;
import org.sakaiproject.component.common.agent.AgentBean;
import org.sakaiproject.component.common.agent.GroupBean;
import org.sakaiproject.component.common.agent.GroupParentMemberMap;
import org.sakaiproject.component.common.superstructure.NodeImpl;
import org.sakaiproject.component.common.superstructure.NodeParentChildMapImpl;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

/**
 * This class does all of the "heavy lifting" for the 
 * {@link org.sakaiproject.component.common.hibernate.DenormalizationInterceptor}
 * class.
 * 
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id$
 */
public class DenormalizationDao extends HibernateDaoSupport
{
  private static final Log LOG = LogFactory.getLog(DenormalizationDao.class);

  private static final String AGENT = "agent";
  private static final String HQL_DELETE_FROM_GROUPPARENTMEMBERMAP_MEMBER = "from org.sakaiproject.component.common.agent.GroupParentMemberMap as gpmm where gpmm.member.id = ?";
  private static final String HQL_FROM_NODEPARENTCHILDMAP_WHERE_CHILD = "from org.sakaiproject.component.common.superstructure.NodeParentChildMapImpl as pc where pc.child.id = ?";
  private static final String JOINNODEANDMAPFORPARENT = "joinNodeAndMapForParent";
  private static final String PARENT = "parent";

  public void denormalizeAgent(final AgentBean agentBean)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("denormalizeAgent(AgentBean " + agentBean + ")");
    }
    if (agentBean == null)
      throw new IllegalArgumentException("Illegal agentBean argument passed!");

    LOG.debug("// delete any existing denormalized data for Agent");
    Integer d = scrubGroupParentMemberMap(agentBean);
    if (LOG.isDebugEnabled())
    {
      LOG.debug("Deleted " + d + " rows from denormalized data");
    }

    LOG
        .debug("// find all non-transitive (first order) Group memberships for Agent");
    List groups = findParents((AgentBean) agentBean);
    if (LOG.isDebugEnabled())
    {
      LOG.debug(groups == null ? "No Group memberships found: " + groups
          : "Found " + groups.size() + "Group memberships");
    }

    LOG.debug("// find all transitive Group memberships for Agent");
    Set transitiveMemberships = new HashSet(groups);
    Map circularReferences = new HashMap();
    for (Iterator iter = groups.iterator(); iter.hasNext();)
    {
      GroupBean group = (GroupBean) iter.next();
      recurseAncestors(group, transitiveMemberships, circularReferences);
    }
    circularReferences = null; // no longer needed; gc

    LOG.debug("// include \"self\" in denormalized data");
    transitiveMemberships.add(agentBean);

    LOG.debug("// save denormalized parent/member relationships");
    for (Iterator iter = transitiveMemberships.iterator(); iter.hasNext();)
    {
      Agent ancestor = (Agent) iter.next();
      GroupParentMemberMap gpmm = new GroupParentMemberMap();
      gpmm.setParent(ancestor);
      gpmm.setMember(agentBean);
      getHibernateTemplate().save(gpmm);
    }
  }

  private void recurseAncestors(GroupBean group, Set ancestors,
      Map circularReferences)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("recurseAncestors(GroupBean " + group + ", Set " + ancestors
          + ", Map " + circularReferences + ")");
    }

    if (circularReferences.get(group.getId()) == null)
    {
      LOG.debug("//1 we have NOT seen this node before");
      circularReferences.put(group.getId(), group);
      List parents = findParents((AgentBean) group);
      if (LOG.isDebugEnabled())
      {
        LOG.debug(parents == null ? "No Group memberships found: " + parents
            : "Found " + parents.size() + "Group memberships");
      }
      for (Iterator iter = parents.iterator(); iter.hasNext();)
      {
        GroupBean gb = (GroupBean) iter.next();
        if (circularReferences.get(gb.getId()) == null)
        {
          LOG.debug("//2 we have NOT seen this node before");
          ancestors.add(gb);
          recurseAncestors(gb, ancestors, circularReferences);
        }
      }
    }
    else
    {
      LOG.debug("// we HAVE seen this node before");
    }
  }

  /**
   * Maintain parent/child denormalized data for Group memberships.
   * 
   * @param member
   * @return
   */
  public Integer scrubGroupParentMemberMap(final AgentBean member)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("scrubGroupParentMemberMap(AgentBean " + member + ")");
    }
    if (member == null)
      throw new IllegalArgumentException("Illegal member argument passed!");

    final HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        return new Integer(session.delete(
            HQL_DELETE_FROM_GROUPPARENTMEMBERMAP_MEMBER, member.getId(),
            Hibernate.LONG));
      }
    };

    LOG.debug("return (Integer) getHibernateTemplate().execute(hcb);");
    return (Integer) getHibernateTemplate().execute(hcb);
  }

  /**
   * Maintain parent/child denormalized data for Nodes.
   * 
   * @param child
   * @return
   */
  public Integer scrubNodeParentChildMap(final NodeImpl child)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("scrubNodeParentChildMap(NodeImpl " + child + ")");
    }
    if (child == null)
      throw new IllegalArgumentException("Illegal child argument passed!");

    final HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        int rowsDeleted = session.delete(
            HQL_FROM_NODEPARENTCHILDMAP_WHERE_CHILD, child.getId(),
            Hibernate.LONG);
        return new Integer(rowsDeleted);
      }
    };

    LOG.debug("return (Integer) getHibernateTemplate().execute(hcb);");
    return (Integer) getHibernateTemplate().execute(hcb);
  }

  /**
   * Creates the denormalized parent/child relationships data for Nodes.
   * 
   * @param node
   */
  public void denormalizeNode(NodeImpl node)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("denormalizeNode(NodeImpl " + node + ")");
    }
    if (node == null)
      throw new IllegalArgumentException("Illegal node argument passed!");

    final Map parents = new HashMap(); // key parent.id, value parent
    LOG.debug("// include \"self\" in denormalized data");
    parents.put(node.getId(), node);

    LOG.debug("Walk up the tree tracking parents and tree depth");
    Node child = node;
    while (child.getParent() != null)
    {
      NodeImpl parent = (NodeImpl) child.getParent();
      parents.put(parent.getId(), parent);
      child = child.getParent();
    }

    if (!parents.isEmpty())
    {
      LOG.debug("// not a root node; i.e has a parent");

      LOG.debug("// delete parent/child denormalized data");
      Integer d = scrubNodeParentChildMap(node);
      if (LOG.isDebugEnabled())
      {
        LOG.debug("Deleted " + d + " rows of denormalized data for Node: "
            + node);
      }

      LOG.debug("// create new denormalized data for Node");
      for (Iterator iter = parents.values().iterator(); iter.hasNext();)
      {
        Node parent = (Node) iter.next();
        NodeParentChildMapImpl pc = new NodeParentChildMapImpl();
        pc.setChild(node);
        pc.setParent(parent);
        getHibernateTemplate().save(pc);
      }
    }
  }

  /**
   * Find all non-transitive groups where this agent is a member.
   * 
   * @param agent
   * @return
   */
  private List findParents(final AgentBean agent)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("findParents(AgentBean " + agent + ")");
    }
    if (agent == null)
      throw new IllegalArgumentException("Illegal agent argument passed!");

    final HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        Query q = session.getNamedQuery("findGroupsContainingMember");
        q.setEntity(AGENT, agent);
        return q.list();
      }
    };

    LOG.debug("return getHibernateTemplate().executeFind(hcb);");
    return getHibernateTemplate().executeFind(hcb);
  }

  public Node getNode(Long id)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getNode(Long " + id + ")");
    }

    LOG
        .debug("return (Node) getHibernateTemplate().get(org.sakaiproject.component.common.superstructure.NodeImpl.class, id);");
    return (Node) getHibernateTemplate().get(
        org.sakaiproject.component.common.superstructure.NodeImpl.class, id);
  }

  public void saveNode(NodeImpl node)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("saveNode(NodeImpl " + node + ")");
    }
    getHibernateTemplate().saveOrUpdate(node);
  }

  public Iterator getTransitiveChildren(final NodeImpl parent)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getTransitiveChildren(NodeImpl " + parent + ")");
    }
    if (parent == null)
      throw new IllegalArgumentException("Illegal parent argument passed!");

    final HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        Query q = session.getNamedQuery(JOINNODEANDMAPFORPARENT);
        q.setParameter(PARENT, parent.getId(), Hibernate.LONG);
        return q.iterate();
      }
    };

    LOG.debug("return (Iterator) getHibernateTemplate().execute(hcb);");
    return (Iterator) getHibernateTemplate().execute(hcb);
  }

}



