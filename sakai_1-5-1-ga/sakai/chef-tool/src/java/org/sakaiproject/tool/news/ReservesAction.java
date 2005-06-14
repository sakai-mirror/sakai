/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/news/ReservesAction.java,v 1.3 2005/02/11 21:21:59 janderse.umich.edu Exp $
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
package org.sakaiproject.tool.news;

// imports
import java.net.URL;

import org.sakaiproject.cheftool.JetspeedRunData;
import org.sakaiproject.cheftool.PortletConfig;
import org.sakaiproject.cheftool.VelocityPortlet;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.legacy.news.cover.NewsService;
import org.sakaiproject.service.legacy.realm.Realm;
import org.sakaiproject.service.legacy.realm.cover.RealmService;
import org.sakaiproject.service.legacy.resource.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.site.SiteEdit;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.site.ToolConfigurationEdit;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.util.StringUtil;

/**
* <p>ReservesAction is the rss news tool used for UM library reserves.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.3 $
*/
public class ReservesAction
	extends NewsAction
{
	private static final String CHANNEL_URL_CHECKED = "channel_url_checked";
	
	/**
	* Populate the state object, if needed.
	*/
	protected void initState(SessionState state, VelocityPortlet portlet, JetspeedRunData data)
	{
		super.initState(state, portlet, data);
		
		PortletConfig config = portlet.getPortletConfig();
		
		try
		{
			ToolConfiguration tool = (ToolConfiguration) data.getRequest().getAttribute(ATTR_TOOL);
			
			if (StringUtil.trimToNull(tool.getProperties().getProperty(CHANNEL_URL_CHECKED)) == null)
			{
				//set the Library Reserves tool url when first launched
				String siteId = tool.getSiteId();
				
				try
				{
					// get a lock on the site
					SiteEdit site = SiteService.editSite(siteId);
					
					// get this tool's configuration
					ToolConfigurationEdit toolEdit = site.getToolEdit(tool.getId());
					ResourcePropertiesEdit rpe = toolEdit.getPropertiesEdit();
					
					try
					{
						Realm r = RealmService.getRealm(SiteService.siteReference(siteId));
						String providerId = r.getProviderRealmId();
						
						String courseReserveUrl = StringUtil.trimToNull(config.getInitParameter(PARAM_CHANNEL_URL));
						
						String urlString = getCourseReserveUrl(courseReserveUrl, providerId);
						if (urlString != null)
						{
							try 
							{
								URL url = new URL(urlString);
								Object content = url.getContent();
								NewsService.getChannel(url.toExternalForm());
								
								state.setAttribute(STATE_CHANNEL_URL, url.toExternalForm());
								
								// update the tool config
								rpe.addProperty("channel-url", url.toExternalForm());
							}
							catch(Exception e)
							{
								// display message
								addAlert(state, "Cannot connect or obtain a file specified by the Library Reserves source URL " + urlString + ". ");
							}
						}
					}
					catch (Exception e)
					{
						Log.warn("chef", this + " Cannot get site realm " + siteId);
					}
					
					rpe.addProperty(CHANNEL_URL_CHECKED, Boolean.TRUE.toString());
					SiteService.commitEdit(site);
				}
				catch (Exception ee)
				{
					Log.warn("chef", this + " Cannot get site edit for " + siteId);
				}
			}	// if
		}
		catch (Exception any)
		{
			
		}
		
	}	// initState
	
	/**
	* getCourseReserve gets a Course Reserve URL based on section
	*
	*/
	private String getCourseReserveUrl(String courseReserveUrl, String id)
	{
		String url = null;
		
		String[] courses;
		String[] fields;
		//String course = NULL_STRING;
		String sections = null;
		//String[] sect;
		String firstCourse = "";
		String firstCatalogNbr = null;
		String firstSection = null;
		String firstSubject = null;
		String firstSemester = null;
		String firstYear = null;
		try
		{
			if (id.indexOf("+") != -1) 
			{
				// This is a crosslisted course; tab is first course and its section(s)
				courses = id.split("\\+");
				firstCourse = courses[0]; // Get the first course
				if (firstCourse.indexOf("[") != -1) 
				{
					// Case 1: A cross-listed course with multiple sections; tab is first course and its sections
					sections = firstCourse.substring(firstCourse.indexOf("[")+1, firstCourse.indexOf("]"));
					fields = firstCourse.split(",");
					firstYear = fields[0];
					firstSemester = fields[1];
					firstSubject = fields[3]; //Subject
					firstCatalogNbr = fields[4]; //Catalog number
					firstSection = (sections.indexOf(",")!=-1)?sections.substring(0, sections.indexOf(",")):sections; //Sections
				}
				else
				{
					// Case 2: A cross-listed course with one section; tab is first course and its section
					fields = firstCourse.split(",");
					firstYear = fields[0];
					firstSemester = fields[1];
					firstSubject = fields[3]; //Subject
					firstCatalogNbr = fields[4]; //Catalog number
					firstSection = fields[5]; //Section
				}
			}
			else if (id.indexOf("[") != -1) 
			{
				// Case 3: A single course with multiple sections; tab is course and sections
				firstCourse = id.substring(0,id.indexOf("[")-1);
				fields = firstCourse.split(",");
				firstYear = fields[0];
				firstSemester = fields[1];
				firstSubject = fields[3]; //Subject
				firstCatalogNbr = fields[4]; //Catalog number
				sections = id.substring(id.indexOf("[")+1, id.indexOf("]"));
				firstSection = (sections.indexOf(",")!=-1)?sections.substring(0, sections.indexOf(",")):sections; //Section
			}
			else 
			{
				// Case 4: A single course with a single section; tab is course and section
				fields = id.split(",");
				firstYear = fields[0];
				firstSemester = fields[1];
				firstSubject = fields[3]; //Subject
				firstCatalogNbr = fields[4]; //Catalog number
				firstSection = fields[5]; //Section
			}
		}
		catch (Exception e)
		{
			// if there is a problem, create a generic tab
			Log.warn("chef", "SiteAction.getCourseTab Exception " + e.getMessage() + " " + id);
		}
		
		if (courseReserveUrl != null)
		{
			if(	firstCatalogNbr != null 
				&& firstSection != null
				&& firstSubject != null
				&& firstSemester != null
				&& firstYear != null)
			{
				//e.g., http://www.lib.umich.edu/r/rss-reserves/rss.php?catalog_nbr=468&section=001&semester=2&subject=JUDAIC&year=2004
				url = courseReserveUrl + "catalog_nbr=" + firstCatalogNbr + 
					"&section=" + firstSection + "&semester=" + firstSemester + 
					"&subject=" + firstSubject + "&year=" + firstYear;
			}
			else
			{
				// splash page for Course Reserve
				url = courseReserveUrl + "catalog_nbr=&section=&semester=&subject=&year=";
			}
		}
		
		return url;
	}//getCourseReserveUrl
	
}	// class ReservesAction

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/news/ReservesAction.java,v 1.3 2005/02/11 21:21:59 janderse.umich.edu Exp $
*
**********************************************************************************/
