/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/manager-api/src/java/org/sakaiproject/api/common/manager/Persistable.java,v 1.1 2005/05/10 21:23:26 lance.indiana.edu Exp $
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

package org.sakaiproject.api.common.manager;

import java.util.Date;

/**
 * 
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id: Persistable.java,v 1.1 2005/05/10 21:23:26 lance.indiana.edu Exp $
 * @since Sakai 2.0
 */
public interface Persistable
{
  /**
   * All persistent objects must have a UUID.
   * 
   * @return Returns the UUID for given object.
   */
  public String getUuid();

  /**
   * The last Agent that modified the persistent state of this object.
   * 
   * @return UUID of Agent that made last modification.
   */
  public String getLastModifiedBy();

  /**
   * The last time this object's persistent state was modified.
   * 
   * @return
   */
  public Date getLastModifiedDate();

  /**
   * The Agent that created this persistent object.
   * 
   * @return UUID of the Agent that created this persistent object.
   */
  public String getCreatedBy();

  /**
   * The time and date this persistent object was created.
   * 
   * @return
   */
  public Date getCreatedDate();
}

/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/manager-api/src/java/org/sakaiproject/api/common/manager/Persistable.java,v 1.1 2005/05/10 21:23:26 lance.indiana.edu Exp $
 *
 **********************************************************************************/
