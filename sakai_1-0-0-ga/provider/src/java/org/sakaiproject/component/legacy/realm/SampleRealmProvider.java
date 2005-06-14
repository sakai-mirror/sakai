/**********************************************************************************
*
* $Header: /cvs/sakai/provider/src/java/org/sakaiproject/component/legacy/realm/SampleRealmProvider.java,v 1.2 2004/07/08 02:12:08 ggolden.umich.edu Exp $
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

// package
package org.sakaiproject.component.legacy.realm;

// imports
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.legacy.realm.Abilities;
import org.sakaiproject.service.legacy.realm.Realm;
import org.sakaiproject.service.legacy.realm.RealmProvider;
import org.sakaiproject.service.legacy.realm.cover.RealmService;

/**
* <p>Sample of a RealmProvider</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.2 $
*/
public class SampleRealmProvider
	implements RealmProvider
{
	/*******************************************************************************
	* Dependencies and their setter methods
	*******************************************************************************/

	/** Dependency: logging service */
	protected Logger m_logger = null;

	/**
	 * Dependency: logging service.
	 * @param service The logging service.
	 */
	public void setLogger(Logger service)
	{
		m_logger = service;
	}

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		try
		{
			m_logger.info(this +".init()");
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}

	} // init

	/**
	* Returns to uninitialized state.
	*
	* You can use this method to release resources thet your Service
	* allocated when Turbine shuts down.
	*/
	public void destroy()
	{

		m_logger.info(this +".destroy()");

	} // destroy

	/*******************************************************************************
	* RealmProvider implementation
	*******************************************************************************/

	/** A collection of user ids. */
	protected HashSet m_users = null;

	/**
	* Construct.
	*/
	public SampleRealmProvider()
	{
		// fill a set of users
		m_users = new HashSet();
		m_users.add("sample");
		m_users.add("user");
		
	}	// SampleRealmProvider

	/**
	* Access the Abilities for this particular user in the external realm.
	* @param realm The realm (to provide roles).
	* @param id The external realm id.
	* @param userId The user Id.
	* @return an Abilities object for this user (may be empty).
	*/
	public Abilities getAbilities(Realm realm, String id, String user)
	{
		Abilities abilities = RealmService.newAbilities();

		// if the user is in the list and this is the "sakai" external realm
		if ((m_users.contains(user)) && ("sakai".equals(id)))
		{
			// give the "maintain" role
			abilities.add(realm.getRole("maintain"));
		}
		
		return abilities;

	}	// getAbilities

	/**
	* Access the userId - Abilities Map for all know users.
	* @param realm The realm (to provide roles).
	* @param id The external realm id.
	* @return the userId - Abilities Map for all know users (may be empty).
	*/
	public Map getAbilities(Realm realm, String id)
	{
		Map rv = new HashMap();
		if ("sakai".equals(id))
		{
			// for the abilities (the same for each user)
			Abilities abilities = RealmService.newAbilities();
			abilities.add(realm.getRole("maintain"));

			// put each user with the abilities in the map
			for (Iterator it = m_users.iterator(); it.hasNext(); )
			{
				String userId = (String) it.next();
				rv.put(userId, abilities);
			}
		}

		return rv;

	}	// getAbilities

}	// SampleRealmProvider

/**********************************************************************************
*
* $Header: /cvs/sakai/provider/src/java/org/sakaiproject/component/legacy/realm/SampleRealmProvider.java,v 1.2 2004/07/08 02:12:08 ggolden.umich.edu Exp $
*
**********************************************************************************/
