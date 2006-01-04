/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.service.legacy.resource.cover;

import org.sakaiproject.service.framework.component.cover.ComponentManager;

/**
* <p>EntityManager is a static Cover for the {@link org.sakaiproject.service.legacy.entity.EntityManager EntityManager};
* see that interface for usage details.</p>
* 
* @author Sakai Software Development Team
*/
public class EntityManager
{
	/**
	 * Access the component instance: special cover only method.
	 * @return the component instance.
	 */
	public static org.sakaiproject.service.legacy.entity.EntityManager getInstance()
	{
		if (ComponentManager.CACHE_SINGLETONS)
		{
			if (m_instance == null) m_instance = (org.sakaiproject.service.legacy.entity.EntityManager) ComponentManager.get(org.sakaiproject.service.legacy.entity.EntityManager.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.service.legacy.entity.EntityManager) ComponentManager.get(org.sakaiproject.service.legacy.entity.EntityManager.class);
		}
	}
	private static org.sakaiproject.service.legacy.entity.EntityManager m_instance = null;


	public static void registerEntityProducer(org.sakaiproject.service.legacy.entity.EntityProducer param0)
	{
		org.sakaiproject.service.legacy.entity.EntityManager service = getInstance();
		if (service == null)
			return;

		service.registerEntityProducer(param0);
	}

	public static java.util.List getEntityProducers()
	{
		org.sakaiproject.service.legacy.entity.EntityManager service = getInstance();
		if (service == null)
			return null;

		return service.getEntityProducers();
	}

	public static org.sakaiproject.service.legacy.entity.Reference newReference(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.entity.EntityManager service = getInstance();
		if (service == null)
			return null;

		return service.newReference(param0);
	}

	public static org.sakaiproject.service.legacy.entity.Reference newReference(org.sakaiproject.service.legacy.entity.Reference param0)
	{
		org.sakaiproject.service.legacy.entity.EntityManager service = getInstance();
		if (service == null)
			return null;

		return service.newReference(param0);
	}

	public static java.util.List newReferenceList()
	{
		org.sakaiproject.service.legacy.entity.EntityManager service = getInstance();
		if (service == null)
			return null;

		return service.newReferenceList();
	}

	public static java.util.List newReferenceList(java.util.List param0)
	{
		org.sakaiproject.service.legacy.entity.EntityManager service = getInstance();
		if (service == null)
			return null;

		return service.newReferenceList(param0);
	}
}



