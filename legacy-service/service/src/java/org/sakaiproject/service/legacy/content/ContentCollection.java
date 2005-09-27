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
package org.sakaiproject.service.legacy.content;

// import
import java.util.List;

import org.sakaiproject.service.legacy.resource.Entity;

/**
* <p>ContentCollection is the core interface for a Collection object in the GenericContentHostingService.</p>
* <p>A Collection has a list of internal members, each a resource id.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public interface ContentCollection
	extends Entity
{
	/**
	* Access a List of the collection's internal members, each a resource id String.
	* @return a List of the collection's internal members, each a resource id String (may be empty).
	*/
	public List getMembers();

	/**
	* Access a List of the collections' internal members as full ContentResource or
	* ContentCollection objects.
	* @return a List of the full objects of the members of the collection.
	*/
	public List getMemberResources();

	/**
	* Access the size of all the resource body bytes within this collection in Kbytes.
	* @return The size of all the resource body bytes within this collection in Kbytes.
	*/
	public long getBodySizeK();

}	// ContentCollection



