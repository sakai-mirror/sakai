/**********************************************************************************
*
* $Header: /cvs/sakai/annc/src/java/org/sakaiproject/tool/annc/AnnouncementTool.java,v 1.29 2004/09/30 20:19:37 ggolden.umich.edu Exp $
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
 * @version $Revision: 1.29 $
 */
public class AnnouncementTool implements SessionStateBindingListener
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
		private AnnouncementMessage announcement = null;

		/** Tool support: true if selected. */
		private boolean selected = false;

		/** Tool support: the site from which this announcement comes. */
		private String site = "aSite";

		/**
		 * Construct the decorator around this announcement message object.
		 * @param msg The announcement message.
		 */
		public Annc(AnnouncementMessage msg)
		{
			announcement = msg;
		}

		/**
		 * Access the Service API Announcement object.
		 * @return The Announcement entity from the service.
		 */
		public AnnouncementMessage getAnnouncement()
		{
			return announcement;
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
			return site;
		}

		/**
		 * Access the selected flag for this announcement.
		 * @return true if selected, false if not.
		 */
		public boolean isSelected()
		{
			return selected;
		}

		/**
		 * Setup for the list -> read transition on this announcement.
		 * @return navigation outcome: "read" to go to new mode, or null to stay here (due to an error condition).
		 */
		public String processActionListRead()
		{
			getLogger().info(this +".processActionListRead() for: " + getAnnouncement().getId());

			String rv = null;

			if (setupRead(getAnnouncement().getId()))
			{
				// done with the announcements
				clearAnnouncements();

				rv = "read";
			}

			else
			{
				FacesContext.getCurrentInstance().addMessage(
					null,
					MessageFactory.getMessage(
						FacesContext.getCurrentInstance(),
						"annc_error_read_setup",
						(new Object[] { getAnnouncement().getId()})));
			}

			return rv;
		}

		/**
		 * Set the Announcement entity from the service for this announcement.
		 * @param message The announcement.
		 */
		public void setAnnouncement(AnnouncementMessage message)
		{
			announcement = message;
		}

		/**
		 * Set the seleced flag for this announcement.
		 * @param b The selected flag.
		 */
		public void setSelected(boolean b)
		{
			selected = b;
		}

		/**
		 * Set the site id for this announcement.
		 * @param string The site id.
		 */
		public void setSite(String string)
		{
			site = string;
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

			switch (sort)
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
			if (ascending)
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
	protected List announcements = new Vector();

	/** Dependency: The announcement service. */
	protected AnnouncementService announcementService = null;

	/** Sort direction selection. */
	protected boolean ascending = true;

	/** Managed Property: The channel id to use (will be compute here if not set). */
	protected String channel = null;

	/** Announcement being edited. */
	protected AnnouncementMessageEdit editAnnc = null;

	/** Instruction message indicating edit mode: new or edit. */
	protected String editInstructionMsg = null;

	/** Toolbar message indicating edit mode: new or edit. */
	protected String editToolbarMsg = null;

	/** Dependency:  The logging service. */
	protected Logger logger = null;

	/** Dependency: The portal service. */
	protected PortalService portalService = null;

	/** Announcement selected for read. */
	protected Annc readAnnc = null;

	/** Sort field selection. */
	protected int sort = 0;

	/** Managed Property: The ToolConfiguration. */
	protected ToolConfiguration toolConfig = null;

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
		if (sort == newSort)
		{
			ascending = !ascending;
		}

		// else sort this way, ascending
		else
		{
			sort = newSort;
			ascending = true;
		}
	}

	/**
	 * Clear the list of announcements
	 */
	protected void clearAnnouncements()
	{
		setAnnouncements(null);
	}

	/**
	 * Access the list of announcements, refreshed if needed, sorted.
	 * @return The current list of announcements.
	 */
	public List getAnnouncements()
	{
		if (announcements.isEmpty())
		{
			refreshAnnouncements();
		}

		// sort
		Collections.sort(announcements, new AnnouncementComparator());

		return announcements;
	}

	/**
	 * Access the announcement service.
	 * @return The announcement service.
	 */
	protected AnnouncementService getAnnouncementService()
	{
		return announcementService;
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
			List anncs = getAnnouncements();
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
		if (channel == null)
		{
			// if we have a toolConfiguration, use the "channel" parameter if present
			if (toolConfig != null)
			{
				channel = toolConfig.getProperties().getProperty("channel");
			}

			// if nothing yet, try automatic configuration
			if (channel == null)
			{
				channel =
					announcementService.channelReference(portalService.getCurrentSiteId(), SiteService.MAIN_CONTAINER);
			}
		}

		return channel;
	}

	/**
	 * Access the announcement locked for edit.
	 * @return The announcement currently locked for edit.
	 */
	public AnnouncementMessageEdit getEditAnnc()
	{
		return editAnnc;
	}

	/**
	 * Access the selected editing instruction message.
	 * @return The message to use for instructions in Edit mode.
	 */
	public String getEditInstructionMsg()
	{
		return editInstructionMsg;
	}

	/**
	 * Access the message for the current edit (edit / new) mode.
	 * @return A message for the edit view's toolbar message.
	 */
	public String getEditToolbarMsg()
	{
		return editToolbarMsg;
	}

	/**
	 * Access the logging component.
	 * @return The logger to use.
	 */
	protected Logger getLogger()
	{
		return logger;
	}

	/**
	 * Access the portal service.
	 * @return The portal service.
	 */
	public PortalService getPortalService()
	{
		return portalService;
	}

	/**
	 * Access the announcement currently selected for reading (display).
	 * @return The current display announcement.
	 */
	public Annc getReadAnnc()
	{
		return readAnnc;
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
		if (sort == field)
		{
			if (ascending)
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
		return toolConfig;
	}

	/**
	 * Setup for the delete -(cancel)-> list transition.
	 * @return navigation outcome: "main" to go to list mode, or null to stay here (due to an error condition).
	 */
	public String processActionDeleteCancel()
	{
		getLogger().info(this +".processActionDeleteCancel()");

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
		getLogger().info(this +".processActionDeleteDelete()");

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
					getLogger().info(this +".processActionDeleteDelete: " + e);
					FacesContext.getCurrentInstance().addMessage(
						null,
						MessageFactory.getMessage(
							FacesContext.getCurrentInstance(),
							"annc_error_permission",
							(new Object[] { e.toString()})));
				}
				catch (InUseException e)
				{
					getLogger().info(this +".processActionDeleteDelete: " + e);
					FacesContext.getCurrentInstance().addMessage(
						null,
						MessageFactory.getMessage(
							FacesContext.getCurrentInstance(),
							"annc_error_delete_inuse",
							(new Object[] { e.getId()})));
				}
				catch (IdUnusedException e)
				{
					getLogger().info(this +".processActionDeleteDelete: " + e);
					FacesContext.getCurrentInstance().addMessage(
						null,
						MessageFactory.getMessage(
							FacesContext.getCurrentInstance(),
							"annc_error_delete_unused",
							(new Object[] { e.getId()})));
				}
			}
		}
		catch (IdUnusedException e)
		{
			getLogger().info(this +".processActionDeleteDelete: " + e);
			FacesContext.getCurrentInstance().addMessage(
				null,
				MessageFactory.getMessage(
					FacesContext.getCurrentInstance(),
					"annc_error_channel",
					(new Object[] { e.getId()})));
		}
		catch (PermissionException e)
		{
			getLogger().info(this +".processActionDeleteDelete: " + e);
			FacesContext.getCurrentInstance().addMessage(
				null,
				MessageFactory.getMessage(
					FacesContext.getCurrentInstance(),
					"annc_error_permission",
					(new Object[] { e.toString()})));
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
		getLogger().info(this +".processActionEditCancel(): annc: " + getEditAnnc());

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
			getLogger().info(this +".processActionEditCancel: " + e);
			FacesContext.getCurrentInstance().addMessage(
				null,
				MessageFactory.getMessage(
					FacesContext.getCurrentInstance(),
					"annc_error_edit_unused",
					(new Object[] { e.getId()})));
		}
		catch (PermissionException e)
		{
			getLogger().info(this +".processActionEditCancel: " + e);
			FacesContext.getCurrentInstance().addMessage(
				null,
				MessageFactory.getMessage(
					FacesContext.getCurrentInstance(),
					"annc_error_permission",
					(new Object[] { e.toString()})));
		}

		return null;
	}

	/**
	 * Setup for the edit -> preview transition.
	 * @return navigation outcome: "preview" to go to preview mode, or null to stay here (due to an error condition).
	 */
	public String processActionEditPreview()
	{
		getLogger().info(this +".processActionEditPreview(): annc: " + getEditAnnc());

		return "preview";
	}

	/**
	 * Setup for the edit -(save)-> list transition.
	 * @return navigation outcome: "main" to go to list mode, or null to stay here (due to an error condition).
	 */
	public String processActionEditSave()
	{
		getLogger().info(this +".processActionEditSave() annc: " + getEditAnnc());

		// set (not) draft mode
		getEditAnnc().getAnnouncementHeaderEdit().setDraft(false);

		// save
		if (saveEdit())
		{
			return "main";
		}

		FacesContext.getCurrentInstance().addMessage(
			null,
			MessageFactory.getMessage(FacesContext.getCurrentInstance(), "annc_error_edit_save", null));
		return null;
	}

	/**
	 * Setup for the edit -(save draft)-> list transition.
	 * @return navigation outcome: "main" to go to list mode, or null to stay here (due to an error condition).
	 */
	public String processActionEditSaveDraft()
	{
		getLogger().info(this +".processActionEditSaveDraft(): annc: " + getEditAnnc());

		// set draft mode
		getEditAnnc().getAnnouncementHeaderEdit().setDraft(true);

		// save
		if (saveEdit())
		{
			return "main";
		}

		FacesContext.getCurrentInstance().addMessage(
			null,
			MessageFactory.getMessage(FacesContext.getCurrentInstance(), "annc_error_edit_save", null));
		return null;
	}

	/**
	 * Setup for the list -> delete transition: make sure we have something selected to delete.
	 * @return navigation outcome: "delete" to go to delete mode, or null to stay here (due to an error condition).
	 */
	public String processActionListDelete()
	{
		getLogger().info(this +".processActionListDelete()");

		// make sure something is selected
		List anncs = getAnnouncementsSelected();
		if (anncs.isEmpty())
		{
			FacesContext.getCurrentInstance().addMessage(
				null,
				MessageFactory.getMessage(FacesContext.getCurrentInstance(), "annc_error_delete_select", null));

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
		getLogger().info(this +".processActionListEdit()");

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
			FacesContext.getCurrentInstance().addMessage(
				null,
				MessageFactory.getMessage(FacesContext.getCurrentInstance(), "annc_error_edit_select", null));
		}

		return rv;
	}

	/**
	 * Setup for the list -> new transition: ready the new Announcement for editing.
	 * @return navigation outcome: "edit" to go to edit (new) mode, or null to stay here (due to an error condition).
	 */
	public String processActionListNew()
	{
		getLogger().info(this +".processActionListNew()");

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
			setEditToolbarMsg(
				MessageFactory.getMessage(FacesContext.getCurrentInstance(), "annc_edit_new_toolbar", null).getDetail());

			// set the edit instruction message for "new" instead of "edit"
			setEditInstructionMsg(
				MessageFactory
					.getMessage(FacesContext.getCurrentInstance(), "annc_edit_new_instruction", null)
					.getDetail());

			// make the transition
			rv = "edit";
		}
		catch (IdUnusedException e)
		{
			getLogger().info(this +".processActionListNew: " + e);
			FacesContext.getCurrentInstance().addMessage(
				null,
				MessageFactory.getMessage(
					FacesContext.getCurrentInstance(),
					"annc_error_channel",
					(new Object[] { e.getId()})));
		}
		catch (PermissionException e)
		{
			getLogger().info(this +".processActionListNew: " + e);
			FacesContext.getCurrentInstance().addMessage(
				null,
				MessageFactory.getMessage(
					FacesContext.getCurrentInstance(),
					"annc_error_permission",
					(new Object[] { e.toString()})));
		}

		return rv;
	}

	/**
	 * Respond to a sort by subject click.
	 * @return navigation outcome: null (stay on this screen)
	 */
	public String processActionListSortDate()
	{
		getLogger().info(this +".processActionListSortDate()");
		changeSort(SORT_DATE);

		return null;
	}

	/**
	 * Respond to a sort by subject click.
	 * @return navigation outcome: null (stay on this screen)
	 */
	public String processActionListSortFrom()
	{
		getLogger().info(this +".processActionListSortFrom()");
		changeSort(SORT_FROM);

		return null;
	}

	/**
	 * Respond to a sort by subject click.
	 * @return navigation outcome: null (stay on this screen)
	 */
	public String processActionListSortSite()
	{
		getLogger().info(this +".processActionListSortSite()");
		changeSort(SORT_SITE);

		return null;
	}

	/**
	 * Respond to a sort by subject click.
	 * @return navigation outcome: null (stay on this screen)
	 */
	public String processActionListSortSubject()
	{
		getLogger().info(this +".processActionListSortSubject()");
		changeSort(SORT_SUBJECT);

		return null;
	}

	/**
	 * Setup for the preview -> edit transition.
	 * @return navigation outcome: "edit" to go to edit mode, or null to stay here (due to an error condition).
	 */
	public String processActionPreviewEdit()
	{
		getLogger().info(this +".processActionPreviewEdit(): annc: " + getEditAnnc());

		return "edit";
	}

	/**
	 * Setup for the read -> list transition.
	 * @return navigation outcome: "main" to go to list mode, or null to stay here (due to an error condition).
	 */
	public String processActionReadBack()
	{
		getLogger().info(this +".processActionReadBack()");

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
		getLogger().info(this +".processActionReadDelete()");

		// make sure something is selected
		Annc annc = getReadAnnc();
		if (annc != null)
		{
			annc.setSelected(true);

			// set a delete outcome
			return "delete";
		}

		FacesContext.getCurrentInstance().addMessage(
			null,
			MessageFactory.getMessage(FacesContext.getCurrentInstance(), "annc_error_delete_select", null));

		return null;
	}

	/**
	 * Setup for the read -> edit transition.
	 * @return navigation outcome: "edit" to go to edit mode, or null to stay here (due to an error condition).
	 */
	public String processActionReadEdit()
	{
		getLogger().info(this +".processActionReadEdit()");

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
		getLogger().info(this +".processActionReadNew()");

		// clear the read announcement
		setReadAnnc(null);

		return processActionListNew();
	}

	/**
	 * Refresh the list of announcements
	 */
	protected void refreshAnnouncements()
	{
		getLogger().info(this +".refreshAnnouncements()");

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
			getLogger().info(this +".getAnnouncements: " + e);
			FacesContext.getCurrentInstance().addMessage(
				null,
				MessageFactory.getMessage(
					FacesContext.getCurrentInstance(),
					"annc_error_channel",
					(new Object[] { e.getId()})));
		}
		catch (PermissionException e)
		{
			getLogger().info(this +".getAnnouncements: " + e);
			FacesContext.getCurrentInstance().addMessage(
				null,
				MessageFactory.getMessage(
					FacesContext.getCurrentInstance(),
					"annc_error_permission",
					(new Object[] { e.toString()})));
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
			getLogger().info(this +".saveEdit: " + e);
			FacesContext.getCurrentInstance().addMessage(
				null,
				MessageFactory.getMessage(
					FacesContext.getCurrentInstance(),
					"annc_error_channel",
					(new Object[] { e.getId()})));
		}
		catch (PermissionException e)
		{
			getLogger().info(this +".saveEdit: " + e);
			FacesContext.getCurrentInstance().addMessage(
				null,
				MessageFactory.getMessage(
					FacesContext.getCurrentInstance(),
					"annc_error_permission",
					(new Object[] { e.toString()})));
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
		announcements.clear();

		if (list == null)
			return;

		// process each new entry
		for (Iterator iAnncs = list.iterator(); iAnncs.hasNext();)
		{
			Object o = iAnncs.next();
			if (o instanceof AnnouncementMessage)
			{
				announcements.add(new Annc((AnnouncementMessage) o));
			}
		}
	}

	/**
	 * Set the announcement service to use.
	 * @param announcementService The announcement service component to use.
	 */
	public void setAnnouncementService(AnnouncementService announcementService)
	{
		System.out.println(this +".setAnnouncementService(): " + announcementService);
		this.announcementService = announcementService;
	}

	/**
	 * Set the channel id.
	 * @param string The channel id.
	 */
	public void setChannel(String string)
	{
		System.out.println(this +".setChannel()");
		channel = string;
	}

	/**
	 * Set the announcement currently locked for edit.
	 * @param edit The currently locked for edit announcement.
	 */
	protected void setEditAnnc(AnnouncementMessageEdit edit)
	{
		editAnnc = edit;
	}

	/**
	 * Set the instruction message to next use for edit mode.
	 * @param string The edit instruction message.
	 */
	public void setEditInstructionMsg(String string)
	{
		editInstructionMsg = string;
	}

	/**
	 * Set the toolbar message to next use for edit mode.
	 * @param string The edit toolbar message.
	 */
	public void setEditToolbarMsg(String string)
	{
		editToolbarMsg = string;
	}

	/**
	 * Set the logging component to use.
	 * @param logger The logger to use.
	 */
	public void setLogger(Logger logger)
	{
		this.logger = logger;
	}

	/**
	 * Set the portal service to use.
	 * @param service The portal service to use.
	 */
	public void setPortalService(PortalService service)
	{
		portalService = service;
	}

	/**
	 * Set the current read (display) announcement.
	 * @param annc The announcement currently being displayed.
	 */
	protected void setReadAnnc(Annc annc)
	{
		readAnnc = annc;
	}

	/**
	 * Set the ToolConfiguration for this tool placement.
	 * @param configuration The ToolConfiguration for this tool placement.
	 */
	public void setToolConfig(ToolConfiguration configuration)
	{
		toolConfig = configuration;
	}

	/**
	 * Setup to edit this message.
	 * @return true if setup ok, false if not.
	 */
	protected boolean setupEdit(String id)
	{
		getLogger().info(this +".setupEdit: id: " + id);

		try
		{
			// get the channel
			AnnouncementChannel channel = getAnnouncementService().getAnnouncementChannel(getChannel());

			// get the edit
			AnnouncementMessageEdit edit = channel.editAnnouncementMessage(id);

			setEditAnnc(edit);

			// set the edit toolbar message for "edit" instead of "new"
			setEditToolbarMsg(
				MessageFactory.getMessage(FacesContext.getCurrentInstance(), "annc_edit_edit_toolbar", null).getDetail());

			// set the edit instruction message for "edit" instead of "new"
			setEditInstructionMsg(
				MessageFactory
					.getMessage(FacesContext.getCurrentInstance(), "annc_edit_edit_instruction", null)
					.getDetail());

			return true;
		}
		catch (PermissionException e)
		{
			getLogger().info(this +".setupEdit: " + e);
			FacesContext.getCurrentInstance().addMessage(
				null,
				MessageFactory.getMessage(
					FacesContext.getCurrentInstance(),
					"annc_error_permission",
					(new Object[] { e.toString()})));
		}
		catch (IdUnusedException e)
		{
			getLogger().info(this +".setupEdit: " + e);
			FacesContext.getCurrentInstance().addMessage(
				null,
				MessageFactory.getMessage(
					FacesContext.getCurrentInstance(),
					"annc_error_edit_unused",
					(new Object[] { e.toString()})));
		}
		catch (InUseException e)
		{
			getLogger().info(this +".setupEdit: " + e);
			FacesContext.getCurrentInstance().addMessage(
				null,
				MessageFactory.getMessage(
					FacesContext.getCurrentInstance(),
					"annc_error_edit_inuse",
					(new Object[] { e.toString()})));
		}

		return false;
	}

	/**
	 * Setup to read this message.
	 * @return true if successful, false if not.
	 */
	protected boolean setupRead(String id)
	{
		getLogger().info(this +".setupRead: id: " + id);

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
			getLogger().info(this +".setupEdit: " + e);
			FacesContext.getCurrentInstance().addMessage(
				null,
				MessageFactory.getMessage(
					FacesContext.getCurrentInstance(),
					"annc_error_permission",
					(new Object[] { e.toString()})));
		}
		catch (IdUnusedException e)
		{
			getLogger().info(this +".setupEdit: " + e);
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
		// getLogger().info(this +".valueUnbound");

		if (getEditAnnc() != null)
		{
			// cancel an active edit
			getAnnouncementService().cancelMessage(getEditAnnc());
			setEditAnnc(null);

			// getLogger().info(this +".valueUnbound: cleaned up edit");
		}
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/annc/src/java/org/sakaiproject/tool/annc/AnnouncementTool.java,v 1.29 2004/09/30 20:19:37 ggolden.umich.edu Exp $
*
**********************************************************************************/
