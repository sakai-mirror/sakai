/**********************************************************************************
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
package org.sakaiproject.tool.search;

import org.w3c.dom.Element;

/**
 * Interface for HTML result rendering
 */
public interface ResultPageInterface {

	/**
	 * Initialize (values from the query operation are available)
	 * @param query Search query handler
	 * @param searchResult Search results
	 */
	 public void initialize(QueryBase query, SearchResultBase searchResult);

	/**
	 * Initialize (no query values are available)
	 * @param formType Type of query (form type)
	 * @param css URL for our CSS file
	 */
	 public void initialize(String formType, String css);

	/**
	 * Render the HTML HEAD portion of our response
	 */
	public Element doResultPageHeader();

	/**
	 * Render the initial (starting) page body
	 */
	public Element doInitialQueryBody();

	/**
	 * Render a standard page body
	 */
	public Element doResultPageBody();

	/**
	 * Render an error page body
	 * @param text Error text
	 */
	public Element doResultPageErrorBody(String text);

	/**
	 * Render any "end of page" text
	 */
	public void doResultPageFooter();
}
