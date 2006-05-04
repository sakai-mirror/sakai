/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/trunk/sakai/legacy/tool-support/src/java/org/sakaiproject/cheftool/PagedResourceAction.java $
* $Id: PagedResourceAction.java 2371 2005-10-06 03:53:19Z ggolden@umich.edu $
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
package org.sakaiproject.cheftool;

// imports
import java.util.List;
import java.util.Vector;
import java.util.ResourceBundle;

import org.sakaiproject.cheftool.menu.Menu;
import org.sakaiproject.cheftool.menu.MenuDivider;
import org.sakaiproject.cheftool.menu.MenuEntry;
import org.sakaiproject.cheftool.menu.MenuField;
import org.sakaiproject.cheftool.menu.MenuItem;
import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.util.ParameterParser;
import org.sakaiproject.util.java.StringUtil;
import org.sakaiproject.util.courier.ObservingCourier;

/**
* <p>PagedResourceAction is a base class that handles paged display of lists of Resourecs.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 2371 $
*/
public abstract class PagedResourceHelperAction
	extends VelocityPortletPaneledAction
{
	
	protected static ResourceBundle rb = ResourceBundle.getBundle("pagedresourceaction");
	
	/** The default number of messages per page. */
	protected static final int DEFAULT_PAGE_SIZE = 10;

	/** portlet configuration parameter names. */
	protected static final String PARAM_PAGESIZE = "pagesize";

	/** state attribute names. */
	protected static final String STATE_VIEW_ID = "view-id";
	protected static final String STATE_TOP_PAGE_MESSAGE = "msg-top";
	protected static final String STATE_PAGESIZE = "page-size";
	protected static final String STATE_NUM_MESSAGES = "num-messages";
	protected static final String STATE_NEXT_PAGE_EXISTS = "msg-next-page";
	protected static final String STATE_PREV_PAGE_EXISTS = "msg-prev-page";
	protected static final String STATE_GO_NEXT_PAGE = "msg-go-next-page";
	protected static final String STATE_GO_PREV_PAGE = "msg-go-prev-page";
	protected static final String STATE_GO_NEXT = "msg-go-next";
	protected static final String STATE_GO_PREV = "msg-go-prev";
	protected static final String STATE_NEXT_EXISTS = "msg-next";
	protected static final String STATE_PREV_EXISTS = "msg-prev";
	protected static final String STATE_GO_FIRST_PAGE = "msg-go-first-page";
	protected static final String STATE_GO_LAST_PAGE = "msg-go-last-page";
	protected static final String STATE_SEARCH = "search";
	protected static final String STATE_MANUAL_REFRESH = "manual";

	/** Form fields. */
	protected static final String FORM_SEARCH = "search";

	/**
	* Override this to return a list of all the resources that there are to page.
	* Sort them as appropriate, and apply search criteria.
	*/
	protected static List readAllResources(SessionState state)
	{
		List allResources = new Vector();
		
		return allResources;
	}

	/**
	* Populate the state object, if needed, concerning paging
	*/
	protected void initState(SessionState state, VelocityPortlet portlet, JetspeedRunData rundata)
	{
		super.initState(state, portlet, rundata);

		if (state.getAttribute(STATE_PAGESIZE) == null)
		{
			state.setAttribute(STATE_PAGESIZE, new Integer(DEFAULT_PAGE_SIZE));
			PortletConfig config = portlet.getPortletConfig();

			try
			{
				Integer size = new Integer(config.getInitParameter(PARAM_PAGESIZE));
				if (size.intValue() <= 0)
				{
					size = new Integer(DEFAULT_PAGE_SIZE);
					if (Log.getLogger("chef").isDebugEnabled())
						Log.debug("chef", this + ".initState: size parameter invalid: "
									+ config.getInitParameter(PARAM_PAGESIZE));
				}
				state.setAttribute(STATE_PAGESIZE, size);
			}
			catch (Exception any)
			{
				if (Log.getLogger("chef").isDebugEnabled())
					Log.debug("chef", this + ".initState: size parameter invalid: "
								+ any.toString());
				state.setAttribute(STATE_PAGESIZE, new Integer(DEFAULT_PAGE_SIZE));
			}
		}

	}   // initState

	/** 
	* Add the menus for a view mode for paging.
	*/
	protected static void addViewPagingMenus(Menu bar, SessionState state)
	{
		bar.add( new MenuEntry(rb.getString("viepag.prev"), (state.getAttribute(STATE_PREV_EXISTS) != null), "doView_prev"));
		bar.add( new MenuEntry(rb.getString("viepag.next"), (state.getAttribute(STATE_NEXT_EXISTS) != null), "doView_next"));

	}   // addViewPagingMenus

	/** 
	* Add the menus for a list mode for paging.
	*/
	protected static void addListPagingMenus(Menu bar, SessionState state)
	{
		bar.add( new MenuEntry("First Page", (state.getAttribute(STATE_PREV_PAGE_EXISTS) != null),
					"doList_first"));
		bar.add( new MenuEntry("Previous Page", (state.getAttribute(STATE_PREV_PAGE_EXISTS) != null),
					"doList_prev"));
		bar.add( new MenuEntry("Next Page", (state.getAttribute(STATE_NEXT_PAGE_EXISTS) != null),
					"doList_next"));
		bar.add( new MenuEntry("Last Page", (state.getAttribute(STATE_NEXT_PAGE_EXISTS) != null),
					"doList_last"));

	}   // addListPagingMenus

	/** 
	* Add the menus for search.
	*/
	protected static void addSearchMenus(Menu bar, SessionState state)
	{
		bar.add( new MenuDivider());
		bar.add( new MenuField(FORM_SEARCH, "toolbar", "doSearch", (String) state.getAttribute(STATE_SEARCH)));
		bar.add( new MenuEntry(rb.getString("sea.sea"), null, true, MenuItem.CHECKED_NA, "doSearch", "toolbar"));
		if (state.getAttribute(STATE_SEARCH) != null)
		{
			bar.add( new MenuEntry(rb.getString("sea.cleasea"), "doSearch_clear"));
		}

	}   // addSearchMenus

	/**
	* Add the menus for manual / auto - refresh.
	*/
	protected static void addRefreshMenus(Menu bar, SessionState state)
	{
		// only offer if there's an observer
		ObservingCourier observer = (ObservingCourier) state.getAttribute(STATE_OBSERVER);
		if (observer == null) return;

		bar.add( new MenuDivider());
		bar.add( new MenuEntry((observer.getEnabled() ? rb.getString("ref.manref") : rb.getString("ref.autoref")), "doAuto"));
		if (!observer.getEnabled())
		{
			bar.add( new MenuEntry(rb.getString("ref.refresh"), "doRefresh"));		
		}
		
	}	// addRefreshMenus

	/**
	* Prepare the current page of messages to display.
	* @return List of MailArchiveMessage to display on this page.
	*/
	protected static List prepPage(SessionState state)
	{
		List rv = new Vector();

		// access the page size
		int pageSize = ((Integer) state.getAttribute(STATE_PAGESIZE)).intValue();

		// cleanup prior prep
		state.removeAttribute(STATE_NUM_MESSAGES);

		// are we going next or prev, first or last page?
		boolean goNextPage = state.getAttribute(STATE_GO_NEXT_PAGE) != null;
		boolean goPrevPage = state.getAttribute(STATE_GO_PREV_PAGE) != null;
		boolean goFirstPage = state.getAttribute(STATE_GO_FIRST_PAGE) != null;
		boolean goLastPage = state.getAttribute(STATE_GO_LAST_PAGE) != null;
		state.removeAttribute(STATE_GO_NEXT_PAGE);
		state.removeAttribute(STATE_GO_PREV_PAGE);
		state.removeAttribute(STATE_GO_FIRST_PAGE);
		state.removeAttribute(STATE_GO_LAST_PAGE);

		// are we going next or prev message?
		boolean goNext = state.getAttribute(STATE_GO_NEXT) != null;
		boolean goPrev = state.getAttribute(STATE_GO_PREV) != null;
		state.removeAttribute(STATE_GO_NEXT);
		state.removeAttribute(STATE_GO_PREV);

		// read all channel messages
		List allMessages = readAllResources(state);

		if (allMessages == null)
		{
			return rv;
		}

		// if we have no prev page and do have a top message, then we will stay "pined" to the top
		boolean pinToTop = (	(state.getAttribute(STATE_TOP_PAGE_MESSAGE) != null)
							&&	(state.getAttribute(STATE_PREV_PAGE_EXISTS) == null)
							&&	!goNextPage && !goPrevPage && !goNext && !goPrev && !goFirstPage && !goLastPage);

		// if we have no next page and do have a top message, then we will stay "pined" to the bottom
		boolean pinToBottom = (	(state.getAttribute(STATE_TOP_PAGE_MESSAGE) != null)
							&&	(state.getAttribute(STATE_NEXT_PAGE_EXISTS) == null)
							&&	!goNextPage && !goPrevPage && !goNext && !goPrev && !goFirstPage && !goLastPage);

		// how many messages, total
		int numMessages = allMessages.size();

		if (numMessages == 0)
		{
			return rv;
		}

		// save the number of messges
		state.setAttribute(STATE_NUM_MESSAGES, new Integer(numMessages));

		// find the position of the message that is the top first on the page
		int posStart = 0;
		String messageIdAtTheTopOfThePage = (String) state.getAttribute(STATE_TOP_PAGE_MESSAGE);
		if (messageIdAtTheTopOfThePage != null)
		{
			// find the next page
			posStart = findResourceInList(allMessages, messageIdAtTheTopOfThePage);

			// if missing, start at the top
			if (posStart == -1)
			{
				posStart = 0;
			}
		}

		// if going to the next page, adjust
		if (goNextPage)
		{
			posStart += pageSize;
		}

		// if going to the prev page, adjust
		else if (goPrevPage)
		{
			posStart -= pageSize;
			if (posStart < 0) posStart = 0;
		}
		
		// if going to the first page, adjust
		else if (goFirstPage)
		{
			posStart = 0;
		}
		
		// if going to the last page, adjust
		else if (goLastPage)
		{
			posStart = numMessages - pageSize;
			if (posStart < 0) posStart = 0;
		}

		// pinning
		if (pinToTop)
		{
			posStart = 0;
		}
		else if (pinToBottom)
		{
			posStart = numMessages - pageSize;
			if (posStart < 0) posStart = 0;
		}

		// get the last page fully displayed
		if (posStart + pageSize > numMessages)
		{
			posStart = numMessages - pageSize;
			if (posStart < 0) posStart = 0;
		}

		// compute the end to a page size, adjusted for the number of messages available
		int posEnd = posStart + (pageSize-1);
		if (posEnd >= numMessages) posEnd = numMessages-1;
		int numMessagesOnThisPage = (posEnd - posStart) + 1;

		// select the messages on this page
		for (int i = posStart; i <= posEnd; i++)
		{
			rv.add(allMessages.get(i));
		}

		// save which message is at the top of the page
		Entity messageAtTheTopOfThePage = (Entity) allMessages.get(posStart);
		state.setAttribute(STATE_TOP_PAGE_MESSAGE, messageAtTheTopOfThePage.getId());

		// which message starts the next page (if any)
		int next = posStart + pageSize;
		if (next < numMessages)
		{
			state.setAttribute(STATE_NEXT_PAGE_EXISTS, "");
		}
		else
		{
			state.removeAttribute(STATE_NEXT_PAGE_EXISTS);
		}

		// which message ends the prior page (if any)
		int prev = posStart - 1;
		if (prev >= 0)
		{
			state.setAttribute(STATE_PREV_PAGE_EXISTS, "");
		}
		else
		{
			state.removeAttribute(STATE_PREV_PAGE_EXISTS);
		}

		if (state.getAttribute(STATE_VIEW_ID) != null)
		{
			int viewPos = findResourceInList(allMessages, (String) state.getAttribute(STATE_VIEW_ID));
	
			// are we moving to the next message
			if (goNext)
			{
				// advance
				viewPos++;
				if (viewPos >= numMessages) viewPos = numMessages-1;
			}
	
			// are we moving to the prev message
			if (goPrev)
			{
				// retreat
				viewPos--;
				if (viewPos < 0) viewPos = 0;
			}
			
			// update the view message
			state.setAttribute(STATE_VIEW_ID, ((Entity) allMessages.get(viewPos)).getId());
			
			// if the view message is no longer on the current page, adjust the page
			// Note: next time through this will get processed
			if (viewPos < posStart)
			{
				state.setAttribute(STATE_GO_PREV_PAGE, "");
			}
			else if (viewPos > posEnd)
			{
				state.setAttribute(STATE_GO_NEXT_PAGE, "");
			}
			
			if (viewPos > 0)
			{
				state.setAttribute(STATE_PREV_EXISTS,"");
			}
			else
			{
				state.removeAttribute(STATE_PREV_EXISTS);
			}
			
			if (viewPos < numMessages-1)
			{
				state.setAttribute(STATE_NEXT_EXISTS,"");
			}
			else
			{
				state.removeAttribute(STATE_NEXT_EXISTS);
			}			
		}

		return rv;

	}	// prepPage

	/** 
	* Handle a next-message (view) request.
	**/
	public static void doView_next(RunData runData)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState state = ((JetspeedRunData)runData).getPortletSessionState(peid);

		// set the flag to go to the next message on the next view
		state.setAttribute(STATE_GO_NEXT,"");

	}	// doView_next

	/** 
	* Handle a first-message page (list) request.
	**/
	public static void doList_first(RunData runData)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState state = ((JetspeedRunData)runData).getPortletSessionState(peid);

		// set the flag to go to the next message on the next view
		state.setAttribute(STATE_GO_FIRST_PAGE,"");

	}	// doList_first

	/**
	* Handle a last-message page (list) request.
	**/
	public static void doList_last(RunData runData)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState state = ((JetspeedRunData)runData).getPortletSessionState(peid);

		// set the flag to go to the next message on the next view
		state.setAttribute(STATE_GO_LAST_PAGE,"");

	}	// doList_last

	/** 
	* Handle a next-page (list) request.
	**/	
	public static  void doList_next(RunData runData)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState state = ((JetspeedRunData)runData).getPortletSessionState(peid);

		// set the flag to go to the next page on the next list
		state.setAttribute(STATE_GO_NEXT_PAGE,"");

		// %%% ?? doList(runData, context);

	}	// doList_next
	
	/** 
	* Handle a request to change the page-size.
	**/	
	public static void doChange_pagesize(RunData runData)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState state = ((JetspeedRunData)runData).getPortletSessionState(peid);
		ParameterParser params = runData.getParameters();
		
		String pagesize = params.getString("selectPageSize");
		Integer size = Integer.valueOf(pagesize);

		state.setAttribute(STATE_PAGESIZE, size);
		
	}

	/** 
	* Handle a prev-message (view) request.
	**/	
	public static void doView_prev(RunData runData)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState state = ((JetspeedRunData)runData).getPortletSessionState(peid);

		// set the flag to go to the prev message on the next view
		state.setAttribute(STATE_GO_PREV,"");

	}	// doView_prev

	/** 
	* Handle a prev-page (list) request.
	**/	
	public static void doList_prev(RunData runData)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState state = ((JetspeedRunData)runData).getPortletSessionState(peid);

		// set the flag to go to the prev page on the next list
		state.setAttribute(STATE_GO_PREV_PAGE,"");

	}	// doList_prev

	/** 
	* Handle a Search request.
	**/	
	public static void doSearch(RunData runData)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState state = ((JetspeedRunData)runData).getPortletSessionState(peid);

		// read the search form field into the state object
		String search = StringUtil.trimToNull(runData.getParameters().getString(FORM_SEARCH));

		// set the flag to go to the prev page on the next list
		if (search == null)
		{
			state.removeAttribute(STATE_SEARCH);
		}
		else
		{
			state.setAttribute(STATE_SEARCH, search);
		}

		// start paging again from the top of the list
		resetPaging(state);

		// if we are searching, turn off auto refresh
		if (search != null)
		{
			ObservingCourier observer = (ObservingCourier) state.getAttribute(STATE_OBSERVER);
			if (observer != null)
			{
				observer.disable();
			}
		}
		
		// else turn it back on
		else
		{
			enableObserver(state);
		}

	}	// doSearch

	/** 
	* Handle a Search Clear request.
	**/	
	public static void doSearch_clear(RunData runData)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState state = ((JetspeedRunData)runData).getPortletSessionState(peid);

		// clear the search
		state.removeAttribute(STATE_SEARCH);

		// start paging again from the top of the list
		resetPaging(state);

		// turn on auto refresh
		enableObserver(state);

	}	// doSearch_clear

	/**
	* Reset to the first page
	*/
	protected static void resetPaging(SessionState state)
	{
		// we are changing the sort, so start from the first page again
		state.removeAttribute(STATE_TOP_PAGE_MESSAGE);

	}	// resetPaging

	/**
	* Find the resource with this id in the list.
	* @param messages The list of messages.
	* @param id The message id.
	* @return The index position in the list of the message with this id, or -1 if not found.
	*/
	protected static int findResourceInList(List resources, String id)
	{
		for (int i = 0; i < resources.size(); i++)
		{
			// if this is the one, return this index
			if (((Entity) (resources.get(i))).getId().equals(id)) return i;
		}

		// not found
		return -1;

	}	// findResourceInList

	/**
	* Toggle auto-update
	*/
	public static void doAuto(RunData data, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(peid);

		// get the observer
		ObservingCourier observer = (ObservingCourier) state.getAttribute(STATE_OBSERVER);
		if (observer != null)
		{
			boolean enabled = observer.getEnabled();
			if (enabled)
			{
				observer.disable();
				state.setAttribute(STATE_MANUAL_REFRESH,"manual");
			}
			else
			{
				observer.enable();
				state.removeAttribute(STATE_MANUAL_REFRESH);
			}
		}

	}	// doAuto

	/**
	* The action for when the user want's an update
	*/
	public static void doRefresh(RunData data, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(peid);

	}	// doRefresh

	/**
	* Enable the observer, unless we are in search mode, where we want it disabled.
	*/
	public static void enableObserver(SessionState state)
	{
		// get the observer
		ObservingCourier observer = (ObservingCourier) state.getAttribute(STATE_OBSERVER);
		if (observer != null)
		{
			// we leave it disabled if we are searching, or if the user has last selected to be manual
			if (	(state.getAttribute(STATE_SEARCH) != null)
				||	(state.getAttribute(STATE_MANUAL_REFRESH) != null))
			{
				observer.disable();
			}
			else
			{
				observer.enable();
			}
		}

	}	// enableObserver

}   // PagedResourceAction


