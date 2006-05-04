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

package uk.ac.cam.caret.sakai.rwiki.component.service.impl.test;

import java.util.Date;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.framework.log.CommonsLogger;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import uk.ac.cam.caret.sakai.rwiki.component.model.impl.RWikiEntityImpl;
import uk.ac.cam.caret.sakai.rwiki.component.service.impl.Decoded;
import uk.ac.cam.caret.sakai.rwiki.component.service.impl.XSLTEntityHandler;
import uk.ac.cam.caret.sakai.rwiki.model.RWikiCurrentObjectImpl;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiEntity;

/**
 * @author ieb
 * 
 */
public class XSLTEntityHandlerTest extends TestCase {
	private static Log logger = LogFactory.getLog(XSLTEntityHandlerTest.class);

	private XSLTEntityHandler eh = null;

	public void setUp() {

		eh = new XSLTEntityHandler();

	}

	public void testDecode() {

		XSLTEntityHandler eh = new XSLTEntityHandler();
		eh.setAccessURLStart("/wiki/");
		eh.setMinorType("html");
		String[] test = {
				"/wiki/site/c8e34826-dab9-466c-80a9-e8e9bed50465/home/sdfsdf/sdfsdf/sdfsdfsd/sdfsdfsdf/sdfsdfdsf/sdfsdf,123123.html",
				"/wikisite/c8e34826-dab9-466c-80a9-e8e9bed50465/home/sdfsdf/sdfsdf/sdfsdfsd/sdfsdfsdf/sdfsdfdsf/sdfsdf,123123.html",
				"/wiki/site/c8e34826-dab9-466c-80a9-e8e9bed50465/home,sdfsdf,223,234.html",
				"/wikin/site/c8e34826-dab9-466c-80a9-e8e9bed50465/home.html",
				"/wikin/site/c8e34826-dab9-466c-80a9-e8e9bed50465/home.html",
				"/wiki/site/c8e34826-dab9-466c-80a9-e8e9bed50465/sdfsdfs/home,123123.html",
				"/wiki/site/c8e34826-dab9-466c-80a9-e8e9bed50465/home,123123.html",
				"/wiki/site/c8e34826-dab9-466c-80a9-e8e9bed50465/home.html",
				"/wiki/site/c8e34826-dab9-466c-80a9-e8e9bed50465/sdfsdfs/home,123123.html",
				"/wiki/site/c8e34826-dab9-466c-80a9-e8e9bed50465/sdfsdfs/home.html",
				"wiki/site/c8e34826-dab9-466c-80a9-e8e9bed50465/sdfsdfs/home.html",
				"home.html",
				"/wiki/sitec8e34826-dab9-466c-80a9-e8e9bed50465sdfsdfs/home.html",
				"/wiki/sitec8e34826-dab9-466c-80a9-e8e9bed50465sdfsdfs///home.html",
				"/wiki///sitec8e34826-dab9-466c-80a9-e8e9bed50465sdfsdfs/home.html",
				"/wiki/global/HelpPage.html",
				"/wiki/site/c8e34826-dab9-466c-80a9-e8e9bed50465sdfsdfs/HelpPage.",
				"/wiki/site/c8e34826-dab9-466c-80a9-e8e9bed50465sdfsdfs/HelpPage..",
				"/wiki/site/c8e34826-dab9-466c-80a9-e8e9bed50465sdfsdfs/HelpPage..html",
				"/wiki/site/c8e34826-dab9-466c-80a9-e8e9bed50465sdfsdfs/HelpPage",
				"/wiki/site/site-uk.ac.cam.caret.sakai.rwiki.component.test.componentintegrationtest-71220.0/hometestpage.html"

		};
		Decoded[] results = {
				new Decoded("/site/c8e34826-dab9-466c-80a9-e8e9bed50465",
						"/home/sdfsdf/sdfsdf/sdfsdfsd/sdfsdfsdf/sdfsdfdsf",
						"sdfsdf", "123123"),
				null,
				new Decoded("/site/c8e34826-dab9-466c-80a9-e8e9bed50465", "/",
						"home", "sdfsdf,223,234"),
				null,
				null,
				new Decoded("/site/c8e34826-dab9-466c-80a9-e8e9bed50465",
						"/sdfsdfs", "home", "123123"),
				new Decoded("/site/c8e34826-dab9-466c-80a9-e8e9bed50465", "/",
						"home", "123123"),
				new Decoded("/site/c8e34826-dab9-466c-80a9-e8e9bed50465", "/",
						"home", "-1"),
				new Decoded("/site/c8e34826-dab9-466c-80a9-e8e9bed50465",
						"/sdfsdfs", "home", "123123"),
				new Decoded("/site/c8e34826-dab9-466c-80a9-e8e9bed50465",
						"/sdfsdfs", "home", "-1"),
				null,
				null,
				new Decoded("/sitec8e34826-dab9-466c-80a9-e8e9bed50465sdfsdfs",
						"/", "home", "-1"),
				null,
				null,
				new Decoded("/global", "/", "HelpPage", "-1"),
				null,
				null,
				new Decoded(
						"/site/c8e34826-dab9-466c-80a9-e8e9bed50465sdfsdfs",
						"/", "HelpPage.", "-1"),
				null,
				new Decoded(
						"/site/site-uk.ac.cam.caret.sakai.rwiki.component.test.componentintegrationtest-71220.0",
						"/", "hometestpage", "-1") };
		Decoded[] results2 = {
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				new Decoded(
						"/site/c8e34826-dab9-466c-80a9-e8e9bed50465sdfsdfs",
						"/", "HelpPage", "-1"),
				new Decoded(
						"/site/c8e34826-dab9-466c-80a9-e8e9bed50465sdfsdfs",
						"/", "HelpPage.", "-1"), null, null, null };
		assertEquals("Test and results are not setup correctly ",
				results.length, test.length);
		for (int j = 0; j < test.length; j++) {
			Decoded decoded = eh.decode(test[j]);

			Decoded result = results[j];
			if (decoded != null) {
				logger.info("--Context = " + decoded.getContext());
				logger.info("--getContainer = " + decoded.getContainer());
				logger.info("--getPage = " + decoded.getPage());
				logger.info("--getVersion = " + decoded.getVersion());
				logger.info("--getId = " + decoded.getId());

			} else {
				logger.info("--null");
			}
			if (result != null) {
				logger.info("++Context = " + result.getContext());
				logger.info("++getContainer = " + result.getContainer());
				logger.info("++getPage = " + result.getPage());
				logger.info("++getVersion = " + result.getVersion());
				logger.info("++getId = " + result.getId());
			} else {
				logger.info("++null");
			}

			if (result != null && decoded == null)
				fail(" Should have matched  " + test[j]);
			if (result == null && decoded != null)
				fail(" Should not have matched  " + test[j]);
			if (result != null && decoded != null) {
				assertEquals("Test " + j + " Failed Contexts not the same "
						+ test[j], result.getContext(), decoded.getContext());
				assertEquals("Test " + j + " Failed Container not the same "
						+ test[j], result.getContainer(), decoded
						.getContainer());
				assertEquals("Test " + j + " Failed Page not the same "
						+ test[j], result.getPage(), decoded.getPage());
				assertEquals("Test " + j + " Failed Version not the same "
						+ test[j], result.getVersion(), decoded.getVersion());
				assertEquals(
						"Test " + j + " Failed Id not the same " + test[j],
						result.getId(), decoded.getId());
			}
		}
		eh.setMinorType("");
		for (int j = 0; j < test.length; j++) {
			Decoded decoded = eh.decode(test[j]);

			Decoded result = results2[j];

			if (result != null && decoded == null)
				fail(" Should have matched  " + test[j]);
			if (result == null && decoded != null)
				fail(" Should not have matched  " + test[j]);
			if (result != null && decoded != null) {
				assertEquals("Test " + j + " Failed Contexts not the same "
						+ test[j], result.getContext(), decoded.getContext());
				assertEquals("Test " + j + " Failed Container not the same "
						+ test[j], result.getContainer(), decoded
						.getContainer());
				assertEquals("Test " + j + " Failed Page not the same "
						+ test[j], result.getPage(), decoded.getPage());
				assertEquals("Test " + j + " Failed Version not the same "
						+ test[j], result.getVersion(), decoded.getVersion());
				assertEquals(
						"Test " + j + " Failed Id not the same " + test[j],
						result.getId(), decoded.getId());
			}
		}

		long start = System.currentTimeMillis();
		int iters = 10000;
		for (int i = 0; i < iters; i++) {
			eh.decode(test[i % test.length]);

		}
		float timet = (float) 1.0 * (System.currentTimeMillis() - start);
		float tper = (float) (timet / (1.0 * iters));
		logger.info("Decode call cost = " + tper + " ms");

	}

	public void testXSLT() throws Exception {
		String[] test = { "/uk/ac/cam/caret/sakai/rwiki/component/service/impl/null.xslt"

		};

		RWikiCurrentObjectImpl rwco = new RWikiCurrentObjectImpl();
		RWikiEntity rwe = new RWikiEntityImpl(rwco);
		rwco.setContent("Some Content");
		rwco.setGroupAdmin(false);
		rwco.setId("/site/sdf-sdf-sdf-sdf-sdf-sfd/SomePage/sdfgsfd/Home");
		rwco.setId("/site/sdf-sdf-sdf-sdf-sdf-sfd/SomePage/sdfgsfd/Home");
		rwco.setOwner("The Owner");
		rwco.setUser("The User");
		rwco.setVersion(new Date());
		rwco.setRevision(new Integer(5));

		MockHttpServletRequest request = new MockHttpServletRequest();

		for (int i = 0; i < test.length; i++) {
			MockHttpServletResponse response = new MockHttpServletResponse();
			eh.setLogger(new CommonsLogger());
			eh.setXslt(test[i]);
			eh.init();
			eh.outputContent(rwe, request, response);
			logger.info(response.getContentAsString());
		}
		long start = System.currentTimeMillis();
		int iters = 10;
		for (int j = 0; j < iters; j++) {
			for (int i = 0; i < test.length; i++) {
				MockHttpServletResponse response = new MockHttpServletResponse();
				eh.setLogger(new CommonsLogger());
				// eh.setXslt(test[i]);
				// eh.init();
				eh.outputContent(rwe, request, response);
			}
		}
		float timet = (float) 1.0 * (System.currentTimeMillis() - start);
		float tper = (float) (timet / (1.0 * iters));
		logger.info("Transform and Serialize Call Cost = " + tper + " ms");

	}

}
