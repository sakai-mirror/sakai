/**********************************************************************************
*
* $Header: /cvs/sakai/annc/src/java/org/sakaiproject/tool/annc/AnnouncementTool.java,v 1.31 2004/11/29 22:42:00 janderse.umich.edu Exp $
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

package org.sakaiproject.tool.annc;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.legacy.announcement.AnnouncementChannel;
import org.sakaiproject.service.legacy.announcement.AnnouncementMessage;
import org.sakaiproject.service.legacy.announcement.AnnouncementMessageEdit;
import org.sakaiproject.service.legacy.announcement.AnnouncementService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.portal.PortalService;
import org.sakaiproject.service.legacy.site.SiteService;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.framework.session.SessionStateBindingListener;
import org.sakaiproject.jsf.ToolBean;

import com.sun.faces.util.MessageFactory;

/**
 * <p>AnnouncementTool is a sample Sakai tool based on the CHEF 1.2 Announcement tool.</p>
 * <p>Still to do:<ul>
 * <li>Merge, Options, Permissions modes</li>
 * <li>Update after sort changes</li>
 * <li>User and Date support are minimal in the service.</li>
 * <li>Formatting issues in the Renderer</li>
 * </ul></p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.31 $
 */
public class AnnouncementTool implements SessionStateBindingListener, ToolBean
{
	/**
	 * <p>Annc decorates an Announcement entity from the Announcement Service for this tool.</p>
	 * <p>Control specific functionality added includes:<ul>
	 * <li>Selection and "click to read" support for the List mode.</li>
	 * <li>Draft icon support for the List mode.</li>
	 * <li>Site support.</li>
	 * </ul></p>
	 */
	public class Annc
	{
		/** The actual announcement from the service. */
		private AnnouncementMessage m_announcement = null;

		/** Tool support: true if selected. */
		private boolean m_selected = false;

		/** Tool support: the site from which this announcement comes. */
		private String m_site = "aSite";

		/**
		 * Construct the decorator around this announcement message object.
		 * @param msg The announcement message.
		 */
		public Annc(AnnouncementMessage msg)
		{
			m_announcement = msg;
		}

		/**
		 * Access the Service API Announcement object.
		 * @return The Announcement entity from the service.
		 */
		public AnnouncementMessage getAnnouncement()
		{
			return m_announcement;
		}

		public String getDraftIcon()
		{
			return getAnnouncement().getAnnouncementHeader().getDraft() ? "d" : "";
		}

		/**
		 * Access the site id this announcement belongs to.
		 * @return The announcement's site id.
		 */
		public String getSite()
		{
			return m_site;
		}

		/**
		 * Access the selected flag for this announcement.
		 * @return true if selected, false if not.
		 */
		public boolean isSelected()
		{
			return m_selected;
		}

		/**
		 * Setup for the list -> read transition on this announcement.
		 * @return navigation outcome: "read" to go to new mode, or null to stay here (due to an error condition).
		 */
		public String processActionListRead()
		{
			logMethodCall("for: " + getAnnouncement().getId());

			String rv = null;

			if (setupRead(getAnnouncement().getId()))
			{
				// done with the announcements
				clearAnnouncements();

				rv = "read";
			}

			else
			{
				addLocalizedMessage("annc_error_read_setup", getAnnouncement().getId());
			}

			return rv;
		}


		/**
		 * Set the Announcement entity from the service for this announcement.
		 * @param message The announcement.
		 */
		public void setAnnouncement(AnnouncementMessage message)
		{
			m_announcement = message;
		}

		/**
		 * Set the seleced flag for this announcement.
		 * @param b The selected flag.
		 */
		public void setSelected(boolean b)
		{
			m_selected = b;
		}

		/**
		 * Set the site id for this announcement.
		 * @param string The site id.
		 */
		public void setSite(String string)
		{
			m_site = string;
		}
	}

	/**
	 * <p>AnnouncementComparator is a class that helps to do sorting of announcements.</p>
	 */
	private class AnnouncementComparator implements Comparator
	{
		/**
		 * constructor
		 */
		public AnnouncementComparator()
		{
		}

		/**
		 * implementing the compare function
		 * @param o1 The first object
		 * @param o2 The second object
		 * @return The compare result. 1 if o1 < o2; 0 for same, -1 otherwise
		 */
		public int compare(Object o1, Object o2)
		{
			Annc a1 = (Annc) o1;
			Annc a2 = (Annc) o2;
			int result = 0;

			switch (m_sort)
			{
				case SORT_SUBJECT :
					result =
						a1.getAnnouncement().getAnnouncementHeader().getSubject().compareToIgnoreCase(
							a2.getAnnouncement().getAnnouncementHeader().getSubject());
					break;

				case SORT_DATE :
					result =
						a1.getAnnouncement().getAnnouncementHeader().getDate().before(
							a2.getAnnouncement().getAnnouncementHeader().getDate())
							? -1

							: 1;
					break;
				case SORT_FROM :
					result =
						a1.getAnnouncement().getAnnouncementHeader().getFrom().getSortName().compareToIgnoreCase(
							a2.getAnnouncement().getAnnouncementHeader().getFrom().getSortName());
					break;

				case SORT_SITE :
					result = a1.getSite().compareToIgnoreCase(a2.getSite());
					break;
			}

			// sort ascending or descending
			if (m_ascending)
			{
				result = -result;
			}

			return result;
		}
	}

	/** sort field selections. */
	protected static final int SORT_DATE = 0;
	protected static final int SORT_FROM = 1;
	protected static final int SORT_SITE = 2;
	protected static final int SORT_SUBJECT = 3;

	/** A list (Annc) announcements to be displayed. */
	protected List m_announcements = new Vector();

	/** Dependency: The announcement service. */
	protected AnnouncementService m_announcementService = null;

	/** Sort direction selection. */
	protected boolean m_ascending = true;

	/** Managed Property: The channel id to use (will be compute here if not set). */
	protected String m_channel = null;

	/** Announcement being edited. */
	protected AnnouncementMessageEdit m_editAnnc = null;

	/** Instruction message indicating edit mode: new or edit. */
	protected String m_editInstructionMsg = null;

	/** Toolbar message indicating edit mode: new or edit. */
	protected String m_editToolbarMsg = null;

	/** Dependency:  The logging service. */
	protected Logger m_logger = null;

	/** Dependency: The portal service. */
	protected PortalService m_portalService = null;

	/** Announcement selected for read. */
	protected Annc m_readAnnc = null;

	/** Sort field selection. */
	protected int m_sort = 0;

	/** Managed Property: The ToolConfiguration. */
	protected ToolConfiguration m_toolConfig = null;
 
	/**
	 * no-arg constructor.
	 */
	public AnnouncementTool()
	{
	}

	/**
	 * Change the sort / ascending based on a selection of this sort field.
	 * @param newSort The selected sort field.
	 */
	protected void changeSort(int newSort)
	{
		// if already sorting this way, reverse the direction
		if (m_sort == newSort)
		{
			m_ascending = !m_ascending;
		}

		// else sort this way, ascending
		else
		{
			m_sort = newSort;
			m_ascending = true;
		}
	}

	/**
	 * Clear the list of announcements
	 */
	protected void clearAnnouncements()
	{
		setAnnouncements(null);
	}

	public List getAnnouncements()
	{
	    List anncs = getAnnouncementsInternal();
	    if (anncs != null)
	    {
		    m_pagerTotalItems = anncs.size();
		    if (m_pagerLastItem > anncs.size()) m_pagerLastItem = anncs.size();
	        if (m_pagerFirstItem > m_pagerLastItem)
	        {
	            m_pagerFirstItem = m_pagerLastItem - m_pagerNumItems;
	        }
	        if (m_pagerFirstItem < 1) m_pagerFirstItem = 1;
	        
	        if (anncs.size() == 0) return anncs;
	        
	        return anncs.subList(getPagerFirstItem()-1, getPagerLastItem());
	    }
	    return null;
	}
	
	
	/**
	 * Access the list of announcements, refreshed if needed, sorted.
	 * @return The current list of announcements.
	 */
	protected List getAnnouncementsInternal()
	{
		if (m_announcements.isEmpty())
		{
			refreshAnnouncements();
		}

		// sort
		Collections.sort(m_announcements, new AnnouncementComparator());

		return m_announcements;
	}

	/**
	 * Access the announcement service.
	 * @return The announcement service.
	 */
	protected AnnouncementService getAnnouncementService()
	{
		return m_announcementService;
	}

	/**
	 * Get a list of those announcements from the getAnnouncements() list that are marked as selected.
	 * @return A list of selected announcements.
	 */
	public List getAnnouncementsSelected()
	{
		List rv = new Vector();

		// if there's a read annc and it's selected, use just that
		if ((getReadAnnc() != null) && (getReadAnnc().isSelected()))
		{
			rv.add(getReadAnnc());
		}

		// otherwise use the selected ones from the announcements
		else
		{
			List anncs = getAnnouncementsInternal();
			for (Iterator iAnncs = anncs.iterator(); iAnncs.hasNext();)
			{
				Annc annc = (Annc) iAnncs.next();
				if (annc.isSelected())
				{
					rv.add(annc);
				}
			}
		}

		return rv;
	}

	/**
	 * Access the configured channel id string.
	 * @return The channel id string.
	 */
	public String getChannel()
	{
		// figure the channel from the tool config or automatically, if it's not been set
		if (m_channel == null)
		{
			// if we have a toolConfiguration, use the "channel" parameter if present
			if (m_toolConfig != null)
			{
				m_channel = m_toolConfig.getProperties().getProperty("channel");
				if ("".equals(m_channel)) m_channel = null;
			}

			// if nothing yet, try automatic configuration
			if (m_channel == null)
			{
				m_channel =
					m_announcementService.channelReference(m_portalService.getCurrentSiteId(), SiteService.MAIN_CONTAINER);
			}
		}

		return m_channel;
	}

	/**
	 * Access the announcement locked for edit.
	 * @return The announcement currently locked for edit.
	 */
	public AnnouncementMessageEdit getEditAnnc()
	{
		return m_editAnnc;
	}

	/**
	 * Access the selected editing instruction message.
	 * @return The message to use for instructions in Edit mode.
	 */
	public String getEditInstructionMsg()
	{
		return m_editInstructionMsg;
	}

	/**
	 * Access the message for the current edit (edit / new) mode.
	 * @return A message for the edit view's toolbar message.
	 */
	public String getEditToolbarMsg()
	{
		return m_editToolbarMsg;
	}

	/**
	 * Access the logging component.
	 * @return The logger to use.
	 */
	protected Logger getLogger()
	{
		return m_logger;
	}

	/**
	 * Access the portal service.
	 * @return The portal service.
	 */
	public PortalService getPortalService()
	{
		return m_portalService;
	}

	/**
	 * Access the announcement currently selected for reading (display).
	 * @return The current display announcement.
	 */
	public Annc getReadAnnc()
	{
		return m_readAnnc;
	}

	/**
	 * Access the icon to use for the date column based on the sort selection.
	 * @return The sort icon for the data column.
	 */
	public String getSortDateIcon()
	{
		return getSortIcon(SORT_DATE);
	}

	/**
	 * Access the icon to use for the date column based on the sort selection.
	 * @return the sort icon for the from column.
	 */
	public String getSortFromIcon()
	{
		return getSortIcon(SORT_FROM);
	}

	/**
	 * Get the icon for the sort header display for this field.
	 * @param field The field.
	 * @return The icon the sort header display.
	 */
	protected String getSortIcon(int field)
	{
		if (m_sort == field)
		{
			if (m_ascending)
			{
				return "/image/sortascending.gif";
			}
			else
			{
				return "/image/sortdescending.gif";
			}
		}
		else
		{
			return "/image/nothing.gif";
		}
	}

	/**
	 * Access the icon to use for the date column based on the sort selection.
	 * @return The icon to use for the site column.
	 */
	public String getSortSiteIcon()
	{
		return getSortIcon(SORT_SITE);
	}

	/**
	 * Access the icon to use for the date column based on the sort selection.
	 * @return The icon to use for the subject column.
	 */
	public String getSortSubjectIcon()
	{
		return getSortIcon(SORT_SUBJECT);
	}

	/**
	 * Access the ToolConfiguration for this tool placement.
	 * @return The ToolConfiguration for this tool placement.
	 */
	public ToolConfiguration getToolConfig()
	{
		return m_toolConfig;
	}

	/**
	 * Setup for the delete -(cancel)-> list transition.
	 * @return navigation outcome: "main" to go to list mode, or null to stay here (due to an error condition).
	 */
	public String processActionDeleteCancel()
	{
		logMethodCall();

		// done with the announcements
		clearAnnouncements();
		setReadAnnc(null);

		return "main";
	}

	/**
	 * Setup for the delete -(cancel)-> list transition.
	 * @return navigation outcome: "main" to go to list mode, or null to stay here (due to an error condition).
	 */
	public String processActionDeleteDelete()
	{
		logMethodCall();

		try
		{
			// get the channel
			AnnouncementChannel channel = getAnnouncementService().getAnnouncementChannel(getChannel());

			// delete all the selected announcements
			List anncs = getAnnouncementsSelected();
			for (Iterator iAnncs = anncs.iterator(); iAnncs.hasNext();)
			{
				Annc annc = (Annc) iAnncs.next();
				try
				{
					// remove this message
					AnnouncementMessageEdit edit = channel.editAnnouncementMessage(annc.getAnnouncement().getId());
					channel.removeMessage(edit);
				}
				catch (PermissionException e)
				{
					handleException(e, "annc_error_permission", e.toString() );
				}
				catch (InUseException e)
				{
					handleException(e, "annc_error_delete_inuse", e.getId() );
				}
				catch (IdUnusedException e)
				{
					handleException(e, "annc_error_delete_unused", e.getId() );
				}
			}
		}
		catch (IdUnusedException e)
		{
			handleException(e, "annc_error_channel", e.getId() );
		}
		catch (PermissionException e)
		{
			handleException(e, "annc_error_permission", e.toString() );
		}

		// done with the announcements
		clearAnnouncements();
		setReadAnnc(null);

		return "main";
	}


	/**
	 * Setup for the edit -(cancel)-> list transition.
	 * @return navigation outcome: "main" to go to list mode, or null to stay here (due to an error condition).
	 */
	public String processActionEditCancel()
	{
		logMethodCall("annc: " + getEditAnnc());

		// clear and discard the edit
		try
		{
			// get the channel
			AnnouncementChannel channel = getAnnouncementService().getAnnouncementChannel(getChannel());

			// cancel
			channel.cancelMessage(getEditAnnc());

			setEditAnnc(null);

			return "main";
		}
		catch (IdUnusedException e)
		{
			handleException(e, "annc_error_edit_unused", e.getId());
		}
		catch (PermissionException e)
		{
			handleException(e, "annc_error_permission", e.toString());
		}

		return null;
	}

	/**
	 * Setup for the edit -> preview transition.
	 * @return navigation outcome: "preview" to go to preview mode, or null to stay here (due to an error condition).
	 */
	public String processActionEditPreview()
	{
		logMethodCall("annc: " + getEditAnnc());

		return "preview";
	}

	/**
	 * Setup for the edit -(save)-> list transition.
	 * @return navigation outcome: "main" to go to list mode, or null to stay here (due to an error condition).
	 */
	public String processActionEditSave()
	{
		logMethodCall("annc: " + getEditAnnc());

		// set (not) draft mode
		getEditAnnc().getAnnouncementHeaderEdit().setDraft(false);

		// save
		if (saveEdit())
		{
			return "main";
		}

		addLocalizedMessage("annc_error_edit_save", null);
		return null;
	}

	/**
	 * Setup for the edit -(save draft)-> list transition.
	 * @return navigation outcome: "main" to go to list mode, or null to stay here (due to an error condition).
	 */
	public String processActionEditSaveDraft()
	{
		logMethodCall("annc: " + getEditAnnc());

		// set draft mode
		getEditAnnc().getAnnouncementHeaderEdit().setDraft(true);

		// save
		if (saveEdit())
		{
			return "main";
		}

		addLocalizedMessage("annc_error_edit_save", null);
		return null;
	}

	/**
	 * Setup for the list -> delete transition: make sure we have something selected to delete.
	 * @return navigation outcome: "delete" to go to delete mode, or null to stay here (due to an error condition).
	 */
	public String processActionListDelete()
	{
		logMethodCall();

		// make sure something is selected
		List anncs = getAnnouncementsSelected();
		if (anncs.isEmpty())
		{
			addLocalizedMessage("annc_error_delete_select", null);
			return null;
		}

		// set a delete outcome
		return "delete";
	}

	/**
	 * Setup for the list -> edit transition: make sure we have exactly one selected.
	 * @return navigation outcome: "edit" to go to edit mode, or null to stay here (due to an error condition).
	 */
	public String processActionListEdit()
	{
		logMethodCall();

		String rv = null;

		// make sure only one thing is selected
		List anncs = getAnnouncementsSelected();
		if (anncs.size() == 1)
		{
			// setup for edit mode
			Annc annc = (Annc) anncs.get(0);

			// edit this announcement
			if (setupEdit(annc.getAnnouncement().getId()))
			{
				// done with the announcements
				clearAnnouncements();

				rv = "edit";
			}
		}

		else
		{
			addLocalizedMessage("annc_error_edit_select", null);
		}

		return rv;
	}

	/**
	 * Setup for the list -> new transition: ready the new Announcement for editing.
	 * @return navigation outcome: "edit" to go to edit (new) mode, or null to stay here (due to an error condition).
	 */
	public String processActionListNew()
	{
		logMethodCall();

		String rv = null;

		// make a new announcement and lock it as an edit
		try
		{
			// get the channel
			AnnouncementChannel channel = getAnnouncementService().getAnnouncementChannel(getChannel());

			// new
			AnnouncementMessageEdit edit = channel.addAnnouncementMessage();

			setEditAnnc(edit);

			// done with the announcements
			clearAnnouncements();

			// set the edit toolbar message for "new" instead of "edit"
			setEditToolbarMsg(getLocalizedMessage("annc_edit_new_toolbar", null).getDetail());

			// set the edit instruction message for "new" instead of "edit"
			setEditInstructionMsg(getLocalizedMessage("annc_edit_new_instruction", null).getDetail());

			// make the transition
			rv = "edit";
		}
		catch (IdUnusedException e)
		{
			handleException(e, "annc_error_channel", e.getId());
		}
		catch (PermissionException e)
		{
			handleException(e, "annc_error_permission", e.toString());
		}

		return rv;
	}

	/**
	 * Respond to a sort by subject click.
	 * @return navigation outcome: null (stay on this screen)
	 */
	public String processActionListSortDate()
	{
		logMethodCall();
		changeSort(SORT_DATE);

		return null;
	}

	/**
	 * Respond to a sort by subject click.
	 * @return navigation outcome: null (stay on this screen)
	 */
	public String processActionListSortFrom()
	{
		logMethodCall();
		changeSort(SORT_FROM);

		return null;
	}

	/**
	 * Respond to a sort by subject click.
	 * @return navigation outcome: null (stay on this screen)
	 */
	public String processActionListSortSite()
	{
		logMethodCall();
		changeSort(SORT_SITE);

		return null;
	}

	/**
	 * Respond to a sort by subject click.
	 * @return navigation outcome: null (stay on this screen)
	 */
	public String processActionListSortSubject()
	{
		logMethodCall();
		changeSort(SORT_SUBJECT);

		return null;
	}

	/**
	 * Setup for the preview -> edit transition.
	 * @return navigation outcome: "edit" to go to edit mode, or null to stay here (due to an error condition).
	 */
	public String processActionPreviewEdit()
	{
		logMethodCall("annc: " + getEditAnnc());

		return "edit";
	}

	/**
	 * Setup for the read -> list transition.
	 * @return navigation outcome: "main" to go to list mode, or null to stay here (due to an error condition).
	 */
	public String processActionReadBack()
	{
		logMethodCall();

		// clear the read announcement
		setReadAnnc(null);

		return "main";
	}

	/**
	 * Setup for the read -> delete transition: set the read annc to be selected.
	 * @return navigation outcome: "delete" to go to delete mode, or null to stay here (due to an error condition).
	 */
	public String processActionReadDelete()
	{
		logMethodCall();

		// make sure something is selected
		Annc annc = getReadAnnc();
		if (annc != null)
		{
			annc.setSelected(true);

			// set a delete outcome
			return "delete";
		}

		addLocalizedMessage("annc_error_delete_select", null);
		
		return null;
	}

	/**
	 * Setup for the read -> edit transition.
	 * @return navigation outcome: "edit" to go to edit mode, or null to stay here (due to an error condition).
	 */
	public String processActionReadEdit()
	{
		logMethodCall();

		// edit the read announcement
		if (setupEdit(getReadAnnc().getAnnouncement().getId()))
		{
			// and clear it
			setReadAnnc(null);

			return "edit";
		}

		return null;
	}

	/**
	 * Setup for the read -> new transition: ready the new Announcement for editing.
	 * @return navigation outcome: "edit" to go to edit (new) mode, or null to stay here (due to an error condition).
	 */
	public String processActionReadNew()
	{
		logMethodCall();

		// clear the read announcement
		setReadAnnc(null);

		return processActionListNew();
	}

	/**
	 * Refresh the list of announcements
	 */
	protected void refreshAnnouncements()
	{
		logMethodCall();

		clearAnnouncements();

		try
		{
			// get the channel
			AnnouncementChannel channel = getAnnouncementService().getAnnouncementChannel(getChannel());

			// get the messages
			List anncs = channel.getMessages(null, true);

			// use this to populate our decorated list
			setAnnouncements(anncs);
		}
		catch (IdUnusedException e)
		{
			handleException(e, "annc_error_channel", e.getId());
		}
		catch (PermissionException e)
		{
			handleException(e, "annc_error_permission", e.toString());
		}
	}

	/**
	 * Save the currently being edited announcement, adding it if it's new.
	 * @return true if successfull, false if not.
	 */
	protected boolean saveEdit()
	{
		try
		{
			// get the channel
			AnnouncementChannel channel = getAnnouncementService().getAnnouncementChannel(getChannel());

			// commit
			channel.commitMessage(getEditAnnc());

			// clear the edit
			setEditAnnc(null);

			return true;
		}
		catch (IdUnusedException e)
		{
			handleException(e, "annc_error_channel", e.getId());
		}
		catch (PermissionException e)
		{
			handleException(e, "annc_error_permission", e.toString());
		}

		return false;
	}

	/**
	 * Set the announcements list to be decorated objects of this list
	 * @param list The set of AnnouncementMessages for the list.
	 */
	protected void setAnnouncements(List list)
	{
		// clear the list
		m_announcements.clear();

		if (list == null)
			return;

		// process each new entry
		for (Iterator iAnncs = list.iterator(); iAnncs.hasNext();)
		{
			Object o = iAnncs.next();
			if (o instanceof AnnouncementMessage)
			{
				m_announcements.add(new Annc((AnnouncementMessage) o));
			}
		}
	}

	/**
	 * Set the announcement service to use.
	 * @param announcementService The announcement service component to use.
	 */
	public void setAnnouncementService(AnnouncementService announcementService)
	{
		logMethodCall(announcementService.toString());
		this.m_announcementService = announcementService;
	}

	/**
	 * Set the channel id.
	 * @param string The channel id.
	 */
	public void setChannel(String string)
	{
		logMethodCall();
		if ("".equals(string)) string = null;
		m_channel = string;
	}

	/**
	 * Set the announcement currently locked for edit.
	 * @param edit The currently locked for edit announcement.
	 */
	protected void setEditAnnc(AnnouncementMessageEdit edit)
	{
		m_editAnnc = edit;
	}

	/**
	 * Set the instruction message to next use for edit mode.
	 * @param string The edit instruction message.
	 */
	public void setEditInstructionMsg(String string)
	{
		m_editInstructionMsg = string;
	}

	/**
	 * Set the toolbar message to next use for edit mode.
	 * @param string The edit toolbar message.
	 */
	public void setEditToolbarMsg(String string)
	{
		m_editToolbarMsg = string;
	}

	/**
	 * Set the logging component to use.
	 * @param logger The logger to use.
	 */
	public void setLogger(Logger logger)
	{
		this.m_logger = logger;
	}

	/**
	 * Set the portal service to use.
	 * @param service The portal service to use.
	 */
	public void setPortalService(PortalService service)
	{
		m_portalService = service;
	}

	/**
	 * Set the current read (display) announcement.
	 * @param annc The announcement currently being displayed.
	 */
	protected void setReadAnnc(Annc annc)
	{
		m_readAnnc = annc;
	}

	/**
	 * Set the ToolConfiguration for this tool placement.
	 * @param configuration The ToolConfiguration for this tool placement.
	 */
	public void setToolConfig(ToolConfiguration configuration)
	{
		m_toolConfig = configuration;
	}
	
	private int m_pagerFirstItem = 1;
	private int m_pagerLastItem = 2;
	private int m_pagerNumItems = 2;
	private int m_pagerTotalItems = 0;
	
	public int getPagerFirstItem() { return m_pagerFirstItem; }
	public int getPagerLastItem() { return m_pagerLastItem; }
	public int getPagerNumItems() { return m_pagerNumItems; }
	
	public int getPagerTotalItems() 
	{ 
	    List anncs = getAnnouncementsInternal();
	    if (anncs != null)
	    {
	        m_pagerTotalItems = anncs.size();
	    }
	    return m_pagerTotalItems; 
	}
	
	public void setPagerFirstItem(int i) 
	{ 
	   m_pagerFirstItem = i; 
	   clearAnnouncements();
	}
	
	public void setPagerLastItem(int i)
    {
	    m_pagerLastItem = i;
	    clearAnnouncements();
    }

    public void setPagerNumItems(int i)
    {
       m_pagerNumItems = i;
       clearAnnouncements();
    }
    
	//public void setPagerTotalItems(int i) { m_pagerTotalItems = i; }
	
	
	/**
	 * Setup to edit this message.
	 * @return true if setup ok, false if not.
	 */
	protected boolean setupEdit(String id)
	{
		logMethodCall(id);

		try
		{
			// get the channel
			AnnouncementChannel channel = getAnnouncementService().getAnnouncementChannel(getChannel());

			// get the edit
			AnnouncementMessageEdit edit = channel.editAnnouncementMessage(id);

			setEditAnnc(edit);

			// set the edit toolbar message for "edit" instead of "new"
			setEditToolbarMsg(getLocalizedMessage("annc_edit_edit_toolbar", null).getDetail());

			// set the edit instruction message for "edit" instead of "new"
			setEditInstructionMsg(getLocalizedMessage("annc_edit_edit_instruction", null).getDetail());

			return true;
		}
		catch (PermissionException e)
		{
			handleException(e, "annc_error_permission", e.toString());
		}
		catch (IdUnusedException e)
		{
			handleException(e, "annc_error_edit_unused", e.toString());
		}
		catch (InUseException e)
		{
			handleException(e, "annc_error_edit_inuse", e.toString());
		}

		return false;
	}

	/**
	 * Setup to read this message.
	 * @return true if successful, false if not.
	 */
	protected boolean setupRead(String id)
	{
		logMethodCall(id);

		try
		{
			// get the channel
			AnnouncementChannel channel = getAnnouncementService().getAnnouncementChannel(getChannel());

			// get the message
			AnnouncementMessage msg = channel.getAnnouncementMessage(id);

			setReadAnnc(new Annc(msg));

			return true;
		}
		catch (PermissionException e)
		{
			handleException(e, "annc_error_permission", e.toString());
		}
		catch (IdUnusedException e)
		{
			handleException(e, null, null);
		}

		return false;
	}

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
		// logMethodCall();

		if (getEditAnnc() != null)
		{
			// cancel an active edit
			getAnnouncementService().cancelMessage(getEditAnnc());
			setEditAnnc(null);

			// getLogger().info(this +".valueUnbound: cleaned up edit");
		}
	}
	
	/** Log the name of the method that called logMethodCall().  This convienience
	 * method logs that a certain line of code was reached - this could be 
	 * used to log a method entrance, for example.
	 */
	protected void logMethodCall()
	{
		if (getLogger().isInfoEnabled())
		{
			// calculate the line of code that called logMethodCall() so that it can be logged
			String codeLocationStr = "(Unknown location)";
			StackTraceElement[] stes = (new Exception()).fillInStackTrace().getStackTrace();
			if (stes != null && stes.length >= 2)
			{
				codeLocationStr = stes[1].toString();
			}
			
			getLogger().info(codeLocationStr);
		}
	}
	
	/** Log the method that called logMethodCall().  This convienience
	 * method logs that a certain line of code was reached - this could be 
	 * used to log a method entrance, for example.
	 * @param addlLogMsg Additional message to log (along with the line of code reached)
	 */
	protected void logMethodCall(String addlLogMsg)
	{
		Logger logger = getLogger();
		if (logger != null && logger.isInfoEnabled())
		{
			// calculate the line of code that called logMethodCall() so that it can be logged
			String codeLocationStr = "(Unknown location)";
			StackTraceElement[] stes = (new Exception()).fillInStackTrace().getStackTrace();
			if (stes != null && stes.length >= 2)
			{
				codeLocationStr = stes[1].toString();
			}
			
			getLogger().info(codeLocationStr + ": " + addlLogMsg);
		}
	}

	
	/**
	 * Add a message to display to the user (because of an exception that occured).
	 * Logs the given exception, including where the exception was caught.
	 * Adds a JSF message to display to the user, from the given resource bundle
	 * property name and additional user message parameters.
	 * @param e Exception that caused the message
	 * @param resourceBundlePropertyName Resource bundle property name that contains the localized message to display
	 *   to the user
	 * @param userMessageParameters Additional parameters to the localized message to display
	 *   to the user
	 */
	protected void handleException(Exception e, String resourceBundlePropertyName, String userMessageParameter)
	{	
		if (e != null)
		{
			Logger logger = getLogger();
			if (logger != null && logger.isInfoEnabled())
			{
				// Log the location where the exception was caught
				String exceptionContextString = "(Unknown location)";
				StackTraceElement[] stes = (new Exception()).fillInStackTrace().getStackTrace();
				if (stes != null && stes.length >= 2)
				{
					exceptionContextString = stes[1].toString();
				}
				
				logger.info(exceptionContextString + " caught exception: " + e);
			}
		}
		if (resourceBundlePropertyName != null)
		{
			// Create a JSF message to display to the user
			addLocalizedMessage(resourceBundlePropertyName, userMessageParameter);
		}
	}
	
	protected void addLocalizedMessage(String resourceBundlePropertyName, String messageParameter)
	{
		FacesContext.getCurrentInstance().addMessage(null, getLocalizedMessage(resourceBundlePropertyName, messageParameter));
	}
	
	protected FacesMessage getLocalizedMessage(String resourceBundlePropertyName, String messageParameter)
	{
		Object[] messageParameters = null;
		if (messageParameter != null)
		{
			messageParameters = new Object[] { messageParameter };
		}
		return MessageFactory.getMessage(FacesContext.getCurrentInstance(), resourceBundlePropertyName, messageParameters);	
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/annc/src/java/org/sakaiproject/tool/annc/AnnouncementTool.java,v 1.31 2004/11/29 22:42:00 janderse.umich.edu Exp $
*
**********************************************************************************/
