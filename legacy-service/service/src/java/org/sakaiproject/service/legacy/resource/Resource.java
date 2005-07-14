/**********************************************************************************
* $URL$
* $Id$
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
package org.sakaiproject.service.legacy.resource;

// imports
import java.util.Stack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
* <p>Resource is the interface for objects that are considered Sakai resources and have unique URL addresses.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public interface Resource
{
	/** The character used to separate names in the region address path */
	public static final String SEPARATOR = "/";

	/**
	* Access the URL which can be used to access the resource.
	* @return The URL which can be used to access the resource.
	*/
	public String getUrl();

	/**
	* Access the internal reference which can be used to access the resource from within the system.
	* @return The the internal reference which can be used to access the resource from within the system.
	*/
	public String getReference();

	/**
	* Access the id of the resource.
	* @return The id.
	*/
	public String getId();

	/**
	* Access the resource's properties.
	* @return The resource's properties.
	*/
	public ResourceProperties getProperties();

	/**
	* Serialize the resource into XML, adding an element to the doc under the top of the stack element.
	* @param doc The DOM doc to contain the XML (or null for a string return).
	* @param stack The DOM elements, the top of which is the containing element of the new "resource" element.
	* @return The newly added element.
	*/
	public Element toXml(Document doc, Stack stack);

}	// Resource



