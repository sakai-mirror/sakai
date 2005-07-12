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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/utils/xml/impl/IntegerElementType.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
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
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 16, 2004
 * Time: 4:05:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class IntegerElementType extends FormatterElementType {

   protected Number maxIncl = null;
   protected Number minIncl = null;
   protected Number maxExcl = null;
   protected Number minExcl = null;
   protected int totalDigits = -1;

   private Format format = null;

   public IntegerElementType(String typeName, Element schemaElement, SchemaNode parentNode, Namespace xsdNamespace) {
      super(typeName, schemaElement, parentNode, xsdNamespace);

      format = NumberFormat.getIntegerInstance();

      totalDigits = (new String(Long.MAX_VALUE + "")).length();

      Element simpleType = schemaElement.getChild("simpleType", xsdNamespace);
      if (simpleType != null) {
         Element restrictions = simpleType.getChild("restriction", xsdNamespace);
         if (restrictions != null) {
            // process restrictions
            try {
               maxIncl = (Number) getFormattedRestriction(restrictions, "maxInclusive", xsdNamespace);
               minIncl = (Number) getFormattedRestriction(restrictions, "minInclusive", xsdNamespace);
               maxExcl = (Number) getFormattedRestriction(restrictions, "maxExclusive", xsdNamespace);
               minExcl = (Number) getFormattedRestriction(restrictions, "minExclusive", xsdNamespace);
            } catch (ParseException e) {
               throw new SchemaInvalidException(e);
            }

            totalDigits = processIntRestriction(restrictions, "totalDigits", xsdNamespace, totalDigits);
         }
      }
   }

   protected Format getFormatter() {
      return format;
   }

   protected Object checkConstraints(Object o) {

      int i = ((Number) o).intValue();

      if (maxIncl != null && i > maxIncl.intValue()) {
         throw new NormalizationException("Invalid number",
            "Value {0} must be less than or equal to {1}", new Object[]{o, maxIncl});
      }

      if (minIncl != null && i < minIncl.intValue()) {
         throw new NormalizationException("Invalid number",
            "Value {0} must be more than or equal {1}", new Object[]{o, minIncl});
      }

      if (maxExcl != null && i >= maxExcl.intValue()) {
         throw new NormalizationException("Invalid number",
            "Value {0} must be less than {1}", new Object[]{o, maxExcl});
      }

      if (minExcl != null && i <= minExcl.intValue()) {
         throw new NormalizationException("Invalid number",
            "Value {0} must be more than {1}", new Object[]{o, minExcl});
      }

      if (totalDigits != -1 && o.toString().length() > totalDigits) {
         throw new NormalizationException("Invalid number",
            "Value {0} must be less than {1} digits", new Object[]{o, new Integer(totalDigits)});
      }

      return o;
   }

   protected String parserException(String value, ParseException e) {
      throw new NormalizationException("Invalid number",
         "Value {0} must be a number", new Object[]{value});
   }

   public Class getObjectType() {
      return Number.class;
   }

   public int getMaxLength() {
      return totalDigits;
   }

}
