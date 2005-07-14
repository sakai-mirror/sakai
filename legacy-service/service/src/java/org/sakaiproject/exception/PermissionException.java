/**********************************************************************************
* $URL$
* $Id$
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
package org.sakaiproject.exception;

/**
* <p>PermissionException indicates an invalid unlock attempt by a user for a lock and a resource.</p>
*
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public class PermissionException extends Exception
{
	/** The id of the user. */
	private String m_user = null;

	/**
	* Access the id of the user.
	* @return The id of the user.
	*/
	public String getUser()
	{
		return m_user;
	}

	/** The lock name. */
	private String m_lock = null;

	/**
	* Access the lock name.
	* @return The lock name.
	*/
	public String getLock()
	{
		return m_lock;
	}

	/** The resource id. */
	private String m_resource = null;

	/**
	* Access the resource id.
	* @return The resource id.
	*/
	public String getResource()
	{
		return m_resource;
	}

	/**
	* Construct.
	* @param user The id of the user.
	* @param lock The lock name.
	* @param resource The resource id.
	*/
	public PermissionException(String user, String lock, String resource)
	{
		m_user = user;
		m_lock = lock;
		m_resource = resource;
	}

	public String toString()
	{
		return super.toString() + " user=" + m_user + " lock=" + m_lock + " resource=" + m_resource;
	}

} // PermissionException



