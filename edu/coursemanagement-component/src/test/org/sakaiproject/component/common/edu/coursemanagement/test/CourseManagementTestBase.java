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
package org.sakaiproject.component.common.edu.coursemanagement.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.edu.coursemanagement.CanonicalCourse;
import org.sakaiproject.api.edu.coursemanagement.Session;
import org.sakaiproject.api.edu.coursemanagement.SessionType;
import org.sakaiproject.api.edu.coursemanagement.CourseManagementManager;

import org.springframework.test.AbstractTransactionalSpringContextTests;

/**
 * Base class for gradebook test classes that provides the spring application
 * context.  The database used is an in-memory hsqldb by default, but this can
 * be overridden to test specific database configurations by setting the "mem"
 * system property to "false".  In the "mem=false" case, the database configuration
 * is set in the hibernate.properties file in the "hibernate.properties.dir" directory.
 * 
 * For tests that should always use the configured database (such as data loading tests),
 * you should extend {@link org.sakaiproject.tool.gradebook.test.GradebookDbTestBase}
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 */
public abstract class CourseManagementTestBase extends AbstractTransactionalSpringContextTests {
	private static Log log = LogFactory.getLog(CourseManagementTestBase.class);

    protected CanonicalCourse canonicalCourse;
    protected Session session;
    protected SessionType sessionType;
    protected CourseManagementManager courseManagementManager;

    protected void onSetUpInTransaction() throws Exception {
        canonicalCourse = (CanonicalCourse)applicationContext.getBean("org_sakaiproject_api_edu_coursemanagement_CanonicalCourse");
        session = (Session)applicationContext.getBean("org_sakaiproject_api_edu_coursemanagement_Session");
        sessionType = (SessionType)applicationContext.getBean("org_sakaiproject_api_edu_coursemanagement_SessionType");
        courseManagementManager = (CourseManagementManager)applicationContext.getBean("org_sakaiproject_api_edu_coursemanagement_CourseManagementManager");
    }
    
    /**
     * @see org.springframework.test.AbstractDependencyInjectionSpringContextTests#getConfigLocations()
     */
    protected String[] getConfigLocations() {
        
        String[] configLocations = {"spring-db.xml","spring-beans.xml", "spring-hib.xml", "spring-archive.xml"};
        
        return configLocations;
    }
}




