/*
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/shared/model/SchemaFile.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision$
 * $Date$
 */
package org.sakaiproject.metaobj.shared.model;

public class SchemaFile extends IdentifiableObject {
   private String documentRoot;

   public String getDocumentRoot() {
      return documentRoot;
   }

   public void setDocumentRoot(String documentRoot) {
      this.documentRoot = documentRoot;
   }
}
