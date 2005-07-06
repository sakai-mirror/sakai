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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/mgt/impl/FileArtifactFinderImpl.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/29 18:36:41 $
 */
/*
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/mgt/impl/FileArtifactFinderImpl.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/29 18:36:41 $
 */
package org.sakaiproject.metaobj.repository.mgt.impl;

import org.sakaiproject.metaobj.repository.RepositoryConstants;
import org.sakaiproject.metaobj.repository.mgt.FileArtifactFinder;
import org.sakaiproject.metaobj.repository.model.NodeMetadata;
import org.sakaiproject.metaobj.repository.model.impl.FileArtifactImpl;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.Id;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class FileArtifactFinderImpl extends ArtifactFinderImpl implements FileArtifactFinder {
   private HomeFactory homeFactory;

   public Collection findByOwnerMimeTypeAndExtension(Id owner, String mimeType, String extension) {
      String primaryMimeType = null;
      String subMimeType = null;

      if (mimeType != null) {
         int pos = mimeType.indexOf('/');
         if (pos != -1) {
            primaryMimeType = mimeType.substring(0, pos);
            subMimeType = mimeType.substring(pos + 1);
         }
      }

      Collection wrList = getHibernateTemplate().find("from NodeMetadata w where owner_id=? and primaryMimeType=? and subMimeType=? and name like '%." + extension + "'",
         new Object[]{owner.getValue(), primaryMimeType, subMimeType});
      return getArtifacts(wrList);
   }

   public Collection findByOwnerPrimaryMimeType(Id owner, String primaryMimeType) {
      Collection wrList = getHibernateTemplate().find("from NodeMetadata w where owner_idd=? and primaryMimeType=?",
         new Object[]{owner.getValue(), primaryMimeType});
      return getArtifacts(wrList);
   }

   public Collection findByOwnerMimeType(Id owner, String mimeType) {
      String primaryMimeType = null;
      String subMimeType = null;

      if (mimeType != null) {
         int pos = mimeType.indexOf('/');
         if (pos != -1) {
            primaryMimeType = mimeType.substring(0, pos);
            subMimeType = mimeType.substring(pos + 1);
         }
      }

      Collection wrList = getHibernateTemplate().find("from NodeMetadata w where owner_id=? and primaryMimeType=? and subMimeType=?",
         new Object[]{owner.getValue(), primaryMimeType, subMimeType});
      return getArtifacts(wrList);
   }

   /**
    * loads and builds a collection of artifacts from a NodeMetadata collection
    *
    * @param wrList
    * @return
    */
   protected Collection getArtifacts(Collection wrList) {
      Collection artifacts = new ArrayList();
      for (Iterator i = wrList.iterator(); i.hasNext();) {
         NodeMetadata wr = (NodeMetadata) i.next();

         if (RepositoryConstants.FOLDER_TYPE.getId().equals(wr.getType().getId())) {
            continue;
         }

         if (getLoadArtifacts()){
            artifacts.add(loadResource(wr));
         } else {
            artifacts.add(convertToArtifact(wr));
         }
      }
      return artifacts;
   }

   protected Artifact convertToArtifact(NodeMetadata resource){
      FileArtifactImpl artifact = new FileArtifactImpl();
      artifact.setArtifactId(resource.getId());
      artifact.setMimeType(resource.getMimeTypeObject());
      artifact.setDisplayName(resource.getName());
      return artifact;
   }

   public HomeFactory getHomeFactory() {
      return homeFactory;
   }

   public void setHomeFactory(HomeFactory homeFactory) {
      this.homeFactory = homeFactory;
   }

}
