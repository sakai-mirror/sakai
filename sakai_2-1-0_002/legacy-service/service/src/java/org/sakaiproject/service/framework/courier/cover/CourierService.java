/**********************************************************************************
* $URL$
* $Id$
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

package org.sakaiproject.service.framework.courier.cover;

import org.sakaiproject.service.framework.component.cover.ComponentManager;

/**
* <p>CourierService is a static Cover for the {@link org.sakaiproject.service.framework.courier.CourierService CourierService};
* see that interface for usage details.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public class CourierService
{
	/**
	 * Access the component instance: special cover only method.
	 * @return the component instance.
	 */
	public static org.sakaiproject.service.framework.courier.CourierService getInstance()
	{
		if (ComponentManager.CACHE_SINGLETONS)
		{
			if (m_instance == null) m_instance = (org.sakaiproject.service.framework.courier.CourierService) ComponentManager.get(org.sakaiproject.service.framework.courier.CourierService.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.service.framework.courier.CourierService) ComponentManager.get(org.sakaiproject.service.framework.courier.CourierService.class);
		}
	}
	private static org.sakaiproject.service.framework.courier.CourierService m_instance = null;



	public static java.lang.String SERVICE_NAME = org.sakaiproject.service.framework.courier.CourierService.SERVICE_NAME;

	public static void deliver(org.sakaiproject.service.framework.courier.Delivery param0)
	{
		org.sakaiproject.service.framework.courier.CourierService service = getInstance();
		if (service == null)
			return;

		service.deliver(param0);
	}

	public static java.util.List getDeliveries(java.lang.String param0)
	{
		org.sakaiproject.service.framework.courier.CourierService service = getInstance();
		if (service == null)
			return null;

		return service.getDeliveries(param0);
	}

	public static boolean hasDeliveries(java.lang.String param0)
	{
		org.sakaiproject.service.framework.courier.CourierService service = getInstance();
		if (service == null)
			return false;

		return service.hasDeliveries(param0);
	}

	public static void clear(java.lang.String param0, java.lang.String param1)
	{
		org.sakaiproject.service.framework.courier.CourierService service = getInstance();
		if (service == null)
			return;

		service.clear(param0, param1);
	}

	public static void clear(java.lang.String param0)
	{
		org.sakaiproject.service.framework.courier.CourierService service = getInstance();
		if (service == null)
			return;

		service.clear(param0);
	}
}



