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
import org.sakaiproject.api.kernel.component.cover.ComponentManager;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.WritableObjectHome;
import org.sakaiproject.metaobj.shared.model.StructuredArtifact;

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
      if (rs.wasNull()) {
         return null;
      }

      //TODO figure out how to inject this
      HomeFactory homeFactory = (HomeFactory) ComponentManager.getInstance().get("xmlHomeFactory");
      WritableObjectHome home = (WritableObjectHome) homeFactory.getHome("agent");

      StructuredArtifact artifact = (StructuredArtifact) home.createInstance();
      ByteArrayInputStream in = new ByteArrayInputStream(bytes);
      SAXBuilder saxBuilder = new SAXBuilder();
      try {
         Document doc = saxBuilder.build(in);
         artifact.setBaseElement(doc.getRootElement());
      }
      catch (JDOMException e) {
         throw new HibernateException(e);
      }
      catch (IOException e) {
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
      }
      else {
         StructuredArtifact artifact = (StructuredArtifact) value;
         Document doc = new Document();
         Element rootElement = artifact.getBaseElement();
         rootElement.detach();
         doc.setRootElement(rootElement);
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         XMLOutputter xmlOutputter = new XMLOutputter();
         try {
            xmlOutputter.output(doc, out);
         }
         catch (IOException e) {
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
