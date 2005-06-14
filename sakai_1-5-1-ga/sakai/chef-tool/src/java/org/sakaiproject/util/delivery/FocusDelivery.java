/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/util/delivery/FocusDelivery.java,v 1.3 2004/06/22 03:04:57 ggolden Exp $
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
* <p>FocusDelivery is a type of Delivery object that directs the focus 
* to a particular frame in a portlet.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.3 $
*/
public class FocusDelivery
	extends BaseDelivery 
{
	// an array that stores id's of elements forming a path 
	// from 'top' to the element that is to be given focus
	protected String[] m_focusPath;
	
	/**
	* Construct.
	* @param address The address.
	* @param focusPath An array containing the id's of a series of frames, 
	*    each of which is the parent of the one following it.  The last 
	*    element is the element to which focus should be delivered.  The 
	*    first frame in the array should be a child of the "top" frame.
	*/
	public FocusDelivery(String address, String[] focusPath)
	{
		super(address,focusPath[0]);
		m_focusPath = (String[])focusPath.clone();
		
	}	// FocusDelivery	

	/**
	* Composes a javascript message for delivery to the courier. This
	* message calles the setFocus function to place focus on the last 
	* element named in the focusPath array (see contructor param above).
	*
	* @return The javascript message to send to the browser client window.
	*/
	public String compose()
	{
		String jsArray = "[";
		for(int i = 0; i < m_focusPath.length; i++)
		{
			if(i > 0)
			{
				jsArray = jsArray + ",";
			}
			jsArray = jsArray + " \"" + m_focusPath[i] + "\"";
		}
		jsArray = jsArray + " ]";	  
		
		// no need for ifExistsScript because setFocus checks existence 
		// of elements along m_focusPath
		return "if(parent)\n{\n\tparent.setFocus( " + jsArray + " );\n}\n";
	
	}	// compose

}   // FocusDelivery

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/util/delivery/FocusDelivery.java,v 1.3 2004/06/22 03:04:57 ggolden Exp $
*
**********************************************************************************/
