/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/resource/ResourcePropertiesEdit.java,v 1.1 2005/05/12 15:45:32 ggolden.umich.edu Exp $
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
package org.sakaiproject.service.legacy.resource;

import java.util.Properties;

// import

/**
* <p>ResourcePropertiesEdit is the core interface for read/write ResourceProperties.</p>
*
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.1 $
*/
public interface ResourcePropertiesEdit
	extends ResourceProperties
{
	/**
	* Add a single valued property.
	* @param name The property name.
	* @param value The property value.
	*/
	public void addProperty(String name, String value);

	/**
	* Add a value to a multi-valued property.
	* @param name The property name.
	* @param value The property value.
	*/
	public void addPropertyToList(String name, String value);

	/**
	* Add all the properties from the other ResourceProperties object.
	* @param other The ResourceProperties to add.
	*/
	public void addAll(ResourceProperties other);

	/**
	* Add all the properties from the Properties object.
	* @param props The Properties to add.
	*/
	public void addAll(Properties props);

	/**
	* Remove all properties.
	*/
	public void clear();

	/**
	* Remove a property.
	* @param name The property name.
	*/
	public void removeProperty(String name);

	/**
	* Take all values from this object.
	* @param other The ResourceProperties object to take values from.
	*/
	public void set(ResourceProperties other);

}	// ResourcePropertiesEdit

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/resource/ResourcePropertiesEdit.java,v 1.1 2005/05/12 15:45:32 ggolden.umich.edu Exp $
*
**********************************************************************************/
