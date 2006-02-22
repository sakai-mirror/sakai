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
package org.sakaiproject.metaobj.shared;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.UserType;
import org.sakaiproject.metaobj.shared.model.IdImpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author rpembry
 *         <p/>
 *         (based on http://www.hibernate.org/50.html)
 */
public class IdType implements UserType {
   protected final org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory
         .getLog(getClass());

   private final static int[] SQL_TYPES = new int[]{Types.VARCHAR};


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
      String value;
      try {
         value = rs.getString(names[0]);
      }
      catch (SQLException e) {
         logger.error("Stmt: " + rs.getStatement().toString(), e);
         throw e;
      }
      if (rs.wasNull()) {
         return null;
      }
      return new IdImpl(value, null);
   }

   /* (non-Javadoc)
    * @see net.sf.hibernate.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int)
    */
   public void nullSafeSet(PreparedStatement st, Object value, int index)
         throws HibernateException, SQLException {
      if (value == null) {
         st.setNull(index, Types.VARCHAR);
      }
      else {
         st.setString(index, ((IdImpl) value).getValue());
      }

   }

   /* (non-Javadoc)
    * @see net.sf.hibernate.UserType#returnedClass()
    */
   public Class returnedClass() {
      return IdImpl.class;
   }

   /* (non-Javadoc)
    * @see net.sf.hibernate.UserType#sqlTypes()
    */
   public int[] sqlTypes() {
      return SQL_TYPES;
   }

}
