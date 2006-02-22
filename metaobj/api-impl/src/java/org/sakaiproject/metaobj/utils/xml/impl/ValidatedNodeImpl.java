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
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.metaobj.utils.xml.ValidatedNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 15, 2004
 * Time: 12:10:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class ValidatedNodeImpl implements ValidatedNode {

   private SchemaNode parentSchema;
   private Element currentElement;
   private List currentErrors = new ArrayList();
   private List children = new ArrayList();
   private Object normalizedValue;

   public ValidatedNodeImpl(SchemaNode parentSchema,
                            Element currentElement) {

      this.parentSchema = parentSchema;
      this.currentElement = currentElement;
   }


   /**
    * Get the schema responsible for this node.
    *
    * @return
    */
   public SchemaNode getSchema() {
      return parentSchema;
   }

   /**
    * Get the named child node as a validated node
    *
    * @param elementName
    * @return
    */
   public ValidatedNode getChild(String elementName) {

      for (Iterator i = children.iterator(); i.hasNext();) {
         ValidatedNode currentNode = (ValidatedNode) i.next();

         if (elementName.equals(currentNode.getElement().getName())) {
            return currentNode;
         }
      }

      return null;
   }

   /**
    * Get all the direct children of this node as
    * a list of ValidatedNode objects
    *
    * @return
    */
   public List getChildren() {
      return children;
   }

   /**
    * Get all the named direct children of this node
    * as a list of ValidatedNode objects.
    *
    * @param elementName
    * @return
    */
   public List getChildren(String elementName) {

      List namedList = new ArrayList();

      for (Iterator i = children.iterator(); i.hasNext();) {
         ValidatedNode currentNode = (ValidatedNode) i.next();

         if (elementName.equals(currentNode.getElement().getName())) {
            namedList.add(currentNode);
         }
      }

      return namedList;
   }

   /**
    * Get the normalized value of this element as an object.
    * Note: in the case of complex nodes, this could return
    * a List of ValidatedNode objects (the children of this node)
    *
    * @return
    */
   public Object getNormalizedValue() {
      return normalizedValue;
   }

   public void setNormalizedValue(Object normalizedValue) {
      this.normalizedValue = normalizedValue;
   }

   /**
    * The errors associated with this node if any or null if the
    * node validated completely.
    *
    * @return
    */
   public List getErrors() {
      return currentErrors;
   }

   /**
    * This returnes the element associated with this node.
    *
    * @return
    */
   public Element getElement() {
      return currentElement;
   }
}
