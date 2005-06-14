/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/cheftool/PortletConfig.java,v 1.9 2004/06/22 03:04:55 ggolden Exp $
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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;

import org.sakaiproject.service.framework.config.ToolRegistration;
import org.sakaiproject.service.legacy.site.ToolConfiguration;

public class PortletConfig
{
	private ServletConfig config;
	private ToolConfiguration toolConfig;
	private ToolRegistration toolReg;

	/**
	 * @param VelocityPortletPaneledAction
	 */
	PortletConfig(ServletConfig config, ToolConfiguration toolConfig, ToolRegistration reg)
	{
		this.config = config;
		this.toolConfig = toolConfig;
		this.toolReg = reg;
	}

	public String getInitParameter(String name, String dflt)
	{
		String value = getInitParameter(name);

		if (value == null)
		{
			value = dflt;
		}

		return value;
	}

	public String getInitParameter(String name)
	{
		String value = null;

		// check the tool config if present
		if (toolConfig != null)
		{
			value = toolConfig.getProperties().getProperty(name);
		}

		// check the registration if present and no value so far
		if (value == null)
		{
			if (toolReg != null)
			{
				value = toolReg.getDefaultConfiguration().getProperty(name);
			}
		}

		// check the servlet config if no value so far
		if (value == null)
		{
			value = config.getInitParameter(name);
		}

		return value;
	}

	public Map getInitParameters()
	{
		Map rv = new HashMap();

		// load up with init parameters
		Enumeration e = config.getInitParameterNames();
		while (e.hasMoreElements())
		{
			String name = (String) e.nextElement();
			rv.put(name, config.getInitParameter(name));
		}

		// add reg
		if (toolReg != null)
		{
			for (Iterator i = toolReg.getDefaultConfiguration().getPropertyNames(); i.hasNext();)
			{
				String name = (String) i.next();
				rv.put(name, toolReg.getDefaultConfiguration().getProperty(name));
			}
		}

		// add config
		if (toolConfig != null)
		{
			for (Iterator i = toolConfig.getProperties().getPropertyNames(); i.hasNext();)
			{
				String name = (String) i.next();
				rv.put(name, toolConfig.getProperties().getProperty(name));
			}
		}

		return rv;
	}

	public String getTitle()
	{
		if (toolConfig != null)
		{
			return toolConfig.getTitle();
		}

		return "";
	}

	/**
	 * Special non-jetspeed imitation: get three possible init parameter values:
	 * [0] from servlet config
	 * [1] from tool registration
	 * [2] from tool config
	 * nulls if not present
	 */
	public String[] get3InitParameter(String name)
	{
		String[] value = new String[3];

		// check the tool config if present
		if (toolConfig != null)
		{
			value[2] = toolConfig.getProperties().getProperty(name);
		}

		// check the registration if present
		if (toolReg != null)
		{
			value[1] = toolReg.getDefaultConfiguration().getProperty(name);
		}

		// check the servlet config
		value[0] = config.getInitParameter(name);

		return value;
	}

}

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/cheftool/PortletConfig.java,v 1.9 2004/06/22 03:04:55 ggolden Exp $
*
**********************************************************************************/
