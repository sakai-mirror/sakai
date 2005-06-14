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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.ElementImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.sakaiproject.tool.assessment.business.entity.constants.QTIConstantStrings;
import org.sakaiproject.tool.assessment.business.entity.helper.item.ItemHelper;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.SectionDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.shared.TypeIfc;

/**
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organization: Sakai Project</p>
 * @author rshastri
 * @author Ed Smiley esmiley@stanford.edu
 * @version $Id: Section.java,v 1.8 2005/02/01 19:12:13 esmiley.stanford.edu Exp $
 */
public class Section extends ASIBaseClass
{
  private final static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(Section.class);
  public final String basePath = QTIConstantStrings.SECTION;
  private Map items;

  /**
   * Explicitly setting serialVersionUID insures future versions can be
     * successfully restored. It is essential this variable name not be changed
     * to SERIALVERSIONUID, as the default serialization methods expects this
   * exact name.
   */
  private static final long serialVersionUID = 1;

  /**
   * Creates a new Section object.
   */
  public Section()
  {
    super();
  }

  /**
   * Creates a new Section object.
   *
   * @param document DOCUMENTATION PENDING
   */
  public Section(Document document)
  {
    super(document);
  }

  /**
   * Creates a new Section object.
   *
   * @param xml DOCUMENTATION PENDING
   */
  public Section(String xml)
  {
    super(xml);
  }

  public void setIdent(String ident)
  {
    String xpath = "section";
    List list = this.selectNodes(xpath);
    if (list.size() > 0)
    {
      Element element = (Element) list.get(0);
      element.setAttribute("ident", ident);
    }
  }

  public void setTitle(String title)
  {
    String xpath = "section";
    List list = this.selectNodes(xpath);
    if (list.size() > 0)
    {
      Element element = (Element) list.get(0);
      element.setAttribute("title", title);
    }
  }

  /**
   * Update XML from perisistence
   * @param section
   */
  public void update(SectionDataIfc section)
  {
    // identity
    setIdent("" + section.getSectionId());
    setTitle(section.getTitle());
    // metadata
    setFieldentry("SECTION_OBJECTIVE", section.getDescription());
    // Where the heck do these come from?  Looks like not being used.
    // If required we could extract keyworkds by weighting, and
    // set rubrics identical to description, or, we could eliminate these from XML.
    setFieldentry("SECTION_KEYWORD", "");
    setFieldentry("SECTION_RUBRIC", "");
    // items
    ArrayList items = section.getItemArray();
    addItems(items);
  }

//  /**
//   * DOCUMENTATION PENDING
//   *
//   * @return DOCUMENTATION PENDING
//   *
//   * @throws ParserConfigurationException DOCUMENTATION PENDING
//   * @throws SAXException DOCUMENTATION PENDING
//   * @throws IOException DOCUMENTATION PENDING
//   * @throws DOMException DOCUMENTATION PENDING
//   * @throws SharedException DOCUMENTATION PENDING
//   * @throws DigitalRepositoryException DOCUMENTATION PENDING
//   */
//  protected Asset save()
//    throws ParserConfigurationException, SAXException, IOException,
//      DOMException, SharedException, DigitalRepositoryException
//  {
//    LOG.debug("save()");
//    Asset asset = super.save(QTIConstantStrings.SECTION);
//
//    return asset;
//  }

  /**
   * DOCUMENTATION PENDING
   */
  public void selectAndOrder()
  {
    LOG.debug("selectAndOrder()");
    ArrayList selectedList = this.selectItems();
    this.orderItems(selectedList);
    ArrayList selectedSections = this.selectSections(basePath);
    this.orderSections(basePath, selectedSections);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private ArrayList selectItems()
  {
    LOG.debug("selectItems()");

    ArrayList items = new ArrayList();

    //    try
    //    {
    String xpath = basePath + "/" + QTIConstantStrings.SELECTION_ORDERING +
                   "/";
    String selectionXPath = xpath + QTIConstantStrings.SELECTION;
    String orderingXPath = xpath + QTIConstantStrings.ORDER;

    List selectNodes = this.selectNodes(selectionXPath);
    List orderNodes = this.selectNodes(orderingXPath);

    int selectNodeSize = selectNodes.size();
    for (int i = 0; i < selectNodeSize; i++)
    {
      Element selectElement = (Element) selectNodes.get(i);
//      items.addAll(processSelectElement(selectElement));
    }

    if (selectNodeSize == 0)
    {
      items.addAll(this.getAllItems());
    }

    //    }
    //    catch(Exception ex)
    //    {
    //      LOG.error(ex.getMessage(), ex);
    //    }
    removeItems();

    return items;
  }

//  /**
//   * DOCUMENTATION PENDING
//   *
//   * @param selectElement DOCUMENTATION PENDING
//   *
//   * @return DOCUMENTATION PENDING
//   */
//  private List processSelectElement(Element selectElement)
//  {
//    if(LOG.isDebugEnabled())
//    {
//      LOG.debug("processSelectElement(Element " + selectElement + ")");
//    }
//
//    int selectNumber = -1;
//    String sourceBankId = null;
//
//    NodeList childNodes = selectElement.getChildNodes();
//    if(childNodes != null && childNodes.getLength()>0)
//    {
//      for(int i=0; i<childNodes.getLength(); i++)
//      {
//        Node node = childNodes.item(i);
//        String nodeName = node.getNodeName();
//        if(QTIConstantStrings.SELECTION_NUMBER.equalsIgnoreCase(nodeName))
//        {
//          selectNumber = Integer.parseInt(node.getNodeValue());
//        }
//        if(QTIConstantStrings.SOURCEBANK_REF.equalsIgnoreCase(nodeName))
//        {
//          sourceBankId = node.getNodeValue().trim();
//        }
//      }
//    }
//    // there is no select number and sourceBank_ref
//    // then select all items within this section.
//    if((selectNumber == -1) && (sourceBankId == null))
//    {
//      return getAllItems();
//    }
//
//    // there is select number but no sourceBank_ref
//    // then select number of items within this section.
//    if((selectNumber > 0) && (sourceBankId == null))
//    {
//      return getNumOfItems(selectNumber);
//    }
//
//    // there is select number and sourceBank_ref
//    // then select number of items within specified sourcebank.
//    if((selectNumber > 0) && (sourceBankId != null))
//    {
//      return getNumberofItemsFromBank(sourceBankId, selectNumber);
//    }
//
//    // there is sourceBank_ref but no selectNumber
//    // then select all items within specified sourcebank.
//    if((selectNumber == -1) && (sourceBankId != null))
//    {
//      return getAllItemsFromBank(sourceBankId);
//    }
//
//    return null;
//  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param selectNumber DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private List getNumOfItems(int selectNumber)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getNumOfItems(int " + selectNumber + ")");
    }

    List list = new ArrayList();
    List allItems = getAllItems();
    long seed = System.currentTimeMillis();
    int allItemSize = allItems.size();
    Random random = new Random(seed);
    while (list.size() < selectNumber)
    {
      int randomNum = random.nextInt(allItemSize);
      Object item = allItems.get(randomNum);
      if (!list.contains(item))
      {
        list.add(item);
      }
    }

    return list;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private List getAllItems()
  {
    LOG.debug("getAllItems()");

    String xpath = basePath + "/" + QTIConstantStrings.ITEM;

    return this.selectNodes(xpath);
  }

//  /**
//   * DOCUMENTATION PENDING
//   *
//   * @param sourceBankId DOCUMENTATION PENDING
//   *
//   * @return DOCUMENTATION PENDING
//   */
//  private List getAllItemsFromBank(String sourceBankId)
//  {
//    if(LOG.isDebugEnabled())
//    {
//      LOG.debug("getAllItemsFromBank(String " + sourceBankId + ")");
//    }
//
//    List list = new ArrayList();
//
//    ObjectBank bank = new ObjectBank();
//    bank.setBankId(sourceBankId);
//    list = bank.getAllItems();
//
//    return list;
//
//  }

//  /**
//   * DOCUMENTATION PENDING
//   *
//   * @param sourceBankId DOCUMENTATION PENDING
//   * @param number DOCUMENTATION PENDING
//   *
//   * @return DOCUMENTATION PENDING
//   */
//  private List getNumberofItemsFromBank(String sourceBankId, int number)
//  {
//    if(LOG.isDebugEnabled())
//    {
//      LOG.debug(
//        "getNumberofItemsFromBank(String " + sourceBankId + ", int " + number +
//        ")");
//    }
//
//    QuestionPoolService delegate = new QuestionPoolService();
//    Id id = IdHelper.stringToId(sourceBankId);
//    HttpServletRequest request = (HttpServletRequest) ThreadLocalMapProvider.getMap().get(Constants.HTTP_SERVLET_REQUEST);
//
//    List list = new ArrayList();
//    try
//    {
//      Agent agent = org.navigoproject.osid.OsidManagerFactory.getAgent();
//      QuestionPool  pool = delegate.getPool(id, agent);
//      Collection collection = delegate.selectNrandomQuestions(pool, number, agent);
//      list = new ArrayList(collection);
//    }
//    catch (Exception e)
//    {
//      LOG.error(e.getMessage(), e);
//    }
//    return list;
//  }

  /**
   * DOCUMENTATION PENDING
   */
  private void removeItems()
  {
    LOG.debug("removeItems()");

    String xpath = basePath + "/" + QTIConstantStrings.ITEM;
    this.removeElement(xpath);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param items DOCUMENTATION PENDING
   */
  private void orderItems(ArrayList items)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("orderItems(ArrayList " + items + ")");
    }

    String xpath = basePath + "/" + QTIConstantStrings.SELECTION_ORDERING +
                   "/";
    String orderingXPath = xpath + QTIConstantStrings.ORDER;
    List orderNodes = this.selectNodes(orderingXPath);
    if ( (orderNodes != null) && (orderNodes.size() > 0))
    {
      Element order = (Element) orderNodes.get(0);
      String orderType = order.getAttribute(QTIConstantStrings.ORDER_TYPE);
      if ("Random".equalsIgnoreCase(orderType))
      {
        //Randomly order items.
        long seed = System.currentTimeMillis();
        Random rand = new Random(seed);
        int size = items.size();
        for (int i = 0; i < size; i++)
        {
          int randomNum = rand.nextInt(size);
          Object temp = items.get(i);
          items.set(i, items.get(randomNum));
          items.set(randomNum, temp);
          LOG.debug("switch item " + i + " with " + randomNum);
        }
      }
    }

    addItems(items);
  }

  /**
   * Add item list to this section document.
   * @todo read in individual item data, right now just dumping templates
   *
   * @param items list of ItemDataIfc
   */
  private void addItems(ArrayList items)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("addItems(ArrayList " + items + ")");
    }
    ItemHelper itemHelper = new ItemHelper();

    try
    {
      String xpath = basePath;
      for (int i = 0; i < items.size(); i++)
      {
        ItemDataIfc item = (ItemDataIfc) items.get(i);
        TypeIfc type = item.getType();
        Item itemXml = itemHelper.readTypeXMLItem(type);
        // update item data
        itemXml.setIdent(item.getItemIdString());
        itemXml.update(item);
        Element itemElement = (Element) itemXml.getDocument().
                              getDocumentElement();
        LOG.debug(
          "Item ident is: " + itemElement.getAttribute("ident"));
        this.addElement(xpath, itemElement);
      }
    }
    catch (Exception e)
    {
      LOG.error(e.getMessage(), e);
    }
  }


  /**
   * methods for meta data
   *
   * @param fieldlabel DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getFieldentry(String fieldlabel)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getFieldentry(String " + fieldlabel + ")");
    }

    String xpath =
      "section/qtimetadata/qtimetadatafield/fieldlabel[text()='" +
      fieldlabel +
      "']/following-sibling::fieldentry";

    return super.getFieldentry(xpath);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param fieldlabel DOCUMENTATION PENDING
   * @param setValue DOCUMENTATION PENDING
   */
  public void setFieldentry(String fieldlabel, String setValue)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug(
        "setFieldentry(String " + fieldlabel + ", String " + setValue +
        ")");
    }

    String xpath =
      "section/qtimetadata/qtimetadatafield/fieldlabel[text()='" +
      fieldlabel +
      "']/following-sibling::fieldentry";
    super.setFieldentry(xpath, setValue);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param fieldlabel DOCUMENTATION PENDING
   */
  public void createFieldentry(String fieldlabel)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("createFieldentry(String " + fieldlabel + ")");
    }

    String xpath = "section/qtimetadata";
    super.createFieldentry(xpath, fieldlabel);
  }

  /**
   * ASI OKI implementation
   *
   * @param itemId DOCUMENTATION PENDING
   */
  /**
   * DOCUMENTATION PENDING
   *
   * @param itemId DOCUMENTATION PENDING
   */
  public void addItemRef(String itemId)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("addItem(String " + itemId + ")");
    }

    String xpath = basePath;
    CoreDocumentImpl document = new CoreDocumentImpl();
    Element element = new ElementImpl(document, QTIConstantStrings.ITEMREF);
    element.setAttribute(QTIConstantStrings.LINKREFID, itemId);
    this.addElement(xpath, element);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param itemId DOCUMENTATION PENDING
   */
  public void removeItemRef(String itemId)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("removeItem(String " + itemId + ")");
    }

    String xpath =
      basePath + "/" + QTIConstantStrings.ITEMREF + "[@" +
      QTIConstantStrings.LINKREFID + "=\"" + itemId + "\"]";
    this.removeElement(xpath);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param sectionId DOCUMENTATION PENDING
   */
  public void addSectionRef(String sectionId)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("addSection(String " + sectionId + ")");
    }

    String xpath = basePath;
    CoreDocumentImpl document = new CoreDocumentImpl();
    Element element = new ElementImpl(document,
                      QTIConstantStrings.SECTIONREF);
    element.setAttribute(QTIConstantStrings.LINKREFID, sectionId);
    this.addElement(xpath, element);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param sectionId DOCUMENTATION PENDING
   */
  public void removeSectionRef(String sectionId)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("removeSection(String " + sectionId + ")");
    }

    String xpath =
      basePath + "/" + QTIConstantStrings.SECTIONREF + "[@" +
      QTIConstantStrings.LINKREFID + "=" + sectionId + "]";
    this.removeElement(xpath);
  }

  /**
   * Methods for OSID implementation
   *
   * @param itemRefIds DOCUMENTATION PENDING
   */
  public void orderItemRefs(ArrayList itemRefIds)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("orderItemRefs(ArrayList " + itemRefIds + ")");
    }

    this.removeItemRefs();
    int size = itemRefIds.size();
    for (int i = 0; i < size; i++)
    {
      this.addItemRef( (String) itemRefIds.get(i));
    }
  }

  /**
   * DOCUMENTATION PENDING
   */
  private void removeItemRefs()
  {
    LOG.debug("removeItems()");

    String xpath = basePath + "/" + QTIConstantStrings.ITEMREF;
    this.removeElement(xpath);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public List getSectionRefs()
  {
    LOG.debug("getSectionRefs()");
    String xpath = basePath + "/" + QTIConstantStrings.SECTIONREF;

    return this.selectNodes(xpath);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public List getSectionRefIds()
  {
    List refs = this.getSectionRefs();
    List ids = new ArrayList();
    int size = refs.size();
    for (int i = 0; i < size; i++)
    {
      Element ref = (ElementImpl) refs.get(0);
      String idString = ref.getAttribute(QTIConstantStrings.LINKREFID);
      ids.add(idString);
    }

    return ids;
  }
}
