/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/coursemanagement/Term.java,v 1.1 2005/05/12 15:45:35 ggolden.umich.edu Exp $
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
package org.sakaiproject.service.legacy.coursemanagement;

// import
import org.sakaiproject.service.legacy.time.Time;

/**
* <p>Term has a unique Id assigned by the implementation, a default-term boolean value, a title, a list abbreviation, a start date, and a end-date. </p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.1 $
*/
public class Term
{
	/** The term id. */
	protected String m_id = null;

	/** The term's term attribute. */
	protected String m_term = null;
	
	/** The term's year attribute. */
	protected String m_year = null;

	/** The term's list abbreviation. */
	protected String m_listAbbreviation = null;
	
	/** Is current term or not */
	protected boolean m_isCurrentTerm = false;

	/** The term start time. */
	protected Time m_startTime = null;

	/** The term end time. */
	protected Time m_endTime = null;

	/**
	* Take all values from this object.
	* @param term The term object to take values from.
	*/
	protected void setAll(Term term)
	{
		m_id = term.getId();
		m_term = term.getTerm();
		m_year = term.getYear();
		m_listAbbreviation = term.getListAbbreviation();
		m_isCurrentTerm = term.isCurrentTerm();
		if (((Term) term).m_startTime != null) m_startTime = (Time) ((Term) term).m_startTime.clone();
		if (((Term) term).m_endTime != null) m_endTime = (Time) ((Term) term).m_endTime.clone();

	} // setAll

	/**
	* Access the term id.
	* @return The term id string.
	*/
	public String getId()
	{
		if (m_id == null)
			return "";
		return m_id;

	} // getId

	/**
	* Access the term
	* @return The term
	*/
	public String getTerm()
	{
		return m_term;

	} // getTerm
	
	/**
	* Access the term's year
	* @return The term's year
	*/
	public String getYear()
	{
		return m_year;

	} // getYear

	/**
	* (@inheritDoc)
	*/
	public String getListAbbreviation()
	{
		return m_listAbbreviation;

	} // setListAbbreviation
	
	/**
	 * {@inheritDoc}
	 */
	public boolean isCurrentTerm()
	{
		return m_isCurrentTerm;
	}

	/**
	 * {@inheritDoc}
	 */
	public Time getStartTime()
	{
		return m_startTime;
	}

	/**
	 * {@inheritDoc}
	 */
	public Time getEndTime()
	{
		return m_endTime;
	}

	/**
	* Are these objects equal?  If they are both Term objects, and they have
	* matching id's, they are.
	* @return true if they are equal, false if not.
	*/
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Term))
			return false;
		return ((Term) obj).getId().equals(getId());

	} // equals

	/**
	* Make a hash code that reflects the equals() logic as well.
	* We want two objects, even if different instances, if they have the same id to hash the same.
	*/
	public int hashCode()
	{
		return getId().hashCode();

	} // hashCode

	/**
	* Compare this object with the specified object for order.
	* @return A negative integer, zero, or a positive integer as this object is
	* less than, equal to, or greater than the specified object.
	*/
	public int compareTo(Object obj)
	{
		if (!(obj instanceof Term))
			throw new ClassCastException();

		// if the object are the same, say so
		if (obj == this)
			return 0;

		// sort based on (unique) id
		return getId().compareTo(((Term) obj).getId());

	} // compareTo

	/**
	* Clean up.
	*/
	protected void finalize()
	{

	} // finalize

	/**
	* Set the term's id.
	* Note: this is a special purpose routine that is used only to establish the id field,
	* when the id is null, and cannot be used to change a term's id.
	* @param name The term id.
	*/
	public void setId(String id)
	{
		if (m_id == null)
		{
			m_id = id;
		}

	} // setId

	/**
	* Set the term's term
	* @param title The term's term
	*/
	public void setTerm(String term)
	{
		m_term = term;

	} // setTerm
	
	/**
	* Set the term's year
	* @param year The term's year
	*/
	public void setYear(String year)
	{
		m_year = year;

	} // setYear
	
	/**
	* Set the term's abbreviation
	* @param listAbbreviation The term's list abbreviation
	*/
	public void setListAbbreviation(String listAbbreviation)
	{
		m_listAbbreviation = listAbbreviation;

	} // setListAbbreviation

	/**
	* Set the term current term status.
	* @param isCurrentTerm true if the term is current term; false otherwise.
	*/
	public void setIsCurrentTerm(boolean isCurrentTerm)
	{
		m_isCurrentTerm = isCurrentTerm;

	} // setIsCurrentTerm
	
	/**
	* Set the term start time
	* @param startTime the start time
	*/
	public void setStartTime(Time startTime)
	{
		m_startTime = startTime;

	} // setStartTime
	
	/**
	* Set the term end time
	* @param endTime the end time
	*/
	public void setEndTime(Time endTime)
	{
		m_endTime = endTime;

	} // setEndTime

}	// Term

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/coursemanagement/Term.java,v 1.1 2005/05/12 15:45:35 ggolden.umich.edu Exp $
*
**********************************************************************************/
