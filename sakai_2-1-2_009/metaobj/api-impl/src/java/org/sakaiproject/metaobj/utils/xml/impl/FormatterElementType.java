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
               NormalizationException.INVALID_TYPE_ERROR_CODE,
               new Object[]{value, getObjectType()});
      }

      value = checkConstraints(value);

      try {
         return getFormatter().format(value);
      }
      catch (Exception e) {
         throw new NormalizationException(e);
      }
   }


   public String getSchemaNormalizedValue(String value) throws NormalizationException {

      if (value == null) {
         return null;
      }

      try {
         return getFormatter().format(checkConstraints(getFormatter().parseObject(value)));
      }
      catch (ParseException e) {
         return parserException(value, e);
      }
      catch (Exception e) {
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
      }
      catch (ParseException e) {
         return parserException(value, e);
      }
   }

   public abstract Class getObjectType();

}
