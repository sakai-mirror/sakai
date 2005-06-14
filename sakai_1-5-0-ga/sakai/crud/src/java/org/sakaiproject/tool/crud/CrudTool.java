/**********************************************************************************
*
* $Header: /cvs/sakai/crud/src/java/org/sakaiproject/tool/crud/CrudTool.java,v 1.6 2004/11/10 02:48:03 janderse.umich.edu Exp $
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

package org.sakaiproject.tool.crud;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.faces.context.FacesContext;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.VersionException;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.jsf.ToolBean;
import org.sakaiproject.service.sample.crud.CrudObject;
import org.sakaiproject.service.sample.crud.CrudService;

import com.sun.faces.util.MessageFactory;

/**
 * <p>CrudTool is a sample Sakai tool showing a data Create, Read, Update and Delete interface.</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.6 $
 */
public class CrudTool implements ToolBean
{
	/**
	 * <p>DecoratedCrudObject decorates a CrudObject for use in the interface.</p>
	 * <p>Functionality added:<ul>
	 * <li>Selection and "click to read" support.</li>
	 * </ul></p>
	 */
	public class DecoratedCrudObject
	{
		/** The actual CrudObject being decorated. */
		protected CrudObject m_co = null;

		/** Tool support: true if selected. */
		protected boolean m_selected = false;

		/** Tool support: true if just created (i.e. from a new) and not yet edited and saved. */
		protected boolean m_justCreated = false;

		/**
		 * Construct the decorator around this object.
		 * @param entry The CrudObject.
		 */
		public DecoratedCrudObject(CrudObject entry)
		{
			m_co = entry;
		}

		/**
		 * Access the CrudObject we wrap.
		 * @return The CrudObject.
		 */
		public CrudObject getEntry()
		{
			return m_co;
		}

		/**
		 * Access the just created flag.
		 * @return true if this was just created, false if not.
		 */
		public boolean isJustCreated()
		{
			return m_justCreated;
		}

		/**
		 * Access the selected flag for this entry.
		 * @return true if selected, false if not.
		 */
		public boolean isSelected()
		{
			return m_selected;
		}

		/**
		 * Setup for the list -> read transition on this entry.
		 * @return navigation outcome: "read" to go to read mode, or null to stay here (due to an error condition).
		 */
		public String processActionListRead()
		{
			m_logger.info(this +".processActionListRead() for: " + getEntry().getId());

			m_entry = this;

			// done with the entry list
			m_entries.clear();

			return "read";
		}

		/**
		 * Set the seleced flag for this entry.
		 * @param b The selected flag.
		 */
		public void setSelected(boolean b)
		{
			m_selected = b;
		}

		/**
		 * Set the just created flag.
		 * @param b Te
		 */
		public void setJustCreated(boolean b)
		{
			m_justCreated = b;
		}
	}

	/** Dependency: The CrudService. */
	protected CrudService m_crudService = null;

	/** Instruction message indicating edit mode: new or edit. */
	protected String m_editInstructionMsg = null;

	/** Toolbar message indicating edit mode: new or edit. */
	protected String m_editToolbarMsg = null;

	/** A list of entries to be displayed, cached and decorated from the service */
	protected List m_entries = new Vector();

	/** A single selected entry, taken and decorated from the service */
	protected DecoratedCrudObject m_entry = null;

	/** Dependency:  The logging service. */
	protected Logger m_logger = null;

	/**
	 * no-arg constructor.
	 */
	public CrudTool()
	{
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
	 * Access the list of entries, refreshed if needed.
	 * @return The current list of entries.
	 */
	public List getEntries()
	{
		if (m_entries.isEmpty())
		{
			m_logger.info(this +".refreshing entries");

			try
			{
				// get "all" the entries
				List entries = m_crudService.findByName("");

				// wrap them and keep them
				for (Iterator i = entries.iterator(); i.hasNext();)
				{
					CrudObject co = (CrudObject) i.next();
					DecoratedCrudObject dco = new DecoratedCrudObject(co);
					m_entries.add(dco);
				}
			}
			catch (PermissionException e)
			{
				m_logger.info(this +".refreshEntries: " + e);
				FacesContext.getCurrentInstance().addMessage(
					null,
					MessageFactory.getMessage(
						FacesContext.getCurrentInstance(),
						"error_permission",
						(new Object[] { e.toString()})));
			}
		}

		return m_entries;
	}

	/**
	 * Get the single selected entry.
	 * @return The single selected entry.
	 */
	public DecoratedCrudObject getEntry()
	{
		return m_entry;
	}

	/**
	 * Get a list of those entries from the getEntries() list that are marked as selected.
	 * @return A list of selected entries.
	 */
	public List getSelectedEntries()
	{
		List rv = new Vector();

		// if there's a single entry and it's selected, use just that
		if ((m_entry != null) && (m_entry.isSelected()))
		{
			rv.add(m_entry);
		}

		// otherwise use the selected ones from the entrys
		else
		{
			for (Iterator i = m_entries.iterator(); i.hasNext();)
			{
				DecoratedCrudObject dco = (DecoratedCrudObject) i.next();
				if (dco.isSelected())
				{
					rv.add(dco);
				}
			}
		}

		return rv;
	}

	/**
	 * Setup for the delete -(cancel)-> list transition.
	 * @return navigation outcome: "main" to go to list mode, or null to stay here (due to an error condition).
	 */
	public String processActionDeleteCancel()
	{
		m_logger.info(this +".processActionDeleteCancel()");

		// done with the entries
		m_entries.clear();
		m_entry = null;

		return "main";
	}

	/**
	 * Setup for the delete -(cancel)-> list transition.
	 * @return navigation outcome: "main" to go to list mode, or null to stay here (due to an error condition).
	 */
	public String processActionDeleteDelete()
	{
		m_logger.info(this +".processActionDeleteDelete()");

		List selected = getSelectedEntries();
		for (Iterator i = selected.iterator(); i.hasNext();)
		{
			DecoratedCrudObject dco = (DecoratedCrudObject) i.next();
			try
			{
				m_crudService.delete(dco.getEntry().getId());
			}
			catch (PermissionException e)
			{
				m_logger.info(this +".processActionDeleteDelete: " + e);
				FacesContext.getCurrentInstance().addMessage(
					null,
					MessageFactory.getMessage(
						FacesContext.getCurrentInstance(),
						"error_permission",
						(new Object[] { e.toString()})));
			}
			catch (IdUnusedException e)
			{
				m_logger.info(this +".processActionDeleteDelete: " + e);
				FacesContext.getCurrentInstance().addMessage(
					null,
					MessageFactory.getMessage(
						FacesContext.getCurrentInstance(),
						"error_delete_unused",
						(new Object[] { e.getId()})));
			}
		}

		// done with the entries
		m_entries.clear();
		m_entry = null;

		return "main";
	}

	/**
	 * Setup for the edit -(cancel)-> list transition.
	 * @return navigation outcome: "main" to go to list mode, or null to stay here (due to an error condition).
	 */
	public String processActionEditCancel()
	{
		m_logger.info(this +".processActionEditCancel(): entry: " + getEntry());

		// if we had just created this entry, we need to delete it
		if (m_entry.isJustCreated())
		{
			try
			{
				m_crudService.delete(m_entry.getEntry().getId());
			}
			catch (IdUnusedException e)
			{
			}
			catch (PermissionException e)
			{
			}
		}

		// done with the entries
		m_entries.clear();
		m_entry = null;

		return "main";
	}

	/**
	 * Setup for the edit -(save)-> list transition.
	 * @return navigation outcome: "main" to go to list mode, or null to stay here (due to an error condition).
	 */
	public String processActionEditSave()
	{
		m_logger.info(this +".processActionEditSave() entry: " + getEntry());

		try
		{
			m_crudService.update(getEntry().getEntry());

			// done with the entries
			m_entries.clear();
			m_entry = null;

			return "main";
		}
		catch (PermissionException e)
		{
			m_logger.info(this +".processActionEditSave: " + e);
			FacesContext.getCurrentInstance().addMessage(
				null,
				MessageFactory.getMessage(
					FacesContext.getCurrentInstance(),
					"error_permission",
					(new Object[] { e.toString()})));
		}
		catch (VersionException e)
		{
			m_logger.info(this +".processActionEditSave: " + e);
			FacesContext.getCurrentInstance().addMessage(
				null,
				MessageFactory.getMessage(FacesContext.getCurrentInstance(), "error_version", (new Object[] { e.getId()})));
		}

		return null;
	}

	/**
	 * Setup for the list -> delete transition: make sure we have something selected to delete.
	 * @return navigation outcome: "delete" to go to delete mode, or null to stay here (due to an error condition).
	 */
	public String processActionListDelete()
	{
		m_logger.info(this +".processActionListDelete()");

		// make sure something is selected
		List selected = getSelectedEntries();
		if (selected.isEmpty())
		{
			FacesContext.getCurrentInstance().addMessage(
				null,
				MessageFactory.getMessage(FacesContext.getCurrentInstance(), "error_delete_select", null));

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
		m_logger.info(this +".processActionListEdit()");

		// make sure only one thing is selected
		List selected = getSelectedEntries();
		if (selected.size() != 1)
		{
			FacesContext.getCurrentInstance().addMessage(
				null,
				MessageFactory.getMessage(FacesContext.getCurrentInstance(), "error_edit_select", null));

			return null;
		}

		// setup for edit mode
		m_entry = (DecoratedCrudObject) selected.get(0);

		// done with the entries
		m_entries.clear();

		// set the edit toolbar message for "edit" instead of "new"
		m_editToolbarMsg = MessageFactory.getMessage(FacesContext.getCurrentInstance(), "toolbar_edit", null).getDetail();

		// set the edit instruction message for "edit" instead of "new"
		m_editInstructionMsg =
			MessageFactory.getMessage(FacesContext.getCurrentInstance(), "instruction_edit", null).getDetail();

		return "edit";
	}

	/**
	 * Setup for the list -> new transition: ready the new entry for editing.
	 * @return navigation outcome: "edit" to go to edit (new) mode, or null to stay here (due to an error condition).
	 */
	public String processActionListNew()
	{
		m_logger.info(this +".processActionListNew()");

		// make a new entry and lock it as an edit
		try
		{
			// create the object
			CrudObject co = m_crudService.create();

			// wrap it and keep it as the one selected
			m_entry = new DecoratedCrudObject(co);
			m_entry.setJustCreated(true);

			// done with the entries
			m_entries.clear();

			// set the edit toolbar message for "new" instead of "edit"
			m_editToolbarMsg =
				MessageFactory.getMessage(FacesContext.getCurrentInstance(), "toolbar_new", null).getDetail();

			// set the edit instruction message for "new" instead of "edit"
			m_editInstructionMsg =
				MessageFactory.getMessage(FacesContext.getCurrentInstance(), "instruction_new", null).getDetail();

			// make the transition
			return "edit";
		}
		catch (PermissionException e)
		{
			m_logger.info(this +".processActionListNew: " + e);
			FacesContext.getCurrentInstance().addMessage(
				null,
				MessageFactory.getMessage(
					FacesContext.getCurrentInstance(),
					"error_permission",
					(new Object[] { e.toString()})));

			return null;
		}
	}

	/**
	 * Setup for the read -> list transition.
	 * @return navigation outcome: "main" to go to list mode, or null to stay here (due to an error condition).
	 */
	public String processActionReadBack()
	{
		m_logger.info(this +".processActionReadBack()");

		// done with the entries
		m_entries.clear();
		m_entry = null;

		return "main";
	}

	/**
	 * Setup for the read -> delete transition: set the entry to be selected.
	 * @return navigation outcome: "delete" to go to delete mode, or null to stay here (due to an error condition).
	 */
	public String processActionReadDelete()
	{
		m_logger.info(this +".processActionReadDelete()");

		// mark the one being read as selected
		m_entry.setSelected(true);

		// set a delete outcome
		return "delete";
	}

	/**
	 * Setup for the read -> edit transition.
	 * @return navigation outcome: "edit" to go to edit mode, or null to stay here (due to an error condition).
	 */
	public String processActionReadEdit()
	{
		m_logger.info(this +".processActionReadEdit()");

		// mark the one being read as selected
		m_entry.setSelected(true);

		// set the edit toolbar message for "edit" instead of "new"
		m_editToolbarMsg = MessageFactory.getMessage(FacesContext.getCurrentInstance(), "toolbar_edit", null).getDetail();

		// set the edit instruction message for "edit" instead of "new"
		m_editInstructionMsg =
			MessageFactory.getMessage(FacesContext.getCurrentInstance(), "instruction_edit", null).getDetail();

		return "edit";
	}

	/**
	 * Setup for the read -> new transition: ready the new entry for editing.
	 * @return navigation outcome: "edit" to go to edit (new) mode, or null to stay here (due to an error condition).
	 */
	public String processActionReadNew()
	{
		m_logger.info(this +".processActionReadNew()");

		// done with the entry
		m_entry = null;

		return processActionListNew();
	}

	/**
	 * Dependency: the CrudService.
	 * @param service The CrudService.
	 */
	public void setCrudService(CrudService service)
	{
		m_crudService = service;
	}

	/**
	 * Dependency: the Logger.
	 * @param logger The logger.
	 */
	public void setLogger(Logger logger)
	{
		m_logger = logger;
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/crud/src/java/org/sakaiproject/tool/crud/CrudTool.java,v 1.6 2004/11/10 02:48:03 janderse.umich.edu Exp $
*
**********************************************************************************/
