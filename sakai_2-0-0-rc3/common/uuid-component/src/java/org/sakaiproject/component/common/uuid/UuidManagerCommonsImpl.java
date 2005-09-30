/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/uuid-component/src/java/org/sakaiproject/component/common/uuid/UuidManagerCommonsImpl.java,v 1.1 2005/05/11 15:42:51 lance.indiana.edu Exp $
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

package org.sakaiproject.component.common.uuid;

import org.apache.commons.id.uuid.VersionFourGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.common.uuid.UuidManager;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id: UuidManagerCommonsImpl.java,v 1.1 2005/05/11 15:42:51 lance.indiana.edu Exp $
 */
public class UuidManagerCommonsImpl implements UuidManager
{
  private static final Log LOG = LogFactory
      .getLog(UuidManagerCommonsImpl.class);

  private static final VersionFourGenerator VFG = new VersionFourGenerator();

  /**
   * @see org.sakaiproject.service.uuid.UuidManager#createId()
   */
  public String createUuid()
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("createUuid()");
    }
    return VFG.nextIdentifier().toString();
  }

  public static void main(String[] args)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("main(String[] " + args + ")");
    }
    UuidManagerCommonsImpl o = new UuidManagerCommonsImpl();
    System.out.println(o.createUuid());
  }
}

/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/uuid-component/src/java/org/sakaiproject/component/common/uuid/UuidManagerCommonsImpl.java,v 1.1 2005/05/11 15:42:51 lance.indiana.edu Exp $
 *
 **********************************************************************************/
