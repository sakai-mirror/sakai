/**********************************************************************************
*
* $Header: /cvs/sakai/provider/src/java/org/sakaiproject/component/legacy/user/SampleUserDirectoryProvider.java,v 1.8 2004/10/06 05:05:27 ggolden.umich.edu Exp $
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

// imports
import java.util.Hashtable;

import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.legacy.user.UserDirectoryProvider;
import org.sakaiproject.service.legacy.user.UserEdit;

/**
* <p>SampleUserDirectoryProvider is a samaple UserDirectoryProvider.</p>
* <p>This one defines "user" and "sample with the password "chef".</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.8 $
*/
public class SampleUserDirectoryProvider
	implements UserDirectoryProvider
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
	* UserDirectoryProvider implementation
	*******************************************************************************/

	/** A collection of user ids/names. */
	protected Hashtable m_info = null;

	protected class Info
	{
		public String id;
		public String firstName;
		public String lastName;
		public String email;
		public Info(String id, String firstName, String lastName, String email)
		{
			this.id = id;
			this.firstName = firstName;
			this.lastName = lastName;
			this.email = email;
		}

	}	// class info

	/**
	* Construct.
	*/
	public SampleUserDirectoryProvider()
	{
		// fill a set of users
		m_info = new Hashtable();
		m_info.put("user", new Info("user", "A.","User", "user@sakaiproject.org"));
		m_info.put("sample", new Info("sample", "A.","Sample", "sample@sakaiproject.org"));

	}	// SampleUserDirectoryProvider

	/**
	* See if a user by this id exists.
	* @param userId The user id string.
	* @return true if a user by this id exists, false if not.
	*/
	public boolean userExists(String userId)
	{
		if (userId == null) return false;
		if (userId.startsWith("test")) return true;
		if (m_info.containsKey(userId)) return true;

		return false;

	}	// userExists

	/**
	* Access a user object.  Update the object with the information found.
	* @param edit The user object (id is set) to fill in.
	* @return true if the user object was found and information updated, false if not.
	*/
	public boolean getUser(UserEdit edit)
	{
		if (edit == null) return false;
		if (!userExists(edit.getId())) return false;

		Info info = (Info) m_info.get(edit.getId());
		if (info == null)
		{
			edit.setFirstName(edit.getId());
			edit.setLastName(edit.getId());
			edit.setEmail(edit.getId());
			edit.setPassword(edit.getId());
		}
		else
		{
			edit.setFirstName(info.firstName);
			edit.setLastName(info.lastName);
			edit.setEmail(info.email);
			edit.setPassword("sakai");
		}
		edit.setType("sample");

		return true;

	}	// getUser

	/**
	* Find a user object who has this email address. Update the object with the information found.
	* @param email The email address string.
	* @return true if the user object was found and information updated, false if not.
	*/
	public boolean findUserByEmail(UserEdit edit, String email)
	{
		if ((edit == null) || (email == null)) return false;

		// assume a "@sakaiproject.org"
		int pos = email.indexOf("@sakaiproject.org");
		if (pos != -1)
		{
			String id = email.substring(0, pos);
			edit.setId(id);
			return getUser(edit);
		}

		return false;

	}	// findUserByEmail

	/**
	 * Authenticate a user / password.
	 * If the user edit exists it may be modified, and will be stored if...
	 * @param id The user id.
	 * @param edit The UserEdit matching the id to be authenticated (and updated) if we have one.
	 * @param password The password.
	 * @return true if authenticated, false if not.
	 */
	public boolean authenticateUser(String userId, UserEdit edit, String password)
	{
		if ((userId == null) || (password == null)) return false;

		if (userId.startsWith("test")) return userId.equals(password);
		if (userExists(userId) && password.equals("sakai")) return true;

		return false;

	}	// authenticateUser

	/**
	 * Will this provider update user records on successfull authentication?
	 * If so, the UserDirectoryService will cause these updates to be stored.
	 * @return true if the user record may be updated after successfull authentication, false if not.
	 */
	public boolean updateUserAfterAuthentication()
	{
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void destroyAuthentication()
	{
	}

}	// SampleUserDirectoryProvider

/**********************************************************************************
*
* $Header: /cvs/sakai/provider/src/java/org/sakaiproject/component/legacy/user/SampleUserDirectoryProvider.java,v 1.8 2004/10/06 05:05:27 ggolden.umich.edu Exp $
*
**********************************************************************************/
