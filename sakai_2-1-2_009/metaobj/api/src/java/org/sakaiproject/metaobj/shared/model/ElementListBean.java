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
