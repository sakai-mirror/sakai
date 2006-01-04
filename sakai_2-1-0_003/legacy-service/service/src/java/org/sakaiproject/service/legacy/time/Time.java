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

package org.sakaiproject.service.legacy.time;

import java.io.Serializable;

/**
 * <p>Time ...</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public interface Time extends Cloneable, Comparable, Serializable
{
	/**
	 * Format as a string, GMT, for a SQL statement.
	 * @return Time in string format.
	 */
	String toStringSql();

	/**
	 * Format as a string, Local time zone.
	 * @return Time in string format.
	 */
	String toStringLocal();

	/**
	 * Format as a string, Human Readable, full format, GMT.
	 * @return Time in string format.
	 */
	String toStringGmtFull();

	/**
	 * Format as a string, Human Readable, full format, Local.
	 * @return Time in string format.
	 */
	String toStringLocalFull();

	/**
	 * Format as a string, Human Readable, full format, Local, with zone.
	 * @return Time in string format.
	 */
	String toStringLocalFullZ();

	/**
	 * Format as a string, Human Readable, short (time only) format, GMT.
	 * @return Time in string format.
	 */
	String toStringGmtShort();

	/**
	 * Format as a string, Human Readable, short (time only) format, Local.
	 * @return Time in string format.
	 */
	String toStringLocalShort();

	/**
	 * Format as a string, Human Readable, time only format, GMT.
	 * @return Time in string format.
	 */
	String toStringGmtTime();

	/**
	 * Format as a string, Human Readable, time only format, Local.
	 * @return Time in string format.
	 */
	String toStringLocalTime();

	/**
	 * Format as a string, Human Readable, time only format, 24hour Local.
	 * @return Time in string format.
	 */
	String toStringLocalTime24();

	/**
	 * Format as a string, Human Readable, date only format, GMT.
	 * @return Time in string format.
	 */
	String toStringGmtDate();

	/**
	 * Format as a string, Human Readable, date only format, Local.
	 * @return Time in string format.
	 */
	String toStringLocalDate();

	/**
	 * Format as a string, short format: MM/DD/YY, Local.
	 * @return Time in string format.
	 */
	String toStringLocalShortDate();

	/**
	 * Format as a file path based on the date and time.
	 * @return Time is string format.
	 */
	String toStringFilePath();

	/**
	 * Set the time in milliseconds since.
	 * @param value The milliseconds since value for the time.
	 */
	void setTime(long value);

	/**
	* Access the milliseconds since.
	* @return The milliseconds since value.
	*/
	long getTime();

	/**
	 * Is this time before the other time?
	 * @param other The other time for the comparison.
	 * @return true if this time is before the other, false if not.
	 */
	boolean before(Time other);

	/**
	 * Is this time after the other time?
	 * @param other The other time for the comparison.
	 * @return true if this time is after the other, false if not.
	 */
	boolean after(Time other);

	/**
	 * Make a clone.
	 * @return The clone.
	 */
	Object clone();

	/**
	 * Access the time value as a TimeBreakdown object, in GMT
	 * @return A TimeBreakdown object representing this time's value in GMT
	 */
	TimeBreakdown breakdownGmt();

	/**
	 * Access the time value as a TimeBreakdown object, in Local
	 * @return A TimeBreakdown object representing this time's value in GMT
	 */
	TimeBreakdown breakdownLocal();
}



