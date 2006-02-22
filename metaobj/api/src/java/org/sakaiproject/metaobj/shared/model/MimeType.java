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
package org.sakaiproject.metaobj.shared.model;


/**
 * @author rpembry
 *         <p/>
 *         <p/>
 *         Based roughly on javax.activation.MimeType, which is
 *         "A Multipurpose Internet Mail Extension (MIME) type, as defined in RFC 2045 and 2046."
 */
public class MimeType {

   public final static MimeType MIMETYPE_PDF = new MimeType("application", "pdf");

   private String primaryType;
   private String subType;

   public MimeType() {
      ;
   }

   public MimeType(String rawdata) {
      setValue(rawdata);
   }

   public MimeType(String primaryType, String subType) {
      this.primaryType = primaryType;
      this.subType = subType;
   }

   public void setValue(String value) {
      String[] parts = value.split("/");
      this.primaryType = parts[0];
      if (parts.length > 1) {
         this.subType = parts[1];
      }
   }

   /**
    * @return Returns the primaryType.
    */
   public String getPrimaryType() {
      return primaryType;
   }

   /**
    * @param primaryType The primaryType to set.
    */
   public void setPrimaryType(String primaryType) {
      this.primaryType = primaryType;
   }

   /**
    * @return Returns the subType.
    */
   public String getSubType() {
      return subType;
   }

   /**
    * @param subType The subType to set.
    */
   public void setSubType(String subType) {
      this.subType = subType;
   }

   public String getDescription() {
      return getValue();
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
   */
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (!(o instanceof MimeType)) {
         return false;
      }

      final MimeType mimeType = (MimeType) o;

      if (primaryType != null ? !primaryType.equals(mimeType.primaryType) : mimeType.primaryType != null) {
         return false;
      }
      if (subType != null ? !subType.equals(mimeType.subType) : mimeType.subType != null) {
         return false;
      }

      return true;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   public int hashCode() {
      return this.getValue().hashCode();
   }

   public String getValue() {
      return this.primaryType + "/" + this.subType;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   public String toString() {
      return this.getValue();
   }
}
