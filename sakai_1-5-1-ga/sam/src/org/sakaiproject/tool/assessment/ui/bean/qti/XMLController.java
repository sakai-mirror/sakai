package org.sakaiproject.tool.assessment.ui.bean.qti;

import java.io.InputStream;
import java.io.Serializable;

import javax.faces.context.FacesContext;

import org.w3c.dom.Document;

import org.sakaiproject.tool.assessment.business.entity.helper.AuthoringHelper;
import org.sakaiproject.tool.assessment.business.entity.helper.AuthoringXml;
import org.sakaiproject.tool.assessment.util.XmlUtil;

/**
 * <p>Bean for QTI XML or XML fragments and descriptive information. </p>
 * <p>Used to maintain information or to dump XML to client.</p>
 * <p>Copyright: Copyright (c) 2004 Sakai</p>
 * @author Ed Smiley esmiley@stanford.edu
 * @version $Id: XMLController.java,v 1.25 2005/01/26 17:55:29 esmiley.stanford.edu Exp $
 */

public class XMLController implements Serializable
{
  private XMLDisplay xmlBean;
  private String documentType;
  private String id;

  private static final AuthoringXml ax = new AuthoringXml();

  public XMLController()
  {
  }

  public XMLDisplay getXmlBean()
  {
    return xmlBean;
  }

  public void setXmlBean(XMLDisplay xmlBean)
  {
    this.xmlBean = xmlBean;
  }

  /**
   * sets needed info in xml display bean when id set to assessment id
   * @return
   */
  public String displayAssessmentXml()
  {
    documentType = ax.ASSESSMENT;
    return display();
  }

  public String displaySectionXmlTemplate()
  {
    documentType = ax.SECTION;
    return display();
  }

  public String displayItemXml()
  {
    documentType = ax.ITEM_MCSC; // this is just a default, we will override
    item();
    return "xmlDisplay";
  }

  public String display()
  {

    try
    {
      if (ax.isAssessment(documentType))
      {
        assessment();
      }
      else if (ax.isSection(documentType))
      {
        section();
      }
      else if (ax.isItem(documentType))
      {
        System.out.println("item type detected");
        item();
      }
      else if (ax.isSurveyFragment(documentType))
      {
        frag();
      }
    }
    catch (Exception ex)
    {
      xmlBean.setXml("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + "\n" +
                "<error-report>" + "\n" +
                ex.toString() + "\n" +
                "</error-report>" + "\n" );
      xmlBean.setDescription(ex.toString());
      xmlBean.setName("error");
      xmlBean.setId("");
      ex.printStackTrace();
    }
//    System.out.println("debug" +xmlBean.getXml());

    return "xmlDisplay";

  }

  public String getDocumentType()
  {
    return documentType;
  }

  public void setDocumentType(String documentType)
  {
    this.documentType = documentType;
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }


  private void assessment()
  {
    xmlBean.setId(id);
    xmlBean.setName(documentType);
    System.out.println("assessment() id=" + id);
    AuthoringHelper authHelper = new AuthoringHelper();

    if (id !=null && id.length()>0) // return populated xml from template
    {
      Document doc = authHelper.getAssessment(id);
      xmlBean.setDescription("Exported QTI XML Copyright: Copyright (c) 2005 Sakai");
      xmlBean.setName("assessment " + id);
      xmlBean.setXml(XmlUtil.getDOMString(doc));
    }
    else // return  xml template
    {
      xmlBean.setDescription("assessment template");
      String xml = ax.getTemplateAsString(authHelper.getBlankAssessmentTemplateContextStream());
      xmlBean.setXml(xml);
    }
  }

  /**
   * @todo add code to populate from SectionHelper
   */
  private void section()
  {
    xmlBean.setId(id);
    if (id !=null && id.length()>0)
    {
      xmlBean.setDescription("section fragment id=" + id);
      xmlBean.setName(documentType);// get from document later
      InputStream is = ax.getTemplateInputStream(ax.SECTION,FacesContext.getCurrentInstance());
      xmlBean.setXml(ax.getTemplateAsString(is));
    }
    else
    {
      xmlBean.setDescription("section template");
      xmlBean.setName(documentType);// get from document later
      InputStream is = ax.getTemplateInputStream(ax.SECTION,FacesContext.getCurrentInstance());
      xmlBean.setXml(ax.getTemplateAsString(is));
    }
  }

  /**
   * read in XML from item or item template
   */
  private void item()
  {
    System.out.println("got to item.");
    xmlBean.setId(id);
    if (id !=null && id.length()>0)
    {
      AuthoringHelper ah = new AuthoringHelper();
      xmlBean.setDescription("Exported QTI XML Copyright: Copyright (c) 2005 Sakai");
      xmlBean.setName("item " + id);// get from document later
      xmlBean.setXml(XmlUtil.getDOMString(ah.getItem(id)));
    }
    else
    {
      xmlBean.setDescription("item template");
      xmlBean.setName(documentType);// get from document later
      InputStream is = ax.getTemplateInputStream(documentType, FacesContext.getCurrentInstance());
      xmlBean.setXml(ax.getTemplateAsString(is));
    }
  }

  private void frag()
  {
    xmlBean.setDescription("survey item fragment template");
    xmlBean.setName(documentType);// get from document later
    InputStream is = ax.getTemplateInputStream(documentType, FacesContext.getCurrentInstance());
    xmlBean.setXml(ax.getTemplateAsString(is));
  }

}