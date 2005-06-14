/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/site/ResourceVector.java,v 1.5 2004/06/22 03:14:01 ggolden Exp $
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
package org.sakaiproject.component.legacy.site;

// imports
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.sakaiproject.service.legacy.resource.Resource;

/**
* <p>ResourceVector is a Vector of Resources....</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.5 $
*/
public class ResourceVector
	extends Vector
{
	/**
	* Constructor.
	*/
	public ResourceVector(int initialCapacity, int capacityIncrement) { super(initialCapacity, capacityIncrement); }

	/**
	* Constructor.
	*/
	public ResourceVector(int initialCapacity) { super(initialCapacity); }

	/**
	* Constructor.
	*/
	public ResourceVector(Collection c) { super(c); }

	/**
	* Constructor.
	*/
	public ResourceVector() { super(); }

	/**
	* Find the first item with this Resource id.
	* @param id The resource id.
	* @return the Resource that has this id, first in the list.
	*/
	public Resource getById(String id)
	{
		for (Iterator i = iterator(); i.hasNext();)
		{
			Resource r = (Resource) i.next();
			if (r.getId().equals(id)) return r;
		}
		
		return null;

	}	// getById

	/**
	* Move an entry one up towards the start of the list.
	* @param entry The resource to move.
	*/
	public void moveUp(Resource entry)
	{
		int pos = indexOf(entry);
		if (pos == -1) return;
		if (pos == 0) return;
		remove(entry);
		add(pos-1, entry);

	}	// moveUp

	/**
	* Move an entry one down towards the end of the list.
	* @param entry The resource to move.
	*/
	public void moveDown(Resource entry)
	{
		int pos = indexOf(entry);
		if (pos == -1) return;
		if (pos == size()-1) return;
		remove(entry);
		add(pos+1, entry);

	}	// moveDown

}	// ResourceVector

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/site/ResourceVector.java,v 1.5 2004/06/22 03:14:01 ggolden Exp $
*
**********************************************************************************/
