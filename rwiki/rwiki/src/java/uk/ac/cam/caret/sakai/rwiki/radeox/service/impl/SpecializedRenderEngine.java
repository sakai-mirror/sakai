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
package uk.ac.cam.caret.sakai.rwiki.radeox.service.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.radeox.api.engine.ImageRenderEngine;
import org.radeox.api.engine.RenderEngine;
import org.radeox.api.engine.WikiRenderEngine;
import org.radeox.api.engine.context.RenderContext;

import uk.ac.cam.caret.sakai.rwiki.model.NameHelper;
import uk.ac.cam.caret.sakai.rwiki.service.PageLinkRenderer;
import uk.ac.cam.caret.sakai.rwiki.service.RWikiObjectService;
import uk.ac.cam.caret.sakai.rwiki.service.impl.PageLinkRendererImpl;

/**
 * @author andrew
 *
 */
public class SpecializedRenderEngine implements ImageRenderEngine, WikiRenderEngine, RenderEngine {
    
    // FIXME make this an ImageRenderEngine and an IncludeRenderEngine
    
    private RenderEngine renderEngine;
    private RWikiObjectService objectService;
    private PageLinkRenderer plr;
    private String realm;
    private String externalImageLink;

    public SpecializedRenderEngine(RenderEngine renderEngine, RWikiObjectService objectService, PageLinkRenderer plr, String realm, String externalImageLink) {
        this.externalImageLink = externalImageLink;
        this.plr = plr;
        this.realm = realm;
        this.objectService = objectService;
        this.renderEngine = renderEngine;
    }

    
    public void appendLink(StringBuffer buffer, String name, String view) {
        plr.appendLink(buffer, name, view);
    }

    public void appendLink(StringBuffer buffer, String name, String view, String anchor) {
        plr.appendLink(buffer, name, view, anchor);
    }

    public void appendCreateLink(StringBuffer buffer, String name, String view) {
        plr.appendCreateLink(buffer, name, view);
    }

    public boolean exists(String name) {
        return (objectService.exists(NameHelper.globaliseName(name, realm), realm));
    }

    public boolean showCreate() {
        return true;
    }

    public String getName() {
        return renderEngine.getName();
    }

    public String render(Reader in, RenderContext context) throws IOException {
        return renderEngine.render(in, context);
    }

    public String render(String content, RenderContext context) {
        return renderEngine.render(content, context);
    }

    public void render(Writer out, String content, RenderContext context) throws IOException {
        renderEngine.render(out, content, context);
    }

    public String getExternalImageLink() {
        return externalImageLink;
    }

    
    public RenderEngine getRenderEngine() {
        return renderEngine;
    }

    public void setRenderEngine(RenderEngine deligate) {
        this.renderEngine = deligate;
    }

    public RWikiObjectService getObjectService() {
        return objectService;
    }

    public void setObjectService(RWikiObjectService objectService) {
        this.objectService = objectService;
    }

    public PageLinkRenderer getPageLinkRenderer() {
    		return plr;
    }
    
}
