package org.sakaiproject.metaobj.shared.mgt.impl;

import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactValidationService;
import org.sakaiproject.metaobj.shared.model.ElementBean;
import org.sakaiproject.metaobj.shared.model.ElementListBean;
import org.sakaiproject.metaobj.shared.model.ValidationError;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.metaobj.utils.xml.NormalizationException;
import org.sakaiproject.metaobj.utils.mvc.intf.FieldValueWrapper;
import org.jdom.Element;
import org.jdom.Attribute;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.text.MessageFormat;

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
    * @param element filled in element to be validated.
    * @return list of ValidationError objects.  If this list is
    * returned empty, then the element validated successfully
    * @see org.sakaiproject.metaobj.shared.model.ValidationError
    */
   public List validate(ElementBean element) {
      List errors = new ArrayList();

      return validate(element, null, errors);
   }

   /**
    * Validate this element from the root.
    * @param element filled in element to be validated.
    * @param parentName this is the name of the parent of this object.
    * All fields that have errors will have this name prepended with a "."
    * @return list of ValidationError objects.  If this list is
    * returned empty, then the element validated successfully
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
               validate((ElementBean)value,
                     composeName(parentName, childSchema.getName()), errors);
               if (childSchema.isDataNode()) {
                  ElementBean bean = (ElementBean)value;
                  value = bean.currentElement().getTextTrim();
                  if (value.toString().length() == 0) {
                     value = null;
                  }
                  validateElement(rootElement, childSchema, value, parentName, errors);
               }
            } else if (value instanceof ElementListBean) {
               boolean found = false;
               for (Iterator iter=((ElementListBean)value).iterator();iter.hasNext();) {
                  found = true;
                  Object currentValue = iter.next();
                  validate((ElementBean)currentValue,
                        composeName(parentName, childSchema.getName()), errors);
                  if (childSchema.isDataNode()) {
                     ElementBean bean = (ElementBean)currentValue;
                     currentValue = bean.currentElement().getTextTrim();
                     if (currentValue.toString().length() == 0) {
                        currentValue = null;
                     }
                     try {
                        validateChildElement(bean.currentElement(),
                           childSchema, currentValue, parentName, errors);
                     }
                      catch (NormalizationException exp) {
                        errors.add(new ValidationError(
                              composeName(parentName, childSchema.getName()),
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
                    errors.add(new ValidationError(
                          composeName(parentName, childSchema.getName()),
                          exp.getErrorCode(),
                          exp.getErrorInfo(),
                          MessageFormat.format(exp.getErrorCode(), exp.getErrorInfo())));
                 }
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
                  errors.add(new ValidationError(
                        composeName(parentName, childSchema.getName()),
                        "required value {0}",
                        new Object[]{childSchema.getName()},
                        MessageFormat.format("required value {0}", new Object[]{childSchema.getName()})));
               }

            } else {
               validateElement(rootElement, childSchema, value, parentName, errors);
            }
         } catch (NormalizationException exp) {
            errors.add(new ValidationError(
                  composeName(parentName, childSchema.getName()),
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
         String stringValue = null;
         if (value != null && value instanceof String) {
            stringValue = (String) value;
            value = childSchema.getActualNormalizedValue(stringValue);
         }

         childElement.setText(childSchema.getSchemaNormalizedValue(value));
      } else if (childSchema.getMinOccurs() > 0) {
         errors.add(new ValidationError(
               composeName(parentName, childSchema.getName()),
               "required value {0}",
               new Object[]{childSchema.getName()},
               MessageFormat.format("required value {0}", new Object[]{childSchema.getName()})));
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
