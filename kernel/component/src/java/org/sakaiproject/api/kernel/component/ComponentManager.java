/**********************************************************************************
 * $URL$
 * $Id$
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

package org.sakaiproject.api.kernel.component;

import java.util.Properties;
import java.util.Set;

/**
 * <p>
 * ...
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public interface ComponentManager
{
	/**
	 * Find a component that is registered to provide this interface.
	 * 
	 * @param iface
	 *        The interface Class.
	 * @return a component instance, or null if not found.
	 */
	Object get(Class iface);

	/**
	 * Find a component that is registered to provide this interface.
	 * 
	 * @param ifaceName
	 *        The fully qualified interface Class name.
	 * @return a component instance, or null if not found.
	 */
	Object get(String ifaceName);

	/**
	 * Check if this interface Class has a registered component.
	 * 
	 * @param iface
	 *        The interface Class.
	 * @return <strong>true</strong> if this interface Class has a registered component, <strong>false</strong> if not.
	 */
	boolean contains(Class iface);

	/**
	 * Check if this interface Class name has a registered component.
	 * 
	 * @param ifaceName
	 *        The fully qualified interface Class name.
	 * @return <strong>true</strong> if this interface has a registered component, <strong>false</strong> if not.
	 */
	boolean contains(String ifaceName);

	/**
	 * Get all interfaces registered in the component manager.
	 * 
	 * @return A Set (String class name) of all interfaces registered in the component manager.
	 */
	Set getRegisteredInterfaces();

	/**
	 * Load a singleton already created component for this interface class as a singleton.
	 * 
	 * @param iface
	 *        The interface class.
	 * @param component
	 *        The alread created component.
	 */
	void loadComponent(Class iface, Object component);

	/**
	 * Load a singleton already created component for this interface class as a singleton.
	 * 
	 * @param ifaceName
	 *        The fully qualified interface Class name.
	 * @param component
	 *        The alread created component.
	 */
	void loadComponent(String ifaceName, Object component);

	/**
	 * Close the component manager, shutting down any created singletons.
	 */
	void close();

	/**
	 * Access the configuration properties used when configuring components.
	 * 
	 * @return The Properties with the configuration values used when configuring components.
	 */
	Properties getConfig();
}
