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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/utils/xml/impl/DateFormatterElementType.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision$
 * $Date$
 */
package org.sakaiproject.metaobj.utils.xml.impl;

import org.jdom.Element;
import org.jdom.Namespace;
import org.sakaiproject.metaobj.utils.xml.NormalizationException;
import org.sakaiproject.metaobj.utils.xml.SchemaInvalidException;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;

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

   public DateFormatterElementType(String typeName, Element schemaElement,
                                   SchemaNode parentNode, Namespace xsdNamespace) {
      super(typeName, schemaElement, parentNode, xsdNamespace);

      if (typeName.equals(DATE_TYPE)) {
         format = DATE_FORMAT;
      } else if (typeName.equals(TIME_TYPE)) {
         format = TIME_FORMAT;
      } else if (typeName.equals(DATE_TIME_TYPE)) {
         format = DATE_TIME_FORMAT;
      } else {
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
            } catch (ParseException e) {
               throw new SchemaInvalidException(e);
            }
         }
      }
   }

   protected Format getFormatter() {
      return formatter;
   }

   protected Object checkConstraints(Object o) {
      Date date = (Date) o;

      if (maxIncl != null && date.after(maxIncl)) {
         throw new NormalizationException("Invalid date",
            "Value {0} must be before {1}", new Object[]{o, maxIncl});
      }

      if (minIncl != null && date.before(minIncl)) {
         throw new NormalizationException("Invalid date",
            "Value {0} must be after {1}", new Object[]{o, minIncl});
      }

      if (maxExcl != null && !date.after(maxExcl)) {
         throw new NormalizationException("Invalid date",
            "Value {0} must be not be after {1}", new Object[]{o, maxExcl});
      }

      if (minExcl != null && !date.before(minExcl)) {
         throw new NormalizationException("Invalid date",
            "Value {0} must be not be before {1}", new Object[]{o, minExcl});
      }

      return o;
   }

   protected String parserException(String value, ParseException e) {
      throw new NormalizationException("Invalid date/time",
         "Value {0} must fit {1}", new Object[]{value, format});
   }

   public Class getObjectType() {
      return Date.class;
   }
}
