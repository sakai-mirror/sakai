/***********************************************************************************
 *
 * $Header: $
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

package org.sakaiproject.imslaunch.bean;

import javax.faces.model.SelectItem;

import org.sakaiproject.imslaunch.sitegrabber.SiteLocator;
import org.sakaiproject.imslaunch.util.Util;

public class SiteGrabber 
{
	private int siteId;
	private String site;
	private static SelectItem[] siteItems = new SelectItem[] 
	{
	 	new SelectItem(new Integer(0), "Please Select"),
	 	new SelectItem(new Integer(1), "Amazon"),
	   	new SelectItem(new Integer(2), "e-Bay"),
		new SelectItem(new Integer(3), "Google"), 
		new SelectItem(new Integer(4), "New York Review of Books"), 
		new SelectItem(new Integer(5), "Sun's Java site")
	};
	
	public SiteGrabber() 
	{
		// constructor
		siteId = 0;
		//site = "blank.html";
	}
	
	public int getSiteId()
	{
		return siteId;
	}
	
	public void setSiteId(int val)
	{
		this.siteId = val;
	}
	
	public String getSite()
	{
		return site;
	}
	
	public void setSite(String val)
	{
		this.site = val;
	}
	
	public SelectItem[] getSiteItems() 
	{
      return siteItems;
	}
  
	// Display selected dropdown option
	public String getSiteIdSelected() 
	{
		return Util.getOptionLabel(siteItems, siteId);  
	}
		
	public void SiteURL()
	{	
		SiteLocator loc = new SiteLocator();
		setSite(loc.URLTest(siteId)); // debug
	}
}
