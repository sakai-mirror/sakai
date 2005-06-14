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
 * $Header: /cvs/help/component/src/java/org/sakaiproject/component/help/model/SourceBean.java,v 1.2 2004/12/10 15:50:39 casong.indiana.edu Exp $
 * $Revision: 1.2 $
 * $Date: 2004/12/10 15:50:39 $
 */
package org.sakaiproject.component.help.model;

import org.sakaiproject.component.help.model.IdentifiableObject;
import org.sakaiproject.service.help.Source;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents a Source for a Resource.  Examples of sources might be an
 * internal knowledge base, free external sources, etc.  Sources have attributes.
 * An example of when attributes are useful would be when a source is secure and
 * credentials need to be passed to load the resource.
 *
 * @see org.sakaiproject.component.help.model.UrlAppender
 */
public class SourceBean extends IdentifiableObject implements Source {
   private Map attributes = new HashMap();
   private String name;
   private Set urlAppenders;

   public Map getAttributes() {
      return attributes;
   }

   public void setAttributes(Map attributes) {
      this.attributes = attributes;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Set getUrlAppenders() {
      return urlAppenders;
   }

   public void setUrlAppenders(Set urlAppenders) {
      this.urlAppenders = urlAppenders;
   }
}
