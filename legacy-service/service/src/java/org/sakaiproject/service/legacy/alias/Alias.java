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
package org.sakaiproject.service.legacy.alias;

import org.sakaiproject.service.legacy.resource.Resource;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.user.User;

// imports

/**
* <p>Alias is the core interface for the CHEF Alias object.</p>
* <p>Alias objects hold everything we know about CHEF aliass (except for
* their authentication password).</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
* @see org.chefproject.service.generic.GenericAliasDirectoryService
*/
public interface Alias
	extends Resource, Comparable
{
	// TODO: move these to Resource
	
	/**
	 * @return the user who created this.
	 */
	public User getCreatedBy();

	/**
	 * @return the user who last modified this.
	 */
	public User getModifiedBy();

	/**
	 * @return the time created.
	 */
	public Time getCreatedTime();

	/**
	 * @return the time last modified.
	 */
	public Time getModifiedTime();
	
	// TODO:

	/**
	* Access the alias target.
	* @return The alias target.
	*/
	public String getTarget();

	/**
	* @return a description of the item this alias's target applies to.
	*/
	public String getDescription();

}   // Alias



