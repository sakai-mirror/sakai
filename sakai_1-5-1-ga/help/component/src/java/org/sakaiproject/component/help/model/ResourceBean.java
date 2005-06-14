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
 * $Header: /cvs/help/component/src/java/org/sakaiproject/component/help/model/ResourceBean.java,v 1.4 2005/02/28 23:36:40 rshastri.iupui.edu Exp $
 * $Revision: 1.4 $
 * $Date: 2005/02/28 23:36:40 $
 */
package org.sakaiproject.component.help.model;

import org.sakaiproject.component.help.model.IdentifiableObject;
import org.sakaiproject.service.help.Resource;
import org.sakaiproject.service.help.Source;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

/**
 * A resource represents an external url and metadata about that resource.
 * This metadata includes: which contexts the resource is associated with,
 * and what source (if any) the resource is associated with.
 *
 * @see org.theospi.portfolio.help.model.SourceBean 
 */
public class ResourceBean extends IdentifiableObject implements Resource, Comparable{
   private String docId;
   private String name;
   private Set contexts = new HashSet();
   private String location;
   private Source source;
   private float score;
   private String formattedScore;

  
  /**
   * @return Returns the formattedScore.
   */
  public String getFormattedScore()
  {
    formattedScore = String.valueOf(score);
    int index = formattedScore.indexOf(".");
    formattedScore = formattedScore.substring(0, index+2);
    return formattedScore+ "%";
  }
  /**
   * @return Returns the docId.
   */
  public String getDocId()
  {
    return docId;
  }
  /**
   * @param docId The docId to set.
   */
  public void setDocId(String docId)
  {
    this.docId = docId;
  }
  
   public String getLocation() {
      return location;
   }

   public void setLocation(String location) {
      this.location = location;
   }

   public Set getContexts() {
      return contexts;
   }

   public void setContexts(Set contexts) {
      this.contexts = contexts;
   }

   public URL getUrl() throws MalformedURLException {
      if (getSource() != null && getSource().getUrlAppenders().size() > 0){
         URL url = null;
         for (Iterator i=getSource().getUrlAppenders().iterator();i.hasNext();){
            if (url == null){
               url = new URL(getLocation());
            }
            UrlAppender urlAppender = (UrlAppender) i.next();
            url = urlAppender.getUrl(this, url);
         }
         return url;
      }
      return new URL(getLocation());
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Source getSource() {
      return source;
   }

   public void setSource(Source source) {
      this.source = source;
   }

   public float getScore() {
      return score;
   }

   public void setScore(float score) {
      this.score = score;
   }
   
   
   private String businessKey()
   {
     StringBuffer sb = new StringBuffer();
     sb.append(docId);
     return sb.toString();
   }
   
   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object obj)
   {
     if (this == obj) return true;
     if (!(obj instanceof ResourceBean)) return false;
     ResourceBean other = (ResourceBean) obj;
     return this.businessKey().equals(other.businessKey());
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   public int hashCode()
   {
     return businessKey().hashCode();
   }

   /**
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
     StringBuffer sb = new StringBuffer();
     sb.append("docId=");
     sb.append(docId);
     sb.append(", name=");
     sb.append(name);
     sb.append(", location=");
     sb.append(location);
     return sb.toString();
   }
  /* (non-Javadoc)
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(Object object)
  {
    ResourceBean resourceBean = (ResourceBean)object;
    return Float.compare(resourceBean.score, score);
  }
}

