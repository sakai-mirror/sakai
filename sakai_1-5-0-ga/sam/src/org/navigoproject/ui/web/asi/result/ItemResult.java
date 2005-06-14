/*
 * Created on Mar 1, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
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
 * Created on Aug 22, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.result;

import org.navigoproject.QTIConstantStrings;
import org.navigoproject.data.RepositoryManager;
import org.navigoproject.ui.web.asi.delivery.ItemResponseIdentMap;
import org.navigoproject.util.XmlUtil;

import java.io.IOException;

import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.dom.TextImpl;

import org.jaxen.JaxenException;
import org.jaxen.XPath;

import org.jaxen.dom.DOMXPath;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import osid.shared.Id;

/**
 * DOCUMENT ME!
 *
 * @author casong To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public class ItemResult
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(ItemResultWriter.class);
  private Document itemResultDoc;
  private Element item; 
  private Element itemResult;
  private List itemReponseValues;
  private String assessmentId;
  private String itemIdent;
  private boolean emptyResponseValue = true;

//  /**
//   * Creates a new ItemResultWriter object.
//   *
//   * @param assessmentId DOCUMENTATION PENDING
//   */
//  public ItemResult(String assessmentId, String itemId)
//  {
//    this.assessmentId = assessmentId;
//    this.itemId = itemId;
//    this.itemResultDoc = XmlUtil.createDocument()();
//  }
  
  public ItemResult(String assessmentId, String itemIdent, Element item, Element itemResult,
  List itemResponseValues)
  {
    this.assessmentId = assessmentId;
    this.itemIdent = itemIdent;
    this.itemResultDoc = XmlUtil.createDocument();
    this.item = item;
    this.setItemResult(itemResult);
    this.itemReponseValues = itemResponseValues;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Element getItemResult()
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getItemResult()");
    }

    return this.itemResult;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean isEmptyResponseValue()
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("isEmptyResponseValemptyResponseValueue()");
    }

    return emptyResponseValue;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param itemResult DOCUMENTATION PENDING
   */
  public void setItemResult(Element itemResult)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("setItemResult(Element " + itemResult + ")");
    }

    this.itemResult = itemResult;
    if(itemResult != null)
    {
      clearAllResponseValues();
    }
  }

  /**
   * DOCUMENTATION PENDING
   */
  private void clearAllResponseValues()
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("clearAllResponseValues()");
    }

    NodeList responses =
      itemResult.getElementsByTagName(QTIConstantStrings.RESPONSE);
    int size = responses.getLength();
    for(int i = 0; i < size; i++)
    {
      Element response = (Element) responses.item(i);
      NodeList responseValues =
        response.getElementsByTagName(QTIConstantStrings.RESPONSE_VALUE);
      while(responseValues.getLength() > 0)
      {
        response.removeChild(responseValues.item(0));
      }
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   */
  public void updateItemResult(HttpServletRequest request)
  {

    createItemResult(request);
    increaseNumAttempts();
    NodeList responseGroups = this.item.getElementsByTagName("response_grp");
    if(responseGroups.getLength() > 0)
    {
      processGRP(request);
    }
    saveItemResult();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   */
  private void createItemResult(HttpServletRequest request)
  {
    
    if(this.itemResult == null)
    {
      this.itemResult =
        this.itemResultDoc.createElement(QTIConstantStrings.ITEM_RESULT);
      this.itemResult.setAttribute(QTIConstantStrings.IDENT_REF, itemIdent);
    }
    for(int i=0; i<itemReponseValues.size(); i++)
    {
        String name = (String)this.itemReponseValues.get(i);
        String value = request.getParameter(name);
          ItemResponseIdentMap irimap = new ItemResponseIdentMap(name);
          String itemIdentRef = irimap.getItemResultIdentRef();
          String responseIdentRef = irimap.getResponseIdentRef();

          // check if response with given ident ref exist, create new one if not.
          Element response = getResponseElement(responseIdentRef);
          Element responseValue =
            this.itemResultDoc.createElement(QTIConstantStrings.RESPONSE_VALUE);
          Text responseValueText =
            this.itemResultDoc.createTextNode(value);
          responseValueText.setNodeValue(value);
          responseValue.appendChild(responseValueText);

          Element newResponseValue =
            (Element) response.getOwnerDocument().importNode(
              responseValue, true);
          response.appendChild(newResponseValue);
          if((value != null) && ! "".equals(value.trim()))
          {
            this.emptyResponseValue = false;
          }
      }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param responseIdentRef DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private Element getResponseElement(String responseIdentRef)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getResponseElement(String " + responseIdentRef + ")");
    }

    NodeList responses =
      itemResult.getElementsByTagName(QTIConstantStrings.RESPONSE);
    int size = responses.getLength();
    for(int i = 0; i < size; i++)
    {
      Element response = (Element) responses.item(i);
      if(
        responseIdentRef.equals(
            response.getAttribute(QTIConstantStrings.IDENT_REF)))
      {
        return response;
      }
    }

    Element returnValue =
      this.itemResultDoc.createElement(QTIConstantStrings.RESPONSE);
    returnValue.setAttribute(QTIConstantStrings.IDENT_REF, responseIdentRef);
    Element numAttempts =
      this.itemResultDoc.createElement(QTIConstantStrings.NUM_ATTEMPTS);
    Text numAttemptsValue =
      this.itemResultDoc.createTextNode("0");
    numAttempts.appendChild(numAttemptsValue);
    returnValue.appendChild(numAttempts);

    //    Node new itemResult.appendChild(returnValue);
    Node newNode = itemResult.getOwnerDocument().importNode(returnValue, true);
    itemResult.appendChild(newNode);
    responses = itemResult.getElementsByTagName(QTIConstantStrings.RESPONSE);
    size = responses.getLength();
    for(int i = 0; i < size; i++)
    {
      Element response = (Element) responses.item(i);
      if(
        responseIdentRef.equals(
            response.getAttribute(QTIConstantStrings.IDENT_REF)))
      {
        return response;
      }
    }

    return null;
  }

  /**
   * DOCUMENTATION PENDING
   */
  private void increaseNumAttempts()
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("increaseNumAttempts()");
    }

    NodeList responses =
      itemResult.getElementsByTagName(QTIConstantStrings.RESPONSE);
    int size = responses.getLength();
    for(int i = 0; i < size; i++)
    {
      Element response = (Element) responses.item(i);
      NodeList nodes = response.getChildNodes();
      int length = nodes.getLength();
      for(int j = 0; j < length; j++)
      {
        Node node = nodes.item(j);
        if(
          (node != null) &&
            QTIConstantStrings.NUM_ATTEMPTS.equals(node.getNodeName()))
        {
          Element numAttempts = (Element) node;
          NodeList numAttemptValueNodes = node.getChildNodes();
          if(nodes.getLength() > 0)
          {
            TextImpl numAttemptsValue = (TextImpl) numAttemptValueNodes.item(0);
            String numString = numAttemptsValue.getNodeValue();
            int nums = Integer.parseInt(numString);
            numAttemptsValue.setNodeValue(String.valueOf(nums + 1));
          }
        }
      }
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   */
  private void processGRP(HttpServletRequest request)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("processGRP(HttpServletRequest " + request + ")");
    }

    Element response = null;
    try
    {
      for(int i=0; i<itemReponseValues.size(); i++)
      {
        String name = (String) this.itemReponseValues.get(i);
        String value = request.getParameter(name);
        ItemResponseIdentMap irimap = new ItemResponseIdentMap(name);
        String itemIdentRef = irimap.getItemResultIdentRef();
        String responseIdentRef = irimap.getResponseIdentRef();
        if(true){ 
          //TODO need to modify this when multiple response_grp or response_str in each item.
          String position = irimap.getResponseValue();
          String responsePath = "response[@ident_ref='" + responseIdentRef +
            "']";
          String responseValuePath =
            responsePath + "/response_value[" + position + "]";
          Element responseValue = getResponseValueNode(responseValuePath);
          TextImpl responseValueText = (TextImpl) responseValue.getFirstChild();
          responseValueText.setNodeValue(value);
        }
      }
    }
    catch(Exception ex)
    {
      LOG.error(ex.getMessage(), ex);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param responseValuePath DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws JaxenException DOCUMENTATION PENDING
   * @throws IOException DOCUMENTATION PENDING
   */
  private Element getResponseValueNode(String responseValuePath)
    throws JaxenException, IOException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getResponseValueNode(String " + responseValuePath + ")");
    }

    Element responseValue = null;

    XPath xpath = new DOMXPath(responseValuePath);
    List responseValues = xpath.selectNodes(itemResult);
    if(responseValues.size() > 0)
    {
      responseValue = (Element) responseValues.get(0);
    }

    return responseValue;
  }

  /**
   * DOCUMENTATION PENDING
   */
  public void saveItemResult()
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("saveItemResult()");
    }

    String itemIdent =
      this.itemResult.getAttribute(QTIConstantStrings.IDENT_REF);
    try
    {
      RepositoryManager rm = new RepositoryManager();
      Id aid = rm.getId(assessmentId);
      Id itemId = rm.getId(itemIdent);
      rm.setItemResult(aid, itemId, itemResult);
    }
    catch(Exception ex)
    {
      LOG.error(ex.getMessage(), ex);
    }
  }

}

