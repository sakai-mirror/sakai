/*
 *                       Navigo Software License
 *
 * Copyright 2003, Trustees of Indiana University, The Regents of the University
 * of Michigan, and Stanford University, all rights reserved.
 *
 * This work, including software, documents, or other related items (the
 * "Software"), is being provided by the copyright holder(s) subject to the
 * terms of the Navigo Software License. By obtaining, using and/or copying this
 * Software, you agree that you have read, understand, and will comply with the
 * following terms and conditions of the Navigo Software License:
 *
 * Permission to use, copy, modify, and distribute this Software and its
 * documentation, with or without modification, for any purpose and without fee
 * or royalty is hereby granted, provided that you include the following on ALL
 * copies of the Software or portions thereof, including modifications or
 * derivatives, that you make:
 *
 *    The full text of the Navigo Software License in a location viewable to
 *    users of the redistributed or derivative work.
 *
 *    Any pre-existing intellectual property disclaimers, notices, or terms and
 *    conditions. If none exist, a short notice similar to the following should
 *    be used within the body of any redistributed or derivative Software:
 *    "Copyright 2003, Trustees of Indiana University, The Regents of the
 *    University of Michigan and Stanford University, all rights reserved."
 *
 *    Notice of any changes or modifications to the Navigo Software, including
 *    the date the changes were made.
 *
 *    Any modified software must be distributed in such as manner as to avoid
 *    any confusion with the original Navigo Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) and/or Indiana University,
 * The University of Michigan, Stanford University, or Navigo may NOT be used
 * in advertising or publicity pertaining to the Software without specific,
 * written prior permission. Title to copyright in the Software and any
 * associated documentation will at all times remain with the copyright holders.
 * The export of software employing encryption technology may require a specific
 * license from the United States Government. It is the responsibility of any
 * person or organization contemplating export to obtain such a license before
 * exporting this Software.
 */

package org.navigoproject.business.entity.questionpool.model;

import org.navigoproject.business.entity.AAMTree;
import org.navigoproject.business.entity.BeanSort;
import org.navigoproject.business.entity.DataFacade;
import org.navigoproject.business.entity.QuestionPoolException;
import org.navigoproject.osid.OsidManagerFactory;
import org.navigoproject.osid.questionpool.impl.QuestionPoolImpl;
import org.navigoproject.osid.questionpool.impl.QuestionPoolIteratorImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.text.*;

import org.apache.commons.beanutils.BeanUtils;

import org.apache.log4j.Logger;

import osid.OsidException;

import osid.shared.*;

/**
 * DOCUMENTATION PENDING
 *
 * @author $author$
 * @version $Id: QuestionPoolTreeImpl.java,v 1.4 2004/10/15 21:50:26 lydial.stanford.edu Exp $
 */
public class QuestionPoolTreeImpl
  implements AAMTree
{
  private static final Logger LOG =
    Logger.getLogger(QuestionPoolTreeImpl.class.getName());
  private HashMap poolMap;
  private HashMap poolFamilies;
  private Id currentPoolId;
  private String sortString = "lastModified";

  public QuestionPoolTreeImpl()
  {
    poolMap = new HashMap();
    poolFamilies = new HashMap();
  }

  /**
   * Constucts the representation of the tree of pools.
   * @param iter QuestionPoolIteratorImpl for the pools in question
   */
  public QuestionPoolTreeImpl(QuestionPoolIteratorImpl iter)
  {
    // this is a table of pools by Id
    poolMap = new HashMap();

    // this is a cross reference of pool ids by parent id
    // the pool ids in an Arraylist where the key is parent id
    poolFamilies = new HashMap();

    try
    {
      while(iter.hasNext())
      {
        QuestionPoolImpl pool = (QuestionPoolImpl) iter.next();
        SharedManager sm = OsidManagerFactory.createSharedManager();
        Id parentId = pool.getParentId();
        if(parentId == null)
        {
          parentId = sm.getId("0");
        }

        Id poolId = pool.getId();
        poolMap.put(poolId.toString(), pool);
        ArrayList childList = new ArrayList();
        if(poolFamilies.containsKey(parentId.toString()))
        {
          childList = (ArrayList) poolFamilies.get(parentId.toString());
        }

        childList.add(poolId);
        poolFamilies.put(parentId.toString(), childList);
      }

      // Now sort the sibling lists.
      Iterator iter2 = poolFamilies.keySet().iterator();
      while(iter2.hasNext())
      {
        String key = (String) iter2.next();
        Iterator children = ((ArrayList) poolFamilies.get(key)).iterator();
        Collection sortedList = new ArrayList();
        while(children.hasNext())
        {
          QuestionPool pool =
            (QuestionPool) poolMap.get(children.next().toString());
          DataFacade data = new DataFacade(pool, pool.getData());
          sortedList.add(data);
        }

        BeanSort sort = new BeanSort(sortedList, sortString);
        sortedList = sort.sort();
        Collection ids = new ArrayList();
        Iterator siblings = sortedList.iterator();
        while(siblings.hasNext())
        {
          ids.add(
            ((QuestionPool) poolMap.get(
              ((DataFacade) siblings.next()).get("id"))).getId());
        }

        poolFamilies.put(key, ids);
      }
    }
    catch(OsidException ex)
    {
      LOG.error("Unable to complete lists." + ex);
    }
  }

  /**
   * Get a List of pools having parentId as parent
   * @param parentId the Id of the parent pool
   * @return a List with the Ids of all momma's children
   */
  public List getChildList(Id parentId)
  {
    if(! poolFamilies.containsKey(parentId.toString()))
    {
      return new ArrayList();
    }

    return (ArrayList) poolFamilies.get(parentId.toString());
  }

  /**
   * Get a List of top level pools.
   * @return List of top level pool id strings
   */
  public List getRootNodeList()
  {
    try
    {
      SharedManager sm = OsidManagerFactory.createSharedManager();

      return getChildList(sm.getId("0"));
    }
    catch(Exception e)
    {
      LOG.error(e); 
//      throw new Error(e);

      return null;
    }
  }

  /**
   * Get the object we're currently looking at.
   *
   * @return Current QuestionPool.
   */
  public Object getCurrentObject()
  {
    return poolMap.get(currentPoolId.toString());
  }

  /**
   * Get the parent of the object we're currently looking at.
   *
   * @return The parent pool of the current object, or null if
   * it's a root node.
   */
  public Object getParent()
  {
    if(currentPoolId == null)
    {
      return null;
    }

    QuestionPool current = (QuestionPool) getCurrentObject();
    try
    {
      return (poolMap.get(current.getParentId().toString()));
    }
    catch(Exception e)
    {
      LOG.error(e); 
//      throw new Error(e);

      return null;
    }
  }

  /**
   * Get the HTML id of the current object.
   *
   * @return An HTML representation of the pool Id.
   */
  public String getCurrentObjectHTMLId()
  {
    QuestionPool current = (QuestionPool) getCurrentObject();
    try
    {
      QuestionPool parent = (QuestionPool) getParent();
      if(parent == null)
      {
        SharedManager sm = OsidManagerFactory.createSharedManager();
        Collection childList = getChildList(sm.getId("0"));

        return new Integer(
          ((ArrayList) childList).indexOf(
            ((QuestionPool) getCurrentObject()).getId()) + 1).toString();
      }
      else
      {
        setCurrentId(current.getParentId());
        String result = getCurrentObjectHTMLId();
        Collection childList = getChildList(parent.getId());
        setCurrentId(current.getId());

        return result + "-" +
        (
          ((ArrayList) childList).indexOf(
            ((QuestionPool) getCurrentObject()).getId()) + 1
        );
      }
    }
    catch(Exception e)
    {
      LOG.error(e); 
//      throw new Error(e);

      return "0";
    }
  }

  /**
   * Get the current level.
   *
   * @return A String that represents the level we're on (1 is root node,
   * 2 is first level child, etc..
   */
  public String getCurrentLevel()
  {
    int index1 = 1;
    QuestionPool current = (QuestionPool) getCurrentObject();
    try
    {
      while(! current.getParentId().toString().equals("0"))
      {
        current = (QuestionPool) poolMap.get(current.getParentId().toString());
        index1++;
      }

      QuestionPool parent = (QuestionPool) getParent();
    }
    catch(Exception e)
    {
      LOG.error(e); 
//      throw new Error(e);

      return "0";
    }

    return new Integer(index1).toString();
  }

  /**
   * Return in order the current:<ul>
   * <li> Pool Name
   * <li> Author
   * <li> Last Modified
   * <li> Total # of Questions
   * <li> Total # of Subpools
   */
  public Collection getCurrentObjectProperties()
  {
    Collection properties = new ArrayList();
    if(currentPoolId == null)
    {
      properties.add("Pool Name");
      properties.add("Author");
      properties.add("Last Modified");
      properties.add("Total # of Questions");
      properties.add("Total # of Subpools");
    }
    else
    {
      try
      {
        QuestionPool pool = (QuestionPool) getCurrentObject();
        QuestionPoolProperties props = (QuestionPoolProperties) pool.getData();
        if(props == null)
        {
          props = new QuestionPoolProperties();
        }

        //properties.add(pool.getDisplayName());
        properties.add(props.getOwner().getDisplayName());
        if(props.getLastModified() != null)
        {
        //  SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
         // properties.add(sdf.format(props.getLastModified()));
         properties.add(props.getLastModified());

        }
        else
        {
          properties.add("N/A");
        }

        if(props.getQuestions() != null)
        {
          properties.add(new Integer(props.getQuestions().size()).toString());
        }
        else
        {
          properties.add("0");
        }

        if(getChildren(currentPoolId) != null)
        {
          properties.add(
            new Integer(getChildren(currentPoolId).size()).toString());
        }
        else
        {
          properties.add("0");
        }
      }
      catch(Exception e)
      {
        LOG.error(e); throw new Error(e);
      }
    }

    return properties;
  }

  /**
   * Set the properties to request.  Not needed here.
   */
  public void setPropertyMethods(String[] methods)
  {
  }

  /**
   * Get Map of QuestionPoolImpls.
   * @return Map of all QuestionPoolImpls
   */
  public Map getAllObjects()
  {
    return poolMap;
  }

  /**
   * Dump the tree into a long collection of objects of the form:<ul>
   * <li> Pool 1
   * <ul> <li> Pool 2
   * <ul> <li>     Pool 3
   *      <li>     Pool 4
   * </ul><li> Pool 5
   *
   */
  public Collection getSortedObjects()
  {
    Collection total = new ArrayList();
    try
    {
      SharedManager sm = OsidManagerFactory.createSharedManager();
      addChildren(total, sm.getId("0"));
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }

    return total;
  }

  /**
   * get sorted objects for a subpool tree
   */
  public Collection getSortedObjects(Id poolId)
  {
    Collection total = new ArrayList();
    try
    {
      SharedManager sm = OsidManagerFactory.createSharedManager();
      addChildren(total, poolId);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }

    return total;
  }


  /**
   * Auxiliary method for recursion.
   */
  private void addChildren(Collection total, Id parentId)
  {
    List childList = getChildList(parentId);
    if(childList.isEmpty())
    {
      return;
    }

    Iterator iter = childList.iterator();
    while(iter.hasNext())
    {
      Id nextId = (Id) iter.next();
      total.add(poolMap.get(nextId.toString()));
      addChildren(total, nextId);
    }
  }

  /**
   * Get Map of QuestionPoolImpls retricted to a given
   * Parent Id string
   * @param parentId parent of all returned children
   * @return  a Map of QuestionPoolImpls that are childen
   */
  public Map getChildren(Id parentId)
  {
    HashMap childPool = new HashMap();
    List childList = getChildList(parentId);
    Iterator children = childList.iterator();

    while(children.hasNext())
    {
      Id poolId = (Id) children.next();
      childPool.put(poolId.toString(), poolMap.get(poolId.toString()));
    }

    return childPool;
  }

  /**
   * Get Map of QuestionPoolImpls retricted to childeren of the currently
   * selected pool Id string
   * @return  a Map of QuestionPoolImpls that are children
   */
  public Map getChildren()
  {
    return getChildren(getCurrentId());
  }

  /**
   * Obtain the poolId set as the current working poolId-designated node
   * @return the current working poolId String
   */
  public Id getCurrentId()
  {
    return currentPoolId;
  }

  /**
   * Set the current working poolId String, from which context relative
   * child lists are calculated from the poolId-designated node
   * @param poolId
   */
  public void setCurrentId(Id poolId)
  {
    currentPoolId = poolId;
  }

  /**
   * Determine if the pool has childeren
   * @return true if it has children
   */
  public boolean currentObjectIsParent()
  {
    return poolFamilies.containsKey(getCurrentId().toString());
  }

  /**
   * List the child pool id strings
   * @return a list of children's pool id strings
   */
  public List getChildList()
  {
    return getChildList(getCurrentId());
  }

  /**
   * This gets the property by which siblings will be sorted.
   *
   * @return A String representation of the sort property.
   */
  public String getSortProperty()
  {
    return sortString;
  }

  /**
   * This sets the property by which siblings will be sorted.
   */
  public void setSortProperty(String newProperty)
  {
    sortString = newProperty;
  }

  /**
   * THis checks to see if given two pools have a common ancestor
   */ 
  public boolean haveCommonRoot(Id poolIdA,Id poolIdB){
    try{
    Id rootA=poolIdA;
    Id rootB=poolIdB;
    
    QuestionPool tempPool=(QuestionPool)poolMap.get(rootA.toString());
    while(tempPool!=null){
      if((tempPool.getParentId()==null)||(((tempPool.getParentId()).toString()).equals("0"))){
        tempPool=null;
      }else{
        rootA = tempPool.getParentId();
        tempPool = (QuestionPool)poolMap.get(rootA.toString());
      }
    } 
    tempPool=(QuestionPool)poolMap.get(rootB.toString());
    while(tempPool!=null){
      if((tempPool.getParentId()==null)||(((tempPool.getParentId()).toString()).equals("0"))){
        tempPool=null;
      }else{
        rootB = tempPool.getParentId();
        tempPool = (QuestionPool)poolMap.get(rootB.toString());
      }
    } 
    return rootA.isEqual(rootB);
    }catch(Exception e){
      LOG.error(e); 
//      throw new Error(e);
      return false;
    }
  }

  /**
   * Is a pool a descendant of the other?
   */
  public boolean isDescendantOf(Id poolA,Id poolB)
  {
    try{
      Id tempPoolId = poolA;
      while((tempPoolId !=null)&&(tempPoolId.toString().compareTo("0")>0)){
        QuestionPool tempPool = (QuestionPool)poolMap.get(tempPoolId.toString());
        if(tempPool.getParentId().toString().compareTo(poolB.toString())==0) return true;
        tempPoolId = tempPool.getParentId();   
      }
      return false;
    
    }catch(Exception e){
      LOG.error(e); 
//      throw new Error(e);
      return false;
    }
  }

 /**
   * This returns the level of the pool inside a pool tree, Root being 0.
   */
  public int poolLevel(Id poolId){
    try{
    Id rootId=poolId;
    int level=0;

    QuestionPool tempPool=(QuestionPool)poolMap.get(rootId.toString());
    while(tempPool!=null){
      if((tempPool.getParentId()==null)||(((tempPool.getParentId()).toString()).equals("0"))){
        tempPool=null;
      }else{
        level++;
        rootId = tempPool.getParentId();
        tempPool = (QuestionPool)poolMap.get(rootId.toString());
      }
    }
    return level; 
    }catch(Exception e){
      LOG.error(e); 
//      throw new Error(e);
      return 0;
    }
  }
}
