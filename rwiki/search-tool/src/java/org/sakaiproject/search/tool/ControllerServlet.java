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
package org.sakaiproject.search.tool;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
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

/**
 * @author andrew
 * 
 */
// FIXME: Tool
public class ControllerServlet extends HttpServlet {
	public static Logger log;

	/**
	 * Required for serialization... also to stop eclipse from giving me a
	 * warning!
	 */
	private static final long serialVersionUID = 676743152200357708L;

	public static final String SAVED_REQUEST_URL = "org.sakaiproject.search.last-request-url";

	private static final String PANEL = "panel";

	private static final Object TITLE_PANEL = "Title";

	private WebApplicationContext wac;

	private String headerPreContent;

	private SearchBeanFactory searchBeanFactory = null;

	public void init(ServletConfig servletConfig) throws ServletException {

		super.init(servletConfig);

		ServletContext sc = servletConfig.getServletContext();

		wac = WebApplicationContextUtils.getWebApplicationContext(sc);
		try {
			log = (Logger) wac.getBean("search-logger");
			searchBeanFactory = (SearchBeanFactory) wac
					.getBean("search-searchBeanFactory");
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

		if (wac == null) {
			wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(this.getServletContext());
			if (wac == null) {
				response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
						"Cannot get WebApplicationContext");
				return;
			}

		}
		if (log == null) {
			log = (Logger) wac.getBean("search-logger");
		}
		if (searchBeanFactory == null) {
			searchBeanFactory = (SearchBeanFactory) wac
					.getBean("search-searchBeanFactory");
		}

		request.setAttribute(Tool.NATIVE_URL, Tool.NATIVE_URL);

		String targetURL = persistState(request);
		if (targetURL != null && targetURL.trim().length() > 0) {
			response.sendRedirect(targetURL);
			return;
		}
		if (TITLE_PANEL.equals(request.getParameter(PANEL))) {

			String targetPage = "/WEB-INF/pages/title.jsp";
			RequestDispatcher rd = request.getRequestDispatcher(targetPage);
			rd.forward(request, response);

		} else {
			String path = request.getPathInfo();
			if ( path == null || path.length() == 0) {
				path = "/index";
			}
			if ( !path.startsWith("/") ) {
				path = "/" + path;
			}
			String targetPage = "/WEB-INF/pages" + path + ".jsp";
			request.setAttribute(SearchBeanFactory.SEARCH_BEAN_FACTORY_ATTR,
					searchBeanFactory);
			RequestDispatcher rd = request.getRequestDispatcher(targetPage);
			rd.forward(request, response);

		}

		request.removeAttribute(Tool.NATIVE_URL);
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
	private boolean isPageToolDefault(HttpServletRequest request) {
		if (TITLE_PANEL.equals(request.getParameter(PANEL)))
			return false;
		String pathInfo = request.getPathInfo();
		String queryString = request.getQueryString();
		String method = request.getMethod();
		return ("GET".equalsIgnoreCase(method)
				&& (pathInfo == null || request.getPathInfo().length() == 0) 
				&& (queryString == null || queryString.length() == 0));
	}

	/**
	 * Check to see if the request represents a page that can act as a restor
	 * point.
	 * 
	 * @param request
	 * @return true if it is possible to restore to this point.
	 */
	private boolean isPageRestorable(HttpServletRequest request) {
		if (TITLE_PANEL.equals(request.getParameter(PANEL)))
			return false;

		if ("GET".equalsIgnoreCase(request.getMethod()))
			return true;

		return false;
	}

}
