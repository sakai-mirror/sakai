/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/resource/ReferenceVector.java,v 1.1 2005/05/12 15:45:32 ggolden.umich.edu Exp $
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

// package
package org.sakaiproject.service.legacy.resource;

// imports
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

/**
* <p>ReferenceVector is a Vector of References, just like Vector with the added contains(Reference) member.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public class ReferenceVector
	extends Vector
{
	/**
	* Constructor.
	*/
	public ReferenceVector(int initialCapacity, int capacityIncrement) { super(initialCapacity, capacityIncrement); }

	/**
	* Constructor.
	*/
	public ReferenceVector(int initialCapacity) { super(initialCapacity); }

	/**
	* Constructor.
	*/
	public ReferenceVector(Collection c) { super(c); }

	/**
	* Constructor.
	*/
	public ReferenceVector() { super(); }

	/**
	* Is this resource referred to in any of the references in the vector?
	* Accept any Resource, or a String assumed to be a resource reference.
	* @param o The Resource (or resource reference string) to check for presence in the references in the vector.
	* @return true if the resource referred to in any of the references in the Vector, false if not.
	*/
	public boolean contains(Object o)
	{
		if ((o instanceof Resource) || (o instanceof String) || (o instanceof Reference)) 
		{
			String ref = null;
			if (o instanceof Resource)
			{
				ref = ((Resource) o).getReference();
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

		else return super.contains(o);

	}	// contains

	/**
	* Removes the first occurrence of the specified element in this Vector.
	* If the element is a String, treat it as a resource reference, else it's a Reference object.
	* @return true if the element was found, false if not.
	*/
	public boolean remove(Object o)
	{
		//  if a string, treat as a resource reference
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

	}	// remove

}	// ReferenceVector

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/resource/ReferenceVector.java,v 1.1 2005/05/12 15:45:32 ggolden.umich.edu Exp $
*
**********************************************************************************/
