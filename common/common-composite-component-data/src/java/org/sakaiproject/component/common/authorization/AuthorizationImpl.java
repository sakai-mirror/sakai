/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/common-composite-component-data/src/java/org/sakaiproject/component/common/authorization/AuthorizationImpl.java,v 1.1 2005/05/11 15:41:15 lance.indiana.edu Exp $
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

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.common.authorization.Authorization;
import org.sakaiproject.api.common.manager.Persistable;
import org.sakaiproject.component.common.manager.PersistableImpl;

/**
 * @author <a href="mailto:jlannan.iupui.edu">Jarrod Lannan</a>
 * @version $Id: AuthorizationImpl.java,v 1.1 2005/05/11 15:41:15 lance.indiana.edu Exp $
 * 
 */
public class AuthorizationImpl extends PersistableImpl implements Persistable,
    Authorization
{

  private static final Log LOG = LogFactory.getLog(AuthorizationImpl.class);
  
  private String agentUuid;
  private Date effectiveDate;
  private Date expirationDate;
  private String nodeUuid;
  private String permissionsUuid;

  public AuthorizationImpl()
  {
  }

  /**
   * @param agentUuid
   * @param permissionsUuid
   * @param nodeUuid
   * @param effectiveDate
   * @param expirationDate
   * 
   * An implementation of the Authorization
   */
  AuthorizationImpl(String agentUuid, String permissionsUuid, String nodeUuid,
      Date effectiveDate, Date expirationDate)
  {

    if (LOG.isDebugEnabled())
    {
      LOG.debug("AuthorizationImpl(String " + agentUuid + ", String "
          + permissionsUuid + ", String" + nodeUuid + ", Date " + effectiveDate
          + ", Date " + expirationDate + ")");
    }

    if (agentUuid == null)
      throw new IllegalArgumentException("Illegal agentUuid argument passed!");
    else
      if (permissionsUuid == null)
          throw new IllegalArgumentException(
              "Illegal permissionsUuid argument passed!");
    if (nodeUuid == null)
        throw new IllegalArgumentException("Illegal nodeUuid argument passed!");

    this.agentUuid = agentUuid;
    this.permissionsUuid = permissionsUuid;
    this.nodeUuid = nodeUuid;
    this.lastModifiedBy = "sakai_tester";
    this.setLastModifiedDate(new Date());

    if (effectiveDate == null)
    {
      this.effectiveDate = new Date();
    }
    else
    {
      this.effectiveDate = effectiveDate;
    }

    if (expirationDate != null)
    {
      this.expirationDate = expirationDate;
    }

    // test for expiration date which preceedes effective date
    if (effectiveDate != null && expirationDate != null)
    {
      if (effectiveDate.compareTo(expirationDate) < 0)
      {
        throw new Error("Expiration Date preceedes Effective Date");
      }
    }
  }

  /**
   * @see org.sakaiproject.api.common.authorization.Authorization#getAgentUuid()
   */
  public String getAgentUuid()
  {
    return agentUuid;
  }

  /**
   * @see org.sakaiproject.api.common.authorization.Authorization#getEffectiveDate()
   */
  public Date getEffectiveDate()
  {
    return effectiveDate;
  }

  /**
   * @see org.sakaiproject.api.common.authorization.Authorization#getExpirationDate()
   */
  public Date getExpirationDate()
  {
    return expirationDate;
  }

  /**
   * @see org.sakaiproject.api.common.authorization.Authorization#getNodeUuid()
   */
  public String getNodeUuid()
  {
    return nodeUuid;
  }

  /**
   * @see org.sakaiproject.api.common.authorization.Authorization#getPermissionsUuid()
   */
  public String getPermissionsUuid()
  {
    return permissionsUuid;
  }

  /**
   * @see org.sakaiproject.api.common.authorization.Authorization#setAgentUuid(java.lang.String)
   */
  public void setAgentUuid(String agentUuid)
  {
    this.agentUuid = agentUuid;
  }

  /**
   * @see org.sakaiproject.api.common.authorization.Authorization#setEffectiveDate(java.util.Date)
   */
  public void setEffectiveDate(Date effectiveDate)
  {
    this.effectiveDate = effectiveDate;
  }

  /**
   * @see org.sakaiproject.api.common.authorization.Authorization#setExpirationDate(java.util.Date)
   */
  public void setExpirationDate(Date expirationDate)
  {
    this.expirationDate = expirationDate;
  }

  /**
   * @see org.sakaiproject.api.common.authorization.Authorization#setNodeUuid(java.lang.String)
   */
  public void setNodeUuid(String nodeUuid)
  {
    this.nodeUuid = nodeUuid;
  }

  /**
   * @see org.sakaiproject.api.common.authorization.Authorization#setPermissionsUuid(java.lang.String)
   */
  public void setPermissionsUuid(String permissionsUuid)
  {
    this.permissionsUuid = permissionsUuid;
  }

  /**
   * @see org.sakaiproject.api.common.authorization.Authorization#isActive()
   */
  public boolean isActive()
  {

    if (LOG.isDebugEnabled())
    {
      LOG.debug("isActive()");
    }

    Date now = new Date();

    // effective date is never null
    if (now.before(effectiveDate))
    {
      return false;
    }

    if (expirationDate != null)
    {
      if (now.before(expirationDate))
      {
        return true;
      }
      else
      {
        return false;
      }
    }
    else
    {
      return true;
    }
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if (!(obj instanceof AuthorizationImpl)) return false;
    AuthorizationImpl other = (AuthorizationImpl) obj;

    if ((agentUuid == null ? other.agentUuid == null : agentUuid
        .equals(other.agentUuid))
        && (permissionsUuid == null ? other.permissionsUuid == null
            : permissionsUuid.equals(other.permissionsUuid))
        && (nodeUuid == null ? other.nodeUuid == null : nodeUuid
            .equals(other.nodeUuid)))
    {
      return true;
    }
    return false;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  public int hashCode()
  {
    return agentUuid.hashCode() + permissionsUuid.hashCode()
        + nodeUuid.hashCode();
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append(super.toString());

    sb.append("{agentId=");
    sb.append(agentUuid);
    sb.append(", permissionsId=");
    sb.append(permissionsUuid);
    sb.append(", nodeId=");
    sb.append(nodeUuid);
    sb.append(", effectiveDate=");
    sb.append(effectiveDate);
    sb.append(", expirationDate=");
    sb.append(expirationDate);
    sb.append("}");
    return sb.toString();
  }

}

/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/common-composite-component-data/src/java/org/sakaiproject/component/common/authorization/AuthorizationImpl.java,v 1.1 2005/05/11 15:41:15 lance.indiana.edu Exp $
 *
 **********************************************************************************/