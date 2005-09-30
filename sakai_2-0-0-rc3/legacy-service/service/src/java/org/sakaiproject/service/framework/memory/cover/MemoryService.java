/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/framework/memory/cover/MemoryService.java,v 1.1 2005/05/12 15:45:39 ggolden.umich.edu Exp $
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

package org.sakaiproject.service.framework.memory.cover;

import org.sakaiproject.service.framework.component.cover.ComponentManager;

/**
* <p>MemoryService is a static Cover for the {@link org.sakaiproject.service.framework.memory.MemoryService MemoryService};
* see that interface for usage details.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.1 $
*/
public class MemoryService
{
	/**
	 * Access the component instance: special cover only method.
	 * @return the component instance.
	 */
	public static org.sakaiproject.service.framework.memory.MemoryService getInstance()
	{
		if (ComponentManager.CACHE_SINGLETONS)
		{
			if (m_instance == null) m_instance = (org.sakaiproject.service.framework.memory.MemoryService) ComponentManager.get(org.sakaiproject.service.framework.memory.MemoryService.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.service.framework.memory.MemoryService) ComponentManager.get(org.sakaiproject.service.framework.memory.MemoryService.class);
		}
	}
	private static org.sakaiproject.service.framework.memory.MemoryService m_instance = null;



	public static long getAvailableMemory()
	{
		org.sakaiproject.service.framework.memory.MemoryService service = getInstance();
		if (service == null)
			return 0;

		return service.getAvailableMemory();
	}

	public static void resetCachers() throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.framework.memory.MemoryService service = getInstance();
		if (service == null)
			return;

		service.resetCachers();
	}

	public static void registerCacher(org.sakaiproject.service.framework.memory.Cacher param0)
	{
		org.sakaiproject.service.framework.memory.MemoryService service = getInstance();
		if (service == null)
			return;

		service.registerCacher(param0);
	}

	public static void unregisterCacher(org.sakaiproject.service.framework.memory.Cacher param0)
	{
		org.sakaiproject.service.framework.memory.MemoryService service = getInstance();
		if (service == null)
			return;

		service.unregisterCacher(param0);
	}

	public static org.sakaiproject.service.framework.memory.Cache newCache(org.sakaiproject.service.framework.memory.CacheRefresher param0, java.lang.String param1)
	{
		org.sakaiproject.service.framework.memory.MemoryService service = getInstance();
		if (service == null)
			return null;

		return service.newCache(param0, param1);
	}

	public static org.sakaiproject.service.framework.memory.Cache newHardCache(org.sakaiproject.service.framework.memory.CacheRefresher param0, java.lang.String param1)
	{
		org.sakaiproject.service.framework.memory.MemoryService service = getInstance();
		if (service == null)
			return null;

		return service.newHardCache(param0, param1);
	}

	public static org.sakaiproject.service.framework.memory.Cache newCache(org.sakaiproject.service.framework.memory.CacheRefresher param0, long param1)
	{
		org.sakaiproject.service.framework.memory.MemoryService service = getInstance();
		if (service == null)
			return null;

		return service.newCache(param0, param1);
	}

	public static org.sakaiproject.service.framework.memory.Cache newHardCache(org.sakaiproject.service.framework.memory.CacheRefresher param0, long param1)
	{
		org.sakaiproject.service.framework.memory.MemoryService service = getInstance();
		if (service == null)
			return null;

		return service.newHardCache(param0, param1);
	}

	public static org.sakaiproject.service.framework.memory.Cache newCache()
	{
		org.sakaiproject.service.framework.memory.MemoryService service = getInstance();
		if (service == null)
			return null;

		return service.newCache();
	}

	public static org.sakaiproject.service.framework.memory.Cache newHardCache()
	{
		org.sakaiproject.service.framework.memory.MemoryService service = getInstance();
		if (service == null)
			return null;

		return service.newHardCache();
	}

	public static org.sakaiproject.service.framework.memory.Cache newSiteCache(org.sakaiproject.service.framework.memory.CacheRefresher param0, java.lang.String param1)
	{
		org.sakaiproject.service.framework.memory.MemoryService service = getInstance();
		if (service == null)
			return null;

		return service.newSiteCache(param0, param1);
	}

	public static java.lang.String getStatus()
	{
		org.sakaiproject.service.framework.memory.MemoryService service = getInstance();
		if (service == null)
			return null;

		return service.getStatus();
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/framework/memory/cover/MemoryService.java,v 1.1 2005/05/12 15:45:39 ggolden.umich.edu Exp $
*
**********************************************************************************/
