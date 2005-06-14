/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/alias/XmlFileAliasService.java,v 1.10 2004/09/16 03:48:42 ggolden.umich.edu Exp $
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
package org.sakaiproject.component.legacy.alias;

// import
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.sakaiproject.service.legacy.alias.Alias;
import org.sakaiproject.service.legacy.alias.AliasEdit;
import org.sakaiproject.util.StringUtil;
import org.sakaiproject.util.storage.BaseXmlFileStorage;
import org.sakaiproject.util.storage.StorageUser;

/**
* <p>XmlFileAliasService is an extension of the BaseAliasService  with a in-memory xml file backed up
* storage.  The full set of users are read in at startup, kept in memory, and
* written out at shutdown.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.10 $
* @see org.chefproject.service.component.BaseAliasService
*/
public class XmlFileAliasService
	extends BaseAliasService
{
	/** A full path and file name to the storage file. */
	protected String m_storagePath = "db/alias.xml";

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
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}

	} // init

	/*******************************************************************************
	* BaseAliasService extensions
	*******************************************************************************/

	/**
	* Construct a Storage object.
	* @return The new storage object.
	*/
	protected Storage newStorage()
	{
		return new XmlFileStorage(this);

	}   // newStorage

	/*******************************************************************************
	* Storage implementation
	*******************************************************************************/

	/**
	* Covers for the BaseXmlFileStorage, providing Alias and AliasEdit parameters
	* Note: when searching for an alias, the id is case insensitive, but the actual case of the
	* alias id is preserved in the alias objects.
	*/
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
			super(m_storagePath, "aliases", null, "alias", user);
			setCaseInsensitivity(true);
	
		}	// XmlFileStorage

		public boolean check(String id) { return super.checkResource(null, id); }

		public AliasEdit get(String id) { return (AliasEdit) super.getResource(null, id); }

		public List getAll(int first, int last) { return super.getAllResources(null, first, last); }

		public List getAll() { return super.getAllResources(null); }

		public int count() { return super.countAllResources(null); }

		public AliasEdit put(String id) { return (AliasEdit) super.putResource(null, id, null); }

		public AliasEdit edit(String id) { return (AliasEdit) super.editResource(null, id); }

		public void commit(AliasEdit user) { super.commitResource(null, user); }

		public void cancel(AliasEdit user) { super.cancelResource(null, user); }

		public void remove(AliasEdit user) { super.removeResource(null, user); }

		public List getAll(String target)
		{
			List all = super.getAllResources(null);

			// pick out from all those that are for this target
			List found = new Vector();
			for (Iterator iAll = all.iterator(); iAll.hasNext();)
			{
				BaseAliasEdit a = (BaseAliasEdit) iAll.next();
				if (a.getTarget().equals(target)) found.add(a);
			}

			return found;
		}

		public List getAll(String target, int first, int last)
		{
			List all = super.getAllResources(null);

			// pick out from all those that are for this target
			List found = new Vector();
			for (Iterator iAll = all.iterator(); iAll.hasNext();)
			{
				BaseAliasEdit a = (BaseAliasEdit) iAll.next();
				if (a.getTarget().equals(target)) found.add(a);
			}

			// sort for position check
			Collections.sort(found);

			// subset by position
			if (first < 1) first = 1;
			if (last >= found.size()) last = found.size();
			
			found = found.subList(first-1, last);

			return found;
		}

		/**
		* Search for aliases with id or target matching criteria, in range.
		* @param criteria The search criteria.
		* @param first The first record position to return.
		* @param last The last record position to return.
		* @return The List (BaseAliasEdit) of all alias.
		*/
		public List search(String criteria, int first, int last)
		{
			List all = super.getAllResources(null);

			List rv = new Vector();
			for (Iterator i = all.iterator(); i.hasNext();)
			{
				Alias a = (Alias) i.next();
				if (	StringUtil.containsIgnoreCase(a.getId(),criteria)
					||	StringUtil.containsIgnoreCase(a.getTarget(),criteria))
				{
					rv.add(a);
				}
			}
			
			Collections.sort(rv);

			// subset by position
			if (first < 1) first = 1;
			if (last >= rv.size()) last = rv.size();
			
			rv = rv.subList(first-1, last);

			return rv;
		}

		/**
		* Count all the aliases with id or target matching criteria.
		* @param criteria The search criteria.
		* @return The count of all aliases with id or target matching criteria.
		*/
		public int countSearch(String criteria)
		{
			List all = super.getAllResources(null);

			Vector rv = new Vector();
			for (Iterator i = all.iterator(); i.hasNext();)
			{
				Alias a = (Alias) i.next();
				if (	StringUtil.containsIgnoreCase(a.getId(),criteria)
					||	StringUtil.containsIgnoreCase(a.getTarget(),criteria))
				{
					rv.add(a);
				}
			}
			
			return rv.size();
		}

	}   // XmlFileStorage

}   // XmlFileAliasService

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/alias/XmlFileAliasService.java,v 1.10 2004/09/16 03:48:42 ggolden.umich.edu Exp $
*
**********************************************************************************/
