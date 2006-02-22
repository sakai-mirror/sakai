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

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 15, 2004
 * Time: 3:47:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class ComplexSchemaNodeImpl extends SchemaNodeImpl {

   private String[] childrenElements;
   private Map childrenMap;

   private String[] childrenAttributes;
   private Map childrenAttributeMap;

   private String[] attributeGroupNames;

   private boolean orderDependant = false;
   private boolean attributeGroupsSetup = false;

   private SchemaNode extensionType;

   public ComplexSchemaNodeImpl(Element schemaElement, GlobalMaps globalMaps) throws SchemaInvalidException {
      super(schemaElement, globalMaps);
   }

   protected void initSchemaElement() {
      super.initSchemaElement();
      childrenMap = new Hashtable();
      childrenAttributeMap = new Hashtable();

      Element sequenceElement = null;
      Element attributeParentElement = null;

      Element complexTypeElement =
            getSchemaElement().getChild("complexType", xsdNamespace);

      attributeParentElement = complexTypeElement;

      sequenceElement = complexTypeElement.getChild("sequence", xsdNamespace);

      if (sequenceElement == null) {
         sequenceElement = complexTypeElement.getChild("choice", xsdNamespace);
      }

      if (sequenceElement == null) {
         Element content = complexTypeElement.getChild("complexContent", xsdNamespace);

         if (content == null) {
            content = complexTypeElement.getChild("simpleContent", xsdNamespace);
         }

         Element extension = content.getChild("extension", xsdNamespace);
         sequenceElement = extension.getChild("sequence", xsdNamespace);
         attributeParentElement = extension;

         String baseType = extension.getAttributeValue("base");

         if (baseType.startsWith(xsdNamespace.getPrefix())) {
            extensionType = new SimpleSchemaNodeImpl(complexTypeElement, getGlobalMaps(), false);
         }
         else {
            extensionType = new CustomTypeSchemaNodeImpl(complexTypeElement, getGlobalMaps(), baseType, false);
         }
      }

      if (sequenceElement != null) {
         processSequence(sequenceElement);
      }
      else {
         childrenElements = new String[0];
      }

      processAttributes(attributeParentElement.getChildren("attribute", xsdNamespace));

      processAttributeGroups(attributeParentElement.getChildren("attributeGroup", xsdNamespace));
   }

   private void processAttributeGroups(List childList) {

      attributeGroupNames = new String[childList.size()];

      for (int i = 0; i < attributeGroupNames.length; i++) {
         Element currentElement = (Element) childList.get(i);

         attributeGroupNames[i] = currentElement.getAttributeValue("ref");
      }

   }

   protected void processAttributes(List childList) {
      childrenAttributes = new String[childList.size()];

      for (int i = 0; i < childrenAttributes.length; i++) {
         Element currentElement = (Element) childList.get(i);

         childrenAttributes[i] = currentElement.getAttributeValue("name");

         if (childrenAttributes[i] == null) {
            childrenAttributes[i] = currentElement.getAttributeValue("ref");
         }

         childrenAttributeMap.put(childrenAttributes[i],
               createNode(currentElement, true));
      }
   }

   protected void processSequence(Element sequenceElement) {
      List childList = sequenceElement.getChildren("element", xsdNamespace);

      childrenElements = new String[childList.size()];

      for (int i = 0; i < childrenElements.length; i++) {
         Element currentElement = (Element) childList.get(i);

         childrenElements[i] = currentElement.getAttributeValue("name");

         if (childrenElements[i] == null) {
            childrenElements[i] = currentElement.getAttributeValue("ref");
         }

         childrenMap.put(childrenElements[i],
               createNode(currentElement));
      }

      // looking for sequence or all
      if (sequenceElement.getName().equals("sequence")) {
         orderDependant = true;
      }
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

      setupAttributeGroups();

      ValidatedNodeImpl validatedNode =
            new ValidatedNodeImpl(this, node);

      if (orderDependant) {
         // todo add ordering check here

      }

      for (int i = 0; i < childrenElements.length; i++) {
         SchemaNode currentSchemaNode =
               (SchemaNode) childrenMap.get(childrenElements[i]);

         int actualNumberOfElements = node.getChildren(childrenElements[i]).size();

         if (actualNumberOfElements == 0 && currentSchemaNode.getMinOccurs() == 1) {

            ValidatedNodeImpl validatedChildNode =
                  new ValidatedNodeImpl(currentSchemaNode, null);

            validatedChildNode.getErrors().add(new ValidationError(validatedChildNode, "Required field: {0}",
                  new Object[]{childrenElements[i]}));
            validatedNode.getChildren().add(validatedChildNode);
         }
         else if (actualNumberOfElements > currentSchemaNode.getMaxOccurs() &&
               currentSchemaNode.getMaxOccurs() != -1) {

            ValidatedNodeImpl validatedChildNode =
                  new ValidatedNodeImpl(currentSchemaNode, null);

            validatedChildNode.getErrors().add(new ValidationError(validatedChildNode, "Too many elements {0}, {1}",
                  new Object[]{childrenElements[i],
                               new Integer(getMaxOccurs())}));
            validatedNode.getChildren().add(validatedChildNode);
         }
         else if (actualNumberOfElements < currentSchemaNode.getMinOccurs()) {

            ValidatedNodeImpl validatedChildNode =
                  new ValidatedNodeImpl(currentSchemaNode, null);

            validatedChildNode.getErrors().add(new ValidationError(validatedChildNode, "Too few elements {0}, {1}",
                  new Object[]{childrenElements[i],
                               new Integer(getMinOccurs())}));
            validatedNode.getChildren().add(validatedChildNode);
         }
      }

      List children = node.getChildren();

      for (Iterator i = children.iterator(); i.hasNext();) {
         Element currentElement = (Element) i.next();

         SchemaNode currentSchemaNode = (SchemaNode) childrenMap.get(currentElement.getName());

         if (currentSchemaNode == null) {
            validatedNode.getErrors().add(new ValidationError("Unkown node {0}",
                  new Object[]{currentElement.getName()}));
         }
         else {
            validatedNode.getChildren().add(currentSchemaNode.validateAndNormalize(currentElement));
         }
      }

      children = node.getAttributes();

      for (Iterator i = children.iterator(); i.hasNext();) {
         Attribute currentAttribute = (Attribute) i.next();

         SchemaNode currentSchemaNode = (SchemaNode) childrenAttributeMap.get(currentAttribute.getName());

         if (currentSchemaNode == null) {
            validatedNode.getErrors().add(new ValidationError("Unkown node {0}",
                  new Object[]{currentAttribute.getName()}));
         }
         else {
            validatedNode.getChildren().add(currentSchemaNode.validateAndNormalize(currentAttribute));
         }
      }

      return validatedNode;
   }

   protected synchronized void setupAttributeGroups() {
      if (attributeGroupsSetup) {
         return;
      }
      attributeGroupsSetup = true;
      List newAttributes = new ArrayList();

      for (int i = 0; i < attributeGroupNames.length; i++) {
         SchemaNode[] nodes = (SchemaNode[]) getGlobalMaps().globalAttributeGroups.get(attributeGroupNames[i]);

         for (int j = 0; j < nodes.length; j++) {
            newAttributes.add(nodes[j]);
         }
      }

      String[] newAttributeNames = new String[childrenAttributes.length + newAttributes.size()];

      System.arraycopy(childrenAttributes, 0, newAttributeNames, 0, childrenAttributes.length);

      int index = childrenAttributes.length;

      for (Iterator i = newAttributes.iterator(); i.hasNext();) {
         SchemaNode node = (SchemaNode) i.next();
         newAttributeNames[index] = node.getName();
         childrenAttributeMap.put(node.getName(), node);
         index++;
      }

      childrenAttributes = newAttributeNames;
   }

   /**
    * Gets the schema object for the named child node.
    *
    * @param elementName the name of the schema node to retrive.
    * @return
    */
   public SchemaNode getChild(String elementName) {
      if (childrenMap.get(elementName) != null) {
         return (SchemaNode) childrenMap.get(elementName);
      }
      else if (this.getClass().isInstance(extensionType)) {
         return extensionType.getChild(elementName);
      }

      setupAttributeGroups();
      return (SchemaNode) childrenAttributeMap.get(elementName);
   }


   public String getSchemaNormalizedValue(Object value) throws NormalizationException {
      if (extensionType != null) {
         return extensionType.getSchemaNormalizedValue(value);
      }

      throw new UnsupportedOperationException("Cannot call this without this being the document node.");
   }

   public Class getObjectType() {
      if (this.getMaxOccurs() > 1) {
         return List.class;
      }
      else {
         return Map.class;
      }
   }

   public List getChildren() {
      setupAttributeGroups();

      List returnedList = new ArrayList();

      if (extensionType != null && extensionType.getChildren() != null) {
         returnedList.addAll(extensionType.getChildren());
      }

      for (int i = 0; i < childrenElements.length; i++) {
         returnedList.add(childrenMap.get(childrenElements[i]));
      }

      for (int i = 0; i < childrenAttributes.length; i++) {
         returnedList.add(childrenAttributeMap.get(childrenAttributes[i]));
      }

      return returnedList;
   }

   public boolean isDataNode() {
      if (extensionType != null) {
         return extensionType.isDataNode();
      }
      else {
         return false;
      }
   }

   public boolean hasEnumerations() {
      if (extensionType != null) {
         return extensionType.hasEnumerations();
      }
      else {
         return false;
      }
   }

   public List getEnumeration() {
      if (extensionType != null) {
         return extensionType.getEnumeration();
      }
      else {
         return super.getEnumeration();
      }
   }

   public Object getActualNormalizedValue(String value) throws NormalizationException {
      if (extensionType != null) {
         return extensionType.getActualNormalizedValue(value);
      }
      else {
         return super.getActualNormalizedValue(value);
      }
   }

}
