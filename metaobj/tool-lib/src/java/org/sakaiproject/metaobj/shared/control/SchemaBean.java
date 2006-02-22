/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/trunk/sakai/legacy-service/service/src/java/org/sakaiproject/exception/InconsistentException.java $
 * $Id: InconsistentException.java 632 2005-07-14 21:22:50Z janderse@umich.edu $
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
package org.sakaiproject.metaobj.shared.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 20, 2004
 * Time: 5:43:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class SchemaBean {

   private SchemaNode schema;
   private String schemaName;
   private String parentName = "";
   private boolean rootNodeFlag = false;
   private Map annotations = null;
   private SchemaBean parent = null;
   protected final Log logger = LogFactory.getLog(getClass());
   private boolean documentRoot = false;
   private String description;

   public SchemaBean(String rootNode, SchemaNode schema, String schemaName, String schemaDescription) {
      this.schema = schema.getChild(rootNode);
      this.schemaName = schemaName;
      this.description = schemaDescription;
      rootNodeFlag = true;
      this.parent = new SchemaBean(schema, schemaName, null, schemaDescription);
      documentRoot = true;
   }

   public SchemaBean(SchemaNode schema, String schemaName, String parentName, String schemaDescription) {
      this.schema = schema;
      this.schemaName = schemaName;
      this.parentName = parentName;
      this.description = schemaDescription;

      if (parentName == null) {
         rootNodeFlag = true;
      }
   }

   public List getFields() {
      logger.debug("schema name" + schema.getName());
      List fieldList = schema.getChildren();
      List returnedList = new ArrayList();

      for (Iterator i = fieldList.iterator(); i.hasNext();) {
         returnedList.add(new SchemaBean((SchemaNode) i.next(),
               schemaName, getFieldNamePath(), description));
      }

      return returnedList;
   }

   public String getFieldName() {
      return schema.getName();
   }

   public String getFieldNamePath() {
      return getFieldNamePath(false);
   }

   public String getFieldNamePathReadOnly() {
      return getFieldNamePath(true);
   }

   public String getFieldNamePath(boolean viewOnly) {
      if (rootNodeFlag) {
         return "";
      }

      String returnedPath = parentName;

      if (returnedPath != null && returnedPath.length() > 0) {
         if (viewOnly) {
            returnedPath += "']['";
         }
         else {
            returnedPath += ".";
         }
      }
      else if (returnedPath == null) {
         returnedPath = "";
      }

      return returnedPath + getFieldName();
   }

   public SchemaNode getSchema() {
      return schema;
   }

   public void setSchema(SchemaNode schema) {
      this.schema = schema;
   }

   public String getSchemaName() {
      return schemaName;
   }

   public void setSchemaName(String schemaName) {
      this.schemaName = schemaName;
   }

   public Map getAnnotations() {
      if (annotations != null) {
         return annotations;
      }

      annotations = new Hashtable();

      for (Iterator i = schema.getDocumentAnnotations().entrySet().iterator(); i.hasNext();) {
         Map.Entry entry = (Map.Entry) i.next();

         if (entry.getKey().toString().startsWith("ospi.")) {
            annotations.put(entry.getKey().toString().substring(5),
                  entry.getValue());
         }
         else if (entry.getKey().toString().startsWith("sakai.")) {
            annotations.put(entry.getKey().toString().substring(6),
                  entry.getValue());
         }
      }

      return annotations;
   }

   public SchemaBean getParent() {
      return parent;
   }

   public void setParent(SchemaBean parent) {
      this.parent = parent;
   }

   public boolean isDocumentRoot() {
      return documentRoot;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }
}
