/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
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

// package
package org.sakaiproject.component.imsent.user;

// imports
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.service.framework.sql.SqlReader;
import org.sakaiproject.service.framework.sql.SqlService;
import org.sakaiproject.service.legacy.user.UserDirectoryProvider;
import org.sakaiproject.service.legacy.user.UserEdit;

/**
 * <p>
 * IMSEntUserDirectoryProvider is a sample UserDirectoryProvider.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class IMSEntUserDirectoryProvider implements UserDirectoryProvider
{
	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(IMSEntUserDirectoryProvider.class);

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Dependencies and their setter methods
	 *********************************************************************************************************************************************************************************************************************************************************/
	/** Dependency: SqlService */
	protected SqlService m_sqlService = null;

	/**
	 * Dependency: SqlService.
	 * @param service The SqlService.
	 */
	public void setSqlService(SqlService service)
	{
		System.out.println("Setting Sql Service");
		m_sqlService = service;
	}

	/** Configuration: to run the ddl on init or not. */
	// TODO: Set back to false
	protected boolean m_autoDdl = true;

	/**
	 * Configuration: to run the ddl on init or not.
	 * 
	 * @param value
	 *        the auto ddl value.
	 */
	public void setAutoDdl(String value)
	{
		m_autoDdl = new Boolean(value).booleanValue();
	}
	
	/**********************************************************************************************************************************************************************************************************************************************************
	 * Init and Destroy
	 *********************************************************************************************************************************************************************************************************************************************************/
	
	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		try
		{
			M_log.info("init()");
		}
		catch (Throwable t)
		{
			System.out.println(this+".init() - failed attempting to log " + t);
			M_log.warn(".init(): " + t);
		}
		
		try {
			// if we are auto-creating our schema, check and create
			if (	m_autoDdl && m_sqlService != null)
			{
				m_sqlService.ddl(this.getClass().getClassLoader(), "imsent_provider");
				System.out.println("Back from autoddl");
			}
			
			// Check to see if we are ready to run...
			if ( ! isReady () ) {
				M_log.warn(".init(): Not properly initialized.");
			}
			
			// Run our local unit tests
			IMSEntProviderUnitTest.localUnitTests(this, null, null);
		}
		catch (Throwable t)
		{				
			M_log.warn(".init(): ", t);
			m_isReady = false;
		}
		// Check to see if we are ready to run...
		if ( ! isReady () ) {
			M_log.warn(".init(): Not properly initialized.");
		}
		
	} // init
	
	/**
	 * Returns to uninitialized state. You can use this method to release resources thet your Service allocated when Turbine shuts down.
	 */
	public void destroy()
	{
		M_log.info("destroy()");
	} // destroy
	
	/**
	 * Determine if we are in a ready-to-go-state
	 */	
	private boolean m_isReady = true; 
	private boolean m_firstCheck = true;
	
	private boolean isReady()
	{
		// Only check things once
		if ( ! m_firstCheck ) return m_isReady;
		m_firstCheck = false;
		
		boolean retval = true;
		
		if ( m_sqlService == null ) {
			M_log.warn("sqlService injection failed");
			retval = false;
		}
		
		// Check all other injections here
		
		// Return the value and set
		m_isReady = retval;
		return retval;
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * UserDirectoryProvider implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	public class SakaiIMSUser {
		// From User
		public String eMail = null;
		public String displayName  = null;
		public String sortName = null;
		public String firstName = null;
		public String lastName  = null;
	
		// From Resource
		// public ResourceProperties getProperties;
		public String id  = null;
		
		// For use locally
		public String password  = null;
		
		// For debugging
		public String toString()
		{
			String rv = "SakaiIMSUser Email="+eMail+" DisplayName="+displayName+" SortName="+sortName+
			            " FirstName="+firstName+" LastName="+lastName+" Id="+id+" Password="+password;
			return rv;
		}
	}
	
	public SakaiIMSUser retrieveUser(final String userId, boolean isEmail)
	{
		String statement;
		
		if (userId == null) return null;

		if ( isEmail )
		{
			//                   1      2 3     4        5      6     7  
			statement = "select USERID,FN,SORT,PASSWORD,FAMILY,GIVEN,EMAIL from IMSENT_PERSON where EMAIL = ?";
		}
		else
		{
			statement = "select USERID,FN,SORT,PASSWORD,FAMILY,GIVEN,EMAIL from IMSENT_PERSON where USERID = ?";
		}
			
		Object fields[] = new Object[1];
		fields[0] = userId;

		System.out.println("SQL:"+statement);
		List rv = m_sqlService.dbRead(statement, fields,
			new SqlReader()
			{
				public Object readSqlResultRecord(ResultSet result)
				{
					try
					{
						SakaiIMSUser rv = new SakaiIMSUser();
						rv.id = result.getString(1);
						rv.displayName = result.getString(2);
						rv.sortName = result.getString(3);
						if ( rv.sortName == null ) rv.sortName = rv.displayName;
						rv.password = result.getString(4);
						rv.lastName = result.getString(5);
						rv.firstName = result.getString(6);
						rv.eMail = result.getString(7);
						System.out.println("Inside reader "+rv);
						return rv;
					}
					catch (SQLException e)
					{
						M_log.warn(this + ".authenticateUser: " + userId + " : " + e);
						return null;
					}
				}
			});
		
		if ((rv != null) && (rv.size() > 0))
		{
			System.out.println("Returning ");
			System.out.println(" "+(SakaiIMSUser) rv.get(0));
			return (SakaiIMSUser) rv.get(0);
		}
		return null;
	}

	/**
	 * Construct.
	 */
	public IMSEntUserDirectoryProvider()
	{
	
	} // SampleUserDirectoryProvider

	/**
	 * See if a user by this id exists.
	 * 
	 * @param userId
	 *        The user id string.
	 * @return true if a user by this id exists, false if not.
	 */
	public boolean userExists(String userId)
	{
		if ( ! isReady() ) return false;
		if (userId == null) return false;
		System.out.println("userExists("+userId+")");
		SakaiIMSUser rv = retrieveUser(userId, false);
		return (rv != null);
	} // userExists

	/**
	 * Copy the information from our internal structure into the Sakai User
	 * structure.
	 * @param edit
	 * @param imsUser
	 */
	private void copyInfo(UserEdit edit, SakaiIMSUser imsUser)
	{
		edit.setId(imsUser.id);
		edit.setFirstName(imsUser.firstName);
		edit.setLastName(imsUser.lastName);
		edit.setEmail(imsUser.eMail);
		edit.setPassword(imsUser.password);
		// Sakai currently creates sortname from first and last name
		edit.setType("imsent");
	}
	
	/**
	 * Access a user object. Update the object with the information found.
	 * 
	 * @param edit
	 *        The user object (id is set) to fill in.
	 * @return true if the user object was found and information updated, false if not.
	 */
	public boolean getUser(UserEdit edit)
	{
		if ( ! isReady() ) return false;
		if (edit == null) return false;
		String userId = edit.getId();
	
		System.out.println("getUser("+userId+")");
		SakaiIMSUser rv = retrieveUser(userId, false);
		if ( rv == null ) return false;
		copyInfo(edit,rv);
		return true;
	} // getUser

	/**
	 * Access a collection of UserEdit objects; if the user is found, update the information, otherwise remove the UserEdit object from the collection.
	 * @param users The UserEdit objects (with id set) to fill in or remove.
	 */
	public void getUsers(Collection users)
	{
		for (Iterator i = users.iterator(); i.hasNext();)
		{
			UserEdit user = (UserEdit) i.next();
			if (!getUser(user))
			{
				i.remove();
			}
		}
	}

	/**
	 * Find a user object who has this email address. Update the object with the information found.
	 * 
	 * @param email
	 *        The email address string.
	 * @return true if the user object was found and information updated, false if not.
	 */
	public boolean findUserByEmail(UserEdit edit, String email)
	{
		if ( ! isReady() ) return false;
		if ((edit == null) || (email == null)) return false;
		
		System.out.println("findUserByEmail("+email+")");
		SakaiIMSUser rv = retrieveUser(email, true);
		if ( rv == null ) return false;
		copyInfo(edit,rv);
		return true;

	} // findUserByEmail

	/**
	 * Authenticate a user / password. If the user edit exists it may be modified, and will be stored if...
	 * 
	 * @param id
	 *        The user id.
	 * @param edit
	 *        The UserEdit matching the id to be authenticated (and updated) if we have one.
	 * @param password
	 *        The password.
	 * @return true if authenticated, false if not.
	 */
	public boolean authenticateUser(final String userId, UserEdit edit, String password)
	{
		if ( ! isReady() ) return false;
		if ((userId == null) || (password == null)) return false;
		System.out.println("authenticateUser("+userId+")");
		SakaiIMSUser rv = retrieveUser(userId, false);
		if ( rv == null ) return false;
		return (password.compareTo(rv.password) == 0) ;
	} // authenticateUser

	/**
	 * Will this provider update user records on successfull authentication? If so, the UserDirectoryService will cause these updates to be stored.
	 * 
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

} // SampleUserDirectoryProvider



