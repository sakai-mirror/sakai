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

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 15, 2004
 * Time: 11:10:01 AM
 * To change this template use File | Settings | File Templates.
 */
public interface SchemaNode extends Serializable {

   /**
    * Get the namespace for this schema
    *
    * @return the target namespace of this xsd schema
    */
   public Namespace getTargetNamespace();

   /**
    * Validates the passed in node and all children.
    * Will also normalize any values.
    *
    * @param node a jdom element to validate
    * @return the validated Element wrapped
    *         in a ValidatedNode class
    */
   public ValidatedNode validateAndNormalize(Element node);

   public ValidatedNode validateAndNormalize(Attribute node);

   /**
    * Gets the schema object for the named child node.
    *
    * @param elementName the name of the schema node to retrive.
    * @return
    */
   public SchemaNode getChild(String elementName);

   /**
    * Retuns the max number of times the element
    * defined by this node can occur in its parent.
    * The root schema will always return 1 here.
    *
    * @return
    */
   public int getMaxOccurs();

   /**
    * Returns the min number of times the element
    * defined by this node can occur in its parent.
    * The root schema will always return 1 here.
    *
    * @return
    */
   public int getMinOccurs();

   public String getSchemaNormalizedValue(Object value)
         throws NormalizationException;

   public Object getActualNormalizedValue(String value)
         throws NormalizationException;

   public String getName();

   public Class getObjectType();

   public List getChildren();

   public Collection getRootChildren();

   public String getDocumentAnnotation(String source);

   public String getAppAnnotation(String source);

   public Map getDocumentAnnotations();

   public Map getAppAnnotations();

   public List getEnumeration();

   public boolean hasEnumerations();

   public boolean isAttribute();

   public boolean isDataNode();

   public Element getSchemaElement();

   public ElementType getType();

}
