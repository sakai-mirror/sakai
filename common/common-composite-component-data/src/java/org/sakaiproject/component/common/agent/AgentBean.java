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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.common.manager.NodeAwareTypeablePersistableImpl;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id$
 */
public class AgentBean extends NodeAwareTypeablePersistableImpl implements
    org.sakaiproject.api.common.agent.Agent
{
  private static final Log LOG = LogFactory.getLog(AgentBean.class);

  private String displayName;
  private String enterpriseId;
  private String sessionManagerUserId;

  private String getBusinessKey()
  {
    LOG.trace("getBusinessKey()");

    return uuid + enterpriseId;
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

    if (this == obj) return true;
    if (!(obj instanceof AgentBean)) return false;
    //TODO support alternate Agent impls
    AgentBean other = (AgentBean) obj;
    return this.getBusinessKey().equals(other.getBusinessKey());
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  public int hashCode()
  {
    LOG.trace("hashCode()");

    return getBusinessKey().hashCode();
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    LOG.trace("toString()");

    StringBuffer sb = new StringBuffer();
    sb.append("{id=");
    sb.append(id);
    sb.append(", uuid=");
    sb.append(uuid);
    sb.append(", displayName=");
    sb.append(displayName);
    sb.append(", type=");
    sb.append(type);
    return sb.toString();
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

    this.displayName = displayName;
  }

  public String getEnterpriseId()
  {
    LOG.trace("getEnterpriseId()");

    return enterpriseId;
  }

  public void setEnterpriseId(String enterpriseId)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setEnterpriseId(String " + enterpriseId + ")");
    }

    this.enterpriseId = enterpriseId;
  }

  public String getSortName()
  {
    LOG.trace("getSortName()");

    return displayName;
  }

  public void setSortName(String sortName)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setSortName(String " + sortName + ")");
    }
    if (sortName == null)
      throw new IllegalArgumentException("Illegal sortName argument passed!");

    ; // take no action; displayName is used by default for sortName
  }

  /**
   * @return Returns the sessionManagerUserId.
   */
  public String getSessionManagerUserId()
  {
    LOG.trace("getSessionManagerUserId()");

    return sessionManagerUserId;
  }

  /**
   * @param sessionManagerUserId The sessionManagerUserId to set.
   */
  public void setSessionManagerUserId(String sessionManagerUserId)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setSessionManagerUserId(String " + sessionManagerUserId + ")");
    }

    this.sessionManagerUserId = sessionManagerUserId;
  }
}



