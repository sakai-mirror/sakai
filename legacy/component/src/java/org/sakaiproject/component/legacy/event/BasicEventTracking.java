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
package org.sakaiproject.component.legacy.event;

// imports
import org.sakaiproject.service.legacy.event.Event;

/**
* <p>BasicEventTracking is a basic implementation of the EventTracking CHEF event tracking service.</p>
* <p>Events are just logged, and observers notified.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public class BasicEventTracking
	extends BaseEventTrackingService
{
	/** String used to identify this service in the logs */
	protected static String m_logId = "EventTracking: ";

	/*******************************************************************************
	* Event post / flow
	*******************************************************************************/

	/**
	* Cause this new event to get to wherever it has to go for persistence, etc.
	* @param event The new event to post.
	*/
	protected void postEvent(Event event)
	{
		String reportId = null;
		if (event.getSessionId() != null)
		{
			reportId = event.getSessionId();
		}
		else
		{
			reportId = "~" + event.getUserId();
		}

		m_logger.info(m_logId + reportId + "@" + event);

		// notify observers, sending the event
		notifyObservers(event, true);

	}	// postEvent

}	// BasicEventTracking



