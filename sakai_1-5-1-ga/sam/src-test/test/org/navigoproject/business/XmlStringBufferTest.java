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

package test.org.navigoproject.business;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.jdom.Document;
import org.jdom.Element;
import org.navigoproject.business.entity.XmlStringBuffer;

import test.org.navigoproject.fixture.Log4jFixture;
import test.org.navigoproject.fixture.PathInfoFixture;

/**
 *
 * <p>Title: NavigoProject.org</p>
 * <p>Description: OKI based implementation</p>
 * <p>Copyright: Copyright (c) 2003 Indiana University</p>
 * <p>Company: </p>
 * @author <a href="mailto:rpembry@indiana.edu">Randall Embry</a>
 * @version $Id: XmlStringBufferTest.java,v 1.1.1.1 2004/07/28 21:32:08 rgollub.stanford.edu Exp $
 */
public class XmlStringBufferTest
  extends TestCase
{
  private final static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(XmlStringBufferTest.class);
  private Log4jFixture log4jFixture;
  private PathInfoFixture pathInfoFixture;
  private String xml = ExampleXmlString.str;

  /**
   * Creates a new XmlStringBufferTest object.
   *
   * @param name DOCUMENTATION PENDING
   */
  public XmlStringBufferTest(String name)
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

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testSelectNodes()
    throws Exception
  {
    XmlStringBuffer a1 = new XmlStringBuffer(this.xml);

    List e = a1.selectNodes("questestinterop/item/itemmetadata/qmd_itemtype");
    LOG.debug("xpath result: " + e);

    e = a1.selectNodes("count(questestinterop/item)");
    LOG.debug("# of items: " + e);

    e = a1.selectNodes(
        "count(questestinterop/item[/descendant::response_label])");
    LOG.debug("# of items: " + e);

    e = a1.selectNodes("count(//item[descendant::response_label])");
    LOG.debug("# of items: " + e);

    e = a1.selectNodes(
        "questestinterop/item/presentation/material/mattext[@texttype=\"text/plain\"]");

    e = a1.selectNodes("//descendant:material/mattext");

    Iterator iter = e.iterator();
    while(iter.hasNext())
    {
      Element elem = (Element) iter.next();
      LOG.debug("xpath result: " + elem.getText());
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param o DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws IOException DOCUMENTATION PENDING
   * @throws ClassNotFoundException DOCUMENTATION PENDING
   */
  public Object SerializeAndDeserializeObject(Object o)
    throws IOException, ClassNotFoundException
  {
    ByteArrayOutputStream fout = new ByteArrayOutputStream();

    ObjectOutputStream oos = new ObjectOutputStream(fout);
    oos.writeObject(o);
    oos.close();

    ByteArrayInputStream fin = new ByteArrayInputStream(fout.toByteArray());

    ObjectInputStream ois = new ObjectInputStream(fin);
    Object result = ois.readObject();
    ois.close();

    return result;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testBugNullCacheWhenConstructedFromDocument()
    throws Exception
  {
    XmlStringBuffer a1 = new XmlStringBuffer(this.xml);
    XmlStringBuffer a2 = new XmlStringBuffer(a1.getDocument());

    a2.selectNodes(
      "questestinterop/item/presentation/material/mattext[@texttype=\"text/plain\"]");
    LOG.debug("Null Pointer Exception was not thrown. Good.");
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testBugXMLStringBufferDoesNotSerializeProperly()
    throws Exception
  {
    XmlStringBuffer a1 =
      new XmlStringBuffer("<junk><test></test><test></test></junk>");
    XmlStringBuffer a2 =
      (XmlStringBuffer) this.SerializeAndDeserializeObject(a1);

    assertEquals(
      "After de-serialize, Strings should be equal", a1.stringValue(),
      a2.stringValue());

    a1.update("*/test", "obvious change");

    assertFalse("Strings are same", a1.stringValue().equals(a2.stringValue()));

    a1.getDocument();
    a2 = (XmlStringBuffer) this.SerializeAndDeserializeObject(a1);

    LOG.debug(a1.stringValue());
    LOG.debug(a2.stringValue());
    assertEquals(
      "After document change, Strings should be equal", a1.stringValue(),
      a2.stringValue());
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testUpdateDocument()
    throws Exception
  {
    XmlStringBuffer a1 = new XmlStringBuffer(this.xml);
    org.w3c.dom.Document doc = a1.getDocument();

    // TODO - finish UpdateDocument test
    LOG.debug(a1.toString());
  }
}
