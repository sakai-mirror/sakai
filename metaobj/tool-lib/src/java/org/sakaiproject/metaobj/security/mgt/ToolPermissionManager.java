/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/trunk/sakai/legacy-service/service/src/java/org/sakaiproject/exception/InconsistentException.java $
 * $Id: InconsistentException.java 632 2005-07-14 21:22:50Z janderse@umich.edu $
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
package org.sakaiproject.metaobj.security.mgt;

import org.sakaiproject.metaobj.security.model.PermissionsEdit;
import org.sakaiproject.service.legacy.site.ToolConfiguration;

import java.util.List;

public interface ToolPermissionManager {

   /**
    * Get a list of functions that this tool is interested in setting.
    * This list should be in some reasonable order (read to delete, etc).
    *
    * @param edit contains information about the permissions edit such as
    *             qualifier, etc.
    * @return list of strings that name the functions in some reasonable order
    */
   public List getFunctions(PermissionsEdit edit);

   /**
    * This method is called to see if the qualifier being edited
    * has some parent qualifiers that imply permissions for this qualifier.
    * One example might be a directory that has implied permissions of
    * the parent directory.  Since the permissions are implied, the
    * set permissions screen will not allow these permissions to be turned off.
    *
    * @param edit contains information about the permissions edit such as
    *             qualifier, etc.
    * @return list of Id objects that are parents of the passed in qualifier.
    */
   public List getReadOnlyQualifiers(PermissionsEdit edit);

   public void duplicatePermissions(ToolConfiguration fromTool, ToolConfiguration toTool);

}
