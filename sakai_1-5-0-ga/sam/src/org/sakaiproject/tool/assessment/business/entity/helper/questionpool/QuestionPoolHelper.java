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

package org.sakaiproject.tool.assessment.business.entity.helper.questionpool;


/**
 * @todo pretty well stubbed out right now....
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organization: Sakai Project</p>
 * @author rshastri
 * @author Ed Smiley esmiley@stanford.edu
 * @version $Id: QuestionPoolHelper.java,v 1.3 2005/01/06 01:27:48 esmiley.stanford.edu Exp $
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
//  public ArrayList getPools(Map parameterMap ) //HttpServletRequest request)
//  {
//    QuestionPoolDelegate delegate = new QuestionPoolDelegate();
//    AuthoringHelper authoringHelper = new AuthoringHelper();
//    ArrayList pools =
//      (ArrayList) delegate.getPoolIdsByAgent(
//        authoringHelper.getRemoteUserAgent(parameterMap));
//
//    if (pools== null)
//    {
//    	LOG.warn("No pools returned");
//    	return new ArrayList();
//    }
//    return pools;
//  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
//  public ArrayList getPoolsPlusName(Map parameterMap)//HttpServletRequest request)
//  {
//    QuestionPoolDelegate delegate = new QuestionPoolDelegate();
//    AuthoringHelper authoringHelper = new AuthoringHelper();
//    List pools = getPools(parameterMap);
//    ArrayList poolsAndName = new ArrayList();
//    if(pools != null && pools.size()> 0)
//    {
//      Iterator poolIter = pools.iterator();
//      while(poolIter.hasNext())
//      {
//        try
//        {
//          Id poolId = (Id) poolIter.next();
//          poolsAndName.add(
//            poolId + "+" +
//            delegate.getPool(
//              poolId, authoringHelper.getRemoteUserAgent(parameterMap))
//                    .getDisplayName());
//        }
//        catch(QuestionPoolException e)
//        {
//          LOG.error(e.getMessage(), e);
//        }
//      }
//    }
//
//    return poolsAndName;
//  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param itemId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
//  public ArrayList getPoolsByItem(String itemId)
//  {
//    ArrayList pools = new ArrayList();
//    if((itemId != null) && (itemId.trim().length() > 0))
//    {
//      QuestionPoolDelegate delegate = new QuestionPoolDelegate();
//      AuthoringHelper authoringHelper = new AuthoringHelper();
//      List poolIds = delegate.getPoolIdsByItem(authoringHelper.getId(itemId));
//
//      if(poolIds != null)
//      {
//        Iterator iter = poolIds.iterator();
//        while(iter.hasNext())
//        {
//          try
//          {
//            pools.add(((Id) iter.next()).getIdString());
//          }
//          catch(SharedException e)
//          {
//            LOG.error(e.getMessage(), e);
//          }
//        }
//      }
//    }
//
//    return pools;
//  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param itemId DOCUMENTATION PENDING
   */
//  private void deleteItemIdFromAllPools(String itemId)
//  {
//    QuestionPoolDelegate delegate = new QuestionPoolDelegate();
//    AuthoringHelper authoringHelper = new AuthoringHelper();
//    ArrayList pools = getPoolsByItem(itemId);
//    if(pools != null)
//    {
//      Iterator iter = pools.iterator();
//      while(iter.hasNext())
//      {
//        Id poolId = IdHelper.stringToId((String) iter.next());
//        delegate.removeQuestionFromPool(authoringHelper.getId(itemId), poolId);
//      }
//    }
//  }

//  /**
//   * DOCUMENTATION PENDING
//   *
//   * @param itemId DOCUMENTATION PENDING
//   * @param poolIds DOCUMENTATION PENDING
//   */
//  public void addToPools(String itemId, String[] poolIds)
//  {
//    //clean the pools first for the item
//    deleteItemIdFromAllPools(itemId);
//
//    // Assign to pools
//    if(
//      ((itemId != null) && (itemId.trim().length() > 0)) &&
//        ((poolIds != null) && (poolIds.length > 0)))
//    {
//      QuestionPoolDelegate delegate = new QuestionPoolDelegate();
//      AuthoringHelper authoringHelper = new AuthoringHelper();
//      for(int i = 0; i < poolIds.length; i++)
//      {
//      	if(poolIds[i] != null && poolIds[i].trim().length()>0)
//     		{  delegate.addItemToPool(
//          authoringHelper.getId(itemId), authoringHelper.getId(poolIds[i]));
//    		}
//      }
//    }
//  }
}
