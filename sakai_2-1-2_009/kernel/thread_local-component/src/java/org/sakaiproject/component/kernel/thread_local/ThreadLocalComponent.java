/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.component.kernel.thread_local;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.thread_local.ThreadLocalManager;

/**
 * <p>
 * ThreadLocalComponent provides the standard implementation of the Sakai Framework ThreadLocalManager.
 * </p>
 * <p>
 * See the {@link org.sakaiproject.api.kernel.thread_local.ThreadLocalManager}interface for details.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class ThreadLocalComponent implements ThreadLocalManager
{
	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(ThreadLocalComponent.class);

	/**
	 * <p>
	 * ThreadBindings is a thread local map of keys to objects, holding the things bound to each thread.
	 * </p>
	 */
	protected class ThreadBindings extends ThreadLocal
	{
		public Object initialValue()
		{
			return new HashMap();
		}

		public Map getBindings()
		{
			return (Map) get();
		}
	}

	/** The bindings for each thread. */
	protected ThreadBindings m_bindings = new ThreadBindings();

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Dependencies and their setter methods
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Init and Destroy
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		M_log.info("init()");
	}

	/**
	 * Final cleanup.
	 */
	public void destroy()
	{
		M_log.info("destroy()");
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Work interface methods: org.sakaiproject.api.kernel.thread_local.ThreadLocalManager
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * {@inheritDoc}
	 */
	public void set(String name, Object value)
	{
		// find the map that might already exist
		Map bindings = m_bindings.getBindings();
		if (bindings == null)
		{
			M_log.warn("setInThread: no bindings!");
			return;
		}

		// remove if nulling
		if (value == null)
		{
			bindings.remove(name);
		}

		// otherwise bind the object
		else
		{
			bindings.put(name, value);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void clear()
	{
		Map bindings = m_bindings.getBindings();
		if (bindings == null)
		{
			M_log.warn("clear: no bindings!");
			return;
		}

		// clear the bindings map associated with this thread
		bindings.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	public Object get(String name)
	{
		Map bindings = m_bindings.getBindings();
		if (bindings == null)
		{
			M_log.warn("get: no bindings!");
			return null;
		}

		return bindings.get(name);
	}
}



