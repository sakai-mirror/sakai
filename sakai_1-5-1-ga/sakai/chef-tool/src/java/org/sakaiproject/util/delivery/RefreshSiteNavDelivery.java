/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/util/delivery/RefreshSiteNavDelivery.java,v 1.4 2004/06/22 03:04:57 ggolden Exp $
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
package org.sakaiproject.util.delivery;

// imports

/**
* <p>RefreshSiteNavDelivery is a Delivery that causes the top site navigation frame to refresh.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.4 $
*/
public class RefreshSiteNavDelivery
	extends RefreshDelivery
{
	/**
	* Construct.
	* @param address The address.
	* @param elementId The elementId.
	*/
	public RefreshSiteNavDelivery(String address)
	{
		super(address, "");
	}

	/**
	* Compose a javascript message for delivery to the browser client window.
	* @return The javascript message to send to the browser client window.
	*/
	public String compose()
	{
		// TODO: assumes a "sitenav" peer frame for refresh...
		return
				"if (parent)\n\tif(parent.sitenav)\n"
			+	"{\n"
			// %%% if we use addAuto, the visible main URL will become auto -ggolden
			// use addAuto to indicate that this is an automatic courier request, not a user action
			// +		"\tparent.location.replace(addAuto(parent.location));\n"
			+		"\tparent.sitenav.location.replace(parent.sitenav.location);\n"
			+	"}\n";
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/util/delivery/RefreshSiteNavDelivery.java,v 1.4 2004/06/22 03:04:57 ggolden Exp $
*
**********************************************************************************/
