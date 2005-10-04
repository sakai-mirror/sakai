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
package org.sakaiproject.tool.edu.coursemanagement.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sakaiproject.api.edu.coursemanagement.CanonicalCourse;
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
import org.sakaiproject.component.common.edu.coursemanagement.
    CourseSetImpl;

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

  public void testSetUpDefault() throws Exception {
    System.out.println("[Test #1: testSetUpDefault]");
    addSessionType();
    addEnrollmentStatusType();
    addParticipationStatusType();
  }


  /**
   * test create 2 equivalent canonical courses
   * @throws java.lang.Exception
   */
  public void testCreateCanonicalCourse() throws Exception {
    System.out.println("[Test #2: testCreateCanonicalCourse]");
    Date currentDate = new Date();
    CanonicalCourseStatusTypeImpl type = (CanonicalCourseStatusTypeImpl)
        ( (CourseManagementManagerImpl) courseManagementManager).
        getCanonicalCourseStatusByKeyword("canonical.active");
    //1. create math101
    CanonicalCourseImpl math101 = (CanonicalCourseImpl) courseManagementManager.
        createCanonicalCourse(
        "title", "description", "math-101",
        "*uuid_canonical" + currentDate.getTime(), type);
    //setComplete();
    System.out.println("**** done 1: create math101");

    //2. add topics to math101
    math101.addTopic("optimization");
    math101.addTopic("normal distribution");
    math101.addPrerequisite("math-10");
    ( (CourseManagementManagerImpl) courseManagementManager).
        saveCanonicalCourse(math101);
    //setComplete();
    System.out.println("**** done 2: add topic");

    //3. create stat101
    CanonicalCourseImpl stat101 = createCanonicalCourse("title2",
        "stat-101");
    System.out.println("**** done 3: create ata101");

    //4. make them equivalent
    math101.addEquivalent(stat101.getUuid());
    stat101.addEquivalent(math101.getUuid());
    //setComplete();
    System.out.println("**** done 4: make equivalent");
  }

  public void testRemoveCanonical() throws Exception {
    System.out.println("[Test #3: testRemoveCanonical]");
    CanonicalCourseImpl c = createCourse();
    String cUuid = c.getUuid();
    System.out.println("**** done 8: remove canonical" + c);
    courseManagementManager.removeCanonicalCourse(cUuid);
    CanonicalCourse c1 = courseManagementManager.getCanonicalCourse(cUuid);
    System.out.println("**** done 9: REMOVED canonical" + c1);
  }
  
  /**
   * test create a canonical course with one offering and one section
   * @throws java.lang.Exception
   */
  private CanonicalCourseImpl createCourse() throws Exception {
    // -1. set up default type
    addSessionType();
    addEnrollmentStatusType();
    addParticipationStatusType();

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
    //setComplete();
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
    //setComplete();
    System.out.println("**** done 3: update offering 1052-eng-101");

    // 4. add a section to course offering
    CourseSectionTypeImpl sectionType = (CourseSectionTypeImpl)
    ( (CourseManagementManagerImpl) courseManagementManager).
    getCourseSectionTypeByKeyword("section.lecture");
    CourseSectionImpl section = (CourseSectionImpl)((CourseManagementManagerImpl) courseManagementManager)
		.createCourseSection("title", "description", "1052-eng-101-01", 
				offering.getUuid(), session.getUuid(), sectionType);
    //setComplete();
    System.out.println("**** done 4: add section to offering");

    // 5. add student enrollment to course section
    EnrollmentStatusTypeImpl enrollmentStatus = (EnrollmentStatusTypeImpl)
    ( (CourseManagementManagerImpl) courseManagementManager).
       getEnrollmentStatusByKeyword("enrollment_status.full_credit"); 
    System.out.println("**** E status="+enrollmentStatus);
    EnrollmentRecordImpl e = (EnrollmentRecordImpl)((CourseManagementManagerImpl) courseManagementManager)
		.createEnrollmentRecord("daisyf", "student", enrollmentStatus.getUuid(), section.getUuid());
    //setComplete();    
    System.out.println("**** done 5: add studnet enrollment");

    // 6. add an instructor to course section
    ParticipationStatusTypeImpl participationStatus = (ParticipationStatusTypeImpl)
    ( (CourseManagementManagerImpl) courseManagementManager).
    getParticipationStatusByKeyword("participation_status.unknown"); 
    ParticipationRecordImpl p = (ParticipationRecordImpl)((CourseManagementManagerImpl) courseManagementManager)
		.createParticipationRecord("rgollub", "instructor", participationStatus.getUuid(), section.getUuid());
    //setComplete();    
    System.out.println("**** done 6: add participation record");

    // 7. add sub section
    // through the CourseManagementManager.createSubSection() method
    CourseSectionTypeImpl subSectionType = (CourseSectionTypeImpl)
      ( (CourseManagementManagerImpl) courseManagementManager).getCourseSectionTypeByKeyword("section.lab");
    CourseSectionImpl subSection = (CourseSectionImpl)courseManagementManager.createSubSection(
        "title: subSection", "description: subSection",
        "1052-eng-101-01.lab1",
        section.getUuid(), session.getUuid(), subSectionType);
    //setComplete();
    System.out.println("**** done 7: add subsection to section, subSection="+subSection.getCourseSectionId());
    return eng101;
  }

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
    //setComplete();
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
    //setComplete();
    System.out.println("*** testCreateSession(), sessionId = " + s.getSessionId());
    String sUuid = s.getUuid();
    System.out.println("*** testCreateSession(), sessionUuid = " + sUuid);
    SessionImpl s1 = (SessionImpl) ( (CourseManagementManagerImpl)
                                    courseManagementManager).getSession(sUuid);
    System.out.println("*** testCreateSession(), session = " + s1);
    return s;
  }

  public void testCreateCourseSet() throws Exception {
    Date currentDate = new Date();
    HashSet set = new HashSet();
    set.add(createCanonicalCourse("spanish101", "1052-spanish-101-01"));
    set.add(createCanonicalCourse("spanish201", "1052-spanish-102-01"));
    set.add(createCanonicalCourse("spanish301", "1052-spanish-103-01"));
    CourseSetImpl cs =(CourseSetImpl) courseManagementManager.createCourseSet("Course Set 1");
    cs.setContext("Site One");
    cs.setCanonicalCourseSet(set); 
    ((CourseManagementManagerImpl)courseManagementManager).saveCourseSet(cs);
    //setComplete();
    System.out.println("**** testCreateCourseSet(), courseSetId="+cs.getCourseSetId());
  }

  public void addSessionType(){
    Date currentDate = new Date();
    System.out.println("*** populate session type...");
    ArrayList list = new ArrayList();
    list.add(new SessionTypeImpl("org.sakaiproject" ,"coursemgmt" ,
                                 "term.unknown", "Unknown", null, "*uuid_session_1",
                                 new Integer(1), "site", currentDate , "site" ,currentDate));
    list.add(new SessionTypeImpl("org.sakaiproject" ,"coursemgmt" ,
                                 "term.spring", "Spring", null, "*uuid_session_2",
                                 new Integer(1), "site", currentDate , "site" ,currentDate));
    list.add(new SessionTypeImpl("org.sakaiproject" ,"coursemgmt" ,
                                 "term.summer", "Summer", null, "*uuid_session_3",
                                 new Integer(1), "site", currentDate , "site" ,currentDate));
    list.add(new SessionTypeImpl("org.sakaiproject" ,"coursemgmt" ,
                                 "term.fall", "Fall", null, "*uuid_session_4",
                                 new Integer(1), "site", currentDate , "site" ,currentDate));
    list.add(new SessionTypeImpl("org.sakaiproject" ,"coursemgmt" ,
                                 "term.winter", "Winter", null, "*uuid_session_5",
                                 new Integer(1), "site", currentDate , "site" ,currentDate));
    list.add(new SessionTypeImpl("org.sakaiproject" ,"coursemgmt" ,
                                 "term.first_quarter", "First Quarter", null, "*uuid_session_6",
                                 new Integer(1), "site", currentDate , "site" ,currentDate));
    list.add(new SessionTypeImpl("org.sakaiproject" ,"coursemgmt" ,
                                 "term.second_quarter", "Second Quarter", null, "*uuid_session_7",
                                 new Integer(1), "site", currentDate , "site" ,currentDate));
    list.add(new SessionTypeImpl("org.sakaiproject" ,"coursemgmt" ,
                                 "term.third_quarter", "Third Quarter", null, "*uuid_session_8",
                                 new Integer(1), "site", currentDate , "site" ,currentDate));
    list.add(new SessionTypeImpl("org.sakaiproject" ,"coursemgmt" ,
                                 "term.forth_quarter", "Forth Quarter", null, "*uuid_session_9",
                                 new Integer(1), "site", currentDate , "site" ,currentDate));
    ((CourseManagementManagerImpl) courseManagementManager).saveOrUpdateAll(list);
    //setComplete();
  }


  public void addEnrollmentStatusType(){
    Date currentDate = new Date();
    System.out.println("*** populate enrollment status type...");
    ArrayList list = new ArrayList();
    list.add(new EnrollmentStatusTypeImpl("org.sakaiproject" ,"coursemgmt" ,
                                 "enrollment_status.unknown", "Unknown", null, "*uuid_enrollment_status_1",
                                 new Integer(1), "site", currentDate , "site" ,currentDate));
    list.add(new EnrollmentStatusTypeImpl("org.sakaiproject" ,"coursemgmt" ,
                                 "enrollment_status.full_credit", "Full Credit", null, "*uuid_enrollment_status_2",
                                 new Integer(1), "site", currentDate , "site" ,currentDate));
    list.add(new EnrollmentStatusTypeImpl("org.sakaiproject" ,"coursemgmt" ,
                                 "enrollment_status.partial_credit", "Partial Credit", null, "*uuid_enrollment_status_3",
                                 new Integer(1), "site", currentDate , "site" ,currentDate));
    list.add(new EnrollmentStatusTypeImpl("org.sakaiproject" ,"coursemgmt" ,
                                 "enrollment_status.no_credit", "No Credit", null, "*uuid_enrollment_status_4",
                                 new Integer(1), "site", currentDate , "site" ,currentDate));
    list.add(new EnrollmentStatusTypeImpl("org.sakaiproject" ,"coursemgmt" ,
                                 "enrollment_status.auditing", "Auditing", null, "*uuid_enrollment_status_5",
                                 new Integer(1), "site", currentDate , "site" ,currentDate));
    list.add(new EnrollmentStatusTypeImpl("org.sakaiproject" ,"coursemgmt" ,
                                 "enrollment_status.dropped", "Dropped", null, "*uuid_enrollment_status_6",
                                 new Integer(1), "site", currentDate , "site" ,currentDate));
    list.add(new EnrollmentStatusTypeImpl("org.sakaiproject" ,"coursemgmt" ,
                                 "enrollment_status.pending", "Pending", null, "*uuid_enrollment_status_7",
                                 new Integer(1), "site", currentDate , "site" ,currentDate));
    ((CourseManagementManagerImpl) courseManagementManager).saveOrUpdateAll(list);
    //setComplete();
  }

  public void addParticipationStatusType(){
    Date currentDate = new Date();
    System.out.println("*** populate participation status type...");
    ArrayList list = new ArrayList();
    list.add(new ParticipationStatusTypeImpl("org.sakaiproject" ,"coursemgmt" ,
                                 "participation_status.unknown", "Unknown", null, "*uuid_participation_status_1",
                                 new Integer(1), "site", currentDate , "site" ,currentDate));
    list.add(new ParticipationStatusTypeImpl("org.sakaiproject" ,"coursemgmt" ,
                                 "participation_status.full_credit", "Full Credit", null, "*uuid_participation_status_2",
                                 new Integer(1), "site", currentDate , "site" ,currentDate));
    list.add(new ParticipationStatusTypeImpl("org.sakaiproject" ,"coursemgmt" ,
                                 "participation_status.partial_credit", "Partial Credit", null, "*uuid_participation_status_3",
                                 new Integer(1), "site", currentDate , "site" ,currentDate));
    list.add(new ParticipationStatusTypeImpl("org.sakaiproject" ,"coursemgmt" ,
                                 "participation_status.no_credit", "No Credit", null, "*uuid_participation_status_4",
                                 new Integer(1), "site", currentDate , "site" ,currentDate));
    list.add(new ParticipationStatusTypeImpl("org.sakaiproject" ,"coursemgmt" ,
                                 "participation_status.auditing", "Auditing", null, "*uuid_participation_status_5",
                                 new Integer(1), "site", currentDate , "site" ,currentDate));
    list.add(new ParticipationStatusTypeImpl("org.sakaiproject" ,"coursemgmt" ,
                                 "participation_status.dropped", "Dropped", null, "*uuid_participation_status_6",
                                 new Integer(1), "site", currentDate , "site" ,currentDate));
    list.add(new ParticipationStatusTypeImpl("org.sakaiproject" ,"coursemgmt" ,
                                 "participation_status.pending", "Pending", null, "*uuid_participation_status_7",
                                 new Integer(1), "site", currentDate , "site" ,currentDate));
    ((CourseManagementManagerImpl) courseManagementManager).saveOrUpdateAll(list);
    //setComplete();
  }

}
