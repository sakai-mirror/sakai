package org.sakaiproject.component.syllabus.data;

import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

import net.sf.hibernate.Criteria;
import net.sf.hibernate.FetchMode;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import net.sf.hibernate.expression.Expression;

import org.sakaiproject.service.syllabus.data.SyllabusData;
import org.sakaiproject.service.syllabus.data.SyllabusItem;
import org.sakaiproject.service.syllabus.data.SyllabusManager;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

/**
 * SyllabusManagerImpl provides convenience functions to query the database
 * 
 * @author <a href="mailto:jlannan@iupui.edu">Jarrod Lannan </a>
 * @version $Id:
 */
public class SyllabusManagerImpl extends HibernateDaoSupport implements SyllabusManager
{

  private static final String QUERY_BY_USERID_AND_CONTEXTID = "findSyllabusItemByUserAndContextIds";
  private static final String QUERY_BY_CONTEXTID = "findSyllabusItemByContextId";
  private static final String QUERY_LARGEST_POSITION = "findLargestSyllabusPosition";
  private static final String USER_ID = "userId";
  private static final String CONTEXT_ID = "contextId";
  private static final String SURROGATE_KEY = "surrogateKey";
  private static final String SYLLABI = "syllabi";  
  private static final String FOREIGN_KEY = "foreignKey";

  /**
   * createSyllabusItem creates a new SyllabusItem
   * @param userId
   * @param contextId
   * @param redirectURL
   *        
   */
  public SyllabusItem createSyllabusItem(String userId, String contextId,
      String redirectURL)
  {
    if (userId == null || contextId == null)
    {
      throw new IllegalArgumentException("Null Argument");
    }
    else
    {
      // construct a new SyllabusItem
      SyllabusItem item = new SyllabusItemImpl(userId, contextId, redirectURL);      
      saveSyllabusItem(item);
      return item;
    }
  }
  
  /**
   * getSyllabiForSyllabusItem returns the collection of syllabi
   * @param syllabusItem
   */
  public Set getSyllabiForSyllabusItem(final SyllabusItem syllabusItem)
  {
    if (syllabusItem == null)
    {
      throw new IllegalArgumentException("Null Argument");
    }
    else
    {                 
      HibernateCallback hcb = new HibernateCallback()
      {                
        public Object doInHibernate(Session session) throws HibernateException,
            SQLException
        {            
          // get syllabi in an eager fetch mode
          Criteria crit = session.createCriteria(SyllabusItemImpl.class)
                      .add(Expression.eq(SURROGATE_KEY, syllabusItem.getSurrogateKey()))
                      .setFetchMode(SYLLABI, FetchMode.EAGER);
                      
          
          SyllabusItem syllabusItem = (SyllabusItem) crit.uniqueResult();
          
          if (syllabusItem != null){            
            return syllabusItem.getSyllabi();                                           
          }     
          return new TreeSet();
        }
      };             
      return (Set) getHibernateTemplate().execute(hcb);     
    }
  }  
  
  /**
   * createSyllabusData creates a persistent SyllabusData object
   * @param title
   * @param position
   * @param assetId
   * @param view
   * @param status
   * @param emailNotification 
   */
  public SyllabusData createSyllabusDataObject(String title, Integer position,
        String asset, String view, String status, String emailNotification)      
  {
    if (position == null)
    {
      throw new IllegalArgumentException("Null Argument");
    }
    else
    {
      // construct a new SyllabusData persistent object
      SyllabusData data = new SyllabusDataImpl();
      data.setTitle(title);
      data.setPosition(position);
      data.setAsset(asset);
      data.setView(view);
      data.setStatus(status);
      data.setEmailNotification(emailNotification);
            
      saveSyllabus(data);
      return data;
    }
  }
  
  /**
   * swapSyllabusDataPositions swaps positions for two SyllabusData objects
   * @param syllabusItem
   * @param d1
   * @param d2
   */
  public void swapSyllabusDataPositions(final SyllabusItem syllabusItem, final SyllabusData d1, final SyllabusData d2)      
  {
    if (syllabusItem == null || d1 == null || d2 == null)
    {
      throw new IllegalArgumentException("Null Argument");
    }
    else
    {
      HibernateCallback hcb = new HibernateCallback()
      {                
        public Object doInHibernate(Session session) throws HibernateException,
            SQLException
        {            
          // load objects from hibernate
          SyllabusItem item = (SyllabusItem) session.get(SyllabusItemImpl.class, syllabusItem.getSurrogateKey());
          SyllabusData data1 = (SyllabusData) session.get(SyllabusDataImpl.class, d1.getSyllabusId());
          SyllabusData data2 = (SyllabusData) session.get(SyllabusDataImpl.class, d2.getSyllabusId());
          
          Integer temp = data1.getPosition();
          data1.setPosition(data2.getPosition());
          data2.setPosition(temp);
          
          return null;                    
        }
      };
      getHibernateTemplate().execute(hcb);
    }
  }    


  /**
   * findLargestSyllabusPosition finds the largest syllabus data position for an item
   * @param syllabusItem
   */
  public Integer findLargestSyllabusPosition(final SyllabusItem syllabusItem)      
  {
    if (syllabusItem == null)
    {
      throw new IllegalArgumentException("Null Argument");
    }
    else
    {
      HibernateCallback hcb = new HibernateCallback()
      {                
        public Object doInHibernate(Session session) throws HibernateException,
            SQLException
        {            
          Query q = session.getNamedQuery(QUERY_LARGEST_POSITION);                
          q.setParameter(FOREIGN_KEY, syllabusItem.getSurrogateKey(), Hibernate.LONG);
          
          Integer position = (Integer) q.uniqueResult();
          
          if (position == null){
            return new Integer(0);
          }
          else{
            return position;
          }
          
        }
      };
      return (Integer) getHibernateTemplate().execute(hcb);
    }
  }    
  
    
  /**
   * getSyllabusItemByContextId finds a SyllabusItem
   * @param contextId
   * @return SyllabusItem
   *        
   */
  public SyllabusItem getSyllabusItemByContextId(final String contextId)
  {
    if (contextId == null)
    {
      throw new IllegalArgumentException("Null Argument");
    }
          
    HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        Query q = session.getNamedQuery(QUERY_BY_CONTEXTID);                        
        q.setParameter(CONTEXT_ID, contextId, Hibernate.STRING);                   
        return q.uniqueResult();
      }
    };
        
    return (SyllabusItem) getHibernateTemplate().execute(hcb);
  }
  
  /**
   * getSyllabusItemByUserAndContextIds finds a SyllabusItem
   * @param userId
   * @param contextId
   * @return SyllabusItem
   *        
   */
  public SyllabusItem getSyllabusItemByUserAndContextIds(final String userId, final String contextId)
  {
    if (userId == null || contextId == null)
    {
      throw new IllegalArgumentException("Null Argument");
    }
          
    HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        Query q = session.getNamedQuery(QUERY_BY_USERID_AND_CONTEXTID);                
        q.setParameter(USER_ID, userId, Hibernate.STRING);
        q.setParameter(CONTEXT_ID, contextId, Hibernate.STRING);                   
        return q.uniqueResult();
      }
    };
        
    return (SyllabusItem) getHibernateTemplate().execute(hcb);
  }
  
  /**
   * addSyllabusToSyllabusItem adds a SyllabusData object to syllabi collection
   * @param syllabusItem
   * @param syllabusData
   * @return Set
   */
  public void addSyllabusToSyllabusItem(final SyllabusItem syllabusItem, final SyllabusData syllabusData)
  {
             
    if (syllabusItem == null || syllabusData == null)
    {
      throw new IllegalArgumentException("Null Argument");
    }      
           
    HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        
        SyllabusItem returnedItem = (SyllabusItem) session.get(SyllabusItemImpl.class, syllabusItem.getSurrogateKey());
        if (returnedItem != null){          
          returnedItem.getSyllabi().add(syllabusData);                   
          session.save(returnedItem);                              
        }           
        return null;
      }
    }; 
    getHibernateTemplate().execute(hcb);    
  }  
  
  
  /**
   * removeSyllabusToSyllabusItem loads many side of the relationship
   * @param syllabusItem
   * @param syllabusData
   * @return Set
   */
  public void removeSyllabusFromSyllabusItem(final SyllabusItem syllabusItem, final SyllabusData syllabusData)
  {
            
    if (syllabusItem == null || syllabusData == null)
    {
      throw new IllegalArgumentException("Null Argument");
    }      
           
    HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        SyllabusItem returnedItem = (SyllabusItem) session.get(SyllabusItemImpl.class, syllabusItem.getSurrogateKey());
        if (returnedItem != null){                    
          returnedItem.getSyllabi().remove(syllabusData);          
          session.saveOrUpdate(returnedItem);          
        }           
        return null;
      }
    }; 
    getHibernateTemplate().execute(hcb);
  }  
  
  /**
   * saveSyllabusItem persists a SyllabusItem
   * @param item
   */
  public void saveSyllabusItem(SyllabusItem item)
  {
    getHibernateTemplate().saveOrUpdate(item);
  }
  
  /**
   * saveSyllabus persists a SyllabusData object
   * @param item
   */
  public void saveSyllabus(SyllabusData data)
  {
    getHibernateTemplate().saveOrUpdate(data);
  }  
    
}