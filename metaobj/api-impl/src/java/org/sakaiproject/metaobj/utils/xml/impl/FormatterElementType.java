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
package org.sakaiproject.metaobj.utils.xml.impl;

import org.jdom.Element;
import org.jdom.Namespace;
import org.sakaiproject.metaobj.utils.xml.NormalizationException;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;

import java.text.Format;
import java.text.ParseException;


/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 16, 2004
 * Time: 2:48:13 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class FormatterElementType extends BaseElementType {

   public FormatterElementType(String typeName, Element schemaElement, SchemaNode parentNode,
                               Namespace xsdNamespace) {

      super(typeName, schemaElement, parentNode, xsdNamespace);
   }

   protected abstract Format getFormatter();

   protected abstract Object checkConstraints(Object o);

   public String getSchemaNormalizedValue(Object value) throws NormalizationException {

      if (value == null) {
         return null;
      }

      if (!getObjectType().isInstance(value)) {
         throw new NormalizationException("Invalid object type",
            "Object {0} should be class {1}",
            new Object[]{value, getObjectType()});
      }

      value = checkConstraints(value);

      try {
         return getFormatter().format(value);
      } catch (Exception e) {
         throw new NormalizationException(e);
      }
   }


   public String getSchemaNormalizedValue(String value) throws NormalizationException {

      if (value == null) {
         return null;
      }

      try {
         return getFormatter().format(checkConstraints(getFormatter().parseObject(value)));
      } catch (ParseException e) {
         return parserException(value, e);
      } catch (Exception e) {
         throw new NormalizationException(e);
      }
   }

   protected Object getFormattedRestriction(Element restrictions, String name,
                                            Namespace xsdNamespace) throws ParseException {
      String value = processStringRestriction(restrictions, name, xsdNamespace);

      if (value == null) {
         return null;
      }

      return getFormatter().parseObject(value);
   }

   protected abstract String parserException(String value, ParseException e);

   public Object getActualNormalizedValue(String value) {
      try {
         return checkConstraints(getFormatter().parseObject(value));
      } catch (ParseException e) {
         return parserException(value, e);
      }
   }

   public abstract Class getObjectType();

}
