/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/framework/cluster/cover/ClusterService.java,v 1.1 2005/05/12 15:45:34 ggolden.umich.edu Exp $
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

package org.sakaiproject.service.framework.cluster.cover;

import org.sakaiproject.service.framework.component.cover.ComponentManager;

/**
* <p>ClusterService is a static Cover for the {@link org.sakaiproject.service.framework.cluster.ClusterService ClusterService};
* see that interface for usage details.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.1 $
*/
public class ClusterService
{
	/**
	 * Access the component instance: special cover only method.
	 * @return the component instance.
	 */
	public static org.sakaiproject.service.framework.cluster.ClusterService getInstance()
	{
		if (ComponentManager.CACHE_SINGLETONS)
		{
			if (m_instance == null) m_instance = (org.sakaiproject.service.framework.cluster.ClusterService) ComponentManager.get(org.sakaiproject.service.framework.cluster.ClusterService.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.service.framework.cluster.ClusterService) ComponentManager.get(org.sakaiproject.service.framework.cluster.ClusterService.class);
		}
	}
	private static org.sakaiproject.service.framework.cluster.ClusterService m_instance = null;

	public static java.util.List getServers()
	{
		org.sakaiproject.service.framework.cluster.ClusterService service = getInstance();
		if (service == null)
			return null;

		return service.getServers();
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/framework/cluster/cover/ClusterService.java,v 1.1 2005/05/12 15:45:34 ggolden.umich.edu Exp $
*
**********************************************************************************/
