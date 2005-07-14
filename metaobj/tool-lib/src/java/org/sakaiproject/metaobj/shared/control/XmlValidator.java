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
 * $URL: /opt/CVS/osp2.x/HomesTool/src/java/org/theospi/metaobj/shared/control/XmlValidator.java,v 1.1 2005/06/30 17:34:21 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/30 17:34:21 $
 */
package org.sakaiproject.metaobj.shared.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Attribute;
import org.jdom.Element;
import org.springframework.validation.Errors;
import org.sakaiproject.metaobj.shared.model.ElementBean;
import org.sakaiproject.metaobj.shared.model.ElementListBean;
//import org.sakaiproject.metaobj.repository.control.FileNameValidator;
import org.sakaiproject.metaobj.utils.mvc.impl.ValidatorBase;
import org.sakaiproject.metaobj.utils.mvc.intf.FieldValueWrapper;
import org.sakaiproject.metaobj.utils.xml.NormalizationException;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;

public class XmlValidator extends ValidatorBase {
   protected final Log logger = LogFactory.getLog(getClass());
   protected String parentPrefix = "";
   //private FileNameValidator fileNameValidator;

   public XmlValidator() {
   }

   public XmlValidator(String parentPrefix) {
      this.parentPrefix = parentPrefix;
   }

   public boolean supports(Class clazz) {
      return (ElementBean.class.isAssignableFrom(clazz));
   }

   public void validate(Object obj, Errors errors) {
      validate(obj, errors, false);
   }

   public void validate(Object obj, Errors errors, boolean checkListNumbers) {
      ElementBean elementBean = (ElementBean) obj;

      validateDisplayName(elementBean, errors);

      // go through all nodes... call validate on the nodes that are elements...
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
               pushNestedPath(childSchema.getName() + ".", errors);
               validate(value, errors, checkListNumbers);
               popNestedPath(errors);
               if (childSchema.isDataNode()) {
                  ElementBean bean = (ElementBean)value;
                  value = bean.currentElement().getTextTrim();
                  if (value.toString().length() == 0) {
                     value = null;
                  }
                  validateElement(rootElement, childSchema, value, errors);
               }
            } else if (value instanceof ElementListBean) {
               boolean found = false;
               for (Iterator iter=((ElementListBean)value).iterator();iter.hasNext();) {
                  found = true;
                  Object currentValue = iter.next();
                  validate(currentValue, errors, checkListNumbers);
                  if (childSchema.isDataNode()) {
                     ElementBean bean = (ElementBean)currentValue;
                     currentValue = bean.currentElement().getTextTrim();
                     if (currentValue.toString().length() == 0) {
                        currentValue = null;
                     }
                     try {
                        validateChildElement(bean.currentElement(),
                           childSchema, currentValue, errors);
                     }
                      catch (NormalizationException exp) {
                        errors.rejectValue(childSchema.getName(),
                           exp.getErrorCode(),
                           exp.getErrorInfo(),
                           MessageFormat.format(exp.getErrorCode(), exp.getErrorInfo()));
                     }
                  }
               }
               if (!found && checkListNumbers) {
                  validateChildElement(null,
                     childSchema, null, errors);
               }
            } else if (childSchema.isAttribute()) {
               Attribute childAttribute = rootElement.getAttribute(childSchema.getName());

               if (childAttribute != null) {
                  String stringValue = null;
                  if (value != null && value instanceof String) {
                     stringValue = (String) value;
                     value = childSchema.getActualNormalizedValue(stringValue);
                  }

                  childAttribute.setValue(childSchema.getSchemaNormalizedValue(value));
               } else if (childSchema.getMinOccurs() > 0) {
                  errors.rejectValue(childSchema.getName(),
                     "required value {0}",
                     new Object[]{childSchema.getName()},
                     MessageFormat.format("required value {0}", new Object[]{childSchema.getName()}));
               }

            } else {
               validateElement(rootElement, childSchema, value, errors);
            }
         } catch (NormalizationException exp) {
            errors.rejectValue(childSchema.getName(),
               exp.getErrorCode(),
               exp.getErrorInfo(),
               MessageFormat.format(exp.getErrorCode(), exp.getErrorInfo()));
         }
      }
   }

   protected void validateDisplayName(ElementBean elementBean, Errors errors) {
      // don't care about display name here
   }

   protected void validateElement(Element rootElement, SchemaNode childSchema,
                                  Object value, Errors errors) {
      validateChildElement(rootElement.getChild(childSchema.getName()),
         childSchema, value, errors);
   }


   protected void validateChildElement(Element childElement, SchemaNode childSchema,
                                     Object value, Errors errors) {
      if (childElement != null) {
         String stringValue = null;
         if (value != null && value instanceof String) {
            stringValue = (String) value;
            value = childSchema.getActualNormalizedValue(stringValue);
         }

         childElement.setText(childSchema.getSchemaNormalizedValue(value));
      } else if (childSchema.getMinOccurs() > 0) {
         errors.rejectValue(childSchema.getName(),
            "required value {0}",
            new Object[]{childSchema.getName()},
            MessageFormat.format("required value {0}", new Object[]{childSchema.getName()}));
      }
   }

   protected boolean checkWrappedField(SchemaNode childSchema, ElementBean elementBean, Errors errors) {

      Class childClass = elementBean.getType(childSchema.getName());
      Object value = elementBean.get(childSchema.getName());

      if (!(value instanceof FieldValueWrapper)) {
         return false;
      }

      FieldValueWrapper beanValue = (FieldValueWrapper) elementBean.get(childSchema.getName());

      if (beanValue.getValue() == null) {
         if (childSchema.getMinOccurs() > 0) {
            throw new NormalizationException("Required field", "required field {0}",
               new Object[]{childSchema.getName()});
         } else {
            elementBean.getBaseElement().removeChild(childSchema.getName());
            return true;
         }
      }

      // check date...
      this.pushNestedPath(childSchema.getName(), errors);
      beanValue.validate(errors);
      this.popNestedPath(errors);

      Element dateElement = elementBean.getBaseElement().getChild(childSchema.getName());

      if (dateElement == null) {
         dateElement = new Element(childSchema.getName());
         elementBean.getBaseElement().addContent(dateElement);
      }
      dateElement.setText(childSchema.getSchemaNormalizedValue(beanValue.getValue()));

      return true;
   }

//   public FileNameValidator getFileNameValidator() {
//      return fileNameValidator;
//   }
//
//   public void setFileNameValidator(FileNameValidator fileNameValidator) {
//      this.fileNameValidator = fileNameValidator;
//   }
}
