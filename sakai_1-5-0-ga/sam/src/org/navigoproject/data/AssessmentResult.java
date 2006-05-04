/*
 * Created on May 13, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.data;

import java.io.IOException;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import org.navigoproject.osid.impl.PersistenceService;

import osid.OsidException;
import osid.dr.Asset;
import osid.dr.DigitalRepository;
import osid.dr.DigitalRepositoryException;
import osid.dr.DigitalRepositoryManager;
import osid.shared.Id;
import osid.shared.SharedManager;

import org.w3c.dom.Element;

/**
 * @author palcasi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AssessmentResult {
  private Id assessmentId;
  private Element element;
  private final static org.apache.log4j.Logger LOG =
        org.apache.log4j.Logger.getLogger(AssessmentResult.class);
  
  public AssessmentResult(AssessmentResultBean bean)
  {
    this.assessmentId = IdHelper.stringToId(bean.getAssessmentId());
    Asset stringAsset;
      try
      {
        stringAsset = getDr().getAsset(IdHelper.stringToId(bean.getElementId()));
        this.element = this.StringAssetToElement(stringAsset);
      }
      catch (DigitalRepositoryException e)
      {
        LOG.error(e);
        throw new Error(e);
      }
      catch (OsidException e)
      {
        LOG.error(e);
        throw new Error(e);
      }
      catch (ParserConfigurationException e)
      {
        LOG.error(e);
        throw new Error(e);
      }
      catch (SAXException e)
      {
        LOG.error(e);
        throw new Error(e);
      }
      catch (IOException e)
      {
        LOG.error(e);
        throw new Error(e);
      }
  }
  
  public static AssessmentResult getAssessmentResult(Id assessmentId)
    throws OsidException
  {
    AssessmentResultBean identity = makeIdentityBean(assessmentId.getIdString());
    AssessmentResultBean bean = retrieveAssessmentResultBean(identity);
    
    AssessmentResult assessmentResult = null;
    if (bean != null)
    {
      assessmentResult = new AssessmentResult(bean);
    }
    return assessmentResult;
  }
  
  public static void store(Id assessmentId, Element element)
    throws OsidException, IOException
  {
    SharedManager sm =
      org.navigoproject.osid.OsidManagerFactory.createSharedManager();

    DigitalRepository dr = getDr();

    String resultString = DomHelper.stringValue(element);

    String assessmentIdString = assessmentId.getIdString();
    AssessmentResultBean identity = makeIdentityBean(assessmentId.getIdString());
    AssessmentResultBean found = retrieveAssessmentResultBean(identity);

    if(found == null)
    {
      //Create a new Asset to hold the result
      Asset newResultAsset = dr.createAsset("", "", dr.getType());

      // Store the result
      newResultAsset.updateContent(resultString);

      // Grab this new Asset's Id
      Id resultAssetId = newResultAsset.getId();
      identity.setElementId(resultAssetId.getIdString());


      PersistenceService.getInstance().getQtiQueries().persistAssessmentResultBean(identity);
      if(LOG.isDebugEnabled())
      {
        LOG.debug(
          "Created AssessmentResult in local table. Created Asset: " + resultAssetId);
      }
    }
    else //update existing data
    {
      Id assetId = sm.getId(found.getElementId());

      if(LOG.isDebugEnabled())
      {
        LOG.debug(
          "AssessmentResult found in local table. Updating Asset: " + assetId);
      }

      Asset foundResultAsset = dr.getAsset(assetId);
      foundResultAsset.updateContent(resultString);
    }
  }
  
  private static AssessmentResultBean makeIdentityBean(String assessmentId)
  {
    AssessmentResultBean identity = new AssessmentResultBean();
    identity.setAssessmentId(assessmentId);
    return identity;
  }

  private static AssessmentResultBean retrieveAssessmentResultBean(AssessmentResultBean identity)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("retrieveAssessmentResultBean(" + identity + ")");
    }
                                         
    AssessmentResultBean found = PersistenceService.getInstance().getQtiQueries().retrieveAssessmentResultBean(identity.getAssessmentId());

    if(LOG.isDebugEnabled())
    {
      LOG.debug("found: " + found);
    }

    return found;
  }
  
  private static DigitalRepository getDr()
    throws OsidException
  {
    DigitalRepositoryManager drm =
      org.navigoproject.osid.OsidManagerFactory.createDigitalRepositoryManager();

    return RepositoryManager.getDigitalRepository(drm, ItemResult.class);
  }
  
  private Element StringAssetToElement(Asset stringAsset)
    throws DigitalRepositoryException, ParserConfigurationException, 
      SAXException, IOException
  {
    return DomHelper.stringToElement((String) stringAsset.getContent());
  }
  
  /**
   * @return
   */
  public Id getAssessmentId()
  {
    return this.assessmentId;
  }

  /**
   * @return
   */
  public Element getElement()
  {
    return this.element;
  }

}