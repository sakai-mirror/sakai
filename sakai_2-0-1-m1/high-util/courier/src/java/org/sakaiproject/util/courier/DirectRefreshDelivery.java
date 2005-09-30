/**********************************************************************************
 *
 * $Header: /cvs/sakai2/high-util/courier/src/java/org/sakaiproject/util/courier/DirectRefreshDelivery.java,v 1.2 2005/05/12 18:53:52 ggolden.umich.edu Exp $
 *
 ***********************************************************************************
 *
 * Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.util.courier;

/**
 * <p>
 * DirectRefreshDelivery is a Delivery that does a direct location refresh.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class DirectRefreshDelivery extends BaseDelivery
{
	/**
	 * Construct.
	 * 
	 * @param address
	 *        The address.
	 */
	public DirectRefreshDelivery(String address)
	{
		super(address, null);
	}

	/**
	 * Construct.
	 * 
	 * @param address
	 *        The address.
	 * @param elementId
	 *        The elementId.
	 */
	public DirectRefreshDelivery(String address, String elementId)
	{
		super(address, elementId);
	}

	/**
	 * Compose a javascript message for delivery to the browser client window.
	 * 
	 * @return The javascript message to send to the browser client window.
	 */
	public String compose()
	{
		return "try { " + ((m_elementId == null) ? "" : m_elementId + ".") + "location.replace("
				+ ((m_elementId == null) ? "" : m_elementId + ".") + "location); } catch(error) {}";
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/high-util/courier/src/java/org/sakaiproject/util/courier/DirectRefreshDelivery.java,v 1.2 2005/05/12 18:53:52 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
