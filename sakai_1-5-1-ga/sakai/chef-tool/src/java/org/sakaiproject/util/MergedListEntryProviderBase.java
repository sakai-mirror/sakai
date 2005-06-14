/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/util/MergedListEntryProviderBase.java,v 1.8 2005/02/11 21:21:59 janderse.umich.edu Exp $
*
***********************************************************************************
*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
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
package org.sakaiproject.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;

/**
 * Collects common functionality between MergedListEntry providers.
 */
public abstract class MergedListEntryProviderBase implements MergedList.EntryProvider
{
    /* (non-Javadoc)
     * @see org.sakaiproject.util.MergedList.EntryProvider#isUserChannel(java.lang.Object)
     */
    public boolean isUserChannel(Object channel)
    {
        String context = getContext(channel);
        
        return (context == null ? false : SiteService.isUserSite(context));
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.util.MergedList.EntryProvider#isSpecialSite(java.lang.Object)
     */
    public boolean isSpecialSite(Object channel)
    {
        String context = getContext(channel);
        
        return (context == null ? true : SiteService.isSpecialSite(context));
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.util.MergedList.EntryProvider#getSiteUserId(java.lang.Object)
     */
    public String getSiteUserId(Object channel)
    {
		String context = getContext(channel);
		
        return (context == null ? "" : SiteService.getSiteUserId(context));
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.util.MergedList.EntryProvider#getSite(java.lang.Object)
     */
    public Site getSite(Object channel)
    {
        try
        {
            String context = getContext(channel);
            
            return (context == null ? null : SiteService.getSite(context));
        }
        
        catch (IdUnusedException e)
        {
            return null;
        }
    }
    
	/* (non-Javadoc)
	 * @see org.chefproject.actions.MergedEntryList.EntryProvider#getIterator()
	 */
	public Iterator getIterator()
	{
		List siteList = SiteService.getSites(org.sakaiproject.service.legacy.site.SiteService.SelectionType.ACCESS,
						null, null, null, org.sakaiproject.service.legacy.site.SiteService.SortType.TITLE_ASC, null);

		List objectList = new ArrayList();
		
		Iterator it = siteList.iterator();
		
		while ( it.hasNext() )
		{
		    Site curSite = (Site)it.next();
		    
		    if ( curSite != null )
		    {
				Object object = makeObjectFromSiteId(curSite.getId());

				if ( object != null )
				{
				    objectList.add(object);
				}
		    }
		}
		
		return objectList.iterator(); 
	}
	
	/**
	 * Make a channel/calendar/etc. in an generic way. 
	 */
	public abstract Object makeObjectFromSiteId(String siteId);
 }
