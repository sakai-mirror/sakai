/**********************************************************************************
*
* $Header: /cvs/sakai/faces/src/java/org/sakaiproject/jsf/custom/SakaiVariableResolver.java,v 1.7 2004/09/30 20:20:57 ggolden.umich.edu Exp $
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

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.servlet.http.HttpServletRequest;

import org.sakaiproject.service.framework.current.cover.CurrentService;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.framework.session.SessionState;

import com.sun.faces.application.ApplicationImpl;
import com.sun.faces.el.VariableResolverImpl;

/**
 * <p>SakaiVariableResolver extends JSF 1.0's VariableResolverImpl to add PID scope and the special value "ToolConfig".</p>
 * 
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision: 1.7 $
 */
public class SakaiVariableResolver extends VariableResolverImpl
{
	// Specified by javax.faces.el.VariableResolver.resolveVariable()
	public Object resolveVariable(FacesContext context, String name) throws EvaluationException
	{
		ExternalContext ec = context.getExternalContext();

		if ("applicationScope".equals(name))
		{
			return (ec.getApplicationMap());
		}
		else if ("cookie".equals(name))
		{
			return (ec.getRequestCookieMap());
		}
		else if ("facesContext".equals(name))
		{
			return (context);
		}
		else if ("header".equals(name))
		{
			return (ec.getRequestHeaderMap());
		}
		else if ("headerValues".equals(name))
		{
			return (ec.getRequestHeaderValuesMap());
		}
		else if ("initParam".equals(name))
		{
			return (ec.getInitParameterMap());
		}
		else if ("param".equals(name))
		{
			return (ec.getRequestParameterMap());
		}
		else if ("paramValues".equals(name))
		{
			return (ec.getRequestParameterValuesMap());
		}
		else if ("requestScope".equals(name))
		{
			return (ec.getRequestMap());
		}
		else if ("sessionScope".equals(name))
		{
			return (ec.getSessionMap());
		}
		else if ("view".equals(name))
		{
			return (context.getViewRoot());
		}

		// this is how to get the current request's tool configuration, based on the pid
		else if ("toolConfig".equals(name))
		{
			HttpServletRequest req = (HttpServletRequest) CurrentService.getInThread(HttpServletRequest.class.getName());
			if (req != null)
			{
				String pid = req.getParameter("pid");
				if (pid != null)
				{
					ToolConfiguration tool = SiteService.findTool(pid);
					return tool;
				}
			}

			return null;
		}

		else
		{
			// do the scoped lookup thing
			Object value = null;

			if (null == (value = ec.getRequestMap().get(name)))
			{
				// check the tool state (pid based) next
				SessionState state = PortalService.getCurrentToolState();

				if ((null == state) || (null == (value = state.getAttribute(name))))
				{
					if (null == (value = ec.getSessionMap().get(name)))
					{
						if (null == (value = ec.getApplicationMap().get(name)))
						{
							// if it's a managed bean try and create it
							ApplicationFactory aFactory =
								(ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
							Application application = aFactory.getApplication();
							if (application instanceof ApplicationImpl)
							{
								value = ((ApplicationImpl) application).createAndMaybeStoreManagedBeans(context, name);
							}
						}
					}
				}
			}
			return (value);
		}
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/faces/src/java/org/sakaiproject/jsf/custom/SakaiVariableResolver.java,v 1.7 2004/09/30 20:20:57 ggolden.umich.edu Exp $
*
**********************************************************************************/
