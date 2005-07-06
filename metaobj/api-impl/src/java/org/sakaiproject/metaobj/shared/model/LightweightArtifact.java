/**********************************************************************************
 *
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/shared/model/LightweightArtifact.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 *
 ***********************************************************************************
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.sakaiproject.metaobj.shared.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.repository.model.NodeMetadata;

public class LightweightArtifact implements Artifact {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private NodeMetadata nodeMetadata;
   private ReadableObjectHome home;

   public LightweightArtifact(ReadableObjectHome home, NodeMetadata nodeMetadata) {
      this.home = home;
      this.nodeMetadata = nodeMetadata;
   }

   public String getDisplayName() {
      return nodeMetadata.getName();
   }

   public ReadableObjectHome getHome() {
      return home;
   }

   public Id getId() {
      return nodeMetadata.getId();
   }

   public Agent getOwner() {
      return nodeMetadata.getOwner();
   }

   public void setHome(ReadableObjectHome home) {
      this.home = home;
   }
}
