/**********************************************************************************
*
* $Id$
*
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of California, The Regents of the University of Michigan,
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

package org.sakaiproject.test.section.dataload;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.api.section.CourseManager;
import org.sakaiproject.api.section.SectionManager;
import org.sakaiproject.api.section.coursemanagement.Course;
import org.sakaiproject.api.section.coursemanagement.User;
import org.sakaiproject.test.section.SectionsTestBase;
import org.sakaiproject.test.section.manager.UserManager;

public class GradebookDataLoader extends SectionsTestBase {
	private static Log log = LogFactory.getLog(GradebookDataLoader.class);

    public static final String[] SITE_UIDS = {
		"QA_1",
		"QA_2",
		"QA_3",
		"QA_4",
		"QA_5",
		"QA_6",
		"QA_7",
		"QA_8",
    };
    public static final String[] SITE_NAMES = {
		"QA Site #1 [no students, 1 instructor, no self reg, no self switch, external]",
		"QA Site #2 [10 students, 1 instructor, no self reg, no self switch, internal]",
		"QA Site #3 [10 students, 1 instructor, no self reg, no self switch, internal]",
		"QA Site #4 [10 students, 1 instructor, no self reg, no self switch, internal]",
		"QA Site #5 [50 students, 1 instructor, no self reg, no self switch, internal]",
		"QA Site #6 [50 students, 1 instructor, no self reg, no self switch, internal]",
		"QA Site #7 [150 students, 1 instructor, no self reg, no self switch, internal]",
		"QA Site #8 [400 students, 1 instructor, no self reg, no self switch, internal]",
    };
    public static boolean[] SITE_SELF_REG = {
    	false, false, false, false,
    	false, false, false, false,
    };
    public static boolean[] SITE_SELF_SWITCH = {
    	false, false, false, false,
    	false, false, false, false,
    };
    public static boolean[] SITE_EXTERNALLY_MANAGED = {
    	true, false, false, false,
    	false, false, false, false,
    };

    /** Special users */
    public final static String AUTHID_TEACHER_ALL = "authid_teacher";
    public final static String AUTHID_TEACHER_AND_STUDENT = "authid_teacher_student";
    public final static String AUTHID_STUDENT_ALL = "stu_0";
    public final static String AUTHID_NO_SITE = "authid_nowhere";
    public final static String AUTHID_STUDENT_PREFIX = "stu_";

	/** Special sites */
	public static int SITE_AMBIGUOUS_TEACHER = 2;
	public static int SITE_AMBIGUOUS_STUDENT = 6;

	public GradebookDataLoader() {
    	// Don't roll these tests back, since they are intended to load data
		setDefaultRollback(false);
	}

	protected CourseManager courseManager;
	protected SectionManager sectionManager;
    protected UserManager userManager;

	protected void onSetUpInTransaction() throws Exception {
		courseManager = (CourseManager)applicationContext.getBean("org.sakaiproject.api.section.CourseManager");
		sectionManager = (SectionManager)applicationContext.getBean("org.sakaiproject.api.section.SectionManager");
		userManager = (UserManager)applicationContext.getBean("org.sakaiproject.test.section.manager.UserManager");
    }

	public void testLoadData() {
		// Load courses. (No sections yet!)
		List sites = new ArrayList(SITE_UIDS.length);
		for (int i = 0; i < SITE_UIDS.length; i++) {
			sites.add(courseManager.createCourse(SITE_UIDS[i], SITE_NAMES[i], SITE_SELF_REG[i], SITE_SELF_SWITCH[i], SITE_EXTERNALLY_MANAGED[i]));
		}

		// Load users.
        User teacherAll = userManager.createUser(AUTHID_TEACHER_ALL, "Bizzy Teacher", "Teacher, Bizzy", "uTeacher");
        User teacherStudent = userManager.createUser(AUTHID_TEACHER_AND_STUDENT, "Teaching Assistant", "Assistant, Teaching", "uTA");
        List students = new ArrayList(400);

		for(int i=0; i < 400; i++) {
			String firstName;
			String lastName;
			switch(i) {
				case 0:
					firstName = "Abby Lynn";
					lastName = "Astudent";
					break;
				case 1:
					firstName = "Mary";
					lastName = "LongLastNameThatExceedsTheMaximumInTheGradebook";
					break;
				case 3:
					firstName = "Susan";
					lastName = "Smith-Morris";
					break;
				case 4:
					firstName = "Nathan Q., Jr.";
					lastName = "Brewster";
					break;
				case 5:
					firstName = "Carol Lee";
					lastName = "Williams";
					break;
				case 6:
					firstName = "Kim";
					lastName = "Jones Parker";
					break;
				case 7:
					firstName = "Joe";
					lastName = "Brown";
					break;
				case 8:
					firstName = "Joe";
					lastName = "Brown";
					break;
				case 9:
					firstName = "Sarah Jane";
					lastName = "Miller";
					break;
				case 10:
					firstName = "Rachel";
					lastName = "Wilson";
					break;
				case 11:
					firstName = "Ali";
					lastName = "Moore";
					break;
				case 12:
					firstName = "Chen-Wai";
					lastName = "Taylor";
					break;
				case 13:
					firstName = "Samuel Taylor Coleridge";
					lastName = "Ascot";
					break;
				case 14:
					firstName = "Jane Quincy";
					lastName = "Brandenburg";
					break;
				case 15:
					firstName = "Thor";
					lastName = "Mj\u00F8lner";
					break;
				case 16:
					firstName = "Lazy";
					lastName = "Etudient1";
					break;
				case 17:
					firstName = "Lazy";
					lastName = "Etudient2";
					break;
				default:
					firstName = "First Middle";
					lastName = "LastName" + i;
			}
			String uidPrefix = (i != 3) ? "uid_" : "uID_";
			students.add(userManager.createUser(AUTHID_STUDENT_PREFIX + i, firstName + " " + lastName, lastName + ", " + firstName, uidPrefix + i));
		}
		User nobody = userManager.createUser(AUTHID_NO_SITE, "Johnny Nobody", "Nobody, Johnny", AUTHID_NO_SITE);

		// No TAs yet.

		// Load enrollments into the courses.
		for (int i = 0; i < students.size(); i++) {
			// Everyone is added to Site 8.
			courseManager.addEnrollment((User)students.get(i), (Course)sites.get(7));

			// The first 150 students are added to Site 7.
			if (i < 150) {
				courseManager.addEnrollment((User)students.get(i), (Course)sites.get(6));

				// The first 50 students are added to Site 5 and 6, but 6 contains a special student....
				if (i < 50) {
					courseManager.addEnrollment((User)students.get(i), (Course)sites.get(4));
					if (i < 49) {
						courseManager.addEnrollment((User)students.get(i), (Course)sites.get(5));

						// The first 10 students are added to Site 2, 3, and 4.
						if (i < 10) {
							courseManager.addEnrollment((User)students.get(i), (Course)sites.get(3));
							courseManager.addEnrollment((User)students.get(i), (Course)sites.get(2));
							courseManager.addEnrollment((User)students.get(i), (Course)sites.get(1));
						}
					}
				}
			}
		}

		// Load instructors into the courses.
		for (Iterator iter = sites.iterator(); iter.hasNext(); ) {
			courseManager.addInstructor(teacherAll, (Course)iter.next());
		}

		// Load the ambiguous one.
		courseManager.addInstructor(teacherStudent, (Course)sites.get(SITE_AMBIGUOUS_TEACHER));
		courseManager.addEnrollment(teacherStudent, (Course)sites.get(SITE_AMBIGUOUS_STUDENT));
	}

}

/**********************************************************************************
 * $Id$
 *********************************************************************************/
