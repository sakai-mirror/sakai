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

package org.navigoproject.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utility class.  Maps  XML elements and attribute under a given node to a Map,
 * or populates bean.
 *
 * @author @author Ed Smiley
 * @version $Id: XMLMapper.java,v 1.1.1.1 2004/07/28 21:32:08 rgollub.stanford.edu Exp $
 */
public class XMLMapper
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(XMLMapper.class);

  public static final String ATTRIBUTE_PREFIX = "attribute_";

  /**
   * Maps each element node and attribute under a given node to a Map.
   * It associates each element's text value with its name, and
   * each attribute value with a key of "attribute_" + the attribute name.
   *
   * If node is a document it processes it as if it were the root node.
   *
   * If there are multiple nodes with the same element name they will be stored
   * in a List.
   *
   * NOTE:
   * This is DESIGNED to ignore elements at more depth so that simple
   * String key value pairs are used.  This WILL NOT recurse to more depth,
   * by design.  If it did so, you would have to use maps of maps.
   *
   * @param node Node
   * @param indent String
   * @return HashMap
   */
  static public Map map(Document doc)
  {
    return hashNode(doc);
  }

  /**
   * Maps each element node to a bean property.
   * Supports only simple types such as String, int and long, plus Lists.
   *
   * If node is a document it processes it as if it were the root node.
   *
   * If there are multiple nodes with the same element name they will be stored
   * in a List.
   *
   * NOTE:
   * This is DESIGNED to ignore elements at more depth so that simple
   * String key value pairs are used.  This WILL NOT recurse to more depth,
   * by design.  If it did so, you would have to use maps of maps.
   *
   * @param bean Serializable object which has the appropriate setters/getters
   * @param doc the document
   */
  static public void populateBeanFromDoc(Serializable bean, Document doc)
  {
    try
    {
      Map m = map(doc);
      BeanUtils.populate(bean, m);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /**
   * utility class, hides the implementation as a HashMap
   * @param node
   * @return HashMap
   */
  private static HashMap hashNode(Node node)
  {
    HashMap hNode = new HashMap();

    int nType = node.getNodeType();
    NodeList nodes = node.getChildNodes();
    NamedNodeMap attributes = node.getAttributes();
    String name = node.getNodeName();

    // node is a document, recurse
    if(nType == Node.DOCUMENT_NODE)
    {
      // find root node
      if(nodes != null)
      {
        for(int i = 0; i < nodes.getLength(); i++)
        {
          // find  and process root node
          Node rnode = nodes.item(i);
          if(rnode.getNodeType() == Node.ELEMENT_NODE)
          {
            hNode = hashNode(rnode);

            break;
          }
        }
      }
    }

    //end if Node.DOCUMENT_NODE
    if(nType == Node.ELEMENT_NODE)
    {
      // add in child elements
      if(nodes != null)
      {
        for(int j = 0; j < nodes.getLength(); j++)
        {
          Node cnode = nodes.item(j);
          if(cnode.getNodeType() == Node.ELEMENT_NODE)
          {
            String cname = cnode.getNodeName();
            String ctext = textValue(cnode);
            String ctype = getTypeAttribute(cnode);
//            LOG.debug(cname + "=" + ctype);

            // if we have multiple identical entries store them in a List
            if("list".equals(ctype))
            {
              ArrayList list;
              // if this element name already has a list
              if(hNode.get(cname) instanceof ArrayList)
              {
                list = (ArrayList) hNode.get(cname);
              }
              else // put it in a new list
              {
                list = new ArrayList();
              }

              list.add(ctext);
              hNode.put(cname, list);
            }
            else // scalar (default)
            {
              hNode.put(cname, ctext);
            }
          }
        }
      }

      // add in attributes
      if(attributes != null)
      {
        for(int i = 0; i < attributes.getLength(); i++)
        {
          Node current = attributes.item(i);
          hNode.put(
            ATTRIBUTE_PREFIX + current.getNodeName(), current.getNodeValue());
        }
      }
    }

    return hNode;
  }

  /**
   * utility method
   *
   * @param nd node
   *
   * @return text value of node
   */
  private static String textValue(Node nd)
  {
    String text = "";
    NodeList nodes = nd.getChildNodes();
    for(int i = 0; i < nodes.getLength(); i++)
    {
      Node cnode = nodes.item(i);
      if(cnode.getNodeType() == Node.TEXT_NODE)
      {
        text += cnode.getNodeValue();
      }
    }

    return text;
  }

  /**
   * If there is a type attribute for the element node, return its value,
   * otherwise return "scalar".
   * @param node
   * @return
   */
  private static String getTypeAttribute(Node node){
    NamedNodeMap attributes = node.getAttributes();
    if(attributes != null)
    {
      for(int i = 0; i < attributes.getLength(); i++)
      {
        Node current = attributes.item(i);
        if ("type".equals(current.getNodeName())){
          return current.getNodeValue();
        }
      }
    }

    return "scalar";
  }

}


