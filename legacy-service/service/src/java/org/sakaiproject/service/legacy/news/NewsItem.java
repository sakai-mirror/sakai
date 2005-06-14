/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/news/NewsItem.java,v 1.1 2005/05/12 15:45:37 ggolden.umich.edu Exp $
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
package org.sakaiproject.service.legacy.news;

// import

/**
* <p>NewsItem is the Interface for a CHEF News message.</p>
* <p>The news message has header fields (from, date) and a body (text).  Each
* message also has an id, unique within the channel.  All fields are read only.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
* @see org.chefproject.service.NewsService
* @see org.chefproject.core.NewsChannel
*/
public interface NewsItem
{
	/**
	* Access the title of the NewsItem.
	* @return The title of the NewsItem.
	*/
	public String getTitle();

	/**
	* Access the description (or body) of the NewsItem.
	* @return The description of the NewsItem.
	*/
	public String getDescription();

	/**
	* Access the time when the NewsItem was updated.
	* @return The time when the NewsItem was updated.
	*/
	public String getPubdate();

	/**
	* Access the URL where the complete story can be found.
	* @return The URL where the complete story can be found.
	*/
	public String getLink();

	/**
	* Set the title of the NewsItem.
	* @param title The title of the NewsItem.
	*/
	public void setTitle(String title);

	/**
	* Set the description of the NewsItem.
	* @param description The description of the NewsItem.
	*/
	public void setDescription(String description);

	/**
	* Set the time when the NewsItem was updated.
	* @param pubdate The time when the NewsItem was updated.
	*/
	public void setPubdate(String pubdate);

	/**
	* Set the URL where the complete story can be found.
	* @return link The URL where the complete story can be found.
	*/
	public void setLink(String link);

}	// NewsItem

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/news/NewsItem.java,v 1.1 2005/05/12 15:45:37 ggolden.umich.edu Exp $
*
**********************************************************************************/
