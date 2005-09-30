/**********************************************************************************
*
* $Header: /cvs/sakai2/gradebook/component-data/src/java/org/sakaiproject/tool/gradebook/GradableObject.java,v 1.4 2005/05/26 18:04:54 josh.media.berkeley.edu Exp $
*
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of California, The MIT Corporation
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

package org.sakaiproject.tool.gradebook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A GradableObject is a component of a Gradebook for which students can be
 * assigned a GradeRecord.
 *
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 */
public abstract class GradableObject {
    protected static final Log log = LogFactory.getLog(GradableObject.class);

    protected Long id;
    protected int version;
    protected Gradebook gradebook;
    protected String name;
    protected Double mean;	// not persisted; not used in all contexts (in Overview & Assignment Grading,
    	                    // not in Roster or Student View)

    protected boolean removed;  // We had trouble with foreign key constraints in the UCB pilot when
                                // instructors "emptied" all scores for an assignment and then tried to
                                // delete the assignment.  Instead, we should hide the "removed" assignments
                                // from the app by filtering the removed assignments in the hibernate queries

    /**
     * @return Whether this gradable object is a course grade
     */
    public abstract boolean isCourseGrade();

    /**
     * The number of points to display in the UI.
     */
    public abstract Double getPointsForDisplay();

    /**
     * The date to display in the UI.
     */
    public abstract Date getDateForDisplay();
	/**
	 * @return Returns the id.
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(Long id) {
		this.id = id;
	}
    /**
     * @return Returns the gradebook.
     */
    public Gradebook getGradebook() {
        return gradebook;
    }
    /**
     * @param gradebook The gradebook to set.
     */
    public void setGradebook(Gradebook gradebook) {
        this.gradebook = gradebook;
    }

    /**
     * @return Returns the mean.
     */
    public Double getMean() {
        return mean;
    }

    /**
	 * @return Returns the mean while protecting against displaying NaN.
	 */
	public Double getFormattedMean() {
        if(mean == null || mean.equals(new Double(Double.NaN))) {
        	return null;
        } else {
            return mean;
        }
	}

    /**
	 * @param mean The mean to set.
	 */
	public void setMean(Double mean) {
		this.mean = mean;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return Returns the version.
	 */
	public int getVersion() {
		return version;
	}
	/**
	 * @param version The version to set.
	 */
	public void setVersion(int version) {
		this.version = version;
	}
    /**
     * @return Returns the removed.
     */
    public boolean isRemoved() {
        return removed;
    }
    /**
     * @param removed The removed to set.
     */
    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    /**
     * Calculates the mean score for all students on this GradableObject.
     *
     * @param gradeRecords The grade records for this gradable object
     * @param numEnrollments The total number of enrollments in this gradebook
     * to consider when calculating statistics
     */
    public void calculateStatistics(Collection gradeRecords, int numEnrollments) {
        List grades = new ArrayList(); // Keep track of all of the grades (as Doubles)
        for(Iterator gradeIter = gradeRecords.iterator(); gradeIter.hasNext();) {
            AbstractGradeRecord record = (AbstractGradeRecord)gradeIter.next();
            // Skip grade records that don't apply to this gradable object
            if(!record.getGradableObject().equals(this)) {
                continue;
            }
            grades.add(record.getGradeAsPercentage());
        }
        mean = calculateMean(grades, numEnrollments);
    }

    /**
     * Calculates the mean value of the Double values passed.  The collection of
     * grades contains Doubles and potentially nulls.
     *
     * @param grades A collection of grades as percentage values (Doubles)
     *
     * @return The mean of all entered grades
     */
    protected abstract Double calculateMean(Collection grades, int numEnrollments);

    public String toString() {
        return new ToStringBuilder(this).
        append("id", id).
        append("name", name).toString();

    }

    public boolean equals(Object other) {
        if (!(other instanceof GradableObject)) {
        	return false;
        }
        GradableObject go = (GradableObject)other;
        return new EqualsBuilder()
            .append(gradebook, go.getGradebook())
            .append(id, go.getId())
            .append(name, go.getName()).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder().
          append(gradebook).
          append(id).
          append(name).
          toHashCode();
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/gradebook/component-data/src/java/org/sakaiproject/tool/gradebook/GradableObject.java,v 1.4 2005/05/26 18:04:54 josh.media.berkeley.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
