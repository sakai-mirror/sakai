
/*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
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
*/

package org.sakaiproject.tool.assessment.queries;

import org.sakaiproject.tool.assessment.data.dao.questionpool.UtilAccessObject;
import org.navigoproject.business.entity.assessment.model.ItemIteratorImpl;
import org.sakaiproject.tool.assessment.business.entity.questionpool.*;
import org.navigoproject.data.GenericConnectionManager;
import org.navigoproject.osid.OsidManagerFactory;
import org.sakaiproject.tool.assessment.osid.questionpool_0_6.impl.QuestionPoolImpl;
import org.sakaiproject.tool.assessment.osid.questionpool_0_6.impl.QuestionPoolIteratorImpl;
import org.navigoproject.business.entity.assessment.model.ItemImpl;
import osid.assessment.AssessmentException;
import org.navigoproject.business.entity.Item;
import org.sakaiproject.tool.assessment.business.entity.AAMTree;

import org.sakaiproject.tool.assessment.data.dao.questionpool.QuestionPoolProperties;
import org.sakaiproject.tool.assessment.data.dao.questionpool.QuestionPoolAccess;
import org.sakaiproject.tool.assessment.data.dao.questionpool.QuestionPoolItem;
import org.sakaiproject.tool.assessment.data.dao.questionpool.AssetBeanie;

import java.sql.*;
import java.util.*;

import org.apache.log4j.Logger;

import osid.shared.Agent;
import osid.shared.Id;
import osid.shared.SharedManager;
import osid.shared.Type;

// added these in Aug 2004 when switching to spring dependency injection - daisyf
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import org.navigoproject.osid.impl.PersistenceService;
import net.sf.hibernate.Hibernate;
import org.navigoproject.osid.TypeLib;


/**
 * DOCUMENTATION PENDING
 *
 * @author $author$
 * @version $Id: QuestionPoolQueries.java,v 1.7 2004/10/12 15:15:26 daisyf.stanford.edu Exp $
 */
public class QuestionPoolQueries extends HibernateDaoSupport
{
  private static Logger LOG =
    Logger.getLogger(QuestionPoolQueries.class.getName());

  public QuestionPoolQueries()
  {
  }

  /**
   * Get a list of all the pools in the site. Note that questions in each pool will not
   * be populated. We must keep this list updated.
   */
  public List getAllPools()
  {
    return getHibernateTemplate().find("from QuestionPoolProperties");
  }

  /**
   * Get all the pools that the agent has access to. The easiest way seems to be
   * #1. get all the existing pool
   * #2. get all the QuestionPoolAccess record of the agent
   * #3. go through the existing pools and check it against the QuestionPoolAccess (qpa) record to see if
   * the agent is granted access to it. qpa record (if exists) always trumps the default access right set
   * up for a pool
   * e.g. if the defaultAccessType for a pool is ACCESS_DENIED but the qpa record say ADMIN, then access=ADMIN
   * e.g. if the defaultAccessType for a pool is ADMIN but the qpa record say ACCESS_DENIED, then access=ACCESS_DENIED
   * e.g. if no qpa record exists, then access rule will follow the defaultAccessType set by the pool
  */
  public QuestionPoolIterator getAllPools(String agentId)
  {
    ArrayList qpList = new ArrayList();

    // #1.
    List poolList = getAllPools(); // we initiate it when application restart already

    // #2. get all the QuestionPoolAccess record belonging to the agent
    List questionPoolAccessList = getHibernateTemplate().find("from QuestionPoolAccess as qpa where qpa.agentId=?",new Object[] { agentId }, new net.sf.hibernate.type.Type[] { Hibernate.STRING });
    System.out.println("*** all existing questionPoolAccess"+ questionPoolAccessList.size());
    HashMap h = new HashMap(); // prepare a hashMap with (poolId, qpa)
    Iterator i = questionPoolAccessList.iterator();
    while (i.hasNext()){
      QuestionPoolAccess qpa = (QuestionPoolAccess) i.next();
      h.put(qpa.getQuestionPoolId(), qpa);
    }

    // #3. We need to go through the existing QuestionPool and the QuestionPoolAccess record
    // to determine the access type
    try{
      Iterator j= poolList.iterator();
      while (j.hasNext()){
	QuestionPoolProperties qpp = (QuestionPoolProperties) j.next();
        // I really wish we don't need to populate  the questionpool size & subpool size for JSF
        // watch this for performance. hope Hibernate is smart enough not to load the entire question
        // - daisy, 10/04/04 
        populateQuestionPoolItems(qpp);

	QuestionPoolAccess qpa = (QuestionPoolAccess) h.get(qpp.getId());
    	if (qpa == null) {
          // if (qpa == null), take what is set for pool.
          if (qpp.getAccessTypeId() != QuestionPoolProperties.ACCESS_DENIED)
            qpList.add(getQuestionPoolImpl(qpp));
    	}
	else{
        if (qpa.getAccessTypeId() != QuestionPoolProperties.ACCESS_DENIED){
            qpp.setAccessTypeId(qpa.getAccessTypeId());
            qpList.add(getQuestionPoolImpl(qpp));
          }
        }
      }
    }
    catch (Exception e){
      LOG.warn(e.getMessage());
    }
    System.out.println("*****final pool size is ="+qpList.size());
    return new QuestionPoolIteratorImpl(qpList);
  }

  private QuestionPoolImpl getQuestionPoolImpl(QuestionPoolProperties qpp){
    try{
      SharedManager  sm = OsidManagerFactory.createSharedManager();
      QuestionPoolImpl qp = new QuestionPoolImpl(sm.getId(qpp.getId().toString()),sm.getId(qpp.getParentPoolId().toString()));
      qp.updateDisplayName(qpp.getTitle());
      qp.updateDescription(qpp.getDescription());
      TypeImplQueries typeImplQueries = PersistenceService.getInstance().getTypeImplQueries();
      qp.updateQuestionPoolType(typeImplQueries.getTypeImplByStanfordId(qpp.getTypeId()));
      qp.updateData(qpp);
      return qp;
    }
    catch (Exception e){
      LOG.warn(e.getMessage());
      return null;
    }
  }

  /**
   *  This is a bit mad and I want to explain what is going on here.
   *  All I want is to get a list of questions in a pool. getAllItems() return a list
   *  of AssetBeanie. AssetBeanie can be anything (e.g. question, assessment,..). In this case,
   *  it is a question. So we need to put a "mask" on AssetBeanie by constructing a ItemImpl.
   *  In the JSP page, they don't know what AssetBeanie is but they know about osid.assessment.Item
   *  of which ItemImpl implements. One more twist is that ItemImpl.getData() must return Item. Item
   *  is a class that is able to accept String and parse it and create a meaningful xmlDoc. - daisyf
   */
  private void populateQuestionPoolItems(QuestionPoolProperties qpp){
    try{
      SharedManager sm = OsidManagerFactory.createSharedManager();

      Set questionPoolItems = qpp.getQuestionPoolItems();
      if (questionPoolItems != null){
	// let's get all the items for the pool in one shot
	HashMap h = new HashMap();
	List itemList = getAllItems(qpp.getId());

	Iterator j = itemList.iterator();
	while (j.hasNext()){
          AssetBeanie assetBeanie = (AssetBeanie) j.next();
	  h.put(assetBeanie.getId(), assetBeanie);
	}
	ArrayList itemArrayList = new ArrayList();
	Iterator i = questionPoolItems.iterator();
	while (i.hasNext()){
	  QuestionPoolItem questionPoolItem = (QuestionPoolItem) i.next();
	  AssetBeanie assetBeanie = (AssetBeanie) h.get(questionPoolItem.getItemId());

	  ItemImpl item = new ItemImpl(sm.getId(assetBeanie.getId())); // Sigh...
          item.updateDisplayName(assetBeanie.getTitle());
          item.updateDescription(assetBeanie.getDescription());
          item.updateData(assetBeanie.getData());
          itemArrayList.add(item);
        }
        qpp.setQuestions(itemArrayList);
        qpp.setSubPoolSize(new Integer(getSubPoolSize(qpp.getId())));
        System.out.println("**** questionpool question size = " +qpp.getQuestionSize());
        System.out.println("**** questionpool subpool size = " +qpp.getSubPoolSize());
      }
    }
    catch (Exception e){
      System.out.println(e.getMessage());
    }
  }

  /**
   * This method return a List of AssetBeanie that we can use to construct our ItemImpl
   */
  public List getAllItems(Long questionPoolId)
  {
    return getHibernateTemplate().find("select ab from AssetBeanie as ab, QuestionPoolItem qpi where ab.id=qpi.itemId and qpi.questionPoolId = ?",new Object[] { questionPoolId }, new net.sf.hibernate.type.Type[] { Hibernate.LONG });
  }

  /**
   * This method return an AssetBeanie that we can use to construct our ItemImpl
   */
  public AssetBeanie getAsset(String id)
  {
    return (AssetBeanie)getHibernateTemplate().load(AssetBeanie.class, id);
  }

  /**
   * Get a pool based on poolId. I am not sure why agent is not used though is being parsed.
   *
   * @param poolid DOCUMENTATION PENDING
   * @param agent DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public QuestionPool getPool(Long poolId, String agent)
  {
    try{
      QuestionPoolProperties qpp = (QuestionPoolProperties) getHibernateTemplate().load(QuestionPoolProperties.class, poolId);
      // setAccessType
      setPoolAccessType(qpp, agent);
      // QuestionPoolItem's identifier is a compsite identifier made up of
      // poolId and itemId <-- is regarded as "legacy DB" in Hibernate language.
      // We need to construct the properties for such as object ourselves.
      populateQuestionPoolItems(qpp);
      return getQuestionPoolImpl(qpp);
    }
    catch(Exception e){
      LOG.error(e);
      return null;
    }
  }


  public void setPoolAccessType(QuestionPoolProperties qpp, String agentId)
  {
    try{
	QuestionPoolAccess qpa = getQuestionPoolAccess(qpp.getId(), agentId);
  	if (qpa == null) {
          // if (qpa == null), take what is set for pool.
    	}
	else{
            qpp.setAccessTypeId(qpa.getAccessTypeId());
        }
    }
    catch (Exception e){
      LOG.warn(e.getMessage());
    }
  }

  public QuestionPoolAccess getQuestionPoolAccess(Long poolId, String agentId)
  {
    List list = getHibernateTemplate().find("from QuestionPoolAccess as qpa where qpa.questionPoolId =? and qpa.agentId=?",new Object[] { poolId,  agentId }, new net.sf.hibernate.type.Type[] { Hibernate.LONG, Hibernate.STRING });
    return (QuestionPoolAccess)list.get(0);
  }


  /**
   * DOCUMENTATION PENDING
   *
   * @param ids DOCUMENTATION PENDING
   * @param sectionId DOCUMENTATION PENDING
   */
  public void addItemsToSection(Collection ids, long sectionId)
  {

  }


  /**
   * add a question to a pool
   *
   * @param itemId DOCUMENTATION PENDING
   * @param poolId DOCUMENTATION PENDING
   */
  public void addItemToPool(QuestionPoolItem qpi){
    getHibernateTemplate().save(qpi);
  }

  /**
   * Delete pool and questions attached to it plus any subpool under it
   *
   * @param itemId DOCUMENTATION PENDING
   * @param poolId DOCUMENTATION PENDING
   */
  public void deletePool(Id poolId, Agent agent, AAMTree tree)
  {
    try{
      Long qpId = new Long(poolId.getIdString());

      // I decided not to load the questionpool and delete things that associate with it
      // because question is associated with it as ItemImpl not AssetBeanie. To delete
      // AssetBeanie, I would still need to do it manually. I cannot find a way to take advantage of the
      // Hibernate cascade feature, can you ? - daisyf

      // #1. delete all questions which mean AssetBeanie (not ItemImpl) 'cos AssetBeanie
      // is the one that is associated with the DB
      List itemList = getAllItems(qpId);
      getHibernateTemplate().deleteAll(itemList); // delete all AssetBeanie

      // #2. delete question and questionpool map.
      // Sorry! delete(java.lang.String queryString, java.lang.Object[] values, net.sf.hibernate.type.Type[] types)
      // is not available in this version of Spring that we are using. So, we are a using a long winded method.
      getHibernateTemplate().deleteAll(getHibernateTemplate().find("select qpi from QuestionPoolItem as qpi where qpi.questionPoolId= ?",new Object[] { qpId }, new net.sf.hibernate.type.Type[] { Hibernate.LONG }));

      // #3. Pool is owned by one but can be shared by multiple agents. So need to
      // delete all QuestionPoolAccess record first. This seems to be missing in Navigo, nope? - daisyf
      List qpaList = getHibernateTemplate().find("select qpa from QuestionPoolAccess as qpa where qpa.questionPoolId= ?",new Object[] { qpId }, new net.sf.hibernate.type.Type[] { Hibernate.LONG });
      getHibernateTemplate().deleteAll(qpaList);

      // #4. Ready! delete pool now
      List qppList = getHibernateTemplate().find("select qp from QuestionPoolProperties as qp where qp.id= ?",new Object[] { qpId }, new net.sf.hibernate.type.Type[] { Hibernate.LONG }); // there should only be one
      getHibernateTemplate().deleteAll(qppList);

      // #5. delete all subpools if any, this is recursive
      Iterator citer = (tree.getChildList(poolId)).iterator();
      while(citer.hasNext())
      {
        deletePool((Id) citer.next(), agent, tree);
      }
    }
    catch(Exception e){
      LOG.warn(e.getMessage());
    }
  }

  /**
   * Move pool under another pool. The dest pool must not be the
   * descendant of the source nor can they be the same pool .
   */
  public void movePool(String agentId, Long sourcePoolId, Long destPoolId)
  {
    try{
      QuestionPool sourcePool = getPool(sourcePoolId, agentId);
      QuestionPool destPool = getPool(destPoolId, agentId);
      if (! isDescendantOf (destPool, sourcePool) && ! sourcePool.equals(destPool))
      {
        sourcePool.setParentId(destPool.getId());
        getHibernateTemplate().update((QuestionPoolProperties)sourcePool.getData());
      }
    }
    catch (Exception e){
      LOG.warn(e.getMessage());
    }
  }


  /**
   * Is destination a descendant of the source?
   */
  public boolean isDescendantOf(QuestionPool destPool, QuestionPool sourcePool)
  {
    boolean isDescendant = false;
    try{
      if (destPool.getParentId().equals(sourcePool.getId()))
        isDescendant = true;
    }
    catch (Exception e){
      LOG.warn(e.getMessage());
    }
    return isDescendant;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param itemId DOCUMENTATION PENDING
   * @param poolId DOCUMENTATION PENDING
   */
  public void removeItemFromPool(String itemId, Long poolId)
  {
    QuestionPoolItem qpi = new QuestionPoolItem(poolId, itemId);
    getHibernateTemplate().delete(qpi);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param itemId DOCUMENTATION PENDING
   * @param poolId DOCUMENTATION PENDING
   */
  public void moveItemToPool(String itemId, Long sourceId, Long destId)
  {
    QuestionPoolItem qpi = new QuestionPoolItem(sourceId, itemId);
    getHibernateTemplate().delete(qpi);
    QuestionPoolItem qpi2 = new QuestionPoolItem(destId, itemId);
    getHibernateTemplate().save(qpi2);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pool DOCUMENTATION PENDING
   */
  public QuestionPool savePool(QuestionPool pool)
  {
    boolean insert = false;
    try{
      QuestionPoolProperties qpp = (QuestionPoolProperties) pool.getData();
      if (qpp.getId() == null || qpp.getId().equals(new Long("0"))) // indicate a new pool
	  insert = true;
      getHibernateTemplate().saveOrUpdate(qpp);

      if (insert){
	// add a QuestionPoolAccess record for the owner who should have ADMIN access to the pool
        QuestionPoolAccess qpa = new QuestionPoolAccess(qpp.getId(), qpp.getOwnerId(), QuestionPoolProperties.ADMIN);
        getHibernateTemplate().save(qpa);
      }
      return pool;
    }
    catch(Exception e){
      LOG.warn(e.getMessage());
      return null;
    }
  }

  /**
   * Get all the children pools of a pool. Return a list of QuestionPoolProperties
   * should return QuestionPool instead - need fixing, daisyf
   *
   * @param itemId DOCUMENTATION PENDING
   * @param poolId DOCUMENTATION PENDING
   */

 public List getSubPools(Long poolId)
  {
    return getHibernateTemplate().find("from QuestionPoolProperties as qpp where qpp.parentPoolId=?",new Object[] { poolId }, new net.sf.hibernate.type.Type[] { Hibernate.LONG });
    //return new ArrayList();
  }

 public int getSubPoolSize(Long poolId)
  {
    return getSubPools(poolId).size();
  }


  /**
   * DOCUMENTATION PENDING
   *
   * @param itemId DOCUMENTATION PENDING
   * @param poolId DOCUMENTATION PENDING
   */

 public boolean hasSubPools(Long poolId)
  {
    List subPools =
      getHibernateTemplate().find("from QuestionPoolProperties as qpp where qpp.parentPoolId=?",new Object[] { poolId }, new net.sf.hibernate.type.Type[] { Hibernate.LONG });
    if (subPools.size() > 0)
      return true;
    else
      return false;
  }




  /**
   * Return a list of questionPoolId (java.lang.Long)
   *
   * @param itemId DOCUMENTATION PENDING
   * @param poolId DOCUMENTATION PENDING
   */

 public List getPoolIdsByAgent(String  agentId)
  {
    ArrayList idList = new ArrayList();
    List qpaList = getHibernateTemplate().find("select qpa from QuestionPoolAccess as qpa where qpa.agentId= ?",new Object[] { agentId }, new net.sf.hibernate.type.Type[] { Hibernate.STRING });
    try{
      SharedManager  sm = OsidManagerFactory.createSharedManager();
      Iterator iter = qpaList.iterator();
      while(iter.hasNext()){
        QuestionPoolAccess qpa = (QuestionPoolAccess) iter.next();
        idList.add(qpa.getQuestionPoolId()); // return a list of poolId (java.lang.Long)
      }
      return idList;
    }
    catch(Exception e){
      return null;
    }
  }




  /**
   * Return a list of questionPoolId (java.lang.Long)
   *
   * @param itemId DOCUMENTATION PENDING
   * @param poolId DOCUMENTATION PENDING
   */

 public List getPoolIdsByItem(String itemId)
  {
    ArrayList idList = new ArrayList();
    List qpiList = getHibernateTemplate().find("select qpi from QuestionPoolItem as qpi where qpi.itemId= ?",new Object[] { itemId }, new net.sf.hibernate.type.Type[] { Hibernate.STRING });
    try{
      SharedManager  sm = OsidManagerFactory.createSharedManager();
      Iterator iter = qpiList.iterator();
      while(iter.hasNext()){
        QuestionPoolAccess qpa = (QuestionPoolAccess) iter.next();
        idList.add(qpa.getQuestionPoolId()); // return a list of poolId (java.lang.Long)
      }
      return idList;
    }
    catch(Exception e){
      return null;
    }
  }

}
