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

/*
 * Created on May 6, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.author.questionpool;

import org.navigoproject.business.entity.QuestionPoolException;
import org.navigoproject.data.IdHelper;
import org.navigoproject.osid.questionpool.QuestionPoolDelegate;
import org.navigoproject.ui.web.asi.author.AuthoringHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import osid.shared.Id;
import osid.shared.SharedException;

/**
 * @author rshastri
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class QuestionPoolHelper
{
  private static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(QuestionPoolHelper.class);

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ArrayList getPools(HttpServletRequest request)
  {
    QuestionPoolDelegate delegate = new QuestionPoolDelegate();
    AuthoringHelper authoringHelper = new AuthoringHelper();
    ArrayList pools =
      (ArrayList) delegate.getPoolIdsByAgent(
        authoringHelper.getRemoteUserAgent(request));

    if (pools== null)
    {
    	LOG.warn("No pools returned");
    	return new ArrayList();
    }
    return pools;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ArrayList getPoolsPlusName(HttpServletRequest request)
  {
    QuestionPoolDelegate delegate = new QuestionPoolDelegate();
    AuthoringHelper authoringHelper = new AuthoringHelper();
    List pools = getPools(request);
    ArrayList poolsAndName = new ArrayList();
    if(pools != null && pools.size()> 0)
    {
      Iterator poolIter = pools.iterator();
      while(poolIter.hasNext())
      {
        try
        {
          Id poolId = (Id) poolIter.next();
          poolsAndName.add(
            poolId + "+" +
            delegate.getPool(
              poolId, authoringHelper.getRemoteUserAgent(request))
                    .getDisplayName());
        }
        catch(QuestionPoolException e)
        {
          LOG.error(e.getMessage(), e);
        }
      }
    }

    return poolsAndName;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param itemId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ArrayList getPoolsByItem(String itemId)
  {
    ArrayList pools = new ArrayList();
    if((itemId != null) && (itemId.trim().length() > 0))
    {
      QuestionPoolDelegate delegate = new QuestionPoolDelegate();
      AuthoringHelper authoringHelper = new AuthoringHelper();
      List poolIds = delegate.getPoolIdsByItem(authoringHelper.getId(itemId));

      if(poolIds != null)
      {
        Iterator iter = poolIds.iterator();
        while(iter.hasNext())
        {
          try
          {
            pools.add(((Id) iter.next()).getIdString());
          }
          catch(SharedException e)
          {
            LOG.error(e.getMessage(), e);
          }
        }
      }
    }

    return pools;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param itemId DOCUMENTATION PENDING
   */
  private void deleteItemIdFromAllPools(String itemId)
  {
    QuestionPoolDelegate delegate = new QuestionPoolDelegate();
    AuthoringHelper authoringHelper = new AuthoringHelper();
    ArrayList pools = getPoolsByItem(itemId);
    if(pools != null)
    {
      Iterator iter = pools.iterator();
      while(iter.hasNext())
      {
        Id poolId = IdHelper.stringToId((String) iter.next());
        delegate.removeQuestionFromPool(authoringHelper.getId(itemId), poolId);
      }
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param itemId DOCUMENTATION PENDING
   * @param poolIds DOCUMENTATION PENDING
   */
  public void addToPools(String itemId, String[] poolIds)
  {
    //clean the pools first for the item
    deleteItemIdFromAllPools(itemId);

    // Assign to pools
    if(
      ((itemId != null) && (itemId.trim().length() > 0)) &&
        ((poolIds != null) && (poolIds.length > 0)))
    {
      QuestionPoolDelegate delegate = new QuestionPoolDelegate();
      AuthoringHelper authoringHelper = new AuthoringHelper();
      for(int i = 0; i < poolIds.length; i++)
      {
      	if(poolIds[i] != null && poolIds[i].trim().length()>0)
     		{  delegate.addItemToPool(    
          authoringHelper.getId(itemId), authoringHelper.getId(poolIds[i]));
    		}
      }
    }
  }
}
