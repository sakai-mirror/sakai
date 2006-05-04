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

package org.sakaiproject.api.common.authorization;

import java.util.Date;

/**
 * <b>TODO:</b> The old AuthorizationManager had a composite key consisting of 
 * agentId, functionId, and NodeId. This seems a bit limiting but also 
 * simplifies the implementation. If we do not create this composite key, we
 * may run into trouble with ordering of query results and no significance.
 * 
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @author <a href="mailto:jlannan@iupui.edu">Jarrod Lannan</a>
 * @version $Id$
 * @since Sakai 2.0
 */
public interface AuthorizationManager
{
  public Authorization createAuthorization(String agentUuid,
      String permissionsUuid, String nodeUuid);

  public Authorization createAuthorization(String agentUuid,
      String permissionsUuid, String nodeUuid, Date effectiveDate,
      Date expirationDate);

  /**
   * Retrieve an Authorization by UUID.
   * 
   * @param uuid
   * @return
   */
  public Authorization getAuthorization(String uuid);

  /**
   * Persist Authorization.
   * 
   * @param authorization
   */
  public void saveAuthorization(Authorization authorization);

  /**
   * Delete Authorization.
   * 
   * @param authorization
   */
  public void deleteAuthorization(Authorization authorization);

  /**
   * Since no AgentUuid is passed, the currently logged in user will be used.
   * 
   * The combination of functionType and signficantBits create the "what".
   * 
   * @param permissionsManager The PermissionsManager that will be queried
   *        by AuthorizationManager.
   * @param permissions The "function".
   * @param nodeUuid The "where".
   * @return TRUE if the Agent is authorized; FALSE if Agent is not authorized.
   */
  public boolean isAuthorized(PermissionsManager permissionsManager,
      Permissions permissions, String nodeUuid);

  /**
   * The combination of functionType and signficantBits create the "what".
   * 
   * @param agentUuid The "who".
   * @param permissionsManager The PermissionsManager that will be queried
   *        by AuthorizationManager.
   * @param permissions The "function".
   * @param nodeUuid The "where".
   * @return TRUE if the Agent is authorized; FALSE if Agent is not authorized.
   */
  public boolean isAuthorized(String agentUuid,
      PermissionsManager permissionsManager, Permissions permissions,
      String nodeUuid);
}



