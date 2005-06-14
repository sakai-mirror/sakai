/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/common-composite-component/src/java/org/sakaiproject/component/common/manager/PersistableHelper.java,v 1.2 2005/05/13 17:22:17 lance.indiana.edu Exp $
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

package org.sakaiproject.component.common.manager;

import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.common.manager.Persistable;
import org.sakaiproject.api.kernel.session.Session;
import org.sakaiproject.api.kernel.session.SessionManager;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id$
 */
public class PersistableHelper
{
  private static final String SYSTEM = "SYSTEM";

  private static final Log LOG = LogFactory.getLog(PersistableHelper.class);

  private static final String LASTMODIFIEDDATE = "lastModifiedDate";
  private static final String LASTMODIFIEDBY = "lastModifiedBy";
  private static final String CREATEDDATE = "createdDate";
  private static final String CREATEDBY = "createdBy";

  private SessionManager sessionManager; // dep inj

  public void modifyPersistableFields(Persistable persistable)
  {
    Date now = new Date(); //time sensitive
    if (LOG.isDebugEnabled())
    {
      LOG.debug("modifyPersistableFields(Persistable " + persistable + ")");
    }
    if (persistable == null)
      throw new IllegalArgumentException("Illegal persistable argument passed!");

    try
    {
      String actor = getActor();

      PropertyUtils.setProperty(persistable, LASTMODIFIEDBY, actor);
      PropertyUtils.setProperty(persistable, LASTMODIFIEDDATE, now);
    }
    catch (Exception e)
    {
      LOG.error(e);
      throw new Error(e);
    }
  }

  public void createPersistableFields(Persistable persistable)
  {
    Date now = new Date(); //time sensitive
    if (LOG.isDebugEnabled())
    {
      LOG.debug("modifyPersistableFields(Persistable " + persistable + ")");
    }
    if (persistable == null)
      throw new IllegalArgumentException("Illegal persistable argument passed!");

    try
    {
      String actor = getActor();

      PropertyUtils.setProperty(persistable, LASTMODIFIEDBY, actor);
      PropertyUtils.setProperty(persistable, LASTMODIFIEDDATE, now);
      PropertyUtils.setProperty(persistable, CREATEDBY, actor);
      PropertyUtils.setProperty(persistable, CREATEDDATE, now);
    }
    catch (Exception e)
    {
      LOG.error(e);
      throw new Error(e);
    }
  }

  private String getActor()
  {
    LOG.debug("getActor()");

    String actor = null;
    Session session = sessionManager.getCurrentSession();
    if (session != null)
    {
      actor = session.getUserId();
    }
    else
    {
      return SYSTEM;
    }
    if (actor == null || actor.length() < 1)
    {
      return SYSTEM;
    }
    else
    {
      return actor;
    }
  }

  /**
   * Dependency injection.
   * @param sessionManager The sessionManager to set.
   */
  public void setSessionManager(SessionManager sessionManager)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setSessionManager(SessionManager " + sessionManager + ")");
    }

    this.sessionManager = sessionManager;
  }
}

/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/common-composite-component/src/java/org/sakaiproject/component/common/manager/PersistableHelper.java,v 1.2 2005/05/13 17:22:17 lance.indiana.edu Exp $
 *
 **********************************************************************************/
