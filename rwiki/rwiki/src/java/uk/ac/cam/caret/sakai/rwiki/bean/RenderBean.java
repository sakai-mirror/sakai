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
package uk.ac.cam.caret.sakai.rwiki.bean;

import uk.ac.cam.caret.sakai.rwiki.exception.PermissionException;
import uk.ac.cam.caret.sakai.rwiki.model.NameHelper;
import uk.ac.cam.caret.sakai.rwiki.model.RWikiObject;
import uk.ac.cam.caret.sakai.rwiki.model.impl.RWikiCurrentObjectImpl;
import uk.ac.cam.caret.sakai.rwiki.model.impl.RWikiObjectImpl;
import uk.ac.cam.caret.sakai.rwiki.service.RWikiObjectService;
import uk.ac.cam.caret.sakai.rwiki.service.RenderService;

/**
 * Bean that renders the current rwikiObject using a set objectService and
 * renderService.
 * 
 * @author andrew
 */
public class RenderBean {

    private RenderService renderService;

    private RWikiObjectService objectService;

    private String user;

    private RWikiObject rwo;

    // FIXME internationalise this!
    private static final String PERMISSION_PROBLEM = 
        "You do not have permission to view this page";

    /**
     * Create new RenderBean from given RWikiObject
     * 
     * @param rwo
     *            the current RWikiObject
     * @param user
     *            the current user
     * @param renderService
     *            the RenderService
     * @param objectService
     *            the RWikiObjectService
     */
    public RenderBean(RWikiObject rwo, String user,
            RenderService renderService, RWikiObjectService objectService) {
        this.user = user;
        this.rwo = rwo;
        this.renderService = renderService;
        this.objectService = objectService;
    }

    /**
     * Create new RenderBean and get the chosen RWikiObject
     * 
     * @param name
     *            the name of the RWikiObject to render
     * @param user
     *            the current user
     * @param defaultRealm
     *            the defaultRealm to globalise the name with
     * @param renderService
     *            the RenderService
     * @param objectService
     *            the RWikiObjectService
     */
    public RenderBean(String name, String user, String defaultRealm,
            RenderService renderService, RWikiObjectService objectService) {
        this.objectService = objectService;
        this.renderService = renderService;
        this.user = user;
        String pageName = NameHelper.globaliseName(name, defaultRealm);
        String pageRealm = defaultRealm;
        try {
            this.rwo = objectService.getRWikiObject(pageName, user, pageRealm);
        } catch (PermissionException e) {
            this.rwo = new RWikiCurrentObjectImpl();
            rwo.setName(pageName);
            rwo.setContent(PERMISSION_PROBLEM);
        }
    }

    /**
     * Get the RWikiObjectService
     * 
     * @return objectService
     */
    public RWikiObjectService getObjectService() {
        return objectService;
    }

    /**
     * Set the RWikiObjectService for this bean.
     * 
     * @param objectService
     *            The RWikiObjectService to use.
     */
    public void setObjectService(RWikiObjectService objectService) {
        this.objectService = objectService;
    }

    /**
     * Get the RenderService
     * 
     * @return renderService
     */
    public RenderService getRenderService() {
        return renderService;
    }

    /**
     * Set the RenderService for this bean.
     * 
     * @param renderService
     *            the RenderService to use.
     */
    public void setRenderService(RenderService renderService) {
        this.renderService = renderService;

    }
    public String getPreviewPage() {
        return renderService.renderPage(rwo, user, false);
    }

    /**
     * Render the current RWikiObject
     * 
     * @return XHTML as a String representing the content of the current
     *         RWikiObject
     */
    public String getRenderedPage() {
        return renderService.renderPage(rwo, user);
    }
    /**
     * Render the current RWikiObject with public links
     * @return XMTML as a String representing the content of the current
     * RWikiObject
     */
    public String getPublicRenderedPage() {
    		return renderService.renderPublicPage(rwo,user);
    }

    /**
     * Render the current RWikiObject
     * 
     * @return XHTML as a String representing the content of the current
     *         RWikiObject
     */
    public String renderPage() {
        return renderService.renderPage(rwo, user);
    }
    /**
     * Render the current RWikiObject with public links
     * @return XMTML as a String representing the content of the current
     * RWikiObject
     */
    public String publicRenderedPage() {
    		return renderService.renderPublicPage(rwo,user);
    }

    /**
     * Render the rwikiObject represented by the given name and realm
     * 
     * @param name
     *            a possible non-globalised name
     * @param realm
     *            the default realm of that should be globalised against
     * @return XHTML as a String representing the content of the RWikiObject
     */
    public String renderPage(String name, String realm) {
        String pageName = NameHelper.globaliseName(name, realm);
        String linkRealm = realm;
        String pageRealm = NameHelper.localizeSpace(pageName, realm);

        try {
            RWikiObject page = objectService.getRWikiObject(pageName, user,
                    pageRealm);
            return renderService.renderPage(page, user, linkRealm);
        } catch (PermissionException e) {
            RWikiObjectImpl page = new RWikiCurrentObjectImpl();
            page.setName(pageName);
            page.setContent(PERMISSION_PROBLEM);
            return renderService.renderPage(page, user, linkRealm);
        }

    }
    /**
     * Render the rwikiObject as a public page represented by the given name and realm
     * 
     * @param name
     *            a possible non-globalised name
     * @param realm
     *            the default realm of that should be globalised against
     * @return XHTML as a String representing the content of the RWikiObject
     */
    public String publicRenderPage(String name, String realm) {
        String pageName = NameHelper.globaliseName(name, realm);
        String linkRealm = realm;
        String pageRealm = NameHelper.localizeSpace(pageName, realm);

        try {
            RWikiObject page = objectService.getRWikiObject(pageName, user,
                    pageRealm);
            return renderService.renderPublicPage(page, user, linkRealm);
        } catch (PermissionException e) {
            RWikiObjectImpl page = new RWikiCurrentObjectImpl();
            page.setName(pageName);
            page.setContent(PERMISSION_PROBLEM);
            return renderService.renderPublicPage(page, user, linkRealm);
        }

    }

    /**
     * The current RWikiObject
     * 
     * @return rwikiObject
     */
    public RWikiObject getRwikiObject() {
        return rwo;
    }

    /**
     * The localised name of the rwikiObject
     * 
     * @return localised page name
     */
    public String getLocalisedPageName() {
        return NameHelper.localizeName(rwo.getName(), rwo.getRealm());
    }

    /**
     * Returns an url that generate a view string to the current rwikiObject
     * 
     * @return url as String
     */
    public String getEditUrl() {
        ViewBean vb = new ViewBean(rwo.getName(), rwo.getRealm());
        return vb.getEditUrl();
    }
    /**
     * Returns true if the underlying page exists.
     * @return
     */
    public boolean getExists() {
    		return objectService.exists(rwo.getName(),rwo.getRealm());
    }
    /**
     * Returns true if the page has content and it has length
     * @return
     */
    public boolean getHasContent() {
    		if ( ! getExists() ) return false;
    		String content = rwo.getContent();
    		return (content != null && content.trim().length() > 0 );
    }

}
