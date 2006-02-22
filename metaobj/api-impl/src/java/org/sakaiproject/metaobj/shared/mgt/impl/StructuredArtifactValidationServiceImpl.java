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
package org.sakaiproject.metaobj.shared.mgt.impl;

import org.jdom.Attribute;
import org.jdom.Element;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactValidationService;
import org.sakaiproject.metaobj.shared.model.ElementBean;
import org.sakaiproject.metaobj.shared.model.ElementListBean;
import org.sakaiproject.metaobj.shared.model.ValidationError;
import org.sakaiproject.metaobj.utils.mvc.intf.FieldValueWrapper;
import org.sakaiproject.metaobj.utils.xml.NormalizationException;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Aug 18, 2005
 * Time: 3:07:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class StructuredArtifactValidationServiceImpl implements StructuredArtifactValidationService {

   /**
    * Validate this element from the root.
    *
    * @param element filled in element to be validated.
    * @return list of ValidationError objects.  If this list is
    *         returned empty, then the element validated successfully
    * @see org.sakaiproject.metaobj.shared.model.ValidationError
    */
   public List validate(ElementBean element) {
      List errors = new ArrayList();

      return validate(element, null, errors);
   }

   /**
    * Validate this element from the root.
    *
    * @param element    filled in element to be validated.
    * @param parentName this is the name of the parent of this object.
    *                   All fields that have errors will have this name prepended with a "."
    * @return list of ValidationError objects.  If this list is
    *         returned empty, then the element validated successfully
    * @see org.sakaiproject.metaobj.shared.model.ValidationError
    */
   public List validate(ElementBean element, String parentName) {
      List errors = new ArrayList();

      return validate(element, parentName, errors);
   }

   protected List validate(ElementBean elementBean, String parentName, List errors) {
      SchemaNode currentNode = elementBean.getCurrentSchema();
      Element rootElement = elementBean.currentElement();

      List children = currentNode.getChildren();
      for (Iterator i = children.iterator(); i.hasNext();) {
         SchemaNode childSchema = (SchemaNode) i.next();

         try {
            if (checkWrappedField(childSchema, elementBean, errors)) {
               continue;
            }

            Object value = elementBean.get(childSchema.getName());

            if (value instanceof ElementBean) {
               validate((ElementBean) value,
                     composeName(parentName, childSchema.getName()), errors);
               if (childSchema.isDataNode()) {
                  ElementBean bean = (ElementBean) value;
                  value = bean.currentElement().getTextTrim();
                  if (value.toString().length() == 0) {
                     value = null;
                  }
                  validateElement(rootElement, childSchema, value, parentName, errors);
               }
            }
            else if (value instanceof ElementListBean) {
               boolean found = false;
               for (Iterator iter = ((ElementListBean) value).iterator(); iter.hasNext();) {
                  found = true;
                  Object currentValue = iter.next();
                  validate((ElementBean) currentValue,
                        composeName(parentName, childSchema.getName()), errors);
                  if (childSchema.isDataNode()) {
                     ElementBean bean = (ElementBean) currentValue;
                     currentValue = bean.currentElement().getTextTrim();
                     if (currentValue.toString().length() == 0) {
                        currentValue = null;
                     }
                     try {
                        validateChildElement(bean.currentElement(),
                              childSchema, currentValue, parentName, errors);
                     }
                     catch (NormalizationException exp) {
                        errors.add(new ValidationError(composeName(parentName, childSchema.getName()),
                              exp.getErrorCode(),
                              exp.getErrorInfo(),
                              MessageFormat.format(exp.getErrorCode(), exp.getErrorInfo())));
                     }
                  }
               }
               if (!found) {
                  try {
                     validateChildElement(null,
                           childSchema, null, parentName, errors);
                  }
                  catch (NormalizationException exp) {
                     errors.add(new ValidationError(composeName(parentName, childSchema.getName()),
                           exp.getErrorCode(),
                           exp.getErrorInfo(),
                           MessageFormat.format(exp.getErrorCode(), exp.getErrorInfo())));
                  }
               }
            }
            else if (childSchema.isAttribute()) {
               Attribute childAttribute = rootElement.getAttribute(childSchema.getName());

               if (childAttribute != null) {
                  String stringValue = null;
                  if (value != null && value instanceof String) {
                     stringValue = (String) value;
                     value = childSchema.getActualNormalizedValue(stringValue);
                  }

                  childAttribute.setValue(childSchema.getSchemaNormalizedValue(value));
               }
               else if (childSchema.getMinOccurs() > 0) {
                  errors.add(new ValidationError(composeName(parentName, childSchema.getName()),
                        NormalizationException.REQIRED_FIELD_ERROR_CODE,
                        new Object[0], NormalizationException.REQIRED_FIELD_ERROR_CODE));
               }

            }
            else {
               validateElement(rootElement, childSchema, value, parentName, errors);
            }
         }
         catch (NormalizationException exp) {
            errors.add(new ValidationError(composeName(parentName, childSchema.getName()),
                  exp.getErrorCode(),
                  exp.getErrorInfo(),
                  MessageFormat.format(exp.getErrorCode(), exp.getErrorInfo())));
         }
      }

      return errors;
   }

   protected void validateChildElement(Element childElement, SchemaNode childSchema,
                                       Object value, String parentName, List errors) {
      if (childElement != null) {
         if (value instanceof FieldValueWrapper) {
            value = ((FieldValueWrapper) value).getValue();
         }

         String stringValue = null;
         if (value != null && value instanceof String) {
            stringValue = (String) value;
            value = childSchema.getActualNormalizedValue(stringValue);
         }

         childElement.setText(childSchema.getSchemaNormalizedValue(value));
      }
      else if (childSchema.getMinOccurs() > 0) {
         errors.add(new ValidationError(composeName(parentName, childSchema.getName()),
               NormalizationException.REQIRED_FIELD_ERROR_CODE,
               new Object[0], NormalizationException.REQIRED_FIELD_ERROR_CODE));
      }
   }

   protected void validateElement(Element rootElement, SchemaNode childSchema,
                                  Object value, String parentName, List errors) {
      validateChildElement(rootElement.getChild(childSchema.getName()),
            childSchema, value, parentName, errors);
   }

   protected String composeName(String parentName, String name) {
      if (parentName == null) {
         return name;
      }
      else {
         return parentName + "." + name;
      }
   }

   protected boolean checkWrappedField(SchemaNode childSchema, ElementBean elementBean, List errors) {
      return false;
   }
}
