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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/shared/model/impl/HibernateStructuredArtifact.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision$
 * $Date$
 */
/*
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/shared/model/impl/HibernateStructuredArtifact.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision$
 * $Date$
 */
package org.sakaiproject.metaobj.shared.model.impl;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.UserType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.WritableObjectHome;
import org.sakaiproject.metaobj.shared.model.StructuredArtifact;
import org.sakaiproject.metaobj.utils.BeanFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class HibernateStructuredArtifact extends StructuredArtifact implements UserType {
   protected final Log logger = LogFactory.getLog(getClass());
   private final static int[] SQL_TYPES = new int[]{Types.BLOB};

   public HibernateStructuredArtifact() {
   }

   public int[] sqlTypes() {
      return SQL_TYPES;
   }

   /* (non-Javadoc)
    * @see net.sf.hibernate.UserType#deepCopy(java.lang.Object)
    */
   public Object deepCopy(Object arg0) throws HibernateException {
      return arg0;
   }

   /* (non-Javadoc)
    * @see net.sf.hibernate.UserType#equals(java.lang.Object, java.lang.Object)
    */
   public boolean equals(Object x, Object y) throws HibernateException {
      return (x == y) || (x != null && y != null && x.equals(y));
   }

   /* (non-Javadoc)
    * @see net.sf.hibernate.UserType#isMutable()
    */
   public boolean isMutable() {
      return false;
   }

   /* (non-Javadoc)
    * @see net.sf.hibernate.UserType#nullSafeGet(java.sql.ResultSet, java.lang.String[], java.lang.Object)
    */
   public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
      throws HibernateException, SQLException {
      byte[] bytes = rs.getBytes(names[0]);
      if (rs.wasNull()) return null;

      //TODO figure out how to inject this
      HomeFactory homeFactory = (HomeFactory) BeanFactory.getInstance().getBean("xmlHomeFactory");
      WritableObjectHome home = (WritableObjectHome) homeFactory.getHome("agent");

      StructuredArtifact artifact = (StructuredArtifact) home.createInstance();
      ByteArrayInputStream in = new ByteArrayInputStream(bytes);
      SAXBuilder saxBuilder = new SAXBuilder();
      try {
         Document doc = saxBuilder.build(in);
         artifact.setBaseElement(doc.getRootElement());
      } catch (JDOMException e) {
         throw new HibernateException(e);
      } catch (IOException e) {
         throw new HibernateException(e);
      }
      return artifact;
   }

   /* (non-Javadoc)
    * @see net.sf.hibernate.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int)
    */
   public void nullSafeSet(PreparedStatement st, Object value, int index)
      throws HibernateException, SQLException {
      if (value == null) {
         st.setNull(index, Types.VARBINARY);
      } else {
         StructuredArtifact artifact = (StructuredArtifact) value;
         Document doc = new Document();
         Element rootElement = artifact.getBaseElement();
         rootElement.detach();
         doc.setRootElement(rootElement);
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         XMLOutputter xmlOutputter = new XMLOutputter();
         try {
            xmlOutputter.output(doc, out);
         } catch (IOException e) {
            throw new HibernateException(e);
         }
         st.setBytes(index, out.toByteArray());
      }

   }

   /* (non-Javadoc)
    * @see net.sf.hibernate.UserType#returnedClass()
    */
   public Class returnedClass() {
      return StructuredArtifact.class;
   }

}
