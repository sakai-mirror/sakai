/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/news/cover/NewsService.java,v 1.6 2004/07/22 17:04:22 janderse.umich.edu Exp $
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

package org.sakaiproject.service.legacy.news.cover;

import org.sakaiproject.service.framework.component.cover.ComponentManager;

/**
* <p>NewsService is a static Cover for the {@link org.sakaiproject.service.legacy.news.NewsService NewsService};
* see that interface for usage details.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.6 $
*/
public class NewsService
{
	/**
	 * Access the component instance: special cover only method.
	 * @return the component instance.
	 */
	public static org.sakaiproject.service.legacy.news.NewsService getInstance()
	{
		if (ComponentManager.CACHE_SINGLETONS)
		{
			if (m_instance == null) m_instance = (org.sakaiproject.service.legacy.news.NewsService) ComponentManager.get(org.sakaiproject.service.legacy.news.NewsService.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.service.legacy.news.NewsService) ComponentManager.get(org.sakaiproject.service.legacy.news.NewsService.class);
		}
	}
	private static org.sakaiproject.service.legacy.news.NewsService m_instance = null;



	public static java.lang.String SERVICE_NAME = org.sakaiproject.service.legacy.news.NewsService.SERVICE_NAME;
	public static java.lang.String REFERENCE_ROOT = org.sakaiproject.service.legacy.news.NewsService.REFERENCE_ROOT;

	public static java.util.List getChannels()
	{
		org.sakaiproject.service.legacy.news.NewsService service = getInstance();
		if (service == null)
			return null;

		return service.getChannels();
	}

	public static void removeChannel(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.news.NewsService service = getInstance();
		if (service == null)
			return;

		service.removeChannel(param0);
	}

	public static java.util.List getNewsitems(java.lang.String param0) throws org.sakaiproject.service.legacy.news.NewsConnectionException, org.sakaiproject.service.legacy.news.NewsFormatException
	{
		org.sakaiproject.service.legacy.news.NewsService service = getInstance();
		if (service == null)
			return null;

		return service.getNewsitems(param0);
	}

	public static java.util.List getNewsitems(java.lang.String param0, org.sakaiproject.util.Filter param1) throws org.sakaiproject.service.legacy.news.NewsConnectionException, org.sakaiproject.service.legacy.news.NewsFormatException
	{
		org.sakaiproject.service.legacy.news.NewsService service = getInstance();
		if (service == null)
			return null;

		return service.getNewsitems(param0, param1);
	}

	public static boolean isUpdateAvailable(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.news.NewsService service = getInstance();
		if (service == null)
			return false;

		return service.isUpdateAvailable(param0);
	}

	public static org.sakaiproject.service.legacy.news.NewsChannel getChannel(java.lang.String param0) throws org.sakaiproject.service.legacy.news.NewsConnectionException, org.sakaiproject.service.legacy.news.NewsFormatException
	{
		org.sakaiproject.service.legacy.news.NewsService service = getInstance();
		if (service == null)
			return null;

		return service.getChannel(param0);
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/news/cover/NewsService.java,v 1.6 2004/07/22 17:04:22 janderse.umich.edu Exp $
*
**********************************************************************************/
