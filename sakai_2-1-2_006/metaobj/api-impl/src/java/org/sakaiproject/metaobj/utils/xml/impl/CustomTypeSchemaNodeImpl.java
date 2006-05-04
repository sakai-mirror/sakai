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
import org.sakaiproject.metaobj.utils.xml.SchemaNode;

import java.util.List;
import java.util.Map;

public class CustomTypeSchemaNodeImpl extends RefSchemaNodeImpl {
   protected final Log logger = LogFactory.getLog(getClass());
   private String name = null;
   private Map localGlobalElements;
   private String refName;
   private boolean isAttribute = false;

   public CustomTypeSchemaNodeImpl(Element schemaElement,
                                   GlobalMaps globalMaps, String type,
                                   boolean isAttribute) throws SchemaInvalidException {
      // use the type to look up the real SchemaNode
      super(type, schemaElement, globalMaps);
      refName = type;
      localGlobalElements = globalMaps.globalCustomTypes;
      name = schemaElement.getAttributeValue("name");
      this.isAttribute = isAttribute;
   }

   public String getName() {
      return name;
   }

   protected SchemaNode getActualNode() {
      return (SchemaNode) localGlobalElements.get(refName);
   }

   public boolean isAttribute() {
      return isAttribute;
   }

   public List getEnumeration() {
      return getActualNode().getEnumeration();
   }
}
