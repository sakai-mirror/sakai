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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/utils/xml/impl/SimpleSchemaNodeImpl.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision$
 * $Date$
 */
package org.sakaiproject.metaobj.utils.xml.impl;

import org.jdom.Attribute;
import org.jdom.Element;
import org.sakaiproject.metaobj.utils.xml.NormalizationException;
import org.sakaiproject.metaobj.utils.xml.SchemaInvalidException;
import org.sakaiproject.metaobj.utils.xml.ValidatedNode;
import org.sakaiproject.metaobj.utils.xml.ValidationError;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 15, 2004
 * Time: 3:47:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleSchemaNodeImpl extends SchemaNodeImpl {

   private BaseElementType type;
   private boolean isAttribute = false;

   /*
   public SimpleSchemaNodeImpl(Element schemaElement, GlobalMaps globalMaps)
      throws SchemaInvalidException {

      super(schemaElement, globalMaps);
   }
   */


   public SimpleSchemaNodeImpl(Element schemaElement, GlobalMaps globalMaps, boolean isAttribute)
      throws SchemaInvalidException {

      super(schemaElement, globalMaps);
      this.isAttribute = isAttribute;
   }


   protected void initSchemaElement() {
      super.initSchemaElement();
      type = ElementTypeFactory.getInstance().createElementType(getSchemaElement(),
         this, xsdNamespace);
   }

   /**
    * Validates the passed in node and all children.
    * Will also normalize any values.
    *
    * @param node a jdom element to validate
    * @return the validated Element wrapped
    *         in a ValidatedNode class
    */
   public ValidatedNode validateAndNormalize(Element node) {
      return type.validateAndNormalize(node);
   }

   public ValidatedNode validateAndNormalize(Attribute node) {
      ValidatedNodeImpl validatedNode =
         new ValidatedNodeImpl(this, null);

      if (!isAttribute()) {
         validatedNode.getErrors().add(
            new ValidationError(validatedNode, "not an attribute", new Object[]{}));
      }
      return type.validateAndNormalize(node);
   }

   public String getSchemaNormalizedValue(Object value) throws NormalizationException {
      if (value instanceof String) {
         return type.getSchemaNormalizedValue((String) value);
      } else {
         return type.getSchemaNormalizedValue(value);
      }
   }

   public Object getActualNormalizedValue(String value) throws NormalizationException {
      return type.getActualNormalizedValue(value);
   }

   public Class getObjectType() {
      return type.getObjectType();
   }

   public int getMaxLength() {
      return type.maxLength;
   }

   public BaseElementType getType() {
      return type;
   }

   public List getEnumeration() {
      return type.getEnumeration();
   }

   public boolean isAttribute() {
      return isAttribute;
   }

   public boolean isDataNode() {
      return true;
   }

}
