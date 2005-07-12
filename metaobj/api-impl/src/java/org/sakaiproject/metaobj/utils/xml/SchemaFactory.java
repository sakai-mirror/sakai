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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/utils/xml/SchemaFactory.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision$
 * $Date$
 */
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
         return (SchemaNode)schemas.get(in);
      }

      SAXBuilder builder = new SAXBuilder();
      try {
         Document doc = builder.build(in);
         SchemaNode node = new SchemaNodeImpl(doc, this, in);
         schemas.put(in, node);
         return node;
      } catch (Exception e) {
         throw new SchemaInvalidException(e);
      }
   }

   public SchemaNode getSchema(java.io.InputStream in) {

      SAXBuilder builder = new SAXBuilder();
      try {
         Document doc = builder.build(in);
         SchemaNode node = new SchemaNodeImpl(doc, this, in);
         return node;
      } catch (Exception e) {
         throw new SchemaInvalidException(e);
      }
   }

   public SchemaNode getSchema(java.io.InputStream in, java.lang.String systemId) {
      if (schemas.get(systemId) != null) {
         return (SchemaNode)schemas.get(systemId);
      }

      SAXBuilder builder = new SAXBuilder();
      try {
         Document doc = builder.build(in, systemId);
         SchemaNode node = new SchemaNodeImpl(doc, this, in);
         schemas.put(systemId, node);
         return node;
      } catch (Exception e) {
         throw new SchemaInvalidException(e);
      }
   }

   public SchemaNode getSchema(java.io.Reader characterStream, java.lang.String systemId) {
      if (schemas.get(systemId) != null) {
         return (SchemaNode)schemas.get(systemId);
      }

      SAXBuilder builder = new SAXBuilder();
      try {
         Document doc = builder.build(characterStream, systemId);
         SchemaNode node = new SchemaNodeImpl(doc, this, systemId);
         schemas.put(systemId, node);
         return node;
      } catch (Exception e) {
         throw new SchemaInvalidException(e);
      }
   }

   public SchemaNode getSchema(java.lang.String systemId) {
      if (schemas.get(systemId) != null) {
         return (SchemaNode)schemas.get(systemId);
      }

      SAXBuilder builder = new SAXBuilder();
      try {
         Document doc = builder.build(systemId);
         SchemaNode node = new SchemaNodeImpl(doc, this, systemId);
         schemas.put(systemId, node);
         return node;
      } catch (Exception e) {
         throw new SchemaInvalidException(e);
      }
   }

   public SchemaNode getSchema(java.net.URL url) {
      if (schemas.get(url) != null) {
         return (SchemaNode)schemas.get(url);
      }

      SAXBuilder builder = new SAXBuilder();
      try {
         Document doc = builder.build(url);
         SchemaNode node = new SchemaNodeImpl(doc, this, url);
         schemas.put(url, node);
         return node;
      } catch (Exception e) {
         throw new SchemaInvalidException(e);
      }
   }

   public SchemaNode getRelativeSchema(String schemaLocation, Object path) {
      try {
         if (path instanceof URL) {
            URL urlPath = (URL)path;
            URL schemaUrl = new URL(urlPath, schemaLocation);
            return getSchema(schemaUrl);
         }
         else if (path instanceof File) {
            File filePath = (File)path;
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
