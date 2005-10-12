/**********************************************************************************
* $URL$
* $Id$
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

package org.sakaiproject.tool.announcement;

// imports
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import org.sakaiproject.api.kernel.session.SessionBindingEvent;
import org.sakaiproject.api.kernel.session.SessionBindingListener;
import org.sakaiproject.api.kernel.tool.Tool;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.sakaiproject.cheftool.ControllerState;
import org.sakaiproject.service.framework.log.cover.Log;
import org.sakaiproject.service.framework.log.cover.Logger;
import org.sakaiproject.service.legacy.announcement.AnnouncementMessageEdit;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.util.ParameterParser;
import org.sakaiproject.util.java.StringUtil;

/**
* <p>AnnouncementActionState is the state object for the AnnouncementAction tool.  This
* object listens for changes on the announcement, and requests a UI delivery when
* changes occur.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/

public class AnnouncementActionState extends ControllerState implements SessionBindingListener
{	
	/** Resource bundle using current language locale */
    private static ResourceBundle rb = ResourceBundle.getBundle("announcement");
	
	/**
	 * Holds the display options for the Announcements tool
	 */
	static public class DisplayOptions
	{
		private static final String varNameEnforceNumberOfCharsPerAnnouncementLimit = "limitNumberOfCharsPerAnnouncement";
		private static final String varNameEnforceNumberOfAnnouncementsLimit = "limitNumberOfAnnouncements";
		private static final String varNameEnforceNumberOfDaysInPastLimit = "limitNumberOfDaysInPast";
		private static final String varNameShowAnnouncementBody = "showAnnouncementBody";
		private static final String varNameShowAllColumns = "showAllColumns";
		private static final String varNameNumberOfDaysInPast = "days";
		private static final String varNameNumberOfAnnouncements = "items";
		private static final String varNameNumberCharsPerAnnouncement = "length";
		private static final String varNameShowOnlyOptionsButton = "showOnlyOptionsButton";
		
		private static final String VarNameDisplaySelection = "displaySelection";
		private static final String ANNOUNCEMENT_TOOL_ID =  "sakai.announcements";

		boolean showAllColumns=true;
		boolean showAnnouncementBody=false;
		
		int numberOfDaysInThePast=365;
		boolean enforceNumberOfDaysInThePastLimit;
		
		int numberOfAnnouncements=100;
		boolean enforceNumberOfAnnouncementsLimit;

		int numberOfCharsPerAnnouncement=Integer.MAX_VALUE;
		boolean enforceNumberOfCharsPerAnnouncement;
		
		boolean showOnlyOptionsButton=false;
		
		/**
		 * Default constructor
		 */
		public DisplayOptions()
		{
		}
		
		/**
		 * Gets the number of announcements that we will show (if the limit is enabled).
		 */
		public int getNumberOfAnnouncements()
		{
			return numberOfAnnouncements;
		}

		/**
		 * Gets the number of characters that we will show in an announcement (if the limit is enabled).
		 */
		public int getNumberOfCharsPerAnnouncement()
		{
			return numberOfCharsPerAnnouncement;
		}

		/**
		 * Gets the number of days in the past for which we will show announcments (if the limit is enabled).
		 */
		public int getNumberOfDaysInThePast()
		{
			return numberOfDaysInThePast;
		}

		/**
		 * Gets whether or not we should show the announcement body.
		 */
		public boolean isShowAnnouncementBody()
		{
			return showAnnouncementBody;
		}

		/**
		 * Gets whether or not we should show all the columns associated with the announcement.
		 */
		public boolean isShowAllColumns()
		{
			return showAllColumns;
		}

		/**
		 * Sets the limit on the number of announcements to show (if the limit is enabled).
		 */
		public void setNumberOfAnnouncements(int i)
		{
			// Force the setting to be greater than or equal to zero since
			// a negative value doesn't make sense.
			numberOfAnnouncements = Math.max(i, 0);
		}

		/**
		 * 
		 * Sets the number of characters to show per announcement (if the limit is enabled).
		 */
		public void setNumberOfCharsPerAnnouncement(int i)
		{
			// Force the setting to be greater than or equal to zero since
			// a negative value doesn't make sense.
			numberOfCharsPerAnnouncement = Math.max(i, 0);
		}

		/**
		 * Sets the number of days in the past for which we will show announcments (if the limit is enabled).
		 */
		public void setNumberOfDaysInThePast(int i)
		{
			// Force the setting to be greater than or equal to zero since
			// a negative value doesn't make sense.
			numberOfDaysInThePast = Math.max(i, 0);
		}

		/**
		 * Sets whether or not we should show the announcement body.
		 */
		public void setShowAnnouncementBody(boolean b)
		{
			showAnnouncementBody = b;
		}

		/**
		 * Sets whether or not we should show all the columns associated with the announcement.
		 */
		public void setShowAllColumns(boolean b)
		{
			showAllColumns = b;
		}

		/**
		 * Returns true if we should limit the number of announcments shown.
		 */
		public boolean isEnforceNumberOfAnnouncementsLimit()
		{
			return enforceNumberOfAnnouncementsLimit;
		}

		/**
		 * Returns true if we should limit the number of characters per announcement.
		 */
		public boolean isEnforceNumberOfCharsPerAnnouncement()
		{
			return enforceNumberOfCharsPerAnnouncement;
		}

		/**
		 * Returns true if we should limit the announcements displayed based on the number of days
		 * in the past on which the occurred.
		 */
		public boolean isEnforceNumberOfDaysInThePastLimit()
		{
			return enforceNumberOfDaysInThePastLimit;
		}

		/**
		 * Sets whether or not we should limit the number of announcements displayed.
		 */
		public void setEnforceNumberOfAnnouncementsLimit(boolean b)
		{
			enforceNumberOfAnnouncementsLimit = b;
		}

		/**
		 * Sets whether or not we should limit the number of chars per announcement.
		 */
		public void setEnforceNumberOfCharsPerAnnouncement(boolean b)
		{
			enforceNumberOfCharsPerAnnouncement = b;
		}

		/**
		 * Sets whether or not we shoud limit the announcements displayed based on the number of days
		 * in the past on which they occurred.
		 */
		public void setEnforceNumberOfDaysInThePastLimit(boolean b)
		{
			enforceNumberOfDaysInThePastLimit = b;
		}
		

		/**
		 * 
		 * Utility routine used to get an integer named value from a map or supply a default value if none is found.
		 */
		private int getIntegerParameter(Map params, String paramName, int defaultValue)
		{
			String intValString = (String) params.get(paramName);
			
			if ( StringUtil.trimToNull(intValString) != null )
			{
				return Integer.parseInt(intValString);
			}
			else
			{
				return defaultValue;
			}
		}
		
		/**
		 * Utility routine used to get an boolean named value from a map or supply a default value if none is found.
		 */
		boolean getBooleanParameter(Map params, String paramName, boolean defaultValue)
		{
			String booleanValString = (String) params.get(paramName);
			
			if ( StringUtil.trimToNull(booleanValString) != null )
			{
				return Boolean.valueOf(booleanValString).booleanValue();
			}
			else
			{
				return defaultValue;
			}
		}
		
		/**
		 * Loads properties from a map into our object.
		 */
		public void loadProperties(Map params)
		{
			setShowAllColumns(getBooleanParameter(params, varNameShowAllColumns, showAllColumns));
			setShowAnnouncementBody(getBooleanParameter(params, varNameShowAnnouncementBody, showAnnouncementBody));
			setShowOnlyOptionsButton(getBooleanParameter(params, varNameShowOnlyOptionsButton, showOnlyOptionsButton));
			
		
			if ( params.get(varNameNumberOfDaysInPast) != null )
			{
				setNumberOfDaysInThePast(getIntegerParameter(params, varNameNumberOfDaysInPast, numberOfDaysInThePast));
				setEnforceNumberOfDaysInThePastLimit(true);
			}
			else
			{
				setEnforceNumberOfDaysInThePastLimit(false);
			}
			

			if ( params.get(varNameNumberOfAnnouncements) != null )
			{
				setNumberOfAnnouncements(getIntegerParameter(params, varNameNumberOfAnnouncements, numberOfAnnouncements));
				setEnforceNumberOfAnnouncementsLimit(true);
			}
			else
			{
				setEnforceNumberOfAnnouncementsLimit(false);
			}

			if ( params.get(varNameNumberCharsPerAnnouncement) != null )
			{
				setNumberOfCharsPerAnnouncement(getIntegerParameter(params, varNameNumberCharsPerAnnouncement, numberOfCharsPerAnnouncement));
				setEnforceNumberOfCharsPerAnnouncement(true);
			}
			else
			{
				setEnforceNumberOfCharsPerAnnouncement(false);
			}
		}

		/**
		 * Loads properties from a ParameterParser object (usually gotten from a user's page submission).
		 */
		public void loadProperties(ParameterParser parameters)
		{
			Tool tool = ToolManager.getCurrentTool();
			if (tool.getId().equals(ANNOUNCEMENT_TOOL_ID))
			{
				String VarNameDisplaySelection = parameters.getString("VarNameDisplaySelection");
				if (VarNameDisplaySelection.equals("sortable"))
				{
					setShowAllColumns(true);
					setShowAnnouncementBody(false);
					
					setNumberOfCharsPerAnnouncement(numberOfCharsPerAnnouncement);
					setEnforceNumberOfCharsPerAnnouncement(false);
				}
				else if (VarNameDisplaySelection.equals("sortableWithBody"))
				{
					setShowAllColumns(true);
					setShowAnnouncementBody(true);
					
					setNumberOfCharsPerAnnouncement(numberOfCharsPerAnnouncement);
					setEnforceNumberOfCharsPerAnnouncement(false);
				}
				else if (VarNameDisplaySelection.equals("list"))
				{
					setShowAllColumns(false);
					setShowAnnouncementBody(true);
					
					String varNameNumberChars = parameters.getString("changeChars");
					if (varNameNumberChars.equals(rb.getString("custom.shofir")))
					{
						setNumberOfCharsPerAnnouncement(50);
						setEnforceNumberOfCharsPerAnnouncement(true);
					}
					else if (varNameNumberChars.equals(rb.getString("custom.shofirtwo")))
					{
						setNumberOfCharsPerAnnouncement(100);
						setEnforceNumberOfCharsPerAnnouncement(true);
					}
					else if (varNameNumberChars.equals(rb.getString("custom.shoall")))
					{
						setNumberOfCharsPerAnnouncement(numberOfCharsPerAnnouncement);
						setEnforceNumberOfCharsPerAnnouncement(false);
					}
				}
			}
			else
			{
				setShowAllColumns(parameters.getBoolean(varNameShowAllColumns));
				setShowAnnouncementBody(parameters.getBoolean(varNameShowAnnouncementBody));
				
				String varNameNumberChars = parameters.getString("changeChars");
				if (varNameNumberChars.equals(rb.getString("custom.shofir")))
				{
					setNumberOfCharsPerAnnouncement(50);
					setEnforceNumberOfCharsPerAnnouncement(true);
				}
				else if (varNameNumberChars.equals(rb.getString("custom.shofirtwo")))
				{
					setNumberOfCharsPerAnnouncement(100);
					setEnforceNumberOfCharsPerAnnouncement(true);
				}
				else if (varNameNumberChars.equals(rb.getString("custom.shoall")))
				{
					setNumberOfCharsPerAnnouncement(numberOfCharsPerAnnouncement);
					setEnforceNumberOfCharsPerAnnouncement(false);
				}
			}
			// if this parameter has been defined, use its value to replace the current setting
			// otherwise, leave alone the current setting
			if (parameters.get(varNameShowOnlyOptionsButton) != null)
			{
				setShowOnlyOptionsButton(parameters.getBoolean(varNameShowOnlyOptionsButton));
			}

			setNumberOfDaysInThePast(parameters.getInt(varNameNumberOfDaysInPast));
			setEnforceNumberOfDaysInThePastLimit(StringUtil.trimToNull(parameters.get(varNameNumberOfDaysInPast)) != null);

			setNumberOfAnnouncements(parameters.getInt(varNameNumberOfAnnouncements, numberOfAnnouncements));
			setEnforceNumberOfAnnouncementsLimit(StringUtil.trimToNull(parameters.get(varNameNumberOfAnnouncements)) != null);

			//setNumberOfCharsPerAnnouncement(parameters.getInt(varNameNumberCharsPerAnnouncement, numberOfCharsPerAnnouncement));
			//setEnforceNumberOfCharsPerAnnouncement(StringUtil.trimToNull(parameters.get(varNameNumberCharsPerAnnouncement)) != null);
		}
		
		/**
		 * Saves the properties in this object to a ResourcePropertiesEdit object.
		 */
		public void saveProperties(Properties resEdit)
		{
			resEdit.setProperty(varNameShowAllColumns, Boolean.toString(showAllColumns));
			resEdit.setProperty(varNameShowAnnouncementBody, Boolean.toString(showAnnouncementBody));
			resEdit.setProperty(varNameShowOnlyOptionsButton, Boolean.toString(showOnlyOptionsButton));
		
			
			if ( isEnforceNumberOfDaysInThePastLimit() )
			{
				resEdit.setProperty(varNameNumberOfDaysInPast, Integer.toString(numberOfDaysInThePast));
			}
			else
			{
				// Since the absence of a property means that there are no limits, remove the
				// property from the resEdit object, in case it is already present.
				resEdit.remove(varNameNumberOfDaysInPast);
			}

			if ( this.isEnforceNumberOfAnnouncementsLimit() )
			{
				resEdit.setProperty( varNameNumberOfAnnouncements, Integer.toString(numberOfAnnouncements));
			}
			else
			{
				// Since the absence of a property means that there are no limits, remove the
				// property from the resEdit object, in case it is already present.
				resEdit.remove(varNameNumberOfAnnouncements);
			}
			

			if ( this.isEnforceNumberOfCharsPerAnnouncement())
			{
				resEdit.setProperty( varNameNumberCharsPerAnnouncement, Integer.toString(numberOfCharsPerAnnouncement));
			}
			else
			{
				// Since the absence of a property means that there are no limits, remove the
				// property from the resEdit object, in case it is already present.
				resEdit.remove(varNameNumberCharsPerAnnouncement);
			}
		}
		
		/**
		 * Gets a variable name for use in Velocity scripts to name input fields, etc.
		 */
		public static String getVarNameEnforceNumberOfAnnouncementsLimit()
		{
			return varNameEnforceNumberOfAnnouncementsLimit;
		}

		/**
		 * Gets a variable name for use in Velocity scripts to name input fields, etc.
		 */
		public static String getVarNameEnforceNumberOfDaysInPastLimit()
		{
			return varNameEnforceNumberOfDaysInPastLimit;
		}

		/**
		 * Gets a variable name for use in Velocity scripts to name input fields, etc.
		 */
		public static String getVarNameNumberCharsPerAnnouncement()
		{
			return varNameNumberCharsPerAnnouncement;
		}

		/**
		 * Gets a variable name for use in Velocity scripts to name input fields, etc.
		 */
		public static String getVarNameNumberOfAnnouncements()
		{
			return varNameNumberOfAnnouncements;
		}

		/**
		 * Gets a variable name for use in Velocity scripts to name input fields, etc.
		 */
		public static String getVarNameEnforceNumberOfCharsPerAnnouncementLimit()
		{
			return varNameEnforceNumberOfCharsPerAnnouncementLimit;
		}

		/**
		 * Gets a variable name for use in Velocity scripts to name input fields, etc.
		 */
		public static String getVarNameNumberOfDaysInPast()
		{
			return varNameNumberOfDaysInPast;
		}

		/**
		 * Gets a variable name for use in Velocity scripts to name input fields, etc.
		 */
		public static String getVarNameShowAllColumns()
		{
			return varNameShowAllColumns;
		}

		/**
		 * Gets a variable name for use in Velocity scripts to name input fields, etc.
		 */
		public static String getVarNameShowAnnouncementBody()
		{
			return varNameShowAnnouncementBody;
		}

		
		public boolean isShowOnlyOptionsButton()
		{
			return showOnlyOptionsButton;
		}

		
		public void setShowOnlyOptionsButton(boolean b)
		{
			showOnlyOptionsButton = b;
		}

	}
	


	/** Creates new AnnouncementActionState */
	public AnnouncementActionState() 
	{
		Log.debug("chef", "AnnouncementActionState created: " + this);
		init();
	}	// constructor
	
	
	/**
	* Release any resources and restore the object to initial conditions to be reused.
	*/
	public void recycle()
	{
		Log.debug("chef", "AnnouncementActionState recycled: " + this);
		super.recycle();

	}	// recycle

	/**
	* Init to startup values
	*/
	protected void init()
	{
		super.init();
		
	}	// init
	
	
	private Site m_editSite;
	// the announcement channel id
	private String m_channelId = null;
	// the announecement message id
	//private String m_messageId = null;
	// the announecement message reference
	private String m_messageReference = null;
	// parameter for announcement status: true - new created; false - already existing
	private boolean m_isNewAnn = true;
	// the template index: the main list screen - true; other screen - false
	private boolean m_isListVM = true;
	// the list of messages to be deleted
	private Vector m_delete_messages = new Vector();
	// collection ID
	//private String m_collectionId = null;
	// vm status
	private String m_status = null;
	// temporary attachment list - never set to null!
	private List m_attachments = EntityManager.newReferenceList();
	// temporary selected attachment list
	private Vector m_selectedAttachments = new Vector();
	// temporary added attachments
	private Vector m_moreAttachments = new Vector();
	// temporary attachments only for attachment editing
	private Vector m_tempAttachments = new Vector();
	// temporary moreAttachments only for attachment editing
	private Vector m_tempMoreAttachments = new Vector();
	// temporary storage for new announcement subject
	private String m_tempSubject;
	// temporary storage for new announcement body
	private String m_tempBody;
	//temporary storage for announce to selection
	private String m_tempAnnounceTo;
	// temporary storage for local file inputStream, contentType and display name
	private HashMap m_fileProperties = new HashMap();
	// temporary storage for attachment properties: title, description, and copyright
	private HashMap m_attachProperties = new HashMap();
	// storage for home collection Id
	private String m_homeCollectionId;
	// storage for home Collection Display ame
	private String m_homeCollectionDisplayName;
	
	// ********* for sorting *********
	//  the current sorted by property name
	private String m_currentSortedBy = "date";
	// the current sort sequence: ture - acscending/false - descending
	private boolean m_currentSortAsc = false;
	// ********* for sorting *********
	
	private DisplayOptions m_displayOptions;
	
	/**
	 * Get 
	 */
	public String getTempSubject()
	{
		return m_tempSubject;

	}	// getTempSubject()
	
	/**
	 * Get 
	 */
	public String getTempBody()
	{
		return m_tempBody;

	}	// getTempBody()
	
	
	/**
	 * Get 
	 */
	public String getTempAnnounceTo()
	{
		return m_tempAnnounceTo;

	}	// getTempAnnounceTo()
	
	
	/**
	 * set 
	 */
	public void setTempAnnounceTo(String tempAnnounceTo)
	{
		// if there's a change
		if (!tempAnnounceTo.equals(m_tempAnnounceTo))
		{
			// remember the new
			m_tempAnnounceTo = tempAnnounceTo;
		}

	}	// setTempAnnounceTo()
	
	/**
	 * Get 
	 */
	public void setTempSubject(String tempSubject)
	{
		// if there's a change
		if (tempSubject != m_tempSubject)
		{
			// remember the new
			m_tempSubject = tempSubject;
		}

	}	// setTempSubject()
	
	
	
	/**
	 * Get 
	 */
	public void setTempBody(String tempBody)
	{
		if (tempBody != m_tempBody)
		{
			// remember the new
			m_tempBody = tempBody;
		}

	}	// setTempBody()
	
	/**
	 * Get the channel id of current state
	 * @return The current channel id
	 */
	public String getChannelId()
	{
		return m_channelId;

	}	// getChannelId
	

	/**
	* Set the chat channel id to listen to.
	* @param channel The chat channel id to listen to.
	*/
	public void setChannelId(String channelId)
	{
		// if there's a change
		if (channelId != m_channelId)
		{
			// remember the new
			m_channelId = channelId;
		}

	}	// setChannelId
	
	
//	/**
//	 * Get the collectionId of current state
//	 * @return The current collectionId
//	 */
//	public String getCollectionId()
//	{
//		return m_collectionId;
//
//	}	// getCollectionId
//	
//
//	/**
//	* Set the chat collectionId to listen to.
//	* @param collectionId The collectionId.
//	*/
//	public void setCollectionId(String collectionId)
//	{
//		// if there's a change
//		if (collectionId != m_collectionId)
//		{
//			// remember the new
//			m_collectionId = collectionId;
//		}
//
//	}	// setCollectionId
	
	
	/**
	 * Get the the file properties for uploading
	 * @return The current collectionId
	 */	
	public Vector getFileProperties(String key)
	{
		
		Set m_filePropertiesSet = m_fileProperties.entrySet();
		Iterator i = m_filePropertiesSet.iterator();
		
		while(i.hasNext())
		{
			Map.Entry me = (Map.Entry) i.next();
			if ( (me.getKey()).equals(key) )
				return (Vector)me.getValue();
		}
		return null;

	}	// getFileProperties
	

	/**
	* Set the  fileProperties
	* @param key The key for map class, which is the absolute local path of file
	* @param properties The Vector which stores the inputStream, contentType, fileName of the file in order
	*/
	
	public void setFileProperties(String key, Vector properties)
	{
		m_fileProperties.put(key, properties);

	}	// setFileProperties
	
	
	/**
	 * Get the attachment properties
	 * @return The property based on the given key
	 */	
	public Vector getAttachProperties(String key)
	{
		
		Set m_attachPropertiesSet = m_attachProperties.entrySet();
		Iterator i = m_attachPropertiesSet.iterator();
		
		while(i.hasNext())
		{
			Map.Entry me = (Map.Entry) i.next();
			if ( (me.getKey()).equals(key) )
				return (Vector)me.getValue();
		}
		return null;

	}	// getAttachProperties
	

	/**
	* Set the  attachProperties
	* @param key The key for map class, which is the absolute local path of file
	* @param properties The Vector which stores the attachment properties: title, description, and copyright in order
	*/	
	public void setAttachProperties(String key, Vector properties)
	{
		m_attachProperties.put(key, properties);

	}	// setAttachProperties
	
	
	
	
	public void setFF(String key)
	{
		key.length();
	}

	
	/**
	 * Get the status of preview: true - view new created; false - view revised existed
	 * @return The current status
	 */
	public boolean getIsNewAnnouncement()
	{
		return m_isNewAnn;

	}	//	gsetIsNewAnnouncement
	

	/**
	* Set the status of preview: true - view new created; false - view revised existed
	* @param preview_status The status of preview: true - view new created; false - view revised existed
	*/
	public void setIsNewAnnouncement(boolean isNewAnn)
	{
		// if there's a change
		if (isNewAnn != m_isNewAnn)
		{
			// remember the new
			m_isNewAnn = isNewAnn;
		}

	}	// setIsNewAnnouncement
	
	
	/**
	 * Get the current vm: true - in main list view; false - in other view
	 * @return The boolean to show whether in main list view
	 */
	public boolean getIsListVM()
	{
		return m_isListVM;

	}	//	getIsListVM
	

	/**
	* Set the current vm: true - in main list view; false - in other view
	* @param m_isListVM: true - in main list view; false - in other view 
	*/
	public void setIsListVM(boolean isListVM)
	{
		m_isListVM = isListVM;
	}	// setIsListVM
	
	
	/**
	 * Get 
	 */
	public Vector getDelete_messages()
	{
		Log.debug("chef", "AnnouncementActionState getDelete_messages() called: " + this);
		return m_delete_messages;

	}	//	getDelete_messages

	/**
	* Set
	*/
	public void setDeleteMessages(Vector delete_messages)
	{
		// if there's a change
		if (delete_messages != null)
		{
			m_delete_messages = (Vector) delete_messages.clone();
		}
		else
		{
			m_delete_messages = null;
		}		
		// remember the new
		
	}	// setDelete_messages
	
	private AnnouncementMessageEdit m_edit;
	
	/**
	 * Get edit The edit object
	 */
	public void setEdit(AnnouncementMessageEdit edit)
	{
		m_edit = edit;
	}
	
	/**
	 * Get edit object
	 * @return m_edit The edit object
	 */
	public AnnouncementMessageEdit getEdit()
	{
		return m_edit;
	}

	/**
	 * Get 
	 */
	public List getAttachments()
	{
		return m_attachments;

	}	//	getAttachment
	
	/**
	* Set
	*/
	public void setAttachments(List attachments)
	{
		if (attachments != null)
		{
			m_attachments = EntityManager.newReferenceList(attachments);
		}
		else
		{
			m_attachments.clear();
		}		
		// remember the new
		
	}	//	setAttachments
	

	/**
	 * Get 
	 */
	public Vector getSelectedAttachments()
	{
		return m_selectedAttachments;

	}	//	getSelectedAttachment
	
	/**
	* Set
	*/
	public void setSelectedAttachments(Vector selectedAttachments)
	{
		// if there's a change
		if (selectedAttachments != null)
		{
			m_selectedAttachments = (Vector) selectedAttachments.clone();
		}
		else
		{
			m_selectedAttachments = null;
		}
		
		// remember the new
	}	//	setSelectedAttachments
	
	
	/**
	 * Get 
	 */
	public Vector getMoreAttachments()
	{
		return m_moreAttachments;

	}	//	getMoreAttachment
	
	/**
	* Set
	*/
	public void setMoreAttachments(Vector moreAttachments)
	{
		// if there's a change
		if (moreAttachments != null)
		{
			m_moreAttachments = (Vector) moreAttachments.clone();
		}
		else
		{
			m_moreAttachments = null;
		}
		
		// remember the new
		
	}	//	setMoreAttachments
	
	
	
	/**
	 * Get 
	 */
	public Vector getTempAttachments()
	{
		return m_tempAttachments;

	}	//	getTempAttachments
	
	/**
	* Set
	*/
	public void setTempAttachments(Vector tempAttachments)
	{
		// if there's a change
		if (tempAttachments != null)
		{
			m_tempAttachments = (Vector) tempAttachments.clone();
		}
		else
		{
			m_tempAttachments = null;
		}
		
		// remember the new
		
	}	//	setTempAttachments
	
	/**
	 * Get 
	 */
	public Vector getTempMoreAttachments()
	{
		return m_tempMoreAttachments;

	}	//	getTempMoreAttachments()
	
	/**
	* Set
	*/
	public void setTempMoreAttachments(Vector tempMoreAttachments)
	{
		// if there's a change
		if (tempMoreAttachments != null)
		{
			m_tempMoreAttachments = (Vector) tempMoreAttachments.clone();
		}
		else
		{
			m_tempMoreAttachments = null;
		}
		
		// remember the new
		
	}	//	setTempMoreAttachments()
	/*******************************************************************************
	* Observer implementation
	*******************************************************************************/

	/**
	* This method is called whenever the observed object is changed. An
	* application calls an <tt>Observable</tt> object's
	* <code>notifyObservers</code> method to have all the object's
	* observers notified of the change.
	*
	* default implementation is to cause the courier service to deliver to the
	* interface controlled by my controller.  Extensions can override.
	*
	* @param   o     the observable object.
	* @param   arg   an argument passed to the <code>notifyObservers</code>
	*                 method.
	*/
	public void update(Observable o, Object arg)
	{
//		// deliver to the control panel of the portlet
//		CourierService.deliver(getSetId(), getId()/* %%%, ChatListAction.CONTROL_PANEL */);

	}
	
	/**
	 * Get the status to be
	 * @return The status to be
	 */
	public String getStatus() 
	{
	    return m_status;
	    
	}
	
	/**
	 * Set the status to be
	 * @param status The status to be
	 */
	public void setStatus(String status) 
	{
		// if there's a change
		if (status != m_status) {
		// remember the new
		m_status = status;
	    }
	    
	}
	
		// ********* for sorting *********
	/**
	 *	set the current sorted by property name
	 * @param name The sorted by property name
	 */
	protected void setCurrentSortedBy(String name)
	{
		m_currentSortedBy = name;
		
	}	// setCurrentSortedBy
	
	/**
	 * get the current sorted by property name
	 * @return "true" if the property is sorted ascendingly; "false" if the property is sorted descendingly
	 */
	protected String getCurrentSortedBy()
	{
		return m_currentSortedBy;
		
	}	// getSortCurrentSortedBy
	
	
	/**
	 * set the current sort property 
	 * @param asc "true" if the property is sorted ascendingly; "false" if the property is sorted descendingly
	 */
	protected void setCurrentSortAsc(boolean asc)
	{
		m_currentSortAsc = asc;
		
	}	// setCurrentSortAsc
	
	/**
	 * get the current sort property
	 * @return "true" if the property is sorted ascendingly; "false" if the property is sorted descendingly
	 */
	protected boolean getCurrentSortAsc()
	{
		return m_currentSortAsc;
		
	}	// getCurrentSortAsc
	
	// ********* for sorting *********

	/**
	 * Returns the currently selected message reference.
	 */
	public String getMessageReference()
	{
		return m_messageReference;
	}

	/**
	 * Sets the currently selected message reference.
	 */
	public void setMessageReference(String string)
	{
		m_messageReference = string;
	}


	
	public Site getEditSite()
	{
		return m_editSite;
	}


	
	public void setEditSite(Site site)
	{
		m_editSite = site;
		
	}

	
	public DisplayOptions getDisplayOptions()
	{
		return m_displayOptions;
	}

	
	public void setDisplayOptions(DisplayOptions options)
	{
		m_displayOptions = options;
	}

	/*******************************************************************************
	* SessionBindingListener implementation
	*******************************************************************************/

	public void valueBound(SessionBindingEvent event)
	{
	}

	public void valueUnbound(SessionBindingEvent event)
	{
		if (Logger.isDebugEnabled())
			Logger.debug(this +".valueUnbound()");

		// pass it on to my edits
		if ((m_editSite != null) && (m_editSite instanceof SessionBindingListener))
		{
			((SessionBindingListener) m_editSite).valueUnbound(event);
		}

		if ((m_edit != null) && (m_edit instanceof SessionBindingListener))
		{
			((SessionBindingListener) m_edit).valueUnbound(event);
		}

	} // valueUnbound

}	// AnnouncementActionState



