/*
 * $Header: /cvs/sakai/portal/src/tests/org/sakaiproject/portal/varuna/ParsePathTest.java,v 1.2 2005/01/10 13:31:14 dlhaines.umich.edu Exp $
 * 
 * Created on Nov 11, 2004
 *
 * These are jmock junit tests for the new short url parser.  They 
 * rely on Aspectj aspects to capture the outside calls and return
 * the mock values instead.
 *
 */
package org.sakaiproject.portal.varuna;

import org.sakaiproject.util.ParameterParser;

/**
 * @author haines
 *  
 */

//public class ParsePathTest extends MockObjectTestCase
public class ParsePathTest extends GenericPathTest
{

    /*
     * Create an expected result and a method to check that the actual result
     * matches the expected result. @author haines
     * 
     * TODO To change the template for this generated type comment go to Window -
     * Preferences - Java - Code Style - Code Templates
     */
    class PathExpected
    {
        protected String portal = null;

        protected String gallery = null;

        protected String site = null;

        protected String page = null;

        protected String tid = null;

        PathExpected(String portal, String gallery, String site, String page,
                String tid)
        {
            this.portal = portal;
            this.gallery = gallery;
            this.site = site;
            this.page = page;
            this.tid = tid;
        }

        void verify(PortalUrl parse)
        {

            if (!checkId(this.portal, parse.pathPortalId))
                fail("Expected portal: [" + this.portal + "] found: ["
                        + parse.pathPortalId + "]");
            if (!checkId(this.gallery, parse.pathGalleryId))
                fail("Expected gallery: [" + this.gallery + "] found: ["
                        + parse.pathGalleryId + "]");
            if (!checkId(this.site, parse.pathSiteId))
                fail("Expected site: [" + this.site + "] found: ["
                        + parse.pathSiteId + "]");
            if (!checkId(this.page, parse.pathPageId))
                fail("Expected page: [" + this.page + "] found: ["
                        + parse.pathPageId + "]");
            if (!checkId(this.tid, parse.pathTId))
                fail("Expected tool: [" + this.tid + "] found: ["
                        + parse.pathTId + "]");
        }
    }

    /*
     * check particular path element id
     */
    boolean checkId(String correctId, String parsedId)
    {
        if ((correctId == null && parsedId != null)
                || ((parsedId != null) && !parsedId.equals(correctId))
                || ((correctId != null) && !correctId.equals(parsedId)))
            return false;
        return true;
    }

    // test this path against this expected result.
    protected void runPathTest(String path, PathExpected result)
    {
        PortalUrl pu = createPortalUrl();
        //      // for top level
        setupPath(path);
        //        if (result.page != null) {
        //            setPageMocks(result.page);
        //        }
        //        if (result.tid != null) {
        //            setToolMocks((result.page == null ? defaultPage : result.page),
        //                    result.tid);
        //        }
        int displayLevel = pu.parseDisplayPath(req, (ParameterParser) pp);
        result.verify(pu);
    }

    // check pure prefix paths

    public void testPortalPrefix()
    {
        runPathTest("/portal", new PathExpected("/portal", null, null, null,
                null));
    }

    // nothing makes sense with gallery note that this is the
    // only form of gallery that makes sense.
    public void testGalleryPrefix()
    {
        runPathTest("/gallery", new PathExpected(null, "/gallery", null, null,
                null));
    }

    public void testSitePrefix()
    {
        runPathTest("/site", new PathExpected(null, null, "", null, null));
    }

    public void testSiteDefaultPageDefault()
    {
        runPathTest("/page",
                new PathExpected(null, null, null, "", null));
    }

    public void testSiteDefaultPageDefaultToolDefault()
    {
        runPathTest("/tool", new PathExpected(null, null, null,
                null, ""));
    }

    // two level paths both without and with default.

    // default site
    public void testPortalSiteDefault()
    {
        runPathTest("/portal/site", new PathExpected("/portal", null, "", null,
                null));
    }

    public void testSiteDefaultPageValue()
    {
        String pageId = "APAGE";

        runPathTest("/page/" + pageId, new PathExpected(null, null,
                null, pageId, null));
    }

    public void testSiteDefaultPageDefaultToolValue()
    {

        String tid = "ATOOL";

        runPathTest("/tool/" + tid, new PathExpected(null, null, null,
                null, tid));
    }

    // test three level
    public void testPortalSiteValue()
    {
        runPathTest("/portal/site/ASITE", new PathExpected("/portal", null,
                "ASITE", null, null));
    }

    public void testSiteValuePageDefault()
    {
        runPathTest("/site/ASITE/page", new PathExpected(null, null, "ASITE",
                "", null));
    }

    public void testSiteDefaultPageValueToolDefault()
    {
        // stub since may not be called, depending on the path.
        String pageId = "APAGE";

        runPathTest("/page/" + pageId + "/tool", new PathExpected(null, null,
                null, pageId, ""));
    }

    //////////////////// test four level
    public void testPortalSiteValuePageDefault()
    {
        runPathTest("/portal/site/ASITE/page", new PathExpected("/portal",
                null, "ASITE", "", null));
    }

    public void testSiteValuePageValue()
    {
        runPathTest("/site/ASITE/page/APAGE", new PathExpected(null, null,
                "ASITE", "APAGE", null));
    }

    public void testSiteDefaultPageValueToolValue()
    {
        // stub since may not be called, depending on the path.
        String pageId = "APAGE";
        String tid = "ATOOL";

        runPathTest("/page/" + pageId + "/tool/" + tid, new PathExpected(null,
                null, null, pageId, tid));
    }

}

