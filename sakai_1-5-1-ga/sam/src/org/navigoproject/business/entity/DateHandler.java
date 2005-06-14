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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * Title: NavigoProject.org
 * </p>
 *
 * <p>
 * Description: AAM - Date Class handling the Date funcionality
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 *
 * <p>
 * Company: Stanford University
 * </p>
 *
 * @author Durairaju Madhu
 * @version 1.0
 */
public class DateHandler
{
  private Integer[] dayArray =
  {
    new Integer(1), new Integer(2), new Integer(3), new Integer(4),
    new Integer(5), new Integer(6), new Integer(7), new Integer(8),
    new Integer(9), new Integer(10), new Integer(11), new Integer(12),
    new Integer(13), new Integer(14), new Integer(15), new Integer(16),
    new Integer(17), new Integer(18), new Integer(19), new Integer(20),
    new Integer(21), new Integer(22), new Integer(23), new Integer(24),
    new Integer(25), new Integer(26), new Integer(27), new Integer(28),
    new Integer(29), new Integer(30), new Integer(31)
  };
  private Integer[] yearArray = { new Integer(2003), new Integer(2004) };
  private Integer[] monthArray =
  {
    new Integer(1), new Integer(2), new Integer(3), new Integer(4),
    new Integer(5), new Integer(6), new Integer(7), new Integer(8),
    new Integer(9), new Integer(10), new Integer(11), new Integer(12)
  };
  private String[] hourArray =
  {
    new String("01"), new String("02"), new String("03"), new String("04"),
    new String("05"), new String("06"), new String("07"), new String("08"),
    new String("09"), new String("10"), new String("11"), new String("12")
  };
  private String[] minArray =
  {
    new String("00"), new String("05"), new String("10"), new String("15"),
    new String("20"), new String("25"), new String("30"), new String("35"),
    new String("40"), new String("45"), new String("50"), new String("55")
  };
  private String[] ampmArray = { new String("AM"), new String("PM") };
  private List day = Arrays.asList(dayArray);
  private List month = Arrays.asList(monthArray);
  private List year = Arrays.asList(yearArray);
  private List hour = Arrays.asList(hourArray);
  private List min = Arrays.asList(minArray);
  private List ampm = Arrays.asList(ampmArray);
  private LabelValue[] wmonthArray =
  {
    new LabelValue("January", "1"), new LabelValue("February", "2"),
    new LabelValue("March", "3"), new LabelValue("April", "4"),
    new LabelValue("May", "5"), new LabelValue("June", "6"),
    new LabelValue("July", "7"), new LabelValue("August", "8"),
    new LabelValue("September", "9"), new LabelValue("October", "10"),
    new LabelValue("November", "11"), new LabelValue("December", "12")
  };
  private List wmonth = Arrays.asList(wmonthArray);

  /**
   * Creates a new DateHandler object.
   */
  public DateHandler()
  {
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getDay()
  {
    return (Collection) day;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getMonth()
  {
    return (Collection) month;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getYear()
  {
    return (Collection) year;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getHour()
  {
    return (Collection) hour;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getMin()
  {
    return (Collection) min;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getAmPm()
  {
    return (Collection) ampm;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getWmonth()
  {
    return (Collection) wmonth;
  }
}
