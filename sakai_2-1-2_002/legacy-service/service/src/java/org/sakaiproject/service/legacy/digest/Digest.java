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
package org.sakaiproject.service.legacy.digest;

// imports
import java.util.List;

import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.time.Time;

/**
* <p>Digest stores sets of messages for a given user (id).</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public interface Digest
	extends Entity, Comparable
{
	/**
	* Access the (user) id for this digest.
	* @return The (user) id for this digest.
	*/
	public String getId();

	/**
	* Access the list (DigestMessage) of messages, for the time period.
	* @param period A time in the time period to select.
	* @return The List (DigestMessage) of messages (possibly empty).
	*/
	public List getMessages(Time period);

	/**
	* Access the list (String, TimePeriod string) of periods.
	* @return The List (String, TimePeriod string) of periods.
	*/
	public List getPeriods();

}   // Digest



