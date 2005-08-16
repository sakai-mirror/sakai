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

package org.sakaiproject.tool.section.jsf.backingbean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sakaiproject.api.section.coursemanagement.CourseSection;
import org.sakaiproject.api.section.coursemanagement.User;
import org.sakaiproject.tool.section.decorator.StudentSectionDecorator;

public class StudentViewBean extends CourseDependentBean {
	private String sortColumn;
	private boolean sortAscending;
	
	private List sections;
	
	public void init() {
		Set sectionSet = getSections(getSiteContext());
		sections = new ArrayList();
		for(Iterator sectionIter = sectionSet.iterator(); sectionIter.hasNext();) {
			CourseSection section = (CourseSection)sectionIter.next();
			String catName = getCategoryName(section.getCategory(), getLocale());
			List tas = getTeachingAssistants(section.getUuid());
			StringBuffer taNames = new StringBuffer();
			for(Iterator taIter = tas.iterator(); taIter.hasNext();) {
				User ta = (User)taIter.next();
				taNames.append(ta.getDisplayName());
				if(taIter.hasNext()) {
					taNames.append(", ");
				}
			}
			int totalEnrollments = getSectionManager().getTotalEnrollments(section.getUuid());
			StudentSectionDecorator decoratedSection = new StudentSectionDecorator(
					section, catName, taNames.toString(), totalEnrollments);
			sections.add(decoratedSection);
		}
		
		// TODO Sort the collection set properly
		Collections.sort(sections);
	}

	public List getSections() {
		return sections;
	}
	
	public boolean isSortAscending() {
		return sortAscending;
	}
	public void setSortAscending(boolean sortAscending) {
		this.sortAscending = sortAscending;
	}
	public String getSortColumn() {
		return sortColumn;
	}
	public void setSortColumn(String sortColumn) {
		this.sortColumn = sortColumn;
	}
}



/**********************************************************************************
 * $Id$
 *********************************************************************************/
