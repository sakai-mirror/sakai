/**********************************************************************************
 *
 * $Header: /cvs/sakai2/legacy/tools/src/java/org/sakaiproject/tool/calendar/CalendarAction.java,v 1.12 2005/06/14 20:08:47 suiyy.umich.edu Exp $
 *
 ***********************************************************************************
 * *
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
package org.sakaiproject.tool.calendar;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import org.sakaiproject.api.kernel.tool.Placement;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.sakaiproject.cheftool.Context;
import org.sakaiproject.cheftool.JetspeedRunData;
import org.sakaiproject.cheftool.RunData;
import org.sakaiproject.cheftool.VelocityPortlet;
import org.sakaiproject.cheftool.VelocityPortletStateAction;
import org.sakaiproject.cheftool.menu.Menu;
import org.sakaiproject.cheftool.menu.MenuEntry;
import org.sakaiproject.cheftool.menu.MenuItem;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.ImportException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.calendar.Calendar;
import org.sakaiproject.service.legacy.calendar.CalendarEdit;
import org.sakaiproject.service.legacy.calendar.CalendarEvent;
import org.sakaiproject.service.legacy.calendar.CalendarEventEdit;
import org.sakaiproject.service.legacy.calendar.CalendarEventVector;
import org.sakaiproject.service.legacy.calendar.RecurrenceRule;
import org.sakaiproject.service.legacy.calendar.cover.CalendarImporterService;
import org.sakaiproject.service.legacy.calendar.cover.CalendarService;
import org.sakaiproject.service.legacy.content.cover.ContentHostingService;
import org.sakaiproject.service.legacy.content.cover.ContentTypeImageService;
import org.sakaiproject.service.legacy.resource.Reference;
import org.sakaiproject.service.legacy.resource.ReferenceVector;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.security.cover.SecurityService;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.TimeBreakdown;
import org.sakaiproject.service.legacy.time.TimeRange;
import org.sakaiproject.service.legacy.time.cover.TimeService;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.tool.helper.AttachmentAction;
import org.sakaiproject.tool.helper.PermissionsAction;
import org.sakaiproject.util.CalendarUtil;
import org.sakaiproject.util.FileItem;
import org.sakaiproject.util.FormattedText;
import org.sakaiproject.util.MergedList;
import org.sakaiproject.util.MergedListEntryProviderBase;
import org.sakaiproject.util.MergedListEntryProviderFixedListWrapper;
import org.sakaiproject.util.ParameterParser;
import org.sakaiproject.util.PrintFileGenerator;
import org.sakaiproject.util.java.StringUtil;


/**
 * The schedule tool.
 *
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision$
 */
public class CalendarAction
extends VelocityPortletStateAction
{
	/** Resource bundle using current language locale */
    private static ResourceBundle rb = ResourceBundle.getBundle("calendar");
	
	private static final String CONFIRM_IMPORT_WIZARD_STATE = "CONFIRM_IMPORT";
	private static final String WIZARD_IMPORT_FILE = "importFile";
	private static final String GENERIC_SELECT_FILE_IMPORT_WIZARD_STATE = "GENERIC_SELECT_FILE";
	private static final String OTHER_SELECT_FILE_IMPORT_WIZARD_STATE = "OTHER_SELECT_FILE";
	private static final String WIZARD_IMPORT_TYPE = "importType";
	private static final String SELECT_TYPE_IMPORT_WIZARD_STATE = "SELECT_TYPE";
	private static final String IMPORT_WIZARD_SELECT_TYPE_STATE = SELECT_TYPE_IMPORT_WIZARD_STATE;
	private static final String STATE_SCHEDULE_IMPORT = "scheduleImport";
	private static final String CALENDAR_INIT_PARAMETER = "calendar";
	private static final int HOURS_PER_DAY = 24;
	private static final int NUMBER_HOURS_PER_PAGE_FOR_WEEK_VIEW = 10;

	private static final int FIRST_PAGE_START_HOUR = 0;
	private static final int SECOND_PAGE_START_HOUR = 8;
	private static final int THIRD_PAGE_START_HOUR = 14;
	
	private static final String MERGE_SCHEDULE_BUTTON_CAPTION = rb.getString("java.merge");//"Merge...";
	private static final String MERGE_SCHEDULE_BUTTON_HANDLER = "doMerge";
	private static final String STATE_YEAR = "calYear";
	private static final String STATE_MONTH = "calMonth";
	private static final String STATE_DAY = "calDay";
	
	private static final String STATE_REVISE = "revise";
	
	private static final String STATE_SET_FREQUENCY = "setFrequency";
	private static final String FREQUENCY_SELECT = "frequencySelect";
	private static final String DEFAULT_FREQ = "once";
	private static final String FREQ_ONCE = "once";
	
	private static final String SSTATE__RECURRING_RULE = "rule";
	private static final String STATE_BEFORE_SET_RECURRENCE = "state_before_set_recurrence";
	
	private static final String ALERT_ENDING_EARLIER_THAN_STARTING = rb.getString("java.theend");//"The ending date is earlier than the starting date. Change it please.";
	
	// Name used in the velocity template for the list of merged/non-merged calendars
	private final static String MERGED_CALENDARS_COLLECTION = "mergedCalendarsCollection";
	
	private final static String TIME_FILTER_OPTION_VAR = "timeFilterOption";
	private final static String TIME_FILTER_SETTING_CUSTOM_START_DATE_VAR = "customStartDate";
	private final static String TIME_FILTER_SETTING_CUSTOM_END_DATE_VAR = "customEndDate";
	private final static String TIME_FILTER_SETTING_CUSTOM_START_YEAR = "customStartYear";
	private final static String TIME_FILTER_SETTING_CUSTOM_END_YEAR = "customEndYear";
	private final static String TIME_FILTER_SETTING_CUSTOM_START_MONTH = "customStartMonth";
	private final static String TIME_FILTER_SETTING_CUSTOM_END_MONTH = "customEndMonth";
	private final static String TIME_FILTER_SETTING_CUSTOM_START_DAY = "customStartDay";
	private final static String TIME_FILTER_SETTING_CUSTOM_END_DAY = "customEndDay";
	
	/** state selected view */
	private static final String STATE_SELECTED_VIEW = "state_selected_view";
	private static final String VIEW_BY_DAY = rb.getString("java.byday");//"Calendar by day";
	private static final String VIEW_BY_WEEK = rb.getString("java.byweek");//"Calendar by week";
	private static final String VIEW_BY_MONTH = rb.getString("java.bymonth");//"Calendar by month";
	private static final String VIEW_BY_YEAR = rb.getString("java.byyear");//"Calendar by year";
	private static final String VIEW_LIST = rb.getString("java.listeve");//"List of events";
	
	/** DELIMETER used to separate the list of custom fields for this calendar. */
	private final static String ADDFIELDS_DELIMITER = "_,_";

	protected final static String STATE_INITED = "calendar.state.inited";
	
	/** for sorting in list view */
	private static final String STATE_DATE_SORT_ASC = "dateSortedAsc";

	/**
	 * Used by callback to convert channel references to channels.
	 */
	private final class CalendarReferenceToChannelConverter implements MergedListEntryProviderFixedListWrapper.ReferenceToChannelConverter
    {
        public Object getChannel(String channelReference)
        {
            try
            {
                return CalendarService.getCalendar(channelReference); 
            }
            catch (IdUnusedException e)
            {
                return null;
            }
            catch (PermissionException e)
            {
                return null;
            }
        }
    }

	/*
	 * Callback class so that we can form references in a generic way.
	 */
	private final class CalendarChannelReferenceMaker implements MergedList.ChannelReferenceMaker
    {
        public String makeReference(String siteId)
        {
			return CalendarService.calendarReference(siteId, SiteService.MAIN_CONTAINER);
        }
    }
	
	/**
	 * Converts a string that is used to store additional attribute fields to an array of strings.
	 */
	private String[] fieldStringToArray(String addfields_str, String delimiter)
	{
		String [] fields = addfields_str.split(delimiter);
		List destStringList = new ArrayList();
			
		// Don't copy empty fields.
		for ( int i=0; i < fields.length; i++)
		{
			if ( fields[i].length() > 0 )
			{
				destStringList.add(fields[i]);
			}
		}
			
		return (String[]) destStringList.toArray(new String[destStringList.size()]);
	}
	
	/**
	 * Enable or disable the observer
	 * @param enable if true, the observer is enabled, if false, it is disabled
	 */
	protected void enableObserver(SessionState sstate, boolean enable)
	{
		if (enable)
		{
			enableObservers(sstate);
		}
		else
		{
			disableObservers(sstate);
		}
	}
	
	// myYear class
	public class MyYear
	{
		private MyMonth[][] yearArray;
		
		private int year;
		private MyMonth m;
		
		public MyYear()
		{
			yearArray = new MyMonth[4][3];
			m = null;
			year = 0;
		}
		
		public void setMonth(MyMonth m, int x, int y)
		{
			
			yearArray[x][y] = m;
			
		}
		
		
		public MyMonth getMonth(int x, int y)
		{
			m = yearArray[x][y];
			return (m);
		}
		
		public void setYear(int y)
		{
			year = y;
		}
		
		public int getYear()
		{
			return year;
		}
		
		
	}// myYear class
	
	// my week
	public class MyWeek
	{
		private MyDate[] week;
		private int weekOfMonth;
		
		public MyWeek()
		{
			week = new MyDate[7];
			weekOfMonth = 0;
		}
		
		public void setWeek(int i, MyDate date)
		{
			week[i] = date;
		}
		
		public MyDate getWeek(int i)
		{
			return week[i];
		}
		
		public String getWeekRange()
		{
			String range = null;
			range =  week[0].getTodayDate() + "   "+ "-" + " " + week[6].getTodayDate();
			return range;
		}
		
		public void setWeekOfMonth(int w)
		{
			weekOfMonth = w;
		}
		
		public int getWeekOfMonth()
		{
			return weekOfMonth+1;
		}
		
		
		
	}
	
	// myMonth class
	public class MyMonth
	{
		private MyDate[][] monthArray;
		// private myDate[] monthArray;
		private MyDate result;
		private String monthName;
		private int month;
		private int row;
		private int numberOfDaysInMonth;
		
		public MyMonth()
		{
			result = null;
			monthArray = new MyDate[6][7];
			//monthArray = new myDate[43];
			month = 0;
			row = 0;
			numberOfDaysInMonth=0;
		}
		
		public void setRow(int r)
		{
			row = r;
		}
		
		public int getRow()
		{
			return row;
		}
		
		public void setNumberOfDaysInMonth(int daysInMonth)
		{
			numberOfDaysInMonth = daysInMonth;
		}
		
		public int getNumberOfDaysInMonth()
		{
			return numberOfDaysInMonth;
		}
		
		public void setDay(MyDate d,int x, int y)
		{
			monthArray[x][y] = d;
			//monthArray[x] = d;
		}
		
		public MyDate getDay(int x,int y)
		{
			result = monthArray[x][y];
			//result = monthArray[x];
			return (result);
		}
		
		public void setMonthName(String name)
		{
			monthName = name;
		}
		
		public String getMonthName()
		{
			return monthName;
		}
		
		public void setMonth(int m)
		{
			month = m;
		}
		
		public int getMonth()
		{
			return month;
		}
		
	}// myMonth
	
	// myDay class
	public class MyDay
	{
		private String m_data;  // data will have the days in the month
		private String m_attachment_data;   // data need to be displayed and attached, currently
		// this si a string and it can be any structure in the future.
		private int m_flag; // 0 if it is not a current date , 1 if it is a current date
		private int day;
		private int year;
		private int month;
		private String dayName; // name for each day
		private String todayDate;
		
		public MyDay()
		{
			m_data = "";
			m_flag = 0;
			m_attachment_data = "";
			day = 0;
			dayName = "";
			todayDate = "";
		}
		
		
		public void setDay(int d)
		{
			day = d;
		}
		
		public int getDay()
		{
			return day;
		}
		
		public void setFlag(int flag)
		{
			m_flag = flag;
		}
		
		
		public void setData(String data)
		{
			m_data = data;
		}
		
		
		public int getFlag()
		{
			return m_flag;
		}
		
		
		public String getData()
		{
			return(m_data);
		}
		
		
		public void setAttachment(String data)
		{
			m_attachment_data = data;
		}
		
		
		public String getAttachment()
		{
			return(m_attachment_data);
		}
		
		public void setDayName(String dname)
		{
			dayName = dname;
		}
		
		public String getDayName()
		{
			return dayName;
		}
		
		
		public void setTodayDate(String date)
		{
			todayDate = date;
		}
		
		public String getTodayDate()
		{
			return todayDate;
		}
		
		public void setYear(int y)
		{
			year = y;
		}
		
		public int getYear()
		{
			return year;
		}
		
		public void setMonth(int m)
		{
			month = m;
		}
		
		public int getMonth()
		{
			return month;
		}
	}// myDay class
	
	public class EventClass
	{
		private String displayName;
		private long firstTime;
		private String eventId;
		
		public EventClass()
		{
			displayName = "";
			firstTime = 0;
		}
		
		public void setDisplayName(String name)
		{
			displayName = name;
		}
		public void setFirstTime(long time)
		{
			firstTime = time;
		}
		
		public String getDisplayName()
		{
			return displayName;
		}
		
		public long getfirstTime()
		{
			return firstTime;
		}
		
		public void setId(String id)
		{
			eventId = id;
		}
		
		public String getId()
		{
			return eventId;
		}
	}
	
	public class EventDisplayClass
	{
		private CalendarEvent calendareventobj;
		private boolean eventConflict;
		private int eventPosition;
		
		public EventDisplayClass()
		{
			
			eventConflict = false;
			calendareventobj = null;
			eventPosition = 0;
		}
		
		
		public void setEvent(CalendarEvent ce, boolean eventconf, int pos)
		{
			eventConflict = eventconf;
			calendareventobj = ce;
			eventPosition = pos;
			
		}
		
		
		public void setFlag(boolean conflict)
		{
			eventConflict = conflict;
		}
		
		public void setPosition(int position)
		{
			eventPosition = position;
		}
		
		
		public int getPosition()
		{
			return eventPosition;
		}
		
		public CalendarEvent getEvent()
		{
			return calendareventobj;
		}
		
		public boolean getFlag()
		{
			return eventConflict;
		}
		
		
		
	}
	
	public class MyDate
	{
		private MyDay day = null;
		private MyMonth month = null;
		private MyYear year = null;
		private String dayName = "";
		private Iterator iteratorObj = null;
		private int flag = -1;
		private Vector eVector;
		
		
		
		public MyDate()
		{
			day = new MyDay();
			month = new MyMonth();
			year = new MyYear();
		}
		
		public void setTodayDate(int m, int d, int y)
		{
			day.setDay(d);
			month.setMonth(m);
			year.setYear(y);
		}
		
		
		public void setNumberOfDaysInMonth(int daysInMonth)
		{
			month.setNumberOfDaysInMonth(daysInMonth);
		}
		
		
		public int getNumberOfDaysInMonth()
		{
			return month.getNumberOfDaysInMonth();
		}
		
		
		public String getTodayDate()
		{
			String date = month.getMonth() + "/" + day.getDay() + "/" + year.getYear();
			return date;
		}
		
		public void setFlag(int i)
		{
			flag = i;
		}
		
		public int getFlag()
		{
			return flag;
		}
		
		public void setDayName(String name)
		{
			dayName = name;
		}
		
		public void setNameOfMonth(String name)
		{
			month.setMonthName(name);
		}
		
		public String getDayName()
		{
			return dayName;
		}
		
		public int getDay()
		{
			return day.getDay();
		}
		
		public int getMonth()
		{
			return month.getMonth();
		}
		
		public String getNameOfMonth()
		{
			return month.getMonthName();
		}
		
		public int getYear()
		{
			return year.getYear();
		}
		
		public void setEventBerWeek(Vector eventVector)
		{
			
			eVector = eventVector;
		}
		
		public void setEventBerDay(Vector eventVector)
		{
			
			eVector = eventVector;
		}
		
		public Vector getEventsBerDay(int index)
		{
			Vector dayVector = new Vector();

			if (eVector != null)
				dayVector = (Vector)eVector.get(index);

			if (dayVector == null)
				dayVector = new Vector();
			
			return dayVector;

		}
		
		
		public Vector getEventsBerWeek(int index)
		{
			Vector dayVector = new Vector();
			if (eVector != null)
				dayVector = (Vector)eVector.get(index);
				
			if (dayVector == null)
				dayVector = new Vector();
				
			return dayVector;
		}
		
		
		public void setEvents(Iterator t)
		{
			iteratorObj = t;
		}
		
		
		public Vector getEvents()
		{
			Vector vectorObj = new Vector();
			int i = 0;
			if (iteratorObj!=null)
			{
				while(iteratorObj.hasNext())
				{
					vectorObj.add(i,iteratorObj.next());
					i++;
				}
			}
			return vectorObj;
		}
		
	}
	
	public class Helper
	{
		private int numberOfActivity =0;
		
		
		public int getduration(long x, int b)
		{
			
			Long l = new Long(x);
			int v = l.intValue()/3600000;
			return v;
		}
		
		
		public int getFractionIn(long x,int b)
		{
			Long ll = new Long(x);
			int y = (ll.intValue()-(b*3600000));
			int m = (y/60000);
			return m;
		}
		
		
		public CalendarEvent getActivity(Vector mm)
		{
			int size = mm.size();
			numberOfActivity = size;
			
			CalendarEvent activityEvent,event=null;
			
			if(size>0)
			{
				activityEvent = (CalendarEvent)mm.elementAt(0);
				long temp = activityEvent.getRange().duration();
				for(int i =0; i<size;i++)
				{
					activityEvent = (CalendarEvent)mm.elementAt(i);
					if(temp<activityEvent.getRange().duration())
					{
						temp = activityEvent.getRange().duration();
						event = activityEvent;
					}
				}
			}
			else
				event = null;
			
			return event;
		}
		
		
		public int getNumberOfActivity()
		{
			return numberOfActivity;
		}
		
		public int getInt(long x)
		{
			Long temp = new Long(x);
			return(temp.intValue());
		}
	}
	
	/**
	 * Given a current date via the calendarUtil paramter, returns a TimeRange for the week.
	 */
	public TimeRange getWeekTimeRange(
	CalendarUtil calendarUtil)
	{
		int dayofweek = 0;
		
		
		dayofweek = calendarUtil.getDay_Of_Week()-1;
		int tempCurrentYear = calendarUtil.getYear();
		int tempCurrentMonth = calendarUtil.getMonthInteger();
		int tempCurrentDay = calendarUtil.getDayOfMonth();
		
		for(int i = dayofweek; i>0;i--)
		{
			calendarUtil.prevDate();
		}
		
		Time startTime = TimeService.newTimeLocal(calendarUtil.getYear(),calendarUtil.getMonthInteger(),calendarUtil.getDayOfMonth(),00,00,00,000);
		
		calendarUtil.setDay(tempCurrentYear,tempCurrentMonth,tempCurrentDay);
		dayofweek = calendarUtil.getDay_Of_Week();
		
		if (dayofweek< 7)
		{
			for(int i = dayofweek; i<=6;i++)
			{
				calendarUtil.nextDate();
			}
		}
		
		Time endTime = TimeService.newTimeLocal(calendarUtil.getYear(),calendarUtil.getMonthInteger(),calendarUtil.getDayOfMonth(),23,00,00,000);
		
		return TimeService.newTimeRange(startTime,endTime,true,true);
		
	} // etWeekTimeRange
	
	/**
	 * Given a current date via the calendarUtil paramter, returns a TimeRange for the month.
	 */
	public TimeRange getMonthTimeRange(CalendarUtil calendarUtil)
	{
		
		int dayofweek = 0;
		
		calendarUtil.setDay(calendarUtil.getYear(), calendarUtil.getMonthInteger(), 1);
		int numberOfCurrentDays = calendarUtil.getNumberOfDays();
		int tempCurrentMonth = calendarUtil.getMonthInteger();
		int tempCurrentYear = calendarUtil.getYear();
		
		// get the index of the first day in the month
		int firstDay_of_Month = calendarUtil.getDay_Of_Week() - 1;
		
		// Construct the time range to get all the days in the current month plus the days in the first week in the previous month and
		// the days in the last week from the last month
		
		// get the days in the first week that exists in the prev month
		for(int i = firstDay_of_Month; i>0;i--)
		{
			calendarUtil.getPrevDate();
		}
		
		Time startTime = TimeService.newTimeLocal(calendarUtil.getYear(),calendarUtil.getMonthInteger(),calendarUtil.getDayOfMonth(),00,00,00,000);
		
		// set the date object to the current month and last day in the current month
		calendarUtil.setDay(tempCurrentYear,tempCurrentMonth,numberOfCurrentDays);
		
		// get the index of the last day in the current month
		dayofweek = calendarUtil.getDay_Of_Week();
		
		// move the date object to the last day in the last week of the current month , this day will be one of those days in the
		// following month
		if (dayofweek < 7)
		{
			for(int i = dayofweek; i<=6;i++)
			{
				calendarUtil.nextDate();
			}
		}
		
		
		Time endTime = TimeService.newTimeLocal(calendarUtil.getYear(),calendarUtil.getMonthInteger(),calendarUtil.getDayOfMonth(),23,00,00,000);
		return TimeService.newTimeRange(startTime,endTime,true,true);
	}
	
	/**
	 * Given a current date in the year, month, and day parameters, returns a TimeRange for the day.
	 */
	public TimeRange getDayTimeRange(
	int year,
	int month,
	int day)
	{
		Time startTime = TimeService.newTimeLocal(year,month,day,00,00,00,000);
		Time endTime = TimeService.newTimeLocal(year,month,day,23,00,00,000);
		
		return TimeService.newTimeRange(startTime,endTime,true,true);
	}
	
	
	/**
	 * Provides a list of merged calendars by iterating through all
	 * available calendars.
	 */
	class EntryProvider extends MergedListEntryProviderBase
	{
		/* (non-Javadoc)
		 * @see org.sakaiproject.util.MergedListEntryProviderBase#makeReference(java.lang.String)
		 */
		public Object makeObjectFromSiteId(String id)
		{
			String calendarReference = CalendarService.calendarReference(id, SiteService.MAIN_CONTAINER);
			Object calendar = null;
			
			if ( calendarReference != null )
			{
			    try
                {
			        calendar = CalendarService.getCalendar(calendarReference);
                }
                catch (IdUnusedException e)
                {
                    // The channel isn't there.
                }
                catch (PermissionException e)
                {
                    // We can't see the channel
                }			    
			}
			
			return calendar;
		}

		/* (non-Javadoc)
		 * @see org.chefproject.actions.MergedEntryList.EntryProvider#allowGet(java.lang.Object)
		 */
		public boolean allowGet(String ref)
		{
			return CalendarService.allowGetCalendar(ref);
		}
		
		/* (non-Javadoc)
		 * @see org.chefproject.actions.MergedEntryList.EntryProvider#getContext(java.lang.Object)
		 */
		public String getContext(Object obj)
		{
			if ( obj == null )
			{
			    return "";
			}

			Calendar calendar = (Calendar)obj;
			return calendar.getContext();
		}
		
		/* (non-Javadoc)
		 * @see org.chefproject.actions.MergedEntryList.EntryProvider#getReference(java.lang.Object)
		 */
		public String getReference(Object obj)
		{
			if ( obj == null )
		    {
			    return "";
		    }
			
		    Calendar calendar = (Calendar)obj;
			return calendar.getReference();
		}
		
		/* (non-Javadoc)
		 * @see org.chefproject.actions.MergedEntryList.EntryProvider#getProperties(java.lang.Object)
		 */
		public ResourceProperties getProperties(Object obj)
		{
			if ( obj == null )
		    {
			    return null;
		    }

			Calendar calendar = (Calendar)obj;
			return calendar.getProperties();
		}
	}
	


	/**
	 * This class controls the page that allows the user to customize which
	 * calendars will be merged with the current group.
	 */
	public class MergePage
	{
		private final String mergeScheduleButtonCaption;
		private final String mergeScheduleButtonHandler;
		
		// Name used in the velocity template for the list of merged/non-merged calendars
		private final String mergedCalendarsCollection;
		
		
		public MergePage(String mergeButtonCaption, String mergeButtonHandler, String mergedListVelocityName)
		{
			super();
			this.mergeScheduleButtonCaption = mergeButtonCaption;
			this.mergeScheduleButtonHandler = mergeButtonHandler;
			this.mergedCalendarsCollection = mergedListVelocityName;
		}
		
		/**
		 * Build the context for showing merged view
		 */
		public void buildContext(
		VelocityPortlet portlet,
		Context context,
		RunData runData,
		CalendarActionState state,
		SessionState sstate)
		{			
			MergedList calendarList = new MergedList();
			
			EntryProvider entryProvider = new EntryProvider();
			
			calendarList.loadChannelsFromDelimitedString(isOnWorkspaceTab(),
                    entryProvider,
                    StringUtil.trimToZero(UsageSessionService.getSessionUserId()),

                    calendarList.getChannelReferenceArrayFromDelimitedString(state
                            .getPrimaryCalendarReference(), portlet
                            .getPortletConfig().getInitParameter(
                                    PORTLET_CONFIG_PARM_MERGED_CALENDARS)),
                    SecurityService.isSuperUser(), PortalService
                            .getCurrentSiteId());
			
			// Place this object in the context so that the velocity template
			// can get at it.
			context.put(mergedCalendarsCollection, calendarList);
			context.put("tlang",rb);
			sstate.setAttribute(
			CalendarAction.SSTATE_ATTRIBUTE_MERGED_CALENDARS,
			calendarList);
		}
		
		/**
		 * Action is used when the docancel is requested when the user click on cancel  in the new view
		 */
		public void doCancel(
		RunData data,
		Context context,
		CalendarActionState state,
		SessionState sstate)
		{
			// Go back to whatever state we were in beforehand.
			state.setReturnState(state.getPrevState());
			
			// cancel the options, release the site lock, cleanup
			cancelOptions();
			
			// Clear the previous state so that we don't get confused elsewhere.
			state.setPrevState("");
			
			sstate.removeAttribute(STATE_MODE);
			
			enableObserver(sstate, true);
		} // doCancel
		
		/**
		 * Handle the "Merge" button on the toolbar
		 */
		public void doMerge(
		RunData runData,
		Context context,
		CalendarActionState state,
		SessionState sstate)
		{
			// TODO: really?
			// get a lock on the site and setup for options work
//			VelocityPortletPaneledAlert alert = new VelocityPortletPaneledAlert();
//			alert.doOptions(runData, context);
			doOptions(runData, context);
			
			// if we didn't end up in options mode, bail out
			if (!MODE_OPTIONS.equals(sstate.getAttribute(STATE_MODE))) return;
			
			// Disable the observer
			enableObserver(sstate, false);
			
			// Save the previous state so that we can get to it after we're done with the options mode.
			//state.setPrevState(state.getState());
			// Save the previous state so that we can get to it after we're done with the options mode.
			// if the previous state is Description, we need to remember one more step back
			// coz there is a back link in description view
			if ((state.getState()).equalsIgnoreCase("description"))
			{
				state.setPrevState(state.getReturnState() + "!!!fromDescription");
			}
			else
			{
				state.setPrevState(state.getState());
			}
			
			state.setState(CalendarAction.STATE_MERGE_CALENDARS);
		} // doMerge
		
		/**
		 * Handles the user clicking on the save button on the page to specify which
		 * calendars will be merged into the present schedule.
		 */
		public void doUpdate(
		RunData runData,
		Context context,
		CalendarActionState state,
		SessionState sstate)
		{
			// Get the merged calendar list out of our session state
			MergedList mergedCalendarList =
			(MergedList) sstate.getAttribute(
			CalendarAction.SSTATE_ATTRIBUTE_MERGED_CALENDARS);
			
			if (mergedCalendarList != null)
			{
				// Get the information from the run data and load it into
				// our calendar list that we have in the session state.
				mergedCalendarList.loadFromRunData(runData.getParameters());
			}
			
			// update the tool config
			Placement placement = ToolManager.getCurrentPlacement();
			if (mergedCalendarList != null)
			{
				placement.getPlacementConfig().setProperty(
				PORTLET_CONFIG_PARM_MERGED_CALENDARS,
				mergedCalendarList.getDelimitedChannelReferenceString());
			}
			else
			{
				placement.getPlacementConfig().remove(PORTLET_CONFIG_PARM_MERGED_CALENDARS);
			}
			
			// commit the change
			saveOptions();
			
			updateObservationOfChannel(mergedCalendarList, runData, sstate, state);

			// Turn the observer back on.
			enableObserver(sstate, true);
			
			// Go back to whatever state we were in beforehand.
			state.setReturnState(state.getPrevState());
			
			// Clear the previous state so that we don't get confused elsewhere.
			state.setPrevState("");
			
			sstate.removeAttribute(STATE_MODE);
			
		} // doUpdate
		
		/* (non-Javadoc)
		 * @see org.chefproject.actions.schedulePages.SchedulePage#getMenuHandlerID()
		 */
		public String getButtonHandlerID()
		{
			return mergeScheduleButtonHandler;
		}
		
		/* (non-Javadoc)
		 * @see org.chefproject.actions.schedulePages.SchedulePage#getMenuText()
		 */
		public String getButtonText()
		{
			return mergeScheduleButtonCaption;
		}
		
	}
	
	
	/**
	 * This class controls the page that allows the user to add arbitrary
	 * attributes to the attribute list for the primary calendar that
	 * corresponds to the current group.
	 */
	public class CustomizeCalendarPage
	{
		
		//This is the session attribute name to store init and current addFields list
		
		// Name used in the velocity template for the list of calendar addFields
		private final static String ADDFIELDS_CALENDARS_COLLECTION = "addFieldsCalendarsCollection";
		private final static String ADDFIELDS_CALENDARS_COLLECTION_ISEMPTY = "addFieldsCalendarsCollectionIsEmpty";
		private final String OPTIONS_BUTTON_CAPTION = rb.getString("java.fields");
		private final static String OPTIONS_BUTTON_HANDLER = "doCustomize";
		
		
		
		public CustomizeCalendarPage()
		{
			super();
		}
		
		/**
		 * Build the context for addfields calendar (Options menu)
		 */
		public void buildContext(
		VelocityPortlet portlet,
		Context context,
		RunData runData,
		CalendarActionState state,
		SessionState sstate)
		{
			String[] addFieldsCalendarArray = null;
			
			// Get a list of current calendar addFields.  This is a comma-delimited list.
			if (sstate.getAttribute(CalendarAction.SSTATE_ATTRIBUTE_ADDFIELDS_PAGE).toString().equals(CalendarAction.PAGE_MAIN))  //when the 'Options' button click
			{
				//when the 'Options' button click
				
				Calendar calendarObj = null;
				
				StringBuffer exceptionMessage = new StringBuffer();
				
				String calId = state.getPrimaryCalendarReference();
				try
				{
					calendarObj = CalendarService.getCalendar(calId);
				}
				catch (IdUnusedException e)
				{
					exceptionMessage.append(rb.getString("java.alert.thereis"));
					Log.debug("chef", this +".buildCustomizeContext(): " + e);
				}
				catch (PermissionException e)
				{
					exceptionMessage.append(rb.getString("java.alert.youdont"));
					Log.debug("chef", this +".buildCustomizeContext(): " + e);
				}
				
				// Get a current list of add fields.  This is a comma-delimited string.
				String addfieldsCalendars = calendarObj.getEventFields();
				
				if (addfieldsCalendars != null)
				{
					addFieldsCalendarArray =
					fieldStringToArray(
					addfieldsCalendars,
					ADDFIELDS_DELIMITER);
				}
				
				sstate.setAttribute(CalendarAction.SSTATE_ATTRIBUTE_ADDFIELDS_CALENDARS_INIT, addfieldsCalendars);
				sstate.setAttribute(CalendarAction.SSTATE_ATTRIBUTE_ADDFIELDS_CALENDARS, addfieldsCalendars);
				
				sstate.setAttribute(CalendarAction.SSTATE_ATTRIBUTE_DELFIELDS_CONFIRM, "N");
				state.setDelfieldAlertOff(true);
			}
			else //after the 'Options' button click
			{
				String addFieldsCollection = (String) sstate.getAttribute(CalendarAction.SSTATE_ATTRIBUTE_ADDFIELDS_CALENDARS);
				
				if (addFieldsCollection != null)
					addFieldsCalendarArray = fieldStringToArray(addFieldsCollection, ADDFIELDS_DELIMITER);
			}
			
			// Place this object in the context so that the velocity template
			// can get at it.
			context.put(ADDFIELDS_CALENDARS_COLLECTION, addFieldsCalendarArray);
			context.put("tlang",rb);
			if (addFieldsCalendarArray == null)
				context.put(ADDFIELDS_CALENDARS_COLLECTION_ISEMPTY, Boolean.valueOf(true));
			else
				context.put(ADDFIELDS_CALENDARS_COLLECTION_ISEMPTY, Boolean.valueOf(false));
			
		} //buildCustomizeCalendarContext
		
		/**
		 * Handles the click on the page to add a field to events that will
		 * be added to the calendar. Changes aren't complete until the user
		 * commits changes with a save.
		 */
		public void doAddfield(
		RunData runData,
		Context context,
		CalendarActionState state,
		SessionState sstate)
		{
			String addFields = (String) sstate.getAttribute(CalendarAction.SSTATE_ATTRIBUTE_ADDFIELDS_CALENDARS);
			String [] addFieldsCalendarList = null;
			
			if (addFields != null)
				addFieldsCalendarList = fieldStringToArray(addFields,ADDFIELDS_DELIMITER);
			
			// Go back to whatever state we were in beforehand.
			state.setReturnState(state.getPrevState());
			
			enableObserver(sstate, true);
			
			String addField = "";
			addField = runData.getParameters().getString("textfield").trim();
			String dupAddfield = "N";
			
			//prevent entry of some characters (can cause problem)
			addField = addField.replaceAll("  "," ");
			addField = addField.replaceAll("'","");
			addField = addField.replaceAll("\"","");
			
			if (addField.length()==0)
			{
				addAlert(sstate, rb.getString("java.alert.youneed"));
			}
			else
			{
				if (addFieldsCalendarList != null)
				{
					for (int i=0; i < addFieldsCalendarList.length; i++)
					{
						if (addField.toUpperCase().equals(addFieldsCalendarList[i].toUpperCase()))
						{
							addAlert(sstate, rb.getString("java.alert.theadd"));
							dupAddfield = "Y";
							i = addFieldsCalendarList.length + 1;
						}
					}
					if (dupAddfield.equals("N"))
						addFieldsCalendarList = fieldStringToArray(addFields+ADDFIELDS_DELIMITER+addField, ADDFIELDS_DELIMITER);
				}
				else
				{
					String [] initString = new String[1];
					initString[0] = addField;
					addFieldsCalendarList = initString;
					
				}
				
				if (dupAddfield.equals("N"))
				{
					if (addFields != null)
						addFields = addFields + ADDFIELDS_DELIMITER + addField;
					else
						addFields = addField;
					
					sstate.setAttribute(CalendarAction.SSTATE_ATTRIBUTE_ADDFIELDS_CALENDARS, addFields);
				}
			}
			
			sstate.setAttribute(CalendarAction.SSTATE_ATTRIBUTE_ADDFIELDS_PAGE, CalendarAction.PAGE_ADDFIELDS);
			
		}
		
		/**
		 * Handles a click on the cancel button in the page that allows the
		 * user to add/remove events to/from events that will be added to
		 * the calendar.
		 */
		public void doCancel(
		RunData data,
		Context context,
		CalendarActionState state,
		SessionState sstate)
		{
			// Go back to whatever state we were in beforehand.
			state.setReturnState(state.getPrevState());
			
			sstate.setAttribute(CalendarAction.SSTATE_ATTRIBUTE_ADDFIELDS_CALENDARS, sstate.getAttribute(CalendarAction.SSTATE_ATTRIBUTE_ADDFIELDS_CALENDARS_INIT));
			sstate.setAttribute(CalendarAction.SSTATE_ATTRIBUTE_ADDFIELDS_PAGE, CalendarAction.PAGE_MAIN);
			enableObserver(sstate, true);
		} // doCancel
		
		/**
		 * This initiates the page where the user can add/remove additional
		 * properties to/from events that will be added to the calendar.
		 */
		public void doCustomize(
		RunData runData,
		Context context,
		CalendarActionState state,
		SessionState sstate)
		{
			// Disable the observer
			enableObserver(sstate, false);
			
			// Save the previous state so that we can get to it after we're done with the options mode.
			// if the previous state is Description, we need to remember one more step back
			// coz there is a back link in description view
			if ((state.getState()).equalsIgnoreCase("description"))
			{
				state.setPrevState(state.getReturnState() + "!!!fromDescription");
			}
			else
			{
				state.setPrevState(state.getState());
			}
			
			state.setState(CalendarAction.STATE_CUSTOMIZE_CALENDAR);
		}
		
		/**
		 * Handles the click on the page to remove a field from events in the
		 * calendar. Changes aren't complete until the user commits changes
		 * with a save.
		 */
		public void doDeletefield(
		RunData runData,
		Context context,
		CalendarActionState state,
		SessionState sstate)
		{
			
			ParameterParser params = runData.getParameters();
			String addFields = (String) sstate.getAttribute(CalendarAction.SSTATE_ATTRIBUTE_ADDFIELDS_CALENDARS);
			String [] addFieldsCalendarList = null, newAddFieldsCalendarList = null;
			
			int nextNewFieldsIndex = 0;
			if (addFields != null)
			{
				addFieldsCalendarList = fieldStringToArray(addFields,ADDFIELDS_DELIMITER);
				
				// The longest the new array can possibly be is the current size of the list.
				newAddFieldsCalendarList = new String[addFieldsCalendarList.length];
				
				for (int i=0; i< addFieldsCalendarList.length; i++)
				{
					String fieldName = params.getString(addFieldsCalendarList[i]);
					
					// If a value is present, then that means that the user has checked
					// the box for the field to be removed.  Don't add it to the
					// new list of field names.  If it is not present, then add it
					// to the new list of field names.
					if ( fieldName == null || fieldName.length() == 0 )
					{
						newAddFieldsCalendarList[nextNewFieldsIndex++] = addFieldsCalendarList[i];
					}
					else
						sstate.setAttribute(CalendarAction.SSTATE_ATTRIBUTE_DELFIELDS_CONFIRM, "Y");
				}
				addFields = arrayToString(newAddFieldsCalendarList, ADDFIELDS_DELIMITER);
			}
			
			// Go back to whatever state we were in beforehand.
			state.setReturnState(state.getPrevState());
			
			enableObserver(sstate, true);

			sstate.setAttribute(CalendarAction.SSTATE_ATTRIBUTE_ADDFIELDS_CALENDARS, addFields);
			
			
			sstate.setAttribute(CalendarAction.SSTATE_ATTRIBUTE_ADDFIELDS_PAGE, CalendarAction.PAGE_ADDFIELDS);
			
		}
		
		/**
		 * Handles the user clicking on the save button on the page to add or
		 * remove additional attributes for all calendar events.
		 */
		public void doUpdate(
		RunData runData,
		Context context,
		CalendarActionState state,
		SessionState sstate)
		{
			
			if (sstate.getAttribute(CalendarAction.SSTATE_ATTRIBUTE_DELFIELDS_CONFIRM).equals("Y") && state.getDelfieldAlertOff() )
			{
				String errorCode = rb.getString("java.alert.areyou");
				addAlert(sstate, errorCode);
				state.setDelfieldAlertOff(false);
			}
			else
			{
				state.setDelfieldAlertOff(true);
				String addfields = (String) sstate.getAttribute(CalendarAction.SSTATE_ATTRIBUTE_ADDFIELDS_CALENDARS);
				while (addfields.startsWith(ADDFIELDS_DELIMITER))
				{
					addfields = addfields.substring(ADDFIELDS_DELIMITER.length());
				}
				
				StringBuffer exceptionMessage = new StringBuffer();
				
				String calId = state.getPrimaryCalendarReference();
				try
				{
					CalendarEdit edit = CalendarService.editCalendar(calId);
					edit.setEventFields(addfields);
					CalendarService.commitCalendar(edit);
				}
				catch (IdUnusedException e)
				{
					exceptionMessage.append(rb.getString("java.alert.thereisno")); 
					Log.debug("chef", this + ".doUpdate customize calendar IdUnusedException"+e);
				}
				catch (PermissionException e)
				{
					exceptionMessage.append(rb.getString("java.alert.youdonthave"));
					Log.debug("chef", this + ".doUpdate customize calendar "+e);
				}
				catch (InUseException e)
				{
					exceptionMessage.append(rb.getString("java.alert.someone")); 
					Log.debug("chef", this + ".doUpdate() for CustomizeCalendar: " + e);
				}
				
				sstate.setAttribute(CalendarAction.SSTATE_ATTRIBUTE_ADDFIELDS_CALENDARS, addfields);
				
				sstate.setAttribute(CalendarAction.SSTATE_ATTRIBUTE_ADDFIELDS_PAGE, CalendarAction.PAGE_MAIN);
			}
			
			// Go back to whatever state we were in beforehand.
			state.setReturnState(state.getPrevState());
			enableObserver(sstate, true);
			
		} // doUpdate
		
		/* (non-Javadoc)
		 * @see org.chefproject.actions.schedulePages.SchedulePage#getMenuHandlerID()
		 */
		public String getButtonHandlerID()
		{
			return OPTIONS_BUTTON_HANDLER;
		}
		
		/* (non-Javadoc)
		 * @see org.chefproject.actions.schedulePages.SchedulePage#getMenuText()
		 */
		public String getButtonText()
		{
			return OPTIONS_BUTTON_CAPTION;
		}
		
		/**
		 * Loads additional fields information from the calendar object passed
		 * as a parameter and loads them into the context object for the Velocity
		 * template.
		 */
		public void loadAdditionalFieldsIntoContextFromCalendar(
		Calendar calendarObj,
		Context context)
		{
			// Get a current list of add fields.  This is a ADDFIELDS_DELIMITER string.
			String addfieldsCalendars = calendarObj.getEventFields();
			
			String[] addfieldsCalendarArray = null;
			
			if (addfieldsCalendars != null)
			{
				addfieldsCalendarArray =
				fieldStringToArray(
				addfieldsCalendars,
				ADDFIELDS_DELIMITER);
			}
			
			// Place this object in the context so that the velocity template
			// can get at it.
			context.put(ADDFIELDS_CALENDARS_COLLECTION, addfieldsCalendarArray);
			context.put("tlang",rb);
			if (addfieldsCalendarArray == null)
				context.put(ADDFIELDS_CALENDARS_COLLECTION_ISEMPTY, Boolean.valueOf(true));
			else
				context.put(ADDFIELDS_CALENDARS_COLLECTION_ISEMPTY, Boolean.valueOf(false));
		}
		
		/**
		 * Loads additional fields from the run data into a provided map object.
		 */
		public void loadAdditionalFieldsMapFromRunData(
		RunData rundata,
		Map addfieldsMap,
		Calendar calendarObj)
		{
			String addfields_str = calendarObj.getEventFields();
			if ( addfields_str != null && addfields_str.trim().length() != 0)
			{
				String [] addfields = fieldStringToArray(addfields_str, ADDFIELDS_DELIMITER);
				String eachfield;
				
				for (int i=0; i < addfields.length; i++)
				{
					eachfield = addfields[i];
					addfieldsMap.put(eachfield, rundata.getParameters().getString(eachfield));
				}
			}
		}
	}
	
	/**
	 * Utility class to figure out permissions for a calendar object.
	 */
	static public class CalendarPermissions
	{
		/**
		 * Priate constructor, doesn't allow instances of this object.
		 */
		private CalendarPermissions()
		{
			super();
		}
		
		/**
		 * Returns true if the primary and selected calendar are the same, but not null.
		 */
		static boolean verifyPrimarySelectedMatch(String primaryCalendarReference, String selectedCalendarReference)
		{
			//
			// Both primary and secondary calendar ids must be specified.
			// These must also match to be able to delete an event
			//
			if ( primaryCalendarReference == null ||
			selectedCalendarReference == null ||
			!primaryCalendarReference.equals(selectedCalendarReference) )
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		
		/**
		 * Utility routint to get the calendar for a given calendar id.
		 */
		static private Calendar getTheCalendar(String calendarReference)
		{
			Calendar calendarObj = null;
			
			try
			{
				calendarObj = CalendarService.getCalendar(calendarReference);
				
				if (calendarObj == null)
				{
					// If the calendar isn't there, try adding it.
					if (CalendarService.allowAddCalendar(calendarReference))
					{
						CalendarService.commitCalendar(
						CalendarService.addCalendar(calendarReference));
						calendarObj = CalendarService.getCalendar(calendarReference);
					}
				}
			}
			
			catch (IdUnusedException e)
			{
				org.sakaiproject.service.framework.log.cover.Log.debug("chef", "CalendarPermissions.getTheCalendar(): ",e);
			}
			
			catch (PermissionException e)
			{
				org.sakaiproject.service.framework.log.cover.Log.debug("chef", "CalendarPermissions.getTheCalendar(): " + e);
			}
			
			catch (IdUsedException e)
			{
				org.sakaiproject.service.framework.log.cover.Log.debug("chef", "CalendarPermissions.getTheCalendar(): " + e);
			}
			
			catch (IdInvalidException e)
			{
				org.sakaiproject.service.framework.log.cover.Log.debug("chef", "CalendarPermissions.getTheCalendar(): " + e);
			}
			
			return calendarObj;
		}
		
		/**
		 * Returns true if the current user can see the events in a calendar.
		 */
		static public boolean allowViewEvents(String calendarReference)
		{
			Calendar calendarObj = getTheCalendar(calendarReference);
			
			if (calendarObj == null)
			{
				return false;
			}
			else
			{
				return calendarObj.allowGetEvents();
			}
		}
		
		/**
		 * Returns true if the current user is allowed to delete events on the calendar id
		 * passed in as the selectedCalendarReference parameter.  The selected calendar must match
		 * the primary calendar for this function to return true.
		 * @param primaryCalendarReference calendar id for the default channel
		 * @param selectedCalendarReference calendar id for the event the user has just selected
		 */
		public static boolean allowDeleteEvent(String primaryCalendarReference, String selectedCalendarReference, String eventId)
		{
			//
			// Both primary and secondary calendar ids must be specified.
			// These must also match to be able to delete an event
			//
			if ( !verifyPrimarySelectedMatch(primaryCalendarReference, selectedCalendarReference) )
			{
				return false;
			}
			
			Calendar calendarObj = getTheCalendar(primaryCalendarReference);
			
			if (calendarObj == null)
			{
				return false;
			}
			else
			{
				CalendarEvent event = null;
				try
				{
					event = calendarObj.getEvent(eventId);
				}
				catch (IdUnusedException e)
				{
					org.sakaiproject.service.framework.log.cover.Log.debug("chef", "CalendarPermissions.canDeleteEvent(): " + e);
				}
				catch (PermissionException e)
				{
					org.sakaiproject.service.framework.log.cover.Log.debug("chef", "CalendarPermissions.canDeleteEvent(): " + e);
				}
				
				if (event == null)
				{
					return false;
				}
				else
				{
					return calendarObj.allowRemoveEvent(event);
				}
			}
		}
		
		/**
		 * Returns true if the current user is allowed to revise events on the calendar id
		 * passed in as the selectedCalendarReference parameter.  The selected calendar must match
		 * the primary calendar for this function to return true.
		 * @param primaryCalendarReference calendar id for the default channel
		 * @param selectedCalendarReference calendar reference for the event the user has just selected
		 */
		static public boolean allowReviseEvents(String primaryCalendarReference, String selectedCalendarReference, String eventId)
		{
			//
			// Both primary and secondary calendar ids must be specified.
			// These must also match to be able to delete an event
			//
			if ( !verifyPrimarySelectedMatch(primaryCalendarReference, selectedCalendarReference) )
			{
				return false;
			}
			
			Calendar calendarObj = getTheCalendar(primaryCalendarReference);
			
			if (calendarObj == null)
			{
				return false;
			}
			else
			{
				return calendarObj.allowEditEvent(eventId);
			}
		}
		
		/**
		 * Returns true if the current user is allowed to create events on the calendar id
		 * passed in as the selectedCalendarReference parameter.  The selected calendar must match
		 * the primary calendar for this function to return true.
		 * @param primaryCalendarReference calendar reference for the default channel
		 * @param selectedCalendarReference calendar reference for the event the user has just selected
		 */
		static public boolean allowCreateEvents(String primaryCalendarReference, String selectedCalendarReference)
		{
			// %%% Note: disabling this check as the allow create events should ONLY be on the primary,
			// we don't care about the selected -ggolden
/*
			//
			// The primary and selected calendar ids must match, unless the selected calendar
			// is null or empty.
			//
 
			if ( selectedCalendarReference != null &&
				 selectedCalendarReference.length() > 0 &&
				 !verifyPrimarySelectedMatch(primaryCalendarReference, selectedCalendarReference) )
			{
				return false;
			}
 */
			
			Calendar calendarObj = getTheCalendar(primaryCalendarReference);
			
			if (calendarObj == null)
			{
				return false;
			}
			else
			{
				return calendarObj.allowAddEvent();
			}
		}
		
		/**
		 * Returns true if the user is allowed to merge events from different calendars
		 * within the default channel.
		 */
		static public boolean allowMergeCalendars(String calendarReference, boolean isOnWorkspaceTab)
		{
			// Don't allow merging on the user's own tab.  This currently only works for groups.
			// Note: if this is really what you want, then check if the "id" (really a reference) is to a user or group site
			//       (for now, check that the id is "group-" -ggolden
			//
			// I wasn't quite sure how to check if the calendar reference that was formerly
			// passed was a user or a group site.  This seems to do the job, but I'm leaving
			// Glenn's comment intact in case this needs to be revistied.
			return !isOnWorkspaceTab && allowModifyCalendarProperties(calendarReference);
		}
		
		/**
		 * Returns true if the use is allowed to modify properties of the calendar itself,
		 * and not just the events within the calendar.
		 */
		static public boolean allowModifyCalendarProperties(String calendarReference)
		{
			return CalendarService.allowUpdateCalendar(calendarReference);
		}

		/**
		 * Returns true if the use is allowed to import events into the calendar.
		 */
		static public boolean allowImport(String calendarReference)
		{
			return CalendarService.allowImportCalendar(calendarReference);
		}
	}
	
	private final static String SSTATE_ATTRIBUTE_ADDFIELDS_PAGE =
	"addfieldsPage";
	private final static String SSTATE_ATTRIBUTE_ADDFIELDS_CALENDARS_INIT =
	"addfieldsInit";
	private final static String SSTATE_ATTRIBUTE_ADDFIELDS_CALENDARS =
	"addfields";
	
	private final static String SSTATE_ATTRIBUTE_DELFIELDS_CONFIRM =
	"delfieldsConfirm";
	
	private final static String STATE_NEW = "new";
	
	private static final String EVENT_REFERENCE_PARAMETER = "eventReference";
	
	private static final String EVENT_CONTEXT_VAR = "event";
	private static final String NO_EVENT_FLAG_CONTEXT_VAR = "noEvent";
	
	//
	// These are variables used in the context for communication between this
	// action class and the Velocity template.
	//
	
	// False/true string values are used in the context variables in a number of places.
	private static final String FALSE_STRING = "false";
	private static final String TRUE_STRING = "true";
	
	// This is the property name in the portlet config for the list of calendars
	// that are not merged.
	private final static String PORTLET_CONFIG_PARM_MERGED_CALENDARS = "mergedCalendarReferences";
	
	private final static String PAGE_MAIN = "main";
	private final static String PAGE_ADDFIELDS = "addFields";
	
	/** The flag name and value in state to indicate an update to the portlet is needed. */
	private final static String SSTATE_ATTRIBUTE_MERGED_CALENDARS =
	"mergedCalendars";
	
	// String constants for user interface states
	private final static String STATE_MERGE_CALENDARS = "mergeCalendars";
	private final static String STATE_CUSTOMIZE_CALENDAR = "customizeCalendar";
	
	// for detailed event view navigator
	private final static String STATE_PREV_ACT = "toPrevActivity";
	private final static String STATE_NEXT_ACT = "toNextActivity";
	private final static Object STATE_CURRENT_ACT = "toCurrentActivity";
	private final static String STATE_EVENTS_LIST ="eventIds";
	private final static String STATE_NAV_DIRECTION = "navigationDirection";
	
	private MergePage mergedCalendarPage =
	new MergePage(
	MERGE_SCHEDULE_BUTTON_CAPTION,
	MERGE_SCHEDULE_BUTTON_HANDLER,
	MERGED_CALENDARS_COLLECTION);
	
	private CustomizeCalendarPage customizeCalendarPage =
	new CustomizeCalendarPage();
	
	/**
	 * See if the current tab is the workspace tab.
	 * @return true if we are currently on the "My Workspace" tab.
	 */
	private static boolean isOnWorkspaceTab()
	{
		// TODO: return to this question! -ggolden
		// return false;
		// we'll really answer the question - is the current request's site a user site?
		return SiteService.isUserSite(PortalService.getCurrentSiteId());
	}
	
	
	protected Class getStateClass()
	{
		return CalendarActionState.class;
		
	}   // getStateClass
	
	/**
	 * Gets an array of all the calendars whose events we can access.
	 */
	private List getCalendarReferenceList(VelocityPortlet portlet, String primaryCalendarReference, boolean isOnWorkspaceTab)
	{
		MergedList mergedCalendarList = new MergedList();

		// TODO - MERGE FIX
		String[] channelArray = null;
		
		// Figure out the list of channel references that we'll be using.
		// If we're on the workspace tab, we get everything.
        // Don't do this if we're the super-user, since we'd be
        // overwhelmed.
		if ( isOnWorkspaceTab()  && !SecurityService.isSuperUser() )
        {
		    channelArray = mergedCalendarList
                    .getAllPermittedChannels(new CalendarChannelReferenceMaker());
        }
		else
		{
			channelArray = mergedCalendarList
            .getChannelReferenceArrayFromDelimitedString(
                    primaryCalendarReference, portlet.getPortletConfig()
                            .getInitParameter(
                                    PORTLET_CONFIG_PARM_MERGED_CALENDARS));
		}


        mergedCalendarList
                .loadChannelsFromDelimitedString(isOnWorkspaceTab,
                        new MergedListEntryProviderFixedListWrapper(
                                new EntryProvider(), primaryCalendarReference,
                                channelArray,
                                new CalendarReferenceToChannelConverter()),
						StringUtil.trimToZero(UsageSessionService.getSessionUserId()),
                        channelArray, SecurityService.isSuperUser(),
                        PortalService.getCurrentSiteId());

		return mergedCalendarList.getReferenceList();
	}
	
	/**
	 * Gets the session state from the Jetspeed RunData
	 */
	static private SessionState getSessionState(RunData runData)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)runData).getJs_peid();
		return ((JetspeedRunData)runData).getPortletSessionState(peid);
	}
	
	public String buildMainPanelContext(  VelocityPortlet portlet,
	Context context,
	RunData runData,
	SessionState sstate)
	{
		CalendarActionState state = (CalendarActionState)getState(portlet, runData, CalendarActionState.class);
		
		// if we are in edit permissions...
		String helperMode = (String) sstate.getAttribute(PermissionsAction.STATE_MODE);
		if (helperMode != null)
		{
			String template = PermissionsAction.buildHelperContext(portlet, context, runData, sstate);
			if (template == null)
			{
				addAlert(sstate, rb.getString("java.alert.thereisa"));
			}
			else
			{
				return template;
			}
		}
		
		String template = (String)getContext(runData).get("template");
		
		// if we are in edit attachments...
		String mode = (String) sstate.getAttribute(AttachmentAction.STATE_MODE);
		if (mode != null)
		{
			// if the mode is not done, defer to the AttachmentAction
			if (!mode.equals(AttachmentAction.MODE_DONE))
			{
				template = AttachmentAction.buildHelperContext(portlet, context, runData, sstate);
				return template;
			}
			
			// when done, get the new attachments, (if null, there was no change)
			ReferenceVector attachments = (ReferenceVector) sstate.getAttribute(AttachmentAction.STATE_ATTACHMENTS);
			if (attachments != null)
			{
				state.setAttachments(attachments);
			}
			
			// clean up
			sstate.removeAttribute(AttachmentAction.STATE_MODE);
			sstate.removeAttribute(AttachmentAction.STATE_ATTACHMENTS);
		}
		
		String stateName = state.getState();
		if (stateName == null) stateName = "";		
		if ( stateName.equals(STATE_SCHEDULE_IMPORT) )
		{
			buildImportContext(portlet, context, runData, state, getSessionState(runData));			
		}
		else if ( stateName.equals(STATE_MERGE_CALENDARS) )
		{
			// build the context to display the options panel
			mergedCalendarPage.buildContext(portlet, context, runData, state, getSessionState(runData));
		}
		else if ( stateName.equals(STATE_CUSTOMIZE_CALENDAR) )
		{
			// build the context to display the options panel
			//needed to track when user clicks 'Save' or 'Cancel'
			String sstatepage = "";
			
			Object statepageAttribute = sstate.getAttribute(SSTATE_ATTRIBUTE_ADDFIELDS_PAGE);
			
			if ( statepageAttribute != null )
			{
				sstatepage = statepageAttribute.toString();
			}
			
			if (!sstatepage.equals(PAGE_ADDFIELDS))
			{
				sstate.setAttribute(SSTATE_ATTRIBUTE_ADDFIELDS_PAGE, PAGE_MAIN);
			}
			
			customizeCalendarPage.buildContext(portlet, context, runData, state, getSessionState(runData));
		}
		else if ((stateName.equals("revise"))|| (stateName.equals("goToReviseCalendar")))
		{
			// build the context for the normal view show
			buildReviseContext(portlet, context, runData, state);
		}
		else if (stateName.equals("description"))
		{
			// build the context for the basic step of adding file
			buildDescriptionContext(portlet, context, runData, state);
		}
		else if (stateName.equals("year"))
		{
			// build the context for the advanced step of adding file
			buildYearContext(portlet, context, runData, state);
		}
		else if (stateName.equals("month"))
		{
			// build the context for the basic step of adding folder
			buildMonthContext(portlet, context, runData, state);
		}
		else if (stateName.equals("day"))
		{
			// build the context for the basic step of adding simple text
			buildDayContext(portlet, context, runData, state);
		}
		else if (stateName.equals("week"))
		{
			// build the context for the basic step of delete confirm page
			buildWeekContext(portlet, context, runData, state);
		}
		else if (stateName.equals("new"))
		{
			// build the context to display the property list
			buildNewContext(portlet, context, runData, state);
		}
		else if (stateName.equals("delete"))
		{
			// build the context to display the property list
			buildDeleteContext(portlet, context, runData, state);
		}
		else if (stateName.equals("list"))
		{
			// build the context to display the list view
			buildListContext(portlet, context, runData, state);
		}
		else if (stateName.equals(STATE_SET_FREQUENCY))
		{
			buildFrequencyContext(portlet, context, runData, state);
		}

		context.put("message", state.getState());
		context.put("state", state.getKey());
		context.put("tlang",rb);
		return template;
		
	}   // buildMainPanelContext
	
	
	private void buildImportContext(VelocityPortlet portlet, Context context, RunData runData, CalendarActionState state, SessionState state2)
	{
		// Place this object in the context so that the velocity template
		// can get at it.
			
		// Start at the beginning if nothing is set yet.
		if ( state.getImportWizardState() == null )
		{
			state.setImportWizardState(IMPORT_WIZARD_SELECT_TYPE_STATE);
		}
		
		// Set whatever the current wizard state is.
		context.put("importWizardState", state.getImportWizardState());
		context.put("tlang",rb);
		// Set the imported events into the context.
		context.put("wizardImportedEvents", state.getWizardImportedEvents());
	}

	/**
	 * Addes the primary calendar reference (this site's default calendar)
	 * to the calendar action state object.
	 */
	private void setPrimaryCalendarReferenceInState(VelocityPortlet portlet, CalendarActionState state)
	{
		String calendarReference = state.getPrimaryCalendarReference();
		
		if (calendarReference == null)
		{
			
			calendarReference = StringUtil.trimToNull(portlet.getPortletConfig().getInitParameter(CALENDAR_INIT_PARAMETER));
			if (calendarReference == null)
			{
				// form a reference to the default calendar for this request's site
				calendarReference = CalendarService.calendarReference(PortalService.getCurrentSiteId(), SiteService.MAIN_CONTAINER);
				state.setPrimaryCalendarReference(calendarReference);
				//CalendarCalendarService.getCalendar(calendarReference));
			}
		}
	}

	/**
	 * Build the context for editing the frequency
	 */
	protected void buildFrequencyContext(VelocityPortlet portlet,
	Context context,
	RunData runData,
	CalendarActionState state)
	{
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState sstate = ((JetspeedRunData)runData).getPortletSessionState(peid);
		
		// defaultly set frequency to be once
		// if there is a saved state frequency attribute, replace the default one
		String freq = CalendarAction.DEFAULT_FREQ;
		
		if (sstate.getAttribute(FREQUENCY_SELECT) != null)
		{
			freq = (String)(sstate.getAttribute(FREQUENCY_SELECT));
			RecurrenceRule rule = (RecurrenceRule) sstate.getAttribute(CalendarAction.SSTATE__RECURRING_RULE);
			// for a brand new event, there is no saved recurring rule
			if (rule != null)
			{
				context.put("freq", rule.getFrequencyDescription());
				context.put("rule", rule);
			} // if rule is not null
		} // if there is a state save frequency setting
		
		context.put("freq", freq);
		context.put("tlang",rb);
		// get the data the user just input in the preview new/revise page
		context.put("savedData",state.getNewData());
		
		context.put("realDate", TimeService.newTime());
				
	} // buildFrequencyContext
	
	/**
	 * Build the context for showing revise view
	 */
	protected void buildReviseContext(VelocityPortlet portlet,
	Context context,
	RunData runData,
	CalendarActionState state)
	{
		
		// to get the content Type Image Service
		context.put("contentTypeImageService", ContentTypeImageService.getInstance());
		context.put("tlang",rb);
		Calendar calendarObj = null;
		CalendarEvent calEvent = null;
		CalendarUtil calObj= new CalendarUtil(); //null;
		MyDate dateObj1 = null;
		dateObj1 = new MyDate();
		boolean getEventsFlag = false;
		
		StringBuffer exceptionMessage = new StringBuffer();
		
		ReferenceVector attachments = state.getAttachments();
		
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState sstate = ((JetspeedRunData)runData).getPortletSessionState(peid);
		
		Time m_time = TimeService.newTime();
		TimeBreakdown b = m_time.breakdownLocal();
		int stateYear = b.getYear();
		int stateMonth = b.getMonth();
		int stateDay = b.getDay();
		if ((sstate.getAttribute(STATE_YEAR) != null) && (sstate.getAttribute(STATE_MONTH) != null) && (sstate.getAttribute(STATE_DAY) != null))
		{
			stateYear = ((Integer)sstate.getAttribute(STATE_YEAR)).intValue();
			stateMonth = ((Integer)sstate.getAttribute(STATE_MONTH)).intValue();
			stateDay = ((Integer)sstate.getAttribute(STATE_DAY)).intValue();
		}
		calObj.setDay(stateYear, stateMonth, stateDay);
		
		
		dateObj1.setTodayDate(calObj.getMonthInteger(),calObj.getDayOfMonth(),calObj.getYear());
		String calId = state.getPrimaryCalendarReference();
		if( state.getIsNewCalendar() == false)
		{
			if (CalendarService.allowGetCalendar(calId)== false)
			{
				exceptionMessage.append(rb.getString("java.alert.younotallow"));
			}
			else
			{
				try
				{
					calendarObj = CalendarService.getCalendar(calId);
					if(calendarObj.allowGetEvents())
					{
						calEvent = calendarObj.getEvent(state.getCalendarEventId());
						getEventsFlag = true;
					}
					else
						getEventsFlag = false;
					
					// Add any additional fields in the calendar.
					customizeCalendarPage.loadAdditionalFieldsIntoContextFromCalendar( calendarObj, context);
					context.put("tlang",rb);
					context.put("calEventFlag","true");
					context.put("new", "false");
					// if from the metadata view of announcement, the message is already the system resource
					if ( state.getState().equals("goToReviseCalendar") )
					{
						context.put("backToRevise", "false");
					}
					// if from the attachments editing view or preview view of announcement
					else if (state.getState().equals("revise"))
					{
						context.put("backToRevise", "true");
					}
					
					//Vector attachments = state.getAttachments();
					if ( attachments != null )
					{
						context.put("attachments", attachments);
					}
					else
					{
						context.put("attachNull", "true");
					}
					
					//context.put("moreAttachments", moreAttachments.iterator());
					context.put("fromAttachmentFlag",state.getfromAttachmentFlag());
				}
				catch(IdUnusedException e)
				{
					exceptionMessage.append(rb.getString("java.alert.therenoactv"));
					Log.debug("chef", this + ".buildReviseContext(): " + e);
				}
				catch (PermissionException e)
				{
					exceptionMessage.append(rb.getString("java.alert.younotperm"));
					Log.debug("chef", this + ".buildReviseContext(): " + e);
				}
			}
		}
		else
		{
			// if this a new annoucement, get the subject and body from temparory record
			context.put("new", "true");
			context.put("tlang",rb);
			context.put("attachments", attachments);
			context.put("fromAttachmentFlag",state.getfromAttachmentFlag());
		}
		
		// Output for recurring events
		
		// for an existing event
		// if the saved recurring rule equals to string FREQ_ONCE, set it as not recurring
		// if there is a saved recurring rule in sstate, display it
		// otherwise, output the event's rule instead
		if ((((String) sstate.getAttribute(FREQUENCY_SELECT)) != null) 
			&& (((String) sstate.getAttribute(FREQUENCY_SELECT)).equals(FREQ_ONCE)))
		{
			context.put("rule", null);
		}
		else
		{
			RecurrenceRule rule = (RecurrenceRule) sstate.getAttribute(CalendarAction.SSTATE__RECURRING_RULE);
			if (rule == null)
			{
				rule = calEvent.getRecurrenceRule();
			}
			else
				context.put("rule", rule);
			
			if (rule != null)
			{
				context.put("freq", rule.getFrequencyDescription());
			} // if (rule != null)
		} //if ((String) sstate.getAttribute(FREQUENCY_SELECT).equals(FREQ_ONCE))
		
		context.put("tlang",rb);
		context.put("event", calEvent);
		context.put("helper",new Helper());
		context.put("message","revise");
		context.put("savedData",state.getNewData());
		context.put("getEventsFlag", Boolean.valueOf(getEventsFlag));
		
		if(state.getIsNewCalendar()==true)
			context.put("vmtype","new");
		else
			context.put("vmtype","revise");
		
		context.put("service", ContentHostingService.getInstance());
		
		// output the real time
		context.put("realDate", TimeService.newTime());
		
	} // buildReviseContext
	
	
	
	/**
	 * Build the context for showing description for events
	 */
	protected void buildDescriptionContext(VelocityPortlet portlet,
	Context context,
	RunData runData,
	CalendarActionState state)
	{
		
		// to get the content Type Image Service
		context.put("contentTypeImageService", ContentTypeImageService.getInstance());
		context.put("tlang",rb);
		context.put("PortalService", PortalService.getInstance());
                context.put("CalendarService", CalendarService.getInstance());
                context.put("SiteService", SiteService.getInstance());
                
		Calendar calendarObj = null;
		CalendarEvent calEvent = null;
		
		MyDate dateObj1 = null;
		dateObj1 = new MyDate();
		
		StringBuffer exceptionMessage = new StringBuffer();
		
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState sstate = ((JetspeedRunData)runData).getPortletSessionState(peid);
		
		navigatorContextControl(portlet, context, runData, (String)sstate.getAttribute(STATE_NAV_DIRECTION));
		boolean prevAct = sstate.getAttribute(STATE_PREV_ACT) != null;
		boolean nextAct = sstate.getAttribute(STATE_NEXT_ACT) != null;
		context.put("prevAct", new Boolean(prevAct));
		context.put("nextAct", new Boolean(nextAct));
		
		Time m_time = TimeService.newTime();
		TimeBreakdown b = m_time.breakdownLocal();
		int stateYear = b.getYear();
		int stateMonth = b.getMonth();
		int stateDay = b.getDay();
		if ((sstate.getAttribute(STATE_YEAR) != null) && (sstate.getAttribute(STATE_MONTH) != null) && (sstate.getAttribute(STATE_DAY) != null))
		{
			stateYear = ((Integer)sstate.getAttribute(STATE_YEAR)).intValue();
			stateMonth = ((Integer)sstate.getAttribute(STATE_MONTH)).intValue();
			stateDay = ((Integer)sstate.getAttribute(STATE_DAY)).intValue();
		}
		CalendarUtil calObj= new CalendarUtil();
		calObj.setDay(stateYear, stateMonth, stateDay);
		
		// get the today date in month/day/year format
		dateObj1.setTodayDate(calObj.getMonthInteger(),calObj.getDayOfMonth(),calObj.getYear());
		
		// get the event id from the CalendarService.
		// send the event to the vm
		String ce = state.getCalendarEventId();
		
		String selectedCalendarReference = state.getSelectedCalendarReference();
		
		if ( !CalendarPermissions.allowViewEvents(selectedCalendarReference) )
		{
			exceptionMessage.append(rb.getString("java.alert.younotallow")); 
			Log.debug("chef", "here in buildDescription not showing event");
		}
		else
		{
			try
			{
				calendarObj = CalendarService.getCalendar(selectedCalendarReference);
				calEvent = calendarObj.getEvent(ce);
				
				// Add any additional fields in the calendar.
				customizeCalendarPage.loadAdditionalFieldsIntoContextFromCalendar( calendarObj, context);
				
				context.put(EVENT_CONTEXT_VAR, calEvent);
				context.put("tlang",rb);	
				RecurrenceRule rule = calEvent.getRecurrenceRule();
				// for a brand new event, there is no saved recurring rule
				if (rule != null)
				{
					context.put("freq", rule.getFrequencyDescription());
					
					context.put("rule", rule);
				}
			}
			catch (IdUnusedException  e)
			{
				Log.debug("chef", this + ".buildDescriptionContext(): " + e);
				context.put(NO_EVENT_FLAG_CONTEXT_VAR, TRUE_STRING);
			}
			catch (PermissionException e)
			{
				exceptionMessage.append(rb.getString("java.alert.younotpermadd"));
				Log.debug("chef", this + ".buildDescriptionContext(): " + e);
			}
		}
		
		buildMenu(
			portlet,
			context,
			runData,
			state,
			CalendarPermissions.allowCreateEvents(
				state.getPrimaryCalendarReference(),
				state.getSelectedCalendarReference()),
			CalendarPermissions.allowDeleteEvent(
				state.getPrimaryCalendarReference(),
				state.getSelectedCalendarReference(),
				state.getCalendarEventId()),
			CalendarPermissions.allowReviseEvents(
				state.getPrimaryCalendarReference(),
				state.getSelectedCalendarReference(),
				state.getCalendarEventId()),
			CalendarPermissions.allowMergeCalendars(
				state.getPrimaryCalendarReference(),
				isOnWorkspaceTab()),
			CalendarPermissions.allowModifyCalendarProperties(
				state.getPrimaryCalendarReference()),
			CalendarPermissions.allowImport(
				state.getPrimaryCalendarReference()));
		
		context.put(
				"allowDelete",
				new Boolean(CalendarPermissions.allowDeleteEvent(
						state.getPrimaryCalendarReference(),
						state.getSelectedCalendarReference(),
						state.getCalendarEventId())));
		context.put(
				"allowRevise",
				new Boolean(CalendarPermissions.allowReviseEvents(
						state.getPrimaryCalendarReference(),
						state.getSelectedCalendarReference(),
						state.getCalendarEventId())));

	}   // buildDescriptionContext
	
	
	/**
	 * Build the context for showing Year view
	 */
	protected void buildYearContext(VelocityPortlet portlet,
	Context context,
	RunData runData,
	CalendarActionState state)
	{
		CalendarUtil calObj= new CalendarUtil();
		MyYear yearObj = null;
		MyMonth monthObj1, monthObj2 = null;
		MyDay dayObj = null;
		MyDate dateObj1 = null;
		boolean allowed = false;
		CalendarEventVector CalendarEventVectorObj = null;
		
		// new objects of myYear, myMonth, myDay, myWeek classes
		yearObj = new MyYear();
		monthObj1 = new MyMonth();
		dayObj = new MyDay();
		dateObj1 = new MyDate();
		StringBuffer exceptionMessage = new StringBuffer();
		
		int month = 1;
		int col = 3;
		int row = 4;
		
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState sstate = ((JetspeedRunData)runData).getPortletSessionState(peid);
		
		Time m_time = TimeService.newTime();
		TimeBreakdown b = m_time.breakdownLocal();
		int stateYear = b.getYear();
		int stateMonth = b.getMonth();
		int stateDay = b.getDay();
		if ((sstate.getAttribute(STATE_YEAR) != null) && (sstate.getAttribute(STATE_MONTH) != null) && (sstate.getAttribute(STATE_DAY) != null))
		{
			stateYear = ((Integer)sstate.getAttribute(STATE_YEAR)).intValue();
			stateMonth = ((Integer)sstate.getAttribute(STATE_MONTH)).intValue();
			stateDay = ((Integer)sstate.getAttribute(STATE_DAY)).intValue();
		}
		calObj.setDay(stateYear, stateMonth, stateDay);
		
		dateObj1.setTodayDate(calObj.getMonthInteger(),calObj.getDayOfMonth(),calObj.getYear());
		yearObj.setYear(calObj.getYear());
		monthObj1.setMonth(calObj.getMonthInteger());
		dayObj.setDay(calObj.getDayOfMonth());
		
		if (CalendarService.allowGetCalendar(state.getPrimaryCalendarReference())== false)
		{
			allowed = false;
			exceptionMessage.append(rb.getString("java.alert.younotallowsee"));
			CalendarEventVectorObj = new  CalendarEventVector();
		}
		else
		{
			try
			{
				allowed = CalendarService.getCalendar(state.getPrimaryCalendarReference()).allowAddEvent();
			}
			catch(IdUnusedException e)
			{
				exceptionMessage.append(rb.getString("java.alert.therenoactv"));
				Log.debug("chef", this + ".buildYearContext(): " + e);
			}
			catch (PermissionException e)
			{
				exceptionMessage.append(rb.getString("java.alert.younotperm"));
				Log.debug("chef", this + ".buildYearContext(): " + e);
			}
		}
		
		for(int r = 0; r<row; r++)
		{
			for (int c = 0; c<col;c++)
			{
				monthObj2 = new MyMonth();
				calObj.setDay(dateObj1.getYear(),month,1);
				
				CalendarEventVectorObj =
				CalendarService.getEvents(
				getCalendarReferenceList(
				portlet,
				state.getPrimaryCalendarReference(),
				isOnWorkspaceTab()),
				getMonthTimeRange(calObj));
				
				
				calObj.setDay(dateObj1.getYear(),dateObj1.getMonth(),dateObj1.getDay());
				monthObj2 = calMonth(month, calObj,state,CalendarEventVectorObj);
				
				month++;
				yearObj.setMonth(monthObj2,r,c);
			}
		}
		calObj.setDay(dateObj1.getYear(),dateObj1.getMonth(),dateObj1.getDay());
		context.put("tlang",rb);
		context.put("yearArray",yearObj);
		context.put("year",new Integer(calObj.getYear()));
		context.put("date",dateObj1);
		state.setState("year");
		
		buildMenu(
			portlet,
			context,
			runData,
			state,
			CalendarPermissions.allowCreateEvents(
				state.getPrimaryCalendarReference(),
				state.getSelectedCalendarReference()),
			CalendarPermissions.allowDeleteEvent(
				state.getPrimaryCalendarReference(),
				state.getSelectedCalendarReference(),
				state.getCalendarEventId()),
			CalendarPermissions.allowReviseEvents(
				state.getPrimaryCalendarReference(),
				state.getSelectedCalendarReference(),
				state.getCalendarEventId()),
			CalendarPermissions.allowMergeCalendars(
				state.getPrimaryCalendarReference(),
				isOnWorkspaceTab()),
			CalendarPermissions.allowModifyCalendarProperties(
				state.getPrimaryCalendarReference()),
			CalendarPermissions.allowImport(
				state.getPrimaryCalendarReference()));
		
		// added by zqian for toolbar
		context.put("allow_new", Boolean.valueOf(allowed));
		context.put("allow_delete", Boolean.valueOf(false));
		context.put("allow_revise", Boolean.valueOf(false));
		context.put("tlang",rb);
		context.put(Menu.CONTEXT_ACTION, "CalendarAction");
		
		context.put("selectedView", VIEW_BY_YEAR);
		
	} // buildYearContext
	
	
	/**
	 * Build the context for showing month view
	 */
	
	protected void buildMonthContext(VelocityPortlet portlet,
	Context context,
	RunData runData,
	CalendarActionState state)
	{
		MyMonth monthObj2 = null;
		
		MyDate dateObj1 = null;
		StringBuffer exceptionMessage = new StringBuffer();
		CalendarEventVector CalendarEventVectorObj = null;
		
		dateObj1 = new MyDate();
		
		// read calendar object saved in state object
		//calObj = state.getCalObj();
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState sstate = ((JetspeedRunData)runData).getPortletSessionState(peid);
		
		Time m_time = TimeService.newTime();
		TimeBreakdown b = m_time.breakdownLocal();
		int stateYear = b.getYear();
		int stateMonth = b.getMonth();
		int stateDay = b.getDay();
		if ((sstate.getAttribute(STATE_YEAR) != null) && (sstate.getAttribute(STATE_MONTH) != null) && (sstate.getAttribute(STATE_DAY) != null))
		{
			stateYear = ((Integer)sstate.getAttribute(STATE_YEAR)).intValue();
			stateMonth = ((Integer)sstate.getAttribute(STATE_MONTH)).intValue();
			stateDay = ((Integer)sstate.getAttribute(STATE_DAY)).intValue();
		}
		
		CalendarUtil calObj = new CalendarUtil();
		calObj.setDay(stateYear, stateMonth, stateDay);
		
		dateObj1.setTodayDate(calObj.getMonthInteger(),calObj.getDayOfMonth(),calObj.getYear());
		
		// fill this month object with all days avilable for this month
		if (CalendarService.allowGetCalendar(state.getPrimaryCalendarReference())== false)
		{
			exceptionMessage.append(rb.getString("java.alert.younotallow"));
			CalendarEventVectorObj = new  CalendarEventVector();
			
		}
		
		CalendarEventVectorObj =
		CalendarService.getEvents(
		getCalendarReferenceList(
		portlet,
		state.getPrimaryCalendarReference(),
		isOnWorkspaceTab()),
		getMonthTimeRange(calObj));
		
		
		calObj.setDay(dateObj1.getYear(),dateObj1.getMonth(),dateObj1.getDay());
		
		monthObj2 = calMonth(calObj.getMonthInteger(), calObj,state, CalendarEventVectorObj);
		
		calObj.setDay(dateObj1.getYear(),dateObj1.getMonth(),dateObj1.getDay());
		
		// retrieve the information from day, month and year to calObj again since calObj changed during the process of CalMonth().
		context.put("nameOfMonth",calObj.getMonth());
		context.put("year", new Integer(calObj.getYear()));
		context.put("monthArray",monthObj2);
		context.put("tlang",rb);
		int row = 5;
		context.put("row",new Integer(row));
		context.put("date",dateObj1);
		context.put("realDate", TimeService.newTime());
		
		buildMenu(
			portlet,
			context,
			runData,
			state,
			CalendarPermissions.allowCreateEvents(
				state.getPrimaryCalendarReference(),
				state.getSelectedCalendarReference()),
			CalendarPermissions.allowDeleteEvent(
				state.getPrimaryCalendarReference(),
				state.getSelectedCalendarReference(),
				state.getCalendarEventId()),
			CalendarPermissions.allowReviseEvents(
				state.getPrimaryCalendarReference(),
				state.getSelectedCalendarReference(),
				state.getCalendarEventId()),
			CalendarPermissions.allowMergeCalendars(
				state.getPrimaryCalendarReference(),
				isOnWorkspaceTab()),
			CalendarPermissions.allowModifyCalendarProperties(
				state.getPrimaryCalendarReference()),
			CalendarPermissions.allowImport(
				state.getPrimaryCalendarReference()));
		
		state.setState("month");
		
		context.put("selectedView", VIEW_BY_MONTH);
		
	} // buildMonthContext
	
	
	protected Vector getNewEvents(int year, int month, int day, CalendarActionState state, RunData rundata, int time, int numberofcycles,Context context,CalendarEventVector CalendarEventVectorObj)
	{
		boolean firstTime = true;
		Vector events = new Vector();
		
		Time timeObj = TimeService.newTimeLocal(year,month,day,time,00,00,000);
		
		long duration = ((30*60)*(1000));
		Time updatedTime = TimeService.newTime(timeObj.getTime()+ duration);
		
		/*** include the start time ***/
		TimeRange timeRangeObj = TimeService.newTimeRange(timeObj,updatedTime,true,false);
		
		for (int range = 0; range <=numberofcycles;range++)
		{
			Iterator calEvent = null;
			
			calEvent = CalendarEventVectorObj.getEvents(timeRangeObj);
			
			Vector vectorObj = new Vector();
			EventDisplayClass eventDisplayObj;
			Vector newVectorObj = null;
			boolean swapflag=true;
			EventDisplayClass eventdisplayobj = null;
			
			if (calEvent.hasNext())
			{
				int i = 0;
				while (calEvent.hasNext())
				{
					eventdisplayobj = new EventDisplayClass();
					eventdisplayobj.setEvent((CalendarEvent)calEvent.next(),false,i);
					
					vectorObj.add(i,eventdisplayobj);
					i++;
				} // while
				
				if(firstTime)
				{
					events.add(range,vectorObj);
					firstTime = false;
				}
				else
				{
					while(swapflag == true)
					{
						swapflag=false;
						for(int mm = 0; mm<events.size();mm++)
						{
							int eom, mv =0;
							Vector evectorObj = (Vector)events.elementAt(mm);
							if(evectorObj.isEmpty()==false)
							{
								for(eom = 0; eom<evectorObj.size();eom++)
								{
									if(evectorObj.elementAt(eom)!="")
									{
										String eomId = (((EventDisplayClass)evectorObj.elementAt(eom)).getEvent()).getId();
										newVectorObj = new Vector();
										for(mv = 0; mv<vectorObj.size();mv++)
										{
											if(vectorObj.elementAt(mv)!="")
											{
												String vectorId = (((EventDisplayClass)vectorObj.elementAt(mv)).getEvent()).getId();
												if (vectorId.equals(eomId))
												{
													eventDisplayObj = (EventDisplayClass)vectorObj.elementAt(mv);
													eventDisplayObj.setFlag(true);
													if (mv != eom)
													{
														swapflag = true;
														vectorObj.removeElementAt(mv);
														for(int x = 0 ; x<eom;x++)
														{
															if(vectorObj.isEmpty()==false)
															{
																newVectorObj.add(x,vectorObj.elementAt(0));
																vectorObj.removeElementAt(0);
															}
															else
															{
																newVectorObj.add(x,"");
															}
														}// for
														newVectorObj.add(eom, eventDisplayObj);
														int neweom = eom;
														neweom = neweom+1;
														
														while(vectorObj.isEmpty()==false)
														{
															newVectorObj.add(neweom,vectorObj.elementAt(0));
															vectorObj.removeElementAt(0);
															neweom++;
														}
														
														for(int vv =0;vv<newVectorObj.size();vv++)
														{
															vectorObj.add(vv,newVectorObj.elementAt(vv));
														}
													}   // if
												}   // if
											}   // if
										}   //for
									}   // if
								}   // for
							}   // if
						}   // for
					}   // while
					
					if (vectorObj.isEmpty())
					{
						events.add(range,vectorObj);
					}
					else
					{
						events.add(range,vectorObj);
					}
				}   // if - else firstTime
				
				timeRangeObj.shiftForward(1800000);
			}
			else
			{
				events.add(range,vectorObj);
				timeRangeObj.shiftForward(1800000);
			}
		} // for
		return events;
	} // getNewEvents
	
	
	
	
	/**
	 * Build the context for showing day view
	 */
	protected void buildDayContext(VelocityPortlet portlet,
	Context context,
	RunData runData,
	CalendarActionState state)
	{
		
		Calendar calendarObj = null;
		boolean allowed = false;
		MyDate dateObj1 = null;
		StringBuffer exceptionMessage = new StringBuffer();
		CalendarEventVector CalendarEventVectorObj = null;
		
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState sstate = ((JetspeedRunData)runData).getPortletSessionState(peid);
		
		
		Time m_time = TimeService.newTime();
		TimeBreakdown b = m_time.breakdownLocal();
		int stateYear = b.getYear();
		int stateMonth = b.getMonth();
		int stateDay = b.getDay();
		context.put("todayYear", new Integer(stateYear));
		context.put("todayMonth", new Integer(stateMonth));
		context.put("todayDay", new Integer(stateDay));
		
		if ((sstate.getAttribute(STATE_YEAR) != null) && (sstate.getAttribute(STATE_MONTH) != null) && (sstate.getAttribute(STATE_DAY) != null))
		{	
			stateYear = ((Integer)sstate.getAttribute(STATE_YEAR)).intValue();
			stateMonth = ((Integer)sstate.getAttribute(STATE_MONTH)).intValue();
			stateDay = ((Integer)sstate.getAttribute(STATE_DAY)).intValue();
		}
		CalendarUtil calObj = new CalendarUtil();
		calObj.setDay(stateYear, stateMonth, stateDay);
				
		// new objects of myYear, myMonth, myDay, myWeek classes
		dateObj1 = new MyDate();
		dateObj1.setTodayDate(calObj.getMonthInteger(),calObj.getDayOfMonth(),calObj.getYear());
		
		int year = dateObj1.getYear();
		int month = dateObj1.getMonth();
		int day = dateObj1.getDay();
		
		Vector eventVector = new Vector();
		Vector eventVector1;
		
		String calId = state.getPrimaryCalendarReference();
		
		if (CalendarService.allowGetCalendar(calId)== false)
		{
			allowed = false;
			exceptionMessage.append(rb.getString("java.alert.younotallow"));
		}
		else
		{
			try
			{
				calendarObj = CalendarService.getCalendar(calId);
				allowed = calendarObj.allowAddEvent();
				
				CalendarEventVectorObj =
				CalendarService.getEvents(
				getCalendarReferenceList(
				portlet,
				state.getPrimaryCalendarReference(),
				isOnWorkspaceTab()),
				getDayTimeRange(year, month, day));
				
				String currentPage = state.getCurrentPage();
				
				// if coming from clicking the the day number in month view, year view or list view
				// select the time slot first, go to the slot containing earliest event on that day
				
				if (state.getPrevState() != null)
					if ( (state.getPrevState()).equalsIgnoreCase("list")
					|| (state.getPrevState()).equalsIgnoreCase("month")
					|| (state.getPrevState()).equalsIgnoreCase("year"))
					{
						CalendarEventVector vec = null;
						Time timeObj = TimeService.newTimeLocal(year,month,day,FIRST_PAGE_START_HOUR,00,00,000);
						Time timeObj2 = TimeService.newTimeLocal(year,month,day,7,59,59,000);
						TimeRange timeRangeObj = TimeService.newTimeRange(timeObj,timeObj2);
						vec  = CalendarService.getEvents(getCalendarReferenceList(portlet, state.getPrimaryCalendarReference(),isOnWorkspaceTab()), timeRangeObj);
						
						if (vec.size() > 0)
							currentPage = "first";
						else
						{
							timeObj = TimeService.newTimeLocal(year,month,day,SECOND_PAGE_START_HOUR,00,00,000);
							timeObj2 = TimeService.newTimeLocal(year,month,day,17,59,59,000);
							timeRangeObj = TimeService.newTimeRange(timeObj,timeObj2);
							vec  = CalendarService.getEvents(getCalendarReferenceList(portlet, state.getPrimaryCalendarReference(),isOnWorkspaceTab()), timeRangeObj);
							
							if (vec.size() > 0)
								currentPage = "second";
							else
							{
								timeObj = TimeService.newTimeLocal(year,month,day,THIRD_PAGE_START_HOUR,00,00,000);
								timeObj2 = TimeService.newTimeLocal(year,month,day,23,59,59,000);
								timeRangeObj = TimeService.newTimeRange(timeObj,timeObj2);
								vec  = CalendarService.getEvents(getCalendarReferenceList(portlet, state.getPrimaryCalendarReference(),isOnWorkspaceTab()), timeRangeObj);
								
								if (vec.size() > 0)
									currentPage = "third";
								else
									currentPage = "second";
							}
						}
					}
				state.setCurrentPage(currentPage);
				
				if(currentPage.equals("third"))
				{
					eventVector1 = new Vector();
					eventVector = getNewEvents(year,month,day, state, runData,THIRD_PAGE_START_HOUR,19,context,CalendarEventVectorObj);
					
					for(int index = 0;index<eventVector1.size();index++)
					{
						eventVector.add(eventVector.size(),eventVector1.get(index));
					}
					
				}
				else if (currentPage.equals("second"))
				{
					eventVector = getNewEvents(year,month,day, state, runData,SECOND_PAGE_START_HOUR,19,context,CalendarEventVectorObj);
				}
				
				else
				{
					eventVector1 = new Vector();
					eventVector1 = getNewEvents(year,month,day, state, runData,FIRST_PAGE_START_HOUR,19,context,CalendarEventVectorObj);
					
					for(int index = 0;index<eventVector1.size();index++)
					{
						eventVector.insertElementAt(eventVector1.get(index),index);
					}
				}
				
				dateObj1.setEventBerDay(eventVector);
			}
			catch(IdUnusedException e)
			{
				exceptionMessage.append(rb.getString("java.alert.therenoactv"));
				Log.debug("chef", this + ".buildDayContext(): " + e);
				
				for(int i=0; i<20;i++)
					eventVector.add(i,new Vector());
				
				dateObj1.setEventBerDay(eventVector);
			}
			catch (PermissionException e)
			{
				exceptionMessage.append(rb.getString("java.alert.younotperm"));
				Log.debug("chef", this + ".buildDayContext(): " + e);
				
				for(int i=0; i<20;i++)
					eventVector.add(i,new Vector());
				
				dateObj1.setEventBerDay(eventVector);
			}
		} 
    
		context.put("nameOfMonth",calObj.getMonth());
		context.put("monthInt", new Integer(calObj.getMonthInteger()));
		context.put("firstpage","true");
		context.put("secondpage","false");
		context.put("page",state.getCurrentPage());
		context.put("date",dateObj1);
		context.put("helper",new Helper());
		context.put("calObj", calObj);
		context.put("tlang",rb);
		//state.setCalObj(calObj);
		state.setState("day");
		context.put("message", state.getState());
		
		state.setPrevState("");
		
		buildMenu(
			portlet,
			context,
			runData,
			state,
			CalendarPermissions.allowCreateEvents(
				state.getPrimaryCalendarReference(),
				state.getSelectedCalendarReference()),
			CalendarPermissions.allowDeleteEvent(
				state.getPrimaryCalendarReference(),
				state.getSelectedCalendarReference(),
				state.getCalendarEventId()),
			CalendarPermissions.allowReviseEvents(
				state.getPrimaryCalendarReference(),
				state.getSelectedCalendarReference(),
				state.getCalendarEventId()),
			CalendarPermissions.allowMergeCalendars(
				state.getPrimaryCalendarReference(),
				isOnWorkspaceTab()),
			CalendarPermissions.allowModifyCalendarProperties(
				state.getPrimaryCalendarReference()),
			CalendarPermissions.allowImport(
				state.getPrimaryCalendarReference()));
		
		context.put("permissionallowed",Boolean.valueOf(allowed));
		context.put("tlang",rb);

		context.put("selectedView", VIEW_BY_DAY);

	} // buildDayContext
	
	
	
	/**
	 * Build the context for showing week view
	 */
	protected void buildWeekContext(VelocityPortlet portlet,
	Context context,
	RunData runData,
	CalendarActionState state)
	{
		Calendar calendarObj = null;
		//Time st,et = null;
		//CalendarUtil calObj= null;
		MyYear yearObj = null;
		MyMonth monthObj1 = null;
		MyWeek weekObj =null;
		MyDay dayObj = null;
		MyDate dateObj1, dateObj2 = null;
		boolean allowed = false;
		int dayofweek = 0;
		
		// new objects of myYear, myMonth, myDay, myWeek classes
		yearObj = new MyYear();
		monthObj1 = new MyMonth();
		weekObj = new MyWeek();
		dayObj = new MyDay();
		dateObj1 = new MyDate();
		CalendarEventVector CalendarEventVectorObj = null;
		
		//calObj = state.getCalObj();
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState sstate = ((JetspeedRunData)runData).getPortletSessionState(peid);
		
		Time m_time = TimeService.newTime();
		TimeBreakdown b = m_time.breakdownLocal();
		int stateYear = b.getYear();
		int stateMonth = b.getMonth();
		int stateDay = b.getDay();
		if ((sstate.getAttribute(STATE_YEAR) != null) && (sstate.getAttribute(STATE_MONTH) != null) && (sstate.getAttribute(STATE_DAY) != null))
		{
			stateYear = ((Integer)sstate.getAttribute(STATE_YEAR)).intValue();
			stateMonth = ((Integer)sstate.getAttribute(STATE_MONTH)).intValue();
			stateDay = ((Integer)sstate.getAttribute(STATE_DAY)).intValue();
		}
		
		CalendarUtil calObj = new CalendarUtil();
		calObj.setDay(stateYear, stateMonth, stateDay);
		int iii =0;
		
		
		dateObj1.setTodayDate(calObj.getMonthInteger(),calObj.getDayOfMonth(),calObj.getYear());
		yearObj.setYear(calObj.getYear());
		monthObj1.setMonth(calObj.getMonthInteger());
		dayObj.setDay(calObj.getDayOfMonth());
		String calId = state.getPrimaryCalendarReference();
		
		StringBuffer exceptionMessage = new StringBuffer();
		
		// this loop will move the calender to the begining of the week
		
		if (CalendarService.allowGetCalendar(calId)== false)
		{
			allowed = false;
			exceptionMessage.append(rb.getString("java.alert.younotallow")); 
		}
		else
		{
			try
			{
				calendarObj = CalendarService.getCalendar(calId);
				allowed = true;
			}
			catch(IdUnusedException e)
			{
				if(CalendarService.allowAddCalendar(calId))
				{
					try
					{
						CalendarService.commitCalendar(CalendarService.addCalendar(calId));
						calendarObj = CalendarService.getCalendar(calId);
						allowed = true;
					}
					catch (PermissionException err)
					{
						Log.debug("chef", this + ".buildWeekContext(): " + err);
					}
					catch(IdUsedException err)
					{
						Log.debug("chef", this + ".buildWeekContext(): " + err);
					}
					catch(IdInvalidException err)
					{
						Log.debug("chef", this + ".buildWeekContext(): " + err);
					}
					catch(IdUnusedException err)
					{
						Log.debug("chef", this + ".buildWeekContext(): " + err);
					}
				}//end if
				else
				{
					allowed  = false;
				}
			}
			catch (PermissionException e)
			{
				Log.debug("chef", this + ".buildWeekContext(): " + e);
				allowed = false;
			}
		}
		
		if ((allowed == true) && ( exceptionMessage.toString().length()<=0))
		{
			if (calendarObj.allowGetEvents() == true)
			{
				CalendarEventVectorObj =
				CalendarService.getEvents(
				getCalendarReferenceList(
				portlet,
				state.getPrimaryCalendarReference(),
				isOnWorkspaceTab()),
				getWeekTimeRange(calObj));
			}
			else
			{
				CalendarEventVectorObj = new CalendarEventVector();
			}
		}
		else
		{
			CalendarEventVectorObj = new CalendarEventVector();
		}
		
		calObj.setDay(dateObj1.getYear(),dateObj1.getMonth(),dateObj1.getDay());
		dayofweek = calObj.getDay_Of_Week();
		
		for(int i = dayofweek; i>1;i--)
		{
			calObj.getPrevDate();
		}
		
		dayofweek = calObj.getDay_Of_Week();
		
		Time[] pageStartTime = new Time[7];
		Time[] pageEndTime = new Time[7];
		//Vector pageStartTimeVec = new Vector();
		//Vector pageEndTimeVec = new Vector();
		
		for(int i = 7; i>=dayofweek; i--)
		{
			
			Vector eventVector = new Vector();
			Vector eventVector1;
			dateObj2 =  new MyDate();
			dateObj2.setTodayDate(calObj.getMonthInteger(),calObj.getDayOfMonth(),calObj.getYear());
			dateObj2.setDayName(calObj.getDay());
			dateObj2.setNameOfMonth(calObj.getMonth());
			
			if (calObj.getDayOfMonth() == dayObj.getDay())
				dateObj2.setFlag(1);
			
			if(state.getCurrentPage().equals("third"))
			{
				eventVector1 = new Vector();
				// JS -- the third page starts at 2PM(14 o'clock), and lasts 20 half-hour
				eventVector = getNewEvents(calObj.getYear(),calObj.getMonthInteger(),calObj.getDayOfMonth(), state, runData,THIRD_PAGE_START_HOUR,19,context,CalendarEventVectorObj);
				
				for(int index = 0;index<eventVector1.size();index++)
				{
					eventVector.add(eventVector.size(),eventVector1.get(index));
				}
				
				// Reminder: weekview vm is using 0..6
				pageStartTime[i-1] = TimeService.newTimeLocal(calObj.getYear(),calObj.getMonthInteger(),calObj.getDayOfMonth(), THIRD_PAGE_START_HOUR, 0, 0, 0);
				pageEndTime[i-1] = TimeService.newTimeLocal(calObj.getYear(),calObj.getMonthInteger(),calObj.getDayOfMonth(), 23, 59, 0, 0);
				
			}
			else if (state.getCurrentPage().equals("second"))
			{
				eventVector = getNewEvents(calObj.getYear(),calObj.getMonthInteger(),calObj.getDayOfMonth(), state, runData,SECOND_PAGE_START_HOUR,19,context,CalendarEventVectorObj);
				// Reminder: weekview vm is using 0..6
				pageStartTime[i-1] = TimeService.newTimeLocal(calObj.getYear(),calObj.getMonthInteger(),calObj.getDayOfMonth(), SECOND_PAGE_START_HOUR, 0, 0, 0);
				pageEndTime[i-1] = TimeService.newTimeLocal(calObj.getYear(),calObj.getMonthInteger(),calObj.getDayOfMonth(), 17, 59, 0, 0);
				
			}
			else
			{
				eventVector1 = new Vector();
				// JS -- the first page starts at 12AM(0 o'clock), and lasts 20 half-hour
				eventVector1 = getNewEvents(calObj.getYear(),calObj.getMonthInteger(),calObj.getDayOfMonth(), state, runData, FIRST_PAGE_START_HOUR,19,context,CalendarEventVectorObj);
				
				for(int index = 0;index<eventVector1.size();index++)
				{
					eventVector.insertElementAt(eventVector1.get(index),index);
				}
				
				// Reminder: weekview vm is using 0..6
				pageStartTime[i-1] = TimeService.newTimeLocal(calObj.getYear(),calObj.getMonthInteger(),calObj.getDayOfMonth(), 0, 0, 0, 0);
				pageEndTime[i-1] = TimeService.newTimeLocal(calObj.getYear(),calObj.getMonthInteger(),calObj.getDayOfMonth(), 9, 59, 0, 0);
				
			}
			dateObj2.setEventBerWeek(eventVector);
			weekObj.setWeek(7-i,dateObj2);
			
			// the purpose of this if condition is to check if we reached day 7 if yes do not
			// call next day.
			if (i > dayofweek)
				calObj.nextDate();
		}
		
		calObj.setDay(yearObj.getYear(),monthObj1.getMonth(),dayObj.getDay());
		context.put("week", weekObj);
		context.put("helper",new Helper());
		context.put("date",dateObj1);
		context.put("page",state.getCurrentPage());
		state.setState("week");
		context.put("tlang",rb);
		//state.setCalObj(calObj);
		
		context.put("message",state.getState());
		
		buildMenu(
			portlet,
			context,
			runData,
			state,
			CalendarPermissions.allowCreateEvents(
				state.getPrimaryCalendarReference(),
				state.getSelectedCalendarReference()),
			CalendarPermissions.allowDeleteEvent(
				state.getPrimaryCalendarReference(),
				state.getSelectedCalendarReference(),
				state.getCalendarEventId()),
			CalendarPermissions.allowReviseEvents(
				state.getPrimaryCalendarReference(),
				state.getSelectedCalendarReference(),
				state.getCalendarEventId()),
			CalendarPermissions.allowMergeCalendars(
				state.getPrimaryCalendarReference(),
				isOnWorkspaceTab()),
			CalendarPermissions.allowModifyCalendarProperties(
				state.getPrimaryCalendarReference()),
			CalendarPermissions.allowImport(
				state.getPrimaryCalendarReference()));
		
		calObj.setDay(yearObj.getYear(),monthObj1.getMonth(),dayObj.getDay());
		
		context.put("realDate", TimeService.newTime());
		context.put("tlang",rb);
		Vector vec = new Vector();
		context.put("vec", vec);
		Vector conflictVec = new Vector();
		context.put("conflictVec", conflictVec);
		Vector calVec = new Vector();
		context.put("calVec", calVec);
		HashMap hm = new HashMap();
		context.put("hm", hm);
		Integer intObj = new Integer(0);
		context.put("intObj", intObj);
		
		context.put("pageStartTime", pageStartTime);
		context.put("pageEndTime", pageEndTime);
		
		context.put("selectedView", VIEW_BY_WEEK);
		
	} // buildWeekContext
	
	
	/**
	 * Build the context for showing New view
	 */
	protected void buildNewContext(VelocityPortlet portlet,
	Context context,
	RunData runData,
	CalendarActionState state)
	{
		context.put("tlang",rb);
		// to get the content Type Image Service
		context.put("contentTypeImageService", ContentTypeImageService.getInstance());
		
		MyDate dateObj1 = new MyDate();
		
		CalendarUtil calObj= new CalendarUtil();
		
		// set real today's date as default
		Time m_time = TimeService.newTime();
		TimeBreakdown b = m_time.breakdownLocal();
		calObj.setDay(b.getYear(), b.getMonth(), b.getDay());
		
		dateObj1.setTodayDate(calObj.getMonthInteger(),calObj.getDayOfMonth(),calObj.getYear());
		
		// get the event id from the CalendarService.
		// send the event to the vm
		dateObj1.setNumberOfDaysInMonth(calObj.getNumberOfDays());
		ReferenceVector attachments = state.getAttachments();
		context.put("attachments",attachments);
		
		String calId = state.getPrimaryCalendarReference();
		Calendar calendarObj = null;
		StringBuffer exceptionMessage = new StringBuffer();
		
		try
		{
			calendarObj = CalendarService.getCalendar(calId);
		}
		catch(IdUnusedException e)
		{
			exceptionMessage.append(rb.getString("java.alert.thereis"));
			Log.debug("chef", this + ".buildNewContext(): " + e);
		}
		catch (PermissionException e)
		{
			exceptionMessage.append(rb.getString("java.alert.youdont"));
			Log.debug("chef", this + ".buildNewContext(): " + e);
		}
		
		// Add any additional fields in the calendar.
		customizeCalendarPage.loadAdditionalFieldsIntoContextFromCalendar( calendarObj, context);
		
		// Output for recurring events
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState sstate = ((JetspeedRunData)runData).getPortletSessionState(peid);
		
		// if the saved recurring rule equals to string FREQ_ONCE, set it as not recurring
		// if there is a saved recurring rule in sstate, display it		
		RecurrenceRule rule = (RecurrenceRule) sstate.getAttribute(CalendarAction.SSTATE__RECURRING_RULE);

		if (rule != null)
		{
			context.put("freq", rule.getFrequencyDescription());

			context.put("rule", rule);
		}

		context.put("date",dateObj1);
		context.put("savedData",state.getNewData());
		context.put("helper",new Helper());
		context.put("realDate", TimeService.newTime());
		
	} // buildNewContext
	
	
	/**
	 * Build the context for showing delete view
	 */
	protected void buildDeleteContext(VelocityPortlet portlet,
	Context context,
	RunData runData,
	CalendarActionState state)
	{
		context.put("tlang",rb);
		// to get the content Type Image Service
		context.put("contentTypeImageService", ContentTypeImageService.getInstance());
		
		Calendar calendarObj = null;
		CalendarEvent calEvent = null;
		StringBuffer exceptionMessage = new StringBuffer();
		
		// get the event id from the CalendarService.
		// send the event to the vm
		String calId = state.getPrimaryCalendarReference();
		String calendarEventObj = state.getCalendarEventId();
		
		try
		{
			calendarObj = CalendarService.getCalendar(calId);
			calEvent = calendarObj.getEvent(calendarEventObj);
			
			RecurrenceRule rule = calEvent.getRecurrenceRule();
			// for a brand new event, there is no saved recurring rule
			if (rule != null)
			{
				context.put("freq", rule.getFrequencyDescription());				
				context.put("rule", rule);
			}
			
			context.put("message","delete");
			context.put("event",calEvent);
		}
		catch (IdUnusedException  e)
		{
			exceptionMessage.append(rb.getString("java.alert.noexist"));
			Log.debug("chef", this + ".buildDeleteContext(): " + e);
		}
		catch (PermissionException  e)
		{
			exceptionMessage.append(rb.getString("java.alert.youcreate"));
			Log.debug("chef", this + ".buildDeleteContext(): " + e);
		}		
	}   // buildDeleteContext
	
	
	
	/**
	 * calculate the days in the month and there events if any
	 * @param month is int
	 * @param m_calObj is object of calendar
	 */
	public MyMonth calMonth(int month, CalendarUtil m_calObj, CalendarActionState state, CalendarEventVector CalendarEventVectorObj)
	{
		int numberOfDays = 0;
		int firstDay_of_Month = 0;
		boolean start = true;
		MyMonth monthObj = null;
		MyDate dateObj = null;
		Iterator eventList = null;
		Time startTime = null;
		Time endTime = null;
		TimeRange timeRange = null;
		StringBuffer exceptionMessage = new StringBuffer();
		
		// new objects of myYear, myMonth, myDay, myWeek classes.
		monthObj = new MyMonth();
		
		// set the calendar to the begining of the month
		m_calObj.setDay(m_calObj.getYear(), month, 1);
		numberOfDays = m_calObj.getNumberOfDays();
		
		// get the index of the first day in the month
		firstDay_of_Month = m_calObj.getDay_Of_Week() - 1;
		
		// get the index of the day
		monthObj.setMonthName(m_calObj.getMonth());
		
		for(int i = firstDay_of_Month; i>=0;i--)
		{
			m_calObj.getPrevDate();
		}
		
		for(int weekInMonth = 0; weekInMonth < 1; weekInMonth++)
		{
			// got the seven days in the first week of the month do..
			for(int dayInWeek = 0; dayInWeek < 7; dayInWeek++)
			{
				dateObj = new MyDate();
				m_calObj.nextDate();
				// check if reach the first day of the month.
				if ((dayInWeek == firstDay_of_Month) || (start == false))
				{
					// check if the current day of the month has been match, if yes set the flag to highlight the day in the
					// user interface.
					if ((m_calObj.getDayOfMonth() == state.getcurrentDay()) && (state.getcurrentMonth()== m_calObj.getMonthInteger()) && (state.getcurrentYear() == m_calObj.getYear()))
					{
						dateObj.setFlag(1);
					}
					
					// Each monthObj contains dayObjs for the number of the days in the month.
					dateObj.setTodayDate(m_calObj.getMonthInteger(),m_calObj.getDayOfMonth(),m_calObj.getYear());
					
					//qz if(state.getState()!="year") {
					//startTime = TimeService.newTimeLocal(m_calObj.getYear(),m_calObj.getMonthInteger(),m_calObj.getDayOfMonth(),01,00,00,000);
					startTime = TimeService.newTimeLocal(m_calObj.getYear(),m_calObj.getMonthInteger(),m_calObj.getDayOfMonth(),00,00,00,001);
					//endTime = TimeService.newTimeLocal(m_calObj.getYear(),m_calObj.getMonthInteger(),m_calObj.getDayOfMonth(),24,00,00,000);
					endTime = TimeService.newTimeLocal(m_calObj.getYear(),m_calObj.getMonthInteger(),m_calObj.getDayOfMonth(),23,59,00,000);
					
					eventList = CalendarEventVectorObj.getEvents(TimeService.newTimeRange(startTime,endTime,true,true));
					
					dateObj.setEvents(eventList);
					//qz }
					
					// keep iterator of events in the dateObj
					numberOfDays--;
					monthObj.setDay(dateObj,weekInMonth,dayInWeek);
					start = false;
					
				}
				else if (start == true)
				{
					// fill empty spaces for the first days in the first week in the month before reach the first day of the month
					dateObj.setTodayDate(m_calObj.getMonthInteger(),m_calObj.getDayOfMonth(),m_calObj.getYear());
					
					startTime = TimeService.newTimeLocal(m_calObj.getYear(),m_calObj.getMonthInteger(),m_calObj.getDayOfMonth(),00,00,00,001);
					endTime = TimeService.newTimeLocal(m_calObj.getYear(),m_calObj.getMonthInteger(),m_calObj.getDayOfMonth(),23,59,00,000);
					
					timeRange = TimeService.newTimeRange(startTime,endTime,true,true);
					
					eventList = CalendarEventVectorObj.getEvents(timeRange);
					dateObj.setEvents(eventList);
					
					monthObj.setDay(dateObj,weekInMonth,dayInWeek);
					dateObj.setFlag(0);
				}// end else
			}// end for m
		}// end for i
		
		// Construct the weeks left in the month and save it in the monthObj.
		// row is the max number of rows in the month., Col is equal to 7 which is the max number of col in the month.
		for(int row = 1; row<6; row++)
		{
			// Col is equal to 7 which is the max number of col in tin he month.
			for(int col = 0; col<7; col++)
			{
				if (numberOfDays != 0)
				{
					dateObj = new MyDate();
					m_calObj.nextDate();
					if ((m_calObj.getDayOfMonth() == state.getcurrentDay()) && (state.getcurrentMonth()== m_calObj.getMonthInteger()) && (state.getcurrentYear() == m_calObj.getYear()))
						dateObj.setFlag(1);
					
					dateObj.setTodayDate(m_calObj.getMonthInteger(),m_calObj.getDayOfMonth(),m_calObj.getYear());
					//qz if(state.getState()!="year") {
					//startTime = TimeService.newTimeLocal(m_calObj.getYear(),m_calObj.getMonthInteger(),m_calObj.getDayOfMonth(),01,00,00,000);
					startTime = TimeService.newTimeLocal(m_calObj.getYear(),m_calObj.getMonthInteger(),m_calObj.getDayOfMonth(),00,00,00,001);
					//endTime = TimeService.newTimeLocal(m_calObj.getYear(),m_calObj.getMonthInteger(),m_calObj.getDayOfMonth(),24,00,00,000);
					endTime = TimeService.newTimeLocal(m_calObj.getYear(),m_calObj.getMonthInteger(),m_calObj.getDayOfMonth(),23,59,00,000);
					
					
					timeRange = TimeService.newTimeRange(startTime,endTime,true,true);
					eventList = CalendarEventVectorObj.getEvents(timeRange);
					dateObj.setEvents(eventList);
					
					//qz }
					numberOfDays--;
					monthObj.setDay(dateObj,row,col);
					monthObj.setRow(row);
				}
				else // if it is not the end of week , complete the week wih days from next month.
				{
					if ((m_calObj.getDay_Of_Week())== 7) // if end of week, exit the loop
					{
						row  = 7;
						col = SECOND_PAGE_START_HOUR;
					}
					else // if it is not the end of week, complete with days from next month
					{
						dateObj = new MyDate();
						m_calObj.nextDate();
						dateObj.setTodayDate(m_calObj.getMonthInteger(),m_calObj.getDayOfMonth(),m_calObj.getYear());
						
						//qz if(state.getState()!="year") {
						//startTime = TimeService.newTimeLocal(m_calObj.getYear(),m_calObj.getMonthInteger(),m_calObj.getDayOfMonth(),01,00,00,000);
						startTime = TimeService.newTimeLocal(m_calObj.getYear(),m_calObj.getMonthInteger(),m_calObj.getDayOfMonth(),00,00,00,001);
						//endTime = TimeService.newTimeLocal(m_calObj.getYear(),m_calObj.getMonthInteger(),m_calObj.getDayOfMonth(),24,00,00,000);
						endTime = TimeService.newTimeLocal(m_calObj.getYear(),m_calObj.getMonthInteger(),m_calObj.getDayOfMonth(),23,59,00,000);
						
						timeRange = TimeService.newTimeRange(startTime,endTime,true,true);
						
						eventList = CalendarEventVectorObj.getEvents(timeRange);
						dateObj.setEvents(eventList);
						//qz }
						monthObj.setDay(dateObj,row,col);
						monthObj.setRow(row);
						dateObj.setFlag(0);
					}
				}
			}// end for
		}// end for
		
		return monthObj;
	}
	
	
	public void doAttachments(RunData rundata, Context context)
	{
		CalendarActionState State = (CalendarActionState)getState( context, rundata, CalendarActionState.class );
		String peid = ((JetspeedRunData)rundata).getJs_peid();
		SessionState sstate = ((JetspeedRunData)rundata).getPortletSessionState(peid);
		
		int houri;
		// Data dataObj = null;
		
		// **************** changed for the new attachment editor **************************
		
		// setup... we'll use the attachment action's mode
		sstate.setAttribute(AttachmentAction.STATE_MODE, AttachmentAction.MODE_MAIN);
		String activitytitle = rundata.getParameters().getString("activitytitle");
		String stateFromText = rb.getString("java.schedule");
		if (activitytitle != null && activitytitle.length() > 0)
		{
			stateFromText = rb.getString("java.sched") + '"' + activitytitle + '"';
		}
		sstate.setAttribute(AttachmentAction.STATE_FROM_TEXT, stateFromText);
		
		// put a copy of the attachments into the state
		
		ReferenceVector attachments = State.getAttachments();
		sstate.setAttribute(AttachmentAction.STATE_ATTACHMENTS, attachments.clone());
		// whether there is already an attachment //%%%zqian
		if (attachments.size() > 0)
		{
			sstate.setAttribute(AttachmentAction.STATE_HAS_ATTACHMENT_BEFORE, Boolean.TRUE);
		}
		else
		{
			sstate.setAttribute(AttachmentAction.STATE_HAS_ATTACHMENT_BEFORE, Boolean.FALSE);
		}

		String hour = "";
		hour = rundata.getParameters().getString("startHour");
		String title ="";
		title = rundata.getParameters().getString("activitytitle");
		String minute = "";
		minute = rundata.getParameters().getString("startMinute");
		String dhour = "";
		dhour = rundata.getParameters().getString("duHour");
		String dminute = "";
		dminute = rundata.getParameters().getString("duMinute");
		String description = "";
		description = rundata.getParameters().getString("description");
		description = processFormattedTextFromBrowser(sstate, description);
		String month = "";
		month = rundata.getParameters().getString("month");
		
		String day = "";
		day = rundata.getParameters().getString("day");
		String year = "";
		year = rundata.getParameters().getString("yearSelect");
		String timeType = "";
		timeType = rundata.getParameters().getString("startAmpm");
		String type = "";
		type = rundata.getParameters().getString("eventType");
		String location = "";
		location = rundata.getParameters().getString("location");
		
		// read the recurrence modification intention
		String intentionStr = rundata.getParameters().getString("intention");
		if (intentionStr == null) intentionStr = "";
		
		Calendar calendarObj = null;
		StringBuffer exceptionMessage = new StringBuffer();
		
		String calId = State.getPrimaryCalendarReference();
		try
		{
			calendarObj = CalendarService.getCalendar(calId);
		}
		catch(IdUnusedException e)
		{
			exceptionMessage.append(rb.getString("java.alert.thereis"));
			Log.debug("chef", this + ".buildCustomizeContext(): " + e);
		}
		catch (PermissionException e)
		{
			exceptionMessage.append(rb.getString("java.alert.youdont")); 
			Log.debug("chef", this + ".buildCustomizeContext(): " + e);
		}
		
		Map addfieldsMap = new HashMap();
		
		// Add any additional fields in the calendar.
		customizeCalendarPage. loadAdditionalFieldsMapFromRunData(rundata, addfieldsMap, calendarObj);
		
		if (timeType.equals("pm"))
		{
			if (Integer.parseInt(hour)>11)
				houri = Integer.parseInt(hour);
			else
				houri = Integer.parseInt(hour)+12;
		}
		else if (timeType.equals("am") && Integer.parseInt(hour)==12)
		{
			houri = 24;
		}
		else
		{
			houri = Integer.parseInt(hour);
		}
		
		State.clearData();
		State.setNewData(State.getPrimaryCalendarReference(), title,description,Integer.parseInt(month),Integer.parseInt(day),year,houri,Integer.parseInt(minute),Integer.parseInt(dhour),Integer.parseInt(dminute),type,timeType,location, addfieldsMap, intentionStr);
		
		// **************** changed for the new attachment editor **************************
	} // doAttachments
	
	
	/**
	 * Action is used when doMonth requested in the menu
	 */
	
	public void doMonth(RunData data, Context context)
	{
		
		CalendarActionState state = (CalendarActionState)getState(context, data, CalendarActionState.class);
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState sstate = ((JetspeedRunData)data).getPortletSessionState(peid);
		
		state.setState("month");
		
	} // doMonth
	
	
	/**
	 * Action is used when doDescription is requested when the user click on an event
	 */
	public void doDescription(RunData data, Context context)
	{
		CalendarEvent calendarEventObj = null;
		Calendar calendarObj = null;
		
		CalendarActionState state = (CalendarActionState)getState(context, data, CalendarActionState.class);
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState sstate = ((JetspeedRunData)data).getPortletSessionState(peid);
		
		state.setPrevState(state.getState());
		
		// store the state coming from, like day view, week view, month view or list view
		String returnState = state.getState();
		state.setReturnState(returnState);
		
		state.setState("description");
		
		// "crack" the reference (a.k.a dereference, i.e. make a Reference)
		// and get the event id and calendar reference
		Reference ref = new Reference(data.getParameters().getString(EVENT_REFERENCE_PARAMETER));
		String eventId = ref.getId();
		String calId = CalendarService.calendarReference(ref.getContext(), ref.getContainer());
		
		state.setAttachments(null);
		state.setCalendarEventId(calId, eventId);
		
		// %%% get the event object from the reference new Reference(data.getParameters().getString(EVENT_REFERENCE_PARAMETER)).getResource() -ggolden
		try
		{
			calendarObj = CalendarService.getCalendar(calId);
			try
			{
				//calendarObj.getEvent(eventId);
				calendarEventObj = calendarObj.getEvent(eventId);
				
				TimeBreakdown b = calendarEventObj.getRange().firstTime().breakdownLocal();
				//CalendarUtil m_calObj = state.getCalObj();
				//m_calObj.setDay(b.getYear(), b.getMonth(), b.getDay());
				//state.setCalObj(m_calObj);
				
				sstate.setAttribute(STATE_YEAR,  new Integer(b.getYear()));
				sstate.setAttribute(STATE_MONTH,  new Integer(b.getMonth()));
				sstate.setAttribute(STATE_DAY,  new Integer(b.getDay()));
				
				sstate.setAttribute(STATE_NAV_DIRECTION, STATE_CURRENT_ACT);
				
			}
			catch (IdUnusedException err)
			{
				// if this event doesn't exist, let user not go to the detail view
				// set the state recorded ID as null
				// show the alert message
				Log.debug("chef", this + ".IdUnusedException " + err);
				//state.setState("returnState");
				state.setCalendarEventId("", "");
				String errorCode = rb.getString("java.error");
				addAlert(sstate, errorCode);
			}
			catch (PermissionException err)
			{
				Log.debug("chef", this + ".PermissionException " + err);
			}
		}
		catch (IdUnusedException  e)
		{
			addAlert(sstate, rb.getString("java.alert.noexist"));
		}
		catch (PermissionException  e)
		{
			addAlert(sstate, rb.getString("java.alert.youcreate"));
		}
	}	   // doDescription
	
	
	
	/**
	 * Action is used when doGomonth requested in the year/list view
	 */
	public void doGomonth(RunData data, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, data, CalendarActionState.class);
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState sstate = ((JetspeedRunData)data).getPortletSessionState(peid);
		
		Time m_time = TimeService.newTime();
		TimeBreakdown b = m_time.breakdownLocal();
		int stateYear = b.getYear();
		int stateMonth = b.getMonth();
		int stateDay = b.getDay();
		if ((sstate.getAttribute(STATE_YEAR) != null) && (sstate.getAttribute(STATE_MONTH) != null) && (sstate.getAttribute(STATE_DAY) != null))
		{
			stateYear = ((Integer)sstate.getAttribute(STATE_YEAR)).intValue();
			stateMonth = ((Integer)sstate.getAttribute(STATE_MONTH)).intValue();
			stateDay = ((Integer)sstate.getAttribute(STATE_DAY)).intValue();
		}
		CalendarUtil m_calObj = new CalendarUtil();
		m_calObj.setDay(stateYear, stateMonth, stateDay);
		
		String month = "";
		month = data.getParameters().getString("month");
		m_calObj.setMonth(Integer.parseInt(month));
		
		// if this function is called from list view
		// the value of year must be caught also
		int yearInt = m_calObj.getYear();
		
		String currentState = state.getState();
		if (currentState.equalsIgnoreCase("list"))
		{
			String year = "";
			year = data.getParameters().getString("year");
			
			yearInt = Integer.parseInt(year);
		}
		
		m_calObj.setDay(yearInt, m_calObj.getMonthInteger(), m_calObj.getDayOfMonth());
		
		sstate.setAttribute(STATE_YEAR, new Integer(yearInt));
		sstate.setAttribute(STATE_MONTH, new Integer(m_calObj.getMonthInteger()));
		sstate.setAttribute(STATE_DAY, new Integer(m_calObj.getDayOfMonth()));
		
		state.setState("month");
		
	}   // doGomonth
	
	
	/**
	 * Action is used when doGoyear requested in the list view
	 */
	public void doGoyear(RunData data, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, data, CalendarActionState.class);
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState sstate = ((JetspeedRunData)data).getPortletSessionState(peid);
		
		Time m_time = TimeService.newTime();
		TimeBreakdown b = m_time.breakdownLocal();
		int stateYear = b.getYear();
		int stateMonth = b.getMonth();
		int stateDay = b.getDay();
		if ((sstate.getAttribute(STATE_YEAR) != null) && (sstate.getAttribute(STATE_MONTH) != null) && (sstate.getAttribute(STATE_DAY) != null))
		{
			stateYear = ((Integer)sstate.getAttribute(STATE_YEAR)).intValue();
			stateMonth = ((Integer)sstate.getAttribute(STATE_MONTH)).intValue();
			stateDay = ((Integer)sstate.getAttribute(STATE_DAY)).intValue();
		}
		CalendarUtil m_calObj = new CalendarUtil();
		CalendarUtil calObj =  new CalendarUtil();
		
		calObj.setDay(stateYear, stateMonth, stateDay);
		m_calObj.setDay(stateYear, stateMonth, stateDay);
		
		// catch the year value from the list view
		int yearInt = m_calObj.getYear();
		String currentState = state.getState();
		if (currentState.equalsIgnoreCase("list"))
		{
			String year = "";
			year = data.getParameters().getString("year");
			
			yearInt = Integer.parseInt(year);
		}
		
		m_calObj.setDay(yearInt, m_calObj.getMonthInteger(), m_calObj.getDayOfMonth());
		
		//state.setCalObj(m_calObj);
		sstate.setAttribute(STATE_YEAR, new Integer(yearInt));
		sstate.setAttribute(STATE_MONTH, new Integer(m_calObj.getMonthInteger()));
		sstate.setAttribute(STATE_DAY, new Integer(m_calObj.getDayOfMonth()));
		
		state.setState("year");
		
	}   // doGoyear
	
	/**
	 * Action is used when doOk is requested when user click on Back button
	 */
	public void doOk(RunData data, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, data, CalendarActionState.class);
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState sstate = ((JetspeedRunData)data).getPortletSessionState(peid);
		
		// return to the state coming from
		String returnState = state.getReturnState();
		state.setState(returnState);
	}
	
	
	/**
	 * Action is used when the user click on the doRevise in the menu
	 */
	public void doRevise(RunData data, Context context)
	{
		CalendarEvent calendarEventObj = null;
		Calendar calendarObj = null;
		
		CalendarActionState state = (CalendarActionState)getState(context, data, CalendarActionState.class);
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState sstate = ((JetspeedRunData)data).getPortletSessionState(peid);
		
		String calId = state.getPrimaryCalendarReference();
		state.setPrevState(state.getState());
		state.setState("goToReviseCalendar");
		state.setIsNewCalendar(false);
		state.setfromAttachmentFlag("false");
		sstate.setAttribute(FREQUENCY_SELECT, null);
		sstate.setAttribute(CalendarAction.SSTATE__RECURRING_RULE, null);
		
		state.clearData();
		
		try
		{
			calendarObj = CalendarService.getCalendar(calId);
			try
			{
				String eventId = state.getCalendarEventId();
				// get the edit object, and lock the event for the furthur revise
				CalendarEventEdit edit = calendarObj.editEvent(eventId);
				state.setEdit(edit);
				state.setPrimaryCalendarEdit(edit);
				calendarEventObj = calendarObj.getEvent(eventId);
				state.setAttachments(calendarEventObj.getAttachments());
			}
			catch (IdUnusedException err)
			{
				// if this event doesn't exist, let user stay in activity view
				// set the state recorded ID as null
				// show the alert message
				// reset the menu button display, no revise/delete
				Log.debug("chef", this + ".IdUnusedException " + err);
				state.setState("description");
				state.setCalendarEventId("", "");
				String errorCode = rb.getString("java.alert.event"); 
				addAlert(sstate, errorCode);
			}
			catch (PermissionException err)
			{
				Log.debug("chef", this + ".PermissionException " + err);
			}
			catch (InUseException err)
			{
				Log.debug("chef", this + ".InUseException " + err);
				state.setState("description");
				String errorCode = rb.getString("java.alert.eventbeing");
				addAlert(sstate, errorCode);
			}
		}
		catch (IdUnusedException  e)
		{
			addAlert(sstate, rb.getString("java.alert.noexist"));
		}
		catch (PermissionException  e)
		{
			addAlert(sstate, rb.getString("java.alert.youcreate"));
		}
		
	} // doRevise
	
	
	/**
	 * Handle the "continue" button on the schedule import wizard.
	 */
	public void doScheduleContinue(RunData data, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, data, CalendarActionState.class);
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState sstate = ((JetspeedRunData)data).getPortletSessionState(peid);
		
		if ( SELECT_TYPE_IMPORT_WIZARD_STATE.equals(state.getImportWizardState()) )
		{
			// If the type is Outlook or MeetingMaker, the next state is
			// the "other" file select mode where we just select a file without
			// all of the extra info on the generic import page.
			
			String importType = data.getParameters ().getString(WIZARD_IMPORT_TYPE);
			
			
			if ( CalendarImporterService.OUTLOOK_IMPORT.equals(importType) || CalendarImporterService.MEETINGMAKER_IMPORT.equals(importType))
			{
				if (CalendarImporterService.OUTLOOK_IMPORT.equals(importType))
				{
					state.setImportWizardType(CalendarImporterService.OUTLOOK_IMPORT);
				}
				else
				{
					state.setImportWizardType(CalendarImporterService.MEETINGMAKER_IMPORT);
				}
				
				state.setImportWizardState(OTHER_SELECT_FILE_IMPORT_WIZARD_STATE);
			}
			else
			{
				// Remember the type we're importing
				state.setImportWizardType(CalendarImporterService.CSV_IMPORT);
				
				state.setImportWizardState(GENERIC_SELECT_FILE_IMPORT_WIZARD_STATE);
			}
		}
		else
		if ( GENERIC_SELECT_FILE_IMPORT_WIZARD_STATE.equals(state.getImportWizardState()) )
		{ 
			boolean importSucceeded = false;
			
			// Do the import and send us to the confirm page
			FileItem importFile = data.getParameters().getFileItem(WIZARD_IMPORT_FILE);
			
			try
			{
				Map columnMap = CalendarImporterService.getDefaultColumnMap(CalendarImporterService.CSV_IMPORT);
				
				String [] addFieldsCalendarArray = getCustomFieldsArray(state, sstate);

				if ( addFieldsCalendarArray != null )
				{
					// Add all custom columns.  Assume that there will be no 
					// name collisions. (Maybe a marginal assumption.)
					for ( int i=0; i < addFieldsCalendarArray.length; i++)
					{
						columnMap.put(
							addFieldsCalendarArray[i],
							addFieldsCalendarArray[i]);
					}
				}
						
				state.setWizardImportedEvents(
					CalendarImporterService.doImport(
						CalendarImporterService.CSV_IMPORT,
						new ByteArrayInputStream(importFile.get()),
						columnMap,
						addFieldsCalendarArray));

				importSucceeded = true;
			}
			catch (ImportException e)
			{
				addAlert(sstate, e.getMessage());
			}
			
			if ( importSucceeded )
			{
				// If all is well, go on to the confirmation page. 
				state.setImportWizardState(CONFIRM_IMPORT_WIZARD_STATE);
			}
			else
			{
				// If there are errors, send us back to the file selection page.
				state.setImportWizardState(GENERIC_SELECT_FILE_IMPORT_WIZARD_STATE);
			}
		}
		else
		if ( OTHER_SELECT_FILE_IMPORT_WIZARD_STATE.equals(state.getImportWizardState()) )
		{ 
			boolean importSucceeded = false;

			// Do the import and send us to the confirm page
			FileItem importFile = data.getParameters().getFileItem(WIZARD_IMPORT_FILE);
			
			String [] addFieldsCalendarArray = getCustomFieldsArray(state, sstate);
			
			try
			{
				state.setWizardImportedEvents(
					CalendarImporterService.doImport(
						state.getImportWizardType(),
						new ByteArrayInputStream(importFile.get()),
						null,
						addFieldsCalendarArray));
						
				importSucceeded = true;
			}
			catch (ImportException e)
			{
				addAlert(sstate, e.getMessage());
			} 
				
			if ( importSucceeded )
			{
				// If all is well, go on to the confirmation page. 
				state.setImportWizardState(CONFIRM_IMPORT_WIZARD_STATE);
			}
			else
			{
				// If there are errors, send us back to the file selection page.
				state.setImportWizardState(OTHER_SELECT_FILE_IMPORT_WIZARD_STATE);
			}
		}
		else
		if ( CONFIRM_IMPORT_WIZARD_STATE.equals(state.getImportWizardState()) )
		{
			// If there are errors, send us back to Either
			// the OTHER_SELECT_FILE or GENERIC_SELECT_FILE states.
			// Otherwise, we're done.
			
			List wizardCandidateEventList = state.getWizardImportedEvents();
			
			for ( int i =0; i < wizardCandidateEventList.size(); i++ )
			{
				// The line numbers are one-based.
				String selectionName =  "eventSelected" + (i+1);
				String selectdValue = data.getParameters().getString(selectionName);
				
				if ( TRUE_STRING.equals(selectdValue) )
				{
					// Add the events
					String calId = state.getPrimaryCalendarReference();
					try
					{
						Calendar calendarObj = CalendarService.getCalendar(calId);
						CalendarEvent event = (CalendarEvent) wizardCandidateEventList.get(i);
						
						CalendarEventEdit newEvent = calendarObj.addEvent();
						state.setEdit(newEvent);
						
						if ( event.getDescriptionFormatted() != null )
						{
							newEvent.setDescriptionFormatted( event.getDescriptionFormatted() ); 
						}
						
						// Range must be present at this point, so don't check for null.
						newEvent.setRange(event.getRange());

						if ( event.getDisplayName() != null )
						{
							newEvent.setDisplayName(event.getDisplayName());
						}
						 
						// The type must have either been set or defaulted by this point.
						newEvent.setType(event.getType());
						 
						if ( event.getLocation() != null )
						{
							newEvent.setLocation(event.getLocation());
						}
						 
						if ( event.getRecurrenceRule() != null )
						{
							newEvent.setRecurrenceRule(event.getRecurrenceRule()); 
						}
						
						String [] customFields = getCustomFieldsArray(state, sstate); 
						
						// Copy any custom fields.
						if ( customFields != null )
						{
							for ( int j = 0; j < customFields.length; j++ )
							{
								newEvent.setField(customFields[j], event.getField(customFields[j]));
							}
						} 

						calendarObj.commitEvent(newEvent);
						state.setEdit(null);
						
					}
					catch (IdUnusedException e)
					{
						addAlert(sstate, e.getMessage());
						Log.debug("chef", this +".doScheduleContinue(): " + e);
						break;
					}
					catch (PermissionException e)
					{
						addAlert(sstate, e.getMessage());
						Log.debug("chef", this +".doScheduleContinue(): " + e);
						break;
					}
				}
			}
			
			// Cancel wizard mode.
			doCancelImportWizard(data, context);
		}
		
	}

	/**
	 * Get an array of custom field names (if any)
	 */
	private String[] getCustomFieldsArray(
		CalendarActionState state,
		SessionState sstate)
	{
		Calendar calendarObj = null;
		
		try
		{
			calendarObj =
				CalendarService.getCalendar(
					state.getPrimaryCalendarReference());
		}
		catch (IdUnusedException e1)
		{
			// Ignore
		}
		catch (PermissionException e)
		{
			addAlert(sstate, e.getMessage());
		}
		
		// Get a current list of add fields.  This is a comma-delimited string.
		String[] addFieldsCalendarArray = null;
				
		if ( calendarObj != null )
		{
			String addfieldsCalendars = calendarObj.getEventFields();
			if (addfieldsCalendars != null)
			{
				addFieldsCalendarArray =
					fieldStringToArray(
						addfieldsCalendars,
						ADDFIELDS_DELIMITER);
			}
		}
		return addFieldsCalendarArray;
	}
	
	/**
	 * Handle the back button on the schedule import wizard
	 */
	public void doScheduleBack(RunData data, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, data, CalendarActionState.class);
		
		if (GENERIC_SELECT_FILE_IMPORT_WIZARD_STATE.equals(state.getImportWizardState())
			|| OTHER_SELECT_FILE_IMPORT_WIZARD_STATE.equals(state.getImportWizardState()))
		{
			state.setImportWizardState(SELECT_TYPE_IMPORT_WIZARD_STATE);
		}
		else
		if (CONFIRM_IMPORT_WIZARD_STATE.equals(state.getImportWizardState()))
		{
			if (CalendarImporterService.OUTLOOK_IMPORT.equals(state.getImportWizardType())
				|| CalendarImporterService.MEETINGMAKER_IMPORT.equals(state.getImportWizardType()))
			{
				state.setImportWizardState(OTHER_SELECT_FILE_IMPORT_WIZARD_STATE);
			}
			else
			{
				state.setImportWizardState(GENERIC_SELECT_FILE_IMPORT_WIZARD_STATE);
			}
		}
	}
	
	/**
	 * Called when the user cancels the import wizard.
	 */
	public void doCancelImportWizard(RunData data, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, data, CalendarActionState.class);

		// Get rid of any events
		state.setWizardImportedEvents(null);
		
		// Make sure that we start the wizard at the beginning.
		state.setImportWizardState(null);
		
		// Return to the previous state.
		state.setState(state.getPrevState());
	}
	
	/**
	 * Action is used when the docancel is requested when the user click on cancel  in the new view
	 */
	public void doCancel(RunData data, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, data, CalendarActionState.class);
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState sstate = ((JetspeedRunData)data).getPortletSessionState(peid);
		
		Calendar calendarObj = null;
		StringBuffer exceptionMessage = new StringBuffer();
		String currentState = state.getState();
		String returnState = state.getReturnState();
		
		if (currentState.equals(STATE_NEW))
		{
			// no need to release the lock. 
			// clear the saved recurring rule and the selected frequency
			sstate.setAttribute(CalendarAction.SSTATE__RECURRING_RULE, null);
			sstate.setAttribute(FREQUENCY_SELECT, null);
		}
		else
			if (currentState.equals(STATE_CUSTOMIZE_CALENDAR))
			{
				customizeCalendarPage.doCancel(data, context, state, getSessionState(data));
				returnState=state.getPrevState();
				if (returnState.endsWith("!!!fromDescription"))
				{
					state.setReturnState(returnState.substring(0, returnState.indexOf("!!!fromDescription")));
					returnState = "description";
				}
			}
			else
				if (currentState.equals(STATE_MERGE_CALENDARS))
				{
					mergedCalendarPage.doCancel(data, context, state, getSessionState(data));
					//returnState=state.getPrevState();
					returnState=state.getReturnState();
					
					if (returnState.endsWith("!!!fromDescription"))
					{
						state.setReturnState(returnState.substring(0, returnState.indexOf("!!!fromDescription")));
						returnState = "description";
					}
				}
				else	// in revise view, state name varies
					if ((currentState.equals("revise"))|| (currentState.equals("goToReviseCalendar")))
					{
						String calId = state.getPrimaryCalendarReference();
						
						if (state.getPrimaryCalendarEdit() != null)
						{
							try
							{
								calendarObj = CalendarService.getCalendar(calId);
								
								// the event is locked, now we need to release the lock
								calendarObj.cancelEvent(state.getPrimaryCalendarEdit());
								state.setPrimaryCalendarEdit(null);
								state.setEdit(null);
							}
							catch (IdUnusedException  e)
							{
								addAlert(sstate, rb.getString("java.alert.noexist"));
							}
							catch (PermissionException  e)
							{
								addAlert(sstate, rb.getString("java.alert.youcreate"));
							}
						}
						// clear the saved recurring rule and the selected frequency
						sstate.setAttribute(CalendarAction.SSTATE__RECURRING_RULE, null);
						sstate.setAttribute(FREQUENCY_SELECT, null);
					}
					else if (currentState.equals(STATE_SET_FREQUENCY))// cancel at frequency editing page
					{
						returnState = (String)sstate.getAttribute(STATE_BEFORE_SET_RECURRENCE);
					}

		state.setState(returnState);
		
		state.setAttachments(null);
	}   // doCancel
	
	
	/**
	 * Action is used when the doBack is called when the user click on the back on the EventActivity view
	 */
	public void doBack(RunData data, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, data, CalendarActionState.class);
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState sstate = ((JetspeedRunData)data).getPortletSessionState(peid);
		
		Calendar calendarObj = null;
		String calId = state.getPrimaryCalendarReference();
		StringBuffer exceptionMessage = new StringBuffer();
		
		try
		{
			calendarObj = CalendarService.getCalendar(calId);
			
			// the event is locked, now we need to release the lock
			calendarObj.cancelEvent(state.getPrimaryCalendarEdit());
			state.setPrimaryCalendarEdit(null);
			state.setEdit(null);
		}
		catch (IdUnusedException  e)
		{
			addAlert(sstate, rb.getString("java.alert.noexist"));
		}
		catch (PermissionException  e)
		{
			addAlert(sstate, rb.getString("java.alert.youcreate"));
		}
		
		
		String returnState = state.getReturnState();
		state.setState(returnState);
		
	}   // doBack
	
	
	
	/**
	 * Action is used when the doDelete is called when the user click on delete in menu
	 */
	
	public void doDelete(RunData data, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, data, CalendarActionState.class);
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState sstate = ((JetspeedRunData)data).getPortletSessionState(peid);
		
		CalendarEvent calendarEventObj = null;
		Calendar calendarObj = null;
		String calId = state.getPrimaryCalendarReference();
		StringBuffer exceptionMessage = new StringBuffer();
		
		try
		{
			calendarObj = CalendarService.getCalendar(calId);
			
			try
			{
				String eventId = state.getCalendarEventId();
				// get the edit object, and lock the event for the furthur revise
				CalendarEventEdit edit = calendarObj.editEvent(eventId);
				state.setEdit(edit);
				state.setPrimaryCalendarEdit(edit);
				calendarEventObj = calendarObj.getEvent(eventId);
				state.setAttachments(calendarEventObj.getAttachments());
				
				// after deletion, it needs to go back to previous page
				// if coming from description, it won't go back to description
				// but the state one step ealier
				String returnState = state.getState();
				if (!returnState.equals("description"))
				{
					state.setReturnState(returnState);
				}
				state.setState("delete");
			}
			catch (IdUnusedException err)
			{
				// if this event doesn't exist, let user stay in activity view
				// set the state recorded ID as null
				// show the alert message
				// reset the menu button display, no revise/delete
				Log.debug("chef", this + ".IdUnusedException " + err);
				state.setState("description");
				state.setCalendarEventId("", "");
				String errorCode = rb.getString("java.alert.event");
				addAlert(sstate, errorCode);
			}
			catch (PermissionException err)
			{
				Log.debug("chef", this + ".PermissionException " + err);
			}
			catch (InUseException err)
			{
				Log.debug("chef", this + ".InUseException delete" + err);
				state.setState("description");
				String errorCode = rb.getString("java.alert.eventbeing");
				addAlert(sstate, errorCode);
			}
		}
		catch (IdUnusedException  e)
		{
			addAlert(sstate, rb.getString("java.alert.noexist"));
		}
		catch (PermissionException  e)
		{
			addAlert(sstate, rb.getString("java.alert.youcreate"));
		}
	}   // doDelete
	
	/**
	 * Action is used when the doConfirm is called when the user click on confirm to delete event in the delete view.
	 */
	
	public void doConfirm(RunData data, Context context)
	{
		Calendar calendarObj = null;
		CalendarActionState state = (CalendarActionState)getState(context, data, CalendarActionState.class);
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState sstate = ((JetspeedRunData)data).getPortletSessionState(peid);
		
		// read the intention field
		String intentionStr = data.getParameters().getString("intention");
		int intention = CalendarService.MOD_NA;
		if ("t".equals(intentionStr)) intention = CalendarService.MOD_THIS;

		StringBuffer exceptionMessage = new StringBuffer();
		
		String calId = state.getPrimaryCalendarReference();
		
		try
		{
			calendarObj = CalendarService.getCalendar(calId);
			CalendarEventEdit edit = state.getPrimaryCalendarEdit();
			calendarObj.removeEvent(edit, intention);
			state.setPrimaryCalendarEdit(null);
		}
		catch (IdUnusedException  e)
		{
			addAlert(sstate, rb.getString("java.alert.noexist"));
			Log.debug("chef", this + ".doConfirm(): " + e);
		}
		catch (PermissionException  e)
		{
			addAlert(sstate, rb.getString("java.alert.youcreate"));
			Log.debug("chef", this + ".doConfirm(): " + e);
		}
		
		String returnState = state.getReturnState();
		state.setState(returnState);
		
	} // doConfirm
	
	
   public void doView (RunData data, Context context)
   {
	   	SessionState state = ((JetspeedRunData)data).getPortletSessionState (((JetspeedRunData)data).getJs_peid ());
	
	   	String viewMode = data.getParameters ().getString("view");
		
			if (viewMode.equalsIgnoreCase(VIEW_BY_DAY))
		   	{
				doMenueday(data, context);
		   	}
		   	else if (viewMode.equalsIgnoreCase(VIEW_BY_WEEK))
		   	{
				doWeek(data, context);
		   	}
		   	else if (viewMode.equalsIgnoreCase(VIEW_BY_MONTH))
			{
				doMonth(data, context);
			}
			else if (viewMode.equalsIgnoreCase(VIEW_BY_YEAR))
			{
				doYear(data, context);
			}
			else if (viewMode.equalsIgnoreCase(VIEW_LIST))
			{
				doList(data, context);
			}
			state.setAttribute(STATE_SELECTED_VIEW, viewMode);
	
   }	// doView
   
	/**
	 * Action doYear is requested when the user click on Year on menu
	 */
	
	public void doYear(RunData data, Context context)
	{
		
		CalendarActionState state = (CalendarActionState)getState(context, data, CalendarActionState.class);
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState sstate = ((JetspeedRunData)data).getPortletSessionState(peid);
		
		state.setState("year");
	}   // doYear
	
	/**
	 * Action doWeek is requested when the user click on the week item in then menu
	 */
	
	public void doWeek(RunData data, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, data, CalendarActionState.class);
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState sstate = ((JetspeedRunData)data).getPortletSessionState(peid);
		
		state.setState("week");
	}   // doWeek
	
	
	/**
	 * Action doDay is requested when the user click on the day item in the menue
	 */
	
	public void doDay(RunData data, Context context)
	{
		
		String year = null;
		year = data.getParameters().getString("year");
		String month = null;
		month = data.getParameters().getString("month");
		String day = null;
		day = data.getParameters().getString("day");
		
		CalendarActionState state = (CalendarActionState)getState(context, data, CalendarActionState.class);
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState sstate = ((JetspeedRunData)data).getPortletSessionState(peid);
		
		//CalendarUtil m_calObj = state.getCalObj();
		//m_calObj.setDay(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day));
		//state.setCalObj(m_calObj);
		
		sstate.setAttribute(STATE_YEAR, new Integer(Integer.parseInt(year)));
		sstate.setAttribute(STATE_MONTH, new Integer(Integer.parseInt(month)));
		sstate.setAttribute(STATE_DAY, new Integer(Integer.parseInt(day)));
		
		state.setPrevState(state.getState()); // remember the coming state from Month, Year or List
		state.setState("day");
	}   // doDay
	
	
	/**
	 * Action doToday is requested when the user click on "Go to today" button
	 */
	public void doToday(RunData data, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, data, CalendarActionState.class);
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState sstate = ((JetspeedRunData)data).getPortletSessionState(peid);
		
		CalendarUtil m_calObj = new CalendarUtil();
		
		Time m_time = TimeService.newTime();
		TimeBreakdown b = m_time.breakdownLocal();
		m_calObj.setDay(b.getYear(), b.getMonth(), b.getDay());
		
		sstate.setAttribute(STATE_YEAR, new Integer(b.getYear()));
		sstate.setAttribute(STATE_MONTH, new Integer(b.getMonth()));
		sstate.setAttribute(STATE_DAY, new Integer(b.getDay()));
		
		state.setState("day");
		
		//for dropdown menu display purpose
		sstate.setAttribute(STATE_SELECTED_VIEW, VIEW_BY_DAY);
		
	}   // doToday
	
	
	/**
	 * Action doCustomDate is requested when the user specifies a start/end date
	 * to filter the list view.
	 */
	public void doCustomDate(RunData data, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, data, CalendarActionState.class);
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState sstate = ((JetspeedRunData)data).getPortletSessionState(peid);
		
		String sY = data.getParameters().getString(TIME_FILTER_SETTING_CUSTOM_START_YEAR);
		String sM = data.getParameters().getString(TIME_FILTER_SETTING_CUSTOM_START_MONTH);
		String sD = data.getParameters().getString(TIME_FILTER_SETTING_CUSTOM_START_DAY);
		String eY = data.getParameters().getString(TIME_FILTER_SETTING_CUSTOM_END_YEAR);
		String eM = data.getParameters().getString(TIME_FILTER_SETTING_CUSTOM_END_MONTH);
		String eD = data.getParameters().getString(TIME_FILTER_SETTING_CUSTOM_END_DAY);
		if (sM.length() == 1) sM = "0"+sM;
		if (eM.length() == 1) eM = "0"+eM;
		if (sD.length() == 1) sD = "0"+sD;
		if (eD.length() == 1) eD = "0"+eD;
		sY = sY.substring(2);
		eY = eY.substring(2);
		
		String startingDateStr = sM + "/" + sD + "/" + sY;
		String endingDateStr   = eM + "/" + eD + "/" + eY;
		
		// Get the start/end dates from the user.
		//String startingDateStr = data.getParameters().getString(TIME_FILTER_SETTING_CUSTOM_START_DATE_VAR);
		//String endingDateStr = data.getParameters().getString(TIME_FILTER_SETTING_CUSTOM_END_DATE_VAR);

		// Pass in a buffer for a possible error message.
		StringBuffer errorMessage = new StringBuffer();
		
		// Try to simultaneously set the start/end dates.
		// If that doesn't work, add an error message.
		if ( !state.getCalendarFilter().setStartAndEndListViewDates(startingDateStr, endingDateStr, errorMessage) )
		{
			addAlert(sstate, errorMessage.toString());
		}
		
	}   // doCustomDate
	
	/**
	 * Action doFilter is requested when the user clicks on the list box
	 * to select a filtering mode for the list view.
	 */
	public void doFilter(RunData data, Context context)
	{
		CalendarActionState state =
			(CalendarActionState) getState(context,
				data,
				CalendarActionState.class);

		state.getCalendarFilter().setListViewFilterMode(
			data.getParameters().getString(TIME_FILTER_OPTION_VAR));
		
	}   // doFilter
	
	/**
	 * Action is requestd when the user select day from the menu avilable in some views.
	 */
	
	public void doMenueday(RunData data, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, data, CalendarActionState.class);
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState sstate = ((JetspeedRunData)data).getPortletSessionState(peid);
		
		state.setState("day");
	}   // doMenueday
	
	
	
	/**
	 * Action is requsted when the user select day from menu in Activityevent view.
	 */
	
	public void doActivityday(RunData data, Context context)
	{
		
		CalendarEvent ce = null;
		Calendar calendarObj = null;
		
		CalendarActionState state = (CalendarActionState)getState(context, data, CalendarActionState.class);
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState sstate = ((JetspeedRunData)data).getPortletSessionState(peid);
		
		CalendarUtil m_calObj = new CalendarUtil();
		
		StringBuffer exceptionMessage = new StringBuffer();
		String id = state.getCalendarEventId();
		
		String calId = state.getPrimaryCalendarReference();
		
		
		try
		{
			calendarObj = CalendarService.getCalendar(calId);
			ce = calendarObj.getEvent(id);
		}
		catch (IdUnusedException  e)
		{
			addAlert(sstate, rb.getString("java.alert.noexist"));
			Log.debug("chef", this + ".doActivityday(): " + e);
		}
		catch (PermissionException  e)
		{
			addAlert(sstate, rb.getString("java.alert.youcreate"));
			Log.debug("chef", this + ".doActivityday(): " + e);
		}
		
		TimeRange tr = ce.getRange();
		Time t = tr.firstTime();
		TimeBreakdown b = t.breakdownLocal();
		m_calObj.setDay(b.getYear(),b.getMonth(),b.getDay()) ;
		
		sstate.setAttribute(STATE_YEAR, new Integer(b.getYear()));
		sstate.setAttribute(STATE_MONTH, new Integer(b.getMonth()));
		sstate.setAttribute(STATE_DAY, new Integer(b.getDay()));
		
		state.setState("day");
		
	} // doActivityDay
	
	/**
	 * Action doNext is called when the user click on next button to move to next day, next week, next month or next year.
	 */
	
	public void doNext(RunData data, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, data, CalendarActionState.class);
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState sstate = ((JetspeedRunData)data).getPortletSessionState(peid);
		
		
		String currentstate = state.getState();
		
		Time m_time = TimeService.newTime();
		TimeBreakdown b = m_time.breakdownLocal();
		int stateYear = b.getYear();
		int stateMonth = b.getMonth();
		int stateDay = b.getDay();
		if ((sstate.getAttribute(STATE_YEAR) != null) && (sstate.getAttribute(STATE_MONTH) != null) && (sstate.getAttribute(STATE_DAY) != null))
		{
			stateYear = ((Integer)sstate.getAttribute(STATE_YEAR)).intValue();
			stateMonth = ((Integer)sstate.getAttribute(STATE_MONTH)).intValue();
			stateDay = ((Integer)sstate.getAttribute(STATE_DAY)).intValue();
		}
		CalendarUtil m_calObj = new CalendarUtil();
		
		m_calObj.setDay(stateYear, stateMonth, stateDay);
		
		if (currentstate.equals("month"))
		{
			m_calObj.getNextMonth();
		}
		
		if (currentstate.equals("year"))
		{
			m_calObj.setNextYear();
		}
		
		if (currentstate.equals("day"))
		{
			String date = m_calObj.getNextDate();
			state.setnextDate(date);
		}
		
		if (currentstate.equals("week"))
		{
			m_calObj.setNextWeek();
		}
		
		sstate.setAttribute(STATE_YEAR, new Integer(m_calObj.getYear()));
		sstate.setAttribute(STATE_MONTH, new Integer(m_calObj.getMonthInteger()));
		sstate.setAttribute(STATE_DAY, new Integer(m_calObj.getDayOfMonth()));
		
	}	// doNext
	
	
	/**
	 * Action doNextday is called when the user click on "Tomorrow" link in day view
	 */
	
	public void doNextday(RunData data, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, data, CalendarActionState.class);
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState sstate = ((JetspeedRunData)data).getPortletSessionState(peid);
		
		
		String currentstate = state.getState();
		
		Time m_time = TimeService.newTime();
		TimeBreakdown b = m_time.breakdownLocal();
		int stateYear = b.getYear();
		int stateMonth = b.getMonth();
		int stateDay = b.getDay();
		if ((sstate.getAttribute(STATE_YEAR) != null) && (sstate.getAttribute(STATE_MONTH) != null) && (sstate.getAttribute(STATE_DAY) != null))
		{
			stateYear = ((Integer)sstate.getAttribute(STATE_YEAR)).intValue();
			stateMonth = ((Integer)sstate.getAttribute(STATE_MONTH)).intValue();
			stateDay = ((Integer)sstate.getAttribute(STATE_DAY)).intValue();
		}
		CalendarUtil m_calObj = new CalendarUtil(); //null;
		m_calObj.setDay(stateYear, stateMonth, stateDay);
		
		if (currentstate.equals("day"))
		{
			String date = m_calObj.getNextDate();
			state.setnextDate(date);
			
			sstate.setAttribute(STATE_YEAR, new Integer(m_calObj.getYear()));
			sstate.setAttribute(STATE_MONTH, new Integer(m_calObj.getMonthInteger()));
			sstate.setAttribute(STATE_DAY, new Integer(m_calObj.getDayOfMonth()));
			
			// if this function is called thru "tomorrow" link
			// the default page has to be changed to "first"
			state.setCurrentPage("first");
		}
	}   // doNextday
	
	/**
	 * Action doPrev is requested when the user click on the prev button to move into pre day, month, year, or week.
	 */
	
	public void doPrev(RunData data, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, data, CalendarActionState.class);
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState sstate = ((JetspeedRunData)data).getPortletSessionState(peid);
		
		Time m_time = TimeService.newTime();
		TimeBreakdown b = m_time.breakdownLocal();
		int stateYear = b.getYear();
		int stateMonth = b.getMonth();
		int stateDay = b.getDay();
		if ((sstate.getAttribute(STATE_YEAR) != null) && (sstate.getAttribute(STATE_MONTH) != null) && (sstate.getAttribute(STATE_DAY) != null))
		{
			stateYear = ((Integer)sstate.getAttribute(STATE_YEAR)).intValue();
			stateMonth = ((Integer)sstate.getAttribute(STATE_MONTH)).intValue();
			stateDay = ((Integer)sstate.getAttribute(STATE_DAY)).intValue();
		}
		CalendarUtil m_calObj = new CalendarUtil();
		m_calObj.setDay(stateYear, stateMonth, stateDay);
		
		String currentstate = state.getState();
		
		if (currentstate.equals("month"))
		{
			m_calObj.getPrevMonth();
			//state.setCalObj(m_calObj);
		}
		
		if (currentstate.equals("year"))
		{
			m_calObj.setPrevYear();
			//state.setCalObj(m_calObj);
		}
		
		if (currentstate.equals("day"))
		{
			String date = m_calObj.getPrevDate();
			//state.setCalObj(m_calObj);
			state.setprevDate(date);
		}
		
		if (currentstate.equals("week"))
		{
			m_calObj.setPrevWeek();
			//state.setCalObj(m_calObj);
		}
		sstate.setAttribute(STATE_YEAR, new Integer(m_calObj.getYear()));
		sstate.setAttribute(STATE_MONTH, new Integer(m_calObj.getMonthInteger()));
		sstate.setAttribute(STATE_DAY, new Integer(m_calObj.getDayOfMonth()));
		
	}   // doPrev
	
	/**
	 * Action doPreday is called when the user click on "Yesterday" link in day view
	 */
	
	public void doPreday(RunData data, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, data, CalendarActionState.class);
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState sstate = ((JetspeedRunData)data).getPortletSessionState(peid);
		
		String currentstate = state.getState();
		
		Time m_time = TimeService.newTime();
		TimeBreakdown b = m_time.breakdownLocal();
		int stateYear = b.getYear();
		int stateMonth = b.getMonth();
		int stateDay = b.getDay();
		if ((sstate.getAttribute(STATE_YEAR) != null) && (sstate.getAttribute(STATE_MONTH) != null) && (sstate.getAttribute(STATE_DAY) != null))
		{
			stateYear = ((Integer)sstate.getAttribute(STATE_YEAR)).intValue();
			stateMonth = ((Integer)sstate.getAttribute(STATE_MONTH)).intValue();
			stateDay = ((Integer)sstate.getAttribute(STATE_DAY)).intValue();
		}
		CalendarUtil m_calObj = new CalendarUtil(); //null;
		m_calObj.setDay(stateYear, stateMonth, stateDay);
		
		if (currentstate.equals("day"))
		{
			String date = m_calObj.getPrevDate();
			//state.setCalObj(m_calObj);
			
			sstate.setAttribute(STATE_YEAR, new Integer(m_calObj.getYear()));
			sstate.setAttribute(STATE_MONTH, new Integer(m_calObj.getMonthInteger()));
			sstate.setAttribute(STATE_DAY, new Integer(m_calObj.getDayOfMonth()));
			
			state.setprevDate(date);
			
			// if this function is called thru "Yesterday" link, it goes the last page of yesterday
			// the default page has to be changed to "third"
			state.setCurrentPage("third");
		}
	}   // doPreday
	
	
	/**
	 * Enter the schedule import wizard
	 */
	public void doImport(RunData data, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, data, CalendarActionState.class);
		
		// Remember the state prior to entering the wizard.
		state.setPrevState(state.getState());
		
		// Enter wizard mode.
		state.setState(STATE_SCHEDULE_IMPORT);
		
	}   // doImport

	/**
	 * Action doNew is requested when the user click on New in the menu
	 */
	
	public void doNew(RunData data, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, data, CalendarActionState.class);
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState sstate = ((JetspeedRunData)data).getPortletSessionState(peid);
		
		// store the state coming from
		String returnState = state.getState();
		if (returnState.equals("description"))
		{
		}
		else
		{
			state.setReturnState(returnState);
		}
		
		state.clearData();
		state.setAttachments(null);
		state.setPrevState(state.getState());
		state.setState("new");
		state.setCalendarEventId("", "");
		state.setIsNewCalendar(true);
		state.setIsPastAlertOff(true);
		sstate.setAttribute(FREQUENCY_SELECT, null);
		sstate.setAttribute(CalendarAction.SSTATE__RECURRING_RULE, null);
		
	}   // doNew
	
	
	/**
	 * Action doAdd is requested when the user click on the add in the new view to add an event into a calendar.
	 */
	
	public void doAdd(RunData runData, Context context)
	{
		CalendarUtil m_calObj = new CalendarUtil();// null;
		Calendar calendarObj = null;
		int houri;
		
		CalendarActionState state = (CalendarActionState)getState(context, runData, CalendarActionState.class);
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState sstate = ((JetspeedRunData)runData).getPortletSessionState(peid);
		
		StringBuffer exceptionMessage = new StringBuffer();
		
		Time m_time = TimeService.newTime();
		TimeBreakdown b = m_time.breakdownLocal();
		int stateYear = b.getYear();
		int stateMonth = b.getMonth();
		int stateDay = b.getDay();
		if ((sstate.getAttribute(STATE_YEAR) != null) && (sstate.getAttribute(STATE_MONTH) != null) && (sstate.getAttribute(STATE_DAY) != null))
		{
			stateYear = ((Integer)sstate.getAttribute(STATE_YEAR)).intValue();
			stateMonth = ((Integer)sstate.getAttribute(STATE_MONTH)).intValue();
			stateDay = ((Integer)sstate.getAttribute(STATE_DAY)).intValue();
		}
		m_calObj.setDay(stateYear, stateMonth, stateDay);
		
		String hour = "";
		hour = runData.getParameters().getString("startHour");
		String title ="";
		title = runData.getParameters().getString("activitytitle");
		String minute = "";
		minute = runData.getParameters().getString("startMinute");
		String dhour = "";
		dhour = runData.getParameters().getString("duHour");
		String dminute = "";
		dminute = runData.getParameters().getString("duMinute");
		String description = "";
		description = runData.getParameters().getString("description");
		description = processFormattedTextFromBrowser(sstate, description);
		String month = "";
		month = runData.getParameters().getString("month");
		
		String day = "";
		day = runData.getParameters().getString("day");
		String year = "";
		year = runData.getParameters().getString("yearSelect");
		String timeType = "";
		timeType = runData.getParameters().getString("startAmpm");
		String type = "";
		type = runData.getParameters().getString("eventType");
		String location = "";
		location = runData.getParameters().getString("location");
		
		String calId = state.getPrimaryCalendarReference();
		try
		{
			calendarObj = CalendarService.getCalendar(calId);
		}
		catch(IdUnusedException e)
		{
			exceptionMessage.append(rb.getString("java.alert.thereisno"));
			Log.debug("chef", this + ".doAdd(): " + e);
		}
		catch (PermissionException e)
		{
			exceptionMessage.append(rb.getString("java.alert.youdont")); 
			Log.debug("chef", this + ".doAdd(): " + e);
		}
		
		Map addfieldsMap = new HashMap();
		
		// Add any additional fields in the calendar.
		customizeCalendarPage.loadAdditionalFieldsMapFromRunData(runData, addfieldsMap, calendarObj);
		
		if (timeType.equals("pm"))
		{
			if (Integer.parseInt(hour)>11)
				houri = Integer.parseInt(hour);
			else
				houri = Integer.parseInt(hour)+12;
		}
		else if (timeType.equals("am") && Integer.parseInt(hour)==12)
		{
			// set 12 AM as the beginning of one day
			houri = 0;
		}
		else
		{
			houri = Integer.parseInt(hour);
		}
		
		Time now_time = TimeService.newTime();
		Time event_startTime = TimeService.newTimeLocal(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), houri, Integer.parseInt(minute), 0,  0);
		
		// conditions for an new event:
		// 1st, frequency not touched, no save state rule or state freq (0, 0)
		// --> non-recurring, no alert needed (0)
		// 2st, frequency revised, there is a state-saved rule, and state-saved freq exists (1, 1)
		// --> no matter if the start has been modified, compare the ending and starting date, show alert if needed (1)
		// 3th, frequency revised, the state saved rule is null, but state-saved freq exists (0, 1)
		// --> non-recurring, no alert needed (0)
		// so the only possiblityto show the alert is under condistion 2.
		
		boolean earlierEnding = false;
		
		String freq = "";
		if ((( freq = (String) sstate.getAttribute(FREQUENCY_SELECT))!= null)
		&& (!(freq.equals(FREQ_ONCE))))
		{
			RecurrenceRule rule = (RecurrenceRule) sstate.getAttribute(CalendarAction.SSTATE__RECURRING_RULE);
			if (rule != null)
			{
				Time startingTime = TimeService.newTimeLocal(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day),houri,Integer.parseInt(minute),00,000);
				
				Time endingTime = rule.getUntil();
				if ((endingTime != null) && endingTime.before(startingTime))
					earlierEnding = true;
			} // if (rule != null)
		} // if state saved freq is not null, and it not equals "once"
		
		String intentionStr = ""; // there is no recurrence modification intention for new event
		
		if(title.length()==0)
		{
			String errorCode = rb.getString("java.pleasetitle");
			addAlert(sstate, errorCode);
			
			state.setNewData(state.getPrimaryCalendarReference(), title,description,Integer.parseInt(month),Integer.parseInt(day),year,houri,Integer.parseInt(minute),Integer.parseInt(dhour),Integer.parseInt(dminute),type,timeType,location, addfieldsMap, intentionStr);
			state.setState("new");
		}
		else if(hour.equals("100") || minute.equals("100"))
		{
			String errorCode = rb.getString("java.pleasetime");
			addAlert(sstate, errorCode);
			
			state.setNewData(state.getPrimaryCalendarReference(), title,description,Integer.parseInt(month),Integer.parseInt(day),year,houri,Integer.parseInt(minute),Integer.parseInt(dhour),Integer.parseInt(dminute),type,timeType,location, addfieldsMap, intentionStr);
			state.setState("new");
		}
		else if( earlierEnding ) // if ending date is earlier than the starting date, show alert
		{
			addAlert(sstate, ALERT_ENDING_EARLIER_THAN_STARTING);
			
			state.setNewData(calId, title,description,Integer.parseInt(month),Integer.parseInt(day),year,houri,Integer.parseInt(minute),Integer.parseInt(dhour),Integer.parseInt(dminute),type,timeType,location, addfieldsMap, intentionStr);
			state.setState("new");
		}
		else if( event_startTime.before(now_time) && state.getIsPastAlertOff() )
		{
			// IsPastAlertOff
			// true: no alert shown -> then show the alert, set false;
			// false: Alert shown, if user click ADD - doAdd again -> accept it, set true, set alert empty;
			
			String errorCode = rb.getString("java.alert.past");
			addAlert(sstate, errorCode);
			
			state.setNewData(state.getPrimaryCalendarReference(), title,description,Integer.parseInt(month),Integer.parseInt(day),year,houri,Integer.parseInt(minute),Integer.parseInt(dhour),Integer.parseInt(dminute),type,timeType,location, addfieldsMap, intentionStr);
			state.setState("new");
			state.setIsPastAlertOff(false);
		}
		else
		{
			try
			{
				calendarObj = CalendarService.getCalendar(calId);
				
				Time timeObj = TimeService.newTimeLocal(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day),houri,Integer.parseInt(minute),00,000);
				
				long du = (((Integer.parseInt(dhour) * 60)*60)*1000) + ((Integer.parseInt(dminute)*60)*(1000));
				Time endTime = TimeService.newTime(timeObj.getTime() + du);
				boolean includeEndTime = false;
				if (du==0)
				{
					includeEndTime = true;
				}
				TimeRange range = TimeService.newTimeRange(timeObj, endTime, true, includeEndTime);
				//TimeRange range = TimeService.newTimeRange(timeObj.getTime(),du);
				ReferenceVector attachments = state.getAttachments();
				
				// create the event
				CalendarEventEdit edit = calendarObj.addEvent();
				state.setEdit(edit);
				edit.setRange(range);
				edit.setDisplayName(title);
				edit.setDescriptionFormatted(description);
				edit.setType(type);
				edit.setLocation(location);
				edit.replaceAttachments(attachments);
				setFields(edit, addfieldsMap);
				
				RecurrenceRule rule = (RecurrenceRule) sstate.getAttribute(CalendarAction.SSTATE__RECURRING_RULE);
				// for a brand new event, there is no saved recurring rule
				if (rule != null)
					edit.setRecurrenceRule(rule);
				else
					edit.setRecurrenceRule(null);
				
				// save it
				calendarObj.commitEvent(edit);
				state.setEdit(null);
				
				state.setIsNewCalendar(false);
				
				m_calObj.setDay(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day));
				
				//state.setCalObj(m_calObj);
				sstate.setAttribute(STATE_YEAR, new Integer(m_calObj.getYear()));
				sstate.setAttribute(STATE_MONTH, new Integer(m_calObj.getMonthInteger()));
				sstate.setAttribute(STATE_DAY, new Integer(m_calObj.getDayOfMonth()));
				
				// clear the saved recurring rule and the selected frequency
				sstate.setAttribute(CalendarAction.SSTATE__RECURRING_RULE, null);
				sstate.setAttribute(FREQUENCY_SELECT, null);
				
				// set the return state to be the state before new/revise
				String returnState = state.getReturnState();
				if (returnState != null)
				{
					state.setState(returnState);
				}
				else
				{
					state.setState("week");
				}
			}
			catch (IdUnusedException  e)
			{
				addAlert(sstate, rb.getString("java.alert.noexist"));
				Log.debug("chef", this + ".doAdd(): " + e);
			}
			
			catch (PermissionException  e)
			{
				addAlert(sstate, rb.getString("java.alert.youcreate"));
				Log.debug("chef", this + ".doAdd(): " + e);
			}
		}   // elseif
	}   // doAdd
	
	
	/**
	 * Action doUpdate is requested when the user click on the save button on the revise screen.
	 */
	
	public void doUpdate(RunData runData, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, runData, CalendarActionState.class);
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState sstate = ((JetspeedRunData)runData).getPortletSessionState(peid);
		
		CalendarUtil m_calObj= new CalendarUtil();
		
		// read the intention field
		String intentionStr = runData.getParameters().getString("intention");
		int intention = CalendarService.MOD_NA;
		if ("t".equals(intentionStr)) intention = CalendarService.MOD_THIS;
		
		// See if we're in the "options" state.
		if (state.getState().equalsIgnoreCase(STATE_MERGE_CALENDARS))
		{
			mergedCalendarPage.doUpdate(runData, context, state, getSessionState(runData));
			
			// ReturnState was set up above.  Switch states now.
			String returnState = state.getReturnState();
			if (returnState.endsWith("!!!fromDescription"))
			{
				state.setReturnState(returnState.substring(0, returnState.indexOf("!!!fromDescription")));
				state.setState("description");
			}
			else
			{
				state.setReturnState("");
				state.setState(returnState);
			}
			
		}
		else
			if (state.getState().equalsIgnoreCase(STATE_CUSTOMIZE_CALENDAR))
			{
				customizeCalendarPage.doDeletefield( runData, context, state, getSessionState(runData));
				customizeCalendarPage.doUpdate(runData, context, state, getSessionState(runData));
				
				if (!state.getDelfieldAlertOff())
				{
					state.setState(CalendarAction.STATE_CUSTOMIZE_CALENDAR);
				}
				else
				{
					// ReturnState was set up above.  Switch states now.
					String returnState = state.getReturnState();
					if (returnState.endsWith("!!!fromDescription"))
					{
						state.setReturnState(returnState.substring(0, returnState.indexOf("!!!fromDescription")));
						state.setState("description");
					}
					else
					{
						state.setReturnState("");
						state.setState(returnState);
					}
				} // if (!state.getDelfieldAlertOff())
			}
			else
			{
				int houri;
				Calendar calendarObj = null;
				
				StringBuffer exceptionMessage = new StringBuffer();
				
				String hour = "";
				hour = runData.getParameters().getString("startHour");
				String title = "";
				title = runData.getParameters().getString("activitytitle");
				String minute = "";
				minute = runData.getParameters().getString("startMinute");
				String dhour = "";
				dhour = runData.getParameters().getString("duHour");
				String dminute = "";
				dminute = runData.getParameters().getString("duMinute");
				String description = "";
				description = runData.getParameters().getString("description");
				description = processFormattedTextFromBrowser(sstate, description);
				String month = "";
				month = runData.getParameters().getString("month");
				String day = "";
				day = runData.getParameters().getString("day");
				String year = "";
				year = runData.getParameters().getString("yearSelect");
				String timeType = "";
				timeType = runData.getParameters().getString("startAmpm");
				String type = "";
				type = runData.getParameters().getString("eventType");
				String location = "";
				location = runData.getParameters().getString("location");
				
				String calId = state.getPrimaryCalendarReference();
				try
				{
					calendarObj = CalendarService.getCalendar(calId);
				}
				catch (IdUnusedException e)
				{
					exceptionMessage.append(rb.getString("java.alert.theresisno"));
					Log.debug("chef", this +".doUpdate() Other: " + e);
				}
				catch (PermissionException e)
				{
					exceptionMessage.append(rb.getString("java.alert.youdont"));
					Log.debug("chef", this +".doUpdate() Other: " + e);
				}
				
				Map addfieldsMap = new HashMap();
				
				// Add any additional fields in the calendar.
				customizeCalendarPage.loadAdditionalFieldsMapFromRunData(runData, addfieldsMap, calendarObj);
				
				if (timeType.equals("pm"))
				{
					if (Integer.parseInt(hour)>11)
						houri = Integer.parseInt(hour);
					else
						houri = Integer.parseInt(hour)+12;
				}
				else if (timeType.equals("am") && Integer.parseInt(hour)==12)
				{
					houri = 0;
				}
				else
				{
					houri = Integer.parseInt(hour);
				}
				
				// conditions for an existing event: (if recurring event, if state-saved-rule exists, if state-saved-freq exists)
				// 1st, an existing recurring one, just revised without frequency change, no save state rule or state freq (1, 0, 0)
				// --> the starting time might has been modified, compare the ending and starting date, show alert if needed (1)
				// 2st, and existing non-recurring one, just revised, no save state rule or state freq (0, 0, 0)
				// --> non-recurring, no alert needed (0)
				// 3rd, an existing recurring one, frequency revised, there is a state-saved rule, and state-saved freq exists (1, 1, 1)
				// --> no matter if the start has been modified, compare the ending and starting date, show alert if needed (1)
				// 4th, an existing recurring one, changed to non-recurring, the state saved rule is null, but state-saved freq exists (1, 0, 1)
				// --> non-recurring, no alert needed (0)
				// 5th, an existing non-recurring one, changed but kept as non-recurring, the state-saved rule is null, but state-saved freq exists (1, 0, 1)
				// --> non-recurring, no alert needed (0)
				// 6th, an existing recurring one, changed only the starting time, showed alert for ealier ending time, 
				// so the only possiblity to show the alert is under condistion 1 & 3: recurring one stays as recurring
				
				boolean earlierEnding = false;
				
				
				CalendarEventEdit edit = state.getPrimaryCalendarEdit();
				if (edit != null)
				{
					RecurrenceRule editRule = edit.getRecurrenceRule();
					if ( editRule != null)
					{
						String freq = (String) sstate.getAttribute(FREQUENCY_SELECT);
						RecurrenceRule rule = (RecurrenceRule) sstate.getAttribute(CalendarAction.SSTATE__RECURRING_RULE);
						boolean comparisonNeeded = false;
						
						if ((freq == null) && (rule == null))
						{
							// condition 1: recurring without frequency touched, but the starting might change
							rule = editRule;
							comparisonNeeded = true;
						}
						else if ((freq != null) && (!(freq.equals(FREQ_ONCE))))
						{
							// condition 3: recurring with frequency changed, and stays at recurring
							comparisonNeeded = true;
						}
						if (comparisonNeeded) // if under condition 1 or 3
						{
							if (rule != null)
							{
								Time startingTime = TimeService.newTimeLocal(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day),houri,Integer.parseInt(minute),00,000);
								
								Time endingTime = rule.getUntil();
								if ((endingTime != null) && endingTime.before(startingTime))
									earlierEnding = true;
							} // if (editRule != null)
						} // if (comparisonNeeded) // if under condition 1 or 3
					} // if (calEvent.getRecurrenceRule() != null)
				} // if (edit != null)

				if(title.length()==0)
				{
					String errorCode = rb.getString("java.pleasetitle");
					addAlert(sstate, errorCode);
					
					state.setNewData(calId, title,description,Integer.parseInt(month),Integer.parseInt(day),year,houri,Integer.parseInt(minute),Integer.parseInt(dhour),Integer.parseInt(dminute),type,timeType,location, addfieldsMap, intentionStr);
					state.setState("revise");
				}
				/*
				else if(hour.equals("0") && minute.equals("0"))
				{
					String errorCode = "Please enter a time";
					addAlert(sstate, errorCode);
					
					state.setNewData(calId, title,description,Integer.parseInt(month),Integer.parseInt(day),year,houri,Integer.parseInt(minute),Integer.parseInt(dhour),Integer.parseInt(dminute),type,timeType,location, addfieldsMap);
					state.setState("revise");
				}
				 */
				else if( earlierEnding ) // if ending date is earlier than the starting date, show alert
				{
					addAlert(sstate, ALERT_ENDING_EARLIER_THAN_STARTING);
					
					state.setNewData(calId, title,description,Integer.parseInt(month),Integer.parseInt(day),year,houri,Integer.parseInt(minute),Integer.parseInt(dhour),Integer.parseInt(dminute),type,timeType,location, addfieldsMap, intentionStr);
					state.setState("revise");
				}
				else
				{
					try
					{
						calendarObj = CalendarService.getCalendar(calId);
						Time timeObj = TimeService.newTimeLocal(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day),houri,Integer.parseInt(minute),00,000);
						
						long du = (((Integer.parseInt(dhour) * 60)*60)*1000) + ((Integer.parseInt(dminute)*60)*(1000));
						Time endTime = TimeService.newTime(timeObj.getTime() + du);
						boolean includeEndTime = false;
						TimeRange range = null;
						if (du==0)
						{
							range = TimeService.newTimeRange(timeObj);
						}
						else
						{
							range = TimeService.newTimeRange(timeObj, endTime, true, includeEndTime);
						}
						ReferenceVector attachments = state.getAttachments();
						
                                                if (edit != null)
						{
							edit.setRange(range);
							edit.setDescriptionFormatted(description);
							edit.setDisplayName(title);
							edit.setType(type);
							edit.setLocation(location);

							setFields(edit, addfieldsMap);
							edit.replaceAttachments(attachments);

							RecurrenceRule rule = (RecurrenceRule) sstate.getAttribute(CalendarAction.SSTATE__RECURRING_RULE);

							// conditions:
							// 1st, an existing recurring one, just revised, no save state rule or state freq (0, 0)
							// --> let edit rule untouched 
							// 2st, and existing non-recurring one, just revised, no save state rule or state freq (0, 0)
							// --> let edit rule untouched 
 							// 3rd, an existing recurring one, frequency revised, there is a state-saved rule, and state-saved freq exists (1, 1)
							// --> replace the edit rule with state-saved rule
							// 4th, and existing recurring one, changed to non-recurring, the state saved rule is null, but state-saved freq exists (0, 1)
							// --> replace the edit rule with state-saved rule
							// 5th, and existing non-recurring one, changed but kept as non-recurring, the state-saved rule is null, but state-saved freq exists (0, 1)
							// --> replace the edit rule with state-saved rule
							// so if the state-saved freq exists, replace the event rule
							
							String freq = (String) sstate.getAttribute(FREQUENCY_SELECT);
							if (sstate.getAttribute(FREQUENCY_SELECT) != null)
							{
								edit.setRecurrenceRule(rule);
							}

							calendarObj.commitEvent(edit, intention);
							state.setPrimaryCalendarEdit(null);
							state.setEdit(null);
							state.setIsNewCalendar(false);
                                                } // if (edit != null)
						
						m_calObj.setDay(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day));
						
						sstate.setAttribute(STATE_YEAR, new Integer(m_calObj.getYear()));
						sstate.setAttribute(STATE_MONTH, new Integer(m_calObj.getMonthInteger()));
						sstate.setAttribute(STATE_DAY, new Integer(m_calObj.getDayOfMonth()));
						
						// clear the saved recurring rule and the selected frequency
						sstate.setAttribute(CalendarAction.SSTATE__RECURRING_RULE, null);
						sstate.setAttribute(FREQUENCY_SELECT, null);

						// set the return state as the one before new/revise
						String returnState = state.getReturnState();
						if (returnState != null)
						{
							state.setState(returnState);
						}
						else
						{
							state.setState("week");
						}
					}
					catch (IdUnusedException  e)
					{
						addAlert(sstate, rb.getString("java.alert.noexist"));
						Log.debug("chef", this + ".doUpdate(): " + e);
					}
					catch (PermissionException  e)
					{
						addAlert(sstate, rb.getString("java.alert.youcreate"));
						Log.debug("chef", this + ".doUpdate(): " + e);
					} // try-catch
				} // if(title.length()==0)
			} // if (state.getState().equalsIgnoreCase(STATE_CUSTOMIZE_CALENDAR))
		
	}   // doUpdate
	
	
	public void doDeletefield(RunData runData, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, runData, CalendarActionState.class);
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState sstate = ((JetspeedRunData)runData).getPortletSessionState(peid);
		
		customizeCalendarPage.doDeletefield( runData, context, state, getSessionState(runData));
	}
	
	
	/**
	 * Handle the button click to add a field to the list of optional attributes.
	 */
	public void doAddfield(RunData runData, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, runData, CalendarActionState.class);
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState sstate = ((JetspeedRunData)runData).getPortletSessionState(peid);
		
		customizeCalendarPage.doAddfield( runData, context, state, getSessionState(runData));
	}
	
	
	/**
	 * Action doNpagew is requested when the user click on the next arrow to move to the next page in the week view.
	 */
	
	public void doNpagew(RunData runData, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, runData, CalendarActionState.class);
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState sstate = ((JetspeedRunData)runData).getPortletSessionState(peid);
		
		if(state.getCurrentPage().equals("third"))
			state.setCurrentPage("first");
		else if(state.getCurrentPage().equals("second"))
			state.setCurrentPage("third");
		else if(state.getCurrentPage().equals("first"))
			state.setCurrentPage("second");
		state.setState("week");
	}
	
	
	
	/**
	 * Action doPpagew is requested when the user click on the previous arrow to move to the previous page in week view.
	 */
	
	public void doPpagew(RunData runData, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, runData, CalendarActionState.class);
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState sstate = ((JetspeedRunData)runData).getPortletSessionState(peid);
		
		if(state.getCurrentPage().equals("first"))
			state.setCurrentPage("third");
		else if (state.getCurrentPage().equals("third"))
			state.setCurrentPage("second");
		else if (state.getCurrentPage().equals("second"))
			state.setCurrentPage("first");
		state.setState("week");
	}
	
	
	/**
	 * Action doDpagen is requested when the user click on the next arrow to move to the next page in day view.
	 */
	
	public void doDpagen(RunData runData, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, runData, CalendarActionState.class);
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState sstate = ((JetspeedRunData)runData).getPortletSessionState(peid);
		
		if(state.getCurrentPage().equals("third"))
			state.setCurrentPage("first");
		else if(state.getCurrentPage().equals("second"))
			state.setCurrentPage("third");
		else if(state.getCurrentPage().equals("first"))
			state.setCurrentPage("second");
		state.setState("day");
	}
	
	
	
	/**
	 * Action doDpagep is requested when the user click on the upper arrow to move to the previous page in day view.
	 */
	
	public void doDpagep(RunData runData, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, runData, CalendarActionState.class);
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState sstate = ((JetspeedRunData)runData).getPortletSessionState(peid);
		
		if(state.getCurrentPage().equals("first"))
			state.setCurrentPage("third");
		else if (state.getCurrentPage().equals("third"))
			state.setCurrentPage("second");
		else if (state.getCurrentPage().equals("second"))
			state.setCurrentPage("first");
		state.setState("day");
	}   // doDpagep
	
	/**
	 * Action doPrev_activity is requested when the user navigates to the previous message in the detailed view.
	 */
	
	public void doPrev_activity(RunData runData, Context context)
	{
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState sstate = ((JetspeedRunData)runData).getPortletSessionState(peid);
		
		sstate.setAttribute(STATE_NAV_DIRECTION, STATE_PREV_ACT);
	} //doPrev_activity
	
	/**
	 * Action doNext_activity is requested when the user navigates to the previous message in the detailed view.
	 */
	public void doNext_activity(RunData runData, Context context)
	{
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState sstate = ((JetspeedRunData)runData).getPortletSessionState(peid);
		
		sstate.setAttribute(STATE_NAV_DIRECTION, STATE_NEXT_ACT);
	} // doNext_activity
	
	/*
	 * detailNavigatorControl will handle the goNext/goPrev buttons in detailed view,
	 * as well as figure out the prev/next message if available
	 */
	private void navigatorContextControl(VelocityPortlet portlet, Context context, RunData runData, String direction)
	{
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState sstate = ((JetspeedRunData)runData).getPortletSessionState(peid);
		
		CalendarActionState state = (CalendarActionState)getState(context, runData, CalendarActionState.class);
		
		String eventId = state.getCalendarEventId();
		
		List events = prepEventList(portlet, context, runData);
		
		int index = -1;
		int size = events.size();
		for (int i=0; i<size; i++)
		{
			CalendarEvent e = (CalendarEvent) events.get(i);
			if (e.getId().equals(eventId))
				index = i;
		}
		
		CalendarEvent ce = null;
		
		if (direction.equals(STATE_PREV_ACT))
		{
			// navigate to the previous activity
			if (index > 0) 
			{
				 ce = (CalendarEvent) events.get(index-1);
			}
			Reference ref = new Reference(ce.getReference());
			eventId = ref.getId();
			String calId = CalendarService.calendarReference(ref.getContext(), ref.getContainer());
			
			state.setCalendarEventId(calId, eventId);
			state.setAttachments(null);
			index--;
		}
		else if (direction.equals(STATE_NEXT_ACT))
		{
			// navigate to the next activity
			if (index < size-1) 
			{
				ce = (CalendarEvent) events.get(index+1);
			}
			Reference ref = new Reference(ce.getReference());
			eventId = ref.getId();
			String calId = CalendarService.calendarReference(ref.getContext(), ref.getContainer());
			
			state.setCalendarEventId(calId, eventId);
			state.setAttachments(null);
			index++;
		}
		
		if (index > 0)
			sstate.setAttribute(STATE_PREV_ACT, "");
		else
			sstate.removeAttribute(STATE_PREV_ACT);
		
		if(index < size-1)
			sstate.setAttribute(STATE_NEXT_ACT, "");
		else
			sstate.removeAttribute(STATE_NEXT_ACT);
		
		sstate.setAttribute(STATE_NAV_DIRECTION, STATE_CURRENT_ACT);
		
	} // navigatorControl
	
	private CalendarEventVector prepEventList(VelocityPortlet portlet,
			Context context,
			RunData runData)
	{
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState sstate = ((JetspeedRunData)runData).getPortletSessionState(peid);
		
		CalendarActionState state = (CalendarActionState)getState(context, runData, CalendarActionState.class);
		
		List events = new Vector();
		
		TimeRange fullTimeRange =
			TimeService.newTimeRange(
				TimeService.newTimeLocal(
					CalendarFilter.LIST_VIEW_STARTING_YEAR,
					1,
					1,
					0,
					0,
					0,
					0),
				TimeService.newTimeLocal(
					CalendarFilter.LIST_VIEW_ENDING_YEAR,
					12,
					31,
					23,
					59,
					59,
					999));
		
		// We need to get events from all calendars for the full time range.
		CalendarEventVector masterEventVectorObj =
			CalendarService.getEvents(
				getCalendarReferenceList(
					portlet,
					state.getPrimaryCalendarReference(),
					isOnWorkspaceTab()),
				fullTimeRange);
		
		/*
		Vector eventsIdVector = new Vector();
		for (int i=0; i< masterEventVectorObj.size(); i++)
		{
			eventsIdVector.add(((CalendarEvent)masterEventVectorObj.get(i)).getId());
		}
		*/
		sstate.setAttribute(STATE_EVENTS_LIST, masterEventVectorObj);
		return masterEventVectorObj;
		
	} // eventList
	
	
	/**
	 * Action is to parse the function calls
	 **/
	public void doParse(RunData data, Context context)
	{
		ParameterParser params = data.getParameters();
		
		String source = params.getString("source");
		if (source.equalsIgnoreCase("new"))
		{
			// create new event
			doNew(data, context);
		}
		else if (source.equalsIgnoreCase("revise"))
		{
			// revise an event
			doRevise(data, context);
		}
		else if (source.equalsIgnoreCase("delete"))
		{
			// delete event
			doDelete(data, context);
		}
		else if (source.equalsIgnoreCase("byday"))
		{
			// view by day
			doMenueday(data, context);
		}
		else if (source.equalsIgnoreCase("byweek"))
		{
			// view by week
			doWeek(data, context);
		}
		else if (source.equalsIgnoreCase("bymonth"))
		{
			// view by month
			doMonth(data, context);
		}
		else if (source.equalsIgnoreCase("byyear"))
		{
			// view by year
			doYear(data, context);
		}
		else if (source.equalsIgnoreCase("prev"))
		{
			// go previous
			doPrev(data, context);
		}
		else if (source.equalsIgnoreCase("next"))
		{
			// go next
			doNext(data, context);
		}
		else if (source.equalsIgnoreCase("bylist"))
		{
			// view by list
			doList(data, context);
		}
		
		
	}   // doParse
	
	
	/**
	 * Action doList is requested when the user click on the list in the toolbar
	 */
	public void doList(RunData data, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, data, CalendarActionState.class);
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState sstate = ((JetspeedRunData)data).getPortletSessionState(peid);
		
		state.setState("list");
	}   // doList
	
	/**
	 * Action doSort_by_date_toggle is requested when the user click on the sorting icon in the list view
	 */
	public void doSort_by_date_toggle(RunData data, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, data, CalendarActionState.class);
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState sstate = ((JetspeedRunData)data).getPortletSessionState(peid);
		
		boolean dateAsc = sstate.getAttribute(STATE_DATE_SORT_ASC) != null;
		if (dateAsc)
			sstate.removeAttribute(STATE_DATE_SORT_ASC);
		else
			sstate.setAttribute(STATE_DATE_SORT_ASC, "");
		
	}   // doSort_by_date_toggle
	
	/**
	 * Handle a request from the "merge" page to merge calendars from other groups into this group's Schedule display.
	 */
	public void doMerge(RunData runData, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, runData, CalendarActionState.class);
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState sstate = ((JetspeedRunData)runData).getPortletSessionState(peid);
		
		mergedCalendarPage.doMerge( runData, context, state, getSessionState(runData));
	} // doMerge
	
	/**
	 * Handle a request to set options.
	 */
	public void doCustomize(RunData runData, Context context)
	{
		CalendarActionState state =
		(CalendarActionState) getState(context,
		runData,
		CalendarActionState.class);
		
		customizeCalendarPage.doCustomize(
		runData,
		context,
		state,
		getSessionState(runData));
	}
	
	/**
	 * Build the context for showing list view
	 */
	protected void buildListContext(VelocityPortlet portlet, Context context, RunData runData, CalendarActionState state)
	{
		// to get the content Type Image Service
		context.put("contentTypeImageService", ContentTypeImageService.getInstance());
		context.put("tlang",rb);
		MyMonth monthObj2 = null;
		MyDate dateObj1 = new MyDate();
		StringBuffer exceptionMessage = new StringBuffer();
		CalendarEventVector calendarEventVectorObj = null;
		boolean allowed = false;
		LinkedHashMap yearMap = new LinkedHashMap();
		
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState sstate = ((JetspeedRunData)runData).getPortletSessionState(peid);
		
		// Set up list filtering information in the context.
		context.put(TIME_FILTER_OPTION_VAR, state.getCalendarFilter().getListViewFilterMode());

		//
		// Fill in the custom dates
		//
		String sDate; // starting date
		String eDate; // ending date
		
		java.util.Calendar userCal = java.util.Calendar.getInstance();
		context.put("ddStartYear", new Integer(userCal.get(java.util.Calendar.YEAR) - 3));
		context.put("ddEndYear", new Integer(userCal.get(java.util.Calendar.YEAR) + 4));
		
		if (state.getCalendarFilter().isCustomListViewDates() )
		{
			sDate = state.getCalendarFilter().getStartingListViewDateString();
			eDate = state.getCalendarFilter().getEndingListViewDateString();
		}	
		else
		{
			// default to entire current year?
			int userYear = userCal.get(java.util.Calendar.YEAR) - 2000;
			if ((userYear < 10) && (userYear >= 0))
			{ 
				sDate = "01/01/0"+ userYear;
				eDate = "12/31/0"+ userYear;
			}
			else
			{
				sDate = "01/01/"+ userYear;
				eDate = "12/31/"+ userYear;
			}
		}
		
		String sM = sDate.substring(0, 2);
		String eM = eDate.substring(0, 2);
		String sD = sDate.substring(3, 5);
		String eD = eDate.substring(3, 5);
		String sY = "20" + sDate.substring(6);
		String eY = "20" + eDate.substring(6);
		
		context.put(TIME_FILTER_SETTING_CUSTOM_START_YEAR,  Integer.valueOf(sY));
		context.put(TIME_FILTER_SETTING_CUSTOM_END_YEAR,    Integer.valueOf(eY));
		context.put(TIME_FILTER_SETTING_CUSTOM_START_MONTH, Integer.valueOf(sM));
		context.put(TIME_FILTER_SETTING_CUSTOM_END_MONTH,   Integer.valueOf(eM));
		context.put(TIME_FILTER_SETTING_CUSTOM_START_DAY,   Integer.valueOf(sD));
		context.put(TIME_FILTER_SETTING_CUSTOM_END_DAY,     Integer.valueOf(eD));
		
		Time m_time = TimeService.newTime();
		TimeBreakdown b = m_time.breakdownLocal();
		int stateYear = b.getYear();
		int stateMonth = b.getMonth();
		int stateDay = b.getDay();
		if ((sstate.getAttribute(STATE_YEAR) != null) && (sstate.getAttribute(STATE_MONTH) != null) && (sstate.getAttribute(STATE_DAY) != null))
		{
			stateYear = ((Integer)sstate.getAttribute(STATE_YEAR)).intValue();
			stateMonth = ((Integer)sstate.getAttribute(STATE_MONTH)).intValue();
			stateDay = ((Integer)sstate.getAttribute(STATE_DAY)).intValue();
		}
		CalendarUtil calObj= new CalendarUtil();
		calObj.setDay(stateYear, stateMonth, stateDay);
		dateObj1.setTodayDate(calObj.getMonthInteger(),calObj.getDayOfMonth(),calObj.getYear());
		
		// fill this month object with all days avilable for this month
		if (CalendarService.allowGetCalendar(state.getPrimaryCalendarReference())== false)
		{
			allowed = false;
			exceptionMessage.append(rb.getString("java.alert.younotallow"));
			calendarEventVectorObj = new  CalendarEventVector();
		}
		else
		{
			try
			{
				allowed = CalendarService.getCalendar(state.getPrimaryCalendarReference()).allowAddEvent();
			}
			catch(IdUnusedException e)
			{
				Log.debug("chef", this + ".buildMonthContext(): " + e);
			}
			catch (PermissionException e)
			{
				Log.debug("chef", this + ".buildMonthContext(): " + e);
			}
		}
		
		int yearInt, monthInt, dayInt=1;
		
		TimeRange fullTimeRange =
			TimeService.newTimeRange(
				TimeService.newTimeLocal(
					CalendarFilter.LIST_VIEW_STARTING_YEAR,
					1,
					1,
					0,
					0,
					0,
					0),
				TimeService.newTimeLocal(
					CalendarFilter.LIST_VIEW_ENDING_YEAR,
					12,
					31,
					23,
					59,
					59,
					999));
		
		// We need to get events from all calendars for the full time range.
		CalendarEventVector masterEventVectorObj =
			CalendarService.getEvents(
				getCalendarReferenceList(
					portlet,
					state.getPrimaryCalendarReference(),
					isOnWorkspaceTab()),
				fullTimeRange);
		
		boolean dateAsc = sstate.getAttribute(STATE_DATE_SORT_ASC) != null;
		context.put("currentDateSortAsc", new Boolean(dateAsc));
		
		if (dateAsc)
		{
			for (yearInt = CalendarFilter.LIST_VIEW_STARTING_YEAR;
				yearInt <= CalendarFilter.LIST_VIEW_ENDING_YEAR;
				yearInt++)
			{
				Vector arrayOfMonths = new Vector(20);
				for(monthInt = 1; monthInt <13; monthInt++)
				{
					CalendarUtil AcalObj = new CalendarUtil();
					
					monthObj2 = new MyMonth();
					AcalObj.setDay(yearInt, monthInt, dayInt);
					
					dateObj1.setTodayDate(AcalObj.getMonthInteger(),AcalObj.getDayOfMonth(),AcalObj.getYear());
					
					// Get the events for the particular month from the
					// master list of events.
					calendarEventVectorObj =
						new CalendarEventVector(
							state.getCalendarFilter().filterEvents(
								masterEventVectorObj.getEvents(
									getMonthTimeRange((CalendarUtil) AcalObj))));
					
					if (!calendarEventVectorObj.isEmpty())
					{
						AcalObj.setDay(dateObj1.getYear(),dateObj1.getMonth(),dateObj1.getDay());
						
						monthObj2 = calMonth(monthInt, (CalendarUtil)AcalObj, state, calendarEventVectorObj);
						
						AcalObj.setDay(dateObj1.getYear(),dateObj1.getMonth(),dateObj1.getDay());
						
						if (!calendarEventVectorObj.isEmpty())
							arrayOfMonths.addElement(monthObj2);
					}
				}
				if (!arrayOfMonths.isEmpty())
					yearMap.put(new Integer(yearInt), arrayOfMonths.iterator());
			}
		}
		else
		{
			for (yearInt = CalendarFilter.LIST_VIEW_ENDING_YEAR;
			yearInt >= CalendarFilter.LIST_VIEW_STARTING_YEAR;
			yearInt--)
			{
				Vector arrayOfMonths = new Vector(20);
				for(monthInt = 12; monthInt >=1; monthInt--)
				{
					CalendarUtil AcalObj = new CalendarUtil();
					
					monthObj2 = new MyMonth();
					AcalObj.setDay(yearInt, monthInt, dayInt);
					
					dateObj1.setTodayDate(AcalObj.getMonthInteger(),AcalObj.getDayOfMonth(),AcalObj.getYear());
					
					// Get the events for the particular month from the
					// master list of events.
					calendarEventVectorObj =
						new CalendarEventVector(
							state.getCalendarFilter().filterEvents(
								masterEventVectorObj.getEvents(
									getMonthTimeRange((CalendarUtil) AcalObj))));
					
					if (!calendarEventVectorObj.isEmpty())
					{
						AcalObj.setDay(dateObj1.getYear(),dateObj1.getMonth(),dateObj1.getDay());
						
						monthObj2 = calMonth(monthInt, (CalendarUtil)AcalObj, state, calendarEventVectorObj);
						
						AcalObj.setDay(dateObj1.getYear(),dateObj1.getMonth(),dateObj1.getDay());
						
						if (!calendarEventVectorObj.isEmpty())
							arrayOfMonths.addElement(monthObj2);
					}
				}
				if (!arrayOfMonths.isEmpty())
					yearMap.put(new Integer(yearInt), arrayOfMonths.iterator());
			}
		}
		
		context.put("yearMap", yearMap);
		
		int row = 5;
		context.put("row",new Integer(row));
		calObj.setDay(stateYear, stateMonth, stateDay);
		
		// using session state stored year-month-day to replace saving calObj
		sstate.setAttribute(STATE_YEAR, new Integer(stateYear));
		sstate.setAttribute(STATE_MONTH, new Integer(stateMonth));
		sstate.setAttribute(STATE_DAY, new Integer(stateDay));
		
		state.setState("list");
		context.put("date",dateObj1);
		
		// output CalendarService and SiteService
		context.put("CalendarService", CalendarService.getInstance());
		context.put("SiteService", SiteService.getInstance());
		context.put("PortalService", PortalService.getInstance());
		
		buildMenu(
			portlet,
			context,
			runData,
			state,
			CalendarPermissions.allowCreateEvents(
				state.getPrimaryCalendarReference(),
				state.getSelectedCalendarReference()),
			CalendarPermissions.allowDeleteEvent(
				state.getPrimaryCalendarReference(),
				state.getSelectedCalendarReference(),
				state.getCalendarEventId()),
			CalendarPermissions.allowReviseEvents(
				state.getPrimaryCalendarReference(),
				state.getSelectedCalendarReference(),
				state.getCalendarEventId()),
			CalendarPermissions.allowMergeCalendars(
				state.getPrimaryCalendarReference(),
				isOnWorkspaceTab()),
			CalendarPermissions.allowModifyCalendarProperties(
				state.getPrimaryCalendarReference()),
			CalendarPermissions.allowImport(
				state.getPrimaryCalendarReference()));
		
		// added by zqian for toolbar
		context.put("allow_new", Boolean.valueOf(allowed));
		context.put("allow_delete", Boolean.valueOf(false));
		context.put("allow_revise", Boolean.valueOf(false));
		context.put("realDate", TimeService.newTime());
		
		context.put("selectedView", VIEW_LIST);
		context.put("tlang",rb);

	}   // buildListContext
	
	/**
	 * Build the menu.
	 */
	private void buildMenu( VelocityPortlet portlet,
	Context context,
	RunData runData,
	CalendarActionState state,
	boolean allow_new,
	boolean allow_delete,
	boolean allow_revise,
	boolean allow_merge_calendars,
	boolean allow_modify_calendar_properties,
	boolean allow_import)
	{
		Menu bar = new Menu(portlet, runData, "CalendarAction");
		
		String status = state.getState();
		
		
		if ((status.equals("day"))
		||(status.equals("week"))
		||(status.equals("month"))
		||(status.equals("year"))
		||(status.equals("list")))
		{
			allow_revise = false;
			allow_delete = false;
		}
		
		bar.add( new MenuEntry(rb.getString("java.new"), null, allow_new, MenuItem.CHECKED_NA, "doNew") );
		
		//
		// Don't allow the user to customize the "My Workspace" tab.
		//
		if ( !isOnWorkspaceTab() )
		{
			bar.add( new MenuEntry(mergedCalendarPage.getButtonText(), null, allow_merge_calendars, MenuItem.CHECKED_NA, mergedCalendarPage.getButtonHandlerID()) );
		}

		// See if we are allowed to import items.
		if ( allow_import )
		{
			bar.add( new MenuEntry(rb.getString("java.import"), null, allow_new, MenuItem.CHECKED_NA, "doImport") );
		}
		
		//bar.add( new MenuEntry(rb.getString("java.delete"), null, allow_delete, MenuItem.CHECKED_NA, "doDelete") );
		//bar.add( new MenuEntry(rb.getString("java.revise"), null, allow_revise, MenuItem.CHECKED_NA, "doRevise") );
		//bar.add( new MenuEntry("View by day", null, viewDay, MenuItem.CHECKED_NA, "doMenueday") );
		//bar.add( new MenuEntry("View by week", null, viewWeek, MenuItem.CHECKED_TRUE, "doWeek") );
		//bar.add( new MenuEntry("View by month", null, viewMonth, MenuItem.CHECKED_NA, "doMonth") );
		//bar.add( new MenuEntry("View by year", null, viewYear, MenuItem.CHECKED_NA, "doYear") );
		//bar.add( new MenuEntry("View list", null, viewList, MenuItem.CHECKED_NA, "doList") );
		//bar.add( new MenuEntry("Go home", null, true/* visible at all views */, MenuItem.CHECKED_NA, "doHome") );
		
		// 2nd menu bar for the PDF print only
		Menu bar_PDF = new Menu(portlet, runData, "CalendarAction");
		
		String stateName = state.getState();
		
		if (stateName.equals("month")
		|| stateName.equals("day")
		|| stateName.equals("week")
		|| stateName.equals("list"))
		{
			int printType = PrintFileGenerator.UNKNOWN_VIEW;
			String timeRangeString = "";
			
			TimeRange dailyStartTime = null;
			int startHour = 0, startMinute = 0;
			int endHour = 0, endMinute = 0;
			int endSeconds = 0, endMSeconds = 0;
			
			//
			// Depending what page we are on, there will be
			// a different time of the day on which we start.
			//
			if (state.getCurrentPage().equals("first"))
			{
				startHour = FIRST_PAGE_START_HOUR;
				endHour = startHour + NUMBER_HOURS_PER_PAGE_FOR_WEEK_VIEW;
			}
			else
				if (state.getCurrentPage().equals("second"))
				{
					startHour = SECOND_PAGE_START_HOUR;
					endHour = startHour + NUMBER_HOURS_PER_PAGE_FOR_WEEK_VIEW;
				}
				else
					if (state.getCurrentPage().equals("third"))
					{
						startHour = THIRD_PAGE_START_HOUR;
						endHour = startHour + NUMBER_HOURS_PER_PAGE_FOR_WEEK_VIEW;
					}
					else
					{
						startHour = 0;
						endHour = startHour + HOURS_PER_DAY;
					}
			
			// If we go over twenty-four hours, stop at the end of the day.
			if ( endHour >= HOURS_PER_DAY )
			{
				endHour = 23;
				endMinute = 59;
				endSeconds = 59;
				endMSeconds = 999;
			}
			
			dailyStartTime =
			TimeService.newTimeRange(
			TimeService.newTimeLocal(
			state.getcurrentYear(),
			state.getcurrentMonth(),
			state.getcurrentDay(),
			startHour,
			startMinute,
			00,
			000),
			TimeService.newTimeLocal(
			state.getcurrentYear(),
			state.getcurrentMonth(),
			state.getcurrentDay(),
			endHour,
			endMinute,
			endSeconds,
			endMSeconds));
			
			String peid = ((JetspeedRunData)runData).getJs_peid();
			SessionState sstate = ((JetspeedRunData)runData).getPortletSessionState(peid);
			
			Time m_time = TimeService.newTime();
			TimeBreakdown b = m_time.breakdownLocal();
			int stateYear = b.getYear();
			int stateMonth = b.getMonth();
			int stateDay = b.getDay();
			if ((sstate.getAttribute(STATE_YEAR) != null) && (sstate.getAttribute(STATE_MONTH) != null) && (sstate.getAttribute(STATE_DAY) != null))
			{
				stateYear = ((Integer)sstate.getAttribute(STATE_YEAR)).intValue();
				stateMonth = ((Integer)sstate.getAttribute(STATE_MONTH)).intValue();
				stateDay = ((Integer)sstate.getAttribute(STATE_DAY)).intValue();
			}
			
			CalendarUtil calObj = new CalendarUtil();
			calObj.setDay(stateYear, stateMonth, stateDay);
			
			if (stateName.equals("month"))
			{
				printType = PrintFileGenerator.MONTH_VIEW;
				timeRangeString = getMonthTimeRange(calObj).toString();
			}
			else
				if (stateName.equals("day"))
				{
					printType = PrintFileGenerator.DAY_VIEW;
					
					timeRangeString =
					getDayTimeRange(
					calObj.getYear(),
					calObj.getMonthInteger(),
					calObj.getDayOfMonth())
					.toString();
				}
				else
					if (stateName.equals("week"))
					{
						printType = PrintFileGenerator.WEEK_VIEW;
						timeRangeString = getWeekTimeRange(calObj).toString();
					}
					else
						if (stateName.equals("list"))
						{
							printType = PrintFileGenerator.LIST_VIEW;
							
							timeRangeString =
								TimeService
									.newTimeRange(
									state.getCalendarFilter().getListViewStartingTime(),
									state.getCalendarFilter().getListViewEndingTime())
									.toString();
						}
			
			String accessPointUrl =
			(ServerConfigurationService.getAccessUrl()).concat(
			PrintFileGenerator.submissionsPrintingReference(
			printType,
			getCalendarReferenceList(
			portlet,
			state.getPrimaryCalendarReference(),
			isOnWorkspaceTab()),
			timeRangeString,
			UserDirectoryService.getCurrentUser().getDisplayName(),
			dailyStartTime));
			
			bar_PDF.add(new MenuEntry(rb.getString("java.print"), "").setUrl(accessPointUrl));
		}
		
		bar.add( new MenuEntry(customizeCalendarPage.getButtonText(), null, allow_modify_calendar_properties, MenuItem.CHECKED_NA, customizeCalendarPage.getButtonHandlerID()) );
		
		// add permissions, if allowed
		if (SiteService.allowUpdateSite(PortalService.getCurrentSiteId()))
		{
			bar.add( new MenuEntry(rb.getString("java.permissions"), "doPermissions") );
		}
		
		// Set menu state attribute
		SessionState stateForMenus = ((JetspeedRunData)runData).getPortletSessionState(portlet.getID());
		stateForMenus.setAttribute(MenuItem.STATE_MENU, bar);
		context.put("tlang",rb);
		context.put(Menu.CONTEXT_MENU, bar);
		context.put("menu_PDF", bar_PDF);
		context.put(Menu.CONTEXT_ACTION, "CalendarAction");
		
	}   // buildMenu
	
	/**
	 * Align the edit's fields with these values.
	 * @param edit The CalendarEventEdit.
	 * @param values The map of name-value pairs.
	 */
	private void setFields(CalendarEventEdit edit, Map values)
	{
		Set keys = values.keySet();
		for (Iterator it = keys.iterator(); it.hasNext(); )
		{
			String name = (String) it.next();
			String value = (String) values.get(name);
			edit.setField(name, value);
		}
		
	}   // setFields
	
	/**
	 * Fire up the permissions editor
	 */
	public void doPermissions(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());
		CalendarActionState cstate = (CalendarActionState)getState(context, data, CalendarActionState.class);
		
		String calendarRefStr = cstate.getPrimaryCalendarReference();
		Reference calendarRef = new Reference(calendarRefStr);
		String siteRef = SiteService.siteReference(calendarRef.getContext());
		
		// setup for editing the permissions of the site for this tool, using the roles of this site, too
		state.setAttribute(PermissionsAction.STATE_REALM_ID, siteRef);
		state.setAttribute(PermissionsAction.STATE_REALM_ROLES_ID, siteRef);
		
		// ... with this description
		state.setAttribute(PermissionsAction.STATE_DESCRIPTION, rb.getString("java.set")
		+ SiteService.getSiteDisplay(calendarRef.getContext()));
		
		// ... showing only locks that are prpefixed with this
		state.setAttribute(PermissionsAction.STATE_PREFIX, rb.getString("java.calendar"));
		
		// start the helper
		state.setAttribute(PermissionsAction.STATE_MODE, PermissionsAction.MODE_MAIN);
		
		// schedule a main refresh
/*		String toolId = PortalService.getCurrentToolId();
		String address = clientWindowId(state, toolId);
		String mainPanelId = mainPanelUpdateId(toolId);
		CourierService.deliver(address, mainPanelId);
 */
	} // doPermissions
	
	
	/**
	 * Action doFrequency is requested when "set Frequency" button is clicked in new/revise page
	 */
	
	public void doEditfrequency(RunData rundata, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, rundata, CalendarActionState.class);
		
		String peid = ((JetspeedRunData)rundata).getJs_peid();
		SessionState sstate = ((JetspeedRunData)rundata).getPortletSessionState(peid);
		
		String calId = "";
		Calendar calendarObj = null;
		
		String eventId = state.getCalendarEventId();
		
		try
		{
			calId = state.getPrimaryCalendarReference();
			calendarObj = CalendarService.getCalendar(calId);
			
			String freq = (String) sstate.getAttribute(FREQUENCY_SELECT);
			
			// conditions when the doEditfrequency is called:
			// 1. new/existing event, in create-new/revise page first time: freq is null.
			//    It has been set to null in both doNew & doRevise.
			//    Make sure to re-set the freq in this step.
			// 2. new/existing event, back from cancel/save-frequency-setting page: freq is sth, because when
			// the first time doEditfrequency is called, there is a freq set up already
			
			// condition 1 -
			if ((freq == null)||(freq.equals("")))
			{
				// if a new event
				if ((eventId == null)||(eventId.equals("")))
				{
					// set the frequency to be default "once", rule to be null
					sstate.setAttribute(FREQUENCY_SELECT, DEFAULT_FREQ);
					sstate.setAttribute(CalendarAction.SSTATE__RECURRING_RULE, null);
				}
				else
				{ // exiting event
					try
					{
						if(calendarObj.allowGetEvents())
						{
							CalendarEvent event = calendarObj.getEvent(eventId);
							RecurrenceRule rule = event.getRecurrenceRule();
							if (rule == null)
							{
								// not recurring, i.e., frequency is once
								sstate.setAttribute(FREQUENCY_SELECT, DEFAULT_FREQ);
								sstate.setAttribute(CalendarAction.SSTATE__RECURRING_RULE, null);
							}
							else
							{
								sstate.setAttribute(CalendarAction.SSTATE__RECURRING_RULE, rule);
								sstate.setAttribute(FREQUENCY_SELECT, rule.getFrequencyDescription());
							} // if (rule==null)
						} // if allowGetEvents
					} // try
					catch(IdUnusedException e)
					{
						Log.debug("chef", this + ".doEditfrequency() + calendarObj.getEvent(): " + e);
					} // try-cath
				} // if ((eventId == null)||(eventId.equals(""))
			}
			else
			{
				// condition 2, state freq is set, and state rule is set already
			}
		} // try
		catch(IdUnusedException e)
		{
			Log.debug("chef", this + ".doEditfrequency() + CalendarService.getCalendar(): " + e);
		}
		catch (PermissionException e)
		{
			Log.debug("chef", this + ".doEditfrequency() + CalendarService.getCalendar(): " + e);
		}
		
		
		int houri;
		
		String hour = "";
		hour = rundata.getParameters().getString("startHour");
		String title ="";
		title = rundata.getParameters().getString("activitytitle");
		String minute = "";
		minute = rundata.getParameters().getString("startMinute");
		String dhour = "";
		dhour = rundata.getParameters().getString("duHour");
		String dminute = "";
		dminute = rundata.getParameters().getString("duMinute");
		String description = "";
		description = rundata.getParameters().getString("description");
		description = processFormattedTextFromBrowser(sstate, description);
		String month = "";
		month = rundata.getParameters().getString("month");
		
		String day = "";
		day = rundata.getParameters().getString("day");
		String year = "";
		year = rundata.getParameters().getString("yearSelect");
		String timeType = "";
		timeType = rundata.getParameters().getString("startAmpm");
		String type = "";
		type = rundata.getParameters().getString("eventType");
		String location = "";
		location = rundata.getParameters().getString("location");

		// read the recurrence modification intention
		String intentionStr = rundata.getParameters().getString("intention");
		if (intentionStr == null) intentionStr = "";
		
		StringBuffer exceptionMessage = new StringBuffer();
		
		try
		{
			calendarObj = CalendarService.getCalendar(calId);
			Map addfieldsMap = new HashMap();
			
			// Add any additional fields in the calendar.
			customizeCalendarPage.loadAdditionalFieldsMapFromRunData(rundata, addfieldsMap, calendarObj);
			
			if (timeType.equals("pm"))
			{
				if (Integer.parseInt(hour)>11)
					houri = Integer.parseInt(hour);
				else
					houri = Integer.parseInt(hour)+12;
			}
			else if (timeType.equals("am") && Integer.parseInt(hour)==12)
			{
				houri = 24;
			}
			else
			{
				houri = Integer.parseInt(hour);
			}
			state.clearData();
			state.setNewData(state.getPrimaryCalendarReference(), title,description,Integer.parseInt(month),Integer.parseInt(day),year,houri,Integer.parseInt(minute),Integer.parseInt(dhour),Integer.parseInt(dminute),type,timeType,location, addfieldsMap, intentionStr);
		}
		catch(IdUnusedException e)
		{
			exceptionMessage.append(rb.getString("java.alert.thereis"));
			Log.debug("chef", this + ".doEditfrequency(): " + e);
		}
		catch (PermissionException e)
		{
			exceptionMessage.append(rb.getString("java.alert.youdont"));
			Log.debug("chef", this + ".doEditfrequency(): " + e);
		}
		
		sstate.setAttribute(STATE_BEFORE_SET_RECURRENCE, state.getState());
		state.setState(STATE_SET_FREQUENCY);
		
	}   // doEditfrequency
	
	/**
	 * Action doChangefrequency is requested when the user changes the selected frequency at the frequency setting page
	 */
	
	public void doChangefrequency(RunData rundata, Context context)
	{
		CalendarActionState state = (CalendarActionState)getState(context, rundata, CalendarActionState.class);
		
		String freqSelect = rundata.getParameters().getString(FREQUENCY_SELECT);
		
		String peid = ((JetspeedRunData)rundata).getJs_peid();
		SessionState sstate = ((JetspeedRunData)rundata).getPortletSessionState(peid);
		
		sstate.setAttribute(FREQUENCY_SELECT, freqSelect);		
		state.setState(STATE_SET_FREQUENCY);
		
	}   // doChangefrequency
	
	/**
	 * Action doSavefrequency is requested when the user click on the "Save" button in the frequency setting page
	 */
	
	public void doSavefrequency(RunData rundata, Context context)
	{		
		CalendarActionState state = (CalendarActionState)getState(context, rundata, CalendarActionState.class);
		
		String peid = ((JetspeedRunData)rundata).getJs_peid();
		SessionState sstate = ((JetspeedRunData)rundata).getPortletSessionState(peid);
		
		String returnState = (String)sstate.getAttribute(STATE_BEFORE_SET_RECURRENCE);
		
		// if by any chance, the returnState is not available, 
		// then reset it as either "new" or "revise". 
		// For new event, the id is null or empty string
		if ((returnState == null)||(returnState.equals("")))
		{
			String eventId = state.getCalendarEventId();
			if ((eventId == null) || (eventId.equals("")))
				returnState = "new";
			else
				returnState = "revise";
		}
		state.setState(returnState);
		
		// get the current frequency setting the user has selected - daily, weekly, or etc.
		String freq = (String) rundata.getParameters().getString(FREQUENCY_SELECT);
			
		if ((freq == null)||(freq.equals(FREQ_ONCE)))
		{
			sstate.setAttribute(CalendarAction.SSTATE__RECURRING_RULE, null);
			sstate.setAttribute(FREQUENCY_SELECT, FREQ_ONCE);
		}
		else
		{
			sstate.setAttribute(FREQUENCY_SELECT, freq);
			
			String interval = rundata.getParameters().getString("interval");
			int intInterval = Integer.parseInt(interval);
			
			RecurrenceRule rule = null;

			String CountOrTill = rundata.getParameters().getString("CountOrTill");
			if (CountOrTill.equals("Never"))
			{
				rule = CalendarService.newRecurrence(freq, intInterval);
			}
			else if (CountOrTill.equals("Till"))
			{
				String endMonth = rundata.getParameters().getString("endMonth");
				String endDay = rundata.getParameters().getString("endDay");
				String endYear = rundata.getParameters().getString("endYear");
				int intEndMonth = Integer.parseInt(endMonth);
				int intEndDay = Integer.parseInt(endDay);
				int intEndYear = Integer.parseInt(endYear);
			 	
				//construct time object from individual ints, GMT values
				Time endTime = TimeService.newTimeGmt(intEndYear, intEndMonth, intEndDay, 23, 59, 59, 999);
				rule = CalendarService.newRecurrence(freq, intInterval, endTime);
			}
			else if (CountOrTill.equals("Count"))
			{
				String count = rundata.getParameters().getString("count");
				int intCount = Integer.parseInt(count);
				rule = CalendarService.newRecurrence(freq, intInterval, intCount);
			}
			sstate.setAttribute(CalendarAction.SSTATE__RECURRING_RULE, rule);
		} // if (freq.equals(FREQ_ONCE))
		
	}   // doSavefrequency

// TODO:???	
//	/**
//	 * This class overrides the alert message of Merge,
//	 * when the site is already being edited
//	 */
//	public class VelocityPortletPaneledAlert
//	extends VelocityPortletPaneledAction
//	{
//		/**
//		 * Handle a request to set options.
//		 */
//		public void doOptions(RunData runData, Context context)
//		{
//			String toolId = PortalService.getCurrentToolId();
//			String siteId = PortalService.getCurrentSiteId();
//			SessionState state =
//			((JetspeedRunData)runData).getPortletSessionState(toolId);
//			
//			String msg = null;
//			
//			try
//			{
//				// get a lock on the site
//				SiteEdit site = SiteService.editSite(siteId);
//				
//				// get this tool's configuration
//				ToolConfigurationEdit tool = site.getToolEdit(toolId);
//				
//				// put in state
//				state.setAttribute(STATE_SITE_EDIT, site);
//				state.setAttribute(STATE_TOOL_EDIT, tool);
//				
//				// go into options mode
//				state.setAttribute(STATE_MODE, MODE_OPTIONS);
//				
//				// disable auto-updates while editing
//				disableObservers(state);
//				
//				// if we're not in the main panel for this tool, schedule an update of the main panel
//				String currentPanelId =
//				runData.getParameters().getString(REQ_PANEL);
//				if (!LAYOUT_MAIN.equals(currentPanelId))
//				{
//					String address = clientWindowId(state, toolId);
//					String mainPanelId = mainPanelUpdateId(toolId);
//					CourierService.deliver(address, mainPanelId);
//				}
//				return;
//			} catch (IdUnusedException ignore)
//			{
//				msg = "due to a system error.";
//			} catch (PermissionException e)
//			{
//				msg = "you do not have permission to set this option for this Worksite.";
//			} catch (InUseException e)
//			{
//				msg = "you are already configuring this Worksite from another tool, or someone else is configuring this Worksite.";
//			}
//			
//			// we didn't get a lock on the site, so tell the user and don't change mode
//			state.setAttribute(STATE_MESSAGE, "Merge is unavailable right now: " + msg);
//			
//		} // doOptions
//	} // VelocityPortletPaneledAlert
	
	/**
	 * Populate the state object, if needed.
	 */
	protected void initState(SessionState state, VelocityPortlet portlet, JetspeedRunData rundata)
	{
		super.initState(state, portlet, rundata);

		// retrieve the state from state object
		CalendarActionState calState = (CalendarActionState)getState( portlet, rundata, CalendarActionState.class );

		setPrimaryCalendarReferenceInState(portlet, calState);

		//String channel = StringUtil.trimToNull(config.getInitParameter(PARAM_CHANNEL));
		// setup the observer to notify our main panel
		if (state.getAttribute(STATE_INITED) == null)
		{
			state.setAttribute(STATE_INITED,STATE_INITED);
//			// the delivery location for this tool
//			String deliveryId = clientWindowId(state, portlet.getID());
//
//			// the html element to update on delivery
//			String elementId = mainPanelUpdateId(portlet.getID());
//
//			// the event resource reference pattern to watch for
//			EventsObservingCourier observer =
//				new EventsObservingCourier(
//					deliveryId,
//					elementId,
//					calState.getPrimaryCalendarReference());
//			
//			state.setAttribute(STATE_OBSERVER, observer);
			
			MergedList mergedCalendarList = new MergedList();

            String[] channelArray = null;

			// Figure out the list of channel references that we'll be using.
			// If we're on the workspace tab, we get everything.
            // Don't do this if we're the super-user, since we'd be
            // overwhelmed.
            if ( isOnWorkspaceTab()  && !SecurityService.isSuperUser() )
            {
			    channelArray = mergedCalendarList
                        .getAllPermittedChannels(new CalendarChannelReferenceMaker());
            }
            else
            {
                // Get the list of merged calendar sources.
    			// TODO - MERGE FIX
                channelArray = mergedCalendarList
                        .getChannelReferenceArrayFromDelimitedString(calState
                                .getPrimaryCalendarReference(), portlet
                                .getPortletConfig().getInitParameter(
                                        PORTLET_CONFIG_PARM_MERGED_CALENDARS));
            }


            mergedCalendarList.loadChannelsFromDelimitedString(
                    isOnWorkspaceTab(),
                    new MergedListEntryProviderFixedListWrapper(
                            new EntryProvider(), calState
                                    .getPrimaryCalendarReference(),
                            channelArray,
                            new CalendarReferenceToChannelConverter()),
					StringUtil.trimToZero(UsageSessionService.getSessionUserId()), channelArray,
                    SecurityService.isSuperUser(), PortalService
                            .getCurrentSiteId());

			// make sure the observer is in sync with state
			updateObservationOfChannel(mergedCalendarList, rundata, state, calState);
		}
		
	} // initState

//	/**
//	 * Adds the merged sites to the list of events that we're interested
//	 * in watching.
//	 */
//	private void addMergedCalendarsToObserver(MergedList mergedCalendarList, CalendarActionState calendarActionState, EventsObservingCourier observer)
//	{
//		Iterator it = mergedCalendarList.iterator();
//		
//		while (it.hasNext())
//		{
//			MergedList.MergedEntry entry = (MergedList.MergedEntry) it.next();
//
//			if ( entry.isMerged() )
//			{
//				Reference ref = new Reference(entry.getReference());
//			
//				String pattern =
//				CalendarService.eventReference(ref.getContext(), ref.getId(), "");
//				
//				observer.addResourcePattern(pattern);				
//			}
//		}
//	}

	/**
	 * Setup our observer to be watching for change events for our channel.
	 */
	private void updateObservationOfChannel(MergedList mergedCalendarList, RunData runData, SessionState state, CalendarActionState calState)
	{
//		String peid = ((JetspeedRunData) runData).getJs_peid();
//		
//		EventsObservingCourier observer =
//		(EventsObservingCourier) state.getAttribute(STATE_OBSERVER);
//
//		addMergedCalendarsToObserver(mergedCalendarList, calState, observer);
//
//		// the delivery location for this tool
//		String deliveryId = clientWindowId(state, peid);
//		observer.setDeliveryId(deliveryId);

	} // updateObservationOfChannel

	/**
	  *  Takes an array of tokens and converts into separator-separated string.
	  *
	  * @param String[] The array of strings input.
	  * @param String The string separator.
	  * @return String A string containing tokens separated by seperator.
	  */
	 protected String arrayToString(String[] array, String separators)
	 {
		 StringBuffer sb = new StringBuffer("");
		 String empty = "";
        
		 if (array == null)
			 return empty;

		 if (separators == null)
			 separators = ",";

		 for (int ix=0; ix < array.length; ix++) 
		 {
			 if (array[ix] != null && !array[ix].equals("")) 
			 {
				 sb.append(array[ix] + separators);
			 }
		 }
		 String str = sb.toString();
		 if (!str.equals("")) 
		 {
			 str = str.substring(0, (str.length() - separators.length()));
		 }
		 return str;
	 }
	 
	/**
	 * Processes formatted text that is coming back from the browser 
	 * (from the formatted text editing widget).
	 * @param state Used to pass in any user-visible alerts or errors when processing the text
	 * @param strFromBrowser The string from the browser
	 * @return The formatted text
	 */
	private String processFormattedTextFromBrowser(SessionState state, String strFromBrowser)
	{
		StringBuffer alertMsg = new StringBuffer();
		try
		{
			String text = FormattedText.processFormattedText(strFromBrowser, alertMsg);
			if (alertMsg.length() > 0) addAlert(state, alertMsg.toString());
			return text;
		}
		catch (Exception e)
		{
			Log.warn("chef", this + ": ", e);
			return strFromBrowser;
		}
	}

}   // CalendarAction

/**********************************************************************************
 *
 * $Header: /cvs/sakai2/legacy/tools/src/java/org/sakaiproject/tool/calendar/CalendarAction.java,v 1.12 2005/06/14 20:08:47 suiyy.umich.edu Exp $
 *
 **********************************************************************************/
