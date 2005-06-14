/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/authorization-api/src/java/org/sakaiproject/api/common/authorization/PermissionsManager.java,v 1.1 2005/05/10 21:23:25 lance.indiana.edu Exp $
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

import java.util.List;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id$
 * @since Sakai 2.0
 */
public interface PermissionsManager
{
  /**
   * Create a new persistent Permissions object.
   * 
   * @param name
   * @param description
   * @param permissionsMask
   * @return
   */
  public Permissions createPermissions(String name, String description,
      PermissionsMask permissionsMask);

  /**
   * Retrieve Permissions object by Uuid;
   * 
   * @param uuid
   * @return
   */
  public Permissions getPermissions(String uuid);

  /**
   * Before creating or querying Authorizations, one will need to obtain an 
   * instance of a Permissions class using this method.
   * 
   * @param permissionsMask
   * @return
   */
  public Permissions getPermissions(PermissionsMask permissionsMask);

  /**
   * Persist passed Permissions object.
   * 
   * @param permissions
   */
  public void savePermissions(Permissions permissions);

  /**
   * Implementations of this interface need to return the Class that is used
   * to map the object relationship. This will be needed to determine which
   * Permissions Class is used to resolve the Permissions mappping; this is
   * especially important for code that will extend the default Permissions 
   * matrix.
   * 
   * @return
   */
  public Class getBackingBeanClass();

  /**
   * For example, if you need to find all simple and composite permissions where 
   *  the READ bit is not null (has significant semantic meaning).
   *  
   * @param permissions
   * @return A List of UUIDs of type String.
   */
  public List findAllRelatedPermissions(Permissions permissions);
}

/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/authorization-api/src/java/org/sakaiproject/api/common/authorization/PermissionsManager.java,v 1.1 2005/05/10 21:23:25 lance.indiana.edu Exp $
 *
 **********************************************************************************/
