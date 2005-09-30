/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/common-composite-component-data/src/java/org/sakaiproject/component/common/manager/TypeablePersistableImpl.java,v 1.1 2005/05/11 15:41:15 lance.indiana.edu Exp $
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.common.type.Type;
import org.sakaiproject.api.common.type.Typeable;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id: TypeablePersistableImpl.java,v 1.1 2005/05/11 15:41:15 lance.indiana.edu Exp $
 */
public abstract class TypeablePersistableImpl extends PersistableImpl implements
    Typeable
{
  private static final Log LOG = LogFactory
      .getLog(TypeablePersistableImpl.class);

  private static final String NULL = "null";
  private static final String DOT = ".";

  protected Type type;

  /**
   * @see org.sakaiproject.api.common.manager.Typeable#getType()
   */
  public Type getType()
  {
    LOG.trace("getType()");

    return type;
  }

  /**
   * @see org.sakaiproject.api.common.manager.Typeable#setType(org.sakaiproject.api.common.type.Type)
   */
  public void setType(Type type)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setType(Type " + type + ")");
    }
    if (type == null)
      throw new IllegalArgumentException("Illegal type argument passed!");

    this.type = type;
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    LOG.trace("toString()");

    StringBuffer sb = new StringBuffer();
    sb.append(super.toString());
    sb.append(", type=");
    if (type != null)
    {
      sb.append(type.getAuthority());
      sb.append(DOT);
      sb.append(type.getDomain());
      sb.append(DOT);
      sb.append(type.getKeyword());
    }
    else
    {
      sb.append(NULL);
    }
    return sb.toString();
  }

}

/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/common-composite-component-data/src/java/org/sakaiproject/component/common/manager/TypeablePersistableImpl.java,v 1.1 2005/05/11 15:41:15 lance.indiana.edu Exp $
 *
 **********************************************************************************/
