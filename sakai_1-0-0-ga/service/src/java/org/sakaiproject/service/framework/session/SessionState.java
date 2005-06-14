/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/framework/session/SessionState.java,v 1.3 2004/09/30 20:20:57 ggolden.umich.edu Exp $
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

// package
package org.sakaiproject.service.framework.session;

import java.util.List;

/**
 * <p>SessionState is a collection of named attributes associated with the current session.</p>
 * @version $Revision: 1.3 $
 * @author University of Michigan, Sakai Software Development Team
 */
public interface SessionState
{
	/**
	 * Access the named attribute.
	 * @param name The attribute name.
	 * @return The named attribute value.
	 */
	Object getAttribute(String name);

	/**
	 * Set the named attribute value to the provided object.
	 * @param name The attribute name.
	 * @param value The value of the attribute (any object type).
	 */
	void setAttribute(String name, Object value);

	/**
	 * Remove the named attribute, if it exists.
	 * @param name The attribute name.
	 */
	void removeAttribute(String name);

	/**
	 * Remove all attributes.
	 */
	void clear();

	/**
	 * Access a List of all names of attributes stored in the SessionState.
	 * @return A List of all names of attribute stored in the SessionState.
	 */
	List getAttributeNames();
}

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/framework/session/SessionState.java,v 1.3 2004/09/30 20:20:57 ggolden.umich.edu Exp $
*
**********************************************************************************/
