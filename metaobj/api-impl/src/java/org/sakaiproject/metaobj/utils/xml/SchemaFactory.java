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

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.sakaiproject.metaobj.utils.xml.impl.SchemaNodeImpl;

import java.io.File;
import java.net.URL;
import java.util.Hashtable;

public class SchemaFactory {
   static private SchemaFactory schemaFactory = new SchemaFactory();

   private Hashtable schemas = new Hashtable();

   private SchemaFactory() {
   }

   public static SchemaFactory getInstance() {
      return schemaFactory;
   }

   public SchemaNode getSchema(java.io.File in) {
      if (schemas.get(in) != null) {
         return (SchemaNode) schemas.get(in);
      }

      SAXBuilder builder = new SAXBuilder();
      try {
         Document doc = builder.build(in);
         SchemaNode node = new SchemaNodeImpl(doc, this, in);
         schemas.put(in, node);
         return node;
      }
      catch (Exception e) {
         throw new SchemaInvalidException(e);
      }
   }

   public SchemaNode getSchema(java.io.InputStream in) {

      SAXBuilder builder = new SAXBuilder();
      try {
         Document doc = builder.build(in);
         SchemaNode node = new SchemaNodeImpl(doc, this, in);
         return node;
      }
      catch (Exception e) {
         throw new SchemaInvalidException(e);
      }
   }

   public SchemaNode getSchema(java.io.InputStream in, java.lang.String systemId) {
      if (schemas.get(systemId) != null) {
         return (SchemaNode) schemas.get(systemId);
      }

      SAXBuilder builder = new SAXBuilder();
      try {
         Document doc = builder.build(in, systemId);
         SchemaNode node = new SchemaNodeImpl(doc, this, in);
         schemas.put(systemId, node);
         return node;
      }
      catch (Exception e) {
         throw new SchemaInvalidException(e);
      }
   }

   public SchemaNode getSchema(java.io.Reader characterStream, java.lang.String systemId) {
      if (schemas.get(systemId) != null) {
         return (SchemaNode) schemas.get(systemId);
      }

      SAXBuilder builder = new SAXBuilder();
      try {
         Document doc = builder.build(characterStream, systemId);
         SchemaNode node = new SchemaNodeImpl(doc, this, systemId);
         schemas.put(systemId, node);
         return node;
      }
      catch (Exception e) {
         throw new SchemaInvalidException(e);
      }
   }

   public SchemaNode getSchema(java.lang.String systemId) {
      if (schemas.get(systemId) != null) {
         return (SchemaNode) schemas.get(systemId);
      }

      SAXBuilder builder = new SAXBuilder();
      try {
         Document doc = builder.build(systemId);
         SchemaNode node = new SchemaNodeImpl(doc, this, systemId);
         schemas.put(systemId, node);
         return node;
      }
      catch (Exception e) {
         throw new SchemaInvalidException(e);
      }
   }

   public SchemaNode getSchema(java.net.URL url) {
      if (schemas.get(url) != null) {
         return (SchemaNode) schemas.get(url);
      }

      SAXBuilder builder = new SAXBuilder();
      try {
         Document doc = builder.build(url);
         SchemaNode node = new SchemaNodeImpl(doc, this, url);
         schemas.put(url, node);
         return node;
      }
      catch (Exception e) {
         throw new SchemaInvalidException(e);
      }
   }

   public SchemaNode getRelativeSchema(String schemaLocation, Object path) {
      try {
         if (path instanceof URL) {
            URL urlPath = (URL) path;
            URL schemaUrl = new URL(urlPath, schemaLocation);
            return getSchema(schemaUrl);
         }
         else if (path instanceof File) {
            File filePath = (File) path;
            File schemaFile = new File(filePath.getParentFile(), schemaLocation);
            return getSchema(schemaFile);
         }
         else {
            URL urlPath = new URL(path.toString());
            URL schemaUrl = new URL(urlPath, schemaLocation);
            return getSchema(schemaUrl);
         }
      }
      catch (Exception exp) {
         throw new SchemaInvalidException(exp);
      }
   }

   public void reload() {
      schemas = new Hashtable();
   }

}
