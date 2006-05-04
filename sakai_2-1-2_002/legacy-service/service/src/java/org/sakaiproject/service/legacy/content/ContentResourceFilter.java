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
package org.sakaiproject.service.legacy.content;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 26, 2005
 * Time: 3:18:31 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ContentResourceFilter {

   /**
    * Implement this method to control which resources are allowed
    * to be selected.  Implementation should inspect the resource and
    * return true if the resource should be selectable and false if not.
    * @param contentResource resource to test
    * @return true if resource should be selectable, false if not
    */
   public boolean allowSelect(ContentResource contentResource);

   /**
    * Implement this method to control which resources are viewable.
    * Implementation should inspect the resource and
    * return true if the resource should be presented in the list
    * and false if not.
    * @param contentResource resource to test
    * @return true if resource should be viewable, false if not
    */
   public boolean allowView(ContentResource contentResource);
}