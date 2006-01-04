/**********************************************************************************
 * $URL$
 * $Id$
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

// package
package org.sakaiproject.service.legacy.entity;

import java.util.List;

// imports

/**
 * <p>
 * EntityManager is the API for managing EntityProducer services / managers.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public interface EntityManager
{
	/**
	 * Access the list of managers that are registered EntityProducer.
	 * 
	 * @return List (EntityProducer) of managers that are registered EntityProducer.
	 */
	List getEntityProducers();

	/**
	 * Register this as an EntityProducer.
	 * 
	 * @param manager
	 *        The EntityProducer manager to register.
	 */
	void registerEntityProducer(EntityProducer manager);

	/**
	 * Create a new Reference object, from the given reference string.
	 * 
	 * @param refString
	 *        The reference string.
	 * @return a new reference object made from the given reference string.
	 */
	Reference newReference(String refString);

	/**
	 * Create a new Reference object, as a copy of the given Reference object.
	 * 
	 * @param copyMe
	 *        The Reference object to copy
	 * @return a new Reference object, as a copy of the given Reference object.
	 */
	Reference newReference(Reference copyMe);

	/**
	 * Create a new List specially designed to hold References.
	 * 
	 * @return a new List specially designed to hold References.
	 */
	List newReferenceList();

	/**
	 * Create a new List specially designed to hold References, as a copy of another.
	 * 
	 * @param copyMe
	 *        Make the new list contain a copy of this list.
	 * @return a new List specially designed to hold References, as a copy of another.
	 */
	List newReferenceList(List copyMe);
}
