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

import java.util.GregorianCalendar;
import java.util.TimeZone;

// imports

/**
 * <p>TimeService ...</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public interface TimeService
{
	/** This string can be used to find the service in the service manager. */
	public static final String SERVICE_NAME = TimeService.class.getName();
   
	/** Preferences key for user's time zone */
	public static final String TIMEZONE_KEY = "timezone";
   
	/**
	 * Get a time object.
	 * @return A time object, set to now.
	 */
	Time newTime();

	/**
	 * Get a time object, set from this string in our format, Gmt values
	 * @param value time format string.
	 * @return A time object.
	 */
	Time newTimeGmt(String value);

	/**
	 * Get a time object, set from this long milliseconds since the "epoc" value.
	 * @param value time long milliseconds value.
	 * @return A time object.
	 */
	Time newTime(long value);

	/**
	 * Get a time object, based on the time set in the calendar
	 * @return A time object, set to now.
	 */
	Time newTime(GregorianCalendar cal);

	/**
	 * Get a time object, set from individual ints, Gmt values
	 * @param year full year (i.e. 1999, 2000)
	 * @param month month in year (1..12)
	 * @param day day in month (1..31)
	 * @param hour hour in day (0..23)
	 * @param minuet minute in hour (0..59)
	 * @param second second in minute (0..59)
	 * @param millisecond millisecond in second (0..999)
	 * @return A time object.
	 */
	Time newTimeGmt(int year, int month, int day, int hour, int minute, int second, int millisecond);

	/**
	 * Get a time object, set from this breakdown (Gmt values).
	 * @param breakdown The time breakdown values.
	 * @return A time object.
	 */
	Time newTimeGmt(TimeBreakdown breakdown);

	/**
	 * Get a time object, set from individual ints, Local values
	 * @param year full year (i.e. 1999, 2000)
	 * @param month month in year (1..12)
	 * @param day day in month (1..31)
	 * @param hour hour in day (0..23)
	 * @param minuet minute in hour (0..59)
	 * @param second second in minute (0..59)
	 * @param millisecond millisecond in second (0..999)
	 * @return A time object.
	 */
	Time newTimeLocal(int year, int month, int day, int hour, int minute, int second, int millisecond);

	/**
	 * Get a time object, set from this breakdown (Local values).
	 * @param breakdown The time breakdown values.
	 * @return A time object.
	 */
	Time newTimeLocal(TimeBreakdown breakdown);

	/**
	 * Get a TimeBreakdown object, set from individual ints.
	 * @param year full year (i.e. 1999, 2000)
	 * @param month month in year (1..12)
	 * @param day day in month (1..31)
	 * @param hour hour in day (0..23)
	 * @param minuet minute in hour (0..59)
	 * @param second second in minute (0..59)
	 * @param millisecond millisecond in second (0..999)
	 * @return A TimeBreakdown.
	 */
	TimeBreakdown newTimeBreakdown(int year, int month, int day, int hour, int minute, int second, int millisecond);

	/**
	 * Get a TimeRange, from parts.
	 * @param start The start Time.
	 * @param end The end Time.
	 * @param startIncluded true if start is part of the range, false if not.
	 * @param endIncluded true of end is part of the range, false if not.
	 * @return A TimeRange.
	 */
	TimeRange newTimeRange(Time start, Time end, boolean startIncluded, boolean endIncluded);

	/**
	 * Get a TimeRange, from our string format.
	 * @param value The TimeRange string.
	 * @return A TimeRange.
	 */
	TimeRange newTimeRange(String value);
	
	/**
	 * Get a TimeRange, from a single time.
	 * @param startAndEnd The Time for the range.
	 * @return A TimeRange.
	 */
	TimeRange newTimeRange(Time startAndEnd);

	/**
	 * Get a TimeRange, from a time value long start and duration
	 * @param start The long start time (milliseconds since).
	 * @param duration The long milliseconds duration.
	 * @return A TimeRange.
	 */
	TimeRange newTimeRange(long start, long duration);

	/**
	 * Get a TimeRange, from two times, inclusive.
	 * @param start The start time.
	 * @param end The end time.
	 * @return A TimeRange.
	 */
	TimeRange newTimeRange(Time start, Time end);

	/**
	 * Access the local TimeZone.
	 * @return The local TimeZone.
	 */
	TimeZone getLocalTimeZone();

	/**
	 * Clear local time zone for specified user
	 * @return true if successful
	 */
	boolean clearLocalTimeZone( String userId );

	/**
	 * Get a Calendar, set to this zone and these values.
	 * @param zone The TimeZone for the calendar.
	 * @param year full year (i.e. 1999, 2000)
	 * @param month month in year (1..12)
	 * @param day day in month (1..31)
	 * @param hour hour in day (0..23)
	 * @param min minute in hour (0..59)
	 * @param second second in minute (0..59)
	 * @param ms millisecond in second (0..999)
	 */
	GregorianCalendar getCalendar(TimeZone zone, int year, int month, int day, int hour, int min, int sec, int ms);

	/**
	 * Compare two Time for differences, either may be null
	 * @param a One Time.
	 * @param b The other Time.
	 * @return true if the Times are different, false if they are the same.
	 */
	boolean different(Time a, Time b);
}



