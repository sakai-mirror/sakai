/**********************************************************************************
*
* $Header: /cvs/sakai/component/src/java/org/sakaiproject/component/framework/log/Jdk14Logger.java,v 1.1 2004/06/25 03:58:46 ggolden.umich.edu Exp $
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

import java.util.logging.FileHandler;
import java.util.logging.Level;

import org.sakaiproject.service.framework.log.Logger;

/**
 * <p>CommonsLogger is an implementation of the Sakai log Service API using the Apache Jakarta Commons Logger.</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.1 $
 */
public class Jdk14Logger implements Logger
{
	/** Our commons logging log. */
	private static java.util.logging.Logger log = null;

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/** The file path and name. */
	protected String m_filePath = "/usr/local/sakai/logs/sakai%u.log";

	/**
	 * Set the file path and name.
	 * @param path The file path and name.
	 */
	public void setFilePath(String path)
	{
		m_filePath = path;
	}

	/** The logging level to use. */
	protected Level m_level = Level.INFO;

	/**
	 * Set the lowest logging level to be enabled.  Values accepted are:
	 * <ol>
	 * <li>trace</li>
	 * <li>debug</li>
	 * <li>info</li>
	 * <li>warn</li>
	 * <li>error</li>
	 * <li>fatal</li>
	 * </ol>
	 */
	public void setLevel(String level)
	{
		if ("trace".equalsIgnoreCase(level))
		{
			m_level = Level.FINEST;
		}
		else if ("debug".equalsIgnoreCase(level))
		{
			m_level = Level.FINE;
		}
		else if ("info".equalsIgnoreCase(level))
		{
			m_level = Level.INFO;
		}
		else if ("warn".equalsIgnoreCase(level))
		{
			m_level = Level.WARNING;
		}
		else if ("error".equalsIgnoreCase(level))
		{
			m_level = Level.SEVERE;
		}
		else if ("fatal".equalsIgnoreCase(level))
		{
			m_level = Level.SEVERE;
		}
	}

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		try
		{
			log = java.util.logging.Logger.getLogger(Jdk14Logger.class.getName());

			log.setLevel(m_level);
			FileHandler fh = new FileHandler(m_filePath, 0, 1, true);
			fh.setFormatter(new Jdk14LoggerFormatter());
			fh.setLevel(m_level);
			log.addHandler(fh);
		}
		catch (Exception e)
		{
			System.err.println(this +".init(): " + e);
		}

		info(this +".init()");
	}

	/**
	 * Final cleanup.
	 */
	public void destroy()
	{
		info(this +".destroy()");
	}

	/*******************************************************************************
	* Work Interface: org.sakaiproject.service.framework.log.Logger
	*******************************************************************************/

	/**
	 * {@inheritDoc}
	 */
	public boolean isTraceEnabled()
	{
		return log.isLoggable(Level.FINEST);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isDebugEnabled()
	{
		return log.isLoggable(Level.FINE);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isInfoEnabled()
	{
		return log.isLoggable(Level.INFO);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isWarnEnabled()
	{
		return log.isLoggable(Level.WARNING);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isErrorEnabled()
	{
		return log.isLoggable(Level.SEVERE);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isFatalEnabled()
	{
		return log.isLoggable(Level.SEVERE);
	}

	/**
	 * {@inheritDoc}
	 */
	public void trace(Object message)
	{
		log.finest(message.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	public void trace(Object message, Throwable t)
	{
		log.log(Level.FINEST, message.toString(), t);
	}

	/**
	 * {@inheritDoc}
	 */
	public void debug(Object message)
	{
		log.fine(message.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	public void debug(Object message, Throwable t)
	{
		log.log(Level.FINE, message.toString(), t);
	}

	/**
	 * {@inheritDoc}
	 */
	public void info(Object message)
	{
		log.info(message.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	public void info(Object message, Throwable t)
	{
		log.log(Level.INFO, message.toString(), t);
	}

	/**
	 * {@inheritDoc}
	 */
	public void warn(Object message)
	{
		log.warning(message.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	public void warn(Object message, Throwable t)
	{
		log.log(Level.WARNING, message.toString(), t);
	}

	/**
	 * {@inheritDoc}
	 */
	public void error(Object message)
	{
		log.severe(message.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	public void error(Object message, Throwable t)
	{
		log.log(Level.SEVERE, message.toString(), t);
	}

	/**
	 * {@inheritDoc}
	 */
	public void fatal(Object message)
	{
		log.severe(message.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	public void fatal(Object message, Throwable t)
	{
		log.log(Level.SEVERE, message.toString(), t);
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/component/src/java/org/sakaiproject/component/framework/log/Jdk14Logger.java,v 1.1 2004/06/25 03:58:46 ggolden.umich.edu Exp $
*
**********************************************************************************/
