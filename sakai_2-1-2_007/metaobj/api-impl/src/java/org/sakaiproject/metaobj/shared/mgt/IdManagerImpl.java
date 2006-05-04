/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
package org.sakaiproject.metaobj.shared.mgt;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdImpl;
import org.sakaiproject.metaobj.utils.id.guid.Guid;

/**
 * @author rpembry
 */
public class IdManagerImpl implements IdManager {
   /* (non-Javadoc)
    * @see org.sakaiproject.metaobj.shared.IdManager#getInstance()
    */
   public IdManager getInstance() {
//TODO: retrieve Singleton from Spring?
      return new IdManagerImpl();
   }

   /* (non-Javadoc)
    * @see org.sakaiproject.metaobj.shared.IdManager#getId(java.lang.String)
    */
   public Id getId(String id) {
      //return new org.sakaiproject.metaobj.shared.model.IdImpl(id);
      return new IdImpl(id, null);
   }

   /* (non-Javadoc)
    * @see org.sakaiproject.metaobj.shared.IdManager#createId()
    */
   public Id createId() {
//TODO: delegate to OKI impl here
      return getId(new Guid().getString());
   }


}
