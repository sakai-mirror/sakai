/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2003-2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
*
**********************************************************************************/


package org.sakaiproject.tool.assessment.qti.asi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.ElementImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.sakaiproject.tool.assessment.qti.constants.QTIConstantStrings;

/**
 * <p>Copyright: Copyright (c) 2003-5</p>
 * <p>Organization: Sakai Project</p>
 * @author casong
 * @author Ed Smiley esmiley@stanford.edu
 * @version $Id$
 */
public class Assessment  extends ASIBaseClass
{
  private static Log log = LogFactory.getLog(Assessment.class);
  private String basePath;
  private Map sections;
  private Map items;

  /**
   * Explicitly setting serialVersionUID insures future versions can be
   * successfully restored. It is essential this variable name not be changed
   * to SERIALVERSIONUID, as the default serialization methods expects this
   * exact name.
   */
  private static final long serialVersionUID = 1;

  /**
   * Creates a new Assessment object.
   */
  protected Assessment()
  {
    super();
    this.sections = new HashMap();
    this.items = new HashMap();
    this.basePath = QTIConstantStrings.QUESTESTINTEROP + "/" +
                    QTIConstantStrings.ASSESSMENT;
  }

  /**
   * Creates a new Assessment object.
   *
   * @param document the Document containing the assessment.
   */
  public Assessment(Document document)
  {
    super(document);
    this.sections = new HashMap();
    this.items = new HashMap();
    this.basePath = QTIConstantStrings.QUESTESTINTEROP + "/" +
                    QTIConstantStrings.ASSESSMENT;
  }

  /**
   * Method for meta data.
   *
   * @todo use QTIConstantStrings:
   *   //QTIMetaData
  public static String QTIMETADATAFIELD = "qtimetadatafield";
  public static String FIELDLABEL = "fieldlabel";
  public static String FIELDENTRY = "fieldentry";

   *
   * @param fieldlabel the fieldlabel
   *
   * @return the entry
   */
  public String getFieldentry(String fieldlabel)
  {
    if(log.isDebugEnabled())
    {
      log.debug("getFieldentry(String " + fieldlabel + ")");
    }

    String xpath =
      basePath + "/qtimetadata/qtimetadatafield/fieldlabel[text()='" +
      fieldlabel + "']/following-sibling::fieldentry";

    return super.getFieldentry(xpath);
  }

  /**
   *
   *
   * @param fieldlabel
   * @param setValue
   */
  public void setFieldentry(String fieldlabel, String setValue)
  {
    if(log.isDebugEnabled())
    {
      log.debug(
        "setFieldentry(String " + fieldlabel + ", String " + setValue + ")");
    }

    String xpath =
      "questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='" +
      fieldlabel + "']/following-sibling::fieldentry";
    super.setFieldentry(xpath, setValue);
  }
  /**
   * Add a section ref with section Id sectionId.
   *
   * @param sectionId
   */
  public void addSectionRef(String sectionId)
  {
    if(log.isDebugEnabled())
    {
      log.debug("addSection(String " + sectionId + ")");
    }

    String xpath = basePath;
    CoreDocumentImpl document = new CoreDocumentImpl();
    Element element = new ElementImpl(document, QTIConstantStrings.SECTIONREF);
    element.setAttribute(QTIConstantStrings.LINKREFID, sectionId);
    this.addElement(xpath, element);
  }

  /**
   * Remove a section ref with section Id sectionId.
   *
   * @param sectionId
   */
  public void removeSectionRef(String sectionId)
  {
    if(log.isDebugEnabled())
    {
      log.debug("removeSectionRef(String " + sectionId + ")");
    }

    String xpath =
      basePath + "/" + QTIConstantStrings.SECTIONREF + "[@" +
      QTIConstantStrings.LINKREFID + "='" + sectionId + "']";
    this.removeElement(xpath);
  }

  /**
   * Remove all section refs.
   */
  public void removeSectionRefs()
  {
    log.debug("removeSectionRefs()");
    String xpath = basePath + "/" + QTIConstantStrings.SECTIONREF;
    this.removeElement(xpath);
  }

  /**
   * Get a collection of section refs.
   *
   * @return
   */
  public List getSectionRefs()
  {
    log.debug("getSectionRefs()");
    String xpath = basePath + "/" + QTIConstantStrings.SECTIONREF;

    return this.selectNodes(xpath);
  }

  /**
   * Get a collection of sections.
   * @return the sections
   */
  public Collection getSections()
  {
  	return this.sections.values();
  }

  /**
   * Get a collection of items.
   * @return the items
   */
  public Collection getItems()
  {
  	return this.items.values();
  }

  /**
   *
   *
   * @return
   */
  public List getSectionRefIds()
  {
    log.debug("getSectionRefIds()");
    List refs = this.getSectionRefs();
    List ids = new ArrayList();
    int size = refs.size();
    for(int i = 0; i < size; i++)
    {
      Element ref = (ElementImpl) refs.get(0);
      String idString = ref.getAttribute(QTIConstantStrings.LINKREFID);
      ids.add(idString);
    }

    return ids;
  }

  /**
   * Assessment title.
   * @return title
   */
  public String getTitle()
  {
    String title = "";
    String xpath = basePath;
    List list = this.selectNodes(xpath);
    if(list.size()>0)
    {
      Element element = (Element)list.get(0);
      title = element.getAttribute("title");
    }
    return title;
  }

  /**
   * Assessment id (ident attribute)
   * @return ident
   */
	public String getIdent()
	{
		String ident = "";
		String xpath = basePath;
		List list = this.selectNodes(xpath);
		if(list.size()>0)
		{
			Element element = (Element)list.get(0);
			ident = element.getAttribute("ident");
		}
		return ident;
	}


  /**
   * Assessment id (ident attribute)
   * @param ident the ident
   */
  public void setIdent(String ident)
  {
    String xpath = basePath;
    List list = this.selectNodes(xpath);
    if(list.size()>0)
    {
      Element element = (Element)list.get(0);
      element.setAttribute("ident", ident);
    }
  }

  /**
   * Assessment title.
   * @param title
   */
  public void setTitle(String title)
  {
    String xpath = basePath;
    List list = this.selectNodes(xpath);
    if(list.size()>0)
    {
      Element element = (Element)list.get(0);
      element.setAttribute("title", escapeXml(title));
    }
  }

  /**
   * Base XPath for the assessment.
   * @return
   */
  public String getBasePath()
  {
    return basePath;
  }

  /**
   * Base XPath for the assessment.
   * @param basePath
   */
  public void setBasePath(String basePath)
  {
    this.basePath = basePath;
  }
}


