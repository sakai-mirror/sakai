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

package test.org.navigoproject.business.criteria;

import org.navigoproject.business.criteria.Criteria;
import org.navigoproject.business.criteria.CriteriaAnd;
import org.navigoproject.business.criteria.CriteriaFactory;
import org.navigoproject.business.criteria.CriteriaOr;
import org.navigoproject.business.criteria.Criterion;
import org.navigoproject.business.criteria.CriterionEq;
import org.navigoproject.business.criteria.CriterionGt;
import org.navigoproject.business.criteria.CriterionGte;
import org.navigoproject.business.criteria.CriterionLt;
import org.navigoproject.business.criteria.CriterionLte;
import org.navigoproject.business.criteria.EvaluationException;

import java.util.HashMap;
import java.util.Iterator;

import junit.framework.TestCase;

import test.org.navigoproject.fixture.Log4jFixture;
import test.org.navigoproject.fixture.PathInfoFixture;

/**
 *
 * <p>Title: NavigoProject.org</p>
 * <p>Description: OKI based implementation</p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author <a href="mailto:rpembry@indiana.edu">Randall P. Embry</a>
 * @version $Id: CriteriaTest.java,v 1.1.1.1 2004/07/28 21:32:08 rgollub.stanford.edu Exp $
 */
public class CriteriaTest
  extends TestCase
{
  private final static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(CriteriaTest.class);
  private Criteria criteria = null;
  private Log4jFixture log4jFixture;
  private PathInfoFixture pathInfoFixture;

  /**
   * Creates a new CriteriaTest object.
   *
   * @param name DOCUMENTATION PENDING
   */
  public CriteriaTest(String name)
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

    /**@todo remove this*/ org.apache.log4j.BasicConfigurator.configure();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  protected void tearDown()
    throws Exception
  {
    criteria = null;
    log4jFixture.tearDown();
    pathInfoFixture.tearDown();

    log4jFixture = null;
    pathInfoFixture = null;
    super.tearDown();
  }

  /**
   * DOCUMENTATION PENDING
   */
  public void testCriteria()
  {
    HashMap data1 = new HashMap();
    HashMap data2 = new HashMap();
    HashMap data3 = new HashMap();

    data1.put("LNAME", "Embry");
    data1.put("FNAME", "Randall");

    data2.put("LNAME", "Embry");
    data2.put("FNAME", "Fred");

    data3.put("LNAME", "Jones");
    data3.put("FNAME", "Fred");

    Criterion a = new CriterionEq("LNAME", "Embry");
    Criterion b = new CriterionEq("FNAME", "Randall");
    Criteria c = new CriteriaAnd();

    // c = a & b
    c.add(a);
    c.add(b);

    // d = a | b
    Criteria d = new CriteriaOr(c);

    if(LOG.isDebugEnabled())
    {
      LOG.debug(a.toString());
      LOG.debug(b.toString());
      LOG.debug(c.toString());
      LOG.debug(d.toString());
    }

    try
    {
      // All should eval true for data1
      assertTrue(a.eval(data1));
      assertTrue(b.eval(data1));
      assertTrue(c.eval(data1));
      assertTrue(d.eval(data1));

      // Only A and D should eval true for data2
      assertTrue(a.eval(data2));
      assertFalse(b.eval(data2));
      assertFalse(c.eval(data2));
      assertTrue(d.eval(data2));

      // All should eval false for data3
      assertFalse(a.eval(data3));
      assertFalse(b.eval(data3));
      assertFalse(c.eval(data3));
      assertFalse(d.eval(data3));
    }
    catch(EvaluationException ex1)
    {
      assertFalse("Unexpected CriteriaException: " + ex1.getMessage(), true);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws EvaluationException DOCUMENTATION PENDING
   */
  public void testComparators()
    throws EvaluationException
  {
    HashMap data1 = new HashMap();
    data1.put("AGE", new Integer(25));

    HashMap data2 = new HashMap();
    data2.put("AGE", new Integer(35));

    HashMap data3 = new HashMap();
    data3.put("AGE", new Integer(31));

    Criterion a = new CriterionGt("AGE", new Integer(31));
    Criterion b = new CriterionGte("AGE", new Integer(31));
    Criterion c = new CriterionLt("AGE", new Integer(31));
    Criterion d = new CriterionLte("AGE", new Integer(31));

    assertFalse(a.eval(data1));
    assertTrue(a.eval(data2));
    assertFalse(a.eval(data3));

    assertFalse(b.eval(data1));
    assertTrue(b.eval(data2));
    assertTrue(b.eval(data3));

    assertTrue(c.eval(data1));
    assertFalse(c.eval(data2));
    assertFalse(c.eval(data3));

    assertTrue(d.eval(data1));
    assertFalse(d.eval(data2));
    assertTrue(d.eval(data3));

    HashMap data4 = new HashMap();
    data4.put("AGE", new Boolean(true));

    try
    {
      a.eval(data4);
      fail("Boolean isn't Comparable");
    }
    catch(EvaluationException ex)
    {
      ;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws EvaluationException DOCUMENTATION PENDING
   */
  public void testNegation()
    throws EvaluationException
  {
    HashMap data1 = new HashMap();
    data1.put("AGE", new Integer(25));

    HashMap data2 = new HashMap();
    data2.put("AGE", new Integer(35));

    Criterion a = new CriterionGt("AGE", new Integer(31));

    Criteria b = new CriteriaOr();
    b.add(a);

    LOG.debug(b.toString());
    assertFalse(b.eval(data1));
    b.negate();
    LOG.debug(b.toString());
    assertTrue(b.eval(data1));
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testBeans()
    throws Exception
  {
    SampleBean bean1 = new SampleBean();
    bean1.setAge(new Integer(5));

    SampleBean bean2 = new SampleBean();
    bean2.setAge(new Integer(31));

    Criterion a = new CriterionEq("age", new Integer(31));

    assertFalse("5==31", a.eval(bean1));
    assertTrue("31==31", a.eval(bean2));

    Criterion b = new CriterionLte("age", new Integer(31));

    assertTrue("5<=31", b.eval(bean1));
    assertTrue("31<=31", b.eval(bean2));

    Criterion c = new CriterionGt("age", new Integer(10));

    Criteria d = new CriteriaAnd();
    d.add(b);
    d.add(c);

    assertFalse(d.eval(bean1));
    assertTrue(d.eval(bean2));
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testCriteriaFactory()
    throws Exception
  {
    SampleBean bean1 = new SampleBean();
    bean1.setAge(new Integer(5));

    SampleBean bean2 = new SampleBean();
    bean2.setAge(new Integer(31));

    CriteriaFactory f = new CriteriaFactory();

    Iterator iter = f.availableCriterion("");

    while(iter.hasNext())
    {
      LOG.debug("symbol: " + iter.next());
    }

    Criterion c = f.makeCriterion("=", "age", new Integer(5));
    assertTrue(c.eval(bean1));
    assertFalse(c.eval(bean2));
  }
}
