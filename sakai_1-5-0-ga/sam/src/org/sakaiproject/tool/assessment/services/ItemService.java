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


package org.sakaiproject.tool.assessment.services;

import org.sakaiproject.tool.assessment.data.dao.assessment.ItemData;
import org.sakaiproject.tool.assessment.data.dao.assessment.ItemFeedback;
import org.sakaiproject.tool.assessment.data.dao.assessment.ItemFeedback;
import org.sakaiproject.tool.assessment.facade.ItemFacade;
import org.sakaiproject.tool.assessment.facade.ItemIteratorFacade;
import org.sakaiproject.tool.assessment.osid.assessment.impl.ItemIteratorImpl;
import org.navigoproject.osid.impl.PersistenceService;

import java.io.*;
import javax.servlet.http.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Collection;


// DELETEME
import org.osid.shared.SharedException;

//import osid.assessment.Item;


/**
 * The ItemService calls persistent service locator to reach the
 * manager on the back end.
 */
public class ItemService
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(ItemService.class);


  /**
   * Creates a new ItemService object.
   */
  public ItemService()
  {
  }


  /**
   * Get a particular item from the backend, with all questions.
   */
  public ItemFacade getItem(Long itemId, String agentId)
  {
    ItemFacade item = null;
    try
    {
    System.out.println("***** itemId in delegate=" + itemId);
      item =
        PersistenceService.getInstance().getItemFacadeQueries().
          getItem(itemId, agentId);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }

    return item;
  }

  /**
   * Delete a item
   */
  public void deleteItem(Long itemId, String agentId)
  {
    try
    {
      ItemFacade item= PersistenceService.getInstance().
        getItemFacadeQueries().getItem(itemId, agentId);

      System.out.println("Ownerid = " + item.getData().getCreatedBy());
      // you are not allowed to delete item if you are not the owner
      if (!item.getData().getCreatedBy().equals(agentId))
        throw new Error(new Exception());
      PersistenceService.getInstance().getItemFacadeQueries().
        deleteItem(itemId, agentId);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }


  /**
   * Delete itemtextset for an item, used for modify
   */
  public void deleteItemContent(Long itemId, String agentId)
  {
    try
    {
      ItemFacade item= PersistenceService.getInstance().
        getItemFacadeQueries().getItem(itemId, agentId);

      // you are not allowed to delete item if you are not the owner

      System.out.println("item.getdata()= " + item.getData());
      System.out.println("item.getdata().getCreatedBy= " + item.getData().getCreatedBy());
      if (!item.getData().getCreatedBy().equals(agentId))
        throw new Error(new Exception());
      PersistenceService.getInstance().getItemFacadeQueries().
        deleteItemContent(itemId, agentId);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }


  /**
   * Delete metadata for an item, used for modify
   * param:  itemid, label, agentId
   */
  public void deleteItemMetaData(Long itemId, String label, String agentId)
  {
    try
    {
      ItemFacade item= PersistenceService.getInstance().
        getItemFacadeQueries().getItem(itemId, agentId);

      // you are not allowed to delete item if you are not the owner

      if (!item.getData().getCreatedBy().equals(agentId))
        throw new Error(new Exception());
      PersistenceService.getInstance().getItemFacadeQueries().
        deleteItemMetaData(itemId, label);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

   /**
   * Add metadata for an item, used for modify
   * param:  itemid, label, value, agentId
   */
  public void addItemMetaData(Long itemId, String label, String value, String agentId)
  {
    try
    {
      ItemFacade item= PersistenceService.getInstance().
        getItemFacadeQueries().getItem(itemId, agentId);

      // you are not allowed to delete item if you are not the owner

      if (!item.getData().getCreatedBy().equals(agentId))
        throw new Error(new Exception());
      PersistenceService.getInstance().getItemFacadeQueries().
        addItemMetaData(itemId, label, value);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }




  /**
   * Save a question item.
   */
  public ItemFacade saveItem(ItemFacade item)
  {
    try
    {
      System.out.println("Ownerid = " + item.getData().getCreatedBy());
      System.out.println("Ownerid = " + item.getData().getScore());
      return PersistenceService.getInstance().getItemFacadeQueries().saveItem(item);
    }
    catch(Exception e)
    {
      LOG.error(e);
//      throw new Error(e);

      return item;
    }
  }

  public ItemFacade getItem(String itemId) {
    try{
      return PersistenceService.getInstance().getItemFacadeQueries().
          getItem(new Long(itemId));
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

}
