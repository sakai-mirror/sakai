/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/authorization-api/src/java/org/sakaiproject/api/common/authorization/Authorization.java,v 1.1 2005/05/10 21:23:25 lance.indiana.edu Exp $
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

package org.sakaiproject.api.common.authorization;

/**
 * @version $Id: Authorization.java,v 1.1 2005/05/10 21:23:25 lance.indiana.edu Exp $
 */
import java.util.Date;

import org.sakaiproject.api.common.manager.Persistable;

/**
 * Represents an access control entry.
 * <pre>
 *   Who == agentUuid; i.e. Agent
 *  What == permissionsUuid; i.e. Permissions
 * Where == nodeUuid; i.e. Node
 *  When == effectiveDate >= now <= expirationDate
 *    Note: Both effectiveDate and expirationDate are optional and can be null.
 * </pre>
 * 
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @author <a href="mailto:jlannan@iupui.edu">Jarrod Lannan</a>
 * @version $Id: Authorization.java,v 1.1 2005/05/10 21:23:25 lance.indiana.edu Exp $
 * @since Sakai 2.0
 */
public interface Authorization extends Persistable
{
  public String getAgentUuid();

  public void setAgentUuid(String agentUuid);

  public String getPermissionsUuid();

  public void setPermissionsUuid(String permissionsUuid);

  public String getNodeUuid();

  public void setNodeUuid(String nodeUuid);

  public Date getEffectiveDate();

  public void setEffectiveDate(Date effectiveDate);

  public Date getExpirationDate();

  public void setExpirationDate(Date expirationDate);

  public boolean isActive();
}

/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/authorization-api/src/java/org/sakaiproject/api/common/authorization/Authorization.java,v 1.1 2005/05/10 21:23:25 lance.indiana.edu Exp $
 *
 **********************************************************************************/
