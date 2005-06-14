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

package test.org.navigoproject.data;

import org.navigoproject.QTIConstantStrings;
import org.navigoproject.business.entity.XmlStringBuffer;
import org.navigoproject.data.RepositoryManager;

import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.ElementImpl;

import org.w3c.dom.Element;

import osid.dr.Asset;
import osid.dr.DigitalRepositoryException;

import osid.shared.Id;

import test.org.navigoproject.fixture.BootStrapFixture;
import test.org.navigoproject.osid.MockHttpServletRequest;

/**
 * DOCUMENT ME!
 *
 * @author <a href="mailto:rpembry@indiana.edu">Randall P. Embry</a>
 * @version $Id: RepositoryManagerTest.java,v 1.1.1.1 2004/07/28 21:32:08 rgollub.stanford.edu Exp $
 */
public class RepositoryManagerTest
  extends TestCase
{
  private final static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(RepositoryManagerTest.class);
  private Id sampleItemId;
  private Id sampleAssessmentId;
  private Id bogusId;
  private final String SAMPLE_ASSET_ID = "c7a137d98644128600b33d0abcbad9a7";
  private RepositoryManager repositoryManager;

  static
  {
    BasicConfigurator.configure();
    new BootStrapFixture();
  }

  /**
   * Constructor for RepositoryManagerTest.
   *
   * @param arg0
   */
  public RepositoryManagerTest(String arg0)
  {
    super(arg0);
  }

  /*
   * @see TestCase#setUp()
   */
  protected void setUp()
    throws Exception
  {
    LOG.debug("setup()");
    super.setUp();

    if(this.repositoryManager == null)
    {
      this.repositoryManager =
        new RepositoryManager(new MockHttpServletRequest(null));
    }

    try
    {
      sampleItemId = repositoryManager.getId(SAMPLE_ASSET_ID);
      sampleAssessmentId =
        this.repositoryManager.getSharedManager().getId(
          "JUnitSampleAssessment");
      sampleAssessmentId =
        this.repositoryManager.getSharedManager().getId("JUnitSampleItem");
      bogusId = this.repositoryManager.getSharedManager().getId("JUnitBogusId");
    }
    catch(DigitalRepositoryException e)
    {
      LOG.debug("Error getting Id");
      LOG.error(e); throw new Error(e);
    }
  }

  /*
   * Test for Id store(Serializable, String, String)
   */
  public void testStore()
    throws Exception
  {
    XmlStringBuffer obj = new XmlStringBuffer("<test>xyz</test>");
    Asset asset = repositoryManager.store(obj.stringValue(), "test", "test");
    LOG.debug("Here is the Id: " + asset.getId());
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testCreate()
    throws Exception
  {
    XmlStringBuffer obj = new XmlStringBuffer("<test>xyz</test>");
    Asset asset = repositoryManager.createAsset(obj.getClass(), "test", "test");
    LOG.debug("Here is the Id of the newly created Asset: " + asset.getId());
    LOG.error(
      "Here is the Id of the newly created Asset: " + asset.getId());
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testUpdate()
    throws Exception
  {
    XmlStringBuffer obj = new XmlStringBuffer("<test2>abc</test2>");
    repositoryManager.update(sampleItemId, obj.stringValue());
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testRetrieve()
    throws Exception
  {
    XmlStringBuffer xml =
      new XmlStringBuffer((String) repositoryManager.retrieve(sampleItemId));
    LOG.debug("Here is the retrieved Asset: " + xml);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testSetItemResult()
    throws Exception
  {
    DocumentImpl document = new DocumentImpl();
    String elementValue = "this is a test";
    Id anotherItemId = repositoryManager.getSharedManager().createId();

    Element element = new ElementImpl(document, QTIConstantStrings.ITEM_RESULT);
    element.setAttribute(
      QTIConstantStrings.IDENT_REF, sampleItemId.getIdString());

    element.setNodeValue(elementValue);

    repositoryManager.setItemResult(
      this.sampleAssessmentId, anotherItemId, element);
    repositoryManager.setItemResult(
      this.sampleAssessmentId, this.sampleItemId, element);

    // set it a second time, to verify it is updated instead of inserted:
    repositoryManager.setItemResult(
      this.sampleAssessmentId, this.sampleItemId, element);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testGetItemResult()
    throws Exception
  {
    Element element =
      repositoryManager.getItemResult(
        this.sampleAssessmentId, this.sampleItemId);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testGetEmptyItemResults()
    throws Exception
  {
    List elements = repositoryManager.getItemResults(this.bogusId);

    assertEquals(elements.size(), 0);
    elements = repositoryManager.getItemResults(this.sampleItemId);
    assertEquals(elements.size(), 0);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testGetItemResults()
    throws Exception
  {
    List elements = repositoryManager.getItemResults(this.sampleAssessmentId);

    LOG.debug("Elements retrieved: " + elements.size());
  }
}
