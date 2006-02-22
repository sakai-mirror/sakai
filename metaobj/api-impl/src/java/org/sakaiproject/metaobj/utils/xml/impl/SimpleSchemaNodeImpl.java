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

import org.jdom.Attribute;
import org.jdom.Element;
import org.sakaiproject.metaobj.utils.xml.*;

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
         validatedNode.getErrors().add(new ValidationError(validatedNode, "not an attribute", new Object[]{}));
      }
      return type.validateAndNormalize(node);
   }

   public String getSchemaNormalizedValue(Object value) throws NormalizationException {
      if (value instanceof String) {
         return type.getSchemaNormalizedValue((String) value);
      }
      else {
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

   public ElementType getType() {
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
