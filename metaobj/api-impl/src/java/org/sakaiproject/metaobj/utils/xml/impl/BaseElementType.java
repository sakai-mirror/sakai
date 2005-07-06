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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/utils/xml/impl/BaseElementType.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/29 18:36:41 $
 */
package org.sakaiproject.metaobj.utils.xml.impl;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.sakaiproject.metaobj.utils.xml.NormalizationException;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.metaobj.utils.xml.ValidatedNode;
import org.sakaiproject.metaobj.utils.xml.ValidationError;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 15, 2004
 * Time: 5:07:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseElementType {

   protected static final int WHITE_SPACE_TYPE_NONE = 0;
   protected static final int WHITE_SPACE_TYPE_PRESERVE = 1;
   // replace with spaces
   protected static final int WHITE_SPACE_TYPE_REPLACE = 2;
   // remove all whitespace and replace with spaces
   protected static final int WHITE_SPACE_TYPE_COLLAPSE = 3;

   protected int length = -1;
   protected int maxLength = -1;
   protected int minLength = -1;
   protected Pattern pattern = null;
   protected int whiteSpaceType = WHITE_SPACE_TYPE_NONE;
   protected String defaultValue = "";
   protected String fixedValue = "";
   protected Element schemaElement;
   protected SchemaNode parentNode = null;
   private List enumeration = null;

   public BaseElementType(String typeName, Element schemaElement, SchemaNode parentNode,
                          Namespace xsdNamespace) {

      this.schemaElement = schemaElement;
      this.parentNode = parentNode;
      init(xsdNamespace);
   }

   protected void init(Namespace xsdNamespace) {
      Element simpleType = schemaElement.getChild("simpleType", xsdNamespace);
      if (simpleType != null) {
         Element restrictions = simpleType.getChild("restriction", xsdNamespace);
         if (restrictions != null) {
            // process restrictions
            length = processIntRestriction(restrictions, "length", xsdNamespace, length);
            maxLength = processIntRestriction(restrictions, "maxLength", xsdNamespace, maxLength);
            minLength = processIntRestriction(restrictions, "minLength", xsdNamespace, minLength);

            String patternValue = processStringRestriction(restrictions, "pattern", xsdNamespace);

            if (patternValue != null) {
               pattern = Pattern.compile(patternValue);
            }

            String whiteSpaceValue = processStringRestriction(restrictions, "whiteSpace", xsdNamespace);
            if (whiteSpaceValue != null) {
               if (whiteSpaceValue.equals("preserve")) {
                  whiteSpaceType = WHITE_SPACE_TYPE_PRESERVE;
               }
               if (whiteSpaceValue.equals("replace")) {
                  whiteSpaceType = WHITE_SPACE_TYPE_REPLACE;
               }
               if (whiteSpaceValue.equals("collapse")) {
                  whiteSpaceType = WHITE_SPACE_TYPE_COLLAPSE;
               }
            }
         }
      }

      if (schemaElement.getAttribute("default") != null) {
         defaultValue = schemaElement.getAttributeValue("default");
      }
      if (schemaElement.getAttribute("fixed") != null) {
         fixedValue = schemaElement.getAttributeValue("fixed");
         defaultValue = fixedValue;
      }
   }

   protected void processEnumerations(Element restrictions, Namespace xsdNamespace) {
      List enums = restrictions.getChildren("enumeration", xsdNamespace);
      List temp = null;
      enumeration = null;

      if (enums.size() > 0) {
         temp = new ArrayList();
      }

      for (Iterator i = enums.iterator(); i.hasNext();) {
         Element enum = (Element) i.next();
         String value = enum.getAttributeValue("value");
         temp.add(getActualNormalizedValue(value));
      }

      enumeration = temp;
   }

   protected String processStringRestriction(Element restrictions, String s, Namespace xsdNamespace) {
      Element currentRestriction = restrictions.getChild(s, xsdNamespace);

      if (currentRestriction == null) {
         return null;
      }

      return currentRestriction.getAttributeValue("value");
   }

   protected int processIntRestriction(Element restrictions, String s, Namespace xsdNamespace, int defaultValue) {
      String value = processStringRestriction(restrictions, s, xsdNamespace);

      if (value == null) {
         return defaultValue;
      }

      return Integer.parseInt(value);
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
      ValidatedNodeImpl validatedNode =
         new ValidatedNodeImpl(parentNode, node);

      String value = node.getText();

      try {
         value = getSchemaNormalizedValue(value);
         node.setText(value);
         validatedNode.setNormalizedValue(getActualNormalizedValue(value));

         if (value == null || value.length() == 0) {
            return null;
         }
      } catch (NormalizationException exp) {
         validatedNode.getErrors().add(new ValidationError(validatedNode, exp.getErrorCode(), exp.getErrorInfo()));
      }

      return validatedNode;
   }

   public String getSchemaNormalizedValue(Object value) throws NormalizationException {

      if (value == null) {
         return null;
      }

      return getSchemaNormalizedValue(value.toString());
   }

   public String getSchemaNormalizedValue(String value) throws NormalizationException {
      if (value == null) {
         return defaultValue;
      }

      if (fixedValue != null && fixedValue.length() > 0) {
         return fixedValue;
      }

      String startingValue = handleWhiteSpace(value.toString());

      int valueLength = startingValue.length();

      if (length != -1 && valueLength != length) {
         throw new NormalizationException("Invalid string length",
            "Value {0} must be {1} characters", new Object[]{startingValue, new Integer(length)});
      }

      if (maxLength != -1 && valueLength > maxLength) {
         throw new NormalizationException("Invalid string length",
            "Value {0} must be less than {1} characters", new Object[]{startingValue, new Integer(maxLength)});
      }

      if (minLength != -1 && valueLength < minLength && minLength == 1) {
         throw new NormalizationException("Required field",
            "required field", new Object[0]);
      }

      if (minLength != -1 && valueLength < minLength) {
         throw new NormalizationException("Invalid string length",
            "Value {0} must be at least {1} characters", new Object[]{startingValue, new Integer(minLength)});
      }

      if (pattern != null && !pattern.matcher(startingValue).matches()) {
         throw new NormalizationException("Invalid string pattern",
            "Value {0} must match {1}", new Object[]{startingValue, pattern.pattern()});
      }

      return startingValue;
   }

   private String handleWhiteSpace(String s) {
      if (whiteSpaceType == WHITE_SPACE_TYPE_NONE ||
         whiteSpaceType == WHITE_SPACE_TYPE_PRESERVE) {
         return s;
      }

      if (whiteSpaceType == WHITE_SPACE_TYPE_REPLACE) {
         s = s.replaceAll("\\s", " ");
      } else if (whiteSpaceType == WHITE_SPACE_TYPE_COLLAPSE) {
         s = s.replaceAll("\\s+", " ");
      }

      return s;
   }

   public Class getObjectType() {
      return String.class;
   }

   public Object getActualNormalizedValue(String value) {
      Object returned = getSchemaNormalizedValue(value);

      if (enumeration != null) {
         if (!enumeration.contains(returned)) {
            throw new NormalizationException("Not enumerated",
               "Value {0} must be in enueration", new Object[]{returned});
         }
      }

      return returned;
   }


   public int getLength() {
      return length;
   }

   public int getMaxLength() {
      return maxLength;
   }

   public int getMinLength() {
      return minLength;
   }

   public Pattern getPattern() {
      return pattern;
   }

   public int getWhiteSpaceType() {
      return whiteSpaceType;
   }

   public String getDefaultValue() {
      return defaultValue;
   }

   public String getFixedValue() {
      return fixedValue;
   }

   public List getEnumeration() {
      return enumeration;
   }

   public BaseElementType postInit(Namespace xsdNamespace) {
      Element simpleType = schemaElement.getChild("simpleType", xsdNamespace);
      if (simpleType != null) {
         Element restrictions = simpleType.getChild("restriction", xsdNamespace);

         if (restrictions != null) {
            processEnumerations(restrictions, xsdNamespace);
         }
      }

      return this;
   }

   public ValidatedNode validateAndNormalize(Attribute node) {
      ValidatedNodeImpl validatedNode =
         new ValidatedNodeImpl(parentNode, null);

      String value = node.getValue();

      try {
         value = getSchemaNormalizedValue(value);
         node.setValue(value);
         validatedNode.setNormalizedValue(getActualNormalizedValue(value));

         if (value == null || value.length() == 0) {
            return null;
         }
      } catch (NormalizationException exp) {
         validatedNode.getErrors().add(new ValidationError(validatedNode, exp.getErrorCode(), exp.getErrorInfo()));
      }

      return validatedNode;
   }

}
