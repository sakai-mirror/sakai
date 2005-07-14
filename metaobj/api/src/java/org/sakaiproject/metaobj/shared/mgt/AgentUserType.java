/*
 * $URL$
 * $Revision$
 * $Date$
 */
package org.sakaiproject.metaobj.shared.mgt;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.UserType;
import org.sakaiproject.api.kernel.component.cover.ComponentManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class AgentUserType implements UserType {

   public int[] sqlTypes() {
      return new int[]{Types.VARCHAR};
   }

   public Class returnedClass() {
      return Agent.class;
   }

   public boolean equals(Object x, Object y) throws HibernateException {
      return (x == y) || (x != null && y != null && x.equals(y));
   }

   public Object nullSafeGet(ResultSet resultSet, String[] names, Object o) throws HibernateException, SQLException {
      String result = resultSet.getString(names[0]);
      if (result == null)
         return null;
      
      Id agentId = getIdManager().getId(result);
      Agent agent = getAgentManager().getAgent(agentId);
      return agent;
   }

   public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
      Agent agent = (Agent) value;
      if (value == null || agent.getId() == null) {
         st.setNull(index, Types.VARCHAR);
      } else {
         st.setString(index, agent.getId().getValue());
      }
   }

   public Object deepCopy(Object o) throws HibernateException {
      return o;
   }

   public boolean isMutable() {
      return false;
   }

   public AgentManager getAgentManager() {
      return (AgentManager) ComponentManager.getInstance().get("agentManager");
   }

   public IdManager getIdManager() {
      return (IdManager) ComponentManager.getInstance().get("idManager");
   }

}
