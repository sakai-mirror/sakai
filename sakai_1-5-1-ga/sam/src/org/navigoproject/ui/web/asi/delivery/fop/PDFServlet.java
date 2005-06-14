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
 * Created on Oct 9, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.delivery.fop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.Driver;

import org.xml.sax.InputSource;

/**
 * DOCUMENT ME!
 *
 * @author casong To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public class PDFServlet
  extends HttpServlet
{
  private final static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(PDFServlet.class);
  private final String downloadType = "attachment";
  private final String contentType = "application/pdf";

  /**
   * Constructor of the object.
   */
  public PDFServlet()
  {
    super();
  }

  /**
   * Destruction of the servlet. <br>
   */
  public void destroy()
  {
    super.destroy(); // Just puts "destroy" string in log

    // Put your code here
  }

  /**
   * The doGet method of the servlet. <br>
   * This method is called when a form has its tag value method equals to get.
   *
   * @param request the request send by the client to the server
   * @param response the response send by the server to the client
   *
   * @throws ServletException if an error occurred
   * @throws IOException if an error occurred
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "doGet(HttpServletRequest " + request + ", HttpServletResponse" +
        response + ")");
    }

    File mergedXmlFile = null;

    //Construct driver
    Driver driver = new Driver();

    //Setup Renderer (output format)        
    driver.setRenderer(Driver.RENDER_PDF);

    //Setup output
    ServletOutputStream out = response.getOutputStream();
    String filename = getPdfFilename();
    response.setContentType(contentType);
    response.setHeader(
      "Content-disposition", downloadType + "; filename=" + filename);
    try
    {
      driver.setOutputStream(out);

      //Set up xml
      mergedXmlFile = mergeXmlFile();

      //Setup XSLT
      InputStream xslt =
        this.getServletContext().getResourceAsStream("/xml/xsl/fop/asifop.xsl");
      TransformerFactory factory = TransformerFactory.newInstance();
      Transformer transformer = factory.newTransformer(new StreamSource(xslt));

      //Setup input for XSLT transformation
      FileInputStream inputStream = new FileInputStream(mergedXmlFile);
      InputSource inputSource = new InputSource(inputStream);
      Source src = new SAXSource(inputSource);

      //Resulting SAX events (the generated FO) must be piped through to FOP
      Result res = new SAXResult(driver.getContentHandler());

      //Start XSLT transformation and FOP processing
      transformer.transform(src, res);
    }
    catch(TransformerException e)
    {
      LOG.error(e.getMessage(), e);
    }

    finally
    {
      out.close();
      if(mergedXmlFile != null)
      {
        mergedXmlFile.delete();
      }
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private File mergeXmlFile()
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("mergeXmlFile()");
    }

    //generate a unique file name
    String filename = "/tmp/mergedXmlFile.xml";
    File mergedXmlFile = new File(filename);

    // merge multiple xml file into a single file. 
    XmlProcessor xmlp = new XmlProcessor();
    xmlp.combineXml(mergedXmlFile);

    return mergedXmlFile;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private String getPdfFilename()
  {
    return "ResultASIPDF.pdf";
  }

  /**
   * The doPost method of the servlet. <br>
   * This method is called when a form has its tag value method equals to
   * post.
   *
   * @param request the request send by the client to the server
   * @param response the response send by the server to the client
   *
   * @throws ServletException if an error occurred
   * @throws IOException if an error occurred
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    this.doGet(request, response);
  }

  /**
   * Initilaisation of the servlet. <br>
   *
   * @throws ServletException if an error occure
   */
  public void init()
    throws ServletException
  {
    // Put your code here
  }
}
