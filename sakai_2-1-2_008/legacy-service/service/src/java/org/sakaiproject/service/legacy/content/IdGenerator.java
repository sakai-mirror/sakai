package org.sakaiproject.service.legacy.content;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.id.IdentifierGenerator;
import org.sakaiproject.api.kernel.component.cover.ComponentManager;
import org.sakaiproject.api.kernel.id.IdManager;

import java.io.Serializable;
import java.sql.SQLException;

/**
 * creates unique identifiers for hibernate
 */
public class IdGenerator implements IdentifierGenerator {

    static IdManager uuidManager=null;
    
   public IdGenerator() {
   }

   public Serializable generate(SessionImplementor arg0, Object arg1)
      throws SQLException, HibernateException {
       if (uuidManager==null) {
           uuidManager=(IdManager)ComponentManager.get(IdManager.class);
       }
       return uuidManager.createUuid();
   
   }


}
