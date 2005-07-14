/*
 * The Open Source Portfolio Initiative Software is Licensed under the Educational Community License Version 1.0:
 *
 * This Educational Community License (the "License") applies to any original work of authorship
 * (the "Original Work") whose owner (the "Licensor") has placed the following notice immediately
 * following the copyright notice for the Original Work:
 *
 * Copyright (c) 2004 Trustees of Indiana University and r-smart Corporation
 *
 * This Original Work, including software, source code, documents, or other related items, is being
 * provided by the copyright holder(s) subject to the terms of the Educational Community License.
 * By obtaining, using and/or copying this Original Work, you agree that you have read, understand,
 * and will comply with the following terms and conditions of the Educational Community License:
 *
 * Permission to use, copy, modify, merge, publish, distribute, and sublicense this Original Work and
 * its documentation, with or without modification, for any purpose, and without fee or royalty to the
 * copyright holder(s) is hereby granted, provided that you include the following on ALL copies of the
 * Original Work or portions thereof, including modifications or derivatives, that you make:
 *
 * - The full text of the Educational Community License in a location viewable to users of the
 * redistributed or derivative work.
 *
 * - Any pre-existing intellectual property disclaimers, notices, or terms and conditions.
 *
 * - Notice of any changes or modifications to the Original Work, including the date the changes were made.
 *
 * - Any modifications of the Original Work must be distributed in such a manner as to avoid any confusion
 *  with the Original Work of the copyright holders.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) may NOT be used in advertising or publicity pertaining
 * to the Original or Derivative Works without specific, written prior permission. Title to copyright
 * in the Original Work and any associated documentation will at all times remain with the copyright holders.
 *
 * $URL$
 * $Revision$
 * $Date$
 */
package org.sakaiproject.metaobj.shared.mgt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.utils.xml.SchemaFactory;
import org.sakaiproject.metaobj.shared.model.Id;

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
         ReadableObjectHome home = ((HomeFactory)i.next()).findHomeByExternalId(externalId, worksiteId);
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
      for (Iterator j= getHomes().values().iterator();j.hasNext();){
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
