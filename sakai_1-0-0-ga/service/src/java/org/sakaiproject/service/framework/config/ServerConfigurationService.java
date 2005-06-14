/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/framework/config/ServerConfigurationService.java,v 1.11 2004/07/27 02:34:16 ggolden.umich.edu Exp $
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
package org.sakaiproject.service.framework.config;

// imports
import java.util.List;

/**
 * <p>ServerConfigurationService provides information about how the CHEF server is configured.</p>
 *
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision: 1.11 $
 */
public interface ServerConfigurationService
{
	/** This string can be used to find the service in the service manager. */
	public static final String SERVICE_NAME = ServerConfigurationService.class.getName();

	/**
	* Access the unique (to the cluster) id of the server.
	* @return The unique (to the cluster) id of the server.
	*/
	public String getServerId();

	/**
	* Access the unique (to the cluster) instance id of the server.
	* @return The unique (to the cluster) instance id of the server.
	*/
	public String getServerInstance();

	/**
	* Access the combined server / instance id.
	* @return The combined server / instance id.
	*/
	public String getServerIdInstance();

	/**
	* Access the server DNS name.
	* @return The server DNS name.
	*/
	public String getServerName();

	/**
	* Access the URL to the root of the server - append any additional path to the end.
	* @return The URL to the root of the server.
	*/
	public String getServerUrl();

	/**
	* Access the URL to the CHEF access service on the server - append any additional path to the end.
	* @return The URL to the CHEF access service on the server.
	*/
	public String getAccessUrl();

	/**
	* Access the URL to the portal service on the server - append any additional path to the end.
	* @return The URL to the portal service on the server.
	*/
	public String getPortalUrl();

	/**
	* Access the site id for the gateway (public) site.
	* @return The site id for the gateway (public) site.
	*/
	public String getGatewaySiteId();

	/**
	* Access the URL to the portal tunnel on the server - append any additional path to the end.
	* @return The URL to the portal tunnel on the server.
	*/
	public String getPortalTunnelUrl();

	/**
	* Access the URL to use as a redirect when the user needs to login.
	* @return The URL to use as a redirect when the user needs to login.
	*/
	public String getLoginUrl();

	/**
	* Access the URL to use as a redirect when the user has logged out.
	* @return The URL to use as a redirect when the user has logged out.
	*/
	public String getLoggedOutUrl();

	/**
	* Access the URL to the user's "home" (myWorkspace) in the service.
	* @return The URL to the user's "home" (myWorkspace) in the service.
	*/
	public String getUserHomeUrl();

	/**
	* Access some named configuration value as a string.
	* @param name The configuration value name.
	* @return The configuration value with this name, or "" if not found.
	*/
	public String getString(String name);

	/**
	* Access some named configuration value as a string.
	* @param name The configuration value name.
	* @param dflt The value to return if not found.
	* @return The configuration value with this name, or the default value if not found.
	*/
	public String getString(String name, String dflt);

	/**
	* Access some named configuration values as an array of strings.
	* The name is the base name.
	* name + ".count" must be defined to be a positive integer - how many are defined.
	* name + "." + i (1..count) must be defined to be the values.
	* @param name The configuration value name base.
	* @return The configuration value with this name, or the null if not found.
	*/
	public String[] getStrings(String name);

	/**
	* Access some named configuration value as an int.
	* @param name The configuration value name.
	* @param dflt The value to return if not found.
	* @return The configuration value with this name, or the default value if not found.
	*/
	public int getInt(String name, int dflt);

	/**
	* Access some named configuration value as a boolean.
	* @param name The configuration value name.
	* @param dflt The value to return if not found.
	* @return The configuration value with this name, or the default value if not found.
	*/
	public boolean getBoolean(String name, boolean dflt);

	/**
	* Access the list of feature tools available on this server.
	* @return List (ToolRegistration) of available tools.
	*/
    public List getToolRegistrations();

	/**
	* Access a particular tool registration.
	* @param id The tool registration id.
	* @return The ToolRegistration with this id, or null if not found.
	*/
    public ToolRegistration getToolRegistration(String id);

	/**
	* Access the list of security locks in use on this server.
	* @return List (String) of security locks in use on this server.
	*/
    public List getLocks();

	/**
	* Access the list of services which keep site related resources.
	* @return List (GenericResourceService) of services which keep site related resources.
	*/
	public List getResourceServices();

}	// GenericServerConfigurationService

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/framework/config/ServerConfigurationService.java,v 1.11 2004/07/27 02:34:16 ggolden.umich.edu Exp $
*
**********************************************************************************/
