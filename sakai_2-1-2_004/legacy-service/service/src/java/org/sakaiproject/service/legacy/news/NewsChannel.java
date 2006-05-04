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

import org.sakaiproject.util.Filter;

/**
* <p>NewsChannel is the interface for a CHEF News service News channel. 
* Messages in the NewsChannel are NewsItems.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
* @see org.chefproject.service.NewsService
* @see org.chefproject.core.NewsItem
*/
public interface NewsChannel
{

	/**
	* Accesses a list of all news items from this rss news feed.
	* @return a list of NewsItem objects (may be empty).
	*/
	public List getNewsitems();

	/**
	* Accesses a filtered list of news items from this rss news feed.
	* @param filter A filtering object to accept messages, or null if no filtering is desired.
	* @return a list of NewsItem objects (may be empty).
	*/
	public List getNewsitems(Filter filter);

	/**
	*
	*/
	public String getLink();

	/**
	*
	*/
	public String getSource();

	/**
	*
	*/
	public String getTitle();

	/**
	*
	*/
	public String getDescription();

	/**
	*
	*/
    public String getLanguage();

	/**
	*
	*/
    public String getCopyright();

	/**
	*
	*/
    public String getPubdate();

	/**
	*
	*/
    public String getLastbuilddate();

	/**
	*
	*/
	public void setNewsitems(List items);

	/**
	*
	*/
	public void addNewsitem(NewsItem item);

	/**
	*
	*/
	public void setLink(String link);

	/**
	*
	*/
	public void setSource(String source)
		throws NewsConnectionException, NewsFormatException;

	/**
	*
	*/
	public void setTitle(String set);

	/**
	*
	*/
	public void setDescription(String description);

	/**
	*
	*/
    public void setLanguage(String language);

	/**
	*
	*/
    public void setCopyright(String copyright);

	/**
	*
	*/
    public void setPubdate(String pubdate);

	/**
	*
	*/
    public void setLastbuilddate(String builddate);

	/**
	* Checks whether an update is available for the rss news feed.
	* @return true if update is available, false otherwise
	*/
	public boolean isUpdateAvailable();
	
	/**
	* Checks the relative ordering of the String url's of two Channels.
	* Same response pattern as compareTo method for Strings--negative if "this" 
	* object is greater than parameter, zero if the objects are equal, and 
	* positive if "this" object is less than the parameter.
	* The parameter can be a String reference or a MessageChannel object (otherwise
	* method throws ClassCastException).
	* @return A negative integer if "this" object is greater than parameter, 
	* zero if the objects are equal, and a positive integer if "this" object
	* is less than the parameter
	*/
	public int compareTo(Object obj) throws ClassCastException;

	/**
	* Checks whether the parameter obj refers to the same channel as "this" channel.
	* The parameter can be a String URL or a NewsChannel object (otherwise
	* method throws ClassCastException).
	* @return true if the channels are the same, false otherwise
	*/
	public boolean equals(Object obj) throws ClassCastException;
	
	/**
	* Calculates a hash code for the channel object's URL.
	* @return The hash-code for the String URL to the channel.
	*/
	public int hashCode();

}	// NewsChannel



