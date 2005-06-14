/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/user/UserEdit.java,v 1.1 2005/05/12 15:45:38 ggolden.umich.edu Exp $
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

import org.sakaiproject.service.legacy.resource.Edit;

// imports

/**
* <p>UserEdit is the core interface for the editable CHEF User object.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.1 $
*/
public interface UserEdit
	extends User, Edit
{
	/**
	* Set the user's id.
	* Note: this is a special purpose routine that is used only to establish the id field,
	* when the id is null, and cannot be used to change a user's id.
	* @param name The user id.
	*/
	public void setId(String id);

	/**
	* Set the email address.
	* @param email The email address string.
	*/
	public void setEmail(String email);

	/**
	* Set the user's first name.
	* @param name The user's first name.
	*/
	public void setFirstName(String name);

	/**
	* Set the user's last name.
	* @param name The user's last name.
	*/
	public void setLastName(String name);

	/**
	* Set the user's password
	* @param pw The user's new password.
	*/
	public void setPassword(String pw);

	/**
	 * Set the user type.
	 * @param type The user type.
	 */
	public void setType(String type);

}   // UserEdit

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/user/UserEdit.java,v 1.1 2005/05/12 15:45:38 ggolden.umich.edu Exp $
*
**********************************************************************************/
