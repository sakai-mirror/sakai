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
package uk.ac.cam.caret.sakai.rwiki.tool.bean.helper;

import javax.servlet.ServletRequest;

import uk.ac.cam.caret.sakai.rwiki.component.model.impl.NameHelper;
import uk.ac.cam.caret.sakai.rwiki.service.api.RWikiSecurityService;
import uk.ac.cam.caret.sakai.rwiki.tool.bean.EditBean;
import uk.ac.cam.caret.sakai.rwiki.tool.bean.SearchBean;
import uk.ac.cam.caret.sakai.rwiki.tool.bean.ViewBean;

/**
 * Bean to get common request parameters from the servlet request.
 * 
 * @author andrew
 */
//FIXME: Tool

public class ViewParamsHelperBean {

    /**
     * the requested global page name
     */
    private String globalName;

    /**
     * the local space
     */
    private String localSpace;
    
    /**
     * the requested search criteria
     */
    private String search;

    /**
     * the current servlet request
     */
    private ServletRequest request;

    /**
     * the RWikiSecurityService
     */
    private RWikiSecurityService securityService;
    
    /**
     * the default realm
     */
    private String defaultRealm;
    
    /**
     * the submitted content
     */
    private String content;

    /**
     * the submitted previous content
     */
    private String submittedContent;
    
    /**
     * the submitted version
     */
    private String submittedVersion;

    /**
     * the submitted save type
     */
    private String saveType;

    /**
     * Initializes the bean, gets the parameters out of the request
     */
    public void init() {
        String pageName = request.getParameter(ViewBean.PAGE_NAME_PARAM);
        
        localSpace = request.getParameter(SearchBean.REALM_PARAM);
        
        defaultRealm = securityService.getRealm(request);

        if (localSpace == null || localSpace.equals("")) {
            localSpace = defaultRealm;
        }
        
        globalName = NameHelper.globaliseName(pageName, localSpace);
        
        search = request.getParameter(SearchBean.SEARCH_PARAM);
        
        content = request.getParameter(EditBean.CONTENT_PARAM);
        
        submittedContent = request.getParameter(EditBean.SUBMITTED_CONTENT_PARAM);
        
        submittedVersion = request.getParameter(EditBean.VERSION_PARAM);
        
        saveType = request.getParameter(EditBean.SAVE_PARAM);
    }

    /**
     * Get the globalised page name
     * @return the requested globalised page name
     */
    public String getGlobalName() {
        return globalName;
    }

    /**
     * Set the globalised page name
     * @param globalName
     */
    public void setGlobalName(String globalName) {
        this.globalName = globalName;
    }

    /**
     * Get the local space  
     * @return local space
     */
    public String getLocalSpace() {
        return localSpace;
    }
    
    /**
     * Set the local space
     * @param localSpace
     */
    public void setLocalSpace(String localSpace) {
        this.localSpace = localSpace;
        
    }

    /**
     * Get the current page's space
     * @return space relating to globalName
     */
    public String getPageSpace() {
        return NameHelper.localizeSpace(globalName, localSpace);
    }

    /**
     * Get the localised name relating to globalName and localSpace
     * @return the localised name
     */
    public String getLocalName() {
        return NameHelper.localizeName(globalName, localSpace);
    }
    
    /**
     * Get the submitted search criteria
     * @return search criteria
     */
    public String getSearch() {
        return search;
    }

    /**
     * Set the search criteria
     * @param search criteria to set
     */
    public void setSearch(String search) {
        this.search = search;
    }

    /**
     * Get the current servlet request
     * @return current servlet request
     */
    public ServletRequest getServletRequest() {
        return request;
    }

    /**
     * Set the current servlet request
     * @param request 
     */
    public void setServletRequest(ServletRequest request) {
        this.request = request;
    }

    /**
     * Get the RWikiSecurityService
     * @return the rwikiSecurityService
     */
    public RWikiSecurityService getSecurityService() {
        return securityService;
    }

    /**
     * Set the RWikiSecurityService to be used in init
     * @param securityService
     */
    public void setSecurityService(RWikiSecurityService securityService) {
        this.securityService = securityService;
    }

    /**
     * Get the default realm
     * @return default realm for the current request
     */
    public String getDefaultRealm() {
        return defaultRealm;
    }

    /**
     * Set the default realm
     * @param defaultRealm
     */
    public void setDefaultRealm(String defaultRealm) {
        this.defaultRealm = defaultRealm;
    }

    /**
     * Get the submitted content
     * @return content
     */
    public String getContent() {
        return content;
    }

    /**
     * Set the content
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Get the previously submitted content
     * @return content as a String
     */
    public String getSubmittedContent() {
        return submittedContent;
    }

    /**
     * Set the previously submitted content
     * @param submittedContent
     */
    public void setSubmittedContent(String submittedContent) {
        this.submittedContent = submittedContent;
    }

    /**
     * Get the submitted version number
     * @return version as string
     */
    public String getSubmittedVersion() {
        return submittedVersion;
    }

    /**
     * Set the submitted version number
     * @param submittedVersion
     */
    public void setSubmittedVersion(String submittedVersion) {
        this.submittedVersion = submittedVersion;
    }

    /**
     * Get the requested save type 
     * @return save type as string
     */
    public String getSaveType() {
        return saveType;
    }

    /**
     * Set the save type
     * @param saveType
     */
    public void setSaveType(String saveType) {
        this.saveType = saveType;
    }

    
}
