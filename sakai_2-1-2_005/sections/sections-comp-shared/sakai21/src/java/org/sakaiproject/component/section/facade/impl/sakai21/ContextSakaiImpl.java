/**********************************************************************************
*
* $Id$
*
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of California and The Regents of the University of Michigan
*
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

package org.sakaiproject.component.section.facade.impl.sakai21;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.tool.Placement;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.sakaiproject.api.section.facade.manager.Context;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.service.legacy.site.cover.SiteService;

/**
 * Uses Sakai's ToolManager to determine the current context.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 *
 */
public class ContextSakaiImpl implements Context {
	private static final Log log = LogFactory.getLog(ContextSakaiImpl.class);

	/**
	 * @inheritDoc
	 */
	public String getContext(Object request) {
        Placement placement = ToolManager.getCurrentPlacement();        
        if(placement == null) {
            log.error("Placement is null");
        }
        return placement.getContext();
	}

	public String getContextTitle(Object request) {
		String siteContext = getContext(null);
		String siteTitle = null;
		try {
			siteTitle = SiteService.getSite(siteContext).getTitle();
		} catch (IdUnusedException e) {
			log.error("Unable to get site for context " + siteContext);
			siteTitle = siteContext; // Better than nothing???
		}
		return siteTitle;
	}
}