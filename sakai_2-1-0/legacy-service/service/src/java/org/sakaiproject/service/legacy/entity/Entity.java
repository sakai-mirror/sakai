/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
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
package org.sakaiproject.service.legacy.entity;

// imports
import java.util.Stack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p>
 * Entity is the common interface for Sakai data entities.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public interface Entity
{
	/** The character used to separate names in the region address path */
	static final String SEPARATOR = "/";

	/**
	 * Access the URL which can be used to access the entity.
	 * 
	 * @return The URL which can be used to access the entity.
	 */
	String getUrl();

	/**
	 * Access the internal reference which can be used to access the entity from within the system.
	 * 
	 * @return The the internal reference which can be used to access the entity from within the system.
	 */
	String getReference();

	/**
	 * Access the id of the entity.
	 * 
	 * @return The id.
	 */
	String getId();

	/**
	 * Access the entity's properties.
	 * 
	 * @return The entity's properties.
	 */
	ResourceProperties getProperties();

	/**
	 * Serialize the entity into XML, adding an element to the doc under the top of the stack element.
	 * 
	 * @param doc
	 *        The DOM doc to contain the XML (or null for a string return).
	 * @param stack
	 *        The DOM elements, the top of which is the containing element of the new "entity" element.
	 * @return The newly added element.
	 */
	Element toXml(Document doc, Stack stack);
}