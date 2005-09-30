/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/authentication/src/java/org/sakaiproject/api/common/authentication/cover/AuthenticationManager.java,v 1.1 2005/04/06 19:59:01 ggolden.umich.edu Exp $
 *
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.api.common.authentication.cover;

import org.sakaiproject.api.kernel.component.cover.ComponentManager;

/**
 * <p>
 * IdManager is a static Cover for the {@link org.sakaiproject.api.kernel.id.IdManager IdManager}; see that interface for usage details.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class AuthenticationManager
{
	/** Possibly cached component instance. */
	private static org.sakaiproject.api.common.authentication.AuthenticationManager m_instance = null;

	/**
	 * Access the component instance: special cover only method.
	 * 
	 * @return the component instance.
	 */
	public static org.sakaiproject.api.common.authentication.AuthenticationManager getInstance()
	{
		if (ComponentManager.CACHE_COMPONENTS)
		{
			if (m_instance == null)
					m_instance = (org.sakaiproject.api.common.authentication.AuthenticationManager) ComponentManager
							.get(org.sakaiproject.api.common.authentication.AuthenticationManager.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.api.common.authentication.AuthenticationManager) ComponentManager
					.get(org.sakaiproject.api.common.authentication.AuthenticationManager.class);
		}
	}

	public static org.sakaiproject.api.common.authentication.Authentication authenticate(
			org.sakaiproject.api.common.authentication.Evidence param0)
			throws org.sakaiproject.api.common.authentication.AuthenticationException
	{
		org.sakaiproject.api.common.authentication.AuthenticationManager manager = getInstance();
		if (manager == null) return null;

		return manager.authenticate(param0);
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/common/authentication/src/java/org/sakaiproject/api/common/authentication/cover/AuthenticationManager.java,v 1.1 2005/04/06 19:59:01 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
