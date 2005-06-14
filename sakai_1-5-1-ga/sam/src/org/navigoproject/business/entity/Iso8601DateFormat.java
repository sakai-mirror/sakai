/*
 *                       Navigo Software License
 *
 * Copyright 2003, Trustees of Indiana University, The Regents of the University
 * of Michigan, and Stanford University, all rights reserved.
 *
 * This work, including software, documents, or other related items (the
 * "Software"), is being provided by the copyright holder(s) subject to the
 * terms of the Navigo Software License. By obtaining, using and/or copying this
 * Software, you agree that you have read, understand, and will comply with the
 * following terms and conditions of the Navigo Software License:
 *
 * Permission to use, copy, modify, and distribute this Software and its
 * documentation, with or without modification, for any purpose and without fee
 * or royalty is hereby granted, provided that you include the following on ALL
 * copies of the Software or portions thereof, including modifications or
 * derivatives, that you make:
 *
 *    The full text of the Navigo Software License in a location viewable to
 *    users of the redistributed or derivative work.
 *
 *    Any pre-existing intellectual property disclaimers, notices, or terms and
 *    conditions. If none exist, a short notice similar to the following should
 *    be used within the body of any redistributed or derivative Software:
 *    "Copyright 2003, Trustees of Indiana University, The Regents of the
 *    University of Michigan and Stanford University, all rights reserved."
 *
 *    Notice of any changes or modifications to the Navigo Software, including
 *    the date the changes were made.
 *
 *    Any modified software must be distributed in such as manner as to avoid
 *    any confusion with the original Navigo Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) and/or Indiana University,
 * The University of Michigan, Stanford University, or Navigo may NOT be used
 * in advertising or publicity pertaining to the Software without specific,
 * written prior permission. Title to copyright in the Software and any
 * associated documentation will at all times remain with the copyright holders.
 * The export of software employing encryption technology may require a specific
 * license from the United States Government. It is the responsibility of any
 * person or organization contemplating export to obtain such a license before
 * exporting this Software.
 */

package org.navigoproject.business.entity;

import org.navigoproject.business.exception.Iso8601FormatException;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DOCUMENT ME!
 *
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id: Iso8601DateFormat.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
public class Iso8601DateFormat
{
  public static final String BASIC_FORMAT = "yyyyMMdd'T'HHmmssZ";
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(Iso8601DateFormat.class);
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
    LOG.debug("new Iso8601DateFormat()");
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
    LOG.debug("new Iso8601DateFormat(String " + pattern + ")");
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
    LOG.debug("format(Date " + date + ")");
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
    LOG.debug("parse(String " + iso8601String + ")");
    if(iso8601String == null)
    {
      throw new Iso8601FormatException(
        "illegal String iso8601TimeInterval argument: " + iso8601String);
    }

    iso8601String = iso8601String.toUpperCase();
    final Matcher matcher = PATTERN_MATCH.matcher(iso8601String);
    if(matcher.matches())
    {
      if(LOG.isDebugEnabled())
      {
        for(int i = 0; i <= matcher.groupCount(); i++)
        {
          LOG.debug(i + "=" + matcher.group(i));
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

        if(LOG.isDebugEnabled())
        {
          LOG.debug("tz=" + tz);
          LOG.debug("TimeZone.getID()=" + TimeZone.getTimeZone(tz).getID());
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
