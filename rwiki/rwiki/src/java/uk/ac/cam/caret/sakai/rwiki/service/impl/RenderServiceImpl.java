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
package uk.ac.cam.caret.sakai.rwiki.service.impl;

import org.radeox.api.engine.RenderEngine;
import org.radeox.api.engine.context.RenderContext;
import org.sakaiproject.service.framework.log.Logger;

import uk.ac.cam.caret.sakai.rwiki.model.NameHelper;
import uk.ac.cam.caret.sakai.rwiki.model.RWikiObject;
import uk.ac.cam.caret.sakai.rwiki.radeox.service.CachableRenderContext;
import uk.ac.cam.caret.sakai.rwiki.radeox.service.RenderCache;
import uk.ac.cam.caret.sakai.rwiki.radeox.service.RenderContextFactory;
import uk.ac.cam.caret.sakai.rwiki.radeox.service.RenderEngineFactory;
import uk.ac.cam.caret.sakai.rwiki.service.PageLinkRenderer;
import uk.ac.cam.caret.sakai.rwiki.service.RenderService;
import uk.ac.cam.caret.sakai.rwiki.tool.RWikiServlet;

/**
 * @author andrew
 *
 */
public class RenderServiceImpl implements RenderService {
    private Logger log;

    private RenderEngineFactory renderEngineFactory;
    private RenderContextFactory renderContextFactory;
    private RenderCache renderCache;
  
    public RenderCache getRenderCache() {
		return renderCache;
	}

	public void setRenderCache(RenderCache renderCache) {
		this.renderCache = renderCache;
	}

	/*
     *  (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.RenderService#publicRenderPage(uk.ac.cam.caret.sakai.rwiki.model.RWikiObject, java.lang.String)
     */
	public String renderPublicPage(RWikiObject rwo, String user) {
		return renderPublicPage(rwo,user,rwo.getRealm());
	}

	/*
	 *  (non-Javadoc)
	 * @see uk.ac.cam.caret.sakai.rwiki.service.RenderService#publicRenderPage(uk.ac.cam.caret.sakai.rwiki.model.RWikiObject, java.lang.String, java.lang.String)
	 */
	public String renderPublicPage(RWikiObject rwo, String user, String realm) {
		PublicPageLinkRendererImpl plr = new PublicPageLinkRendererImpl(NameHelper.localizeSpace(rwo.getName(),realm),realm);
		return renderPage(rwo,user,realm,plr);
	}
    
    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.RenderService#renderPage(uk.ac.cam.caret.sakai.rwiki.tool.service.RWikiObject)
     */
    public String renderPage(RWikiObject rwo, String user) {
        return renderPage(rwo, user, true);
    }

    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.RenderService#renderPage(uk.ac.cam.caret.sakai.rwiki.tool.service.RWikiObject, java.lang.String)
     */
    public String renderPage(RWikiObject rwo, String user, String realm) {
        PageLinkRendererImpl plr = new PageLinkRendererImpl(NameHelper.localizeSpace(rwo.getName(), realm), realm);
        return renderPage(rwo, user, realm, plr);
    }

    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.RenderService#renderPage(uk.ac.cam.caret.sakai.rwiki.tool.service.RWikiObject, uk.ac.cam.caret.sakai.rwiki.service.PageLinkRenderer)
     */
    public String renderPage(RWikiObject rwo, String user, PageLinkRenderer plr) {

        return renderPage(rwo, user, rwo.getRealm(), plr);
        
    }

    
    public String renderPage(RWikiObject rwo, String user, String realm, PageLinkRenderer plr) {
        long start = System.currentTimeMillis();
        String renderedPage = null;
        String cacheKey = getCacheKey(rwo,plr);
        try {
            if ( plr.canUseCache() && renderCache != null ) {
        	       renderedPage = renderCache.getRenderedContent(cacheKey);
               if ( renderedPage != null ) {
               	if ( RWikiServlet.isLogResponse() )
               		log.info("Cache HIT "+cacheKey);
               	else
               		log.debug("Cache HIT "+cacheKey);
            	    return renderedPage;
              }
            } else {
            	  log.debug("Render Cache Disabled");
            }
            RenderEngine renderEngine = renderEngineFactory.getRenderEngine(realm, plr);
            RenderContext renderContext = renderContextFactory.getRenderContext(rwo, user, renderEngine);
            renderedPage = renderEngine.render(rwo.getContent(), renderContext);
            boolean canCache = false;
            if ( renderContext instanceof CachableRenderContext ) {
            		CachableRenderContext crc = (CachableRenderContext) renderContext;
            		canCache = crc.isCachable();
            }
            if ( canCache && plr.isCachable() && plr.canUseCache() ) {
            		if ( renderCache != null ) {
            			renderCache.putRenderedContent(cacheKey,renderedPage);
            			if ( RWikiServlet.isLogResponse() ) 
            				log.info("Cache PUT "+cacheKey);
            			else
            				log.debug("Cache PUT "+cacheKey);
            		} else {
            			log.debug("Could have cached output");
            		}
            } else {
            	if ( RWikiServlet.isLogResponse() )
            		log.info("Cant Cache "+cacheKey);
            	else
            	   log.debug("Cant Cache "+cacheKey);
            }
            return renderedPage;
        } finally {
            long finish = System.currentTimeMillis();
            RWikiServlet.printTimer("Render: " + rwo.getName(),start,finish);
        }
    }

    public RenderContextFactory getRenderContextFactory() {
        return renderContextFactory;
    }

    public void setRenderContextFactory(RenderContextFactory renderContextFactory) {
        this.renderContextFactory = renderContextFactory;
    }

    public RenderEngineFactory getRenderEngineFactory() {
        return renderEngineFactory;
    }

    public void setRenderEngineFactory(RenderEngineFactory renderEngineFactory) {
        this.renderEngineFactory = renderEngineFactory;
    }
   
    /**
     * Generates a key for the page taking into account the page, version and link render mecahnism
     * @param rwo
     * @param plr
     * @return
     */
    public String getCacheKey(RWikiObject rwo, PageLinkRenderer plr) {
    		String classNameHash = plr.getClass().getName();
    		classNameHash = classNameHash.substring(classNameHash.lastIndexOf("."));
    		return rwo.getId()+"."+rwo.getVersion().getTime()+"."+classNameHash;
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
        return renderPage(rwo, user, rwo.getRealm(), plr);
	}

}
