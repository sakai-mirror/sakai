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

import org.sakaiproject.component.common.edu.coursemanagement.CourseManagementManagerImpl;
import org.sakaiproject.component.common.edu.coursemanagement.SessionImpl;
import org.sakaiproject.component.common.edu.coursemanagement.SessionTypeImpl;
import org.sakaiproject.component.common.edu.coursemanagement.CanonicalCourseImpl;
import org.sakaiproject.component.common.edu.coursemanagement.CanonicalCourseStatusTypeImpl;

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
	public void testCreateCanonicalCourse() throws Exception {
		Date currentDate = new Date();
		CanonicalCourseStatusTypeImpl type = (CanonicalCourseStatusTypeImpl)courseManagementManager.
		getCanonicalCourseStatusTypeByKeyword("canonical.active");
    //1. create math101
		CanonicalCourseImpl math101 = (CanonicalCourseImpl) courseManagementManager.createCanonicalCourse(
				"title","description","1052-math-101-01",
				"*uuid_"+currentDate.getTime(), type);
		setComplete();
		System.out.println("***canonical course Id = "+math101.getCanonicalCourseId());
    System.out.println("**** done 1: create math101");

    //2. add topics to math101
		math101.addTopic("optimization");
		math101.addTopic("normal distribution");
		math101.addPrerequisite("math-10");
		courseManagementManager.saveCanonicalCourse(math101);
		setComplete();
	  System.out.println("**** done 2: add topic");
	
	  //3. create stat101
		CanonicalCourseImpl stat101 = createCanonicalCourse("title2","1052-stat-101-01");
	  System.out.println("**** done 3: create ata101");

	   //4. make them equivalent
		math101.addEquivalent(stat101.getUuid());
		stat101.addEquivalent(math101.getUuid());
		setComplete();		
	  System.out.println("**** done 4: make equivalent");
	}

	private CanonicalCourseImpl createCanonicalCourse(String title, String courseNumber){
		Date currentDate = new Date();
		CanonicalCourseStatusTypeImpl type = (CanonicalCourseStatusTypeImpl)courseManagementManager.
		getCanonicalCourseStatusTypeByKeyword("canonical.active");
		CanonicalCourseImpl c = (CanonicalCourseImpl) courseManagementManager.createCanonicalCourse(
				title,"description",courseNumber,
				"*uuid_"+currentDate.getTime(), type);
		setComplete();		
		System.out.println("***canonical course Id = "+c.getCanonicalCourseId());
    return c;
	}

	public void testCreateSession() throws Exception {
		Date currentDate = new Date();
		SessionTypeImpl type = (SessionTypeImpl)courseManagementManager.getSessionTypeByKeyword(
		"term.spring");
		SessionImpl s = (SessionImpl)courseManagementManager.createSession(
				"Fall 2005","1052","2005", type, 
				"*uuid_"+currentDate.getTime(), Boolean.TRUE);
		setComplete();
		System.out.println("***sessionId = "+s.getSessionId());
	}
	
}
