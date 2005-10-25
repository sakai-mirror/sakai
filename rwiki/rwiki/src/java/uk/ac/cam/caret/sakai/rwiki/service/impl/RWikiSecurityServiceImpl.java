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
package uk.ac.cam.caret.sakai.rwiki.service.impl;

import javax.servlet.ServletRequest;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.portal.PortalService;
import org.sakaiproject.service.legacy.security.SecurityService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SiteService;

import uk.ac.cam.caret.sakai.rwiki.exception.PermissionException;
import uk.ac.cam.caret.sakai.rwiki.service.RWikiSecurityService;

/**
 * @author andrew
 *
 */
public class RWikiSecurityServiceImpl implements RWikiSecurityService {
    private Logger log;

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


    private PortalService portalService;
    
    private SecurityService securityService;
    
    private SiteService siteService;
    
    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.RWikiSecurityService#getRealm(javax.servlet.http.HttpServletRequest)
     */
    public String getRealm(ServletRequest request) {
        try {
            Site currentSite = siteService.getSite(portalService.getCurrentSiteId());
            return currentSite.getReference();
        } catch (IdUnusedException e) {
            throw new PermissionException("You must access the RWiki through a proper site");
        }
    }
    
    public String getSiteId() {
        return portalService.getCurrentSiteId();
    }
    
    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.RWikiSecurityService#checkGetPermission(java.lang.String, java.lang.String)
     */
    public boolean checkGetPermission(String user, String realm) {
        return (securityService.unlock(SECURE_READ,realm));
    }

    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.RWikiSecurityService#checkUpdatePermission(java.lang.String, java.lang.String)
     */
    public boolean checkUpdatePermission(String user, String realm) {
        return (securityService.unlock(SECURE_UPDATE, realm));
    }

    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.RWikiSecurityService#checkAdminPermission(java.lang.String, java.lang.String)
     */
    public boolean checkAdminPermission(String user, String realm) {
        return securityService.unlock(SECURE_ADMIN, realm);
    }

    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.RWikiSecurityService#checkSuperAdminPermission(java.lang.String, java.lang.String)
     */
    public boolean checkSuperAdminPermission(String user, String realm) {
        return securityService.unlock(SECURE_SUPER_ADMIN, realm);
    }

    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.RWikiSecurityService#checkCreatePermission(java.lang.String, java.lang.String)
     */
    public boolean checkCreatePermission(String user, String realm) {
        return securityService.unlock(SECURE_CREATE, realm);
    }

    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.RWikiSecurityService#checkSearchPermission(java.lang.String, java.lang.String)
     */
    public boolean checkSearchPermission(String user, String realm) {
        return securityService.unlock(SECURE_READ, realm);
    }

    public PortalService getPortalService() {
        return portalService;
    }

    public void setPortalService(PortalService portalService) {
        this.portalService = portalService;
    }

    public SecurityService getSecurityService() {
        return securityService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public SiteService getSiteService() {
        return siteService;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}

}
