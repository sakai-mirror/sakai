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
package org.sakaiproject.service.legacy.time;

// imports

/**
 * <p>TimeRange ...</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public interface TimeRange extends Cloneable
{
	/**
	 * Check if this Time in my range.
	 * @param time The time to check for inclusion.
	 * @return true if the time is in the range, false if not.
	 */
	boolean contains(Time time);

	/**
	 * Check if this TimeRange overlap this other TimeRange at all.
	 * @param range The time range to check for overlap.
	 * @return true if any time in the other range is in this range, false if not.
	 */
	boolean overlaps(TimeRange range);

	/**
	 * Check if this TimeRange completely contains the other range.
	 * @param range The time range to check for containment.
	 * @return true if the other TimeRange is within this TimeRange, false if not.
	 */
	boolean contains(TimeRange range);

	/**
	 * Access the first Time of the range.
	 * @return the first Time actually in the range.
	 */
	Time firstTime();

	/**
	 * Access the last Time in the range.
	 * @return the last Time actually in the range.
	 */
	Time lastTime();

	/**
	 * Access the first Time of the range (fudged).
	 * @param fudge How many ms to increase if the first is not included.
	 * @return the first Time actually in the range (fudged).
	 */
	Time firstTime(long fudge);

	/**
	 * Access the last Time of the range (fudged).
	 * @param fudge How many ms to decrease if the last is not included.
	 * @return the first Time actually in the range (fudged).
	 */
	Time lastTime(long fudge);

	/**
	 * Format the TimeRange - human readable.
	 * @return The TimeRange in string format.
	 */
	String toStringHR();

	/**
	 * Access the duration of the TimeRange, in milliseconds.
	 * @return The duration.
	 */
	long duration();

	/**
	 * Shift the time range back an interval
	 * @param i time intervel in ms
	 */
	void shiftBackward(long i);

	/**
	 * Shift the time range forward an interval
	 * 
	 * @param i time intervel in ms
	 */
	void shiftForward(long i);

	/**
	 * Enlarge or shrink the time range by multiplying a zooming factor.
	 * @param f zooming factor
	 */
	void zoom(double f);

	/**
	 * Adjust this time range based on the difference between the origRange and the modRange, if any.
	 * @param original the original time range.
	 * @param modified the modified time range.
	 */
	void adjust(TimeRange original, TimeRange modified);

	/**
	 * Clone the TimeRange
	 * @return A cloned TimeRange.
	 */
	Object clone();

	/**
	 * Check if the TimeRange is really just a single Time.
	 * @return true if the tiTimeRange is a single Time, false if it is not.
	 */
	boolean isSingleTime();
}



