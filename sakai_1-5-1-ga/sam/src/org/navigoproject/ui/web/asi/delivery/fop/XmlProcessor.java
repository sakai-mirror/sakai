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
 * Created on Oct 8, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.delivery.fop;

import org.navigoproject.QTIConstantStrings;
import org.navigoproject.settings.PathInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.Driver;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * DOCUMENT ME!
 *
 * @author casong To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public class XmlProcessor
{
  private final static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(XmlProcessor.class);

  /**
   * DOCUMENTATION PENDING
   *
   * @param xml DOCUMENTATION PENDING
   * @param xslt DOCUMENTATION PENDING
   * @param pdf DOCUMENTATION PENDING
   */
  public void combine(File xml, File xslt, File pdf)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "combine(File " + xml + ", File " + xslt + ", File " + pdf + ")");
    }

    try
    {
      Object inputSource = null;

      //read xml file to generate a Dom object
      FileInputStream fis = new FileInputStream(xml);
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = dbf.newDocumentBuilder();

      org.xml.sax.InputSource is = new org.xml.sax.InputSource(fis);
      Document document = builder.parse(is);

      //trim and leave only presentation and presentation matrial.
      // merge xml files
      Element assessment = (Element) document.getFirstChild().getFirstChild();
      OutputFormat format = new OutputFormat(document);
      StringWriter writer = new StringWriter();
      XMLSerializer serializer = new XMLSerializer(writer, format);
      serializer.serialize(assessment);

      String assessmentString = writer.toString();

      //create SAXSource from input Stream
      PipedOutputStream pos = new PipedOutputStream();
      StringReader sr = new StringReader(assessmentString);
      int a;

      do
      {
        if((a = sr.read()) != -1)
        {
          pos.write(a);
        }
      }
      while(a != -1);

      PipedInputStream pis = new PipedInputStream(pos);
      InputSource inuptSource = null;
      Source src = new SAXSource(inuptSource);

      //transform
      TransformerFactory factory = TransformerFactory.newInstance();
      Transformer transformer = factory.newTransformer(new StreamSource(xslt));

      //Construct driver
      Driver driver = new Driver();

      //Setup Renderer (output format)        
      driver.setRenderer(Driver.RENDER_PDF);

      //Setup output
      OutputStream out = new java.io.FileOutputStream(pdf);
      driver.setOutputStream(out);

      //Resulting SAX events (the generated FO) must be piped through to FOP
      Result res = new SAXResult(driver.getContentHandler());

      //Start XSLT transformation and FOP processing
      transformer.transform(src, res);
    }
    catch(FileNotFoundException ex)
    {
      LOG.error(ex.getMessage(), ex);
    }
    catch(TransformerException ex)
    {
      LOG.error(ex.getMessage(), ex);
    }
    catch(IOException ex)
    {
      LOG.error(ex.getMessage(), ex);
    }
    catch(ParserConfigurationException ex)
    {
      LOG.error(ex.getMessage(), ex);
    }
    catch(SAXException ex)
    {
      LOG.error(ex.getMessage(), ex);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param mergedXmlFile DOCUMENTATION PENDING
   */
  public void combineXml(File mergedXmlFile)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("combineXml(File " + mergedXmlFile + ")");
    }

    try
    {
      FileWriter fw1 = new FileWriter(mergedXmlFile, false);

      fw1.write("<?xml version=\"1.0\" ?> <questestinterop>");
      fw1.close();

      FileWriter fw = new FileWriter(mergedXmlFile, true);

      for(int i = 0; i < 2; i++)
      {
        fw.write(getAssessmentString());
        fw.flush();
      }

      fw.write("</questestinterop>");
      fw.flush();
      fw.close();
    }
    catch(IOException ex)
    {
      LOG.error(ex.getMessage());
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getAssessmentString()
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getAssessmentString()");
    }

    String assessmentString = "";
    try
    {
      /**
       * TODO this will be replaced to use data from DR
       */
      PathInfo pi = PathInfo.getInstance();
      String filePath = pi.getPathToSettings() + "/asifop.xml";
      if(LOG.isDebugEnabled())
      {
        LOG.debug("getAssessmentString(): filePath=" + filePath);
      }

      File xml = new File(filePath);
      if(LOG.isDebugEnabled())
      {
        LOG.debug("getAssessmentString(): File=" + xml);
      }

      //      File xml = new File("c:/develop/tmp/xml/xml/asifop.xml");
      FileInputStream fis = new FileInputStream(xml);
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = dbf.newDocumentBuilder();

      org.xml.sax.InputSource is = new org.xml.sax.InputSource(fis);
      Document document = builder.parse(is);

      /************************************/

      //trim and leave only presentation and presentation matrial.
      Element assessment =
        (Element) document.getElementsByTagName(QTIConstantStrings.ASSESSMENT)
                          .item(0);
      trim(assessment);

      OutputFormat format = new OutputFormat(document);
      StringWriter writer = new StringWriter();
      XMLSerializer serializer = new XMLSerializer(writer, format);
      serializer.serialize(assessment);

      assessmentString = writer.toString();
      int index = assessmentString.indexOf("<" + QTIConstantStrings.ASSESSMENT);
      assessmentString = assessmentString.substring(index);
    }
    catch(Exception ex)
    {
      LOG.error(ex.getMessage(), ex);
    }

    return assessmentString;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessment DOCUMENTATION PENDING
   */
  public void trim(Element assessment)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("trim(Element " + assessment + ")");
    }

    NodeList items = assessment.getElementsByTagName(QTIConstantStrings.ITEM);
    int size = items.getLength();
    for(int i = 0; i < size; i++)
    {
      Element item = (Element) items.item(i);
      trimItem(item);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param item DOCUMENTATION PENDING
   */
  public void trimItem(Element item)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("trimItem(Element " + item + ")");
    }

    NodeList elements = item.getChildNodes();
    int length = elements.getLength();
    for(int j = 0; j < length; j++)
    {
      Node node = (Node) elements.item(0);
      if(! QTIConstantStrings.PRESENTATON.equals(node.getNodeName()))
      {
        item.removeChild(node);
      }
    }
  }
}
