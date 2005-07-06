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
 * $Header: /opt/CVS/osp2.x/HomesService/src/java/org/theospi/utils/xml/ValidatedNode.java,v 1.1 2005/06/28 13:09:17 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/28 13:09:17 $
 */
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
