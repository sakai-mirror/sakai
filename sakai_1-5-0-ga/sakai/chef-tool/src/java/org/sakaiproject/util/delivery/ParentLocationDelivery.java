/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/util/delivery/ParentLocationDelivery.java,v 1.3 2004/06/22 03:04:57 ggolden Exp $
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

/**
* <p>ParentLocationDelivery is a Delivery that causes the top CHEF page to refresh to a new URL.</p>
* <p>Since deliveries are processed in the courier, which is one level below this top page,
*  we can target the parent.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.3 $
*/
public class ParentLocationDelivery extends BaseDelivery
{
	/** The url for the parent. */
	protected String m_url = null;

	/**
	 * Construct.
	 * @param address The delivery address.
	 * @param url The new url for the parent.
	 */
	public ParentLocationDelivery(String address, String url)
	{
		super(address, "");
		m_url = url;
	}

	/**
	 * Compose a javascript message for delivery to the browser client window.
	 * @return The javascript message to send to the browser client window.
	 */
	public String compose()
	{
		return "if (parent)\n" + "{\n" + "\tparent.location.replace('" + m_url + "');\n" + "}\n";
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/util/delivery/ParentLocationDelivery.java,v 1.3 2004/06/22 03:04:57 ggolden Exp $
*
**********************************************************************************/
