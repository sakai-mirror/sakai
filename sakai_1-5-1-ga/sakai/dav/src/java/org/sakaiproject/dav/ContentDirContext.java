/**********************************************************************************
*
* $Header: /cvs/sakai/dav/src/java/org/sakaiproject/dav/ContentDirContext.java,v 1.2 2004/09/30 20:21:43 ggolden.umich.edu Exp $
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

package org.sakaiproject.dav;

import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;

import org.apache.naming.resources.Resource;
import org.apache.naming.resources.ResourceAttributes;
import org.sakaiproject.exception.EmptyException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.service.framework.log.cover.Log;
import org.sakaiproject.service.framework.log.cover.Logger;
import org.sakaiproject.service.legacy.content.ContentCollection;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.content.ContentResourceEdit;
import org.sakaiproject.service.legacy.content.cover.ContentHostingService;
import org.sakaiproject.service.legacy.notification.cover.NotificationService;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.resource.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.time.TimeBreakdown;
import org.sakaiproject.service.legacy.time.cover.TimeService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.util.Blob;

/**
 * <p>ContentDirContext is a DirContext for hosted content.</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.2 $
 */
public class ContentDirContext implements DirContext
{
	protected String path;
	public boolean isCollection;
	private ContentCollection collection;
	private boolean isRootCollection;
	private ResourceProperties props;

	public Object lookup(String id) throws NamingException
	{
		path = id;

		// resource or collection? check the properties (also finds bad id and checks permissions)
		isCollection = false;
		isRootCollection = false;
		try
		{
			path = fixDirPathCHEF(path);

			props = ContentHostingService.getProperties(path);

			isCollection = props.getBooleanProperty(ResourceProperties.PROP_IS_COLLECTION);

			if (isCollection)
			{
				collection = ContentHostingService.getCollection(path);
				isRootCollection = ContentHostingService.isRootCollection(path);
			}
		}
		catch (PermissionException e)
		{
			Logger.info("ContentDirContextlookup - You do not have permission to view this resource: " + id);
			throw new NamingException();
		}
		catch (IdUnusedException e)
		{
			Logger.warn("ContentDirContextlookup - This resource does not exist: " + id);
			throw new NamingException();
		}
		catch (EmptyException e)
		{
			Logger.warn("ContentDirContextlookup - This resource is empty: " + id);
			throw new NamingException();
		}
		catch (TypeException e)
		{
			Logger.warn("ContentDirContextlookup - This resource has a type exception: " + id);
			throw new NamingException();
		}

		if (isCollection)
		{
			return this;
		}
		else
		{
			// TODO: this may be used to access, via getContent()
			return new MyResource(path);
		}
	}

	public NamingEnumeration list(String id)
	{
		try
		{
			Object object = lookup(id);
		}
		catch (Exception e)
		{
			return null;
		}
		if (Log.getLogger("chef").isDebugEnabled())
			Log.debug("chef", "DirContextCHEF.list getting collection members and iterator");
		List members = collection.getMembers();
		Iterator it = members.iterator();
		return new MyNamingEnumeration(it);
	}

	public String fixDirPathCHEF(String path)
	{
		String tmpPath = path;

		ResourceProperties props;
		try
		{
			props = ContentHostingService.getProperties(tmpPath);
		}
		catch (IdUnusedException e)
		{
			if (!tmpPath.endsWith("/"))
			{
				// We will add a slash and try again
				String newPath = tmpPath + "/";
				try
				{
					props = ContentHostingService.getProperties(newPath);
					tmpPath = newPath;
				}
				catch (Exception x)
				{
				} // Ignore everything
			}
		}
		catch (Exception e)
		{
		} // Ignore all other exceptions

		return tmpPath;
	}

	/**
	 * {@inheritDoc}
	 */
	public Attributes getAttributes(Name name) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: getAttributes(Name name)");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Attributes getAttributes(String name) throws NamingException
	{
		try
		{
			Object object = lookup(name);
		}
		catch (Exception e)
		{
			return null;
		}

		return new MyResourceAttributes(props);
	}

	/**
	 * {@inheritDoc}
	 */
	public Attributes getAttributes(Name name, String[] attrIds) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: getAttributes(Name name, String[] attrIds)");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Attributes getAttributes(String name, String[] attrIds) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: getAttributes(String name, String[] attrIds)");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void modifyAttributes(Name name, int mod_op, Attributes attrs) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: modifyAttributes(Name name, int mod_op, Attributes attrs)");

	}

	/**
	 * {@inheritDoc}
	 */
	public void modifyAttributes(String name, int mod_op, Attributes attrs) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: modifyAttributes(String name, int mod_op, Attributes attrs)");

	}

	/**
	 * {@inheritDoc}
	 */
	public void modifyAttributes(Name name, ModificationItem[] mods) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: modifyAttributes(Name name, ModificationItem[] mods)");

	}

	/**
	 * {@inheritDoc}
	 */
	public void modifyAttributes(String name, ModificationItem[] mods) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: modifyAttributes(String name, ModificationItem[] mods)");

	}

	/**
	 * {@inheritDoc}
	 */
	public void bind(Name name, Object obj, Attributes attrs) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: bind(Name name, Object obj, Attributes attrs)");

	}

	/**
	 * {@inheritDoc}
	 */
	public void bind(String name, Object obj, Attributes attrs) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: bind(String name, Object obj, Attributes attrs)");

	}

	/**
	 * {@inheritDoc}
	 */
	public void rebind(Name name, Object obj, Attributes attrs) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: rebind(Name name, Object obj, Attributes attrs)");

	}

	/**
	 * {@inheritDoc}
	 */
	public void rebind(String name, Object obj, Attributes attrs) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: rebind(String name, Object obj, Attributes attrs)");

	}

	/**
	 * {@inheritDoc}
	 */
	public DirContext createSubcontext(Name name, Attributes attrs) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: createSubcontext(Name name, Attributes attrs)");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public DirContext createSubcontext(String name, Attributes attrs) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: createSubcontext(String name, Attributes attrs)");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public DirContext getSchema(Name name) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: getSchema(Name name)");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public DirContext getSchema(String name) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: getSchema(String name)");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public DirContext getSchemaClassDefinition(Name name) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: getSchemaClassDefinition(Name name");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public DirContext getSchemaClassDefinition(String name) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: getSchemaClassDefinition(String name)");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public NamingEnumeration search(Name name, Attributes matchingAttributes, String[] attributesToReturn)
		throws NamingException
	{
		Logger.warn(
			"ContentDirContext: calling: search(Name name, Attributes matchingAttributes, String[] attributesToReturn)");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public NamingEnumeration search(String name, Attributes matchingAttributes, String[] attributesToReturn)
		throws NamingException
	{
		Logger.warn(
			"ContentDirContext: calling: search(String name, Attributes matchingAttributes, String[] attributesToReturn)");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public NamingEnumeration search(Name name, Attributes matchingAttributes) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: search(Name name, Attributes matchingAttributes)");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public NamingEnumeration search(String name, Attributes matchingAttributes) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: search(String name, Attributes matchingAttributes)");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public NamingEnumeration search(Name name, String filter, SearchControls cons) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: search(Name name, String filter, SearchControls cons)");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public NamingEnumeration search(String name, String filter, SearchControls cons) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: search(String name, String filter, SearchControls cons)");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public NamingEnumeration search(Name name, String filterExpr, Object[] filterArgs, SearchControls cons)
		throws NamingException
	{
		Logger.warn(
			"ContentDirContext: calling: search(Name name, String filterExpr, Object[] filterArgs, SearchControls cons)");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public NamingEnumeration search(String name, String filterExpr, Object[] filterArgs, SearchControls cons)
		throws NamingException
	{
		Logger.warn(
			"ContentDirContext: calling: search(String name, String filterExpr, Object[] filterArgs, SearchControls cons)");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object lookup(Name name) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: lookup(Name name)");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void bind(Name name, Object obj) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: bind(Name name, Object obj)");

	}

	/**
	 * {@inheritDoc}
	 */
	public void bind(String name, Object obj) throws NamingException
	{
		try
		{
			// we want to add this named resource - obj is a Resource with a stream
			if (obj instanceof Resource)
			{
				ResourcePropertiesEdit resourceProperties = ContentHostingService.newResourceProperties();

				// read
				Blob b = new Blob();
				int len = b.read(((Resource) obj).streamContent());

				resourceProperties.addProperty(ResourceProperties.PROP_COPYRIGHT, copyright());

				String displayName = justName(name);
				resourceProperties.addProperty(ResourceProperties.PROP_DISPLAY_NAME, displayName);

				ContentResource resource =
					ContentHostingService.addResource(
						name,
						null,
						b.getBytes(),
						resourceProperties,
						NotificationService.NOTI_NONE);
			}
		}
		catch (Throwable t)
		{
			Logger.warn("ContentDirContext.bind: " + t.toString());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void rebind(Name name, Object obj) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: rebind(Name name, Object obj)");

	}

	/**
	 * {@inheritDoc}
	 */
	public void rebind(String name, Object obj) throws NamingException
	{
		try
		{
			// we want to update the content of this named resource - obj is a Resource with a stream
			if (obj instanceof Resource)
			{
				ContentResourceEdit edit = ContentHostingService.editResource(name);

				// read
				Blob b = new Blob();
				int len = b.read(((Resource) obj).streamContent());

				edit.setContent(b.getBytes());
				ContentHostingService.commitResource(edit);
			}
		}
		catch (Throwable t)
		{
			Logger.warn("ContentDirContext.bind: " + t.toString());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void unbind(Name name) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: unbind(Name name)");

	}

	/**
	 * {@inheritDoc}
	 */
	public void unbind(String name) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: unbind(String name)");

	}

	/**
	 * {@inheritDoc}
	 */
	public void rename(Name oldName, Name newName) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: rename(Name oldName, Name newName)");

	}

	/**
	 * {@inheritDoc}
	 */
	public void rename(String oldName, String newName) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: rename(String oldName, String newName)");

	}

	/**
	 * {@inheritDoc}
	 */
	public NamingEnumeration list(Name name) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: list(Name name)");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public NamingEnumeration listBindings(Name name) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: listBindings(Name name)");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public NamingEnumeration listBindings(String name) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: listBindings(String name)");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void destroySubcontext(Name name) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: destroySubcontext(Name name)");

	}

	/**
	 * {@inheritDoc}
	 */
	public void destroySubcontext(String name) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: destroySubcontext(String name)");

	}

	/**
	 * {@inheritDoc}
	 */
	public Context createSubcontext(Name name) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: createSubcontext(Name name)");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Context createSubcontext(String name) throws NamingException
	{
		// create a collection
		try
		{
			ResourcePropertiesEdit resourceProperties = ContentHostingService.newResourceProperties();
			resourceProperties.addProperty(ResourceProperties.PROP_DISPLAY_NAME, justName(name));
			resourceProperties.addProperty(ResourceProperties.PROP_COPYRIGHT, copyright());

			ContentCollection collection = ContentHostingService.addCollection(name, resourceProperties);
		}
		catch (Throwable t)
		{
			Logger.warn("ContentDirContext: createSubcontext(String name): " + t);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object lookupLink(Name name) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: lookupLink(Name name)");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object lookupLink(String name) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: lookupLink(String name)");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public NameParser getNameParser(Name name) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: getNameParser(Name name)");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public NameParser getNameParser(String name) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: getNameParser(String name)");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Name composeName(Name name, Name prefix) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: composeName(Name name, Name prefix)");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String composeName(String name, String prefix) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: composeName(String name, String prefix)");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object addToEnvironment(String propName, Object propVal) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: addToEnvironment(String propName, Object propVal)");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object removeFromEnvironment(String propName) throws NamingException
	{
		Logger.warn("ContentDirContext: calling: removeFromEnvironment(String propName)");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Hashtable getEnvironment() throws NamingException
	{
		Logger.warn("ContentDirContext: calling: getEnvironment()");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() throws NamingException
	{
		Logger.warn("ContentDirContext: calling: close()");

	}

	/**
	 * {@inheritDoc}
	 */
	public String getNameInNamespace() throws NamingException
	{
		Logger.warn("ContentDirContext: calling: getNameInNamespace()");
		return null;
	}

	protected String justName(String str)
	{
		try
		{
			// Note: there may be a trailing separator
			int pos = str.lastIndexOf("/", str.length() - 2);
			String rv = str.substring(pos + 1);
			if (rv.endsWith("/"))
			{
				rv = rv.substring(0, rv.length()-1);
			}
			return rv;
		}
		catch (Throwable t)
		{
			return str;
		}
	}

	protected String copyright()
	{
		User user = UserDirectoryService.getCurrentUser();

		TimeBreakdown timeBreakdown = TimeService.newTime().breakdownLocal();
		String mycopyright =
			"copyright (c)"
				+ " "
				+ timeBreakdown.getYear()
				+ ", "
				+ user.getDisplayName()
				+ ". All Rights Reserved. ";

		return mycopyright;
	}

	public class MyNamingEnumeration implements NamingEnumeration
	{
		protected Iterator m_iterator = null;

		public MyNamingEnumeration(Iterator it)
		{
			m_iterator = it;
		}

		/**
		 * {@inheritDoc}
		 */
		public Object next() throws NamingException
		{
			return new NameClassPair(justName((String) m_iterator.next()), "");
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasMore() throws NamingException
		{
			return m_iterator.hasNext();
		}

		/**
		 * {@inheritDoc}
		 */
		public void close() throws NamingException
		{
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasMoreElements()
		{
			return m_iterator.hasNext();
		}

		/**
		 * {@inheritDoc}
		 */
		public Object nextElement()
		{
			return new NameClassPair(justName((String) m_iterator.next()), "");
		}
	}

	public class MyResourceAttributes extends ResourceAttributes
	{
		public MyResourceAttributes(ResourceProperties props)
		{
			try
			{
				setCreationDate(new Date(props.getTimeProperty(ResourceProperties.PROP_CREATION_DATE).getTime()));
			}
			catch (Exception e)
			{
			}
			try
			{
				setLastModifiedDate(new Date(props.getTimeProperty(ResourceProperties.PROP_MODIFIED_DATE).getTime()));
			}
			catch (Exception e)
			{
			}
			try
			{
				setETag(path);
			}
			catch (Exception e)
			{
			}
			try
			{
				setContentLength(props.getLongProperty(ResourceProperties.PROP_CONTENT_LENGTH));
			}
			catch (Exception e)
			{
			}
		}
	}

	public class MyResource extends Resource
	{
		protected String m_id = null;

		public MyResource(String id)
		{
			m_id = path;
		}

		public byte[] getContent()
		{
			try
			{
				ContentResource resource = ContentHostingService.getResource(m_id);
				byte[] content = resource.getContent();

				//				// for URL content type, encode a redirect to the body URL
				//				if (contentType.equalsIgnoreCase(ResourceProperties.TYPE_URL))
				return content;
			}
			catch (PermissionException e)
			{
			}
			catch (IdUnusedException e)
			{
			}
			catch (TypeException e)
			{
			}

			return null;
		}
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/dav/src/java/org/sakaiproject/dav/ContentDirContext.java,v 1.2 2004/09/30 20:21:43 ggolden.umich.edu Exp $
*
**********************************************************************************/
