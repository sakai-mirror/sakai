/*
 * Created on May 21, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.business.entity;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.navigoproject.QTIConstantStrings;
import org.navigoproject.data.RepositoryManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author casong
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AttemptRealzedAssessment extends Assessment
{
  private static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(AttemptRealzedAssessment.class);
    
  protected AttemptRealzedAssessment()
  {
    super();
  }

  /**
   * Creates a new Assessment object.
   *
   * @param document DOCUMENTATION PENDING
   */
  public AttemptRealzedAssessment(Document document)
  {
    super(document);
  }

  /**
   * Creates a new Assessment object.
   *
   * @param xml DOCUMENTATION PENDING
   */
  public AttemptRealzedAssessment(String xml)
  {
    super(xml);
  }
     
  public Map getPoolNumberMap(Calendar date) throws ParserConfigurationException, SAXException, IOException
  {
    this.attemptRealize(date);
    return this.getSelectionElement();
  }
  
  public void attemptRealize(Calendar date) throws ParserConfigurationException, SAXException, IOException
  {
    NodeList childNodes = this.getDocument().getChildNodes();
    int size = childNodes.getLength();
    for(int i = 0; i < size; i++)
    {
      Node childNode = childNodes.item(i);
      String nodeName = childNode.getNodeName();
      if(QTIConstantStrings.ASSESSMENT.equals(nodeName))
      {
        recomposeSection(childNode, false, date);
      }
      else
      {
        recomposeAssessment(childNode, false, date);
      }
    }
  }
  
  /**
   * DOCUMENTATION PENDING
   *
   * @param assessment DOCUMENTATION PENDING
   */
  public void recomposeSection(Node assessment, boolean dealWithFormat, Calendar date)
  {
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
    }
  }
  
  /**
   * DOCUMENTATION PENDING
   *
   * @param sectionRef DOCUMENTATION PENDING
   */
  protected void replaceSectionRefNode(Node sectionRef, boolean dealWithFormat, Calendar date)
  {
    try
    {
      String sectionId =
        ((Element) sectionRef).getAttribute(QTIConstantStrings.LINKREFID);
      RepositoryManager rm = new RepositoryManager();
      Section section = new Section((String) rm.retrieve(rm.getId(sectionId), date));
      Node sectionNode = section.getDocument().getFirstChild();
      Node newNode = this.getDocument().importNode(sectionNode, true);
      sectionRef.getParentNode().replaceChild(newNode, sectionRef);
    }
    catch(Exception ex)
    {
      LOG.error("Unable to replace sectionRefNode: " + ex.getMessage(), ex);
    }
  }
  
  private Map getSelectionElement()
  {
    String xpath = basePath + "/section/" + QTIConstantStrings.SELECTION_ORDERING +
      "/";
    String selectionXPath = xpath + QTIConstantStrings.SELECTION;
    String orderingXPath = xpath + QTIConstantStrings.ORDER;

    List selectNodes = this.selectNodes(selectionXPath);
    List orderNodes = this.selectNodes(orderingXPath);
    Map map = new HashMap();

    int selectNodeSize = selectNodes.size();
    for(int i = 0; i < selectNodeSize; i++)
    {
      Element selectElement = (Element) selectNodes.get(i);
      String selectNumber = null;
      String sourceBankId = null;
  
      NodeList childNodes = selectElement.getChildNodes();
      if(childNodes != null && childNodes.getLength()>0)
      {
        for(int j=0; j<childNodes.getLength(); j++)
        {
          Node node = childNodes.item(j);
          String nodeName = node.getNodeName();
          if(QTIConstantStrings.SELECTION_NUMBER.equalsIgnoreCase(nodeName))
          {
            selectNumber = node.getNodeValue();
          }
          if(QTIConstantStrings.SOURCEBANK_REF.equalsIgnoreCase(nodeName))
          {
            sourceBankId = node.getNodeValue().trim();
          }
        }
      }
      map.put(sourceBankId, selectNumber);
    }
    return map;
  }
  
//  private Map processSelectElement(Element selectElement)
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
//    return null;
//  }
}
