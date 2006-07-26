/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
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

package org.sakaiproject.component.adaptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.id.IdManager;
import org.sakaiproject.service.legacy.id.IdService;

/**
 * <p>
 * IdServiceAdaptor implements the Sakai 1 Legacy IdService as an adaptor to the new framework IdManager.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class IdServiceAdaptor implements IdService
{
	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(IdServiceAdaptor.class);

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Dependencies and their setter methods
	 *********************************************************************************************************************************************************************************************************************************************************/

	/** Dependency: the id manager. */
	protected IdManager m_idManager = null;

	/**
	 * Dependency - set the id manager.
	 * 
	 * @param value
	 *        The id manager.
	 */
	public void setIdManager(IdManager manager)
	{
		m_idManager = manager;
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Init and Destroy
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		M_log.info("init()");
	}

	/**
	 * Final cleanup.
	 */
	public void destroy()
	{
		M_log.info("destroy()");
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Work interface methods: IdService
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Allocate a unique Id.
	 * 
	 * @return A unique Id.
	 */
	public synchronized String getUniqueId()
	{
		return m_idManager.createUuid();
	}
}



