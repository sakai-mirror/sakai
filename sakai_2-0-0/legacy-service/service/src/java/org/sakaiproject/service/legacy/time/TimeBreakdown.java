/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/time/TimeBreakdown.java,v 1.1 2005/05/12 15:45:34 ggolden.umich.edu Exp $
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

package org.sakaiproject.service.legacy.time;

/**
 * <p>TimeBreakdown ...</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.1 $
 */
public interface TimeBreakdown
{
	/**
	 * Access the year value.
	 * @return The year value.
	 */
	int getYear();

	/**
	 * Set the year value.
	 * @param year The year value.
	 */
	void setYear(int year);

	/**
	 * Access the month value.
	 * @return The month value.
	 */
	int getMonth();

	/**
	 * Set the month value.
	 * @param month The year value.
	 */
	void setMonth(int month);

	/**
	 * Access the day value.
	 * @return The day value.
	 */
	int getDay();

	/**
	 * Set the day value.
	 * @param day The year value.
	 */
	void setDay(int day);

	/**
	 * Access the hour value.
	 * @return The hour value.
	 */
	int getHour();

	/**
	 * Set the hour value.
	 * @param hour The year value.
	 */
	void setHour(int hour);

	/**
	 * Access the minute value.
	 * @return The minute. value.
	 */
	int getMin();

	/**
	 * Set the minute value.
	 * @param minute The year value.
	 */
	void setMin(int minute);

	/**
	 * Access the second value.
	 * @return The second value.
	 */
	int getSec();

	/**
	 * Set the second value.
	 * @param second The year value.
	 */
	void setSec(int second);

	/**
	 * Access the millisecond value.
	 * @return The millisecond value.
	 */
	int getMs();

	/**
	 * Set the millisecond value.
	 * @param millisecond The year value.
	 */
	void setMs(int millisecond);
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/time/TimeBreakdown.java,v 1.1 2005/05/12 15:45:34 ggolden.umich.edu Exp $
*
**********************************************************************************/
