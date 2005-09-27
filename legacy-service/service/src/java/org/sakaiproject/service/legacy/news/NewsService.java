/**********************************************************************************
* $URL$
* $Id$
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
package org.sakaiproject.service.legacy.news;

// import
import java.util.List;

import org.sakaiproject.service.legacy.resource.Entity;
import org.sakaiproject.util.Filter;

/**
* <p>NewsService is the interface for retrieving and caching news items from a rss news feed.</p>
* <p></p>
* <p></p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public interface NewsService
{
	/** This string can be used to find the service in the service manager. */
	public static final String SERVICE_NAME = NewsService.class.getName();

	/** This string starts the references to resources in this service. */
	public static final String REFERENCE_ROOT = Entity.SEPARATOR + "news";
	
	/** 
	* Retrieves a list of rss feeds that are being used.
	* @return A list of NewsChannel objects (possibly empty).
	*/
	public List getChannels();
	
	/** 
	* Retrieves a NewsChannel object indexed by a URL.
	* @param source The url for the channel.
	* @return A NewsChannel object (possibly null).
	* @throws NewsConnectionException
	* @throws NewsFormatException
	*/
	public NewsChannel getChannel(String source)
		throws NewsConnectionException, NewsFormatException;
	

	/** 
	* Retrieves a list of rss feeds that are being used.
	* @return A list of NewsChannel objects (possibly empty).
	* @throws ? if param channel is not a valid url.
	*/
	public void removeChannel(String channel);

	/** 
	* Retrieves a list of items from an rss feed.
	* @param channel The url for the feed.
	* @return A list of NewsItem objects retrieved from the feed.
	* @throws ? if param feed is not a valid url.
	*/
	public List getNewsitems(String channel)
		throws NewsConnectionException, NewsFormatException;

	/** 
	* Retrieves a list of items from an rss feed.
	* @param channel The url for the feed.
	* @param filter A filtering object to accept NewsItems, or null if no filtering is desired.
	* @return A list of NewsItem objects retrieved from the feed.
	* @throws ? if param feed is not a valid url.
	*/
	public List getNewsitems(String channel, Filter filter)
		throws NewsConnectionException, NewsFormatException;

	/**
	* Checks whether an update is available for the rss news feed.
	* @param feed The url for the feed.
	* @return true if update is available, false otherwise
	* @throws ? if param feed is not a valid url.
	*/
	public boolean isUpdateAvailable(String channel);

}	// interface NewsService



