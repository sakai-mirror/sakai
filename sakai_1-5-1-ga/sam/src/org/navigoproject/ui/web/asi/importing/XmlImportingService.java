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

package org.navigoproject.ui.web.asi.importing;

import org.navigoproject.QTIConstantStrings;
import org.navigoproject.business.entity.Assessment;
import org.navigoproject.business.entity.XmlStringBuffer;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import org.xml.sax.SAXException;
import osid.dr.DigitalRepositoryException;
import osid.shared.SharedException;

/**
 * DOCUMENTATION PENDING
 *
 * @author $author$
 * @version $Id: XmlImportingService.java,v 1.1.1.1 2004/07/28 21:32:07 rgollub.stanford.edu Exp $
 */
public class XmlImportingService
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(XmlImportingService.class);

  /**
   * Creates a new XmlImportingService object.
   */
  public XmlImportingService()
  {
  }

  /**
   * DOCUMENTATION PENDING
 * @param xmlString DOCUMENTATION PENDING
 * @param title TODO
   *
   * @throws DOMException DOCUMENTATION PENDING
   * @throws SharedException DOCUMENTATION PENDING
   * @throws DigitalRepositoryException DOCUMENTATION PENDING
   * @throws ParserConfigurationException DOCUMENTATION PENDING
   * @throws SAXException DOCUMENTATION PENDING
   * @throws IOException DOCUMENTATION PENDING
   */
  public static String importXmlFile(String xmlString, String title)
    throws DOMException, SharedException, DigitalRepositoryException, 
      ParserConfigurationException, SAXException, IOException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("importXmlFile(String " + xmlString + ")");
    }

    Assessment assessment = new Assessment(xmlString);
    
    // set the title if provided
    if (!title.equals(""))
    {
    	((Element) assessment.getDocument().getElementsByTagName(QTIConstantStrings.ASSESSMENT).item(0)).setAttribute(QTIConstantStrings.TITLE, title);
    }
    
    Assessment decomposed = assessment.decompose();
    decomposed.save();
    return assessment.getIdent();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param xsb DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private static String getAssessmentTitle(XmlStringBuffer xsb)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getAssessmentTitle(XmlStringBuffer" + xsb + ")");
    }

    String title = "";
    Element root;
    try
    {
      root = xsb.getDocument().getDocumentElement();
      title =
        ((Element) root.getElementsByTagName(QTIConstantStrings.ASSESSMENT)
                       .item(0)).getAttribute(QTIConstantStrings.TITLE);
    }
    catch(SAXException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(ParserConfigurationException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(IOException e)
    {
      LOG.error(e.getMessage(), e);
    }

    return title;
  }
}
