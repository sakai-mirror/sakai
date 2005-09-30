/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/framework/log/cover/Log.java,v 1.1 2005/05/12 15:45:36 ggolden.umich.edu Exp $
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

package org.sakaiproject.service.framework.log.cover;

import org.sakaiproject.service.framework.component.cover.ComponentManager;

/**
 * <p>Log is a static Cover for the {@link org.sakaiproject.service.framework.log.Logger Logging Service};
 * see that interface for usage details.</p>
 * <p>This is an adapter for the CHEF 1.x Log interface.</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.1 $
 */
public class Log
{
	/**
	 * Access the component instance.
	 * @return the component instance.
	 */
	public static org.sakaiproject.service.framework.log.Logger getInstance()
	{
		if (ComponentManager.CACHE_SINGLETONS)
		{
			if (m_instance == null) m_instance = (org.sakaiproject.service.framework.log.Logger) ComponentManager.get(org.sakaiproject.service.framework.log.Logger.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.service.framework.log.Logger) ComponentManager.get(org.sakaiproject.service.framework.log.Logger.class);
		}
	}
	private static org.sakaiproject.service.framework.log.Logger m_instance = null;



	public static boolean isTraceEnabled(String ignored)
	{
		org.sakaiproject.service.framework.log.Logger logger = getInstance();
		if (logger != null)
			return logger.isTraceEnabled();

		return false;
	}

	public static org.sakaiproject.service.framework.log.Logger getLogger(String ignore)
	{
		return getInstance();
		
	}
	public static boolean isDebugEnabled(String ignored)
	{
		org.sakaiproject.service.framework.log.Logger logger = getInstance();
		if (logger != null)
			return logger.isDebugEnabled();

		return false;
	}

	public static boolean isInfoEnabled(String ignored)
	{
		org.sakaiproject.service.framework.log.Logger logger = getInstance();
		if (logger != null)
			return logger.isInfoEnabled();

		return false;
	}

	public static boolean isWarnEnabled(String ignored)
	{
		org.sakaiproject.service.framework.log.Logger logger = getInstance();
		if (logger != null)
			return logger.isWarnEnabled();

		return false;
	}

	public static boolean isErrorEnabled(String ignored)
	{
		org.sakaiproject.service.framework.log.Logger logger = getInstance();
		if (logger != null)
			return logger.isErrorEnabled();

		return false;
	}

	public static boolean isFatalEnabled(String ignored)
	{
		org.sakaiproject.service.framework.log.Logger logger = getInstance();
		if (logger != null)
			return logger.isFatalEnabled();

		return false;
	}

	public static void trace(String ignored, Object message)
	{
		org.sakaiproject.service.framework.log.Logger logger = getInstance();
		if (logger != null)
			getInstance().trace(message);
	}

	public static void trace(String ignored, Object message, Throwable t)
	{
		org.sakaiproject.service.framework.log.Logger logger = getInstance();
		if (logger != null)
			logger.trace(message, t);
	}

	public static void debug(String ignored, Object message)
	{
		org.sakaiproject.service.framework.log.Logger logger = getInstance();
		if (logger != null)
			logger.debug(message);
	}

	public static void debug(String ignored, Object message, Throwable t)
	{
		org.sakaiproject.service.framework.log.Logger logger = getInstance();
		if (logger != null)
			logger.debug(message, t);
	}

	public static void info(String ignored, Object message)
	{
		org.sakaiproject.service.framework.log.Logger logger = getInstance();
		if (logger != null)
			logger.info(message);
	}

	public static void info(String ignored, Object message, Throwable t)
	{
		org.sakaiproject.service.framework.log.Logger logger = getInstance();
		if (logger != null)
			logger.info(message, t);
	}

	public static void warn(String ignored, Object message)
	{
		org.sakaiproject.service.framework.log.Logger logger = getInstance();
		if (logger != null)
			logger.warn(message);
	}

	public static void warn(String ignored, Object message, Throwable t)
	{
		org.sakaiproject.service.framework.log.Logger logger = getInstance();
		if (logger != null)
			logger.warn(message, t);
	}

	public static void error(String ignored, Object message)
	{
		org.sakaiproject.service.framework.log.Logger logger = getInstance();
		if (logger != null)
			logger.error(message);
	}

	public static void error(String ignored, Object message, Throwable t)
	{
		org.sakaiproject.service.framework.log.Logger logger = getInstance();
		if (logger != null)
			logger.error(message, t);
	}

	public static void fatal(String ignored, Object message)
	{
		org.sakaiproject.service.framework.log.Logger logger = getInstance();
		if (logger != null)
			logger.fatal(message);
	}

	public static void fatal(String ignored, Object message, Throwable t)
	{
		org.sakaiproject.service.framework.log.Logger logger = getInstance();
		if (logger != null)
			logger.fatal(message, t);
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/framework/log/cover/Log.java,v 1.1 2005/05/12 15:45:36 ggolden.umich.edu Exp $
*
**********************************************************************************/
