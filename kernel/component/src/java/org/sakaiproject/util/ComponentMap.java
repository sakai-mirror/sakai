/**********************************************************************************
 *
 * $Header: /cvs/sakai2/kernel/component/src/java/org/sakaiproject/util/ComponentMap.java,v 1.3 2005/03/07 03:15:26 ggolden.umich.edu Exp $
 *
 ***********************************************************************************
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

package org.sakaiproject.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * ComponentMap exposes the registered components as a map - the component id is mapped to the component implementation.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.3 $
 */
public class ComponentMap implements Map
{
	/**
	 * @inheritDoc
	 */
	public int size()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @inheritDoc
	 */
	public boolean isEmpty()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @inheritDoc
	 */
	public boolean containsKey(Object arg0)
	{
		return org.sakaiproject.api.kernel.component.cover.ComponentManager
				.contains((String) arg0);
	}

	/**
	 * @inheritDoc
	 */
	public boolean containsValue(Object arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @inheritDoc
	 */
	public Object get(Object arg0)
	{
		return org.sakaiproject.api.kernel.component.cover.ComponentManager
				.get((String) arg0);
	}

	/**
	 * @inheritDoc
	 */
	public Object put(Object arg0, Object arg1)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @inheritDoc
	 */
	public Object remove(Object arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @inheritDoc
	 */
	public void putAll(Map arg0)
	{
		// TODO Auto-generated method stub
	}

	/**
	 * @inheritDoc
	 */
	public void clear()
	{
		// TODO Auto-generated method stub
	}

	/**
	 * @inheritDoc
	 */
	public Set keySet()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @inheritDoc
	 */
	public Collection values()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @inheritDoc
	 */
	public Set entrySet()
	{
		// TODO Auto-generated method stub
		return null;
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/kernel/component/src/java/org/sakaiproject/util/ComponentMap.java,v 1.3 2005/03/07 03:15:26 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
