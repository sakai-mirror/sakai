/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/tool-support/src/java/org/sakaiproject/cheftool/RunData.java,v 1.1 2005/04/12 03:11:38 ggolden.umich.edu Exp $
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

import org.sakaiproject.util.ParameterParser;


public class RunData
{
	protected ParameterParser m_params = null;
	protected HttpServletRequest m_req = null;

	public RunData(HttpServletRequest req, ParameterParser params)
	{
		m_req = req;
		m_params = params;
	}

	public ParameterParser getParameters()
	{
		return m_params;
	}

	public HttpServletRequest getRequest()
	{
		return m_req;
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/tool-support/src/java/org/sakaiproject/cheftool/RunData.java,v 1.1 2005/04/12 03:11:38 ggolden.umich.edu Exp $
*
**********************************************************************************/
