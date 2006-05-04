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

package org.sakaiproject.component.test.components;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.test.components.Api1;
import org.sakaiproject.api.test.components.Api4;

/**
 * <p>
 * Example manager API implementation component.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class Component4 implements Api4
{
	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(Component4.class);

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Dependencies and their setter methods
	 *********************************************************************************************************************************************************************************************************************************************************/

	/** Configuration. */
	private String m_config = "not configured";

	/**
	 * Configure
	 * 
	 * @param value
	 *        The configuration value.
	 */
	public void setValue(String value)
	{
		m_config = value;
		M_log.info("setValue(): " + m_config);
	}

	/** Dependency. */
	private Api1 m_s1 = null;

	/**
	 * Dependency.
	 * 
	 * @param value
	 *        The Api1 value.
	 */
	public void setApi1(Api1 value)
	{
		m_s1 = value;
		M_log.info("setApi1()");
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Init and Destroy
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		M_log.info("init() : config: " + m_config);
	}

	public void destroy()
	{
		M_log.info("destroy()");
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Work interface methods: LocalManager
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * @inheritdoc
	 */
	public String method()
	{
		return "Component4/comp2: " + m_config + " - my Api1: " + m_s1;
	}
}


