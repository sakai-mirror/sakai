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

import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiObject;

//FIXME: Service

public interface RenderService {
	

    /**
     * Renders a view of the page in the same page space as the realm
     * @param rwo the RWiki Object
     * @param user the user requesting the render
     * @param plr the page link rendere to be used for rendering links
     * @return an string representing the rendered content
     */
    String renderPage(RWikiObject rwo, String user, PageLinkRenderer plr);
    /**
     * 
     * @param rwo
     * @param user
     * @param pageSpace
     * @param plr
     * @return
     */
    String renderPage(RWikiObject rwo, String user, String pageSpace, PageLinkRenderer plr);
    
}