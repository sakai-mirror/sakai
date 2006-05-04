package org.sakaiproject.imslaunch.sitegrabber;

public class SiteLocator 
{
	
	public SiteLocator() 
	{
		// constructor
	}
	
	public String URLTest (int siteId) 
	{
		String retVal = "";
		
		// Get site URL from db (some day)				
		if (siteId == 1) {
			retVal = "http://www.amazon.com";
		}
		
		if (siteId == 2) {
			retVal = "http://www.ebay.com";
		}
		
		if (siteId == 3) {
			retVal = "http://www.google.com";			
		}
		
		if (siteId == 4) {
			retVal = "http://www.nybooks.com";			
		}
		
		if (siteId == 5) {
			retVal = "http://java.sun.com";
		}
		
		return retVal;
	}
}
