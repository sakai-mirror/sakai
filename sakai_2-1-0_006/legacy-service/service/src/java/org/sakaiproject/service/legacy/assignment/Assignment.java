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
package org.sakaiproject.service.legacy.assignment;

// import
import java.util.List;

import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.time.Time;


/**
* <p>Assignment is an interface for the CHEF assignments module.
* It represents a specific assignment (as for a specific section or class).</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $ $
*/
public interface Assignment
	extends Entity, Comparable
{
	/** Grade type not set */
	public static final int GRADE_TYPE_NOT_SET = -1;

	/** Ungraded grade type */
	public static final int UNGRADED_GRADE_TYPE = 1;

	/** Letter grade type */
	public static final int LETTER_GRADE_TYPE = 2;

	/** Score based grade type */
	public static final int SCORE_GRADE_TYPE = 3;

	/** Pass/fail grade type */
	public static final int PASS_FAIL_GRADE_TYPE = 4;

	/** Grade type that only requires a check */
	public static final int CHECK_GRADE_TYPE = 5;

	/** Ungraded grade type string */
	public static final String UNGRADED_GRADE_TYPE_STRING = "Ungraded";

	/** Letter grade type string */
	public static final String LETTER_GRADE_TYPE_STRING = "Letter Grade";

	/** Score based grade type string */
	public static final String SCORE_GRADE_TYPE_STRING = "Points";

	/** Pass/fail grade type string */
	public static final String PASS_FAIL_GRADE_TYPE_STRING = "Pass/Fail";

	/** Grade type that only requires a check string */
	public static final String CHECK_GRADE_TYPE_STRING = "Checkmark";

	/** Assignment type not yet set */
	public static final int ASSIGNMENT_SUBMISSION_TYPE_NOT_SET = -1;

	/** Text only assignment type */
	public static final int TEXT_ONLY_ASSIGNMENT_SUBMISSION = 1;

	/** Attachment only assignment type */
	public static final int ATTACHMENT_ONLY_ASSIGNMENT_SUBMISSION = 2;

	/** Text and/or attachment assignment type */
	public static final int TEXT_AND_ATTACHMENT_ASSIGNMENT_SUBMISSION = 3;

	/** Honor Pledge not yet set */
	public static final int HONOR_PLEDGE_NOT_SET = -1;

	/** Honor Pledge not yet set */
	public static final int HONOR_PLEDGE_NONE = 1;

	/** Honor Pledge not yet set */
	public static final int HONOR_PLEDGE_ENGINEERING = 2;
	

	/**
	 * Access the AssignmentContent of this Assignment.
	 * @return The Assignment's AssignmentContent.
	 */
	public AssignmentContent getContent();

	/**
	 * Access the reference of the AssignmentContent of this Assignment.
	 * @return The AssignmentContent's reference.
	 */
	public String getContentReference();
	
	/**
	 * Access the first time at which the assignment can be viewed;
	 * may be null.
	 * @return The Time at which the assignment is due, or null if unspecified.
	 */
	public Time getOpenTime();

	/**
	 * Access the time at which the assignment is due;
	 * may be null.
	 * @return The Time at which the Assignment is due, or null if unspecified.
	 */
	public Time getDueTime();

	/**
	 * Access the drop dead time after which responses to this assignment are considered late;
	 * may be null.
	 * @return The Time object representing the drop dead time, or null if unspecified.
	 */
	public Time getDropDeadTime();

	/**
	 * Access the close time after which this assignment can no longer be viewed, and
	 * after which submissions will not be accepted.  May be null.
	 * @return The Time after which the Assignment is closed, or null if unspecified.
	 */
	public Time getCloseTime();

	/**
	 * Access the section info.
	 * @return The section id.
	 */
	public String getSection();

	/**
	 * Access the context at the time of creation.
	 * @return String - the context string.
	 */
	public String getContext();
	
	/**
	 * Get whether this is a draft or final copy.
	 * @return True if this is a draft, false if it is a final copy.
	 */
	public boolean getDraft();

	/**
	 * Access the creator of this object.
	 * @return String - The id of the creator.
	 */
	public String getCreator();

	/**
	 * Access the time that this object was created.
	 * @return The Time object representing the time of creation.
	 */
	public Time getTimeCreated();

	/**
	 * Access the list of authors.
	 * @return List of authors as User objects.
	 */
	public List getAuthors();

	/**
	 * Access the time of last modificaiton.
	 * @return The Time of last modification.
	 */
	public Time getTimeLastModified();

	/**
	 * Access the author of last modification
	 * @return String - The id of the author.
	 */
	public String getAuthorLastModified();
	
	/**
	 * Access the title.
	 * @return The Assignment's title.
	 */
	public String getTitle();

}	// Assignment



