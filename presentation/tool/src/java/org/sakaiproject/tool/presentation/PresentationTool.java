/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
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

package org.sakaiproject.tool.presentation;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.osid.id.IdManager;
import org.osid.shared.Id;
import org.sakaiproject.api.app.presentation.Presentation;
import org.sakaiproject.api.app.presentation.PresentationManager;
import org.sakaiproject.api.app.presentation.Slide;
import org.sakaiproject.api.kernel.tool.Placement;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.util.courier.EventObservingCourier;
import org.sakaiproject.util.courier.ObservingCourier;

import com.sun.faces.util.MessageFactory;

/**
 * <p>
 * Presentation Tool
 * </p>
 * 
 * @author Mark Norton
 * @version $Revision$
 */
public class PresentationTool
{
	/** Service Dependency: IdManager. */
	protected IdManager idMgr = null;

	/** Service Dependency: Logger - eventually this will be the Sakai logger. */
	protected Logger logger = null;

	/** Service Dependency: Presentation Service. */
	protected PresentationManager prMgr = null;

	/** Private properties. */
	protected Id id = null; // Id test field. No longer used.

	/** A list (PresentationView) of presentations to be displayed. */
	protected List presentations = null;

	protected Presentation currentPresentation = null;

	protected List m_presentations = new Vector();

	/** These flags indicate our relationship to the show or slides. */
	protected String currentMode = "main";

	protected int currentSlidePos = 0; // Local current slide if we are viewing

	/** Determines if this user has access only or has write ability */
	protected boolean allowShow = false;

	/** Error message, etc.. */
	String m_instruction_message = null;

	/** Courier for update * */
	ObservingCourier m_observer = null;

	/**
	 * noarg constructor, required for managed beans. Note: You are NOT service injected at this point.
	 */
	public PresentationTool()
	{

	}

	public String getInstructionMessage()
	{

		return m_instruction_message;
	}

	public void setInstructionMessage(String message)
	{
		m_instruction_message = message;
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

	private String getMsgFromBundle(String val)
	{
		return getLocalizedMessage(val, null).getDetail();
	}

	/**
	 * Our local decorated presentation object.
	 * 
	 * @return
	 */
	public class PresentationBean
	{
		Presentation presentation;

		private boolean showing = false;

		// Constructor
		PresentationBean(Presentation pres)
		{
			this.presentation = pres;
			this.showing = prMgr.isShowing(pres);
		}

		// Actions
		public String processActionListView()
		{
			preAction();

			currentPresentation = getPresentation();
			currentSlidePos = 0; // When switching presentations start at zero

			return switchMode("view");
		}

		public String processActionListJoin()
		{
			preAction();

			currentPresentation = getPresentation();
			currentSlidePos = 0; // When switching presentations start at zero

			return switchMode("join");
		}

		public String processActionListShow()
		{
			preAction();

			currentPresentation = getPresentation();
			currentSlidePos = 0; // When switching presentations start at zero
			String msg = "";

			if (prMgr.startShow(currentPresentation))
			{
				return switchMode("show");
			}
			else
			{
				String newMode = processActionExit();
				msg = getMsgFromBundle("pt_presentation_noStart");
				setInstructionMessage(msg);
				// setInstructionMessage("Unable to start show");
				return switchMode(newMode);
			}
		}

		// Setters and getters
		public Presentation getPresentation()
		{
			return presentation;
		}

		public boolean getShowing()
		{
			// System.out.println("PresentationBean.getShowing() = "+showing);
			return showing;
		}

		public String getTitle()
		{
			return this.presentation.getTitle();
		}

		public String getModificationDate()
		{
			return this.presentation.getModificationDate();
		}

		public int getSlideCount()
		{
			return this.presentation.getSlideCount();
		}
	}

	/**
	 * Set the decorated presentation list to be decorated objects of this list
	 * 
	 * @param list
	 *        The set of Presentations for the list.
	 */
	protected void setPresentations(List list)
	{
		// clear the list
		m_presentations.clear();

		if (list == null) return;

		// process each new entry
		for (Iterator iPres = list.iterator(); iPres.hasNext();)
		{
			Object o = iPres.next();
			if (o instanceof Presentation)
			{
				m_presentations.add(new PresentationBean((Presentation) o));
			}
		}
	}

	protected List loadPresentations()
	{
		List retval = null;
		String msg = "";

		if (this.prMgr == null) return null;

		try
		{
			retval = this.prMgr.getPresentations();
		}
		catch (IdUnusedException e)
		{
			msg = getMsgFromBundle("pt_folder_noExist");
			setInstructionMessage(msg);
			// setInstructionMessage("Presentations folder does not exist in Resources");
			return null;
		}
		catch (TypeException e)
		{
			msg = getMsgFromBundle("pt_folder_typeException");
			setInstructionMessage(msg);
			// setInstructionMessage("Access to Presentations folder failed with a TypeException");
			return null;
		}
		catch (PermissionException e)
		{
			msg = getMsgFromBundle("pt_folder_noPermission");
			setInstructionMessage(msg);
			// setInstructionMessage("You do not have permission on the Presentations folder");
			// System.out.println("PermissionException");
		}
		return retval;
	}

	/**
	 * Refresh the list of presentations.
	 */
	protected void refreshPresentations()
	{
		// System.out.println("PresentationTool.refreshPresentations()");
		if (presentations == null)
		{
			presentations = loadPresentations();
			if (presentations != null)
			{
				if (presentations.size() > 0)
				{
					Presentation pres = (Presentation) presentations.get(0);
					allowShow = prMgr.allowUpdate(pres);
				}
			}
		}
		// System.out.println("PresentationTool.refreshPresentations() ok to show="+allowShow);

		// use this to populate our decorated list
		setPresentations(presentations);
	}

	/**
	 * Return an EventsObservingCourier for the particular reference area.
	 * 
	 * @param pattern
	 */
	protected ObservingCourier makeResourceCourier(String pattern)
	{
		// get the current tool placement
		Placement placement = ToolManager.getCurrentPlacement();

		// location is just placement
		String location = placement.getId();

		// the html element to update on delivery (all of me!)
		String elementId = null;

		EventObservingCourier observer = new EventObservingCourier(location, elementId, pattern);
		return observer;
	}

	/**
	 * Return the IdManager service.
	 */
	public IdManager getIdMgr()
	{
		return this.idMgr;
	}

	/**
	 * Set the IdManager service.
	 */
	public void setIdMgr(IdManager mgr)
	{
		// System.out.println("Presentation Tool Injected Id manager " + mgr);
		this.idMgr = mgr;
	}

	/**
	 * Return the PresentationManager service.
	 */
	public PresentationManager getPrMgr()
	{
		return this.prMgr;
	}

	/**
	 * Set the PrsentationManager service.
	 */
	public void setPrMgr(PresentationManager mgr)
	{
		// System.out.println("Presentation Tool Injected presentation manager " + mgr);
		this.prMgr = mgr;
	}

	/**
	 * Return the list of presentations (decorated). Also check to see if the user is allowed to update the content area so the proper buttons are made available.
	 */
	public List getPresentations()
	{
		// System.out.println("PresentationTool.getPresentations()");

		refreshPresentations();

		return m_presentations;
	}

	/**
	 * Get the current slide.
	 * 
	 * @return curent Slide
	 */
	public Slide getSlide()
	{
		String msg = "";

		if (currentPresentation == null)
		{
			msg = getMsgFromBundle("pt_presentation_noPresentation");
			setInstructionMessage(msg);
			// setInstructionMessage("There is no presentation currently showing.");

			msg = getMsgFromBundle("pt_log_getSlide_noPresentation");
			logInfo(msg);
			return null;
		}
		if (currentMode.equals("view"))
		{
			int maxSlide = currentPresentation.getSlideCount();
			if (currentSlidePos > (maxSlide - 1)) currentSlidePos = maxSlide - 1;
			if (currentSlidePos < 0) currentSlidePos = 0;
			return currentPresentation.getSlide(currentSlidePos);
		}
		else
		{ // For join and show
			return prMgr.getCurrentSlide(currentPresentation);
		}
	}

	/**
	 * Determine whether or not the current presentation is showing.
	 * 
	 * @return True is the current presentation is showing.
	 */
	public boolean getIsShowing()
	{
		if (currentPresentation == null) return false;
		boolean retval = prMgr.isShowing(currentPresentation);
		// System.out.println("getIsShowing()="+retval);
		return retval;
	}

	public boolean getAllowShow()
	{
		if (currentPresentation != null)
		{
			allowShow = prMgr.allowUpdate(currentPresentation);
			// System.out.println("PresentationTool.getAllowShow(current)="+allowShow);
		}
		else
		{ // Refresh the presentations - will check and set access status
			refreshPresentations();
		}
		// System.out.println("PresentationTool.getAllowShow(current)="+allowShow);

		return this.allowShow;
	}

	public int getSlideCount()
	{
		if (currentPresentation == null) return 0;
		return currentPresentation.getSlideCount();
	}

	/**
	 * Return a string indicating the current show position as an example: "1 of 5" the expectation is that there will be a message like Viewing: 1 of 5 Viewing: No current presentation
	 * 
	 * @return Position Insdicator String
	 */
	public String getShowPosition()
	{
		String retval = "";
		String msg = "";
		if (currentPresentation == null)
		{
			msg = getMsgFromBundle("pt_presentation_noPresentation");
			setInstructionMessage(msg);
			// setInstructionMessage("There is no presentation currently showing.");

			msg = getMsgFromBundle("pt_log_getSlide_noPresentation");
			logInfo(msg);
			// logInfo("getSlide() called with no current presentation");

			msg = getMsgFromBundle("pt_presentation_noCurrent");
			return msg;
		}

		// Since we are being called at the beginning of nearly every render,
		// we indicate that any pending events can be discarded.
		// TODO: At some point, this should be placed somewhere that it is not a
		// side-effect but instead an explicit call
		if (m_observer != null)
		{
			m_observer.justDelivered();
		}

		int maxSlide = currentPresentation.getSlideCount();
		if (currentMode.equals("view"))
		{
			if (currentSlidePos > (maxSlide - 1)) currentSlidePos = maxSlide - 1;
			if (currentSlidePos < 0) currentSlidePos = 0;
			retval = retval + (currentSlidePos + 1);
		}
		else
		{ // For join and show
			int tmpSlide = prMgr.getCurrentSlideNumber(currentPresentation);
			retval = retval + (tmpSlide + 1);
		}
		retval = retval + " of " + maxSlide;
		return retval;
	}

	/**
	 * Do general setup for action methods.
	 */
	private void preAction()
	{
		setInstructionMessage(null);
	}

	/**
	 * Helper for switching to "join", "view", or "show" modes.
	 * 
	 * @return the navigation outcome.
	 */
	private String switchMode(String newMode)
	{
		String msg = "";
		// System.out.println("switchMode from="+currentMode+" newMode="+newMode);

		// If Switching into join mode with a valid presentation, set up an observer
		// to watch for the changes

		if (newMode.equals("join") && !currentMode.equals("join") && currentPresentation != null)
		{
			// System.out.println("Switching into join mode");
			String pattern = prMgr.getReference(currentPresentation);
			// System.out.println("pattern="+pattern);

			m_observer = makeResourceCourier(pattern);
		}

		// Switch modes
		currentMode = newMode;

		if (!currentMode.equals("join") && m_observer != null)
		{
			// System.out.println("Turning off observer="+m_observer);
			m_observer.disable();
			m_observer = null;
		}

		if (currentPresentation != null) return currentMode;

		if (!newMode.equals("main") && !newMode.equals("help"))
		{
			msg = getMsgFromBundle("pt_presentation_isNull");
			setInstructionMessage(msg);
			// setInstructionMessage("Error - Current presentation is null");

			// TODO: developer handler for dynamic message below.

			logInfo("Presentation tool, no presentation while switching to " + newMode);
			this.prMgr.clearPresentationCache();
			presentations = null;
			currentMode = "main";
		}

		return currentMode;
	}

	/**
	 * Process the "View" action.
	 * 
	 * @return the navigation outcome.
	 */
	public String processActionView()
	{
		preAction();
		return switchMode("view");
	}

	/**
	 * Process the "Join" action.
	 * 
	 * @return the navigation outcome.
	 */
	public String processActionJoin()
	{
		preAction();

		return switchMode("join");
	}

	/**
	 * Process the "Refresh Presentations" action. This causes the presentation list to be reloaded.
	 * 
	 * @return the navigation outcome.
	 */
	public String processActionRefreshPresentations()
	{
		preAction();
		this.prMgr.clearPresentationCache();
		presentations = null;
		refreshPresentations();
		return switchMode("main");
	}

	/**
	 * Handle a refresh view event. This is only needed while watching a presentation.
	 */
	public String processActionRefresh()
	{
		preAction();
		return currentMode;
	}

	/**
	 * Process the "Help" action. This causes the active show to end.
	 * 
	 * @return the navigation outcome.
	 */
	public String processActionHelp()
	{
		preAction();
		return switchMode("help");
	}

	/**
	 * Process the "End" action. This causes the active show to end.
	 * 
	 * @return the navigation outcome.
	 */
	public String processActionEnd()
	{
		preAction();
		String msg = "";
		if (currentPresentation == null)
		{
			msg = getMsgFromBundle("pt_presentation_noLongerShowing");
			setInstructionMessage(msg);
			// setInstructionMessage("Current presentation no longer showing.");
		}
		else if (!prMgr.stopShow(currentPresentation))
		{
			msg = getMsgFromBundle("pt_presentation_stopProblem");
			setInstructionMessage(msg);
			// setInstructionMessage("Problem encountered while stopping presentation.");
		}
		currentPresentation = null;
		this.prMgr.clearPresentationCache();
		presentations = null;
		return switchMode("main");
	}

	/**
	 * Process the "Next" action. This causes the current show to move to the next slide. It will not go past the end of the show.\
	 * 
	 * @return the navigation outcome.
	 */
	public String processActionNext()
	{
		preAction();
		String msg = "";

		if (currentPresentation == null)
		{
			String retval = processActionExit();
			msg = getMsgFromBundle("pt_presentation_noLongerShowing");
			setInstructionMessage(msg);
			// setInstructionMessage("Current presentation no longer showing.");
			return retval;
		}

		if (currentMode.equals("view"))
		{
			currentSlidePos++;
			int maxSlide = currentPresentation.getSlideCount();
			if (currentSlidePos > (maxSlide - 1)) currentSlidePos = maxSlide - 1;
			if (currentSlidePos < 0) currentSlidePos = 0;
		}
		else
		{ // For join and show
			prMgr.advanceShow(currentPresentation);
		}

		return currentMode;
	}

	/**
	 * Process the "Previous" action. This causes the current show to move the current slide to the previous slide. It will not go past the first slide.
	 * 
	 * @return the navigation outcome.
	 */
	public String processActionPrevious()
	{
		preAction();
		String msg = "";
		if (currentPresentation == null)
		{
			String retval = processActionExit();
			msg = getMsgFromBundle("pt_presentation_noLongerShowing");
			setInstructionMessage(msg);
			// setInstructionMessage("Current presentation no longer showing.");
			return retval;
		}

		if (currentMode.equals("view"))
		{
			currentSlidePos--;
			int maxSlide = currentPresentation.getSlideCount();
			if (currentSlidePos > (maxSlide - 1)) currentSlidePos = maxSlide - 1;
			if (currentSlidePos < 0) currentSlidePos = 0;
		}
		else
		{ // For join and show
			prMgr.backShow(currentPresentation);
		}

		return currentMode;
	}

	/**
	 * Process the "Exit" action. Handle the exit even out of the viewer page.
	 * 
	 * @return the navigation outcome.
	 */
	public String processActionExit()
	{
		preAction();
		currentPresentation = null;
		this.prMgr.clearPresentationCache();
		presentations = null;
		return switchMode("main");
	}

	private void logInfo(String message)
	{
		System.out.println("INFO/Presentation Tool:" + message);
	}

}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Footer: $
 *************************************************************************************************************************************************************************************************************************************************************/
