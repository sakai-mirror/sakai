/**********************************************************************************
 *
 * $Header: /cvs/sakai2/kernel/session/src/java/org/sakaiproject/api/kernel/session/Session.java,v 1.7 2005/05/11 01:27:25 ggolden.umich.edu Exp $
 *
 ***********************************************************************************
 *
 * Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
package org.sakaiproject.api.kernel.session;

// imports
import java.util.Enumeration;

/**
 * <p>
 * Session models an end user's Sakai-wide usage session, modeled on the HttpSession of the Servlet API.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.7 $
 */
public interface Session
{
	/**
	 * Returns the object bound with the specified name in this session, or <code>null</code> if no object is bound under the name.
	 * 
	 * @param name
	 *        a string specifying the name of the object
	 * @return the object with the specified name
	 * @exception IllegalStateException
	 *            if this method is called on an invalidated session
	 */
	Object getAttribute(String name);

	/**
	 * Returns an <code>Enumeration</code> of <code>String</code> objects containing the names of all the objects bound to this session.
	 * 
	 * @return an <code>Enumeration</code> of <code>String</code> objects specifying the names of all the objects bound to this session
	 * @exception IllegalStateException
	 *            if this method is called on an invalidated session
	 */
	Enumeration getAttributeNames();

	/**
	 * Returns the time when this session was created, measured in milliseconds since midnight January 1, 1970 GMT.
	 * 
	 * @return a <code>long</code> specifying when this session was created, expressed in milliseconds since 1/1/1970 GMT
	 * @exception IllegalStateException
	 *            if this method is called on an invalidated session
	 */
	long getCreationTime();

	/**
	 * Returns a string containing the unique identifier assigned to this session.
	 * 
	 * @return a string specifying the identifier assigned to this session
	 * @exception IllegalStateException
	 *            if this method is called on an invalidated session
	 */
	String getId();

	/**
	 * Returns the last time the client sent a request associated with this session, as the number of milliseconds since midnight January 1, 1970 GMT.
	 * <p>
	 * Actions that your application takes, such as getting or setting a value associated with the session, do not affect the access time.
	 * 
	 * @return a <code>long</code> representing the last time the client sent a request associated with this session, expressed in milliseconds since 1/1/1970 GMT
	 * @exception IllegalStateException
	 *            if this method is called on an invalidated session
	 */
	long getLastAccessedTime();

	/**
	 * Returns the maximum time interval, in seconds, that Sakai will keep this session open between client accesses. After this interval, Sakai will invalidate the session. The maximum time interval can be set with the <code>setMaxInactiveInterval</code>
	 * method. A negative time indicates the session should never timeout.
	 * 
	 * @return an integer specifying the number of seconds this session remains open between client requests
	 * @see #setMaxInactiveInterval
	 */
	int getMaxInactiveInterval();
	
	/**
	 * Find or create a tool session for this tool placement id
	 * @param placementId The id used to identify the session.
	 * @return The ToolSession to use for this tool placement id.
	 */
	ToolSession getToolSession(String placementId);

	/**
	 * Find or create a context session for this context id
	 * @param contextId The id used to identify the session.
	 * @return The ContextSession to use for this context id.
	 */
	ContextSession getContextSession(String contextId);

	/**
	 * Return the enterprise id of the user associated with this session.
	 * 
	 * @return The enterprise id of the user associated with this session.
	 */
	String getUserEid();

	/**
	 * Return the authenticated user id associated with this session.
	 * 
	 * @return The authenticated user id associated with this session.
	 */
	String getUserId();

	/**
	 * Invalidates this session then unbinds any objects bound to it.
	 * 
	 * @exception IllegalStateException
	 *            if this method is called on an already invalidated session
	 */
	void invalidate();

	/**
	 * Removes the object bound with the specified name from this session. If the session does not have an object bound with the specified name, this method does nothing.
	 * <p>
	 * After this method executes, and if the object implements <code>SessionBindingListener</code>, Sakai calls <code>SessionBindingListener.valueUnbound</code>.
	 * 
	 * @param name
	 *        the name of the object to remove from this session
	 * @exception IllegalStateException
	 *            if this method is called on an invalidated session
	 */
	void removeAttribute(String name);

	/**
	 * Mark the session as still active, delaying timeout by another period.
	 */
	void setActive();

	/**
	 * Binds an object to this session, using the name specified. If an object of the same name is already bound to the session, the object is replaced.
	 * <p>
	 * After this method executes, and if the new object implements <code>SessionBindingListener</code>, Sakai calls <code>SessionBindingListener.valueBound</code>.
	 * <p>
	 * If an object was already bound to this session of this name that implements <code>SessionBindingListener</code>, its <code>SessionBindingListener.valueUnbound</code> method is called.
	 * <p>
	 * If the value passed in is null, this has the same effect as calling <code>removeAttribute()<code>.
	 *
	 * @param name			the name to which the object is bound;
	 *					cannot be null
	 *
	 * @param value			the object to be bound
	 *
	 * @exception IllegalStateException	if this method is called on an
	 *					invalidated session
	 */
	void setAttribute(String name, Object value);

	/**
	 * Specifies the time, in seconds, between client requests before the Sakai will invalidate this session. A negative time indicates the session should never timeout.
	 * 
	 * @param interval
	 *        An integer specifying the number of seconds
	 */
	void setMaxInactiveInterval(int interval);

	/**
	 * Set the enterprise id of the user associated with this session.
	 * 
	 * @return The enterprise id of the user associated with this session.
	 */
	void setUserEid(String eid);

	/**
	 * Set the user id associated with this session.
	 * 
	 * @param uid
	 *        The user id associated with this session.
	 */
	void setUserId(String uid);
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/kernel/session/src/java/org/sakaiproject/api/kernel/session/Session.java,v 1.7 2005/05/11 01:27:25 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
