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

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.api.kernel.tool.Tool;
import org.sakaiproject.service.framework.log.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import uk.ac.cam.caret.sakai.rwiki.tool.api.HttpCommand;
import uk.ac.cam.caret.sakai.rwiki.tool.bean.PrePopulateBean;
import uk.ac.cam.caret.sakai.rwiki.tool.bean.ViewBean;
import uk.ac.cam.caret.sakai.rwiki.tool.util.WikiPageAction;
import uk.ac.cam.caret.sakai.rwiki.utils.TimeLogger;

/**
 * @author andrew
 * 
 */
// FIXME: Tool
public class RWikiServlet extends HttpServlet {
    public static Logger log;

    /**
     * Required for serialization... also to stop eclipse from giving me a
     * warning!
     */
    private static final long serialVersionUID = 676743152200357706L;

    public static final String SAVED_REQUEST_URL = "uk.ac.cam.caret.sakai.rwiki.tool.RWikiServlet.last-request-url";

    private WebApplicationContext wac;

    private String headerPreContent;

    private String headerScriptSource;

    private String footerScript;

    public void init(ServletConfig servletConfig) throws ServletException {

        super.init(servletConfig);

        ServletContext sc = servletConfig.getServletContext();

        wac = WebApplicationContextUtils.getWebApplicationContext(sc);
        try {
            log = (Logger) wac.getBean("rwiki-logger");
        } catch (Exception ex) {
        }
        headerPreContent = servletConfig.getInitParameter("headerPreContent");
        headerScriptSource = servletConfig
                .getInitParameter("headerScriptSource");
        footerScript = servletConfig.getInitParameter("footerScript");
        try {
            boolean logResponse = "true".equalsIgnoreCase(servletConfig
                    .getInitParameter("log-response"));
            TimeLogger.setLogResponse(logResponse);
        } catch (Exception ex) {

        }
        try {
            boolean logFullResponse = "true".equalsIgnoreCase(servletConfig
                    .getInitParameter("log-full-response"));
            TimeLogger.setLogFullResponse(logFullResponse);
        } catch (Exception ex) {

        }

    }

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        execute(request, response);
    }

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        execute(request, response);
    }

    public void execute(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long finish = System.currentTimeMillis();
        long start = System.currentTimeMillis();
        TimeLogger.printTimer("Response Start Marker", start, finish);

        if (wac == null) {
            wac = WebApplicationContextUtils
                    .getRequiredWebApplicationContext(this.getServletContext());
            if (wac == null) {
                response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                        "Cannot get WebApplicationContext");
                return;
            }
            try {
                log = (Logger) wac.getBean("rwiki-logger");
            } catch (Exception ex) {
                System.err.println("ERROR: No Logger found in RWikiServlet");
            }
        }
        log.debug("========================Page Start==========");
        request.setAttribute(Tool.NATIVE_URL, Tool.NATIVE_URL);
        String targetURL = persistState(request);
        if (targetURL != null && targetURL.trim().length() > 0) {
            response.sendRedirect(targetURL);
            return;
        }

        // Must be done on every request
        prePopulateRealm(request);

        addWikiStylesheet(request);

        request.setAttribute("footerScript", footerScript);
        request.setAttribute("headerScriptSource", headerScriptSource);

        RequestHelper helper = (RequestHelper) wac.getBean(RequestHelper.class
                .getName());

        HttpCommand command = helper.getCommandForRequest(request);

        finish = System.currentTimeMillis();
        TimeLogger.printTimer("Response Preamble Complete", start, finish);
        start = System.currentTimeMillis();
        command.execute(request, response);
        finish = System.currentTimeMillis();

        TimeLogger.printTimer("Response Complete", start, finish);
        if ((finish - start) > 500) {
            log.debug("Slow Wiki Page " + (finish - start) + " ms URL "
                    + request.getRequestURL() + "?" + request.getQueryString());
        } else if (TimeLogger.getLogResponse()) {
            log.info("Wiki Page Response " + (finish - start) + " ms URL "
                    + request.getRequestURL() + "?" + request.getQueryString());
        }
        request.removeAttribute(Tool.NATIVE_URL);
        log.debug("=====================Page End=============");
    }

    public void prePopulateRealm(HttpServletRequest request) {
        RequestScopeSuperBean rssb = RequestScopeSuperBean.createAndAttach(
                request, wac);

        PrePopulateBean ppBean = rssb.getPrePopulateBean();

        ppBean.doPrepopulate();
    }

    public void addWikiStylesheet(HttpServletRequest request) {
        String sakaiHeader = (String) request.getAttribute("sakai.html.head");
        request.setAttribute("sakai.html.head", headerPreContent + sakaiHeader);
    }

    /**
     * returns the request state for the tool. If the state is restored, we set
     * the request attribute RWikiServlet.REQUEST_STATE_RESTORED to Boolean.TRUE
     * and a Thread local named RWikiServlet.REQUEST_STATE_RESTORED to
     * Boolean.TRUE. These MUST be checked by anything that modifies state, to
     * ensure that a reinitialisation of Tool state does not result in a repost
     * of data.
     * 
     * @param request
     * @return
     */
    private String persistState(HttpServletRequest request) {
        ToolSession ts = SessionManager.getCurrentToolSession();
        if (isPageToolDefault(request)) {
            log.debug("Incomming URL is " + request.getRequestURL().toString()
                    + "?" + request.getQueryString());
            log.debug("Restore " + ts.getAttribute(SAVED_REQUEST_URL));
            return (String) ts.getAttribute(SAVED_REQUEST_URL);
        }
        if (isPageRestorable(request)) {
            ts.setAttribute(SAVED_REQUEST_URL, request.getRequestURL()
                    .toString()
                    + "?" + request.getQueryString());
            log.debug("Saved " + ts.getAttribute(SAVED_REQUEST_URL));
        }
        return null;
    }

    /**
     * Check to see if the reques represents the Tool default page. This is not
     * the same as the view Home. It is the same as first entry into a Tool or
     * when the page is refreshed
     * 
     * @param request
     * @return true if the page is the Tool default page
     */
    // XXX this should not be here!! The RequestHelper should perform this
    // functionality.
    private boolean isPageToolDefault(HttpServletRequest request) {
        if (RequestHelper.TITLE_PANEL.equals(request
                .getParameter(RequestHelper.PANEL)))
            return false;

        if (!request.getRequestURL().toString().equals(request.getContextPath() + request.getServletPath())) {
            return false;
        }
        
        String action = request.getParameter(RequestHelper.ACTION);
        if (action != null && action.length() > 0)
            return false;
        String pageName = request.getParameter(ViewBean.PAGE_NAME_PARAM);
        return (pageName == null || pageName.trim().length() == 0);
    }

    /**
     * Check to see if the request represents a page that can act as a restor
     * point.
     * 
     * @param request
     * @return true if it is possible to restore to this point.
     */
    private boolean isPageRestorable(HttpServletRequest request) {
        if (RequestHelper.TITLE_PANEL.equals(request
                .getParameter(RequestHelper.PANEL)))
            return false;

        if (WikiPageAction.PUBLICVIEW_ACTION.getName().equals(
                request.getParameter(RequestHelper.ACTION)))
            return false;

        if ("GET".equalsIgnoreCase(request.getMethod()))
            return true;

        return false;
    }

}
