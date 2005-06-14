/**********************************************************************************
*
* $Header: /cvs/sakai/jsf/src/java/org/sakaiproject/jsf/custom/SakaiViewHandler.java,v 1.9 2004/11/09 03:59:34 janderse.umich.edu Exp $
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

package org.sakaiproject.jsf.custom;

import javax.faces.context.FacesContext;

import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;

import com.sun.faces.application.ViewHandlerImpl;

/**
 * <p>SakaiViewHandler extends the basic ViewHandler functionality
 * for getActionURL().  getActionURL() is extended to be the 
 * fully qualified URL and to additionally include the "pid" and "panel"
 * parameters.
 * 
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision: 1.9 $
 */
public class SakaiViewHandler extends ViewHandlerImpl
{

	public SakaiViewHandler()
	{
		super();
	}

	public String getActionURL(FacesContext context, String viewId)
	{
		String rv = super.getActionURL(context, viewId);
		String pid = (String) context.getExternalContext().getRequestParameterMap().get("pid");
		String panel = (String) context.getExternalContext().getRequestParameterMap().get("panel");
		rv = ServerConfigurationService.getPortalTunnelUrl() + rv;

		if (pid != null)
		{
			rv = rv + "?pid=" + pid;

			if (panel != null)
			{
				rv = rv + "&panel=" + panel;
			}
		}
		
		return rv;
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/jsf/src/java/org/sakaiproject/jsf/custom/SakaiViewHandler.java,v 1.9 2004/11/09 03:59:34 janderse.umich.edu Exp $
*
**********************************************************************************/
