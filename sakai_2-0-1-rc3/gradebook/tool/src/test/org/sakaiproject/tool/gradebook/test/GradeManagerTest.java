/**********************************************************************************
*
* $Header: $
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
package org.sakaiproject.tool.gradebook.test;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.sakaiproject.tool.gradebook.Assignment;
import org.sakaiproject.tool.gradebook.AssignmentGradeRecord;
import org.sakaiproject.tool.gradebook.CourseGrade;
import org.sakaiproject.tool.gradebook.CourseGradeRecord;
import org.sakaiproject.tool.gradebook.GradeMapping;
import org.sakaiproject.tool.gradebook.Gradebook;
import org.sakaiproject.tool.gradebook.business.FacadeUtils;
import org.sakaiproject.tool.gradebook.facades.standalone.EnrollmentStandalone;
import org.sakaiproject.tool.gradebook.facades.standalone.UserStandalone;

/**
 * Tests the grade manager.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 */
public class GradeManagerTest extends GradebookTestBase {

    protected void onSetUpInTransaction() throws Exception {
        super.onSetUpInTransaction();
    }

    /**
     * Save the gradebook.  This is a hack to get around hibernate
     * collections problems.
     * 
     * @throws Exception
     */
    public void testCreateGradebookForTesting() throws Exception {
        String className = this.getClass().getName();
        gradebookService.addGradebook(className, className);
        setComplete();
    }
    
    public void testGradeManager() throws Exception {
        Gradebook gradebook = gradebookManager.getGradebook(this.getClass().getName());

        // Get a gradebook and an assignment
        Gradebook persistentGradebook = gradebookManager.getGradebook(this.getClass().getName());
        gradableObjectManager.createAssignment(persistentGradebook.getId(), "Assignment #1", new Double(20), new Date());
        Assignment persistentAssignment = (Assignment)gradableObjectManager.
            getAssignmentsWithStats(persistentGradebook.getId()).get(0);

        // Create a map of studentUserUids to grades
        Map map = new HashMap();
        map.put("testStudentUserUid1", new Double(18));
        map.put("testStudentUserUid2", new Double(19));
        map.put("testStudentUserUid3", new Double(20));

        gradeManager.updateAssignmentGradeRecords(persistentAssignment.getId(), map);

        // Fetch the grade records
        List records = gradeManager.getPointsEarnedSortedGradeRecords(persistentAssignment);

        // Ensure that each of the students in the map have a grade record, and
        // that their grade is correct
        Set students = map.keySet();
        for(Iterator iter = records.iterator(); iter.hasNext();) {
            AssignmentGradeRecord agr = (AssignmentGradeRecord)iter.next();
            double tmpScore = ((Double)map.get(agr.getStudentId())).doubleValue();
            double persistentScore = agr.getPointsEarned().doubleValue();
            Assert.assertTrue(tmpScore == persistentScore);
        }

        // Add overrides to the course grades
        map.clear();
        map.put("testStudentUserUid1", "C-");
        map.put("testStudentUserUid2", "D+");
        map.put("testStudentUserUid3", "F");

        CourseGrade courseGrade = gradableObjectManager.getCourseGradeWithStats(persistentGradebook.getId());
        gradeManager.updateCourseGradeRecords(persistentGradebook.getId(), map);

        GradeMapping gradeMapping = persistentGradebook.getSelectedGradeMapping();

        // Ensure that the sort grades have been updated to reflect the overridden grades
        List courseGradeRecords = gradeManager.getPointsEarnedSortedGradeRecords(courseGrade);
        for(Iterator iter = courseGradeRecords.iterator(); iter.hasNext();) {
            CourseGradeRecord cgr = (CourseGradeRecord)iter.next();
            Double sortGrade = cgr.getSortGrade();
            String studentId = cgr.getStudentId();
            String tmpGrade = (String)map.get(cgr.getStudentId());
            Assert.assertTrue(sortGrade.equals(gradeMapping.getValue(tmpGrade)));
            Assert.assertTrue(gradeMapping.getGrade(cgr.getSortGrade()).equals(tmpGrade));
        }

        // Remove the overrides
        map.clear();
        map.put("testStudentUserUid1", null);
        map.put("testStudentUserUid2", null);
        map.put("testStudentUserUid3", null);
        gradeManager.updateCourseGradeRecords(persistentGradebook.getId(), map);

        // Ensure that the sort grades have been updated
        courseGradeRecords = gradeManager.getPointsEarnedSortedGradeRecords(courseGrade);
        double totalPoints = gradableObjectManager.getTotalPoints(persistentGradebook.getId());

        for(Iterator iter = courseGradeRecords.iterator(); iter.hasNext();) {
            CourseGradeRecord cgr = (CourseGradeRecord)iter.next();
            Double percent = cgr.calculatePercent(totalPoints);
            Double sortGrade = cgr.getSortGrade();
            String studentId = cgr.getStudentId();
            Assert.assertTrue(sortGrade.doubleValue() - percent.doubleValue() < .001);
        }

        List allGradeRecords = gradeManager.getPointsEarnedSortedAllGradeRecords(persistentGradebook.getId());
        // There should be six grade records for these students
        Assert.assertTrue(allGradeRecords.size() == 6);

        // Create a new, smaller set of enrollments and ensure the smaller set of grade records are selected correctly
        Set filteredEnrollments = new HashSet();
        filteredEnrollments.add(new EnrollmentStandalone(new UserStandalone("testStudentUserUid1", null, null, null), persistentGradebook));
        filteredEnrollments.add(new EnrollmentStandalone(new UserStandalone("testStudentUserUid2", null, null, null), persistentGradebook));
        List filteredGradeRecords = gradeManager.getPointsEarnedSortedAllGradeRecords(persistentGradebook.getId(), FacadeUtils.getStudentUids(filteredEnrollments));

        // There should be four grade records for these students
        Assert.assertTrue(filteredGradeRecords.size() == 4);

        // There should be two grade records for these students and for this assignment
        filteredGradeRecords = gradeManager.getPointsEarnedSortedGradeRecords(persistentAssignment, FacadeUtils.getStudentUids(filteredEnrollments));
        Assert.assertTrue(filteredGradeRecords.size() == 2);
    }

    public void testExcessiveScores() throws Exception {
        Gradebook gradebook = gradebookManager.getGradebook(this.getClass().getName());
        Long asgId = gradableObjectManager.createAssignment(gradebook.getId(), "Excessive Test", new Double(10), new Date());

        Set enrollments = new HashSet();
        enrollments.add(new EnrollmentStandalone(new UserStandalone("normalStudent", null, null, null), gradebook));
        enrollments.add(new EnrollmentStandalone(new UserStandalone("goodStudent", null, null, null), gradebook));
        enrollments.add(new EnrollmentStandalone(new UserStandalone("excessiveStudent", null, null, null), gradebook));

        // Create a map of studentUserUids to grades
        Map map = new HashMap();
        map.put("normalStudent", new Double(9));
        map.put("goodStudent", new Double(10));
        map.put("excessiveStudent", new Double(11));

        Set excessives = gradeManager.updateAssignmentGradeRecords(asgId, map);
        Assert.assertTrue(excessives.size() == 1);
        Assert.assertTrue(excessives.contains("excessiveStudent"));

        map = new HashMap();
        map.put("goodStudent", new Double(11));
        map.put("excessiveStudent", new Double(11));

        // Only updates should be reported.
        excessives = gradeManager.updateAssignmentGradeRecords(asgId, map);
        Assert.assertTrue(excessives.size() == 1);
        Assert.assertTrue(excessives.contains("goodStudent"));
    }

    public void testAssignmentScoresEntered() throws Exception {
        Gradebook gradebook = gradebookManager.getGradebook(this.getClass().getName());
        Long asgId = gradableObjectManager.createAssignment(gradebook.getId(), "Scores Entered Test", new Double(10), new Date());

        Set enrollments = new HashSet();
        enrollments.add(new EnrollmentStandalone(new UserStandalone("entered1", null, null, null), gradebook));

        Assert.assertTrue(!gradeManager.isEnteredAssignmentScores(asgId));

        // Create a map of studentUserUids to grades
        Map map = new HashMap();
        map.put("entered1", new Double(9));

        gradeManager.updateAssignmentGradeRecords(asgId, map);
        Assert.assertTrue(gradeManager.isEnteredAssignmentScores(asgId));

        map = new HashMap();
        map.put("entered1", null);

        gradeManager.updateAssignmentGradeRecords(asgId, map);
        Assert.assertTrue(!gradeManager.isEnteredAssignmentScores(asgId));
    }

}


/**********************************************************************************
 * $Header: $
 *********************************************************************************/