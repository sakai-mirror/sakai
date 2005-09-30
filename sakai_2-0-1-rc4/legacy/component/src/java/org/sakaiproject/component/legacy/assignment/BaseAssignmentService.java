/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/assignment/BaseAssignmentService.java,v 1.6 2005/06/03 20:18:09 zqian.umich.edu Exp $
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
*****************************************************************************f*****/

// package
package org.sakaiproject.component.legacy.assignment;

// import
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.sakaiproject.api.kernel.session.SessionBindingEvent;
import org.sakaiproject.api.kernel.session.SessionBindingListener;
import org.sakaiproject.exception.EmptyException;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.memory.Cache;
import org.sakaiproject.service.framework.memory.CacheRefresher;
import org.sakaiproject.service.framework.memory.MemoryService;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.archive.ArchiveService;
import org.sakaiproject.service.legacy.assignment.Assignment;
import org.sakaiproject.service.legacy.assignment.AssignmentContent;
import org.sakaiproject.service.legacy.assignment.AssignmentContentEdit;
import org.sakaiproject.service.legacy.assignment.AssignmentContentNotEmptyException;
import org.sakaiproject.service.legacy.assignment.AssignmentEdit;
import org.sakaiproject.service.legacy.assignment.AssignmentService;
import org.sakaiproject.service.legacy.assignment.AssignmentSubmission;
import org.sakaiproject.service.legacy.assignment.AssignmentSubmissionEdit;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.content.cover.ContentHostingService;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.event.cover.EventTrackingService;
import org.sakaiproject.service.legacy.id.cover.IdService;
import org.sakaiproject.service.legacy.realm.cover.RealmService;
import org.sakaiproject.service.legacy.resource.AttachmentContainer;
import org.sakaiproject.service.legacy.resource.Edit;
import org.sakaiproject.service.legacy.resource.Reference;
import org.sakaiproject.service.legacy.resource.ReferenceVector;
import org.sakaiproject.service.legacy.resource.Resource;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.resource.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.security.cover.SecurityService;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.cover.TimeService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.util.java.Blob;
import org.sakaiproject.util.java.EmptyIterator;
import org.sakaiproject.util.FormattedText;
import org.sakaiproject.util.java.StringUtil;
import org.sakaiproject.util.Validator;
import org.sakaiproject.util.xml.Xml;
import org.sakaiproject.util.resource.BaseResourcePropertiesEdit;
import org.sakaiproject.util.storage.StorageUser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
* <p>BaseAssignmentService is the abstract service class for Assignments.</p>
* <p>The Concrete Service classes extending this are the XmlFile and DbCached storage classes.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
* @see org.chefproject.core.Assignment
*/
public abstract class BaseAssignmentService 
	implements AssignmentService
{
	/** A Storage object for persistent storage of Assignments. */
	protected AssignmentStorage m_assignmentStorage = null;

	/** A Storage object for persistent storage of Assignments. */
	protected AssignmentContentStorage m_contentStorage = null;

	/** A Storage object for persistent storage of Assignments. */
	protected AssignmentSubmissionStorage m_submissionStorage = null;

	/** A Cache for this service - Assignments keyed by reference. */
	protected Cache m_assignmentCache = null;

	/** A Cache for this service - AssignmentContents keyed by reference. */
	protected Cache m_contentCache = null;
	
	/** A Cache for this service - AssignmentSubmissions keyed by reference. */
	protected Cache m_submissionCache = null;
	
	/** The access point URL. */
	protected String m_relativeAccessPoint = null;
	
	/*******************************************************************************
	* EVENT STRINGS
	*******************************************************************************/

	/** Event for adding an assignment. */
	public static final String EVENT_ADD_ASSIGNMENT = "asn.new.assignment";

	/** Event for adding an assignment. */
	public static final String EVENT_ADD_ASSIGNMENT_CONTENT = "asn.new.assignmentcontent";

	/** Event for adding an assignment submission. */
	public static final String EVENT_ADD_ASSIGNMENT_SUBMISSION = "asn.new.submission";

	/** Event for removing an assignment. */
	public static final String EVENT_REMOVE_ASSIGNMENT = "asn.delete.assignment";

	/** Event for removing an assignment content. */
	public static final String EVENT_REMOVE_ASSIGNMENT_CONTENT = "asn.delete.assignmentcontent";

	/** Event for removing an assignment submission. */
	public static final String EVENT_REMOVE_ASSIGNMENT_SUBMISSION = "asn.delete.submission";

	/** Event for accessing an assignment. */
	public static final String EVENT_ACCESS_ASSIGNMENT = "asn.read.assignment";

	/** Event for accessing an assignment content. */
	public static final String EVENT_ACCESS_ASSIGNMENT_CONTENT = "asn.read.assignmentcontent";
	
	/** Event for accessing an assignment submission. */
	public static final String EVENT_ACCESS_ASSIGNMENT_SUBMISSION = "asn.read.submission";

	/** Event for updating an assignment. */
	public static final String EVENT_UPDATE_ASSIGNMENT = "asn.revise.assignment";

	/** Event for updating an assignment content. */
	public static final String EVENT_UPDATE_ASSIGNMENT_CONTENT = "asn.revise.assignmentcontent";

	/** Event for updating an assignment submission. */
	public static final String EVENT_UPDATE_ASSIGNMENT_SUBMISSION = "asn.revise.submission";
	
	/** Event for saving an assignment submission. */
	public static final String EVENT_SAVE_ASSIGNMENT_SUBMISSION = "asn.save.submission";
	
	/** Event for submitting an assignment submission. */
	public static final String EVENT_SUBMIT_ASSIGNMENT_SUBMISSION = "asn.submit.submission";


	/*******************************************************************************
	* Abstractions, etc.
	*******************************************************************************/
	
	/**
	* Construct a Storage object for Assignments.
	* @return The new storage object.
	*/
	protected abstract AssignmentStorage newAssignmentStorage();

	/**
	* Construct a Storage object for AssignmentContents.
	* @return The new storage object.
	*/
	protected abstract AssignmentContentStorage newContentStorage();
	
	/**
	* Construct a Storage object for AssignmentSubmissions.
	* @return The new storage object.
	*/
	protected abstract AssignmentSubmissionStorage newSubmissionStorage();


	/**
	 * Access the partial URL that forms the root of resource URLs.
	 * @param relative - if true, form within the access path only (i.e. starting with /msg)
	 * @return the partial URL that forms the root of resource URLs.
	 */
	protected String getAccessPoint(boolean relative)
	{
		return (relative ? "" : ServerConfigurationService.getAccessUrl()) + m_relativeAccessPoint;

	}	// getAccessPoint

	/**
	* Access the internal reference which can be used to assess security clearance.
	* @param id The assignment id string.
	* @return The the internal reference which can be used to access the resource from within the system.
	*/
	public String assignmentReference(String context, String id)
	{
		String retVal = null;
		if(context == null)
			retVal = getAccessPoint(true) + Resource.SEPARATOR + "a" + Resource.SEPARATOR + id;
		else
			retVal = getAccessPoint(true) + Resource.SEPARATOR + "a" + Resource.SEPARATOR + context + Resource.SEPARATOR + id;
		return retVal;

	}   // assignmentReference

	/**
	* Access the internal reference which can be used to access the resource from within the system.
	* @param id The content id string.
	* @return The the internal reference which can be used to access the resource from within the system.
	*/
	public String contentReference(String context, String id)
	{
		String retVal = null;
		if(context == null)
			retVal = getAccessPoint(true) + Resource.SEPARATOR + "c" + Resource.SEPARATOR + id;
		else
			retVal = getAccessPoint(true) + Resource.SEPARATOR + "c" + Resource.SEPARATOR + context + Resource.SEPARATOR + id;
		return retVal;

	}   // contentReference

	/**
	* Access the internal reference which can be used to access the resource from within the system.
	* @param id The submission id string.
	* @return The the internal reference which can be used to access the resource from within the system.
	*/
	public String submissionReference(String context, String id, String assignmentId)
	{
		String retVal = null;
		if(context == null)
			retVal = getAccessPoint(true) + Resource.SEPARATOR + "s" + Resource.SEPARATOR + id;
		else
			retVal = getAccessPoint(true) + Resource.SEPARATOR + "s" + Resource.SEPARATOR + context + Resource.SEPARATOR + assignmentId + Resource.SEPARATOR + id;
		return retVal;

	}   // submissionReference
	
	/**
	* Access the assignment id extracted from an assignment reference.
	* @param ref The assignment reference string.
	* @return The the assignment id extracted from an assignment reference.
	*/
	protected String assignmentId(String ref)
	{
		int i = ref.lastIndexOf(Resource.SEPARATOR);
		if (i == -1) return ref;
		String id = ref.substring(i + 1);
		return id;

	}   // assignmentId
	
	/**
	* Access the content id extracted from a content reference.
	* @param ref The content reference string.
	* @return The the content id extracted from a content reference.
	*/
	protected String contentId(String ref)
	{
		int i = ref.lastIndexOf(Resource.SEPARATOR);
		if (i == -1) return ref;
		String id = ref.substring(i + 1);
		return id;

	}   // contentId
	
	/**
	* Access the submission id extracted from a submission reference.
	* @param ref The submission reference string.
	* @return The the submission id extracted from a submission reference.
	*/
	protected String submissionId(String ref)
	{
		int i = ref.lastIndexOf(Resource.SEPARATOR);
		if (i == -1) return ref;
		String id = ref.substring(i + 1);
		return id;

	}   // submissionId
	
	/**
	 * Check security permission.
	 * @param lock - The lock id string.
	 * @param resource - The resource reference string, or null if no resource is involved.
	 * @return true if allowed, false if not
	 */
	protected boolean unlockCheck(String lock, String resource)
	{
		if (!SecurityService.unlock(lock, resource))
		{
			return false;
		}

		return true;

	}// unlockCheck

	/**
	* Check security permission.
	* @param lock1 The lock id string.
	* @param lock2 The lock id string.
	* @param resource The resource reference string, or null if no resource is involved.
	* @return true if either allowed, false if not
	*/
	protected boolean unlockCheck2(String lock1, String lock2, String resource)
	{
		if (!SecurityService.unlock(lock1, resource))
		{
			if (!SecurityService.unlock(lock2, resource))
			{
				return false;
			}
		}

		return true;

	}	// unlockCheck2

	/**
	 * Check security permission.
	 * @param lock - The lock id string.
	 * @param resource - The resource reference string, or null if no resource is involved.
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

	/**
	* Check security permission.
	* @param lock1 The lock id string.
	* @param lock2 The lock id string.
	* @param resource The resource reference string, or null if no resource is involved.
	* @exception PermissionException Thrown if the user does not have access to either.
	*/
	protected void unlock2(String lock1, String lock2, String resource)
		throws PermissionException
	{
		if (!unlockCheck2(lock1, lock2, resource))
		{
			throw new PermissionException(UsageSessionService.getSessionUserId(),
					lock1 + "/" + lock2, resource);
		}

	}	// unlock2
	
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

	/** Configuration: cache, or not. */
	protected boolean m_caching = false;

	/**
	 * Configuration: set the locks-in-db
	 * @param path The storage path.
	 */
	public void setCaching(String value)
	{
		m_caching = new Boolean(value).booleanValue();
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
		m_logger.info(this + ".init()");

		// construct storage helpers and read
		m_assignmentStorage = newAssignmentStorage();
		m_assignmentStorage.open();
		m_contentStorage = newContentStorage();
		m_contentStorage.open();
		m_submissionStorage = newSubmissionStorage();
		m_submissionStorage.open();
		
		// make the cache
		if (m_caching)
		{
			m_assignmentCache = m_memoryService.newCache(new AssignmentCacheRefresher(), assignmentReference(null, ""));
			m_contentCache = m_memoryService.newCache(new AssignmentContentCacheRefresher(), contentReference(null, ""));
			m_submissionCache = m_memoryService.newCache(new AssignmentSubmissionCacheRefresher(), submissionReference(null, "", ""));
		}

		// register as a resource service
		ServerConfigurationService.registerResourceService(this);

	}	// init

	/**
	* Returns to uninitialized state.
	*/
	public void destroy()
	{
		if (m_caching)
		{
			if (m_assignmentCache != null)
			{
				m_assignmentCache.destroy();
				m_assignmentCache = null;
			}
			if (m_contentCache != null)
			{
				m_contentCache.destroy();
				m_contentCache = null;
			}
			if (m_submissionCache != null)
			{
				m_submissionCache.destroy();
				m_submissionCache = null;
			}
		}
		
		m_assignmentStorage.close();
		m_assignmentStorage = null;
		m_contentStorage.close();
		m_contentStorage = null;
		m_submissionStorage.close();
		m_submissionStorage = null;
		
		m_logger.info(this + ".destroy()");
	}

	
	
	/*******************************************************************************
	* AssignmentService implementation
	*******************************************************************************/
	
	/** 
	 * Creates and adds a new Assignment to the service.
	 * @param context - Describes the portlet context - generated with DefaultId.getChannel().
	 * @return The new Assignment object.
	 * @throws IdInvalidException if the id contains prohibited characers.
	 * @throws IdUsedException if the id is already used in the service.
	 * @throws PermissionException if current User does not have permission to do this.
	 */
	public AssignmentEdit addAssignment(String context)
		throws PermissionException
	{
		if(m_logger.isDebugEnabled())
			m_logger.debug("ASSIGNMENT : BASE SERVICE : ENTERING ADD ASSIGNMENT : CONTEXT : " + context);
		
		String assignmentId = null;
		boolean badId = false;
		
		do
		{
			badId = false;
			assignmentId = IdService.getUniqueId();
			try
			{
				Validator.checkResourceId(assignmentId);
			}
			catch(IdInvalidException iie)
			{
				badId = true;
			}
		
			if(m_assignmentStorage.check(assignmentId))
				badId = true;
		}
		while(badId);
		
		String key = assignmentReference(context, assignmentId);

		unlock(SECURE_ADD_ASSIGNMENT, key);

		// storage
		AssignmentEdit assignment = m_assignmentStorage.put(assignmentId, context);

		// event for tracking
		((BaseAssignmentEdit) assignment).setEvent(EVENT_ADD_ASSIGNMENT);

		if(m_logger.isDebugEnabled())
			m_logger.debug("ASSIGNMENT : BASE SERVICE : LEAVING ADD ASSIGNMENT WITH : ID : " + assignment.getId());

		return assignment;

	}	// addAssignment


	/**
	* Add a new assignment to the directory, from a definition in XML.
	* Must commitEdit() to make official, or cancelEdit() when done!
	* @param el The XML DOM Element defining the assignment.
	* @return A locked AssignmentEdit object (reserving the id).
	* @exception IdInvalidException if the assignment id is invalid.
	* @exception IdUsedException if the assignment id is already used.
	* @exception PermissionException if the current user does not have permission to add an assignnment.
	*/
	public AssignmentEdit mergeAssignment(Element el)
		throws IdInvalidException, IdUsedException, PermissionException
	{
		// construct from the XML
		Assignment assignmentFromXml = new BaseAssignment(el);

		// check for a valid assignment name
		Validator.checkResourceId(assignmentFromXml.getId());

		// check security (throws if not permitted)
		unlock(SECURE_ADD_ASSIGNMENT, assignmentFromXml.getReference());

		// reserve a assignment with this id from the info store - if it's in use, this will return null
		AssignmentEdit assignment = m_assignmentStorage.put(assignmentFromXml.getId(), assignmentFromXml.getContext());
		if (assignment == null)
		{
			throw new IdUsedException(assignmentFromXml.getId());
		}

		// transfer from the XML read assignment object to the AssignmentEdit
		((BaseAssignmentEdit) assignment).set(assignmentFromXml);

		((BaseAssignmentEdit) assignment).setEvent(EVENT_ADD_ASSIGNMENT);

		return assignment;
	}


	/** 
	 * Creates and adds a new Assignment to the service
	 * which is a copy of an existing Assignment.
	 * @param assignmentId - The Assignment to be duplicated.
	 * @return The new Assignment object, or null if the original Assignment does not exist.
	 * @throws PermissionException if current User does not have permission to do this.
	 */
	public AssignmentEdit addDuplicateAssignment(String context, String assignmentReference)
		throws PermissionException, IdInvalidException, IdUsedException, IdUnusedException
	{
		if(m_logger.isDebugEnabled())
			m_logger.debug("ASSIGNMENT : BASE SERVICE : ENTERING ADD DUPLICATE ASSIGNMENT WITH ID : " + assignmentReference);

		Vector tempVector = new Vector();
		String tempString = null;
		Reference tempRef = null;
		Reference newRef = null;
		AssignmentEdit retVal = null;
		AssignmentContentEdit newContent = null;

		if(assignmentReference != null)
		{
			String assignmentId = assignmentId(assignmentReference);
			if(!m_assignmentStorage.check(assignmentId))
				throw new IdUnusedException(assignmentId);
			else
			{
				if(m_logger.isDebugEnabled())
					m_logger.debug("ASSIGNMENT : BASE SERVICE : addDuplicateAssignment : assignment exists - will copy");
				
				Assignment existingAssignment = getAssignment(assignmentReference);
				newContent = addDuplicateAssignmentContent(context, existingAssignment.getContentReference());
				commitEdit(newContent);
				
				retVal = addAssignment(context);
				retVal.setContentReference(newContent.getReference());
				retVal.setTitle(existingAssignment.getTitle() + " - Copy");
				retVal.setSection(existingAssignment.getSection());
				retVal.setOpenTime(existingAssignment.getOpenTime());
				retVal.setDueTime(existingAssignment.getDueTime());
				retVal.setDropDeadTime(existingAssignment.getDropDeadTime());
				retVal.setCloseTime(existingAssignment.getCloseTime());
				retVal.setDraft(true);
				((BaseResourcePropertiesEdit)retVal.getProperties()).addAll(existingAssignment.getProperties());
			}
		}

		if(m_logger.isDebugEnabled())
			m_logger.debug("ASSIGNMENT : BASE SERVICE : ADD DUPLICATE ASSIGNMENT : LEAVING ADD DUPLICATE ASSIGNMENT WITH ID : " + retVal.getId());
					
		return retVal;
	}


	/**
	 * Access the Assignment with the specified reference.
	 * @param assignmentReference - The reference of the Assignment.
	 * @return The Assignment corresponding to the reference, or null if it does not exist.
	 * @throws IdUnusedException if there is no object with this reference.
	 * @throws PermissionException if the current user is not allowed to access this.
	 */
	public Assignment getAssignment(String assignmentReference)
		throws IdUnusedException, PermissionException
	{
		if(m_logger.isDebugEnabled())
			m_logger.debug("ASSIGNMENT : BASE SERVICE : GET ASSIGNMENT : REF : " + assignmentReference);

		Assignment assignment = null;

		String assignmentId = assignmentId(assignmentReference);
		
		if ((m_caching) && (m_assignmentCache != null) && (!m_assignmentCache.disabled()))
		{
			// if we have it in the cache, use it
			if(m_assignmentCache.containsKey(assignmentReference))
				assignment = (Assignment)m_assignmentCache.get(assignmentReference);
			else
			{
				assignment = m_assignmentStorage.get(assignmentId);
			
				// cache the result
				m_assignmentCache.put(assignmentReference, assignment);
			}
		}
		else
		{
//			// if we have done this already in this thread, use that
//			assignment = (Assignment) CurrentService.getInThread(assignmentId+".assignment.assignment");
//			if (assignment == null)
//			{
				assignment = m_assignmentStorage.get(assignmentId);
//				
//				// "cache" the assignment in the current service in case they are needed again in this thread...
//				if (assignment != null)
//				{
//					CurrentService.setInThread(assignmentId+".assignment.assignment", assignment);
//				}
//			}
		}

		if(assignment == null)
			throw new IdUnusedException(assignmentId);

		unlock(SECURE_ACCESS_ASSIGNMENT, assignmentReference);

		if(m_logger.isDebugEnabled())
			m_logger.debug("ASSIGNMENT : BASE SERVICE : GOT ASSIGNMENT : ID : " + assignment.getId());

		// track event
		// EventTrackingService.post(EventTrackingService.newEvent(EVENT_ACCESS_ASSIGNMENT, assignment.getReference(), false));

		return assignment;

	}//getAssignment


	/**
	* Access all assignment objects - known to us (not from external providers).
	* @return A list of assignment objects.
	*/
	protected List getAssignments(String context)
	{
		List assignments = new Vector();

		if ((m_caching) && (m_assignmentCache != null) && (!m_assignmentCache.disabled()))
		{
			// if the cache is complete, use it
			if (m_assignmentCache.isComplete())
			{
				assignments = m_assignmentCache.getAll();
				// TODO: filter by context
			}
	
			// otherwise get all the assignments from storage
			else
			{
				// Note: while we are getting from storage, storage might change.  These can be processed
				// after we get the storage entries, and put them in the cache, and mark the cache complete.
				// -ggolden
				synchronized (m_assignmentCache)
				{
					// if we were waiting and it's now complete...
					if (m_assignmentCache.isComplete())
					{
						assignments = m_assignmentCache.getAll();
						return assignments;
					}
	
					// save up any events to the cache until we get past this load
					m_assignmentCache.holdEvents();
	
					assignments = m_assignmentStorage.getAll(context);
	
					// update the cache, and mark it complete
					for (int i = 0; i < assignments.size(); i++)
					{
						Assignment assignment = (Assignment) assignments.get(i);
						m_assignmentCache.put(assignment.getReference(), assignment);
					}
	
					m_assignmentCache.setComplete();
					// TODO: not reall, just for context
	
					// now we are complete, process any cached events
					m_assignmentCache.processEvents();
				}
			}
		}

		else
		{
//			// if we have done this already in this thread, use that
//			assignments = (List) CurrentService.getInThread(context+".assignment.assignments");
//			if (assignments == null)
//			{
				assignments = m_assignmentStorage.getAll(context);
//				
//				// "cache" the assignments in the current service in case they are needed again in this thread...
//				if (assignments != null)
//				{
//					CurrentService.setInThread(context+".assignment.assignments", assignments);
//				}
//			}
		}

		return assignments;

	}   // getAssignments

	
	/**
	* Get a locked assignment object for editing.  Must commitEdit() to make official, or cancelEdit() when done!
	* @param id The assignment id string.
	* @return An AssignmentEdit object for editing.
	* @exception IdUnusedException if not found, or if not an AssignmentEdit object
	* @exception PermissionException if the current user does not have permission to edit this assignment.
	* @exception InUseException if the assignment is being edited by another user.
	*/
	public AssignmentEdit editAssignment(String assignmentReference)
		throws IdUnusedException, PermissionException, InUseException
	{
		// check security (throws if not permitted)
		unlock(SECURE_UPDATE_ASSIGNMENT, assignmentReference);

		String assignmentId = assignmentId(assignmentReference);

		// check for existance
		if (!m_assignmentStorage.check(assignmentId))
		{
			throw new IdUnusedException(assignmentId);
		}

		// ignore the cache - get the assignment with a lock from the info store
		AssignmentEdit assignmentEdit = m_assignmentStorage.edit(assignmentId);
		if (assignmentEdit == null) throw new InUseException(assignmentId);

		((BaseAssignmentEdit) assignmentEdit).setEvent(EVENT_UPDATE_ASSIGNMENT);

		return assignmentEdit;

	}   // editAssignment
	

	/**
	* Commit the changes made to an AssignmentEdit object, and release the lock.
	* @param assignment The AssignmentEdit object to commit.
	*/
	public void commitEdit(AssignmentEdit assignment)
	{
		// check for closed edit
		if (!assignment.isActiveEdit())
		{
			try { throw new Exception(); }
			catch (Exception e) { m_logger.warn(this + ".commitEdit(): closed AssignmentEdit", e); }
			return;
		}

		// update the properties
		addLiveUpdateProperties(assignment.getPropertiesEdit());
		
		// complete the edit
		m_assignmentStorage.commit(assignment);

		// track it
		EventTrackingService.post(
			EventTrackingService.newEvent(((BaseAssignmentEdit) assignment).getEvent(), assignment.getReference(), true));

		// close the edit object
		((BaseAssignmentEdit) assignment).closeEdit();

	}	// commitEdit

	
	/**
	* Cancel the changes made to a AssignmentEdit object, and release the lock.
	* @param assignment The AssignmentEdit object to commit.
	*/
	public void cancelEdit(AssignmentEdit assignment)
	{
		// check for closed edit
		if (!assignment.isActiveEdit())
		{
			try { throw new Exception(); }
			catch (Exception e) { m_logger.warn(this + ".cancelEdit(): closed AssignmentEdit", e); }
			return;
		}

		// release the edit lock
		m_assignmentStorage.cancel(assignment);

		// close the edit object
		((BaseAssignmentEdit) assignment).closeEdit();

	}	// cancelEdit(Assignment)
	
	
	/**
	 * Removes this Assignment and all references to it.
	 * @param assignment - The Assignment to remove.
	 * @throws PermissionException if current User does not have permission to do this.
	 */
	public void removeAssignment(AssignmentEdit assignment)
		throws PermissionException
	{
		if(assignment != null)
		{
			if(m_logger.isDebugEnabled())
				m_logger.debug("BaseAssignmentService :  removeAssignment with id : " + assignment.getId());

			if(!assignment.isActiveEdit())
			{
				try { throw new Exception(); }
				catch (Exception e) { m_logger.warn(this + ".removeAssignment(): closed AssignmentEdit", e); }
				return;
			}

			// CHECK PERMISSION
			unlock(SECURE_REMOVE_ASSIGNMENT, assignment.getReference());

			// complete the edit
			m_assignmentStorage.remove(assignment);

			// track event
			EventTrackingService.post(EventTrackingService.newEvent(EVENT_REMOVE_ASSIGNMENT, assignment.getReference(), true));
			
			// close the edit object
			((BaseAssignmentEdit)assignment).closeEdit();

			// remove any realm defined for this resource
			try
			{
				RealmService.removeRealm(RealmService.editRealm(assignment.getReference()));
			}
			catch (InUseException e) { m_logger.warn(this + ".removeAssignment: removing realm for : " + assignment.getReference() + " : " + e); }
			catch (PermissionException e) { m_logger.warn(this + ".removeAssignment: removing realm for : " + assignment.getReference() + " : " + e); }
			catch (IdUnusedException ignore) {}
		}

	}//removeAssignment
	
	
	/** 
	 * Creates and adds a new AssignmentContent to the service.
	 * @param context - Describes the portlet context - generated with DefaultId.getChannel().
	 * @return AssignmentContent The new AssignmentContent object.
	 * @throws PermissionException if current User does not have permission to do this.
	 */
	public AssignmentContentEdit addAssignmentContent(String context)
		throws PermissionException
	{
		if(m_logger.isDebugEnabled())
			m_logger.debug("ASSIGNMENT : BASE SERVICE : ENTERING ADD ASSIGNMENT CONTENT");

		String contentId = null;
		boolean badId = false;
		
		do
		{
			badId = false;
			contentId = IdService.getUniqueId();
			try
			{
				Validator.checkResourceId(contentId);
			}
			catch(IdInvalidException iie)
			{
				badId = true;
			}
			
			if(m_contentStorage.check(contentId))
				badId = true;
		}
		while(badId);
		
		unlock(SECURE_ADD_ASSIGNMENT_CONTENT, contentReference(context, contentId));

		AssignmentContentEdit content = m_contentStorage.put(contentId, context);

		if(m_logger.isDebugEnabled())
			m_logger.debug("ASSIGNMENT : BASE SERVICE : LEAVING ADD ASSIGNMENT CONTENT : ID : " + content.getId());

		// event for tracking
		((BaseAssignmentContentEdit) content).setEvent(EVENT_ADD_ASSIGNMENT_CONTENT);

		return content;

	}//addAssignmentContent

	
	/**
	* Add a new AssignmentContent to the directory, from a definition in XML.
	* Must commitEdit() to make official, or cancelEdit() when done!
	* @param el The XML DOM Element defining the AssignmentContent.
	* @return A locked AssignmentContentEdit object (reserving the id).
	* @exception IdInvalidException if the AssignmentContent id is invalid.
	* @exception IdUsedException if the AssignmentContent id is already used.
	* @exception PermissionException if the current user does not have permission to add an AssignnmentContent.
	*/
	public AssignmentContentEdit mergeAssignmentContent(Element el)
		throws IdInvalidException, IdUsedException, PermissionException
	{
		// construct from the XML
		AssignmentContent contentFromXml = new BaseAssignmentContent(el);

		// check for a valid assignment name
		Validator.checkResourceId(contentFromXml.getId());

		// check security (throws if not permitted)
		unlock(SECURE_ADD_ASSIGNMENT_CONTENT, contentFromXml.getReference());

		// reserve a content with this id from the info store - if it's in use, this will return null
		AssignmentContentEdit content = m_contentStorage.put(contentFromXml.getId(), contentFromXml.getContext());
		if (content == null)
		{
			throw new IdUsedException(contentFromXml.getId());
		}

		// transfer from the XML read content object to the AssignmentContentEdit
		((BaseAssignmentContentEdit) content).set(contentFromXml);


		((BaseAssignmentContentEdit) content).setEvent(EVENT_ADD_ASSIGNMENT_CONTENT);

		return content;
	}

	/** 
	 * Creates and adds a new AssignmentContent to the service
	 * which is a copy of an existing AssignmentContent.
	 * @param context - From DefaultId.getChannel(RunData)
	 * @param contentReference - The id of the AssignmentContent to be duplicated.
	 * @return AssignmentContentEdit The new AssignmentContentEdit object, or null if the original does not exist.
	 * @throws PermissionException if current User does not have permission to do this.
	 */
	public AssignmentContentEdit addDuplicateAssignmentContent(String context, String contentReference)
		throws PermissionException, IdInvalidException, IdUnusedException
	{
		if(m_logger.isDebugEnabled())
			m_logger.debug("ASSIGNMENT : BASE SERVICE : ENTERING ADD DUPLICATE ASSIGNMENT CONTENT : " + contentReference);
		
		AssignmentContentEdit retVal = null;
		AssignmentContent existingContent = null;
		Vector tempVector = null;
		String tempString = null;
		String newContentId = null;
		Reference tempRef = null;
		Reference newRef = null;

		if(contentReference != null)
		{
			String contentId = contentId(contentReference);
			if(!m_contentStorage.check(contentId))
				throw new IdUnusedException(contentId);
			else
			{
				if(m_logger.isDebugEnabled())
					m_logger.debug("ASSIGNMENT : BASE SERVICE : ADD DUPL. CONTENT : found match - will copy");
				
				existingContent = getAssignmentContent(contentReference);
				retVal = addAssignmentContent(context);
				((BaseResourcePropertiesEdit)retVal.getProperties()).addAll(existingContent.getProperties());
				retVal.setTitle(existingContent.getTitle() + " - Copy");
				retVal.setInstructions(existingContent.getInstructions());
				retVal.setHonorPledge(existingContent.getHonorPledge());
				retVal.setTypeOfSubmission(existingContent.getTypeOfSubmission());
				retVal.setTypeOfGrade(existingContent.getTypeOfGrade());
				retVal.setMaxGradePoint(existingContent.getMaxGradePoint());
				retVal.setGroupProject(existingContent.getGroupProject());
				retVal.setIndividuallyGraded(existingContent.individuallyGraded());
				retVal.setReleaseGrades(existingContent.releaseGrades());
				retVal.setAllowAttachments(existingContent.getAllowAttachments());

				tempVector = existingContent.getAttachments();
				if(tempVector != null)
				{
					for(int z = 0; z < tempVector.size(); z++)
					{
						tempRef = (Reference)tempVector.get(z);
						if(tempRef != null)
						{
							newRef = new Reference(tempRef);
							retVal.addAttachment(newRef);
						}
					}
				}
			}
		}

		if(m_logger.isDebugEnabled())
			m_logger.debug("ASSIGNMENT : BASE SERVICE : LEAVING ADD DUPLICATE CONTENT WITH ID : " + retVal.getId());

		return retVal;
	}

	
	/**
	 * Access the AssignmentContent with the specified reference.
	 * @param contentReference - The reference of the AssignmentContent.
	 * @return The AssignmentContent corresponding to the reference, or null if it does not exist.
	 * @throws IdUnusedException if there is no object with this reference.
	 * @throws PermissionException if the current user is not allowed to access this.
	 */
	public AssignmentContent getAssignmentContent(String contentReference)
		throws IdUnusedException, PermissionException
	{
		if(m_logger.isDebugEnabled())
			m_logger.debug("ASSIGNMENT : BASE SERVICE : GET CONTENT : ID : " + contentReference);
		
		AssignmentContent content = null;

		// if we have it in the cache, use it
		String contentId = contentId(contentReference);

		if ((m_caching) && (m_contentCache != null) && (!m_contentCache.disabled()))
		{
			if(m_contentCache.containsKey(contentReference))
				content = (AssignmentContent)m_contentCache.get(contentReference);
			else
			{
				content = m_contentStorage.get(contentId);
			
				// cache the result
				m_contentCache.put(contentReference, content);
			}
		}

		else
		{
//			// if we have done this already in this thread, use that
//			content = (AssignmentContent) CurrentService.getInThread(contentId+".assignment.content");
//			if (content == null)
//			{
				content = m_contentStorage.get(contentId);
//				
//				// "cache" the content in the current service in case they are needed again in this thread...
//				if (content != null)
//				{
//					CurrentService.setInThread(contentId+".assignment.content", contentId);
//				}
//			}
		}

		// check security
		unlock(SECURE_ACCESS_ASSIGNMENT_CONTENT, contentReference);
		if(content == null)
			throw new IdUnusedException(contentId);

		if(m_logger.isDebugEnabled())
			m_logger.debug("ASSIGNMENT : BASE SERVICE : GOT ASSIGNMENT CONTENT : ID : " + content.getId());
		
		// track event
		// EventTrackingService.post(EventTrackingService.newEvent(EVENT_ACCESS_ASSIGNMENT_CONTENT, content.getReference(), false));

		return content;

	}//getAssignmentContent
	

	/**
	* Access all AssignmentContent objects - known to us (not from external providers).
	* @return A list of AssignmentContent objects.
	*/
	protected List getAssignmentContents(String context)
	{
		List contents = new Vector();

		if ((m_caching) && (m_contentCache != null) && (!m_contentCache.disabled()))
		{
			// if the cache is complete, use it
			if (m_contentCache.isComplete())
			{
				contents = m_contentCache.getAll();
				// TODO: filter by context
			}
	
			// otherwise get all the contents from storage
			else
			{
				// Note: while we are getting from storage, storage might change.  These can be processed
				// after we get the storage entries, and put them in the cache, and mark the cache complete.
				// -ggolden
				synchronized (m_contentCache)
				{
					// if we were waiting and it's now complete...
					if (m_contentCache.isComplete())
					{
						contents = m_contentCache.getAll();
						return contents;
					}
	
					// save up any events to the cache until we get past this load
					m_contentCache.holdEvents();
	
					contents = m_contentStorage.getAll(context);
	
					// update the cache, and mark it complete
					for (int i = 0; i < contents.size(); i++)
					{
						AssignmentContent content = (AssignmentContent) contents.get(i);
						m_contentCache.put(content.getReference(), content);
					}
	
					m_contentCache.setComplete();
					// TODO: not really, just for context
	
					// now we are complete, process any cached events
					m_contentCache.processEvents();
				}
			}
		}

		else
		{
//			// if we have done this already in this thread, use that
//			contents = (List) CurrentService.getInThread(context+".assignment.contents");
//			if (contents == null)
//			{
				contents = m_contentStorage.getAll(context);
//
//				// "cache" the contents in the current service in case they are needed again in this thread...
//				if (contents != null)
//				{
//					CurrentService.setInThread(context+".assignment.contents", contents);
//				}
//			}
		}

		return contents;

	}   // getAssignmentContents
	
	
	/**
	* Get a locked AssignmentContent object for editing.  Must commitEdit() to make official, or cancelEdit() when done!
	* @param id The content id string.
	* @return An AssignmentContentEdit object for editing.
	* @exception IdUnusedException if not found, or if not an AssignmentContentEdit object
	* @exception PermissionException if the current user does not have permission to edit this content.
	* @exception InUseException if the assignment is being edited by another user.
	*/
	public AssignmentContentEdit editAssignmentContent(String contentReference)
		throws IdUnusedException, PermissionException, InUseException
	{
		// check security (throws if not permitted)
		unlock(SECURE_UPDATE_ASSIGNMENT_CONTENT, contentReference);

		String contentId = contentId(contentReference);
		
		// check for existance
		if (!m_contentStorage.check(contentId))
		{
			throw new IdUnusedException(contentId);
		}

		// ignore the cache - get the AssignmentContent with a lock from the info store
		AssignmentContentEdit content = m_contentStorage.edit(contentId);
		if (content == null) throw new InUseException(contentId);

		((BaseAssignmentContentEdit) content).setEvent(EVENT_UPDATE_ASSIGNMENT_CONTENT);

		return content;

	}   // editAssignmentContent


	/**
	* Commit the changes made to an AssignmentContentEdit object, and release the lock.
	* @param content The AssignmentContentEdit object to commit.
	*/
	public void commitEdit(AssignmentContentEdit content)
	{
		// check for closed edit
		if (!content.isActiveEdit())
		{
			try { throw new Exception(); }
			catch (Exception e) { m_logger.warn(this + ".commitEdit(): closed AssignmentContentEdit", e); }
			return;
		}

		// update the properties
		addLiveUpdateProperties(content.getPropertiesEdit());

		// complete the edit
		m_contentStorage.commit(content);

		// track it
		EventTrackingService.post(
			EventTrackingService.newEvent(((BaseAssignmentContentEdit) content).getEvent(), content.getReference(), true));

		// close the edit object
		((BaseAssignmentContentEdit) content).closeEdit();

	}	// commitEdit(AssignmentContent)

	
	/**
	* Cancel the changes made to a AssignmentContentEdit object, and release the lock.
	* @param content The AssignmentContentEdit object to commit.
	*/
	public void cancelEdit(AssignmentContentEdit content)
	{
		// check for closed edit
		if (!content.isActiveEdit())
		{
			try { throw new Exception(); }
			catch (Exception e) { m_logger.warn(this + ".cancelEdit(): closed AssignmentContentEdit", e); }
			return;
		}

		// release the edit lock
		m_contentStorage.cancel(content);

		// close the edit object
		((BaseAssignmentContentEdit) content).closeEdit();

	}	// cancelEdit(Content)
	
	
	/**
	 * Removes an AssignmentContent
	 * @param content - the AssignmentContent to remove.
	 * @throws an AssignmentContentNotEmptyException if this content still has related Assignments.
	 * @throws PermissionException if current User does not have permission to do this.
	 */
	public void removeAssignmentContent(AssignmentContentEdit content)
		throws AssignmentContentNotEmptyException, PermissionException
	{
		if(content != null)
		{
			if(content.inUse())
				throw new AssignmentContentNotEmptyException();
			else
			{
				if(!content.isActiveEdit())
				{
					try { throw new Exception(); }
					catch (Exception e) { m_logger.warn(this + ".removeAssignmentContent(): closed AssignmentContentEdit", e); }
					return;
				}

				// CHECK SECURITY
				unlock(SECURE_REMOVE_ASSIGNMENT_CONTENT, content.getReference());

				// complete the edit
				m_contentStorage.remove(content);

				// track event
				EventTrackingService.post(EventTrackingService.newEvent(EVENT_REMOVE_ASSIGNMENT_CONTENT, content.getReference(), true));
				
				// close the edit object
				((BaseAssignmentContentEdit)content).closeEdit();

				// remove any realm defined for this resource
				try
				{
					RealmService.removeRealm(RealmService.editRealm(content.getReference()));
				}
				catch (InUseException e) { m_logger.warn(this + ".removeAssignmentContent: removing realm for : " + content.getReference() + " : " + e); }
				catch (PermissionException e) { m_logger.warn(this + ".removeAssignmentContent: removing realm for : " + content.getReference() + " : " + e); }
				catch (IdUnusedException ignore) {}
			}
		}
	}
	
	
	/** 
	 * Adds an AssignmentSubmission to the service.
	 * @param context - Describes the portlet context - generated with DefaultId.getChannel().
	 * @return The new AssignmentSubmission.
	 * @exception IdInvalidException if the submission id is invalid.
	 * @exception IdUsedException if the submission id is already used.
	 * @throws PermissionException if the current User does not have permission to do this.
	 */
	public AssignmentSubmissionEdit addSubmission(String context, String assignmentId)
		throws PermissionException
	{
		if(m_logger.isDebugEnabled())
			m_logger.debug("ASSIGNMENT : BASE SERVICE : ENTERING ADD SUBMISSION");

		String submissionId = null;
		boolean badId = false;
		
		do
		{
			badId = false;
			submissionId = IdService.getUniqueId();
			try
			{
				Validator.checkResourceId(submissionId);
			}
			catch(IdInvalidException iie)
			{
				badId = true;
			}
		
			if(m_submissionStorage.check(submissionId))
				badId = true;
		}
		while(badId);
		
		String key = submissionReference(context, submissionId, assignmentId);
		
		if(m_logger.isDebugEnabled())
			m_logger.debug("ASSIGNMENT : BASE SERVICE : ADD SUBMISSION : SUB REF : " + key);
		
		unlock(SECURE_ADD_ASSIGNMENT_SUBMISSION, key);
		
		if(m_logger.isDebugEnabled())
			m_logger.debug("ASSIGNMENT : BASE SERVICE : ADD SUBMISSION : UNLOCKED");
		
		// storage
		AssignmentSubmissionEdit submission = m_submissionStorage.put(submissionId, context, assignmentId);

		if(m_logger.isDebugEnabled())
			m_logger.debug("ASSIGNMENT : BASE SERVICE : LEAVING ADD SUBMISSION : REF : " + submission.getReference());
			
		// event for tracking
		((BaseAssignmentSubmissionEdit) submission).setEvent(EVENT_ADD_ASSIGNMENT_SUBMISSION);
		
		return submission;
	}
	
	/**
	* Add a new AssignmentSubmission to the directory, from a definition in XML.
	* Must commitEdit() to make official, or cancelEdit() when done!
	* @param el The XML DOM Element defining the submission.
	* @return A locked AssignmentSubmissionEdit object (reserving the id).
	* @exception IdInvalidException if the submission id is invalid.
	* @exception IdUsedException if the submission id is already used.
	* @exception PermissionException if the current user does not have permission to add a submission.
	*/
	public AssignmentSubmissionEdit mergeSubmission(Element el)
		throws IdInvalidException, IdUsedException, PermissionException
	{
		// construct from the XML
		BaseAssignmentSubmission submissionFromXml = new BaseAssignmentSubmission(el);

		// check for a valid submission name
		Validator.checkResourceId(submissionFromXml.getId());

		// check security (throws if not permitted)
		unlock(SECURE_ADD_ASSIGNMENT_SUBMISSION, submissionFromXml.getReference());

		// reserve a submission with this id from the info store - if it's in use, this will return null
		AssignmentSubmissionEdit submission = m_submissionStorage.put(submissionFromXml.getId(), submissionFromXml.getContext(), submissionFromXml.getAssignmentId());
		if (submission == null)
		{
			throw new IdUsedException(submissionFromXml.getId());
		}

		// transfer from the XML read submission object to the SubmissionEdit
		((BaseAssignmentSubmissionEdit) submission).set(submissionFromXml);


		((BaseAssignmentSubmissionEdit) submission).setEvent(EVENT_ADD_ASSIGNMENT_SUBMISSION);

		return submission;
	}
	

	/**
	* Get a locked AssignmentSubmission object for editing.  Must commitEdit() to make official, or cancelEdit() when done!
	* @param submissionrReference - the reference for the submission.
	* @return An AssignmentSubmissionEdit object for editing.
	* @exception IdUnusedException if not found, or if not an AssignmentSubmissionEdit object
	* @exception PermissionException if the current user does not have permission to edit this submission.
	* @exception InUseException if the assignment is being edited by another user.
	*/
	public AssignmentSubmissionEdit editSubmission(String submissionReference)
		throws IdUnusedException, PermissionException, InUseException
	{
		// check security (throws if not permitted)
		unlock2(SECURE_UPDATE_ASSIGNMENT_SUBMISSION, SECURE_UPDATE_ASSIGNMENT, submissionReference);

		String submissionId = submissionId(submissionReference);

		// check for existance
		if (!m_submissionStorage.check(submissionId))
		{
			throw new IdUnusedException(submissionId);
		}

		// ignore the cache - get the AssignmentSubmission with a lock from the info store
		AssignmentSubmissionEdit submission = m_submissionStorage.edit(submissionId);
		if (submission == null) throw new InUseException(submissionId);

		((BaseAssignmentSubmissionEdit) submission).setEvent(EVENT_UPDATE_ASSIGNMENT_SUBMISSION);

		return submission;

	}   // editSubmission


	/**
	* Commit the changes made to an AssignmentSubmissionEdit object, and release the lock.
	* @param submission The AssignmentSubmissionEdit object to commit.
	*/
	public void commitEdit(AssignmentSubmissionEdit submission)
	{
		// check for closed edit
		if (!submission.isActiveEdit())
		{
			try { throw new Exception(); }
			catch (Exception e) { m_logger.warn(this + ".commitEdit(): closed AssignmentSubmissionEdit", e); }
			return;
		}
		
		// update the properties
		addLiveUpdateProperties(submission.getPropertiesEdit());

		submission.setTimeLastModified(TimeService.newTime());
		
		// complete the edit
		m_submissionStorage.commit(submission);
		
		//track it
		if (submission.getSubmitted())
		{
			// submitting a submission
			EventTrackingService.post(
				EventTrackingService.newEvent(EVENT_SUBMIT_ASSIGNMENT_SUBMISSION, submission.getReference(), true));	
 		}
		else
		{
			// saving a submission
			EventTrackingService.post(
				EventTrackingService.newEvent(EVENT_SAVE_ASSIGNMENT_SUBMISSION, submission.getReference(), true));	
		}
			 		
		// close the edit object
		((BaseAssignmentSubmissionEdit) submission).closeEdit();

	}	// commitEdit(Submission)

	
	/**
	* Cancel the changes made to a AssignmentSubmissionEdit object, and release the lock.
	* @param submission The AssignmentSubmissionEdit object to commit.
	*/
	public void cancelEdit(AssignmentSubmissionEdit submission)
	{
		// check for closed edit
		if (!submission.isActiveEdit())
		{
			try { throw new Exception(); }
			catch (Exception e) { m_logger.warn(this + ".cancelEdit(): closed AssignmentSubmissionEdit", e); }
			return;
		}

		// release the edit lock
		m_submissionStorage.cancel(submission);

		// close the edit object
		((BaseAssignmentSubmissionEdit) submission).closeEdit();

	}	// cancelEdit(Submission)
	
	
	/** 
	 * Removes an AssignmentSubmission and all references to it
	 * @param submission - the AssignmentSubmission to remove.
	 * @throws PermissionException if current User does not have permission to do this.
	 */
	public void removeSubmission(AssignmentSubmissionEdit submission)
		throws PermissionException
	{
		if(submission != null)
		{
			if(!submission.isActiveEdit())
			{
				try { throw new Exception(); }
				catch (Exception e) { m_logger.warn(this + ".removeSubmission(): closed AssignmentSubmissionEdit", e); }
				return;
			}

			// check security
			unlock(SECURE_REMOVE_ASSIGNMENT_SUBMISSION, submission.getReference());

			// complete the edit
			m_submissionStorage.remove(submission);
				
			// track event
			EventTrackingService.post(EventTrackingService.newEvent(EVENT_REMOVE_ASSIGNMENT_SUBMISSION, submission.getReference(), true));
			
			// close the edit object
			((BaseAssignmentSubmissionEdit)submission).closeEdit();

			// remove any realm defined for this resource
			try
			{
				RealmService.removeRealm(RealmService.editRealm(submission.getReference()));
			}
			catch (InUseException e) { m_logger.warn(this + ".removeSubmission: removing realm for : " + submission.getReference() + " : " + e); }
			catch (PermissionException e) { m_logger.warn(this + ".removeSubmission: removing realm for : " + submission.getReference() + " : " + e); }
			catch (IdUnusedException ignore) {}
		}
	}//removeSubmission


	/**
	* Access all AssignmentSubmission objects - known to us (not from external providers).
	* @return A list of AssignmentSubmission objects.
	*/
	protected List getSubmissions(String context)
	{
		List submissions = new Vector();

		if ((m_caching) && (m_submissionCache != null) && (!m_submissionCache.disabled()))
		{
			// if the cache is complete, use it
			if (m_submissionCache.isComplete())
			{
				submissions = m_submissionCache.getAll();
				// TODO: filter by context
			}
	
			// otherwise get all the submissions from storage
			else
			{
				// Note: while we are getting from storage, storage might change.  These can be processed
				// after we get the storage entries, and put them in the cache, and mark the cache complete.
				// -ggolden
				synchronized (m_submissionCache)
				{
					// if we were waiting and it's now complete...
					if (m_submissionCache.isComplete())
					{
						submissions = m_submissionCache.getAll();
						return submissions;
					}
	
					// save up any events to the cache until we get past this load
					m_submissionCache.holdEvents();
	
					submissions = m_submissionStorage.getAll(context);
	
					// update the cache, and mark it complete
					for (int i = 0; i < submissions.size(); i++)
					{
						AssignmentSubmission submission = (AssignmentSubmission) submissions.get(i);
						m_submissionCache.put(submission.getReference(), submission);
					}
	
					m_submissionCache.setComplete();
					// TODO: not really! just for context
	
					// now we are complete, process any cached events
					m_submissionCache.processEvents();
				}
			}
		}

		else
		{
//			// if we have done this already in this thread, use that
//			submissions = (List) CurrentService.getInThread(context+".assignment.submissions");
//			if (submissions == null)
//			{
				submissions = m_submissionStorage.getAll(context);
//
//				// "cache" the submissions in the current service in case they are needed again in this thread...
//				if (submissions != null)
//				{
//					CurrentService.setInThread(context+".assignment.submissions", submissions);
//				}
//			}
		}

		return submissions;

	}   // getAssignmentSubmissions
	
	
	/**
	 * Access list of all AssignmentContents created by the User.
	 * @param owner - The User who's AssignmentContents are requested.
	 * @return Iterator over all AssignmentContents owned by this User.
	 */
	public Iterator getAssignmentContents(User owner)
	{
		Vector retVal = new Vector();
		AssignmentContent aContent = null;
		List allContents = getAssignmentContents(owner.getId());
	
		for(int x = 0; x < allContents.size(); x++)
		{
			try
			{
				aContent = (AssignmentContent)allContents.get(x);
				if(aContent.getCreator().equals(owner.getId()));
					retVal.add(aContent);
			}
			catch(Exception e){}
		}

		if(retVal.isEmpty())
			return new EmptyIterator();
		else
			return retVal.iterator();

	}//getAssignmentContents(User)



	/**
	 * Access all the Assignments which have the specified AssignmentContent.
	 * @param content - The particular AssignmentContent.
	 * @return Iterator over all the Assignments with the specified AssignmentContent.
	 */
	public Iterator getAssignments(AssignmentContent content)
	{
		Vector retVal = new Vector();
		String contentReference = null;
		String tempContentReference = null;
		
		if(content != null)
		{
			contentReference = content.getReference();
			List allAssignments = getAssignments(content.getContext());
			Assignment tempAssignment = null;

			for(int y = 0; y < allAssignments.size(); y++)
			{
				tempAssignment = (Assignment)allAssignments.get(y);
				tempContentReference = tempAssignment.getContentReference();
				if(tempContentReference != null)
				{
					if(tempContentReference.equals(contentReference))
					{
						retVal.add(tempAssignment);
					}
				}
			}
		}

		if(retVal.isEmpty())
			return new EmptyIterator();
		else
			return retVal.iterator();
	}


	/**
	 * Access all the Assignemnts associated with a group.
	 * @param context - Describes the portlet context - generated with DefaultId.getChannel().
	 * @return Iterator over all the Assignments associated with a group.
	 */
	public Iterator getAssignmentsForContext(String context)
	{
		if(m_logger.isDebugEnabled())
			m_logger.debug("ASSIGNMENT : BASE SERVICE : GET ASSIGNMENTS FOR CONTEXT : CONTEXT : " + context);
		Assignment tempAssignment = null;
		Vector retVal = new Vector();
		List allAssignments = null;
		String aGroup = null;

		if(context != null)
		{
			allAssignments = getAssignments(context);
			for(int x = 0; x < allAssignments.size(); x++)
			{
				tempAssignment = (Assignment)allAssignments.get(x);
				//m_logger.info("ASSIGNMENT : BASE SERVICE : GET ASSIGNMENTS FOR CONTEXT : GOT AN ASSIGNMENT : " + tempAssignment.getTitle());
				//m_logger.info("ASSIGNMENT : BASE SERVICE : GET ASSIGNMENTS FOR CONTEXT : ASSIGNMENT'S CONTEXT : " + tempAssignment.getContext());
				
				if((context.equals(tempAssignment.getContext())) || (context.equals(getGroupNameFromContext(tempAssignment.getContext()))))
				{
					retVal.add(tempAssignment);
					//m_logger.info("ASSIGNMENT : BASE SERVICE : GET ASSIGNMENTS FOR CONTEXT : FOUND A MATCH");
				}
			}
		}

		if(retVal.isEmpty())
			return new EmptyIterator();
		else
			return retVal.iterator();

	}


	/**
	 * Access a User's AssignmentSubmission to a particular Assignment.
	 * @param assignmentReference The reference of the assignment.
	 * @param person - The User who's Submission you would like.
	 * @return AssignmentSubmission The user's submission for that Assignment.
	 * @throws IdUnusedException if there is no object with this id.
	 * @throws PermissionException if the current user is not allowed to access this.
	 */
	public AssignmentSubmission getSubmission(String assignmentReference, User person)
		throws IdUnusedException, PermissionException
	{
		//m_logger.info("ASSIGNMENT : BASE SERVICE : ENTERING GET SUBMISSION(assignmentRef, User)");
		//m_logger.info("ASSIGNMENT : BASE SERVICE : GET SUBMISSION(assignmentRef, User) : REF : " + assignmentReference);
		AssignmentSubmission retVal = null;
		AssignmentSubmission sub = null;
		String subString = null;
		List submitters = null;
		String aUserId = null;

		String assignmentId = assignmentId(assignmentReference);
		
		if(!m_assignmentStorage.check(assignmentId))
			throw new IdUnusedException(assignmentId);

		if((assignmentReference != null) && (person != null))
		{
			Assignment assign = m_assignmentStorage.get(assignmentId);

				// Match User and Assignment
			if(assign != null)
			{
				if(m_logger.isDebugEnabled())
					m_logger.debug("getSubmission : Got assignment with id : " + assign.getId());

				try
				{
					List submissions = getSubmissions(assign.getId());
					for(int z = 0; z < submissions.size(); z++)
					{
						sub = (AssignmentSubmission)submissions.get(z);
						if(m_logger.isDebugEnabled())
							m_logger.debug("getSubmission : submission id found : " + sub.getId());
						if(sub != null)
						{
							//m_logger.info("ASSIGNMENT : BASE SERVICE : GET SUBMISSION(assignmentRef, User) : submission parent assignment id : " + sub.getAssignmentId());
							if(sub.getAssignmentId().equals(assignmentId))
							{
								//m_logger.info("ASSIGNMENT : BASE SERVICE : GET SUBMISSION(assignmentRef, User) : SUB'S PARENT ASSIGNMENT ID MATCHES ASSIGNMENT ID");
								submitters = sub.getSubmitterIds();
								for(int a = 0; a < submitters.size(); a++)
								{
									aUserId = (String) submitters.get(a);
									if(m_logger.isDebugEnabled())
										m_logger.debug("getSubmission : comparing aUser id : " + aUserId + " and chosen user id : " + person.getId());
									if(aUserId.equals(person.getId()))
									{
										if(m_logger.isDebugEnabled())
											m_logger.debug("getSubmission : found a match : return value is " + sub.getId());
										retVal = sub;
									}
								}
							}
						}
					}
				}
				catch(Exception e)
				{
					m_logger.warn("getSubmission : EXCEPTION : " + e);
				}
			}
		}
			
		if(retVal != null)
		{
			unlock2(SECURE_ACCESS_ASSIGNMENT_SUBMISSION, SECURE_ACCESS_ASSIGNMENT, retVal.getReference());
			
			// track event
			// EventTrackingService.post(EventTrackingService.newEvent(EVENT_ACCESS_ASSIGNMENT_SUBMISSION, retVal.getReference(), false));
		}

		return retVal;
	}


	/**
	 * Get the submissions for an assignment.
	 * @param assignment - the Assignment who's submissions you would like.
	 * @return Iterator over all the submissions for an Assignment.
	 */
	public Iterator getSubmissions(Assignment assignment)
	{
		AssignmentSubmission submission = null;
		Vector retVal = new Vector();
		
		if(assignment != null)
		{
			List allSubmissions = getSubmissions(assignment.getId());
			for(int x = 0; x < allSubmissions.size(); x++)
			{
				submission = (AssignmentSubmission)allSubmissions.get(x);
				if(assignment.getId().equals(submission.getAssignmentId()))
					retVal.add(submission);
			}
		}

		if(retVal.isEmpty())
			return new EmptyIterator();
		else
			return retVal.iterator();
	}
	
	


	/**
	 * Access the AssignmentSubmission with the specified id.
	 * @param submissionReference - The reference of the AssignmentSubmission.
	 * @return The AssignmentSubmission corresponding to the id, or null if it does not exist.
	 * @throws IdUnusedException if there is no object with this id.
	 * @throws PermissionException if the current user is not allowed to access this.
	 */
	public AssignmentSubmission getSubmission(String submissionReference)
		throws IdUnusedException, PermissionException
	{		 
		if(m_logger.isDebugEnabled())
			m_logger.debug("ASSIGNMENT : BASE SERVICE : GET SUBMISSION : REF : " + submissionReference);
		
		AssignmentSubmission submission = null;
		
		String submissionId = submissionId(submissionReference);

		if ((m_caching) && (m_submissionCache != null) && (!m_submissionCache.disabled()))
		{
			// if we have it in the cache, use it
			if(m_submissionCache.containsKey(submissionReference))
				submission = (AssignmentSubmission)m_submissionCache.get(submissionReference);
			else
			{
				submission = m_submissionStorage.get(submissionId);
			
				// cache the result
				m_submissionCache.put(submissionReference, submission);
			}
		}

		else
		{
//			// if we have done this already in this thread, use that
//			submission = (AssignmentSubmission) CurrentService.getInThread(submissionId+".assignment.submission");
//			if (submission == null)
//			{
				submission = m_submissionStorage.get(submissionId);
//				
//				// "cache" the submission in the current service in case they are needed again in this thread...
//				if (submission != null)
//				{
//					CurrentService.setInThread(submissionId+".assignment.submission", submission);
//				}
//			}
		}

		if(submission == null)
			throw new IdUnusedException(submissionId);

		unlock2(SECURE_ACCESS_ASSIGNMENT_SUBMISSION, SECURE_ACCESS_ASSIGNMENT, submissionReference);

		if(m_logger.isDebugEnabled())
			m_logger.debug("ASSIGNMENT : BASE SERVICE : GOT SUBMISSION : ID : " + submission.getId());
			
		// track event
		// EventTrackingService.post(EventTrackingService.newEvent(EVENT_ACCESS_ASSIGNMENT_SUBMISSION, submission.getReference(), false));

		return submission;

	}// getAssignmentSubmission


	/**
	 * Return the reference root for use in resource references and urls.
	 * @return The reference root for use in resource references and urls.
	 */
	protected String getReferenceRoot()
	{
		return REFERENCE_ROOT;
	}
	
	/**
	* Update the live properties for an object when modified.
	*/
	protected void addLiveUpdateProperties(ResourcePropertiesEdit props)
	{
		props.addProperty(ResourceProperties.PROP_MODIFIED_BY,
				UsageSessionService.getSessionUserId());

		props.addProperty(ResourceProperties.PROP_MODIFIED_DATE,
			TimeService.newTime().toString());

	}	//  addLiveUpdateProperties

	/**
	* Create the live properties for the object.
	*/
	protected void addLiveProperties(ResourcePropertiesEdit props)
	{
		String current = UsageSessionService.getSessionUserId();
		props.addProperty(ResourceProperties.PROP_CREATOR, current);
		props.addProperty(ResourceProperties.PROP_MODIFIED_BY, current);
		
		String now = TimeService.newTime().toString();
		props.addProperty(ResourceProperties.PROP_CREATION_DATE, now);
		props.addProperty(ResourceProperties.PROP_MODIFIED_DATE, now);

	}	//  addLiveProperties


	/**
	 * Check permissions for adding an Assignment.
	 * @param context - Describes the portlet context - generated with DefaultId.getChannel().
	 * @return True if the current User is allowed to add an Assignment, false if not.
	 */
	public boolean allowAddAssignment(String context)
	{
		String resourceString = getAccessPoint(true) + Resource.SEPARATOR + "a" + Resource.SEPARATOR + context + Resource.SEPARATOR;

		if(m_logger.isDebugEnabled())
		{
			m_logger.debug("Entering allow add Assignment with resource string : " + resourceString);
			m_logger.debug("                                    context string : " + context);
		}
		return unlockCheck(SECURE_ADD_ASSIGNMENT, resourceString);
	}

	/**
	 * Check permissions for accessing an Assignment.
	 * @param assignmentReference - The Assignment's reference.
	 * @return True if the current User is allowed to access the Assignment, false if not.
	 */
	public boolean allowGetAssignment(String assignmentReference)
	{
		if(m_logger.isDebugEnabled())
			m_logger.debug("Entering allow get Assignment with reference string : " + assignmentReference);
		
		return unlockCheck(SECURE_ACCESS_ASSIGNMENT, assignmentReference);
	}

	/**
	 * Check permissions for updateing an Assignment.
	 * @param assignmentReference - The Assignment's reference.
	 * @return True if the current User is allowed to update the Assignment, false if not.
	 */
	public boolean allowUpdateAssignment(String assignmentReference)
	{
		if(m_logger.isDebugEnabled())
			m_logger.debug("Entering allow update Assignment with resource string : " + assignmentReference);

		return unlockCheck(SECURE_UPDATE_ASSIGNMENT, assignmentReference);
	}

	/**
	 * Check permissions for removing an Assignment.
	 * @param assignmentReference - The Assignment's reference.
	 * @return True if the current User is allowed to remove the Assignment, false if not.
	 */
	public boolean allowRemoveAssignment(String assignmentReference)
	{
		if(m_logger.isDebugEnabled())
			m_logger.debug("Entering allow remove Assignment with resource string : " + assignmentReference);

			// check security (throws if not permitted)
		return unlockCheck(SECURE_REMOVE_ASSIGNMENT, assignmentReference);
	}

	/**
	 * Check permissions for get AssignmentContent
	 * @param contentReference - The AssignmentContent reference.
	 * @return True if the current User is allowed to access the AssignmentContent, false if not.
	 */
	public boolean allowGetAssignmentContent(String contentReference)
	{
		if(m_logger.isDebugEnabled())
			m_logger.debug("Entering allow get AssignmentContent with resource string : " + contentReference);

			// check security (throws if not permitted)
		return unlockCheck(SECURE_ACCESS_ASSIGNMENT_CONTENT, contentReference);
	}

	/**
	 * Check permissions for updating AssignmentContent
	 * @param contentReference - The AssignmentContent reference.
	 * @return True if the current User is allowed to update the AssignmentContent, false if not.
	 */
	public boolean allowUpdateAssignmentContent(String contentReference)
	{
		if(m_logger.isDebugEnabled())
			m_logger.debug("Entering allow update AssignmentContent with resource string : " + contentReference);

			// check security (throws if not permitted)
		return unlockCheck(SECURE_UPDATE_ASSIGNMENT_CONTENT, contentReference);
	}

	/**
	 * Check permissions for adding an AssignmentContent.
	 * @param context - Describes the portlet context - generated with DefaultId.getChannel().
	 * @return True if the current User is allowed to add an AssignmentContent, false if not.
	 */
	public boolean allowAddAssignmentContent(String context)
	{
		String resourceString = getAccessPoint(true) + Resource.SEPARATOR + "c" + Resource.SEPARATOR + context + Resource.SEPARATOR;
		if(m_logger.isDebugEnabled())
			m_logger.debug("Entering allow add AssignmentContent with resource string : " + resourceString);

			// check security (throws if not permitted)
		return unlockCheck(SECURE_ADD_ASSIGNMENT_CONTENT, resourceString);
	}
	
	/**
	 * Check permissions for remove the AssignmentContent
	 * @param contentReference - The AssignmentContent reference.
	 * @return True if the current User is allowed to remove the AssignmentContent, false if not.
	 */
	public boolean allowRemoveAssignmentContent(String contentReference)
	{
		if(m_logger.isDebugEnabled())
			m_logger.debug("Entering allow remove assignment content with resource string : " + contentReference);

			// check security (throws if not permitted)
		return unlockCheck(SECURE_REMOVE_ASSIGNMENT_CONTENT, contentReference);
	}

	/**
	 * Check permissions for add AssignmentSubmission
	 * @param context - Describes the portlet context - generated with DefaultId.getChannel().
	 * @return True if the current User is allowed to add an AssignmentSubmission, false if not.
	 */
	public boolean allowAddSubmission(String context)
	{
			// check security (throws if not permitted)
		String resourceString = getAccessPoint(true) + Resource.SEPARATOR + "s" + Resource.SEPARATOR + context + Resource.SEPARATOR;

		if(m_logger.isDebugEnabled())
			m_logger.debug("Entering allow add Submission with resource string : " + resourceString);

		return unlockCheck(SECURE_ADD_ASSIGNMENT_SUBMISSION, resourceString);
	}

	/**
	 * Get the List of Users who can addSubmission() for this assignment.
	 * @param assignmentReference - a reference to an assignment
	 * @return the List (User) of users who can addSubmission() for this assignment.
	 */
	public List allowAddSubmissionUsers(String assignmentReference)
	{
		return SecurityService.unlockUsers(SECURE_ADD_ASSIGNMENT_SUBMISSION, assignmentReference);

	}	// allowAddSubmissionUsers

	/**
	 * Check permissions for accessing a Submission.
	 * @param submissionReference - The Submission's reference.
	 * @return True if the current User is allowed to get the AssignmentSubmission, false if not.
	 */
	public boolean allowGetSubmission(String submissionReference)
	{
		if(m_logger.isDebugEnabled())
			m_logger.debug("Entering allow get Submission with resource string : " + submissionReference);

		return unlockCheck2(SECURE_ACCESS_ASSIGNMENT_SUBMISSION, SECURE_ACCESS_ASSIGNMENT, submissionReference);
	}

	/**
	 * Check permissions for updating Submission.
	 * @param submissionReference - The Submission's reference.
	 * @return True if the current User is allowed to update the AssignmentSubmission, false if not.
	 */
	public boolean allowUpdateSubmission(String submissionReference)
	{
		if(m_logger.isDebugEnabled())
			m_logger.debug("Entering allow update Submission with resource string : " + submissionReference);

		return unlockCheck2(SECURE_UPDATE_ASSIGNMENT_SUBMISSION, SECURE_UPDATE_ASSIGNMENT, submissionReference);
	}

	/**
	 * Check permissions for remove Submission
	 * @param submissionReference - The Submission's reference.
	 * @return True if the current User is allowed to remove the AssignmentSubmission, false if not.
	 */
	public boolean allowRemoveSubmission(String submissionReference)
	{
		if(m_logger.isDebugEnabled())
			m_logger.debug("Entering allow remove Submission with resource string : " + submissionReference);

			// check security (throws if not permitted)
		return unlockCheck(SECURE_REMOVE_ASSIGNMENT_SUBMISSION, submissionReference);
	}
	
	public boolean allowGradeSubmission(String context)
	{
		String resourceString = getAccessPoint(true) + Resource.SEPARATOR + "a" + Resource.SEPARATOR + context + Resource.SEPARATOR;

		if(m_logger.isDebugEnabled())
		{
			m_logger.debug("Entering allow add Assignment with resource string : " + resourceString);
			m_logger.debug("                                    context string : " + context);
		}
		return unlockCheck(SECURE_GRADE_ASSIGNMENT_SUBMISSION, resourceString);
	}

	/**
	* Access the grades spreadsheet for the reference, either for an assignment or all assignments in a context.
	* @param ref The reference, either to a specific assignment, or just to an assignment context.
	* @return The grades spreadsheet bytes.
	* @throws IdUnusedException if there is no object with this id.
	* @throws PermissionException if the current user is not allowed to access this.
	*/
	public byte[] getGradesSpreadsheet(String ref)
		throws IdUnusedException, PermissionException
	{
		String typeGradesString = new String(REF_TYPE_GRADES + Resource.SEPARATOR);
		String context = ref.substring(ref.indexOf(typeGradesString) + typeGradesString.length());
			
		short rowNum = 0;
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("sheet 1");

		// Create a row and put some cells in it. Rows are 0 based.
		HSSFRow row = sheet.createRow(rowNum++);

		row.createCell((short)0).setCellValue("Assignment Submission Excel File for group ");

		// the bold font
		HSSFFont font = wb.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

		// the cell style with bold font
		HSSFCellStyle style = wb.createCellStyle();
		style.setFont(font);

		// set up the header cells
		row = sheet.createRow(rowNum++);
		short cellNum = 0;
		HSSFCell cell = row.createCell(cellNum++);
		cell.setCellStyle(style);
		cell.setCellValue("Name");
		
		Iterator assignments = getAssignmentsForContext(context);
		Vector assignmentTypeVector = new Vector();
		Vector assignmentSubmissionVector = new Vector();
		
		int index = 0;
		List members = new Vector();
		while (assignments.hasNext())
		{
			Assignment a = (Assignment) assignments.next();
			String deleted = a.getProperties().getProperty(ResourceProperties.PROP_ASSIGNMENT_DELETED);
			if ((deleted == null || deleted.equals("")) && (!a.getDraft()) && (a.getOpenTime().before(TimeService.newTime())))
			{
				if (index == 0)
				{
					members = allowAddSubmissionUsers(a.getReference());
				}
				assignmentTypeVector.add(index, new Integer(a.getContent().getTypeOfGrade()));
				Iterator submissions = getSubmissions(a);
				List submissionList = new Vector();
				while(submissions.hasNext())
				{
					submissionList.add(submissions.next());
				}
				assignmentSubmissionVector.add(index, submissionList);
				
				cell = row.createCell(cellNum++);
				cell.setCellStyle(style);
				cell.setCellValue(a.getTitle());
				
				index++;
			}
		}
		
		// is there a grade or not
		AssignmentSubmission submissionWithGrade = null;
		List submitterIds = null;
		String submitterId = "";
		User member = null;
		for (Iterator it= members.iterator(); it.hasNext(); )
		{
			// create one row for each user
			row = sheet.createRow(rowNum++);
			cellNum = 0;
			member = (User) it.next();
			// show user's name
			row.createCell(cellNum++).setCellValue(member.getSortName());

			for (int i = 0; i < assignmentSubmissionVector.size(); i++)
			{	
				// type for this assignment
				int assignmentType = ((Integer)assignmentTypeVector.get(i)).intValue();
				
				// submission for this assignment
				List submissions = (List) (assignmentSubmissionVector.get(i));
				
				// initialize it for every assignment
				submissionWithGrade = null;
				
				for (int k=0; k<submissions.size() && submissionWithGrade == null;k++)
				{	
					AssignmentSubmission s = (AssignmentSubmission) submissions.get(k);
					
					submitterIds = (List) s.getSubmitterIds();
					for(int a = 0; a < submitterIds.size(); a++)
					{
						submitterId = (String) submitterIds.get(a);
						
						if ( submitterId.equals(member.getId()))
						{
							// found the member's submission to the assignment
							// show user's grade
							if ((s!=null) && (s.getGraded()) && (s.getGradeReleased()))
							{
								submissionWithGrade = s;
							}
						}	// if
					}	// for
				}	// for
				
				if (submissionWithGrade != null)
				{
					if (assignmentType == 3)
					{
						try
						{
							//numeric cell type?
							String grade = submissionWithGrade.getGradeDisplay();
							Float.parseFloat(grade);
							
							cell = row.createCell(cellNum++);
							cell.setCellType(0); 
							cell.setCellValue(Float.parseFloat(grade));

							style = wb.createCellStyle();
							style.setDataFormat(wb.createDataFormat().getFormat("#,##0.0"));
							cell.setCellStyle(style);
						}
						catch (Exception e)
						{
							// if the grade is not numeric, let's make it as String type
							cell = row.createCell(cellNum++);
							cell.setCellType(1); 
							cell.setCellValue(submissionWithGrade.getGrade());
						}
						
					}
					else
					{
						// String cell type
						cell = row.createCell(cellNum++);
						cell.setCellType(1); 
						cell.setCellValue(submissionWithGrade.getGrade());
					}
				}	// if
				else
				{	
					// no grade to show yet
					if (assignmentType == 3)
					{
						// numeric cell type
						// set value to be 0 if there is no grade yet
						cell = row.createCell(cellNum++);
						cell.setCellType(0); 
						style = wb.createCellStyle();
						style.setDataFormat(wb.createDataFormat().getFormat("#,##0.0"));
						cell.setCellStyle(style);
					}
					else
					{
						// String cell type
						// set value to be "" if there is no grade yet
						cell = row.createCell(cellNum++);
						cell.setCellType(1); 
						cell.setCellValue("");
					}
				}	// if
			}	//	for
		}	//	for
		 
		Blob b = new Blob();
		try
		{
			wb.write(b.outputStream());
		}
		catch (IOException e)
		{
			m_logger.debug(this + "Can not output the grade spread sheet. ");
		}
		
		return b.getBytes();
		
	}	// getGradesSpreadsheet

	/**
	* Access the submissions zip for the assignment reference.
	* @param ref The assignment reference.
	* @return The submissions zip bytes.
	* @throws IdUnusedException if there is no object with this id.
	* @throws PermissionException if the current user is not allowed to access this.
	*/
	public byte[] getSubmissionsZip(String ref)
		throws IdUnusedException, PermissionException
	{
		if (m_logger.isDebugEnabled())
					m_logger.debug(this + ": getSubmissionsZip reference=" + ref);
		
		Blob b = new Blob();
		StringBuffer exceptionMessage = new StringBuffer();
		try
		{
			Assignment a = getAssignment(assignmentReferenceFromSubmissionsZipReference(ref));
			try
			{
				ZipOutputStream out = new ZipOutputStream(b.outputStream());
				InputStream in = null;
				
				// create the folder structor - named after the assignment's title
				String root = Validator.escapeZipEntry(a.getTitle()) + Resource.SEPARATOR;
				
				Iterator submissions = getSubmissions(a);
				String submittedText = "";
				if (!submissions.hasNext())
				{
					exceptionMessage.append("There is no submission yet. ");
				}

				// Create a buffer for reading the files
				byte[] buf = new byte[1024];
				int len;
				
				// Create the ZIP file				
				String submittersName = "";
				int count = 1;
				while (submissions.hasNext())
				{
					count = 1;
					submittersName = root;
					AssignmentSubmission s = (AssignmentSubmission) submissions.next();
					
					if (s.getSubmitted())
					{
						User[] submitters = s.getSubmitters();
						String submittersString = "";
						for (int i = 0; i < submitters.length; i++)
						{
							if (i>0)
							{
								submittersString = submittersString.concat("; ");
							}
							submittersString = submittersString.concat(submitters[i].getSortName());
						}
						submittersName = submittersName.concat(submittersString);
						submittedText= s.getSubmittedText();

						boolean added = false;
						while (!added)
						{
							try
							{
								submittersName = submittersName.concat("/");
								// create the folder structure - named after the submitter's name
								AssignmentContent ac = a.getContent();
								if (ac.getTypeOfSubmission() != Assignment.ATTACHMENT_ONLY_ASSIGNMENT_SUBMISSION)
								{
									//create the text file only when a text submission is allowed
									ZipEntry textEntry = new ZipEntry(submittersName + submittersString + "_submissionText.txt");
									in = (new Blob(FormattedText.convertFormattedTextToPlaintext(submittedText).getBytes())).inputStream();
									out.putNextEntry(textEntry);
									
									while ((len = in.read(buf)) > 0)
									{
										out.write(buf, 0, len);
									}
									out.closeEntry();
									in.close();
								}

								// create the attachment file(s)
								ReferenceVector attachments = s.getSubmittedAttachments();
								int attachedUrlCount = 0;
								for (int j=0; j < attachments.size(); j++)
								{
									Reference r = (Reference) attachments.get(j);
									try
									{
										ContentResource resource = ContentHostingService.getResource(r.getId());

										String contentType = resource.getContentType();
										byte[] content = resource.getContent();
										ResourceProperties props = r.getProperties();
										String displayName = props.getPropertyFormatted(props.getNamePropDisplayName());

										// for URL content type, encode a redirect to the body URL
										if (contentType.equalsIgnoreCase(ResourceProperties.TYPE_URL))
										{
											displayName = "attached_URL_" + attachedUrlCount;
											attachedUrlCount++;
										}

										ZipEntry attachmentEntry = new ZipEntry(submittersName + displayName);

										// if the attachment's content is null, use the empty constructor %%%zqian 
										if (content != null)
										{
											in = (new Blob(content)).inputStream();
										}
										else
										{
											in = (new Blob()).inputStream();
										}

										out.putNextEntry(attachmentEntry);
										while ((len = in.read(buf)) > 0)
										{
											out.write(buf, 0, len);
										}
										out.closeEntry();
										in.close();

									}
									catch (PermissionException e)
									{
										m_logger.debug(this + ": getSubmissionsZip--PermissionException submittersName=" + submittersName + " attachment reference=" + r);
									}
									catch (IdUnusedException e)
									{
										m_logger.debug(this + ": getSubmissionsZip--IdUnusedException submittersName=" + submittersName + " attachment reference=" + r);
									}
									catch (TypeException e)
									{
										m_logger.debug(this + ": getSubmissionsZip--TypeException: submittersName=" + submittersName + " attachment reference=" + r);
									}
									catch (IOException e)
									{
										m_logger.debug(this + ": getSubmissionsZip--IOException: Problem in creating the attachment file: submittersName=" + submittersName + " attachment reference=" + r);
									}
								}	// for

								added= true;
							}
							catch (IOException e)
							{
								exceptionMessage.append("Can not establish the IO to create zip file for user " + submittersName);
								m_logger.debug(this + ": getSubmissionsZip--IOException unable to create the zip file for user" + submittersName);
								submittersName= submittersName.substring(0, submittersName.length() - 1) + "_" + count++;
							}
						}	// while
					}	// submitted
					
				}	// while -- there is submission
				
				// Complete the ZIP file
				out.close();
			}
			catch (IOException e)
			{
				exceptionMessage.append("Can not establish the IO to create zip file. ");
				m_logger.debug(this + ": getSubmissionsZip--IOException unable to create the zip file for assignment " + a.getTitle());
			}
		}
		catch (IdUnusedException e)
		{
			if (m_logger.isDebugEnabled())
				m_logger.debug(this + ": getSubmissionsZip--IdUnusedException Unable to get assignment " + ref);
			throw new IdUnusedException(ref);
		}
		catch (PermissionException e)
		{
			m_logger.debug(this + ": getSubmissionsZip--PermissionException Not permitted to get assignment " + ref);
			throw new PermissionException(UsageSessionService.getSessionUserId(), SECURE_ACCESS_ASSIGNMENT, ref);
		}
		
		return b.getBytes();
		
	}	// getSubmissionsZip
	
	/**
	 * Get the string to form an assignment grade spreadsheet 
	 * @param context The assignment context String
	 * @param assignmentId The id for the assignment object; when null, indicates all assignment in that context
	 */
	public String gradesSpreadsheetReference(String context, String assignmentId)
	{
		// based on all assignment in that context
		String s = REFERENCE_ROOT + Resource.SEPARATOR + REF_TYPE_GRADES + Resource.SEPARATOR + context;
		if (assignmentId != null)
		{
			// based on the specified assignment only
			s = s.concat(Resource.SEPARATOR + assignmentId);
		}
		
		return s;
		
	}	// gradesSpreadsheetReference
	
	/**
	 * Get the string to form an assignment submissions zip file
	 * @param context The assignment context String
	 * @param assignmentReference The reference for the assignment object; 
	 */
	public String submissionsZipReference(String context, String assignmentReference)
	{
		// based on the specified assignment
		return REFERENCE_ROOT + Resource.SEPARATOR + REF_TYPE_SUBMISSIONS + Resource.SEPARATOR + context + Resource.SEPARATOR + assignmentReference;
		
	}	// submissionsZipReference


	/** 
 	 * Decode the submissionsZipReference string to get the assignment reference String
 	 * @param sReference The submissionZipReference String
 	 * @return The assignment reference String
 	 */
 	private String assignmentReferenceFromSubmissionsZipReference(String sReference)
 	{
 		// remove the String part relating to submissions zip reference
 		return sReference.substring (sReference.lastIndexOf (Resource.SEPARATOR + "assignment"));
 		
 	}	// assignmentReferenceFromSubmissionsZipReference

	/*******************************************************************************
	* ResourceService implementation
	*******************************************************************************/

	/**
	 * {@inheritDoc}
	 */
	public String getLabel()
	{
		return "assignment";
	}

	/**
	 * {@inheritDoc}
	 */
	public String archive(String siteId, Document doc, Stack stack, String archivePath, ReferenceVector attachments)
	{
		
		//m_logger.info(this + ".archive: stubbed");
		// prepare the buffer for the results log
		StringBuffer results = new StringBuffer();
		
		//String assignRef = assignmentReference(siteId, SiteService.MAIN_CONTAINER);
		results.append("archiving " + getLabel() + " context " + Resource.SEPARATOR + siteId + Resource.SEPARATOR + SiteService.MAIN_CONTAINER + ".\n");
		
		// start with an element with our very own (service) name
		Element element = doc.createElement(AssignmentService.class.getName());
		((Element)stack.peek()).appendChild(element);
		stack.push(element);
		
		Iterator assignmentsIterator = org.sakaiproject.service.legacy.assignment.cover.AssignmentService.getAssignmentsForContext(siteId);
		
		while (assignmentsIterator.hasNext())
		{
			Assignment assignment = (Assignment) assignmentsIterator.next();
			
			// archive this assignment
			Element el = assignment.toXml(doc, stack);
			element.appendChild(el);
			
			// in order to make the assignment.xml have a better structure
			// the content id attribute removed from the assignment node
			// the content will be a child of assignment node
			el.removeAttribute("assignmentcontent");
			
			// then archive the related content
			AssignmentContent content = (AssignmentContent) assignment.getContent();
			if (content != null)
			{
				Element contentEl = content.toXml(doc, stack);

				// assignment node has already kept the context info
				contentEl.removeAttribute("context");
				
				// collect message attachments
				ReferenceVector atts = content.getAttachments();
				
				for (int i = 0; i < atts.size(); i++)
				{
					Reference ref = (Reference) atts.elementAt(i);
					// if it's in the attachment area, and not already in the list
					if (	(ref.getReference().startsWith("/content/attachment/"))
					&&	(!attachments.contains(ref)))
					{
						attachments.add(ref);
					}
					
					// in order to make assignment.xml has the consistent format with the other xml files
					// move the attachments to be the children of the content, instead of the attributes
					String attributeString = "attachment" + i;
					String attRelUrl = contentEl.getAttribute(attributeString);
					contentEl.removeAttribute(attributeString);
					Element attNode = doc.createElement("attachment");
					attNode.setAttribute("relative-url", attRelUrl);
					contentEl.appendChild(attNode);
								
				} // for
				
				// make the content a childnode of the assignment node
				el.appendChild(contentEl);
				
				Iterator submissionsIterator = org.sakaiproject.service.legacy.assignment.cover.AssignmentService.getSubmissions(assignment);
				while (submissionsIterator.hasNext())
				{
					AssignmentSubmission submission = (AssignmentSubmission) submissionsIterator.next();
			
					// archive this assignment
					Element submissionEl = submission.toXml(doc, stack);
					el.appendChild(submissionEl);
					
				}
			} // if
		} // while
		
		stack.pop();

		return results.toString();

	} // archive

	/**
	* Replace the WT user id with the new qualified id
	* @param el The XML element holding the perproties
	* @param useIdTrans The HashMap to track old WT id to new CTools id
	 */
	protected void WTUserIdTrans(Element el, HashMap userIdTrans)
	{
		NodeList children4 = el.getChildNodes();
		int length4 = children4.getLength();
		for(int i4 = 0; i4 < length4; i4++)
		{
			Node child4 = children4.item(i4);
			if (child4.getNodeType() == Node.ELEMENT_NODE)
			{
				Element element4 = (Element)child4;
				if (element4.getTagName().equals("property"))
				{
					String creatorId = "";
					String modifierId = "";
					if (element4.hasAttribute("CHEF:creator"))
					{
						if ("BASE64".equalsIgnoreCase(element4.getAttribute("enc")))
						{
							creatorId = Xml.decodeAttribute(element4, "CHEF:creator");
						}
						else
						{
							creatorId = element4.getAttribute("CHEF:creator");
						}
						String newCreatorId = (String)userIdTrans.get(creatorId);
						if (newCreatorId != null)
						{
							Xml.encodeAttribute(element4, "CHEF:creator", newCreatorId);
							element4.setAttribute("enc","BASE64");
						}															
					}
					else if (element4.hasAttribute("CHEF:modifiedby"))
					{
						if ("BASE64".equalsIgnoreCase(element4.getAttribute("enc")))
						{
							modifierId = Xml.decodeAttribute(element4, "CHEF:modifiedby");
						}
						else
						{
							modifierId = element4.getAttribute("CHEF:modifiedby");
						}
						String newModifierId = (String)userIdTrans.get(modifierId);
						if (newModifierId != null)
						{
							Xml.encodeAttribute(element4, "CHEF:creator", newModifierId);
							element4.setAttribute("enc","BASE64");
						}
					}
				}
			}
		}
		
	} // WTUserIdTrans
		
	/**
	 * {@inheritDoc}
	 */
	public String merge(String siteId, Element root, String archivePath, String fromSiteId, Map attachmentNames, HashMap userIdTrans, Set userListAllowImport)
		{			
			// prepare the buffer for the results log
			StringBuffer results = new StringBuffer();
	
			int count = 0;
	
			try
			{
				// pass the DOM to get new assignment ids, and adjust attachments
				NodeList children2 = root.getChildNodes();
				
				int length2 = children2.getLength();
				for (int i2 = 0; i2 < length2; i2++)
				{
					Node child2 = children2.item(i2);
					if (child2.getNodeType() == Node.ELEMENT_NODE)
					{
						Element element2 = (Element)child2;
					
						if (element2.getTagName().equals("assignment"))
						{
							// a flag showing if continuing merging the assignment
							boolean goAhead = true;
							AssignmentContentEdit contentEdit = null;

							// element2 now - assignment node
							// adjust the id of this assignment
							//String newId = IdService.getUniqueId();
							element2.setAttribute("id", IdService.getUniqueId());
							element2.setAttribute("context", siteId);
							
							// cloneNode(false) - no children cloned
							Element el2clone = (Element)element2.cloneNode(false);
							
							// traverse this assignment node first to check if the person who last modified, has the right role.
							// if no right role, mark the flag goAhead to be false.
							NodeList children3 = element2.getChildNodes();
							int length3 = children3.getLength();
							for(int i3 = 0; i3 < length3; i3++)
							{
								Node child3 = children3.item(i3);
								if (child3.getNodeType() == Node.ELEMENT_NODE)
								{
									Element element3 = (Element)child3;
									
									// add the properties childnode to the clone of assignment node
									if (element3.getTagName().equals("properties"))
									{
										NodeList children6 = element3.getChildNodes();
										int length6 = children6.getLength();
										for (int i6 = 0; i6 < length6; i6++)
										{
											Node child6 = children6.item(i6);
											if (child6.getNodeType() == Node.ELEMENT_NODE)
											{
												Element element6 = (Element) child6;
												
												if (element6.getTagName().equals("property"))
												{
													if (element6.getAttribute("name").equalsIgnoreCase("CHEF:modifiedby"))
													{
														if ("BASE64".equalsIgnoreCase(element6.getAttribute("enc")))
														{
															String creatorId = Xml.decodeAttribute(element6, "value");
															if (!userListAllowImport.contains(creatorId)) goAhead = false;
														}
														else
														{
															String creatorId = element6.getAttribute("value");
															if (!userListAllowImport.contains(creatorId)) goAhead = false;
														}
													}
												}
											}
										}
									}
								}
							} // for
							
							// then, go ahead to merge the content and assignment
							if (goAhead)
							{
								for (int i3 = 0; i3 < length3; i3++)
								{
									Node child3 = children3.item(i3);
									if (child3.getNodeType() == Node.ELEMENT_NODE)
									{
										Element element3 = (Element)child3;
										
										// add the properties childnode to the clone of assignment node
										if (element3.getTagName().equals("properties"))
										{
											// add the properties childnode to the clone of assignment node
											el2clone.appendChild(element3.cloneNode(true));
										}
										else if (element3.getTagName().equals("content"))
										{
											// element3 now- content node
											// adjust the id of this content
											String newContentId = IdService.getUniqueId();
											element3.setAttribute("id", newContentId);
											element3.setAttribute("context", siteId);
											
											// clone the content node without the children of <properties>
											Element el3clone = (Element)element3.cloneNode(false);
											
											// update the assignmentcontent id in assignment node
											String assignContentId = "/assignment/c/" + siteId + "/" + newContentId;
											el2clone.setAttribute("assignmentcontent", assignContentId);
											
											// for content node, process the attachment or properties kids
											NodeList children5 = element3.getChildNodes();
											int length5 = children5.getLength();
											int attCount = 0;
											for(int i5 = 0; i5 < length5; i5++)
											{
												Node child5 = children5.item(i5);
												if (child5.getNodeType() == Node.ELEMENT_NODE)
												{
													Element element5 = (Element)child5;
													
													// for the node of "properties"
													if (element5.getTagName().equals("properties"))
													{
														// for the file from WT, preform userId translation when needed
														if (!userIdTrans.isEmpty())
														{
															WTUserIdTrans(element3, userIdTrans);
														}
													} // for the node of properties
													el3clone.appendChild(element5.cloneNode(true));
													
													// for "attachment" children
													if (element5.getTagName().equals("attachment"))
													{
														 // map the attachment area folder name
														// filter out the invalid characters in the attachment id
														//map the attachment area folder name
														String oldUrl = element5.getAttribute("relative-url");
														String newUrl = "";
														if (oldUrl.startsWith("/content/attachment/"))
														{
															newUrl = (String) attachmentNames.get(oldUrl);
															if (newUrl != null)
															{
																if (newUrl.startsWith("/attachment/"))
																	newUrl = "/content".concat(newUrl);
																	
																element5.setAttribute("relative-url", Validator.escapeQuestionMark(newUrl));
															}
														}
														
														// map any references to this site to the new site id
														else if (oldUrl.startsWith("/content/group/" + fromSiteId + "/"))
														{
															newUrl = "/content/group/"
																+ siteId
																+ oldUrl.substring(15 + fromSiteId.length());
															element5.setAttribute("relative-url", Validator.escapeQuestionMark(newUrl));
														}
														//put the attachment back to the attribute field of content
														//to satisfy the input need of mergeAssignmentContent
														String attachmentString = "attachment" + attCount;
														el3clone.setAttribute(attachmentString, newUrl);
														attCount++;
												
													} // if
												} // if
											} // for
											
											// create a newassignment content
											contentEdit = mergeAssignmentContent(el3clone);
											commitEdit(contentEdit);
										}
									}
								} // for

								el2clone.setAttribute("draft", (ArchiveService.SAKAI_msg_draft_import) ? "true" : "false");
								
								// merge in this assignment
								AssignmentEdit edit = mergeAssignment(el2clone);
								edit.setContent(contentEdit);
								commitEdit(edit);
							
								count++;
							} // if goAhead
						} // if			
					} // if
				} // for
			}
			catch (Exception any)
			{
				m_logger.warn(this + ".merge(): exception: ", any);
			}
	
			results.append("merging assignment " + siteId + " (" + count + ") assignments.\n");
			return results.toString();		
		 
		} // merge

	/**
	* import tool(s) contents from the source context into the destination context
	* @param fromContext The source context
	* @param toContext The destination context
	* @param resourceIds when null, all resources will be imported; otherwise, only resources with those ids will be imported
	*/
	public void importResources(String fromContext, String toContext, List resourceIds)
	{
		// import Assignment objects
		Iterator oAssignments = getAssignmentsForContext(fromContext);
		while (oAssignments.hasNext())
		{
			Assignment oAssignment = (Assignment) oAssignments.next();
			String oAssignmentId = oAssignment.getId();
			
			boolean toBeImported = true;
			if (resourceIds != null && resourceIds.size() > 0)
			{
				// if there is a list for import assignments, only import those assignments and relative submissions
				toBeImported = false;
				for (int m=0; m<resourceIds.size() && !toBeImported; m++)
				{
					if (((String)resourceIds.get(m)).equals(oAssignmentId))
					{
						toBeImported = true;
					}
				}
			}
			
			if (toBeImported)
			{
				AssignmentEdit nAssignment = null;
				AssignmentContentEdit nContent = null;
				
				if(!m_assignmentStorage.check(oAssignmentId))
				{
					
				}
				else
				{
					try
					{
						// add new Assignment content
						String oContentReference = oAssignment.getContentReference();
						String oContentId = contentId(oContentReference);
						if(!m_contentStorage.check(oContentId))
							throw new IdUnusedException(oContentId);
						else
						{
							AssignmentContent oContent = getAssignmentContent(oContentReference);
							nContent = addAssignmentContent(toContext);
							//attributes
	
							nContent.setAllowAttachments(oContent.getAllowAttachments());
							nContent.setContext(toContext);
							nContent.setGroupProject(oContent.getGroupProject());
							nContent.setHonorPledge(oContent.getHonorPledge());
							nContent.setIndividuallyGraded(oContent.individuallyGraded());
							nContent.setInstructions(oContent.getInstructions());
							nContent.setMaxGradePoint(oContent.getMaxGradePoint());
							nContent.setReleaseGrades(oContent.releaseGrades());
							nContent.setTimeLastModified(oContent.getTimeLastModified());
							nContent.setTitle(oContent.getTitle());
							nContent.setTypeOfGrade(oContent.getTypeOfGrade());
							nContent.setTypeOfSubmission(oContent.getTypeOfSubmission());
							//properties
							ResourcePropertiesEdit p = nContent.getPropertiesEdit();
							p.clear();
							p.addAll(oContent.getProperties());
							//attachment
							nContent.replaceAttachments(oContent.getAttachments());
							//complete the edit
							m_contentStorage.commit(nContent);
							((BaseAssignmentContentEdit) nContent).closeEdit();
						}
					}
					catch (Exception e)
					{
						if(m_logger.isWarnEnabled())
							m_logger.warn(this + e.toString());
					}
					
					if (nContent != null)
					{
						try
						{
							// add new assignment
							nAssignment = addAssignment(toContext);
							//attribute
							nAssignment.setCloseTime(oAssignment.getCloseTime());
							nAssignment.setContentReference(nContent.getReference());
							nAssignment.setContext(toContext);
							nAssignment.setDraft(true);
							nAssignment.setDropDeadTime(oAssignment.getDropDeadTime());
							nAssignment.setDueTime(oAssignment.getDueTime());
							nAssignment.setOpenTime(oAssignment.getOpenTime());
							nAssignment.setSection(oAssignment.getSection());
							nAssignment.setTitle(oAssignment.getTitle());
							//properties
							ResourcePropertiesEdit p = nAssignment.getPropertiesEdit();
							p.clear();
							p.addAll(oAssignment.getProperties());
							//complete the edit
							m_assignmentStorage.commit(nAssignment);
							((BaseAssignmentEdit) nAssignment).closeEdit();
						}
						catch (Exception ee)
						{
						}
					}
				}	// if-else
			}	// if
		}	// for
	}	//importResources
	/*******************************************************************************
	* Assignment Implementation
	*******************************************************************************/

	public class BaseAssignment
		implements Assignment
	{
		protected ResourcePropertiesEdit m_properties;
		protected String m_id;
		protected String m_assignmentContent;
		protected String m_title;
		protected String m_context;
		protected String m_section;
		protected Time m_openTime;
		protected Time m_dueTime;
		protected Time m_closeTime;
		protected Time m_dropDeadTime;
		protected List m_authors;
		protected boolean m_draft;
		

		/**
		 * Copy constructor
		 */
		public BaseAssignment(Assignment assignment)
		{
			setAll(assignment);
		}//copy constructor
		
		
		/**
		 * Constructor used in addAssignment
		 */
		public BaseAssignment(String id, String context)
		{
			m_properties = new BaseResourcePropertiesEdit();
			addLiveProperties(m_properties);
			m_id = id;
			m_assignmentContent = "";
			m_title = "";
			m_context = context;
			m_section = "";
			m_authors = new Vector();
			m_draft = true;
		}

		/**
		 * Reads the Assignment's attribute values from xml.
		 * @param s - Data structure holding the xml info.
		 */
		public BaseAssignment(Element el)
		{
			if(m_logger.isDebugEnabled())
				m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE ASSIGNMENT : ENTERING STORAGE CONSTRUCTOR");
			
			m_properties = new BaseResourcePropertiesEdit();

			int numAttributes = 0;
			String bool = null;
			String intString = null;
			String attributeString = null;
			String tempString = null;
			Reference tempReference = null;
			AssignmentContent tempContent = null;

			m_id  = el.getAttribute("id");
			if(m_logger.isDebugEnabled())
				m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE ASSIGNMENT : STORAGE CONSTRUCTOR : ASSIGNMENT ID : " + m_id);
			m_title = el.getAttribute("title");
			m_section = el.getAttribute("section");
			m_draft = getBool(el.getAttribute("draft"));
			if(m_logger.isDebugEnabled())
				m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE ASSIGNMENT : STORAGE CONSTRUCTOR : READ THROUGH REG ATTS");

			m_assignmentContent = el.getAttribute("assignmentcontent");
			if(m_logger.isDebugEnabled())
				m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE ASSIGNMENT : STORAGE CONSTRUCTOR : CONTENT ID : " + m_assignmentContent);

			m_openTime = getTimeObject(el.getAttribute("opendate"));
			m_dueTime = getTimeObject(el.getAttribute("duedate"));
			m_dropDeadTime = getTimeObject(el.getAttribute("dropdeaddate"));
			m_closeTime = getTimeObject(el.getAttribute("closedate"));
			m_context = el.getAttribute("context");


				// READ THE AUTHORS
			m_authors = new Vector();
			intString = el.getAttribute("numberofauthors");
			if(m_logger.isDebugEnabled())
			m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE ASSIGNMENT : STORAGE CONSTRUCTOR : number of authors : " + intString);
			try
			{
				numAttributes = Integer.parseInt(intString);

				for(int x = 0; x < numAttributes; x++)
				{
					if(m_logger.isDebugEnabled())
					m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE ASSIGNMENT : STORAGE CONSTRUCTOR : reading author # " + x);
					attributeString = "author" + x;
					tempString = el.getAttribute(attributeString);
					
					if(tempString != null)
					{
						if(m_logger.isDebugEnabled())
							m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE ASSIGNMENT : STORAGE CONSTRUCTOR : adding author # " + x + " id :  " + tempString);
						m_authors.add(tempString);
					}
				}
			}
			catch(Exception e)
			{
				m_logger.warn("ASSIGNMENT : BASE SERVICE : BASE ASSIGNMENT : STORAGE CONSTRUCTOR : Exception reading authors : " + e);
			}

			
				// READ THE PROPERTIES AND INSTRUCTIONS
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
			}

			if(m_logger.isDebugEnabled())
				m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE ASSIGNMENT : LEAVING STORAGE CONSTRUCTOR");

		}//storage constructor
		
		
		/**
		 * Takes the Assignment's attribute values and puts them into the xml document.
		 * @param s - Data structure holding the object to be stored.
		 * @param doc - The xml document.
		 */
		public Element toXml(Document doc, Stack stack)
		{
			if(m_logger.isDebugEnabled())
				m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE ASSIGNMENT : ENTERING TOXML");

			Element assignment = doc.createElement("assignment");

			if (stack.isEmpty())
			{
				doc.appendChild(assignment);
			}
			else
			{
				((Element)stack.peek()).appendChild(assignment);
			}
			stack.push(assignment);

				// SET ASSIGNMENT ATTRIBUTES
			String numItemsString = null;
			String attributeString = null;
			String itemString = null;
			Reference tempReference = null;
			assignment.setAttribute("id", m_id);
			assignment.setAttribute("title", m_title);
			assignment.setAttribute("section", m_section);
			assignment.setAttribute("context", m_context);
			assignment.setAttribute("assignmentcontent", m_assignmentContent);
			assignment.setAttribute("draft", getBoolString(m_draft));
			assignment.setAttribute("opendate", getTimeString(m_openTime));
			assignment.setAttribute("duedate", getTimeString(m_dueTime));
			assignment.setAttribute("dropdeaddate", getTimeString(m_dropDeadTime));
			assignment.setAttribute("closedate", getTimeString(m_closeTime));

			if(m_logger.isDebugEnabled())
				m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE ASSIGNMENT : TOXML : saved regular properties");


				// SAVE THE AUTHORS
			numItemsString = "" + m_authors.size();
			if(m_logger.isDebugEnabled())
				m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE ASSIGNMENT : TOXML : saving " + numItemsString + " authors");
				
			assignment.setAttribute("numberofauthors", numItemsString);
			for(int x = 0; x < m_authors.size(); x++)
			{
				attributeString = "author" + x;
				itemString = (String) m_authors.get(x);
				if(itemString != null)
				{
					assignment.setAttribute(attributeString, itemString);
					if(m_logger.isDebugEnabled())
						m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE ASSIGNMENT : TOXML : saving author : " + itemString);
				}
			}


				// SAVE THE PROPERTIES
			m_properties.toXml(doc, stack);
			m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE ASSIGNMENT : TOXML : SAVED PROPERTIES");
			stack.pop();

			if(m_logger.isDebugEnabled())
				m_logger.debug("ASSIGNMENT : BASE ASSIGNMENT : LEAVING TOXML");
			
			return assignment;

		}//toXml


		protected void setAll(Assignment assignment)
		{
			if(assignment != null)
			{
				m_id = assignment.getId();
				m_assignmentContent = assignment.getContentReference();
				m_authors = assignment.getAuthors();
				m_title = assignment.getTitle();
				m_context = assignment.getContext();
				m_section = assignment.getSection();
				m_openTime = assignment.getOpenTime();
				m_dueTime = assignment.getDueTime();
				m_closeTime = assignment.getCloseTime();
				m_dropDeadTime = assignment.getDropDeadTime();
				m_draft = assignment.getDraft();
				m_properties = new BaseResourcePropertiesEdit();
				m_properties.addAll(assignment.getProperties());
			}
		}

		public String getId()
		{
			return m_id;
		}

		/**
		* Access the URL which can be used to access the resource.
		* @return The URL which can be used to access the resource.
		*/
		public String getUrl()
		{
			return getAccessPoint(false) + Resource.SEPARATOR +  "a" + Resource.SEPARATOR + m_context + Resource.SEPARATOR + m_id;

		}   // getUrl

		/**
		* Access the internal reference which can be used to access the resource from within the system.
		* @return The the internal reference which can be used to access the resource from within the system.
		*/
		public String getReference()
		{
			return assignmentReference(m_context, m_id);

		}   // getReference
	
		/**
		* Access the resource's properties.
		* @return The resource's properties.
		*/
		public ResourceProperties getProperties()
		{
			return m_properties;
		}

		/**
		 * Access the list of authors.
		 * @return FlexStringArray of user ids.
		 */
		public List getAuthors()
		{
			return m_authors;
		}

		/**
		 * Add an author to the author list.
		 * @param author - The User to add to the author list.
		 */
		public void addAuthor(User author)
		{
			if(author != null)
				m_authors.add(author.getId());
		}

		/**
		 * Remove an author from the author list.
		 * @param author - the User to remove from the author list.
		 */
		public void removeAuthor(User author)
		{
			if(author != null)
				m_authors.remove(author.getId());
		}
		
		/**
		 * Access the creator of this object.
		 * @return String The creator's user id.
		 */
		public String getCreator()
		{
			return m_properties.getProperty(ResourceProperties.PROP_CREATOR);
		}

		/**
		 * Access the person of last modificaiton
		 * @return the User's Id
		 */
		public String getAuthorLastModified()
		{
			return m_properties.getProperty(ResourceProperties.PROP_MODIFIED_BY);
		}

		/**
		 * Access the title.
		 * @return The Assignment's title.
		 */
		public String getTitle()
		{
			return m_title;
		}

		/**
		 * Access the time that this object was created.
		 * @return The Time object representing the time of creation.
		 */
		public Time getTimeCreated()
		{
			try
			{
				return m_properties.getTimeProperty(ResourceProperties.PROP_CREATION_DATE);
			}
			catch (EmptyException e)
			{
				
			}
			catch (TypeException e)
			{
			}
			return null;
		}

		/**
		 * Access the time of last modificaiton.
		 * @return The Time of last modification.
		 */
		public Time getTimeLastModified()
		{
			try
			{
				return m_properties.getTimeProperty(ResourceProperties.PROP_MODIFIED_DATE);
			}
			catch (EmptyException e)
			{
				
			}
			catch (TypeException e)
			{
			}
			return null;
		}

		/**
		 * Access the AssignmentContent of this Assignment.
		 * @return The Assignment's AssignmentContent.
		 */
		public AssignmentContent getContent()
		{
			AssignmentContent retVal = null;
			if(m_assignmentContent != null)
			{
				try
				{
					retVal = getAssignmentContent(m_assignmentContent);
				}
				catch(Exception e){}
			}

			return retVal;
		}
		
		/**
		 * Access the reference of the AssignmentContent of this Assignment.
		 * @return The Assignment's reference.
		 */
		public String getContentReference()
		{
			return m_assignmentContent;
		}

		/**
		 * Access the id of the Assignment's group.
		 * @return The id of the group for which this Assignment is designed.
		 */
	 	public String getContext()
		{
			return m_context;
		}

		/**
		 * Access the section info
		 * @return The section String
		 */
	 	public String getSection()
		{
			return m_section;
		}

		/**
		 * Access the first time at which the assignment can be viewed;
		 * may be null.
		 * @return The Time at which the assignment is due, or null if unspecified.
		 */
		public Time getOpenTime()
		{
			return m_openTime;
		}

		/**
		 * Access the time at which the assignment is due;
		 * may be null.
		 * @return The Time at which the Assignment is due, or null if unspecified.
		 */
		public Time getDueTime()
		{
			return m_dueTime;
		}

		/**
		 * Access the drop dead time after which responses to this assignment are considered late;
		 * may be null.
		 * @return The Time object representing the drop dead time, or null if unspecified.
		 */
		public Time getDropDeadTime()
		{
			return m_dropDeadTime;
		}

		/**
		 * Access the close time after which this assignment can no longer be viewed, and
		 * after which submissions will not be accepted.  May be null.
		 * @return The Time after which the Assignment is closed, or null if unspecified.
		 */
		public Time getCloseTime()
		{
			return m_closeTime;
		}

		/**
		 * Get whether this is a draft or final copy.
		 * @return True if this is a draft, false if it is a final copy.
		 */
		public boolean getDraft()
		{
			return m_draft;
		}

		/**
		* Are these objects equal?  If they are both Assignment objects, and they have
		* matching id's, they are.
		* @return true if they are equal, false if not.
		*/
		public boolean equals(Object obj)
		{
			if (!(obj instanceof Assignment)) return false;
			return ((Assignment)obj).getId().equals(getId());

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
			if (!(obj instanceof Assignment)) throw new ClassCastException();

			// if the object are the same, say so
			if (obj == this) return 0;

			// start the compare by comparing their sort names
			int compare = getTitle().compareTo(((Assignment)obj).getTitle());

			// if these are the same
			if (compare == 0)
			{
				// sort based on (unique) id
				compare = getId().compareTo(((Assignment)obj).getId());
			}

			return compare;

		}	// compareTo

	}	// BaseAssignment


	/*******************************************************************************
	* AssignmentEdit implementation
	*******************************************************************************/

	/**
	* <p>BaseAssignmentEdit is an implementation of the CHEF AssignmentEdit object.</p>
	* 
	* @author University of Michigan, CHEF Software Development Team
	*/
	public class BaseAssignmentEdit
		extends BaseAssignment
		implements AssignmentEdit, SessionBindingListener
	{
		/** The event code for this edit. */
		protected String m_event = null;

		/** Active flag. */
		protected boolean m_active = false;

		/**
		* Construct from another Assignment object.
		* @param Assignment The Assignment object to use for values.
		*/
		public BaseAssignmentEdit(Assignment assignment)
		{
			super(assignment);

		}	// BaseAssignmentEdit

		/**
		* Construct.
		* @param id The assignment id.
		*/
		public BaseAssignmentEdit(String id, String context)
		{
			super(id, context);

		}   // BaseAssignmentEdit

		/**
		* Construct from information in XML.
		* @param el The XML DOM Element definining the Assignment.
		*/
		public BaseAssignmentEdit(Element el)
		{
			super(el);

		}	// BaseAssignmentEdit


		/**
		* Clean up.
		*/
		protected void finalize()
		{
			// catch the case where an edit was made but never resolved
			if (m_active)
			{
				cancelEdit(this);
			}

		}	// finalize

		
		/**
		 * Set the title.
		 * @param title - The Assignment's title.
		 */
		public void setTitle(String title)
		{
			m_title = title;
		}
		
		/**
		* Set the reference of the AssignmentContent of this Assignment.
		* @param String - the reference of the AssignmentContent.
		*/
		public void setContentReference(String contentReference)
		{
			if(contentReference != null)
				m_assignmentContent = contentReference;
		}

		/**
		 * Set the AssignmentContent of this Assignment.
		 * @param content - the Assignment's AssignmentContent.
		 */
		public void setContent(AssignmentContent content)
		{
			if(content != null)
				m_assignmentContent = content.getReference();
		}
		
		/**
		 * Set the context at the time of creation.
		 * @param context -   the context string.
		 */
	 	public void setContext(String context)
		{
			m_context = context;
		}
		
		/**
		 * Set the section info
		 * @param sectionId - The section id
		 */
	 	public void setSection(String sectionId)
		{
			m_section = sectionId;
		}

		/**
		 * Set the first time at which the assignment can be viewed;
		 * may be null.
		 * @param opentime - The Time at which the Assignment opens.
		 */
		public void setOpenTime(Time opentime)
		{
			m_openTime = opentime;
		}
		
		/**
		 * Set the time at which the assignment is due;
		 * may be null.
		 * @param dueTime - The Time at which the Assignment is due.
		 */
		public void setDueTime(Time duetime)
		{
			m_dueTime = duetime;
		}

		/**
		 * Set the drop dead time after which responses to this assignment are considered late;
		 * may be null.
		 * @param dropdeadtime - The Time object representing the drop dead time.
		 */
		public void setDropDeadTime(Time dropdeadtime)
		{
			m_dropDeadTime = dropdeadtime;
		}
		
		/**
		 * Set the time after which this assignment can no longer be viewed,
		 * and after which submissions will not be accepted. May be null.
		 * @param closetime - The Time after which the Assignment is closed, or null if unspecified.
		 */
		public void setCloseTime(Time closetime)
		{
			m_closeTime = closetime;
		}
		
		/**
		 * Set whether this is a draft or final copy.
		 * @param draft - true if this is a draft, false if it is a final copy.
		 */
		public void setDraft(boolean draft)
		{
			m_draft = draft;
		}
		
		/**
		* Take all values from this object.
		* @param user The user object to take values from.
		*/
		protected void set(Assignment assignment)
		{
			setAll(assignment);

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
		* SessionBindingListener implementation
		*******************************************************************************/
	
		public void valueBound(SessionBindingEvent event) {}
	
		public void valueUnbound(SessionBindingEvent event)
		{
			if (m_logger.isDebugEnabled())
				m_logger.debug(this + ".valueUnbound()");
	
			// catch the case where an edit was made but never resolved
			if (m_active)
			{
				cancelEdit(this);
			}
	
		}	// valueUnbound
	
	}   // BaseAssignmentEdit



	/*******************************************************************************
	* AssignmentContent Implementation
	*******************************************************************************/

	public class BaseAssignmentContent
		implements AssignmentContent
	{
		protected ResourcePropertiesEdit m_properties;
		protected String m_id;
		protected String m_context;
		protected ReferenceVector m_attachments;
		protected List m_authors;
		protected String m_title;
		protected String m_instructions;
		protected int m_honorPledge;
		protected int m_typeOfSubmission;
		protected int m_typeOfGrade;
		protected int m_maxGradePoint;
		protected boolean m_groupProject;
		protected boolean m_individuallyGraded;
		protected boolean m_releaseGrades;
		protected boolean m_allowAttachments;
		protected Time m_timeCreated;
		protected Time m_timeLastModified;

		/**
		 * Copy constructor.
		 */
		public BaseAssignmentContent(AssignmentContent content)
		{
			setAll(content);
		}

		/**
		 * Constructor used in addAssignmentContent.
		 */
		public BaseAssignmentContent(String id, String context)
		{
			m_id = id;
			m_context = context;
			m_properties = new BaseResourcePropertiesEdit();
			addLiveProperties(m_properties);
			m_authors = new Vector();
			m_attachments = new ReferenceVector();
			m_title = "";
			m_instructions = "";
			m_honorPledge = Assignment.HONOR_PLEDGE_NOT_SET;
			m_typeOfSubmission = Assignment.ASSIGNMENT_SUBMISSION_TYPE_NOT_SET;
			m_typeOfGrade = Assignment.GRADE_TYPE_NOT_SET;
			m_maxGradePoint = 0;
			m_timeCreated = TimeService.newTime();
			m_timeLastModified = TimeService.newTime();
		}


		/**
		 * Reads the AssignmentContent's attribute values from xml.
		 * @param s - Data structure holding the xml info.
		 */
		public BaseAssignmentContent(Element el)
		{
			int numAttributes = 0;
			String bool = null;
			String intString = null;
			String attributeString = null;
			String tempString = null;
			Reference tempReference = null;
			if(m_logger.isDebugEnabled())
				m_logger.debug("DB : DbCachedAssignmentContent : Entering read");

			m_id = el.getAttribute("id");
			m_context = el.getAttribute("context");
			m_title = el.getAttribute("title");
			m_groupProject = getBool(el.getAttribute("groupproject"));
			m_individuallyGraded = getBool(el.getAttribute("indivgraded"));
			m_releaseGrades = getBool(el.getAttribute("releasegrades"));
			m_allowAttachments = getBool(el.getAttribute("allowattach"));
			m_timeCreated = getTimeObject(el.getAttribute("datecreated"));
			m_timeLastModified = getTimeObject(el.getAttribute("lastmod"));

			m_instructions = FormattedText.decodeFormattedTextAttribute(el, "instructions");			

			try
			{
				m_honorPledge = Integer.parseInt(el.getAttribute("honorpledge"));
			}
			catch(Exception e)
			{
				m_logger.warn(this + " Exception parsing honor pledge int from xml file string : " + e);
			}

			try
			{
				m_typeOfSubmission = Integer.parseInt(el.getAttribute("submissiontype"));
			}
			catch(Exception e)
			{
				m_logger.warn(this + " Exception parsing submission type int from xml file string : " + e);
			}

			try
			{
				m_typeOfGrade = Integer.parseInt(el.getAttribute("typeofgrade"));
			}
			catch(Exception e)
			{
				m_logger.warn(this + " Exception parsing grade type int from xml file string : " + e);
			}

			try
			{
				// %%%zqian 
				// read the scaled max grade point first; if there is none, get the old max grade value and multiple by 10
				String maxGradePoint = StringUtil.trimToNull(el.getAttribute("scaled_maxgradepoint"));
				if (maxGradePoint == null)
				{
					maxGradePoint = StringUtil.trimToNull(el.getAttribute("maxgradepoint"));
					if (maxGradePoint != null)
					{
						maxGradePoint = maxGradePoint + "0";
					}
				}
				m_maxGradePoint = Integer.parseInt(maxGradePoint);
			}
			catch(Exception e)
			{
				m_logger.warn(this + " Exception parsing maxgradepoint int from xml file string : " + e);
			}


				// READ THE AUTHORS
			m_authors = new Vector();
			intString = el.getAttribute("numberofauthors");
			try
			{
				numAttributes = Integer.parseInt(intString);

				for(int x = 0; x < numAttributes; x++)
				{
					attributeString = "author" + x;
					tempString = el.getAttribute(attributeString);
					if(tempString != null)
						m_authors.add(tempString);
				}
			}
			catch(Exception e)
			{
				m_logger.warn("DB : DbCachedContent : Exception reading authors : " + e);
			}

				// READ THE ATTACHMENTS
			m_attachments = new ReferenceVector();
			if(m_logger.isDebugEnabled())
			m_logger.debug("DB : DbCachedContent : Reading attachments : ");
			intString = el.getAttribute("numberofattachments");
			if(m_logger.isDebugEnabled())
			m_logger.debug("DB : DbCachedContent : num attachments : " + intString);
			try
			{
				numAttributes = Integer.parseInt(intString);

				for(int x = 0; x < numAttributes; x++)
				{
					attributeString = "attachment" + x;
					tempString = el.getAttribute(attributeString);
					if(tempString != null)
					{
						tempReference = new Reference(tempString);
						m_attachments.add(tempReference);
						if(m_logger.isDebugEnabled())
						m_logger.debug("DB : DbCachedContent : " + attributeString + " : " + tempString);
					}
				}
			}
			catch(Exception e)
			{
				m_logger.warn("DB : DbCachedContent : Exception reading attachments : " + e);
			}

				// READ THE PROPERTIES
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
				// old style of encoding
				else if(element.getTagName().equals("instructions-html") || element.getTagName().equals("instructions-formatted") || element.getTagName().equals("instructions"))
				{
					if ((element.getChildNodes() != null) && (element.getChildNodes().item(0) != null))
					{
						m_instructions = element.getChildNodes().item(0).getNodeValue();
						if (element.getTagName().equals("instructions"))
							m_instructions = FormattedText.convertPlaintextToFormattedText(m_instructions);
						if (element.getTagName().equals("instructions-formatted"))
						    m_instructions = FormattedText.convertOldFormattedText(m_instructions);
						if(m_logger.isDebugEnabled())
						m_logger.debug("XML : DbCachedAssignmentContent : instructions : " + m_instructions);
					}
					if (m_instructions == null)
					{
						m_instructions = "";
					}
				}				
			}

			if(m_logger.isDebugEnabled())
				m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE CONTENT : LEAVING STORAGE CONSTRUTOR");
			
		}// storage constructor
		
		
		
		
		/**
		 * Takes the AssignmentContent's attribute values and puts them into the xml document.
		 * @param s - Data structure holding the object to be stored.
		 * @param doc - The xml document.
		 */
		public Element toXml(Document doc, Stack stack)
		{
			if(m_logger.isDebugEnabled())
				m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE ASSIGNMENT : ENTERING TOXML");
			
			Element content = doc.createElement("content");
			
			if (stack.isEmpty())
			{
				doc.appendChild(content);
			}
			else
			{
				((Element)stack.peek()).appendChild(content);
			}
			stack.push(content);

			String numItemsString = null;
			String attributeString = null;
			String itemString = null;
			Reference tempReference = null;

			content.setAttribute("id", m_id);
			content.setAttribute("context", m_context);
			content.setAttribute("title", m_title);
			content.setAttribute("groupproject", getBoolString(m_groupProject));
			content.setAttribute("indivgraded", getBoolString(m_individuallyGraded));
			content.setAttribute("releasegrades", getBoolString(m_releaseGrades));
			content.setAttribute("allowattach", getBoolString(m_allowAttachments));
			content.setAttribute("honorpledge", String.valueOf(m_honorPledge));
			content.setAttribute("submissiontype", String.valueOf(m_typeOfSubmission));
			content.setAttribute("typeofgrade", String.valueOf(m_typeOfGrade));
			content.setAttribute("scaled_maxgradepoint", String.valueOf(m_maxGradePoint));
			content.setAttribute("datecreated", getTimeString(m_timeCreated));
			content.setAttribute("lastmod", getTimeString(m_timeLastModified));

			if(m_logger.isDebugEnabled())
				m_logger.debug( "ASSIGNMENT : BASE SERVICE : BASE CONTENT : TOXML : SAVED REGULAR PROPERTIES");


				// SAVE THE AUTHORS
			numItemsString = "" + m_authors.size();
			content.setAttribute("numberofauthors", numItemsString);
			for(int x = 0; x < m_authors.size(); x++)
			{
				attributeString = "author" + x;
				itemString = (String) m_authors.get(x);
				if(itemString != null)
					content.setAttribute(attributeString, itemString);
			}

			if(m_logger.isDebugEnabled())
				m_logger.debug( "ASSIGNMENT : BASE SERVICE : BASE CONTENT : TOXML : SAVED AUTHORS");
			
			
				// SAVE THE ATTACHMENTS
			numItemsString = "" + m_attachments.size();
			content.setAttribute("numberofattachments", numItemsString);
			for(int x = 0; x < m_attachments.size(); x++)
			{
				attributeString = "attachment" + x;
				tempReference = (Reference)m_attachments.get(x);
				itemString = tempReference.getReference();
				if(itemString != null)
					content.setAttribute(attributeString, itemString);
			}


				// SAVE THE PROPERTIES
			m_properties.toXml(doc, stack);
			
			if(m_logger.isDebugEnabled())
				m_logger.debug( "ASSIGNMENT : BASE SERVICE : BASE CONTENT : TOXML : SAVED REGULAR PROPERTIES");
			
			stack.pop();


			// SAVE THE INSTRUCTIONS
			FormattedText.encodeFormattedTextAttribute(content, "instructions", m_instructions);
			
			return content;
			
		}//toXml
		
		
		protected void setAll(AssignmentContent content)
		{
			if(content != null)
			{
				m_id = content.getId();
				m_context = content.getContext();
				m_authors = content.getAuthors();
				m_attachments = content.getAttachments();
				m_title = content.getTitle();
				m_instructions = content.getInstructions();
				m_honorPledge = content.getHonorPledge();
				m_typeOfSubmission = content.getTypeOfSubmission();
				m_typeOfGrade = content.getTypeOfGrade();
				m_maxGradePoint = content.getMaxGradePoint();
				m_groupProject = content.getGroupProject();
				m_individuallyGraded = content.individuallyGraded();
				m_releaseGrades = content.releaseGrades();
				m_allowAttachments = content.getAllowAttachments();
				m_timeCreated = content.getTimeCreated();
				m_timeLastModified = content.getTimeLastModified();
				m_properties = new BaseResourcePropertiesEdit();
				m_properties.addAll(content.getProperties());
			}
		}
		
		
		public String getId()
		{
			return m_id;
		}

		/**
		* Access the URL which can be used to access the resource.
		* @return The URL which can be used to access the resource.
		*/
		public String getUrl()
		{
			return getAccessPoint(false) + Resource.SEPARATOR + "c" + Resource.SEPARATOR + m_context + Resource.SEPARATOR + m_id;

		}   // getUrl

		/**
		* Access the internal reference which can be used to access the resource from within the system.
		* @return The the internal reference which can be used to access the resource from within the system.
		*/
		public String getReference()
		{
			return contentReference(m_context, m_id);

		}   // getReference
		
		/**
		* Access the resource's properties.
		* @return The resource's properties.
		*/
		public ResourceProperties getProperties()
		{
			return m_properties;
		}
		
		/*******************************************************************************
		* AttachmentContainer Implementation
		*******************************************************************************/

		/**
		* Access the attachments.
		* @return The set of attachments (a ReferenceVector containing Reference objects) (may be empty).
		*/
		public ReferenceVector getAttachments()
		{
			return m_attachments;
		}



		/*******************************************************************************
		* AssignmentContent Implementation
		*******************************************************************************/

		/**
		 * Access the AssignmentContent's context at the time of creation.
		 * @return String - the context string.
		 */
		public String getContext()
		{
			return m_context;
		}
		
		
		/**
		 * Access the list of authors.
		 * @return FlexStringArray of user ids.
		 */
		public List getAuthors()
		{
			return m_authors;
		}
		
		/**
		 * Access the creator of this object.
		 * @return The User object representing the creator.
		 */
		public String getCreator()
		{
			return m_properties.getProperty(ResourceProperties.PROP_CREATOR);
		}

		/**
		 * Access the person of last modificaiton
		 * @return the User
		 */
		public String getAuthorLastModified()
		{
			return m_properties.getProperty(ResourceProperties.PROP_MODIFIED_BY);
		}

		/**
		 * Access the title.
		 * @return The Assignment's title.
		 */
		public String getTitle()
		{
			return m_title;
		}

		/**
		 * Access the instructions.
		 * @return The Assignment Content's instructions.
		 */
		public String getInstructions()
		{
			return m_instructions;
		}

		/**
		 * Get the type of valid submission.
		 * @return int - Type of Submission.
		 */
		public int getTypeOfSubmission()
		{
			return m_typeOfSubmission;
		}
		
		/**
		 * Access a string describing the type of grade.
		 * @param gradeType - The integer representing the type of grade.
		 * @return Description of the type of grade.
		 */
		public String getTypeOfGradeString(int type)
		{
			String retVal = null;

			switch(type)
			{
				case 1:
					retVal = Assignment.UNGRADED_GRADE_TYPE_STRING;
					break;

				case 2:
					retVal = Assignment.LETTER_GRADE_TYPE_STRING;
					break;

				case 3:
					retVal = Assignment.SCORE_GRADE_TYPE_STRING;
					break;

				case 4:
					retVal = Assignment.PASS_FAIL_GRADE_TYPE_STRING;
					break;

				case 5:
					retVal = Assignment.CHECK_GRADE_TYPE_STRING;
					break;

				default:
					retVal = "Unknown Grade Type";
					break;
			}

			return retVal;
		}

		/**
		* Get the grade type.
		* @return gradeType - The type of grade.
		*/
		public int getTypeOfGrade()
		{
			return m_typeOfGrade;
		}

		/**
		* Get the maximum grade for grade type = SCORE_GRADE_TYPE(3)
		* @return The maximum grade score.
		*/
		public int getMaxGradePoint()
		{
			return m_maxGradePoint;
		}
		
		/**
		* Get the maximum grade for grade type = SCORE_GRADE_TYPE(3)
		* Formated to show one decimal place
		* @return The maximum grade score.
		*/
		public String getMaxGradePointDisplay()
		{
			// formated to show one decimal place, for example, 1000 to 100.0
			String one_decimal_maxGradePoint = m_maxGradePoint/10 + "." + (m_maxGradePoint%10);
			return one_decimal_maxGradePoint;
		}

		/**
		 * Get whether this project can be a group project.
		 * @return True if this can be a group project, false otherwise.
		 */
		public boolean getGroupProject()
		{
			return m_groupProject;
		}

		/**
		 * Get whether group projects should be individually graded.
		 * @return individGraded - true if projects are individually graded, false
		 * if grades are given to the group.
		 */
		public boolean individuallyGraded()
		{
			return m_individuallyGraded;
		}
		
		/**
		 * Gets whether grades can be released once submissions are graded.
		 * @return true if grades can be released once submission are graded, false if
		 * they must be released manually.
		 */
		public boolean releaseGrades()
		{
			return m_releaseGrades;
		}
		
		/**
		* Get the Honor Pledge type;
		* values are NONE and ENGINEERING_HONOR_PLEDGE.
		* @return the Honor Pledge value.
		*/
		public int getHonorPledge()
		{
			return m_honorPledge;
		}

		/**
		 * Does this Assignment allow attachments?
		 * @return true if the Assignment allows attachments, false otherwise?
		 */
		public boolean getAllowAttachments()
		{
			return m_allowAttachments;
		}

		/**
		 * Access the time that this object was created.
		 * @return The Time object representing the time of creation.
		 */
		public Time getTimeCreated()
		{
			return m_timeCreated;
		}

		/**
		 * Access the time of last modificaiton.
		 * @return The Time of last modification.
		 */
		public Time getTimeLastModified()
		{
			return m_timeLastModified;
		}
		
		/**
		* Is this AssignmentContent selected for use by an Assignment ?
		*/
		public boolean inUse()
		{
			boolean retVal = false;
			Assignment assignment = null;
			List allAssignments = getAssignments(m_context);
			for(int x = 0; x < allAssignments.size(); x++)
			{
				assignment = (Assignment)allAssignments.get(x);
				if(assignment.getContentReference().equals(getReference()))
					return true;
			}

			return retVal;
		}

		/**
		* Are these objects equal?  If they are both AssignmentContent objects,
		* and they have matching id's, they are.
		* @return true if they are equal, false if not.
		*/
		public boolean equals(Object obj)
		{
			if (!(obj instanceof AssignmentContent)) return false;
			return ((AssignmentContent)obj).getId().equals(getId());

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
			if (!(obj instanceof AssignmentContent)) throw new ClassCastException();

			// if the object are the same, say so
			if (obj == this) return 0;

			// start the compare by comparing their sort names
			int compare = getTitle().compareTo(((AssignmentContent)obj).getTitle());

			// if these are the same
			if (compare == 0)
			{
				// sort based on (unique) id
				compare = getId().compareTo(((AssignmentContent)obj).getId());
			}

			return compare;

		}	// compareTo

	}//BaseAssignmentContent


	
	
	/*******************************************************************************
	* AssignmentContentEdit implementation
	*******************************************************************************/

	/**
	* <p>BaseAssignmentContentEdit is an implementation of the CHEF AssignmentContentEdit object.</p>
	* 
	* @author University of Michigan, CHEF Software Development Team
	*/
	public class BaseAssignmentContentEdit
		extends BaseAssignmentContent
		implements AttachmentContainer, AssignmentContentEdit, SessionBindingListener
	{
		/** The event code for this edit. */
		protected String m_event = null;

		/** Active flag. */
		protected boolean m_active = false;

		/**
		* Construct from another AssignmentContent object.
		* @param AssignmentContent The AssignmentContent object to use for values.
		*/
		public BaseAssignmentContentEdit(AssignmentContent assignmentContent)
		{
			super(assignmentContent);

		}	// BaseAssignmentContentEdit

		/**
		* Construct.
		* @param id The AssignmentContent id.
		*/
		public BaseAssignmentContentEdit(String id, String context)
		{
			super(id, context);

		}   // BaseAssignmentContentEdit

		/**
		* Construct from information in XML.
		* @param el The XML DOM Element definining the AssignmentContent.
		*/
		public BaseAssignmentContentEdit(Element el)
		{
			super(el);

		}	// BaseAssignmentContentEdit


		/**
		* Clean up.
		*/
		protected void finalize()
		{
			// catch the case where an edit was made but never resolved
			if (m_active)
			{
				cancelEdit(this);
			}

		}	// finalize

		
		/*******************************************************************************
		* AttachmentContainer Implementation
		*******************************************************************************/

		/**
		* Add an attachment.
		* @param ref - The attachment Reference.
		*/
		public void addAttachment(Reference ref)
		{
			if(ref != null)
				m_attachments.add(ref);
		}

		/**
		* Remove an attachment.
		* @param ref - The attachment Reference to remove (the one removed will equal this, they need not be ==).
		*/
		public void removeAttachment(Reference ref)
		{
			if(ref != null)
				m_attachments.remove(ref);
		}

		/**
		* Replace the attachment set.
		* @param attachments - A ReferenceVector that will become the new set of attachments.
		*/
		public void replaceAttachments(ReferenceVector attachments)
		{
			m_attachments = attachments;
		}

		/**
		* Clear all attachments.
		*/
		public void clearAttachments()
		{
			m_attachments.clear();
		}
		
		
		
		/*******************************************************************************
		* AssignmentContentEdit Implementation
		*******************************************************************************/
		
		/**
		 * Set the title.
		 * @param title - The Assignment's title.
		 */
		public void setTitle(String title)
		{
			m_title = title;
		}

		/**
		 * Set the instructions.
		 * @param instructions - The Assignment's instructions.
		 */
		public void setInstructions(String instructions)
		{
			m_instructions = instructions;
		}
		
		/**
		 * Set the context at the time of creation.
		 * @param context -   the context string.
		 */
	 	public void setContext(String context)
		{
			m_context = context;
		}
		
		/**
		 * Set the type of valid submission.
		 * @param int - Type of Submission.
		 */
		public void setTypeOfSubmission(int type)
		{
			m_typeOfSubmission = type;
		}
		
		/**
		* Set the grade type.
		* @param gradeType - The type of grade.
		*/
		public void setTypeOfGrade(int gradeType)
		{
			m_typeOfGrade = gradeType;
		}

		/**
		* Set the maximum grade for grade type = SCORE_GRADE_TYPE(3)
		* @param maxPoints - The maximum grade score.
		*/
		public void setMaxGradePoint(int maxPoints)
		{
			m_maxGradePoint = maxPoints;
		}

		/**
		 * Set whether this project can be a group project.
		 * @param groupProject - True if this can be a group project, false otherwise.
		 */
		public void setGroupProject(boolean groupProject)
		{
			m_groupProject = groupProject;
		}

		/**
		 * Set whether group projects should be individually graded.
		 * @param individGraded - true if projects are individually graded, false
		 * if grades are given to the group.
		 */
		public void setIndividuallyGraded(boolean individGraded)
		{
			m_individuallyGraded = individGraded;
		}
		
		/**
		 * Sets whether grades can be released once submissions are graded.
		 * @param release - true if grades can be released once submission are graded, false if
		 * they must be released manually.
		 */
		public void setReleaseGrades(boolean release)
		{
			m_releaseGrades = release;
		}
		
		/**
		* Set the Honor Pledge type;
		* values are NONE and ENGINEERING_HONOR_PLEDGE.
		* @param pledgeType - the Honor Pledge value.
		*/
		public void setHonorPledge(int pledgeType)
		{
			m_honorPledge = pledgeType;
		}

		/**
		 * Does this Assignment allow attachments?
		 * @param allow - true if the Assignment allows attachments, false otherwise?
		 */
		public void setAllowAttachments(boolean allow)
		{
			m_allowAttachments = allow;
		}
		
		/**
		 * Add an author to the author list.
		 * @param author - The User to add to the author list.
		 */
		public void addAuthor(User author)
		{
			if(author != null)
				m_authors.add(author.getId());
		}

		/**
		 * Remove an author from the author list.
		 * @param author - the User to remove from the author list.
		 */
		public void removeAuthor(User author)
		{
			if(author != null)
				m_authors.remove(author.getId());
		}
		
		/**
		 * Set the time last modified.
		 * @param lastmod - The Time at which the Content was last modified.
		 */
		public void setTimeLastModified(Time lastmod)
		{
			if(lastmod != null)
				m_timeLastModified = lastmod;
		}

		/**
		* Take all values from this object.
		* @param AssignmentContent The AssignmentContent object to take values from.
		*/
		protected void set(AssignmentContent assignmentContent)
		{
			setAll(assignmentContent);

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
		* SessionBindingListener implementation
		*******************************************************************************/
	
		public void valueBound(SessionBindingEvent event) {}
	
		public void valueUnbound(SessionBindingEvent event)
		{
			if (m_logger.isDebugEnabled())
				m_logger.debug(this + ".valueUnbound()");
	
			// catch the case where an edit was made but never resolved
			if (m_active)
			{
				cancelEdit(this);
			}
	
		}	// valueUnbound
	
	}   // BaseAssignmentContentEdit

	
	
	
	/*******************************************************************************
	* AssignmentSubmission implementation
	*******************************************************************************/

	public class BaseAssignmentSubmission implements AssignmentSubmission
	{
		protected final String STATUS_DRAFT = "Drafted";
		protected final String STATUS_SUBMITTED = "Submitted";
		protected final String STATUS_RETURNED = "Returned";
		protected final String STATUS_GRADED = "Graded";
		protected ResourcePropertiesEdit m_properties;
		protected String m_id;
		protected String m_assignment;
		protected String m_context;
		protected List m_submitters;
		protected Time m_timeSubmitted;
		protected Time m_timeReturned;
		protected Time m_timeLastModified;
		protected ReferenceVector m_submittedAttachments;
		protected ReferenceVector m_feedbackAttachments;
		protected String m_submittedText;
		protected String m_feedbackComment;
		protected String m_feedbackText;
		protected String m_grade;
		protected boolean m_submitted;
		protected boolean m_returned;
		protected boolean m_graded;
		protected boolean m_gradeReleased;
		protected boolean m_honorPledgeFlag;


		/**
		 * Copy constructor.
		 */
		public BaseAssignmentSubmission(AssignmentSubmission submission)
		{
			setAll(submission);
		}

		/**
		 * Constructor used by addSubmission.
		 */
		public BaseAssignmentSubmission(String id, String context, String assignId)
		{
			m_id = id;
			m_context = context;
			m_assignment = assignId;
			m_properties = new BaseResourcePropertiesEdit();
			addLiveProperties(m_properties);
			m_submitters = new Vector();
			m_feedbackAttachments = new ReferenceVector();
			m_submittedAttachments = new ReferenceVector();
			m_submitted = false;
			m_returned = false;
			m_graded = false;
			m_gradeReleased = false;
			m_submittedText = "";
			m_feedbackComment = "";
			m_feedbackText = "";
			m_grade = "";
			m_timeLastModified = TimeService.newTime();

			String currentUser = UsageSessionService.getSessionUserId();
			if (currentUser == null) currentUser = "";
			m_submitters.add(currentUser);
		}

		
		/**
		 * Reads the AssignmentSubmission's attribute values from xml.
		 * @param s - Data structure holding the xml info.
		 */
		public BaseAssignmentSubmission(Element el)
		{
			int numAttributes = 0;
			String bool = null;
			String intString = null;
			String attributeString = null;
			String tempString = null;
			Reference tempReference = null;

			if(m_logger.isDebugEnabled())
				m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE SUB : ENTERING STORAGE CONSTRUCTOR");

			m_id = el.getAttribute("id");
			//m_logger.info("ASSIGNMENT : BASE SERVICE : BASE SUBMISSION : CONSTRUCTOR : m_id : " + m_id);
			m_context = el.getAttribute("context");
			//m_logger.info("ASSIGNMENT : BASE SERVICE : BASE SUBMISSION : CONSTRUCTOR : m_context : " + m_context);

			// %%%zqian 
			// read the scaled grade point first; if there is none, get the old grade value
			String grade = StringUtil.trimToNull(el.getAttribute("scaled_grade"));
			if (grade == null)
			{
				grade = StringUtil.trimToNull(el.getAttribute("grade"));
				if (grade != null)
				{
					try
					{
						int test = Integer.parseInt(grade);
						// for the grades in points, multiple those by 10
						grade = grade + "0";
					}
					catch (Exception e){}					
				}
			}
			m_grade = grade;
			
			//m_logger.info("ASSIGNMENT : BASE SERVICE : BASE SUBMISSION : CONSTRUCTOR : m_grade : " + m_grade);
			m_assignment = el.getAttribute("assignment");
			//m_logger.info("ASSIGNMENT : BASE SERVICE : BASE SUBMISSION : CONSTRUCTOR : m_assignment : " + m_assignment);

			m_timeSubmitted = getTimeObject(el.getAttribute("datesubmitted"));
			m_timeReturned = getTimeObject(el.getAttribute("datereturned"));
			m_assignment = el.getAttribute("assignment");
			m_timeLastModified = getTimeObject(el.getAttribute("lastmod"));

			m_submitted = getBool(el.getAttribute("submitted"));
			m_returned = getBool(el.getAttribute("returned"));
			m_graded = getBool(el.getAttribute("graded"));
			m_gradeReleased = getBool(el.getAttribute("gradereleased"));
			m_honorPledgeFlag = getBool(el.getAttribute("pledgeflag"));

			m_submittedText = FormattedText.decodeFormattedTextAttribute(el, "submittedtext");
			m_feedbackComment = FormattedText.decodeFormattedTextAttribute(el, "feedbackcomment");
			m_feedbackText = FormattedText.decodeFormattedTextAttribute(el, "feedbacktext");

				// READ THE SUBMITTERS
			m_submitters = new Vector();
			if(m_logger.isDebugEnabled())
			m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE SUB : CONSTRUCTOR : Reading submitters : ");
			intString = el.getAttribute("numberofsubmitters");
			try
			{
				numAttributes = Integer.parseInt(intString);

				for(int x = 0; x < numAttributes; x++)
				{
					attributeString = "submitter" + x;
					tempString = el.getAttribute(attributeString);
					if(tempString != null)
						m_submitters.add(tempString);
				}
			}
			catch(Exception e)
			{
				m_logger.warn("ASSIGNMENT : BASE SERVICE : BASE SUB : CONSTRUCTOR : Exception reading submitters : " + e);
			}

				// READ THE FEEDBACK ATTACHMENTS
			m_feedbackAttachments = new ReferenceVector();
			intString = el.getAttribute("numberoffeedbackattachments");
			if(m_logger.isDebugEnabled())
			m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE SUB : CONSTRUCTOR : num feedback attachments : " + intString);
			try
			{
				numAttributes = Integer.parseInt(intString);

				for(int x = 0; x < numAttributes; x++)
				{
					attributeString = "feedbackattachment" + x;
					tempString = el.getAttribute(attributeString);
					if(tempString != null)
					{
						tempReference = new Reference(tempString);
						m_feedbackAttachments.add(tempReference);
						if(m_logger.isDebugEnabled())
						m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE SUB : CONSTRUCTOR : " + attributeString + " : " + tempString);
					}
				}
			}
			catch(Exception e)
			{
				m_logger.warn("ASSIGNMENT : BASE SERVICE : BASE SUB : CONSTRUCTOR : Exception reading feedback attachments : " + e);
			}

				// READ THE SUBMITTED ATTACHMENTS
			m_submittedAttachments = new ReferenceVector();
			intString = el.getAttribute("numberofsubmittedattachments");
			if(m_logger.isDebugEnabled())
				m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE SUB : CONSTRUCTOR : num submitted attachments : " + intString);
			try
			{
				numAttributes = Integer.parseInt(intString);

				for(int x = 0; x < numAttributes; x++)
				{
					attributeString = "submittedattachment" + x;
					tempString = el.getAttribute(attributeString);
					if(tempString != null)
					{
						tempReference = new Reference(tempString);
						m_submittedAttachments.add(tempReference);
						if(m_logger.isDebugEnabled())
						m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE SUB : CONSTRUCTOR : " + attributeString + " : " + tempString);
					}
				}
			}
			catch(Exception e)
			{
				m_logger.warn("ASSIGNMENT : BASE SERVICE : BASE SUB : CONSTRUCTOR : Exception reading submitted attachments : " + e);
			}


				// READ THE PROPERTIES, SUBMITTED TEXT, FEEDBACK COMMENT, FEEDBACK TEXT
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
				// old style encoding
				else if(element.getTagName().equals("submittedtext"))
				{
					if ((element.getChildNodes() != null) && (element.getChildNodes().item(0) != null))
					{
						m_submittedText = element.getChildNodes().item(0).getNodeValue();
						if(m_logger.isDebugEnabled())
						m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE SUB : CONSTRUCTOR : submittedtext : " + m_submittedText);
					}
					if (m_submittedText == null)
					{
						m_submittedText = "";
					}
				}
				// old style encoding
				else if(element.getTagName().equals("feedbackcomment"))
				{
					if ((element.getChildNodes() != null) && (element.getChildNodes().item(0) != null))
					{
						m_feedbackComment = element.getChildNodes().item(0).getNodeValue();
						if(m_logger.isDebugEnabled())
						m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE SUB : CONSTRUCTOR : feedbackcomment : " + m_feedbackComment);
					}
					if (m_feedbackComment == null)
					{
						m_feedbackComment = "";
					}
				}
				// old style encoding
				else if(element.getTagName().equals("feedbacktext"))
				{
					if ((element.getChildNodes() != null) && (element.getChildNodes().item(0) != null))
					{
						m_feedbackText = element.getChildNodes().item(0).getNodeValue();
						if(m_logger.isDebugEnabled())
						m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE SUB : CONSTRUCTOR : FEEDBACK TEXT : " + m_feedbackText);
					}
					if (m_feedbackText == null)
					{
						m_feedbackText = "";
					}
				}				
			}

			if(m_logger.isDebugEnabled())
				m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE SUB : LEAVING STORAGE CONSTRUCTOR");

		}// storage constructor
		
		
		/**
		 * Takes the AssignmentContent's attribute values and puts them into the xml document.
		 * @param s - Data structure holding the object to be stored.
		 * @param doc - The xml document.
		 */
		public Element toXml(Document doc, Stack stack)
		{
			if(m_logger.isDebugEnabled())
				m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE ASSIGNMENT : ENTERING TOXML");
			
			Element submission = doc.createElement("submission");
			if (stack.isEmpty())
			{
				doc.appendChild(submission);
			}
			else
			{
				((Element)stack.peek()).appendChild(submission);
			}

			stack.push(submission);

			String numItemsString = null;
			String attributeString = null;
			String itemString = null;
			Reference tempReference = null;

			submission.setAttribute("id", m_id);
			submission.setAttribute("context", m_context);
			submission.setAttribute("scaled_grade", m_grade);
			submission.setAttribute("assignment", m_assignment);
			submission.setAttribute("datesubmitted", getTimeString(m_timeSubmitted));
			submission.setAttribute("datereturned", getTimeString(m_timeReturned));
			submission.setAttribute("lastmod", getTimeString(m_timeLastModified));
			submission.setAttribute("submitted", getBoolString(m_submitted));
			submission.setAttribute("returned", getBoolString(m_returned));
			submission.setAttribute("graded", getBoolString(m_graded));
			submission.setAttribute("gradereleased", getBoolString(m_gradeReleased));
			submission.setAttribute("pledgeflag", getBoolString(m_honorPledgeFlag));


			if(m_logger.isDebugEnabled())
			m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE ASSIGNMENT : SAVED REGULAR PROPERTIES");

				// SAVE THE SUBMITTERS
			numItemsString = "" + m_submitters.size();
			submission.setAttribute("numberofsubmitters", numItemsString);
			for(int x = 0; x < m_submitters.size(); x++)
			{
				attributeString = "submitter" + x;
				itemString = (String) m_submitters.get(x);
				if(itemString != null)
					submission.setAttribute(attributeString, itemString);
			}

			if(m_logger.isDebugEnabled())
			m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE ASSIGNMENT : SAVED SUBMITTERS");

				// SAVE THE FEEDBACK ATTACHMENTS
			numItemsString = "" + m_feedbackAttachments.size();
			submission.setAttribute("numberoffeedbackattachments", numItemsString);
			if(m_logger.isDebugEnabled())
			m_logger.debug("DB : DbCachedStorage : DbCachedAssignmentSubmission : entering fb attach loop : size : " + numItemsString);
			for(int x = 0; x < m_feedbackAttachments.size(); x++)
			{
				attributeString = "feedbackattachment" + x;
				tempReference = (Reference)m_feedbackAttachments.get(x);
				itemString = tempReference.getReference();
				if(itemString != null)
					submission.setAttribute(attributeString, itemString);
			}

			if(m_logger.isDebugEnabled())
				m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE ASSIGNMENT : SAVED FEEDBACK ATTACHMENTS");

				// SAVE THE SUBMITTED ATTACHMENTS
			numItemsString = "" + m_submittedAttachments.size();
			submission.setAttribute("numberofsubmittedattachments", numItemsString);
			for(int x = 0; x < m_submittedAttachments.size(); x++)
			{
				attributeString = "submittedattachment" + x;
				tempReference = (Reference)m_submittedAttachments.get(x);
				itemString = tempReference.getReference();
				if(itemString != null)
					submission.setAttribute(attributeString, itemString);
			}

			if(m_logger.isDebugEnabled())
				m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE ASSIGNMENT : SAVED SUBMITTED ATTACHMENTS");


				// SAVE THE PROPERTIES
			m_properties.toXml(doc, stack);
			stack.pop();

			FormattedText.encodeFormattedTextAttribute(submission, "submittedtext", m_submittedText);
			FormattedText.encodeFormattedTextAttribute(submission, "feedbackcomment", m_feedbackComment);
			FormattedText.encodeFormattedTextAttribute(submission, "feedbacktext", m_feedbackText);

			if(m_logger.isDebugEnabled())
				m_logger.debug("ASSIGNMENT : BASE SERVICE : BASE ASSIGNMENT : LEAVING TOXML");
			
			return submission;

		}//toXml

		
		protected void setAll(AssignmentSubmission submission)
		{
			m_id = submission.getId();
			m_context = submission.getContext();
			m_assignment = submission.getAssignmentId();
			m_grade = submission.getGrade();
			m_submitters = submission.getSubmitterIds();
			m_submitted = submission.getSubmitted();
			m_timeSubmitted = submission.getTimeSubmitted();
			m_timeReturned = submission.getTimeReturned();
			m_timeLastModified = submission.getTimeLastModified();
			m_submittedAttachments = submission.getSubmittedAttachments();
			m_feedbackAttachments = submission.getFeedbackAttachments();
			m_submittedText = submission.getSubmittedText();
			m_feedbackComment = submission.getFeedbackComment();
			m_feedbackText = submission.getFeedbackText();
			m_returned = submission.getReturned();
			m_graded = submission.getGraded();
			m_gradeReleased = submission.getGradeReleased();
			m_honorPledgeFlag = submission.getHonorPledgeFlag();
			m_properties = new BaseResourcePropertiesEdit();
			m_properties.addAll(submission.getProperties());
		}
		
		
		
		/**
		* Access the URL which can be used to access the resource.
		* @return The URL which can be used to access the resource.
		*/
		public String getUrl()
		{
			return getAccessPoint(false) + Resource.SEPARATOR + "s" + Resource.SEPARATOR + m_context + Resource.SEPARATOR + m_id;

		}   // getUrl

		/**
		* Access the internal reference which can be used to access the resource from within the system.
		* @return The the internal reference which can be used to access the resource from within the system.
		*/
		public String getReference()
		{
			return submissionReference(m_context, m_id, m_assignment);

		}   // getReference

		/**
	 	* Access the id of the resource.
	 	* @return The id.
	 	*/
	 	public String getId()
		{
			return m_id;
		}

		/**
		* Access the resource's properties.
		* @return The resource's properties.
		*/
		public ResourceProperties getProperties()
		{
			return m_properties;
		}



		/*******************************************************************************
		* AssignmentSubmission implementation
		*******************************************************************************/

		/**
		 * Access the AssignmentSubmission's context at the time of creation.
		 * @return String - the context string.
		 */
		public String getContext()
		{
			return m_context;
		}

		/**
		 * Access the Assignment for this Submission
		 * @return the Assignment
		 */
		public Assignment getAssignment()
		{
			Assignment retVal = null;
			if(m_assignment != null)
			{
				retVal = m_assignmentStorage.get(m_assignment);
			}

			return retVal;
		}

		/**
		 * Access the Id for the Assignment for this Submission
		 * @return String - the Assignment Id
		 */
		public String getAssignmentId()
		{
			return m_assignment;
		}

		/**
		 * Get whether this is a final submission.
		 * @return True if a final submission, false if still a draft.
		 */
		public boolean getSubmitted()
		{
			return m_submitted;
		}

		/**
		 * Access the list of Users who submitted this response to the Assignment.
		 * @return Array of User objects.
		 */
		public User[] getSubmitters()
		{
			User[] retVal = new User[m_submitters.size()];
			for(int x = 0; x < m_submitters.size(); x++)
			{
				try
				{
					retVal[x] = UserDirectoryService.getUser((String) m_submitters.get(x));
				}
				catch(Exception e){}
			}
			return retVal;
		}

		/**
		 * Access the list of Users who submitted this response to the Assignment.
		 * @return FlexStringArray of user ids.
		 */
		public List getSubmitterIds()
		{
			return m_submitters;
		}

		/**
		 * Set the time at which this response was submitted;
		 * null signifies the response is unsubmitted.
		 * @return Time of submission.
		 */
		public Time getTimeSubmitted()
		{
			return m_timeSubmitted;
		}

		/**
		 * Get whether the grade has been released.
		 * @return True if the Submissions's grade has been released, false otherwise.
		 */
		public boolean getGradeReleased()
		{
			return m_gradeReleased;
		}

		/**
		 * Access the grade recieved.
		 * @return The Submission's grade..
		 */
		public String getGrade()
		{
			return m_grade;
		}
		
		/**
		 * Access the grade recieved.
		 * @return The Submission's grade..
		 */
		public String getGradeDisplay()
		{
			Assignment m = getAssignment();
			if (m.getContent().getTypeOfGrade() == Assignment.SCORE_GRADE_TYPE)
			{
				if (m_grade!=null && m_grade.length() > 0)
				{
					try
					{
						Integer.parseInt(m_grade);
						// if point grade, display the grade with one decimal place
						return m_grade.substring(0, m_grade.length()-1) + "." + m_grade.substring(m_grade.length()-1);
					}
					catch (Exception e)
					{
						return m_grade;
					}
				}
				else
				{
					return StringUtil.trimToZero(m_grade);
				}
			}
			else
			{
				return StringUtil.trimToZero(m_grade);
			}
		}

		/**
		 * Get the time of last modification;
		 * @return The time of last modification.
		 */
		public Time getTimeLastModified()
		{
			return m_timeLastModified;
		}
	
		/**
		 * Text submitted in response to the Assignment.
		 * @return The text of the submission.
		 */
		public String getSubmittedText()
		{
			return m_submittedText;
		}

		/**
		 * Access the list of attachments to this response to the Assignment.
		 * @return ReferenceVector of the list of attachments as Reference objects;
		 */
		public ReferenceVector getSubmittedAttachments()
		{
			return m_submittedAttachments;
		}

		/**
		 * Get the general comments by the grader
		 * @return The text of the grader's comments; may be null.
		 */
		public String getFeedbackComment()
		{
			return m_feedbackComment;
		}


		/**
		 * Access the text part of the instructors feedback; usually an
		 * annotated copy of the submittedText
		 * @return The text of the grader's feedback.
		 */
		public String getFeedbackText()
		{
			return m_feedbackText;
		}

		/**
		 * Access the list of attachments returned to the students
		 * in the process of grading this assignment; usually a modified
		 * or annotated version of the attachment submitted.
		 * @return ReferenceVector of the Resource objects pointing to the attachments.
		 */
		public ReferenceVector getFeedbackAttachments()
		{
			return m_feedbackAttachments;
		}

		/**
		 * Get whether this Submission was rejected by the grader.
		 * @return True if this response was rejected by the grader, false otherwise.
		 */
		public boolean getReturned()
		{
			return m_returned;
		}

		/**
		 * Get whether this Submission has been graded.
		 * @return True if the submission has been graded, false otherwise.
		 */
		public boolean getGraded()
		{
			return m_graded;
		}

		/**
		 * Get the time on which the graded submission was returned;
		 * null means the response is not yet graded.
		 * @return the time (may be null)
		 */
		public Time getTimeReturned()
		{
			return m_timeReturned;
		}

		/**
		 * Access the checked status of the honor pledge flag.
		 * @return True if the honor pledge is checked, false otherwise.
		 */
		public boolean getHonorPledgeFlag()
		{
			return m_honorPledgeFlag;
		}

		/**
		 * Returns the status of the submission : 
		 * Not Started, submitted, returned or graded.
		 * @return The Submission's status.
		 */
		public String getStatus()
		{
			String retVal = null;

			if(m_submitted)
			{
				if(m_graded && m_gradeReleased)
					retVal = STATUS_GRADED;
				else if(m_returned)
					retVal = STATUS_RETURNED;
				else
					retVal = STATUS_SUBMITTED;
			}
			else
				retVal = STATUS_DRAFT;

			return retVal;
		}

		/**
		* Are these objects equal?  If they are both AssignmentSubmission objects,
		* and they have matching id's, they are.
		* @return true if they are equal, false if not.
		*/
		public boolean equals(Object obj)
		{
			if (!(obj instanceof AssignmentSubmission)) return false;
			return ((AssignmentSubmission)obj).getId().equals(getId());

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
			if (!(obj instanceof AssignmentSubmission)) throw new ClassCastException();

			// if the object are the same, say so
			if (obj == this) return 0;

			// start the compare by comparing their sort names
			int compare = getTimeSubmitted().toString().compareTo(((AssignmentSubmission)obj).getTimeSubmitted().toString());

			// if these are the same
			if (compare == 0)
			{
				// sort based on (unique) id
				compare = getId().compareTo(((AssignmentSubmission)obj).getId());
			}

			return compare;

		}	// compareTo
		
	}	// AssignmentSubmission
	
	
	
	
	/*******************************************************************************
	* AssignmentSubmissionEdit implementation
	*******************************************************************************/

	/**
	* <p>BaseAssignmentSubmissionEdit is an implementation of the CHEF AssignmentSubmissionEdit object.</p>
	* 
	* @author University of Michigan, CHEF Software Development Team
	*/
	public class BaseAssignmentSubmissionEdit
		extends BaseAssignmentSubmission
		implements AssignmentSubmissionEdit, SessionBindingListener
	{
		/** The event code for this edit. */
		protected String m_event = null;

		/** Active flag. */
		protected boolean m_active = false;


		/**
		* Construct from another AssignmentSubmission object.
		* @param AssignmentSubmission The AssignmentSubmission object to use for values.
		*/
		public BaseAssignmentSubmissionEdit(AssignmentSubmission assignmentSubmission)
		{
			super(assignmentSubmission);

		}	// BaseAssignmentSubmissionEdit

		/**
		* Construct.
		* @param id The AssignmentSubmission id.
		*/
		public BaseAssignmentSubmissionEdit(String id, String context, String assignmentId)
		{
			super(id, context, assignmentId);

		}   // BaseAssignmentSubmissionEdit

		/**
		* Construct from information in XML.
		* @param el The XML DOM Element definining the AssignmentSubmission.
		*/
		public BaseAssignmentSubmissionEdit(Element el)
		{
			super(el);

		}	// BaseAssignmentSubmissionEdit


		/**
		* Clean up.
		*/
		protected void finalize()
		{
			// catch the case where an edit was made but never resolved
			if (m_active)
			{
				cancelEdit(this);
			}

		}	// finalize


		/**
		 * Set the context at the time of creation.
		 * @param context -   the context string.
		 */
	 	public void setContext(String context)
		{
			m_context = context;
		}
		
		/**
		 * Set the Assignment for this Submission
		 * @param assignment - the Assignment
		 */
		public void setAssignment(Assignment assignment)
		{
			if(assignment != null)
			{
				m_assignment = assignment.getId();
			}
			else
				m_assignment = "";
		}

		/**
		 * Set whether this is a final submission.
		 * @param submitted - True if a final submission, false if still a draft.
		 */
		public void setSubmitted(boolean submitted)
		{
			m_submitted = submitted;
		}

		/**
		 * Add a User to the submitters list.
		 * @param submitter - the User to add.
		 */
		public void addSubmitter(User submitter)
		{
			if(submitter != null)
				m_submitters.add(submitter.getId());
		}
	
		/**
		 * Remove an User from the submitter list
		 * @param submitter - the User to remove.
		 */
		public void removeSubmitter(User submitter)
		{
			if(submitter != null)
				m_submitters.remove(submitter.getId());
		}
		
		/**
		 * Remove all user from the submitter list
		 */
		public void clearSubmitters()
		{
			m_submitters.clear();
		}
		
		/**
		 * Set the time at which this response was submitted;
		 * setting it to null signifies the response is unsubmitted.
		 * @param timeSubmitted - Time of submission.
		 */
		public void setTimeSubmitted(Time value)
		{
			m_timeSubmitted = value;
		}
		
		/**
		 * Set whether the grade has been released.
		 * @param released - True if the Submissions's grade has been released, false otherwise.
		 */
		public void setGradeReleased(boolean released)
		{
			m_gradeReleased = released;
		}
		
		/**
		 * Sets the grade for the Submisssion.
		 * @param grade - The Submission's grade.
		 */
		public void setGrade(String grade)
		{
			m_grade = grade;
		}
		
		/**
		 * Text submitted in response to the Assignment.
		 * @param submissionText - The text of the submission.
		 */
		public void setSubmittedText(String value)
		{
			m_submittedText = value;
		}
		
		/**
		 * Add an attachment to the list of submitted attachments.
		 * @param attachment - The Reference object pointing to the attachment.
		 */
		public void addSubmittedAttachment(Reference attachment)
		{
			if(attachment != null)
				m_submittedAttachments.add(attachment);
		}
	
		/**
		 * Remove an attachment from the list of submitted attachments
		 * @param attachment - The Reference object pointing to the attachment.
		 */
		public void removeSubmittedAttachment(Reference attachment)
		{
			if(attachment != null)
				m_submittedAttachments.remove(attachment);
		}
		

		/**
		 * Remove all submitted attachments.
		 */
		public void clearSubmittedAttachments()
		{
			m_submittedAttachments.clear();
		}
		


		/**
		 * Set the general comments by the grader.
		 * @param comment - the text of the grader's comments; may be null.
		 */
		public void setFeedbackComment(String value)
		{
			m_feedbackComment = value;
		}
		

		/**
		 * Set the text part of the instructors feedback; usually an
		 * annotated copy of the submittedText
		 * @param feedback - The text of the grader's feedback.
		 */
		public void setFeedbackText(String value)
		{
			m_feedbackText = value;
		}
		

		/**
		 * Add an attachment to the list of feedback attachments.
		 * @param attachment - The Resource object pointing to the attachment.
		 */
		public void addFeedbackAttachment(Reference attachment)
		{
			if(attachment != null)
				m_feedbackAttachments.add(attachment);
		}

		/**
		 * Remove an attachment from the list of feedback attachments.
		 * @param attachment - The Resource pointing to the attachment to remove.
		 */
		public void removeFeedbackAttachment(Reference attachment)
		{
			if(attachment != null)
				m_feedbackAttachments.remove(attachment);
		}

		/**
		 * Remove all feedback attachments.
		 */
		public void clearFeedbackAttachments()
		{
			m_feedbackAttachments.clear();
		}
		

		/**
		 * Set whether this Submission was rejected by the grader.
		 * @param returned - true if this response was rejected by the grader, false otherwise.
		 */
		public void setReturned(boolean value)
		{
			m_returned = value;
		}
		

		/**
		 * Set whether this Submission has been graded.
		 * @param graded - true if the submission has been graded, false otherwise.
		 */
		public void setGraded(boolean value)
		{
			m_graded = value;
		}
		

		/**
		* Set the time at which the graded Submission was returned;
		* setting it to null means it is not yet graded.
		* @param timeReturned - The time at which the graded Submission was returned.
		*/
		 public void setTimeReturned(Time timeReturned)
		 {
			 m_timeReturned = timeReturned;
		 }
		 
		/**
		 * Set the checked status of the honor pledge flag.
		 * @param honorPledgeFlag - True if the honor pledge is checked, false otherwise.
		 */
		public void setHonorPledgeFlag(boolean honorPledgeFlag)
		{
			m_honorPledgeFlag = honorPledgeFlag;
		}
		
		/**
		 * Set the time last modified.
		 * @param lastmod - The Time at which the Assignment was last modified.
		 */
		public void setTimeLastModified(Time lastmod)
		{
			if(lastmod != null)
				m_timeLastModified = lastmod;
		}
		
		
		/**
		* Take all values from this object.
		* @param AssignmentSubmission The AssignmentSubmission object to take values from.
		*/
		protected void set(AssignmentSubmission assignmentSubmission)
		{
			setAll(assignmentSubmission);

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
		* SessionBindingListener implementation
		*******************************************************************************/
	
		public void valueBound(SessionBindingEvent event) {}
	
		public void valueUnbound(SessionBindingEvent event)
		{
			if (m_logger.isDebugEnabled())
				m_logger.debug(this + ".valueUnbound()");
	
			// catch the case where an edit was made but never resolved
			if (m_active)
			{
				cancelEdit(this);
			}
	
		}	// valueUnbound
	
	}   // BaseAssignmentSubmissionEdit
	
	
	
	
	
	
	/*******************************************************************************
	* Assignment Storage
	*******************************************************************************/

	protected interface AssignmentStorage
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
		* Check if an Assignment by this id exists.
		* @param id The assignment id.
		* @return true if an Assignment by this id exists, false if not.
		*/
		public boolean check(String id);

		/**
		* Get the Assignment with this id, or null if not found.
		* @param id The Assignment id.
		* @return The Assignment with this id, or null if not found.
		*/
		public Assignment get(String id);

		/**
		* Get all Assignments.
		* @return The list of all Assignments.
		*/
		public List getAll(String context);

		/**
		* Add a new Assignment with this id.
		* @param id The Assignment id.
		* @param context The context.
		* @return The locked Assignment object with this id, or null if the id is in use.
		*/
		public AssignmentEdit put(String id, String context);

		/**
		* Get a lock on the Assignment with this id, or null if a lock cannot be gotten.
		* @param id The Assignment id.
		* @return The locked Assignment with this id, or null if this records cannot be locked.
		*/
		public AssignmentEdit edit(String id);

		/**
		* Commit the changes and release the lock.
		* @param Assignment The Assignment to commit.
		*/
		public void commit(AssignmentEdit assignment);

		/**
		* Cancel the changes and release the lock.
		* @param Assignment The Assignment to commit.
		*/
		public void cancel(AssignmentEdit assignment);

		/**
		* Remove this Assignment.
		* @param Assignment The Assignment to remove.
		*/
		public void remove(AssignmentEdit assignment);

	}   // AssignmentStorage
	
	
	
	
	/*******************************************************************************
	* AssignmentContent Storage
	*******************************************************************************/

	protected interface AssignmentContentStorage
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
		* Check if a AssignmentContent by this id exists.
		* @param id The AssignmentContent id.
		* @return true if a AssignmentContent by this id exists, false if not.
		*/
		public boolean check(String id);

		/**
		* Get the AssignmentContent with this id, or null if not found.
		* @param id The AssignmentContent id.
		* @return The AssignmentContent with this id, or null if not found.
		*/
		public AssignmentContent get(String id);

		/**
		* Get all AssignmentContents.
		* @return The list of all AssignmentContents.
		*/
		public List getAll(String context);

		/**
		* Add a new AssignmentContent with this id.
		* @param id The AssignmentContent id.
		* @param context The context.
		* @return The locked AssignmentContent object with this id, or null if the id is in use.
		*/
		public AssignmentContentEdit put(String id, String context);

		/**
		* Get a lock on the AssignmentContent with this id, or null if a lock cannot be gotten.
		* @param id The AssignmentContent id.
		* @return The locked AssignmentContent with this id, or null if this records cannot be locked.
		*/
		public AssignmentContentEdit edit(String id);

		/**
		* Commit the changes and release the lock.
		* @param AssignmentContent The AssignmentContent to commit.
		*/
		public void commit(AssignmentContentEdit content);

		/**
		* Cancel the changes and release the lock.
		* @param AssignmentContent The AssignmentContent to commit.
		*/
		public void cancel(AssignmentContentEdit content);

		/**
		* Remove this AssignmentContent.
		* @param AssignmentContent The AssignmentContent to remove.
		*/
		public void remove(AssignmentContentEdit content);

	}   // AssignmentContentStorage
	
	
	
	/*******************************************************************************
	* AssignmentSubmission Storage
	*******************************************************************************/

	protected interface AssignmentSubmissionStorage
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
		* Check if a AssignmentSubmission by this id exists.
		* @param id The AssignmentSubmission id.
		* @return true if a AssignmentSubmission by this id exists, false if not.
		*/
		public boolean check(String id);

		/**
		* Get the AssignmentSubmission with this id, or null if not found.
		* @param id The AssignmentSubmission id.
		* @return The AssignmentSubmission with this id, or null if not found.
		*/
		public AssignmentSubmission get(String id);

		/**
		* Get all AssignmentSubmissions.
		* @return The list of all AssignmentSubmissions.
		*/
		public List getAll(String context);

		/**
		* Add a new AssignmentSubmission with this id.
		* @param id The AssignmentSubmission id.
		* @param context The context.
		* @return The locked AssignmentSubmission object with this id, or null if the id is in use.
		*/
		public AssignmentSubmissionEdit put(String id, String context, String assignmentId);

		/**
		* Get a lock on the AssignmentSubmission with this id, or null if a lock cannot be gotten.
		* @param id The AssignmentSubmission id.
		* @return The locked AssignmentSubmission with this id, or null if this records cannot be locked.
		*/
		public AssignmentSubmissionEdit edit(String id);

		/**
		* Commit the changes and release the lock.
		* @param AssignmentSubmission The AssignmentSubmission to commit.
		*/
		public void commit(AssignmentSubmissionEdit submission);

		/**
		* Cancel the changes and release the lock.
		* @param AssignmentSubmission The AssignmentSubmission to commit.
		*/
		public void cancel(AssignmentSubmissionEdit submission);

		/**
		* Remove this AssignmentSubmission.
		* @param AssignmentSubmission The AssignmentSubmission to remove.
		*/
		public void remove(AssignmentSubmissionEdit submission);

	}   // AssignmentSubmissionStorage




	/**
	 * Utility function which returns the string representation of the
	 * long value of the time object.
	 * @param t - the Time object.
	 * @return A String representation of the long value of the time object.
	 */
	protected String getTimeString(Time t)
	{
		String retVal = "";
		if(t != null)
			retVal = t.toString();
		return retVal;
	}

	/**
	 * Utility function which returns a string from a boolean value.
	 * @param b - the boolean value.
	 * @return - "True" if the input value is true, "false" otherwise.
	 */
	protected String getBoolString(boolean b)
	{
		if(b)
			return "true";
		else
			return "false";
	}

	/**
	 * Utility function which returns a boolean value from a string.
	 * @param s - The input string.
	 * @return the boolean true if the input string is "true", false otherwise.
	 */
	protected boolean getBool(String s)
	{
		boolean retVal = false;
		if(s != null)
		{
			if(s.equalsIgnoreCase("true"))
				retVal = true;
		}
		return retVal;
	}

	/**
	 * Utility function which converts a string into a chef time object.
	 * @param timeString - String version of a time in long format, representing the standard ms since the epoch, Jan 1, 1970 00:00:00.
	 * @return A chef Time object.
	 */
	protected Time getTimeObject(String timeString)
	{
		Time aTime = null;
		timeString = StringUtil.trimToNull(timeString);
		if(timeString != null)
		{
			try
			{
				aTime = TimeService.newTimeGmt(timeString);
			}
			catch(Exception e)
			{
				try
				{
					long longTime = Long.parseLong(timeString);
					aTime = TimeService.newTime(longTime);
				}
				catch(Exception ee)
				{
					m_logger.warn(this + " Exception creating time object from xml file : " + ee);
				}
			}
		}
		return aTime;
	}

	protected String getGroupNameFromContext(String context)
	{
		String retVal = "";

		if(context != null)
		{
			int index = context.indexOf("group-");
			if(index != -1)
			{
				String[] parts = StringUtil.splitFirst(context, "-");
				if(parts.length > 1)
				{
					retVal = parts[1];
				}
			}
			else
			{
				retVal = context;
			}
		}

		return retVal;
	}



	/*******************************************************************************
	* StorageUser implementations (no container)
	*******************************************************************************/

	/*******************************************************************************
	* AssignmentStorageUser implementation
	*******************************************************************************/

	protected class AssignmentStorageUser 
		implements StorageUser
	{
		/**
		* Construct a new continer given just an id.
		* @param id The id for the new object.
		* @return The new container Resource.
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
		{ return new BaseAssignment(id, (String) others[0]); }

		/**
		* Construct a new resource, from an XML element.
		* @param container The Resource that is the container for the new resource (may be null).
		* @param element The XML.
		* @return The new resource from the XML.
		*/
		public Resource newResource(Resource container, Element element)
		{ return new BaseAssignment(element); }

		/**
		* Construct a new resource from another resource of the same type.
		* @param container The Resource that is the container for the new resource (may be null).
		* @param other The other resource.
		* @return The new resource as a copy of the other.
		*/
		public Resource newResource(Resource container, Resource other)
		{ return new BaseAssignment((Assignment) other); }

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
			BaseAssignmentEdit e = new BaseAssignmentEdit(id, (String) others[0]);
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
			BaseAssignmentEdit e =  new BaseAssignmentEdit(element);
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
			BaseAssignmentEdit e = new BaseAssignmentEdit((Assignment) other);
			e.activate();
			return e;
		}

		/**
		* Collect the fields that need to be stored outside the XML (for the resource).
		* @return An array of field values to store in the record outside the XML (for the resource).
		*/
		public Object[] storageFields(Resource r)
		{
			Object rv[] = new Object[1];
			rv[0] = ((Assignment) r).getContext();
			return rv;
		}

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

	}//AssignmentStorageUser


	
	/*******************************************************************************
	* AssignmentContentStorageUser implementation
	*******************************************************************************/

	protected class AssignmentContentStorageUser 
		implements StorageUser
	{
		/**
		* Construct a new continer given just an id.
		* @param id The id for the new object.
		* @return The new container Resource.
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
		{ return new BaseAssignmentContent(id, (String) others[0]); }

		/**
		* Construct a new resource, from an XML element.
		* @param container The Resource that is the container for the new resource (may be null).
		* @param element The XML.
		* @return The new resource from the XML.
		*/
		public Resource newResource(Resource container, Element element)
		{ return new BaseAssignmentContent(element); }

		/**
		* Construct a new resource from another resource of the same type.
		* @param container The Resource that is the container for the new resource (may be null).
		* @param other The other resource.
		* @return The new resource as a copy of the other.
		*/
		public Resource newResource(Resource container, Resource other)
		{ return new BaseAssignmentContent((AssignmentContent) other); }

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
		* Construct a new rsource given just an id.
		* @param container The Resource that is the container for the new resource (may be null).
		* @param id The id for the new object.
		* @param others (options) array of objects to load into the Resource's fields.
		* @return The new resource.
		*/
		public Edit newResourceEdit(Resource container, String id, Object[] others)
		{
			BaseAssignmentContentEdit e = new BaseAssignmentContentEdit(id, (String) others[0]);
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
			BaseAssignmentContentEdit e =  new BaseAssignmentContentEdit(element);
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
			BaseAssignmentContentEdit e = new BaseAssignmentContentEdit((AssignmentContent) other);
			e.activate();
			return e;
		}

		/**
		* Collect the fields that need to be stored outside the XML (for the resource).
		* @return An array of field values to store in the record outside the XML (for the resource).
		*/
		public Object[] storageFields(Resource r)
		{
			Object rv[] = new Object[1];
			rv[0] = ((AssignmentContent) r).getCreator();
			return rv;
		}

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

	}//ContentStorageUser
	



	/*******************************************************************************
	* SubmissionStorageUser implementation
	*******************************************************************************/
	

	protected class AssignmentSubmissionStorageUser 
		implements StorageUser
	{
		/**
		* Construct a new continer given just an id.
		* @param id The id for the new object.
		* @return The new container Resource.
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
		{ return new BaseAssignmentSubmission(id, (String)others[0], (String)others[1]); }

		/**
		* Construct a new resource, from an XML element.
		* @param container The Resource that is the container for the new resource (may be null).
		* @param element The XML.
		* @return The new resource from the XML.
		*/
		public Resource newResource(Resource container, Element element)
		{ return new BaseAssignmentSubmission(element); }

		/**
		* Construct a new resource from another resource of the same type.
		* @param container The Resource that is the container for the new resource (may be null).
		* @param other The other resource.
		* @return The new resource as a copy of the other.
		*/
		public Resource newResource(Resource container, Resource other)
		{ return new BaseAssignmentSubmission((AssignmentSubmission) other); }

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
		* Construct a new rsource given just an id.
		* @param container The Resource that is the container for the new resource (may be null).
		* @param id The id for the new object.
		* @param others (options) array of objects to load into the Resource's fields.
		* @return The new resource.
		*/
		public Edit newResourceEdit(Resource container, String id, Object[] others)
		{
			BaseAssignmentSubmissionEdit e = new BaseAssignmentSubmissionEdit(id, (String)others[0], (String)others[1]);
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
			BaseAssignmentSubmissionEdit e =  new BaseAssignmentSubmissionEdit(element);
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
			BaseAssignmentSubmissionEdit e = new BaseAssignmentSubmissionEdit((AssignmentSubmission) other);
			e.activate();
			return e;
		}

		/**
		* Collect the fields that need to be stored outside the XML (for the resource).
		* @return An array of field values to store in the record outside the XML (for the resource).
		*/
		public Object[] storageFields(Resource r)
		{
			Object rv[] = new Object[1];
			rv[0] = ((AssignmentSubmission) r).getAssignmentId();
			return rv;
		}

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

	}//SubmissionStorageUser
	
	
	


	/*******************************************************************************
	* CacheRefresher implementations (no container)
	*******************************************************************************/

	/*******************************************************************************
	* AssignmentCacheRefresher implementation
	*******************************************************************************/

	protected class AssignmentCacheRefresher
		implements CacheRefresher
	{
		/**
		* Get a new value for this key whose value has already expired in the cache.
		* @param key The key whose value has expired and needs to be refreshed.
		* @param oldValue The old expired value of the key.
		* @return a new value for use in the cache for this key; if null, the entry will be removed.
		*/	
		public Object refresh(Object key, Object oldValue, Event event)
		{
		
			// key is a reference, but our storage wants an id
			String id = assignmentId((String) key);

			// get whatever we have from storage for the cache for this vale
			Assignment assignment = m_assignmentStorage.get(id);

			if (m_logger.isDebugEnabled())
				m_logger.debug(this + ".refresh(): " + key + " : " + id);

			return assignment;

		}	// refresh

	}//AssignmentCacheRefresher

	
	/*******************************************************************************
	* AssignmentContentCacheRefresher implementation
	*******************************************************************************/

	protected class AssignmentContentCacheRefresher
		implements CacheRefresher
	{
		/**
		* Get a new value for this key whose value has already expired in the cache.
		* @param key The key whose value has expired and needs to be refreshed.
		* @param oldValue The old expired value of the key.
		* @return a new value for use in the cache for this key; if null, the entry will be removed.
		*/	
		public Object refresh(Object key, Object oldValue, Event event)
		{
		
			// key is a reference, but our storage wants an id
			String id = contentId((String) key);

			// get whatever we have from storage for the cache for this vale
			AssignmentContent content = m_contentStorage.get(id);

			if (m_logger.isDebugEnabled())
				m_logger.debug(this + ".refresh(): " + key + " : " + id);

			return content;

		}	// refresh

	}//AssignmentContentCacheRefresher
	
	
	/*******************************************************************************
	* AssignmentSubmissionCacheRefresher implementation
	*******************************************************************************/

	protected class AssignmentSubmissionCacheRefresher
		implements CacheRefresher
	{
		/**
		* Get a new value for this key whose value has already expired in the cache.
		* @param key The key whose value has expired and needs to be refreshed.
		* @param oldValue The old expired value of the key.
		* @return a new value for use in the cache for this key; if null, the entry will be removed.
		*/	
		public Object refresh(Object key, Object oldValue, Event event)
		{
		
			// key is a reference, but our storage wants an id
			String id = submissionId((String) key);

			// get whatever we have from storage for the cache for this vale
			AssignmentSubmission submission = m_submissionStorage.get(id);

			if (m_logger.isDebugEnabled())
				m_logger.debug(this + ".refresh(): " + key + " : " + id);

			return submission;

		}	// refresh

	}//AssignmentSubmissionCacheRefresher

} // BaseAssignmentService

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/assignment/BaseAssignmentService.java,v 1.6 2005/06/03 20:18:09 zqian.umich.edu Exp $
*
**********************************************************************************/
