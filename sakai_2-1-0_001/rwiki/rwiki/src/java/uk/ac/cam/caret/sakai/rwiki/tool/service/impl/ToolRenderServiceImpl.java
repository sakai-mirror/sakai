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
package uk.ac.cam.caret.sakai.rwiki.tool.service.impl;

import org.sakaiproject.service.framework.log.Logger;

import uk.ac.cam.caret.sakai.rwiki.component.model.impl.NameHelper;
import uk.ac.cam.caret.sakai.rwiki.service.api.RenderService;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiObject;
import uk.ac.cam.caret.sakai.rwiki.tool.api.ToolRenderService;

/**
 * @author andrew
 *
 */
// FIXME: Component WITH FIXES, remove deps on page link render impl
public class ToolRenderServiceImpl implements ToolRenderService {
    private Logger log;

    private RenderService renderService = null;
   
	/*
     *  (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.api.RenderService#publicRenderPage(uk.ac.cam.caret.sakai.rwiki.service.api.api.model.RWikiObject, java.lang.String)
     */
	public String renderPublicPage(RWikiObject rwo, String user) {
		return renderPublicPage(rwo,user,rwo.getRealm());
	}

	/*
	 *  (non-Javadoc)
	 * @see uk.ac.cam.caret.sakai.rwiki.service.api.RenderService#publicRenderPage(uk.ac.cam.caret.sakai.rwiki.service.api.api.model.RWikiObject, java.lang.String, java.lang.String)
	 */
	public String renderPublicPage(RWikiObject rwo, String user, String pageSpace) {
		PublicPageLinkRendererImpl plr = new PublicPageLinkRendererImpl(NameHelper.localizeSpace(rwo.getName(),pageSpace),pageSpace);
		return renderService.renderPage(rwo,user,plr);
	}
    
    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.api.RenderService#renderPage(uk.ac.cam.caret.sakai.rwiki.tool.service.RWikiObject)
     */
    public String renderPage(RWikiObject rwo, String user) {
        return renderPage(rwo, user, true);
    }

    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.api.RenderService#renderPage(uk.ac.cam.caret.sakai.rwiki.tool.service.RWikiObject, java.lang.String)
     */
    public String renderPage(RWikiObject rwo, String user, String pageSpace) {
        PageLinkRendererImpl plr = new PageLinkRendererImpl(NameHelper.localizeSpace(rwo.getName(), pageSpace), pageSpace);
        return renderService.renderPage(rwo, user,  plr);
    }


	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}

	public String renderPage(RWikiObject rwo, String user, boolean b) {
        PageLinkRendererImpl plr = new PageLinkRendererImpl(NameHelper.localizeSpace(rwo.getName(), rwo.getRealm()), rwo.getRealm());
        plr.setUseCache(b);
        plr.setCachable(b);
        return renderService.renderPage(rwo, user,  plr);
	}

    /**
     * @return Returns the renderService.
     */
    public RenderService getRenderService() {
        return renderService;
    }

    /**
     * @param renderService The renderService to set.
     */
    public void setRenderService(RenderService renderService) {
        this.renderService = renderService;
    }

}
