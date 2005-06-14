/**********************************************************************************
*
* $Header: /cvs/sakai/util/src/java/org/sakaiproject/util/servletfilter/ExampleFilter.java,v 1.3 2005/01/10 22:00:11 janderse.umich.edu Exp $
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
/*
* Copyright 2004 The Apache Software Foundation
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.sakaiproject.util.servletfilter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


/**
 * Example filter that can be attached to either an individual servlet
 * or to a URL pattern.  This filter performs the following functions:
 * <ul>
 * <li>Attaches itself as a request attribute, under the attribute name
 *     defined by the value of the <code>attribute</code> initialization
 *     parameter.</li>
 * <li>Calculates the number of milliseconds required to perform the
 *     servlet processing required by this request, including any
 *     subsequently defined filters, and logs the result to the servlet
 *     context log for this application.
 * </ul>
 *
 * @author Craig McClanahan
 * @version $Revision: 1.3 $ $Date: 2005/01/10 22:00:11 $
 */

public final class ExampleFilter implements Filter {


    // ----------------------------------------------------- Instance Variables


    /**
     * The request attribute name under which we store a reference to ourself.
     */
    private String attribute = null;


    /**
     * The filter configuration object we are associated with.  If this value
     * is null, this filter instance is not currently configured.
     */
    private FilterConfig filterConfig = null;


    // --------------------------------------------------------- Public Methods


    /**
     * Take this filter out of service.
     */
    public void destroy() {

        this.attribute = null;
        this.filterConfig = null;

    }


    /**
     * Time the processing that is performed by all subsequent filters in the
     * current filter stack, including the ultimately invoked servlet.
     *
     * @param request The servlet request we are processing
     * @param result The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain)
	throws IOException, ServletException {

	// Store ourselves as a request attribute (if requested)
	if (attribute != null)
	    request.setAttribute(attribute, this);

	// Time and log the subsequent processing
	long startTime = System.currentTimeMillis();
        chain.doFilter(request, response);
	long stopTime = System.currentTimeMillis();
	filterConfig.getServletContext().log
	    (this.toString() + ": " + (stopTime - startTime) +
	     " milliseconds");

    }


    /**
     * Place this filter into service.
     *
     * @param filterConfig The filter configuration object
     */
    public void init(FilterConfig filterConfig) throws ServletException {

	this.filterConfig = filterConfig;
        this.attribute = filterConfig.getInitParameter("attribute");

    }


    /**
     * Return a String representation of this object.
     */
    public String toString() {

	if (filterConfig == null)
	    return ("InvokerFilter()");
	StringBuffer sb = new StringBuffer("InvokerFilter(");
	sb.append(filterConfig);
	sb.append(")");
	return (sb.toString());

    }


}

/**********************************************************************************
*
* $Header: /cvs/sakai/util/src/java/org/sakaiproject/util/servletfilter/ExampleFilter.java,v 1.3 2005/01/10 22:00:11 janderse.umich.edu Exp $
*
**********************************************************************************/
