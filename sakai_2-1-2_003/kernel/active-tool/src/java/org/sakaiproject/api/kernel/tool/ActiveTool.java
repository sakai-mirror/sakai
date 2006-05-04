/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
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

package org.sakaiproject.api.kernel.tool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * Extension to tool to introduce Servlet API specific tool activity.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public interface ActiveTool extends Tool
{
	/**
	 * Invoke the tool to handle the complete request
	 * 
	 * @param req
	 *        The request.
	 * @param res
	 *        The response.
	 * @param placement
	 *        The tool placement for this request.
	 * @param toolContext
	 *        The (optional) servlet context path that is given to the tool.
	 * @param toolPath
	 *        The (optional) servlet pathInfo that is given to the tool.
	 * @throws ToolException
	 *         if there's any trouble running the tool.
	 */
	void forward(HttpServletRequest req, HttpServletResponse res, Placement placement, String toolContext, String toolPath)
			throws ToolException;

	/**
	 * Invoke the tool to handle the request by producing a fragment
	 * 
	 * @param req
	 *        The request.
	 * @param res
	 *        The response.
	 * @param placement
	 *        The tool placement for this request.
	 * @param toolContext
	 *        The (optional) servlet context path that is given to the tool.
	 * @param toolPath
	 *        The (optional) servlet pathInfo that is given to the tool.
	 * @throws ToolException
	 *         if there's any trouble running the tool.
	 */
	void include(HttpServletRequest req, HttpServletResponse res, Placement placement, String toolContext, String toolPath)
			throws ToolException;

	/**
	 * Invoke the tool to handle the complete request as a helper. Note, the placement is shared between invoker and invoked.
	 * 
	 * @param req
	 *        The request.
	 * @param res
	 *        The response.
	 * @param toolContext
	 *        The (optional) servlet context path that is given to the tool.
	 * @param toolPath
	 *        The (optional) servlet pathInfo that is given to the tool.
	 * @throws ToolException
	 *         if there's any trouble running the tool.
	 */
	void help(HttpServletRequest req, HttpServletResponse res, String toolContext, String toolPath) throws ToolException;
}
