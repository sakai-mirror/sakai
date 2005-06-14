/**********************************************************************************
 *
 * $Header: /cvs/sakai2/kernel/session/src/java/org/sakaiproject/api/kernel/session/SessionBindingListener.java,v 1.2 2005/03/07 03:15:29 ggolden.umich.edu Exp $
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

package org.sakaiproject.api.kernel.session;

import java.util.EventListener;

/**
 * <p>
 * Causes an object to be notified when it is bound to or unbound from a session. The object is notified by an {@link SessionBindingEvent}object. This may be as a result of a programmer explicitly unbinding an attribute from a session, due to a session
 * being invalidated, or due to a session timing out.
 * </p>
 * <p>
 * (Based on HttpSessionBindingListener from the Servlet API).
 * </p>
 * 
 * @version $Revision: 1.2 $
 * @author University of Michigan, Sakai Software Development Team
 */
public interface SessionBindingListener extends EventListener
{
	/**
	 * Notifies the object that it is being bound to a session.
	 * 
	 * @param event
	 *        the event that identifies the session
	 */
	void valueBound(SessionBindingEvent event);

	/**
	 * Notifies the object that it is being unbound from a session.
	 * 
	 * @param event
	 *        the event that identifies the session
	 */
	void valueUnbound(SessionBindingEvent event);
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/kernel/session/src/java/org/sakaiproject/api/kernel/session/SessionBindingListener.java,v 1.2 2005/03/07 03:15:29 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
