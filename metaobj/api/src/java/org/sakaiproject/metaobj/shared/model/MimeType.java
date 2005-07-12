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
 * $Header: /opt/CVS/osp2.x/HomesService/src/java/org/theospi/metaobj/shared/model/MimeType.java,v 1.3 2005/06/30 17:34:18 chmaurer Exp $
 * $Revision$
 * $Date$
 */
/*
 * Created on May 19, 2004
 *
 */
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
      if (parts.length > 1) this.subType = parts[1];
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
      if (this == o) return true;
      if (!(o instanceof MimeType)) return false;

      final MimeType mimeType = (MimeType) o;

      if (primaryType != null ? !primaryType.equals(mimeType.primaryType) : mimeType.primaryType != null) return false;
      if (subType != null ? !subType.equals(mimeType.subType) : mimeType.subType != null) return false;

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
