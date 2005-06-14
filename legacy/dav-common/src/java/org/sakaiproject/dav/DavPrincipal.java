/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/dav-common/src/java/org/sakaiproject/dav/DavPrincipal.java,v 1.1 2005/05/29 02:18:18 ggolden.umich.edu Exp $
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

package org.sakaiproject.dav;

import java.security.Principal;

/**
 * Implementation of Principal for Dav support in Sakai - holds the user name and password
 *
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class DavPrincipal implements Principal
{
	/** The username of the user represented by this Principal.*/
	protected String m_name = null;

	/** The authentication credentials for the user represented by this Principal. */
	protected String m_password = null;

	/**
	 * Construct with this name and password.
	 * @param name The username of the user represented by this Principal
	 * @param password Credentials used to authenticate this user
	 */
	public DavPrincipal(String name, String password)
	{
		m_name = name;
		m_password = password;
	}

	public String getName()
	{
		return m_name;
	}

	public String getPassword()
	{
		return m_password;
	}

	/**
	 * Does the user represented by this Principal possess the specified role?
	 * @param role Role to be tested.
	 * @return true if the Principal has the role, false if not.
	 * 
	 */
	public boolean hasRole(String role)
	{
		if (role == null)
			return (false);
		return (true);
	}

	public String toString()
	{
		return "DavPrincipal: " + m_name;
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/dav-common/src/java/org/sakaiproject/dav/DavPrincipal.java,v 1.1 2005/05/29 02:18:18 ggolden.umich.edu Exp $
*
**********************************************************************************/
