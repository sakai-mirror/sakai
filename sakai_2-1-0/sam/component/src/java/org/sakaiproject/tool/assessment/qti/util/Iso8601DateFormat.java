/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
package org.sakaiproject.tool.assessment.qti.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.tool.assessment.qti.exception.Iso8601FormatException;

/**
 * Based on ISO8601 Specification.
 *
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id$
 */
public class Iso8601DateFormat
{
  public static final String BASIC_FORMAT = "yyyyMMdd'T'HHmmssZ";
  private static Log log = LogFactory.getLog(Iso8601DateFormat.class);
  private static final String GMT = "GMT";

  // 20031107T152420-0500  or
  // 2003-11-07T15:24:20-05:00
  private static final Pattern PATTERN_MATCH =
    Pattern.compile(
      "^(?:(\\d{2,4})-?)?" + // year
      "(?:(\\d{2})-?)?" + // month
      "(\\d{2})?" + // day of month
      "T?" + // time separator
      "(?:(\\d{2}):?)?" + // hour
      "(?:(\\d{2}):?)?" + // minutes
      "(\\d{2})?" + // seconds
      "(Z?|(?:\\+|-).+)?$"); // timezone: -0500 or +08:00 or -05 or Z
  private String pattern;

  /**
   * Creates a new Iso8601DateFormat object.
   */
  public Iso8601DateFormat()
  {
    log.debug("new Iso8601DateFormat()");
    this.pattern = BASIC_FORMAT;
  }

  /**
   * DOCUMENT ME!
   *
   * @param simpleDateFormatPattern
   *
   * @see java.text.SimpleDateFormat
   */
  public Iso8601DateFormat(String simpleDateFormatPattern)
  {
    log.debug("new Iso8601DateFormat(String " + pattern + ")");
    this.pattern = simpleDateFormatPattern;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param date DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String format(Date date)
  {
    log.debug("format(Date " + date + ")");
    SimpleDateFormat sdf = null;
    if(this.pattern == null)
    {
      sdf = new SimpleDateFormat();
    }
    else
    {
      sdf = new SimpleDateFormat(pattern);
    }

    return sdf.format(date);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param iso8601String DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws Iso8601FormatException DOCUMENTATION PENDING
   */
  public Calendar parse(String iso8601String)
    throws Iso8601FormatException
  {
    log.debug("parse(String " + iso8601String + ")");
    if(iso8601String == null)
    {
      throw new Iso8601FormatException(
        "illegal String iso8601TimeInterval argument: " + iso8601String);
    }

    iso8601String = iso8601String.toUpperCase();
    final Matcher matcher = PATTERN_MATCH.matcher(iso8601String);
    if(matcher.matches())
    {
      if(log.isDebugEnabled())
      {
        for(int i = 0; i <= matcher.groupCount(); i++)
        {
          log.debug(i + "=" + matcher.group(i));
        }
      }

      String tz = matcher.group(7);
      Calendar cal = null;
      if((tz != null) && (tz.length() > 0))
      {
        if("Z".equals(tz))
        {
          tz = "Zulu";
        }
        else
        {
          tz = GMT + tz;
        }

        if(log.isDebugEnabled())
        {
          log.debug("tz=" + tz);
          log.debug("TimeZone.getID()=" + TimeZone.getTimeZone(tz).getID());
        }

        cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(tz));
      }
      else
      {
        cal = GregorianCalendar.getInstance();

        /* data must be zeroed out to counteract now behavior*/
        cal.clear();
      }

      /* year */
      if(matcher.group(1) != null)
      {
        final int year = Integer.parseInt(matcher.group(1));
        cal.set(Calendar.YEAR, year);
      }
      else
      {
        throw new Iso8601FormatException("Year is required");
      }

      /* month */
      if(matcher.group(2) != null)
      {
        final int month = Integer.parseInt(matcher.group(2)) - 1; // zero based
        cal.set(Calendar.MONTH, month);
      }

      /* date (day) */
      if(matcher.group(3) != null)
      {
        final int date = Integer.parseInt(matcher.group(3));
        cal.set(Calendar.DAY_OF_MONTH, date);
      }

      /* hour */
      if(matcher.group(4) != null)
      {
        final int hour = Integer.parseInt(matcher.group(4));
        cal.set(Calendar.HOUR_OF_DAY, hour);
      }

      /* minutes */
      if(matcher.group(5) != null)
      {
        final int min = Integer.parseInt(matcher.group(5));
        cal.set(Calendar.MINUTE, min);
      }

      /* seconds */
      if(matcher.group(6) != null)
      {
        final int sec = Integer.parseInt(matcher.group(6));
        cal.set(Calendar.SECOND, sec);
      }

      return cal;
    }

    throw new Iso8601FormatException("ISO8601 format could not be matched");
  }
}


