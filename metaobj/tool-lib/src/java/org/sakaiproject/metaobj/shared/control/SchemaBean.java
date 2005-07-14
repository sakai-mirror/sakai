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
 * $URL: /opt/CVS/osp2.x/HomesTool/src/java/org/theospi/metaobj/shared/control/SchemaBean.java,v 1.1 2005/06/28 13:31:53 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/28 13:31:53 $
 */
package org.sakaiproject.metaobj.shared.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
            schemaName, getFieldNamePath(),description));
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
