/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/calendar/CalendarEventEdit.java,v 1.6 2004/07/06 17:44:37 janderse.umich.edu Exp $
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
package org.sakaiproject.service.legacy.calendar;

// import
import org.sakaiproject.service.legacy.resource.AttachmentContainerEdit;
import org.sakaiproject.service.legacy.resource.Edit;
import org.sakaiproject.service.legacy.time.TimeRange;

/**
* <p>CalendarEventEdit is an editable CalendarEvent</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.6 $
*/
public interface CalendarEventEdit
	extends CalendarEvent, Edit, AttachmentContainerEdit
{
	/**
	* Replace the time range
	* @param The new event time range
	*/
	public void setRange(TimeRange range);

	/**
	* Set the display name property (cover for PROP_DISPLAY_NAME).
	* @param name The event's display name property.
	*/
	public void setDisplayName(String name);

	/**
	* Set the description property as plain text (cover for PROP_DESCRIPTION).
	* @param description The event's description property.
	*/
	public void setDescription(String description);
	
	/**
	* Set the description property as formatted text (cover for PROP_DESCRIPTION).
	* @param description The event's description property.
	*/
	public void setDescriptionFormatted(String description);
	
	/**
	* Set the type (cover for PROP_CALENDAR_TYPE).
	* @param type The event's type property.
	*/
	public void setType(String type);
	
	/**
	* Set the location (cover for PROP_CALENDAR_LOCATION).
	* @param location The event's location property.
	*/
	public void setLocation(String location);

	/**
	* Set the value of an "extra" event field.
	* @param name The "extra" field name
	* @param value The value to set, or null to remove the field.
	*/
	public void setField(String name, String value);

	/**
	* Sets the recurrence rule.
	* @param rule The recurrence rule, or null to clear out the rule.
	*/
	public void setRecurrenceRule(RecurrenceRule rule);

}	// CalendarEventEdit

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/calendar/CalendarEventEdit.java,v 1.6 2004/07/06 17:44:37 janderse.umich.edu Exp $
*
**********************************************************************************/
