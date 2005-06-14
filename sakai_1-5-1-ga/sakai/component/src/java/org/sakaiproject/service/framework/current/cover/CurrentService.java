/**********************************************************************************
*
* $Header: /cvs/sakai/component/src/java/org/sakaiproject/service/framework/current/cover/CurrentService.java,v 1.6 2004/10/01 19:29:26 ggolden.umich.edu Exp $
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

package org.sakaiproject.service.framework.current.cover;

import org.sakaiproject.service.framework.component.cover.ComponentManager;

/**
* <p>CurrentService is a static Cover for the {@link org.sakaiproject.service.framework.current.CurrentService CurrentService};
* see that interface for usage details.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.6 $
*/
public class CurrentService
{
	/**
	 * Access the component instance: special cover only method.
	 * @return the component instance.
	 */
	public static org.sakaiproject.service.framework.current.CurrentService getInstance()
	{
		if (ComponentManager.CACHE_SINGLETONS)
		{
			if (m_instance == null) m_instance = (org.sakaiproject.service.framework.current.CurrentService) ComponentManager.get(org.sakaiproject.service.framework.current.CurrentService.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.service.framework.current.CurrentService) ComponentManager.get(org.sakaiproject.service.framework.current.CurrentService.class);
		}
	}
	private static org.sakaiproject.service.framework.current.CurrentService m_instance = null;



	public static void setInThread(java.lang.String param0, java.lang.Object param1)
	{
		org.sakaiproject.service.framework.current.CurrentService service = getInstance();
		if (service == null)
			return;

		service.setInThread(param0, param1);
	}

	public static void clearInThread()
	{
		org.sakaiproject.service.framework.current.CurrentService service = getInstance();
		if (service == null)
			return;

		service.clearInThread();
	}

	public static java.lang.Object getInThread(java.lang.String param0)
	{
		org.sakaiproject.service.framework.current.CurrentService service = getInstance();
		if (service == null)
			return null;

		return service.getInThread(param0);
	}

	public static void startThread(java.lang.String param0)
	{
		org.sakaiproject.service.framework.current.CurrentService service = getInstance();
		if (service == null)
			return;

		service.startThread(param0);
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/component/src/java/org/sakaiproject/service/framework/current/cover/CurrentService.java,v 1.6 2004/10/01 19:29:26 ggolden.umich.edu Exp $
*
**********************************************************************************/
