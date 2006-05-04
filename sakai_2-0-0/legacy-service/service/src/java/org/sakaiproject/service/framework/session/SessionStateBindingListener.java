/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/framework/session/SessionStateBindingListener.java,v 1.1 2005/05/12 15:45:36 ggolden.umich.edu Exp $
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

package org.sakaiproject.service.framework.session;

/**
 * <p>SessionStateBindingListener is an interface for objects that wish to be
 * notified when they are bound to and unbound from a SessionState.</p>
 * <p>This is loosely modeled on the HttpSessionBindingListener.</p>
 * @version $Revision: 1.1 $
 * @author University of Michigan, Sakai Software Development Team
 */
public interface SessionStateBindingListener
{
	/**
	 * Accept notification that this object has been bound as a SessionState attribute.
	 * @param sessionStateKey The id of the session state which holds the attribute.
	 * @param attributeName The id of the attribute to which this object is now the value.
	 */
	void valueBound(String sessionStateKey, String attributeName);

	/**
	 * Accept notification that this object has been removed from a SessionState attribute.
	 * @param sessionStateKey The id of the session state which held the attribute.
	 * @param attributeName The id of the attribute to which this object was the value.
	 */
	void valueUnbound(String sessionStateKey, String attributeName);
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/framework/session/SessionStateBindingListener.java,v 1.1 2005/05/12 15:45:36 ggolden.umich.edu Exp $
*
**********************************************************************************/
