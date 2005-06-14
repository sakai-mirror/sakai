/**********************************************************************************
*
* $Header: /cvs/sakai/framework-component/src/java/org/sakaiproject/component/framework/courier/Attic/RefreshDelivery.java,v 1.5 2004/06/22 03:13:31 ggolden Exp $
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
package org.sakaiproject.component.framework.courier;

// imports
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;

/**
* <p>RefreshDelivery is a Delivery that causes an HTML element to get a refresh.</p>
* <p>Good for any element with a location, such as an iframe.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.5 $
*/
public class RefreshDelivery
	extends BaseDelivery
{
	/**
	* Construct.
	* @param address The address.
	* @param elementId The elementId.
	*/
	public RefreshDelivery(String address, String elementId)
	{
		super(address, elementId);

	}	// RefreshDelivery

	/**
	* Compose a javascript message for delivery to the browser client window.
	* @return The javascript message to send to the browser client window.
	*/
	public String compose()
	{
		return composeRefresh(m_elementId);

	}	// compose

	/**
	* Compose a javascript message for delivery to the browser client window.
	* @return The javascript message to send to the browser client window.
	*/
	public static String composeRefresh(String elementId)
	{
		// sometimes the location of the html element's not good - while it's loading
		// if we find it's not at least as long as the portal url, we don't try to refresh it
		int len = ServerConfigurationService.getPortalUrl().length();

		return
				ifExistsScript("parent." + elementId)
			+	" if (parent." + elementId + ".location.toString().length > " + len + ")\n"
			+	"{\n"
			+	"\ttry\n"
			+	"\t{\n"
			// use addAuto to indicate that this is an automatic courier request, not a user action
			+	"\t\tparent." + elementId + ".location.replace(addAuto(parent." + elementId + ".location));\n"
			+	"\t}\n"
			+	"\tcatch(error) {}\n"
			+	"}\n";

	}	// composeRefresh

}   // RefreshDelivery

/**********************************************************************************
*
* $Header: /cvs/sakai/framework-component/src/java/org/sakaiproject/component/framework/courier/Attic/RefreshDelivery.java,v 1.5 2004/06/22 03:13:31 ggolden Exp $
*
**********************************************************************************/
