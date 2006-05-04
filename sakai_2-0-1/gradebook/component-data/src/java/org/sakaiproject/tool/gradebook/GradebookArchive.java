/**********************************************************************************
*
* $Header: /cvs/sakai2/gradebook/component-data/src/java/org/sakaiproject/tool/gradebook/GradebookArchive.java,v 1.3 2005/06/11 17:40:00 ray.media.berkeley.edu Exp $
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
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Models a gradebook and all of its dependent objects, which can all be
 * serialized as xml for archiving.
 *
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 */
public class GradebookArchive {
    private static Log log = LogFactory.getLog(GradebookArchive.class);

    private Gradebook gradebook;
    private GradeMapping selectedGradeMapping;
    private Collection gradeMappings;
    private CourseGrade courseGrade;
    private Collection assignments;

    public GradebookArchive() {
        // Allows for creating the archive, then populating it via readArchive()
    }

	/**
	 * @param gradebook
	 * @param selectedGradeMapping
	 * @param gradeMappings
	 * @param courseGrade
	 * @param assignments
	 */
	public GradebookArchive(Gradebook gradebook,
			GradeMapping selectedGradeMapping, Collection gradeMappings,
			CourseGrade courseGrade, Collection assignments) {
		super();
		this.gradebook = gradebook;
		this.selectedGradeMapping = selectedGradeMapping;
		this.gradeMappings = gradeMappings;
		this.courseGrade = courseGrade;
		this.assignments = assignments;
	}

    /**
     * Serializes this gradebook archive into an xml document
     */
    public String archive() {
        if(log.isDebugEnabled()) log.debug("GradebookArchive.archive() called");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(baos));
        encoder.writeObject(this);
        encoder.flush();
        String xml = baos.toString();
        if(log.isDebugEnabled()) log.debug("GradebookArchive.archive() finished");
        return xml;
    }

    /**
     * Read a gradebook archive from an xml input stream.
     *
     * @param xml The input stream containing the serialized gradebook archive
     * @return A gradebook archive object modeling the data in the xml stream
     */
    public void readArchive(String xml) {
        ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes());
        XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(in));
        GradebookArchive archive = (GradebookArchive)decoder.readObject();
        decoder.close();
        this.gradebook = archive.getGradebook();
        this.courseGrade = archive.getCourseGrade();
        this.assignments = archive.getAssignments();
    }

    public Collection getAssignments() {
		return assignments;
	}
	public void setAssignments(Collection assignments) {
		this.assignments = assignments;
	}
	public CourseGrade getCourseGrade() {
		return courseGrade;
	}
	public void setCourseGrade(CourseGrade courseGrade) {
		this.courseGrade = courseGrade;
	}
	public Gradebook getGradebook() {
		return gradebook;
	}
	public void setGradebook(Gradebook gradebook) {
		this.gradebook = gradebook;
	}
	public Collection getGradeMappings() {
		return gradeMappings;
	}
	public void setGradeMappings(Collection gradeMappings) {
		this.gradeMappings = gradeMappings;
	}
	public GradeMapping getSelectedGradeMapping() {
		return selectedGradeMapping;
	}
	public void setSelectedGradeMapping(GradeMapping selectedGradeMapping) {
		this.selectedGradeMapping = selectedGradeMapping;
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/gradebook/component-data/src/java/org/sakaiproject/tool/gradebook/GradebookArchive.java,v 1.3 2005/06/11 17:40:00 ray.media.berkeley.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
