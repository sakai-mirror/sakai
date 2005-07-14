/*
 * The Open Source Portfolio Initiative Software is Licensed under the Educational Community License Version 1.0:
 *
 * This Educational Community License (the "License") applies to any original work of authorship
 * (the "Original Work") whose owner (the "Licensor") has placed the following notice immediately
 * following the copyright notice for the Original Work:
 *
 * Copyright (c) 2004 Trustees of Indiana University and r-smart Corporation
 *
 * This Original Work, including software, source code, documents, or other related items, is being
 * provided by the copyright holder(s) subject to the terms of the Educational Community License.
 * By obtaining, using and/or copying this Original Work, you agree that you have read, understand,
 * and will comply with the following terms and conditions of the Educational Community License:
 *
 * Permission to use, copy, modify, merge, publish, distribute, and sublicense this Original Work and
 * its documentation, with or without modification, for any purpose, and without fee or royalty to the
 * copyright holder(s) is hereby granted, provided that you include the following on ALL copies of the
 * Original Work or portions thereof, including modifications or derivatives, that you make:
 *
 * - The full text of the Educational Community License in a location viewable to users of the
 * redistributed or derivative work.
 *
 * - Any pre-existing intellectual property disclaimers, notices, or terms and conditions.
 *
 * - Notice of any changes or modifications to the Original Work, including the date the changes were made.
 *
 * - Any modifications of the Original Work must be distributed in such a manner as to avoid any confusion
 *  with the Original Work of the copyright holders.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) may NOT be used in advertising or publicity pertaining
 * to the Original or Derivative Works without specific, written prior permission. Title to copyright
 * in the Original Work and any associated documentation will at all times remain with the copyright holders.
 *
 * $URL$
 * $Revision$
 * $Date$
 */
package org.sakaiproject.metaobj.shared.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.sakaiproject.metaobj.utils.mvc.intf.FieldValueWrapper;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateBean implements FieldValueWrapper {
   protected final Log logger = LogFactory.getLog(getClass());
   private String month = "";
   private String year = "";
   private String day = "";
   private String hour = "";
   private String minute = "";
   private String second = "";
   boolean nullFlag = true;

   public DateBean() {
   }

   public DateBean(Date date) {
      if (date != null){
         setBackingDate(date);
      }
   }

   public void setBackingDate(Date date) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      setYear("" + cal.get(Calendar.YEAR));
      setMonth("" + (cal.get(Calendar.MONTH) + 1));
      setDay("" + cal.get(Calendar.DATE));
      setHour("" + cal.get(Calendar.HOUR));
      setMinute("" + cal.get(Calendar.MINUTE));
      setSecond("" + cal.get(Calendar.SECOND));
      nullFlag = false;
   }

   public String getMonth() {
      return month;
   }

   public void setMonth(String month) {
      this.month = month;
      checkFlag(month);
   }

   public String getYear() {
      return year;
   }

   public void setYear(String year) {
      this.year = year;
      checkFlag(year);
   }

   public String getDay() {
      return day;
   }

   public void setDay(String day) {
      this.day = day;
      checkFlag(day);
   }

   public String getHour() {
      return hour;
   }

   public void setHour(String hour) {
      this.hour = hour;
      checkFlag(hour);
   }

   public String getMinute() {
      return minute;
   }

   public void setMinute(String minute) {
      this.minute = minute;
      checkFlag(minute);
   }

   public String getSecond() {
      return second;
   }

   public void setSecond(String second) {
      this.second = second;
      checkFlag(second);
   }

   public void setValue(Object value) {
      setBackingDate((Date) value);
      nullFlag = (value == null);
   }

   public Object getValue() {
      return getDate();
   }

   public void validate(Errors errors) {
      if (nullFlag) {
         return;
      }

      try {
         Integer.parseInt(getYear());
      } catch (NumberFormatException e) {
         errors.rejectValue("year", "invalid year {0}", new Object[]{getYear()},
            MessageFormat.format("invalid year {0}", new Object[]{getYear()}));
      }
      try {
         Integer.parseInt(getMonth());
      } catch (NumberFormatException e) {
         errors.rejectValue("month", "invalid month {0}", new Object[]{getYear()},
            MessageFormat.format("invalid month {0}", new Object[]{getYear()}));
      }
      try {
         Integer.parseInt(getDay());
      } catch (NumberFormatException e) {
         errors.rejectValue("day", "invalid day {0}", new Object[]{getYear()},
            MessageFormat.format("invalid day {0}", new Object[]{getYear()}));
      }

      /*
      try {
         Integer.parseInt(getHour());
      } catch (NumberFormatException e) {
         ValidationError error = new ValidationError("invalid hour: {0}",
               new Object[]{getHour()});
         errors.add(error);
      }
      try {
         Integer.parseInt(getMinute());
      } catch (NumberFormatException e) {
         ValidationError error = new ValidationError("invalid minute: {0}",
               new Object[]{getMinute()});
         errors.add(error);
      }
      try {
         Integer.parseInt(getSecond());
      } catch (NumberFormatException e) {
         ValidationError error = new ValidationError("invalid second: {0}",
               new Object[]{getSecond()});
         errors.add(error);
      }
      */
   }

   public Date getDate() {
      if (nullFlag) {
         return null;
      }

      return new GregorianCalendar(getValue(getYear()),
         getValue(getMonth()) - 1, // month is zero indexed
         getValue(getDay()),
         getValue(getHour()),
         getValue(getMinute()),
         getValue(getSecond())).getTime();
   }

   protected int getValue(String value) {
      try {
         return Integer.parseInt(value);
      } catch (NumberFormatException e) {
         return 0;
      }
   }

   protected void checkFlag(String value) {
      nullFlag = (value == null || value.length() == 0);
   }
}
