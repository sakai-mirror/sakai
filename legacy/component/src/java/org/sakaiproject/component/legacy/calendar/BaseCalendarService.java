/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/calendar/BaseCalendarService.java,v 1.3 2005/05/12 01:38:27 ggolden.umich.edu Exp $
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
package org.sakaiproject.component.legacy.calendar;

// import
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.current.cover.CurrentService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.memory.Cache;
import org.sakaiproject.service.framework.memory.CacheRefresher;
import org.sakaiproject.service.framework.memory.MemoryService;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.framework.session.SessionStateBindingListener;
import org.sakaiproject.service.legacy.calendar.Calendar;
import org.sakaiproject.service.legacy.calendar.CalendarEdit;
import org.sakaiproject.service.legacy.calendar.CalendarEvent;
import org.sakaiproject.service.legacy.calendar.CalendarEventEdit;
import org.sakaiproject.service.legacy.calendar.CalendarEventVector;
import org.sakaiproject.service.legacy.calendar.CalendarService;
import org.sakaiproject.service.legacy.calendar.RecurrenceRule;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.event.cover.EventTrackingService;
import org.sakaiproject.service.legacy.id.IdService;
import org.sakaiproject.service.legacy.realm.cover.RealmService;
import org.sakaiproject.service.legacy.resource.Edit;
import org.sakaiproject.service.legacy.resource.Reference;
import org.sakaiproject.service.legacy.resource.ReferenceVector;
import org.sakaiproject.service.legacy.resource.Resource;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.resource.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.security.cover.SecurityService;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.TimeRange;
import org.sakaiproject.service.legacy.time.cover.TimeService;
import org.sakaiproject.util.Filter;
import org.sakaiproject.util.FormattedText;
import org.sakaiproject.util.java.StringUtil;
import org.sakaiproject.util.Validator;
import org.sakaiproject.util.xml.Xml;
import org.sakaiproject.util.resource.BaseResourcePropertiesEdit;
import org.sakaiproject.util.storage.StorageUser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
* <p>BaseCalendarService is an base implementation of the
* CHEF CalendarService, implemented as a Turbine service.  Extension classes
* implement object creation, access and storage.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public abstract class BaseCalendarService
	implements CalendarService, StorageUser, CacheRefresher
{
	/** The initial portion of a relative access point URL. */
	protected String m_relativeAccessPoint = null;

	/** A Cache object for caching: calendars keyed by reference. */
	protected Cache m_calendarCache = null;

	/** A bunch of caches for events: keyed by calendar id, the cache is keyed by event reference. */
	protected Hashtable m_eventCaches = null;

	/** A Storage object for access to calendars and events. */
	protected Storage m_storage = null;
	
	/** DELIMETER used to separate the list of custom fields for this calendar. */
	private final static String ADDFIELDS_DELIMITER = "_,_";

	/**
	* Access this service from the inner classes.
	*/
	protected BaseCalendarService service()
	{
		return this;
	}

	/**
	* Construct a Storage object.
	* @return The new storage object.
	*/
	protected abstract Storage newStorage();

	/**
	* Access the partial URL that forms the root of calendar URLs.
	* @param relative if true, form within the access path only (i.e. starting with /content)
	* @return the partial URL that forms the root of calendar URLs.
	*/
	protected String getAccessPoint(boolean relative)
	{
		return (relative ? "" : ServerConfigurationService.getAccessUrl()) + m_relativeAccessPoint;

	} // getAccessPoint

	/**
	* Check security permission.
	* @param lock The lock id string.
	* @param reference The resource's reference string, or null if no resource is involved.
	* @return true if permitted, false if not.
	*/
	protected boolean unlockCheck(String lock, String reference)
	{
		return SecurityService.unlock(lock, reference);

	} // unlockCheck

	/**
	* Check security permission.
	* @param lock The lock id string.
	* @param reference The resource's reference string, or null if no resource is involved.
	* @exception PermissionException thrown if the user does not have access
	*/
	protected void unlock(String lock, String reference) throws PermissionException
	{
		if (!SecurityService.unlock(lock, reference))
		{
			throw new PermissionException(UsageSessionService.getSessionUserId(), lock, reference);
		}

	} // unlock

	/**
	* Access the internal reference which can be used to access the calendar from within the system.
	* @param context The context.
	* @param id The calendar id.
	* @return The the internal reference which can be used to access the calendar from within the system.
	*/
	public String calendarReference(String context, String id)
	{
		return getAccessPoint(true)
			+ Resource.SEPARATOR
			+ REF_TYPE_CALENDAR
			+ Resource.SEPARATOR
			+ context
			+ Resource.SEPARATOR
			+ id;

	} // calendarReference

	/**
	* Access the internal reference which can be used to access the event from within the system.
	* @param context The context.
	* @param calendarId The calendar id.
	* @param id The event id.
	* @return The the internal reference which can be used to access the event from within the system.
	*/
	public String eventReference(String context, String calendarId, String id)
	{
		return getAccessPoint(true)
			+ Resource.SEPARATOR
			+ REF_TYPE_EVENT
			+ Resource.SEPARATOR
			+ context
			+ Resource.SEPARATOR
			+ calendarId
			+ Resource.SEPARATOR
			+ id;

	} // eventReference

	/**
	* Takes several calendar References and merges their events from within a given time range.
	* @param references The List of calendar References.
	* @param range The time period to use to select events.
	* @return CalendarEventVector object with the union of all events from the list of calendars in the given time range.
	*/
	public CalendarEventVector getEvents(List references, TimeRange range)
	{
		CalendarEventVector calendarEventVector = null;

		if (references != null && range != null)
		{
			List allEvents = new ArrayList();

			Iterator it = references.iterator();

			// Add the events for each calendar in our list.
			while (it.hasNext())
			{
				String calendarReference = (String) it.next();
				Calendar calendarObj = null;

				try
				{
					calendarObj = getCalendar(calendarReference);
				}

				catch (IdUnusedException e)
				{
					continue;
				}

				catch (PermissionException e)
				{
					continue;
				}

				if (calendarObj != null)
				{
					Iterator calEvent = null;

					try
					{
						calEvent = calendarObj.getEvents(range, null).iterator();
					}

					catch (PermissionException e1)
					{
						continue;
					}

					allEvents.addAll(new CalendarEventVector(calEvent));
				}
			}

			// Do a sort since each of the events implements the Comparable interface.
			Collections.sort(allEvents);

			// Build up a CalendarEventVector and return it.
			calendarEventVector = new CalendarEventVector(allEvents.iterator());
		}

		return calendarEventVector;
	}

	/**
	 * Access the id generating service and return a unique id.
	 * @return a unique id.
	 */
	protected String getUniqueId()
	{
		return m_IdService.getUniqueId();
	}

	/*******************************************************************************
	* Constructors, Dependencies and their setter methods
	*******************************************************************************/

	/** Dependency: logging service */
	protected Logger m_logger = null;

	/**
	 * Dependency: logging service.
	 * @param service The logging service.
	 */
	public void setLogger(Logger service)
	{
		m_logger = service;
	}

	/** Dependency: MemoryService. */
	protected MemoryService m_memoryService = null;

	/**
	 * Dependency: MemoryService.
	 * @param service The MemoryService.
	 */
	public void setMemoryService(MemoryService service)
	{
		m_memoryService = service;
	}

	/** Dependency: IdService. */
	protected IdService m_IdService = null;

	/**
	 * Dependency: IdService.
	 * @param manager The IdService.
	 */
	public void setIdService(IdService manager)
	{
		m_IdService = manager;
	}

	/** Configuration: cache, or not. */
	protected boolean m_caching = false;

	/**
	 * Configuration: set the locks-in-db
	 * @param path The storage path.
	 */
	public void setCaching(String value)
	{
		m_caching = new Boolean(value).booleanValue();
	}

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		try
		{
			m_relativeAccessPoint = REFERENCE_ROOT;

			// construct a storage helper and read
			m_storage = newStorage();
			m_storage.open();

			// make the calendar cache
			if (m_caching)
			{
				m_calendarCache =
					m_memoryService.newCache(this, getAccessPoint(true) + Resource.SEPARATOR + REF_TYPE_CALENDAR + Resource.SEPARATOR);

				// make the table to hold the event caches
				m_eventCaches = new Hashtable();
			}

			m_logger.info(this +".init(): caching: " + m_caching);
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}

		// register as a resource service
		ServerConfigurationService.registerResourceService(this);
	}

	/**
	* Returns to uninitialized state.
	*/
	public void destroy()
	{
		if (m_caching)
		{
			m_calendarCache.destroy();
			m_calendarCache = null;

			// TODO: destroy each cache
			m_eventCaches.clear();
			m_eventCaches = null;
		}

		m_storage.close();
		m_storage = null;

		m_logger.info(this +".destroy()");
	}

	/*******************************************************************************
	* CalendarService implementation
	*******************************************************************************/

	/**
	* check permissions for addCalendar().
	* @param ref A reference for the calendar.
	* @return true if the user is allowed to addCalendar(ref), false if not.
	*/
	public boolean allowAddCalendar(String ref)
	{
		return unlockCheck(EVENT_ADD_CALENDAR, ref);

	} // allowAddCalendar

	/**
	* Add a new calendar.
	* Must commitCalendar() to make official, or cancelCalendar() when done!
	* @param ref A reference for the calendar.
	* @return The newly created calendar.
	* @exception IdUsedException if the id is not unique.
	* @exception IdInvalidException if the id is not made up of valid characters.
	* @exception PermissionException if the user does not have permission to add a calendar.
	*/
	public CalendarEdit addCalendar(String ref) throws IdUsedException, IdInvalidException, PermissionException
	{
		// check the name's validity
		Validator.checkResourceRef(ref);

		// check for existance
		if (m_storage.checkCalendar(ref))
		{
			throw new IdUsedException(ref);
		}

		// check security
		unlock(EVENT_ADD_CALENDAR, ref);

		// keep it
		CalendarEdit calendar = m_storage.putCalendar(ref);

		((BaseCalendarEdit) calendar).setEvent(EVENT_ADD_CALENDAR);

		return calendar;

	} // addCalendar

	/**
	* check permissions for getCalendar().
	* @param ref The calendar reference.
	* @return true if the user is allowed to getCalendar(calendarId), false if not.
	*/
	public boolean allowGetCalendar(String ref)
	{
		return unlockCheck(EVENT_READ_CALENDAR, ref);

	} // allowGetCalendar

	/**
	* Find the calendar, in cache or info store - cache it if newly found.
	* @param ref The calendar reference.
	* @return The calendar, if found.
	*/
	protected Calendar findCalendar(String ref)
	{
		Calendar calendar = null;

		if ((!m_caching) || (m_calendarCache == null) || (m_calendarCache.disabled()))
		{
			// TODO: do we really want to do this? -ggolden
			// if we have done this already in this thread, use that
			calendar = (Calendar) CurrentService.getInThread(ref);
			if (calendar == null)
			{
				calendar = m_storage.getCalendar(ref);

				// "cache" the calendar in the current service in case they are needed again in this thread...
				if (calendar != null)
				{
					CurrentService.setInThread(ref, calendar);
				}
			}

			return calendar;
		}

		// if we have it cached, use it (even if it's cached as a null, a miss)
		if (m_calendarCache.containsKey(ref))
		{
			calendar = (Calendar) m_calendarCache.get(ref);
		}

		// if not in the cache, see if we have it in our info store
		else
		{
			calendar = m_storage.getCalendar(ref);

			// if so, cache it, even misses
			m_calendarCache.put(ref, calendar);
		}

		return calendar;

	} // findCalendar

	/**
	* Return a specific calendar.
	* @param ref The calendar reference.
	* @return the Calendar that has the specified name.
	* @exception IdUnusedException If this name is not defined for any calendar.
	* @exception PermissionException If the user does not have any permissions to the calendar.
	*/
	public Calendar getCalendar(String ref) throws IdUnusedException, PermissionException
	{
		Calendar c = findCalendar(ref);
		if (c == null)
			throw new IdUnusedException(ref);

		// check security (throws if not permitted)
		unlock(EVENT_READ_CALENDAR, ref);

		return c;

	} // getCalendar

	/**
	* check permissions for removeCalendar().
	* @param ref The calendar reference.
	* @return true if the user is allowed to removeCalendar(calendarId), false if not.
	*/
	public boolean allowRemoveCalendar(String ref)
	{
		return unlockCheck(EVENT_REMOVE_CALENDAR, ref);

	} // allowRemoveCalendar

	/**
	* Remove a calendar that is locked for edit.
	* @param calendar The calendar to remove.
	* @exception PermissionException if the user does not have permission to remove a calendar.
	*/
	public void removeCalendar(CalendarEdit calendar) throws PermissionException
	{
		// check for closed edit
		if (!calendar.isActiveEdit())
		{
			try
			{
				throw new Exception();
			}
			catch (Exception e)
			{
				m_logger.warn(this +".removeCalendar(): closed CalendarEdit", e);
			}
			return;
		}

		// check security
		unlock(EVENT_REMOVE_CALENDAR, calendar.getReference());

		m_storage.removeCalendar(calendar);

		// track event
		Event event = EventTrackingService.newEvent(EVENT_REMOVE_CALENDAR, calendar.getReference(), true);
		EventTrackingService.post(event);

		// mark the calendar as removed
		 ((BaseCalendarEdit) calendar).setRemoved(event);

		// close the edit object
		 ((BaseCalendarEdit) calendar).closeEdit();

		// remove any realm defined for this resource
		try
		{
			RealmService.removeRealm(RealmService.editRealm(calendar.getReference()));
		}
		catch (InUseException e)
		{
			m_logger.warn(this +".removeCalendar: removing realm for : " + calendar.getReference() + " : " + e);
		}
		catch (PermissionException e)
		{
			m_logger.warn(this +".removeCalendar: removing realm for : " + calendar.getReference() + " : " + e);
		}
		catch (IdUnusedException ignore)
		{
		}

	} // removeCalendar

	/**
	* Return a List of all the defined calendars.
	* @return a List of Calendar objects (may be empty)
	*/
	public List getCalendars()
	{
		List calendars = new Vector();

		if ((!m_caching) || (m_calendarCache == null) || (m_calendarCache.disabled()))
		{ 
			calendars = m_storage.getCalendars();
			return calendars;
		}

		// if the cache is complete, use it
		if (m_calendarCache.isComplete())
		{
			// get just the calendars in the cache
			calendars = m_calendarCache.getAll();
		}

		// otherwise get all the calendars from storage
		else
		{
			// Note: while we are getting from storage, storage might change.  These can be processed
			// after we get the storage entries, and put them in the cache, and mark the cache complete.
			// -ggolden
			synchronized (m_calendarCache)
			{
				// if we were waiting and it's now complete...
				if (m_calendarCache.isComplete())
				{
					// get just the calendars in the cache
					calendars = m_calendarCache.getAll();
					return calendars;
				}

				// save up any events to the cache until we get past this load
				m_calendarCache.holdEvents();

				calendars = m_storage.getCalendars();
				// update the cache, and mark it complete
				for (int i = 0; i < calendars.size(); i++)
				{
					Calendar calendar = (Calendar) calendars.get(i);
					m_calendarCache.put(calendar.getReference(), calendar);
				}

				m_calendarCache.setComplete();

				// now we are complete, process any cached events
				m_calendarCache.processEvents();
			}
		}

		return calendars;

	} // getCalendars

	/**
	* check permissions for importing calendar events
	* @param ref The calendar reference.
	* @return true if the user is allowed to import events, false if not.
	*/
	public boolean allowImportCalendar(String ref)
	{
		return unlockCheck(EVENT_IMPORT_CALENDAR, ref);

	} // allowImportCalendar

	/**
	* check permissions for editCalendar()
	* @param ref The calendar reference.
	* @return true if the user is allowed to update the calendar, false if not.
	*/
	public boolean allowUpdateCalendar(String ref)
	{
		return unlockCheck(EVENT_MODIFY_CALENDAR, ref);

	} // allowUpdateCalendar

	/**
	* Get a locked calendar object for editing.
	* Must commitCalendar() to make official, or cancelCalendar() or removeCalendar() when done!
	* @param ref The calendar reference.
	* @return A CalendarEdit object for editing.
	* @exception IdUnusedException if not found, or if not an CalendarEdit object
	* @exception PermissionException if the current user does not have permission to mess with this user.
	* @exception InUseException if the Calendar object is locked by someone else.
	*/
	public CalendarEdit editCalendar(String ref) throws IdUnusedException, PermissionException, InUseException
	{
		// check for existance
		if (!m_storage.checkCalendar(ref))
		{
			throw new IdUnusedException(ref);
		}

		// check security (throws if not permitted)
		unlock(EVENT_MODIFY_CALENDAR, ref);

		// ignore the cache - get the calendar with a lock from the info store
		CalendarEdit edit = m_storage.editCalendar(ref);
		if (edit == null)
			throw new InUseException(ref);

		((BaseCalendarEdit) edit).setEvent(EVENT_MODIFY_CALENDAR);

		return edit;

	} // editCalendar

	/**
	* Commit the changes made to a CalendarEdit object, and release the lock.
	* The CalendarEdit is disabled, and not to be used after this call.
	* @param edit The CalendarEdit object to commit.
	*/
	public void commitCalendar(CalendarEdit edit)
	{
		// check for closed edit
		if (!edit.isActiveEdit())
		{
			try
			{
				throw new Exception();
			}
			catch (Exception e)
			{
				m_logger.warn(this +".commitEdit(): closed CalendarEdit", e);
			}
			return;
		}

		m_storage.commitCalendar(edit);

		// track event
		Event event = EventTrackingService.newEvent(((BaseCalendarEdit) edit).getEvent(), edit.getReference(), true);
		EventTrackingService.post(event);

		// close the edit object
		 ((BaseCalendarEdit) edit).closeEdit();

	} // commitCalendar

	/**
	* Cancel the changes made to a CalendarEdit object, and release the lock.
	* The CalendarEdit is disabled, and not to be used after this call.
	* @param edit The CalendarEdit object to commit.
	*/
	public void cancelCalendar(CalendarEdit edit)
	{
		// check for closed edit
		if (!edit.isActiveEdit())
		{
			try
			{
				throw new Exception();
			}
			catch (Exception e)
			{
				m_logger.warn(this +".cancelCalendarEdit(): closed CalendarEventEdit", e);
			}
			return;
		}

		// release the edit lock
		m_storage.cancelCalendar(edit);

		// close the edit object
		 ((BaseCalendarEdit) edit).closeEdit();

	} // cancelCalendar

	/**
	 * {@inheritDoc}
	 */
	public RecurrenceRule newRecurrence(String frequency)
	{
		if (frequency.equals(DailyRecurrenceRule.FREQ))
		{
			return new DailyRecurrenceRule();
		}
		else if (frequency.equals(WeeklyRecurrenceRule.FREQ))
		{
			return new WeeklyRecurrenceRule();
		}
		else if (frequency.equals(MonthlyRecurrenceRule.FREQ))
		{
			return new MonthlyRecurrenceRule();
		}
		else if (frequency.equals(YearlyRecurrenceRule.FREQ))
		{
			return new YearlyRecurrenceRule();
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public RecurrenceRule newRecurrence(String frequency, int interval)
	{
		if (frequency.equals(DailyRecurrenceRule.FREQ))
		{
			return new DailyRecurrenceRule(interval);
		}
		else if (frequency.equals(WeeklyRecurrenceRule.FREQ))
		{
			return new WeeklyRecurrenceRule(interval);
		}
		else if (frequency.equals(MonthlyRecurrenceRule.FREQ))
		{
			return new MonthlyRecurrenceRule(interval);
		}
		else if (frequency.equals(YearlyRecurrenceRule.FREQ))
		{
			return new YearlyRecurrenceRule(interval);
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public RecurrenceRule newRecurrence(String frequency, int interval, int count)
	{
		if (frequency.equals(DailyRecurrenceRule.FREQ))
		{
			return new DailyRecurrenceRule(interval, count);
		}
		else if (frequency.equals(WeeklyRecurrenceRule.FREQ))
		{
			return new WeeklyRecurrenceRule(interval, count);
		}
		else if (frequency.equals(MonthlyRecurrenceRule.FREQ))
		{
			return new MonthlyRecurrenceRule(interval, count);
		}
		else if (frequency.equals(YearlyRecurrenceRule.FREQ))
		{
			return new YearlyRecurrenceRule(interval, count);
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public RecurrenceRule newRecurrence(String frequency, int interval, Time until)
	{
		if (frequency.equals(DailyRecurrenceRule.FREQ))
		{
			return new DailyRecurrenceRule(interval, until);
		}
		else if (frequency.equals(WeeklyRecurrenceRule.FREQ))
		{
			return new WeeklyRecurrenceRule(interval, until);
		}
		else if (frequency.equals(MonthlyRecurrenceRule.FREQ))
		{
			return new MonthlyRecurrenceRule(interval, until);
		}
		else if (frequency.equals(YearlyRecurrenceRule.FREQ))
		{
			return new YearlyRecurrenceRule(interval, until);
		}

		return null;
	}

	/*******************************************************************************
	* ResourceService implementation
	*******************************************************************************/

	/**
	 * {@inheritDoc}
	 */
	public String getLabel()
	{
		return "calendar";
	}

	/**
	 * {@inheritDoc}
	 */
	public String archive(String siteId, Document doc, Stack stack, String archivePath, ReferenceVector attachments)
	{
		// prepare the buffer for the results log
		StringBuffer results = new StringBuffer();

		// start with an element with our very own (service) name
		Element element = doc.createElement(CalendarService.class.getName());
		((Element)stack.peek()).appendChild(element);
		stack.push(element);

		// get the channel associated with this site
		String calRef = calendarReference(siteId, SiteService.MAIN_CONTAINER);

		results.append("archiving calendar " + calRef + ".\n");

		try
		{
			// do the channel
			Calendar cal = getCalendar(calRef);
			Element containerElement = cal.toXml(doc, stack);
			stack.push(containerElement);

			// do the messages in the channel
			Iterator events = cal.getEvents(null, null).iterator();
			while (events.hasNext())
			{
				CalendarEvent event = (CalendarEvent) events.next();
				event.toXml(doc, stack);
		
				// collect message attachments
				ReferenceVector atts = event.getAttachments();
				for (int i = 0; i < atts.size(); i++)
				{
					Reference ref = (Reference) atts.elementAt(i);
					// if it's in the attachment area, and not already in the list
					if (	(ref.getReference().startsWith("/content/attachment/"))
						&&	(!attachments.contains(ref)))
					{
						attachments.add(ref);
					}
				}
			}
			
			stack.pop();
		}
		catch (Exception any)
		{
			m_logger.warn(this + ".archve: exception archiving messages for service: "
						+ CalendarService.class.getName() + " channel: "
						+ calRef);
		}

		stack.pop();

		return results.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public String merge(String siteId, Element root, String archivePath, String fromSiteId, Map attachmentNames, HashMap userIdTrans, Set userListAllowImport)
	{
		Map ids = new HashMap();

		// prepare the buffer for the results log
		StringBuffer results = new StringBuffer();

		// get the channel associated with this site
		String calendarRef = calendarReference(siteId, SiteService.MAIN_CONTAINER);

		int count = 0;

		try
		{
			Calendar calendar = null;
			try
			{
				calendar = getCalendar(calendarRef);
			}
			catch (IdUnusedException e)
			{
				CalendarEdit edit = addCalendar(calendarRef);
				commitCalendar(edit);
				calendar = edit;
			}
			
			// pass the DOM to get new event ids, and adjust attachments
			NodeList children2 = root.getChildNodes();
			int length2 = children2.getLength();
			for (int i2 = 0; i2 < length2; i2++)
			{
				Node child2 = children2.item(i2);
				if (child2.getNodeType() == Node.ELEMENT_NODE)
				{
					Element element2 = (Element)child2;
	
					// get the "calendar" child
					if (element2.getTagName().equals("calendar"))
					{					
						NodeList children3 = element2.getChildNodes();
						final int length3 = children3.getLength();
						for(int i3 = 0; i3 < length3; i3++)
						{
							Node child3 = children3.item(i3);
							if (child3.getNodeType() == Node.ELEMENT_NODE)
							{
								Element element3 = (Element)child3;

								if (element3.getTagName().equals("properties"))
								{
									NodeList children8 = element3.getChildNodes();
									final int length8 = children8.getLength();
									for(int i8 = 0; i8 < length8; i8++)
									{
										Node child8 = children8.item(i8);
										if (child8.getNodeType() == Node.ELEMENT_NODE)
										{
											Element element8 = (Element)child8;
	
											// for "event" children
											if (element8.getTagName().equals("property"))
											{
												String pName = element8.getAttribute("name");
												if ((pName != null) && (pName.equalsIgnoreCase("CHEF:calendar-fields")))
												{
													String pValue = element8.getAttribute("value");
													if ("BASE64".equalsIgnoreCase(element8.getAttribute("enc")))
													{
														pValue = Xml.decodeAttribute(element8, "value");
													}
													
													if (pValue != null)
													{
														try
														{
															CalendarEdit calEdit = editCalendar(calendarRef);
															String calFields = StringUtil.trimToNull(calEdit.getEventFields());
			
															if (calFields != null)
																pValue = calFields + ADDFIELDS_DELIMITER + pValue;	
		
															calEdit.setEventFields(pValue);
															commitCalendar(calEdit);
														}
														catch (Exception e)
														{
															m_logger.warn(this + ".merge() when editing calendar: exception: ", e);
														}
													}
												}
											}
										}
									}
								}
				
								// for "event" children
								if (element3.getTagName().equals("event"))
								{
									// adjust the id
									String oldId = element3.getAttribute("id");
									String newId = getUniqueId();
									element3.setAttribute("id", newId);

									// get the attachment kids
									NodeList children5 = element3.getChildNodes();
									final int length5 = children5.getLength();
									for(int i5 = 0; i5 < length5; i5++)
									{
										Node child5 = children5.item(i5);
										if (child5.getNodeType() == Node.ELEMENT_NODE)
										{
											Element element5 = (Element)child5;
							
											// for "attachment" children
											if (element5.getTagName().equals("attachment"))
											{
												// map the attachment area folder name
												String oldUrl = element5.getAttribute("relative-url");
												if (oldUrl.startsWith("/content/attachment/"))
												{
													String newUrl = (String) attachmentNames.get(oldUrl);
													if (newUrl != null)
													{
														if (newUrl.startsWith("/attachment/"))
															newUrl = "/content".concat(newUrl);
																	
														element5.setAttribute("relative-url", Validator.escapeQuestionMark(newUrl));
													}
												}
												
												// map any references to this site to the new site id
												else if (oldUrl.startsWith("/content/group/" + fromSiteId + "/"))
												{
													String newUrl = "/content/group/"
														+ siteId
														+ oldUrl.substring(15 + fromSiteId.length());
													element5.setAttribute("relative-url", Validator.escapeQuestionMark(newUrl));
												}
											}
										}
									}

									// create a new message in the calendar
									CalendarEventEdit edit = calendar.mergeEvent(element3);
									calendar.commitEvent(edit);
									count++;
								}
							}
						}
					}
				}
			}
		}
		catch (Exception any)
		{
			m_logger.warn(this + ".merge(): exception: ", any);
		}

		results.append("merging calendar " + calendarRef + " (" + count + ") messages.\n");
		return results.toString();
	}

	/**
	* import tool(s) contents from the source context into the destination context
	* @param fromContext The source context
	* @param toContext The destination context
	* @param resourceIds when null, all resources will be imported; otherwise, only resources with those ids will be imported
	*/
	public void importResources(String fromContext, String toContext, List resourceIds)
	{
		// get the channel associated with this site
		String oCalendarRef = calendarReference(fromContext, SiteService.MAIN_CONTAINER);

		Calendar oCalendar = null;
		try
		{
			oCalendar = getCalendar(oCalendarRef);
			
			// new calendar
			CalendarEdit nCalendar = null;
			String nCalendarRef = calendarReference(toContext, SiteService.MAIN_CONTAINER);
			try
			{
				nCalendar = editCalendar(nCalendarRef);
			}
			catch (IdUnusedException e)
			{
				try
				{
					CalendarEdit edit = addCalendar(nCalendarRef);
					commitCalendar(edit);
					nCalendar = edit;
				}
				catch (IdUsedException ee){}
				catch (IdInvalidException ee){}
			}
			catch (PermissionException e){}
			catch (InUseException e) {}
			
			if (nCalendar != null)
			{
				List oEvents = oCalendar.getEvents(null, null);
				
				String oFields = StringUtil.trimToNull(oCalendar.getEventFields());
				String nFields = StringUtil.trimToNull(nCalendar.getEventFields());
				String allFields = "";
				
				if (oFields != null)
				{
					if (nFields != null)
					{
						allFields = nFields + ADDFIELDS_DELIMITER + oFields;
					}
					else
					{
						allFields = oFields;
					}
					nCalendar.setEventFields(allFields);
				}
				
																
				for (int i=0; i < oEvents.size(); i++)
				{
					CalendarEvent oEvent = (CalendarEvent) oEvents.get(i);
					try
					{
						CalendarEvent e = nCalendar.addEvent(
								oEvent.getRange(),
								oEvent.getDisplayName(),
								oEvent.getDescription(),
								oEvent.getType(),
								oEvent.getLocation(),
								oEvent.getAttachments());
								
						try
						{
							BaseCalendarEventEdit eEdit = (BaseCalendarEventEdit) nCalendar.editEvent(e.getId());
							// properties
							ResourcePropertiesEdit p = eEdit.getPropertiesEdit();
							p.clear();
							p.addAll(oEvent.getProperties());
							
							// recurrence rule
							RecurrenceRule rule = oEvent.getRecurrenceRule();
							eEdit.setRecurrenceRule(rule);
							
							try
							{
								BaseCalendarEventEdit oEdit = (BaseCalendarEventEdit) oCalendar.editEvent(oEvent.getId());
								RecurrenceRule exRule = oEdit.getExclusionRule();
								eEdit.setExclusionRule(exRule);
							}
							catch (Exception error)
							{}
												
							//commit new event
							m_storage.commitEvent(nCalendar, eEdit);
						}
						catch (InUseException eee){}
					}
					catch (PermissionException ee)
					{
					}
				}
				// commit new calendar
				m_storage.commitCalendar(nCalendar);
			}	// if
		}
		catch (IdUnusedException e)
		{
		}
		catch (PermissionException e)
		{
		}
		
	}	// importResources
	
	/*******************************************************************************
	* Calendar implementation
	*******************************************************************************/

	public class BaseCalendarEdit extends Observable implements CalendarEdit, SessionStateBindingListener
	{
		/** The context in which this calendar exists. */
		protected String m_context = null;

		/** Store the unique-in-context calendar id. */
		protected String m_id = null;

		/** The properties. */
		protected ResourcePropertiesEdit m_properties = null;

		/** When true, the calendar has been removed. */
		protected boolean m_isRemoved = false;

		/** The event code for this edit. */
		protected String m_event = null;

		/** Active flag. */
		protected boolean m_active = false;

		/**
		* Construct with an id.
		* @param ref The calendar reference.
		*/
		public BaseCalendarEdit(String ref)
		{
			// set the ids
			Reference r = new Reference(ref);
			m_context = r.getContext();
			m_id = r.getId();

			// setup for properties
			m_properties = new BaseResourcePropertiesEdit();

		} // BaseCalendarEdit

		/**
		* Construct as a copy of another.
		* @param id The other to copy.
		*/
		public BaseCalendarEdit(Calendar other)
		{
			// set the ids
			m_context = other.getContext();
			m_id = other.getId();

			// setup for properties
			m_properties = new BaseResourcePropertiesEdit();
			m_properties.addAll(other.getProperties());

		} // BaseCalendarEdit

		/**
		* Construct from a calendar (and possibly events) already defined
		* in XML in a DOM tree.
		* The Calendar is added to storage.
		* @param el The XML DOM element defining the calendar.
		*/
		public BaseCalendarEdit(Element el)
		{
			// setup for properties
			m_properties = new BaseResourcePropertiesEdit();

			m_id = el.getAttribute("id");
			m_context = el.getAttribute("context");

			// the children (properties, ignore events)
			NodeList children = el.getChildNodes();
			final int length = children.getLength();
			for (int i = 0; i < length; i++)
			{
				Node child = children.item(i);
				if (child.getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element element = (Element) child;

				// look for properties (ignore possible "event" entries)
				if (element.getTagName().equals("properties"))
				{
					// re-create properties
					m_properties = new BaseResourcePropertiesEdit(element);
				}
			}

		} // BaseCalendarEdit

		/**
		* Clean up.
		*/
		protected void finalize()
		{
			deleteObservers();

			// catch the case where an edit was made but never resolved
			if (m_active)
			{
				cancelCalendar(this);
			}

		} // finalize

		/**
		* Set the calendar as removed.
		* @param event The tracking event associated with this action.
		*/
		public void setRemoved(Event event)
		{
			m_isRemoved = true;

			// notify observers
			notify(event);

			// now clear observers
			deleteObservers();

		} // setRemoved

		/**
		* Access the context of the resource.
		* @return The context.
		*/
		public String getContext()
		{
			return m_context;

		} // getContext

		/**
		* Access the id of the resource.
		* @return The id.
		*/
		public String getId()
		{
			return m_id;

		} // getId

		/**
		* Access the URL which can be used to access the resource.
		* @return The URL which can be used to access the resource.
		*/
		public String getUrl()
		{
			return getAccessPoint(false) + SEPARATOR + getId() + SEPARATOR; // %%% needs fixing re: context

		} // getUrl

		/**
		* Access the internal reference which can be used to access the resource from within the system.
		* @return The the internal reference which can be used to access the resource from within the system.
		*/
		public String getReference()
		{
			return calendarReference(m_context, m_id);

		} // getReference

		/**
		* Access the collection's properties.
		* @return The collection's properties.
		*/
		public ResourceProperties getProperties()
		{
			return m_properties;

		} // getProperties

		/**
		* check permissions for getEvents() and getEvent().
		* @return true if the user is allowed to get events from the calendar, false if not.
		*/
		public boolean allowGetEvents()
		{
			return unlockCheck(EVENT_READ_CALENDAR, getReference());

		} // allowGetEvents

		/**
		* Return a List of all or filtered events in the calendar.
		* The order in which the events will be found in the iteration is by event start date.
		* @param range A time range to limit the iterated events.  May be null; all events will be returned.
		* @param filter A filtering object to accept events into the iterator, or null if no filtering is desired.
		* @return a List of all or filtered CalendarEvents in the calendar (may be empty).
		* @exception PermissionException if the user does not have read permission to the calendar.
		*/
		public List getEvents(TimeRange range, Filter filter) throws PermissionException
		{
			// check security (throws if not permitted)
			unlock(EVENT_READ_CALENDAR, getReference());

			// track event
			//EventTrackingService.post(EventTrackingService.newEvent(EVENT_READ_CALENDAR, getReference(), false));

			List events = new Vector();

			if ((!m_caching) || (m_calendarCache == null) || (m_calendarCache.disabled()))
			{
				// TODO: do we really want to do this? -ggolden
				// if we have done this already in this thread, use that
				events = (List) CurrentService.getInThread(getReference()+".events");
				if (events == null)
				{
					events = m_storage.getEvents(this);

					// "cache" the events in the current service in case they are needed again in this thread...
					CurrentService.setInThread(getReference()+".events", events);
				}
			}

			else
			{
				// find the event cache
				Cache eventCache = (Cache) m_eventCaches.get(getReference());
				if (eventCache == null)
				{
					synchronized (m_eventCaches)
					{
						// check again
						eventCache = (Cache) m_eventCaches.get(getReference());

						// if still not there, make one
						if (eventCache == null)
						{
							eventCache = m_memoryService.newCache(service(), eventReference(m_context, m_id, ""));
							m_eventCaches.put(getReference(), eventCache);
						}
					}
				}

				// if the cache is complete, use it
				if (eventCache.isComplete())
				{
					// get just this calendar's events
					events = eventCache.getAll();
				}

				// otherwise get all the events from storage
				else
				{
					// Note: while we are getting from storage, storage might change.  These can be processed
					// after we get the storage entries, and put them in the cache, and mark the cache complete.
					// -ggolden
					synchronized (eventCache)
					{
						// if we were waiting and it's now complete...
						if (eventCache.isComplete())
						{
							// get just this calendar's events
							events = eventCache.getAll();
						}
						else
						{
							// save up any events to the cache until we get past this load
							eventCache.holdEvents();

							// get all the events for the calendar
							events = m_storage.getEvents(this);

							// update the cache, and mark it complete
							for (int i = 0; i < events.size(); i++)
							{
								CalendarEvent event = (CalendarEvent) events.get(i);
								eventCache.put(event.getReference(), event);
							}

							eventCache.setComplete();

							// now we are complete, process any cached events
							eventCache.processEvents();
						}
					}
				}
			}

			if (events.size() == 0)
				return events;

			// now filter out the events to just those in the range
			// Note: if no range, we won't filter, which means we don't expand recurring events, but just
			//       return it as a single event.  This is very good for an archive... -ggolden
			if (range != null)
			{
				events = filterEvents(events, range);
			}

			// filter out based on the filter
			if (filter != null)
			{
				List filtered = new Vector();
				for (int i = 0; i < events.size(); i++)
				{
					Event event = (Event) events.get(i);
					if (filter.accept(event))
						filtered.add(event);
				}
				if (filtered.size() == 0)
					return filtered;
				events = filtered;
			}

			// sort - natural order is date ascending
			Collections.sort(events);

			return events;

		} // getEvents

		/**
		* Filter the events to only those in the time range.
		* @param events The full list of events.
		* @param range The time range.
		* @return A list of events from the incoming list that overlap the given time range.
		*/
		protected List filterEvents(List events, TimeRange range)
		{
			List filtered = new Vector();
			for (int i = 0; i < events.size(); i++)
			{
				CalendarEvent event = (CalendarEvent) events.get(i);

				// resolve the event to the list of events in this range
				List resolved = ((BaseCalendarEventEdit) event).resolve(range);
				filtered.addAll(resolved);
			}

			return filtered;

		} // filterEvents

		/**
		* Return a specific calendar event, as specified by event id.
		* @param eventId The id of the event to get.
		* @return the CalendarEvent that has the specified id.
		* @exception IdUnusedException If this id is not a defined event in this calendar.
		* @exception PermissionException If the user does not have any permissions to read the calendar.
		*/
		public CalendarEvent getEvent(String eventId) throws IdUnusedException, PermissionException
		{
			// check security on the calendar (throws if not permitted)
			unlock(EVENT_READ_CALENDAR, getReference());

			CalendarEvent e = findEvent(eventId);

			if (e == null)
				throw new IdUnusedException(eventId);

			// track event
			// EventTrackingService.post(EventTrackingService.newEvent(EVENT_READ_CALENDAR, e.getReference(), false));

			return e;

		} // getEvent

		/**
		* check permissions for addEvent().
		* @return true if the user is allowed to addEvent(...), false if not.
		*/
		public boolean allowAddEvent()
		{
			return unlockCheck(EVENT_ADD_CALENDAR, getReference());

		} // allowAddEvent

		/**
		* Add a new event to this calendar.
		* @param range The event's time range.
		* @param displayName The event's display name (PROP_DISPLAY_NAME) property value.
		* @param description The event's description as plain text (PROP_DESCRIPTION) property value.
		* @param type The event's calendar event type (PROP_CALENDAR_TYPE) property value.
		* @param location The event's calendar event location (PROP_CALENDAR_LOCATION) property value.
		* @param attachments The event attachments, a vector of Reference objects.
		* @return The newly added event.
		* @exception PermissionException If the user does not have permission to modify the calendar.
		*/
		public CalendarEvent addEvent(
			TimeRange range,
			String displayName,
			String description,
			String type,
			String location,
			ReferenceVector attachments)
			throws PermissionException
		{
			// make one
			CalendarEventEdit edit = addEvent();

			// set it up
			edit.setRange(range);
			edit.setDisplayName(displayName);
			edit.setDescription(description);
			edit.setType(type);
			edit.setLocation(location);
			edit.replaceAttachments(attachments);

			// commit it
			commitEvent(edit);

			return edit;

		} // addEvent

		/**
		* Add a new event to this calendar.
		* Must commitEvent() to make official, or cancelEvent() when done!
		* @return The newly added event, locked for update.
		* @exception PermissionException If the user does not have write permission to the calendar.
		*/
		public CalendarEventEdit addEvent() throws PermissionException
		{
			// check security (throws if not permitted)
			unlock(EVENT_ADD_CALENDAR, getReference());
			
			// allocate a new unique event id
			String id = getUniqueId();

			// get a new event in the info store
			CalendarEventEdit event = m_storage.putEvent(this, id);

			((BaseCalendarEventEdit) event).setEvent(EVENT_ADD_CALENDAR);

			return event;

		} // addEvent

		/**
		* Merge in a new event as defined in the xml.
		* @param el The event information in XML in a DOM element.
		* @exception PermissionException If the user does not have write permission to the calendar.
		* @exception IdUsedException if the user id is already used.
		*/
		public CalendarEventEdit mergeEvent(Element el) throws PermissionException, IdUsedException
		{
			// check security (throws if not permitted)
			unlock(EVENT_MODIFY_CALENDAR, getReference());

			CalendarEvent eventFromXml = (CalendarEvent) newResource(this, el);

			// reserve a user with this id from the info store - if it's in use, this will return null
			CalendarEventEdit event = m_storage.putEvent(this, eventFromXml.getId());
			if (event == null)
			{
				throw new IdUsedException(eventFromXml.getId());
			}

			// transfer from the XML read object to the Edit
			((BaseCalendarEventEdit) event).set(eventFromXml);

			((BaseCalendarEventEdit) event).setEvent(EVENT_MODIFY_CALENDAR);

			return event;

		} // mergeEvent

		/**
		* check permissions for removeEvent().
		* @param event The event from this calendar to remove.
		* @return true if the user is allowed to removeEvent(event), false if not.
		*/
		public boolean allowRemoveEvent(CalendarEvent event)
		{
			// check security
			return unlockCheck(EVENT_MODIFY_CALENDAR, getReference());

		} // allowRemoveEvent

		/**
		* Remove an event from the calendar, one locked for edit.
		* Note: if the event is a recurring event, the entire sequence is modified by this commit (MOD_ALL).
		* @param event The event from this calendar to remove.
		*/
		public void removeEvent(CalendarEventEdit edit)
		{
			removeEvent(edit, MOD_ALL);

		} // removeEvent

		/**
		* Remove an event from the calendar, one locked for edit.
		* @param event The event from this calendar to remove.
		* @param intention The recurring event modification intention,
		* based on values in the CalendarService "MOD_*",
		* used if the event is part of a recurring event sequence to determine how much of the sequence is removed.
		*/
		public void removeEvent(CalendarEventEdit edit, int intention)
		{
			// check for closed edit
			if (!edit.isActiveEdit())
			{
				try
				{
					throw new Exception();
				}
				catch (Exception e)
				{
					m_logger.warn(this +".removeEvent(): closed EventEdit", e);
				}
				return;
			}

			BaseCalendarEventEdit bedit = (BaseCalendarEventEdit) edit;

			// if the id has a time range encoded, as for one of a sequence of recurring events, separate that out
			TimeRange timeRange = null;
			int sequence = 0;
			if (bedit.m_id.startsWith("!"))
			{
				String[] parts = StringUtil.split(bedit.m_id.substring(1), "!");
				try
				{
					timeRange = TimeService.newTimeRange(parts[0]);
					sequence = Integer.parseInt(parts[1]);
					bedit.m_id = parts[2];
				}
				catch (Exception ex)
				{
					m_logger.warn(this +".removeEvent: exception parsing eventId: " + bedit.m_id + " : " + ex);
				}
			}

			// deal with recurring event sequence modification
			if (timeRange != null)
			{
				// delete only this - add it as an exclusion in the edit
				if (intention == MOD_THIS)
				{
					// get the edit back to initial values... so only the exclusion is changed
					edit = (CalendarEventEdit) m_storage.getEvent(this, bedit.m_id);
					bedit = (BaseCalendarEventEdit) edit;

					// add an exclusion for where this one would have been %%% we are changing it, should it be immutable? -ggolden
					List exclusions = ((ExclusionSeqRecurrenceRule) bedit.getExclusionRule()).getExclusions();
					exclusions.add(new Integer(sequence));

					// complete the edit
					m_storage.commitEvent(this, edit);
				}

				// delete them all, i.e. the one initial event
				else
				{
					m_storage.removeEvent(this, edit);
				}
			}

			// else a single event to delete
			else
			{
				m_storage.removeEvent(this, edit);
			}

			// track event
			Event event = EventTrackingService.newEvent(EVENT_MODIFY_CALENDAR, edit.getReference(), true);
			EventTrackingService.post(event);

			// calendar notification
			notify(event);

			// close the edit object
			 ((BaseCalendarEventEdit) edit).closeEdit();

			// remove any realm defined for this resource
			try
			{
				RealmService.removeRealm(RealmService.editRealm(edit.getReference()));
			}
			catch (InUseException e)
			{
				m_logger.warn(this +".removeEvent: removing realm for : " + edit.getReference() + " : " + e);
			}
			catch (PermissionException e)
			{
				m_logger.warn(this +".removeEvent: removing realm for : " + edit.getReference() + " : " + e);
			}
			catch (IdUnusedException ignore)
			{
			}

		} // removeEvent

		/**
		* check permissions for editEvent()
		* @param id The event id.
		* @return true if the user is allowed to update the event, false if not.
		*/
		public boolean allowEditEvent(String eventId)
		{
			CalendarEvent e = findEvent(eventId);
			if (e == null)
				return false;

			// check security (throws if not permitted)
			return unlockCheck(EVENT_MODIFY_CALENDAR, getReference());

		} // allowEditCalendarEvent

		/**
		* Return a specific calendar event, as specified by event name, locked for update.
		* Must commitEvent() to make official, or cancelEvent(), or removeEvent() when done!
		* @param eventId The id of the event to get.
		* @return the Event that has the specified id.
		* @exception IdUnusedException If this name is not a defined event in this calendar.
		* @exception PermissionException If the user does not have any permissions to edit the event.
		* @exception InUseException if the event is locked for edit by someone else.
		*/
		public CalendarEventEdit editEvent(String eventId) throws IdUnusedException, PermissionException, InUseException
		{
			// if the id has a time range encoded, as for one of a sequence of recurring events, separate that out
			TimeRange timeRange = null;
			int sequence = 0;
			if (eventId.startsWith("!"))
			{
				String[] parts = StringUtil.split(eventId.substring(1), "!");
				try
				{
					timeRange = TimeService.newTimeRange(parts[0]);
					sequence = Integer.parseInt(parts[1]);
					eventId = parts[2];
				}
				catch (Exception ex)
				{
					m_logger.warn(this +".editEvent: exception parsing eventId: " + eventId + " : " + ex);
				}
			}

			CalendarEvent e = findEvent(eventId);
			if (e == null)
				throw new IdUnusedException(eventId);

			// check security (throws if not permitted)
			unlock(EVENT_MODIFY_CALENDAR, getReference());

			// ignore the cache - get the CalendarEvent with a lock from the info store
			CalendarEventEdit edit = m_storage.editEvent(this, eventId);
			if (edit == null)
				throw new InUseException(eventId);

			BaseCalendarEventEdit bedit = (BaseCalendarEventEdit) edit;

			// if this is one in a sequence, adjust it
			if (timeRange != null)
			{
				// move the specified range into the event's range, storing the base range
				bedit.m_baseRange = bedit.m_range;
				bedit.m_range = timeRange;
				bedit.m_id = '!' + bedit.m_range.toString() + '!' + sequence + '!' + bedit.m_id;
			}

			bedit.setEvent(EVENT_MODIFY_CALENDAR);

			return edit;

		} // editEvent

		/**
		* Commit the changes made to a CalendarEventEdit object, and release the lock.
		* The CalendarEventEdit is disabled, and not to be used after this call.
		* Note: if the event is a recurring event, the entire sequence is modified by this commit (MOD_ALL).
		* @param edit The CalendarEventEdit object to commit.
		*/
		public void commitEvent(CalendarEventEdit edit)
		{
			commitEvent(edit, MOD_ALL);

		} // commitEvent

		/**
		* Commit the changes made to a CalendarEventEdit object, and release the lock.
		* The CalendarEventEdit is disabled, and not to be used after this call.
		* @param edit The CalendarEventEdit object to commit.
		* @param intention The recurring event modification intention,
		* based on values in the CalendarService "MOD_*",
		* used if the event is part of a recurring event sequence to determine how much of the sequence is changed by this commmit.
		*/
		public void commitEvent(CalendarEventEdit edit, int intention)
		{
			// check for closed edit
			if (!edit.isActiveEdit())
			{
				try
				{
					throw new Exception();
				}
				catch (Exception e)
				{
					m_logger.warn(this +".commitEvent(): closed CalendarEventEdit", e);
				}
				return;
			}

			BaseCalendarEventEdit bedit = (BaseCalendarEventEdit) edit;

			// if the id has a time range encoded, as for one of a sequence of recurring events, separate that out
			TimeRange timeRange = null;
			int sequence = 0;
			if (bedit.m_id.startsWith("!"))
			{
				String[] parts = StringUtil.split(bedit.m_id.substring(1), "!");
				try
				{
					timeRange = TimeService.newTimeRange(parts[0]);
					sequence = Integer.parseInt(parts[1]);
					bedit.m_id = parts[2];
				}
				catch (Exception ex)
				{
					m_logger.warn(this +".commitEvent: exception parsing eventId: " + bedit.m_id + " : " + ex);
				}
			}

			// for recurring event sequence
			TimeRange newTimeRange = null;
			BaseCalendarEventEdit newEvent = null;
			if (timeRange != null)
			{
				// if changing this event only
				if (intention == MOD_THIS)
				{
					// make a new event for this one
					String id = getUniqueId();
					newEvent = (BaseCalendarEventEdit) m_storage.putEvent(this, id);
					newEvent.setPartial(edit);
					m_storage.commitEvent(this, newEvent);
					EventTrackingService.post(
						EventTrackingService.newEvent(EVENT_MODIFY_CALENDAR, newEvent.getReference(), true));

					// get the edit back to initial values... so only the exclusion is changed
					edit = (CalendarEventEdit) m_storage.getEvent(this, bedit.m_id);
					bedit = (BaseCalendarEventEdit) edit;

					// add an exclusion for where this one would have been %%% we are changing it, should it be immutable? -ggolden
					List exclusions = ((ExclusionSeqRecurrenceRule) bedit.getExclusionRule()).getExclusions();
					exclusions.add(new Integer(sequence));
				}

				// else change the entire sequence (i.e. the one initial event)
				else
				{
					// the time range may have been modified in the edit
					newTimeRange = bedit.m_range;

					// restore the real range, that of the base event of a sequence, if this is one of the other events in the sequence.
					bedit.m_range = bedit.m_baseRange;

					// adjust the base range if there was an edit to range
					bedit.m_range.adjust(timeRange, newTimeRange);
				}
			}

			// update the properties
			//addLiveUpdateProperties(edit.getPropertiesEdit());//%%%

			// complete the edit
			m_storage.commitEvent(this, edit);

			// track event
			Event event = EventTrackingService.newEvent(bedit.getEvent(), edit.getReference(), true);
			EventTrackingService.post(event);

			// calendar notification
			notify(event);

			// close the edit object
			bedit.closeEdit();

			// restore this one's range etc so it can be further referenced
			if (timeRange != null)
			{
				// if changing this event only
				if (intention == MOD_THIS)
				{
					// set the edit to the values of the new event
					bedit.set(newEvent);
				}

				// else we changed the sequence
				else
				{
					// move the specified range into the event's range, storing the base range
					bedit.m_baseRange = bedit.m_range;
					bedit.m_range = newTimeRange;
					bedit.m_id = '!' + bedit.m_range.toString() + '!' + sequence + '!' + bedit.m_id;
				}
			}

		} // commitEvent

		/**
		* Cancel the changes made to a CalendarEventEdit object, and release the lock.
		* The CalendarEventEdit is disabled, and not to be used after this call.
		* @param edit The CalendarEventEdit object to commit.
		*/
		public void cancelEvent(CalendarEventEdit edit)
		{
			// check for closed edit
			if (!edit.isActiveEdit())
			{
				try
				{
					throw new Exception();
				}
				catch (Exception e)
				{
					m_logger.warn(this +".cancelEvent(): closed CalendarEventEdit", e);
				}
				return;
			}

			BaseCalendarEventEdit bedit = (BaseCalendarEventEdit) edit;

			// if the id has a time range encoded, as for one of a sequence of recurring events, separate that out
			TimeRange timeRange = null;
			int sequence = 0;
			if (bedit.m_id.startsWith("!"))
			{
				String[] parts = StringUtil.split(bedit.m_id.substring(1), "!");
				try
				{
					timeRange = TimeService.newTimeRange(parts[0]);
					sequence = Integer.parseInt(parts[1]);
					bedit.m_id = parts[2];
				}
				catch (Exception ex)
				{
					m_logger.warn(this +".commitEvent: exception parsing eventId: " + bedit.m_id + " : " + ex);
				}
			}

			// release the edit lock
			m_storage.cancelEvent(this, edit);

			// close the edit object
			 ((BaseCalendarEventEdit) edit).closeEdit();

		} // cancelCalendarEvent

		/**
		* Return the extra fields kept for each event in this calendar.
		* @return the extra fields kept for each event in this calendar, formatted into a single string. %%%
		*/
		public String getEventFields()
		{
			return m_properties.getPropertyFormatted(ResourceProperties.PROP_CALENDAR_EVENT_FIELDS);

		} // getEventFields

		/**
		* Set the extra fields kept for each event in this calendar.
		* @param meta The extra fields kept for each event in this calendar, formatted into a single string. %%%
		*/
		public void setEventFields(String fields)
		{
			m_properties.addProperty(ResourceProperties.PROP_CALENDAR_EVENT_FIELDS, fields);

		} // setEventFields

		/**
		* Notify the calendar that it has changed
		* @param event The event that caused the update.
		*/
		public void notify(Event event)
		{
			// notify observers, sending the tracking event to identify the change
			setChanged();
			notifyObservers(event);

		} // notify

		/**
		* Serialize the resource into XML, adding an element to the doc under the top of the stack element.
		* @param doc The DOM doc to contain the XML (or null for a string return).
		* @param stack The DOM elements, the top of which is the containing element of the new "resource" element.
		* @return The newly added element.
		*/
		public Element toXml(Document doc, Stack stack)
		{
			Element calendar = doc.createElement("calendar");

			if (stack.isEmpty())
			{
				doc.appendChild(calendar);
			}
			else
			{
				((Element) stack.peek()).appendChild(calendar);
			}

			stack.push(calendar);

			calendar.setAttribute("context", m_context);
			calendar.setAttribute("id", m_id);

			// properties
			m_properties.toXml(doc, stack);

			stack.pop();

			return calendar;

		} // toXml

		/**
		* Find the event, in cache or info store - cache it if newly found.
		* @param eventId The id of the event.
		* @return The event, if found.
		*/
		protected CalendarEvent findEvent(String eventId)
		{
			CalendarEvent e = null;

			// if the id has a time range encoded, as for one of a sequence of recurring events, separate that out
			TimeRange timeRange = null;
			int sequence = 0;
			if (eventId.startsWith("!"))
			{
				String[] parts = StringUtil.split(eventId.substring(1), "!");
				try
				{
					timeRange = TimeService.newTimeRange(parts[0]);
					sequence = Integer.parseInt(parts[1]);
					eventId = parts[2];
				}
				catch (Exception ex)
				{
					m_logger.warn(this +".findEvent: exception parsing eventId: " + eventId + " : " + ex);
				}
			}

			// events are cached with the full reference as key
			String key = eventReference(m_context, m_id, eventId);

			// if cache is disabled, don't use it
			if ((!m_caching) || (m_calendarCache == null) || (m_calendarCache.disabled()))
			{
				// if we have "cached" the entire set of events in the thread, get that and find our message there
				List events = (List) CurrentService.getInThread(getReference()+".events");
				if (events != null)
				{
					for (Iterator i = events.iterator(); i.hasNext();)
					{
						CalendarEvent event = (CalendarEvent) i.next();
						if (event.getId().equals(eventId))
						{
							e = event;
							break;
						}
					}
				}

				if (e == null)
				{
					e = m_storage.getEvent(this, eventId);
				}
			}

			else
			{
				// find the event cache
				Cache eventCache = (Cache) m_eventCaches.get(getReference());
				if (eventCache == null)
				{
					synchronized (m_eventCaches)
					{
						// check again
						eventCache = (Cache) m_eventCaches.get(getReference());

						// if still not there, make one
						if (eventCache == null)
						{
							eventCache = m_memoryService.newCache(service(), eventReference(m_context, m_id, ""));
							m_eventCaches.put(getReference(), eventCache);
						}
					}
				}

				// if we have it cached, use it (even if it's cached as a null, a miss)
				if (eventCache.containsKey(key))
				{
					e = (CalendarEvent) eventCache.get(key);
				}

				// if not in the cache, see if we have it in our info store
				else
				{
					e = m_storage.getEvent(this, eventId);

					// if so, cache it, even misses
					eventCache.put(key, e);
				}
			}

			// now we have the primary event, if we have a recurring event sequence time range selector, use it
			if ((e != null) && (timeRange != null))
			{
				e = new BaseCalendarEventEdit(e, new RecurrenceInstance(timeRange, sequence));
			}

			return e;

		} // findEvent

		/**
		* Access the event code for this edit.
		* @return The event code for this edit.
		*/
		protected String getEvent()
		{
			return m_event;
		}

		/**
		* Set the event code for this edit.
		* @param event The event code for this edit.
		*/
		protected void setEvent(String event)
		{
			m_event = event;
		}

		/**
		* Access the resource's properties for modification
		* @return The resource's properties.
		*/
		public ResourcePropertiesEdit getPropertiesEdit()
		{
			return m_properties;

		} // getPropertiesEdit

		/**
		* Enable editing.
		*/
		protected void activate()
		{
			m_active = true;

		} // activate

		/**
		* Check to see if the edit is still active, or has already been closed.
		* @return true if the edit is active, false if it's been closed.
		*/
		public boolean isActiveEdit()
		{
			return m_active;

		} // isActiveEdit

		/**
		* Close the edit object - it cannot be used after this.
		*/
		protected void closeEdit()
		{
			m_active = false;

		} // closeEdit

		/*******************************************************************************
		* SessionStateBindingListener implementation
		*******************************************************************************/

		/**
		* Accept notification that this object has been bound as a SessionState attribute.
		* @param sessionStateKey The id of the session state which holds the attribute.
		* @param attributeName The id of the attribute to which this object is now the value.
		*/
		public void valueBound(String sessionStateKey, String attributeName)
		{
		}

		/**
		* Accept notification that this object has been removed from a SessionState attribute.
		* @param sessionStateKey The id of the session state which held the attribute.
		* @param attributeName The id of the attribute to which this object was the value.
		*/
		public void valueUnbound(String sessionStateKey, String attributeName)
		{
			if (m_logger.isDebugEnabled())
				m_logger.debug(this +".valueUnbound()");

			// catch the case where an edit was made but never resolved
			if (m_active)
			{
				cancelCalendar(this);
			}

		} // valueUnbound

	} // class BaseCalendar

	/*******************************************************************************
	* CalendarEvent implementation
	*******************************************************************************/

	public class BaseCalendarEventEdit implements CalendarEventEdit, SessionStateBindingListener
	{
		/** The calendar in which this event lives. */
		protected BaseCalendarEdit m_calendar = null;

		/** The effective time range. */
		protected TimeRange m_range = null;

		/** The base time range: for non-recurring events, this matches m_range,
		    but for recurring events, it is always the range of the initial event in the sequence (transient). */
		protected TimeRange m_baseRange = null;

		/** The recurrence rule (single rule). */
		protected RecurrenceRule m_singleRule = null;

		/** The exclusion recurrence rule. */
		protected RecurrenceRule m_exclusionRule = null;

		/** The properties. */
		protected ResourcePropertiesEdit m_properties = null;

		/** The event id. */
		protected String m_id = null;

		/** The attachments - dereferencer objects. */
		protected ReferenceVector m_attachments = null;

		/** The event code for this edit. */
		protected String m_event = null;

		/** Active flag. */
		protected boolean m_active = false;

		/**
		* Construct.
		* @param calendar The calendar in which this event lives.
		* @param id The event id, unique within the calendar.
		*/
		public BaseCalendarEventEdit(Calendar calendar, String id)
		{
			m_calendar = (BaseCalendarEdit) calendar;
			m_id = id;

			// setup for properties
			m_properties = new BaseResourcePropertiesEdit();

			// init the AttachmentContainer
			m_attachments = new ReferenceVector();

		} // BaseCalendarEventEdit

		/**
		* Construct as a copy  of another event.
		* @param other  The other event to copy.
		*/
		public BaseCalendarEventEdit(Calendar calendar, CalendarEvent other)
		{
			// store the calendar
			m_calendar = (BaseCalendarEdit) calendar;

			set(other);

		} // BaseCalendarEventEdit

		/**
		* Construct as a thin copy of another event, with this new time range, and no rules,
		* as part of a recurring event sequence.
		* @param other The other event to copy.
		* @param ri The RecurrenceInstance with the time range (and sequence number) to use.
		*/
		public BaseCalendarEventEdit(CalendarEvent other, RecurrenceInstance ri)
		{
			// store the calendar
			m_calendar = ((BaseCalendarEventEdit) other).m_calendar;

			// encode the instance and the other's id into my id
			m_id = '!' + ri.getRange().toString() + '!' + ri.getSequence() + '!' + ((BaseCalendarEventEdit) other).m_id;

			// use the new range
			m_range = (TimeRange) ri.getRange().clone();
			m_baseRange = ((BaseCalendarEventEdit) other).m_range;

			// point at the properties
			m_properties = ((BaseCalendarEventEdit) other).m_properties;

			// point at the attachments
			m_attachments = ((BaseCalendarEventEdit) other).m_attachments;

			// copy the rules
			m_singleRule = ((BaseCalendarEventEdit) other).m_singleRule;
			m_exclusionRule = ((BaseCalendarEventEdit) other).m_exclusionRule;

		} // BaseCalendarEventEdit

		/**
		* Construct from an existing definition, in xml.
		* @param calendar The calendar in which this event lives.
		* @param el The event in XML in a DOM element.
		*/
		public BaseCalendarEventEdit(Calendar calendar, Element el)
		{
			m_calendar = (BaseCalendarEdit) calendar;
			m_properties = new BaseResourcePropertiesEdit();
			m_attachments = new ReferenceVector();

			m_id = el.getAttribute("id");
			m_range = TimeService.newTimeRange(el.getAttribute("range"));

			// the children (props / attachments / rules)
			NodeList children = el.getChildNodes();
			final int length = children.getLength();
			for (int i = 0; i < length; i++)
			{
				Node child = children.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE)
				{
					Element element = (Element) child;

					// look for an attachment
					if (element.getTagName().equals("attachment"))
					{
						m_attachments.add(new Reference(element.getAttribute("relative-url")));
					}

					// look for properties
					else if (element.getTagName().equals("properties"))
					{
						// re-create properties
						m_properties = new BaseResourcePropertiesEdit(element);
					}

					// else look for rules
					else if (element.getTagName().equals("rules"))
					{
						// children are "rule" elements
						NodeList ruleChildren = element.getChildNodes();
						final int ruleChildrenLength = ruleChildren.getLength();
						for (int iRuleChildren = 0; iRuleChildren < ruleChildrenLength; iRuleChildren++)
						{
							Node ruleChildNode = ruleChildren.item(iRuleChildren);
							if (ruleChildNode.getNodeType() == Node.ELEMENT_NODE)
							{
								Element ruleChildElement = (Element) ruleChildNode;

								// look for a rule
								if (ruleChildElement.getTagName().equals("rule"))
								{
									// get the rule name - modern style encoding
									String ruleName = StringUtil.trimToNull(ruleChildElement.getAttribute("name"));

									// deal with old data
									if (ruleName == null)
									{
										try
										{
											// get the class - this is old CHEF 1.2.10 style encoding
											String ruleClassOld = ruleChildElement.getAttribute("class");

											// use the last class name minus the package
											ruleName = ruleClassOld.substring(ruleClassOld.lastIndexOf('.')+1);
										}
										catch (Throwable t)
										{
											m_logger.warn(this +": trouble loading rule: " + ruleName + " : " + t);
										}
									}
									
									// put my package on the class name
									String ruleClass = this.getClass().getPackage().getName() + "." + ruleName;

									// construct
									try
									{
										m_singleRule = (RecurrenceRule) Class.forName(ruleClass).newInstance();
										m_singleRule.set(ruleChildElement);
									}
									catch (Throwable t)
									{
										m_logger.warn(this +": trouble loading rule: " + ruleClass + " : " + t);
									}
								}

								// look for an exclusion rule
								else if (ruleChildElement.getTagName().equals("ex-rule"))
								{
									// get the rule name - modern style encoding
									String ruleName = StringUtil.trimToNull(ruleChildElement.getAttribute("name"));

									// deal with old data
									if (ruleName == null)
									{
										try
										{
											// get the class - this is old CHEF 1.2.10 style encoding
											String ruleClassOld = ruleChildElement.getAttribute("class");

											// use the last class name minus the package
											ruleName = ruleClassOld.substring(ruleClassOld.lastIndexOf('.')+1);
										}
										catch (Throwable t)
										{
											m_logger.warn(this +": trouble loading rule: " + ruleName + " : " + t);
										}
									}
									
									// put my package on the class name
									String ruleClass = this.getClass().getPackage().getName() + "." + ruleName;

									// construct
									try
									{
										m_exclusionRule = (RecurrenceRule) Class.forName(ruleClass).newInstance();
										m_exclusionRule.set(ruleChildElement);
									}
									catch (Throwable t)
									{
										m_logger.warn(this +": trouble loading rule: " + ruleClass + " : " + t);
									}
								}
							}
						}
					}
				}
			}

		} // BaseCalendarEventEdit

		/**
		* Take all values from this object.
		* @param other The other object to take values from.
		*/
		protected void set(CalendarEvent other)
		{
			// copy the id
			m_id = other.getId();

			// copy the range
			m_range = (TimeRange) other.getRange().clone();

			// copy the properties
			m_properties = new BaseResourcePropertiesEdit();
			m_properties.addAll(other.getProperties());

			// copy the attachments
			m_attachments = new ReferenceVector();
			replaceAttachments(other.getAttachments());

			// copy the rules
			// %%% deep enough? -ggolden
			m_singleRule = ((BaseCalendarEventEdit) other).m_singleRule;
			m_exclusionRule = ((BaseCalendarEventEdit) other).m_exclusionRule;

		} // set

		/**
		* Take some values from this object (not id, not rules).
		* @param other The other object to take values from.
		*/
		protected void setPartial(CalendarEvent other)
		{
			// copy the range
			m_range = (TimeRange) other.getRange().clone();

			// copy the properties
			m_properties = new BaseResourcePropertiesEdit();
			m_properties.addAll(other.getProperties());

			// copy the attachments
			m_attachments = new ReferenceVector();
			replaceAttachments(other.getAttachments());

		} // setPartial

		/**
		* Clean up.
		*/
		protected void finalize()
		{
			// catch the case where an edit was made but never resolved
			if (m_active)
			{
				m_calendar.cancelEvent(this);
			}

			m_calendar = null;

		} // finalize

		/**
		* Access the time range
		* @return The event time range
		*/
		public TimeRange getRange()
		{
			// range might be null in the creation process, before the fields are set in an edit, but
			// after the storage has registered the event and it's id.
			if (m_range == null)
			{
				return TimeService.newTimeRange(TimeService.newTime(0));
			}

			//return (TimeRange) m_range.clone();
			return m_range;
		} // getRange

		/**
		* Replace the time range
		* @param The new event time range
		*/
		public void setRange(TimeRange range)
		{
			m_range = (TimeRange) range.clone();

		} // setRange

		/**
		* Access the display name property (cover for PROP_DISPLAY_NAME).
		* @return The event's display name property.
		*/
		public String getDisplayName()
		{
			return m_properties.getPropertyFormatted(ResourceProperties.PROP_DISPLAY_NAME);

		} // getDisplayName

		/**
		* Set the display name property (cover for PROP_DISPLAY_NAME).
		* @param name The event's display name property.
		*/
		public void setDisplayName(String name)
		{
			m_properties.addProperty(ResourceProperties.PROP_DISPLAY_NAME, name);

		} // setDisplayName

		/**
		* Access the description property as plain text.
		* @return The event's description property.
		*/
		public String getDescription()
		{
			return FormattedText.convertFormattedTextToPlaintext(getDescriptionFormatted());
		}
		
		/**
		* Access the description property as formatted text.
		* @return The event's description property.
		*/
		public String getDescriptionFormatted()
		{
			// %%% JANDERSE the calendar event description can now be formatted text
			// first try to use the formatted text description; if that isn't found, use the plaintext description
			String desc = m_properties.getPropertyFormatted(ResourceProperties.PROP_DESCRIPTION+"-html");
			if (desc != null && desc.length() > 0) return desc;
			desc = m_properties.getPropertyFormatted(ResourceProperties.PROP_DESCRIPTION+"-formatted");
			desc = FormattedText.convertOldFormattedText(desc);
			if (desc != null && desc.length() > 0) return desc;
			desc = FormattedText.convertPlaintextToFormattedText(m_properties.getPropertyFormatted(ResourceProperties.PROP_DESCRIPTION));
			return desc;
		} // getDescriptionFormatted()

		
		/**
		* Set the description property as plain text.
		* @param description The event's description property.
		*/
		public void setDescription(String description)
		{
			setDescriptionFormatted(FormattedText.convertPlaintextToFormattedText(description));
		}
		
		
		/**
		* Set the description property as formatted text.
		* @param description The event's description property.
		*/
		public void setDescriptionFormatted(String description)
		{
			// %%% JANDERSE the calendar event description can now be formatted text
			// save both a formatted and a plaintext version of the description
			m_properties.addProperty(ResourceProperties.PROP_DESCRIPTION+"-html", description);
			m_properties.addProperty(ResourceProperties.PROP_DESCRIPTION, FormattedText.convertFormattedTextToPlaintext(description));
		} // setDescriptionFormatted()

		
		/**
		* Access the type (cover for PROP_CALENDAR_TYPE).
		* @return The event's type property.
		*/
		public String getType()
		{
			return m_properties.getPropertyFormatted(ResourceProperties.PROP_CALENDAR_TYPE);

		} // getType

		/**
		* Set the type (cover for PROP_CALENDAR_TYPE).
		* @param type The event's type property.
		*/
		public void setType(String type)
		{
			m_properties.addProperty(ResourceProperties.PROP_CALENDAR_TYPE, type);

		} // setType

		/**
		* Access the location (cover for PROP_CALENDAR_LOCATION).
		* @return The event's location property.
		*/
		public String getLocation()
		{
			return m_properties.getPropertyFormatted(ResourceProperties.PROP_CALENDAR_LOCATION);

		} // getLocation

		/**
		* Gets the recurrence rule, if any.
		* @return The recurrence rule, or null if none.
		*/
		public RecurrenceRule getRecurrenceRule()
		{
			return m_singleRule;

		} // getRecurrenceRule

		/**
		* Gets the exclusion recurrence rule, if any.
		* @return The exclusionrecurrence rule, or null if none.
		*/
		protected RecurrenceRule getExclusionRule()
		{
			if (m_exclusionRule == null)
				m_exclusionRule = new ExclusionSeqRecurrenceRule();

			return m_exclusionRule;

		} // getExclusionRule
		
		/*
		public RecurrenceRule getExclusionRuleII()
		{
			if (m_exclusionRule == null)
				m_exclusionRule = new ExclusionSeqRecurrenceRule();

			return m_exclusionRule;

		} // getExclusionRule
		*/

		/**
		* Return a list of all resolved events generated from this event plus it's recurrence rules
		* that fall within the time range, including this event, possibly empty.
		* @param range The time range bounds for the events returned.
		* @return a List (CalendarEvent) of all events and recurrences within the time range, including
		* this, possibly empty.
		*/
		protected List resolve(TimeRange range)
		{
			List rv = new Vector();

			// for no rules, use the event if it's in range
			if (m_singleRule == null)
			{
				// the actual event
				if (range.overlaps(getRange()))
				{
					rv.add(this);
				}
			}

			// for rules...
			else
			{
				List instances = m_singleRule.generateInstances(this.getRange(), range, TimeService.getLocalTimeZone());

				// remove any excluded
				getExclusionRule().excludeInstances(instances);

				for (Iterator iRanges = instances.iterator(); iRanges.hasNext();)
				{
					RecurrenceInstance ri = (RecurrenceInstance) iRanges.next();

					// generate an event object that is exactly like me but with this range and no rules
					CalendarEvent clone = new BaseCalendarEventEdit(this, ri);

					rv.add(clone);
				}
			}

			return rv;

		} // resolve

		/**
		* Get the value of an "extra" event field.
		* @param name The name of the field.
		* @return the value of the "extra" event field.
		*/
		public String getField(String name)
		{
			// names are prefixed to form a namespace
			name = ResourceProperties.PROP_CALENDAR_EVENT_FIELDS + "." + name;

			return m_properties.getPropertyFormatted(name);

		} // getField

		/**
		* Set the value of an "extra" event field.
		* @param name The "extra" field name
		* @param value The value to set, or null to remove the field.
		*/
		public void setField(String name, String value)
		{
			// names are prefixed to form a namespace
			name = ResourceProperties.PROP_CALENDAR_EVENT_FIELDS + "." + name;

			if (value == null)
			{
				m_properties.removeProperty(name);
			}
			else
			{
				m_properties.addProperty(name, value);
			}

		} // setField

		/**
		* Set the location (cover for PROP_CALENDAR_LOCATION).
		* @param location The event's location property.
		*/
		public void setLocation(String location)
		{
			m_properties.addProperty(ResourceProperties.PROP_CALENDAR_LOCATION, location);

		} // setLocation

		/**
		* Sets the recurrence rule.
		* @param rule The recurrence rule, or null to clear out the rule.
		*/
		public void setRecurrenceRule(RecurrenceRule rule)
		{
			m_singleRule = rule;

		} // setRecurrenceRule

		/**
		* Sets the exclusion recurrence rule.
		* @param rule The recurrence rule, or null to clear out the rule.
		*/
		protected void setExclusionRule(RecurrenceRule rule)
		{
			m_exclusionRule = rule;

		} // setExclusionRule

		/**
		* Access the id of the resource.
		* @return The id.
		*/
		public String getId()
		{
			return m_id;

		} // getId

		/**
		* Access the URL which can be used to access the resource.
		* @return The URL which can be used to access the resource.
		*/
		public String getUrl()
		{
			return m_calendar.getUrl() + getId();

		} // getUrl

		/**
		* Access the internal reference which can be used to access the resource from within the system.
		* @return The the internal reference which can be used to access the resource from within the system.
		*/
		public String getReference()
		{
			return eventReference(m_calendar.getContext(), m_calendar.getId(), getId());

		} // getReference

		/**
		* Access the event's properties.
		* @return The event's properties.
		*/
		public ResourceProperties getProperties()
		{
			return m_properties;

		} // getProperties

		/**
		* Notify the event that it has changed.
		* @param event The event that caused the update.
		*/
		public void notify(Event event)
		{
			m_calendar.notify(event);

		} // notify

		/**
		* Compare one event to another, based on range.
		* @param o The object to be compared.
		* @return A negative integer, zero, or a positive integer as this object is less than, equal to,
		* or greater than the specified object.
		*/
		public int compareTo(Object o)
		{
			if (!(o instanceof CalendarEvent))
				throw new ClassCastException();
			Time mine = getRange().firstTime();
			Time other = ((CalendarEvent) o).getRange().firstTime();

			if (mine.before(other))
				return -1;
			if (mine.after(other))
				return +1;
			return 0; // %%% perhaps check the rest of the range if the starts are the same?
		}

		/**
		* Serialize the resource into XML, adding an element to the doc under the top of the stack element.
		* @param doc The DOM doc to contain the XML (or null for a string return).
		* @param stack The DOM elements, the top of which is the containing element of the new "resource" element.
		* @return The newly added element.
		*/
		public Element toXml(Document doc, Stack stack)
		{
			Element event = doc.createElement("event");

			if (stack.isEmpty())
			{
				doc.appendChild(event);
			}
			else
			{
				((Element) stack.peek()).appendChild(event);
			}

			stack.push(event);

			event.setAttribute("id", getId());
			event.setAttribute("range", getRange().toString());

			// properties
			m_properties.toXml(doc, stack);

			if ((m_attachments != null) && (m_attachments.size() > 0))
			{
				for (int i = 0; i < m_attachments.size(); i++)
				{
					Reference attch = (Reference) m_attachments.elementAt(i);
					Element attachment = doc.createElement("attachment");
					event.appendChild(attachment);
					attachment.setAttribute("relative-url", attch.getReference());
				}
			}

			// rules
			if (m_singleRule != null)
			{
				Element rules = doc.createElement("rules");
				event.appendChild(rules);
				stack.push(rules);

				// the rule
				m_singleRule.toXml(doc, stack);

				// the exculsions
				if (m_exclusionRule != null)
				{
					m_exclusionRule.toXml(doc, stack);
				}

				stack.pop();
			}

			stack.pop();

			return event;

		} // toXml

		/**
		* Access the event code for this edit.
		* @return The event code for this edit.
		*/
		protected String getEvent()
		{
			return m_event;
		}

		/**
		* Set the event code for this edit.
		* @param event The event code for this edit.
		*/
		protected void setEvent(String event)
		{
			m_event = event;
		}

		/**
		* Access the resource's properties for modification
		* @return The resource's properties.
		*/
		public ResourcePropertiesEdit getPropertiesEdit()
		{
			return m_properties;

		} // getPropertiesEdit

		/**
		* Enable editing.
		*/
		protected void activate()
		{
			m_active = true;

		} // activate

		/**
		* Check to see if the edit is still active, or has already been closed.
		* @return true if the edit is active, false if it's been closed.
		*/
		public boolean isActiveEdit()
		{
			return m_active;

		} // isActiveEdit

		/**
		* Close the edit object - it cannot be used after this.
		*/
		protected void closeEdit()
		{
			m_active = false;

		} // closeEdit

		/*******************************************************************************
		* AttachmentContainer implementation
		*******************************************************************************/

		/**
		* Access the attachments of the event.
		* @return An copy of the set of attachments (a ReferenceVector containing Reference objects) (may be empty).
		*/
		public ReferenceVector getAttachments()
		{
			return (ReferenceVector) m_attachments.clone();

		} // getAttachments

		/**
		* Add an attachment.
		* @param ref The attachment Reference.
		*/
		public void addAttachment(Reference ref)
		{
			m_attachments.add(ref);

		} // addAttachment

		/**
		* Remove an attachment.
		* @param ref The attachment Reference to remove (the one removed will equal this, they need not be ==).
		*/
		public void removeAttachment(Reference ref)
		{
			m_attachments.remove(ref);

		} // removeAttachment

		/**
		* Replace the attachment set.
		* @param attachments A vector of Reference objects that will become the new set of attachments.
		*/
		public void replaceAttachments(ReferenceVector attachments)
		{
			m_attachments.clear();

			if (attachments != null)
			{
				Iterator it = attachments.iterator();
				while (it.hasNext())
				{
					m_attachments.add(it.next());
				}
			}

		} // replaceAttachments

		/**
		* Clear all attachments.
		*/
		public void clearAttachments()
		{
			m_attachments.clear();

		} // clearAttachments

		/*******************************************************************************
		* SessionStateBindingListener implementation
		*******************************************************************************/

		/**
		* Accept notification that this object has been bound as a SessionState attribute.
		* @param sessionStateKey The id of the session state which holds the attribute.
		* @param attributeName The id of the attribute to which this object is now the value.
		*/
		public void valueBound(String sessionStateKey, String attributeName)
		{
		}

		/**
		* Accept notification that this object has been removed from a SessionState attribute.
		* @param sessionStateKey The id of the session state which held the attribute.
		* @param attributeName The id of the attribute to which this object was the value.
		*/
		public void valueUnbound(String sessionStateKey, String attributeName)
		{
			if (m_logger.isDebugEnabled())
				m_logger.debug(this +".valueUnbound()");

			// catch the case where an edit was made but never resolved
			if (m_active)
			{
				m_calendar.cancelEvent(this);
			}

		} // valueUnbound

		/**
		* Gets the containing calendar's reference.
		* @return The containing calendar reference.
		*/
		public String getCalendarReference()
		{
			return m_calendar.getReference();

		} // 	getCalendarReference

	} // BaseCalendarEvent

	/*******************************************************************************
	* Storage implementation
	*******************************************************************************/

	protected interface Storage
	{
		/**
		* Open and read.
		*/
		public void open();

		/**
		* Write and Close.
		*/
		public void close();

		/**
		* Return the identified calendar, or null if not found.
		*/
		public Calendar getCalendar(String ref);

		/**
		* Return true if the identified calendar exists.
		*/
		public boolean checkCalendar(String ref);

		/**
		* Get a list of all calendars
		*/
		public List getCalendars();

		/**
		* Keep a new calendar.
		*/
		public CalendarEdit putCalendar(String ref);

		/**
		* Get a calendar locked for update
		*/
		public CalendarEdit editCalendar(String ref);

		/**
		* Commit a calendar edit.
		*/
		public void commitCalendar(CalendarEdit edit);

		/**
		* Cancel a calendar edit.
		*/
		public void cancelCalendar(CalendarEdit edit);

		/**
		* Forget about a calendar.
		*/
		public void removeCalendar(CalendarEdit calendar);

		/**
		* Get a event from a calendar.
		*/
		public CalendarEvent getEvent(Calendar calendar, String eventId);

		/**
		* Get a event from a calendar locked for update
		*/
		public CalendarEventEdit editEvent(Calendar calendar, String eventId);

		/**
		* Commit an edit.
		*/
		public void commitEvent(Calendar calendar, CalendarEventEdit edit);

		/**
		* Cancel an edit.
		*/
		public void cancelEvent(Calendar calendar, CalendarEventEdit edit);

		/**
		* Does this events exist in a calendar?
		*/
		public boolean checkEvent(Calendar calendar, String eventId);

		/**
		* Get the events from a calendar (within this time range, or all if null)
		*/
		public List getEvents(Calendar calendar);

		/**
		* Make and lock a new event.
		*/
		public CalendarEventEdit putEvent(Calendar calendar, String id);

		/**
		* Forget about a event.
		*/
		public void removeEvent(Calendar calendar, CalendarEventEdit edit);

	} // Storage

	/*******************************************************************************
	* CacheRefresher implementation (no container)
	*******************************************************************************/

	/**
	* Get a new value for this key whose value has already expired in the cache.
	* @param key The key whose value has expired and needs to be refreshed.
	* @param oldValue The old exipred value of the key.
	* @param event The event which triggered this refresh.
	* @return a new value for use in the cache for this key; if null, the entry will be removed.
	*/
	public Object refresh(Object key, Object oldValue, Event event)
	{
		Object rv = null;

		// key is a reference
		Reference ref = new Reference((String) key);

		// get from storage only (not cache!)

		// for events
		if (REF_TYPE_EVENT.equals(ref.getSubType()))
		{
			if (m_logger.isDebugEnabled())
				m_logger.debug(
					this
						+ ".refresh(): key "
						+ key
						+ " calendar id : "
						+ ref.getContext()
						+ "/"
						+ ref.getContainer()
						+ " event id : "
						+ ref.getId());

			// get calendar (Note: from the cache is ok)
			Calendar calendar = findCalendar(calendarReference(ref.getContext(), ref.getContainer()));

			// get the CalendarEvent (Note: not from cache! but only from storage)
			if (calendar != null)
			{
				rv = m_storage.getEvent(calendar, ref.getId());
			}
		}

		// for calendar
		else
		{
			if (m_logger.isDebugEnabled())
				m_logger.debug(this +".refresh(): key " + key + " calendar id : " + ref.getReference());

			// return the calendar (Note: not from cache! but only from storage)
			rv = m_storage.getCalendar(ref.getReference());
		}

		return rv;

	} // refresh

	/*******************************************************************************
	* StorageUser implementation
	*******************************************************************************/

	/**
	* Construct a new continer given just an id.
	* @param ref The reference for the new object.
	* @return The new containe Resource.
	*/
	public Resource newContainer(String ref)
	{
		return new BaseCalendarEdit(ref);
	}

	/**
	* Construct a new container resource, from an XML element.
	* @param element The XML.
	* @return The new container resource.
	*/
	public Resource newContainer(Element element)
	{
		return new BaseCalendarEdit(element);
	}

	/**
	* Construct a new container resource, as a copy of another
	* @param other The other contianer to copy.
	* @return The new container resource.
	*/
	public Resource newContainer(Resource other)
	{
		return new BaseCalendarEdit((Calendar) other);
	}

	/**
	* Construct a new rsource given just an id.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param id The id for the new object.
	* @param others (options) array of objects to load into the Resource's fields.
	* @return The new resource.
	*/
	public Resource newResource(Resource container, String id, Object[] others)
	{
		return new BaseCalendarEventEdit((Calendar) container, id);
	}

	/**
	* Construct a new resource, from an XML element.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param element The XML.
	* @return The new resource from the XML.
	*/
	public Resource newResource(Resource container, Element element)
	{
		return new BaseCalendarEventEdit((Calendar) container, element);
	}

	/**
	* Construct a new resource from another resource of the same type.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param other The other resource.
	* @return The new resource as a copy of the other.
	*/
	public Resource newResource(Resource container, Resource other)
	{
		return new BaseCalendarEventEdit((Calendar) container, (CalendarEvent) other);
	}

	/**
	* Construct a new continer given just an id.
	* @param ref The reference for the new object.
	* @return The new containe Resource.
	*/
	public Edit newContainerEdit(String ref)
	{
		BaseCalendarEdit rv = new BaseCalendarEdit(ref);
		rv.activate();
		return rv;
	}

	/**
	* Construct a new container resource, from an XML element.
	* @param element The XML.
	* @return The new container resource.
	*/
	public Edit newContainerEdit(Element element)
	{
		BaseCalendarEdit rv = new BaseCalendarEdit(element);
		rv.activate();
		return rv;
	}

	/**
	* Construct a new container resource, as a copy of another
	* @param other The other contianer to copy.
	* @return The new container resource.
	*/
	public Edit newContainerEdit(Resource other)
	{
		BaseCalendarEdit rv = new BaseCalendarEdit((Calendar) other);
		rv.activate();
		return rv;
	}

	/**
	* Construct a new rsource given just an id.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param id The id for the new object.
	* @param others (options) array of objects to load into the Resource's fields.
	* @return The new resource.
	*/
	public Edit newResourceEdit(Resource container, String id, Object[] others)
	{
		BaseCalendarEventEdit rv = new BaseCalendarEventEdit((Calendar) container, id);
		rv.activate();
		return rv;
	}

	/**
	* Construct a new resource, from an XML element.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param element The XML.
	* @return The new resource from the XML.
	*/
	public Edit newResourceEdit(Resource container, Element element)
	{
		BaseCalendarEventEdit rv = new BaseCalendarEventEdit((Calendar) container, element);
		rv.activate();
		return rv;
	}

	/**
	* Construct a new resource from another resource of the same type.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param other The other resource.
	* @return The new resource as a copy of the other.
	*/
	public Edit newResourceEdit(Resource container, Resource other)
	{
		BaseCalendarEventEdit rv = new BaseCalendarEventEdit((Calendar) container, (CalendarEvent) other);
		rv.activate();
		return rv;
	}

	/**
	* Collect the fields that need to be stored outside the XML (for the resource).
	* @return An array of field values to store in the record outside the XML (for the resource).
	*/
	public Object[] storageFields(Resource r)
	{
		Object[] rv = new Object[2];
		TimeRange range = ((CalendarEvent) r).getRange();
		rv[0] = range.firstTime(); // %%% fudge?
		rv[1] = range.lastTime(); // %%% fudge?

		return rv;
	}

	/**
	 * Check if this resource is in draft mode.
	 * @param r The resource.
	 * @return true if the resource is in draft mode, false if not.
	 */
	public boolean isDraft(Resource r)
	{
		return false;
	}

	/**
	 * Access the resource owner user id.
	 * @param r The resource.
	 * @return The resource owner user id.
	 */
	public String getOwnerId(Resource r)
	{
		return null;
	}

	/**
	 * Access the resource date.
	 * @param r The resource.
	 * @return The resource date.
	 */
	public Time getDate(Resource r)
	{
		return null;
	}

} // BaseCalendarService

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/calendar/BaseCalendarService.java,v 1.3 2005/05/12 01:38:27 ggolden.umich.edu Exp $
*
**********************************************************************************/
