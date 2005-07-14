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

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.common.agent.Agent;
import org.sakaiproject.api.common.type.Type;
import org.sakaiproject.service.legacy.user.User;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id$
 */
public class AgentUserProxy implements Agent
{
  private static final Log LOG = LogFactory.getLog(AgentUserProxy.class);

  private User user;

  AgentUserProxy(User user)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("new AgentUserProxy(User " + user + ")");
    }
    if (user == null)
      throw new IllegalArgumentException("Illegal user argument passed!");

    this.user = user;
  }

  /**
   * @see org.sakaiproject.api.common.agent.Agent#getSessionManagerUserId()
   * @see org.sakaiproject.service.legacy.resource.Resource#getId()
   */
  public String getSessionManagerUserId()
  {
    LOG.trace("getSessionManagerUserId()");

    return user.getId();
  }

  /**
   * @see org.sakaiproject.api.common.agent.Agent#setSessionManagerUserId(java.lang.String)
   */
  public void setSessionManagerUserId(String sessionManagerUserId)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setSessionManagerUserId(String " + sessionManagerUserId + ")");
    }

    throw new UnsupportedOperationException(
        "User does not have an appropriate method!");
  }

  /**
   * @see org.sakaiproject.api.common.agent.Agent#getEnterpriseId()
   * @see org.sakaiproject.service.legacy.resource.Resource#getId()
   */
  public String getEnterpriseId()
  {
    LOG.trace("getEnterpriseId()");

    return user.getId();
  }

  /**
   * @see org.sakaiproject.api.common.agent.Agent#setEnterpriseId(java.lang.String)
   */
  public void setEnterpriseId(String enterpriseId)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setEnterpriseId(String " + enterpriseId + ")");
    }

    throw new UnsupportedOperationException(
        "User does not have an appropriate method!");
  }

  /**
   * @see org.sakaiproject.api.common.agent.Agent#getDisplayName()
   * @see User#getDisplayName()
   */
  public String getDisplayName()
  {
    LOG.trace("getDisplayName()");

    return user.getDisplayName();
  }

  /**
   * @see org.sakaiproject.api.common.agent.Agent#setDisplayName(java.lang.String)
   */
  public void setDisplayName(String displayName)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setDisplayName(String " + displayName + ")");
    }

    throw new UnsupportedOperationException(
        "User does not have an appropriate method!");
  }

  /**
   * @see org.sakaiproject.api.common.agent.Agent#getSortName()
   * @see User#getDisplayName()
   */
  public String getSortName()
  {
    LOG.trace("getSortName()");

    return user.getDisplayName();
  }

  /**
   * @see org.sakaiproject.api.common.agent.Agent#setSortName(java.lang.String)
   */
  public void setSortName(String sortName)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setSortName(String " + sortName + ")");
    }

    throw new UnsupportedOperationException(
        "User does not have an appropriate method!");
  }

  /**
   * @see org.sakaiproject.api.common.manager.Persistable#getUuid()
   * @see org.sakaiproject.service.legacy.resource.Resource#getId()
   */
  public String getUuid()
  {
    LOG.trace("getUuid()");

    return user.getId();
  }

  /**
   * @see org.sakaiproject.api.common.manager.Persistable#getLastModifiedBy()
   * @see User#getModifiedBy()
   */
  public String getLastModifiedBy()
  {
    LOG.trace("getLastModifiedBy()");

    return user.getModifiedBy().getId();
  }

  /**
   * @see org.sakaiproject.api.common.manager.Persistable#getLastModifiedDate()
   * @see User#getModifiedTime()
   */
  public Date getLastModifiedDate()
  {
    LOG.trace("getLastModifiedDate()");

    return new Date(user.getModifiedTime().getTime());
  }

  /**
   * @see org.sakaiproject.api.common.manager.Persistable#getCreatedBy()
   * @see User#getCreatedBy()
   */
  public String getCreatedBy()
  {
    LOG.trace("getCreatedBy()");

    return user.getCreatedBy().getId();
  }

  /**
   * @see org.sakaiproject.api.common.manager.Persistable#getCreatedDate()
   * @see User#getCreatedTime()
   */
  public Date getCreatedDate()
  {
    LOG.trace("getCreatedDate()");

    return new Date(user.getCreatedTime().getTime());
  }

  /**
   * @see org.sakaiproject.api.common.type.Typeable#getType()
   */
  public Type getType()
  {
    LOG.trace("getType()");

    throw new UnsupportedOperationException(
        "User does not have an appropriate method!");
  }

  /**
   * @see org.sakaiproject.api.common.type.Typeable#setType(org.sakaiproject.api.common.type.Type)
   */
  public void setType(Type type)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setType(Type " + type + ")");
    }

    throw new UnsupportedOperationException(
        "User does not have an appropriate method!");
  }

}



