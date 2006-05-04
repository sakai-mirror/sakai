/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
package org.sakaiproject.metaobj.utils.xml.impl;

import org.jdom.Element;
import org.jdom.Namespace;
import org.sakaiproject.metaobj.utils.xml.NormalizationException;
import org.sakaiproject.metaobj.utils.xml.SchemaInvalidException;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.metaobj.utils.xml.ValueRange;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 17, 2004
 * Time: 3:21:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class DateFormatterElementType extends FormatterElementType {

   private static final String DATE_FORMAT = "yyyy-MM-dd";
   private static final String TIME_FORMAT = "hh:mm:ss";
   private static final String DATE_TIME_FORMAT = DATE_FORMAT + "T" +
         TIME_FORMAT;

   private static final String DATE_TYPE = "xs:date";
   private static final String TIME_TYPE = "xs:time";
   private static final String DATE_TIME_TYPE = "xs:dateTime";

   private Format formatter = null;
   private String format = null;

   private Date maxIncl = null;
   private Date minIncl = null;
   private Date maxExcl = null;
   private Date minExcl = null;

   private ValueRange range = null;

   public DateFormatterElementType(String typeName, Element schemaElement,
                                   SchemaNode parentNode, Namespace xsdNamespace) {
      super(typeName, schemaElement, parentNode, xsdNamespace);

      if (typeName.equals(DATE_TYPE)) {
         format = DATE_FORMAT;
      }
      else if (typeName.equals(TIME_TYPE)) {
         format = TIME_FORMAT;
      }
      else if (typeName.equals(DATE_TIME_TYPE)) {
         format = DATE_TIME_FORMAT;
      }
      else {
         throw new IllegalArgumentException();
      }

      formatter = new SimpleDateFormat(format);

      Element simpleType = schemaElement.getChild("simpleType", xsdNamespace);
      if (simpleType != null) {
         Element restrictions = simpleType.getChild("restriction", xsdNamespace);
         if (restrictions != null) {
            // process restrictions
            try {
               maxIncl = (Date) getFormattedRestriction(restrictions, "maxInclusive", xsdNamespace);
               minIncl = (Date) getFormattedRestriction(restrictions, "minInclusive", xsdNamespace);
               maxExcl = (Date) getFormattedRestriction(restrictions, "maxExclusive", xsdNamespace);
               minExcl = (Date) getFormattedRestriction(restrictions, "minExclusive", xsdNamespace);
            }
            catch (ParseException e) {
               throw new SchemaInvalidException(e);
            }
         }
      }

      if (maxIncl != null || minIncl != null ||
            maxExcl != null || minExcl != null) {
         // one must not be null, create a range
         Comparable min = minIncl;
         if (min == null) {
            min = minExcl;
         }

         Comparable max = maxIncl;
         if (max == null) {
            max = maxExcl;
         }

         range = new ValueRange(max, min, maxIncl != null, minIncl != null);
      }
   }

   protected Format getFormatter() {
      return formatter;
   }

   protected Object checkConstraints(Object o) {
      Date date = (Date) o;

      if (maxIncl != null && date.after(maxIncl)) {
         throw new NormalizationException("Invalid date",
               NormalizationException.DATE_AFTER_ERROR_CODE, new Object[]{o, maxIncl});
      }

      if (minIncl != null && date.before(minIncl)) {
         throw new NormalizationException("Invalid date",
               NormalizationException.DATE_BEFORE_ERROR_CODE, new Object[]{o, minIncl});
      }

      if (maxExcl != null && !date.after(maxExcl)) {
         throw new NormalizationException("Invalid date",
               NormalizationException.DATE_TOO_LATE_ERROR_CODE, new Object[]{o, maxExcl});
      }

      if (minExcl != null && !date.before(minExcl)) {
         throw new NormalizationException("Invalid date",
               NormalizationException.DATE_TOO_EARLY_ERROR_CODE, new Object[]{o, minExcl});
      }

      return o;
   }

   protected String parserException(String value, ParseException e) {
      throw new NormalizationException("Invalid date/time",
            NormalizationException.DATE_INVALID_ERROR_CODE, new Object[]{value, format});
   }

   public Class getObjectType() {
      return Date.class;
   }

   public ValueRange getRange() {
      return range;
   }
}
