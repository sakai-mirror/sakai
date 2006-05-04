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

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 27, 2004
 * Time: 6:31:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class BooleanElementType extends BaseElementType {


   public BooleanElementType(String typeName, Element schemaElement,
                             SchemaNode parentNode, Namespace xsdNamespace) {
      super(typeName, schemaElement, parentNode, xsdNamespace);
   }

   public String getSchemaNormalizedValue(Object value) throws NormalizationException {
      //CWM check pattern?
      
      if (getEnumeration() != null && !getEnumeration().contains(value)) {
         //throw new NormalizationException("Invalid string pattern",
         //      "Value {0} must match {1}", new Object[]{value, getEnumeration()});
         throw new NormalizationException("Required field",
               NormalizationException.REQIRED_FIELD_ERROR_CODE, new Object[0]);
      }
      return value.toString();
   }

   public String getSchemaNormalizedValue(String value) throws NormalizationException {
      return new Boolean(value).toString();
   }

   public Class getObjectType() {
      return Boolean.class;
   }

   public Object getActualNormalizedValue(String value) {
      return new Boolean(value);
   }
}
