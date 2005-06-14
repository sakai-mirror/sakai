/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/framework/component/ComponentManager.java,v 1.1 2005/05/12 15:45:33 ggolden.umich.edu Exp $
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

package org.sakaiproject.service.framework.component;

import java.io.InputStream;
import java.util.Set;

/**
 * <p>ComponentManager manages service implementation components.</p>
 * <p>A component is a java class which implements some published Service API.</p>
 * <p>Components can declare javabean setter methods or constructors to accept configuration parameters
 * and Service API dependency components which are injected by this component framework.  An optional "init" and "destroy"
 * method can also be called by the framework.  These details are convered in the component's definition in the
 * XML definition file.  This file includes <strong>bean</strong> definitions that follow the format defined by
 * the <a href="http://www.springframework.org/">Spring Framework</a> in the
 * <a href="http://www.springframework.org/docs/reference/springbeansdtd.html">spring-beans DTD</a>.</p>
 * <p>To register components, call {@link #loadXmlFile <code>loadXmlFile</code>}
 * or {@link #loadXmlStream <code>loadXmlStream</code>} with a file or stream of XML
 * describing the components to register. These can be called more than once, perhaps at different times,
 * or by different webapps, to register multiple sets of components.  All registered components are all available
 * to all clients as a single set.</p>
 * <p>Components are registered under the fully qualified name of the interface the component implements.</p>
 * <p>To find a component registered to handle a given interface, use {@link #get <code>get</code>},
 * either with the fully qualified interface name as a string, or the interface Class object.
 * The component manager will find a component if it has been created in scope,
 * or create, wire / configure, and remember the component as needed.</p>
 * <p>Information about registered components is available from the {@link #contains <code>contains</code>},
 * {@link #getLifestyle <code>getLifestyle</code>},
 * and {@link #getRegisteredInterfaces <code>getRegisteredInterfaces</code>} methods.</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public interface ComponentManager
{
	/** Lifestyle code: unknown lifecycle. */
	static final int LIFESTYLE_UNKNOWN = 0;

	/** Lifestyle code: a single instance of the component will be served to all clients. */
	static final int LIFESTYLE_SINGLE = 1;

	/** Lifestyle code: a unique instance of the component will be created for each client get() request. */
	static final int LIFESTYLE_MANY = 2;

	/**
	 * Find a component that is registered to provide this interface.
	 * @param iface The interface Class.
	 * @return a component instance, or null if not found.
	 */
	Object get(Class iface);

	/**
	 * Find a component that is registered to provide this interface.
	 * @param ifaceName The fully qualified interface Class name.
	 * @return a component instance, or null if not found.
	 */
	Object get(String ifaceName);

	/**
	 * Check if this interface Class has a registered component.
	 * @param iface The interface Class.
	 * @return <strong>true</strong> if this interface Class has a registered component, <strong>false</strong> if not.
	 */
	boolean contains(Class iface);

	/**
	 * Check if this interface Class name has a registered component.
	 * @param ifaceName The fully qualified interface Class name.
	 * @return <strong>true</strong> if this interface has a registered component, <strong>false</strong> if not.
	 */
	boolean contains(String ifaceName);

	/**
	 * Access the lifestyle code for the component implementing this interface.
	 * @param iface The interface class.
	 * @return the lifestyle code for the component implementing this interface.
	 * @see #LIFESTYLE_UNKNOWN
	 * @see #LIFESTYLE_SINGLE
	 * @see #LIFESTYLE_MANY
	 */
	int getLifestyle(Class iface);

	/**
	 * Get all interfaces registered in the component manager hierarchy.
	 * @return A Set (String class name) of all interfaces registered in the component manager.
	 */
	Set getRegisteredInterfaces();

	/**
	 * Get all interfaces registered in just this component manager.
	 * @return A Set (String class name) of all interfaces registered in the component manager.
	 */
	Set getLocalRegisteredInterfaces();

	/**
	 * Unload registrations and release any constructed instances.
	 */
	void unload();

	/**
	 * Load a set of component definitions from this file.
	 * @param filename The name of the file containing the component definition XML.
	 */
	void loadXmlFile(String filename);

	/**
	 * Load a set of component definitions from this XML input stream.
	 * @param stream The input stream containing the component definition XML.
	 */
	void loadXmlStream(InputStream stream);

	/**
	 * Load a single already created component for this interface class as a singleton.
	 * @param iface The interface class.
	 * @param component The alread created component.
	 */
	void loadComponent(Class iface, Object component);

	/**
	 * Load a single already created component for this interface class as a singleton.
	 * @param ifaceName The fully qualified interface Class name.
	 * @param component The alread created component.
	 */
	void loadComponent(String ifaceName, Object component);

	/**
	 * Create a new component manager nested in this component manager,
	 * extending the definitions of this component manager.
	 * @return The new component manager.
	 */
	ComponentManager nestComponentManager();
	
	/**
	 * Check if the component manager is ready with all expected registrations to start service components.
	 * @return true if ready, false if not.
	 */
	boolean isReady();

	/**
	 * Set the readiness of the hierarchy
	 * @param ready true if ready, false if not.
	 */
	void setReady(boolean ready);
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/framework/component/ComponentManager.java,v 1.1 2005/05/12 15:45:33 ggolden.umich.edu Exp $
*
**********************************************************************************/
