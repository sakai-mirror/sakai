/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/superstructure-api/src/java/org/sakaiproject/api/common/superstructure/Node.java,v 1.1 2005/05/10 21:23:25 lance.indiana.edu Exp $
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

package org.sakaiproject.api.common.superstructure;

import java.util.Set;

import org.sakaiproject.api.common.manager.Persistable;
import org.sakaiproject.api.common.type.Typeable;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id$
 * @since Sakai 2.0
 */
public interface Node extends Persistable, Typeable
{
  /**
   * A convenience method which simplifies adding a child to a Node.
   * 
   * @param child
   */
  public void addChild(Node child);

  /**
   * Removes a child Node from this Node.
   * 
   * @param child
   */
  public void removeChild(Node child);

  /**
   * Provides direct access to the underlying Collection containing children. Mutations of this Set will
   * not be persisted unless you save the Node through the Manager.
   * 
   * @return Returns the children.
   */
  public Set getChildren();

  /**
   * Typically this value will contain more verbose text than the displayName.
   * 
   * @return Returns the description.
   */
  public String getDescription();

  /**
   * @param description
   *          The description to set (i.e. the "long" name).
   */
  public void setDescription(String description);

  /**
   * @return The "short" name of the Node.
   */
  public String getDisplayName();

  /**
   * @param displayName
   *          The "short" name of the Node.
   */
  public void setDisplayName(String displayName);

  /**
   * @return Returns the parent of the Node.
   */
  public Node getParent();

  /**
   * @param parents
   *          The parent to set. Valid values include null (i.e. a null parent specifies a root Node).
   */
  public void setParent(Node parent);

  /**
   * This is the pointer back to the referenced object.
   * 
   * @return
   */
  public String getReferenceUuid();

  /**
   * This is the pointer back to the referenced object.
   * 
   * @param referenceUuid
   */
  public void setReferenceUuid(String referenceUuid);

  /**
   * Returns the depth of the Node in the tree (0 based).
   * 
   * @return
   */
  public Integer getDepth();

}

/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/superstructure-api/src/java/org/sakaiproject/api/common/superstructure/Node.java,v 1.1 2005/05/10 21:23:25 lance.indiana.edu Exp $
 *
 **********************************************************************************/
