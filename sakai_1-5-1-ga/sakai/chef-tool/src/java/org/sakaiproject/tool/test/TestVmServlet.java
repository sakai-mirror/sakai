/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/test/TestVmServlet.java,v 1.5 2004/07/11 01:54:48 ggolden.umich.edu Exp $
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

package org.sakaiproject.tool.test;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.cheftool.VmServlet;
import org.sakaiproject.util.ParameterParser;

/**
* <p>TestVmServlet is a Servlet that tests the CHEF II Vm integration.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.5 $
*/
public class TestVmServlet extends VmServlet
{
	/**
	 * @return servlet information.
	 */
	public String getServletInfo()
	{
		return "CHEF II Sakai Test Vm Servlet";
	}

	/**
	 * Initialize the servlet.
	 */
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);

		log("TestVmServlet.init()");

	} // init

	/**
	 * Shutdown the servlet.
	 */
	public void destroy()
	{
		log("TestVmServlet.destroy()");

		super.destroy();

	} // destroy

	/**
	 * Respond to a request...
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException
	{
		doGet(req, res);
	}

	/**
	 * Respond to a request...
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException
	{
		// parse the parameters
		ParameterParser param = (ParameterParser) req.getAttribute(ATTR_PARAMS);

		// set the objects for the vm context
		setVmReference("one", param.get("one"), req);
		setVmReference("two", param.getFileItem("two"), req);

		setVmReference("three", param.get("three"), req);
		setVmReference("four", param.getFileItem("four"), req);

		setVmReference("five", param.get("five"), req);
		setVmReference("six", param.getFileItem("six"), req);
		setVmReference("seven", param.getFileItem("seven"), req);

		setVmReference("eight", param.get("eight"), req);
		setVmReference("nine", param.get("nine"), req);

		// include the vm response
		includeVm("/vm/test/test.vm", req, res);

	} // doGet

} // class TestVmServlet

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/test/TestVmServlet.java,v 1.5 2004/07/11 01:54:48 ggolden.umich.edu Exp $
*
**********************************************************************************/
