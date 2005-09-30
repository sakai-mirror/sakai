/**********************************************************************************
 *
 * $Header: /cvs/sakai2/sample-tools/jsf1/src/java/org/sakaiproject/util/Jsf1JsfTool.java,v 1.1 2005/05/10 01:05:47 ggolden.umich.edu Exp $
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

package org.sakaiproject.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.session.Session;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.jsf.util.JsfTool;

/**
 * <p>
 * Sakai Servlet to use for all JSF tools.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class Jsf1JsfTool extends JsfTool
{
	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(Jsf1JsfTool.class);

	/**
	 * Compute a target (i.e. the servlet path info, not including folder root or jsf extension) for the case of the actual path being empty.
	 * 
	 * @return The servlet info path target computed for the case of empty actual path.
	 */
	protected String computeDefaultTarget()
	{
		Session session = SessionManager.getCurrentSession();
		if (session.getUserId() == null)
		{
			return "list";
		}
		
		return "main";
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/sample-tools/jsf1/src/java/org/sakaiproject/util/Jsf1JsfTool.java,v 1.1 2005/05/10 01:05:47 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
