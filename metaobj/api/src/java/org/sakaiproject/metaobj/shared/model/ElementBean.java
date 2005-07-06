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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/shared/model/ElementBean.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/29 18:36:41 $
 */
package org.sakaiproject.metaobj.shared.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Attribute;
import org.jdom.Element;
import org.sakaiproject.metaobj.shared.mgt.FieldValueWrapperFactory;
import org.sakaiproject.metaobj.utils.TypedMap;
import org.sakaiproject.metaobj.utils.mvc.intf.FieldValueWrapper;
import org.sakaiproject.metaobj.utils.xml.NormalizationException;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.api.kernel.component.cover.ComponentManager;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Mar 10, 2004
 * Time: 3:45:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ElementBean extends HashMap implements TypedMap {

   protected final Log logger = LogFactory.getLog(getClass());

   private Element baseElement;
   private Map types = new HashMap();
   private SchemaNode currentSchema;
   private Map wrappedInstances = new HashMap();
   private boolean deferValidation = true;

   public static final String FIELD_DATA_TAG = "FIELD_DATA";

   private static FieldValueWrapperFactory wrapperFactory = null;


   public ElementBean(String elementName, SchemaNode currentSchema) {
      this.currentSchema = currentSchema;
      setBaseElement(new Element(elementName));
   }

   public ElementBean(String elementName, SchemaNode currentSchema, boolean deferValidation) {
      this.deferValidation = deferValidation;
      this.currentSchema = currentSchema;
      setBaseElement(new Element(elementName));
   }

   public ElementBean() {
      setBaseElement(new Element("empty"));
   }

   public ElementBean(Element baseElement, SchemaNode currentSchema, boolean deferValidation) {
      this.deferValidation = deferValidation;
      this.currentSchema = currentSchema;
      setBaseElement(baseElement);
   }

   public ElementBean(Element baseElement, SchemaNode currentSchema, Map wrappedInstances) {
      this.currentSchema = currentSchema;
      this.wrappedInstances = wrappedInstances;
      setBaseElement(baseElement);
   }

   public Element currentElement() {
      return baseElement;
   }

   public SchemaNode getCurrentSchema() {
      return currentSchema;
   }

   public void setCurrentSchema(SchemaNode currentSchema) {
      this.currentSchema = currentSchema;
   }

   public Object put(Object key, Object value) {
      logger.debug("put called with " + key + " and " + value + " on " + baseElement.getName());

      SchemaNode elementSchema = currentSchema.getChild((String) key);

      if (getWrapperFactory().checkWrapper(elementSchema.getObjectType())) {
         return this.wrappedObjectPut(key, value, elementSchema);
      }

      if (elementSchema.getMaxOccurs() == 1) {

         String normalizedValue;

         try {
            normalizedValue = elementSchema.getSchemaNormalizedValue(value);
         } catch (NormalizationException exp) {
            if (deferValidation) {
               normalizedValue = value.toString();
            } else {
               throw exp;
            }
         }

         if (elementSchema.isAttribute()) {
            Attribute oldAttribute = currentElement().getAttribute((String)key);
            if (value != null && value.toString().length() > 0) {
               logger.debug("not removing attribute" + key);
               if (oldAttribute == null) {
                  currentElement().setAttribute(key.toString(), normalizedValue);
               } else {
                  oldAttribute.setValue(normalizedValue);
               }
            } else if (oldAttribute != null) {
               logger.debug("removing attribute" + key);
               currentElement().removeAttribute(key.toString());
            }
         }
         else {
            Element oldElement = currentElement().getChild((String) key);

            if (value != null && value.toString().length() > 0) {
               if (oldElement == null) {
                  Element newElement = new Element((String) key);
                  newElement.addContent(normalizedValue);
                  currentElement().addContent(newElement);
               } else {
                  oldElement.setText(normalizedValue);
               }
            } else if (oldElement != null) {
               currentElement().removeContent(oldElement);
            }
         }
      } else {
         // if you got here, must be a simple element
         ElementListBean listBean = (ElementListBean)get(key);

         while (listBean.size() > 0) {
            listBean.remove(0);
         }

         if (value instanceof String[]) {

            String[] values = (String[])value;

            for (int i=0;i<values.length;i++) {
               if (values[i].length() > 0) {
                  ElementBean bean = listBean.createBlank();
                  bean.getBaseElement().addContent(values[i]);
                  listBean.add(bean);
               }
            }
         }
         else if (value instanceof String) {
            if (value.toString().length() > 0) {
               ElementBean bean = listBean.createBlank();
               bean.getBaseElement().addContent((String)value);
               listBean.add(bean);
            }
         }

      }

      return null;
   }

   protected FieldValueWrapperFactory getWrapperFactory() {
      if (wrapperFactory == null) {
         wrapperFactory = (FieldValueWrapperFactory) ComponentManager.getInstance().get("fieldValueWrapperFactory");
      }
      return wrapperFactory;
   }

   public Object remove(Object key) {
      currentElement().removeChild((String) key);
      types.remove(key);
      return null;
   }


   public Object get(Object key) {
      logger.debug("get called with " + key);

      SchemaNode schema = currentSchema.getChild((String) key);

      if (schema == null) {
         return null;
      }

      if (schema.getMaxOccurs() > 1 || schema.getMaxOccurs() == -1) {
         List childElements = new ArrayList();
         List rawElements = baseElement.getChildren((String) key);
         for (Iterator i = rawElements.iterator(); i.hasNext();) {
            logger.debug("got child");
            childElements.add(new ElementBean((Element) i.next(), schema, deferValidation));
         }

         return new ElementListBean(baseElement, childElements, schema, deferValidation);
      }

      if (getWrapperFactory().checkWrapper(schema.getObjectType())) {
         return this.wrappedObjectGet(key, schema);
      }

      if (schema.isAttribute()) {
         Attribute child = baseElement.getAttribute((String)key);

         if (child == null) {
            return null;
         }
         else {
            try {
               return schema.getActualNormalizedValue(child.getValue());
            } catch (NormalizationException exp) {
               // This should not happen... values should already be validated...
               // just return the text itself...
               return child.getValue();
            }
         }
      }
      else {
         Element child = baseElement.getChild((String) key);

         if (child == null) {
            if (schema.getObjectType().isAssignableFrom(java.util.Map.class)) {
               child = new Element(schema.getName());
               baseElement.addContent(child);
            } else {
               return null;
            }
         }

         logger.debug("returning typed object");

         Class objectClass = schema.getObjectType();

         if (Map.class.isAssignableFrom(objectClass)) {
            return new ElementBean(child, schema, deferValidation);
         } else {
            try {
               return schema.getActualNormalizedValue(child.getText());
            } catch (NormalizationException exp) {
               // This should not happen... values should already be validated...
               // just return the text itself...
               return child.getText();
            }
         }
      }
   }

   protected Object wrappedObjectPut(Object key, Object value, SchemaNode schema) {
      FieldValueWrapper wrapper = (FieldValueWrapper) wrappedInstances.get(key);

      if (wrapper == null) {
         wrapper = getWrapperFactory().wrapInstance(value);
         wrappedInstances.put(key, wrapper);
      } else {
         wrapper.setValue(value);
      }

      Element childElement = getBaseElement().getChild(schema.getName());

      if (childElement == null) {
         childElement = new Element(schema.getName());
         getBaseElement().addContent(childElement);
      }

      childElement.setText(schema.getSchemaNormalizedValue(value));

      return null;
   }

   protected Object wrappedObjectGet(Object key, SchemaNode schema) {
      FieldValueWrapper wrapper = (FieldValueWrapper) wrappedInstances.get(key);

      if (wrapper == null) {
         wrapper = getWrapperFactory().wrapInstance(schema.getObjectType());
         wrappedInstances.put(key, wrapper);

         Element valueElement = getBaseElement().getChild(schema.getName());

         if (valueElement != null) {
            Object value = schema.getActualNormalizedValue(valueElement.getText());
            wrapper.setValue(value);
         }
      }

      return wrapper;
   }


   public Class getType(String key) {
      SchemaNode schema = currentSchema.getChild(key);

      if (schema != null) {
         if (schema.getMaxOccurs() > 1 || schema.getMaxOccurs() == -1) {
            return ElementListBean.class;
         }
         else {
            return schema.getObjectType();
         }
      }
      return null;
   }

   public String toString() {
      return currentElement().getText();
   }

   public void setBaseElement(Element element) {
      this.baseElement = element;
   }

   public Element getBaseElement() {
      return baseElement;
   }

   public boolean isDeferValidation() {
      return deferValidation;
   }

   public void setDeferValidation(boolean deferValidation) {
      this.deferValidation = deferValidation;
   }
   
   class EntrySet implements Map.Entry {
       
       private Object key;
       private Object value;
       //TODO what about equals and hashcode?
       
       public EntrySet (Object key, Object value) {
           this.key = key;
           this.value = value;
       }
        /* (non-Javadoc)
         * @see java.util.Map.Entry#getKey()
         */
        public Object getKey() {
            return key;
        }
    
        /* (non-Javadoc)
         * @see java.util.Map.Entry#getValue()
         */
        public Object getValue() {
            return value;
        }
    
        /* (non-Javadoc)
         * @see java.util.Map.Entry#setValue(java.lang.Object)
         */
        public Object setValue(Object value) {
            // TODO Maybe throw an exception instead
            this.value = value;
            return this.value;
        }
   
   }
   
   
   
    /* (non-Javadoc)
     * @see java.util.Map#entrySet()
     */
    public Set entrySet() {
        Set entries = new HashSet();
        List children = currentSchema.getChildren();
        for (Iterator iter = children.iterator(); iter.hasNext();) {
            SchemaNode child = (SchemaNode)iter.next();
            String key = child.getName();
            Object value = get(key);
            EntrySet entry = new EntrySet(key, value);
            entries.add(entry);
        }

        return entries;
    }
    /* (non-Javadoc)
     * @see java.util.Map#keySet()
     */
    public Set keySet() {
        Set keys = new HashSet();
        //List children = currentSchema.getChildren();
        for (Iterator iter = entrySet().iterator(); iter.hasNext();) {
            EntrySet child = (EntrySet)iter.next();
            keys.add(child.getKey());
        }
        return keys;
    }
    /* (non-Javadoc)
     * @see java.util.Map#values()
     */
    public Collection values() {
        Set values = new HashSet();
        for (Iterator iter = entrySet().iterator(); iter.hasNext();) {
            EntrySet child = (EntrySet)iter.next();
            Object value = child.getValue();
            values.add(value);
        }
        return values;
    }
}
