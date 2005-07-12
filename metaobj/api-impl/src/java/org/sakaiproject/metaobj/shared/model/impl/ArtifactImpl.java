package org.sakaiproject.metaobj.shared.model.impl;

import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;

public class ArtifactImpl implements Artifact{
   private Agent owner;
   private Id id;
   private ReadableObjectHome home;
   private String displayName;

   public Agent getOwner() {
      return owner;
   }

   public void setOwner(Agent owner) {
      this.owner = owner;
   }

   public Id getId() {
      return id;
   }

   public void setId(Id id) {
      this.id = id;
   }

   public ReadableObjectHome getHome() {
      return home;
   }

   public void setHome(ReadableObjectHome home) {
      this.home = home;
   }

   public String getDisplayName() {
      return displayName;
   }

   public void setDisplayName(String displayName) {
      this.displayName = displayName;
   }
}
