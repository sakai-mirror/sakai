/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/calendar/Calendar.java,v 1.1 2005/05/12 15:45:35 ggolden.umich.edu Exp $
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
import java.util.List;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.legacy.resource.ReferenceVector;
import org.sakaiproject.service.legacy.resource.Resource;
import org.sakaiproject.util.Filter;
import org.sakaiproject.service.legacy.time.TimeRange;
import org.w3c.dom.Element;

/**
* <p>Calendar is the base interface for CHEF Calendar service calendars.</p>
* <p>Calendars contains collections of CalendarEvents.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public interface Calendar
	extends Resource
{
	/**
	* Access the context of the resource.
	* @return The context.
	*/
	public String getContext();

	/**
	* check permissions for getEvents() and getEvent().
	* @return true if the user is allowed to get events from the calendar, false if not.
	*/
	public boolean allowGetEvents();

	/**
	* Return a List of all or filtered events in the calendar.
	* The order in which the events will be found in the iteration is by event start date.
	* @param range A time range to limit the iterated events.  May be null; all events will be returned.
	* @param filter A filtering object to accept events into the iterator, or null if no filtering is desired.
	* @return a List of all or filtered CalendarEvents in the calendar (may be empty).
	* @exception PermissionException if the user does not have read permission to the calendar.
	*/
	public List getEvents(TimeRange range, Filter filter)
		throws PermissionException;

	/**
	* Return a specific calendar event, as specified by event name.
	* @param eventId The id of the event to get.
	* @return the CalendarEvent that has the specified id.
	* @exception IdUnusedException If this id is not a defined event in this calendar.
	* @exception PermissionException If the user does not have any permissions to read the calendar.
	*/
	public CalendarEvent getEvent(String eventId)
		throws IdUnusedException, PermissionException;

	/**
	* Return the extra fields kept for each event in this calendar.
	* @return the extra fields kept for each event in this calendar, formatted into a single string. %%%
	*/
	public String getEventFields();

	/**
	* check permissions for addEvent().
	* @return true if the user is allowed to addEvent(...), false if not.
	*/
	public boolean allowAddEvent();

	/**
	* Add a new event to this calendar.
	* @param range The event's time range.
	* @param displayName The event's display name (PROP_DISPLAY_NAME) property value.
	* @param description The event's description (PROP_DESCRIPTION) property value.
	* @param type The event's calendar event type (PROP_CALENDAR_TYPE) property value.
	* @param location The event's calendar event location (PROP_CALENDAR_LOCATION) property value.
	* @param attachments The event attachments, a vector of Reference objects.
	* @return The newly added event.
	* @exception PermissionException If the user does not have permission to modify the calendar.
	*/
	public CalendarEvent addEvent(TimeRange range, String displayName, String description,
									String type, String location, ReferenceVector attachments)
		throws PermissionException;

	/**
	* Add a new event to this calendar.
	* Must commitEvent() to make official, or cancelEvent() when done!
	* @return The newly added event, locked for update.
	* @exception PermissionException If the user does not have write permission to the calendar.
	*/
	public CalendarEventEdit addEvent()
		throws PermissionException;

	/**
	* check permissions for editEvent()
	* @param id The event id.
	* @return true if the user is allowed to update the event, false if not.
	*/
	public boolean allowEditEvent(String eventId);

	/**
	* Return a specific calendar event, as specified by event name, locked for update.
	* Must commitEvent() to make official, or cancelEvent(), or removeEvent() when done!
	* @param eventId The id of the event to get.
	* @return the Event that has the specified id.
	* @exception IdUnusedException If this name is not a defined event in this calendar.
	* @exception PermissionException If the user does not have any permissions to edit the event.
	* @exception InUseException if the event is locked for edit by someone else.
	*/
	public CalendarEventEdit editEvent(String eventId)
		throws IdUnusedException, PermissionException, InUseException;

	/**
	* Commit the changes made to a CalendarEventEdit object, and release the lock.
	* The CalendarEventEdit is disabled, and not to be used after this call.
	* @param edit The CalendarEventEdit object to commit.
	* @param intention The recurring event modification intention,
	* based on values in the GenericCalendarService "MOD_*",
	* used if the event is part of a recurring event sequence to determine how much of the sequence is changed by this commmit.
	*/
	public void commitEvent(CalendarEventEdit edit, int intention);

	/**
	* Commit the changes made to a CalendarEventEdit object, and release the lock.
	* The CalendarEventEdit is disabled, and not to be used after this call.
	* Note: if the event is a recurring event, the entire sequence is modified by this commit (MOD_ALL).
	* @param edit The CalendarEventEdit object to commit.
	*/
	public void commitEvent(CalendarEventEdit edit);

	/**
	* Cancel the changes made to a CalendarEventEdit object, and release the lock.
	* The CalendarEventEdit is disabled, and not to be used after this call.
	* @param edit The CalendarEventEdit object to commit.
	*/
	public void cancelEvent(CalendarEventEdit edit);

	/**
	* Merge in a new event as defined in the xml.
	* @param el The event information in XML in a DOM element.
	* @exception PermissionException If the user does not have write permission to the calendar.
	* @exception IdUsedException if the user id is already used.
	*/
	public CalendarEventEdit mergeEvent(Element el)
		throws PermissionException, IdUsedException;

	/**
	* check permissions for removeEvent().
	* @param event The event from this calendar to remove.
	* @return true if the user is allowed to removeEvent(event), false if not.
	*/
	public boolean allowRemoveEvent(CalendarEvent event);

	/**
	* Remove an event from the calendar, one locked for edit.
	* @param edit The event from this calendar to remove.
	* @param intention The recurring event modification intention,
	* based on values in the GenericCalendarService "MOD_*",
	* used if the event is part of a recurring event sequence to determine how much of the sequence is removed.
	*/
	public void removeEvent(CalendarEventEdit edit, int intention);

	/**
	* Remove an event from the calendar, one locked for edit.
	* Note: if the event is a recurring event, the entire sequence is removed by this commit (MOD_ALL).
	* @param edit The event from this calendar to remove.
	*/
	public void removeEvent(CalendarEventEdit edit);

}	// Calendar

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/calendar/Calendar.java,v 1.1 2005/05/12 15:45:35 ggolden.umich.edu Exp $
*
**********************************************************************************/
