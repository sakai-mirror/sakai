/**********************************************************************************
 *
 * $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/news/BasicNewsService.java,v 1.1 2005/04/13 03:16:51 ggolden.umich.edu Exp $
 *
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

package org.sakaiproject.component.legacy.news;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.memory.Cache;
import org.sakaiproject.service.framework.memory.MemoryService;
import org.sakaiproject.service.legacy.news.NewsChannel;
import org.sakaiproject.service.legacy.news.NewsConnectionException;
import org.sakaiproject.service.legacy.news.NewsFormatException;
import org.sakaiproject.service.legacy.news.NewsItem;
import org.sakaiproject.service.legacy.news.NewsService;
import org.sakaiproject.util.Filter;
import org.sakaiproject.util.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * <p>
 * BasicNewsService ...
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.1 $
 */
public class BasicNewsService implements NewsService
{
	// default expiration is 10 minutes (expressed in seconds)
	protected static int DEFAULT_EXPIRATION = 10 * 60;

	// The cache in which channels and news-items are held
	protected Cache m_storage = null;

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Constructors, Dependencies and their setter methods
	 *********************************************************************************************************************************************************************************************************************************************************/

	/** Dependency: logging service */
	protected Logger m_logger = null;

	/**
	 * Dependency: logging service.
	 * 
	 * @param service
	 *        The logging service.
	 */
	public void setLogger(Logger service)
	{
		m_logger = service;
	}

	/** Dependency: MemoryService. */
	protected MemoryService m_memoryService = null;

	/**
	 * Dependency: MemoryService.
	 * 
	 * @param service
	 *        The MemoryService.
	 */
	public void setMemoryService(MemoryService service)
	{
		m_memoryService = service;
	}

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
			m_logger.info(this + ".init()");

			m_storage = m_memoryService.newCache();
		}
		catch (Throwable t)
		{
			m_logger.warn(this + ".init(): ", t);
		}

	} // init

	/**
	 * Returns to uninitialized state.
	 */
	public void destroy()
	{
		m_storage.destroy();
		m_storage = null;

		m_logger.info(this + ".destroy()");
	}

	/**
	 * <p>
	 * Checks whether channel is cached. If not or if it's expired, retrieve the feed and update the cache
	 * </p>
	 * 
	 * @param source
	 *        The url for the news feed
	 * @exception NewsConnectionException,
	 *            for errors making the connection.
	 * @exception NewsFormatException,
	 *            for errors in the URL or errors parsing the feed.
	 */
	protected void updateChannel(String source) throws NewsConnectionException, NewsFormatException
	{
		// if channel is expired or not in cache, attempt to update
		// synchronize this part??? %%%%%%
		if (!m_storage.containsKey(source))
		{
			m_storage.put(source, new BasicNewsChannel(source), DEFAULT_EXPIRATION);

		}

	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * NewsItem implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	public class BasicNewsItem implements NewsItem
	{
		/** The title of this NewsItem. */
		protected String m_title = null;

		/** The URL for the complete story. */
		protected String m_link = null;

		/** The publication date of the NewsItem. */
		protected String m_pubdate = null;

		/** The description (or body) of the news item */
		protected String m_description = null;

		/**
		 * Construct.
		 * 
		 * @param title
		 *        The headline of the item
		 * @param description
		 *        The body of the item
		 * @param link
		 *        The URL for a longer version of the item
		 * @param pubdate
		 *        The date/time at which the item was published
		 */
		public BasicNewsItem(String title, String description, String link, String pubdate)
		{
			m_title = title;
			m_description = description;
			m_link = link;
			m_pubdate = pubdate;

		} // BasicNewsItem

		/**
		 * Access the title of the NewsItem.
		 * 
		 * @return The title of the NewsItem.
		 */
		public String getTitle()
		{
			return m_title;

		} // getTitle

		/**
		 * Access the time when the NewsItem was updated.
		 * 
		 * @return The time when the NewsItem was updated.
		 */
		public String getPubdate()
		{
			return m_pubdate;

		} // getPubdate

		/**
		 * Access the URL where the complete story can be found.
		 * 
		 * @return The URL where the complete story can be found.
		 */
		public String getLink()
		{
			return m_link;

		} // getLink

		/**
		 * Access the description (or body) of the NewsItem.
		 * 
		 * @return The description (or body) of the NewsItem.
		 */
		public String getDescription()
		{
			return m_description;
		}

		/**
		 * Set the title of the NewsItem.
		 * 
		 * @param title
		 *        The title of the NewsItem.
		 */
		public void setTitle(String title)
		{
			m_title = title;

		} // setTitle

		/**
		 * Set the time when the NewsItem was updated.
		 * 
		 * @param pubdate
		 *        The time when the NewsItem was updated.
		 */
		public void setPubdate(String pubdate)
		{
			m_pubdate = pubdate;

		} // setPubdate

		/**
		 * Set the URL where the complete story can be found.
		 * 
		 * @param link
		 *        The URL where the complete story can be found.
		 */
		public void setLink(String link)
		{
			m_link = link;

		} // setLink

		/**
		 * Set the description (or body) of the NewsItem.
		 * 
		 * @param description
		 *        The description (or body) of the NewsItem.
		 */
		public void setDescription(String description)
		{
			m_description = description;
		}

	} // class BasicNewsItemHeader

	/**********************************************************************************************************************************************************************************************************************************************************
	 * NewsChannel implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	public class BasicNewsChannel implements NewsChannel
	{
		protected String m_source = null;

		protected String m_link = null;

		protected String m_title = null;

		protected String m_description = null;

		protected String m_language = null;

		protected String m_copyright = null;

		protected String m_pubdate = null;

		protected String m_lastbuilddate = null;

		protected String m_imageLink = null;

		protected String m_imageTitle = null;

		protected String m_imageUrl = null;

		protected String m_imageHeight = "31";

		protected String m_imageWidth = "88";

		protected String m_imageDescription = null;

		protected List m_items = null;

		/**
		 * Construct.
		 * 
		 * @param source
		 *        The URL from which the feed can be obtained
		 * @exception NewsConnectionException,
		 *            for errors making the connection.
		 * @exception NewsFormatException,
		 *            for errors in the URL or errors parsing the feed.
		 */
		public BasicNewsChannel(String source) throws NewsConnectionException, NewsFormatException
		{
			if (m_items == null)
			{
				m_items = new Vector();
			}

			// get the file, parse it and cache it
			// throw NewsConnectionException if unable to get file
			// throw NewsFormatException if file is in wrong format
			initChannel(source);
		}

		// from org.apache.jetspeed.portal.portlets.NewRSSPortlet
		private Node getNode(Node start, String name)
		{
			NodeList list = start.getChildNodes();

			for (int i = 0; i < list.getLength(); ++i)
			{

				Node node = list.item(i);

				if (node.getNodeName().equals(name))
				{
					return node;
				}
			}
			return null;
		}

		private String getNodeValue(Node start, String nodename, String defval)
		{
			Node thenode = getNode(start, nodename);
			String rv = defval;
			if (thenode != null)
			{
				try
				{
					Node nugget = thenode.getFirstChild();
					if (nugget != null)
					{
						rv = nugget.getNodeValue();
					}
				}
				catch (Exception ignore)
				{
				}
			}
			return rv;
		}

		// get the file, parse it and cache it
		// throw NewsConnectionException if unable to get file
		// throw NewsFormatException if file is in wrong format
		private void initChannel(String source) throws NewsConnectionException, NewsFormatException
		{
			Document document = null;
			try
			{
				// parse the content, save header info and add items to m_items
				URL url = new URL(source);
				DocumentBuilderFactory docfactory = DocumentBuilderFactory.newInstance();
				docfactory.setValidating(false);
				DocumentBuilder parser = docfactory.newDocumentBuilder();
				InputSource insource = new InputSource(url.toExternalForm());
				insource.setEncoding("UTF-8");

				// if insource is not XML, parser may print error message to console
				// before throwing exception -- Needs to be fixed %%%%%%%%%
				document = parser.parse(insource);
			}
			catch (MalformedURLException e)
			{
				if (m_logger.isDebugEnabled()) m_logger.debug(this + ".initChannel(" + source + ") bad url: " + e.getMessage());
				throw new NewsFormatException("\"" + source + "\" is not a complete and valid URL.");
			}
			catch (IOException e)
			{
				if (m_logger.isDebugEnabled())
						m_logger.debug(this + ".initChannel(" + source + ") constructor: couldn't connect: " + e.getMessage());
				throw new NewsConnectionException("Unable to obtain news feed from " + source);
			}
			catch (Exception e)
			{
				if (m_logger.isDebugEnabled())
						m_logger.debug(this + ".initChannel(" + source + ") constructor: couldn't parse: " + e.getMessage());
				throw new NewsConnectionException("Unable to interpret news feed from " + source);
			}

			if (document.equals(null))
			{
				if (m_logger.isDebugEnabled()) m_logger.debug(this + ".initChannel(" + source + ") constructor: null document");
				throw new NewsFormatException("File received from \"" + source + "\" is not in RSS format");
			}

			Node channel = null;

			NodeList list = document.getElementsByTagName("channel");

			if (list.getLength() != 1)
			{
				throw new NewsFormatException("File received from " + source + " is not in RSS format");
			}

			channel = list.item(0);

			Node titlenode = getNode(channel, "title");

			if (titlenode == null)
			{
				throw new NewsFormatException("File received from " + source + " is not in RSS format");
			}
			else
			{
				m_title = titlenode.getFirstChild().getNodeValue();
			}

			m_source = source;
			m_description = getNodeValue(channel, "description", null);
			m_description = Validator.stripAllNewlines(m_description);

			m_lastbuilddate = getNodeValue(channel, "lastBuildDate", null);
			m_lastbuilddate = Validator.stripAllNewlines(m_lastbuilddate);

			m_pubdate = getNodeValue(channel, "pubDate", null);
			m_pubdate = Validator.stripAllNewlines(m_pubdate);

			m_copyright = getNodeValue(channel, "copyright", null);
			m_copyright = Validator.stripAllNewlines(m_copyright);

			m_language = getNodeValue(channel, "language", null);
			m_language = Validator.stripAllNewlines(m_language);

			m_link = getNodeValue(channel, "link", null);
			m_link = Validator.stripAllNewlines(m_link);

			// image
			NodeList imageList = document.getElementsByTagName("image");
			Node image = null;

			if (imageList.getLength() == 1)
			{
				image = imageList.item(0);

				m_imageLink = getNodeValue(image, "link", null);
				m_imageLink = Validator.stripAllNewlines(m_imageLink);

				m_imageTitle = getNodeValue(image, "title", null);
				m_imageTitle = Validator.stripAllNewlines(m_imageTitle);

				m_imageUrl = getNodeValue(image, "url", null);
				m_imageUrl = Validator.stripAllNewlines(m_imageUrl);

				m_imageHeight = getNodeValue(image, "height", null);
				m_imageHeight = Validator.stripAllNewlines(m_imageHeight);

				m_imageWidth = getNodeValue(image, "width", null);
				m_imageWidth = Validator.stripAllNewlines(m_imageWidth);

				m_imageDescription = getNodeValue(image, "description", null);
				m_imageDescription = Validator.stripAllNewlines(m_imageDescription);
			}

			// docs??
			// others??
			m_items = new Vector();

			NodeList items = document.getElementsByTagName("item");

			for (int i = 0; i < items.getLength(); ++i)
			{
				String iTitle = getNodeValue(items.item(i), "title", null);
				iTitle = Validator.stripAllNewlines(iTitle);

				String iDescription = getNodeValue(items.item(i), "description", null);
				iDescription = Validator.stripAllNewlines(iDescription);

				String iLink = getNodeValue(items.item(i), "link", null);
				iLink = Validator.stripAllNewlines(iLink);

				String iPubDate = getNodeValue(items.item(i), "pubDate", null);
				iPubDate = Validator.stripAllNewlines(iPubDate);

				m_items.add(new BasicNewsItem(iTitle, iDescription, iLink, iPubDate));
			}

		} // initChannel

		/**
		 * A .
		 * 
		 * @return the NewsItem that has the specified id.
		 */
		public List getNewsitems()
		{
			List rv = new Vector();
			rv.addAll(m_items);

			return rv;

		} // getNewsitems

		/**
		 * A .
		 * 
		 * @param filter
		 *        A filtering object to accept messages, or null if no filtering is desired.
		 * @return a list of NewsItems objects (may be empty).
		 */
		public List getNewsitems(Filter filter)
		{
			List items = new Vector(m_items);
			if (filter != null)
			{
				List accepted = new Vector();
				Iterator it = items.iterator();
				while (it.hasNext())
				{
					NewsItem item = (NewsItem) it.next();
					if (filter.accept(item))
					{
						accepted.add(item);
					}
				}
				items = accepted;
			}
			return items;
		}

		public String getSource()
		{
			return m_source;
		}

		public String getLink()
		{
			return m_link;
		}

		public String getTitle()
		{
			return m_title;
		}

		public String getDescription()
		{
			return m_description;
		}

		public String getLanguage()
		{
			return m_language;
		}

		public String getCopyright()
		{
			return m_copyright;
		}

		public String getPubdate()
		{
			return m_pubdate;
		}

		public String getLastbuilddate()
		{
			return m_lastbuilddate;
		}

		public String getImageUrl()
		{
			return m_imageUrl;
		}

		public String getImageTitle()
		{
			return m_imageTitle;
		}

		public String getImageLink()
		{
			return m_imageLink;
		}

		public String getImageWidth()
		{
			return m_imageWidth;
		}

		public String getImageHeight()
		{
			return m_imageHeight;
		}

		public String getImageDescription()
		{
			return m_imageDescription;
		}

		public void setNewsitems(List items)
		{
			m_items = new Vector(items);
		}

		public void addNewsitem(NewsItem item)
		{
			m_items.add(item);
		}

		public void setSource(String source) throws NewsConnectionException, NewsFormatException
		{
			m_source = source;
			initChannel(source);
		}

		public void setLink(String link)
		{
			m_link = link;
		}

		public void setTitle(String title)
		{
			m_title = title;
		}

		public void setDescription(String description)
		{
			m_description = description;
		}

		public void setLanguage(String language)
		{
			m_language = language;
		}

		public void setCopyright(String copyright)
		{
			m_copyright = copyright;
		}

		public void setPubdate(String pubdate)
		{
			m_pubdate = pubdate;
		}

		public void setLastbuilddate(String builddate)
		{
			m_lastbuilddate = builddate;
		}

		public void SetImageUrl(String imageUrl)
		{
			m_imageUrl = imageUrl;
		}

		public void setImageTitle(String imageTitle)
		{
			m_imageTitle = imageTitle;
		}

		public void setImageLink(String imageLink)
		{
			m_imageLink = imageLink;
		}

		public void setImageWidth(String imageWidth)
		{
			m_imageWidth = imageWidth;
		}

		public void setImageHeight(String imageHeight)
		{
			m_imageHeight = imageHeight;
		}

		public void setImageDescription(String imageDescription)
		{
			m_imageDescription = imageDescription;
		}

		/**
		 * Checks whether an update is available for the rss news feed.
		 * 
		 * @return true if update is available, false otherwise
		 */
		public boolean isUpdateAvailable()
		{
			// %%%%%%%%
			return true;
		}

		/**
		 * Checks the relative ordering of the String url's of two Channels. Same response pattern as compareTo method for Strings--negative if "this" object is greater than parameter, zero if the objects are equal, and positive if "this" object is less
		 * than the parameter. The parameter can be a String reference or a NewsChannel object (otherwise method throws ClassCastException).
		 * 
		 * @return A negative integer if "this" object is greater than parameter, zero if the objects are equal, and a positive integer if "this" object is less than the parameter
		 */
		public int compareTo(Object obj) throws ClassCastException
		{
			int rv = 0;
			if (m_source == null)
			{
				if (obj != null)
				{
					rv = -1;
				}
			}
			else if (obj == null)
			{
				rv = 1;
			}
			else if (obj instanceof String)
			{
				rv = m_source.compareTo((String) obj);
			}
			else
			{
				NewsChannel other = (NewsChannel) obj;
				rv = m_source.compareTo(other.getLink());
			}
			return rv;
		}

		/**
		 * Checks whether the parameter obj refers to the same channel as "this" channel. The parameter can be a String URL or a NewsChannel object (otherwise method throws ClassCastException).
		 * 
		 * @return true if the channels are the same, false otherwise
		 */
		public boolean equals(Object obj) throws ClassCastException
		{
			return (compareTo(obj) == 0);
		}

		/**
		 * Calculates a hash code for the channel object's URL.
		 * 
		 * @return The hash-code for the String URL to the channel.
		 */
		public int hashCode()
		{
			String hval = "";
			if (m_source != null)
			{
				hval = m_source;
			}

			return hval.hashCode();
		}

	} // BasicNewsChannel

	/**********************************************************************************************************************************************************************************************************************************************************
	 * NewsService implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Retrieves a list of items from an rss feed.
	 * 
	 * @param source
	 *        The url for the feed.
	 * @return A list of NewsItem objects retrieved from the feed.
	 * @exception NewsConnectionException,
	 *            for errors making the connection.
	 * @exception NewsFormatException,
	 *            for errors in the URL or errors parsing the feed.
	 */
	public List getNewsitems(String source) throws NewsConnectionException, NewsFormatException
	{
		// if channel is expired or not in cache, attempt to update
		updateChannel(source);

		// return a list of items from the channel
		Object obj = m_storage.get(source);
		NewsChannel ch = (NewsChannel) obj;
		List items = ch.getNewsitems();
		return items;
	}

	/**
	 * Retrieves a list of items from an rss feed.
	 * 
	 * @param source
	 *        The url for the feed.
	 * @param filter
	 *        A filtering object to accept NewsItems, or null if no filtering is desired.
	 * @return A list of NewsItem objects retrieved from the feed.
	 * @exception NewsConnectionException,
	 *            for errors making the connection.
	 * @exception NewsFormatException,
	 *            for errors in the URL or errors parsing the feed.
	 */
	public List getNewsitems(String source, Filter filter) throws NewsConnectionException, NewsFormatException
	{
		// if channel is expired or not in cache, attempt to update
		updateChannel(source);

		// 
		List items = ((NewsChannel) m_storage.get(source)).getNewsitems(filter);
		return items;

	} // getNewsitems

	/**
	 * Checks whether an update is available for the rss news feed.
	 * 
	 * @param source
	 *        The url for the feed.
	 * @return true if update is available, false otherwise
	 * @exception NewsConnectionException,
	 *            for errors making the connection.
	 * @exception NewsFormatException,
	 *            for errors in the URL or errors parsing the feed.
	 */
	public boolean isUpdateAvailable(String source)
	{
		// %%%%%
		return true;
	}

	/**
	 * Retrieves a list of URLs for NewsChannel objects currently indexed by the service.
	 * 
	 * @return A list of NewsChannel objects (possibly empty).
	 */
	public List getChannels()
	{
		return m_storage.getKeys();
	}

	/**
	 * Retrieves a NewsChannel object indexed by a URL.
	 * 
	 * @param source
	 *        The url for the channel.
	 * @return A NewsChannel object (possibly null).
	 * @exception NewsConnectionException,
	 *            for errors making the connection.
	 * @exception NewsFormatException,
	 *            for errors in the URL or errors parsing the feed.
	 */
	public NewsChannel getChannel(String source) throws NewsConnectionException, NewsFormatException
	{
		updateChannel(source);
		return (NewsChannel) m_storage.get(source);
	}

	/**
	 * Removes a NewsChannel object from the service.
	 * 
	 * @param source
	 *        The url for the channel.
	 */
	public void removeChannel(String source)
	{
		m_storage.remove(source);
	}

} // BasicNewsService

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/news/BasicNewsService.java,v 1.1 2005/04/13 03:16:51 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/