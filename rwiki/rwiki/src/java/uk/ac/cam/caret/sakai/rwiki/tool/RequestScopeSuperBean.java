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
package uk.ac.cam.caret.sakai.rwiki.tool;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroupService;
import org.springframework.context.ApplicationContext;

import uk.ac.cam.caret.sakai.rwiki.bean.DiffBean;
import uk.ac.cam.caret.sakai.rwiki.bean.EditBean;
import uk.ac.cam.caret.sakai.rwiki.bean.ErrorBean;
import uk.ac.cam.caret.sakai.rwiki.bean.HistoryBean;
import uk.ac.cam.caret.sakai.rwiki.bean.HomeBean;
import uk.ac.cam.caret.sakai.rwiki.bean.PermissionsBean;
import uk.ac.cam.caret.sakai.rwiki.bean.PrePopulateBean;
import uk.ac.cam.caret.sakai.rwiki.bean.AuthZGroupBean;
import uk.ac.cam.caret.sakai.rwiki.bean.AuthZGroupEditBean;
import uk.ac.cam.caret.sakai.rwiki.bean.RecentlyVisitedBean;
import uk.ac.cam.caret.sakai.rwiki.bean.ReferencesBean;
import uk.ac.cam.caret.sakai.rwiki.bean.RenderBean;
import uk.ac.cam.caret.sakai.rwiki.bean.SearchBean;
import uk.ac.cam.caret.sakai.rwiki.bean.UpdatePermissionsBean;
import uk.ac.cam.caret.sakai.rwiki.bean.ViewBean;
import uk.ac.cam.caret.sakai.rwiki.bean.helper.DiffHelperBean;
import uk.ac.cam.caret.sakai.rwiki.bean.helper.AuthZGroupBeanHelper;
import uk.ac.cam.caret.sakai.rwiki.bean.helper.AuthZGroupEditBeanHelper;
import uk.ac.cam.caret.sakai.rwiki.bean.helper.RecentlyVisitedHelperBean;
import uk.ac.cam.caret.sakai.rwiki.bean.helper.ReverseHistoryHelperBean;
import uk.ac.cam.caret.sakai.rwiki.bean.helper.ReviewHelperBean;
import uk.ac.cam.caret.sakai.rwiki.bean.helper.UpdatePermissionsBeanHelper;
import uk.ac.cam.caret.sakai.rwiki.bean.helper.UserHelperBean;
import uk.ac.cam.caret.sakai.rwiki.bean.helper.ViewParamsHelperBean;
import uk.ac.cam.caret.sakai.rwiki.model.RWikiObject;
import uk.ac.cam.caret.sakai.rwiki.service.PopulateService;
import uk.ac.cam.caret.sakai.rwiki.service.RWikiObjectService;
import uk.ac.cam.caret.sakai.rwiki.service.RWikiSecurityService;
import uk.ac.cam.caret.sakai.rwiki.service.RenderService;

/**
 * This is a replacement for the RequestScopeApplicationContext which turned out
 * to be too slow.
 * 
 * It is hideous and probably very confusing however it will allow me to keep
 * things working whilst I refactor.
 * 
 * 
 * @author andrew
 * 
 */
public class RequestScopeSuperBean {

    public static final String REQUEST_ATTRIBUTE = "rsacMap";

    private HttpServletRequest request;

    private HashMap map = new HashMap();

    private ApplicationContext context;

    private RWikiSecurityService securityService;

    private RWikiObjectService objectService;

    private Logger logger;

    private RenderService renderService;

    private PopulateService populateService;

    private AuthzGroupService realmService;

    public static RequestScopeSuperBean getFromRequest(
            HttpServletRequest request) {
        return (RequestScopeSuperBean) request.getAttribute(REQUEST_ATTRIBUTE);
    }

    public static RequestScopeSuperBean createAndAttach(
            HttpServletRequest request, ApplicationContext context) {
        RequestScopeSuperBean rssb = new RequestScopeSuperBean();
        rssb.setRequest(request);
        rssb.setContext(context);
        rssb.init();

        request.setAttribute(REQUEST_ATTRIBUTE, rssb);

        return rssb;
    }

    public void init() {
        securityService = (RWikiSecurityService) context
                .getBean("securityService");
        objectService = (RWikiObjectService) context.getBean("objectService");
        logger = (Logger) context.getBean("rwiki-logger");
        renderService = (RenderService) context.getBean("renderService");
        populateService = (PopulateService) context.getBean("populateService");
        realmService = (AuthzGroupService) context.getBean(AuthzGroupService.class.getName());
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public ApplicationContext getContext() {
        return context;
    }

    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    public ViewParamsHelperBean getNameHelperBean() {
        String key = "nameHelperBean";
        if (map.get(key) == null) {
            ViewParamsHelperBean vphb = new ViewParamsHelperBean();
            vphb.setServletRequest(request);
            vphb.setSecurityService(securityService);
            vphb.init();
            map.put(key, vphb);
        }
        return (ViewParamsHelperBean) map.get(key);
    }

    public String getCurrentLocalSpace() {
        String key = "currentLocalSpace";
        if (map.get(key) == null) {
            ViewParamsHelperBean vphb = this.getNameHelperBean();
            map.put(key, vphb.getLocalSpace());
        }

        return (String) map.get(key);
    }

    public String getCurrentDefaultRealm() {
        String key = "currentDefaultRealm";
        if (map.get(key) == null) {
            ViewParamsHelperBean vphb = this.getNameHelperBean();
            map.put(key, vphb.getDefaultRealm());
        }
        return (String) map.get(key);
    }

    public String getCurrentPageName() {
        String key = "currentPageName";
        if (map.get(key) == null) {
            map.put(key, getNameHelperBean().getGlobalName());
        }
        return (String) map.get(key);
    }

    public String getCurrentPageSpace() {
        String key = "currentPageSpace";
        if (map.get(key) == null) {
            map.put(key, getNameHelperBean().getPageSpace());
        }
        return (String) map.get(key);
    }

    public String getCurrentSearch() {
        String key = "currentSearch";
        if (map.get(key) == null) {
            map.put(key, getNameHelperBean().getSearch());
        }
        return (String) map.get(key);
    }

    public String getCurrentUser() {
        String key = "currentUser";
        if (map.get(key) == null) {
            UserHelperBean uhb = new UserHelperBean();
            uhb.setServletRequest(request);
            uhb.init();
            map.put(key, uhb.getUser());
        }
        return (String) map.get(key);
    }

    public ViewBean getViewBean() {
        String key = "viewBean";
        if (map.get(key) == null) {
            ViewBean vb = new ViewBean();
            vb.setLocalSpace(this.getCurrentLocalSpace());
            vb.setPageName(this.getCurrentPageName());
            map.put(key, vb);
        }
        return (ViewBean) map.get(key);
    }

    public RWikiObject getCurrentRWikiObject() {
        String key = "currentRWikiObject";
        if (map.get(key) == null) {

            RWikiObject rwo = objectService.getRWikiObject(
                    getCurrentPageName(), getCurrentUser(),
                    getCurrentLocalSpace());
            map.put(key, rwo);
        }
        return (RWikiObject) map.get(key);
    }

    public RecentlyVisitedBean getRecentlyVisitedBean() {
        String key = "recentlyVisitedBean";
        if (map.get(key) == null) {
            RecentlyVisitedHelperBean rvhb = new RecentlyVisitedHelperBean();
            rvhb.setLog(logger);
            rvhb.setServletRequest(request);
            rvhb.setDefaultSpace(getCurrentDefaultRealm());
            rvhb.init();
            map.put(key, rvhb.getRecentlyVisitedBean());
        }
        return (RecentlyVisitedBean) map.get(key);
    }

    public RenderBean getRenderBean() {
        String key = "renderBean";
        if (map.get(key) == null) {
            RenderBean rb = new RenderBean(getCurrentRWikiObject(),
                    getCurrentUser(), renderService, objectService);
            map.put(key, rb);
        }
        return (RenderBean) map.get(key);
    }

    public HistoryBean getHistoryBean() {
        String key = "historyBean";
        if (map.get(key) == null) {
            HistoryBean hb = new HistoryBean(getCurrentRWikiObject(),
                    getCurrentLocalSpace());
            map.put(key, hb);
        }
        return (HistoryBean) map.get(key);
    }

    public ReverseHistoryHelperBean getReverseHistoryHelperBean() {
        String key = "reverseHistoryHelperBean";
        if (map.get(key) == null) {
            ReverseHistoryHelperBean rhhb = new ReverseHistoryHelperBean();
            rhhb.setRwikiObject(getCurrentRWikiObject());
            rhhb.setRwikiObjectService(objectService);
            map.put(key, rhhb);
        }
        return (ReverseHistoryHelperBean) map.get(key);
    }

    public DiffBean getDiffBean() {
        String key = "diffBean";
        if (map.get(key) == null) {
            DiffHelperBean dhb = new DiffHelperBean();
            dhb.setServletRequest(request);
            dhb.setRwikiObject(getCurrentRWikiObject());
            dhb.setRwikiObjectService(objectService);
            dhb.init();
            map.put(key, dhb.getDiffBean());
        }
        return (DiffBean) map.get(key);
    }

    public PrePopulateBean getPrePopulateBean() {
        String key = "prePopulateBean";
        if (map.get(key) == null) {
            PrePopulateBean ppb = new PrePopulateBean();
            ppb.setPopulateService(populateService);
            ppb.setCurrentPageRealm(getCurrentPageSpace());
            ppb.setCurrentUser(getCurrentUser());
            ppb.setLog(logger);
            map.put(key, ppb);
        }
        return (PrePopulateBean) map.get(key);
    }

    public SearchBean getSearchBean() {
        String key = "searchBean";
        if (map.get(key) == null) {
            SearchBean sb = new SearchBean(getCurrentSearch(),
                    getCurrentUser(), getCurrentLocalSpace(), objectService);
            map.put(key, sb);
        }
        return (SearchBean) map.get(key);
    }

    public PermissionsBean getPermissionsBean() {
        String key = "permissionsBean";
        if (map.get(key) == null) {
            PermissionsBean pb = new PermissionsBean(getCurrentRWikiObject(),
                    getCurrentUser(), objectService);
            map.put(key, pb);
        }
        return (PermissionsBean) map.get(key);
    }

    public ErrorBean getErrorBean() {
        String key = "errorBean";
        if (map.get(key) == null) {
            map.put(key, new ErrorBean());
        }
        return (ErrorBean) map.get(key);
    }

    public EditBean getEditBean() {
        String key = "editBean";
        if (map.get(key) == null) {
            ViewParamsHelperBean vphb = getNameHelperBean();
            
            EditBean editBean = new EditBean();
            editBean.setPreviousContent(vphb.getContent());
            editBean.setPreviousVersion(vphb.getSubmittedVersion());
            editBean.setSaveType(vphb.getSaveType());
            map.put(key, editBean);
        }
        return (EditBean) map.get(key);
    }

    public ReviewHelperBean getReviewHelperBean() {
        String key = "reviewHelperBean";
        if (map.get(key) == null) {
            ReviewHelperBean rhb = new ReviewHelperBean();
            rhb.setServletRequest(request);
            rhb.setRwikiObject(getCurrentRWikiObject());
            rhb.setRwikiObjectService(objectService);
            rhb.init();
            map.put(key, rhb);
        }
        return (ReviewHelperBean) map.get(key);
    }

    public RenderBean getReviewRenderBean() {
        String key = "reviewRenderBean";
        if (map.get(key) == null) {
            RenderBean rb = new RenderBean(getReviewHelperBean().getMock(),
                    getCurrentUser(), renderService, objectService);
            map.put(key, rb);
        }
        return (RenderBean) map.get(key);

    }

    public RenderBean getViewRightRenderBean() {
        String key = "viewRightRenderBean";
        if (map.get(key) == null) {
            String pageName = "view_right";
            RenderBean rb = new RenderBean(pageName, getCurrentUser(),
                    getCurrentDefaultRealm(), renderService, objectService);
            map.put(key, rb);
        }
        return (RenderBean) map.get(key);
    }

    public RenderBean getEditRightRenderBean() {
        String key = "editRightRenderBean";
        if (map.get(key) == null) {
            String pageName = "edit_right";
            RenderBean rb = new RenderBean(pageName, getCurrentUser(),
                    getCurrentDefaultRealm(), renderService, objectService);
            map.put(key, rb);
        }
        return (RenderBean) map.get(key);
    }

    public RenderBean getInfoRightRenderBean() {
        String key = "infoRightRenderBean";
        if (map.get(key) == null) {
            String pageName = "info_right";
            RenderBean rb = new RenderBean(pageName, getCurrentUser(),
                    getCurrentDefaultRealm(), renderService, objectService);
            map.put(key, rb);
        }
        return (RenderBean) map.get(key);
    }

    public RenderBean getReviewRightRenderBean() {
        String key = "reviewRightRenderBean";
        if (map.get(key) == null) {
            String pageName = "review_right";
            RenderBean rb = new RenderBean(pageName, getCurrentUser(),
                    getCurrentDefaultRealm(), renderService, objectService);
            map.put(key, rb);
        }
        return (RenderBean) map.get(key);
    }

    public RenderBean getDiffRightRenderBean() {
        String key = "diffRightRenderBean";
        if (map.get(key) == null) {
            String pageName = "diff_right";
            RenderBean rb = new RenderBean(pageName, getCurrentUser(),
                    getCurrentDefaultRealm(), renderService, objectService);
            map.put(key, rb);
        }
        return (RenderBean) map.get(key);
    }

    public RenderBean getSearchRightRenderBean() {
        String key = "searchRightRenderBean";
        if (map.get(key) == null) {
            String pageName = "search_right";
            RenderBean rb = new RenderBean(pageName, getCurrentUser(),
                    getCurrentDefaultRealm(), renderService, objectService);
            map.put(key, rb);
        }
        return (RenderBean) map.get(key);
    }

    public RenderBean getPreviewRightRenderBean() {
        String key = "previewRightRenderBean";
        if (map.get(key) == null) {
            String pageName = "preview_right";
            RenderBean rb = new RenderBean(pageName, getCurrentUser(),
                    getCurrentDefaultRealm(), renderService, objectService);
            map.put(key, rb);
        }
        return (RenderBean) map.get(key);
    }
    
    public ReferencesBean getReferencesBean() {
        String key = "referencesBean";
        if (map.get(key) == null) {
            ReferencesBean rb = new ReferencesBean(getCurrentRWikiObject(),
                    objectService, getCurrentLocalSpace());
            map.put(key, rb);
        }
        return (ReferencesBean) map.get(key);
    }
    
    public HomeBean getHomeBean() {
        String key = "homeBean";
        if (map.get(key) == null) {
            HomeBean hb = new HomeBean();
            ViewParamsHelperBean vphb = getNameHelperBean();
            ViewBean vb = new ViewBean(null, vphb.getDefaultRealm());
            hb.setHomeLinkUrl(vb.getViewUrl());
            hb.setHomeLinkValue(vb.getLocalName());
            map.put(key, hb);
        }
        return (HomeBean) map.get(key);
    }
    
    public UpdatePermissionsBean getUpdatePermissionsBean() {
        String key = "updatePermissionsBean";
        if (map.get(key) == null) {
            UpdatePermissionsBean ub = UpdatePermissionsBeanHelper.createUpdatePermissionsBean(request);
            map.put(key, ub);
        }
        return (UpdatePermissionsBean) map.get(key);
    }
    
    public AuthZGroupBean getRealmBean() {
        String key = "realmBean";
        if (map.get(key) == null) {
            AuthZGroupBean rb = AuthZGroupBeanHelper.createRealmBean(realmService, getCurrentRWikiObject(), getErrorBean(), getViewBean());
            map.put(key, rb);
        }
        return (AuthZGroupBean) map.get(key);
    }

    public AuthZGroupEditBean getRealmEditBean() {
        String key = "realmEditBean";
        if (map.get(key) == null) {
            AuthZGroupEditBean reb = AuthZGroupEditBeanHelper.createRealmEditBean(request, getViewBean());
            map.put(key, reb);
        }
        
        return (AuthZGroupEditBean) map.get(key);
    }
    
}

