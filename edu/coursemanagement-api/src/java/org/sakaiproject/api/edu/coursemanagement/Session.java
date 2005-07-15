/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.api.edu.coursemanagement;

/**
 * A Term is a unit of time in which a course offering exists. It can include a schedule of events, such as holidays.
 * <ul>
 * <li>A title [String]
 * <li>An abbreviation [String]
 * <li>A school year [String]
 * <li>A current term flag [Boolean]
 * <li>A unique identifier [Id Object]
 * <li>A session type [Type Object]
 * <li>A start/end time or schedule. [Schedule Id Object]
 * <li>A grading start/end time or schedule. [Schedule Id Object]
 * </ul>
 * 
 * @author Mark Norton
 */
public interface Session
{

	/**
	 * Get the title of this term/session.
	 * 
	 * @return term title.
	 */
	public String getTitle();

	/**
	 * Set the tile of this term/session.
	 * 
	 * @param title
	 */
	public void setTitle(String title);

	/**
	 * Get the abbreviation string for this session.
	 * 
	 * @return term abbreviation string
	 */
	public String getAbbreviation();

	/**
	 * Set the abbreviation string for this session.
	 * 
	 * @param abbreviation
	 */
	public void setAbbreviation(String abbreviation);

	/**
	 * Get the year of this session. Note that this could be of the form "2005-2006" rather than a simple year string.
	 * 
	 * @return year string
	 */
	public String getYear();

	/**
	 * Set the year string.
	 * 
	 * @param year
	 */
	public void setYear(String year);

	/**
	 * Return true if this is the current session. The session is current if today's date falls between the start and end times.
	 * 
	 * @return true if this is the current session.
	 */
	public boolean isCurrent();

	/**
	 * Get the uuid of this session.
	 * 
	 * @return
	 */
	public String getUuid();

	/**
	 * Get the term type of this session. The session type might be spring, summer, etc. or first_quarter, second_quarter, etc.
	 * 
	 * @return term type
	 */
	public SessionType getSessionType();

	/**
	 * Set the session type.
	 * 
	 * @param type
	 */
	public void setSessionType(SessionType type);

	/**
	 * Get the starting time of the term given by a Schedule event.
	 * 
	 * @return start Schedule.
	 */
	// public Schedule getStart();
	/**
	 * Set the end time of the session.
	 * 
	 * @author Mark Norton
	 */
	// public void setStart (Schedule start);
	/**
	 * Get the end time of the session.
	 * 
	 * @author Mark Norton
	 */
	// public Schedule getEnd();
	/**
	 * Set the end time of the session.
	 * 
	 * @author Mark Norton
	 */
	// public void setEnd(Schedule end);
	/**
	 * Get the starting grade time for this session.
	 * 
	 * @author Mark Norton
	 */
	// public Schedule getGradeStart();
	/**
	 * Set the starting grade time for this session.
	 * 
	 * @author Mark Norton
	 */
	// public void setGradeStart (Schedule start);
	/**
	 * Get the end grading time for this session.
	 * 
	 * @author Mark Norton
	 */
	// public Schedule getGradeEnd();
	/**
	 * Set the end grading time for this session.
	 * 
	 * @author Mark Norton
	 */
	// public void setGradeEnd(Schedule end);

}
