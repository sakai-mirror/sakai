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


/**
 *
 * @author <a href="mailto:rpembry@indiana.edu">Randall P. Embry</a>
 * @version $Id: ItemResult.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
package org.navigoproject.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.navigoproject.osid.impl.PersistenceService;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import osid.OsidException;
import osid.dr.Asset;
import osid.dr.DigitalRepository;
import osid.dr.DigitalRepositoryException;
import osid.dr.DigitalRepositoryManager;
import osid.shared.Id;
import osid.shared.SharedException;
import osid.shared.SharedManager;

/**
 *
 */

// TODO consider caching bean and/or represented classes, use lazy loading, etc.
public class ItemResult
{
  private final static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(ItemResult.class);
  private Id assessmentId;
  private Id itemId;
  private Element element;
  //private static PersistenceBroker broker =
  //  PersistenceBrokerFactory.defaultPersistenceBroker();

  /**
   * reconstitutes an ItemResult from a data bean.
   * @param bean
   */
  public ItemResult(ItemResultBean bean)
  {
    this.assessmentId = IdHelper.stringToId(bean.getItemResultPK().getAssessmentId());
    this.itemId = IdHelper.stringToId(bean.getItemResultPK().getItemId());
    Asset stringAsset;
    try
    {
      stringAsset =
        ItemResult.getDr().getAsset(IdHelper.stringToId(bean.getElementId()));
      this.element = this.StringAssetToElement(stringAsset);
    }
    catch(DigitalRepositoryException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(OsidException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(ParserConfigurationException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(SAXException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(IOException e)
    {
      LOG.error(e.getMessage(), e);
      ;
    }
  }

  /**
   * Returns a snapshot of this ItemResults data in a bean. Note that
   * this bean will not reflect changes made to the underlying ItemResult.
   *
   * @return bean containing this ItemResult's data at the time it is called.
   *         (useful for persisting the ItemResult?)
   * @throws SharedException
   * @throws IOException
   */
  public ItemResultBean getBean()
    throws SharedException, IOException
  {
    ItemResultBean data = new ItemResultBean();
    data.getItemResultPK().setAssessmentId(assessmentId.getIdString());
    data.getItemResultPK().setItemId(itemId.getIdString());

    data.setElementId(DomHelper.stringValue(element));

    return data;
  }

  /**
   * Private constructor for Item Results. You should use the Manager
   * to create one instead.
   * @param assessmentId - Id of the Assesment. May never be null.
   * @param itemId - Id of an Item in this Assessment.  May never be null.
   * @param element
   */
  private ItemResult(Id assessmentId, Id itemId, Element element)
  {
    if((assessmentId == null) || (itemId == null))
    {
      throw new NullPointerException();
    }

    if(element == null) // not currently supported
    {
      throw new NullPointerException();
    }

    this.assessmentId = assessmentId;
    this.itemId = itemId;
    this.element = element;
  }

  /**
   * @return
   */
  public Id getAssessmentId()
  {
    return assessmentId;
  }

  /**
   * @return
   */
  public Element getElement()
  {
    return element;
  }

  /**
   * @return
   */
  public Id getItemId()
  {
    return itemId;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws SharedException DOCUMENTATION PENDING
   */
  public static List getItemResults(Id assessmentId)
    throws SharedException
  {
//    ItemResultBean example = new ItemResultBean();
//    example.getItemResultPK().setAssessmentId(assessmentId.getIdString());

    //   Iterator iter= broker.getIteratorByQuery(new QueryByCriteria(example));
    //Collection col = broker.getCollectionByQuery(new QueryByCriteria(example));
    //Iterator iter = col.iterator();
    
    List l = PersistenceService.getInstance().getQtiQueries().returnItemResults(assessmentId.toString());
    Iterator iter = l.iterator();	
    
    List result = new ArrayList();

    while(iter.hasNext())
    {
      ItemResult next = new ItemResult((ItemResultBean) iter.next());
      if((next != null) && assessmentId.equals(next.getAssessmentId()))
      {
        result.add(next);
      }
    }

    return result;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws OsidException DOCUMENTATION PENDING
   */
  private static DigitalRepository getDr()
    throws OsidException
  {
    DigitalRepositoryManager drm =
      org.navigoproject.osid.OsidManagerFactory.createDigitalRepositoryManager();

    return RepositoryManager.getDigitalRepository(drm, ItemResult.class);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assetId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws OsidException DOCUMENTATION PENDING
   */
  private Asset deferenceResult(Id assetId)
    throws OsidException
  {
    Asset foundResultAsset = getDr().getAsset(assetId);

    return foundResultAsset;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param stringAsset DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws DigitalRepositoryException DOCUMENTATION PENDING
   * @throws ParserConfigurationException DOCUMENTATION PENDING
   * @throws SAXException DOCUMENTATION PENDING
   * @throws IOException DOCUMENTATION PENDING
   */
  private Element StringAssetToElement(Asset stringAsset)
    throws DigitalRepositoryException, ParserConfigurationException, 
      SAXException, IOException
  {
    return DomHelper.stringToElement((String) stringAsset.getContent());
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentId DOCUMENTATION PENDING
   * @param itemId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws DigitalRepositoryException DOCUMENTATION PENDING
   */
  private Id getItemResultAssetId(String assessmentId, String itemId)
    throws OsidException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getItemResultAsset(" + assessmentId + ", " + itemId + ")");
    }

    SharedManager sm =
      org.navigoproject.osid.OsidManagerFactory.createSharedManager();
    ItemResultBean found =
      retrieveItemResultBean(makeIdentityBean(assessmentId, itemId));

    if(found != null)
    {
      Id assetId = sm.getId(found.getElementId());

      if(LOG.isDebugEnabled())
      {
        LOG.debug("ItemResult found in local table. Asset is: " + assetId);
      }

      return assetId;
    }

    return null;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentId DOCUMENTATION PENDING
   * @param itemId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private static ItemResultBean makeIdentityBean(
    String assessmentId, String itemId)
  {
    ItemResultBean identity = new ItemResultBean();
    identity.getItemResultPK().setAssessmentId(assessmentId);
    identity.getItemResultPK().setItemId(itemId);

    return identity;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param identity DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private static ItemResultBean retrieveItemResultBean(ItemResultBean identity)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getItemResultBean(" + identity + ")");
    }

    ItemResultPK irpk = new ItemResultPK(identity.getItemResultPK().getAssessmentId(),
                                         identity.getItemResultPK().getItemId());
                                         
    ItemResultBean found = PersistenceService.getInstance().getQtiQueries().retrieveItemResultBean(irpk);
//    ItemResultBean found =
//      (ItemResultBean) broker.getObjectByIdentity(
//        new Identity(identity, broker));

    if(LOG.isDebugEnabled())
    {
      LOG.debug("found: " + found);
    }

    return found;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentId DOCUMENTATION PENDING
   * @param itemId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws OsidException DOCUMENTATION PENDING
   * @throws IOException DOCUMENTATION PENDING
   */
  public static ItemResult getItemResult(Id assessmentId, Id itemId)
    throws OsidException, IOException
  {
    String assessmentIdString = assessmentId.getIdString();
    String itemIdString = itemId.getIdString();
    ItemResultBean identity =
      makeIdentityBean(assessmentIdString, itemIdString);
    ItemResultBean bean = retrieveItemResultBean(identity);

    return new ItemResult(bean);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentId DOCUMENTATION PENDING
   * @param itemId DOCUMENTATION PENDING
   * @param result DOCUMENTATION PENDING
   *
   * @throws OsidException DOCUMENTATION PENDING
   * @throws IOException DOCUMENTATION PENDING
   */
  public static void store(Id assessmentId, Id itemId, Element result)
    throws OsidException, IOException
  {
  	
		if(LOG.isDebugEnabled())
		{
			LOG.debug(
				"store(" + assessmentId + ", " + itemId + ", " + result + ")");
		}
				
  	
    SharedManager sm =
      org.navigoproject.osid.OsidManagerFactory.createSharedManager();

    DigitalRepository dr = getDr();

    String resultString = DomHelper.stringValue(result);

    String assessmentIdString = assessmentId.getIdString();
    String itemIdString = itemId.getIdString();
    ItemResultBean identity =
      makeIdentityBean(assessmentIdString, itemIdString);
    ItemResultBean found = retrieveItemResultBean(identity);

    if(found == null)
    {
      //Create a new Asset to hold the result
      Asset newResultAsset = dr.createAsset("", "", dr.getType());

      // Store the result
      newResultAsset.updateContent(resultString);

      // Grab this new Asset's Id
      Id resultAssetId = newResultAsset.getId();
      identity.setElementId(resultAssetId.getIdString());


			PersistenceService.getInstance().getQtiQueries().persistItemResultBean(identity);
      // and Store the bean
//      broker.beginTransaction();
//      broker.store(identity);
//      broker.commitTransaction();
//      broker.close();
      if(LOG.isDebugEnabled())
      {
        LOG.debug(
          "Created ItemResult in local table. Created Asset: " + resultAssetId);
      }
    }
    else //update existing data
    {
      Id assetId = sm.getId(found.getElementId());

      if(LOG.isDebugEnabled())
      {
        LOG.debug(
          "ItemResult found in local table. Updating Asset: " + assetId);
      }

      Asset foundResultAsset = dr.getAsset(assetId);
      foundResultAsset.updateContent(resultString);
    }
  }
}
