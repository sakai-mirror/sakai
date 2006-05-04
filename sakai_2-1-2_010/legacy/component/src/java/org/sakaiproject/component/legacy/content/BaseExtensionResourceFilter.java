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
 * This class implements the typical mime type and extension filter.
 * This will be a registered bean with the component manager that
 * application components can extend to control the list of mime types and
 * the list of acceptable extentions.
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

   /**
    * The list of mime types to allow.  The passed in content resource
    * will be tested to see if the resouce's primary mime type is included in the
    * list (ie "text" for "text/xml") and then the whole mime type will be tested for
    * existence in the list.
    * @param mimeTypes
    */
   public void setMimeTypes(List mimeTypes) {
      this.mimeTypes = mimeTypes;
   }

   public boolean isViewAll() {
      return viewAll;
   }

   /**
    * boolean to indicate if all resources should be viewable.
    *
    * If this is false, then the viewable resources will be based on the
    * mime types and extention set in the other properties.
    * @param viewAll
    */
   public void setViewAll(boolean viewAll) {
      this.viewAll = viewAll;
   }

   public List getAcceptedExtensions() {
      return acceptedExtensions;
   }

   /**
    * List of accepted file name extensions.  If this list is null,
    * all extensions are acceptable.
    * @param acceptedExtensions
    */
   public void setAcceptedExtensions(List acceptedExtensions) {
      this.acceptedExtensions = acceptedExtensions;
   }
}
