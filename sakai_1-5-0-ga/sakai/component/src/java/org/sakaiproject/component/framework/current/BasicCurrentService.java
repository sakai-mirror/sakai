/**********************************************************************************
*
* $Header: /cvs/sakai/component/src/java/org/sakaiproject/component/framework/current/BasicCurrentService.java,v 1.9 2005/02/09 18:17:10 ggolden.umich.edu Exp $
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

package org.sakaiproject.component.framework.current;

import java.util.HashMap;
import java.util.Map;

import org.sakaiproject.service.framework.current.CurrentService;
import org.sakaiproject.service.framework.log.Logger;

/**
 * <p>BasicCurrentService implements the CurrentService.</p>
 * <p>See the {@link org.sakaiproject.service.framework.current.CurrentService} interface for details.</p>
 * 
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision: 1.9 $
 */
public class BasicCurrentService implements CurrentService
{
	/**
	 * <p>ThreadBindings is a thread local map of keys to objects, holding the things bound to each thread.</p>
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

	/*******************************************************************************
	* Dependencies and their setter methods
	*******************************************************************************/

	/** Dependency: a Logger */
	protected Logger logger = null;

	/**
	 * Dependency: a Logger.
	 * @param service the logger.
	 */
	public void setLogger(Logger service)
	{
		this.logger = service;
	}

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		logger.info(this +".init()");
	}

	/**
	 * Final cleanup.
	 */
	public void destroy()
	{
		logger.info(this +".destroy()");
	}

	/*******************************************************************************
	* Work interface methods: org.sakaiproject.service.framework.current.CurrentService
	*******************************************************************************/

	/**
	 * {@inheritDoc}
	 */
	public void setInThread(String name, Object value)
	{
		// find the map that might already exist
		Map bindings = m_bindings.getBindings();
		if (bindings == null)
		{
			logger.warn(this + ".setInThread: no bindings!");
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
	public void clearInThread()
	{
		// how started are we?
		Integer countInt = (Integer) getInThread("DEBUG.COUNT");
		int count = 0;
		if (countInt != null) count = countInt.intValue();
		
		// decrement the count
		count--;
		if (count > 0)
		{
			setInThread("DEBUG.COUNT", new Integer(count));
		}
		else
		{
			setInThread("DEBUG.COUNT", null);
		}

		// only the last time
		if (count == 0)
		{
			if (DEBUG)
			{
				// log any debug caught if over threshold
				long endTime = System.currentTimeMillis();

				Long time = (Long) getInThread("DEBUG.TIME");
				if (time == null) return;
				long startTime = time.longValue();

				StringBuffer buf = (StringBuffer) getInThread("DEBUG");
				if (buf == null) return;

				String description = (String) getInThread("DEBUG.DESC");
				if (description == null)
				{
					description = "TIME";
				}

				buf.insert(0, " " + description + ": " + (endTime-startTime) + " ms");
				logger.info(buf.toString());
			}

			Map bindings = m_bindings.getBindings();
			if (bindings == null)
			{
				logger.warn(this + ".clearInThread: no bindings!");
				return;
			}

			// clear the bindings map associated with this thread
			bindings.clear();
		}
		
		else if (count < 0)
		{
			if (DEBUG)
			{
				logger.warn(this + ".clearInThread: negative count!");
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getInThread(String name)
	{
		Map bindings = m_bindings.getBindings();
		if (bindings == null)
		{
			logger.warn(this + ".getInThread: no bindings!");
			return null;
		}
		
		return bindings.get(name);
	}

	/**
	 * {@inheritDoc}
	 */
	public void startThread(String description)
	{
		// are we started?
		Integer countInt = (Integer) getInThread("DEBUG.COUNT");
		int count = 0;
		if (countInt != null) count = countInt.intValue();
		
		// increment the count
		setInThread("DEBUG.COUNT", new Integer(count+1));

		// first time only
		if (countInt == null)
		{
			if (DEBUG)
			{
				// save the time at thread start
				Long startTime = new Long(System.currentTimeMillis());
				setInThread("DEBUG.TIME", startTime);

				// save the description, if set
				if (description != null)
				{
					setInThread("DEBUG.DESC", description);
				}

				// collect any special debug information
				setInThread("DEBUG", new StringBuffer());
			}
		}
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/component/src/java/org/sakaiproject/component/framework/current/BasicCurrentService.java,v 1.9 2005/02/09 18:17:10 ggolden.umich.edu Exp $
*
**********************************************************************************/
