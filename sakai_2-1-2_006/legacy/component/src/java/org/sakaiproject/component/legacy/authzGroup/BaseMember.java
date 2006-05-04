/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/trunk/sakai/legacy/component/src/java/org/sakaiproject/component/legacy/authzGroup/BaseAuthzGroupService.java $
 * $Id: BaseAuthzGroupService.java 2454 2005-10-09 23:49:14Z ggolden@umich.edu $
 ***********************************************************************************
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
package org.sakaiproject.component.legacy.authzGroup;

// imports
import org.sakaiproject.service.legacy.authzGroup.Member;
import org.sakaiproject.service.legacy.authzGroup.Role;

/**
 * <p>
 * BaseMember is an implementation of the AuthzGroup API Member.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public class BaseMember implements Member
{
	/** A fixed class serian number. */
	private static final long serialVersionUID = 1L;

	protected Role role = null;

	protected boolean provided = false;

	protected boolean active = true;

	protected String userId = null;

	public BaseMember(Role role, boolean active, boolean provided, String userId)
	{
		this.role = role;
		this.active = active;
		this.provided = provided;
		this.userId = userId;
	}

	/**
	 * {@inheritDoc}
	 */
	public Role getRole()
	{
		return role;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getUserId()
	{
		return userId;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isProvided()
	{
		return provided;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isActive()
	{
		return active;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setActive(boolean active)
	{
		this.active = active;
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(Object obj)
	{
		if (!(obj instanceof Member)) throw new ClassCastException();

		// if the object are the same, say so
		if (obj == this) return 0;

		// compare by comparing the user id
		int compare = getUserId().compareTo(((Member) obj).getUserId());

		return compare;
	}
}
