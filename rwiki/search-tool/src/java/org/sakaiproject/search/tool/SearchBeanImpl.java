/**********************************************************************************
 *
 * $Header$
 *
 ***********************************************************************************
 *
 * Copyright (c) 2006 University of Cambridge
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

package org.sakaiproject.search.tool;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.search.SearchList;
import org.sakaiproject.search.SearchResult;
import org.sakaiproject.search.SearchService;
import org.sakaiproject.service.framework.portal.PortalService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SiteService;

/**
 * Implementation of the search bean backing bean
 * @author ieb
 * 
 */
public class SearchBeanImpl implements SearchBean {

	/**
	 * The searhc string parameter name
	 */
	private static final String SEARCH_PARAM = "search";

	/**
	 * The results page
	 */
	private static final String SEARCH_PAGE = "page";

	/**
	 * The search criteria
	 */
	private String search;

	/**
	 * The Search Service to use
	 */
	private SearchService searchService;

	private PortalService portalService;

	private SiteService siteService;

	/**
	 * Time taken
	 */
	private double timeTaken = 0;

	/**
	 * The number of results per page
	 */
	private int pagesize = 20;

	/**
	 * The number of list links
	 */
	private int nlistPages = 5;

	/**
	 * The default request page
	 */
	private int requestPage = 0;

	/**
	 * The current search list
	 */
	private SearchList searchResults;

	/**
	 * Creates a searchBean
	 * 
	 * @param request The HTTP request
	 * @param searchService The search service to use
	 * @param siteService the site service
	 * @param portalService the portal service
	 * @throws IdUnusedException if there is no current worksite
	 */
	public SearchBeanImpl(HttpServletRequest request,
			SearchService searchService, SiteService siteService,
			PortalService portalService) throws IdUnusedException {
		this.search = request.getParameter(SEARCH_PARAM);
		this.searchService = searchService;
		this.siteService = siteService;
		this.portalService = portalService;
		try {
			this.requestPage = Integer.parseInt(request
					.getParameter(SEARCH_PAGE));
		} catch (Exception ex) {

		}
		Site currentSite = this.siteService.getSite(this.portalService
				.getCurrentSiteId());
		String siteCheck = currentSite.getReference();

	}

	/**
	 * {@inheritDoc}
	 */
	public String getSearchResults(String searchItemFormat) {
		StringBuffer sb = new StringBuffer();
		List searchResults = search();
		if (searchResults != null) {
			for (Iterator i = searchResults.iterator(); i.hasNext();) {
				SearchResult sr = (SearchResult) i.next();
				sb.append(MessageFormat.format(searchItemFormat, new Object[] {
						String.valueOf(sr.getIndex()), sr.getUrl(),
						sr.getTitle(), sr.getSearchResult(),
						new Double(sr.getScore()) }));

			}
		}
		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public String getPager(String pagerFormat)
			throws UnsupportedEncodingException {
		SearchList sr = (SearchList) search();
		if (sr == null)
			return "";
		int npages = sr.getFullSize() / pagesize;
		int cpage = requestPage - (nlistPages / 2);
		if (cpage < 0) {
			cpage = 0;
		}
		StringBuffer sb = new StringBuffer();

		int lastPage = Math.min(cpage + nlistPages, npages);
		boolean first = true;
		while (cpage <= lastPage) {
			String searchURL = "?search=" + URLEncoder.encode(search, "UTF-8")
					+ "&page=" + String.valueOf(cpage);
			String cssInd = "1";
			if (first) {
				cssInd = "0";
				first = false;
			}
			if (cpage == lastPage - 1) {
				cssInd = "2";
			}

			sb.append(MessageFormat.format(pagerFormat, new Object[] {
					searchURL, String.valueOf(cpage + 1), cssInd }));
			cpage++;
		}

		return sb.toString();
	}
	/**
	 * {@inheritDoc}
	 * 
	 */

	public String getHeader(String headerFormat) {
		SearchList sr = (SearchList) search();
		if (sr == null)
			return "";
		int total = sr.getFullSize();
		int start = 0;
		int end = 0;
		if (total > 0) {
			start = ((SearchResult) sr.get(0)).getIndex();
			end = start + sr.size();
			start++;
		}
		return MessageFormat.format(headerFormat, new Object[] {
				new Integer(start), new Integer(end), new Integer(total),
				new Double(timeTaken) });
	}

	/**
	 * Gets the current search request
	 * 
	 * @return current search request
	 */
	public String getSearch() {
		if (search == null)
			return "";
		return search;
	}

	/**
	 * The time taken to perform the search only, not including rendering
	 * @return
	 */
	public String getTimeTaken() {
		int tt = (int) timeTaken;
		return String.valueOf(tt);
	}

	/**
	 * Perform the search
	 * 
	 * @return a list of page names that match the search criteria
	 */
	public List search() {
		if (searchResults == null) {
			if (search != null && search.trim().length() > 0) {
				List l = new ArrayList();
				l.add(portalService.getCurrentSiteId());
				long start = System.currentTimeMillis();
				int searchStart = requestPage * pagesize;
				int searchEnd = searchStart + pagesize;
				searchResults = searchService.search(search, l, searchStart,
						searchEnd);
				long end = System.currentTimeMillis();
				timeTaken = 0.001 * (end - start);
			}
		}
		return searchResults;
	}

	/**
	 * @return Returns the nlistPages.
	 */
	public int getNlistPages() {
		return nlistPages;
	}

	/**
	 * @param nlistPages
	 *            The nlistPages to set.
	 */
	public void setNlistPages(int nlistPages) {
		this.nlistPages = nlistPages;
	}

	/**
	 * @return Returns the pagesize.
	 */
	public int getPagesize() {
		return pagesize;
	}

	/**
	 * @param pagesize
	 *            The pagesize to set.
	 */
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	/**
	 * @return Returns the requestPage.
	 */
	public int getRequestPage() {
		return requestPage;
	}

	/**
	 * @param requestPage
	 *            The requestPage to set.
	 */
	public void setRequestPage(int requestPage) {
		this.requestPage = requestPage;
	}

	/**
	 * The Total number of results
	 * @return
	 */
	public int getNresults() {
		return searchResults.getFullSize();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public String getSearchTitle() {
		return "Search results for:" + getSearch();
	}
	/**
	 * {@inheritDoc}
	 * 
	 */
	public boolean hasAdmin() {
		String siteId = portalService.getCurrentSiteId();
		return siteService.allowUpdateSite(siteId);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public String getToolUrl() {
		String portalId = portalService.getCurrentToolId();
		return "/portal/tool/" + portalId;
	}

}
