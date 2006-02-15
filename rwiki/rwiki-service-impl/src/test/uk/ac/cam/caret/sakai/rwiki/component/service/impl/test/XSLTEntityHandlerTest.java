/**
 * 
 */
package uk.ac.cam.caret.sakai.rwiki.component.service.impl.test;

import java.util.Date;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.framework.log.CommonsLogger;

import uk.ac.cam.caret.sakai.rwiki.component.service.impl.Decoded;
import uk.ac.cam.caret.sakai.rwiki.component.service.impl.XSLTEntityHandler;
import uk.ac.cam.caret.sakai.rwiki.component.service.impl.testutil.JUnitHttpServletRequest;
import uk.ac.cam.caret.sakai.rwiki.component.service.impl.testutil.JUnitHttpServletResponse;
import uk.ac.cam.caret.sakai.rwiki.model.RWikiCurrentObjectImpl;

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
		eh.setMinorType(".html");
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
				"/wiki/global/HelpPage.html"

		};
		Decoded[] results = {
				new Decoded(
						"/site/c8e34826-dab9-466c-80a9-e8e9bed50465",
						"/home/sdfsdf/sdfsdf/sdfsdfsd/sdfsdfsdf/sdfsdfdsf",
						"sdfsdf", "123123" ),
				null,
				new Decoded( "/site/c8e34826-dab9-466c-80a9-e8e9bed50465",
						"/", "home",
						"sdfsdf,223,234" ),
				null,
				null,
				new Decoded(  "/site/c8e34826-dab9-466c-80a9-e8e9bed50465",
						"/sdfsdfs",
						"home", "123123" ),
				new Decoded( "/site/c8e34826-dab9-466c-80a9-e8e9bed50465",
						"/", "home",
						"123123" ),
				new Decoded ( "/site/c8e34826-dab9-466c-80a9-e8e9bed50465",
						"/", "home", "-1" ),
				new Decoded ( "/site/c8e34826-dab9-466c-80a9-e8e9bed50465",
						"/sdfsdfs",
						"home", "123123" ),
				new Decoded( "/site/c8e34826-dab9-466c-80a9-e8e9bed50465",
						"/sdfsdfs",
						"home", "-1" ),
				null,
				null,
				new Decoded (
						"/sitec8e34826-dab9-466c-80a9-e8e9bed50465sdfsdfs",
						"/", "home", "-1" ), null, null,
				new Decoded ( "/global", "/", "HelpPage", "-1" )
				};

		assertEquals("Test and results are not setup correctly ",
				results.length, test.length);
		for (int j = 0; j < test.length; j++) {
			Decoded decoded = eh.decode(test[j]);

			Decoded result = results[j];
			
			if ( result != null && decoded == null ) fail(" Should have matched  "+test[j]);
			if ( result == null && decoded != null ) fail(" Should not have matched  "+test[j]);	
			if ( result != null && decoded != null ) {
			assertEquals("Test " + j + " Failed Contexts not the same " + test[j]
			             								, result.getContext(),decoded.getContext());
			assertEquals("Test " + j + " Failed Container not the same " + test[j]
			             								, result.getContainer(),decoded.getContainer());
			assertEquals("Test " + j + " Failed Page not the same " + test[j]
				             								, result.getPage(),decoded.getPage());
			assertEquals("Test " + j + " Failed Version not the same " + test[j]
				             								, result.getVersion(),decoded.getVersion());
			assertEquals("Test " + j + " Failed Id not the same " + test[j]
				             								, result.getId(),decoded.getId());
			}
		}

		long start = System.currentTimeMillis();
		int iters = 10000;
		for (int i = 0; i < iters; i++) {
			eh.decode(test[i % test.length]);

		}
		float timet = (float) 1.0*(System.currentTimeMillis() - start);
		float tper = (float)( timet / (1.0*iters));
		logger.info("Decode call cost = "+tper+" ms");

	}

	public void testXSLT() throws Exception {
		String[] test = { "/uk/ac/cam/caret/sakai/rwiki/component/service/impl/null.xslt"

		};

		RWikiCurrentObjectImpl rwco = new RWikiCurrentObjectImpl();
		rwco.setContent("Some Content");
		rwco.setGroupAdmin(false);
		rwco.setId("/site/sdf-sdf-sdf-sdf-sdf-sfd/SomePage/sdfgsfd/Home");
		rwco.setId("/site/sdf-sdf-sdf-sdf-sdf-sfd/SomePage/sdfgsfd/Home");
		rwco.setOwner("The Owner");
		rwco.setUser("The User");
		rwco.setVersion(new Date());
		rwco.setRevision(new Integer(5));
		JUnitHttpServletResponse response = new JUnitHttpServletResponse();
		JUnitHttpServletRequest request = new JUnitHttpServletRequest();
		
		
		for (int i = 0; i < test.length; i++) {
			eh.setLogger(new CommonsLogger());
			eh.setXslt(test[i]);
			eh.init();
			eh.outputContent(rwco,request,response);
			logger.info(response.getOutput());
			response.reset();
		}
		long start = System.currentTimeMillis();
		int iters = 10;
		for ( int j = 0; j < iters; j++ ) {
			for (int i = 0; i < test.length; i++) {
				eh.setLogger(new CommonsLogger());
//				eh.setXslt(test[i]);
//				eh.init();
				eh.outputContent(rwco,request,response);
				response.reset();
			}
		}
		float timet = (float)1.0*(System.currentTimeMillis() - start);
		float tper = (float)(timet / (1.0*iters));
		logger.info("Transform and Serialize Call Cost = "+tper+" ms");


	}

}
