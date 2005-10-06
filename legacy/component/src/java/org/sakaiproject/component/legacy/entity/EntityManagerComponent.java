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

package org.sakaiproject.component.legacy.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.service.legacy.entity.EntityManager;
import org.sakaiproject.service.legacy.entity.EntityProducer;
import org.sakaiproject.service.legacy.entity.Reference;

/**
 * <p>
 * ?BaseAliasService.java is an implementation of the EntityManager for Sakai.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public class EntityManagerComponent implements EntityManager
{
	/** Our logger. */
	protected static Log M_log = LogFactory.getLog(EntityManagerComponent.class);

	/** Set of EntityProducer services. */
	protected Set m_producers = new HashSet();

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Constructors, Dependencies and their setter methods
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Init and Destroy
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		try
		{
			M_log.info("init()");
		}
		catch (Throwable t)
		{
		}
	}

	/**
	 * Returns to uninitialized state.
	 */
	public void destroy()
	{
		M_log.info("destroy()");
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * EntityManager implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * @inheritDoc
	 */
	public List getEntityProducers()
	{
		List rv = new Vector();
		rv.addAll(m_producers);

		return rv;
	}

	/**
	 * @inheritDoc
	 */
	public void registerEntityProducer(EntityProducer manager)
	{
		m_producers.add(manager);
	}

	/**
	 * @inheritDoc
	 */
	public Reference newReference(String refString)
	{
		return new ReferenceComponent(refString);
	}

	/**
	 * @inheritDoc
	 */
	public Reference newReference(Reference copyMe)
	{
		return new ReferenceComponent(copyMe);
	}

	/**
	 * @inheritDoc
	 */
	public List newReferenceList()
	{
		return new ReferenceVectorComponent();
	}

	/**
	 * @inheritDoc
	 */
	public List newReferenceList(List copyMe)
	{
		return new ReferenceVectorComponent(copyMe);
	}
}
