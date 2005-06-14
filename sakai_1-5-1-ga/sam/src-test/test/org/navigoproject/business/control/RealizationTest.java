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
 * Created on Oct 20, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package test.org.navigoproject.business.control;

import org.navigoproject.business.control.RealizationManager;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringWriter;

import junit.framework.TestCase;
import junit.framework.TestResult;

import test.org.navigoproject.fixture.Log4jFixture;
import test.org.navigoproject.fixture.PathInfoFixture;

/**
 * @author casong
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RealizationTest
  extends TestCase
{
  private final static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(RealizationTest.class);
  private Log4jFixture log4jFixture;
  private PathInfoFixture pathInfoFixture;

  /**
   * Creates a new RealizationTest object.
   *
   * @param name DOCUMENTATION PENDING
   */
  public RealizationTest(String name)
  {
    super(name);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  protected void setUp()
    throws Exception
  {
    super.setUp();
    log4jFixture = new Log4jFixture(this);
    pathInfoFixture = new PathInfoFixture(this);

    log4jFixture.setUp();
    pathInfoFixture.setUp();

    /** @todo remove this configurator: */
    org.apache.log4j.BasicConfigurator.configure();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  protected void tearDown()
    throws Exception
  {
    log4jFixture.tearDown();
    pathInfoFixture.tearDown();

    log4jFixture = null;
    pathInfoFixture = null;
    super.tearDown();
  }

  /* (non-Javadoc)
   * @see junit.framework.TestCase#run()
   */
  public TestResult run()
  {
    // TODO Auto-generated method stub
    return super.run();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testRealizeAssessment()
    throws Exception
  {
    String file = "c:/realizationInput.xml";
    LOG.debug(file);
    FileReader reader = new FileReader(file);
    StringWriter out = new StringWriter();
    int c;
    while((c = reader.read()) != -1)
    {
      out.write(c);
    }

    reader.close();
    String data = out.toString();
    out.close();
    RealizationManager realization = new RealizationManager();
    realization.realizeAssessment(data);
    String outputfile = "c:/realizationTest.xml";
    FileWriter fw = new FileWriter(outputfile, false);
    fw.write(realization.getData());
    fw.flush();
    fw.close();
  }

  //  public void testCompose() throws Exception
  //  {
  //    String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
  //    data +="<questestinterop><assessment ident=\"bc2c63adc0a800960101549541458b6c\" title=\"Delete\">";
  //    data +="<sectionref ident=\"bc2c77c8c0a80096008b1a8f7cfcebbc\"/>";
  //    data += "</assessment> </questestinterop>";
  //    Realization r = new Realization();
  //    MockHttpServletRequest request = new MockHttpServletRequest(null);
  //    String result = r.compose(data, request);
  //    LOG.debug(result);
  //  }
}
