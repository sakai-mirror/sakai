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
package uk.ac.cam.caret.sakai.rwiki.tool.command;

import java.io.IOException;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.service.framework.log.Logger;

import uk.ac.cam.caret.sakai.rwiki.service.api.RWikiObjectService;
import uk.ac.cam.caret.sakai.rwiki.service.exception.PermissionException;
import uk.ac.cam.caret.sakai.rwiki.service.exception.VersionException;
import uk.ac.cam.caret.sakai.rwiki.tool.RWikiServlet;
import uk.ac.cam.caret.sakai.rwiki.tool.RequestScopeSuperBean;
import uk.ac.cam.caret.sakai.rwiki.tool.api.HttpCommand;
import uk.ac.cam.caret.sakai.rwiki.tool.bean.EditBean;
import uk.ac.cam.caret.sakai.rwiki.tool.bean.ErrorBean;
import uk.ac.cam.caret.sakai.rwiki.tool.bean.ViewBean;
import uk.ac.cam.caret.sakai.rwiki.tool.bean.helper.ViewParamsHelperBean;
import uk.ac.cam.caret.sakai.rwiki.tool.command.helper.ErrorBeanHelper;

/**
 * @author andrew
 * 
 */
//FIXME: Tool

public class SaveCommand implements HttpCommand {

    private RWikiObjectService objectService;

    private Logger log;

    private String contentChangedPath;

    private String noUpdatePath;

    private String successfulPath;

    private String previewPath;

    private String cancelPath;

    public void execute(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        RequestScopeSuperBean rssb = RequestScopeSuperBean
                .getFromRequest(request);

        ViewParamsHelperBean vphb = (ViewParamsHelperBean) rssb
                .getNameHelperBean();

        String user = rssb.getCurrentUser();

        String content = vphb.getContent();
        String save = vphb.getSaveType();
        String name = vphb.getGlobalName();
        String realm = vphb.getLocalSpace();
        if (save == null) {
            save = EditBean.SAVE_VALUE;
        }
        if (save.equals(EditBean.OVERWRITE_VALUE)) {
            content = vphb.getSubmittedContent();
            // Set the content as the submitted content in case we have a version exception
            vphb.setContent(content);
        } else if (save.equals(EditBean.PREVIEW_VALUE)) {
            this.previewDispatch(request, response);
            return;
        } else if (save.equals(EditBean.CANCEL_VALUE)) {
            this.cancelDispatch(request, response);
            ViewBean vb = new ViewBean(name,realm);
            String requestURL = request.getRequestURL().toString();
            SessionManager.getCurrentToolSession().setAttribute(RWikiServlet.SAVED_REQUEST_URL,requestURL+vb.getViewUrl());
            return;
        }

        String version = vphb.getSubmittedVersion();
        Date versionDate = new Date(Long.parseLong(version));

        try {
            objectService.update(name, user, realm, versionDate, content);
        } catch (VersionException e) {
            // The page has changed underneath us...

            // redirect probably back to the edit page
            this.contentChangedDispatch(request, response);
            return;
        } catch (PermissionException e) {
            // Redirect back to a no permission page...
            this.noUpdateAllowed(request, response);
            return;
        }
        // Successful update
        this.successfulUpdateDispatch(request, response);
        ViewBean vb = new ViewBean(name,realm);
        String requestURL = request.getRequestURL().toString();
        SessionManager.getCurrentToolSession().setAttribute(RWikiServlet.SAVED_REQUEST_URL,requestURL+vb.getViewUrl());
        return;

    }

    private void cancelDispatch(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher(cancelPath);
        rd.forward(request, response);
    }

    private void previewDispatch(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher(previewPath);
        rd.forward(request, response);
    }

    private void successfulUpdateDispatch(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher(successfulPath);
        rd.forward(request, response);
    }

    private void contentChangedDispatch(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        ErrorBean errorBean = ErrorBeanHelper.getErrorBean(request);
        // FIXME internationalise this!!
        errorBean
                .addError("Content has changed since you last viewed it. Please update the new content or overwrite it with the submitted content.");
        RequestDispatcher rd = request.getRequestDispatcher(contentChangedPath);
        rd.forward(request, response);
    }

    private void noUpdateAllowed(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        ErrorBean errorBean = ErrorBeanHelper.getErrorBean(request);
        // FIXME internationalise this!!
        errorBean.addError("You do not have permission to update this page.");
        RequestDispatcher rd = request.getRequestDispatcher(noUpdatePath);
        rd.forward(request, response);
    }

    public String getSuccessfulPath() {
        return successfulPath;
    }

    public void setSuccessfulPath(String successfulPath) {
        this.successfulPath = successfulPath;
    }

    public String getContentChangedPath() {
        return contentChangedPath;
    }

    public void setContentChangedPath(String contentChangedPath) {
        this.contentChangedPath = contentChangedPath;
    }

    public String getNoUpdatePath() {
        return noUpdatePath;
    }

    public void setNoUpdatePath(String noUpdatePath) {
        this.noUpdatePath = noUpdatePath;
    }

    public String getPreviewPath() {
        return previewPath;
    }

    public void setPreviewPath(String previewPath) {
        this.previewPath = previewPath;
    }

    public String getCancelPath() {
        return cancelPath;
    }

    public void setCancelPath(String cancelPath) {
        this.cancelPath = cancelPath;
    }

    public RWikiObjectService getObjectService() {
        return objectService;
    }

    public void setObjectService(RWikiObjectService objectService) {
        this.objectService = objectService;
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

}