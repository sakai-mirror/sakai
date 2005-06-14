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

package org.navigoproject.data;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;

import org.navigoproject.business.entity.ASIBaseClass;
import org.navigoproject.business.entity.Assessment;
import org.navigoproject.business.entity.Item;
import org.navigoproject.business.entity.Section;
import org.navigoproject.osid.OsidManagerFactory;
import org.navigoproject.osid.TypeLib;
import org.navigoproject.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import osid.OsidException;
import osid.OsidOwner;
import osid.dr.Asset;
import osid.dr.DigitalRepository;
import osid.dr.DigitalRepositoryException;
import osid.dr.DigitalRepositoryManager;
import osid.shared.Id;
import osid.shared.SharedException;
import osid.shared.SharedManager;

/**
 * @author palcasi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
/**
 * @author palcasi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
/**
 * A manager class to facilitate interaction with OKI Digital Repositories
 *
 * @author rpembry
 * @version $Id: RepositoryManager.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
public class RepositoryManager
{
  private final static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(RepositoryManager.class);
  private SharedManager sharedManager;
  private DigitalRepositoryManager digitalRepositoryManager;
  private HttpServletRequest request = null;

  /**
   * Creates a new RepositoryManager object.
   *
   * @param request DOCUMENTATION PENDING
   */
  public RepositoryManager(HttpServletRequest request)
  {
    this.request = request;
  }

  /**
   * Creates a new RepositoryManager object.
   */
  public RepositoryManager()
  {
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws DigitalRepositoryException DOCUMENTATION PENDING
   */
  public SharedManager getSharedManager()
    throws DigitalRepositoryException
  {
    if(sharedManager == null)
    {
      try
      {
        OsidOwner owner = this.getOwner();
        sharedManager =
          org.navigoproject.osid.OsidManagerFactory.createSharedManager(owner);
      }
      catch(OsidException e)
      {
        throw new DigitalRepositoryException(
          "Problem creating SharedManager: " + e.getMessage());
      }
    }

    return sharedManager;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws DigitalRepositoryException DOCUMENTATION PENDING
   */
  private DigitalRepositoryManager getDigitalRepositoryManager()
    throws DigitalRepositoryException
  {
    if(digitalRepositoryManager == null)
    {
      try
      {
        OsidOwner owner =
          OsidManagerFactory.getOsidOwner();
        digitalRepositoryManager =
          org.navigoproject.osid.OsidManagerFactory.createDigitalRepositoryManager(
            owner);
      }
      catch(OsidException e)
      {
        LOG.warn("Unexpected OsidException: " + e.getMessage());
        throw new DigitalRepositoryException(e.getMessage());
      }
    }

    return digitalRepositoryManager;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param digitalRepositoryId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws DigitalRepositoryException DOCUMENTATION PENDING
   */
  public DigitalRepository getDigitalRepository(Id digitalRepositoryId)
    throws DigitalRepositoryException
  {
    DigitalRepositoryManager drm = getDigitalRepositoryManager();

    return drm.getDigitalRepository(digitalRepositoryId);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param objClass DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws DigitalRepositoryException DOCUMENTATION PENDING
   */
  public DigitalRepository getDigitalRepository(Class objClass)
    throws DigitalRepositoryException
  {
    DigitalRepositoryManager drm = getDigitalRepositoryManager();

    return getDigitalRepository(drm, objClass);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param objClass DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws DigitalRepositoryException DOCUMENTATION PENDING
   */
  public static DigitalRepository getDigitalRepository(
    DigitalRepositoryManager drm, Class objClass)
    throws DigitalRepositoryException
  {
    if(Assessment.class.equals(objClass))
    {
      return drm.getDigitalRepositoriesByType(TypeLib.DR_QTI_ASSESSMENT).next();
    }
    
    // hack for assessment taken
		if(Date.class.equals(objClass))
		{
		  return drm.getDigitalRepositoriesByType(TypeLib.DR_QTI_ASSESSMENT_TAKEN).next();
		}

    //    if(DecomposedAssessment.class.equals(objClass))
    //    {
    //      return drm.getDigitalRepositoriesByType(TypeLib.DR_QTI_ASSESSMENT).next();
    //    }
    if(Section.class.equals(objClass))
    {
      return drm.getDigitalRepositoriesByType(TypeLib.DR_QTI_SECTION).next();
    }

    if(Item.class.equals(objClass))
    {
      return drm.getDigitalRepositoriesByType(TypeLib.DR_QTI_ITEM).next();
    }

    if(ItemResult.class.equals(objClass))
    {
      return drm.getDigitalRepositoriesByType(TypeLib.DR_QTI_RESULTS).next();
    }

    LOG.warn("Using temporary DigitalRepository for unknown Class");

    return drm.getDigitalRepositoriesByType(TypeLib.DR_STRING_XML_TEST).next();
  }

  /**
   * This returns ending item results; it is probably only used during recovery
   * from premature logout, etc.
   *
   * @param assessmentId
   *
   * @return
   *
   * @throws DigitalRepositoryException DOCUMENTATION PENDING
   */
  public List getItemResults(Id assessmentId)
    throws DigitalRepositoryException, SharedException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getItemResults(" + assessmentId + ")");
    }

    //    PersistenceBroker broker =
    //       PersistenceBrokerFactory.defaultPersistenceBroker();
    return ItemResult.getItemResults(assessmentId);
  }

  /**
   * Here the caller doesn't know the AssetId, but retrieves based on a Search
   * Type
   *
   * @param assessmentId
   * @param itemId
   *
   * @return
   *
   * @throws DigitalRepositoryException
   */
  public Element getItemResult(Id assessmentId, Id itemId)
    throws ParserConfigurationException, SAXException, OsidException, 
      IOException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getItemResult(" + assessmentId + ", " + itemId + ")");
    }

    return ItemResult.getItemResult(assessmentId, itemId).getElement();
  }

  /**
   * Create a new ItemResult Asset and fill in its primary key for future
   * searches
   *
   * @param assessmentId
   * @param itemId
   * @param result
   *
   * @return
   *
   * @throws DigitalRepositoryException DOCUMENTATION PENDING
   */
  public void setItemResult(Id assessmentId, Id itemId, Element result)
    throws OsidException, IOException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "setItemResult(" + assessmentId + ", " + itemId + ", " + result + ")");
    }

    ItemResult.store(assessmentId, itemId, result);

    return;
  }
  
  /**
   * Updates the score and comments for an item result.
   * 
   * @param assessmentId this should be the realized assessment id
   * @param itemId
   * @param score
   * @param comments
   */
  public void updateItemResult(Id assessmentId, Id itemId, String score, String comments)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("udateItemResult(Id " + assessmentId + ", Id " + itemId + ", String " + score + ", String " + comments + ")");
    }
    
    try
    {
      Element element = this.getItemResult(assessmentId, itemId);
      if (element != null)
      {
        Document document = element.getOwnerDocument();
      
        ServletContext context = this.request.getSession().getServletContext();
        DOMSource source = XmlUtil.getDocumentSource(context, "/xml/xsl/report/setItemScore.xsl");
        Transformer transformer = XmlUtil.createTransformer(source);
        transformer.setParameter("item_id", itemId.getIdString());
        transformer.setParameter("score", score);
        document = XmlUtil.transformDocument(document, transformer);
      
        source = XmlUtil.getDocumentSource(context, "/xml/xsl/report/setItemComments.xsl");
        transformer = XmlUtil.createTransformer(source);
        transformer.setParameter("item_id", itemId.getIdString());
        transformer.setParameter("comments", comments);
        document = XmlUtil.transformDocument(document, transformer);
      
        this.setItemResult(assessmentId, itemId, document.getDocumentElement());        
      }
    }
    catch (SharedException e)
    {
      LOG.debug(e);
      throw new Error(e);
    }
    catch (ParserConfigurationException e)
    {
      LOG.debug(e);
      throw new Error(e);
    }
    catch (SAXException e)
    {
      LOG.debug(e);
      throw new Error(e);
    }
    catch (OsidException e)
    {
      LOG.debug(e);
      throw new Error(e);
    }
    catch (IOException e)
    {
      LOG.debug(e);
      throw new Error(e);
    }
  }
  
  public List getSectionResults(Id assessmentId)
    throws OsidException
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getSectionResults(Id " + assessmentId + ")");
    }
    return SectionResult.getSectionResults(assessmentId);
  }
  
  public SectionResult getSectionResult(Id assessmentId, Id sectionId)
    throws OsidException
  {
    if (LOG.isDebugEnabled());
    {
      LOG.debug("getSectionResult(Id " + assessmentId + ", Id " + sectionId + ")");
    }
    return SectionResult.getSectionResult(assessmentId, sectionId);
  }
  
  public void setSectionResult(Id assessmentId, Id sectionId, Element element)
    throws OsidException, IOException
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setSectionResult(Id " + assessmentId + ", Id " + sectionId + ", Element " + element + ")");
    }
    SectionResult.store(assessmentId, sectionId, element);
  }
  
  public AssessmentResult getAssessmentResult(Id assessmentId)
    throws OsidException
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getAssessmentResult(Id " + assessmentId + ")");
    }
    return AssessmentResult.getAssessmentResult(assessmentId);
  }
  
  public void setAssessmentResult(Id assessmentId, Element element)
    throws OsidException, IOException
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setAssessmentResult(Id " + assessmentId + ", Element " + element + ")");
    }
    AssessmentResult.store(assessmentId, element);
  }
  
  /**
   * Updates the comments for an assessment result
   * 
   * @param assessmentId The realized assessment id.
   * @param score This is just a placeholder for now. We are not saving the score.
   * @param comments
   */
  public void updateAssessmentResult(Id assessmentId, String score, String comments)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("updateAssessmentResult(Id " + assessmentId + ", String " + score + ", String " + comments + ")");
    }
    
    try
    {
      AssessmentResult assessmentResult = this.getAssessmentResult(assessmentId);
      
      if (assessmentResult != null)
      {
        Element element = assessmentResult.getElement();
        Document document = element.getOwnerDocument();
      
        ServletContext context = this.request.getSession().getServletContext();
        DOMSource source = XmlUtil.getDocumentSource(context, "/xml/xsl/report/setAssessmentComments.xsl");
        Transformer transformer = XmlUtil.createTransformer(source);
        transformer.setParameter("assessment_id", assessmentId.getIdString());
        transformer.setParameter("comments", comments);
        document = XmlUtil.transformDocument(document, transformer);
      
        this.setAssessmentResult(assessmentId, document.getDocumentElement());
      }
    }
    catch (SharedException e)
    {
      LOG.debug(e);
      throw new Error(e);
    }
    catch (OsidException e)
    {
      LOG.debug(e);
      throw new Error(e);
    }
    catch (IOException e)
    {
      LOG.debug(e);
      throw new Error(e);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param asi DOCUMENTATION PENDING
   * @param title DOCUMENTATION PENDING
   * @param description DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws DigitalRepositoryException DOCUMENTATION PENDING
   */
  public Asset setASI(ASIBaseClass asi, String title, String description)
    throws DigitalRepositoryException
  {
    return this.store(asi, title, description);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param idString DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws DigitalRepositoryException DOCUMENTATION PENDING
   */
  public Id getId(String idString)
    throws DigitalRepositoryException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getId(" + idString + ")");
    }

    Id id = null;
    try
    {
      id = getSharedManager().getId(idString);
    }
    catch(SharedException e)
    {
      LOG.warn("SharedException while attempting getId(" + idString + ")");
      throw new DigitalRepositoryException(e.getMessage());
    }

    return id;
  }

  /**
   * Stores object in a Digital Repository. Chooses which Digital Repository to
   * use based on object's class.
   *
   * @param object
   * @param title DOCUMENTATION PENDING
   * @param description DOCUMENTATION PENDING
   *
   * @return
   *
   * @throws DigitalRepositoryException DOCUMENTATION PENDING
   */
  public Asset store(Serializable object, String title, String description)
    throws DigitalRepositoryException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("store(" + object + ")");
    }

    Asset asset = createAsset(object.getClass(), title, description);
    asset.updateContent(object);

    return asset;
  }

  /**
   * Stores object in the specified Digital Repository.
   *
   * @param digitalRepositoryId DOCUMENTATION PENDING
   * @param object
   * @param title DOCUMENTATION PENDING
   * @param description DOCUMENTATION PENDING
   *
   * @return
   *
   * @throws DigitalRepositoryException DOCUMENTATION PENDING
   */
  public Asset store(
    Id digitalRepositoryId, Serializable object, String title,
    String description)
    throws DigitalRepositoryException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("store(" + digitalRepositoryId + ", " + object + ")");
    }

    Asset asset =
      createAsset(digitalRepositoryId, object.getClass(), title, description);
    asset.updateContent(object);

    return asset;
  }

  /**
   * Creates an Asset in the specified Digital Repository.
   *
   * @param objClass
   * @param title DOCUMENTATION PENDING
   * @param description DOCUMENTATION PENDING
   *
   * @return
   *
   * @throws DigitalRepositoryException DOCUMENTATION PENDING
   */
  public Asset createAsset(Class objClass, String title, String description)
    throws DigitalRepositoryException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("createAsset(" + objClass + ")");
    }

    return createAsset(
      getDigitalRepository(objClass).getId(), objClass, title, description);
  }

  /**
   * Creates an Asset in the specified Digital Repository.
   *
   * @param digitalRepositoryId
   * @param objClass DOCUMENTATION PENDING
   * @param title DOCUMENTATION PENDING
   * @param description DOCUMENTATION PENDING
   *
   * @return
   *
   * @throws DigitalRepositoryException DOCUMENTATION PENDING
   */
  public Asset createAsset(
    Id digitalRepositoryId, Class objClass, String title, String description)
    throws DigitalRepositoryException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("createAsset(" + digitalRepositoryId + ", " + objClass + ")");
    }

    DigitalRepository dr = getDigitalRepository(digitalRepositoryId);
    Asset asset = dr.createAsset(title, description, dr.getType());

    return asset;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assetId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws DigitalRepositoryException DOCUMENTATION PENDING
   */
  public Asset getAsset(Id assetId)
    throws DigitalRepositoryException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getAsset(" + assetId + ")");
    }

    Asset result = null;
    try
    {
      result = getDigitalRepositoryManager().getAsset(assetId);
    }
    catch(DigitalRepositoryException e)
    {
      LOG.debug("requested asset not found");
      throw e;
    }

    return result;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assetId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws DigitalRepositoryException DOCUMENTATION PENDING
   */
  public Asset getAsset(Id assetId, Calendar date)
    throws DigitalRepositoryException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getAsset(" + assetId + "," + date + ")");
    }

    Asset result = null;
    try
    {
      if(date == null)
      {
        result = getDigitalRepositoryManager().getAsset(assetId);
      }
      else
      {
        result = getDigitalRepositoryManager().getAssetByDate(assetId, date);
      }
    }
    catch(DigitalRepositoryException e)
    {
      LOG.debug("requested asset not found");
      throw e;
    }

    return result;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assetId DOCUMENTATION PENDING
   * @param newObject DOCUMENTATION PENDING
   *
   * @throws DigitalRepositoryException DOCUMENTATION PENDING
   */
  public void update(Id assetId, Serializable newObject)
    throws DigitalRepositoryException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("update(" + assetId + ", " + newObject + ")");
    }

    getAsset(assetId, null).updateContent(newObject);
  }

  /**
   * returns Object with the given Id
   *
   * @param assetId
   *
   * @return
   *
   * @throws DigitalRepositoryException DOCUMENTATION PENDING
   */
  public Serializable retrieve(Id assetId, Calendar date)
    throws DigitalRepositoryException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("retrieve(" + assetId + ", " + date + ")");
    }

    return getAsset(assetId, date).getContent();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assetId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws DigitalRepositoryException DOCUMENTATION PENDING
   */
  public Serializable retrieve(Id assetId)
    throws DigitalRepositoryException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("retrieve(" + assetId + ")");
    }

    return getAsset(assetId).getContent();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private OsidOwner getOwner()
  {
    OsidOwner owner = null;
    if(this.request != null)
    {
      owner = OsidManagerFactory.getOsidOwner();
    }
    else
    {
      owner =
        OsidManagerFactory.getOsidOwner();
    }

    return owner;
  }
}
