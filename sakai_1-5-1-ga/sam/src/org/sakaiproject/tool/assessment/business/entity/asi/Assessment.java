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


package org.sakaiproject.tool.assessment.business.entity.asi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.ElementImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.sakaiproject.tool.assessment.business.entity.constants.QTIConstantStrings;
import org.sakaiproject.tool.assessment.util.XmlUtil;

/**
 * @todo resolve ids
 * <p>Copyright: Copyright (c) 2003-5</p>
 * <p>Organization: Sakai Project</p>
 * @author casong
 * @author Ed Smiley esmiley@stanford.edu
 * @version $Id: Assessment.java,v 1.3 2005/01/12 21:50:33 esmiley.stanford.edu Exp $
 */
public class Assessment
  extends ASIBaseClass
{
  private final static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(Assessment.class);
  public final String basePath =
    QTIConstantStrings.QUESTESTINTEROP + "/" + QTIConstantStrings.ASSESSMENT;
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
  }

  /**
   * Creates a new Assessment object.
   *
   * @param document
   */
  public Assessment(Document document)
  {
    super(document);
    this.sections = new HashMap();
    this.items = new HashMap();
  }

  /**
   * Creates a new Assessment object.
   *
   * @param xml
   */
  public Assessment(String xml)
  {
    super(xml);
    this.sections = new HashMap();
    this.items = new HashMap();
  }

  /**
   * decomposes an assessment xml file and save each item and section(including
   * sections with another section) into their correspondant digital
   * repository (DR).  Example:
   *
   * @return
   */
  public Assessment decompose()
  {
    LOG.debug("decompose()");

    Assessment result = null;
    try
    {
      NodeList childNodes = this.getDocument().getChildNodes();
      int size = childNodes.getLength();
      for(int i = 0; i < size; i++)
      {
        Node childNode = childNodes.item(i);
        String nodeName = childNode.getNodeName();
        if(QTIConstantStrings.ASSESSMENT.equals(nodeName))
        {
          decomposeSections(childNode);
        }
        else
        {
          decomposeAssessment(childNode);
        }
      }

      result = new Assessment(this.getDocument());
    }
    catch(Exception ex)
    {
      LOG.error(ex.getMessage(), ex);
    }

    return result;
  }

  /**
   * decomposes the assessment node contained in this node.
   *
   * @param node the possible assessment parent node.
   */
  private void decomposeAssessment(Node node)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("decomposeAssessment(Node " + node + ")");
    }

    NodeList childNodes = node.getChildNodes();
    int size = childNodes.getLength();
    for(int i = 0; i < size; i++)
    {
      Node childNode = childNodes.item(i);
      String nodeName = childNode.getNodeName();
      if(QTIConstantStrings.ASSESSMENT.equals(nodeName))
      {
        decomposeSections(childNode);
      }
      else
      {
        decomposeAssessment(childNode);
      }
    }
  }

  /**
   * decomposes the section nodes contained in this assessment node.
   *
   * @param assessment the assessment node.
   */
  private void decomposeSections(Node assessment)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("decomposeSections(Node " + assessment + ")");
    }

    NodeList childNodes = assessment.getChildNodes();
    int size = childNodes.getLength();
    for(int i = 0; i < size; i++)
    {
      Node childNode = childNodes.item(i);
      String nodeName = childNode.getNodeName();
      NodeList sectionList = childNode.getChildNodes();
      if(QTIConstantStrings.SECTION.equals(nodeName))
      {
        checkSectionNodeForItems(childNode);
      }

      if(QTIConstantStrings.ITEM.equals(nodeName))
      {
        replaceItemNode(childNode);
      }
    }
  }

  /**
   * checks whether the section node is a leaf node, replaces it with
   * sectionref node if it is a leaf section node, process each section
   * children node if it is not a leaf section node.
   *
   * @param sectionNode the section node that might contains other sections as
   *        children nodes.
   */
  private void checkSectionNodeForItems(Node sectionNode)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("checkSectionNode(Node " + sectionNode + ")");
    }

    boolean contains = false;
    NodeList nodeList = sectionNode.getChildNodes();
    int size = nodeList.getLength();
    for(int i = 0; i < size; i++)
    {
      Node childNode = nodeList.item(i);
      String childNodeName = childNode.getNodeName();
      if(QTIConstantStrings.SECTION.equals(childNodeName))
      {
        checkSectionNodeForItems(childNode);
      }

      if(QTIConstantStrings.ITEM.equals(childNodeName))
      {
        replaceItemNode(childNode);
      }
    }

    replaceSectionNode(sectionNode);
  }

  /**
   * replaces this section node wtih sectionref node.
   *
   * @param sectionNode the section node to be replaced by a new sectionref
   *        node.
   */
  private void replaceSectionNode(Node sectionNode)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("replaceSectionNode(Node " + sectionNode + ")");
    }

    try
    {
      Document sectionDoc = XmlUtil.createDocument();
      Node sectionCopy = sectionDoc.importNode(sectionNode, true);
      sectionDoc.appendChild(sectionCopy);
      Section section = new Section(sectionDoc);
//      Asset asset = item.save();
//      String assetId = asset.getId().getIdString();
      String assetId = "***DEBUGSECTIONSAVE***";

      ((Element) sectionNode).setAttribute(QTIConstantStrings.IDENT, assetId);

      Element newNode =
        this.getDocument().createElement(QTIConstantStrings.SECTIONREF);
      newNode.setAttribute(QTIConstantStrings.LINKREFID, assetId);
      sectionNode.getParentNode().replaceChild(newNode, sectionNode);
    }
    catch(Exception ex)
    {
      LOG.error(ex.getMessage(), ex);
    }
  }

  /**
   * replaces this item node with itemref node.
   *
   * @param itemNode item node to be replaced.
   */
  private void replaceItemNode(Node itemNode)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("replaceItemNode(Node " + itemNode + ")");
    }

    try
    {
      Document itemDoc = XmlUtil.createDocument();
      Node itemCopy = itemDoc.importNode(itemNode, true);
      itemDoc.appendChild(itemCopy);
      Item item = new Item(itemDoc);
//      Asset asset = item.save();
//      String assetId = asset.getId().getIdString();
      String assetId = "***DEBUGITEMSAVE***";

      ((Element) itemNode).setAttribute(QTIConstantStrings.IDENT, assetId);

      Element newNode =
        this.getDocument().createElement(QTIConstantStrings.ITEMREF);
      newNode.setAttribute(QTIConstantStrings.LINKREFID, assetId);
      Node parent = itemNode.getParentNode();
      itemNode = parent.replaceChild(newNode, itemNode);
    }
    catch(Exception ex)
    {
      LOG.error(ex.getMessage(), ex);
    }
  }


  /**
   *
   */
  public void selectAndOrder()
  {
    LOG.debug("selectAndOrder()");

    ArrayList selectedList = this.selectSections(basePath);
    this.orderSections(basePath, selectedList);
  }

  /**
   * methods for meta data
   *
   * @param fieldlabel
   *
   * @return
   */
  public String getFieldentry(String fieldlabel)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getFieldentry(String " + fieldlabel + ")");
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
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "setFieldentry(String " + fieldlabel + ", String " + setValue + ")");
    }

    String xpath =
      "questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='" +
      fieldlabel + "']/following-sibling::fieldentry";
    super.setFieldentry(xpath, setValue);
  }

  /**
   *
   *
   * @param fieldlabel
   */
  public void createFieldentry(String fieldlabel)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("createFieldentry(String " + fieldlabel + ")");
    }

    String xpath = basePath + "/qtimetadata";
    super.createFieldentry(xpath, fieldlabel);
  }

  /**
   * ASI OKI Interface implementation
   *
   * @param sectionId
   */
  /**
   *
   *
   * @param sectionId
   */
  public void addSectionRef(String sectionId)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("addSection(String " + sectionId + ")");
    }

    String xpath = basePath;
    CoreDocumentImpl document = new CoreDocumentImpl();
    Element element = new ElementImpl(document, QTIConstantStrings.SECTIONREF);
    element.setAttribute(QTIConstantStrings.LINKREFID, sectionId);
    this.addElement(xpath, element);
  }

  /**
   *
   *
   * @param sectionId
   */
  public void removeSectionRef(String sectionId)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("removeSectionRef(String " + sectionId + ")");
    }

    String xpath =
      basePath + "/" + QTIConstantStrings.SECTIONREF + "[@" +
      QTIConstantStrings.LINKREFID + "='" + sectionId + "']";
    this.removeElement(xpath);
  }

  /**
   *
   */
  public void removeSectionRefs()
  {
    LOG.debug("removeSectionRefs()");
    String xpath = basePath + "/" + QTIConstantStrings.SECTIONREF;
    this.removeElement(xpath);
  }

  /**
   *
   *
   * @return
   */
  public List getSectionRefs()
  {
    LOG.debug("getSectionRefs()");
    String xpath = basePath + "/" + QTIConstantStrings.SECTIONREF;

    return this.selectNodes(xpath);
  }

  public Collection getSections()
  {
  	return this.sections.values();
  }

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
    LOG.debug("getSectionRefIds()");
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
   *
   *
   * @param sectionRefIds
   */
  public void orderSectionRefs(ArrayList sectionRefIds)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("orderSectionRefs(ArrayList " + sectionRefIds + ")");
    }

    this.removeSectionRefs();
    int size = sectionRefIds.size();
    for(int i = 0; i < size; i++)
    {
      this.addSectionRef((String) sectionRefIds.get(i));
    }
  }

  /**
   * Recompose assessment methods
   *
   * @throws ParserConfigurationException
   * @throws SAXException
   * @throws IOException
   */
  /**
   *
   *
   * @throws ParserConfigurationException
   * @throws SAXException
   * @throws IOException
   */
  public void recompose(boolean dealWithFormat)
    throws ParserConfigurationException, SAXException, IOException
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("recompose(" + dealWithFormat + ")");
    }
    this.realize(dealWithFormat, null);
  }

  public void realize(boolean dealWithFormat, Calendar date)
    throws ParserConfigurationException, SAXException, IOException
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("realize(" + dealWithFormat + ", " + date + ")");
    }
    NodeList childNodes = this.getDocument().getChildNodes();
    int size = childNodes.getLength();
    for(int i = 0; i < size; i++)
    {
      Node childNode = childNodes.item(i);
      String nodeName = childNode.getNodeName();
      if(QTIConstantStrings.ASSESSMENT.equals(nodeName))
      {
        recomposeSection(childNode, dealWithFormat, date);
      }
      else
      {
        recomposeAssessment(childNode, dealWithFormat, date);
      }
    }
    //after recompose assessment wrap all mattext in assessment level.
    if(dealWithFormat){
      this.wrappingMattext();
    }
  }

  /**
   *
   *
   * @param node
   */
  public void recomposeAssessment(Node node, boolean dealWithFormat, Calendar date)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("recomposeAssessment(Node " + node + ")");
    }

    NodeList childNodes = node.getChildNodes();
    int size = childNodes.getLength();
    for(int i = 0; i < size; i++)
    {
      Node childNode = childNodes.item(i);
      String nodeName = childNode.getNodeName();
      if(QTIConstantStrings.ASSESSMENT.equals(nodeName))
      {
        recomposeSection(childNode, dealWithFormat, date);
      }
      else
      {
        recomposeAssessment(childNode, dealWithFormat, date);
      }
    }
  }

  /**
   *
   *
   * @param assessment
   */
  public void recomposeSection(Node assessment, boolean dealWithFormat, Calendar date)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("recomposeSection(Node " + assessment + ")");
    }

    NodeList childNodes = assessment.getChildNodes();
    int size = childNodes.getLength();
    for(int i = 0; i < size; i++)
    {
      Node childNode = childNodes.item(i);
      String nodeName = childNode.getNodeName();
      NodeList sectionList = childNode.getChildNodes();
      if(QTIConstantStrings.SECTIONREF.equals(nodeName))
      {
        replaceSectionRefNode(childNode, dealWithFormat, date);
      }

      if(QTIConstantStrings.ITEMREF.equals(nodeName))
      {
        replaceItemRefNode(childNode, dealWithFormat, date);
      }
    }
  }

  /**
   *
   *
   * @param sectionNode
   */
  private void checkSectionNodeForItemRefs(Node sectionNode, boolean dealWithFormat, Calendar date)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("checkSectionNodeForItemRefs(Node " + sectionNode + ")");
    }

    NodeList nodeList = sectionNode.getChildNodes();
    int size = nodeList.getLength();
    for(int i = 0; i < size; i++)
    {
      Node childNode = nodeList.item(i);
      String childNodeName = childNode.getNodeName();
      if(QTIConstantStrings.SECTIONREF.equals(childNodeName))
      {
        replaceSectionRefNode(childNode, dealWithFormat, date);
      }

      if(QTIConstantStrings.ITEMREF.equals(childNodeName))
      {
        replaceItemRefNode(childNode, dealWithFormat, date);
      }
    }
  }

  /**
   *
   *
   * @param sectionRef
   */
  protected void replaceSectionRefNode(Node sectionRef, boolean dealWithFormat,
    Calendar date)
  {
    LOG.debug("replaceSectionRefNode()");
    try
    {
      String sectionId =
        ((Element) sectionRef).getAttribute(QTIConstantStrings.LINKREFID);
      Section section = new Section();
      this.sections.put(sectionId, section);
      Node sectionNode = section.getDocument().getFirstChild();
      Node newNode = this.getDocument().importNode(sectionNode, true);
      sectionRef.getParentNode().replaceChild(newNode, sectionRef);
      checkSectionNodeForItemRefs(newNode, dealWithFormat, date);
    }
    catch(Exception ex)
    {
      LOG.error("Unable to replace sectionRefNode: " + ex.getMessage(), ex);
    }
  }

  /**
   *
   *
   * @param itemRef
   */
  public void replaceItemRefNode(Node itemRef, boolean dealWithFormat, Calendar date)
  {
    LOG.debug("replaceItemRefNode()");
    try
    {
      String itemId =
        ((Element) itemRef).getAttribute(QTIConstantStrings.LINKREFID);
//      RepositoryManager rm = new RepositoryManager();
//      Item item = new Item(itemIdReplace);
      Item item = new Item();
//      if(dealWithFormat)
//      {
//        item.processSmartTextHtml();
//      }
      this.items.put(itemId, item);
      Node itemNode = item.getDocument().getFirstChild();
      Node newNode = this.getDocument().importNode(itemNode, true);
      itemRef.getParentNode().replaceChild(newNode, itemRef);
    }
    catch(Exception ex)
    {
      LOG.error("Unable to replace itemRefNode: " + ex.getMessage(), ex);
    }
  }

  /**
   *
   *
   * @param fieldlabel
   * @param setValue
   */
  public void setInstructorFieldEntry(String fieldlabel, String setValue)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "setInstructorFieldEntry(String " + fieldlabel + ", String " +
        setValue + ")");
    }

    String xpath =
      "questestinterop/assessment/qtimetadata[1]/qtimetadatafield/fieldlabel[text()='" +
      fieldlabel + "']/following-sibling::fieldentry";
    super.setFieldentry(xpath, setValue);
  }

  /**
   *
   *
   * @param fieldlabel
   * @param setValue
   */
  public void setStudentFieldEntry(String fieldlabel, String setValue)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "setStudentFieldEntry(String " + fieldlabel + ", String " + setValue +
        ")");
    }

    String xpath =
      "questestinterop/assessment/qtimetadata[2]/qtimetadatafield/fieldlabel[text()='" +
      fieldlabel + "']/following-sibling::fieldentry";
    super.setFieldentry(xpath, setValue);
  }

  /**
   *
   *
   * @param fieldlabel
   */
  public void createInstructorFieldEntry(String fieldlabel)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("createInstructorFieldEntry(String " + fieldlabel + ")");
    }

    String xpath = basePath + "/qtimetadata[1]";
    super.createFieldentry(xpath, fieldlabel);
  }

  /**
   *
   *
   * @param fieldlabel
   */
  public void createStudentFieldEntry(String fieldlabel)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("createStudentFieldEntry(String " + fieldlabel + ")");
    }

    String xpath = basePath + "/qtimetadata[2]";
    super.createFieldentry(xpath, fieldlabel);
  }

  public String getTitle()
  {
    String title = "";
    String xpath = "questestinterop/assessment";
    List list = this.selectNodes(xpath);
    if(list.size()>0)
    {
      Element element = (Element)list.get(0);
      title = element.getAttribute("title");
    }
    return title;
  }
	public String getIdent()
	{
		String ident = "";
		String xpath = "questestinterop/assessment";
		List list = this.selectNodes(xpath);
		if(list.size()>0)
		{
			Element element = (Element)list.get(0);
			ident = element.getAttribute("ident");
		}
		return ident;
	}
  public void setIdent(String ident)
  {
    String xpath = "questestinterop/assessment";
    List list = this.selectNodes(xpath);
    if(list.size()>0)
    {
      Element element = (Element)list.get(0);
      element.setAttribute("ident", ident);
    }
  }
  public void setTitle(String title)
  {
    String xpath = "questestinterop/assessment";
    List list = this.selectNodes(xpath);
    if(list.size()>0)
    {
      Element element = (Element)list.get(0);
      element.setAttribute("title", title);
    }
  }
}
