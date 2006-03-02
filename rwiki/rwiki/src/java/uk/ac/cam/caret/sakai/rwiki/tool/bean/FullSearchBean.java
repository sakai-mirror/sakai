/**********************************************************************************
 *
 * $Header$
 *
 ***********************************************************************************
 *
 * Copyright (c) 2005 University of Cambridge
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
package uk.ac.cam.caret.sakai.rwiki.tool.bean;

//FIXME: Tool

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.search.SearchService;

/**
 * Bean for helping with the search view
 * 
 * @author andrew
 */
public class FullSearchBean {


    /**
     * The search criteria
     */
    private String search;

    /**
     * The realm to restrict the search to
     */
    private String realm;


    /**
     * RWikiObjectService to use
     */
    private SearchService searchService;

	private double timeTaken = 0;

    /**
     * Creates a searchBean
     * 
     * @param search
     * @param user
     * @param realm
     * @param objectService
     */
    public FullSearchBean(String search, String realm,
            SearchService searchService) {
        this.search = search;
        this.realm = realm;
        this.searchService = searchService;
    }

    /**
     * Set the RWikiObjectService for searching from
     * 
     * @param objectService
     */
    public void setRWikiObjectService(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Gets the current search request
     * 
     * @return current search request
     */
    public String getSearch() {
        return search;
    }

    /**
     * Gets the current search realm
     * 
     * @return current search realm
     */
    public String getRealm() {
        return realm;
    }

    /**
     * Perform the search
     * @return a list of page names that match the search criteria
     */
    public List getSearchResults() {
        return search();
    }
    
    public double getTimeTaken() {
    	 return timeTaken;
    }


    /**
     * Perform the search
     * @return a list of page names that match the search criteria
     */
    public List search() {
        // FIXME should we use PageLinkRenderer-like thing?
    		List l = new ArrayList();
    		l.add(realm);
    		long start = System.currentTimeMillis();
        List results = searchService.search(search,  l);
        long end = System.currentTimeMillis();
        timeTaken = 0.001*(end-start);
        return results;
    }
}
