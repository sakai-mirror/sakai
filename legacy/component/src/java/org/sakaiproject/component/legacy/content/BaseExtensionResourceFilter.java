/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
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
package org.sakaiproject.component.legacy.content;

import org.sakaiproject.service.legacy.content.ContentResourceFilter;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.entity.ResourceProperties;

import java.util.List;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 26, 2005
 * Time: 10:20:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseExtensionResourceFilter implements ContentResourceFilter {

   private boolean viewAll = true;
   private List mimeTypes;
   private List acceptedExtensions;

   public boolean allowSelect(ContentResource resource) {
      String mimeType = resource.getProperties().getProperty(ResourceProperties.PROP_CONTENT_TYPE);
      String[] parts = mimeType.split("/");
      String primaryType = parts[0];

      if (!getMimeTypes().contains(primaryType) && !getMimeTypes().contains(mimeType)) {
         return false;
      }

      String filePath = resource.getUrl();

      if (getAcceptedExtensions() != null) {
         // check extension
         for (Iterator i=getAcceptedExtensions().iterator();i.hasNext();) {
            if (filePath.endsWith("." + i.next().toString().toLowerCase())) {
               return true;
            }
         }

         return false;
      }
      else {
         return true;
      }
   }

   public boolean allowView(ContentResource contentResource) {
      if (isViewAll()) {
         return true;
      }

      return allowSelect(contentResource);
   }

   public List getMimeTypes() {
      return mimeTypes;
   }

   public void setMimeTypes(List mimeTypes) {
      this.mimeTypes = mimeTypes;
   }

   public boolean isViewAll() {
      return viewAll;
   }

   public void setViewAll(boolean viewAll) {
      this.viewAll = viewAll;
   }

   public List getAcceptedExtensions() {
      return acceptedExtensions;
   }

   public void setAcceptedExtensions(List acceptedExtensions) {
      this.acceptedExtensions = acceptedExtensions;
   }
}
