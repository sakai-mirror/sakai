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
package org.sakaiproject.metaobj.utils.xml;

import org.jdom.Element;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 15, 2004
 * Time: 11:10:17 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ValidatedNode {

   /**
    * Get the schema responsible for this node.
    *
    * @return
    */
   public SchemaNode getSchema();

   /**
    * Get the named child node as a validated node
    *
    * @param elementName
    * @return
    */
   public ValidatedNode getChild(String elementName);

   /**
    * Get all the direct children of this node as
    * a list of ValidatedNode objects
    *
    * @return
    */
   public List getChildren();

   /**
    * Get all the named direct children of this node
    * as a list of ValidatedNode objects.
    *
    * @param elementName
    * @return
    */
   public List getChildren(String elementName);

   /**
    * Get the normalized value of this element as an object.
    * Note: in the case of complex nodes, this could return
    * either a List of ValidatedNode objects (the children of this node)
    *
    * @return
    */
   public Object getNormalizedValue();

   /**
    * The list of errors associated with this node
    *
    * @return
    */
   public List getErrors();

   /**
    * This returnes the element associated with this node.
    *
    * @return
    */
   public Element getElement();

}
