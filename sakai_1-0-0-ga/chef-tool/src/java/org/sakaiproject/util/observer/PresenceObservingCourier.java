/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/util/observer/PresenceObservingCourier.java,v 1.6 2004/06/22 03:05:00 ggolden Exp $
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
package org.sakaiproject.util.observer;

// imports
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.presence.cover.PresenceService;

/**
* <p>PresenceObservingCourier is an EventObservingCourier which watches for only presence service events
* at a particular location.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.6 $
*/
public class PresenceObservingCourier
	extends EventObservingCourier
{
	/** Do we do exact or startsWith checks on the location? */
	protected boolean m_exact = true;

	/**
	* Construct.
	* @param deliveryId The key identifying the Portal Page Instance.
	* @param elementId The key identifying the element on the Portal Page that would
	* need a courier delivered message when things change.
	* @param location The location changes to which are observed.
	* @param exact If true, the location is a full location string, and we look for activity in that
	* exact location onlyl if false, we look for activity that starts with the location value.
	*/
	public PresenceObservingCourier(String deliveryId, String elementId,
					String location, boolean exact)
	{
		super(deliveryId, elementId,
			PresenceService.presenceReference(location));

		m_exact = exact;

	}	// PresenceObservingCourier

	/**
	* Check to see if we want to process or ignore this update.
	* @param arg The arg from the update.
	* @return true to continue, false to quit.
	*/
	protected boolean check(Object arg)
	{
		// arg is Event
		if (!(arg instanceof Event)) return false;
		Event event = (Event) arg;
		String key = event.getResource();

		// reject non presence events
		String function = event.getEvent();
		if (!(function.equals(PresenceService.EVENT_PRESENCE) || function.equals(PresenceService.EVENT_ABSENCE))) return false;

		// look for exact matches to the pattern
		if (m_resourcePattern != null)
		{
			// exact or startsWith?
			if (m_exact)
			{
				// if this resource is not my exact pattern of resources, we can ignore it
				if (!key.equals(m_resourcePattern)) return false;
			}
			else
			{
				// if this resource is not in my pattern of resources, we can ignore it
				if (!key.startsWith(m_resourcePattern)) return false;
			}
		}

		if (m_logger.isDebugEnabled())
			m_logger.debug(this + ".update() [" + m_resourcePattern + "] resource: " + key + " event: " + event.getEvent());

		return true;

	}	// check
	
}	// PresenceObservingCourier

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/util/observer/PresenceObservingCourier.java,v 1.6 2004/06/22 03:05:00 ggolden Exp $
*
**********************************************************************************/
