/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
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

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 25, 2006
 * Time: 10:33:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class UriElementType extends BaseElementType {

   public UriElementType(String typeName, Element schemaElement, SchemaNode parentNode, Namespace xsdNamespace) {
      super(typeName, schemaElement, parentNode, xsdNamespace);
   }

   public Class getObjectType() {
      return URI.class;
   }

   public Object getActualNormalizedValue(String value) {
      try {
         return new URI(value);
      }
      catch (URISyntaxException e) {
         throw new NormalizationException("Invalid URI", NormalizationException.INVALID_URI, new Object[]{value});
      }
   }

   public String getSchemaNormalizedValue(String value) throws NormalizationException {
      try {
         return new URI(value).toString();
      }
      catch (URISyntaxException e) {
         throw new NormalizationException("Invalid URI", NormalizationException.INVALID_URI, new Object[]{value});
      }
   }

   public String getSchemaNormalizedValue(Object value) throws NormalizationException {
      if (value != null) {
         return ((URI) value).toString();
      }
      else {
         return null;
      }
   }

}
