/**********************************************************************************
*
* $Header: /cvs/sakai/component/src/java/org/sakaiproject/component/framework/log/CommonsLogger.java,v 1.8 2004/06/22 03:10:08 ggolden Exp $
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

package org.sakaiproject.component.framework.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.service.framework.log.Logger;

/**
 * <p>CommonsLogger is an implementation of the Sakai log Service API using the Apache Jakarta Commons Logger.</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.8 $
 */
public class CommonsLogger implements Logger
{
	/** Our commons logging log. */
	private static Log log = LogFactory.getLog(CommonsLogger.class);

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		info(this + ".init()");
	}

	/**
	 * Final cleanup.
	 */
	public void destroy()
	{
		info(this + ".destroy()");
	}

	/**
	 * No arg constructor (no dependencies, no configuration)
	 */
	public CommonsLogger()
	{
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isTraceEnabled()
	{
		return log.isTraceEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isDebugEnabled()
	{
		return log.isDebugEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isInfoEnabled()
	{
		return log.isInfoEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isWarnEnabled()
	{
		return log.isWarnEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isErrorEnabled()
	{
		return log.isErrorEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isFatalEnabled()
	{
		return log.isFatalEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	public void trace(Object message)
	{
		log.trace(message);
	}

	/**
	 * {@inheritDoc}
	 */
	public void trace(Object message, Throwable t)
	{
		log.trace(message, t);
	}

	/**
	 * {@inheritDoc}
	 */
	public void debug(Object message)
	{
		log.debug(message);
	}

	/**
	 * {@inheritDoc}
	 */
	public void debug(Object message, Throwable t)
	{
		log.debug(message, t);
	}

	/**
	 * {@inheritDoc}
	 */
	public void info(Object message)
	{
		log.info(message);
	}

	/**
	 * {@inheritDoc}
	 */
	public void info(Object message, Throwable t)
	{
		log.info(message, t);
	}

	/**
	 * {@inheritDoc}
	 */
	public void warn(Object message)
	{
		log.warn(message);
	}

	/**
	 * {@inheritDoc}
	 */
	public void warn(Object message, Throwable t)
	{
		log.warn(message, t);
	}

	/**
	 * {@inheritDoc}
	 */
	public void error(Object message)
	{
		log.error(message);
	}

	/**
	 * {@inheritDoc}
	 */
	public void error(Object message, Throwable t)
	{
		log.error(message, t);
	}

	/**
	 * {@inheritDoc}
	 */
	public void fatal(Object message)
	{
		log.fatal(message);
	}

	/**
	 * {@inheritDoc}
	 */
	public void fatal(Object message, Throwable t)
	{
		log.fatal(message, t);
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/component/src/java/org/sakaiproject/component/framework/log/CommonsLogger.java,v 1.8 2004/06/22 03:10:08 ggolden Exp $
*
**********************************************************************************/
