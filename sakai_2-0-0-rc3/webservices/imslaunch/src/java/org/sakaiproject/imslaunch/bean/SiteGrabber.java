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
