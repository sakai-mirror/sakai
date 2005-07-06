/**********************************************************************************
 *
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/shared/mgt/HomeUserType.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 *
 ***********************************************************************************
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.sakaiproject.metaobj.shared.mgt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.model.Type;
import org.sakaiproject.metaobj.repository.RepositoryConstants;
import org.sakaiproject.metaobj.utils.BeanFactory;
import net.sf.hibernate.UserType;
import net.sf.hibernate.HibernateException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Types;

public class HomeUserType implements UserType {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private final static int[] SQL_TYPES = new int[]{Types.VARCHAR};

   /**
    * Return a deep copy of the persistent state, stopping at entities and at
    * collections.
    *
    * @return Object a copy
    */
   public Object deepCopy(Object value) throws HibernateException {
      Type copy = (Type)value;
      Type newType =
         new Type(copy.getId(), copy.getDescription());
      newType.setSystemOnly(copy.isSystemOnly());
      return newType;
   }

   /**
    * Compare two instances of the class mapped by this type for persistence "equality".
    * Equality of the persistent state.
    *
    * @param x
    * @param y
    * @return boolean
    */
   public boolean equals(Object x, Object y) throws HibernateException {
      return (x == y) || (x != null && y != null &&
         ((Type)x).getId().equals(((Type)y).getId()));
   }

   /**
    * Are objects of this type mutable?
    *
    * @return boolean
    */
   public boolean isMutable() {
      return false;
   }

   /**
    * Retrieve an instance of the mapped class from a JDBC resultset. Implementors
    * should handle possibility of null values.
    *
    * @param rs    a JDBC result set
    * @param names the column names
    * @param owner the containing entity
    * @return Object
    * @throws net.sf.hibernate.HibernateException
    *
    * @throws java.sql.SQLException
    */
   public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
      String value;
      try {
         value = rs.getString(names[0]);
      } catch (SQLException e) {
         logger.error("Stmt: " + rs.getStatement().toString(), e);
         throw e;
      }
      if (rs.wasNull()) return null;

      // todo remove this and make a folder home
      if (value.equals(RepositoryConstants.FOLDER_TYPE.getId().getValue())) {
         return RepositoryConstants.FOLDER_TYPE;
      }

      return getHomeFactory().getHome(value).getType();
   }

   /**
    * Write an instance of the mapped class to a prepared statement. Implementors
    * should handle possibility of null values. A multi-column type should be written
    * to parameters starting from <tt>index</tt>.
    *
    * @param st    a JDBC prepared statement
    * @param value the object to write
    * @param index statement parameter index
    * @throws net.sf.hibernate.HibernateException
    *
    * @throws java.sql.SQLException
    */
   public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
      if (value == null) {
         st.setNull(index, Types.VARCHAR);
      } else {
         st.setString(index, ((Type)value).getId().getValue());
      }
   }

   /**
    * The class returned by <tt>nullSafeGet()</tt>.
    *
    * @return Class
    */
   public Class returnedClass() {
      return Type.class;
   }

   /**
    * Return the SQL type codes for the columns mapped by this type. The
    * codes are defined on <tt>java.sql.Types</tt>.
    *
    * @return int[] the typecodes
    * @see java.sql.Types
    */
   public int[] sqlTypes() {
      return SQL_TYPES;
   }

   protected HomeFactory getHomeFactory() {
      return (HomeFactory)BeanFactory.getInstance().getBean("homeFactory", HomeFactory.class);
   }
}
