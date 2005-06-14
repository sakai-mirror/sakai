/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/discussion/DiscussionService.java,v 1.1 2005/05/12 15:45:34 ggolden.umich.edu Exp $
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
package org.sakaiproject.service.legacy.discussion;

// import
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.legacy.message.MessageService;
import org.sakaiproject.service.legacy.resource.Resource;

/**
* <p>DiscussionService is the extension to MessageService configured for Discussions.</p>
* <p>MessageChannels are DiscussionMessageChannels, and Messages are
* DiscussionMessages with DiscussionMessageHeaders.</p>
* <p>Security in the discussion service, in addition to that defined in the channels,
* include:<ul>
* <li>discussion.channel.add</li></ul></p>
* <li>discussion.channel.remove</li></ul></p>
* <p>Usage Events are generated:<ul>
* <li>discussion.channel.add - discussion channel resource id</li>
* <li>discussion.channel.remove - discussion channel resource id</li></ul></p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.1 $
* @see org.chefproject.core.DiscussionChannel
*/
public interface DiscussionService
	extends MessageService
{
	/** This string can be used to find the service in the service manager. */
	public static final String SERVICE_NAME = DiscussionService.class.getName();

	/** This string starts the references to resources in this service. */
	public static final String REFERENCE_ROOT = Resource.SEPARATOR + "discussion";

	/** Security lock for posting topic messages to a channel. */
	public static final String SECURE_ADD_TOPIC = "new.topic";

	/**
	* A (DiscussionChannel) cover for getChannel() to return a specific discussion channel.
	* @param ref The channel reference.
	* @return the DiscussionChannel that has the specified name.
	* @exception IdUnusedException If this name is not defined for a discussion channel.
	* @exception PermissionException If the user does not have any permissions to the channel.
	*/
	public DiscussionChannel getDiscussionChannel(String ref)
		throws IdUnusedException, PermissionException;

	/**
	* A (DiscussionChannel) cover for addChannel() to add a new discussion channel.
	* @param ref The channel reference.
	* @return The newly created channel.
	* @exception IdUsedException if the id is not unique.
	* @exception IdInvalidException if the id is not made up of valid characters.
	* @exception PermissionException if the user does not have permission to add a channel.
	*/
	public DiscussionChannelEdit addDiscussionChannel(String ref)
		throws IdUsedException, IdInvalidException, PermissionException;

}	// DiscussionService

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/discussion/DiscussionService.java,v 1.1 2005/05/12 15:45:34 ggolden.umich.edu Exp $
*
**********************************************************************************/
