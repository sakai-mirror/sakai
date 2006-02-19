/**********************************************************************************
*
* $Header$
*
***********************************************************************************
*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
* Copyright (c) 2005 University of Cambridge
* 
* Licensed under the Educational Community License Version 1.0 (the "License");
* By obtaining, using and/or copying this Original Work, you agree that you have read,
* understand, and will comply with the terms and conditions of the Educational Community License.
* You may obtain a copy of the License at:
* 
*      http://cvs.sakaiproject.org/licenses/license_1_0.html
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
* AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*
**********************************************************************************/

package uk.ac.cam.caret.sakai.rwiki.component.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.entity.EntityProducer;
import org.sakaiproject.service.legacy.entity.HttpAccess;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;
import org.sakaiproject.service.legacy.site.Group;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SiteService;
import org.sakaiproject.service.legacy.user.UserDirectoryService;
import org.sakaiproject.service.legacy.user.UserEdit;
import org.sakaiproject.test.SakaiTestBase;
import org.sakaiproject.util.xml.Xml;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import uk.ac.cam.caret.sakai.rwiki.component.service.impl.ComponentPageLinkRenderImpl;
import uk.ac.cam.caret.sakai.rwiki.service.api.RWikiObjectService;
import uk.ac.cam.caret.sakai.rwiki.service.api.RenderService;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiEntity;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiObject;
import uk.ac.cam.caret.sakai.rwiki.utils.SimpleCoverage;

public class ComponentIntegrationTest extends SakaiTestBase {
	private static Log logger = LogFactory
			.getLog(ComponentIntegrationTest.class);

	// private AnnouncementService announcementService;
	// private EntityManager entityManager;
	private SiteService siteService;

	private Site site;

	private Group group1;
	private Site targetSite;

	private Group group2;

	private UserDirectoryService userDirService;

	private RWikiObjectService rwikiObjectservice = null;

	private RenderService renderService = null;

	// Constants
	private static final String GROUP1_TITLE = "group1";
	private static final String GROUP2_TITLE = "group2";

	private static final String[] content = { "Some __Simple__ Content",
			"Here is a \nh1. Heading type1\n\n"

	};

	private static final String[] rendered = { "Some <b class=\"bold\">Simple</b> Content",
			"Here is a \n<h3 class=\"heading-h1\"><a name=\"Headingtype1\"></a>Heading type1</h3><p class=\"paragraph\"/>" };

	private static final String archiveContentResource =  "/uk/ac/cam/caret/sakai/rwiki/component/test/archive.xml";
	/**
	 * Runs only once for this TestCase, so we can keep the same component
	 * manager rather than rebuilding it for each test.
	 * 
	 * @return
	 */
	public static Test suite() {
		TestSetup setup = new TestSetup(new TestSuite(
				ComponentIntegrationTest.class)) {
			protected void setUp() throws Exception {
				oneTimeSetup();
			}
		};
		return setup;
	}

	/**
	 * Setup test fixture (runs once for each test method called)
	 */
	public void setUp() throws Exception {
	
		// Get the services we need for the tests
		siteService = (SiteService) getService("org.sakaiproject.service.legacy.site.SiteService");
		userDirService = (UserDirectoryService) getService("org.sakaiproject.service.legacy.user.UserDirectoryService");
		rwikiObjectservice = (RWikiObjectService) getService("uk.ac.cam.caret.sakai.rwiki.service.api.RWikiObjectService");
		renderService = (RenderService) getService("uk.ac.cam.caret.sakai.rwiki.service.api.RenderService");


		assertNotNull(
				"Cant find site service as org.sakaiproject.service.legacy.site.SiteService ",
				siteService);
		assertNotNull(
				"Cant find User Directory service as org.sakaiproject.service.legacy.user.UserDirectoryService ",
				userDirService);
		assertNotNull("Cant find RWiki Object service as "
				+ RWikiObjectService.class.getName(), rwikiObjectservice);
		assertNotNull("Cant find Render Service service as "
				+ RenderService.class.getName(), renderService);
		// Set username as admin
		setUser("admin");
		
		tearDown();

		userDirService.addUser("test.user.1", "Jane", "Doe", "jd@foo.com",
				"123", null, null);
		userDirService.addUser("test.user.2", "Joe", "Schmoe", "js@foo.com",
				"123", null, null);

		// Create a site
		site = siteService.addSite(generateSiteId(), "course");
		targetSite = siteService.addSite(generateSiteId(), "course");
		// Create a group for SectionAwareness to, er, become aware of
		group1 = site.addGroup();

		group1.setTitle(GROUP1_TITLE);

		// Save the group
		siteService.save(site);

		site.addMember("test.user.1", "Student", true, false);

		// Save the site and its new member
		siteService.save(site);

		// Add a user to a group
		group1.addMember("test.user.1", "Student", true, false);
		
		// Create a site to work from
		site = siteService.addSite(generateSiteId(), "course");

		group2 = targetSite.addGroup();

		group2.setTitle(GROUP2_TITLE);

		// Save the group
		siteService.save(targetSite);

		targetSite.addMember("test.user.1", "Student", true, false);

		// Save the site and its new member
		siteService.save(targetSite);

		// Add a user to a group
		group2.addMember("test.user.1", "Student", true, false);

	}

	/**
	 * Remove the newly created objects, so we can run more tests with a clean
	 * slate.
	 */
	public void tearDown() throws Exception {
		try {
			// Remove the site (along with its groups)
			siteService.removeSite(site);

		} catch (Throwable t) {
		}
		try {
			// Remove the site (along with its groups)
			siteService.removeSite(targetSite);

		} catch (Throwable t) {
		}
		try {
			// Remove the users
			UserEdit user1 = userDirService.editUser("test.user.1");
			userDirService.removeUser(user1);
		} catch (Throwable t) {
		}

		try {
			UserEdit user2 = userDirService.editUser("test.user.2");
			userDirService.removeUser(user2);
		} catch (Throwable t) {
		}
	}

	boolean consolidatedtest = true;
	public void testAll()  throws Exception  {
		consolidatedtest = true;
		xtestBasicMethods();
		xtestRenderPage();
		xtestFindAll();
		xtestURLAccess();
		xtestEntityAccess();
		xtestArchiveAccess();
		xtestMerge();
		xtestImport();

	}
	public void dtestBasicMethods() throws Exception {
		consolidatedtest = false;
		xtestBasicMethods();

	}
	public void dtestRenderPage() throws Exception {
		consolidatedtest = false;
		xtestRenderPage();
		
	}
	public void dtestURLAccess() throws Exception {
		consolidatedtest = false;
		xtestURLAccess();
		
	}
	public void dtestEntityAccess() throws Exception {
		consolidatedtest = false;
		xtestEntityAccess();
		
	}
	public void dtestArchiveAccess() throws Exception {
		consolidatedtest = false;
		xtestArchiveAccess();
		
	}
	public void dtestMerge() throws Exception {
		consolidatedtest = false;
		xtestMerge();
		
	}
	public void dtestImport() throws Exception {
		consolidatedtest = false;
		xtestImport();
		
	}

	/**
	 * A simple set of tests of the render service
	 * @throws Exception
	 */
	public void xtestRenderPage() throws Exception {
		SimpleCoverage.cover("Render Page Test");
		assertEquals("Test and results sets are not the same size ",
				content.length, rendered.length);
		Date d = new Date();
		for (int i = 0; i < content.length; i++) {
			SimpleCoverage.cover("Updating page ");
			rwikiObjectservice.update("HomeTestPageRENDER", "admin", site
					.getReference(), d, content[i]);
			SimpleCoverage.cover("loading page ");
			RWikiObject rwo = rwikiObjectservice.getRWikiObject("HomeTestPageRENDER",
					"admin", site.getReference());
			d = rwo.getVersion();
			ComponentPageLinkRenderImpl cplr = new ComponentPageLinkRenderImpl(
					site.getReference());
			cplr.setCachable(false);
			cplr.setUseCache(false);
			SimpleCoverage.cover("render page ");
			String renderedPage = renderService.renderPage(rwo, "admin", site
					.getReference(), cplr);
			SimpleCoverage.cover("Page Rendered as " + rwo.getContent() + "::"
					+ renderedPage + "::");
			assertEquals("Render Page results was not as expected ",
					rendered[i], renderedPage);
			// at the moment I cant get the render engine up and running.
		}
		SimpleCoverage.cover("Render Page Test Ok");
	}
	
	/**
	 * A list of paths to test
	 */
	private static final String[] accessPaths = {
		"/resources/some/resourcethat/shouldworl,123.html",
		"/resources/some/resourcethat/shouldworl.html",
		"/resources/some/resourcethat/shouldworl",
		"/resources/some/resourcethat/shouldworl,123.html",
		"/wiki/non-existant-context/.rss",
		"/wiki/site/SITEID/hometestpageURL.html",
		"/wiki/site/SITEID/HometestpageURL.html",
		"/wiki/site/SITEID/homeTestpageURL,123123.html",
		"/wiki/site/SITEID/hometestpageURL,0.html",
		"/wiki/site/SITEID/indexURL.html",
		"/wiki/site/SITEID/indexURL.rss",
		"/wiki/site/SITEID/changedURL.html",
		"/wiki/site/SITEID/changedURL.rss"
	};
	/**
	 * some page names to populate
	 */
	private static final String[] pageNames =  {
		"HomeTestPageURL",
		"HomeTestPage2URL",
		"indexURL",
		"changedURL"
	};
	/**
	 * some simple page content to use with the above pageNames
	 */
	private static final String[] pageContent = {
		content[0],
		content[1],
		"{index}",
		"{recent-changes}"
	};
	/**
	 * Load a set of pages, and process a set of URLS
	 * @throws Exception
	 */
	public void xtestURLAccess() throws Exception {
		assertEquals("pageNames and pageContent must be the same length ",pageNames.length,pageContent.length);
		
		for( int i = 0; i < pageNames.length; i++ ) {
			rwikiObjectservice.update(pageNames[i], "admin", site.getReference(),
				new Date(), pageContent[i]);
		}
		Collection copy = new ArrayList();
		String siteID = site.getId();
		for ( int i = 0; i < accessPaths.length; i++ ) {
			String testURL = accessPaths[i];
			int ix = testURL.indexOf("SITEID");
			if ( ix != -1 ) {
				testURL = testURL.substring(0,ix)+siteID+testURL.substring(ix+"SITEID".length());
			}
			logger.info("Testing "+testURL);
			Reference ref = EntityManager.newReference(testURL);
			logger.info("Got "+ref);
			EntityProducer service = ref.getEntityProducer();
			if ( service != null ) {
				MockHttpServletRequest req = new MockHttpServletRequest();
				MockHttpServletResponse res = new MockHttpServletResponse();
				HttpAccess ha = service.getHttpAccess();
				ha.handleAccess(req, res, ref, copy);
				logger.info("URL "+testURL+"Got response of " + res.getContentAsString());
			} else {
				logger.info("Rejected URL "+testURL+"");
			}
		}
		

	}
	public void xtestFindAll() {
		List l = rwikiObjectservice.findRWikiSubPages(site.getReference());
		if ( l.size() == 0 ) {
			logger.info("Found "+l.size()+" pages in "+site.getReference());
			fail(" Fialed to find any pages in "+site.getReference());
		}
		logger.info("Found "+l.size()+" pages ");
	}

	/**
	 * Test the entity access based on a URL
	 * @throws Exception
	 */
	public void xtestEntityAccess() throws Exception {
	
		
		rwikiObjectservice.update("HomeTestPageENTITY", "admin", site.getReference(),
				new Date(), content[0]);
		
		RWikiObject rwo = rwikiObjectservice.getRWikiObject("HomeTestPageENTITY",
				"admin", site.getReference());
		
		RWikiEntity rwe = (RWikiEntity) rwikiObjectservice.getEntity(rwo);
		logger.info("Reference is " + rwe.getReference());
		Reference r = EntityManager.newReference(rwe.getReference()
				+ "html");

		logger.info("Reference found as " + r);
		logger.info("Reference Container " + r.getContainer());
		logger.info("Reference Contex " + r.getContext());
		logger.info("Reference Description " + r.getDescription());
		logger.info("Reference Type " + r.getType());
		Entity e = rwikiObjectservice.getEntity(r);
		assertNotNull("Entity is Null should not be",e);
		logger.info(" Got Entity from getEntity "+e);
		Collection c = rwikiObjectservice.getEntityAuthzGroups(r);
		assertNotNull("AuthZGroups  should not be null  ",c);
		logger.info("getAuthZGroups gave "+c);
		String description  = rwikiObjectservice.getEntityDescription(r);
		assertNotNull("description  should not be null  ",description);
		logger.info("description gave "+description);
		
		ResourceProperties rp = rwikiObjectservice.getEntityResourceProperties(r);
		assertNotNull("ResourceProperties  should not be null  ",rp);
		logger.info("ResourceProperties gave "+rp);
		String url = rwikiObjectservice.getEntityUrl(r);
		assertNotNull("URL  should not be null  ",url);
		logger.info("URL gave "+url);

		// try and get the content

		Entity entity = rwikiObjectservice.getEntity(r);
		RWikiEntity rwentity = (RWikiEntity) entity;
		RWikiObject rwo2 = rwentity.getRWikiObject();

		logger.info("Got Object " + rwo2.getName());

		// try and get the access content
		HttpAccess ha = rwikiObjectservice.getHttpAccess();
		Collection copy = new ArrayList();
		MockHttpServletRequest req = new MockHttpServletRequest();
		MockHttpServletResponse res = new MockHttpServletResponse();
		ha.handleAccess(req, res, r, copy);
		logger.info("Got response of " + res.getContentAsString());

	}

	public void xtestArchiveAccess() throws Exception {
		rwikiObjectservice.update("HomeTestPageARCHIVE", "admin", site.getReference(),
				new Date(), content[0]);
		RWikiObject rwo = rwikiObjectservice.getRWikiObject("HomeTestPageARCHIVE",
				"admin", site.getReference());
		rwikiObjectservice.update("HomeTestPageARCHIVE", "admin", site.getReference(),
				rwo.getVersion(), content[1]);
		
		
		rwikiObjectservice.update("HomeTestPage2ARCHIVE", "admin", site.getReference(),
				new Date(), content[0]);
		rwo = rwikiObjectservice.getRWikiObject("HomeTestPage2ARCHIVE",
				"admin", site.getReference());
		rwikiObjectservice.update("HomeTestPage2ARCHIVE", "admin", site.getReference(),
				rwo.getVersion(), content[1]);
		
		ArrayList attachments = new ArrayList();
		Document doc = Xml.createDocument();
		Stack stack = new Stack();
		Element root = doc.createElement("archive");
		doc.appendChild(root);
		root.setAttribute("source", site.getId());
		root.setAttribute("server", "Integration Test");
		root.setAttribute("date", (new Date()).toString());
		root.setAttribute("system", "SAKAI Integration Test");

		stack.push(root);
		File f = new File("./wikitestdir");
		f.mkdirs();
		String tmpdir = f.getAbsolutePath();
		rwikiObjectservice.archive(site.getId(), doc, stack, tmpdir,
				attachments);
		stack.pop();
		String archiveResult = Xml.writeDocumentToString(doc);
		logger.info("Got Archive \n" + archiveResult);

	}
	public void xtestImport() throws Exception {
		// create 2 pages, add their ids to the list, transfer to annother site, check they were there
		List l = new ArrayList();
		rwikiObjectservice.update("HometestPageIMPORT", "admin", site.getReference(),
				new Date(), content[0]);
		RWikiObject rwo = rwikiObjectservice.getRWikiObject("HometestPageIMPORT",
				"admin", site.getReference());
		l.add(rwo.getId());
		rwikiObjectservice.update("HometestPageIMPORT", "admin", site.getReference(),
				rwo.getVersion(), content[1]);
		
		
		rwikiObjectservice.update("HometestPage2IMPORT", "admin", site.getReference(),
				new Date(), content[0]);
		rwo = rwikiObjectservice.getRWikiObject("HometestPage2IMPORT",
				"admin", site.getReference());
		l.add(rwo.getId());
		rwikiObjectservice.update("HometestPage2IMPORT", "admin", site.getReference(),
				rwo.getVersion(), content[1]);
		
		rwikiObjectservice.importEntities(site.getReference(),targetSite.getReference(),l);
		assertEquals("HometestPage failed to import",true,
				rwikiObjectservice.exists("HometestPageIMPORT",targetSite.getReference()));
		assertEquals("HometestPage2 failed to import",true,
				rwikiObjectservice.exists("HometestPage2IMPORT",targetSite.getReference()));
	}
	
	public void xtestMerge() {
		Document doc = Xml.readDocumentFromStream(this.getClass().getResourceAsStream(archiveContentResource));
		String fromSiteId = doc.getDocumentElement().getAttribute("source");
		NodeList nl = doc.getElementsByTagName("uk.ac.cam.caret.sakai.rwiki.service.api.RWikiObjectService");
		for ( int i = 0; i < nl.getLength(); i++ ) {
			Element el = (Element)nl.item(i);
			String results = rwikiObjectservice.merge(targetSite.getId(),el,"/tmp",fromSiteId,new HashMap(),new HashMap(), new HashSet());
			logger.info("Results of merge operation \n======\n"+results+"\n=======");
		}
	}
	public void xtestBasicMethods() {
		assertEquals("Service was not as expected ","wiki",rwikiObjectservice.getLabel());
		assertEquals("Expected to be able to archive  ",true,rwikiObjectservice.willArchiveMerge());
		assertEquals("Expected to be able to import  ",true,rwikiObjectservice.willImport());
	}

}
