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

import java.util.Iterator;

import org.w3c.dom.Element;

/**
 * Render results for the New York Public Library Digital Gallery
 */
public class Result extends ResultPageBase {

  /**
   * Constructor
   */
  public Result() {
    super();
  }

  /**
   * Render the page body
   * @return The BODY element
   */
  public Element doResultPageBody() {
   	SearchResultBase	searchResult;
    Element   				renderElement;
    Element   				body, table;
		int								recordNumber;
		/*
		 * Set up the search results and do initial page formatting
		 */
		searchResult	= getSearchResult();
		recordNumber	= searchResult.getSearchStart();

    body  	      = super.doResultPageBody();
    table 	      = doStandardTableSetup(body);
    renderElement = table;
		/*
		 * Quit now if nothing matched
		 */
		if (searchResult.getMatchCount() == 0) {
			doNoResultForQuery(table, getQuery());
      return body;
    }
		/*
		 * Render each image
		 */
   	for (Iterator iterator = searchResult.iterator(); iterator.hasNext(); ) {
      renderElement = doImageTable(renderElement,
      														 recordNumber++,
      														 (MatchItem) iterator.next());
    }
    return body;
  }
}
