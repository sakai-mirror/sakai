/*
 * Created on May 13, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
public class SectionResult
{
  private Id assessmentId;
  private Id sectionId;
  private Element element;
  private final static org.apache.log4j.Logger LOG =
      org.apache.log4j.Logger.getLogger(SectionResult.class);
  
  public SectionResult(SectionResultBean bean)
  {
    this.assessmentId = IdHelper.stringToId(bean.getSectionResultPK().getAssessmentId());
    this.sectionId = IdHelper.stringToId(bean.getSectionResultPK().getSectionId());
    
    Asset stringAsset;
    try
    {  
      stringAsset = SectionResult.getDr().getAsset(IdHelper.stringToId(bean.getElementId()));
      this.element = this.StringAssetToElement(stringAsset);
    }
    catch (DigitalRepositoryException e)
    {
      LOG.error(e); throw new Error(e);
    }
    catch (OsidException e)
    {
      LOG.error(e); throw new Error(e);
    }
    catch (ParserConfigurationException e)
    {
      LOG.error(e); throw new Error(e);
    }
    catch (SAXException e)
    {
      LOG.error(e); throw new Error(e);
    }
    catch (IOException e)
    {
      LOG.error(e); throw new Error(e);
    }
  }
  
  public static List getSectionResults(Id assessmentId)
    throws OsidException
  {
    
    List l = PersistenceService.getInstance().getQtiQueries().returnSectionResults(assessmentId.getIdString());
    Iterator iter = l.iterator(); 
    
    List result = new ArrayList();

    while(iter.hasNext())
    {
      SectionResult next = new SectionResult((SectionResultBean) iter.next());
      if((next != null) && assessmentId.equals(next.getAssessmentId()))
      {
        result.add(next);
      }
    }

    return result;
  }
  
  public static SectionResult getSectionResult(Id assessmentId, Id sectionId)
    throws OsidException
  {    
    SectionResultBean identity = makeIdentityBean(assessmentId.getIdString(), sectionId.getIdString());
    SectionResultBean sectionResultBean = retrieveSectionResultBean(identity);
    
    return new SectionResult(sectionResultBean);    
  }
  
  public static void store(Id assessmentId, Id sectionId, Element element)
    throws OsidException, IOException
  {
    SharedManager sm =
      org.navigoproject.osid.OsidManagerFactory.createSharedManager();

    DigitalRepository dr = getDr();

    String resultString = DomHelper.stringValue(element);

    String assessmentIdString = assessmentId.getIdString();
    String sectionIdString = sectionId.getIdString();
    SectionResultBean identity = makeIdentityBean(assessmentId.getIdString(), sectionId.getIdString());
    SectionResultBean found = retrieveSectionResultBean(identity);

    if(found == null)
    {
      //Create a new Asset to hold the result
      Asset newResultAsset = dr.createAsset("", "", dr.getType());

      // Store the result
      newResultAsset.updateContent(resultString);

      // Grab this new Asset's Id
      Id resultAssetId = newResultAsset.getId();
      identity.setElementId(resultAssetId.getIdString());


      PersistenceService.getInstance().getQtiQueries().persistSectionResultBean(identity);
      if(LOG.isDebugEnabled())
      {
        LOG.debug(
          "Created SectionResult in local table. Created Asset: " + resultAssetId);
      }
    }
    else //update existing data
    {
      Id assetId = sm.getId(found.getElementId());

      if(LOG.isDebugEnabled())
      {
        LOG.debug(
          "SectionResult found in local table. Updating Asset: " + assetId);
      }

      Asset foundResultAsset = dr.getAsset(assetId);
      foundResultAsset.updateContent(resultString);
    }
  }
  
  private static SectionResultBean makeIdentityBean(
    String assessmentId, String sectionId)
  {
    SectionResultBean identity = new SectionResultBean();
    identity.getSectionResultPK().setAssessmentId(assessmentId);
    identity.getSectionResultPK().setSectionId(sectionId);

    return identity;
  }

  private static SectionResultBean retrieveSectionResultBean(SectionResultBean identity)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getSectionResultBean(" + identity + ")");
    }

    SectionResultPK srpk = new SectionResultPK(identity.getSectionResultPK().getAssessmentId(),
                                         identity.getSectionResultPK().getSectionId());
                                         
    SectionResultBean found = PersistenceService.getInstance().getQtiQueries().retrieveSectionResultBean(srpk);

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

  /**
   * @return
   */
  public Id getSectionId()
  {
    return this.sectionId;
  }

}
