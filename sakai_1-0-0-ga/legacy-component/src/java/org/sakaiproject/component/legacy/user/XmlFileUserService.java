/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/user/XmlFileUserService.java,v 1.10 2004/10/14 14:18:03 ggolden.umich.edu Exp $
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
package org.sakaiproject.component.legacy.user;

// import
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.sakaiproject.service.legacy.user.UserEdit;
import org.sakaiproject.util.storage.BaseXmlFileStorage;
import org.sakaiproject.util.storage.StorageUser;

/**
* <p>XmlFileUserService is an extension of the BaseUserService  with a in-memory xml file backed up
* storage.  The full set of users are read in at startup, kept in memory, and
* written out at shutdown.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.10 $
* @see org.chefproject.service.component.BaseUserService
*/
public class XmlFileUserService extends BaseUserDirectoryService
{
	/** A full path and file name to the storage file. */
	protected String m_storagePath = "db/user.xml";

	/*******************************************************************************
	* Constructors, Dependencies and their setter methods
	*******************************************************************************/

	/**
	 * Configuration: set the storage path
	 * @param path The storage path.
	 */
	public void setStoragePath(String path)
	{
		m_storagePath = path;
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
			super.init();

			m_logger.info(this +".init(): storage path: " + m_storagePath);
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}

	} // init

	/*******************************************************************************
	* BaseUserService extensions
	*******************************************************************************/

	/**
	* Construct a Storage object.
	* @return The new storage object.
	*/
	protected Storage newStorage()
	{
		return new XmlFileStorage(this);

	} // newStorage

	/*******************************************************************************
	* Storage implementation
	*******************************************************************************/

	/**
	* Covers for the BaseXmlFileStorage, providing User and UserEdit parameters
	*/
	protected class XmlFileStorage extends BaseXmlFileStorage implements Storage
	{
		/**
		* Construct.
		* @param user The StorageUser class to call back for creation of Resource and Edit objects.
		*/
		public XmlFileStorage(StorageUser user)
		{
			super(m_storagePath, "users", null, "user", user);

		} // XmlFileStorage

		public boolean check(String id)
		{
			return super.checkResource(null, id);
		}

		public UserEdit get(String id)
		{
			return (UserEdit) super.getResource(null, id);
		}

		public List getAll()
		{
			return super.getAllResources(null);
		}

		public List getAll(int first, int last)
		{
			return super.getAllResources(null, first, last);
		}

		public int count()
		{
			return super.countAllResources(null);
		}

		public UserEdit put(String id)
		{
			return (UserEdit) super.putResource(null, id, null);
		}

		public UserEdit edit(String id)
		{
			return (UserEdit) super.editResource(null, id);
		}

		public void commit(UserEdit user)
		{
			super.commitResource(null, user);
		}

		public void cancel(UserEdit user)
		{
			super.cancelResource(null, user);
		}

		public void remove(UserEdit user)
		{
			super.removeResource(null, user);
		}

		/**
		 * {@inheritdoc}
		 */
		public List search(String criteria, int first, int last)
		{
			List all = super.getAllResources(null);

			List rv = new Vector();
			for (Iterator i = all.iterator(); i.hasNext();)
			{
				BaseUserEdit u = (BaseUserEdit) i.next();
				if (u.selectedBy(criteria))
				{
					rv.add(u);
				}
			}
			
			Collections.sort(rv);

			// subset by position
			if (first < 1) first = 1;
			if (last >= rv.size()) last = rv.size();
			
			rv = rv.subList(first-1, last);

			return rv;
		}

		/**
		 * {@inheritdoc}
		 */
		public int countSearch(String criteria)
		{
			List all = super.getAllResources(null);

			Vector rv = new Vector();
			for (Iterator i = all.iterator(); i.hasNext();)
			{
				BaseUserEdit u = (BaseUserEdit) i.next();
				if (u.selectedBy(criteria))
				{
					rv.add(u);
				}
			}
			
			return rv.size();
		}

		/**
		 * {@inheritdoc}
		 */
		public UserEdit findUserByEmail(String email)
		{
			// check internal users
			List users = getUsers();
			for (Iterator iUsers = users.iterator(); iUsers.hasNext();)
			{
				UserEdit user = (UserEdit) iUsers.next();
				if (user.getEmail().equalsIgnoreCase(email))
				{
					return user;
				}
			}
			
			return null;
		}

	} // XmlFileStorage

} // XmlFileUserService

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/user/XmlFileUserService.java,v 1.10 2004/10/14 14:18:03 ggolden.umich.edu Exp $
*
**********************************************************************************/
