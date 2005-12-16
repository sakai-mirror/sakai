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

// package
package org.sakaiproject.cheftool;

// imports
import java.util.List;
import java.util.Vector;

import org.sakaiproject.util.java.ResourceLoader;
import org.sakaiproject.cheftool.menu.Menu;
import org.sakaiproject.cheftool.menu.MenuDivider;
import org.sakaiproject.cheftool.menu.MenuEntry;
import org.sakaiproject.cheftool.menu.MenuField;
import org.sakaiproject.cheftool.menu.MenuItem;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.util.java.StringUtil;
import org.sakaiproject.util.courier.ObservingCourier;

/**
* <p>PagedResourceAction is a base class that handles paged display of lists of Resourecs with service support for paging.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public abstract class PagedResourceActionII
	extends VelocityPortletPaneledAction
{
	protected static ResourceLoader rb_praII = new ResourceLoader("pagedresourceaction");
   
	/** The default number of messages per page. */
	protected final static int DEFAULT_PAGE_SIZE = 20;

	/** portlet configuration parameter names. */
	protected static final String PARAM_PAGESIZE = "pagesize";

	/** state attribute names. */
	protected static final String STATE_VIEW_ID = "view-id";
	protected static final String STATE_TOP_PAGE_MESSAGE = "msg-top";
	protected static final String STATE_PAGESIZE = "page-size";
	protected static final String STATE_NUM_MESSAGES = "num-messages";
	protected static final String STATE_NEXT_PAGE_EXISTS = "msg-next-page";
	protected static final String STATE_PREV_PAGE_EXISTS = "msg-prev-page";
	protected static final String STATE_FIRST_PAGE_EXISTS = "msg-first-page";
	protected static final String STATE_LAST_PAGE_EXISTS = "msg-last-page";
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
	
	// for navigation to a certain page
	protected static final String STATE_TOTAL_PAGENUMBER = "total_page_number";
	protected static final String STATE_CURRENT_PAGE = "current-page";
	protected static final String FORM_PAGE_NUMBER = "page_number";
	protected static final String STATE_GOTO_PAGE = "goto-page";
	
	protected final static int[] PAGESIZES = {5,10,20,50,100,200};

	/**
	* Implement this to return a list of all the resources in this record range,
	* with search and sorting applied.
	* @param first The first record to include (1 based).
	* @param last The last record to include (inclusive, 1 based).
	*/
	protected abstract List readResourcesPage(SessionState state, int first, int last);

	/**
	 * Implement this to return the number of records that are currently selected.
	 */
	protected abstract int sizeResources(SessionState state);
		
	/**
	 * Return the total page number
	 */
	protected int totalPageNumber(SessionState state)
	{
		return ((Integer) state.getAttribute(STATE_TOTAL_PAGENUMBER)).intValue();
		
	}	// totalPageNumber

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

		if (state.getAttribute(STATE_CURRENT_PAGE) == null)
		{
			state.setAttribute(STATE_CURRENT_PAGE, new Integer(1));
		}
		
		if (state.getAttribute(STATE_TOTAL_PAGENUMBER) == null)
		{
			state.setAttribute(STATE_TOTAL_PAGENUMBER, new Integer(1));
		}
		// ** From NewPageResourceAction -->
		
	}   // initState

	/** 
	* Add the menus for a view mode for paging.
	*/
	protected void addViewPagingMenus(Menu bar, SessionState state)
	{
		bar.add( new MenuEntry(rb_praII.getString("viepag.prev"), (state.getAttribute(STATE_PREV_EXISTS) != null), "doView_prev"));
		bar.add( new MenuEntry(rb_praII.getString("viepag.next"), (state.getAttribute(STATE_NEXT_EXISTS) != null), "doView_next"));

	}   // addViewPagingMenus

	/** 
	* Add the menus for a list mode for paging.
	*/
	protected void addListPagingMenus(Menu bar, SessionState state)
	{
		bar.add( new MenuEntry(rb_praII.getString("lispag.first"), (state.getAttribute(STATE_PREV_PAGE_EXISTS) != null),
					"doList_first"));
		bar.add( new MenuEntry(rb_praII.getString("lispag.prev"), (state.getAttribute(STATE_PREV_PAGE_EXISTS) != null),
					"doList_prev"));
		bar.add( new MenuEntry(rb_praII.getString("lispag.next"), (state.getAttribute(STATE_NEXT_PAGE_EXISTS) != null),
					"doList_next"));
		bar.add( new MenuEntry(rb_praII.getString("lispag.last"), (state.getAttribute(STATE_NEXT_PAGE_EXISTS) != null),
					"doList_last"));

	}   // addListPagingMenus

	/** 
	* Add the menus for search.
	*/
	protected void addSearchMenus(Menu bar, SessionState state)
	{
		bar.add( new MenuDivider());
		bar.add( new MenuField(FORM_SEARCH, "toolbar", "doSearch", (String) state.getAttribute(STATE_SEARCH)));
		bar.add( new MenuEntry(rb_praII.getString("sea.sea"), null, true, MenuItem.CHECKED_NA, "doSearch", "toolbar"));
		if (state.getAttribute(STATE_SEARCH) != null)
		{
			bar.add( new MenuEntry(rb_praII.getString("sea.cleasea"), "doSearch_clear"));
		}

	}   // addSearchMenus

	/**
	* Add the menus for manual / auto - refresh.
	*/
	protected void addRefreshMenus(Menu bar, SessionState state)
	{
		// only offer if there's an observer
		ObservingCourier observer = (ObservingCourier) state.getAttribute(STATE_OBSERVER);
		if (observer == null) return;

		bar.add( new MenuDivider());
		bar.add( new MenuEntry((observer.getEnabled() ? rb_praII.getString("ref.manref") : rb_praII.getString("ref.autoref")), "doAuto"));
		if (!observer.getEnabled())
		{
			bar.add( new MenuEntry(rb_praII.getString("ref.refresh"), "doRefresh"));		
		}
		
	}	// addRefreshMenus

	/**
	* Prepare the current page of messages to display.
	* @return List of MailArchiveMessage to display on this page.
	*/
	protected List prepPage(SessionState state)
	{
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
		
		boolean goViewPage = state.getAttribute(STATE_GOTO_PAGE) != null;

		// if we have no prev page and do have a top message, then we will stay "pined" to the top
		boolean pinToTop = (	(state.getAttribute(STATE_TOP_PAGE_MESSAGE) != null)
							&&	(state.getAttribute(STATE_PREV_PAGE_EXISTS) == null)
							&&	!goNextPage && !goPrevPage && !goNext && !goPrev && !goFirstPage && !goLastPage && !goViewPage);

		// if we have no next page and do have a top message, then we will stay "pined" to the bottom
		boolean pinToBottom = (	(state.getAttribute(STATE_TOP_PAGE_MESSAGE) != null)
							&&	(state.getAttribute(STATE_NEXT_PAGE_EXISTS) == null)
							&&	!goNextPage && !goPrevPage && !goNext && !goPrev && !goFirstPage && !goLastPage && !goViewPage);

		// how many messages, total
		int numMessages = sizeResources(state);

		if (numMessages == 0)
		{
			return new Vector();
		}
		
		// set the total page number
		int totalPageNumber = 1;
		if ((numMessages%pageSize)>0)
		{
			totalPageNumber = numMessages/pageSize + 1;
		}
		else
		{
			totalPageNumber = numMessages/pageSize;
		}
		state.setAttribute(STATE_TOTAL_PAGENUMBER, new Integer(totalPageNumber));

		// save the number of messges
		state.setAttribute(STATE_NUM_MESSAGES, new Integer(numMessages));

		// find the position of the message that is the top first on the page
		int posStart = 0;
		Integer topPageMessage = (Integer) state.getAttribute(STATE_TOP_PAGE_MESSAGE);
		if (topPageMessage != null)
		{
			posStart = topPageMessage.intValue();
		}

		// if going to a certain page
		if (state.getAttribute(STATE_GOTO_PAGE) != null)
		{
			int gotoPage = ((Integer) state.getAttribute(STATE_GOTO_PAGE)).intValue();
			int currentPage = ((Integer) state.getAttribute(STATE_CURRENT_PAGE)).intValue();
			posStart += pageSize * (gotoPage - currentPage);
		}
		
		// if going to the next page, adjust
		else if (goNextPage)
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
		List messagePage = readResourcesPage(state, posStart+1, posEnd+1);

		// save which message is at the top of the page
		state.setAttribute(STATE_TOP_PAGE_MESSAGE, new Integer(posStart));

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
		
		state.removeAttribute(STATE_FIRST_PAGE_EXISTS);
		state.removeAttribute(STATE_LAST_PAGE_EXISTS);
		if (totalPageNumber != 1)
		{
			if (posStart > 0 )
			{
				state.setAttribute(STATE_FIRST_PAGE_EXISTS, "");
			}
			if (posStart + pageSize < numMessages)
			{
				state.setAttribute(STATE_LAST_PAGE_EXISTS, "");
			}
		}

		if (state.getAttribute(STATE_VIEW_ID) != null)
		{
			int viewPos = ((Integer) state.getAttribute(STATE_VIEW_ID)).intValue();
	
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
			state.setAttribute(STATE_VIEW_ID, new Integer(viewPos));

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
		
		if (state.getAttribute(STATE_GOTO_PAGE) != null)
		{
			state.setAttribute(STATE_CURRENT_PAGE, state.getAttribute(STATE_GOTO_PAGE));
			state.removeAttribute(STATE_GOTO_PAGE);
		}

		return messagePage;

	}	// prepPage

	/**
	* Handle a request to change the page size of search list.
	*/
	public void doChange_pagesize(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		String newPageSize = data.getParameters().getString("selectPageSize");
		if (newPageSize == null)
		{
			state.setAttribute(STATE_PAGESIZE, new Integer(DEFAULT_PAGE_SIZE));
			state.setAttribute("inter_size", new Integer(DEFAULT_PAGE_SIZE));
		}
		else
		{
			state.setAttribute(STATE_PAGESIZE, Integer.valueOf(newPageSize));
			state.setAttribute("inter_size", Integer.valueOf(newPageSize));
		}
	}	// doChange_pagesize
	
	public void pagingInfoToContext(SessionState state, Context context)
	{
		if (state.getAttribute(STATE_NUM_MESSAGES) != null)
		{
			context.put("allMsgNumber", state.getAttribute(STATE_NUM_MESSAGES).toString());
			context.put("allMsgNumberInt", state.getAttribute(STATE_NUM_MESSAGES));
		}

		// find the position of the message that is the top first on the page
		if ((state.getAttribute(STATE_TOP_PAGE_MESSAGE) != null) && (state.getAttribute(STATE_PAGESIZE) != null))
		{
			int topMsgPos = ((Integer)state.getAttribute(STATE_TOP_PAGE_MESSAGE)).intValue() + 1;
			context.put("topMsgPos", Integer.toString(topMsgPos));
			int btmMsgPos = topMsgPos + ((Integer)state.getAttribute(STATE_PAGESIZE)).intValue() - 1;
			if (state.getAttribute(STATE_NUM_MESSAGES) != null)
			{
				int allMsgNumber = ((Integer)state.getAttribute(STATE_NUM_MESSAGES)).intValue();
				if (btmMsgPos > allMsgNumber)
					btmMsgPos = allMsgNumber;
			}
			context.put("btmMsgPos", Integer.toString(btmMsgPos));
		}
	
		boolean goPPButton = state.getAttribute(STATE_PREV_PAGE_EXISTS) != null;
		context.put("goPPButton", Boolean.toString(goPPButton));
		boolean goNPButton = state.getAttribute(STATE_NEXT_PAGE_EXISTS) != null;
		context.put("goNPButton", Boolean.toString(goNPButton));
		
		boolean goFPButton = state.getAttribute(STATE_FIRST_PAGE_EXISTS) != null;
		context.put("goFPButton", Boolean.toString(goFPButton));
		boolean goLPButton = state.getAttribute(STATE_LAST_PAGE_EXISTS) != null;
		context.put("goLPButton", Boolean.toString(goLPButton));
		
		context.put("pagesize", state.getAttribute(STATE_PAGESIZE));
		context.put("pagesizes", PAGESIZES);
		
	}	// pagingInfoToContext
	
	/**
	* Clean up all state value for paging.
	*/
	public void cleanStatePaging(SessionState state)
	{
		state.removeAttribute("inter_size");
		state.removeAttribute(STATE_NUM_MESSAGES);
		state.removeAttribute(STATE_TOP_PAGE_MESSAGE);
		state.removeAttribute(STATE_NEXT_PAGE_EXISTS);
		state.removeAttribute(STATE_PREV_PAGE_EXISTS);

	}	// cleanState
	
	/** 
	* Handle a view indecated page request
	**/
	public void doView_page(RunData runData, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState state = ((JetspeedRunData)runData).getPortletSessionState(peid);

		// set the flag to go to the next item on the next view
		String page = runData.getParameters().getString(FORM_PAGE_NUMBER);
		state.setAttribute(STATE_GOTO_PAGE, new Integer(page));

	}	// doView_page
	
	/** 
	* Handle a next-message (view) request.
	**/
	public void doView_next(RunData runData, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState state = ((JetspeedRunData)runData).getPortletSessionState(peid);

		// set the flag to go to the next message on the next view
		state.setAttribute(STATE_GO_NEXT,"");
		
		// set the page number
		int page = ((Integer) state.getAttribute(STATE_CURRENT_PAGE)).intValue();
		state.setAttribute(STATE_CURRENT_PAGE, new Integer(page + 1));

	}	// doView_next

	/** 
	* Handle a first-message page (list) request.
	**/
	public void doList_first(RunData runData, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState state = ((JetspeedRunData)runData).getPortletSessionState(peid);

		// set the flag to go to the next message on the next view
		state.setAttribute(STATE_GO_FIRST_PAGE,"");

		// set the page number
		state.setAttribute(STATE_CURRENT_PAGE, new Integer(1));

	}	// doList_first

	/**
	* Handle a last-message page (list) request.
	**/
	public void doList_last(RunData runData, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState state = ((JetspeedRunData)runData).getPortletSessionState(peid);

		// set the flag to go to the next message on the next view
		state.setAttribute(STATE_GO_LAST_PAGE,"");

		// set the page number
		state.setAttribute(STATE_CURRENT_PAGE, new Integer(totalPageNumber(state)));

	}	// doList_last

	/** 
	* Handle a next-page (list) request.
	**/	
	public void doList_next(RunData runData, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState state = ((JetspeedRunData)runData).getPortletSessionState(peid);

		// set the flag to go to the next page on the next list
		state.setAttribute(STATE_GO_NEXT_PAGE,"");

		// %%% ?? doList(runData, context);
		
		// set the page number
		int page = ((Integer) state.getAttribute(STATE_CURRENT_PAGE)).intValue();
		state.setAttribute(STATE_CURRENT_PAGE, new Integer(page + 1));

	}	// doList_next

	/** 
	* Handle a prev-message (view) request.
	**/	
	public void doView_prev(RunData runData, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState state = ((JetspeedRunData)runData).getPortletSessionState(peid);

		// set the flag to go to the prev message on the next view
		state.setAttribute(STATE_GO_PREV,"");

		// set the page number
		int page = ((Integer) state.getAttribute(STATE_CURRENT_PAGE)).intValue();
		state.setAttribute(STATE_CURRENT_PAGE, new Integer(page - 1));

	}	// doView_prev

	/** 
	* Handle a prev-page (list) request.
	**/	
	public void doList_prev(RunData runData, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState state = ((JetspeedRunData)runData).getPortletSessionState(peid);

		// set the flag to go to the prev page on the next list
		state.setAttribute(STATE_GO_PREV_PAGE,"");	

		// set the page number
		int page = ((Integer) state.getAttribute(STATE_CURRENT_PAGE)).intValue();
		state.setAttribute(STATE_CURRENT_PAGE, new Integer(page - 1));

	}	// doList_prev

	/** 
	* Handle a Search request.
	**/	
	public void doSearch(RunData runData, Context context)
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
	public void doSearch_clear(RunData runData, Context context)
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
	protected void resetPaging(SessionState state)
	{
		// we are changing the sort, so start from the first page again
		state.removeAttribute(STATE_TOP_PAGE_MESSAGE);
		
		state.setAttribute(STATE_CURRENT_PAGE, new Integer(1));

	}	// resetPaging

	/**
	* Toggle auto-update
	*/
	public void doAuto(RunData data, Context context)
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
	public void doRefresh(RunData data, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(peid);

	}	// doRefresh

	/**
	* Enable the observer, unless we are in search mode, where we want it disabled.
	*/
	public void enableObserver(SessionState state)
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



