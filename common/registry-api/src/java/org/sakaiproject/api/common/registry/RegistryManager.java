/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/registry-api/src/java/org/sakaiproject/api/common/registry/RegistryManager.java,v 1.1 2005/05/10 21:23:26 lance.indiana.edu Exp $
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

package org.sakaiproject.api.common.registry;

import org.sakaiproject.api.common.type.Type;
import org.sakaiproject.api.common.type.UuidTypeResolvable;

/**
 * This interface is a strawman of a registry that would contain mappings
 * from Types to Managers. This is needed to support the scenario where
 * one has been traversing the SuperStructure and only has a reference to a 
 * Node. Since a Node only has a referenceUuid and a Type, a resolver is needed
 * to get a reference back to the object that the Node refers to.
 * 
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id: RegistryManager.java,v 1.1 2005/05/10 21:23:26 lance.indiana.edu Exp $
 * @since Sakai 2.0
 */
public interface RegistryManager
{
  /**
   * Useful method for "reverse" lookups.
   * 
   * @param type
   * @return
   */
  public UuidTypeResolvable resolveManager(Type type);

  /**
   * This method provides a way for a Manager to register the Types that it
   * supports.
   * 
   * @param type
   * @param manager
   */
  public void registerManager(Type type, UuidTypeResolvable manager);
}

/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/registry-api/src/java/org/sakaiproject/api/common/registry/RegistryManager.java,v 1.1 2005/05/10 21:23:26 lance.indiana.edu Exp $
 *
 **********************************************************************************/
