/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/time/cover/TimeService.java,v 1.1 2005/05/12 15:45:39 ggolden.umich.edu Exp $
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

package org.sakaiproject.service.legacy.time.cover;

import org.sakaiproject.service.framework.component.cover.ComponentManager;

/**
* <p>TimeService is a static Cover for the {@link org.sakaiproject.service.legacy.time.TimeService TimeService};
* see that interface for usage details.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.1 $
*/
public class TimeService
{
	/**
	 * Access the component instance: special cover only method.
	 * @return the component instance.
	 */
	public static org.sakaiproject.service.legacy.time.TimeService getInstance()
	{
		if (ComponentManager.CACHE_SINGLETONS)
		{
			if (m_instance == null) m_instance = (org.sakaiproject.service.legacy.time.TimeService) ComponentManager.get(org.sakaiproject.service.legacy.time.TimeService.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.service.legacy.time.TimeService) ComponentManager.get(org.sakaiproject.service.legacy.time.TimeService.class);
		}
	}
	private static org.sakaiproject.service.legacy.time.TimeService m_instance = null;



	public static org.sakaiproject.service.legacy.time.Time newTimeGmt(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.time.TimeService service = getInstance();
		if (service == null)
			return null;

		return service.newTimeGmt(param0);
	}

	public static org.sakaiproject.service.legacy.time.Time newTimeGmt(int param0, int param1, int param2, int param3, int param4, int param5, int param6)
	{
		org.sakaiproject.service.legacy.time.TimeService service = getInstance();
		if (service == null)
			return null;

		return service.newTimeGmt(param0, param1, param2, param3, param4, param5, param6);
	}

	public static org.sakaiproject.service.legacy.time.Time newTimeGmt(org.sakaiproject.service.legacy.time.TimeBreakdown param0)
	{
		org.sakaiproject.service.legacy.time.TimeService service = getInstance();
		if (service == null)
			return null;

		return service.newTimeGmt(param0);
	}

	public static org.sakaiproject.service.legacy.time.Time newTimeLocal(org.sakaiproject.service.legacy.time.TimeBreakdown param0)
	{
		org.sakaiproject.service.legacy.time.TimeService service = getInstance();
		if (service == null)
			return null;

		return service.newTimeLocal(param0);
	}

	public static org.sakaiproject.service.legacy.time.Time newTimeLocal(int param0, int param1, int param2, int param3, int param4, int param5, int param6)
	{
		org.sakaiproject.service.legacy.time.TimeService service = getInstance();
		if (service == null)
			return null;

		return service.newTimeLocal(param0, param1, param2, param3, param4, param5, param6);
	}

	public static org.sakaiproject.service.legacy.time.TimeBreakdown newTimeBreakdown(int param0, int param1, int param2, int param3, int param4, int param5, int param6)
	{
		org.sakaiproject.service.legacy.time.TimeService service = getInstance();
		if (service == null)
			return null;

		return service.newTimeBreakdown(param0, param1, param2, param3, param4, param5, param6);
	}

	public static org.sakaiproject.service.legacy.time.TimeRange newTimeRange(org.sakaiproject.service.legacy.time.Time param0, org.sakaiproject.service.legacy.time.Time param1)
	{
		org.sakaiproject.service.legacy.time.TimeService service = getInstance();
		if (service == null)
			return null;

		return service.newTimeRange(param0, param1);
	}

	public static org.sakaiproject.service.legacy.time.TimeRange newTimeRange(org.sakaiproject.service.legacy.time.Time param0)
	{
		org.sakaiproject.service.legacy.time.TimeService service = getInstance();
		if (service == null)
			return null;

		return service.newTimeRange(param0);
	}

	public static org.sakaiproject.service.legacy.time.TimeRange newTimeRange(long param0, long param1)
	{
		org.sakaiproject.service.legacy.time.TimeService service = getInstance();
		if (service == null)
			return null;

		return service.newTimeRange(param0, param1);
	}

	public static org.sakaiproject.service.legacy.time.TimeRange newTimeRange(org.sakaiproject.service.legacy.time.Time param0, org.sakaiproject.service.legacy.time.Time param1, boolean param2, boolean param3)
	{
		org.sakaiproject.service.legacy.time.TimeService service = getInstance();
		if (service == null)
			return null;

		return service.newTimeRange(param0, param1, param2, param3);
	}

	public static org.sakaiproject.service.legacy.time.TimeRange newTimeRange(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.time.TimeService service = getInstance();
		if (service == null)
			return null;

		return service.newTimeRange(param0);
	}

	public static org.sakaiproject.service.legacy.time.Time newTime(long param0)
	{
		org.sakaiproject.service.legacy.time.TimeService service = getInstance();
		if (service == null)
			return null;

		return service.newTime(param0);
	}

	public static org.sakaiproject.service.legacy.time.Time newTime()
	{
		org.sakaiproject.service.legacy.time.TimeService service = getInstance();
		if (service == null)
			return null;

		return service.newTime();
	}

	public static java.util.TimeZone getLocalTimeZone()
	{
		org.sakaiproject.service.legacy.time.TimeService service = getInstance();
		if (service == null)
			return null;

		return service.getLocalTimeZone();
	}

	public static java.util.GregorianCalendar getCalendar(java.util.TimeZone param0, int param1, int param2, int param3, int param4, int param5, int param6, int param7)
	{
		org.sakaiproject.service.legacy.time.TimeService service = getInstance();
		if (service == null)
			return null;

		return service.getCalendar(param0, param1, param2, param3, param4, param5, param6, param7);
	}

	public static org.sakaiproject.service.legacy.time.Time newTime(java.util.GregorianCalendar param0)
	{
		org.sakaiproject.service.legacy.time.TimeService service = getInstance();
		if (service == null)
			return null;

		return service.newTime(param0);
	}

	public static boolean different(org.sakaiproject.service.legacy.time.Time param0, org.sakaiproject.service.legacy.time.Time param1)
	{
		org.sakaiproject.service.legacy.time.TimeService service = getInstance();
		if (service == null)
			return false;

		return service.different(param0, param1);
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/time/cover/TimeService.java,v 1.1 2005/05/12 15:45:39 ggolden.umich.edu Exp $
*
**********************************************************************************/
