/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/superstructure-api/src/java/org/sakaiproject/api/common/superstructure/SuperStructureManager.java,v 1.1 2005/05/10 21:23:25 lance.indiana.edu Exp $
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

import java.util.List;

import org.sakaiproject.api.common.type.Type;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id: SuperStructureManager.java,v 1.1 2005/05/10 21:23:25 lance.indiana.edu Exp $
 * @since Sakai 2.0
 */
public interface SuperStructureManager
{
  /**
   * Create a new Node.
   * 
   * @param referenceUuid A reference to the "real" object referred to by this Node.
   * @param parent The parent of the new Node.
   * @param type Typically the Type of a Node will be equal to the Type of the 
   *              referenced object. 
   * @param displayName The "short" name.
   * @param description The "long" name.
   * @return The newly created Node.
   */
  public Node createNode(String referenceUuid, Node parent, Type type,
      String displayName, String description);

  /**
   * Retrieve Node given its UUID.
   * 
   * @param uuid
   * @return
   */
  public Node getNode(String uuid);

  /**
   * Retrieve Node given it UUID (forward lookup).
   * 
   * @param uuid UUID
   * @param eagerChildren
   *          TRUE == eager children fetching; FALSE == lazy fetching.
   * @return
   */
  public Node getNode(String uuid, boolean eagerChildren);

  /**
   * Retrieve a Node given a referenceUuid (reverse lookup).
   * 
   * @param referenceUuid
   * @return
   */
  public Node getReferencedNode(String referenceUuid);

  /**
   * Retrieve a Node given a referenceUuid. The eagerChildren boolean is used
   * as a hint from the caller that the caller intends to traverse the children
   * of the Node. The implementation may be able to increase the performance
   * of the retrieve by using the knowledge that the caller intends to 
   * traverse the children.
   * 
   * @param referenceUuid
   * @param eagerChildren
   *          TRUE == eager children fetching; FALSE == lazy fetching.
   * @return
   */
  public Node getReferencedNode(String referenceUuid, boolean eagerChildren);

  /**
   * Persist the current state of passed Node.
   * 
   * @param node
   */
  public void saveNode(Node node);

  /**
   * Removes the Node from persistent storage.
   * 
   * @param node
   */
  public void deleteNode(Node node);

  /**
   * Convenience method for moving a Node from one parent to another parent.
   * 
   * @param childNode
   * @param newParent
   */
  public void moveNode(Node childNode, Node newParent);

  /**
   * Returns a reference to the root Node.
   * 
   * @return
   */
  public Node getRootNode();

  /**
   * Retrieve all of the children for a given Node, traversing all related
   * children (downward looking).
   * 
   * @param parent
   * @return
   */
  public List getTransitiveChildren(Node parent);

  /**
   * Retrieve all of the parents for a given Node, traversing all related
   * parents (upward looking).  Results <i>will</i> include the Node passed as  
   * an argument to this method.
   * 
   * @param child
   * @return
   */
  public List getTransitiveParents(Node child);

  /**
   * Constant that defines the Type for an Organization Node. Organization Nodes
   * tend to be the root of the SuperStructure and represent the local
   * institution (e.g. O=Sakai).
   * 
   * @return
   */
  public Type getOrganizationNodeType();

  /**
   * Constant that defines the Type for an Organizational Unit Node. 
   * Organizational Unit Nodes should be used as a "generic" container below an
   * Organization Node. Organizational Units provide the basic means of creating
   * structure below the Organization root Node(s) and are usually modeled in a 
   * way that represents departments within an institution 
   * (e.g. O=Timbuktu University, OU=Human Resources).
   * 
   * @return
   */
  public Type getOrganizationalUnitNodeType();

}

/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/superstructure-api/src/java/org/sakaiproject/api/common/superstructure/SuperStructureManager.java,v 1.1 2005/05/10 21:23:25 lance.indiana.edu Exp $
 *
 **********************************************************************************/
