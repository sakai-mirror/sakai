/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/calendar/CalendarActionState.java,v 1.6 2004/06/22 03:04:57 ggolden Exp $
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
package org.sakaiproject.tool.calendar;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sakaiproject.cheftool.ControllerState;
import org.sakaiproject.service.legacy.calendar.CalendarEventEdit;
import org.sakaiproject.service.legacy.resource.ReferenceVector;


/**
 * Maintains user interface state for the MyCalendar action class.
 */
public class CalendarActionState
	extends ControllerState
{
	private List wizardImportedEvents;

	private String importWizardType;

	private String importWizardState;

	private CalendarFilter calendarFilter = new CalendarFilter();
	
	private int m_currentday;
	private String eventId = "";
	private String selectedCalendarReference = "";
	private int m_currentyear;
	private int m_currentmonth;
	
	private String m_primaryCollectionId = null;
	private boolean m_isNewCal = true;

	// this attachment list is never set to null!
	private ReferenceVector m_attachments = new ReferenceVector();
    private int m_newday;
    
	private String m_nextDate = "";
	private String m_prevDate = "";
	private CalendarUtil m_scalObj;
	private String m_primaryCalendarReference = null;
	private String PrevState = "";

	private String m_AttachmentFlag = "false";
	private LocalEvent savedData = new LocalEvent();
	private boolean m_IsPastAlertOff = true;

	private boolean m_DelfieldAlertOff = true;

	private String m_state = "";
	private String currentpage = "second";

	private String m_returnState;
	private CalendarEventEdit m_primaryCalendarEdit;

	private String m_addfields = "";

	public String getAddfields()
	{
		return m_addfields;
	}


	/**
	 * @return
	 */
	public String getState()
	{
		return m_state;
	}
	/**
	 * @param state
	 */
	public void setState(String state)
	{
		m_state = state;
	}

	/**
	 * @return
	 */
	public String getReturnState()
	{
		return m_returnState;
	}
	/**
	 * @param returnState
	 */
	public void setReturnState(String returnState)
	{
		m_returnState = returnState;
	}


	/**
	 * Get edit The edit object
	 * @return
	 */
	public void setPrimaryCalendarEdit(CalendarEventEdit edit)
	{
		m_primaryCalendarEdit = edit;
	}

	/**
	 * Get edit object
	 * @return m_edit The edit object
	 */
	public CalendarEventEdit getPrimaryCalendarEdit()
	{
		return m_primaryCalendarEdit;
	}

	/**
	 * @param page
	 */
	public void setCurrentPage(String page)
	{
		currentpage = page;
	}


	/**
	 * @return
	 */
	public String getCurrentPage()
	{
		return currentpage;
	}


	/**
	 * @return
	 */
	public String getfromAttachmentFlag()
	{
		return m_AttachmentFlag;
	}


	/**
	 * @param flag
	 */
	public void setfromAttachmentFlag(String flag)
	{
		m_AttachmentFlag = flag;
	}


	/**
	 * Get
	 * @return
	 */
	public ReferenceVector getAttachments()
	{

		return m_attachments;

	}	//	getAttachment

	/**
	* Set
	* @param m_attachments:
	*/
	public void setAttachments(ReferenceVector attachments)
	{
		if (attachments != null)
		{
			m_attachments = (ReferenceVector) attachments.clone();
		}
		else
		{
			m_attachments.clear();
		}
	}	//	setAttachments


	/**
	 * Get the collectionId of current state
	 * @return The current collectionId
	 */
	public String getPrimaryCollectionId()
	{
		return m_primaryCollectionId;

	}	// getCollectionId


	/**
	* Set the chat collectionId to listen to.
	* @param collectionId The collectionId.
	*/
	public void setPrimaryCollectionId(String collectionId)
	{
		// if there's a change
		if (collectionId != m_primaryCollectionId)
		{
			// remember the new
			m_primaryCollectionId = collectionId;
		}

	}	// setCollectionId

	/**
	 * Get the status of preview: true - view new created; false - view revised existed
	 * @return The current status
	 */
	public boolean getIsNewCalendar()
	{
		return m_isNewCal;

	}	//	gsetIsCalendar


	/**
	* Set the status of preview: true - view new created; false - view revised existed
	* @param preview_status The status of preview: true - view new created; false - view revised existed
	*/
	public void setIsNewCalendar(boolean isNewcal)
	{
		// if there's a change
		if (isNewcal != m_isNewCal)
		{
			// remember the new
			m_isNewCal = isNewcal;
		}

	}	// setIsNewCalendar

	/**
	 * Get the status of past alert off: true - no alert shown; false - alert shown
	 * @return IsPastAlertOff
	 */
	public boolean getIsPastAlertOff()
	{
		return m_IsPastAlertOff;

	}	//	getIsPastAlertOff

	/**
	 * Get the status of delfield alert off: true - no alert shown; false - alert shown
	 * @return DelfieldAlertOff
	 */
	public boolean getDelfieldAlertOff()
	{
		return m_DelfieldAlertOff;

	}	//	getDelfieldAlertOff

	/**
	 * Gets the main calendar ID associated with the event list.  Many calendars may be merged into this list, but there is only one one calendar that is used for adding/modifying events.
	 * @return
	 */
	public String getPrimaryCalendarReference()
	{
		return m_primaryCalendarReference;
	}

	/**
	 * Set the status of past alert off: true - no alert shown; false - alert shown
	 * @param IsPastAlertOff The status of past alert off: true - no alert shown; false - alert shown
	 */
	public void setIsPastAlertOff(boolean IsPastAlertOff)
	{
		m_IsPastAlertOff = IsPastAlertOff;

	}	// setIsPastAlertOff

	/**
	 * Set the status of delfield alert off: true - no alert shown; false - alert shown
	 * @param DelfieldAlertOff The status of delfield alert off: true - no alert shown; false - alert shown
	 */
	public void setDelfieldAlertOff(boolean DelfieldAlertOff)
	{
		m_DelfieldAlertOff = DelfieldAlertOff;

	}	// setDelfieldAlertOff

	/**
	 * Sets the main calendar ID associated with the event list.  Many calendars may be merged into this
	 * list, but there is only one one calendar that is used for adding/modifying events/
	 * @param id
	 */
	public void setPrimaryCalendarReference(String reference)
	{
		m_primaryCalendarReference = reference;
	}

	/**
	 * @param id
	 */
	public void setCalendarEventId(String calendarReference, String eventId)
	{
		this.eventId = eventId;
		setSelectedCalendarReference(calendarReference);
	}

	/**
	 * @return
	 */
	public String getCalendarEventId()
	{
		return eventId;
	}

	/**
	 * @param calendarReference
	 * @param title
	 * @param description
	 * @param month
	 * @param day
	 * @param year
	 * @param hour
	 * @param minute
	 * @param dhour
	 * @param dminute
	 * @param type
	 * @param Am
	 * @param location
	 * @param addfieldsMap
	 */
	public void setNewData(String calendarReference, String title, String description, int month, int day, String year, int hour, int minute, int dhour, int dminute, String type, String Am, String location, Map addfieldsMap, String intentionStr)
	{
		savedData = new LocalEvent();
		savedData.setData(calendarReference, title,description,month,day,year,hour,minute,dhour,dminute,type,Am,location,addfieldsMap, intentionStr);
	}

	/**
	 *
	 */
	public void clearData()
	{
		savedData = new LocalEvent();
	}

	/**
	 * @return
	 */
	public LocalEvent getNewData()
	{
		return savedData;
	}

	/**
	 * @param currentday
	 */
	public void setcurrentDate(int currentday){ m_currentday = currentday; }
	/**
	 * @return
	 */
	public int getcurrentDate() { return m_currentday;}

	/**
	 * @param newday
	 */
	public void setnewDate(int newday) { m_newday = newday; }
	/**
	 * @return
	 */
	public int getnewDate() { return m_newday;}


	/**
	 * @param nextDate
	 */
	public void setnextDate(String nextDate){ m_nextDate = nextDate;}
	/**
	 * @return
	 */
	public String getnextDate(){return m_nextDate;}

	/**
	 * @param prevDate
	 */
	public void setprevDate(String prevDate){ m_prevDate = prevDate;}
	/**
	 * @return
	 */
	public String getprevDate(){return m_prevDate;}


	/**
	 * @return
	 */
	public CalendarUtil getCalObj()
	{
		return m_scalObj;
	}

	/**
	 * @param calObj
	 */
	public void setCalObj(CalendarUtil calObj)
	{
		m_scalObj = calObj;
	}

	/**
	 * @param state
	 */
	public void setPrevState(String state){ PrevState = state; }

	/**
	 * @return
	 */
	public String getPrevState(){ return PrevState; }


	/**
	 * @param currentday
	 */
	public void setcurrentDay(int currentday){ m_currentday = currentday; }

	/**
	 * @param currentmonth
	 */
	public void setcurrentMonth(int currentmonth){ m_currentmonth = currentmonth; }

	/**
	 * @param currentyear
	 */
	public void setcurrentYear(int currentyear){ m_currentyear = currentyear; }


	/**
	 * @return
	 */
	public int getcurrentDay(){ return m_currentday;}

	/**
	 * @return
	 */
	public int getcurrentMonth(){ return m_currentmonth;}

	/**
	 * @return
	 */
	public int getcurrentYear(){ return m_currentyear; }


	/**
	 *
	 */
	public CalendarActionState()
	{
		init();
	}

	/* (non-Javadoc)
	 * @see org.chefproject.core.ControllerState#recycle()
	 */
	public void recycle()
	{
		super.recycle();
		init();

	}	// recycle

	/* (non-Javadoc)
	 * @see org.chefproject.core.ControllerState#init()
	 */
	protected void init()
	{
		m_state = "week";
		m_scalObj = new CalendarUtil();
		m_currentday = m_scalObj.getDayOfMonth();
		m_currentyear = m_scalObj.getYear();
		m_currentmonth = m_scalObj.getMonthInteger();
		
		calendarFilter.setListViewDateRangeToDefault();
	}




	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(java.util.Observable observable, java.lang.Object obj) {
	}

	/**
	 * Used in the MyCalendar action and associated Velocity templates to store
	 * a single calendar event in an arbitrary calendar, not necessary the current
	 * primary calendar.
	 */
	public class LocalEvent
	{
		private String title;
		private String description;
		private int month;
		private String year;
		private int day;
		private int hour;
		private int minute;
		private int durationHour;
		private int durationMinute;
		private String type;
		private String am;
		private String location;
		private String calendarReference;
		private String intentionStr;

		// This is a map of additional properties.  These are dynamic and not like title, location, etc.
		private Map addFieldsMap;

		/**
		 * Default constructor
		 */
		public LocalEvent()
		{
		   title = null;
		   description = null;
		   month = 0;
		   day = 0;
		   year = "0";
		   hour = 0;
		   minute = -1;
		   durationHour = -1;
		   durationMinute = -1;
		   type = null;
		   am = null;
		   location="";
		   calendarReference = null;
		   addFieldsMap = new HashMap();
		   intentionStr = "";
		}

		/**
		 * @param calendarReference
		 * @param title
		 * @param description
		 * @param month
		 * @param day
		 * @param year
		 * @param hour
		 * @param minute
		 * @param dhour
		 * @param dminute
		 * @param type
		 * @param am
		 * @param location
		 * @param fieldsMap Map of additional properties
		 */
		public void setData(String calendarReference, String title, String description, int month, int day, String year, int hour, int minute, int dhour, int dminute, String type, String am, String location, Map fieldsMap, String intentionStr)
		{
		   this.title = title;
		   this.description = description;
		   this.month = month;
		   this.day = day;
		   this.year = year;
		   this.hour = hour;
		   this.minute = minute;
		   this.durationHour = dhour;
		   this.durationMinute = dminute;
		   this.type = type;
		   this.am = am;
		   this.location = location;
		   this.calendarReference = calendarReference;
		   this.addFieldsMap = fieldsMap;
		   this.intentionStr = intentionStr;
		}

		/**
		 * @return
		 */
		public String getTitle()
		{
			return title;
		}

		/**
		 * @return
		 */
		public String getDescription()
		{
			return description;
		}

		/**
		 * @return
		 */
		public int getMonth()
		{
			return month;
		}

		/**
		 * @return
		 */
		public int getDay()
		{
			return day;
		}

		/**
		 * @return
		 */
		public String getYear()
		{
			return year;
		}

		/**
		 * @return
		 */
		public int getYearInt()
		{
			return new Integer(year).intValue ();
		}

		/**
		 * @return
		 */
		public int getHour()
		{
			return hour;
		}

		/**
		 * @return
		 */
		public int getMinute()
		{
			return minute;
		}

		/**
		 * @return
		 */
		public int getDurationHour()
		{
			return durationHour;
		}

		/**
		 * @return
		 */
		public int getDurationMinute()
		{
			return durationMinute;
		}

		/**
		 * @return
		 */
		public String getType()
		{
			return type;
		}

		/**
		 * @return
		 */
		public String getAm()
		{
			return am;
		}

		/**
		 * @return
		 */
		public String getLocation()
		{
			return location;
		}

		/**
		 * Gets the value for one of the additional attribute fields.
		 * @param fieldname
		 * @return
		 */
		public String getAddfieldValue(String fieldname)
		{
			Set addfieldsKey = addFieldsMap.keySet();

			Iterator it = addfieldsKey.iterator();
			String prop_name = "";
			String prop_value = "";

			while (it.hasNext())
			{
				prop_name = (String) it.next();
				if (prop_name.equals(fieldname))
				{
					prop_value = (String) addFieldsMap.get(prop_name);
					return prop_value;
				}
			}
			return prop_value;
		}
		
		/**
		 * @return
		 */
		public String getIntentionStr()
		{
			return intentionStr;
		} // getIntentionStr

	}	// localEvent

	/**
	 * Gets the currently selected calendar.
	 * @return
	 */
	public String getSelectedCalendarReference()
	{
		return selectedCalendarReference;
	}

	/**
	 * Sets the currently selected calendar.
	 * @param string
	 */
	public void setSelectedCalendarReference(String string)
	{
		selectedCalendarReference = string;
	}

		
	/**
	 * Returns the calendar filter that is currently being used for the list
	 * view and printing.
	 * @return
	 */
	public CalendarFilter getCalendarFilter()
	{
		return calendarFilter;
	}


	/**
	 * @param IMPORT_WIZARD_SELECT_TYPE_STATE
	 */
	public void setImportWizardState(String importWizardState)
	{
		this.importWizardState = importWizardState;		
	}

	/**
	 * @return
	 */
	public String getImportWizardState()
	{
		return importWizardState;
	}


	/**
	 * @param importType
	 */
	public void setImportWizardType(String importWizardType)
	{
		this.importWizardType = importWizardType;
	}

	/**
	 * @return
	 */
	public String getImportWizardType()
	{
		return importWizardType;
	}


	/**
	 * @param importedEvents
	 */
	public void setWizardImportedEvents(List wizardImportedEvents)
	{
		this.wizardImportedEvents = wizardImportedEvents;		
	}

	/**
	 * @return
	 */
	public List getWizardImportedEvents()
	{
		return wizardImportedEvents;
	}

}	// class CalendarActionState

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/calendar/CalendarActionState.java,v 1.6 2004/06/22 03:04:57 ggolden Exp $
*
**********************************************************************************/