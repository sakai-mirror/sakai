/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/archive/cover/ArchiveService.java,v 1.1 2005/05/12 15:45:41 ggolden.umich.edu Exp $
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

package org.sakaiproject.service.legacy.archive.cover;

import org.sakaiproject.service.framework.component.cover.ComponentManager;

/**
* <p>ArchiveService is a static Cover for the {@link org.sakaiproject.service.legacy.archive.ArchiveService ArchiveService};
* see that interface for usage details.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public class ArchiveService
{
	/**
	 * Access the component instance: special cover only method.
	 * @return the component instance.
	 */
	public static org.sakaiproject.service.legacy.archive.ArchiveService getInstance()
	{
		if (ComponentManager.CACHE_SINGLETONS)
		{
			if (m_instance == null) m_instance = (org.sakaiproject.service.legacy.archive.ArchiveService) ComponentManager.get(org.sakaiproject.service.legacy.archive.ArchiveService.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.service.legacy.archive.ArchiveService) ComponentManager.get(org.sakaiproject.service.legacy.archive.ArchiveService.class);
		}
	}
	private static org.sakaiproject.service.legacy.archive.ArchiveService m_instance = null;



	public static java.lang.String SERVICE_NAME = org.sakaiproject.service.legacy.archive.ArchiveService.SERVICE_NAME;

	public static java.lang.String archive(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.archive.ArchiveService service = getInstance();
		if (service == null)
			return null;

		return service.archive(param0);
	}
	
	public static java.lang.String merge(java.lang.String param0, java.lang.String param1, java.lang.String param2)
   	{
   		org.sakaiproject.service.legacy.archive.ArchiveService service = getInstance();
   		if (service == null)
   			return null;

   		return service.merge(param0, param1, param2);
   	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/archive/cover/ArchiveService.java,v 1.1 2005/05/12 15:45:41 ggolden.umich.edu Exp $
*
**********************************************************************************/
