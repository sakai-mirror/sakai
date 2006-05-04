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
package uk.ac.cam.caret.sakai.rwiki.tool.api;

import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiObject;

//FIXME: Service

public interface ToolRenderService {
	

	/**
	 * Renders a public view of the page
	 * @param rwo the RWiki object
	 * @param user The user 
	 * @return and string containing the rendered content
	 */
    String renderPublicPage(RWikiObject rwo, String user, boolean withBreadcrumbs);
    
    /**
     * Renders a public view of the page
     * @param rwo The RWikiObject representing the page
     * @param user The user making the request
     * @param realm The default realm to render in
     * @return an string representing the rendered content
     */
    String renderPublicPage(RWikiObject rwo, String user, String realm, boolean withBreadcrumbs);
	/**
	 * Renders a view of the page
	 * @param rwo the RWiki object
	 * @param user The user 
	 * @return and string containing the rendered content
	 */	
    String renderPage(RWikiObject rwo, String user);
    /**
     * Renders a view of the page
     * @param rwo The RWikiObject representing the page
     * @param user The user making the request
     * @param defaultRealm The default realm to render in
     * @return an string representing the rendered content
     */
    String renderPage(RWikiObject rwo, String user, String defaultRealm);
 
	String renderPage(RWikiObject rwo, String user, boolean b);
}