/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.component.app.syllabus;

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

import org.sakaiproject.api.app.syllabus.SyllabusData;
import org.sakaiproject.api.app.syllabus.SyllabusItem;
import org.sakaiproject.api.app.syllabus.SyllabusManager;
import org.sakaiproject.api.app.syllabus.SyllabusAttachment;

import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.content.cover.ContentHostingService;

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
  private static final String QUERY_BY_SYLLABUSDATAID = "findSyllabusDataByDataIds";
  private static final String DATA_KEY = "syllabusId";
  private static final String SYLLABUS_DATA_ID = "syllabusId";
  private static final String ATTACHMENTS = "attachments";
  
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
   * removes a syllabus data object (on form cancel action) 
   * @see org.sakaiproject.api.app.syllabus.SyllabusManager#removeSyllabusDataObject(org.sakaiproject.api.app.syllabus.SyllabusData)
   */
  public void removeSyllabusDataObject(SyllabusData o)
  {
    getHibernateTemplate().delete(o);
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

  public SyllabusData getSyllabusData(final String dataId)
  {
    if (dataId == null)
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
          Long longObj = new Long(dataId);
          SyllabusData returnedData = (SyllabusData) session.get(SyllabusDataImpl.class, longObj);
          return returnedData;
        }
      }; 
      return (SyllabusData) getHibernateTemplate().execute(hcb);
    }

  }  

  public SyllabusAttachment createSyllabusAttachmentObject(String attachId, String name)      
  {
    try
    {
      SyllabusAttachment attach = new SyllabusAttachmentImpl();
      
      attach.setAttachmentId(attachId);
      
      attach.setName(name);

      ContentResource cr = ContentHostingService.getResource(attachId);
      attach.setSize((new Integer(cr.getContentLength())).toString());
      attach.setCreatedBy(cr.getProperties().getProperty(cr.getProperties().getNamePropCreator()));
      attach.setLastModifiedBy(cr.getProperties().getProperty(cr.getProperties().getNamePropModifiedBy()));
      attach.setType(cr.getContentType());
      String tempString = cr.getUrl();
      String newString = new String();
      char[] oneChar = new char[1];
      for(int i=0; i<tempString.length(); i++)
      {
        if(tempString.charAt(i) != ' ')
        {
          oneChar[0] = tempString.charAt(i);
          String concatString = new String(oneChar);
          newString = newString.concat(concatString);
        }
        else
        {
          newString = newString.concat("%20");
        }
      } 
      //tempString.replaceAll(" ", "%20");
      attach.setUrl(newString);

      saveSyllabusAttachment(attach);
      
      return attach;
    }
    catch(Exception e)
    {
      e.printStackTrace();
      return null;
    }
  }

  public void saveSyllabusAttachment(SyllabusAttachment attach)
  {
    getHibernateTemplate().saveOrUpdate(attach);
  }
  
  public void addSyllabusAttachToSyllabusData(final SyllabusData syllabusData, final SyllabusAttachment syllabusAttach)
  {
             
    if (syllabusData == null || syllabusAttach == null)
    {
      throw new IllegalArgumentException("Null Argument");
    }      
           
    HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        SyllabusData returnedData = (SyllabusData) session.get(SyllabusDataImpl.class, syllabusData.getSyllabusId());
        if (returnedData != null){
          returnedData.getAttachments().add(syllabusAttach);
          session.save(returnedData);                              
        }           
        return null;
      }
    }; 
    getHibernateTemplate().execute(hcb);
  }  


  public void removeSyllabusAttachmentObject(SyllabusAttachment o)
  {
    getHibernateTemplate().delete(o);
  }
  
  public void removeSyllabusAttachSyllabusData(final SyllabusData syllabusData, final SyllabusAttachment syllabusAttach)
  {
            
    if (syllabusData == null || syllabusAttach == null)
    {
      throw new IllegalArgumentException("Null Argument");
    }      
           
    HibernateCallback hcb = new HibernateCallback()
    {
      public Object doInHibernate(Session session) throws HibernateException,
          SQLException
      {
        SyllabusData returnedData = (SyllabusData) session.get(SyllabusDataImpl.class, syllabusData.getSyllabusId());
        if (returnedData != null){                    
          returnedData.getAttachments().remove(syllabusAttach);          
          session.saveOrUpdate(returnedData);          
        }           
        return null;
      }
    }; 
    getHibernateTemplate().execute(hcb);
  }  

  public Set getSyllabusAttachmentsForSyllabusData(final SyllabusData syllabusData)
  {
    if (syllabusData == null)
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
          Criteria crit = session.createCriteria(SyllabusDataImpl.class)
                      .add(Expression.eq(SYLLABUS_DATA_ID, syllabusData.getSyllabusId()))
                      .setFetchMode(ATTACHMENTS, FetchMode.EAGER);
                      
          
          SyllabusData syllabusData = (SyllabusData) crit.uniqueResult();
          
          if (syllabusData != null){            
            return syllabusData.getAttachments();                                           
          }     
          return new TreeSet();
        }
      };             
      return (Set) getHibernateTemplate().execute(hcb);     
    }
  }  

  public SyllabusAttachment getSyllabusAttachment(final String syllabusAttachId)
  {
    if (syllabusAttachId == null)
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
          Long longObj = new Long(syllabusAttachId);
          SyllabusAttachment returnedAttach = (SyllabusAttachment) session.get(SyllabusAttachmentImpl.class, longObj);
          return returnedAttach;
        }
      }; 
      return (SyllabusAttachment) getHibernateTemplate().execute(hcb);
    }

  }
  
/*  public SyllabusAttachment creatSyllabusAttachmentResource(String attachId, String name)
  {
    SyllabusAttachment attach = new SyllabusAttachmentImpl();
    
    attach.setAttachmentId(attachId);
    
    attach.setName(name);
    
    return attach;
  }*/
}


