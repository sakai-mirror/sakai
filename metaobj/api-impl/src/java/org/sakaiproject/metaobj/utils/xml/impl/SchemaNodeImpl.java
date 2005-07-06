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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/utils/xml/impl/SchemaNodeImpl.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/29 18:36:41 $
 */
package org.sakaiproject.metaobj.utils.xml.impl;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.sakaiproject.metaobj.utils.xml.NormalizationException;
import org.sakaiproject.metaobj.utils.xml.SchemaFactory;
import org.sakaiproject.metaobj.utils.xml.SchemaInvalidException;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.metaobj.utils.xml.ValidatedNode;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 15, 2004
 * Time: 12:21:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class SchemaNodeImpl implements SchemaNode {

   protected Namespace xsdNamespace =
      Namespace.getNamespace("xs", "http://www.w3.org/2001/XMLSchema");

   private SchemaFactory factory = null;
   private Object path = null;
   private Element schemaElement;

   private GlobalMaps globalMaps;

   private Map globalElements;
   private Map globalCustomTypes;

   private Map documentAnnotations;
   private Map appAnnotations;

   private boolean documentNode = false;
   private int maxOccurs = 1;
   private int minOccurs = 1;
   private String elementName = "document";
   private Namespace targetNamespace;

   public SchemaNodeImpl(Element schemaElement, GlobalMaps globalMaps)
      throws SchemaInvalidException {

      this.globalElements = globalMaps.globalElements;
      this.globalCustomTypes = globalMaps.globalCustomTypes;
      this.globalMaps = globalMaps;
      this.schemaElement = schemaElement;
      elementName = schemaElement.getAttributeValue("name");

      setupNamespaces(schemaElement);

      initSchemaElement();
   }

   public SchemaNodeImpl(Document shemaDoc, SchemaFactory factory, Object path)
      throws SchemaInvalidException {

      this.factory = factory;
      this.path = path;

      globalMaps = new GlobalMaps();

      globalElements = new Hashtable();
      globalMaps.globalElements = globalElements;
      globalCustomTypes = new Hashtable();
      globalMaps.globalCustomTypes = globalCustomTypes;
      documentNode = true;

      Element rootElement = shemaDoc.getRootElement();
      schemaElement = rootElement;

      setupNamespaces(schemaElement);

      processAnnotations(rootElement.getChild("annotation", xsdNamespace));
      processIncludes(rootElement.getChildren("include", xsdNamespace));

      List rootSchemaElements =
         rootElement.getChildren("element", xsdNamespace);

      for (Iterator i = rootSchemaElements.iterator(); i.hasNext();) {
         Element schemaElement = (Element) i.next();

         globalElements.put(schemaElement.getAttributeValue("name"),
            createNode(schemaElement));
      }

      rootSchemaElements =
         rootElement.getChildren("attribute", xsdNamespace);

      for (Iterator i = rootSchemaElements.iterator(); i.hasNext();) {
         Element schemaElement = (Element) i.next();

         globalElements.put(schemaElement.getAttributeValue("name"),
            createNode(schemaElement, true));
      }

      rootSchemaElements =
         rootElement.getChildren("attributeGroup", xsdNamespace);

      for (Iterator i = rootSchemaElements.iterator(); i.hasNext();) {
         Element schemaElement = (Element) i.next();

         processAttributeGroup(schemaElement);
      }

      List rootTypes =
         rootElement.getChildren("complexType", xsdNamespace);

      for (Iterator i = rootTypes.iterator(); i.hasNext();) {
         Element schemaElement = (Element) i.next();

         globalCustomTypes.put(schemaElement.getAttributeValue("name"),
            createTypeNode(schemaElement));
      }
      
      rootTypes =
         rootElement.getChildren("simpleType", xsdNamespace);

      for (Iterator i = rootTypes.iterator(); i.hasNext();) {
         Element schemaElement = (Element) i.next();

         globalCustomTypes.put(schemaElement.getAttributeValue("name"),
            createTypeNode(schemaElement));
      }
   }

   protected void setupNamespaces(Element schemaElement) {

      Element rootElement = null;
      if (schemaElement.getDocument() == null) {
         rootElement = schemaElement;
      }
      else {
         rootElement = schemaElement.getDocument().getRootElement();
      }

      xsdNamespace = rootElement.getNamespace();

      if (rootElement.getAttribute("targetNamespace") != null) {
         targetNamespace = Namespace.getNamespace(rootElement.getAttributeValue("targetNamespace"));
      }
      else {
         targetNamespace = Namespace.NO_NAMESPACE;
      }

   }

   protected void processAttributeGroup(Element groupElement) {
      List attributes = groupElement.getChildren("attribute", xsdNamespace);
      SchemaNode[] attributeGroup = new SchemaNode[attributes.size()];

      for (int i=0;i<attributeGroup.length;i++) {
         attributeGroup[i] = createNode((Element)attributes.get(i),
            true);
      }

      getGlobalMaps().globalAttributeGroups.put(groupElement.getAttributeValue("name"),
         attributeGroup);
   }

   protected void processIncludes(List includes) {
      for (Iterator i=includes.iterator();i.hasNext();) {
         Element include = (Element)i.next();
         SchemaNodeImpl includedSchema = (SchemaNodeImpl)factory.getRelativeSchema(
            include.getAttributeValue("schemaLocation"), path);
         GlobalMaps maps = includedSchema.getGlobalMaps();
         this.getGlobalMaps().globalCustomTypes.putAll(maps.globalCustomTypes);
         this.getGlobalMaps().globalElements.putAll(maps.globalElements);
         this.getGlobalMaps().globalAttributeGroups.putAll(maps.globalAttributeGroups);
      }
   }

   protected SchemaNode createTypeNode(Element schemaElement) {

      Element fakeRoot = new Element("element", xsdNamespace);
      fakeRoot.setAttribute("name", schemaElement.getAttributeValue("name"));

      if (getTargetNamespace() != Namespace.NO_NAMESPACE) {
         fakeRoot.setAttribute("targetNamespace", getTargetNamespace().getURI());
      }

      fakeRoot.addContent((Element)schemaElement.clone());
      if (schemaElement.getName().equals("complexType")) {
         return new ComplexSchemaNodeImpl(fakeRoot, globalMaps);
      } else {
         return new SimpleSchemaNodeImpl(fakeRoot, globalMaps, false);
      }
   }

   protected void processAnnotations(Element child) {
      documentAnnotations = new Hashtable();
      appAnnotations = new Hashtable();

      if (child == null) {
         return;
      }

      processAnnotationList(appAnnotations, child.getChildren("appinfo", xsdNamespace));
      processAnnotationList(documentAnnotations, child.getChildren("documentation", xsdNamespace));
   }

   protected void processAnnotationList(Map annotationMap, List children) {
      for (Iterator i = children.iterator(); i.hasNext();) {
         Element elem = (Element) i.next();
         if (elem.getAttribute("source") != null) {
            annotationMap.put(elem.getAttributeValue("source"),
               elem.getText());
         }
      }
   }

   protected void initSchemaElement() {

      processAnnotations(schemaElement.getChild("annotation", xsdNamespace));

      if (schemaElement.getAttribute("maxOccurs") != null) {
         String maxOccursValue = schemaElement.getAttributeValue("maxOccurs");

         if (maxOccursValue.equals("unbounded")) {
            maxOccurs = -1;
         } else {
            maxOccurs = Integer.parseInt(maxOccursValue);
         }
      }

      if (schemaElement.getAttribute("minOccurs") != null) {
         String minOccursValue = schemaElement.getAttributeValue("minOccurs");
         minOccurs = Integer.parseInt(minOccursValue);
      }
   }

   protected SchemaNode createNode(Element schemaElement) {
      return createNode(schemaElement, false);
   }

   protected SchemaNode createNode(Element schemaElement, boolean isAttribute) {
      if (schemaElement.getAttribute("ref") != null) {
         if (isAttribute) {
            return new RefAttributeSchemaNodeImpl(schemaElement.getAttributeValue("ref"),
            schemaElement, globalMaps);
         } else {
            return new RefSchemaNodeImpl(schemaElement.getAttributeValue("ref"),
               schemaElement, globalMaps);
         }
      } else if (schemaElement.getAttribute("type") != null &&
         !schemaElement.getAttributeValue("type").startsWith(xsdNamespace.getPrefix())) {
         return new CustomTypeSchemaNodeImpl(schemaElement, globalMaps,
            schemaElement.getAttributeValue("type"), isAttribute);
      } else if (schemaElement.getChild("complexType", xsdNamespace) != null) {
         return new ComplexSchemaNodeImpl(schemaElement, globalMaps);
      } else if (isAttribute) {
         return new AttributeSchemaNodeImpl(schemaElement, globalMaps, isAttribute);
      }
      else {
         return new SimpleSchemaNodeImpl(schemaElement, globalMaps, isAttribute);
      }
   }


   public Namespace getTargetNamespace() {
      return targetNamespace;
   }

   public void setTargetNamespace(Namespace targetNamespace) {
      this.targetNamespace = targetNamespace;
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

      if (documentNode) {
         SchemaNode schemaNode = (SchemaNode) globalElements.get(node.getName());

         return schemaNode.validateAndNormalize(node);
      }

      throw new UnsupportedOperationException("Cannot call this without this being the document node.");
   }

   public ValidatedNode validateAndNormalize(Attribute node) {
      throw new UnsupportedOperationException("Cannot call this without this being an attribute node.");
   }

   /**
    * Gets the schema object for the named child node.
    *
    * @param elementName the name of the schema node to retrive.
    * @return
    */
   public SchemaNode getChild(String elementName) {
      if (documentNode) {
         return (SchemaNode) globalElements.get(elementName);
      }

      return null;
   }

   public Collection getRootChildren(){
      if (documentNode) {
         return globalElements.keySet();
      }

      throw new UnsupportedOperationException("Cannot call this without this being the document node.");
   }

   /**
    * Retuns the max number of times the element
    * defined by this node can occur in its parent.
    * The root schema will always return 1 here.
    *
    * @return
    */
   public int getMaxOccurs() {
      return maxOccurs;
   }

   /**
    * Returns the min number of times the element
    * defined by this node can occur in its parent.
    * The root schema will always return 1 here.
    *
    * @return
    */
   public int getMinOccurs() {
      return minOccurs;
   }


   public String getSchemaNormalizedValue(Object value) throws NormalizationException {
      throw new UnsupportedOperationException("Cannot call this without this being the document node.");
   }

   public Object getActualNormalizedValue(String value) throws NormalizationException {
      throw new UnsupportedOperationException("Cannot call this without this being the document node.");
   }

//   public Object getActualNormalizedValue(String value) throws NormalizationException {
//      throw new UnsupportedOperationException("Cannot call this without this being the document node.");
//   }

   public String getName() {
      return elementName;
   }

   public Class getObjectType() {
      return Map.class;
   }

   public List getChildren() {
      return new ArrayList();
   }

   public String getDocumentAnnotation(String source) {
      return (String) getDocumentAnnotations().get(source);
   }

   public String getAppAnnotation(String source) {
      return (String) getAppAnnotations().get(source);
   }

   public Map getDocumentAnnotations() {
      return documentAnnotations;
   }

   public Map getAppAnnotations() {
      return appAnnotations;
   }

   public List getEnumeration() {
      return null;
   }

   public boolean hasEnumerations() {
      return (getEnumeration() != null);
   }

   public boolean isAttribute() {
      return false;
   }

   public boolean isDataNode() {
      return false;
   }

   public Element getSchemaElement() {
      return schemaElement;
   }

   public GlobalMaps getGlobalMaps() {
      return globalMaps;
   }

   protected class GlobalMaps {
      public Map globalElements = new Hashtable();
      public Map globalCustomTypes = new Hashtable();
      public Map globalAttributeGroups = new Hashtable();
   }


}
