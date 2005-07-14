/*
 * $URL$
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
