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

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sakaiproject.component.common.edu.coursemanagement.
    CourseManagementManagerImpl;
import org.sakaiproject.component.common.edu.coursemanagement.EnrollmentRecordImpl;
import org.sakaiproject.component.common.edu.coursemanagement.SessionImpl;
import org.sakaiproject.component.common.edu.coursemanagement.SessionTypeImpl;
import org.sakaiproject.component.common.edu.coursemanagement.
    CanonicalCourseImpl;
import org.sakaiproject.component.common.edu.coursemanagement.
    CanonicalCourseStatusTypeImpl;
import org.sakaiproject.component.common.edu.coursemanagement.
    CourseOfferingImpl;
import org.sakaiproject.component.common.edu.coursemanagement.
    CourseOfferingStatusTypeImpl;
import org.sakaiproject.component.common.edu.coursemanagement.
    CourseOfferingTypeImpl;
import org.sakaiproject.component.common.edu.coursemanagement.EnrollmentTypeImpl;
import org.sakaiproject.component.common.edu.coursemanagement.EnrollmentStatusTypeImpl;
import org.sakaiproject.component.common.edu.coursemanagement.ParticipationStatusTypeImpl;
import org.sakaiproject.component.common.edu.coursemanagement.ParticipationRecordImpl;
import org.sakaiproject.component.common.edu.coursemanagement.CourseSectionImpl;
import org.sakaiproject.component.common.edu.coursemanagement.
    CourseSectionStatusTypeImpl;
import org.sakaiproject.component.common.edu.coursemanagement.
    CourseSectionTypeImpl;

import junit.framework.Assert;

/*
 * Tests the grade manager.
 *
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 */
public class CourseManagerTest
    extends CourseManagementTestBase {

  protected void onSetUpInTransaction() throws Exception {
    super.onSetUpInTransaction();
  }

  /**
   * test create 2 equivalent canonical courses
   * @throws java.lang.Exception
   */
  public void testCreateCanonicalCourse() throws Exception {
    Date currentDate = new Date();
    CanonicalCourseStatusTypeImpl type = (CanonicalCourseStatusTypeImpl)
        ( (CourseManagementManagerImpl) courseManagementManager).
        getCanonicalCourseStatusByKeyword("canonical.active");
    //1. create math101
    CanonicalCourseImpl math101 = (CanonicalCourseImpl) courseManagementManager.
        createCanonicalCourse(
        "title", "description", "math-101",
        "*uuid_canonical" + currentDate.getTime(), type);
    setComplete();
    System.out.println("***canonical course Id = " +
                       math101.getCanonicalCourseId());
    System.out.println("**** done 1: create math101");

    //2. add topics to math101
    math101.addTopic("optimization");
    math101.addTopic("normal distribution");
    math101.addPrerequisite("math-10");
    ( (CourseManagementManagerImpl) courseManagementManager).
        saveCanonicalCourse(math101);
    setComplete();
    System.out.println("**** done 2: add topic");

    //3. create stat101
    CanonicalCourseImpl stat101 = createCanonicalCourse("title2",
        "stat-101");
    System.out.println("**** done 3: create ata101");

    //4. make them equivalent
    math101.addEquivalent(stat101.getUuid());
    stat101.addEquivalent(math101.getUuid());
    setComplete();
    System.out.println("**** done 4: make equivalent");
  }

  /**
   * test create a canonical course with one offering and one section
   * @throws java.lang.Exception
   */
  public void testCreateCourse() throws Exception {
    Date currentDate = new Date();
    // 0. create session
    SessionImpl session = testCreateSession();
    System.out.println("**** done 0: create sessoion" + session.getUuid());

    //1. create eng101
    CanonicalCourseImpl eng101 = createCanonicalCourse("English 101",
        "eng-101");
    System.out.println("**** done 1: create eng101");

    // 2. create offering in eng101
    CourseOfferingTypeImpl offeringType = (CourseOfferingTypeImpl)
        ( (CourseManagementManagerImpl) courseManagementManager).
        getCourseOfferingTypeByKeyword("offering.active");
    CourseOfferingImpl offering = (CourseOfferingImpl)((CourseManagementManagerImpl) courseManagementManager)
				.createCourseOffering("title", "description", "1052-eng-101", eng101
						.getUuid(), session.getUuid(), offeringType);
		setComplete();
    System.out.println("**** done 2: create offering 1052-eng-101");
    
    // 3. update course offering
    CourseOfferingStatusTypeImpl status = (CourseOfferingStatusTypeImpl)
        ( (CourseManagementManagerImpl) courseManagementManager).
        getCourseOfferingStatusByKeyword("offering_status.open");
    EnrollmentTypeImpl enrollmentType = (EnrollmentTypeImpl)
        ( (CourseManagementManagerImpl) courseManagementManager).
        getEnrollmentTypeByKeyword("enrollment.final"); 
    offering.setOfferingStatus(status);
    offering.setEnrollmentType(enrollmentType);
    offering.setMaximumStudents(new Integer("0"));
    ((CourseManagementManagerImpl) courseManagementManager)
				.saveCourseOffering(offering);
    setComplete();
    System.out.println("**** done 3: update offering 1052-eng-101");

    // 4. add a section to course offering
    CourseSectionTypeImpl sectionType = (CourseSectionTypeImpl)
    ( (CourseManagementManagerImpl) courseManagementManager).
    getCourseSectionTypeByKeyword("section.lecture");
    CourseSectionImpl section = (CourseSectionImpl)((CourseManagementManagerImpl) courseManagementManager)
		.createCourseSection("title", "description", "1052-eng-101-01", 
				offering.getUuid(), session.getUuid(), sectionType);

    // 5. add student enrollment to course section
    EnrollmentStatusTypeImpl enrollmentStatus = (EnrollmentStatusTypeImpl)
    ( (CourseManagementManagerImpl) courseManagementManager).
    getEnrollmentStatusByKeyword("enrollment_status.full_credit"); 
  	EnrollmentRecordImpl e = (EnrollmentRecordImpl)((CourseManagementManagerImpl) courseManagementManager)
		.createEnrollmentRecord("daisyf", "student", enrollmentStatus.getUuid(), section.getUuid());
    setComplete();    

    // 6. add an instructor to course section
    ParticipationStatusTypeImpl participationStatus = (ParticipationStatusTypeImpl)
    ( (CourseManagementManagerImpl) courseManagementManager).
    getParticipationStatusByKeyword("participation_status.unknown"); 
    ParticipationRecordImpl p = (ParticipationRecordImpl)((CourseManagementManagerImpl) courseManagementManager)
		.createParticipationRecord("rgollub", "instructor", participationStatus.getUuid(), section.getUuid());
    setComplete();    

  }

  /**
   * Course sections must always be derived from a Course Offering.
   *
   * @param title
   * @param description
   * @param sectionNumber
   * @param courseOfferingUuid
   * @param sessionUuid
   * @param type
   * @return a new course section.
   * @author Mark Norton
   */
  /*
     public CourseSection createCourseSection(String title, String description,
                                           String sectionNumber,
                                           String courseOfferingUuid,
                                           String sessionUuid,
                                           CourseSectionType type) {
     }
   */
  private CanonicalCourseImpl createCanonicalCourse(String title,
      String courseNumber) {
    Date currentDate = new Date();
    CanonicalCourseStatusTypeImpl type = (CanonicalCourseStatusTypeImpl) ( (
        CourseManagementManagerImpl) courseManagementManager).
        getCanonicalCourseStatusByKeyword("canonical.active");
    CanonicalCourseImpl c = (CanonicalCourseImpl) ( (
        CourseManagementManagerImpl) courseManagementManager)
        .createCanonicalCourse(
        title, "description", courseNumber,
        "*uuid_" + currentDate.getTime(), type);
    setComplete();
    System.out.println("***canonical course Id = " + c.getCanonicalCourseId());
    return c;
  }

  public SessionImpl testCreateSession() throws Exception {
    System.out.println("*** manger inside test create Session = " +
                       courseManagementManager);

    Date currentDate = new Date();
    SessionTypeImpl type = (SessionTypeImpl) ( (CourseManagementManagerImpl)
                                              courseManagementManager).
        getSessionTypeByKeyword(
        "term.spring");
    SessionImpl s = (SessionImpl) ( (CourseManagementManagerImpl)
                                   courseManagementManager)
        .createSession("Fall 2005", "1052", "2005", type,
                       "*uuid_" + currentDate.getTime(), Boolean.TRUE);
    setComplete();
    System.out.println("*** testCreateSession(), sessionId = " + s.getSessionId());
    String sUuid = s.getUuid();
    System.out.println("*** testCreateSession(), sessionUuid = " + sUuid);
    SessionImpl s1 = (SessionImpl) ( (CourseManagementManagerImpl)
                                    courseManagementManager).getSession(sUuid);
    System.out.println("*** testCreateSession(), session = " + s1);
    return s;
  }

}
