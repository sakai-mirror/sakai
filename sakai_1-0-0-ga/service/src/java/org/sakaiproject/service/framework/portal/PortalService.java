/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/framework/portal/PortalService.java,v 1.8 2004/09/30 20:20:51 ggolden.umich.edu Exp $
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
package org.sakaiproject.service.framework.portal;

// imports
import org.sakaiproject.service.framework.session.SessionState;

/**
 * <p>PortalService is our interface to the portal engine and related information in the request.</p>
 *
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision: 1.8 $
 */
public interface PortalService
{
	/** This string can be used to find the service in the service manager. */
	public static final String SERVICE_NAME = PortalService.class.getName();

	/**
	* Extract the site id from the current request.
	* @return The site id implied from the current request.
	*/
	public String getCurrentSiteId();

	/**
	* Extract the site page id from the current request.
	* @return The site page id implied from the current request.
	*/
	public String getCurrentSitePageId();

	/**
	* Extract the tool id from the current request.
	* @return The tool id implied from the current request.
	*/
	public String getCurrentToolId();

	/**
	* Get the SessionState object for the tool in the current request.
	* @return The SessionState object for the tool in the current request.
	*/
	public SessionState getCurrentToolState();

	/**
	* Format an identity string for the current request's site-tool location:
	* identifying the specific site, and if present, this particular tool in the site.
	* @return The current request's location id.
	*/
	public String getCurrentLocationId();

	/**
	* Format an identity string for the current request's site-tool location:
	* identifying the specific site, and if specified, the id parameter in place of a tool in the site.
	* @param id The tool id to append, if any
	* @return The current request's location id.
	*/
	public String getCurrentLocationId(String id);

	/**
	* Format an identity string for the current request's client window:
	* identifying the session, the specific site, and if present, a particular tool in the site.
	* @return The current request's location id.
	*/
	public String getCurrentClientWindowId();

	/**
	* Format an identity string for the current request's client window:
	* identifying the session, the specific site, and if provided, modified by this particular id.
	* @param id The id modifier, skipped if null.
	* @return The current request's location id.
	*/
	public String getCurrentClientWindowId(String id);

	/**
	 * Access the HTML element id that contains this tool's rendered document.
	 * @param toolId The tool id.
	 * @return The HTML element id that contains this tool's rendered document.
	 */
	String getToolDocElementId(String toolId);
	
	/**
	 * Access the HTML element id that contains this tool's rendered title.
	 * @param toolId The tool id.
	 * @return The HTML element id that contains this tool's rendered title.
	 */
	String getToolTitleElementId(String toolId);

	/**
	 * Access the HTML element id that contains this tool's presence display.
	 * @param toolId The tool id.
	 * @return The HTML element id that contains this tool's presence display.
	 */
	String getPresenceElementId(String toolId);
	
}	// PortalService

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/framework/portal/PortalService.java,v 1.8 2004/09/30 20:20:51 ggolden.umich.edu Exp $
*
**********************************************************************************/
