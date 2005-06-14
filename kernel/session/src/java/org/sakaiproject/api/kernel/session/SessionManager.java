/**********************************************************************************
 *
 * $Header: /cvs/sakai2/kernel/session/src/java/org/sakaiproject/api/kernel/session/SessionManager.java,v 1.8 2005/05/30 03:39:09 ggolden.umich.edu Exp $
 *
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

package org.sakaiproject.api.kernel.session;

/**
 * <p>
 * SessionManager keeps track of Sakai-wide and Tool placement specific user sessions, modeled on the HttpSession of the Servlet API.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public interface SessionManager
{
	/**
	 * Access the known session with this id.
	 * 
	 * @return The Session object that has this id, or null if the id is not known.
	 */
	Session getSession(String sessionId);

	/**
	 * Start a new session.
	 * 
	 * @return The new UsageSession.
	 */
	Session startSession();

	/**
	 * Start a new session, using this session id.
	 * 
	 * @param id
	 *        The session Id to use.
	 * @return The new UsageSession.
	 */
	Session startSession(String id);

	/**
	 * Access the session associated with the current request or processing thread.
	 * 
	 * @return The session associatd with the current request or processing thread.
	 */
	Session getCurrentSession();

	/**
	 * Access the tool session associated with the current request or processing thread.
	 * 
	 * @return The tool session associatd with the current request or processing thread.
	 */
	ToolSession getCurrentToolSession();

	/**
	 * Set this session as the current one for this request processing or thread.
	 * 
	 * @param s
	 *        The session to set as the current session.
	 */
	void setCurrentSession(Session s);

	/**
	 * Set this session as the current tool session for this request processing or thread.
	 * 
	 * @param s
	 *        The session to set as the current tool session.
	 */
	void setCurrentToolSession(ToolSession s);
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/kernel/session/src/java/org/sakaiproject/api/kernel/session/SessionManager.java,v 1.8 2005/05/30 03:39:09 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
