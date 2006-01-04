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

package org.sakaiproject.component.common.manager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.common.superstructure.Node;
import org.sakaiproject.api.common.superstructure.NodeAware;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id$
 */
public abstract class NodeAwareTypeablePersistableImpl extends
    TypeablePersistableImpl implements NodeAware
{
  private static final Log LOG = LogFactory
      .getLog(NodeAwareTypeablePersistableImpl.class);

  protected Node node;

  /**
   * @see org.sakaiproject.api.common.superstructure.NodeAware#getNode()
   */
  public Node getNode()
  {
    LOG.trace("getNode()");

    return node;
  }

  /**
   * @param node The node to set.
   */
  public void setNode(Node node)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setNode(Node " + node + ")");
    }
    if (node == null)
      throw new IllegalArgumentException("Illegal node argument passed!");

    this.node = node;
  }

}



