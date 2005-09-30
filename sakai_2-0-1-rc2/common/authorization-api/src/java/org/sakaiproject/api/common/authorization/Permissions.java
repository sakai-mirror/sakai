/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/authorization-api/src/java/org/sakaiproject/api/common/authorization/Permissions.java,v 1.1 2005/05/10 21:23:25 lance.indiana.edu Exp $
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

import org.sakaiproject.api.common.manager.Persistable;

/**
 * This is the base class for all classes that will implement Permissions.
 * 
 * @see org.sakaiproject.service.common.authorization.DefaultPermissions
 * @author <a href="mailto:jlannan@iupui.edu">Jarrod Lannan</a>
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id$
 * @since Sakai 2.0
 */
public interface Permissions extends Persistable
{
  /**
   * The friendly name of the function or role.
   * 
   * @return
   */
  public String getName();

  /**
   * The friendly name of the function or role.
   * 
   * @param name
   */
  public void setName(String name);

  /**
   * The description of the permission.
   * 
   * @return
   */
  public String getDescription();

  /**
   * The description of the permission.
   * 
   * @param name
   */
  public void setDescription(String description);
}

/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/authorization-api/src/java/org/sakaiproject/api/common/authorization/Permissions.java,v 1.1 2005/05/10 21:23:25 lance.indiana.edu Exp $
 *
 **********************************************************************************/
