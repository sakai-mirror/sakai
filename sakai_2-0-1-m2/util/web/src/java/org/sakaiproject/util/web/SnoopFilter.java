/**********************************************************************************
 *
 * $Header: /cvs/sakai2/util/web/src/java/org/sakaiproject/util/web/SnoopFilter.java,v 1.1 2005/05/12 01:39:07 ggolden.umich.edu Exp $
 *
 ***********************************************************************************
 *
 * Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.util.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * RequestFilter Filters all requests to Sakai tools. It is responsible for keeping the Sakai session, done using a cookie to the end user's browser storing the user's session id.
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class SnoopFilter implements Filter
{
	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(SnoopFilter.class);

	/**
	 * Take this filter out of service.
	 */
	public void destroy()
	{
	}

	/**
	 * Filter a request / response.
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		String out = Web.snoop(null, false, null, (HttpServletRequest) request);
		M_log.info(out);
		chain.doFilter(request, response);
	}

	/**
	 * Place this filter into service.
	 * 
	 * @param filterConfig
	 *        The filter configuration object
	 */
	public void init(FilterConfig filterConfig) throws ServletException
	{
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/util/web/src/java/org/sakaiproject/util/web/SnoopFilter.java,v 1.1 2005/05/12 01:39:07 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
