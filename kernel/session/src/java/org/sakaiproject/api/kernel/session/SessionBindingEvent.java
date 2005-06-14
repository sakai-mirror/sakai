/**********************************************************************************
 *
 * $Header: /cvs/sakai2/kernel/session/src/java/org/sakaiproject/api/kernel/session/SessionBindingEvent.java,v 1.2 2005/03/07 03:15:29 ggolden.umich.edu Exp $
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

/**
 * <p>
 * Events of this type are either sent to an object that implements {@link SessionBindingListener}when it is bound or unbound from a session.
 * </p>
 * <p>
 * (Based on HttpSessionBindingEvent from the Servlet API).
 * </p>
 * 
 * @version $Revision: 1.2 $
 * @author University of Michigan, Sakai Software Development Team
 */
public interface SessionBindingEvent
{
	/**
	 * Returns the name with which the attribute is bound to or unbound from the session.
	 * 
	 * @return a string specifying the name with which the object is bound to or unbound from the session
	 */
	String getName();

	/**
	 * Return the session that bound or unbound the attribute value.
	 * 
	 * @return The Session object that bound or unbound the attribute value.
	 */
	Session getSession();

	/**
	 * Returns the value of the attribute that has been added, removed or replaced. If the attribute was added (or bound), this is the value of the attribute. If the attribute was removed (or unbound), this is the value of the removed attribute. If the
	 * attribute was replaced, this is the old value of the attribute.
	 * 
	 * @return The value of the attribute being bound or unbound.
	 */
	Object getValue();
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/kernel/session/src/java/org/sakaiproject/api/kernel/session/SessionBindingEvent.java,v 1.2 2005/03/07 03:15:29 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
