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


/**
 *
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * @author <a href="mailto:rpembry@indiana.edu">Randall P. Embry</a>
 * @version $Id: AuthorizationSupportTest.java,v 1.1.1.1 2004/07/28 21:32:08 rgollub.stanford.edu Exp $
 */
package test.org.navigoproject.osid;

import org.navigoproject.business.entity.XmlStringBuffer;
import org.navigoproject.data.RepositoryManager;
import org.navigoproject.osid.AuthorizationSupport;
import org.navigoproject.osid.OsidManagerFactory;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Iterator;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;

import osid.authentication.AuthenticationException;

import osid.authorization.Authorization;

import osid.dr.Asset;

import osid.shared.Agent;

import test.org.navigoproject.fixture.Log4jFixture;
import test.org.navigoproject.fixture.PathInfoFixture;

/**
 *
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * @author <a href="mailto:rpembry@indiana.edu">Randall P. Embry</a>
 * @version $Id: AuthorizationSupportTest.java,v 1.1.1.1 2004/07/28 21:32:08 rgollub.stanford.edu Exp $
 */
public class AuthorizationSupportTest
  extends TestCase
{
  private final static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(AuthorizationSupportTest.class);
  AuthorizationSupport as;
  RepositoryManager rm;
  private static final SimpleDateFormat DF = new SimpleDateFormat("MM/dd/yyyy");
  private Log4jFixture log4jFixture;
  private static PathInfoFixture pathInfoFixture;
  private static MockHttpServletRequest mockRequest;
  private static Agent testAgent;

  static
  {
    BasicConfigurator.configure();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void setUp()
    throws Exception
  {
    super.setUp();

    //log4jFixture = new Log4jFixture(this);
    //log4jFixture.setUp();
  }

  /**
   * Constructor for AuthorizationSupportTest.
   * @param arg0
   */
  public AuthorizationSupportTest(String arg0)
  {
    super(arg0);

    pathInfoFixture = new PathInfoFixture(this);
    pathInfoFixture.setUp();

    mockRequest = new MockHttpServletRequest(null);
    as = new AuthorizationSupport(mockRequest);
    rm = new RepositoryManager(null);

    try
    {
      testAgent = OsidManagerFactory.getAgent();
    }
    catch(Exception e)
    {
      throw new Error(e.getMessage());
    }
  }

  /**
   * DOCUMENTATION PENDING
   */
  public void testGetAgent()
  {
  }

  /**
   * DOCUMENTATION PENDING
   */
  public void testGetUserId()
  {
  }

  /**
   * DOCUMENTATION PENDING
   */
  public void testGetQualifier()
  {
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testAuthorizeAssessment()
    throws Exception
  {
    Calendar startTime = Calendar.getInstance();
    startTime.setTime(DF.parse("01/01/1995"));
    Calendar stopTime = Calendar.getInstance();
    stopTime.setTime(DF.parse("01/01/2015"));

    XmlStringBuffer test = new XmlStringBuffer("<test></test>");
    Asset realizedAssessment = rm.store(test, "test assessment", "created by ");
    Authorization auth =
      as.authorizeAssessment(
        testAgent, realizedAssessment, startTime, stopTime);
    LOG.debug(
      "Authorization.getQualifier.getId()=" + auth.getQualifier().getId());
    LOG.debug("realizedAssessment.getId()=" + realizedAssessment.getId());
    assertEquals(realizedAssessment.getId(), auth.getQualifier().getId());
    LOG.debug(OsidManagerFactory.getAgent());
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testGetActiveAssessments()
    throws Exception
  {
    Iterator iter = as.getActiveAssessments(testAgent, null).iterator();
    assertTrue("there should be active assessments", iter.hasNext());
    while(iter.hasNext())
    {
      LOG.debug("here is the next active Assessment: " + iter.next());
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testGetInActiveAssessments()
    throws Exception
  {
    Iterator iter = as.getInActiveAssessments(testAgent, null).iterator();
    assertFalse("there should not be inactive assessments", iter.hasNext());
  }
}
