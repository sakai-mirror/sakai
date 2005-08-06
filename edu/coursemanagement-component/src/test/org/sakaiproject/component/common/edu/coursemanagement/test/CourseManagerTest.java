/**********************************************************************************
*
* $Id$
*
***********************************************************************************
*
* Copyright (c) 2003, 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
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
package  org.sakaiproject.component.common.edu.coursemanagement.test;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.sakaiproject.component.common.edu.coursemanagement.SessionImpl;
import org.sakaiproject.component.common.edu.coursemanagement.SessionTypeImpl;


import junit.framework.Assert;

/*
 * Tests the grade manager.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 */
public class CourseManagerTest extends CourseManagementTestBase {

    protected void onSetUpInTransaction() throws Exception {
        super.onSetUpInTransaction();
    }
    /**
    public void testCreateCanonicalCourse() throws Exception {
        courseManagementManager.createCanonicalCourse("title","description","1052-math-101-01",
	    "*uuid_math-101-01", null);
        setComplete();
    }
    */

    public void testCreateSession() throws Exception {
        System.out.println("***heloo");
        SessionTypeImpl type = new SessionTypeImpl();
        type.setUuid("*uuid_essionType_uuid");
        Date currentDate = new Date();
        SessionImpl s = (SessionImpl)courseManagementManager.createSession("Fall 2005","1052","2005",
	    type, "*uuid_1052", Boolean.TRUE, "site", currentDate, "site", currentDate);
        setComplete();
        System.out.println("***session ="+s);
        System.out.println("***sessionId = "+s.getSessionId());
    }

}
