/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/util/src/java/org/sakaiproject/util/storage/BaseXmlFileStorage.java,v 1.2 2005/05/12 01:38:24 ggolden.umich.edu Exp $
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
package org.sakaiproject.util.storage;

// import
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import org.sakaiproject.service.framework.log.cover.Logger;
import org.sakaiproject.service.legacy.resource.Edit;
import org.sakaiproject.service.legacy.resource.Resource;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.util.xml.Xml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
* <p>BaseXmlFileStorage is a class that stores Resources (of some type) in an XML file
* backed memory store, provides locked access, and generally implements a service's
* "storage" class.  The  service's storage class can extend this to provide covers to
* turn Resource and Edit into something more type specific to the service.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.2 $
* @see org.chefproject.service.component.BaseUserService
*/
public class BaseXmlFileStorage
{
	/**
	* Holds the container object, a table of the resources contained.
	*/
	protected class Container
	{
		/** The container Resource object. */
		public Resource container;
		
		/** The table of contained entry Resources. */
		public Hashtable contained;

		public Container(Resource c)
		{
			container = c;
			contained = new Hashtable();
		}
	}

	/** A full path and file name to the storage file. */
	protected String m_fileStoragePath = null;

	/** The xml tag name for the root element holding the multiple entries. */
	protected String m_rootTagName = null;

	/** The xml tag name for the element holding each container entry. */
	protected String m_containerTagName = null;

	/** The xml tag name for the element holding each actual entry. */
	protected String m_entryTagName = null;

	/** Two level store: Hashtables keyed by container ref of Container. */
	protected Hashtable m_store = null;

	/** Store all locks (across all containers), keyed by entry Resource reference. */
	protected Hashtable m_locks = null;

	/** The StorageUser to callback for new Resource and Edit objects. */
	protected StorageUser m_user = null;

	/** If set, we treat reasource ids as case insensitive. */
	protected boolean m_caseInsensitive = false;

	/**
	* Construct.
	* @param path The storage path.
	* @param root The xml tag name for the root element holding the multiple entries.
	* @param container The xml tag name for the element holding each container entry
	* (may be null if there's no container structure and all entries are in the root).
	* @param entry The xml tag name for the element holding each actual entry.
	* @param user The StorageUser class to call back for creation of Resource and Edit objects.
	*/
	public BaseXmlFileStorage(String path, String root, String container, String entry, StorageUser user)
	{
		m_fileStoragePath = path;
		m_rootTagName = root;
		m_containerTagName = container;
		m_entryTagName = entry;
		m_user = user;

	}	// BaseXmlFileStorage

	/**
	* Clean up.
	*/
	protected void finalize()
	{
		m_user = null;

	}	// finalize

	/**
	* Load the Xml Document
	*/
	protected Document load()
	{
		return Xml.readDocument(m_fileStoragePath);
	}

	/**
	* Open and be ready to read / write.
	*/
	public void open()
	{
		// setup for resources
		m_store = new Hashtable();

		Container top = null;

		// put in a Top Container if we are not doing containers
		if (m_containerTagName == null)
		{
			top = new Container(null);
			m_store.put("", top);
		}

		// setup locks
		m_locks = new Hashtable();

		try
		{
			// read the xml
			Document doc =  load();
			if (doc == null)
			{
				Logger.warn(this + "missing user xml file: " + m_fileStoragePath);
				return;
			}

			// verify the root element
			Element root = doc.getDocumentElement();
			if (!root.getTagName().equals(m_rootTagName))
			{
				Logger.warn(this + ".open(): root tag not: " + m_rootTagName + " found: " + root.getTagName());
				return;
			}

			// the children
			NodeList rootNodes = root.getChildNodes();
			final int rootNodesLength = rootNodes.getLength();
			for (int i = 0; i < rootNodesLength; i++)
			{
				Node rootNode = rootNodes.item(i);
				if (rootNode.getNodeType() != Node.ELEMENT_NODE) continue;
				Element rootElement = (Element)rootNode;

				// look for an entry element (entries in the root)
				if (	(m_containerTagName == null)
					&&	(rootElement.getTagName().equals(m_entryTagName)))
				{
					// re-create the resource and store in the top container
					Resource entry = m_user.newResource(top.container, rootElement);
					
					top.contained.put(caseId(entry.getId()), entry);
				}
				
				// look for a container element (containers in the root, entries in the containers)
				else if (	(m_containerTagName != null)
						&&	(rootElement.getTagName().equals(m_containerTagName)))
				{
					// re-create the container
					Resource containerResource = m_user.newContainer(rootElement);
					
					// add to the store
					Container container = new Container(containerResource);
					m_store.put(containerResource.getReference(), container);

					// scan for entry children of the container
					NodeList containerNodes = rootElement.getChildNodes();
					final int containerNodesLength = containerNodes.getLength();
					for (int j = 0; j < containerNodesLength; j++)
					{
						Node containerNode = containerNodes.item(j);
						if (containerNode.getNodeType() != Node.ELEMENT_NODE) continue;
						Element containerElement = (Element)containerNode;
		
						// look for an entry element (entries in the root)
						if (containerElement.getTagName().equals(m_entryTagName))
						{
							// re-create the resource
							Resource entry = m_user.newResource(container.container, containerElement);

							container.contained.put(caseId(entry.getId()), entry);
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			Logger.warn(this + ".open(): ", e);
		}

	}   // open

	/**
	 * Create and return the XML Document for our storaghe
	 */
	protected Document createDocument()
	{
		// create the Dom with a root element
		Document doc = Xml.createDocument();
		Stack stack = new Stack();
		Element root = doc.createElement(m_rootTagName);
		doc.appendChild(root);

		stack.push(root);
		
		// if we have no containers, store all elements from the Top container under the root
		if (m_containerTagName == null)
		{
			Enumeration e = ((Container) m_store.get("")).contained.elements();
			while (e.hasMoreElements())
			{
				Resource entry = (Resource) e.nextElement();
				entry.toXml(doc, stack);
			}
		}

		// otherwise, process each container
		else
		{
			Enumeration e = m_store.elements();
			while (e.hasMoreElements())
			{
				Container c = (Container) e.nextElement();
				
				// skip Top
				if (c.container == null) continue;

				// store the container
				Element containerElement = c.container.toXml(doc, stack);
				
				// push it onto the stack, so entries are created under it
				stack.push(containerElement);
				
				// store each contained under the container's element
				Enumeration elementEnum = c.contained.elements();
				while (elementEnum.hasMoreElements())
				{
					Resource entry = (Resource) elementEnum.nextElement();
					entry.toXml(doc, stack);
				}
				
				stack.pop();
			}
		}

		stack.pop();
		return doc;

	} // createDocument

	/**
	 * flush
	 */
	protected void flush()
	{

		Document doc = createDocument();
		Xml.writeDocument(doc, m_fileStoragePath);

	} // flush

	/**
	* Close.
	*/
	public void close()
	{

		flush();
		m_locks.clear();
		m_locks = null;
		m_store.clear();
		m_store = null;

	}   // close

	/**
	* Check if a container by this id exists.
	* @param ref The container reference.
	* @return true if a resource by this id exists, false if not.
	*/
	public boolean checkContainer(String ref)
	{
		Container c = ((Container) m_store.get(ref));
		return (c != null);

	}	// checkContainer

	/**
	* Get the container with this id, or null if not found.
	* @param ref The container reference.
	* @return The container with this id, or null if not found.
	*/
	public Resource getContainer(String ref)
	{
		if (ref == null) return null;
		Container c = ((Container) m_store.get(ref));
		if (c == null) return null;
		return c.container;

	}   // getContainer

	/**
	* Get a list of all containers.
	* @return A list (Resource) of all containers, or empty if none defined.
	*/
	public List getAllContainers()
	{
		List rv = new Vector();

		if (m_containerTagName == null) return rv;
		if (m_store.size() == 0) return rv;

		Enumeration e = m_store.elements();
		while (e.hasMoreElements())
		{
			Container c = (Container) e.nextElement();
			rv.add(c.container);
		}
		
		return rv;

	}   // getAllContainers

	/**
	* Add a new container with this id.
	* @param ref The channel reference.
	* @return The locked object with this id, or null if the id is in use.
	*/
	public Edit putContainer(String ref)
	{
		// if it's already defined
		Container c = ((Container) m_store.get(ref));
		if (c != null) return null;
			
		// make an Edit
		Edit edit = m_user.newContainerEdit(ref);

		synchronized (m_locks)
		{
			// if it's in the locks (i.e. it's been put() but not committed
			if (m_locks.get(edit.getReference()) != null) return null;

			// store it in the locks
			m_locks.put(edit.getReference(), edit);
		}

		return edit;

	}	// putContainer

	/**
	* Return a lock on the container with this id, or null if a lock cannot be made.
	* @param ref The container reference.
	* @return The locked object with this id, or null if a lock cannot be made.
	*/
	public Edit editContainer(String ref)
	{
		Container c = (Container) m_store.get(ref);
		if (c == null) return null;

		synchronized (m_locks)
		{
			// check for a lock in place
			if (m_locks.get(c.container.getReference()) != null) return null;

			// make an Edit
			Edit edit = m_user.newContainerEdit(c.container);

			// store it in the locks
			m_locks.put(edit.getReference(), edit);
			
			return edit;
		}

	}	// editContainer

	/**
	* Commit the changes and release the locked container.
	* @param container The container id.
	* @param edit The entry to commit.
	*/
	public void commitContainer(Edit edit)
	{
		// make a new Entry from the Edit to update the info store
		Resource updatedContainer = m_user.newContainer(edit);

		// update the store
		Container c = ((Container) m_store.get(updatedContainer.getReference()));
		if (c != null)
		{
			c.container = updatedContainer;
		}
		else
		{
			c = new Container(updatedContainer);
			m_store.put(updatedContainer.getReference(), c);
		}

		// release the lock
		m_locks.remove(edit.getReference());

	}	// commitContainer

	/**
	* Cancel the changes and release the locked container.
	* @param container The container id.
	* @param edit The entry to cancel.
	*/
	public void cancelContainer(Edit edit)
	{
		// release the lock
		m_locks.remove(edit.getReference());

	}	// cancelContainer

	/**
	* Remove this container and all it contains.
	* @param container The container id.
	* @param edit The entry to remove.
	*/
	public void removeContainer(Edit edit)
	{
		Container c = ((Container) m_store.get(edit.getReference()));
		if (c != null)
		{
			m_store.remove(c);
			// %%% better cleanup?
		}

		// release the lock
		m_locks.remove(edit.getReference());

	}	// removeContainer

	/**
	* Check if a resource by this id exists.
	* @param container The container id.
	* @param id The id.
	* @return true if a resource by this id exists, false if not.
	*/
	public boolean checkResource(String container, String id)
	{
		if (container == null) container = "";
		Container c = ((Container) m_store.get(container));
		if (c == null) return false;

		return c.contained.get(caseId(id)) != null;

	}	// checkResource

	/**
	* Get the entry with this id, or null if not found.
	* @param container The container id.
	* @param id The id.
	* @return The entry with this id, or null if not found.
	*/
	public Resource getResource(String container, String id)
	{
		if (container == null) container = "";
		Container c = ((Container) m_store.get(container));
		if (c == null) return null;

		return (Resource) c.contained.get(caseId(id));

	}   // getResource

	/**
	* Get all entries.
	* @param container The container id.
	* @return The list (Resource) of all entries.
	*/
	public List getAllResources(String container)
	{
		List rv = new Vector();

		if (container == null) container = "";
		Container c = ((Container) m_store.get(container));
		if (c == null) return rv;
		if (c.contained.size() == 0) return rv;

		rv.addAll(c.contained.values());
		return rv;

	}   // getAllResources

	/**
	 * Determine if empty
	 * @return true if empty, false if not.
	 */
	public boolean isEmpty(String container)
	{
		if (container == null) container = "";
		Container c = ((Container) m_store.get(container));
		if (c == null) return true;
		if (c.contained.size() == 0) return true;

		return false;
	}

	/**
	* Get all entries within a range sorted by id.
	* @param container The container id.
	* @param first The first position.
	* @param last The last position.
	* @return The list (Resource) of all entries within a range sorted by id.
	*/
	public List getAllResources(String container, int first, int last)
	{
		List rv = new Vector();

		if (container == null) container = "";
		Container c = ((Container) m_store.get(container));
		if (c == null) return rv;
		if (c.contained.size() == 0) return rv;

		rv.addAll(c.contained.values());
		
		Collections.sort(rv);

		// subset by position
		if (first < 1) first = 1;
		if (last >= rv.size()) last = rv.size();
			
		rv = rv.subList(first-1, last);

		return rv;

	}   // getAllResources

	/**
	* Count all entries.
	* @param container The container id.
	* @return The count of all entries.
	*/
	public int countAllResources(String container)
	{
		List rv = new Vector();

		if (container == null) container = "";
		Container c = ((Container) m_store.get(container));
		if (c == null) return 0;
		return c.contained.size();

	}   // countAllResources

	/**
	* Add a new entry with this id.
	* @param container The container id.
	* @param id The id.
	* @param others Other fields for the newResource call
	* @return The locked object with this id, or null if the id is in use.
	*/
	public Edit putResource(String container, String id, Object[] others)
	{
		if (container == null) container = "";
		Container c = ((Container) m_store.get(container));
		if (c == null) return null;

		// if it's already defined
		if (c.contained.get(caseId(id)) != null) return null;

		// make an Edit
		Edit edit = m_user.newResourceEdit(c.container, id, others);
			
		synchronized (m_locks)
		{
			// if it's in the locks (i.e. it's been put() but not committed
			if (m_locks.get(edit.getReference()) != null) return null;

			// store it in the locks
			m_locks.put(edit.getReference(), edit);
		}

		return edit;

	}   // putResource

	/**
	* Return a lock on the entry with this id, or null if a lock cannot be made.
	* @param container The container id.
	* @param id The id.
	* @return The locked object with this id, or null if a lock cannot be made.
	*/
	public Edit editResource(String container, String id)
	{
		if (container == null) container = "";
		Container c = ((Container) m_store.get(container));
		if (c == null) return null;

		Resource entry = (Resource) c.contained.get(caseId(id));
		if (entry == null) return null;
	
		synchronized (m_locks)
		{
			// check for a lock in place
			if (m_locks.get(entry.getReference()) != null) return null;

			// make an Edit
			Edit edit = m_user.newResourceEdit(c.container, entry);

			// store it in the locks
			m_locks.put(entry.getReference(), edit);
			
			return edit;
		}

	}	// editResource

	/**
	* Commit the changes and release the lock.
	* @param container The container id.
	* @param edit The entry to commit.
	*/
	public void commitResource(String container, Edit edit)
	{
		if (container == null) container = "";
		Container c = ((Container) m_store.get(container));
		if (c != null)
		{
			// make a new Entry from the Edit to update the info store
			Resource updatedEntry = m_user.newResource(c.container, edit);

			c.contained.put(caseId(updatedEntry.getId()), updatedEntry);
		}

		// release the lock
		m_locks.remove(edit.getReference());

	}	// commitResource

	/**
	* Cancel the changes and release the lock.
	* @param container The container id.
	* @param edit The entry to cancel.
	*/
	public void cancelResource(String container, Edit edit)
	{
		// release the lock
		m_locks.remove(edit.getReference());

	}	// cancelResource

	/**
	* Remove this entry.
	* @param container The container id.
	* @param edit The entry to remove.
	*/
	public void removeResource(String container, Edit edit)
	{
		if (container == null) container = "";
		Container c = ((Container) m_store.get(container));
		if (c != null)
		{
			// remove from the info store
			c.contained.remove(caseId(edit.getId()));
		}

		// release the lock
		m_locks.remove(edit.getReference());

	}   // removeResource

	/**
	* Fix the case of resource ids to support case insensitive ids if enabled
	* @param The id to fix.
	* @return The id, case modified as needed.
	*/
	protected String caseId(String id)
	{
		if (m_caseInsensitive)
		{
			return id.toLowerCase();
		}

		return id;

	}	// caseId

	/**
	* Enable / disable case insensitive ids.
	* @param setting true to set case insensitivity, false to set case sensitivity.
	*/
	protected void setCaseInsensitivity(boolean setting)
	{
		m_caseInsensitive = setting;

	}	// setCaseInsensitivity

	/**
	 * Get resources filtered by date and count and drafts, in descending (latest first) order
	 * @param container The container id.
	 * @param afterDate if null, no date limit, else limited to only messages after this date.
	 * @param limitedToLatest if 0, no count limit, else limited to only the latest this number of messages.
	 * @param draftsForId how to handle drafts: null means no drafts, "*" means all, otherwise drafts only if
	 * created by this userId.
	 * @param pubViewOnly if true, include only messages marked pubview, else include any.
	 * @return A list of Message objects that meet the criteria; may be empty
	 */
	public List getResources(String container, Time afterDate, int limitedToLatest, String draftsForId, boolean pubViewOnly)
	{
		if (container == null) container = "";
		Container c = ((Container) m_store.get(container));
		if (c == null) return new Vector();
		if (c.contained.size() == 0) return new Vector();

		List all = new Vector();
		all.addAll(c.contained.values());

		// sort latest date first
		Collections.sort(all,
			new Comparator()
			{
				public int compare(Object o1, Object o2)
				{
					// if the same object
					if (o1 == o2) return 0;

					// assume they are Resource
					Resource r1 = (Resource) o1;
					Resource r2 = (Resource) o2;

					// get each one's date
					Time t1 = m_user.getDate(r1);
					Time t2 = m_user.getDate(r2);

					// compare based on date
					int compare = t2.compareTo(t1);

					return compare;
				}
			}
		);

		// early out - if no filtering needed
		if ((limitedToLatest == 0) && (afterDate == null) && ("*".equals(draftsForId)) && !pubViewOnly)
		{
			return all;
		}

		Vector selected = new Vector();
	
		// deal with drafts / date / pubview
		for (Iterator i = all.iterator(); i.hasNext();)
		{
			Resource r = (Resource) i.next();
			Resource candidate = null;
			if (m_user.isDraft(r))
			{
				// if some drafts
				if ((draftsForId != null) && (m_user.getOwnerId(r).equals(draftsForId)))
				{
					candidate = r;
				}
			}
			else
			{
				candidate = r;
			}

			// deal with date if it passes the draft criteria
			if ((candidate != null) && (afterDate != null))
			{
				if (m_user.getDate(candidate).before(afterDate))
				{
					candidate = null;
				}
			}

			// if we want pub view only
			if ((candidate != null) && pubViewOnly)
			{
				if (candidate.getProperties().getProperty(ResourceProperties.PROP_PUBVIEW) == null)
				{
					candidate = null;
				}
			}

			// add it if it passes all criteria
			if (candidate != null)
			{
				selected.add(candidate);
			}
		}

		// pick what we need
		List rv = new Vector();

		if ((limitedToLatest > 0) && (limitedToLatest < selected.size()))
		{
			all = selected.subList(0, limitedToLatest);
		}
		else
		{
			all = selected;
		}
		
		return all;
	}

	/**
	 * Access a list of container ids that match (start with) the root.
	 * @param context The reference root to match.
	 * @return A List (String) of container id which match the root.
	 */
	public List getContainerIdsMatching(String context)
	{
		List containers = getAllContainers();
		List rv = new Vector();

		// the id of each container will be the part that follows the root reference
		final int pos = context.length();

		// filter
		for (Iterator i = containers.iterator(); i.hasNext();)
		{
			Resource r = (Resource) i.next();
			String ref = r.getReference();
			if (ref.startsWith(context))
			{
				// check the reference, return the id (what follows the root)
				String id = ref.substring(pos);
				rv.add(id);
			}
		}

		return rv;
	}

}   // BaseXmlFileStorage

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/util/src/java/org/sakaiproject/util/storage/BaseXmlFileStorage.java,v 1.2 2005/05/12 01:38:24 ggolden.umich.edu Exp $
*
**********************************************************************************/
