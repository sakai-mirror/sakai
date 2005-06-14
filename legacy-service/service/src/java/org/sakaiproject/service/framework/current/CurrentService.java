/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/framework/current/CurrentService.java,v 1.1 2005/05/12 15:45:38 ggolden.umich.edu Exp $
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

package org.sakaiproject.service.framework.current;

/**
 * <p>CurrentService provides registration and discovery of objects bound to the "current" something.</p>
 * <p>The "something" that things can be bound to includes the current thread.</p>
 * <p>Clients who have objects to bind must register them and then make sure to clear them when the current
 * something expires.</p>
 * 
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision$
 */
public interface CurrentService
{
	/** Set for DEBUG message collection and reporting. */
	public static final boolean DEBUG = false;

	/**
	 * Bind this object under this name with the current thread.
	 * @param name The binding name.
	 * @param value The object to bind.
	 */
	void setInThread(String name, Object value);

	/**
	 * Remove all objects bound to the current thread, and finalize thread specific book-keeping (i.e. DEBUG processing)
	 */
	void clearInThread();

	/**
	 * Find the named object bound to the current thread.
	 * @param name The binding name.
	 * @return The object bound by this name, or null if not found.
	 */
	Object getInThread(String name);

	/**
	 * Setup any thread specific book-keeping - if DEBUG, setup timing and DEBUG message collecion
	 * @param description Short thread description for debugging.
	 */
	void startThread(String description);
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/framework/current/CurrentService.java,v 1.1 2005/05/12 15:45:38 ggolden.umich.edu Exp $
*
**********************************************************************************/
