/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/event/cover/EventTrackingService.java,v 1.6 2004/07/22 17:04:22 janderse.umich.edu Exp $
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

package org.sakaiproject.service.legacy.event.cover;

import org.sakaiproject.service.framework.component.cover.ComponentManager;

/**
* <p>EventTrackingService is a static Cover for the {@link org.sakaiproject.service.legacy.event.EventTrackingService EventTrackingService};
* see that interface for usage details.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.6 $
*/
public class EventTrackingService
{
	/**
	 * Access the component instance: special cover only method.
	 * @return the component instance.
	 */
	public static org.sakaiproject.service.legacy.event.EventTrackingService getInstance()
	{
		if (ComponentManager.CACHE_SINGLETONS)
		{
			if (m_instance == null) m_instance = (org.sakaiproject.service.legacy.event.EventTrackingService) ComponentManager.get(org.sakaiproject.service.legacy.event.EventTrackingService.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.service.legacy.event.EventTrackingService) ComponentManager.get(org.sakaiproject.service.legacy.event.EventTrackingService.class);
		}
	}
	private static org.sakaiproject.service.legacy.event.EventTrackingService m_instance = null;



	public static java.lang.String SERVICE_NAME = org.sakaiproject.service.legacy.event.EventTrackingService.SERVICE_NAME;

	public static void addPriorityObserver(java.util.Observer param0)
	{
		org.sakaiproject.service.legacy.event.EventTrackingService service = getInstance();
		if (service == null)
			return;

		service.addPriorityObserver(param0);
	}

	public static void addLocalObserver(java.util.Observer param0)
	{
		org.sakaiproject.service.legacy.event.EventTrackingService service = getInstance();
		if (service == null)
			return;

		service.addLocalObserver(param0);
	}

	public static org.sakaiproject.service.legacy.event.Event newEvent(java.lang.String param0, java.lang.String param1, boolean param2)
	{
		org.sakaiproject.service.legacy.event.EventTrackingService service = getInstance();
		if (service == null)
			return null;

		return service.newEvent(param0, param1, param2);
	}

	public static org.sakaiproject.service.legacy.event.Event newEvent(java.lang.String param0, java.lang.String param1, boolean param2, int param3)
	{
		org.sakaiproject.service.legacy.event.EventTrackingService service = getInstance();
		if (service == null)
			return null;

		return service.newEvent(param0, param1, param2, param3);
	}

	public static void addObserver(java.util.Observer param0)
	{
		org.sakaiproject.service.legacy.event.EventTrackingService service = getInstance();
		if (service == null)
			return;

		service.addObserver(param0);
	}

	public static void deleteObserver(java.util.Observer param0)
	{
		org.sakaiproject.service.legacy.event.EventTrackingService service = getInstance();
		if (service == null)
			return;

		service.deleteObserver(param0);
	}

	public static void post(org.sakaiproject.service.legacy.event.Event param0, org.sakaiproject.service.framework.session.UsageSession param1)
	{
		org.sakaiproject.service.legacy.event.EventTrackingService service = getInstance();
		if (service == null)
			return;

		service.post(param0, param1);
	}

	public static void post(org.sakaiproject.service.legacy.event.Event param0)
	{
		org.sakaiproject.service.legacy.event.EventTrackingService service = getInstance();
		if (service == null)
			return;

		service.post(param0);
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/event/cover/EventTrackingService.java,v 1.6 2004/07/22 17:04:22 janderse.umich.edu Exp $
*
**********************************************************************************/
