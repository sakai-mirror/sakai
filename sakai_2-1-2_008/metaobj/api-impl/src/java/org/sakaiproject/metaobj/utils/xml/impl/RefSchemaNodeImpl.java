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
import org.sakaiproject.metaobj.utils.xml.*;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 15, 2004
 * Time: 6:53:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class RefSchemaNodeImpl extends SchemaNodeImpl {

   private String refName = null;
   private Map localGlobalElements;

   public RefSchemaNodeImpl(String refName, Element schemaElement,
                            GlobalMaps globalMaps) throws SchemaInvalidException {
      super(schemaElement, globalMaps);
      localGlobalElements = globalMaps.globalElements;
      this.refName = refName;
   }

   protected SchemaNode getActualNode() {
      return (SchemaNode) localGlobalElements.get(refName);
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
      return getActualNode().validateAndNormalize(node);
   }

   /**
    * Gets the schema object for the named child node.
    *
    * @param elementName the name of the schema node to retrive.
    * @return
    */
   public SchemaNode getChild(String elementName) {
      return getActualNode().getChild(elementName);
   }

   public String getSchemaNormalizedValue(Object value) throws NormalizationException {
      return getActualNode().getSchemaNormalizedValue(value);
   }

   public Object getActualNormalizedValue(String value) throws NormalizationException {
      return getActualNode().getActualNormalizedValue(value);
   }

   public List getChildren() {
      return getActualNode().getChildren();
   }

   public ElementType getType() {
      if (getActualNode() instanceof SimpleSchemaNodeImpl) {
         return ((SimpleSchemaNodeImpl) getActualNode()).getType();
      }

      return null;
   }

   public String getName() {
      return getActualNode().getName();
   }

   public Class getObjectType() {
      return getActualNode().getObjectType();
   }

   public List getEnumeration() {
      return getActualNode().getEnumeration();
   }

   public boolean isDataNode() {
      return getActualNode().isDataNode();
   }

}
