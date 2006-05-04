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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.xml.SchemaFactory;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 9, 2004
 * Time: 12:46:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class HomeFactoryImpl implements HomeFactory {
   protected final Log logger = LogFactory.getLog(getClass());
   private List homeFactories = null;

   public boolean handlesType(String objectType) {

      for (Iterator i = homeFactories.iterator(); i.hasNext();) {
         if (((HomeFactory) i.next()).handlesType(objectType)) {
            return true;
         }
      }

      return false;
   }

   public ReadableObjectHome getHome(String objectType) {

      for (Iterator i = homeFactories.iterator(); i.hasNext();) {
         HomeFactory testFactory = (HomeFactory) i.next();
         if (testFactory.handlesType(objectType)) {
            return testFactory.getHome(objectType);
         }
      }

      return null;
   }

   public ReadableObjectHome findHomeByExternalId(String externalId, Id worksiteId) {

      for (Iterator i = getHomeFactories().iterator(); i.hasNext();) {
         ReadableObjectHome home = ((HomeFactory) i.next()).findHomeByExternalId(externalId, worksiteId);
         if (home != null) {
            return home;
         }
      }

      return null;
   }

   public Map getHomes() {
      Map homes = new Hashtable();

      for (Iterator i = getHomeFactories().iterator(); i.hasNext();) {
         homes.putAll(((HomeFactory) i.next()).getHomes());
      }

      return homes;
   }

   public Map getWorksiteHomes(Id worksiteId) {
      Map homes = new Hashtable();

      for (Iterator i = getHomeFactories().iterator(); i.hasNext();) {
         homes.putAll(((HomeFactory) i.next()).getWorksiteHomes(worksiteId));
      }

      return homes;
   }

   public Map getHomes(Class requiredHomeType) {
      Map homes = new Hashtable();

      for (Iterator i = getHomeFactories().iterator(); i.hasNext();) {
         homes.putAll(((HomeFactory) i.next()).getHomes(requiredHomeType));
      }

      return homes;
   }

   public void reload() {
      SchemaFactory.getInstance().reload();
      for (Iterator j = getHomes().values().iterator(); j.hasNext();) {
         ReadableObjectHome home = (ReadableObjectHome) j.next();
         home.refresh();
      }
   }

   public List getHomeFactories() {
      return homeFactories;
   }

   public void setHomeFactories(List homeFactories) {
      this.homeFactories = homeFactories;
   }
}
