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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/shared/model/ElementListBean.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/29 18:36:41 $
 */
package org.sakaiproject.metaobj.shared.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Mar 11, 2004
 * Time: 3:55:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class ElementListBean extends ArrayList {
   protected final Log logger = LogFactory.getLog(getClass());

   private Element parentElement;
   private SchemaNode schema;
   private boolean deferValidation = false;

   public ElementListBean() {

   }

   public ElementListBean(Element parentElement, SchemaNode schema, boolean deferValidation) {
      this.parentElement = parentElement;
      this.schema = schema;
      this.deferValidation = deferValidation;
   }

   public ElementListBean(Element parentElement, List elements,
                          SchemaNode schema, boolean deferValidation) {
      super(elements);
      this.parentElement = parentElement;
      this.schema = schema;
      this.deferValidation = deferValidation;
   }

   public ElementListBean(List elements, SchemaNode schema) {
      super(elements);
      this.schema = schema;
   }

   public Object get(int index) {
      logger.debug("get called with index " + index);
      return super.get(index);
   }


   public ElementBean createBlank() {
      return new ElementBean(new Element(schema.getName()), schema, deferValidation);
   }

   /**
    * Removes the element at the specified position in this list.
    * Shifts any subsequent elements to the left (subtracts one from their
    * indices).
    *
    * @param index the index of the element to removed.
    * @return the element that was removed from the list.
    * @throws IndexOutOfBoundsException if index out of range <tt>(index
    *                                   &lt; 0 || index &gt;= size())</tt>.
    */
   public Object remove(int index) {
      ElementBean bean = (ElementBean) get(index);

      bean.getBaseElement().getParent().removeContent(bean.getBaseElement());

      return super.remove(index);
   }

   /**
    * Appends the specified element to the end of this list.
    *
    * @param o element to be appended to this list.
    * @return <tt>true</tt> (as per the general contract of Collection.add).
    */
   public boolean add(Object o) {
      ElementBean bean = (ElementBean) o;

      parentElement.addContent(bean.getBaseElement());

      return super.add(o);
   }

   /**
    * Inserts the specified element at the specified position in this
    * list. Shifts the element currently at that position (if any) and
    * any subsequent elements to the right (adds one to their indices).
    *
    * @param index   index at which the specified element is to be inserted.
    * @param element element to be inserted.
    * @throws IndexOutOfBoundsException if index is out of range
    *                                   <tt>(index &lt; 0 || index &gt; size())</tt>.
    */
   public void add(int index, Object element) {
      add(element);

      super.add(index, element);
   }


}
