/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/digest/BaseDigestService.java,v 1.2 2005/05/12 01:38:27 ggolden.umich.edu Exp $
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

// package
package org.sakaiproject.component.legacy.digest;

// imports
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import java.util.ResourceBundle;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.email.cover.EmailService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.memory.MemoryService;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.framework.session.SessionStateBindingListener;
import org.sakaiproject.service.legacy.digest.Digest;
import org.sakaiproject.service.legacy.digest.DigestEdit;
import org.sakaiproject.service.legacy.digest.DigestMessage;
import org.sakaiproject.service.legacy.digest.DigestService;
import org.sakaiproject.service.legacy.event.cover.EventTrackingService;
import org.sakaiproject.service.legacy.resource.Edit;
import org.sakaiproject.service.legacy.resource.Resource;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.resource.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.security.cover.SecurityService;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.TimeBreakdown;
import org.sakaiproject.service.legacy.time.TimeRange;
import org.sakaiproject.service.legacy.time.TimeService;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.util.xml.Xml;
import org.sakaiproject.util.resource.BaseResourcePropertiesEdit;
import org.sakaiproject.util.storage.StorageUser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
* <p>BaseDigestService is the base service for DigestService.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.2 $
*/
public abstract class BaseDigestService
	implements DigestService, StorageUser, Runnable
{
	private ResourceBundle rb = ResourceBundle.getBundle("digest");
	
	/** Storage manager for this service. */
	protected Storage m_storage = null;

	/** The initial portion of a relative access point URL. */
	protected String m_relativeAccessPoint = null;

	/** The queue of digests waiting to be added (DigestMessage). */
	protected List m_digestQueue = new Vector();

	/** The thread I run my periodic clean and report on. */
	protected Thread m_thread = null;

	/** My thread's quit flag. */
	protected boolean m_threadStop = false;

	/** How long to wait between runnable runs (ms). */
	protected static final long PERIOD = 1000;

	/** True if we are in the mode of sending out digests, false if we are waiting. */
	protected boolean m_sendDigests = true;

	/** The time period last time the sendDigests() was called. */
	protected String m_lastSendPeriod = null;

	/*******************************************************************************
	* Runnable
	*******************************************************************************/

	/**
	* Start the clean and report thread.
	*/
	protected void start()
	{
		m_threadStop = false;

		m_thread = new Thread(this, getClass().getName());
		m_thread.start();
		
	}	// start

	/**
	* Stop the clean and report thread.
	*/
	protected void stop()
	{
		if (m_thread == null) return;

		// signal the thread to stop
		m_threadStop = true;

		// wake up the thread
		m_thread.interrupt();

		m_thread = null;

	}	// stop

	/**
	* Run the clean and report thread.
	*/
	public void run()
	{
		// loop till told to stop
		while (		(!m_threadStop)
				&&	(!Thread.currentThread().isInterrupted()))
		{
			try
			{
				// process the queue of digest requests
				processQueue();

				// check for a digest mailing time
				sendDigests();
			}
			catch (Throwable e) {m_logger.warn(this + ": exception: ", e);}

			// take a small nap
			try
			{
				Thread.sleep(PERIOD);
			}
			catch (Throwable ignore) {}

		}	// while

	}	// run

	/**
	* Attempt to process all the queued digest requests.
	* Ones that cannot be processed now will be returned to the queue.
	*/
	protected void processQueue()
	{
		// setup a re-try queue
		List retry = new Vector();

		// grab the queue - any new stuff will be processed next time
		List queue = new Vector();
		synchronized (m_digestQueue)
		{
			queue.addAll(m_digestQueue);
			m_digestQueue.clear();
		}

		for (Iterator iQueue = queue.iterator(); iQueue.hasNext();)
		{
			DigestMessage message = (DigestMessage) iQueue.next();
			try
			{
				DigestEdit edit = edit(message.getTo());
				edit.add(message);
				commit(edit);
				// %%% could do this by pulling all for id from the queue in one commit -ggolden
			}
			catch (InUseException e)
			{
				// retry next time
				retry.add(message);
			}
		}

		// requeue the retrys
		if (retry.size() > 0)
		{
			synchronized (m_digestQueue)
			{
				m_digestQueue.addAll(retry);
			}
		}

	}	// processQueue

	/**
	* If it's time, send out any digested messages.
	* Send once daily, after a certiain time of day (local time).
	*/
	protected void sendDigests()
	{
		// compute the current period
		String curPeriod = computeRange(m_timeService.newTime()).toString();

		// if we are in a new period, start sending again
		if (!curPeriod.equals(m_lastSendPeriod))
		{
			m_sendDigests = true;

			// remember this period for next check
			m_lastSendPeriod = curPeriod;
		}
	
		// if we are not sending, early out
		if (!m_sendDigests) return;

		if (m_logger.isDebugEnabled())
			m_logger.debug(this + " checking for sending digests");

		// count send candidate digests
		int count = 0;

		// process each digest
		List digests = getDigests();
		for (Iterator iDigests = digests.iterator(); iDigests.hasNext();)
		{
			Digest digest = (Digest) iDigests.next();

			// see if this one has any prior periods
			List periods = digest.getPeriods();
			if (periods.size() == 0) continue;

			boolean found = false;
			for (Iterator iPeriods = periods.iterator(); iPeriods.hasNext();)
			{
				String period = (String) iPeriods.next();
				if (!curPeriod.equals(period))
				{
					found = true;
					break;
				}
			}
			if (!found) continue;

			// this digest is a send candidate
			count++;

			// get a lock
			DigestEdit edit = null;
			try
			{
				boolean changed = false;
				edit = edit(digest.getId());
				
				// process each non-current period
				for (Iterator iPeriods = edit.getPeriods().iterator(); iPeriods.hasNext();)
				{
					String period = (String) iPeriods.next();

					// process if it's not the current period
					if (!curPeriod.equals(period))
					{
						TimeRange periodRange = m_timeService.newTimeRange(period);
						Time timeInPeriod = periodRange.firstTime();

						// any messages?
						List msgs = edit.getMessages(timeInPeriod);
						if (msgs.size() > 0)
						{
							// send this one
							send(edit.getId(), msgs, periodRange);
						}

						// clear this period
						edit.clear(timeInPeriod);
						
						changed = true;
					}
				}

				// commit, release the lock
				if (changed)
				{
					//  delete it if empty
					if (edit.getPeriods().size() == 0)
					{
						remove(edit);
					}
					else
					{
						commit(edit);
					}
					edit = null;
				}
				else
				{
					cancel(edit);
					edit = null;
				}
			}
			// if in use, missing, whatever, skip on
			catch (Throwable any) {}
			finally
			{
				if (edit != null)
				{
					cancel(edit);
					edit = null;
				}
			}

		}	// for (Iterator iDigests = digests.iterator(); iDigests.hasNext();)

		// if we didn't see any send candidates, we will stop sending till next period
		if (count == 0)
		{
			m_sendDigests = false;
		}

	}	// sendDigests

	/**
	* Send a single digest message
	* @param id The use id to send the message to.
	* @param msgs The List (DigestMessage) of message to digest.
	* @param period The time period of the digested messages.
	*/
	protected void send(String id, List msgs, TimeRange period)
	{
		// sanity check
		if (msgs.size() == 0) return;

		try
		{
			String to = UserDirectoryService.getUser(id).getEmail();
			
			// if use has no email address we can't send it
			if ((to == null) || (to.length() == 0)) return;

			String from = "postmaster@" + ServerConfigurationService.getServerName();
			String subject = ServerConfigurationService.getString("ui.service", "Sakai")
					+ " "+ rb.getString("notif")+ " " + period.firstTime().toStringLocalDate();

			StringBuffer body = new StringBuffer();
			body.append(subject);
			body.append("\n\n");

			// toc
			int count = 1;
			for (Iterator iMsgs = msgs.iterator(); iMsgs.hasNext();)
			{
				DigestMessage msg = (DigestMessage) iMsgs.next();

				body.append(Integer.toString(count));
				body.append(".  ");
				body.append(msg.getSubject());
				body.append("\n");
				count++;
			}
			body.append("\n" + rb.getString("separ")+ "\n\n");

			// for each msg
			count = 1;
			for (Iterator iMsgs = msgs.iterator(); iMsgs.hasNext();)
			{
				DigestMessage msg = (DigestMessage) iMsgs.next();

				// repeate toc entry
				body.append(Integer.toString(count));
				body.append(".  ");
				body.append(msg.getSubject());
				body.append("\n\n");

				// message body
				body.append(msg.getBody());

				body.append("\n" + rb.getString("separ") + "\n\n");
				count++;
			}

			// tag
			body.append(rb.getString("thiaut") + " "
						+ ServerConfigurationService.getString("ui.service","Sakai")
						+ " " + rb.getString("par1")
						+ ServerConfigurationService.getServerUrl()
						+ rb.getString("par2") + "\n"
						+ rb.getString("youcan") + "\n");

			if (m_logger.isDebugEnabled())
				m_logger.debug(this + " sending digest email to: " + to);

			EmailService.send(from, to, subject, body.toString(), to, null, null);
		}
		catch (Throwable any)
		{
			m_logger.warn(this + ".send: digest to: " + id + " not sent: " + any.toString());
		}

	}	// send

	/*******************************************************************************
	* Abstractions, etc.
	*******************************************************************************/

	/**
	* Construct storage for this service.
	*/
	protected abstract Storage newStorage();

	/**
	* Access the partial URL that forms the root of resource URLs.
	* @param relative if true, form within the access path only (i.e. starting with /content)
	* @return the partial URL that forms the root of resource URLs.
	*/
	protected String getAccessPoint(boolean relative)
	{
		return (relative ? "" : ServerConfigurationService.getAccessUrl()) + m_relativeAccessPoint;

	}   // getAccessPoint

	/**
	* Access the internal reference which can be used to access the resource from within the system.
	* @param id The digest id string.
	* @return The the internal reference which can be used to access the resource from within the system.
	*/
	public String digestReference(String id)
	{
		return getAccessPoint(true) + Resource.SEPARATOR + id;

	}   // digestReference

	/**
	* Access the digest id extracted from a digest reference.
	* @param ref The digest reference string.
	* @return The the digest id extracted from a digest reference.
	*/
	protected String digestId(String ref)
	{
		String start = getAccessPoint(true) + Resource.SEPARATOR;
		int i = ref.indexOf(start);
		if (i == -1) return ref;
		String id = ref.substring(i+start.length());
		return id;

	}   // digestId

	/**
	* Check security permission.
	* @param lock The lock id string.
	* @param resource The resource reference string, or null if no resource is involved.
	* @return true if allowd, false if not
	*/
	protected boolean unlockCheck(String lock, String resource)
	{
		if (!SecurityService.unlock(lock, resource))
		{
			return false;
		}

		return true;

	}	// unlockCheck

	/**
	* Check security permission.
	* @param lock The lock id string.
	* @param resource The resource reference string, or null if no resource is involved.
	* @exception PermissionException Thrown if the user does not have access
	*/
	protected void unlock(String lock, String resource)
		throws PermissionException
	{
		if (!unlockCheck(lock, resource))
		{
			throw new PermissionException(UsageSessionService.getSessionUserId(), lock, resource);
		}

	}	// unlock

	/*******************************************************************************
	* Dependencies and their setter methods
	*******************************************************************************/

	/** Dependency: logging service */
	protected Logger m_logger = null;

	/**
	 * Dependency: logging service.
	 * @param service The logging service.
	 */
	public void setLogger(Logger service)
	{
		m_logger = service;
	}

	/** Dependency: MemoryService. */
	protected MemoryService m_memoryService = null;

	/**
	 * Dependency: MemoryService.
	 * @param service The MemoryService.
	 */
	public void setMemoryService(MemoryService service)
	{
		m_memoryService = service;
	}

	/** Dependency: TimeService. */
	protected TimeService m_timeService = null;

	/**
	 * Dependency: TimeService.
	 * @param service The TimeService.
	 */
	public void setTimeService(TimeService service)
	{
		m_timeService = service;
	}

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		m_relativeAccessPoint = REFERENCE_ROOT;

		// construct storage and read
		m_storage = newStorage();
		m_storage.open();

		// setup the queue
		m_digestQueue.clear();

		start();

		m_logger.info(this + ".init()");

	}   // init

	/**
	* Returns to uninitialized state.
	*/
	public void destroy()
	{
		stop();

		m_storage.close();
		m_storage = null;

		if (m_digestQueue.size() > 0)
		{
			m_logger.warn(this + ".shutdown: with items in digest queue"); // %%%
		}
		m_digestQueue.clear();

		m_logger.info(this + ".destroy()");
	}

	/*******************************************************************************
	* DigestService implementation
	*******************************************************************************/

	/**
	* Access digest associated with this id.
	* @param id The digest id.
	* @return The Digest object.
	* @exception IdUnusedException if there is not digest object with this id.
	*/
	public Digest getDigest(String id)
		throws IdUnusedException
	{
		Digest digest = findDigest(id);
		if (digest == null) throw new IdUnusedException(id);

		return digest;

	}	// getDigest

	/**
	* Access all digest objects.
	* @return A List (Digest) of all defined digests.
	*/
	public List getDigests()
	{
		List digests = m_storage.getAll();

		return digests;

	}   // getDigests

	/**
	* Add a new message to a digest, creating one if needed.
	* This returns right away; the digest will be added as soon as possible.
	* @param message The message to digest.
	*/
	public void digest(DigestMessage message)
	{
		// queue this for digesting
		synchronized (m_digestQueue)
		{
			m_digestQueue.add(message);
		}

	}	// digest

	/**
	* Get a locked Digest object for editing. May be new.
	* Must commit(), cancel() or remove() when done.
	* @param id The digest id.
	* @return A DigestEdit object for editing.
	* @exception InUseException if the digest object is locked by someone else.
	*/
	public DigestEdit edit(String id)
		throws InUseException
	{
		// security
		// unlock(SECURE_EDIT_DIGEST, digestReference(id));

		// one add/edit at a time, please, to make sync. only one digest per user
		// TODO: I don't link sync... could just do the add and let it fail if it already exists -ggolden
		synchronized (m_storage)
		{
			// check for existance
			if (!m_storage.check(id))
			{
				try
				{
					return add(id);
				}
				catch (IdUsedException e ) { m_logger.warn(this + ".edit: from the add: " + e); }
			}

			// ignore the cache - get the user with a lock from the info store
			DigestEdit edit = m_storage.edit(id);
			if (edit == null) throw new InUseException(id);
	
			((BaseDigest) edit).setEvent(SECURE_EDIT_DIGEST);
	
			return edit;
		}

	}	// edit

	/**
	* Commit the changes made to a DigestEdit object, and release the lock.
	* The DigestEdit is disabled, and not to be used after this call.
	* @param user The DigestEdit object to commit.
	*/
	public void commit(DigestEdit edit)
	{
		// check for closed edit
		if (!edit.isActiveEdit())
		{
			try { throw new Exception(); }
			catch (Exception e) { m_logger.warn(this + ".commit(): closed DigestEdit", e); }
			return;
		}

		// update the properties
		// addLiveUpdateProperties(user.getPropertiesEdit());

		// complete the edit
		m_storage.commit(edit);

		// track it
		EventTrackingService.post(
			EventTrackingService.newEvent(((BaseDigest) edit).getEvent(), edit.getReference(), true));

		// close the edit object
		((BaseDigest) edit).closeEdit();

	}	// commit

	/**
	* Cancel the changes made to a DigestEdit object, and release the lock.
	* The DigestEdit is disabled, and not to be used after this call.
	* @param user The DigestEdit object to commit.
	*/
	public void cancel(DigestEdit edit)
	{
		// check for closed edit
		if (!edit.isActiveEdit())
		{
			try { throw new Exception(); }
			catch (Exception e) { m_logger.warn(this + ".cancel(): closed DigestEdit", e); }
			return;
		}

		// release the edit lock
		m_storage.cancel(edit);

		// close the edit object
		((BaseDigest) edit).closeEdit();

	}	// cancel

	/**
	* Remove this DigestEdit - it must be locked from edit().
	* The DigestEdit is disabled, and not to be used after this call.
	* @param user The DigestEdit object to remove.
	*/
	public void remove(DigestEdit edit)
	{
		// check for closed edit
		if (!edit.isActiveEdit())
		{
			try { throw new Exception(); }
			catch (Exception e) { m_logger.warn(this + ".remove(): closed DigestEdit", e); }
			return;
		}

		// complete the edit
		m_storage.remove(edit);

		// track it
		EventTrackingService.post(
			EventTrackingService.newEvent(SECURE_REMOVE_DIGEST, edit.getReference(), true));

		// close the edit object
		((BaseDigest) edit).closeEdit();

	}	// remove

	/**
	* Find the digest object, in cache or storage.
	* @param id The digest id.
	* @return The digest object found in cache or storage, or null if not found.
	*/
	protected BaseDigest findDigest(String id)
	{
		BaseDigest digest = (BaseDigest) m_storage.get(id);			

		return digest;

	}	// findDigest

	/**
	* Add a new set of digest with this id.  Must commit(), remove() or cancel() when done.
	* @param id The digest id.
	* @return A new DigestEdit object for editing.
	* @exception IdUsedException if these digest already exist.
	*/
	public DigestEdit add(String id)
		throws IdUsedException
	{
		// check security (throws if not permitted)
		// unlock(SECURE_ADD_DIGEST, digestReference(id));

		// one add/edit at a time, please, to make sync. only one digest per user
		synchronized (m_storage)
		{
			// reserve a user with this id from the info store - if it's in use, this will return null
			DigestEdit edit = m_storage.put(id);
			if (edit == null)
			{
				throw new IdUsedException(id);
			}
	
			return edit;
		}

	}   // add

	/*******************************************************************************
	* Digest implementation
	*******************************************************************************/

	public class BaseDigest
		implements DigestEdit, SessionStateBindingListener
	{
		/** The user id. */
		protected String  m_id = null;

		/** The properties. */
		protected ResourcePropertiesEdit m_properties = null;

		/** The digest time ranges (Map TimeRange string to List of DigestMessage). */
		protected Map m_ranges = null;

		/**
		* Construct.
		* @param id The user id.
		*/
		public BaseDigest(String id)
		{
			m_id = id;

			// setup for properties
			ResourcePropertiesEdit props = new BaseResourcePropertiesEdit();
			m_properties = props;

			// setup for ranges
			m_ranges = new Hashtable();

			// if the id is not null (a new user, rather than a reconstruction)
			// and not the anon (id == "") user,
			// add the automatic (live) properties
			// %%% if ((m_id != null) && (m_id.length() > 0)) addLiveProperties(props);

		}   // BaseDigest

		/**
		* Construct from another Digest object.
		* @param user The user object to use for values.
		*/
		public BaseDigest(Digest digest)
		{
			setAll(digest);

		}	// BaseDigest

		/**
		* Construct from information in XML.
		* @param el The XML DOM Element definining the user.
		*/
		public BaseDigest(Element el)
		{
			// setup for properties
			m_properties = new BaseResourcePropertiesEdit();

			// setup for ranges
			m_ranges = new Hashtable();

			m_id = el.getAttribute("id");

			// the children (properties, messages)
			NodeList children = el.getChildNodes();
			final int length = children.getLength();
			for(int i = 0; i < length; i++)
			{
				Node child = children.item(i);
				if (child.getNodeType() != Node.ELEMENT_NODE) continue;
				Element element = (Element)child;

				// look for properties
				if (element.getTagName().equals("properties"))
				{
					// re-create properties
					m_properties = new BaseResourcePropertiesEdit(element);
				}
				
				// look for a messages
				else if (element.getTagName().equals("messages"))
				{
					String period = element.getAttribute("period");

					// find the range
					List msgs = (List) m_ranges.get(period);
					if (msgs == null)
					{
						msgs = new Vector();
						m_ranges.put(period, msgs);
					}

					// do these children for messages
					NodeList msgChildren = element.getChildNodes();
					final int msgChildrenLen = msgChildren.getLength();
					for(int m = 0; m < msgChildrenLen; m++)
					{
						Node msgChild = msgChildren.item(m);
						if (msgChild.getNodeType() != Node.ELEMENT_NODE) continue;
						Element msgChildEl = (Element)msgChild;

						if (msgChildEl.getTagName().equals("message"))
						{
							String subject = Xml.decodeAttribute(msgChildEl, "subject");
							String body = Xml.decodeAttribute(msgChildEl, "body");
							msgs.add(new DigestMessage(m_id, subject, body));
						}
					}
				}
			}

		}	// BaseDigest

		/**
		* Take all values from this object.
		* @param user The user object to take values from.
		*/
		protected void setAll(Digest digest)
		{
			m_id = digest.getId();

			m_properties = new BaseResourcePropertiesEdit();
			m_properties.addAll(digest.getProperties());

			m_ranges = new Hashtable();
			// %%% deep enough? -ggolden
			m_ranges.putAll(((BaseDigest) digest).m_ranges);

		}   // setAll

		/**
		* Serialize the resource into XML, adding an element to the doc under the top of the stack element.
		* @param doc The DOM doc to contain the XML (or null for a string return).
		* @param stack The DOM elements, the top of which is the containing element of the new "resource" element.
		* @return The newly added element.
		*/
		public Element toXml(Document doc, Stack stack)
		{
			Element digest = doc.createElement("digest");

			if (stack.isEmpty())
			{
				doc.appendChild(digest);
			}
			else
			{
				((Element)stack.peek()).appendChild(digest);
			}

			stack.push(digest);

			digest.setAttribute("id", getId());

			// properties
			m_properties.toXml(doc, stack);

			// for each message range
			for (Iterator it = m_ranges.entrySet().iterator(); it.hasNext();)
			{
				Map.Entry entry = (Map.Entry ) it.next();
				
				Element messages = doc.createElement("messages");
				digest.appendChild(messages);
				messages.setAttribute("period", (String) entry.getKey());
				
				// for each message
				for (Iterator iMsgs = ((List) entry.getValue()).iterator(); iMsgs.hasNext();)
				{
					DigestMessage msg = (DigestMessage) iMsgs.next();

					Element message = doc.createElement("message");
					messages.appendChild(message);
					Xml.encodeAttribute(message, "subject", msg.getSubject());
					Xml.encodeAttribute(message, "body", msg.getBody());
				}
			}

			stack.pop();

			return digest;

		}	// toXml

		/**
		* Access the user id.
		* @return The digest id.
		*/
		public String getId()
		{
			if (m_id == null) return "";
			return m_id;

		}   // getId

		/**
		* Access the URL which can be used to access the resource.
		* @return The URL which can be used to access the resource.
		*/
		public String getUrl()
		{
			return getAccessPoint(false) + m_id;

		}   // getUrl

		/**
		* Access the internal reference which can be used to access the resource from within the system.
		* @return The the internal reference which can be used to access the resource from within the system.
		*/
		public String getReference()
		{
			return digestReference(m_id);

		}   // getReference

		/**
		* Access the resources's properties.
		* @return The resources's properties.
		*/
		public ResourceProperties getProperties()
		{
			return m_properties;

		}   // getProperties

		/**
		* Access the list (DigestMessage) of messages, for the time period.
		* @param period A time in the time period to select.
		* @return The List (DigestMessage) of messages (possibly empty).
		*/
		public List getMessages(Time period)
		{
			synchronized (m_ranges)
			{
				// find the range
				String range = computeRange(period).toString();
				List msgs = (List) m_ranges.get(range);

				List rv = new Vector();
				if (msgs != null)
				{
					rv.addAll(msgs);
				}

				return rv;
			}

		}	// getMessages

		/**
		* Access the list (String, TimePeriod string) of periods.
		* @return The List (String, TimePeriod string) of periods.
		*/
		public List getPeriods()
		{
			synchronized (m_ranges)
			{
				List rv = new Vector();
				rv.addAll(m_ranges.keySet());
				
				return rv;
			}

		}	// getPeriods

		/**
		* Are these objects equal?  If they are both User objects, and they have
		* matching id's, they are.
		* @return true if they are equal, false if not.
		*/
		public boolean equals(Object obj)
		{
			if (!(obj instanceof Digest)) return false;
			return ((Digest)obj).getId().equals(getId());

		}   // equals

		/**
		* Make a hash code that reflects the equals() logic as well.
		* We want two objects, even if different instances, if they have the same id to hash the same.
		*/
		public int hashCode()
		{
			return getId().hashCode();

		}	// hashCode

		/**
		* Compare this object with the specified object for order.
		* @return A negative integer, zero, or a positive integer as this object is
		* less than, equal to, or greater than the specified object.
		*/
		public int compareTo(Object obj)
		{
			if (!(obj instanceof Digest)) throw new ClassCastException();

			// if the object are the same, say so
			if (obj == this) return 0;

			// sort based on (unique) id
			int compare = getId().compareTo(((Digest)obj).getId());

			return compare;

		}	// compareTo

		/*******************************************************************************
		* Edit implementation
		*******************************************************************************/

		/** The event code for this edit. */
		protected String m_event = null;

		/** Active flag. */
		protected boolean m_active = false;

		/**
		* Add another message, in the current time period.
		* @param msg The DigestMessage to add.
		*/
		public void add(DigestMessage msg)
		{
			synchronized (m_ranges)
			{
				// find the current range
				String range = computeRange(m_timeService.newTime()).toString();
				List msgs = (List) m_ranges.get(range);
				if (msgs == null)
				{
					msgs = new Vector();
					m_ranges.put(range, msgs);
				}
				msgs.add(msg);
			}

		}	// add

		/**
		* Clear all messages from a time period.
		* @param period a Time in the time period.
		*/
		public void clear(Time period)
		{
			synchronized (m_ranges)
			{
				// find the range
				String range = computeRange(period).toString();
				List msgs = (List) m_ranges.get(range);
				if (msgs != null)
				{
					m_ranges.remove(range);
				}
			}

		}	// clear

		/**
		* Clean up.
		*/
		protected void finalize()
		{
			// catch the case where an edit was made but never resolved
			if (m_active)
			{
				cancel(this);
			}

		}	// finalize

		/**
		* Take all values from this object.
		* @param user The user object to take values from.
		*/
		protected void set(Digest digest)
		{
			setAll(digest);

		}   // set

		/**
		* Access the event code for this edit.
		* @return The event code for this edit.
		*/
		protected String getEvent() { return m_event; }
	
		/**
		* Set the event code for this edit.
		* @param event The event code for this edit.
		*/
		protected void setEvent(String event) { m_event = event; }

		/**
		* Access the resource's properties for modification
		* @return The resource's properties.
		*/
		public ResourcePropertiesEdit getPropertiesEdit()
		{
			return m_properties;

		}	// getPropertiesEdit

		/**
		* Enable editing.
		*/
		protected void activate()
		{
			m_active = true;

		}	// activate

		/**
		* Check to see if the edit is still active, or has already been closed.
		* @return true if the edit is active, false if it's been closed.
		*/
		public boolean isActiveEdit()
		{
			return m_active;

		}	// isActiveEdit

		/**
		* Close the edit object - it cannot be used after this.
		*/
		protected void closeEdit()
		{
			m_active = false;

		}	// closeEdit

		/*******************************************************************************
		* SessionStateBindingListener implementation
		*******************************************************************************/
	
		/**
		* Accept notification that this object has been bound as a SessionState attribute.
		* @param sessionStateKey The id of the session state which holds the attribute.
		* @param attributeName The id of the attribute to which this object is now the value.
		*/
		public void valueBound(String sessionStateKey, String attributeName) {}
	
		/**
		* Accept notification that this object has been removed from a SessionState attribute.
		* @param sessionStateKey The id of the session state which held the attribute.
		* @param attributeName The id of the attribute to which this object was the value.
		*/
		public void valueUnbound(String sessionStateKey, String attributeName)
		{
			if (m_logger.isDebugEnabled())
				m_logger.debug(this + ".valueUnbound()");
	
			// catch the case where an edit was made but never resolved
			if (m_active)
			{
				cancel(this);
			}
	
		}	// valueUnbound
	
	}   // BaseDigest

	/*******************************************************************************
	* Storage
	*******************************************************************************/

	protected interface Storage
	{
		/**
		* Open.
		*/
		public void open();

		/**
		* Close.
		*/
		public void close();

		/**
		* Check if a digest by this id exists.
		* @param id The user id.
		* @return true if a digest for this id exists, false if not.
		*/
		public boolean check(String id);

		/**
		* Get the digest with this id, or null if not found.
		* @param id The digest id.
		* @return The digest with this id, or null if not found.
		*/
		public Digest get(String id);

		/**
		* Get all digests.
		* @return The list of all digests.
		*/
		public List getAll();

		/**
		* Add a new digest with this id.
		* @param id The digest id.
		* @return The locked Digest object with this id, or null if the id is in use.
		*/
		public DigestEdit put(String id);

		/**
		* Get a lock on the digest with this id, or null if a lock cannot be gotten.
		* @param id The digest id.
		* @return The locked Digest with this id, or null if this records cannot be locked.
		*/
		public DigestEdit edit(String id);

		/**
		* Commit the changes and release the lock.
		* @param user The edit to commit.
		*/
		public void commit(DigestEdit edit);

		/**
		* Cancel the changes and release the lock.
		* @param user The edit to commit.
		*/
		public void cancel(DigestEdit edit);

		/**
		* Remove this edit and release the lock.
		* @param user The edit to remove.
		*/
		public void remove(DigestEdit edit);

	}   // Storage

	/*******************************************************************************
	* StorageUser implementation (no container)
	*******************************************************************************/

	/**
	* Construct a new continer given just an id.
	* @param id The id for the new object.
	* @return The new containe Resource.
	*/
	public Resource newContainer(String ref) { return null; }

	/**
	* Construct a new container resource, from an XML element.
	* @param element The XML.
	* @return The new container resource.
	*/
	public Resource newContainer(Element element) { return null; }

	/**
	* Construct a new container resource, as a copy of another
	* @param other The other contianer to copy.
	* @return The new container resource.
	*/
	public Resource newContainer(Resource other) { return null; }

	/**
	* Construct a new resource given just an id.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param id The id for the new object.
	* @param others (options) array of objects to load into the Resource's fields.
	* @return The new resource.
	*/
	public Resource newResource(Resource container, String id, Object[] others)
	{ return new BaseDigest(id); }

	/**
	* Construct a new resource, from an XML element.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param element The XML.
	* @return The new resource from the XML.
	*/
	public Resource newResource(Resource container, Element element)
	{ return new BaseDigest(element); }

	/**
	* Construct a new resource from another resource of the same type.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param other The other resource.
	* @return The new resource as a copy of the other.
	*/
	public Resource newResource(Resource container, Resource other)
	{ return new BaseDigest((Digest) other); }

	/**
	* Construct a new continer given just an id.
	* @param id The id for the new object.
	* @return The new containe Resource.
	*/
	public Edit newContainerEdit(String ref) { return null; }

	/**
	* Construct a new container resource, from an XML element.
	* @param element The XML.
	* @return The new container resource.
	*/
	public Edit newContainerEdit(Element element) { return null; }

	/**
	* Construct a new container resource, as a copy of another
	* @param other The other contianer to copy.
	* @return The new container resource.
	*/
	public Edit newContainerEdit(Resource other) { return null; }

	/**
	* Construct a new resource given just an id.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param id The id for the new object.
	* @param others (options) array of objects to load into the Resource's fields.
	* @return The new resource.
	*/
	public Edit newResourceEdit(Resource container, String id, Object[] others)
	{
		BaseDigest e = new BaseDigest(id);
		e.activate();
		return e;
	}

	/**
	* Construct a new resource, from an XML element.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param element The XML.
	* @return The new resource from the XML.
	*/
	public Edit newResourceEdit(Resource container, Element element)
	{
		BaseDigest e =  new BaseDigest(element);
		e.activate();
		return e;
	}

	/**
	* Construct a new resource from another resource of the same type.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param other The other resource.
	* @return The new resource as a copy of the other.
	*/
	public Edit newResourceEdit(Resource container, Resource other)
	{
		BaseDigest e = new BaseDigest((Digest) other);
		e.activate();
		return e;
	}

	/**
	* Collect the fields that need to be stored outside the XML (for the resource).
	* @return An array of field values to store in the record outside the XML (for the resource).
	*/
	public Object[] storageFields(Resource r) { return null; }

	/**
	 * Check if this resource is in draft mode.
	 * @param r The resource.
	 * @return true if the resource is in draft mode, false if not.
	 */
	public boolean isDraft(Resource r)
	{
		return false;
	}

	/**
	 * Access the resource owner user id.
	 * @param r The resource.
	 * @return The resource owner user id.
	 */
	public String getOwnerId(Resource r)
	{
		return null;
	}

	/**
	 * Access the resource date.
	 * @param r The resource.
	 * @return The resource date.
	 */
	public Time getDate(Resource r)
	{
		return null;
	}

	/**
	* Compute a time range based on a specific time.
	* @return The time range that encloses the specific time.
	*/
	protected TimeRange computeRange(Time time)
	{
		// set the period to "today" (local!) from day start to next day start, not end inclusive
		TimeBreakdown brk = time.breakdownLocal();
		brk.setMs(0);
		brk.setSec(0);
		brk.setMin(0);
		brk.setHour(0);
		Time start = m_timeService.newTimeLocal(brk);
		Time end = m_timeService.newTime(start.getTime() + 24 * 60 * 60 * 1000);
		return m_timeService.newTimeRange(start, end, true, false);

	}	// computeRange

}   // BaseDigestService

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/digest/BaseDigestService.java,v 1.2 2005/05/12 01:38:27 ggolden.umich.edu Exp $
*
**********************************************************************************/
