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

package org.sakaiproject.component.common.superstructure;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.common.manager.Persistable;
import org.sakaiproject.api.common.superstructure.Node;
import org.sakaiproject.api.common.type.Typeable;
import org.sakaiproject.component.common.manager.TypeablePersistableImpl;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id$
 */
public class NodeImpl extends TypeablePersistableImpl implements Node,
    Persistable, Typeable
{
  private static final Log LOG = LogFactory.getLog(NodeImpl.class);

  private NodeImpl parent;
  private Set children = new HashSet(); // <Node>
  private String description;
  private String displayName;
  private String referenceUuid;
  private Integer depth;

  /**
   * 
   * @see org.sakaiproject.api.common.superstructure.Node#addChild(org.sakaiproject.api.common.superstructure.Node)
   */
  public void addChild(Node childNode)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("addChild(Node " + childNode + ")");
    }
    if (childNode == null)
      throw new IllegalArgumentException("childNode == null");

    childNode.setParent(this);
    children.add(childNode);
  }

  /**
   * 
   * @see org.sakaiproject.api.common.superstructure.Node#removeChild(org.sakaiproject.api.common.superstructure.Node)
   */
  public void removeChild(Node child)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("removeChild(Node " + child + ")");
    }
    if (child == null)
      throw new IllegalArgumentException("Illegal child argument passed!");

    child.setParent(null);
    children.remove(child);
  }

  /**
   * @return Returns the children.
   */
  public Set getChildren()
  {
    LOG.trace("getChildren()");

    return children;
  }

  /**
   * @param children
   *          The children to set.
   */
  public void setChildren(Set children)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setChildren(Set " + children + ")");
    }

    this.children = children;
  }

  /**
   * @return Returns the description.
   */
  public String getDescription()
  {
    LOG.trace("getDescription()");

    return description;
  }

  /**
   * @param description
   *          The description to set.
   */
  public void setDescription(String description)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setDescription(String " + description + ")");
    }

    this.description = description;
  }

  /**
   * @see org.sakaiproject.service.osid.authorization.Node#getParent()
   */
  public Node getParent()
  {
    LOG.trace("getParent()");

    return parent;
  }

  /**
   * @see org.sakaiproject.service.osid.authorization.Node#setParent(org.sakaiproject.service.osid.authorization.Node)
   */
  public void setParent(Node parent)
  {
    if (this == parent || this.equals(parent))
    {
      throw new IllegalArgumentException(
          "Illegal parent argument passed! Parent cannot be set to self!");
    }

    this.parent = (NodeImpl) parent;
  }

  /**
   * @return Returns the displayName.
   */
  public String getDisplayName()
  {
    LOG.trace("getDisplayName()");

    return displayName;
  }

  /**
   * @param displayName
   *          The displayName to set.
   */
  public void setDisplayName(String displayName)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setDisplayName(String " + displayName + ")");
    }
    if (displayName == null || displayName.length() < 1)
      throw new IllegalArgumentException("Illegal displayName argument passed!");

    this.displayName = displayName;
  }

  /**
   * @see org.sakaiproject.api.common.superstructure.Node#getReferenceUuid()
   */
  public String getReferenceUuid()
  {
    LOG.trace("getReferenceUuid()");

    return referenceUuid;
  }

  /**
   * @see org.sakaiproject.api.common.superstructure.Node#setReferenceUuid(java.lang.String)
   */
  public void setReferenceUuid(String referenceUuid)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setReferenceUuid(String " + referenceUuid + ")");
    }
    if (referenceUuid == null || referenceUuid.length() < 1)
      throw new IllegalArgumentException(
          "Illegal referenceUuid argument passed!");

    this.referenceUuid = referenceUuid;
  }

  private String businessKey()
  {
    StringBuffer sb = new StringBuffer();
    sb.append(uuid);
    sb.append(referenceUuid);
    return sb.toString();
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if (!(obj instanceof NodeImpl)) return false;
    NodeImpl other = (NodeImpl) obj;
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

    StringBuffer sb = new StringBuffer();
    sb.append(super.toString());
    sb.append(", displayName=");
    sb.append(displayName);
    sb.append(", referenceUuid=");
    sb.append(referenceUuid);
    sb.append(", parent=");
    sb.append(parent == null ? null : parent.getId());
    sb.append(", depth=");
    sb.append(depth);
    sb.append(", description=");
    sb.append(description);
    sb.append("}");
    return sb.toString();
  }

  /**
   * @return Returns the depth.
   */
  public Integer getDepth()
  {
    LOG.trace("getDepth()");

    return depth;
  }

  /**
   * @param depth The depth to set.
   */
  public void setDepth(Integer depth)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setDepth(Integer " + depth + ")");
    }
    //FIXME
    //    if (depth == null)
    //      throw new IllegalArgumentException("Illegal depth argument passed!");

    this.depth = depth;
  }

}



