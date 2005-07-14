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
      for (Iterator j=getHomes().entrySet().iterator();j.hasNext();){
         ReadableObjectHome home = (ReadableObjectHome) j.next();
         home.refresh();
         homesByExternalId.put(home.getExternalType(), home);
      }
   }

   public ReadableObjectHome findHomeByExternalId(String externalId, Id worksiteId) {
      if (homesByExternalId == null) {
         synchronized(homesByExternalIdLock) {
            if (homesByExternalId == null) {
               homesByExternalId = new Hashtable();
               for (Iterator j=getHomes().entrySet().iterator();j.hasNext();){
                  Map.Entry entry = (Map.Entry)j.next();
                  ReadableObjectHome home = (ReadableObjectHome)entry.getValue();
                  homesByExternalId.put(home.getExternalType(), home);
               }
            }
         }
      }
      return (ReadableObjectHome)homesByExternalId.get(externalId);
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
