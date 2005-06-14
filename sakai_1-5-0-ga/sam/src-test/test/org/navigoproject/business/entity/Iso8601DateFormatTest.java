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

package test.org.navigoproject.business.entity;

import org.navigoproject.business.entity.Iso8601DateFormat;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;

/**
 * DOCUMENT ME!
 *
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id: Iso8601DateFormatTest.java,v 1.1.1.1 2004/07/28 21:32:08 rgollub.stanford.edu Exp $
 */
public class Iso8601DateFormatTest
  extends TestCase
{
  private Integer year;
  private Integer month;
  private Integer date;
  private Integer hour;
  private Integer min;
  private Integer sec;
  private String gmtOffset1;
  private String gmtOffset2;
  private String gmtOffset1Terse;
  private String gmtOffset2Terse;
  private String gmtOffset1Ext;
  private String gmtOffset2Ext;
  private Long rawOffset1;
  private Long rawOffset2;

  static
  {
    BasicConfigurator.configure();
  }

  /* (non-Javadoc)
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp()
    throws Exception
  {
    super.setUp();
    this.year = new Integer(2003);
    this.month = new Integer(11);
    this.date = new Integer(10);
    this.hour = new Integer(14);
    this.min = new Integer(59);
    this.sec = new Integer(33);
    this.gmtOffset1 = "+0800";
    this.gmtOffset2 = "-0500";
    this.gmtOffset1Terse = "+08";
    this.gmtOffset2Terse = "-05";
    this.gmtOffset1Ext = "+08:00";
    this.gmtOffset2Ext = "-05:00";
    this.rawOffset1 = new Long(8L * 60 * 60 * 1000);
    this.rawOffset2 = new Long(-5L * 60 * 60 * 1000);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testFormat()
    throws Exception
  {
    Iso8601DateFormat o = new Iso8601DateFormat();
    Date now = new Date();
    String iso = o.format(now);
    assertNotNull("string should not be null", iso);
    SimpleDateFormat sdf = new SimpleDateFormat(Iso8601DateFormat.BASIC_FORMAT);
    Date result = sdf.parse(iso);
    Calendar nowCal = Calendar.getInstance();
    nowCal.setTime(now);
    Calendar resultCal = Calendar.getInstance();
    resultCal.setTime(result);
    assertEquals(
      "YEAR should be equal", nowCal.get(Calendar.YEAR),
      resultCal.get(Calendar.YEAR));
    assertEquals(
      "MONTH should be equal", nowCal.get(Calendar.MONTH),
      resultCal.get(Calendar.MONTH));
    assertEquals(
      "DAY_OF_MONTH should be equal", nowCal.get(Calendar.DAY_OF_MONTH),
      resultCal.get(Calendar.DAY_OF_MONTH));
    assertEquals(
      "HOUR_OF_DAY should be equal", nowCal.get(Calendar.HOUR_OF_DAY),
      resultCal.get(Calendar.HOUR_OF_DAY));
    assertEquals(
      "MINUTE should be equal", nowCal.get(Calendar.MINUTE),
      resultCal.get(Calendar.MINUTE));
    assertEquals(
      "SECOND should be equal", nowCal.get(Calendar.SECOND),
      resultCal.get(Calendar.SECOND));
    assertEquals(
      "TimeZone should be equal", nowCal.getTimeZone(), resultCal.getTimeZone());
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testParseBasicComplete()
    throws Exception
  {
    Iso8601DateFormat idf = new Iso8601DateFormat();
    String toParse =
      year + "" + month + "" + date + "T" + hour + "" + min + "" + sec +
      gmtOffset1;
    Calendar c = idf.parse(toParse);
    this.testEquality(c, year, month, date, hour, min, sec, rawOffset1);
    toParse =
      year + "" + month + "" + date + "T" + hour + "" + min + "" + sec +
      gmtOffset2;
    c = idf.parse(toParse);
    this.testEquality(c, year, month, date, hour, min, sec, rawOffset2);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testParseBasicCompleteTerseTz()
    throws Exception
  {
    Iso8601DateFormat idf = new Iso8601DateFormat();
    String toParse =
      year + "" + month + "" + date + "T" + hour + "" + min + "" + sec +
      gmtOffset1Terse;
    Calendar c = idf.parse(toParse);
    this.testEquality(c, year, month, date, hour, min, sec, rawOffset1);
    toParse =
      year + "" + month + "" + date + "T" + hour + "" + min + "" + sec +
      gmtOffset2Terse;
    c = idf.parse(toParse);
    this.testEquality(c, year, month, date, hour, min, sec, rawOffset2);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testParseBasicYearMonthDateHourMinSec()
    throws Exception
  {
    Iso8601DateFormat idf = new Iso8601DateFormat();
    String toParse =
      year + "" + month + "" + date + "T" + hour + "" + min + "" + sec;
    Calendar c = idf.parse(toParse);
    this.testEquality(c, year, month, date, hour, min, sec, null);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testParseBasicYearMonthDateHourMin()
    throws Exception
  {
    Iso8601DateFormat idf = new Iso8601DateFormat();
    String toParse = year + "" + month + "" + date + "T" + hour + "" + min;
    Calendar c = idf.parse(toParse);
    this.testEquality(c, year, month, date, hour, min, null, null);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testParseBasicYearMonthDateHour()
    throws Exception
  {
    Iso8601DateFormat idf = new Iso8601DateFormat();
    String toParse = year + "" + month + "" + date + "T" + hour;
    Calendar c = idf.parse(toParse);
    this.testEquality(c, year, month, date, hour, null, null, null);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testParseBasicYearMonthDate()
    throws Exception
  {
    Iso8601DateFormat idf = new Iso8601DateFormat();
    String toParse = year + "" + month + "" + date;
    Calendar c = idf.parse(toParse);
    this.testEquality(c, year, month, date, null, null, null, null);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testParseBasicYearMonth()
    throws Exception
  {
    Iso8601DateFormat idf = new Iso8601DateFormat();
    String toParse = year + "" + month;
    Calendar c = idf.parse(toParse);
    this.testEquality(c, year, month, null, null, null, null, null);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testParseBasicYear()
    throws Exception
  {
    Iso8601DateFormat idf = new Iso8601DateFormat();
    String toParse = year + "";
    Calendar c = idf.parse(toParse);
    this.testEquality(c, year, null, null, null, null, null, null);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testParseBasicZulu()
    throws Exception
  {
    Iso8601DateFormat idf = new Iso8601DateFormat();
    String toParse =
      year + "" + month + "" + date + "T" + hour + "" + min + "" + sec + "Z";
    Calendar c = idf.parse(toParse);
    this.testEquality(c, year, month, date, hour, min, sec, new Long(0L));
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testParseExtendedComplete()
    throws Exception
  {
    Iso8601DateFormat idf = new Iso8601DateFormat();
    String toParse =
      year + "-" + month + "-" + date + "T" + hour + ":" + min + ":" + sec +
      gmtOffset1Ext;
    Calendar c = idf.parse(toParse);
    this.testEquality(c, year, month, date, hour, min, sec, rawOffset1);
    toParse =
      year + "-" + month + "-" + date + "T" + hour + ":" + min + ":" + sec +
      gmtOffset2Ext;
    c = idf.parse(toParse);
    this.testEquality(c, year, month, date, hour, min, sec, rawOffset2);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testParseExtendedCompleteTerseTz()
    throws Exception
  {
    Iso8601DateFormat idf = new Iso8601DateFormat();
    String toParse =
      year + "-" + month + "-" + date + "T" + hour + ":" + min + ":" + sec +
      gmtOffset1Terse;
    Calendar c = idf.parse(toParse);
    this.testEquality(c, year, month, date, hour, min, sec, rawOffset1);
    toParse =
      year + "-" + month + "-" + date + "T" + hour + ":" + min + ":" + sec +
      gmtOffset2Terse;
    c = idf.parse(toParse);
    this.testEquality(c, year, month, date, hour, min, sec, rawOffset2);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testParseExtendedZulu()
    throws Exception
  {
    Iso8601DateFormat idf = new Iso8601DateFormat();
    String toParse =
      year + "-" + month + "-" + date + "T" + hour + ":" + min + ":" + sec +
      "Z";
    Calendar c = idf.parse(toParse);
    this.testEquality(c, year, month, date, hour, min, sec, new Long(0L));
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param c DOCUMENTATION PENDING
   * @param year DOCUMENTATION PENDING
   * @param month DOCUMENTATION PENDING
   * @param date DOCUMENTATION PENDING
   * @param hour DOCUMENTATION PENDING
   * @param min DOCUMENTATION PENDING
   * @param sec DOCUMENTATION PENDING
   * @param gmtOffset DOCUMENTATION PENDING
   */
  private void testEquality(
    Calendar c, Integer year, Integer month, Integer date, Integer hour,
    Integer min, Integer sec, Long gmtOffset)
  {
    assertNotNull("Calendar should not be null", c);
    if(gmtOffset != null)
    {
      assertEquals(
        "TimeZone.getRawOffset() should be equal", gmtOffset.longValue(),
        c.getTimeZone().getRawOffset());
    }
    else
    {
      assertEquals(
        "TimeZones should be equal", TimeZone.getDefault(), c.getTimeZone());
    }

    if(sec != null)
    {
      assertEquals(
        "SECOND should be equal", sec.intValue(), c.get(Calendar.SECOND));
    }
    else
    {
      assertEquals("SECOND should be equal", 0, c.get(Calendar.SECOND));
    }

    if(min != null)
    {
      assertEquals(
        "MINUTE should be equal", min.intValue(), c.get(Calendar.MINUTE));
    }
    else
    {
      assertEquals("MINUTE should be equal", 0, c.get(Calendar.MINUTE));
    }

    if(hour != null)
    {
      assertEquals(
        "HOUR_OF_DAY should be equal", hour.intValue(),
        c.get(Calendar.HOUR_OF_DAY));
    }
    else
    {
      assertEquals(
        "HOUR_OF_DAY should be equal", 0, c.get(Calendar.HOUR_OF_DAY));
    }

    if(date != null)
    {
      assertEquals(
        "DAY_OF_MONTH should be equal", date.intValue(),
        c.get(Calendar.DAY_OF_MONTH));
    }
    else
    {
      assertEquals(
        "DAY_OF_MONTH should be equal", 1, c.get(Calendar.DAY_OF_MONTH));
    }

    if(month != null)
    {
      assertEquals( // zero based
        "MONTH should be equal", month.intValue() - 1, c.get(Calendar.MONTH));
    }
    else
    {
      assertEquals( // zero based
        "MONTH should be equal", 0, c.get(Calendar.MONTH));
    }

    if(year != null)
    {
      assertEquals(
        "YEAR should be equal", year.intValue(), c.get(Calendar.YEAR));
    }
  }
}
