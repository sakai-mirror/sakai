/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/site/XmlFileSiteService.java,v 1.7 2004/06/22 03:14:01 ggolden Exp $
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
package org.sakaiproject.component.legacy.site;

// import
import java.util.List;

import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SiteEdit;
import org.sakaiproject.util.storage.BaseXmlFileStorage;
import org.sakaiproject.util.storage.StorageUser;

/**
* <p>XmlFileSiteService is an extension of the BaseSiteService  with a in-memory xml file backed up
* storage.  The full set of sites are read in at startup, kept in memory, and
* written out at shutdown.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.7 $
* @see org.chefproject.service.component.BaseSiteService
*/
public class XmlFileSiteService
	extends BaseSiteService
{
	/** A full path and file name to the storage file. */
	protected String m_storagePath = "db/site.xml";

	/*******************************************************************************
	* Constructors, Dependencies and their setter methods
	*******************************************************************************/

	/**
	 * Configuration: set the storage path
	 * @param path The storage path.
	 */
	public void setStoragePath(String path)
	{
		m_storagePath = path;
	}

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		try
		{
			super.init();

			m_logger.info(this +".init(): storage path: " + m_storagePath);

			// we don't need the cache, since we hold everything in memory already
			m_cache.disable();
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}

	} // init

	/*******************************************************************************
	* BaseSiteService extensions
	*******************************************************************************/

	/**
	* Construct a Storage object.
	* @return The new storage object.
	*/
	protected Storage newStorage()
	{
		return new XmlFileStorage(this);

	}	// newStorage

	/*******************************************************************************
	* Storage implementation
	*******************************************************************************/

	protected class XmlFileStorage
		extends BaseXmlFileStorage
		implements Storage
	{
		/**
		* Construct.
		* @param user The StorageUser class to call back for creation of Resource and Edit objects.
		*/
		public XmlFileStorage(StorageUser user)
		{
			super(m_storagePath, "sites", null, "site", user);

		}	// XmlFileStorage

		public boolean check(String id) { return super.checkResource(null, id); }

		public Site get(String id) { return (Site) super.getResource(null, id); }

		public List getAll() { return super.getAllResources(null); }

		public SiteEdit put(String id) { return (SiteEdit) super.putResource(null, id, null); }

		public SiteEdit edit(String id) { return (SiteEdit) super.editResource(null, id); }

		public void commit(SiteEdit edit) { super.commitResource(null, edit); }

		public void cancel(SiteEdit edit) { super.cancelResource(null, edit); }

		public void remove(SiteEdit edit) { super.removeResource(null, edit); }

	}	// XmlFileStorage

}	// XmlFileSiteService

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/site/XmlFileSiteService.java,v 1.7 2004/06/22 03:14:01 ggolden Exp $
*
**********************************************************************************/
