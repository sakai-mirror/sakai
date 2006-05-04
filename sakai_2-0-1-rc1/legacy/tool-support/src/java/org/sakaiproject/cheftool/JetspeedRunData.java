/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/tool-support/src/java/org/sakaiproject/cheftool/JetspeedRunData.java,v 1.1 2005/04/12 03:11:38 ggolden.umich.edu Exp $
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

package org.sakaiproject.cheftool;

import javax.servlet.http.HttpServletRequest;

import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.util.ParameterParser;

public class JetspeedRunData extends RunData
{
	protected SessionState state = null;
	protected String pid = null;

	public JetspeedRunData(HttpServletRequest req, SessionState state, String pid, ParameterParser params)
	{
		super(req, params);
		this.state = state;
		this.pid = pid;
	}

	// support the return of the SessionState by:
	// SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

	/**
	 * Access the current request's PortletSession state object.
	 * @param id The Portlet's unique id.
	 * @return the current request's PortletSession state object. (may be null).
	 */
	public SessionState getPortletSessionState(String id)
	{
		return state;
	}

	/**
	 * Returns the portlet id (PEID) referenced in this request
	 *
	 * @return the portlet id (PEID) referenced or null
	 */
	public String getJs_peid()
	{
		return pid;
	}
}
/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/tool-support/src/java/org/sakaiproject/cheftool/JetspeedRunData.java,v 1.1 2005/04/12 03:11:38 ggolden.umich.edu Exp $
*
**********************************************************************************/