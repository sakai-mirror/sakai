/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/alias/cover/AliasService.java,v 1.1 2005/05/12 15:45:40 ggolden.umich.edu Exp $
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

package org.sakaiproject.service.legacy.alias.cover;

import org.sakaiproject.service.framework.component.cover.ComponentManager;

/**
* <p>AliasService is a static Cover for the {@link org.sakaiproject.service.legacy.alias.AliasService AliasService};
* see that interface for usage details.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.1 $
*/
public class AliasService
{
	/**
	 * Access the component instance: special cover only method.
	 * @return the component instance.
	 */
	public static org.sakaiproject.service.legacy.alias.AliasService getInstance()
	{
		if (ComponentManager.CACHE_SINGLETONS)
		{
			if (m_instance == null) m_instance = (org.sakaiproject.service.legacy.alias.AliasService) ComponentManager.get(org.sakaiproject.service.legacy.alias.AliasService.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.service.legacy.alias.AliasService) ComponentManager.get(org.sakaiproject.service.legacy.alias.AliasService.class);
		}
	}
	private static org.sakaiproject.service.legacy.alias.AliasService m_instance = null;

	public static java.lang.String SERVICE_NAME = org.sakaiproject.service.legacy.alias.AliasService.SERVICE_NAME;
	public static java.lang.String REFERENCE_ROOT = org.sakaiproject.service.legacy.alias.AliasService.REFERENCE_ROOT;
	public static java.lang.String SECURE_ADD_ALIAS = org.sakaiproject.service.legacy.alias.AliasService.SECURE_ADD_ALIAS;
	public static java.lang.String SECURE_UPDATE_ALIAS = org.sakaiproject.service.legacy.alias.AliasService.SECURE_UPDATE_ALIAS;
	public static java.lang.String SECURE_REMOVE_ALIAS = org.sakaiproject.service.legacy.alias.AliasService.SECURE_REMOVE_ALIAS;

	public static boolean allowSetAlias(java.lang.String param0, java.lang.String param1)
	{
		org.sakaiproject.service.legacy.alias.AliasService service = getInstance();
		if (service == null)
			return false;

		return service.allowSetAlias(param0, param1);
	}

	public static void setAlias(java.lang.String param0, java.lang.String param1) throws org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.IdInvalidException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.alias.AliasService service = getInstance();
		if (service == null)
			return;

		service.setAlias(param0, param1);
	}

	public static boolean allowRemoveAlias(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.alias.AliasService service = getInstance();
		if (service == null)
			return false;

		return service.allowRemoveAlias(param0);
	}

	public static void removeAlias(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.InUseException
	{
		org.sakaiproject.service.legacy.alias.AliasService service = getInstance();
		if (service == null)
			return;

		service.removeAlias(param0);
	}

	public static boolean allowRemoveTargetAliases(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.alias.AliasService service = getInstance();
		if (service == null)
			return false;

		return service.allowRemoveTargetAliases(param0);
	}

	public static void removeTargetAliases(java.lang.String param0) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.alias.AliasService service = getInstance();
		if (service == null)
			return;

		service.removeTargetAliases(param0);
	}

	public static java.util.List getAliases(int param0, int param1)
	{
		org.sakaiproject.service.legacy.alias.AliasService service = getInstance();
		if (service == null)
			return null;

		return service.getAliases(param0, param1);
	}

	public static java.util.List getAliases(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.alias.AliasService service = getInstance();
		if (service == null)
			return null;

		return service.getAliases(param0);
	}

	public static java.util.List getAliases(java.lang.String param0, int param1, int param2)
	{
		org.sakaiproject.service.legacy.alias.AliasService service = getInstance();
		if (service == null)
			return null;

		return service.getAliases(param0, param1, param2);
	}

	public static int countAliases()
	{
		org.sakaiproject.service.legacy.alias.AliasService service = getInstance();
		if (service == null)
			return 0;

		return service.countAliases();
	}

	public static java.util.List searchAliases(java.lang.String param0, int param1, int param2)
	{
		org.sakaiproject.service.legacy.alias.AliasService service = getInstance();
		if (service == null)
			return null;

		return service.searchAliases(param0, param1, param2);
	}

	public static int countSearchAliases(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.alias.AliasService service = getInstance();
		if (service == null)
			return 0;

		return service.countSearchAliases(param0);
	}

	public static java.lang.String aliasReference(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.alias.AliasService service = getInstance();
		if (service == null)
			return null;

		return service.aliasReference(param0);
	}

	public static boolean allowAdd()
	{
		org.sakaiproject.service.legacy.alias.AliasService service = getInstance();
		if (service == null)
			return false;

		return service.allowAdd();
	}

	public static boolean allowEdit(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.alias.AliasService service = getInstance();
		if (service == null)
			return false;

		return service.allowEdit(param0);
	}

	public static void commit(org.sakaiproject.service.legacy.alias.AliasEdit param0)
	{
		org.sakaiproject.service.legacy.alias.AliasService service = getInstance();
		if (service == null)
			return;

		service.commit(param0);
	}

	public static org.sakaiproject.service.legacy.alias.AliasEdit add(java.lang.String param0) throws org.sakaiproject.exception.IdInvalidException, org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.alias.AliasService service = getInstance();
		if (service == null)
			return null;

		return service.add(param0);
	}

	public static void remove(org.sakaiproject.service.legacy.alias.AliasEdit param0) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.alias.AliasService service = getInstance();
		if (service == null)
			return;

		service.remove(param0);
	}

	public static void cancel(org.sakaiproject.service.legacy.alias.AliasEdit param0)
	{
		org.sakaiproject.service.legacy.alias.AliasService service = getInstance();
		if (service == null)
			return;

		service.cancel(param0);
	}

	public static java.lang.String getTarget(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException
	{
		org.sakaiproject.service.legacy.alias.AliasService service = getInstance();
		if (service == null)
			return null;

		return service.getTarget(param0);
	}

	public static org.sakaiproject.service.legacy.alias.AliasEdit edit(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.InUseException
	{
		org.sakaiproject.service.legacy.alias.AliasService service = getInstance();
		if (service == null)
			return null;

		return service.edit(param0);
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/alias/cover/AliasService.java,v 1.1 2005/05/12 15:45:40 ggolden.umich.edu Exp $
*
**********************************************************************************/
