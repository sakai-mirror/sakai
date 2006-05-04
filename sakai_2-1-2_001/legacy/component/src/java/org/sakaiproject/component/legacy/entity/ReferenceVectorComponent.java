/**********************************************************************************
 * $URL$
 * $Id$
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

// package
package org.sakaiproject.component.legacy.entity;

// imports
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.entity.Reference;

/**
 * <p>
 * ReferenceVectorComponent implements the ReferenceVector API.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public class ReferenceVectorComponent extends Vector
{
	/**
	 * Constructor.
	 */
	public ReferenceVectorComponent(int initialCapacity, int capacityIncrement)
	{
		super(initialCapacity, capacityIncrement);
	}

	/**
	 * Constructor.
	 */
	public ReferenceVectorComponent(int initialCapacity)
	{
		super(initialCapacity);
	}

	/**
	 * Constructor.
	 */
	public ReferenceVectorComponent(Collection c)
	{
		super(c);
	}

	/**
	 * Constructor.
	 */
	public ReferenceVectorComponent()
	{
		super();
	}

	/**
	 * Is this resource referred to in any of the references in the vector? Accept any Resource, or a String assumed to be a resource reference.
	 * 
	 * @param o
	 *        The Resource (or resource reference string) to check for presence in the references in the vector.
	 * @return true if the resource referred to in any of the references in the Vector, false if not.
	 */
	public boolean contains(Object o)
	{
		if ((o instanceof Entity) || (o instanceof String) || (o instanceof Reference))
		{
			String ref = null;
			if (o instanceof Entity)
			{
				ref = ((Entity) o).getReference();
			}
			else if (o instanceof String)
			{
				ref = (String) o;
			}
			else
			{
				ref = ((Reference) o).getReference();
			}

			Iterator it = iterator();
			while (it.hasNext())
			{
				Reference de = (Reference) it.next();
				if (de.getReference().equals(ref)) return true;
			}

			return false;
		}

		else
			return super.contains(o);
	}

	/**
	 * Removes the first occurrence of the specified element in this Vector. If the element is a String, treat it as a resource reference, else it's a Reference object.
	 * 
	 * @return true if the element was found, false if not.
	 */
	public boolean remove(Object o)
	{
		// if a string, treat as a resource reference
		if (o instanceof String)
		{
			String ref = (String) o;
			Iterator it = iterator();
			while (it.hasNext())
			{
				Reference de = (Reference) it.next();
				if (de.getReference().equals(ref))
				{
					return super.remove(de);
				}
			}

			return false;
		}

		return super.remove(o);
	}
}
