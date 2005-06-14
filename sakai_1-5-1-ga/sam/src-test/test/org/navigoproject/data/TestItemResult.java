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
 * <p>Title: NavigoProject.org</p>
 * <p>Description: OKI based implementation</p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author jlannan
 * @version $Id: TestItemResult.java,v 1.1.1.1 2004/07/28 21:32:08 rgollub.stanford.edu Exp $
 */
/**
 * @author jlannan
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/*
 * Copyright 2003, Trustees of Indiana University
 */
package test.org.navigoproject.data;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.navigoproject.data.PublishedAssessmentBean;
import org.navigoproject.osid.impl.PersistenceService;

import test.org.navigoproject.fixture.SpringFixture;


/**
 * DOCUMENTATION PENDING
 *
 * @author $author$
 * @version $Id: TestItemResult.java,v 1.1.1.1 2004/07/28 21:32:08 rgollub.stanford.edu Exp $
 */
public class TestItemResult
  extends TestCase
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(TestItemResult.class);
	
	SpringFixture sf;
  /**
   * Creates a new TestAuthz object.
   *
   * @param name DOCUMENTATION PENDING
   */
  public TestItemResult(String name)
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
    sf = new SpringFixture(null);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  protected void tearDown()
    throws Exception
  {
    super.tearDown();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void testItemResult()
    throws Exception
  {
//  	ThreadLocalMapProvider.getMap().put(Constants.HTTP_SERVLET_REQUEST,new MockHttpServletRequest(null));     
//		OsidOwner myOwner = new OsidOwner();
//		AuthorizationManager azm = OsidManagerFactory.createAuthorizationManager(myOwner);
//		SharedManager sm = OsidManagerFactory.createSharedManager(myOwner);		
//		azm.deleteAuthorization(sm.getId("d:d:d"));

//    AssetBean ab = OsidPersistenceService.getInstance().getDrQueries().returnMostRecentAsset("76180cad-dbf7-4f56-0091-211effcd3386");
//    System.out.println(ab.getAssetPK().getId());
//    
//		AssetBean ab2 = OsidPersistenceService.getInstance().getDrQueries().returnMostRecentAssetByDate("76180cad-dbf7-4f56-0091-211effcd3386", Calendar.getInstance());
//	  System.out.println(ab2.getAssetPK().getId());
    
    
//    AssetBean ab = OsidPersistenceService.getInstance().getDrQueries().returnMostRecentAsset("b737d99c-26cd-4799-80ef-3f6d04ebff10");    
//    System.out.println(ab.getAssetPK().getVersion());
//    
//		AssetBean ab2 = OsidPersistenceService.getInstance().getDrQueries().returnMostRecentAsset("76180cad-dbf7-4f56-0091-211effcd3386");    
//		System.out.println(ab2.getAssetPK().getVersion());

//      QtiSettingsBean qsb = new QtiSettingsBean();
//      qsb.setId("id1");
//      qsb.setPasswordRestriction("thispassword");
//      PersistenceService.getInstance().getQtiQueries().persistSettingsBean(qsb);
//      
//		  QtiSettingsBean qsb2 = PersistenceService.getInstance().getQtiQueries().returnQtiSettingsBean("id1");
//		  System.out.println(qsb2.getPasswordRestriction());

//		  ThreadLocalMapProvider.getMap().put(Constants.HTTP_SERVLET_REQUEST,new MockHttpServletRequest(null));
//      SharedManager sm = OsidManagerFactory.createSharedManager();
//      Id id1 = sm.getId("c6224034-6f4a-4d12-00f8-c124e5c4e4f4");
//      int i = PersistenceService.getInstance().getAssessmentQueries().getNumberOfRealizedAssessments(id1);
//      System.out.println(i);

      //PersistenceService.getInstance().getQtiQueries().deleteSettingsBean("id12");
      //PersistenceService.getInstance().getQtiQueries().persistSettingsBean(qsb);
      //qsb = PersistenceService.getInstance().getQtiQueries().returnQtiSettingsBean("id11");
      //System.out.println(qsb.getId());    
      
  }
}
