/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/dav-server/src/java/org/sakaiproject/dav/DavRealm.java,v 1.1 2005/05/29 02:18:17 ggolden.umich.edu Exp $
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

import org.apache.catalina.LifecycleException;
import org.apache.catalina.realm.RealmBase;

/**
 * Simple implementation of <b>Realm</b> that consults the Sakai
 * user directory service to provide container security equivalent
 * to then application security in CHEF.
 * <p>
 * <strong>IMPLEMENTATION NOTE</strong>: The user is assumed to have all
 * "roles" because servlets and teamlets will enforce roles
 * within CHEF - so in this realm, we simply indicate "true".
 *
 * @author Charles Severance
 * @version $Revision$ $Date$
 */

public final class DavRealm extends RealmBase
{
	/** Descriptive information about this Realm implementation. */
	protected final String info = "org.sakaiproject.realm.DavRealm/1.0";

	/** Descriptive information about this Realm implementation. */
	protected static final String name = "DavRealm";

	/**
	 * Return descriptive information about this Realm implementation and
	 * the corresponding version number, in the format
	 * <code>&lt;description&gt;/&lt;version&gt;</code>.
	 */
	public String getInfo()
	{
		return info;
	}

	/**
	 * Return the Principal associated with the specified username and
	 * credentials, if there is one; otherwise return <code>null</code>.
	 *
	 * @param username Username of the Principal to look up
	 * @param credentials Password or other credentials to use in
	 *  authenticating this username
	 */
	public Principal authenticate(String username, String credentials)
	{
		if (username == null || credentials == null)
			return (null);
		if (username.length() <= 0 || credentials.length() <= 0)
			return (null);

		DavPrincipal prin = new DavPrincipal(username, credentials);

		return prin;
	}

	/**
	 * Return a short name for this Realm implementation.
	 */
	protected String getName()
	{
		return name;
	}

	protected Principal getPrincipal(String username)
	{
		System.out.println("DavRealm.getPrincipal(" + username + ") -- why is this being called?");

		if (username == null)
			return (null);

		return new DavPrincipal(username, " ");
	}

	/**
	 * Return the password associated with the given principal's user name.
	 */
	protected String getPassword(String username)
	{
		System.out.println("DavRealm.getPassword(" + username + ")");
		return (null);
	}

	/**
	 * Prepare for active use of the public methods of this Component.
	 *
	 * @exception IllegalStateException if this component has already been
	 *  started
	 * @exception LifecycleException if this component detects a fatal error
	 *  that prevents it from being started
	 */
	public synchronized void start() throws LifecycleException
	{
		System.out.println("DavRealm.start()");

		// Perform normal superclass initialization
		super.start();
	}

	/**
	 * Gracefully shut down active use of the public methods of this Component.
	 *
	 * @exception IllegalStateException if this component has not been started
	 * @exception LifecycleException if this component detects a fatal error
	 *  that needs to be reported
	 */
	public synchronized void stop() throws LifecycleException
	{
		// Perform normal superclass finalization
		super.stop();

		// No shutdown activities required
	}

	public boolean hasRole(Principal principal, String role)
	{
		return (true);
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/dav-server/src/java/org/sakaiproject/dav/DavRealm.java,v 1.1 2005/05/29 02:18:17 ggolden.umich.edu Exp $
*
**********************************************************************************/
