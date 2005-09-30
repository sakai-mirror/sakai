/**********************************************************************************
 *
 * $Header: /cvs/sakai2/kernel/thread_local/src/java/org/sakaiproject/api/kernel/thread_local/ThreadLocalManager.java,v 1.4 2005/04/01 02:44:04 ggolden.umich.edu Exp $
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

package org.sakaiproject.api.kernel.thread_local;

/**
 * <p>
 * ThreadLocalManager provides registration and discovery of objects bound to the "current" request processing or thread.
 * </p>
 * <p>
 * Clients who have objects to bind can set/get them at will - the Sakai Framework assures that the entire set of objects bound to current are cleared when the request processing is complete.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.4 $
 */
public interface ThreadLocalManager
{
	/**
	 * Bind this object under this name with the current thread, or remove if the value is null.
	 * 
	 * @param name
	 *        The binding name.
	 * @param value
	 *        The object to bind, or null to un-bind this name.
	 */
	void set(String name, Object value);

	/**
	 * Remove all objects bound to the current thread.
	 */
	void clear();

	/**
	 * Find the named object bound to the current thread.
	 * 
	 * @param name
	 *        The binding name.
	 * @return The object bound by this name, or null if not found.
	 */
	Object get(String name);
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/kernel/thread_local/src/java/org/sakaiproject/api/kernel/thread_local/ThreadLocalManager.java,v 1.4 2005/04/01 02:44:04 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
