/*
 * $Header: /cvs/sakai/portal/src/tests/org/sakaiproject/portal/varuna/NormalizeUrlTest.java,v 1.3 2005/01/10 13:31:14 dlhaines.umich.edu Exp $
 * 
 * Created on Nov 11, 2004
 *
 * These are jmock junit tests for the new short url parser.  They 
 * rely on Aspectj aspects to capture the outside calls and return
 * the mock values instead.
 *
 */
package org.sakaiproject.portal.varuna;

/**
 * @author haines
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
//public class NormalizeUrlTest extends MockObjectTestCase {
public class NormalizeUrlTest extends GenericPathTest {
    
    // rule is that all mocks are in the MockInstances static class.
    // and all proxies are local. Mocks being in the static class allows
    // aspects to get at them.  Anyone who needs a proxy can get at it
    // through the mock.
    
    //////////// tests
    
    // test that can change old format urls to new
    
    void bodyTestUrl(String right,String url) {
        VarunaServlet varuna = createVaruna();
        String result = varuna.url.normalize(url);
        assertEquals("url processing wrong: url: ["+url+"] ", right,result);
    }
    
    public void testUrlA() {  bodyTestUrl("","");    }
    public void testUrlB() {  bodyTestUrl("X","X");    }
    public void testUrlC() {  bodyTestUrl("/site/","/site/");    }
    public void testUrlD() {  bodyTestUrl("/site/","?site=");    }
    public void testUrlE() {  bodyTestUrl("/site/","&site=");    }
    public void testUrlF() {  bodyTestUrl("/site/","?site=");    }
    public void testUrlG() {  bodyTestUrl("/page/","&page=");    }
    public void testUrlH() {  bodyTestUrl("/page/","?page=");    }
    public void testUrlI() {  bodyTestUrl("/tool/","&tool=");    }
    public void testUrlJ() {  bodyTestUrl("/tool/","?tool=");    }
    public void testUrlK() {  bodyTestUrl("/","/");    }
    
    // old syntax
    public void testUrlL() {  bodyTestUrl("/page/","?js_pane=");    }
    public void testUrlM() {  bodyTestUrl("/page/","&js_pane=");    }
    
    // test big urls
    public void testUrlBigA() {  bodyTestUrl(
            "http://localhost:8080/varuna/site/SITE/page/PAGE/tool/TOOL",
            "http://localhost:8080/varuna?site=SITE&page=PAGE&tool=TOOL"
            );    }
    public void testUrlBigB() {  bodyTestUrl(
            "http://localhost:8080/varuna/site/SITE/page/JS_PANE",
            "http://localhost:8080/varuna?site=SITE?js_pane=JS_PANE"
            );    }
    public void testUrlBigC() {  bodyTestUrl(
            "http://localhost:8080/varuna/site/SITE/page/JS_PANE/tool/TOOL",
            "http://localhost:8080/varuna?site=SITE?js_pane=JS_PANE&tool=TOOL"
            );    }
    public void testUrlBigD() {  bodyTestUrl(
            "http://localhost:8080/varuna/site/SITE/page/JS_PANE/tool/TOOL",
            "http://localhost:8080/varuna/site/SITE?js_pane=JS_PANE&tool=TOOL"
            );    }
    public void testUrlBigE() {  bodyTestUrl(
            "http://localhost:8080/varuna/site/SITE/page/JS_PANE/tool/TOOL/",
            "http://localhost:8080/varuna/site/SITE?js_pane=JS_PANE&tool=TOOL/"
            );    }
  
  
}  

