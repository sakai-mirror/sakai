/**********************************************************************************
*
* $Header: /cvs/sakai/ctools/src/java/org/sakaiproject/component/legacy/realm/UnivOfMichRealmProvider.java,v 1.7 2004/11/25 02:25:56 janderse.umich.edu Exp $
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

package org.sakaiproject.component.legacy.realm;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.legacy.realm.RealmProvider;
import org.sakaiproject.util.StringUtil;
import org.sakaiproject.util.UmiacClient;

/**
* <p>UnivOfMichRealmProvider with information from U of M's UMIAC system.</p>
* <p>ExternalRealmId is converted into a UMIAC class/section identifier</p>
*
* @author University of Michigan, CTools / Sakai Software Development Team
* @version $Revision: 1.7 $
*/
public class UnivOfMichRealmProvider implements RealmProvider
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
	}

	/**
	 * Final cleanup.
	 */
	public void destroy()
	{
		m_logger.info(this +".destroy()");
	}

	/*******************************************************************************
	* RealmProvider implementation
	*******************************************************************************/

	/** My UMIAC client interface. */
	protected UmiacClient m_umiac = UmiacClient.getInstance();

	/**
	* Construct.
	*/
	public UnivOfMichRealmProvider()
	{
	} // UnivOfMichRealmProvider

	/**
	 * {@inheritDoc}
	 */
	public String getRole(String id, String user)
	{
		if (id == null) return null;

		String rv = null;

		// compute the set of individual umiac ids that are packed into id
		String ids[] = unpackId(id);

		// use the user's external list of sites : Map of provider id -> role for this user
		Map map = m_umiac.getUserSections(user);
		if (!map.isEmpty())
		{
			for (int i = 0; i < ids.length; i++)
			{
				// does this one of my ids exist in the map?
				String roleId = (String) map.get(ids[i]);
				if (roleId != null)
				{
					// prefer "Instructor" to "Student" in roles
					if (!"Instructor".equals(rv))
					{
						rv = roleId;
					}
				}
			}
		}

		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map getUserRolesForRealm(String id)
	{
		if (id == null) return new HashMap();

		// compute the set of individual umiac ids that are packed into id
		String ids[] = unpackId(id);

		// get a Map of userId - role string (Student, Instructor) for these umiac ids
		try
		{
			Map map = m_umiac.getGroupRoles(ids);

			return map;
		}
		catch (IdUnusedException e)
		{
			return new HashMap();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Map getRealmRolesForUser(String userId)
	{
		if (userId == null) return new HashMap();

		// get the user's external list of sites : Map of provider id -> role for this user
		Map map = m_umiac.getUserSections(userId);

		// transfer to our special map
		MyMap rv = new MyMap();
		rv.putAll(map);

		return rv;
	}

	/**
	 * Unpack a multiple id that may contain many full ids connected with "+", each
	 * of which may have multiple sections enclosed in []
	 * @param id The multiple group id.
	 * @return An array of strings of real umiac group ids, one for each in the multiple.
	 */
	public String[] unpackId(String id)
	{
		if (id == null) return null;

		Vector returnVector = new Vector();

		// first unpack the full ids
		String[] first = unpackIdFull(id);

		// then, for each, unpack the sections
		for (int i = 0; i < first.length; i++)
		{
			String[] second = unpackIdSections(first[i]);
			for (int s = 0; s < second.length; s++)
			{
				returnVector.add(second[s]);
			}
		}

		String[] rv = (String[]) returnVector.toArray(new String[returnVector.size()]);

		return rv;
	}

	/**
	 * Unpack a crosslisted multiple groupId into a set of individual group ids.
	 * 2002,2,A,EDUC,504,[001,002,003,004,006]+2002,2,A,LSA,101,[002,003]+etc
	 * @param id The crosslisted multiple group id.
	 * @return An array of strings of real umiac group ids, one for each in the multiple.
	 */
	protected String[] unpackIdFull(String id)
	{
		String[] rv = null;

		// if there is not a '+' return just the id
		int pos = id.indexOf('+');
		if (pos == -1)
		{
			rv = new String[1];
			rv[0] = id;
		}
		else
		{
			// split by the "+" separators
			rv = StringUtil.split(id, "+");
		}

		return rv;
	}

	/**
	 * Unpack a multiple section groupId into a set of individual group ids.
	 * 2002,2,A,EDUC,504,[001,002,003,004,006]
	 * @param id The multiple section group id.
	 * @return An array of strings of real umiac group ids, one for each section in the multiple.
	 */
	protected String[] unpackIdSections(String id)
	{
		String[] rv = null;

		// if there is not a '[' and a ']', or they are inverted or enclose an empty string,
		// return just the id
		int leftPos = id.indexOf('[');
		int rightPos = id.indexOf(']');
		if (!((leftPos != -1) && (rightPos != -1)) || (rightPos - leftPos <= 1))
		{
			rv = new String[1];
			rv[0] = id;
		}
		else
		{
			// isolate the root
			String root = id.substring(0, leftPos);

			// isolate the sections
			String sectionString = id.substring(leftPos + 1, rightPos);

			// separate these
			String sections[] = StringUtil.split(sectionString, ",");

			// handle misformed strings
			if ((sections == null) || (sections.length == 0))
			{
				rv = new String[1];
				rv[0] = id;
			}

			else
			{
				// build a return for each section
				rv = new String[sections.length];
				for (int i = 0; i < sections.length; i++)
				{
					rv[i] = root + sections[i];
				}
			}
		}

		return rv;
	}

	/**
	 * <p>MyMap is a Map that in get() recognizes compound keys.</p>
	 */
	public class MyMap extends HashMap
	{
		/**
		 * {@inheritDoc}
		 */
		public Object get(Object key)
		{
			// if we have this key exactly, use it
			Object value = super.get(key);
			if (value != null)
				return value;

			// otherwise break up key as a compound id and find what values we have for these
			// the values are roles, and we prefer "Instructor" to "Student"
			String rv = null;
			String[] ids = unpackId((String) key);
			for (int i = 0; i < ids.length; i++)
			{
				value = super.get(ids[i]);
				if ((value != null) && !("Instructor".equals(rv)))
				{
					rv = (String) value;
				}
			}

			return rv;
		}
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/ctools/src/java/org/sakaiproject/component/legacy/realm/UnivOfMichRealmProvider.java,v 1.7 2004/11/25 02:25:56 janderse.umich.edu Exp $
*
**********************************************************************************/
