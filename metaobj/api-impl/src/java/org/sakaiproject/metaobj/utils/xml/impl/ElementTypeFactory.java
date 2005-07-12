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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/utils/xml/impl/ElementTypeFactory.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision$
 * $Date$
 */
package org.sakaiproject.metaobj.utils.xml.impl;

import org.jdom.Element;
import org.jdom.Namespace;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 15, 2004
 * Time: 6:02:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ElementTypeFactory {

   private static ElementTypeFactory instance = new ElementTypeFactory();

   private static final String INT_TYPES = "" +
      "        xs:byte" + //A signed 8-bit integer \n" +
      "        xs:int" + //A signed 32-bit integer \n" +
      "        xs:integer" + //An integer value \n" +
      "        xs:long" + //A signed 64-bit integer \n" +
      "        xs:negativeInteger" + //An integer containing only negative values ( .., -2, -1.) \n" +
      "        xs:nonNegativeInteger" + //An integer containing only non-negative values (0, 1, 2, ..) \n" +
      "        xs:nonPositiveInteger" + //An integer containing only non-positive values (.., -2, -1, 0) \n" +
      "        xs:positiveInteger" + //An integer containing only positive values (1, 2, ..) \n" +
      "        xs:short" + //A signed 16-bit integer \n" +
      "        xs:unsignedLong" + //An unsigned 64-bit integer \n" +
      "        xs:unsignedInt" + //An unsigned 32-bit integer \n" +
      "        xs:unsignedShort" + //An unsigned 16-bit integer \n" +
      "        xs:unsignedByte"; //An unsigned 8-bit integer";

   private static final String DECIMAL_TYPES = "" +
      "        xs:decimal";

   private static final String BOOLEAN_TYPES = "" +
      "        xs:boolean";

   private static final String DATE_TYPES = "" +
      "        xs:date" +
      "        xs:time" +
      "        xs:dateTime";

   protected ElementTypeFactory() {

   }

   public static ElementTypeFactory getInstance() {
      return instance;
   }

   public BaseElementType createElementType(Element schemaElement, SchemaNode parentNode,
                                            Namespace xsdNamespace) {
      String typeName = "unknown";

      if (schemaElement.getAttributeValue("type") != null) {
         typeName = schemaElement.getAttributeValue("type");
      } else {
         Element simpleType = schemaElement.getChild("simpleType", xsdNamespace);
         if (simpleType == null) {
            simpleType = schemaElement.getChild("simpleContent", xsdNamespace);
         }

         if (simpleType != null) {
            Element restrictions = simpleType.getChild("restriction", xsdNamespace);
            if (restrictions != null) {
               typeName = restrictions.getAttributeValue("base");
            }
            else {
               typeName = simpleType.getChild("extension", xsdNamespace).getAttributeValue("base");
            }
         }
      }

      if (INT_TYPES.indexOf(typeName) != -1) {
         return new IntegerElementType(typeName, schemaElement,
            parentNode, xsdNamespace).postInit(xsdNamespace);
      } else if (DECIMAL_TYPES.indexOf(typeName) != -1) {
         return new DecimalElementType(typeName, schemaElement,
            parentNode, xsdNamespace).postInit(xsdNamespace);
      } else if (BOOLEAN_TYPES.indexOf(typeName) != -1) {
         return new BooleanElementType(typeName, schemaElement,
            parentNode, xsdNamespace).postInit(xsdNamespace);
      } else if (DATE_TYPES.indexOf(typeName) != -1) {
         return new DateFormatterElementType(typeName, schemaElement,
            parentNode, xsdNamespace).postInit(xsdNamespace);
      }

      // default to BaseElementType
      return new BaseElementType(typeName, schemaElement, parentNode, xsdNamespace).postInit(xsdNamespace);
   }

}
