/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/assignment/XmlFileAssignmentService.java,v 1.9 2004/09/03 01:34:56 ggolden.umich.edu Exp $
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
package org.sakaiproject.component.legacy.assignment;

// import
import java.util.List;

import org.sakaiproject.service.legacy.assignment.Assignment;
import org.sakaiproject.service.legacy.assignment.AssignmentContent;
import org.sakaiproject.service.legacy.assignment.AssignmentContentEdit;
import org.sakaiproject.service.legacy.assignment.AssignmentEdit;
import org.sakaiproject.service.legacy.assignment.AssignmentSubmission;
import org.sakaiproject.service.legacy.assignment.AssignmentSubmissionEdit;
import org.sakaiproject.util.storage.BaseXmlFileStorage;

/**
* <p>XmlFileAssignmentService is the xml-file-storing service class for Assignments.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.9 $
* @see org.chefproject.core.Assignment
*/
public class XmlFileAssignmentService extends BaseAssignmentService
{

	/** A full path and file name to the storage file. */
	protected String m_assignmentStoragePath = null;

	/** A full path and file name to the storage file. */
	protected String m_contentStoragePath = null;

	/** A full path and file name to the storage file. */
	protected String m_submissionStoragePath = null;

	/*******************************************************************************
	* Constructors, Dependencies and their setter methods
	*******************************************************************************/

	/**
	 * Configuration: set the assignment storage path
	 * @param path The storage path.
	 */
	public void setAssignmentStoragePath(String path)
	{
		m_assignmentStoragePath = path;
	}

	/**
	 * Configuration: set the content storage path
	 * @param path The storage path.
	 */
	public void setContentStoragePath(String path)
	{
		m_contentStoragePath = path;
	}

	/**
	 * Configuration: set the submission storage path
	 * @param path The storage path.
	 */
	public void setSubmissionStoragePath(String path)
	{
		m_submissionStoragePath = path;
	}

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		try
		{
			super.init();

			m_logger.info(this +".init(): assignment storage path: " + m_assignmentStoragePath);
			m_logger.info(this +".init(): assignment content storage path: " + m_contentStoragePath);
			m_logger.info(this +".init(): assignment submission storage path: " + m_submissionStoragePath);

			// we don't need the cache, since we hold everything in memory already
			if (m_caching)
			{
				if (m_assignmentCache != null) m_assignmentCache.disable();
				if (m_contentCache != null) m_contentCache.disable();
				if (m_submissionCache != null) m_submissionCache.disable();
			}
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}

	} // init

	/*******************************************************************************
	* BaseAssignmentService extensions
	*******************************************************************************/

	/*******************************************************************************
	* BaseAssignmentService extensions
	*******************************************************************************/

	/**
	 * Construct a Storage object for Assignments.
	 * @return The new storage object for Assignments.
	 */
	protected AssignmentStorage newAssignmentStorage()
	{
		return new XmlFileAssignmentStorage(new AssignmentStorageUser());

	} // newAssignmentStorage

	/**
	 * Construct a Storage object for AssignmentsContents.
	 * @return The new storage object for AssignmentContents.
	 */
	protected AssignmentContentStorage newContentStorage()
	{
		return new XmlFileAssignmentContentStorage(new AssignmentContentStorageUser());

	} // newContentStorage

	/**
	 * Construct a Storage object for AssignmentSubmissions.
	 * @return The new storage object for AssignmentSubmissions.
	 */
	protected AssignmentSubmissionStorage newSubmissionStorage()
	{
		return new XmlFileAssignmentSubmissionStorage(new AssignmentSubmissionStorageUser());

	} // newSubmissionStorage

	/*******************************************************************************
	* Storage implementations
	*******************************************************************************/

	/*******************************************************************************
	* AssignmentStorage implementation
	*******************************************************************************/

	/**
	* Covers for the BaseXmlFileStorage, providing Assignment and AssignmentEdit parameters
	*/
	protected class XmlFileAssignmentStorage extends BaseXmlFileStorage implements AssignmentStorage
	{
		/**
		* Construct.
		* @param assignment The StorageUser class to call back for creation of Resource and Edit objects.
		*/
		public XmlFileAssignmentStorage(AssignmentStorageUser assignment)
		{
			super(m_assignmentStoragePath, "assignments", null, "assignment", assignment);

		} // XmlFileAssignmentStorage

		public boolean check(String id)
		{
			return super.checkResource(null, id);
		}

		public Assignment get(String id)
		{
			return (Assignment) super.getResource(null, id);
		}

		public List getAll(String context)
		{
			// TODO: actually filter results by context!
			return super.getAllResources(null);
		}

		public AssignmentEdit put(String id, String context)
		{
			// pack the context in an array
			Object[] others = new Object[1];
			others[0] = context;
			return (AssignmentEdit) super.putResource(null, id, others);
		}

		public AssignmentEdit edit(String id)
		{
			return (AssignmentEdit) super.editResource(null, id);
		}

		public void commit(AssignmentEdit edit)
		{
			super.commitResource(null, edit);
		}

		public void cancel(AssignmentEdit edit)
		{
			super.cancelResource(null, edit);
		}

		public void remove(AssignmentEdit edit)
		{
			super.removeResource(null, edit);
		}

	} // XmlFileAssignmentStorage

	/*******************************************************************************
	* AssignmentContentStorage implementation
	*******************************************************************************/

	/**
	* Covers for the BaseXmlFileStorage, providing AssignmentContent and AssignmentContentEdit parameters
	*/
	protected class XmlFileAssignmentContentStorage extends BaseXmlFileStorage implements AssignmentContentStorage
	{
		/**
		* Construct.
		* @param content The StorageUser class to call back for creation of Resource and Edit objects.
		*/
		public XmlFileAssignmentContentStorage(AssignmentContentStorageUser content)
		{
			super(m_contentStoragePath, "contents", null, "content", content);

		} // XmlFileAssignmentContentStorage

		public boolean check(String id)
		{
			return super.checkResource(null, id);
		}

		public AssignmentContent get(String id)
		{
			return (AssignmentContent) super.getResource(null, id);
		}

		public List getAll(String context)
		{
			// TODO: actually filter by context!
			return super.getAllResources(null);
		}

		public AssignmentContentEdit put(String id, String context)
		{
			// pack the context in an array
			Object[] others = new Object[1];
			others[0] = context;
			return (AssignmentContentEdit) super.putResource(null, id, others);
		}

		public AssignmentContentEdit edit(String id)
		{
			return (AssignmentContentEdit) super.editResource(null, id);
		}

		public void commit(AssignmentContentEdit edit)
		{
			super.commitResource(null, edit);
		}

		public void cancel(AssignmentContentEdit edit)
		{
			super.cancelResource(null, edit);
		}

		public void remove(AssignmentContentEdit edit)
		{
			super.removeResource(null, edit);
		}

	} // XmlFileAssignmentContentStorage

	/*******************************************************************************
	* AssignmentSubmissionStorage implementation
	*******************************************************************************/

	/**
	* Covers for the BaseXmlFileStorage, providing AssignmentSubmission and AssignmentSubmissionEdit parameters
	*/
	protected class XmlFileAssignmentSubmissionStorage extends BaseXmlFileStorage implements AssignmentSubmissionStorage
	{
		/**
		* Construct.
		* @param submission The StorageUser class to call back for creation of Resource and Edit objects.
		*/
		public XmlFileAssignmentSubmissionStorage(AssignmentSubmissionStorageUser submission)
		{
			super(m_submissionStoragePath, "submissions", null, "submission", submission);

		} // XmlFileAssignmentSubmissionStorage

		public boolean check(String id)
		{
			return super.checkResource(null, id);
		}

		public AssignmentSubmission get(String id)
		{
			return (AssignmentSubmission) super.getResource(null, id);
		}

		public List getAll(String context)
		{
			// TODO: actually filter by context!
			return super.getAllResources(null);
		}

		public AssignmentSubmissionEdit put(String id, String context, String assignmentId)
		{
			// pack the context in an array
			Object[] others = new Object[2];
			others[0] = context;
			others[1] = assignmentId;
			return (AssignmentSubmissionEdit) super.putResource(null, id, others);
		}

		public AssignmentSubmissionEdit edit(String id)
		{
			return (AssignmentSubmissionEdit) super.editResource(null, id);
		}

		public void commit(AssignmentSubmissionEdit edit)
		{
			super.commitResource(null, edit);
		}

		public void cancel(AssignmentSubmissionEdit edit)
		{
			super.cancelResource(null, edit);
		}

		public void remove(AssignmentSubmissionEdit edit)
		{
			super.removeResource(null, edit);
		}

	} // XmlFileAssignmentSubmissionStorage

} // XmlFileAssignmentService

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/assignment/XmlFileAssignmentService.java,v 1.9 2004/09/03 01:34:56 ggolden.umich.edu Exp $
*
**********************************************************************************/
