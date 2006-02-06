/**********************************************************************************
 *
 * $Header$
 *
 ***********************************************************************************
 *
 * Copyright (c) 2005 University of Cambridge
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
package uk.ac.cam.caret.sakai.rwiki.service.api;

import javax.servlet.ServletRequest;

//FIXME: Service

public interface RWikiSecurityService {

    String getRealm(ServletRequest request);

    boolean checkGetPermission(String user, String realm);

    boolean checkUpdatePermission(String user, String realm);

    boolean checkAdminPermission(String user, String realm);

    boolean checkSuperAdminPermission(String user, String realm);

    boolean checkCreatePermission(String user, String realm);

    boolean checkSearchPermission(String user, String realm);

    String getSiteId();
    
    /** Security function name for create. required to create */
    public static final String SECURE_CREATE = "rwiki.create";

    /** Security function name for read. required to read */
    public static final String SECURE_READ = "rwiki.read";

    /** Security function name for update. required to update */
    public static final String SECURE_UPDATE = "rwiki.update";

    /** Security function name for delete. required to delete */
    public static final String SECURE_DELETE = "rwiki.delete";
    
    /** Security function name for admin. Having this overrides for the site */
    public static final String SECURE_SUPER_ADMIN = "rwiki.superadmin";
    
    /** Security function name for admin. Having this is required to do admin on objects */
    public static final String SECURE_ADMIN = "rwiki.admin";


}
