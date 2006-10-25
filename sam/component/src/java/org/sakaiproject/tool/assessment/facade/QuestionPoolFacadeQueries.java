/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2004-2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
package org.sakaiproject.tool.assessment.facade;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osid.OsidException;
import org.sakaiproject.tool.assessment.data.model.Tree;
import org.sakaiproject.tool.assessment.data.dao.assessment.ItemData;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemMetaDataIfc;
import org.sakaiproject.tool.assessment.data.dao.assessment.ItemText;
import org.sakaiproject.tool.assessment.data.dao.questionpool.QuestionPoolAccessData;
import org.sakaiproject.tool.assessment.data.dao.questionpool.QuestionPoolData;
import org.sakaiproject.tool.assessment.data.dao.questionpool.QuestionPoolItemData;
import org.sakaiproject.tool.assessment.osid.shared.impl.IdImpl;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import org.springframework.orm.hibernate.HibernateCallback;
import org.sakaiproject.tool.assessment.services.PersistenceService;

public class QuestionPoolFacadeQueries
    extends HibernateDaoSupport implements QuestionPoolFacadeQueriesAPI {
  private static Log log = LogFactory.getLog(QuestionPoolFacadeQueries.class);

  public QuestionPoolFacadeQueries() {
  }

  public IdImpl getQuestionPoolId(String id) {
    return new IdImpl(id);
  }

  public IdImpl getQuestionPoolId(Long id) {
    return new IdImpl(id);
  }

  public IdImpl getQuestionPoolId(long id) {
    return new IdImpl(id);
  }

  /**
   * Get a list of all the pools in the site. Note that questions in each pool will not
   * be populated. We must keep this list updated.
   */
  public List getAllPools() {
    return getHibernateTemplate().find("from QuestionPoolData");
  }


  public List getAllPoolsByAgent(String agentId) {
    List list = getHibernateTemplate().find(
        "from QuestionPoolData a where a.ownerId= ? ",
        new Object[] {agentId}
        , new net.sf.hibernate.type.Type[] {Hibernate.STRING});
    return list;

  }
  /**
       * Get all the pools that the agent has access to. The easiest way seems to be
   * #1. get all the existing pool
   * #2. get all the QuestionPoolAccessData record of the agent
   * #3. go through the existing pools and check it against the QuestionPoolAccessData (qpa) record to see if
   * the agent is granted access to it. qpa record (if exists) always trumps the default access right set
   * up for a pool
   * e.g. if the defaultAccessType for a pool is ACCESS_DENIED but the qpa record say ADMIN, then access=ADMIN
   * e.g. if the defaultAccessType for a pool is ADMIN but the qpa record say ACCESS_DENIED, then access=ACCESS_DENIED
   * e.g. if no qpa record exists, then access rule will follow the defaultAccessType set by the pool
   */
  public QuestionPoolIteratorFacade getAllPools(String agentId) {
    ArrayList qpList = new ArrayList();

    // #1.
    // lydial: 9/22/05 we are not really using QuestionPoolAccessData, so filter by ownerid 
    //List poolList = getAllPools(); 
    List poolList = getAllPoolsByAgent(agentId); 

/*
    // #2. get all the QuestionPoolAccessData record belonging to the agent
    List questionPoolAccessList = getHibernateTemplate().find(
        "from QuestionPoolAccessData as qpa where qpa.agentId=?",
        new Object[] {agentId}
        , new net.sf.hibernate.type.Type[] {Hibernate.STRING});
    HashMap h = new HashMap(); // prepare a hashMap with (poolId, qpa)
    Iterator i = questionPoolAccessList.iterator();
    while (i.hasNext()) {
      QuestionPoolAccessData qpa = (QuestionPoolAccessData) i.next();
      h.put(qpa.getQuestionPoolId(), qpa);
    }

    // #3. We need to go through the existing QuestionPool and the QuestionPoolAccessData record
    // to determine the access type
*/

    try {
      Iterator j = poolList.iterator();
      while (j.hasNext()) {
        QuestionPoolData qpp = (QuestionPoolData) j.next();
        // I really wish we don't need to populate  the questionpool size & subpool size for JSF
        // watch this for performance. hope Hibernate is smart enough not to load the entire question
        // - daisy, 10/04/04
        populateQuestionPoolItemDatas(qpp);

        qpList.add(getQuestionPool(qpp));
/*
        QuestionPoolAccessData qpa = (QuestionPoolAccessData) h.get(qpp.
            getQuestionPoolId());
        if (qpa == null) {
          // if (qpa == null), take what is set for pool.
          if (! (QuestionPoolFacade.ACCESS_DENIED).equals(qpp.getAccessTypeId())) {
            qpList.add(getQuestionPool(qpp));
          }
        }
        else {
          if (! (QuestionPoolFacade.ACCESS_DENIED).equals(qpa.getAccessTypeId())) {
            qpp.setAccessTypeId(qpa.getAccessTypeId());
            qpList.add(getQuestionPool(qpp));
          }
        }
*/
      }
    }
    catch (Exception e) {
      log.warn(e.getMessage());
    }
    return new QuestionPoolIteratorFacade(qpList);
  }

  public ArrayList getBasicInfoOfAllPools(String agentId) {
    List list = getHibernateTemplate().find(
        "select new QuestionPoolData(a.questionPoolId, a.title)from QuestionPoolData a where a.ownerId= ? ",
        new Object[] {agentId}
        , new net.sf.hibernate.type.Type[] {Hibernate.STRING});
    ArrayList poolList = new ArrayList();
    for (int i = 0; i < list.size(); i++) {
      QuestionPoolData a = (QuestionPoolData) list.get(i);
      QuestionPoolFacade f = new QuestionPoolFacade(a.getQuestionPoolId(),
          a.getTitle());
      poolList.add(f);
    }
    return poolList;
  }

  private QuestionPoolFacade getQuestionPool(QuestionPoolData qpp) {
    try {
      return new QuestionPoolFacade(qpp);
    }
    catch (Exception e) {
      log.warn(e.getMessage());
      return null;
    }
  }

  private List getAllItemsInThisPoolOnlyAndDetachFromAssessment(final Long questionPoolId) {
    // return items that belong to this pool and this pool only.  These items can not be part of any assessment either.
    List list = getAllItemsInThisPoolOnly(questionPoolId);
    ArrayList newlist = new ArrayList();
    for (int i = 0; i < list.size(); i++) {
      ItemData itemdata = (ItemData) list.get(i);
      if (itemdata.getSection()==null ) {
        // these items do not belong to any assessments, so add them to the list
        newlist.add(itemdata);
      }
      else {
        // do not add these items to the list, but we need to remove the POOLID metadata
        // this item still links to an assessment
        // remove this item's POOLID itemmetadata
        itemdata.removeMetaDataByType(ItemMetaDataIfc.POOLID);
        getHibernateTemplate().saveOrUpdate(itemdata);  //save itemdata after removing metadata
      }
    }
    return newlist;
  }


  public List getAllItemsInThisPoolOnly(Long questionPoolId) {
  // return items that belong to this pool and this pool only.   
    List list = getHibernateTemplate().find("select ab from ItemData ab, QuestionPoolItemData qpi where ab.itemId=qpi.itemId and qpi.questionPoolId = ?",
                                            new Object[] {questionPoolId}
                                            ,
                                            new net.sf.hibernate.type.Type[] {Hibernate.
                                            LONG});
    ArrayList newlist = new ArrayList();
    for (int i = 0; i < list.size(); i++) {
      ItemData itemdata = (ItemData) list.get(i);
      String itemId = itemdata.getItemId().toString();
     if (getPoolIdsByItem(itemId).size() == 1) {
       newlist.add(itemdata);
     }
     else {
       // this item still links to other pool(s)
     } 
    }
    return newlist;
  }

  public List getAllItems(Long questionPoolId) {
    List list = getHibernateTemplate().find("select ab from ItemData ab, QuestionPoolItemData qpi where ab.itemId=qpi.itemId and qpi.questionPoolId = ?",
                                            new Object[] {questionPoolId}
                                            ,
                                            new net.sf.hibernate.type.Type[] {Hibernate.
                                            LONG});
    return list;

  }

  public List getAllItemFacadesOrderByItemText(Long questionPoolId,
                                               String orderBy) {
    List list = getHibernateTemplate().find("select ab from ItemData as ab, QuestionPoolItemData as qpi  WHERE ab.itemId=qpi.itemId and qpi.questionPoolId = ? order by ab." +
                                            orderBy,
                                            new Object[] {questionPoolId},
                                            new net.sf.hibernate.type.Type[] {Hibernate.
                                            LONG});

    ArrayList itemList = new ArrayList();

    for (int i = 0; i < list.size(); i++) {
       ItemData itemdata = (ItemData) list.get(i);
       ItemFacade f = new ItemFacade(itemdata);
       itemList.add(f);
    }
    return itemList;

  }

  public List getAllItemFacadesOrderByItemType(Long questionPoolId,
                                               String orderBy) {
    List list = getHibernateTemplate().find("select ab from ItemData ab, QuestionPoolItemData qpi, TypeD t where ab.itemId=qpi.itemId and ab.typeId=t.typeId and qpi.questionPoolId = ? order by t." +
                                            orderBy,
                                            new Object[] {questionPoolId}
                                            ,
                                            new net.sf.hibernate.type.Type[] {Hibernate.
                                            LONG});

    ArrayList itemList = new ArrayList();
    for (int i = 0; i < list.size(); i++) {
      ItemData itemdata = (ItemData) list.get(i);
      ItemFacade f = new ItemFacade(itemdata);
      itemList.add(f);
    }
    return itemList;
  }

  public List getAllItemFacades(Long questionPoolId) {
    List list = getHibernateTemplate().find("select ab from ItemData ab, QuestionPoolItemData qpi where ab.itemId=qpi.itemId and qpi.questionPoolId = ?",
                                            new Object[] {questionPoolId}
                                            ,
                                            new net.sf.hibernate.type.Type[] {Hibernate.
                                            LONG});
    ArrayList itemList = new ArrayList();
    for (int i = 0; i < list.size(); i++) {
      ItemData itemdata = (ItemData) list.get(i);
      ItemFacade f = new ItemFacade(itemdata);
      itemList.add(f);
    }
    return itemList;

  }

  private void populateQuestionPoolItemDatas(QuestionPoolData qpp) {
    try {
      Set questionPoolItems = qpp.getQuestionPoolItems();
      if (questionPoolItems != null) {
        // let's get all the items for the specified pool in one shot
        HashMap h = new HashMap();
        List itemList = getAllItems(qpp.getQuestionPoolId());

        Iterator j = itemList.iterator();
        while (j.hasNext()) {
          ItemData itemData = (ItemData) j.next();
          h.put(itemData.getItemIdString(), itemData);
        }
        ArrayList itemArrayList = new ArrayList();
        Iterator i = questionPoolItems.iterator();
        while (i.hasNext()) {
          QuestionPoolItemData questionPoolItem = (QuestionPoolItemData) i.next();
          ItemData itemData_0 = (ItemData) h.get(questionPoolItem.getItemId());
          Set itemTextSet = itemData_0.getItemTextSet();
          Iterator k = itemTextSet.iterator();
          while (k.hasNext()) {
            ItemText itemText = (ItemText) k.next();
          }
          itemArrayList.add(itemData_0);
        }
        qpp.setQuestions(itemArrayList);
        qpp.setSubPoolSize(new Integer(getSubPoolSize(qpp.getQuestionPoolId())));
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
       * This method returns an ItemFacade that we can use to construct our ItemImpl
   */
  public ItemFacade getItem(String id) {
    ItemData item = (ItemData) getHibernateTemplate().load(ItemData.class, id);
    return new ItemFacade(item);
  }

  /**
   * Get a pool based on poolId. I am not sure why agent is not used though is being parsed.
   *
   * @param poolid DOCUMENTATION PENDING
   * @param agent DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public QuestionPoolFacade getPool(Long poolId, String agent) {
    try {
      QuestionPoolData qpp = (QuestionPoolData) getHibernateTemplate().load(
          QuestionPoolData.class, poolId);
      // setAccessType
      setPoolAccessType(qpp, agent);
      // QuestionPoolItemData's identifier is a compsite identifier made up of
      // poolId and itemId <-- is regarded as "legacy DB" in Hibernate language.
      // We need to construct the properties for such as object ourselves.
      populateQuestionPoolItemDatas(qpp);
      return getQuestionPool(qpp);
    }
    catch (Exception e) {
      log.error(e);
      return null;
    }
  }

  public void setPoolAccessType(QuestionPoolData qpp, String agentId) {
    try {
      QuestionPoolAccessData qpa = getQuestionPoolAccessData(qpp.
          getQuestionPoolId(), agentId);
      if (qpa == null) {
        // if (qpa == null), take what is set for pool.
      }
      else {
        qpp.setAccessTypeId(qpa.getAccessTypeId());
      }
    }
    catch (Exception e) {
      log.warn(e.getMessage());
    }
  }

  public QuestionPoolAccessData getQuestionPoolAccessData(Long poolId,
      String agentId) {
    List list = getHibernateTemplate().find("from QuestionPoolAccessData as qpa where qpa.questionPoolId =? and qpa.agentId=?",
                                            new Object[] {poolId, agentId}
                                            ,
                                            new net.sf.hibernate.type.Type[] {Hibernate.
                                            LONG, Hibernate.STRING});
    return (QuestionPoolAccessData) list.get(0);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param ids DOCUMENTATION PENDING
   * @param sectionId DOCUMENTATION PENDING
   */
  public void addItemsToSection(Collection ids, long sectionId) {

  }

  /**
   * add a question to a pool
   *
   * @param itemId DOCUMENTATION PENDING
   * @param poolId DOCUMENTATION PENDING
   */
  public void addItemToPool(QuestionPoolItemData qpi) {
    int retryCount = PersistenceService.getInstance().getRetryCount().intValue();
    while (retryCount > 0){
      try {
        getHibernateTemplate().save(qpi);
        retryCount = 0;
      }
      catch (Exception e) {
        log.warn("problem saving item to pool: "+e.getMessage());
        retryCount = PersistenceService.getInstance().retryDeadlock(e, retryCount);
      }
    }


  }

  /**
   * Delete pool and questions attached to it plus any subpool under it
   *
   * @param itemId DOCUMENTATION PENDING
   * @param poolId DOCUMENTATION PENDING
   */
  public void deletePool(final Long poolId, String agent, Tree tree) {
    try {
        QuestionPoolData questionPool = (QuestionPoolData) getHibernateTemplate().load(QuestionPoolData.class, poolId);

      // #1. delete all questions which mean AssetBeanie (not ItemImpl) 'cos AssetBeanie
      // is the one that is associated with the DB
      // lydial:  getting list of items that only belong to this pool and not linked to any assessments. 
      List itemList = getAllItemsInThisPoolOnlyAndDetachFromAssessment(poolId);

      int retryCount = PersistenceService.getInstance().getRetryCount().intValue();
      while (retryCount > 0){
        try {
          getHibernateTemplate().deleteAll(itemList); // delete all AssetBeanie
          retryCount = 0;
        }
        catch (Exception e) {
          log.warn("problem delete all items in pool: "+e.getMessage());
          retryCount = PersistenceService.getInstance().retryDeadlock(e, retryCount);
        }
      }


      // #2. delete question and questionpool map.
      retryCount = PersistenceService.getInstance().getRetryCount().intValue();
      while (retryCount > 0){
        try {
          final HibernateCallback hcb = new HibernateCallback(){
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
              Query q = session.createQuery("select qpi from QuestionPoolItemData as qpi where qpi.questionPoolId= ?");
              q.setLong(0, poolId.longValue());
              return q.list();
    	    };
          };
          List list = getHibernateTemplate().executeFind(hcb);

          // a. delete item and pool association in SAM_ITEMMETADATA_T - this is the primary
          // pool that item is attached to
          ArrayList metaList = new ArrayList();
          for (int j=0; j<list.size(); j++){
            String itemId = ((QuestionPoolItemData)list.get(j)).getItemId();
            String query = "from ItemMetaData as meta where meta.item.itemId="+itemId+
              " and meta.label='"+ItemMetaDataIfc.POOLID+"'";
            List m = getHibernateTemplate().find(query);
            if (m.size()>0){
              ItemMetaDataIfc meta = (ItemMetaDataIfc)m.get(0);
              meta.setItem(null);
              metaList.add(meta);
	    }
          }
          try{
            getHibernateTemplate().deleteAll(metaList);
            retryCount = 0;
	  }
          catch (Exception e) {
            log.warn("problem delete question and questionpool map inside itemMetaData: "+e.getMessage());
            retryCount = PersistenceService.getInstance().retryDeadlock(e, retryCount);
          }

          // b. delete item and pool association in SAM_QUESTIONPOOLITEM_T
          if (list.size() > 0) {
            questionPool.setQuestionPoolItems(new HashSet());
            getHibernateTemplate().deleteAll(list);
            retryCount = 0;
          }
          else retryCount = 0;
        }
        catch (Exception e) {
          log.warn("problem delete question and questionpool map: "+e.getMessage());
          retryCount = PersistenceService.getInstance().retryDeadlock(e, retryCount);
        }
      }

      // #3. Pool is owned by one but can be shared by multiple agents. So need to
      // delete all QuestionPoolAccessData record first. This seems to be missing in Navigo, nope? - daisyf
      // Actually, I don't think we have ever implemented sharing between agents. So we may wnat to
      // clean up this bit of code - daisyf 07/07/06
      final HibernateCallback hcb = new HibernateCallback(){
    	public Object doInHibernate(Session session) throws HibernateException, SQLException {
          Query q = session.createQuery("select qpa from QuestionPoolAccessData as qpa where qpa.questionPoolId= ?");
          q.setLong(0, poolId.longValue());
          return q.list();
    	};
      };
      List qpaList = getHibernateTemplate().executeFind(hcb);
      retryCount = PersistenceService.getInstance().getRetryCount().intValue();
      while (retryCount > 0){
        try {
          getHibernateTemplate().deleteAll(qpaList);
          retryCount = 0;
        }
        catch (Exception e) {
          log.warn("problem delete question pool access data: "+e.getMessage());
          retryCount = PersistenceService.getInstance().retryDeadlock(e, retryCount);
        }
      }

      // #4. Ready! delete pool now
      final HibernateCallback hcb2 = new HibernateCallback(){
    	public Object doInHibernate(Session session) throws HibernateException, SQLException {
    		Query q = session.createQuery("select qp from QuestionPoolData as qp where qp.id= ?");
    		q.setLong(0, poolId.longValue());
    		return q.list();
    	};
      };
      List qppList = getHibernateTemplate().executeFind(hcb2);
      retryCount = PersistenceService.getInstance().getRetryCount().intValue();
      while (retryCount > 0){
        try {
          getHibernateTemplate().deleteAll(qppList);
          retryCount = 0;
        }
        catch (Exception e) {
          log.warn("problem delete all pools: "+e.getMessage());
          retryCount = PersistenceService.getInstance().retryDeadlock(e, retryCount);
        }
      }

      // #5. delete all subpools if any, this is recursive
      Iterator citer = (tree.getChildList(poolId)).iterator();
      while (citer.hasNext()) {
        deletePool( (Long) citer.next(), agent, tree);
      }
    }
    catch (Exception e) {
      log.warn("error deleting pool. " + e.getMessage());
    }
  }

  /**
   * Move pool under another pool. The dest pool must not be the
   * descendant of the source nor can they be the same pool .
   */
  public void movePool(String agentId, Long sourcePoolId, Long destPoolId) {
    try {

      QuestionPoolFacade sourcePool = getPool(sourcePoolId, agentId);
      if (destPoolId.equals(QuestionPoolFacade.ROOT_POOL) &&
          !sourcePoolId.equals(QuestionPoolFacade.ROOT_POOL)) {
        sourcePool.setParentPoolId(QuestionPoolFacade.ROOT_POOL);
    int retryCount = PersistenceService.getInstance().getRetryCount().intValue();
    while (retryCount > 0){
      try {
        getHibernateTemplate().update( (QuestionPoolData) sourcePool.getData());
        retryCount = 0;
      }
      catch (Exception e) {
        log.warn("problem moving pool: "+e.getMessage());
        retryCount = PersistenceService.getInstance().retryDeadlock(e, retryCount);
      }
    }
      }
      else {
        QuestionPoolFacade destPool = getPool(destPoolId, agentId);
        sourcePool.setParentPoolId(destPool.getQuestionPoolId());
    int retryCount = PersistenceService.getInstance().getRetryCount().intValue();
    while (retryCount > 0){
      try {
        getHibernateTemplate().update( (QuestionPoolData) sourcePool.getData());
        retryCount = 0;
      }
      catch (Exception e) {
        log.warn("problem update source pool: "+e.getMessage());
        retryCount = PersistenceService.getInstance().retryDeadlock(e, retryCount);
      }
    }
      }
    }
    catch (Exception e) {
      log.warn(e.getMessage());
    }
  }

  /**
   * Is destination a descendant of the source?
   */
  public boolean isDescendantOf(QuestionPoolFacade destPool,
                                QuestionPoolFacade sourcePool) {

    Long tempPoolId = destPool.getQuestionPoolId();
    try {
      while((tempPoolId != null) &&
        (!tempPoolId.equals(QuestionPoolFacade.ROOT_POOL)))
      {
        QuestionPoolFacade tempPool = getPoolById(tempPoolId);
        if (tempPool.getParentPoolId().equals(sourcePool.getQuestionPoolId())) {
          return true;
        }
      tempPoolId = tempPool.getParentPoolId();
      }
      return false;
    }
    catch (Exception e) {
      log.warn(e.getMessage());
      return false;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param itemId DOCUMENTATION PENDING
   * @param poolId DOCUMENTATION PENDING
   */
  public void removeItemFromPool(String itemId, Long poolId) {
    QuestionPoolItemData qpi = new QuestionPoolItemData(poolId, itemId);
    int retryCount = PersistenceService.getInstance().getRetryCount().intValue();
    while (retryCount > 0){
      try {
        getHibernateTemplate().delete(qpi);
        retryCount = 0;
      }
      catch (Exception e) {
        log.warn("problem delete item from pool: "+e.getMessage());
        retryCount = PersistenceService.getInstance().retryDeadlock(e, retryCount);
      }
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param itemId DOCUMENTATION PENDING
   * @param poolId DOCUMENTATION PENDING
   */
  public void moveItemToPool(String itemId, Long sourceId, Long destId) {
    QuestionPoolItemData qpi = new QuestionPoolItemData(sourceId, itemId);
    int retryCount = PersistenceService.getInstance().getRetryCount().intValue();
    while (retryCount > 0){
      try {
        getHibernateTemplate().delete(qpi);
        retryCount = 0;
      }
      catch (Exception e) {
        log.warn("problem delete old mapping: "+e.getMessage());
        retryCount = PersistenceService.getInstance().retryDeadlock(e, retryCount);
      }
    }
    QuestionPoolItemData qpi2 = new QuestionPoolItemData(destId, itemId);
    retryCount = PersistenceService.getInstance().getRetryCount().intValue();
    while (retryCount > 0){
      try {
        getHibernateTemplate().save(qpi2);
        retryCount = 0;
      }
      catch (Exception e) {
        log.warn("problem saving new mapping: "+e.getMessage());
        retryCount = PersistenceService.getInstance().retryDeadlock(e, retryCount);
      }
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pool DOCUMENTATION PENDING
   */
  public QuestionPoolFacade savePool(QuestionPoolFacade pool) {
    boolean insert = false;
    try {
      QuestionPoolData qpp = (QuestionPoolData) pool.getData();
      qpp.setLastModified(new Date());
      qpp.setLastModifiedById(AgentFacade.getAgentString());
      if (qpp.getQuestionPoolId() == null ||
          qpp.getQuestionPoolId().equals(new Long("0"))) { // indicate a new pool
        insert = true;
      }
    int retryCount = PersistenceService.getInstance().getRetryCount().intValue();
    while (retryCount > 0){
      try {
        getHibernateTemplate().saveOrUpdate(qpp);
        retryCount = 0;
      }
      catch (Exception e) {
        log.warn("problem saving Or Update pool: "+e.getMessage());
        retryCount = PersistenceService.getInstance().retryDeadlock(e, retryCount);
      }
    }

      if (insert) {
        // add a QuestionPoolAccessData record for the owner who should have ADMIN access to the pool
        QuestionPoolAccessData qpa = new QuestionPoolAccessData(qpp.
            getQuestionPoolId(), qpp.getOwnerId(), QuestionPoolData.ADMIN);
    retryCount = PersistenceService.getInstance().getRetryCount().intValue();
    while (retryCount > 0){
      try {
        getHibernateTemplate().save(qpa);
        retryCount = 0;
      }
      catch (Exception e) {
        log.warn("problem saving pool: "+e.getMessage());
        retryCount = PersistenceService.getInstance().retryDeadlock(e, retryCount);
      }
    }
      }
      return pool;
    }
    catch (Exception e) {
      log.warn(e.getMessage());
      return null;
    }
  }

  /**
   * Get all the children pools of a pool. Return a list of QuestionPoolData
   * should return QuestionPool instead - need fixing, daisyf
   *
   * @param itemId DOCUMENTATION PENDING
   * @param poolId DOCUMENTATION PENDING
   */

  public List getSubPools(Long poolId) {
    return getHibernateTemplate().find(
        "from QuestionPoolData as qpp where qpp.parentPoolId=?",
        new Object[] {poolId}
        , new net.sf.hibernate.type.Type[] {Hibernate.LONG});
    //return new ArrayList();
  }

  public int getSubPoolSize(Long poolId) {
    return getSubPools(poolId).size();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param itemId DOCUMENTATION PENDING
   * @param poolId DOCUMENTATION PENDING
   */

  public boolean hasSubPools(Long poolId) {
    List subPools =
        getHibernateTemplate().find(
        "from QuestionPoolData as qpp where qpp.parentPoolId=?",
        new Object[] {poolId}
        , new net.sf.hibernate.type.Type[] {Hibernate.LONG});
    if (subPools.size() > 0) {
      return true;
    }
    else {
      return false;
    }
  }


public boolean poolIsUnique(Long questionPoolId, String title, Long parentPoolId) {
    
     List list = getHibernateTemplate().find(
        "select new QuestionPoolData(a.questionPoolId, a.title, a.parentPoolId)from QuestionPoolData a where a.questionPoolId!= ? and a.title=? and a.parentPoolId=?",
        new Object[] {questionPoolId,title,parentPoolId}
       , new net.sf.hibernate.type.Type[] {Hibernate.LONG,Hibernate.STRING, Hibernate.LONG});
 if(list.size()>0)
     return false;
 else return true;
 

}


  /**
   * Return a list of questionPoolId (java.lang.Long)
   *
   * @param itemId DOCUMENTATION PENDING
   * @param poolId DOCUMENTATION PENDING
   */

  public List getPoolIdsByAgent(String agentId) {
    ArrayList idList = new ArrayList();
    List qpaList = getHibernateTemplate().find(
        "select qpa from QuestionPoolAccessData as qpa where qpa.agentId= ?",
        new Object[] {agentId}
        , new net.sf.hibernate.type.Type[] {Hibernate.STRING});
    try {
      Iterator iter = qpaList.iterator();
      while (iter.hasNext()) {
        QuestionPoolAccessData qpa = (QuestionPoolAccessData) iter.next();
        idList.add(qpa.getQuestionPoolId()); // return a list of poolId (java.lang.Long)
      }
      return idList;
    }
    catch (Exception e) {
      return null;
    }
  }

  /**
   * Return a list of questionPoolId (java.lang.Long)
   *
   * @param itemId DOCUMENTATION PENDING
   * @param poolId DOCUMENTATION PENDING
   */

  public List getPoolIdsByItem(String itemId) {
    ArrayList idList = new ArrayList();
    List qpiList = getHibernateTemplate().find(
        "select qpi from QuestionPoolItemData as qpi where qpi.itemId= ?",
        new Object[] {itemId}
        , new net.sf.hibernate.type.Type[] {Hibernate.STRING});
    try {
      Iterator iter = qpiList.iterator();
      while (iter.hasNext()) {
        QuestionPoolItemData qpa = (QuestionPoolItemData) iter.next();
        idList.add(qpa.getQuestionPoolId()); // return a list of poolId (java.lang.Long)
      }
      return idList;
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }


  /**
   * Copy a pool to a new location.
   */
  public void copyPool(Tree tree, String agentId, Long sourceId,
                       Long destId) {
    try {
      boolean haveCommonRoot = false;
      boolean duplicate = false;

      // Get the Pools
      QuestionPoolFacade oldPool = getPool(sourceId, agentId);
      String oldPoolName= oldPool.getDisplayName();

      // Are we creating a duplicate under the same parent?
      if (destId.equals(oldPool.getParentPoolId())) {
        duplicate = true;
      }

      // Determine if the Pools are in the same tree
      // If so, make sure the source level is not higher(up the tree)
      // than the dest. to avoid the endless loop.
      if (!duplicate) {
        haveCommonRoot = tree.haveCommonRoot(sourceId, destId);
      }

      if (haveCommonRoot &&
          (tree.poolLevel(sourceId) <=
           tree.poolLevel(destId))) {
        return; // Since otherwise it would cause an infinite loop.
        // We should revisit this.
      }

      QuestionPoolFacade newPool = (QuestionPoolFacade) oldPool.clone();
      newPool.setParentPoolId(destId);
      newPool.setQuestionPoolId(new Long(0));
      Set questionSet = newPool.getQuestionPoolItems();
      newPool.setQuestionPoolItems(new HashSet());

      // If Pools in same trees,
      if (!haveCommonRoot) {
        // If Pools in different trees,
        // Copy to a Pool outside the same root
        // Copy *this* Pool first
        if (duplicate) 
          resetTitle(destId, newPool, oldPoolName);
        else 
          newPool.updateDisplayName(oldPoolName);
      }

      newPool = savePool(newPool);
      // then save question to pool
      Set newQuestionSet = prepareQuestions(newPool.getQuestionPoolId(), questionSet);
      saveQuestionSet(newQuestionSet);

      // Get the SubPools of oldPool
      Iterator citer = (tree.getChildList(sourceId)).iterator();
      while (citer.hasNext()) {
        Long childPoolId = (Long) citer.next();
        copyPool(tree, agentId, childPoolId, newPool.getQuestionPoolId());
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws DataFacadeException {
    QuestionPoolFacadeQueriesAPI instance = new QuestionPoolFacadeQueries();
    // add an item
    if (args[0].equals("add")) {
      Long questionPoolId = instance.add();
    }
    if (args[0].equals("getQPItems")) {
      List items = instance.getAllItems(new Long(args[1])); // poolId
      for (int i = 0; i < items.size(); i++) {
        ItemData item = (ItemData) items.get(i);

      }
    }
    System.exit(0);
  }

  public Long add() {
    QuestionPoolData questionPool = new QuestionPoolData();
    questionPool.setTitle("Daisy Happy Pool");
    questionPool.setOwnerId("1");
    questionPool.setDateCreated(new Date());
    questionPool.setLastModifiedById("1");
    questionPool.setLastModified(new Date());
    getHibernateTemplate().save(questionPool);
    return questionPool.getQuestionPoolId();
  }

  public QuestionPoolFacade getPoolById(Long questionPoolId) {
    QuestionPoolFacade questionPoolFacade = null;
    try {
      if (!questionPoolId.equals(QuestionPoolFacade.ROOT_POOL)) {
        QuestionPoolData questionPool = (QuestionPoolData) getHibernateTemplate().
            load(QuestionPoolData.class, questionPoolId);
        if (questionPool != null) {
          questionPoolFacade = new QuestionPoolFacade(questionPool);
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return questionPoolFacade;
  }

  public HashMap getQuestionPoolItemMap(){
    HashMap h = new HashMap();
    String query = "from QuestionPoolItemData";
    List l = getHibernateTemplate().find(query);
    for (int i = 0; i < l.size(); i++) {
      QuestionPoolItemData q = (QuestionPoolItemData) l.get(i);
      h.put(q.getItemId(), q);
    }
    return h;
  }

  public HashSet prepareQuestions(Long questionPoolId, Set questionSet){
    System.out.println("**** no of question="+questionSet.size());
    HashSet set = new HashSet();
    Iterator iter = questionSet.iterator();
    while (iter.hasNext()){
      QuestionPoolItemData i = (QuestionPoolItemData)iter.next();
      System.out.println(questionPoolId.intValue()+":"+i.getItemId());
      set.add(new QuestionPoolItemData(questionPoolId, i.getItemId()));
    }
    System.out.println("**** no of question adjusted="+set.size());
    return set;
  }

  private void resetTitle(Long destId, QuestionPoolFacade newPool, String oldPoolName){
    //find name by loop through sibslings
    List siblings=getSubPools(destId);
    boolean subVersion=true;
    int num=0;
    int indexNum=0;
    int maxNum=0;
    for (int l = 0; l < siblings.size(); l++) {
       QuestionPoolData a = (QuestionPoolData)siblings.get(l);
       String n=a.getTitle();
       if(n.startsWith("Copy of ")){
         if(n.equals("Copy of "+oldPoolName)){
           if (maxNum<1) maxNum=1;
         }
       }
       if(n.startsWith("Copy(")){
         indexNum=n.indexOf(")",4);
         if(indexNum>5){
	   try{
             num=Integer.parseInt(n.substring(5,indexNum));
             if(oldPoolName.equals(n.substring(indexNum+5).trim())){
               if (num>maxNum) maxNum=num;
             }
	   }
	   catch(NumberFormatException e){
             log.warn("rename title of duplicate pool:"+ e.getMessage());
	   }
       }
     }
   }
   if(maxNum==0)
     newPool.updateDisplayName("Copy of "+oldPoolName);
   else
     newPool.updateDisplayName("Copy("+(maxNum+1)+") of "+oldPoolName);
  }

  private void saveQuestionSet(Set newQuestionSet) {
    Iterator iter = newQuestionSet.iterator();
    while (iter.hasNext()){
      QuestionPoolItemData i = (QuestionPoolItemData)iter.next();
      int retryCount = PersistenceService.getInstance().getRetryCount().intValue();
      while (retryCount > 0){
        try {
	  getHibernateTemplate().save(i);
	  retryCount = 0;
        }
        catch (Exception e) {
	  log.warn("problem saving question: "+e.getMessage());
	  retryCount = PersistenceService.getInstance().retryDeadlock(e, retryCount);
        }
      }
    }
  }

}
