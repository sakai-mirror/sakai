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

/*
 * Created on Mar 8, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.delivery;

import org.navigoproject.QTIConstantStrings;
import org.navigoproject.business.entity.XmlStringBuffer;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author casong
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ItemSectionPathExtractor
{
  private static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(ItemSectionPathExtractor.class);
  public String ASSESSMENT_PATH =
    "navigo_taken_assessment/questestinterop/assessment";
  private boolean restartNumbering;
  private int continueNumber = 0;
  
  public ItemSectionPathExtractor(String numbering)
  {
    if("Restart".equalsIgnoreCase(numbering)){
      this.restartNumbering = true;
    }
    restartNumbering = false;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param document DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ArrayList extractDocumentItemXpath(Document document)
  {
    ArrayList list = null;
    try
    {
      XmlStringBuffer xsb = new XmlStringBuffer(document);
      List assessments = xsb.selectNodes(XmlDeliveryService.QUESTTESTINTEROP_ASSESSMENT);

      if(assessments.iterator().hasNext())
      {
        Element assessment = (Element) assessments.iterator().next();
        list = extractAssessmentItemXpath(assessment);
      }
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
    }

    return list;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessment DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ArrayList extractAssessmentItemXpath(Element assessment)
  {
    ArrayList list = new ArrayList();
    String rootPath = XmlDeliveryService.QUESTTESTINTEROP_ASSESSMENT;
    NodeList nodes = assessment.getChildNodes();
    int size = nodes.getLength();
    int restart = 0;
    for(int i = 0; i < size; i++)
    {
      Node node = nodes.item(i);
      if(QTIConstantStrings.SECTION.equals(node.getNodeName()))
      {
        restart++;
        Element section = (Element) node;
        String ident = section.getAttribute(QTIConstantStrings.IDENT);
        String xpath =
          rootPath + "/" + QTIConstantStrings.SECTION + "[@ident=\"" + ident +
          "\"]";
        //
        String displayIndex = getDisplayIndex("", restart);
        //
        list.addAll(extractSectionItemXpath(xpath, section, displayIndex));
      }
    }

    return list;
  }

  /**
   * @param string
   * @param i
   * @return
   */
  private String getDisplayIndex(String lastDisplay, int i)
  {
    if(restartNumbering){
      return lastDisplay + String.valueOf(i) + ".";  
    }
    return String.valueOf(continueNumber);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param rootPath DOCUMENTATION PENDING
   * @param section DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private ArrayList extractSectionItemXpath(String rootPath, Element section, String lastDisplay)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("extractSectionItems( Element " + section);
    }

    ArrayList list = new ArrayList();
    if(section.getElementsByTagName(QTIConstantStrings.ITEM).getLength() > 0)
    {
      list.addAll(extractItemXPath(rootPath, section, lastDisplay));
    }

    NodeList nodes = section.getChildNodes();
    int size = nodes.getLength();
    int restart = 0;
    for(int i = 0; i < size; i++)
    {
      Node node = nodes.item(i);
      if(QTIConstantStrings.SECTION.equals(node.getNodeName()))
      {
        restart++;
        Element subSection = (Element) node;

        String ident = subSection.getAttribute(QTIConstantStrings.IDENT);
        String xpath =
          rootPath + "/" + QTIConstantStrings.SECTION + "[@ident=\"" + ident +
          "\"]";
          //
          String displayIndex = getDisplayIndex(lastDisplay, restart);
          //
        list.addAll(extractSectionItemXpath(xpath, subSection, displayIndex));
      }
    }

    return list;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param rootPath DOCUMENTATION PENDING
   * @param section DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private ArrayList extractItemXPath(String rootPath, Element section, String lastDisplay)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("extractItems (Element " + section);
    }

    ArrayList list = new ArrayList();
    NodeList nodes = section.getChildNodes();
    int size = nodes.getLength();
    int restart = 0;
    for(int i = 0; i < size; i++)
    {
      Node node = nodes.item(i);
      if(QTIConstantStrings.ITEM.equals(node.getNodeName()))
      {
        restart++;
        continueNumber++;
        Element item = (Element) node;
        String xpath =
          rootPath + "/item[@ident=\"" +
          item.getAttribute(QTIConstantStrings.IDENT) + "\"]";
        //
        String displayIndex = getDisplayIndex(lastDisplay, restart);
        //
        item.setAttribute("displayIndex", displayIndex);
        item.setAttribute("continueNumber", String.valueOf(this.continueNumber));
        list.add(xpath);
      }
    }

    return list;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessment DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private ArrayList extractAssessmentItems(Element assessment)
  {
    ArrayList list = new ArrayList();
    NodeList nodes = assessment.getChildNodes();
    int size = nodes.getLength();
    for(int i = 0; i < size; i++)
    {
      Node node = nodes.item(i);
      if(QTIConstantStrings.SECTION.equals(node.getNodeName()))
      {
        Element section = (Element) nodes.item(i);
        list.addAll(extractSectionItems(section));
      }
    }

    return list;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param section DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private ArrayList extractSectionItems(Element section)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("extractSectionItems( Element " + section);
    }

    ArrayList list = new ArrayList();
    if(section.getElementsByTagName(QTIConstantStrings.ITEM).getLength() > 0)
    {
      list.addAll(extractItems(section));
    }

    NodeList nodes = section.getChildNodes();
    int size = nodes.getLength();
    for(int i = 0; i < size; i++)
    {
      Node node = nodes.item(i);
      if(QTIConstantStrings.SECTION.equals(node.getNodeName()))
      {
        Element subSection = (Element) nodes.item(i);
        list.addAll(extractSectionItems(subSection));
      }
    }

    return list;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param section DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private ArrayList extractItems(Element section)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("extractItems (Element " + section);
    }

    ArrayList list = new ArrayList();
    NodeList nodes = section.getChildNodes();
    int size = nodes.getLength();
    for(int i = 0; i < size; i++)
    {
      Node node = nodes.item(i);
      if(QTIConstantStrings.ITEM.equals(node.getNodeName()))
      {
        Element item = (Element) nodes.item(i);
        list.add(item);
      }
    }

    return list;
  }

  /**
   * @param assessmentTaken
   * @return
   */
  public ArrayList extractDocumentSectionXpath(Document document)
  {
    ArrayList list = null;
    try
    {
      XmlStringBuffer xsb = new XmlStringBuffer(document);
      List assessments = xsb.selectNodes(XmlDeliveryService.QUESTTESTINTEROP_ASSESSMENT);

      if(assessments.iterator().hasNext())
      {
        Element assessment = (Element) assessments.iterator().next();
        list = extractAssessmentSectionXpath(assessment);
      }
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
    }

    return list;
  }

  /**
   * @param assessment
   * @return
   */
  private ArrayList extractAssessmentSectionXpath(Element assessment)
  {
    ArrayList list = new ArrayList();
    String rootPath = XmlDeliveryService.QUESTTESTINTEROP_ASSESSMENT;
    NodeList nodes = assessment.getChildNodes();
    int size = nodes.getLength();
    for(int i = 0; i < size; i++)
    {
      Node node = nodes.item(i);
      if(QTIConstantStrings.SECTION.equals(node.getNodeName()))
      {
        Element section = (Element) node;
        String ident = section.getAttribute(QTIConstantStrings.IDENT);
        String xpath =
          rootPath + "/" + QTIConstantStrings.SECTION + "[@ident=\"" + ident +
          "\"]";
        list.add(xpath);
      }
    }

    return list;
  }
}
