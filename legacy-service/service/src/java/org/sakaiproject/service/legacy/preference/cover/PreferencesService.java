/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/preference/cover/PreferencesService.java,v 1.1 2005/05/12 15:45:40 ggolden.umich.edu Exp $
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

package org.sakaiproject.service.legacy.preference.cover;

import org.sakaiproject.service.framework.component.cover.ComponentManager;

/**
* <p>PreferencesService is a static Cover for the {@link org.sakaiproject.service.legacy.preference.PreferencesService PreferencesService};
* see that interface for usage details.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.1 $
*/
public class PreferencesService
{
	/**
	 * Access the component instance: special cover only method.
	 * @return the component instance.
	 */
	public static org.sakaiproject.service.legacy.preference.PreferencesService getInstance()
	{
		if (ComponentManager.CACHE_SINGLETONS)
		{
			if (m_instance == null) m_instance = (org.sakaiproject.service.legacy.preference.PreferencesService) ComponentManager.get(org.sakaiproject.service.legacy.preference.PreferencesService.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.service.legacy.preference.PreferencesService) ComponentManager.get(org.sakaiproject.service.legacy.preference.PreferencesService.class);
		}
	}
	private static org.sakaiproject.service.legacy.preference.PreferencesService m_instance = null;



	public static java.lang.String SERVICE_NAME = org.sakaiproject.service.legacy.preference.PreferencesService.SERVICE_NAME;
	public static java.lang.String REFERENCE_ROOT = org.sakaiproject.service.legacy.preference.PreferencesService.REFERENCE_ROOT;
	public static java.lang.String SECURE_ADD_PREFS = org.sakaiproject.service.legacy.preference.PreferencesService.SECURE_ADD_PREFS;
	public static java.lang.String SECURE_EDIT_PREFS = org.sakaiproject.service.legacy.preference.PreferencesService.SECURE_EDIT_PREFS;
	public static java.lang.String SECURE_REMOVE_PREFS = org.sakaiproject.service.legacy.preference.PreferencesService.SECURE_REMOVE_PREFS;

	public static void commit(org.sakaiproject.service.legacy.preference.PreferencesEdit param0)
	{
		org.sakaiproject.service.legacy.preference.PreferencesService service = getInstance();
		if (service == null)
			return;

		service.commit(param0);
	}

	public static org.sakaiproject.service.legacy.preference.Preferences getPreferences(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.preference.PreferencesService service = getInstance();
		if (service == null)
			return null;

		return service.getPreferences(param0);
	}

	public static boolean allowUpdate(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.preference.PreferencesService service = getInstance();
		if (service == null)
			return false;

		return service.allowUpdate(param0);
	}

	public static org.sakaiproject.service.legacy.preference.PreferencesEdit add(java.lang.String param0) throws org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.IdUsedException
	{
		org.sakaiproject.service.legacy.preference.PreferencesService service = getInstance();
		if (service == null)
			return null;

		return service.add(param0);
	}

	public static void remove(org.sakaiproject.service.legacy.preference.PreferencesEdit param0)
	{
		org.sakaiproject.service.legacy.preference.PreferencesService service = getInstance();
		if (service == null)
			return;

		service.remove(param0);
	}

	public static void cancel(org.sakaiproject.service.legacy.preference.PreferencesEdit param0)
	{
		org.sakaiproject.service.legacy.preference.PreferencesService service = getInstance();
		if (service == null)
			return;

		service.cancel(param0);
	}

	public static org.sakaiproject.service.legacy.preference.PreferencesEdit edit(java.lang.String param0) throws org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.InUseException, org.sakaiproject.exception.IdUnusedException
	{
		org.sakaiproject.service.legacy.preference.PreferencesService service = getInstance();
		if (service == null)
			return null;

		return service.edit(param0);
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/preference/cover/PreferencesService.java,v 1.1 2005/05/12 15:45:40 ggolden.umich.edu Exp $
*
**********************************************************************************/
