/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/framework/log/Logger.java,v 1.1 2005/05/12 15:45:32 ggolden.umich.edu Exp $
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

package org.sakaiproject.service.framework.log;

/**
 * <p>Logger is a simple, single log destination interface for program events logging.</p>
 * <p>This interface is function compatible with the concepts of the Apache Jakarta Commons Loging Log class
 * (org.apache.commons.logging.Log version 1.0.3)</p>
 * <p>Log messages are classified with a type.  Types are ordered, from least sever to most sever:
 * <ol>
 * <li>trace</li>
 * <li>debug</li>
 * <li>info</li>
 * <li>warn</li>
 * <li>error</li>
 * <li>fatal</li>
 * </ol>
 * Logging can be enabled above a certain severity type, disabled below.  For example, if logging is set to "warn",
 * all "warn", "error" and "fatal" messages will be logged; "info", "debug" and "trace" messages will be ignored.</p>
 * <p>It's best practice to guard all logging calls with an "enabled test".  The extra time spent on the test will
 * be recovered in production when the logging level is disabled.  The parameter preparation cost of the log call
 * which may be high, will be avoided when the level is disabled and guarding is used.  A guarded log call looks like:
 * <br /><code>if (Logger.isDebugEnabled() Logger.Debug("a message");</code></p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.1 $
 */
public interface Logger
{
	/**
	 * Check if the logger's enabled level includes generation of "trace" type messages.
	 * @return true if trace logging is currently enabled, false if not.
	 */
	boolean isTraceEnabled();

	/**
	 * Check if the logger's enabled level includes generation of "debug" type messages.
	 * @return true if debug logging is currently enabled, false if not.
	 */
	boolean isDebugEnabled();

	/**
	 * Check if the logger's enabled level includes generation of "info" type messages.
	 * @return true if info logging is currently enabled, false if not.
	 */
	boolean isInfoEnabled();

	/**
	 * Check if the logger's enabled level includes generation of "warn" type messages.
	 * @return true if warn logging is currently enabled, false if not.
	 */
	boolean isWarnEnabled();

	/**
	 * Check if the logger's enabled level includes generation of "error" type messages.
	 * @return true if error logging is currently enabled, false if not.
	 */
	boolean isErrorEnabled();

	/**
	 * Check if the logger's enabled level includes generation of "fatal" type messages.
	 * @return true if fatal logging is currently enabled, false if not.
	 */
	boolean isFatalEnabled();

	/**
	 * Log a "trace" type message.
	 * @param message The message to log.
	 */
	void trace(Object message);

	/**
	 * Log a "trace" type message.
	 * @param message The message to log.
	 * @param t The throwable to add to the message in the log.
	 */
	void trace(Object message, Throwable t);

	/**
	 * Log a "debug" type message.
	 * @param message The message to log.
	 */
	void debug(Object message);

	/**
	 * Log a "debug" type message.
	 * @param message The message to log.
	 * @param t The throwable to add to the message in the log.
	 */
	void debug(Object message, Throwable t);

	/**
	 * Log a "info" type message.
	 * @param message The message to log.
	 */
	void info(Object message);

	/**
	 * Log a "info" type message.
	 * @param message The message to log.
	 * @param t The throwable to add to the message in the log.
	 */
	void info(Object message, Throwable t);

	/**
	 * Log a "warn" type message.
	 * @param message The message to log.
	 */
	void warn(Object message);

	/**
	 * Log a "warn" type message.
	 * @param message The message to log.
	 * @param t The throwable to add to the message in the log.
	 */
	void warn(Object message, Throwable t);

	/**
	 * Log a "error" type message.
	 * @param message The message to log.
	 */
	void error(Object message);

	/**
	 * Log a "error" type message.
	 * @param message The message to log.
	 * @param t The throwable to add to the message in the log.
	 */
	void error(Object message, Throwable t);

	/**
	 * Log a "fatal" type message.
	 * @param message The message to log.
	 */
	void fatal(Object message);

	/**
	 * Log a "fatal" type message.
	 * @param message The message to log.
	 * @param t The throwable to add to the message in the log.
	 */
	void fatal(Object message, Throwable t);

}

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/framework/log/Logger.java,v 1.1 2005/05/12 15:45:32 ggolden.umich.edu Exp $
*
**********************************************************************************/
