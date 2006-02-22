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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.sakaiproject.metaobj.utils.xml.SchemaInvalidException;

public class RefAttributeSchemaNodeImpl extends RefSchemaNodeImpl {
   protected final Log logger = LogFactory.getLog(getClass());
   private boolean required = false;

   public RefAttributeSchemaNodeImpl(String refName, Element schemaElement,
                                     SchemaNodeImpl.GlobalMaps globalMaps) throws SchemaInvalidException {
      super(refName, schemaElement, globalMaps);
      required = "required".equals(getSchemaElement().getAttributeValue("use"));
   }

   /**
    * Retuns the max number of times the element
    * defined by this node can occur in its parent.
    * The root schema will always return 1 here.
    *
    * @return
    */
   public int getMaxOccurs() {
      return 1;
   }

   /**
    * Returns the min number of times the element
    * defined by this node can occur in its parent.
    * The root schema will always return 1 here.
    *
    * @return
    */
   public int getMinOccurs() {
      return required ? 1 : 0;
   }

   public boolean isAttribute() {
      return true;
   }
}
