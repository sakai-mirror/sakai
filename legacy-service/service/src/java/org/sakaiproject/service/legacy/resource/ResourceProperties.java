/**********************************************************************************
* $URL$
* $Id$
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

// package
package org.sakaiproject.service.legacy.resource;

// import
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Stack;

import org.sakaiproject.exception.EmptyException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.time.Time;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
* <p>ResourceProperties is the core interface for the properties of all resources in CHEF (such as
* messages and message channels, content hosting collections and resources, etc).</p>
* <p>ResourceProperties are read only, and are name, value pairs.</p>
* <p>Pre-defined property names are defined in static string here. </p>
*
* @author Sakai Software Development Team
*/
public interface ResourceProperties
{
	/** Property for resource creator (uploader) (automatic). [user id string]*/
	public static final String PROP_CREATOR = "CHEF:creator";

	/** Property for resource last one to modify (automatic). [user id string]*/
	public static final String PROP_MODIFIED_BY = "CHEF:modifiedby";

	/** Property for creation (upload) date (live, from DAV:). [Time] */
	public static final String PROP_CREATION_DATE = "DAV:creationdate";

	/** Property for the display name (description) (dead, from DAV:). [String] */
	public static final String PROP_DISPLAY_NAME = "DAV:displayname";

	/** Property for the original filename (automatic). [String] */
	public static final String PROP_ORIGINAL_FILENAME = "CHEF:originalfilename";

	/** Property for the copyright attribution (user settable). [String] */
	public static final String PROP_COPYRIGHT = "CHEF:copyright";
	
	/** Property for the copyright choice attribution (user settable). [String] */
	public static final String PROP_COPYRIGHT_CHOICE = "CHEF:copyrightchoice";
	
	/** Property for the copyright alert attribution (user settable). [String] */
	public static final String PROP_COPYRIGHT_ALERT = "CHEF:copyrightalert";

	/** Property for the content length (live, from DAV:). [Long] */
	public static final String PROP_CONTENT_LENGTH = "DAV:getcontentlength";

	/** Property for the content type (live, from DAV:). [MIME type string] */
	public static final String PROP_CONTENT_TYPE = "DAV:getcontenttype";

	/** Property for the last modified date  (live, from DAV:, set when anything changes). [Time] */
	public static final String PROP_MODIFIED_DATE = "DAV:getlastmodified";

	/** Property that distinguishes a collection from a non-collection resource (automatic). [Boolean] */
	public static final String PROP_IS_COLLECTION = "CHEF:is-collection";

	/** Property that holds a ContentHosting collection body bytes quota, in K (user settable). [long] */
	public static final String PROP_COLLECTION_BODY_QUOTA = "CHEF:collection-body-quota";

	/** Property to associate a chat message with a chat room (user settable). [String] */
	public static final String PROP_CHAT_ROOM = "CHEF:chat-room";

	/** Property to target a message to a specific user (user settable). [User] */
	public static final String PROP_TO = "CHEF:to";

	/** Property for long open description (user settable). [String] */
	public static final String PROP_DESCRIPTION = "CHEF:description";

	/** Property for calendar event types (user settable). [String] */
	public static final String PROP_CALENDAR_TYPE = "CHEF:calendar-type";
	
	/** Property for calendar event location (user settable). [String] */
	public static final String PROP_CALENDAR_LOCATION = "CHEF:calendar-location";

	/** Property for the channel to categories names inside a discussion channel (user settable). [String] */
	public static final String PROP_DISCUSSION_CATEGORIES = "CHEF:discussion-categories";
	
	/** Property that discussion reply message style is star or thread (automatic) [String] */
 	public static final String PROP_REPLY_STYLE = "CHEF:discussion-reply-style";

	/** Property for a message channel indicating if the channel is 'enabled' (user settable) [Boolean] */
	public static final String PROP_CHANNEL_ENABLED = "CHEF:channel-enabled";

	/** Property for a message channel indicating if the channel is 'enabled' (user settable) [Boolean] */
	public static final String PROP_MAIL_CHANNEL_OPEN = "CHEF:mail-channel-open";
	
	/** Property for a site storing the email notification id associated with the site's mailbox (user settable) [String] */
	public static final String PROP_SITE_EMAIL_NOTIFICATION_ID = "CHEF:site-email-notification-id";
	
	/** Property for a site indicating if email archiveing is enabled for the site (user settable) [Boolean] */
	public static final String PROP_SITE_EMAIL_ARCHIVE = "CHEF:site-email-archive";

	/** Property for a ToolRegistration, the title of the tool (user settable) [String] */
	public static final String PROP_TOOL_TITLE = "CHEF:tool-title";

	/** Property for a ToolRegistration, description of the tool (user settable) [String] */
	public static final String PROP_TOOL_DESCRIPTION = "CHEF:tool-description";

	/** Property for a ToolRegistration, category of the tool (user settable) [String] %%% list desired -ggolden */
	public static final String PROP_TOOL_CATEGORY = "CHEF:tool-category";

    /** Property for calendar event extra fields (user settable). [String] */
    public static final String PROP_CALENDAR_EVENT_FIELDS = "CHEF:calendar-fields";

   /** Property for whether an assignment's open date will be announced (user settable). [String] */
    public static final String NEW_ASSIGNMENT_CHECK_AUTO_ANNOUNCE = "new_assignment_check_auto_announce";
	
	/** Property for whether an assignment's due date will be added into schedule as an event(user settable). [String] */
    public static final String NEW_ASSIGNMENT_CHECK_ADD_DUE_DATE = "new_assignment_check_add_due_date";
	
    /** Property for calendar event associated with an assignment's due date (user settable). [String] */
    public static final String PROP_ASSIGNMENT_DUEDATE_CALENDAR_EVENT_ID = "CHEF:assignment_duedate_calender_event_id";

   /** Property for announcement message id associated with an assignment's open date (user settable). [String] */
    public static final String PROP_ASSIGNMENT_OPENDATE_ANNOUNCEMENT_MESSAGE_ID = "CHEF:assignment_opendate_announcement_message_id";

   /** Property for assignment submission's previous grade (user settable). [String] */
    public static final String PROP_SUBMISSION_PREVIOUS_GRADES = "CHEF:submission_previous_grades";

	/** Property for assignment submission's scaled previous grade (user settable). [String] */
	public static final String PROP_SUBMISSION_SCALED_PREVIOUS_GRADES = "CHEF:submission_scaled_previous_grades";

	/** Property for assignment submission's previous inline feedback text (user settable). [String] */
    public static final String PROP_SUBMISSION_PREVIOUS_FEEDBACK_TEXT = "CHEF:submission_previous_feedback_text";

	/** Property for assignment submission's previous feedback comment (user settable). [String] */
    public static final String PROP_SUBMISSION_PREVIOUS_FEEDBACK_COMMENT = "CHEF:submission_previous_feedback_comment";

	/** Property for assignment been deleted status(user settable) [String] */
    public static final String PROP_ASSIGNMENT_DELETED = "CHEF:assignment_deleted";

	/** Property indicating public viewable (manual). [Boolean] */
	public static final String PROP_PUBVIEW = "SAKAI:pubview";

	/** URL MIME type */
	public static final String TYPE_URL = "text/url";

	/** The encoding of the resource - UTF-8 or ISO-8559-1 for example */
	public static final String PROP_CONTENT_ENCODING = "encoding";	

	/** Property for "object type" of a structured artifact */
	public static final String PROP_STRUCTOBJ_TYPE = "SAKAI:structobj_type";

   /** Used to find non structured object ContentResources (files, url's, etc.) */
   public static final String FILE_TYPE = "fileResource";

	/**
	* Access an iterator on the names of the defined properties (Strings).
	* @return An iterator on the names of the defined properties (Strings) (may be empty).
	*/
	public Iterator getPropertyNames();
	
	/**
	* Access a named property as a string (won't find multi-valued ones.)
	* @param name The property name.
	* @return the property value, or null if not found.
	*/
	public String getProperty(String name);

	/**
	* Access a named property as a List of (String), good for single or multi-valued properties.
	* @param name The property name.
	* @return the property value, or null if not found.
	*/
	public List getPropertyList(String name);

	/**
	* Access a named property as a properly formatted string.
	* @param name The property name.
	* @return the property value, or an empty string if not found.
	*/
	public String getPropertyFormatted(String name);

	/**
	* Check if a named property is a live one (auto updated).
	* @param name The property name.
	* @return True if the property is a live one, false if not.
	*/
	public boolean isLiveProperty(String name);

	/**
	* Access a named property as a boolean.
	* @param name The property name.
	* @return the property value.
	* @exception EmptyException if not found.
	* @exception TypeException if the property is found but not a boolean.
	*/
	public boolean getBooleanProperty(String name)
		throws EmptyException, TypeException;

	/**
	* Access a named property as a long.
	* @param name The property name.
	* @return the property value.
	* @exception EmptyException if not found.
	* @exception TypeException if the property is found but not a long.
	*/
	public long getLongProperty(String name)
		throws EmptyException, TypeException;

	/**
	* Access a named property as a Time.
	* @param name The property name.
	* @return the property value
	* @exception EmptyException if not found.
	* @exception TypeException if the property is found but not a Time.
	*/
	public Time getTimeProperty(String name)
		throws EmptyException, TypeException;

	/**
	* Access a named property as a User.
	* @param name The property name.
	* @return the property value
	* @exception EmptyException if not found.
	* @exception TypeException if the property is found but not a User.
	*/
	public User getUserProperty(String name)
		throws EmptyException, TypeException;

	/**
	 * Get the static String of PROP_CREATOR
	 * @return The static String of PROP_CREATOR 
	 */
	public String getNamePropCreator();

	/**
	 * Get the static String of PROP_MODIFIED_BY
	 * @return The static String of PROP_MODIFIED_BY 
	 */
	public String getNamePropModifiedBy();

	/**
	 * Get the static String of PROP_CREATION_DATE
	 * @return The static String of PROP_CREATION_DATE
	 */
	public String getNamePropCreationDate();

	/**
	 * Get the static String of PROP_DISPLAY_NAME
	 * @return The static String of PROP_DISPLAY_NAME
	 */
	public String getNamePropDisplayName();

	/**
	 * Get the static String of PROP_COPYRIGHT_CHOICE
	 * @return The static String of PROP_COPYRIGHT_CHOICE
	 */
	public String getNamePropCopyrightChoice();
	
	/**
	 * Get the static String of PROP_COPYRIGHT_ALERT
	 * @return The static String of PROP_COPYRIGHT_ALERT 
	 */
	public String getNamePropCopyrightAlert();
	
	/**
	 * Get the static String of PROP_COPYRIGHT
	 * @return The static String of PROP_COPYRIGHT 
	 */
	public String getNamePropCopyright();

	/**
	 * Get the static String of PROP_CONTENT_LENGTH
	 * @return The static String of PROP_CONTENT_LENGTH
	 */
	public String getNamePropContentLength();

	/**
	 * Get the static String of PROP_CONTENT_TYPE
	 * @return The static String of PROP_CONTENT_TYPE
	 */
	public String getNamePropContentType();
	
	/**
	 * Get the static String of PROP_MODIFIED_DATE
	 * @return The static String of PROP_MODIFIED_DATE
	 */
	public String getNamePropModifiedDate();

	/**
	 * Get the static String of PROP_IS_COLLECTION
	 * @return The static String of PROP_IS_COLLECTION
	 */
	public String getNamePropIsCollection();

	/**
	* Get the static String of PROP_COLLECTION_BODY_QUOTA
	* @return The static String of PROP_COLLECTION_BODY_QUOTA
	*/
	public String getNamePropCollectionBodyQuota();

	/**
	 * Get the static String of PROP_CHAT_ROOM
	 * @return The static String of PROP_CHAT_ROOM 
	 */
	public String getNamePropChatRoom();

	/**
	 * Get the static String of PROP_TO
	 * @return The static String of PROP_TO
	 */
	public String getNamePropTo();
	
	/**
	 * Get the static String of PROP_DESCRIPTION
	 * @return The static String of PROP_DESCRIPTION 
	 */
	public String getNamePropDescription();
	
	/**
	 * Get the static String of PROP_CALENDAR_TYPE
	 * @return The static String of PROP_CALENDAR_TYPE
	 */
	public String getNamePropCalendarType();
	
	/**
	 * Get the static String of PROP_CALENDAR_LOCATION
	 * @return The static String of PROP_CALENDAR_LOCATION
	 */
	public String getNamePropCalendarLocation();

	/**
 	* Get the static String of PROP_REPLY_STYLE
 	* @return The static String of PROP_REPLY_STYLE
 	*/
 	public String getNamePropReplyStyle();

	/**
 	* Get the static String of NEW_ASSIGNMENT_CHECK_ADD_DUE_DATE
 	* @return The static String of NEW_ASSIGNMENT_CHECK_ADD_DUE_DATE
 	*/
 	public String getNamePropNewAssignmentCheckAddDueDate();
	
	/**
 	* Get the static String of NEW_ASSIGNMENT_CHECK_AUTO_ANNOUNCE
 	* @return The static String of NEW_ASSIGNMENT_CHECK_AUTO_ANNOUNCE
 	*/
 	public String getNamePropNewAssignmentCheckAutoAnnounce();
	
	/**
 	* Get the static String of PROP_SUBMISSION_PREVIOUS_GRADES
 	* @return The static String of PROP_SUBMISSION_PREVIOUS_GRADES
 	*/
 	public String getNamePropSubmissionPreviousGrades();
	
	/**
	* Get the static String of PROP_SUBMISSION_SCALED_PREVIOUS_GRADES
	* @return The static String of PROP_SUBMISSION_SCALED_PREVIOUS_GRADES
	*/
	public String getNamePropSubmissionScaledPreviousGrades();
	
	/**
 	* Get the static String of PROP_SUBMISSION_PREVIOUS_FEEDBACK_TEXT
 	* @return The static String of PROP_SUBMISSION_PREVIOUS_FEEDBACK_TEXT
 	*/
 	public String getNamePropSubmissionPreviousFeedbackText();
	
	/**
 	* Get the static String of PROP_SUBMISSION_PREVIOUS_FEEDBACK_COMMENT
 	* @return The static String of PROP_SUBMISSION_PREVIOUS_FEEDBACK_COMMENT
 	*/
 	public String getNamePropSubmissionPreviousFeedbackComment();
	
	/**
 	* Get the static String of PROP_ASSIGNMENT_DELETED
 	* @return The static String of PROP_ASSIGNMENT_DELETED
 	*/
 	public String getNamePropAssignmentDeleted();
	
	/**
 	* Get the static String of PROP_STRUCTOBJ_TYPE
 	* @return The static String of PROP_STRUCTOBJ_TYPE
 	*/
 	public String getNamePropStructObjType();
	
	/**
	 * Get the static String of TYPE_URL
	 * @return The static String of TYPE_URL
	 */
	public String getTypeUrl();
	
	/**
	* Serialize the resource into XML, adding an element to the doc under the top of the stack element.
	* @param doc The DOM doc to contain the XML (or null for a string return).
	* @param stack The DOM elements, the top of which is the containing element of the new "resource" element.
	* @return The newly added element.
	*/
	public Element toXml(Document doc, Stack stack);

	/**
	* Add a single valued property.
	* @param name The property name.
	* @param value The property value.
	*/
	public void addProperty(String name, String value);

	/**
	* Add a value to a multi-valued property.
	* @param name The property name.
	* @param value The property value.
	*/
	public void addPropertyToList(String name, String value);

	/**
	* Add all the properties from the other ResourceProperties object.
	* @param other The ResourceProperties to add.
	*/
	public void addAll(ResourceProperties other);

	/**
	* Add all the properties from the Properties object.
	* @param props The Properties to add.
	*/
	public void addAll(Properties props);

	/**
	* Remove all properties.
	*/
	public void clear();

	/**
	* Remove a property.
	* @param name The property name.
	*/
	public void removeProperty(String name);

	/**
	* Take all values from this object.
	* @param other The ResourceProperties object to take values from.
	*/
	public void set(ResourceProperties other);

}	// ResourceProperties



