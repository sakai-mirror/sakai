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

package org.sakaiproject.tool.assessment.osid.questionpool_0_6;

import org.sakaiproject.tool.assessment.business.entity.AAMTree;
import org.sakaiproject.tool.assessment.business.entity.questionpool.QuestionPool;
import org.sakaiproject.tool.assessment.business.entity.questionpool.QuestionPoolIterator;

import org.navigoproject.data.IdHelper;
import org.sakaiproject.tool.assessment.data.dao.questionpool.QuestionPoolAccessObject;
import org.navigoproject.osid.TypeLib;
import org.navigoproject.osid.assessment.impl.AssessmentManagerImpl;
import org.sakaiproject.tool.assessment.osid.questionpool_0_6.impl.QuestionPoolImpl;
import org.sakaiproject.tool.assessment.osid.questionpool_0_6.impl.QuestionPoolIteratorImpl;
import org.navigoproject.osid.OsidManagerFactory;
import org.navigoproject.ui.web.asi.author.item.EditItemHelper;
import org.navigoproject.ui.web.asi.author.item.ItemHelper;
import org.navigoproject.ui.web.asi.author.*;
import org.navigoproject.ui.web.asi.author.assessment.*;

import org.sakaiproject.tool.assessment.data.dao.questionpool.QuestionPoolProperties;

import java.io.*;
import javax.servlet.http.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.jdom.*;
import org.jdom.output.*;
import org.jdom.input.*;

import org.apache.log4j.Logger;

import osid.assessment.AssessmentManager;
import osid.assessment.Item;
import osid.assessment.*;

import osid.shared.Agent;
import osid.shared.Id;

/**
 * This is the QuestionPool factory.  It is called by the QuestionPool
 * ejb's, and it fills requests using the database accessor,
 * QuestionPoolAccessObject.
 *
 * @author Rachel Gollub <rgollub@stanford.edu>
 * @version goes here
 */
public class QuestionPoolFactory
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(QuestionPoolFactory.class);
    
  private static QuestionPoolFactory factory = null;

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public static QuestionPoolFactory getInstance()
  {
    if(factory == null)
    {
      factory = new QuestionPoolFactory();
    }

    return factory;
  }

  /**
   * Get a list of Question Pools from the database.
   *
   * @return A QuestionPoolIterator.
   */
  public QuestionPoolIterator getAllPools(Agent agent)
  {
    try
    {
      QuestionPoolAccessObject qpao = new QuestionPoolAccessObject();
      QuestionPoolIterator results = qpao.getAllPools(agent.getId().toString());

      return results;
    }
    catch(Exception e)
    {
      LOG.error(e); 
//      throw new Error(e);

      return new QuestionPoolIteratorImpl(new ArrayList());
    }
  }

  /**
   * Get a particular pool from the back end, with all questions.
   *
   * @return A QuestionPool.
   */
  public QuestionPool getPool(Id poolId, Agent agent)
  {
    try
    {
      QuestionPoolAccessObject qpao = new QuestionPoolAccessObject();
      QuestionPool pool =
        qpao.getPool(
          Integer.parseInt(poolId.toString()), agent.getDisplayName());

      return pool;
    }
    catch(Exception e)
    {
      LOG.error(e); 
//      throw new Error(e);

      return null;
    }
  }

 /**
   * Get a list of pools that share a specific Item, return their Ids
   *
   * @return A List.
   */
  public List getPoolIdsByItem(Id itemId)
  {
    try
    {
      QuestionPoolAccessObject qpao = new QuestionPoolAccessObject();
      List poolList = qpao.getPoolIdsByItem(itemId.toString());
      List idList = new ArrayList();
      Iterator iter = poolList.iterator(); 
      while(iter.hasNext()){
        idList.add(IdHelper.stringToId((String)iter.next()));
      }
      return idList;

    }
    catch(Exception e)
    {
      LOG.error(e); 
//      throw new Error(e);

      return null;
    }
  }

 /**
   * Get a list of pools that have a specific Owner, return their Ids
   *
   * @return A List.
   */
  public List getPoolIdsByAgent(Agent agent)
  {
    try
    {
      QuestionPoolAccessObject qpao = new QuestionPoolAccessObject();
      List poolList = qpao.getPoolIdsByAgent(agent.getId().toString());
      List idList = new ArrayList();
      Iterator iter = poolList.iterator();
      while(iter.hasNext()){
        idList.add(IdHelper.stringToId((String)iter.next()));
      }
      return idList;

    }
    catch(Exception e)
    {
      LOG.error(e); 
//      throw new Error(e);

      return null;
    }
  }

/**
   * Get a list of pools that have a specific parent, return their Ids
   *
   * @return A List.
   */
  public List getSubPools(Id poolId )
  {
    try
    {
      QuestionPoolAccessObject qpao = new QuestionPoolAccessObject();
      List poolList = qpao.getSubPools(Long.parseLong(poolId.toString()));
      List idList = new ArrayList();
      Iterator iter = poolList.iterator();
      while(iter.hasNext()){
        idList.add(IdHelper.stringToId((String)iter.next()));
      }
      return idList;

    }
    catch(Exception e)
    {
      LOG.error(e); 
//      throw new Error(e);

      return null;
    }
  }

/**
   * Checks to see if pool has subpools
   *
   * @return A boolean.
   */
  public boolean hasSubPools(Id poolId)
  {
    try
    {
      QuestionPoolAccessObject qpao = new QuestionPoolAccessObject();
      return qpao.hasSubPools(Long.parseLong(poolId.toString()));

    }
    catch(Exception e)
    {
      LOG.error(e); 
//      throw new Error(e);

      return false;
    }
  }





 /**
   * Get a list of pools that share a specific Item
   *
   * @return A List.
   */
  public List getPoolNamesByItem(Id itemId)
  {
    try
    {
      QuestionPoolAccessObject qpao = new QuestionPoolAccessObject();
      return qpao.getPoolNamesByItem(itemId.toString());

    }
    catch(Exception e)
    {
      LOG.error(e); 
//      throw new Error(e);

      return null;
    }
  }


  /**
   * Save items to a section.
   */
  public void addItemsToSection(Collection ids, Id sectionId)
  {
    try
    {
      QuestionPoolAccessObject qpao = new QuestionPoolAccessObject();
      qpao.addItemsToSection(ids, Long.parseLong(sectionId.toString()));
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /**
   * Save a question to a pool.
   */
  public void addItemToPool(Id itemId, Id poolId)
  {
    try
    {
      QuestionPoolAccessObject qpao = new QuestionPoolAccessObject();
      qpao.addItemToPool(
        itemId.toString(), Long.parseLong(poolId.toString()));
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /**
   * Move a question to a pool.
   */
  public void moveItemToPool(Id itemId, Id sourceId, Id destId)
  {
    try
    {
      QuestionPoolAccessObject qpao = new QuestionPoolAccessObject();
      qpao.moveItemToPool(
        itemId.toString(), Long.parseLong(sourceId.toString()),
        Long.parseLong(destId.toString()));
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /**
   * Is a pool a descendant of the other?
   */
  public boolean isDescendantOf(Id poolA, Id poolB, Agent agent)
  {
    try
    {
      Id tempPoolId = poolA;
      while((tempPoolId != null) && (tempPoolId.toString().compareTo("0") > 0))
      {
        QuestionPool tempPool = getPool(tempPoolId, agent);
        if(tempPool.getParentId().toString().compareTo(poolB.toString()) == 0)
        {
          return true;
        }

        tempPoolId = tempPool.getParentId();
      }

      return false;
    }
    catch(Exception e)
    {
      LOG.error(e); 
//      throw new Error(e);

      return false;
    }
  }

  /**
   * Move a subpool
   */
  public void movePool(Agent agent, Id source, Id dest)
  {
    try
    {
      QuestionPool qPoolS = getPool(source, agent);

      // check for illegal moves
      if(source.toString().compareTo(dest.toString()) != 0)
      {
        if(! isDescendantOf(dest, source, agent))
        {
          qPoolS.setParentId(dest);
          savePool(qPoolS);
        }
      }
      else
      {
        LOG.debug(
          "this move is illegal: " + source.toString() + " to : " +
          dest.toString());
      }
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /**
   * Create a new Question Pool
   */
  public QuestionPool createPool(Id parentPoolId)
  {
    try
    {
      return new QuestionPoolImpl(IdHelper.stringToId("0"), parentPoolId);
    }
    catch(Exception e)
    {
      LOG.error(e); 
//      throw new Error(e);

      return null;
    }
  }

  /**
   * Create a new Question
   */
  public Item createQuestion(
    String displayName, String description, org.navigoproject.business.entity.Item itemXml,
    HttpServletRequest request)
  {
    try
    {
      AssessmentManager assessmentManager = new AssessmentManagerImpl();
      ItemHelper itemHelper = new ItemHelper();
      EditItemHelper editItemHelper = new EditItemHelper();

      Item newItem = null;
      String ident = "";
      if( (request!=null) && (request.getParameter("ItemIdent") != null) &&
        (request.getParameter("ItemIdent").trim().length() > 0))
      {
        ident = request.getParameter("ItemIdent").trim();
        newItem = assessmentManager.getItem(IdHelper.stringToId(ident));
        itemXml = (org.navigoproject.business.entity.Item) request.getSession().getAttribute("xmlString");

      }else{

        newItem = assessmentManager.createItem(
          displayName, description, TypeLib.DR_QTI_ITEM);
      }
      ident = newItem.getId().getIdString();
      itemXml.update("item/@ident", ident);
      if(request!=null)
        itemXml = editItemHelper.updateXml(itemXml,request);

      newItem.updateData(itemXml);
      return newItem;
    }
    catch(Exception e)
    {
      LOG.error(e); 
e.printStackTrace();
//      throw new Error(e);

      return null;
    }
  }

  /**
   *
   */
  public void removeQuestionFromPool(Id questionId, Id poolId)
  {
    try
    {
      QuestionPoolAccessObject qpao = new QuestionPoolAccessObject();
      qpao.removeItemFromPool(
        questionId.toString(), Long.parseLong(
          poolId.toString()));
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /**
   * Delete a QuestionPool
   */
  public void deletePool(Id poolId, Agent agent, AAMTree tree)
  {
    try
    {
      QuestionPoolAccessObject qpao = new QuestionPoolAccessObject();
      QuestionPool qp = getPool(poolId, agent);

      // Delete all questions if any
      Collection questions =
        ((QuestionPoolProperties) qp.getData()).getQuestions();
      Iterator iter = questions.iterator();

      // For each question ,
      while(iter.hasNext())
      {
        Item item = (Item) iter.next();

          // add that question to questionpool
          qpao.removeItemFromPool(item.getId().toString(), Long.parseLong(qp.getId().toString()));
        }

      // Delete all subpools if any
      Iterator citer = (tree.getChildList(qp.getId())).iterator();
      while(citer.hasNext())
      {
        deletePool((Id) citer.next(), agent, tree);
      }

      qpao.deletePool(Long.parseLong(poolId.toString()));
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /**
   * Copy Pool wrapper
   */
  public void copyPool(
    AAMTree tree, Agent agent, Id source, Id dest )
  {
    copyPool(tree, agent, source, dest,  false);
  }

  /**
   * Copy a pool
   */
  public void copyPool(
    AAMTree tree, Agent agent, Id source, Id dest, boolean duplicateCopy)
  {
    try
    {
      ItemHelper itemHelper = new ItemHelper();

      boolean haveCommonRoot = false;
      boolean duplicate = false;

      // Get the Pools
      QuestionPool qPoolS = getPool(source, agent);

      // Are we creating a duplicate copy?
      if(dest.toString().compareTo(qPoolS.getParentId().toString()) == 0)
      {
        duplicate = true;
        duplicateCopy = true;
      }

      // Determine if the Pools are in the same tree
      // If so, make sure the source level is not higher(up the tree) than the dest.
      // to avoid the endless loop.
      if(duplicateCopy)
      {
        haveCommonRoot = false;
      }
      else
      {
        haveCommonRoot = tree.haveCommonRoot(source, dest);
      }

      /* come back to this later....
         if((haveCommonRoot)&&(tree.poolLevel(source)<=tree.poolLevel(dest))){
           return;
         }
       */

      // If Pools in same trees,
      if(haveCommonRoot)
      {
        // Copy to a Pool inside the root
        // Copy *this* Pool first
        QuestionPool questionpool =
          new QuestionPoolImpl(IdHelper.stringToId("0"), dest);
        questionpool.updateDisplayName(qPoolS.getDisplayName());
        questionpool.updateDescription(qPoolS.getDescription());
        questionpool.updateData(qPoolS.getData());
        questionpool = savePool(questionpool);

        // Get the Questions of qPoolS
        Collection questions =
          ((QuestionPoolProperties) qPoolS.getData()).getQuestions();
        Iterator iter = questions.iterator();

        // For each question ,
        while(iter.hasNext())
        {
          Item item = (Item) iter.next();

          // add that question to questionpool
          addItemToPool(item.getId(), questionpool.getId());
        }

        // Get the SubPools of qPoolS
        Iterator citer = (tree.getChildList(source)).iterator();
        while(citer.hasNext())
        {
          QuestionPool poolC = getPool((Id) citer.next(), agent);

          /* Why would I do this??
             questionpool = new QuestionPoolImpl(IdHelper.stringToId("0"), dest);
             questionpool.updateDisplayName(poolC.getDisplayName());
             questionpool.updateDescription(poolC.getDescription());
             questionpool.updateData(poolC.getData());
             questionpool = savePool(questionpool);
           */
          copyPool(
            tree, agent, poolC.getId(), questionpool.getId() );
        }
      }
      else
      { // If Pools in different trees,
        AssessmentManager assessmentManager = new AssessmentManagerImpl();

        // Copy to a Pool outside the same root
        // Copy *this* Pool first
        QuestionPool questionpool =
          new QuestionPoolImpl(IdHelper.stringToId("0"), dest);
        if(duplicate)
        {
          questionpool.updateDisplayName(qPoolS.getDisplayName() + " Copy");
        }
        else
        {
          questionpool.updateDisplayName(qPoolS.getDisplayName());
        }

        questionpool.updateDescription(qPoolS.getDescription());
        questionpool.updateData(qPoolS.getData());
        questionpool = savePool(questionpool);

        // Get the Questions of qPoolS
        Collection questions =
          ((QuestionPoolProperties) qPoolS.getData()).getQuestions();
        Iterator iter = questions.iterator();

        // For each question ,
        while(iter.hasNext())
        {
          Item item = (Item) iter.next();
          org.navigoproject.business.entity.Item odata =
            (org.navigoproject.business.entity.Item) item.getData();
          Item newItem =
            createQuestion(
              item.getDisplayName() , item.getDescription(),
              odata,null);

          // add that question to questionpool
          addItemToPool(newItem.getId(), questionpool.getId());
        }

        // Get the SubPools of qPoolS
        Iterator citer = (tree.getChildList(source)).iterator();
        while(citer.hasNext())
        {
          QuestionPool poolC = getPool((Id) citer.next(), agent);

          /*
             questionpool = new QuestionPoolImpl(IdHelper.stringToId("0"), dest);
             questionpool.updateDisplayName(poolC.getDisplayName());
             questionpool.updateDescription(poolC.getDescription());
             questionpool.updateData(poolC.getData());
             questionpool = savePool(questionpool);
           */
          copyPool(
            tree, agent, poolC.getId(), questionpool.getId() );
        }
      }
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /**
   * Copy Question wrapper
   */
  public void copyQuestion(
    Id questionId, Id destId )
  {
    copyQuestion(questionId, destId, false);
  }

  /**
   * Copy a question to a pool
   */
  public void copyQuestion(
    Id questionId, Id destId,  boolean duplicateCopy)
  {
    try
    {
      // TO DO figure out if in same node or not
      boolean haveCommonRoot = false;
      if(haveCommonRoot)
      {
        addItemToPool(questionId, destId);
      }
      else
      {
        AssessmentManager assessmentManager = new AssessmentManagerImpl();
        ItemHelper itemHelper = new ItemHelper();

        // create a new question 
        Item item = assessmentManager.getItem(questionId);
        org.navigoproject.business.entity.Item itemXml =
          (org.navigoproject.business.entity.Item) item.getData();

        Item newItem =null;
        if(duplicateCopy){
        itemXml.update("item/presentation/flow/material/mattext",itemXml.selectSingleValue("item/presentation/flow/material/mattext","element") +" Copy");
          newItem = createQuestion(
            item.getDisplayName() + " Copy", item.getDescription(),
            itemXml,null);
        }else{
          newItem = createQuestion(
            item.getDisplayName() , item.getDescription(),
            itemXml,null);
        } 

        // add that question to questionpool
        addItemToPool(newItem.getId(), destId);
      }
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /**
   * Save a question pool.
   */
  public QuestionPool savePool(QuestionPool pool)
  {
    try
    {
      QuestionPoolAccessObject qpao = new QuestionPoolAccessObject();

      return qpao.savePool(pool);
    }
    catch(Exception e)
    {
      LOG.error(e); 
//      throw new Error(e);

      return pool;
    }
  }

  /**
   * Select N questions randomly from a pool tree
   */
  public Collection selectNrandomQuestions(
    QuestionPool pool, int n, Agent agent)
  {
    try
    {
      // collect all questions in the pool tree into qCollection
      Collection qCollection = new ArrayList();
      collectAllQuestions(pool, qCollection, agent);

      // select n questions from qCollection
      Vector qC = new Vector(qCollection);

      Collection randomC = new ArrayList();
      Random rGen = new Random();
      for(int i = 0; i < n; i++)
      {
        int m = qC.size();

        // get a random number between 0 and m-1
        int r = rGen.nextInt(m);

        // add item at r to randomC and remove item at r from qC
        randomC.add(qC.remove(r));
      }

      return randomC;
    }
    catch(Exception e)
    {
      LOG.error(e); 
//      throw new Error(e);

      return new ArrayList();
    }
  }

  /**
   * Collect all questions in a pool tree
   * selects all the questions of all the subpools and the pool
   */
  private void collectAllQuestions(
    QuestionPool qp, Collection qC, Agent agent)
  {
    try
    {
      if(qC == null)
      {
        qC = new ArrayList();
      }

      // add all questions to qC;
      Collection questions =
        ((QuestionPoolProperties) qp.getData()).getQuestions();
      Iterator iter = questions.iterator();

      // For each question ,
      while(iter.hasNext())
      {
        Item item = (Item) iter.next();
        qC.add(item);
      }

      // add all questions of all subpools to qC;
      Iterator citer = (getSubPools(qp.getId())).iterator();
      while(citer.hasNext())
      {
        QuestionPool subPool = getPool((Id) citer.next(), agent);
        collectAllQuestions(subPool, qC, agent);
      }
    }
    catch(Exception e)
    {
      LOG.debug("Exception in collectAllQuestions");
      LOG.error(e); throw new Error(e);
    }
  }

  /*
   * Exports a Question as an Xml file
   */
  public String exportQuestion(Id questionId)
  {
    try{
      AssessmentManager assessmentManager = new AssessmentManagerImpl();
      Item item = assessmentManager.getItem(questionId);
      org.navigoproject.business.entity.Item itemXml = (org.navigoproject.business.entity.Item)item.getData();
      return itemXml.stringValue();
    }catch(Exception e){
      LOG.debug("Exception in exportQuestion");
      LOG.error(e);
      return null;
    }
  }

  /*
   * Imports a Question from an Xml file
   */
  public Item importQuestion(InputStream inputStream)
  {
    try{
      ItemHelper itemHelper = new ItemHelper();
org.navigoproject.business.entity.Item itemXml = itemHelper.readXMLDocument(inputStream);
      return createQuestion("","",itemXml,null);
       
    }catch(Exception e){
      LOG.debug("Exception in importQuestion");
      LOG.error(e); 
e.printStackTrace();
      return null;
    }
  }


  /*
   * getQPelement 
   *   Helper method that creates a DOM Element representing the pool
   */
  public Element getQPelement(QuestionPool pool)
  {
    try{
      Element root = new Element("objectbank");
      String strDisplayName = pool.getDisplayName();
      String strDescription = pool.getDescription();
      QuestionPoolProperties props = (QuestionPoolProperties) pool.getData();
      String strObjectives = props.getObjectives();
      String strKeywords = props.getKeywords();
      String strRubric = props.getRubric();
      if(strDisplayName==null) strDisplayName = "";
      if(strDescription==null) strDescription = "";
      if(strObjectives==null) strObjectives = "";
      if(strKeywords==null) strKeywords = "";
      if(strRubric==null) strRubric = "";
      Attribute displayName = new Attribute("displayname",strDisplayName);
      Attribute description = new Attribute("description",strDescription);
      Attribute objectives = new Attribute("objectives",strObjectives);
      Attribute keywords = new Attribute("keywords",strKeywords);
      Attribute rubric = new Attribute("rubric",strRubric);
      root.setAttribute(displayName);
      root.setAttribute(description);
      root.setAttribute(objectives);
      root.setAttribute(keywords);
      root.setAttribute(rubric);

      // Questions
      Collection questions =
          ((QuestionPoolProperties) pool.getData()).getQuestions();
      Iterator iter = questions.iterator();

      // For each question ,
      while(iter.hasNext())
      {
      Item item = (Item) iter.next();
      org.navigoproject.business.entity.Item itemXml = (org.navigoproject.business.entity.Item) item.getData();
      org.w3c.dom.Document itemdoc = itemXml.getDocument();
      DOMBuilder builder = new DOMBuilder();
      org.jdom.Document itemjdomdoc = builder.build(itemdoc);
      org.jdom.Element itemjdomel = itemjdomdoc.detachRootElement();
      root.addContent(itemjdomel);
      }

      // SubPools
      List subpools = getSubPools(pool.getId());
      if(!(subpools.isEmpty())){
        Iterator spIter = subpools.iterator();
        while(spIter.hasNext())
        {
          Id spId = (Id) spIter.next();
          QuestionPool subPool = getPool(spId,OsidManagerFactory.getAgent());
          root.addContent(getQPelement(subPool));
        }
      }

      return root;
    }catch(Exception e){
      LOG.debug("Exception in exportQuestionPool");
      LOG.error(e);
      return null;
    }
  }


  /*
   * Export Question Pool to Xml file
   */
  public String exportQuestionPool(QuestionPool pool)
  {
    try{

// Build  Document representing the Pool
    Element root = getQPelement(pool);
    org.jdom.Document doc = new org.jdom.Document(root);
    XMLOutputter outputter = new XMLOutputter(" ",true);

      return outputter.outputString(doc);
   }catch(Exception e){
      LOG.debug("Exception in exportQuestionPool");
      LOG.error(e);
      return null;    
    }

  }

  /*
   * importQuestionPoolFromElement
   *  imports a QuestionPool from a DOM Element
   */
  public QuestionPool importQuestionPoolFromElement(Element objectbankel)
  {
    try{
      XMLOutputter xmlOutputter = new XMLOutputter(" ",true);
      QuestionPool qpool = createPool(IdHelper.stringToId("0"));
      qpool.updateDisplayName(objectbankel.getAttributeValue("displayname"));
      qpool.updateDescription(objectbankel.getAttributeValue("description"));
      QuestionPoolProperties props = new QuestionPoolProperties();
      props.setOwner(OsidManagerFactory.getAgent());
      props.setLastModifiedBy(OsidManagerFactory.getAgent());
      props.setObjectives(objectbankel.getAttributeValue("objectives"));
      props.setKeywords(objectbankel.getAttributeValue("keywords"));
      props.setRubric(objectbankel.getAttributeValue("rubric"));
//TODO -- Read from the DOM, pool properties
      qpool.updateData(props);
      qpool = savePool(qpool);

//IMPORT QUESTIONS
      Iterator itemIter = (objectbankel.getChildren("item")).iterator();
      while(itemIter.hasNext()){
        Element itemEl = (Element)itemIter.next();
        String strItemXml = xmlOutputter.outputString(itemEl);
        org.navigoproject.business.entity.Item itemXml = new org.navigoproject.business.entity.Item(strItemXml);
        addItemToPool((createQuestion("","",itemXml,null)).getId(),qpool.getId());

      }

//IMPORT SUBPOOLS
      Iterator spIter = (objectbankel.getChildren("objectbank")).iterator();
      while(spIter.hasNext()){
        Element objEl = (Element)spIter.next();
        QuestionPool subPool = importQuestionPoolFromElement(objEl);
        subPool.setParentId(qpool.getId());
        savePool(subPool);
      }
    return qpool;
    }catch(Exception e){
      LOG.debug("Exception in importQuestionPool");
      LOG.error(e);
      return null;
    }
  }
 
  /*
   * Imports a Question Pool
   */
  public QuestionPool importQuestionPool(InputStream inputStream)
  {
    org.jdom.Document document = null;
    try
    {
      SAXBuilder sxb = new SAXBuilder();
      document = sxb.build(inputStream);

      org.jdom.Element objectbankel = document.getRootElement();

      return importQuestionPoolFromElement(objectbankel);
    }catch(Exception e){
      LOG.debug("Exception in importQuestionPool");
      LOG.error(e);
      return null;

    }

  }
}
