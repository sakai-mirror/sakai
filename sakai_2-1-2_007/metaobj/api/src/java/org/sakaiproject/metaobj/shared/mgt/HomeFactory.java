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
package org.sakaiproject.metaobj.shared.mgt;

import org.sakaiproject.metaobj.shared.model.Id;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 9, 2004
 * Time: 12:44:49 PM
 * To change this template use File | Settings | File Templates.
 */
public interface HomeFactory {

   /**
    * Check to see if this home factory is responsible for the passed in object type
    *
    * @param objectType
    * @return true if this home factory handles the specified object type
    */
   public boolean handlesType(String objectType);

   /**
    * Get a home for the given object type.  The returned home may support a number of interfaces
    * depending on the features of this home.  At a minimum, the home must support
    * ReadableObjectHome interface.
    *
    * @param objectType
    * @return a home suitable for reading the object, but it may support other home interfaces
    */
   public ReadableObjectHome getHome(String objectType);

   /**
    * Find a home by an external id.  This id should be unique and naturally occuring.
    * This is used for matching up a home imported from another worksite or system when importing
    * things that use Homes (ie. presentation templates, matrices, etc)
    *
    * @param externalId naturally occuring id (like the document root and system id of an xml document)
    * @param worksiteId The worksite to import it into or null for global import
    * @return the home if found or null.
    */
   public ReadableObjectHome findHomeByExternalId(String externalId, Id worksiteId);

   /**
    * @param worksiteId
    * @return a map with all worksite and global homes
    */
   public Map getWorksiteHomes(Id worksiteId);

   /**
    * Map of all homes.  This map will map the object type as a String to the home as a ReadableObjectHome.
    * The home may support more features.  This can be determined by checking instanceof on other home interfaces
    *
    * @return map of object type to home
    */
   public Map getHomes();

   /**
    * Map of certain homes.  This map will map the object type as a String to the home as a ReadableObjectHome.
    * The home may support more features.  This can be determined by checking instanceof on other home interfaces.
    * <p/>
    * All the homes returned will implement the requiredHomeType interface.  This method can be used to get
    * homes that support certain features.
    *
    * @param requiredHomeType interface that all returned homes will be an implementation of
    * @return map of object type to home
    */
   public Map getHomes(Class requiredHomeType);

   /**
    * forces reloading of any cached homes.  Allows configuration
    * changes to occur at runtime.
    */
   public void reload();

}
