/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/user/User.java,v 1.7 2004/10/06 05:05:28 ggolden.umich.edu Exp $
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
package org.sakaiproject.service.legacy.user;

import org.sakaiproject.service.legacy.resource.Resource;

// imports

/**
* <p>User is the core interface for the Sakai User object.</p>
* <p>User objects hold everything we know about Sakai users (except for
* their authentication password).</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.7 $
* @see org.sakaiproject.service.legacy.generic.GenericUserDirectoryService
*/
public interface User
	extends Resource, Comparable
{
	/**
	* Access the email address.
	* @return The email address string.
	*/
	public String getEmail();

	/**
	* Access the user's name for display purposes.
	* @return The user's name for display purposes.
	*/
	public String getDisplayName();

	/**
	* Access the user's name for sorting purposes.
	* @return The user's name for sorting purposes.
	*/
	public String getSortName();

	/**
	* Access the user's first name.
	* @return The user's first name.
	*/
	public String getFirstName();

	/**
	* Access the user's last name.
	* @return The user's last name.
	*/
	public String getLastName();

	/**
	* Check if this is the user's password.
	* @param pw The clear text password to check.
	* @return true if the password matches, false if not.
	*/
	public boolean checkPassword(String pw);
	
	/**
	 * Access the user type.
	 * @return The user type.
	 */
	public String getType();

}   // User

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/user/User.java,v 1.7 2004/10/06 05:05:28 ggolden.umich.edu Exp $
*
**********************************************************************************/
