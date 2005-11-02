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
package uk.ac.cam.caret.sakai.rwiki.component.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.portal.PortalService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SiteService;

import uk.ac.cam.caret.sakai.rwiki.component.model.impl.NameHelper;
import uk.ac.cam.caret.sakai.rwiki.service.api.PageLinkRenderer;
import uk.ac.cam.caret.sakai.rwiki.service.api.RenderService;
import uk.ac.cam.caret.sakai.rwiki.service.api.dao.RWikiCurrentObjectDao;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiCurrentObject;
import uk.ac.cam.caret.sakai.rwiki.service.exception.PermissionException;
import uk.ac.cam.caret.sakai.rwiki.tool.api.PopulateService;

/**
 * @author andrew
 *
 */

// FIXME: Tool

public class PopulateServiceImpl implements PopulateService {
	
	private Logger log;
	private HashMap seenPageSpaces = new HashMap();
	private List seedPages;
	
	private RWikiCurrentObjectDao dao;
    
    private RenderService renderService = null;
    private SiteService siteService = null;
    private PortalService portalService = null;
	
	/* (non-Javadoc)
	 * @see uk.ac.cam.caret.sakai.rwiki.service.api.PopulateService#populateRealm(java.lang.String, java.lang.String)
	 */
    // SAK-2514
	public void populateRealm(String user, String space, String group)
	throws PermissionException {
		synchronized (seenPageSpaces) {
			if (seenPageSpaces.get(space) == null ) {
				for ( Iterator i = seedPages.iterator(); i.hasNext(); ) {
					if (log.isDebugEnabled()) {
						log.debug("Populating space: " + space);
					}
					
					RWikiCurrentObject seed = (RWikiCurrentObject) i.next();
					
					String name = NameHelper.globaliseName(seed.getName(), space);
					log.	debug("Populating Space with "+seed.getName());
					if (dao.findByGlobalName(name) == null) {
						if (log.isDebugEnabled()) {
							log.debug("Creating Page: " + name);
						}
						log.debug("Creating Page :"+name);
                        
						RWikiCurrentObject rwo = dao.createRWikiObject(name, space);
						seed.copyTo(rwo);
                         // SAK-2513
                    
                        String owner = user;
                        try {
                            Site s = siteService.getSite(portalService.getCurrentSiteId());
                            owner =  s.getCreatedBy().getId();
                        } catch (Exception e) {
                            log.warn("Cant find who created this site, defaulting to current user for prepopulate ownership :"+owner);
                        }
                         log.debug("Populate with Owner "+owner);
                         rwo.setUser(owner);
                         rwo.setOwner(owner);
                         updateReferences(rwo,owner,space);
						rwo.setName(name);
						rwo.setRealm(group);
						dao.update(rwo,null);
						log.debug("Page Created ");
					} else {
						log.debug("Page Already exists ");
					}
				}
				seenPageSpaces.put(space, space);
			}
		}
	}
	
    // SAK-2470
    private void updateReferences(RWikiCurrentObject rwo, String user, String space) {

            // render to get a list of links
            final HashSet referenced = new HashSet();
            final String currentRealm = rwo.getRealm();

            PageLinkRenderer plr = new PageLinkRenderer() {
                public void appendLink(StringBuffer buffer, String name,
                        String view) {
                    referenced
                            .add(NameHelper.globaliseName(name, currentRealm));
                }

                public void appendLink(StringBuffer buffer, String name,
                        String view, String anchor) {
                    referenced
                            .add(NameHelper.globaliseName(name, currentRealm));
                }

                public void appendCreateLink(StringBuffer buffer, String name,
                        String view) {
                    referenced
                            .add(NameHelper.globaliseName(name, currentRealm));
                }

                public boolean isCachable() {
                    return false; // should not cache this render op
                }

                public boolean canUseCache() {
                    return false;
                }

                public void setCachable(boolean cachable) {
                }
                public void setUseCache(boolean b) {
                    
                }

            };

            renderService.renderPage(rwo, user, space, plr);

            // process the references
            StringBuffer sb = new StringBuffer();
            Iterator i = referenced.iterator();
            while (i.hasNext()) {
                sb.append("::").append(i.next());
            }
            sb.append("::");
            rwo.setReferenced(sb.toString());

        }

	
	public Logger getLog() {
		return log;
	}
	
	public void setLog(Logger log) {
		this.log = log;
	}
	
	public List getSeedPages() {
		return seedPages;
	}
	
	public void setSeedPages(List seedPages) {
		this.seedPages = seedPages;
	}
	
	public RWikiCurrentObjectDao getRWikiCurrentObjectDao() {
		return dao;
	}
	
	public void setRWikiCurrentObjectDao(RWikiCurrentObjectDao dao) {
		this.dao = dao;
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

    /**
     * @return Returns the siteService.
     */
    public SiteService getSiteService() {
        return siteService;
    }

    /**
     * @param siteService The siteService to set.
     */
    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    /**
     * @return Returns the portalService.
     */
    public PortalService getPortalService() {
        return portalService;
    }

    /**
     * @param portalService The portalService to set.
     */
    public void setPortalService(PortalService portalService) {
        this.portalService = portalService;
    }
	
	
	
	
}
