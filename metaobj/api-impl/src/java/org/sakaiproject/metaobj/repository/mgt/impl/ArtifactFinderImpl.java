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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/mgt/impl/ArtifactFinderImpl.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/29 18:36:41 $
 */
/*
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/mgt/impl/ArtifactFinderImpl.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/29 18:36:41 $
 */
package org.sakaiproject.metaobj.repository.mgt.impl;

import org.sakaiproject.metaobj.shared.ArtifactFinder;
import org.sakaiproject.metaobj.repository.impl.NodeMetadataServiceImpl;
import org.sakaiproject.metaobj.repository.model.NodeMetadata;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.OspException;
import org.sakaiproject.metaobj.shared.model.PersistenceException;
import org.sakaiproject.metaobj.shared.model.impl.ArtifactImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class ArtifactFinderImpl extends NodeMetadataServiceImpl implements ArtifactFinder {
   private HomeFactory homeFactory;
   private boolean loadArtifacts = true;

   public Collection findByOwnerAndType(Id owner, String type) {
      Collection wrList = getHibernateTemplate().find("from NodeMetadata w where owner_id=? and typeId=?",
         new Object[]{owner.getValue(), type});
      return getArtifacts(wrList);
   }

   public Collection findByOwner(Id owner) {
      Collection wrList = getHibernateTemplate().find("from NodeMetadata w where owner_id=?",
         new Object[]{owner.getValue()});
      return getArtifacts(wrList);
   }

   public Collection findByWorksiteAndType(Id worksite, String type) {
      Collection wrList = getHibernateTemplate().find("from NodeMetadata w where worksiteId=? and typeId=?",
         new Object[]{worksite.getValue(), type});
      return getArtifacts(wrList);
   }

   public Collection findByWorksite(Id worksite) {
      Collection wrList = getHibernateTemplate().find("from NodeMetadata w where worksiteId=?",
         new Object[]{worksite.getValue()});
      return getArtifacts(wrList);
   }

   public Collection findByType(String type) {
      Collection wrList = getHibernateTemplate().find("from NodeMetadata w where typeId=?",
         new Object[]{type});
      return getArtifacts(wrList);
   }

   /**
    * return null if not found
    * @param artifactId
    * @return
    */
   public Artifact load(Id artifactId) {
      NodeMetadata resource = getNode(artifactId);
      if (resource == null) return null;
      return loadResource(resource);
   }

   public Collection findAll() {
      //TODO add authz
      return getHibernateTemplate().find("from NodeMetadata");
   }

   protected Artifact loadResource(NodeMetadata resource) {
      ReadableObjectHome home = getHomeFactory().getHome(resource.getType().getId().getValue());
      try {
         return home.load(resource.getId());
      } catch (PersistenceException e) {
         throw new OspException(e);
      }
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
         if (getLoadArtifacts()){
            artifacts.add(loadResource(wr));
         } else {
            artifacts.add(convertToArtifact(wr));
         }
      }
      return artifacts;
   }

   protected Artifact convertToArtifact(NodeMetadata resource){
      ArtifactImpl artifact = new ArtifactImpl();
      artifact.setId(resource.getId());
      artifact.setDisplayName(resource.getName());
      return artifact;
   }

   public HomeFactory getHomeFactory() {
      return homeFactory;
   }

   public void setHomeFactory(HomeFactory homeFactory) {
      this.homeFactory = homeFactory;
   }

   public boolean getLoadArtifacts() {
      return loadArtifacts;
   }

   public void setLoadArtifacts(boolean loadArtifacts) {
      this.loadArtifacts = loadArtifacts;
   }
}
