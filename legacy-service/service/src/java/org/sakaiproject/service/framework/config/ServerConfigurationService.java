/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
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

// package
package org.sakaiproject.service.framework.config;

// imports
import java.util.List;

/**
 * <p>
 * ServerConfigurationService provides information about how the server is configured.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public interface ServerConfigurationService
{
	/** This string can be used to find the service in the service manager. */
	public static final String SERVICE_NAME = ServerConfigurationService.class.getName();

	/**
	 * Access the unique (to the cluster) id of the server.
	 * 
	 * @return The unique (to the cluster) id of the server.
	 */
	String getServerId();

	/**
	 * Access the unique (to the cluster) instance id of the server.
	 * 
	 * @return The unique (to the cluster) instance id of the server.
	 */
	String getServerInstance();

	/**
	 * Access the combined server / instance id.
	 * 
	 * @return The combined server / instance id.
	 */
	String getServerIdInstance();

	/**
	 * Access the server DNS name.
	 * 
	 * @return The server DNS name.
	 */
	String getServerName();

	/**
	 * Access the URL to the root of the server - append any additional path to the end.
	 * 
	 * @return The URL to the root of the server.
	 */
	String getServerUrl();

	/**
	 * Access the URL to the help service on the server - append in the path the tool well known id for context sensitive help.
	 * @param helpContext The context string.
	 * @return The URL to the help service on the server.
	 */
	String getHelpUrl(String helpContext);

	/**
	 * Access the URL to the access service on the server - append any additional path to the end.
	 * 
	 * @return The URL to the access service on the server.
	 */
	String getAccessUrl();

	/**
	 * Access the URL to the portal service on the server - append any additional path to the end.
	 * 
	 * @return The URL to the portal service on the server.
	 */
	String getPortalUrl();

	/**
	 * Access the site id for the gateway (public) site.
	 * 
	 * @return The site id for the gateway (public) site.
	 */
	String getGatewaySiteId();

	/**
	 * Access the URL to use as a redirect when the user has logged out.
	 * 
	 * @return The URL to use as a redirect when the user has logged out.
	 */
	String getLoggedOutUrl();

	/**
	 * Access the URL to the user's "home" (My Workspace) in the service.
	 * 
	 * @return The URL to the user's "home" (My Workspace) in the service.
	 */
	String getUserHomeUrl();

	/**
	 * Access the file path to the "sakai home" on the app server.
	 * 
	 * @return The file path to the "sakai home" on the app server.
	 */
	String getSakaiHomePath();

	/**
	 * Access some named configuration value as a string.
	 * 
	 * @param name
	 *        The configuration value name.
	 * @return The configuration value with this name, or "" if not found.
	 */
	String getString(String name);

	/**
	 * Access some named configuration value as a string.
	 * 
	 * @param name
	 *        The configuration value name.
	 * @param dflt
	 *        The value to return if not found.
	 * @return The configuration value with this name, or the default value if not found.
	 */
	String getString(String name, String dflt);

	/**
	 * Access some named configuration values as an array of strings. The name is the base name. name + ".count" must be defined to be a positive integer - how many are defined. name + "." + i (1..count) must be defined to be the values.
	 * 
	 * @param name
	 *        The configuration value name base.
	 * @return The configuration value with this name, or the null if not found.
	 */
	String[] getStrings(String name);

	/**
	 * Access some named configuration value as an int.
	 * 
	 * @param name
	 *        The configuration value name.
	 * @param dflt
	 *        The value to return if not found.
	 * @return The configuration value with this name, or the default value if not found.
	 */
	int getInt(String name, int dflt);

	/**
	 * Access some named configuration value as a boolean.
	 * 
	 * @param name
	 *        The configuration value name.
	 * @param dflt
	 *        The value to return if not found.
	 * @return The configuration value with this name, or the default value if not found.
	 */
	boolean getBoolean(String name, boolean dflt);

	/**
	 * Access the list of security locks in use on this server.
	 * 
	 * @return List (String) of security locks in use on this server.
	 */
	List getLocks();

	/**
	 * Access the list of tool ids in order for this category, to impose on the displays of many tools
	 * @param category The tool category
	 * @return An ordered list of tool ids (String) indicating the desired tool display order, or an empty list if there are none for this category.
	 */
	List getToolOrder(String category);

	/**
	 * Access the list of tool ids that are required for this category.
	 * @param category The tool category.
	 * @return A list of tool ids (String) that are required for this category, or an empty list if there are none for this category.
	 */
	List getToolsRequired(String category);
}



