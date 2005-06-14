/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/common-composite-component-data/src/java/org/sakaiproject/component/common/superstructure/NodeParentChildMapImpl.java,v 1.1 2005/05/11 15:41:14 lance.indiana.edu Exp $
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.common.superstructure.Node;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id$
 */
public class NodeParentChildMapImpl
{
  private static final Log LOG = LogFactory
      .getLog(NodeParentChildMapImpl.class);

  private Long id;
  private Integer version;

  private NodeImpl parent;
  private NodeImpl child;

  /**
   * @return Returns the child.
   */
  public Node getChild()
  {
    LOG.trace("getChild()");

    return child;
  }

  /**
   * @param child
   *          The child to set.
   */
  public void setChild(Node child)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setChild(Node " + child + ")");
    }
    if (child == null)
      throw new IllegalArgumentException("Illegal child argument passed!");

    this.child = (NodeImpl) child;
  }

  /**
   * @return Returns the id.
   */
  public Long getId()
  {
    LOG.trace("getId()");

    return id;
  }

  /**
   * @param id
   *          The id to set.
   */
  public void setId(Long id)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setId(Long " + id + ")");
    }
    if (id == null)
      throw new IllegalArgumentException("Illegal id argument passed!");

    this.id = id;
  }

  /**
   * @return Returns the parent.
   */
  public Node getParent()
  {
    LOG.trace("getParent()");

    return parent;
  }

  /**
   * @param parent
   *          The parent to set.
   */
  public void setParent(Node parent)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setParent(Node " + parent + ")");
    }
    if (parent == null)
      throw new IllegalArgumentException("Illegal parent argument passed!");

    this.parent = (NodeImpl) parent;
  }

  /**
   * @return Returns the version.
   */
  public Integer getVersion()
  {
    LOG.trace("getVersion()");

    return version;
  }

  /**
   * @param version
   *          The version to set.
   */
  public void setVersion(Integer version)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setVersion(Integer " + version + ")");
    }
    ; // validation removed to enable hibernate's reflection optimizer

    this.version = version;
  }

  private String businessKey()
  {
    LOG.trace("businessKey()");

    return parent.getUuid() + child.getUuid();
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object obj)
  {
    if (LOG.isTraceEnabled())
    {
      LOG.trace("equals(Object " + obj + ")");
    }

    if (obj == this) return true;
    if (!(obj instanceof NodeParentChildMapImpl)) return false;
    NodeParentChildMapImpl other = (NodeParentChildMapImpl) obj;
    return this.businessKey().equals(other.businessKey());
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  public int hashCode()
  {
    LOG.trace("hashCode()");

    return businessKey().hashCode();
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    LOG.trace("toString()");

    final StringBuffer sb = new StringBuffer();
    sb.append("{id=");
    sb.append(id);
    sb.append(", parent=");
    sb.append(parent == null ? null : parent.getId());
    sb.append(", child=");
    sb.append(child == null ? child.getId() : child.getId());
    sb.append(", version=");
    sb.append(version);
    sb.append("}");
    return sb.toString();
  }

}

/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/common-composite-component-data/src/java/org/sakaiproject/component/common/superstructure/NodeParentChildMapImpl.java,v 1.1 2005/05/11 15:41:14 lance.indiana.edu Exp $
 *
 **********************************************************************************/
