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
package uk.ac.cam.caret.sakai.rwiki.bean;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import uk.ac.cam.caret.sakai.rwiki.model.NameHelper;
import uk.ac.cam.caret.sakai.rwiki.util.WikiPageAction;

/**
 * This bean is a helper bean for the view view.
 * 
 * @author andrew
 */
public class ViewBean {

    /**
     * Parameter name for the parameter indicating which panel to use
     */
    public static final String PANEL_PARAM = "panel";

    /**
     * Value of the parameter panel that indicates the main panel
     */
    public static final String MAIN_PANEL = "Main";

    /**
     * Parameter name for the parameter indicating what action is required
     */
    public static final String ACTION_PARAM = "action";

    /**
     * Parameter name for the paramater indicating which pageName to view, edit
     * etc.
     */
    public static final String PAGE_NAME_PARAM = "pageName";

    /*
     * These are urlencoded variants of constants from files that actually do
     * the work...
     */
    protected static final String PAGENAME_URL_ENCODED = 
        urlEncode(PAGE_NAME_PARAM);

    protected static final String ACTION_URL_ENCODED = 
        urlEncode(ACTION_PARAM);

    protected static final String PANEL_URL_ENCODED = 
        urlEncode(PANEL_PARAM);

    protected static final String MAIN_URL_ENCODED = 
        urlEncode(MAIN_PANEL);

    protected static final String SEARCH_URL_ENCODED = 
        urlEncode(SearchBean.SEARCH_PARAM);

    protected static final String REALM_URL_ENCODED = 
        urlEncode(SearchBean.REALM_PARAM);

    /**
     * The current pageName
     */
    private String pageName;

    /**
     * The current localSpace
     */
    private String localSpace;

    /**
     * The anchor to view
     */
    private String anchor;

    /**
     * The current search criteria
     */
    private String search;

    /**
     * Simple constructor that creates an empty view bean.
     * 
     * You must set the pageName and the localSpace to make this useful
     * 
     */
    public ViewBean() {
        // Beans must have null constructor!
    }

    /**
     * Creates a ViewBean and set's the interested page name and local space
     * 
     * @param name
     *            page name possibly non-globalised
     * @param defaultSpace
     *            default space to globalise against
     */
    public ViewBean(String name, String defaultSpace) {
        this.pageName = NameHelper.globaliseName(name, defaultSpace);
        this.localSpace = defaultSpace;
    }

    private String getAnchorString() {
        if (anchor != null) {
            return "#" + urlEncode(anchor);
        }
        return "";
    }

    /**
     * Returns a string representation of an url to perma view the current page
     * 
     * @return url as string
     */
    public String getPublicViewUrl() {
        return getPageUrl(pageName, WikiPageAction.PUBLICVIEW_ACTION.getName());
    }
    /**
     * Returns a string representation of an url to view the current page
     * 
     * @return url as string
     */
    public String getViewUrl() {
        return getPageUrl(pageName, WikiPageAction.VIEW_ACTION.getName());
    }

    /**
     * Returns a string representation of an url to view the passed in page
     * 
     * @param name
     *            possibly non-globalised name to view
     * @return url as string
     */
    public String getViewUrl(String name) {
        return getPageUrl(NameHelper.globaliseName(name, localSpace),
                WikiPageAction.VIEW_ACTION.getName());
    }

    /**
     * Returns a string representation of an url to edit the current page
     * 
     * @return url as string
     */
    public String getEditUrl() {
        return getPageUrl(pageName, WikiPageAction.EDIT_ACTION.getName());
    }

    /**
     * Returns a string representation of an url to edit the passed in page.
     * 
     * @param name
     *            possibly non-globalised name
     * @return url as string
     */

    public String getEditUrl(String name) {
        return getPageUrl(NameHelper.globaliseName(name, localSpace),
                WikiPageAction.EDIT_ACTION.getName());
    }

    /**
     * Returns a string representation of an url to view information about the
     * current page
     * 
     * @return url as string
     */
    public String getInfoUrl() {
        return getPageUrl(pageName, WikiPageAction.INFO_ACTION.getName());
    }

    /**
     * Returns a string representation of an url to view information about the
     * passed in page
     * 
     * @param name
     *            possibly non-globalised name
     * @return url as string
     */
    public String getInfoUrl(String name) {
        return getPageUrl(NameHelper.globaliseName(name, localSpace),
                WikiPageAction.INFO_ACTION.getName());
    }
    
    public String getHistoryUrl() {
        return getHistoryUrl(pageName);
    }

    public String getHistoryUrl(String name) {
        return getPageUrl(NameHelper.globaliseName(name, localSpace),
                WikiPageAction.HISTORY_ACTION.getName());
    }
    
    /**
     * Given a page name and an action return an url that represents it.
     * 
     * @param pageName
     *            globalised pagename to perform action on
     * @param action
     *            name of action to perform
     * @return url as string
     */
    protected String getPageUrl(String pageName, String action) {
        return getAnchorString() + "?" + PAGENAME_URL_ENCODED + "="
                + urlEncode(pageName) + "&" + ACTION_URL_ENCODED + "="
                + urlEncode(action) + "&" + PANEL_URL_ENCODED + "="
                + MAIN_URL_ENCODED + "&" + REALM_URL_ENCODED + "="
                + urlEncode(localSpace);
    }

    /**
     * Creates an appropriate url for searching for the given criteria.
     * 
     * XXX this shouldn't be here!
     * 
     * @return url as string
     */
    protected String getSearchUrl() {
        return "?" + ACTION_URL_ENCODED + "=" + SEARCH_URL_ENCODED + "&"
                + SEARCH_URL_ENCODED + "=" + urlEncode(search) + "&"
                + REALM_URL_ENCODED + "=" + localSpace + "&"
                + PANEL_URL_ENCODED + "=" + MAIN_URL_ENCODED;
    }

    /**
     * The Globalised Page Name
     * 
     * @return globalised page name
     */
    public String getPageName() {
        return pageName;
    }

    /**
     * Set the globalised page name
     * 
     * @param pageName
     *            globalised page name
     */
    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    /**
     * The page name localised against the localSpace
     * 
     * @return localised page name
     */
    public String getLocalName() {
        return NameHelper.localizeName(this.pageName, this.localSpace);
    }

    /**
     * The localSpace
     * 
     * @return localSpace as string
     */
    public String getLocalSpace() {
        return localSpace;
    }

    /**
     * Set the localSpace
     * 
     * @param localSpace
     *            the new localSpace
     */
    public void setLocalSpace(String localSpace) {
        this.localSpace = localSpace;
    }

    /**
     * The space of that the page is in
     * 
     * @return the page's space
     */
    public String getPageSpace() {
        return NameHelper.localizeSpace(pageName, localSpace);
    }

    /**
     * Takes a string to encode and encodes it as a UTF-8 URL-Encoded string.
     * 
     * @param toEncode
     *            string to encode.
     * @return url encoded string.
     */
    public static String urlEncode(String toEncode) {
        try {
            return URLEncoder.encode(toEncode, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(
                    "Not entirely sure how this happened but UTF-8 doesn't " 
                        + "represent a valid encoding anymore! Weird!",
                    e);
        }
    }

    /**
     * Set the current anchor
     * 
     * @param anchor
     *            anchor to set
     */
    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    /**
     * Get the current anchor name
     * @return anchor
     */
    public String getAnchor() {
        return anchor;
    }

    /**
     * The current search criteria
     *  
     * XXX This shouldn't be here!
     * @return search criteria
     */
    public String getSearch() {
        return search;
    }

    /**
     * Set the current search criteria
     * 
     * XXX This shouldn't be here!
     * @param search the search criteria
     */
    public void setSearch(String search) {
        this.search = search;
    }

}
