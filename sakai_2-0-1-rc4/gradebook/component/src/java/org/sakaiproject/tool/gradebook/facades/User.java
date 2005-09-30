/**********************************************************************************
*
* $Header: /cvs/sakai2/gradebook/component/src/java/org/sakaiproject/tool/gradebook/facades/User.java,v 1.2 2005/05/26 18:04:54 josh.media.berkeley.edu Exp $
*
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of California, The MIT Corporation
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

package org.sakaiproject.tool.gradebook.facades;

/**
 * A User is a gradebook user.  This is an immutable convenience object returned by
 * the CourseManagement facade, usually obtained from an external service. It is not normally
 * managed or made persistent by the gradebook application.
 *
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 */
public interface User {
	/**
	 * @return Returns the userUid, the unique ID returned by the authentication facade and
	 * used to identify users in the course management and authorization facades
	 */
	public String getUserUid();

	/**
	 * @return Returns the sortName, displayed when users are listed in order (for example,
	 * "Paine, Thomas" or "Wong Kar-Wai")
	 */
	public String getSortName();

    /**
	 * @return Returns the displayUid, AKA "campus ID", a human-meaningful UID for the user (for
	 * example, a student ID number or an institutional email address)
	 */
	public String getDisplayUid();

	/**
	 * @return Returns the displayName, displayed when only this user is being referred to
	 * (for example, "Thomas Paine" or "Wong Kar-Wai")
	 */
	public String getDisplayName();
}
/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/gradebook/component/src/java/org/sakaiproject/tool/gradebook/facades/User.java,v 1.2 2005/05/26 18:04:54 josh.media.berkeley.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
