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

import junit.framework.TestCase;

import test.org.navigoproject.fixture.Log4jFixture;
import test.org.navigoproject.fixture.PathInfoFixture;

/*
   class AssessmentExample
   {
    public Assessment createAssessment()
    {
      Assessment a= Assessment.getInstance();
      a.Ident="test122211";
      return a;
    }
   }
   class Assessment implements java.io.Serializable
   {
    public String Ident;
    public static Assessment getInstance()
    {
      return new Assessment();
    }
    private Assessment()
    {
    }
    public SectionContainer getSections()
    {
      return null;
    }
    public String getIdent()
    {
      return this.Ident;
    }
   }
 */

/**
 *
 * <p>Title: NavigoProject.org</p>
 * <p>Description: OKI based implementation</p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author <a href="mailto:rpembry@indiana.edu">Randall P. Embry</a>
 * @version $Id: DigitalRepositoryTest.java,v 1.1.1.1 2004/07/28 21:32:08 rgollub.stanford.edu Exp $
 */
public class DigitalRepositoryTest
  extends TestCase
{
  private final static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(DigitalRepositoryTest.class);
  private Log4jFixture log4jFixture;
  private PathInfoFixture pathInfoFixture;
  final String TEST_ASSESSMENT_ID =
    "20030620122045478-687201301829965738-iu.edu";

  /**
   * Creates a new DigitalRepositoryTest object.
   *
   * @param name DOCUMENTATION PENDING
   */
  public DigitalRepositoryTest(String name)
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

    /**@todo verify the constructors*/
    log4jFixture = new Log4jFixture(this);
    pathInfoFixture = new PathInfoFixture(this);

    log4jFixture.setUp();
    pathInfoFixture.setUp();
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

  //  public void debugAssessment(String msg, Assessment a)
  //  {
  //
  //  }
  //  public void testSerializeAssessment()
  //      throws Exception
  //  {
  //    Assessment a = new AssessmentExample().createAssessment();
  //    ByteArrayOutputStream fout = new ByteArrayOutputStream();
  //
  //    ObjectOutputStream oos = new ObjectOutputStream(fout);
  //    oos.writeObject(a);
  //    oos.close();
  //
  //    ByteArrayInputStream fin = new ByteArrayInputStream(fout.toByteArray());
  //
  //    ObjectInputStream ois = new ObjectInputStream(fin);
  //    Assessment b = (Assessment)ois.readObject();
  //    ois.close();
  //
  //    this.debugAssessment("before", a);
  //    this.debugAssessment("after", b);
  //
  //  }
  //  public void testPersistAssessment()
  //      throws OsidException
  //
  //  {
  //
  //    boolean create = true;
  //
  //    create = false; //comment this line to create & persist (be sure to record its Id!)
  //
  //    if(create)
  //    {
  //
  //      LOG.debug("testPersistAssessment()");
  //      final String TEST_DR_ID = "20030617162322974-670611648105794032-iu.edu";
  //      SharedManager sharedManager;
  //
  //      // use the OsidLoader to get an instance of a Manager
  //      OsidOwner myOwner = new OsidOwner();
  //
  //      AssessmentExample ae = new AssessmentExample();
  //      Assessment assessment = ae.createAssessment();
  //
  //      sharedManager = org.navigoproject.oki.ManagerFactory.createSharedManager(
  //          myOwner);
  //      DigitalRepositoryManager drm = org.navigoproject.oki.ManagerFactory.
  //          createDigitalRepositoryManager();
  //
  //      Id testDrId = sharedManager.getId(TEST_DR_ID);
  //      DigitalRepository dr = drm.getDigitalRepository(testDrId);
  //
  //      Asset myAsset = dr.createAsset("Sample Assessment", "A sample Assessment",
  //                                     org.navigoproject.oki.TypeLib.DR_JAVA);
  //
  //      Id myAssetId = myAsset.getId();
  //      LOG.debug("myAssetId:" + myAssetId);
  //      LOG.debug("myAssetId:" + myAssetId);
  //
  //      myAsset.updateContent(assessment);
  //    }
  //
  //  }
  //  public Assessment testRetrieveAssessment()
  //  {
  //    LOG.debug("testRetrieveAssessment()");
  //    Assessment result;
  //    SharedManager sharedManager;
  //
  //    // use the OsidLoader to get an instance of a Manager
  //    OsidOwner myOwner = new OsidOwner();
  //
  //    try
  //    {
  //      sharedManager = org.navigoproject.oki.ManagerFactory.createSharedManager(
  //          myOwner);
  //      DigitalRepositoryManager drm = org.navigoproject.oki.ManagerFactory.
  //          createDigitalRepositoryManager();
  //
  //      Id testAssessmentId = sharedManager.getId(TEST_ASSESSMENT_ID);
  //      result = (Assessment)(drm.getAsset(testAssessmentId).getContent());
  //      LOG.debug("Assessment retrieved from DigitalRepository");
  //
  //      this.debugAssessment("retrieved", result);
  //
  //    }
  //    catch(OsidException ex)
  //    {
  //      LOG.error("Something failed; falling back to to static Assessment: " +
  //                ex.getMessage());
  //      AssessmentExample ae = new AssessmentExample();
  //      result = ae.createAssessment();
  //    }
  //    return result;
  //  }
  //
  //  public void testUpdateAssessment()
  //      throws OsidException
  //  {
  //    LOG.debug("testUpdateAssessment()");
  //
  //    boolean update = true;
  //
  //    update = false; //comment this line to really update this Assessment
  //
  //    if(update)
  //    {
  //      SharedManager sharedManager;
  //
  //      // use the OsidLoader to get an instance of a Manager
  //      OsidOwner myOwner = new OsidOwner();
  //
  //      AssessmentExample ae = new AssessmentExample();
  //      Assessment assessment = ae.createAssessment();
  //
  //      sharedManager = org.navigoproject.oki.ManagerFactory.createSharedManager(
  //          myOwner);
  //      DigitalRepositoryManager drm = org.navigoproject.oki.ManagerFactory.
  //          createDigitalRepositoryManager();
  //
  //      Id testAssessmentId = sharedManager.getId(TEST_ASSESSMENT_ID);
  //      Asset asset = drm.getAsset(testAssessmentId);
  //      asset.updateContent(assessment);
  //      this.debugAssessment("updated", assessment);
  //    }
  //  }
  //
}
