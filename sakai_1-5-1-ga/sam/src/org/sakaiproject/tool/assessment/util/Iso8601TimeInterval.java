/*
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
 */



package org.sakaiproject.tool.assessment.util;

import org.navigoproject.business.exception.Iso8601FormatException;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Based on ISO8601 Specification.
 *
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id: Iso8601TimeInterval.java,v 1.1 2005/01/14 01:34:51 esmiley.stanford.edu Exp $
 */
public class Iso8601TimeInterval
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(Iso8601TimeInterval.class);
  private static final long SECONDS = 1000L;
  private static final long MINUTES = 60L * SECONDS;
  private static final long HOURS = 60L * MINUTES;
  private static final long DAYS = 24L * HOURS;
  private static final long WEEKS = 7L * DAYS;
  private static final long MONTHS = 30L * DAYS;
  private static final long YEARS = 365L * DAYS;

  //  i.e.: P2Y10M15DT10H30M20S
  private static final Pattern PATTERN =
    Pattern.compile(
      "-?P(?:(\\d*)Y)?(?:(\\d*)M)?(?:(\\d*)W)?(?:(\\d*)D)?T?(?:(\\d*)H)?(?:(\\d*)M)?(?:(\\d*)S)?");

  // one part of the interval has a fixed date/time
  private boolean bounded = false;
  private boolean recurring = false;
  private String iso8601TimeInterval;
  private Long duration;
  private Calendar begin;
  private Calendar end;
  private Integer years;
  private Integer months;
  private Integer weeks;
  private Integer days;
  private Integer hours;
  private Integer minutes;
  private Integer seconds;
  private boolean negative = false;

  /**
   * Creates a new Iso8601TimeInterval object.
   *
   * @param iso8601TimeInterval DOCUMENTATION PENDING
   *
   * @throws Iso8601FormatException DOCUMENTATION PENDING
   */
  public Iso8601TimeInterval(String iso8601TimeInterval)
    throws Iso8601FormatException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("new TimeInterval(String " + iso8601TimeInterval + ")");
    }

    this.iso8601TimeInterval = iso8601TimeInterval;
    this.duration = new Long(parseLong(iso8601TimeInterval));
  }

  /**
   * Creates a new Iso8601TimeInterval object.
   *
   * @param ms DOCUMENTATION PENDING
   */
  public Iso8601TimeInterval(long ms)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("new Iso8601TimeInterval(long " + ms + ")");
    }

    this.duration = new Long(ms);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param iso8601TimeInterval DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws Iso8601FormatException DOCUMENTATION PENDING
   */
  private long parseLong(String iso8601TimeInterval)
    throws Iso8601FormatException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("parseLong(String " + iso8601TimeInterval + ")");
    }

    if(iso8601TimeInterval == null)
    {
      throw new Iso8601FormatException(
        "illegal String iso8601TimeInterval argument: " + iso8601TimeInterval);
    }

    iso8601TimeInterval = iso8601TimeInterval.toUpperCase();
    Matcher matcher = PATTERN.matcher(iso8601TimeInterval);
    if(matcher.matches())
    {
      if(iso8601TimeInterval.indexOf("-") == 0)
      {
        LOG.debug("negative = true");
        negative = true;
      }

      if(LOG.isDebugEnabled())
      {
        for(int i = 1; i <= matcher.groupCount(); i++)
        {
          LOG.debug("matcher.group(" + i + ")=" + matcher.group(i));
        }
      }

      String tmp = null;

      /* years */
      int years = 0;
      tmp = matcher.group(1);
      if(tmp != null)
      {
        years = Integer.parseInt(tmp);
        this.years = new Integer(tmp);
      }

      /* months */
      int months = 0;
      tmp = matcher.group(2);
      if(tmp != null)
      {
        this.months = new Integer(tmp);
        months = this.months.intValue();
      }

      /* weeks */
      int weeks = 0;
      tmp = matcher.group(3);
      if(tmp != null)
      {
        this.weeks = new Integer(tmp);
        weeks = this.weeks.intValue();
      }

      /* days */
      int days = 0;
      tmp = matcher.group(4);
      if(tmp != null)
      {
        this.days = new Integer(tmp);
        days = this.days.intValue();
      }

      /* hours */
      int hours = 0;
      tmp = matcher.group(5);
      if(tmp != null)
      {
        this.hours = new Integer(tmp);
        hours = this.hours.intValue();
      }

      /* minutes */
      int minutes = 0;
      tmp = matcher.group(6);
      if(tmp != null)
      {
        this.minutes = new Integer(tmp);
        minutes = this.minutes.intValue();
      }

      /* seconds */
      int seconds = 0;
      tmp = matcher.group(7);
      if(tmp != null)
      {
        this.seconds = new Integer(tmp);
        seconds = this.seconds.intValue();
      }

      if(LOG.isDebugEnabled())
      {
        LOG.debug(
          "years=" + years + ", months=" + months + ", weeks=" + weeks +
          ", days=" + days + ", hours=" + hours + ", minutes=" + minutes +
          ", seconds=" + seconds);
      }

      long durtmp =
        (years * YEARS) + (months * MONTHS) + (weeks * WEEKS) + (days * DAYS) +
        (hours * HOURS) + (minutes * MINUTES) + (seconds * SECONDS);
      if(negative)
      {
        return -durtmp;
      }
      else
      {
        return durtmp;
      }
    }
    else
    {
      throw new Iso8601FormatException(
        "Invalid ISO8601 format: " + iso8601TimeInterval);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean isBounded()
  {
    LOG.debug("isBounded()");

    //    return bounded;
    throw new UnsupportedOperationException();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean isRecurring()
  {
    //    return recurring;
    throw new UnsupportedOperationException();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public long getDuration()
  {
    LOG.debug("getDuration()");

    return duration.longValue();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Calendar getBegin()
  {
    throw new UnsupportedOperationException();

    //    return begin;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Calendar getEnd()
  {
    throw new UnsupportedOperationException();

    //    return end;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String toString()
  {
    if(this.iso8601TimeInterval == null)
    {
      this.createIso8601TimeInterval();
    }

    return this.iso8601TimeInterval;
  }

  /**
   * DOCUMENTATION PENDING
   */
  public void createString()
  {
    LOG.debug("regenerateString()");
    this.reset(); // clear any old bean properties
    this.createIso8601TimeInterval();
  }

  /**
   * DOCUMENTATION PENDING
   */
  private void createIso8601TimeInterval()
  {
    LOG.debug("createIso8601TimeInterval()");
    if(this.duration == null)
    {
      throw new IllegalStateException("duration is null!");
    }

    if(this.duration.longValue() < 0)
    {
      negative = true;
    }

    long remainder = this.duration.longValue();

    /* anything above weeks incurs a loss of precision */

    //    int years = (int)(remainder / YEARS);
    //    remainder %= YEARS;

    /* anything above weeks incurs a loss of precision */

    //    int months = (int)(remainder / MONTHS);
    //    remainder %= MONTHS;
    int weeks = (int) (remainder / WEEKS);
    remainder %= WEEKS;

    int days = (int) (remainder / DAYS);
    remainder %= DAYS;

    int hours = (int) (remainder / HOURS);
    remainder %= HOURS;

    int minutes = (int) (remainder / MINUTES);
    remainder %= MINUTES;

    int seconds = (int) (remainder / SECONDS);

    StringBuffer sb = new StringBuffer();
    if(negative)
    {
      sb.append("-");
    }

    sb.append("P");
    if(weeks != 0)
    {
      if(negative)
      {
        sb.append(-weeks);
        this.weeks = new Integer(-weeks);
      }
      else
      {
        sb.append(weeks);
        this.weeks = new Integer(weeks);
      }

      sb.append("W");
    }

    if(days != 0)
    {
      if(negative)
      {
        sb.append(-days);
        this.days = new Integer(-days);
      }
      else
      {
        sb.append(days);
        this.days = new Integer(days);
      }

      sb.append("D");
    }

    if((hours != 0) || (minutes != 0) || (seconds != 0))
    {
      sb.append("T");
      if(hours != 0)
      {
        if(negative)
        {
          sb.append(-hours);
          this.hours = new Integer(-hours);
        }
        else
        {
          sb.append(hours);
          this.hours = new Integer(hours);
        }

        sb.append("H");
      }

      if(minutes != 0)
      {
        if(negative)
        {
          sb.append(-minutes);
          this.minutes = new Integer(-minutes);
        }
        else
        {
          sb.append(minutes);
          this.minutes = new Integer(minutes);
        }

        sb.append("M");
      }

      if(seconds != 0)
      {
        if(negative)
        {
          sb.append(-seconds);
          this.seconds = new Integer(-seconds);
        }
        else
        {
          sb.append(seconds);
          this.seconds = new Integer(seconds);
        }

        sb.append("S");
      }
    }

    this.iso8601TimeInterval = sb.toString();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param args DOCUMENTATION PENDING
   */
  public static void main(String[] args)
  {
    org.apache.log4j.BasicConfigurator.configure();
    try
    {
      Iso8601TimeInterval ti = new Iso8601TimeInterval("-P2y10m15dT10H30M20S");

      //      Iso8601TimeInterval ti = new Iso8601TimeInterval("PT1S");
      //      Iso8601TimeInterval ti = new Iso8601TimeInterval("P6W");
      //      Iso8601TimeInterval ti = new Iso8601TimeInterval(null);
      long duration = ti.getDuration();
      LOG.debug("duration=" + duration);
      LOG.debug("weeks=" + ti.getWeeks());
      LOG.debug("days=" + ti.getDays());

      Iso8601TimeInterval t2 = new Iso8601TimeInterval(duration);
      LOG.debug("t2=" + t2);
      LOG.debug("t2.getWeeks()=" + t2.getWeeks());
      LOG.debug("t2.getHours()=" + t2.getHours());
    }
    catch(Iso8601FormatException ex)
    {
      LOG.error(ex.getMessage(), ex);
    }
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public Integer getDays()
  {
    return days;
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public Integer getHours()
  {
    return hours;
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public Integer getMinutes()
  {
    return minutes;
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public Integer getMonths()
  {
    return months;
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public Integer getSeconds()
  {
    return seconds;
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public Integer getWeeks()
  {
    return weeks;
  }

  /**
   * DOCUMENT ME!
   *
   * @return
   */
  public Integer getYears()
  {
    return years;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean isNegative()
  {
    return this.negative;
  }

  /**
   * DOCUMENTATION PENDING
   */
  private void reset()
  {
    LOG.debug("reset()");
    this.years = null;
    this.months = null;
    this.weeks = null;
    this.days = null;
    this.hours = null;
    this.minutes = null;
    this.seconds = null;
    this.negative = false;
  }
}
