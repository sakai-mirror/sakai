/**********************************************************************************
*
* $Header: /cvs/sakai2/gradebook/tool/src/java/org/sakaiproject/tool/gradebook/ui/AssignmentDetailsBean.java,v 1.4 2005/05/26 18:04:54 josh.media.berkeley.edu Exp $
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

package org.sakaiproject.tool.gradebook.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.ActionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.service.gradebook.shared.StaleObjectModificationException;
import org.sakaiproject.tool.gradebook.AbstractGradeRecord;
import org.sakaiproject.tool.gradebook.Assignment;
import org.sakaiproject.tool.gradebook.AssignmentGradeRecord;
import org.sakaiproject.tool.gradebook.facades.Enrollment;
import org.sakaiproject.tool.gradebook.jsf.FacesUtil;

public class AssignmentDetailsBean extends EnrollmentTableBean {
	private static final Log logger = LogFactory.getLog(AssignmentDetailsBean.class);

	// View maintenance fields - serializable.
	private Long assignmentId;
    private String name;
    private Double points;
    private Double mean;
    private Date dueDate;
	private Long previousId;
	private Long nextId;
	private List scoreRows;
	private Map scores;
    private boolean external;
    private String externalLink;

    // Controller fields - transient.
    private transient Assignment assignment;
	private transient Assignment previousAssignment;
	private transient Assignment nextAssignment;
    private transient String externalAppName;

	public class ScoreRow implements Serializable {
		private String studentUid;
		private String sortName;	// Transient except for validation errors
		private String displayUid;	// Transient except for validation errors
		public ScoreRow() {
		}
		public ScoreRow(Enrollment enrollment) {
			studentUid = enrollment.getUser().getUserUid();
			sortName = enrollment.getUser().getSortName();
			displayUid = enrollment.getUser().getDisplayUid();
		}
		public String getStudentUid() {
			return studentUid;
		}
		public void setStudentUid(String studentUid) {
			this.studentUid = studentUid;
		}
		public Double getScore() {
			return (Double)scores.get(studentUid);
		}
		public void setScore(Double score) {
			scores.put(studentUid, score);
		}
		public String getSortName() {
			return sortName;
		}
		public void setSortName(String sortName) {
			this.sortName = sortName;
		}
		public String getDisplayUid() {
			return displayUid;
		}
		public void setDisplayUid(String displayUid) {
			this.displayUid = displayUid;
		}
	}

	protected void init() {
		if (logger.isDebugEnabled()) logger.debug("loadData assignment=" + assignment + ", previousAssignment=" + previousAssignment + ", nextAssignment=" + nextAssignment);
		if (logger.isDebugEnabled()) logger.debug("    assignmentId=" + assignmentId + ", previousId=" + previousId + ", nextId=" + nextId);

		// Clear view state.
        external = false;
		previousId = null;
		nextId = null;
		scoreRows = new ArrayList();
		scores = new HashMap();

		if (assignmentId != null) {
			assignment = assignment = (Assignment)getGradableObjectManager().getGradableObjectWithStats(assignmentId);
			if (assignment != null) {
                // Populate the serializable assignment properties in this bean
                name = assignment.getName();
                points = assignment.getPointsForDisplay();
                dueDate = assignment.getDateForDisplay();
                Double formattedMean = assignment.getFormattedMean();
                if(formattedMean != null) {
                	mean = new Double(formattedMean.doubleValue() / 100);
                } else {
                    mean = null;
                }

                // Get the list of assignments.  If we are sorting by mean, we
                // need to fetch the assignment statistics as well.
				List assignments;
                if(Assignment.SORT_BY_MEAN.equals(getAssignmentSortColumn())) {
                    assignments = getGradableObjectManager().getAssignmentsWithStats(getGradebookId(),
                            getAssignmentSortColumn(), isAssignmentSortAscending());
                } else {
                    assignments = getGradableObjectManager().getAssignments(getGradebookId(),
                            getAssignmentSortColumn(), isAssignmentSortAscending());
                }

                // Set up next and previous links, if any.
                int thisIndex = assignments.indexOf(assignment);
				if (thisIndex > 0) {
					previousAssignment = (Assignment)assignments.get(thisIndex - 1);
					previousId = previousAssignment.getId();
				}
				if (thisIndex < (assignments.size() - 1)) {
					nextAssignment = (Assignment)assignments.get(thisIndex + 1);
					nextId = nextAssignment.getId();
				}

                external = assignment.isExternallyMaintained();
                externalLink = assignment.getExternalInstructorLink();
                externalAppName = assignment.getExternalAppName();

				// Set up score rows.
				Map enrollmentMap = getOrderedEnrollmentMap();

				List gradeRecords;

				if (isFilteredSearch() || isEnrollmentSort()) {
					gradeRecords = getGradeManager().getPointsEarnedSortedGradeRecords(assignment, enrollmentMap.keySet());
				} else {
					gradeRecords = getGradeManager().getPointsEarnedSortedGradeRecords(assignment);
				}

				List workingEnrollments = new ArrayList(enrollmentMap.values());

				if (!isEnrollmentSort()) {
					// Need to sort and page based on a scores column.
					List scoreSortedEnrollments = new ArrayList();
					for(Iterator iter = gradeRecords.iterator(); iter.hasNext();) {
						AbstractGradeRecord agr = (AbstractGradeRecord)iter.next();
						scoreSortedEnrollments.add(enrollmentMap.get(agr.getStudentId()));
					}

					// Put enrollments with no scores at the beginning of the final list.
					workingEnrollments.removeAll(scoreSortedEnrollments);

					// Add all sorted enrollments with scores into the final list
					workingEnrollments.addAll(scoreSortedEnrollments);

					workingEnrollments = finalizeSortingAndPaging(workingEnrollments);
				}

                for (Iterator iter = gradeRecords.iterator(); iter.hasNext(); ) {
					AssignmentGradeRecord gradeRecord = (AssignmentGradeRecord)iter.next();
					scores.put(gradeRecord.getStudentId(), gradeRecord.getPointsEarned());
				}
				for (Iterator iter = workingEnrollments.iterator(); iter.hasNext(); ) {
					Enrollment enrollment = (Enrollment)iter.next();
					scoreRows.add(new ScoreRow(enrollment));
				}

			} else {
				// The assignment might have been removed since this link was set up.
				if (logger.isWarnEnabled()) logger.warn("No assignmentId=" + assignmentId + " in gradebookUid " + getGradebookUid());
                FacesUtil.addErrorMessage(getLocalizedString("assignment_details_assignment_removed"));
			}
		}
	}

	// Delegated sort methods for read-only assignment sort order
    public String getAssignmentSortColumn() {
        return getPreferencesBean().getAssignmentSortColumn();
    }
    public boolean isAssignmentSortAscending() {
        return getPreferencesBean().isAssignmentSortAscending();
    }

	/**
	 * Action listener to view a different assignment.
	 * NOTE: No transient fields are available yet.
	 */
	public void processAssignmentIdChange(ActionEvent event) {
		Map params = FacesUtil.getEventParameterMap(event);
		if (logger.isDebugEnabled()) logger.debug("processAssignmentIdAction params=" + params + ", current assignmentId=" + assignmentId);
		Long idParam = (Long)params.get("assignmentId");
		if (idParam != null) {
			setAssignmentId(idParam);
		}
	}

	/**
	 * Action listener to update scores.
	 * NOTE: No transient fields are available yet.
	 */
	public void processUpdateScores(ActionEvent event) {
		try {
			saveScores();
		} catch (StaleObjectModificationException e) {
            logger.error(e);
            FacesUtil.addErrorMessage(getLocalizedString("assignment_details_locking_failure"));
		}
	}

	private void saveScores() throws StaleObjectModificationException {
		if (logger.isInfoEnabled()) logger.info("saveScores " + assignmentId);
		Set excessiveScores = getGradeManager().updateAssignmentGradeRecords(assignmentId, scores);

		String messageKey = (excessiveScores.size() > 0) ?
			"assignment_details_scores_saved_excessive" :
			"assignment_details_scores_saved";

        // Let the user know.
        FacesUtil.addMessage(getLocalizedString(messageKey));
	}

    /**
	 * View maintenance methods.
	 */
	public Long getAssignmentId() {
		if (logger.isDebugEnabled()) logger.debug("getAssignmentId " + assignmentId);
		return assignmentId;
	}
	public void setAssignmentId(Long assignmentId) {
		if (logger.isDebugEnabled()) logger.debug("setAssignmentId " + assignmentId);
		this.assignmentId = assignmentId;
	}
	public Long getNextId() {
		return nextId;
	}
	public void setNextId(Long nextId) {
		this.nextId = nextId;
	}
	public Long getPreviousId() {
		return previousId;
	}
	public void setPreviousId(Long previousId) {
		this.previousId = previousId;
	}

	public String getName() {
        return name;
	}

	public Double getPoints() {
        return points;
	}

	public Date getDueDate() {
        return dueDate;
	}

	public Double getMean() {
        return mean;
	}

	public boolean isFirst() {
		return (previousId == null);
	}
	public String getPreviousTitle() {
		return (previousAssignment != null) ? previousAssignment.getName() : "";
	}

	public boolean isLast() {
		return (nextId == null);
	}
	public String getNextTitle() {
		return (nextAssignment != null) ? nextAssignment.getName() : "";
	}

	public List getScoreRows() {
		return scoreRows;
	}
	public void setScoreRows(List scoreRows) {
		this.scoreRows = scoreRows;
	}
	public boolean isExternal() {
		return external;
	}
	public void setExternal(boolean external) {
		this.external = external;
	}
	public String getExternalLink() {
		return externalLink;
	}
	public void setExternalLink(String externalLink) {
		this.externalLink = externalLink;
	}
	public String getExternalAppName() {
		return externalAppName;
	}
	public void setExternalAppName(String externalAppName) {
		this.externalAppName = externalAppName;
	}
    public boolean isEmptyEnrollments() {
        return emptyEnrollments;
    }

    // A desparate stab at reasonable embedded validation message formatting.
    // If the score column is an input box, it may have a wide message associated
    // with it, and we want the input field left-aligned to match up with
    // the non-erroroneous input fields (even though the actual input values
    // will be right-aligned). On the other hand, if the score column is read-only,
    // then we want to simply right-align the table column.
    public String getScoreColumnAlignment() {
    	if (isExternal()) {
    		return "right";
    	} else {
    		return "left";
    	}
    }

    // Sorting
    public boolean isSortAscending() {
        return getPreferencesBean().isAssignmentDetailsTableSortAscending();
    }
    public void setSortAscending(boolean sortAscending) {
        getPreferencesBean().setAssignmentDetailsTableSortAscending(sortAscending);
    }
    public String getSortColumn() {
        return getPreferencesBean().getAssignmentDetailsTableSortColumn();
    }
    public void setSortColumn(String sortColumn) {
        getPreferencesBean().setAssignmentDetailsTableSortColumn(sortColumn);
    }
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/gradebook/tool/src/java/org/sakaiproject/tool/gradebook/ui/AssignmentDetailsBean.java,v 1.4 2005/05/26 18:04:54 josh.media.berkeley.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/