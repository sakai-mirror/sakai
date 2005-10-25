package uk.ac.cam.caret.sakai.rwiki.bean.test;

import junit.framework.TestCase;

import org.easymock.MockControl;

import uk.ac.cam.caret.sakai.rwiki.bean.RenderBean;
import uk.ac.cam.caret.sakai.rwiki.exception.PermissionException;
import uk.ac.cam.caret.sakai.rwiki.model.RWikiObject;
import uk.ac.cam.caret.sakai.rwiki.service.RWikiObjectService;
import uk.ac.cam.caret.sakai.rwiki.service.RenderService;

public class RenderBeanTest extends TestCase {

    String localName = "Foo";
    String realm = "bar";
    String globalName = "bar.Foo";
    String otherRealm = "realm";
    String user = "user";
    String value = "value";

    RenderService mockRenderService;
    RWikiObjectService mockObjectService;
    RWikiObject mockObject;
    RenderBean rb;
    MockControl renderServiceControl,objectServiceControl, rwikiObjectControl;
    
    public RenderBeanTest(String test) {
        super(test);
    }
    
    
    
    protected void setUp() throws Exception {
        super.setUp();
        renderServiceControl = MockControl.createControl(RenderService.class);
        objectServiceControl = MockControl.createControl(RWikiObjectService.class);
        rwikiObjectControl = MockControl.createControl(RWikiObject.class);
        
        mockRenderService = (RenderService) renderServiceControl.getMock();
        mockObjectService = (RWikiObjectService) objectServiceControl.getMock();
        mockObject = (RWikiObject) rwikiObjectControl.getMock();
        //mockObject = new RWikiObjectImpl();
        
        
        rb = new RenderBean(mockObject,user, mockRenderService, mockObjectService); 
        
    }



    /*
     * Test method for 'uk.ac.cam.caret.sakai.rwiki.bean.RenderBean.renderPage()'
     */
    public void testRenderPage() {
        mockRenderService.renderPage(mockObject, user);
        renderServiceControl.setReturnValue(value);
        objectServiceControl.replay();
        rwikiObjectControl.replay();
        renderServiceControl.replay();
        
        assertTrue(value.equals(rb.renderPage()));
        objectServiceControl.verify();
        renderServiceControl.verify();
        rwikiObjectControl.verify();
    }

    /*
     * Test method for 'uk.ac.cam.caret.sakai.rwiki.bean.RenderBean.renderPage(String, String)'
     */
    public void testRenderPageStringString() {
    		return;
    		/*
        try {
            mockObjectService.getRWikiObject(globalName,user,realm);
            
        } catch (PermissionException e) {
           // EMPTY
        }
        objectServiceControl.setReturnValue(mockObject);
        mockRenderService.renderPage(mockObject,user,otherRealm);
        renderServiceControl.setReturnValue(value);
        objectServiceControl.replay();
        rwikiObjectControl.replay();
        renderServiceControl.replay();
        assertTrue(value.equals(rb.renderPage(globalName, otherRealm)));
        objectServiceControl.verify();
        renderServiceControl.verify();
        rwikiObjectControl.verify();
        */
    }

}
