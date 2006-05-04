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
package org.sakaiproject.metaobj.shared.mgt.factories;

import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.model.Id;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 14, 2004
 * Time: 4:22:36 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class HomeFactoryBase implements HomeFactory {

   private Map homesByExternalId = null;
   private Object homesByExternalIdLock = new Object();

   public void reload() {
      if (homesByExternalId == null) {
         homesByExternalId = new Hashtable();
      }
      homesByExternalId.clear();
      for (Iterator j = getHomes().entrySet().iterator(); j.hasNext();) {
         ReadableObjectHome home = (ReadableObjectHome) j.next();
         home.refresh();
         homesByExternalId.put(home.getExternalType(), home);
      }
   }

   public ReadableObjectHome findHomeByExternalId(String externalId, Id worksiteId) {
      if (homesByExternalId == null) {
         synchronized (homesByExternalIdLock) {
            if (homesByExternalId == null) {
               homesByExternalId = new Hashtable();
               for (Iterator j = getHomes().entrySet().iterator(); j.hasNext();) {
                  Map.Entry entry = (Map.Entry) j.next();
                  ReadableObjectHome home = (ReadableObjectHome) entry.getValue();
                  homesByExternalId.put(home.getExternalType(), home);
               }
            }
         }
      }
      return (ReadableObjectHome) homesByExternalId.get(externalId);
   }

   protected void addHome(ReadableObjectHome newHome) {
      homesByExternalId.put(newHome.getExternalType(), newHome);
   }

   public Map getHomes(Class requiredHomeType) {
      Map newMap = new Hashtable();
      Map homes = getHomes();

      for (Iterator i = homes.keySet().iterator(); i.hasNext();) {
         Object key = i.next();

         if (requiredHomeType.isInstance(homes.get(key))) {
            newMap.put(key, homes.get(key));
         }
      }

      return newMap;
   }

   public Map getWorksiteHomes(Id worksiteId) {
      return getHomes();
   }

}
